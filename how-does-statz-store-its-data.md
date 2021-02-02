# How does Statz store its data?

Statz records quite a bit of data, as can be seen [here](https://www.spigotmc.org/resources/statz.25969/). To make sure this data is properly stored and easy and efficient to retrieve, Statz uses either [SQLite](https://www.sqlite.org/) or [MySQL](https://www.mysql.com/) \(depending on the choice of the configuration options\). Since this page is not meant for an introductory course on SQL and database, I'll provide you a link to a tutorial on the internet: [http://www.sqlcourse.com/intro.html](http://www.sqlcourse.com/intro.html).

Table of contents:

* [Data stored in Statz's database](https://github.com/Staartvin/Statz/wiki/How-does-Statz-record-its-data%3F#data-stored-in-statzs-database)
* [Data not stored in Statz's database, but still retrievable by Statz](https://github.com/Staartvin/Statz/wiki/How-does-Statz-record-its-data%3F#data-not-stored-in-statzs-database-but-retrievable-by-statz)  

## Data stored in Statz's database

Statz records a multitude of different datasets and arranges them in tables. For example, Statz has a table called 'statz_distance\_travelled' that contains all info about the ways players travel. Every table will be discussed here. Every table consists of columns that hold specific info about an action. The identifier and info stored by each column vary over tables, but every table has at least an \_id_ column that will track the changes across the table.

### arrows\_shot

This table holds info about arrows that were shot by players. It has 4 columns \(excluding the id column\): _uuid_, _value_, _world_ and _forceShot_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of arrows a player has fired.  
The WORLD column stores on what world the arrow was fired.  
The FORCESHOT column stores in with what force the arrow was shot. This can be anything from 0.0 to 1.0.

### blocks\_broken

This table holds all info about the blocks a player has broken. It has 5 columns \(excluding the id column\): _uuid_, _value_, _world_, _typeid_ and _datavalue_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of blocks that were broken for a specific block.  
The WORLD column stores on what world the blocks were broken.

Penultimately, the TYPEID column stores the id of the block that was broken. For example, a torch has id 50 \(for reference, see [http://www.minecraftwiki.net/images/8/8c/DataValuesBeta.png](http://www.minecraftwiki.net/images/8/8c/DataValuesBeta.png)\).

Lastly, the DATAVALUE column stores the extra data that is sometimes associated with the broken block. For example, a torch does not have a data value, but wool \(id 35\) has a data value for each color \(white wool has data value 0, while black wool has data value 15\).

### blocks\_placed

This table has exactly the same columns as _**statz\_blocks\_broken**_, but is associated with placed blocks instead of broken ones.

### buckets\_emptied

This table holds info about players that emptied buckets. It has 3 columns \(excluding the id column\): _uuid_, _value_ and _world_ The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the amount of buckets a player has emptied.  
The WORLD column stores on what world the bucket was emptied by the player.

### buckets\_filled

This table stores exactly the same things as **buckets\_emptied**, but it is associated with buckets that were filled instead of emptied.

### commands\_performed

This table stores the commands that were performed by players. It has 5 columns \(excluding the id column\): _uuid_, _value_, _world_, _command_ and _arguments_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a command was performed. The WORLD column stores on what world the command was performed. The COMMAND column stores what command was performed. Together with the ARGUMENTS column, the whole argument syntax is provided. The COMMAND column only stores the first word \(or letter\) and the others words \(arguments/parameters\) are stored in the ARGUMENTS column. For example, when a player performs **/statz check Staartvin**, the COMMAND column will be filled by '/statz' and the ARGUMENTS column will store 'check Staartvin'.

### damage\_taken

This table holds info about players that took damage in some way or form. It has 4 columns \(excluding the id column\): _uuid_, _value_, _world_ and _cause_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the amount of damage a player has taken.  
The WORLD column stores on what world the damage was taken by the player.  
The CAUSE column stores in what way the player was hurt. It can take any of the following forms: [https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/event/entity/EntityDamageEvent.DamageCause.html).

### deaths

The deaths table is used to store info regarding the deaths of any player on the server. It has 3 columns \(excluding the id column\): _uuid_, _value_ and _world_. The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores how many times a player has died. The WORLD column stores on what world the player has died.

### distance\_travelled

This table consists of data used to track the movement of players. It has 4 columns \(excluding the id column\): _uuid_, _value_, _world_ and _moveType_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores how far a player has travelled. The WORLD column stores on what world the player travelled. The MOVETYPE column stores in what way the player travelled. This can either be:

* BOAT
* FLY
* MINECART
* PIG
* PIG IN MINECART
* HORSE
* HORSE IN MINECART
* FLY WITH ELYTRA

### eggs\_thrown

This table holds info about players that threw eggs. It has 3 columns \(excluding the id column\): _uuid_, _value_ and _world_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of eggs a player has thrown. The WORLD column stores on what world the egg was thrown.

### entered\_beds

This table stores when a player went to sleep. It has 3 columns \(excluding the id column\): _uuid_, _value_ and _world_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player went to bed. The WORLD column stores on what world the player went to sleep.

### food\_eaten

This table holds info about what players have eaten. It has 4 columns \(excluding the id column\): _uuid_, _value_, _world_ and _foodEaten_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player has eaten a specific food item. The WORLD column stores on what world the food was omnomnom'd. The FOODEATEN column stores what food item was digested.

### items\_caught

This table holds info about players that caught some item while fishing. It has 4 columns \(excluding the id column\): _uuid_, _value_, _world_ and _caught_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player caught a specific item. The WORLD column stores on what world the item was caught. The CAUGHT column stores what item was caught. \(A written book, a certain fish species, an old bottle of liquor, an old boot, etc.\)

### items\_crafted

This table holds info about players that crafted something. It has 4 columns \(excluding the id column\): _uuid_, _value_, _world_ and _item_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player crafted a specific item. The WORLD column stores on what world the item was crafted. The ITEM column stores what was exactly crafted.

### items\_dropped

This table holds info about players that dropped something. It has 4 columns \(excluding the id column\): _uuid_, _value_, _world_ and _item_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player dropped a specific item. The WORLD column stores on what world the item was dropped. The ITEM column stores what was exactly dropped.

### items\_picked\_up

This table holds info about players that picked up something. It has 4 columns \(excluding the id column\): _uuid_, _value_, _world_ and _item_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player picked up a specific item. The WORLD column stores on what world the item was picked up. The ITEM column stores what was exactly picked up.

### joins

This table holds info about players that joined the server. It has 2 columns \(excluding the id column\): _uuid_ and _value_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player has joined the server.

### kills\_mobs

This table holds info about players that killed a mob. It has 5 columns \(excluding the id column\): _uuid_, _value_, _world_, _mob_ and _weapon_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player killed a specific mob. The WORLD column stores on what world the mob was killed. The MOB column stores what mob was killed by the player. the WEAPON column stores what weapon was used to kill the mob.

### kills\_players

This table holds info about players that killed other players. It has 4 columns \(excluding the id column\): _uuid_, _value_, _world_ and _playerKilled_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player killed another player. The WORLD column stores on what world the other player was killed. The MOB column stores what other player was killed by the player.

### players

This table holds info about the name and UUID of a player. It has 2 columns \(excluding the id column\): _uuid_ and _playerName_.  
The UUID column stores, coincidentally, the UUID of the player.  
The PLAYERNAME column stores the name of the player \(with a specific UUID\) when it joined the server.

### teleports

This table holds info about players that teleported. It has 5 columns \(excluding the id column\): _uuid_, _value_, _world_, _destWorld_ and _cause_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player has teleported. The WORLD column stores what world the player teleported from. The DESTWORLD column stores what world the player teleported to. The CAUSE column stores what the cause was of the teleport. It the cause was unknown, UNKNOWN will be provided as cause.

### time\_played

This table holds info about the time players played on the server. It has 3 columns \(excluding the id column\): _uuid_, _value_ and _world_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of minutes a player has been online. The WORLD column stores on what world the player has played for x minutes.

### times\_kicked

This table holds info about players that were kicked. It has 4 columns \(excluding the id column\): _uuid_, _value_, _world_ and _reason_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player has been kicked. The WORLD column stores what world the player was kicked. The REASON column stores what the specified reason was for the kick. If no reason was provided by the 'kicker', the reason will be 'Kicked from server.'.

### times\_shorn

This table holds info about players that have shorn sheep. It has 3 columns \(excluding the id column\): _uuid_, _value_ and _world_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player has shorn a sheep. The WORLD column stores on what world the player has shorn a sheep.

### tools\_broken

This table holds info about players that broke their tools. It has 4 columns \(excluding the id column\): _uuid_, _value_, _world_ and _item_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player has broken a type of tool. The WORLD column stores on what world the player has broken the tool. The ITEM column stores what tool was broken.

### villager\_trades

This table holds info about players that traded with villagers. It has 4 columns \(excluding the id column\): _uuid_, _value_, _world_ and _trade_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player has traded with a villager. The WORLD column stores on what world the player has performed the traded. The TRADE column stores what item the player received from the villager.

### votes

This table holds info about players that voted via Votifier or NuVotifier. It has 2 columns \(excluding the id column\): _uuid_ and _value_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player has voted for the server.

### worlds\_changed

This table holds info about players that changed worlds \(via portals or commands\). It has 4 columns \(excluding the id column\): _uuid_, _value_, _world_ and _destWorld_.  
The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the number of times a player has switched worlds. The WORLD column stores from what world the player switched. The DESTWORLD column stores to what world the player switched.

### xp\_gained

This table holds info about players that gained exp. It has 3 columns \(excluding the id column\): _uuid_, _value_ and _world_. The UUID column stores, coincidentally, the UUID of the player.  
The VALUE column stores the amount of xp that was gained. The WORLD column stores on what world the player gained xp.

## Data not stored in Statz's database, but retrievable by Statz

Besides storing a lot of information of players, Statz also manages data connection between other third-party plugins.

#### MCMMO

Statz tracks the level of each individual skill for every player and their total power level.

#### AcidIsland

Statz tracks island level of each player.

#### ASkyBlock

Much like AcidIsland, Statz tracks island level of each player.

#### AFKTerminator

Statz can check whether a player is AFK according to AFKTerminator

#### EssentialsX

Statz tracks whether a player is AFK \(according to EssentialsX\) and the \(estimated\) geo-location of their IP.

#### Factions

Statz can track the faction power of each player in a faction.

#### Jobs

Statz tracks multiple things:

* Current points in a job
* Total points across all jobs
* Current XP in a current job
* Current level of a specific job

#### OnTime

Statz tracks play time of a player.

#### RoyalCommands

Statz can track whether a player is AFK.

#### Stats 3

Statz can track all data of Stats 3 and also import data from Stats 3 \(see **/statz migrate**\).

#### UltimateCore

Statz tracks whether players are AFK.

#### Vault

Statz uses Vault to connect to permissions plugins and economy plugins, hence it can check for permissions and for money of a player.

#### WorldGuard

Statz can track in what region a player currently resides

#### GriefPrevention

Statz tracks a few things:

* Number of claims of a player
* Number of claimed blocks of a player
* Number of remaining blocks a player can still claim
* Number of bonus blocks a player is allowed to claim

