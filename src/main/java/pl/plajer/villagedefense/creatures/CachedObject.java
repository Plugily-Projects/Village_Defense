/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (C) 2019  Plajer's Lair - maintained by Plajer and Tigerpanzer
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

package pl.plajer.villagedefense.creatures;

/**
 * @author Plajer
 * <p>
 * Created at 06.01.2019
 */
public class CachedObject {

  private String fieldName;
  private Class clazz;
  private Object object;

  public CachedObject(String fieldName, Class clazz, Object object) {
    this.fieldName = fieldName;
    this.clazz = clazz;
    this.object = object;
  }

  public String getFieldName() {
    return fieldName;
  }

  public Class getClazz() {
    return clazz;
  }

  public Object getObject() {
    return object;
  }
}
