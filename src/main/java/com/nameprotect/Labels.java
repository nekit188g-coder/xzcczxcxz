package com.nameprotect;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

/** Тексти меню — картинки, відрендерені шрифтом arialmt.ttf. */
public final class Labels {
    public static final int H = 43;
    private static final Map<String, Integer> W = new HashMap<>();
    private static final Map<String, Identifier> TEX = new HashMap<>();

    static {
        W.put("wm", 189);
        W.put("targetesp", 189);
        W.put("mode", 107);
        W.put("m1", 154);
        W.put("m2", 132);
        W.put("m3", 147);
        W.put("m4", 130);
        W.put("m5", 139);
        W.put("m6", 199);
        W.put("m7", 216);
        W.put("m8", 192);
        W.put("hitsounds", 179);
        W.put("h0", 51);
        W.put("h1", 219);
        W.put("h2", 172);
        W.put("watermark", 186);
        W.put("on", 54);
        W.put("off", 78);
    }

    private Labels() {}

    public static int width(String id) {
        Integer w = W.get(id);
        return w == null ? 0 : w;
    }

    public static void draw(GuiGraphics g, String id, int x, int y, float scale) {
        Integer w = W.get(id);
        if (w == null) return;
        Identifier tex = TEX.computeIfAbsent(id, k -> Identifier.fromNamespaceAndPath("nameprotect", "textures/gui/label/" + k + ".png"));
        g.pose().pushMatrix();
        g.pose().translate((float) x, (float) y);
        g.pose().scale(scale);
        g.blit(RenderPipelines.GUI_TEXTURED, tex, 0, 0, 0, 0, w, H, w, H);
        g.pose().popMatrix();
    }
}
