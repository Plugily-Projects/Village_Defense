
/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2022  Plugily Projects - maintained by Tigerpanzer_02 and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package plugily.projects.villagedefense.handlers.setup;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 04.01.2022
 */
public class SetupInventory {
}/* extends PluginSetupInventory {

  private final Main plugin;
  private Arena arena;
  private final Player player;


  public SetupInventory(Main plugin, @Nullable PluginArena arena, Player player) {
    super(plugin, arena, player);
    this.plugin = plugin;
    this.player = player;
    setArena(player, arena);
    open();
  }

  public SetupInventory(Main plugin, @Nullable PluginArena arena, Player player, SetupUtilities.InventoryStage inventoryStage) {
    super(plugin, arena, player, inventoryStage);
    this.plugin = plugin;
    this.player = player;
    setArena(player, arena);
    open();
  }

  @Override
  public void setArena(Player player, PluginArena arena) {
    if(arena == null && plugin.getSetupUtilities().getArena(player) != null) {
      this.arena = plugin.getArenaRegistry().getArena(plugin.getSetupUtilities().getArena(player).getId());
      setInventoryStage(SetupUtilities.InventoryStage.PAGED_GUI);
    } else if(arena != null) {
      this.arena = plugin.getArenaRegistry().getArena(arena.getId());
    } else {
      this.arena = null;
    }
    setArena(this.arena);
  }

  @Override
  public void addExternalItems(NormalFastInv inv) {
    switch(getInventoryStage()) {
      case SETUP_GUI:
        break;
      case ARENA_LIST:
        break;
      case PAGED_GUI:
        break;
      case PAGED_VALUES:
        break;
      case PAGED_BOOLEAN:
        break;
      case PAGED_COUNTABLE:
        break;
      case PAGED_LOCATIONS:
        inv.setItem(21, new LocationItem(new ItemBuilder(Material.ROTTEN_FLESH)
            .name(new MessageBuilder("&e&lAdd Zombie Location").build())
            .lore(ChatColor.GRAY + "Click add new zombie spawn")
            .lore(ChatColor.GRAY + "on the place you're standing at.")
            .lore("", plugin.getSetupUtilities().isOptionDoneSection("zombiespawns", 2, this))
            .lore("", new MessageBuilder("&8Right Click to remove all spawns").build())
            .build(), e -> {
          e.getWhoClicked().closeInventory();
          if(e.getClick() == ClickType.RIGHT) {
            removeSection(SectionType.ZOMBIE_SPAWN);
            return;
          }

          addSection(player.getLocation(), SectionType.ZOMBIE_SPAWN);
        }, event -> {
          switch(event.getAction()) {
            case LEFT_CLICK_AIR:
              addSection(event.getPlayer().getLocation(), SectionType.ZOMBIE_SPAWN);
              break;
            case LEFT_CLICK_BLOCK:
              addSection(event.getClickedBlock().getRelative(0, 1, 0).getLocation(), SectionType.ZOMBIE_SPAWN);
              break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
              removeSection(SectionType.ZOMBIE_SPAWN);
              break;
          }
        }, true, true, false));


        inv.setItem(23, new LocationItem(new ItemBuilder(Material.EMERALD_BLOCK)
            .name(new MessageBuilder("&e&lAdd Villager Location").build())
            .lore(ChatColor.GRAY + "Click add new villager spawn")
            .lore(ChatColor.GRAY + "on the place you're standing at.")
            .lore("", plugin.getSetupUtilities().isOptionDoneSection("villagerspawns", 2, this))
            .lore("", new MessageBuilder("&8Right Click to remove all spawns").build())
            .build(), e -> {
          e.getWhoClicked().closeInventory();
          if(e.getClick() == ClickType.RIGHT) {
            removeSection(SectionType.VILLAGER_SPAWN);
            return;
          }

          addSection(player.getLocation(), SectionType.VILLAGER_SPAWN);
        }, event -> {
          switch(event.getAction()) {
            case LEFT_CLICK_AIR:
              addSection(event.getPlayer().getLocation(), SectionType.VILLAGER_SPAWN);
              break;
            case LEFT_CLICK_BLOCK:
              addSection(event.getClickedBlock().getRelative(0, 1, 0).getLocation(), SectionType.VILLAGER_SPAWN);
              break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
              removeSection(SectionType.VILLAGER_SPAWN);
              break;
          }
        }, true, true, false));

        inv.setItem(37, new LocationItem(new ItemBuilder(XMaterial.OAK_DOOR.parseItem())
            .name(new MessageBuilder("&e&lAdd Game Door").build())
            .lore(ChatColor.GRAY + "Target arena door and click this.")
            .lore(ChatColor.DARK_GRAY + "(doors are required and will be")
            .lore(ChatColor.DARK_GRAY + "regenerated each game, villagers will hide")
            .lore(ChatColor.DARK_GRAY + "in houses so you can put doors there)")
            .lore("", plugin.getSetupUtilities().isOptionDoneSection("doors", 1, this))
            .lore("", new MessageBuilder("&8Right Click to remove all locations").build())
            .build(), e -> {
          e.getWhoClicked().closeInventory();
          if(e.getClick() == ClickType.RIGHT) {
            removeDoors();
            return;
          }
          addDoors(player.getTargetBlock(null, 10));
        }, event -> {
          switch(event.getAction()) {
            case LEFT_CLICK_AIR:
              new MessageBuilder("&c&l✘ &cYou need to break a door!").player(player).sendPlayer();
              break;
            case LEFT_CLICK_BLOCK:
              addDoors(event.getClickedBlock());
              break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
              removeDoors();
              break;
          }
        }, true, true, false));

        inv.setItem(30, new LocationItem(new ItemBuilder(Material.CHEST)
            .name(new MessageBuilder("&e&lSet Game Shop").build())
            .lore(ChatColor.GRAY + "Look at (double-) chest with items")
            .lore(ChatColor.GRAY + "and click it to set it as game shop.")
            .lore(ChatColor.DARK_GRAY + "(it allows to click villagers to buy game items)")
            .lore(ChatColor.RED + "Remember to set item prices for the game")
            .lore(ChatColor.RED + "using /vda setprice command!")
            .build(), e -> {
          e.getWhoClicked().closeInventory();
          if(e.getClick() == ClickType.RIGHT) {
            removeGameShop();
            return;
          }
          addGameShop();
        }, event -> {
          switch(event.getAction()) {
            case LEFT_CLICK_AIR:
              new MessageBuilder("&c&l✘ &cYou need to break a game shop!").player(player).sendPlayer();
              break;
            case LEFT_CLICK_BLOCK:
              addGameShop();
              break;
            case RIGHT_CLICK_AIR:
            case RIGHT_CLICK_BLOCK:
              removeGameShop();
              break;
          }
        }, true, true, false));
        break;
      default:
        break;
    }
    inv.refresh();
  }

  private void removeGameShop() {
    plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".shop", null);
    arena.setReady(false);
    new MessageBuilder("&e✔ Removed | &aGame Shop location for arena " + arena.getId() + "!").player(player).sendPlayer();
    ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
    arena.reloadShopManager();
  }

  private void addGameShop() {
    Block targetBlock = player.getTargetBlock(null, 10);
    if(targetBlock.getType() != Material.CHEST) {
      new MessageBuilder("&c&l✘ &cLook at the chest! You are targeting something else!").player(player).sendPlayer();
      return;
    }
    boolean found = false;
    for(ItemStack stack : ((Chest) targetBlock.getState()).getBlockInventory()) {
      if(stack == null) {
        continue;
      }

      org.bukkit.inventory.meta.ItemMeta meta = stack.getItemMeta();
      List<String> lore;

      if(meta != null && meta.hasLore() && (lore = ComplementAccessor.getComplement().getLore(meta)).get(lore.size() - 1)
          .contains(new MessageBuilder("IN_GAME_MESSAGES_VILLAGE_SHOP_CURRENCY").build())) {
        found = true;
        break;
      }
    }
    if(!found) {
      new MessageBuilder("&c&l✖ &cWarning | No items in shop have price set! Set their prices using &6/vda setprice&c!").player(player).sendPlayer();
    }
    LocationSerializer.saveLoc(plugin, plugin.getSetupUtilities().getConfig(), "arenas", "instances." + arena.getId() + ".shop", targetBlock.getLocation());
    player.sendMessage(ChatColor.GREEN + "Shop for chest set!");
    new MessageBuilder("&e&lTIP: &7You can use special items in shops! Check out https://wiki.plugily.xyz/villagedefense/support/faq#special-shop-items").player(player).sendPlayer();
    ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
    arena.reloadShopManager();
  }

  private void addDoors(Block block) {
    Material door = block.getType();
    if(!MaterialUtils.isDoor(door)) {
      new MessageBuilder("&c&l✘ &cTarget block is not an wood door!").player(player).sendPlayer();
      return;
    }

    org.bukkit.configuration.ConfigurationSection doorSection = plugin.getSetupUtilities().getConfig().getConfigurationSection("instances." + arena.getId() + ".doors");
    int doors = (doorSection != null ? doorSection.getKeys(false).size() : 0) + 1;

    Block relativeBlock = null;
    Block faceBlock;

    if((faceBlock = block.getRelative(BlockFace.DOWN)).getType() == door) {
      relativeBlock = block;
      block = faceBlock;
    } else if((faceBlock = block.getRelative(BlockFace.UP)).getType() == door) {
      relativeBlock = faceBlock;
    }

    if(relativeBlock == null) {
      new MessageBuilder("&c&l✘ &cThis door doesn't have 2 blocks? Maybe it's bugged? Try placing it again.").player(player).sendPlayer();
      return;
    }

    String relativeLocation = relativeBlock.getWorld().getName() + "," + relativeBlock.getX() + "," + relativeBlock.getY() + "," + relativeBlock.getZ() + ",0.0" + ",0.0";
    plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".doors." + doors + ".location", relativeLocation);
    plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".doors." + doors + ".byte", 8);
    doors++;

    String doorLocation = block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ() + ",0.0" + ",0.0";
    plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".doors." + doors + ".location", doorLocation);
    if(!ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_11_R1) && !ServerVersion.Version.isCurrentEqual(ServerVersion.Version.v1_12_R1)
        && block.getState().getData() instanceof Door) {
      plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".doors." + doors + ".byte", plugin.getBukkitHelper().getDoorByte(((Door) block.getState().getData()).getFacing()));
    } else {
      try {
        plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".doors." + doors + ".byte", block.getClass().getDeclaredMethod("getData").invoke(block));
      } catch(Exception e1) {
        e1.printStackTrace();
      }
    }
    new MessageBuilder("&a&l✔ &aDoor successfully added! To apply door changes you must restart your server!").player(player).sendPlayer();
    ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
  }

  private void removeDoors() {
    plugin.getSetupUtilities().getConfig().set("instances." + arena.getId() + ".doors", null);
    arena.getMapRestorerManager().getGameDoorLocations().clear();
    new MessageBuilder("&e✔ Removed | &aDoor locations deleted, you can add them again now!").player(player).sendPlayer();
    arena.setReady(false);
    ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
  }

  private void removeSection(SectionType sectionType) {
    plugin.getSetupUtilities().getConfig().set("instances." + getArena().getId() + "." + sectionType.getPath(), null);
    switch(sectionType) {
      case ZOMBIE_SPAWN:
        arena.getZombieSpawns().clear();
        break;
      case VILLAGER_SPAWN:
        arena.getVillagerSpawns().clear();
        break;
    }
    new MessageBuilder("&e✔ Removed | &a" + sectionType.getName() + " spawn points deleted, you can add them again now!").player(getPlayer()).sendPlayer();
    getArena().setReady(false);
    ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
  }

  private void addSection(Location location, SectionType sectionType) {
    ConfigurationSection configurationSection = plugin.getSetupUtilities().getConfig().getConfigurationSection("instances." + arena.getId() + "." + sectionType.getPath());
    int value = (configurationSection != null ? configurationSection.getKeys(false).size() : 0) + 1;

    LocationSerializer.saveLoc(plugin, plugin.getSetupUtilities().getConfig(), "arenas", "instances." + arena.getId() + "." + sectionType.getPath() + "." + value, location);
    String progress = value >= 2 ? "&e✔ Completed | " : "&c✘ Not completed | ";
    new MessageBuilder(progress + "&a" + sectionType.getName() + " spawn added! &8(&7" + value + "/2&8)").player(player).sendPlayer();
    if(value == 2) {
      new MessageBuilder("&eInfo | &aYou can add more than 2 " + sectionType.getName() + " spawns! Two is just a minimum!").player(player).sendPlayer();
    }
    switch(sectionType) {
      case ZOMBIE_SPAWN:
        arena.getZombieSpawns().add(player.getLocation());
        break;
      case VILLAGER_SPAWN:
        arena.getVillagerSpawns().add(player.getLocation());
        break;
    }
    ConfigUtils.saveConfig(plugin, plugin.getSetupUtilities().getConfig(), "arenas");
  }

  private enum SectionType {
    ZOMBIE_SPAWN("zombiespawns", "Zombie"), VILLAGER_SPAWN("villagerspawns", "Villager");

    private final String path;
    private final String name;

    SectionType(String path, String name) {
      this.path = path;
      this.name = name;
    }

    public String getPath() {
      return path;
    }

    public String getName() {
      return name;
    }
  }


  @Override
  public boolean addAdditionalArenaValidateValues(InventoryClickEvent event, PluginArena arena, PluginMain plugin, FileConfiguration config) {
    for(String s : new String[]{"zombiespawns", "villagerspawns"}) {
      org.bukkit.configuration.ConfigurationSection spawnSection = config.getConfigurationSection("instances." + arena.getId() + "." + s);

      if(spawnSection == null || spawnSection.getKeys(false).size() < 2) {
        new MessageBuilder("&c&l✘ &cArena validation failed! Please configure following spawns properly: " + s + " (must be minimum 2 spawns)").send(event.getWhoClicked());
        return false;
      }
    }

    if(config.getConfigurationSection("instances." + arena.getId() + ".doors") == null) {
      new MessageBuilder("&c&l✘ &cArena validation failed! Please configure doors properly!").send(event.getWhoClicked());
      return false;
    }

    return true;
  }

  @Override
  public void addAdditionalArenaSetValues(PluginArena arena, FileConfiguration config) {
    Arena pluginArena = plugin.getArenaRegistry().getArena(arena.getId());
    if(pluginArena == null) {
      return;
    }
    for(String string : config.getConfigurationSection("instances." + arena.getId() + ".zombiespawns").getKeys(false)) {
      pluginArena.addZombieSpawn(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".zombiespawns." + string)));
    }
    for(String string : config.getConfigurationSection("instances." + arena.getId() + ".villagerspawns").getKeys(false)) {
      pluginArena.addVillagerSpawn(LocationSerializer.getLocation(config.getString("instances." + arena.getId() + ".villagerspawns." + string)));
    }
    for(String string : config.getConfigurationSection("instances." + arena.getId() + ".doors").getKeys(false)) {
      String path = "instances." + arena.getId() + ".doors." + string + ".";
      pluginArena.getMapRestorerManager().addDoor(LocationSerializer.getLocation(config.getString(path + "location")),
          (byte) config.getInt(path + "byte"));
    }
  }
}*/
