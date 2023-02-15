package os.arcadiadevs.playerservers.hubcore;

import com.samjakob.spigui.SpiGUI;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.SessionFactory;
import os.arcadiadevs.playerservers.hubcore.cache.ServerCache;
import os.arcadiadevs.playerservers.hubcore.commands.CommandManager;
import os.arcadiadevs.playerservers.hubcore.controllers.NodesController;
import os.arcadiadevs.playerservers.hubcore.controllers.ServersController;
import os.arcadiadevs.playerservers.hubcore.events.ClickEvent;
import os.arcadiadevs.playerservers.hubcore.events.HubEvents;
import os.arcadiadevs.playerservers.hubcore.events.JoinEvent;
import os.arcadiadevs.playerservers.hubcore.models.Allocation;
import os.arcadiadevs.playerservers.hubcore.models.Node;
import os.arcadiadevs.playerservers.hubcore.models.Server;
import os.arcadiadevs.playerservers.hubcore.placeholders.PlayerCount;

/**
 * Main class of the plugin.
 *
 * @author ArcadiaDevs
 */
public class PsHubCore extends JavaPlugin {

  @Getter
  private static PsHubCore instance;

  @Getter
  private SpiGUI spiGui;

  @Getter
  private SessionFactory sessionFactory;

  @Getter
  private ServersController serversController;

  @Getter
  private NodesController nodesController;

  @Getter
  private ServerCache serverCache;

  @SneakyThrows
  @Override
  public void onEnable() {
    instance = this;
    getConfig().options().copyDefaults(true);
    saveConfig();

    extractFile("hibernate.cfg.xml");

    var configuration = new org.hibernate.cfg.Configuration()
        .addAnnotatedClass(Server.class)
        .addAnnotatedClass(Node.class)
        .addAnnotatedClass(Allocation.class);

    if (getConfig().getBoolean("mysql.get-from-file")) {
      configuration
          .configure(new File(this.getDataFolder().getAbsolutePath() + "/" + "hibernate.cfg.xml"));
    } else {
      configuration
          .setProperty("hibernate.connection.url", getConfig().getString("mysql.url"))
          .setProperty("hibernate.connection.username", getConfig().getString("mysql.username"))
          .setProperty("hibernate.connection.password", getConfig().getString("mysql.password"))
          .setProperty("hibernate.connection.driver_class", getConfig().getString("mysql.driver"))
          .setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect")
          .setProperty("hibernate.connection.pool_size", "10")
          .setProperty("hibernate.show_sql", String.valueOf(getConfig().getBoolean("mysql.debug")))
          .setProperty("hibernate.format_sql",
              String.valueOf(getConfig().getBoolean("mysql.debug")))
          .setProperty("hibernate.current_session_context_class", "thread")
          .setProperty("hibernate.hbm2ddl.auto", getConfig().getString("mysql.update-policy"));
    }

    sessionFactory = configuration.buildSessionFactory();

    // Initialize the controllers
    serversController = new ServersController(sessionFactory);
    nodesController = new NodesController(sessionFactory);

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      /*
       * We register the EventListeneres here, when PlaceholderAPI is installed.
       * Since all events are in the main class (this class), we simply use "this"
       */
      new PlayerCount(serversController).register();
    }

    Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    Bukkit.getPluginManager().registerEvents(new ClickEvent(this), this);
    Bukkit.getPluginManager().registerEvents(new JoinEvent(this), this);
    Bukkit.getPluginManager().registerEvents(new HubEvents(getConfig()), this);

    Objects.requireNonNull(getCommand("servers")).setExecutor(new CommandManager());

    // Initialize SpiGUI
    spiGui = new SpiGUI(this);

    // Initialize ServerCache
    serverCache = new ServerCache(serversController);

    // Create ServerCache refreshing task
    ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    executor.scheduleAtFixedRate(serverCache, 1, getConfig().getInt("cache.cache-time"), TimeUnit.SECONDS);
  }

  /**
   * Extracts a file from the jar to the data folder.
   *
   * @param name The name of the file.
   * @throws IOException If the file cannot be extracted.
   */
  private void extractFile(String name) throws IOException {
    var configFile = new File(this.getDataFolder(), name);

    //noinspection ResultOfMethodCallIgnored
    getDataFolder().mkdirs();
    if (configFile.createNewFile()) {
      try (
          var fis = getClass().getResourceAsStream("/" + name);
          var fos = new FileOutputStream(configFile)
      ) {
        var buf = new byte[1024];
        int i;
        while ((i = Objects.requireNonNull(fis).read(buf)) != -1) {
          fos.write(buf, 0, i);
        }
      } catch (Exception e) {
        Bukkit.getLogger()
            .warning(String.format("Error extracting %s: %s", name, e.getMessage()));
      }
    }
  }

  @Override
  public void onDisable() {
    super.onDisable();
    getServer().getMessenger().unregisterIncomingPluginChannel(this, "BungeeCord");
  }
}
