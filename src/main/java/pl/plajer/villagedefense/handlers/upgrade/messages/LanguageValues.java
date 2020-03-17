package pl.plajer.villagedefense.handlers.upgrade.messages;

/**
 * @author Plajer
 * <p>
 * Created at 18.06.2019
 */
public enum LanguageValues {

  UPGRADE_MENU_TITLE("Upgrade-Menu.Title", "&e&lUpgrade entity"),
  UPGRADE_MENU_STATS_ITEM_NAME("Upgrade-Menu.Stats-Item.Name", "&e&lCurrent Stats"),
  UPGRADE_MENU_STATS_ITEM_LORE("Upgrade-Menu.Stats-Item.Description", "&eMovement speed: &7%speed%;&eAttack Damage: &7%damage%;&eHealth: &7%current_hp%/%max_hp%"),

  UPGRADES_HEALTH_NAME("Upgrade-Menu.Upgrades.Health.Name", "&a&lUpgrade Health"),
  UPGRADES_HEALTH_LORE("Upgrade-Menu.Upgrades.Health.Description", "&7Upgrade max health to tier &e%tier%&7!;&7From &e%from% &7to &e%to%;&7Cost of upgrade: &e%cost%;;&eClick to purchase"),
  UPGRADES_DAMAGE_NAME("Upgrade-Menu.Upgrades.Damage.Name", "&a&lUpgrade Damage"),
  UPGRADES_DAMAGE_LORE("Upgrade-Menu.Upgrades.Damage.Description", "&7Upgrade entity damage to tier &e%tier%&7!;&7From &e%from% &7to &e%to%;&7Cost of upgrade: &e%cost%;;&eClick to purchase"),
  UPGRADES_SPEED_NAME("Upgrade-Menu.Upgrades.Speed.Name", "&a&lUpgrade Speed"),
  UPGRADES_SPEED_LORE("Upgrade-Menu.Upgrades.Speed.Description", "&7Upgrade movement speed to tier &e%tier%&7!;&7From &e%from% &7to &e%to%;&7Cost of upgrade: &e%cost%;;&eClick to purchase"),
  UPGRADES_SWARM_AWARENESS_NAME("Upgrade-Menu.Upgrades.Swarm-Awareness.Name", "&a&lSwarm Awareness"),
  UPGRADES_SWARM_AWARENESS_LORE("Upgrade-Menu.Upgrades.Swarm-Awareness.Description", "&7Upgrade swarm awareness to tier &e%tier%&7!;&7From &e%from% &edamage multiplier per wolf in radius;&eof 3 " +
      "blocks &7to %to%;&7The more wolves near attacking wolf;&7the more damage wolf deal;&7Cost of upgrade: &e%cost%;;&eClick to purchase"),
  UPGRADES_FINAL_DEFENSE_NAME("Upgrade-Menu.Upgrades.Final-Defense.Name", "&a&lFinal Defense"),
  UPGRADES_FINAL_DEFENSE_LORE("Upgrade-Menu.Upgrades.Final-Defense.Description", "&7Upgrade final defense to tier &e%tier%&7!;&7From &e%from% explosion radius &7to &e%to%;&7Golem will explode after" +
      " death killing nearby;&7zombies and stun all alive ones;&7Cost of upgrade: &e%cost%;;&eClick to purchase"),

  UPGRADED_ENTITY("Upgrade-Menu.Upgraded-Entity", "&7Upgraded entity to tier &e%tier%&7!"),
  CANNOT_AFFORD_UPGRADE("Upgrade-Menu.Cannot-Afford", "&cYou don't have enough orbs to apply that upgrade!"),
  MAX_TIER_UPGRADE("Upgrade-Menu.Max-Tier", "&cEntity is at max tier of this upgrade!");

  private String accessor;
  private String defaultMessage;

  LanguageValues(String accessor, String defaultMessage) {
    this.accessor = accessor;
    this.defaultMessage = defaultMessage;
  }

  public String getAccessor() {
    return accessor;
  }

  public String getDefaultMessage() {
    return defaultMessage;
  }
}
