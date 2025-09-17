package star.sequoia2.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.wynntils.core.components.Handlers;
import com.wynntils.core.components.Models;
import com.wynntils.core.components.Services;
import com.wynntils.models.territories.profile.TerritoryProfile;
import com.wynntils.screens.maps.AbstractMapScreen;
import com.wynntils.services.map.MapTexture;
import com.wynntils.services.map.pois.Poi;
import com.wynntils.services.map.pois.TerritoryPoi;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.mc.KeyboardUtils;
import com.wynntils.utils.mc.McUtils;
import com.wynntils.utils.render.MapRenderer;
import com.wynntils.utils.render.RenderUtils;
import com.wynntils.utils.type.BoundingBox;
import com.wynntils.utils.type.CappedValue;
import mil.nga.color.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.accessors.TextRendererAccessor;
import star.sequoia2.client.SeqClient;
import star.sequoia2.utils.render.TextureStorage;

import java.util.*;

import static com.wynntils.models.territories.type.GuildResource.*;

public class BetterGuildMapScreen extends AbstractMapScreen implements RenderUtilAccessor, TextRendererAccessor {
    enum Roles { TANK, DPS, HEAL }
    enum UiState { OPTED_OUT, CHOOSING_ROLE, CHOSEN_ROLE }

    CappedValue ore;
    CappedValue fish;
    CappedValue wood;
    CappedValue crop;

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

