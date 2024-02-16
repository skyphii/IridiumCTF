package dev.skyphi.Models.Pickups.Simple;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import dev.skyphi.Models.Pickups.Pickup;

public class Freezeball extends Pickup {

    public Freezeball() {
        name = ChatColor.AQUA+""+ChatColor.BOLD+"Freezeball";
        description = ChatColor.AQUA+"Are there chunks of ice in this snowball?";
        itemStack = new ItemStack(Material.SNOWBALL, 4);
        init();
    }

}
