package me.nikosgram.oglofus.protection;

import me.nikosgram.oglofus.protection.api.ProtectionPlugin;
import me.nikosgram.oglofus.protection.api.manager.InvitationManager;
import me.nikosgram.oglofus.protection.api.manager.RegionManager;

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

    public static InvitationManager getInvitationManager() {
        return plugin.getInvitationManager();
    }
}
