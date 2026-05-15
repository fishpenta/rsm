package com.ricedotwho.rsm.ui.termsim;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Items;

public class PanesSimScreen extends TerminalSimScreen {
    public PanesSimScreen() {
        super("Correct all the panes!", 9 * 5);
    }

    @Override
    protected void buildContents() {
        fillSlots(index -> inPlayArea(index, 1, 3, 2, 6)
                ? (Math.random() > 0.75 ? blank(Items.LIME_STAINED_GLASS_PANE) : blank(Items.RED_STAINED_GLASS_PANE))
                : blackPane);
    }

    @Override
    protected void handleSlotClick(int slotId, int button) {
        if (slotId < 0 || slotId >= container.getContainerSize()) {
            return;
        }

        Slot slot = menu.slots.get(slotId);
        if (!slot.getItem().is(Items.RED_STAINED_GLASS_PANE)) {
            return;
        }

        setSlot(slotId, blank(Items.LIME_STAINED_GLASS_PANE));
        syncAll();
        clickSound();

        if (finished()) {
            complete();
        }
    }

    private boolean finished() {
        for (int i = 0; i < container.getContainerSize(); i++) {
            if (inPlayArea(i, 1, 3, 2, 6) && container.getItem(i).is(Items.RED_STAINED_GLASS_PANE)) {
                return false;
            }
        }
        return true;
    }
}


