package com.coffeemachine.api.coffeemachine;

import static com.coffeemachine.api.coffeemachine.CoffeeMachineState.DISPENSED;
import static com.coffeemachine.api.coffeemachine.CoffeeMachineState.DISPENSING;
import static com.coffeemachine.api.coffeemachine.CoffeeMachineState.READY;
import com.coffeemachine.api.exception.CoffeeTypeNotSupportedException;
import com.coffeemachine.api.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CoffeeMachineService {

    private final CoffeeMachineRepository coffeeMachineRepository;

    public CoffeeMachineService(CoffeeMachineRepository coffeeMachineRepository) {
        this.coffeeMachineRepository = coffeeMachineRepository;
    }

    public CoffeeMachineState getState(final UUID coffeeMachineId) {
        CoffeeMachine coffeeMachine = getExistingCoffeeMachine(coffeeMachineId);
        return coffeeMachine.getState();
    }

    public CoffeeMachineDTO getDetail(final UUID coffeeMachineId) {
        CoffeeMachine coffeeMachine = getExistingCoffeeMachine(coffeeMachineId);
        return new CoffeeMachineDTO(coffeeMachine);
    }

    public void makeCoffee(final UUID coffeeMachineId, final CoffeeType coffeeType) throws InterruptedException {
        CoffeeMachine coffeeMachine = getExistingCoffeeMachine(coffeeMachineId);
        if (!coffeeMachine.getSupportedCoffeeTypes().contains(coffeeType)) {
            throw new CoffeeTypeNotSupportedException(coffeeMachine, coffeeType.name());
        }
        if (coffeeMachine.isInDesiredState(CoffeeMachineState.READY)) {
            coffeeMachineRepository.save(coffeeMachine.state(DISPENSING));
            Thread.sleep(10000); // wait while dispensing
            coffeeMachineRepository.save(coffeeMachine.state(DISPENSED));
        }
    }

    public void removeCupFromTray(final UUID coffeeMachineId) {
        CoffeeMachine coffeeMachine = getExistingCoffeeMachine(coffeeMachineId);
        if (coffeeMachine.isInDesiredState(DISPENSED)) {
            coffeeMachineRepository.save(coffeeMachine.state(READY));
        }
    }

    private CoffeeMachine getExistingCoffeeMachine(final UUID coffeeMachineId) {
        return coffeeMachineRepository.findById(coffeeMachineId)
            .orElseThrow(() -> new ResourceNotFoundException("Resource coffeeMachine with id " + coffeeMachineId + " doesn't exist"));
    }
}
