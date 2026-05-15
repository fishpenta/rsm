package com.ricedotwho.rsm.ui.termsim;

import com.ricedotwho.rsm.utils.ItemUtils;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SelectSimScreen extends TerminalSimScreen {
    private final DyeColor color;

    public SelectSimScreen() {
        this(randomColor());
    }

    private SelectSimScreen(DyeColor color) {
        super("Select all the " + displayName(color) + " items!", 9 * 6);
        this.color = color;
    }

    @Override
    protected void buildContents() {
        List<Item> matching = possibleItems(color);
        List<Item> otherItems = new ArrayList<>();
        Arrays.stream(DyeColor.values()).filter(other -> other != color).forEach(other -> otherItems.addAll(possibleItems(other)));

        int guaranteed = 10 + (int) (Math.random() * 7);
        fillSlots(index -> {
            if (!inPlayArea(index, 1, 4, 1, 7)) {
                return blackPane;
            }

            boolean chooseMatching = index == guaranteed || Math.random() > 0.75;
            Item item = chooseMatching ? random(matching) : random(otherItems);
            if (item == null) {
                return blackPane;
            }

            return named(item, new ItemStack(item).getHoverName().getString(), false);
        });
    }

    @Override
    protected void handleSlotClick(int slotId, int button) {
        if (slotId < 0 || slotId >= container.getContainerSize()) {
            return;
        }

        Slot slot = menu.slots.get(slotId);
        if (!possibleItems(color).contains(slot.getItem().getItem()) || ItemUtils.isEnchanted(slot.getItem())) {
            return;
        }

        setSlot(slotId, named(slot.getItem().getItem(), slot.getItem().getHoverName().getString(), true));
        syncAll();
        clickSound();

        if (finished()) {
            complete();
        }
    }

    private boolean finished() {
        List<Item> matching = possibleItems(color);
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (matching.contains(stack.getItem()) && !ItemUtils.isEnchanted(stack)) {
                return false;
            }
        }
        return true;
    }

    private static String displayName(DyeColor color) {
        return color.name().replace("LIGHT_GRAY", "SILVER").replace('_', ' ');
    }

    private static List<Item> possibleItems(DyeColor color) {
        return List.of(
                switch (color) {
                    case WHITE -> Items.WHITE_STAINED_GLASS;
                    case ORANGE -> Items.ORANGE_STAINED_GLASS;
                    case MAGENTA -> Items.MAGENTA_STAINED_GLASS;
                    case LIGHT_BLUE -> Items.LIGHT_BLUE_STAINED_GLASS;
                    case YELLOW -> Items.YELLOW_STAINED_GLASS;
                    case LIME -> Items.LIME_STAINED_GLASS;
                    case PINK -> Items.PINK_STAINED_GLASS;
                    case GRAY -> Items.GRAY_STAINED_GLASS;
                    case LIGHT_GRAY -> Items.LIGHT_GRAY_STAINED_GLASS;
                    case CYAN -> Items.CYAN_STAINED_GLASS;
                    case PURPLE -> Items.PURPLE_STAINED_GLASS;
                    case BLUE -> Items.BLUE_STAINED_GLASS;
                    case BROWN -> Items.BROWN_STAINED_GLASS;
                    case GREEN -> Items.GREEN_STAINED_GLASS;
                    case RED -> Items.RED_STAINED_GLASS;
                    case BLACK -> Items.BLACK_STAINED_GLASS;
                },
                switch (color) {
                    case WHITE -> Items.WHITE_WOOL;
                    case ORANGE -> Items.ORANGE_WOOL;
                    case MAGENTA -> Items.MAGENTA_WOOL;
                    case LIGHT_BLUE -> Items.LIGHT_BLUE_WOOL;
                    case YELLOW -> Items.YELLOW_WOOL;
                    case LIME -> Items.LIME_WOOL;
                    case PINK -> Items.PINK_WOOL;
                    case GRAY -> Items.GRAY_WOOL;
                    case LIGHT_GRAY -> Items.LIGHT_GRAY_WOOL;
                    case CYAN -> Items.CYAN_WOOL;
                    case PURPLE -> Items.PURPLE_WOOL;
                    case BLUE -> Items.BLUE_WOOL;
                    case BROWN -> Items.BROWN_WOOL;
                    case GREEN -> Items.GREEN_WOOL;
                    case RED -> Items.RED_WOOL;
                    case BLACK -> Items.BLACK_WOOL;
                },
                switch (color) {
                    case WHITE -> Items.WHITE_CONCRETE;
                    case ORANGE -> Items.ORANGE_CONCRETE;
                    case MAGENTA -> Items.MAGENTA_CONCRETE;
                    case LIGHT_BLUE -> Items.LIGHT_BLUE_CONCRETE;
                    case YELLOW -> Items.YELLOW_CONCRETE;
                    case LIME -> Items.LIME_CONCRETE;
                    case PINK -> Items.PINK_CONCRETE;
                    case GRAY -> Items.GRAY_CONCRETE;
                    case LIGHT_GRAY -> Items.LIGHT_GRAY_CONCRETE;
                    case CYAN -> Items.CYAN_CONCRETE;
                    case PURPLE -> Items.PURPLE_CONCRETE;
                    case BLUE -> Items.BLUE_CONCRETE;
                    case BROWN -> Items.BROWN_CONCRETE;
                    case GREEN -> Items.GREEN_CONCRETE;
                    case RED -> Items.RED_CONCRETE;
                    case BLACK -> Items.BLACK_CONCRETE;
                },
                switch (color) {
                    case WHITE -> Items.BONE_MEAL;
                    case BLACK -> Items.INK_SAC;
                    case BROWN -> Items.COCOA_BEANS;
                    case LIGHT_BLUE -> Items.LIGHT_BLUE_DYE;
                    case LIGHT_GRAY -> Items.LIGHT_GRAY_DYE;
                    default -> getDye(color);
                }
        );
    }

    private static Item getDye(DyeColor color) {
        return switch (color) {
            case WHITE -> Items.WHITE_DYE;
            case ORANGE -> Items.ORANGE_DYE;
            case MAGENTA -> Items.MAGENTA_DYE;
            case LIGHT_BLUE -> Items.LIGHT_BLUE_DYE;
            case YELLOW -> Items.YELLOW_DYE;
            case LIME -> Items.LIME_DYE;
            case PINK -> Items.PINK_DYE;
            case GRAY -> Items.GRAY_DYE;
            case LIGHT_GRAY -> Items.LIGHT_GRAY_DYE;
            case CYAN -> Items.CYAN_DYE;
            case PURPLE -> Items.PURPLE_DYE;
            case BLUE -> Items.BLUE_DYE;
            case BROWN -> Items.BROWN_DYE;
            case GREEN -> Items.GREEN_DYE;
            case RED -> Items.RED_DYE;
            case BLACK -> Items.BLACK_DYE;
        };
    }

    private static Item random(List<Item> items) {
        if (items.isEmpty()) {
            return null;
        }
        return items.get((int) (Math.random() * items.size()));
    }

    private static DyeColor randomColor() {
        DyeColor[] values = DyeColor.values();
        return values[(int) (Math.random() * values.length)];
    }
}






