package com.ev.bedrockcaseopening;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.FontRenderer;

public class FloatFontRenderer {
    private final FontRenderer fontRenderer;

    public FloatFontRenderer(FontRenderer fontRenderer) {
        this.fontRenderer = fontRenderer;
    }

    public void drawString(String text, float x, float y, int color, boolean shadow) {
        GL11.glPushMatrix();
        GL11.glTranslatef(x, y, 0f);
        fontRenderer.drawString(text, 0, 0, color, shadow);
        GL11.glPopMatrix();
    }

    public void drawString(String text, float x, float y, int color) {
        drawString(text, x, y, color, false);
    }
    
    public void drawCenteredString(String text, float x, float y, int color, boolean shadow) {
        float textWidth = fontRenderer.getStringWidth(text);
        float textHeight = 9f;

        GL11.glPushMatrix();
        GL11.glTranslatef(x - textWidth / 2f, y - textHeight / 2f, 0f);
        fontRenderer.drawString(text, 0, 0, color, shadow);
        GL11.glPopMatrix();
    }

    public void drawCenteredString(String text, float x, float y, int color) {
        drawCenteredString(text, x, y, color, false);
    }
}