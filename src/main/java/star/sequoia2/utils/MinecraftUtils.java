package star.sequoia2.utils;

import java.util.regex.Pattern;

public final class MinecraftUtils {
    private static final Pattern MINECRAFT_NAME_PATTERN = Pattern.compile("\\w{3,16}");
    private static final Pattern MINECRAFT_NAME_PATTERN_WITH_COLOR = Pattern.compile("§[a-fA-F0-9 ]+\\w{3,16}");
    private static final Pattern NON_MINECRAFT_NAME_PATTERN = Pattern.compile("\\W");

    private MinecraftUtils() {}

    public static boolean isValidUsername(String username) {
        return MINECRAFT_NAME_PATTERN.matcher(username).matches();
    }

    public static boolean isValidUsernameWithColor(String username) {
        return MINECRAFT_NAME_PATTERN_WITH_COLOR.matcher(username).matches();
    }

    public static String cleanUsername(String username) {
        return NON_MINECRAFT_NAME_PATTERN.matcher(username).replaceAll("");
    }
}