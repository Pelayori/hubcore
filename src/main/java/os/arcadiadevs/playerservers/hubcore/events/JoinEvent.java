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

        final var itemMaterial = PSH.getConfig().getString("gui.selector.item.material");
        final var itemMaterial1 = PSH.getConfig().getString("gui.player-menu.item.material");
        final var selectorMaterial = XMaterial.matchXMaterial(itemMaterial).orElse(XMaterial.COMPASS).parseMaterial();
        final var selectorMaterial1 = XMaterial.matchXMaterial(itemMaterial1).orElse(XMaterial.EMERALD).parseMaterial();
        final var player = e.getPlayer();
        
        if (!PSH.getConfig().getBoolean("gui.selector.item.enabled")) {
            player.getInventory().forEach(item -> {
                if (item.getType() == selectorMaterial) {
                    player.getInventory().remove(item);
                }
            });
            return;
        }

        if (!PSH.getConfig().getBoolean("gui.player-menu.item.enabled")) {
            player.getInventory().forEach(item -> {
                if (item.getType() == selectorMaterial1) {
                    player.getInventory().remove(item);
                }
            });
            return;
        }

        new Thread(() -> {
            final var itemStack = new ItemStack(selectorMaterial);
            final var itemStack1 = new ItemStack(selectorMaterial1);
            final var itemMeta = itemStack.getItemMeta();
            final var itemMeta1 = itemStack1.getItemMeta();

            itemMeta.setDisplayName(ChatUtil.translate(PSH.getConfig().getString("gui.selector.item.name")));
            itemMeta1.setDisplayName(ChatUtil.translate(PSH.getConfig().getString("gui.player-menu.item.name")));

            final List<String> lore = PSH.getConfig().getStringList("gui.selector.item.description")
                    .stream()
                    .map(ChatUtil::translate)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            final List<String> lore1 = PSH.getConfig().getStringList("gui.player-menu.item.description")
                    .stream()
                    .map(ChatUtil::translate)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            itemMeta.setLore(lore);
            itemMeta1.setLore(lore1);
            itemStack.setItemMeta(itemMeta);
            itemStack1.setItemMeta(itemMeta1);

            Bukkit.getScheduler().runTask(PSH, () -> player.getInventory().setItem(PSH.getConfig().getInt("gui.selector.item.location"), itemStack));
            Bukkit.getScheduler().runTask(PSH, () -> player.getInventory().setItem(PSH.getConfig().getInt("gui.player-menu.item.location"), itemStack1));
        }).start();

    }
}
