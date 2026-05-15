package com.ricedotwho.rsm.ui.termsim;

import net.minecraft.world.item.Items;

public class MelodySimScreen extends TerminalSimScreen {
    private int magentaColumn = 1;
    private int limeColumn = 2;
    private int currentRow = 1;
    private int limeDirection = 1;
    private int counter = 0;

    public MelodySimScreen() {
        super("Click the button on time!", 9 * 6);
    }

    @Override
    protected void buildContents() {
        currentRow = 1;
        magentaColumn = 1 + (int) (Math.random() * 5);
        limeColumn = 1;
        limeDirection = 1;
        counter = 0;
        updateGui();
    }

    @Override
    public void containerTick() {
        super.containerTick();
        if (mc.screen != this) {
            return;
        }

        if (++counter % 10 != 0) {
            return;
        }

        limeColumn += limeDirection;
        if (limeColumn == 1 || limeColumn == 5) {
            limeDirection *= -1;
        }
        updateGui();
    }

    @Override
    protected void handleSlotClick(int slotId, int button) {
        if (slotId < 0 || slotId >= container.getContainerSize()) {
            return;
        }

        if (slotId % 9 != 7 || limeColumn != magentaColumn || slotId / 9 != currentRow) {
            return;
        }

        magentaColumn = 1 + (int) (Math.random() * 4);
        currentRow++;
        updateGui();
        clickSound();

        if (currentRow >= 5) {
            complete();
        }
    }

    private void updateGui() {
        fillSlots(index -> {
            int col = index % 9;
            int row = index / 9;

            if (col == magentaColumn && (row == 0 || row == 5)) {
                return named(Items.MAGENTA_STAINED_GLASS_PANE, "");
            }

            if (col == limeColumn && row == currentRow) {
                return named(Items.LIME_STAINED_GLASS_PANE, "");
            }

            if (row == currentRow && col > 0 && col < 6) {
                return named(Items.RED_STAINED_GLASS_PANE, "");
            }

            if (col == 7 && row == currentRow) {
                return named(Items.LIME_TERRACOTTA, "");
            }

            if (col == 7 && row > 0 && row < 5) {
                return named(Items.RED_TERRACOTTA, "");
            }

            if (col > 0 && col < 6 && row > 0 && row < 5) {
                return named(Items.WHITE_STAINED_GLASS_PANE, "");
            }

            return blackPane;
        });
    }
}



