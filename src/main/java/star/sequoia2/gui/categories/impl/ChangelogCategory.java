package star.sequoia2.gui.categories.impl;

import com.mojang.logging.LogUtils;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.accessors.TextRendererAccessor;
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

        render2DUtil().roundRectFilled(context.getMatrices(), left, top, right, bottom, 8, new Color(50, 50, 50));

        float x = left + root.pad;
        float y = top + root.pad + 5;

        renderText(context, "§9seq §f+ " + commitHash, x, y, Color.white().getColor(), true);
        y = y + textRenderer().fontHeight;
        renderText(context, "§8" + date.getFirst(), x, y, Color.white().getColor(), true);

        y = y + 15;

        renderText(context, "Changelog:", x, y, Color.white().getColor(), true);

        y = y + 10;
        for (int i = 0; i < commits.size(); i++) { //todo update when more commits
            context.enableScissor((int) left, (int) top, (int) (right), (int) (bottom));
            String commit_message =  commits.get(i);
            String author_message =  authors.get(i);
            renderText(context, "*" + commit_message + "§3 -" + author_message, x, y, Color.white().getColor(), true);
            y += textRenderer().fontHeight + root.btnGap;
            context.disableScissor();
        }


        float mx = localMouseX(mouseX);
        float my = localMouseY(mouseY);
    }

    @Override
    public void mouseMoved(float mouseX, float mouseY) {

    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {

    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int button) {

    }

    @Override
    public void mouseScrolled(float mouseX, float mouseY, double horizontalAmount, double verticalAmount) {

    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public void keyReleased(int keyCode, int scanCode, int modifiers) {

    }

    @Override
    public void charTyped(char chr, int modifiers) {

    }
}
