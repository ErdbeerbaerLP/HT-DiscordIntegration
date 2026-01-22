package de.erdbeerbaerlp.htdcintegration.storage.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class DiscordConfig {

    // 1. Codec definition for serialization/deserialization
    public static final BuilderCodec<DiscordConfig> CODEC =
            BuilderCodec.builder(DiscordConfig.class, DiscordConfig::new)
                    .append(new KeyedCodec<Long>("ChannelID", Codec.LONG),
                            (exConfig, val, extraInfo) -> exConfig.channelID = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.channelID)                     // Getter

                    .add()
                    .append(new KeyedCodec<String>("BotToken", Codec.STRING),
                            (exConfig, val, extraInfo) -> exConfig.botToken = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.botToken)                     // Getter
                    .add()
                    .append(new KeyedCodec<String[]>("VcMessageChannelWhitelist", Codec.STRING_ARRAY ),
                            (exConfig, val, extraInfo) -> exConfig.vcMessageWhitelist = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.vcMessageWhitelist)                     // Getter
                    .add()
                    .build();

    // 2. Configuration variable with default value
    public Long channelID = 0L;
    public String botToken = "";
    public String[] vcMessageWhitelist = new String[0];



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

    public String[] getVcMessageWhitelist() {
        return vcMessageWhitelist;
    }
}