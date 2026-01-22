package de.erdbeerbaerlp.htdcintegration.storage.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class WebhookConfig {
    // 1. Codec definition for serialization/deserialization
    public static final BuilderCodec<WebhookConfig> CODEC =
            BuilderCodec.builder(WebhookConfig.class, WebhookConfig::new)

                    .append(new KeyedCodec<Boolean>("EnableWebhookMode", Codec.BOOLEAN),
                            (exConfig, val, extraInfo) -> exConfig.enableWebhookMode = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.enableWebhookMode)                     // Getter
                    .add()
                    .append(new KeyedCodec<String>("WebhookAvatarURL", Codec.STRING),
                            (exConfig, val, extraInfo) -> exConfig.webhookAvatarURL = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.webhookAvatarURL)                     // Getter
                    .add()
                    .append(new KeyedCodec<String>("WebhookName", Codec.STRING),
                            (exConfig, val, extraInfo) -> exConfig.webhookName = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.webhookName)                     // Getter
                    .add()
                    .append(new KeyedCodec<String>("WebhookDisplayName", Codec.STRING),
                            (exConfig, val, extraInfo) -> exConfig.webhookDisplayName = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.webhookDisplayName)                     // Getter
                    .add()
                    .append(new KeyedCodec<String>("WebhookPlayerAvatarURL", Codec.STRING),
                            (exConfig, val, extraInfo) -> exConfig.webhookPlayerAvatarURL = val,  // Setter
                            (exConfig, extraInfo) -> exConfig.webhookPlayerAvatarURL)                     // Getter
                    .add()
                    .build();

    // 2. Configuration variable with default value
    public boolean enableWebhookMode = false;
    public String webhookAvatarURL = "https://hytale.com/static/images/logo-h.png";
    public String webhookName = "hytale_server";
    public String webhookDisplayName = "Hytale Server";
    public String webhookPlayerAvatarURL = "https://crafthead.net/hytale/avatar/%uuid%";

    // 3. Constructor
    public WebhookConfig() {
    }
}
