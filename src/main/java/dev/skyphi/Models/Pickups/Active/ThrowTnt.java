package dev.skyphi.Models.Pickups.Active;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import dev.skyphi.SootCTF;
import dev.skyphi.Models.Pickups.ActivePickup;

public class ThrowTnt extends ActivePickup {

    public static final double SPEED = 0.8;
    public static final int FUSE_TICKS = 40;

    public ThrowTnt() {
        name = ChatColor.RED+""+ChatColor.BOLD+"Throwable TNT";
        description = ChatColor.RED+"Right click to toss TNT!";
        itemStack = new ItemStack(Material.TNT);
        key = new NamespacedKey(SootCTF.INSTANCE, "TNT");
        init();
    }

    @Override
    public void activate() {
        Location spawnLoc = owner.getEyeLocation();
        Vector dir = spawnLoc.getDirection();
        spawnLoc.add(dir);

        TNTPrimed tnt = (TNTPrimed)owner.getWorld().spawnEntity(spawnLoc, EntityType.PRIMED_TNT);
        tnt.setVelocity(dir.multiply(SPEED));
        tnt.setFuseTicks(FUSE_TICKS);

        owner.getWorld().playSound(spawnLoc, Sound.ENTITY_TNT_PRIMED, 1, 1);
    }
    
}
