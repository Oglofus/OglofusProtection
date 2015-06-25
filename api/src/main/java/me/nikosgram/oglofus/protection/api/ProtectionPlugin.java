package me.nikosgram.oglofus.protection.api;

import me.nikosgram.oglofus.protection.api.manager.InvitationManager;
import me.nikosgram.oglofus.protection.api.manager.RegionManager;

@SuppressWarnings("unused")
public interface ProtectionPlugin {
    String getVersion();

    String getVersionName();

    int getProtocol();

    Platform getPlatform();

    RegionManager getRegionManager();

    InvitationManager getInvitationManager();
}
