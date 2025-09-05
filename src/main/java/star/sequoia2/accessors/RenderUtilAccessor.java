package star.sequoia2.accessors;

import star.sequoia2.client.SeqClient;
import star.sequoia2.utils.render.Render2DUtil;
import star.sequoia2.utils.render.Render3DUtil;

public interface RenderUtilAccessor {
    default Render2DUtil render2DUtil() {
        return SeqClient.getRender2DUtil();
    }

    default Render3DUtil render3DUtil() {
        return SeqClient.getRender3DUtil();
    }
}
