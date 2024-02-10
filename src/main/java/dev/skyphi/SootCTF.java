package dev.skyphi;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import dev.skyphi.Commands.ScoreCommand;
import dev.skyphi.Commands.SootCtfCommand;
import dev.skyphi.Models.CTFTeam;

public class SootCTF extends JavaPlugin {

    public static SootCTF INSTANCE;
    public static CTFTeam TEAM1, TEAM2;
    public static Material FLAG_TYPE = Material.IRON_BLOCK;

    public static boolean PAIR_NEARBY_PLAYERS, STRICT_PAIRING;
    public static int FLAGS_TO_WIN = 5;

    @Override
    public void onEnable() {
        INSTANCE = this;

        initConfig();

        Location loc1 = getConfig().getLocation("teams.one.flag");
        Block block1 = loc1 != null ? loc1.getBlock() : null;
        TEAM1 = new CTFTeam(getConfig().getString("teams.one.name"), block1);

        Location loc2 = getConfig().getLocation("teams.two.flag");
        Block block2 = loc2 != null ? loc2.getBlock() : null;
        TEAM2 = new CTFTeam(getConfig().getString("teams.two.name"), block2);

        this.getCommand("sootctf").setExecutor(new SootCtfCommand());
        this.getCommand("score").setExecutor(new ScoreCommand());
    }

    @Override
    public void onDisable() {
        
    }

    private void initConfig() {
        saveDefaultConfig();

        FileConfiguration config = getConfig();

        // flag block type
        Material configMat = Material.getMaterial(config.getString("flag_type"));
        if(configMat != null) FLAG_TYPE = configMat;

        PAIR_NEARBY_PLAYERS = config.getBoolean("pair_nearby_players");
        STRICT_PAIRING = config.getBoolean("strict_pairing");
        FLAGS_TO_WIN = config.getInt("flags_to_win");
    }
    
}
