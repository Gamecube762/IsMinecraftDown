package com.github.Gamecube762.IsMinecraftDown.Events.Bukkit;

import com.github.Gamecube762.IsMinecraftDown.Service;
import com.github.Gamecube762.IsMinecraftDown.Status;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.Date;


/**
 * Created by Gamecube762 on 8/28/14.
 */
public class StatusUpdatedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private Status status;

    public StatusUpdatedEvent(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public Service getService() {
        return status.getService();
    }

    public Date getTimeStarted() {
        return status.getTimeChecked();
    }

    public Date getTimeFinished() {
        return status.getTimeFinished();
    }

    public Long getTimeUsed() {
        return status.getTimeUsed();
    }

    public String getStatusInfo() {
        return status.getStatusInfo();
    }

    public Status.StatusLevel getStatusLevel() {
        return status.getStatusLevel();
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
