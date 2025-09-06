package star.sequoia2.utils.text;

import net.minecraft.client.font.TextRenderer;

import java.util.Locale;

public class TextUtils {
    public static String padInvisible(TextRenderer tr, String s, int targetPx) {
        // todo make it more efficient (for my own sake; it doesn't rlly matter), this is raw chatgpt

        int havePx = tr.getWidth(s);
        int needPx = targetPx - havePx;
        if (needPx <= 0) return s;                 // already long enough

        /* find a combination of 4-px and 5-px blanks that fits exactly */
        int bold = 0, regular = 0;
        for (int b = 0; b <= 3; b++) {             // we never need >3 bold blanks
            int remainder = needPx - 5 * b;
            if (remainder >= 0 && remainder % 4 == 0) {
                bold = b;
                regular = remainder / 4;
                break;
            }
        }

        StringBuilder out = new StringBuilder(s);

        // --- bold blanks (5 px each) ---
        if (bold > 0) {
            out.append("§l").append(" ".repeat(bold)).append("§r");
        }

        // --- normal blanks (4 px each) ---
        if (regular > 0) {
            out.append(" ".repeat(regular));
        }

        return out.toString();
    }


    public static String upperfirst(String text) {
        return text.substring(0,1).toUpperCase(Locale.ROOT) + text.substring(1).toLowerCase(Locale.ROOT);
    }

}
