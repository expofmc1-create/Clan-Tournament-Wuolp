package com.bagadbilla.clantournament;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.Bukkit;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;

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
    @EventHandler
    public void onWitherSkeletonKill(EntityDeathEvent event) {
        if (event.getEntityType() == EntityType.WITHER_SKELETON) {
            Player killer = event.getEntity().getKiller();
        
            if (killer != null) {
                Clan clan = plugin.getClanByPlayer(killer.getUniqueId());
            
                if (clan != null) {
                // Check if a Wither Skeleton Skull dropped in the loot
                    boolean droppedSkull = event.getDrops().stream()
                            .anyMatch(item -> item.getType() == Material.WITHER_SKELETON_SKULL);

                    if (droppedSkull) {
                        if (clan.getWitherSkullsFound() < 8) {
                            clan.addWitherSkull();
                        
                        // Notify the whole clan!
                            for (UUID memberUUID : clan.getMembers()) {
                                 Player member = Bukkit.getPlayer(memberUUID);
                                 if (member != null && member.isOnline()) {
                                     member.sendMessage("§8» §6§lRARE DROP! §e" + killer.getName() + " §ffound a Wither Skull! §7(" + clan.getWitherSkullsFound() + "/8)");
                                 }
                            }

                        // Reward: Let's say 25 points for this task
                            if (clan.getWitherSkullsFound() == 8) {
                                clan.setPoints(clan.getPoints() + 45);
                                killer.sendMessage("§a§lMISSION COMPLETE! §fYour clan earned §e45 Points§f.");
                            }
                        
                            plugin.saveClansToDisk();
                        }
                    }
                }
            }
        }
    }
    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null && !killer.getUniqueId().equals(victim.getUniqueId())) {
            Clan killerClan = plugin.getClanByPlayer(killer.getUniqueId());
            Clan victimClan = plugin.getClanByPlayer(victim.getUniqueId());

        // 1. Both must be in clans
        // 2. Must be DIFFERENT clans
            if (killerClan != null && victimClan != null && !killerClan.getName().equals(victimClan.getName())) {
            
            // 3. Check if this victim has already been counted for this chapter
                if (!killerClan.getUniqueChapter2Kills().contains(victim.getUniqueId())) {
                
                    if (killerClan.getUniqueChapter2Kills().size() < 10) {
                        killerClan.getUniqueChapter2Kills().add(victim.getUniqueId());
                    
                        int currentKills = killerClan.getUniqueChapter2Kills().size();
                    
                    // Notify Clan
                        for (UUID memberUUID : killerClan.getMembers()) {
                            Player member = Bukkit.getPlayer(memberUUID);
                            if (member != null && member.isOnline()) {
                                member.sendMessage("§c§lDOMINATOR §8» §e" + killer.getName() + " §fslayed §n" + victim.getName() + "§f! §7(" + currentKills + "/10)");
                            }
                        }

                    // MISSION COMPLETE: 65 POINTS
                        if (currentKills == 10) {
                            killerClan.setPoints(killerClan.getPoints() + 65);
                            Bukkit.broadcastMessage("§6§lCLAN WARS §8» §eClan §l" + killerClan.getName() + " §fhas completed the §cDominator §fChapter!");
                        }
                    
                        plugin.saveClansToDisk();
                    }
                } else {
                    killer.sendMessage("§c§l(!) §7You already killed " + victim.getName() + "! Go find someone else.");
                }
            }
        }
    }
