package dev.hamsjunk.buyhomes.events.selectors;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChangeBlockSelectorEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    Player p;

    public ChangeBlockSelectorEvent(Player p) {
        this.p = p;
    }

    public Player getPlayer() {
        return p;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
