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
    private Location pos1, pos2;

// --- NEW: Mission Progress Tracking ---
    private int mobKills = 0; 
    private Set<UUID> uniqueKills = new HashSet<>();

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
}
