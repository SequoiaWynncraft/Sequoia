package star.sequoia2.utils;

public class Timer {
    private long time = System.currentTimeMillis();

    public void reset() {
        this.time = System.currentTimeMillis();
    }

    public boolean passed(long ms) {
        return ((System.currentTimeMillis() - this.time)) >= ms;
    }

    public boolean passed(int seconds) {
        return ((System.currentTimeMillis() - this.time)) >= seconds * 1000L;
    }

    public boolean ticksPassed(int gameTicks) {
        return System.currentTimeMillis() - this.time >= ((long) gameTicks) * 50;
    }

    public int getTicksPassed() {
        return (int) ((System.currentTimeMillis() - this.time) / 50);
    }

    public int getPassed() {
        return (int) ((System.currentTimeMillis() - this.time));
    }
}
