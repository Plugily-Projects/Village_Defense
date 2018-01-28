package me.tomthedeveloper.kitapi.basekits;

import me.tomthedeveloper.handlers.ChatManager;
import me.tomthedeveloper.handlers.ConfigurationManager;
import me.tomthedeveloper.utils.Util;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

/**
 * Created by Tom on 14/08/2014.
 */
public abstract class LevelKit extends Kit {



    int level;

    public void setLevel(int level){
        this.level = level;
        FileConfiguration config = ConfigurationManager.getConfig("kits");
        String name = getClass().getName().substring(getClass().getName().indexOf("K"));
        if(!config.contains(name)) {
            config.set("Required-Level." + name, level);
            try {
                config.save(ConfigurationManager.getFile("kits"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else{
            this.level = config.getInt("Required-Level." + name);
        }
    }

    public int getLevel(){
        return level;
    }

    public ItemStack getItemStack(){
        ItemStack itemStack = new ItemStack(getMaterial());
        setItemNameAndLore(itemStack, getName(), getDescription());
        Util.addLore(itemStack, ChatManager.colorMessage("kits.Kit-Menu.Locked-Lores.Unlock-At-Level").replaceAll("%NUMBER%",Integer.toString(getLevel())));
        return itemStack;
    }
}
