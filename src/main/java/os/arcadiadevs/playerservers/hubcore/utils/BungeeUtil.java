package os.arcadiadevs.playerservers.hubcore.utils;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import os.arcadiadevs.playerservers.hubcore.PSHubCore;

public class BungeeUtil {

    public static void connectPlayer(InventoryClickEvent event, Player player, String server) {
        if (event.getCurrentItem().getType() == XMaterial.REDSTONE_BLOCK.parseMaterial()) {
            player.sendMessage(ChatUtil.translate("&9PlayerServers> &7Oops, the server you tried to connect to is offline."));
            return;
        }

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(PSHubCore.getInstance(), "BungeeCord", out.toByteArray());
        event.getWhoClicked().closeInventory();
    }

}
