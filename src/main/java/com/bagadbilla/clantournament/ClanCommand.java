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
            player.sendMessage("§e/clan invite <player> §7- Invite to group or clan");
            player.sendMessage("§e/clan accept §7- Join the inviter");
            player.sendMessage("§e/clan leave §7- Leave your current clan/group");
            player.sendMessage("§e/clan disband §7- Delete clan (Leader only)");
            player.sendMessage("§e/clan create <name> §7- Register (15 Diamonds + 5 members)");
            player.sendMessage("§e/clan seemyclan §7- Open GUI");
            player.sendMessage("§e/clan pos1/pos2 §7- Set protection area");
            return true;
        }

        // --- INVITE ---
        if (args[0].equalsIgnoreCase("invite")) {
            if (args.length < 2) { player.sendMessage("§cUsage: /clan invite <player>"); return true; }
            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) { player.sendMessage("§cPlayer not found."); return true; }

            Clan myClan = plugin.getClanByPlayer(uuid);
            if (myClan != null && !myClan.getLeader().equals(uuid)) {
                player.sendMessage("§cOnly the leader can invite new members!");
                return true;
            }

            if (plugin.getClanByPlayer(target.getUniqueId()) != null) {
                player.sendMessage("§cThat player is already in a clan!");
                return true;
            }

            plugin.getPendingInvites().put(target.getUniqueId(), uuid);
            player.sendMessage("§aInvite sent to " + target.getName());
            target.sendMessage("§a" + player.getName() + " invited you! Type §6/clan accept");
            return true; // FIXED: Added return
        }

        // --- ACCEPT ---
        else if (args[0].equalsIgnoreCase("accept")) {
            UUID inviterUUID = plugin.getPendingInvites().get(uuid);
            if (inviterUUID == null) { player.sendMessage("§cNo pending invites."); return true; }

            Clan targetClan = plugin.getClanByPlayer(inviterUUID);

            if (targetClan != null) {
                if (targetClan.getMembers().size() >= 10) {
                    player.sendMessage("§cThat clan is full (10/10)!");
                    return true;
                }
                targetClan.getMembers().add(uuid);
                plugin.getPendingInvites().remove(uuid);
                plugin.saveClansToDisk();
                player.sendMessage("§aJoined §6" + targetClan.getName());
                
                Player leader = Bukkit.getPlayer(inviterUUID);
                if (leader != null) leader.sendMessage("§a" + player.getName() + " joined your clan!");
            } else {
                // LOBBY GROUP LOGIC
                plugin.getGroupCheck().putIfAbsent(inviterUUID, new ArrayList<>());
                ArrayList<UUID> group = plugin.getGroupCheck().get(inviterUUID);
                
                if (group.size() >= 9) {
                    player.sendMessage("§cThis group is full!");
                    return true;
                }

                group.add(uuid);
                plugin.getPendingInvites().remove(uuid); // FIXED: Moved after adding to group
                player.sendMessage("§aJoined the group!");
                
                Player leader = Bukkit.getPlayer(inviterUUID);
                if (leader != null) leader.sendMessage("§e" + player.getName() + " joined your group! (" + (group.size() + 1) + "/5)");
            }
            return true; // FIXED: Added return
        }

        // --- CREATE ---
        else if (args[0].equalsIgnoreCase("create")) {
            if (args.length < 2) { player.sendMessage("§cUsage: /clan create <name>"); return true; }
            if (plugin.getClanByPlayer(uuid) != null) { player.sendMessage("§cYou already have a clan!"); return true; }

            ArrayList<UUID> group = plugin.getGroupCheck().get(uuid);
            if (group == null || group.size() < 0) {   //here ak here ak here here here see this brt see ronjfdjbvhrfnjknfjsdjfsdjfshf
                player.sendMessage("§cYou need at least 5 members in your group (including you)!");
                return true;
            }

            if (!player.getInventory().contains(Material.DIAMOND, 15)) {
                player.sendMessage("§cRequirement: 15 Diamonds.");
                return true;
            }

            player.getInventory().removeItem(new ItemStack(Material.DIAMOND, 15));
            Clan newClan = new Clan(args[1], uuid);
            newClan.getMembers().add(uuid); 
            for (UUID m : group) newClan.getMembers().add(m);
            
            plugin.getClans().put(args[1], newClan);
            plugin.getGroupCheck().remove(uuid);
            plugin.saveClansToDisk();
            Bukkit.broadcastMessage("§6§l" + args[1] + " §ahas been created by " + player.getName());
            return true;
        }

        // --- LEAVE ---
        else if (args[0].equalsIgnoreCase("leave")) {
            Clan clan = plugin.getClanByPlayer(uuid);
            if (clan != null) {
                if (clan.getLeader().equals(uuid)) player.sendMessage("§cLeaders must use /clan disband");
                else { clan.getMembers().remove(uuid); player.sendMessage("§eLeft clan."); plugin.saveClansToDisk(); }
            } else {
                UUID leader = plugin.getGroupLeader(uuid);
                if (leader != null) { 
                    plugin.getGroupCheck().get(leader).remove(uuid); 
                    player.sendMessage("§eLeft group."); 
                } else {
                    player.sendMessage("§cYou are not in a group or clan.");
                }
            }
            return true;
        }

        // --- DISBAND ---
        else if (args[0].equalsIgnoreCase("disband")) {
            Clan clan = plugin.getClanByPlayer(uuid);

    // 1. Check if they are actually a leader
            if (clan == null || !clan.getLeader().equals(uuid)) {
                player.sendMessage("§cOnly the clan leader can disband the clan!");
                return true;
            }

    // 2. Check if they are running the confirmation
           if (args.length > 1 && args[1].equalsIgnoreCase("confirm")) {
               if (plugin.getDisbandQueue().contains(uuid)) {
            // SUCCESSFUL DISBAND
                   Bukkit.broadcastMessage("§4§l[!] §6" + clan.getName() + " §chas been disbanded by " + player.getName() + "!");
                   plugin.getClans().remove(clan.getName());
                   plugin.getDisbandQueue().remove(uuid);
                   plugin.saveClansToDisk();
                   return true;
              }
           }

    // 3. First time running the command - Send Warning
           if (!plugin.getDisbandQueue().contains(uuid)) {
               plugin.getDisbandQueue().add(uuid);
           }

           player.sendMessage(" ");
           player.sendMessage("§4§l⚠️ WARNING ⚠️");
   	   player.sendMessage("§cDisbanding will §npermanently§c delete your territory protection!");
   	   player.sendMessage("§cAll members will be kicked instantly.");
   	   player.sendMessage("§7To proceed, type: §f/clan disband confirm");
   	   player.sendMessage(" ");
    
    // Optional: Remove them from the queue after 20 seconds so it doesn't stay dangerous forever
   	   Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getDisbandQueue().remove(uuid), 400L);
    
   	   return true;
        }
 
        // --- CLAN CHAT TOGGLE ---
        else if (args[0].equalsIgnoreCase("chat") || args[0].equalsIgnoreCase("cc")) {
            Clan clan = plugin.getClanByPlayer(uuid);
            if (clan == null) {
                player.sendMessage("§cYou must be in a clan to use clan chat!");
                return true;
            }

            if (plugin.getClanChatToggled().contains(uuid)) {
                plugin.getClanChatToggled().remove(uuid);
                player.sendMessage("§eClan Chat: §cOFF §7(Global Chat Enabled)");
            } else {
                plugin.getClanChatToggled().add(uuid);
                player.sendMessage("§eClan Chat: §aON §7(Teammates Only)");
            }
            return true;
        }

        // --- POSITIONS ---
        else if (args[0].equalsIgnoreCase("pos1")) {
            plugin.getSelectionManager().setPosition(player, 1);
            return true;
        }
        else if (args[0].equalsIgnoreCase("pos2")) {
            plugin.getSelectionManager().setPosition(player, 2);
            return true;
        }

        // --- SEEMYCLAN ---
        else if (args[0].equalsIgnoreCase("seemyclan")) {
            Clan clan = plugin.getClanByPlayer(uuid);
            if (clan != null) plugin.getClanGUI().openClanMenu(player, clan);
            else player.sendMessage("§cYou don't have a clan yet.");
            return true;
        }

        return true;
    }
}
