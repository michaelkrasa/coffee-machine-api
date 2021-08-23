package com.coffeemachine.api.kitchen;

import com.coffeemachine.api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KitchenService {

    private final KitchenRepository kitchenRepository;

    public KitchenService(final KitchenRepository kitchenRepository) {
        this.kitchenRepository = kitchenRepository;
    }

    public KitchenDTO getDetail(UUID kitchenId) {
        Kitchen kitchen = getExistingKitchen(kitchenId);
        return new KitchenDTO(kitchen);
    }

    private Kitchen getExistingKitchen(UUID kitchenId) {
        return kitchenRepository.findById(kitchenId)
            .orElseThrow(() -> new ResourceNotFoundException("Resource kitchen with id " + kitchenId + " doesn't exist"));
    }

    public AllKitchensDTO getAllDetail() {
        return new AllKitchensDTO(kitchenRepository.findAll());
    }
}
