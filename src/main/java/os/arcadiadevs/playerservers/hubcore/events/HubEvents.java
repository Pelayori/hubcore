package os.arcadiadevs.playerservers.hubcore.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

import static os.arcadiadevs.playerservers.hubcore.PSHubCore.PSH;

public class HubEvents implements Listener {

    @EventHandler
    public void weatherChange(WeatherChangeEvent e) {
        if (PSH.getConfig().getBoolean("disable-weather")) {
            e.getWorld().setThundering(false);
            e.getWorld().setStorm(false);
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void entityDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player && PSH.getConfig().getBoolean("disable-damage"))
            e.setCancelled(true);
    }

}
