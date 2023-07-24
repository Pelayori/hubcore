package os.arcadiadevs.playerservers.hubcore.guis;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.dto.ServerRecord;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;
import os.arcadiadevs.playerservers.hubcore.models.Server;
import os.arcadiadevs.playerservers.hubcore.utils.BungeeUtil;
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;
import os.arcadiadevs.playerservers.hubcore.utils.ServerPinger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import os.arcadiadevs.playerservers.hubcore.utils.formatter.Formatter;

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
    final var records = PsHubCore.getInstance().getServerCache().getServers();
    final var menu = instance.getSpiGui()
        .create(ChatUtil.translate(instance.getConfig().getString("gui.selector.menu.name")), 5);
    final var useCache = instance.getConfig().getBoolean("cache.enabled");

    menu.setAutomaticPaginationEnabled(true);
    menu.setBlockDefaultInteractions(true);

    List<ServerRecord> filteredServers = new ArrayList<>(records);

    filteredServers.sort(Comparator.comparing(s -> s.online() ? 1 : 0));

    List<ServerRecord> filteredServersByPlayers = new ArrayList<>(filteredServers);

    filteredServersByPlayers.sort(
        Comparator.comparing(s -> s.players() != null ? -s.players() : 0));

    final var serversPage = instance.getConfig().getBoolean("gui.selector.menu.sort-by-players")
        ? filteredServersByPlayers
        : filteredServers;

    final XMaterial onlinexMaterial =
        XMaterial.matchXMaterial(instance.getConfig().getString("gui.selector.menu.online.block"))
            .orElse(XMaterial.PLAYER_HEAD);

    final XMaterial offlinexMaterial =
        XMaterial.matchXMaterial(instance.getConfig().getString("gui.selector.menu.offline.block"))
            .orElse(XMaterial.RED_TERRACOTTA);

    final boolean showOffline = instance.getConfig().getBoolean("gui.selector.menu.show-offline");

    serversPage.stream()
        .filter(server -> showOffline || server.online())
        .forEach(server -> {
          boolean online = server.online();
          XMaterial material = online ? onlinexMaterial : offlinexMaterial;
          List<String> onlineLore = Formatter.format(server,
              instance.getConfig().getStringList("gui.selector.menu.online.lore"));

          List<String> offlineLore = Formatter.format(server,
              instance.getConfig().getStringList("gui.selector.menu.offline.lore"));

          String onlineName = Formatter.format(server,
              instance.getConfig().getString("gui.selector.menu.online.name"));

          String offlineName = Formatter.format(server,
              instance.getConfig().getString("gui.selector.menu.offline.name"));

          ItemBuilder itemBuilder = new ItemBuilder(material.parseMaterial())
              .name(online ? onlineName : offlineName);

          ItemStack item = itemBuilder
              .lore(online ? onlineLore : offlineLore)
              .build();

          menu.addButton(new SGButton(item).withListener(
              listener -> BungeeUtil.connectPlayer(player, server.name())));
        });

    XSound.BLOCK_NOTE_BLOCK_BASS.play(player);
    player.openInventory(menu.getInventory());
  }

}
