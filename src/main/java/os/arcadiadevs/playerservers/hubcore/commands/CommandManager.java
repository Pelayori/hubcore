package os.arcadiadevs.playerservers.hubcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.guis.AdminGui;
import os.arcadiadevs.playerservers.hubcore.guis.SelectorGui;
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;
import os.arcadiadevs.playerservers.hubcore.guis.PlayerMenuGui;

/**
 * A class that handles all the commands.
 *
 * @author ArcadiaDevs
 */
public class CommandManager implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String s,
                           String[] strings) {
    if (!(commandSender instanceof Player player)) {
      ChatUtil.sendMessage(commandSender,
          "&9Error> &cYou must be a player to execute this command.");
      return true;
    }

    if (!PsHubCore.getInstance().getConfig().getBoolean("gui.selector.enabled")) {
      ChatUtil.sendMessage(player, "&9PlayerServers> &7Oops, this feature is disabled.");
      return true;
    }

    if (command.getName().equalsIgnoreCase("servers") ||
        command.getName().equalsIgnoreCase("opengui")) {
      SelectorGui.openGui(player);
    }

    if (command.getName().equalsIgnoreCase("playermenu") ||
            command.getName().equalsIgnoreCase("pmenu") ||
            command.getName().equalsIgnoreCase("playergui")) {
      PlayerMenuGui.openGui(player);
    }

    if (command.getName().equalsIgnoreCase("adminmenu") ||
            command.getName().equalsIgnoreCase("admingui")) {
      AdminGui.openGui(player);
    }

    return true;
  }
}
