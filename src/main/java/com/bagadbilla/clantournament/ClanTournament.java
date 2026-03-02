package com.bagadbilla.clantournament;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class ClanTournament extends JavaPlugin {

    private HashMap<String, Clan> clans = new HashMap<>();
    private HashMap<UUID, UUID> pendingInvites = new HashMap<>(); // Invited -> Inviter
    // Moved GroupCheck here so it persists and is accessible by the GUI later
    private HashMap<UUID, ArrayList<UUID>> groupCheck = new HashMap<>(); 
    
    private File clansFile;
    private FileConfiguration clansConfig;
    private SelectionManager selectionManager;
    private ClanGUI clanGUI;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        setupClansFile();
        loadClansFromDisk();

        this.selectionManager = new SelectionManager(this);
        this.clanGUI = new ClanGUI(this);
        getServer().getPluginManager().registerEvents(this.clanGUI, this);
        getServer().getPluginManager().registerEvents(new ProtectionListener(this), this);
        if (getCommand("clan") != null) {
            getCommand("clan").setExecutor(new ClanCommand(this));
        }

        getLogger().info("Bagadbilla's Clan System Loaded Successfully!");
    }

    @Override
    public void onDisable() {
        saveClansToDisk();
    }

    // Helper: Find if a player is in an official clan
    public Clan getClanByPlayer(UUID uuid) {
        for (Clan clan : clans.values()) {
            if (clan.getMembers().contains(uuid)) return clan;
        }
        return null;
    }

    // Helper: Find if a player is in a lobby group
    public UUID getGroupLeader(UUID memberUUID) {
        if (groupCheck.containsKey(memberUUID)) return memberUUID; // They are the leader
        for (HashMap.Entry<UUID, ArrayList<UUID>> entry : groupCheck.entrySet()) {
            if (entry.getValue().contains(memberUUID)) return entry.getKey();
        }
        return null;
    }

    private void setupClansFile() {
        clansFile = new File(getDataFolder(), "clans.yml");
        if (!clansFile.exists()) {
            getDataFolder().mkdirs();
            try { clansFile.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
        clansConfig = YamlConfiguration.loadConfiguration(clansFile);
    }

    public void saveClansToDisk() {
        clansConfig.set("clans", null); 
        for (Clan clan : clans.values()) {
            String path = "clans." + clan.getName();
            clansConfig.set(path + ".leader", clan.getLeader().toString());
            clansConfig.set(path + ".points", clan.getPoints());
            if (clan.getPos1() != null) clansConfig.set(path + ".pos1", clan.getPos1());
            if (clan.getPos2() != null) clansConfig.set(path + ".pos2", clan.getPos2());
            List<String> memberList = clan.getMembers().stream().map(UUID::toString).toList();
            clansConfig.set(path + ".members", memberList);
        }
        try { clansConfig.save(clansFile); } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadClansFromDisk() {
        if (clansConfig.getConfigurationSection("clans") == null) return;
        for (String name : clansConfig.getConfigurationSection("clans").getKeys(false)) {
            String path = "clans." + name;
            UUID leader = UUID.fromString(clansConfig.getString(path + ".leader"));
            Clan clan = new Clan(name, leader);
            clan.setPoints(clansConfig.getInt(path + ".points"));
            if (clansConfig.contains(path + ".pos1")) clan.setPos1(clansConfig.getLocation(path + ".pos1"));
            if (clansConfig.contains(path + ".pos2")) clan.setPos2(clansConfig.getLocation(path + ".pos2"));
            if (clansConfig.contains(path + ".members")) {
                for (String m : clansConfig.getStringList(path + ".members")) {
                    clan.getMembers().add(UUID.fromString(m));
                }
            }
            clans.put(name, clan);
        }
    }

    public HashMap<String, Clan> getClans() { return clans; }
    public HashMap<UUID, UUID> getPendingInvites() { return pendingInvites; }
    public HashMap<UUID, ArrayList<UUID>> getGroupCheck() { return groupCheck; }
    public SelectionManager getSelectionManager() { return selectionManager; }
    public ClanGUI getClanGUI() { return clanGUI; }
}
