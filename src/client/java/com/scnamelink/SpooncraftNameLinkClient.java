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

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // List of mappings for replacement and optional color changes
    static List<DisplayMapping> mappings = new ArrayList<>();

	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
        mappings.add(new DisplayMapping("GoldenRedstone", "golðen", "F1B50F"));
        mappings.add(new DisplayMapping("WiseGuyIT", "🍵SauronPantera200🍵", null));
        mappings.add(new DisplayMapping("SarahPantera100", "✨✨SarahPantera100✨✨", "FF168B"));
        mappings.add(new DisplayMapping("Duckyz", "Ducky", "B74AFF"));
        mappings.add(new DisplayMapping("StormiStik", "Warlock Stormi💜 [v5.0]", "B74AFF"));
        mappings.add(new DisplayMapping("Moon2Mars30", "Fort", "D4006E"));

		LOGGER.info("{} initialized!", MOD_ID);
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

    public static Text getStyledName(Text displayName, UUID uuid, Text name) {
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

        MutableText mt_name = Text.literal(displayString);
        mt_name = mt_name.setStyle(style);

        return mt_name;
    }

    public static Text getAppliedName(Text displayName, UUID uuid, Text name) {
        var nameWrapper = new Object(){Text wrappedName = displayName;};
        nameWrapper.wrappedName = SpooncraftNameLinkClient.getStyledName(displayName, uuid, name);
        return nameWrapper.wrappedName;
    }

    public static Text naivelyStyleText(Text message) {
        List<StringVisitable> visitableList = new ArrayList<>();
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
                        replacedStyle = replacedStyle.withColor(Integer.parseInt(mapping.colorHex.get(), 16));
                    }
                }
            }

            MutableText newText = (MutableText) Text.of(replacedText);
            newText.setStyle(replacedStyle);
            outputMessage.append(newText);

            return Optional.empty();  // Continue visiting
        }, Style.EMPTY);  // Assuming Style.EMPTY as the starting style, can change based on context

        // Return a new concatenated StringVisitable containing the replaced text
        return outputMessage;
    }

}