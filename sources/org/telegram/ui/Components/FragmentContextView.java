package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_phone;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSlider;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.SharingLocationsAlert;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LocationActivity;

public class FragmentContextView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, VoIPService.StateListener {
    private static final float[] speeds = {0.5f, 1.0f, 1.2f, 1.5f, 1.7f, 2.0f};
    private final int account;
    private FragmentContextView additionalContextView;
    private AnimatorSet animatorSet;
    private View applyingView;
    private AvatarsImageView avatars;
    private ChatActivityInterface chatActivity;
    private boolean checkCallAfterAnimation;
    private boolean checkImportAfterAnimation;
    private Runnable checkLocationRunnable;
    private boolean checkPlayerAfterAnimation;
    private ImageView closeButton;
    float collapseProgress;
    boolean collapseTransition;
    private int currentProgress;
    private int currentStyle;
    private FragmentContextViewDelegate delegate;
    boolean drawOverlay;
    float extraHeight;
    private boolean firstLocationsLoaded;
    private boolean flickOnAttach;
    private BaseFragment fragment;
    private FrameLayout frameLayout;
    private Paint gradientPaint;
    private TextPaint gradientTextPaint;
    private int gradientWidth;
    private RLottieImageView importingImageView;
    private boolean isLocation;
    private boolean isMusic;
    private boolean isMuted;
    private TextView joinButton;
    private CellFlickerDrawable joinButtonFlicker;
    private int joinButtonWidth;
    private int lastLocationSharingCount;
    private MessageObject lastMessageObject;
    private long lastPlaybackClick;
    private String lastString;
    private LinearGradient linearGradient;
    private Matrix matrix;
    float micAmplitude;
    private RLottieImageView muteButton;
    private RLottieDrawable muteDrawable;
    private AnimationNotificationsLocker notificationsLocker;
    private AnimationNotificationsLocker notificationsLocker2;
    private ButtonBounce notifyButtonBounce;
    private boolean notifyButtonEnabled;
    private AnimatedTextView.AnimatedTextDrawable notifyText;
    private ImageView playButton;
    private PlayPauseDrawable playPauseDrawable;
    private ActionBarMenuItem playbackSpeedButton;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean scheduleRunnableScheduled;
    private View selector;
    private View shadow;
    private FrameLayout silentButton;
    private ImageView silentButtonImage;
    private boolean slidingSpeed;
    float speakerAmplitude;
    private HintView speedHintView;
    private SpeedIconDrawable speedIcon;
    private ActionBarMenuItem.Item[] speedItems;
    private ActionBarMenuSlider.SpeedSlider speedSlider;
    private AudioPlayerAlert.ClippingTextViewSwitcher subtitleTextView;
    private boolean supportsCalls;
    private AudioPlayerAlert.ClippingTextViewSwitcher titleTextView;
    private int toggleGroupCallStartSubscriptionReqId;
    protected float topPadding;
    private final Runnable updateScheduleTimeRunnable;
    private boolean visible;
    boolean wasDraw;
    private boolean willBeNotified;

    public class AnonymousClass7 extends RLottieImageView {
        private final Runnable pressRunnable;
        boolean pressed;
        boolean scheduled;
        private final Runnable toggleMicRunnable;

        AnonymousClass7(Context context) {
            super(context);
            this.toggleMicRunnable = new Runnable() {
                @Override
                public final void run() {
                    FragmentContextView.AnonymousClass7.this.lambda$$0();
                }
            };
            this.pressRunnable = new Runnable() {
                @Override
                public final void run() {
                    FragmentContextView.AnonymousClass7.this.lambda$$1();
                }
            };
        }

        public void lambda$$0() {
            RLottieDrawable rLottieDrawable;
            if (VoIPService.getSharedInstance() == null) {
                return;
            }
            int i = 0;
            VoIPService.getSharedInstance().setMicMute(false, true, false);
            if (FragmentContextView.this.muteDrawable.setCustomEndFrame(FragmentContextView.this.isMuted ? 15 : 29)) {
                if (FragmentContextView.this.isMuted) {
                    rLottieDrawable = FragmentContextView.this.muteDrawable;
                } else {
                    rLottieDrawable = FragmentContextView.this.muteDrawable;
                    i = 14;
                }
                rLottieDrawable.setCurrentFrame(i);
            }
            FragmentContextView.this.muteButton.playAnimation();
            Theme.getFragmentContextViewWavesDrawable().updateState(true);
        }

        public void lambda$$1() {
            if (!this.scheduled || VoIPService.getSharedInstance() == null) {
                return;
            }
            this.scheduled = false;
            this.pressed = true;
            FragmentContextView.this.isMuted = false;
            AndroidUtilities.runOnUIThread(this.toggleMicRunnable, 90L);
            try {
                FragmentContextView.this.muteButton.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName(Button.class.getName());
            accessibilityNodeInfo.setText(LocaleController.getString(FragmentContextView.this.isMuted ? R.string.VoipUnmute : R.string.VoipMute));
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (FragmentContextView.this.currentStyle != 3 && FragmentContextView.this.currentStyle != 1) {
                return super.onTouchEvent(motionEvent);
            }
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (sharedInstance == null) {
                AndroidUtilities.cancelRunOnUIThread(this.pressRunnable);
                AndroidUtilities.cancelRunOnUIThread(this.toggleMicRunnable);
                this.scheduled = false;
                this.pressed = false;
                return true;
            }
            if (motionEvent.getAction() == 0 && sharedInstance.isMicMute()) {
                AndroidUtilities.runOnUIThread(this.pressRunnable, 300L);
                this.scheduled = true;
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                AndroidUtilities.cancelRunOnUIThread(this.toggleMicRunnable);
                if (this.scheduled) {
                    AndroidUtilities.cancelRunOnUIThread(this.pressRunnable);
                    this.scheduled = false;
                } else if (this.pressed) {
                    FragmentContextView.this.isMuted = true;
                    if (FragmentContextView.this.muteDrawable.setCustomEndFrame(15)) {
                        if (FragmentContextView.this.isMuted) {
                            FragmentContextView.this.muteDrawable.setCurrentFrame(0);
                        } else {
                            FragmentContextView.this.muteDrawable.setCurrentFrame(14);
                        }
                    }
                    FragmentContextView.this.muteButton.playAnimation();
                    if (VoIPService.getSharedInstance() != null) {
                        VoIPService.getSharedInstance().setMicMute(true, true, false);
                        try {
                            FragmentContextView.this.muteButton.performHapticFeedback(3, 2);
                        } catch (Exception unused) {
                        }
                    }
                    this.pressed = false;
                    Theme.getFragmentContextViewWavesDrawable().updateState(true);
                    MotionEvent obtain = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
                    super.onTouchEvent(obtain);
                    obtain.recycle();
                    return true;
                }
            }
            return super.onTouchEvent(motionEvent);
        }
    }

    public interface FragmentContextViewDelegate {
        void onAnimation(boolean z, boolean z2);
    }

    public FragmentContextView(Context context, BaseFragment baseFragment, View view, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.speedItems = new ActionBarMenuItem.Item[6];
        this.currentProgress = -1;
        this.currentStyle = -1;
        this.supportsCalls = true;
        this.notifyText = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
        this.updateScheduleTimeRunnable = new Runnable() {
            @Override
            public void run() {
                if (FragmentContextView.this.gradientTextPaint == null || !(FragmentContextView.this.fragment instanceof ChatActivity)) {
                    FragmentContextView.this.scheduleRunnableScheduled = false;
                    return;
                }
                ChatObject.Call groupCall = FragmentContextView.this.chatActivity.getGroupCall();
                if (groupCall == null || !groupCall.isScheduled()) {
                    FragmentContextView.this.notifyButtonEnabled = false;
                    FragmentContextView.this.scheduleRunnableScheduled = false;
                    return;
                }
                int currentTime = groupCall.call.schedule_date - FragmentContextView.this.fragment.getConnectionsManager().getCurrentTime();
                String formatPluralString = currentTime >= 86400 ? LocaleController.formatPluralString("Days", Math.round(currentTime / 86400.0f), new Object[0]) : AndroidUtilities.formatFullDuration(currentTime);
                AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = FragmentContextView.this.notifyText;
                if (!FragmentContextView.this.willBeNotified) {
                    formatPluralString = LocaleController.getString(R.string.VoipChatNotify);
                }
                animatedTextDrawable.setText(formatPluralString, true);
                AndroidUtilities.runOnUIThread(FragmentContextView.this.updateScheduleTimeRunnable, 1000L);
                FragmentContextView.this.frameLayout.invalidate();
            }
        };
        this.account = UserConfig.selectedAccount;
        this.lastLocationSharingCount = -1;
        this.checkLocationRunnable = new Runnable() {
            @Override
            public void run() {
                FragmentContextView.this.checkLocationString();
                AndroidUtilities.runOnUIThread(FragmentContextView.this.checkLocationRunnable, 1000L);
            }
        };
        this.notificationsLocker = new AnimationNotificationsLocker();
        this.notificationsLocker2 = new AnimationNotificationsLocker(new int[]{NotificationCenter.messagesDidLoad});
        this.toggleGroupCallStartSubscriptionReqId = 0;
        this.resourcesProvider = resourcesProvider;
        this.fragment = baseFragment;
        if (baseFragment instanceof ChatActivityInterface) {
            this.chatActivity = (ChatActivityInterface) baseFragment;
        }
        this.applyingView = view;
        this.visible = true;
        this.isLocation = z;
        if (view == null) {
            ((ViewGroup) baseFragment.getFragmentView()).setClipToPadding(false);
        }
        setTag(1);
    }

    public FragmentContextView(Context context, BaseFragment baseFragment, boolean z) {
        this(context, baseFragment, null, z, null);
    }

    public FragmentContextView(Context context, BaseFragment baseFragment, boolean z, Theme.ResourcesProvider resourcesProvider) {
        this(context, baseFragment, null, z, resourcesProvider);
    }

