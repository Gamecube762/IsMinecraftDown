package com.github.Gamecube762.IsMinecraftDown;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

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
    public final static String StatusURLService = "?service=";
    public final static String StatusURLMinecraftNet = "minecraft.net";
    public final static String StatusURLMojangtCom = "mojang.com";

    private String
        status_mcwebsite,
        status_login,
        status_session,
        status_account,
        status_auth,
        status_skins,
        status_authserver,
        status_sessionserver
    ;

    @Override
    public void onEnable() {
        getLogger().info("Loading Config ...");
        config = new Config(this);
        getLogger().info("Loaded Config");

        if (config.Settings_AutoUpdate)
            updater = new Updater(this, 75086, this.getFile(), Updater.UpdateType.DEFAULT, false);

        getLogger().info("Checking MC servers status");
        updateStatus();

        getLogger().info("minecraft.net :            " + status_mcwebsite);
        getLogger().info("login.minecraft.net :      " + status_login);
        getLogger().info("session.minecraft.net :    " + status_session);
        getLogger().info("account.mojang.com :       " + status_account);
        getLogger().info("auth.mojang.com :          " + status_auth);
        getLogger().info("skins.minecraft.net :      " + status_skins);
        getLogger().info("authserver.mojang.com :    " + status_authserver);
        getLogger().info("sessionserver.mojang.com : " + status_sessionserver);

        LoadMCStats();

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("check")) {
                updateStatus();
                sender.sendMessage(getStatusMessage());
             }
            if (args[0].equalsIgnoreCase("reloadconfig")) config.loadConfig();
            if(debugCommands) {
                if (args[0].equals("a")) announcerCheck();
                if (args[0].equals("u")) updateStatus();
                if (args[0].equals("s"))
                    if (args.length > 2) setStatus(args[1], args[2]); else sender.sendMessage("Needs args");
                if (args[0].equals("cset"))
                    if (args.length > 2) config.config.set(args[1], args[2]); else sender.sendMessage("Needs args");
                if (args[0].equals("csave")) config.saveConfig();
                if (args[0].equals("cclean")) config.cleanUp();
                if (args[0].equals("creset")) config.resetConfig();
                if (args[0].equals("mupdatec")) updateStatusChecker(config.Settings_checkDelay);
            }
        }
        else sender.sendMessage(getStatusMessage());

        return true;
    }

    public static String checkService(String service) throws IOException, ParseException {
        if(service.equals("")) service = StatusURLMinecraftNet;
        if(service.equals("login") | service.equals("session") | service.equals("skins")) service = service + "." + StatusURLMinecraftNet;
        if(service.equals("account") | service.equals("auth") | service.equals("authserver") | service.equals("sessionserver")) service = service + "." + StatusURLMojangtCom;

        String checkurl = StatusURL + StatusURLService + service;

        URL url = new URL(checkurl);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        Object obj = parser.parse(in);
        JSONObject jsonObject = (JSONObject) obj;

        return (String) jsonObject.get(service);

    }

    public boolean updateStatus() {

        try {

            try {status_mcwebsite = checkService("");}                  catch (ParseException e) {parseException("");}
            try {status_login = checkService("login");}                 catch (ParseException e) {parseException("login");}
            try {status_session = checkService("session");}             catch (ParseException e) {parseException("session");}
            try {status_account = checkService("account");}             catch (ParseException e) {parseException("account");}
            try {status_auth = checkService("auth");}                   catch (ParseException e) {parseException("auth");}
            try {status_skins = checkService("skins");}                 catch (ParseException e) {parseException("skins");}
            try {status_authserver = checkService("authserver");}       catch (ParseException e) {parseException("authserver");}
            try {status_sessionserver = checkService("sessionserver");} catch (ParseException e) {parseException("sessionserver");}

        } catch (IOException e) {
            getLogger().severe("Couldn't check the status of the MC servers! [IOException ~ Couldn't connect]");

            status_mcwebsite     = "red";
            status_login         = "red";
            status_session       = "red";
            status_account       = "red";
            status_auth          = "red";
            status_skins         = "red";
            status_authserver    = "red";
            status_sessionserver = "red";
            return false;
        }
        return true;
    }

    public void announcerCheck(){//ToDo: Implement non-spamm form of announcing for when multiple are red
        if (config.Settings_announce_MCwebsite     & status_mcwebsite.equals("red"))     tellAll(config.Settings_announce_message.replace("%Service%", "minecraft.net"));
        if (config.Settings_announce_login         & status_login.equals("red"))         tellAll(config.Settings_announce_message.replace("%Service%", "login"));
        if (config.Settings_announce_session       & status_session.equals("red"))       tellAll(config.Settings_announce_message.replace("%Service%", "session"));
        if (config.Settings_announce_account       & status_account.equals("red"))       tellAll(config.Settings_announce_message.replace("%Service%", "account"));
        if (config.Settings_announce_auth          & status_auth.equals("red"))          tellAll(config.Settings_announce_message.replace("%Service%", "auth"));
        if (config.Settings_announce_skins         & status_skins.equals("red"))         tellAll(config.Settings_announce_message.replace("%Service%", "skins"));
        if (config.Settings_announce_authserver    & status_authserver.equals("red"))    tellAll(config.Settings_announce_message.replace("%Service%", "authserver"));
        if (config.Settings_announce_sessionserver & status_sessionserver.equals("red")) tellAll(config.Settings_announce_message.replace("%Service%", "sessionserver"));
    }

    private void setStatus(String service, String status){
        if (service.equals(""))              status_mcwebsite     = status;
        if (service.equals("login"))         status_login         = status;
        if (service.equals("session"))       status_session       = status;
        if (service.equals("account"))       status_account       = status;
        if (service.equals("auth"))          status_auth          = status;
        if (service.equals("skins"))         status_skins         = status;
        if (service.equals("authserver"))    status_authserver    = status;
        if (service.equals("sessionserver")) status_sessionserver = status;
    }

    private void parseException(String service) {
        if (service.equals(""))              { status_mcwebsite     = "red";  service = "minecraft.net";               }
        if (service.equals("login"))         { status_login         = "red";  service = "login.minecraft.net";         }
        if (service.equals("session"))       { status_session       = "red";  service = "session.minecraft.net";       }
        if (service.equals("account"))       { status_account       = "red";  service = "account.minecraft.net";       }
        if (service.equals("auth"))          { status_auth          = "red";  service = "auth.minecraft.net";          }
        if (service.equals("skins"))         { status_skins         = "red";  service = "skins.minecraft.net";         }
        if (service.equals("authserver"))    { status_authserver    = "red";  service = "authserver.minecraft.net";    }
        if (service.equals("sessionserver")) { status_sessionserver = "red";  service = "sessionserver.minecraft.net"; }

        getLogger().severe("Couldn't parse JSON data from " + service + "!");
    }

    public String parseStatusColors(String s){
        if (s.contains("green"))  s = s.replace("green",  ChatColor.GREEN  + "green"  + ChatColor.RESET);
        if (s.contains("yellow")) s = s.replace("yellow", ChatColor.YELLOW + "yellow" + ChatColor.RESET);
        if (s.contains("red"))    s = s.replace("red",    ChatColor.RED    + "red"    + ChatColor.RESET);
        return s;
    }

    public int getCheckDelay(){
        return config.Settings_checkDelay;
    }

    public String getStatus_mcwebsite() {
        return status_mcwebsite;
    }

    public String getStatus_login() {
        return status_login;
    }

    public String getStatus_session() {
        return status_session;
    }

    public String getStatus_account() {
        return status_account;
    }

    public String getStatus_auth() {
        return status_auth;
    }

    public String getStatus_skins() {
        return status_skins;
    }

    public String getStatus_authserver() {
        return status_authserver;
    }

    public String getStatus_sessionserver() {
        return status_sessionserver;
    }

    public String getStatusMessage(){
        return  "minecraft.net :            "   +   parseStatusColors(status_mcwebsite) + "\n" +
                "login.minecraft.net :      "   +   parseStatusColors(status_login)     + "\n" +
                "session.minecraft.net :    "   +   parseStatusColors(status_session)   + "\n" +
                "account.mojang.com :       "   +   parseStatusColors(status_account)   + "\n" +
                "auth.mojang.com :          "   +   parseStatusColors(status_auth)      + "\n" +
                "skins.minecraft.net :      "   +   parseStatusColors(status_skins)     + "\n" +
                "authserver.mojang.com :    "   +   parseStatusColors(status_authserver)+ "\n" +
                "sessionserver.mojang.com : "   +   parseStatusColors(status_sessionserver);
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
        getLogger().info("Starting the Status checker with a time of " + i + " minutes");
        i = i*60*20;
        statusChecker = new CheckNewStatus().runTaskTimerAsynchronously(this, i, i);
    }

    private class CheckNewStatus extends BukkitRunnable {
        public CheckNewStatus(){}

        @Override
        public void run(){
            if ( updateStatus() ) announcerCheck();
        }
    }
}
