package com.github.Gamecube762.IsMinecraftDown;

import com.github.Gamecube762.IsMinecraftDown.Events.StatusUpdatedEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gamecube762 on 1/30/14.
 */
public class Main extends JavaPlugin implements Listener{

    protected Config config;

    private BukkitTask statusChecker;
    private Updater updater;

    @Override
    public void onEnable() {
        config = new Config(this);
        getLogger().info("Loaded Config");

        if (config.Settings_AutoUpdate) {
            updater = new Updater(this, 75086, this.getFile(), Updater.UpdateType.DEFAULT, false);
            getLogger().info("Update Status: " + updater.getResult());
        }

        Bukkit.getPluginManager().registerEvents(this, this);

        getLogger().info("Checking the status of the MC services!");
        waitTillUpdate(Bukkit.getConsoleSender());
        Status.updateServiceStatus(this);

        LoadMCStats();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        try {
            if (args[0].equalsIgnoreCase("forcecheck") && sender.hasPermission("IsMcDown.command.ForceUpdateCheck")) {
                Status.updateServiceStatus(this);
                waitTillUpdate(sender);
                return true;
            }

            if (args[0].equalsIgnoreCase("reloadconfig") && sender.hasPermission("IsMcDown.command.ReloadConfig")){
                config.loadConfig();
                return true;
            }

            if(args[0].equalsIgnoreCase("about")) {
                sender.sendMessage(
                        "IsMinecraftDown by Gamecube762 \n" +
                        "Version: " + getDescription().getVersion() +
                        (config.Settings_AutoUpdate ? "\nLatest File: " + updater.getLatestName() : "")
                );
                return true;
            }

        } catch (ArrayIndexOutOfBoundsException e) {}

        sender.sendMessage(Status.getStatusMessage((sender instanceof Player), true, (sender instanceof Player)));

        return true;
    }


    //Wait for the Update to finish before telling them the status
    private List<CommandSender> holdTillUpdate = new ArrayList<CommandSender>();

    public void waitTillUpdate(CommandSender commandSender) {
        holdTillUpdate.add(commandSender);
    }

    @EventHandler
    public void onStatusUpdated(StatusUpdatedEvent e) {
        String time = new SimpleDateFormat("HH:mm").format(e.getDate());

        for (CommandSender a : holdTillUpdate) {

            if (e.getUpdateStatus() == Status.UpdateStatus.FAILED)
                a.sendMessage("The status update on " + time + " failed! Last status:");
            else
                a.sendMessage("Update finished! Current status:");

            a.sendMessage( Status.getStatusMessage(true, true, false) );
        }

        holdTillUpdate.clear();
    }




    public void announcerCheck(){
        for (Services service : Services.values_Simple())
            if (Status.getStatus(service).equalsIgnoreCase("red"))
                if (config.config.getBoolean("Settings.announce." + ( service.getService().equalsIgnoreCase("") ? "MCWebsite" : service.getService() )  ))
                    tellAll( config.Settings_announce_message.replace( "%Service%", service.getServiceURL() ) );
    }

    public int getCheckDelay(){
        return config.Settings_checkDelay;
    }


    private void tellAll(String s){
        for (Player p : Bukkit.getOnlinePlayers()) p.sendMessage(s);
        getLogger().info(s);
    }

    private void LoadMCStats(){
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
            getLogger().info("Metrics Failed! D=");
            getLogger().info("(This won't impact how the plugin works)");
        }
    }

    protected void updateStatusChecker(int i){
        if (statusChecker != null) statusChecker.cancel();
        getLogger().info("[Status Checker] Checking every " + i + " minutes.");
        i = i*60*20;
        statusChecker = new CheckNewStatus(this).runTaskTimerAsynchronously(this, i, i);
    }
}
