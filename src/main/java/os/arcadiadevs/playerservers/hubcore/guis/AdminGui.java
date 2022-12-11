package os.arcadiadevs.playerservers.hubcore.guis;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.enums.PowerAction;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;
import os.arcadiadevs.playerservers.hubcore.models.Server;
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;
import os.arcadiadevs.playerservers.hubcore.utils.GuiUtils;

import java.util.ArrayList;

/**
 * Admin gui for admins to manage all servers.
 *
 * @author ArcadiaDevs
 */
public class AdminGui {

  public static void openGui(Player player) {

    final var instance = PsHubCore.getInstance();
    final var menu = instance.getSpiGui()
            .create(ChatUtil.translate(instance.getConfig().getString("gui.admin-menu.menu.name")), 5);

    menu.setAutomaticPaginationEnabled(true);
    menu.setBlockDefaultInteractions(true);

    final var onlinexMaterial =
            XMaterial.matchXMaterial(instance.getConfig().getString("gui.admin-menu.menu.online.block"))
                    .orElse(XMaterial.PLAYER_HEAD).parseMaterial();
    final var offlinexMaterial =
            XMaterial.matchXMaterial(instance.getConfig().getString("gui.admin-menu.menu.offline.block"))
                    .orElse(XMaterial.RED_TERRACOTTA).parseMaterial();

    final var servers = PsHubCore.getInstance()
            .getServersController()
            .getServers();

    servers.forEach(server -> {
      final var itemBuilder = new ItemBuilder(
              server.getStatus() == ServerStatus.ONLINE ? onlinexMaterial : offlinexMaterial);

      final var online = server.getInfo() != null && server.getInfo().motd() != null && server.getStatus() == ServerStatus.ONLINE;

      var lore = instance.getConfig().getStringList(online
              ? "gui.player-menu.menu.info.online.lore"
              : "gui.player-menu.menu.info.offline.lore"
      );

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
                              online ? "gui.admin-menu.menu.online.name" : "gui.admin-menu.menu.offline.name")
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
              .lore(lore)
              .build();

      menu.setButton(0, menu.getInventory().firstEmpty(), new SGButton(item).withListener(
              listener -> openServerMenu(player, server)));
    });

    XSound.BLOCK_NOTE_BLOCK_BASS.play(player);
    player.openInventory(menu.getInventory());

  }

  public static void openServerMenu(Player player, Server server) {

    XSound.BLOCK_NOTE_BLOCK_BASS.play(player);

    final var instance = PsHubCore.getInstance();
    final var rows = 5;
    final var menu = instance.getSpiGui()
            .create(ChatUtil.translate(server.getOfflinePlayer().getName()), rows);

    menu.setAutomaticPaginationEnabled(false);
    menu.setBlockDefaultInteractions(true);

    GuiUtils.addBorder(menu, rows);

    if (server == null) {
      ChatUtil.sendMessage(player, "&9Error> &cCould not find your server!");
      return;
    }

    final var itemStop = new SGButton(new ItemBuilder(XMaterial.RED_TERRACOTTA.parseItem())
            .name(ChatUtil.translate(
                    instance.getConfig().getString("gui.player-menu.menu.stop.name")))
            .lore(instance.getConfig().getStringList("gui.player-menu.menu.stop.lore"))
            .build())
            .withListener(listener -> {
              server.adminStop(player);
              player.closeInventory();
            });

    final var itemStart = new SGButton(new ItemBuilder(XMaterial.GREEN_TERRACOTTA.parseItem())
            .name(ChatUtil.translate(
                    instance.getConfig().getString("gui.player-menu.menu.start.name")))
            .lore(instance.getConfig().getStringList("gui.player-menu.menu.start.lore"))
            .build())
            .withListener(listener -> {
              server.adminStart(player);
              player.closeInventory();
            });

    final var itemDelete = new SGButton(new ItemBuilder(XMaterial.BARRIER.parseItem())
            .name(ChatUtil.translate(
                    instance.getConfig().getString("gui.player-menu.menu.delete.name")))
            .lore(instance.getConfig().getStringList("gui.player-menu.menu.delete.lore"))
            .build())
            .withListener(listener -> {
              server.adminDelete(player);
              player.closeInventory();
            });

    final var online = server.getInfo() != null && server.getInfo().motd() != null && server.getStatus() == ServerStatus.ONLINE;

    if (online) {
      menu.setButton(9 * 2 + 2, itemStop);
    }
    if (!online) {
      menu.setButton(9 * 2 + 2, itemStart);
    }
    menu.setButton(9*2+6 , itemDelete);
    player.openInventory(menu.getInventory());

  }

}
