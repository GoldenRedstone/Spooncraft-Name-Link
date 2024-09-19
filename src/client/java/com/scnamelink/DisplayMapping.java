package com.scnamelink;

import java.util.UUID;

public class DisplayMapping {
    final String mc_name;
    final UUID mc_uuid;
    final long discord_id;
    final String discord_nick;
    final String colour;

    public DisplayMapping(String mc_name, UUID mc_uuid, long discord_id, String discord_nick,
                          String colour) {
        this.mc_name = mc_name;
        this.mc_uuid = mc_uuid;
        this.discord_id = discord_id;
        this.discord_nick = discord_nick;
        this.colour = colour;
    }

    @Override
    public String toString() {
        return "DisplayMapping{" +
                "mc_name='" + mc_name + '\'' +
                ", mc_uuid=" + mc_uuid +
                ", discord_id=" + discord_id +
                ", discord_nick=" + discord_nick +
                ", colour=" + colour +
                '}';
    }
}
