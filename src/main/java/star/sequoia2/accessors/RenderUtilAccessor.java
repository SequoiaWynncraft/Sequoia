package star.sequoia2.accessors;

import star.sequoia2.client.NectarClient;
import star.sequoia2.utils.render.Render2DUtil;
import star.sequoia2.utils.render.Render3DUtil;

public interface RenderUtilAccessor {
    default Render2DUtil render2DUtil() {
        return NectarClient.getRender2DUtil();
    }

    default Render3DUtil render3DUtil() {
        return NectarClient.getRender3DUtil();
    }
}
