package com.scnamelink;

import net.fabricmc.api.ClientModInitializer;

import net.minecraft.text.MutableText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class SpooncraftNameLinkClient implements ClientModInitializer {
    public static final String MOD_ID = "sc-name-link";

   // Logger for outputting information to the console and log files
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // List of mappings for replacement and optional color changes
    static List<DisplayMapping> mappings = new ArrayList<>();

    /**
     * Replaces specific display names based on the mappings and applies the associated styles.
     *
     * @param displayName the original display name
     * @param uuid        UUID of the player (not used yet)
     * @param name        the original name (not used yet)
     * @return a new Text object with replaced display names and updated styles
     */
    public static Text getStyledName(Text displayName, UUID uuid, Text name) {
        // Get the current style and content of the display name
        Style style = displayName.getStyle();
        String displayString = displayName.getString();

        // Process each mapping
        for (DisplayMapping mapping : mappings) {
            if (displayString.contains(mapping.original)) {
                // Replace the string
                displayString = displayString.replace(mapping.original, mapping.replacement);

                // Apply color if specified
                if (mapping.colorHex.isPresent()) {
                    style = style.withColor(Integer.parseInt(mapping.colorHex.get(), 16));
                }
            }
        }

        // Create new MutableText with the display string and style
        MutableText mt_name = Text.literal(displayString);
        mt_name = mt_name.setStyle(style);

        return mt_name;
    }

    /**
     * Naively applies styling and replacements to a given message.
     * Applies every name mapping possible.
     *
     * @param message the original message
     * @return a new Text object with replaced text and styles applied
     */
    public static Text naivelyStyleText(Text message) {
        MutableText outputMessage = Text.empty();

        // Visit the original StringVisitable and replace occurrences of the target string
        message.visit((style, text) -> {
            Style replacedStyle = style;
            String replacedText = text;

            // Process each mapping
            for (DisplayMapping mapping : mappings) {
                if (replacedText.contains(mapping.original)) {
                    // Replace the string
                    replacedText = replacedText.replace(mapping.original, mapping.replacement);

                    // Apply color if specified
                    if (mapping.colorHex.isPresent()) {
                        replacedStyle =
                                replacedStyle.withColor(Integer.parseInt(mapping.colorHex.get(),
                                                                         16));
                    }
                }
            }

            // Create new MutableText with the display string and style
            MutableText newText = (MutableText) Text.of(replacedText);
            newText.setStyle(replacedStyle);
            outputMessage.append(newText);

            return Optional.empty();  // Continue visiting
        }, Style.EMPTY);

        // Return a new concatenated StringVisitable containing the replaced text
        return outputMessage;
    }

    @Override
    public void onInitializeClient() {
        // This entrypoint is suitable for setting up client-specific logic, such as rendering.
        mappings.add(new DisplayMapping("GoldenRedstone", "golðen", "F1B50F"));
        mappings.add(new DisplayMapping("WiseGuyIT", "🍵SauronPantera200🍵", null));
        mappings.add(new DisplayMapping("SarahPantera100", "✨✨SarahPantera100✨✨", "FF168B"));
        mappings.add(new DisplayMapping("Duckyz", "Ducky", "B74AFF"));
        mappings.add(new DisplayMapping("StormiStik", "Warlock Stormi💜 [v5.0]", "B74AFF"));
        mappings.add(new DisplayMapping("Moon2Mars30", "Fort", "D4006E"));

        LOGGER.info("{} initialised with {} mappings", MOD_ID, mappings.size());
    }

    // Class to hold display name replacements and style information
    static class DisplayMapping {
        String original;
        String replacement;
        Optional<String> colorHex;  // Use Optional to allow entries without color changes

        DisplayMapping(String original, String replacement, String colorHex) {
            this.original = original;
            this.replacement = replacement;
            this.colorHex = Optional.ofNullable(colorHex);
        }
    }

}