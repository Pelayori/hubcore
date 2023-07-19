package os.arcadiadevs.playerservers.hubcore.guis;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.samjakob.spigui.buttons.SGButton;
import com.samjakob.spigui.item.ItemBuilder;
import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.dto.ServerRecord;
import os.arcadiadevs.playerservers.hubcore.enums.ServerStatus;
import os.arcadiadevs.playerservers.hubcore.models.Server;
import os.arcadiadevs.playerservers.hubcore.utils.BungeeUtil;
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;
import os.arcadiadevs.playerservers.hubcore.utils.ServerPinger;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Handles the selector GUI.
 *
 * @author ArcadiaDevs
 */
public class SelectorGui {

  /**
   * Opens the selector GUI.
   *
   * @param player The player to open the GUI for.
   */
  public static void openGui(Player player) {
    BungeeUtil.checkIfOnline(player);
  }

}
