package star.sequoia2.settings;

import org.apache.commons.lang3.StringUtils;

public interface CommandSupport extends Named {

    default String commandName() {
        return StringUtils.replace(this.name(), " ", "").toLowerCase();
    }

    String toPrintableValue();

    void parseValueFromCommand(String value);
}
