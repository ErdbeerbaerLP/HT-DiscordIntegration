package de.erdbeerbaerlp.htdcintegration.storage.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class LinkingConfig {
    // 1. Codec definition for serialization/deserialization
    public static final BuilderCodec<LinkingConfig> CODEC =
            BuilderCodec.builder(LinkingConfig.class, LinkingConfig::new)

                    .append(new KeyedCodec<Boolean>("EnableLinking", Codec.BOOLEAN),
                            (exConfig, val, extraInfo) -> exConfig.enableLinking = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.enableLinking)                     // Getter
                    .add()
                    .append(new KeyedCodec<Boolean>("WhitelistMode", Codec.BOOLEAN),
                            (exConfig, val, extraInfo) -> exConfig.linkingWhitelistMode = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.linkingWhitelistMode)                     // Getter
                    .add()
                    .append(new KeyedCodec<String[]>("RequiredRoles", Codec.STRING_ARRAY),
                            (exConfig, val, extraInfo) -> exConfig.requiredRoles = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.requiredRoles)                     // Getter
                    .add()
                    .build();

    // 2. Configuration variable with default value
    public boolean enableLinking = false;
    public boolean linkingWhitelistMode = true;
    public String[] requiredRoles = new String[0];

    // 3. Constructor
    public LinkingConfig() {
    }
}
