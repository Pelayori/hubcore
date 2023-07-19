package os.arcadiadevs.playerservers.hubcore.events;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import lombok.AllArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.dto.ServerRecord;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;
import os.arcadiadevs.playerservers.hubcore.models.Server;
import os.arcadiadevs.playerservers.hubcore.utils.BungeeUtil;
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;
import os.arcadiadevs.playerservers.hubcore.utils.ServerPinger;
import os.arcadiadevs.playerservers.hubcore.utils.formatter.Formatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
public class MessageChannelListener implements PluginMessageListener {

  private final Gson gson;

  public void onPluginMessageReceived(String channel, @NotNull Player player, byte[] bytes) {
    if (!channel.equalsIgnoreCase("BungeeCord")) {
      return;
    }

    ByteArrayDataInput in = ByteStreams.newDataInput(bytes);
    String subChannel = in.readUTF();

    if (subChannel.equalsIgnoreCase("servers")) {
      String json = in.readUTF();

      TypeToken<List<ServerRecord>> typeToken = new TypeToken<>() {
      };
      List<ServerRecord> serverRecord = new Gson().fromJson(json, typeToken.getType());

      openGui(player, serverRecord);
      System.out.println("Server record: " + serverRecord);
    }

  }

  private void openGui(Player player, List<ServerRecord> serverRecord) {
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

    filteredServersByPlayers.sort(
        Comparator.comparing(s -> s.getInfo().players() != null ? -s.getInfo().players() : 0));

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

    serverRecord.stream()
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