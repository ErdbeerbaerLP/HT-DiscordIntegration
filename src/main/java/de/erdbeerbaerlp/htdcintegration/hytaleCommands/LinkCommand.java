package de.erdbeerbaerlp.htdcintegration.hytaleCommands;


import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.erdbeerbaerlp.htdcintegration.DiscordPlugin;
import de.erdbeerbaerlp.htdcintegration.storage.LinkManager;
import org.jetbrains.annotations.NotNull;


import java.awt.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class LinkCommand extends AbstractPlayerCommand {

    public LinkCommand() {
        super("link", DiscordPlugin.getInstance().messages.get().cmdLinkIngameDescription);
    }


    @Override
    protected void execute(@NotNull CommandContext commandContext, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {

        if (DiscordPlugin.getInstance().linkconf.get().enableLinking && !DiscordPlugin.getInstance().linkconf.get().linkingWhitelistMode) {
            if (LinkManager.isPlayerLinked(playerRef.getUuid())) {
                commandContext.sendMessage(Message.raw(DiscordPlugin.getInstance().messages.get().alreadyLinked.replace("%player%", DiscordPlugin.getInstance().discord.getJda().getUserById(LinkManager.getLink(null, playerRef.getUuid()).discordID).getAsTag())).color(Color.RED));
            }
            final int r = LinkManager.genLinkNumber(playerRef.getUuid());
            commandContext.sendMessage(Message.raw(DiscordPlugin.getInstance().messages.get().linkMsgIngame.replace("%num%", r + "")).color(Color.ORANGE));
        } else {
            commandContext.sendMessage(Message.raw(DiscordPlugin.getInstance().messages.get().subcommandDisabled).color(Color.RED));
        }

    }
}