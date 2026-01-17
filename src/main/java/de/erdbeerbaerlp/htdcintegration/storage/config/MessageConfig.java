package de.erdbeerbaerlp.htdcintegration.storage.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import net.dv8tion.jda.api.entities.Activity;

public class MessageConfig {

    // 1. Codec definition for serialization/deserialization
    public static final BuilderCodec<MessageConfig> CODEC =
            BuilderCodec.builder(MessageConfig.class, MessageConfig::new)

                    .append(new KeyedCodec<>("DiscordMessage", Codec.STRING),
                            (c, v, e) -> c.discordMessage = v,
                            (c, e) -> c.discordMessage)
                    .add()

                    .append(new KeyedCodec<>("IngameMessage", Codec.STRING),
                            (c, v, e) -> c.ingameMessage = v,
                            (c, e) -> c.ingameMessage)
                    .add()

                    .append(new KeyedCodec<>("BotActivity", Codec.STRING),
                            (c, v, e) -> c.botActivity = v,
                            (c, e) -> c.botActivity)
                    .add()

                    .append(new KeyedCodec<>("BotActivityType", Codec.STRING),
                            (c, v, e) -> {
                                try {
                                    c.botActivityType = Activity.ActivityType.valueOf(v);
                                } catch (IllegalArgumentException ex) {
                                    c.botActivityType = Activity.ActivityType.CUSTOM_STATUS;
                                }
                            },
                            (c, e) -> c.botActivityType.name())
                    .add()

                    .append(new KeyedCodec<>("ServerStarted", Codec.STRING),
                            (c, v, e) -> c.serverStart = v,
                            (c, e) -> c.serverStart)
                    .add()

                    .append(new KeyedCodec<>("ServerStopped", Codec.STRING),
                            (c, v, e) -> c.serverStop = v,
                            (c, e) -> c.serverStop)
                    .add()

                    .append(new KeyedCodec<>("PlayerJoined", Codec.STRING),
                            (c, v, e) -> c.playerJoin = v,
                            (c, e) -> c.playerJoin)
                    .add()

                    .append(new KeyedCodec<>("PlayerLeft", Codec.STRING),
                            (c, v, e) -> c.playerLeave = v,
                            (c, e) -> c.playerLeave)
                    .add()

                    .append(new KeyedCodec<>("PlayerNotFound", Codec.STRING),
                            (c, v, e) -> c.playerNotFound = v,
                            (c, e) -> c.playerNotFound)
                    .add()

                    .append(new KeyedCodec<>("CmdUptimeDescription", Codec.STRING),
                            (c, v, e) -> c.cmdUptimeDescription = v,
                            (c, e) -> c.cmdUptimeDescription)
                    .add()

                    .append(new KeyedCodec<>("CmdUptimeMessage", Codec.STRING),
                            (c, v, e) -> c.cmdUptimeMessage = v,
                            (c, e) -> c.cmdUptimeMessage)
                    .add()

                    .append(new KeyedCodec<>("LinkSuccessful", Codec.STRING),
                            (c, v, e) -> c.linkSuccessful = v,
                            (c, e) -> c.linkSuccessful)
                    .add()

                    .append(new KeyedCodec<>("LinkFailed", Codec.STRING),
                            (c, v, e) -> c.linkFailed = v,
                            (c, e) -> c.linkFailed)
                    .add()

                    .append(new KeyedCodec<>("AlreadyLinked", Codec.STRING),
                            (c, v, e) -> c.alreadyLinked = v,
                            (c, e) -> c.alreadyLinked)
                    .add()

                    .append(new KeyedCodec<>("InvalidLinkNumber", Codec.STRING),
                            (c, v, e) -> c.invalidLinkNumber = v,
                            (c, e) -> c.invalidLinkNumber)
                    .add()

                    .append(new KeyedCodec<>("LinkNumberNAN", Codec.STRING),
                            (c, v, e) -> c.linkNumberNAN = v,
                            (c, e) -> c.linkNumberNAN)
                    .add()

                    .append(new KeyedCodec<>("NotWhitelistedCode", Codec.STRING),
                            (c, v, e) -> c.notWhitelistedCode = v,
                            (c, e) -> c.notWhitelistedCode)
                    .add()

                    .append(new KeyedCodec<>("NotWhitelistedRole", Codec.STRING),
                            (c, v, e) -> c.notWhitelistedRole = v,
                            (c, e) -> c.notWhitelistedRole)
                    .add()

