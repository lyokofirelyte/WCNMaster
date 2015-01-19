package com.github.lyokofirelyte.Spectral.DataTypes;

public enum DPI {
	
	/**
	 * Website Auth Code
	 * @dataType String
	 */
	WEBSITE_CODE("WEBSITE_CODE"),
	
	/**
	 * Boolean that determines if we should send packets to google spreadsheets
	 * @dataType boolean
	 * @specificTo DivinitySystem
	 */
	ENABLE_SPREADSHEET("ENABLE_SPREADSHEET"),
	
	/**
	 * The last marrkit action to take place per item
	 * @dataType String List
	 * @specificTo DivinitySystem
	 */
	LAST_ACTION("LAST_ACTION"),
	
	/**
	 * The username & password for the WCN Google Account
	 * @dataType String
	 * @outputExample user pass
	 * @specificTo DivinitySystem
	 */
	SPREADSHEET_LOGIN("SPREADSHEET_LOGIN"),
	
	/**
	 * The shiny balance of the player
	 * @dataType integer
	 */
	BALANCE("BALANCE"),
	
	/**
	 * The about of money the player has made in a day
	 * @dataType integer
	 */
	DAILY_BALANCE("DAILY_BALANCE"),
	
	/**
	 * The list of homes of the player
	 * @dataType String List
	 */
	HOME("HOME"),
	
	/**
	 * Previous scoreboard info for the player - do not touch
	 * @dataType String List
	 */
	PREVIOUS_BOARD_INFO("PREVIOUS_BOARD_INFO"),
	
	/**
	 * A running total of all mob kills for the player
	 * @dataType String List
	 * @outputExample
	 *     - Skeleton 5
	 *     - Creeper 10
	 */
	MOB_COUNTS("MOB_COUNTS"),
	
	/**
	 * The last skill that the player has trained, with XP
	 * @dataType String
	 */
	LAST_ELYSS_SKILL("LAST_ELYSS_SKILL"),
	
	/**
	 * The last monster that the player has killed
	 * @dataType String
	 */
	LAST_ELYSS_KILL("LAST_ELYSS_KILL"),
	
	/**
	 * The last login date of the player <br />
	 * @dataType String
	 * @outputExample Sunday, October 05, 4.14
	 */
	LAST_LOGIN("LAST_LOGIN"),
	
	/**
	 * The last logout date of the player <br />
	 * @dataType String
	 * @outputExample Sunday, October 05, 5.34
	 */
	LAST_LOGOUT("LAST_LOGOUT"),
	
	/**
	 * The last logout location of the player (world, x, y, z, yaw, pitch) <br />
	 * @dataType String
	 * @outputExample world 1184 63 1548 -196.799 12.4500265
	 */
	LOGOUT_LOCATION("LOGOUT_LOCATION"),
	
	/**
	 * The permission set for the player
	 * @dataType String List
	 * @outputExample 
	 *     - wa.staff.intern <br />
	 *     - wa.staff.mod <br />
	 *     - wa.rank.dweller <br />
	 *     - wa.member <br />
	 */
	PERMS("PERMS"),
	
	/**
	 * The display name of the player <b>without</b> converted color codes <br />
	 * @dataType String
	 * @outputExample &7Hu&7gs
	 */
	DISPLAY_NAME("DISPLAY_NAME"),
	
	/**
	 * The custom-set join message for the player <br />
	 * @dataType String
	 */
	JOIN_MESSAGE("JOIN_MESSAGE"),
	
	/**
	 * The custom-set quit message for the player <br />
	 * @dataType String
	 */
	QUIT_MESSAGE("QUIT_MESSAGE"),
	
	/**
	 * The global chat color for the player <br />
	 * @dataType String
	 * @outputExample &7
	 */
	GLOBAL_COLOR("GLOBAL_COLOR"),
	
	/**
	 * The private chat color for the player <br />
	 * @dataType String
	 * @outputExample &d
	 */
	PM_COLOR("PM_COLOR"),
	
