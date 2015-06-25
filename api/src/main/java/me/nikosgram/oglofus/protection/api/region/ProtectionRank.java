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

public enum ProtectionRank {
    /**
     * The owner has access to destroy the {@link ProtectionRegion}
     */
    Owner,
    /**
     * The officer has access to kick and invite players in {@link ProtectionRegion}
     */
    Officer,
    /**
     * The member has access to build inside from the {@link ProtectionRegion}
     */
    Member,
    /**
     * The none hasn't access to the {@link ProtectionRegion}
     */
    None;
}
