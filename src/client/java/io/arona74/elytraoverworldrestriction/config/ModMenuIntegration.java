package io.arona74.elytraoverworldrestriction.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class ModMenuIntegration implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<Screen> getModConfigScreenFactory() {
        return parent -> {
            ElytraConfig config = ElytraConfig.getInstance();
            
            ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("ElytraOverworldRestriction Config"));
            
            ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();
            
            // Enable in Nether option
            general.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Enable in Nether"), config.enableInNether)
                .setDefaultValue(false)
                .setTooltip(Text.literal("Enable elytra restrictions in the Nether dimension too"))
                .setSaveConsumer(value -> config.enableInNether = value)
                .build());
            
            // Realistic gliding option
            general.addEntry(entryBuilder
                .startBooleanToggle(Text.literal("Realistic Gliding"), config.realisticGliding)
                .setDefaultValue(true)
                .setTooltip(Text.literal("Enable slower realistic gliding with velocity restrictions.\nDisable for classic gliding behavior."))
                .setSaveConsumer(value -> config.realisticGliding = value)
                .build());
            
            builder.setSavingRunnable(() -> {
                config.save();
            });
            
            return builder.build();
        };
    }
}