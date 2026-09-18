scoreboard players add @s vc_fear 22
playsound minecraft:entity.creeper.primed master @s ~ ~ ~ 0.45 0.55
summon minecraft:zombie ~ ~ ~ {Silent:1b,PersistenceRequired:1b,NoAI:1b,Tags:["vc_horror"]}
title @s title {"text":"RUN","color":"dark_red","bold":true}
title @s subtitle {"text":"Verity has noticed you.","color":"gray"}
schedule function veritycraft:events/clear_ambush 100t
