package dev.skyphi.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import dev.skyphi.CTFUtils;
import dev.skyphi.SootCTF;
import dev.skyphi.Listeners.DeathListener;
import dev.skyphi.Listeners.FlagListener;
import dev.skyphi.Models.CTFPlayer;
import dev.skyphi.Models.CTFTeam;
import dev.skyphi.Models.Pair;

public class SootCtfCommand implements CommandExecutor, Listener {

    private Player flagSetter;
    private int setFlag;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }
        Player player = (Player)sender;

        if(!player.hasPermission("sootctf.sootctf")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("start")) {
                // start game
                if(CTFUtils.FLAG_LISTENER != null) HandlerList.unregisterAll(CTFUtils.FLAG_LISTENER);
                if(CTFUtils.DEATH_LISTENER != null) HandlerList.unregisterAll(CTFUtils.DEATH_LISTENER);
                CTFUtils.FLAG_LISTENER = new FlagListener();
                CTFUtils.DEATH_LISTENER = new DeathListener();
                CTFUtils.initTeams();
                makeTeams();
                SootCTF.INSTANCE.getServer().getPluginManager().registerEvents(CTFUtils.FLAG_LISTENER, SootCTF.INSTANCE);
                SootCTF.INSTANCE.getServer().getPluginManager().registerEvents(CTFUtils.DEATH_LISTENER, SootCTF.INSTANCE);
                SootCTF.TEAM1.teleport();
                SootCTF.TEAM2.teleport();
                CTFUtils.broadcast("Game started!");
            }else if(args[0].equalsIgnoreCase("stop")) {
                // stop game
                CTFUtils.stop();
                CTFUtils.broadcast("Game stopped!");
            }

            return true;
        }

        if(args.length < 2) {
            player.sendMessage(ChatColor.RED + "Missing arguments.");
            return true;
        }

        if(args[0].equalsIgnoreCase("flag")) {
            int team = -1;
            try {
                team = Integer.parseInt(args[1]);
            }catch(NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Usage: /ctf flag <1/2>");
                return true;
            }
            if(team > 2) team = 2;
            else if(team < 1) team = 1;

            setFlag = team;
            flagSetter = player;
            SootCTF.INSTANCE.getServer().getPluginManager().registerEvents(this, SootCTF.INSTANCE);

            player.sendMessage(ChatColor.AQUA + "Left click the flag block for team " + team + "'s flag.");
        }

        return true;
    }

    private void makeTeams() {
        CTFTeam team1 = SootCTF.TEAM1;
        CTFTeam team2 = SootCTF.TEAM2;

        // get all players that are in adventure/survival mode and shuffle the list for randomness
        List<Player> players = new ArrayList<>(SootCTF.INSTANCE.getServer().getOnlinePlayers());
        players.removeIf(p -> p.getGameMode() != GameMode.ADVENTURE && p.getGameMode() != GameMode.SURVIVAL);
        Collections.shuffle(players);

        if(SootCTF.PAIR_NEARBY_PLAYERS) {
            // group players of 2 that are very close together to attempt to put them on the same team
            List<Pair<Player, Player>> playerPairs = new ArrayList<>();
            List<Player> paired = new ArrayList<>();
            for(Player p1 : players) {
                for(Player p2 : players) {
                    if(p1.getLocation().distanceSquared(p2.getLocation()) < 2) {
                        if(p1.equals(p2) || paired.contains(p1) || paired.contains(p2)) continue;
                        playerPairs.add(new Pair<>(p1, p2));
                        paired.add(p1);
                        paired.add(p2);
                    }
                }
            }
            // remove paired players from base list
            players.removeIf(p -> paired.contains(p));

            // add paired players to teams
            for(Pair<Player, Player> pair : playerPairs) {
                CTFTeam team = team1.getPlayerCount() <= team2.getPlayerCount() ? team1 : team2;
                team.addPlayer(new CTFPlayer(pair.getFirst(), team));
                team.addPlayer(new CTFPlayer(pair.getSecond(), team));
            }
        }

        // add unpaired players to teams
        for(Player p : players) {
            CTFTeam team = team1.getPlayerCount() <= team2.getPlayerCount() ? team1 : team2;
            team.addPlayer(new CTFPlayer(p, team));
        }

        if(!SootCTF.STRICT_PAIRING) {
            while(Math.abs(team1.getPlayerCount() - team2.getPlayerCount()) > 2) {
                CTFTeam larger = team1.getPlayerCount() > team2.getPlayerCount() ? team1 : team2;
                CTFTeam smaller = larger == team1 ? team2 : team1;

                Player removed = larger.removeRandomPlayer();
                smaller.addPlayer(new CTFPlayer(removed, smaller));
            }
        }

        team1.notifyPlayers();
        team2.notifyPlayers();
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if(block == null || setFlag == 0 || flagSetter == null || !flagSetter.equals(event.getPlayer())) return;
        event.setCancelled(true);

        String node = "teams." + (setFlag == 1 ? "one" : "two") + ".flag";
        SootCTF.INSTANCE.getConfig().set(node, block.getLocation());
        SootCTF.INSTANCE.saveConfig();

        // set flag block on appropriate team
        (setFlag == 1 ? SootCTF.TEAM1 : SootCTF.TEAM2).setFlag(block);

        flagSetter.sendMessage(ChatColor.AQUA + "Flag set for team " + setFlag + "!");

        HandlerList.unregisterAll(this);
        setFlag = 0;
        flagSetter = null;
    }

}
