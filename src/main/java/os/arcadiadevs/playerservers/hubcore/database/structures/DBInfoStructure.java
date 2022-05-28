package os.arcadiadevs.playerservers.hubcore.database.structures;

public class DBInfoStructure {

    private final String UUID;
    private final String ServerID;
    private final String Port;
    private final String ServerName;
    private final String PlayerName;
    private final String Node;

    public DBInfoStructure(String UUID, String ServerID, String Port, String ServerName, String PlayerName, String node) {
        this.UUID = UUID;
        this.ServerID = ServerID;
        this.Port = Port;
        this.ServerName = ServerName;
        this.PlayerName = PlayerName;
        this.Node = node;
    }

    public String getPlayerName() {
        return PlayerName;
    }

    public String getPort() {
        return Port;
    }

    public String getServerID() {
        return ServerID;
    }

    public String getServerName() {
        return ServerName;
    }

    public String getUUID() {
        return UUID;
    }

    public String getNode() {
        return Node;
    }

}
