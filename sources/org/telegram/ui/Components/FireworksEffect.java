package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;

public class FireworksEffect {
    private long lastAnimationTime;
    private Paint particlePaint;
    final float angleDiff = 1.0471976f;
    private ArrayList particles = new ArrayList();
    private ArrayList freeParticles = new ArrayList();

    public class Particle {
        float alpha;
        int color;
        float currentTime;
        float lifeTime;
        float scale;
        int type;
        float velocity;
        float vx;
        float vy;
        float x;
        float y;

        private Particle() {
        }

        public void draw(Canvas canvas) {
            if (this.type != 0) {
                return;
            }
            FireworksEffect.this.particlePaint.setColor(this.color);
            FireworksEffect.this.particlePaint.setStrokeWidth(AndroidUtilities.dp(1.5f) * this.scale);
            FireworksEffect.this.particlePaint.setAlpha((int) (this.alpha * 255.0f));
            canvas.drawPoint(this.x, this.y, FireworksEffect.this.particlePaint);
        }
    }

    public FireworksEffect() {
        Paint paint = new Paint(1);
        this.particlePaint = paint;
        paint.setStrokeWidth(AndroidUtilities.dp(1.5f));
        this.particlePaint.setColor(Theme.getColor(Theme.key_actionBarDefaultTitle) & (-1644826));
        this.particlePaint.setStrokeCap(Paint.Cap.ROUND);
        this.particlePaint.setStyle(Paint.Style.STROKE);
        for (int i = 0; i < 20; i++) {
            this.freeParticles.add(new Particle());
        }
    }

    private void updateParticles(long j) {
        int size = this.particles.size();
        int i = 0;
        while (i < size) {
            Particle particle = (Particle) this.particles.get(i);
            float f = particle.currentTime;
            float f2 = particle.lifeTime;
            if (f >= f2) {
                if (this.freeParticles.size() < 40) {
                    this.freeParticles.add(particle);
                }
                this.particles.remove(i);
                i--;
                size--;
            } else {
                particle.alpha = 1.0f - AndroidUtilities.decelerateInterpolator.getInterpolation(f / f2);
                float f3 = particle.x;
                float f4 = particle.vx;
                float f5 = particle.velocity;
                float f6 = (float) j;
                particle.x = f3 + (((f4 * f5) * f6) / 500.0f);
                float f7 = particle.y;
                float f8 = particle.vy;
                particle.y = f7 + (((f5 * f8) * f6) / 500.0f);
                particle.vy = f8 + (f6 / 100.0f);
                particle.currentTime += f6;
            }
            i++;
        }
    }

    public void onDraw(View view, Canvas canvas) {
        Particle particle;
        if (view == null || canvas == null) {
            return;
        }
        int size = this.particles.size();
        for (int i = 0; i < size; i++) {
            ((Particle) this.particles.get(i)).draw(canvas);
        }
        if (Utilities.random.nextBoolean() && this.particles.size() + 8 < 150) {
            int i2 = Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0;
            float nextFloat = Utilities.random.nextFloat() * view.getMeasuredWidth();
            float nextFloat2 = i2 + (Utilities.random.nextFloat() * ((view.getMeasuredHeight() - AndroidUtilities.dp(20.0f)) - i2));
            int nextInt = Utilities.random.nextInt(4);
            int i3 = nextInt != 0 ? nextInt != 1 ? nextInt != 2 ? nextInt != 3 ? -5752 : -15088582 : -207021 : -843755 : -13357350;
            for (int i4 = 0; i4 < 8; i4++) {
                double nextInt2 = Utilities.random.nextInt(270) - 225;
                Double.isNaN(nextInt2);
                double d = nextInt2 * 0.017453292519943295d;
                float cos = (float) Math.cos(d);
                float sin = (float) Math.sin(d);
                if (this.freeParticles.isEmpty()) {
                    particle = new Particle();
                } else {
                    particle = (Particle) this.freeParticles.get(0);
                    this.freeParticles.remove(0);
                }
                particle.x = nextFloat;
                particle.y = nextFloat2;
                particle.vx = cos * 1.5f;
                particle.vy = sin;
                particle.color = i3;
                particle.alpha = 1.0f;
                particle.currentTime = 0.0f;
                particle.scale = Math.max(1.0f, Utilities.random.nextFloat() * 1.5f);
                particle.type = 0;
                particle.lifeTime = Utilities.random.nextInt(1000) + 1000;
                particle.velocity = (Utilities.random.nextFloat() * 4.0f) + 20.0f;
                this.particles.add(particle);
            }
        }
        long currentTimeMillis = System.currentTimeMillis();
        updateParticles(Math.min(17L, currentTimeMillis - this.lastAnimationTime));
        this.lastAnimationTime = currentTimeMillis;
        view.invalidate();
    }
}
