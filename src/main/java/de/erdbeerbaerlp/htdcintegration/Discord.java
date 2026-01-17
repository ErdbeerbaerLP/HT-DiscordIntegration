package de.erdbeerbaerlp.htdcintegration;

import de.erdbeerbaerlp.htdcintegration.storage.LinkManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

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

    public void sendMessage(String message){
        getChannel()
                .sendMessage(message).setAllowedMentions(Collections.singleton(Message.MentionType.USER)).queue();
    }

    public GuildMessageChannel getChannel() {
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
