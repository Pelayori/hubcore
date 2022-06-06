package os.arcadiadevs.playerservers.hubcore.utils;

import lombok.Getter;
import os.arcadiadevs.playerservers.hubcore.database.structures.PingInfoStructure;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("ALL")
public class PingUtil {

    @Getter
    private final String host;

    @Getter
    private final int port;

    public PingUtil(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public boolean isOnline() {
        AtomicBoolean isOpen = new AtomicBoolean(false);

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);

        executor.execute(() -> {
            try {
                Socket socket = new Socket(host, port);
                isOpen.set(true);
                socket.close();
            } catch (IOException e) {
                isOpen.set(false);
            }
        });

        try {
            Thread.sleep(200);
            executor.shutdown();
            executor.awaitTermination(1, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return isOpen.get();

    }

    public PingInfoStructure getData() {
        try {
            Socket sock = new Socket(host, port);

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

}
