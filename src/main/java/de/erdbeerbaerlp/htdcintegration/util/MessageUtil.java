package de.erdbeerbaerlp.htdcintegration.util;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.ibm.icu.text.Transliterator;
import de.erdbeerbaerlp.htdcintegration.DiscordPlugin;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.time.Duration;
import java.time.Instant;

public class MessageUtil {

    static final Transliterator tr =
            Transliterator.getInstance("Any-Latin; Latin-ASCII");

    /**
     * Gets the full server uptime formatted
     *
     * @return Uptime String
     */

    public static String getFullUptime() {
        if (DiscordPlugin.getInstance().started == 0) {
            return "?????";
        }
        final Duration duration = Duration.between(Instant.ofEpochMilli(DiscordPlugin.getInstance().started), Instant.now());
        return DurationFormatUtils.formatDuration(duration.toMillis(), DiscordPlugin.getInstance().cmdconf.get().uptimeFormat);
    }

    public static void broadcastMessageIngame(String msg) {
        broadcastMessageIngame(Message.raw(msg));
    }

    public static void broadcastMessageIngame(Message msg) {
        for (final PlayerRef p : Universe.get().getPlayers()) {
            p.sendMessage(msg);
        }
    }

    public static CharSequence normalize(String input) {
        return tr.transliterate(input);
    }



    /**
     * Escapes markdown from String
     *
     * @param in String with markdown
     * @return Input string without markdown
     */

    public static String escapeMarkdown(String in) {
        return in.replace("(?<!\\\\)[`*_|~]/g", "\\\\$0");
    }

    /**
     * Escapes markdown codeblocks from String
     *
     * @param in String with markdown codeblocks
     * @return Input string without markdown codeblocks
     */

    public static String escapeMarkdownCodeBlocks(String in) {
        return in.replace("(?<!\\\\)`/g", "\\\\$0");
    }
}
