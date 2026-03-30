package com.bagadbilla.clantournament;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.List;
import java.util.ArrayList;

public class LeaderboardGUI {

    private final ClanTournament plugin;
    public LeaderboardGUI(ClanTournament plugin) { this.plugin = plugin; }

    public void openLeaderboard(Player player) {
        Inventory gui = Bukkit.createInventory(null, 36, "§8» §6§lCLAN LEADERBOARD");
        List<Clan> topClans = plugin.getSortedClans();

        // Filler
        ItemStack filler = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta fMeta = filler.getItemMeta();
        fMeta.setDisplayName(" ");
        filler.setItemMeta(fMeta);
        for (int i = 0; i < 36; i++) gui.setItem(i, filler);

        // Display Top 10
        for (int i = 0; i < Math.min(topClans.size(), 10); i++) {
            Clan clan = topClans.get(i);
            Material icon = Material.IRON_BLOCK;
            if (i == 0) icon = Material.GOLD_BLOCK;
            else if (i == 1) icon = Material.LIGHT_GRAY_STAINED_GLASS; // Or IRON_BLOCK
            else if (i == 2) icon = Material.COPPER_BLOCK;

            gui.setItem(10 + (i > 6 ? i + 2 : i), createLeaderItem(icon, i + 1, clan));
        }

        player.openInventory(gui);
    }

    private ItemStack createLeaderItem(Material mat, int rank, Clan clan) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§e§lRank #" + rank + " §f- §b" + clan.getName());
        List<String> lore = new ArrayList<>();
        lore.add("§7Leader: §f" + Bukkit.getOfflinePlayer(clan.getLeader()).getName());
        lore.add("§7Total Points: §e" + clan.getPoints());
        lore.add("");
        lore.add("§7Members: §f" + clan.getMembers().size());
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
