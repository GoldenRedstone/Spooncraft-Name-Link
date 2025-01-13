package com.scnamelink.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

/**
 * Stores configuration options for the Spooncraft Name Link mod.
 */
@Config (name = "scnamelink")
public class SCNameLinkConfig implements ConfigData {
    // Whether to enable the mod. Game should be restarted after enabling.
    @ConfigEntry.Gui.Tooltip
    public boolean enableMod = true;
    // The uri of the data api. Leave blank if unsure. Game should be restarted after changing.
    @ConfigEntry.Gui.Tooltip(count = 2)
    public String apiLink = "";

    // Whether to replace names in the tablist using discord nicknames.
    public boolean replacetablist = false;
    // Whether to colour names in the using the colour from discord.
    public boolean colourtablist = true;

    // Whether to replace names in player nametags using discord nicknames.
    public boolean replacenametag = false;
    // Whether to colour names in player nametags using the colour from discord.
    public boolean colournametag = true;

    // Whether to replace names in the chat using discord nicknames.
    public boolean replacechat = false;
    // Whether to colour names in the chat using the colour from discord.
    public boolean colourchat = true;
}