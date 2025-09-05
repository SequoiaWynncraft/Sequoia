package star.sequoia2.events;

import com.collarmc.pounce.EventInfo;
import com.collarmc.pounce.Preference;
import net.minecraft.client.gui.DrawContext;

@EventInfo(preference = Preference.CALLER)
public record Render2DEvent(DrawContext context, float delta) {
}
