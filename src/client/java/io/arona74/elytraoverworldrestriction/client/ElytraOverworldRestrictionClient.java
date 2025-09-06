package io.arona74.elytraoverworldrestriction.client;

import io.arona74.elytraoverworldrestriction.config.ElytraConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public class ElytraOverworldRestrictionClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        // Load config on client side
        ElytraConfig.getInstance();
        
        // Elytra rendering is handled by the mixin (ElytraFeatureRendererMixin)
        
        System.out.println("ElytraOverworldRestriction: Client-side initialized with elytra rendering mixin");
    }
}