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
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.annotation.Nullable;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public class OglofusUserManager implements UserManager, Listener {
    private final Map<UUID, User> users = new HashMap<UUID, User>();
    private final OglofusBukkit bukkit;
    private final Path dataFolder;

    protected OglofusUserManager(OglofusBukkit bukkit) {
        this.bukkit = bukkit;

        dataFolder = Paths.get(bukkit.getDataFolder().toString(), "users");

        if (!Files.exists(dataFolder)) {
            try {
                Files.createDirectories(dataFolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            users.put(player.getUniqueId(), new OglofusUser(bukkit, player, dataFolder));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        if (!users.containsKey(uuid)) {
            users.put(uuid, new OglofusUser(bukkit, event.getPlayer(), dataFolder));
        }
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
        if (sender instanceof CommandSender) {
            if (sender instanceof Player) {
                if (region.getProtectionStaff().hasOwnerAccess(((Player) sender).getUniqueId())) {
                    //TODO: call the handler PlayerInviteHandler.
                    return invite(target, region);
                }
                return ActionResponse.Failure.setMessage("access");
            }
            if (((CommandSender) sender).hasPermission("oglofus.protection.bypass")) {
                return invite(target, region);
            }
            return ActionResponse.Failure.setMessage("access");
        }
        return ActionResponse.Failure.setMessage("object");
    }

    @Override
    public ActionResponse invite(UUID target, ProtectionRegion region) {
        //TODO: call the handler PlayerInviteHandler.
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