                    .append(new KeyedCodec<>("LinkRequiredRole", Codec.STRING),
                            (c, v, e) -> c.link_requiredRole = v,
                            (c, e) -> c.link_requiredRole)
                    .add()

                    .append(new KeyedCodec<>("LinkSuccessfulIngame", Codec.STRING),
                            (c, v, e) -> c.linkSuccessfulIngame = v,
                            (c, e) -> c.linkSuccessfulIngame)
                    .add()

                    .append(new KeyedCodec<>("LinkMsgIngame", Codec.STRING),
                            (c, v, e) -> c.linkMsgIngame = v,
                            (c, e) -> c.linkMsgIngame)
                    .add()

                    .append(new KeyedCodec<>("CmdLinkWhitelistDescription", Codec.STRING),
                            (c, v, e) -> c.cmdLinkWhitelistDescription = v,
                            (c, e) -> c.cmdLinkWhitelistDescription)
                    .add()

                    .append(new KeyedCodec<>("CmdLinkDescription", Codec.STRING),
                            (c, v, e) -> c.cmdLinkDescription = v,
                            (c, e) -> c.cmdLinkDescription)
                    .add()

                    .append(new KeyedCodec<>("SubcommandDisabled", Codec.STRING),
                            (c, v, e) -> c.subcommandDisabled = v,
                            (c, e) -> c.subcommandDisabled)
                    .add()

                    .append(new KeyedCodec<>("CmdDiscordMessage", Codec.STRING),
                            (c, v, e) -> c.cmdDiscordMessage = v,
                            (c, e) -> c.cmdDiscordMessage)
                    .add()

                    .append(new KeyedCodec<>("InviteURL", Codec.STRING),
                            (c, v, e) -> c.inviteURL = v,
                            (c, e) -> c.inviteURL)
                    .add()

                    .append(new KeyedCodec<>("CmdLinkIngameDescription", Codec.STRING),
                            (c, v, e) -> c.cmdLinkIngameDescription = v,
                            (c, e) -> c.cmdLinkIngameDescription)
                    .add()

                    .append(new KeyedCodec<>("NoPermission", Codec.STRING),
                            (c, v, e) -> c.noPermission = v,
                            (c, e) -> c.noPermission)
                    .add()

                    .build();


    // 2. Configuration variable with default value
    public String discordMessage = "%playername%: %message%";
    public String ingameMessage = "[Discord] %username%: %message%";
    public String botActivity = "Hytale (Players: %playercount%)";
    public Activity.ActivityType botActivityType = Activity.ActivityType.PLAYING;
    public String serverStart = "Server started!";
    public String serverStop = "Server stopped!";
    public String playerJoin = "%playername% joined the game!";
    public String playerLeave = "%playername% left the game!";


    public String playerNotFound = "%playername% not found!";
    public String cmdUptimeDescription = "Shows server uptime";
    public String cmdUptimeMessage = "The server is running for %uptime%";

    public String linkSuccessful = "Your account is now linked!";


    public String linkFailed = "Account link failed";


    public String alreadyLinked = "Your account is already linked!";

    public String invalidLinkNumber = "Invalid link number!";


    public String linkNumberNAN = "This is not a number!";


    public String notWhitelistedCode = "You are not whitelisted.\nJoin the discord server for more information\nhttps://discord.gg/someserver\nYour Whitelist-Code is: %code%";

    public String notWhitelistedRole = "You are whitelisted, but you need a role to join.\nSee the discord server for more information";

    public String link_requiredRole = "You need to have an role to use this";

    public String linkSuccessfulIngame = "Your account is now linked with discord-user %username%";

    public String linkMsgIngame = "Send this command to the server channel to link your account: /link %num%\nThis number will expire after 10 minutes";
    public String cmdLinkWhitelistDescription = "Whitelists you on the server by linking with Discord";
    public String cmdLinkDescription = "Links your Discord account with your Hytale account";
    public String subcommandDisabled = "This subcommand is disabled!";

    public String cmdDiscordMessage = "Join our discord! https://discord.gg/myserver";
    public String inviteURL = "https://discord.gg/myserver";
    public String cmdLinkIngameDescription = "Link your discord account with your Hytale account";
    public String noPermission = "You don't have permission to use this command!";


    // 3. Constructor
    public MessageConfig() {
    }
}