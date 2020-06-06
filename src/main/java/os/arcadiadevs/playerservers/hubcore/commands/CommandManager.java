package os.arcadiadevs.playerservers.hubcore.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import os.arcadiadevs.playerservers.hubcore.utils.GUIUtils;

public class CommandManager implements CommandExecutor {

    @SuppressWarnings("NullableProblems")
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender instanceof Player) {
            if (command.getName().equalsIgnoreCase("servers") || command.getName().equalsIgnoreCase("menu") || command.getName().equalsIgnoreCase("opengui")) {
                Player player = (Player) commandSender;
                GUIUtils gu = new GUIUtils();
                gu.openSelector(player);
            }
        }
        return true;
    }

}
