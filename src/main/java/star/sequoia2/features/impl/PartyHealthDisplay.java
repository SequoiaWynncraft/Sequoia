package star.sequoia2.features.impl;

import lombok.Getter;
import star.sequoia2.features.ToggleFeature;
import star.sequoia2.settings.types.FloatSetting;

public class PartyHealthDisplay extends ToggleFeature {

    @Getter
    FloatSetting min = settings().number("MinSize", "Minimum healthbar size", 1.0f, 0.1f, 1.0f);
    @Getter
    FloatSetting max = settings().number("MaxSize", "Maximum healthbar size", 1.0f, 1.0f, 5.0f);

    public PartyHealthDisplay() {
        super("PartyHealthDisplay", "displays party members health over their heads");
    }


}
