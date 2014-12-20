package com.github.Gamecube762.IsMinecraftDown.Loaders;

import com.github.Gamecube762.IsMinecraftDown.IsMinecraftDown;
import com.github.Gamecube762.IsMinecraftDown.Status;

import java.util.Date;

/**
 * Created by Gamecube762 on 12/17/2014.
 */
public class StandAloneLoader {

    public static Date a;

    public static void main(String[] args) throws InterruptedException {
        new LoaderManager(LoaderManager.ServerAPI.StandAlone, null);//todo

        a = new Date();

        System.out.println("Starting... Running checks on every Service");
        IsMinecraftDown.checkAllStatus();

        while (!IsMinecraftDown.getThreads().isEmpty())
            for (Thread t : IsMinecraftDown.getThreads().keySet()){
                t.join();//wait till one of the threads is done, while loop will check if others are
                break;
            }

        System.out.println((new Date().getTime() - a.getTime()) + "ms\n");

        for (Status status : IsMinecraftDown.getAllCurrentStatus())
            status.printStatus();

        System.out.println(IsMinecraftDown.getFormatedStatus());
    }

}
