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
import org.lwjgl.glfw.GLFW;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.utils.render.TextureStorage;

import java.util.ArrayList;
import java.util.List;

public class BetterGuildMapScreen extends AbstractMapScreen implements RenderUtilAccessor {
    boolean optedIn = false;

    Roles selectedRole = null;

    enum Roles {
        TANK,
        DPS,
        HEAL
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

        float renderPosX = renderX + renderedBorderXOffset;
        float renderPosY = renderY + renderedBorderYOffset;

        if (!optedIn) {
            render2DUtil().roundRectFilled(context.getMatrices(), renderPosX + 5, renderPosY + 5, renderPosX + 50, renderY + renderedBorderYOffset + 50, 5f, Color.darkGray());
            render2DUtil().drawTexture(context, TextureStorage.opt_in, renderPosX + 10, renderPosY + 10, renderPosX + 45, renderPosY + 45);
        } else if (selectedRole == null) {
            render2DUtil().roundRectFilled(context.getMatrices(), renderPosX, renderPosY + 5, renderPosX + (50*3), renderPosY + 50, 5f, Color.darkGray());

            render2DUtil().drawTexture(context, TextureStorage.tank_inactive, renderPosX + 10, renderPosY + 10, renderPosX + 45, renderPosY + 45);
            renderPosX = renderPosX + 50;
            render2DUtil().drawTexture(context, TextureStorage.damage_inactive, renderPosX + 10, renderPosY + 10, renderPosX + 45, renderPosY + 45);
            renderPosX = renderPosX + 50;
            render2DUtil().drawTexture(context, TextureStorage.healer_inactive, renderPosX + 10, renderPosY + 10, renderPosX + 45, renderPosY + 45);
        } else {
            switch (selectedRole) {
                case TANK:
                    render2DUtil().roundRectFilled(context.getMatrices(), renderPosX, renderPosY + 5, renderPosX + (50*3), renderPosY + 50, 5f, Color.darkGray());

                    render2DUtil().drawTexture(context, TextureStorage.tank_active, renderPosX + 10, renderPosY + 10, renderPosX + 45, renderPosY + 45);
                    renderPosX = renderPosX + 50;
                    render2DUtil().drawTexture(context, TextureStorage.damage_inactive, renderPosX + 10, renderPosY + 10, renderPosX + 45, renderPosY + 45);
                    renderPosX = renderPosX + 50;
                    render2DUtil().drawTexture(context, TextureStorage.healer_inactive, renderPosX + 10, renderPosY + 10, renderPosX + 45, renderPosY + 45);
                    render2DUtil().roundRectFilled(context.getMatrices(), renderPosX + 45, renderPosY, renderPosX + 45 + 20, renderPosY + 20, 1f, Color.gray());
                    render2DUtil().drawTexture(context, TextureStorage.opt_out, renderPosX + 45, renderPosY, renderPosX + 45 + 18, renderPosY + 18);
                    return;
                case DPS:
                    render2DUtil().roundRectFilled(context.getMatrices(), renderPosX, renderPosY + 5, renderPosX + (50*3), renderPosY + 50, 5f, Color.darkGray());

                    render2DUtil().drawTexture(context, TextureStorage.tank_inactive, renderPosX + 10, renderPosY + 10, renderPosX + 45, renderPosY + 45);
                    renderPosX = renderPosX + 50;
                    render2DUtil().drawTexture(context, TextureStorage.damage_active, renderPosX + 10, renderPosY + 10, renderPosX + 45, renderPosY + 45);
                    renderPosX = renderPosX + 50;
                    render2DUtil().drawTexture(context, TextureStorage.healer_inactive, renderPosX + 10, renderPosY + 10, renderPosX + 45, renderPosY + 45);
                    render2DUtil().roundRectFilled(context.getMatrices(), renderPosX + 45, renderPosY, renderPosX + 45 + 20, renderPosY + 20, 1f, Color.gray());
                    render2DUtil().drawTexture(context, TextureStorage.opt_out, renderPosX + 45, renderPosY, renderPosX + 45 + 18, renderPosY + 18);
                    return;
                case HEAL:
                    render2DUtil().roundRectFilled(context.getMatrices(), renderPosX, renderPosY + 5, renderPosX + (50*3), renderPosY + 50, 5f, Color.darkGray());

                    render2DUtil().drawTexture(context, TextureStorage.tank_inactive, renderPosX + 10, renderPosY + 10, renderPosX + 45, renderPosY + 45);
                    renderPosX = renderPosX + 50;
                    render2DUtil().drawTexture(context, TextureStorage.damage_inactive, renderPosX + 10, renderPosY + 10, renderPosX + 45, renderPosY + 45);
                    renderPosX = renderPosX + 50;
                    render2DUtil().drawTexture(context, TextureStorage.healer_active, renderPosX + 10, renderPosY + 10, renderPosX + 45, renderPosY + 45);
                    render2DUtil().roundRectFilled(context.getMatrices(), renderPosX + 45, renderPosY, renderPosX + 45 + 20, renderPosY + 20, 1f, Color.gray());
                    render2DUtil().drawTexture(context, TextureStorage.opt_out, renderPosX + 45, renderPosY, renderPosX + 45 + 18, renderPosY + 18);
                    return;
            }
        }

    }

    @Override
    public boolean doMouseClicked(double mouseX, double mouseY, int button) {
        float renderPosX = renderX + renderedBorderXOffset;
        float renderPosY = renderY + renderedBorderYOffset;
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (isWithin(mouseX, mouseY, renderPosX + 5, renderPosY + 5, renderPosX + 50, renderPosY + 50) && !optedIn) {
                optedIn = true;
            } else if (optedIn && selectedRole == null) {
                if (isWithin(mouseX, mouseY, renderPosX + 5, renderPosY + 5, renderPosX + 50, renderPosY + 50)) {
                    selectedRole = Roles.TANK;
                }
                renderPosX = renderPosX + 50;
                if (isWithin(mouseX, mouseY, renderPosX + 5, renderPosY + 5, renderPosX + 50, renderPosY + 50)) {
                    selectedRole = Roles.DPS;
                }
                renderPosX = renderPosX + 50;
                if (isWithin(mouseX, mouseY, renderPosX + 5, renderPosY + 5, renderPosX + 50, renderPosY + 50)) {
                    selectedRole = Roles.HEAL;
                }
            } else {
                renderPosX = renderPosX + 50;renderPosX = renderPosX + 50;
                if (isWithin(mouseX, mouseY, renderPosX + 45, renderPosY, renderPosX + 45 + 18, renderPosY + 18)) {
                    optedIn = false;
                    selectedRole = null;
                }
            }
        }

        return super.doMouseClicked(mouseX, mouseY, button);
    }

    public boolean isWithin(double mouseX, double mouseY, double x, double y, double x2, double y2) {
        return mouseX >= x && mouseX <= x2 && mouseY >= y && mouseY <= y2;
    }

    protected void renderMap(DrawContext context) {
        MatrixStack poseStack = context.getMatrices();

        RenderUtils.enableScissor(
                context,
                (int) (renderX + renderedBorderXOffset),
                (int) (renderY + renderedBorderYOffset),
                (int) mapWidth,
                (int) mapHeight);

        // Background black void color
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

            // If the API and advamcement pois don't match, we use the API pois without advancement info
            if (territoryProfile != null
                    && territoryProfile
                    .getGuild()
                    .equals(poi.getTerritoryInfo().getGuildName())) {
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
