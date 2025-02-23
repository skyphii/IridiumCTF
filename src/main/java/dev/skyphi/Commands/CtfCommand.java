package dev.skyphi.Commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.util.StringUtil;

import dev.skyphi.CTFUtils;
import dev.skyphi.IridiumCTF;
import dev.skyphi.Listeners.DeathListener;
import dev.skyphi.Listeners.FlagListener;
import dev.skyphi.Listeners.SetupListener;
import dev.skyphi.Listeners.SetupListener.SetupType;
import dev.skyphi.Models.CTFConfig;
import dev.skyphi.Models.CTFPlayer;
import dev.skyphi.Models.CTFTeam;
import dev.skyphi.Models.Pair;

public class CtfCommand implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "You must be a player to use this command.");
            return true;
        }
        Player player = (Player)sender;

        if(!player.hasPermission("iridiumctf.ctf")) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("start")) {
                // start game
                CTFUtils.stop();
                CTFUtils.initTeams();
                makeTeams();
                IridiumCTF.TEAM1.giveChestplates();
                IridiumCTF.TEAM2.giveChestplates();

                if(CTFUtils.FLAG_LISTENER != null) HandlerList.unregisterAll(CTFUtils.FLAG_LISTENER);
                if(CTFUtils.DEATH_LISTENER != null) HandlerList.unregisterAll(CTFUtils.DEATH_LISTENER);
                CTFUtils.FLAG_LISTENER = new FlagListener();
                CTFUtils.DEATH_LISTENER = new DeathListener();
                IridiumCTF.INSTANCE.getServer().getPluginManager().registerEvents(CTFUtils.FLAG_LISTENER, IridiumCTF.INSTANCE);
                IridiumCTF.INSTANCE.getServer().getPluginManager().registerEvents(CTFUtils.DEATH_LISTENER, IridiumCTF.INSTANCE);

                IridiumCTF.TEAM1.tpTeamToFlag();
                IridiumCTF.TEAM2.tpTeamToFlag();
                IridiumCTF.PICKUP_MANAGER.startSpawning();
                CTFUtils.broadcast("Game started!", true);
            } else if (args[0].equalsIgnoreCase("stop")) {
                // stop game
                IridiumCTF.PICKUP_MANAGER.stopSpawning();
                CTFUtils.teleportTeamsToWorldSpawn();
                CTFUtils.stop();
                CTFUtils.broadcast("Game stopped!", true);
            } else if (args[0].equalsIgnoreCase("spawner")) {
                // setup item spawner
                new SetupListener(player, SetupType.SPAWNER);
                player.sendMessage(ChatColor.AQUA + "Click a block to set a new item spawner.");
            } else if (args[0].equalsIgnoreCase("removespawner") || args[0].equalsIgnoreCase("rspawner")) {
                new SetupListener(player, SetupType.REMOVE_SPAWNER);
                player.sendMessage(ChatColor.AQUA + "Click a block to remove item spawner.");
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

            new SetupListener(player, (team == 1)?SetupType.FLAG_1 : SetupType.FLAG_2);

            player.sendMessage(ChatColor.AQUA + "Click the flag block for team " + team + "'s flag.");
        }else if(args[0].equalsIgnoreCase("swap")) {
            Player playerToSwap = IridiumCTF.INSTANCE.getServer().getPlayer(args[1]);
            if(playerToSwap == null) {
                player.sendMessage(ChatColor.RED + "Player not found.");
                return true;
            }
            CTFPlayer ctfp = CTFUtils.getCTFPlayer(playerToSwap);
            if(ctfp == null) {
                player.sendMessage(ChatColor.RED + "Player is not on a team.");
                return true;
            }

            // replace flag if the swapped player is holding it (c'mon mods, really?)
            if(ctfp.hasFlag()) {
                ctfp.setFlag(false);
                ctfp.getEnemyTeam().getFlag().setType(CTFConfig.FLAG_TYPE);
                ctfp.getTeam().announce(ChatColor.RED, "The enemy flag was returned to their base!");
                ctfp.getEnemyTeam().announce(ChatColor.GREEN, "Your flag was returned to base!");
            }

            CTFTeam newTeam = ctfp.getEnemyTeam();
            ctfp.getTeam().removePlayer(playerToSwap);
            CTFPlayer newCtfp = new CTFPlayer(playerToSwap, newTeam);
            newTeam.addPlayer(newCtfp);
            playerToSwap.getInventory().setChestplate(CTFUtils.getTeamChestplate(newTeam));
            playerToSwap.teleport(newCtfp.getTeam().getFlag().getLocation().add(0, 1, 0));

            player.sendMessage(ChatColor.AQUA+"Player's team swapped!");
            playerToSwap.sendMessage(ChatColor.AQUA+"You have been swapped to " + CTFUtils.getTeamChatColour(newTeam)+ChatColor.BOLD+newTeam.getName());
        }else if(args[0].equalsIgnoreCase("win")) {
            int num = -1;
            try {
                num = Integer.parseInt(args[1]);
            }catch(NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Usage: /ctf win <#>");
                return true;
            }
            if(num <= 0) {
                player.sendMessage(ChatColor.RED + "Number must be positive and non-zero.");
                return true;
            }

            player.sendMessage(ChatColor.AQUA + "Number of captures needed to win is now " + num + ".");
            
            CTFConfig.FLAGS_TO_WIN = num;
            CTFConfig.save();
        }else if(args[0].equalsIgnoreCase("spawnrate")) {
            int num = -1;
            try {
                num = Integer.parseInt(args[1]);
            }catch(NumberFormatException e) {
                player.sendMessage(ChatColor.RED + "Usage: /ctf spawnrate <#>");
                return true;
            }
            if(num <= 0) {
                player.sendMessage(ChatColor.RED + "Number must be positive and non-zero.");
                return true;
            }

            player.sendMessage(ChatColor.AQUA + "Items will now spawn every " + num + " seconds. (does not apply to active games)");

            IridiumCTF.PICKUP_MANAGER.setItemSpawnRate(num);
        }

        return true;
    }

    private void makeTeams() {
        CTFTeam team1 = IridiumCTF.TEAM1;
        CTFTeam team2 = IridiumCTF.TEAM2;

        // get all players that are in adventure/survival mode and shuffle the list for randomness
        List<Player> players = new ArrayList<>(IridiumCTF.INSTANCE.getServer().getOnlinePlayers());
        players.removeIf(p -> p.getGameMode() != GameMode.ADVENTURE && p.getGameMode() != GameMode.SURVIVAL);
        Collections.shuffle(players);

        if(CTFConfig.PAIR_NEARBY_PLAYERS) {
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

        if(!CTFConfig.STRICT_PAIRING) {
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String cmdLbl, String[] args) {
        final List<String> completions = new ArrayList<>();
        if (args.length == 1) StringUtil.copyPartialMatches(args[0], List.of("start", "stop", "spawner", "removespawner", "flag", "swap", "win", "spawnrate"), completions);
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("flag")) {
                Collection<String> next = List.of("1", "2");
                StringUtil.copyPartialMatches(args[1], next, completions);
            } else if (args[0].equalsIgnoreCase("swap")) {
                Collection<String> next = IridiumCTF.INSTANCE.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
                StringUtil.copyPartialMatches(args[1], next, completions);
            } else if (args[0].equalsIgnoreCase("win")) {
                StringUtil.copyPartialMatches(args[1], List.of("3", "5", "10"), completions);
            } else if (args[0].equalsIgnoreCase("spawnrate")) {
                StringUtil.copyPartialMatches(args[1], List.of("10", "20", "30"), completions);
            }
        }
        return completions;
    }

}
