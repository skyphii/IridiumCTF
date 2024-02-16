package dev.skyphi.Models.Pickups.Simple;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import dev.skyphi.Models.Pickups.Pickup;

public class Arrows extends Pickup {
    
    public Arrows() {
        name = ChatColor.GRAY+""+ChatColor.BOLD+"Slowness Arrow";
        description = ChatColor.GOLD+"Pew pew. Look how slow they are!";
        itemStack = new ItemStack(Material.TIPPED_ARROW, 4);
        PotionMeta meta = (PotionMeta)itemStack.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.SLOWNESS));
        itemStack.setItemMeta(meta);
        init();
    }

}
