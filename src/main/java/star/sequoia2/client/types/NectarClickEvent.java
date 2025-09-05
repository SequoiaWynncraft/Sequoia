package star.sequoia2.client.types;

import net.minecraft.text.ClickEvent;

public class NectarClickEvent extends ClickEvent {
    public final String value;

    public NectarClickEvent(Action action, String value) {
        super(action, value);
        this.value = value;
    }

    @Override
    public Action getAction() {
        return Action.RUN_COMMAND;
    }
}
