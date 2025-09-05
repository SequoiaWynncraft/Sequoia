package star.sequoia2.accessors;

import star.sequoia2.client.NectarClient;
import star.sequoia2.features.Feature;
import star.sequoia2.features.Features;

import java.util.function.Consumer;

public interface FeaturesAccessor {
    default Features features() {
        return NectarClient.getFeatures();
    }

    default <T extends Feature> T feature(Class<T> clazz) {
        return NectarClient.getFeatures().get(clazz)
                .orElseThrow(() -> new IllegalStateException("Feature \"" + clazz + "\" is not a feature or is not registered"));
    }

    default <T extends Feature> void featureIfPresent(Class<T> clazz, Consumer<T> consumer) {
        NectarClient.getFeatures().get(clazz).ifPresent(consumer);
    }

    default <T extends Feature> void featureIfActive(Class<T> clazz, Consumer<T> consumer) {
        NectarClient.getFeatures().getIfActive(clazz).ifPresent(consumer);
    }
}