	/**
	 * The alliance chat color for the player <br />
	 * @dataType String
	 * @outputExample &b
	 */
	ALLIANCE_COLOR("ALLIANCE_COLOR"),
	
	/**
	 * Asks if the player is allowing shared xp in patrols <br />
	 * @dataType boolean
	 */
	SHARE_XP("SHARE_XP"),
	
	/**
	 * The system time that it must be greater than or equal to for the cooldown to be over
	 * @dataType long
	 * @outputExample 11020392054927
	 * @exampleUsage if (dp.getLong(DPI.PATROL_TP_COOLDOWN)) >= System.currentTimeMillis()){ }
	 */
	PATROL_TP_COOLDOWN("PATROL_TP_COOLDOWN"),
	
	/**
	 * The name of the patrol that they have typed to form
	 * @dataType String
	 */
	PATROL_INPUT("PATROL_INPUT"),
	
	/**
	 * The first prefix in the chat for the player
	 * @dataType String
	 * @outputExample &aWCN
	 * @outputExample &7D
	 */
	RANK_NAME("RANK_NAME"),
	
	/**
	 * The color of the snowflake for the player
	 * @dataType String
	 * @outputExample &1
	 */
	RANK_COLOR("RANK_COLOR"),
	
	/**
	 * The description of the player's rank
	 * @dataType String
	 * @outputExample &6Guest Rank. No perms at all, except chat!
	 */
	RANK_DESC("RANK_DESC"),
	
	/**
	 * Provided the player is staff, this value determines what kind of staff they are
	 * @dataType String
	 * @outputExample &a
	 */
	STAFF_COLOR("STAFF_COLOR"),
	
	/**
	 * Provided the player is staff, this value displays the description of their duties
	 * @dataType String
	 * @outputExample Intern of the server! Responsible for x, y, and z
	 */
	STAFF_DESC("STAFF_DESC"),
	
	/**
	 * The self-set player bio
	 * @dataType String
	 * @outputExample Hey, I'm Hugs and I love ramen noodles.
	 */
	PLAYER_DESC("PLAYER_DESC"),
	
	/**
	 * Used to determine if we should take XP from the player or not
	 * @dataType boolean
	 */
	IGNORE_XP("IGNORE_XP"),
	
	/**
	 * The notepad data for the player
	 * @dataType String List
	 */
	NOTEPAD("NOTEPAD"),
	
	/**
	 * Temporary settings for adding / deleting notes
	 * @dataType String List
	 */
	NOTEPAD_SETTING("NOTEPAD_SETTING"),
	
	/**
	 * The first half of the player's alliance color
	 * @dataType String
	 * @outputExample &7
	 */
	ALLIANCE_COLOR_1("ALLIANCE_COLOR_1"),
	
	/**
	 * The second half of the player's alliance color
	 * @dataType String
	 * @outputExample &4
	 */
	ALLIANCE_COLOR_2("ALLIANCE_COLOR_2"),
	
	/**
	 * The actual name of the player's alliance
	 * @dataType String
	 * @outputExample Winhaven
	 */
	ALLIANCE_NAME("ALLIANCE_NAME"),
	
	/**
	 * I forgot what I used this for. -_-
	 * @dataType Rainbow Sprinkles
	 * @outputExample (\__/) <br /> (='.'=)
	 */
	ELY("ELY"),
	
	@Deprecated
	WEALTH_LOOKUP("WEALTH_LOOKUP"),
	
	/**
	 * The amount of XP in the player's repair system
	 * @dataType integer
	 */
	REPAIR_EXP("REPAIR_EXP"),
	
	/**
	 * The state of the player's repair toggle
	 * @dataType boolean
	 */
	REPAIR_TOGGLE("REPAIR_TOGGLE"),
	
	/**
	 * The list of items allowed to be repaired for the player
	 * @dataType String List
	 * @outputExample - DIAMOND_SPADE <br /> - DIAMOND_AXE
	 */
	REPAIR_TOOLS("REPAIR_TOOLS"),
	
