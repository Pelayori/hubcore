package os.arcadiadevs.playerservers.hubcore.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.enums.PowerAction;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;
import os.arcadiadevs.playerservers.hubcore.utils.BungeeUtil;
import os.arcadiadevs.playerservers.hubcore.utils.ServerPinger;

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

  @Column(name = "owner")
  private UUID owner;

  @Column(name = "port")
  private Integer port;

  @ManyToOne
  @JoinColumn(name = "node", nullable = false)
  private Node node;

  public String getFullAddress() {
    return (this.node.getIp() + ":" + this.port)
        .replaceAll("localhost", "127.0.0.1");
  }

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

  public void connect() {
    BungeeUtil.connectPlayer(getPlayer());
  }

  /**
   * Gets the InetSocketAddress of the server.
   *
   * @return The InetSocketAddress
   */
  public InetSocketAddress getInetAddress() {
    return new InetSocketAddress(this.node.getIp(), this.port);
  }

  /**
   * Gets the status of the server.
   *
   * @return The status of the server
   */
  public ServerStatus getStatus() {
    final var address = node.getNumericAddress();

    var status = ServerStatus.UNKNOWN;
    try {
      Socket s = new Socket(address, port);
      if (s.isConnected()) {
        status = ServerStatus.ONLINE;
      }
      s.close();
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
   * Execute a power action on the server.
   *
   * @param action The action to execute
   */
  public void executePowerAction(PowerAction action) {
    switch (action) {
      case START -> BungeeUtil.startServer(getPlayer());
      case STOP -> BungeeUtil.stopServer(getPlayer());
      default -> throw new IllegalStateException("Unexpected value: " + action);
    }
  }

  /**
   * Deletes the server.
   */
  public void delete() {
    BungeeUtil.deleteServer(getPlayer());
  }

}
