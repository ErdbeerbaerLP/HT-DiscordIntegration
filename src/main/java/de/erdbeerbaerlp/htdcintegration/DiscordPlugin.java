package de.erdbeerbaerlp.htdcintegration;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.Config;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Main plugin class.
 * 
 * TODO: Implement your plugin logic here.
 * 
 * @author ErdbeerbaerLP
 * @version 1.0.0
 */
public class DiscordPlugin extends JavaPlugin {

    private static DiscordPlugin instance;
    final Config<DiscordConfig> config;

    public Discord discord;
    private DiscordConfig conf;

    /**
     * Constructor - Called when plugin is loaded.
     */
    public DiscordPlugin(JavaPluginInit i) {
        super(i);

        getLogger().atInfo().log("Loading...");
        instance = this;
        this.config = this.withConfig("DiscordIntegration", DiscordConfig.CODEC);


    }

    @Override
    protected void start() {
        super.start();
        discord.sendMessage("Server Started!");
    }

    @Override
    protected void setup() {
        super.setup();
        getLogger().atInfo().log("Plugin enabled!");

        this.config.save();

        if(config.get().botToken.isBlank()){
            getLogger().atSevere().log("Discord bot token is blank!");
            return;
        }

        try {
            discord = new Discord();

        } catch (InterruptedException e) {
            getLogger().atSevere().log(e.getMessage());
            e.printStackTrace();
            return;
        }
        getEventRegistry().registerGlobal(PlayerChatEvent.class, this::onPlayerChat);
        getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
        getEventRegistry().registerGlobal(PlayerConnectEvent.class, this::onPlayerConnect);
    }

    private void onPlayerConnect(PlayerConnectEvent event) {
        final PlayerRef player = event.getPlayerRef();
        discord.sendMessage(player.getUsername() + " joined the game!");
    }

    private void onPlayerDisconnect(PlayerDisconnectEvent event) {
        final PlayerRef player = event.getPlayerRef();
        discord.sendMessage(player.getUsername() + " left the game!");

    }
    public void onPlayerChat(PlayerChatEvent event) {
        final PlayerRef player = event.getSender();
        final String message = event.getContent();

        discord.sendMessage(player.getUsername() + ": "+message);
    }

    @Override
    protected void shutdown() {
        getLogger().atInfo().log("Plugin disabled!");

        discord.sendMessage("Server Stopped!");
        try {
            discord.getJda().awaitShutdown(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        discord = null;
    }

    /**
     * Get plugin instance.
     */
    public static DiscordPlugin getInstance() {
        return instance;
    }
}
