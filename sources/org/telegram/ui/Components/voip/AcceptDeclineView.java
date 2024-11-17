package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.widget.Button;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.voip.ImageWithWavesView;

public class AcceptDeclineView extends View {
    private Paint acceptCirclePaint;
    private FabBackgroundDrawable acceptDrawable;
    private StaticLayout acceptLayout;
    Rect acceptRect;
    private AcceptDeclineAccessibilityNodeProvider accessibilityNodeProvider;
    private final ImageWithWavesView.AvatarWavesDrawable avatarWavesDrawable;
    float bigRadius;
    private int buttonWidth;
    private RLottieDrawable callAcceptDrawable;
    private ValueAnimator callAnimator;
    private Drawable callDrawable;
    private Drawable cancelDrawable;
    boolean captured;
    private FabBackgroundDrawable declineDrawable;
    private StaticLayout declineLayout;
    Rect declineRect;
    boolean expandBigRadius;
    boolean expandSmallRadius;
    Animator leftAnimator;
    boolean leftDrag;
    float leftOffsetX;
    Paint linePaint;
    Listener listener;
    private final Paint maskPaint;
    float maxOffset;
    private StaticLayout retryLayout;
    boolean retryMod;
    Animator rightAnimator;
    float rigthOffsetX;
    Drawable rippleDrawable;
    float smallRadius;
    boolean startDrag;
    float startX;
    float startY;
    float touchSlop;

    public static abstract class AcceptDeclineAccessibilityNodeProvider extends AccessibilityNodeProvider {
        private final AccessibilityManager accessibilityManager;
        private final View hostView;
        private final int virtualViewsCount;
        private final Rect rect = new Rect();
        private int currentFocusedVirtualViewId = -1;

        protected AcceptDeclineAccessibilityNodeProvider(View view, int i) {
            this.hostView = view;
            this.virtualViewsCount = i;
            this.accessibilityManager = (AccessibilityManager) ContextCompat.getSystemService(view.getContext(), AccessibilityManager.class);
        }

        private void sendAccessibilityEventForVirtualView(int i, int i2) {
            ViewParent parent;
            if (!this.accessibilityManager.isTouchExplorationEnabled() || (parent = this.hostView.getParent()) == null) {
                return;
            }
            AccessibilityEvent obtain = AccessibilityEvent.obtain(i2);
            obtain.setPackageName(this.hostView.getContext().getPackageName());
            obtain.setSource(this.hostView, i);
            parent.requestSendAccessibilityEvent(this.hostView, obtain);
        }

        @Override
        public AccessibilityNodeInfo createAccessibilityNodeInfo(int i) {
            AccessibilityNodeInfo.AccessibilityAction accessibilityAction;
            if (i == -1) {
                AccessibilityNodeInfo obtain = AccessibilityNodeInfo.obtain(this.hostView);
                obtain.setPackageName(this.hostView.getContext().getPackageName());
                for (int i2 = 0; i2 < this.virtualViewsCount; i2++) {
                    obtain.addChild(this.hostView, i2);
                }
                return obtain;
            }
            AccessibilityNodeInfo obtain2 = AccessibilityNodeInfo.obtain(this.hostView, i);
            obtain2.setPackageName(this.hostView.getContext().getPackageName());
            int i3 = Build.VERSION.SDK_INT;
            if (i3 >= 21) {
                accessibilityAction = AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK;
                obtain2.addAction(accessibilityAction);
            }
            obtain2.setText(getVirtualViewText(i));
            obtain2.setClassName(Button.class.getName());
            if (i3 >= 24) {
                obtain2.setImportantForAccessibility(true);
            }
            obtain2.setVisibleToUser(true);
            obtain2.setClickable(true);
            obtain2.setEnabled(true);
            obtain2.setParent(this.hostView);
            getVirtualViewBoundsInScreen(i, this.rect);
            obtain2.setBoundsInScreen(this.rect);
            return obtain2;
        }

        protected abstract void getVirtualViewBoundsInParent(int i, Rect rect);

        protected abstract void getVirtualViewBoundsInScreen(int i, Rect rect);

        protected abstract CharSequence getVirtualViewText(int i);

        public boolean onHoverEvent(MotionEvent motionEvent) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (motionEvent.getAction() == 9 || motionEvent.getAction() == 7) {
                for (int i = 0; i < this.virtualViewsCount; i++) {
                    getVirtualViewBoundsInParent(i, this.rect);
                    if (this.rect.contains(x, y)) {
                        if (i != this.currentFocusedVirtualViewId) {
                            this.currentFocusedVirtualViewId = i;
                            sendAccessibilityEventForVirtualView(i, 32768);
                        }
                        return true;
                    }
                }
            } else if (motionEvent.getAction() == 10 && this.currentFocusedVirtualViewId != -1) {
                this.currentFocusedVirtualViewId = -1;
                return true;
            }
            return false;
        }