	/**
	 * The list of items inside the player's /rep config list
	 * @dataType ItemStack List
	 */
	REPAIR_INV("REPAIR_INV"),
	
	/**
	 * Is the player in one of the minigames? This prevents them from doing a lot of stuff and helps keep the minigames safe
	 * @dataType boolean
	 */
	IN_GAME("IN_GAME"),
	
	@Deprecated
	TS3_CREDENTIALS("TS3_CREDENTIALS"),
	
	/**
	 * Determines if the player should receive a logger tool upon doing /log, or if it should be deleted
	 * @dataType boolean
	 */
	LOGGER("LOGGER"),
	
	@Deprecated
	REGISTERED("REGISTERED"),
	
	@Deprecated
	HOLO_ID("HOLO_ID"),
	
	/**
	 * The list of items looked up by the player for later paging
	 * @dataType String List
	 */
	LOGGER_RESULTS("LOGGER_RESULTS"),
	
	/**
	 * A global list of AFK players
	 * @dataType String List
	 * @specificTo DivinitySystem
	 */
	AFK_PLAYERS("AFK_PLAYERS"),
	
	/**
	 * The string location of a player's death chest
	 * @dataType String
	 * @outputExample world 100 40 1000
	 */
	DEATH_CHEST_LOC("DEATH_CHEST_LOC"),
	
	/**
	 * The inventory of a player's death chest
	 * @dataType ItemStack List
	 */
	DEATH_CHEST_INV("DEATH_CHEST_INV"),
	
	/**
	 * This value is covered by DEATH_CHEST_INV since Divinity v1.1
	 * @deprecated
	 */
	DEATH_ARMOR("DEATH_ARMORS"),
	
	/**
	 * The list of items inside the closet trading center
	 */
	CLOSET_ITEMS("CLOSET_ITEMS"),
	
	/**
	 * Value no longer necessary due to appending _TOGGLE for ease of use
	 * @deprecated
	 */
	SCOREBOARD("SCOREBOARD"),
	
	/**
	 * Toggle for displaying XP on tools for ELYSS
	 * @dataType boolean
	 */
	XP_DISP_NAME_TOGGLE("XP_DISP_NAME_TOGGLE"),
	
	/**
	 * Temporary storage of falling blocks
	 * @deprecated
	 */
	FALLING_BLOCKS("FALLING_BLOCKS"),
	
	/**
	 * The total amount of miliseconds the player has been AFK
	 * @dataType long
	 */
	AFK_TIME("AFK_TIME"),
	
	/**
	 * The system time that the player started being AFK
	 * @dataType long
	 */
	AFK_TIME_INIT("AFK_TIME_INIT"),
	
	/**
	 * Moved to ElySkill
	 * @deprecated
	 */
	PATROL_LEVEL("PATROL_LEVEL"),
	
	/**
	 * Is the player spectating someone?
	 * @dataType boolean
	 */
	SPECTATING("SPECTATING"),
	
	/**
	 * The spectate target of the player
	 * @dataType String
	 */
	SPECTATE_TARGET("SPECTATE_TARGET"),
	
	/**
	 * The total amount of stored EXP for the player
	 * @dataType integer
	 */
	EXP("EXP"),
	
	@Deprecated
	VISUAL("VISUAL"),
	
	/**
	 * The toggle status of being in alliance chat
	 * @dataType boolean
	 */
	ALLIANCE_TOGGLE("ALLIANCE_TOGGLE"),
	
	/**
	 * The toggle status of viewing the sideboard
	 * @dataType boolean
	 */
	SCOREBOARD_TOGGLE("SCOREBOARD_TOGGLE"),
	
	@Deprecated
	POKES_TOGGLE("POKES_TOGGLE"),
	
	@Deprecated
	PVP_TOGGLE("PVP_TOGGLE"),
	
