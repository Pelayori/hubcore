package os.arcadiadevs.playerservers.hubcore.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import os.arcadiadevs.playerservers.hubcore.PSHubCore;
import os.arcadiadevs.playerservers.hubcore.database.DataBase;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class ServerCache implements Runnable {

    @Getter @Setter
    private List<Server> servers;

    @Override
    public void run() {
        final List<Server> servers;

        try {
            servers = DataBase.getServersInfo().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return;
        }

        servers.forEach(server -> {
            server.setCachedData(server.getData());
            server.setCachedStatus(server.getServerStatus());
        });

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
    }

}
