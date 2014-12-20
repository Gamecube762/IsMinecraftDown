package com.github.Gamecube762.IsMinecraftDown.Loaders;

/**
 * Created by Gamecube762 on 12/18/2014.
 */
public class LoaderManager {
    private static LoaderManager loaderManager;

    private ServerAPI serverAPI;
    private Object loader;

    public LoaderManager(ServerAPI serverAPI, Object loader) {
        this.serverAPI = serverAPI;
        this.loader = loader;

        if (loaderManager == null)
            loaderManager = this;
    }

    public static ServerAPI getLoadedAPI() {
        return (loaderManager == null) ? null : loaderManager.serverAPI;
    }

    public static Object getLoader() {
        return loaderManager.loader;
    }

    public enum ServerAPI {
        BUKKIT,
        StandAlone
    }
}
