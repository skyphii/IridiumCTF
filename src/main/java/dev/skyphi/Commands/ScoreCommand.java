package dev.skyphi.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import dev.skyphi.SootCTF;
import dev.skyphi.Models.CTFTeam;

public class ScoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }
        Player player = (Player)sender;

        // if(!player.hasPermission("sootctf.score")) {
        //     player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
        //     return true;
        // }

        CTFTeam team1 = SootCTF.TEAM1;
        CTFTeam team2 = SootCTF.TEAM2;

        player.sendMessage(ChatColor.BLUE+""+ChatColor.BOLD+team1.getName()+" "
                        + ChatColor.GOLD+""+ChatColor.BOLD + team1.getScore()
                        + " : " + team2.getScore() + ChatColor.RED+" "+ChatColor.BOLD+team2.getName());

        return true;
    }

}
