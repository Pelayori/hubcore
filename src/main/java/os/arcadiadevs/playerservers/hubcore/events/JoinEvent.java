package os.arcadiadevs.playerservers.hubcore.events;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;

import java.util.ArrayList;
import java.util.List;
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;

/**
 * Handles player join events.
 *
 * @author ArcadiaDevs
 */
public class JoinEvent implements Listener {

  private final PsHubCore instance;

  public JoinEvent(PsHubCore instance) {
    this.instance = instance;
  }

  /**
   * Handles player join events.
   *
   * @param event The event.
   */
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {

    final var selectorConfigMaterial = instance.getConfig().getString("gui.selector.item.material");
    final var menuConfigMaterial = instance.getConfig().getString("gui.player-menu.item.material");
    final var selectorMaterial =
        XMaterial.matchXMaterial(selectorConfigMaterial).orElse(XMaterial.COMPASS).parseMaterial();
    final var menuMaterial =
        XMaterial.matchXMaterial(menuConfigMaterial).orElse(XMaterial.EMERALD).parseMaterial();
    final var player = event.getPlayer();

    if (!instance.getConfig().getBoolean("gui.selector.item.enabled")) {
      player.getInventory().forEach(item -> {
        if (item != null && item.getType() == selectorMaterial) {
          player.getInventory().remove(item);
        }
      });
    }

    if (!instance.getConfig().getBoolean("gui.player-menu.item.enabled")) {
      player.getInventory().forEach(item -> {
        if (item != null && item.getType() == menuMaterial) {
          player.getInventory().remove(item);
        }
      });
    }

    new Thread(() -> {
      final var selectorItemStack = new ItemStack(selectorMaterial);
      final var menuItemStack = new ItemStack(menuMaterial);
      final var selectorItemMeta = selectorItemStack.getItemMeta();
      final var menuItemMeta = menuItemStack.getItemMeta();

      selectorItemMeta.setDisplayName(
          ChatUtil.translate(instance.getConfig().getString("gui.selector.item.name")));
      menuItemMeta.setDisplayName(
          ChatUtil.translate(instance.getConfig().getString("gui.player-menu.item.name")));

      final List<String> lore = instance.getConfig().getStringList("gui.selector.item.description")
          .stream()
          .map(ChatUtil::translate)
          .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

      final List<String> lore1 = instance.getConfig().getStringList("gui.player-menu.item.description")
          .stream()
          .map(ChatUtil::translate)
          .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

      selectorItemMeta.setLore(lore);
      menuItemMeta.setLore(lore1);
      selectorItemStack.setItemMeta(selectorItemMeta);
      menuItemStack.setItemMeta(menuItemMeta);

      if (instance.getConfig().getBoolean("gui.selector.item.enabled")) {
        Bukkit.getScheduler().runTask(instance, () -> player.getInventory()
            .setItem(instance.getConfig().getInt("gui.selector.item.location"), selectorItemStack));
      }

      if (instance.getConfig().getBoolean("gui.player-menu.item.enabled")) {
        Bukkit.getScheduler().runTask(instance, () -> player.getInventory()
            .setItem(instance.getConfig().getInt("gui.player-menu.item.location"), menuItemStack));
      }
    }).start();

  }
}
