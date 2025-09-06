package star.sequoia2.http;

import star.sequoia2.client.SeqClient;

import java.util.concurrent.Semaphore;

public class RateLimiter {
    private final Semaphore concurrencySemaphore;
    private final double capacity;
    private double tokens;
    private final double refillRate;
    private long lastRefillTime;

    public RateLimiter(int maxConcurrentRequests, double requestsPerMinute) {
        concurrencySemaphore = new Semaphore(maxConcurrentRequests);
        capacity = requestsPerMinute;
        tokens = requestsPerMinute;
        refillRate = requestsPerMinute / 60000.0;
        lastRefillTime = System.currentTimeMillis();
    }

    private void waitForToken() {
        synchronized (this) {
            while (tokens < 1) {
                refillTokens();
                if (tokens >= 1) {
                    tokens -= 1;
                    return;
                }
                long sleepTime = (long) Math.ceil((1 - tokens) / refillRate);
                try {
                    wait(sleepTime);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    SeqClient.warn("Interrupted while waiting for rate limiter", exception);
                    return;
                }
            }
            tokens -= 1;
        }
    }

    private synchronized void refillTokens() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;
        if (elapsed > 0) {
            tokens = Math.min(capacity, tokens + elapsed * refillRate);
            lastRefillTime = now;
            notifyAll();
        }
    }

    /**
     * Acquires permission for a request. This call blocks until both a token is available
     * (ensuring we respect the global rate) and a concurrency permit is available (ensuring
     * only a limited number of concurrent requests).
     */
    public void acquire() {
        try {
            waitForToken();
            concurrencySemaphore.acquire();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            SeqClient.warn("Interrupted while waiting for rate limiter", exception);
        }
    }

    /**
     * Releases a previously acquired concurrency permit.
     */
    public void release() {
        concurrencySemaphore.release();
    }
}