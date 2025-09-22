package star.sequoia2.gui.categories;

import star.sequoia2.gui.categories.impl.ChangelogCategory;
import star.sequoia2.gui.categories.impl.FeaturesCategory;
import star.sequoia2.gui.categories.impl.PlayerLookupCategory;
import star.sequoia2.gui.categories.impl.SettingsCategory;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

public final class Categories {

    private static final Set<RelativeComponent> CATEGORIES = new LinkedHashSet<>();

    public static Stream<RelativeComponent> all() {
        return CATEGORIES.stream();
    }

    public static void register(RelativeComponent relativeComponent) {
        CATEGORIES.add(relativeComponent);
    }

    public static void registerDefault() {
        register(FEATURES);
        register(CHANGELOG);
        register(LOOKUP);
        register(SETTINGS);
    }

    public static final ChangelogCategory CHANGELOG = new ChangelogCategory();
    public static final FeaturesCategory FEATURES = new FeaturesCategory();
    public static final PlayerLookupCategory LOOKUP = new PlayerLookupCategory();
    public static final SettingsCategory SETTINGS = new SettingsCategory();
}
