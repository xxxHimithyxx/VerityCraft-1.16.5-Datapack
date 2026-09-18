scoreboard players add @a vc_timer 1
scoreboard players remove @a[scores={vc_cd=1..}] vc_cd 1
execute as @a[scores={vc_fear=1..}] run function veritycraft:fear_effects
execute as @a[scores={vc_fear=1..}] at @s if entity @e[type=minecraft:zombie,distance=..18] run scoreboard players add @s vc_fear 1
execute as @a[scores={vc_timer=200..,vc_cd=0}] at @s run function veritycraft:event
execute as @a[scores={vc_timer=1200..}] run scoreboard players set @s vc_timer 0
execute as @a[scores={vc_fear=1..}] run scoreboard players remove @s vc_fear 1
