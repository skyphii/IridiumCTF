package dev.skyphi;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import dev.skyphi.Listeners.FlagListener;
import dev.skyphi.Models.CTFPlayer;
import dev.skyphi.Models.CTFTeam;

public class CTFUtils {

    private static final String PREFIX = ChatColor.GOLD+""+ChatColor.BOLD+"["
                                        +ChatColor.AQUA+""+ChatColor.BOLD+"SootCTF"
                                        +ChatColor.GOLD+""+ChatColor.BOLD+"]"
                                        +ChatColor.AQUA+" ";

    public static FlagListener FLAG_LISTENER;
    
    public static CTFPlayer getCTFPlayer(Player player) {
        CTFPlayer ctfp = SootCTF.TEAM1.getCtfPlayer(player);
        if(ctfp != null) return ctfp;
        return SootCTF.TEAM2.getCtfPlayer(player);
    }

    public static void broadcast(String msg) {
        SootCTF.INSTANCE.getServer().broadcastMessage(PREFIX + msg);
    }

    public static void showScore(CTFPlayer flagCarrier) {
        String scoreLine = getTeamChatColour(SootCTF.TEAM1)+""+ChatColor.BOLD + SootCTF.TEAM1.getScore()
                        + ChatColor.GOLD+""+ChatColor.BOLD + " : "
                        + getTeamChatColour(SootCTF.TEAM2)+""+ChatColor.BOLD + SootCTF.TEAM2.getScore();
        String subtitle = ChatColor.GOLD+""+ChatColor.BOLD + flagCarrier.getPlayerName()
                        + ChatColor.GOLD+" captured the flag!";
        for(Player player : SootCTF.INSTANCE.getServer().getOnlinePlayers()) {
            player.sendTitle(scoreLine, subtitle, -1, -1, -1);
        }
    }

    public static void announceWinner(CTFTeam winner) {
        String title = getTeamChatColour(winner)+""+ChatColor.BOLD + SootCTF.TEAM1.getName() + " wins!";
        String subtitle = getTeamChatColour(SootCTF.TEAM1)+""+ChatColor.BOLD + SootCTF.TEAM1.getScore()
                        + ChatColor.GOLD+""+ChatColor.BOLD + " : "
                        + getTeamChatColour(SootCTF.TEAM2)+""+ChatColor.BOLD + SootCTF.TEAM2.getScore();
        for(Player player : SootCTF.INSTANCE.getServer().getOnlinePlayers()) {
            player.sendTitle(title, subtitle, -1, -1, -1);
            player.sendMessage(getTeamChatColour(winner)+""+ChatColor.BOLD + SootCTF.TEAM1.getName() + " wins!");
        }
    }

    public static ChatColor getTeamChatColour(CTFTeam team) {
        ChatColor teamColour = ChatColor.GRAY;
        String n = team.getName().toLowerCase();
        if(n.contains("blue")) teamColour = ChatColor.BLUE;
        else if(n.contains("red")) teamColour = ChatColor.RED;
        else if(n.contains("yellow")) teamColour = ChatColor.YELLOW;
        else if(n.contains("purple")) teamColour = ChatColor.LIGHT_PURPLE;
        else if(n.contains("green")) teamColour = ChatColor.GREEN;
        return teamColour;
    }

    public static void teleportTeamsToWorldSpawn() {
        SootCTF.TEAM1.teleportWorldSpawn();
        SootCTF.TEAM2.teleportWorldSpawn();
    }

    public static void stop() {
        HandlerList.unregisterAll(FLAG_LISTENER);
        FLAG_LISTENER = null;
    }

}