	/**
	 * The toggle status of viewing fireworks
	 * @dataType boolean
	 */
	FIREWORKS_TOGGLE("FIREWORKS_TOGGLE"),
	
	/**
	 * The toggle status of the system notifing you of where you died upon death
	 * @dataType boolean
	 */
	DEATHLOCS_TOGGLE("DEATHLOCS_TOGGLE"),
	
	/**
	 * The toggle status of viewing particle effects while teleporting
	 * @dataType boolean
	 */
	PARTICLES_TOGGLE("PARTICLES_TOGGLE"),
	
	/**
	 * The toggle status of the chat filter
	 * @dataType boolean
	 */
	CHAT_FILTER_TOGGLE("CHAT_FILTER"),
	
	/**
	 * Is the player muted?
	 * @dataType boolean
	 */
	MUTED("MUTED"),
	
	/**
	 * The system time it should be greater than or equal to for the player to be un-muted
	 * @dataType long
	 */
	MUTE_TIME("MUTE_TIME"),
	
	/**
	 * A list of string-locations for the chests the player is added to
	 * @dataType String List
	 */
	OWNED_CHESTS("OWNED_CHESTS"),

	/**
	 * The ItemStack list for the player's clear-inventory backup <br />
	 * This has been removed with Divinity v1.1. <b>/ci is now final.</b>
	 * @deprecated
	 */
	BACKUP_INVENTORY("BACKUP_INVENTORY"),
	
	/**
	 * The action the player is trying to perform on a chest
	 * @dataType String
	 * @outputExample view
	 * @outputExample release
	 */
	CHEST_MODE("CHEST_MODE"),
	
	/**
	 * The list of names the player is attempting to add to a chest
	 * @dataType String List
	 */
	CHEST_NAMES("CHEST_NAMES"),
	
	/**
	 * A system boolean to prevent double-rollbacks
	 * @dataType boolean
	 * @specificTo DivinitySystem
	 */
	ROLLBACK_IN_PROGRESS("ROLLBACK_IN_PROGRESS"),
	
	/**
	 * A list of things to be automatically announced per 20 minutes
	 * @dataType String List
	 * @specificTo DivinitySystem
	 */
	ANNOUNCER("ANNOUNCER"),
	
	/**
	 * A list of the player's mail
	 * @dataType String List
	 */
	MAIL("MAIL"),
	
	/**
	 * A list of the player's last known locations from teleporting
	 * @dataType String List
	 */
	PREVIOUS_LOCATIONS("PREVIOUS_LOCATIONS"),
	
	/**
	 * The name of the person who has invited them to teleport
	 * @dataType String
	 */
	TP_INVITE("TP_INVITE"),
	
	/**
	 * A boolean to determine if it is safe to teleport to the player
	 * @dataType boolean
	 */
	TP_BLOCK("TP_BLOCK"),
	
	/**
	 * A boolean to determine if the player is visible or not
	 * @dataType boolean
	 */
	VANISHED("VANISHED"),
	
	/**
	 * The queue of staff ore alerts
	 * @dataType String List
	 * @specificTo DivinitySystem
	 */
	ALERT_QUEUE("ALERT_QUEUE"),
	
	/**
	 * The name of the player who has invited you to join their alliance
	 * @dataType String
	 */
	ALLIANCE_INVITE("ALLIANCE_INVITE"),
	
	/**
	 * A boolean to determine if the player is a leader
	 * @dataType boolean
	 */
	ALLIANCE_LEADER("ALLIANCE_LEADER"),
	
	/**
	 * Determines which message to announce
	 * @dataType integer
	 * @specificTo DivinitySystem
	 */
	ANNOUNCER_INDEX("ANNOUNCER_INDEX"),
	
	/**
	 * The name of the player who most recently messaged the player
	 * @dataType String
	 */
	PREVIOUS_PM("PREVIOUS_PM"),
	
