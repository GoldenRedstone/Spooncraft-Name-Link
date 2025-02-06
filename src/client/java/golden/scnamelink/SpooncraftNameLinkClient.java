package golden.scnamelink;

import golden.scnamelink.config.SCNameLinkConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Client-side mod initializer for the Minecraft mod "Spooncraft Name Link".
 */
public class SpooncraftNameLinkClient implements ClientModInitializer {

    // The mod ID as used in logging
    static final String MOD_ID = "scnamelink";

    // Logger for outputting information to the console and log files
    static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    static SCNameLinkConfig config;

    // List of mappings for replacement and optional color changes
    private static List<DisplayMapping> mappings = new ArrayList<>();

    /**
     * Applies the mapping to a given message. It optionally replaces the Minecraft name with
     * the Discord nickname and applies the colour styling.
     *
     * @param message       The original in-game message or name
     * @param mapping       The {@code DisplayMapping} object containing the name and color mapping
     *                      details
     * @param replaceName   Whether to replace the name with the name defined in the mapping
     * @param replaceColour Whether to replace the colour with the colour defined in the mapping
     * @return A new MutableText object with the mapping applied (replacements and color changes)
     */
    static MutableText applyMapping(Text message, DisplayMapping mapping,
                                           boolean replaceName, boolean replaceColour) {
        if (message == null || message.getString().isEmpty() || mapping == null) {
            return Text.empty();
        }

        MutableText outputMessage = Text.empty();
        message.visit((style, text) -> {
            String replacedText = text;
            Style replacedStyle = style;

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
     * Retrieves and applies the correct name mapping (if any) for a given Minecraft username or
     * UUID.
     * Checks the mappings list to see if the provided displayName or uuid has a corresponding
     * mapping, and if found, applies it by optionally altering the name and color.
     *
     * @param displayName   The original in-game name to be displayed
     * @param uuid          The UUID of the Minecraft player
     * @param name          The in-game name as a Text object
     * @param replaceName   Whether to replace the name with the name defined in the mapping
     * @param replaceColour Whether to replace the colour with the colour defined in the mapping
     * @return A Text object containing the potentially modified name with appropriate styling
     */
    public static Text getStyledName(Text displayName, UUID uuid, String name, boolean replaceName,
                                     boolean replaceColour) {
        // Iterate over the mappings to find the correct match based on UUID or Minecraft name
        for (DisplayMapping mapping : mappings) {
            // If the UUID matches or the name matches, apply the mapping and return it
            if (Objects.equals(mapping.mc_uuid, uuid) || Objects.equals(mapping.mc_name, name)) {
                return applyMapping(displayName, mapping, replaceName, replaceColour);
            }
        }
        return displayName;
    }

    /**
     * Retrieves and applies the correct name mapping (if any) for a given Minecraft username.
     *
     * @param displayName   The original in-game name to be displayed
     * @param name          The in-game name as a Text object
     * @param replaceName   Whether to replace the name with the name defined in the mapping
     * @param replaceColour Whether to replace the colour with the colour defined in the mapping
     * @return A Text object containing the potentially modified name with appropriate styling
     * @see #getStyledName(Text, UUID, String, boolean, boolean)
     */
    public static Text getStyledName(Text displayName, String name, boolean replaceName,
                                     boolean replaceColour) {
        return getStyledName(displayName, UUID.fromString("00000000-0000-0000-0000-000000000000"), name, replaceName, replaceColour);
    }

    /**
     * Styles the chat message by replacing the name and colour if specified.
     * Only styles the name if the text has a {@code HoverEvent} component.
     *
     * @param message       The original in-game message or name
     * @param replaceName   Whether to replace the name with the name defined in the mapping
     * @param replaceColour Whether to replace the colour with the colour defined in the mapping
     * @return A new MutableText object with the mapping applied (replacements and color changes)
     */
    public static Text getStyledChat(Text message, boolean replaceName, boolean replaceColour) {
        if (message == null || message.getString().isEmpty()) {
            return Text.empty();
        }

        MutableText outputMessage = Text.empty();
        message.visit((style, text) -> {
            MutableText newText = Text.literal(text).setStyle(style);

            HoverEvent event = style.getHoverEvent();
            if (event != null) {
                HoverEvent.EntityContent value = event.getValue(HoverEvent.Action.SHOW_ENTITY);
                if (value != null && value.name.isPresent()) {
                    newText = (MutableText) getStyledName(newText, value.uuid, String.valueOf(value.name), replaceName, replaceColour);
                    newText.setStyle(newText.getStyle().withHoverEvent(event));
                }
            }

            outputMessage.append(newText);
            return Optional.empty();
        }, Style.EMPTY);

        return outputMessage;
    }

    /**
     * Retrieves the mappings from the specified source URL or the default URL if none is provided.
     * If the mod is disabled in the configuration, it disables the mod and logs a warning.
     *
     * @param source The URL from which to fetch the mappings. If null or empty, the default URL is
     *               used.
     * @return The number of mappings retrieved.
     */
    public static int getMappings(String source) {
        String s = (source == null || source.isEmpty()) ? "https://gwaff.uqcloud.net/api/spooncraft" : source;
        mappings = NameLinkAPI.getMappings(s);
        return mappings.size();
    }

    /**
     * Retrieves a Text object detailing the status of the mod.
     *
     * @return A Text object containing the status of the mod
     */
    public static Text getStatusString() {
        String status = NameLinkAPI.getStatus();
        return switch (status) {
            case "Success" -> Text.translatable("text.scnamelink.status.success").formatted(Formatting.WHITE);
            case "Working" -> Text.translatable("text.scnamelink.status.working").formatted(Formatting.YELLOW);
            case "Fallback" -> Text.translatable("text.scnamelink.status.fallback").formatted(Formatting.RED);
            case "Failure" -> Text.translatable("text.scnamelink.status.failure").formatted(Formatting.RED, Formatting.BOLD);
            default -> Text.of(NameLinkAPI.getStatus());
        };
    }

    @Override
    public void onInitializeClient() {
        AutoConfig.register(SCNameLinkConfig.class, Toml4jConfigSerializer::new);
        config = AutoConfig.getConfigHolder(SCNameLinkConfig.class).getConfig();

        final int count = getMappings(config.apiLink);
        if (count > 0) {
            LOGGER.info("{} initialised with {} mappings", MOD_ID, mappings.size());
        } else {
            LOGGER.warn("{} initialised with NO mappings found", MOD_ID);
        }

        if (!config.enableMod) {
            NameLinkAPI.disableMod();
            LOGGER.warn("Mod disabled.");
            return;
        }
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> CommandManager.register(dispatcher));
    }
}
