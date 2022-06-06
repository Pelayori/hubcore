package os.arcadiadevs.playerservers.hubcore.objects;

import lombok.Getter;
import lombok.Setter;
import os.arcadiadevs.playerservers.hubcore.PSHubCore;
import os.arcadiadevs.playerservers.hubcore.database.DataBase;
import os.arcadiadevs.playerservers.hubcore.utils.PingUtil;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class ServerCache {

    @Getter @Setter
    private ArrayList<Server> servers;

    public ServerCache() {
        this.startTask();
    }

    private void startTask() {
        new Thread(() -> {
            while(PSHubCore.getInstance().isEnabled()) {
                final var database = new DataBase();
                final var map = PSHubCore.getInstance().multinode.getTable("servers").toMap();
                final var rawServers = database.getServersInfo();
                final var servers = new ArrayList<Server>();

                rawServers.forEach(server -> {
                    var pingUtil = new PingUtil(map.get(server.getNode()).toString().split(" ")[0].replaceAll(":8080", ""), server.getPort());

                    var _server = new Server();

                    _server.setAddress(pingUtil.getHost());
                    _server.setPort(server.getPort());
                    _server.setOnline(pingUtil.isOnline());
                    _server.setUniqueId(server.getServerId());
                    _server.setMotd(pingUtil.isOnline() ? pingUtil.getData().getMOTD() : "&cOffline");
                    _server.setOwner(server.getPlayerName());
                    _server.setNode(server.getNode());
                    _server.setPlayers(pingUtil.isOnline() ? pingUtil.getData().getOnline() : 0);
                    _server.setMaxPlayers(pingUtil.isOnline() ? pingUtil.getData().getMax() : 0);

                    servers.add(_server);
                });

                servers.sort((s1, s2) -> {
                    final var p1 = s1.isOnline();
                    final var p2 = s2.isOnline();

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

    @Getter
    @Setter
    public static class Server {

        private String uniqueId;
        private String motd;
        private String owner;
        private String address;
        private String node;

        private int players;
        private int maxPlayers;
        private int port;

        private boolean online;

    }

}
