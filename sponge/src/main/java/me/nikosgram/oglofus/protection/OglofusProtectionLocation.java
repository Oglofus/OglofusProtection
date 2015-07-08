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

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.nikosgram.oglofus.protection.api.region.ProtectionLocation;
import org.apache.commons.lang3.ClassUtils;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.UUID;

@ToString
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class OglofusProtectionLocation implements ProtectionLocation {
    private final OglofusSponge sponge;
    @Getter
    private final UUID world;
    @Getter
    private int x;
    @Getter
    private int y;
    @Getter
    private int z;

    protected OglofusProtectionLocation(OglofusSponge sponge, Location location) {
        this.sponge = sponge;
        this.world = ((World) location.getExtent()).getUniqueId();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }

    protected OglofusProtectionLocation(OglofusSponge sponge, ProtectionLocation location) {
        this.sponge = sponge;
        this.world = location.getWorld();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    @Override
    public String getWorldName() {
        return getWorldAs(World.class).get().getName();
    }

    @Override
    public ProtectionLocation add(int x, int y, int z) {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getWorldAs(Class<T> tClass) {
        if (ClassUtils.isAssignable(tClass, World.class)) {
            return Optional.of((T) sponge.server.getWorld(world));
        } else if (ClassUtils.isAssignable(tClass, Extent.class)) {
            getLocationAs(Location.class).get().getExtent();
        }
        return Optional.absent();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> getLocationAs(Class<T> tClass) {
        Location location = new Location(getWorldAs(World.class).get(), x, y, z);
        if (ClassUtils.isAssignable(tClass, Location.class)) {
            return Optional.of((T) location);
        } else if (ClassUtils.isAssignable(tClass, BlockState.class)) {
            return Optional.of((T) location.getBlock());
        } else if (ClassUtils.isAssignable(tClass, Vector3d.class)) {
            return Optional.of((T) location.getPosition());
        }
        return Optional.absent();
    }
}
