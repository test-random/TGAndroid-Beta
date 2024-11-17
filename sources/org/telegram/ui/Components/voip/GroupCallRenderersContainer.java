package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.SystemClock;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarsImageView;
import org.telegram.ui.Components.CrossOutDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.GroupCallFullscreenAdapter;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.GroupCallActivity;

public abstract class GroupCallRenderersContainer extends FrameLayout {
    private boolean animateSpeakingOnNextDraw;
    private LongSparseIntArray attachedPeerIds;
    private final ArrayList attachedRenderers;
    private final ImageView backButton;
    ChatObject.Call call;
    private boolean canZoomGesture;
    private boolean drawFirst;
    private boolean drawRenderesOnly;
    ValueAnimator fullscreenAnimator;
    private final RecyclerView fullscreenListView;
    public ChatObject.VideoParticipant fullscreenParticipant;
    public long fullscreenPeerId;
    public GroupCallMiniTextureView fullscreenTextureView;
    GroupCallActivity groupCallActivity;
    public boolean hasPinnedVideo;
    Runnable hideUiRunnable;
    boolean hideUiRunnableIsScheduled;
    public boolean inFullscreenMode;
    public boolean inLayout;
    private boolean isInPinchToZoomTouchMode;
    private boolean isTablet;
    public long lastUpdateTime;
    long lastUpdateTooltipTime;
    private final RecyclerView listView;
    public int listWidth;
    boolean maybeSwipeToBackGesture;
    private boolean notDrawRenderes;
    AnimationNotificationsLocker notificationsLocker;
    private GroupCallMiniTextureView outFullscreenTextureView;
    private final ImageView pinButton;
    View pinContainer;
    CrossOutDrawable pinDrawable;
    TextView pinTextView;
    private float pinchCenterX;
    private float pinchCenterY;
    float pinchScale;
    private float pinchStartCenterX;
    private float pinchStartCenterY;
    private float pinchStartDistance;
    private float pinchTranslationX;
    private float pinchTranslationY;
    public ImageView pipView;
    private int pointerId1;
    private int pointerId2;
    public float progressToFullscreenMode;
    float progressToHideUi;
    public float progressToScrimView;
    ValueAnimator replaceFullscreenViewAnimator;
    Drawable rightShadowDrawable;
    private final View rightShadowView;
    private boolean showSpeakingMembersToast;
    private float showSpeakingMembersToastProgress;
    private final AvatarsImageView speakingMembersAvatars;
    private final TextView speakingMembersText;
    private final FrameLayout speakingMembersToast;
    private float speakingMembersToastChangeProgress;
    private float speakingMembersToastFromLeft;
    private float speakingMembersToastFromRight;
    private float speakingMembersToastFromTextLeft;
    private long speakingToastPeerId;
    ValueAnimator swipeToBackAnimator;
    float swipeToBackDy;
    boolean swipeToBackGesture;
    public boolean swipedBack;
    boolean tapGesture;
    long tapTime;
    float tapX;
    float tapY;
    Drawable topShadowDrawable;
    private final View topShadowView;
    private final int touchSlop;
    boolean uiVisible;
    public UndoView[] undoView;
    TextView unpinTextView;
    Runnable updateTooltipRunnbale;
    ValueAnimator zoomBackAnimator;
    private boolean zoomStarted;

