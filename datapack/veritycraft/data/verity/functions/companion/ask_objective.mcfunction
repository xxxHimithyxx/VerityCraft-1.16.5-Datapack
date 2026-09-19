execute if score @s verity.objective matches 0 tellraw @s {"text":"Verity: A new requirement has been assigned. Follow the signal.","color":"gray"}
execute if score @s verity.objective matches 1.. tellraw @s {"text":"Verity: Your current objective is still active. Do not abandon it.","color":"gray"}
