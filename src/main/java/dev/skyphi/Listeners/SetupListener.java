package dev.skyphi.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import dev.skyphi.IridiumCTF;
import dev.skyphi.Models.CTFConfig;
import dev.skyphi.Models.Pickups.ItemSpawner;

public class SetupListener implements Listener {
    
    public enum SetupType {
        FLAG_1,
        FLAG_2,
        SPAWNER,
        REMOVE_SPAWNER
    };

    private Player player;
    private SetupType setupType;

    public SetupListener(Player player, SetupType setupType) {
        this.player = player;
        this.setupType = setupType;

        IridiumCTF.INSTANCE.getServer().getPluginManager().registerEvents(this, IridiumCTF.INSTANCE);
    }

    public Player getPlayer() { return player; }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null || setupType == null || player == null || !player.equals(event.getPlayer())) return;
        
        event.setCancelled(true);

        if (setupType == SetupType.SPAWNER) {
            // set item spawner
            IridiumCTF.PICKUP_MANAGER.addSpawner(new ItemSpawner(block));
            player.sendMessage(ChatColor.AQUA + "Item spawner set!");
        } else if (setupType == SetupType.REMOVE_SPAWNER) {
            boolean removed = IridiumCTF.PICKUP_MANAGER.removeSpawner(block);
            String msg = removed ? "Item spawner removed" : "No item spawner found at this location";
            player.sendMessage(ChatColor.AQUA + msg);
        } else {
            Location newFlagLoc = block.getLocation();

            // set flag block on appropriate team
            (setupType == SetupType.FLAG_1 ? IridiumCTF.TEAM1 : IridiumCTF.TEAM2).setFlag(block);

            player.sendMessage(ChatColor.AQUA + "Flag set for team "
                            + (setupType == SetupType.FLAG_1 ? "one" : "two")
                            + "!");
            
            // save to config
            if (setupType == SetupType.FLAG_1) CTFConfig.FLAG_ONE = newFlagLoc;
            else if (setupType == SetupType.FLAG_2) CTFConfig.FLAG_TWO = newFlagLoc;
            CTFConfig.save();
        }

        HandlerList.unregisterAll(this);
    }

}
