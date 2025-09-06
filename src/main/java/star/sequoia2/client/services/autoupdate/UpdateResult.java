package star.sequoia2.client.services.autoupdate;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public enum UpdateResult {
    SUCCESSFUL(Text.translatable("success").formatted(Formatting.DARK_GREEN)),
    ALREADY_ON_LATEST(Text.translatable("on latest").formatted(Formatting.YELLOW)),
    UPDATE_PENDING(Text.translatable("update pending").formatted(Formatting.YELLOW)),
    ERROR(Text.translatable("error").formatted(Formatting.DARK_RED));

    private final MutableText message;

    private UpdateResult(MutableText message) {
        this.message = message;
    }

    public MutableText getMessage() {
        return this.message;
    }
}
