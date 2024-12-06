package org.telegram.ui.Components.Premium;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;

public abstract class HelloParticles {
    private static final String[] hellos = {"Hello", "Привіт", "Привет", "Bonjour", "Hola", "Ciao", "Olá", "여보세요", "你好", "Salve", "Sveiki", "Halo", "გამარჯობა", "Hallå", "Salam", "Tere", "Dia dhuit", "こんにちは", "Сайн уу", "Bongu", "Ahoj", "γεια", "Zdravo", "नमस्ते", "Habari", "Hallo", "ជំរាបសួរ", "مرحبًا", "ನಮಸ್ಕಾರ", "Салам", "Silav li wir", "سڵاو", "Kif inti", "Talofa", "Thobela", "हॅलो", "ሰላም", "Здраво", "ഹലോ", "ہیلو", "ꯍꯦꯜꯂꯣ", "Alô", "வணக்கம்", "Mhoro", "Moni", "Alo", "สวัสดี", "Salom", "Բարեւ"};

    public static class Drawable {
        private float bitmapScale;
        public final int count;
        public boolean paused;
        long pausedTime;
        private TextPaint textPaint = new TextPaint(1);
        private HashMap bitmaps = new HashMap();
        public RectF rect = new RectF();
        public RectF screenRect = new RectF();
        private Paint paint = new Paint();
        ArrayList particles = new ArrayList();
        public float speedScale = 1.0f;
        public int size1 = 14;
        public int size2 = 12;
        public int size3 = 10;
        public long minLifeTime = 2000;
        private final float dt = 1000.0f / AndroidUtilities.screenRefreshRate;

        private class Particle {
            private int alpha;
            private Bitmap bitmap;
            private long duration;
            private int h;
            float inProgress;
            private int l;
            private float scale;
            private boolean set;
            private StaticLayout staticLayout;
            private float vecX;
            private float vecY;
            private int w;
            private float x;
            private float y;

            private Particle() {
            }

            public void draw(Canvas canvas, int i, long j) {
                Drawable drawable = Drawable.this;
                if (!drawable.paused) {
                    float f = this.inProgress;
                    if (f != 1.0f) {
                        float f2 = f + (drawable.dt / ((float) this.duration));
                        this.inProgress = f2;
                        if (f2 > 1.0f) {
                            this.inProgress = 1.0f;
                        }
                    }
                }
                if (this.bitmap != null) {
                    canvas.save();
                    float pow = 1.0f - (((float) Math.pow(this.inProgress - 0.5f, 2.0d)) * 4.0f);
                    float f3 = (this.scale / Drawable.this.bitmapScale) * ((0.4f * pow) + 0.7f);
                    canvas.translate(this.x - (this.w / 2.0f), this.y - (this.h / 2.0f));
                    canvas.scale(f3, f3, this.w / 2.0f, this.h / 2.0f);
                    Drawable.this.paint.setAlpha((int) (this.alpha * pow));
                    canvas.drawBitmap(this.bitmap, 0.0f, 0.0f, Drawable.this.paint);
                    canvas.restore();
                }
            }

