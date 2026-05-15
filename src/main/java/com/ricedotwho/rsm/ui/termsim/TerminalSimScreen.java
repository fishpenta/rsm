package com.ricedotwho.rsm.ui.termsim;

import com.ricedotwho.rsm.component.impl.Terminals;
import com.ricedotwho.rsm.event.impl.game.TerminalEvent;
import com.ricedotwho.rsm.utils.Accessor;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Objects;
import java.util.function.IntFunction;

public abstract class TerminalSimScreen extends AbstractContainerScreen<ChestMenu> implements Accessor {
    protected final SimpleContainer container;
    protected final int rows;
    private final String terminalTitle;
    private boolean closing = false;
    protected final ItemStack blackPane = createBlankPane();

    protected TerminalSimScreen(String title, int size) {
        this(createBundle(title, size));
    }

    private TerminalSimScreen(Bundle bundle) {
        super(bundle.menu(), bundle.playerInventory(), Component.literal(bundle.title()));
        this.container = bundle.container();
        this.rows = bundle.rows();
        this.terminalTitle = bundle.title();
        this.imageWidth = 176;
        this.imageHeight = 114 + (this.rows * 18);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    private static Bundle createBundle(String title, int size) {
        Inventory inventory = Objects.requireNonNull(mc.player, "Player must be present to open the terminal simulator").getInventory();
        SimpleContainer container = new SimpleContainer(size);
        ChestMenu menu = new ChestMenu(menuType(size), 0, inventory, container, size / 9);
        return new Bundle(container, menu, inventory, title, size / 9);
    }

    private static MenuType<?> menuType(int size) {
        return switch (size / 9) {
            case 1 -> MenuType.GENERIC_9x1;
            case 2 -> MenuType.GENERIC_9x2;
            case 3 -> MenuType.GENERIC_9x3;
            case 4 -> MenuType.GENERIC_9x4;
            case 5 -> MenuType.GENERIC_9x5;
            case 6 -> MenuType.GENERIC_9x6;
            default -> throw new IllegalArgumentException("Unsupported terminal simulator size: " + size);
        };
    }

    @Override
    protected void init() {
        super.init();
        buildContents();
    }

    protected abstract void buildContents();

    protected abstract void handleSlotClick(int slotId, int button);

    public final void handleSolverClick(int slotId, int button) {
        Terminals.registerSimulatorClick();
        handleSlotClick(slotId, button);
    }

    public final String getTerminalTitle() {
        return terminalTitle;
    }

    @Override
    public void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
    }

    @Override
    public void slotClicked(Slot slot, int slotId, int button, ClickType actionType) {
        if (slot == null) return;
        handleSlotClick(slotId, button);
    }

    protected void setSlot(int index, ItemStack stack) {
        container.setItem(index, stack);
        container.setChanged();
        syncSlot(index);
    }

    protected void fillSlots(IntFunction<ItemStack> generator) {
        for (int i = 0; i < container.getContainerSize(); i++) {
            setSlot(i, generator.apply(i));
        }
    }

    protected void syncAll() {
        if (Terminals.getCurrent() != null) {
            for (int i = 0; i < container.getContainerSize(); i++) {
                Terminals.getCurrent().onSlot(i, container.getItem(i));
            }
        }
    }

    protected void syncSlot(int index) {
        if (Terminals.getCurrent() != null) {
            Terminals.getCurrent().onSlot(index, container.getItem(index));
        }
    }

    protected ItemStack named(Item item, String name) {
        return named(item, name, null);
    }

    protected ItemStack named(Item item, String name, Boolean glint) {
        ItemStack stack = new ItemStack(item);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(name));
        if (glint != null) {
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, glint);
        }
        return stack;
    }

    protected ItemStack blank(Item item) {
        return named(item, "");
    }

    protected void clickSound() {
        if (mc.player != null) {
            mc.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1f, 1f);
        }
    }

    protected void complete() {
        closing = true;
        new TerminalEvent.Close(true).post();
        if (mc.player != null) {
            mc.player.playSound(SoundEvents.EXPERIENCE_ORB_PICKUP, 1f, 1.2f);
        }
        mc.setScreen(null);
    }

    protected int rowOf(int index) {
        return index / 9;
    }

    protected int colOf(int index) {
        return index % 9;
    }

    protected boolean inPlayArea(int index, int minRow, int maxRow, int minCol, int maxCol) {
        int row = rowOf(index);
        int col = colOf(index);
        return row >= minRow && row <= maxRow && col >= minCol && col <= maxCol;
    }

    protected static ItemStack blankPane() {
        return createBlankPane();
    }

    @Override
    public void onClose() {
        if (!closing) {
            new TerminalEvent.Close(false).post();
        }
        closing = false;
        super.onClose();
    }

    private static ItemStack createBlankPane() {
        ItemStack stack = new ItemStack(Items.BLACK_STAINED_GLASS_PANE);
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(""));
        return stack;
    }

    private record Bundle(SimpleContainer container, ChestMenu menu, Inventory playerInventory, String title, int rows) {
    }
}







