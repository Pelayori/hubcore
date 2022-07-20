package os.arcadiadevs.playerservers.hubcore.objects;

import lombok.Getter;

@Getter
public class Node {

    private String name;
    private String hostname;
    private String token;
    private int port;

    public String getFullAddress() {
        return hostname + ":" + port;
    }

    public Node setName(String name) {
        this.name = name;
        return this;
    }

    public Node setHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public Node setToken(String token) {
        this.token = token;
        return this;
    }

    public Node setPort(int port) {
        this.port = port;
        return this;
    }
}
