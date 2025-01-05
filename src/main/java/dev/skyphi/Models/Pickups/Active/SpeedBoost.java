package dev.skyphi.Models.Pickups.Active;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dev.skyphi.IridiumCTF;
import dev.skyphi.Listeners.FallListener;
import dev.skyphi.Models.Pickups.ActivePickup;

public class SpeedBoost extends ActivePickup {

    public SpeedBoost() {
        name = ChatColor.AQUA+""+ChatColor.BOLD+"Zoom!";
        description = ChatColor.AQUA+"Right click to " + ChatColor.ITALIC + "zoom! ";
        itemStack = new ItemStack(Material.SUGAR, 3);
        key = new NamespacedKey(IridiumCTF.INSTANCE, "SpeedBoost");
        init();
    }

    @Override
    public void activate() {
        FallListener.addPlayer(owner);
        owner.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20*2, 3));
        owner.getWorld().playSound(owner.getLocation(), Sound.ITEM_TRIDENT_RIPTIDE_3, 1, 1);
        owner.getWorld().spawnParticle(Particle.ELECTRIC_SPARK, owner.getLocation(), 4);
    }
    
}
