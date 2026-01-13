package de.erdbeerbaerlp.htdcintegration;

import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.util.Collections;

public class Discord {
    private JDA jda;

    public Discord() throws InterruptedException {
        jda = JDABuilder.createDefault(DiscordPlugin.getInstance().config.get().getBotToken()).enableIntents(GatewayIntent.MESSAGE_CONTENT).setActivity(Activity.playing("Hytale")).build().awaitReady();
        jda.addEventListener(new DiscordEventListener());
    }

    public JDA getJda() {
        return jda;
    }

    public void sendMessage(String message){
        jda.getChannelById(MessageChannel.class,DiscordPlugin.getInstance().config.get().channelID)
                .sendMessage(message).setAllowedMentions(Collections.singleton(Message.MentionType.USER)).queue();
    }
}
