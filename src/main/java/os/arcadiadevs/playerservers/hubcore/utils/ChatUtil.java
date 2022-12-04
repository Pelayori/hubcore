package os.arcadiadevs.playerservers.hubcore.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtil {

  public static String translate(String s) {
    return ChatColor.translateAlternateColorCodes('&', s);
  }

  public static void sendMessage(CommandSender sender, String message) {
    sender.sendMessage(translate(message));
  }

  public static void sendMessage(CommandSender sender, String... message) {
    for (String s : message) {
      sender.sendMessage(translate(s));
    }
  }

}
