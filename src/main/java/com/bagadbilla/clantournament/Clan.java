package com.bagadbilla.clantournament;

import org.bukkit.Location;
import java.util.HashSet;
import java.util.UUID;
import java.util.Set;

public class Clan {
    private String name;
    private UUID leader;
    private HashSet<UUID> members = new HashSet<>();
    private int points = 0;
    private int wardenKills = 0;
    private int blazeKills = 0;
    private int witherSkeletonKills = 0;
    private int debrisMined = 0;
    private int leaderLives = 5;
    private Location pos1, pos2;
// --- NEW: Mission Progress Tracking ---
    private int mobKills = 0; 
    private Set<UUID> uniqueKills = new HashSet<>();
    private int witherSkullsFound = 0;
    private Set<UUID> uniqueChapter2Kills = new HashSet<>(); //exp
    public Set<UUID> getUniqueChapter2Kills() { return uniqueChapter2Kills; } // exp
    private boolean mission5Done = false;
    public Clan(String name, UUID leader) {
        this.name = name;
        this.leader = leader;
        this.members.add(leader);
    }

    public boolean isInside(Location loc) {
        if (pos1 == null || pos2 == null || !loc.getWorld().equals(pos1.getWorld())) return false;
        double xMin = Math.min(pos1.getX(), pos2.getX());
        double xMax = Math.max(pos1.getX(), pos2.getX());
        double zMin = Math.min(pos1.getZ(), pos2.getZ());
        double zMax = Math.max(pos1.getZ(), pos2.getZ());
        return loc.getX() >= xMin && loc.getX() <= xMax && loc.getZ() >= zMin && loc.getZ() <= zMax;
    }

    // Getters and Setters
    public int getMobKills() { return mobKills; }
    public void setMobKills(int mobKills) { this.mobKills = mobKills; }
    public void addMobKill() { this.mobKills++; }
    public Set<UUID> getUniqueKills() { return uniqueKills; }
    public void setUniqueKills(Set<UUID> uniqueKills) { this.uniqueKills = uniqueKills; }
    public String getName() { return name; }
    public UUID getLeader() { return leader; }
    public HashSet<UUID> getMembers() { return members; }
    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
    public void setPos1(Location pos1) { this.pos1 = pos1; }
    public void setPos2(Location pos2) { this.pos2 = pos2; }
    public Location getPos1() { return pos1; }
    public Location getPos2() { return pos2; }
    public int getWardenKills() { return wardenKills; }
    public void setWardenKills(int kills) { this.wardenKills = kills; }
    public void addWardenKill() { this.wardenKills++; }
    public int getWitherSkullsFound() { return witherSkullsFound; }
    public void setWitherSkullsFound(int count) { this.witherSkullsFound = count; }
    public void addWitherSkull() { this.witherSkullsFound++; }
    public int getLeaderLives() { return leaderLives; }
    public void setLeaderLives(int lives) { this.leaderLives = lives; }
    public void removeLeaderLife() { this.leaderLives--; }
    public int getBlazeKills() { return blazeKills; }
    public void addBlazeKill() { this.blazeKills++; }
    public int getWitherSkeletonKills() { return witherSkeletonKills; }
    public void addWitherSkeletonKill() { this.witherSkeletonKills++; }
    public int getDebrisMined() { return debrisMined; }
    public void addDebrisMined() { this.debrisMined++; }
    public boolean isMission5Done() { return mission5Done; }
    public void setMission5Done(boolean done) { this.mission5Done = done; }
    public void setBlazeKills(int kills) { this.blazeKills = kills; }
    public void setWitherSkeletonKills(int kills) { this.witherSkeletonKills = kills; }
    public void setDebrisMined(int count) { this.debrisMined = count; }
}

