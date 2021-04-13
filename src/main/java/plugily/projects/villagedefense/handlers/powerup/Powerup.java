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

package plugily.projects.villagedefense.handlers.powerup;

import pl.plajerlair.commonsbox.minecraft.compat.xseries.XMaterial;

import java.util.function.Consumer;

/**
 * @author Plajer
 * <p>
 * Created at 15.01.2019
 */
public class Powerup {

  private final String id;
  private final String name;
  private final String description;
  private final XMaterial material;
  private final Consumer<PowerupPickupHandler> onPickup;

  public Powerup(String id, String name, String description, XMaterial material, Consumer<PowerupPickupHandler> pickup) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.material = material;
    onPickup = pickup;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public XMaterial getMaterial() {
    return material;
  }

  public Consumer<PowerupPickupHandler> getOnPickup() {
    return onPickup;
  }
}
