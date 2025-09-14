package star.sequoia2.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wynntils.core.components.Models;
import com.wynntils.core.components.Services;
import com.wynntils.models.territories.profile.TerritoryProfile;
import com.wynntils.screens.maps.AbstractMapScreen;
import com.wynntils.services.map.MapTexture;
import com.wynntils.services.map.pois.Poi;
import com.wynntils.services.map.pois.TerritoryPoi;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.render.MapRenderer;
import com.wynntils.utils.render.RenderUtils;
import com.wynntils.utils.type.BoundingBox;
import mil.nga.color.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.utils.render.TextureStorage;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

public class BetterGuildMapScreen extends AbstractMapScreen implements RenderUtilAccessor {
    enum Roles { TANK, DPS, HEAL }
    enum UiState { OPTED_OUT, CHOOSING_ROLE, CHOSEN_ROLE }

    static final float SCALE = 1f;
    static final float PAD = 6f * SCALE;
    static final float BTN_SIZE = 46f * SCALE;
    static final float BTN_GAP = 8f * SCALE;
    static final float BAR_HEIGHT = BTN_SIZE + PAD * 2f;
    static final float BAR_CORNER = 6f * SCALE;

    static final float OPT_BTN_SIZE = 42f * SCALE;
    static final float OPT_CORNER = 5f * SCALE;

    static final float OPT_OUT_SIZE = 20f * SCALE;

    static class Rect {
        final float x, y, w, h;
        Rect(float x, float y, float w, float h) { this.x = x; this.y = y; this.w = w; this.h = h; }
        boolean contains(double mx, double my) { return mx >= x && mx <= x + w && my >= y && my <= y + h; }
    }

    static class TexturePair {
        final Identifier active, inactive;
        TexturePair(Identifier active, Identifier inactive) { this.active = active; this.inactive = inactive; }
    }

    final EnumMap<Roles, TexturePair> roleTextures = new EnumMap<>(Roles.class);

    UiState state = UiState.OPTED_OUT;
    Roles selectedRole = null;

    public BetterGuildMapScreen() {
        roleTextures.put(Roles.TANK, new TexturePair(TextureStorage.tank_active, TextureStorage.tank_inactive));
        roleTextures.put(Roles.DPS, new TexturePair(TextureStorage.damage_active, TextureStorage.damage_inactive));
        roleTextures.put(Roles.HEAL, new TexturePair(TextureStorage.healer_active, TextureStorage.healer_inactive));
    }

    @Override
    public void doRender(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);

        RenderSystem.enableDepthTest();
        renderMap(context);

        RenderUtils.enableScissor(
                context,
                (int) (renderX + renderedBorderXOffset),
                (int) (renderY + renderedBorderYOffset),
                (int) mapWidth,
                (int) mapHeight);

        renderPois(context.getMatrices(), mouseX, mouseY);
        RenderUtils.disableScissor(context);

