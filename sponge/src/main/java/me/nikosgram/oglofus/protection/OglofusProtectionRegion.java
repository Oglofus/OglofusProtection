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

import lombok.Getter;
import me.nikosgram.oglofus.protection.api.ActionResponse;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;
import me.nikosgram.oglofus.protection.api.region.ProtectionStaff;
import me.nikosgram.oglofus.protection.api.region.ProtectionVector;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.util.command.CommandSource;

import java.util.UUID;

public class OglofusProtectionRegion implements ProtectionRegion {
    @Getter
    private final UUID uuid;
    private final OglofusSponge sponge;
    @Getter
    private final ProtectionStaff protectionStaff;
    @Getter
    private final ProtectionVector protectionVector;
    @Getter
    private String name;

    protected OglofusProtectionRegion(UUID uuid, OglofusSponge sponge) {
        this.uuid = uuid;
        this.sponge = sponge;
        name = sponge.connector.getString(
                "oglofus_regions", "uuid", uuid.toString(), "name"
        ).get();
        protectionStaff = new OglofusProtectionStaff(this, sponge);
        protectionVector = new OglofusProtectionVector(uuid, sponge);
    }

    @Override
    public ActionResponse changeName(String name) {
        if (name.length() > 36) {
            return ActionResponse.Failure.setMessage("length");
        }
        if (sponge.connector.exists("oglofus_regions", "name", name)) {
            return ActionResponse.Failure.setMessage("exists");
        }
        this.name = name;
        sponge.connector.update("oglofus_regions", "uuid", uuid.toString(), "name", name);
        return ActionResponse.Successful.setMessage(name);
    }

    @Override
    public ActionResponse changeName(Object sender, String name) {
        if (sender instanceof CommandSource) {
            if (sender instanceof Player) {
                if (getProtectionStaff().hasOwnerAccess(((Player) sender).getUniqueId())) {
                    return changeName(name);
                }
                return ActionResponse.Failure.setMessage("access");
            }
            if (((CommandSource) sender).hasPermission("oglofus.protection.bypass")) {
                return changeName(name);
            }
            return ActionResponse.Failure.setMessage("access");
        }
        return ActionResponse.Failure.setMessage("object");
    }
}
