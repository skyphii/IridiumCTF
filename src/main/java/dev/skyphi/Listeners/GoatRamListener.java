package dev.skyphi.Listeners;

import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import dev.skyphi.Models.Pickups.Active.GoatRam;

public class GoatRamListener implements Listener {
    
    @EventHandler
    public void on(EntityPotionEffectEvent event) {
        if (event.getNewEffect() == null || event.getNewEffect().getType() != PotionEffectType.OOZING || event.getEntityType() == EntityType.GOAT || event.getCause() != Cause.AREA_EFFECT_CLOUD) return;
        event.setCancelled(true);

        Entity entity = event.getEntity();

        entity.setVelocity(new Vector(0, GoatRam.RAM_VELOCITY, 0));
        if (entity instanceof LivingEntity)
            ((LivingEntity)entity).damage(GoatRam.RAM_DAMAGE, DamageSource.builder(DamageType.DRY_OUT).build());
    }

}
