package os.arcadiadevs.playerservers.hubcore.utils;

import com.cryptomorin.xseries.XMaterial;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.PSHubCore;
import os.arcadiadevs.playerservers.hubcore.database.DataBase;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;
import os.arcadiadevs.playerservers.hubcore.objects.Server;

import java.util.ArrayList;

public class GUIUtils {

    public static void openSelector(Player player) {

        final var PSH = PSHubCore.getInstance();
        final var menu = PSH.getSpiGUI().create(ChatUtil.translate(PSH.getConfig().getString("gui.selector.menu.name")), 5);

        menu.setAutomaticPaginationEnabled(true);
        menu.setBlockDefaultInteractions(true);

        final var onlineXMaterialOptional = XMaterial.matchXMaterial(PSH.getConfig().getString("gui.selector.menu.online.block"));
        final var offlineXMaterialOptional = XMaterial.matchXMaterial(PSH.getConfig().getString("gui.selector.menu.offline.block"));

        final var onlineXMaterial = onlineXMaterialOptional.orElse(XMaterial.EMERALD_BLOCK).parseMaterial();
        final var offlineXMaterial = offlineXMaterialOptional.orElse(XMaterial.REDSTONE_BLOCK).parseMaterial();

        new Thread(() -> {
            final var servers = PSHubCore.getInstance().getServerCache().getServers();

            servers.forEach(server -> {
                final var itemBuilder = new ItemBuilder(server.getServerStatus() == ServerStatus.ONLINE ? onlineXMaterial : offlineXMaterial);

                var lore = PSH.getConfig().getStringList(server.getServerStatus() == ServerStatus.ONLINE ? "gui.selector.menu.online.lore" : "gui.selector.menu.offline.lore");

                lore = lore.stream()
                        .map(s -> s.replaceAll("%server%", server.getPlayerName()))
                        .map(s -> s.replaceAll("%status%", server.getServerStatus() == ServerStatus.ONLINE ? "&aOnline" : "&cOffline"))
                        .map(s -> s.replaceAll("%players%", server.getServerStatus() == ServerStatus.ONLINE ? server.getData().getOnline() + "" : "0"))
                        .map(s -> s.replaceAll("%maxplayers%", server.getServerStatus() == ServerStatus.ONLINE ? server.getData().getMax() + "" : "0"))
                        .map(s -> s.replaceAll("%port%", server.getPort() + ""))
                        .map(s -> s.replaceAll("%motd%", server.getServerStatus() == ServerStatus.ONLINE ? server.getData().getMOTD() : "&cOffline"))
                        .map(s -> s.replaceAll("%node%", server.getNode()))
                        .map(s -> s.replaceAll("%owner%", server.getPlayerName()))
                        .map(s -> s.replaceAll("%ip%", server.getHostname()))
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

                var item = itemBuilder
                        .name(ChatUtil.translate(PSH.getConfig().getString(server.getServerStatus() == ServerStatus.ONLINE ? "gui.selector.menu.online.name" : "gui.selector.menu.offline.name")
                                .replaceAll("%server%", server.getPlayerName())
                                .replaceAll("%status%", server.getServerStatus() == ServerStatus.ONLINE ? "&aOnline" : "&cOffline"))
                                .replaceAll("%players%", server.getServerStatus() == ServerStatus.ONLINE ? server.getData().getOnline() + "" : "0")
                                .replaceAll("%maxplayers%", server.getServerStatus() == ServerStatus.ONLINE ? server.getData().getMax() + "" : "0")
                                .replaceAll("%port%", server.getPort() + "")
                                .replaceAll("%motd%", server.getServerStatus() == ServerStatus.ONLINE ? server.getData().getMOTD() : "&cOffline")
                                .replaceAll("%node%", server.getNode())
                                .replaceAll("%owner%", server.getPlayerName())
                                .replaceAll("%ip%", server.getHostname())
                        )
                        .lore(lore)
                        .build();

                menu.setButton(0, menu.getInventory().firstEmpty(), new SGButton(item).withListener(listener -> BungeeUtil.connectPlayer(listener, player, server.getPlayerName())));
            });

            Bukkit.getScheduler().runTask(PSHubCore.getInstance(), () -> player.openInventory(menu.getInventory()));
        }).start();
    }

