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

package me.nikosgram.oglofus.protection.api.region;

import com.google.common.base.Optional;
import me.nikosgram.oglofus.protection.api.ActionResponse;
import me.nikosgram.oglofus.protection.api.message.MessageType;

import java.util.Collection;
import java.util.UUID;

@SuppressWarnings("unused")
public interface ProtectionStaff {
    /**
     * Get the region's owner.
     *
     * @param tClass get owner as who?
     * @param <T>    the owner as who.
     * @return the owner.
     */
    <T> Optional<T> getOwnerAs(Class<T> tClass);

    /**
     * Get the region's owner.
     *
     * @return the owner's uuid.
     */
    UUID getOwner();

    /**
     * Get the officers.
     *
     * @param tClass get officers as who?
     * @param <T>    the officers as who.
     * @return the members.
     */
    <T> Collection<T> getOfficersAs(Class<T> tClass);

    /**
     * Get the officers ids.
     *
     * @return the members as {@link UUID}.
     */
    Collection<UUID> getOfficers();

    /**
     * Get the members.
     *
     * @param tClass get members as who?
     * @param <T>    the members as who.
     * @return the members.
     */
    <T> Collection<T> getMembersAs(Class<T> tClass);

    /**
     * Get the members ids.
     *
     * @return the members as {@link UUID}.
     */
    Collection<UUID> getMembers();

    /**
     * Get the staff.
     *
     * @return the staff.
     */
    <T> Collection<T> getStaffAs(Class<T> tClass);

    /**
     * Get the staff ids.
     *
     * @return staff as {@link UUID}.
     */
    Collection<UUID> getStaff();

    /**
     * Check if a player is owner.
     *
     * @param target the {@link UUID}
     * @return true if the player is owner
     */
    boolean isOwner(UUID target);

    /**
     * Check if a player is officer.
     *
     * @param target the {@link UUID}
     * @return true if the player is officer
     */
    boolean isOfficer(UUID target);

    /**
     * Check if a player is owner.
     *
     * @param target the {@link UUID}
     * @return true if the player is member
     */
    boolean isMember(UUID target);

    /**
     * Check if a player is staff.
     *
     * @param target the {@link UUID}
     * @return true if the player is staff
     */
    boolean isStaff(UUID target);

    /**
     * Check if a player has owner access.
     *
     * @param target the {@link UUID}
     * @return true if the player has owner access
     */
    boolean hasOwnerAccess(UUID target);

    /**
     * Check if a player has officer access.
     *
     * @param target the {@link UUID}
     * @return true if the player has officer access
     */
    boolean hasOfficerAccess(UUID target);

    /**
     * Check if a player has member access.
     *
     * @param target the {@link UUID}
     * @return true if the player has member access
     */
    boolean hasMemberAccess(UUID target);

    /**
     * Get the rank from player
     *
     * @param target the {@link UUID}
     * @return the rank
     */
    ProtectionRank getRank(UUID target);

    /**
     * Broadcast, to protection area's members, a message
     *
     * @param message the message
     */
    void broadcast(String message);

    /**
     * Broadcast, to protection area's members with rank, a message
     *
     * @param message the message
     * @param rank    who you want to display the message
     */
    void broadcast(String message, ProtectionRank rank);

    /**
     * Broadcast, to protection area's members, a message
     *
     * @param type    where you want to show the message
     * @param message the message
     */
    void broadcast(MessageType type, String message);

    /**
     * Broadcast, to protection area's members with rank, a message
     *
     * @param type    where you want to show the message
     * @param message the message
     * @param rank    who you want to display the message
     */
    void broadcast(MessageType type, String message, ProtectionRank rank);

    /**
     * Broadcast, to protection area's members, a message
     *
     * @param message the message as raw
     */
    void broadcastRaw(Object message);

    /**
     * Broadcast, to protection area's members with rank, a message
     *
     * @param message the message as raw
     * @param rank    who you want to display the message
     */
    void broadcastRaw(Object message, ProtectionRank rank);

    /**
     * Broadcast, to protection area's members, a message
     *
     * @param type    where you want to show the message
     * @param message the message as raw
     */
    void broadcastRaw(MessageType type, Object message);

    /**
     * Broadcast, to protection area's members with rank, a message
     *
     * @param type    where you want to show the message
     * @param message the message as raw
     * @param rank    who you want to display the message
     */
    void broadcastRaw(MessageType type, Object message, ProtectionRank rank);

    /**
     * Reflag this region.
     *
     * @return the response.
     */
    ActionResponse reFlag();

    /**
     * Invite a player to join at this area
     *
     * @param sender who want to invite the player
     * @param target the player
     * @return the response.
     */
    ActionResponse invite(Object sender, UUID target);

    /**
     * Invite a player to join at this area
     *
     * @param target the player
     * @return the response.
     */
    ActionResponse invite(UUID target);

    /**
     * Kick a player to join at this area
     *
     * @param sender who want to kick the player
     * @param target the player
     * @return the response.
     */
    ActionResponse kick(Object sender, UUID target);

    /**
     * Kick a player to join at this area
     *
     * @param target the player
     * @return the response.
     */
    ActionResponse kick(UUID target);

    /**
     * Promote a Member to Officer in this region.
     *
     * @param sender who want to promote the player
     * @param target the player
     * @return the response.
     */
    ActionResponse promote(Object sender, UUID target);

    /**
     * Promote a member to Officer in this region.
     *
     * @param target the player
     * @return the response.
     */
    ActionResponse promote(UUID target);

    /**
     * Demote a Officer to Member in this region.
     *
     * @param sender who want to demote the player
     * @param target the player
     * @return the response.
     */
    ActionResponse demote(Object sender, UUID target);

    /**
     * Demote a Officer to Member in this region.
     *
     * @param target the player
     * @return the response.
     */
    ActionResponse demote(UUID target);

    /**
     * Change the rank from a player in this region.
     *
     * @param sender who want to change the rank from the player
     * @param target the player
     * @param rank   the rank
     * @return the response.
     */
    ActionResponse changeRank(Object sender, UUID target, ProtectionRank rank);

    /**
     * Change the rank from a player in this region.
     *
     * @param target the player
     * @param rank   the rank
     * @return the response.
     */
    ActionResponse changeRank(UUID target, ProtectionRank rank);
}
