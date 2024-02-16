package dev.skyphi;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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
    public static Scoreboard SCOREBOARD = Bukkit.getScoreboardManager().getMainScoreboard();
    
    public static CTFPlayer getCTFPlayer(Player player) {
        CTFPlayer ctfp = SootCTF.TEAM1.getCtfPlayer(player);
        if(ctfp != null) return ctfp;
        return SootCTF.TEAM2.getCtfPlayer(player);
    }

    public static void broadcast(String msg, boolean usePrefix) {
        SootCTF.INSTANCE.getServer().broadcastMessage((usePrefix?PREFIX:ChatColor.AQUA) + msg);
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
        CTFTeam loser = winner.equals(SootCTF.TEAM1) ? SootCTF.TEAM2 : SootCTF.TEAM1;

        String title = getTeamChatColour(winner)+""+ChatColor.BOLD + winner.getName() + " wins!";
        String subtitle = getTeamChatColour(winner)+""+ChatColor.BOLD + winner.getScore()
                        + ChatColor.GOLD+""+ChatColor.BOLD + " : "
                        + getTeamChatColour(loser)+""+ChatColor.BOLD + loser.getScore();
        for(Player player : SootCTF.INSTANCE.getServer().getOnlinePlayers()) {
            player.sendTitle(title, subtitle, -1, -1, -1);
            player.sendMessage(getTeamChatColour(winner)+""+ChatColor.BOLD + winner.getName() + " wins!");
        }

        // clear titles on bedrock clients
        new BukkitRunnable() {
            @Override
            public void run() {
                for(Player player : SootCTF.INSTANCE.getServer().getOnlinePlayers()) {
                    player.sendTitle("", "", -1, -1, -1);
                }
            }
        }.runTaskLater(SootCTF.INSTANCE, 80);
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

    public static ItemStack getTeamChestplate(CTFTeam team) {
        ItemStack teamChestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta)teamChestplate.getItemMeta();
        
        String n = team.getName().toLowerCase();
        if(n.contains("blue")) meta.setColor(Color.BLUE);
        else if(n.contains("red")) meta.setColor(Color.RED);
        else if(n.contains("yellow")) meta.setColor(Color.YELLOW);
        else if(n.contains("purple")) meta.setColor(Color.PURPLE);
        else if(n.contains("green")) meta.setColor(Color.LIME);

        meta.setUnbreakable(true);
        teamChestplate.setItemMeta(meta);

        return teamChestplate;
    }

    public static void teleportTeamsToWorldSpawn() {
        SootCTF.TEAM1.teleportWorldSpawn();
        SootCTF.TEAM2.teleportWorldSpawn();
    }

    public static void teleportTeamsToFlags() {
        SootCTF.TEAM1.teleport();
        SootCTF.TEAM2.teleport();
    }

    public static void stop() {
        if(FLAG_LISTENER != null) {
            HandlerList.unregisterAll(FLAG_LISTENER);
            FLAG_LISTENER = null;
        }

        if(DEATH_LISTENER != null) {
            HandlerList.unregisterAll(DEATH_LISTENER);
            DEATH_LISTENER = null;
        }

        SootCTF.PICKUP_MANAGER.stopSpawning();

        // unregister scoreboard teams
        for(Team team : CTFUtils.SCOREBOARD.getTeams()) {
            team.unregister();
        }

        SootCTF.TEAM1.clearPlayers();
        SootCTF.TEAM2.clearPlayers();
    }

    public static void initTeams() {
        FileConfiguration config = SootCTF.INSTANCE.getConfig();

        Location loc1 = config.getLocation("teams.one.flag");
        Block block1 = loc1 != null ? loc1.getBlock() : null;
        SootCTF.TEAM1 = new CTFTeam(config.getString("teams.one.name"), block1);

        Location loc2 = config.getLocation("teams.two.flag");
        Block block2 = loc2 != null ? loc2.getBlock() : null;
        SootCTF.TEAM2 = new CTFTeam(config.getString("teams.two.name"), block2);

        // Setup scoreboard teams
        Team scoreboardTeam1 = SCOREBOARD.registerNewTeam(SootCTF.TEAM1.getName());
        SootCTF.TEAM1.setMcTeam(scoreboardTeam1);
        scoreboardTeam1.setColor(getTeamChatColour(SootCTF.TEAM1));
        scoreboardTeam1.setAllowFriendlyFire(false);

        Team scoreboardTeam2 = SCOREBOARD.registerNewTeam(SootCTF.TEAM2.getName());
        SootCTF.TEAM2.setMcTeam(scoreboardTeam2);
        scoreboardTeam2.setColor(getTeamChatColour(SootCTF.TEAM2));
        scoreboardTeam2.setAllowFriendlyFire(false);
    }

}
