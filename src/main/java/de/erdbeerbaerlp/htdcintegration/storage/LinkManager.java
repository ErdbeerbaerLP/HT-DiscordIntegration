package de.erdbeerbaerlp.htdcintegration.storage;

import de.erdbeerbaerlp.htdcintegration.DiscordPlugin;
import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("unused")
public class LinkManager {

    private static ArrayList<PlayerLink> linkCache = new ArrayList<>();

    /**
     * Player UUID cache for players not on global linking API
     */
    private static final ArrayList<String> nonexistentPlayerUUIDs = new ArrayList<>();

    /**
     * Pending /discord link requests
     */
    public static final HashMap<Integer, KeyValue<Instant, UUID>> pendingLinks = new HashMap<>();

    public static void load() {
        if (DiscordPlugin.getInstance().linkconf.get().enableLinking) {
            linkCache = new ArrayList<>(Arrays.asList(JSONInterface.getAllLinks()));
            DiscordPlugin.getInstance().getLogger().atFinest().log("LinkManager load | cache: " + linkCache);
        }
    }

    public static void save() {
        if (DiscordPlugin.getInstance().linkconf.get().enableLinking)
            linkCache.forEach(JSONInterface::addLink);
    }

    public static ArrayList<PlayerLink> getAllLinks() {
        return linkCache;
    }

    /**
     * Unlinks a player from the local database
     *
     * @param discordID discord ID of the player to unlink
     * @return true if the unlink process was successful
     */
    public static boolean unlinkPlayer(String discordID) {
        if (!DiscordPlugin.getInstance().linkconf.get().enableLinking) return false;
        linkCache.removeIf(link -> link.discordID.equals(discordID));
        JSONInterface.removeLink(discordID);
        return true;
    }

    /**
     * Dummy UUID for unknown players or server messages
     */
    public static final UUID dummyUUID = new UUID(0L, 0L);

    /**
     * Links a discord user ID with a player's {@link UUID}
     *
     * @param discordID Discord ID to link
     * @param player    {@link UUID} to link
     * @return true, if linking was successful
     * @throws IllegalArgumentException if one side is already linked
     */
    @SuppressWarnings({"ConstantConditions", "DuplicatedCode"})
    public static boolean linkPlayer(String discordID, UUID player) throws IllegalArgumentException {
        if (player.equals(dummyUUID))
            return false;
        if (isDiscordUserLinked(discordID) || isPlayerLinked(player))
            throw new IllegalArgumentException("One link side already exists");

        DiscordPlugin.getInstance().getLogger().atInfo().log("LinkManager linkPlayer | discordID:" + discordID + ", player:" + player);
        return addLink(new PlayerLink(discordID, player.toString()));
    }


    /**
     * Checks if a player has linked their minecraft account with discord
     *
     * @param player {@link UUID} of the player to check
     * @return The player's link status
     */
    public static boolean isPlayerLinked(UUID player) {
        if (!DiscordPlugin.getInstance().linkconf.get().enableLinking) return false;
        for (final PlayerLink o : getAllLinks()) {
            if (o.player != null && !o.player.isEmpty() && o.player.equals(player.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a user has linked their discord account with minecraft java
     *
     * @param discordID The discord ID to check
     * @return The user's link status
     */
    public static boolean isDiscordUserLinked(String discordID) {
        if (!DiscordPlugin.getInstance().linkconf.get().enableLinking) return false;
        for (final PlayerLink o : getAllLinks()) {
            if (!o.discordID.isEmpty() && o.discordID.equals(discordID)) {
                return !(o.player == null || o.player.isEmpty());
            }
        }
        return false;
    }

    /**
     * Adds a player link to the local database
     *
     * @param l PlayerLink object to save to the database
     * @return true if successful
     */
    public static boolean addLink(final PlayerLink l) {
        if (!DiscordPlugin.getInstance().linkconf.get().enableLinking) return false;
        if (l.discordID == null) return false;
        PlayerLink tmp = null;
        for (final PlayerLink link : getAllLinks()) {
            if (link.discordID.equals(l.discordID) || (link.player != null && !link.player.isEmpty() && link.player.equals(l.player)))
                tmp = link;
        }
        if (tmp != null) linkCache.remove(tmp);
        DiscordPlugin.getInstance().getLogger().atFinest().log("LinkManager addLink | tmp:" + tmp + ", l:" + l + ", linkCache:" + linkCache);
        linkCache.add(l);
        return true;
    }

    /**
     * Gets the player link from one of the given parameters, if it exists
     *
     * @param discordID Discord User ID of the user
     * @param uuid      Minecraft UUID of the player
     * @return PlayerLink object
     */
    public static PlayerLink getLink(final String discordID, final UUID uuid) {
        if (!DiscordPlugin.getInstance().linkconf.get().enableLinking) return null;
        if (uuid == null && discordID == null) return null;
        if (discordID != null) {
            for (final PlayerLink l : getAllLinks()) {
                if (l.discordID.equals(discordID))
                    return l;
            }
        }
        if (uuid != null) {
            for (final PlayerLink l : getAllLinks()) {
                if (l.player.equals(uuid.toString()))
                    return l;
            }

        }
        return null;
    }


    /**
     * Generates or gets a unique link number for a player
     *
     * @param uniqueID The player's {@link UUID} to generate the number for
     * @return Link number for this player
     */
    public static int genLinkNumber(UUID uniqueID) {
        return genLinkNumber(uniqueID, pendingLinks);
    }


    private static int genLinkNumber(UUID uniqueID, HashMap<Integer, KeyValue<Instant, UUID>> targetMap) {
        final AtomicInteger r = new AtomicInteger(-1);
        targetMap.forEach((k, v) -> {
            if (v.getValue().equals(uniqueID))
                r.set(k);
        });
        if (r.get() != -1) return r.get();
        do {
            r.set(new Random().nextInt(99999));
        } while (targetMap.containsKey(r.get()));
        targetMap.put(r.get(), new DefaultKeyValue<>(Instant.now(), uniqueID));
        return r.get();
    }
}