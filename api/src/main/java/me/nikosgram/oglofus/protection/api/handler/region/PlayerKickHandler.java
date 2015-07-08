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

package me.nikosgram.oglofus.protection.api.handler.region;

import com.google.common.base.Optional;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.nikosgram.oglofus.protection.api.CommandExecutor;
import me.nikosgram.oglofus.protection.api.entity.User;
import me.nikosgram.oglofus.protection.api.handler.CancelableHandler;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;

@ToString
@SuppressWarnings("unused")
public class PlayerKickHandler extends RegionHandler implements CancelableHandler {
    @Getter
    private final User user;
    @Getter
    private final Optional<CommandExecutor> sender;
    @Getter
    @Setter
    private boolean canceled = false;

    public PlayerKickHandler(ProtectionRegion region, User user, CommandExecutor executor) {
        super(region);
        this.user = user;
        this.sender = Optional.fromNullable(executor);
    }
}
