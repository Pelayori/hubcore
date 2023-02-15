package os.arcadiadevs.playerservers.hubcore.events;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.guis.SelectorGui;
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;
import os.arcadiadevs.playerservers.hubcore.guis.PlayerMenuGui;

/**
 * A class that handles all click/interact events for the hubcore plugin.
 *
 * @author ArcadiaDevs
 */
public class ClickEvent implements Listener {

  private final PsHubCore instance;

  public ClickEvent(PsHubCore instance) {
    this.instance = instance;
  }

  /**
   * Executes action on player interact event.
   *
   * @param event the event
   */
  @EventHandler
  public void onClick(PlayerInteractEvent event) {
    final var player = event.getPlayer();

    if (event.getAction() != Action.RIGHT_CLICK_AIR
        && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
      return;
    }

    if (instance.getConfig().getBoolean("gui.selector.item.enabled")
        && player.getInventory().getHeldItemSlot()
        == PsHubCore.getInstance().getConfig().getInt("gui.selector.item.location")) {
      if (!player.hasPermission("hubcore.selector")) {
        ChatUtil.sendMessage(player, instance.getConfig().getString("messages.no-permission"));
        return;
      }
      SelectorGui.openGui(player);
      event.setCancelled(true);
    }

    if (instance.getConfig().getBoolean("gui.player-menu.item.enabled")
        && player.getInventory().getHeldItemSlot()
        == PsHubCore.getInstance().getConfig().getInt("gui.player-menu.item.location")) {
      if (!player.hasPermission("hubcore.player-menu")) {
        ChatUtil.sendMessage(player, instance.getConfig().getString("messages.no-permission"));
        return;
      }

      PlayerMenuGui.openGui(player);
      event.setCancelled(true);
    }
  }

  /**
   * Prevents player from moving items in their inventory.
   *
   * @param event The event.
   */
  @EventHandler
  public void inventory(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof final Player player)) {
      return;
    }

    final var inventoryType = event.getInventory().getType();
    final var selectorItemSlot =
        PsHubCore.getInstance().getConfig().getInt("gui.selector.item.location");
    final var menuItemSlot =
        PsHubCore.getInstance().getConfig().getInt("gui.player-menu.item.location");

    if (instance.getConfig().getBoolean("gui.selector.item.enabled")) {
      if (event.getSlot() == selectorItemSlot
              && (inventoryType == InventoryType.PLAYER
              || inventoryType == InventoryType.CRAFTING)) {
        if (player.getGameMode() == GameMode.CREATIVE) {
          player.closeInventory();
        }

        event.setCancelled(true);
      }
    }

    if (instance.getConfig().getBoolean("gui.player-menu.item.enabled")) {
      if (event.getSlot() == menuItemSlot
              && (inventoryType == InventoryType.PLAYER
              || inventoryType == InventoryType.CRAFTING)) {
        if (player.getGameMode() == GameMode.CREATIVE) {
          player.closeInventory();
        }

        event.setCancelled(true);
      }
    }
  }

  /**
   * Prevents players from dropping the selector item.
   *
   * @param event The event.
   */
  @EventHandler
  public void drop(PlayerDropItemEvent event) {
    final var player = event.getPlayer();

    if (player.getInventory().getHeldItemSlot()
        == PsHubCore.getInstance().getConfig().getInt("gui.selector.item.location")
        || player.getInventory().getHeldItemSlot()
        == PsHubCore.getInstance().getConfig().getInt("gui.player-menu.item.location")
    ) {
      event.setCancelled(true);
    }
  }

}
