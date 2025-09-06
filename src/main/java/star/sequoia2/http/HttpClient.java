package star.sequoia2.http;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import star.sequoia2.client.SeqClient;

import java.net.http.HttpResponse;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentMap;

public class HttpClient {
    private static final int[] OK_STATUS_CODES = {200, 201, 202, 203, 204, 205, 206, 207, 208, 226};
    private static final int POOL_SIZE = 10;
    private static final Gson gson = new GsonBuilder().create();
    private static final ConcurrentMap<java.net.http.HttpClient, Boolean> httpClientPool = Maps.newConcurrentMap();

    protected HttpClient() {
        createClientPool();
    }

    public static HttpClient newHttpClient() {
        return new HttpClient();
    }

    private void createClientPool() {
        for (int i = 0; i < POOL_SIZE; i++) {
            java.net.http.HttpClient client = java.net.http.HttpClient.newHttpClient();
            httpClientPool.put(client, false);
        }
    }

    private boolean isOkStatusCode(int statusCode) {
        for (int okStatusCode : OK_STATUS_CODES) {
            if (statusCode == okStatusCode) {
                return true;
            }
        }
        return false;
    }

    private java.net.http.HttpClient getAvailableClient() {
        for (Map.Entry<java.net.http.HttpClient, Boolean> entry : httpClientPool.entrySet()) {
            java.net.http.HttpClient client = entry.getKey();
            if (Boolean.FALSE.equals(entry.getValue())) {
                httpClientPool.put(client, true);
                return client;
            }
        }
        java.net.http.HttpClient newClient = java.net.http.HttpClient.newHttpClient();
        httpClientPool.put(newClient, true);
        return newClient;
    }

    /**
     * Sends a synchronous request using the provided HttpRequest.
     *
     * @param request the HTTP request to send
     * @param context a short text describing the request context for error logging
     * @return the HTTP response, or null if an error occurred
     */
    private HttpResponse<String> sendSyncRequest(java.net.http.HttpRequest request, String context) {
        java.net.http.HttpClient client = getAvailableClient();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            SeqClient.error("Thread interrupted while " + context, exception);
            return null;
        } catch (Exception exception) {
            SeqClient.error("Failed " + context, exception);
            return null;
        } finally {
            httpClientPool.put(client, false);
        }
    }

    /**
     * Sends an asynchronous request using the provided HttpRequest.
     *
     * @param request the HTTP request to send
     * @param context a short text describing the request context for error logging
     * @return a CompletableFuture with the HTTP response, or null if an error occurred
     */
    private CompletableFuture<HttpResponse<String>> sendAsyncRequest(
            java.net.http.HttpRequest request, String context) {
        try {
            java.net.http.HttpClient client = getAvailableClient();
            CompletableFuture<HttpResponse<String>> future =
                    client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            future.thenRun(() -> httpClientPool.put(client, false));
            return future;
        } catch (Exception exception) {
            SeqClient.error("Failed " + context, exception);
            return null;
        }
    }

    public HttpResponse<String> get(String url) {
        return sendSyncRequest(HttpUtils.newGetRequest(url), "fetching response");
    }

    public CompletableFuture<HttpResponse<String>> getAsync(String url) {
        return sendAsyncRequest(HttpUtils.newGetRequest(url), "fetching async response");
    }

    public HttpResponse<String> post(String url, String body) {
        return sendSyncRequest(HttpUtils.newPostRequest(url, body), "posting response");
    }

    public CompletableFuture<HttpResponse<String>> postAsync(String url, String body) {
        return sendAsyncRequest(HttpUtils.newPostRequest(url, body), "posting async response");
    }

    public <T> T getJson(String url, Class<T> responseType) {
        return getJson(url, responseType, gson);
    }

    public <T> T getJson(String url, Class<T> responseType, Gson gson) {
        HttpResponse<String> response = get(url);
        if (response != null) {
            SeqClient.debug(response.statusCode() + " " + url + " " + response.body());
            if (response.body() != null && isOkStatusCode(response.statusCode())) {
                return gson.fromJson(response.body(), responseType);
            }
        }
        return null;
    }

    public <T> CompletableFuture<T> getJsonAsync(String url, Class<T> responseType) {
        return getJsonAsync(url, responseType, gson);
    }

    public <T> CompletableFuture<T> getJsonAsync(String url, Class<T> responseType, Gson gson) {
        return getAsync(url).thenApply(response -> {
            if (response != null) {
                SeqClient.debug("ASYNC " + response.statusCode() + " " + url + " " + response.body());
                if (response.body() != null && isOkStatusCode(response.statusCode())) {
                    return gson.fromJson(response.body(), responseType);
                }
            }
            return null;
        });
    }
}