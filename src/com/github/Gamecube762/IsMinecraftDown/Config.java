package com.github.Gamecube762.IsMinecraftDown;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by Gamecube762 on 2/24/14.
 */
public class Config {

    Main main;

    protected File configFile;
    protected YamlConfiguration config;

    protected int Settings_checkDelay = 0;

    protected boolean
        Settings_AutoUpdate,
        Settings_announce_MCwebsite,
        Settings_announce_login,
        Settings_announce_session,
        Settings_announce_account,
        Settings_announce_auth,
        Settings_announce_skins,
        Settings_announce_textures,
        Settings_announce_api,
        Settings_announce_MOTD
    ;

    protected String
        Settings_announce_message,
        Settings_announce_MOTDmessage
    ;


    public Config(Main main){
        this.main = main;

        this.main.getDataFolder().mkdirs();

        configFile = new File(this.main.getDataFolder(), "config.yml");
        load();

        updateConfig();
        loadConfig();
    }

    public void updateConfig() {

        if (!config.contains("Settings.checkDelay"))                         config.set("Settings.checkDelay",           5);
        if (!config.contains("Settings.AutoUpdate"))                         config.set("Settings.AutoUpdate",           true);
        if (!config.contains("Settings.announce.message"))                   config.set("Settings.announce.message",     "Warning, %Service% is Down!");
        if (!config.contains("Settings.announce.MCwebsite"))                 config.set("Settings.announce.MCwebsite",   false);
        if (!config.contains("Settings.announce.login"))                     config.set("Settings.announce.login",       false);
        if (!config.contains("Settings.announce.session"))                   config.set("Settings.announce.session",     false);
        if (!config.contains("Settings.announce.account"))                   config.set("Settings.announce.account",     false);
        if (!config.contains("Settings.announce.auth"))                      config.set("Settings.announce.auth",        false);
        if (!config.contains("Settings.announce.skins"))                     config.set("Settings.announce.skins",       false);
        if (!config.contains("Settings.announce.MOTD"))                      config.set("Settings.announce.MOTD",        false);
        if (!config.contains("Settings.announce.MOTDmessage"))               config.set("Settings.announce.MOTDmessage", "Warning, %Service% is Down!");

        //removed
        if (config.contains("Settings.announce.sessionserver"))             config.set("Settings.announce.sessionserver",null);
        if (config.contains("Settings.announce.authserver"))                config.set("Settings.announce.authserver",  null);

        save();
    }

    public void loadConfig(){
        load(); int a = Settings_checkDelay;

        Settings_checkDelay             =   config.getInt("Settings.checkDelay");

        Settings_AutoUpdate             =   config.getBoolean("Settings.AutoUpdate");
        Settings_announce_MCwebsite     =   config.getBoolean("Settings.announce.MCwebsite");
        Settings_announce_login         =   config.getBoolean("Settings.announce.login");
        Settings_announce_session       =   config.getBoolean("Settings.announce.session");
        Settings_announce_account       =   config.getBoolean("Settings.announce.account");
        Settings_announce_auth          =   config.getBoolean("Settings.announce.auth");
        Settings_announce_skins         =   config.getBoolean("Settings.announce.skins");
        Settings_announce_textures      =   config.getBoolean("Settings.announce.textures");
        Settings_announce_api           =   config.getBoolean("Settings.announce.api");
        Settings_announce_MOTD          =   config.getBoolean("Settings.announce.MOTD");

        Settings_announce_message       =   config.getString("Settings.announce.message");
        Settings_announce_MOTDmessage   =   config.getString("Settings.announce.MOTDmessage");


        if (Settings_checkDelay == 0) {
            main.getLogger().info("Settings.checkDelay can't be 0, defaulting to 5");
            Settings_checkDelay = 5;
            saveConfig();
        }

        if (Settings_checkDelay < 0) {
            Settings_checkDelay = Settings_checkDelay * -1;
            main.getLogger().info("Settings can't be negative, changing to " + Settings_checkDelay);
            saveConfig();
        }

        //If the check delay changes, we want to update the Checker
        //check for change or if a == 0 (a will not be 0 unless plugin is starting)
        if (a != Settings_checkDelay || a == 0) main.updateStatusChecker(Settings_checkDelay);

    }

    public void saveConfig() {
        config.set("Settings.checkDelay",               Settings_checkDelay);
        config.set("Settings.AutoUpdate",               Settings_AutoUpdate);
        config.set("Settings.announce.message",         Settings_announce_message);
        config.set("Settings.announce.MCwebsite",       Settings_announce_MCwebsite);
        config.set("Settings.announce.login",           Settings_announce_login);
        config.set("Settings.announce.session",         Settings_announce_session);
        config.set("Settings.announce.account",         Settings_announce_account);
        config.set("Settings.announce.auth",            Settings_announce_auth);
        config.set("Settings.announce.skins",           Settings_announce_skins);
        config.set("Settings.announce.textures",        Settings_announce_textures);
        config.set("Settings.announce.api",             Settings_announce_api);
        config.set("Settings.announce.MOTD",            Settings_announce_MOTD);
        config.set("Settings.announce.MOTDmessage",     Settings_announce_MOTDmessage);

        save();
    }

    //used to remove any unneeded lines in config file
    //also reorganizes file which can get unorganized when updating
    public void cleanUp() {
        saveConfig();
        loadConfig();
        config = new YamlConfiguration();
        saveConfig();
    }

    //used to reset the config to the defaults if needed.
    protected void resetConfig(){
        config = new YamlConfiguration();
        updateConfig();
    }

    private void load() { config = (configFile.exists()) ? YamlConfiguration.loadConfiguration(configFile) : new YamlConfiguration(); }
    private void save() { try {config.save(configFile);} catch (IOException e) { main.getLogger().severe("Couldn't save config file! [IOException ~ Couldn't write file]"); } }

    public boolean isSettings_announce_MCwebsite() {
        return Settings_announce_MCwebsite;
    }

    public boolean isSettings_announce_login() {
        return Settings_announce_login;
    }

    public boolean isSettings_announce_session() {
        return Settings_announce_session;
    }

    public boolean isSettings_announce_account() {
        return Settings_announce_account;
    }

    public boolean isSettings_announce_auth() {
        return Settings_announce_auth;
    }

    public boolean isSettings_announce_skins() {
        return Settings_announce_skins;
    }

    public boolean isSettings_announce_textures() {
        return Settings_announce_textures;
    }

    public boolean isSettings_announce_api() {
        return Settings_announce_api;
    }

    public boolean isSettings_announce_MOTD() {
        return Settings_announce_MOTD;
    }

    public String getSettings_announce_message() {
        return Settings_announce_message;
    }

    public String getSettings_announce_MOTDmessage() {
        return Settings_announce_MOTDmessage;
    }

    public int getSettings_checkDelay() {
        return Settings_checkDelay;
    }

    public boolean isSettings_AutoUpdate() {
        return Settings_AutoUpdate;
    }
}
