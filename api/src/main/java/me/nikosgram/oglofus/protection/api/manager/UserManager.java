package me.nikosgram.oglofus.protection.api.manager;

import com.google.common.base.Optional;
import com.sk89q.intake.parametric.Provider;
import me.nikosgram.oglofus.protection.api.ActionResponse;
import me.nikosgram.oglofus.protection.api.entity.User;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;

import java.util.Collection;
import java.util.UUID;

@SuppressWarnings("unused")
public interface UserManager extends Iterable<User>, Provider<User> {
    @Deprecated
    Optional<User> getUser(String name);

    Optional<User> getUser(UUID uuid);

    Collection<User> getOnlineUsers();

    ActionResponse invite(Object sender, UUID target, ProtectionRegion region);

    ActionResponse invite(UUID target, ProtectionRegion region);

    ActionResponse cancel(UUID target, ProtectionRegion region);
}
