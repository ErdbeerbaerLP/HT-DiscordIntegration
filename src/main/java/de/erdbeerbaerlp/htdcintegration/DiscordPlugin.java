package de.erdbeerbaerlp.htdcintegration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.event.events.player.PlayerChatEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerConnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerDisconnectEvent;
import com.hypixel.hytale.server.core.event.events.player.PlayerSetupConnectEvent;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.Config;
import de.erdbeerbaerlp.htdcintegration.hytaleCommands.DiscordCommand;
import de.erdbeerbaerlp.htdcintegration.hytaleCommands.HTDiscordSubCommandRegistry;
import de.erdbeerbaerlp.htdcintegration.storage.CommandRegistry;
import de.erdbeerbaerlp.htdcintegration.storage.JSONInterface;
import de.erdbeerbaerlp.htdcintegration.storage.LinkManager;
import de.erdbeerbaerlp.htdcintegration.storage.config.CommandConfig;
import de.erdbeerbaerlp.htdcintegration.storage.config.DiscordConfig;
import de.erdbeerbaerlp.htdcintegration.storage.config.LinkingConfig;
import de.erdbeerbaerlp.htdcintegration.storage.config.MessageConfig;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.io.File;
import java.util.Date;
import java.util.Timer;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
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


    public static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public long started = -1;
    private static DiscordPlugin instance;
    final Config<DiscordConfig> config;
    public final Config<MessageConfig> messages;
    public final Config<LinkingConfig> linkconf;
    public final Config<CommandConfig> cmdconf;

    public Discord discord;
    private StatusUpdateTask statusUpdater;
    public static final File baseDir = new File("mods","DiscordIntegration_DiscordIntegration") ;


    /**
     * Constructor - Called when plugin is loaded.
     */
    public DiscordPlugin(JavaPluginInit i) {
        super(i);

        getLogger().atInfo().log("Loading...");
        instance = this;
        this.config = this.withConfig("DiscordIntegration", DiscordConfig.CODEC);
        this.messages = this.withConfig("Messages", MessageConfig.CODEC);
        this.linkconf = this.withConfig("Linking", LinkingConfig.CODEC);
        this.cmdconf = this.withConfig("Commands", CommandConfig.CODEC);

        JSONInterface.initialize();


    }

    @Override
    protected void start() {
        super.start();


        this.getCommandRegistry().registerCommand(new DiscordCommand());
        CompletableFuture.runAsync(()->{
            try {
                CommandRegistry.updateSlashCommands();
            } catch (IllegalStateException e) {
                getLogger().atSevere().log(e.getMessage());
            }
        });

        started = new Date().getTime();
        discord.sendMessage(messages.get().serverStart);
    }

    private static Timer timer = new Timer();
    @Override
    protected void setup() {
        super.setup();
        getLogger().atInfo().log("Plugin enabled!");

        this.config.save();
        this.messages.save();
        this.linkconf.save();
        this.cmdconf.save();

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
        LinkManager.load();
        getEventRegistry().registerGlobal(PlayerChatEvent.class, this::onPlayerChat);
        getEventRegistry().registerGlobal(PlayerDisconnectEvent.class, this::onPlayerDisconnect);
        getEventRegistry().registerGlobal(PlayerConnectEvent.class, this::onPlayerConnect);
        getEventRegistry().registerGlobal(PlayerSetupConnectEvent.class, this::onPlayerSetupConnect);

        if (statusUpdater == null) statusUpdater = new StatusUpdateTask();

        timer.scheduleAtFixedRate(statusUpdater, 0, TimeUnit.SECONDS.toMillis(10));




        HTDiscordSubCommandRegistry.registerDefaultCommands();
        CommandRegistry.registerDefaultCommands();


    }
    public boolean canPlayerJoin(UUID uuid) {
        if (!DiscordPlugin.getInstance().linkconf.get().linkingWhitelistMode) return true;
        if (LinkManager.isPlayerLinked(uuid)) {
            if (DiscordPlugin.getInstance().linkconf.get().requiredRoles.length != 0) {
                final Member mem = discord.getMemberById(LinkManager.getLink(null, uuid).discordID);
                if (mem == null) return false;
                final Guild g = discord.getChannel().getGuild();
                for (String requiredRole : DiscordPlugin.getInstance().linkconf.get().requiredRoles) {
                    final Role role = g.getRoleById(requiredRole);
                    if (role == null) continue;
                    if (mem.getRoles().contains(role)) {
                        return true;
                    }
                }
                return false;
            } else return true;
        }
        return false;
    }
    private void onPlayerSetupConnect(PlayerSetupConnectEvent event) {
        if(linkconf.get().enableLinking && linkconf.get().linkingWhitelistMode){
            try {
                if (!LinkManager.isPlayerLinked(event.getUuid())) {
                    event.setReason(DiscordPlugin.getInstance().messages.get().notWhitelistedCode.replace("%code%", "" + (LinkManager.genLinkNumber(event.getUuid()))));
                    event.setCancelled(true);
                } else if (!canPlayerJoin(event.getUuid())) {
                    event.setReason(DiscordPlugin.getInstance().messages.get().notWhitelistedRole);
                    event.setCancelled(true);
                }
            } catch (IllegalStateException e) {
                event.setReason("An error occured\nPlease check Server Log for more information\n\n" + e);
                event.setCancelled(true);
                e.printStackTrace();
            }
        }
    }

    private void onPlayerConnect(PlayerConnectEvent event) {
        final PlayerRef player = event.getPlayerRef();

        discord.sendMessage(messages.get().playerJoin.replace("%playername%",player.getUsername()));
    }

    private void onPlayerDisconnect(PlayerDisconnectEvent event) {
        final PlayerRef player = event.getPlayerRef();
        discord.sendMessage(messages.get().playerLeave.replace("%playername%",player.getUsername()));

    }
    public void onPlayerChat(PlayerChatEvent event) {
        final PlayerRef player = event.getSender();
        final String message = event.getContent();

        discord.sendMessage(messages.get().discordMessage.replace("%playername%",player.getUsername()).replace("%message%",message));
    }

    @Override
    protected void shutdown() {
        getLogger().atInfo().log("Plugin disabled!");

        timer.cancel();
        timer.purge();
        discord.sendMessage(messages.get().serverStop);
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
