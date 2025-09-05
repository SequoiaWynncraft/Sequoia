package star.sequoia2.client.types;

import net.minecraft.client.gui.hud.MessageIndicator;
import net.minecraft.network.message.MessageSignatureData;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public interface IChatHud {
    void nectar$invokeAddMessage(Text text, @Nullable MessageSignatureData sig, @Nullable MessageIndicator indicator);

    void nectar$remove(@Nullable MessageSignatureData signature);
}
