package de.erdbeerbaerlp.htdcintegration;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import net.dv8tion.jda.api.entities.Activity;

public class MessageConfig {

    // 1. Codec definition for serialization/deserialization
    public static final BuilderCodec<MessageConfig> CODEC =
            BuilderCodec.builder(MessageConfig.class, MessageConfig::new)
                    .append(new KeyedCodec<String>("DiscordMessage", Codec.STRING),
                            (exConfig, val, extraInfo) -> exConfig.discordMessage = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.discordMessage)                     // Getter

                    .add()
                    .append(new KeyedCodec<String>("IngameMessage", Codec.STRING),
                            (exConfig, val, extraInfo) -> exConfig.ingameMessage = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.ingameMessage)                     // Getter

                    .add()
                    .append(new KeyedCodec<String>("BotActivity", Codec.STRING),
                            (exConfig, val, extraInfo) -> exConfig.botActivity = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.botActivity)                     // Getter
                    .add()
                    .append(new KeyedCodec<String>("BotActivityType", Codec.STRING),
                            (exConfig, val, extraInfo) -> {
                                try {
                                    exConfig.botActivityType = Activity.ActivityType.valueOf(val);
                                } catch (IllegalArgumentException e) {
                                    exConfig.botActivityType = Activity.ActivityType.CUSTOM_STATUS;
                                }
                            },  // Setter
                            (exConfig, extraInfo) -> exConfig.botActivityType.name())                     // Getter
                    .add()
                    .append(new KeyedCodec<String>("ServerStarted", Codec.STRING),
                            (exConfig, val, extraInfo) -> exConfig.serverStart = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.serverStart)                     // Getter
                    .add()
                    .append(new KeyedCodec<String>("ServerStopped", Codec.STRING),
                            (exConfig, val, extraInfo) -> exConfig.serverStop = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.serverStop)                     // Getter
                    .add()
                    .append(new KeyedCodec<String>("PlayerJoined", Codec.STRING),
                            (exConfig, val, extraInfo) -> exConfig.playerJoin = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.playerJoin)                     // Getter
                    .add()
                    .append(new KeyedCodec<String>("PlayerLeft", Codec.STRING),
                            (exConfig, val, extraInfo) -> exConfig.playerLeave = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.playerLeave)                     // Getter
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

    // 3. Constructor
    public MessageConfig() {
    }
}