        drawRoleUi(context, mouseX, mouseY);
        drawHqResourceUI(context, mouseX, mouseY);
    }

    void drawHqResourceUI(DrawContext ctx, int mouseX, int mouseY) {
        float baseX = renderX + renderedBorderXOffset + mapWidth - PAD;
        float baseY = renderY + renderedBorderYOffset + PAD;

        float iconH = 20f * SCALE;
        float iconW = iconH * 2.5f;
        float gap = 6f * SCALE;

        int lh = textRenderer().fontHeight;
        float labelPad = lh + 2f;

        float panelW = PAD + iconW + gap + iconW + PAD;
        float panelH = PAD + labelPad + iconH + gap + labelPad + iconH + PAD;

        render2DUtil().roundRectFilled(ctx.getMatrices(), baseX - panelW, baseY, baseX, baseY + panelH, 5f, Color.darkGray());

        float x1 = baseX - panelW + PAD;
        float x2 = x1 + iconW + gap;
        float y1 = baseY + PAD + labelPad;
        float y2 = y1 + iconH + gap + labelPad;

        render2DUtil().drawText(ctx, "Ore",  (int)(x1 + iconW / 2f - textRenderer().getWidth("Ore")  / 2f),  (int)(y1 - lh - 2), 0xFFFFFF, true);
        drawResourceMeter(ctx, x1, y1, iconW, iconH, valueRatio(ore), TextureStorage.ore_empty, TextureStorage.ore_full);

        render2DUtil().drawText(ctx, "Fish", (int)(x2 + iconW / 2f - textRenderer().getWidth("Fish") / 2f), (int)(y1 - lh - 2), 0xFFFFFF, true);
        drawResourceMeter(ctx, x2, y1, iconW, iconH, valueRatio(fish), TextureStorage.fish_empty, TextureStorage.fish_full);

        render2DUtil().drawText(ctx, "Wood", (int)(x1 + iconW / 2f - textRenderer().getWidth("Wood") / 2f), (int)(y2 - lh - 2), 0xFFFFFF, true);
        drawResourceMeter(ctx, x1, y2, iconW, iconH, valueRatio(wood), TextureStorage.wood_empty, TextureStorage.wood_full);

        render2DUtil().drawText(ctx, "Crops",(int)(x2 + iconW / 2f - textRenderer().getWidth("Crops")/ 2f), (int)(y2 - lh - 2), 0xFFFFFF, true);
        drawResourceMeter(ctx, x2, y2, iconW, iconH, valueRatio(crop), TextureStorage.crop_empty, TextureStorage.crop_full);
    }

    void drawResourceMeter(DrawContext ctx, float x, float y, float w, float h, double ratio, Identifier emptyTex, Identifier fullTex) {
        render2DUtil().drawTexture(ctx, emptyTex, x, y, x + w, y + h);
        int sx = (int) x;
        int sy = (int) y;
        int sw = (int) (w * Math.max(0d, Math.min(1d, ratio)));
        int sh = (int) h;
        if (sw > 0 && sh > 0) {
            RenderUtils.enableScissor(ctx, sx, sy, sw, sh);
            render2DUtil().drawTexture(ctx, fullTex, x, y, x + w, y + h);
            RenderUtils.disableScissor(ctx);
        }
    }

    double valueRatio(CappedValue v) {
        if (v == null) return 0d;
        double max = v.max();
        double val = v.current();
        if (max <= 0d) return 0d;
        return val / max;
    }

    void drawRoleUi(DrawContext ctx, int mouseX, int mouseY) {
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
        if (button != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT
                    && KeyboardUtils.isShiftDown()
                    && hovered instanceof TerritoryPoi territoryPoi) {
                Handlers.Command.queueCommand("gu territory " + territoryPoi.getName());
            }
            return super.doMouseClicked(mouseX, mouseY, button);
        }

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
                if (poi.getTerritoryInfo().isHeadquarters() && poi.getTerritoryInfo().getGuildName().equals("Sequoia")) {
                    ore = poi.getTerritoryInfo().getStorage(ORE);
                    fish = poi.getTerritoryInfo().getStorage(FISH);
                    wood = poi.getTerritoryInfo().getStorage(WOOD);
                    crop = poi.getTerritoryInfo().getStorage(CROPS);
                }

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

    @Override
    protected void renderPois(
            List<Poi> pois,
            MatrixStack matrixStack,
            BoundingBox textureBoundingBox,
            float poiScale,
            int mouseX,
            int mouseY) {
        hovered = null;

        List<Poi> filteredPois = getRenderedPois(pois, textureBoundingBox, poiScale, mouseX, mouseY);

        // Render trading routes
        // We render them in both directions because optimizing it is not cheap either
        for (Poi poi : filteredPois) {
            if (!(poi instanceof TerritoryPoi territoryPoi)) continue;

            float poiRenderX = MapRenderer.getRenderX(poi, mapCenterX, centerX, zoomRenderScale);
            float poiRenderZ = MapRenderer.getRenderZ(poi, mapCenterZ, centerZ, zoomRenderScale);

            for (String tradingRoute : territoryPoi.getTerritoryInfo().getTradingRoutes()) {
                Optional<Poi> routePoi = filteredPois.stream()
                        .filter(filteredPoi -> filteredPoi.getName().equals(tradingRoute))
                        .findFirst();

                // Only render connection if the other poi is also in the filtered pois
                if (routePoi.isPresent() && filteredPois.contains(routePoi.get())) {
                    float x = MapRenderer.getRenderX(routePoi.get(), mapCenterX, centerX, zoomRenderScale);
                    float z = MapRenderer.getRenderZ(routePoi.get(), mapCenterZ, centerZ, zoomRenderScale);

                    RenderUtils.drawLine(matrixStack, CommonColors.DARK_GRAY, poiRenderX, poiRenderZ, x, z, 0, 1);
                }
            }
        }

        VertexConsumerProvider.Immediate bufferSource = McUtils.mc().getBufferBuilders().getEntityVertexConsumers();

        // Reverse and Render
        for (int i = filteredPois.size() - 1; i >= 0; i--) {
            Poi poi = filteredPois.get(i);

            float poiRenderX = MapRenderer.getRenderX(poi, mapCenterX, centerX, zoomRenderScale);
            float poiRenderZ = MapRenderer.getRenderZ(poi, mapCenterZ, centerZ, zoomRenderScale);

            poi.renderAt(
                    matrixStack,
                    bufferSource,
                    poiRenderX,
                    poiRenderZ,
                    hovered == poi,
                    poiScale,
                    zoomRenderScale,
                    zoomLevel,
                    true);
        }

        bufferSource.drawCurrentLayer();
    }
}
