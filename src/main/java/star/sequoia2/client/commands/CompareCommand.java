package star.sequoia2.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.wynntils.core.components.Models;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.services.wynn.guild.GuildResponse;
import star.sequoia2.client.types.command.Command;
import star.sequoia2.client.types.command.suggestions.SuggestionProviders;
import star.sequoia2.utils.cache.GuildCache;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.mojang.brigadier.arguments.StringArgumentType.word;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static star.sequoia2.utils.text.TextUtils.padInvisible;

public class CompareCommand extends Command {

    @Override
    public String getCommandName() {
        return "compare";
    }

    @Override
    public CommandNode<FabricClientCommandSource> register(
            CommandDispatcher<FabricClientCommandSource> dispatcher,
            CommandRegistryAccess access) {

        return dispatcher.register(
                literal(getCommandName())
                        .then(literal("guild") //uncomment this and the end parenthesis to reenable
                                .then(argument("guild1", word())
                                        .suggests(SuggestionProviders.Prefix)      // guild 1
                                        .then(argument("guild2", word())
                                                .suggests(SuggestionProviders.Prefix) // guild 2
                                                .executes(this::compareGuilds))))
        );
    }

    /* ---------------------------------------------------------------------- */

    /** /compare guild <guild1> <guild2> */
    private int compareGuilds(CommandContext<FabricClientCommandSource> ctx) {

        // ── parse & canonicalise ────────────────────────────────────────────
        String raw1 = ctx.getArgument("guild1", String.class).trim();
        String raw2 = ctx.getArgument("guild2", String.class).trim();

        String g1Name = GuildCache.canonicalName(raw1).orElse(raw1);
        String g2Name = GuildCache.canonicalName(raw2).orElse(raw2);

        // ── async fetch both guilds in parallel ─────────────────────────────
        CompletableFuture<GuildResponse> f1 = Services.Guild.getGuild(g1Name);
        CompletableFuture<GuildResponse> f2 = Services.Guild.getGuild(g2Name);

        CompletableFuture.allOf(f1, f2).whenComplete((v, throwable) -> {
            if (throwable != null) {
                SeqClient.error("Error comparing guilds", throwable);
                ctx.getSource().sendError(SeqClient.prefix(
                        Text.translatable("sequoia.command.compare.error")));
                return;
            }

            GuildResponse g1 = f1.join();
            GuildResponse g2 = f2.join();

            if (g1 == null || g2 == null) {
                ctx.getSource().sendError(SeqClient.prefix(
                        Text.translatable("sequoia.command.compare.guildNotFound",
                                g1 == null ? g1Name : g2Name)));
                return;
            }

            showGuildComparison(ctx, g1, g2);
        });

        return 1;  // Brigadier always needs a return
    }

    // todo redo gpt code (it's lowkey not an issue but same as other todo)

