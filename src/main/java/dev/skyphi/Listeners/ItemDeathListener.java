package dev.skyphi.Listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ItemDeathListener implements Listener {
    
    @EventHandler
    public void on(EntityDamageByEntityEvent event) {
        if(event.getEntityType() != EntityType.DROPPED_ITEM) return;

        event.setCancelled(true);
    }

}
