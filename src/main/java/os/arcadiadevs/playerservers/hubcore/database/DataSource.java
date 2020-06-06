package os.arcadiadevs.playerservers.hubcore.database;

import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;

public class DataSource
{

    public static HikariDataSource hikari;

    @SuppressWarnings("ConstantConditions")
    public void registerDataSource() {

        Plugin pl = Bukkit.getPluginManager().getPlugin("PSHubCore");

        String address = pl.getConfig().getString("mysql.hostname");
        String name = pl.getConfig().getString("mysql.database");
        String username = pl.getConfig().getString("mysql.username");
        String password = pl.getConfig().getString("mysql.password");

        hikari = new HikariDataSource();
        hikari.setMaximumPoolSize(10);
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", address);
        hikari.addDataSourceProperty("port", "3306");
        hikari.addDataSourceProperty("databaseName", name);
        hikari.addDataSourceProperty("user", username);
        hikari.addDataSourceProperty("password", password);
    }

    public static Connection getConnection() throws SQLException {
        return hikari.getConnection();
    }

}
