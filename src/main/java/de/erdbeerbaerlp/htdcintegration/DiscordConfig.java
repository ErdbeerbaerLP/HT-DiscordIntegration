package de.erdbeerbaerlp.htdcintegration;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class DiscordConfig {

    // 1. Codec definition for serialization/deserialization
    public static final BuilderCodec<DiscordConfig> CODEC =
            BuilderCodec.builder(DiscordConfig.class, DiscordConfig::new)
                    .append(new KeyedCodec<Long>("ChannelID", Codec.LONG),
                            (exConfig, aDouble, extraInfo) -> exConfig.channelID = aDouble,  // Setter
                            (exConfig, extraInfo) -> exConfig.channelID)                     // Getter

                    .add()
                    .append(new KeyedCodec<String>("BotToken", Codec.STRING),
                            (exConfig, aDouble, extraInfo) -> exConfig.botToken = aDouble,  // Setter
                            (exConfig, extraInfo) -> exConfig.botToken)                     // Getter
                    .add()
                    .build();

    // 2. Configuration variable with default value
    public Long channelID = 0L;
    public String botToken = "";

    // 3. Constructor
    public DiscordConfig() {
    }

    // 4. Getter method (optional but recommended)

    public Long getChannelID() {
        return channelID;
    }

    public String getBotToken() {
        return botToken;
    }
}