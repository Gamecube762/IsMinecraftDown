package com.github.Gamecube762.IsMinecraftDown;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gamecube762 on 8/27/14.
 */
public class Status {


    public final static String StatusURL = "http://status.mojang.com/check";

    protected static Date lastCheck;
    protected static JSONArray status;
    protected static UpdateStatus updateStatus = UpdateStatus.NOT_CHECKED;


    public static void updateServiceStatus(Plugin plugin) {
        updateStatus = UpdateStatus.CHECKING;
        Bukkit.getScheduler().runTaskAsynchronously(plugin, new CheckNewStatus(plugin));
    }

    public static Date getLastCheckDate() {
        return lastCheck;
    }

    public static JSONArray getJSONStatusArray() {
        return status;
    }

    public static UpdateStatus getUpdateStatus() {
        return updateStatus;
    }

    private static JSONObject getJSONObject(Services services){
        for (Object a : status.toArray())
            if ( ( (JSONObject)a ).toJSONString().contains(services.getServiceURL()) )
                return (JSONObject) a;
        return null;
    }

    public static String getStatus(Services services) {//readStatus(getJSONObject(services).toJSONString())
        return (String)getJSONObject(services).get(services.getServiceURL());
    }

    public static String getStatus_mcwebsite() {
        return getStatus(Services.WEBSITE);
    }

    public static String getStatus_session() {
        return getStatus(Services.SESSION);
    }

    public static String getStatus_account() {
        return getStatus(Services.ACCOUNT);
    }

    public static String getStatus_auth() {
        return getStatus(Services.AUTH);
    }

    public static String getStatus_skins() {
        return getStatus(Services.SKINS);
    }

    public static String getStatus_authserver() {
        return getStatus(Services.AUTHSERVER);
    }

    public static String getStatus_sessionserver() {
        return getStatus(Services.SESSIONSERVER);
    }

    public static String getStatus_api() {
        return getStatus(Services.API);
    }

    public static String getStatus_textures() {
        return getStatus(Services.TEXTURES);
    }

    public static String getStatusMessage(boolean SimpleList, boolean SimpleNames, boolean Icons){
        if (status == null) return "Status wasn't updated! Try to force a status check.";

        StringBuilder a = new StringBuilder("Last Checked: " + new SimpleDateFormat("HH:mm").format(lastCheck));

        for (Services services : (SimpleList ? Services.values_Simple() : Services.values()) ){
            a.append("\n ");
            a.append(Icons ? parseStatusIcons( getStatus(services) ) : parseStatusColors(getStatus(services)));
            a.append("   ");
            a.append( (!SimpleNames ? services.getServiceURL() : (services.getService().equals("")) ? services.getSite() : services.getService() ));
        }
        return a.toString();
    }

    public static boolean isWorking(String status){
        status = ChatColor.stripColor(status);
        return (status.equalsIgnoreCase("green") || status.equalsIgnoreCase("yellow") ||
                status.equalsIgnoreCase("✔") || status.equalsIgnoreCase("!"));
    }

    public static boolean isWorking_Authentication() {
        return isWorking(getStatus_auth()) && isWorking(getStatus_authserver());
    }

    public static boolean isWorking_sessions() {
        return isWorking(getStatus_session()) && isWorking(getStatus_sessionserver());
    }

    public static boolean canLogin() {
        return isWorking( getStatus_account() ) && isWorking_sessions() && isWorking_Authentication() ;
    }

    public static boolean canJoin() {
        return isWorking_Authentication() && isWorking_sessions();
    }


    public static String parseStatusColors(String s){
        if (s.contains("green"))  s = s.replace("green",  ChatColor.GREEN  + "green"  + ChatColor.RESET);
        if (s.contains("yellow")) s = s.replace("yellow", ChatColor.YELLOW + "yellow" + ChatColor.RESET);
        if (s.contains("red"))    s = s.replace("red",    ChatColor.RED    + "red"    + ChatColor.RESET);
        return s;
    }

    public static String parseStatusIcons(String s){
        if (s.contains("green"))  s = s.replace("green",  ChatColor.GREEN  + "✔"  + ChatColor.RESET);
        if (s.contains("yellow")) s = s.replace("yellow", ChatColor.YELLOW + "! " + ChatColor.RESET);
        if (s.contains("red"))    s = s.replace("red",    ChatColor.RED    + "✗"  + ChatColor.RESET);
        return s;
    }

    public enum UpdateStatus {
        NOT_CHECKED, CHECKING, DONE, FAILED
    }
}
