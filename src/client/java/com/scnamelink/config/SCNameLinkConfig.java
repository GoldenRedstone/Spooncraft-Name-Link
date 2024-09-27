package com.scnamelink.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

/**
 * Stores configuration options for the Spooncraft Name Link mod.
 */
@Config (name = "scnamelink")
public class SCNameLinkConfig implements ConfigData {
    // Whether to enable the mod. Game must be restarted after enabling.
    public boolean enableMod = true;
    // The uri of the data api. Game must be restarted after changing.
    public String apiLink = "https://gwaff.uqcloud.net/api/spooncraft";

    // Whether to replace names in the tablist using discord nicknames.
    public boolean replacetablist = true;
    // Whether to colour names in the using the colour from discord.
    public boolean colourtablist = true;

    // Whether to replace names in player nametags using discord nicknames.
    public boolean replacenametag = true;
    // Whether to colour names in player nametags using the colour from discord.
    public boolean colournametag = true;

    // Whether to replace names in the chat using discord nicknames.
    public boolean replacechat = true;
    // Whether to colour names in the chat using the colour from discord.
    public boolean colourchat = false;
}