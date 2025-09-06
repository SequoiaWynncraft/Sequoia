package star.sequoia2.client.types;

import java.util.List;

public abstract class Service extends CoreComponent {
    protected Service(List<Service> dependencies) {
        // dependencies are technically not used, but only required
        // as a reminder for implementers to be wary about dependencies

        // A manager is responsible for never accessing another manager except
        // those listed in the dependencies, due to bootstrapping ordering
    }

    @Override
    public String getTypeName() {
        return "Service";
    }
}