package star.sequoia2.accessors;

import star.sequoia2.client.SeqClient;
import star.sequoia2.configuration.Configuration;

public interface ConfigurationAccessor {
    default Configuration configuration() {
        return SeqClient.getConfiguration();
    }
}
