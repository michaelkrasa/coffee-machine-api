package com.coffeemachine.api.coffeemachine;

import com.coffeemachine.api.exception.IllegalCoffeeMachineStateException;
import com.coffeemachine.api.kitchen.Kitchen;
import com.sun.istack.NotNull;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.List;
import java.util.UUID;

@Entity
public class CoffeeMachine {

    @Id
    @Column
    @NotNull
    private UUID id;

    @Column
    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private List<CoffeeType> supportedCoffeeTypes;

    @Column
    @Enumerated(EnumType.STRING)
    private CoffeeMachineState state;

    @ManyToOne
    private Kitchen kitchen;

    public CoffeeMachine(List<CoffeeType> supportedTypes) {
        this.id = UUID.randomUUID();
        this.state = CoffeeMachineState.READY;
        this.supportedCoffeeTypes = supportedTypes;
    }

    public CoffeeMachine() {
    }

    public boolean isInDesiredState(CoffeeMachineState state) {
        if (this.state != state) {
            throw new IllegalCoffeeMachineStateException(this, state);
        } else {
            return true;
        }
    }

    public void setKitchen(final Kitchen kitchen) {
        this.kitchen = kitchen;
    }

    public CoffeeMachine kitchen(final Kitchen kitchen) {
        this.kitchen = kitchen;
        return this;
    }

    public UUID getId() {
        return id;
    }

    public List<CoffeeType> getSupportedCoffeeTypes() {
        return supportedCoffeeTypes;
    }

    public CoffeeMachineState getState() {
        return state;
    }

    public Kitchen getKitchen() {
        return kitchen;
    }

    public CoffeeMachine state(CoffeeMachineState state) {
        this.state = state;
        return this;
    }
}
