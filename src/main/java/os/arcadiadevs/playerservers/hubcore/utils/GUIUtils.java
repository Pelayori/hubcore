package os.arcadiadevs.playerservers.hubcore.utils;

import com.cryptomorin.xseries.XMaterial;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.Bukkit;
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
                final var itemBuilder = new ItemBuilder(server.getCachedStatus() == ServerStatus.ONLINE ? onlineXMaterial : offlineXMaterial);

                var lore = PSH.getConfig().getStringList(server.getCachedStatus() == ServerStatus.ONLINE ? "gui.selector.menu.online.lore" : "gui.selector.menu.offline.lore");

                System.out.println("test1");

                lore = lore.stream()
                        .map(s -> s.replaceAll("%server%", server.getPlayerName()))
                        .map(s -> s.replaceAll("%status%", server.getCachedStatus() == ServerStatus.ONLINE ? "&aOnline" : "&cOffline"))
                        .map(s -> s.replaceAll("%players%", server.getCachedStatus() == ServerStatus.ONLINE ? server.getCachedData().getOnline() + "" : "0"))
                        .map(s -> s.replaceAll("%maxplayers%", server.getCachedStatus() == ServerStatus.ONLINE ? server.getCachedData().getMax() + "" : "0"))
                        .map(s -> s.replaceAll("%port%", server.getPort() + ""))
                        .map(s -> s.replaceAll("%motd%", server.getCachedStatus() == ServerStatus.ONLINE ? server.getCachedData().getMOTD() : "&cOffline"))
                        .map(s -> s.replaceAll("%node%", server.getNode()))
                        .map(s -> s.replaceAll("%owner%", server.getPlayerName()))
                        .map(s -> s.replaceAll("%ip%", server.getHostname()))
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

                System.out.println("test2");

                var item = itemBuilder
                        .name(ChatUtil.translate(PSH.getConfig().getString(server.getCachedStatus() == ServerStatus.ONLINE ? "gui.selector.menu.online.name" : "gui.selector.menu.offline.name")
                                .replaceAll("%server%", server.getPlayerName())
                                .replaceAll("%status%", server.getCachedStatus() == ServerStatus.ONLINE ? "&aOnline" : "&cOffline"))
                                .replaceAll("%players%", server.getCachedStatus() == ServerStatus.ONLINE ? server.getCachedData().getOnline() + "" : "0")
                                .replaceAll("%maxplayers%", server.getCachedStatus() == ServerStatus.ONLINE ? server.getCachedData().getMax() + "" : "0")
                                .replaceAll("%port%", server.getPort() + "")
                                .replaceAll("%motd%", server.getCachedStatus() == ServerStatus.ONLINE ? server.getCachedData().getMOTD() : "&cOffline")
                                .replaceAll("%node%", server.getNode())
                                .replaceAll("%owner%", server.getPlayerName())
                                .replaceAll("%ip%", server.getHostname())
                        )
                        .lore(lore)
                        .build();

                System.out.println("test3");

                menu.setButton(0, menu.getInventory().firstEmpty(), new SGButton(item).withListener(listener -> BungeeUtil.connectPlayer(listener, player, server.getPlayerName())));
            });

            Bukkit.getScheduler().runTask(PSHubCore.getInstance(), () -> player.openInventory(menu.getInventory()));
        }).start();
    }

    public static void openMenu(Player player) {

        final var PSH = PSHubCore.getInstance();

        new Thread(() -> {
            if (!DataBase.containsServer(player.getUniqueId().toString())) {
                final var menu = PSH.getSpiGUI().create(ChatUtil.translate(PSH.getConfig().getString("gui.player-menu.menu.name")), 3);
                menu.setAutomaticPaginationEnabled(false);
                menu.setBlockDefaultInteractions(true);

                var itemCreate = new ItemBuilder(XMaterial.DIAMOND_BLOCK.parseMaterial())
                        .name(PSH.getConfig().getString("gui.player-menu.menu.create.name"))
                        .lore(PSH.getConfig().getStringList("gui.player-menu.menu.create.lore"))
                        .build();

                menu.setButton(0, 13, new SGButton(itemCreate).withListener(listener -> BungeeUtil.createServer(listener, player, player.getDisplayName())));
                Bukkit.getScheduler().runTask(PSHubCore.getInstance(), () -> player.openInventory(menu.getInventory()));
                return;
            }

            final var menu = PSH.getSpiGUI().create(ChatUtil.translate(PSH.getConfig().getString("gui.player-menu.menu.name")), 4);
            menu.setAutomaticPaginationEnabled(false);
            menu.setBlockDefaultInteractions(true);

            final var deleteConfirmationMenu = PSH.getSpiGUI().create(ChatUtil.translate(PSH.getConfig().getString("gui.player-menu.menu.delete.confirmation.name")), 3);
            deleteConfirmationMenu.setAutomaticPaginationEnabled(false);
            deleteConfirmationMenu.setBlockDefaultInteractions(true);

            final var stopConfirmationMenu = PSH.getSpiGUI().create(ChatUtil.translate(PSH.getConfig().getString("gui.player-menu.menu.stop.confirmation.name")), 3);
            stopConfirmationMenu.setAutomaticPaginationEnabled(false);
            stopConfirmationMenu.setBlockDefaultInteractions(true);

            final var server = new Server(player);

            var lore = PSH.getConfig().getStringList(server.getCachedStatus() == ServerStatus.ONLINE ? "gui.player-menu.menu.info.online.lore" : "gui.player-menu.menu.info.offline.lore");

            lore = lore.stream()
                    .map(s -> s.replaceAll("%server%", server.getPlayerName()))
                    .map(s -> s.replaceAll("%status%", server.getCachedStatus() == ServerStatus.ONLINE ? "&aOnline" : "&cOffline"))
                    .map(s -> s.replaceAll("%players%", server.getCachedStatus() == ServerStatus.ONLINE ? server.getCachedData().getOnline() + "" : "0"))
                    .map(s -> s.replaceAll("%maxplayers%", server.getCachedStatus() == ServerStatus.ONLINE ? server.getCachedData().getMax() + "" : "0"))
                    .map(s -> s.replaceAll("%port%", server.getPort() + ""))
                    .map(s -> s.replaceAll("%motd%", server.getCachedStatus() == ServerStatus.ONLINE ? server.getCachedData().getMOTD() : "&cOffline"))
                    .map(s -> s.replaceAll("%node%", server.getNode()))
                    .map(s -> s.replaceAll("%owner%", server.getPlayerName()))
                    .map(s -> s.replaceAll("%ip%", server.getHostname()))
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

            final var itemPlayer = new ItemBuilder(XMaterial.OAK_SIGN.parseMaterial())
                    .name(ChatUtil.translate(PSH.getConfig().getString(server.getCachedStatus() == ServerStatus.ONLINE ? "gui.player-menu.menu.info.online.name" : "gui.player-menu.menu.info.offline.name")
                            .replaceAll("%server%", server.getPlayerName())))
                    .lore(lore)
                    .build();

            final var itemDelete = new ItemBuilder(XMaterial.BARRIER.parseMaterial())
                    .name(ChatUtil.translate(PSH.getConfig().getString("gui.player-menu.menu.delete.name")))
                    .lore(PSH.getConfig().getStringList("gui.player-menu.menu.delete.lore"))
                    .build();

            final var itemQuestionYes = new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial())
                    .name(ChatUtil.translate(PSH.getConfig().getString("gui.player-menu.menu.delete.confirmation.accept.name")))
                    .lore(PSH.getConfig().getStringList("gui.player-menu.menu.delete.confirmation.accept.lore"))
                    .build();

            final var itemQuestionNo = new ItemBuilder(XMaterial.REDSTONE_BLOCK.parseMaterial())
                    .name(ChatUtil.translate(PSH.getConfig().getString("gui.player-menu.menu.delete.confirmation.decline.name")))
                    .lore(PSH.getConfig().getStringList("gui.player-menu.menu.delete.confirmation.decline.lore"))
                    .build();

            if (server.getCachedStatus() == ServerStatus.ONLINE) {
                final var itemJoin = new ItemBuilder(XMaterial.OAK_DOOR.parseMaterial())
                        .name(ChatUtil.translate(PSH.getConfig().getString("gui.player-menu.menu.join.name")))
                        .lore(PSH.getConfig().getStringList("gui.player-menu.menu.join.lore"))
                        .build();
                final var itemStop = new ItemBuilder(XMaterial.REDSTONE_BLOCK.parseMaterial())
                        .name(ChatUtil.translate(PSH.getConfig().getString("gui.player-menu.menu.stop.name")))
                        .lore(PSH.getConfig().getStringList("gui.player-menu.menu.stop.lore"))
                        .build();

                menu.setButton(0, 22, new SGButton(itemJoin).withListener(listener -> BungeeUtil.connectPlayer(listener, player, player.getDisplayName())));
                menu.setButton(0, 20, new SGButton(itemStop).withListener(listener -> {
                    stopConfirmationMenu.setButton(0, 11, new SGButton(itemQuestionYes).withListener(listener2 -> {
                        BungeeUtil.stopServer(listener2, player, player.getDisplayName());
                        player.closeInventory();
                    }));
                    stopConfirmationMenu.setButton(0, 15, new SGButton(itemQuestionNo).withListener(listener2 -> {
                        player.closeInventory();
                    }));
                    player.openInventory(stopConfirmationMenu.getInventory());
                }));
            }

            if (server.getCachedStatus() == ServerStatus.OFFLINE) {
                final var itemStart = new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial())
                        .name(ChatUtil.translate(PSH.getConfig().getString("gui.player-menu.menu.start.name")))
                        .lore(PSH.getConfig().getStringList("gui.player-menu.menu.start.lore"))
                        .build();

                menu.setButton(0, 20, new SGButton(itemStart).withListener(listener -> BungeeUtil.startServer(listener, player, player.getDisplayName())));
            }

            menu.setButton(0, 24, new SGButton(itemDelete).withListener(listener -> {
                deleteConfirmationMenu.setButton(0, 11, new SGButton(itemQuestionYes).withListener(listener2 -> {
                    BungeeUtil.deleteServer(listener2, player, player.getDisplayName());
                    player.closeInventory();
                }));
                deleteConfirmationMenu.setButton(0, 15, new SGButton(itemQuestionNo).withListener(listener2 -> {
                    player.closeInventory();
                }));
                player.openInventory(deleteConfirmationMenu.getInventory());
            }));

            menu.setButton(0, 4, new SGButton(itemPlayer));

            Bukkit.getScheduler().runTask(PSHubCore.getInstance(), () -> player.openInventory(menu.getInventory()));
        }).start();
    }
}
