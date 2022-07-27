package dev.hamsjunk.buyhomes.commands;

import dev.hamsjunk.buyhomes.Main;
import dev.hamsjunk.buyhomes.events.HomePageEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Homes implements CommandExecutor {
    private final Main main;

    public Homes(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Bukkit.getServer().getPluginManager().callEvent(new HomePageEvent((Player) sender));

            return true;
        } else {
            Bukkit.getLogger().info("Error: Executor of type " + sender + " is not a player!");

            return false;
        }
    }
}

