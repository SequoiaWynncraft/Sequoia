package star.sequoia2.http;

public final class RateLimiters {
    public static final RateLimiter WYNNCRAFT_API = new RateLimiter(5, 180);
    public static final RateLimiter MOJANG_API = new RateLimiter(5, 60);
    public static final RateLimiter UPDATE_API = new RateLimiter(1, 10);
    private RateLimiters() {}
}