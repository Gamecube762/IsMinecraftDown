package com.github.Gamecube762.IsMinecraftDown.Loaders;

import com.github.Gamecube762.IsMinecraftDown.Events.Bukkit.AllStatusUpdatesCompletedEvent;
import com.github.Gamecube762.IsMinecraftDown.Events.Bukkit.StatusUpdatedEvent;
import com.github.Gamecube762.IsMinecraftDown.IsMinecraftDown;
import com.github.Gamecube762.IsMinecraftDown.Metrics;
import com.github.Gamecube762.IsMinecraftDown.Service;
import com.github.Gamecube762.IsMinecraftDown.Updater;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Gamecube762 on 1/30/14.
 */
public class BukkitLoader extends JavaPlugin implements Listener{

    private BukkitTask statusChecker;
    private Updater updater;
    private long updateDelay = -1;
    private int statusCheckTaskID = -1;

    @Override
    public void onEnable() {
        if (LoaderManager.getLoadedAPI() != null) {
            getLogger().info("Plugin is already loaded/loading for the " + LoaderManager.getLoadedAPI().name());
            this.setEnabled(false);
            return;
        }
        else new LoaderManager(LoaderManager.ServerAPI.BUKKIT, this);

        Bukkit.getPluginManager().registerEvents(this, this);

        saveDefaultConfig();
        updateDelay = getConfig().getLong("Settings.checkDelay") * 60 * 20;

        if (getConfig().getBoolean("Settings.AutoUpdate")) {
            updater = new Updater(this, 75086, this.getFile(), Updater.UpdateType.DEFAULT, false);
            getLogger().info("Update Status: " + updater.getResult());
        }

        getLogger().info("Checking status");
        waitTillAllUpdatesFinished(Bukkit.getConsoleSender());
        IsMinecraftDown.checkAllStatus();

        getLogger().info("Starting checks every " + (updateDelay/(60*20)) + " minutes!");
        startStatusChecker();

        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            getLogger().info("Metrics Failed! D=");
            getLogger().info("(This won't impact how the plugin works.)");
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(IsMinecraftDown.getFormatedStatus());
            return true;
        }

        if (args[0].equalsIgnoreCase("ForceCheck")) {
            if (!sender.hasPermission("IsMcDown.command.ForceUpdateCheck")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this!");
                return true;
            }
            IsMinecraftDown.checkAllStatus();
            waitTillAllUpdatesFinished(sender);
        }

        if (args[0].equalsIgnoreCase("reloadConfig"))
            if (sender.hasPermission("IsMcDown.command.ReloadConfig"))
                reloadConfig();
            else
                sender.sendMessage(ChatColor.RED + "You don't have permission to use this!");

        if(args[0].equalsIgnoreCase("about"))
            sender.sendMessage(
                    "IsMinecraftDown by Gamecube762 \n" +
                    "Version: " + getDescription().getVersion() +
                    (getConfig().getBoolean("Settings.AutoUpdate") ? "\nLatest File: " + updater.getLatestName() : "")
            );

        return true;
    }

    @Override
    public void reloadConfig() {
        long oldDelay = updateDelay;
        super.reloadConfig();
        updateDelay = getConfig().getLong("Settings.checkDelay") * 60 * 20;

        if (oldDelay == updateDelay) return;
        startStatusChecker();
    }

    private void startStatusChecker() {
        if (statusCheckTaskID != -1) Bukkit.getScheduler().cancelTask(statusCheckTaskID);
        statusCheckTaskID =
                Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
                    @Override
                    public void run() {
                        IsMinecraftDown.checkStatus(Service.ALL);
                    }
                }, updateDelay, updateDelay)
                .getTaskId();
    }

    @EventHandler
    public void onStatusUpdate(StatusUpdatedEvent event) {
        Iterator<CommandSender> i = holdTillUpdate.keySet().iterator();

        while (i.hasNext()) {
            CommandSender sender = i.next();
            if (holdTillUpdate.get(sender) == event.getService()) {
                sender.sendMessage(event.getService().name() + " : " + event.getStatus().getStatusLevel().name());
                i.remove();
            }
        }
    }

    @EventHandler
    public void onAllStatusUpdateCompleted(AllStatusUpdatesCompletedEvent event) {
        Iterator<CommandSender> i = holdTillAllUpdatesFinished.iterator();

        while (i.hasNext()) {
            i.next().sendMessage(IsMinecraftDown.getFormatedStatus());
            i.remove();
        }
    }

    //==================================== Statics ====================================
    private static HashMap<CommandSender, Service> holdTillUpdate = new HashMap<CommandSender, Service>();
    private static List<CommandSender> holdTillAllUpdatesFinished = new ArrayList<CommandSender>();

    public static void waitTillUpdate(CommandSender commandSender, Service service) {
        holdTillUpdate.put(commandSender, service);
    }

    public static void waitTillAllUpdatesFinished(CommandSender commandSender) {
        holdTillAllUpdatesFinished.add(commandSender);
    }

    public static void callEvent(Event event) {
        Bukkit.getPluginManager().callEvent(event);
    }

}
