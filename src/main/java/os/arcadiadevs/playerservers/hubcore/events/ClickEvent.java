package os.arcadiadevs.playerservers.hubcore.events;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import os.arcadiadevs.playerservers.hubcore.PSHubCore;
import os.arcadiadevs.playerservers.hubcore.utils.ColorUtils;
import os.arcadiadevs.playerservers.hubcore.utils.GUIUtils;

import java.util.Objects;

import static os.arcadiadevs.playerservers.hubcore.utils.ColorUtils.translate;

@SuppressWarnings("UnstableApiUsage")
public class ClickEvent implements Listener  {

    @EventHandler
    public void onClick(PlayerInteractEvent e) {

        Player player = e.getPlayer();

        if ((e.getAction() == Action.RIGHT_CLICK_AIR ||
                e.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                player.getInventory().getItemInHand().getItemMeta() != null && player.getInventory().getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(translate(PSHubCore.getInstance().getConfig().getString("compass-name")))) {

            GUIUtils gu = new GUIUtils();
            gu.openSelector(player);

            e.setCancelled(true);
        }

    }

    @EventHandler
    public void inventory(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null
                && e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase(translate(PSHubCore.getInstance().getConfig().getString("compass-name"))))
            e.setCancelled(true);

        else if (e.getView().getTitle().equalsIgnoreCase(translate("&aServer Selector"))) {
            if (e.getCurrentItem() != null && e.getCurrentItem().getItemMeta() != null && e.getCurrentItem().getItemMeta().getLore() != null) {
                if (e.getCurrentItem().getType() == XMaterial.EMERALD_BLOCK.parseMaterial()) {
                    String UUID = e.getCurrentItem().getItemMeta().getLore().get(1).split(" ")[1].replaceAll("§7", "");
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Connect");
                    out.writeUTF(UUID);

                    p.sendPluginMessage(PSHubCore.getInstance(), "BungeeCord", out.toByteArray());
                    e.getWhoClicked().closeInventory();
                } else if (e.getCurrentItem().getType() == XMaterial.REDSTONE_BLOCK.parseMaterial())
                    p.sendMessage(ColorUtils.translate("&9PlayerServers> &7Oops, the server you tried to connect to is offline."));
                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void drop(PlayerDropItemEvent e) {
        if (Objects.requireNonNull(e.getItemDrop().getItemStack().getItemMeta()).getDisplayName().equalsIgnoreCase(translate(PSHubCore.getInstance().getConfig().getString("compass-name")))) {
            e.setCancelled(true);
        }
    }

}
