# IridiumCTF

An Iridium Network event plugin for **Capture the Flag**!

# In-Game Setup
- Use `/ctf flag <1/2>` to set each team's flag block. You will be prompted to click the block after running the command.
- Use `/ctf spawner` to set item spawners. You'll be prompted to click a block just like for flags.
    - There's also `/ctf rspawner` to remove an item spawner.
- Use `/ctf <start/stop>` to start/stop the game. Starting the game will automatically make teams randomly (although, check the first Note/Tip in the next section about this)
- Use `/ctf win <#>` to set the number of captures required to win. This can also be changed in the config.
- Use `/ctf spawnrate <#>` to set item spawnrate. Every spawner will pop out an item every `<#>` seconds. This can also be changed in the config.
- Make sure world spawn is in a good location (such as the centre of the map), as both teams will be teleported to it when a game ends.

# Notes/Tips
- If `pair_nearby_players` is true in the config, players standing very close to each other will be grouped (in pairs) and placed on the same team.
- When configuring team names, it is recommended to use the format "Team X" where X is whatever you want. This will ensure chat messages make grammatical sense.
    - Extra naming tip, the following colours are recommended for team names as some chat messages will auto-colour the name if the colour is included.
        - Blue, red, yellow, purple, and green
- All players can use `/score` to view the current score in chat. Currently, the only other time the score shows up is when a flag is captured.
- Players are teleported **around** their team's flag when a flag is captured. Just thought that's worthy of a note. It is best to keep the immediate area around the flag clear of obstructions as there are **no** checks for suffocation.
- Use `/ctf swap <player>` to swap a player's team during a game. They will be teleported to their new team's flag.
