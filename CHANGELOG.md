# Village Defense 3 Changelog

### 3.3.0 Beta (04.08.2018)
* Added Powerups (BETA)
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


