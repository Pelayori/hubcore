package os.arcadiadevs.playerservers.hubcore.guis;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;
import os.arcadiadevs.playerservers.hubcore.utils.BungeeUtil;
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;

import java.util.ArrayList;

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

    menu.setAutomaticPaginationEnabled(true);
    menu.setBlockDefaultInteractions(true);

    final var servers = PsHubCore.getInstance()
        .getServersController()
        .getServers();

    servers.forEach(server -> {
      final var onlinexMaterial =
          XMaterial.matchXMaterial(instance.getConfig().getString("gui.selector.menu.online.block"))
              .orElse(XMaterial.PLAYER_HEAD).parseItem();
      final var offlinexMaterial =
          XMaterial.matchXMaterial(
                  instance.getConfig().getString("gui.selector.menu.offline.block"))
              .orElse(XMaterial.RED_TERRACOTTA).parseItem();

      final var itemBuilder = new ItemBuilder(
          server.getStatus() == ServerStatus.ONLINE ? onlinexMaterial : offlinexMaterial);
      final var online = server.getStatus() == ServerStatus.ONLINE;

      var lore = instance.getConfig().getStringList(
          server.getStatus() == ServerStatus.ONLINE ? "gui.selector.menu.online.lore" :
              "gui.selector.menu.offline.lore");

      lore = lore.stream()
          .map(s -> s.replaceAll("%server%", server.getId()))
          .map(s -> s.replaceAll("%status%", online ? "&aOnline" : "&cOffline"))
          .map(s -> s.replaceAll("%players%", online ? server.getInfo().players() + "" : "0"))
          .map(s -> s.replaceAll("%maxplayers%", online ? server.getInfo().maxPlayers() + "" : "0"))
          .map(s -> s.replaceAll("%port%", server.getDefaultAllocation().getPort() + ""))
          .map(s -> s.replaceAll("%motd%", online ? server.getInfo().motd() : "&cOffline"))
          .map(s -> s.replaceAll("%node%", server.getNode().getName()))
          .map(s -> s.replaceAll("%owner%", server.getOfflinePlayer().getName()))
          .map(s -> s.replaceAll("%ip%", server.getNode().getIp()))
          .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

      var item = itemBuilder
          .name(ChatUtil.translate(instance.getConfig().getString(
                  online ? "gui.selector.menu.online.name" : "gui.selector.menu.offline.name")
              .replaceAll("%server%", server.getId())
              .replaceAll("%status%", online ? "&aOnline" : "&cOffline")
              .replaceAll("%players%", online ? server.getInfo().players() + "" : "0")
              .replaceAll("%maxplayers%", online ? server.getInfo().maxPlayers() + "" : "0")
              .replaceAll("%port%", server.getDefaultAllocation().getPort() + "")
              .replaceAll("%motd%", online ? server.getInfo().motd() : "&cOffline")
              .replaceAll("%node%", server.getNode().getName())
              .replaceAll("%owner%", server.getOfflinePlayer().getName())
              .replaceAll("%ip%", server.getNode().getIp())
          ))
          .skullOwner(server.getOfflinePlayer().getName())
          .lore(lore)
          .build();

      menu.addButton(new SGButton(item).withListener(
          listener -> BungeeUtil.connectPlayer(player, server.getId())));
    });

    XSound.BLOCK_NOTE_BLOCK_BASS.play(player);
    player.openInventory(menu.getInventory());

  }

}
