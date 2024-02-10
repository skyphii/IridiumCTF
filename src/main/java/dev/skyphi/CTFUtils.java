package dev.skyphi;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import dev.skyphi.Models.CTFPlayer;

public class CTFUtils {

    private static final String PREFIX = ChatColor.GOLD+""+ChatColor.BOLD+"["
                                        +ChatColor.AQUA+""+ChatColor.BOLD+"SootCTF"
                                        +ChatColor.GOLD+""+ChatColor.GOLD+"]"
                                        +ChatColor.AQUA+" ";
    
    public static CTFPlayer getCTFPlayer(Player player) {
        CTFPlayer ctfp = SootCTF.TEAM1.getCtfPlayer(player);
        if(ctfp != null) return ctfp;
        return SootCTF.TEAM2.getCtfPlayer(player);
    }

    public static void broadcast(String msg) {
        SootCTF.INSTANCE.getServer().broadcastMessage(PREFIX + msg);
    }

    public static void showScore(CTFPlayer flagCarrier) {
        String scoreLine = ChatColor.BLUE+""+ChatColor.BOLD + SootCTF.TEAM1.getScore()
                        + ChatColor.GOLD+""+ChatColor.BOLD + " : "
                        + ChatColor.RED+""+ChatColor.BOLD + SootCTF.TEAM2.getScore();
        String subtitle = ChatColor.GOLD+""+ChatColor.BOLD + flagCarrier.getPlayerName()
                        + ChatColor.GOLD+" captured the flag!";
        for(Player player : SootCTF.INSTANCE.getServer().getOnlinePlayers()) {
            player.sendTitle(scoreLine, subtitle, -1, -1, -1);
        }
    }

}
