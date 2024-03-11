package dev.skyphi.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;

import dev.skyphi.CTFUtils;

public class GoldenArrowListener implements Listener {
    
    @EventHandler
    public void on(ProjectileHitEvent event) {
        if(event.getEntityType() != EntityType.SPECTRAL_ARROW || !(event.getEntity().getShooter() instanceof Player)) return;
        if(event.getHitEntity() == null || !(event.getHitEntity() instanceof LivingEntity)) {
            event.getEntity().remove();
            return;
        }

        final Player shooter = (Player)event.getEntity().getShooter();
        final LivingEntity hitEntity = (LivingEntity)event.getHitEntity();
        if(hitEntity == null) return;

        event.setCancelled(true);

        hitEntity.damage(1000, shooter);

        if(hitEntity instanceof Player) {
            final Player hitPlayer = (Player)hitEntity;
            
            ChatColor shooterColour = CTFUtils.getTeamChatColour(CTFUtils.getCTFPlayer(shooter).getTeam());
            ChatColor deadColour = CTFUtils.getTeamChatColour(CTFUtils.getCTFPlayer(hitPlayer).getTeam());

            CTFUtils.broadcast(shooterColour+""+ChatColor.BOLD+shooter.getName() + ChatColor.AQUA+" one-shot "
                            + deadColour+""+ChatColor.BOLD+hitPlayer.getName()
                            + ChatColor.AQUA+" with a "
                            + ChatColor.GOLD+ChatColor.BOLD + "Golden Arrow"
                            + ChatColor.AQUA+"!"
                            , false);
        }
    }

}
