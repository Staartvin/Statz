# How to disable statistics?

## What is it and why is it useful?

The `disabled-stats.yml` allows you to disable any statistic in under specific circumstances. For example, you can disable the tracking of kills in a certain _WorldGuard_ arena or in a _GriefPrevention_ claim. As of now, these 'scenarios' are supported:

* WorldGuard regions: disable a statistic in a WorldGuard region
* GriefPrevention claims: disable a statistic in the claim of any user.

For more info on how to set this up, read on!

## How to configure the file?

To configure the disabled-stats.yml, you can use any file editor. Each statistic can be specified only once, but for more than one scenario's simultaneously. Let's take a look at a sample file:

![Example of config file](https://i.imgur.com/h5ibohD.png)

The image shows an example of how you can define WorldGuard regions and GriefPrevention claims. A WorldGuard region needs a region name, so Statz can check whether the player is in that region. The GriefPrevention claim needs a UUID of a player to check whether a random player is in the claim of the player the UUID corresponds to.

You can see that the DISTANCE\_TRAVELLED stat will not be updated whenever a player is in a WorldGuard region called 'TestRegion' or 'Lala'. Similarly, this stat will not be updated whenever a player is in the GriefPrevention claim area that corresponds to one of those UUIDs.

