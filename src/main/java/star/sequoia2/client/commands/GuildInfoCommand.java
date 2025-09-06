package star.sequoia2.client.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.services.wynn.guild.GuildResponse;
import star.sequoia2.client.types.command.Command;
import star.sequoia2.client.types.command.suggestions.SuggestionProviders;
import star.sequoia2.utils.cache.GuildCache;

import java.util.List;

import static com.mojang.brigadier.arguments.StringArgumentType.greedyString;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class GuildInfoCommand extends Command {
    @Override
    public String getCommandName() {
        return "guildinfo";
    }

    @Override
    public List<String> getAliases() {
        return List.of("gi");
    }

    @Override
    public CommandNode<FabricClientCommandSource> register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess access) {
        return dispatcher.register(
                literal(getCommandName())
                        .then(argument("guildName", greedyString()).suggests(SuggestionProviders.Guild)
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
                Sequoia2.error("Error looking up guild " + query, t);
                ctx.getSource().sendError(SeqClient.prefix(Text.translatable(
                        "sequoia.command.onlineMembers.errorLookingUpGuildMembers", query)));
                return;
            }
            if (g == null) {
                GuildCache.canonicalName(raw).ifPresentOrElse(
                        canon -> Services.Guild.getGuild(canon).whenComplete((g2, t2) -> {
                            if (g2 != null) showGuild(ctx, g2);
                            else ctx.getSource().sendError(Sequoia2.prefix(
                                    Text.translatable("sequoia.command.onlineMembers.guildNotFound", raw)));
                        }),
                        () -> ctx.getSource().sendError(Sequoia2.prefix(
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
                SeqClient.prefix(g.toPrettyMessage()
                                .append(Text.literal("\n"))
                                .append(g.getOnline() > 0 ? g.getOnlineMembers().toPrettyMessage() : Text.empty())
                )
        );
    }

}
