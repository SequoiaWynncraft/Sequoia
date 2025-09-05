package star.sequoia2.features.impl;

import com.collarmc.pounce.Subscribe;
import com.mojang.logging.LogUtils;
import star.sequoia2.accessors.NotificationsAccessor;
import star.sequoia2.client.types.RaidStats;
import star.sequoia2.events.ChatMessageEvent;
import star.sequoia2.http.Http;
import star.sequoia2.features.ToggleFeature;
import star.sequoia2.settings.types.NumberSetting;
import star.sequoia2.utils.Timer;
import net.minecraft.text.Text;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static star.sequoia2.client.NectarClient.mc;

public class MessageAnalyzerFeature extends ToggleFeature implements NotificationsAccessor {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final NumberSetting<Integer> timeWindowSeconds = settings().number("TimeWindow", "time window in which the messages can show up", 15, 3, 20);
    private final NumberSetting<Integer> uploadCooldownSeconds = settings().number("UploadCooldown", "cooldown for uploads", 30, 10, 50);

    private final RaidStats stats = new RaidStats();
    private final Timer lastMessage = new Timer(); // raid complete
    private final Timer lastUpload = new Timer(); //upload limit thang

    private static final Pattern RAID_COMPLETED = Pattern.compile("Raid Completed!", Pattern.CASE_INSENSITIVE);

    private static final Pattern TIME_LINE = Pattern.compile("Time Elapsed: ([0-9]{2}:[0-9]{2})");

    //   "67.0K/69K (24.6%) 2 Reward Pulls"
    //   "1.1M/10M (11%) 5 Aspect Pulls"
    //   "999/1.5K (66.6%) 20 Raid Experience"
    private static final Pattern METRIC_LINE = Pattern.compile(
            "^(?<val>[0-9]+(?:\\.[0-9]+)?[KM]?)\\/[0-9]+(?:\\.[0-9]+)?[KM]? " +
                    "\\((?<pct>[0-9]{1,3}(?:\\.[0-9]+)?)%\\) ?[0-9]+ (?<kind>Reward Pulls|Aspect Pulls|Raid Experience)$",
            Pattern.CASE_INSENSITIVE
    );

    // "theoplegends, notnaag, popbob, and fitmc finished Frozen Spire and claimed 3x Aspects, 8x Emeralds,
    // +5.5k Guild Experience, and +12.3 Seasonal Rating"
    private static final Pattern RAID_SUMMARY = Pattern.compile(
            "^([^:]+), ([^:]+), ([^:]+), and ([^:]+) finished ([^:]+) and claimed [0-9]+x Aspects, [0-9]+x Emeralds, " +
                    "\\+(?<xp>[0-9]+(?:\\.[0-9]+)?[kbm]?) Guild Experience, and \\+(?<rating>[0-9]+(?:\\.[0-9]+)?) Seasonal Rating",
            Pattern.CASE_INSENSITIVE
    );

    public MessageAnalyzerFeature() {
        super("MessageAnalyzer", "analyzes messages");
    }

    @Subscribe
    public void onChatMessageEvent(ChatMessageEvent event) {
        if (mc.world == null || mc.player == null) return;

        Text message = event.message();
        String msg = sanitize(message.getString());

        LOGGER.info("Final Message: [" + msg + "]");

        if (handleRaidCompleted(msg)) {
            return;
        }

        if (lastMessage.passed(timeWindowSeconds.get())) return;

        boolean changed = false;
        changed |= parseMetric(msg);
        changed |= parseTime(msg);
        changed |= parseRaidSummary(msg);

        if (changed) {
            maybeUploadStats();
        }
    }

    private boolean handleRaidCompleted(String msg) {
        Matcher m = RAID_COMPLETED.matcher(msg);
        if (m.find()) {
            LOGGER.info("Raid completed found.");
            lastMessage.reset();
            stats.clearData();
            return true;
        }
        return false;
    }

    private boolean parseMetric(String msg) {
        Matcher m = METRIC_LINE.matcher(msg);
        if (!m.find()) return false;

        float value = parseCompactNumber(m.group("val"));
        float pct   = Float.parseFloat(m.group("pct"));
        String kind = m.group("kind").toLowerCase(Locale.ROOT);

        switch (kind) {
            case "reward pulls": // damage done
                stats.setDamageDone(value);
                stats.setDamageDonePercent(pct);
                LOGGER.info("Parsed damage done: {} ({})", value, pct);
                break;
            case "aspect pulls": // damage taken
                stats.setDamageTaken(value);
                stats.setDamageTakenPercent(pct);
                LOGGER.info("Parsed damage taken: {} ({})", value, pct);
                break;
            case "raid experience": // healing
                stats.setHealing(value);
                stats.setHealingPercent(pct);
                LOGGER.info("Parsed healing: {} ({})", value, pct);
                break;
            default:
                return false;
        }
        return true;
    }

    private boolean parseTime(String msg) {
        Matcher m = TIME_LINE.matcher(msg);
        if (!m.find()) return false;

        int secs = parseTimeToSeconds(m.group(1));
        stats.setTime(secs);
        LOGGER.info("Parsed time: {} seconds", secs);
        return true;
    }

    private boolean parseRaidSummary(String msg) {
        Matcher m = RAID_SUMMARY.matcher(msg);
        if (!m.find()) return false;

        String playerName = mc.player.getName().getString().toLowerCase(Locale.ROOT);

        String g1 = m.group(1).toLowerCase(Locale.ROOT);
        String g2 = m.group(2).toLowerCase(Locale.ROOT);
        String g3 = m.group(3).toLowerCase(Locale.ROOT);
        String g4 = m.group(4).toLowerCase(Locale.ROOT);

        if (!(playerName.equals(g1) || playerName.equals(g2) || playerName.equals(g3) || playerName.equals(g4))) {
            // someone wanted this, the names gonna be wrong tho cuz you can change character name
            return false;
        }

        stats.setRaidName(m.group(5));
        stats.setXp(parseCompactNumber(m.group("xp")));
        stats.setRating(Float.parseFloat(m.group("rating")));
        LOGGER.info("Parsed raid summary for raid '{}'", stats.getRaidName());
        return true;
    }

    private void maybeUploadStats() {
        LOGGER.info(stats.toString());

        if (!stats.isValid()) return;

        if (!lastUpload.passed(uploadCooldownSeconds.get())) return;

        lastUpload.reset();

        LOGGER.info("Uploading stats");
        RaidStats copy = stats.clone();
        Http.sendGuildRaidData(copy);
        stats.clearData();
    }

    private static String sanitize(String s) {
        return s.replaceAll("[^\\x00-\\x7F]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private static float parseCompactNumber(String input) {
        String in = input.trim().toLowerCase(Locale.ROOT);
        try {
            if (in.endsWith("b")) {
                return Float.parseFloat(in.substring(0, in.length() - 1)) * 1_000_000_000f;
            } else if (in.endsWith("m")) {
                return Float.parseFloat(in.substring(0, in.length() - 1)) * 1_000_000f;
            } else if (in.endsWith("k")) {
                return Float.parseFloat(in.substring(0, in.length() - 1)) * 1_000f;
            } else {
                return Float.parseFloat(in);
            }
        } catch (NumberFormatException nfe) {
            LOGGER.warn("Failed to parse compact number: {}", input, nfe);
            return 0f;
        }
    }

    private static int parseTimeToSeconds(String time) {
        String[] parts = time.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid time format, expected MM:SS");
        }
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return minutes * 60 + seconds;
    }
}
