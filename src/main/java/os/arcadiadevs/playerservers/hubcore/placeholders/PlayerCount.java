package os.arcadiadevs.playerservers.hubcore.placeholders;

import lombok.SneakyThrows;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.controllers.ServersController;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;

public class PlayerCount extends PlaceholderExpansion {

  private final ServersController controller;

  public PlayerCount(ServersController controller) {
    this.controller = controller;
  }

  @Override
  public boolean canRegister() {
    return true;
  }

  @Override
  public String getIdentifier() {
    return "playerservers";
  }

  @Override
  public String getName() {
    return "placeholders";
  }

  @Override
  public String getAuthor() {
    return "OpenSource/Cuftica";
  }

  @Override
  public String getVersion() {
    return "1.0.0";
  }

  @SneakyThrows
  @Override
  public String onRequest(OfflinePlayer player, String params) {
    return switch (params) {
      case "online" -> {
        final var server = controller.getServer(player.getUniqueId());
        yield server.getStatus() == ServerStatus.ONLINE ? "Online" : "Offline";
      }
      case "serveronline" -> {
        final var online = (int) controller.getServers()
            .stream()
            .filter(server -> server.getStatus() == ServerStatus.ONLINE)
            .count();

        yield String.valueOf(online);
      }
      default -> throw new IllegalArgumentException("Invalid placeholder: " + params);
    };
  }
}
