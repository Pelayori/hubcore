package os.arcadiadevs.playerservers.hubcore.database.structures;

public class DBInfoStructure {

    private String UUID;
    private String ServerID;
    private String Port;
    private String ServerName;
    private String PlayerName;

    public DBInfoStructure(String UUID, String ServerID, String Port, String ServerName, String PlayerName) {
        this.UUID = UUID;
        this.ServerID = ServerID;
        this.Port = Port;
        this.ServerName = ServerName;
        this.PlayerName = PlayerName;
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

}
