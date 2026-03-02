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
import org.bukkit.inventory.meta.SkullMeta;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClanGUI implements Listener {

    private final ClanTournament plugin;
    public ClanGUI(ClanTournament plugin) { this.plugin = plugin; }

    public void openClanMenu(Player player, Clan clan) {
        Inventory gui = Bukkit.createInventory(null, 54, "§0Clan Profile: " + clan.getName());

        // --- ROW 1: INFO ---
        ItemStack clock = new ItemStack(Material.CLOCK);
        ItemMeta clockMeta = clock.getItemMeta();
        clockMeta.setDisplayName("§6§l" + clan.getName());
        List<String> clockLore = new ArrayList<>();
        clockLore.add("§7Leader: §f" + Bukkit.getOfflinePlayer(clan.getLeader()).getName());
        clockMeta.setLore(clockLore);
        clock.setItemMeta(clockMeta);
        gui.setItem(13, clock);

        ItemStack compass = new ItemStack(Material.COMPASS);
        ItemMeta compMeta = compass.getItemMeta();
        compMeta.setDisplayName("§b§lClan Territory");
        List<String> compLore = new ArrayList<>();
        if (clan.getPos1() != null) {
            compLore.add("§7X: §f" + clan.getPos1().getBlockX() + " §7Z: §f" + clan.getPos1().getBlockZ());
        } else {
            compLore.add("§cLocation not set!");
        }
        compMeta.setLore(compLore);
        compass.setItemMeta(compMeta);
        gui.setItem(15, compass);

        // --- ROW 2: HEADER ---
        ItemStack sign = new ItemStack(Material.OAK_SIGN);
        ItemMeta signMeta = sign.getItemMeta();
        signMeta.setDisplayName("§e§lMember List");
        sign.setItemMeta(signMeta);
        gui.setItem(22, sign);

        // --- ROWS 3/4: MEMBER HEADS ---
        int[] memberSlots = {30, 31, 32, 33, 34};
        int i = 0;
        for (UUID memberId : clan.getMembers()) {
            if (i >= memberSlots.length) break;
            ItemStack head = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta headMeta = (SkullMeta) head.getItemMeta();
            headMeta.setOwningPlayer(Bukkit.getOfflinePlayer(memberId));
            headMeta.setDisplayName("§a" + Bukkit.getOfflinePlayer(memberId).getName());
            head.setItemMeta(headMeta);
            gui.setItem(memberSlots[i], head);
            i++;
        }

        player.openInventory(gui);
    }

    // THIS PREVENTS STEALING
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().startsWith("§0Clan Profile:")) {
            event.setCancelled(true); // Nobody can take anything!
        }
    }
}