        drawUi(context, mouseX, mouseY);
    }

    void drawUi(DrawContext ctx, int mouseX, int mouseY) {
        float baseX = renderX + renderedBorderXOffset;
        float baseY = renderY + renderedBorderYOffset;

        if (state == UiState.OPTED_OUT) {
            Rect r = new Rect(baseX + PAD, baseY + PAD, OPT_BTN_SIZE, OPT_BTN_SIZE);
            render2DUtil().roundRectFilled(ctx.getMatrices(), r.x, r.y, r.x + r.w, r.y + r.h, OPT_CORNER, Color.darkGray());
            render2DUtil().drawTexture(ctx, TextureStorage.opt_in, r.x + 4, r.y + 4, r.x + r.w - 4, r.y + r.h - 4);
            return;
        }

        float barWidth = PAD + (BTN_SIZE * 3f) + (BTN_GAP * 2f) + PAD;
        Rect bar = new Rect(baseX, baseY + PAD, barWidth, BAR_HEIGHT);
        render2DUtil().roundRectFilled(ctx.getMatrices(), bar.x, bar.y, bar.x + bar.w, bar.y + bar.h, BAR_CORNER, Color.darkGray());

        float bx = bar.x + PAD;
        Rect tankRect = new Rect(bx, bar.y + PAD, BTN_SIZE, BTN_SIZE);
        Rect dpsRect = new Rect(bx + BTN_SIZE + BTN_GAP, bar.y + PAD, BTN_SIZE, BTN_SIZE);
        Rect healRect = new Rect(bx + (BTN_SIZE + BTN_GAP) * 2f, bar.y + PAD, BTN_SIZE, BTN_SIZE);

        drawRoleButton(ctx, tankRect, Roles.TANK);
        drawRoleButton(ctx, dpsRect, Roles.DPS);
        drawRoleButton(ctx, healRect, Roles.HEAL);

        if (state != UiState.OPTED_OUT) {
            Rect out = new Rect(healRect.x + healRect.w - 1 + 4, bar.y - 4, OPT_OUT_SIZE, OPT_OUT_SIZE);
            render2DUtil().roundRectFilled(ctx.getMatrices(), out.x, out.y, out.x + out.w, out.y + out.h, 2f, Color.gray());
            render2DUtil().drawTexture(ctx, TextureStorage.opt_out, out.x + 1, out.y + 1, out.x + out.w - 1, out.y + out.h - 1);
        }
    }

    void drawRoleButton(DrawContext ctx, Rect r, Roles role) {
        boolean active = Objects.equals(selectedRole, role);
        TexturePair tp = roleTextures.get(role);
        render2DUtil().drawTexture(ctx, active ? tp.active : tp.inactive, r.x, r.y, r.x + r.w, r.y + r.h);
    }

    @Override
    public boolean doMouseClicked(double mouseX, double mouseY, int button) {
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) return super.doMouseClicked(mouseX, mouseY, button);

        float baseX = renderX + renderedBorderXOffset;
        float baseY = renderY + renderedBorderYOffset;

        if (state == UiState.OPTED_OUT) {
            Rect optIn = new Rect(baseX + PAD, baseY + PAD, OPT_BTN_SIZE, OPT_BTN_SIZE);
            if (optIn.contains(mouseX, mouseY)) {
                state = UiState.CHOOSING_ROLE;
                selectedRole = null;
                return true;
            }
            return super.doMouseClicked(mouseX, mouseY, button);
        }

        float barWidth = PAD + (BTN_SIZE * 3f) + (BTN_GAP * 2f) + PAD;
        Rect bar = new Rect(baseX, baseY + PAD, barWidth, BAR_HEIGHT);

        Rect tankRect = new Rect(bar.x + PAD, bar.y + PAD, BTN_SIZE, BTN_SIZE);
        Rect dpsRect = new Rect(bar.x + PAD + BTN_SIZE + BTN_GAP, bar.y + PAD, BTN_SIZE, BTN_SIZE);
        Rect healRect = new Rect(bar.x + PAD + (BTN_SIZE + BTN_GAP) * 2f, bar.y + PAD, BTN_SIZE, BTN_SIZE);

        if (tankRect.contains(mouseX, mouseY)) { selectedRole = Roles.TANK; state = UiState.CHOSEN_ROLE; return true; }
        if (dpsRect.contains(mouseX, mouseY)) { selectedRole = Roles.DPS; state = UiState.CHOSEN_ROLE; return true; }
        if (healRect.contains(mouseX, mouseY)) { selectedRole = Roles.HEAL; state = UiState.CHOSEN_ROLE; return true; }

        if (state != UiState.OPTED_OUT) {
            Rect out = new Rect(healRect.x + healRect.w - 1 + 4, bar.y - 4, OPT_OUT_SIZE, OPT_OUT_SIZE);
            if (out.contains(mouseX, mouseY)) { state = UiState.OPTED_OUT; selectedRole = null; return true; }
        }

        return super.doMouseClicked(mouseX, mouseY, button);
    }

    protected void renderMap(DrawContext context) {
        MatrixStack poseStack = context.getMatrices();

        RenderUtils.enableScissor(
                context,
                (int) (renderX + renderedBorderXOffset),
                (int) (renderY + renderedBorderYOffset),
                (int) mapWidth,
                (int) mapHeight);

        RenderUtils.drawRect(
                poseStack,
                CommonColors.BLACK,
                renderX + renderedBorderXOffset,
                renderY + renderedBorderYOffset,
                0,
                mapWidth,
                mapHeight);

        BoundingBox textureBoundingBox = BoundingBox.centered(mapCenterX, mapCenterZ, width / zoomRenderScale, height / zoomRenderScale);
        List<MapTexture> maps = Services.Map.getMapsForBoundingBox(textureBoundingBox);

        for (MapTexture map : maps) {
            float textureX = map.getTextureXPosition(mapCenterX);
            float textureZ = map.getTextureZPosition(mapCenterZ);

            MapRenderer.renderMapQuad(
                    map,
                    poseStack,
                    BUFFER_SOURCE,
                    centerX,
                    centerZ,
                    textureX,
                    textureZ,
                    mapWidth,
                    mapHeight,
                    1f / zoomRenderScale);
        }

        BUFFER_SOURCE.draw();
        RenderUtils.disableScissor(context);
    }

    private void renderPois(MatrixStack matrixStack, int mouseX, int mouseY) {
        List<TerritoryPoi> advancementPois = Models.Territory.getTerritoryPoisFromAdvancement();
        List<Poi> renderedPois = new ArrayList<>();

        for (TerritoryPoi poi : advancementPois) {
            TerritoryProfile territoryProfile = Models.Territory.getTerritoryProfile(poi.getName());
            if (territoryProfile != null && territoryProfile.getGuild().equals(poi.getTerritoryInfo().getGuildName())) {
                renderedPois.add(poi);
            } else {
                renderedPois.add(new TerritoryPoi(territoryProfile, poi.getTerritoryInfo()));
            }
        }

        Models.Marker.USER_WAYPOINTS_PROVIDER.getPois().forEach(renderedPois::add);

        renderPois(
                renderedPois,
                matrixStack,
                BoundingBox.centered(mapCenterX, mapCenterZ, width / zoomRenderScale, height / zoomRenderScale),
                1,
                mouseX,
                mouseY);
    }
}
