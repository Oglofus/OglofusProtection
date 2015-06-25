/*
 * Copyright (c) 2015 Nikos Grammatikos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.nikosgram.oglofus.protection.database;

import com.google.common.base.Optional;

import java.sql.Connection;

public interface DatabaseDriver {
    DatabaseDriver openConnection();

    Boolean checkConnection();

    void closeConnection();

    Optional<Connection> getConnection();

    String getName();
}
