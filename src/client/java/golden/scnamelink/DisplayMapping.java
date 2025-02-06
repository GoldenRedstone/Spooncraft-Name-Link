package golden.scnamelink;

import java.util.UUID;

public class DisplayMapping {
    final String mc_name;
    final UUID mc_uuid;
    final String discord_nick;
    final String colour;

    public DisplayMapping(String mc_name, UUID mc_uuid, String discord_nick,
                          String colour) {
        this.mc_name = mc_name;
        this.mc_uuid = mc_uuid;
        this.discord_nick = discord_nick;
        this.colour = colour;
    }

    @Override
    public String toString() {
        return "DisplayMapping{" +
                "mc_name='" + mc_name + '\'' +
                ", mc_uuid=" + mc_uuid +
                ", discord_nick=" + discord_nick +
                ", colour=" + colour +
                '}';
    }
}
