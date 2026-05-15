package com.ricedotwho.rsm.ui.termsim;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderSimScreen extends TerminalSimScreen {
    private final Map<Integer, Integer> orderNumbers = new HashMap<>();
    private int nextExpected = 1;

    public OrderSimScreen() {
        super("Click in order!", 9 * 4);
    }

    @Override
    protected void buildContents() {
        orderNumbers.clear();
        nextExpected = 1;

        List<Integer> candidates = new ArrayList<>();
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (inPlayArea(i, 1, 2, 1, 7)) {
                candidates.add(i);
            }
        }
        Collections.shuffle(candidates);
        List<Integer> chosen = candidates.subList(0, Math.min(14, candidates.size()));
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= chosen.size(); i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);

        fillSlots(index -> {
            int orderIndex = chosen.indexOf(index);
            if (orderIndex == -1) {
                return blackPane;
            }
            int number = numbers.get(orderIndex);
            orderNumbers.put(index, number);
            return new net.minecraft.world.item.ItemStack(Items.RED_STAINED_GLASS_PANE, number);
        });
    }

    @Override
    protected void handleSlotClick(int slotId, int button) {
        if (slotId < 0 || slotId >= container.getContainerSize()) {
            return;
        }

        Slot slot = menu.slots.get(slotId);
        Integer number = orderNumbers.get(slotId);
        if (!slot.getItem().is(Items.RED_STAINED_GLASS_PANE) || number == null || number != nextExpected) {
            return;
        }

        setSlot(slotId, new net.minecraft.world.item.ItemStack(Items.LIME_STAINED_GLASS_PANE, number));
        syncAll();
        nextExpected++;
        clickSound();

        if (nextExpected > orderNumbers.size()) {
            complete();
        }
    }
}





