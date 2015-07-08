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

import me.nikosgram.oglofus.protection.api.message.MessageType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Method;

@SuppressWarnings("unused")
public class OglofusUtils {
    private static String nameServer;

    static {
        nameServer = Bukkit.getServer().getClass().getPackage().getName();
        nameServer = nameServer.substring(nameServer.lastIndexOf(".") + 1);
    }

    public static void sendMessage(Player player, String message, MessageType type) {
        try {
            Class<?> craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + nameServer + ".entity.CraftPlayer");
            Object craftPlayer = craftPlayerClass.cast(player);
            Object returned;
            Class<?> packetPlayOut = Class.forName("net.minecraft.server." + nameServer + ".PacketPlayOutChat");
            Class<?> packet = Class.forName("net.minecraft.server." + nameServer + ".Packet");
            if (nameServer.equalsIgnoreCase("v1_8_R1") || !nameServer.startsWith("v1_8_")) {
                Class<?> chatSerializer = Class.forName("net.minecraft.server." + nameServer + ".ChatSerializer");
                Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nameServer + ".IChatBaseComponent");
                Method a = chatSerializer.getDeclaredMethod("a", String.class);
                Object reformedMessage = chatBaseComponent.cast(a.invoke(chatSerializer, "{\"text\": \"" + message + "\"}"));
                returned = packetPlayOut.getConstructor(new Class<?>[]{chatBaseComponent, byte.class}).newInstance(reformedMessage, type.getPosition());
            } else {
                Class<?> chatComponentText = Class.forName("net.minecraft.server." + nameServer + ".ChatComponentText");
                Class<?> chatBaseComponent = Class.forName("net.minecraft.server." + nameServer + ".IChatBaseComponent");
                Object reformedMessage = chatComponentText.getConstructor(new Class<?>[]{String.class}).newInstance(message);
                returned = packetPlayOut.getConstructor(new Class<?>[]{chatBaseComponent, byte.class}).newInstance(reformedMessage, type.getPosition());
            }
            Object handler = craftPlayerClass.getDeclaredMethod("getHandle").invoke(craftPlayer);
            Object playerConnection = handler.getClass().getDeclaredField("playerConnection").get(handler);
            playerConnection.getClass().getDeclaredMethod("sendPacket", packet).invoke(playerConnection, returned);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}