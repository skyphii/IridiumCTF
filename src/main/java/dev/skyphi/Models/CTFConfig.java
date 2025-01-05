package dev.skyphi.Models;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import dev.skyphi.IridiumCTF;
import dev.skyphi.Models.Pickups.ItemSpawner;

public class CTFConfig {
    
    public static boolean PAIR_NEARBY_PLAYERS = true;
    public static boolean STRICT_PAIRING;

    public static int FLAGS_TO_WIN = 5;
    public static int RESPAWN_TIMER = 5;
    public static int ITEM_SPAWN_RATE = 20;

    public static Material FLAG_TYPE = Material.IRON_BLOCK;
    public static Material MISSING_FLAG_TYPE = Material.SMOOTH_STONE;

    public static String TEAM_ONE_NAME, TEAM_TWO_NAME;
    public static Location FLAG_ONE, FLAG_TWO;

    public static void load() {
        IridiumCTF.INSTANCE.saveDefaultConfig();

        FileConfiguration config = IridiumCTF.INSTANCE.getConfig();

        // flag block type
        Material flagMat = Material.getMaterial(config.getString("flag_type"));
        if(flagMat != null) FLAG_TYPE = flagMat;
        Material missingFlagMat = Material.getMaterial(config.getString("missing_flag_type"));
        if(missingFlagMat != null || flagMat == missingFlagMat) MISSING_FLAG_TYPE = missingFlagMat;

        // teams
        TEAM_ONE_NAME = config.getString("teams.one.name");
        TEAM_TWO_NAME = config.getString("teams.two.name");
        FLAG_ONE = config.getLocation("teams.one.flag");
        FLAG_TWO = config.getLocation("teams.two.flag");

        // item spawners
        IridiumCTF.PICKUP_MANAGER.loadSpawners();
        IridiumCTF.PICKUP_MANAGER.setItemSpawnRate(config.getInt("item_spawn_rate"));

        // configurable options
        PAIR_NEARBY_PLAYERS = config.getBoolean("pair_nearby_players");
        STRICT_PAIRING = config.getBoolean("strict_pairing");
        FLAGS_TO_WIN = config.getInt("flags_to_win");
        RESPAWN_TIMER = config.getInt("respawn_timer");
    }

    public static void save() {
        FileConfiguration config = IridiumCTF.INSTANCE.getConfig();

        // captures required to win
        config.set("flags_to_win", FLAGS_TO_WIN);
        
        // flag locations
        config.set("teams.one.flag", FLAG_ONE);
        config.set("teams.two.flag", FLAG_TWO);

        // item spawnrate
        config.set("item_spawn_rate", ITEM_SPAWN_RATE);

        IridiumCTF.INSTANCE.saveConfig();
    }


    public static void saveItemSpawners(List<ItemSpawner> itemSpawners) {
        FileConfiguration config = IridiumCTF.INSTANCE.getConfig();
        for(int i = 0; i < itemSpawners.size(); i++) {
            config.set("item_spawners."+i, itemSpawners.get(i).getBlock().getLocation());
        }
        IridiumCTF.INSTANCE.saveConfig();
    }

}
