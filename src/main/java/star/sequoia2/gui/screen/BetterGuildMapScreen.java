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
import star.sequoia2.utils.render.TextureStorage;

import java.util.*;

import static com.wynntils.models.territories.type.GuildResource.*;
import static star.sequoia2.client.SeqClient.mc;

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

    static final float CMD_BTN_SIZE = 42f * SCALE;
    static final float CMD_CORNER = 5f * SCALE;

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
    boolean commandActive = false;

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

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        MatrixStack m = context.getMatrices();
        m.push();
        m.translate(0, 0, 300);

        drawRoleUI(context, mouseX, mouseY);
        drawCommandUI(context, mouseX, mouseY);
        drawHqResourceUI(context, mouseX, mouseY);

        m.pop();
        RenderSystem.enableDepthTest();
    }

    void fillRoundedGradient(DrawContext ctx, Rect r, float radius, Color start, Color end, boolean sideways) {
        render2DUtil().roundGradientFilled(ctx.getMatrices(), r.x, r.y, r.x + r.w, r.y + r.h, radius, start, end, sideways);
    }

    void drawCommandUI(DrawContext ctx, int mouseX, int mouseY) {
        float baseX = renderX + renderedBorderXOffset + mapWidth / 2f;
        float baseY = renderY + renderedBorderYOffset + PAD;
        Rect r = new Rect(baseX - CMD_BTN_SIZE / 2f, baseY, CMD_BTN_SIZE, CMD_BTN_SIZE);
        boolean hover = r.contains(mouseX, mouseY);
        Color s = commandActive ? new Color(70, 110, 255, 220) : new Color(48, 48, 54, 220);
        Color e = commandActive ? new Color(40, 70, 220, 220)  : new Color(26, 26, 32, 220);
        if (hover) {
            s = new Color(Math.min(255, s.getRed() + 20), Math.min(255, s.getGreen() + 20), Math.min(255, s.getBlue() + 20), s.getAlpha());
            e = new Color(Math.min(255, e.getRed() + 20), Math.min(255, e.getGreen() + 20), Math.min(255, e.getBlue() + 20), e.getAlpha());
            render2DUtil().drawGlow(ctx, r.x, r.y, r.x + r.w, r.y + r.h, new Color(90, 120, 255, 140), CMD_CORNER);
        }
        fillRoundedGradient(ctx, r, CMD_CORNER, s, e, true);
        Identifier tex = commandActive ? TextureStorage.command_active : TextureStorage.command_inactive;
        render2DUtil().drawTexture(ctx, tex, r.x + 4, r.y + 4, r.x + r.w - 4, r.y + r.h - 4);
    }

    void drawHqResourceUI(DrawContext ctx, int mouseX, int mouseY) {
        float baseX = renderX + renderedBorderXOffset + mapWidth - PAD;
        float baseY = renderY + renderedBorderYOffset + PAD;

        float iconH = 20f * SCALE;
        float defaultIconW = iconH * 2.5f;
        float gap = 6f * SCALE;

        int lh = textRenderer().fontHeight;
        float namePad = lh + 2f;
        float valuePad = lh;

        String oreVal = valueText(ore);
        String fishVal = valueText(fish);
        String woodVal = valueText(wood);
        String cropVal = valueText(crop);

        int maxTextW = Math.max(
                Math.max(textRenderer().getWidth("Ore"), textRenderer().getWidth("Wood")),
                Math.max(textRenderer().getWidth("Fish"), textRenderer().getWidth("Crops")));
        maxTextW = Math.max(maxTextW, Math.max(
                Math.max(textRenderer().getWidth(oreVal), textRenderer().getWidth(woodVal)),
                Math.max(textRenderer().getWidth(fishVal), textRenderer().getWidth(cropVal))));

        float iconW = Math.max(defaultIconW, maxTextW);

        float panelW = PAD + iconW + gap + iconW + PAD;
        float panelH = PAD + namePad + valuePad + iconH + gap + namePad + valuePad + iconH + PAD;

        Rect panel = new Rect(baseX - panelW, baseY, panelW, panelH);
        boolean hover = panel.contains(mouseX, mouseY);
        Color s = new Color(40, 40, 46, 220);
        Color e = new Color(22, 22, 28, 220);
        if (hover) {
            s = new Color(50, 50, 58, 230);
            e = new Color(28, 28, 36, 230);
            render2DUtil().drawGlow(ctx, panel.x, panel.y, panel.x + panel.w, panel.y + panel.h, new Color(80, 110, 255, 90), 6f);
        }
        fillRoundedGradient(ctx, panel, 5f, s, e, false);

        float x1 = panel.x + PAD;
        float x2 = x1 + iconW + gap;
        float y1 = panel.y + PAD + namePad + valuePad;
        float y2 = y1 + iconH + gap + namePad + valuePad;

        render2DUtil().drawText(ctx, "Ore",  (int)(x1 + iconW / 2f - textRenderer().getWidth("Ore")  / 2f),  (int)(y1 - valuePad - lh - 2), 0xFFFFFF, true);
        render2DUtil().drawText(ctx, oreVal, (int)(x1 + iconW / 2f - textRenderer().getWidth(oreVal) / 2f), (int)(y1 - lh - 2), 0xFFFFFF, true);
        drawResourceMeter(ctx, x1, y1, iconW, iconH, valueRatio(ore), TextureStorage.ore_empty, TextureStorage.ore_full);

        render2DUtil().drawText(ctx, "Fish", (int)(x2 + iconW / 2f - textRenderer().getWidth("Fish") / 2f), (int)(y1 - valuePad - lh - 2), 0xFFFFFF, true);
        render2DUtil().drawText(ctx, fishVal,(int)(x2 + iconW / 2f - textRenderer().getWidth(fishVal)/ 2f), (int)(y1 - lh - 2), 0xFFFFFF, true);
        drawResourceMeter(ctx, x2, y1, iconW, iconH, valueRatio(fish), TextureStorage.fish_empty, TextureStorage.fish_full);

        render2DUtil().drawText(ctx, "Wood", (int)(x1 + iconW / 2f - textRenderer().getWidth("Wood") / 2f), (int)(y2 - valuePad - lh - 2), 0xFFFFFF, true);
        render2DUtil().drawText(ctx, woodVal,(int)(x1 + iconW / 2f - textRenderer().getWidth(woodVal)/ 2f), (int)(y2 - lh - 2), 0xFFFFFF, true);
        drawResourceMeter(ctx, x1, y2, iconW, iconH, valueRatio(wood), TextureStorage.wood_empty, TextureStorage.wood_full);

        render2DUtil().drawText(ctx, "Crops",(int)(x2 + iconW / 2f - textRenderer().getWidth("Crops")/ 2f), (int)(y2 - valuePad - lh - 2), 0xFFFFFF, true);
        render2DUtil().drawText(ctx, cropVal,(int)(x2 + iconW / 2f - textRenderer().getWidth(cropVal)/ 2f), (int)(y2 - lh - 2), 0xFFFFFF, true);
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

    void drawRoleUI(DrawContext ctx, int mouseX, int mouseY) {
        float baseX = renderX + renderedBorderXOffset;
        float baseY = renderY + renderedBorderYOffset;

        if (state == UiState.OPTED_OUT) {
            Rect r = new Rect(baseX + PAD, baseY + PAD, OPT_BTN_SIZE, OPT_BTN_SIZE);
            boolean hover = r.contains(mouseX, mouseY);
            Color s = new Color(46, 46, 52, 220);
            Color e = new Color(28, 28, 34, 220);
            if (hover) {
                s = new Color(56, 56, 64, 230);
                e = new Color(34, 34, 42, 230);
                render2DUtil().drawGlow(ctx, r.x, r.y, r.x + r.w, r.y + r.h, new Color(80, 110, 255, 90), OPT_CORNER);
            }
            fillRoundedGradient(ctx, r, OPT_CORNER, s, e, true);
            render2DUtil().drawTexture(ctx, TextureStorage.opt_in, r.x + 4, r.y + 4, r.x + r.w - 4, r.y + r.h - 4);
            return;
        }

        float barWidth = PAD + (BTN_SIZE * 3f) + (BTN_GAP * 2f) + PAD;
        Rect bar = new Rect(baseX, baseY + PAD, barWidth, BAR_HEIGHT);
        boolean barHover = bar.contains(mouseX, mouseY);
        Color s = new Color(40, 40, 46, 220);
        Color e = new Color(22, 22, 28, 220);
        if (barHover) {
            s = new Color(50, 50, 58, 230);
            e = new Color(28, 28, 36, 230);
            render2DUtil().drawGlow(ctx, bar.x, bar.y, bar.x + bar.w, bar.y + bar.h, new Color(80, 110, 255, 90), BAR_CORNER);
        }
        fillRoundedGradient(ctx, bar, BAR_CORNER, s, e, false);

        float bx = bar.x + PAD;
        Rect tankRect = new Rect(bx, bar.y + PAD, BTN_SIZE, BTN_SIZE);
        Rect dpsRect = new Rect(bx + BTN_SIZE + BTN_GAP, bar.y + PAD, BTN_SIZE, BTN_SIZE);
        Rect healRect = new Rect(bx + (BTN_SIZE + BTN_GAP) * 2f, bar.y + PAD, BTN_SIZE, BTN_SIZE);

        drawRoleButton(ctx, tankRect, Roles.TANK);
        drawRoleButton(ctx, dpsRect, Roles.DPS);
        drawRoleButton(ctx, healRect, Roles.HEAL);

        if (state != UiState.OPTED_OUT) {
            Rect out = new Rect(healRect.x + healRect.w - 1 + 4, bar.y - 4, OPT_OUT_SIZE, OPT_OUT_SIZE);
            boolean hover = out.contains(mouseX, mouseY);
            Color os = hover ? new Color(70, 70, 78, 230) : new Color(64, 64, 72, 220);
            Color oe = hover ? new Color(40, 40, 48, 230) : new Color(36, 36, 44, 220);
            fillRoundedGradient(ctx, out, 2f, os, oe, true);
            render2DUtil().drawTexture(ctx, TextureStorage.opt_out, out.x + 1, out.y + 1, out.x + out.w - 1, out.y + out.h - 1);
        }
    }

    void drawRoleButton(DrawContext ctx, Rect r, Roles role) {
        boolean active = Objects.equals(selectedRole, role);
        TexturePair tp = roleTextures.get(role);
        render2DUtil().drawTexture(ctx, active ? tp.active : tp.inactive, r.x, r.y, r.x + r.w, r.y + r.h);
    }

    String valueText(CappedValue v) {
        if (v == null) return "0/0";
        int c = (int) Math.round(v.current());
        int m = (int) Math.round(v.max());
        return c + "/" + m;
    }

    double valueRatio(CappedValue v) {
        if (v == null) return 0d;
        double max = v.max();
        double val = v.current();
        if (max <= 0d) return 0d;
        return val / max;
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

        float cmdBaseX = renderX + renderedBorderXOffset + mapWidth / 2f;
        Rect cmdRect = new Rect(cmdBaseX - CMD_BTN_SIZE / 2f, baseY + PAD, CMD_BTN_SIZE, CMD_BTN_SIZE);
        if (cmdRect.contains(mouseX, mouseY)) {
            commandActive = !commandActive;
            return true;
        }

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

        if (state == UiState.CHOOSING_ROLE) {
            if (tankRect.contains(mouseX, mouseY)) { selectedRole = Roles.TANK; state = UiState.CHOSEN_ROLE; return true; }
            if (dpsRect.contains(mouseX, mouseY)) { selectedRole = Roles.DPS; state = UiState.CHOSEN_ROLE; return true; }
            if (healRect.contains(mouseX, mouseY)) { selectedRole = Roles.HEAL; state = UiState.CHOSEN_ROLE; return true; }
        }

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

        for (Poi poi : filteredPois) {
            if (!(poi instanceof TerritoryPoi territoryPoi)) continue;

            float poiRenderX = MapRenderer.getRenderX(poi, mapCenterX, centerX, zoomRenderScale);
            float poiRenderZ = MapRenderer.getRenderZ(poi, mapCenterZ, centerZ, zoomRenderScale);

            for (String tradingRoute : territoryPoi.getTerritoryInfo().getTradingRoutes()) {
                Optional<Poi> routePoi = filteredPois.stream()
                        .filter(filteredPoi -> filteredPoi.getName().equals(tradingRoute))
                        .findFirst();

                if (routePoi.isPresent() && filteredPois.contains(routePoi.get())) {
                    float x = MapRenderer.getRenderX(routePoi.get(), mapCenterX, centerX, zoomRenderScale);
                    float z = MapRenderer.getRenderZ(routePoi.get(), mapCenterZ, centerZ, zoomRenderScale);

                    RenderUtils.drawLine(matrixStack, CommonColors.DARK_GRAY, poiRenderX, poiRenderZ, x, z, 0, 1);
                }
            }
        }

        VertexConsumerProvider.Immediate bufferSource = mc.getBufferBuilders().getEntityVertexConsumers();

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
