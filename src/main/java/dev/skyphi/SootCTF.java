package dev.skyphi;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import dev.skyphi.Commands.ScoreCommand;
import dev.skyphi.Commands.SootCtfCommand;
import dev.skyphi.Listeners.FallListener;
import dev.skyphi.Listeners.FreezeballListener;
import dev.skyphi.Listeners.GoldenArrowListener;
import dev.skyphi.Models.CTFTeam;
import dev.skyphi.Models.Pickups.PickupManager;

public class SootCTF extends JavaPlugin {

    public static SootCTF INSTANCE;
    public static CTFTeam TEAM1, TEAM2;
    public static PickupManager PICKUP_MANAGER = new PickupManager();
    public static Material FLAG_TYPE = Material.IRON_BLOCK, MISSING_FLAG_TYPE = Material.SMOOTH_STONE;

    public static boolean PAIR_NEARBY_PLAYERS, STRICT_PAIRING;
    public static int FLAGS_TO_WIN = 5, RESPAWN_TIMER = 5;

    @Override
    public void onEnable() {
        INSTANCE = this;

        initConfig();
        CTFUtils.initTeams();

        this.getCommand("sootctf").setExecutor(new SootCtfCommand());
        this.getCommand("score").setExecutor(new ScoreCommand());

        SootCTF.INSTANCE.getServer().getPluginManager().registerEvents(new FallListener(), SootCTF.INSTANCE);
        SootCTF.INSTANCE.getServer().getPluginManager().registerEvents(new FreezeballListener(), SootCTF.INSTANCE);
        SootCTF.INSTANCE.getServer().getPluginManager().registerEvents(new GoldenArrowListener(), SootCTF.INSTANCE);
    }

    @Override
    public void onDisable() {
        CTFUtils.stop();
    }

    private void initConfig() {
        saveDefaultConfig();

        FileConfiguration config = getConfig();

        // flag block type
        Material flagMat = Material.getMaterial(config.getString("flag_type"));
        if(flagMat != null) FLAG_TYPE = flagMat;
        Material missingFlagMat = Material.getMaterial(config.getString("missing_flag_type"));
        if(missingFlagMat != null || flagMat == missingFlagMat) MISSING_FLAG_TYPE = missingFlagMat;

        // item spawners
        PICKUP_MANAGER.loadSpawners();

        // configurable options
        PAIR_NEARBY_PLAYERS = config.getBoolean("pair_nearby_players");
        STRICT_PAIRING = config.getBoolean("strict_pairing");
        FLAGS_TO_WIN = config.getInt("flags_to_win");
        RESPAWN_TIMER = config.getInt("respawn_timer");
    }
    
}
