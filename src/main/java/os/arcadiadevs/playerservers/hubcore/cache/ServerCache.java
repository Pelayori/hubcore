package os.arcadiadevs.playerservers.hubcore.cache;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.Getter;
import org.bukkit.scheduler.BukkitRunnable;
import os.arcadiadevs.playerservers.hubcore.controllers.ServersController;
import os.arcadiadevs.playerservers.hubcore.models.CachedServer;

public class ServerCache extends BukkitRunnable {

  @Getter
  private final List<CachedServer> servers;

  private final ServersController serversController;

  public ServerCache(ServersController serversController) {
    this.serversController = serversController;
    this.servers = new ArrayList<>();
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
