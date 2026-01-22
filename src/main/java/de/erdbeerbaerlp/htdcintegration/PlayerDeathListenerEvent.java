package de.erdbeerbaerlp.htdcintegration;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.erdbeerbaerlp.htdcintegration.util.DiscordMessage;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.text.MessageFormat;

public class PlayerDeathListenerEvent extends DeathSystems.OnDeathSystem {

    public void onComponentAdded(@Nonnull Ref ref, @Nonnull DeathComponent component, @Nonnull Store store, @Nonnull CommandBuffer commandBuffer) {
        Player player = (Player)store.getComponent(ref, Player.getComponentType());

        assert player != null;
        Message msg = component.getDeathMessage();
        if(msg == null)
            msg = Message.raw(MessageFormat.format("Player {0} has died.", player.getDisplayName()));

        DiscordPlugin.getInstance().discord.sendSysMessage(new DiscordMessage(msg.getAnsiMessage().replace("You were", player.getDisplayName()+" was")));
    }

    @Nullable
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }
}
