package star.sequoia2.accessors;

import star.sequoia2.client.SeqClient;
import star.sequoia2.features.Feature;
import star.sequoia2.features.Features;

import java.util.function.Consumer;

public interface FeaturesAccessor {
    default Features features() {
        return SeqClient.getFeatures();
    }

    default <T extends Feature> T feature(Class<T> clazz) {
        return SeqClient.getFeatures().get(clazz)
                .orElseThrow(() -> new IllegalStateException("Feature \"" + clazz + "\" is not a feature or is not registered"));
    }

    default <T extends Feature> void featureIfPresent(Class<T> clazz, Consumer<T> consumer) {
        SeqClient.getFeatures().get(clazz).ifPresent(consumer);
    }

    default <T extends Feature> void featureIfActive(Class<T> clazz, Consumer<T> consumer) {
        SeqClient.getFeatures().getIfActive(clazz).ifPresent(consumer);
    }
}
