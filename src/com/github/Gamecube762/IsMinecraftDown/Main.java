package com.github.Gamecube762.IsMinecraftDown;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
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

    public final static String StatusURL = "http://status.mojang.com/check";
    public final static String StatusURLService = "?service=";
    public final static String StatusURLMinecraftNet = "minecraft.net";
    public final static String StatusURLMojangtCom = "mojang.com";

    private String status_mc_website;
    private String status_login;
    private String status_session;
    private String status_account;
    private String status_auth;
    private String status_skins;
    private String status_authserver;
    private String status_sessionserver;

    @Override
    public void onEnable() {

        getLogger().info("Checking MC servers status");
        updateStatus();

        getLogger().info("minecraft.net : "             +status_mc_website);
        getLogger().info("login.minecraft.net : "       +status_login);
        getLogger().info("session.minecraft.net : "     +status_session);
        getLogger().info("account.mojang.com : "        +status_account);
        getLogger().info("auth.mojang.com : "           +status_auth);
        getLogger().info("skins.minecraft.net : "       +status_skins);
        getLogger().info("authserver.mojang.com : "     +status_authserver);
        getLogger().info("sessionserver.mojang.com : "  +status_sessionserver);

        //---------------Schedule for every 5 mins (20 tics per second, 60 seconds per minute, 5 minutes)
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new CheckNewStatus(this), 5*60*20, 5*60*20);


    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String labal, String[] args) {
        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("check")) {updateStatus(); sender.sendMessage(getStatusMessage());}

        }
        else sender.sendMessage(getStatusMessage());

        return true;
    }

    public static String checkService(String service) throws IOException, ParseException {
        if(service.equals("")) service = StatusURLMinecraftNet;
        if(service.equals("login")|service.equals("session")|service.equals("skins")) service = service + "." + StatusURLMinecraftNet;
        if(service.equals("account")|service.equals("auth")|service.equals("authserver")|service.equals("sessionserver")) service = service + "." + StatusURLMojangtCom;

        String checkurl = StatusURL + StatusURLService + service;

        URL url = new URL(checkurl);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        Object obj = parser.parse(in);
        JSONObject jsonObject = (JSONObject) obj;

        return (String) jsonObject.get(service);

    }

    public void updateStatus() {

        try {

            try {status_mc_website = checkService("");}                 catch (ParseException e) {parseException("");}
            try {status_login = checkService("login");}                 catch (ParseException e) {parseException("login");}
            try {status_session = checkService("session");}             catch (ParseException e) {parseException("session");}
            try {status_account = checkService("account");}             catch (ParseException e) {parseException("account");}
            try {status_auth = checkService("auth");}                   catch (ParseException e) {parseException("auth");}
            try {status_skins = checkService("skins");}                 catch (ParseException e) {parseException("skins");}
            try {status_authserver = checkService("authserver");}       catch (ParseException e) {parseException("authserver");}
            try {status_sessionserver = checkService("sessionserver");} catch (ParseException e) {parseException("sessionserver");}

        } catch (IOException e) {
            getLogger().severe("Couldn't check the status of the MC servers! [IOException ~ Couldn't connect]");

            status_mc_website = "red";
            status_login = "red";
            status_session = "red";
            status_account = "red";
            status_auth = "red";
            status_skins = "red";
            status_authserver = "red";
            status_sessionserver = "red";

        }
    }

    private void parseException(String service) {
        if (service.equals(""))             {status_mc_website = "red";     service = "minecraft.net";}
        if (service.equals("login"))        {status_login = "red";          service = "login.minecraft.net";}
        if (service.equals("session"))      {status_session = "red";        service = "session.minecraft.net";}
        if (service.equals("account"))      {status_account = "red";        service = "account.minecraft.net";}
        if (service.equals("auth"))         {status_auth = "red";           service = "auth.minecraft.net";}
        if (service.equals("skins"))        {status_skins = "red";          service = "skins.minecraft.net";}
        if (service.equals("authserver"))   {status_authserver = "red";     service = "authserver.minecraft.net";}
        if (service.equals("sessionserver")){status_sessionserver = "red";  service = "sessionserver.minecraft.net";}

        getLogger().severe("Couldn't parse Json data from " + service + "!");
    }

    public String parseStatusColors(String s){
        if (s.contains("green")) s = s.replace("green", ChatColor.GREEN + "green" + ChatColor.RESET);
        if (s.contains("yellow")) s = s.replace("yellow", ChatColor.YELLOW + "yellow" + ChatColor.RESET);
        if (s.contains("red")) s = s.replace("red", ChatColor.RED + "red" + ChatColor.RESET);
        return s;
    }

    public String getStatus_mc_website() {
        return status_mc_website;
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
        return  "minecraft.net :            "     +parseStatusColors(status_mc_website) + "\n" +
                "login.minecraft.net :      "     +parseStatusColors(status_login)     + "\n" +
                "session.minecraft.net :    "     +parseStatusColors(status_session)   + "\n" +
                "account.mojang.com :       "     +parseStatusColors(status_account)   + "\n" +
                "auth.mojang.com :          "     +parseStatusColors(status_auth)      + "\n" +
                "skins.minecraft.net :      "     +parseStatusColors(status_skins)     + "\n" +
                "authserver.mojang.com :    "     +parseStatusColors(status_authserver) + "\n" +
                "sessionserver.mojang.com : "     +parseStatusColors(status_sessionserver);
    }

    private class CheckNewStatus extends BukkitRunnable {
        private Main plugin;
        public CheckNewStatus(Main plugin){this.plugin = plugin;}

        @Override
        public void run(){plugin.updateStatus();}
    }
}
