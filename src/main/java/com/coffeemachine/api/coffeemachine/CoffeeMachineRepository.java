package com.coffeemachine.api.coffeemachine;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoffeeMachineRepository extends JpaRepository<CoffeeMachine, UUID> {
}
