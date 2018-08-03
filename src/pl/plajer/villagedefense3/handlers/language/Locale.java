/*
 * Village Defense 3 - Protect villagers from hordes of zombies
 * Copyright (C) 2018  Plajer's Lair - maintained by Plajer
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

package pl.plajer.villagedefense3.handlers.language;

/**
 * @author Plajer
 * <p>
 * Created at 29.06.2018
 */
public enum Locale {

  CHINESE_SIMPLIFIED("简体中文", "zh_Hans", "POEditor contributors"),
  ENGLISH("English", "en_GB", "Plajer"),
  FRENCH("Français", "fr_FR", "POEditor contributors"),
  GERMAN("Deutsch", "de_DE", "Tigerkatze"),
  HUNGARIAN("Magyar", "hu_HU", "POEditor contributors"),
  INDONESIA("Indonesia", "id_ID", "POEditor contributors (Yume)"),
  SPANISH("Español", "es_ES", "POEditor contributors"),
  POLISH("Polski", "pl_PL", "Plajer"),
  VIETNAMESE("Việt", "vn_VN", "POEditor contributors (HStreamGamer)");

  String formattedName;
  String prefix;
  String author;

  Locale(String formattedName, String prefix, String author) {
    this.prefix = prefix;
    this.formattedName = formattedName;
    this.author = author;
  }

  public String getFormattedName() {
    return formattedName;
  }

  public String getAuthor() {
    return author;
  }

  public String getPrefix() {
    return prefix;
  }

}
