package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.PowerManager;
import android.text.Layout;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.EncryptionKeyEmojifier;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.messenger.voip.VoIPServiceState;
import org.telegram.messenger.voip.VoipAudioManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_phone;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.DarkAlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.voip.AcceptDeclineView;
import org.telegram.ui.Components.voip.EmojiRationalLayout;
import org.telegram.ui.Components.voip.EndCloseLayout;
import org.telegram.ui.Components.voip.HideEmojiTextView;
import org.telegram.ui.Components.voip.ImageWithWavesView;
import org.telegram.ui.Components.voip.PrivateVideoPreviewDialogNew;
import org.telegram.ui.Components.voip.RateCallLayout;
import org.telegram.ui.Components.voip.VoIPBackgroundProvider;
import org.telegram.ui.Components.voip.VoIPButtonsLayout;
import org.telegram.ui.Components.voip.VoIPFloatingLayout;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.Components.voip.VoIPNotificationsLayout;
import org.telegram.ui.Components.voip.VoIPPiPView;
import org.telegram.ui.Components.voip.VoIPStatusTextView;
import org.telegram.ui.Components.voip.VoIPTextureView;
import org.telegram.ui.Components.voip.VoIPToggleButton;
import org.telegram.ui.Components.voip.VoIPWindowView;
import org.telegram.ui.Components.voip.VoIpCoverView;
import org.telegram.ui.Components.voip.VoIpGradientLayout;
import org.telegram.ui.Components.voip.VoIpHintView;
import org.telegram.ui.Components.voip.VoIpSnowView;
import org.telegram.ui.Components.voip.VoIpSwitchLayout;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.VoIPFragment;
import org.webrtc.EglBase;
import org.webrtc.GlRectDrawer;
import org.webrtc.RendererCommon;
import org.webrtc.TextureViewRenderer;

