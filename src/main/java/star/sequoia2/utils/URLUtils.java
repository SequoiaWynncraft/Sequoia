package star.sequoia2.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class URLUtils {
    private static final Pattern URL_PATTERN = Pattern.compile("(https?://\\S+)", Pattern.CASE_INSENSITIVE);

    private URLUtils() {}

    public static String sanitize(String url) {
        return url.replace(" ", "%20");
    }

    public static boolean isValidURL(String url) {
        return URL_PATTERN.matcher(url).matches();
    }

    public static Matcher getURLMatcher(String url) {
        return URL_PATTERN.matcher(url);
    }
}