package star.sequoia2.events;

import com.collarmc.pounce.EventInfo;
import com.collarmc.pounce.Preference;
import net.minecraft.client.util.math.MatrixStack;

@EventInfo(preference = Preference.CALLER)
public record Render3DEvent(MatrixStack matrices, float delta) {}