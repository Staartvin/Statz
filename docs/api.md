# Developer's API
You're probably a developer and you want to know how Statz works and how to obtain data from Statz through its API.
It's actually very easy. There are two methods that you can use to obtain info from players via Statz.

## The Javadoc can be found [here](http://62.131.38.74:8080/job/Statz/javadoc/).

First, you'll have to get the API class of Statz:

    Plugin statz = Bukkit.getServer().getPluginManager().getPlugin("Statz");
    
    if (statz != null && statz.isEnabled()) {
    StatzAPI api = statz.getStatzAPI();
    }
    

Now, you can either choose to use the _getTotalOf()_ method to obtain a total value of a stat. You can also get the total amount of a stat per world. Examples of using _getTotalOf()_ are when you want to get the total number of votes of player, regardless of the world he voted on, or the total number of mob kills of a player on one specific world.

To use this method, you'll need to provide a _PlayerStat_, a _UUID_ of the player and a specific _worldName_. If you don't care about the world, you can just leave this as **null**. **If Statz does not have the requested data, it will return null.**

For example, to get the total amount of votes regardless of the world:

    
    Double totalVotes = api.getTotalOf(PlayerStat.VOTES, UUID.fromString("c5f39a1d-3786-46a7-8953-d4efabf8880d"), null);
    

Another example, to get the total amount of mobs killed on the world 'Slaughterhouse':

    
    Double totalMobsKilled = api.getTotalOf(PlayerStat.MOBS_KILLS, UUID.fromString("c5f39a1d-3786-46a7-8953-d4efabf8880d"), "Slaughterhouse");
    


This is fun and all, but let's say you want to know the number of cows a player killed on world 'Lala'. Since this is a more specific request, you will need to use _getSpecificData()_. This method is almost the same as _getTotalOf_ except that it allows you to specify certain conditions, called _RowRequirements_. You can have an unlimited amount of requirements, but the more requirements you have, the more specific a request is. Let's get back to our example.

To obtain the amount of cows a player killed on world 'Lala', we'll have to run this code:

    
    Double totalCowsKilled = api.getSpecificData(PlayerStat.MOBS_KILLS, UUID.fromString("c5f39a1d-3786-46a7-8953-d4efabf8880d"), new RowRequirement("mob", "COW"), new RowRequirement("world", "Lala"));
    

Each RowRequirement has two inputs; the first is the name of the column in the database (you can find the different column names [here](https://github.com/Staartvin/Statz/wiki/How-does-Statz-record-its-data%3F)), the second is the value it needs to have. The first RowRequirement says that we only want to count the data that has value *COW* for column *mob*. The second RowRequirement tells Statz to only look for data that also has value *Lala* for column *world*. Both these conditions need to be met.

***

## Events fired by Statz
As of Statz 1.3.3.2, Statz will fire an event ('[UpdateDataEvent](https://github.com/Staartvin/Statz/blob/master/src/me/staartvin/statz/api/events/UpdateDataEvent.java)') when data changes are sent to its database (either SQLite or MySQL). This event has two handy methods to obtain the newly updated data from the database without having to check every x seconds whether an update has occured. 

### Method 1: getUpdatedInfo(PlayerStat)
When the UpdateDataEvent is fired, it allows you, as a developer, to obtain the freshly updated data that has been sent to the database. To retrieve the data, you should create a listener, like normal, and listen for the UpdateDataEvent (in me.staartvin.statz.api.events package). The getUpdatedInfo() method asks for one parameter, namely the stat type. Since it updates its database in bulk, it will send a lot of data simultaneously, hence you need a way to sort this data. You can retrieve updated data based on the PlayerStat. This method will return a list of PlayerInfo objects that might look confusing to you. Read on to learn more.

### PlayerInfo object
The PlayerInfo class represents data from the database in almost the same way as a relational database would. Here is an example of the DISTANCE_TRAVELLED table: 

![Example of database entries](https://cdn.pbrd.co/images/pXxSMl7di.png). 

This image shows 4 rows with a number of columns. In this table, there are 5 columns; **ID**, **UUID**, **VALUE**, **WORLD** and **MOVETYPE**. A PlayerInfo object represents this table in a more programmatical way. You can use getRow(rowNumber) to get a specific row number and getValue(rowNumber, columnName) to get the specific value of a certain column in a certain row. You can also use getTotalValue(RowRequirements) to get very detailed and specific information.

Let's give an example. Let's assume Statz has just updated the FOOD_EATEN statistic of a few players. We want to get the number of apples a player has eaten on world 'foodWorld'. We can perform the following code:

    List<PlayerInfo> playerInfos = event.getUpdatedInfo(PlayerStat.FOOD_EATEN);
		
		for (PlayerInfo playerInfo: playerInfos) {
			// Incorrect PlayerInfo object.
			if (playerInfo == null || !playerInfo.isValid()) {
				return;
			}
			
			double totalValue = playerInfo.getTotalValue(new RowRequirement("foodEaten", "APPLE"), new RowRequirement("world", "foodWorld"));
			
			System.out.println("Player " + playerInfo.getUUID() + " has now eaten " + totalValue + " apples!");
		}


Every statistic has its own table and so I appeal to you to see the structure of each table of every statistic [here](https://github.com/Staartvin/Statz/wiki/How-does-Statz-record-its-data%3F#data-stored-in-statzs-database).

### Method 2: getUpdatedQueries(PlayerStat)
This method returns the exact queries that were sent to the database. In this way, you can fiddle yourself with the given data in a raw format and can see what the incremental changes (update value) are instead of just the absolute change (final value). I will not go into detail on how to read data from these queries, but I will provide a final example on how to listen to an UpdateDataEvent below.


### Final example: How to set up your listener
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onUpdateDateEvent(final UpdateDataEvent event) {

		List<PlayerInfo> playerInfos = event.getUpdatedInfo(PlayerStat.FOOD_EATEN);
		
		for (PlayerInfo playerInfo: playerInfos) {
			// Incorrect PlayerInfo object.
			if (playerInfo == null || !playerInfo.isValid()) {
				return;
			}
			
			double totalValue = playerInfo.getTotalValue(new RowRequirement("foodEaten", "APPLE"), new RowRequirement("world", "foodWorld"));
			
			System.out.println("Player " + playerInfo.getUUID() + " has now eaten " + totalValue + " apples!");
		}
	}