	/**
	 * The system time it must be equal or greater to in order for another firework to work
	 * @dataType long
	 */
	FIREWORK_COOLDOWN("FIREWORK_COOLDOWN"),
	
	/**
	 * A boolean used to determine if the player is disabled or not
	 * @dataType boolean
	 */
	DISABLED("DISABLED"),
	
	/**
	 * The system time it must be equal or greater to in order for the player to be un-disabled
	 * @dataType long
	 */
	DISABLE_TIME("DISABLE_TIME"),
	
	/**
	 * The running total of how much the system owes the player per 20 seconds of mob killing
	 * @dataType integer
	 */
	MOB_MONEY("MOB_MONEY"),
	
	/**
	 * The list of filtered words
	 * @dataType String List
	 * @specificTo DivinitySystem
	 */
	FILTER("FILTER"),
	
	/**
	 * A boolean used to determine if the player is in combat or not
	 * @dataType boolean
	 */
	IN_COMBAT("IN_COMBAT"),
	
	/**
	 * The name of the player's duel partner
	 * @dataType String
	 */
	DUEL_PARTNER("DUEL_PARTNER"),
	
	/**
	 * Used to determine the saftey factor of the duel <br /> All duels are safe as of Divinity v1.1
	 * @deprecated
	 */
	IS_DUEL_SAFE("IS_DUEL_SAFE"),
	
	/**
	 * A list of players the player has defeated
	 * @dataType String List
	 */
	DUEL_WINS("DUEL_WINS"),
	
	/**
	 * A boolean used to determine if it is safe to deposit XP or not
	 * @dataType boolean
	 */
	EXP_DEPOSIT("EXP_DEPOSIT"),
	
	/**
	 * The name of the player inviting the player to a duel
	 * @dataType String
	 */
	DUEL_INVITE("DUEL_INVITE"),
	
	/**
	 * A list of ring locations
	 * @dataType String List
	 * @specificTo DivinitySystem
	 */
	RING_LOCS("RING_LOCS"),
	
	/**
	 * Spaceship magic.
	 * @dataType MAGIC
	 */
	GV1("GV1"),
	
	/**
	 * Spaceship magic.
	 * @dataType MAGIC
	 */
	GV2("GV2"),
	
	/**
	 * Spaceship magic.
	 * @dataType MAGIC
	 */
	GV3("GV3"),
	
	/**
	 * Spaceship magic.
	 * @dataType MAGIC
	 */
	GV4("GV4"),
	
	/**
	 * Spaceship magic.
	 * @dataType MAGIC
	 */
	GV5("GV5"),
	
	/**
	 * The prefix in front of a staff member's personal cast
	 * @dataType String
	 * @outputExample [HugsCast]
	 */
	CAST_PREFIX("CAST_PREFIX"),
	
	/**
	 * Provides a link to the calender
	 * @dataType String
	 */
	CALENDAR_LINK("CALENDAR_LINK"),
	
	/**
	 * The system time it must be greater to or equal to in order to spawn an enderdragon
	 * @dataType long
	 */
	ENDERDRAGON_CD("ENDERDRAGON_CD"),
	
	/**
	 * A boolean used to determine if the enderdragon is dead or not
	 * @dataType boolean
	 */
	ENDERDRAGON_DEAD("ENDERDRAGON_DEAD"),
	
	/**
	 * Used in the banning system
	 */
	BAN_QUEUE("BAN_QUEUE"),
	
	/**
	 * Used to stop chat from reaching a player if they are using the banning interface
	 * @dataType boolean
	 */
	IS_BANNING("IS_BANNING"),
	
	/**
	 * A list of missed chat from being in the ban interface
	 * @dataType String List
	 */
	PAUSED_CHAT("PAUSED_CHAT"),
	
	/**
	 * A boolean used to determine if the player is disguised as a mob or not
	 * @dataType boolean
	 */
	IS_DIS("IS_DIS"),
	
