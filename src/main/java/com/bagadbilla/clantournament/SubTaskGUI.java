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

public class SubTaskGUI implements Listener {

    private final ClanTournament plugin;
    public SubTaskGUI(ClanTournament plugin) { this.plugin = plugin; }

    public void openSlayerTasks(Player player, Clan clan) {
        Inventory gui = Bukkit.createInventory(null, 27, "§8» §lCAMPAIGN: BLOODBATH");

        // Border
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fMeta = filler.getItemMeta();
        fMeta.setDisplayName(" ");
        filler.setItemMeta(fMeta);
        for (int i = 0; i < 27; i++) {
            if (i < 9 || i > 17 || i % 9 == 0 || i % 9 == 8) gui.setItem(i, filler);
        }

        // --- TASK 1: ESTABLISH ROOTS ---
        boolean t1Done = clan.getPos1() != null;
        gui.setItem(11, createTask(Material.GRASS_BLOCK, "§a§lTask I: Establish Roots", 
            "§7Set your clan territory.", t1Done ? 1 : 0, 1, 10, t1Done));

        // --- TASK 2: TENSION RISING ---
        // For now, using placeholders for progress; we will need to store these in the Clan object later
        gui.setItem(13, createTask(Material.IRON_SWORD, "§e§lTask II: Tension Rising", 
            "§7Kill 4 players of other clans.", 0, 4, 20, false));

        // --- TASK 3: TOTAL WAR ---
        gui.setItem(15, createTask(Material.NETHERITE_AXE, "§c§lTask III: Total War", 
            "§715 Unique kills against rivals.", 0, 15, 45, false));

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
        if (event.getView().getTitle().contains("CAMPAIGN: BLOODBATH")) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.ARROW) {
                Player player = (Player) event.getWhoClicked();
                Clan clan = plugin.getClanByPlayer(player.getUniqueId());
                new MissionsGUI(plugin).openMissionsMenu(player, clan);
            }
        }
    }
}
