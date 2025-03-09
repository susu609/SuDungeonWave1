package net.ss.dungeonwaves.manager;

public enum MonsterType {
    ZOMBIE("minecraft:zombie"),
    SKELETON("minecraft:skeleton"),
    CREEPER("minecraft:creeper"),
    SPIDER("minecraft:spider");

    private final String stringId;

    MonsterType(String stringId) {
        this.stringId = stringId;
    }

    public String getStringId() {
        return stringId;
    }

    public static MonsterType fromString(String id) {
        for (MonsterType type : values()) {
            if (type.stringId.equals(id)) {
                return type;
            }
        }
        return null;
    }

    public MonsterAttributes getAttributes() {
        return MonsterAttributes.get(this);
    }

}
