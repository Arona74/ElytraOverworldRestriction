package io.arona74.elytraoverworldrestriction.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ElytraConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "elytraoverworldrestriction.json");
    
    // Config options
    public boolean enableInNether = false;
    public boolean realisticGliding = false;
    public boolean invisibleOnGround = true;
    
    // Transient field for the singleton instance
    private static transient ElytraConfig instance;
    private static transient long lastModified = 0;
    
    public static ElytraConfig getInstance() {
        // Check if config file was modified and reload if needed
        if (CONFIG_FILE.exists()) {
            long currentModified = CONFIG_FILE.lastModified();
            if (instance == null || currentModified != lastModified) {
                instance = load();
                lastModified = currentModified;
            }
        } else if (instance == null) {
            instance = load();
        }
        return instance;
    }
    
    public static ElytraConfig load() {
        ElytraConfig config = null;
        
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                config = GSON.fromJson(reader, ElytraConfig.class);
                if (config != null) {
                    System.out.println("ElytraOverworldRestriction: Config loaded from file");
                    return config;
                }
            } catch (IOException e) {
                System.err.println("ElytraOverworldRestriction: Failed to load config: " + e.getMessage());
            }
        }
        
        // Create default config if file doesn't exist or failed to load
        System.out.println("ElytraOverworldRestriction: Creating default config file");
        config = new ElytraConfig();
        config.save();
        return config;
    }
    
    public void save() {
        try {
            // Ensure the config directory exists
            if (!CONFIG_FILE.getParentFile().exists()) {
                CONFIG_FILE.getParentFile().mkdirs();
                System.out.println("ElytraOverworldRestriction: Created config directory");
            }
            
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(this, writer);
                System.out.println("ElytraOverworldRestriction: Config saved to " + CONFIG_FILE.getAbsolutePath());
                // Update the singleton instance and timestamp
                instance = this;
                lastModified = CONFIG_FILE.lastModified();
            }
        } catch (IOException e) {
            System.err.println("ElytraOverworldRestriction: Failed to save config: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public boolean shouldRestrictInDimension(String dimensionName) {
        return "overworld".equals(dimensionName) || (enableInNether && "the_nether".equals(dimensionName));
    }
}