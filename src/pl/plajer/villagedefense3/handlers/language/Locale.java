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

import java.util.Arrays;
import java.util.List;

/**
 * @author Plajer
 * <p>
 * Created at 29.06.2018
 */
public enum Locale {

  CHINESE_SIMPLIFIED("简体中文", "zh_Hans", "POEditor contributors (Haoting)", Arrays.asList("简体中文", "中文", "chinese", "zh")),
  CZECH("Český", "cs_CZ", "POEditor contributors", Arrays.asList("czech", "cesky", "český", "cs")),
  ENGLISH("English", "en_GB", "Plajer", Arrays.asList("default", "english", "en")),
  FRENCH("Français", "fr_FR", "POEditor contributors", Arrays.asList("french", "francais", "français", "fr")),
  GERMAN("Deutsch", "de_DE", "Tigerkatze and POEditor contributors", Arrays.asList("deutsch", "german", "de")),
  HUNGARIAN("Magyar", "hu_HU", "POEditor contributors (montlikadani)", Arrays.asList("hungarian", "magyar", "hu")),
  INDONESIAN("Indonesia", "id_ID", "POEditor contributors", Arrays.asList("indonesian", "indonesia", "id")),
  POLISH("Polski", "pl_PL", "Plajer", Arrays.asList("polish", "polski", "pl")),
  ROMANIAN("Românesc", "ro_RO", "POEditor contributors (Andrei)", Arrays.asList("romanian", "romanesc", "românesc", "ro")),
  SPANISH("Español", "es_ES", "POEditor contributors", Arrays.asList("spanish", "espanol", "español", "es")),
  VIETNAMESE("Việt", "vn_VN", "POEditor contributors (HStreamGamer)", Arrays.asList("vietnamese", "viet", "việt", "vn"));

  String formattedName;
  String prefix;
  String author;
  List<String> aliases;

  Locale(String formattedName, String prefix, String author, List<String> aliases) {
    this.prefix = prefix;
    this.formattedName = formattedName;
    this.author = author;
    this.aliases = aliases;
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

  public List<String> getAliases() {
    return aliases;
  }

}
