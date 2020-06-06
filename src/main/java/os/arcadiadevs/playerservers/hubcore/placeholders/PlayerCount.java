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
        return "online";
    }

    @Override
    public String getAuthor() {
        return "OpenSource";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        DataBase db = new DataBase();
        PingUtil pu = new PingUtil();
        PingInfoStructure structure = pu.getData(Integer.parseInt(db.getPortByUUID(p.getUniqueId().toString())));

        return (structure != null) ? String.valueOf(structure.getOnline()) : "Offline";
    }
}
