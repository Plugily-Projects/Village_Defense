/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2021  Plugily Projects - maintained by 2Wild4You, Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.kits.basekits;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import plugily.projects.commonsbox.minecraft.configuration.ConfigUtils;
import plugily.projects.commonsbox.minecraft.item.ItemBuilder;
import plugily.projects.villagedefense.Main;
import plugily.projects.villagedefense.utils.constants.Constants;

/**
 * Created by Tom on 25/07/2014.
 */
public abstract class Kit {

  private static final Main plugin = JavaPlugin.getPlugin(Main.class);

  private final FileConfiguration kitsConfig = ConfigUtils.getConfig(plugin, Constants.Files.KITS.getName());

  private String name = "";
  private boolean unlockedOnDefault = false;
  private String[] description = new String[0];

  protected Kit() {
  }

  public Kit(String name) {
    setName(name);
  }

  public abstract boolean isUnlockedByPlayer(Player p);

  public boolean isUnlockedOnDefault() {
    return unlockedOnDefault;
  }

  public void setUnlockedOnDefault(boolean unlockedOnDefault) {
    this.unlockedOnDefault = unlockedOnDefault;
  }

  /**
   * @return main plugin
   */
  public Main getPlugin() {
    return plugin;
  }

  /**
   * @return config file of kits
   */
  public FileConfiguration getKitsConfig() {
    return kitsConfig;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    if (name != null)
      this.name = name;
  }

  public String[] getDescription() {
    return description.clone();
  }

  public void setDescription(String[] description) {
    if (description != null)
      this.description = description.clone();
  }

  public ItemStack getItemStack() {
    return new ItemBuilder(getMaterial())
        .name(name)
        .lore(getDescription())
        .build();
  }

  public abstract void giveKitItems(Player player);

  public abstract Material getMaterial();

  public abstract void reStock(Player player);

}
