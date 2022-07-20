package os.arcadiadevs.playerservers.hubcore.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import os.arcadiadevs.playerservers.hubcore.PSHubCore;
import os.arcadiadevs.playerservers.hubcore.database.DataBase;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ServerCache {

    @Getter @Setter
    private List<Server> servers;

    public ServerCache() {
        this.startTask();
    }

    @SneakyThrows
    private void startTask() {
        new Thread(() -> {
            while(PSHubCore.getInstance().isEnabled()) {
                final List<Server> servers;

                try {
                    servers = DataBase.getServersInfo().get();
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                    return;
                }


                servers.sort((s1, s2) -> {
                    final var p1 = s1.getServerStatus() == ServerStatus.ONLINE;
                    final var p2 = s2.getServerStatus() == ServerStatus.ONLINE;

                    if (p1 && !p2) {
                        return -1;
                    } else if (!p1 && p2) {
                        return 1;
                    } else {
                        return 0;
                    }
                });

                setServers(servers);

                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(PSHubCore.getInstance().getConfig().getInt("gui.menu.cache-time")));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
