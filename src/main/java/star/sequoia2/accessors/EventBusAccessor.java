package star.sequoia2.accessors;

import com.collarmc.pounce.CancelableCallback;
import com.collarmc.pounce.EventBus;
import star.sequoia2.client.NectarClient;

public interface EventBusAccessor {
    default EventBus events() {
        return NectarClient.getEventBus();
    }

    default void dispatch(Object o) {
        events().dispatch(o);
    }

    default void dispatch(Object event, CancelableCallback callback) {
        events().dispatch(event, callback);
    }

    default void subscribe(Object listener) {
        events().subscribeStrongly(listener);
    }

    default void unsubscribe(Object listener) {
        events().unsubscribe(listener);
    }
}
