package com.coffeemachine.api.kitchen;

import com.coffeemachine.api.coffeemachine.CoffeeMachine;
import static com.coffeemachine.api.coffeemachine.CoffeeType.AMERICANO;
import static com.coffeemachine.api.coffeemachine.CoffeeType.CAPPUCCINO;
import static com.coffeemachine.api.coffeemachine.CoffeeType.ESPRESSO;
import static com.coffeemachine.api.coffeemachine.CoffeeType.LATTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doReturn;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class KitchenServiceTest {

    @InjectMocks
    private KitchenService kitchenService;

    @Mock
    private KitchenRepository kitchenRepository;

    @Test
    void getDetail() {
        CoffeeMachine cm1 = new CoffeeMachine(List.of(ESPRESSO));
        CoffeeMachine cm2 = new CoffeeMachine(List.of(LATTE));
        Kitchen k = new Kitchen(2, "North");
        k.addCoffeeMachine(cm1);
        k.addCoffeeMachine(cm2);
        cm1.setKitchen(k);
        cm2.setKitchen(k);
        doReturn(Optional.of(k)).when(kitchenRepository).findById(any());

        KitchenDTO dto = kitchenService.getDetail(k.getId());
        assertEquals(dto.getId(), k.getId().toString());
        assertEquals(dto.getCoffeeMachines().get(0).getId(), k.getCoffeeMachines().get(0).getId().toString());
        assertEquals(dto.getCoffeeMachines().get(0).getKitchenId(), k.getCoffeeMachines().get(0).getKitchen().getId().toString());
        assertEquals(dto.getDescription(), k.getDescription());
        assertEquals(dto.getFloorNo(), k.getFloorNo());
    }

    @Test
    void getAllDetail() {
        Kitchen k1 = new Kitchen(1, "North");
        Kitchen k2 = new Kitchen(2, "North");
        CoffeeMachine cm1 = new CoffeeMachine(List.of(ESPRESSO)).kitchen(k1);
        CoffeeMachine cm2 = new CoffeeMachine(List.of(LATTE)).kitchen(k1);
        CoffeeMachine cm3 = new CoffeeMachine(List.of(AMERICANO)).kitchen(k2);
        CoffeeMachine cm4 = new CoffeeMachine(List.of(CAPPUCCINO)).kitchen(k2);
        k1.addCoffeeMachine(cm1).addCoffeeMachine(cm2);
        k2.addCoffeeMachine(cm3).addCoffeeMachine(cm4);
        doReturn(List.of(k1, k2)).when(kitchenRepository).findAll();
        List<KitchenDTO> kitchens = kitchenService.getAllDetail().getKitchens();

        assertEquals(kitchens.size(), 2);
        assertEquals(kitchens.get(0).getFloorNo(), k1.getFloorNo());
        assertEquals(kitchens.get(0).getId(), k1.getId().toString());
        assertEquals(kitchens.get(0).getDescription(), k1.getDescription());
        assertEquals(kitchens.get(0).getCoffeeMachines().get(0).getId(), k1.getCoffeeMachines().get(0).getId().toString());
        assertEquals(kitchens.get(0).getCoffeeMachines().get(1).getId(), k1.getCoffeeMachines().get(1).getId().toString());
        assertEquals(kitchens.get(1).getFloorNo(), k2.getFloorNo());
        assertEquals(kitchens.get(1).getId(), k2.getId().toString());
        assertEquals(kitchens.get(1).getDescription(), k2.getDescription());
        assertEquals(kitchens.get(1).getCoffeeMachines().get(0).getId(), k2.getCoffeeMachines().get(0).getId().toString());
        assertEquals(kitchens.get(1).getCoffeeMachines().get(1).getId(), k2.getCoffeeMachines().get(1).getId().toString());
    }
}
