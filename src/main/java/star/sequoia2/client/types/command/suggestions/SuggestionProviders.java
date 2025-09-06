package star.sequoia2.client.types.command.suggestions;

public final class SuggestionProviders {
    public static final GuildSuggestionProvider Guild = new GuildSuggestionProvider();
    public static final GuildPrefixSuggestionProvider Prefix = new GuildPrefixSuggestionProvider();
    public static final PlayerSuggestionProvider Player = new PlayerSuggestionProvider();
    public static final DeleteTokenSuggestionProvider DELETE_TOKEN_SUGGESTION_PROVIDER = new DeleteTokenSuggestionProvider();
}
