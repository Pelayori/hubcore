package os.arcadiadevs.playerservers.hubcore.events;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import os.arcadiadevs.playerservers.hubcore.PSHubCore;
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;

import java.util.ArrayList;
import java.util.List;

public class JoinEvent implements Listener {

    final PSHubCore PSH = PSHubCore.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        final var itemName = PSH.getConfig().getString("gui.item.material");
        final var guiMaterial = XMaterial.matchXMaterial(itemName).orElse(XMaterial.COMPASS).parseMaterial();
        final var player = e.getPlayer();
        
        if (!PSH.getConfig().getBoolean("gui.enabled") || !PSH.getConfig().getBoolean("gui.item.enabled")) {
            player.getInventory().forEach(item -> {
                if (item.getType() == guiMaterial) {
                    player.getInventory().remove(item);
                }
            });
            return;
        }

        new Thread(() -> {
            final var itemStack = new ItemStack(guiMaterial);
            final var itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(ChatUtil.translate(PSH.getConfig().getString("gui.item.name")));

            final List<String> lore = PSH.getConfig().getStringList("gui.item.description")
                    .stream()
                    .map(ChatUtil::translate)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            Bukkit.getScheduler().runTask(PSH, () -> player.getInventory().setItem(PSH.getConfig().getInt("gui.item.location"), itemStack));
        }).start();
    }
}
