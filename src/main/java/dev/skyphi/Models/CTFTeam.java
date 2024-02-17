package dev.skyphi.Models;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Team;

import dev.skyphi.CTFUtils;
import dev.skyphi.SootCTF;

public class CTFTeam {
    
    private HashMap<UUID, CTFPlayer> playerList = new HashMap<>();
    private Block flag;
    private int score;
    private String name;
    private Team mcTeam;

    public CTFTeam(String name, Block flag) {
        this.name = name;
        this.flag = flag;
    }

    // GETTERS/SETTERS

    public void addPlayer(CTFPlayer player) {
        playerList.put(player.getUniqueId(), player);
        mcTeam.addEntry(player.getPlayerName());
    }
    public void removePlayer(Player player) {
        playerList.remove(player.getUniqueId());
        mcTeam.removeEntry(player.getName());
    }
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
    public void addPoint(CTFPlayer flagCarrier) {
        if(++score == SootCTF.FLAGS_TO_WIN) {
            CTFUtils.announceWinner(this);
            CTFUtils.teleportTeamsToWorldSpawn();
            CTFUtils.stop();
        }else {
            CTFUtils.showScore(flagCarrier);
            CTFUtils.teleportTeamsToFlags();
        }
    }

    public Block getFlag() { return flag; }
    public void setFlag(Block flagBlock) { this.flag = flagBlock; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Team getMcTeam() { return mcTeam; }
    public void setMcTeam(Team mcTeam) { this.mcTeam = mcTeam; }

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

        // clear titles on bedrock clients
        new BukkitRunnable() {
            @Override
            public void run() {
                for(CTFPlayer ctfp : playerList.values()) {
                    ctfp.getPlayer().sendTitle("", "", -1, -1, -1);
                }
            }
        }.runTaskLater(SootCTF.INSTANCE, 80);
    }

    public void playSound(Sound sound, float volume, float pitch) {
        for(CTFPlayer ctfp : playerList.values()) {
            ctfp.getPlayer().playSound(ctfp.getPlayer().getLocation(), sound, volume, pitch);
        }
    }

    public void notifyPlayers() {
        ChatColor teamColour = CTFUtils.getTeamChatColour(this);

        for(CTFPlayer ctfp : playerList.values()) {
            ctfp.getPlayer().sendMessage(ChatColor.GOLD + "You are on " + teamColour + ChatColor.BOLD + name + "!");
            ctfp.getPlayer().playSound(ctfp.getPlayer().getLocation(), Sound.ITEM_GOAT_HORN_SOUND_0, 1, 1);
        }
    }

    public void tpTeamToFlag() {
        removePickups();
        SootCTF.PICKUP_MANAGER.clearSpawnedPickups();

        Location baseLoc = flag.getLocation().clone().add(0.5, 1, 0.5);
        int x = -2;
        int z = -2;
        for(CTFPlayer ctfp : playerList.values()) {
            Location tpLoc = baseLoc.clone().add(x, 0, z);
            int retryCount = 0, maxRetries = 25;
            while(!tpLoc.getBlock().isPassable() || !tpLoc.getBlock().getRelative(0, 1, 0).isPassable()) {
                tpLoc = baseLoc.clone().add(x, 0, z);
                
                x++;
                if(x > 2) {
                    x = -2;
                    z++;
                }
                if(z > 2) {
                    x = -2;
                    z = -2;
                }

                // prevent stack overflow, give up and spawn on top of flag
                if(retryCount++ >= maxRetries) {
                    tpLoc = baseLoc;
                    break;
                }
            }
            ctfp.getPlayer().teleport(tpLoc);
            ctfp.getPlayer().setHealth(20);
            ctfp.getPlayer().setFoodLevel(20);
            ctfp.getPlayer().setSaturation(20);
        }
    }

    public void teleportWorldSpawn() {
        for(CTFPlayer ctfp : playerList.values()) {
            Player player = ctfp.getPlayer();
            player.teleport(player.getWorld().getSpawnLocation().add(0.5, 0, 0.5));
        }
    }

    public void clearPlayers() {
        playerList.values().forEach(ctfp -> {
            if(ctfp.hasFlag()) {
                ctfp.setFlag(false);
                ctfp.getEnemyTeam().getFlag().setType(SootCTF.FLAG_TYPE);
            }
        });
        playerList.clear();
    }

    public void giveChestplates() {
        ItemStack chestplate = CTFUtils.getTeamChestplate(this);
        for(CTFPlayer ctfp : playerList.values()) {
            ctfp.getPlayer().getInventory().setChestplate(chestplate);
        }
    }

    public void removePickups() {
        for(CTFPlayer ctfp : playerList.values()) {
            ctfp.removePickups();
        }
    }

}