    private void checkCreateView() {
        if (this.frameLayout != null) {
            return;
        }
        final Context context = getContext();
        BlurredFrameLayout blurredFrameLayout = new BlurredFrameLayout(context, this.fragment.getFragmentView() instanceof SizeNotifierFrameLayout ? (SizeNotifierFrameLayout) this.fragment.getFragmentView() : null) {
            private final RectF notifyButtonRect = new RectF();

            @Override
            public void dispatchDraw(Canvas canvas) {
                float f;
                super.dispatchDraw(canvas);
                if (FragmentContextView.this.currentStyle == 4 && FragmentContextView.this.notifyButtonEnabled) {
                    int ceil = ((int) Math.ceil(FragmentContextView.this.notifyText.getCurrentWidth())) + AndroidUtilities.dp(24.0f);
                    if (ceil != FragmentContextView.this.gradientWidth) {
                        FragmentContextView.this.linearGradient = new LinearGradient(0.0f, 0.0f, ceil * 1.7f, 0.0f, new int[]{-10187532, -7575089, -2860679, -2860679}, new float[]{0.0f, 0.294f, 0.588f, 1.0f}, Shader.TileMode.CLAMP);
                        FragmentContextView.this.gradientPaint.setShader(FragmentContextView.this.linearGradient);
                        FragmentContextView.this.gradientWidth = ceil;
                    }
                    ChatObject.Call groupCall = FragmentContextView.this.chatActivity.getGroupCall();
                    if (FragmentContextView.this.fragment == null || groupCall == null || !groupCall.isScheduled()) {
                        f = 0.0f;
                    } else {
                        long currentTimeMillis = (groupCall.call.schedule_date * 1000) - FragmentContextView.this.fragment.getConnectionsManager().getCurrentTimeMillis();
                        f = currentTimeMillis >= 0 ? currentTimeMillis < 5000 ? 1.0f - (((float) currentTimeMillis) / 5000.0f) : 0.0f : 1.0f;
                        if (currentTimeMillis < 6000) {
                            invalidate();
                        }
                    }
                    FragmentContextView.this.matrix.reset();
                    FragmentContextView.this.matrix.postTranslate((-FragmentContextView.this.gradientWidth) * 0.7f * f, 0.0f);
                    FragmentContextView.this.linearGradient.setLocalMatrix(FragmentContextView.this.matrix);
                    int measuredWidth = (getMeasuredWidth() - ceil) - AndroidUtilities.dp(10.0f);
                    float f2 = measuredWidth;
                    float dp = AndroidUtilities.dp(10.0f);
                    this.notifyButtonRect.set(f2, dp, measuredWidth + ceil, r2 + AndroidUtilities.dp(28.0f));
                    canvas.save();
                    float scale = FragmentContextView.this.notifyButtonBounce.getScale(0.1f);
                    canvas.scale(scale, scale, this.notifyButtonRect.centerX(), this.notifyButtonRect.centerY());
                    canvas.translate(f2, dp);
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(0.0f, 0.0f, ceil, AndroidUtilities.dp(28.0f));
                    canvas.drawRoundRect(rectF, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), FragmentContextView.this.gradientPaint);
                    canvas.translate(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(6.0f));
                    FragmentContextView.this.notifyText.setBounds(0, 0, AndroidUtilities.displaySize.x, AndroidUtilities.dp(16.0f));
                    FragmentContextView.this.notifyText.draw(canvas);
                    canvas.restore();
                }
            }

            @Override
            public boolean dispatchTouchEvent(android.view.MotionEvent r6) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.AnonymousClass3.dispatchTouchEvent(android.view.MotionEvent):boolean");
            }

            @Override
            public void invalidate() {
                super.invalidate();
                if (FragmentContextView.this.avatars == null || FragmentContextView.this.avatars.getVisibility() != 0) {
                    return;
                }
                FragmentContextView.this.avatars.invalidate();
            }

