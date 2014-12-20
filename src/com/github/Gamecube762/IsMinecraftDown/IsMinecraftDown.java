package com.github.Gamecube762.IsMinecraftDown;

import com.github.Gamecube762.IsMinecraftDown.Events.Bukkit.AllStatusUpdatesCompletedEvent;
import com.github.Gamecube762.IsMinecraftDown.Events.Bukkit.StatusUpdatedEvent;
import com.github.Gamecube762.IsMinecraftDown.Loaders.BukkitLoader;
import com.github.Gamecube762.IsMinecraftDown.Loaders.LoaderManager;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Gamecube762 on 12/11/2014.
 */
public class IsMinecraftDown {

    private static HashMap<Service, Status> statusMap = new HashMap<Service, Status>();
    static {for (Service service : Service.values()) if (service.canDirectCheck()) statusMap.put(service, new Status(service));}

    protected static HashMap<Thread, Service> threads = new HashMap<Thread, Service>();

    protected static boolean debugMode = true;

    private static JSONParser parser = new JSONParser();

    /*
     | Actions |
     */

    public static void checkStatus(final Service service) {
        if (!service.canDirectCheck()) {
            System.out.println("Can't direct check " + service.name() + " as there is no url to ping.");
            return;
        }

        //A check is already running for this service
        if (threads.containsValue(service)) return;

        Thread a = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Status status = new Status(service);
                        status.timeChecked = new Date();

                        try {status.statusInfo = getServiceResponce(service);}
                        catch (IOException ex) {
                            System.out.println("Could not connect to check the Service " + service.name() + "[IOException]");
                            status.updateStatus = Status.UpdateStatus.FAILED;
                        }

                        status.statusLevel = status.findStatusLevel();
                        status.timeFinished = new Date();
                        status.updateStatus = Status.UpdateStatus.DONE;

                        completedCheck(Thread.currentThread(), status);
                    }
                })
        ;

        synchronized (Thread.currentThread()) {
            threads.put(a, service);
        }

        a.start();
    }

    public static void checkAllStatus() {
        for (Service service : Service.values())
            if (service.canDirectCheck())
                checkStatus(service);
    }

    private synchronized static void completedCheck(Thread t, Status status) {
        if (debugMode)
            System.out.println(
                    String.format(
                            "Finished check [%s] @ %-6s [%s]",
                            new SimpleDateFormat("HH:mm:ss").format(status.getTimeChecked()),
                            status.getTimeUsed() + "ms",
                            status.service.name()
                    )
            );

        threads.remove(t);
        setStatus(status.getService(), status);

        if (LoaderManager.getLoadedAPI() == LoaderManager.ServerAPI.BUKKIT)
            BukkitLoader.callEvent(new StatusUpdatedEvent(status));

        if (threads.isEmpty())
            if (LoaderManager.getLoadedAPI() == LoaderManager.ServerAPI.BUKKIT)
                BukkitLoader.callEvent(new AllStatusUpdatesCompletedEvent());
    }

    /*
     | Getters/Setters |
     */

    public synchronized static Status getStatus(Service service) {
        return statusMap.get(service);
    }

    protected synchronized static void setStatus(Service service, Status status) {
        statusMap.put(service, status);
    }

    public synchronized static HashMap<Thread, Service> getThreads() {
        return threads;
    }

    public synchronized static Collection<Status> getAllCurrentStatus() {
        return statusMap.values();
    }

    @Deprecated//Todo make formatter
    public static String getFormatedStatus() {
        Status a = getStatus(Service.ALL);
        return String.format("Status for %s from [%s]\n", a.getService(), new SimpleDateFormat("HH:mm").format(a.getTimeChecked())) +
               a.getStatusInfo()
                .replace("[", "").replace("]", "")
                .replace("{", "").replace("}", "")
                .replace("\"", "")
                .replace(".minecraft.net", "")
                .replace(".mojang.com", "")
                .replace(",","\n")
                .replace(":", " : ");
    }

    @Deprecated
    public String getStatusMessage(Service service) {
        Status status = getStatus(service);

        if (status.updateStatus == Status.UpdateStatus.NOT_CHECKED)
            return "Status wasn't updated! Try to force a status check.";

        StringBuilder a = new StringBuilder("Last Checked: " + new SimpleDateFormat("HH:mm").format(status.getTimeChecked()));

        return a.toString();
    }


    public static String getServiceResponce(Service service) throws IOException, NullPointerException{
        if (!service.canDirectCheck()) throw new NullPointerException("Service " + service.name() + " has no URL to ping!");

        URL url = new URL(service.getServiceURL());
        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

        return in.readLine();
    }


}
