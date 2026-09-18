scoreboard players add @s verity.threat 15
playsound minecraft:entity.phantom.swoop master @s ~ ~ ~ 0.65 0.7
summon minecraft:armor_stand ~ ~ ~ {Invisible:1b,Invulnerable:1b,NoGravity:1b,Marker:1b,CustomName:'{"text":"Verity","color":"dark_red"}',Tags:["verity_vision"]}
particle minecraft:portal ~ ~1 ~ 0.35 0.7 0.35 0.1 35 force @s
schedule function verity:clear_vision 30t
