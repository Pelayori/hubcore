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

    // Add gray stained glass pane outline
    for (int i = 0; i < 45; i++) {
        if (i < 9 || i > 35 || i % 9 == 0 || i % 9 == 8) {
            menu.setButton(i, new SGButton(new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).name(" ").build()));
        }
    }

    List<ServerRecord> filteredServers = new ArrayList<>(records);

    filteredServers.sort(Comparator.comparing(s -> s.online() ? 0 : 1));

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

    int slot = 10; // Start at the first slot of the second row
    for (ServerRecord server : serversPage) {
        if (!showOffline && !server.online()) {
            continue;
        }

        if (slot % 9 == 8) {
            slot += 2; // Skip the outline columns
        }

        if (slot >= 35) {
            break; // Stop if we've filled all available slots
        }

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
            .skullOwner(server.name() == null ? "" : server.name())
            .build();

        menu.setButton(slot, new SGButton(item).withListener(
            listener -> BungeeUtil.connectPlayer(player, server.name())));

        slot++;
    }

    // Add page buttons at the bottom
    menu.setButton(36, menu.previousPageButton());
    menu.setButton(44, menu.nextPageButton());

    XSound.BLOCK_NOTE_BLOCK_BASS.play(player);
    player.openInventory(menu.getInventory());
  }
}
