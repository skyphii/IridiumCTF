package dev.skyphi.Listeners;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.util.Vector;

public class ExplosionListener implements Listener {
    
    @EventHandler
    public void on(EntityExplodeEvent event) {
        if(event.getEntityType() != EntityType.PRIMED_TNT && event.getEntityType() != EntityType.CREEPER) return;
        
        // clear blocks so none are destroyed
        event.blockList().clear();

        // prevent items being blown away - Pickups have no gravity so they would float off into the sunset
        List<Entity> items = event.getEntity().getNearbyEntities(10, 10, 10);
        items.removeIf(e -> e.getType() != EntityType.DROPPED_ITEM);
        
        for(Entity e : items) {
            e.setVelocity(new Vector());
        }
    }

}
