package dev.skyphi.Models.Pickups.Simple;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import dev.skyphi.Models.Pickups.Pickup;

public class WindCharges extends Pickup {
    
    public WindCharges() {
        name = ChatColor.GRAY+""+ChatColor.BOLD+"Wind Charge";
        description = ChatColor.DARK_GRAY+"*wind noises*";
        itemStack = new ItemStack(Material.WIND_CHARGE, 4);
        init();
    }

}
