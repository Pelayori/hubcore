package os.arcadiadevs.playerservers.hubcore.cache;

import java.util.HashSet;
import lombok.Getter;
import os.arcadiadevs.playerservers.hubcore.controllers.ServersController;
import os.arcadiadevs.playerservers.hubcore.models.CachedServer;

public class ServerCache implements Runnable {

  @Getter
  private final HashSet<CachedServer> servers;

  private final ServersController serversController;

  public ServerCache(ServersController serversController) {
    this.serversController = serversController;
    this.servers = new HashSet<>();
  }

  /**
   * Refreshes the cache.
   */
  @Override
  public void run() {
    HashSet<CachedServer> newCache = new HashSet<>();
    serversController.getServers().forEach(server -> newCache.add(new CachedServer(server)));
    servers.clear();
    servers.addAll(newCache);
  }
}