            @Override
            protected boolean verifyDrawable(Drawable drawable) {
                return drawable == FragmentContextView.this.notifyText || super.verifyDrawable(drawable);
            }
        };
        this.frameLayout = blurredFrameLayout;
        this.notifyButtonBounce = new ButtonBounce(blurredFrameLayout);
        this.notifyText.setOverrideFullWidth(AndroidUtilities.displaySize.x);
        this.notifyText.setScaleProperty(0.4f);
        this.notifyText.setCallback(this.frameLayout);
        this.notifyText.setTextColor(-1);
        this.notifyText.setTextSize(AndroidUtilities.dp(14.0f));
        this.notifyText.setTypeface(AndroidUtilities.bold());
        addView(this.frameLayout, LayoutHelper.createFrame(-1, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        View view = new View(context);
        this.selector = view;
        this.frameLayout.addView(view, LayoutHelper.createFrame(-1, -1.0f));
        View view2 = new View(context);
        this.shadow = view2;
        view2.setBackgroundResource(R.drawable.blockpanel_shadow);
        addView(this.shadow, LayoutHelper.createFrame(-1, 2.0f, 51, 0.0f, 36.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.playButton = imageView;
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        imageView.setScaleType(scaleType);
        ImageView imageView2 = this.playButton;
        int i = Theme.key_inappPlayerPlayPause;
        int themedColor = getThemedColor(i);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        imageView2.setColorFilter(new PorterDuffColorFilter(themedColor, mode));
        ImageView imageView3 = this.playButton;
        PlayPauseDrawable playPauseDrawable = new PlayPauseDrawable(14);
        this.playPauseDrawable = playPauseDrawable;
        imageView3.setImageDrawable(playPauseDrawable);
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 21) {
            this.playButton.setBackground(Theme.createSelectorDrawable(getThemedColor(i) & 436207615, 1, AndroidUtilities.dp(14.0f)));
        }
        addView(this.playButton, LayoutHelper.createFrame(36, 36, 51));
        this.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                FragmentContextView.this.lambda$checkCreateView$0(view3);
            }
        });
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.importingImageView = rLottieImageView;
        rLottieImageView.setScaleType(scaleType);
        this.importingImageView.setAutoRepeat(true);
        this.importingImageView.setAnimation(R.raw.import_progress, 30, 30);
        this.importingImageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(22.0f), getThemedColor(i)));
        addView(this.importingImageView, LayoutHelper.createFrame(22, 22.0f, 51, 7.0f, 7.0f, 0.0f, 0.0f));
        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = new AudioPlayerAlert.ClippingTextViewSwitcher(context) {
            @Override
            protected TextView createTextView() {
                Typeface typeface;
                TextView textView = new TextView(context);
                textView.setMaxLines(1);
                textView.setLines(1);
                textView.setSingleLine(true);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                float f = 15.0f;
                textView.setTextSize(1, 15.0f);
                textView.setGravity(19);
                if (FragmentContextView.this.currentStyle == 0 || FragmentContextView.this.currentStyle == 2) {
                    textView.setGravity(19);
                    typeface = Typeface.DEFAULT;
                } else {
                    if (FragmentContextView.this.currentStyle != 4) {
                        if (FragmentContextView.this.currentStyle == 1 || FragmentContextView.this.currentStyle == 3) {
                            textView.setGravity(19);
                            textView.setTextColor(FragmentContextView.this.getThemedColor(Theme.key_returnToCallText));
                            textView.setTypeface(AndroidUtilities.bold());
                            f = 14.0f;
                            textView.setTextSize(1, f);
                        }
                        return textView;
                    }
                    textView.setGravity(51);
                    textView.setTextColor(FragmentContextView.this.getThemedColor(Theme.key_inappPlayerPerformer));
                    typeface = AndroidUtilities.bold();
                }
                textView.setTypeface(typeface);
                textView.setTextSize(1, f);
                return textView;
            }
        };
        this.titleTextView = clippingTextViewSwitcher;
        addView(clippingTextViewSwitcher, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher2 = new AudioPlayerAlert.ClippingTextViewSwitcher(context) {
            @Override
            protected TextView createTextView() {
                TextView textView = new TextView(context);
                textView.setMaxLines(1);
                textView.setLines(1);
                textView.setSingleLine(true);
                textView.setEllipsize(TextUtils.TruncateAt.END);
                textView.setGravity(3);
                textView.setTextSize(1, 13.0f);
                textView.setTextColor(FragmentContextView.this.getThemedColor(Theme.key_inappPlayerClose));
                return textView;
            }
        };
        this.subtitleTextView = clippingTextViewSwitcher2;
        addView(clippingTextViewSwitcher2, LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 10.0f, 36.0f, 0.0f));
        CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
        this.joinButtonFlicker = cellFlickerDrawable;
        cellFlickerDrawable.setProgress(1.0f);
        this.joinButtonFlicker.repeatEnabled = false;
        TextView textView = new TextView(context) {
            private void updateJoinButtonWidth(int i3) {
                if (FragmentContextView.this.joinButtonWidth != i3) {
                    FragmentContextView.this.titleTextView.setPadding(FragmentContextView.this.titleTextView.getPaddingLeft(), FragmentContextView.this.titleTextView.getPaddingTop(), (FragmentContextView.this.titleTextView.getPaddingRight() - FragmentContextView.this.joinButtonWidth) + i3, FragmentContextView.this.titleTextView.getPaddingBottom());
                    FragmentContextView.this.joinButtonWidth = i3;
                }
            }

            @Override
            public void draw(Canvas canvas) {
                super.draw(canvas);
                int dp = AndroidUtilities.dp(1.0f);
                RectF rectF = AndroidUtilities.rectTmp;
                float f = dp;
                rectF.set(f, f, getWidth() - dp, getHeight() - dp);
                FragmentContextView.this.joinButtonFlicker.draw(canvas, rectF, AndroidUtilities.dp(16.0f), this);
            }

            @Override
            protected void onMeasure(int i3, int i4) {
                super.onMeasure(i3, i4);
                updateJoinButtonWidth(getMeasuredWidth());
            }

            @Override
            protected void onSizeChanged(int i3, int i4, int i5, int i6) {
                super.onSizeChanged(i3, i4, i5, i6);
                FragmentContextView.this.joinButtonFlicker.setParentWidth(getWidth());
            }

            @Override
            public void setVisibility(int i3) {
                super.setVisibility(i3);
                if (i3 != 0) {
                    updateJoinButtonWidth(0);
                    FragmentContextView.this.joinButtonWidth = 0;
                }
            }
        };
        this.joinButton = textView;
        textView.setText(LocaleController.getString(R.string.VoipChatJoin));
        this.joinButton.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
        this.joinButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(16.0f), getThemedColor(Theme.key_featuredStickers_addButton), getThemedColor(Theme.key_featuredStickers_addButtonPressed)));
        this.joinButton.setTextSize(1, 14.0f);
        this.joinButton.setTypeface(AndroidUtilities.bold());
        this.joinButton.setGravity(17);
        this.joinButton.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
        addView(this.joinButton, LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 10.0f, 14.0f, 0.0f));
        this.joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                FragmentContextView.this.lambda$checkCreateView$1(view3);
            }
        });
        if (this.flickOnAttach) {
            startJoinFlickerAnimation();
        }
        this.silentButton = new FrameLayout(context);
        ImageView imageView4 = new ImageView(context);
        this.silentButtonImage = imageView4;
        imageView4.setImageResource(R.drawable.msg_mute);
        ImageView imageView5 = this.silentButtonImage;
        int i3 = Theme.key_inappPlayerClose;
        imageView5.setColorFilter(new PorterDuffColorFilter(getThemedColor(i3), mode));
        this.silentButton.addView(this.silentButtonImage, LayoutHelper.createFrame(20, 20, 17));
        if (i2 >= 21) {
            this.silentButton.setBackground(Theme.createSelectorDrawable(getThemedColor(i3) & 436207615, 1, AndroidUtilities.dp(14.0f)));
        }
        this.silentButton.setContentDescription(LocaleController.getString(R.string.Unmute));
        this.silentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                FragmentContextView.lambda$checkCreateView$2(view3);
            }
        });
        this.silentButton.setVisibility(8);
        addView(this.silentButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 36.0f, 0.0f));
        if (!this.isLocation) {
            createPlaybackSpeedButton();
        }
        AvatarsImageView avatarsImageView = new AvatarsImageView(context, false);
        this.avatars = avatarsImageView;
        avatarsImageView.setAvatarsTextSize(AndroidUtilities.dp(21.0f));
        this.avatars.setDelegate(new Runnable() {
            @Override
            public final void run() {
                FragmentContextView.this.lambda$checkCreateView$3();
            }
        });
        this.avatars.setVisibility(8);
        addView(this.avatars, LayoutHelper.createFrame(108, 36, 51));
        int i4 = R.raw.voice_muted;
        this.muteDrawable = new RLottieDrawable(i4, "" + i4, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(20.0f), true, null);
        AnonymousClass7 anonymousClass7 = new AnonymousClass7(context);
        this.muteButton = anonymousClass7;
        anonymousClass7.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_returnToCallText), mode));
        if (i2 >= 21) {
            this.muteButton.setBackground(Theme.createSelectorDrawable(getThemedColor(i3) & 436207615, 1, AndroidUtilities.dp(14.0f)));
        }
        this.muteButton.setAnimation(this.muteDrawable);
        this.muteButton.setScaleType(scaleType);
        this.muteButton.setVisibility(8);
        addView(this.muteButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
        this.muteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                FragmentContextView.this.lambda$checkCreateView$4(view3);
            }
        });
        ImageView imageView6 = new ImageView(context);
        this.closeButton = imageView6;
        imageView6.setImageResource(R.drawable.miniplayer_close);
        this.closeButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(i3), mode));
        if (i2 >= 21) {
            this.closeButton.setBackground(Theme.createSelectorDrawable(getThemedColor(i3) & 436207615, 1, AndroidUtilities.dp(14.0f)));
        }
        this.closeButton.setScaleType(scaleType);
        addView(this.closeButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 2.0f, 0.0f));
        this.closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                FragmentContextView.this.lambda$checkCreateView$6(view3);
            }
        });
        setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                FragmentContextView.this.lambda$checkCreateView$8(view3);
            }
        });
    }

    private void checkLiveLocation(boolean z) {
        String formatPluralString;
        int i;
        View fragmentView = this.fragment.getFragmentView();
        if (!z && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
            z = true;
        }
        BaseFragment baseFragment = this.fragment;
        if (!(baseFragment instanceof DialogsActivity ? LocationController.getLocationsCount() != 0 : LocationController.getInstance(baseFragment.getCurrentAccount()).isSharingLocation(this.chatActivity.getDialogId()))) {
            this.lastLocationSharingCount = -1;
            AndroidUtilities.cancelRunOnUIThread(this.checkLocationRunnable);
            if (this.visible) {
                this.visible = false;
                if (z) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                    return;
                }
                AnimatorSet animatorSet = this.animatorSet;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.animatorSet = null;
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animatorSet = animatorSet2;
                animatorSet2.playTogether(ObjectAnimator.ofFloat(this, "topPadding", 0.0f));
                this.animatorSet.setDuration(200L);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (FragmentContextView.this.animatorSet == null || !FragmentContextView.this.animatorSet.equals(animator)) {
                            return;
                        }
                        FragmentContextView.this.setVisibility(8);
                        FragmentContextView.this.animatorSet = null;
                    }
                });
                this.animatorSet.start();
                return;
            }
            return;
        }
        checkCreateView();
        updateStyle(2);
        this.playButton.setImageDrawable(new ShareLocationDrawable(getContext(), 1));
        if (z && this.topPadding == 0.0f) {
            setTopPadding(AndroidUtilities.dp2(getStyleHeight()));
        }
        if (!this.visible) {
            if (!z) {
                AnimatorSet animatorSet3 = this.animatorSet;
                if (animatorSet3 != null) {
                    animatorSet3.cancel();
                    this.animatorSet = null;
                }
                AnimatorSet animatorSet4 = new AnimatorSet();
                this.animatorSet = animatorSet4;
                animatorSet4.playTogether(ObjectAnimator.ofFloat(this, "topPadding", AndroidUtilities.dp2(getStyleHeight())));
                this.animatorSet.setDuration(200L);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (FragmentContextView.this.animatorSet == null || !FragmentContextView.this.animatorSet.equals(animator)) {
                            return;
                        }
                        FragmentContextView.this.animatorSet = null;
                    }
                });
                this.animatorSet.start();
            }
            this.visible = true;
            setVisibility(0);
        }
        if (!(this.fragment instanceof DialogsActivity)) {
            this.checkLocationRunnable.run();
            checkLocationString();
            return;
        }
        String string = LocaleController.getString(R.string.LiveLocationContext);
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < 4; i2++) {
            arrayList.addAll(LocationController.getInstance(i2).sharingLocationsUI);
        }
        if (arrayList.size() == 1) {
            LocationController.SharingLocationInfo sharingLocationInfo = (LocationController.SharingLocationInfo) arrayList.get(0);
            long dialogId = sharingLocationInfo.messageObject.getDialogId();
            boolean isUserDialog = DialogObject.isUserDialog(dialogId);
            MessagesController messagesController = MessagesController.getInstance(sharingLocationInfo.messageObject.currentAccount);
            if (isUserDialog) {
                formatPluralString = UserObject.getFirstName(messagesController.getUser(Long.valueOf(dialogId)));
                i = R.string.AttachLiveLocationIsSharing;
            } else {
                TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-dialogId));
                formatPluralString = chat != null ? chat.title : "";
                i = R.string.AttachLiveLocationIsSharingChat;
            }
        } else {
            formatPluralString = LocaleController.formatPluralString("Chats", arrayList.size(), new Object[0]);
            i = R.string.AttachLiveLocationIsSharingChats;
        }
        String format = String.format(LocaleController.getString(i), string, formatPluralString);
        int indexOf = format.indexOf(string);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(format);
        int i3 = 0;
        while (i3 < 2) {
            AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.titleTextView;
            TextView textView = i3 == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
            if (textView != null) {
                textView.setEllipsize(TextUtils.TruncateAt.END);
            }
            i3++;
        }
        spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.bold(), 0, getThemedColor(Theme.key_inappPlayerPerformer)), indexOf, string.length() + indexOf, 18);
        this.titleTextView.setText(spannableStringBuilder, false);
    }

    public void checkLocationString() {
        int i;
        String format;
        String format2;
        if (this.chatActivity == null || this.titleTextView == null) {
            return;
        }
        checkCreateView();
        long dialogId = this.chatActivity.getDialogId();
        int currentAccount = this.fragment.getCurrentAccount();
        ArrayList arrayList = (ArrayList) LocationController.getInstance(currentAccount).locationsCache.get(dialogId);
        if (!this.firstLocationsLoaded) {
            LocationController.getInstance(currentAccount).loadLiveLocations(dialogId);
            this.firstLocationsLoaded = true;
        }
        TLRPC.User user = null;
        if (arrayList != null) {
            long clientUserId = UserConfig.getInstance(currentAccount).getClientUserId();
            int currentTime = ConnectionsManager.getInstance(currentAccount).getCurrentTime();
            i = 0;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                TLRPC.Message message = (TLRPC.Message) arrayList.get(i2);
                TLRPC.MessageMedia messageMedia = message.media;
                if (messageMedia != null && message.date + messageMedia.period > currentTime) {
                    long fromChatId = MessageObject.getFromChatId(message);
                    if (user == null && fromChatId != clientUserId) {
                        user = MessagesController.getInstance(currentAccount).getUser(Long.valueOf(fromChatId));
                    }
                    i++;
                }
            }
        } else {
            i = 0;
        }
        if (this.lastLocationSharingCount == i) {
            return;
        }
        this.lastLocationSharingCount = i;
        String string = LocaleController.getString(R.string.LiveLocationContext);
        if (i == 0) {
            format = string;
        } else {
            int i3 = i - 1;
            if (LocationController.getInstance(currentAccount).isSharingLocation(dialogId)) {
                if (i3 == 0) {
                    format = String.format("%1$s - %2$s", string, LocaleController.getString(R.string.ChatYourSelfName));
                } else if (i3 != 1 || user == null) {
                    format2 = String.format("%1$s - %2$s %3$s", string, LocaleController.getString(R.string.ChatYourSelfName), LocaleController.formatPluralString("AndOther", i3, new Object[0]));
                    format = format2;
                } else {
                    format = String.format("%1$s - %2$s", string, LocaleController.formatString("SharingYouAndOtherName", R.string.SharingYouAndOtherName, UserObject.getFirstName(user)));
                }
            } else if (i3 != 0) {
                format2 = String.format("%1$s - %2$s %3$s", string, UserObject.getFirstName(user), LocaleController.formatPluralString("AndOther", i3, new Object[0]));
                format = format2;
            } else {
                format = String.format("%1$s - %2$s", string, UserObject.getFirstName(user));
            }
        }
        if (format.equals(this.lastString)) {
            return;
        }
        this.lastString = format;
        int indexOf = format.indexOf(string);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(format);
        int i4 = 0;
        while (i4 < 2) {
            AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.titleTextView;
            TextView textView = i4 == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
            if (textView != null) {
                textView.setEllipsize(TextUtils.TruncateAt.END);
            }
            i4++;
        }
        if (indexOf >= 0) {
            spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.bold(), 0, getThemedColor(Theme.key_inappPlayerPerformer)), indexOf, string.length() + indexOf, 18);
        }
        this.titleTextView.setText(spannableStringBuilder, false);
    }

    public void checkPlayer(boolean r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.checkPlayer(boolean):void");
    }

    private void checkSpeedHint() {
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis - this.lastPlaybackClick > 300) {
            int i = MessagesController.getGlobalNotificationsSettings().getInt("speedhint", 0) + 1;
            if (i > 2) {
                i = -10;
            }
            MessagesController.getGlobalNotificationsSettings().edit().putInt("speedhint", i).apply();
            if (i >= 0) {
                showSpeedHint();
            }
        }
        this.lastPlaybackClick = currentTimeMillis;
    }

    private void checkVisibility() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.checkVisibility():void");
    }

    private void createPlaybackSpeedButton() {
        if (this.playbackSpeedButton != null) {
            return;
        }
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(getContext(), (ActionBarMenu) null, 0, getThemedColor(Theme.key_dialogTextBlack), this.resourcesProvider);
        this.playbackSpeedButton = actionBarMenuItem;
        actionBarMenuItem.setAdditionalYOffset(AndroidUtilities.dp(30.0f));
        this.playbackSpeedButton.setLongClickEnabled(false);
        this.playbackSpeedButton.setVisibility(8);
        this.playbackSpeedButton.setTag(null);
        this.playbackSpeedButton.setShowSubmenuByMove(false);
        this.playbackSpeedButton.setContentDescription(LocaleController.getString(R.string.AccDescrPlayerSpeed));
        this.playbackSpeedButton.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() {
            @Override
            public final void onItemClick(int i) {
                FragmentContextView.this.lambda$createPlaybackSpeedButton$9(i);
            }
        });
        ActionBarMenuItem actionBarMenuItem2 = this.playbackSpeedButton;
        SpeedIconDrawable speedIconDrawable = new SpeedIconDrawable(true);
        this.speedIcon = speedIconDrawable;
        actionBarMenuItem2.setIcon(speedIconDrawable);
        final float[] fArr = {1.0f, 1.5f, 2.0f};
        ActionBarMenuSlider.SpeedSlider speedSlider = new ActionBarMenuSlider.SpeedSlider(getContext(), this.resourcesProvider);
        this.speedSlider = speedSlider;
        speedSlider.setRoundRadiusDp(6.0f);
        this.speedSlider.setDrawShadow(true);
        this.speedSlider.setOnValueChange(new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                FragmentContextView.this.lambda$createPlaybackSpeedButton$10((Float) obj, (Boolean) obj2);
            }
        });
        this.speedItems[0] = this.playbackSpeedButton.lazilyAddSubItem(0, R.drawable.msg_speed_slow, LocaleController.getString(R.string.SpeedSlow));
        this.speedItems[1] = this.playbackSpeedButton.lazilyAddSubItem(1, R.drawable.msg_speed_normal, LocaleController.getString(R.string.SpeedNormal));
        this.speedItems[2] = this.playbackSpeedButton.lazilyAddSubItem(2, R.drawable.msg_speed_medium, LocaleController.getString(R.string.SpeedMedium));
        this.speedItems[3] = this.playbackSpeedButton.lazilyAddSubItem(3, R.drawable.msg_speed_fast, LocaleController.getString(R.string.SpeedFast));
        this.speedItems[4] = this.playbackSpeedButton.lazilyAddSubItem(4, R.drawable.msg_speed_veryfast, LocaleController.getString(R.string.SpeedVeryFast));
        this.speedItems[5] = this.playbackSpeedButton.lazilyAddSubItem(5, R.drawable.msg_speed_superfast, LocaleController.getString(R.string.SpeedSuperFast));
        if (AndroidUtilities.density >= 3.0f) {
            this.playbackSpeedButton.setPadding(0, 1, 0, 0);
        }
        this.playbackSpeedButton.setAdditionalXOffset(AndroidUtilities.dp(8.0f));
        addView(this.playbackSpeedButton, LayoutHelper.createFrame(36, 36.0f, 53, 0.0f, 0.0f, 36.0f, 0.0f));
        this.playbackSpeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                FragmentContextView.this.lambda$createPlaybackSpeedButton$11(fArr, view);
            }
        });
        this.playbackSpeedButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public final boolean onLongClick(View view) {
                boolean lambda$createPlaybackSpeedButton$13;
                lambda$createPlaybackSpeedButton$13 = FragmentContextView.this.lambda$createPlaybackSpeedButton$13(view);
                return lambda$createPlaybackSpeedButton$13;
            }
        });
        updatePlaybackButton(false);
    }

    private boolean equals(float f, float f2) {
        return Math.abs(f - f2) < 0.05f;
    }

    public int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    private int getTitleTextColor() {
        int i = this.currentStyle;
        return getThemedColor(i == 4 ? Theme.key_inappPlayerPerformer : (i == 1 || i == 3) ? Theme.key_returnToCallText : Theme.key_inappPlayerTitle);
    }

    private boolean isPlayingVoice() {
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        return playingMessageObject != null && playingMessageObject.isVoice();
    }

    public void lambda$checkCreateView$0(View view) {
        if (this.currentStyle == 0) {
            if (MediaController.getInstance().isMessagePaused()) {
                MediaController.getInstance().playMessage(MediaController.getInstance().getPlayingMessageObject());
            } else {
                MediaController.getInstance().lambda$startAudioAgain$7(MediaController.getInstance().getPlayingMessageObject());
            }
        }
    }

    public void lambda$checkCreateView$1(View view) {
        callOnClick();
    }

    public static void lambda$checkCreateView$2(View view) {
        MediaController.getInstance().updateSilent(false);
    }

    public void lambda$checkCreateView$3() {
        updateAvatars(true);
    }

    public void lambda$checkCreateView$4(View view) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null) {
            return;
        }
        if (sharedInstance.groupCall != null) {
            AccountInstance.getInstance(sharedInstance.getAccount());
            ChatObject.Call call = sharedInstance.groupCall;
            TLRPC.Chat chat = sharedInstance.getChat();
            TLRPC.TL_groupCallParticipant tL_groupCallParticipant = (TLRPC.TL_groupCallParticipant) call.participants.get(sharedInstance.getSelfId());
            if (tL_groupCallParticipant != null && !tL_groupCallParticipant.can_self_unmute && tL_groupCallParticipant.muted && !ChatObject.canManageCalls(chat)) {
                return;
            }
        }
        boolean z = !sharedInstance.isMicMute();
        this.isMuted = z;
        sharedInstance.setMicMute(z, false, true);
        if (this.muteDrawable.setCustomEndFrame(this.isMuted ? 15 : 29)) {
            if (this.isMuted) {
                this.muteDrawable.setCurrentFrame(0);
            } else {
                this.muteDrawable.setCurrentFrame(14);
            }
        }
        this.muteButton.playAnimation();
        Theme.getFragmentContextViewWavesDrawable().updateState(true);
        try {
            this.muteButton.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
    }

    public void lambda$checkCreateView$5(DialogInterface dialogInterface, int i) {
        BaseFragment baseFragment = this.fragment;
        if (!(baseFragment instanceof DialogsActivity)) {
            LocationController.getInstance(baseFragment.getCurrentAccount()).removeSharingLocation(this.chatActivity.getDialogId());
            return;
        }
        for (int i2 = 0; i2 < 4; i2++) {
            LocationController.getInstance(i2).removeAllLocationSharings();
        }
    }

    public void lambda$checkCreateView$6(android.view.View r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.lambda$checkCreateView$6(android.view.View):void");
    }

    public void lambda$checkCreateView$7(DialogInterface dialogInterface) {
        checkImport(false);
    }

    public void lambda$checkCreateView$8(View view) {
        ChatObject.Call groupCall;
        long j;
        BaseFragment baseFragment;
        Dialog sharingLocationsAlert;
        String str;
        int i = this.currentStyle;
        if (i == 0) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (this.fragment == null || playingMessageObject == null) {
                return;
            }
            if (!playingMessageObject.isMusic()) {
                ChatActivityInterface chatActivityInterface = this.chatActivity;
                if (playingMessageObject.getDialogId() == (chatActivityInterface != null ? chatActivityInterface.getDialogId() : 0L)) {
                    this.chatActivity.scrollToMessageId(playingMessageObject.getId(), 0, false, 0, true, 0);
                    return;
                }
                long dialogId = playingMessageObject.getDialogId();
                Bundle bundle = new Bundle();
                if (DialogObject.isEncryptedDialog(dialogId)) {
                    bundle.putInt("enc_id", DialogObject.getEncryptedChatId(dialogId));
                } else {
                    if (DialogObject.isUserDialog(dialogId)) {
                        str = "user_id";
                    } else {
                        dialogId = -dialogId;
                        str = "chat_id";
                    }
                    bundle.putLong(str, dialogId);
                }
                bundle.putInt("message_id", playingMessageObject.getId());
                this.fragment.presentFragment(new ChatActivity(bundle), this.fragment instanceof ChatActivity);
                return;
            }
            if (!(getContext() instanceof LaunchActivity)) {
                return;
            }
            baseFragment = this.fragment;
            sharingLocationsAlert = new AudioPlayerAlert(getContext(), this.resourcesProvider);
        } else {
            if (i == 1) {
                getContext().startActivity(new Intent(getContext(), (Class<?>) LaunchActivity.class).setAction("voip"));
                return;
            }
            if (i != 2) {
                if (i == 3) {
                    if (VoIPService.getSharedInstance() == null || !(getContext() instanceof LaunchActivity)) {
                        return;
                    }
                    GroupCallActivity.create((LaunchActivity) getContext(), AccountInstance.getInstance(VoIPService.getSharedInstance().getAccount()), null, null, false, null);
                    return;
                }
                if (i != 4) {
                    if (i != 5 || this.fragment.getSendMessagesHelper().getImportingHistory(((ChatActivity) this.fragment).getDialogId()) == null) {
                        return;
                    }
                    ImportingAlert importingAlert = new ImportingAlert(getContext(), null, (ChatActivity) this.fragment, this.resourcesProvider);
                    importingAlert.setOnHideListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public final void onDismiss(DialogInterface dialogInterface) {
                            FragmentContextView.this.lambda$checkCreateView$7(dialogInterface);
                        }
                    });
                    this.fragment.showDialog(importingAlert);
                    checkImport(false);
                    return;
                }
                if (this.fragment.getParentActivity() == null || (groupCall = this.chatActivity.getGroupCall()) == null) {
                    return;
                }
                TLRPC.Chat chat = this.fragment.getMessagesController().getChat(Long.valueOf(groupCall.chatId));
                TLRPC.GroupCall groupCall2 = groupCall.call;
                Boolean valueOf = Boolean.valueOf((groupCall2 == null || groupCall2.rtmp_stream) ? false : true);
                Activity parentActivity = this.fragment.getParentActivity();
                BaseFragment baseFragment2 = this.fragment;
                VoIPHelper.startCall(chat, null, null, false, valueOf, parentActivity, baseFragment2, baseFragment2.getAccountInstance());
                return;
            }
            int i2 = UserConfig.selectedAccount;
            ChatActivityInterface chatActivityInterface2 = this.chatActivity;
            if (chatActivityInterface2 != null) {
                j = chatActivityInterface2.getDialogId();
                i2 = this.fragment.getCurrentAccount();
            } else {
                if (LocationController.getLocationsCount() == 1) {
                    for (int i3 = 0; i3 < 4; i3++) {
                        if (!LocationController.getInstance(i3).sharingLocationsUI.isEmpty()) {
                            LocationController.SharingLocationInfo sharingLocationInfo = LocationController.getInstance(i3).sharingLocationsUI.get(0);
                            j = sharingLocationInfo.did;
                            i2 = sharingLocationInfo.messageObject.currentAccount;
                            break;
                        }
                    }
                }
                j = 0;
            }
            if (j != 0) {
                openSharingLocation(LocationController.getInstance(i2).getSharingLocationInfo(j));
                return;
            } else {
                baseFragment = this.fragment;
                sharingLocationsAlert = new SharingLocationsAlert(getContext(), new SharingLocationsAlert.SharingLocationsAlertDelegate() {
                    @Override
                    public final void didSelectLocation(LocationController.SharingLocationInfo sharingLocationInfo2) {
                        FragmentContextView.this.openSharingLocation(sharingLocationInfo2);
                    }
                }, this.resourcesProvider);
            }
        }
        baseFragment.showDialog(sharingLocationsAlert);
    }

    public void lambda$createPlaybackSpeedButton$10(Float f, Boolean bool) {
        this.slidingSpeed = !bool.booleanValue();
        MediaController.getInstance().setPlaybackSpeed(this.isMusic, this.speedSlider.getSpeed(f.floatValue()));
    }

    public void lambda$createPlaybackSpeedButton$11(float[] fArr, View view) {
        float playbackSpeed = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
        int i = 0;
        while (true) {
            if (i >= fArr.length) {
                i = -1;
                break;
            } else if (playbackSpeed - 0.1f <= fArr[i]) {
                break;
            } else {
                i++;
            }
        }
        int i2 = i + 1;
        float f = fArr[i2 < fArr.length ? i2 : 0];
        MediaController.getInstance().setPlaybackSpeed(this.isMusic, f);
        playbackSpeedChanged(true, playbackSpeed, f);
        checkSpeedHint();
    }

    public void lambda$createPlaybackSpeedButton$12(float f, Boolean bool) {
        if (bool.booleanValue()) {
            return;
        }
        playbackSpeedChanged(false, f, MediaController.getInstance().getPlaybackSpeed(this.isMusic));
    }

    public boolean lambda$createPlaybackSpeedButton$13(View view) {
        final float playbackSpeed = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
        this.speedSlider.setSpeed(playbackSpeed, false);
        ActionBarMenuSlider.SpeedSlider speedSlider = this.speedSlider;
        int i = Theme.key_actionBarDefaultSubmenuBackground;
        speedSlider.setBackgroundColor(Theme.getColor(i, this.resourcesProvider));
        this.speedSlider.invalidateBlur(this.fragment instanceof ChatActivity);
        this.playbackSpeedButton.redrawPopup(Theme.getColor(i));
        this.playbackSpeedButton.updateColor();
        updatePlaybackButton(false);
        this.playbackSpeedButton.setDimMenu(0.3f);
        this.playbackSpeedButton.toggleSubMenu(this.speedSlider, null);
        this.playbackSpeedButton.setOnMenuDismiss(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                FragmentContextView.this.lambda$createPlaybackSpeedButton$12(playbackSpeed, (Boolean) obj);
            }
        });
        MessagesController.getGlobalNotificationsSettings().edit().putInt("speedhint", -15).apply();
        return true;
    }

    public void lambda$createPlaybackSpeedButton$9(int i) {
        if (i >= 0) {
            float[] fArr = speeds;
            if (i >= fArr.length) {
                return;
            }
            float playbackSpeed = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
            float f = fArr[i];
            MediaController.getInstance().setPlaybackSpeed(this.isMusic, f);
            if (playbackSpeed != f) {
                playbackSpeedChanged(false, playbackSpeed, f);
            }
        }
    }

    public static void lambda$openSharingLocation$14(LocationController.SharingLocationInfo sharingLocationInfo, long j, TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
        SendMessagesHelper.getInstance(sharingLocationInfo.messageObject.currentAccount).sendMessage(SendMessagesHelper.SendMessageParams.of(messageMedia, j, (MessageObject) null, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i2));
    }

    public void lambda$startJoinFlickerAnimation$15() {
        this.joinButtonFlicker.setProgress(0.0f);
        this.joinButton.invalidate();
    }

    public void openSharingLocation(final LocationController.SharingLocationInfo sharingLocationInfo) {
        if (sharingLocationInfo == null || !(this.fragment.getParentActivity() instanceof LaunchActivity)) {
            return;
        }
        LaunchActivity launchActivity = (LaunchActivity) this.fragment.getParentActivity();
        launchActivity.switchToAccount(sharingLocationInfo.messageObject.currentAccount, true);
        LocationActivity locationActivity = new LocationActivity(2);
        locationActivity.setMessageObject(sharingLocationInfo.messageObject);
        final long dialogId = sharingLocationInfo.messageObject.getDialogId();
        locationActivity.setDelegate(new LocationActivity.LocationActivityDelegate() {
            @Override
            public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2) {
                FragmentContextView.lambda$openSharingLocation$14(LocationController.SharingLocationInfo.this, dialogId, messageMedia, i, z, i2);
            }
        });
        launchActivity.lambda$runLinkRequest$95(locationActivity);
    }

    private void playbackSpeedChanged(boolean r8, float r9, float r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.playbackSpeedChanged(boolean, float, float):void");
    }

    private void showSpeedHint() {
        if (this.fragment == null || !(getParent() instanceof ViewGroup)) {
            return;
        }
        HintView hintView = new HintView(getContext(), 6, true) {
            @Override
            public void setVisibility(int i) {
                super.setVisibility(i);
                if (i != 0) {
                    try {
                        ((ViewGroup) getParent()).removeView(this);
                    } catch (Exception unused) {
                    }
                }
            }
        };
        this.speedHintView = hintView;
        hintView.setExtraTranslationY(AndroidUtilities.dp(-12.0f));
        this.speedHintView.setText(LocaleController.getString("SpeedHint"));
        ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(-2, -2);
        marginLayoutParams.rightMargin = AndroidUtilities.dp(3.0f);
        ((ViewGroup) getParent()).addView(this.speedHintView, marginLayoutParams);
        this.speedHintView.showForView(this.playbackSpeedButton, true);
    }

    public void startJoinFlickerAnimation() {
        CellFlickerDrawable cellFlickerDrawable = this.joinButtonFlicker;
        if (cellFlickerDrawable == null || cellFlickerDrawable.getProgress() < 1.0f) {
            this.flickOnAttach = true;
        } else {
            this.flickOnAttach = false;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    FragmentContextView.this.lambda$startJoinFlickerAnimation$15();
                }
            }, 150L);
        }
    }

    private void updateAvatars(boolean z) {
        ChatObject.Call call;
        int i;
        TLRPC.User user;
        int i2;
        ValueAnimator valueAnimator;
        checkCreateView();
        if (!z && (valueAnimator = this.avatars.avatarsDrawable.transitionProgressAnimator) != null) {
            valueAnimator.cancel();
            this.avatars.avatarsDrawable.transitionProgressAnimator = null;
        }
        AvatarsImageView avatarsImageView = this.avatars;
        if (avatarsImageView.avatarsDrawable.transitionProgressAnimator != null) {
            avatarsImageView.updateAfterTransitionEnd();
            return;
        }
        if (this.currentStyle == 4) {
            ChatActivityInterface chatActivityInterface = this.chatActivity;
            if (chatActivityInterface != null) {
                call = chatActivityInterface.getGroupCall();
                i2 = this.fragment.getCurrentAccount();
            } else {
                i2 = this.account;
                call = null;
            }
            i = i2;
            user = null;
        } else if (VoIPService.getSharedInstance() != null) {
            call = VoIPService.getSharedInstance().groupCall;
            user = this.chatActivity != null ? null : VoIPService.getSharedInstance().getUser();
            i = VoIPService.getSharedInstance().getAccount();
        } else {
            call = null;
            i = this.account;
            user = null;
        }
        if (call != null) {
            int size = call.sortedParticipants.size();
            for (int i3 = 0; i3 < 3; i3++) {
                AvatarsImageView avatarsImageView2 = this.avatars;
                if (i3 < size) {
                    avatarsImageView2.setObject(i3, i, call.sortedParticipants.get(i3));
                } else {
                    avatarsImageView2.setObject(i3, i, null);
                }
            }
        } else if (user != null) {
            this.avatars.setObject(0, i, user);
            for (int i4 = 1; i4 < 3; i4++) {
                this.avatars.setObject(i4, i, null);
            }
        } else {
            for (int i5 = 0; i5 < 3; i5++) {
                this.avatars.setObject(i5, i, null);
            }
        }
        this.avatars.commitTransition(z);
        if (this.currentStyle != 4 || call == null) {
            return;
        }
        int min = call.call.rtmp_stream ? 0 : Math.min(3, call.sortedParticipants.size());
        int i6 = min == 0 ? 10 : ((min - 1) * 24) + 52;
        if (z) {
            int i7 = ((FrameLayout.LayoutParams) this.titleTextView.getLayoutParams()).leftMargin;
            if (AndroidUtilities.dp(i6) != i7) {
                float translationX = (this.titleTextView.getTranslationX() + i7) - AndroidUtilities.dp(r3);
                this.titleTextView.setTranslationX(translationX);
                this.subtitleTextView.setTranslationX(translationX);
                ViewPropertyAnimator duration = this.titleTextView.animate().translationX(0.0f).setDuration(220L);
                CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
                duration.setInterpolator(cubicBezierInterpolator);
                this.subtitleTextView.animate().translationX(0.0f).setDuration(220L).setInterpolator(cubicBezierInterpolator);
            }
        } else {
            this.titleTextView.animate().cancel();
            this.subtitleTextView.animate().cancel();
            this.titleTextView.setTranslationX(0.0f);
            this.subtitleTextView.setTranslationX(0.0f);
        }
        float f = i6;
        this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, 51, f, 5.0f, call.isScheduled() ? 90.0f : 36.0f, 0.0f));
        this.subtitleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 20.0f, 51, f, 25.0f, call.isScheduled() ? 90.0f : 36.0f, 0.0f));
    }

    private void updateCallTitle() {
        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher;
        String str;
        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher2;
        int i;
        checkCreateView();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            int i2 = this.currentStyle;
            if (i2 == 1 || i2 == 3) {
                int callState = sharedInstance.getCallState();
                if (sharedInstance.isSwitchingStream() || !(callState == 1 || callState == 2 || callState == 6 || callState == 5)) {
                    if (sharedInstance.getChat() == null) {
                        if (sharedInstance.getUser() != null) {
                            TLRPC.User user = sharedInstance.getUser();
                            ChatActivityInterface chatActivityInterface = this.chatActivity;
                            if (chatActivityInterface == null || chatActivityInterface.getCurrentUser() == null || this.chatActivity.getCurrentUser().id != user.id) {
                                this.titleTextView.setText(ContactsController.formatName(user.first_name, user.last_name));
                                return;
                            } else {
                                this.titleTextView.setText(LocaleController.getString(R.string.ReturnToCall));
                                return;
                            }
                        }
                        return;
                    }
                    if (TextUtils.isEmpty(sharedInstance.groupCall.call.title)) {
                        ChatActivityInterface chatActivityInterface2 = this.chatActivity;
                        if (chatActivityInterface2 == null || chatActivityInterface2.getCurrentChat() == null || this.chatActivity.getCurrentChat().id != sharedInstance.getChat().id) {
                            clippingTextViewSwitcher = this.titleTextView;
                            str = sharedInstance.getChat().title;
                        } else {
                            TLRPC.Chat currentChat = this.chatActivity.getCurrentChat();
                            if (VoIPService.hasRtmpStream() || ChatObject.isChannelOrGiga(currentChat)) {
                                clippingTextViewSwitcher2 = this.titleTextView;
                                i = R.string.VoipChannelViewVoiceChat;
                            } else {
                                clippingTextViewSwitcher2 = this.titleTextView;
                                i = R.string.VoipGroupViewVoiceChat;
                            }
                        }
                    } else {
                        clippingTextViewSwitcher = this.titleTextView;
                        str = sharedInstance.groupCall.call.title;
                    }
                    clippingTextViewSwitcher.setText(str, false);
                    return;
                }
                clippingTextViewSwitcher2 = this.titleTextView;
                i = R.string.VoipGroupConnecting;
                clippingTextViewSwitcher2.setText(LocaleController.getString(i), false);
            }
        }
    }

    private void updatePaddings() {
        ViewGroup.LayoutParams layoutParams;
        int dp = getVisibility() == 0 ? 0 - AndroidUtilities.dp(getStyleHeight()) : 0;
        FragmentContextView fragmentContextView = this.additionalContextView;
        if (fragmentContextView == null || fragmentContextView.getVisibility() != 0) {
            layoutParams = getLayoutParams();
        } else {
            dp -= AndroidUtilities.dp(this.additionalContextView.getStyleHeight());
            ((FrameLayout.LayoutParams) getLayoutParams()).topMargin = dp;
            layoutParams = this.additionalContextView.getLayoutParams();
        }
        ((FrameLayout.LayoutParams) layoutParams).topMargin = dp;
    }

    private void updatePlaybackButton(boolean z) {
        ActionBarMenuItem.Item item;
        int i;
        if (this.speedIcon == null) {
            return;
        }
        float playbackSpeed = MediaController.getInstance().getPlaybackSpeed(this.isMusic);
        this.speedIcon.setValue(playbackSpeed, z);
        updateColors();
        boolean z2 = !this.slidingSpeed;
        this.slidingSpeed = false;
        for (int i2 = 0; i2 < this.speedItems.length; i2++) {
            if (!z2 || Math.abs(playbackSpeed - speeds[i2]) >= 0.05f) {
                item = this.speedItems[i2];
                i = Theme.key_actionBarDefaultSubmenuItem;
            } else {
                item = this.speedItems[i2];
                i = Theme.key_featuredStickers_addButtonPressed;
            }
            item.setColors(getThemedColor(i), getThemedColor(i));
        }
        this.speedSlider.setSpeed(playbackSpeed, z);
    }

    private void updateStyle(int i) {
        ImageView imageView;
        int i2;
        ActionBarMenuItem actionBarMenuItem;
        if (this.currentStyle == i) {
            return;
        }
        checkCreateView();
        int i3 = this.currentStyle;
        if (i3 == 3 || i3 == 1) {
            Theme.getFragmentContextViewWavesDrawable().removeParent(this);
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().unregisterStateListener(this);
            }
        }
        this.currentStyle = i;
        this.frameLayout.setWillNotDraw(i != 4);
        if (i != 4) {
            this.notifyButtonEnabled = false;
        }
        AvatarsImageView avatarsImageView = this.avatars;
        if (avatarsImageView != null) {
            avatarsImageView.setStyle(this.currentStyle);
            this.avatars.setLayoutParams(LayoutHelper.createFrame(108, getStyleHeight(), 51));
        }
        this.frameLayout.setLayoutParams(LayoutHelper.createFrame(-1, getStyleHeight(), 51, 0.0f, 0.0f, 0.0f, 0.0f));
        this.shadow.setLayoutParams(LayoutHelper.createFrame(-1, 2.0f, 51, 0.0f, getStyleHeight(), 0.0f, 0.0f));
        float f = this.topPadding;
        if (f > 0.0f && f != AndroidUtilities.dp2(getStyleHeight())) {
            updatePaddings();
            setTopPadding(AndroidUtilities.dp2(getStyleHeight()));
        }
        if (i == 5) {
            this.selector.setBackground(Theme.getSelectorDrawable(false));
            FrameLayout frameLayout = this.frameLayout;
            int i4 = Theme.key_inappPlayerBackground;
            frameLayout.setBackgroundColor(getThemedColor(i4));
            this.frameLayout.setTag(Integer.valueOf(i4));
            int i5 = 0;
            while (i5 < 2) {
                AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher = this.titleTextView;
                TextView textView = i5 == 0 ? clippingTextViewSwitcher.getTextView() : clippingTextViewSwitcher.getNextTextView();
                if (textView != null) {
                    textView.setGravity(19);
                    textView.setTextColor(getThemedColor(Theme.key_inappPlayerTitle));
                    textView.setTypeface(Typeface.DEFAULT);
                    textView.setTextSize(1, 15.0f);
                }
                i5++;
            }
            this.titleTextView.setTag(Integer.valueOf(Theme.key_inappPlayerTitle));
            this.subtitleTextView.setVisibility(8);
            this.joinButton.setVisibility(8);
            this.closeButton.setVisibility(8);
            this.playButton.setVisibility(8);
            this.muteButton.setVisibility(8);
            this.avatars.setVisibility(8);
            this.importingImageView.setVisibility(0);
            this.importingImageView.playAnimation();
            this.closeButton.setContentDescription(LocaleController.getString(R.string.AccDescrClosePlayer));
            ActionBarMenuItem actionBarMenuItem2 = this.playbackSpeedButton;
            if (actionBarMenuItem2 != null) {
                actionBarMenuItem2.setVisibility(8);
                this.playbackSpeedButton.setTag(null);
            }
            this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
            return;
        }
        if (i == 0 || i == 2) {
            this.selector.setBackground(Theme.getSelectorDrawable(false));
            FrameLayout frameLayout2 = this.frameLayout;
            int i6 = Theme.key_inappPlayerBackground;
            frameLayout2.setBackgroundColor(getThemedColor(i6));
            this.frameLayout.setTag(Integer.valueOf(i6));
            this.subtitleTextView.setVisibility(8);
            this.joinButton.setVisibility(8);
            this.closeButton.setVisibility(0);
            this.playButton.setVisibility(0);
            this.muteButton.setVisibility(8);
            this.importingImageView.setVisibility(8);
            this.importingImageView.stopAnimation();
            this.avatars.setVisibility(8);
            int i7 = 0;
            while (i7 < 2) {
                AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher2 = this.titleTextView;
                TextView textView2 = i7 == 0 ? clippingTextViewSwitcher2.getTextView() : clippingTextViewSwitcher2.getNextTextView();
                if (textView2 != null) {
                    textView2.setGravity(19);
                    textView2.setTextColor(getThemedColor(Theme.key_inappPlayerTitle));
                    textView2.setTypeface(Typeface.DEFAULT);
                    textView2.setTextSize(1, 15.0f);
                }
                i7++;
            }
            this.titleTextView.setTag(Integer.valueOf(Theme.key_inappPlayerTitle));
            if (i == 0) {
                this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 35.0f, 0.0f, 36.0f, 0.0f));
                createPlaybackSpeedButton();
                ActionBarMenuItem actionBarMenuItem3 = this.playbackSpeedButton;
                if (actionBarMenuItem3 != null) {
                    actionBarMenuItem3.setVisibility(0);
                    this.playbackSpeedButton.setTag(1);
                }
                imageView = this.closeButton;
                i2 = R.string.AccDescrClosePlayer;
            } else {
                this.playButton.setLayoutParams(LayoutHelper.createFrame(36, 36.0f, 51, 8.0f, 0.0f, 0.0f, 0.0f));
                this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-1, 36.0f, 51, 51.0f, 0.0f, 36.0f, 0.0f));
                imageView = this.closeButton;
                i2 = R.string.AccDescrStopLiveLocation;
            }
            imageView.setContentDescription(LocaleController.getString(i2));
            return;
        }
        if (i == 4) {
            this.selector.setBackground(Theme.getSelectorDrawable(false));
            FrameLayout frameLayout3 = this.frameLayout;
            int i8 = Theme.key_inappPlayerBackground;
            frameLayout3.setBackgroundColor(getThemedColor(i8));
            this.frameLayout.setTag(Integer.valueOf(i8));
            this.muteButton.setVisibility(8);
            this.subtitleTextView.setVisibility(0);
            int i9 = 0;
            while (i9 < 2) {
                AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher3 = this.titleTextView;
                TextView textView3 = i9 == 0 ? clippingTextViewSwitcher3.getTextView() : clippingTextViewSwitcher3.getNextTextView();
                if (textView3 != null) {
                    textView3.setGravity(51);
                    textView3.setTextColor(getThemedColor(Theme.key_inappPlayerPerformer));
                    textView3.setTypeface(AndroidUtilities.bold());
                    textView3.setTextSize(1, 15.0f);
                }
                i9++;
            }
            this.titleTextView.setTag(Integer.valueOf(Theme.key_inappPlayerPerformer));
            this.titleTextView.setPadding(0, 0, this.joinButtonWidth, 0);
            this.importingImageView.setVisibility(8);
            this.importingImageView.stopAnimation();
            ChatActivityInterface chatActivityInterface = this.chatActivity;
            this.avatars.setVisibility(!((chatActivityInterface == null || chatActivityInterface.getGroupCall() == null || this.chatActivity.getGroupCall().call == null || !this.chatActivity.getGroupCall().call.rtmp_stream) ? false : true) ? 0 : 8);
            if (this.avatars.getVisibility() != 8) {
                updateAvatars(false);
            } else {
                this.titleTextView.setTranslationX(-AndroidUtilities.dp(36.0f));
                this.subtitleTextView.setTranslationX(-AndroidUtilities.dp(36.0f));
            }
            this.closeButton.setVisibility(8);
            this.playButton.setVisibility(8);
            actionBarMenuItem = this.playbackSpeedButton;
            if (actionBarMenuItem == null) {
                return;
            }
        } else {
            if (i != 1 && i != 3) {
                return;
            }
            this.selector.setBackground(null);
            updateCallTitle();
            boolean hasRtmpStream = VoIPService.hasRtmpStream();
            this.avatars.setVisibility(!hasRtmpStream ? 0 : 8);
            if (i == 3 && VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().registerStateListener(this);
            }
            if (this.avatars.getVisibility() != 8) {
                updateAvatars(false);
            } else {
                this.titleTextView.setTranslationX(0.0f);
                this.subtitleTextView.setTranslationX(0.0f);
            }
            this.muteButton.setVisibility(!hasRtmpStream ? 0 : 8);
            boolean z = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
            this.isMuted = z;
            this.muteDrawable.setCustomEndFrame(z ? 15 : 29);
            RLottieDrawable rLottieDrawable = this.muteDrawable;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
            this.muteButton.invalidate();
            this.frameLayout.setBackground(null);
            this.frameLayout.setBackgroundColor(0);
            this.importingImageView.setVisibility(8);
            this.importingImageView.stopAnimation();
            Theme.getFragmentContextViewWavesDrawable().addParent(this);
            invalidate();
            int i10 = 0;
            while (i10 < 2) {
                AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher4 = this.titleTextView;
                TextView textView4 = i10 == 0 ? clippingTextViewSwitcher4.getTextView() : clippingTextViewSwitcher4.getNextTextView();
                if (textView4 != null) {
                    textView4.setGravity(19);
                    textView4.setTextColor(getThemedColor(Theme.key_returnToCallText));
                    textView4.setTypeface(AndroidUtilities.bold());
                    textView4.setTextSize(1, 14.0f);
                }
                i10++;
            }
            this.titleTextView.setTag(Integer.valueOf(Theme.key_returnToCallText));
            this.closeButton.setVisibility(8);
            this.playButton.setVisibility(8);
            this.subtitleTextView.setVisibility(8);
            this.joinButton.setVisibility(8);
            this.titleTextView.setLayoutParams(LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, 2.0f));
            this.titleTextView.setPadding(AndroidUtilities.dp(112.0f), 0, AndroidUtilities.dp(112.0f) + this.joinButtonWidth, 0);
            actionBarMenuItem = this.playbackSpeedButton;
            if (actionBarMenuItem == null) {
                return;
            }
        }
        actionBarMenuItem.setVisibility(8);
        this.playbackSpeedButton.setTag(null);
    }

    public void checkCall(boolean r17) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.FragmentContextView.checkCall(boolean):void");
    }

    public void checkImport(boolean z) {
        FrameLayout.LayoutParams layoutParams;
        int styleHeight;
        int i;
        if (this.chatActivity != null) {
            if (this.visible && ((i = this.currentStyle) == 1 || i == 3)) {
                return;
            }
            checkCreateView();
            SendMessagesHelper.ImportingHistory importingHistory = this.fragment.getSendMessagesHelper().getImportingHistory(this.chatActivity.getDialogId());
            View fragmentView = this.fragment.getFragmentView();
            if (!z && fragmentView != null && (fragmentView.getParent() == null || ((View) fragmentView.getParent()).getVisibility() != 0)) {
                z = true;
            }
            Dialog visibleDialog = this.fragment.getVisibleDialog();
            if ((isPlayingVoice() || this.chatActivity.shouldShowImport() || ((visibleDialog instanceof ImportingAlert) && !((ImportingAlert) visibleDialog).isDismissed())) && importingHistory != null) {
                importingHistory = null;
            }
            if (importingHistory == null) {
                if (!this.visible || ((!z || this.currentStyle != -1) && this.currentStyle != 5)) {
                    int i2 = this.currentStyle;
                    if (i2 == -1 || i2 == 5) {
                        this.visible = false;
                        setVisibility(8);
                        return;
                    }
                    return;
                }
                this.visible = false;
                if (z) {
                    if (getVisibility() != 8) {
                        setVisibility(8);
                    }
                    setTopPadding(0.0f);
                    return;
                }
                AnimatorSet animatorSet = this.animatorSet;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.animatorSet = null;
                }
                this.notificationsLocker.lock();
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animatorSet = animatorSet2;
                animatorSet2.playTogether(ObjectAnimator.ofFloat(this, "topPadding", 0.0f));
                this.animatorSet.setDuration(220L);
                this.animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        FragmentContextView.this.notificationsLocker.unlock();
                        if (FragmentContextView.this.animatorSet == null || !FragmentContextView.this.animatorSet.equals(animator)) {
                            return;
                        }
                        FragmentContextView.this.setVisibility(8);
                        FragmentContextView.this.animatorSet = null;
                        if (FragmentContextView.this.checkCallAfterAnimation) {
                            FragmentContextView.this.checkCall(false);
                        } else if (FragmentContextView.this.checkPlayerAfterAnimation) {
                            FragmentContextView.this.checkPlayer(false);
                        } else if (FragmentContextView.this.checkImportAfterAnimation) {
                            FragmentContextView.this.checkImport(false);
                        }
                        FragmentContextView.this.checkCallAfterAnimation = false;
                        FragmentContextView.this.checkPlayerAfterAnimation = false;
                        FragmentContextView.this.checkImportAfterAnimation = false;
                    }
                });
                this.animatorSet.start();
                return;
            }
            if (this.currentStyle != 5 && this.animatorSet != null && !z) {
                this.checkImportAfterAnimation = true;
                return;
            }
            updateStyle(5);
            if (z && this.topPadding == 0.0f) {
                updatePaddings();
                setTopPadding(AndroidUtilities.dp2(getStyleHeight()));
                FragmentContextViewDelegate fragmentContextViewDelegate = this.delegate;
                if (fragmentContextViewDelegate != null) {
                    fragmentContextViewDelegate.onAnimation(true, true);
                    this.delegate.onAnimation(false, true);
                }
            }
            if (!this.visible) {
                if (!z) {
                    AnimatorSet animatorSet3 = this.animatorSet;
                    if (animatorSet3 != null) {
                        animatorSet3.cancel();
                        this.animatorSet = null;
                    }
                    this.notificationsLocker.lock();
                    this.animatorSet = new AnimatorSet();
                    FragmentContextView fragmentContextView = this.additionalContextView;
                    if (fragmentContextView == null || fragmentContextView.getVisibility() != 0) {
                        layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
                        styleHeight = getStyleHeight();
                    } else {
                        layoutParams = (FrameLayout.LayoutParams) getLayoutParams();
                        styleHeight = getStyleHeight() + this.additionalContextView.getStyleHeight();
                    }
                    layoutParams.topMargin = -AndroidUtilities.dp(styleHeight);
                    FragmentContextViewDelegate fragmentContextViewDelegate2 = this.delegate;
                    if (fragmentContextViewDelegate2 != null) {
                        fragmentContextViewDelegate2.onAnimation(true, true);
                    }
                    this.animatorSet.playTogether(ObjectAnimator.ofFloat(this, "topPadding", AndroidUtilities.dp2(getStyleHeight())));
                    this.animatorSet.setDuration(200L);
                    this.animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            FragmentContextView.this.notificationsLocker.unlock();
                            if (FragmentContextView.this.animatorSet == null || !FragmentContextView.this.animatorSet.equals(animator)) {
                                return;
                            }
                            if (FragmentContextView.this.delegate != null) {
                                FragmentContextView.this.delegate.onAnimation(false, true);
                            }
                            FragmentContextView.this.animatorSet = null;
                            if (FragmentContextView.this.checkCallAfterAnimation) {
                                FragmentContextView.this.checkCall(false);
                            } else if (FragmentContextView.this.checkPlayerAfterAnimation) {
                                FragmentContextView.this.checkPlayer(false);
                            } else if (FragmentContextView.this.checkImportAfterAnimation) {
                                FragmentContextView.this.checkImport(false);
                            }
                            FragmentContextView.this.checkCallAfterAnimation = false;
                            FragmentContextView.this.checkPlayerAfterAnimation = false;
                            FragmentContextView.this.checkImportAfterAnimation = false;
                        }
                    });
                    this.animatorSet.start();
                }
                this.visible = true;
                setVisibility(0);
            }
            int i3 = this.currentProgress;
            int i4 = importingHistory.uploadProgress;
            if (i3 != i4) {
                this.currentProgress = i4;
                this.titleTextView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("ImportUploading", R.string.ImportUploading, Integer.valueOf(i4))), false);
            }
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        VoIPService sharedInstance;
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant;
        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher;
        String string;
        if (i == NotificationCenter.liveLocationsChanged) {
            checkLiveLocation(false);
            return;
        }
        if (i == NotificationCenter.liveLocationsCacheChanged) {
            if (this.chatActivity != null) {
                if (this.chatActivity.getDialogId() == ((Long) objArr[0]).longValue()) {
                    checkLocationString();
                    return;
                }
                return;
            }
            return;
        }
        if (i == NotificationCenter.messagePlayingDidStart || i == NotificationCenter.messagePlayingPlayStateChanged || i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.didEndCall) {
            int i3 = this.currentStyle;
            if (i3 == 1 || i3 == 3 || i3 == 4) {
                checkCall(false);
            }
            checkPlayer(false);
            return;
        }
        int i4 = NotificationCenter.didStartedCall;
        if (i == i4 || i == NotificationCenter.groupCallUpdated || i == NotificationCenter.groupCallVisibilityChanged) {
            checkCall(false);
            if (this.currentStyle != 3 || (sharedInstance = VoIPService.getSharedInstance()) == null || sharedInstance.groupCall == null) {
                return;
            }
            if (i == i4) {
                sharedInstance.registerStateListener(this);
            }
            int callState = sharedInstance.getCallState();
            if (callState == 1 || callState == 2 || callState == 6 || callState == 5 || this.muteButton == null || (tL_groupCallParticipant = (TLRPC.TL_groupCallParticipant) sharedInstance.groupCall.participants.get(sharedInstance.getSelfId())) == null || tL_groupCallParticipant.can_self_unmute || !tL_groupCallParticipant.muted || ChatObject.canManageCalls(sharedInstance.getChat())) {
                return;
            }
            sharedInstance.setMicMute(true, false, false);
            long uptimeMillis = SystemClock.uptimeMillis();
            this.muteButton.dispatchTouchEvent(MotionEvent.obtain(uptimeMillis, uptimeMillis, 3, 0.0f, 0.0f, 0));
            return;
        }
        if (i == NotificationCenter.groupCallTypingsUpdated) {
            checkCreateView();
            if (this.visible && this.currentStyle == 4) {
                ChatObject.Call groupCall = this.chatActivity.getGroupCall();
                if (groupCall != null && this.subtitleTextView != null) {
                    if (groupCall.isScheduled()) {
                        clippingTextViewSwitcher = this.subtitleTextView;
                        string = LocaleController.formatStartsTime(groupCall.call.schedule_date, 4);
                    } else {
                        TLRPC.GroupCall groupCall2 = groupCall.call;
                        int i5 = groupCall2.participants_count;
                        if (i5 == 0) {
                            clippingTextViewSwitcher = this.subtitleTextView;
                            string = LocaleController.getString(groupCall2.rtmp_stream ? R.string.ViewersWatchingNobody : R.string.MembersTalkingNobody);
                        } else {
                            this.subtitleTextView.setText(LocaleController.formatPluralString(groupCall2.rtmp_stream ? "ViewersWatching" : "Participants", i5, new Object[0]), false);
                        }
                    }
                    clippingTextViewSwitcher.setText(string, false);
                }
                updateAvatars(true);
                return;
            }
            return;
        }
        if (i == NotificationCenter.historyImportProgressChanged) {
            int i6 = this.currentStyle;
            if (i6 == 1 || i6 == 3 || i6 == 4) {
                checkCall(false);
            }
            checkImport(false);
            return;
        }
        if (i == NotificationCenter.messagePlayingSpeedChanged) {
            updatePlaybackButton(true);
            return;
        }
        if (i == NotificationCenter.webRtcMicAmplitudeEvent) {
            if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute()) {
                this.micAmplitude = 0.0f;
            } else {
                this.micAmplitude = Math.min(8500.0f, ((Float) objArr[0]).floatValue() * 4000.0f) / 8500.0f;
            }
            if (VoIPService.getSharedInstance() != null) {
                Theme.getFragmentContextViewWavesDrawable().setAmplitude(Math.max(this.speakerAmplitude, this.micAmplitude));
                return;
            }
            return;
        }
        if (i == NotificationCenter.webRtcSpeakerAmplitudeEvent) {
            checkCreateView();
            this.speakerAmplitude = Math.max(0.0f, Math.min((((Float) objArr[0]).floatValue() * 15.0f) / 80.0f, 1.0f));
            if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute()) {
                this.micAmplitude = 0.0f;
            }
            if (VoIPService.getSharedInstance() != null) {
                Theme.getFragmentContextViewWavesDrawable().setAmplitude(Math.max(this.speakerAmplitude, this.micAmplitude));
            }
            this.avatars.invalidate();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        boolean z;
        if (this.frameLayout == null) {
            return;
        }
        if (!this.drawOverlay || getVisibility() == 0) {
            int i = this.currentStyle;
            if (i == 3 || i == 1) {
                Theme.getFragmentContextViewWavesDrawable().updateState(this.wasDraw);
                float dp = this.topPadding / AndroidUtilities.dp(getStyleHeight());
                if (this.collapseTransition) {
                    Theme.getFragmentContextViewWavesDrawable().draw(0.0f, (AndroidUtilities.dp(getStyleHeight()) - this.topPadding) + this.extraHeight, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(2.0f), canvas, null, Math.min(dp, 1.0f - this.collapseProgress));
                } else {
                    Theme.getFragmentContextViewWavesDrawable().draw(0.0f, AndroidUtilities.dp(getStyleHeight()) - this.topPadding, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(2.0f), canvas, this, dp);
                }
                float dp2 = AndroidUtilities.dp(getStyleHeight()) - this.topPadding;
                if (this.collapseTransition) {
                    dp2 += this.extraHeight;
                }
                if (dp2 > getMeasuredHeight()) {
                    return;
                }
                canvas.save();
                canvas.clipRect(0.0f, dp2, getMeasuredWidth(), getMeasuredHeight());
                invalidate();
                z = true;
            } else {
                z = false;
            }
            super.dispatchDraw(canvas);
            if (z) {
                canvas.restore();
            }
            this.wasDraw = true;
        }
    }

    public int getStyleHeight() {
        return this.currentStyle == 4 ? 48 : 36;
    }

    public float getTopPadding() {
        return this.topPadding;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        int i = this.currentStyle;
        if ((i == 3 || i == 1) && getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    public boolean isCallStyle() {
        int i = this.currentStyle;
        return i == 3 || i == 1;
    }

    public boolean isCallTypeVisible() {
        int i = this.currentStyle;
        return (i == 1 || i == 3) && this.visible;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.isLocation) {
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsChanged);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.liveLocationsCacheChanged);
            FragmentContextView fragmentContextView = this.additionalContextView;
            if (fragmentContextView != null) {
                fragmentContextView.checkVisibility();
            }
            checkLiveLocation(true);
        } else {
            for (int i = 0; i < 4; i++) {
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidStart);
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.groupCallUpdated);
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.groupCallTypingsUpdated);
                NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.historyImportProgressChanged);
            }
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.messagePlayingSpeedChanged);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didStartedCall);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didEndCall);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.webRtcSpeakerAmplitudeEvent);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
            NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.groupCallVisibilityChanged);
            FragmentContextView fragmentContextView2 = this.additionalContextView;
            if (fragmentContextView2 != null) {
                fragmentContextView2.checkVisibility();
            }
            if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isHangingUp() || VoIPService.getSharedInstance().getCallState() == 15 || GroupCallPip.isShowing()) {
                if (this.chatActivity == null || this.fragment.getSendMessagesHelper().getImportingHistory(this.chatActivity.getDialogId()) == null || isPlayingVoice()) {
                    ChatActivityInterface chatActivityInterface = this.chatActivity;
                    if (chatActivityInterface == null || chatActivityInterface.getGroupCall() == null || !this.chatActivity.getGroupCall().shouldShowPanel() || GroupCallPip.isShowing() || isPlayingVoice()) {
                        checkCall(true);
                        checkPlayer(true);
                        updatePlaybackButton(false);
                    }
                } else {
                    checkImport(true);
                }
            }
            checkCall(true);
        }
        int i2 = this.currentStyle;
        if (i2 == 3 || i2 == 1) {
            Theme.getFragmentContextViewWavesDrawable().addParent(this);
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().registerStateListener(this);
            }
            boolean z = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
            if (this.isMuted != z && this.muteButton != null) {
                this.isMuted = z;
                this.muteDrawable.setCustomEndFrame(z ? 15 : 29);
                RLottieDrawable rLottieDrawable = this.muteDrawable;
                rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
                this.muteButton.invalidate();
            }
        } else if (i2 == 4 && !this.scheduleRunnableScheduled) {
            this.scheduleRunnableScheduled = true;
            this.updateScheduleTimeRunnable.run();
        }
        if (this.visible && this.topPadding == 0.0f) {
            updatePaddings();
            setTopPadding(AndroidUtilities.dp2(getStyleHeight()));
        }
        this.speakerAmplitude = 0.0f;
        this.micAmplitude = 0.0f;
    }

    @Override
    public void onAudioSettingsChanged() {
        boolean z = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
        if (this.isMuted != z) {
            this.isMuted = z;
            this.muteDrawable.setCustomEndFrame(z ? 15 : 29);
            RLottieDrawable rLottieDrawable = this.muteDrawable;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
            this.muteButton.invalidate();
            Theme.getFragmentContextViewWavesDrawable().updateState(this.visible);
        }
        if (this.isMuted) {
            this.micAmplitude = 0.0f;
            Theme.getFragmentContextViewWavesDrawable().setAmplitude(0.0f);
        }
    }

    @Override
    public void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    @Override
    public void onCameraSwitch(boolean z) {
        VoIPService.StateListener.CC.$default$onCameraSwitch(this, z);
    }

    @Override
    protected void onDetachedFromWindow() {
        NotificationCenter globalInstance;
        int i;
        super.onDetachedFromWindow();
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        if (this.scheduleRunnableScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.updateScheduleTimeRunnable);
            this.scheduleRunnableScheduled = false;
        }
        this.visible = false;
        this.notificationsLocker.unlock();
        this.topPadding = 0.0f;
        if (this.isLocation) {
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.liveLocationsChanged);
            globalInstance = NotificationCenter.getGlobalInstance();
            i = NotificationCenter.liveLocationsCacheChanged;
        } else {
            for (int i2 = 0; i2 < 4; i2++) {
                NotificationCenter.getInstance(i2).removeObserver(this, NotificationCenter.messagePlayingDidReset);
                NotificationCenter.getInstance(i2).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
                NotificationCenter.getInstance(i2).removeObserver(this, NotificationCenter.messagePlayingDidStart);
                NotificationCenter.getInstance(i2).removeObserver(this, NotificationCenter.groupCallUpdated);
                NotificationCenter.getInstance(i2).removeObserver(this, NotificationCenter.groupCallTypingsUpdated);
                NotificationCenter.getInstance(i2).removeObserver(this, NotificationCenter.historyImportProgressChanged);
            }
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.messagePlayingSpeedChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didStartedCall);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcSpeakerAmplitudeEvent);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
            globalInstance = NotificationCenter.getGlobalInstance();
            i = NotificationCenter.groupCallVisibilityChanged;
        }
        globalInstance.removeObserver(this, i);
        int i3 = this.currentStyle;
        if (i3 == 3 || i3 == 1) {
            Theme.getFragmentContextViewWavesDrawable().removeParent(this);
        }
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().unregisterStateListener(this);
        }
        this.wasDraw = false;
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, AndroidUtilities.dp2(getStyleHeight() + 2));
    }

    @Override
    public void onMediaStateUpdated(int i, int i2) {
        VoIPService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    public void onPanTranslationUpdate(float f) {
        HintView hintView = this.speedHintView;
        if (hintView != null) {
            hintView.setExtraTranslationY(AndroidUtilities.dp(72.0f) + f);
        }
    }

    @Override
    public void onScreenOnChange(boolean z) {
        VoIPService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    @Override
    public void onSignalBarsCountChanged(int i) {
        VoIPService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    @Override
    public void onStateChanged(int i) {
        updateCallTitle();
    }

    @Override
    public void onVideoAvailableChange(boolean z) {
        VoIPService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    public void setAdditionalContextView(FragmentContextView fragmentContextView) {
        this.additionalContextView = fragmentContextView;
    }

    public void setCollapseTransition(boolean z, float f, float f2) {
        this.collapseTransition = z;
        this.extraHeight = f;
        this.collapseProgress = f2;
    }

    public void setDelegate(FragmentContextViewDelegate fragmentContextViewDelegate) {
        this.delegate = fragmentContextViewDelegate;
    }

    public void setDrawOverlay(boolean z) {
        this.drawOverlay = z;
    }

    public void setSupportsCalls(boolean z) {
        this.supportsCalls = z;
    }

    public void setTopPadding(float f) {
        this.topPadding = f;
        if (this.fragment == null || getParent() == null) {
            return;
        }
        View view = this.applyingView;
        if (view == null) {
            view = this.fragment.getFragmentView();
        }
        FragmentContextView fragmentContextView = this.additionalContextView;
        int dp = (fragmentContextView == null || fragmentContextView.getVisibility() != 0 || this.additionalContextView.getParent() == null) ? 0 : AndroidUtilities.dp(this.additionalContextView.getStyleHeight());
        if (view == null || getParent() == null) {
            return;
        }
        view.setPadding(0, ((int) (getVisibility() == 0 ? this.topPadding : 0.0f)) + dp, 0, 0);
    }

    @Override
    public void setVisibility(int i) {
        super.setVisibility(i);
        updatePaddings();
        setTopPadding(this.topPadding);
        if (i == 8) {
            this.wasDraw = false;
        }
    }

    public void toggleScheduledNotify() {
        ChatActivityInterface chatActivityInterface;
        ChatObject.Call groupCall;
        if (this.fragment == null || (chatActivityInterface = this.chatActivity) == null || (groupCall = chatActivityInterface.getGroupCall()) == null || groupCall.call == null) {
            return;
        }
        if (this.toggleGroupCallStartSubscriptionReqId != 0) {
            this.fragment.getConnectionsManager().cancelRequest(this.toggleGroupCallStartSubscriptionReqId, true);
            this.toggleGroupCallStartSubscriptionReqId = 0;
        }
        TL_phone.toggleGroupCallStartSubscription togglegroupcallstartsubscription = new TL_phone.toggleGroupCallStartSubscription();
        togglegroupcallstartsubscription.call = groupCall.getInputGroupCall();
        TLRPC.GroupCall groupCall2 = groupCall.call;
        boolean z = true ^ this.willBeNotified;
        this.willBeNotified = z;
        groupCall2.schedule_start_subscribed = z;
        togglegroupcallstartsubscription.subscribed = z;
        this.toggleGroupCallStartSubscriptionReqId = this.fragment.getConnectionsManager().sendRequest(togglegroupcallstartsubscription, null);
        if (this.scheduleRunnableScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.updateScheduleTimeRunnable);
            this.scheduleRunnableScheduled = false;
        }
        this.updateScheduleTimeRunnable.run();
        BulletinFactory of = BulletinFactory.of(this.fragment);
        boolean z2 = this.willBeNotified;
        of.createSimpleBulletin(z2 ? R.raw.silent_unmute : R.raw.silent_mute, LocaleController.getString(z2 ? R.string.LiveStreamWillNotify : R.string.LiveStreamWillNotNotify)).show();
    }

    public void updateColors() {
        int themedColor = getThemedColor(!equals(MediaController.getInstance().getPlaybackSpeed(this.isMusic), 1.0f) ? Theme.key_featuredStickers_addButtonPressed : Theme.key_inappPlayerClose);
        SpeedIconDrawable speedIconDrawable = this.speedIcon;
        if (speedIconDrawable != null) {
            speedIconDrawable.setColor(themedColor);
        }
        ActionBarMenuItem actionBarMenuItem = this.playbackSpeedButton;
        if (actionBarMenuItem == null || Build.VERSION.SDK_INT < 21) {
            return;
        }
        actionBarMenuItem.setBackground(Theme.createSelectorDrawable(themedColor & 436207615, 1, AndroidUtilities.dp(14.0f)));
    }
}
