package os.arcadiadevs.playerservers.hubcore.guis;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.enums.PowerAction;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;
import os.arcadiadevs.playerservers.hubcore.models.Server;
import os.arcadiadevs.playerservers.hubcore.utils.BungeeUtil;
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;
import os.arcadiadevs.playerservers.hubcore.utils.GuiUtils;

public class PlayerMenuGui {

  public static void openGui(Player player) {
    final var instance = PsHubCore.getInstance();
    final var serversController = instance.getServersController();

    if (!serversController.hasServer(player)) {
      final var menu = instance.getSpiGui().create(
          ChatUtil.translate(instance.getConfig().getString("gui.player-menu.menu.name")), 3);

      menu.setAutomaticPaginationEnabled(false);
      menu.setBlockDefaultInteractions(true);

      for (int i = 0; i < 9 * 4; i++) {
        menu.setButton(
            i,
            new SGButton(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).build())
        );
      }

      var itemCreate = new ItemBuilder(XMaterial.DIAMOND_BLOCK.parseMaterial())
          .name(instance.getConfig().getString("gui.player-menu.menu.create.name"))
          .lore(instance.getConfig().getStringList("gui.player-menu.menu.create.lore"))
          .build();

      menu.setButton(0, 13, new SGButton(itemCreate).withListener(
          listener -> {
            BungeeUtil.createServer(player);
            XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player);
          }));

