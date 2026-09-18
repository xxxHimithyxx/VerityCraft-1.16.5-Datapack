scoreboard players add @s vc_fear 10
playsound minecraft:block.redstone_torch.burnout master @s ~ ~ ~ 0.5 0.5
effect give @s minecraft:blindness 1 0 true
particle minecraft:smoke ~ ~1 ~ 0.5 0.8 0.5 0.02 20 force @s
title @s actionbar {"text":"The lights are lying to you.","color":"gray","italic":true}
