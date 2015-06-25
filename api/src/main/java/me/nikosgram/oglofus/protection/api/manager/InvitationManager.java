package me.nikosgram.oglofus.protection.api.manager;

import me.nikosgram.oglofus.protection.api.ActionResponse;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;

import java.util.UUID;

@SuppressWarnings("unused")
public interface InvitationManager {
    ActionResponse invite(Object sender, UUID target, ProtectionRegion region);

    ActionResponse invite(UUID target, ProtectionRegion region);

    ActionResponse cancel(UUID target, ProtectionRegion region);
}
