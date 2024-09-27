package com.scnamelink;

import com.scnamelink.config.SCNameLinkConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Client-side mod initializer for the Minecraft mod "Spooncraft Name Link".
 */
public class SpooncraftNameLinkClient implements ClientModInitializer {

    // The mod ID as used in logging
    public static final String MOD_ID = "SC-Name-Link";

    // Logger for outputting information to the console and log files
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // List of mappings for replacement and optional color changes
    static List<DisplayMapping> mappings = new ArrayList<>();

    /**
     * Retrieves and applies the correct name mapping (if any) for a given Minecraft username or
     * UUID.
     * This method checks the mappings list to see if the provided displayName or uuid has a
     * corresponding mapping, and if found, applies it by optionally altering the name and color.
     *
     * @param displayName   The original in-game name to be displayed
     * @param uuid          The UUID of the Minecraft player
     * @param name          The in-game name as a Text object
     * @param replaceName   Whether to replace the name with the name defined in the mapping
     * @param replaceColour Whether to replace the colour with the colour defined in the mapping
     * @return A Text object containing the potentially modified name with appropriate styling
     */
    public static Text getStyledName(Text displayName, UUID uuid, Text name, boolean replaceName,
                                     boolean replaceColour) {
        DisplayMapping correctMapping = null;

        // Iterate over the mappings to find the correct match based on UUID or Minecraft name
        for (DisplayMapping mapping : mappings) {
            // If the UUID matches or the name matches, select the mapping and break
            if (mapping.mc_uuid == uuid || Objects.equals(mapping.mc_name, name.getString())) {
                correctMapping = mapping;
                break;
            }
        }

        // If a matching mapping is found, apply it; otherwise, return the original displayName
        if (correctMapping != null) {     // If a mapping could be found.
            return applyMapping(displayName, correctMapping, replaceName, replaceColour);
        }
        // If the mapping couldn't be found, simply return what we got.
        return displayName;
    }

    /**
     * Retrieves and applies the correct name mapping (if any) for a given Minecraft username or
     * UUID.
     * This method checks the mappings list to see if the provided displayName or uuid has a
     * corresponding mapping, and if found, applies it by altering the name and color.
     *
     * @param displayName The original in-game name to be displayed
     * @param uuid        The UUID of the Minecraft player
     * @param name        The in-game name as a Text object
     * @return A Text object containing the potentially modified name with appropriate styling
     */
    public static Text getStyledName(Text displayName, UUID uuid, Text name) {
        return getStyledName(displayName, uuid, name, true, true);
    }

    /**
     * Applies the mapping to a given message. It optionally replaces the Minecraft name with
     * the Discord nickname and applies the colour styling.
     *
     * @param message       The original in-game message or name
     * @param mapping       The DisplayMapping object containing the name and color mapping details
     * @param replaceName   Whether to replace the name with the name defined in the mapping
     * @param replaceColour Whether to replace the colour with the colour defined in the mapping
     * @return A new MutableText object with the mapping applied (replacements and color changes)
     */
    public static MutableText applyMapping(Text message, DisplayMapping mapping,
                                           boolean replaceName, boolean replaceColour) {
        MutableText outputMessage = Text.empty();

        if (message == null) {
            return outputMessage;
        }

        message.visit((style, text) -> {
            Style replacedStyle = style;
            String replacedText = text;

            // Apply the mapping
            if (replacedText.contains(mapping.mc_name)) {
                // Replace the string
                if (mapping.discord_nick != null && replaceName) {
                    replacedText = replacedText.replace(mapping.mc_name, mapping.discord_nick);
                }
                // Apply color if specified
                if (mapping.colour != null && replaceColour) {
                    replacedStyle = replacedStyle.withColor(Integer.parseInt(mapping.colour, 16));
                }
            }

            // Create new MutableText with the display string and style
            MutableText newText = (MutableText) Text.of(replacedText);
            newText.setStyle(replacedStyle);
            outputMessage.append(newText);

            return Optional.empty();  // Continue visiting
        }, Style.EMPTY);

        return outputMessage;
    }

    /**
     * Applies the mapping to a given message. It both replaces the Minecraft name with
     * the Discord nickname and applies color styling if defined in the mapping.
     *
     * @param message The original in-game message or name
     * @param mapping The DisplayMapping object containing the name and color mapping details
     * @return A new MutableText object with the mapping applied (replacements and color changes)
     */
    public static MutableText applyMapping(Text message, DisplayMapping mapping) {
        return applyMapping(message, mapping, true, true);
    }

    /**
     * Naively applies styling and replacements to a given message.
     * This method applies every name mapping in the mappings list to the message, regardless of
     * context.
     *
     * @param message       The original message to which mappings will be applied
     * @param replaceName   Whether to replace the name with the name defined in the mapping
     * @param replaceColour Whether to replace the colour with the colour defined in the mapping
     * @return A new Text object with all applicable mappings and styles applied
     */
    public static Text naivelyStyleText(Text message, boolean replaceName, boolean replaceColour) {
        MutableText outputMessage = (MutableText) message;

        // Apply each mapping sequentially to the message
        for (DisplayMapping mapping : mappings) {
            outputMessage = applyMapping(outputMessage, mapping, replaceName, replaceColour);
        }

        // Return the final styled message
        return outputMessage;
    }

    /**
     * Naively applies styling and replacements to a given message.
     * This method applies every name mapping in the mappings list to the message, regardless of
     * context.
     *
     * @param message The original message to which mappings will be applied
     * @return A new Text object with all applicable mappings and styles applied
     */
    public static Text naivelyStyleText(Text message) {
        return naivelyStyleText(message, true, true);
    }


    @Override
    public void onInitializeClient() {
        AutoConfig.register(SCNameLinkConfig.class, Toml4jConfigSerializer::new);
        SCNameLinkConfig config = AutoConfig.getConfigHolder(SCNameLinkConfig.class).getConfig();
        if (!config.enableMod) {
            NameLinkAPI.disableMod();
            LOGGER.warn("Mod is disabled!");
            return;
        }
        mappings = NameLinkAPI.getMappings();
        if (mappings != null)
            LOGGER.info("{} initialised with {} mappings", MOD_ID, mappings.size());
        else LOGGER.error("{} initialised with NO mappings found", MOD_ID);
    }
}
