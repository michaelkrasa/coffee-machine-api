package com.coffeemachine.api.kitchen;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class KitchenController {

    private final KitchenService kitchenService;

    public KitchenController(final KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @GetMapping("/kitchen/{id}/detail")
    public KitchenDTO getDetail(@PathVariable(name = "id") String kitchenId) {
        return kitchenService.getDetail(UUID.fromString(kitchenId));
    }

    @GetMapping("/kitchens")
    public AllKitchensDTO getAllDetail() {
        return kitchenService.getAllDetail();
    }
}
