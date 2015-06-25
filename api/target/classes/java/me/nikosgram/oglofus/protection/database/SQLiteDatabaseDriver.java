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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class SQLiteDatabaseDriver implements DatabaseDriver {
    private final Path database;
    @Getter
    private final String name = "SQLite";
    private Connection connection = null;

    @Override
    public DatabaseDriver openConnection() {
        try {
            Class.forName("org.sqlite.JDBC");

            if (Files.notExists(database.getParent())) {
                try {
                    Files.createDirectories(database.getParent());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            if (Files.notExists(database)) {
                try {
                    Files.createFile(database);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            connection = DriverManager.getConnection(String.format("jdbc:sqlite:%s", database));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public Boolean checkConnection() {
        if (connection == null) {
            return false;
        }
        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void closeConnection() {
        if (checkConnection()) {
            try {
                connection.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Optional<Connection> getConnection() {
        return Optional.fromNullable(connection);
    }
}
