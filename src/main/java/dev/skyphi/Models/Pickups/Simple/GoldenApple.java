package dev.skyphi.Models.Pickups.Simple;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import dev.skyphi.Models.Pickups.Pickup;

public class GoldenApple extends Pickup {
    
    public GoldenApple() {
        name = ChatColor.GOLD+""+ChatColor.BOLD+"Golden Apple";
        description = ChatColor.GOLD+"Om nom.";
        itemStack = new ItemStack(Material.GOLDEN_APPLE);
        init();
    }

}
