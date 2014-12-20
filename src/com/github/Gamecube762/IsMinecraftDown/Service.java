package com.github.Gamecube762.IsMinecraftDown;

/**
 * Created by Gamecube762 on 7/23/14.
 */
public enum Service {
    WEBSITE     (""          ,   "minecraft.net",   ""                              ),
    SESSION     ("session"   ,   "minecraft.net",   ""                              ),
    ACCOUNT     ("account"   ,   "mojang.com",      ""                              ),
    AUTH        ("auth"      ,   "mojang.com",      ""                              ),
    SKINS       ("skins"     ,   "minecraft.net",   "http://skins.minecraft.net"    ),
    AUTHSERVER  ("authserver",   "mojang.com",      "https://authserver.mojang.com/"),
    SESSIONSERVER("sessionserver","mojang.com",     ""                              ),
    API         ("api"       ,   "mojang.com",      ""                              ),
    TEXTURES    ("textures"  ,   "minecraft.net",   "http://textures.minecraft.net" ),
    StatusServer("status"    ,   "mojang.com",      "http://status.mojang.com"      ),
    ALL         (""          ,   "mojang.com",      "http://status.mojang.com/check");

    private String service, site, url;

    Service(String service, String site, String url) {
        this.service = service;
        this.site = site;
        this.url = url;
    }

    public String getService() {
        return service;
    }

    public String getSite() {
        return site;
    }

    public String getServiceURL() {
        return url;
    }

    public boolean canDirectCheck() {
        return !url.equals("");
    }

    public static Service[] values_Simple() {//__SERVER isn't needed, if one of those is down, the other one is probably down too
        return new Service[] { WEBSITE, SESSION, ACCOUNT, AUTH, SKINS, API, TEXTURES};
    }

}