    public static void openMenu(Player player) {

        final var PSH = PSHubCore.getInstance();
        final var menu = PSH.getSpiGUI().create(ChatUtil.translate(PSH.getConfig().getString("gui.player-menu.menu.name")), 4);

        menu.setAutomaticPaginationEnabled(false);
        menu.setBlockDefaultInteractions(true);

        new Thread(() -> {
            if (!DataBase.containsServer(player.getUniqueId().toString())) {
                var itemCreate = new ItemBuilder(XMaterial.DIAMOND_BLOCK.parseMaterial())
                        .name(ChatUtil.translate("&aCreate Server"))
                        .build();

                menu.setButton(0, 13, new SGButton(itemCreate).withListener(listener -> BungeeUtil.createServer(listener, player, player.getDisplayName())));
                Bukkit.getScheduler().runTask(PSHubCore.getInstance(), () -> player.openInventory(menu.getInventory()));
                return;
            }

            final var server = new Server(player);

            var lore = PSH.getConfig().getStringList(server.getServerStatus() == ServerStatus.ONLINE ? "gui.player-menu.menu.online.lore" : "gui.player-menu.menu.offline.lore");

            lore = lore.stream()
                    .map(s -> s.replaceAll("%server%", server.getPlayerName()))
                    .map(s -> s.replaceAll("%status%", server.getServerStatus() == ServerStatus.ONLINE ? "&aOnline" : "&cOffline"))
                    .map(s -> s.replaceAll("%players%", server.getServerStatus() == ServerStatus.ONLINE ? server.getData().getOnline() + "" : "0"))
                    .map(s -> s.replaceAll("%maxplayers%", server.getServerStatus() == ServerStatus.ONLINE ? server.getData().getMax() + "" : "0"))
                    .map(s -> s.replaceAll("%port%", server.getPort() + ""))
                    .map(s -> s.replaceAll("%motd%", server.getServerStatus() == ServerStatus.ONLINE ? server.getData().getMOTD() : "&cOffline"))
                    .map(s -> s.replaceAll("%node%", server.getNode()))
                    .map(s -> s.replaceAll("%owner%", server.getPlayerName()))
                    .map(s -> s.replaceAll("%ip%", server.getHostname()))
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            final var itemPlayer = new ItemBuilder(XMaterial.OAK_SIGN.parseMaterial())
                    .name(ChatUtil.translate(PSH.getConfig().getString(server.getServerStatus() == ServerStatus.ONLINE ? "gui.player-menu.menu.online.name" : "gui.player-menu.menu.offline.name")
                            .replaceAll("%server%", server.getPlayerName())))
                    .lore(lore)
                    .build();

            var itemDelete = new ItemBuilder(XMaterial.BARRIER.parseMaterial())
                    .name("&cDelete Server")
                    .build();

            if (server.getServerStatus() == ServerStatus.ONLINE) {
                var itemJoin = new ItemBuilder(XMaterial.OAK_DOOR.parseMaterial())
                        .name(ChatUtil.translate("&aJoin Server"))
                        .build();
                var itemStop = new ItemBuilder(XMaterial.REDSTONE_BLOCK.parseMaterial())
                        .name(ChatUtil.translate("&cStop Server"))
                        .build();
                menu.setButton(0, 22, new SGButton(itemJoin).withListener(listener -> BungeeUtil.connectPlayer(listener, player, player.getDisplayName())));
                menu.setButton(0, 20, new SGButton(itemStop).withListener(listener -> BungeeUtil.stopServer(listener, player, player.getDisplayName())));
            }

            if (server.getServerStatus() == ServerStatus.OFFLINE) {
                var itemStart = new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial())
                        .name(ChatUtil.translate("&aStart Server"))
                        .build();

                menu.setButton(0, 20, new SGButton(itemStart).withListener(listener -> BungeeUtil.startServer(listener, player, player.getDisplayName())));
            }

            menu.setButton(0, 24, new SGButton(itemDelete).withListener(listener -> BungeeUtil.deleteServer(listener, player, player.getDisplayName())));
            menu.setButton(0, 4, new SGButton(itemPlayer));

            Bukkit.getScheduler().runTask(PSHubCore.getInstance(), () -> player.openInventory(menu.getInventory()));
        }).start();
    }
}
