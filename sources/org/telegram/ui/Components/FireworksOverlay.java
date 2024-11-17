package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;

public class FireworksOverlay extends View {
    private static int[] colors;
    private static final int fallParticlesCount;
    private static int[] heartColors;
    private static Drawable[] heartDrawable;
    private static Paint[] paint;
    private static final int particlesCount;
    private static int[] starsColors;
    private static Drawable[] starsDrawable;
    private int fallingDownCount;
    private boolean isFebruary14;
    private long lastUpdateTime;
    private ArrayList particles;
    private RectF rect;
    private float speedCoef;
    private boolean started;
    private boolean startedFall;
    private boolean withStars;

    public class Particle {
        byte colorType;
        byte finishedStart;
        float moveX;
        float moveY;
        short rotation;
        byte side;
        byte type;
        byte typeSize;
        float x;
        byte xFinished;
        float y;

        private Particle() {
        }

        public void draw(Canvas canvas) {
            byte b = this.type;
            if (b == 0) {
                canvas.drawCircle(this.x, this.y, AndroidUtilities.dp(this.typeSize), FireworksOverlay.paint[this.colorType]);
                return;
            }
            if (b == 1) {
                FireworksOverlay.this.rect.set(this.x - AndroidUtilities.dp(this.typeSize), this.y - AndroidUtilities.dp(2.0f), this.x + AndroidUtilities.dp(this.typeSize), this.y + AndroidUtilities.dp(2.0f));
                canvas.save();
                canvas.rotate(this.rotation, FireworksOverlay.this.rect.centerX(), FireworksOverlay.this.rect.centerY());
                canvas.drawRoundRect(FireworksOverlay.this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), FireworksOverlay.paint[this.colorType]);
            } else {
                if (b != 2) {
                    return;
                }
                Drawable drawable = FireworksOverlay.starsDrawable != null ? FireworksOverlay.starsDrawable[this.colorType] : null;
                if (FireworksOverlay.heartDrawable != null) {
                    drawable = FireworksOverlay.heartDrawable[this.colorType];
                }
                if (drawable == null) {
                    return;
                }
                int intrinsicWidth = drawable.getIntrinsicWidth() / 2;
                int intrinsicHeight = drawable.getIntrinsicHeight() / 2;
                int i = (int) this.x;
                int i2 = (int) this.y;
                drawable.setBounds(i - intrinsicWidth, i2 - intrinsicHeight, i + intrinsicWidth, i2 + intrinsicHeight);
                canvas.save();
                canvas.rotate(this.rotation, this.x, this.y);
                float f = this.typeSize / 6.0f;
                canvas.scale(f, f, this.x, this.y);
                drawable.draw(canvas);
            }
            canvas.restore();
        }

