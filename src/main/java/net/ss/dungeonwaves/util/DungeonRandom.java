package net.ss.dungeonwaves.util;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class DungeonRandom {
    private static Random globalRandom;
    private static long seed = 0L;
    private static final ConcurrentHashMap<String, Random> rngMap = new ConcurrentHashMap<>();

    public static void setSeed(long dungeonSeed) {
        seed = dungeonSeed;
        globalRandom = new Random(seed);
        rngMap.clear();
        Log.d("🔄 Dungeon Seed cập nhật: " + seed);
    }

    public static Random getRandom() {
        if (globalRandom == null) {
            globalRandom = new Random(seed);
        }
        return globalRandom;
    }

    public static Random getRNG(String key) {
        return rngMap.computeIfAbsent(key, k -> new Random(seed + k.hashCode()));
    }

    public static long getSeed() {
        return seed;
    }
}
