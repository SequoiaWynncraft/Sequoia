package star.sequoia2.features.impl.ws;

import com.collarmc.pounce.Subscribe;
import com.wynntils.core.text.StyledText;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import star.sequoia2.accessors.GuildParserAccessor;
import star.sequoia2.accessors.TeXParserAccessor;
import star.sequoia2.client.SeqClient;
import star.sequoia2.events.ChatMessageEvent;
import star.sequoia2.features.ToggleFeature;

import java.util.regex.Pattern;

import static star.sequoia2.client.SeqClient.mc;

public class ChatHookFeature extends ToggleFeature implements GuildParserAccessor, TeXParserAccessor {

    public ChatHookFeature() {
        super("ChatHook", "Chat related stuffs (type shi)", true);
    }

    private static final String GUILD_CHAT_PREFIX1 = "§b\uE006\uE002§b §b\uE060";
    private static final String GUILD_CHAT_PREFIX2 = "§b\uE001§b §b\uE060";

    // i hate apostrophes
    private static final Pattern GUILD_CHAT_HOVER = Pattern.compile(
            "\\\\hover\\{§f[^}]+'(?:s)?§7\\s+real\\s+name\\s+is\\s+§f[\\w]+}\\{§3(?:§o)?[^}]+}\\s*§3:§b"
    );

    private static final Pattern GUILD_CHAT_PLAIN = Pattern.compile(
            "§3\\w+:§b"
    );

    private static final Pattern GUILD_CHAT_WYNNTILS_NAME = Pattern.compile(
            "§3[^\\s§/]+/§3§o[^\\s§]+"
    );

    // one “name” token: either plain colored name, or hover-visible colored name
    private static final String NAME_TOK =
            "(?:\\\\hover\\{[^}]+}\\{§e(?:§o)?[^}]+}§b|§e[^§\\r\\n]+§b)";

    // raid header
    private static final Pattern GUILD_RAID_HEADER = Pattern.compile(
            "^(?:§b)+\\s*"
                    + NAME_TOK + "\\s*,\\s*"
                    + NAME_TOK + "\\s*,\\s*"
                    + NAME_TOK + "\\s*,\\s*and\\s*"
                    + NAME_TOK + "\\s*$",
            Pattern.DOTALL
    );

//    // allow any §-style code
//    private static final String SEC = "§[0-9a-fk-or<>]";
//
//    // raid block (more tolerant)
//    private static final Pattern GUILD_RAID_BLOCK = Pattern.compile(
//            "^(?:§b)+\\s*"
//                    + NAME_TOK + "\\s*,\\s*"
//                    + NAME_TOK + "\\s*,\\s*"
//                    + NAME_TOK + "\\s*,\\s*and\\s*"
//                    + NAME_TOK
//                    + ".*?finished\\s*(?:" + SEC + ")*\\s*§3[^§\\r\\n]+§b",
//            Pattern.DOTALL
//    );


    private static final String GUILD_RAID_PREFIX1 = "§b\uE006\uE002§b";
    private static final String GUILD_RAID_PREFIX2 = "§b\uE001§b";
    // this actually works
    private static final Pattern GUILD_RAID_BLOCK = Pattern.compile("§b finished");
    private static final Pattern OTHER_GUILD_RAID_BLOCK = Pattern.compile("§b §bfinished");

    private static final Pattern AUTO_CONNECT =
            Pattern.compile("§6§lWelcome to Wynncraft!");

    @Subscribe
    public void onChatMessage(ChatMessageEvent event) {
        Text message = event.message();
//            "[12:12:02] [Render thread/INFO] (sequoia2) [VERBOSE] [CHAT] §b\uDAFF\uDFFC\uE006\uDAFF\uDFFF\uE002\uDAFF\uDFFE §ebad_and_sad§b, §eMasss§b, §e§owar tank§b, and §e§oTotal Obliteration§b finished §3The Nameless §b\uDAFF\uDFFC\uE001\uDB00\uDC06§3 Anomaly§b and claimed §32x Aspects§b, §32048x §b\uDAFF\uDFFC\uE001\uDB00\uDC06§3 Emeralds§b, and §3+10367m Guild Experience\n"
        StyledText styledText = StyledText.fromComponent(message);

        String tex = teXParser().toTeX(styledText.stripAlignment());

        SeqClient.debug(tex);

        if (AUTO_CONNECT.matcher(tex).find()) {
            SeqClient.debug("parsing as login...");
            if (features().getIfActive(WebSocketFeature.class).map(webSocketFeature -> webSocketFeature.getConnectOnJoin().get()).orElse(false)
                    && !features().getIfActive(WebSocketFeature.class).map(WebSocketFeature::isAuthenticated).orElse(false)
                    && mc.player != null) {
                mc.player.networkHandler.sendCommand("connect");
            }
            return;
        }

        if (!features().getIfActive(WebSocketFeature.class).map(WebSocketFeature::isActive).orElse(false)
                || !features().getIfActive(WebSocketFeature.class).map(WebSocketFeature::isAuthenticated).orElse(false)
                || !features().getIfActive(ChatHookFeature.class).map(ChatHookFeature::isActive).orElse(false)) {
            return;
        }

        if ((tex.startsWith(GUILD_CHAT_PREFIX1) || tex.startsWith(GUILD_CHAT_PREFIX2)) &&
                ((GUILD_CHAT_HOVER.matcher(tex).results().limit(2).count()
                        + GUILD_CHAT_PLAIN.matcher(tex).results().limit(2).count()) + GUILD_CHAT_WYNNTILS_NAME.matcher(tex).results().limit(2).count()) == 1) {
            SeqClient.debug("parsing as guild chat message...");
            guildMessageParser().parseGuildMessage(tex);
            return;
        }

        tex = remove_multiline(tex); // cleaned tex for variable multiline texts like guild raids
//            Sequoia2.debug(String.format("cleaned tex: %s", tex));

        if (GUILD_RAID_BLOCK.matcher(tex).find() || OTHER_GUILD_RAID_BLOCK.matcher(tex).find()) {
            SeqClient.debug("parsing as guild raid completion...");
            guildRaidParser().parseGuildRaid(tex);
        }
    }

    private static final Pattern SECTION_CODES =
            Pattern.compile("§[0-9a-fk-or<>]", Pattern.CASE_INSENSITIVE);


    public static String remove_multiline(String s) {
        if (s == null || s.isEmpty()) return "";
        s = s.replaceAll("§.\uE001§.", "").trim();
        // strip private codepoints
        StringBuilder out = new StringBuilder(s.length());
        s.codePoints().forEach(cp -> {
            if (Character.getType(cp) != Character.PRIVATE_USE) out.appendCodePoint(cp);
        });
        s = out.toString();
        // normalize all whitespace (including single \n) to a single space also remove end of line formats
        s = s.replace('\u00A0', ' ').replaceAll("\\s+", " ").trim();
        return s;
    }

    public static String remove_formatting(String s) {
        return SECTION_CODES.matcher(s).replaceAll("");
    }

    public static String clean(String s) {
        if (s == null || s.isEmpty()) return "";
        return remove_formatting(remove_multiline(s));
    }
}