        public boolean update(int r10) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FireworksOverlay.Particle.update(int):boolean");
        }
    }

    static {
        particlesCount = SharedConfig.getDevicePerformanceClass() == 0 ? 50 : 60;
        fallParticlesCount = SharedConfig.getDevicePerformanceClass() == 0 ? 20 : 30;
        colors = new int[]{-13845272, -6421296, -79102, -187561, -14185218, -10897300};
        heartColors = new int[]{-1944197, -10498574, -9623, -2399389, -1870160};
        starsColors = new int[]{-14778113, -15677815, -42601, -26844, -13639175};
        paint = new Paint[6];
        int i = 0;
        while (true) {
            Paint[] paintArr = paint;
            if (i >= paintArr.length) {
                return;
            }
            paintArr[i] = new Paint(1);
            paint[i].setColor(colors[i]);
            i++;
        }
    }

    public FireworksOverlay(Context context) {
        super(context);
        this.rect = new RectF();
        this.speedCoef = 1.0f;
        this.particles = new ArrayList(particlesCount + fallParticlesCount);
    }

    static int access$508(FireworksOverlay fireworksOverlay) {
        int i = fireworksOverlay.fallingDownCount;
        fireworksOverlay.fallingDownCount = i + 1;
        return i;
    }

    private Particle createParticle(boolean z) {
        SecureRandom secureRandom;
        int length;
        Particle particle = new Particle();
        byte nextInt = (byte) Utilities.random.nextInt(2);
        particle.type = nextInt;
        if (this.isFebruary14 && nextInt == 0) {
            particle.type = (byte) 2;
            secureRandom = Utilities.random;
            length = heartColors.length;
        } else if (this.withStars && Utilities.random.nextBoolean()) {
            particle.type = (byte) 2;
            secureRandom = Utilities.random;
            length = starsColors.length;
        } else {
            secureRandom = Utilities.random;
            length = colors.length;
        }
        particle.colorType = (byte) secureRandom.nextInt(length);
        particle.side = (byte) Utilities.random.nextInt(2);
        particle.finishedStart = (byte) (Utilities.random.nextInt(2) + 1);
        byte b = particle.type;
        particle.typeSize = (byte) (((b == 0 || b == 2) ? Utilities.random.nextFloat() * 2.0f : Utilities.random.nextFloat() * 4.0f) + 4.0f);
        if (z) {
            particle.y = (-Utilities.random.nextFloat()) * getHeightForAnimation() * 1.2f;
            particle.x = AndroidUtilities.dp(5.0f) + Utilities.random.nextInt(getWidthForAnimation() - AndroidUtilities.dp(10.0f));
            particle.xFinished = particle.finishedStart;
        } else {
            int dp = AndroidUtilities.dp(Utilities.random.nextInt(10) + 4);
            int heightForAnimation = getHeightForAnimation() / 4;
            particle.x = particle.side == 0 ? -dp : getWidthForAnimation() + dp;
            particle.moveX = (particle.side != 0 ? -1 : 1) * (AndroidUtilities.dp(1.2f) + (Utilities.random.nextFloat() * AndroidUtilities.dp(4.0f)));
            particle.moveY = -(AndroidUtilities.dp(4.0f) + (Utilities.random.nextFloat() * AndroidUtilities.dp(4.0f)));
            particle.y = (heightForAnimation / 2) + Utilities.random.nextInt(heightForAnimation * 2);
        }
        return particle;
    }

    public int getHeightForAnimation() {
        return getMeasuredHeight() == 0 ? ((View) getParent()).getHeight() : getMeasuredHeight();
    }

    private int getWidthForAnimation() {
        return getMeasuredWidth() == 0 ? ((View) getParent()).getWidth() : getMeasuredWidth();
    }

    public void lambda$onDraw$0() {
        if (this.started) {
            return;
        }
        setLayerType(0, null);
    }

    private void loadHeartDrawables() {
        if (heartDrawable != null) {
            return;
        }
        heartDrawable = new Drawable[heartColors.length];
        int i = 0;
        while (true) {
            Drawable[] drawableArr = heartDrawable;
            if (i >= drawableArr.length) {
                return;
            }
            drawableArr[i] = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.heart_confetti).mutate();
            heartDrawable[i].setColorFilter(new PorterDuffColorFilter(heartColors[i], PorterDuff.Mode.MULTIPLY));
            i++;
        }
    }

    private void loadStarsDrawables() {
        if (starsDrawable != null) {
            return;
        }
        starsDrawable = new Drawable[starsColors.length];
        int i = 0;
        while (true) {
            Drawable[] drawableArr = starsDrawable;
            if (i >= drawableArr.length) {
                return;
            }
            drawableArr[i] = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.msg_settings_premium).mutate();
            starsDrawable[i].setColorFilter(new PorterDuffColorFilter(starsColors[i], PorterDuff.Mode.MULTIPLY));
            i++;
        }
    }

    private void startFall() {
        if (this.startedFall) {
            return;
        }
        this.startedFall = true;
        for (int i = 0; i < fallParticlesCount; i++) {
            this.particles.add(createParticle(true));
        }
    }

    public boolean isStarted() {
        return this.started;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        int i = (int) (elapsedRealtime - this.lastUpdateTime);
        this.lastUpdateTime = elapsedRealtime;
        if (i > 18) {
            i = 16;
        }
        int size = this.particles.size();
        int i2 = 0;
        while (i2 < size) {
            Particle particle = (Particle) this.particles.get(i2);
            particle.draw(canvas);
            if (particle.update(i)) {
                this.particles.remove(i2);
                i2--;
                size--;
            }
            i2++;
        }
        if (this.fallingDownCount >= particlesCount / 2 && this.speedCoef > 0.2f) {
            startFall();
            float f = this.speedCoef - ((i / 16.0f) * 0.15f);
            this.speedCoef = f;
            if (f < 0.2f) {
                this.speedCoef = 0.2f;
            }
        }
        if (!this.particles.isEmpty()) {
            invalidate();
            return;
        }
        this.started = false;
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FireworksOverlay.this.lambda$onDraw$0();
            }
        });
        onStop();
    }

    public void onStop() {
    }

    public void start() {
        start(false);
    }

    public void start(boolean z) {
        this.withStars = z;
        setLayerType(2, null);
        boolean z2 = true;
        this.started = true;
        this.startedFall = false;
        this.fallingDownCount = 0;
        this.speedCoef = 1.0f;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        int i = calendar.get(5);
        if (calendar.get(2) != 1 || (!BuildVars.DEBUG_PRIVATE_VERSION && i != 14)) {
            z2 = false;
        }
        this.isFebruary14 = z2;
        if (z2) {
            loadHeartDrawables();
        } else if (z) {
            loadStarsDrawables();
        }
        int i2 = particlesCount;
        int clamp = Utilities.clamp(i2 - this.particles.size(), i2, i2 / 3);
        for (int i3 = 0; i3 < clamp; i3++) {
            this.particles.add(createParticle(false));
        }
        invalidate();
    }
}