// CLan Destroying stuff ------------
// dont fking argue why is this in mission listener it just is shut up
   @EventHandler
   public void onLeaderDeath(PlayerDeathEvent event) {
       Player victim = event.getEntity();
       Player killer = victim.getKiller();
       Clan victimClan = plugin.getClanByPlayer(victim.getUniqueId());

    // Only proceed if the victim is a Leader
       if (victimClan != null && victimClan.getLeader().equals(victim.getUniqueId())) {
        
        // Check if server is in Hardcore mode OR if lives hit 0
           boolean isHardcore = Bukkit.getWorlds().get(0).isHardcore();
        
           if (isHardcore || victimClan.getLeaderLives() <= 1) {
            // --- CLAN DESTRUCTION LOGIC ---
               if (killer != null) {
                   Clan killerClan = plugin.getClanByPlayer(killer.getUniqueId());
                   if (killerClan != null) {
                       executeClanWipe(victimClan, killerClan);
                   } else {
                       executeClanWipe(victimClan, null); // Killed by a mob/world
                   }
               } else {
                   executeClanWipe(victimClan, null);
               }
           } else {
            // Just lose a life
               victimClan.removeLeaderLife();
               victim.sendMessage("§c§lWARNING §8» §fYou lost a life! Lives remaining: §e" + victimClan.getLeaderLives());
               plugin.saveClansToDisk();
           }
       }
   }

   private void executeClanWipe(Clan loser, Clan winner) {
       int pointsToSteal = loser.getPoints() / 2;

       if (winner != null) {
           winner.setPoints(winner.getPoints() + pointsToSteal);
           Bukkit.broadcastMessage("§8» §6§lCLAN WIPE: §b" + winner.getName() + " §fhas eliminated §c" + loser.getName() + "§f!");
           Bukkit.broadcastMessage("§8» §e+" + pointsToSteal + " Points §fstolen from the fallen.");
       } else {
           Bukkit.broadcastMessage("§8» §6§lCLAN WIPE: §c" + loser.getName() + " §fhas fallen to the world!");
       }

    // Completely remove the clan
       plugin.getClans().remove(loser.getName().toLowerCase());
    
    // Kick members out of the clan in-game (Optional: message them)
       for (UUID memberUUID : loser.getMembers()) {
           Player p = Bukkit.getPlayer(memberUUID);
           if (p != null) p.sendMessage("§c§lYour clan has been destroyed. You are now a nomad.");
       }
    
       plugin.saveClansToDisk();
   }
//-------3 functions--mission 5 and 6 ---------
  @EventHandler
  public void onEliteMobKill(EntityDeathEvent event) {
      Player killer = event.getEntity().getKiller();
      if (killer == null) return;

      Clan clan = plugin.getClanByPlayer(killer.getUniqueId());
      if (clan == null) return;

      EntityType type = event.getEntityType();

    // Mission 5: The Fortress Siege
      if (type == EntityType.WITHER_SKELETON) {
          if (clan.getWitherSkeletonKills() < 50) {
              clan.addWitherSkeletonKill();
              checkMissionComplete(killer, clan, "Wither Skeletons", clan.getWitherSkeletonKills(), 50);
          }
      } else if (type == EntityType.BLAZE) {
          if (clan.getBlazeKills() < 35) {
              clan.addBlazeKill();
              checkMissionComplete(killer, clan, "Blazes", clan.getBlazeKills(), 35);
          }
      }
  }

  @EventHandler
  public void onDebrisPlace(BlockPlaceEvent event) {
      if (event.getBlock().getType() == Material.ANCIENT_DEBRIS) {
        // Tag the block so these fuckass cant glitch to get infinite points
          event.getBlock().setMetadata("placed_by_player", new FixedMetadataValue(plugin, true));
      }
  }

  @EventHandler
  public void onDebrisMine(BlockBreakEvent event) {
      Player player = event.getPlayer();
      if (event.getBlock().getType() != Material.ANCIENT_DEBRIS) return;

    if (event.getBlock().hasMetadata("placed_by_player")) {
        event.getPlayer().sendMessage("§c§l(!) §7Nice try, but you can't farm placed Debris!");
        return;
    }

      Clan clan = plugin.getClanByPlayer(player.getUniqueId());
      if (clan == null) return;

    // Mission 6: Ancient Power
      if (clan.getDebrisMined() < 12) {
          clan.addDebrisMined();
          player.sendMessage("§8» §6§lMINED! §fAncient Debris found (§e" + clan.getDebrisMined() + "§7/12)");
        
          if (clan.getDebrisMined() == 12) {
              clan.setPoints(clan.getPoints() + 75); // Big reward for Netherite
              Bukkit.broadcastMessage("§8» §b§lMISSION COMPLETE: §fClan §6" + clan.getName() + " §fhas mastered Ancient Debris!");
          }
      }
  }

// Helper to keep code clean
  private void checkMissionComplete(Player p, Clan clan, String mob, int current, int goal) {
      if (current % 10 == 0 || current == goal) {
          p.sendMessage("§8» §6§lMISSION: §f" + mob + " (§e" + current + "§7/" + goal + ")");
      }
    
    // Check if both parts of Mission 5 are done
      if (clan.getWitherSkeletonKills() >= 50 && clan.getBlazeKills() >= 35 && !clan.isMission5Done()) {
          clan.setPoints(clan.getPoints() + 50);
          clan.setMission5Done(true); // You'll need this boolean in Clan.java
          Bukkit.broadcastMessage("§8» §b§lCHAPTER 2: §fClan §6" + clan.getName() + " §fhas conquered the Nether Fortress!");
      }
  }
}

