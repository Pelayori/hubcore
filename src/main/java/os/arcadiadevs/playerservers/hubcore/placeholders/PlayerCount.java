package os.arcadiadevs.playerservers.hubcore.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import os.arcadiadevs.playerservers.hubcore.database.DataBase;
import os.arcadiadevs.playerservers.hubcore.database.structures.PingInfoStructure;
import os.arcadiadevs.playerservers.hubcore.utils.PingUtil;

public class PlayerCount extends PlaceholderExpansion {

    Plugin plugin;

    public PlayerCount(Plugin plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getIdentifier() {
        return "playerservers";
    }

    @Override
    public String getName() {
        return "placeholders";
    }

    @Override
    public String getAuthor() {
        return "OpenSource/Cuftica";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        if (params.equalsIgnoreCase("online")) {
            DataBase db = new DataBase();
            PingUtil pu = new PingUtil("127.0.0.1", Integer.parseInt(db.getPortByUUID(p.getUniqueId().toString())));

            try {
                PingInfoStructure structure = pu.getData();

                return (structure != null) ? String.valueOf(structure.getOnline()) : "Offline";
            } catch (Exception ex) {
                return "Offline";
            }
        } else if (params.equalsIgnoreCase("serversonline")) {
            DataBase db = new DataBase();

            int online = (int) db.getServersInfo().stream().filter(dbInfoStructure -> new PingUtil("127.0.0.1", dbInfoStructure.getPort()).isOnline()).count();

            return String.valueOf(online);
        }

        return null;
    }
}
