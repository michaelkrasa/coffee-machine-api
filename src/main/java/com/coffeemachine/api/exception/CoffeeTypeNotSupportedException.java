package com.coffeemachine.api.exception;

import com.coffeemachine.api.coffeemachine.CoffeeMachine;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CoffeeTypeNotSupportedException extends RuntimeException {
    public CoffeeTypeNotSupportedException(final CoffeeMachine coffeeMachine, final String coffeeType) {
        super(String.format("Coffee type %s is not supported by machine with id: %s", coffeeType, coffeeMachine.getId()));
    }
}
