package dev.skyphi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import dev.skyphi.Listeners.DeathListener;
import dev.skyphi.Listeners.FlagListener;
import dev.skyphi.Models.CTFPlayer;
import dev.skyphi.Models.CTFTeam;

// a class that lets me be lazy with organizing code (so I can rush this out faster and make it better later)
public class CTFUtils {

    private static final String PREFIX = ChatColor.GOLD+""+ChatColor.BOLD+"["
                                        +ChatColor.AQUA+""+ChatColor.BOLD+"SootCTF"
                                        +ChatColor.GOLD+""+ChatColor.BOLD+"]"
                                        +ChatColor.AQUA+" ";

    public static FlagListener FLAG_LISTENER;
    public static DeathListener DEATH_LISTENER;

    public static ScoreboardManager BOARD_MANAGER = Bukkit.getScoreboardManager();
    public static Scoreboard board = BOARD_MANAGER.getNewScoreboard();

    public static void init() {
        board.registerNewTeam("1");
        // board.getTeam("1").setColor();

        board.registerNewTeam("2");
    }
    
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

    public static Material getTeamWool(CTFTeam team) {
        Material teamWool = Material.GRAY_WOOL;
        String n = team.getName().toLowerCase();
        if(n.contains("blue")) teamWool = Material.BLUE_WOOL;
        else if(n.contains("red")) teamWool = Material.RED_WOOL;
        else if(n.contains("yellow")) teamWool = Material.YELLOW_WOOL;
        else if(n.contains("purple")) teamWool = Material.PURPLE_WOOL;
        else if(n.contains("green")) teamWool = Material.LIME_WOOL;
        return teamWool;
    }

    public static void teleportTeamsToWorldSpawn() {
        SootCTF.TEAM1.teleportWorldSpawn();
        SootCTF.TEAM2.teleportWorldSpawn();
    }

    public static void stop() {
        HandlerList.unregisterAll(FLAG_LISTENER);
        FLAG_LISTENER = null;
    }

    public static void initTeams() {
        FileConfiguration config = SootCTF.INSTANCE.getConfig();

        Location loc1 = config.getLocation("teams.one.flag");
        Block block1 = loc1 != null ? loc1.getBlock() : null;
        SootCTF.TEAM1 = new CTFTeam(config.getString("teams.one.name"), block1);
        SootCTF.TEAM1.setMcTeam(board.getTeam("1"));

        Location loc2 = config.getLocation("teams.two.flag");
        Block block2 = loc2 != null ? loc2.getBlock() : null;
        SootCTF.TEAM2 = new CTFTeam(config.getString("teams.two.name"), block2);
        SootCTF.TEAM2.setMcTeam(board.getTeam("2"));
    }

}
