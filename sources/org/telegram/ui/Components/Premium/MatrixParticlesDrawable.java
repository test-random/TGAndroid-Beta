package org.telegram.ui.Components.Premium;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;

public class MatrixParticlesDrawable {
    MatrixTextParticle[][] matrixTextParticles;
    ArrayList[] particles;
    int size;
    RectF excludeRect = new RectF();
    Bitmap[] bitmaps = new Bitmap[16];
    Rect drawingRect = new Rect();
    Paint paint = new Paint();

    private class MatrixTextParticle {
        int index;
        long lastUpdateTime;
        int nextIndex;
        long nextUpdateTime;

        private MatrixTextParticle() {
        }

        public void draw(Canvas canvas, float f, float f2, long j, float f3) {
            long j2 = this.nextUpdateTime - j;
            if (j2 >= 150) {
                MatrixParticlesDrawable.this.paint.setAlpha((int) (f3 * 255.0f));
                MatrixParticlesDrawable matrixParticlesDrawable = MatrixParticlesDrawable.this;
                canvas.drawBitmap(matrixParticlesDrawable.bitmaps[this.index], f, f2, matrixParticlesDrawable.paint);
                return;
            }
            float clamp = Utilities.clamp(1.0f - (((float) j2) / 150.0f), 1.0f, 0.0f);
            MatrixParticlesDrawable.this.paint.setAlpha((int) ((1.0f - clamp) * f3 * 255.0f));
            MatrixParticlesDrawable matrixParticlesDrawable2 = MatrixParticlesDrawable.this;
            canvas.drawBitmap(matrixParticlesDrawable2.bitmaps[this.index], f, f2, matrixParticlesDrawable2.paint);
            MatrixParticlesDrawable.this.paint.setAlpha((int) (f3 * clamp * 255.0f));
            MatrixParticlesDrawable matrixParticlesDrawable3 = MatrixParticlesDrawable.this;
            canvas.drawBitmap(matrixParticlesDrawable3.bitmaps[this.nextIndex], f, f2, matrixParticlesDrawable3.paint);
            MatrixParticlesDrawable.this.paint.setAlpha(255);
            if (clamp >= 1.0f) {
                this.index = this.nextIndex;
                this.lastUpdateTime = j;
                this.nextIndex = Math.abs(Utilities.fastRandom.nextInt() % 16);
                this.nextUpdateTime = j + Math.abs(Utilities.fastRandom.nextInt() % 300) + 150;
            }
        }

        public void init(long j) {
            this.index = Math.abs(Utilities.fastRandom.nextInt() % 16);
            this.nextIndex = Math.abs(Utilities.fastRandom.nextInt() % 16);
            this.lastUpdateTime = j;
            this.nextUpdateTime = j + Math.abs(Utilities.fastRandom.nextInt() % 300) + 150;
        }
    }

    private class Particle {
        int len;
        long time;
        int y;

        private Particle() {
            this.len = 5;
        }

        public void init(int i, long j) {
            this.y = Math.abs(Utilities.fastRandom.nextInt() % i);
            this.time = j;
            this.len = Math.abs(Utilities.fastRandom.nextInt() % 6) + 4;
        }

        public void reset(long j) {
            this.y = 0;
            this.time = j;
            this.len = Math.abs(Utilities.fastRandom.nextInt() % 6) + 4;
        }
    }

    public void init() {
        this.size = AndroidUtilities.dp(16.0f);
        TextPaint textPaint = new TextPaint(65);
        textPaint.setTypeface(AndroidUtilities.getTypeface("fonts/rcondensedbold.ttf"));
        textPaint.setTextSize(this.size);
        textPaint.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_premiumStartSmallStarsColor2), 30));
        textPaint.setTextAlign(Paint.Align.CENTER);
        int i = 0;
        while (i < 16) {
            int i2 = i < 10 ? i + 48 : i + 55;
            Bitmap[] bitmapArr = this.bitmaps;
            int i3 = this.size;
            bitmapArr[i] = Bitmap.createBitmap(i3, i3, Bitmap.Config.ARGB_8888);
            new Canvas(this.bitmaps[i]).drawText(Character.toString((char) i2), r4 >> 1, this.size, textPaint);
            i++;
        }
    }

    public void onDraw(Canvas canvas) {
        int i;
        int i2;
        Particle particle;
        int width = this.drawingRect.width() / this.size;
        int height = this.drawingRect.height() / this.size;
        if (width == 0 || height == 0) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        ArrayList[] arrayListArr = this.particles;
        AnonymousClass1 anonymousClass1 = null;
        if (arrayListArr == null || arrayListArr.length != width + 1) {
            this.particles = new ArrayList[width + 1];
            for (int i3 = 0; i3 <= width; i3++) {
                this.particles[i3] = new ArrayList();
                Particle particle2 = new Particle();
                particle2.init(height, currentTimeMillis);
                this.particles[i3].add(particle2);
            }
        }
        MatrixTextParticle[][] matrixTextParticleArr = this.matrixTextParticles;
        if (matrixTextParticleArr == null || matrixTextParticleArr.length != width + 1 || matrixTextParticleArr[0].length != height + 1) {
            this.matrixTextParticles = new MatrixTextParticle[width + 1];
            for (int i4 = 0; i4 <= width; i4++) {
                this.matrixTextParticles[i4] = new MatrixTextParticle[height + 1];
                for (int i5 = 0; i5 <= height; i5++) {
                    this.matrixTextParticles[i4][i5] = new MatrixTextParticle();
                    this.matrixTextParticles[i4][i5].init(currentTimeMillis);
                }
            }
        }
        int i6 = 0;
        while (i6 <= width) {
            ArrayList arrayList = this.particles[i6];
            int i7 = 0;
            while (i7 < arrayList.size()) {
                Particle particle3 = (Particle) arrayList.get(i7);
                int i8 = 1;
                if (currentTimeMillis - particle3.time > 50) {
                    int i9 = particle3.y + 1;
                    particle3.y = i9;
                    particle3.time = currentTimeMillis;
                    if (i9 - particle3.len >= height) {
                        if (arrayList.size() == 1) {
                            particle3.reset(currentTimeMillis);
                        } else {
                            arrayList.remove(particle3);
                            i7--;
                        }
                    }
                    if (particle3.y > particle3.len && i7 == arrayList.size() - 1 && Math.abs(Utilities.fastRandom.nextInt(4)) == 0) {
                        Particle particle4 = new Particle();
                        particle4.reset(currentTimeMillis);
                        arrayList.add(particle4);
                    }
                }
                int i10 = i7;
                int min = Math.min(particle3.y, height + 1);
                int max = Math.max(0, particle3.y - particle3.len);
                while (max < min) {
                    int i11 = this.size;
                    float f = i11 * i6;
                    float f2 = i11 * max;
                    if (this.excludeRect.contains(f, f2)) {
                        i = max;
                        i2 = min;
                        particle = particle3;
                    } else {
                        i = max;
                        i2 = min;
                        particle = particle3;
                        this.matrixTextParticles[i6][max].draw(canvas, f, f2, currentTimeMillis, Utilities.clamp(((1.0f - ((particle3.y - max) / (particle3.len - i8))) * 0.8f) + 0.2f, 1.0f, 0.0f));
                    }
                    max = i + 1;
                    min = i2;
                    particle3 = particle;
                    i8 = 1;
                }
                i7 = i10 + 1;
                anonymousClass1 = null;
            }
            i6++;
            anonymousClass1 = null;
        }
    }
}
