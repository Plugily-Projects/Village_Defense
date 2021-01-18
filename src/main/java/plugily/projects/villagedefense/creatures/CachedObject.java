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

package plugily.projects.villagedefense.creatures;

/**
 * @author Plajer
 * <p>
 * Created at 06.01.2019
 */
public class CachedObject {

    private final String fieldName;
    private final Class<?> clazz;
    private final Object object;

    public CachedObject(String fieldName, Class<?> clazz, Object object) {
        this.fieldName = fieldName;
        this.clazz = clazz;
        this.object = object;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getObject() {
        return object;
    }
}
