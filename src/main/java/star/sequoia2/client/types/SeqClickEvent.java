package star.sequoia2.client.types;

import net.minecraft.text.ClickEvent;

public class SeqClickEvent extends ClickEvent {
    public final String value;

    public SeqClickEvent(Action action, String value) {
        super(action, value);
        this.value = value;
    }

    @Override
    public Action getAction() {
        return Action.RUN_COMMAND;
    }
}
