package dev.skyphi.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.persistence.PersistentDataType;

import dev.skyphi.Models.Pickups.Pickup;

public class MobDeathListener implements Listener {
    
    @EventHandler
    public void on(EntityDeathEvent event) {
        if(!event.getEntity().getPersistentDataContainer().has(Pickup.PICKUP_KEY, PersistentDataType.BYTE)) return;

        event.setDroppedExp(0);
        event.getDrops().clear();
    }

}
