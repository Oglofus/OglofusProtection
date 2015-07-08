package me.nikosgram.oglofus.protection.api.handler;

public interface CancelableHandler {
    boolean isCanceled();

    void setCanceled(boolean canceled);
}
