package star.sequoia2.client.types;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RaidStats{

    private String raidName;
    private Float damageDone;
    private Float damageDonePercent;
    private Float damageTaken;
    private Float damageTakenPercent;
    private Float healing;
    private Float healingPercent;
    private Integer time;
    private Float xp;
    private Float rating;

    public RaidStats(RaidStats other) {
        this.raidName = other.raidName;
        this.damageDone = other.damageDone;
        this.damageDonePercent = other.damageDonePercent;
        this.damageTaken = other.damageTaken;
        this.damageTakenPercent = other.damageTakenPercent;
        this.healing = other.healing;
        this.healingPercent = other.healingPercent;
        this.time = other.time;
        this.xp = other.xp;
        this.rating = other.rating;
    }

    @Override
    public RaidStats clone()  {
        return new RaidStats(this);
    }


    public RaidStats(String raidName, float damageDone, float damageDonePercent, float damageTaken,
                     float damageTakenPercent, float healing, float healingPercent, int time, float xp,
                     float rating) {
        this.raidName = raidName;
        this.damageDone = damageDone;
        this.damageDonePercent = damageDonePercent;
        this.damageTaken = damageTaken;
        this.damageTakenPercent = damageTakenPercent;
        this.healing = healing;
        this.healingPercent = healingPercent;
        this.time = time;
        this.xp = xp;
        this.rating = rating;
    }

    public RaidStats() {}

    public void clearData() {
        raidName = null;
        damageDone = null;
        damageDonePercent = null;
        damageTaken = null;
        damageTakenPercent = null;
        healing = null;
        healingPercent = null;
        time = null;
        xp = null;
        rating = null;
    }

    public boolean isValid() {
        return raidName != null && damageDone != null && damageDonePercent != null
                && damageTaken != null && damageTakenPercent != null && healing != null
                && healingPercent != null && time != null && xp != null && rating != null;

    }

    public String toString() {
        return "raidName: " + raidName +
                " damageDone: " + damageDone +
                " damageDonePercent: " + damageDonePercent +
                " damageTaken: " + damageTaken +
                " damageTakenPercent: " + damageTakenPercent +
                " healing: " + healing +
                " healingPercent: " + healingPercent +
                " time: " + time +
                " xp: " + xp +
                " rating: " + rating;
    }

}