package os.arcadiadevs.playerservers.hubcore.placeholders;

import lombok.SneakyThrows;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.Plugin;
import os.arcadiadevs.playerservers.hubcore.database.DataBase;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;
import os.arcadiadevs.playerservers.hubcore.objects.Server;

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

    @SneakyThrows
    @Override
    public String onRequest(OfflinePlayer p, String params) {
        switch (params) {
            case "online":
                final var server = new Server(p.getPlayer());
                return server.getServerStatus() == ServerStatus.ONLINE ? "Online" : "Offline";
            case "serveronline":
                final var online = (int) DataBase
                        .getServersInfo()
                        .get()
                        .stream()
                        .filter(_server -> _server.getServerStatus() == ServerStatus.ONLINE)
                        .count();

                return String.valueOf(online);
        }

        return null;
    }
}
