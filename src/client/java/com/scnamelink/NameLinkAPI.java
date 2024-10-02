package com.scnamelink;

import com.scnamelink.config.SCNameLinkConfig;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import me.shedaniel.autoconfig.AutoConfig;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * NameLinkAPI is a utility class responsible for fetching and caching mappings between
 * Minecraft names/UUIDs and Discord nicknames from an external API.
 * <p>
 * The class handles retrieving the mappings either from a remote server or, if that fails, from a
 * cached
 * local file. It also manages the conversion of JSON data to Java objects and provides status
 * reporting
 * on the success or failure of these operations.
 */
public class NameLinkAPI {
    // The mod ID as used in logging
    public static final String MOD_ID = "SC-Name-Link";
    // Logger for outputting information to the console and log files
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // The file path to cache the JSON data locally in case of API failure.
    static final String CACHE_PATH = "config/spooncraft-name-link-cache.json";
    static final int MAX_RETRIES = 5;
    static final int BASE_DELAY_MS = 500;

    static final SCNameLinkConfig CONFIG =
            AutoConfig.getConfigHolder(SCNameLinkConfig.class).getConfig();

    // Tracks the current status of the API fetch and caching process
    static String status = "Working";

    /**
     * Fetches the display name mappings from the external API. If an error occurs during the
     * process,
     * it attempts to load the mappings from the cached local file.
     * <p>
     * The status is updated to reflect whether the data was successfully fetched from the API
     * ({@code "Success"}), retrieved from the cache ({@code "Fallback"}), or if the process failed
     * entirely
     * ({@code "Failure"}).
     *
     * @return A list of {@code DisplayMapping} objects, either from the API or the cached file,
     * or an empty
     * list in case of failure.
     */
    public static @Nullable List<DisplayMapping> getMappings() {
        status = "Working";

        try {
            // Load JSON from URL
            String jsonData = loadJsonFromUrl();
            LOGGER.info("Load data from url");
            // Convert String JSON into Java objects
            List<DisplayMapping> displayMappings = loadJsonToObjects(jsonData);
            LOGGER.info("Converted sting to Object");
            // Save to file as a backup
            saveJsonToFile(jsonData);
            LOGGER.info("Saved the data to {}", NameLinkAPI.CACHE_PATH);

            status = "Success";
            return displayMappings;

        } catch (RuntimeException | IOException | URISyntaxException e) {
            // If an exception occurs
            try {
                // Try loading it from the cached file
                List<DisplayMapping> displayMappings = loadJsonFromFile();
                status = "Fallback";
                LOGGER.warn("Could not reach the server. Using cached fallback.");
                return displayMappings;
            } catch (RuntimeException | IOException ex) {
                status = "Failure";
                LOGGER.warn("Could not reach the server or find a fallback.");
                return new ArrayList<DisplayMapping>();
            }
        }
    }

    /**
     * Loads the JSON data from the remote API URL and returns it as a string.
     *
     * @return The JSON response from the API as a string.
     * @throws IOException        If an I/O error occurs during the connection or reading process.
     * @throws URISyntaxException If the API URL is incorrectly formatted.
     */
    private static String loadJsonFromUrl() throws IOException, URISyntaxException {
        StringBuilder result = new StringBuilder(70000);
        URI uri = new URI(CONFIG.apiLink);
        URL url = uri.toURL();
        int retries = 0;

        // Retry logic with exponential backoff
        while (retries < MAX_RETRIES) {
            HttpURLConnection conn = null;
            try {
                // Attempt to open the connection and read the data
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                try (BufferedReader reader =
                             new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                }

                // Return the result if successful
                return result.toString();

            } catch (IOException e) {
                retries++;
                if (retries == MAX_RETRIES) {
                    throw new IOException("Failed to load JSON from URL after " + retries + " " +
                                                  "retries", e);
                }

                // Calculate the exponential backoff delay
                long backoffDelay = BASE_DELAY_MS * (1L << retries); // Exponential backoff:
                // baseDelay * 2^retries

                LOGGER.warn("Could not load JSON from URL. Retrying in {}ms (attempt {} of " + MAX_RETRIES + ")", backoffDelay, retries);

                // Sleep before the next retry
                try {
                    Thread.sleep(backoffDelay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new IOException("Retry was interrupted", ie);
                }
            } finally {
                if (conn != null) {
                    conn.disconnect(); // Always disconnect to free resources
                }
            }
        }

        return result.toString(); // This should never be reached due to retries limit.
    }

    /**
     * Saves the provided JSON data to a local file as a backup. This allows the program
     * to load the data from the cache if the API is unavailable in the future.
     *
     * @param jsonData The JSON data as a string.
     * @throws IOException If an I/O error occurs while writing to the file.
     */
    private static void saveJsonToFile(String jsonData) throws IOException {
        try (FileWriter fileWriter = new FileWriter(NameLinkAPI.CACHE_PATH)) {
            fileWriter.write(jsonData);
        }
    }

    /**
     * Converts a JSON string into a list of {@code DisplayMapping} objects using Gson.
     *
     * @param jsonData The JSON data as a string.
     * @return A list of {@code DisplayMapping} objects parsed from the JSON string.
     * @throws IOException If an error occurs while parsing the JSON data.
     */
    private static List<DisplayMapping> loadJsonToObjects(String jsonData) throws IOException {
        Gson gson = new Gson();
        return gson.fromJson(jsonData, getDisplayMappingListType());
    }

    /**
     * Loads the JSON data from the local cache file and converts it into a list of
     * {@code DisplayMapping}
     * objects.<br>
     * This method is used as a fallback if the API request fails.
     *
     * @return A list of {@code DisplayMapping} objects loaded from the cache file.
     * @throws IOException If an error occurs while reading the cache file.
     */
    private static List<DisplayMapping> loadJsonFromFile() throws IOException {
        Gson gson = new Gson();
        try (Reader reader = Files.newBufferedReader(Paths.get(NameLinkAPI.CACHE_PATH))) {
            return gson.fromJson(reader, getDisplayMappingListType());
        }
    }

    /**
     * Helper method to get the Type of {@code List<DisplayMapping>} for Gson parsing.
     * This method returns the type information required by Gson to correctly deserialize
     * a list of {@code DisplayMapping} objects.
     *
     * @return The Type representing {@code List<DisplayMapping>}.
     */
    private static Type getDisplayMappingListType() {
        return new TypeToken<List<DisplayMapping>>() {}.getType();
    }

    /**
     * Returns the current status of the API fetching and caching process.
     * The possible statuses are:<br>
     * - {@code "Working"}: The process is ongoing.<br>
     * - {@code "Success":} Data was successfully fetched from the API.<br>
     * - {@code "Fallback"}: Data was loaded from the cache.<br>
     * - {@code "Failure"}: Both API and cache loading failed.<br>
     *
     * @return The current status as a string.
     */
    public static String getStatus() {
        return status;
    }

    /**
     * Marks the mod as disabled.<br>
     * This prevents messages from showing up when joining a server.
     */
    public static void disableMod() {
        status = "Disabled";
    }
}
