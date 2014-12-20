package com.github.Gamecube762.IsMinecraftDown;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Gamecube762 on 8/27/14.
 */
public class Status {

    protected Date timeChecked, timeFinished;
    protected String statusInfo;
    protected StatusLevel statusLevel;
    protected Service service;
    protected UpdateStatus updateStatus = UpdateStatus.NOT_CHECKED;

    public Status(Service service) {
        this.service = service;
    }

    public Date getTimeChecked() {
        return timeChecked;
    }

    public Date getTimeFinished() {
        return timeFinished;
    }

    public long getTimeUsed() {
        if (timeFinished == null) return -1;
        return timeFinished.getTime() - timeChecked.getTime();
    }

    public String getStatusInfo() {
        return statusInfo;
    }

    public StatusLevel getStatusLevel() {
        return statusLevel;
    }

    public Service getService() {
        return service;
    }

    public void printStatus() {
        SimpleDateFormat hm = new SimpleDateFormat("HH:mm"), hms = new SimpleDateFormat("HH:mm:ss");
        System.out.println(String.format("===Status of %s from [%s]===", service.name(), hm.format(getTimeChecked())));
        System.out.println("   " + service.getServiceURL());
        System.out.println(String.format("   UpdateStatus: %s", updateStatus.name()));
        if (updateStatus == Status.UpdateStatus.FAILED)
            System.out.println(String.format("   Started at [%s] and failed at [%s] taking %sms long", hms.format(timeChecked), hms.format(timeFinished), getTimeUsed() ));
        else{
            System.out.println(String.format("   Started at [%s] and ended at [%s] taking %sms long", hms.format(timeChecked), hms.format(timeFinished), getTimeUsed() ));
            System.out.println(String.format("   Status Level: %s", statusLevel.name()));
            System.out.println("   " + statusInfo + "\n");
        }
    }

    public Status.UpdateStatus getUpdateStatus() {
        return updateStatus;
    }

    protected StatusLevel findStatusLevel() {
        if (statusInfo == null)
            return StatusLevel.None;
        if (statusInfo.contains("OK"))
            return StatusLevel.OK;
        if (statusInfo.contains("yellow") || (statusInfo.contains("red") && statusInfo.contains("green")))
            return StatusLevel.YELLOW;
        if (statusInfo.contains("green") && !statusInfo.contains("yellow") && !statusInfo.contains("red"))
            return StatusLevel.GREEN;
        if (!statusInfo.contains("green") && !statusInfo.contains("yellow") && statusInfo.contains("red"))
            return StatusLevel.RED;
        return StatusLevel.Unknown;
    }

    public enum UpdateStatus {
        NOT_CHECKED,
        DONE,
        FAILED
    }

    public enum StatusLevel {
        GREEN,
        YELLOW,
        RED,
        OK,
        Unknown,
        None
    }
}
