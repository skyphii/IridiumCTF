package dev.skyphi.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FireworkExplodeEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import dev.skyphi.IridiumCTF;
import dev.skyphi.Models.Pickups.Active.FireworkBarrage;

public class FireworkBarrageListener implements Listener {
    
    private static final double RADIUS = 2.0;
    private static final int ARROWS_PER_RING = 32;
    private static final int RINGS = 8;

    @EventHandler
    public void on(FireworkExplodeEvent event) {
        if (!event.getEntity().hasMetadata(FireworkBarrage.METADATA_KEY)) return;

        Firework firework = event.getEntity();

        for (int currentRing = 0; currentRing < RINGS; currentRing++) {
            double currentRadius = RADIUS * (currentRing + 1) / RINGS;

            for (int arrowNum = 0; arrowNum < ARROWS_PER_RING; arrowNum++) {
                double angle = 2 * Math.PI * arrowNum / ARROWS_PER_RING;
                double x = currentRadius * Math.cos(angle);
                double z = currentRadius * Math.sin(angle);

                spawnArrow(firework.getLocation(), new Vector(x, 1.0, z));
            }
        }

        // 4 centre arrows
        Vector velocity = new Vector(0, 1.0, 0);
        spawnArrow(firework.getLocation().add(1, 0, 0), velocity);
        spawnArrow(firework.getLocation().add(0, 0, 1), velocity);
        spawnArrow(firework.getLocation().add(-1, 0, 0), velocity);
        spawnArrow(firework.getLocation().add(0, 0, -1), velocity);
    }

    private void spawnArrow(Location location, Vector velocity) {
        Arrow arrow = (Arrow) location.getWorld().spawnEntity(location, EntityType.ARROW);
        arrow.setVelocity(velocity.normalize().multiply(0.5));
        arrow.setMetadata("barrage", new FixedMetadataValue(IridiumCTF.INSTANCE, true));
    }

    @EventHandler
    public void on(ProjectileHitEvent event) {
        if (!event.getEntity().hasMetadata("barrage")) return;

        Arrow arrow = (Arrow) event.getEntity();

        if (event.getHitBlock() != null) {
            event.setCancelled(true);
            arrow.remove();
            return;
        }
    }

}