    public GroupCallRenderersContainer(Context context, RecyclerView recyclerView, RecyclerView recyclerView2, ArrayList arrayList, ChatObject.Call call, final GroupCallActivity groupCallActivity) {
        super(context);
        this.attachedPeerIds = new LongSparseIntArray();
        this.notificationsLocker = new AnimationNotificationsLocker();
        this.speakingMembersToastChangeProgress = 1.0f;
        this.animateSpeakingOnNextDraw = true;
        this.uiVisible = true;
        this.hideUiRunnable = new Runnable() {
            @Override
            public void run() {
                if (!GroupCallRenderersContainer.this.canHideUI()) {
                    AndroidUtilities.runOnUIThread(GroupCallRenderersContainer.this.hideUiRunnable, 3000L);
                    return;
                }
                GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                groupCallRenderersContainer.hideUiRunnableIsScheduled = false;
                groupCallRenderersContainer.setUiVisible(false);
            }
        };
        this.pinchScale = 1.0f;
        this.undoView = new UndoView[2];
        this.listView = recyclerView;
        this.fullscreenListView = recyclerView2;
        this.attachedRenderers = arrayList;
        this.call = call;
        this.groupCallActivity = groupCallActivity;
        ImageView imageView = new ImageView(context) {
            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(ActionBar.getCurrentActionBarHeight(), 1073741824));
            }
        };
        this.backButton = imageView;
        BackDrawable backDrawable = new BackDrawable(false);
        backDrawable.setColor(-1);
        imageView.setImageDrawable(backDrawable);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        imageView.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 55)));
        View view = new View(context);
        this.topShadowView = view;
        Drawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{0, ColorUtils.setAlphaComponent(-16777216, 114)});
        this.topShadowDrawable = gradientDrawable;
        view.setBackground(gradientDrawable);
        addView(view, LayoutHelper.createFrame(-1, 120.0f));
        View view2 = new View(context);
        this.rightShadowView = view2;
        Drawable gradientDrawable2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0, ColorUtils.setAlphaComponent(-16777216, 114)});
        this.rightShadowDrawable = gradientDrawable2;
        view2.setBackground(gradientDrawable2);
        view2.setVisibility((call == null || !isRtmpStream()) ? 8 : 0);
        addView(view2, LayoutHelper.createFrame(160, -1, 5));
        addView(imageView, LayoutHelper.createFrame(56, -1, 51));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                GroupCallRenderersContainer.this.lambda$new$0(view3);
            }
        });
        ImageView imageView2 = new ImageView(context) {
            @Override
            public void invalidate() {
                super.invalidate();
                GroupCallRenderersContainer.this.pinContainer.invalidate();
                GroupCallRenderersContainer.this.invalidate();
            }

            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(ActionBar.getCurrentActionBarHeight(), 1073741824));
            }
        };
        this.pinButton = imageView2;
        final Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(20.0f), 0, ColorUtils.setAlphaComponent(-1, 100));
        View view3 = new View(context) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                float measuredWidth = (GroupCallRenderersContainer.this.pinTextView.getMeasuredWidth() * (1.0f - GroupCallRenderersContainer.this.pinDrawable.getProgress())) + (GroupCallRenderersContainer.this.unpinTextView.getMeasuredWidth() * GroupCallRenderersContainer.this.pinDrawable.getProgress());
                canvas.save();
                createSimpleSelectorRoundRectDrawable.setBounds(0, 0, AndroidUtilities.dp(50.0f) + ((int) measuredWidth), getMeasuredHeight());
                createSimpleSelectorRoundRectDrawable.draw(canvas);
                super.dispatchDraw(canvas);
            }

            @Override
            protected void drawableStateChanged() {
                super.drawableStateChanged();
                createSimpleSelectorRoundRectDrawable.setState(getDrawableState());
            }

            @Override
            public void jumpDrawablesToCurrentState() {
                super.jumpDrawablesToCurrentState();
                createSimpleSelectorRoundRectDrawable.jumpToCurrentState();
            }

            @Override
            public boolean verifyDrawable(Drawable drawable) {
                return createSimpleSelectorRoundRectDrawable == drawable || super.verifyDrawable(drawable);
            }
        };
        this.pinContainer = view3;
        view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view4) {
                GroupCallRenderersContainer.this.lambda$new$1(view4);
            }
        });
        createSimpleSelectorRoundRectDrawable.setCallback(this.pinContainer);
        addView(this.pinContainer);
        CrossOutDrawable crossOutDrawable = new CrossOutDrawable(context, R.drawable.msg_pin_filled, -1);
        this.pinDrawable = crossOutDrawable;
        crossOutDrawable.setOffsets(-AndroidUtilities.dp(1.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(1.0f));
        imageView2.setImageDrawable(this.pinDrawable);
        imageView2.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        addView(imageView2, LayoutHelper.createFrame(56, -1, 51));
        TextView textView = new TextView(context);
        this.pinTextView = textView;
        textView.setTextColor(-1);
        this.pinTextView.setTextSize(1, 15.0f);
        this.pinTextView.setTypeface(AndroidUtilities.bold());
        this.pinTextView.setText(LocaleController.getString(R.string.CallVideoPin));
        TextView textView2 = new TextView(context);
        this.unpinTextView = textView2;
        textView2.setTextColor(-1);
        this.unpinTextView.setTextSize(1, 15.0f);
        this.unpinTextView.setTypeface(AndroidUtilities.bold());
        this.unpinTextView.setText(LocaleController.getString(R.string.CallVideoUnpin));
        addView(this.pinTextView, LayoutHelper.createFrame(-2, -2, 51));
        addView(this.unpinTextView, LayoutHelper.createFrame(-2, -2, 51));
        ImageView imageView3 = new ImageView(context);
        this.pipView = imageView3;
        imageView3.setVisibility(4);
        this.pipView.setAlpha(0.0f);
        this.pipView.setImageResource(R.drawable.ic_goinline);
        this.pipView.setContentDescription(LocaleController.getString(R.string.AccDescrPipMode));
        int dp = AndroidUtilities.dp(4.0f);
        this.pipView.setPadding(dp, dp, dp, dp);
        this.pipView.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(-1, 55)));
        this.pipView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view4) {
                GroupCallRenderersContainer.this.lambda$new$2(groupCallActivity, view4);
            }
        });
        addView(this.pipView, LayoutHelper.createFrame(32, 32.0f, 53, 12.0f, 12.0f, 12.0f, 12.0f));
        final ShapeDrawable createRoundRectDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_listViewBackground), 204));
        FrameLayout frameLayout = new FrameLayout(context) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                if (GroupCallRenderersContainer.this.speakingMembersToastChangeProgress == 1.0f) {
                    createRoundRectDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    GroupCallRenderersContainer.this.speakingMembersAvatars.setTranslationX(0.0f);
                    GroupCallRenderersContainer.this.speakingMembersText.setTranslationX(0.0f);
                } else {
                    float interpolation = 1.0f - CubicBezierInterpolator.DEFAULT.getInterpolation(GroupCallRenderersContainer.this.speakingMembersToastChangeProgress);
                    float left = (GroupCallRenderersContainer.this.speakingMembersToastFromLeft - getLeft()) * interpolation;
                    float left2 = (GroupCallRenderersContainer.this.speakingMembersToastFromTextLeft - GroupCallRenderersContainer.this.speakingMembersText.getLeft()) * interpolation;
                    createRoundRectDrawable.setBounds((int) left, 0, getMeasuredWidth() + ((int) ((GroupCallRenderersContainer.this.speakingMembersToastFromRight - getRight()) * interpolation)), getMeasuredHeight());
                    GroupCallRenderersContainer.this.speakingMembersAvatars.setTranslationX(left);
                    GroupCallRenderersContainer.this.speakingMembersText.setTranslationX(-left2);
                }
                createRoundRectDrawable.draw(canvas);
                super.dispatchDraw(canvas);
            }
        };
        this.speakingMembersToast = frameLayout;
        AvatarsImageView avatarsImageView = new AvatarsImageView(context, true);
        this.speakingMembersAvatars = avatarsImageView;
        avatarsImageView.setStyle(10);
        frameLayout.setClipChildren(false);
        frameLayout.setClipToPadding(false);
        frameLayout.addView(avatarsImageView, LayoutHelper.createFrame(100, 32.0f, 16, 0.0f, 0.0f, 0.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.speakingMembersText = textView3;
        textView3.setTextSize(1, 14.0f);
        textView3.setTextColor(-1);
        textView3.setLines(1);
        textView3.setEllipsize(TextUtils.TruncateAt.END);
        frameLayout.addView(textView3, LayoutHelper.createFrame(-2, -2, 16));
        addView(frameLayout, LayoutHelper.createFrame(-2, 36.0f, 1, 0.0f, 0.0f, 0.0f, 0.0f));
        this.touchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        for (int i = 0; i < 2; i++) {
            this.undoView[i] = new UndoView(context) {
                @Override
                public void invalidate() {
                    super.invalidate();
                    GroupCallRenderersContainer.this.invalidate();
                }
            };
            this.undoView[i].setHideAnimationType(2);
            this.undoView[i].setAdditionalTranslationY(AndroidUtilities.dp(10.0f));
            addView(this.undoView[i], LayoutHelper.createFrame(-1, -2.0f, 80, 16.0f, 0.0f, 0.0f, 8.0f));
        }
        this.pinContainer.setVisibility(8);
        setIsTablet(GroupCallActivity.isTabletMode);
    }

    private void animateSwipeToBack(boolean z) {
        ValueAnimator ofFloat;
        if (this.swipeToBackGesture) {
            this.swipeToBackGesture = false;
            float f = this.swipeToBackDy;
            float[] fArr = new float[2];
            if (z) {
                fArr[0] = f;
                fArr[1] = 0.0f;
                ofFloat = ValueAnimator.ofFloat(fArr);
            } else {
                fArr[0] = f;
                fArr[1] = 0.0f;
                ofFloat = ValueAnimator.ofFloat(fArr);
            }
            this.swipeToBackAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    GroupCallRenderersContainer.this.lambda$animateSwipeToBack$7(valueAnimator);
                }
            });
            this.swipeToBackAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                    groupCallRenderersContainer.swipeToBackAnimator = null;
                    groupCallRenderersContainer.swipeToBackDy = 0.0f;
                    groupCallRenderersContainer.invalidate();
                }
            });
            ValueAnimator valueAnimator = this.swipeToBackAnimator;
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            valueAnimator.setInterpolator(cubicBezierInterpolator);
            this.swipeToBackAnimator.setDuration(z ? 350L : 200L);
            this.swipeToBackAnimator.setInterpolator(cubicBezierInterpolator);
            GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
            if (groupCallMiniTextureView != null) {
                groupCallMiniTextureView.textureView.synchOrRunAnimation(this.swipeToBackAnimator);
            } else {
                this.swipeToBackAnimator.start();
            }
            this.lastUpdateTime = System.currentTimeMillis();
        }
        this.maybeSwipeToBackGesture = false;
    }

    private boolean checkPointerIds(MotionEvent motionEvent) {
        if (motionEvent.getPointerCount() < 2) {
            return false;
        }
        if (this.pointerId1 == motionEvent.getPointerId(0) && this.pointerId2 == motionEvent.getPointerId(1)) {
            return true;
        }
        return this.pointerId1 == motionEvent.getPointerId(1) && this.pointerId2 == motionEvent.getPointerId(0);
    }

    public void clearCurrentFullscreenTextureView() {
        GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
        if (groupCallMiniTextureView != null) {
            groupCallMiniTextureView.setSwipeToBack(false, 0.0f);
            this.fullscreenTextureView.setZoom(false, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        }
    }

    private void finishZoom() {
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
                    GroupCallRenderersContainer.this.lambda$finishZoom$8(f, f2, f3, valueAnimator);
                }
            });
            this.zoomBackAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                    groupCallRenderersContainer.zoomBackAnimator = null;
                    groupCallRenderersContainer.pinchScale = 1.0f;
                    groupCallRenderersContainer.pinchTranslationX = 0.0f;
                    GroupCallRenderersContainer.this.pinchTranslationY = 0.0f;
                    GroupCallRenderersContainer.this.invalidate();
                }
            });
            this.zoomBackAnimator.setDuration(350L);
            this.zoomBackAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.zoomBackAnimator.start();
            this.lastUpdateTime = System.currentTimeMillis();
        }
        this.canZoomGesture = false;
        this.isInPinchToZoomTouchMode = false;
    }

    private boolean isRtmpStream() {
        ChatObject.Call call = this.call;
        return call != null && call.call.rtmp_stream;
    }

    public void lambda$animateSwipeToBack$7(ValueAnimator valueAnimator) {
        this.swipeToBackDy = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void lambda$finishZoom$8(float f, float f2, float f3, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.pinchScale = (f * floatValue) + ((1.0f - floatValue) * 1.0f);
        this.pinchTranslationX = f2 * floatValue;
        this.pinchTranslationY = f3 * floatValue;
        invalidate();
    }

    public void lambda$new$0(View view) {
        onBackPressed();
    }

    public void lambda$new$1(View view) {
        if (this.inFullscreenMode) {
            boolean z = !this.hasPinnedVideo;
            this.hasPinnedVideo = z;
            this.pinDrawable.setCrossOut(z, true);
            requestLayout();
        }
    }

    public void lambda$new$2(GroupCallActivity groupCallActivity, View view) {
        if (isRtmpStream()) {
            if (!AndroidUtilities.checkInlinePermissions(groupCallActivity.getParentActivity())) {
                AlertsCreator.createDrawOverlayPermissionDialog(groupCallActivity.getParentActivity(), null).show();
                return;
            } else {
                RTMPStreamPipOverlay.show();
                groupCallActivity.dismiss();
                return;
            }
        }
        if (!AndroidUtilities.checkInlinePermissions(groupCallActivity.getParentActivity())) {
            AlertsCreator.createDrawOverlayGroupCallPermissionDialog(getContext()).show();
        } else {
            GroupCallPip.clearForce();
            groupCallActivity.dismiss();
        }
    }

    public void lambda$requestFullscreen$3(final GroupCallMiniTextureView groupCallMiniTextureView, final GroupCallMiniTextureView groupCallMiniTextureView2) {
        ValueAnimator valueAnimator = this.replaceFullscreenViewAnimator;
        if (valueAnimator != null) {
            valueAnimator.start();
        }
        groupCallMiniTextureView.animate().scaleX(0.5f).scaleY(0.5f).alpha(0.0f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (groupCallMiniTextureView.getParent() != null) {
                    GroupCallRenderersContainer.this.removeView(groupCallMiniTextureView);
                    groupCallMiniTextureView.release();
                }
            }
        }).setDuration(100L).start();
        if (groupCallMiniTextureView2 != null) {
            groupCallMiniTextureView2.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(100L).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    groupCallMiniTextureView2.animateEnter = false;
                }
            }).start();
        }
    }

    public void lambda$requestFullscreen$4(final GroupCallMiniTextureView groupCallMiniTextureView) {
        groupCallMiniTextureView.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                groupCallMiniTextureView.animateEnter = false;
            }
        }).setDuration(150L).start();
    }

    public void lambda$requestFullscreen$5(GroupCallMiniTextureView groupCallMiniTextureView, ValueAnimator valueAnimator) {
        groupCallMiniTextureView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
        invalidate();
    }

    public void lambda$requestFullscreen$6(ValueAnimator valueAnimator) {
        this.progressToFullscreenMode = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.groupCallActivity.getMenuItemsContainer().setAlpha(1.0f - this.progressToFullscreenMode);
        this.groupCallActivity.invalidateActionBarAlpha();
        this.groupCallActivity.invalidateScrollOffsetY();
        update();
    }

    public void lambda$setVisibleParticipant$9() {
        this.updateTooltipRunnbale = null;
        setVisibleParticipant(true);
    }

    public void setUiVisible(boolean z) {
        if (this.uiVisible != z) {
            this.uiVisible = z;
            onUiVisibilityChanged();
            if (!z || !this.inFullscreenMode) {
                this.hideUiRunnableIsScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(this.hideUiRunnable);
            } else if (!this.hideUiRunnableIsScheduled) {
                this.hideUiRunnableIsScheduled = true;
                AndroidUtilities.runOnUIThread(this.hideUiRunnable, 3000L);
            }
            GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
            if (groupCallMiniTextureView != null) {
                groupCallMiniTextureView.requestLayout();
            }
        }
    }

    public void attach(GroupCallMiniTextureView groupCallMiniTextureView) {
        this.attachedRenderers.add(groupCallMiniTextureView);
        long peerId = MessageObject.getPeerId(groupCallMiniTextureView.participant.participant.peer);
        LongSparseIntArray longSparseIntArray = this.attachedPeerIds;
        longSparseIntArray.put(peerId, longSparseIntArray.get(peerId, 0) + 1);
    }

    public boolean autoPinEnabled() {
        return (this.hasPinnedVideo || System.currentTimeMillis() - this.lastUpdateTime <= 2000 || this.swipeToBackGesture || this.isInPinchToZoomTouchMode) ? false : true;
    }

    public boolean canHideUI() {
        return this.inFullscreenMode;
    }

    public void delayHideUi() {
        if (this.hideUiRunnableIsScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.hideUiRunnable);
        }
        AndroidUtilities.runOnUIThread(this.hideUiRunnable, 3000L);
        this.hideUiRunnableIsScheduled = true;
    }

    public void detach(GroupCallMiniTextureView groupCallMiniTextureView) {
        this.attachedRenderers.remove(groupCallMiniTextureView);
        long peerId = MessageObject.getPeerId(groupCallMiniTextureView.participant.participant.peer);
        this.attachedPeerIds.put(peerId, r4.get(peerId, 0) - 1);
    }

    @Override
    protected void dispatchDraw(android.graphics.Canvas r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallRenderersContainer.dispatchDraw(android.graphics.Canvas):void");
    }

    @Override
    public boolean drawChild(Canvas canvas, View view, long j) {
        if (!this.drawFirst) {
            UndoView[] undoViewArr = this.undoView;
            if (view == undoViewArr[0] || view == undoViewArr[1]) {
                return true;
            }
            if (!(view instanceof GroupCallMiniTextureView)) {
                if (this.drawRenderesOnly) {
                    return true;
                }
                return super.drawChild(canvas, view, j);
            }
            GroupCallMiniTextureView groupCallMiniTextureView = (GroupCallMiniTextureView) view;
            if (groupCallMiniTextureView == this.fullscreenTextureView || groupCallMiniTextureView == this.outFullscreenTextureView || this.notDrawRenderes || groupCallMiniTextureView.drawFirst) {
                return true;
            }
            if (groupCallMiniTextureView.primaryView != null) {
                float y = this.listView.getY() - getTop();
                float measuredHeight = (this.listView.getMeasuredHeight() + y) - this.listView.getTranslationY();
                float f = this.progressToFullscreenMode;
                if (groupCallMiniTextureView.secondaryView == null) {
                    f = 0.0f;
                }
                canvas.save();
                float f2 = 1.0f - f;
                canvas.clipRect(0.0f, y * f2, getMeasuredWidth(), (measuredHeight * f2) + (getMeasuredHeight() * f));
            } else {
                if (!GroupCallActivity.isTabletMode) {
                    return super.drawChild(canvas, view, j);
                }
                canvas.save();
                canvas.clipRect(0, 0, getMeasuredWidth(), getMeasuredHeight());
            }
        } else {
            if (!(view instanceof GroupCallMiniTextureView) || !((GroupCallMiniTextureView) view).drawFirst) {
                return true;
            }
            float y2 = this.listView.getY() - getTop();
            float measuredHeight2 = (this.listView.getMeasuredHeight() + y2) - this.listView.getTranslationY();
            canvas.save();
            canvas.clipRect(0.0f, y2, getMeasuredWidth(), measuredHeight2);
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restore();
        return drawChild;
    }

    public UndoView getUndoView() {
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView undoView = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = undoView;
            undoView.hide(true, 2);
            removeView(this.undoView[0]);
            addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    public boolean isAnimating() {
        return this.fullscreenAnimator != null;
    }

    public boolean isUiVisible() {
        return this.uiVisible;
    }

    public boolean isVisible(TLRPC.TL_groupCallParticipant tL_groupCallParticipant) {
        return this.attachedPeerIds.get(MessageObject.getPeerId(tL_groupCallParticipant.peer)) > 0;
    }

    protected abstract void onBackPressed();

    protected abstract void onFullScreenModeChanged(boolean z);

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return onTouchEvent(motionEvent);
    }

    @Override
    protected void onMeasure(int r5, int r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallRenderersContainer.onMeasure(int, int):void");
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallRenderersContainer.onTouchEvent(android.view.MotionEvent):boolean");
    }

    protected abstract void onUiVisibilityChanged();

    public void requestFullscreen(ChatObject.VideoParticipant videoParticipant) {
        final GroupCallMiniTextureView groupCallMiniTextureView;
        ChatObject.VideoParticipant videoParticipant2;
        if (videoParticipant == null && this.fullscreenParticipant == null) {
            return;
        }
        if (videoParticipant == null || !videoParticipant.equals(this.fullscreenParticipant)) {
            long peerId = videoParticipant == null ? 0L : MessageObject.getPeerId(videoParticipant.participant.peer);
            GroupCallMiniTextureView groupCallMiniTextureView2 = this.fullscreenTextureView;
            if (groupCallMiniTextureView2 != null) {
                groupCallMiniTextureView2.runDelayedAnimations();
            }
            ValueAnimator valueAnimator = this.replaceFullscreenViewAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (sharedInstance != null && (videoParticipant2 = this.fullscreenParticipant) != null) {
                sharedInstance.requestFullScreen(videoParticipant2.participant, false, videoParticipant2.presentation);
            }
            this.fullscreenParticipant = videoParticipant;
            if (sharedInstance != null && videoParticipant != null) {
                sharedInstance.requestFullScreen(videoParticipant.participant, true, videoParticipant.presentation);
            }
            this.fullscreenPeerId = peerId;
            boolean z = this.inFullscreenMode;
            this.lastUpdateTime = System.currentTimeMillis();
            final GroupCallMiniTextureView groupCallMiniTextureView3 = null;
            if (videoParticipant == null) {
                if (this.inFullscreenMode) {
                    ValueAnimator valueAnimator2 = this.fullscreenAnimator;
                    if (valueAnimator2 != null) {
                        valueAnimator2.cancel();
                    }
                    this.inFullscreenMode = false;
                    GroupCallMiniTextureView groupCallMiniTextureView4 = this.fullscreenTextureView;
                    if (groupCallMiniTextureView4.primaryView != null || groupCallMiniTextureView4.secondaryView != null || groupCallMiniTextureView4.tabletGridView != null) {
                        ChatObject.VideoParticipant videoParticipant3 = groupCallMiniTextureView4.participant;
                        if (ChatObject.Call.videoIsActive(videoParticipant3.participant, videoParticipant3.presentation, this.call)) {
                            this.fullscreenTextureView.setShowingInFullscreen(false, true);
                        }
                    }
                    this.fullscreenTextureView.forceDetach(true);
                    GroupCallGridCell groupCallGridCell = this.fullscreenTextureView.primaryView;
                    if (groupCallGridCell != null) {
                        groupCallGridCell.setRenderer(null);
                    }
                    GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = this.fullscreenTextureView.secondaryView;
                    if (groupCallUserCell != null) {
                        groupCallUserCell.setRenderer(null);
                    }
                    GroupCallGridCell groupCallGridCell2 = this.fullscreenTextureView.tabletGridView;
                    if (groupCallGridCell2 != null) {
                        groupCallGridCell2.setRenderer(null);
                    }
                    final GroupCallMiniTextureView groupCallMiniTextureView5 = this.fullscreenTextureView;
                    groupCallMiniTextureView5.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (groupCallMiniTextureView5.getParent() != null) {
                                GroupCallRenderersContainer.this.removeView(groupCallMiniTextureView5);
                                groupCallMiniTextureView5.release();
                            }
                        }
                    }).setDuration(350L).start();
                }
                this.backButton.setEnabled(false);
                this.hasPinnedVideo = false;
            } else {
                int i = 0;
                while (true) {
                    if (i >= this.attachedRenderers.size()) {
                        groupCallMiniTextureView = null;
                        break;
                    } else {
                        if (((GroupCallMiniTextureView) this.attachedRenderers.get(i)).participant.equals(videoParticipant)) {
                            groupCallMiniTextureView = (GroupCallMiniTextureView) this.attachedRenderers.get(i);
                            break;
                        }
                        i++;
                    }
                }
                if (groupCallMiniTextureView != null) {
                    ValueAnimator valueAnimator3 = this.fullscreenAnimator;
                    if (valueAnimator3 != null) {
                        valueAnimator3.cancel();
                    }
                    if (this.inFullscreenMode) {
                        this.hasPinnedVideo = false;
                        this.pinDrawable.setCrossOut(false, false);
                        this.fullscreenTextureView.forceDetach(false);
                        groupCallMiniTextureView.forceDetach(false);
                        if (!this.isTablet) {
                            GroupCallMiniTextureView groupCallMiniTextureView6 = this.fullscreenTextureView;
                            if (groupCallMiniTextureView6.primaryView != null || groupCallMiniTextureView6.secondaryView != null || groupCallMiniTextureView6.tabletGridView != null) {
                                groupCallMiniTextureView3 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                                GroupCallMiniTextureView groupCallMiniTextureView7 = this.fullscreenTextureView;
                                groupCallMiniTextureView3.setViews(groupCallMiniTextureView7.primaryView, groupCallMiniTextureView7.secondaryView, groupCallMiniTextureView7.tabletGridView);
                                groupCallMiniTextureView3.setFullscreenMode(this.inFullscreenMode, false);
                                groupCallMiniTextureView3.updateAttachState(false);
                                GroupCallGridCell groupCallGridCell3 = this.fullscreenTextureView.primaryView;
                                if (groupCallGridCell3 != null) {
                                    groupCallGridCell3.setRenderer(groupCallMiniTextureView3);
                                }
                                GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell2 = this.fullscreenTextureView.secondaryView;
                                if (groupCallUserCell2 != null) {
                                    groupCallUserCell2.setRenderer(groupCallMiniTextureView3);
                                }
                                GroupCallGridCell groupCallGridCell4 = this.fullscreenTextureView.tabletGridView;
                                if (groupCallGridCell4 != null) {
                                    groupCallGridCell4.setRenderer(groupCallMiniTextureView3);
                                }
                            }
                        }
                        final GroupCallMiniTextureView groupCallMiniTextureView8 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                        groupCallMiniTextureView8.participant = groupCallMiniTextureView.participant;
                        groupCallMiniTextureView8.setViews(groupCallMiniTextureView.primaryView, groupCallMiniTextureView.secondaryView, groupCallMiniTextureView.tabletGridView);
                        groupCallMiniTextureView8.setFullscreenMode(this.inFullscreenMode, false);
                        groupCallMiniTextureView8.updateAttachState(false);
                        groupCallMiniTextureView8.textureView.renderer.setAlpha(1.0f);
                        groupCallMiniTextureView8.textureView.blurRenderer.setAlpha(1.0f);
                        GroupCallGridCell groupCallGridCell5 = groupCallMiniTextureView.primaryView;
                        if (groupCallGridCell5 != null) {
                            groupCallGridCell5.setRenderer(groupCallMiniTextureView8);
                        }
                        GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell3 = groupCallMiniTextureView.secondaryView;
                        if (groupCallUserCell3 != null) {
                            groupCallUserCell3.setRenderer(groupCallMiniTextureView8);
                        }
                        GroupCallGridCell groupCallGridCell6 = groupCallMiniTextureView.tabletGridView;
                        if (groupCallGridCell6 != null) {
                            groupCallGridCell6.setRenderer(groupCallMiniTextureView8);
                        }
                        groupCallMiniTextureView8.animateEnter = true;
                        groupCallMiniTextureView8.setAlpha(0.0f);
                        this.outFullscreenTextureView = this.fullscreenTextureView;
                        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(groupCallMiniTextureView8, (Property<GroupCallMiniTextureView, Float>) View.ALPHA, 0.0f, 1.0f);
                        this.replaceFullscreenViewAnimator = ofFloat;
                        ofFloat.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                                groupCallRenderersContainer.replaceFullscreenViewAnimator = null;
                                groupCallMiniTextureView8.animateEnter = false;
                                if (groupCallRenderersContainer.outFullscreenTextureView != null) {
                                    if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                        GroupCallRenderersContainer groupCallRenderersContainer2 = GroupCallRenderersContainer.this;
                                        groupCallRenderersContainer2.removeView(groupCallRenderersContainer2.outFullscreenTextureView);
                                        groupCallMiniTextureView.release();
                                    }
                                    GroupCallRenderersContainer.this.outFullscreenTextureView = null;
                                }
                            }
                        });
                        if (groupCallMiniTextureView3 != null) {
                            groupCallMiniTextureView3.setAlpha(0.0f);
                            groupCallMiniTextureView3.setScaleX(0.5f);
                            groupCallMiniTextureView3.setScaleY(0.5f);
                            groupCallMiniTextureView3.animateEnter = true;
                        }
                        groupCallMiniTextureView8.runOnFrameRendered(new Runnable() {
                            @Override
                            public final void run() {
                                GroupCallRenderersContainer.this.lambda$requestFullscreen$3(groupCallMiniTextureView, groupCallMiniTextureView3);
                            }
                        });
                        clearCurrentFullscreenTextureView();
                        this.fullscreenTextureView = groupCallMiniTextureView8;
                        groupCallMiniTextureView8.setShowingInFullscreen(true, false);
                        update();
                    } else {
                        this.inFullscreenMode = true;
                        clearCurrentFullscreenTextureView();
                        this.fullscreenTextureView = groupCallMiniTextureView;
                        groupCallMiniTextureView.setShowingInFullscreen(true, true);
                        invalidate();
                        this.pinDrawable.setCrossOut(this.hasPinnedVideo, false);
                    }
                } else if (this.inFullscreenMode) {
                    GroupCallMiniTextureView groupCallMiniTextureView9 = this.fullscreenTextureView;
                    if (groupCallMiniTextureView9.primaryView == null) {
                        if (!((groupCallMiniTextureView9.secondaryView != null) | (groupCallMiniTextureView9.tabletGridView != null))) {
                            groupCallMiniTextureView9.forceDetach(true);
                            final GroupCallMiniTextureView groupCallMiniTextureView10 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                            groupCallMiniTextureView10.participant = videoParticipant;
                            groupCallMiniTextureView10.setFullscreenMode(this.inFullscreenMode, false);
                            groupCallMiniTextureView10.setShowingInFullscreen(true, false);
                            groupCallMiniTextureView10.animateEnter = true;
                            groupCallMiniTextureView10.setAlpha(0.0f);
                            this.outFullscreenTextureView = this.fullscreenTextureView;
                            ValueAnimator ofFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
                            this.replaceFullscreenViewAnimator = ofFloat2;
                            ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public final void onAnimationUpdate(ValueAnimator valueAnimator4) {
                                    GroupCallRenderersContainer.this.lambda$requestFullscreen$5(groupCallMiniTextureView10, valueAnimator4);
                                }
                            });
                            this.replaceFullscreenViewAnimator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animator) {
                                    GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                                    groupCallRenderersContainer.replaceFullscreenViewAnimator = null;
                                    groupCallMiniTextureView10.animateEnter = false;
                                    if (groupCallRenderersContainer.outFullscreenTextureView != null) {
                                        if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                            GroupCallRenderersContainer groupCallRenderersContainer2 = GroupCallRenderersContainer.this;
                                            groupCallRenderersContainer2.removeView(groupCallRenderersContainer2.outFullscreenTextureView);
                                            GroupCallRenderersContainer.this.outFullscreenTextureView.release();
                                        }
                                        GroupCallRenderersContainer.this.outFullscreenTextureView = null;
                                    }
                                }
                            });
                            this.replaceFullscreenViewAnimator.start();
                            clearCurrentFullscreenTextureView();
                            this.fullscreenTextureView = groupCallMiniTextureView10;
                            groupCallMiniTextureView10.setShowingInFullscreen(true, false);
                            this.fullscreenTextureView.updateAttachState(false);
                            update();
                        }
                    }
                    groupCallMiniTextureView9.forceDetach(false);
                    final GroupCallMiniTextureView groupCallMiniTextureView11 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                    GroupCallMiniTextureView groupCallMiniTextureView12 = this.fullscreenTextureView;
                    groupCallMiniTextureView11.setViews(groupCallMiniTextureView12.primaryView, groupCallMiniTextureView12.secondaryView, groupCallMiniTextureView12.tabletGridView);
                    groupCallMiniTextureView11.setFullscreenMode(this.inFullscreenMode, false);
                    groupCallMiniTextureView11.updateAttachState(false);
                    GroupCallGridCell groupCallGridCell7 = this.fullscreenTextureView.primaryView;
                    if (groupCallGridCell7 != null) {
                        groupCallGridCell7.setRenderer(groupCallMiniTextureView11);
                    }
                    GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell4 = this.fullscreenTextureView.secondaryView;
                    if (groupCallUserCell4 != null) {
                        groupCallUserCell4.setRenderer(groupCallMiniTextureView11);
                    }
                    GroupCallGridCell groupCallGridCell8 = this.fullscreenTextureView.tabletGridView;
                    if (groupCallGridCell8 != null) {
                        groupCallGridCell8.setRenderer(groupCallMiniTextureView11);
                    }
                    groupCallMiniTextureView11.setAlpha(0.0f);
                    groupCallMiniTextureView11.setScaleX(0.5f);
                    groupCallMiniTextureView11.setScaleY(0.5f);
                    groupCallMiniTextureView11.animateEnter = true;
                    groupCallMiniTextureView11.runOnFrameRendered(new Runnable() {
                        @Override
                        public final void run() {
                            GroupCallRenderersContainer.this.lambda$requestFullscreen$4(groupCallMiniTextureView11);
                        }
                    });
                    final GroupCallMiniTextureView groupCallMiniTextureView102 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                    groupCallMiniTextureView102.participant = videoParticipant;
                    groupCallMiniTextureView102.setFullscreenMode(this.inFullscreenMode, false);
                    groupCallMiniTextureView102.setShowingInFullscreen(true, false);
                    groupCallMiniTextureView102.animateEnter = true;
                    groupCallMiniTextureView102.setAlpha(0.0f);
                    this.outFullscreenTextureView = this.fullscreenTextureView;
                    ValueAnimator ofFloat22 = ValueAnimator.ofFloat(0.0f, 1.0f);
                    this.replaceFullscreenViewAnimator = ofFloat22;
                    ofFloat22.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public final void onAnimationUpdate(ValueAnimator valueAnimator4) {
                            GroupCallRenderersContainer.this.lambda$requestFullscreen$5(groupCallMiniTextureView102, valueAnimator4);
                        }
                    });
                    this.replaceFullscreenViewAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                            groupCallRenderersContainer.replaceFullscreenViewAnimator = null;
                            groupCallMiniTextureView102.animateEnter = false;
                            if (groupCallRenderersContainer.outFullscreenTextureView != null) {
                                if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                    GroupCallRenderersContainer groupCallRenderersContainer2 = GroupCallRenderersContainer.this;
                                    groupCallRenderersContainer2.removeView(groupCallRenderersContainer2.outFullscreenTextureView);
                                    GroupCallRenderersContainer.this.outFullscreenTextureView.release();
                                }
                                GroupCallRenderersContainer.this.outFullscreenTextureView = null;
                            }
                        }
                    });
                    this.replaceFullscreenViewAnimator.start();
                    clearCurrentFullscreenTextureView();
                    this.fullscreenTextureView = groupCallMiniTextureView102;
                    groupCallMiniTextureView102.setShowingInFullscreen(true, false);
                    this.fullscreenTextureView.updateAttachState(false);
                    update();
                } else {
                    this.inFullscreenMode = true;
                    clearCurrentFullscreenTextureView();
                    GroupCallMiniTextureView groupCallMiniTextureView13 = new GroupCallMiniTextureView(this, this.attachedRenderers, this.call, this.groupCallActivity);
                    this.fullscreenTextureView = groupCallMiniTextureView13;
                    groupCallMiniTextureView13.participant = videoParticipant;
                    groupCallMiniTextureView13.setFullscreenMode(this.inFullscreenMode, false);
                    this.fullscreenTextureView.setShowingInFullscreen(true, false);
                    this.fullscreenTextureView.setShowingInFullscreen(true, false);
                    ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.fullscreenTextureView, (Property<GroupCallMiniTextureView, Float>) View.ALPHA, 0.0f, 1.0f);
                    this.replaceFullscreenViewAnimator = ofFloat3;
                    ofFloat3.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                            groupCallRenderersContainer.replaceFullscreenViewAnimator = null;
                            groupCallRenderersContainer.fullscreenTextureView.animateEnter = false;
                            if (groupCallRenderersContainer.outFullscreenTextureView != null) {
                                if (GroupCallRenderersContainer.this.outFullscreenTextureView.getParent() != null) {
                                    GroupCallRenderersContainer groupCallRenderersContainer2 = GroupCallRenderersContainer.this;
                                    groupCallRenderersContainer2.removeView(groupCallRenderersContainer2.outFullscreenTextureView);
                                    GroupCallRenderersContainer.this.outFullscreenTextureView.release();
                                }
                                GroupCallRenderersContainer.this.outFullscreenTextureView = null;
                            }
                        }
                    });
                    this.replaceFullscreenViewAnimator.start();
                    invalidate();
                    this.pinDrawable.setCrossOut(this.hasPinnedVideo, false);
                }
                this.backButton.setEnabled(true);
            }
            boolean z2 = this.inFullscreenMode;
            if (z != z2) {
                if (z2) {
                    this.backButton.setVisibility(0);
                    this.pinButton.setVisibility(0);
                    this.unpinTextView.setVisibility(0);
                    this.pinContainer.setVisibility(0);
                } else {
                    setUiVisible(true);
                    if (this.hideUiRunnableIsScheduled) {
                        this.hideUiRunnableIsScheduled = false;
                        AndroidUtilities.cancelRunOnUIThread(this.hideUiRunnable);
                    }
                }
                onFullScreenModeChanged(true);
                ValueAnimator ofFloat4 = ValueAnimator.ofFloat(this.progressToFullscreenMode, this.inFullscreenMode ? 1.0f : 0.0f);
                this.fullscreenAnimator = ofFloat4;
                ofFloat4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator4) {
                        GroupCallRenderersContainer.this.lambda$requestFullscreen$6(valueAnimator4);
                    }
                });
                final GroupCallMiniTextureView groupCallMiniTextureView14 = this.fullscreenTextureView;
                groupCallMiniTextureView14.animateToFullscreen = true;
                this.groupCallActivity.getCurrentAccount();
                this.swipedBack = this.swipeToBackGesture;
                this.notificationsLocker.lock();
                this.fullscreenAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        GroupCallRenderersContainer.this.notificationsLocker.unlock();
                        GroupCallRenderersContainer groupCallRenderersContainer = GroupCallRenderersContainer.this;
                        groupCallRenderersContainer.fullscreenAnimator = null;
                        groupCallMiniTextureView14.animateToFullscreen = false;
                        if (!groupCallRenderersContainer.inFullscreenMode) {
                            groupCallRenderersContainer.clearCurrentFullscreenTextureView();
                            GroupCallRenderersContainer groupCallRenderersContainer2 = GroupCallRenderersContainer.this;
                            groupCallRenderersContainer2.fullscreenTextureView = null;
                            groupCallRenderersContainer2.fullscreenPeerId = 0L;
                        }
                        GroupCallRenderersContainer groupCallRenderersContainer3 = GroupCallRenderersContainer.this;
                        groupCallRenderersContainer3.progressToFullscreenMode = groupCallRenderersContainer3.inFullscreenMode ? 1.0f : 0.0f;
                        groupCallRenderersContainer3.update();
                        GroupCallRenderersContainer.this.onFullScreenModeChanged(false);
                        GroupCallRenderersContainer groupCallRenderersContainer4 = GroupCallRenderersContainer.this;
                        if (groupCallRenderersContainer4.inFullscreenMode) {
                            return;
                        }
                        groupCallRenderersContainer4.backButton.setVisibility(8);
                        GroupCallRenderersContainer.this.pinButton.setVisibility(8);
                        GroupCallRenderersContainer.this.unpinTextView.setVisibility(8);
                        GroupCallRenderersContainer.this.pinContainer.setVisibility(8);
                    }
                });
                this.fullscreenAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.fullscreenAnimator.setDuration(350L);
                this.fullscreenTextureView.textureView.synchOrRunAnimation(this.fullscreenAnimator);
            }
            animateSwipeToBack(this.fullscreenParticipant == null);
        }
    }

    public void setAmplitude(TLRPC.TL_groupCallParticipant tL_groupCallParticipant, float f) {
        for (int i = 0; i < this.attachedRenderers.size(); i++) {
            if (MessageObject.getPeerId(((GroupCallMiniTextureView) this.attachedRenderers.get(i)).participant.participant.peer) == MessageObject.getPeerId(tL_groupCallParticipant.peer)) {
                ((GroupCallMiniTextureView) this.attachedRenderers.get(i)).setAmplitude(f);
            }
        }
    }

    public void setGroupCall(ChatObject.Call call) {
        this.call = call;
    }

    public void setIsTablet(boolean z) {
        if (this.isTablet != z) {
            this.isTablet = z;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.backButton.getLayoutParams();
            layoutParams.gravity = z ? 85 : 51;
            layoutParams.rightMargin = z ? AndroidUtilities.dp(328.0f) : 0;
            layoutParams.bottomMargin = z ? -AndroidUtilities.dp(8.0f) : 0;
            if (this.isTablet) {
                this.backButton.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.msg_calls_minimize));
                return;
            }
            BackDrawable backDrawable = new BackDrawable(false);
            backDrawable.setColor(-1);
            this.backButton.setImageDrawable(backDrawable);
        }
    }

    public void setProgressToHideUi(float f) {
        if (this.progressToHideUi != f) {
            this.progressToHideUi = f;
            invalidate();
            GroupCallMiniTextureView groupCallMiniTextureView = this.fullscreenTextureView;
            if (groupCallMiniTextureView != null) {
                groupCallMiniTextureView.invalidate();
            }
        }
    }

    public void setVisibleParticipant(boolean z) {
        boolean z2;
        int i;
        String str;
        TypefaceSpan typefaceSpan;
        int i2 = 0;
        if (!this.inFullscreenMode || this.isTablet || this.fullscreenParticipant == null || this.fullscreenAnimator != null || this.call == null) {
            if (this.showSpeakingMembersToast) {
                this.showSpeakingMembersToast = false;
                this.showSpeakingMembersToastProgress = 0.0f;
                return;
            }
            return;
        }
        int currentAccount = this.groupCallActivity.getCurrentAccount();
        if (System.currentTimeMillis() - this.lastUpdateTooltipTime < 500) {
            if (this.updateTooltipRunnbale == null) {
                Runnable runnable = new Runnable() {
                    @Override
                    public final void run() {
                        GroupCallRenderersContainer.this.lambda$setVisibleParticipant$9();
                    }
                };
                this.updateTooltipRunnbale = runnable;
                AndroidUtilities.runOnUIThread(runnable, (System.currentTimeMillis() - this.lastUpdateTooltipTime) + 50);
                return;
            }
            return;
        }
        this.lastUpdateTooltipTime = System.currentTimeMillis();
        int i3 = 0;
        SpannableStringBuilder spannableStringBuilder = null;
        int i4 = 0;
        while (i3 < this.call.currentSpeakingPeers.size()) {
            TLRPC.TL_groupCallParticipant tL_groupCallParticipant = (TLRPC.TL_groupCallParticipant) this.call.currentSpeakingPeers.get(this.call.currentSpeakingPeers.keyAt(i3));
            if (tL_groupCallParticipant.self || tL_groupCallParticipant.muted_by_you || MessageObject.getPeerId(this.fullscreenParticipant.participant.peer) == MessageObject.getPeerId(tL_groupCallParticipant.peer)) {
                i = i3;
            } else {
                long peerId = MessageObject.getPeerId(tL_groupCallParticipant.peer);
                i = i3;
                if (SystemClock.uptimeMillis() - tL_groupCallParticipant.lastSpeakTime >= 500) {
                    continue;
                } else {
                    if (spannableStringBuilder == null) {
                        spannableStringBuilder = new SpannableStringBuilder();
                    }
                    if (i4 == 0) {
                        this.speakingToastPeerId = MessageObject.getPeerId(tL_groupCallParticipant.peer);
                    }
                    if (i4 < 3) {
                        TLRPC.User user = peerId > 0 ? MessagesController.getInstance(currentAccount).getUser(Long.valueOf(peerId)) : null;
                        TLRPC.Chat chat = peerId <= 0 ? MessagesController.getInstance(currentAccount).getChat(Long.valueOf(peerId)) : null;
                        if (user != null || chat != null) {
                            this.speakingMembersAvatars.setObject(i4, currentAccount, tL_groupCallParticipant);
                            if (i4 != 0) {
                                spannableStringBuilder.append((CharSequence) ", ");
                            }
                            if (user == null) {
                                int i5 = Build.VERSION.SDK_INT;
                                str = chat.title;
                                if (i5 >= 21) {
                                    typefaceSpan = new TypefaceSpan(AndroidUtilities.bold());
                                    spannableStringBuilder.append(str, typefaceSpan, 0);
                                }
                                spannableStringBuilder.append((CharSequence) str);
                            } else if (Build.VERSION.SDK_INT >= 21) {
                                str = UserObject.getFirstName(user);
                                typefaceSpan = new TypefaceSpan(AndroidUtilities.bold());
                                spannableStringBuilder.append(str, typefaceSpan, 0);
                            } else {
                                str = UserObject.getFirstName(user);
                                spannableStringBuilder.append((CharSequence) str);
                            }
                        }
                    }
                    i4++;
                    if (i4 == 3) {
                        break;
                    }
                }
            }
            i3 = i + 1;
        }
        boolean z3 = i4 != 0;
        boolean z4 = this.showSpeakingMembersToast;
        if (!z4 && z3) {
            z2 = false;
        } else {
            if (!z3 && z4) {
                this.showSpeakingMembersToast = z3;
                invalidate();
                return;
            }
            if (z4 && z3) {
                this.speakingMembersToastFromLeft = this.speakingMembersToast.getLeft();
                this.speakingMembersToastFromRight = this.speakingMembersToast.getRight();
                this.speakingMembersToastFromTextLeft = this.speakingMembersText.getLeft();
                this.speakingMembersToastChangeProgress = 0.0f;
            }
            z2 = z;
        }
        if (!z3) {
            this.showSpeakingMembersToast = z3;
            invalidate();
            return;
        }
        String pluralString = LocaleController.getPluralString("MembersAreSpeakingToast", i4);
        int indexOf = pluralString.indexOf("un1");
        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(pluralString);
        spannableStringBuilder2.replace(indexOf, indexOf + 3, (CharSequence) spannableStringBuilder);
        this.speakingMembersText.setText(spannableStringBuilder2);
        if (i4 != 0) {
            i2 = AndroidUtilities.dp(i4 == 1 ? 40.0f : i4 == 2 ? 64.0f : 88.0f);
        }
        ((FrameLayout.LayoutParams) this.speakingMembersText.getLayoutParams()).leftMargin = i2;
        ((FrameLayout.LayoutParams) this.speakingMembersText.getLayoutParams()).rightMargin = AndroidUtilities.dp(16.0f);
        this.showSpeakingMembersToast = z3;
        invalidate();
        while (i4 < 3) {
            this.speakingMembersAvatars.setObject(i4, currentAccount, null);
            i4++;
        }
        this.speakingMembersAvatars.commitTransition(z2);
    }

    public void update() {
        invalidate();
    }
}
