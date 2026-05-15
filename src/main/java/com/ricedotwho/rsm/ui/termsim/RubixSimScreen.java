package com.ricedotwho.rsm.ui.termsim;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.List;

public class RubixSimScreen extends TerminalSimScreen {
    private static final List<Item> COLOR_ORDER = List.of(
            Items.BLUE_STAINED_GLASS_PANE,
            Items.RED_STAINED_GLASS_PANE,
            Items.ORANGE_STAINED_GLASS_PANE,
            Items.YELLOW_STAINED_GLASS_PANE,
            Items.GREEN_STAINED_GLASS_PANE
    );

    public RubixSimScreen() {
        super("Change all to same color!", 9 * 5);
    }

    @Override
    protected void buildContents() {
        fillSlots(index -> inPlayArea(index, 1, 3, 3, 5)
                ? blank(COLOR_ORDER.get((int) (Math.random() * COLOR_ORDER.size())))
                : blackPane);
    }

    @Override
    protected void handleSlotClick(int slotId, int button) {
        if (slotId < 0 || slotId >= container.getContainerSize()) {
            return;
        }

        if (!inPlayArea(slotId, 1, 3, 3, 5)) {
            return;
        }

        Slot slot = menu.slots.get(slotId);
        Item current = slot.getItem().getItem();
        int index = COLOR_ORDER.indexOf(current);
        if (index == -1) {
            return;
        }

        int next = button == 1 ? (index - 1 + COLOR_ORDER.size()) % COLOR_ORDER.size() : (index + 1) % COLOR_ORDER.size();
        setSlot(slotId, blank(COLOR_ORDER.get(next)));
        syncAll();
        clickSound();

        if (finished()) {
            complete();
        }
    }

    private boolean finished() {
        Item first = null;
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (!inPlayArea(i, 1, 3, 3, 5)) {
                continue;
            }

            Item current = container.getItem(i).getItem();
            if (!COLOR_ORDER.contains(current)) {
                continue;
            }

            if (first == null) {
                first = current;
                continue;
            }

            if (first != current) {
                return false;
            }
        }

        return true;
    }
}


