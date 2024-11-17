package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BlobDrawable;
import org.telegram.ui.Components.CrossOutDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.GroupCallFullscreenAdapter;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.voip.GroupCallStatusIcon;
import org.telegram.ui.GroupCallActivity;
import org.webrtc.GlGenericDrawer;
import org.webrtc.RendererCommon;
import org.webrtc.TextureViewRenderer;

public class GroupCallMiniTextureView extends FrameLayout implements GroupCallStatusIcon.Callback {
    GroupCallActivity activity;
    boolean animateEnter;
    int animateToColor;
    public boolean animateToFullscreen;
    public boolean animateToScrimView;
    boolean attached;
    ArrayList attachedRenderers;
    ImageView blurredFlippingStub;
    ChatObject.Call call;
    private Drawable castingScreenDrawable;
    private boolean checkScale;
    int collapseSize;
    ValueAnimator colorAnimator;
    int currentAccount;
    public boolean drawFirst;
    ValueAnimator flipAnimator;
    boolean flipHalfReached;
    public boolean forceDetached;
    int fullSize;
    Paint gradientPaint;
    LinearGradient gradientShader;
    int gridItemsCount;
    public boolean hasVideo;
    ImageReceiver imageReceiver;
    boolean inPinchToZoom;
    FrameLayout infoContainer;
    private boolean invalidateFromChild;
    boolean isFullscreenMode;
    int lastIconColor;
    private boolean lastLandscapeMode;
    private int lastSize;
    int lastSpeakingFrameColor;
    private final RLottieImageView micIconView;
    private final SimpleTextView nameView;
    private Runnable noRtmpStreamCallback;
    private TextView noRtmpStreamTextView;
    ValueAnimator noVideoStubAnimator;
    private NoVideoStubLayout noVideoStubLayout;
    ArrayList onFirstFrameRunnables;
    float overlayIconAlpha;
    GroupCallRenderersContainer parentContainer;
    public ChatObject.VideoParticipant participant;
    private CrossOutDrawable pausedVideoDrawable;
    float pinchCenterX;
    float pinchCenterY;
    float pinchScale;
    float pinchTranslationX;
    float pinchTranslationY;
    private boolean postedNoRtmpStreamCallback;
    public GroupCallGridCell primaryView;
    private float progressToBackground;
    public float progressToNoVideoStub;
    float progressToSpeaking;
    private Rect rect;
    private final ImageView screencastIcon;
    public GroupCallFullscreenAdapter.GroupCallUserCell secondaryView;
    private boolean showingAsScrimView;
    public boolean showingInFullscreen;
    float spanCount;
    Paint speakingPaint;
    private GroupCallStatusIcon statusIcon;
    private TextView stopSharingTextView;
    private boolean swipeToBack;
    private float swipeToBackDy;
    public GroupCallGridCell tabletGridView;
    public VoIPTextureView textureView;
    Bitmap thumb;
    Paint thumbPaint;
    private boolean updateNextLayoutAnimated;
    boolean useSpanSize;
    private boolean videoIsPaused;
    private float videoIsPausedProgress;

    public class NoVideoStubLayout extends View {
        float amplitude;
        float animateAmplitudeDiff;
        float animateToAmplitude;
        AvatarDrawable avatarDrawable;
        public ImageReceiver avatarImageReceiver;
        public ImageReceiver backgroundImageReceiver;
        Paint backgroundPaint;
        BlobDrawable bigWaveDrawable;
        private GroupCallActivity.WeavingState currentState;
        float cx;
        float cy;
        int muteButtonState;
        Paint paint;
        private GroupCallActivity.WeavingState prevState;
        float speakingProgress;
        private GroupCallActivity.WeavingState[] states;
        float switchProgress;
        BlobDrawable tinyWaveDrawable;
        float wavesEnter;

