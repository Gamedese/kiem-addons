package com.example.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

public class UpdateChecker {
    public static final String MODRINTH_SLUG = "kiem-addons";
    public static final String MODRINTH_URL = "https://modrinth.com/mod/" + MODRINTH_SLUG;
    public static final String CURSEFORGE_URL = "https://www.curseforge.com/minecraft/mc-mods/kiem-addons";

    private static final String MOD_ID = "modid";

    private static volatile boolean checked = false;
    private static volatile boolean updateAvailable = false;
    private static volatile String latestVersion = null;
    private static volatile String downloadUrl = MODRINTH_URL;

    public static boolean hasChecked() {
        return checked;
    }

    public static boolean hasUpdate() {
        return updateAvailable && latestVersion != null;
    }

    public static String getLatestVersion() {
        return latestVersion;
    }

    public static String getDownloadUrl() {
        return downloadUrl != null ? downloadUrl : MODRINTH_URL;
    }

    public static String getCurrentVersion() {
        try {
            return FabricLoader.getInstance()
                    .getModContainer(MOD_ID)
                    .map(c -> c.getMetadata().getVersion().getFriendlyString())
                    .orElse("1.0.1");
        } catch (Throwable ignored) {
            return "1.0.1";
        }
    }

    private static String getMinecraftVersion() {
        try {
            return SharedConstants.getCurrentVersion().id();
        } catch (Throwable ignored) {
            return "26.2";
        }
    }

    public static void checkAsync() {
        if (!KiemConfig.updateCheckerEnabled || checked) {
            return;
        }
        checked = true;

        CompletableFuture.runAsync(() -> {
            try {
                String currentVersion = getCurrentVersion();
                String mcVersion = getMinecraftVersion();
                String encodedMc = URLEncoder.encode(mcVersion, StandardCharsets.UTF_8);

                String apiUrl = "https://api.modrinth.com/v2/project/" + MODRINTH_SLUG
                        + "/version?loaders=%5B%22fabric%22%5D&game_versions=%5B%22" + encodedMc + "%22%5D";

                HttpClient client = HttpClient.newBuilder()
                        .connectTimeout(Duration.ofSeconds(5))
                        .build();

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(apiUrl))
                        .timeout(Duration.ofSeconds(5))
                        .header("User-Agent", "KiemAddons/" + currentVersion + " (https://github.com/Gamedese/kiem-addons)")
                        .header("Accept", "application/json")
                        .GET()
                        .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() != 200 || response.body() == null || response.body().isEmpty()) {
                    return;
                }

                JsonElement parsed = JsonParser.parseString(response.body());
                if (!parsed.isJsonArray()) {
                    return;
                }

                JsonArray versions = parsed.getAsJsonArray();
                String bestVersion = null;
                String bestUrl = MODRINTH_URL;

                for (JsonElement elem : versions) {
                    if (!elem.isJsonObject()) continue;
                    JsonObject obj = elem.getAsJsonObject();
                    if (!obj.has("version_number") || obj.get("version_number").isJsonNull()) continue;

                    String remoteVersion = obj.get("version_number").getAsString();
                    if (isNewerVersion(remoteVersion, currentVersion)) {
                        if (bestVersion == null || isNewerVersion(remoteVersion, bestVersion)) {
                            bestVersion = remoteVersion;
                            if (obj.has("id") && !obj.get("id").isJsonNull()) {
                                String versionId = obj.get("id").getAsString();
                                if (!versionId.isEmpty()) {
                                    bestUrl = MODRINTH_URL + "/version/" + versionId;
                                }
                            } else {
                                bestUrl = MODRINTH_URL;
                            }
                        }
                    }
                }

                if (bestVersion != null) {
                    latestVersion = bestVersion;
                    downloadUrl = bestUrl;
                    updateAvailable = true;
                }
            } catch (Exception ignored) {
                // Gracefully ignore network/404/parse errors without spamming logs
            }
        });
    }

    static boolean isNewerVersion(String candidate, String current) {
        if (candidate == null || current == null) return false;
        int[] candParts = parseSemVer(candidate);
        int[] currParts = parseSemVer(current);
        int len = Math.max(candParts.length, currParts.length);
        for (int i = 0; i < len; i++) {
            int a = i < candParts.length ? candParts[i] : 0;
            int b = i < currParts.length ? currParts[i] : 0;
            if (a > b) return true;
            if (a < b) return false;
        }
        return false;
    }

    private static int[] parseSemVer(String version) {
        String clean = version.trim();
        if (clean.startsWith("v") || clean.startsWith("V")) {
            clean = clean.substring(1);
        }
        int dashIdx = clean.indexOf('-');
        if (dashIdx >= 0) {
            clean = clean.substring(0, dashIdx);
        }
        int plusIdx = clean.indexOf('+');
        if (plusIdx >= 0) {
            clean = clean.substring(0, plusIdx);
        }
        String[] tokens = clean.split("\\.");
        int[] nums = new int[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            String digits = tokens[i].replaceAll("[^0-9].*$", "");
            if (digits.isEmpty()) {
                nums[i] = 0;
            } else {
                try {
                    nums[i] = Integer.parseInt(digits);
                } catch (NumberFormatException e) {
                    nums[i] = 0;
                }
            }
        }
        return nums;
    }
}
