package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.SystemClock;
import android.util.Property;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.voip.RTMPStreamPipOverlay;
import org.telegram.ui.GroupCallActivity;

public class GroupCallPip implements NotificationCenter.NotificationCenterDelegate {
    private static boolean forceRemoved = true;
    private static GroupCallPip instance;
    FrameLayout alertContainer;
    boolean animateToPrepareRemove;
    boolean animateToShowRemoveTooltip;
    AvatarsImageView avatarsImageView;
    private final GroupCallPipButton button;
    boolean buttonInAlpha;
    int currentAccount;
    RLottieDrawable deleteIcon;
    private final RLottieImageView iconView;
    int lastScreenX;
    int lastScreenY;
    boolean moving;
    ValueAnimator pinAnimator;
    GroupCallPipAlertView pipAlertView;
    boolean pressedState;
    View removeTooltipView;
    boolean removed;
    boolean showAlert;
    AnimatorSet showRemoveAnimator;
    WindowManager.LayoutParams windowLayoutParams;
    int windowLeft;
    WindowManager windowManager;
    float windowOffsetLeft;
    float windowOffsetTop;
    FrameLayout windowRemoveTooltipOverlayView;
    FrameLayout windowRemoveTooltipView;
    int windowTop;
    FrameLayout windowView;
    float windowX;
    float windowY;
    float prepareToRemoveProgress = 0.0f;
    int[] location = new int[2];
    float[] point = new float[2];
    float xRelative = -1.0f;
    float yRelative = -1.0f;
    private ValueAnimator.AnimatorUpdateListener updateXlistener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            GroupCallPip groupCallPip = GroupCallPip.this;
            groupCallPip.windowLayoutParams.x = (int) floatValue;
            groupCallPip.updateAvatarsPosition();
            if (GroupCallPip.this.windowView.getParent() != null) {
                GroupCallPip groupCallPip2 = GroupCallPip.this;
                groupCallPip2.windowManager.updateViewLayout(groupCallPip2.windowView, groupCallPip2.windowLayoutParams);
            }
        }
    };
    private ValueAnimator.AnimatorUpdateListener updateYlistener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            GroupCallPip groupCallPip = GroupCallPip.this;
            groupCallPip.windowLayoutParams.y = (int) floatValue;
            if (groupCallPip.windowView.getParent() != null) {
                GroupCallPip groupCallPip2 = GroupCallPip.this;
                groupCallPip2.windowManager.updateViewLayout(groupCallPip2.windowView, groupCallPip2.windowLayoutParams);
            }
        }
    };
    boolean animateToPinnedToCenter = false;
    float pinnedProgress = 0.0f;

    class AnonymousClass3 extends FrameLayout {
        Runnable micRunnable;
        AnimatorSet moveToBoundsAnimator;
        boolean pressed;
        Runnable pressedRunnable;
        long startTime;
        float startX;
        float startY;
        final float val$touchSlop;

        AnonymousClass3(Context context, float f) {
            super(context);
            this.val$touchSlop = f;
            this.pressedRunnable = new Runnable() {
                @Override
                public void run() {
                    VoIPService sharedInstance = VoIPService.getSharedInstance();
                    if (sharedInstance == null || !sharedInstance.isMicMute()) {
                        return;
                    }
                    TLRPC.TL_groupCallParticipant tL_groupCallParticipant = (TLRPC.TL_groupCallParticipant) sharedInstance.groupCall.participants.get(sharedInstance.getSelfId());
                    if (tL_groupCallParticipant == null || tL_groupCallParticipant.can_self_unmute || !tL_groupCallParticipant.muted || ChatObject.canManageCalls(sharedInstance.getChat())) {
                        AndroidUtilities.runOnUIThread(AnonymousClass3.this.micRunnable, 90L);
                        try {
                            AnonymousClass3.this.performHapticFeedback(3, 2);
                        } catch (Exception unused) {
                        }
                        AnonymousClass3.this.pressed = true;
                    }
                }
            };
            this.micRunnable = new Runnable() {
                @Override
                public final void run() {
                    GroupCallPip.AnonymousClass3.lambda$$0();
                }
            };
        }

        public static void lambda$$0() {
            if (VoIPService.getSharedInstance() == null || !VoIPService.getSharedInstance().isMicMute()) {
                return;
            }
            VoIPService.getSharedInstance().setMicMute(false, true, false);
        }

        private void onTap() {
            if (VoIPService.getSharedInstance() != null) {
                GroupCallPip.this.showAlert(!r0.showAlert);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            android.graphics.Point point = AndroidUtilities.displaySize;
            int i3 = point.x;
            GroupCallPip groupCallPip = GroupCallPip.this;
            if (i3 == groupCallPip.lastScreenX && groupCallPip.lastScreenY == point.y) {
                return;
            }
            groupCallPip.lastScreenX = i3;
            groupCallPip.lastScreenY = point.y;
            if (groupCallPip.xRelative < 0.0f) {
                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("groupcallpipconfig", 0);
                GroupCallPip.this.xRelative = sharedPreferences.getFloat("relativeX", 1.0f);
                GroupCallPip.this.yRelative = sharedPreferences.getFloat("relativeY", 0.4f);
            }
            if (GroupCallPip.instance != null) {
                GroupCallPip groupCallPip2 = GroupCallPip.instance;
                GroupCallPip groupCallPip3 = GroupCallPip.this;
                groupCallPip2.setPosition(groupCallPip3.xRelative, groupCallPip3.yRelative);
            }
        }

        @Override
        public boolean onTouchEvent(android.view.MotionEvent r13) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCallPip.AnonymousClass3.onTouchEvent(android.view.MotionEvent):boolean");
        }
    }

    public class AnonymousClass9 extends AnimatorListenerAdapter {
        final View val$alert;
        final WindowManager val$windowManager;
        final View val$windowRemoveTooltipOverlayView;
        final View val$windowRemoveTooltipView;
        final View val$windowView;

        AnonymousClass9(View view, View view2, WindowManager windowManager, View view3, View view4) {
            this.val$windowView = view;
            this.val$windowRemoveTooltipView = view2;
            this.val$windowManager = windowManager;
            this.val$windowRemoveTooltipOverlayView = view3;
            this.val$alert = view4;
        }

        public static void lambda$onAnimationEnd$0(View view, View view2, WindowManager windowManager, View view3, View view4) {
            view.setVisibility(8);
            view2.setVisibility(8);
            windowManager.removeView(view);
            windowManager.removeView(view2);
            windowManager.removeView(view3);
            windowManager.removeView(view4);
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            NotificationCenter notificationCenter = NotificationCenter.getInstance(GroupCallPip.this.currentAccount);
            final View view = this.val$windowView;
            final View view2 = this.val$windowRemoveTooltipView;
            final WindowManager windowManager = this.val$windowManager;
            final View view3 = this.val$windowRemoveTooltipOverlayView;
            final View view4 = this.val$alert;
            notificationCenter.doOnIdle(new Runnable() {
                @Override
                public final void run() {
                    GroupCallPip.AnonymousClass9.lambda$onAnimationEnd$0(view, view2, windowManager, view3, view4);
                }
            });
        }
    }

    public GroupCallPip(Context context, int i) {
        this.currentAccount = i;
        AnonymousClass3 anonymousClass3 = new AnonymousClass3(context, ViewConfiguration.get(context).getScaledTouchSlop());
        this.windowView = anonymousClass3;
        anonymousClass3.setAlpha(0.7f);
        GroupCallPipButton groupCallPipButton = new GroupCallPipButton(context, this.currentAccount, false);
        this.button = groupCallPipButton;
        this.windowView.addView(groupCallPipButton, LayoutHelper.createFrame(-1, -1, 17));
        AvatarsImageView avatarsImageView = new AvatarsImageView(context, true);
        this.avatarsImageView = avatarsImageView;
        avatarsImageView.setStyle(5);
        this.avatarsImageView.setCentered(true);
        this.avatarsImageView.setVisibility(8);
        this.avatarsImageView.setDelegate(new Runnable() {
            @Override
            public final void run() {
                GroupCallPip.this.lambda$new$0();
            }
        });
        updateAvatars(false);
        this.windowView.addView(this.avatarsImageView, LayoutHelper.createFrame(108, 36, 49));
        this.windowRemoveTooltipView = new FrameLayout(context) {
            @Override
            protected void onLayout(boolean z, int i2, int i3, int i4, int i5) {
                super.onLayout(z, i2, i3, i4, i5);
                GroupCallPip groupCallPip = GroupCallPip.this;
                groupCallPip.windowRemoveTooltipView.getLocationOnScreen(groupCallPip.location);
                GroupCallPip groupCallPip2 = GroupCallPip.this;
                int[] iArr = groupCallPip2.location;
                groupCallPip2.windowLeft = iArr[0];
                groupCallPip2.windowTop = iArr[1] - AndroidUtilities.dp(25.0f);
            }

            @Override
            public void setVisibility(int i2) {
                super.setVisibility(i2);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setVisibility(i2);
            }
        };
        View view = new View(context) {
            Paint paint = new Paint(1);

            @Override
            protected void onDraw(Canvas canvas) {
                GroupCallPip groupCallPip = GroupCallPip.this;
                boolean z = groupCallPip.animateToPrepareRemove;
                if (z) {
                    float f = groupCallPip.prepareToRemoveProgress;
                    if (f != 1.0f) {
                        float f2 = f + 0.064f;
                        groupCallPip.prepareToRemoveProgress = f2;
                        if (f2 > 1.0f) {
                            groupCallPip.prepareToRemoveProgress = 1.0f;
                        }
                        invalidate();
                        this.paint.setColor(ColorUtils.blendARGB(1711607061, 1714752530, GroupCallPip.this.prepareToRemoveProgress));
                        canvas.drawCircle(getMeasuredWidth() / 2.0f, (getMeasuredHeight() / 2.0f) - AndroidUtilities.dp(25.0f), AndroidUtilities.dp(35.0f) + (AndroidUtilities.dp(5.0f) * GroupCallPip.this.prepareToRemoveProgress), this.paint);
                    }
                }
                if (!z) {
                    float f3 = groupCallPip.prepareToRemoveProgress;
                    if (f3 != 0.0f) {
                        float f4 = f3 - 0.064f;
                        groupCallPip.prepareToRemoveProgress = f4;
                        if (f4 < 0.0f) {
                            groupCallPip.prepareToRemoveProgress = 0.0f;
                        }
                        invalidate();
                    }
                }
                this.paint.setColor(ColorUtils.blendARGB(1711607061, 1714752530, GroupCallPip.this.prepareToRemoveProgress));
                canvas.drawCircle(getMeasuredWidth() / 2.0f, (getMeasuredHeight() / 2.0f) - AndroidUtilities.dp(25.0f), AndroidUtilities.dp(35.0f) + (AndroidUtilities.dp(5.0f) * GroupCallPip.this.prepareToRemoveProgress), this.paint);
            }

            @Override
            public void setAlpha(float f) {
                super.setAlpha(f);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setAlpha(f);
            }

            @Override
            public void setScaleX(float f) {
                super.setScaleX(f);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setScaleX(f);
            }

            @Override
            public void setScaleY(float f) {
                super.setScaleY(f);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setScaleY(f);
            }

            @Override
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                GroupCallPip.this.windowRemoveTooltipOverlayView.setTranslationY(f);
            }
        };
        this.removeTooltipView = view;
        this.windowRemoveTooltipView.addView(view);
        this.windowRemoveTooltipOverlayView = new FrameLayout(context);
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.iconView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        int i2 = R.raw.group_pip_delete_icon;
        RLottieDrawable rLottieDrawable = new RLottieDrawable(i2, "" + i2, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f), true, null);
        this.deleteIcon = rLottieDrawable;
        rLottieDrawable.setPlayInDirectionOfCustomEndFrame(true);
        rLottieImageView.setAnimation(this.deleteIcon);
        rLottieImageView.setColorFilter(-1);
        this.windowRemoveTooltipOverlayView.addView(rLottieImageView, LayoutHelper.createFrame(40, 40.0f, 17, 0.0f, 0.0f, 0.0f, 25.0f));
        FrameLayout frameLayout = new FrameLayout(context) {
            int lastSize = -1;

            @Override
            protected void onLayout(boolean z, int i3, int i4, int i5, int i6) {
                super.onLayout(z, i3, i4, i5, i6);
                android.graphics.Point point = AndroidUtilities.displaySize;
                int i7 = point.x + point.y;
                int i8 = this.lastSize;
                if (i8 > 0 && i8 != i7) {
                    setVisibility(8);
                    GroupCallPip groupCallPip = GroupCallPip.this;
                    groupCallPip.showAlert = false;
                    groupCallPip.checkButtonAlpha();
                }
                this.lastSize = i7;
            }

            @Override
            public void setVisibility(int i3) {
                super.setVisibility(i3);
                if (i3 == 8) {
                    this.lastSize = -1;
                }
            }
        };
        this.alertContainer = frameLayout;
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                GroupCallPip.this.lambda$new$1(view2);
            }
        });
        this.alertContainer.setClipChildren(false);
        FrameLayout frameLayout2 = this.alertContainer;
        GroupCallPipAlertView groupCallPipAlertView = new GroupCallPipAlertView(context, this.currentAccount);
        this.pipAlertView = groupCallPipAlertView;
        frameLayout2.addView(groupCallPipAlertView, LayoutHelper.createFrame(-2, -2.0f));
    }

    public void checkButtonAlpha() {
        boolean z = this.pressedState || this.showAlert;
        if (this.buttonInAlpha != z) {
            this.buttonInAlpha = z;
            this.windowView.animate().alpha(z ? 1.0f : 0.7f).start();
            this.button.setPressedState(z);
        }
    }

    public static boolean checkInlinePermissions() {
        return Build.VERSION.SDK_INT < 23 || ApplicationLoader.canDrawOverlays;
    }

    public static void clearForce() {
        forceRemoved = false;
    }

    private static WindowManager.LayoutParams createWindowLayoutParams(Context context) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.height = AndroidUtilities.dp(105.0f);
        layoutParams.width = AndroidUtilities.dp(105.0f);
        layoutParams.gravity = 51;
        layoutParams.format = -3;
        layoutParams.type = AndroidUtilities.checkInlinePermissions(context) ? Build.VERSION.SDK_INT >= 26 ? 2038 : 2003 : 99;
        layoutParams.flags = 520;
        return layoutParams;
    }

    public static void finish() {
        GroupCallPip groupCallPip = instance;
        if (groupCallPip != null) {
            groupCallPip.showAlert(false);
            GroupCallPip groupCallPip2 = instance;
            final WindowManager windowManager = groupCallPip2.windowManager;
            final FrameLayout frameLayout = groupCallPip2.windowView;
            final FrameLayout frameLayout2 = groupCallPip2.windowRemoveTooltipView;
            final FrameLayout frameLayout3 = groupCallPip2.windowRemoveTooltipOverlayView;
            final FrameLayout frameLayout4 = groupCallPip2.alertContainer;
            frameLayout.animate().scaleX(0.5f).scaleY(0.5f).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    if (frameLayout.getParent() != null) {
                        frameLayout.setVisibility(8);
                        frameLayout2.setVisibility(8);
                        frameLayout3.setVisibility(8);
                        windowManager.removeView(frameLayout);
                        windowManager.removeView(frameLayout2);
                        windowManager.removeView(frameLayout3);
                        windowManager.removeView(frameLayout4);
                    }
                }
            }).start();
            instance.onDestroy();
            instance = null;
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
        }
    }

    public static GroupCallPip getInstance() {
        return instance;
    }

    public void getRelativePosition(float f, float f2, float[] fArr) {
        android.graphics.Point point = AndroidUtilities.displaySize;
        float f3 = point.x;
        float f4 = point.y;
        float f5 = -AndroidUtilities.dp(36.0f);
        fArr[0] = (f - f5) / ((f3 - (f5 * 2.0f)) - AndroidUtilities.dp(105.0f));
        fArr[1] = f2 / (f4 - AndroidUtilities.dp(105.0f));
        fArr[0] = Math.min(1.0f, Math.max(0.0f, fArr[0]));
        fArr[1] = Math.min(1.0f, Math.max(0.0f, fArr[1]));
    }

    public static boolean isShowing() {
        VoIPService sharedInstance;
        if (!RTMPStreamPipOverlay.isVisible() && instance == null) {
            return (!checkInlinePermissions() || (sharedInstance = VoIPService.getSharedInstance()) == null || sharedInstance.groupCall == null || sharedInstance.isHangingUp() || forceRemoved || (!ApplicationLoader.mainInterfaceStopped && GroupCallActivity.groupCallUiVisible)) ? false : true;
        }
        return true;
    }

    public void lambda$new$0() {
        updateAvatars(true);
    }

    public void lambda$new$1(View view) {
        showAlert(false);
    }

    public void lambda$pinnedToCenter$3(ValueAnimator valueAnimator) {
        if (this.removed) {
            return;
        }
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.pinnedProgress = floatValue;
        this.button.setPinnedProgress(floatValue);
        this.windowView.setScaleX(1.0f - (this.pinnedProgress * 0.6f));
        this.windowView.setScaleY(1.0f - (this.pinnedProgress * 0.6f));
        if (this.moving) {
            updateButtonPosition();
        }
    }

    public static void lambda$remove$2() {
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
    }

    public static boolean onBackPressed() {
        GroupCallPip groupCallPip = instance;
        if (groupCallPip == null || !groupCallPip.showAlert) {
            return false;
        }
        groupCallPip.showAlert(false);
        return true;
    }

    private void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupCallUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcSpeakerAmplitudeEvent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.groupCallVisibilityChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
    }

    public void remove() {
        GroupCallPip groupCallPip = instance;
        if (groupCallPip == null) {
            return;
        }
        this.removed = true;
        forceRemoved = true;
        this.button.removed = true;
        groupCallPip.showAlert(false);
        float measuredWidth = ((this.windowLeft - this.windowOffsetLeft) + (this.windowRemoveTooltipView.getMeasuredWidth() / 2.0f)) - (this.windowLayoutParams.x + (this.windowView.getMeasuredWidth() / 2.0f));
        float measuredHeight = ((this.windowTop - this.windowOffsetTop) + (this.windowRemoveTooltipView.getMeasuredHeight() / 2.0f)) - (this.windowLayoutParams.y + (this.windowView.getMeasuredHeight() / 2.0f));
        GroupCallPip groupCallPip2 = instance;
        WindowManager windowManager = groupCallPip2.windowManager;
        FrameLayout frameLayout = groupCallPip2.windowView;
        FrameLayout frameLayout2 = groupCallPip2.windowRemoveTooltipView;
        FrameLayout frameLayout3 = groupCallPip2.windowRemoveTooltipOverlayView;
        FrameLayout frameLayout4 = groupCallPip2.alertContainer;
        onDestroy();
        instance = null;
        AnimatorSet animatorSet = new AnimatorSet();
        long currentFrame = this.deleteIcon.getCurrentFrame() < 33 ? ((1.0f - (this.deleteIcon.getCurrentFrame() / 33.0f)) * ((float) this.deleteIcon.getDuration())) / 2.0f : 0L;
        float f = this.windowLayoutParams.x;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(f, measuredWidth + f);
        ofFloat.addUpdateListener(this.updateXlistener);
        ValueAnimator duration = ofFloat.setDuration(250L);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        duration.setInterpolator(cubicBezierInterpolator);
        animatorSet.playTogether(ofFloat);
        float f2 = this.windowLayoutParams.y;
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(f2, (f2 + measuredHeight) - AndroidUtilities.dp(30.0f), this.windowLayoutParams.y + measuredHeight);
        ofFloat2.addUpdateListener(this.updateYlistener);
        ofFloat2.setDuration(250L).setInterpolator(cubicBezierInterpolator);
        animatorSet.playTogether(ofFloat2);
        Property property = View.SCALE_X;
        animatorSet.playTogether(ObjectAnimator.ofFloat(frameLayout, (Property<FrameLayout, Float>) property, frameLayout.getScaleX(), 0.1f).setDuration(180L));
        Property property2 = View.SCALE_Y;
        animatorSet.playTogether(ObjectAnimator.ofFloat(frameLayout, (Property<FrameLayout, Float>) property2, frameLayout.getScaleY(), 0.1f).setDuration(180L));
        Property property3 = View.ALPHA;
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(frameLayout, (Property<FrameLayout, Float>) property3, 1.0f, 0.0f);
        float f3 = (float) 350;
        ofFloat3.setStartDelay(f3 * 0.7f);
        ofFloat3.setDuration(f3 * 0.3f);
        animatorSet.playTogether(ofFloat3);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                GroupCallPip.lambda$remove$2();
            }
        }, 370L);
        long j = currentFrame + 530;
        ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(this.removeTooltipView, (Property<View, Float>) property, 1.0f, 1.05f);
        ofFloat4.setDuration(j);
        CubicBezierInterpolator cubicBezierInterpolator2 = CubicBezierInterpolator.EASE_BOTH;
        ofFloat4.setInterpolator(cubicBezierInterpolator2);
        animatorSet.playTogether(ofFloat4);
        ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(this.removeTooltipView, (Property<View, Float>) property2, 1.0f, 1.05f);
        ofFloat5.setDuration(j);
        ofFloat5.setInterpolator(cubicBezierInterpolator2);
        animatorSet.playTogether(ofFloat5);
        ObjectAnimator ofFloat6 = ObjectAnimator.ofFloat(this.removeTooltipView, (Property<View, Float>) property, 1.0f, 0.3f);
        ofFloat6.setStartDelay(j);
        ofFloat6.setDuration(350L);
        CubicBezierInterpolator cubicBezierInterpolator3 = CubicBezierInterpolator.EASE_OUT_QUINT;
        ofFloat6.setInterpolator(cubicBezierInterpolator3);
        animatorSet.playTogether(ofFloat6);
        ObjectAnimator ofFloat7 = ObjectAnimator.ofFloat(this.removeTooltipView, (Property<View, Float>) property2, 1.0f, 0.3f);
        ofFloat7.setStartDelay(j);
        ofFloat7.setDuration(350L);
        ofFloat7.setInterpolator(cubicBezierInterpolator3);
        animatorSet.playTogether(ofFloat7);
        ObjectAnimator ofFloat8 = ObjectAnimator.ofFloat(this.removeTooltipView, (Property<View, Float>) View.TRANSLATION_Y, 0.0f, AndroidUtilities.dp(60.0f));
        ofFloat8.setStartDelay(j);
        ofFloat8.setDuration(350L);
        ofFloat8.setInterpolator(cubicBezierInterpolator3);
        animatorSet.playTogether(ofFloat8);
        ObjectAnimator ofFloat9 = ObjectAnimator.ofFloat(this.removeTooltipView, (Property<View, Float>) property3, 1.0f, 0.0f);
        ofFloat9.setStartDelay(j);
        ofFloat9.setDuration(350L);
        ofFloat9.setInterpolator(cubicBezierInterpolator3);
        animatorSet.playTogether(ofFloat9);
        animatorSet.addListener(new AnonymousClass9(frameLayout, frameLayout2, windowManager, frameLayout3, frameLayout4));
        animatorSet.start();
        this.deleteIcon.setCustomEndFrame(66);
        this.iconView.stopAnimation();
        this.iconView.playAnimation();
    }

    public void setPosition(float f, float f2) {
        float f3 = -AndroidUtilities.dp(36.0f);
        this.windowLayoutParams.x = (int) (f3 + (((AndroidUtilities.displaySize.x - (2.0f * f3)) - AndroidUtilities.dp(105.0f)) * f));
        this.windowLayoutParams.y = (int) ((AndroidUtilities.displaySize.y - AndroidUtilities.dp(105.0f)) * f2);
        updateAvatarsPosition();
        if (this.windowView.getParent() != null) {
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        }
    }

    public static void show(Context context, int i) {
        if (instance != null) {
            return;
        }
        instance = new GroupCallPip(context, i);
        WindowManager windowManager = (WindowManager) ApplicationLoader.applicationContext.getSystemService("window");
        instance.windowManager = windowManager;
        WindowManager.LayoutParams createWindowLayoutParams = createWindowLayoutParams(context);
        createWindowLayoutParams.width = -1;
        createWindowLayoutParams.height = -1;
        createWindowLayoutParams.dimAmount = 0.25f;
        createWindowLayoutParams.flags = 522;
        windowManager.addView(instance.alertContainer, createWindowLayoutParams);
        instance.alertContainer.setVisibility(8);
        WindowManager.LayoutParams createWindowLayoutParams2 = createWindowLayoutParams(context);
        createWindowLayoutParams2.gravity = 81;
        createWindowLayoutParams2.width = AndroidUtilities.dp(100.0f);
        createWindowLayoutParams2.height = AndroidUtilities.dp(150.0f);
        windowManager.addView(instance.windowRemoveTooltipView, createWindowLayoutParams2);
        WindowManager.LayoutParams createWindowLayoutParams3 = createWindowLayoutParams(context);
        GroupCallPip groupCallPip = instance;
        groupCallPip.windowLayoutParams = createWindowLayoutParams3;
        windowManager.addView(groupCallPip.windowView, createWindowLayoutParams3);
        WindowManager.LayoutParams createWindowLayoutParams4 = createWindowLayoutParams(context);
        createWindowLayoutParams4.gravity = 81;
        createWindowLayoutParams4.width = AndroidUtilities.dp(100.0f);
        createWindowLayoutParams4.height = AndroidUtilities.dp(150.0f);
        windowManager.addView(instance.windowRemoveTooltipOverlayView, createWindowLayoutParams4);
        instance.windowRemoveTooltipView.setVisibility(8);
        instance.windowView.setScaleX(0.5f);
        instance.windowView.setScaleY(0.5f);
        instance.windowView.setAlpha(0.0f);
        instance.windowView.animate().alpha(0.7f).scaleY(1.0f).scaleX(1.0f).setDuration(350L).setInterpolator(new OvershootInterpolator()).start();
        NotificationCenter.getInstance(instance.currentAccount).addObserver(instance, NotificationCenter.groupCallUpdated);
        NotificationCenter.getGlobalInstance().addObserver(instance, NotificationCenter.webRtcSpeakerAmplitudeEvent);
        NotificationCenter.getGlobalInstance().addObserver(instance, NotificationCenter.didEndCall);
    }

    public void showAlert(boolean z) {
        ViewPropertyAnimator listener;
        if (z != this.showAlert) {
            this.showAlert = z;
            this.alertContainer.animate().setListener(null).cancel();
            if (this.showAlert) {
                if (this.alertContainer.getVisibility() != 0) {
                    this.alertContainer.setVisibility(0);
                    this.alertContainer.setAlpha(0.0f);
                    this.pipAlertView.setScaleX(0.7f);
                    this.pipAlertView.setScaleY(0.7f);
                }
                this.alertContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        GroupCallPipAlertView groupCallPipAlertView;
                        GroupCallPip.this.alertContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                        GroupCallPip groupCallPip = GroupCallPip.this;
                        groupCallPip.alertContainer.getLocationOnScreen(groupCallPip.location);
                        float measuredWidth = r0.windowLayoutParams.x + GroupCallPip.this.windowOffsetLeft + (r0.button.getMeasuredWidth() / 2.0f);
                        float f = measuredWidth - r0.location[0];
                        int i = 1;
                        float measuredWidth2 = ((r0.windowLayoutParams.y + GroupCallPip.this.windowOffsetTop) + (r0.button.getMeasuredWidth() / 2.0f)) - GroupCallPip.this.location[1];
                        boolean z2 = measuredWidth2 - ((float) AndroidUtilities.dp(61.0f)) > 0.0f && ((float) AndroidUtilities.dp(61.0f)) + measuredWidth2 < ((float) GroupCallPip.this.alertContainer.getMeasuredHeight());
                        if (AndroidUtilities.dp(61.0f) + f + GroupCallPip.this.pipAlertView.getMeasuredWidth() >= GroupCallPip.this.alertContainer.getMeasuredWidth() - AndroidUtilities.dp(16.0f) || !z2) {
                            if ((f - AndroidUtilities.dp(61.0f)) - GroupCallPip.this.pipAlertView.getMeasuredWidth() > AndroidUtilities.dp(16.0f) && z2) {
                                float dp = AndroidUtilities.dp(40.0f) / GroupCallPip.this.pipAlertView.getMeasuredHeight();
                                float max = Math.max(dp, Math.min(measuredWidth2 / GroupCallPip.this.alertContainer.getMeasuredHeight(), 1.0f - dp));
                                GroupCallPip.this.pipAlertView.setTranslationX((int) ((f - AndroidUtilities.dp(61.0f)) - GroupCallPip.this.pipAlertView.getMeasuredWidth()));
                                GroupCallPip.this.pipAlertView.setTranslationY((int) (measuredWidth2 - (r0.getMeasuredHeight() * max)));
                                groupCallPipAlertView = GroupCallPip.this.pipAlertView;
                            } else if (measuredWidth2 > GroupCallPip.this.alertContainer.getMeasuredHeight() * 0.3f) {
                                float dp2 = AndroidUtilities.dp(40.0f) / GroupCallPip.this.pipAlertView.getMeasuredWidth();
                                float max2 = Math.max(dp2, Math.min(f / GroupCallPip.this.alertContainer.getMeasuredWidth(), 1.0f - dp2));
                                GroupCallPip.this.pipAlertView.setTranslationX((int) (f - (r5.getMeasuredWidth() * max2)));
                                GroupCallPip.this.pipAlertView.setTranslationY((int) ((measuredWidth2 - r2.getMeasuredHeight()) - AndroidUtilities.dp(61.0f)));
                                groupCallPipAlertView = GroupCallPip.this.pipAlertView;
                                i = 3;
                            } else {
                                float dp3 = AndroidUtilities.dp(40.0f) / GroupCallPip.this.pipAlertView.getMeasuredWidth();
                                float max3 = Math.max(dp3, Math.min(f / GroupCallPip.this.alertContainer.getMeasuredWidth(), 1.0f - dp3));
                                GroupCallPip.this.pipAlertView.setTranslationX((int) (f - (r5.getMeasuredWidth() * max3)));
                                GroupCallPip.this.pipAlertView.setTranslationY((int) (AndroidUtilities.dp(61.0f) + measuredWidth2));
                                groupCallPipAlertView = GroupCallPip.this.pipAlertView;
                                i = 2;
                            }
                            groupCallPipAlertView.setPosition(i, f, measuredWidth2);
                        } else {
                            GroupCallPip.this.pipAlertView.setTranslationX(AndroidUtilities.dp(61.0f) + f);
                            float dp4 = AndroidUtilities.dp(40.0f) / GroupCallPip.this.pipAlertView.getMeasuredHeight();
                            float max4 = Math.max(dp4, Math.min(measuredWidth2 / GroupCallPip.this.alertContainer.getMeasuredHeight(), 1.0f - dp4));
                            GroupCallPip.this.pipAlertView.setTranslationY((int) (measuredWidth2 - (r2.getMeasuredHeight() * max4)));
                            GroupCallPip.this.pipAlertView.setPosition(0, f, measuredWidth2);
                        }
                        return false;
                    }
                });
                this.alertContainer.animate().alpha(1.0f).setDuration(150L).start();
                listener = this.pipAlertView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(150L);
            } else {
                this.pipAlertView.animate().scaleX(0.7f).scaleY(0.7f).setDuration(150L).start();
                listener = this.alertContainer.animate().alpha(0.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        GroupCallPip.this.alertContainer.setVisibility(8);
                    }
                });
            }
            listener.start();
        }
        checkButtonAlpha();
    }

    private void showAvatars(boolean z) {
        ViewPropertyAnimator listener;
        if (z != (this.avatarsImageView.getTag() != null)) {
            this.avatarsImageView.animate().setListener(null).cancel();
            if (z) {
                if (this.avatarsImageView.getVisibility() != 0) {
                    this.avatarsImageView.setVisibility(0);
                    this.avatarsImageView.setAlpha(0.0f);
                    this.avatarsImageView.setScaleX(0.5f);
                    this.avatarsImageView.setScaleY(0.5f);
                }
                listener = this.avatarsImageView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(150L);
            } else {
                listener = this.avatarsImageView.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).setDuration(150L).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        GroupCallPip.this.avatarsImageView.setVisibility(8);
                    }
                });
            }
            listener.start();
            this.avatarsImageView.setTag(z ? 1 : null);
        }
    }

    private void updateAvatars(boolean z) {
        AvatarsImageView avatarsImageView = this.avatarsImageView;
        if (avatarsImageView.avatarsDrawable.transitionProgressAnimator != null) {
            avatarsImageView.updateAfterTransitionEnd();
            return;
        }
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        ChatObject.Call call = sharedInstance != null ? sharedInstance.groupCall : null;
        int i = 0;
        if (call != null) {
            long selfId = sharedInstance.getSelfId();
            int size = call.sortedParticipants.size();
            int i2 = 0;
            while (i < 2) {
                if (i2 < size) {
                    TLRPC.TL_groupCallParticipant tL_groupCallParticipant = call.sortedParticipants.get(i2);
                    if (MessageObject.getPeerId(tL_groupCallParticipant.peer) != selfId && SystemClock.uptimeMillis() - tL_groupCallParticipant.lastSpeakTime <= 500) {
                        this.avatarsImageView.setObject(i, this.currentAccount, tL_groupCallParticipant);
                    }
                    i2++;
                } else {
                    this.avatarsImageView.setObject(i, this.currentAccount, null);
                }
                i++;
                i2++;
            }
            this.avatarsImageView.setObject(2, this.currentAccount, null);
        } else {
            while (i < 3) {
                this.avatarsImageView.setObject(i, this.currentAccount, null);
                i++;
            }
        }
        this.avatarsImageView.commitTransition(z);
    }

    public void updateAvatarsPosition() {
        float max = Math.max(this.windowLayoutParams.x, -AndroidUtilities.dp(36.0f));
        int i = AndroidUtilities.displaySize.x;
        float min = Math.min(max, (i - this.windowView.getMeasuredWidth()) + AndroidUtilities.dp(36.0f));
        if (min < 0.0f) {
            this.avatarsImageView.setTranslationX(Math.abs(min) / 3.0f);
        } else if (min > i - this.windowView.getMeasuredWidth()) {
            this.avatarsImageView.setTranslationX((-Math.abs(min - (i - this.windowView.getMeasuredWidth()))) / 3.0f);
        } else {
            this.avatarsImageView.setTranslationX(0.0f);
        }
    }

    public void updateButtonPosition() {
        float measuredWidth = ((this.windowLeft - this.windowOffsetLeft) + (this.windowRemoveTooltipView.getMeasuredWidth() / 2.0f)) - (this.windowView.getMeasuredWidth() / 2.0f);
        float measuredHeight = (((this.windowTop - this.windowOffsetTop) + (this.windowRemoveTooltipView.getMeasuredHeight() / 2.0f)) - (this.windowView.getMeasuredHeight() / 2.0f)) - AndroidUtilities.dp(25.0f);
        WindowManager.LayoutParams layoutParams = this.windowLayoutParams;
        float f = this.windowX;
        float f2 = this.pinnedProgress;
        float f3 = 1.0f - f2;
        layoutParams.x = (int) ((f * f3) + (measuredWidth * f2));
        layoutParams.y = (int) ((this.windowY * f3) + (measuredHeight * f2));
        updateAvatarsPosition();
        if (this.windowView.getParent() != null) {
            this.windowManager.updateViewLayout(this.windowView, this.windowLayoutParams);
        }
    }

    public static void updateVisibility(Context context) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        boolean z = (sharedInstance == null || sharedInstance.groupCall == null || sharedInstance.isHangingUp()) ? false : true;
        if (!AndroidUtilities.checkInlinePermissions(ApplicationLoader.applicationContext) || !z || forceRemoved || (!ApplicationLoader.mainInterfaceStopped && GroupCallActivity.groupCallUiVisible)) {
            finish();
        } else {
            show(context, sharedInstance.getAccount());
            instance.showAvatars(true);
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.groupCallUpdated || i == NotificationCenter.webRtcSpeakerAmplitudeEvent) {
            updateAvatars(true);
        } else if (i == NotificationCenter.didEndCall) {
            updateVisibility(ApplicationLoader.applicationContext);
        }
    }

    void pinnedToCenter(final boolean z) {
        if (this.removed || this.animateToPinnedToCenter == z) {
            return;
        }
        this.animateToPinnedToCenter = z;
        ValueAnimator valueAnimator = this.pinAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.pinAnimator.cancel();
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.pinnedProgress, z ? 1.0f : 0.0f);
        this.pinAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                GroupCallPip.this.lambda$pinnedToCenter$3(valueAnimator2);
            }
        });
        this.pinAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                GroupCallPip groupCallPip = GroupCallPip.this;
                if (groupCallPip.removed) {
                    return;
                }
                groupCallPip.pinnedProgress = z ? 1.0f : 0.0f;
                groupCallPip.button.setPinnedProgress(GroupCallPip.this.pinnedProgress);
                GroupCallPip groupCallPip2 = GroupCallPip.this;
                groupCallPip2.windowView.setScaleX(1.0f - (groupCallPip2.pinnedProgress * 0.6f));
                GroupCallPip groupCallPip3 = GroupCallPip.this;
                groupCallPip3.windowView.setScaleY(1.0f - (groupCallPip3.pinnedProgress * 0.6f));
                GroupCallPip groupCallPip4 = GroupCallPip.this;
                if (groupCallPip4.moving) {
                    groupCallPip4.updateButtonPosition();
                }
            }
        });
        this.pinAnimator.setDuration(250L);
        this.pinAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.pinAnimator.start();
    }

    void prepareToRemove(boolean z) {
        if (this.animateToPrepareRemove != z) {
            this.animateToPrepareRemove = z;
            this.removeTooltipView.invalidate();
            if (!this.removed) {
                this.deleteIcon.setCustomEndFrame(z ? 33 : 0);
                this.iconView.playAnimation();
            }
            if (z) {
                try {
                    this.button.performHapticFeedback(3, 2);
                } catch (Exception unused) {
                }
            }
        }
        this.button.prepareToRemove(z);
    }

    void showRemoveTooltip(boolean z) {
        AnimatorSet animatorSet;
        if (this.animateToShowRemoveTooltip != z) {
            this.animateToShowRemoveTooltip = z;
            AnimatorSet animatorSet2 = this.showRemoveAnimator;
            if (animatorSet2 != null) {
                animatorSet2.removeAllListeners();
                this.showRemoveAnimator.cancel();
            }
            if (z) {
                if (this.windowRemoveTooltipView.getVisibility() != 0) {
                    this.windowRemoveTooltipView.setVisibility(0);
                    this.removeTooltipView.setAlpha(0.0f);
                    this.removeTooltipView.setScaleX(0.5f);
                    this.removeTooltipView.setScaleY(0.5f);
                    this.deleteIcon.setCurrentFrame(0);
                }
                AnimatorSet animatorSet3 = new AnimatorSet();
                this.showRemoveAnimator = animatorSet3;
                View view = this.removeTooltipView;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(view, (Property<View, Float>) View.ALPHA, view.getAlpha(), 1.0f);
                View view2 = this.removeTooltipView;
                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(view2, (Property<View, Float>) View.SCALE_X, view2.getScaleX(), 1.0f);
                View view3 = this.removeTooltipView;
                animatorSet3.playTogether(ofFloat, ofFloat2, ObjectAnimator.ofFloat(view3, (Property<View, Float>) View.SCALE_Y, view3.getScaleY(), 1.0f));
                animatorSet = this.showRemoveAnimator.setDuration(150L);
            } else {
                AnimatorSet animatorSet4 = new AnimatorSet();
                this.showRemoveAnimator = animatorSet4;
                View view4 = this.removeTooltipView;
                ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(view4, (Property<View, Float>) View.ALPHA, view4.getAlpha(), 0.0f);
                View view5 = this.removeTooltipView;
                ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(view5, (Property<View, Float>) View.SCALE_X, view5.getScaleX(), 0.5f);
                View view6 = this.removeTooltipView;
                animatorSet4.playTogether(ofFloat3, ofFloat4, ObjectAnimator.ofFloat(view6, (Property<View, Float>) View.SCALE_Y, view6.getScaleY(), 0.5f));
                this.showRemoveAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        GroupCallPip.this.windowRemoveTooltipView.setVisibility(8);
                        GroupCallPip groupCallPip = GroupCallPip.this;
                        groupCallPip.animateToPrepareRemove = false;
                        groupCallPip.prepareToRemoveProgress = 0.0f;
                    }
                });
                this.showRemoveAnimator.setDuration(150L);
                animatorSet = this.showRemoveAnimator;
            }
            animatorSet.start();
        }
    }
}
