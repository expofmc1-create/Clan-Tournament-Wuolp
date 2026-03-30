package com.bagadbilla.clantournament;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.List;

public class DominatorGUI implements Listener {

    private final ClanTournament plugin;
    public DominatorGUI(ClanTournament plugin) { this.plugin = plugin; }

    public void openDominatorTasks(Player player, Clan clan) {
        Inventory gui = Bukkit.createInventory(null, 27, "§8» §lChapter II: DOMINATOR");

        // Border (Standard design)
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fMeta = filler.getItemMeta();
        fMeta.setDisplayName(" ");
        filler.setItemMeta(fMeta);
        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17 || i % 9 == 0 || i % 9 == 8) gui.setItem(i, filler);
        }

        // --- TASK 1: THE DARK COLLECTION (Wither Skulls) ---
        int skullProg = clan.getWitherSkullsFound();
        boolean t1Done = skullProg >= 8;
        gui.setItem(11, createTask(Material.WITHER_SKELETON_SKULL, "§6§lTask I: The Dark Collection", 
            "§7Loot skulls from Wither Skeletons.", skullProg, 8, 45, t1Done));

        // --- TASK 2: Placeholder for now ---
        int pvpProg = clan.getUniqueChapter2Kills().size();
        boolean t2Done = pvpProg >= 10;
        gui.setItem(13, createTask(Material.NETHERITE_SWORD, "§c§lTask II: Rival Extinction",
            "§7Slay 10 unique rival clan members.", pvpProg, 10, 65, t2Done));

        // Back Button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta bMeta = back.getItemMeta();
        bMeta.setDisplayName("§cBack to Campaigns");
        back.setItemMeta(bMeta);
        gui.setItem(22, back);

        player.openInventory(gui);
    }

    private ItemStack createTask(Material mat, String name, String desc, int prog, int target, int reward, boolean done) {
        ItemStack item = new ItemStack(done ? Material.ENCHANTED_BOOK : mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        List<String> lore = new ArrayList<>();
        lore.add(desc);
        lore.add("");
        lore.add("§fProgress: §b" + prog + "/" + target);
        lore.add("§fReward: §e+" + reward + " Points");
        lore.add("");
        lore.add(done ? "§a§l✔ COMPLETED" : "§c§l✘ INCOMPLETE");
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().contains("Chapter II: DOMINATOR")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ARROW) {
                Player player = (Player) event.getWhoClicked();
                Clan clan = plugin.getClanByPlayer(player.getUniqueId());
                new MissionsGUI(plugin).openMissionsMenu(player, clan);
            }
        }
    }
}
