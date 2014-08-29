package com.github.Gamecube762.IsMinecraftDown;

import com.github.Gamecube762.IsMinecraftDown.Events.StatusUpdatedEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Date;

/**
 * Created by Gamecube762 on 8/27/14.
 */
public class CheckNewStatus extends BukkitRunnable {
    private Plugin pl;

    public CheckNewStatus(Plugin pl) {
        this.pl = pl;
    }

    @Override
    public void run(){
        final Date cur = new Date();

        JSONArray b = null;

        try { b = getServicesStatus();}
        catch (IOException e) {pl.getLogger().severe("Couldn't check the status of the MC servers! [IOException ~ Couldn't connect]");}
        catch (ParseException e) {pl.getLogger().severe("Couldn't read the status of the MC servers! [ParseException ~ Couldn't parse]");}

        final boolean failed = (b == null);
        final JSONArray latest = b;

        Bukkit.getScheduler().runTask(pl, new Runnable() {
            @Override
            public void run() {

                if (!failed) {
                    Status.lastCheck = cur;
                    Status.status = latest;
                }

                Status.updateStatus = (failed) ? Status.UpdateStatus.FAILED : Status.UpdateStatus.DONE;

                Bukkit.getPluginManager().callEvent(new StatusUpdatedEvent(cur));
            }
        });

    }

    private static JSONParser parser = new JSONParser();

    public static JSONArray getServicesStatus() throws IOException, ParseException {
        URL url = new URL(Status.StatusURL);
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        return  (JSONArray) parser.parse(in);
    }
}
