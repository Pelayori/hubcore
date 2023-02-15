package os.arcadiadevs.playerservers.hubcore.events;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;

/**
 * Handles miscellaneous events.
 *
 * @author ArcadiaDevs
 */
public class HubEvents implements Listener {

  private final Configuration config;

  public HubEvents(FileConfiguration config) {
    this.config = config;
  }

  /**
   * Handles weather change events.
   *
   * @param event The event.
   */
  @EventHandler
  public void weatherChange(WeatherChangeEvent event) {
    if (config.getBoolean("miscellaneous.disable-weather")) {
      event.setCancelled(true);
    }
  }

  /**
   * Handles entity damage events.
   *
   * @param event The event.
   */
  @EventHandler
  public void entityDamage(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player && config.getBoolean("miscellaneous.disable-damage")) {
      event.setCancelled(true);
    }
  }

}
