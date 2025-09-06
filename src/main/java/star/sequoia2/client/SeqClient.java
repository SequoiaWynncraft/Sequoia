package star.sequoia2.client;

import com.collarmc.pounce.EventBus;
import com.collarmc.pounce.Preference;
import com.collarmc.pounce.Subscribe;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.mojang.logging.LogUtils;
import lombok.Getter;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.MinecraftClient;
import org.slf4j.Logger;
import star.sequoia2.accessors.EventBusAccessor;
import star.sequoia2.client.commands.Commands;
import star.sequoia2.client.notifications.Notifications;
import star.sequoia2.configuration.Configuration;
import star.sequoia2.events.MinecraftFinishedLoading;
import star.sequoia2.features.Features;
import star.sequoia2.features.impl.Client;
import star.sequoia2.features.impl.RenderTest;
import star.sequoia2.gui.Fonts;
import star.sequoia2.gui.categories.Categories;
import star.sequoia2.settings.SettingsState;
import star.sequoia2.utils.Themes;
import star.sequoia2.utils.render.Render2DUtil;
import star.sequoia2.utils.render.Render3DUtil;

import java.io.IOException;

public class SeqClient implements ClientModInitializer, EventBusAccessor {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static boolean initialized = false;

    public static final MinecraftClient mc = MinecraftClient.getInstance();
    public static final String SERVERADDENDPOINT = "http://replaceme/api/raids/add";

    @Getter
    private static EventBus eventBus;

    @Getter
    private static Notifications notifications;

    @Getter
    private static Configuration configuration;

    @Getter
    private static Features features;

    @Getter
    private static SettingsState settings;

    @Getter
    private static Commands commands;

    @Getter
    private static Fonts fonts;

    @Getter
    private static Themes themes;

    @Getter
    private static Render2DUtil render2DUtil;

    @Getter
    private static Render3DUtil render3DUtil;

    @Getter
    private static SimpleProfileFetcher profileFetcher;

    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing Seq client.");
        eventBus = new EventBus(mc::execute); //before initializing everything else
        eventBus.subscribe(this);

        try {
            configuration = new Configuration();
        } catch (IOException e) {
            throw new IllegalStateException("could not read configuration", e);
        }

        fonts = new Fonts();

        themes = new Themes();

        notifications = new Notifications();
        render2DUtil = new Render2DUtil();
        render3DUtil = new Render3DUtil();
    }

    @Subscribe(value = Preference.MAIN, priority = 1)
    public void onFinishedLoading(MinecraftFinishedLoading ignored) {
        features = new Features();
        settings = new SettingsState();

        subscribe(settings);
        registerFeatures();

        settings.load(features);

        commands = new Commands();

        Categories.registerDefault();

        initialized = true;
        LOGGER.info("Initialization complete.");
    }

    @Subscribe(value = Preference.CALLER, priority = 2)
    public void onFinishedLoadingOnUI(MinecraftFinishedLoading ignored) {
        try {
            fonts.initializeFonts();
            profileFetcher = new SimpleProfileFetcher(); //init late so hopefully service is created
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void registerFeatures() {
        features.add(new Client()); // always first so you can get colors
        features.add(new RenderTest());
    }

    public static Supplier<Client> clientModule = Suppliers
            .memoize(() -> features.get(Client.class)
                    .orElseThrow(() -> new IllegalStateException("Client feature was not registered")));

    public static void reloadConfiguration() {
        try {
            configuration = new Configuration();
        } catch (IOException e) {
            throw new RuntimeException("Failed to reload configuration", e);
        }
    }
}
