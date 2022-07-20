package os.arcadiadevs.playerservers.hubcore.objects;

public class PingInfoStructure {

    private final int online;
    private final int max;
    private final String MOTD;

    public PingInfoStructure(int online, int max, String MOTD) {
        this.online = online;
        this.max = max;
        this.MOTD = MOTD;
    }

    public int getMax() {
        return max;
    }

    public int getOnline() {
        return online;
    }

    public String getMOTD() {
        return MOTD;
    }
}