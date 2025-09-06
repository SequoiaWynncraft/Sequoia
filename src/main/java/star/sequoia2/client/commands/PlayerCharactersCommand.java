package star.sequoia2.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import star.sequoia2.accessors.TeXParserAccessor;
import star.sequoia2.client.services.wynn.player.PlayerResponse;
import star.sequoia2.client.types.command.Command;
import star.sequoia2.client.types.command.suggestions.SuggestionProviders;
import star.sequoia2.utils.text.TextUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class PlayerCharactersCommand extends Command implements TeXParserAccessor {
    @Override
    public String getCommandName() {
        return "playercharacters";
    }

    @Override
    public List<String> getAliases() {
        return List.of("pc");
    }

    @Override
    public CommandNode<FabricClientCommandSource> register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        return dispatcher.register(
                literal(getCommandName())
                        .then(argument("player", word()).suggests(SuggestionProviders.Player)
                                .executes(this::playerCharacters
                                )
                        )
        );
    }

    private int playerCharacters(CommandContext<FabricClientCommandSource> ctx) {
        String username = ctx.getArgument("player", String.class);
        if (StringUtils.isBlank(username) || !MinecraftUtils.isValidUsername(username)) {
            ctx.getSource()
                    .sendError(Sequoia2.prefix(Text.translatable("sequoia.command.invalidUsername")));
            return 0;
        } else {
            Services.Player.getPlayerFullResult(username).whenComplete((playerResponse, throwable) -> {
                if (throwable != null) {
                    Sequoia2.error("Error looking up player: " + username, throwable);
                    ctx.getSource()
                            .sendError(Sequoia2.prefix(Text.translatable(
                                    "sequoia.command.playerInfo.errorLookingUpPlayer", username)));
                } else {
                    if (playerResponse == null
                            || playerResponse.getGlobalData() == null
                            || playerResponse.getGlobalData().getRaids() == null) {
                        ctx.getSource()
                                .sendError(Sequoia2.prefix(Text.translatable(
                                        "sequoia.command.playerRaids.playerNotFound", username)));
                    } else {
                        StringBuilder TeX = new StringBuilder();
                        TeX.append(I18n.translate("sequoia.command.playerInfo.showingPlayerInfoIntro",
                                playerResponse.getLegacyRankColour() != null ? playerResponse.getLegacyRankColour().getMain().substring(1) : "ffffff", playerResponse.getSupportRank() != null ? "[" + playerResponse.getSupportRank().toUpperCase(Locale.ROOT) + "] " : "",
                                playerResponse.getUsername())
                        );
                        playerResponse.getCharacters().values().stream()
                                .sorted(Comparator
                                        .comparingInt(PlayerResponse.Character::getLevel)
                                        .thenComparingInt(PlayerResponse.Character::getTotalLevel)
                                        .reversed()).forEach(c -> {
                            TeX.append(characterText(c)).append("\\n");
                        });
                        ctx.getSource().sendFeedback(Sequoia2.prefix(teXParser().parseMutableText(TeX.substring(0, TeX.length()-2))));
                    }
                }

            });
        }
        return 1;
    }

    public static String characterText(PlayerResponse.Character character) {
        Sequoia2.debug(String.format("\\hover{%s}{\\-{[}\\2{%d}\\-{]}%s \\1{%s}\\nickname{%s}}", characterHoverText(character), character.getLevel(), gamemodeText(character),
                TextUtils.upperfirst(character.getType()), character.getNickname()));
        return String.format("\\hover{%s}{\\-{[}\\2{%d}\\-{]}%s \\1{%s}\\nickname{%s}}", characterHoverText(character), character.getLevel(), gamemodeText(character),
                TextUtils.upperfirst(character.getType()), character.getNickname());
    }

    public static String characterTextNoHover(PlayerResponse.Character character) {
        return String.format("\\-{[}\\2{%d}\\-{]}%s \\1{%s}\\nickname{%s}", character.getLevel(), gamemodeText(character),
                TextUtils.upperfirst(character.getType()), character.getNickname());
    }

    private static String characterHoverText(PlayerResponse.Character character) {
        return I18n.translate("sequoia.command.playerCharacters.showingCharacterHover", characterTextNoHover(character), character.getRaids().getTotal(), character.getWars(), character.getMobsKilled(), character.getChestsFound(), professions(character), (int) character.getPlaytime() + " hours");
    }

    public static String gamemodeText(PlayerResponse.Character character) {
        List<String> modes = character.getGamemode();
        if (modes == null || modes.isEmpty()) return "";
        var set = new HashSet<>(modes);
        if (set.contains("ultimate_ironman")) set.remove("ironman"); // drop ironman if ultimate present

        return " " + Stream.of("hardcore", "craftsman", "ultimate_ironman", "ironman", "hunted")
                .filter(set::contains)
                .map(ICONS::get)
                .collect(Collectors.joining());
    }

    public static final Map<String, String> ICONS;
    static {
        Map<String, String> m = new HashMap<>();
        m.put("hardcore", "§c\uE027");
        m.put("ultimate_ironman", "§b\uE083");
        m.put("ironman", "§6\uE029");
        m.put("craftsman", "§3\uE026");
        m.put("hunted", "§5\uE028");
        m.put("fishing", "Ⓚ");
        m.put("woodcutting", "Ⓒ");
        m.put("mining", "Ⓑ");
        m.put("farming", "Ⓙ");
        m.put("scribing", "Ⓔ");
        m.put("jeweling", "Ⓓ");
        m.put("alchemism", "Ⓛ");
        m.put("cooking", "Ⓐ");
        m.put("weaponsmithing", "Ⓖ");
        m.put("tailoring", "Ⓕ");
        m.put("woodworking", "Ⓘ");
        m.put("armouring", "Ⓗ");
        ICONS = m;
    }


    private static final List<String> ROW1 = List.of(
            "fishing","woodcutting","mining","farming","weaponsmithing","woodworking"
    );
    private static final List<String> ROW2 = List.of(
            "scribing","alchemism","cooking","armouring","tailoring","jeweling"
    );
    private static String professions(PlayerResponse.Character character) {
        Function<List<String>, String> row = list -> list.stream()
                .map(p -> String.format("%s \\2{%d}",
                        ICONS.get(p),
                        character.getProfessions().get(p).getLevel()))
                .collect(Collectors.joining(" \\={|} "));

        return row.apply(ROW1) + "\n" + row.apply(ROW2);
    }

}
