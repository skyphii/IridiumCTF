package dev.skyphi.Listeners;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import dev.skyphi.CTFUtils;
import dev.skyphi.Models.CTFPlayer;
import dev.skyphi.Models.Pickups.Pickup;
import dev.skyphi.Models.Pickups.Simple.MobEgg;

public class ProjectileListener implements Listener {
    
    @EventHandler
    public void on(ProjectileHitEvent event) {
        if(!(event.getEntity().getShooter() instanceof Player)) return;
        
        Player shooter = (Player)event.getEntity().getShooter();
        CTFPlayer ctfp = CTFUtils.getCTFPlayer(shooter);
        if(ctfp == null) return;

        Projectile projectile = event.getEntity();
        
        if(projectile instanceof Arrow) {
            Entity hitEntity = event.getHitEntity();
            if(hitEntity != null) return;

            projectile.remove();
        }else if(projectile instanceof Egg) {
            EntityType[] types = MobEgg.MOB_TYPES;
            EntityType entityType = types[(int)(Math.random() * types.length)];
            LivingEntity entity = (LivingEntity)projectile.getWorld().spawnEntity(projectile.getLocation(), entityType);
            ctfp.getTeam().addMob(entity);
            entity.getPersistentDataContainer().set(Pickup.PICKUP_KEY, PersistentDataType.BYTE, (byte)1);
            
            // if rabbit, turn into killer rabbit
            if(entityType == EntityType.RABBIT) ((Rabbit)entity).setRabbitType(Type.THE_KILLER_BUNNY);

            // add helmet to avoid burning in sun
            ItemStack helmet = MobEgg.HELMET;
            helmet.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 4);
            ItemMeta meta = helmet.getItemMeta();
            meta.setUnbreakable(true);
            helmet.setItemMeta(meta);
            entity.getEquipment().setHelmet(helmet);

            // add team chestplate
            entity.getEquipment().setChestplate(CTFUtils.getTeamChestplate(ctfp.getTeam()));

            event.setCancelled(true);
            projectile.remove();
        }
    }

}
