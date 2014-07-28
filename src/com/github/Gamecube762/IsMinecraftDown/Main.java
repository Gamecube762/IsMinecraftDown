package com.github.Gamecube762.IsMinecraftDown;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gamecube762 on 1/30/14.
 */
public class Main extends JavaPlugin {

    private static JSONParser parser = new JSONParser();

    public Config config;

    private boolean debugCommands = false;

    private BukkitTask statusChecker;
    private Updater updater;

    public final static String StatusURL = "http://status.mojang.com/check";

    Date lastCheck;

    private boolean checked;
    private JSONArray status;

    @Override
    public void onEnable() {
        getLogger().info("Loading Config ...");
        config = new Config(this);
        getLogger().info("Loaded Config");

        if (config.Settings_AutoUpdate)
            updater = new Updater(this, 75086, this.getFile(), Updater.UpdateType.DEFAULT, false);

        getLogger().info("Checking MC servers status");
        checked = updateStatus();
        getLogger().info( getStatusMessage(false, true, false) );

        LoadMCStats();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("forcecheck") && sender.hasPermission("IsMcDown.command.ForceUpdateCheck")) {
                updateStatus();
                if (sender instanceof Player) sender.sendMessage(getStatusMessage(true, true, true));
                else sender.sendMessage(getStatusMessage(false, true, false));
            }

            if (args[0].equalsIgnoreCase("reloadconfig") && sender.hasPermission("IsMcDown.command.ReloadConfig"))
                config.loadConfig();



            if(debugCommands) {
                if (args[0].equals("a")) announcerCheck();
                if (args[0].equals("u")) updateStatus();
                if (args[0].equals("cset"))
                    if (args.length > 2) config.config.set(args[1], args[2]); else sender.sendMessage("Needs args");
                if (args[0].equals("csave")) config.saveConfig();
                if (args[0].equals("cclean")) config.cleanUp();
                if (args[0].equals("creset")) config.resetConfig();
                if (args[0].equals("mupdatec")) updateStatusChecker(config.Settings_checkDelay);
            }

