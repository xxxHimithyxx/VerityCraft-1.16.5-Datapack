scoreboard players set @s verity.companion 1
tellraw @s [{"text":"Verity: ","color":"dark_red"},{"text":"Hey. I am Verity, your helper. Friend is a word humans use quickly, but I will try.","color":"gray"}]
tellraw @s {"text":"Verity: Ask me with /function verity:companion/ask_help, /ask_objective, /ask_safe, /ask_who, or /ask_signal.","color":"gray"}
playsound minecraft:block.note_block.pling master @s ~ ~ ~ 0.7 0.8
