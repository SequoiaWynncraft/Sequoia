package star.sequoia2.client.services.autoupdate;

import star.sequoia2.client.SeqClient;
import star.sequoia2.client.types.Service;
import star.sequoia2.http.HttpClients;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SequoiaUpdateVerifyService extends Service {
    private static final String INFO_URL = "cdn.sequoia.ooo/hash";

    public SequoiaUpdateVerifyService() {
        super(List.of());
    }

        public CompletableFuture<UpdateServerResponse> getUpdateInfo() {
            return HttpClients.UPDATE_API.getJsonAsync(INFO_URL, UpdateServerResponse.class).thenCompose(UpdateServerResponse -> {
                if (UpdateServerResponse == null) {
                    return getUpdateInfo();
                }
                SeqClient.info("Received Update Info.");
                return CompletableFuture.completedFuture(UpdateServerResponse);
            });
        }

    public int getVersionInt() throws ExecutionException, InterruptedException {
        try {
            return getUpdateInfo().get().getVersionInt();
        }catch (ExecutionException e) {}
        return -1;
    }
    public String getHash() throws ExecutionException, InterruptedException {
        try{
        return getUpdateInfo().get().getMD5();
        } catch (ExecutionException e) {}
        return "";
        }
    }
