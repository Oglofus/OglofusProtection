package me.nikosgram.oglofus.protection;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import me.nikosgram.oglofus.protection.api.handler.CancelableHandler;
import me.nikosgram.oglofus.protection.api.handler.Handler;
import me.nikosgram.oglofus.protection.api.handler.HandlerAnnotation;
import me.nikosgram.oglofus.protection.api.handler.HandlerListener;
import me.nikosgram.oglofus.protection.api.manager.HandlerManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class OglofusHandlerManager implements HandlerManager {
    private final List<HandlerListener> listeners = new ArrayList<>();

    @Override
    public void registerHandlers(HandlerListener listener) {
        listeners.add(listener);
    }

    @Override
    public boolean call(Handler handler) {
        for (HandlerListener listener : this) {
            for (Method method : listener.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(HandlerAnnotation.class)) continue;
                if (method.getParameterTypes().length != 1) continue;
                if (method.getParameterTypes()[0] != handler.getClass()) continue;

                method.setAccessible(true);

                try {
                    method.invoke(listener, handler);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return !(handler instanceof CancelableHandler) || !((CancelableHandler) handler).isCanceled();
    }

    @Override
    public Iterator<HandlerListener> iterator() {
        return listeners.iterator();
    }
}