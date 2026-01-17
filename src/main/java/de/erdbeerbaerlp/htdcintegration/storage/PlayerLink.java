package de.erdbeerbaerlp.htdcintegration.storage;

public class PlayerLink {
    public String discordID = "";
    public String player = "";
    public PlayerLink(final String id, final String playerUUID) {
        this.player = playerUUID;
        this.discordID = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerLink that = (PlayerLink) o;

        if (!discordID.equals(that.discordID)) return false;
        return player.equals(that.player);
    }

    @Override
    public String toString() {
        return "PlayerLink{" +
                "discordID='" + discordID + '\'' +
                ", mcPlayerUUID='" + player + '\''+
                '}';
    }

    @Override
    public int hashCode() {
        int result = discordID.hashCode();
        result = 31 * result + player.hashCode();
        return result;
    }
}