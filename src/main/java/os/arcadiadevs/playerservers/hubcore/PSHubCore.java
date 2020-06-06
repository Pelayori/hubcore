package os.arcadiadevs.playerservers.hubcore;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import os.arcadiadevs.playerservers.hubcore.commands.CommandManager;
import os.arcadiadevs.playerservers.hubcore.database.DataSource;
import os.arcadiadevs.playerservers.hubcore.events.ClickEvent;
import os.arcadiadevs.playerservers.hubcore.events.HubEvents;
import os.arcadiadevs.playerservers.hubcore.events.JoinEvent;
import os.arcadiadevs.playerservers.hubcore.placeholders.PlayerCount;

import java.util.Objects;

public class PSHubCore extends JavaPlugin {

    public static Plugin PSH;

    @Override
    public void onEnable() {
        PSH = this;
        getConfig().options().copyDefaults(true);
        saveConfig();

        DataSource ds = new DataSource();
        ds.registerDataSource();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            /*
             * We register the EventListeneres here, when PlaceholderAPI is installed.
             * Since all events are in the main class (this class), we simply use "this"
             */
            new PlayerCount(this).register();
        }

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getPluginManager().registerEvents(new ClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new JoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new HubEvents(), this);

        Objects.requireNonNull(getCommand("servers")).setExecutor(new CommandManager());
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

}
