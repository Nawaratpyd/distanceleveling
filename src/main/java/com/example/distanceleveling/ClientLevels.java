package com.example.distanceleveling;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Client-side cache of entityId -> level. Contains no client-only classes, so it is safe to load anywhere. */
public final class ClientLevels {
    private static final Map<Integer, Integer> LEVELS = new ConcurrentHashMap<>();

    private ClientLevels() {
    }

    public static void set(int entityId, int level) {
        LEVELS.put(entityId, level);
    }

    public static int get(int entityId) {
        return LEVELS.getOrDefault(entityId, 0);
    }

    public static void remove(int entityId) {
        LEVELS.remove(entityId);
    }

    public static void clear() {
        LEVELS.clear();
    }
}
