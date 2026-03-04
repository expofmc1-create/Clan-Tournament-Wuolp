package com.bagadbilla.clantournament;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClanGUI implements Listener {

    private final ClanTournament plugin;
    public ClanGUI(ClanTournament plugin) { this.plugin = plugin; }

    public void openClanMenu(Player player, Clan clan) {
        Inventory gui = Bukkit.createInventory(null, 54, "§8» §l" + clan.getName().toUpperCase());

        // --- 1. FILLER & BORDER DESIGN ---
        ItemStack blackFiller = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta blackMeta = blackFiller.getItemMeta();
        blackMeta.setDisplayName(" ");
        blackFiller.setItemMeta(blackMeta);

        ItemStack grayFiller = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        grayFiller.setItemMeta(blackMeta);

        for (int i = 0; i < 54; i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                gui.setItem(i, blackFiller);
            } else {
                gui.setItem(i, grayFiller);
            }
        }

        // --- 2. ROW 1: CLAN DATA ---
        ItemStack clock = new ItemStack(Material.CLOCK);
        ItemMeta clockMeta = clock.getItemMeta();
        clockMeta.setDisplayName("§6§l" + clan.getName());
        List<String> clockLore = new ArrayList<>();
        clockLore.add("§7Leader: §f" + Bukkit.getOfflinePlayer(clan.getLeader()).getName());
        clockLore.add("§7Tournament Points: §e" + clan.getPoints());
        clockMeta.setLore(clockLore);
        clock.setItemMeta(clockMeta);
        gui.setItem(13, clock);

        ItemStack map = new ItemStack(Material.MAP);
        ItemMeta mapMeta = map.getItemMeta();
        mapMeta.setDisplayName("§b§lClan Territory");
        List<String> mapLore = new ArrayList<>();
        if (clan.getPos1() != null) {
            mapLore.add("§7X: §f" + clan.getPos1().getBlockX() + " §7Z: §f" + clan.getPos1().getBlockZ());
            mapLore.add("§aArea: 100x100 Protected");
        } else {
            mapLore.add("§cLocation not set!");
        }
        mapMeta.setLore(mapLore);
        map.setItemMeta(mapMeta);
        gui.setItem(15, map);

        // --- 3. ROW 3: MEMBER LIST SIGN ---
        ItemStack sign = new ItemStack(Material.OAK_SIGN);
        ItemMeta signMeta = sign.getItemMeta();
        signMeta.setDisplayName("§e§lClan Members");
        sign.setItemMeta(signMeta);
        gui.setItem(22, sign);

        // --- 4. ROW 4: PLAYER HEADS ---
        int[] memberSlots = {28, 29, 30, 31, 32, 37, 38, 39, 40, 41};
        int i = 0;
        for (UUID memberId : clan.getMembers()) {
            if (i >= memberSlots.length) break;
            OfflinePlayer op = Bukkit.getOfflinePlayer(memberId);
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            headMeta.setOwningPlayer(op);
            headMeta.setDisplayName("§a" + op.getName());
            List<String> headLore = new ArrayList<>();
            headLore.add(op.isOnline() ? "§a● Online" : "§c○ Offline");
            if (memberId.equals(clan.getLeader())) headLore.add("§6§lLEADER");
            headMeta.setLore(headLore);
            head.setItemMeta(headMeta);
            gui.setItem(memberSlots[i], head);
            i++;
        }

        // Vitals Button - Slot 11
        ItemStack vitals = new ItemStack(Material.GLISTERING_MELON_SLICE);
	ItemMeta vMeta = vitals.getItemMeta();
	if (vMeta != null) {
  	    vMeta.setDisplayName("§a§lClan Vitals");
    	    List<String> vLore = new ArrayList<>();
    	    vLore.add("§7Check health, hunger, and");
    	    vLore.add("§7location of all teammates.");
    	    vLore.add("");
    	    vLore.add("§eClick to view!");
    	    vMeta.setLore(vLore);
    	    vitals.setItemMeta(vMeta);
	}
	gui.setItem(11, vitals);

        // --- 5. LEAVE BUTTON (Slot 53) ---
        // If leader, show barrier (can't leave). If member, show red bed.
        if (clan.getLeader().equals(player.getUniqueId())) {
            ItemStack barrier = new ItemStack(Material.BARRIER);
            ItemMeta bMeta = barrier.getItemMeta();
            bMeta.setDisplayName("§c§lLEADER STATUS");
            List<String> bLore = new ArrayList<>();
            bLore.add("§7You cannot leave your own clan.");
            bLore.add("§7Use §f/clan disband §7to delete.");
            bMeta.setLore(bLore);
            barrier.setItemMeta(bMeta);
            gui.setItem(53, barrier);
        } else {
            ItemStack bed = new ItemStack(Material.RED_BED);
            ItemMeta bedMeta = bed.getItemMeta();
            bedMeta.setDisplayName("§c§lLEAVE CLAN");
            List<String> bedLore = new ArrayList<>();
            bedLore.add("§7Click to exit the clan.");
            bedMeta.setLore(bedLore);
            bed.setItemMeta(bedMeta);
            gui.setItem(53, bed);
        }

        player.openInventory(gui);
    }

    // --- NEW: CONFIRMATION MENU ---
    public void openConfirmMenu(Player player) {
        Inventory confirm = Bukkit.createInventory(null, 27, "§4Confirm Leaving?");
        
        // Yes Button
        ItemStack yes = new ItemStack(Material.LIME_CONCRETE);
        ItemMeta yMeta = yes.getItemMeta();
        yMeta.setDisplayName("§a§lYES, I WANT TO LEAVE");
        yes.setItemMeta(yMeta);

        // No Button
        ItemStack no = new ItemStack(Material.RED_CONCRETE);
        ItemMeta nMeta = no.getItemMeta();
        nMeta.setDisplayName("§c§lNO, TAKE ME BACK");
        no.setItemMeta(nMeta);

        confirm.setItem(11, yes);
        confirm.setItem(15, no);
        player.openInventory(confirm);
    }

@EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getCurrentItem() == null) return;
        String title = event.getView().getTitle();
        Player player = (Player) event.getWhoClicked();
        Material clickedType = event.getCurrentItem().getType();

        // Match the Main Menu
        if (title.contains("»")) {
            event.setCancelled(true);
            if (event.getCurrentItem().getType() == Material.RED_BED) {
                openConfirmMenu(player);
            }
            else if (clickedType == Material.GLISTERING_MELON_SLICE) {
                player.closeInventory();
                player.performCommand("clan vitals");
            }
        }

        // Match the Confirmation Menu
        else if (title.equals("§4Confirm Leaving?")) {
            event.setCancelled(true);
            if (event.getCurrentItem().getType() == Material.LIME_CONCRETE) {
                player.closeInventory();
                player.performCommand("clan leave");
            } else if (event.getCurrentItem().getType() == Material.RED_CONCRETE) {
                player.closeInventory();
                // Re-open main clan menu
                Clan clan = plugin.getClanByPlayer(player.getUniqueId());
                if (clan != null) openClanMenu(player, clan);
            }
        }
    }
}
