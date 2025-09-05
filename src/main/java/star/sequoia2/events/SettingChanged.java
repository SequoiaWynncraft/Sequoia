package star.sequoia2.events;

import star.sequoia2.settings.Setting;

public record SettingChanged(Setting<?> setting) {}
