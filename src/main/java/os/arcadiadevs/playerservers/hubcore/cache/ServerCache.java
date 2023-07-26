package os.arcadiadevs.playerservers.hubcore.cache;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.controllers.ServersController;
import os.arcadiadevs.playerservers.hubcore.dto.ServerRecord;
import os.arcadiadevs.playerservers.hubcore.utils.BungeeUtil;

public class ServerCache extends BukkitRunnable implements PluginMessageListener {

  @Getter
  private List<ServerRecord> servers;

  private final FileConfiguration config;

  private final ServersController serversController;

  private final PsHubCore instance;

  public ServerCache(ServersController serversController, FileConfiguration config, PsHubCore instance) {
    this.serversController = serversController;
    this.servers = new ArrayList<>();
    this.config = config;
    this.instance = instance;
  }

  /**
   * Refreshes the cache.
   */
  @SneakyThrows
  @Override
  public void run() {
    while (instance.isEnabled()) {
      Player player = Bukkit.getOnlinePlayers().stream().findAny().orElse(null);

      if (player == null) {
        Thread.sleep(1000);
        continue;
      }

      BungeeUtil.checkIfOnline(player);
      Thread.sleep(config.getLong("cache.cache-time") * 1000);
    }
  }

  @SneakyThrows
  @Override
  public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player,
                                      @NotNull byte[] bytes) {
    if (!channel.equalsIgnoreCase("BungeeCord")) {
      return;
    }

    ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
    String subChannel = in.readUTF();

    if (subChannel.equalsIgnoreCase("servers")) {
      String json = in.readUTF();

      TypeToken<List<ServerRecord>> typeToken = new TypeToken<>() {
      };
      servers = new Gson().fromJson(json, typeToken.getType());
    }
  }
}
