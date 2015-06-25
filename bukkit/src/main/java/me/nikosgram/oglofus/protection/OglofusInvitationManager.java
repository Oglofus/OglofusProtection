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

import me.nikosgram.oglofus.protection.api.ActionResponse;
import me.nikosgram.oglofus.protection.api.manager.InvitationManager;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OglofusInvitationManager implements InvitationManager {
    private final OglofusBukkit bukkit;

    protected OglofusInvitationManager(OglofusBukkit bukkit) {
        this.bukkit = bukkit;
        this.bukkit.connector.createTable(
                "oglofus_staff",
                "id int identity(1,1) primary key",
                "uuid varchar(36)",
                "player varchar(36)",
                "rank varchar(10)"
        );
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
}
