package star.sequoia2.gui.categories.impl;

import com.mojang.logging.LogUtils;
import mil.nga.color.Color;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minidev.json.JSONObject;
import star.sequoia2.accessors.RenderUtilAccessor;
import star.sequoia2.client.SeqClient;
import star.sequoia2.features.impl.Settings;
import star.sequoia2.gui.categories.RelativeComponent;
import star.sequoia2.gui.component.SearchBarComponent;
import star.sequoia2.http.Http;
import star.sequoia2.utils.render.TextureStorage;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static star.sequoia2.client.SeqClient.mc;

public class PlayerLookupCategory extends RelativeComponent implements RenderUtilAccessor {
    private static final Logger LOGGER = LogUtils.getLogger();

    SearchBarComponent searchBarComponent;

    // --- Added for skin preview ---
    private OtherClientPlayerEntity previewPlayer;
    private String previewedUsername;

    private volatile JSONObject currentStats;

    public PlayerLookupCategory() {
        super("Lookup");
        searchBarComponent = new SearchBarComponent();
    }

    @Override
    public void render(DrawContext context, float mouseX, float mouseY, float delta) {
        float left = contentX();
        float top = contentY();
        float right = left + contentWidth();
        float bottom = top + contentHeight();
        MatrixStack matrices = context.getMatrices();

        Color normal = features().get(Settings.class).map(Settings::getThemeNormal).orElse(Color.black());
        Color dark = features().get(Settings.class).map(Settings::getThemeDark).orElse(Color.black());
        Color light = features().get(Settings.class).map(Settings::getThemeLight).orElse(Color.black());
        Color accent1 = features().get(Settings.class).map(Settings::getThemeAccent1).orElse(Color.black());
        Color accent2 = features().get(Settings.class).map(Settings::getThemeAccent2).orElse(Color.black());
        Color accent3 = features().get(Settings.class).map(Settings::getThemeAccent3).orElse(Color.black());

        searchBarComponent.render(context, mouseX, mouseY, delta);
        searchBarComponent.setPos(left, top);
        searchBarComponent.setDimensions(contentWidth() - 25f, getGuiRoot().btnH);

        boolean hoverEnter = isWithin(mouseX, mouseY, right - 25f, top, 25, getGuiRoot().btnH);
        Color enterStart = hoverEnter ? light : normal;
        render2DUtil().roundRectFilled(context.getMatrices(), right - 25f, top, right, top + getGuiRoot().btnH, getGuiRoot().rounding, enterStart);

        matrices.push();
        matrices.translate(right - 25f, top, 0);
        context.drawTexture(RenderLayer::getGuiTextured, TextureStorage.enter, 0, 0, 0, 0, (int) getGuiRoot().btnH, (int) getGuiRoot().btnH, (int) getGuiRoot().btnH, (int) getGuiRoot().btnH,
                hoverEnter ? new java.awt.Color(dark.getRed(), dark.getGreen(), dark.getBlue(), dark.getAlpha()).getRGB() : new java.awt.Color(light.getRed(), light.getGreen(), light.getBlue(), light.getAlpha()).getRGB());
        matrices.pop();

        if (currentStats != null) {
            String username = jStr(currentStats, "username");
            boolean online = jBool(currentStats, "online");
            String server = jStr(currentStats, "server");

            double playtime = jDouble(currentStats, "playtime");

            JSONObject guildObj = jObj(currentStats, "guild");
            String guildName = guildObj != null ? jStr(guildObj, "name") : null;
            String guildRank = guildObj != null ? jStr(guildObj, "rank") : null;

            JSONObject raidsList = jObj(jObj(jObj(currentStats, "globalData"), "raids"), "list");

            int warsComplete = jInt(jObj(currentStats, "globalData"), "wars");

            int totalLevel = jInt(jObj(currentStats, "globalData"), "totalLevel");

            float x = left;
            float y = top + getGuiRoot().btnH + 6f;
            float line = textRenderer().fontHeight + 4f;

            render2DUtil().drawText(context, (username != null ? username : "(unknown)") + (online ? "  • ONLINE" : "  • offline"), x, y, light.getColor(), true);
            y += line;

            render2DUtil().drawText(context, "Total playtime: " + String.format("%.1f hrs", playtime), x, y, light.getColor(), true);
            y += line;

            render2DUtil().drawText(context, "Total level: " + totalLevel, x, y, light.getColor(), true);
            y += line;

            render2DUtil().drawText(context, (guildName != null) ? guildRank + " of " + guildName + " guild." : "Not in a guild", x, y, light.getColor(), true);
            y += line;

            render2DUtil().drawText(context, "Wars: " + warsComplete, x, y, light.getColor(), true);
            y += line;

            if (online) {
                render2DUtil().drawText(context, "Server: " + (server != null ? server : "-"), x, y, light.getColor(), true);
                y += line;
            } else {
                render2DUtil().drawText(context, "Last known server: " + (server != null ? server : "-"), x, y, light.getColor(), true);
                y += line;
            }

            render2DUtil().drawText(context, "Raid completions (total across all characters):", x, y, accent2.getColor(), true);
            y += line;

            if (raidsList == null || raidsList.isEmpty()) {
                render2DUtil().drawText(context, "No raids found.", x, y, light.getColor(), true);
                y += line;
            } else {
                List<Map.Entry<String, Object>> entries = new ArrayList<>(raidsList.entrySet());
                entries.sort((a, b) -> a.getKey().compareToIgnoreCase(b.getKey()));

                for (Map.Entry<String, Object> e : entries) {
                    String raidName = e.getKey();
                    int clears = (e.getValue() instanceof Number)
                            ? ((Number) e.getValue()).intValue() : 0;

                    if (y + line > bottom - 4) break;

                    render2DUtil().drawText(context, raidName + ": " + clears, x, y, light.getColor(), true);
                    y += line;
                }
            }
        } else {
            render2DUtil().drawText(context, "§8Search to see stats", left + ((contentWidth()) / 2) - (textRenderer().getWidth("Search to see stats") / 2f), top + (contentHeight() / 2), light.getColor(), true);
        }

        if (previewPlayer != null) {
            int pad = 6;
            int boxHeight = (int) 100f;
            int boxWidth = (int) 50f;
            int x2 = (int) (right - pad);
            int y1 = (int) (top + getGuiRoot().btnH + 6);
            int x1 = x2 - boxWidth;
            int y2 = y1 + boxHeight;

            previewPlayer.bodyYaw += delta * 10f;
            previewPlayer.headYaw = previewPlayer.bodyYaw;

            render2DUtil().roundGradientFilled(context.getMatrices(), x1 - 1, y1 - 1, x2 + 1, y2 + 1, getGuiRoot().rounding, dark, normal, true);

            InventoryScreen.drawEntity(
                    context,
                    x1, y1, x2, y2,
                    boxWidth - 10,
                    0.0625f,
                    mouseX, mouseY,
                    previewPlayer
            );
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        searchBarComponent.mouseClicked(mouseX, mouseY, button);
        float left = contentX();
        float top = contentY();
        float right = left + contentWidth();
        if (isWithin(mouseX, mouseY, right - 25f, top, 25, getGuiRoot().btnH) && searchBarComponent.isSearching()) {
            Http.fetchPlayerFullStats(searchBarComponent.getSearch())
                    .thenAccept(json -> {
                        currentStats = json;
                        tryMakePreviewFromStats(json);
                    })
                    .exceptionally(ex -> {
                        LOGGER.warn(ex.getMessage());
                        return null;
                    });
            searchBarComponent.setSearching(false);
            searchBarComponent.setSearch("");
        }
    }

    @Override
    public void keyPressed(int keyCode, int scanCode, int modifiers) {
        searchBarComponent.keyPressed(keyCode, scanCode, modifiers);
        if (keyCode == GLFW.GLFW_KEY_ENTER && searchBarComponent.isSearching()) {
            Http.fetchPlayerFullStats(searchBarComponent.getSearch())
                    .thenAccept(json -> {
                        currentStats = json;
                        tryMakePreviewFromStats(json);
                    })
                    .exceptionally(ex -> {
                        LOGGER.warn(ex.getMessage());
                        return null;
                    });
            searchBarComponent.setSearching(false);
            searchBarComponent.setSearch("");
        }
    }

    @Override
    public void charTyped(char chr, int modifiers) {
        searchBarComponent.charTyped(chr, modifiers);
    }

    private void tryMakePreviewFromStats(JSONObject stats) {
        String username = jStr(stats, "username");
        String uuidStr  = jStr(stats, "uuid");

        if (uuidStr == null || uuidStr.isBlank()) return;
        if (previewPlayer != null && username.equalsIgnoreCase(previewedUsername)) return;

        if (mc.world == null) return;

        SeqClient.getProfileFetcher().fetchByUUID(UUID.fromString(uuidStr))
            .thenAccept(gameProfile -> {
                previewPlayer = gameProfile.map(profile -> new OtherClientPlayerEntity(mc.world, profile)).orElse(null);
            });
        if (previewPlayer != null) {
            previewPlayer.bodyYaw = 180f;
            previewPlayer.headYaw = 180f;
            previewedUsername = username;
        }
    }

    private static String jStr(JSONObject o, String k) {
        if (o == null) return null;
        Object v = o.get(k);
        return (v == null) ? null : String.valueOf(v);
    }

    private static int jInt(JSONObject o, String k) {
        if (o == null) return 0;
        Object v = o.get(k);
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).intValue();
        String s = v.toString().trim();
        if (s.isEmpty() || s.equalsIgnoreCase("null")) return 0;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static boolean jBool(JSONObject o, String k) {
        if (o == null) return false;
        Object v = o.get(k);
        if (v == null) return false;
        if (v instanceof Boolean) return (Boolean) v;
        String s = v.toString().trim();
        if (s.isEmpty() || s.equalsIgnoreCase("null")) return false;
        return Boolean.parseBoolean(s);
    }

    private static JSONObject jObj(JSONObject o, String k) {
        if (o == null) return null;
        Object v = o.get(k);
        return (v instanceof JSONObject) ? (JSONObject) v : null;
    }

    private static double jDouble(JSONObject o, String k) {
        if (o == null) return 0.0;
        Object v = o.get(k);
        if (v == null) return 0.0;
        if (v instanceof Number) return ((Number) v).doubleValue();
        String s = v.toString().trim();
        if (s.isEmpty() || s.equalsIgnoreCase("null")) return 0.0;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