public class VoIPFragment implements VoIPService.StateListener, NotificationCenter.NotificationCenterDelegate {
    private static VoIPFragment instance;
    private AcceptDeclineView acceptDeclineView;
    private AccessibilityManager accessibilityManager;
    Activity activity;
    private ImageView backIcon;
    private VoIPToggleButton bottomEndCallBtn;
    private VoIpSwitchLayout bottomMuteBtn;
    View bottomShadow;
    private VoIpSwitchLayout bottomSpeakerBtn;
    private VoIpSwitchLayout bottomVideoBtn;
    private VoIPButtonsLayout buttonsLayout;
    TLRPC.User callingUser;
    boolean callingUserIsVideo;
    private VoIPFloatingLayout callingUserMiniFloatingLayout;
    private TextureViewRenderer callingUserMiniTextureRenderer;
    private ImageWithWavesView callingUserPhotoViewMini;
    private VoIPTextureView callingUserTextureView;
    private TextView callingUserTitle;
    boolean cameraForceExpanded;
    private Animator cameraShowingAnimator;
    private boolean canHideUI;
    private boolean canSwitchToPip;
    private boolean canZoomGesture;
    private final int currentAccount;
    private int currentState;
    TLRPC.User currentUser;
    private VoIPFloatingLayout currentUserCameraFloatingLayout;
    private boolean currentUserCameraIsFullscreen;
    boolean currentUserIsVideo;
    private VoIPTextureView currentUserTextureView;
    private boolean deviceIsLocked;
    private boolean emojiExpanded;
    LinearLayout emojiLayout;
    private boolean emojiLoaded;
    LinearLayout emojiRationalLayout;
    TextView emojiRationalTextView;
    TextView emojiRationalTopTextView;
    HintView2 encryptionTooltip;
    EndCloseLayout endCloseLayout;
    boolean enterFromPiP;
    private float enterTransitionProgress;
    float fillNaviagtionBarValue;
    private ViewGroup fragmentView;
    private VoIpGradientLayout gradientLayout;
    FrameLayout hideEmojiLayout;
    TextView hideEmojiTextView;
    boolean hideUiRunnableWaiting;
    private boolean isFinished;
    private boolean isInPinchToZoomTouchMode;
    private boolean isNearEar;
    boolean isOutgoing;
    private boolean isVideoCall;
    long lastContentTapTime;
    private WindowInsets lastInsets;
    private boolean lockOnScreen;
    VoIPNotificationsLayout notificationsLayout;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartCenterX;
    private float pinchStartCenterY;
    private float pinchStartDistance;
    private float pinchTranslationX;
    private float pinchTranslationY;
    private int pointerId1;
    private int pointerId2;
    private PrivateVideoPreviewDialogNew previewDialog;
    private int previousState;
    RateCallLayout rateCallLayout;
    private boolean screenWasWakeup;
    private int selectedRating;
    private boolean signalBarWasReceived;
    private ImageView speakerPhoneIcon;
    LinearLayout statusLayout;
    private int statusLayoutAnimateToOffset;
    private VoIPStatusTextView statusTextView;
    private boolean switchingToPip;
    HintView2 tapToVideoTooltip;
    View topShadow;
    float touchSlop;
    ValueAnimator uiVisibilityAnimator;
    private VoIpCoverView voIpCoverView;
    private VoIpSnowView voIpSnowView;
    private boolean wasEstablished;
    private VoIPWindowView windowView;
    ValueAnimator zoomBackAnimator;
    private boolean zoomStarted;
    private final VoIPBackgroundProvider backgroundProvider = new VoIPBackgroundProvider();
    BackupImageView[] emojiViews = new BackupImageView[4];
    Emoji.EmojiDrawable[] emojiDrawables = new Emoji.EmojiDrawable[4];
    Paint overlayPaint = new Paint();
    Paint overlayBottomPaint = new Paint();
    private boolean uiVisible = true;
    float uiVisibilityAlpha = 1.0f;
    AnimationNotificationsLocker notificationsLocker = new AnimationNotificationsLocker();
    ValueAnimator.AnimatorUpdateListener statusbarAnimatorListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
            VoIPFragment.this.lambda$new$0(valueAnimator);
        }
    };
    Runnable hideUIRunnable = new Runnable() {
        @Override
        public final void run() {
            VoIPFragment.this.lambda$new$1();
        }
    };
    Runnable stopAnimatingBgRunnable = new Runnable() {
        @Override
        public final void run() {
            VoIPFragment.this.lambda$new$2();
        }
    };
    float pinchScale = 1.0f;

    public class AnonymousClass12 implements RendererCommon.RendererEvents {
        AnonymousClass12() {
        }

        public void lambda$onFirstFrameRendered$0() {
            VoIPFragment.this.updateViewState();
        }

        @Override
        public void onFirstFrameRendered() {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    VoIPFragment.AnonymousClass12.this.lambda$onFirstFrameRendered$0();
                }
            });
        }

        @Override
        public void onFrameResolutionChanged(int i, int i2, int i3) {
        }
    }

    public class AnonymousClass13 implements RendererCommon.RendererEvents {
        AnonymousClass13() {
        }

        public void lambda$onFirstFrameRendered$0() {
            VoIPFragment.this.updateViewState();
        }

        @Override
        public void onFirstFrameRendered() {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    VoIPFragment.AnonymousClass13.this.lambda$onFirstFrameRendered$0();
                }
            });
        }

        @Override
        public void onFrameResolutionChanged(int i, int i2, int i3) {
        }
    }

    public class AnonymousClass14 extends AnimatorListenerAdapter {
        AnonymousClass14() {
        }

        public void lambda$onAnimationEnd$0() {
            VoIPFragment.this.notificationsLocker.unlock();
            VoIPPiPView.getInstance().onTransitionEnd();
            VoIPFragment.this.currentUserCameraFloatingLayout.setCornerRadius(-1.0f);
            VoIPFragment.this.callingUserTextureView.renderer.release();
            VoIPFragment.this.currentUserTextureView.renderer.release();
            VoIPFragment.this.callingUserMiniTextureRenderer.release();
            VoIPFragment.this.destroy();
            VoIPFragment.this.windowView.finishImmediate();
            VoIPPiPView.switchingToPip = false;
            VoIPFragment.this.switchingToPip = false;
            VoIPFragment unused = VoIPFragment.instance = null;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            VoIPPiPView.getInstance().windowView.setAlpha(1.0f);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    VoIPFragment.AnonymousClass14.this.lambda$onAnimationEnd$0();
                }
            }, 200L);
        }
    }

    public class AnonymousClass8 implements AcceptDeclineView.Listener {
        AnonymousClass8() {
        }

        public void lambda$onAccept$0() {
            if (VoIPService.getSharedState() != null) {
                VoIPService.getSharedState().acceptIncomingCall();
                if (!VoIPFragment.this.currentUserIsVideo || VoIPService.getSharedInstance() == null) {
                    return;
                }
                VoIPService.getSharedInstance().requestVideoCall(false);
            }
        }

        @Override
        public void onAccept() {
            int checkSelfPermission;
            if (VoIPFragment.this.currentState != 17) {
                if (Build.VERSION.SDK_INT >= 23) {
                    checkSelfPermission = VoIPFragment.this.activity.checkSelfPermission("android.permission.RECORD_AUDIO");
                    if (checkSelfPermission != 0) {
                        VoIPFragment.this.activity.requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
                        return;
                    }
                }
                if (VoIPService.getSharedState() != null) {
                    VoIPFragment.this.runAcceptCallAnimation(new Runnable() {
                        @Override
                        public final void run() {
                            VoIPFragment.AnonymousClass8.this.lambda$onAccept$0();
                        }
                    });
                    return;
                }
                return;
            }
            Intent intent = new Intent(VoIPFragment.this.activity, (Class<?>) VoIPService.class);
            intent.putExtra("user_id", VoIPFragment.this.callingUser.id);
            intent.putExtra("is_outgoing", true);
            intent.putExtra("start_incall_activity", false);
            intent.putExtra("video_call", VoIPFragment.this.isVideoCall);
            intent.putExtra("can_video_call", VoIPFragment.this.isVideoCall);
            intent.putExtra("account", VoIPFragment.this.currentAccount);
            try {
                VoIPFragment.this.activity.startService(intent);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }

        @Override
        public void onDecline() {
            if (VoIPFragment.this.currentState == 17 || VoIPService.getSharedState() == null) {
                VoIPFragment.this.windowView.finish();
            } else {
                VoIPService.getSharedState().declineIncomingCall();
            }
        }
    }

    public VoIPFragment(int i) {
        this.currentAccount = i;
        this.currentUser = MessagesController.getInstance(i).getUser(Long.valueOf(UserConfig.getInstance(i).getClientUserId()));
        VoIPServiceState sharedState = VoIPService.getSharedState();
        if (sharedState == null) {
            return;
        }
        this.callingUser = sharedState.getUser();
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().registerStateListener(this);
        }
        this.isOutgoing = sharedState.isOutgoing();
        this.previousState = -1;
        this.currentState = sharedState.getCallState();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.webRtcSpeakerAmplitudeEvent);
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.voipServiceCreated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.closeInCallActivity);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.nearEarEvent);
    }

    private void checkEmojiLoaded(boolean z) {
        int i = 0;
        for (int i2 = 0; i2 < 4; i2++) {
            Emoji.EmojiDrawable emojiDrawable = this.emojiDrawables[i2];
            if (emojiDrawable != null && emojiDrawable.isLoaded()) {
                i++;
            }
        }
        if (i == 4) {
            this.emojiLoaded = true;
            for (int i3 = 0; i3 < 4; i3++) {
                if (this.emojiViews[i3].getVisibility() != 0) {
                    this.emojiViews[i3].setVisibility(0);
                    if (z) {
                        this.emojiViews[i3].setAlpha(0.0f);
                        this.emojiViews[i3].setScaleX(0.0f);
                        this.emojiViews[i3].setScaleY(0.0f);
                        this.emojiViews[i3].animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_BACK).setDuration(250L).start();
                    }
                }
            }
            this.encryptionTooltip.postDelayed(new Runnable() {
                @Override
                public final void run() {
                    VoIPFragment.this.lambda$checkEmojiLoaded$32();
                }
            }, 1000L);
        }
    }

    public boolean checkPointerIds(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == motionEvent.getPointerId(0) && this.pointerId2 == motionEvent.getPointerId(1)) {
            return true;
        }
        return this.pointerId1 == motionEvent.getPointerId(1) && this.pointerId2 == motionEvent.getPointerId(0);
    }

    public static void clearInstance() {
        WindowInsets windowInsets;
        int systemWindowInsetTop;
        int systemWindowInsetBottom;
        if (instance != null) {
            if (VoIPService.getSharedInstance() != null) {
                int measuredHeight = instance.windowView.getMeasuredHeight();
                VoIPFragment voIPFragment = instance;
                if (voIPFragment.canSwitchToPip) {
                    VoIPPiPView.show(voIPFragment.activity, voIPFragment.currentAccount, voIPFragment.windowView.getMeasuredWidth(), measuredHeight, 0);
                    if (Build.VERSION.SDK_INT >= 20 && (windowInsets = instance.lastInsets) != null) {
                        systemWindowInsetTop = windowInsets.getSystemWindowInsetTop();
                        VoIPPiPView.topInset = systemWindowInsetTop;
                        systemWindowInsetBottom = instance.lastInsets.getSystemWindowInsetBottom();
                        VoIPPiPView.bottomInset = systemWindowInsetBottom;
                    }
                }
            }
            instance.callingUserTextureView.renderer.release();
            instance.currentUserTextureView.renderer.release();
            instance.callingUserMiniTextureRenderer.release();
            instance.destroy();
        }
        instance = null;
    }

    public void destroy() {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().unregisterStateListener(this);
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcSpeakerAmplitudeEvent);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.voipServiceCreated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.closeInCallActivity);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.nearEarEvent);
    }

    public void expandEmoji(boolean z) {
        ViewPropertyAnimator interpolator;
        if (this.emojiLoaded && this.emojiExpanded != z && this.uiVisible) {
            this.emojiExpanded = z;
            this.voIpCoverView.onEmojiExpanded(z);
            if (z) {
                if (SharedConfig.callEncryptionHintDisplayedCount < 2) {
                    SharedConfig.incrementCallEncryptionHintDisplayed(2);
                }
                this.encryptionTooltip.hide();
                AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
                this.hideUiRunnableWaiting = false;
                if (this.callingUserPhotoViewMini.getVisibility() == 0) {
                    this.callingUserPhotoViewMini.animate().setStartDelay(0L).translationY(AndroidUtilities.dp(48.0f)).scaleY(0.1f).scaleX(0.1f).alpha(0.0f).setDuration(200L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                }
                this.hideEmojiLayout.animate().setListener(null).cancel();
                this.hideEmojiLayout.setVisibility(0);
                this.hideEmojiLayout.setAlpha(0.0f);
                this.hideEmojiLayout.setScaleX(0.3f);
                this.hideEmojiLayout.setScaleY(0.3f);
                this.hideEmojiLayout.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setDuration(340L).setInterpolator(CubicBezierInterpolator.EASE_OUT).start();
                ViewPropertyAnimator translationY = this.emojiLayout.animate().scaleX(1.72f).scaleY(1.72f).translationY(AndroidUtilities.dp(140.0f));
                CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
                translationY.setInterpolator(cubicBezierInterpolator).setDuration(400L).start();
                this.emojiRationalLayout.animate().setListener(null).cancel();
                this.emojiRationalLayout.setVisibility(0);
                this.emojiRationalLayout.setTranslationY(-AndroidUtilities.dp(120.0f));
                this.emojiRationalLayout.setScaleX(0.7f);
                this.emojiRationalLayout.setScaleY(0.7f);
                this.emojiRationalLayout.setAlpha(0.0f);
                interpolator = this.emojiRationalLayout.animate().alpha(1.0f).translationY(0.0f).scaleX(1.0f).scaleY(1.0f).setDuration(400L).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        for (BackupImageView backupImageView : VoIPFragment.this.emojiViews) {
                            AnimatedEmojiDrawable animatedEmojiDrawable = backupImageView.animatedEmojiDrawable;
                            if (animatedEmojiDrawable != null && animatedEmojiDrawable.getImageReceiver() != null) {
                                backupImageView.animatedEmojiDrawable.getImageReceiver().setAllowStartAnimation(true);
                                backupImageView.animatedEmojiDrawable.getImageReceiver().startAnimation();
                            }
                        }
                    }
                }).setInterpolator(cubicBezierInterpolator);
            } else {
                if (this.callingUserPhotoViewMini.getVisibility() == 0) {
                    this.callingUserPhotoViewMini.animate().setStartDelay(50L).translationY(0.0f).scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setDuration(250L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                }
                this.hideEmojiLayout.animate().setListener(null).cancel();
                ViewPropertyAnimator duration = this.hideEmojiLayout.animate().alpha(0.0f).scaleY(0.3f).scaleX(0.3f).setDuration(230L);
                CubicBezierInterpolator cubicBezierInterpolator2 = CubicBezierInterpolator.DEFAULT;
                duration.setInterpolator(cubicBezierInterpolator2).setListener(new HideViewAfterAnimation(this.hideEmojiLayout)).start();
                this.emojiLayout.animate().scaleX(1.0f).scaleY(1.0f).translationY(0.0f).setInterpolator(cubicBezierInterpolator2).setDuration(280L).start();
                this.emojiRationalLayout.animate().setListener(null).cancel();
                interpolator = this.emojiRationalLayout.animate().alpha(0.0f).scaleY(0.7f).scaleX(0.7f).translationY(-AndroidUtilities.dp(120.0f)).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        VoIPFragment.this.startWaitingFoHideUi();
                        for (BackupImageView backupImageView : VoIPFragment.this.emojiViews) {
                            AnimatedEmojiDrawable animatedEmojiDrawable = backupImageView.animatedEmojiDrawable;
                            if (animatedEmojiDrawable != null && animatedEmojiDrawable.getImageReceiver() != null) {
                                backupImageView.animatedEmojiDrawable.getImageReceiver().setAllowStartAnimation(false);
                                backupImageView.animatedEmojiDrawable.getImageReceiver().stopAnimation();
                            }
                        }
                        VoIPFragment.this.emojiRationalLayout.setVisibility(8);
                    }
                }).setDuration(250L).setInterpolator(cubicBezierInterpolator2);
            }
            interpolator.start();
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    public void finishZoom() {
        if (this.zoomStarted) {
            this.zoomStarted = false;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
            this.zoomBackAnimator = ofFloat;
            final float f = this.pinchScale;
            final float f2 = this.pinchTranslationX;
            final float f3 = this.pinchTranslationY;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    VoIPFragment.this.lambda$finishZoom$15(f, f2, f3, valueAnimator);
                }
            });
            this.zoomBackAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    VoIPFragment voIPFragment = VoIPFragment.this;
                    voIPFragment.zoomBackAnimator = null;
                    voIPFragment.pinchScale = 1.0f;
                    voIPFragment.pinchTranslationX = 0.0f;
                    VoIPFragment.this.pinchTranslationY = 0.0f;
                    VoIPFragment.this.fragmentView.invalidate();
                }
            });
            this.zoomBackAnimator.setDuration(350L);
            this.zoomBackAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.zoomBackAnimator.start();
        }
        this.canZoomGesture = false;
        this.isInPinchToZoomTouchMode = false;
    }

    public VoIPTextureView getFullscreenTextureView() {
        return this.callingUserIsVideo ? this.callingUserTextureView : this.currentUserTextureView;
    }

    public static VoIPFragment getInstance() {
        return instance;
    }

    private void initRenderers() {
        this.currentUserTextureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new AnonymousClass12());
        this.callingUserTextureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new AnonymousClass13(), EglBase.CONFIG_PLAIN, new GlRectDrawer());
        this.callingUserMiniTextureRenderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), null);
    }

    public void lambda$checkEmojiLoaded$32() {
        if (SharedConfig.callEncryptionHintDisplayedCount < 2) {
            SharedConfig.incrementCallEncryptionHintDisplayed(1);
            this.encryptionTooltip.setTranslationY(this.emojiLayout.getY() + AndroidUtilities.dp(36.0f));
            this.encryptionTooltip.show();
        }
    }

    public void lambda$createPiPTransition$18(boolean z, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, float f9, float f10, float f11, float f12, float f13, float f14, float f15, float f16, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f17 = 1.0f - floatValue;
        this.enterTransitionProgress = f17;
        updateSystemBarColors();
        if (z) {
            float f18 = (f * f17) + (f2 * floatValue);
            this.currentUserCameraFloatingLayout.setScaleX(f18);
            this.currentUserCameraFloatingLayout.setScaleY(f18);
            this.currentUserCameraFloatingLayout.setTranslationX((f3 * f17) + (f4 * floatValue));
            this.currentUserCameraFloatingLayout.setTranslationY((f5 * f17) + (f6 * floatValue));
            this.currentUserCameraFloatingLayout.setCornerRadius((f7 * f17) + (f8 * floatValue));
            this.currentUserCameraFloatingLayout.setAlpha((f9 * f17) + (f10 * floatValue));
        }
        float f19 = (f11 * f17) + (f12 * floatValue);
        this.callingUserTextureView.setScaleX(f19);
        this.callingUserTextureView.setScaleY(f19);
        this.callingUserTextureView.setTranslationX((f13 * f17) + (f14 * floatValue));
        this.callingUserTextureView.setTranslationY((f17 * f15) + (f16 * floatValue));
        this.callingUserTextureView.setRoundCorners(((AndroidUtilities.dp(4.0f) * floatValue) * 1.0f) / f19);
        if (!this.currentUserCameraFloatingLayout.measuredAsFloatingMode) {
            this.currentUserTextureView.setScreenshareMiniProgress(floatValue, false);
        }
        this.windowView.invalidate();
    }

    public void lambda$createView$10(View view) {
        VoIPService sharedInstance;
        if (this.speakerPhoneIcon.getTag() == null || (sharedInstance = VoIPService.getSharedInstance()) == null) {
            return;
        }
        startWaitingFoHideUi();
        sharedInstance.toggleSpeakerphoneOrShowRouteSheet(this.activity, false, Integer.valueOf(sharedInstance.isBluetoothOn() ? 2 : sharedInstance.isSpeakerphoneOn() ? 0 : 1));
    }

    public void lambda$createView$11(View view) {
        if (this.lockOnScreen) {
            return;
        }
        onBackPressed();
    }

    public void lambda$createView$12() {
        this.previousState = this.currentState;
        updateViewState();
    }

    public void lambda$createView$5(float f, boolean z) {
        this.currentUserTextureView.setScreenshareMiniProgress(f, z);
    }

    public void lambda$createView$6(View view) {
        if (this.currentUserIsVideo && this.callingUserIsVideo && System.currentTimeMillis() - this.lastContentTapTime > 500) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            this.lastContentTapTime = System.currentTimeMillis();
            this.callingUserMiniFloatingLayout.setRelativePosition(this.currentUserCameraFloatingLayout);
            this.currentUserCameraIsFullscreen = true;
            this.cameraForceExpanded = true;
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    public void lambda$createView$7(View view) {
        if (!this.cameraForceExpanded || System.currentTimeMillis() - this.lastContentTapTime <= 500) {
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
        this.hideUiRunnableWaiting = false;
        this.lastContentTapTime = System.currentTimeMillis();
        this.currentUserCameraFloatingLayout.setRelativePosition(this.callingUserMiniFloatingLayout);
        this.currentUserCameraIsFullscreen = false;
        this.cameraForceExpanded = false;
        this.previousState = this.currentState;
        updateViewState();
    }

    public void lambda$createView$8(View view) {
        if (System.currentTimeMillis() - this.lastContentTapTime < 500) {
            return;
        }
        this.lastContentTapTime = System.currentTimeMillis();
        boolean z = this.emojiExpanded;
        if (!z && this.emojiLoaded) {
            expandEmoji(!z);
        }
    }

    public void lambda$createView$9(View view) {
        if (System.currentTimeMillis() - this.lastContentTapTime < 500) {
            return;
        }
        this.lastContentTapTime = System.currentTimeMillis();
        if (this.emojiLoaded) {
            expandEmoji(!this.emojiExpanded);
        }
    }

    public void lambda$finishZoom$15(float f, float f2, float f3, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.pinchScale = (f * floatValue) + ((1.0f - floatValue) * 1.0f);
        this.pinchTranslationX = f2 * floatValue;
        this.pinchTranslationY = f3 * floatValue;
        this.fragmentView.invalidate();
    }

    public void lambda$new$0(ValueAnimator valueAnimator) {
        this.uiVisibilityAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateSystemBarColors();
    }

    public void lambda$new$1() {
        this.hideUiRunnableWaiting = false;
        HintView2 hintView2 = this.tapToVideoTooltip;
        boolean z = hintView2 != null && hintView2.shown();
        if (!this.canHideUI || !this.uiVisible || this.emojiExpanded || z) {
            return;
        }
        this.lastContentTapTime = System.currentTimeMillis();
        showUi(false);
        this.previousState = this.currentState;
        updateViewState();
    }

    public void lambda$new$2() {
        if (this.currentState == 3) {
            this.callingUserPhotoViewMini.setMute(true, false);
            this.gradientLayout.pause();
        }
    }

    public static void lambda$onRequestPermissionsResultInternal$39() {
        if (VoIPService.getSharedState() != null) {
            VoIPService.getSharedState().acceptIncomingCall();
        }
    }

    public void lambda$onRequestPermissionsResultInternal$40() {
        this.windowView.finish();
    }

    public void lambda$onSignalBarsCountChanged$4(int i) {
        this.statusTextView.setSignalBarCount(i);
        if (i <= 1) {
            this.gradientLayout.showToBadConnection();
            this.statusTextView.showBadConnection(true, true);
        } else {
            this.gradientLayout.hideBadConnection();
            this.statusTextView.showBadConnection(false, true);
        }
    }

    public void lambda$requestInlinePermissions$42(AlertDialog alertDialog, int i) {
        VoIPWindowView voIPWindowView = this.windowView;
        if (voIPWindowView != null) {
            voIPWindowView.finish();
        }
    }

    public void lambda$runAcceptCallAnimation$13(int i, int i2, int i3, ViewGroup.MarginLayoutParams marginLayoutParams, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.acceptDeclineView.setTranslationY(i * floatValue);
        int i4 = (int) (i2 - ((i2 + i3) * floatValue));
        marginLayoutParams.leftMargin = i4;
        marginLayoutParams.rightMargin = i4;
        this.acceptDeclineView.requestLayout();
    }

    public void lambda$runAcceptCallAnimation$14() {
        int[] iArr = new int[2];
        this.acceptDeclineView.getLocationOnScreen(iArr);
        int i = iArr[0];
        int i2 = iArr[1];
        this.bottomSpeakerBtn.getLocationOnScreen(iArr);
        this.bottomSpeakerBtn.setTranslationX((i - iArr[0]) + AndroidUtilities.dp(42.0f));
        this.bottomSpeakerBtn.setTranslationY((i2 - iArr[1]) + AndroidUtilities.dp(44.0f));
        this.bottomMuteBtn.getLocationOnScreen(iArr);
        this.bottomMuteBtn.setTranslationX((i - iArr[0]) + AndroidUtilities.dp(42.0f));
        this.bottomMuteBtn.setTranslationY((i2 - iArr[1]) + AndroidUtilities.dp(44.0f));
        this.bottomVideoBtn.getLocationOnScreen(iArr);
        this.bottomVideoBtn.setTranslationX((i - iArr[0]) + AndroidUtilities.dp(42.0f));
        this.bottomVideoBtn.setTranslationY((i2 - iArr[1]) + AndroidUtilities.dp(44.0f));
        this.bottomEndCallBtn.getLocationOnScreen(iArr);
        this.bottomEndCallBtn.setTranslationX((((i + this.acceptDeclineView.getWidth()) - iArr[0]) - AndroidUtilities.dp(49.0f)) - AndroidUtilities.dp(60.0f));
        this.bottomEndCallBtn.setTranslationY((i2 - iArr[1]) + AndroidUtilities.dp(44.0f));
        this.bottomEndCallBtn.setAlpha(1.0f);
        this.bottomSpeakerBtn.setAlpha(1.0f);
        this.bottomMuteBtn.setAlpha(1.0f);
        this.bottomVideoBtn.setAlpha(1.0f);
        long j = 200;
        this.bottomEndCallBtn.animate().setStartDelay(0L).translationY(0.0f).setInterpolator(new LinearInterpolator()).translationX(0.0f).setDuration(j).start();
        this.bottomSpeakerBtn.animate().setStartDelay(0L).translationY(0.0f).setInterpolator(new LinearInterpolator()).translationX(0.0f).setDuration(j).start();
        this.bottomMuteBtn.animate().setStartDelay(0L).translationY(0.0f).setInterpolator(new LinearInterpolator()).translationX(0.0f).setDuration(j).start();
        this.bottomVideoBtn.animate().setStartDelay(0L).translationY(0.0f).setInterpolator(new LinearInterpolator()).translationX(0.0f).setDuration(j).start();
    }

    public void lambda$setFrontalCameraAction$38(VoIPService voIPService, VoIpSwitchLayout voIpSwitchLayout, View view) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            if (this.accessibilityManager.isTouchExplorationEnabled()) {
                view.announceForAccessibility(LocaleController.getString(voIPService.isFrontFaceCamera() ? R.string.AccDescrVoipCamSwitchedToBack : R.string.AccDescrVoipCamSwitchedToFront));
            }
            voIpSwitchLayout.setType(VoIpSwitchLayout.Type.CAMERA, !voIPService.isFrontFaceCamera());
            sharedInstance.switchCamera();
        }
    }

    public void lambda$setMicrohoneAction$34(View view) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            boolean z = !sharedInstance.isMicMute();
            if (this.accessibilityManager.isTouchExplorationEnabled()) {
                view.announceForAccessibility(LocaleController.getString(z ? R.string.AccDescrVoipMicOff : R.string.AccDescrVoipMicOn));
            }
            sharedInstance.setMicMute(z, false, true);
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    public void lambda$setSpeakerPhoneAction$37(int i, VoIpSwitchLayout voIpSwitchLayout, VoIPService voIPService, View view) {
        if (VoIPService.getSharedInstance() != null) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(this.activity, false, Integer.valueOf(i));
            setSpeakerPhoneAction(voIpSwitchLayout, voIPService, true);
        }
    }

    public void lambda$setVideoAction$35(VoIPService voIPService, AlertDialog alertDialog, int i) {
        voIPService.sharedUIParams.cameraAlertWasShowed = true;
        toggleCameraInput();
    }

    public void lambda$setVideoAction$36(final VoIPService voIPService, View view) {
        TL_phone.PhoneCall phoneCall;
        int checkSelfPermission;
        AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
        this.hideUiRunnableWaiting = false;
        int i = Build.VERSION.SDK_INT;
        if (i >= 23) {
            checkSelfPermission = this.activity.checkSelfPermission("android.permission.CAMERA");
            if (checkSelfPermission != 0) {
                this.activity.requestPermissions(new String[]{"android.permission.CAMERA"}, 102);
                return;
            }
        }
        if (i >= 21 || (phoneCall = voIPService.privateCall) == null || phoneCall.video || this.callingUserIsVideo || voIPService.sharedUIParams.cameraAlertWasShowed) {
            toggleCameraInput();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this.activity);
        builder.setMessage(LocaleController.getString(R.string.VoipSwitchToVideoCall));
        builder.setPositiveButton(LocaleController.getString(R.string.VoipSwitch), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i2) {
                VoIPFragment.this.lambda$setVideoAction$35(voIPService, alertDialog, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.create().show();
    }

    public static WindowInsets lambda$show$3(VoIPFragment voIPFragment, View view, WindowInsets windowInsets) {
        WindowInsets consumeSystemWindowInsets;
        WindowInsets windowInsets2;
        int i = Build.VERSION.SDK_INT;
        if (i >= 21) {
            voIPFragment.setInsets(windowInsets);
        }
        if (i >= 30) {
            windowInsets2 = WindowInsets.CONSUMED;
            return windowInsets2;
        }
        consumeSystemWindowInsets = windowInsets.consumeSystemWindowInsets();
        return consumeSystemWindowInsets;
    }

    public void lambda$showErrorDialog$41(DialogInterface dialogInterface) {
        this.windowView.finish();
    }

    public void lambda$startTransitionFromPiP$16(Animator animator) {
        VoIPPiPView.switchingToPip = false;
        VoIPPiPView.finish();
        ViewPropertyAnimator duration = this.speakerPhoneIcon.animate().setDuration(150L);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        duration.setInterpolator(cubicBezierInterpolator).start();
        this.backIcon.animate().alpha(1.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
        this.emojiLayout.animate().alpha(1.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
        this.statusLayout.animate().alpha(1.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
        this.buttonsLayout.animate().alpha(1.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
        this.bottomShadow.animate().alpha(1.0f).setDuration(350L).setInterpolator(cubicBezierInterpolator).start();
        this.topShadow.animate().alpha(1.0f).setDuration(350L).setInterpolator(cubicBezierInterpolator).start();
        this.notificationsLayout.animate().alpha(1.0f).setDuration(350L).setInterpolator(cubicBezierInterpolator).start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator2) {
                VoIPFragment.this.notificationsLocker.unlock();
                VoIPFragment.this.currentUserCameraFloatingLayout.setCornerRadius(-1.0f);
                VoIPFragment.this.switchingToPip = false;
                VoIPFragment.this.currentUserCameraFloatingLayout.switchingToPip = false;
                VoIPFragment voIPFragment = VoIPFragment.this;
                voIPFragment.previousState = voIPFragment.currentState;
                VoIPFragment.this.updateViewState();
            }
        });
        animator.setDuration(350L);
        animator.setInterpolator(cubicBezierInterpolator);
        animator.start();
    }

    public void lambda$startTransitionFromPiP$17() {
        this.windowView.setAlpha(1.0f);
        this.windowView.invalidate();
        final Animator createPiPTransition = createPiPTransition(true);
        this.backIcon.setAlpha(0.0f);
        this.emojiLayout.setAlpha(0.0f);
        this.statusLayout.setAlpha(0.0f);
        this.buttonsLayout.setAlpha(0.0f);
        this.bottomShadow.setAlpha(0.0f);
        this.topShadow.setAlpha(0.0f);
        this.speakerPhoneIcon.setAlpha(0.0f);
        this.notificationsLayout.setAlpha(0.0f);
        this.currentUserCameraFloatingLayout.switchingToPip = true;
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VoIPFragment.this.lambda$startTransitionFromPiP$16(createPiPTransition);
            }
        }, 32L);
    }

    public void lambda$updateButtons$33(View view) {
        if (VoIPService.getSharedInstance() != null) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            VoIPService.getSharedInstance().hangUp();
        }
    }

    public void lambda$updateViewState$19() {
        this.windowView.finish();
    }

    public void lambda$updateViewState$20(VoIPService voIPService, View view) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VoIPFragment.this.lambda$updateViewState$19();
            }
        });
        int i = this.selectedRating;
        if (i > 0) {
            voIPService.sendCallRating(i);
        }
    }

    public void lambda$updateViewState$21(final VoIPService voIPService) {
        this.endCloseLayout.switchToClose(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VoIPFragment.this.lambda$updateViewState$20(voIPService, view);
            }
        }, true);
    }

    public void lambda$updateViewState$22() {
        this.windowView.finish();
    }

    public void lambda$updateViewState$23(VoIPService voIPService, View view) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VoIPFragment.this.lambda$updateViewState$22();
            }
        });
        int i = this.selectedRating;
        if (i > 0) {
            voIPService.sendCallRating(i);
        }
    }

    public void lambda$updateViewState$24(int i) {
        this.selectedRating = i;
    }

    public void lambda$updateViewState$25() {
        this.windowView.finish();
    }

    public void lambda$updateViewState$26(AlertDialog alertDialog, int i) {
        this.windowView.finish();
    }

    public void lambda$updateViewState$27(boolean[] zArr, AlertDialog alertDialog, int i) {
        zArr[0] = true;
        this.currentState = 17;
        Intent intent = new Intent(this.activity, (Class<?>) VoIPService.class);
        intent.putExtra("user_id", this.callingUser.id);
        intent.putExtra("is_outgoing", true);
        intent.putExtra("start_incall_activity", false);
        intent.putExtra("video_call", false);
        intent.putExtra("can_video_call", false);
        intent.putExtra("account", this.currentAccount);
        try {
            this.activity.startService(intent);
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public void lambda$updateViewState$28(boolean[] zArr, DialogInterface dialogInterface) {
        if (zArr[0]) {
            return;
        }
        this.windowView.finish();
    }

    public void lambda$updateViewState$29() {
        this.windowView.finish();
    }

    public void lambda$updateViewState$30() {
        this.windowView.finish();
    }

    public void lambda$updateViewState$31() {
        VoIPFloatingLayout voIPFloatingLayout = this.callingUserMiniFloatingLayout;
        voIPFloatingLayout.isAppearing = false;
        voIPFloatingLayout.invalidate();
    }

    public void onBackPressed() {
        if (this.isFinished || this.switchingToPip) {
            return;
        }
        PrivateVideoPreviewDialogNew privateVideoPreviewDialogNew = this.previewDialog;
        if (privateVideoPreviewDialogNew != null) {
            privateVideoPreviewDialogNew.dismiss(false, false);
            return;
        }
        if (this.callingUserIsVideo && this.currentUserIsVideo && this.cameraForceExpanded) {
            this.cameraForceExpanded = false;
            this.currentUserCameraFloatingLayout.setRelativePosition(this.callingUserMiniFloatingLayout);
            this.currentUserCameraIsFullscreen = false;
            this.previousState = this.currentState;
            updateViewState();
            return;
        }
        if (this.emojiExpanded) {
            expandEmoji(false);
            return;
        }
        if (this.emojiRationalLayout.getVisibility() != 8) {
            return;
        }
        if (!this.canSwitchToPip || this.lockOnScreen) {
            this.windowView.finish();
        } else if (AndroidUtilities.checkInlinePermissions(this.activity)) {
            switchToPip();
        } else {
            requestInlinePermissions();
        }
    }

    public static void onPause() {
        VoIPFragment voIPFragment = instance;
        if (voIPFragment != null) {
            voIPFragment.onPauseInternal();
        }
        if (VoIPPiPView.getInstance() != null) {
            VoIPPiPView.getInstance().onPause();
        }
    }

    public static void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        VoIPFragment voIPFragment = instance;
        if (voIPFragment != null) {
            voIPFragment.onRequestPermissionsResultInternal(i, strArr, iArr);
        }
    }

    private void onRequestPermissionsResultInternal(int i, String[] strArr, int[] iArr) {
        boolean shouldShowRequestPermissionRationale;
        if (i == 101) {
            if (VoIPService.getSharedState() == null) {
                this.windowView.finish();
                return;
            }
            if (iArr.length <= 0 || iArr[0] != 0) {
                shouldShowRequestPermissionRationale = this.activity.shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO");
                if (!shouldShowRequestPermissionRationale) {
                    if (VoIPService.getSharedState() != null) {
                        VoIPService.getSharedState().declineIncomingCall();
                    }
                    VoIPHelper.permissionDenied(this.activity, new Runnable() {
                        @Override
                        public final void run() {
                            VoIPFragment.this.lambda$onRequestPermissionsResultInternal$40();
                        }
                    }, i);
                    return;
                }
            } else {
                runAcceptCallAnimation(new Runnable() {
                    @Override
                    public final void run() {
                        VoIPFragment.lambda$onRequestPermissionsResultInternal$39();
                    }
                });
            }
        }
        if (i == 102) {
            if (VoIPService.getSharedState() == null) {
                this.windowView.finish();
            } else {
                if (iArr.length <= 0 || iArr[0] != 0) {
                    return;
                }
                toggleCameraInput();
            }
        }
    }

    public static void onResume() {
        VoIPFragment voIPFragment = instance;
        if (voIPFragment != null) {
            voIPFragment.onResumeInternal();
        }
        if (VoIPPiPView.getInstance() != null) {
            VoIPPiPView.getInstance().onResume();
        }
    }

    private void requestInlinePermissions() {
        if (Build.VERSION.SDK_INT >= 21) {
            AlertsCreator.createDrawOverlayPermissionDialog(this.activity, new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i) {
                    VoIPFragment.this.lambda$requestInlinePermissions$42(alertDialog, i);
                }
            }).show();
        }
    }

    public void runAcceptCallAnimation(final Runnable runnable) {
        if (this.bottomVideoBtn.getVisibility() == 0) {
            this.acceptDeclineView.getLocationOnScreen(new int[2]);
            this.acceptDeclineView.stopAnimations();
            runnable.run();
            return;
        }
        this.bottomEndCallBtn.animate().cancel();
        this.bottomSpeakerBtn.animate().cancel();
        this.bottomMuteBtn.animate().cancel();
        this.bottomVideoBtn.animate().cancel();
        this.acceptDeclineView.getLocationOnScreen(new int[2]);
        this.acceptDeclineView.stopAnimations();
        this.bottomEndCallBtn.setData(R.drawable.calls_decline, -1, -1041108, LocaleController.getString(R.string.VoipEndCall2), false, false);
        this.bottomSpeakerBtn.setType(VoIpSwitchLayout.Type.SPEAKER, false);
        this.bottomMuteBtn.setType(VoIpSwitchLayout.Type.MICRO, false);
        this.bottomVideoBtn.setType(VoIpSwitchLayout.Type.VIDEO, true);
        this.bottomEndCallBtn.setVisibility(0);
        this.bottomSpeakerBtn.setVisibility(0);
        this.bottomMuteBtn.setVisibility(0);
        this.bottomVideoBtn.setVisibility(0);
        this.bottomEndCallBtn.setAlpha(0.0f);
        this.bottomSpeakerBtn.setAlpha(0.0f);
        this.bottomMuteBtn.setAlpha(0.0f);
        this.bottomVideoBtn.setAlpha(0.0f);
        final ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.acceptDeclineView.getLayoutParams();
        final int marginEnd = marginLayoutParams.getMarginEnd();
        AndroidUtilities.dp(52.0f);
        final int dp = AndroidUtilities.dp(24.0f);
        final int dp2 = AndroidUtilities.dp(62.0f);
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                VoIPFragment.this.lambda$runAcceptCallAnimation$13(dp2, marginEnd, dp, marginLayoutParams, valueAnimator);
            }
        });
        AcceptDeclineView acceptDeclineView = this.acceptDeclineView;
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(acceptDeclineView, (Property<AcceptDeclineView, Float>) View.SCALE_X, acceptDeclineView.getScaleX(), 1.0f, 1.0f, 1.0f);
        AcceptDeclineView acceptDeclineView2 = this.acceptDeclineView;
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(acceptDeclineView2, (Property<AcceptDeclineView, Float>) View.SCALE_Y, acceptDeclineView2.getScaleY(), 1.0f, 1.0f, 1.0f);
        AcceptDeclineView acceptDeclineView3 = this.acceptDeclineView;
        animatorSet.playTogether(ofFloat, ofFloat2, ofFloat3, ObjectAnimator.ofFloat(acceptDeclineView3, (Property<AcceptDeclineView, Float>) View.ALPHA, acceptDeclineView3.getAlpha(), this.acceptDeclineView.getAlpha(), 0.0f, 0.0f));
        animatorSet.setDuration(400L);
        animatorSet.setInterpolator(new LinearInterpolator());
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                runnable.run();
                VoIPFragment.this.acceptDeclineView.setScaleX(1.15f);
                VoIPFragment.this.acceptDeclineView.setScaleY(1.15f);
                ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) VoIPFragment.this.acceptDeclineView.getLayoutParams();
                marginLayoutParams2.leftMargin = AndroidUtilities.dp(10.0f);
                marginLayoutParams2.rightMargin = AndroidUtilities.dp(10.0f);
                VoIPFragment.this.acceptDeclineView.setVisibility(8);
            }
        });
        animatorSet.start();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VoIPFragment.this.lambda$runAcceptCallAnimation$14();
            }
        }, 133L);
    }

    private void setFrontalCameraAction(final VoIpSwitchLayout voIpSwitchLayout, final VoIPService voIPService, boolean z) {
        if (!this.currentUserIsVideo) {
            voIpSwitchLayout.setType(VoIpSwitchLayout.Type.CAMERA, false);
            voIpSwitchLayout.setOnBtnClickedListener(null);
            voIpSwitchLayout.setEnabled(false);
        } else {
            voIpSwitchLayout.setEnabled(true);
            if (voIPService.isFrontFaceCamera()) {
                voIpSwitchLayout.setType(VoIpSwitchLayout.Type.CAMERA, true ^ voIPService.isSwitchingCamera());
            } else {
                voIpSwitchLayout.setType(VoIpSwitchLayout.Type.CAMERA, voIPService.isSwitchingCamera());
            }
            voIpSwitchLayout.setOnBtnClickedListener(new VoIpSwitchLayout.VoIpButtonView.OnBtnClickedListener() {
                @Override
                public final void onClicked(View view) {
                    VoIPFragment.this.lambda$setFrontalCameraAction$38(voIPService, voIpSwitchLayout, view);
                }
            });
        }
    }

    private void setInsets(WindowInsets windowInsets) {
        int systemWindowInsetBottom;
        int systemWindowInsetBottom2;
        int systemWindowInsetTop;
        int systemWindowInsetTop2;
        int systemWindowInsetTop3;
        int systemWindowInsetTop4;
        int systemWindowInsetTop5;
        int systemWindowInsetTop6;
        int systemWindowInsetTop7;
        int systemWindowInsetTop8;
        int systemWindowInsetBottom3;
        int systemWindowInsetBottom4;
        int systemWindowInsetBottom5;
        this.lastInsets = windowInsets;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.buttonsLayout.getLayoutParams();
        systemWindowInsetBottom = this.lastInsets.getSystemWindowInsetBottom();
        layoutParams.bottomMargin = systemWindowInsetBottom;
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.acceptDeclineView.getLayoutParams();
        systemWindowInsetBottom2 = this.lastInsets.getSystemWindowInsetBottom();
        layoutParams2.bottomMargin = systemWindowInsetBottom2;
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.backIcon.getLayoutParams();
        systemWindowInsetTop = this.lastInsets.getSystemWindowInsetTop();
        layoutParams3.topMargin = systemWindowInsetTop;
        FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.speakerPhoneIcon.getLayoutParams();
        systemWindowInsetTop2 = this.lastInsets.getSystemWindowInsetTop();
        layoutParams4.topMargin = systemWindowInsetTop2;
        FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) this.statusLayout.getLayoutParams();
        int dp = AndroidUtilities.dp(135.0f);
        systemWindowInsetTop3 = this.lastInsets.getSystemWindowInsetTop();
        layoutParams5.topMargin = dp + systemWindowInsetTop3;
        FrameLayout.LayoutParams layoutParams6 = (FrameLayout.LayoutParams) this.emojiLayout.getLayoutParams();
        int dp2 = AndroidUtilities.dp(17.0f);
        systemWindowInsetTop4 = this.lastInsets.getSystemWindowInsetTop();
        layoutParams6.topMargin = dp2 + systemWindowInsetTop4;
        FrameLayout.LayoutParams layoutParams7 = (FrameLayout.LayoutParams) this.callingUserPhotoViewMini.getLayoutParams();
        int dp3 = AndroidUtilities.dp(93.0f);
        systemWindowInsetTop5 = this.lastInsets.getSystemWindowInsetTop();
        layoutParams7.topMargin = dp3 + systemWindowInsetTop5;
        FrameLayout.LayoutParams layoutParams8 = (FrameLayout.LayoutParams) this.hideEmojiLayout.getLayoutParams();
        systemWindowInsetTop6 = this.lastInsets.getSystemWindowInsetTop();
        layoutParams8.topMargin = systemWindowInsetTop6;
        FrameLayout.LayoutParams layoutParams9 = (FrameLayout.LayoutParams) this.emojiRationalLayout.getLayoutParams();
        int dp4 = AndroidUtilities.dp(118.0f);
        systemWindowInsetTop7 = this.lastInsets.getSystemWindowInsetTop();
        layoutParams9.topMargin = dp4 + systemWindowInsetTop7;
        FrameLayout.LayoutParams layoutParams10 = (FrameLayout.LayoutParams) this.rateCallLayout.getLayoutParams();
        int dp5 = AndroidUtilities.dp(380.0f);
        systemWindowInsetTop8 = this.lastInsets.getSystemWindowInsetTop();
        layoutParams10.topMargin = dp5 + systemWindowInsetTop8;
        FrameLayout.LayoutParams layoutParams11 = (FrameLayout.LayoutParams) this.callingUserMiniFloatingLayout.getLayoutParams();
        systemWindowInsetBottom3 = this.lastInsets.getSystemWindowInsetBottom();
        layoutParams11.bottomMargin = systemWindowInsetBottom3;
        FrameLayout.LayoutParams layoutParams12 = (FrameLayout.LayoutParams) this.notificationsLayout.getLayoutParams();
        systemWindowInsetBottom4 = this.lastInsets.getSystemWindowInsetBottom();
        layoutParams12.bottomMargin = systemWindowInsetBottom4;
        this.currentUserCameraFloatingLayout.setInsets(this.lastInsets);
        this.callingUserMiniFloatingLayout.setInsets(this.lastInsets);
        this.fragmentView.requestLayout();
        PrivateVideoPreviewDialogNew privateVideoPreviewDialogNew = this.previewDialog;
        if (privateVideoPreviewDialogNew != null) {
            systemWindowInsetBottom5 = this.lastInsets.getSystemWindowInsetBottom();
            privateVideoPreviewDialogNew.setBottomPadding(systemWindowInsetBottom5);
        }
    }

    private void setMicrohoneAction(VoIpSwitchLayout voIpSwitchLayout, VoIPService voIPService, boolean z) {
        voIpSwitchLayout.setType(VoIpSwitchLayout.Type.MICRO, voIPService.isMicMute());
        this.currentUserCameraFloatingLayout.setMuted(voIPService.isMicMute(), z);
        voIpSwitchLayout.setOnBtnClickedListener(new VoIpSwitchLayout.VoIpButtonView.OnBtnClickedListener() {
            @Override
            public final void onClicked(View view) {
                VoIPFragment.this.lambda$setMicrohoneAction$34(view);
            }
        });
    }

    private void setSpeakerPhoneAction(final VoIpSwitchLayout voIpSwitchLayout, final VoIPService voIPService, boolean z) {
        VoipAudioManager voipAudioManager = VoipAudioManager.get();
        final int i = 0;
        if (voIPService.isBluetoothOn()) {
            voIpSwitchLayout.setType(VoIpSwitchLayout.Type.BLUETOOTH, false);
            i = 2;
        } else if (voipAudioManager.isSpeakerphoneOn()) {
            voIpSwitchLayout.setType(VoIpSwitchLayout.Type.SPEAKER, true);
        } else {
            voIpSwitchLayout.setType(VoIpSwitchLayout.Type.SPEAKER, false);
            i = 1;
        }
        voIpSwitchLayout.setEnabled(true);
        voIpSwitchLayout.setOnBtnClickedListener(new VoIpSwitchLayout.VoIpButtonView.OnBtnClickedListener() {
            @Override
            public final void onClicked(View view) {
                VoIPFragment.this.lambda$setSpeakerPhoneAction$37(i, voIpSwitchLayout, voIPService, view);
            }
        });
    }

    public void setVideoAction(VoIpSwitchLayout voIpSwitchLayout, final VoIPService voIPService, boolean z) {
        if (!((this.currentUserIsVideo || this.callingUserIsVideo) ? true : voIPService.isVideoAvailable())) {
            voIpSwitchLayout.setType(VoIpSwitchLayout.Type.VIDEO, true);
            voIpSwitchLayout.setOnClickListener(null);
            voIpSwitchLayout.setEnabled(false);
        } else {
            if (this.currentUserIsVideo) {
                voIPService.isScreencast();
                voIpSwitchLayout.setType(VoIpSwitchLayout.Type.VIDEO, false, z);
            } else {
                voIpSwitchLayout.setType(VoIpSwitchLayout.Type.VIDEO, true, z);
            }
            voIpSwitchLayout.setOnBtnClickedListener(new VoIpSwitchLayout.VoIpButtonView.OnBtnClickedListener() {
                @Override
                public final void onClicked(View view) {
                    VoIPFragment.this.lambda$setVideoAction$36(voIPService, view);
                }
            });
            voIpSwitchLayout.setEnabled(true);
        }
    }

    public static void show(Activity activity, int i) {
        show(activity, false, i);
    }

    public static void show(Activity activity, boolean z, int i) {
        VoIPFragment voIPFragment = instance;
        if (voIPFragment != null && voIPFragment.windowView.getParent() == null) {
            VoIPFragment voIPFragment2 = instance;
            if (voIPFragment2 != null) {
                voIPFragment2.callingUserTextureView.renderer.release();
                instance.currentUserTextureView.renderer.release();
                instance.callingUserMiniTextureRenderer.release();
                VoIPWindowView voIPWindowView = instance.windowView;
                if (voIPWindowView != null) {
                    voIPWindowView.finishImmediate();
                }
                instance.destroy();
            }
            instance = null;
        }
        if (instance != null || activity.isFinishing()) {
            return;
        }
        boolean z2 = VoIPPiPView.getInstance() != null;
        if (VoIPService.getSharedState() == null || VoIPService.getSharedState().getUser() == null) {
            return;
        }
        final VoIPFragment voIPFragment3 = new VoIPFragment(i);
        voIPFragment3.activity = activity;
        instance = voIPFragment3;
        VoIPWindowView voIPWindowView2 = new VoIPWindowView(activity, !z2) {
            private final Path clipPath = new Path();
            private final RectF rectF = new RectF();

            @Override
            protected void dispatchDraw(Canvas canvas) {
                if (!voIPFragment3.switchingToPip || getAlpha() == 0.0f) {
                    super.dispatchDraw(canvas);
                    return;
                }
                float width = voIPFragment3.callingUserTextureView.getWidth() * voIPFragment3.callingUserTextureView.getScaleX();
                float height = voIPFragment3.callingUserTextureView.getHeight() * voIPFragment3.callingUserTextureView.getScaleY();
                float x = voIPFragment3.callingUserTextureView.getX() + ((voIPFragment3.callingUserTextureView.getWidth() - width) / 2.0f);
                float y = voIPFragment3.callingUserTextureView.getY() + ((voIPFragment3.callingUserTextureView.getHeight() - height) / 2.0f);
                canvas.save();
                this.clipPath.rewind();
                this.rectF.set(x, y, width + x, height + y);
                float dp = AndroidUtilities.dp(4.0f);
                this.clipPath.addRoundRect(this.rectF, dp, dp, Path.Direction.CW);
                this.clipPath.close();
                canvas.clipPath(this.clipPath);
                super.dispatchDraw(canvas);
                canvas.restore();
            }

            @Override
            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                VoIPServiceState sharedState;
                if (voIPFragment3.isFinished || voIPFragment3.switchingToPip) {
                    return false;
                }
                int keyCode = keyEvent.getKeyCode();
                if (keyCode == 4 && keyEvent.getAction() == 1 && !voIPFragment3.lockOnScreen) {
                    voIPFragment3.onBackPressed();
                    return true;
                }
                if ((keyCode != 25 && keyCode != 24) || voIPFragment3.currentState != 15 || (sharedState = VoIPService.getSharedState()) == null) {
                    return super.dispatchKeyEvent(keyEvent);
                }
                sharedState.stopRinging();
                return true;
            }
        };
        instance.deviceIsLocked = ((KeyguardManager) activity.getSystemService("keyguard")).inKeyguardRestrictedInputMode();
        PowerManager powerManager = (PowerManager) activity.getSystemService("power");
        int i2 = Build.VERSION.SDK_INT;
        boolean isInteractive = i2 >= 20 ? powerManager.isInteractive() : powerManager.isScreenOn();
        VoIPFragment voIPFragment4 = instance;
        voIPFragment4.screenWasWakeup = true ^ isInteractive;
        voIPWindowView2.setLockOnScreen(voIPFragment4.deviceIsLocked);
        voIPFragment3.windowView = voIPWindowView2;
        if (i2 >= 20) {
            voIPWindowView2.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    WindowInsets lambda$show$3;
                    lambda$show$3 = VoIPFragment.lambda$show$3(VoIPFragment.this, view, windowInsets);
                    return lambda$show$3;
                }
            });
        }
        WindowManager windowManager = (WindowManager) activity.getSystemService("window");
        WindowManager.LayoutParams createWindowLayoutParams = voIPWindowView2.createWindowLayoutParams();
        if (z) {
            createWindowLayoutParams.type = i2 >= 26 ? 2038 : 2003;
        }
        windowManager.addView(voIPWindowView2, createWindowLayoutParams);
        voIPWindowView2.addView(voIPFragment3.createView(activity));
        if (z2) {
            voIPFragment3.enterTransitionProgress = 0.0f;
            voIPFragment3.startTransitionFromPiP();
        } else {
            voIPFragment3.enterTransitionProgress = 1.0f;
            voIPFragment3.updateSystemBarColors();
        }
    }

    private void showAcceptDeclineView(boolean z, boolean z2) {
        if (z2) {
            if (z && this.acceptDeclineView.getTag() == null) {
                this.acceptDeclineView.animate().setListener(null).cancel();
                if (this.acceptDeclineView.getVisibility() == 8) {
                    this.acceptDeclineView.setVisibility(0);
                    this.acceptDeclineView.setAlpha(0.0f);
                }
                this.acceptDeclineView.animate().alpha(1.0f);
            }
            if (!z && this.acceptDeclineView.getTag() != null) {
                this.acceptDeclineView.animate().setListener(null).cancel();
                this.acceptDeclineView.animate().setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        VoIPFragment.this.acceptDeclineView.setVisibility(8);
                    }
                }).alpha(0.0f);
            }
        } else {
            this.acceptDeclineView.setVisibility(z ? 0 : 8);
        }
        this.acceptDeclineView.setEnabled(z);
        this.acceptDeclineView.setTag(z ? 1 : null);
    }

    private void showCallingUserAvatarMini(boolean z, boolean z2) {
        ViewPropertyAnimator listener;
        boolean z3 = (this.currentUserIsVideo || this.callingUserIsVideo) ? false : true;
        if (!z) {
            this.callingUserPhotoViewMini.animate().setListener(null).cancel();
            this.callingUserPhotoViewMini.setTranslationY(0.0f);
            this.callingUserPhotoViewMini.setAlpha(1.0f);
            this.callingUserPhotoViewMini.setScaleX(1.0f);
            this.callingUserPhotoViewMini.setScaleY(1.0f);
            this.callingUserPhotoViewMini.setVisibility(z3 ? 0 : 8);
        } else if (z3 && this.callingUserPhotoViewMini.getTag() == null) {
            this.callingUserPhotoViewMini.animate().setListener(null).cancel();
            this.callingUserPhotoViewMini.setVisibility(0);
            if (!this.emojiExpanded) {
                this.callingUserPhotoViewMini.setAlpha(0.0f);
                ImageWithWavesView imageWithWavesView = this.callingUserPhotoViewMini;
                if (!z2) {
                    imageWithWavesView.setTranslationY(-AndroidUtilities.dp(135.0f));
                    imageWithWavesView = this.callingUserPhotoViewMini;
                }
                listener = imageWithWavesView.animate().alpha(1.0f).translationY(0.0f).scaleY(1.0f).scaleX(1.0f).setDuration(150L).setInterpolator(CubicBezierInterpolator.DEFAULT);
                listener.start();
            } else if (z2) {
                this.callingUserPhotoViewMini.setAlpha(0.0f);
                this.callingUserPhotoViewMini.setTranslationY(AndroidUtilities.dp(48.0f));
                this.callingUserPhotoViewMini.setScaleX(0.1f);
                this.callingUserPhotoViewMini.setScaleY(0.1f);
            }
        } else if (!z3 && this.callingUserPhotoViewMini.getTag() != null) {
            this.callingUserPhotoViewMini.animate().setListener(null).cancel();
            this.callingUserPhotoViewMini.setTranslationY(0.0f);
            listener = this.callingUserPhotoViewMini.animate().alpha(0.0f).setDuration(150L).scaleX(0.1f).scaleY(0.1f).setInterpolator(CubicBezierInterpolator.DEFAULT).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    VoIPFragment.this.callingUserPhotoViewMini.setVisibility(8);
                }
            });
            listener.start();
        }
        this.callingUserPhotoViewMini.setTag(z3 ? 1 : null);
    }

    private void showErrorDialog(CharSequence charSequence) {
        if (this.activity.isFinishing()) {
            return;
        }
        AlertDialog show = new DarkAlertDialog.Builder(this.activity).setTitle(LocaleController.getString(R.string.VoipFailed)).setMessage(charSequence).setPositiveButton(LocaleController.getString(R.string.OK), null).show();
        show.setCanceledOnTouchOutside(true);
        show.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                VoIPFragment.this.lambda$showErrorDialog$41(dialogInterface);
            }
        });
    }

    private void showFloatingLayout(int i, boolean z) {
        Animator animator;
        if (this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() != 2) {
            this.currentUserCameraFloatingLayout.setUiVisible(this.uiVisible);
        }
        if (!z && (animator = this.cameraShowingAnimator) != null) {
            animator.removeAllListeners();
            this.cameraShowingAnimator.cancel();
        }
        if (i != 0) {
            boolean z2 = (this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() == 0) ? false : z;
            if (!z) {
                this.currentUserCameraFloatingLayout.setVisibility(0);
            } else if (this.currentUserCameraFloatingLayout.getTag() != null && ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() == 0) {
                if (this.currentUserCameraFloatingLayout.getVisibility() == 8) {
                    this.currentUserCameraFloatingLayout.setAlpha(0.0f);
                    this.currentUserCameraFloatingLayout.setScaleX(0.7f);
                    this.currentUserCameraFloatingLayout.setScaleY(0.7f);
                    this.currentUserCameraFloatingLayout.setVisibility(0);
                }
                Animator animator2 = this.cameraShowingAnimator;
                if (animator2 != null) {
                    animator2.removeAllListeners();
                    this.cameraShowingAnimator.cancel();
                }
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(ObjectAnimator.ofFloat(this.currentUserCameraFloatingLayout, (Property<VoIPFloatingLayout, Float>) View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.currentUserCameraFloatingLayout, (Property<VoIPFloatingLayout, Float>) View.SCALE_X, 0.7f, 1.0f), ObjectAnimator.ofFloat(this.currentUserCameraFloatingLayout, (Property<VoIPFloatingLayout, Float>) View.SCALE_Y, 0.7f, 1.0f));
                this.cameraShowingAnimator = animatorSet;
                animatorSet.setDuration(150L).start();
            }
            if (this.currentUserCameraFloatingLayout.getTag() == null || ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() != 2) {
                VoIPFloatingLayout voIPFloatingLayout = this.currentUserCameraFloatingLayout;
                if (voIPFloatingLayout.relativePositionToSetX < 0.0f) {
                    voIPFloatingLayout.setRelativePosition(1.0f, 1.0f);
                    this.currentUserCameraIsFullscreen = true;
                }
            }
            this.currentUserCameraFloatingLayout.setFloatingMode(i == 2, z2);
            this.currentUserCameraIsFullscreen = i != 2;
        } else if (!z) {
            this.currentUserCameraFloatingLayout.setVisibility(8);
        } else if (this.currentUserCameraFloatingLayout.getTag() != null && ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() != 0) {
            Animator animator3 = this.cameraShowingAnimator;
            if (animator3 != null) {
                animator3.removeAllListeners();
                this.cameraShowingAnimator.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            VoIPFloatingLayout voIPFloatingLayout2 = this.currentUserCameraFloatingLayout;
            animatorSet2.playTogether(ObjectAnimator.ofFloat(voIPFloatingLayout2, (Property<VoIPFloatingLayout, Float>) View.ALPHA, voIPFloatingLayout2.getAlpha(), 0.0f));
            if (this.currentUserCameraFloatingLayout.getTag() != null && ((Integer) this.currentUserCameraFloatingLayout.getTag()).intValue() == 2) {
                VoIPFloatingLayout voIPFloatingLayout3 = this.currentUserCameraFloatingLayout;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(voIPFloatingLayout3, (Property<VoIPFloatingLayout, Float>) View.SCALE_X, voIPFloatingLayout3.getScaleX(), 0.7f);
                VoIPFloatingLayout voIPFloatingLayout4 = this.currentUserCameraFloatingLayout;
                animatorSet2.playTogether(ofFloat, ObjectAnimator.ofFloat(voIPFloatingLayout4, (Property<VoIPFloatingLayout, Float>) View.SCALE_Y, voIPFloatingLayout4.getScaleX(), 0.7f));
            }
            this.cameraShowingAnimator = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator4) {
                    VoIPFragment.this.currentUserCameraFloatingLayout.setTranslationX(0.0f);
                    VoIPFragment.this.currentUserCameraFloatingLayout.setTranslationY(0.0f);
                    VoIPFragment.this.currentUserCameraFloatingLayout.setScaleY(1.0f);
                    VoIPFragment.this.currentUserCameraFloatingLayout.setScaleX(1.0f);
                    VoIPFragment.this.currentUserCameraFloatingLayout.setVisibility(8);
                }
            });
            this.cameraShowingAnimator.setDuration(250L).setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.cameraShowingAnimator.setStartDelay(50L);
            this.cameraShowingAnimator.start();
        }
        this.currentUserCameraFloatingLayout.setTag(Integer.valueOf(i));
    }

    public void showUi(boolean z) {
        int i;
        ValueAnimator valueAnimator = this.uiVisibilityAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (z || !this.uiVisible) {
            if (z && !this.uiVisible) {
                this.tapToVideoTooltip.hide();
                this.encryptionTooltip.hide();
                ViewPropertyAnimator translationY = this.callingUserTitle.animate().alpha(1.0f).setDuration(150L).translationY(0.0f);
                CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
                translationY.setInterpolator(cubicBezierInterpolator).start();
                this.statusTextView.animate().alpha(1.0f).setDuration(150L).translationY(0.0f).setInterpolator(cubicBezierInterpolator).start();
                this.speakerPhoneIcon.animate().alpha(1.0f).translationY(0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
                this.backIcon.animate().alpha(1.0f).translationY(0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
                this.emojiLayout.animate().alpha(1.0f).translationY(0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
                this.buttonsLayout.animate().alpha(1.0f).translationY(0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
                this.bottomShadow.animate().alpha(1.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
                this.topShadow.animate().alpha(1.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
                ValueAnimator ofFloat = ValueAnimator.ofFloat(this.uiVisibilityAlpha, 1.0f);
                this.uiVisibilityAnimator = ofFloat;
                ofFloat.addUpdateListener(this.statusbarAnimatorListener);
                this.uiVisibilityAnimator.setDuration(150L).setInterpolator(cubicBezierInterpolator);
                this.uiVisibilityAnimator.start();
                this.buttonsLayout.setEnabled(true);
            }
            i = 0;
        } else {
            ViewPropertyAnimator duration = this.speakerPhoneIcon.animate().alpha(0.0f).translationY(-AndroidUtilities.dp(10.0f)).setDuration(150L);
            CubicBezierInterpolator cubicBezierInterpolator2 = CubicBezierInterpolator.DEFAULT;
            duration.setInterpolator(cubicBezierInterpolator2).start();
            this.backIcon.animate().alpha(0.0f).translationY(-AndroidUtilities.dp(10.0f)).setDuration(150L).setInterpolator(cubicBezierInterpolator2).start();
            this.emojiLayout.animate().alpha(0.0f).translationY(-AndroidUtilities.dp(10.0f)).setDuration(150L).setInterpolator(cubicBezierInterpolator2).start();
            this.callingUserTitle.animate().alpha(0.0f).setDuration(150L).translationY(-AndroidUtilities.dp(10.0f)).setInterpolator(cubicBezierInterpolator2).start();
            this.statusTextView.animate().alpha(0.0f).setDuration(150L).translationY(-AndroidUtilities.dp(10.0f)).setInterpolator(cubicBezierInterpolator2).start();
            this.buttonsLayout.animate().alpha(0.0f).translationY(AndroidUtilities.dp(10.0f)).setDuration(150L).setInterpolator(cubicBezierInterpolator2).start();
            this.bottomShadow.animate().alpha(0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator2).start();
            this.topShadow.animate().alpha(0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator2).start();
            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(this.uiVisibilityAlpha, 0.0f);
            this.uiVisibilityAnimator = ofFloat2;
            ofFloat2.addUpdateListener(this.statusbarAnimatorListener);
            this.uiVisibilityAnimator.setDuration(150L).setInterpolator(cubicBezierInterpolator2);
            this.uiVisibilityAnimator.start();
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            this.buttonsLayout.setEnabled(false);
            this.encryptionTooltip.hide();
            i = 150;
        }
        this.uiVisible = z;
        this.windowView.requestFullscreen(!z);
        this.notificationsLayout.animate().translationY((-AndroidUtilities.dp(16.0f)) - (this.uiVisible ? AndroidUtilities.dp(80.0f) : 0)).setDuration(150L).setStartDelay(i).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
    }

    public void startWaitingFoHideUi() {
        if (VoIPService.getSharedInstance() != null) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUIRunnable);
            this.hideUiRunnableWaiting = false;
            if (this.canHideUI && this.uiVisible) {
                AndroidUtilities.runOnUIThread(this.hideUIRunnable, 3000L);
                this.hideUiRunnableWaiting = true;
            }
        }
    }

    private void toggleCameraInput() {
        int systemWindowInsetBottom;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (this.accessibilityManager.isTouchExplorationEnabled()) {
                this.fragmentView.announceForAccessibility(LocaleController.getString(!this.currentUserIsVideo ? R.string.AccDescrVoipCamOn : R.string.AccDescrVoipCamOff));
            }
            if (this.currentUserIsVideo) {
                this.currentUserTextureView.saveCameraLastBitmap();
                sharedInstance.setVideoState(false, 0);
                if (Build.VERSION.SDK_INT >= 21) {
                    sharedInstance.clearCamera();
                }
            } else {
                if (Build.VERSION.SDK_INT >= 21) {
                    if (this.previewDialog == null) {
                        sharedInstance.createCaptureDevice(false);
                        if (!sharedInstance.isFrontFaceCamera()) {
                            sharedInstance.switchCamera();
                        }
                        this.windowView.setLockOnScreen(true);
                        this.bottomVideoBtn.getLocationOnScreen(new int[2]);
                        PrivateVideoPreviewDialogNew privateVideoPreviewDialogNew = new PrivateVideoPreviewDialogNew(this.fragmentView.getContext(), r0[0], r0[1]) {
                            @Override
                            protected void afterOpened() {
                                VoIPFragment.this.gradientLayout.lockDrawing = true;
                                VoIPFragment.this.gradientLayout.invalidate();
                            }

                            @Override
                            protected void beforeClosed() {
                                VoIPFragment.this.gradientLayout.lockDrawing = false;
                                VoIPFragment.this.gradientLayout.invalidate();
                            }

                            @Override
                            protected int[] getFloatingViewLocation() {
                                int[] iArr = new int[2];
                                VoIPFragment.this.currentUserCameraFloatingLayout.getLocationOnScreen(iArr);
                                return new int[]{iArr[0], iArr[1], VoIPFragment.this.currentUserCameraFloatingLayout.getMeasuredWidth()};
                            }

                            @Override
                            protected boolean isHasVideoOnMainScreen() {
                                return VoIPFragment.this.callingUserIsVideo;
                            }

                            @Override
                            public void onDismiss(boolean z, boolean z2) {
                                VoIPFragment.this.previewDialog = null;
                                VoIPService sharedInstance2 = VoIPService.getSharedInstance();
                                VoIPFragment.this.windowView.setLockOnScreen(false);
                                if (z2) {
                                    VoIPFragment.this.currentUserIsVideo = true;
                                    if (sharedInstance2 != null && !z) {
                                        sharedInstance2.requestVideoCall(false);
                                        sharedInstance2.setVideoState(false, 2);
                                        sharedInstance2.switchToSpeaker();
                                    }
                                    if (sharedInstance2 != null) {
                                        VoIPFragment voIPFragment = VoIPFragment.this;
                                        voIPFragment.setVideoAction(voIPFragment.bottomVideoBtn, sharedInstance2, true);
                                    }
                                } else if (sharedInstance2 != null) {
                                    sharedInstance2.setVideoState(false, 0);
                                }
                                VoIPFragment voIPFragment2 = VoIPFragment.this;
                                voIPFragment2.previousState = voIPFragment2.currentState;
                                VoIPFragment.this.updateViewState();
                            }
                        };
                        this.previewDialog = privateVideoPreviewDialogNew;
                        WindowInsets windowInsets = this.lastInsets;
                        if (windowInsets != null) {
                            systemWindowInsetBottom = windowInsets.getSystemWindowInsetBottom();
                            privateVideoPreviewDialogNew.setBottomPadding(systemWindowInsetBottom);
                        }
                        this.fragmentView.addView(this.previewDialog);
                        return;
                    }
                    return;
                }
                this.currentUserIsVideo = true;
                if (!sharedInstance.isSpeakerphoneOn()) {
                    VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(this.activity, false);
                }
                sharedInstance.requestVideoCall(false);
                sharedInstance.setVideoState(false, 2);
            }
            this.previousState = this.currentState;
            updateViewState();
        }
    }

    private void updateButtons(boolean z) {
        ViewPropertyAnimator alpha;
        ViewPropertyAnimator alpha2;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null) {
            return;
        }
        if (z) {
            TransitionSet transitionSet = new TransitionSet();
            Transition duration = new Visibility() {
                @Override
                public Animator onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
                    ObjectAnimator ofPropertyValuesHolder = ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat((Property<?, Float>) View.TRANSLATION_Y, AndroidUtilities.dp(100.0f), 0.0f), PropertyValuesHolder.ofFloat((Property<?, Float>) View.SCALE_Y, 0.0f, 1.0f), PropertyValuesHolder.ofFloat((Property<?, Float>) View.SCALE_X, 0.0f, 1.0f));
                    if (view instanceof VoIPToggleButton) {
                        view.setTranslationY(AndroidUtilities.dp(100.0f));
                        view.setScaleX(0.0f);
                        view.setScaleY(0.0f);
                        ofPropertyValuesHolder.setStartDelay(((VoIPToggleButton) view).animationDelay);
                    }
                    if (view instanceof VoIpSwitchLayout) {
                        view.setTranslationY(AndroidUtilities.dp(100.0f));
                        view.setScaleX(0.0f);
                        view.setScaleY(0.0f);
                        ofPropertyValuesHolder.setStartDelay(((VoIpSwitchLayout) view).animationDelay);
                    }
                    return ofPropertyValuesHolder;
                }

                @Override
                public Animator onDisappear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
                    return ObjectAnimator.ofPropertyValuesHolder(view, PropertyValuesHolder.ofFloat((Property<?, Float>) View.TRANSLATION_Y, view.getTranslationY(), AndroidUtilities.dp(100.0f)), PropertyValuesHolder.ofFloat((Property<?, Float>) View.SCALE_Y, view.getScaleY(), 0.0f), PropertyValuesHolder.ofFloat((Property<?, Float>) View.SCALE_X, view.getScaleX(), 0.0f));
                }
            }.setDuration(250L);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            transitionSet.addTransition(duration.setInterpolator(cubicBezierInterpolator)).addTransition(new ChangeBounds().setDuration(250L).setInterpolator(cubicBezierInterpolator));
            transitionSet.excludeChildren(VoIPToggleButton.class, true);
            transitionSet.excludeChildren(VoIpSwitchLayout.class, true);
            TransitionManager.beginDelayedTransition(this.buttonsLayout, transitionSet);
        }
        int i = this.currentState;
        if (i == 11) {
            this.bottomSpeakerBtn.setVisibility(8);
            this.bottomVideoBtn.setVisibility(8);
            this.bottomMuteBtn.setVisibility(8);
            this.bottomEndCallBtn.setVisibility(8);
            return;
        }
        int i2 = 0;
        if (i == 15 || i == 17) {
            TL_phone.PhoneCall phoneCall = sharedInstance.privateCall;
            if (phoneCall != null && phoneCall.video && i == 15) {
                if (sharedInstance.isScreencast() || !(this.currentUserIsVideo || this.callingUserIsVideo)) {
                    setSpeakerPhoneAction(this.bottomSpeakerBtn, sharedInstance, z);
                    alpha = this.speakerPhoneIcon.animate().alpha(0.0f);
                } else {
                    setFrontalCameraAction(this.bottomSpeakerBtn, sharedInstance, z);
                    if (this.uiVisible) {
                        alpha = this.speakerPhoneIcon.animate().alpha(1.0f);
                    }
                    setVideoAction(this.bottomVideoBtn, sharedInstance, false);
                    setMicrohoneAction(this.bottomMuteBtn, sharedInstance, z);
                }
                alpha.start();
                setVideoAction(this.bottomVideoBtn, sharedInstance, false);
                setMicrohoneAction(this.bottomMuteBtn, sharedInstance, z);
            } else {
                this.bottomSpeakerBtn.setVisibility(8);
                this.bottomVideoBtn.setVisibility(8);
                this.bottomMuteBtn.setVisibility(8);
            }
            this.bottomEndCallBtn.setVisibility(8);
        } else {
            if (instance == null) {
                return;
            }
            if (sharedInstance.isScreencast() || !(this.currentUserIsVideo || this.callingUserIsVideo)) {
                setSpeakerPhoneAction(this.bottomSpeakerBtn, sharedInstance, z);
                this.speakerPhoneIcon.setTag(null);
                alpha2 = this.speakerPhoneIcon.animate().alpha(0.0f);
            } else {
                setFrontalCameraAction(this.bottomSpeakerBtn, sharedInstance, z);
                if (this.uiVisible) {
                    this.speakerPhoneIcon.setTag(1);
                    alpha2 = this.speakerPhoneIcon.animate().alpha(1.0f);
                }
                setVideoAction(this.bottomVideoBtn, sharedInstance, false);
                setMicrohoneAction(this.bottomMuteBtn, sharedInstance, z);
                this.bottomEndCallBtn.setData(R.drawable.calls_decline, -1, -1041108, LocaleController.getString(R.string.VoipEndCall2), false, z);
                this.bottomEndCallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        VoIPFragment.this.lambda$updateButtons$33(view);
                    }
                });
            }
            alpha2.start();
            setVideoAction(this.bottomVideoBtn, sharedInstance, false);
            setMicrohoneAction(this.bottomMuteBtn, sharedInstance, z);
            this.bottomEndCallBtn.setData(R.drawable.calls_decline, -1, -1041108, LocaleController.getString(R.string.VoipEndCall2), false, z);
            this.bottomEndCallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    VoIPFragment.this.lambda$updateButtons$33(view);
                }
            });
        }
        if (this.bottomSpeakerBtn.getVisibility() == 0) {
            this.bottomSpeakerBtn.animationDelay = 0;
            i2 = 16;
        }
        if (this.bottomVideoBtn.getVisibility() == 0) {
            this.bottomVideoBtn.animationDelay = i2;
            i2 += 16;
        }
        if (this.bottomMuteBtn.getVisibility() == 0) {
            this.bottomMuteBtn.animationDelay = i2;
            i2 += 16;
        }
        if (this.bottomEndCallBtn.getVisibility() == 0) {
            this.bottomEndCallBtn.animationDelay = i2;
        }
        updateSpeakerPhoneIcon();
    }

    private void updateKeyView(boolean z) {
        VoIPService sharedInstance;
        byte[] bArr;
        if (this.emojiLoaded || (sharedInstance = VoIPService.getSharedInstance()) == null) {
            return;
        }
        int i = 0;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(sharedInstance.getEncryptionKey());
            byteArrayOutputStream.write(sharedInstance.getGA());
            bArr = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            FileLog.e((Throwable) e, false);
            bArr = null;
        }
        if (bArr == null) {
            return;
        }
        String[] emojifyForCall = EncryptionKeyEmojifier.emojifyForCall(Utilities.computeSHA256(bArr, 0, bArr.length));
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (int i2 = 0; i2 < 4; i2++) {
            Emoji.preloadEmoji(emojifyForCall[i2]);
            Emoji.EmojiDrawable emojiDrawable = Emoji.getEmojiDrawable(emojifyForCall[i2]);
            if (emojiDrawable != null) {
                emojiDrawable.setBounds(0, 0, AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                emojiDrawable.preload();
                int[] iArr = new int[1];
                TextPaint textPaint = new TextPaint(1);
                textPaint.setTextSize(AndroidUtilities.dp(28.0f));
                TLRPC.Document replaceEmojiToLottieFrame = replaceEmojiToLottieFrame(Emoji.replaceEmoji((CharSequence) emojifyForCall[i2], textPaint.getFontMetricsInt(), false, iArr), iArr);
                arrayList2.add(emojiDrawable);
                if (replaceEmojiToLottieFrame != null) {
                    arrayList.add(replaceEmojiToLottieFrame);
                }
                this.emojiViews[i2].setVisibility(8);
            }
            this.emojiDrawables[i2] = emojiDrawable;
        }
        if (arrayList.size() == 4) {
            while (i < arrayList.size()) {
                this.emojiViews[i].setAnimatedEmojiDrawable(new AnimatedEmojiDrawable(17, this.currentAccount, (TLRPC.Document) arrayList.get(i)));
                this.emojiViews[i].getImageReceiver().clearImage();
                i++;
            }
        } else {
            while (i < arrayList2.size()) {
                this.emojiViews[i].setImageDrawable((Drawable) arrayList2.get(i));
                i++;
            }
        }
        checkEmojiLoaded(z);
    }

    private void updateSpeakerPhoneIcon() {
        ImageView imageView;
        int i;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null) {
            return;
        }
        VoipAudioManager voipAudioManager = VoipAudioManager.get();
        if (sharedInstance.isBluetoothOn()) {
            imageView = this.speakerPhoneIcon;
            i = R.drawable.calls_bluetooth;
        } else if (voipAudioManager.isSpeakerphoneOn()) {
            imageView = this.speakerPhoneIcon;
            i = R.drawable.calls_speaker;
        } else if (sharedInstance.isHeadsetPlugged()) {
            imageView = this.speakerPhoneIcon;
            i = R.drawable.calls_menu_headset;
        } else {
            imageView = this.speakerPhoneIcon;
            i = R.drawable.calls_menu_phone;
        }
        imageView.setImageResource(i);
    }

    private void updateSystemBarColors() {
        this.overlayPaint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (this.uiVisibilityAlpha * 102.0f * this.enterTransitionProgress)));
        this.overlayBottomPaint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (((this.fillNaviagtionBarValue * 0.5f) + 0.5f) * 255.0f * this.enterTransitionProgress)));
        ViewGroup viewGroup = this.fragmentView;
        if (viewGroup != null) {
            viewGroup.invalidate();
        }
    }

    public void updateViewState() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.VoIPFragment.updateViewState():void");
    }

    public android.animation.Animator createPiPTransition(boolean r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.VoIPFragment.createPiPTransition(boolean):android.animation.Animator");
    }

    public View createView(Context context) {
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.accessibilityManager = (AccessibilityManager) ContextCompat.getSystemService(context, AccessibilityManager.class);
        FrameLayout frameLayout = new FrameLayout(context) {
            boolean check;
            long pressedTime;
            float pressedX;
            float pressedY;

            @Override
            protected boolean drawChild(Canvas canvas, View view, long j) {
                if (view == VoIPFragment.this.gradientLayout) {
                    VoIPFragment voIPFragment = VoIPFragment.this;
                    if (voIPFragment.currentUserIsVideo || voIPFragment.callingUserIsVideo) {
                        return false;
                    }
                }
                if ((view != VoIPFragment.this.gradientLayout && view != VoIPFragment.this.callingUserTextureView && (view != VoIPFragment.this.currentUserCameraFloatingLayout || !VoIPFragment.this.currentUserCameraIsFullscreen)) || (!VoIPFragment.this.zoomStarted && VoIPFragment.this.zoomBackAnimator == null)) {
                    return super.drawChild(canvas, view, j);
                }
                canvas.save();
                VoIPFragment voIPFragment2 = VoIPFragment.this;
                float f = voIPFragment2.pinchScale;
                canvas.scale(f, f, voIPFragment2.pinchCenterX, VoIPFragment.this.pinchCenterY);
                canvas.translate(VoIPFragment.this.pinchTranslationX, VoIPFragment.this.pinchTranslationY);
                boolean drawChild = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild;
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == 1) {
                    VoIPFragment.this.callingUserPhotoViewMini.setMute(false, false);
                    VoIPFragment.this.gradientLayout.resume();
                    AndroidUtilities.cancelRunOnUIThread(VoIPFragment.this.stopAnimatingBgRunnable);
                    if (VoIPFragment.this.currentState == 3) {
                        AndroidUtilities.runOnUIThread(VoIPFragment.this.stopAnimatingBgRunnable, 10000L);
                    }
                }
                return super.onInterceptTouchEvent(motionEvent);
            }

            @Override
            public boolean onTouchEvent(android.view.MotionEvent r14) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.VoIPFragment.AnonymousClass2.onTouchEvent(android.view.MotionEvent):boolean");
            }
        };
        frameLayout.setClipToPadding(false);
        frameLayout.setClipChildren(false);
        frameLayout.setBackgroundColor(-16777216);
        updateSystemBarColors();
        this.fragmentView = frameLayout;
        frameLayout.setFitsSystemWindows(true);
        this.gradientLayout = new VoIpGradientLayout(context, this.backgroundProvider);
        VoIPTextureView voIPTextureView = new VoIPTextureView(context, false, true, false, false);
        this.callingUserTextureView = voIPTextureView;
        TextureViewRenderer textureViewRenderer = voIPTextureView.renderer;
        RendererCommon.ScalingType scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FIT;
        textureViewRenderer.setScalingType(scalingType);
        this.callingUserTextureView.renderer.setEnableHardwareScaler(true);
        this.callingUserTextureView.renderer.setRotateTextureWithScreen(true);
        this.callingUserTextureView.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
        frameLayout.addView(this.gradientLayout, LayoutHelper.createFrame(-1, -1.0f));
        VoIpCoverView voIpCoverView = new VoIpCoverView(context, this.callingUser, this.backgroundProvider);
        this.voIpCoverView = voIpCoverView;
        frameLayout.addView(voIpCoverView, LayoutHelper.createFrame(-1, -1.0f));
        VoIpSnowView voIpSnowView = new VoIpSnowView(context);
        this.voIpSnowView = voIpSnowView;
        frameLayout.addView(voIpSnowView, LayoutHelper.createFrame(-1, 220.0f));
        frameLayout.addView(this.callingUserTextureView);
        GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TOP_BOTTOM;
        new BackgroundGradientDrawable(orientation, new int[]{-14994098, -14328963}).startDithering(BackgroundGradientDrawable.Sizes.ofDeviceScreen(BackgroundGradientDrawable.Sizes.Orientation.PORTRAIT), new BackgroundGradientDrawable.ListenerAdapter() {
            @Override
            public void onAllSizesReady() {
                VoIPFragment.this.gradientLayout.invalidate();
            }
        });
        VoIPFloatingLayout voIPFloatingLayout = new VoIPFloatingLayout(context);
        this.currentUserCameraFloatingLayout = voIPFloatingLayout;
        voIPFloatingLayout.setDelegate(new VoIPFloatingLayout.VoIPFloatingLayoutDelegate() {
            @Override
            public final void onChange(float f, boolean z) {
                VoIPFragment.this.lambda$createView$5(f, z);
            }
        });
        this.currentUserCameraFloatingLayout.setRelativePosition(1.0f, 1.0f);
        this.currentUserCameraIsFullscreen = true;
        VoIPTextureView voIPTextureView2 = new VoIPTextureView(context, true, false);
        this.currentUserTextureView = voIPTextureView2;
        voIPTextureView2.renderer.setIsCamera(true);
        this.currentUserTextureView.renderer.setUseCameraRotation(true);
        this.currentUserCameraFloatingLayout.setOnTapListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VoIPFragment.this.lambda$createView$6(view);
            }
        });
        this.currentUserTextureView.renderer.setMirror(true);
        this.currentUserCameraFloatingLayout.addView(this.currentUserTextureView);
        VoIPFloatingLayout voIPFloatingLayout2 = new VoIPFloatingLayout(context);
        this.callingUserMiniFloatingLayout = voIPFloatingLayout2;
        voIPFloatingLayout2.alwaysFloating = true;
        voIPFloatingLayout2.setFloatingMode(true, false);
        TextureViewRenderer textureViewRenderer2 = new TextureViewRenderer(context);
        this.callingUserMiniTextureRenderer = textureViewRenderer2;
        textureViewRenderer2.setEnableHardwareScaler(true);
        this.callingUserMiniTextureRenderer.setIsCamera(false);
        this.callingUserMiniTextureRenderer.setFpsReduction(30.0f);
        this.callingUserMiniTextureRenderer.setScalingType(scalingType);
        this.callingUserMiniFloatingLayout.addView(this.callingUserMiniTextureRenderer, LayoutHelper.createFrame(-1, -2, 17));
        this.callingUserMiniFloatingLayout.setOnTapListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                VoIPFragment.this.lambda$createView$7(view);
            }
        });
        this.callingUserMiniFloatingLayout.setVisibility(8);
        frameLayout.addView(this.currentUserCameraFloatingLayout, LayoutHelper.createFrame(-2, -2.0f));
        frameLayout.addView(this.callingUserMiniFloatingLayout);
        View view = new View(context);
        this.bottomShadow = view;
        view.setBackground(new GradientDrawable(orientation, new int[]{0, ColorUtils.setAlphaComponent(-16777216, 127)}));
        frameLayout.addView(this.bottomShadow, LayoutHelper.createFrame(-1, 160, 80));
        View view2 = new View(context);
        this.topShadow = view2;
        view2.setBackground(new GradientDrawable(orientation, new int[]{ColorUtils.setAlphaComponent(-16777216, 102), 0}));
        frameLayout.addView(this.topShadow, LayoutHelper.createFrame(-1, 160, 48));
        LinearLayout linearLayout = new LinearLayout(context) {
            @Override
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                accessibilityNodeInfo.setVisibleToUser(VoIPFragment.this.emojiLoaded);
            }
        };
        this.emojiLayout = linearLayout;
        linearLayout.setOrientation(0);
        this.emojiLayout.setPadding(0, 0, 0, AndroidUtilities.dp(30.0f));
        this.emojiLayout.setClipToPadding(false);
        this.emojiLayout.setContentDescription(LocaleController.getString(R.string.VoipHintEncryptionKey));
        this.emojiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                VoIPFragment.this.lambda$createView$8(view3);
            }
        });
        this.hideEmojiTextView = new HideEmojiTextView(context, this.backgroundProvider);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.hideEmojiLayout = frameLayout2;
        frameLayout2.addView(this.hideEmojiTextView, LayoutHelper.createFrame(-2, -2.0f, 3, 0.0f, 16.0f, 0.0f, 0.0f));
        this.hideEmojiLayout.setVisibility(8);
        this.hideEmojiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                VoIPFragment.this.lambda$createView$9(view3);
            }
        });
        EmojiRationalLayout emojiRationalLayout = new EmojiRationalLayout(context, this.backgroundProvider);
        this.emojiRationalLayout = emojiRationalLayout;
        emojiRationalLayout.setOrientation(1);
        TextView textView = new TextView(context);
        this.emojiRationalTopTextView = textView;
        textView.setText(LocaleController.getString(R.string.VoipCallEncryptionEndToEnd));
        this.emojiRationalTopTextView.setTextSize(1, 15.0f);
        this.emojiRationalTopTextView.setTypeface(AndroidUtilities.bold());
        this.emojiRationalTopTextView.setTextColor(-1);
        this.emojiRationalTopTextView.setGravity(17);
        TextView textView2 = new TextView(context) {
            @Override
            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (z) {
                    VoIPFragment.this.updateViewState();
                }
            }
        };
        this.emojiRationalTextView = textView2;
        textView2.setTextSize(1, 15.0f);
        this.emojiRationalTextView.setTextColor(-1);
        this.emojiRationalTextView.setGravity(17);
        this.emojiRationalTextView.setText(LocaleController.formatString("CallEmojiKeyTooltip", R.string.CallEmojiKeyTooltip, TextUtils.ellipsize(UserObject.getFirstName(this.callingUser), this.emojiRationalTextView.getPaint(), AndroidUtilities.dp(300.0f), TextUtils.TruncateAt.END)));
        this.emojiRationalLayout.setVisibility(8);
        this.emojiRationalLayout.addView(this.emojiRationalTopTextView);
        this.emojiRationalLayout.addView(this.emojiRationalTextView, LayoutHelper.createLinear(-2, -2, 0.0f, 8.0f, 0.0f, 0.0f));
        this.emojiRationalLayout.setPadding(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(80.0f), AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f));
        int i = 0;
        while (i < 4) {
            this.emojiViews[i] = new BackupImageView(context);
            this.emojiViews[i].getImageReceiver().setAspectFit(true);
            this.emojiLayout.addView(this.emojiViews[i], LayoutHelper.createLinear(25, 25, i == 0 ? 0.0f : 6.0f, 0.0f, 0.0f, 0.0f));
            i++;
        }
        LinearLayout linearLayout2 = new LinearLayout(context) {
            @Override
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                VoIPServiceState sharedState = VoIPService.getSharedState();
                CharSequence text = VoIPFragment.this.callingUserTitle.getText();
                if (sharedState == null || TextUtils.isEmpty(text)) {
                    return;
                }
                StringBuilder sb = new StringBuilder(text);
                sb.append(", ");
                sb.append(LocaleController.getString((sharedState.getPrivateCall() == null || !sharedState.getPrivateCall().video) ? R.string.VoipInCallBranding : R.string.VoipInVideoCallBranding));
                long callDuration = sharedState.getCallDuration();
                if (callDuration > 0) {
                    sb.append(", ");
                    sb.append(LocaleController.formatDuration((int) (callDuration / 1000)));
                }
                accessibilityNodeInfo.setText(sb);
            }
        };
        this.statusLayout = linearLayout2;
        linearLayout2.setOrientation(1);
        this.statusLayout.setFocusable(true);
        this.statusLayout.setFocusableInTouchMode(true);
        this.callingUserPhotoViewMini = new ImageWithWavesView(context);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setInfo(this.callingUser);
        this.callingUserPhotoViewMini.setImage(ImageLocation.getForUserOrChat(this.callingUser, 0), null, avatarDrawable, this.callingUser);
        this.callingUserPhotoViewMini.setRoundRadius(AndroidUtilities.dp(135.0f) / 2);
        TextView textView3 = new TextView(context);
        this.callingUserTitle = textView3;
        textView3.setTextSize(1, 28.0f);
        TLRPC.User user = this.callingUser;
        this.callingUserTitle.setText(Emoji.replaceEmoji(ContactsController.formatName(user.first_name, user.last_name), this.callingUserTitle.getPaint().getFontMetricsInt(), false));
        this.callingUserTitle.setMaxLines(2);
        this.callingUserTitle.setEllipsize(TextUtils.TruncateAt.END);
        this.callingUserTitle.setTextColor(-1);
        this.callingUserTitle.setGravity(1);
        this.callingUserTitle.setImportantForAccessibility(2);
        this.statusLayout.addView(this.callingUserTitle, LayoutHelper.createLinear(-2, -2, 1, 8, 0, 8, 6));
        VoIPStatusTextView voIPStatusTextView = new VoIPStatusTextView(context, this.backgroundProvider);
        this.statusTextView = voIPStatusTextView;
        ViewCompat.setImportantForAccessibility(voIPStatusTextView, 4);
        this.statusLayout.addView(this.statusTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 6));
        this.statusLayout.setClipChildren(false);
        this.statusLayout.setClipToPadding(false);
        this.statusLayout.setPadding(0, 0, 0, AndroidUtilities.dp(15.0f));
        this.endCloseLayout = new EndCloseLayout(context);
        this.rateCallLayout = new RateCallLayout(context, this.backgroundProvider);
        this.endCloseLayout.setAlpha(0.0f);
        this.rateCallLayout.setVisibility(8);
        frameLayout.addView(this.callingUserPhotoViewMini, LayoutHelper.createFrame(204, 204.0f, 1, 0.0f, 93.0f, 0.0f, 0.0f));
        frameLayout.addView(this.statusLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 135.0f, 0.0f, 0.0f));
        frameLayout.addView(this.hideEmojiLayout, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 0.0f, 0.0f, 0.0f));
        frameLayout.addView(this.emojiRationalLayout, LayoutHelper.createFrame(304, -2.0f, 49, 0.0f, 118.0f, 0.0f, 0.0f));
        frameLayout.addView(this.emojiLayout, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 0.0f, 0.0f, 0.0f));
        frameLayout.addView(this.endCloseLayout, LayoutHelper.createFrame(-1, 52.0f, 5, 0.0f, 0.0f, 0.0f, 0.0f));
        frameLayout.addView(this.rateCallLayout, LayoutHelper.createFrame(-1, -2.0f, 3, 0.0f, 380.0f, 0.0f, 0.0f));
        this.buttonsLayout = new VoIPButtonsLayout(context);
        this.bottomSpeakerBtn = new VoIpSwitchLayout(context, this.backgroundProvider);
        this.bottomVideoBtn = new VoIpSwitchLayout(context, this.backgroundProvider);
        this.bottomMuteBtn = new VoIpSwitchLayout(context, this.backgroundProvider);
        this.bottomEndCallBtn = new VoIPToggleButton(context) {
            @Override
            protected void dispatchSetPressed(boolean z) {
                super.dispatchSetPressed(z);
                setPressedBtn(z);
            }
        };
        this.bottomSpeakerBtn.setTranslationY(AndroidUtilities.dp(100.0f));
        this.bottomSpeakerBtn.setScaleX(0.0f);
        this.bottomSpeakerBtn.setScaleY(0.0f);
        this.bottomSpeakerBtn.animate().setStartDelay(150).translationY(0.0f).scaleY(1.0f).scaleX(1.0f).setDuration(250L).start();
        this.bottomVideoBtn.setTranslationY(AndroidUtilities.dp(100.0f));
        this.bottomVideoBtn.setScaleX(0.0f);
        this.bottomVideoBtn.setScaleY(0.0f);
        this.bottomVideoBtn.animate().setStartDelay(166).translationY(0.0f).scaleY(1.0f).scaleX(1.0f).setDuration(250L).start();
        this.bottomMuteBtn.setTranslationY(AndroidUtilities.dp(100.0f));
        this.bottomMuteBtn.setScaleX(0.0f);
        this.bottomMuteBtn.setScaleY(0.0f);
        this.bottomMuteBtn.animate().setStartDelay(182).translationY(0.0f).scaleY(1.0f).scaleX(1.0f).setDuration(250L).start();
        this.bottomEndCallBtn.setTranslationY(AndroidUtilities.dp(100.0f));
        this.bottomEndCallBtn.setScaleX(0.0f);
        this.bottomEndCallBtn.setScaleY(0.0f);
        this.bottomEndCallBtn.animate().setStartDelay(198).translationY(0.0f).scaleY(1.0f).scaleX(1.0f).setDuration(250L).start();
        this.buttonsLayout.addView(this.bottomSpeakerBtn);
        this.buttonsLayout.addView(this.bottomVideoBtn);
        this.buttonsLayout.addView(this.bottomMuteBtn);
        this.buttonsLayout.addView(this.bottomEndCallBtn);
        AcceptDeclineView acceptDeclineView = new AcceptDeclineView(context);
        this.acceptDeclineView = acceptDeclineView;
        acceptDeclineView.setListener(new AnonymousClass8());
        this.acceptDeclineView.setScaleX(1.15f);
        this.acceptDeclineView.setScaleY(1.15f);
        frameLayout.addView(this.buttonsLayout, LayoutHelper.createFrame(-1, -2, 80));
        float f = AndroidUtilities.isTablet() ? 100 : 27;
        frameLayout.addView(this.acceptDeclineView, LayoutHelper.createFrame(-1, 186.0f, 80, f, 0.0f, f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.backIcon = imageView;
        imageView.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 76)));
        this.backIcon.setImageResource(R.drawable.msg_call_minimize_shadow);
        this.backIcon.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        this.backIcon.setContentDescription(LocaleController.getString(R.string.Back));
        frameLayout.addView(this.backIcon, LayoutHelper.createFrame(56, 56, 51));
        ImageView imageView2 = new ImageView(context) {
            @Override
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                accessibilityNodeInfo.setClassName(ToggleButton.class.getName());
                accessibilityNodeInfo.setCheckable(true);
                VoIPService sharedInstance = VoIPService.getSharedInstance();
                if (sharedInstance != null) {
                    accessibilityNodeInfo.setChecked(sharedInstance.isSpeakerphoneOn());
                }
            }
        };
        this.speakerPhoneIcon = imageView2;
        imageView2.setContentDescription(LocaleController.getString(R.string.VoipSpeaker));
        this.speakerPhoneIcon.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 76)));
        this.speakerPhoneIcon.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f));
        frameLayout.addView(this.speakerPhoneIcon, LayoutHelper.createFrame(56, 56, 53));
        this.speakerPhoneIcon.setAlpha(0.0f);
        this.speakerPhoneIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                VoIPFragment.this.lambda$createView$10(view3);
            }
        });
        this.backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                VoIPFragment.this.lambda$createView$11(view3);
            }
        });
        if (this.windowView.isLockOnScreen()) {
            this.backIcon.setVisibility(8);
        }
        VoIPNotificationsLayout voIPNotificationsLayout = new VoIPNotificationsLayout(context, this.backgroundProvider);
        this.notificationsLayout = voIPNotificationsLayout;
        voIPNotificationsLayout.setGravity(80);
        this.notificationsLayout.setOnViewsUpdated(new Runnable() {
            @Override
            public final void run() {
                VoIPFragment.this.lambda$createView$12();
            }
        });
        frameLayout.addView(this.notificationsLayout, LayoutHelper.createFrame(-1, 200.0f, 80, 16.0f, 0.0f, 16.0f, 0.0f));
        HintView2 multilineText = new VoIpHintView(context, 3, this.backgroundProvider, true).setMultilineText(true);
        Layout.Alignment alignment = Layout.Alignment.ALIGN_CENTER;
        HintView2 rounding = multilineText.setTextAlign(alignment).setDuration(-1L).setOnHiddenListener(new Runnable() {
            @Override
            public final void run() {
                VoIPFragment.this.startWaitingFoHideUi();
            }
        }).setHideByTouch(true).setMaxWidth(320.0f).useScale(true).setInnerPadding(10.0f, 6.0f, 10.0f, 6.0f).setRounding(8.0f);
        this.tapToVideoTooltip = rounding;
        rounding.setText(LocaleController.getString(R.string.TapToTurnCamera));
        frameLayout.addView(this.tapToVideoTooltip, LayoutHelper.createFrame(-2, -2.0f, 80, 19.0f, 0.0f, 19.0f, 0.0f));
        HintView2 rounding2 = new VoIpHintView(context, 1, this.backgroundProvider, false).setMultilineText(true).setTextAlign(alignment).setDuration(4000L).setHideByTouch(true).setMaxWidth(320.0f).useScale(true).setInnerPadding(10.0f, 6.0f, 10.0f, 6.0f).setRounding(8.0f);
        this.encryptionTooltip = rounding2;
        rounding2.setText(LocaleController.getString(R.string.VoipHintEncryptionKey));
        frameLayout.addView(this.encryptionTooltip, LayoutHelper.createFrame(-2, -2.0f, 1, 0.0f, 0.0f, 0.0f, 0.0f));
        updateViewState();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (!this.isVideoCall) {
                TL_phone.PhoneCall phoneCall = sharedInstance.privateCall;
                this.isVideoCall = phoneCall != null && phoneCall.video;
            }
            initRenderers();
        }
        return frameLayout;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.voipServiceCreated) {
            if (this.currentState != 17 || VoIPService.getSharedInstance() == null) {
                return;
            }
            this.currentUserTextureView.renderer.release();
            this.callingUserTextureView.renderer.release();
            this.callingUserMiniTextureRenderer.release();
            initRenderers();
            VoIPService.getSharedInstance().registerStateListener(this);
            return;
        }
        if (i == NotificationCenter.emojiLoaded) {
            updateKeyView(true);
            return;
        }
        if (i == NotificationCenter.closeInCallActivity) {
            this.windowView.finish();
            return;
        }
        if (i == NotificationCenter.webRtcSpeakerAmplitudeEvent) {
            this.callingUserPhotoViewMini.setAmplitude(((Float) objArr[0]).floatValue() * 15.0f);
            return;
        }
        if (i == NotificationCenter.nearEarEvent) {
            boolean booleanValue = ((Boolean) objArr[0]).booleanValue();
            this.isNearEar = booleanValue;
            if (booleanValue) {
                this.callingUserPhotoViewMini.setMute(true, true);
            }
        }
    }

    @Override
    public void onAudioSettingsChanged() {
        updateButtons(true);
    }

    @Override
    public void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    @Override
    public void onCameraSwitch(boolean z) {
        this.previousState = this.currentState;
        updateViewState();
    }

    @Override
    public void onMediaStateUpdated(int i, int i2) {
        this.previousState = this.currentState;
        if (i2 == 2 && !this.isVideoCall) {
            this.isVideoCall = true;
        }
        updateViewState();
    }

    public void onPauseInternal() {
        VoIPService sharedInstance;
        WindowInsets windowInsets;
        int systemWindowInsetTop;
        int systemWindowInsetBottom;
        PowerManager powerManager = (PowerManager) this.activity.getSystemService("power");
        int i = Build.VERSION.SDK_INT;
        boolean isInteractive = i >= 20 ? powerManager.isInteractive() : powerManager.isScreenOn();
        boolean checkInlinePermissions = AndroidUtilities.checkInlinePermissions(this.activity);
        if (this.canSwitchToPip && checkInlinePermissions) {
            int measuredHeight = instance.windowView.getMeasuredHeight();
            VoIPFragment voIPFragment = instance;
            VoIPPiPView.show(voIPFragment.activity, voIPFragment.currentAccount, voIPFragment.windowView.getMeasuredWidth(), measuredHeight, 0);
            if (i >= 20 && (windowInsets = instance.lastInsets) != null) {
                systemWindowInsetTop = windowInsets.getSystemWindowInsetTop();
                VoIPPiPView.topInset = systemWindowInsetTop;
                systemWindowInsetBottom = instance.lastInsets.getSystemWindowInsetBottom();
                VoIPPiPView.bottomInset = systemWindowInsetBottom;
            }
        }
        if (this.currentUserIsVideo) {
            if ((checkInlinePermissions && isInteractive) || (sharedInstance = VoIPService.getSharedInstance()) == null) {
                return;
            }
            sharedInstance.setVideoState(false, 1);
        }
    }

    public void onResumeInternal() {
        if (VoIPPiPView.getInstance() != null) {
            VoIPPiPView.finish();
        }
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (sharedInstance.getVideoState(false) == 1) {
                sharedInstance.setVideoState(false, 2);
            }
            updateViewState();
        } else if (VoIPService.getSharedState() == null) {
            this.windowView.finish();
        }
        this.deviceIsLocked = ((KeyguardManager) this.activity.getSystemService("keyguard")).inKeyguardRestrictedInputMode();
    }

    public void onScreenCastStart() {
        PrivateVideoPreviewDialogNew privateVideoPreviewDialogNew = this.previewDialog;
        if (privateVideoPreviewDialogNew == null) {
            return;
        }
        privateVideoPreviewDialogNew.dismiss(true, true);
    }

    @Override
    public void onScreenOnChange(boolean z) {
    }

    @Override
    public void onSignalBarsCountChanged(final int i) {
        VoIpGradientLayout voIpGradientLayout;
        if (i > 0) {
            this.signalBarWasReceived = true;
        }
        if (this.statusTextView == null || (voIpGradientLayout = this.gradientLayout) == null || !voIpGradientLayout.isConnectedCalled() || !this.signalBarWasReceived) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VoIPFragment.this.lambda$onSignalBarsCountChanged$4(i);
            }
        }, 400L);
    }

    @Override
    public void onStateChanged(int i) {
        int i2 = this.currentState;
        if (i2 != i) {
            this.previousState = i2;
            this.currentState = i;
            if (this.windowView != null) {
                updateViewState();
            }
        }
    }

    @Override
    public void onVideoAvailableChange(boolean z) {
        this.previousState = this.currentState;
        if (z && !this.isVideoCall) {
            this.isVideoCall = true;
        }
        updateViewState();
    }

    public TLRPC.Document replaceEmojiToLottieFrame(CharSequence charSequence, int[] iArr) {
        if (!(charSequence instanceof Spannable)) {
            return null;
        }
        Spannable spannable = (Spannable) charSequence;
        Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) spannable.getSpans(0, spannable.length(), Emoji.EmojiSpan.class);
        AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spannable.getSpans(0, spannable.length(), AnimatedEmojiSpan.class);
        if (emojiSpanArr != null) {
            if (((iArr == null ? 0 : iArr[0]) - emojiSpanArr.length) - (animatedEmojiSpanArr == null ? 0 : animatedEmojiSpanArr.length) <= 0 && emojiSpanArr.length > 0) {
                return MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(emojiSpanArr[0].emoji);
            }
        }
        return null;
    }

    public void startTransitionFromPiP() {
        this.enterFromPiP = true;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null && sharedInstance.getVideoState(false) == 2) {
            this.callingUserTextureView.setStub(VoIPPiPView.getInstance().callingUserTextureView);
            this.currentUserTextureView.setStub(VoIPPiPView.getInstance().currentUserTextureView);
        }
        this.windowView.setAlpha(0.0f);
        updateViewState();
        this.switchingToPip = true;
        VoIPPiPView.switchingToPip = true;
        VoIPPiPView.prepareForTransition();
        this.notificationsLocker.lock();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                VoIPFragment.this.lambda$startTransitionFromPiP$17();
            }
        }, 32L);
    }

    public void switchToPip() {
        WindowInsets windowInsets;
        int systemWindowInsetTop;
        int systemWindowInsetBottom;
        if (this.isFinished || !AndroidUtilities.checkInlinePermissions(this.activity) || instance == null) {
            return;
        }
        this.isFinished = true;
        if (VoIPService.getSharedInstance() != null) {
            int measuredHeight = instance.windowView.getMeasuredHeight();
            VoIPFragment voIPFragment = instance;
            VoIPPiPView.show(voIPFragment.activity, voIPFragment.currentAccount, voIPFragment.windowView.getMeasuredWidth(), measuredHeight, 1);
            if (Build.VERSION.SDK_INT >= 20 && (windowInsets = instance.lastInsets) != null) {
                systemWindowInsetTop = windowInsets.getSystemWindowInsetTop();
                VoIPPiPView.topInset = systemWindowInsetTop;
                systemWindowInsetBottom = instance.lastInsets.getSystemWindowInsetBottom();
                VoIPPiPView.bottomInset = systemWindowInsetBottom;
            }
        }
        if (VoIPPiPView.getInstance() == null) {
            return;
        }
        ViewPropertyAnimator duration = this.speakerPhoneIcon.animate().alpha(0.0f).setDuration(150L);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
        duration.setInterpolator(cubicBezierInterpolator).start();
        this.backIcon.animate().alpha(0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
        this.emojiLayout.animate().alpha(0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
        this.statusLayout.animate().alpha(0.0f).setDuration(150L).setInterpolator(cubicBezierInterpolator).start();
        this.buttonsLayout.animate().alpha(0.0f).setDuration(350L).setInterpolator(cubicBezierInterpolator).start();
        this.bottomShadow.animate().alpha(0.0f).setDuration(350L).setInterpolator(cubicBezierInterpolator).start();
        this.topShadow.animate().alpha(0.0f).setDuration(350L).setInterpolator(cubicBezierInterpolator).start();
        this.callingUserMiniFloatingLayout.animate().alpha(0.0f).setDuration(350L).setInterpolator(cubicBezierInterpolator).start();
        this.notificationsLayout.animate().alpha(0.0f).setDuration(350L).setInterpolator(cubicBezierInterpolator).start();
        VoIPPiPView.switchingToPip = true;
        this.switchingToPip = true;
        Animator createPiPTransition = createPiPTransition(false);
        this.notificationsLocker.lock();
        createPiPTransition.addListener(new AnonymousClass14());
        createPiPTransition.setDuration(350L);
        createPiPTransition.setInterpolator(cubicBezierInterpolator);
        createPiPTransition.start();
    }
}
