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
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;
import os.arcadiadevs.playerservers.hubcore.utils.GUIUtils;

public class ClickEvent implements Listener  {

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        final PSHubCore PSH = PSHubCore.getInstance();
        final var player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (PSH.getConfig().getBoolean("gui.selector.item.enabled") && player.getInventory().getHeldItemSlot() == PSHubCore.getInstance().getConfig().getInt("gui.selector.item.location")) {
            if (!player.hasPermission("hubcore.selector")) {
                player.sendMessage(ChatUtil.translate(PSH.getConfig().getString("messages.no-permission")));
                return;
            }
            GUIUtils.openSelector(player);
            event.setCancelled(true);
        }

        if (PSH.getConfig().getBoolean("gui.player-menu.item.enabled") && player.getInventory().getHeldItemSlot() == PSHubCore.getInstance().getConfig().getInt("gui.player-menu.item.location")) {
            if (!player.hasPermission("hubcore.player-menu")) {
                player.sendMessage(ChatUtil.translate(PSH.getConfig().getString("messages.no-permission")));
                return;
            }
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
        final var selectorItemSlot = PSHubCore.getInstance().getConfig().getInt("gui.selector.item.location");
        final var menuItemSlot = PSHubCore.getInstance().getConfig().getInt("gui.player-menu.item.location");

        if (event.getSlot() == selectorItemSlot && (inventoryType == InventoryType.PLAYER || inventoryType == InventoryType.CRAFTING)
        || event.getSlot() == menuItemSlot && (inventoryType == InventoryType.PLAYER || inventoryType == InventoryType.CRAFTING)) {
            if (player.getGameMode() == GameMode.CREATIVE) {
                player.closeInventory();
            }

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent event) {
        final var player = event.getPlayer();

        if (player.getInventory().getHeldItemSlot() == PSHubCore.getInstance().getConfig().getInt("gui.selector.item.location") ||
                player.getInventory().getHeldItemSlot() == PSHubCore.getInstance().getConfig().getInt("gui.player-menu.item.location")) {
            event.setCancelled(true);
        }
    }

}
