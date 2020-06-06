package os.arcadiadevs.playerservers.hubcore.utils;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import os.arcadiadevs.playerservers.hubcore.database.DataBase;
import os.arcadiadevs.playerservers.hubcore.database.structures.DBInfoStructure;
import os.arcadiadevs.playerservers.hubcore.database.structures.PingInfoStructure;

import java.util.ArrayList;

import static os.arcadiadevs.playerservers.hubcore.PSHubCore.PSH;
import static os.arcadiadevs.playerservers.hubcore.utils.ColorUtils.translate;

public class GUIUtils {

    public void openSelector(Player player) {
        DataBase db = new DataBase();
        PingUtil pu = new PingUtil();

        Inventory gui = Bukkit.createInventory(player, 9*6, ChatColor.GREEN + "Server Selector");

        Bukkit.getScheduler().runTaskAsynchronously(PSH, () -> {
            for (DBInfoStructure is : db.getServersInfo()) {
                ItemStack istack;
                if (pu.isOnline("127.0.0.1", is.getPort())) {

                    PingInfoStructure pus = pu.getData(Integer.parseInt(is.getPort()));

                    istack = new ItemStack(XMaterial.EMERALD_BLOCK.parseMaterial());
                    ItemMeta ir = istack.getItemMeta();
                    //noinspection ConstantConditions
                    ir.setDisplayName(translate("&a" + is.getPlayerName() + "'s server"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(translate("&cPort: &7" + is.getPort()));
                    lore.add(translate("&cUUID: &7" + is.getServerID().split("-")[0]));
                    lore.add(translate(String.format("&cOnline: &7%d/%d", pus.getOnline(), pus.getMax())));
                    lore.add(translate("&cMOTD: &7" + pus.getMOTD()));
                    ir.setLore(lore);
                    istack.setItemMeta(ir);

                    gui.addItem(istack);
                }
            }

            for (DBInfoStructure is : db.getServersInfo()) {
                if (!pu.isOnline("127.0.0.1", is.getPort())) {
                    ItemStack istack = new ItemStack(XMaterial.REDSTONE_BLOCK.parseMaterial());
                    ItemMeta ir = istack.getItemMeta();
                    //noinspection ConstantConditions
                    ir.setDisplayName(translate("&a" + is.getPlayerName() + "'s server"));
                    ArrayList<String> lore = new ArrayList<>();
                    lore.add(translate("&cPort: &7" + is.getPort()));
                    lore.add(translate("&cUUID: &7" + is.getServerID().split("-")[0]));
                    ir.setLore(lore);
                    istack.setItemMeta(ir);

                    gui.addItem(istack);
                }
            }

            Bukkit.getScheduler().runTask(PSH, () -> player.openInventory(gui));
        });
    }

}
