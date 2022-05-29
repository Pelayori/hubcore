package os.arcadiadevs.playerservers.hubcore.utils;

import com.cryptomorin.xseries.XMaterial;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.PSHubCore;
import os.arcadiadevs.playerservers.hubcore.database.DataBase;
import os.arcadiadevs.playerservers.hubcore.database.structures.DBInfoStructure;
import os.arcadiadevs.playerservers.hubcore.database.structures.PingInfoStructure;

import java.util.Map;
import java.util.Objects;

import static os.arcadiadevs.playerservers.hubcore.utils.ColorUtils.translate;

public class GUIUtils {


    public void openSelector(Player player) {

        final DataBase db = new DataBase();
        final PingUtil pu = new PingUtil();
        final PSHubCore PSH = PSHubCore.getInstance();

        final SGMenu menu = PSH.spiGUI.create(ChatColor.GREEN + "Server Selector " + ChatColor.GRAY + "(Page {currentPage}/{maxPage})", 5);
        final Map<String, Object> map = PSH.multinode.getTable("servers").toMap();

        menu.setAutomaticPaginationEnabled(true);

        Bukkit.getScheduler().runTaskAsynchronously(PSH, () -> {
                for (DBInfoStructure is : db.getServersInfo()) {
                    if (pu.isOnline(PSH.getConfig().getBoolean("multi-node") ? map.get(is.getNode()).toString() : "127.0.0.1", is.getPort())) {

                        final PingInfoStructure pus = pu.getData(Integer.parseInt(is.getPort()));

                        menu.setButton(0, menu.getInventory().firstEmpty(), new SGButton(
                                new ItemBuilder(Objects.requireNonNull(XMaterial.EMERALD_BLOCK.parseMaterial()))
                                        .name(translate("&a" + is.getPlayerName() + "'s server"))
                                        .lore(
                                                translate("&7UUID: &a" + is.getServerID().split("-")[0]),
                                                translate("&7Port: &a" + is.getPort()),
                                                translate(String.format("&cOnline: &7%d/%d", pus.getOnline(), pus.getMax())),
                                                translate("&7MOTD: &a" + pus.getMOTD())
                                        )
                                        .build()
                        ));
                    }
                }

                for (DBInfoStructure is : db.getServersInfo()) {
                    if (!pu.isOnline(PSH.getConfig().getBoolean("multi-node") ? map.get(is.getNode()).toString() : "127.0.0.1", is.getPort())) {

                        menu.setButton(0, menu.getInventory().firstEmpty(), new SGButton(
                                new ItemBuilder(Objects.requireNonNull(XMaterial.REDSTONE_BLOCK.parseMaterial()))
                                        .name(translate("&c" + is.getPlayerName() + "'s server"))
                                        .lore(
                                                translate("&7Status: &cOffline"),
                                                translate("&7UUID: &c" + is.getServerID().split("-")[0]),
                                                translate("&7Port: &c" + is.getPort())
                                        )
                                        .build()
                        ));
                    }
                }
            Bukkit.getScheduler().runTask(PSH, () -> player.openInventory(menu.getInventory()));
        });
    }
}
