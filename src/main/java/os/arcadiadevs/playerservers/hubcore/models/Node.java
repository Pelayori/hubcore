package os.arcadiadevs.playerservers.hubcore.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.net.InetSocketAddress;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * The node object.
 *
 * @author ArcadiaDevs
 */
@Entity
@Table(name = "nodes")
@Getter
@Setter
public class Node {

  @Id
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "ip", nullable = false)
  private String ip;

  @Column(name = "port", nullable = false)
  private int port;

  @Column(name = "max_online")
  private Integer maxOnline;

  @Column(name = "pterodactyl", nullable = false)
  private boolean pterodactyl = false;

  @Column(name = "token")
  private String token;

  @Column(name = "servers")
  @OneToMany(mappedBy = "node", fetch = FetchType.EAGER)
  private List<Server> servers;

  @Column(name = "allocations")
  @OneToMany(mappedBy = "node", fetch = FetchType.EAGER)
  private List<Allocation> allocations;

  /**
   * Get the full address of the server as ip:port.
   *
   * @return The full address
   */
  public String getFullAddress() {
    return this.ip + ":" + this.port;
  }

  /**
   * Get the address of the node as InetSocketAddress.
   *
   * @return The address
   */
  public InetSocketAddress getInetAddress() {
    return new InetSocketAddress(this.ip, this.port);
  }

  public String getNumericAddress() {
    return getIp().replaceAll("localhost", "127.0.0.1");
  }

}
