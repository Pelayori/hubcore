package os.arcadiadevs.playerservers.hubcore.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.net.InetSocketAddress;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "allocations")
@Getter
@Setter
public class Allocation {

  @Id
  @Column(name = "id", unique = true, nullable = false)
  private long id;

  @Column(name = "ip", nullable = false)
  private String ip;

  @Column(name = "port", nullable = false)
  private Integer port;

  @ManyToOne
  @JoinColumn(name = "node")
  private Node node;

  @ManyToOne()
  @JoinColumn(name = "server_id")
  private Server server;

  public String getFullAddress() {
    return this.ip + ":" + this.port;
  }

  public InetSocketAddress getInetAddress() {
    return new InetSocketAddress(this.node.getIp(), this.port);
  }
}