	/**
	 * The entity that the player is riding in the disguise event <br /><b>NOT SAVED</b>
	 * @dataType LivingEntity
	 */
	DIS_ENTITY("DIS_ENTITY"),
	
	/**
	 * The MOTD of the server.
	 * @dataType String
	 */
	MOTD("MOTD"),
	
	/**
	 * The system time it must be greater than or equal to in order to receive another double drop from crafting
	 * @dataType long
	 */
	CRAFT_COOLDOWN("CRAFT_COOLDOWN"),
	
	/**
	 * A boolean to determine if the player is in the staff teleport mode
	 * @dataType boolean
	 */
	IS_STAFF_TP("IS_STAFF_TP"),

	/**
	 * The system time it must be greater than or equal to in order receive another friendly reminder for animal deaths
	 * @dataType long
	 */
	FR_FK_COOLDOWN("FR_FK_COOLDOWN"),
	
	/**
	 * Friendly reminder toggle status for animal deaths
	 * @dataType boolean
	 */
	FR_FK_TOGGLE("FR_FK_TOGGLE"),
	
	/**
	 * The system time it must be greater than or equal to in order receive another friendly reminder for crop deaths
	 * @dataType long
	 */
	FR_CH_COOLDOWN("FR_CH_COOLDOWN"),
	
	/**
	 * Friendly reminder toggle status for crop deaths
	 * @dataType boolean
	 */
	FR_CH_TOGGLE("FR_CH_TOGGLE"),
	
	/**
	 * The system time it must be greater than or equal to in order receive another friendly reminder for creeper deaths
	 * @dataType long
	 */
	FR_CR_COOLDOWN("FR_CR_COOLDOWN"),
	
	/**
	 * Friendly reminder toggle status for creeper deaths
	 * @dataType boolean
	 */
	FR_CR_TOGGLE("FR_CR_TOGGLE"),
	
	/**
	 * The system time it must be greater than or equal to in order receive another friendly reminder for tree deaths
	 * @dataType long
	 */
	FR_TR_COOLDOWN("FR_TR_COOLDOWN"),
	
	/**
	 * Friendly reminder toggle status for tree deaths
	 * @dataType boolean
	 */
	FR_TR_TOGGLE("FR_TR_TOGGLE"),
	
	/**
	 * A itemstack list for the player's survival inventory
	 * @dataType ItemStack List
	 */
	SURVIVAL_INVENTORY("SURVIVAL_INVENTORY"),
	
	/**
	 * A itemstack list for the player's creative inventory
	 * @dataType ItemStack List
	 */
	CREATIVE_INVENTORY("CREATIVE_INVENTORY"),
	
	/**
	 * A itemstack list for the player's adventure inventory
	 * @dataType ItemStack List
	 * @deprecated
	 */
	ADVENTURE_INVENTORY("ADVENTURE_INVENTORY"),
	
	/**
	 * A list of people who voted "yes"
	 * @dataType String List
	 * @specificTo DivinitySystem
	 */
	YES_VOTE("YES_VOTE"),
	
	/**
	 * A list of people who voted "no"
	 * @dataType String List
	 * @specificTo DivinitySystem
	 */
	NO_VOTE("NO_VOTE"),
	
	/**
	 * The vote message for the current poll
	 * @dataType String
	 * @specificTo DivinitySystem
	 */
	VOTE_MESSAGE("VOTE_MESSAGE"),
	
	/**
	 * The log of markkit transactions
	 * @dataType String List
	 */
	MARKKIT_LOG("MARKKIT_LOG"), 
	
	/**
	 * The amount of paragon points for the player
	 * @dataType int
	 */
	PARAGONS("PARAGONS"),
	
	/**
	 * The time when the player last performed the /rainoff command.
	 * @dataType long
	 */
	
	RAIN_TOGGLE("RAIN_TOGGLE");

	DPI(String info){
		this.info = info;
	}
	
	public String info;

	/**
	 * @return A safe string version of the DPI
	 */
	public String s(){
		return info;
	}
}