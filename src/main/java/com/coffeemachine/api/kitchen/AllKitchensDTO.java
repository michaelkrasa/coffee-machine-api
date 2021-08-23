package com.coffeemachine.api.kitchen;

import java.util.List;
import java.util.stream.Collectors;

public class AllKitchensDTO {

    private final List<KitchenDTO> kitchens;

    public AllKitchensDTO(final List<Kitchen> kitchens) {
        this.kitchens = kitchens.stream().map(KitchenDTO::new).collect(Collectors.toList());
    }

    public List<KitchenDTO> getKitchens() {
        return kitchens;
    }
}
