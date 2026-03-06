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

        // --- MISSION 1: SLAYER ---
        gui.setItem(11, createMissionItem(
            Material.IRON_SWORD, 
            "§b§lSlayer Mission", 
            "§7Kill 50 players in the arena.", 
            clan.getPoints(), 50, "Points"
        ));

        // --- MISSION 2: EXPLORER ---
        gui.setItem(13, createMissionItem(
            Material.COMPASS, 
            "§e§lDominator Mission", 
            "§7Capture the central hill.", 
            0, 1, "Capture"
        ));

        // --- MISSION 3: WEALTH ---
        gui.setItem(15, createMissionItem(
            Material.GOLD_INGOT, 
            "§a§lWealth Mission", 
            "§7Collect 1,000 tournament coins.", 
            0, 1000, "Coins"
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

    private ItemStack createMissionItem(Material material, String name, String goal, int progress, int target, String unit) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> lore = new ArrayList<>();
            lore.add(goal);
            lore.add("");
            lore.add("§fProgress: §b" + progress + "/" + target + " " + unit);
            
            String bar = progress >= target ? "§a§lCOMPLETED" : "§eIn Progress...";
            lore.add("§fStatus: " + bar);
            
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        
        String title = event.getView().getTitle();
        
        // Use contains to bypass any bold/color formatting issues
        if (title.contains("CLAN MISSIONS")) {
            event.setCancelled(true);
            Player player = (Player) event.getWhoClicked();
            ItemStack clicked = event.getCurrentItem();

            if (clicked == null || clicked.getType() == Material.AIR) return;

            // Handle the Back Button
            if (clicked.getType() == Material.ARROW) {
                player.closeInventory();
                // Forces the main clan menu to open via the registered command
                player.performCommand("c myclan"); 
            }
        }
    }
}
