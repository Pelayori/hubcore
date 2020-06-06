package os.arcadiadevs.playerservers.hubcore.events;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import os.arcadiadevs.playerservers.hubcore.utils.ColorUtils;

import java.util.ArrayList;

import static os.arcadiadevs.playerservers.hubcore.PSHubCore.PSH;

public class JoinEvent implements Listener {

    @SuppressWarnings("ConstantConditions")
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (PSH.getConfig().getBoolean("enable-compass")) {
            Bukkit.getScheduler().runTaskAsynchronously(PSH, () -> {

                ItemStack is = new ItemStack(XMaterial.COMPASS.parseMaterial());
                ItemMeta im = is.getItemMeta();

                im.setDisplayName(ColorUtils.translate(PSH.getConfig().getString("compass-name")));

                ArrayList<String> lore = new ArrayList<>();
                PSH.getConfig().getStringList("compass-description").forEach(string -> lore.add(ColorUtils.translate(string)));
                im.setLore(lore);
                is.setItemMeta(im);

                Bukkit.getScheduler().runTask(PSH, () -> p.getInventory().setItem(PSH.getConfig().getInt("compass-location"), is));

            });
        } else {
            p.getInventory().forEach(itemStack -> {
                if (itemStack.getType() == XMaterial.COMPASS.parseMaterial())
                    p.getInventory().remove(itemStack);
            });
        }
    }


}
