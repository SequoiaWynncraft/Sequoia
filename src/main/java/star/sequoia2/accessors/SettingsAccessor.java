package star.sequoia2.accessors;


import star.sequoia2.client.NectarClient;
import star.sequoia2.settings.SettingsState;

public interface SettingsAccessor {
    default SettingsState settingsState() {
        return NectarClient.getSettings();
    }
}
