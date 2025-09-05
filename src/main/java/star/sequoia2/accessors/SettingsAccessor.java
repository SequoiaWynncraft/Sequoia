package star.sequoia2.accessors;


import star.sequoia2.client.SeqClient;
import star.sequoia2.settings.SettingsState;

public interface SettingsAccessor {
    default SettingsState settingsState() {
        return SeqClient.getSettings();
    }
}
