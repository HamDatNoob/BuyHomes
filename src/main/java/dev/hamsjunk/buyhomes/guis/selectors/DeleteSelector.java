package dev.hamsjunk.buyhomes.guis;

import dev.hamsjunk.buyhomes.Main;
import dev.hamsjunk.buyhomes.events.HomePageEvent;
import dev.hamsjunk.buyhomes.events.RenameEvent;
import dev.hamsjunk.buyhomes.misc.PlayerHomes;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Rename implements Listener {
    private Inventory inv;

    public void openNewGui(Player p) throws URISyntaxException {
        inv = Bukkit.createInventory(null, 54, "BuyHomes 1.0-DEV");

        initializeItems(p);

        p.openInventory(inv);
    }

    public void initializeItems(Player p) throws URISyntaxException {
        // static items
        inv.setItem(46, createGuiItem(Material.NAME_TAG, ChatColor.YELLOW + "Rename home", "§7Rename a home from your list of homes!"));
        inv.setItem(51, createGuiItem(Material.ARROW, ChatColor.GRAY + "Previous page"));
        inv.setItem(52, createGuiItem(Material.BARRIER, ChatColor.RED + "Cancel"));
        inv.setItem(53, createGuiItem(Material.ARROW, ChatColor.GRAY + "Next page"));
        inv.setItem(36, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(37, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(38, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(39, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(40, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(41, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(42, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(43, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));
        inv.setItem(44, createGuiItem(Material.BLACK_STAINED_GLASS_PANE, "§r"));

        // find homes from config.yml
        FileConfiguration homesData = PlayerHomes.get();
        List<String> homes = homesData.getStringList("Homes." + p.getUniqueId() + "._homes");

        // set the homes for the player
        for (String home : homes) {
            if (String.valueOf(home.charAt(0)).equals("_")) {
                continue;
            }

            int index = homesData.getInt("Homes." + p.getUniqueId() + "." + home + ".index");
            String item = homesData.getString("Homes." + p.getUniqueId() + "." + home + ".item");
            String name = homesData.getString("Homes." + p.getUniqueId() + "." + home + ".name");

            inv.setItem(index, createGuiItem(Material.valueOf(item), ChatColor.WHITE + name));
        }
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
    public void openGuiEvent(RenameEvent p) throws URISyntaxException {
        PlayerHomes.get().set("Homes." + p.getPlayer().getUniqueId() + ".__name", p.getPlayer().getName());
        PlayerHomes.save();

        openNewGui(p.getPlayer());
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;

        e.setCancelled(true); // prevents moving of items in the inventories
        final Player p = (Player) e.getWhoClicked(); // get the player who clicked

        List<String> homes = PlayerHomes.get().getStringList("Homes." + p.getUniqueId() + "._homes");
        String oldName = PlayerHomes.get().getString("Homes." + p.getUniqueId() + "." + homes.get(e.getSlot()) + ".name");

        String home = homes.get(e.getSlot());

        int index = PlayerHomes.get().getInt("Homes." + p.getUniqueId() + "." + home + ".index");
        String item = PlayerHomes.get().getString("Homes." + p.getUniqueId() + "." + home + ".item");
        String world = PlayerHomes.get().getString("Homes." + p.getUniqueId() + "." + home + ".coords.world");
        double x = PlayerHomes.get().getDouble("Homes." + p.getUniqueId() + "." + home + ".coords.x");
        double y = PlayerHomes.get().getDouble("Homes." + p.getUniqueId() + "." + home + ".coords.y");
        double z = PlayerHomes.get().getDouble("Homes." + p.getUniqueId() + "." + home + ".coords.z");
        float yaw = (float) PlayerHomes.get().getDouble("Homes." + p.getUniqueId() + "." + home + ".coords.yaw");
        float pitch = (float) PlayerHomes.get().getDouble("Homes." + p.getUniqueId() + "." + home + ".coords.pitch");

        AtomicBoolean prematureClose = new AtomicBoolean(true);

        switch (e.getSlot()) {
            case 52:
                Bukkit.getServer().getPluginManager().callEvent(new HomePageEvent(p));
                p.sendMessage(ChatColor.RED + "Canceled renaming of the home!");

                return;

            case 36:
            case 37:
            case 38:
            case 39:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 46: return;

            default:
                new AnvilGUI.Builder()
                    .onClose(player -> {
                        if (prematureClose.get()) p.sendMessage(ChatColor.RED + "Canceled renaming of the home!");
                    }).onComplete((player, text) -> {
                        if (String.valueOf(text.charAt(0)).equals("_")) {
                            p.sendMessage(ChatColor.RED + "Home names cannot start with \"_\"!");
                            return AnvilGUI.Response.close();
                        }

                        // reassign the values
                        homes.set(e.getSlot(), text);
                        PlayerHomes.get().set("Homes." + p.getUniqueId() + "._homes", homes);
                        PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + home, null);

                        PlayerHomes.save();

                        String newHome = homes.get(e.getSlot());

                        PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + newHome + ".name", text);
                        PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + newHome + ".index", index);
                        PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + newHome + ".item", item);
                        PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + newHome + ".coords.world", world);
                        PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + newHome + ".coords.x", x);
                        PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + newHome + ".coords.y", y);
                        PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + newHome + ".coords.z", z);
                        PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + newHome + ".coords.yaw", yaw);
                        PlayerHomes.get().set("Homes." + p.getUniqueId() + "." + newHome + ".coords.pitch", pitch);

                        PlayerHomes.save();

                        p.sendMessage(ChatColor.GREEN + "Successfully renamed the home " + ChatColor.WHITE + oldName + ChatColor.GREEN + " to " + ChatColor.WHITE + text);

                        prematureClose.set(false);

                        return AnvilGUI.Response.close();
                    }).text(oldName).title("Input a name").plugin(Main.getPlugin()).open(p);
        }
    }
}
