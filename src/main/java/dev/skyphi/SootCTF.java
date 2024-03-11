package dev.skyphi;

import org.bukkit.plugin.java.JavaPlugin;

import dev.skyphi.Commands.ScoreCommand;
import dev.skyphi.Commands.SootCtfCommand;
import dev.skyphi.Listeners.ExplosionListener;
import dev.skyphi.Listeners.FallListener;
import dev.skyphi.Listeners.FreezeballListener;
import dev.skyphi.Listeners.GoldenArrowListener;
import dev.skyphi.Listeners.ItemDeathListener;
import dev.skyphi.Listeners.MobDeathListener;
import dev.skyphi.Listeners.PlayerLeaveListener;
import dev.skyphi.Listeners.ProjectileListener;
import dev.skyphi.Models.CTFConfig;
import dev.skyphi.Models.CTFTeam;
import dev.skyphi.Models.Pickups.PickupManager;

public class SootCTF extends JavaPlugin {

    public static SootCTF INSTANCE;
    public static CTFTeam TEAM1, TEAM2;
    public static PickupManager PICKUP_MANAGER = new PickupManager();

    @Override
    public void onEnable() {
        INSTANCE = this;

        CTFConfig.load();
        
        CTFUtils.stop();
        CTFUtils.initTeams();

        this.getCommand("sootctf").setExecutor(new SootCtfCommand());
        this.getCommand("score").setExecutor(new ScoreCommand());

        getServer().getPluginManager().registerEvents(new FallListener(), SootCTF.INSTANCE);
        getServer().getPluginManager().registerEvents(new FreezeballListener(), SootCTF.INSTANCE);
        getServer().getPluginManager().registerEvents(new GoldenArrowListener(), SootCTF.INSTANCE);
        getServer().getPluginManager().registerEvents(new PlayerLeaveListener(), SootCTF.INSTANCE);
        getServer().getPluginManager().registerEvents(new ProjectileListener(), SootCTF.INSTANCE);
        getServer().getPluginManager().registerEvents(new ExplosionListener(), SootCTF.INSTANCE);
        getServer().getPluginManager().registerEvents(new ItemDeathListener(), SootCTF.INSTANCE);
        getServer().getPluginManager().registerEvents(new MobDeathListener(), SootCTF.INSTANCE);
    }

    @Override
    public void onDisable() {
        CTFUtils.stop();
    }
    
}
