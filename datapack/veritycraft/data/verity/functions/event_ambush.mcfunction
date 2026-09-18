scoreboard players add @s verity.threat 22
playsound minecraft:entity.creeper.primed master @s ~ ~ ~ 0.45 0.55
summon minecraft:zombie ~ ~ ~ {Silent:1b,PersistenceRequired:1b,NoAI:1b,Tags:["verity_horror"]}
title @s title {"text":"RUN","color":"dark_red","bold":true}
title @s subtitle {"text":"Verity has noticed you.","color":"gray"}
schedule function verity:clear_ambush 100t
