package de.erdbeerbaerlp.htdcintegration.discordCommands;

import de.erdbeerbaerlp.htdcintegration.DiscordPlugin;
import de.erdbeerbaerlp.htdcintegration.storage.LinkManager;
import de.erdbeerbaerlp.htdcintegration.util.MessageUtil;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import net.dv8tion.jda.api.utils.messages.MessageEditData;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;


public class CommandLink extends DiscordCommand {

    public CommandLink() {
        super("link", DiscordPlugin.getInstance().linkconf.get().linkingWhitelistMode ? DiscordPlugin.getInstance().messages.get().cmdLinkWhitelistDescription : DiscordPlugin.getInstance().messages.get().cmdLinkDescription);
        addOption(OptionType.INTEGER, "code", "Link Code", true);
    }

    @Override
    public void execute(SlashCommandInteractionEvent ev, ReplyCallbackAction replyCallbackAction) {
        final CompletableFuture<InteractionHook> reply = replyCallbackAction.setEphemeral(true).submit();
        Member m = null;
        if (ev.getChannelType().isGuild())
           m = ev.getMember();
        else
            m = DiscordPlugin.getInstance().discord.getMemberById(ev.getUser().getIdLong());
        if(m == null){
            DiscordPlugin.getInstance().getLogger().atWarning().log("[Link Command] Could not find member for user " + ev.getUser().getIdLong()+". Interrupting.");
        }
        if (m != null)
            if (DiscordPlugin.getInstance().linkconf.get().requiredRoles.length != 0) {
                AtomicBoolean ok = new AtomicBoolean(false);
                m.getRoles().forEach((role) -> {
                    for (String s : DiscordPlugin.getInstance().linkconf.get().requiredRoles) {
                        if (s.equals(role.getId())) ok.set(true);
                    }
                });
                if (!ok.get()) {
                    reply.thenAccept((c) -> c.editOriginal(MessageEditData.fromContent(DiscordPlugin.getInstance().messages.get().link_requiredRole)).queue());
                    return;
                }
            }
        final OptionMapping code = ev.getOption("code");
        if (code != null) {
            try {
                int num = Integer.parseInt(code.getAsString());
                if (LinkManager.isDiscordUserLinked(ev.getUser().getId())) {
                    reply.thenAccept((c) -> c.editOriginal(DiscordPlugin.getInstance().messages.get().alreadyLinked).queue());
                    return;
                }
                if (LinkManager.pendingLinks.containsKey(num)) {
                    final boolean linked = LinkManager.linkPlayer(ev.getUser().getId(), LinkManager.pendingLinks.get(num).getValue());
                    if (linked) {
                        LinkManager.save();
                        reply.thenAccept((c) -> c.editOriginal(DiscordPlugin.getInstance().messages.get().linkSuccessful).queue());
                        MessageUtil.broadcastMessageIngame((DiscordPlugin.getInstance().messages.get().linkSuccessfulIngame.replace("%playername%", ev.getUser().getName()).replace("%username%", ev.getUser().getAsTag())));
                    } else
                        reply.thenAccept((c) -> c.editOriginal(DiscordPlugin.getInstance().messages.get().linkFailed).queue());
                } else {
                    reply.thenAccept((c) -> c.editOriginal(DiscordPlugin.getInstance().messages.get().invalidLinkNumber).queue());
                }
            } catch (NumberFormatException nfe) {
                reply.thenAccept((c) -> c.editOriginal(DiscordPlugin.getInstance().messages.get().linkNumberNAN).queue());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    @Override
    public boolean canUserExecuteCommand(@NotNull User user) {
        return true;
    }
}