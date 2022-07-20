package os.arcadiadevs.playerservers.hubcore.database;

import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.objects.Server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DataBase {

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public static boolean containsPort(String port) {
        try (Connection connection = DataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM PlayerServers");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                if (rs.getString("Port").equals(port))
                    return true;
            }
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Future<Server> getServer(Player player) {
        return executor.submit(() -> {
            try (Connection connection = DataSource.getConnection()) {
                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM PlayerServers WHERE UUID = ?");
                stmt.setString(1, player.getUniqueId().toString());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Server()
                            .setServerId(rs.getString("ServerID"))
                            .setPort(rs.getInt("Port"))
                            .setPlayerName(rs.getString("PlayerName"))
                            .setNode(rs.getString("Node"));
                }
                return null;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static Future<HashSet<Server>> getServersInfo() {
        return executor.submit(() -> {
            try (Connection connection = DataSource.getConnection()) {
                PreparedStatement stmt = connection.prepareStatement("SELECT * FROM PlayerServers");
                ResultSet rs = stmt.executeQuery();
                HashSet<Server> servers = new HashSet<>();
                while (rs.next()) {
                    servers.add(new Server()
                            .setServerId(rs.getString("ServerID"))
                            .setPort(rs.getInt("Port"))
                            .setPlayerName(rs.getString("PlayerName"))
                            .setNode(rs.getString("Node")));
                }
                return servers;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public static String getPortByUUID(String UUID) {
        try (Connection connection = DataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM PlayerServers WHERE UUID='" + UUID + "'");
            ResultSet rs = stmt.executeQuery(); rs.next();
            return rs.getString("Port");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean containsServer(String serverID) {
        try (Connection connection = DataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM PlayerServers");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                if (rs.getString("ServerID").equals(serverID))
                    return true;
            }
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}