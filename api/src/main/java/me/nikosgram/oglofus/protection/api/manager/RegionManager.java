package me.nikosgram.oglofus.protection.api.manager;

import com.google.common.base.Optional;
import com.sk89q.intake.parametric.Provider;
import me.nikosgram.oglofus.protection.api.ActionResponse;
import me.nikosgram.oglofus.protection.api.region.ProtectionLocation;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;

import java.util.UUID;

@SuppressWarnings("unused")
public interface RegionManager extends Iterable<ProtectionRegion>, Provider<ProtectionRegion> {
    Optional<ProtectionRegion> getRegion(UUID target);

    Optional<ProtectionRegion> getRegion(String target);

    Optional<ProtectionRegion> getRegion(ProtectionLocation location);

    ActionResponse createProtectionRegion(ProtectionLocation location, UUID owner);

    ActionResponse deleteProtectionRegion(ProtectionRegion area);
}
