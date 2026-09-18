scoreboard players add @a vc_fear 0
scoreboard players add @a vc_timer 0
scoreboard players add @a vc_cd 0
scoreboard players add @a vc_roll 0
scoreboard players add @a vc_init 0
execute as @a[scores={vc_init=0}] run function veritycraft:setup_player

tellraw @a {"text":"[VerityCraft] The signal is awake.","color":"dark_red"}
