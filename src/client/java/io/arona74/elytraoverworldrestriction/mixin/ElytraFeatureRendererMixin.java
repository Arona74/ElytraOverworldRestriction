package io.arona74.elytraoverworldrestriction.mixin;

import io.arona74.elytraoverworldrestriction.config.ElytraConfig;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ElytraFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(ElytraFeatureRenderer.class)
public class ElytraFeatureRendererMixin {
    
    // Use method_4199 which is the obfuscated name for render in MC 1.20.1
    @Inject(method = "method_4199", at = @At("HEAD"), cancellable = true)
    private void onRender(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, Entity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        // Only apply to players
        if (!(entity instanceof PlayerEntity player)) {
            return; // Let non-player entities render normally
        }
        
        // Check config setting
        ElytraConfig config = ElytraConfig.getInstance();
        if (!config.invisibleOnGround) {
            return; // Feature disabled, render normally
        }
        
        // Check if player has elytra equipped
        if (!hasElytraEquipped(player)) {
            return; // No elytra equipped, nothing to hide
        }
        
        // Check if player should have invisible elytra
        if (shouldHideElytra(player)) {
            ci.cancel(); // Cancel the rendering
        }
    }
    
    private boolean hasElytraEquipped(PlayerEntity player) {
        // Check vanilla chest slot
        if (player.getEquippedStack(net.minecraft.entity.EquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
            return true;
        }
        
        // Check trinkets if available (using reflection to avoid compile-time dependency)
        try {
            Class<?> trinketsApiClass = Class.forName("dev.emi.trinkets.api.TrinketsApi");
            java.lang.reflect.Method getTrinketComponentMethod = trinketsApiClass.getMethod("getTrinketComponent", net.minecraft.entity.LivingEntity.class);
            
            Object trinketComponentOptional = getTrinketComponentMethod.invoke(null, player);
            
            if (trinketComponentOptional instanceof java.util.Optional<?> javaOptional && javaOptional.isPresent()) {
                Object trinketComponent = javaOptional.get();
                
                java.lang.reflect.Method getAllEquippedMethod = trinketComponent.getClass().getMethod("getAllEquipped");
                Object equippedList = getAllEquippedMethod.invoke(trinketComponent);
                
                if (equippedList instanceof Iterable<?> equipped) {
                    for (Object pair : equipped) {
                        try {
                            java.lang.reflect.Method getItemStackMethod = pair.getClass().getMethod("method_15441");
                            Object itemStack = getItemStackMethod.invoke(pair);
                            
                            if (itemStack instanceof net.minecraft.item.ItemStack stack && stack.getItem() == Items.ELYTRA) {
                                return true;
                            }
                        } catch (Exception e) {
                            continue;
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Trinkets not available or failed, ignore
        }
        
        return false;
    }
    
    private boolean shouldHideElytra(PlayerEntity player) {
        return player.isOnGround() && !player.isFallFlying();
    }
}