package com.coffeemachine.api.kitchen;

import com.coffeemachine.api.coffeemachine.CoffeeMachine;
import com.coffeemachine.api.coffeemachine.CoffeeType;
import static com.coffeemachine.api.coffeemachine.CoffeeType.AMERICANO;
import static com.coffeemachine.api.coffeemachine.CoffeeType.CAPPUCCINO;
import static com.coffeemachine.api.coffeemachine.CoffeeType.ESPRESSO;
import static com.coffeemachine.api.coffeemachine.CoffeeType.LATTE;
import com.coffeemachine.api.exception.ResourceNotFoundException;
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
import org.springframework.restdocs.payload.FieldDescriptor;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@ExtendWith({MockitoExtension.class, RestDocumentationExtension.class})
public class KitchenControllerTest {

    protected MockMvc mockMvc;

    @Mock
    private KitchenService kitchenService;

    @BeforeEach
    void init(RestDocumentationContextProvider restDocumentation) {
        mockMvc = MockMvcBuilders.standaloneSetup(new KitchenController(kitchenService))
            .apply(documentationConfiguration(restDocumentation)).build();
    }

    @Test
    void getDetail() throws Exception {
        Kitchen kitchen = new Kitchen(0, "North");
        CoffeeMachine cm = new CoffeeMachine(Arrays.asList(ESPRESSO, AMERICANO)).kitchen(kitchen);
        kitchen.addCoffeeMachine(cm);
        KitchenDTO dto = new KitchenDTO(kitchen);
        doReturn(dto).when(kitchenService).getDetail(any());

        mockMvc.perform(get("/kitchen/{id}/detail", UUID.randomUUID()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(kitchen.getId().toString()))
            .andExpect(jsonPath("$.coffeeMachines[0].id").value(cm.getId().toString()))
            .andExpect(jsonPath("$.coffeeMachines[0].supportedCoffeeTypes[0]").value("ESPRESSO"))
            .andExpect(jsonPath("$.coffeeMachines[0].state").value("READY"))
            .andExpect(jsonPath("$.floorNo").value("0"))
            .andExpect(jsonPath("$.description").value("North"))
            .andExpect(status().isOk())
            .andDo(document("kitchen/get-detail",
                pathParameters(
                    parameterWithName("id").description("ID of the kitchen")
                ),
                responseFields(
                    fieldWithPath("id").description("ID of the kitchen"),
                    fieldWithPath("coffeeMachines").description("List of coffee machines in the kitchen"),
                    fieldWithPath("coffeeMachines[0].id").description("List of coffee machines in the kitchen"),
                    displayEnumsField("coffeeMachines[0].supportedCoffeeTypes", CoffeeType.class, "Types of coffee supported by the machine"),
                    fieldWithPath("coffeeMachines[0].state").description("Possible states of the coffee machine"),
                    fieldWithPath("coffeeMachines[0].kitchenId").description("ID of the kitchen in which the coffee machine is"),
                    fieldWithPath("floorNo").description("Floor number the kitchen is on"),
                    fieldWithPath("description").description("Description of the kitchen")
                )
            ));
    }

    @Test
    void getAllDetail() throws Exception {
        Kitchen k1 = new Kitchen(1, "North");
        Kitchen k2 = new Kitchen(2, "South");
        CoffeeMachine cm1 = new CoffeeMachine(List.of(ESPRESSO)).kitchen(k1);
        CoffeeMachine cm2 = new CoffeeMachine(List.of(LATTE)).kitchen(k1);
        CoffeeMachine cm3 = new CoffeeMachine(List.of(AMERICANO)).kitchen(k2);
        CoffeeMachine cm4 = new CoffeeMachine(List.of(CAPPUCCINO)).kitchen(k2);
        k1.addCoffeeMachine(cm1).addCoffeeMachine(cm2);
        k2.addCoffeeMachine(cm3).addCoffeeMachine(cm4);
        doReturn(new AllKitchensDTO(List.of(k1, k2))).when(kitchenService).getAllDetail();

        mockMvc.perform(get("/kitchens", UUID.randomUUID()))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.kitchens[0].id").value(k1.getId().toString()))
            .andExpect(jsonPath("$.kitchens[0].coffeeMachines[0].id").value(cm1.getId().toString()))
            .andExpect(jsonPath("$.kitchens[0].floorNo").value("1"))
            .andExpect(jsonPath("$.kitchens[0].description").value("North"))
            .andExpect(jsonPath("$.kitchens[1].id").value(k2.getId().toString()))
            .andExpect(jsonPath("$.kitchens[1].coffeeMachines[0].id").value(cm3.getId().toString()))
            .andExpect(jsonPath("$.kitchens[1].floorNo").value("2"))
            .andExpect(jsonPath("$.kitchens[1].description").value("South"))
            .andExpect(status().isOk())
            .andDo(document("kitchen/get-all-detail",
                responseFields(
                    fieldWithPath("kitchens[0].id").description("ID of the kitchen"),
                    fieldWithPath("kitchens[0].coffeeMachines[0].id").description("List of coffee machines in the kitchen"),
                    displayEnumsField("kitchens[0].coffeeMachines[0].supportedCoffeeTypes", CoffeeType.class, "Types of coffee supported by the machine"),
                    fieldWithPath("kitchens[0].coffeeMachines[0].state").description("Possible states of the coffee machine"),
                    fieldWithPath("kitchens[0].coffeeMachines[0].kitchenId").description("ID of the kitchen in which the coffee machine is"),
                    fieldWithPath("kitchens[0].floorNo").description("Floor number the kitchen is on"),
                    fieldWithPath("kitchens[0].description").description("Description of the kitchen")
                )
            ));
    }

    @Test
    void kitchenNonExistent() throws Exception {
        Kitchen k = new Kitchen(1, "North");
        doThrow(new ResourceNotFoundException("Resource kitchen with id " + k.getId() + " doesn't exist"))
            .when(kitchenService).getAllDetail();
        mockMvc.perform(get("/kitchens"))
            .andExpect(status().isNotFound())
            .andExpect(mvcResult -> assertTrue(mvcResult.getResolvedException() instanceof ResourceNotFoundException))
            .andExpect(mvcResult -> assertEquals("Resource kitchen with id " + k.getId() + " doesn't exist",
                Objects.requireNonNull(mvcResult.getResolvedException()).getMessage()))
            .andDo(document("kitchen/not-found"));
    }

    private static FieldDescriptor displayEnumsField(String fieldName, Class<?> enumType, String description) {
        String formattedEnumValues = Arrays.stream(enumType.getEnumConstants())
            .map(type -> String.format("%s", type))
            .collect(Collectors.joining(", "));
        return fieldWithPath(fieldName).description(String.format("%s [%s]", description, formattedEnumValues));
    }
}
