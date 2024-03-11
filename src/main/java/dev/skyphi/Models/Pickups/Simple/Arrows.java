package dev.skyphi.Models.Pickups.Simple;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import dev.skyphi.Models.Pickups.Pickup;

public class Arrows extends Pickup {
    
    public Arrows() {
        name = ChatColor.WHITE+""+ChatColor.BOLD+"Arrow";
        description = ChatColor.GRAY+"Pew pew.";
        itemStack = new ItemStack(Material.ARROW, 4);
        init();
    }

}
