package com.nameprotect;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;

public final class Config {
    public static boolean targetEsp = true;
    public static int mode = 0;       // 0..7 = Radar..Sardelki
    public static int hitSound = 0;   // 0 = Off, 1 = Undertale, 2 = Bubble
    public static boolean watermark = true;

    private Config() {}

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("nameprotect.properties");
    }

    private static int clamp(int v, int lo, int hi) {
        return Math.max(lo, Math.min(hi, v));
    }

    public static void load() {
        try {
            Path p = file();
            if (!Files.exists(p)) return;
            Properties pr = new Properties();
            try (InputStream in = Files.newInputStream(p)) {
                pr.load(in);
            }
            targetEsp = Boolean.parseBoolean(pr.getProperty("targetEsp", "true"));
            mode = clamp(Integer.parseInt(pr.getProperty("mode", "0")), 0, 7);
            hitSound = clamp(Integer.parseInt(pr.getProperty("hitSound", "0")), 0, 2);
            watermark = Boolean.parseBoolean(pr.getProperty("watermark", "true"));
        } catch (Exception ignored) {
        }
    }

    public static void save() {
        try {
            Properties pr = new Properties();
            pr.setProperty("targetEsp", String.valueOf(targetEsp));
            pr.setProperty("mode", String.valueOf(mode));
            pr.setProperty("hitSound", String.valueOf(hitSound));
            pr.setProperty("watermark", String.valueOf(watermark));
            try (OutputStream out = Files.newOutputStream(file())) {
                pr.store(out, "NameProtect / LoLHelper");
            }
        } catch (Exception ignored) {
        }
    }
}
