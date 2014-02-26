package com.github.Gamecube762.IsMinecraftDown;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * Created by Gamecube762 on 2/25/14.
 */
public class MOTDHandler implements Listener {

    Main main;

    public MOTDHandler(Main main) {
        this.main = main;
    }

    @EventHandler(priority = EventPriority.HIGH)//We want it high so it overrides the MOTD set by another plugin
    public void MOTD(ServerListPingEvent e){
        if(main.config.Settings_announce_MOTD){//ToDo: Turn this into a switch type statement so user had control over which servers to announce
            String s = main.config.getSettings_announce_MOTDmessage();
            s = s.replace("%Service%","Session")    .replace("%Service%", "SessionServer")
                    .replace("%Service%", "Auth")    .replace("%Service%", "AuthServer")
                    .replace("%Service%", "Login")   .replace("%Service%", "account")
                    .replace("%Service%", "skins")   .replace("%Service%", "minecraft.net")
            ;
            e.setMotd( ChatColor.translateAlternateColorCodes('&', s) );
        }//Todo: make use of the players list in Servers List to announce the servers
    }
}
