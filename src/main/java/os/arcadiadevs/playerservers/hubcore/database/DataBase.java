package os.arcadiadevs.playerservers.hubcore.database;

import os.arcadiadevs.playerservers.hubcore.database.structures.DBInfoStructure;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DataBase {

    public boolean containsPort(String port) {
        try (Connection connection = DataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM PLAYERSERVERS");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                if (rs.getString("PORT").equals(port))
                    return true;
            }
            return false;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<DBInfoStructure> getServersInfo() {

        ArrayList<DBInfoStructure> output = new ArrayList<>();

        try (Connection connection = DataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM PLAYERSERVERS");
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                output.add(new DBInfoStructure(
                        rs.getString("UUID"),
                        rs.getString("SERVERID"),
                        rs.getString("PORT"),
                        rs.getString("NAME"),
                        rs.getString("PLAYERNAME"))
                );
            }
            return output;
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String getPortByUUID(String UUID) {
        try (Connection connection = DataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM PLAYERSERVERS WHERE UUID='" + UUID + "'");
            ResultSet rs = stmt.executeQuery(); rs.next();
            return rs.getString("PORT");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getServerByUUID(String UUID) {
        try (Connection connection = DataSource.getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT * FROM PLAYERSERVERS WHERE UUID='" + UUID + "'");
            ResultSet rs = stmt.executeQuery(); rs.next();
            return rs.getString("SERVERID");
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
