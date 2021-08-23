package com.coffeemachine.api.kitchen;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface KitchenRepository extends JpaRepository<Kitchen, UUID> {
}
