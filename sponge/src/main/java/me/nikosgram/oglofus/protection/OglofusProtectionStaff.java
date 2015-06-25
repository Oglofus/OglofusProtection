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
import lombok.Getter;
import me.nikosgram.oglofus.protection.api.ActionResponse;
import me.nikosgram.oglofus.protection.api.message.MessageType;
import me.nikosgram.oglofus.protection.api.region.ProtectionRank;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;
import me.nikosgram.oglofus.protection.api.region.ProtectionStaff;
import org.apache.commons.lang3.ClassUtils;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.User;
import org.spongepowered.api.service.user.UserStorage;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.util.command.CommandSource;

import java.util.*;

public class OglofusProtectionStaff implements ProtectionStaff {
    private final Map<UUID, ProtectionRank> staff = new HashMap<>();
    @Getter
    private final UUID owner;
    private final ProtectionRegion region;
    private final OglofusSponge sponge;

    protected OglofusProtectionStaff(ProtectionRegion region, OglofusSponge sponge) {
        this.region = region;
        this.sponge = sponge;
        owner = UUID.fromString(
                sponge.connector.getString(
                        "oglofus_regions", "uuid", region.getUuid().toString(), "owner"
                ).get()
        );
        Map<String, String> staff = sponge.connector.getStringMap(
                "oglofus_regions", "uuid", region.getUuid().toString(), new String[]{"player", "rank"}
        );
        for (String uid : staff.keySet()) {
            this.staff.put(UUID.fromString(uid), ProtectionRank.valueOf(staff.get(uid)));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getOwnerAs(Class<T> tClass) {
        if (ClassUtils.isAssignable(tClass, Player.class)) {
            return (Optional<T>) sponge.server.getPlayer(owner);
        } else if (ClassUtils.isAssignable(tClass, User.class)) {
            UserStorage storage;
            if ((storage = sponge.game.getServiceManager().provide(UserStorage.class).orNull()) !=
                    null) {
                return (Optional<T>) storage.get(owner).orNull();
            }
        }
        return Optional.absent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getOfficersAs(Class<T> tClass) {
        List<T> returned = new ArrayList<T>();
        if (ClassUtils.isAssignable(tClass, Player.class)) {
            for (UUID uuid : getOfficers()) {
                Player player;
                if ((player = sponge.server.getPlayer(uuid).orNull()) != null) {
                    returned.add((T) player);
                }
            }
        } else if (ClassUtils.isAssignable(tClass, User.class)) {
            UserStorage storage;
            if ((storage = sponge.game.getServiceManager().provide(UserStorage.class).orNull()) !=
                    null) {
                for (UUID uuid : getOfficers()) {
                    User player;
                    if ((player = storage.get(uuid).orNull()) != null) {
                        returned.add((T) player);
                    }
                }
            }
        }
        return returned;
    }

    @Override
    public Collection<UUID> getOfficers() {
        List<UUID> returned = new ArrayList<>();
        for (UUID uuid : staff.keySet()) {
            if (staff.get(uuid).equals(ProtectionRank.Officer)) {
                returned.add(uuid);
            }
        }
        return returned;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getMembersAs(Class<T> tClass) {
        List<T> returned = new ArrayList<T>();
        if (ClassUtils.isAssignable(tClass, Player.class)) {
            for (UUID uuid : getMembers()) {
                Player player;
                if ((player = sponge.server.getPlayer(uuid).orNull()) != null) {
                    returned.add((T) player);
                }
            }
        } else if (ClassUtils.isAssignable(tClass, User.class)) {
            UserStorage storage;
            if ((storage = sponge.game.getServiceManager().provide(UserStorage.class).orNull()) !=
                    null) {
                for (UUID uuid : getMembers()) {
                    User player;
                    if ((player = storage.get(uuid).orNull()) != null) {
                        returned.add((T) player);
                    }
                }
            }
        }
        return returned;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<UUID> getMembers() {
        List<UUID> returned = new ArrayList<UUID>();
        for (UUID uuid : staff.keySet()) {
            if (staff.get(uuid).equals(ProtectionRank.Member)) {
                returned.add(uuid);
            }
        }
        return returned;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Collection<T> getStaffAs(Class<T> tClass) {
        List<T> returned = new ArrayList<T>();
        if (ClassUtils.isAssignable(tClass, Player.class)) {
            for (UUID uuid : staff.keySet()) {
                Player player;
                if ((player = sponge.server.getPlayer(uuid).orNull()) != null) {
                    returned.add((T) player);
                }
            }
        } else if (ClassUtils.isAssignable(tClass, User.class)) {
            UserStorage storage;
            if ((storage = sponge.game.getServiceManager().provide(UserStorage.class).orNull()) !=
                    null) {
                for (UUID uuid : staff.keySet()) {
                    User player;
                    if ((player = storage.get(uuid).orNull()) != null) {
                        returned.add((T) player);
                    }
                }
            }
        }
        return returned;
    }

    @Override
    public Collection<UUID> getStaff() {
        return staff.keySet();
    }

    @Override
    public boolean isOwner(UUID target) {
        return target.equals(owner);
    }

    @Override
    public boolean isOfficer(UUID target) {
        return staff.containsKey(target) && staff.get(target).equals(ProtectionRank.Officer);
    }

    @Override
    public boolean isMember(UUID target) {
        return staff.containsKey(target) && staff.get(target).equals(ProtectionRank.Member);
    }

    @Override
    public boolean isStaff(UUID target) {
        return staff.containsKey(target);
    }

    @Override
    public boolean hasOwnerAccess(UUID target) {
        if (target.equals(owner)) {
            return true;
        }
        Player player;
        return (player = sponge.server.getPlayer(target).orNull()) != null &&
                player.hasPermission("oglofus.protection.bypass.owner");
    }

    @Override
    public boolean hasOfficerAccess(UUID target) {
        if (staff.containsKey(target) && staff.get(target).equals(ProtectionRank.Officer)) {
            return true;
        }
        Player player;
        return (player = sponge.server.getPlayer(target).orNull()) != null &&
                player.hasPermission("oglofus.protection.bypass.officer");
    }

    @Override
    public boolean hasMemberAccess(UUID target) {
        if (staff.containsKey(target) && staff.get(target).equals(ProtectionRank.Member)) {
            return true;
        }
        Player player;
        return (player = sponge.server.getPlayer(target).orNull()) != null &&
                player.hasPermission("oglofus.protection.bypass.member");
    }

    @Override
    public ProtectionRank getRank(UUID target) {
        return staff.containsKey(target) ? staff.get(target) : ProtectionRank.None;
    }

    @Override
    public void broadcast(String message) {
        broadcastRaw(MessageType.CHAT, Texts.of(message));
    }

    @Override
    public void broadcast(String message, ProtectionRank rank) {
        broadcastRaw(MessageType.CHAT, Texts.of(message), rank);
    }

    @Override
    public void broadcast(MessageType type, String message) {
        broadcastRaw(type, Texts.of(message));
    }

    @Override
    public void broadcast(MessageType type, String message, ProtectionRank rank) {
        broadcastRaw(type, Texts.of(message), rank);
    }

    @Override
    public void broadcastRaw(Object message) {
        broadcastRaw(MessageType.CHAT, message);
    }

    @Override
    public void broadcastRaw(Object message, ProtectionRank rank) {
        broadcastRaw(MessageType.CHAT, message, rank);
    }

    @Override
    public void broadcastRaw(MessageType type, Object message) {
        if (message instanceof Text) {
            ChatType chatType = ChatTypes.CHAT;
            switch (type) {
                case ACTION_BAR:
                    chatType = ChatTypes.ACTION_BAR;
                    break;
                case CHAT:
                    chatType = ChatTypes.CHAT;
                    break;
                case SYSTEM:
                    chatType = ChatTypes.SYSTEM;
                    break;
            }
            for (Player player : getStaffAs(Player.class)) {
                player.sendMessage(chatType, (Text) message);
            }
        }
    }

    @Override
    public void broadcastRaw(MessageType type, Object message, ProtectionRank rank) {
        if (message instanceof Text) {
            ChatType chatType = ChatTypes.CHAT;
            switch (type) {
                case ACTION_BAR:
                    chatType = ChatTypes.ACTION_BAR;
                    break;
                case CHAT:
                    chatType = ChatTypes.CHAT;
                    break;
                case SYSTEM:
                    chatType = ChatTypes.SYSTEM;
                    break;
            }
            switch (rank) {
                case Member:
                    for (Player player : getMembersAs(Player.class)) {
                        player.sendMessage(chatType, (Text) message);
                    }
                    break;
                case Officer:
                    for (Player player : getOfficersAs(Player.class)) {
                        player.sendMessage(chatType, (Text) message);
                    }
                    break;
                case Owner:
                    Optional<Player> player = getOwnerAs(Player.class);
                    if (player.isPresent()) {
                        player.get().sendMessage(chatType, (Text) message);
                    }
                    break;
            }
        }
    }

    @Override
    public ActionResponse reFlag() {
        //TODO: make it.
        return null;
    }

    @Override
    public ActionResponse invite(Object sender, UUID target) {
        return sponge.getInvitationManager().invite(sender, target, region);
    }

    @Override
    public ActionResponse invite(UUID target) {
        return sponge.getInvitationManager().invite(target, region);
    }

    @Override
    public ActionResponse kick(Object sender, UUID target) {
        if (sender instanceof CommandSource) {
            if (sender instanceof Player) {
                if (hasOwnerAccess(((Player) sender).getUniqueId())) {
                    return kick(target);
                }
                return ActionResponse.Failure.setMessage("access");
            }
            if (((CommandSource) sender).hasPermission("oglofus.protection.bypass.kick")) {
                return kick(target);
            }
            return ActionResponse.Failure.setMessage("access");
        }
        return ActionResponse.Failure.setMessage("object");
    }

    @Override
    public ActionResponse kick(UUID target) {
        //TODO: make it!
        return null;
    }

    @Override
    public ActionResponse promote(Object sender, UUID target) {
        return null;
    }

    @Override
    public ActionResponse promote(UUID target) {
        return null;
    }

    @Override
    public ActionResponse demote(Object sender, UUID target) {
        return null;
    }

    @Override
    public ActionResponse demote(UUID target) {
        return null;
    }

    @Override
    public ActionResponse changeRank(Object sender, UUID target, ProtectionRank rank) {
        return null;
    }

    @Override
    public ActionResponse changeRank(UUID target, ProtectionRank rank) {
        return null;
    }
}
