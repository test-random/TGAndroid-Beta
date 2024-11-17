package org.telegram.ui.bots;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.voip.CellFlickerDrawable;

public abstract class BotButtons extends FrameLayout {
    public final AnimatedColor background;
    private final Paint backgroundPaint;
    public final Button[] buttons;
    public final AnimatedFloat height;
    private Button pressedButton;
    private final Paint separatorPaint;
    public ButtonsState state;
    private Utilities.Callback whenClicked;
    private Runnable whenResized;

    public class Button {
        public final AnimatedFloat alpha;
        public final AnimatedColor backgroundColor;
        public final Paint backgroundPaint;
        public final ButtonBounce bounce;
        public final RectF bounds;
        public final CellFlickerDrawable flicker;
        public final AnimatedFloat flickerAlpha;
        public final CircularProgressDrawable progress;
        public final AnimatedFloat progressAlpha;
        public final Drawable ripple;
        public int rippleColor;
        public final AnimatedColor textColor;
        public final AnimatedTextView.AnimatedTextDrawable textDrawable;
        public final AnimatedFloat w;
        public final AnimatedFloat x;
        public final AnimatedFloat y;

        private Button() {
            this.bounds = new RectF();
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.alpha = new AnimatedFloat(BotButtons.this, 0L, 320L, cubicBezierInterpolator);
            this.x = new AnimatedFloat(BotButtons.this, 0L, 320L, cubicBezierInterpolator);
            this.y = new AnimatedFloat(BotButtons.this, 0L, 320L, cubicBezierInterpolator);
            this.w = new AnimatedFloat(BotButtons.this, 0L, 320L, cubicBezierInterpolator);
            this.backgroundColor = new AnimatedColor(BotButtons.this, 0L, 320L, cubicBezierInterpolator);
            this.textColor = new AnimatedColor(BotButtons.this, 0L, 320L, cubicBezierInterpolator);
            this.progressAlpha = new AnimatedFloat(BotButtons.this, 0L, 320L, cubicBezierInterpolator);
            this.flickerAlpha = new AnimatedFloat(BotButtons.this, 0L, 320L, cubicBezierInterpolator);
            this.bounce = new ButtonBounce(BotButtons.this);
            this.backgroundPaint = new Paint(1);
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(true, true, true);
            this.textDrawable = animatedTextDrawable;
            Drawable createRadSelectorDrawable = Theme.createRadSelectorDrawable(0, 9, 9);
            this.ripple = createRadSelectorDrawable;
            CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable();
            this.progress = circularProgressDrawable;
            CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
            this.flicker = cellFlickerDrawable;
            animatedTextDrawable.setGravity(17);
            animatedTextDrawable.setTextSize(AndroidUtilities.dp(14.0f));
            animatedTextDrawable.setTypeface(AndroidUtilities.bold());
            animatedTextDrawable.setOverrideFullWidth(AndroidUtilities.displaySize.x * 4);
            animatedTextDrawable.setEllipsizeByGradient(true);
            circularProgressDrawable.setCallback(BotButtons.this);
            createRadSelectorDrawable.setCallback(BotButtons.this);
            cellFlickerDrawable.frameInside = true;
            cellFlickerDrawable.repeatProgress = 2.0f;
        }
    }

    public static class ButtonState {
        public boolean active;
        public int color;
        public String position;
        public boolean progressVisible;
        public boolean shineEffect;
        public String text;
        public int textColor;
        public boolean visible;

        public static ButtonState of(boolean z, boolean z2, boolean z3, boolean z4, String str, int i, int i2) {
            return of(z, z2, z3, z4, str, i, i2, null);
        }

        public static ButtonState of(boolean z, boolean z2, boolean z3, boolean z4, String str, int i, int i2, String str2) {
            ButtonState buttonState = new ButtonState();
            buttonState.visible = z;
            buttonState.active = z2;
            buttonState.progressVisible = z3;
            buttonState.shineEffect = z4;
            buttonState.text = str;
            buttonState.color = i;
            buttonState.textColor = i2;
            buttonState.position = str2;
            return buttonState;
        }
    }

