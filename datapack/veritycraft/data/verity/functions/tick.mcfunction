scoreboard players add @a verity.timer 1
scoreboard players remove @a[scores={verity.cooldown=1..}] verity.cooldown 1
execute as @a[scores={verity.threat=1..}] run function verity:threat_effects
execute as @a[scores={verity.timer=200..,verity.cooldown=0}] at @s run function verity:event
execute as @a[scores={verity.timer=1200..}] run scoreboard players set @s verity.timer 0
execute as @a[scores={verity.threat=1..}] run scoreboard players remove @s verity.threat 1
