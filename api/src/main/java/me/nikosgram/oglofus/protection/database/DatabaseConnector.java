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
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.net.URLCodec;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@SuppressWarnings("unused")
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class DatabaseConnector {
    private static final StringEncoder encoder = new URLCodec();
    private static final StringDecoder decoder = new URLCodec();

    private final DatabaseDriver driver;

    public static String reformedListToString(List<String> list, boolean encode) {
        List<String> returned = new ArrayList<>();
        for (String s : list) {
            returned.add(encode ? encodeString(s) : s.trim());
        }
        String Returned = "StartAT*" + returned.toString() + "*EndAT";
        return Returned.replaceFirst("StartAT\\*\\[", "").replaceFirst("\\]\\*EndAT", "");
    }

    public static String reformedListToString(Collection<String> list, boolean encode) {
        List<String> returned = new ArrayList<>();
        Collections.addAll(returned, list.toArray(new String[list.size()]));
        return reformedListToString(returned, encode);
    }


    public static String reformedListToString(String[] list, boolean encode) {
        List<String> returned = new ArrayList<>();
        Collections.addAll(returned, list);
        return reformedListToString(returned, encode);
    }

    public static String reformedListToString(List<String> list) {
        return reformedListToString(list, true);
    }

    public static String reformedListToString(String[] list) {
        List<String> StringList = new ArrayList<>();
        Collections.addAll(StringList, list);
        return reformedListToString(StringList, true);
    }

    public static String reformedListToString(Collection<String> list) {
        List<String> returned = new ArrayList<>();
        Collections.addAll(returned, list.toArray(new String[list.size()]));
        return reformedListToString(returned, true);
    }

    public static String encodeString(String string) {
        string = string.trim();
        if ((string.startsWith("'")) && (string.endsWith("'"))) {
            try {
                return "'" + encoder.encode(string) + "'";
            } catch (EncoderException e) {
                throw new RuntimeException(e);
            }

        } else {
            try {
                return "'" + encoder.encode(string) + "'";
            } catch (EncoderException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String decodeString(String string) {
        string = string.trim();
        try {
            return decoder.decode(string);
        } catch (DecoderException e) {
            throw new RuntimeException(e);
        }
    }

    public static String reformMessage(String message) {
        if (!message.endsWith(";")) {
            message = message + ";";
        }
        return message;
    }

    public DatabaseConnector openConnection() {
        if (driver != null) {
            driver.openConnection();
        }
        return this;
    }

    public boolean checkConnection() {
        if (driver == null) {
            return false;
        }
        return driver.checkConnection();
    }

    public DatabaseConnector closeConnection() {
        if (checkConnection()) {
            driver.closeConnection();
        }
        return this;
    }

    public Optional<Connection> getConnection() {
        if (driver == null) {
            return Optional.absent();
        }
        return driver.getConnection();
    }

    public Optional<String> getName() {
        if (driver == null) {
            return Optional.absent();
        }
        return Optional.of(driver.getName());
    }

    public DatabaseConnector execute(String message) {
        if (checkConnection()) {
            Optional<Statement> statement = getStatement();
            if (statement.isPresent()) {
                try {
                    statement.get().execute(reformMessage(message));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeStatement(statement.get());
                }
            }
        }
        return this;
    }

    public Optional<Object> getObject(String message, String wanted) {
        Object returned = null;
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    if (result.get().next()) {
                        try {
                            returned = result.get().getObject(wanted);
                        } catch (SQLException ignored) {}
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return Optional.fromNullable(returned);
    }

    public Optional<String> getString(String message, String wanted) {
        String returned = null;
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    if (result.get().next()) {
                        try {
                            returned = result.get().getString(wanted);
                        } catch (SQLException ignored) {}
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return Optional.fromNullable(returned);
    }

    public Optional<Integer> getInteger(String message, String wanted) {
        Integer returned = null;
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    if (result.get().next()) {
                        try {
                            returned = result.get().getInt(wanted);
                        } catch (SQLException ignored) {}
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return Optional.fromNullable(returned);
    }

    public Optional<Double> getDouble(String message, String wanted) {
        Double returned = null;
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    if (result.get().next()) {
                        try {
                            returned = result.get().getDouble(wanted);
                        } catch (SQLException ignored) {}
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return Optional.fromNullable(returned);
    }

    public Optional<Float> getFloat(String message, String wanted) {
        Float returned = null;
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    if (result.get().next()) {
                        try {
                            returned = result.get().getFloat(wanted);
                        } catch (SQLException ignored) {}
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return Optional.fromNullable(returned);
    }

    public Optional<Boolean> getBoolean(String message, String wanted) {
        Boolean returned = null;
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    if (result.get().next()) {
                        try {
                            returned = result.get().getBoolean(wanted);
                        } catch (SQLException ignored) {}
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return Optional.fromNullable(returned);
    }

    public boolean exists(String message) {
        boolean returned = false;
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    returned = result.get().next();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public List<Object> getObjectList(String message, String wanted) {
        List<Object> objects = new ArrayList<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        try {
                            objects.add(result.get().getObject(wanted));
                        } catch (SQLException ignored) {}
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return objects;
    }

    public List<String> getStringList(String message, String wanted) {
        List<String> returned = new ArrayList<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        try {
                            returned.add(result.get().getString(wanted));
                        } catch (SQLException ignored) {}
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public List<Integer> getIntegerList(String message, String wanted) {
        List<Integer> returned = new ArrayList<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        try {
                            returned.add(result.get().getInt(wanted));
                        } catch (SQLException ignored) {}
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public List<Double> getDoubleList(String message, String wanted) {
        List<Double> returned = new ArrayList<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        try {
                            returned.add(result.get().getDouble(wanted));
                        } catch (SQLException ignored) {}
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public List<Float> getFloatList(String message, String wanted) {
        List<Float> returned = new ArrayList<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        try {
                            returned.add(result.get().getFloat(wanted));
                        } catch (SQLException ignored) {}
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public List<Boolean> getBooleanList(String message, String wanted) {
        List<Boolean> returned = new ArrayList<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        try {
                            returned.add(result.get().getBoolean(wanted));
                        } catch (SQLException ignored) {}
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public Map<String, Object> getObjectMap(String message, String[] wanted) {
        Map<String, Object> returned = new HashMap<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        for (String want : wanted) {
                            try {
                                returned.put(want, result.get().getObject(want));
                            } catch (SQLException ignored) {}
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public Map<String, String> getStringMap(String message, String[] wanted) {
        Map<String, String> returned = new HashMap<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        for (String want : wanted) {
                            try {
                                returned.put(want, result.get().getString(want));
                            } catch (SQLException ignored) {}
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public Map<String, Integer> getIntegerMap(String message, String[] wanted) {
        Map<String, Integer> returned = new HashMap<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        for (String want : wanted) {
                            try {
                                returned.put(want, result.get().getInt(want));
                            } catch (SQLException ignored) {}
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public Map<String, Double> getDoubleMap(String message, String[] wanted) {
        Map<String, Double> returned = new HashMap<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        for (String want : wanted) {
                            try {
                                returned.put(want, result.get().getDouble(want));
                            } catch (SQLException ignored) {}
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public Map<String, Float> getFloatMap(String message, String[] wanted) {
        Map<String, Float> returned = new HashMap<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        for (String want : wanted) {
                            try {
                                returned.put(want, result.get().getFloat(want));
                            } catch (SQLException ignored) {}
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public Map<String, Boolean> getBooleanMap(String message, String[] wanted) {
        Map<String, Boolean> returned = new HashMap<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        for (String want : wanted) {
                            try {
                                returned.put(want, result.get().getBoolean(want));
                            } catch (SQLException ignored) {}
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public List<Map<String, Object>> getListOfObjectMap(String message, String[] wanted) {
        List<Map<String, Object>> returned = new ArrayList<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        Map<String, Object> map = new HashMap<>();
                        for (String want : wanted) {
                            try {
                                map.put(want, result.get().getObject(want));
                            } catch (SQLException ignored) {}
                        }
                        returned.add(map);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public List<Map<String, String>> getListOfStringMap(String message, String[] wanted) {
        List<Map<String, String>> returned = new ArrayList<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        Map<String, String> map = new HashMap<>();
                        for (String want : wanted) {
                            try {
                                map.put(want, result.get().getString(want));
                            } catch (SQLException ignored) {}
                        }
                        returned.add(map);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public List<Map<String, Integer>> getListOfIntegerMap(String message, String[] wanted) {
        List<Map<String, Integer>> returned = new ArrayList<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        Map<String, Integer> map = new HashMap<>();
                        for (String want : wanted) {
                            try {
                                map.put(want, result.get().getInt(want));
                            } catch (SQLException ignored) {}
                        }
                        returned.add(map);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public List<Map<String, Double>> getListOfDoubleMap(String message, String[] wanted) {
        List<Map<String, Double>> returned = new ArrayList<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        Map<String, Double> map = new HashMap<>();
                        for (String want : wanted) {
                            try {
                                map.put(want, result.get().getDouble(want));
                            } catch (SQLException ignored) {}
                        }
                        returned.add(map);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public List<Map<String, Float>> getListOfFloatMap(String message, String[] wanted) {
        List<Map<String, Float>> returned = new ArrayList<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        Map<String, Float> map = new HashMap<>();
                        for (String want : wanted) {
                            try {
                                map.put(want, result.get().getFloat(want));
                            } catch (SQLException ignored) {}
                        }
                        returned.add(map);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public List<Map<String, Boolean>> getListOfBooleanMap(String message, String[] wanted) {
        List<Map<String, Boolean>> returned = new ArrayList<>();
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(reformMessage(message));
            if (result.isPresent()) {
                try {
                    while (result.get().next()) {
                        Map<String, Boolean> map = new HashMap<>();
                        for (String want : wanted) {
                            try {
                                map.put(want, result.get().getBoolean(want));
                            } catch (SQLException ignored) {}
                        }
                        returned.add(map);
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public boolean existsInside(String message, String wanted, String type) {
        for (String s : getStringList(message, wanted)) {
            if (s.equals(type)) {
                return true;
            }
        }
        return false;
    }

    public Optional<Statement> getStatement() {
        if (checkConnection()) {
            try {
                return Optional.of(getConnection().get().createStatement());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return Optional.absent();
    }

    public Optional<ResultSet> getResultSet(String message) {
        if (checkConnection()) {
            Optional<Statement> statement = getStatement();
            if (statement.isPresent()) {
                try {
                    return Optional.of(statement.get().executeQuery(message));
                } catch (SQLException e) {
                    closeStatement(statement.get());
                    throw new RuntimeException(e);
                }
            }
        }
        return Optional.absent();
    }

    public int update(String message) {
        if (checkConnection()) {
            Optional<Statement> statement = getStatement();
            if (statement.isPresent()) {
                try {
                    return statement.get().executeUpdate(message);
                } catch (SQLException e) {
                    closeStatement(statement.get());
                    throw new RuntimeException(e);
                }
            }
        }
        return 0;
    }

    public DatabaseConnector closeStatement(Statement statement) {
        try {
            statement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public DatabaseConnector closeResultSet(ResultSet result) {
        try {
            closeStatement(result.getStatement());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            result.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public boolean exists(String table, String where, String like) {
        boolean returned = false;
        if (checkConnection()) {
            Optional<ResultSet> result = getResultSet(
                    reformMessage(
                            "SELECT * FROM " +
                                    table +
                                    " WHERE " +
                                    where +
                                    " LIKE " +
                                    encodeString(like) + " LIMIT 1"
                    )
            );
            if (result.isPresent()) {
                try {
                    returned = result.get().next();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    closeResultSet(result.get());
                }
            }
        }
        return returned;
    }

    public Optional<Object> getObject(String table, String where, String like, String wanted) {
        return getObject("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like) + " LIMIT 1", wanted);
    }

    public Optional<String> getString(String table, String where, String like, String wanted) {
        return getString("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like) + " LIMIT 1", wanted);
    }

    public Optional<Integer> getInteger(String table, String where, String like, String wanted) {
        return getInteger("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like) + " LIMIT 1", wanted);
    }

    public Optional<Double> getDouble(String table, String where, String like, String wanted) {
        return getDouble("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like) + " LIMIT 1", wanted);
    }

    public Optional<Float> getFloat(String table, String where, String like, String wanted) {
        return getFloat("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like) + " LIMIT 1", wanted);
    }

    public Optional<Boolean> getBoolean(String table, String where, String like, String wanted) {
        return getBoolean("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like) + " LIMIT 1", wanted);
    }

    public Map<String, Object> getObjectMap(String table, String where, String like, String[] wanted) {
        return getObjectMap("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like) + " LIMIT 1", wanted);
    }

    public Map<String, String> getStringMap(String table, String where, String like, String[] wanted) {
        return getStringMap("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like) + " LIMIT 1", wanted);
    }

    public Map<String, Integer> getIntegerMap(String table, String where, String like, String[] wanted) {
        return getIntegerMap("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like) + " LIMIT 1", wanted);
    }

    public Map<String, Double> getDoubleMap(String table, String where, String like, String[] wanted) {
        return getDoubleMap("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like) + " LIMIT 1", wanted);
    }

    public Map<String, Float> getFloatMap(String table, String where, String like, String[] wanted) {
        return getFloatMap("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like) + " LIMIT 1", wanted);
    }

    public Map<String, Boolean> getBooleanMap(String table, String where, String like, String[] wanted) {
        return getBooleanMap("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like) + " LIMIT 1", wanted);
    }

    public List<Object> getObjectList(String table, String where, String like, String wanted) {
        return getObjectList("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like), wanted);
    }

    public List<String> getStringList(String table, String where, String like, String wanted) {
        return getStringList("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like), wanted);
    }

    public List<Integer> getIntegerList(String table, String where, String like, String wanted) {
        return getIntegerList("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like), wanted);
    }

    public List<Double> getDoubleList(String table, String where, String like, String wanted) {
        return getDoubleList("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like), wanted);
    }

    public List<Float> getFloatList(String table, String where, String like, String wanted) {
        return getFloatList("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like), wanted);
    }

    public List<Boolean> getBooleanList(String table, String where, String like, String wanted) {
        return getBooleanList("SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString(like), wanted);
    }

    public DatabaseConnector createTable(String table, String... values) {
        execute("CREATE TABLE IF NOT EXISTS " + table + " (" + reformedListToString(values, false) + ")");
        return this;
    }

    public DatabaseConnector insert(String table, Map<String, String> values) {
        execute(
                "INSERT INTO " +
                        table +
                        " (" +
                        reformedListToString(values.keySet(), false) +
                        ") VALUES (" +
                        reformedListToString(values.values()) +
                        ")"
        );
        return this;
    }

    public int update(String table, String where, String like, String change, String to) {
        return update(
                "UPDATE " +
                        table +
                        " SET " +
                        change +
                        " = " +
                        encodeString(to) +
                        " WHERE " +
                        where +
                        " LIKE " +
                        encodeString(like)
        );
    }

    public int update(String table, String where, String like, Map<String, String> values) {
        List<String> settable = new ArrayList<>();
        for (String change : values.keySet()) {
            settable.add(change + "=" + encodeString(values.get(change)));
        }
        return update(
                "UPDATE " +
                        table +
                        " SET " +
                        reformedListToString(settable) +
                        " WHERE " +
                        where +
                        " LIKE " +
                        encodeString(like)
        );
    }
}
