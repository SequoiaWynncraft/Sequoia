package star.sequoia2.http;

import star.sequoia2.http.clients.MojangApiHttpClient;
import star.sequoia2.http.clients.UpdateApiHttpClient;
import star.sequoia2.http.clients.WynncraftApiHttpClient;

public final class HttpClients {
    public static final WynncraftApiHttpClient WYNNCRAFT_API = WynncraftApiHttpClient.newHttpClient();
    public static final MojangApiHttpClient MOJANG_API = MojangApiHttpClient.newHttpClient();
    public static final UpdateApiHttpClient UPDATE_API =UpdateApiHttpClient.newHttpClient();

    private HttpClients() {}
}