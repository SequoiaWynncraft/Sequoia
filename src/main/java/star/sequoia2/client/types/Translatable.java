package star.sequoia2.client.types;

import com.google.common.base.CaseFormat;
import net.minecraft.client.resource.language.I18n;

import java.util.Locale;

public interface Translatable {
    String getTypeName();

    default String getTranslation(String keySuffix) {
        return getTranslation(keySuffix, new Object[0]);
    }

    default String getTranslation(String keySuffix, Object... parameters) {
        return I18n.translate(
                "sequoia." + getTypeName().toLowerCase(Locale.ROOT) + "." + getTranslationKeyName() + "." + keySuffix,
                parameters);
    }

    default String getTranslationKeyName() {
        String name = this.getClass().getSimpleName().replace(getTypeName(), "");
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name);
    }

    default String getTranslatedName() {
        return getTranslation("name");
    }
}