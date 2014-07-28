package com.github.Gamecube762.IsMinecraftDown;

/**
 * Created by Gamecube762 on 7/23/14.
 */
public enum Services {
    WEBSITE     (""          ,   "minecraft.net"),
    SESSION     ("session"   ,   "minecraft.net"),
    ACCOUNT     ("account"   ,   "mojang.com"),
    AUTH        ("auth"      ,   "mojang.com"),
    SKINS       ("skins"     ,   "minecraft.net"),
    AUTHSERVER  ("authserver",   "mojang.com"),
    SESSIONSERVER("sessionserver","mojang.com"),
    API         ("api"       ,   "mojang.com"),
    TEXTURES    ("textures"  ,   "minecraft.net");

    private String service, site;

    Services(String service, String site) {
        this.service = service;
        this.site = site;
    }

    public String getService() {
        return service;
    }

    public String getSite() {
        return site;
    }

    public String getServiceURL() {//Added check and fix for WEBSITE service and any other "" services
        return ( ( getService().equals("") ) ? "" : getService() + "." ) + getSite();
    }

    public static Services[] values_Simple() {//__SERVER isn't needed, if one of those is down, the other one is probably down too
        return new Services[] { WEBSITE, SESSION, ACCOUNT, AUTH, SKINS, API, TEXTURES};
    }

}