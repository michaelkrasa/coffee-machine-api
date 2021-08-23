package com.coffeemachine.api;

import com.coffeemachine.api.coffeemachine.CoffeeMachine;
import com.coffeemachine.api.coffeemachine.CoffeeMachineRepository;
import com.coffeemachine.api.coffeemachine.CoffeeType;
import static com.coffeemachine.api.coffeemachine.CoffeeType.AMERICANO;
import static com.coffeemachine.api.coffeemachine.CoffeeType.CAPPUCCINO;
import static com.coffeemachine.api.coffeemachine.CoffeeType.ESPRESSO;
import static com.coffeemachine.api.coffeemachine.CoffeeType.LATTE;
import com.coffeemachine.api.kitchen.Kitchen;
import com.coffeemachine.api.kitchen.KitchenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader {

    @Autowired
    CoffeeMachineRepository coffeeMachineRepository;

    @Autowired
    KitchenRepository kitchenRepository;

    @PostConstruct
    public void createData() {
        String[] desc = {"North", "South", "East", "West"};
        List<CoffeeType> all = Arrays.asList(ESPRESSO, AMERICANO, LATTE, CAPPUCCINO);
        List<CoffeeType> withoutMilk = Arrays.asList(ESPRESSO, AMERICANO);
        CoffeeMachine cm1, cm2;
        Kitchen k1, k2;

        // 2 kitchens for each floor with one coffee machine each
        for (int i = 0; i < 10; i++) {
            cm1 = new CoffeeMachine(i % 2 == 0 ? all : withoutMilk);
            cm2 = new CoffeeMachine(i % 2 == 1 ? all : withoutMilk);
            k1 = new Kitchen(i, desc[i % 4]).addCoffeeMachine(cm1);
            k2 = new Kitchen(i, desc[i/2 % 4]).addCoffeeMachine(cm2);
            coffeeMachineRepository.saveAll(List.of(cm1, cm2));
            kitchenRepository.saveAll(List.of(k1, k2));
            cm1.setKitchen(k1);
            cm2.setKitchen(k2);
            coffeeMachineRepository.saveAll(List.of(cm1, cm2));
        }
    }

    @PreDestroy
    public void removeData() {
        kitchenRepository.deleteAll();
        coffeeMachineRepository.deleteAll();
    }
}
