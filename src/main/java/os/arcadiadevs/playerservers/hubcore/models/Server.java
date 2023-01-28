package os.arcadiadevs.playerservers.hubcore.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
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

  public void connect() {
    BungeeUtil.connectPlayer(this);
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
