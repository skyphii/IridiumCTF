package dev.skyphi.Models.Pickups.Active;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import dev.skyphi.IridiumCTF;
import dev.skyphi.Models.Pickups.ActivePickup;


//TODO catch event of hook hitting ctfplayer, set their velocity towards owner + up
public class RailgunRod extends ActivePickup {
    
    public RailgunRod() {
        name = ChatColor.BLUE+""+ChatColor.BOLD+"Railgun Rod!";
        description = ChatColor.AQUA+"Right click to shoot a hook and pull whoever is hit! 1 hit or 3 misses will consume the rod.";
        itemStack = new ItemStack(Material.FISHING_ROD);
        itemStack.addEnchantment(Enchantment.BINDING_CURSE, 1);
        key = new NamespacedKey(IridiumCTF.INSTANCE, "RailRod");
        init();
    }

    @Override
    public void activate() {
        FishHook hook = (FishHook)owner.getWorld().spawnEntity(owner.getLocation(), EntityType.FISHING_BOBBER);
        Vector dir = owner.getEyeLocation().getDirection();
        hook.setVelocity(dir.multiply(10));

        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_FISHING_BOBBER_THROW, 1, 1);
        owner.getWorld().spawnParticle(Particle.FISHING, owner.getLocation(), 4);
    }

}
