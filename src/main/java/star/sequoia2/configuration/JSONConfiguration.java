package star.sequoia2.configuration;

public interface JSONConfiguration {
    JsonCompound toJSON();

    void fromJSON(JsonCompound compound);
}
