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

package me.nikosgram.oglofus.protection.handler;

import com.google.common.base.Optional;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import me.nikosgram.oglofus.protection.api.handler.ProtectionCreateHandler;
import me.nikosgram.oglofus.protection.api.region.ProtectionVector;
import org.bukkit.Location;

public class WorldGuardHandler implements ProtectionCreateHandler {
    public boolean hasRegion(Location loc) {
        return WorldGuardPlugin.inst().getRegionManager(loc.getWorld()).getApplicableRegions(loc).size() > 0;
    }

    @Override
    public boolean onProtectionCreate(ProtectionVector vector, Optional<Object> sender) {
        for (Location location : vector.getBlocks(Location.class)) {
            if (hasRegion(location)) {
                return false;
            }
        }
        return true;
    }
}
