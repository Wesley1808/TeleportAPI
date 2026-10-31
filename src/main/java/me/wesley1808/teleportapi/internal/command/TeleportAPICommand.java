package me.wesley1808.teleportapi.internal.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import me.wesley1808.teleportapi.internal.TeleportAPI;
import me.wesley1808.teleportapi.internal.config.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;

public class TeleportAPICommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(literal("teleportapi").requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(literal("reload").executes(TeleportAPICommand::reload))
                .then(literal("save").executes(TeleportAPICommand::save))
        );
    }

    private static int save(CommandContext<CommandSourceStack> context) {
        ConfigManager.save();
        context.getSource().sendSuccess(() -> Component.literal("Configs saved!").withStyle(ChatFormatting.GREEN), true);
        return Command.SINGLE_SUCCESS;
    }

    private static int reload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            ConfigManager.load();
            source.sendSuccess(() -> Component.literal("Configs reloaded!").withStyle(ChatFormatting.GREEN), true);
        } catch (Exception ex) {
            TeleportAPI.LOGGER.error("Failed to reload configs!", ex);
            source.sendFailure(Component.literal(ex.getMessage()));
            return 0;
        }
        return Command.SINGLE_SUCCESS;
    }
}
