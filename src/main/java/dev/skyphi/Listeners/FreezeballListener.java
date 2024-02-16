package dev.skyphi.Listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import dev.skyphi.CTFUtils;
import dev.skyphi.SootCTF;
import dev.skyphi.Models.CTFPlayer;

public class FreezeballListener implements Listener {

    @EventHandler
    public void on(ProjectileHitEvent event) {
        if(event.getEntityType() != EntityType.SNOWBALL || !(event.getEntity().getShooter() instanceof Player)) return;
        if(!(event.getHitEntity() instanceof LivingEntity)) return;

        final LivingEntity hitEntity = (LivingEntity)event.getHitEntity();
        if(hitEntity == null) return;

        event.setCancelled(true);

        if(hitEntity instanceof Player) {
            CTFPlayer ctfp = CTFUtils.getCTFPlayer((Player)hitEntity);
            CTFPlayer shooterCtfp = CTFUtils.getCTFPlayer((Player)event.getEntity().getShooter());
            if(ctfp.getTeam().equals(shooterCtfp.getTeam())) return;
        }

        final BukkitRunnable hurtRunnable = new BukkitRunnable() {
			@Override
			public void run() {
				hitEntity.damage(0.1);
                hitEntity.setFreezeTicks(160);
                hitEntity.setNoDamageTicks(0);
			}
        };
        hurtRunnable.runTaskTimer(SootCTF.INSTANCE, 0, 2);

        new BukkitRunnable() {
			@Override
			public void run() {
				hurtRunnable.cancel();
			}
        }.runTaskLater(SootCTF.INSTANCE, 16);
    }

}
