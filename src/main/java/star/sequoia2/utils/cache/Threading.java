package star.sequoia2.utils.cache;

public class Threading implements Runnable {
    @Override
    public void run(){
        GuildCache.init();
        SequoiaMemberCache.init();
    }
}
