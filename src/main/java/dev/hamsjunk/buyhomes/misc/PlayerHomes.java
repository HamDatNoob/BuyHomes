package dev.hamsjunk.buyhomes.misc;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class PlayerHomes {
    private static File f;
    private static FileConfiguration file;

    public static void setup() {
        f = new File(Bukkit.getServer().getPluginManager().getPlugin("BuyHomes").getDataFolder(), "homes.yml");

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        file = YamlConfiguration.loadConfiguration(f);
    }

    public static FileConfiguration get() {
        return file;
    }

    public static void save() {
        try {
            file.save(f);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void reload() {
        file = YamlConfiguration.loadConfiguration(f);
    }
}
