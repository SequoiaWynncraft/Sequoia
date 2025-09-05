package star.sequoia2.events.input;

import com.collarmc.pounce.EventInfo;
import com.collarmc.pounce.Preference;

@EventInfo(preference = Preference.CALLER)
public record MouseCursorPosEvent(long window, double x, double y) {}
