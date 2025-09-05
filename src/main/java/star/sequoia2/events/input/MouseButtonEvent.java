package star.sequoia2.events.input;

import com.collarmc.pounce.Cancelable;
import com.collarmc.pounce.EventInfo;
import com.collarmc.pounce.Preference;

@EventInfo(preference = Preference.CALLER)
public record MouseButtonEvent(long window, int button, int action, int modifiers) implements Cancelable {}
