package dev.skyphi.Models.Pickups.Active;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import dev.skyphi.IridiumCTF;
import dev.skyphi.Listeners.FallListener;
import dev.skyphi.Models.Pickups.ActivePickup;

public class JumpBoost extends ActivePickup {

    private static final Vector JUMP = new Vector(0, 1.2, 0);

    public JumpBoost() {
        name = ChatColor.AQUA+""+ChatColor.BOLD+"Super Jump!";
        description = ChatColor.AQUA+"Right click to jump high! You will not take fall damage.";
        itemStack = new ItemStack(Material.FEATHER);
        key = new NamespacedKey(IridiumCTF.INSTANCE, "JumpBoost");
        init();
    }

    @Override
    public void activate() {
        FallListener.addPlayer(owner);
        owner.setVelocity(JUMP);
        owner.getWorld().playSound(owner.getLocation(), Sound.ENTITY_ENDER_DRAGON_FLAP, 1, 1);
        owner.getWorld().spawnParticle(Particle.CLOUD, owner.getLocation(), 10);
    }
    
}
