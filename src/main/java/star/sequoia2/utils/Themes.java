package star.sequoia2.utils;

import star.sequoia2.client.NectarClient;

import java.awt.*;

public final class Themes {
    public enum Theme {
        NOTHING,
        RAINBOW,
        FADE,
        BRIGHT,
    }

    private Color cachedColor;

    private final Timer colorTimer = new Timer();

    public Color getThemeColor(float colorOffset) {
        return getThemeColor(colorOffset, 1, NectarClient.clientModule.get().themeControlEnumSetting.get());
    }

    public Color getThemeColor(float offset, float multiplier, Theme control) {
        Color primary = new Color(NectarClient.clientModule.get().getPrimaryColor());
        Color secondary = new Color(NectarClient.clientModule.get().getSecondaryColor());

        if (colorTimer.passed(2500)) {
            colorTimer.reset();
            cachedColor = primary;
        }

        if (cachedColor == null || control == Theme.NOTHING) return primary;

        offset *= 2;

        final double timer = (System.currentTimeMillis() / 1E+8 * multiplier) * 4E+5;

        switch (control) {
            case FADE -> {
                final double factor = (Math.sin(timer + offset * 0.55f) + 1) * 0.5f;
                return Colors.mixColors(primary, secondary, factor);
            }
            case RAINBOW -> {
                return new Color(Colors.getColor(-(1 + offset * 1.7f), 0.7f, 1));
            }
            case BRIGHT -> {
                final float offset1 = (float) (Math.abs(Math.sin(timer + offset * 0.45)) / 2) + 1;
                return Colors.brighter(primary, offset1);
            }
            default -> {
                return cachedColor;
            }
        }
    }
}
