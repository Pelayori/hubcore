package os.arcadiadevs.playerservers.hubcore.utils;

import com.cryptomorin.xseries.XMaterial;
import com.samjakob.spigui.SGMenu;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.Bukkit;
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

        final SGMenu menu = PSH.spiGUI.create(ChatUtil.translate("&aServer Selector"), 5);
        final Map<String, Object> map = PSHubCore.getInstance().multinode.getTable("servers").toMap();

        menu.setAutomaticPaginationEnabled(true);
        menu.setBlockDefaultInteractions(true);

        new Thread(() -> {
            final var servers = db.getServersInfo();

            // Sort servers by pu.isOnline(server)
            servers.sort((s1, s2) -> {
                final var pingUtil1 = new PingUtil(map.get(s1.getNode()).toString().split(" ")[0].replaceAll(":8080", ""), s1.getPort());
                final var pingUtil2 = new PingUtil(map.get(s2.getNode()).toString().split(" ")[0].replaceAll(":8080", ""), s2.getPort());

                final var p1 = pingUtil1.isOnline();
                final var p2 = pingUtil2.isOnline();

                if (p1 && !p2) {
                    return -1;
                } else if (!p1 && p2) {
                    return 1;
                } else {
                    return 0;
                }
            });

            servers.forEach(server -> {
                final var pingUtil = new PingUtil(map.get(server.getNode()).toString().split(" ")[0].replaceAll(":8080", ""), server.getPort());

                final var itemBuilder = new ItemBuilder(Objects.requireNonNull(
                        pingUtil.isOnline() ? XMaterial.EMERALD_BLOCK.parseMaterial() : XMaterial.REDSTONE_BLOCK.parseMaterial()
                ));

                final ItemStack item;

                if (pingUtil.isOnline()) {
                    item = itemBuilder
                            .name(ChatUtil.translate("&a" + server.getPlayerName() + "'s server"))
                            .lore(
                                    ChatUtil.translate("&7UUID: &a" + server.getServerId().split("-")[0]),
                                    ChatUtil.translate("&7Port: &a" + server.getPort()),
                                    ChatUtil.translate(String.format("&7Online: &a%d/%d", pingUtil.getData().getOnline(), pingUtil.getData().getMax())),
                                    ChatUtil.translate("&7MOTD: &a" + pingUtil.getData().getMOTD())
                            )
                            .build();
                } else {
                    item = itemBuilder
                            .name(ChatUtil.translate("&c" + server.getPlayerName() + "'s server"))
                            .lore(
                                    ChatUtil.translate("&7UUID: &c" + server.getServerId().split("-")[0]),
                                    ChatUtil.translate("&7Port: &c" + server.getPort())
                            )
                            .build();
                }

                menu.setButton(0, menu.getInventory().firstEmpty(), new SGButton(item).withListener(listener -> BungeeUtil.connectPlayer(listener, player, server.getServerId())));
            });

            Bukkit.getScheduler().runTask(PSHubCore.getInstance(), () -> player.openInventory(menu.getInventory()));
        }).start();
    }
}
