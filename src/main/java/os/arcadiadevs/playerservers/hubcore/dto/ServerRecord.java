package os.arcadiadevs.playerservers.hubcore.dto;

import os.arcadiadevs.playerservers.hubcore.utils.formatter.Formattable;

import java.util.HashMap;

public record ServerRecord(String name, String motd, boolean online, String ip, Integer port, Integer players, Integer maxPlayers) implements Formattable {

	@Override
	public HashMap<String, String> getPlaceHolders() {
		return new HashMap<>() {{
			put("%server%", name);
			put("%status%", online ? "&aOnline" : "&cOffline");
			put("%players%", players.toString());
			put("%maxplayers%", maxPlayers.toString());
			put("%port%", port.toString());
			put("%motd%", motd);
			put("%node%", ip);
			put("%owner%", name);
			put("%ip%", ip);
		}};
	}
}
