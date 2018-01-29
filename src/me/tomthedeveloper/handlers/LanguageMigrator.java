package me.tomthedeveloper.handlers;

import me.tomthedeveloper.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.Arrays;
import java.util.List;

public class LanguageMigrator {

    private static List<String> oldmessages =
            Arrays.asList("Teleported-To-The-Lobby", "No-Arena-Like-That", "STATS-AboveLine", "STATS-UnderLinen", "STATS-Kills", "STATS-Deaths", "STATS-Games-Played", "STATS-Hihgest-Wave", "STATS-Level", "STATS-Exp", "STATS-Next-Level-Exp", "SCOREBOARD-Header", "SCOREBOARD-Villagers", "SCOREBOARD-Zombies", "SCOREBOARD-Players-Left", "SCOREBOARD-Min-Players", "SCOREBOARD-Starting-In", "SCOREBOARD-Players", "SCOREBOARD-Next-Wave-In",
                    "SCOREBOARD-Orbs", "SCOREBOARD-Rotten-Flesh", "Kit Menu-Title", "KitUnlockedLoreInKitMenu", "KitLockedLoreInKitMenu", "Unlocks-at-level", "Unlock-This-Kit-In-The-Store", "KitChosenButNotUnlockedMessage", "KitChosenMessage", "Kit-Menu-Item-Name", "Open-Kit-Menu", "Ability-Still-On-Cooldown", "Cleaner-Kit-Name", "Cleaner-Kit-Description", "Cleaner-Wand-Name", "Cleaner-Item-Lore", "Player-has-cleaned-the-map", "Map-is-already-empty", "You-Can't-Clean-You're-Spectator", "Zombie-Teleporter-Kit-Name", "Zombie-Teleporter-Kit-Description", "Zombie-Teleporter-Name", "Zombie-Teleporter-Lore", "Knight-Kit-Name",
                    "Knight-Kit-Description", "Light-Tank-Kit-Name", "Light-Tank-Kit-Description", "Archer-Kit-Name", "Archer-Kit-Description", "Puncher-Kit-Name", "Puncher-Kit-Description", "Healer-Kit-Name", "Healer-Kit-Description", "SuperArcher-Kit-Name", "SuperArcher-Kit-Description",
                    "Looter-Kit-Name", "Looter-Kit-Description", "Runner-Kit-Name", "Runner-Kit-Description", "Medium-Tank-Kit", "Medium-Tank-Kit-Description", "Worker-Kit-Name", "Worker-Kit-Description", "Door-Placed", "Dog-Friend-Kit-Name", "Dog-Friend-Kit-Description", "Hardcore-Kit-Name", "Hardcore-Kit-Description", "Golem-Friend-Kit-Name", "Golem-Friend-Kit-Description", "Tornado-Kit-Name",
                    "Tornado-Kit-Description", "Tornado-Item-Name", "Tornado-Item-Lore", "Terminator-Kit-Name", "Terminator-Kit-Description", "Teleporter-Kit-Name", "Teleporter-Kit-Description", "Teleportion-Menu-Name", "Teleportion-Item-Lore", "Teleportation-Menu-Name", "Teleported-To-Villager", "You-Can't-Teleport-You're-Spectator", "Didn't-Found-The-Villager", "Teleported-To-Player", "Player-Not-Found", "Heavy-Tank-Kit-Name", "Heavy-Tank-Kit-Description", "Shotbow-Kit-Name", "ShotBow-Kit-Description",
                    "The-Blocker-Kit-Name", "Blocker-Kit-Description", "Blocker-Fence-Item-Name", "Blocker-Fence-Item-Lore", "Barrier-Placed",
                    "Barrier-Can't-Be-Placed-Here", "PremiumHardcore-Kit-Name", "PremiumHardcore-Kit-Description", "Medic-Kit", "Medic-Kit-Description", "The-Bunny-Kit-Name", "Jumper-Kit-Description", "YouAreAlreadyIngame", "NoPermissionToJoinFullGames", "FullGameAlreadyFullWithPermiumPlayers", "Dead-Tag-On-Death", "DEAD-SCREEN", "Died-Respawn-In-Next-Wave", "You're-Back-In-Game", "You-Are-Spectator", "You-leveled-up", "RottenFleshLevelUp", "Only-Command-Ingame-Is-Leave", "Seconds-Left-Until-Game-Starts", "Waiting-For-Players", "Enough-Players-To-Start", "The-Game-Has-Started", "KickedToMakePlaceForPremiumPlayer", "YouGotKickedToMakePlaceForAPremiumPlayer", "Join", "Death", "Leave", "Next-Wave-Starts-In",
                    "Wave-Started", "A-Villager-Has-Died", "You-Feel-Refreshed", "You-Can't-Ride-Golem-From-Somebody-Else", "Golem-Spawned", "Wolf-Spawned", "Zombie-Got-Stuck-In-The-Map", "Spawn-Golem", "Need-More-Orbs-To-Buy-this",
                    "Don't-Hit-Me-With-Weapon", "orbs-In-Shop", "All-Players-Have-Died", "All-Villagers-Have-Died", "Reached-Wave-X", "Teleporting-To-Lobby-In-10-Seconds", "Teleport-To-EndLocation-In-X-Seconds",
                    "Admin-ForceStart-Game", "Admin-Set-Starting-In-To-0", "Admin-Removed-Zombies", "Admin-Removed-Golems", "Admin-Removed-Villagers", "Admin-Removed-Zombies", "Admin-Changed-Wave", "PREFIX");
    private static List<String> migratedmessages =
            Arrays.asList("commands.Teleported-To-The-Lobby", "commands.No-Arena-Like-That", "commands.Stats-Command.Header", "commands.Stats-Command.Footer", "commands.Stats-Command.Kills", "commands.Stats-Command.Deaths", "commands.Stats-Command.Games-Played", "commands.Stats-Command.Highest-Wave", "commands.Stats-Command.Level", "commands.Stats-Command.Exp", "commands.Stats-Command.Next-Level-Exp", "Scoreboard.Header", "Scoreboard.Villagers-Left", "Scoreboard.Zombies-Left", "Scoreboard.Players-Left", "Scoreboard.Minimum-Players", "Scoreboard.Starting-In", "Scoreboard.Players", "Scoreboard.Next-Wave-In",
                    "Scoreboard.Orbs", "Scoreboard.Rotten-Flesh", "kits.Kit-Menu.Title", "kits.Kit-Menu.Unlocked-Kit-Lore", "kits.Kit-Menu.Locked-Lores.Locked-Lore", "kits.Kit-Menu.Locked-Lores.Unlock-At-Level", "kits.Kit-Menu.Locked-Lores.Unlock-In-Store", "kits.Not-Unlocked-Message", "kits.Choose-Message", "kits.Kit-Menu-Item-Name", "kits.Open-Kit-Menu", "kits.Ability-Still-On-Cooldown", "kits.Cleaner.Kit-Name", "kits.Cleaner.Kit-Description", "kits.Cleaner.Game-Item-Name", "kits.Cleaner.Game-Item-Lore", "kits.Cleaner.Cleaned-Map", "kits.Cleaner.Nothing-To-Clean", "kits.Cleaner.Spectator-Warning", "kits.Zombie-Teleporter.Kit-Name", "kits.Zombie-Teleporter.Kit-Description", "kits.Zombie-Teleporter.Game-Item-Name", "kits.Zombie-Teleporter.Game-Item-Lore", "kits.Knight.Kit-Name",
                    "kits.Knight.Kit-Description", "kits.Light-Tank.Kit-Name", "kits.Light-Tank.Kit-Description", "kits.Archer.Kit-Name", "kits.Archer.Kit-Description", "kits.Puncher.Kit-Name", "kits.Puncher.Kit-Description", "kits.Healer.Kit-Name", "kits.Healer.Kit-Description", "kits.Super-Archer.Kit-Name", "kits.Super-Archer.Kit-Description",
                    "kits.Looter.Kit-Name", "kits.Looter.Kit-Description", "kits.Runner.Kit-Name", "kits.Runner.Kit-Description", "kits.Medium-Tank.Kit-Name", "kits.Medium-Tank.Kit-Description", "kits.Worker.Kit-Name", "kits.Worker.Kit-Description", "kits.Worker.Game-Item-Place-Message", "kits.Dog-Friend.Kit-Name", "kits.Dog-Friend.Kit-Description", "kits.Hardcore.Kit-Name", "kits.Hardcore.Kit-Description", "kits.Golem-Friend.Kit-Name", "kits.Golem-Friend.Kit-Description", "kits.Tornado.Kit-Name",
                    "kits.Tornado.Kit-Description", "kits.Tornado.Game-Item-Name", "kits.Tornado.Game-Item-Lore", "kits.Terminator.Kit-Name", "kits.Terminator.Kit-Description", "kits.Teleporter.Kit-Name", "kits.Teleporter.Kit-Description", "kits.Teleporter.Game-Item-Name", "kits.Teleporter.Game-Item-Lore", "kits.Teleporter.Game-Item-Menu-Name", "kits.Teleporter.Teleported-To-Villager", "kits.Teleporter.Spectator-Warning", "kits.Teleporter.Villager-Warning", "kits.Teleporter.Teleported-To-Player", "kits.Teleporter.Player-Not-Found", "kits.Heavy-Tank.Kit-Name", "kits.Heavy-Tank.Kit-Description", "kits.Shot-Bow.Kit-Name", "kits.Shot-Bow.Kit-Description",
                    "kits.Blocker.Kit-Name", "kits.Blocker.Kit-Description", "kits.Blocker.Game-Item-Name", "kits.Blocker.Game-Item-Lore", "kits.Blocker.Game-Item-Place-Message",
                    "kits.Blocker.Game-Item-Place-Fail", "kits.Premium-Hardcore.Kit-Name", "kits.Premium-Hardcore.Kit-Description", "kits.Medic.Kit-Name", "kits.Medic.Kit-Description", "kits.Bunny.Kit-Name", "kits.Bunny.Kit-Description", "In-game.Already-Playing", "In-game.Full-Game-No-Permission", "In-game.No-Slots-For-Premium", "In-game.Dead-Tag-On-Death", "In-game.Death-Screen", "In-game.Died-Respawn-In-Next-Wave", "In-game.Back-In-Game", "In-game.You-Are-Spectator", "In-game.You-Leveled-Up", "In-game.Rotten-Flesh-Level-Up", "In-game.Only-Command-Ingame-Is-Leave", "In-game.Messages.Lobby-Messages.Start-In", "In-game.Messages.Lobby-Messages.Waiting-For-Players", "In-game.Messages.Lobby-Messages.Enough-Players-To-Start", "In-game.Messages.Lobby-Messages.Game-Started", "In-game.Messages.Lobby-Messages.Kicked-For-Premium-Slot", "In-game.Messages.Lobby-Messages.You-Were-Kicked-For-Premium-Slot", "In-game.Messages.Join", "In-game.Messages.Death", "In-game.Messages.Leave", "In-game.Messages.Next-Wave-In",
                    "In-game.Messages.Wave-Started", "In-game.Messages.Villager-Died", "In-game.Messages.You-Feel-Refreshed", "In-game.Messages.Cant-Ride-Others-Golem", "In-game.Messages.Golem-Spawned", "In-game.Messages.Wolf-Spawned", "In-game.Messages.Zombie-Got-Stuck-In-The-Map", "In-game.Messages.Shop-Messages.Golem-Item-Name", "In-game.Messages.Shop-Messages.Not-Enough-Orbs",
                    "In-game.Messages.Shop-Messages.Rude-Message", "In-game.Messages.Shop-Messages.Currency-In-Shop", "In-game.Messages.Game-End-Messages.All-Players-Died", "In-game.Messages.Game-End-Messages.All-Villagers-Died", "In-game.Messages.Game-End-Messages.Reached-Wave-X", "In-game.Messages.Game-End-Messages.Teleporting-To-Lobby-In-10-Seconds", "In-game.Messages.Game-End-Messages.Teleporting-To-Lobby-In-X-Seconds",
                    "In-game.Messages.Admin-Messages.Force-Start-Game", "In-game.Messages.Admin-Messages.Set-Starting-In-To-0", "In-game.Messages.Admin-Messages.Removed-Zombies", "In-game.Messages.Admin-Messages.Removed-Golems", "In-game.Messages.Admin-Messages.Removed-Villagers", "In-game.Messages.Admin-Messages.Removed-Zombies", "In-game.Messages.Admin-Messages.Changed-Wave", "In-game.Plugin-Prefix");

