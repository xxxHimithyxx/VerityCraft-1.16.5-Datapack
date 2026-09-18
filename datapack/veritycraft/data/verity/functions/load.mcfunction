scoreboard objectives add verity.threat dummy
scoreboard objectives add verity.timer dummy
scoreboard objectives add verity.cooldown dummy
scoreboard objectives add verity.roll dummy
scoreboard objectives add verity.init dummy
scoreboard objectives add verity.objective dummy
scoreboard objectives add verity.scarcity dummy
scoreboard objectives setdisplay sidebar verity.threat
scoreboard players add @a verity.threat 0
scoreboard players add @a verity.timer 0
scoreboard players add @a verity.cooldown 0
scoreboard players add @a verity.roll 0
scoreboard players add @a verity.init 0
scoreboard players add @a verity.objective 0
scoreboard players add @a verity.scarcity 0
execute as @a[scores={verity.init=0}] run function verity:player_init
tellraw @a {"text":"[Verity] The signal is awake.","color":"dark_red"}
