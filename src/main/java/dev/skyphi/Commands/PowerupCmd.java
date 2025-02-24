package dev.skyphi.Commands;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import dev.skyphi.CTFUtils;
import dev.skyphi.IridiumCTF;
import dev.skyphi.Models.CTFPlayer;
import dev.skyphi.Models.Pickups.ActivePickup;
import dev.skyphi.Models.Pickups.Pickup;

public class PowerupCmd implements CommandExecutor, TabCompleter {
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }

        Player player = (Player)sender;
        if (!player.hasPermission("iridiumctf.powerup")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        CTFPlayer ctfp = CTFUtils.getCTFPlayer(player);
        if (ctfp == null) {
            player.sendMessage(ChatColor.RED + "You must be on a team in an active game to give a powerup.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /powerup <powerup_name>");
            return true;
        }

        String name = args[0];
        Pickup pickup = null;
        try {
            List<Class<? extends Pickup>> pickupClasses = IridiumCTF.PICKUP_MANAGER.getAllPickupClasses();
            Class<? extends Pickup> cp = pickupClasses.stream().filter(p -> p.getTypeName().contains(name)).findFirst().get();
            pickup = cp.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
            player.sendMessage(ChatColor.RED + "Powerup '" + name + "' not found.");
            return true;
        }

        if (pickup instanceof ActivePickup) {
            IridiumCTF.PICKUP_MANAGER.addActivePickup((ActivePickup)pickup);
        }

        pickup.setOwner(player);

        player.getInventory().addItem(pickup.getActualItem());

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdLbl, String[] args) {
        final List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            StringUtil.copyPartialMatches(args[0], IridiumCTF.PICKUP_MANAGER.getAllPickupClasses().stream().map(Class::getSimpleName).toList(), completions);
        }
        return completions;
    }
}
