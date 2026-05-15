package com.ricedotwho.rsm.ui.termsim;

import com.ricedotwho.rsm.utils.ItemUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StartsWithSimScreen extends TerminalSimScreen {
    private static final List<String> LETTERS = List.of("A", "B", "C", "G", "D", "M", "N", "R", "S", "T", "W");
    private final String letter;

    public StartsWithSimScreen() {
        this(randomLetter());
    }

    private StartsWithSimScreen(String letter) {
        super("What starts with: '" + letter + "'?", 9 * 5);
        this.letter = letter;
    }

    @Override
    protected void buildContents() {
        List<Item> matching = matchingItems();
        List<Item> nonMatching = nonMatchingItems();
        int guaranteed = 10 + (int) (Math.random() * 7);

        fillSlots(index -> {
            if (!inPlayArea(index, 1, 3, 1, 7)) {
                return blackPane;
            }

            boolean chooseMatching = index == guaranteed || Math.random() > 0.7;
            Item item = chooseMatching ? random(matching) : random(nonMatching);
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
        if (!matches(slot.getItem()) || ItemUtils.isEnchanted(slot.getItem())) {
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
        for (int i = 0; i < container.getContainerSize(); i++) {
            ItemStack stack = container.getItem(i);
            if (matches(stack) && !ItemUtils.isEnchanted(stack)) {
                return false;
            }
        }
        return true;
    }

    private boolean matches(ItemStack stack) {
        if (stack.isEmpty() || stack.getItem() == Items.AIR) {
            return false;
        }

        String path = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath().toLowerCase(Locale.ROOT);
        return path.startsWith(letter.toLowerCase(Locale.ROOT));
    }

    private List<Item> matchingItems() {
        List<Item> items = new ArrayList<>();
        BuiltInRegistries.ITEM.forEach(item -> {
            String path = BuiltInRegistries.ITEM.getKey(item).getPath().toLowerCase(Locale.ROOT);
            if (path.startsWith(letter.toLowerCase(Locale.ROOT)) && !path.contains("pane") && item != Items.AIR) {
                items.add(item);
            }
        });
        return items;
    }

    private List<Item> nonMatchingItems() {
        List<Item> items = new ArrayList<>();
        BuiltInRegistries.ITEM.forEach(item -> {
            String path = BuiltInRegistries.ITEM.getKey(item).getPath().toLowerCase(Locale.ROOT);
            if (!path.startsWith(letter.toLowerCase(Locale.ROOT)) && !path.contains("pane") && item != Items.AIR) {
                items.add(item);
            }
        });
        return items;
    }

    private Item random(List<Item> items) {
        if (items.isEmpty()) {
            return null;
        }
        return items.get((int) (Math.random() * items.size()));
    }

    private static String randomLetter() {
        return LETTERS.get((int) (Math.random() * LETTERS.size()));
    }
}