    public static class ButtonsState {
        public int backgroundColor;
        public ButtonState main = new ButtonState();
        public ButtonState secondary = new ButtonState();
    }

    public BotButtons(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        Paint paint = new Paint(1);
        this.backgroundPaint = paint;
        Paint paint2 = new Paint(1);
        this.separatorPaint = paint2;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.height = new AnimatedFloat(this, 0L, 320L, cubicBezierInterpolator);
        this.background = new AnimatedColor(this, 0L, 320L, cubicBezierInterpolator);
        this.state = new ButtonsState();
        this.buttons = r2;
        setWillNotDraw(false);
        paint2.setColor(Theme.multAlpha(-16777216, 0.1f));
        ButtonsState buttonsState = this.state;
        int color = Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider);
        buttonsState.backgroundColor = color;
        paint.setColor(color);
        Button[] buttonArr = {new Button(), new Button()};
    }

    private Button getHitButton(float f, float f2) {
        int i = 0;
        while (true) {
            Button[] buttonArr = this.buttons;
            if (i >= buttonArr.length) {
                return null;
            }
            ButtonsState buttonsState = this.state;
            ButtonState buttonState = i == 0 ? buttonsState.main : buttonsState.secondary;
            if (buttonArr[i].bounds.contains(f, f2) && buttonState.visible && buttonState.active) {
                return this.buttons[i];
            }
            i++;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        float f;
        float f2;
        float f3;
        float f4;
        float f5;
        float height = getHeight() - this.height.set(getTotalHeight());
        canvas.drawRect(0.0f, height - 1.0f, getWidth(), height, this.separatorPaint);
        this.backgroundPaint.setColor(this.background.set(this.state.backgroundColor));
        canvas.drawRect(0.0f, height, getWidth(), getHeight(), this.backgroundPaint);
        String str = this.state.secondary.position;
        int i = 1;
        int i2 = this.buttons[1].alpha.get() < this.buttons[0].alpha.get() ? 1 : 0;
        int i3 = i2;
        while (true) {
            if (i2 != 0) {
                if (i3 < 0) {
                    return;
                }
            } else if (i3 > i) {
                return;
            }
            Button button = this.buttons[i3];
            ButtonsState buttonsState = this.state;
            ButtonState buttonState = i3 == 0 ? buttonsState.main : buttonsState.secondary;
            float f6 = button.alpha.set(buttonState.visible);
            if (buttonState.visible) {
                AnimatedFloat animatedFloat = button.x;
                ButtonsState buttonsState2 = this.state;
                if (buttonsState2.secondary.visible && buttonsState2.main.visible) {
                    f = (!"left".equalsIgnoreCase(str) ? !(!"right".equalsIgnoreCase(str) || i3 == 0) : i3 == 0) ? 0 : 1;
                } else {
                    f = 0.0f;
                }
                f2 = animatedFloat.set(f);
            } else {
                f2 = button.x.get();
            }
            if (buttonState.visible) {
                AnimatedFloat animatedFloat2 = button.y;
                ButtonsState buttonsState3 = this.state;
                if (buttonsState3.secondary.visible && buttonsState3.main.visible) {
                    f3 = (!"top".equalsIgnoreCase(str) ? !(!"bottom".equalsIgnoreCase(str) || i3 == 0) : i3 == 0) ? 0 : 1;
                } else {
                    f3 = 0.0f;
                }
                f4 = animatedFloat2.set(f3);
            } else {
                f4 = button.y.get();
            }
            if (buttonState.visible) {
                AnimatedFloat animatedFloat3 = button.w;
                ButtonsState buttonsState4 = this.state;
                f5 = animatedFloat3.set((buttonsState4.secondary.visible && buttonsState4.main.visible && ("left".equalsIgnoreCase(str) || "right".equalsIgnoreCase(str))) ? 0.0f : 1.0f);
            } else {
                f5 = button.w.get();
            }
            float lerp = AndroidUtilities.lerp((getWidth() - AndroidUtilities.dp(26.0f)) / 2.0f, getWidth() - AndroidUtilities.dp(16.0f), f5);
            float dp = AndroidUtilities.dp(44.0f);
            float f7 = lerp / 2.0f;
            float lerp2 = AndroidUtilities.lerp(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(18.0f) + ((getWidth() - AndroidUtilities.dp(26.0f)) / 2.0f), f2) + f7;
            float f8 = dp / 2.0f;
            float lerp3 = AndroidUtilities.lerp(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(58.0f), f4) + f8 + height;
            button.bounds.set(lerp2 - f7, lerp3 - f8, f7 + lerp2, f8 + lerp3);
            float f9 = button.progressAlpha.set(buttonState.progressVisible);
            float f10 = button.flickerAlpha.set(buttonState.shineEffect);
            canvas.save();
            float scale = button.bounce.getScale(0.02f) * AndroidUtilities.lerp(0.7f, 1.0f, f6);
            canvas.scale(scale, scale, lerp2, lerp3);
            button.backgroundPaint.setColor(Theme.multAlpha(button.backgroundColor.set(buttonState.color), f6));
            String str2 = str;
            canvas.drawRoundRect(button.bounds, AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), button.backgroundPaint);
            if (f9 < 1.0f) {
                canvas.save();
                float f11 = 1.0f - f9;
                float lerp4 = AndroidUtilities.lerp(0.75f, 1.0f, f11);
                canvas.scale(lerp4, lerp4, lerp2, lerp3);
                canvas.translate(0.0f, AndroidUtilities.dp(-10.0f) * f9);
                button.textDrawable.setTextColor(Theme.multAlpha(button.textColor.set(buttonState.textColor), f11 * f6));
                button.textDrawable.setBounds(button.bounds);
                button.textDrawable.draw(canvas);
                canvas.restore();
            }
            float f12 = 0.0f;
            if (f9 > 0.0f) {
                canvas.save();
                float lerp5 = AndroidUtilities.lerp(0.75f, 1.0f, f9);
                canvas.scale(lerp5, lerp5, lerp2, lerp3);
                canvas.translate(0.0f, AndroidUtilities.dp(10.0f) * (1.0f - f9));
                button.progress.setColor(Theme.multAlpha(button.textColor.set(buttonState.textColor), f9 * f6));
                CircularProgressDrawable circularProgressDrawable = button.progress;
                RectF rectF = button.bounds;
                circularProgressDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                button.progress.draw(canvas);
                canvas.restore();
                f12 = 0.0f;
            }
            if (f10 > f12) {
                button.flicker.setColors(Theme.multAlpha(button.textColor.set(buttonState.textColor), f6 * f10));
                button.flicker.draw(canvas, button.bounds, AndroidUtilities.dp(8.0f), this);
            }
            if (button.rippleColor != Theme.multAlpha(buttonState.textColor, 0.15f)) {
                Drawable drawable = button.ripple;
                int multAlpha = Theme.multAlpha(buttonState.textColor, 0.15f);
                button.rippleColor = multAlpha;
                Theme.setSelectorDrawableColor(drawable, multAlpha, true);
            }
            Drawable drawable2 = button.ripple;
            RectF rectF2 = button.bounds;
            drawable2.setBounds((int) rectF2.left, (int) rectF2.top, (int) rectF2.right, (int) rectF2.bottom);
            button.ripple.draw(canvas);
            canvas.restore();
            i3 += i2 != 0 ? -1 : 1;
            str = str2;
            i = 1;
        }
    }

    public float getAnimatedTotalHeight() {
        return this.height.get();
    }

    public int getTotalHeight() {
        ButtonsState buttonsState = this.state;
        boolean z = buttonsState.main.visible;
        int i = (z || buttonsState.secondary.visible) ? 1 : 0;
        if (z) {
            ButtonState buttonState = buttonsState.secondary;
            if (buttonState.visible && ("top".equalsIgnoreCase(buttonState.position) || "bottom".equalsIgnoreCase(this.state.secondary.position))) {
                i++;
            }
        }
        if (i == 0) {
            return 0;
        }
        return AndroidUtilities.dp(i == 1 ? 58.0f : 109.0f);
    }

    @Override
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(109.0f) + 1, 1073741824));
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Utilities.Callback callback;
        if (motionEvent.getAction() == 0) {
            Button hitButton = getHitButton(motionEvent.getX(), motionEvent.getY());
            this.pressedButton = hitButton;
            if (hitButton != null) {
                hitButton.bounce.setPressed(true);
                if (Build.VERSION.SDK_INT >= 21) {
                    this.pressedButton.ripple.setHotspot(motionEvent.getX(), motionEvent.getY());
                }
                this.pressedButton.ripple.setState(new int[]{16842919, 16842910});
            }
        } else if ((motionEvent.getAction() == 1 || motionEvent.getAction() == 3) && this.pressedButton != null) {
            if (motionEvent.getAction() == 1) {
                Button hitButton2 = getHitButton(motionEvent.getX(), motionEvent.getY());
                Button button = this.pressedButton;
                if (hitButton2 == button && (callback = this.whenClicked) != null) {
                    callback.run(Boolean.valueOf(button == this.buttons[0]));
                }
            }
            this.pressedButton.bounce.setPressed(false);
            this.pressedButton.ripple.setState(new int[0]);
            this.pressedButton = null;
        }
        return this.pressedButton != null;
    }

    public void setBackgroundColor(int i, boolean z) {
        Paint paint = this.backgroundPaint;
        this.state.backgroundColor = i;
        paint.setColor(i);
        if (z) {
            return;
        }
        this.background.set(i, true);
    }

    public void setMainState(ButtonState buttonState, boolean z) {
        int totalHeight = getTotalHeight();
        this.state.main = buttonState;
        this.buttons[0].textDrawable.cancelAnimation();
        this.buttons[0].textDrawable.setText(buttonState.text, z);
        invalidate();
        if (totalHeight == getTotalHeight() || this.whenResized == null) {
            return;
        }
        if (totalHeight < getTotalHeight()) {
            AndroidUtilities.runOnUIThread(this.whenResized, 200L);
        } else {
            this.whenResized.run();
        }
    }

    public void setOnButtonClickListener(Utilities.Callback<Boolean> callback) {
        this.whenClicked = callback;
    }

    public void setOnResizeListener(Runnable runnable) {
        this.whenResized = runnable;
    }

    public void setSecondaryState(ButtonState buttonState, boolean z) {
        int totalHeight = getTotalHeight();
        this.state.secondary = buttonState;
        this.buttons[1].textDrawable.cancelAnimation();
        this.buttons[1].textDrawable.setText(buttonState.text, z);
        invalidate();
        if (totalHeight == getTotalHeight() || this.whenResized == null) {
            return;
        }
        if (totalHeight < getTotalHeight()) {
            AndroidUtilities.runOnUIThread(this.whenResized, 200L);
        } else {
            this.whenResized.run();
        }
    }

    public void setState(ButtonsState buttonsState, boolean z) {
        int totalHeight = getTotalHeight();
        this.state = buttonsState;
        this.buttons[0].textDrawable.cancelAnimation();
        this.buttons[0].textDrawable.setText(buttonsState.main.text, z);
        this.buttons[1].textDrawable.cancelAnimation();
        this.buttons[1].textDrawable.setText(buttonsState.secondary.text, z);
        invalidate();
        if (totalHeight != getTotalHeight() && this.whenResized != null) {
            if (totalHeight < getTotalHeight()) {
                AndroidUtilities.runOnUIThread(this.whenResized, 200L);
            } else {
                this.whenResized.run();
            }
        }
        setBackgroundColor(buttonsState.backgroundColor, z);
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        Button[] buttonArr = this.buttons;
        Button button = buttonArr[0];
        if (button.ripple != drawable && button.progress != drawable) {
            Button button2 = buttonArr[1];
            if (button2.ripple != drawable && button2.progress != drawable && !super.verifyDrawable(drawable)) {
                return false;
            }
        }
        return true;
    }
}
