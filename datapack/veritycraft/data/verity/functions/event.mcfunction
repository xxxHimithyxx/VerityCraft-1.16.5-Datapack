scoreboard players random @s verity.roll 1 100
scoreboard players set @s verity.cooldown 240
execute if score @s verity.roll matches 1..35 run function verity:event_whisper
execute if score @s verity.roll matches 36..60 run function verity:event_footsteps
execute if score @s verity.roll matches 61..78 run function verity:event_flicker
execute if score @s verity.roll matches 79..92 run function verity:event_vision
execute if score @s verity.roll matches 93..100 run function verity:event_ambush
