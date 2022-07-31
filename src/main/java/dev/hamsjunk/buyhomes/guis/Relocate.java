package dev.hamsjunk.buyhomes.guis;

import dev.hamsjunk.buyhomes.Main;
import dev.hamsjunk.buyhomes.events.DeleteEvent;
import dev.hamsjunk.buyhomes.events.HomePageEvent;
import dev.hamsjunk.buyhomes.events.SetHomeEvent;
import dev.hamsjunk.buyhomes.misc.PlayerHomes;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Delete implements Listener {
    private Inventory inv;

    public void openNewGui(Player p) throws URISyntaxException {
        inv = Bukkit.createInventory(null, 27, "Delete this home?");

        initializeItems(p);

        p.openInventory(inv);
    }

    public void initializeItems(Player p) throws URISyntaxException {
        // static items
        inv.setItem(11, createGuiItem(Material.RED_CONCRETE, ChatColor.RED + "Cancel"));
        inv.setItem(15, createGuiItem(Material.LIME_CONCRETE, ChatColor.GREEN + "Confirm"));
    }

    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        assert meta != null;
        meta.setDisplayName(name); // set a display name
        meta.setLore(Arrays.asList(lore)); // set the item lore

        item.setItemMeta(meta); // set the metadata

        return item;
    }

    @EventHandler
    public void openGuiEvent(DeleteEvent p) throws URISyntaxException {
        openNewGui(p.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;

        e.setCancelled(true); // prevents moving of items in the inventories
        final Player p = (Player) e.getWhoClicked(); // get the player who clicked

        List<String> homes = PlayerHomes.get().getStringList("Homes." + p.getUniqueId() + "._homes");
        List<String> indexes = PlayerHomes.get().getStringList("Homes." + p.getUniqueId() + "._takenIndexes");

        String home = null;
        for (String h : homes) {
            if (PlayerHomes.get().getBoolean("Homes." + p.getUniqueId() + "." + h + ".flags.deletion")) {
                home = h;

                break;
            }
        }

        if (e.getSlot() == 11) {
            PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + home + ".flags.deletion", false);
            p.sendMessage(ChatColor.RED + "Canceled deletion of a home!");

            Bukkit.getServer().getPluginManager().callEvent(new HomePageEvent(p));
        } else if (e.getSlot() == 15) {
            int index = PlayerHomes.get().getInt("Homes." + p.getUniqueId() + "." + home + ".index");
            String name = PlayerHomes.get().getString("Homes." + p.getUniqueId() + "." + home + ".name");

            homes.remove(index);
            indexes.remove(index);
            PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + home, null);
            PlayerHomes.get().set("Homes." + p.getUniqueId() + "._homes", homes);
            PlayerHomes.get().set("Homes." + p.getUniqueId() + "._takenIndexes", indexes);

            PlayerHomes.save();

            p.closeInventory();
            p.sendMessage(ChatColor.GREEN + "Successfully deleted the home " + ChatColor.WHITE + name + ChatColor.GREEN + "!");
        }
    }

    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent e) {
        if (!e.getInventory().equals(inv)) return;

        final Player p = (Player) e.getPlayer(); // get the player who clicked

        List<String> homes = PlayerHomes.get().getStringList("Homes." + p.getUniqueId() + "._homes");

        String home = null;
        for (String h : homes) {
            if (PlayerHomes.get().getBoolean("Homes." + p.getUniqueId() + "." + h + ".flags.deletion")) {
                home = h;

                break;
            }
        }

        if (home == null) return;

        p.sendMessage(ChatColor.RED + "Canceled deletion of a home!");
        PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + home + ".flags.deletion", false);
    }
}
