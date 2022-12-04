package os.arcadiadevs.playerservers.hubcore.events;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;

public class HubEvents implements Listener {

  private final Configuration config;

  public HubEvents(FileConfiguration config) {
    this.config = config;
  }

  @EventHandler
  public void weatherChange(WeatherChangeEvent e) {
    if (config.getBoolean("miscellaneous.disable-weather")) {
      e.setCancelled(true);
    }
  }

  @EventHandler
  public void entityDamage(EntityDamageEvent e) {
    if (e.getEntity() instanceof Player && config.getBoolean("miscellaneous.disable-damage")) {
      e.setCancelled(true);
    }
  }

}
