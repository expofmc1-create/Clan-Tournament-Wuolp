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

public class MissionsGUI implements Listener {

    private final ClanTournament plugin;

    public MissionsGUI(ClanTournament plugin) {
        this.plugin = plugin;
    }

    public void openMissionsMenu(Player player, Clan clan) {
        Inventory gui = Bukkit.createInventory(null, 27, "§8» §lCLAN MISSIONS");

        // --- BORDER DESIGN ---
        ItemStack filler = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta fMeta = filler.getItemMeta();
        if (fMeta != null) {
            fMeta.setDisplayName(" ");
            filler.setItemMeta(fMeta);
        }

        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17 || i % 9 == 0 || i % 9 == 8) {
                gui.setItem(i, filler);
            }
        }

        // Current progress check
        int points = clan.getPoints();

        // --- MISSION 1: SLAYER (Always Unlocked) ---
        gui.setItem(11, createMissionItem(
            Material.IRON_SWORD, 
            "§b§l[Tier I] Slayer", 
            "§7Kill 50 players in the arena.", 
            points, 50, "Points", true
        ));

        // --- MISSION 2: DOMINATOR (Unlocks at 50 points) ---
        boolean tier2Unlocked = points >= 50;
        gui.setItem(13, createMissionItem(
            tier2Unlocked ? Material.COMPASS : Material.BARRIER, 
            tier2Unlocked ? "§e§l[Tier II] Dominator" : "§c§lLocked: Tier II", 
            "§7Capture the central hill.", 
            points, 100, "Points", tier2Unlocked
        ));

        // --- MISSION 3: WEALTH (Unlocks at 100 points) ---
        boolean tier3Unlocked = points >= 100;
        gui.setItem(15, createMissionItem(
            tier3Unlocked ? Material.GOLD_INGOT : Material.BARRIER, 
            tier3Unlocked ? "§a§l[Tier III] Wealth" : "§c§lLocked: Tier III", 
            "§7Collect 1,000 tournament coins.", 
            points, 200, "Points", tier3Unlocked
        ));

        // --- BACK BUTTON ---
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta bMeta = back.getItemMeta();
        if (bMeta != null) {
            bMeta.setDisplayName("§c§lBack to Clan Menu");
            back.setItemMeta(bMeta);
        }
        gui.setItem(22, back);

        player.openInventory(gui);
    }

    private ItemStack createMissionItem(Material material, String name, String goal, int progress, int target, String unit, boolean unlocked) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> lore = new ArrayList<>();
            
            if (unlocked) {
                lore.add(goal);
                lore.add("");
                lore.add("§fProgress: §b" + progress + "/" + target + " " + unit);
                
                String status = progress >= target ? "§a§lCOMPLETED" : "§eIn Progress...";
                lore.add("§fStatus: " + status);
            } else {
                lore.add("§7Complete the previous mission");
                lore.add("§7to unlock this task.");
                lore.add("");
                lore.add("§cRequires 50 Clan Points");
            }
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        String title = event.getView().getTitle();
        
        if (title.contains("CLAN MISSIONS")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clicked = event.getCurrentItem();

            if (clicked == null || clicked.getType() == Material.AIR) return;

// --- THE NEW PART: Opening Sub-Tasks ---
            if (clicked.getType() == Material.IRON_SWORD) {
                Clan clan = plugin.getClanByPlayer(player.getUniqueId());
                if (clan != null) {
                    // This opens the specific missions for the Bloodbath campaign
                    new SubTaskGUI(plugin).openSlayerTasks(player, clan);
                }
            }

            if (clicked.getType() == Material.ARROW) {
                player.closeInventory();
                player.performCommand("c myclan"); 
            }
        }
    }
}
