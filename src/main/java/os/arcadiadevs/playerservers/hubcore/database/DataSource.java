package os.arcadiadevs.playerservers.hubcore.database;

import com.zaxxer.hikari.HikariDataSource;
import os.arcadiadevs.playerservers.hubcore.PSHubCore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource
{

    public static HikariDataSource hikari;

    @SuppressWarnings("ConstantConditions")
    public boolean registerDataSource() {

        final var pl = PSHubCore.getInstance();

        final var address = pl.getConfig().getString("mysql.hostname");
        final var name = pl.getConfig().getString("mysql.database");
        final var username = pl.getConfig().getString("mysql.username");
        final var password = pl.getConfig().getString("mysql.password");
        final var useSSL = pl.getConfig().getBoolean("mysql.useSSL");

        try {
            hikari = new HikariDataSource();
            hikari.setPoolName("PSHubCore");
            hikari.setMaximumPoolSize(10);
            hikari.setLoginTimeout(10);
            hikari.setConnectionTimeout(10000);
            hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");
            hikari.addDataSourceProperty("serverName", address);
            hikari.addDataSourceProperty("port", "3306");
            hikari.addDataSourceProperty("databaseName", name);
            hikari.addDataSourceProperty("user", username);
            hikari.addDataSourceProperty("password", password);
            hikari.addDataSourceProperty("useSSL", useSSL);
            hikari.getConnection();
        } catch (SQLException e) {
            pl.getLogger().severe(e.getLocalizedMessage());
            return false;
        }

        return true;
    }

    public static Connection getConnection() throws SQLException {
        return hikari.getConnection();
    }

}