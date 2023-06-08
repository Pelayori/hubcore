package os.arcadiadevs.playerservers.hubcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.PsHubCore;
import os.arcadiadevs.playerservers.hubcore.guis.PlayerMenuGui;
import os.arcadiadevs.playerservers.hubcore.guis.SelectorGui;
import os.arcadiadevs.playerservers.hubcore.statics.Permissions;
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;

/**
 * A class that handles all the commands for the hubcore plugin.
 *
 * @author ArcadiaDevs
 */
@SuppressWarnings("NullableProblems")
public class CommandManager implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String s,
                           String[] strings) {

    if (!(commandSender instanceof Player player)) {
      ChatUtil.sendMessage(commandSender,
          "&9Error> &cYou must be a player to execute this command.");
      return true;
    }

    final var enableGuiPermissions = PsHubCore.getInstance().getConfig().getBoolean("gui.enable-permissions");
    final var commandName = command.getName();

    if (!enableGuiPermissions) {
      if (isPlayerMenu(commandName)) {
        PlayerMenuGui.openGui(player);
        return true;
      }
      if (isSelector(commandName)) {
        SelectorGui.openGui(player);
        return true;
      }
    }

    if (isPlayerMenu(commandName) && player.hasPermission(Permissions.PLAYER_MENU) ) {
      PlayerMenuGui.openGui(player);
      return true;
    }

    if (isSelector(commandName) && player.hasPermission(Permissions.SERVER_SELECTOR)) {
      SelectorGui.openGui(player);
      return true;
    }

    ChatUtil.sendMessage(player, PsHubCore.getInstance().getConfig().getString("messages.no-permission"));

    return true;
  }

  private boolean isSelector(String commandName) {
    return commandName.equalsIgnoreCase("servers")
            || commandName.equalsIgnoreCase("menu")
            || commandName.equalsIgnoreCase("opengui");
  }

  private boolean isPlayerMenu(String commandName) {
    return commandName.equalsIgnoreCase("playermenu")
            || commandName.equalsIgnoreCase("pmenu")
            || commandName.equalsIgnoreCase("playergui")
            || commandName.equalsIgnoreCase("pgui");
  }
}
