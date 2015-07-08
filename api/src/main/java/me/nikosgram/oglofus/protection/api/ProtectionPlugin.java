package me.nikosgram.oglofus.protection.api;

import me.nikosgram.oglofus.protection.api.manager.HandlerManager;
import me.nikosgram.oglofus.protection.api.manager.RegionManager;
import me.nikosgram.oglofus.protection.api.manager.UserManager;

@SuppressWarnings("unused")
public interface ProtectionPlugin {
    String getVersion();

    String getVersionName();

    int getProtocol();

    Platform getPlatform();

    RegionManager getRegionManager();

    UserManager getUserManager();

    HandlerManager getHandlerManager();
}