    public static void initiateMigration() {
        System.out.println("[VillageDefense] Initiated language.yml migration process! (File-Version: 1)");
        int counter = 0;
        int nomessages = 0;
        for(String oldmessage : LanguageManager.getLanguageFile().getKeys(false)) {
            if(oldmessages.contains(oldmessage)) {
                for(int i = 0; i < oldmessages.size(); i++) {
                    if(oldmessages.get(i).equals(oldmessage)) {
                        LanguageManager.getLanguageFile().set(migratedmessages.get(i), LanguageManager.getLanguageFile().get(oldmessage));
                        LanguageManager.getLanguageFile().set(oldmessage, null);
                        counter++;
                    }
                }
            }
        }
        LanguageManager.saveLanguageFile();
        for(String newmessage : migratedmessages) {
            if(!LanguageManager.getLanguageFile().isSet(newmessage)) {
                LanguageManager.getLanguageFile().set(newmessage, "MESSAGE NOT FOUND! Either fill this one in yourself or delete this file to create a entire new one!");
                System.out.println("[VillageDefense] Message " + newmessage + " doesn't exists in your old language.yml!");
                nomessages++;
            }
        }
        LanguageManager.getLanguageFile().set("In-game.Plugin-Prefix", "&a[Village Defense] ");
        LanguageManager.getLanguageFile().set("File-Version", 1);
        LanguageManager.saveLanguageFile();
        System.out.println("[VillageDefense] Successfully migrated language.yml to new format! Changed " + counter + " lines!");
        if(nomessages > 0) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[VillageDefense] WARNING! Your old language.yml didn't have all messages needed for migration,");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "please backup 'language.yml' file and generate new to copy needed messages to file from backup!");
        }
    }

    public static void ineffectiveFileUpdate() {
        if(LanguageManager.getLanguageMessage("File-Version").equals("1")) {
            if(Main.isDebugged()) {
                System.out.println("[Village Debugger] Language file is updating please wait...");
            }
            LanguageManager.getLanguageFile().set("In-game.Villager-Names", "Jagger,Kelsey,Kelton,Haylie,Harlow,Howard,Wulffric,Winfred,Ashley,Bailey,Beckett,Alfredo,Alfred,Adair,Edgar,ED,Eadwig,Edgaras,Buckley,Stanley,Nuffley,Mary,Jeffry,Rosaly,Elliot,Harry,Sam,Rosaline,Tom,Ivan,Kevin,Adam");
            LanguageManager.getLanguageFile().set("File-Version", "2");
            LanguageManager.saveLanguageFile();
        }
        if(LanguageManager.getLanguageMessage("File-Version").equals("2")) {
            if(Main.isDebugged()) {
                System.out.println("[Village Debugger] Language file is updating please wait...");
            }
            LanguageManager.getLanguageFile().set("In-game.Game-Chat-Format", "&6[&5%level%&6]&6[%kit%&6] %player%: &f%message%");
            LanguageManager.getLanguageFile().set("File-Version", "3");
            LanguageManager.saveLanguageFile();
        }
        if(LanguageManager.getLanguageMessage("File-Version").equals("3")) {
            if(Main.isDebugged()) {
                System.out.println("[Village Debugger] Language file is updating please wait...");
            }
            LanguageManager.getLanguageFile().set("In-game.Spectator.Spectator-Item-Name", "&7Players list");
            LanguageManager.getLanguageFile().set("In-game.Spectator.Spectator-Menu-Name", "&lSpectator menu");
            LanguageManager.getLanguageFile().set("File-Version", "4");
            LanguageManager.saveLanguageFile();
        }
    }

}
