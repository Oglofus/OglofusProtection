/*
 * Copyright 2014-2015 Nikos Grammatikos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://raw.githubusercontent.com/nikosgram13/OglofusProtection/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.nikosgram.oglofus.protection;

import com.google.common.base.Optional;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.ArgumentParseException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.ProvisionException;
import me.nikosgram.oglofus.protection.api.ActionResponse;
import me.nikosgram.oglofus.protection.api.CommandExecutor;
import me.nikosgram.oglofus.protection.api.entity.User;
import me.nikosgram.oglofus.protection.api.message.MessageType;
import me.nikosgram.oglofus.protection.api.region.ProtectionRank;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;
import me.nikosgram.oglofus.protection.api.region.ProtectionStaff;
import org.apache.commons.lang3.ClassUtils;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.service.user.UserStorage;
import org.spongepowered.api.util.command.CommandSource;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OglofusProtectionStaff implements ProtectionStaff {
    private final List<User> staff = new ArrayList<User>();
    private final Map<UUID, ProtectionRank> ranks = new HashMap<UUID, ProtectionRank>();
    private final User owner;
    private final ProtectionRegion region;
    private final OglofusSponge sponge;

    protected OglofusProtectionStaff(ProtectionRegion region, OglofusSponge sponge) {
        this.region = region;
        this.sponge = sponge;
        owner = sponge.getUserManager().getUser(UUID.fromString(sponge.connector.getString(
                "oglofus_regions", "uuid", region.getUuid().toString(), "owner"
        ).get())).get();
        Map<String, String> staff = sponge.connector.getStringMap(
                "oglofus_regions", "uuid", region.getUuid().toString(), new String[]{"player", "rank"}
        );
        for (String uid : staff.keySet()) {
            UUID uuid = UUID.fromString(uid);
            this.staff.add(sponge.getUserManager().getUser(uuid).get());
            ranks.put(uuid, ProtectionRank.valueOf(staff.get(uid)));
        }
    }

    @Override
    public UUID getOwnerUuid() {
        return owner.getUuid();
    }

    @Override
    public User getOwner() {
        return owner;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOwnerAs(Class<T> tClass) {
        if (ClassUtils.isAssignable(tClass, Player.class)) {
            return (Optional<T>) sponge.server.getPlayer(owner.getUuid());
        } else if (ClassUtils.isAssignable(tClass, User.class)) {
            UserStorage storage;
            if ((storage = sponge.game.getServiceManager().provide(UserStorage.class).orNull()) !=
                    null) {
                return (Optional<T>) storage.get(owner.getUuid()).orNull();
            }
        }
        return Optional.absent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getOfficersAs(Class<T> tClass) {
        List<T> returned = new ArrayList<T>();
        if (ClassUtils.isAssignable(tClass, Player.class)) {
            for (UUID uuid : getOfficersUuid()) {
                Player player;
                if ((player = sponge.server.getPlayer(uuid).orNull()) != null) {
                    returned.add((T) player);
                }
            }
        }
        return returned;
    }

    @Override
    public Collection<UUID> getOfficersUuid() {
        List<UUID> returned = new ArrayList<UUID>();
        for (User user : getOfficers()) {
            returned.add(user.getUuid());
        }
        return returned;
    }

    @Override
    public Collection<User> getOfficers() {
        List<User> returned = new ArrayList<User>();
        for (User user : this) {
            if (ranks.get(user.getUuid()).equals(ProtectionRank.Officer)) {
                returned.add(user);
            }
        }
        return returned;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getMembersAs(Class<T> tClass) {
        List<T> returned = new ArrayList<T>();
        if (ClassUtils.isAssignable(tClass, Player.class)) {
            for (UUID uuid : getMembersUuid()) {
                Player player;
                if ((player = sponge.server.getPlayer(uuid).orNull()) != null) {
                    returned.add((T) player);
                }
            }
        }
        return returned;
    }

    @Override
    public Collection<UUID> getMembersUuid() {
        List<UUID> returned = new ArrayList<UUID>();
        for (User user : getMembers()) {
            returned.add(user.getUuid());
        }
        return returned;
    }

    @Override
    public Collection<User> getMembers() {
        List<User> returned = new ArrayList<User>();
        for (User user : this) {
            if (ranks.get(user.getUuid()).equals(ProtectionRank.Member)) {
                returned.add(user);
            }
        }
        return returned;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getStaffAs(Class<T> tClass) {
        List<T> returned = new ArrayList<T>();
        if (ClassUtils.isAssignable(tClass, Player.class)) {
            for (User user : this) {
                Player player;
                if ((player = sponge.server.getPlayer(user.getUuid()).orNull()) != null) {
                    returned.add((T) player);
                }
            }
        }
        return returned;
    }

    @Override
    public Collection<UUID> getStaffUuid() {
        Collection<UUID> returned = new ArrayList<UUID>();
        for (User user : this) {
            returned.add(user.getUuid());
        }
        return returned;
    }

    @Override
    public boolean isOwner(UUID target) {
        return owner.getUuid().equals(target);
    }

    @Override
    public boolean isOwner(User target) {
        return owner.getUuid().equals(target.getUuid());
    }

    @Override
    public boolean isOfficer(UUID target) {
        return ranks.containsKey(target) && ranks.get(target).equals(ProtectionRank.Officer);
    }

    @Override
    public boolean isOfficer(User target) {
        return ranks.containsKey(target.getUuid()) && ranks.get(target.getUuid()).equals(ProtectionRank.Officer);
    }

    @Override
    public boolean isMember(UUID target) {
        return ranks.containsKey(target) && ranks.get(target).equals(ProtectionRank.Member);
    }

    @Override
    public boolean isMember(User target) {
        return ranks.containsKey(target.getUuid()) && ranks.get(target.getUuid()).equals(ProtectionRank.Member);
    }

    @Override
    public boolean isStaff(UUID target) {
        return ranks.containsKey(target);
    }

    @Override
    public boolean isStaff(User target) {
        return ranks.containsKey(target.getUuid());
    }

    @Override
    public boolean hasOwnerAccess(UUID target) {
        return isOwner(target) || sponge.getUserManager().getUser(target).get().hasPermission("oglofus.protection.bypass.owner");
    }

    @Override
    public boolean hasOwnerAccess(User target) {
        return isOwner(target) || target.hasPermission("oglofus.protection.bypass.owner");
    }

    @Override
    public boolean hasOfficerAccess(UUID target) {
        return isOfficer(target) || sponge.getUserManager().getUser(target).get().hasPermission("oglofus.protection.bypass.officer");
    }

    @Override
    public boolean hasOfficerAccess(User target) {
        return isOfficer(target) || target.hasPermission("oglofus.protection.bypass.officer");
    }

    @Override
    public boolean hasMemberAccess(UUID target) {
        return isMember(target) || sponge.getUserManager().getUser(target).get().hasPermission("oglofus.protection.bypass.officer");
    }

    @Override
    public boolean hasMemberAccess(User target) {
        return isMember(target) || target.hasPermission("oglofus.protection.bypass.member");
    }

    @Override
    public ProtectionRank getRank(UUID target) {
        return ranks.containsKey(target) ? ranks.get(target) : ProtectionRank.None;
    }

    @Override
    public ProtectionRank getRank(User target) {
        return ranks.containsKey(target.getUuid()) ? ranks.get(target.getUuid()) : ProtectionRank.None;
    }

    @Override
    public void broadcast(String message) {
        broadcast(MessageType.CHAT, message);
    }

    @Override
    public void broadcast(String message, ProtectionRank rank) {
        broadcast(MessageType.CHAT, message, rank);
    }

    @Override
    public void broadcast(MessageType type, String message) {
        for (User user : this) {
            user.sendMessage(type, message);
        }
    }

    @Override
    public void broadcast(MessageType type, String message, ProtectionRank rank) {
        switch (rank) {
            case Member:
                for (User user : getMembers()) {
                    user.sendMessage(type, message);
                }
                break;
            case Officer:
                for (User user : getOfficers()) {
                    user.sendMessage(type, message);
                }
                break;
            case Owner:
                owner.sendMessage(type, message);
                break;
        }
    }

    @Override
    public void broadcastRaw(Object message) {
        for (User user : this) {
            user.sendMessage(message);
        }
    }

    @Override
    public void broadcastRaw(Object message, ProtectionRank rank) {
        switch (rank) {
            case Member:
                for (User user : getMembers()) {
                    user.sendMessage(message);
                }
                break;
            case Officer:
                for (User user : getOfficers()) {
                    user.sendMessage(message);
                }
                break;
            case Owner:
                owner.sendMessage(message);
                break;
        }
    }

    @Override
    public void broadcastRaw(MessageType type, Object message) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void broadcastRaw(MessageType type, Object message, ProtectionRank rank) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ActionResponse reFlag() {
        //TODO: make it.
        return null;
    }

    @Override
    public ActionResponse invite(Object sender, UUID target) {
        return sponge.getUserManager().invite(sender, target, region);
    }

    @Override
    public ActionResponse invite(CommandExecutor sender, UUID target) {
        return null;
    }

    @Override
    public ActionResponse invite(Object sender, User target) {
        return null;
    }

    @Override
    public ActionResponse invite(CommandExecutor sender, User target) {
        return null;
    }

    @Override
    public ActionResponse invite(UUID target) {
        return sponge.getUserManager().invite(target, region);
    }

    @Override
    public ActionResponse invite(User target) {
        return null;
    }

    @Override
    public ActionResponse kick(Object sender, UUID target) {
        if (sender instanceof CommandSource) {
            if (sender instanceof Player) {
                if (region.getProtectionStaff().hasOwnerAccess(((Player) sender).getUniqueId())) {
                    //TODO: call the handler PlayerKickHandler.
                    return kick(target);
                }
                return ActionResponse.Failure.setMessage("access");
            }
            if (((CommandSource) sender).hasPermission("oglofus.protection.bypass")) {
                return kick(target);
            }
            return ActionResponse.Failure.setMessage("access");
        }
        return ActionResponse.Failure.setMessage("object");
    }

    @Override
    public ActionResponse kick(CommandExecutor sender, UUID target) {
        return null;
    }

    @Override
    public ActionResponse kick(Object sender, User target) {
        return null;
    }

    @Override
    public ActionResponse kick(CommandExecutor sender, User target) {
        return null;
    }

    @Override
    public ActionResponse kick(UUID target) {
        //TODO: call the handler PlayerKickHandler.
        return null;
    }

    @Override
    public ActionResponse kick(User target) {
        return null;
    }

    @Override
    public ActionResponse promote(Object sender, UUID target) {
        return null;
    }

    @Override
    public ActionResponse promote(CommandExecutor sender, UUID target) {
        return null;
    }

    @Override
    public ActionResponse promote(Object sender, User target) {
        return null;
    }

    @Override
    public ActionResponse promote(CommandExecutor sender, User target) {
        return null;
    }

    @Override
    public ActionResponse promote(UUID target) {
        return null;
    }

    @Override
    public ActionResponse promote(User target) {
        return null;
    }

    @Override
    public ActionResponse demote(Object sender, UUID target) {
        return null;
    }

    @Override
    public ActionResponse demote(CommandExecutor sender, UUID target) {
        return null;
    }

    @Override
    public ActionResponse demote(Object sender, User target) {
        return null;
    }

    @Override
    public ActionResponse demote(CommandExecutor sender, User target) {
        return null;
    }

    @Override
    public ActionResponse demote(UUID target) {
        return null;
    }

    @Override
    public ActionResponse demote(User target) {
        return null;
    }

    @Override
    public ActionResponse changeRank(Object sender, UUID target, ProtectionRank rank) {
        return null;
    }

    @Override
    public ActionResponse changeRank(CommandExecutor sender, UUID target, ProtectionRank rank) {
        return null;
    }

    @Override
    public ActionResponse changeRank(Object sender, User target, ProtectionRank rank) {
        return null;
    }

    @Override
    public ActionResponse changeRank(CommandExecutor sender, User target, ProtectionRank rank) {
        return null;
    }

    @Override
    public ActionResponse changeRank(UUID target, ProtectionRank rank) {
        return null;
    }

    @Override
    public ActionResponse changeRank(User target, ProtectionRank rank) {
        return null;
    }

    @Override
    public Iterator<User> iterator() {
        return staff.iterator();
    }

    @Override
    public boolean isProvided() {
        return false;
    }

    @Nullable
    @Override
    public User get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
        String name = arguments.next();
        Optional<User> user = sponge.getUserManager().getUser(name);
        if (user.isPresent() && isStaff(user.get())) {
            return user.get();
        } else {
            throw new ArgumentParseException(String.format("I can't find the Staff with name '%s'.", name));
        }
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        List<String> returned = new ArrayList<String>();
        for (User user : this) {
            if (user.getName().startsWith(prefix)) {
                returned.add(user.getName());
            }
        }
        return returned;
    }
}
