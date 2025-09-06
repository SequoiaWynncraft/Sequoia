package star.sequoia2.client.types.ws;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import star.sequoia2.client.types.ws.json.OffsetDateTimeAdapter;

import java.time.OffsetDateTime;

public final class WSConstants {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(OffsetDateTime.class, new OffsetDateTimeAdapter())
            .create();

    private WSConstants() {}
}