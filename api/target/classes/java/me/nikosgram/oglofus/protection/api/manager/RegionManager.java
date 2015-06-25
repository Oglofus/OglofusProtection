package me.nikosgram.oglofus.protection.api.manager;

import com.google.common.base.Optional;
import me.nikosgram.oglofus.protection.api.ActionResponse;
import me.nikosgram.oglofus.protection.api.handler.Handler;
import me.nikosgram.oglofus.protection.api.region.ProtectionLocation;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;

import java.util.Collection;
import java.util.UUID;

@SuppressWarnings("unused")
public interface RegionManager {
    Optional<ProtectionRegion> getRegion(UUID target);

    Optional<ProtectionRegion> getRegion(String target);

    Optional<ProtectionRegion> getRegion(ProtectionLocation location);

    Collection<ProtectionRegion> getRegions();

    ActionResponse createProtectionRegion(ProtectionLocation location, UUID owner);

    ActionResponse deleteProtectionRegion(ProtectionRegion area);

    void registerHandler(Handler handler);
}
