package com.bagadbilla.clantournament;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class ProtectionListener implements Listener {
    private final ClanTournament plugin;

    public ProtectionListener(ClanTournament plugin) { this.plugin = plugin; }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (!plugin.getConfig().getBoolean("land-protection-enabled")) return;
        for (Clan clan : plugin.getClans().values()) {
            if (clan.isInside(e.getBlock().getLocation())) {
                if (!clan.getMembers().contains(e.getPlayer().getUniqueId())) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ChatColor.RED + "This land belongs to " + clan.getName());
                }
            }
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        if (!plugin.getConfig().getBoolean("land-protection-enabled")) return;
        for (Clan clan : plugin.getClans().values()) {
            if (clan.isInside(e.getBlock().getLocation())) {
                if (!clan.getMembers().contains(e.getPlayer().getUniqueId())) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage(ChatColor.RED + "You cannot build here!");
                }
            }
        }
    }
}
