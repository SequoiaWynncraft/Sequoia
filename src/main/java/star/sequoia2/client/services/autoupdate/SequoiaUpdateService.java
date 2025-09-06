package star.sequoia2.client.services.autoupdate;

import com.google.gson.JsonObject;
import com.wynntils.utils.FileUtils;
import star.sequoia2.client.SeqClient;
import star.sequoia2.client.types.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public final class SequoiaUpdateService extends Service {
    SequoiaUpdateVerifyService sequoiaUpdateVerifyService = new SequoiaUpdateVerifyService();
    private static final int versionInt = SeqClient.getVersionInt();
    private static final String LATESTURL = "cdn.sequoia.ooo/latest";
    private float updateProgress = -1.0F;
    private static final File UPDATES_FOLDER = SeqClient.getModStorageDir("updates");
    JsonObject UpdateInfo = new JsonObject();


    public SequoiaUpdateService() {
        super(List.of());
    }
    private String tryGetHash() throws ExecutionException, InterruptedException {
        try {
            return sequoiaUpdateVerifyService.getHash();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
    private int tryGetVersionInt() throws ExecutionException, InterruptedException {
        try {
            return sequoiaUpdateVerifyService.getVersionInt();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean isLatest () throws ExecutionException, InterruptedException {
        try {
            return versionInt == tryGetVersionInt();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public CompletableFuture<UpdateResult> tryUpdate() throws ExecutionException, InterruptedException {
        CompletableFuture<UpdateResult> future = new CompletableFuture();
    if (isLatest()){future.complete(
            UpdateResult.ALREADY_ON_LATEST);
            return future;
    }
            File localUpdateFile = this.getUpdateFile();
            if (localUpdateFile.exists()) {
                String localUpdateMd5 = FileUtils.getMd5(localUpdateFile);
                if (Objects.equals(localUpdateMd5, sequoiaUpdateVerifyService.getHash())) {
                    future.complete(UpdateResult.UPDATE_PENDING);
                    return future;
                }

                FileUtils.deleteFile(localUpdateFile);
            }

            Executors.newSingleThreadExecutor().submit(() -> this.tryFetchingUpdate(LATESTURL, future));


        return future;
    }


    private void tryFetchingUpdate(String LATESTURL, CompletableFuture<UpdateResult> future){
        File oldJar = SeqClient.getModJar();
        File newJar = this.getUpdateFile();
        try{
            URL downloadUrl = URI.create(LATESTURL).toURL();
            URLConnection connection = downloadUrl.openConnection();
            FileUtils.downloadFileWithProgress(connection, newJar, (progress) -> this.updateProgress = progress);
            this.updateProgress = -1.0F;
            String downloadedUpdateFileMd5 = FileUtils.getMd5(newJar);
            if (!(Objects.equals(downloadedUpdateFileMd5, Services.VerifyUpdate.getUpdateInfo().get().getMD5()) || isLatest()) ) {
                newJar.delete();
                future.complete(UpdateResult.ERROR);
                SeqClient.error("Downloaded update file is corrupted!");
                return;
            }
            future.complete(UpdateResult.SUCCESSFUL);
            SeqClient.info("Successfuly finished downloading");
            this.addShutdownHook(oldJar, newJar);

        }catch (Exception e){
            SeqClient.error("Failed fetching update");
            future.complete(UpdateResult.ERROR);
        }
    }
    private File getUpdateFile() {
        File updatesDir = new File(UPDATES_FOLDER.toURI());
        FileUtils.mkdir(updatesDir);
        return new File(updatesDir, "sequpdate.jar");
    }
    private void addShutdownHook(File oldJar, File newJar) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (oldJar == null || !oldJar.exists() || oldJar.isDirectory()) {
                    SeqClient.warn("Mod jar file not found or incorrect.");
                    return;
                }

                FileUtils.copyFile(newJar, oldJar);
                newJar.delete();
                SeqClient.info("Successfully applied update!");
            } catch (IOException e) {
                SeqClient.error("Cannot apply update!", e);
            }

        }));
    }
}
