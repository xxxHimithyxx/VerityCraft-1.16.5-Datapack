execute if score @s verity.threat matches 0..1 tellraw @s {"text":"Verity: Follow the objective and secure a shelter before nightfall.","color":"gray"}
execute if score @s verity.threat matches 2..3 tellraw @s {"text":"Verity: Follow the pattern. The signal is always slightly ahead of you.","color":"gray"}
execute if score @s verity.threat matches 4.. tellraw @s {"text":"Verity: The signal is behind you now.","color":"dark_red"}
