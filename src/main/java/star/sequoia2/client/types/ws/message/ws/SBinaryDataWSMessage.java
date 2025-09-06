package star.sequoia2.client.types.ws.message.ws;

import com.google.gson.annotations.SerializedName;
import star.sequoia2.client.types.ws.message.WSMessage;
import star.sequoia2.client.types.ws.type.SBinaryDataOpCodeType;
import star.sequoia2.client.types.ws.type.WSMessageType;

import java.time.OffsetDateTime;
import java.util.Arrays;

import static star.sequoia2.client.types.ws.WSConstants.GSON;


public class SBinaryDataWSMessage extends WSMessage {
    public SBinaryDataWSMessage(Data data) {
        super(WSMessageType.S_BINARY_DATA.getValue(), GSON.toJsonTree(data));
    }

    public Data getSBinaryData() {
        return GSON.fromJson(getData(), Data.class);
    }

    public record Data(
            @SerializedName("transfer_id") String transferId,
            @SerializedName("protocol_version") int protocolVersion,
            @SerializedName("op_code") SBinaryDataOpCodeType opCode,
            String header,
            Metadata metadata,
            Alignment alignment,
            int sequence,
            boolean done,
            int remaining,
            @SerializedName("frame_hash") String frameHash,
            byte[] payload,
            @SerializedName("total_hash") String totalHash,
            OffsetDateTime timestamp,
            SBinaryDataError[] errors) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Data data = (Data) o;
            return protocolVersion == data.protocolVersion
                    && sequence == data.sequence
                    && done == data.done
                    && remaining == data.remaining
                    && opCode == data.opCode
                    && transferId.equals(data.transferId)
                    && header.equals(data.header)
                    && metadata.equals(data.metadata)
                    && alignment.equals(data.alignment)
                    && frameHash.equals(data.frameHash)
                    && totalHash.equals(data.totalHash)
                    && timestamp.equals(data.timestamp)
                    && Arrays.equals(errors, data.errors);
        }

        @Override
        public int hashCode() {
            return transferId.hashCode()
                    + protocolVersion
                    + opCode.hashCode()
                    + header.hashCode()
                    + metadata.hashCode()
                    + alignment.hashCode()
                    + sequence
                    + (done ? 1 : 0)
                    + remaining
                    + frameHash.hashCode()
                    + Arrays.hashCode(payload)
                    + totalHash.hashCode()
                    + timestamp.hashCode()
                    + Arrays.hashCode(errors);
        }

        @Override
        public String toString() {
            return "Data{" + "transferId='" + transferId + '\'' + ", protocolVersion=" + protocolVersion + ", opCode="
                    + opCode + ", header='" + header + '\'' + ", metadata=" + metadata + ", alignment=" + alignment
                    + ", sequence=" + sequence + ", done=" + done + ", remaining=" + remaining + ", frameHash='"
                    + frameHash + '\'' + ", payload=" + Arrays.toString(payload) + ", totalHash='" + totalHash + '\''
                    + ", timestamp="
                    + timestamp + ", errors=" + Arrays.toString(errors) + '}';
        }
    }

    public record Metadata(String filename, int length, @SerializedName("mime_type") String mimeType) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Metadata metadata = (Metadata) o;
            return length == metadata.length
                    && filename.equals(metadata.filename)
                    && mimeType.equals(metadata.mimeType);
        }

        @Override
        public int hashCode() {
            return filename.hashCode() + length + mimeType.hashCode();
        }

        @Override
        public String toString() {
            return "Metadata{" + "filename='" + filename + '\'' + ", length=" + length + ", mimeType='" + mimeType
                    + '\'' + '}';
        }
    }

    public record Alignment(int offset, int length) {
        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Alignment alignment = (Alignment) o;
            return offset == alignment.offset && length == alignment.length;
        }

        @Override
        public int hashCode() {
            return offset + length;
        }

        @Override
        public String toString() {
            return "Alignment{" + "offset=" + offset + ", length=" + length + '}';
        }
    }

    public record SBinaryDataError(int sequence, int error, @SerializedName("error_string") String errorString) {}
}