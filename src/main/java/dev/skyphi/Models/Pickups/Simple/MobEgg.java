package dev.skyphi.Models.Pickups.Simple;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import dev.skyphi.Models.Pickups.Pickup;

public class MobEgg extends Pickup {
    
    public static final EntityType[] MOB_TYPES = {
        EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER, EntityType.GUARDIAN,
        EntityType.HUSK, EntityType.WITHER_SKELETON, EntityType.RABBIT
    };
    public static final ItemStack HELMET = new ItemStack(Material.GOLDEN_HELMET);

    public MobEgg() {
        name = ChatColor.GREEN+""+ChatColor.BOLD+"Mob Egg";
        description = ChatColor.GREEN+"I wouldn't fry this egg...";
        itemStack = new ItemStack(Material.EGG);
        init();
    }

    // uses ProjectileListener to spawn mob when egg lands

}
