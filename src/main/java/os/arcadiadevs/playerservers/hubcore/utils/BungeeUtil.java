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

        try {
            out.writeUTF("Connect");
            out.writeUTF(server);
        } catch (Exception e) {
            e.printStackTrace();
        }

        player.sendPluginMessage(PSHubCore.getInstance(), "BungeeCord", out.toByteArray());
        event.getWhoClicked().closeInventory();
    }

    public static void stopServer(InventoryClickEvent event, Player player, String server) {

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        try {
            out.writeUTF("stop");
            out.writeUTF(server);
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(PSHubCore.getInstance(), "BungeeCord", out.toByteArray());
        event.getWhoClicked().closeInventory();
    }

    public static void deleteServer(InventoryClickEvent event, Player player, String server) {

        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        try {
            out.writeUTF("delete");
            out.writeUTF(server);
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(PSHubCore.getInstance(), "BungeeCord", out.toByteArray());
        event.getWhoClicked().closeInventory();
    }

    public static void createServer(InventoryClickEvent event, Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        try {
            out.writeUTF("create");
            out.writeUTF(server);
        } catch (Exception e) {
            e.printStackTrace();
        }
        player.sendPluginMessage(PSHubCore.getInstance(), "BungeeCord", out.toByteArray());
        event.getWhoClicked().closeInventory();
    }

}