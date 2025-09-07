package star.sequoia2.features.impl.ws;

import lombok.Getter;
import star.sequoia2.features.ToggleFeature;
import star.sequoia2.settings.types.BooleanSetting;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordChatBridgeFeature extends ToggleFeature {

    @Getter
    BooleanSetting sendDiscordMessageToChat = settings().bool("ShowDiscordInChat", "test", true);

    protected static final Pattern GUILD_CHAT_PATTERN = Pattern.compile(
            "^[\\s\\p{C}\\p{M}\\p{So}\\p{Sk}\\p{P}\\p{Z}\\p{S}\\p{L}\\p{N}§[0-9a-fk-or<]*]*?([^:]+):\\s*(.+)$",
            Pattern.MULTILINE);
    private static final Pattern NICKNAME_PATTERN =
            Pattern.compile("(.*?)'s? real name is (.*)", Pattern.MULTILINE);
    private static final Pattern ITEM_PATTERN =
             Pattern.compile("\uDB80\uDC00", Pattern.MULTILINE);

    public DiscordChatBridgeFeature() {
        super("DiscordChatBridge", "forwards messages to discord", true);
    }

    @Override
    public void onActivate() {
//        ChatHudMessageEvents.TEXT.register(message -> {
//            StyledText styledText = StyledText.fromComponent(message);
//
//            ChatMessageReceivedEvent chat;
//            if (!RecipientType.GUILD.matchPattern(styledText, MessageType.FOREGROUND)) {
//                return;
//            }
//
//            StyledText messageTextWithoutNewLines = styledText
//                    .replaceAll(" \n", "")
////                    .replaceAll("\uDAFF\uDFFC\uE001\uDB00\uDC06\\s+", "")
//                    .replaceAll("\\s{2,}", "")
//                    .replaceAll("\uDAFF\uDFFC\uE001\uDB00\uDC06", "");
//            MutableText messageComponent = messageTextWithoutNewLines.getComponent();
//
//            if (messageTextWithoutNewLines.isBlank()) {
//                return;
//            }
//
//            if (Sequoia2.getWebSocketFeature() == null
//                    || !Sequoia2.getWebSocketFeature().isEnabled()) {
//                return;
//            }
//
//            // todo: authenticated removed cuz maybe not needed
//
//            if (!Sequoia2.CONFIG.discordChatBridgeFeature.enabled()) {
//                return;
//            }
//
//            if (!Sequoia2.CONFIG.discordChatBridgeFeature.sendInGameGuildChatMessagesToDiscord()) {
//                return;
//            }
//
//            String messageStringWithoutFormatting = messageTextWithoutNewLines.getStringWithoutFormatting();
//            Matcher guildChatMatcher = GUILD_CHAT_PATTERN.matcher(messageStringWithoutFormatting);
//            Map<String, List<String>> nameMap = Maps.newHashMap();
//            String nickname;
//            String username = null;
//
//            createRealNameMap(messageComponent, nameMap);
//
//            try {
//                if (guildChatMatcher.matches()) {
//                    nickname = guildChatMatcher
//                            .group(1)
//                            .replaceAll("[^\\p{Print}]", "")
//                            .trim();
//                    String guildMessage = guildChatMatcher
//                            .group(2)
//                            .replaceAll("[^\\p{Print}]", "")
//                            .trim();
//
//                    if (nickname != null && nameMap.containsKey(nickname)) {
//                        username = nameMap.get(nickname).getFirst();
//                    }
//
//                    if (username == null) {
//                        username = nickname;
//                        nickname = null;
//                    }
//
//                    GChatMessageWSMessage gChatMessageWSMessage = new GChatMessageWSMessage(new GChatMessageWSMessage.Data(
//                            username, nickname, guildMessage, TimeUtils.wsTimestamp(), McUtils.playerName()));
//                    Sequoia2.getWebSocketFeature().sendMessage(gChatMessageWSMessage);
//                }
//            } catch (Exception exception) {
//                Sequoia2.error("Failed to send guild chat message to Discord", exception);
//            }
//        if(isItemUnicode(messageStringWithoutFormatting)) {
//            //format message to get fourth unicode and the unicode block - I think we can get away with string
//            // why fourth unicode? From testing I have realized that the fourth unicode shows which type of item it is
//            // this is important because the itemtransformers all have specific areas,
//            String formattedMessage = messageStringWithoutFormatting; // all unicode
//            String fourthUnicode = messageStringWithoutFormatting; // fourth unicode
//            switch (fourthUnicode){
//                case ("\udd00"):
//                    //import GearItemTransformer.decodeitem()
//
//
//
//
//            }
//
//        }
//        });
    }
    private static boolean isItemUnicode (String messageStringWithoutFormatting){
        Matcher itemUnicodeMatcher = ITEM_PATTERN.matcher(messageStringWithoutFormatting);
        try{
            if(itemUnicodeMatcher.matches()){
                return true;
            }
            else return false;

        }catch (Exception exception){
            return false;
        }

    }

//    private static void createRealNameMap(Text message, Map<String, List<String>> nameMap) {
//        if (!messageHasNickHoverDeep(message)) {
//            return;
//        }
//
//        if (!message.getSiblings().isEmpty()) {
//            for (Text siblingMessage : message.getSiblings()) {
//                if (messageHasNickHoverDeep(siblingMessage)) {
//                    createRealNameMap(siblingMessage, nameMap);
//                    tryToAddRealName(siblingMessage, nameMap);
//                }
//            }
//        }
//    }
//
//    private static void tryToAddRealName(Text message, Map<String, List<String>> nameMap) {
//        if (messageHasNickHover(message)) {
//            HoverEvent hover = message.getStyle().getHoverEvent();
//            if (hover == null) {
//                return;
//            }
//            if (hover.getValue(hover.getAction()) instanceof Text hoverText) {
//                Matcher nicknameMatcher = NICKNAME_PATTERN.matcher(hoverText.getString());
//                if (!nicknameMatcher.matches()) {
//                    return;
//                }
//
//                String nickname = nicknameMatcher.group(1);
//                String username = nicknameMatcher.group(2);
//
//                Sequoia2.debug("Mapped nickname '" + nickname + "' to username '" + username + "'");
//                nameMap.computeIfAbsent(nickname, k -> new ArrayList<>()).add(username);
//            }
//        }
//    }
//
//    private static boolean messageHasNickHoverDeep(Text message) {
//        boolean hasNick = false;
//        if (!message.getSiblings().isEmpty()) {
//            for (Text messageSibling : message.getSiblings()) {
//                hasNick = hasNick || messageHasNickHoverDeep(messageSibling);
//            }
//        } else {
//            return messageHasNickHover(message);
//        }
//        return hasNick;
//    }
//
//    private static boolean messageHasNickHover(Text message) {
//        HoverEvent hover = message.getStyle().getHoverEvent();
//        if (hover != null && hover.getValue(hover.getAction()) instanceof Text hoverText) {
//            String hoverString = hoverText.getString();
//            return hoverString.contains("real username");
//        }
//        return false;
//    }
}
