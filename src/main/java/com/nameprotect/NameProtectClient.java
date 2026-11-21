package com.nameprotect;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import org.lwjgl.glfw.GLFW;

public class NameProtectClient implements ClientModInitializer {
    /** Справжній нік (для NameProtect-підміни). */
    public static volatile String realName = "";

    private static KeyMapping menuKey;

    @Override
    public void onInitializeClient() {
        Config.load();

        menuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.nameprotect.menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, KeyMapping.Category.MISC));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                realName = client.player.getName().getString();
            }
            while (menuKey.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new MenuScreen());
                }
            }
        });

        WorldRenderEvents.END_EXTRACTION.register(TargetEsp::extract);

        HudElementRegistry.addLast(Identifier.fromNamespaceAndPath("nameprotect", "overlay"), (g, tick) -> {
            TargetEsp.render(g);
            Watermark.render(g);
        });

        AttackEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
            if (level.isClientSide() && entity instanceof LivingEntity) {
                TargetEsp.setTarget(entity);
                HitSounds.play();
            }
            return InteractionResult.PASS;
        });
    }
}
