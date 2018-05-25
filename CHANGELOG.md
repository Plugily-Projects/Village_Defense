# Village Defense 3 Changelog

### 3.7.1 Release (25.05.2018)
* Full games permission now works for BungeeCord

### 3.7.0 Release (23.05.2018)
* New modernized and customizable scoreboard
* /vda addsign is now deprecated, use Setup menu instead - changed due to some problems
* Now signs accept %state%, %players% etc placeholders in different lines! You can swap them how you want.
* Players with MVP and Elite permissions will be able to join full arenas now (only Vip permission worked)
* By default level kits were unlocked to operator players, now they won't
* Now spectators will not have night vision after respawn

### 3.6.3 Release (11/12.05.2018)
* Added tab completer
* Added did you mean to help with wrong commands (ex. /vd leaveve > Did you mean /vd leave?)
* Added line wrapping for blocker kit and some new comments in language.yml file
* Fixed #4 bug, mysql users should be aware of that problem especially users with cloud management systems
* Now removing boss bar of player when plugin gets disabled while games are on
* Re-commented rewards.yml file and added chance condition
* Overriding WorldGuard build deny flag which disallow Villager damage by zombies
* Fixed bug where flaming arrows from player could set on fire another player
* Reworked files migrator, now it will update config and language files without losing comments!

### 3.6.2 Release (04/10.05.2018)
* Fixed /vd create command without argument gave an error
* Added PlaceholderAPI support
* Fixed permissions for Worker and Blocker kits (villagedefense.kit.%kit name% wasn't working for them)
* Updated Java Docs and API for getting sorted statistics
* Fixed errors in console when offline player's wolves killed zombies
* Update notify permission for admins with villagedefense.admin.* added
* Now doors amount in setup menu will be normal (was 2x more because 1 door is 2 blocks)
* Fixed bug where power ups were staying in game even if we left it, now power ups will remove after 40 seconds after spawn
* Added /vd top <stat> command to show top 10 users!

### 3.6.1 Hotfix (04.05.2018)
* Fixed /vda command not working on 1.8

### 3.6.0 Release (04.05.2018)
* Code improvements
* Fixed another bug from early 2.x versions with cooldown drop faster depending on arenas number (ex. 2 arenas = 2 cooldown seconds less per second!)
* Wolves are rideable now
* Golems and wolves won't get drowned now, they can swim!
* Added new zombie spawning randomly in waves higher than 21! Villager Slayer.
* Fixed shotbow bow ability was working for every different kit
* Fixed /vd admin list command
* Prettified admin commands, now you don't need wiki for extended information about commands :)
* Fixed bug where you could throw any items to the secret well

### 3.5.0 Release (28.04.2018)
* Fixed #3 bug
* Fixed particles in 1.8
* Fixed files generation without comments
* Fixed TPS loss
* Now wizard staff's attack will damage zombie if zombie is in front of you (less than 1 block away)

### 3.4.0 Release (22.04.2018)
* Fixed major memory leak since 3.2.0
* Fixed setup errors on clean installation
* Fixed title message on death not displaying properly
* Fixed typo "plugin is up to date" instead of "plugin is outdated"

### 3.3.0 Release (20.04.2018)
* Added Powerups
* Fixed titles weren't displaying (bug since 2.1.x)
* Now removing potion effects from player after force server stop
* Organised code
* Enhanced Village Debugger
* Added generic VillageEvent event class

### 3.2.0 Release (1.04.2018)
* Small fix for plugin loading errors when dependencies weren't installed and plugin was disabled
* Using metadata for village defense entities
* Fixed permission villagedefense.join.<arena_name>
* Fixed client kick when trying to get spawn eggs
* Fixed compatibility with plugins like Citizens

### 3.1.3 Release (30.03.2018)
* Fixed zombies spawning warning in console (reported by transportowiec96)
* You can now ride villagers properly in 1.11
* Removed usage of /vda setshopchest command, now you can set shop chest via setup menu only
* Implemented iron golems riding in 1.8, 1.9 and 1.11 versions
* Improved Villagers pathfinders, now they will avoid zombies
* Fixed zombie kills stats counting
* Fixed zombie kills stats counting by wolves
* Fixed shop registering when reloading arenas via /vda reload

### 3.1.2 Release (28.03.2018)
* Fixed scoreboard issue

### 3.1.1 Release (28.03.2018)
* Fixed zombie kills stats counting X times on zombie death (based on created arenas size)
* Now wolf owner will get stat for killing zombie
* Optimized Tornado and Blocker kits schedulers (even if kits were unused)
* Buffed Archer kit's arrows amount per wave from 5 to 15
* Fixed small memory leak
* Fixed bStats new config generation in bStats/resources/ folder
* Improved auto respawn
* Added messages In-Game.Spectator.Target-Player-Health and Scoreboard.Footer
* Properly clearing player inventory when server is closed/plugin gets disabled
* Hypixelized plugin (sort of)
* Fixed some typos in setup menu
* Added night vision for spectators
* Fixed restart needed after shop was created

### 3.1.0 Release
* MAJOR EXPLOIT FIX - You could get rotten fleshes infinitely from secret well using freecam (Exploit since early 2.0 versions)
* Added permissions for signs creating and breaking
* Now you properly get Spectator menu on death
* Now players that join running arena gets bonus hearts that were added before from rotten fleshes

### 3.0.1 Release
* Fixed plugin startup error when using MySQL

### 3.0.0 Release
* Added game mode, saturation and flying state saving to Inventory Manager
* ZombieFinderKit fixes
* Some arenas fixes
* Fixed migrator (v2 > v3)
* Fixed arena registering via setup menu


