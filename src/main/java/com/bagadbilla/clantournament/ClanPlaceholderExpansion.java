package com.bagadbilla.clantournament;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class ClanPlaceholderExpansion extends PlaceholderExpansion {

    private final ClanTournament plugin;

    public ClanPlaceholderExpansion(ClanTournament plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "clantournament"; // This means your placeholders will start with %clantournament_...%
    }

    @Override
    public @NotNull String getAuthor() {
        return "Bagadbilla";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true; // Very important: keeps it registered during PAPI reloads
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        Clan clan = plugin.getClanByPlayer(player.getUniqueId());

        // Placeholder: %clantournament_name%
        if (params.equalsIgnoreCase("name")) {
            return (clan != null) ? clan.getName() : "No Clan";
        }

        // Placeholder: %clantournament_points%
        if (params.equalsIgnoreCase("points")) {
            return (clan != null) ? String.valueOf(clan.getPoints()) : "0";
        }

        return null; // Placeholder not found
    }
}
