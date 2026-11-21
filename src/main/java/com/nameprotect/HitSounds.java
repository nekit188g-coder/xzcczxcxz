package com.nameprotect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public final class HitSounds {
    private static final SoundEvent UNDERTALE = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("nameprotect", "hit_undertale"));
    private static final SoundEvent BUBBLE = SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("nameprotect", "hit_bubble"));

    private HitSounds() {}

    public static void play() {
        play(Config.hitSound);
    }

    public static void play(int mode) {
        if (mode <= 0) return;
        SoundEvent ev = mode == 1 ? UNDERTALE : BUBBLE;
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(ev, 1.0F, 1.0F));
    }
}
