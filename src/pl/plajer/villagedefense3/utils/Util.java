package pl.plajer.villagedefense3.utils;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.BlockIterator;
import pl.plajer.villagedefense3.Main;
import pl.plajer.villagedefense3.handlers.ConfigurationManager;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Tom on 29/07/2014.
 */
public class Util {

    private static Main plugin;

    public Util(Main plugin){
        Util.plugin = plugin;
    }


    public static void addLore(ItemStack itemStack, String string) {
        ItemMeta meta = itemStack.getItemMeta();
        List<String> lore = new ArrayList<>();
        if(meta != null && meta.hasLore()) lore.addAll(meta.getLore());
        lore.add(string);
        meta.setLore(lore);
        itemStack.setItemMeta(meta);
    }

    public static Queue<Block> getLineOfSight(LivingEntity entity, HashSet<Byte> transparent, int maxDistance, int maxLength) {
        if(maxDistance > 120) {
            maxDistance = 120;
        }

        Queue<Block> blocks = new LinkedList<>();
        Iterator<Block> itr = new BlockIterator(entity, maxDistance);
        while(itr.hasNext()) {
            Block block = itr.next();
            blocks.add(block);

            if(maxLength != 0 && blocks.size() > maxLength) {
                blocks.remove(0);
            }
            int id = block.getTypeId();
            if(transparent == null) {
                if(id != 0 && id != 50 && id != 59 && id != 31 && id != 175 && id != 38 && id != 37 && id != 6 && id != 106)
                    break;
            } else {
                if(!transparent.contains((byte) id))
                    break;
            }

        }
        return blocks;
    }

    public static Entity[] getNearbyEntities(Location l, int radius) {
        int chunkRadius = radius < 16 ? 1 : radius / 16;
        HashSet<Entity> radiusEntities = new HashSet<>();
        for(int chX = 0 - chunkRadius; chX <= chunkRadius; chX++) {
            for(int chZ = 0 - chunkRadius; chZ <= chunkRadius; chZ++) {
                int x = (int) l.getX(), y = (int) l.getY(), z = (int) l.getZ();
                for(Entity e : new Location(l.getWorld(), x + chX * 16, y, z + chZ * 16).getChunk().getEntities()) {
                    if(!(l.getWorld().getName().equalsIgnoreCase(e.getWorld().getName())))
                        continue;
                    if(e.getLocation().distanceSquared(l) <= radius * radius && e.getLocation().getBlock() != l
                            .getBlock()) {
                        radiusEntities.add(e);
                    }
                }
            }
        }
        return radiusEntities.toArray(new Entity[radiusEntities.size()]);
    }

    public static String formatIntoMMSS(int secsIn) {
        int minutes = secsIn / 60,
                seconds = secsIn % 60;
        return ((minutes < 10 ? "0" : "") + minutes
                + ":" + (seconds < 10 ? "0" : "") + seconds);

    }

    public static int serializeInt(Integer i) {
        if((i % 9) == 0)
            return i;
        else
            return (int) ((Math.ceil(i / 9) * 9) + 9);
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() + " , " + location.getX() + " , " + location.getY() + ", " + location.getZ();
    }

    public static void spawnRandomFirework(Location location) {
        Firework fw = (Firework) location.getWorld().spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();

        //Our random generator
        Random r = new Random();

        //Get the type
        int rt = r.nextInt(4) + 1;
        FireworkEffect.Type type = FireworkEffect.Type.BALL;
        if(rt == 1) type = FireworkEffect.Type.BALL;
        if(rt == 2) type = FireworkEffect.Type.BALL_LARGE;
        if(rt == 3) type = FireworkEffect.Type.BURST;
        if(rt == 4) type = FireworkEffect.Type.CREEPER;
        if(rt == 5) type = FireworkEffect.Type.STAR;

        //Get our random colours
        int r1i = r.nextInt(250) + 1;
        int r2i = r.nextInt(250) + 1;
        Color c1 = Color.fromBGR(r1i);
        Color c2 = Color.fromBGR(r2i);

        //Create our effect with this
        FireworkEffect effect = FireworkEffect.builder().flicker(r.nextBoolean()).withColor(c1).withFade(c2).with(type).trail(r.nextBoolean()).build();

        //Then apply the effect to the meta
        fwm.addEffect(effect);

        //Generate some random power and set it
        int rp = r.nextInt(2) + 1;
        fwm.setPower(rp);
        fw.setFireworkMeta(fwm);
    }


    public static List<String> splitString(String string, int max) {
        List<String> matchList = new ArrayList<>();
        Pattern regex = Pattern.compile(".{1," + max + "}(?:\\s|$)", Pattern.DOTALL);
        Matcher regexMatcher = regex.matcher(string);
        while(regexMatcher.find()) {
            matchList.add(ChatColor.translateAlternateColorCodes('&', "&7") + regexMatcher.group());
        }
        return matchList;
    }

    public static void saveLoc(String path, Location loc, boolean inConfig) {
        String location = loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ() + "," + loc.getYaw() + "," + loc.getPitch();
        if(inConfig) {
            plugin.getConfig().set(path, location);
            plugin.saveConfig();
        } else{
            FileConfiguration config = ConfigurationManager.getConfig("arenas");
            config.set(path, location);
            ConfigurationManager.saveConfig(config, "arenas");
        }
    }

    public static Location getLocation(boolean configUsage, String path) {
        String[] loc;
        if(configUsage) {
            loc = plugin.getConfig().getString(path).split(",");
        } else {
            loc = path.split(",");
        }
        plugin.getServer().createWorld(new WorldCreator(loc[0]));
        World w = plugin.getServer().getWorld(loc[0]);
        Double x = Double.parseDouble(loc[1]);
        Double y = Double.parseDouble(loc[2]);
        Double z = Double.parseDouble(loc[3]);
        float yaw = Float.parseFloat(loc[4]);
        float pitch = Float.parseFloat(loc[5]);
        return new Location(w, x, y, z, yaw, pitch);
    }

}
