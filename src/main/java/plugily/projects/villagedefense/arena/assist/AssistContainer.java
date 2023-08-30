/*
 * Village Defense - Protect villagers from hordes of zombies
 * Copyright (c) 2023  Plugily Projects - maintained by Tigerpanzer_02 and contributors
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

package plugily.projects.villagedefense.arena.assist;

import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Metadata container that includes every player
 * that assisted in killing an enemy and their
 * kill participation (damage/buffing the killer/other)
 * <p>
 * While assists for enemies are always counted,
 * assists for players are only counted if time_millis parameter
 * is no more than 10 seconds from actual time.
 * <p>
 * Data is stored in the following way:
 * uuid,assist_type,value,time_millis|uuid,assist_type,value,time_millis
 *
 * @author Plajer
 * <p>
 * Created at 29.08.2023
 */
public class AssistContainer {

  private final List<AssistData> data;

  public AssistContainer() {
    this.data = new ArrayList<>();
  }

  public AssistContainer(List<AssistData> data) {
    this.data = data;
  }

  public static AssistContainer deserialize(String text) {
    if(text.isBlank()) {
      return new AssistContainer(new ArrayList<>());
    }
    List<AssistData> assistData = new ArrayList<>();
    String[] serialized = text.split("\\|");
    for(String assistSerialized : serialized) {
      String[] values = assistSerialized.split(",");
      assistData.add(new AssistData(UUID.fromString(values[0]), AssistData.AssistType.valueOf(values[1].toUpperCase()),
        Double.parseDouble(values[2]), Long.parseLong(values[3])));
    }
    return new AssistContainer(assistData);
  }

  public List<AssistData> getData() {
    return data;
  }

  public void updateAssist(LivingEntity entity, AssistData.AssistType type) {
    updateAssist(entity, type, 0);
  }

  public void updateAssist(LivingEntity entity, AssistData.AssistType type, double value) {
    Optional<AssistData> optional = data.stream().filter(a -> a.getUuid().equals(entity.getUniqueId())).findFirst();
    if(optional.isEmpty()) {
      data.add(new AssistData(entity.getUniqueId(), type, value, System.currentTimeMillis()));
      return;
    }
    AssistData assistData = optional.get();
    //damage has the highest assist reward, override if any damage is given
    if(assistData.getType() != type && type == AssistData.AssistType.DAMAGE) {
      assistData.setType(type);
      assistData.setValue(value);
      assistData.setTimeMillis(System.currentTimeMillis());
      return;
    }
    //on damage, increase total value of assist
    if(type == AssistData.AssistType.DAMAGE) {
      assistData.setValue(assistData.getValue() + value);
    }
    //update time for any type of assist be it buff or damage
    assistData.setTimeMillis(System.currentTimeMillis());
  }

  public String serialize() {
    StringJoiner joiner = new StringJoiner("|");
    for(AssistData assistData : data) {
      joiner.add(assistData.toString());
    }
    return joiner.toString();
  }

  public static class AssistData {

    private final UUID uuid;
    private AssistType type;
    private double value;
    private long timeMillis;

    public AssistData(UUID uuid, AssistType type, double value, long timeMillis) {
      this.uuid = uuid;
      this.type = type;
      this.value = value;
      this.timeMillis = timeMillis;
    }

    public UUID getUuid() {
      return uuid;
    }

    public AssistType getType() {
      return type;
    }

    public void setType(AssistType type) {
      this.type = type;
    }

    public double getValue() {
      return value;
    }

    public void setValue(double value) {
      this.value = value;
    }

    public long getTimeMillis() {
      return timeMillis;
    }

    public void setTimeMillis(long timeMillis) {
      this.timeMillis = timeMillis;
    }

    @Override
    public String toString() {
      return uuid.toString() + "," + type.name() + "," + value + "," + timeMillis;
    }

    public enum AssistType {
      /**
       * Assisting ally did damage to killed enemy
       */
      DAMAGE,
      /**
       * Assisting ally did buff ally that killed the enemy
       */
      BUFF,
      /**
       * Assisting ally did debuff enemy that was killed by ally
       */
      DEBUFF
    }

  }

}
