package com.nameprotect;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

/** Меню на Right Shift. Тексти — картинки з шрифтом arialmt.ttf. */
public class MenuScreen extends Screen {
    private static final float S = 0.2f;   // масштаб текстових картинок
    private int mx, my;
    private final List<Item> items = new ArrayList<>();

    private record Item(int x, int y, int w, int h, Runnable action) {}

    public MenuScreen() {
        super(Component.literal("LoLHelper"));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void button(GuiGraphics g, int x, int y, int w, int h, String label, boolean selected, String status, Runnable action) {
        boolean hover = mx >= x && mx < x + w && my >= y && my < y + h;
        int bg = selected ? 0xFF5B3FC4 : (hover ? 0xFF2E2E3E : 0xFF1C1C26);
        g.fill(x, y, x + w, y + h, bg);
        int ty = y + Math.max(0, (h - (int) (Labels.H * S)) / 2);
        Labels.draw(g, label, x + 4, ty, S);
        if (status != null) {
            Labels.draw(g, status, x + w - (int) (Labels.width(status) * S) - 4, ty, S);
        }
        items.add(new Item(x, y, w, h, action));
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        mx = mouseX;
        my = mouseY;
        items.clear();

        g.fill(0, 0, this.width, this.height, 0x66000000);

        final int pw = 250, ph = 148;
        int x0 = (this.width - pw) / 2;
        int y0 = (this.height - ph) / 2;
        g.fill(x0, y0, x0 + pw, y0 + ph, 0xF0121218);
        g.fill(x0, y0, x0 + pw, y0 + 2, 0xFF8B5CF6);
        Labels.draw(g, "wm", x0 + 8, y0 + 6, 0.3f);

        // --- ліва колонка: TargetESP
        int lx = x0 + 8, lw = 112;
        button(g, lx, y0 + 22, lw, 11, "targetesp", Config.targetEsp, Config.targetEsp ? "on" : "off",
                () -> Config.targetEsp = !Config.targetEsp);
        Labels.draw(g, "mode", lx + 1, y0 + 36, S);
        String[] modes = {"m1", "m2", "m3", "m4", "m5", "m6", "m7", "m8"};
        for (int i = 0; i < modes.length; i++) {
            final int idx = i;
            button(g, lx, y0 + 48 + i * 11, lw, 10, modes[i], Config.mode == i, null, () -> Config.mode = idx);
        }

        // --- права колонка: Hitsounds + Watermark
        int rx = x0 + 130, rw = 112;
        Labels.draw(g, "hitsounds", rx + 1, y0 + 24, S);
        String[] hs = {"h0", "h1", "h2"};
        for (int i = 0; i < hs.length; i++) {
            final int idx = i;
            button(g, rx, y0 + 35 + i * 11, rw, 10, hs[i], Config.hitSound == i, null, () -> {
                Config.hitSound = idx;
                HitSounds.play(idx);
            });
        }
        button(g, rx, y0 + 80, rw, 11, "watermark", Config.watermark, Config.watermark ? "on" : "off",
                () -> Config.watermark = !Config.watermark);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        for (Item it : items) {
            if (mx >= it.x() && mx < it.x() + it.w() && my >= it.y() && my < it.y() + it.h()) {
                it.action().run();
                Config.save();
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }
}
