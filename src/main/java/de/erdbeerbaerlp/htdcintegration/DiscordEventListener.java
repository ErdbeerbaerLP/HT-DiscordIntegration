package de.erdbeerbaerlp.htdcintegration;

import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.emoji.CustomEmoji;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import org.jetbrains.annotations.NotNull;

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
        if (event instanceof MessageReceivedEvent ev) {
            if (!ev.getAuthor().getId().equals(ev.getJDA().getSelfUser().getId())) {
                if (ev.getChannel().getIdLong() == DiscordPlugin.getInstance().config.get().getChannelID()) {
                    String msg = ev.getMessage().getContentDisplay();
                    msg = formatEmoteMessage(ev.getMessage().getMentions().getCustomEmojis(), msg);
                    for (final PlayerRef p : Universe.get().getPlayers()) {
                        p.sendMessage(com.hypixel.hytale.server.core.Message.raw(DiscordPlugin.getInstance().messages.get().ingameMessage.replace("%username%",ev.getAuthor().getEffectiveName()).replace("%message%", msg)));
                    }
                }

            }
        }

    }
}
