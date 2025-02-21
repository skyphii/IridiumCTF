package dev.skyphi.Models.Pickups.Active;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Goat;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import dev.skyphi.CTFUtils;
import dev.skyphi.IridiumCTF;
import dev.skyphi.Models.Pickups.ActivePickup;

public class GoatRam extends ActivePickup {

    public static final float RAM_VELOCITY = 1.65f;
    public static final double RAM_DAMAGE = 3.0;

    public GoatRam() {
        name = ChatColor.GRAY+""+ChatColor.BOLD+"Goat";
        description = ChatColor.AQUA+"Right click to " + ChatColor.ITALIC + "goat";

        itemStack = new ItemStack(Material.GOAT_HORN, 1);
        MusicInstrumentMeta meta = (MusicInstrumentMeta)itemStack.getItemMeta();
        meta.setInstrument(MusicInstrument.DREAM_GOAT_HORN);
        itemStack.setItemMeta(meta);

        key = new NamespacedKey(IridiumCTF.INSTANCE, "Goat");
        init();
    }

    @Override
    public void activate() {
        Goat goat = (Goat)owner.getWorld().spawnEntity(owner.getLocation().add(0, 2, 0), EntityType.GOAT);
        goat.setInvulnerable(true);
        goat.setCustomName(CTFUtils.getTeamChatColour(CTFUtils.getCTFPlayer(owner).getTeam())+""+ChatColor.BOLD + owner.getName() + "'s GOAT");
        goat.setCustomNameVisible(true);

        AreaEffectCloud cloud = (AreaEffectCloud)owner.getWorld().spawnEntity(goat.getLocation(), EntityType.AREA_EFFECT_CLOUD);
        cloud.setBasePotionType(PotionType.OOZING);
        cloud.setWaitTime(0);
        cloud.setReapplicationDelay(40);
        cloud.setParticle(Particle.BLOCK, Material.AIR.createBlockData());

        Vector dir = owner.getLocation().getDirection();
        goat.getLocation().setDirection(dir);
        goat.setVelocity(dir.multiply(1.5));

        BukkitRunnable cloudRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                cloud.teleport(goat);
            }
        };
        cloudRunnable.runTaskTimer(IridiumCTF.INSTANCE, 1, 1);
        BukkitRunnable speedRunnable = new BukkitRunnable() {
            @Override
            public void run() {
                goat.setVelocity(dir);
            }
        };
        speedRunnable.runTaskTimer(IridiumCTF.INSTANCE, 10, 2);
        
        owner.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, owner.getLocation(), 4);

        new BukkitRunnable() {
            @Override
            public void run() {
                cloudRunnable.cancel();
                speedRunnable.cancel();
                cloud.remove();
                goat.remove();
            }
        }.runTaskLater(IridiumCTF.INSTANCE, 20*3);
    }
    
}
