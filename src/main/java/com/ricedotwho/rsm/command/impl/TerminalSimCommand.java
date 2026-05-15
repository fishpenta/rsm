package com.ricedotwho.rsm.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.ricedotwho.rsm.command.Command;
import com.ricedotwho.rsm.command.api.CommandInfo;
import com.ricedotwho.rsm.ui.termsim.TerminalSimulator;
import com.ricedotwho.rsm.component.impl.task.TaskComponent;
import com.ricedotwho.rsm.utils.ChatUtils;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.SharedSuggestionProvider;

@CommandInfo(name = "term", aliases = {"termsim"}, description = "Open a local terminal simulator")
public class TerminalSimCommand extends Command {
    @Override
    public LiteralArgumentBuilder<ClientSuggestionProvider> build() {
        return literal(name())
                .executes(ctx -> {
                    ChatUtils.chat("Usage: .term <type>");
                    ChatUtils.chat("Available types: %s", String.join(", ", TerminalSimulator.getTypeSuggestions()));
                    return 1;
                })
                .then(argument("type", StringArgumentType.greedyString())
                        .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(TerminalSimulator.getTypeSuggestions(), builder))
                        .executes(ctx -> {
                            String type = StringArgumentType.getString(ctx, "type");
                            TaskComponent.onTick(() -> TerminalSimulator.open(type));
                            return 1;
                        })
                );
    }
}

