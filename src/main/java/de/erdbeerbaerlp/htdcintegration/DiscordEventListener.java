package de.erdbeerbaerlp.htdcintegration;

import com.vdurmont.emoji.EmojiParser;
import de.erdbeerbaerlp.htdcintegration.discordCommands.DiscordCommand;
import de.erdbeerbaerlp.htdcintegration.storage.CommandRegistry;
import de.erdbeerbaerlp.htdcintegration.util.DiscordMessage;
import de.erdbeerbaerlp.htdcintegration.util.MessageUtil;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberUpdateEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyCallbackAction;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DiscordEventListener implements EventListener {
    public static String formatEmoteMessage(List<CustomEmoji> emotes, String msg) {
        msg = EmojiParser.parseToAliases(msg);
        for (final Emoji e : emotes) {
            msg = msg.replace(e.toString(), ":" + e.getName() + ":");
        }
        return msg;
    }

    @Override
    public void onEvent(@NotNull GenericEvent event) {

        if (event instanceof GuildMemberUpdateEvent ev) {
            DiscordPlugin.getInstance().discord.memberCache.replace(ev.getMember().getIdLong(), ev.getMember());
        }

        if (event instanceof SlashCommandInteractionEvent ev) {
            if (!true) return;
            if (ev.getChannelType().equals(ChannelType.TEXT)) {
                if (CommandRegistry.registeredCMDs.containsKey(ev.getCommandId())) {
                    final DiscordCommand cfCommand = CommandRegistry.registeredCMDs.get(ev.getCommandId());
                    String cmd = cfCommand.getName();
                    String args = ev.getOption("args") != null ? ev.getOption("args").getAsString() : "";
                    processDiscordCommand(ev, ArrayUtils.addAll(new String[]{cmd}, args.split(" ")), ev.getChannel(), ev.getUser());
                }
            }
        }

        if (event instanceof MessageReceivedEvent ev) {
            if (ev.isWebhookMessage() && DiscordPlugin.getInstance().webhookconf.get().enableWebhookMode) {
                // Hacky way to lower risk of duplicate webhook messages
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    return;
                }
            }
            if (DiscordPlugin.getInstance().messages.get().ingameMessage.isBlank()) return;
            if (DiscordPlugin.getInstance().discord.recentMessages.containsKey(ev.getMessageIdLong())) return;
            if (!ev.getAuthor().getId().equals(ev.getJDA().getSelfUser().getId())) {
                if (ev.getChannel().getIdLong() == DiscordPlugin.getInstance().config.get().getChannelID()) {
                    String msg = ev.getMessage().getContentDisplay();
                    msg = formatEmoteMessage(ev.getMessage().getMentions().getCustomEmojis(), msg);
                    MessageUtil.broadcastMessageIngame(
                            DiscordPlugin.getInstance().messages.get().ingameMessage
                                    .replace("%username%", MessageUtil.normalize(ev.getAuthor().getEffectiveName()))
                                    .replace("%uniquename%", ev.getAuthor().getName())
                                    .replace("%message%", msg));
                }

            }
        }
        if (event instanceof GuildVoiceUpdateEvent ev) {
            AudioChannelUnion joinedChannel = ev.getChannelJoined();
            AudioChannelUnion leftChannel = ev.getChannelLeft();
            Member member = ev.getMember();

            if (DiscordPlugin.getInstance().config.get().getVcMessageWhitelist().length == 0 || (joinedChannel != null && ArrayUtils.contains(DiscordPlugin.getInstance().config.get().getVcMessageWhitelist(), joinedChannel.getId())) || (leftChannel != null && ArrayUtils.contains(DiscordPlugin.getInstance().config.get().getVcMessageWhitelist(), leftChannel.getId()))) {
                if (joinedChannel != null && !ev.getGuild().getSelfMember().hasPermission(joinedChannel, Permission.VIEW_CHANNEL))
                    joinedChannel = null;
                if (leftChannel != null && !ev.getGuild().getSelfMember().hasPermission(leftChannel, Permission.VIEW_CHANNEL))
                    leftChannel = null;

                if (joinedChannel != null && leftChannel == null) {
                    if (!DiscordPlugin.getInstance().messages.get().vcJoin.isBlank())
                        MessageUtil.broadcastMessageIngame(DiscordPlugin.getInstance().messages.get().vcJoin
                                .replace("%username%", MessageUtil.normalize(member.getEffectiveName()))
                                .replace("%dstName%", MessageUtil.normalize(joinedChannel.getName())));
                } else if (leftChannel != null && joinedChannel == null) {
                    if (!DiscordPlugin.getInstance().messages.get().vcLeave.isBlank())
                        MessageUtil.broadcastMessageIngame(DiscordPlugin.getInstance().messages.get().vcLeave
                                .replace("%username%", MessageUtil.normalize(member.getEffectiveName()))
                                .replace("%sourceName%", MessageUtil.normalize(leftChannel.getName())));
                } else if (joinedChannel != null && leftChannel != null) {
                    if (!DiscordPlugin.getInstance().messages.get().vcMove.isBlank())
                        MessageUtil.broadcastMessageIngame(DiscordPlugin.getInstance().messages.get().vcMove
                                .replace("%username%", MessageUtil.normalize(member.getEffectiveName()))
                                .replace("%sourceName%", MessageUtil.normalize(leftChannel.getName()))
                                .replace("%dstName%", MessageUtil.normalize(joinedChannel.getName())));
                }
            }
        }

    }

    private void processDiscordCommand(final SlashCommandInteractionEvent ev, final String[] command,
                                       final MessageChannelUnion channel, User sender) {
        boolean hasPermission = true;
        boolean executed = false;
        ReplyCallbackAction replyCallbackAction = ev.deferReply();
        for (final DiscordCommand cmd : CommandRegistry.getCommandList()) {
            if (cmd.getName().equals(command[0])) {
                if (cmd.canUserExecuteCommand(sender)) {
                    cmd.execute(ev, replyCallbackAction);
                    executed = true;
                } else {
                    hasPermission = false;
                }
            }

        }
        if (!hasPermission) {
            replyCallbackAction.setContent(DiscordPlugin.getInstance().messages.get().noPermission).setEphemeral(true).queue();
        }
    }
}
