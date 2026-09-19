execute if score @s verity.threat matches 0..1 tellraw @s {"text":"Verity: You are safe enough for now.","color":"gray"}
execute if score @s verity.threat matches 2..3 tellraw @s {"text":"Verity: Not completely. Something is listening.","color":"gray"}
execute if score @s verity.threat matches 4.. tellraw @s {"text":"Verity: No. Move to shelter. Do not look back.","color":"dark_red"}
