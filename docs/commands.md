# Commands
***

Parentheses denote optional arguments, like '(' and ')'.
Triangle brackets ('<' and '>') denote required arguments.

* **/statz help** - Show a list of commands that can be performed.
* **/statz list (player) (stat name)** - Show a list of stats that are stored for a player. You can click on a stat to get detailed information about that specific stat.
* **/statz hooks** - Shows a list of plugins that Statz is currently hooked into.
* **/statz transfer** - Transfer data from SQLite database to your MySQL database. This command can come in handy when you have old data after your switch from SQLite to MySQL.
* **/statz transfer reverse** - Transfer data the other way around: from MySQL to SQLite database.
* **/statz migrate** - Migrate data from Stats 3 to Statz. Stats 3 has to be running to perform this command.
* **/statz purge <time>** - Remove old players from the database that have not logged in since x time.

***
# Permissions
* _statz.list.self_ - This permission is automatically given to all players. It allows a player to perform **/statz list**.
* _statz.list.others_ - This permission is only given to OP. It allows a player to check the statistics of other players.
* _statz.help_ - This permission allows players to perform **/statz help** and is automatically awarded to all players.
* _statz.hooks_ - To be able to perform **/statz hooks**, you'll need this permission. By default, only OP's have this permission.
* _statz.transfer.sqlite_ - To be able to perform transfers with **/statz transfer**, you'll need this command. By default, only OP's get this permission.
* _statz.migrate_ - This permission allows players to perform /statz migrate.
* _statz.purge_ - This permission allows players to purge players from the database.
