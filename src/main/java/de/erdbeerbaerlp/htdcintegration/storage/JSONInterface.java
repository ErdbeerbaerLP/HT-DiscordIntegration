package de.erdbeerbaerlp.htdcintegration.storage;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import de.erdbeerbaerlp.htdcintegration.DiscordPlugin;

import java.io.*;

import static de.erdbeerbaerlp.htdcintegration.DiscordPlugin.baseDir;
import static de.erdbeerbaerlp.htdcintegration.DiscordPlugin.gson;

/**
 * Default JSON database implementation
 */
public class JSONInterface{

    public static final File jsonFile = new File(baseDir, "LinkedPlayers.json");


    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void initialize() {
        try {
            if (!jsonFile.getParentFile().exists())
                jsonFile.getParentFile().mkdirs();
            if (!jsonFile.exists()) {
                jsonFile.createNewFile();
                try (Writer writer = new FileWriter(jsonFile)) {
                    gson.toJson(new JsonArray(), writer);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addLink(final PlayerLink link) {
        DiscordPlugin.getInstance().getLogger().atFinest().log("JSONInterface addLink | Saving "+link);
        final JsonArray json = getJson();
        DiscordPlugin.getInstance().getLogger().atFinest().log("JSONInterface addLink | json (old): " + json);
        for (final JsonElement e : json) {
            final PlayerLink o = gson.fromJson(e, PlayerLink.class);
            if (o.discordID.equals(link.discordID) || (o.player != null && !o.player.isEmpty() && o.player.equals(link.player))) {
                json.remove(e);
                DiscordPlugin.getInstance().getLogger().atFinest().log("JSONInterface addLink | Removing old link from json "+o.discordID);
                break;
            }
        }
        json.add(gson.toJsonTree(link).getAsJsonObject());
        DiscordPlugin.getInstance().getLogger().atFinest().log("JSONInterface addLink | json (new): " + json);
        try (Writer writer = new FileWriter(jsonFile)) {
            gson.toJson(json, writer);
            DiscordPlugin.getInstance().getLogger().atFinest().log("JSONInterface addLink | Written to File");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void removeLink(final String id) {
        for (final JsonElement e : getJson()) {
            final PlayerLink o = gson.fromJson(e, PlayerLink.class);
            if (o.discordID != null && o.discordID.equals(id)) {
                final JsonArray json = getJson();
                json.remove(e);
                try (Writer writer = new FileWriter(jsonFile)) {
                    gson.toJson(json, writer);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    return;
                }
            }
        }

    }

    public static PlayerLink[] getAllLinks() {
        return gson.fromJson(getJson(), PlayerLink[].class);
    }

    private static JsonArray getJson() {
        final FileReader is;
        try {
            is = new FileReader(jsonFile);
            final JsonArray a = JsonParser.parseReader(is).getAsJsonArray();
            is.close();
            return a;
        } catch (IOException e) {
            return new JsonArray();
        }
    }

}