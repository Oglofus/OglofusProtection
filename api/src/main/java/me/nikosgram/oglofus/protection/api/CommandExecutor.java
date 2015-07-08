package me.nikosgram.oglofus.protection.api;

@SuppressWarnings("unused")
public interface CommandExecutor {
    String getName();

    boolean hasPermission(String permission);

    void sendMessage(String... messages);

    void sendMessage(Object... messages);

    boolean executeCommand(String command, String[] parameters);
}
