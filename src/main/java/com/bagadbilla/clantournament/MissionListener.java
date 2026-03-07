package com.bagadbilla.clantournament;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import java.util.UUID;

public class MissionListener implements Listener {

    private final ClanTournament plugin;

    public MissionListener(ClanTournament plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if (event.getEntity().getKiller() instanceof Player player) {
            Clan clan = plugin.getClanByPlayer(player.getUniqueId());
            
            if (clan != null) {
                if (isHostile(event.getEntity().getType())) {
                    // Mission 2: 120 Mob Kills
                    if (clan.getMobKills() < 120) {
                        clan.addMobKill();
                        
                        if (clan.getMobKills() % 10 == 0) {
                            player.sendMessage("§b§lTRIDENT §8» §fClan Mob Kills: §e" + clan.getMobKills() + "/120");
                        }

                        if (clan.getMobKills() == 120) {
                            clan.setPoints(clan.getPoints() + 20);
                            player.sendMessage("§a§lMISSION COMPLETE! §fYour clan earned §e20 Points§f.");
                            plugin.saveClansToDisk();
                        }
                    }
                }
            }
        }
    }

    private boolean isHostile(EntityType type) {
        return type == EntityType.ZOMBIE || type == EntityType.SKELETON || 
               type == EntityType.SPIDER || type == EntityType.CREEPER || 
               type == EntityType.WITCH || type == EntityType.ENDERMAN ||
               type == EntityType.BLAZE || type == EntityType.GHAST;
    }
    @EventHandler
    public void onWardenKill(EntityDeathEvent event) {
    // 1. Check if the dead thing is a Warden
        if (event.getEntityType() == EntityType.WARDEN) {
            Player killer = event.getEntity().getKiller();

        // 2. Check if a player killed it
            if (killer != null) {
                Clan clan = plugin.getClanByPlayer(killer.getUniqueId());

            // 3. If the killer is in a clan, progress the mission
                if (clan != null) {
                    if (clan.getWardenKills() < 5) {
                        clan.addWardenKill();
                    
                    // Broadcast to the whole clan so they can celebrate
                        for (UUID memberUUID : clan.getMembers()) {
                            Player member = org.bukkit.Bukkit.getPlayer(memberUUID);
                            if (member != null && member.isOnline()) {
                                member.sendMessage("§b§lTRIDENT §8» §e" + killer.getName() + " §fslayed a Warden! §7(" + clan.getWardenKills() + "/5)");
                            }
                        }

                    // Mission Complete Check
                        if (clan.getWardenKills() == 5) {
                            clan.setPoints(clan.getPoints() + 45); // Give the big 45 point reward
                            killer.sendMessage("§a§lCAMPAIGN COMPLETE! §fYour clan earned §e45 Points§f.");
                        }
                    
                        plugin.saveClansToDisk();
                    }
                }
            }
        }
    }
}
