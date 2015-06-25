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

import java.util.Collection;

@SuppressWarnings("unused")
public interface ProtectionVector {
    /**
     * Get the region's block location.
     *
     * @return the block location {@link ProtectionLocation}.
     */
    ProtectionLocation getBlockLocation();

    /**
     * Get the min location from region.
     *
     * @return the min location {@link ProtectionLocation}.
     */
    ProtectionLocation getMinLocation();

    /**
     * Get the max location from region.
     *
     * @return the max location {@link ProtectionLocation}.
     */
    ProtectionLocation getMaxLocation();

    /**
     * Get all the blocks inside the region.
     *
     * @return the blocks.
     */
    <T> Collection<T> getBlocks(Class<T> tClass);

    /**
     * Get all the entities inside the region.
     *
     * @return the entities
     */
    <T> Collection<T> getEntities(Class<T> tClass);

    /**
     * Get the region's radius.
     *
     * @return the radius
     */
    int getRadius();
}
