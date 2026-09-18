scoreboard players random @s vc_roll 1 100
scoreboard players set @s vc_cd 240
execute if score @s vc_roll matches 1..35 run function veritycraft:events/whisper
execute if score @s vc_roll matches 36..60 run function veritycraft:events:footsteps
execute if score @s vc_roll matches 61..78 run function veritycraft:events/flicker
execute if score @s vc_roll matches 79..92 run function veritycraft:events/eyes
execute if score @s vc_roll matches 93..100 run function veritycraft:events/ambush
