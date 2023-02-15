package os.arcadiadevs.playerservers.hubcore.models;

import lombok.Getter;
import lombok.Setter;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;
import os.arcadiadevs.playerservers.hubcore.utils.ServerPinger;

@Getter
@Setter
public class CachedServer extends Server {

  private ServerStatus status;

  private ServerPinger.PingResult pingResult;

  public CachedServer(Server server) {
    this.setId(server.getId());
    this.setExternalId(server.getExternalId());
    this.setExternalUuid(server.getExternalUuid());
    this.setOwner(server.getOwner());
    this.setNode(server.getNode());
    this.setAllocations(server.getAllocations());
    this.setDefaultAllocation(server.getDefaultAllocation());

    ServerPinger.PingResult pingResult = server.getInfo();

    setStatus(pingResult.status());
    setPingResult(pingResult);
  }

  @Override
  public ServerPinger.PingResult getInfo() {
    System.out.println("Getting cached info");
    return pingResult;
  }

  @Override
  public ServerStatus getStatus() {
    System.out.println("Getting cached status");
    return status;
  }

}
