package os.arcadiadevs.playerservers.hubcore;

import com.samjakob.spigui.SpiGUI;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import os.arcadiadevs.playerservers.hubcore.commands.CommandManager;
import os.arcadiadevs.playerservers.hubcore.database.DataBase;
import os.arcadiadevs.playerservers.hubcore.database.DataSource;
import os.arcadiadevs.playerservers.hubcore.events.ClickEvent;
import os.arcadiadevs.playerservers.hubcore.events.HubEvents;
import os.arcadiadevs.playerservers.hubcore.events.JoinEvent;
import os.arcadiadevs.playerservers.hubcore.objects.ServerCache;
import os.arcadiadevs.playerservers.hubcore.placeholders.PlayerCount;

import java.io.*;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PSHubCore extends JavaPlugin {

    private static PSHubCore PSH;
    @Getter
    private static DataBase dataBase;
    @Getter
    public YamlConfiguration multinode;
    @Getter
    private SpiGUI spiGUI;

    @Getter
    public ServerCache serverCache;

    @SneakyThrows
    @Override
    public void onEnable() {
        PSH = this;
        getConfig().options().copyDefaults(true);
        saveConfig();
        createMultiNodeConfig();

        DataSource ds = new DataSource();
        ds.registerDataSource();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            /*
             * We register the EventListeneres here, when PlaceholderAPI is installed.
             * Since all events are in the main class (this class), we simply use "this"
             */
            new PlayerCount(this).register();
        }

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        Bukkit.getPluginManager().registerEvents(new ClickEvent(), this);
        Bukkit.getPluginManager().registerEvents(new JoinEvent(), this);
        Bukkit.getPluginManager().registerEvents(new HubEvents(), this);

        Objects.requireNonNull(getCommand("servers")).setExecutor(new CommandManager());

        // Initialize SpiGUI
        spiGUI = new SpiGUI(this);

        // Initialize ServerCache
        serverCache = new ServerCache();

        // Create ServerCache refreshing task
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(serverCache, 1, getConfig().getInt("cache-time"), TimeUnit.SECONDS);
    }

    private void createMultiNodeConfig() throws IOException {

        if (!getConfig().getBoolean("multi-node")) {
            return;
        }

        File configFile = new File(this.getDataFolder(), "multinode.yml");

        if (!configFile.exists()) {
            InputStream inputStream = getClass().getResourceAsStream("/multinode.yml");
            OutputStream outputStream = new FileOutputStream(configFile);
            int read;
            byte[] bytes = new byte[1024];
            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }
            inputStream.close();
            outputStream.close();
        }

        multinode = YamlConfiguration.loadConfiguration(configFile);
    }

    public static PSHubCore getInstance() {
        return PSH;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        getServer().getMessenger().unregisterIncomingPluginChannel(this, "BungeeCord");
    }
}
