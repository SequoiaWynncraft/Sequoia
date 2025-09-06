package star.sequoia2.utils;

import java.util.HashMap;
import java.util.Map;

public final class Themes {
    public enum ThemeEnum {
        NEXUS(Themes.NexusTheme),
        OLUX(Themes.OluxTheme),
        MOLTEN(Themes.MoltenTheme),
        SEAVALE(Themes.SeavaleTheme);

        private Theme theme = NexusTheme;

        ThemeEnum(Theme theme) {
            this.theme = theme;
        }

        public Theme getTheme() {
            return theme;
        }
    }

    public static Map<String, Theme> ThemesMap = new HashMap<>();

    public static Theme NexusTheme = new Theme(
            "Nexus of Light",
            0xf3e6ff,
            0xa64dff,
            0x6600cc,
            0xffc34d,
            0x66e0ff,
            0xff3399
    );
    public static Theme OluxTheme = new Theme(
            "Olux Swamp",
            0x80dfff,
            0x00e6ac,
            0x267326,
            0x73e600,
            0xa366ff,
            0xff3399
    );
    public static Theme MoltenTheme = new Theme(
            "Molten Heights",
            0xffedfc,
            0xea3481,
            0xa90f50,
            0xffc34d,
            0x66e0ff,
            0xfa795f
    );
    public static Theme SeavaleTheme = new Theme(
            "Seavale Reef",
            0xedf9ff,
            0xa0deff,
            0x3945ff,
            0x690ac8,
            0xff35aa,
            0x73e304
    );
}
