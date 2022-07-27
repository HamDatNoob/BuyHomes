package dev.hamsjunk.buyhomes;

import dev.hamsjunk.buyhomes.commands.Homes;
import dev.hamsjunk.buyhomes.guis.HomePage;
import dev.hamsjunk.buyhomes.guis.SetHome;
import dev.hamsjunk.buyhomes.misc.PlayerHomes;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public final class Main extends JavaPlugin {

    private static Main plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        getConfig().options().copyDefaults();
        saveDefaultConfig();

        PlayerHomes.setup();
        PlayerHomes.get().addDefault("Homes", new ArrayList<String>());
        PlayerHomes.get().options().copyDefaults(true);
        PlayerHomes.save();

        plugin = this;

        getServer().getPluginManager().registerEvents(new HomePage(), this);
        getServer().getPluginManager().registerEvents(new SetHome(), this);
        getCommand("homes").setExecutor(new Homes(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Main getPlugin() {
        return plugin;
    }
}
