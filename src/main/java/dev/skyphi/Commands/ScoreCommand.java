package dev.skyphi.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import dev.skyphi.IridiumCTF;
import dev.skyphi.Models.CTFTeam;

public class ScoreCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        CTFTeam team1 = IridiumCTF.TEAM1;
        CTFTeam team2 = IridiumCTF.TEAM2;

        sender.sendMessage(ChatColor.BLUE+""+ChatColor.BOLD + team1.getName()+" "
                        + ChatColor.GOLD+""+ChatColor.BOLD
                        + team1.getScore() + " : " + team2.getScore() 
                        + ChatColor.RED+" "+ChatColor.BOLD + team2.getName());

        return true;
    }

}