            public void genPosition(long j, int i, boolean z) {
                this.duration = Math.abs(Utilities.fastRandom.nextLong() % 2250) + 2250;
                this.scale = (Math.abs(Utilities.fastRandom.nextFloat()) * 0.45f) + 0.6f;
                String str = HelloParticles.hellos[Math.abs(Utilities.fastRandom.nextInt() % HelloParticles.hellos.length)];
                if (str.length() > 7) {
                    this.scale *= 0.6f;
                } else if (str.length() > 5) {
                    this.scale *= 0.75f;
                }
                StaticLayout staticLayout = new StaticLayout(str, Drawable.this.textPaint, AndroidUtilities.displaySize.x, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.staticLayout = staticLayout;
                if (staticLayout.getLineCount() <= 0) {
                    this.h = 0;
                    this.w = 0;
                    this.l = 0;
                } else {
                    this.l = (int) this.staticLayout.getLineLeft(0);
                    this.w = (int) this.staticLayout.getLineWidth(0);
                    this.h = this.staticLayout.getHeight();
                }
                Bitmap bitmap = (Bitmap) Drawable.this.bitmaps.get(str);
                this.bitmap = bitmap;
                if (bitmap == null) {
                    this.bitmap = Bitmap.createBitmap(Math.max(1, this.w - Math.max(0, this.l)), Math.max(1, this.h), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(this.bitmap);
                    canvas.translate(-this.l, 0.0f);
                    this.staticLayout.draw(canvas);
                    Drawable.this.bitmaps.put(str, this.bitmap);
                }
                RectF rectF = Drawable.this.rect;
                float f = this.w / 4.0f;
                float f2 = rectF.left + f;
                float f3 = rectF.right - f;
                int i2 = i % 2;
                float centerX = rectF.centerX();
                if (i2 == 0) {
                    f3 = centerX - (this.w / 2.0f);
                } else {
                    f2 = (this.w / 2.0f) + centerX;
                }
                float f4 = f3 - f2;
                float abs = Math.abs(Utilities.fastRandom.nextInt() % f4) + f2;
                float abs2 = Drawable.this.rect.top + Math.abs(Utilities.fastRandom.nextInt() % Drawable.this.rect.height());
                float f5 = 0.0f;
                for (int i3 = 0; i3 < 10; i3++) {
                    float abs3 = Math.abs(Utilities.fastRandom.nextInt() % f4) + f2;
                    float abs4 = Drawable.this.rect.top + Math.abs(Utilities.fastRandom.nextInt() % Drawable.this.rect.height());
                    float f6 = 2.1474836E9f;
                    for (int i4 = 0; i4 < Drawable.this.particles.size(); i4++) {
                        Particle particle = (Particle) Drawable.this.particles.get(i4);
                        if (particle.set) {
                            float min = Math.min(Math.abs((particle.x + ((particle.w * (this.scale / Drawable.this.bitmapScale)) * 1.1f)) - abs3), Math.abs(particle.x - abs3));
                            float f7 = particle.y - abs4;
                            float f8 = (min * min) + (f7 * f7);
                            if (f8 < f6) {
                                f6 = f8;
                            }
                        }
                    }
                    if (f6 > f5) {
                        abs = abs3;
                        abs2 = abs4;
                        f5 = f6;
                    }
                }
                this.x = abs;
                this.y = abs2;
                double atan2 = Math.atan2(abs - Drawable.this.rect.centerX(), this.y - Drawable.this.rect.centerY());
                this.vecX = (float) Math.sin(atan2);
                this.vecY = (float) Math.cos(atan2);
                this.alpha = (int) (((Utilities.fastRandom.nextInt(50) + 50) / 100.0f) * 255.0f);
                this.inProgress = z ? Math.abs((Utilities.fastRandom.nextFloat() % 1.0f) * 0.9f) : 0.0f;
                this.set = true;
            }
        }

        public Drawable(int i) {
            this.bitmapScale = 1.0f;
            this.count = i;
            this.textPaint.setTypeface(AndroidUtilities.bold());
            this.textPaint.setColor(-1);
            int devicePerformanceClass = SharedConfig.getDevicePerformanceClass();
            this.bitmapScale = devicePerformanceClass != 0 ? devicePerformanceClass != 1 ? 0.75f : 0.5f : 0.25f;
            this.textPaint.setTextSize(AndroidUtilities.dp(this.bitmapScale * 24.0f));
            this.paint.setColor(-1);
        }

        public void init() {
            if (this.particles.isEmpty()) {
                for (int i = 0; i < this.count; i++) {
                    this.particles.add(new Particle());
                }
            }
        }

        public void onDraw(Canvas canvas) {
            long currentTimeMillis = System.currentTimeMillis();
            for (int i = 0; i < this.particles.size(); i++) {
                Particle particle = (Particle) this.particles.get(i);
                if (this.paused) {
                    particle.draw(canvas, i, this.pausedTime);
                } else {
                    particle.draw(canvas, i, currentTimeMillis);
                }
                if (particle.inProgress >= 1.0f) {
                    particle.genPosition(currentTimeMillis, i, false);
                }
            }
        }

        public void recycle() {
            Iterator it = this.bitmaps.values().iterator();
            while (it.hasNext()) {
                ((Bitmap) it.next()).recycle();
            }
            this.bitmaps.clear();
        }

        public void resetPositions() {
            long currentTimeMillis = System.currentTimeMillis();
            for (int i = 0; i < this.particles.size(); i++) {
                ((Particle) this.particles.get(i)).genPosition(currentTimeMillis, i, true);
            }
        }
    }
}
