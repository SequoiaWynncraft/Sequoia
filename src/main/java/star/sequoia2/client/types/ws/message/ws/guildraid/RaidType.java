package star.sequoia2.client.types.ws.message.ws.guildraid;

import org.apache.commons.lang3.StringUtils;

public enum RaidType {
    NEST_OF_THE_GROOTSLANGS(0, "Nest of the Grootslangs"),
    NEXUS_OF_LIGHT(1, "Orphion's Nexus of Light"),
    THE_CANYON_COLOSSUS(2, "The Canyon Colossus"),
    THE_NAMELESS_ANOMALY(3, "The Nameless Anomaly");

    private final int id;
    private final String displayName;

    RaidType(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public static RaidType getRaidType(String name) {
        for (RaidType raidType : values()) {
            if (StringUtils.equals(raidType.getDisplayName(), name)) {
                return raidType;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }
}