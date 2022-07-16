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
        if (commandSender instanceof Player) {
            if (!PSHubCore.getInstance().getConfig().getBoolean("gui.enabled")) {
                commandSender.sendMessage(ChatUtil.translate("&9PlayerServers> &7Oops, gui isn't enabled in config file!"));
                return true;
            }
            if (PSHubCore.getInstance().getConfig().getBoolean("gui.enabled")) {
                if (command.getName().equalsIgnoreCase("servers") || command.getName().equalsIgnoreCase("menu") || command.getName().equalsIgnoreCase("opengui")) {
                    Player player = (Player) commandSender;
                    GUIUtils.openSelector(player);
                }
            }
        }
        return true;
    }
}
