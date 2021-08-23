package com.coffeemachine.api.kitchen;

import com.coffeemachine.api.coffeemachine.CoffeeMachine;
import com.sun.istack.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
public class Kitchen {

    @Id
    @Column
    @NotNull
    private UUID id;

    @OneToMany
    private final List<CoffeeMachine> coffeeMachines = new ArrayList<>();

    @Column
    @NotNull
    private Integer floorNo;

    @Column
    private String description;

    public Kitchen(Integer floorNo, String description) {
        this.id = UUID.randomUUID();
        this.floorNo = floorNo;
        this.description = description;
    }

    public Kitchen() {
    }

    public Kitchen addCoffeeMachine(final CoffeeMachine coffeeMachine) {
        coffeeMachines.add(coffeeMachine);
        return this;
    }

    public UUID getId() {
        return id;
    }

    public Integer getFloorNo() {
        return floorNo;
    }

    public List<CoffeeMachine> getCoffeeMachines() {
        return coffeeMachines;
    }

    public String getDescription() {
        return description;
    }
}
