package de.erdbeerbaerlp.htdcintegration;

import com.hypixel.hytale.server.core.universe.Universe;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.jetbrains.annotations.NotNull;

import java.util.TimerTask;

public class StatusUpdateTask extends TimerTask {

    public StatusUpdateTask() {
    }

    @Override
    public void run() {
        final JDA jda = DiscordPlugin.getInstance().discord.getJda();
            if (jda != null) {
                final String game = getString();
                switch (DiscordPlugin.getInstance().messages.get().botActivityType) {
                    case LISTENING:
                        jda.getPresence().setActivity(Activity.listening(game));
                        break;
                    case PLAYING:
                        jda.getPresence().setActivity(Activity.playing(game));
                        break;
                    case WATCHING:
                        jda.getPresence().setActivity(Activity.watching(game));
                        break;
                    case COMPETING:
                        jda.getPresence().setActivity(Activity.competing(game));
                        break;
                    case STREAMING:
                        jda.getPresence().setActivity(Activity.streaming(game, "https://www.youtube.com/watch?v=dQw4w9WgXcQ")); //URL is required to show up as "Streaming"
                        break;
                    case CUSTOM_STATUS:
                        jda.getPresence().setActivity(Activity.customStatus(game));
                        break;
                }
            }
    }

    @NotNull
    private String getString() {
        final int playerCount = Universe.get().getPlayerCount();

        return DiscordPlugin.getInstance().messages.get().botActivity.replace("%playercount%", playerCount+"");
    }
}