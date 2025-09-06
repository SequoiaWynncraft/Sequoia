package star.sequoia2.client.types.ws.type;

public enum SBinaryDataOpCodeType {
    // Server to client
    INVALID,
    FILE_TRANSFER_START,
    FILE_TRANSFER_DATA,
    FILE_TRANSFER_END,
    // Client to server
    FILE_TRANSFER_READY,
    FILE_TRANSFER_ACK;

    public static SBinaryDataOpCodeType fromValue(int value) {
        if (value < 0 || value >= values().length) {
            return INVALID;
        }
        return values()[value];
    }

    public int getValue() {
        return ordinal();
    }
}