package com.github.Gamecube762.IsMinecraftDown.Events;

import com.github.Gamecube762.IsMinecraftDown.Services;
import com.github.Gamecube762.IsMinecraftDown.Status;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Date;

/**
 * Created by Gamecube762 on 8/28/14.
 */
public class StatusUpdatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Date date;

    public StatusUpdatedEvent(Date date) {
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public String getStatus(Services services) {
        return Status.getStatus(services);
    }

    public Status.UpdateStatus getUpdateStatus() {
        return Status.getUpdateStatus();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
