package de.erdbeerbaerlp.htdcintegration.hytaleCommands;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.AbstractCommand;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import de.erdbeerbaerlp.htdcintegration.DiscordPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class DiscordCommand extends AbstractAsyncCommand {

    public DiscordCommand() {
        super("discord", "Discord Integration");
        for (final AbstractCommand cmd : HTDiscordSubCommandRegistry.getCommands()) {
            addSubCommand(cmd);
        }
    }

    @NotNull
    @Override
    protected CompletableFuture<Void> executeAsync(@NotNull CommandContext commandContext) {
        return CompletableFuture.runAsync(() -> {
            commandContext.sendMessage(Message.raw(DiscordPlugin.getInstance().messages.get().cmdDiscordMessage).link(DiscordPlugin.getInstance().messages.get().inviteURL));
        });
    }
}