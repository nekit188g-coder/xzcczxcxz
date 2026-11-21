package com.nameprotect;

import net.fabricmc.fabric.api.client.rendering.v1.world.WorldExtractionContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

/** TargetESP: картинка поверх цілі (моб/гравець), на яку дивишся або яку вдарив. */
public final class TargetEsp {
    private static final String[] NAMES = {"radar", "bruh", "circle", "what", "triad", "fsociety", "photo", "sardelki"};
    private static final boolean[] SPIN = {true, true, true, true, true, true, false, false};
    private static final Identifier[] TEX = new Identifier[NAMES.length];
    private static final long HOLD_MS = 2000;

    private static Entity target;
    private static long targetTime;

    private static boolean hasSmooth;
    private static double sx, sy, sz;
    private static long lastNs;

    private static boolean visible;
    private static float ndcX, ndcY, ndcH;

    static {
        for (int i = 0; i < NAMES.length; i++) {
            TEX[i] = Identifier.fromNamespaceAndPath("nameprotect", "textures/target/" + NAMES[i] + ".png");
        }
    }

    private TargetEsp() {}

    public static void setTarget(Entity e) {
        if (e != target) {
            target = e;
            hasSmooth = false;
        }
        targetTime = System.currentTimeMillis();
    }

    private static boolean project(Matrix4fc view, Matrix4fc proj, Vec3 cam, boolean viewHasTranslation,
                                   double x, double y, double z, float[] out) {
        Vector4f v = viewHasTranslation
                ? new Vector4f((float) x, (float) y, (float) z, 1f)
                : new Vector4f((float) (x - cam.x), (float) (y - cam.y), (float) (z - cam.z), 1f);
        view.transform(v);
        proj.transform(v);
        if (v.w <= 0.001f) return false;
        out[0] = v.x / v.w;
        out[1] = v.y / v.w;
        return true;
    }

    /** Викликається щокадру після збору стану світу: рахуємо екранну позицію цілі. */
    public static void extract(WorldExtractionContext ctx) {
        visible = false;
        if (!Config.targetEsp) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        HitResult hr = mc.hitResult;
        if (hr instanceof EntityHitResult ehr && ehr.getEntity() instanceof LivingEntity le
                && le != mc.player && le.isAlive()) {
            setTarget(le);
        }

        Entity t = target;
        if (t == null) return;
        if (!t.isAlive() || t.isRemoved() || System.currentTimeMillis() - targetTime > HOLD_MS) {
            target = null;
            hasSmooth = false;
            return;
        }

        long now = System.nanoTime();
        double dt = lastNs == 0 ? 1.0 : (now - lastNs) / 1.0e9;
        lastNs = now;
        if (!hasSmooth) {
            sx = t.getX(); sy = t.getY(); sz = t.getZ();
            hasSmooth = true;
        } else {
            double k = 1.0 - Math.exp(-dt * 25.0);
            sx += (t.getX() - sx) * k;
            sy += (t.getY() - sy) * k;
            sz += (t.getZ() - sz) * k;
        }

        Matrix4fc view = ctx.viewMatrix();
        Matrix4fc proj = ctx.cullProjectionMatrix();
        Vec3 cam = ctx.camera().position();
        boolean hasTr = Math.abs(view.m30()) + Math.abs(view.m31()) + Math.abs(view.m32()) > 1.0e-3f;

        double h = t.getBbHeight();
        float[] c = new float[2], top = new float[2], bot = new float[2];
        if (!project(view, proj, cam, hasTr, sx, sy + h * 0.5, sz, c)) return;
        if (!project(view, proj, cam, hasTr, sx, sy + h, sz, top)) return;
        if (!project(view, proj, cam, hasTr, sx, sy, sz, bot)) return;

        ndcX = c[0];
        ndcY = c[1];
        ndcH = Math.abs(top[1] - bot[1]) * 0.5f;   // частка висоти екрана
        visible = true;
    }

    public static void render(GuiGraphics g) {
        if (!visible || !Config.targetEsp) return;
        int w = g.guiWidth(), h = g.guiHeight();
        float size = ndcH * h * 1.15f;
        size = Math.max(16f, Math.min(h * 0.6f, size));
        float cx = (ndcX * 0.5f + 0.5f) * w;
        float cy = (0.5f - ndcY * 0.5f) * h;
        if (cx < -size || cx > w + size || cy < -size || cy > h + size) return;

        int m = Math.max(0, Math.min(NAMES.length - 1, Config.mode));
        g.pose().pushMatrix();
        g.pose().translate(cx, cy);
        if (SPIN[m]) {
            float ang = (float) ((System.nanoTime() / 1.0e9) % 3.0 / 3.0 * (Math.PI * 2.0));
            g.pose().rotate(ang);
        }
        g.pose().scale(size / 256f);
        g.blit(RenderPipelines.GUI_TEXTURED, TEX[m], -128, -128, 0, 0, 256, 256, 256, 256);
        g.pose().popMatrix();
    }
}
