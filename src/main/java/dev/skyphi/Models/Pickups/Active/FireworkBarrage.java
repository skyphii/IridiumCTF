package dev.skyphi.Models.Pickups.Active;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.metadata.FixedMetadataValue;

import dev.skyphi.IridiumCTF;
import dev.skyphi.Models.Pickups.ActivePickup;

public class FireworkBarrage extends ActivePickup {

    public static String METADATA_KEY = "barrage";

    public FireworkBarrage() {
        name = ChatColor.RED+""+ChatColor.BOLD+"Firework Barrage!";
        description = ChatColor.RED+"Right click to " + ChatColor.ITALIC + "rain hellfire";
        itemStack = new ItemStack(Material.FIREWORK_ROCKET);
        key = new NamespacedKey(IridiumCTF.INSTANCE, "Firework");
        init();
    }

    @Override
    public void activate() {
        Firework firework = (Firework) owner.getWorld().spawnEntity(owner.getLocation().add(0, 6, 0), EntityType.FIREWORK_ROCKET);
        firework.setMetadata(METADATA_KEY, new FixedMetadataValue(IridiumCTF.INSTANCE, true));
        firework.setMaxLife(20*3);

        FireworkMeta meta = firework.getFireworkMeta();
        meta.addEffect(FireworkEffect.builder()
            .with(Type.BURST)
            .withColor(Color.GRAY)
            .build());
        firework.setFireworkMeta(meta);
    }
    
}
