package me.nikosgram.oglofus.protection.api.entity;

import com.google.common.base.Optional;
import me.nikosgram.oglofus.protection.api.CommandExecutor;
import me.nikosgram.oglofus.protection.api.message.MessageType;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;

import java.util.Collection;
import java.util.UUID;

//Is offline and online player.
@SuppressWarnings("unused")
public interface User extends CommandExecutor {
    UUID getUuid();

    boolean isOnline();

    Collection<ProtectionRegion> getOwnRegions();

    Collection<ProtectionRegion> getMemberRegions();

    Collection<ProtectionRegion> getOfficerRegions();

    Optional<ProtectionRegion> getRegion();

    void sendMessage(MessageType type, String... messages);
}
