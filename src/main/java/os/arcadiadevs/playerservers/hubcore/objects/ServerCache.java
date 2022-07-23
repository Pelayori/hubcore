package os.arcadiadevs.playerservers.hubcore.objects;

import lombok.Getter;
import lombok.Setter;
import os.arcadiadevs.playerservers.hubcore.PSHubCore;
import os.arcadiadevs.playerservers.hubcore.database.DataBase;

import java.util.ArrayList;
import java.util.Map;
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
                Map<String, Object> map = null;
                final var rawServers = DataBase.getServersInfo().get();
                final var servers = new ArrayList<Server>();

                if (PSHubCore.getInstance().getConfig().getBoolean("multi-node")) {
                    map = PSHubCore.getInstance().multinode.getTable("servers").toMap();
                }

                Map<String, Object> finalMap = map;
                rawServers.forEach(server -> {
                    var pingUtil = new PingUtil(PSHubCore.getInstance().getConfig().getBoolean("multi-node") ? finalMap.get(server.getNode()).toString().split(" ")[0].replaceAll(":8080", "") : "localhost", server.getPort());

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

}
