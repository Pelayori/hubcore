package os.arcadiadevs.playerservers.hubcore.guis;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.dto.ServerRecord;
import os.arcadiadevs.playerservers.hubcore.utils.BungeeUtil;
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;
import os.arcadiadevs.playerservers.hubcore.utils.GuiUtils;
import os.arcadiadevs.playerservers.hubcore.utils.formatter.Formatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SelectorGui {

    private static final int SERVERS_PER_PAGE = 21;

    public static void openGui(Player player) {
        openGui(player, 0);
    }

    public static void openGui(Player player, int page) {
        final var instance = PsHubCore.getInstance();
        final var records = instance.getServerCache().getServers();
        final var menu = instance.getSpiGui()
                .create(ChatUtil.translate(instance.getConfig().getString("gui.selector.menu.name")), 5);

        menu.setAutomaticPaginationEnabled(false);
        menu.setBlockDefaultInteractions(true);

        // Add border
        GuiUtils.addBorder(menu, 5);

        List<ServerRecord> filteredServers = new ArrayList<>(records);
        filteredServers.sort(Comparator.comparing(s -> s.online() ? 0 : 1));

        List<ServerRecord> filteredServersByPlayers = new ArrayList<>(filteredServers);
        filteredServersByPlayers.sort(
            Comparator.comparing(s -> s.players() != null ? -s.players() : 0)
        );

        final var serversPage = instance.getConfig().getBoolean("gui.selector.menu.sort-by-players")
                ? filteredServersByPlayers
                : filteredServers;

        final XMaterial onlinexMaterial = XMaterial.matchXMaterial(instance.getConfig().getString("gui.selector.menu.online.block"))
                .orElse(XMaterial.PLAYER_HEAD);
        final XMaterial offlinexMaterial = XMaterial.matchXMaterial(instance.getConfig().getString("gui.selector.menu.offline.block"))
                .orElse(XMaterial.RED_TERRACOTTA);

        final boolean showOffline = instance.getConfig().getBoolean("gui.selector.menu.show-offline");

        int slot = 10; // Starting slot for servers
        int serverCount = 0;
        for (int i = page * SERVERS_PER_PAGE; i < serversPage.size() && serverCount < SERVERS_PER_PAGE; i++) {
            ServerRecord server = serversPage.get(i);
            if (showOffline || server.online()) {
                boolean online = server.online();
                XMaterial material = online ? onlinexMaterial : offlinexMaterial;
                List<String> lore = Formatter.format(server, instance.getConfig().getStringList(online ? "gui.selector.menu.online.lore" : "gui.selector.menu.offline.lore"));
                String name = Formatter.format(server, instance.getConfig().getString(online ? "gui.selector.menu.online.name" : "gui.selector.menu.offline.name"));

                ItemStack item = new ItemBuilder(material.parseMaterial())
                        .name(name)
                        .lore(lore)
                        .skullOwner(server.name() == null ? "" : server.name())
                        .build();

                menu.setButton(slot, new SGButton(item).withListener(
                        listener -> BungeeUtil.connectPlayer(player, server.name())));

                slot++;
                if ((slot % 9) == 8) slot += 2; // Move to next row if at the end
                serverCount++;
            }
        }

        // Add pagination buttons
        if (page > 0) {
            menu.setButton(18, createPaginationButton("Previous Page", XMaterial.ARROW, () -> openGui(player, page - 1)));
        }
        if ((page + 1) * SERVERS_PER_PAGE < serversPage.size()) {
            menu.setButton(26, createPaginationButton("Next Page", XMaterial.ARROW, () -> openGui(player, page + 1)));
        }
        
        // Add new buttons
        menu.setButton(39, createCommandButton("&eMy Server", 
                Arrays.asList("&7", "&7Open your server menu", "&7", "&eClick to open!"), 
                XMaterial.COMPARATOR, player, "/pguy"));
        menu.setButton(40, createCommandButton("&bServer Guide", 
                Arrays.asList("&7", "&7Open the server guide", "&7", "&eClick to open!"), 
                XMaterial.BOOK, player, "/guide"));
        menu.setButton(41, createCommandButton("&aGet a Server", 
                Arrays.asList("&7", "&7Buy your own server", "&7", "&eClick to open!"), 
                XMaterial.EMERALD, player, "/buysrv"));

        XSound.BLOCK_NOTE_BLOCK_BASS.play(player);
        player.openInventory(menu.getInventory());
    }

    private static SGButton createPaginationButton(String name, XMaterial material, Runnable action) {
        ItemStack item = new ItemBuilder(material.parseMaterial())
                .name(ChatUtil.translate("&a" + name))
                .build();
        return new SGButton(item).withListener(event -> action.run());
    }

    private static SGButton createCommandButton(String name, List<String> lore, XMaterial material, Player player, String command) {
        ItemStack item = new ItemBuilder(material.parseMaterial())
                .name(ChatUtil.translate(name))
                .lore(ChatUtil.translate(lore))
                .build();
        return new SGButton(item).withListener(event -> {
            player.closeInventory();
            player.performCommand(command.substring(1)); // Remove the leading '/'
        });
    }
}
