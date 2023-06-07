package os.arcadiadevs.playerservers.hubcore.guis;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;
import os.arcadiadevs.playerservers.hubcore.models.Server;
import os.arcadiadevs.playerservers.hubcore.utils.BungeeUtil;
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;
import os.arcadiadevs.playerservers.hubcore.utils.ServerPinger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Handles the selector GUI.
 *
 * @author ArcadiaDevs
 */
public class SelectorGui {

  /**
   * Opens the selector GUI.
   *
   * @param player The player to open the GUI for.
   */
  public static void openGui(Player player) {

    final var instance = PsHubCore.getInstance();
    final var menu = instance.getSpiGui()
        .create(ChatUtil.translate(instance.getConfig().getString("gui.selector.menu.name")), 5);
    final var useCache = instance.getConfig().getBoolean("cache.enabled");

    menu.setAutomaticPaginationEnabled(true);
    menu.setBlockDefaultInteractions(true);

    final var servers = useCache
        ?
        PsHubCore.getInstance()
            .getServerCache()
            .getServers()
        :
        PsHubCore.getInstance()
            .getServersController()
            .getServers();

    List<Server> filteredServers = new ArrayList<>(servers);

    filteredServers.sort(Comparator.comparing(s -> {
      final ServerPinger.PingResult info = s.getInfo();
      if (info == null || info.status() != ServerStatus.ONLINE) {
        return 1;
      }
      return 0;
    }));

    List<Server> filteredServersByPlayers = new ArrayList<>(filteredServers);

    filteredServersByPlayers.sort(Comparator.comparing(s -> s.getInfo().players() != null ? -s.getInfo().players() : 0));

    final var serversPage = instance.getConfig().getBoolean("gui.selector.menu.sort-by-players")
        ? filteredServersByPlayers
        : filteredServers;

    serversPage.forEach(server -> {
      final var onlinexMaterial =
          XMaterial.matchXMaterial(instance.getConfig().getString("gui.selector.menu.online.block"))
              .orElse(XMaterial.PLAYER_HEAD).parseItem();
      final var offlinexMaterial =
          XMaterial.matchXMaterial(
                  instance.getConfig().getString("gui.selector.menu.offline.block"))
              .orElse(XMaterial.RED_TERRACOTTA).parseItem();

      final var serverInfo = server.getInfo();
      final var online = serverInfo.status() == ServerStatus.ONLINE;
      final boolean showOffline = instance.getConfig().getBoolean("gui.selector.menu.show-offline");

      if (!showOffline && !online) {
        return;
      }

      final var itemBuilder = new ItemBuilder(
          serverInfo.status() == ServerStatus.ONLINE ? onlinexMaterial : offlinexMaterial);

      var lore = instance.getConfig().getStringList(
          serverInfo.status() == ServerStatus.ONLINE ? "gui.selector.menu.online.lore" :
              "gui.selector.menu.offline.lore");

      lore = lore.stream()
          .map(s -> s.replaceAll("%server%", server.getId()))
          .map(s -> s.replaceAll("%status%", online ? "&aOnline" : "&cOffline"))
          .map(s -> s.replaceAll("%players%", online ? String.valueOf(serverInfo.players()) : "0"))
          .map(s -> s.replaceAll("%maxplayers%", online ? String.valueOf(serverInfo.maxPlayers()) : "0"))
          .map(s -> s.replaceAll("%port%", String.valueOf(server.getDefaultAllocation().getPort())))
          .map(s -> s.replaceAll("%motd%", online ? serverInfo.motd() : "&cOffline"))
          .map(s -> s.replaceAll("%node%", server.getNode().getName()))
          .map(s -> s.replaceAll("%owner%", server.getOfflinePlayer().getName() == null ? "Unknown" : server.getOfflinePlayer().getName()))
          .map(s -> s.replaceAll("%ip%", server.getNode().getIp()))
          .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

      var item = itemBuilder
          .name(ChatUtil.translate(instance.getConfig().getString(
                  online ? "gui.selector.menu.online.name" : "gui.selector.menu.offline.name")
              .replaceAll("%server%", server.getId())
              .replaceAll("%status%", online ? "&aOnline" : "&cOffline")
              .replaceAll("%players%", online ? String.valueOf(serverInfo.players()) : "0")
              .replaceAll("%maxplayers%", online ? String.valueOf(serverInfo.maxPlayers()) : "0")
              .replaceAll("%port%", String.valueOf(server.getDefaultAllocation().getPort()))
              .replaceAll("%motd%", online ? serverInfo.motd() : "&cOffline")
              .replaceAll("%node%", server.getNode().getName())
              .replaceAll("%owner%", server.getOfflinePlayer().getName() == null ? "Unknown" : server.getOfflinePlayer().getName())
              .replaceAll("%ip%", server.getNode().getIp())
          ))
          .skullOwner(server.getOfflinePlayer().getName() == null ? "MHF_Question" : server.getOfflinePlayer().getName())
          .lore(lore)
          .build();

      menu.addButton(new SGButton(item).withListener(
          listener -> BungeeUtil.connectPlayer(player, server.getId())));
    });

    XSound.BLOCK_NOTE_BLOCK_BASS.play(player);
    player.openInventory(menu.getInventory());

  }

}
