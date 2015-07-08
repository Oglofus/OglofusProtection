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
import me.nikosgram.oglofus.protection.api.entity.User;
import me.nikosgram.oglofus.protection.api.manager.UserManager;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;
import org.spongepowered.api.entity.player.Player;
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

public class OglofusUserManager implements UserManager {
    private final Map<UUID, User> users = new HashMap<UUID, User>();
    private final OglofusSponge sponge;

    protected OglofusUserManager(OglofusSponge sponge) {
        this.sponge = sponge;
        this.sponge.connector.createTable(
                "oglofus_staff",
                "id int identity(1,1) primary key",
                "region varchar(36)",
                "player varchar(36)",
                "rank varchar(10)"
        );

        //TODO: import the users.
    }

    @Override
    public Optional<User> getUser(String name) {
        for (User user : this) {
            if (user.getName().equals(name)) {
                return Optional.of(user);
            }
        }
        return Optional.absent();
    }

    @Override
    public Optional<User> getUser(UUID uuid) {
        if (users.containsKey(uuid)) {
            return Optional.of(users.get(uuid));
        } else {
            return Optional.absent();
        }
    }

    @Override
    public Collection<User> getOnlineUsers() {
        List<User> returned = new ArrayList<User>();
        for (User user : this) {
            if (user.isOnline()) {
                returned.add(user);
            }
        }
        return returned;
    }

    @Override
    public ActionResponse invite(Object sender, UUID target, ProtectionRegion region) {
        if (sender instanceof CommandSource) {
            if (sender instanceof Player) {
                if (region.getProtectionStaff().hasOwnerAccess(((Player) sender).getUniqueId())) {
                    //TODO: call the handler PlayerInviteHandler.
                    return invite(target, region);
                }
                return ActionResponse.Failure.setMessage("access");
            }
            if (((CommandSource) sender).hasPermission("oglofus.protection.bypass.invite")) {
                return invite(target, region);
            }
            return ActionResponse.Failure.setMessage("access");
        }
        return ActionResponse.Failure.setMessage("object");
    }

    @Override
    public ActionResponse invite(UUID target, ProtectionRegion region) {
        //TODO: make it to invite a player and call the event.
        return null;
    }

    @Override
    public ActionResponse cancel(UUID target, ProtectionRegion region) {
        return null;
    }

    @Override
    public Iterator<User> iterator() {
        return users.values().iterator();
    }

    @Override
    public boolean isProvided() {
        return false;
    }

    @Nullable
    @Override
    public User get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
        String name = arguments.next();
        Optional<User> user = getUser(name);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new ArgumentParseException(String.format("I can't find the User with name '%s'.", name));
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
