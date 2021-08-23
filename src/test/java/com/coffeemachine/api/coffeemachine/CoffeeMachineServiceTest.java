package com.coffeemachine.api.coffeemachine;

import static com.coffeemachine.api.coffeemachine.CoffeeMachineState.DISPENSED;
import static com.coffeemachine.api.coffeemachine.CoffeeMachineState.READY;
import static com.coffeemachine.api.coffeemachine.CoffeeType.CAPPUCCINO;
import static com.coffeemachine.api.coffeemachine.CoffeeType.ESPRESSO;
import com.coffeemachine.api.exception.CoffeeTypeNotSupportedException;
import com.coffeemachine.api.exception.IllegalCoffeeMachineStateException;
import com.coffeemachine.api.exception.ResourceNotFoundException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class CoffeeMachineServiceTest {

    @InjectMocks
    private CoffeeMachineService coffeeMachineService;

    @Mock
    private CoffeeMachineRepository coffeeMachineRepository;

    @Test
    void getState() {
        CoffeeMachine cm = new CoffeeMachine(List.of(ESPRESSO));
        doReturn(Optional.of(cm)).when(coffeeMachineRepository).findById(any());

        assertEquals(coffeeMachineService.getState(cm.getId()), cm.getState());
    }

    @Test
    void getDetail() {
        CoffeeMachine cm = new CoffeeMachine(List.of(ESPRESSO));
        doReturn(Optional.of(cm)).when(coffeeMachineRepository).findById(any());

        CoffeeMachineDTO dto = coffeeMachineService.getDetail(cm.getId());
        assertEquals(dto.getState(), cm.getState());
        assertEquals(dto.getSupportedCoffeeTypes(), cm.getSupportedCoffeeTypes());
        assertEquals(dto.getId(), cm.getId().toString());
    }

    @Test
    void makeCoffee() throws InterruptedException {
        CoffeeMachine cm = new CoffeeMachine(List.of(ESPRESSO));
        doReturn(Optional.of(cm)).when(coffeeMachineRepository).findById(any());

        coffeeMachineService.makeCoffee(cm.getId(), ESPRESSO);
        assertEquals(cm.getState(), DISPENSED);
    }

    @Test
    void makeCoffeeNonExistentType() {
        CoffeeMachine cm = new CoffeeMachine(List.of(ESPRESSO));
        assertThatThrownBy(() -> coffeeMachineService.makeCoffee(cm.getId(), CoffeeType.valueOf("bing bong")))
            .isExactlyInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void makeCoffeeUnsupportedType() {
        CoffeeMachine cm = new CoffeeMachine(List.of(ESPRESSO));
        doReturn(Optional.of(cm)).when(coffeeMachineRepository).findById(any());

        assertThatThrownBy(() -> coffeeMachineService.makeCoffee(cm.getId(), CAPPUCCINO))
            .isExactlyInstanceOf(CoffeeTypeNotSupportedException.class);
    }

    @Test
    void makeCoffeeInvalidState() {
        CoffeeMachine cm = new CoffeeMachine(List.of(ESPRESSO)).state(DISPENSED);
        doReturn(Optional.of(cm)).when(coffeeMachineRepository).findById(any());

        assertThatThrownBy(() -> coffeeMachineService.makeCoffee(cm.getId(), ESPRESSO))
            .isExactlyInstanceOf(IllegalCoffeeMachineStateException.class);
    }

    @Test
    void removeCup() {
        CoffeeMachine cm = new CoffeeMachine(List.of(ESPRESSO)).state(DISPENSED);
        doReturn(Optional.of(cm)).when(coffeeMachineRepository).findById(any());

        coffeeMachineService.removeCupFromTray(cm.getId());
        assertEquals(cm.getState(), READY);
    }

    @Test
    void removeCupInvalidState() {
        CoffeeMachine cm = new CoffeeMachine(List.of(ESPRESSO)).state(READY);
        doReturn(Optional.of(cm)).when(coffeeMachineRepository).findById(any());

        assertThatThrownBy(() -> coffeeMachineService.removeCupFromTray(cm.getId()))
            .isExactlyInstanceOf(IllegalCoffeeMachineStateException.class);
    }

    @Test
    void coffeeMachineDoesntExist() {
        CoffeeMachine cm = new CoffeeMachine(List.of(ESPRESSO)).state(DISPENSED);
        assertThatThrownBy(() -> coffeeMachineService.removeCupFromTray(cm.getId()))
            .isExactlyInstanceOf(ResourceNotFoundException.class);
    }
}
