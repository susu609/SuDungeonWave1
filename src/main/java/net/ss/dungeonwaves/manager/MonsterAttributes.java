package net.ss.dungeonwaves.manager;

import java.util.HashMap;
import java.util.Map;

public record MonsterAttributes(int summonCost, int minWave) {
    private static final Map<MonsterType, MonsterAttributes> ATTRIBUTES = new HashMap<>();

    static {
        ATTRIBUTES.put(MonsterType.ZOMBIE, new MonsterAttributes(10, 0));
        ATTRIBUTES.put(MonsterType.SKELETON, new MonsterAttributes(15, 3));
        ATTRIBUTES.put(MonsterType.CREEPER, new MonsterAttributes(25, 5));
        ATTRIBUTES.put(MonsterType.SPIDER, new MonsterAttributes(20, 7));
    }

    public static MonsterAttributes get(MonsterType type) {
        return ATTRIBUTES.getOrDefault(type, new MonsterAttributes(10, 1));
    }
}
