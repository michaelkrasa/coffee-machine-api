package com.coffeemachine.api.kitchen;

import com.coffeemachine.api.coffeemachine.CoffeeMachineDTO;

import java.util.List;
import java.util.stream.Collectors;

public class KitchenDTO {

    private String id;

    private List<CoffeeMachineDTO> coffeeMachines;

    private Integer floorNo;

    private String description;

    public KitchenDTO(final Kitchen kitchen) {
        this.id = kitchen.getId().toString();
        this.coffeeMachines = kitchen.getCoffeeMachines().stream().map(CoffeeMachineDTO::new).collect(Collectors.toList());
        this.floorNo = kitchen.getFloorNo();
        this.description = kitchen.getDescription();
    }

    public String getId() {
        return id;
    }

    public List<CoffeeMachineDTO> getCoffeeMachines() {
        return coffeeMachines;
    }

    public Integer getFloorNo() {
        return floorNo;
    }

    public String getDescription() {
        return description;
    }
}
