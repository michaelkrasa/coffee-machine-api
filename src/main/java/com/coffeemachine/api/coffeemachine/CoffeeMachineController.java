package com.coffeemachine.api.coffeemachine;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
public class CoffeeMachineController {

    private final CoffeeMachineService coffeeMachineService;

    public CoffeeMachineController(final CoffeeMachineService coffeeMachineService) {
        this.coffeeMachineService = coffeeMachineService;
    }

    @GetMapping("/coffee-machine/{id}/state")
    public Map<String, CoffeeMachineState> getState(@PathVariable(name = "id") String coffeeMachineId) {
        CoffeeMachineState state = coffeeMachineService.getState(UUID.fromString(coffeeMachineId));
        return Map.of("state", state);
    }

    @GetMapping("/coffee-machine/{id}/detail")
    public CoffeeMachineDTO getDetail(@PathVariable(name = "id") String coffeeMachineId) {
        return coffeeMachineService.getDetail(UUID.fromString(coffeeMachineId));
    }

    @PutMapping("/coffee-machine/{id}/make-coffee")
    public void makeCoffee(
        @PathVariable(name = "id") String coffeeMachineId,
        @RequestParam(name = "coffeeType") String coffeeType) throws InterruptedException, IllegalArgumentException {
        coffeeMachineService.makeCoffee(UUID.fromString(coffeeMachineId), CoffeeType.valueOf(coffeeType));
    }

    // Pretend we live in an IOT utopia where the coffee machine can sense the cup being taken away and send a request
    @PutMapping("/coffee-machine/{id}/remove-cup")
    public void removeCupFromTray(@PathVariable(name = "id") String coffeeMachineId) {
        coffeeMachineService.removeCupFromTray(UUID.fromString(coffeeMachineId));
    }

    // Handle built in exception for testing
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public void handleIllegalArgumentException() {
    }
}
