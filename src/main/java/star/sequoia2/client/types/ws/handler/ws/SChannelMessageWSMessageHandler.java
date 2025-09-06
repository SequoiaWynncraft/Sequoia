package star.sequoia2.client.types.ws.handler.ws;

import com.wynntils.utils.mc.McUtils;
import star.sequoia2.accessors.FeaturesAccessor;
import star.sequoia2.accessors.TeXParserAccessor;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.types.ws.handler.WSMessageHandler;
import star.sequoia2.client.types.ws.message.ws.SChannelMessageWSMessage;
import star.sequoia2.features.impl.ws.DiscordChatBridgeFeature;

import java.util.List;

import static star.sequoia2.client.types.ws.WSConstants.GSON;

public class SChannelMessageWSMessageHandler extends WSMessageHandler implements TeXParserAccessor, FeaturesAccessor {
    public SChannelMessageWSMessageHandler(String message) {
        super(GSON.fromJson(message, SChannelMessageWSMessage.class), message);
    }

    private final String MESSAGE_FORMAT = "\\gradient%s{%s}\\={:} \\-{%s}";

    @Override
    public void handle() {

        // todo: add hover, add pillbox format, add message length limiter with more hover shenanigans
        SeqClient.debug(wsMessage.toString());
        if (features().getIfActive(DiscordChatBridgeFeature.class).map(DiscordChatBridgeFeature::isActive).orElse(false)
                && features().getIfActive(DiscordChatBridgeFeature.class).map(DiscordChatBridgeFeature::getSendDiscordtyshi).orElse(false)) {
            SChannelMessageWSMessage sChannelMessageWSMessage = (SChannelMessageWSMessage) wsMessage;
            SChannelMessageWSMessage.Data sChannelMessageWSMessageData =
                    sChannelMessageWSMessage.getSChannelMessageData();
            McUtils.sendMessageToClient(SeqClient.prefix(teXParser().parseMutableText(MESSAGE_FORMAT, formatColorArgs(sChannelMessageWSMessageData.color()), sanitize(sChannelMessageWSMessageData.displayName()), sanitize(sChannelMessageWSMessageData.message()))));
//            String[] displayNameSplit =
//                    sChannelMessageWSMessageData.displayName().split(" ");
//            String playerName = displayNameSplit.length > 1 ? displayNameSplit[1] : displayNameSplit[0];
//            String playerRank = displayNameSplit.length > 1 ? displayNameSplit[0] : null;


//            McUtils.sendMessageToClient();

//            McUtils.sendMessageToClient(Fonts.BannerPill.parse("discord")
//                    .withColor(0x7289DA)
//                    .append(Component.literal(" "))
//                    .append(Fonts.BannerPill.parse(getGuildRank(sChannelMessageWSMessageData.sequoiaRoles()))
//                            .withStyle(ChatFormatting.AQUA))
//                    .append(Fonts.Default.parse(" " + playerName + ": ").withStyle(ChatFormatting.DARK_AQUA))
//                    .append(Fonts.Default.parse(sChannelMessageWSMessageData.message())
//                            .withStyle(ChatFormatting.AQUA)));
        }
    }

    public static String formatColorArgs(List<Integer> colors) {
        StringBuilder sb = new StringBuilder();
        sb.append("{").append(colors.size()).append("}");
        for (Integer color : colors) {
            sb.append("{").append(String.format("%06x", color)).append("}");
        }
        return sb.toString();
    }
}