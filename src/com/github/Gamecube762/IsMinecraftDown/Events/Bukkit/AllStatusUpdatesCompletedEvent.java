package com.github.Gamecube762.IsMinecraftDown.Events.Bukkit;

import com.github.Gamecube762.IsMinecraftDown.IsMinecraftDown;
import com.github.Gamecube762.IsMinecraftDown.Service;
import com.github.Gamecube762.IsMinecraftDown.Status;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Date;


/**
 * Created by Gamecube762 on 8/28/14.
 */
public class AllStatusUpdatesCompletedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Date date;

    public AllStatusUpdatesCompletedEvent() {
        date = new Date();
    }

    public Status getStatus(Service service) {
        return IsMinecraftDown.getStatus(service);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
