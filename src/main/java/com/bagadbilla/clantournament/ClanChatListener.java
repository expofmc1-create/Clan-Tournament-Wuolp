package com.bagadbilla.clantournament;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import java.util.UUID;

public class ClanChatListener implements Listener {

    private final ClanTournament plugin;
    public ClanChatListener(ClanTournament plugin) { this.plugin = plugin; }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClanChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // If they don't have the toggle ON, let them chat normally
        if (!plugin.getClanChatToggled().contains(uuid)) return;

        Clan clan = plugin.getClanByPlayer(uuid);
        if (clan == null) {
            plugin.getClanChatToggled().remove(uuid);
            return;
        }

        // Cancel the global message
        event.setCancelled(true);

        // Format the private message
        String format = "§8[§bClan Chat§8] §f" + player.getName() + ": §b" + event.getMessage();

        // Send only to online clan members
        for (UUID memberId : clan.getMembers()) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null && member.isOnline()) {
                member.sendMessage(format);
            }
        }
        
        // Log it to console so you (the admin) can still see what's happening
        Bukkit.getConsoleSender().sendMessage("§7[ClanLog] " + clan.getName() + " - " + player.getName() + ": " + event.getMessage());
    }
}
