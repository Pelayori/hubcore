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
import os.arcadiadevs.playerservers.hubcore.PSHubCore;
import os.arcadiadevs.playerservers.hubcore.utils.GUIUtils;

public class ClickEvent implements Listener  {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        final var player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (player.getInventory().getHeldItemSlot() == PSHubCore.getInstance().getConfig().getInt("gui.item.location")) {
            GUIUtils.openSelector(player);
            event.setCancelled(true);
        }
        if (player.getInventory().getHeldItemSlot() == PSHubCore.getInstance().getConfig().getInt("gui.item.location1")) {
            GUIUtils.openMenu(player);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void inventory(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        final var player = (Player) event.getWhoClicked();
        final var inventoryType = event.getInventory().getType();
        final var slot = PSHubCore.getInstance().getConfig().getInt("gui.item.location");
        final var slot1 = PSHubCore.getInstance().getConfig().getInt("gui.item.location1");

        if (event.getSlot() == slot && (inventoryType == InventoryType.PLAYER || inventoryType == InventoryType.CRAFTING)
        || event.getSlot() == slot1 && (inventoryType == InventoryType.PLAYER || inventoryType == InventoryType.CRAFTING)) {
            if (player.getGameMode() == GameMode.CREATIVE) {
                player.closeInventory();
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent event) {
        final var player = event.getPlayer();

        if (player.getInventory().getHeldItemSlot() == PSHubCore.getInstance().getConfig().getInt("gui.item.location") ||
                player.getInventory().getHeldItemSlot() == PSHubCore.getInstance().getConfig().getInt("gui.item.location1")) {
            event.setCancelled(true);
        }
    }

}
