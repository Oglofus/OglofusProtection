package me.nikosgram.oglofus.protection;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ProtectionUtils {
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    public static String getFile(Path path) {
        String returned = "";
        BufferedReader reader = null;
        try {
            String line;
            reader = new BufferedReader(new FileReader(path.toFile()));
            while ((line = reader.readLine()) != null) {
                returned += line + "\n";
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null)
                    reader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return returned;
    }

    public static <T> List<T> page(int page, int size, List<T> list) {
        List<T> returned = new ArrayList<T>();
        if ((size * page) > list.size()) {
            return returned;
        }
        int end = (size * page) + size;
        if ((size * page) + size > list.size()) {
            end = list.size();
        }
        for (int i = (size * page); i < end; i++) {
            returned.add(list.get(i));
        }
        return returned;
    }

    public static <T> T newInstance(Class<T> tClass, Object... objects) {
        try {
            return tClass.getConstructor().newInstance(objects);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static String capitalizeMessage(String message) {
        return message.substring(0, 1) + message.substring(1).toLowerCase();
    }
}
