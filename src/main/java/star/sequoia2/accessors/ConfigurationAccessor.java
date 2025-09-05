package star.sequoia2.accessors;

import star.sequoia2.client.NectarClient;
import star.sequoia2.configuration.Configuration;

public interface ConfigurationAccessor {
    default Configuration configuration() {
        return NectarClient.getConfiguration();
    }
}
