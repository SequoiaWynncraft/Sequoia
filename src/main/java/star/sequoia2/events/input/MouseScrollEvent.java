package star.sequoia2.events.input;

import com.collarmc.pounce.Cancelable;
import com.collarmc.pounce.EventInfo;
import com.collarmc.pounce.Preference;

@EventInfo(preference = Preference.CALLER)
public record MouseScrollEvent(long window, double horizontal, double vertical) implements Cancelable {}
