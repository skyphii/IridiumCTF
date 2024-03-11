package dev.skyphi.Listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

public class MobSpawnListener implements Listener {
    
    @EventHandler
    public void on(CreatureSpawnEvent event) {
        if(event.getEntityType() == EntityType.CHICKEN && event.getSpawnReason() == SpawnReason.EGG) {
            event.setCancelled(true);
        }
    }

}
