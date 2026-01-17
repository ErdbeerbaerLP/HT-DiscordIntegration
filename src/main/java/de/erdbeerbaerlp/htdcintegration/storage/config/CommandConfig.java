package de.erdbeerbaerlp.htdcintegration.storage.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class CommandConfig {
    // 1. Codec definition for serialization/deserialization
    public static final BuilderCodec<CommandConfig> CODEC =
            BuilderCodec.builder(CommandConfig.class, CommandConfig::new)
                    .append(new KeyedCodec<Boolean>("EnableUptimeCommand", Codec.BOOLEAN),
                            (exConfig, val, extraInfo) -> exConfig.enableUptime = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.enableUptime)                     // Getter
                    .add()
                    .append(new KeyedCodec<Boolean>("HideUptimeResponse", Codec.BOOLEAN),
                            (exConfig, val, extraInfo) -> exConfig.hideUptimeCommand = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.hideUptimeCommand)                     // Getter
                    .add()
                    .append(new KeyedCodec<String>("UptimeFormat", Codec.STRING),
                            (exConfig, val, extraInfo) -> exConfig.uptimeFormat = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.uptimeFormat)                     // Getter
                    .add()
                    .build();

    // 2. Configuration variable with default value
    public boolean enableUptime = true;
    public boolean hideUptimeCommand = false;
    public String uptimeFormat = "dd 'days' HH 'hours' mm 'minutes'";

    // 3. Constructor
    public CommandConfig() {
    }
}
