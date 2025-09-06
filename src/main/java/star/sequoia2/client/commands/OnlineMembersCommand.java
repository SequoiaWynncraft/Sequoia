package star.sequoia2.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import com.wynntils.core.components.Models;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.services.wynn.guild.GuildResponse;
import star.sequoia2.client.types.Services;
import star.sequoia2.client.types.command.Command;
import star.sequoia2.client.types.command.suggestions.GuildSuggestionProvider;
import star.sequoia2.features.impl.Settings;
import star.sequoia2.utils.cache.GuildCache;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

@Deprecated
public class OnlineMembersCommand extends Command implements FeaturesAccessor {
    @Override
    public String getCommandName() {
        return "members";
    }

    @Override
    public List<String> getAliases() {
        return List.of("ms", "mem");
    }

    @Override
    public CommandNode<FabricClientCommandSource> register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        GuildCache.init();
        return dispatcher.register(
                literal(getCommandName())
                        .then(argument("guildName", greedyString()).suggests(new GuildSuggestionProvider())
                                .executes(this::lookupGuild
                                )
                        )
        );
    }

    private int lookupGuild(CommandContext<FabricClientCommandSource> ctx) {
        final String raw = ctx.getArgument("guildName", String.class).trim();

        String query = GuildCache.canonicalName(raw).orElse(raw);

        Services.Guild.getGuild(query).whenComplete((g, t) -> {
            if (t != null) {
                SeqClient.error("Error looking up guild " + query, t);
                ctx.getSource().sendError(SeqClient.prefix(Text.translatable(
                        "sequoia.command.onlineMembers.errorLookingUpGuildMembers", query)));
                return;
            }
            if (g == null) {
                GuildCache.canonicalName(raw).ifPresentOrElse(
                        canon -> Services.Guild.getGuild(canon).whenComplete((g2, t2) -> {
                            if (g2 != null) showGuild(ctx, g2);
                            else ctx.getSource().sendError(SeqClient.prefix(
                                    Text.translatable("sequoia.command.onlineMembers.guildNotFound", raw)));
                        }),
                        () -> ctx.getSource().sendError(SeqClient.prefix(
                                Text.translatable("sequoia.command.onlineMembers.guildNotFound", raw)))
                );
            } else {
                showGuild(ctx, g);
            }
        });
        return 1;
    }

    private void showGuild(CommandContext<FabricClientCommandSource> ctx, GuildResponse g) {
        ctx.getSource().sendFeedback(
                SeqClient.prefix(
                        Text.literal(g.getName()).styled(features().get(Settings.class).map(settings -> settings.getTheme().get().getTheme().accent1()).orElse(style -> style))
                                .append(Text.literal(" [").styled(features().get(Settings.class).map(settings -> settings.getTheme().get().getTheme().light()).orElse(style -> style)))
                                .append(Text.literal(g.getPrefix()).styled(s -> {return s.withColor(Models.Guild.getColor(g.getName()).asInt());}))
                                .append(Text.literal("]: ").styled(features().get(Settings.class).map(settings -> settings.getTheme().get().getTheme().light()).orElse(style -> style)))
                                .append(Text.literal(g.getOnline() + "/" + g.getMembers().getTotal() + " ")
                                        .styled(features().get(Settings.class).map(settings -> settings.getTheme().get().getTheme().accent1()).orElse(style -> style)))
                                .append(Text.translatable("sequoia.command.onlineMembers.showingGuildMembers")
                                        .styled(features().get(Settings.class).map(settings -> settings.getTheme().get().getTheme().light()).orElse(style -> style)))
                                .append(Text.literal("\n"))
                                .append(g.getOnline() > 0 ? g.getOnlineMembers().toPrettyMessage() : Text.empty())
                )
        );
    }

}
