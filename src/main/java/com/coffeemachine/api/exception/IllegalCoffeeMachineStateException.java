package com.coffeemachine.api.exception;

import com.coffeemachine.api.coffeemachine.CoffeeMachine;
import com.coffeemachine.api.coffeemachine.CoffeeMachineState;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class IllegalCoffeeMachineStateException extends RuntimeException {
    public IllegalCoffeeMachineStateException(CoffeeMachine coffeeMachine, CoffeeMachineState required) {
        super(String.format("Coffee machine with id: %s is in state %s but should be in %s",
            coffeeMachine.getId(), required.name(), coffeeMachine.getState().name()));
    }
}
