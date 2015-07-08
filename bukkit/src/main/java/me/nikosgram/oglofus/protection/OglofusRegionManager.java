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
import me.nikosgram.oglofus.protection.api.manager.RegionManager;
import me.nikosgram.oglofus.protection.api.region.ProtectionLocation;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;
import me.nikosgram.oglofus.protection.api.region.ProtectionVector;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OglofusRegionManager implements RegionManager {
    private final OglofusBukkit bukkit;
    private final Map<UUID, ProtectionRegion> map = new HashMap<>();

    protected OglofusRegionManager(OglofusBukkit bukkit) {
        this.bukkit = bukkit;
        bukkit.connector.createTable(
                "oglofus_regions",
                "id int identity(1,1) primary key",
                "uuid varchar(36)",
                "name varchar(32)",
                "owner varchar(36)",
                "created date"
        );
        bukkit.connector.createTable(
                "oglofus_vectors",
                "id int identity(1,1) primary key",
                "uuid varchar(36)",
                "radius tinyint",
                "x int",
                "y int",
                "z int",
                "world varchar(36)"
        );

        for (String uid : bukkit.connector.getStringList("select uuid from oglofus_regions", "uuid")) {
            UUID uuid = UUID.fromString(uid);
            map.put(uuid, new OglofusProtectionRegion(uuid, bukkit));
        }
    }

    @Override
    public Optional<ProtectionRegion> getRegion(UUID target) {
        if (map.containsKey(target)) {
            return Optional.of(map.get(target));
        }
        return Optional.absent();
    }

    @Override
    public Optional<ProtectionRegion> getRegion(String target) {
        String uid;
        if ((uid = bukkit.connector.getString("oglofus_regions", "name", target, "uuid").orNull()) != null) {
            UUID uuid = UUID.fromString(uid);
            if (map.containsKey(uuid)) {
                return Optional.of(map.get(uuid));
            }
        }
        return Optional.absent();
    }

    @Override
    public Optional<ProtectionRegion> getRegion(ProtectionLocation location) {
        String uid;
        if ((uid = bukkit.connector.getString(
                "select uuid from oglofus_vectors where x=" +
                        location.getX() +
                        " and y=" +
                        location.getY() +
                        " and z=" +
                        location.getZ(), "uuid"
        ).orNull()) != null) {
            UUID uuid = UUID.fromString(uid);
            if (map.containsKey(uuid)) {
                return Optional.of(map.get(uuid));
            }
        }
        for (ProtectionRegion region : this) {
            ProtectionVector vector = region.getProtectionVector();
            if (!location.getWorld().equals(vector.getBlockLocation().getWorld())) {
                continue;
            }
            if (Math.abs(vector.getBlockLocation().getX() - location.getX()) <= vector.getRadius()) {
                if (Math.abs(vector.getBlockLocation().getY() - location.getY()) <= vector.getRadius()) {
                    if (Math.abs(vector.getBlockLocation().getZ() - location.getZ()) <= vector.getRadius()) {
                        return Optional.of(region);
                    }
                }
            }
        }
        return Optional.absent();
    }

    @Override
    public ActionResponse createProtectionRegion(ProtectionLocation location, UUID owner) {
        //TODO create a new protection region!
        return null;
    }

    @Override
    public ActionResponse deleteProtectionRegion(ProtectionRegion area) {
        //TODO delete a protection region!
        return null;
    }

    @Override
    public Iterator<ProtectionRegion> iterator() {
        return map.values().iterator();
    }

    @Override
    public boolean isProvided() {
        return false;
    }

    @Nullable
    @Override
    public ProtectionRegion get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
        String name = arguments.next();
        Optional<ProtectionRegion> region = getRegion(name);
        if (region.isPresent()) {
            return region.get();
        } else {
            throw new ArgumentParseException(String.format("I can't find the Region with name '%s'.", name));
        }
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        List<String> returned = new ArrayList<String>();
        for (ProtectionRegion region : this) {
            if (region.getName().startsWith(prefix)) {
                returned.add(region.getName());
            }
        }
        return returned;
    }
}