      player.openInventory(menu.getInventory());
      return;
    }

    final var rows = 5;
    final var menu = instance.getSpiGui()
        .create(ChatUtil.translate(instance.getConfig().getString("gui.player-menu.menu.name")),
            rows);
    menu.setAutomaticPaginationEnabled(false);
    menu.setBlockDefaultInteractions(true);

    GuiUtils.addBorder(menu, rows);

    final var server = serversController.getServer(player);

    if (server == null) {
      player.sendMessage(ChatUtil.translate("&9Error> &cCould not find your server!"));
      return;
    }

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

    final var itemPlayer = new SGButton(
        new ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial())
            .name(ChatUtil.translate(instance.getConfig().getString(online
                    ? "gui.player-menu.menu.info.online.name"
                    : "gui.player-menu.menu.info.offline.name")
                .replaceAll("%server%", server.getId())))
            .skullOwner(player.getName())
            .lore(lore)
            .build()
    );

    final var itemDelete = new SGButton(new ItemBuilder(XMaterial.BARRIER.parseMaterial())
        .name(ChatUtil.translate(
            instance.getConfig().getString("gui.player-menu.menu.delete.name")))
        .lore(instance.getConfig().getStringList("gui.player-menu.menu.delete.lore"))
        .build())
        .withListener(listener -> openDeleteConfirmation(player, server));

    if (online) {
      final var itemJoin = new SGButton(new ItemBuilder(XMaterial.DARK_OAK_DOOR.parseItem())
          .name(ChatUtil.translate(
              instance.getConfig().getString("gui.player-menu.menu.join.name")))
          .lore(instance.getConfig().getStringList("gui.player-menu.menu.join.lore"))
          .build())
          .withListener(listener -> server.connect());

      final var itemStop = new SGButton(new ItemBuilder(XMaterial.RED_TERRACOTTA.parseItem())
          .name(ChatUtil.translate(
              instance.getConfig().getString("gui.player-menu.menu.stop.name")))
          .lore(instance.getConfig().getStringList("gui.player-menu.menu.stop.lore"))
          .build())
          .withListener(listener -> openStopConfirmation(player, server));

      menu.setButton(22, itemJoin);
      menu.setButton(20, itemStop);
    }

    if (!online) {
      final var itemStart = new SGButton(new ItemBuilder(XMaterial.GREEN_TERRACOTTA.parseItem())
          .name(ChatUtil.translate(
              instance.getConfig().getString("gui.player-menu.menu.start.name")))
          .lore(instance.getConfig().getStringList("gui.player-menu.menu.start.lore"))
          .build())
          .withListener(listener -> server.executePowerAction(PowerAction.START));

      menu.setButton(20, itemStart);
    }

    menu.setButton(0, 24, itemDelete);

    menu.setButton(0, 4, itemPlayer);

    XSound.BLOCK_NOTE_BLOCK_BASS.play(player);
    player.openInventory(menu.getInventory());
  }

  public static void openDeleteConfirmation(Player player, Server server) {
    XSound.BLOCK_NOTE_BLOCK_BASS.play(player);
    final var instance = PsHubCore.getInstance();

    final var deleteConfirmationMenu = instance.getSpiGui().create(ChatUtil.translate(
        instance.getConfig().getString("gui.player-menu.menu.delete.confirmation.name")), 3);

    deleteConfirmationMenu.setAutomaticPaginationEnabled(false);
    deleteConfirmationMenu.setBlockDefaultInteractions(true);

    final var yesButton = new SGButton(new ItemBuilder(XMaterial.GREEN_TERRACOTTA.parseItem())
        .name(ChatUtil.translate(
            instance.getConfig()
                .getString("gui.player-menu.menu.delete.confirmation.accept.name")))
        .lore(
            instance.getConfig()
                .getStringList("gui.player-menu.menu.delete.confirmation.accept.lore"))
        .build())
        .withListener(listener2 -> {
          XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player);
          server.delete();
          player.closeInventory();
        });

    final var noButton = new SGButton(new ItemBuilder(XMaterial.RED_TERRACOTTA.parseItem())
        .name(ChatUtil.translate(
            instance.getConfig()
                .getString("gui.player-menu.menu.delete.confirmation.decline.name")))
        .lore(instance.getConfig()
            .getStringList("gui.player-menu.menu.delete.confirmation.decline.lore"))
        .build())
        .withListener(listener -> {
          player.closeInventory();
          XSound.BLOCK_NOTE_BLOCK_BASS.play(player);
        });

    deleteConfirmationMenu.setButton(0, 11, yesButton);

    deleteConfirmationMenu.setButton(0, 15, noButton);

    player.openInventory(deleteConfirmationMenu.getInventory());
  }

  public static void openStopConfirmation(Player player, Server server) {
    final var instance = PsHubCore.getInstance();

    final var stopConfirmationMenu = instance.getSpiGui().create(ChatUtil.translate(
        instance.getConfig().getString("gui.player-menu.menu.stop.confirmation.name")), 3);

    stopConfirmationMenu.setAutomaticPaginationEnabled(false);
    stopConfirmationMenu.setBlockDefaultInteractions(true);

    final var yesButton = new SGButton(new ItemBuilder(XMaterial.GREEN_TERRACOTTA.parseItem())
        .name(ChatUtil.translate(
            instance.getConfig().getString("gui.player-menu.menu.stop.confirmation.accept.name")))
        .lore(instance.getConfig()
            .getStringList("gui.player-menu.menu.stop.confirmation.accept.lore"))
        .build())
        .withListener(listener2 -> {
          XSound.ENTITY_EXPERIENCE_ORB_PICKUP.play(player);
          server.executePowerAction(PowerAction.STOP);
          player.closeInventory();
        });

    final var noButton = new SGButton(new ItemBuilder(XMaterial.RED_TERRACOTTA.parseItem())
        .name(ChatUtil.translate(
            instance.getConfig()
                .getString("gui.player-menu.menu.stop.confirmation.decline.name")))
        .lore(
            instance.getConfig()
                .getStringList("gui.player-menu.menu.stop.confirmation.decline.lore"))
        .build())
        .withListener(listener -> {
          XSound.BLOCK_NOTE_BLOCK_BASS.play(player);
          player.closeInventory();
        });

    stopConfirmationMenu.setButton(0, 11, yesButton);

    stopConfirmationMenu.setButton(0, 15, noButton);

    player.openInventory(stopConfirmationMenu.getInventory());
  }

}
