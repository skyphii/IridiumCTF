package dev.skyphi.Listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import dev.skyphi.CTFUtils;
import dev.skyphi.Models.CTFPlayer;

public class ProjectileListener implements Listener {
    
    @EventHandler
    public void on(ProjectileHitEvent event) {
        if(!(event.getEntity().getShooter() instanceof Player)) return;
        
        Player shooter = (Player)event.getEntity().getShooter();
        CTFPlayer ctfp = CTFUtils.getCTFPlayer(shooter);
        if(ctfp == null) return;

        Entity hitEntity = event.getHitEntity();
        if(hitEntity != null) return;

        Projectile projectile = event.getEntity();
        if(projectile instanceof Arrow) {
            projectile.remove();
        }
    }

}