        public NoVideoStubLayout(Context context) {
            super(context);
            this.avatarImageReceiver = new ImageReceiver();
            this.backgroundImageReceiver = new ImageReceiver();
            this.avatarDrawable = new AvatarDrawable();
            this.paint = new Paint(1);
            this.backgroundPaint = new Paint(1);
            this.wavesEnter = 0.0f;
            this.states = new GroupCallActivity.WeavingState[3];
            this.muteButtonState = -1;
            this.switchProgress = 1.0f;
            this.tinyWaveDrawable = new BlobDrawable(9);
            this.bigWaveDrawable = new BlobDrawable(12);
            this.tinyWaveDrawable.minRadius = AndroidUtilities.dp(76.0f);
            this.tinyWaveDrawable.maxRadius = AndroidUtilities.dp(92.0f);
            this.tinyWaveDrawable.generateBlob();
            this.bigWaveDrawable.minRadius = AndroidUtilities.dp(80.0f);
            this.bigWaveDrawable.maxRadius = AndroidUtilities.dp(95.0f);
            this.bigWaveDrawable.generateBlob();
            this.paint.setColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_voipgroup_listeningText), Theme.getColor(Theme.key_voipgroup_speakingText), this.speakingProgress));
            this.paint.setAlpha(102);
            this.backgroundPaint.setColor(ColorUtils.setAlphaComponent(-16777216, 127));
        }

        public void updateMuteButtonState(boolean z) {
            GroupCallActivity.WeavingState weavingState;
            RadialGradient radialGradient;
            int i = (GroupCallMiniTextureView.this.statusIcon.isMutedByMe() || GroupCallMiniTextureView.this.statusIcon.isMutedByAdmin()) ? 2 : GroupCallMiniTextureView.this.statusIcon.isSpeaking() ? 1 : 0;
            if (i == this.muteButtonState) {
                return;
            }
            this.muteButtonState = i;
            GroupCallActivity.WeavingState[] weavingStateArr = this.states;
            if (weavingStateArr[i] == null) {
                weavingStateArr[i] = new GroupCallActivity.WeavingState(i);
                int i2 = this.muteButtonState;
                if (i2 == 2) {
                    this.states[i2].shader = new LinearGradient(0.0f, 400.0f, 400.0f, 0.0f, new int[]{Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient), Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient3), Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient2)}, (float[]) null, Shader.TileMode.CLAMP);
                } else {
                    GroupCallActivity.WeavingState[] weavingStateArr2 = this.states;
                    if (i2 == 1) {
                        weavingState = weavingStateArr2[i2];
                        radialGradient = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor(Theme.key_voipgroup_muteButton), Theme.getColor(Theme.key_voipgroup_muteButton3)}, (float[]) null, Shader.TileMode.CLAMP);
                    } else {
                        weavingState = weavingStateArr2[i2];
                        radialGradient = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor(Theme.key_voipgroup_unmuteButton2), Theme.getColor(Theme.key_voipgroup_unmuteButton)}, (float[]) null, Shader.TileMode.CLAMP);
                    }
                    weavingState.shader = radialGradient;
                }
            }
            GroupCallActivity.WeavingState weavingState2 = this.states[this.muteButtonState];
            GroupCallActivity.WeavingState weavingState3 = this.currentState;
            if (weavingState2 != weavingState3) {
                this.prevState = weavingState3;
                this.currentState = weavingState2;
                if (weavingState3 == null || !z) {
                    this.switchProgress = 1.0f;
                    this.prevState = null;
                } else {
                    this.switchProgress = 0.0f;
                }
            }
            invalidate();
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.avatarImageReceiver.onAttachedToWindow();
            this.backgroundImageReceiver.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.avatarImageReceiver.onDetachedFromWindow();
            this.backgroundImageReceiver.onDetachedFromWindow();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            GroupCallActivity.WeavingState weavingState;
            float f;
            GroupCallActivity.WeavingState weavingState2;
            super.onDraw(canvas);
            RectF rectF = AndroidUtilities.rectTmp;
            float x = GroupCallMiniTextureView.this.textureView.getX();
            VoIPTextureView voIPTextureView = GroupCallMiniTextureView.this.textureView;
            float f2 = x + voIPTextureView.currentClipHorizontal;
            float y = voIPTextureView.getY();
            VoIPTextureView voIPTextureView2 = GroupCallMiniTextureView.this.textureView;
            float f3 = y + voIPTextureView2.currentClipVertical;
            float x2 = voIPTextureView2.getX() + GroupCallMiniTextureView.this.textureView.getMeasuredWidth();
            VoIPTextureView voIPTextureView3 = GroupCallMiniTextureView.this.textureView;
            rectF.set(f2, f3, x2 - voIPTextureView3.currentClipHorizontal, voIPTextureView3.getY() + GroupCallMiniTextureView.this.textureView.getMeasuredHeight() + GroupCallMiniTextureView.this.textureView.currentClipVertical);
            this.backgroundImageReceiver.setImageCoords(rectF.left, rectF.top, rectF.width(), rectF.height());
            this.backgroundImageReceiver.setRoundRadius((int) GroupCallMiniTextureView.this.textureView.roundRadius);
            this.backgroundImageReceiver.draw(canvas);
            float f4 = GroupCallMiniTextureView.this.textureView.roundRadius;
            canvas.drawRoundRect(rectF, f4, f4, this.backgroundPaint);
            float f5 = this.animateToAmplitude;
            float f6 = this.amplitude;
            if (f5 != f6) {
                float f7 = this.animateAmplitudeDiff;
                float f8 = f6 + (16.0f * f7);
                this.amplitude = f8;
                if (f7 <= 0.0f ? f8 < f5 : f8 > f5) {
                    this.amplitude = f5;
                }
            }
            float f9 = this.switchProgress;
            if (f9 != 1.0f) {
                if (this.prevState != null) {
                    this.switchProgress = f9 + 0.07272727f;
                }
                if (this.switchProgress >= 1.0f) {
                    this.switchProgress = 1.0f;
                    this.prevState = null;
                }
            }
            float f10 = (this.amplitude * 0.8f) + 1.0f;
            canvas.save();
            canvas.scale(f10, f10, this.cx, this.cy);
            GroupCallActivity.WeavingState weavingState3 = this.currentState;
            if (weavingState3 != null) {
                weavingState3.update((int) (this.cy - AndroidUtilities.dp(100.0f)), (int) (this.cx - AndroidUtilities.dp(100.0f)), AndroidUtilities.dp(200.0f), 16L, this.amplitude);
            }
            this.bigWaveDrawable.update(this.amplitude, 1.0f);
            this.tinyWaveDrawable.update(this.amplitude, 1.0f);
            for (int i = 0; i < 2; i++) {
                if (i != 0 || (weavingState2 = this.prevState) == null) {
                    if (i == 1 && (weavingState = this.currentState) != null) {
                        this.paint.setShader(weavingState.shader);
                        f = this.switchProgress;
                    }
                } else {
                    this.paint.setShader(weavingState2.shader);
                    f = 1.0f - this.switchProgress;
                }
                this.paint.setAlpha((int) (f * 76.0f));
                this.bigWaveDrawable.draw(this.cx, this.cy, canvas, this.paint);
                this.tinyWaveDrawable.draw(this.cx, this.cy, canvas, this.paint);
            }
            canvas.restore();
            float f11 = (this.amplitude * 0.2f) + 1.0f;
            canvas.save();
            canvas.scale(f11, f11, this.cx, this.cy);
            this.avatarImageReceiver.draw(canvas);
            canvas.restore();
            invalidate();
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            float dp = AndroidUtilities.dp(157.0f);
            this.cx = getMeasuredWidth() >> 1;
            this.cy = (getMeasuredHeight() >> 1) + (GroupCallActivity.isLandscapeMode ? 0.0f : (-getMeasuredHeight()) * 0.12f);
            float f = dp / 2.0f;
            this.avatarImageReceiver.setRoundRadius((int) f);
            this.avatarImageReceiver.setImageCoords(this.cx - f, this.cy - f, dp, dp);
        }

        public void setAmplitude(double d) {
            float f = ((float) d) / 80.0f;
            if (f > 1.0f) {
                f = 1.0f;
            } else if (f < 0.0f) {
                f = 0.0f;
            }
            this.animateToAmplitude = f;
            this.animateAmplitudeDiff = (f - this.amplitude) / 200.0f;
        }
    }

    public GroupCallMiniTextureView(final GroupCallRenderersContainer groupCallRenderersContainer, ArrayList arrayList, final ChatObject.Call call, final GroupCallActivity groupCallActivity) {
        super(groupCallRenderersContainer.getContext());
        TextView textView;
        String formatString;
        this.gradientPaint = new Paint(1);
        this.speakingPaint = new Paint(1);
        this.progressToNoVideoStub = 1.0f;
        this.imageReceiver = new ImageReceiver();
        this.onFirstFrameRunnables = new ArrayList();
        this.noRtmpStreamCallback = new Runnable() {
            @Override
            public final void run() {
                GroupCallMiniTextureView.this.lambda$new$0();
            }
        };
        this.rect = new Rect();
        this.call = call;
        this.currentAccount = groupCallActivity.getCurrentAccount();
        CrossOutDrawable crossOutDrawable = new CrossOutDrawable(groupCallRenderersContainer.getContext(), R.drawable.calls_video, -1);
        this.pausedVideoDrawable = crossOutDrawable;
        crossOutDrawable.setCrossOut(true, false);
        this.pausedVideoDrawable.setOffsets(-AndroidUtilities.dp(4.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f));
        this.pausedVideoDrawable.setStrokeWidth(AndroidUtilities.dpf2(3.4f));
        this.castingScreenDrawable = groupCallRenderersContainer.getContext().getResources().getDrawable(R.drawable.screencast_big).mutate();
        final TextPaint textPaint = new TextPaint(1);
        textPaint.setTypeface(AndroidUtilities.bold());
        textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        textPaint.setColor(-1);
        final TextPaint textPaint2 = new TextPaint(1);
        textPaint2.setTypeface(AndroidUtilities.bold());
        textPaint2.setTextSize(AndroidUtilities.dp(15.0f));
        textPaint2.setColor(-1);
        final String string = LocaleController.getString(R.string.VoipVideoOnPause);
        String string2 = LocaleController.getString(R.string.VoipVideoScreenSharingTwoLines);
        int dp = AndroidUtilities.dp(400.0f);
        Layout.Alignment alignment = Layout.Alignment.ALIGN_CENTER;
        final StaticLayout staticLayout = new StaticLayout(string2, textPaint, dp, alignment, 1.0f, 0.0f, false);
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(call.chatId));
        final StaticLayout staticLayout2 = new StaticLayout(LocaleController.formatString("VoipVideoNotAvailable", R.string.VoipVideoNotAvailable, LocaleController.formatPluralString("Participants", MessagesController.getInstance(this.currentAccount).groupCallVideoMaxParticipants, new Object[0])), textPaint, AndroidUtilities.dp(400.0f), alignment, 1.0f, 0.0f, false);
        final String string3 = LocaleController.getString(R.string.VoipVideoScreenSharing);
        final float measureText = textPaint.measureText(string);
        final float measureText2 = textPaint2.measureText(string3);
        boolean z = true;
        boolean z2 = false;
        VoIPTextureView voIPTextureView = new VoIPTextureView(groupCallRenderersContainer.getContext(), z2, z2, z, z) {
            float overlayIconAlphaFrom;

            @Override
            public void animateToLayout() {
                super.animateToLayout();
                this.overlayIconAlphaFrom = GroupCallMiniTextureView.this.overlayIconAlpha;
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                float f;
                float dp2;
                float f2;
                float f3;
                if (!this.renderer.isFirstFrameRendered() || ((this.renderer.getAlpha() != 1.0f && this.blurRenderer.getAlpha() != 1.0f) || GroupCallMiniTextureView.this.videoIsPaused)) {
                    if (GroupCallMiniTextureView.this.progressToBackground != 1.0f) {
                        GroupCallMiniTextureView.access$116(GroupCallMiniTextureView.this, 0.10666667f);
                        if (GroupCallMiniTextureView.this.progressToBackground > 1.0f) {
                            GroupCallMiniTextureView.this.progressToBackground = 1.0f;
                        } else {
                            invalidate();
                        }
                    }
                    GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                    if (groupCallMiniTextureView.thumb != null) {
                        canvas.save();
                        float f4 = this.currentThumbScale;
                        canvas.scale(f4, f4, getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f);
                        GroupCallMiniTextureView groupCallMiniTextureView2 = GroupCallMiniTextureView.this;
                        if (groupCallMiniTextureView2.thumbPaint == null) {
                            groupCallMiniTextureView2.thumbPaint = new Paint(1);
                            GroupCallMiniTextureView.this.thumbPaint.setFilterBitmap(true);
                        }
                        canvas.drawBitmap(GroupCallMiniTextureView.this.thumb, (getMeasuredWidth() - GroupCallMiniTextureView.this.thumb.getWidth()) / 2.0f, (getMeasuredHeight() - GroupCallMiniTextureView.this.thumb.getHeight()) / 2.0f, GroupCallMiniTextureView.this.thumbPaint);
                        canvas.restore();
                    } else {
                        groupCallMiniTextureView.imageReceiver.setImageCoords(this.currentClipHorizontal, this.currentClipVertical, getMeasuredWidth() - (this.currentClipHorizontal * 2.0f), getMeasuredHeight() - (this.currentClipVertical * 2.0f));
                        GroupCallMiniTextureView groupCallMiniTextureView3 = GroupCallMiniTextureView.this;
                        groupCallMiniTextureView3.imageReceiver.setAlpha(groupCallMiniTextureView3.progressToBackground);
                        GroupCallMiniTextureView.this.imageReceiver.draw(canvas);
                    }
                    GroupCallMiniTextureView groupCallMiniTextureView4 = GroupCallMiniTextureView.this;
                    ChatObject.VideoParticipant videoParticipant = groupCallMiniTextureView4.participant;
                    if (videoParticipant == call.videoNotAvailableParticipant) {
                        if (groupCallMiniTextureView4.showingInFullscreen || !groupCallRenderersContainer.inFullscreenMode) {
                            float dp3 = AndroidUtilities.dp(48.0f);
                            textPaint.setAlpha(255);
                            canvas.save();
                            canvas.translate((((getMeasuredWidth() - dp3) / 2.0f) - (AndroidUtilities.dp(400.0f) / 2.0f)) + (dp3 / 2.0f), ((getMeasuredHeight() / 2) - dp3) + dp3 + AndroidUtilities.dp(10.0f));
                            staticLayout2.draw(canvas);
                            canvas.restore();
                        }
                        if (GroupCallMiniTextureView.this.stopSharingTextView.getVisibility() != 4) {
                            GroupCallMiniTextureView.this.stopSharingTextView.setVisibility(4);
                        }
                    } else if (videoParticipant.presentation && videoParticipant.participant.self) {
                        if (groupCallMiniTextureView4.stopSharingTextView.getVisibility() != 0) {
                            GroupCallMiniTextureView.this.stopSharingTextView.setVisibility(0);
                            GroupCallMiniTextureView.this.stopSharingTextView.setScaleX(1.0f);
                            GroupCallMiniTextureView.this.stopSharingTextView.setScaleY(1.0f);
                        }
                        float f5 = GroupCallMiniTextureView.this.drawFirst ? 0.0f : groupCallRenderersContainer.progressToFullscreenMode;
                        int dp4 = AndroidUtilities.dp(33.0f);
                        GroupCallMiniTextureView groupCallMiniTextureView5 = GroupCallMiniTextureView.this;
                        if (groupCallMiniTextureView5.animateToFullscreen || groupCallMiniTextureView5.showingInFullscreen) {
                            f = dp4;
                            dp2 = AndroidUtilities.dp(10.0f) + (AndroidUtilities.dp(39.0f) * groupCallRenderersContainer.progressToFullscreenMode);
                        } else {
                            f = dp4;
                            dp2 = AndroidUtilities.dp(10.0f) * Math.max(1.0f - groupCallRenderersContainer.progressToFullscreenMode, (GroupCallMiniTextureView.this.showingAsScrimView || GroupCallMiniTextureView.this.animateToScrimView) ? groupCallRenderersContainer.progressToScrimView : 0.0f);
                        }
                        int i = (int) (f + dp2);
                        int measuredWidth = (getMeasuredWidth() - i) / 2;
                        float f6 = (GroupCallMiniTextureView.this.showingAsScrimView || GroupCallMiniTextureView.this.animateToScrimView) ? groupCallRenderersContainer.progressToScrimView : 0.0f;
                        GroupCallMiniTextureView groupCallMiniTextureView6 = GroupCallMiniTextureView.this;
                        if (groupCallMiniTextureView6.showingInFullscreen) {
                            f2 = f5;
                        } else {
                            f5 = groupCallMiniTextureView6.animateToFullscreen ? groupCallRenderersContainer.progressToFullscreenMode : f6;
                            f2 = (groupCallMiniTextureView6.showingAsScrimView || GroupCallMiniTextureView.this.animateToScrimView) ? groupCallRenderersContainer.progressToScrimView : groupCallRenderersContainer.progressToFullscreenMode;
                        }
                        float measuredHeight = ((getMeasuredHeight() - i) / 2) - AndroidUtilities.dp(28.0f);
                        float dp5 = AndroidUtilities.dp(17.0f);
                        float dp6 = AndroidUtilities.dp(74.0f);
                        GroupCallMiniTextureView groupCallMiniTextureView7 = GroupCallMiniTextureView.this;
                        int dp7 = (int) ((measuredHeight - ((dp5 + (dp6 * ((groupCallMiniTextureView7.showingInFullscreen || groupCallMiniTextureView7.animateToFullscreen) ? groupCallRenderersContainer.progressToFullscreenMode : 0.0f))) * f5)) + (AndroidUtilities.dp(17.0f) * f2));
                        GroupCallMiniTextureView.this.castingScreenDrawable.setBounds(measuredWidth, dp7, measuredWidth + i, dp7 + i);
                        GroupCallMiniTextureView.this.castingScreenDrawable.draw(canvas);
                        float f7 = groupCallRenderersContainer.progressToFullscreenMode;
                        if (f7 > 0.0f || f6 > 0.0f) {
                            float max = Math.max(f7, f6) * f5;
                            textPaint2.setAlpha((int) (max * 255.0f));
                            GroupCallMiniTextureView groupCallMiniTextureView8 = GroupCallMiniTextureView.this;
                            if (groupCallMiniTextureView8.animateToFullscreen || groupCallMiniTextureView8.showingInFullscreen) {
                                groupCallMiniTextureView8.stopSharingTextView.setAlpha(max * (1.0f - f6));
                            } else {
                                groupCallMiniTextureView8.stopSharingTextView.setAlpha(0.0f);
                            }
                            canvas.drawText(string3, (measuredWidth - (measureText2 / 2.0f)) + (i / 2.0f), AndroidUtilities.dp(32.0f) + r12, textPaint2);
                        } else {
                            GroupCallMiniTextureView.this.stopSharingTextView.setAlpha(0.0f);
                        }
                        GroupCallMiniTextureView.this.stopSharingTextView.setTranslationY(((AndroidUtilities.dp(72.0f) + r12) + GroupCallMiniTextureView.this.swipeToBackDy) - this.currentClipVertical);
                        GroupCallMiniTextureView.this.stopSharingTextView.setTranslationX(((getMeasuredWidth() - GroupCallMiniTextureView.this.stopSharingTextView.getMeasuredWidth()) / 2.0f) - this.currentClipHorizontal);
                        float f8 = groupCallRenderersContainer.progressToFullscreenMode;
                        if (f8 < 1.0f && f6 < 1.0f) {
                            TextPaint textPaint3 = textPaint;
                            double max2 = Math.max(f8, f6);
                            Double.isNaN(max2);
                            textPaint3.setAlpha((int) ((1.0d - max2) * 255.0d));
                            canvas.save();
                            canvas.translate((measuredWidth - (AndroidUtilities.dp(400.0f) / 2.0f)) + (i / 2.0f), r12 + AndroidUtilities.dp(10.0f));
                            staticLayout.draw(canvas);
                            canvas.restore();
                        }
                    } else {
                        if (groupCallMiniTextureView4.stopSharingTextView.getVisibility() != 4) {
                            GroupCallMiniTextureView.this.stopSharingTextView.setVisibility(4);
                        }
                        groupCallActivity.cellFlickerDrawable.draw(canvas, GroupCallMiniTextureView.this);
                    }
                    invalidate();
                }
                GroupCallMiniTextureView.this.noRtmpStreamTextView.setTranslationY((((getMeasuredHeight() - GroupCallMiniTextureView.this.noRtmpStreamTextView.getMeasuredHeight()) / 2.0f) + GroupCallMiniTextureView.this.swipeToBackDy) - this.currentClipVertical);
                GroupCallMiniTextureView.this.noRtmpStreamTextView.setTranslationX(((getMeasuredWidth() - GroupCallMiniTextureView.this.noRtmpStreamTextView.getMeasuredWidth()) / 2.0f) - this.currentClipHorizontal);
                ImageView imageView = GroupCallMiniTextureView.this.blurredFlippingStub;
                if (imageView != null && imageView.getParent() != null) {
                    GroupCallMiniTextureView groupCallMiniTextureView9 = GroupCallMiniTextureView.this;
                    groupCallMiniTextureView9.blurredFlippingStub.setScaleX(groupCallMiniTextureView9.textureView.renderer.getScaleX());
                    GroupCallMiniTextureView groupCallMiniTextureView10 = GroupCallMiniTextureView.this;
                    groupCallMiniTextureView10.blurredFlippingStub.setScaleY(groupCallMiniTextureView10.textureView.renderer.getScaleY());
                }
                super.dispatchDraw(canvas);
                float measuredHeight2 = (getMeasuredHeight() - this.currentClipVertical) - AndroidUtilities.dp(80.0f);
                if (GroupCallMiniTextureView.this.participant != call.videoNotAvailableParticipant) {
                    canvas.save();
                    GroupCallMiniTextureView groupCallMiniTextureView11 = GroupCallMiniTextureView.this;
                    if ((groupCallMiniTextureView11.showingInFullscreen || groupCallMiniTextureView11.animateToFullscreen) && !GroupCallActivity.isLandscapeMode && !GroupCallActivity.isTabletMode) {
                        float dp8 = AndroidUtilities.dp(90.0f);
                        GroupCallRenderersContainer groupCallRenderersContainer2 = groupCallRenderersContainer;
                        measuredHeight2 -= (dp8 * groupCallRenderersContainer2.progressToFullscreenMode) * (1.0f - groupCallRenderersContainer2.progressToHideUi);
                    }
                    canvas.translate(0.0f, measuredHeight2);
                    canvas.drawPaint(GroupCallMiniTextureView.this.gradientPaint);
                    canvas.restore();
                }
                if (GroupCallMiniTextureView.this.videoIsPaused || GroupCallMiniTextureView.this.videoIsPausedProgress != 0.0f) {
                    if (GroupCallMiniTextureView.this.videoIsPaused && GroupCallMiniTextureView.this.videoIsPausedProgress != 1.0f) {
                        GroupCallMiniTextureView.access$716(GroupCallMiniTextureView.this, 0.064f);
                        if (GroupCallMiniTextureView.this.videoIsPausedProgress > 1.0f) {
                            GroupCallMiniTextureView.this.videoIsPausedProgress = 1.0f;
                        }
                        invalidate();
                    } else if (!GroupCallMiniTextureView.this.videoIsPaused && GroupCallMiniTextureView.this.videoIsPausedProgress != 0.0f) {
                        GroupCallMiniTextureView.access$724(GroupCallMiniTextureView.this, 0.064f);
                        if (GroupCallMiniTextureView.this.videoIsPausedProgress < 0.0f) {
                            GroupCallMiniTextureView.this.videoIsPausedProgress = 0.0f;
                        }
                        invalidate();
                    }
                    float f9 = GroupCallMiniTextureView.this.videoIsPausedProgress;
                    if (isInAnimation()) {
                        float f10 = this.overlayIconAlphaFrom;
                        float f11 = this.animationProgress;
                        f3 = (f10 * (1.0f - f11)) + (GroupCallMiniTextureView.this.overlayIconAlpha * f11);
                    } else {
                        f3 = GroupCallMiniTextureView.this.overlayIconAlpha;
                    }
                    float f12 = f9 * f3;
                    if (f12 > 0.0f) {
                        float dp9 = AndroidUtilities.dp(48.0f);
                        float measuredWidth2 = (getMeasuredWidth() - dp9) / 2.0f;
                        float measuredHeight3 = (getMeasuredHeight() - dp9) / 2.0f;
                        if (GroupCallMiniTextureView.this.participant == call.videoNotAvailableParticipant) {
                            measuredHeight3 -= dp9 / 2.5f;
                        }
                        RectF rectF = AndroidUtilities.rectTmp;
                        float f13 = measuredHeight3 + dp9;
                        rectF.set((int) measuredWidth2, (int) measuredHeight3, (int) (measuredWidth2 + dp9), (int) f13);
                        if (f12 != 1.0f) {
                            canvas.saveLayerAlpha(rectF, (int) (f12 * 255.0f), 31);
                        } else {
                            canvas.save();
                        }
                        GroupCallMiniTextureView.this.pausedVideoDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                        GroupCallMiniTextureView.this.pausedVideoDrawable.draw(canvas);
                        canvas.restore();
                        float f14 = f12 * groupCallRenderersContainer.progressToFullscreenMode;
                        if (f14 <= 0.0f || GroupCallMiniTextureView.this.participant == call.videoNotAvailableParticipant) {
                            return;
                        }
                        textPaint.setAlpha((int) (f14 * 255.0f));
                        canvas.drawText(string, (measuredWidth2 - (measureText / 2.0f)) + (dp9 / 2.0f), f13 + AndroidUtilities.dp(16.0f), textPaint);
                    }
                }
            }

            @Override
            protected boolean drawChild(Canvas canvas, View view, long j) {
                GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                if (!groupCallMiniTextureView.inPinchToZoom || view != groupCallMiniTextureView.textureView.renderer) {
                    return super.drawChild(canvas, view, j);
                }
                canvas.save();
                GroupCallMiniTextureView groupCallMiniTextureView2 = GroupCallMiniTextureView.this;
                float f = groupCallMiniTextureView2.pinchScale;
                canvas.scale(f, f, groupCallMiniTextureView2.pinchCenterX, groupCallMiniTextureView2.pinchCenterY);
                GroupCallMiniTextureView groupCallMiniTextureView3 = GroupCallMiniTextureView.this;
                canvas.translate(groupCallMiniTextureView3.pinchTranslationX, groupCallMiniTextureView3.pinchTranslationY);
                boolean drawChild = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild;
            }

            @Override
            public void invalidate() {
                super.invalidate();
                GroupCallMiniTextureView.this.invalidateFromChild = true;
                GroupCallMiniTextureView.this.invalidate();
                GroupCallMiniTextureView.this.invalidateFromChild = false;
            }

            @Override
            protected void onFirstFrameRendered() {
                int i;
                ChatObject.VideoParticipant videoParticipant;
                invalidate();
                ChatObject.Call call2 = call;
                if (call2 != null && call2.call.rtmp_stream && GroupCallMiniTextureView.this.postedNoRtmpStreamCallback) {
                    AndroidUtilities.cancelRunOnUIThread(GroupCallMiniTextureView.this.noRtmpStreamCallback);
                    GroupCallMiniTextureView.this.postedNoRtmpStreamCallback = false;
                    GroupCallMiniTextureView.this.noRtmpStreamTextView.animate().cancel();
                    GroupCallMiniTextureView.this.noRtmpStreamTextView.animate().alpha(0.0f).setDuration(150L).start();
                    GroupCallMiniTextureView.this.textureView.animate().cancel();
                    GroupCallMiniTextureView.this.textureView.animate().alpha(1.0f).setDuration(150L).start();
                }
                if (!GroupCallMiniTextureView.this.videoIsPaused && this.renderer.getAlpha() != 1.0f) {
                    this.renderer.animate().setDuration(300L).alpha(1.0f);
                }
                TextureView textureView = this.blurRenderer;
                if (textureView != null && textureView.getAlpha() != 1.0f) {
                    this.blurRenderer.animate().setDuration(300L).alpha(1.0f);
                }
                ImageView imageView = GroupCallMiniTextureView.this.blurredFlippingStub;
                if (imageView != null && imageView.getParent() != null) {
                    if (GroupCallMiniTextureView.this.blurredFlippingStub.getAlpha() == 1.0f) {
                        GroupCallMiniTextureView.this.blurredFlippingStub.animate().alpha(0.0f).setDuration(300L).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                if (GroupCallMiniTextureView.this.blurredFlippingStub.getParent() != null) {
                                    GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                                    groupCallMiniTextureView.textureView.removeView(groupCallMiniTextureView.blurredFlippingStub);
                                }
                            }
                        }).start();
                    } else if (GroupCallMiniTextureView.this.blurredFlippingStub.getParent() != null) {
                        GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                        groupCallMiniTextureView.textureView.removeView(groupCallMiniTextureView.blurredFlippingStub);
                    }
                }
                TextureViewRenderer textureViewRenderer = this.renderer;
                int i2 = textureViewRenderer.rotatedFrameHeight;
                if (i2 == 0 || (i = textureViewRenderer.rotatedFrameWidth) == 0 || (videoParticipant = GroupCallMiniTextureView.this.participant) == null) {
                    return;
                }
                videoParticipant.setAspectRatio(i, i2, call);
            }

            @Override
            protected void onLayout(boolean z3, int i, int i2, int i3, int i4) {
                int i5;
                ChatObject.VideoParticipant videoParticipant;
                GroupCallMiniTextureView groupCallMiniTextureView;
                VoIPTextureView voIPTextureView2;
                int i6;
                GroupCallMiniTextureView groupCallMiniTextureView2 = GroupCallMiniTextureView.this;
                if (groupCallMiniTextureView2.attached && groupCallMiniTextureView2.checkScale) {
                    TextureViewRenderer textureViewRenderer = this.renderer;
                    if (textureViewRenderer.rotatedFrameHeight != 0 && textureViewRenderer.rotatedFrameWidth != 0) {
                        if (GroupCallMiniTextureView.this.showingAsScrimView) {
                            groupCallMiniTextureView = GroupCallMiniTextureView.this;
                        } else {
                            groupCallMiniTextureView = GroupCallMiniTextureView.this;
                            if (!groupCallMiniTextureView.showingInFullscreen) {
                                if (groupCallRenderersContainer.inFullscreenMode) {
                                    voIPTextureView2 = groupCallMiniTextureView.textureView;
                                    i6 = VoIPTextureView.SCALE_TYPE_FILL;
                                } else if (!groupCallMiniTextureView.participant.presentation) {
                                    voIPTextureView2 = groupCallMiniTextureView.textureView;
                                    i6 = VoIPTextureView.SCALE_TYPE_ADAPTIVE;
                                }
                                voIPTextureView2.scaleType = i6;
                                GroupCallMiniTextureView.this.checkScale = false;
                            }
                        }
                        voIPTextureView2 = groupCallMiniTextureView.textureView;
                        i6 = VoIPTextureView.SCALE_TYPE_FIT;
                        voIPTextureView2.scaleType = i6;
                        GroupCallMiniTextureView.this.checkScale = false;
                    }
                }
                super.onLayout(z3, i, i2, i3, i4);
                TextureViewRenderer textureViewRenderer2 = this.renderer;
                int i7 = textureViewRenderer2.rotatedFrameHeight;
                if (i7 == 0 || (i5 = textureViewRenderer2.rotatedFrameWidth) == 0 || (videoParticipant = GroupCallMiniTextureView.this.participant) == null) {
                    return;
                }
                videoParticipant.setAspectRatio(i5, i7, call);
            }

            @Override
            public void requestLayout() {
                GroupCallMiniTextureView.this.requestLayout();
                super.requestLayout();
            }

            @Override
            protected void updateRendererSize() {
                super.updateRendererSize();
                ImageView imageView = GroupCallMiniTextureView.this.blurredFlippingStub;
                if (imageView == null || imageView.getParent() == null) {
                    return;
                }
                GroupCallMiniTextureView.this.blurredFlippingStub.getLayoutParams().width = GroupCallMiniTextureView.this.textureView.renderer.getMeasuredWidth();
                GroupCallMiniTextureView.this.blurredFlippingStub.getLayoutParams().height = GroupCallMiniTextureView.this.textureView.renderer.getMeasuredHeight();
            }
        };
        this.textureView = voIPTextureView;
        voIPTextureView.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
        this.parentContainer = groupCallRenderersContainer;
        this.attachedRenderers = arrayList;
        this.activity = groupCallActivity;
        this.textureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
            @Override
            public void onFirstFrameRendered() {
                for (int i = 0; i < GroupCallMiniTextureView.this.onFirstFrameRunnables.size(); i++) {
                    AndroidUtilities.cancelRunOnUIThread((Runnable) GroupCallMiniTextureView.this.onFirstFrameRunnables.get(i));
                    ((Runnable) GroupCallMiniTextureView.this.onFirstFrameRunnables.get(i)).run();
                }
                GroupCallMiniTextureView.this.onFirstFrameRunnables.clear();
            }

            @Override
            public void onFrameResolutionChanged(int i, int i2, int i3) {
            }
        });
        this.textureView.attachBackgroundRenderer();
        setClipChildren(false);
        this.textureView.renderer.setAlpha(0.0f);
        addView(this.textureView);
        NoVideoStubLayout noVideoStubLayout = new NoVideoStubLayout(getContext());
        this.noVideoStubLayout = noVideoStubLayout;
        addView(noVideoStubLayout);
        SimpleTextView simpleTextView = new SimpleTextView(groupCallRenderersContainer.getContext());
        this.nameView = simpleTextView;
        simpleTextView.setTextSize(13);
        simpleTextView.setTextColor(ColorUtils.setAlphaComponent(-1, 229));
        simpleTextView.setTypeface(AndroidUtilities.bold());
        simpleTextView.setFullTextMaxLines(1);
        simpleTextView.setBuildFullLayout(true);
        FrameLayout frameLayout = new FrameLayout(groupCallRenderersContainer.getContext());
        this.infoContainer = frameLayout;
        frameLayout.addView(simpleTextView, LayoutHelper.createFrame(-1, -2.0f, 19, 32.0f, 0.0f, 8.0f, 0.0f));
        addView(this.infoContainer, LayoutHelper.createFrame(-1, 32.0f));
        this.speakingPaint.setStyle(Paint.Style.STROKE);
        this.speakingPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.speakingPaint.setColor(Theme.getColor(Theme.key_voipgroup_speakingText));
        this.infoContainer.setClipChildren(false);
        RLottieImageView rLottieImageView = new RLottieImageView(groupCallRenderersContainer.getContext());
        this.micIconView = rLottieImageView;
        addView(rLottieImageView, LayoutHelper.createFrame(24, 24.0f, 0, 4.0f, 6.0f, 4.0f, 0.0f));
        ImageView imageView = new ImageView(groupCallRenderersContainer.getContext());
        this.screencastIcon = imageView;
        addView(imageView, LayoutHelper.createFrame(24, 24.0f, 0, 4.0f, 6.0f, 4.0f, 0.0f));
        imageView.setPadding(AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f));
        imageView.setImageDrawable(ContextCompat.getDrawable(groupCallRenderersContainer.getContext(), R.drawable.voicechat_screencast));
        imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.MULTIPLY));
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(19.0f), 0, ColorUtils.setAlphaComponent(-1, 100));
        TextView textView2 = new TextView(groupCallRenderersContainer.getContext()) {
            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (Math.abs(GroupCallMiniTextureView.this.stopSharingTextView.getAlpha() - 1.0f) > 0.001f) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.stopSharingTextView = textView2;
        textView2.setText(LocaleController.getString(R.string.VoipVideoScreenStopSharing));
        this.stopSharingTextView.setTextSize(1, 15.0f);
        this.stopSharingTextView.setTypeface(AndroidUtilities.bold());
        this.stopSharingTextView.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
        this.stopSharingTextView.setTextColor(-1);
        this.stopSharingTextView.setBackground(createSimpleSelectorRoundRectDrawable);
        this.stopSharingTextView.setGravity(17);
        this.stopSharingTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                GroupCallMiniTextureView.this.lambda$new$1(view);
            }
        });
        addView(this.stopSharingTextView, LayoutHelper.createFrame(-2, 38, 51));
        TextView textView3 = new TextView(groupCallRenderersContainer.getContext());
        this.noRtmpStreamTextView = textView3;
        textView3.setTextSize(1, 15.0f);
        this.noRtmpStreamTextView.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
        this.noRtmpStreamTextView.setTextColor(Theme.getColor(Theme.key_voipgroup_lastSeenText));
        this.noRtmpStreamTextView.setBackground(createSimpleSelectorRoundRectDrawable);
        this.noRtmpStreamTextView.setGravity(17);
        this.noRtmpStreamTextView.setAlpha(0.0f);
        if (ChatObject.canManageCalls(chat)) {
            textView = this.noRtmpStreamTextView;
            formatString = LocaleController.getString(R.string.NoRtmpStreamFromAppOwner);
        } else {
            textView = this.noRtmpStreamTextView;
            formatString = LocaleController.formatString("NoRtmpStreamFromAppViewer", R.string.NoRtmpStreamFromAppViewer, chat.title);
        }
        textView.setText(AndroidUtilities.replaceTags(formatString));
        addView(this.noRtmpStreamTextView, LayoutHelper.createFrame(-2, -2, 51));
    }

    static float access$116(GroupCallMiniTextureView groupCallMiniTextureView, float f) {
        float f2 = groupCallMiniTextureView.progressToBackground + f;
        groupCallMiniTextureView.progressToBackground = f2;
        return f2;
    }

    static float access$716(GroupCallMiniTextureView groupCallMiniTextureView, float f) {
        float f2 = groupCallMiniTextureView.videoIsPausedProgress + f;
        groupCallMiniTextureView.videoIsPausedProgress = f2;
        return f2;
    }

    static float access$724(GroupCallMiniTextureView groupCallMiniTextureView, float f) {
        float f2 = groupCallMiniTextureView.videoIsPausedProgress - f;
        groupCallMiniTextureView.videoIsPausedProgress = f2;
        return f2;
    }

    public static GroupCallMiniTextureView getOrCreate(ArrayList arrayList, GroupCallRenderersContainer groupCallRenderersContainer, GroupCallGridCell groupCallGridCell, GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell, GroupCallGridCell groupCallGridCell2, ChatObject.VideoParticipant videoParticipant, ChatObject.Call call, GroupCallActivity groupCallActivity) {
        GroupCallMiniTextureView groupCallMiniTextureView;
        int i = 0;
        while (true) {
            if (i >= arrayList.size()) {
                groupCallMiniTextureView = null;
                break;
            }
            if (videoParticipant.equals(((GroupCallMiniTextureView) arrayList.get(i)).participant)) {
                groupCallMiniTextureView = (GroupCallMiniTextureView) arrayList.get(i);
                break;
            }
            i++;
        }
        if (groupCallMiniTextureView == null) {
            groupCallMiniTextureView = new GroupCallMiniTextureView(groupCallRenderersContainer, arrayList, call, groupCallActivity);
        }
        if (groupCallGridCell != null) {
            groupCallMiniTextureView.setPrimaryView(groupCallGridCell);
        }
        if (groupCallUserCell != null) {
            groupCallMiniTextureView.setSecondaryView(groupCallUserCell);
        }
        if (groupCallGridCell2 != null) {
            groupCallMiniTextureView.setTabletGridView(groupCallGridCell2);
        }
        return groupCallMiniTextureView;
    }

    public void lambda$new$0() {
        if (this.textureView.renderer.isFirstFrameRendered()) {
            return;
        }
        this.textureView.animate().cancel();
        this.textureView.animate().alpha(0.0f).setDuration(150L).start();
        this.noRtmpStreamTextView.animate().cancel();
        this.noRtmpStreamTextView.animate().alpha(1.0f).setDuration(150L).start();
    }

    public void lambda$new$1(View view) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().stopScreenCapture();
        }
        this.stopSharingTextView.animate().alpha(0.0f).scaleX(0.0f).scaleY(0.0f).setDuration(180L).start();
    }

    public void lambda$saveThumb$4(Bitmap bitmap) {
        HashMap<String, Bitmap> hashMap = this.call.thumbs;
        ChatObject.VideoParticipant videoParticipant = this.participant;
        boolean z = videoParticipant.presentation;
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant = videoParticipant.participant;
        hashMap.put(z ? tL_groupCallParticipant.presentationEndpoint : tL_groupCallParticipant.videoEndpoint, bitmap);
    }

    public void lambda$saveThumb$5(final Bitmap bitmap, int i) {
        if (bitmap == null || bitmap.getPixel(0, 0) == 0) {
            return;
        }
        Utilities.stackBlurBitmap(bitmap, Math.max(7, Math.max(bitmap.getWidth(), bitmap.getHeight()) / 180));
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                GroupCallMiniTextureView.this.lambda$saveThumb$4(bitmap);
            }
        });
    }

    public void lambda$startFlipAnimation$7(ValueAnimator valueAnimator) {
        boolean z;
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (floatValue < 0.5f) {
            z = false;
        } else {
            floatValue -= 1.0f;
            z = true;
        }
        if (z && !this.flipHalfReached) {
            this.blurredFlippingStub.setAlpha(1.0f);
            this.flipHalfReached = true;
            this.textureView.renderer.clearImage();
        }
        float f = floatValue * 180.0f;
        this.blurredFlippingStub.setRotationY(f);
        this.textureView.renderer.setRotationY(f);
    }

    public void lambda$updateAttachState$2(View view) {
        this.parentContainer.removeView(view);
    }

    public void lambda$updateAttachState$3(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.progressToNoVideoStub = floatValue;
        this.noVideoStubLayout.setAlpha(floatValue);
        this.textureView.invalidate();
    }

    public void lambda$updateIconColor$6(int i, int i2, int i3, int i4, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.lastIconColor = ColorUtils.blendARGB(i, i2, floatValue);
        int blendARGB = ColorUtils.blendARGB(i3, i4, floatValue);
        this.lastSpeakingFrameColor = blendARGB;
        this.speakingPaint.setColor(blendARGB);
        if (this.progressToSpeaking > 0.0f) {
            invalidate();
        }
    }

    private void loadThumb() {
        ImageLocation forChat;
        GradientDrawable gradientDrawable;
        TLRPC.Chat chat;
        if (this.thumb != null) {
            return;
        }
        HashMap<String, Bitmap> hashMap = this.call.thumbs;
        ChatObject.VideoParticipant videoParticipant = this.participant;
        boolean z = videoParticipant.presentation;
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant = videoParticipant.participant;
        Bitmap bitmap = hashMap.get(z ? tL_groupCallParticipant.presentationEndpoint : tL_groupCallParticipant.videoEndpoint);
        this.thumb = bitmap;
        this.textureView.setThumb(bitmap);
        if (this.thumb == null) {
            long peerId = MessageObject.getPeerId(this.participant.participant.peer);
            ChatObject.VideoParticipant videoParticipant2 = this.participant;
            if (videoParticipant2.participant.self && videoParticipant2.presentation) {
                this.imageReceiver.setImageBitmap(new MotionBackgroundDrawable(-14602694, -13935795, -14395293, -14203560, true));
                return;
            }
            MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            if (peerId > 0) {
                TLRPC.User user = messagesController.getUser(Long.valueOf(peerId));
                forChat = ImageLocation.getForUser(user, 1);
                int colorForId = user != null ? AvatarDrawable.getColorForId(user.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(colorForId, -16777216, 0.2f), ColorUtils.blendARGB(colorForId, -16777216, 0.4f)});
                chat = user;
            } else {
                TLRPC.Chat chat2 = messagesController.getChat(Long.valueOf(-peerId));
                forChat = ImageLocation.getForChat(chat2, 1);
                int colorForId2 = chat2 != null ? AvatarDrawable.getColorForId(chat2.id) : ColorUtils.blendARGB(-16777216, -1, 0.2f);
                gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{ColorUtils.blendARGB(colorForId2, -16777216, 0.2f), ColorUtils.blendARGB(colorForId2, -16777216, 0.4f)});
                chat = chat2;
            }
            this.imageReceiver.setImage(forChat, "50_50_b", gradientDrawable, null, chat, 0);
        }
    }

    private void updateIconColor(boolean r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.updateIconColor(boolean):void");
    }

    @Override
    protected void dispatchDraw(android.graphics.Canvas r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.dispatchDraw(android.graphics.Canvas):void");
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        if (!this.swipeToBack || (view != this.textureView && view != this.noVideoStubLayout)) {
            return super.drawChild(canvas, view, j);
        }
        float max = (Math.max(0.0f, 1.0f - (Math.abs(this.swipeToBackDy) / AndroidUtilities.dp(300.0f))) * 0.1f) + 0.9f;
        canvas.save();
        canvas.scale(max, max, view.getX() + (view.getMeasuredWidth() / 2.0f), view.getY() + (view.getMeasuredHeight() / 2.0f));
        canvas.translate(0.0f, this.swipeToBackDy);
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restore();
        return drawChild;
    }

    public void forceDetach(boolean z) {
        this.forceDetached = true;
        this.attached = false;
        this.parentContainer.detach(this);
        if (z) {
            if (this.participant.participant.self) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().setLocalSink(null, this.participant.presentation);
                }
            } else if (VoIPService.getSharedInstance() != null && !RTMPStreamPipOverlay.isVisible()) {
                VoIPService sharedInstance = VoIPService.getSharedInstance();
                ChatObject.VideoParticipant videoParticipant = this.participant;
                sharedInstance.removeRemoteSink(videoParticipant.participant, videoParticipant.presentation);
            }
        }
        saveThumb();
        ValueAnimator valueAnimator = this.noVideoStubAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.noVideoStubAnimator.cancel();
        }
        this.textureView.renderer.release();
    }

    public String getName() {
        long peerId = MessageObject.getPeerId(this.participant.participant.peer);
        return DialogObject.isUserDialog(peerId) ? UserObject.getUserName(AccountInstance.getInstance(UserConfig.selectedAccount).getMessagesController().getUser(Long.valueOf(peerId))) : AccountInstance.getInstance(UserConfig.selectedAccount).getMessagesController().getChat(Long.valueOf(-peerId)).title;
    }

    public void getRenderBufferBitmap(GlGenericDrawer.TextureCallback textureCallback) {
        this.textureView.renderer.getRenderBufferBitmap(textureCallback);
    }

    public boolean hasImage() {
        return this.textureView.stubVisibleProgress == 1.0f;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!this.invalidateFromChild) {
            this.textureView.invalidate();
        }
        GroupCallGridCell groupCallGridCell = this.primaryView;
        if (groupCallGridCell != null) {
            groupCallGridCell.invalidate();
            if (this.activity.getScrimView() == this.primaryView) {
                this.activity.getContainerView().invalidate();
            }
        }
        GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = this.secondaryView;
        if (groupCallUserCell != null) {
            groupCallUserCell.invalidate();
            if (this.secondaryView.getParent() != null) {
                ((View) this.secondaryView.getParent()).invalidate();
            }
        }
        if (getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    public boolean isAttached() {
        return this.attached;
    }

    public boolean isFullyVisible() {
        return !this.showingInFullscreen && !this.animateToFullscreen && this.attached && this.textureView.renderer.isFirstFrameRendered() && getAlpha() == 1.0f;
    }

    public boolean isInsideStopScreenButton(float f, float f2) {
        this.stopSharingTextView.getHitRect(this.rect);
        return this.rect.contains((int) f, (int) f2);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.imageReceiver.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int r14, int r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.onMeasure(int, int):void");
    }

    @Override
    public void onStatusChanged() {
        invalidate();
        updateIconColor(true);
        if (this.noVideoStubLayout.getVisibility() == 0) {
            this.noVideoStubLayout.updateMuteButtonState(true);
        }
    }

    public void release() {
        this.textureView.renderer.release();
        GroupCallStatusIcon groupCallStatusIcon = this.statusIcon;
        if (groupCallStatusIcon != null) {
            this.activity.statusIconPool.add(groupCallStatusIcon);
            this.statusIcon.setCallback(null);
            this.statusIcon.setImageView(null);
        }
        this.statusIcon = null;
    }

    public void runDelayedAnimations() {
        for (int i = 0; i < this.onFirstFrameRunnables.size(); i++) {
            ((Runnable) this.onFirstFrameRunnables.get(i)).run();
        }
        this.onFirstFrameRunnables.clear();
    }

    public void runOnFrameRendered(Runnable runnable) {
        if (this.textureView.renderer.isFirstFrameRendered()) {
            runnable.run();
        } else {
            AndroidUtilities.runOnUIThread(runnable, 250L);
            this.onFirstFrameRunnables.add(runnable);
        }
    }

    public void saveThumb() {
        if (this.participant == null || this.textureView.renderer.getMeasuredHeight() == 0 || this.textureView.renderer.getMeasuredWidth() == 0) {
            return;
        }
        getRenderBufferBitmap(new GlGenericDrawer.TextureCallback() {
            @Override
            public final void run(Bitmap bitmap, int i) {
                GroupCallMiniTextureView.this.lambda$saveThumb$5(bitmap, i);
            }
        });
    }

    public void setAmplitude(double d) {
        this.statusIcon.setAmplitude(d);
        this.noVideoStubLayout.setAmplitude(d);
    }

    public void setFullscreenMode(boolean z, boolean z2) {
        if (this.isFullscreenMode != z) {
            this.isFullscreenMode = z;
            updateAttachState(!(this.primaryView == null && this.tabletGridView == null) && z2);
        }
    }

    public void setPrimaryView(GroupCallGridCell groupCallGridCell) {
        if (this.primaryView != groupCallGridCell) {
            this.primaryView = groupCallGridCell;
            this.checkScale = true;
            updateAttachState(true);
        }
    }

    public void setSecondaryView(GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell) {
        if (this.secondaryView != groupCallUserCell) {
            this.secondaryView = groupCallUserCell;
            this.checkScale = true;
            updateAttachState(true);
        }
    }

    public void setShowingAsScrimView(boolean z, boolean z2) {
        this.showingAsScrimView = z;
        updateAttachState(z2);
    }

    public void setShowingInFullscreen(boolean z, boolean z2) {
        if (this.showingInFullscreen != z) {
            this.showingInFullscreen = z;
            this.checkScale = true;
            updateAttachState(z2);
        }
    }

    public void setSwipeToBack(boolean z, float f) {
        if (this.swipeToBack == z && this.swipeToBackDy == f) {
            return;
        }
        this.swipeToBack = z;
        this.swipeToBackDy = f;
        this.textureView.invalidate();
        invalidate();
    }

    public void setTabletGridView(GroupCallGridCell groupCallGridCell) {
        if (this.tabletGridView != groupCallGridCell) {
            this.tabletGridView = groupCallGridCell;
            updateAttachState(true);
        }
    }

    public void setViews(GroupCallGridCell groupCallGridCell, GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell, GroupCallGridCell groupCallGridCell2) {
        this.primaryView = groupCallGridCell;
        this.secondaryView = groupCallUserCell;
        this.tabletGridView = groupCallGridCell2;
    }

    public void setZoom(boolean z, float f, float f2, float f3, float f4, float f5) {
        if (this.pinchScale == f && this.pinchCenterX == f2 && this.pinchCenterY == f3 && this.pinchTranslationX == f4 && this.pinchTranslationY == f5) {
            return;
        }
        this.inPinchToZoom = z;
        this.pinchScale = f;
        this.pinchCenterX = f2;
        this.pinchCenterY = f3;
        this.pinchTranslationX = f4;
        this.pinchTranslationY = f5;
        this.textureView.invalidate();
    }

    public void startFlipAnimation() {
        if (this.flipAnimator != null) {
            return;
        }
        this.flipHalfReached = false;
        ImageView imageView = this.blurredFlippingStub;
        if (imageView == null) {
            this.blurredFlippingStub = new ImageView(getContext());
        } else {
            imageView.animate().cancel();
        }
        if (this.textureView.renderer.isFirstFrameRendered()) {
            Bitmap bitmap = this.textureView.blurRenderer.getBitmap(100, 100);
            if (bitmap != null) {
                Utilities.blurBitmap(bitmap, 3, 1, bitmap.getWidth(), bitmap.getHeight(), bitmap.getRowBytes());
                this.blurredFlippingStub.setBackground(new BitmapDrawable(bitmap));
            }
            this.blurredFlippingStub.setAlpha(0.0f);
        } else {
            this.blurredFlippingStub.setAlpha(1.0f);
        }
        if (this.blurredFlippingStub.getParent() == null) {
            this.textureView.addView(this.blurredFlippingStub);
        }
        ((FrameLayout.LayoutParams) this.blurredFlippingStub.getLayoutParams()).gravity = 17;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.flipAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GroupCallMiniTextureView.this.lambda$startFlipAnimation$7(valueAnimator);
            }
        });
        this.flipAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                GroupCallMiniTextureView groupCallMiniTextureView = GroupCallMiniTextureView.this;
                groupCallMiniTextureView.flipAnimator = null;
                groupCallMiniTextureView.textureView.setRotationY(0.0f);
                GroupCallMiniTextureView groupCallMiniTextureView2 = GroupCallMiniTextureView.this;
                if (groupCallMiniTextureView2.flipHalfReached) {
                    return;
                }
                groupCallMiniTextureView2.textureView.renderer.clearImage();
            }
        });
        this.flipAnimator.setDuration(400L);
        this.flipAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.flipAnimator.start();
    }

    public void updateAttachState(boolean r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.updateAttachState(boolean):void");
    }

    public void updateInfo() {
        String str;
        if (this.attached) {
            long peerId = MessageObject.getPeerId(this.participant.participant.peer);
            if (DialogObject.isUserDialog(peerId)) {
                str = UserObject.getUserName(AccountInstance.getInstance(this.currentAccount).getMessagesController().getUser(Long.valueOf(peerId)));
            } else {
                TLRPC.Chat chat = AccountInstance.getInstance(this.currentAccount).getMessagesController().getChat(Long.valueOf(-peerId));
                str = chat != null ? chat.title : null;
            }
            this.nameView.setText(str);
        }
    }

    public void updatePosition(android.view.ViewGroup r9, android.view.ViewGroup r10, org.telegram.ui.Components.RecyclerListView r11, org.telegram.ui.Components.voip.GroupCallRenderersContainer r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.GroupCallMiniTextureView.updatePosition(android.view.ViewGroup, android.view.ViewGroup, org.telegram.ui.Components.RecyclerListView, org.telegram.ui.Components.voip.GroupCallRenderersContainer):void");
    }

    public void updateSize(int i) {
        int measuredWidth = this.parentContainer.getMeasuredWidth() - AndroidUtilities.dp(6.0f);
        if ((this.collapseSize == i || i <= 0) && (this.fullSize == measuredWidth || measuredWidth <= 0)) {
            return;
        }
        if (i != 0) {
            this.collapseSize = i;
        }
        if (measuredWidth != 0) {
            this.fullSize = measuredWidth;
        }
        this.nameView.setFullLayoutAdditionalWidth(measuredWidth - i, 0);
    }
}
