package me.nikosgram.oglofus.protection;

import me.nikosgram.oglofus.protection.api.ProtectionPlugin;
import me.nikosgram.oglofus.protection.api.manager.HandlerManager;
import me.nikosgram.oglofus.protection.api.manager.RegionManager;
import me.nikosgram.oglofus.protection.api.manager.UserManager;

@SuppressWarnings("unused")
public final class OglofusProtection {
    private static ProtectionPlugin plugin = null;

    protected static void invoke(ProtectionPlugin plugin) {
        if (OglofusProtection.plugin != null) {
            return;
        }
        OglofusProtection.plugin = plugin;
    }

    public static ProtectionPlugin getPlugin() {
        return plugin;
    }

    public static RegionManager getRegionManager() {
        return plugin.getRegionManager();
    }

    public static UserManager getUserManager() {
        return plugin.getUserManager();
    }

    public static HandlerManager getHandlerManager() {
        return plugin.getHandlerManager();
    }
}
