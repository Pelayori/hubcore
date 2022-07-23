package os.arcadiadevs.playerservers.hubcore.objects;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.PSHubCore;
import os.arcadiadevs.playerservers.hubcore.database.DataBase;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class Server {

    private String serverId;
    private String playerName;
    private String node;
    private Integer port;

    @Setter private PingInfoStructure cachedData;
    @Setter private ServerStatus cachedStatus;

    public String getShortId() {
        return serverId.split("-")[0];
    }

    public Server() {}

    @SneakyThrows
    public Server(Player player) {
        final var server = DataBase.getServer(player).get();
        this.serverId = server.getServerId();
        this.playerName = server.getPlayerName();
        this.node = server.getNode();
        this.port = server.getPort();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public ServerStatus getServerStatus() {
        AtomicBoolean isOnline = new AtomicBoolean(false);

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

        executor.execute(() -> {
            try {
                Socket socket = new Socket(getHostname(), port);
                isOnline.set(true);
                socket.close();
            } catch (IOException e) {
                isOnline.set(false);
            }
        });

        try {
            Thread.sleep(200);
            executor.shutdown();
            executor.awaitTermination(1, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return isOnline.get() ? ServerStatus.ONLINE : ServerStatus.OFFLINE;

    }

    public Node getNodeObject() {
        final var config = PSHubCore
                .getInstance()
                .getMultinode()
                .getConfigurationSection("nodes." + this.node);

        return new Node()
                .setName(this.node)
                .setHostname(config.getString("hostname"))
                .setToken(config.getString("token"))
                .setPort(config.getInt("port"));
    }

    public PingInfoStructure getData() {
        try {
            Socket sock = new Socket(getHostname(), port);

            DataOutputStream out = new DataOutputStream(sock.getOutputStream());
            DataInputStream in = new DataInputStream(sock.getInputStream());

            out.write(0xFE);

            int b;
            StringBuilder str = new StringBuilder();
            while ((b = in.read()) != -1) {
                if (b > 16 && b != 255 && b != 23 && b != 24) {
                    str.append((char) b);
                }
            }

            String[] data = str.toString().split("§");
            String serverMotd = data[0];
            int onlinePlayers = Integer.parseInt(data[1]);
            int maxPlayers = Integer.parseInt(data[2]);

            return new PingInfoStructure(onlinePlayers, maxPlayers, serverMotd);

        } catch (IOException e) {
            return null;
        }
    }

    public String getHostname() {
        return node.equalsIgnoreCase("local") ? "localhost" : getNodeObject().getHostname();
    }

    public Server setServerId(String serverId) {
        this.serverId = serverId;
        return this;
    }

    public Server setPlayerName(String playerName) {
        this.playerName = playerName;
        return this;
    }

    public Server setNode(String node) {
        this.node = node;
        return this;
    }

    public Server setPort(int port) {
        this.port = port;
        return this;
    }

}
