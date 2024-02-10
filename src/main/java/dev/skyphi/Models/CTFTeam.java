package dev.skyphi.Models;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class CTFTeam {
    
    private HashMap<UUID, CTFPlayer> playerList = new HashMap<>();
    private Block flag;
    private int score;
    private String name;

    public CTFTeam(String name, Block flag) {
        this.name = name;
        this.flag = flag;
    }

    // GETTERS/SETTERS

    public void addPlayer(CTFPlayer player) { playerList.put(player.getUniqueId(), player); }
    public void removePlayer(Player player) { playerList.remove(player.getUniqueId()); }
    public CTFPlayer getCtfPlayer(Player player) { return playerList.get(player.getUniqueId()); }
    public Player removeRandomPlayer() {
        int randomNum = (int)(Math.random() * playerList.size());
        CTFPlayer ctfp = playerList.get(playerList.keySet().toArray()[randomNum]);
        Player player = ctfp.getPlayer();
        removePlayer(player);
        return player;
    }

    public int getPlayerCount() { return playerList.size(); }

    public int getScore() { return score; }
    public void addPoint() { score++; }

    public Block getFlag() { return flag; }
    public void setFlag(Block flagBlock) { this.flag = flagBlock; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // UTILITY

    public void announce(ChatColor startColour, String msg) {
        for(CTFPlayer ctfp : playerList.values()) {
            ctfp.getPlayer().sendMessage(startColour + msg);
        }
    }

    public void title(ChatColor titleColour, String title, ChatColor subtitleColour, String subtitle) {
        for(CTFPlayer ctfp : playerList.values()) {
            ctfp.getPlayer().sendTitle(titleColour+title, subtitleColour+subtitle, -1, -1, -1);
        }
    }

    public void playSound(Sound sound, float volume, float pitch) {
        for(CTFPlayer ctfp : playerList.values()) {
            ctfp.getPlayer().playSound(ctfp.getPlayer().getLocation(), sound, volume, pitch);
        }
    }

    public void notifyPlayers() {
        ChatColor teamColour = ChatColor.GRAY;
        String n = name.toLowerCase();
        if(n.contains("blue")) teamColour = ChatColor.BLUE;
        else if(n.contains("red")) teamColour = ChatColor.RED;
        else if(n.contains("yellow")) teamColour = ChatColor.YELLOW;
        else if(n.contains("purple")) teamColour = ChatColor.LIGHT_PURPLE;
        else if(n.contains("green")) teamColour = ChatColor.GREEN;

        for(CTFPlayer ctfp : playerList.values()) {
            ctfp.getPlayer().sendMessage(ChatColor.GOLD + "You are on " + teamColour + ChatColor.BOLD + name + "!");
        }
    }

}
