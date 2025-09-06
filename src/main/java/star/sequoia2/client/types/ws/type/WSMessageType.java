package star.sequoia2.client.types.ws.type;

public enum WSMessageType {
    INVALID,

    G_CHAT_MESSAGE,
    G_CLIENT_COMMAND,
    G_RAID_SUBMISSION,
    G_I_STATE_UPDATE,
    NOT_IMPLEMENTED_1,
    G_IDENTIFY,
    NOT_IMPLEMENTED_2,
    NOT_IMPLEMENTED_3,
    G_AUTH,
    G_IC3H,
    G_RESOURCE_REQUEST,
    G_RESERVED_8,
    G_RESERVED_9,
    G_RESERVED_10,

    S_CHANNEL_MESSAGE,
    S_COMMAND_DATA,
    S_COMMAND_RESULT,
    S_CHAT_MESSAGE_BROADCAST,
    S_COMMAND_PIPE,
    S_RAID_SUBMISSION,
    S_MESSAGE,
    S_SESSION_RESULT,
    S_IC3_DATA,
    S_BINARY_DATA,
    S_RESERVED_7,
    S_REWARD_DATA,
    S_RESERVED_9,
    S_RESERVED_10,

    D_CHANNEL_MESSAGE,
    D_GET_CONNECTED_CLIENT,
    D_SERVER_RESTART,
    D_SERVER_MESSAGE;

    public static WSMessageType fromValue(int value) {
        for (WSMessageType type : values()) {
            if (type.getValue() == value) {
                return type;
            }
        }
        return INVALID;
    }

    public int getValue() {
        return ordinal();
    }
}