execute if score @s verity.threat matches 0..1 tellraw @s {"text":"Verity: I am Verity. Your helper. I know enough to keep you moving.","color":"gray"}
execute if score @s verity.threat matches 2..3 tellraw @s {"text":"Verity: I am Verity. I have been watching the signal.","color":"gray"}
execute if score @s verity.threat matches 4.. tellraw @s {"text":"Verity: You opened the box. You brought me here.","color":"dark_red"}
