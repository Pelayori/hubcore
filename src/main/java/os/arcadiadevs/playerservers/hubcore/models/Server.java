package os.arcadiadevs.playerservers.hubcore.models;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.enums.MessageAction;
import os.arcadiadevs.playerservers.hubcore.enums.PowerAction;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;
import os.arcadiadevs.playerservers.hubcore.utils.ServerPinger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

/**
 * The model of a server.
 *
 * @author ArcadiaDevs
 */
@Entity
@Table(name = "servers")
@Getter
@Setter
public class Server {

  @Id
  @Column(name = "id")
  private String id;

  @Column(name = "external_id")
  private Long externalId = null;

  @Column(name = "external_uuid")
  private UUID externalUuid = null;

  @Column(name = "owner", nullable = false)
  private UUID owner;

  @ManyToOne
  @JoinColumn(name = "node")
  private Node node;

  @Column(name = "allocations")
  @OneToMany(mappedBy = "id", fetch = FetchType.EAGER)
  private List<Allocation> allocations;

  @JoinColumn(name = "default_allocation", referencedColumnName = "id")
  @OneToOne
  private Allocation defaultAllocation;

  /**
   * Gets the owner of the server as a OfflinePlayer.
   *
   * @return The owner of the server
   */
  public OfflinePlayer getOfflinePlayer() {
    return Bukkit.getOfflinePlayer(owner);
  }

  /**
   * Gets the owner of the server as a player.
   *
   * @return The owner of the server
   */
  public Player getPlayer() {
    return Bukkit.getPlayer(owner);
  }

  /**
   * Connect player to the server.
   */
  public void connect() {
    serverAction(MessageAction.CONNECT, getPlayer());
  }

  /**
   * Gets the InetSocketAddress of the server.
   *
   * @return The InetSocketAddress
   */
  public InetSocketAddress getInetAddress() {
    return defaultAllocation.getInetAddress();
  }

  /**
   * Gets the status of the server.
   *
   * @return The status of the server
   */
  public ServerStatus getStatus() {
    var status = ServerStatus.UNKNOWN;
    try (Socket s = new Socket()) {
      s.connect(getInetAddress(), 1000);
      s.setSoTimeout(1000);
      if (s.isConnected()) {
        status = ServerStatus.ONLINE;
      }
    } catch (IOException ignored) {
      status = ServerStatus.OFFLINE;
    }

    return status;
  }

  /**
   * Gets the MOTD and player count of the server.
   *
   * @return The status of the server
   */
  public ServerPinger.PingResult getInfo() {
    try {
      return ServerPinger.ping(this.getInetAddress(), 1000).get();
    } catch (InterruptedException | ExecutionException e) {
      return null;
    }
  }

  /**
   * Sends bungee message.
   *
   * @param action message to bungee
   */
  private void serverAction(MessageAction action, Player player, String id) {
    ByteArrayDataOutput out = ByteStreams.newDataOutput();

    try {
      out.writeUTF(action.toString().toLowerCase());
      if (id != null) {
        out.writeUTF(id);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    player.sendPluginMessage(PsHubCore.getInstance(), "BungeeCord", out.toByteArray());
    player.closeInventory();
  }

  private void serverAction(MessageAction action, Player player) {
    serverAction(action, player, null);
  }

  public void start() {
    serverAction(MessageAction.START, getPlayer());
  }

  public void stop() {
    serverAction(MessageAction.STOP, getPlayer());
  }

  public void create() {
    serverAction(MessageAction.CREATE, getPlayer());
  }

  /**
   * Deletes the server.
   */
  public void delete() {
    serverAction(MessageAction.DELETE, getPlayer());
  }

  /**
   * Deletes specified server.
   */
  public void adminDelete(Player player) {
    serverAction(MessageAction.ADELETE, player, getId());
  }

  /**
   * Stops specified server.
   */
  public void adminStop(Player player) {
    serverAction(MessageAction.ASTOP, player, getId());
  }

  /**
   * Starts specified server.
   */
  public void adminStart(Player player) {
    serverAction(MessageAction.ASTART, player, getId());
  }


}
