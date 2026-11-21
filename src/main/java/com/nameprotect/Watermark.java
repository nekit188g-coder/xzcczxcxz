package com.nameprotect;

import net.minecraft.client.gui.GuiGraphics;

public final class Watermark {
    private Watermark() {}

    public static void render(GuiGraphics g) {
        if (!Config.watermark) return;
        float s = 0.32f;
        int tw = (int) (Labels.width("wm") * s);
        int th = (int) (Labels.H * s);
        int x = 6, y = 6;
        g.fill(x, y, x + tw + 12, y + th + 8, 0xAA101018);
        g.fill(x, y, x + 2, y + th + 8, 0xFF8B5CF6);
        Labels.draw(g, "wm", x + 7, y + 4, s);
    }
}
