package os.arcadiadevs.playerservers.hubcore;

import com.moandjiezana.toml.Toml;
import com.samjakob.spigui.SpiGUI;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import os.arcadiadevs.playerservers.hubcore.commands.CommandManager;
import os.arcadiadevs.playerservers.hubcore.database.DataSource;
import os.arcadiadevs.playerservers.hubcore.events.ClickEvent;
import os.arcadiadevs.playerservers.hubcore.events.HubEvents;
import os.arcadiadevs.playerservers.hubcore.events.JoinEvent;
import os.arcadiadevs.playerservers.hubcore.objects.ServerCache;
import os.arcadiadevs.playerservers.hubcore.placeholders.PlayerCount;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class PSHubCore extends JavaPlugin {

    private static PSHubCore PSH;
    public Toml multinode;
    public SpiGUI spiGUI;

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

        spiGUI = new SpiGUI(this);

        serverCache = new ServerCache();
    }

    private void createMultiNodeConfig() throws IOException {

        if (!getConfig().getBoolean("multi-node")) {
            return;
        }

        File configFile = new File(this.getDataFolder(), "multinode.toml");
        //noinspection ResultOfMethodCallIgnored
        getDataFolder().mkdirs();
        if (configFile.createNewFile()) {

            try (InputStream fis = getClass().getResourceAsStream("/multinode.toml"); FileOutputStream fos = new FileOutputStream(configFile)) {
                byte[] buf = new byte[1024];
                int i;
                while ((i = fis.read(buf)) != -1) {
                    fos.write(buf, 0, i);
                }
            } catch (Exception e) {
                getLogger().info("[PlayerServers] Failed to load MultiNode file from Jar");
            }
        }
        multinode = new Toml().read(new File(this.getDataFolder(), "multinode.toml"));
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
