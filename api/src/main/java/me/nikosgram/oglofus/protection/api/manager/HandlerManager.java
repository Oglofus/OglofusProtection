package me.nikosgram.oglofus.protection.api.manager;

import me.nikosgram.oglofus.protection.api.handler.Handler;
import me.nikosgram.oglofus.protection.api.handler.HandlerListener;

@SuppressWarnings("unused")
public interface HandlerManager extends Iterable<HandlerListener> {
    void registerHandlers(HandlerListener listener);

    boolean call(Handler handler);
}
