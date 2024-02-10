# SootCTF

A new SootMC event plugin for **Capture the Flag**!

# In-Game Setup
- Use `/ctf flag <1/2>` to set each team's flag block. You will be prompted to click the block after running the command.
- Use `/ctf <start/stop>` to start/stop the game. Starting the game will automatically make teams randomly (although, check the first Note/Tip in the next section about this)
- All players can use `/score` to view the current score in chat. Currently, the only other time the score shows up is when a flag is captured.

# Notes/Tips
- If `pair_nearby_players` is true in the config, players standing very close to each other will be grouped (in pairs) and placed on the same team.
- When configuring team names, it is recommended to use the format "Team X" where X is whatever you want. This will ensure chat messages make grammatical sense.
    - Extra naming tip, the following colours are recommended for team names as some chat messages will auto-colour the name if the colour is included.
        - Blue, red, yellow, purple, and green