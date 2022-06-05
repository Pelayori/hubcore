package os.arcadiadevs.playerservers.hubcore.utils;

import org.bukkit.ChatColor;

public class ChatUtil {

    public static String translate(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
