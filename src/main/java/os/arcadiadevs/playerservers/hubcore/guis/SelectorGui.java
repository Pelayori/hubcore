package os.arcadiadevs.playerservers.hubcore.guis;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import com.samjakob.spigui.menu.SGMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.dto.ServerRecord;
import os.arcadiadevs.playerservers.hubcore.utils.BungeeUtil;
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;
import os.arcadiadevs.playerservers.hubcore.utils.formatter.Formatter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SelectorGui {

    public static void openGui(Player player) {
        final var instance = PsHubCore.getInstance();
        final var records = PsHubCore.getInstance().getServerCache().getServers();
        final var menu = instance.getSpiGui()
                .create(ChatUtil.translate(instance.getConfig().getString("gui.selector.menu.name")), 5);

        menu.setAutomaticPaginationEnabled(false);
        menu.setBlockDefaultInteractions(true);

        // Add border
        addBorder(menu, 5);

        List<ServerRecord> filteredServers = new ArrayList<>(records);
        filteredServers.sort(Comparator.comparing(s -> s.online() ? 0 : 1));

        List<ServerRecord> filteredServersByPlayers = new ArrayList<>(filteredServers);
        filteredServersByPlayers.sort(Comparator.comparing(s -> s.players() != null ? -s.players() : 0));

        final var serversPage = instance.getConfig().getBoolean("gui.selector.menu.sort-by-players")
                ? filteredServersByPlayers
                : filteredServers;

        final XMaterial onlinexMaterial = XMaterial.matchXMaterial(instance.getConfig().getString("gui.selector.menu.online.block"))
                .orElse(XMaterial.PLAYER_HEAD);
        final XMaterial offlinexMaterial = XMaterial.matchXMaterial(instance.getConfig().getString("gui.selector.menu.offline.block"))
                .orElse(XMaterial.RED_TERRACOTTA);

        final boolean showOffline = instance.getConfig().getBoolean("gui.selector.menu.show-offline");

        int slot = 10; // Starting slot for servers
        for (ServerRecord server : serversPage) {
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
                if (slot > 34) break; // Stop if we've filled the 3x7 grid
            }
        }

        XSound.BLOCK_NOTE_BLOCK_BASS.play(player);
        player.openInventory(menu.getInventory());
    }

    private static void addBorder(SGMenu menu, int rows) {
        ItemStack borderItem = new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()).build();
        SGButton borderButton = new SGButton(borderItem);

        // Top and bottom rows
        for (int i = 0; i < 9; i++) {
            menu.setButton(i, borderButton);
            menu.setButton((rows - 1) * 9 + i, borderButton);
        }

        // Left and right columns
        for (int i = 1; i < rows - 1; i++) {
            menu.setButton(i * 9, borderButton);
            menu.setButton(i * 9 + 8, borderButton);
        }
    }
}