    private void showGuildComparison(CommandContext<FabricClientCommandSource> ctx,
                                     GuildResponse g1, GuildResponse g2) {

        /* ---- raw strings ------------------------------------------------ */

        String name1 = g1.getName();
        String name2 = g2.getName();
        String tagRaw1 = "[" + g1.getPrefix() + "]";
        String tagRaw2 = "[" + g2.getPrefix() + "]";

        String lLevel  = Text.translatable("sequoia.command.compare.level").getString();
        String lSR     = Text.translatable("sequoia.command.compare.SR").getString();
        String lOnline = Text.translatable("sequoia.command.compare.online").getString();
        String lTotal  = Text.translatable("sequoia.command.compare.all").getString();

        /* ---- numbers ---------------------------------------------------- */

        String level1 = String.valueOf(g1.getLevel());
        String level2 = String.valueOf(g2.getLevel());
        String sr1    = String.valueOf(g1.getLatestSeasonRating());
        String sr2    = String.valueOf(g2.getLatestSeasonRating());
        String on1    = String.valueOf(g1.getOnline());
        String on2    = String.valueOf(g2.getOnline());
        String tot1   = String.valueOf(g1.getMembers().getTotal());
        String tot2   = String.valueOf(g2.getMembers().getTotal());

        /* ---- pixel widths ------------------------------------------------ */

        TextRenderer tr = MinecraftClient.getInstance().textRenderer;

        int labelPx = Stream.of(lLevel, lSR, lOnline, lTotal)
                .mapToInt(tr::getWidth).max().orElse(0) + tr.getWidth(" ");

        int col1Px = Stream.of(name1, tagRaw1, level1, sr1, on1, tot1)
                .mapToInt(tr::getWidth).max().orElse(0) + tr.getWidth(" ");

        /* ---- styled pieces ---------------------------------------------- */

        MutableText name1Text = Text.literal(name1).styled(selectedTheme.accent1());
        MutableText name2Text = Text.literal(name2).styled(selectedTheme.accent1());

        MutableText tag1Text = buildGuildTag(tagRaw1, name1);
        MutableText tag2Text = buildGuildTag(tagRaw2, name2);

        Function<String, MutableText> num1 =
                s -> Text.literal(s).styled(selectedTheme.accent3());

        Function<String, MutableText> num2 =
                s -> Text.literal(s).styled(selectedTheme.accent3());


        /* ---- rows ------------------------------------------------------- */

        List<MutableText> rows = new ArrayList<>();

        rows.add(buildGuildRow("",      name1, name1Text, name2Text, labelPx, col1Px, tr));
        rows.add(buildGuildRow("",      tagRaw1, tag1Text, tag2Text, labelPx, col1Px, tr));
        rows.add(buildGuildRow(lLevel,  g1.getLevel(), g2.getLevel(),
                labelPx, col1Px, tr));

        rows.add(buildGuildRow(lSR,     g1.getLatestSeasonRating(),
                g2.getLatestSeasonRating(),
                labelPx, col1Px, tr));

        rows.add(buildGuildRow(lOnline, g1.getOnline(), g2.getOnline(),
                labelPx, col1Px, tr));

        rows.add(buildGuildRow(lTotal,  g1.getMembers().getTotal(),
                g2.getMembers().getTotal(),
                labelPx, col1Px, tr));


        /* ---- assemble & send ------------------------------------------- */

        MutableText out = SeqClient.prefix(Text.translatable("sequoia.command.compare.compareGuild").styled(selectedTheme.light()).append(Text.literal("\n")));
        for (int i = 0; i < rows.size(); i++) {
            out.append(rows.get(i));
            if (i < rows.size() - 1) out.append("\n");
        }
        ctx.getSource().sendFeedback(out);
    }

    // --------------------------------------------------------------------- //
    // Helpers                                                               //
    // --------------------------------------------------------------------- //

    /** tag with light brackets + accent2 prefix */
    private static MutableText buildGuildTag(String rawTag, String guildName) {
        String inner = rawTag.substring(1, rawTag.length() - 1); // strip [ ]
        return Text.literal("[")
                .styled(selectedTheme.light())
                .append(Text.literal(inner).styled(s -> s.withColor(Models.Guild.getColor(guildName).asInt())))
                .append(Text.literal("]").styled(selectedTheme.light()));
    }

    /** build one row:  label | col1 | col2  (pixel-padded) */
    private MutableText buildGuildRow(String labelRaw,
                                      String col1Raw,
                                      MutableText col1Styled,
                                      MutableText col2Styled,
                                      int labelPx,
                                      int col1Px,
                                      TextRenderer tr) {

        String lblPad = padInvisible(tr, labelRaw, labelPx);
        MutableText label = Text.literal(lblPad).styled(selectedTheme.dark());

        int need = Math.max(0, col1Px - tr.getWidth(col1Raw));
        String colPad = padInvisible(tr, "", need);
        MutableText col1 = Text.literal(colPad).append(col1Styled);

        MutableText bar = Text.literal(" | ").styled(selectedTheme.normal());

        return label.append(bar)
                .append(col1)
                .append(bar.copy())
                .append(col2Styled);
    }

    private MutableText buildGuildRow(String   labelRaw,
                                      int      value1,
                                      int      value2,
                                      int      labelPx,
                                      int      col1Px,
                                      TextRenderer tr) {

        /* format value2 as "value2 (±diff)" -------------------------- */
        String col1Raw = String.valueOf(value1);
        String col2Raw = fmt(value2, value2 - value1);

        /* style numbers with accent3 -------------------------------- */
        MutableText col1Styled = Text.literal(col1Raw).styled(selectedTheme.accent3());
        MutableText col2Styled = Text.literal(col2Raw).styled(selectedTheme.accent3());

        return buildGuildRow(labelRaw, col1Raw, col1Styled, col2Styled,
                labelPx, col1Px, tr);
    }





    private static String fmt(int value, int diff) {
        return String.format(Locale.ROOT, "%d (%+d)", value, diff);
    } // todo make this work with a config to flip for the people who want that

}
