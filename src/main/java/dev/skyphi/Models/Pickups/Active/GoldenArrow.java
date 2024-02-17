package dev.skyphi.Models.Pickups.Active;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.SpectralArrow;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

import dev.skyphi.SootCTF;
import dev.skyphi.Models.Pickups.ActivePickup;

public class GoldenArrow extends ActivePickup {
    
    public GoldenArrow() {
        name = ChatColor.GOLD+""+ChatColor.BOLD+"Golden Arrow";
        description = ChatColor.GOLD+"One shot. One opportunity.";
        itemStack = new ItemStack(Material.SPECTRAL_ARROW);
        key = new NamespacedKey(SootCTF.INSTANCE, "GoldenArrow");

        actualItem = new ItemStack(Material.CROSSBOW);
        CrossbowMeta meta = (CrossbowMeta)actualItem.getItemMeta();
        meta.addChargedProjectile(itemStack);
        meta.setDisplayName(ChatColor.GOLD+""+ChatColor.BOLD+"Golden Crossbow");
        meta.setLore(Arrays.asList(description));
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte)1);
        actualItem.setItemMeta(meta);
        init();
    }

    @Override
    public void activate() {
        Vector dir = owner.getEyeLocation().getDirection();
        SpectralArrow projectile = owner.launchProjectile(SpectralArrow.class);
        projectile.setVelocity(dir.multiply(4));

        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1, 1);
        owner.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, owner.getLocation(), 4);
    }

}