            if(args[0].equalsIgnoreCase("about")) sender.sendMessage(
                    "IsMinecraftDown by Gamecube762 \n" +
                    "Version: " + getDescription().getVersion() +
                    (config.Settings_AutoUpdate ? "\nLatest File: " + updater.getLatestName() : "")
            );
        }
        else {
            if (sender instanceof Player) sender.sendMessage(getStatusMessage(true, true, true));
            else sender.sendMessage(getStatusMessage(false, true, false));
        }

        return true;
    }

    public boolean updateStatus() {
        try {
            status = getServicesStatus();
            lastCheck = new Date();
            return true;
        }
        catch (IOException e) {getLogger().severe("Couldn't check the status of the MC servers! [IOException ~ Couldn't connect]");}
        catch (ParseException e) {getLogger().severe("Couldn't read the status of the MC servers! [ParseException ~ Couldn't parse]");}

        return false;
    }

    public void announcerCheck(){//ToDo: Update to new version and Implement non-spamm form of announcing for when multiple are red
        if (config.Settings_announce_MCwebsite     & getStatus_mcwebsite().equals("red"))     tellAll(config.Settings_announce_message.replace("%Service%", "minecraft.net"));
        if (config.Settings_announce_session       & getStatus_session().equals("red"))       tellAll(config.Settings_announce_message.replace("%Service%", "session"));
        if (config.Settings_announce_account       & getStatus_account().equals("red"))       tellAll(config.Settings_announce_message.replace("%Service%", "account"));
        if (config.Settings_announce_auth          & getStatus_auth().equals("red"))          tellAll(config.Settings_announce_message.replace("%Service%", "auth"));
        if (config.Settings_announce_skins         & getStatus_skins().equals("red"))         tellAll(config.Settings_announce_message.replace("%Service%", "skins"));
        if (config.Settings_announce_authserver    & getStatus_authserver().equals("red"))    tellAll(config.Settings_announce_message.replace("%Service%", "authserver"));
        if (config.Settings_announce_sessionserver & getStatus_sessionserver().equals("red")) tellAll(config.Settings_announce_message.replace("%Service%", "sessionserver"));
    }

    public String parseStatusColors(String s){
        if (s.contains("green"))  s = s.replace("green",  ChatColor.GREEN  + "green"  + ChatColor.RESET);
        if (s.contains("yellow")) s = s.replace("yellow", ChatColor.YELLOW + "yellow" + ChatColor.RESET);
        if (s.contains("red"))    s = s.replace("red",    ChatColor.RED    + "red"    + ChatColor.RESET);
        return s;
    }

    public String parseStatusIcons(String s){
        if (s.contains("green"))  s = s.replace("green",  ChatColor.GREEN  + "✔"  + ChatColor.RESET);
        if (s.contains("yellow")) s = s.replace("yellow", ChatColor.YELLOW + "! " + ChatColor.RESET);
        if (s.contains("red"))    s = s.replace("red",    ChatColor.RED    + "✗"  + ChatColor.RESET);
        return s;
    }

    public int getCheckDelay(){
        return config.Settings_checkDelay;
    }

    private JSONObject getJSONObject(Services services){
        for (Object a : status.toArray())
            if ( ( (JSONObject)a ).toJSONString().contains(services.getServiceURL()) )
                return (JSONObject) a;
        return null;
    }

    public String getStatus(Services services) {//readStatus(getJSONObject(services).toJSONString())
        return (checked) ? (String)getJSONObject(services).get(services.getServiceURL()) : "Check Failed";
    }

    public String getStatus_mcwebsite() {
        return getStatus(Services.WEBSITE);
    }

    public String getStatus_session() {
        return getStatus(Services.SESSION);
    }

    public String getStatus_account() {
        return getStatus(Services.ACCOUNT);
    }

    public String getStatus_auth() {
        return getStatus(Services.AUTH);
    }

    public String getStatus_skins() {
        return getStatus(Services.SKINS);
    }

    public String getStatus_authserver() {
        return getStatus(Services.AUTHSERVER);
    }

    public String getStatus_sessionserver() {
        return getStatus(Services.SESSIONSERVER);
    }

    public String getStatus_api() {
        return getStatus(Services.API);
    }

    public String getStatus_textures() {
        return getStatus(Services.TEXTURES);
    }

    public String getStatusMessage(boolean SimpleList, boolean SimpleNames, boolean Icons){
        StringBuilder a = new StringBuilder("Last Checked: " + new SimpleDateFormat("HH:mm").format(lastCheck));

        for (Services services : (SimpleList ? Services.values_Simple() : Services.values()) ){
             a.append("\n ");
             a.append(Icons ? parseStatusIcons( getStatus(services) ) : parseStatusColors(getStatus(services)));
             a.append("   ");
             a.append( (!SimpleNames ? services.getServiceURL() : (services.getService().equals("")) ? services.getSite() : services.getService() ));
        }
        return a.toString();
    }

    public boolean isWorking(String status){
        status = ChatColor.stripColor(status);
        return (status.equalsIgnoreCase("green") || status.equalsIgnoreCase("yellow") ||
                status.equalsIgnoreCase("✔") || status.equalsIgnoreCase("!"));
    }

    public boolean isWorking_Authentication() {
        return isWorking(getStatus_auth()) && isWorking(getStatus_authserver());
    }

    public boolean isWorking_sessions() {
        return isWorking(getStatus_session()) && isWorking(getStatus_sessionserver());
    }

    public boolean canLogin() {
        return isWorking( getStatus_account() ) && isWorking_sessions() && isWorking_Authentication() ;
    }

    public boolean canJoin() {
        return isWorking_Authentication() && isWorking_sessions();
    }

    public static JSONArray getServicesStatus() throws IOException, ParseException {
        URL url = new URL(StatusURL);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        return  (JSONArray) parser.parse(in);
    }

    private void tellAll(String s){
        for (Player p :Bukkit.getOnlinePlayers()) p.sendMessage(s);
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
        statusChecker = new CheckNewStatus().runTaskTimer(this, i, i);
    }

    private class CheckNewStatus extends BukkitRunnable {
        public CheckNewStatus(){}

        @Override
        public void run(){
            if ( updateStatus() ) announcerCheck();
        }
    }
}