        protected abstract void onVirtualViewClick(int i);

        @Override
        public boolean performAction(int i, int i2, Bundle bundle) {
            if (i == -1) {
                return this.hostView.performAccessibilityAction(i2, bundle);
            }
            if (i2 == 64) {
                sendAccessibilityEventForVirtualView(i, 32768);
                return false;
            }
            if (i2 != 16) {
                return false;
            }
            onVirtualViewClick(i);
            return true;
        }
    }

    public interface Listener {
        void onAccept();

        void onDecline();
    }

    public AcceptDeclineView(Context context) {
        super(context);
        this.acceptCirclePaint = new Paint(1);
        this.expandSmallRadius = true;
        this.expandBigRadius = true;
        this.acceptRect = new Rect();
        this.declineRect = new Rect();
        this.linePaint = new Paint(1);
        Paint paint = new Paint(1);
        this.maskPaint = paint;
        ImageWithWavesView.AvatarWavesDrawable avatarWavesDrawable = new ImageWithWavesView.AvatarWavesDrawable(AndroidUtilities.dp(45.0f), AndroidUtilities.dp(50.0f), AndroidUtilities.dp(8.0f), 4);
        this.avatarWavesDrawable = avatarWavesDrawable;
        avatarWavesDrawable.muteToStatic = true;
        avatarWavesDrawable.muteToStaticProgress = 0.0f;
        avatarWavesDrawable.wavesEnter = 0.0f;
        avatarWavesDrawable.setAmplitude(0.0d);
        paint.setColor(-16777216);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.buttonWidth = AndroidUtilities.dp(60.0f);
        FabBackgroundDrawable fabBackgroundDrawable = new FabBackgroundDrawable();
        this.acceptDrawable = fabBackgroundDrawable;
        fabBackgroundDrawable.setColor(-12531895);
        FabBackgroundDrawable fabBackgroundDrawable2 = new FabBackgroundDrawable();
        this.declineDrawable = fabBackgroundDrawable2;
        fabBackgroundDrawable2.setColor(-1041108);
        FabBackgroundDrawable fabBackgroundDrawable3 = this.declineDrawable;
        int i = this.buttonWidth;
        fabBackgroundDrawable3.setBounds(0, 0, i, i);
        FabBackgroundDrawable fabBackgroundDrawable4 = this.acceptDrawable;
        int i2 = this.buttonWidth;
        fabBackgroundDrawable4.setBounds(0, 0, i2, i2);
        TextPaint textPaint = new TextPaint(1);
        textPaint.setTextSize(AndroidUtilities.dp(11.0f));
        textPaint.setColor(-1);
        String string = LocaleController.getString(R.string.AcceptCall);
        String string2 = LocaleController.getString(R.string.DeclineCall);
        String string3 = LocaleController.getString(R.string.RetryCall);
        int measureText = (int) textPaint.measureText(string);
        Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
        this.acceptLayout = new StaticLayout(string, textPaint, measureText, alignment, 1.0f, 0.0f, false);
        this.declineLayout = new StaticLayout(string2, textPaint, (int) textPaint.measureText(string2), alignment, 1.0f, 0.0f, false);
        this.retryLayout = new StaticLayout(string3, textPaint, (int) textPaint.measureText(string3), alignment, 1.0f, 0.0f, false);
        this.callDrawable = ContextCompat.getDrawable(context, R.drawable.calls_decline).mutate();
        Drawable mutate = ContextCompat.getDrawable(context, R.drawable.ic_close_white).mutate();
        this.cancelDrawable = mutate;
        mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        int i3 = R.raw.call_accept;
        RLottieDrawable rLottieDrawable = new RLottieDrawable(i3, "" + i3, AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f), true, null);
        this.callAcceptDrawable = rLottieDrawable;
        rLottieDrawable.setAutoRepeat(1);
        this.callAcceptDrawable.setCustomEndFrame(90);
        this.callAcceptDrawable.setMasterParent(this);
        this.acceptCirclePaint.setColor(-1);
        this.acceptCirclePaint.setAlpha(20);
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(52.0f), 0, ColorUtils.setAlphaComponent(-1, 76));
        this.rippleDrawable = createSimpleSelectorCircleDrawable;
        createSimpleSelectorCircleDrawable.setCallback(this);
    }

    public void lambda$onTouchEvent$0(ValueAnimator valueAnimator) {
        this.leftOffsetX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        this.leftAnimator = null;
    }

    public void lambda$onTouchEvent$1(ValueAnimator valueAnimator) {
        this.rigthOffsetX = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        this.rightAnimator = null;
    }

    public void lambda$setRetryMod$2(ValueAnimator valueAnimator) {
        this.avatarWavesDrawable.setAmplitude(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.rippleDrawable.setState(getDrawableState());
    }

    @Override
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        if (this.accessibilityNodeProvider == null) {
            this.accessibilityNodeProvider = new AcceptDeclineAccessibilityNodeProvider(this, 2) {
                private final int[] coords = {0, 0};

                @Override
                protected void getVirtualViewBoundsInParent(int i, Rect rect) {
                    Rect rect2;
                    if (i == 0) {
                        rect2 = AcceptDeclineView.this.acceptRect;
                    } else {
                        if (i != 1) {
                            rect.setEmpty();
                            return;
                        }
                        rect2 = AcceptDeclineView.this.declineRect;
                    }
                    rect.set(rect2);
                }

                @Override
                protected void getVirtualViewBoundsInScreen(int i, Rect rect) {
                    getVirtualViewBoundsInParent(i, rect);
                    AcceptDeclineView.this.getLocationOnScreen(this.coords);
                    int[] iArr = this.coords;
                    rect.offset(iArr[0], iArr[1]);
                }

                @Override
                protected CharSequence getVirtualViewText(int i) {
                    StaticLayout staticLayout;
                    if (i == 0) {
                        AcceptDeclineView acceptDeclineView = AcceptDeclineView.this;
                        if (acceptDeclineView.retryMod) {
                            if (acceptDeclineView.retryLayout == null) {
                                return null;
                            }
                            staticLayout = AcceptDeclineView.this.retryLayout;
                        } else {
                            if (acceptDeclineView.acceptLayout == null) {
                                return null;
                            }
                            staticLayout = AcceptDeclineView.this.acceptLayout;
                        }
                    } else {
                        if (i != 1 || AcceptDeclineView.this.declineLayout == null) {
                            return null;
                        }
                        staticLayout = AcceptDeclineView.this.declineLayout;
                    }
                    return staticLayout.getText();
                }

                @Override
                protected void onVirtualViewClick(int i) {
                    Listener listener = AcceptDeclineView.this.listener;
                    if (listener != null) {
                        if (i == 0) {
                            listener.onAccept();
                        } else if (i == 1) {
                            listener.onDecline();
                        }
                    }
                }
            };
        }
        return this.accessibilityNodeProvider;
    }

    @Override
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.rippleDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimations();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        StaticLayout staticLayout;
        if (!this.retryMod) {
            if (this.expandSmallRadius) {
                float dp = this.smallRadius + (AndroidUtilities.dp(2.0f) * 0.04f);
                this.smallRadius = dp;
                if (dp > AndroidUtilities.dp(4.0f)) {
                    this.smallRadius = AndroidUtilities.dp(4.0f);
                    this.expandSmallRadius = false;
                }
            } else {
                float dp2 = this.smallRadius - (AndroidUtilities.dp(2.0f) * 0.04f);
                this.smallRadius = dp2;
                if (dp2 < 0.0f) {
                    this.smallRadius = 0.0f;
                    this.expandSmallRadius = true;
                }
            }
            if (this.expandBigRadius) {
                float dp3 = this.bigRadius + (AndroidUtilities.dp(4.0f) * 0.03f);
                this.bigRadius = dp3;
                if (dp3 > AndroidUtilities.dp(10.0f)) {
                    this.bigRadius = AndroidUtilities.dp(10.0f);
                    this.expandBigRadius = false;
                }
            } else {
                float dp4 = this.bigRadius - (AndroidUtilities.dp(5.0f) * 0.03f);
                this.bigRadius = dp4;
                if (dp4 < AndroidUtilities.dp(5.0f)) {
                    this.bigRadius = AndroidUtilities.dp(5.0f);
                    this.expandBigRadius = true;
                }
            }
            invalidate();
        }
        this.bigRadius += AndroidUtilities.dp(8.0f) * 0.005f;
        canvas.save();
        canvas.translate(0.0f, AndroidUtilities.dp(40.0f));
        canvas.save();
        canvas.translate(((this.rigthOffsetX + getMeasuredWidth()) - AndroidUtilities.dp(46.0f)) - this.buttonWidth, 0.0f);
        if (this.retryMod) {
            canvas.saveLayer(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.linePaint, 31);
            this.declineDrawable.draw(canvas);
            Drawable drawable = this.cancelDrawable;
            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if (bitmapDrawable.getBitmap() != null) {
                    canvas.drawBitmap(bitmapDrawable.getBitmap(), (Rect) null, bitmapDrawable.getBounds(), this.maskPaint);
                }
            }
            canvas.restore();
        } else {
            this.declineDrawable.draw(canvas);
            this.callDrawable.draw(canvas);
        }
        canvas.save();
        canvas.translate((this.buttonWidth / 2.0f) - (this.declineLayout.getWidth() / 2.0f), this.buttonWidth + AndroidUtilities.dp(4.0f));
        this.declineLayout.draw(canvas);
        this.declineRect.set((getMeasuredWidth() - AndroidUtilities.dp(46.0f)) - this.buttonWidth, AndroidUtilities.dp(40.0f), getMeasuredWidth() - AndroidUtilities.dp(46.0f), AndroidUtilities.dp(40.0f) + this.buttonWidth);
        canvas.restore();
        if (this.leftDrag) {
            this.rippleDrawable.setBounds(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f));
            this.rippleDrawable.draw(canvas);
        }
        canvas.restore();
        canvas.save();
        canvas.translate(this.leftOffsetX + AndroidUtilities.dp(46.0f), 0.0f);
        if (!this.retryMod) {
            this.avatarWavesDrawable.update();
            float f = (int) (this.buttonWidth / 2.0f);
            this.avatarWavesDrawable.draw(canvas, f, f, this);
        }
        this.acceptDrawable.draw(canvas);
        this.acceptRect.set(AndroidUtilities.dp(46.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(46.0f) + this.buttonWidth, AndroidUtilities.dp(40.0f) + this.buttonWidth);
        boolean z = this.retryMod;
        canvas.save();
        if (z) {
            canvas.translate((this.buttonWidth / 2.0f) - (this.retryLayout.getWidth() / 2.0f), this.buttonWidth + AndroidUtilities.dp(4.0f));
            staticLayout = this.retryLayout;
        } else {
            canvas.translate((this.buttonWidth / 2.0f) - (this.acceptLayout.getWidth() / 2.0f), this.buttonWidth + AndroidUtilities.dp(4.0f));
            staticLayout = this.acceptLayout;
        }
        staticLayout.draw(canvas);
        canvas.restore();
        canvas.save();
        canvas.translate(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
        this.callAcceptDrawable.draw(canvas);
        canvas.restore();
        if (!this.leftDrag) {
            this.rippleDrawable.setBounds(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f), this.buttonWidth - AndroidUtilities.dp(4.0f));
            this.rippleDrawable.draw(canvas);
        }
        canvas.restore();
        canvas.restore();
        if (this.captured) {
            invalidate();
        }
    }

    @Override
    public boolean onHoverEvent(MotionEvent motionEvent) {
        AcceptDeclineAccessibilityNodeProvider acceptDeclineAccessibilityNodeProvider = this.accessibilityNodeProvider;
        if (acceptDeclineAccessibilityNodeProvider == null || !acceptDeclineAccessibilityNodeProvider.onHoverEvent(motionEvent)) {
            return super.onHoverEvent(motionEvent);
        }
        return true;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.maxOffset = (getMeasuredWidth() / 2.0f) - ((this.buttonWidth / 2.0f) + AndroidUtilities.dp(46.0f));
        int dp = (this.buttonWidth - AndroidUtilities.dp(28.0f)) / 2;
        this.callDrawable.setBounds(dp, dp, AndroidUtilities.dp(28.0f) + dp, AndroidUtilities.dp(28.0f) + dp);
        this.cancelDrawable.setBounds(dp, dp, AndroidUtilities.dp(28.0f) + dp, AndroidUtilities.dp(28.0f) + dp);
        this.linePaint.setStrokeWidth(AndroidUtilities.dp(3.0f));
        this.linePaint.setColor(-1);
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.AcceptDeclineView.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public void setRetryMod(boolean z) {
        this.retryMod = z;
        if (z) {
            this.declineDrawable.setColor(-1);
            return;
        }
        this.callAcceptDrawable.start();
        this.avatarWavesDrawable.setShowWaves(true, this);
        this.declineDrawable.setColor(-1041108);
        ValueAnimator ofInt = ValueAnimator.ofInt(0, 60, 0, 0, 60, 0, 0, 0, 0);
        this.callAnimator = ofInt;
        ofInt.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                AcceptDeclineView.this.lambda$setRetryMod$2(valueAnimator);
            }
        });
        this.callAnimator.setDuration(1500L);
        this.callAnimator.setRepeatMode(1);
        this.callAnimator.setRepeatCount(-1);
        this.callAnimator.start();
    }

    public void stopAnimations() {
        ValueAnimator valueAnimator = this.callAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.callAnimator = null;
            this.callAcceptDrawable.stop();
        }
    }

    @Override
    public boolean verifyDrawable(Drawable drawable) {
        return this.rippleDrawable == drawable || super.verifyDrawable(drawable);
    }
}
