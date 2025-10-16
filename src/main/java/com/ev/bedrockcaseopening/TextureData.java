package com.ev.bedrockcaseopening;

import net.minecraft.util.ResourceLocation;

public class TextureData {
    private final ResourceLocation rl;
    private int frametime;
    private int frames;
    

    public TextureData(ResourceLocation rl, int frametime, int frames) {
        this.rl = rl;
        this.frametime = frametime;
        this.frames = frames;
    }

    public ResourceLocation getRl() {
        return rl;
    }

    public int getFrames() {
        return frames;
    }

    public int getFrametime() {
        return frametime;
    }
}
