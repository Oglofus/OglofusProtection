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
import org.apache.commons.lang.ClassUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class OglofusProtectionStaff implements ProtectionStaff {
    private final Map<UUID, ProtectionRank> staff = new HashMap<UUID, ProtectionRank>();
    @Getter
    private final UUID owner;
    private final ProtectionRegion region;
    private final OglofusBukkit bukkit;

    protected OglofusProtectionStaff(ProtectionRegion region, OglofusBukkit bukkit) {
        this.region = region;
        this.bukkit = bukkit;
        owner = UUID.fromString(
                bukkit.connector.getString(
                        "oglofus_regions", "uuid", region.getUuid().toString(), "owner"
                ).get()
        );
        Map<String, String> staff = this.bukkit.connector.getStringMap(
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
            return (Optional<T>) bukkit.getServer().getPlayer(owner);
        } else if (ClassUtils.isAssignable(tClass, OfflinePlayer.class)) {
            return (Optional<T>) bukkit.getServer().getOfflinePlayer(owner);
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
                if ((player = bukkit.getServer().getPlayer(uuid)) != null) {
                    returned.add((T) player);
                }
            }
        } else if (ClassUtils.isAssignable(tClass, OfflinePlayer.class)) {
            for (UUID uuid : getOfficers()) {
                OfflinePlayer player;
                if ((player = bukkit.getServer().getOfflinePlayer(uuid)) != null) {
                    returned.add((T) player);
                }
            }
        }
        return returned;
    }

    @Override
    public Collection<UUID> getOfficers() {
        List<UUID> returned = new ArrayList<UUID>();
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
                if ((player = bukkit.getServer().getPlayer(uuid)) != null) {
                    returned.add((T) player);
                }
            }
        } else if (ClassUtils.isAssignable(tClass, OfflinePlayer.class)) {
            for (UUID uuid : getMembers()) {
                OfflinePlayer player;
                if ((player = bukkit.getServer().getOfflinePlayer(uuid)) != null) {
                    returned.add((T) player);
                }
            }
        }
        return returned;
    }

    @Override
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
                if ((player = bukkit.getServer().getPlayer(uuid)) != null) {
                    returned.add((T) player);
                }
            }
        } else if (ClassUtils.isAssignable(tClass, OfflinePlayer.class)) {
            for (UUID uuid : staff.keySet()) {
                OfflinePlayer player;
                if ((player = bukkit.getServer().getOfflinePlayer(uuid)) != null) {
                    returned.add((T) player);
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
        return (player = bukkit.getServer().getPlayer(target)) != null &&
                player.hasPermission("oglofus.protection.bypass.owner");
    }

    @Override
    public boolean hasOfficerAccess(UUID target) {
        if (staff.containsKey(target) && staff.get(target).equals(ProtectionRank.Officer)) {
            return true;
        }
        Player player;
        return (player = bukkit.getServer().getPlayer(target)) != null &&
                player.hasPermission("oglofus.protection.bypass.officer");
    }

    @Override
    public boolean hasMemberAccess(UUID target) {
        if (staff.containsKey(target) && staff.get(target).equals(ProtectionRank.Member)) {
            return true;
        }
        Player player;
        return (player = bukkit.getServer().getPlayer(target)) != null &&
                player.hasPermission("oglofus.protection.bypass.member");
    }

    @Override
    public ProtectionRank getRank(UUID target) {
        return staff.containsKey(target) ? staff.get(target) : ProtectionRank.None;
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
        for (Player player : getStaffAs(Player.class)) {
            OglofusUtils.sendMessage(player, message, type);
        }
    }

    @Override
    public void broadcast(MessageType type, String message, ProtectionRank rank) {
        switch (rank) {
            case Member:
                for (Player player : getMembersAs(Player.class)) {
                    OglofusUtils.sendMessage(player, message, type);
                }
                break;
            case Officer:
                for (Player player : getOfficersAs(Player.class)) {
                    OglofusUtils.sendMessage(player, message, type);
                }
                break;
            case Owner:
                Optional<Player> player = getOwnerAs(Player.class);
                if (player.isPresent()) {
                    OglofusUtils.sendMessage(player.get(), message, type);
                }
                break;
        }
    }

    @Override
    public void broadcastRaw(Object message) {
        for (Player player : getStaffAs(Player.class)) {
            player.sendRawMessage((String) message);
        }
    }

    @Override
    public void broadcastRaw(Object message, ProtectionRank rank) {
        if (message instanceof String) {
            switch (rank) {
                case Member:
                    for (Player player : getMembersAs(Player.class)) {
                        player.sendRawMessage((String) message);
                    }
                    break;
                case Officer:
                    for (Player player : getOfficersAs(Player.class)) {
                        player.sendRawMessage((String) message);
                    }
                    break;
                case Owner:
                    Optional<Player> player = getOwnerAs(Player.class);
                    if (player.isPresent()) {
                        player.get().sendRawMessage((String) message);
                    }
                    break;
            }
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
        return bukkit.getInvitationManager().invite(sender, target, region);
    }

    @Override
    public ActionResponse invite(UUID target) {
        return bukkit.getInvitationManager().invite(target, region);
    }

    @Override
    public ActionResponse kick(Object sender, UUID target) {
        if (sender instanceof CommandSender) {
            if (sender instanceof Player) {
                if (region.getProtectionStaff().hasOwnerAccess(((Player) sender).getUniqueId())) {
                    //TODO: call the handler PlayerKickHandler.
                    return kick(target);
                }
                return ActionResponse.Failure.setMessage("access");
            }
            if (((CommandSender) sender).hasPermission("oglofus.protection.bypass")) {
                return kick(target);
            }
            return ActionResponse.Failure.setMessage("access");
        }
        return ActionResponse.Failure.setMessage("object");
    }

    @Override
    public ActionResponse kick(UUID target) {
        //TODO: call the handler PlayerKickHandler.
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
