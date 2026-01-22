package de.erdbeerbaerlp.htdcintegration;

import club.minnced.discord.webhook.external.JDAWebhookClient;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import de.erdbeerbaerlp.htdcintegration.util.DiscordMessage;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.StandardGuildMessageChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.internal.utils.PermissionUtil;

import java.util.*;

public class Discord {
    private JDA jda;

    public Discord() throws InterruptedException {
        jda = JDABuilder.createDefault(DiscordPlugin.getInstance().config.get().getBotToken()).enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS).build().awaitReady();
        jda.addEventListener(new DiscordEventListener());
    }

    public JDA getJda() {
        return jda;
    }

    private final HashMap<String, JDAWebhookClient> webhookClis = new HashMap<>();


    private final HashMap<String, Webhook> webhookHashMap = new HashMap<>();

    /**
     * @return an instance of the webhook or null
     */

    public Webhook getWebhook(final GuildMessageChannel ic) {
        if (!DiscordPlugin.getInstance().webhookconf.get().enableWebhookMode || ic == null) return null;
        if (ic instanceof ThreadChannel) {
            ThreadChannel c = (ThreadChannel) ic;
            return webhookHashMap.computeIfAbsent(c.getId(), cid -> {
                if (!PermissionUtil.checkPermission(c.getParentChannel(), getMemberById(jda.getSelfUser().getIdLong()), Permission.MANAGE_WEBHOOKS)) {
                    DiscordPlugin.getInstance().getLogger().atWarning().log("ERROR! Bot does not have permission to manage webhooks, disabling webhook");
                    DiscordPlugin.getInstance().webhookconf.get().enableWebhookMode = false;
                    DiscordPlugin.getInstance().webhookconf.save();
                    return null;
                }
                for (final Webhook web : c.getParentMessageChannel().asStandardGuildMessageChannel().retrieveWebhooks().complete()) {
                    if (web.getName().equals(DiscordPlugin.getInstance().webhookconf.get().webhookName)) {
                        return web;
                    }
                }

                return c.getParentMessageChannel().asStandardGuildMessageChannel().createWebhook(DiscordPlugin.getInstance().webhookconf.get().webhookName).complete();
            });
        } else if (ic instanceof StandardGuildMessageChannel) {
            StandardGuildMessageChannel c = (StandardGuildMessageChannel) ic;
            return webhookHashMap.computeIfAbsent(c.getId(), cid -> {
                if (!PermissionUtil.checkPermission(c, getMemberById(jda.getSelfUser().getIdLong()), Permission.MANAGE_WEBHOOKS)) {
                    DiscordPlugin.getInstance().getLogger().atWarning().log("ERROR! Bot does not have permission to manage webhooks, disabling webhook");
                    DiscordPlugin.getInstance().webhookconf.get().enableWebhookMode = false;
                    DiscordPlugin.getInstance().webhookconf.save();
                    return null;
                }
                for (final Webhook web : c.retrieveWebhooks().complete()) {
                    if (web.getName().equals(DiscordPlugin.getInstance().webhookconf.get().webhookName)) {
                        return web;
                    }
                }
                return c.createWebhook(DiscordPlugin.getInstance().webhookconf.get().webhookName).complete();
            });
        }
        return null;
    }

    /**
     * Returns the corresponding {@link WebhookClient} for the given Channel ID
     *
     * @param channelID Channel ID
     * @return Webhook Client for the Channel ID
     */

    public JDAWebhookClient getWebhookCli(String channelID) {
        return webhookClis.computeIfAbsent(channelID, (id) -> {
            final GuildMessageChannel channel = getChannel(id);
            final Webhook wh = getWebhook(channel);

            if (wh == null) return null;

            JDAWebhookClient cli = JDAWebhookClient.from(wh);
            if (channel instanceof ThreadChannel) {
                ThreadChannel c = (ThreadChannel) channel;
                cli = cli.onThread(c.getIdLong());
            }
            return cli;
        });
    }


    /**
     * Holds messages recently forwarded to discord in format MessageID, Sender UUID
     */
    final HashMap<Long, UUID> recentMessages = new HashMap<>(150);

    /**
     * Saves a new Message into the recent messages list
     *
     * @param msgID Message ID
     * @param uuid  Sender UUID
     */
    public void rememberRecentMessage(long msgID, UUID uuid) {
        if (recentMessages.size() + 1 >= 150) {
            final long oldest = recentMessages.entrySet().stream().sorted(Map.Entry.comparingByKey()).iterator().next().getKey();
            recentMessages.remove(oldest);
        }
        recentMessages.put(msgID, uuid);
    }










    public void sendSysMessage(final DiscordMessage message){
        final GuildMessageChannel channel = getChannel();
        try {
            if (DiscordPlugin.getInstance().webhookconf.get().enableWebhookMode) {
                final ArrayList<WebhookMessageBuilder> messages = message.buildWebhookMessages();
                messages.forEach((builder) -> {
                    builder.setUsername(DiscordPlugin.getInstance().webhookconf.get().webhookDisplayName);
                    builder.setAvatarUrl(DiscordPlugin.getInstance().webhookconf.get().webhookAvatarURL);
                    final JDAWebhookClient webhookCli = getWebhookCli(channel.getId());
                    if (webhookCli != null) {
                        webhookCli.send(builder.build()).thenAccept((a)-> rememberRecentMessage(a.getId(),  null));
                    }
                });
            } else {
                channel.sendMessage(message.buildMessages()).submit().thenAccept((a) -> rememberRecentMessage(a.getIdLong(), null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void sendPlayerChatMessage(final DiscordMessage message, PlayerRef p){
        final GuildMessageChannel channel = getChannel();

        try {
            if (DiscordPlugin.getInstance().webhookconf.get().enableWebhookMode) {
                final ArrayList<WebhookMessageBuilder> messages = message.buildWebhookMessages();
                messages.forEach((builder) -> {
                    builder.setUsername(p.getUsername());
                    builder.setAvatarUrl(DiscordPlugin.getInstance().webhookconf.get().webhookPlayerAvatarURL.replace("%uuid%", p.getUuid().toString()).replace("%playername%", p.getUsername()));
                    final JDAWebhookClient webhookCli = getWebhookCli(channel.getId());
                    if (webhookCli != null) {
                        webhookCli.send(builder.build()).thenAccept((a)-> rememberRecentMessage(a.getId(),  null));
                    }
                });
            } else{
                message.setMessage(DiscordPlugin.getInstance().messages.get().discordMessage.replace("%playername%", p.getUsername()).replace("%message%", message.getMessage()));
                message.setIsChatMessage();
                channel.sendMessage(message.buildMessages()).submit().thenAccept((a) -> rememberRecentMessage(a.getIdLong(), p.getUuid()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





    public GuildMessageChannel getChannel() {
        return getChannel(DiscordPlugin.getInstance().config.get().channelID+"");
    }
    public GuildMessageChannel getChannel(String id) {
        return jda.getChannelById(GuildMessageChannel.class,DiscordPlugin.getInstance().config.get().channelID);
    }


    /**
     * Cache of members so that they don't need to be fetched every single time
     */
    static final Map<Long, Member> memberCache = new HashMap<>();



    /**
     * Get member by ID from cache or from discord, saving the member to cache
     *
     * @param userid ID of the member
     * @return Fetched member, or null
     */
    public Member getMemberById(final String userid) {
        return getMemberById(Long.parseLong(userid));
    }

    /**
     * Get member by ID from cache or from discord, saving the member to cache
     *
     * @param userid ID of the member
     * @return Fetched member, or null
     */
    public Member getMemberById(final Long userid) {
        if (memberCache.containsKey(userid)) return memberCache.get(userid);
        else {
            final Member out = getChannel().getGuild().retrieveMember(UserSnowflake.fromId(userid)).complete();
            memberCache.put(userid, out);
            return out;
        }
    }

    public boolean hasAdminRole(final List<Role> roles) {
        return false; //Stub, admin role IDs don't exist yet
    }




}
