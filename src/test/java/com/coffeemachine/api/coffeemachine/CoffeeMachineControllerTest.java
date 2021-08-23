package com.coffeemachine.api.coffeemachine;

import static com.coffeemachine.api.coffeemachine.CoffeeType.AMERICANO;
import static com.coffeemachine.api.coffeemachine.CoffeeType.ESPRESSO;
import com.coffeemachine.api.exception.CoffeeTypeNotSupportedException;
import com.coffeemachine.api.exception.IllegalCoffeeMachineStateException;
import com.coffeemachine.api.exception.ResourceNotFoundException;
import com.coffeemachine.api.kitchen.Kitchen;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import org.springframework.restdocs.payload.FieldDescriptor;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import org.springframework.restdocs.request.ParameterDescriptor;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
public class CoffeeMachineControllerTest {

    protected MockMvc mockMvc;

    @Mock
    private CoffeeMachineService coffeeMachineService;

    @BeforeEach
    void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(new CoffeeMachineController(coffeeMachineService))
            .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test
    void getState() throws Exception {
        CoffeeMachine cm = new CoffeeMachine(Arrays.asList(ESPRESSO, AMERICANO));
        doReturn(cm.getState()).when(coffeeMachineService).getState(any());

        mockMvc.perform(get("/coffee-machine/{id}/state", UUID.randomUUID()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.state").value("READY"))
            .andExpect(status().isOk())
            .andDo(document("coffee-machine/get-state",
                pathParameters(
                    parameterWithName("id").description("ID of the coffee machine")
                ),
                responseFields(
                    displayEnumsField("state", CoffeeMachineState.class, "Possible states of the coffee machine"))
            ));
    }

    @Test
    void getDetail() throws Exception {
        Kitchen kitchen = new Kitchen(0, "North");
        CoffeeMachine cm = new CoffeeMachine(Arrays.asList(ESPRESSO, AMERICANO));
        kitchen.addCoffeeMachine(cm);
        cm.setKitchen(kitchen);
        CoffeeMachineDTO dto = new CoffeeMachineDTO(cm);
        doReturn(dto).when(coffeeMachineService).getDetail(any());

        mockMvc.perform(get("/coffee-machine/{id}/detail", UUID.randomUUID()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(cm.getId().toString()))
            .andExpect(jsonPath("$.supportedCoffeeTypes[0]").value("ESPRESSO"))
            .andExpect(jsonPath("$.supportedCoffeeTypes[1]").value("AMERICANO"))
            .andExpect(jsonPath("$.state").value("READY"))
            .andExpect(jsonPath("$.kitchenId").value(kitchen.getId().toString()))
            .andExpect(status().isOk())
            .andDo(document("coffee-machine/get-detail",
                pathParameters(
                    parameterWithName("id").description("ID of the coffee machine")
                ),
                responseFields(
                    fieldWithPath("id").description("ID of the coffee machine"),
                    displayEnumsField("supportedCoffeeTypes", CoffeeType.class, "Types of coffee supported by the machine"),
                    displayEnumsField("state", CoffeeMachineState.class, "Possible states of the coffee machine"),
                    fieldWithPath("kitchenId").description("ID of the kitchen in which the coffee machine is")
                    )
            ));
    }

    @Test
    void makeCoffee() throws Exception {
        mockMvc.perform(put("/coffee-machine/{id}/make-coffee", UUID.randomUUID())
                .param("coffeeType", "ESPRESSO"))
            .andExpect(status().isOk())
            .andDo(document("coffee-machine/make-coffee",
                pathParameters(
                    parameterWithName("id").description("ID of the coffee machine")
                ),
                requestParameters(
                    displayEnumsParam("coffeeType", CoffeeType.class, "Type of coffee to be made")
                )));
    }

    @Test
    void makeCoffeeIncorrectState() throws Exception {
        CoffeeMachine cm = new CoffeeMachine(Arrays.asList(ESPRESSO, AMERICANO));
        doThrow(new IllegalCoffeeMachineStateException(cm, CoffeeMachineState.READY)).when(coffeeMachineService).makeCoffee(any(), any());

        mockMvc.perform(put("/coffee-machine/{id}/make-coffee", UUID.randomUUID())
                .param("coffeeType", "ESPRESSO"))
            .andExpect(status().isConflict())
            .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof IllegalCoffeeMachineStateException))
            .andExpect(mvcResult -> assertEquals("Coffee machine with id: " + cm.getId() + " is in state READY but should be in READY",
                Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()))
            .andDo(document("coffee-machine/make-coffee/incorrect-state"));
    }

    @Test
    void makeCoffeeCoffeeTypeDoesntExist() throws Exception {
        CoffeeMachine cm = new CoffeeMachine(Arrays.asList(ESPRESSO, AMERICANO));
        mockMvc.perform(put("/coffee-machine/{id}/make-coffee", UUID.randomUUID())
                .param("coffeeType", "gibberish"))
            .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof IllegalArgumentException))
            .andDo(document("coffee-machine/make-coffee/type-doesnt-exist"));
    }

    @Test
    void makeCoffeeCoffeeTypeNotSupported() throws Exception {
        CoffeeMachine cm = new CoffeeMachine(Arrays.asList(ESPRESSO, AMERICANO));
        doThrow(new CoffeeTypeNotSupportedException(cm, "LATTE"))
            .when(coffeeMachineService).makeCoffee(any(), any());
        mockMvc.perform(put("/coffee-machine/{id}/make-coffee", UUID.randomUUID())
                .param("coffeeType", "LATTE"))
            .andExpect(status().isBadRequest())
            .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof CoffeeTypeNotSupportedException))
            .andExpect(mvcResult -> assertEquals("Coffee type LATTE is not supported by machine with id: " + cm.getId(),
                Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()))
            .andDo(document("coffee-machine/make-coffee/type-not-supported"));
    }

    @Test
    void removeCup() throws Exception {
        mockMvc.perform(put("/coffee-machine/{id}/remove-cup", UUID.randomUUID()))
            .andExpect(status().isOk())
            .andDo(document("coffee-machine/remove-cup",
                pathParameters(
                    parameterWithName("id").description("ID of the coffee machine")
                )));
    }

    @Test
    void removeCupIncorrectState() throws Exception {
        CoffeeMachine cm = new CoffeeMachine(Arrays.asList(ESPRESSO, AMERICANO));
        doThrow(new IllegalCoffeeMachineStateException(cm, CoffeeMachineState.READY)).when(coffeeMachineService).removeCupFromTray(any());

        mockMvc.perform(put("/coffee-machine/{id}/remove-cup", UUID.randomUUID()))
            .andExpect(status().isConflict())
            .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof IllegalCoffeeMachineStateException))
            .andExpect(mvcResult -> assertEquals("Coffee machine with id: " + cm.getId() + " is in state READY but should be in READY",
                Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()))
            .andDo(document("coffee-machine/remove-cup/incorrect-state"));
    }

    // Can occur in all endpoints
    @Test
    void coffeeMachineNotFound() throws Exception {
        CoffeeMachine cm = new CoffeeMachine(Arrays.asList(ESPRESSO, AMERICANO));
        doThrow(new ResourceNotFoundException("Resource coffeeMachine with id " + cm.getId() + " doesn't exist"))
            .when(coffeeMachineService).makeCoffee(any(), any());
        mockMvc.perform(put("/coffee-machine/{id}/make-coffee", UUID.randomUUID())
                .param("coffeeType", "ESPRESSO"))
            .andExpect(status().isNotFound())
            .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof ResourceNotFoundException))
            .andExpect(mvcResult -> assertEquals("Resource coffeeMachine with id " + cm.getId() + " doesn't exist",
                Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()))
            .andDo(document("coffee-machine/coffee-machine-not-found"));
    }

    private static FieldDescriptor displayEnumsField(String fieldName, Class<?> enumType, String description) {
        String formattedEnumValues = Arrays.stream(enumType.getEnumConstants())
            .map(type -> String.format("%s", type))
            .collect(Collectors.joining(", "));
        return fieldWithPath(fieldName).description(String.format("%s [%s]", description, formattedEnumValues));
    }

    private static ParameterDescriptor displayEnumsParam(String fieldName, Class<?> enumType, String description) {
        String formattedEnumValues = Arrays.stream(enumType.getEnumConstants())
            .map(type -> String.format("%s", type))
            .collect(Collectors.joining(", "));
        return parameterWithName(fieldName).description(String.format("%s [%s]", description, formattedEnumValues));
    }
}


