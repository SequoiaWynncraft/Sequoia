package star.sequoia2.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.wynntils.core.components.Models;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import org.apache.commons.lang3.StringUtils;
import star.sequoia2.accessors.TeXParserAccessor;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.services.wynn.player.PlayerResponse;
import star.sequoia2.client.types.Services;
import star.sequoia2.client.types.command.Command;
import star.sequoia2.client.types.command.suggestions.SuggestionProviders;
import star.sequoia2.utils.MinecraftUtils;
import star.sequoia2.utils.TimeUtils;
import star.sequoia2.utils.text.TextUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static star.sequoia2.client.commands.PlayerCharactersCommand.ICONS;

public class PlayerInfoCommand extends Command implements TeXParserAccessor {
    @Override
    public String getCommandName() {
        return "playerinfo";
    }

    @Override
    public List<String> getAliases() {
        return List.of("pi");
    }

    @Override
    public CommandNode<FabricClientCommandSource> register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        return dispatcher.register(
                literal(getCommandName())
                        .then(argument("player", word()).suggests(SuggestionProviders.Player)
                                .executes(this::playerInfo
                                )
                        )
        );
    }

    private int playerInfo(CommandContext<FabricClientCommandSource> ctx) {
        String username = ctx.getArgument("player", String.class);
        if (StringUtils.isBlank(username) || !MinecraftUtils.isValidUsername(username)) {
            ctx.getSource()
                    .sendError(SeqClient.prefix(Text.translatable("sequoia.command.invalidUsername")));
            return 0;
        } else {
            Services.Player.getPlayerFullResult(username).whenComplete((playerResponse, throwable) -> {
                if (throwable != null) {
                    SeqClient.error("Error looking up player: " + username, throwable);
                    ctx.getSource()
                            .sendError(SeqClient.prefix(Text.translatable(
                                    "sequoia.command.playerInfo.errorLookingUpPlayer", username)));
                } else {
                    if (playerResponse == null
                            || playerResponse.getGlobalData() == null
                            || playerResponse.getGlobalData().getRaids() == null) {
                        ctx.getSource()
                                .sendError(SeqClient.prefix(Text.translatable(
                                        "sequoia.command.playerRaids.playerNotFound", username)));
                    } else {
                        PlayerResponse.Guild guild = playerResponse.getGuild();
                        String character = playerResponse.getActiveCharacter();
                        String Rank;
                        if(Objects.equals(playerResponse.getRank(), "Player")){
                        if (playerResponse.getSupportRank() == null){
                            Rank = "";
                        }else{
                            SeqClient.info(playerResponse.getRank());
                        switch(playerResponse.getSupportRank().toUpperCase(Locale.ROOT)) {
                            case "VIP":
                                Rank = "\uE023";
                                break;
                            case "VIPPLUS":
                                Rank = "\uE024";
                                break;
                            case "HERO":
                                Rank = "\uE01B";
                                break;
                            case "CHAMPION":
                                Rank = "\uE017";
                                break;
                            case "HEROPLUS":
                                Rank = "\uE08A";
                                break;
                            default:
                                Rank = " ";
                                break;
                        }
                        }

                            /*
                             *  = vip
                             *  = vip +
                             *  = hero
                             *  = hero +
                             *  = champion
                             * -------------------------
                             *  = music
                             *  = owner
                             *  = quality assurance
                             *  = web dev
                             *  = admin
                             *  = artist
                             *  = builder
                             *  = commandblocker
                             *  = developer
                             *  = game master
                             *  = hybrid
                             *  = item
                             *  = media
                             *  = moderator
                             */
                        }
                            else {
                                switch (playerResponse.getRank()){
                                    case "to replace later": // not sure - Originally Music tag, music tag is now unused I think
                                        Rank = "\uE020";
                                        break;
                                    case "Owner":
                                        Rank = "\uE021";
                                        break;
                                    case "Quality Assurance": // not sure
                                        Rank = "\uE022";
                                        break;
                                    case "WebDev":
                                        Rank = "\uE025";
                                        break;
                                    case "Administrator":
                                        Rank = "\uE014";
                                        break;
                                    case "Music": // It was normally Artist here but in wynn api Artist tag is music rank
                                        Rank = "\uE015";
                                        break;
                                    case "Builder":
                                        Rank = "\uE016";
                                        break;
                                    case "Commandblocker": // not sure - many said unused
                                        Rank = "\uE018";
                                        break;
                                    case "Developer":
                                        Rank = "\uE019";
                                        break;
                                    case "Game Master":
                                        Rank = "\uE01A";
                                        break;
                                    case "Hybrid":
                                        Rank = "\uE01C";
                                        break;
                                    case "Item":
                                        Rank = "\uE01D";
                                        break;
                                    case "Media":
                                        Rank = "\uE01E";
                                        break;
                                    case "Moderator":
                                        Rank = "\uE01F";
                                        break;
                                    default:
                                        Rank = " ";
                                        break;
                                }
                        }

                        ctx.getSource()
                                .sendFeedback(
                                        SeqClient.prefix(
                                                teXParser().parseMutableText(I18n.translate("sequoia.command.playerInfo.showingPlayerInfoIntro",
                                                        playerResponse.getLegacyRankColour() != null ? playerResponse.getLegacyRankColour().getMain().substring(1) : "ffffff", Rank + " ",
                                                        playerResponse.getUsername())
                                                ).append(guild != null ?
                                                teXParser().parseMutableText(I18n.translate("sequoia.command.playerInfo.showingPlayerInfoGuild",
                                                        TextUtils.upperfirst(guild.getRank()),
                                                        guild.getName(), guild.getName(), guild.getName(), String.format("%06X", Models.Guild.getColor(guild.getName()).asInt() & 0xFFFFFF), guild.getPrefix()
                                                        )) : Text.empty()
                                                ).append(playerResponse.getCharacters() != null ?
                                                teXParser().parseMutableText(I18n.translate("sequoia.command.playerInfo.showingPlayerInfoBody",
                                                        playerResponse.getUsername(), playerResponse.getUsername(), playerResponse.getGlobalData().getRaids().getTotal(),
                                                        playerResponse.getUsername(), playerResponse.getUsername(), playerResponse.getGlobalData().getWars(), playerResponse.getRanking().get("warsCompletion"),
                                                        playerResponse.getGlobalData().getKilledMobs(), playerResponse.getGlobalData().getChestsFound(), professions(playerResponse.getCharacters()),
                                                        TimeUtils.toReadable(playerResponse.getFirstJoin()), (int) playerResponse.getPlaytime() + " hours"
                                                        )): teXParser().parseMutableText(I18n.translate("sequoia.command.playerInfo.showingplayerInfoBodyError",""))
                                                ).append(
                                                playerResponse.isOnline()
                                                ?
                                                        playerResponse.getCharacters() != null ?
                                                            teXParser().parseMutableText(I18n.translate("sequoia.command.playerInfo.showingPlayerInfoOnline",
                                                                PlayerCharactersCommand.characterText(playerResponse.getCharacters().get(character)), playerResponse.getServer()
                                                        )): Text.empty()
                                                :
                                                        playerResponse.getLastJoin() != null?
                                                            teXParser().parseMutableText(I18n.translate("sequoia.command.playerInfo.showingPlayerInfoOffline",
                                                                TimeUtils.since(playerResponse.getLastJoin())
                                                        )): teXParser().parseMutableText(I18n.translate("sequoia.command.playerInfo.showingplayerInfoOnlineError", ""))
                                                )
                                        )
                                );
                    }
                }

            });
        }

//                        ctx.getSource().sendFeedback(Text.translatable("sequoia.command.invalidUsername").formatted(Formatting.BLUE));
        return 1;
    }


    private static final List<String> ROW1 = List.of(
            "fishing","woodcutting","mining","farming","weaponsmithing","woodworking"
    );
    private static final List<String> ROW2 = List.of(
            "scribing","alchemism","cooking","armouring","tailoring","jeweling"
    );

    private String professions(Map<String, PlayerResponse.Character> characters) {
        Map<String, Integer> maxLvl = new HashMap<>();
        characters.values().forEach(c -> {
            var profs = c.getProfessions();
            if (profs == null) return;
            profs.forEach((name, prof) -> {
                if (prof != null)
                    maxLvl.merge(name, prof.getLevel(), Math::max);
            });
        });
        Function<List<String>, String> row = list -> list.stream()
                .map(p -> String.format("%s \\2{%d}",
                        ICONS.get(p),
                        maxLvl.getOrDefault(p, 0)))
                .collect(Collectors.joining(" \\={|} "));

        return row.apply(ROW1) + "\n" + row.apply(ROW2);
    }


}
