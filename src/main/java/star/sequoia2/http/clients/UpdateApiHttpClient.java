package star.sequoia2.http.clients;


import star.sequoia2.http.HttpClient;
import star.sequoia2.http.RateLimiters;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class UpdateApiHttpClient extends HttpClient {
    private UpdateApiHttpClient() {
        super();
    }

    public static UpdateApiHttpClient newHttpClient() {
        return new UpdateApiHttpClient();
    }

    @Override
    public HttpResponse<String> get(String url) {
        RateLimiters.UPDATE_API.acquire();
        try {
            return super.get(url);
        } finally {
            RateLimiters.UPDATE_API.release();
        }
    }

    @Override
    public CompletableFuture<HttpResponse<String>> getAsync(String url) {
        return CompletableFuture.runAsync(RateLimiters.UPDATE_API::acquire)
                .thenCompose(v -> super.getAsync(url))
                .whenComplete((response, throwable) -> RateLimiters.UPDATE_API.release());
    }

    @Override
    public HttpResponse<String> post(String url, String body) {
        RateLimiters.UPDATE_API.acquire();
        try {
            return super.post(url, body);
        } finally {
            RateLimiters.UPDATE_API.release();
        }
    }

    @Override
    public CompletableFuture<HttpResponse<String>> postAsync(String url, String body) {
        return CompletableFuture.runAsync(RateLimiters.UPDATE_API::acquire)
                .thenCompose(v -> super.postAsync(url, body))
                .whenComplete((response, throwable) -> RateLimiters.UPDATE_API.release());
    }
}

