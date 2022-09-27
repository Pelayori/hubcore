package os.arcadiadevs.playerservers.hubcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.PSHubCore;
import os.arcadiadevs.playerservers.hubcore.utils.ChatUtil;
import os.arcadiadevs.playerservers.hubcore.utils.GUIUtils;

public class CommandManager implements CommandExecutor {

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (!(commandSender instanceof Player)) {
            return true;
        }

        final Player player =  (Player) commandSender;

        if (!PSHubCore.getInstance().getConfig().getBoolean("gui.selector.item.enabled")) {
            commandSender.sendMessage(ChatUtil.translate("&9PlayerServers> &7Oops, selector isn't enabled in config file!"));
            return true;
        }

        if (!PSHubCore.getInstance().getConfig().getBoolean("gui.player-menu.item.enabled")) {
            commandSender.sendMessage(ChatUtil.translate("&9PlayerServers> &7Oops, player-menu isn't enabled in config file!"));
            return true;
        }

        if (PSHubCore.getInstance().getConfig().getBoolean("gui.selector.item.enabled")) {
            switch (command.getName()) {
                case ("servers"):
                    GUIUtils.openSelector(player);
                case ("menu"):
                    GUIUtils.openSelector(player);
                case ("opengui"):
                    GUIUtils.openSelector(player);
            }
        }
        if (PSHubCore.getInstance().getConfig().getBoolean("gui.player-menu.item.enabled")) {
            switch (command.getName()) {
                case ("playermenu"):
                    GUIUtils.openMenu(player);
                case ("pmenu"):
                    GUIUtils.openMenu(player);
            }
        }

        return true;
    }
}
