package os.arcadiadevs.playerservers.hubcore.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.models.Server;

@SuppressWarnings("UnstableApiUsage")
public class BungeeUtil {

  public static void connectPlayer(Player player, String server) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();

    try {
      out.writeUTF("Connect");
      out.writeUTF(server);
    } catch (Exception e) {
      e.printStackTrace();
    }

    player.sendPluginMessage(PsHubCore.getInstance(), "BungeeCord", out.toByteArray());
    player.closeInventory();
  }

  public static void connectPlayer(Server server) {
    connectPlayer(server.getPlayer(), server.getId());
  }

  public static void stopServer(Player player) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();

    try {
      out.writeUTF("stop");
    } catch (Exception e) {
      e.printStackTrace();
    }
    player.sendPluginMessage(PsHubCore.getInstance(), "BungeeCord", out.toByteArray());
    player.closeInventory();
  }

  public static void deleteServer(Player player) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();

    try {
      out.writeUTF("delete");
    } catch (Exception e) {
      e.printStackTrace();
    }
    player.sendPluginMessage(PsHubCore.getInstance(), "BungeeCord", out.toByteArray());
    player.closeInventory();
  }

  public static void createServer(Player player) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();

    try {
      out.writeUTF("create");
    } catch (Exception e) {
      e.printStackTrace();
    }
    player.sendPluginMessage(PsHubCore.getInstance(), "BungeeCord", out.toByteArray());
    player.closeInventory();
  }

  public static void startServer(Player player) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();

    try {
      out.writeUTF("start");
    } catch (Exception e) {
      e.printStackTrace();
    }
    player.sendPluginMessage(PsHubCore.getInstance(), "BungeeCord", out.toByteArray());
    player.closeInventory();
  }

}