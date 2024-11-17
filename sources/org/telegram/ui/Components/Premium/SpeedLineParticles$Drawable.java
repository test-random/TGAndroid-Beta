package org.telegram.ui.Components.Premium;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;

public class SpeedLineParticles$Drawable {
    public final int count;
    private int lastColor;
    private float[] lines;
    public boolean paused;
    long pausedTime;
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
        float inProgress;
        private long lifeTime;
        private float vecX;
        private float vecY;
        private float x;
        private float y;

        private Particle() {
        }

        public void draw(Canvas canvas, int i, long j) {
            int i2 = i * 4;
            SpeedLineParticles$Drawable.this.lines[i2] = this.x;
            SpeedLineParticles$Drawable.this.lines[i2 + 1] = this.y;
            SpeedLineParticles$Drawable.this.lines[i2 + 2] = this.x + (AndroidUtilities.dp(30.0f) * this.vecX);
            SpeedLineParticles$Drawable.this.lines[i2 + 3] = this.y + (AndroidUtilities.dp(30.0f) * this.vecY);
            if (SpeedLineParticles$Drawable.this.paused) {
                return;
            }
            float dp = AndroidUtilities.dp(4.0f) * (SpeedLineParticles$Drawable.this.dt / 660.0f);
            SpeedLineParticles$Drawable speedLineParticles$Drawable = SpeedLineParticles$Drawable.this;
            float f = dp * speedLineParticles$Drawable.speedScale;
            this.x += this.vecX * f;
            this.y += this.vecY * f;
            float f2 = this.inProgress;
            if (f2 != 1.0f) {
                float f3 = f2 + (speedLineParticles$Drawable.dt / 200.0f);
                this.inProgress = f3;
                if (f3 > 1.0f) {
                    this.inProgress = 1.0f;
                }
            }
        }

        public void genPosition(long j, boolean z) {
            this.lifeTime = j + SpeedLineParticles$Drawable.this.minLifeTime + Utilities.fastRandom.nextInt(1000);
            SpeedLineParticles$Drawable speedLineParticles$Drawable = SpeedLineParticles$Drawable.this;
            RectF rectF = z ? speedLineParticles$Drawable.screenRect : speedLineParticles$Drawable.rect;
            float abs = rectF.left + Math.abs(Utilities.fastRandom.nextInt() % rectF.width());
            float abs2 = rectF.top + Math.abs(Utilities.fastRandom.nextInt() % rectF.height());
            this.x = abs;
            this.y = abs2;
            double atan2 = Math.atan2(abs - SpeedLineParticles$Drawable.this.rect.centerX(), this.y - SpeedLineParticles$Drawable.this.rect.centerY());
            this.vecX = (float) Math.sin(atan2);
            this.vecY = (float) Math.cos(atan2);
            this.alpha = (int) (((Utilities.fastRandom.nextInt(50) + 50) / 100.0f) * 255.0f);
            this.inProgress = 0.0f;
        }
    }

    public SpeedLineParticles$Drawable(int i) {
        this.count = i;
        this.lines = new float[i * 4];
    }

    public void init() {
        if (this.particles.isEmpty()) {
            for (int i = 0; i < this.count; i++) {
                this.particles.add(new Particle());
            }
        }
        updateColors();
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
            if (currentTimeMillis > particle.lifeTime || !this.screenRect.contains(particle.x, particle.y)) {
                particle.genPosition(currentTimeMillis, false);
            }
        }
        canvas.drawLines(this.lines, this.paint);
    }

    public void resetPositions() {
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 0; i < this.particles.size(); i++) {
            ((Particle) this.particles.get(i)).genPosition(currentTimeMillis, true);
        }
    }

    public void updateColors() {
        int alphaComponent = ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_premiumStartSmallStarsColor2), 80);
        if (this.lastColor != alphaComponent) {
            this.lastColor = alphaComponent;
            this.paint.setColor(alphaComponent);
        }
    }
}
