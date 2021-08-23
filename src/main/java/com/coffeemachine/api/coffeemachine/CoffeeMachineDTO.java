package com.coffeemachine.api.coffeemachine;

import java.util.List;

public class CoffeeMachineDTO {

    private final String id;

    private final List<CoffeeType> supportedCoffeeTypes;

    private final CoffeeMachineState state;

    private final String kitchenId;

    public CoffeeMachineDTO(CoffeeMachine coffeeMachine) {
        this.id = coffeeMachine.getId().toString();
        this.supportedCoffeeTypes = coffeeMachine.getSupportedCoffeeTypes();
        this.state = coffeeMachine.getState();
        this.kitchenId = coffeeMachine.getKitchen() != null ? coffeeMachine.getKitchen().getId().toString() : "null";
    }

    public String getId() {
        return id;
    }

    public List<CoffeeType> getSupportedCoffeeTypes() {
        return supportedCoffeeTypes;
    }

    public CoffeeMachineState getState() {
        return state;
    }

    public String getKitchenId() {
        return kitchenId;
    }
}
