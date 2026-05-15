package com.ricedotwho.rsm.ui.termsim;

import com.ricedotwho.rsm.component.impl.Terminals;
import com.ricedotwho.rsm.component.impl.task.TaskComponent;
import com.ricedotwho.rsm.data.TerminalType;
import com.ricedotwho.rsm.event.impl.game.TerminalEvent;
import com.ricedotwho.rsm.utils.Accessor;
import com.ricedotwho.rsm.utils.ChatUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.world.inventory.MenuType;
import lombok.experimental.UtilityClass;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

@UtilityClass
public class TerminalSimulator implements Accessor {
    public enum Type {
        PANES("panes", PanesSimScreen::new),
        RUBIX("rubix", RubixSimScreen::new),
        ORDER("order", OrderSimScreen::new),
        STARTS_WITH("starts_with", StartsWithSimScreen::new),
        SELECT("select", SelectSimScreen::new),
        MELODY("melody", MelodySimScreen::new);

        private final String commandName;
        private final Supplier<TerminalSimScreen> screenFactory;

        Type(String commandName, Supplier<TerminalSimScreen> screenFactory) {
            this.commandName = commandName;
            this.screenFactory = screenFactory;
        }

        public String commandName() {
            return commandName;
        }

        public TerminalSimScreen createScreen() {
            return screenFactory.get();
        }

        public static Optional<Type> fromInput(String input) {
            if (input == null) {
                return Optional.empty();
            }

            String normalized = normalize(input);
            if (normalized.equals("random")) {
                Type[] values = values();
                return Optional.of(values[(int) (Math.random() * values.length)]);
            }

            return Arrays.stream(values())
                    .filter(type -> type.matches(normalized))
                    .findFirst();
        }

        private boolean matches(String normalized) {
            return normalize(commandName).equals(normalized)
                    || normalize(name()).equals(normalized)
                    || normalize(commandName).startsWith(normalized)
                    || normalize(name()).startsWith(normalized);
        }
    }

    public void open(String input) {
        Optional<Type> type = Type.fromInput(input);
        if (type.isEmpty()) {
            ChatUtils.chat("Unknown terminal type '%s'. Try one of: %s", input, String.join(", ", getTypeSuggestions()));
            return;
        }

        open(type.get());
    }

    public void open(Type type) {
        TerminalSimScreen screen = type.createScreen();
        if (Terminals.getCurrent() != null) {
            new TerminalEvent.Close(false).post();
        }

        new TerminalEvent.Open(
                new ClientboundOpenScreenPacket(0, menuType(screen.rows), Component.literal(screen.getTerminalTitle())),
                TerminalType.valueOf(type.name())
        ).post();

        Terminals.startSimulatorTiming();

        TaskComponent.onTick(() -> mc.setScreen(screen));
    }

    public List<String> getTypeSuggestions() {
        List<String> types = Arrays.stream(Type.values()).map(Type::commandName).collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        types.add("random");
        return types;
    }

    private static String normalize(String input) {
        return Normalizer.normalize(input, Normalizer.Form.NFKC)
                .trim()
                .toLowerCase(Locale.ROOT)
                .replace('-', '_')
                .replace(' ', '_');
    }

    private static MenuType<?> menuType(int rows) {
        return switch (rows) {
            case 1 -> MenuType.GENERIC_9x1;
            case 2 -> MenuType.GENERIC_9x2;
            case 3 -> MenuType.GENERIC_9x3;
            case 4 -> MenuType.GENERIC_9x4;
            case 5 -> MenuType.GENERIC_9x5;
            case 6 -> MenuType.GENERIC_9x6;
            default -> throw new IllegalArgumentException("Unsupported terminal simulator rows: " + rows);
        };
    }
}





