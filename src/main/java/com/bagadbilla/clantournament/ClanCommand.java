package com.bagadbilla.clantournament;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.UUID;

public class ClanCommand implements CommandExecutor {
    private final ClanTournament plugin;

    public ClanCommand(ClanTournament plugin) { 
        this.plugin = plugin; 
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        UUID uuid = player.getUniqueId();

        if (args.length == 0) {
            player.sendMessage("§b--- Clan Tournament ---");
            player.sendMessage("§e/clan invite <player> §7- Start a group");
            player.sendMessage("§e/clan accept §7- Join a group");
            player.sendMessage("§e/clan leave §7- Leave your group/clan");
            player.sendMessage("§e/clan disband §7- Delete your clan (Leader only)");
            player.sendMessage("§e/clan create <name> §7- Register (15 Diamonds + 5 members)");
            player.sendMessage("§e/clan seemyclan §7- View your clan GUI");
            player.sendMessage("§e/clan pos1/pos2 §7- Set 100x100 area");
            return true;
        }

        // --- INVITE ---
        if (args[0].equalsIgnoreCase("invite")) {
            if (plugin.getClanByPlayer(uuid) != null) {
                player.sendMessage("§cYou are already in a clan!");
                return true;
            }
            if (args.length < 2) { player.sendMessage("§cUsage: /clan invite <player>"); return true; }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) { player.sendMessage("§cPlayer not found."); return true; }
            
            plugin.getPendingInvites().put(target.getUniqueId(), uuid);
            player.sendMessage("§aInvite sent to " + target.getName());
            target.sendMessage("§a" + player.getName() + " invited you! Type §6/clan accept");
        }

        // --- ACCEPT ---
        else if (args[0].equalsIgnoreCase("accept")) {
            if (plugin.getClanByPlayer(uuid) != null) {
                player.sendMessage("§cYou are already in a clan!");
                return true;
            }
            UUID inviterUUID = plugin.getPendingInvites().get(uuid);
            if (inviterUUID == null) { player.sendMessage("§cNo pending invites."); return true; }

            plugin.getGroupCheck().putIfAbsent(inviterUUID, new ArrayList<>());
            ArrayList<UUID> group = plugin.getGroupCheck().get(inviterUUID);
            
            if (group.size() >= 4) {
                player.sendMessage("§cThat group is already full (5/5)!");
                return true;
            }

            group.add(uuid);
            plugin.getPendingInvites().remove(uuid);
            player.sendMessage("§aJoined the group!");
            Player leader = Bukkit.getPlayer(inviterUUID);
            if (leader != null) leader.sendMessage("§a" + player.getName() + " joined! Group: " + (group.size() + 1) + "/5");
        }

        // --- CREATE ---
        else if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 2) { player.sendMessage("§cUsage: /clan create <name>"); return true; }
            ArrayList<UUID> members = plugin.getGroupCheck().get(uuid);
            
            if (members == null || members.size() < 4) {
                player.sendMessage("§cYou need 5 people in your group to create a clan!");
                return true;
            }
            if (!player.getInventory().contains(Material.DIAMOND, 15)) {
                player.sendMessage("§cRequirement: 15 Diamonds.");
                return true;
            }

            player.getInventory().removeItem(new ItemStack(Material.DIAMOND, 15));
            String clanName = args[1];
            Clan newClan = new Clan(clanName, uuid);
            for (UUID m : members) newClan.getMembers().add(m);
            
            plugin.getClans().put(clanName, newClan);
            plugin.getGroupCheck().remove(uuid);
            plugin.saveClansToDisk();
            Bukkit.broadcastMessage("§6§l" + clanName + " §aformed by " + player.getName());
        }

        // --- LEAVE ---
        else if (args[0].equalsIgnoreCase("leave")) {
            Clan clan = plugin.getClanByPlayer(uuid);
            if (clan != null) {
                if (clan.getLeader().equals(uuid)) {
                    player.sendMessage("§cLeaders must use /clan disband");
                } else {
                    clan.getMembers().remove(uuid);
                    player.sendMessage("§eLeft the clan.");
                    plugin.saveClansToDisk();
                }
                return true;
            }
            UUID leaderUUID = plugin.getGroupLeader(uuid);
            if (leaderUUID != null) {
                plugin.getGroupCheck().get(leaderUUID).remove(uuid);
                player.sendMessage("§eLeft the pending group.");
            } else {
                player.sendMessage("§cYou are not in a group or clan.");
            }
        }

        // --- DISBAND ---
        else if (args[0].equalsIgnoreCase("disband")) {
            Clan clan = plugin.getClanByPlayer(uuid);
            if (clan == null || !clan.getLeader().equals(uuid)) {
                player.sendMessage("§cOnly leaders can disband.");
                return true;
            }
            plugin.getClans().remove(clan.getName());
            plugin.saveClansToDisk();
            player.sendMessage("§cClan disbanded. Protection gone!");
        }

        // --- POSITIONS ---
        else if (args[0].equalsIgnoreCase("pos1")) {
            plugin.getSelectionManager().setPosition(player, 1);
        }
        else if (args[0].equalsIgnoreCase("pos2")) {
            plugin.getSelectionManager().setPosition(player, 2);
        }

        // --- SEE MY CLAN ---
        else if (args[0].equalsIgnoreCase("seemyclan")) {
            Clan clan = plugin.getClanByPlayer(uuid);

            if (clan == null) {
                UUID leaderUUID = plugin.getGroupLeader(uuid);
                if (leaderUUID != null) {
                    ArrayList<UUID> group = plugin.getGroupCheck().get(leaderUUID);
                    int size = (group != null) ? group.size() + 1 : 1;
                    player.sendMessage("§eYour official clan hasn't been created yet!");
                    player.sendMessage("§7Current Group Size: §b" + size + "/5");
                } else {
                    player.sendMessage("§cYou are not in a clan or a group.");
                }
                return true;
            }

            // Open the GUI
            plugin.getClanGUI().openClanMenu(player, clan);
        }

        return true;
    }
}
