package star.sequoia2.gui.categories.impl;

import com.mojang.logging.LogUtils;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.accessors.TextRendererAccessor;
import star.sequoia2.features.impl.Settings;
import star.sequoia2.gui.categories.RelativeComponent;
import star.sequoia2.gui.screen.GuiRoot;
import mil.nga.color.Color;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import org.slf4j.Logger;

import java.util.List;

public class ChangelogCategory extends RelativeComponent implements RenderUtilAccessor, TextRendererAccessor, FeaturesAccessor {
    private static final Logger LOGGER = LogUtils.getLogger();

    public ChangelogCategory() {
        super("Changelog");
    }

    @Override
    public void render(DrawContext context, float mouseX, float mouseY, float delta) {
        float left = contentX();
        float top = contentY();
        float right = left + contentWidth();
        float bottom = top + contentHeight();

        GuiRoot root = getGuiRoot();

        Color normal = features().get(Settings.class).map(Settings::getThemeNormal).orElse(Color.black());
        Color dark = features().get(Settings.class).map(Settings::getThemeDark).orElse(Color.black());
        Color light = features().get(Settings.class).map(Settings::getThemeLight).orElse(Color.black());
        Color accent1 = features().get(Settings.class).map(Settings::getThemeAccent1).orElse(Color.black());
        Color accent2 = features().get(Settings.class).map(Settings::getThemeAccent2).orElse(Color.black());
        Color accent3 = features().get(Settings.class).map(Settings::getThemeAccent3).orElse(Color.black());

        String commitsStr = FabricLoader.getInstance().getModContainer("seq")
                .map(c -> c.getMetadata().getCustomValue("commit_list").getAsString())
                .orElse("");
        List<String> commits = commitsStr.isEmpty() ? List.of() : List.of(commitsStr.split("\\R"));

        String authorStr = FabricLoader.getInstance().getModContainer("seq")
                .map(c -> c.getMetadata().getCustomValue("commit_authors").getAsString())
                .orElse("");
        List<String> authors = authorStr.isEmpty() ? List.of() : List.of(authorStr.split("\\R"));

        String commitHash = FabricLoader.getInstance().getModContainer("seq")
                .map(c -> c.getMetadata().getVersion().toString())
                .orElse("");

        String commitDate = FabricLoader.getInstance().getModContainer("seq")
                .map(c -> c.getMetadata().getCustomValue("commit_date").getAsString())
                .orElse("");

        List<String> date = commitDate.isEmpty() ? List.of() : List.of(commitDate.split("\\R"));

        render2DUtil().roundGradientFilled(context.getMatrices(), left, top, right, bottom, 8, normal, dark, true);

        float x = left + root.pad;
        float y = top + root.pad + 5;

        renderText(context, "seq §f+ " + commitHash, x, y, accent1.getColor(), true);
        y = y + textRenderer().fontHeight;
        if (!date.isEmpty()) {
            renderText(context, "§8" + date.getFirst(), x, y, light.getColor(), true);
        }

        y = y + 15;

        renderText(context, "Changelog:", x, y, accent2.getColor(), true);

        y = y + 10;
        for (int i = 0; i < commits.size(); i++) {
            context.enableScissor((int) left, (int) top, (int) (right), (int) (bottom));
            String commit_message = commits.get(i);
            String author_message = authors.size() > i ? authors.get(i) : "";
            renderText(context, "*" + commit_message + "§3 -" + author_message, x, y, light.getColor(), true);
            y += textRenderer().fontHeight + root.btnGap;
            context.disableScissor();
        }
    }
}
