package os.arcadiadevs.playerservers.hubcore.utils;

import com.cryptomorin.xseries.XMaterial;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.PSHubCore;

import java.util.ArrayList;

public class GUIUtils {

    public static void openSelector(Player player) {

        final var PSH = PSHubCore.getInstance();
        final var menu = PSH.spiGUI.create(ChatUtil.translate(PSH.getConfig().getString("gui.menu.name")), 5);

        menu.setAutomaticPaginationEnabled(true);
        menu.setBlockDefaultInteractions(true);

        final var onlineXMaterialOptional = XMaterial.matchXMaterial(PSH.getConfig().getString("gui.menu.online.block"));
        final var offlineXMaterialOptional = XMaterial.matchXMaterial(PSH.getConfig().getString("gui.menu.offline.block"));
        
        final var onlineXMaterial = onlineXMaterialOptional.orElse(XMaterial.EMERALD_BLOCK).parseMaterial();
        final var offlineXMaterial = offlineXMaterialOptional.orElse(XMaterial.REDSTONE_BLOCK).parseMaterial();

        new Thread(() -> {
            final var servers = PSHubCore.getInstance().getServerCache().getServers();

            servers.forEach(server -> {
                final var itemBuilder = new ItemBuilder(server.isOnline() ? onlineXMaterial : offlineXMaterial);

                var lore = PSH.getConfig().getStringList(server.isOnline() ? "gui.menu.online.lore" : "gui.menu.offline.lore");

                lore = lore.stream()
                        .map(s -> s.replaceAll("%server%", server.getUniqueId()))
                        .map(s -> s.replaceAll("%status%", server.isOnline() ? "&aOnline" : "&cOffline"))
                        .map(s -> s.replaceAll("%players%", server.isOnline() ? server.getPlayers() + "" : "0"))
                        .map(s -> s.replaceAll("%maxplayers%", server.isOnline() ? server.getMaxPlayers() + "" : "0"))
                        .map(s -> s.replaceAll("%port%", server.getPort() + ""))
                        .map(s -> s.replaceAll("%motd%", server.isOnline() ? server.getMotd() : "&cOffline"))
                        .map(s -> s.replaceAll("%node%", server.getNode()))
                        .map(s -> s.replaceAll("%owner%", server.getOwner()))
                        .map(s -> s.replaceAll("%ip%", server.getAddress()))
                        .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

                var item = itemBuilder
                        .name(ChatUtil.translate(PSH.getConfig().getString(server.isOnline() ? "gui.menu.online.name" : "gui.menu.offline.name")
                                        .replaceAll("%server%", server.getUniqueId())
                                        .replaceAll("%status%", server.isOnline() ? "&aOnline" : "&cOffline"))
                                        .replaceAll("%players%", server.isOnline() ? server.getPlayers() + "" : "0")
                                        .replaceAll("%maxplayers%", server.isOnline() ? server.getMaxPlayers() + "" : "0")
                                        .replaceAll("%port%", server.getPort() + "")
                                        .replaceAll("%motd%", server.isOnline() ? server.getMotd() : "&cOffline")
                                        .replaceAll("%node%", server.getNode())
                                        .replaceAll("%owner%", server.getOwner())
                                        .replaceAll("%ip%", server.getAddress())
                        )
                        .lore(lore)
                        .build();

                menu.setButton(0, menu.getInventory().firstEmpty(), new SGButton(item).withListener(listener -> BungeeUtil.connectPlayer(listener, player, server.getUniqueId())));
            });

            Bukkit.getScheduler().runTask(PSHubCore.getInstance(), () -> player.openInventory(menu.getInventory()));
        }).start();
    }
}
