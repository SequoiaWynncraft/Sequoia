package star.sequoia2.gui.component.settings;

import star.sequoia2.gui.categories.RelativeComponent;
import star.sequoia2.settings.Setting;
import lombok.Getter;

public abstract class SettingComponent<T> extends RelativeComponent {
    @Getter
    private final Setting<T> setting;

    public SettingComponent(Setting<T> setting) {
        super(setting.name);
        this.setting = setting;
    }
}
