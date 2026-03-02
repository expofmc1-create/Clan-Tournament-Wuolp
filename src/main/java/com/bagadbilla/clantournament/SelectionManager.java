package com.bagadbilla.clantournament;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class SelectionManager {

    private final ClanTournament plugin;

    public SelectionManager(ClanTournament plugin) {
        this.plugin = plugin;
    }

    public void setPosition(Player player, int pointNumber) {
        Clan clan = getPlayerClan(player);

        if (clan == null) {
            player.sendMessage("§cYou are not in a clan!");
            return;
        }

        if (!clan.getLeader().equals(player.getUniqueId())) {
            player.sendMessage("§cOnly the leader can set the claim area!");
            return;
        }

        Location loc = player.getLocation();
        if (pointNumber == 1) {
            clan.setPos1(loc);
            player.sendMessage("§aPosition 1 set to: " + loc.getBlockX() + ", " + loc.getBlockZ());
        } else {
            clan.setPos2(loc);
            player.sendMessage("§aPosition 2 set to: " + loc.getBlockX() + ", " + loc.getBlockZ());
        }

        // Now we check the 100-block rule
        validateAndSave(player, clan);
    }

    private void validateAndSave(Player player, Clan clan) {
        if (clan.getPos1() != null && clan.getPos2() != null) {
            int xDist = Math.abs(clan.getPos1().getBlockX() - clan.getPos2().getBlockX());
            int zDist = Math.abs(clan.getPos1().getBlockZ() - clan.getPos2().getBlockZ());

            if (xDist > 100 || zDist > 100) {
                player.sendMessage("§c§lWARNING: §7Your selection is " + xDist + "x" + zDist);
                player.sendMessage("§cThe limit is 100x100. Protection will NOT work until resized!");
            } else {
                player.sendMessage("§b§lSUCCESS: §7Area is " + xDist + "x" + zDist + ". Data saved to VPS.");
                plugin.saveClansToDisk();
            }
        }
    }

    private Clan getPlayerClan(Player player) {
        for (Clan clan : plugin.getClans().values()) {
            if (clan.getMembers().contains(player.getUniqueId())) return clan;
        }
        return null;
    }
}
