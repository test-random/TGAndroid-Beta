package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.messenger.voip.VoipAudioManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AccountSelectCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.GroupCallInvitedCell;
import org.telegram.ui.Cells.GroupCallTextCell;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlobDrawable;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FillLastGridLayoutManager;
import org.telegram.ui.Components.GroupCallFullscreenAdapter;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.GroupCallRecordAlert;
import org.telegram.ui.Components.GroupVoipInviteAlert;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.PermissionRequest;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.Components.voip.GroupCallGridCell;
import org.telegram.ui.Components.voip.GroupCallMiniTextureView;
import org.telegram.ui.Components.voip.GroupCallRenderersContainer;
import org.telegram.ui.Components.voip.PrivateVideoPreviewDialog;
import org.telegram.ui.Components.voip.PrivateVideoPreviewDialog$$ExternalSyntheticApiModelOutline0;
import org.telegram.ui.Components.voip.RTMPStreamPipOverlay;
import org.telegram.ui.Components.voip.VoIPTextureView;
import org.telegram.ui.Components.voip.VoIPToggleButton;
import org.telegram.ui.GroupCallActivity;
import org.telegram.ui.Stories.recorder.DominantColors;
import org.webrtc.voiceengine.WebRtcAudioTrack;

public class GroupCallActivity extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, VoIPService.StateListener {
    public static GroupCallActivity groupCallInstance;
    public static boolean groupCallUiVisible;
    public static boolean isLandscapeMode;
    public static boolean isTabletMode;
    public static boolean paused;
    private View accountGap;
    private AccountInstance accountInstance;
    private AccountSelectCell accountSelectCell;
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private View actionBarBackground;
    private View actionBarShadow;
    ObjectAnimator additionalSubtitleYAnimator;
    private ActionBarMenuSubItem adminItem;
    private float amplitude;
    private float animateAmplitudeDiff;
    boolean animateButtonsOnNextLayout;
    private float animateToAmplitude;
    private boolean animatingToFullscreenExpand;
    private boolean anyEnterEventSent;
    private final ArrayList attachedRenderers;
    private final ArrayList attachedRenderersTmp;
    private final AvatarPreviewPagerIndicator avatarPagerIndicator;
    private final FrameLayout avatarPreviewContainer;
    private boolean avatarPriviewTransitionInProgress;
    AvatarUpdaterDelegate avatarUpdaterDelegate;
    private boolean avatarsPreviewShowed;
    private final ProfileGalleryView avatarsViewPager;
    private int backgroundColor;
    private RLottieDrawable bigMicDrawable;
    private final BlobDrawable bigWaveDrawable;
    private View blurredView;
    private HashMap buttonsAnimationParamsX;
    private HashMap buttonsAnimationParamsY;
    private GradientDrawable buttonsBackgroundGradient;
    private final View buttonsBackgroundGradientView;
    private final View buttonsBackgroundGradientView2;
    private FrameLayout buttonsContainer;
    private int buttonsVisibility;
    public ChatObject.Call call;
    private boolean callInitied;
    private VoIPToggleButton cameraButton;
    private float cameraButtonScale;
    public CellFlickerDrawable cellFlickerDrawable;
    private boolean changingPermissions;
    private float colorProgress;
    private final int[] colorsTmp;
    private boolean contentFullyOverlayed;
    private long creatingServiceTime;
    ImageUpdater currentAvatarUpdater;
    private int currentCallState;
    public TLRPC.Chat currentChat;
    private ViewGroup currentOptionsLayout;
    private WeavingState currentState;
    private boolean delayedGroupCallUpdated;
    private DiffUtil.Callback diffUtilsCallback;
    private boolean drawSpeakingSubtitle;
    public boolean drawingForBlur;
    private ActionBarMenuSubItem editTitleItem;
    private boolean enterEventSent;
    private ActionBarMenuSubItem everyoneItem;
    private ValueAnimator expandAnimator;
    private ImageView expandButton;
    private ValueAnimator expandSizeAnimator;
    private VoIPToggleButton flipButton;
    private final RLottieDrawable flipIcon;
    private int flipIconCurrentEndFrame;
    GroupCallFullscreenAdapter fullscreenAdapter;
    private final DefaultItemAnimator fullscreenListItemAnimator;
    RecyclerListView fullscreenUsersListView;
    private int[] gradientColors;
    private GroupVoipInviteAlert groupVoipInviteAlert;
    private RLottieDrawable handDrawables;
    private boolean hasScrimAnchorView;
    private boolean hasVideo;
    private boolean invalidateColors;
    private ActionBarMenuSubItem inviteItem;
    private String[] invites;
    private GroupCallItemAnimator itemAnimator;
    private long lastUpdateTime;
    private FillLastGridLayoutManager layoutManager;
    private Paint leaveBackgroundPaint;
    private VoIPToggleButton leaveButton;
    private ActionBarMenuSubItem leaveItem;
    private ListAdapter listAdapter;
    private RecyclerListView listView;
    private Paint listViewBackgroundPaint;
    private boolean listViewVideoVisibility;
    private ValueAnimator liveLabelBgColorAnimator;
    private Paint liveLabelPaint;
    private TextView liveLabelTextView;
    private final LinearLayout menuItemsContainer;
    private ImageView minimizeButton;
    private RLottieImageView muteButton;
    private ValueAnimator muteButtonAnimator;
    private int muteButtonState;
    private TextView[] muteLabel;
    private boolean needTextureLightning;
    private ActionBarMenuSubItem noiseItem;
    private int oldAddMemberRow;
    private int oldCount;
    private ArrayList oldInvited;
    private int oldInvitedEndRow;
    private int oldInvitedStartRow;
    private ArrayList oldParticipants;
    private int oldUsersEndRow;
    private int oldUsersStartRow;
    private int oldUsersVideoEndRow;
    private int oldUsersVideoStartRow;
    private int oldVideoDividerRow;
    private int oldVideoNotAvailableRow;
    private ArrayList oldVideoParticipants;
    private Runnable onUserLeaveHintListener;
    private ActionBarMenuItem otherItem;
    private Paint paint;
    private Paint paintTmp;
    private LaunchActivity parentActivity;
    private ActionBarMenuSubItem permissionItem;
    PinchToZoomHelper pinchToZoomHelper;
    private ActionBarMenuItem pipItem;
    private boolean playingHandAnimation;
    private int popupAnimationIndex;
    private Runnable pressRunnable;
    private boolean pressed;
    private WeavingState prevState;
    PrivateVideoPreviewDialog previewDialog;
    private boolean previewTextureTransitionEnabled;
    private float progressToAvatarPreview;
    float progressToHideUi;
    private RadialGradient radialGradient;
    private final Matrix radialMatrix;
    private final Paint radialPaint;
    private RadialProgressView radialProgressView;
    private RecordCallDrawable recordCallDrawable;
    private HintView recordHintView;
    private ActionBarMenuSubItem recordItem;
    private RectF rect;
    private HintView reminderHintView;
    private GroupCallRenderersContainer renderersContainer;
    ViewTreeObserver.OnPreDrawListener requestFullscreenListener;
    private ValueAnimator scheduleAnimator;
    private TextView scheduleButtonTextView;
    private float scheduleButtonsScale;
    private boolean scheduleHasFewPeers;
    private TextView scheduleInfoTextView;
    private TLRPC.InputPeer schedulePeer;
    private int scheduleStartAt;
    private SimpleTextView scheduleStartAtTextView;
    private SimpleTextView scheduleStartInTextView;
    private SimpleTextView scheduleTimeTextView;
    private LinearLayout scheduleTimerContainer;
    private boolean scheduled;
    private String scheduledHash;
    private ActionBarMenuSubItem screenItem;
    private ActionBarMenuItem screenShareItem;
    private AnimatorSet scrimAnimatorSet;
    private GroupCallFullscreenAdapter.GroupCallUserCell scrimFullscreenView;
    private GroupCallGridCell scrimGridView;
    private Paint scrimPaint;
    private View scrimPopupLayout;
    private ActionBarPopupWindow scrimPopupWindow;
    private GroupCallMiniTextureView scrimRenderer;
    private GroupCallUserCell scrimView;
    private boolean scrimViewAttached;
    private float scrollOffsetY;
    private TLRPC.Peer selfPeer;
    private int shaderBitmapSize;
    private Drawable shadowDrawable;
    private ShareAlert shareAlert;
    private float showLightingProgress;
    private float showWavesProgress;
    private VoIPToggleButton soundButton;
    private float soundButtonScale;
    private ActionBarMenuSubItem soundItem;
    private View soundItemDivider;
    private final GridLayoutManager.SpanSizeLookup spanSizeLookup;
    private boolean startingGroupCall;
    private WeavingState[] states;
    public final ArrayList statusIconPool;
    ObjectAnimator subtitleYAnimator;
    private float switchProgress;
    private float switchToButtonInt2;
    private float switchToButtonProgress;
    GroupCallTabletGridAdapter tabletGridAdapter;
    RecyclerListView tabletVideoGridView;
    private LightningView textureLightningView;
    private float textureLightningViewAnimatingAlpha;
    private final BlobDrawable tinyWaveDrawable;
    private LinearLayout titleLayout;
    private AudioPlayerAlert.ClippingTextViewSwitcher titleTextView;
    private UndoView[] undoView;
    private Runnable unmuteRunnable;
    private Runnable updateCallRecordRunnable;
    private Runnable updateSchedeulRunnable;
    private final Runnable updateTextureLightningRunnable;
    private boolean useBlur;
    private TLObject userSwitchObject;
    LongSparseIntArray visiblePeerIds;
    public final ArrayList visibleVideoParticipants;
    private Boolean wasExpandBigSize;
    private Boolean wasNotInLayoutFullscreen;
    private WatchersView watchersView;
    public static volatile DispatchQueue updateTextureLightningQueue = new DispatchQueue("updateTextureLightningQueue");
    public static final Property COLOR_PROGRESS = new AnimationProperties.FloatProperty("colorProgress") {
        @Override
        public Float get(GroupCallActivity groupCallActivity) {
            return Float.valueOf(groupCallActivity.getColorProgress());
        }

        @Override
        public void setValue(GroupCallActivity groupCallActivity, float f) {
            groupCallActivity.setColorProgress(f);
        }
    };

    class AnonymousClass18 extends FrameLayout {
        AnimatorSet currentButtonsAnimation;
        int currentLightColor;
        final OvershootInterpolator overshootInterpolator;

        AnonymousClass18(Context context) {
            super(context);
            this.overshootInterpolator = new OvershootInterpolator(1.5f);
        }

        @Override
        protected void dispatchDraw(android.graphics.Canvas r26) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.AnonymousClass18.dispatchDraw(android.graphics.Canvas):void");
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (view != GroupCallActivity.this.muteButton || view.getScaleX() == 1.0f) {
                return super.drawChild(canvas, view, j);
            }
            canvas.save();
            float scaleX = (((1.0f / GroupCallActivity.this.muteButton.getScaleX()) - 1.0f) * 0.2f) + 1.0f;
            canvas.scale(scaleX, scaleX, view.getX() + (view.getMeasuredWidth() / 2.0f), view.getY() + (view.getMeasuredHeight() / 2.0f));
            boolean drawChild = super.drawChild(canvas, view, j);
            canvas.restore();
            return drawChild;
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int measuredWidth;
            VoIPToggleButton voIPToggleButton;
            int i5;
            int measuredWidth2 = (getMeasuredWidth() - AndroidUtilities.dp(122.0f)) / 2;
            int measuredHeight = getMeasuredHeight();
            int i6 = GroupCallActivity.this.cameraButton.getVisibility() != 0 ? 4 : 5;
            if (GroupCallActivity.this.soundButton.getVisibility() != 0) {
                i6--;
            }
            if (GroupCallActivity.this.flipButton.getVisibility() != 0) {
                i6--;
            }
            if (GroupCallActivity.isLandscapeMode && !GroupCallActivity.isTabletMode) {
                int measuredHeight2 = getMeasuredHeight() / i6;
                if (GroupCallActivity.this.soundButton.getVisibility() == 0) {
                    int i7 = measuredHeight2 / 2;
                    int measuredHeight3 = i7 - (GroupCallActivity.this.cameraButton.getMeasuredHeight() / 2);
                    int measuredWidth3 = (getMeasuredWidth() - GroupCallActivity.this.cameraButton.getMeasuredWidth()) >> 1;
                    GroupCallActivity.this.cameraButton.layout(measuredWidth3, measuredHeight3, GroupCallActivity.this.cameraButton.getMeasuredWidth() + measuredWidth3, GroupCallActivity.this.cameraButton.getMeasuredHeight() + measuredHeight3);
                    int measuredHeight4 = (i7 + (i6 == 4 ? measuredHeight2 : 0)) - (GroupCallActivity.this.soundButton.getMeasuredHeight() / 2);
                    int measuredWidth4 = (getMeasuredWidth() - GroupCallActivity.this.soundButton.getMeasuredWidth()) >> 1;
                    GroupCallActivity.this.soundButton.layout(measuredWidth4, measuredHeight4, GroupCallActivity.this.soundButton.getMeasuredWidth() + measuredWidth4, GroupCallActivity.this.soundButton.getMeasuredHeight() + measuredHeight4);
                } else {
                    int i8 = measuredHeight2 / 2;
                    int measuredHeight5 = i8 - (GroupCallActivity.this.flipButton.getMeasuredHeight() / 2);
                    int measuredWidth5 = (getMeasuredWidth() - GroupCallActivity.this.flipButton.getMeasuredWidth()) >> 1;
                    GroupCallActivity.this.flipButton.layout(measuredWidth5, measuredHeight5, GroupCallActivity.this.flipButton.getMeasuredWidth() + measuredWidth5, GroupCallActivity.this.flipButton.getMeasuredHeight() + measuredHeight5);
                    int measuredHeight6 = (i8 + (i6 == 4 ? measuredHeight2 : 0)) - (GroupCallActivity.this.cameraButton.getMeasuredHeight() / 2);
                    int measuredWidth6 = (getMeasuredWidth() - GroupCallActivity.this.cameraButton.getMeasuredWidth()) >> 1;
                    GroupCallActivity.this.cameraButton.layout(measuredWidth6, measuredHeight6, GroupCallActivity.this.cameraButton.getMeasuredWidth() + measuredWidth6, GroupCallActivity.this.cameraButton.getMeasuredHeight() + measuredHeight6);
                }
                int i9 = measuredHeight2 / 2;
                int measuredHeight7 = ((i6 == 4 ? measuredHeight2 * 3 : measuredHeight2 * 2) + i9) - (GroupCallActivity.this.leaveButton.getMeasuredHeight() / 2);
                int measuredWidth7 = (getMeasuredWidth() - GroupCallActivity.this.leaveButton.getMeasuredWidth()) >> 1;
                GroupCallActivity.this.leaveButton.layout(measuredWidth7, measuredHeight7, GroupCallActivity.this.leaveButton.getMeasuredWidth() + measuredWidth7, GroupCallActivity.this.leaveButton.getMeasuredHeight() + measuredHeight7);
                int measuredWidth8 = (((i6 == 4 ? measuredHeight2 * 2 : measuredHeight2) + i9) - (GroupCallActivity.this.muteButton.getMeasuredWidth() / 2)) - AndroidUtilities.dp(4.0f);
                int measuredWidth9 = (getMeasuredWidth() - GroupCallActivity.this.muteButton.getMeasuredWidth()) >> 1;
                if (i6 == 3) {
                    measuredWidth8 -= AndroidUtilities.dp(6.0f);
                }
                GroupCallActivity.this.muteButton.layout(measuredWidth9, measuredWidth8, GroupCallActivity.this.muteButton.getMeasuredWidth() + measuredWidth9, GroupCallActivity.this.muteButton.getMeasuredHeight() + measuredWidth8);
                GroupCallActivity.this.minimizeButton.layout(measuredWidth9, measuredWidth8, GroupCallActivity.this.minimizeButton.getMeasuredWidth() + measuredWidth9, GroupCallActivity.this.minimizeButton.getMeasuredHeight() + measuredWidth8);
                GroupCallActivity.this.expandButton.layout(measuredWidth9, measuredWidth8, GroupCallActivity.this.expandButton.getMeasuredWidth() + measuredWidth9, GroupCallActivity.this.expandButton.getMeasuredHeight() + measuredWidth8);
                float dp = AndroidUtilities.dp(52.0f) / (GroupCallActivity.this.muteButton.getMeasuredWidth() - AndroidUtilities.dp(8.0f));
                GroupCallActivity.this.muteButton.animate().cancel();
                GroupCallActivity.this.muteButton.setScaleX(dp);
                GroupCallActivity.this.muteButton.setScaleY(dp);
                for (int i10 = 0; i10 < 2; i10++) {
                    int measuredWidth10 = (getMeasuredWidth() - GroupCallActivity.this.muteLabel[i10].getMeasuredWidth()) >> 1;
                    int i11 = i6 == 4 ? measuredHeight2 * 2 : measuredHeight2;
                    int measuredWidth11 = ((i9 + i11) - (GroupCallActivity.this.muteButton.getMeasuredWidth() / 2)) - AndroidUtilities.dp(4.0f);
                    if (i6 == 3) {
                        measuredWidth11 -= AndroidUtilities.dp(6.0f);
                    }
                    int measuredWidth12 = (int) (measuredWidth11 + (GroupCallActivity.this.muteButton.getMeasuredWidth() * 0.687f) + AndroidUtilities.dp(4.0f));
                    if (GroupCallActivity.this.muteLabel[i10].getMeasuredHeight() + measuredWidth12 > i11 + measuredHeight2) {
                        measuredWidth12 -= AndroidUtilities.dp(4.0f);
                    }
                    GroupCallActivity.this.muteLabel[i10].layout(measuredWidth10, measuredWidth12, GroupCallActivity.this.muteLabel[i10].getMeasuredWidth() + measuredWidth10, GroupCallActivity.this.muteLabel[i10].getMeasuredHeight() + measuredWidth12);
                    GroupCallActivity.this.muteLabel[i10].setScaleX(0.687f);
                    GroupCallActivity.this.muteLabel[i10].setScaleY(0.687f);
                }
            } else if ((!GroupCallActivity.this.renderersContainer.inFullscreenMode || GroupCallActivity.isTabletMode) && !GroupCallActivity.this.isRtmpStream()) {
                int dp2 = AndroidUtilities.dp(0.0f);
                if (GroupCallActivity.this.soundButton.getVisibility() == 0) {
                    if (GroupCallActivity.this.cameraButton.getVisibility() == 0) {
                        int measuredWidth13 = (measuredWidth2 - GroupCallActivity.this.cameraButton.getMeasuredWidth()) / 2;
                        int measuredHeight8 = (measuredHeight - GroupCallActivity.this.cameraButton.getMeasuredHeight()) / 2;
                        GroupCallActivity.this.cameraButton.layout(measuredWidth13, measuredHeight8, GroupCallActivity.this.cameraButton.getMeasuredWidth() + measuredWidth13, GroupCallActivity.this.cameraButton.getMeasuredHeight() + measuredHeight8);
                        measuredWidth = (measuredWidth2 - GroupCallActivity.this.soundButton.getMeasuredWidth()) / 2;
                        voIPToggleButton = GroupCallActivity.this.leaveButton;
                    } else {
                        measuredWidth = (measuredWidth2 - GroupCallActivity.this.soundButton.getMeasuredWidth()) / 2;
                        voIPToggleButton = GroupCallActivity.this.soundButton;
                    }
                    int measuredHeight9 = (measuredHeight - voIPToggleButton.getMeasuredHeight()) / 2;
                    GroupCallActivity.this.soundButton.layout(measuredWidth, measuredHeight9, GroupCallActivity.this.soundButton.getMeasuredWidth() + measuredWidth, GroupCallActivity.this.soundButton.getMeasuredHeight() + measuredHeight9);
                } else {
                    int dp3 = GroupCallActivity.this.flipButton.getVisibility() == 0 ? AndroidUtilities.dp(28.0f) : 0;
                    int measuredWidth14 = (measuredWidth2 - GroupCallActivity.this.flipButton.getMeasuredWidth()) / 2;
                    int measuredHeight10 = (((measuredHeight - GroupCallActivity.this.flipButton.getMeasuredHeight()) / 2) + dp2) - dp3;
                    GroupCallActivity.this.flipButton.layout(measuredWidth14, measuredHeight10, GroupCallActivity.this.flipButton.getMeasuredWidth() + measuredWidth14, GroupCallActivity.this.flipButton.getMeasuredHeight() + measuredHeight10);
                    int measuredWidth15 = (measuredWidth2 - GroupCallActivity.this.cameraButton.getMeasuredWidth()) / 2;
                    int measuredHeight11 = ((measuredHeight - GroupCallActivity.this.cameraButton.getMeasuredHeight()) / 2) + dp2 + dp3;
                    GroupCallActivity.this.cameraButton.layout(measuredWidth15, measuredHeight11, GroupCallActivity.this.cameraButton.getMeasuredWidth() + measuredWidth15, GroupCallActivity.this.cameraButton.getMeasuredHeight() + measuredHeight11);
                }
                int measuredHeight12 = ((measuredHeight - GroupCallActivity.this.leaveButton.getMeasuredHeight()) / 2) + dp2;
                int measuredWidth16 = (getMeasuredWidth() - measuredWidth2) + ((measuredWidth2 - GroupCallActivity.this.leaveButton.getMeasuredWidth()) / 2);
                GroupCallActivity.this.leaveButton.layout(measuredWidth16, measuredHeight12, GroupCallActivity.this.leaveButton.getMeasuredWidth() + measuredWidth16, GroupCallActivity.this.leaveButton.getMeasuredHeight() + measuredHeight12);
                int measuredWidth17 = (getMeasuredWidth() - GroupCallActivity.this.muteButton.getMeasuredWidth()) / 2;
                int measuredHeight13 = ((measuredHeight - GroupCallActivity.this.muteButton.getMeasuredHeight()) / 2) - AndroidUtilities.dp(9.0f);
                GroupCallActivity.this.muteButton.layout(measuredWidth17, measuredHeight13, GroupCallActivity.this.muteButton.getMeasuredWidth() + measuredWidth17, GroupCallActivity.this.muteButton.getMeasuredHeight() + measuredHeight13);
                GroupCallActivity.this.minimizeButton.layout(measuredWidth17, measuredHeight13, GroupCallActivity.this.minimizeButton.getMeasuredWidth() + measuredWidth17, GroupCallActivity.this.minimizeButton.getMeasuredHeight() + measuredHeight13);
                GroupCallActivity.this.expandButton.layout(measuredWidth17, measuredHeight13, GroupCallActivity.this.expandButton.getMeasuredWidth() + measuredWidth17, GroupCallActivity.this.expandButton.getMeasuredHeight() + measuredHeight13);
                GroupCallActivity.this.muteButton.animate().setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).scaleX(1.0f).scaleY(1.0f).start();
                for (int i12 = 0; i12 < 2; i12++) {
                    int measuredWidth18 = (getMeasuredWidth() - GroupCallActivity.this.muteLabel[i12].getMeasuredWidth()) / 2;
                    int dp4 = (measuredHeight - AndroidUtilities.dp(12.0f)) - GroupCallActivity.this.muteLabel[i12].getMeasuredHeight();
                    GroupCallActivity.this.muteLabel[i12].layout(measuredWidth18, dp4, GroupCallActivity.this.muteLabel[i12].getMeasuredWidth() + measuredWidth18, GroupCallActivity.this.muteLabel[i12].getMeasuredHeight() + dp4);
                    GroupCallActivity.this.muteLabel[i12].animate().scaleX(1.0f).scaleY(1.0f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                }
            } else {
                int measuredWidth19 = getMeasuredWidth() / i6;
                boolean z2 = !GroupCallActivity.this.renderersContainer.inFullscreenMode && GroupCallActivity.this.isRtmpStream();
                int dp5 = z2 ? AndroidUtilities.dp(27.0f) : 0;
                if (GroupCallActivity.this.soundButton.getVisibility() == 0) {
                    int i13 = measuredWidth19 / 2;
                    int measuredWidth20 = i13 - (GroupCallActivity.this.cameraButton.getMeasuredWidth() / 2);
                    int measuredHeight14 = getMeasuredHeight() - GroupCallActivity.this.cameraButton.getMeasuredHeight();
                    GroupCallActivity.this.cameraButton.layout(measuredWidth20, measuredHeight14, GroupCallActivity.this.cameraButton.getMeasuredWidth() + measuredWidth20, GroupCallActivity.this.cameraButton.getMeasuredHeight() + measuredHeight14);
                    int measuredWidth21 = (i13 + (i6 == 4 ? measuredWidth19 : 0)) - (GroupCallActivity.this.leaveButton.getMeasuredWidth() / 2);
                    int measuredHeight15 = (getMeasuredHeight() - GroupCallActivity.this.soundButton.getMeasuredHeight()) - dp5;
                    GroupCallActivity.this.soundButton.layout(measuredWidth21, measuredHeight15, GroupCallActivity.this.soundButton.getMeasuredWidth() + measuredWidth21, GroupCallActivity.this.soundButton.getMeasuredHeight() + measuredHeight15);
                } else {
                    int i14 = measuredWidth19 / 2;
                    int measuredWidth22 = ((i6 == 4 ? measuredWidth19 : 0) + i14) - (GroupCallActivity.this.cameraButton.getMeasuredWidth() / 2);
                    int measuredHeight16 = getMeasuredHeight() - GroupCallActivity.this.cameraButton.getMeasuredHeight();
                    GroupCallActivity.this.cameraButton.layout(measuredWidth22, measuredHeight16, GroupCallActivity.this.cameraButton.getMeasuredWidth() + measuredWidth22, GroupCallActivity.this.cameraButton.getMeasuredHeight() + measuredHeight16);
                    int measuredWidth23 = i14 - (GroupCallActivity.this.flipButton.getMeasuredWidth() / 2);
                    int measuredHeight17 = getMeasuredHeight() - GroupCallActivity.this.flipButton.getMeasuredHeight();
                    GroupCallActivity.this.flipButton.layout(measuredWidth23, measuredHeight17, GroupCallActivity.this.flipButton.getMeasuredWidth() + measuredWidth23, GroupCallActivity.this.flipButton.getMeasuredHeight() + measuredHeight17);
                }
                int i15 = measuredWidth19 / 2;
                int measuredWidth24 = ((i6 == 4 ? measuredWidth19 * 3 : measuredWidth19 * 2) + i15) - (GroupCallActivity.this.leaveButton.getMeasuredWidth() / 2);
                int measuredHeight18 = (getMeasuredHeight() - GroupCallActivity.this.leaveButton.getMeasuredHeight()) - dp5;
                GroupCallActivity.this.leaveButton.layout(measuredWidth24, measuredHeight18, GroupCallActivity.this.leaveButton.getMeasuredWidth() + measuredWidth24, GroupCallActivity.this.leaveButton.getMeasuredHeight() + measuredHeight18);
                int measuredWidth25 = (i15 + (i6 == 4 ? measuredWidth19 * 2 : measuredWidth19)) - (GroupCallActivity.this.muteButton.getMeasuredWidth() / 2);
                int measuredHeight19 = ((getMeasuredHeight() - GroupCallActivity.this.leaveButton.getMeasuredHeight()) - ((GroupCallActivity.this.muteButton.getMeasuredWidth() - AndroidUtilities.dp(52.0f)) / 2)) - dp5;
                GroupCallActivity.this.muteButton.layout(measuredWidth25, measuredHeight19, GroupCallActivity.this.muteButton.getMeasuredWidth() + measuredWidth25, GroupCallActivity.this.muteButton.getMeasuredHeight() + measuredHeight19);
                GroupCallActivity.this.minimizeButton.layout(measuredWidth25, measuredHeight19, GroupCallActivity.this.minimizeButton.getMeasuredWidth() + measuredWidth25, GroupCallActivity.this.minimizeButton.getMeasuredHeight() + measuredHeight19);
                GroupCallActivity.this.expandButton.layout(measuredWidth25, measuredHeight19, GroupCallActivity.this.expandButton.getMeasuredWidth() + measuredWidth25, GroupCallActivity.this.expandButton.getMeasuredHeight() + measuredHeight19);
                float dp6 = AndroidUtilities.dp(52.0f) / (GroupCallActivity.this.muteButton.getMeasuredWidth() - AndroidUtilities.dp(8.0f));
                GroupCallActivity.this.muteButton.animate().scaleX(dp6).scaleY(dp6).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                for (int i16 = 0; i16 < 2; i16++) {
                    int measuredWidth26 = (i6 == 4 ? measuredWidth19 * 2 : measuredWidth19) + ((measuredWidth19 - GroupCallActivity.this.muteLabel[i16].getMeasuredWidth()) / 2);
                    int dp7 = measuredHeight - AndroidUtilities.dp(27.0f);
                    if (z2) {
                        dp7 -= AndroidUtilities.dp(GroupCallActivity.this.renderersContainer.inFullscreenMode ? 2.0f : 25.0f);
                    }
                    GroupCallActivity.this.muteLabel[i16].layout(measuredWidth26, dp7, GroupCallActivity.this.muteLabel[i16].getMeasuredWidth() + measuredWidth26, GroupCallActivity.this.muteLabel[i16].getMeasuredHeight() + dp7);
                    GroupCallActivity.this.muteLabel[i16].animate().scaleX(0.687f).scaleY(0.687f).setDuration(350L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                }
            }
            if (GroupCallActivity.this.animateButtonsOnNextLayout) {
                AnimatorSet animatorSet = new AnimatorSet();
                int i17 = 0;
                boolean z3 = false;
                while (i17 < getChildCount()) {
                    View childAt = getChildAt(i17);
                    Float f = (Float) GroupCallActivity.this.buttonsAnimationParamsX.get(childAt);
                    Float f2 = (Float) GroupCallActivity.this.buttonsAnimationParamsY.get(childAt);
                    if (f == null || f2 == null) {
                        i5 = 1;
                    } else {
                        Property property = FrameLayout.TRANSLATION_X;
                        float floatValue = f.floatValue() - childAt.getLeft();
                        i5 = 1;
                        animatorSet.playTogether(ObjectAnimator.ofFloat(childAt, (Property<View, Float>) property, floatValue, 0.0f));
                        animatorSet.playTogether(ObjectAnimator.ofFloat(childAt, (Property<View, Float>) FrameLayout.TRANSLATION_Y, f2.floatValue() - childAt.getTop(), 0.0f));
                        z3 = true;
                    }
                    i17 += i5;
                }
                if (z3) {
                    AnimatorSet animatorSet2 = this.currentButtonsAnimation;
                    if (animatorSet2 != null) {
                        animatorSet2.removeAllListeners();
                        this.currentButtonsAnimation.cancel();
                    }
                    this.currentButtonsAnimation = animatorSet;
                    animatorSet.setDuration(350L);
                    animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    animatorSet.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            AnonymousClass18.this.currentButtonsAnimation = null;
                            for (int i18 = 0; i18 < AnonymousClass18.this.getChildCount(); i18++) {
                                View childAt2 = AnonymousClass18.this.getChildAt(i18);
                                childAt2.setTranslationX(0.0f);
                                childAt2.setTranslationY(0.0f);
                            }
                        }
                    });
                    animatorSet.start();
                }
                GroupCallActivity.this.buttonsAnimationParamsX.clear();
                GroupCallActivity.this.buttonsAnimationParamsY.clear();
            }
            GroupCallActivity.this.animateButtonsOnNextLayout = false;
        }

        @Override
        protected void onMeasure(int i, int i2) {
            ViewGroup.LayoutParams layoutParams;
            int i3;
            if (!GroupCallActivity.isLandscapeMode) {
                i = View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(460.0f), View.MeasureSpec.getSize(i)), 1073741824);
            }
            for (int i4 = 0; i4 < 2; i4++) {
                if (!GroupCallActivity.isLandscapeMode || GroupCallActivity.isTabletMode) {
                    layoutParams = GroupCallActivity.this.muteLabel[i4].getLayoutParams();
                    i3 = -2;
                } else {
                    layoutParams = GroupCallActivity.this.muteLabel[i4].getLayoutParams();
                    i3 = (int) (View.MeasureSpec.getSize(i) / 0.68f);
                }
                layoutParams.width = i3;
            }
            super.onMeasure(i, i2);
        }
    }

    public class AnonymousClass2 implements Runnable {
        AnonymousClass2() {
        }

        public void lambda$run$0(int[] iArr) {
            GroupCallActivity.this.textureLightningView.setNewColors(iArr);
        }

        public void lambda$run$1(VoIPTextureView voIPTextureView) {
            try {
                Bitmap bitmap = voIPTextureView.blurRenderer.getBitmap(100, 100);
                if (bitmap == null) {
                    return;
                }
                final int[] colorsSync = DominantColors.getColorsSync(false, bitmap, true);
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        GroupCallActivity.AnonymousClass2.this.lambda$run$0(colorsSync);
                    }
                });
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override
        public void run() {
            GroupCallGridCell findGroupCallGridCell;
            GroupCallMiniTextureView renderer;
            final VoIPTextureView voIPTextureView;
            try {
                if (GroupCallActivity.this.renderersContainer == null || GroupCallActivity.this.renderersContainer.inFullscreenMode || (findGroupCallGridCell = GroupCallActivity.this.findGroupCallGridCell()) == null || (renderer = findGroupCallGridCell.getRenderer()) == null || (voIPTextureView = renderer.textureView) == null) {
                    return;
                }
                GroupCallActivity.updateTextureLightningQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        GroupCallActivity.AnonymousClass2.this.lambda$run$1(voIPTextureView);
                    }
                });
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public class AnonymousClass20 implements View.OnClickListener {
        Runnable finishRunnable = new Runnable() {
            @Override
            public void run() {
                GroupCallActivity.this.muteButton.setAnimation(GroupCallActivity.this.bigMicDrawable);
                GroupCallActivity.this.playingHandAnimation = false;
            }
        };

        AnonymousClass20() {
        }

        public void lambda$onClick$0() {
            GroupCallActivity.this.wasNotInLayoutFullscreen = null;
            GroupCallActivity.this.wasExpandBigSize = null;
            GroupCallActivity groupCallActivity = GroupCallActivity.this;
            groupCallActivity.updateMuteButton(groupCallActivity.muteButtonState, true);
        }

        public void lambda$onClick$1(TLObject tLObject, TLRPC.TL_error tL_error) {
            if (tLObject != null) {
                GroupCallActivity.this.accountInstance.getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
            }
        }

        public void lambda$onClick$2(TLObject tLObject, TLRPC.TL_error tL_error) {
            if (tLObject != null) {
                GroupCallActivity.this.accountInstance.getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
            }
        }

        @Override
        public void onClick(View view) {
            GroupCallActivity groupCallActivity;
            int checkSelfPermission;
            GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
            if (groupCallActivity2.call == null || groupCallActivity2.muteButtonState == 3) {
                return;
            }
            int i = 6;
            int i2 = 0;
            if (GroupCallActivity.this.isRtmpStream() && !GroupCallActivity.this.call.isScheduled()) {
                if (GroupCallActivity.this.renderersContainer != null && GroupCallActivity.this.renderersContainer.inFullscreenMode && (AndroidUtilities.isTablet() || GroupCallActivity.isLandscapeMode == GroupCallActivity.this.isRtmpLandscapeMode())) {
                    GroupCallActivity.this.fullscreenFor(null);
                    if (GroupCallActivity.isLandscapeMode) {
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public final void run() {
                                GroupCallActivity.AnonymousClass20.this.lambda$onClick$0();
                            }
                        }, 200L);
                    }
                    GroupCallActivity.this.parentActivity.setRequestedOrientation(-1);
                    return;
                }
                if (GroupCallActivity.this.visibleVideoParticipants.isEmpty()) {
                    return;
                }
                ChatObject.VideoParticipant videoParticipant = (ChatObject.VideoParticipant) GroupCallActivity.this.visibleVideoParticipants.get(0);
                if (AndroidUtilities.isTablet()) {
                    GroupCallActivity.this.fullscreenFor(videoParticipant);
                    return;
                }
                if (GroupCallActivity.isLandscapeMode == GroupCallActivity.this.isRtmpLandscapeMode()) {
                    GroupCallActivity.this.fullscreenFor(videoParticipant);
                }
                if (GroupCallActivity.this.isRtmpLandscapeMode()) {
                    GroupCallActivity.this.parentActivity.setRequestedOrientation(6);
                    return;
                } else {
                    GroupCallActivity.this.parentActivity.setRequestedOrientation(1);
                    return;
                }
            }
            if (GroupCallActivity.this.muteButtonState == 5) {
                if (GroupCallActivity.this.startingGroupCall) {
                    return;
                }
                try {
                    view.performHapticFeedback(3, 2);
                } catch (Exception unused) {
                }
                GroupCallActivity.this.startingGroupCall = true;
                TLRPC.TL_phone_startScheduledGroupCall tL_phone_startScheduledGroupCall = new TLRPC.TL_phone_startScheduledGroupCall();
                tL_phone_startScheduledGroupCall.call = GroupCallActivity.this.call.getInputGroupCall();
                GroupCallActivity.this.accountInstance.getConnectionsManager().sendRequest(tL_phone_startScheduledGroupCall, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        GroupCallActivity.AnonymousClass20.this.lambda$onClick$1(tLObject, tL_error);
                    }
                });
                return;
            }
            if (GroupCallActivity.this.muteButtonState == 7 || GroupCallActivity.this.muteButtonState == 6) {
                if (GroupCallActivity.this.muteButtonState == 6 && GroupCallActivity.this.reminderHintView != null) {
                    GroupCallActivity.this.reminderHintView.hide();
                }
                TLRPC.TL_phone_toggleGroupCallStartSubscription tL_phone_toggleGroupCallStartSubscription = new TLRPC.TL_phone_toggleGroupCallStartSubscription();
                tL_phone_toggleGroupCallStartSubscription.call = GroupCallActivity.this.call.getInputGroupCall();
                GroupCallActivity groupCallActivity3 = GroupCallActivity.this;
                TLRPC.GroupCall groupCall = groupCallActivity3.call.call;
                boolean z = !groupCall.schedule_start_subscribed;
                groupCall.schedule_start_subscribed = z;
                tL_phone_toggleGroupCallStartSubscription.subscribed = z;
                groupCallActivity3.accountInstance.getConnectionsManager().sendRequest(tL_phone_toggleGroupCallStartSubscription, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        GroupCallActivity.AnonymousClass20.this.lambda$onClick$2(tLObject, tL_error);
                    }
                });
                groupCallActivity = GroupCallActivity.this;
                if (groupCallActivity.call.call.schedule_start_subscribed) {
                    i = 7;
                }
            } else {
                if (VoIPService.getSharedInstance() == null || GroupCallActivity.this.isStillConnecting()) {
                    return;
                }
                i = 4;
                if (GroupCallActivity.this.muteButtonState != 2 && GroupCallActivity.this.muteButtonState != 4) {
                    if (GroupCallActivity.this.muteButtonState == 0) {
                        if (Build.VERSION.SDK_INT >= 23 && GroupCallActivity.this.getParentActivity() != null) {
                            checkSelfPermission = GroupCallActivity.this.getParentActivity().checkSelfPermission("android.permission.RECORD_AUDIO");
                            if (checkSelfPermission != 0) {
                                PermissionRequest.ensurePermission(R.raw.permission_request_microphone, R.string.VoipNeedMicPermissionWithHint, "android.permission.RECORD_AUDIO", new Utilities.Callback() {
                                    @Override
                                    public final void run(Object obj) {
                                        ((Boolean) obj).booleanValue();
                                    }
                                });
                                return;
                            }
                        }
                        GroupCallActivity.this.updateMuteButton(1, true);
                        VoIPService.getSharedInstance().setMicMute(false, false, true);
                    } else {
                        GroupCallActivity.this.updateMuteButton(0, true);
                        VoIPService.getSharedInstance().setMicMute(true, false, true);
                    }
                    try {
                        GroupCallActivity.this.muteButton.performHapticFeedback(3, 2);
                        return;
                    } catch (Exception unused2) {
                        return;
                    }
                }
                if (GroupCallActivity.this.playingHandAnimation) {
                    return;
                }
                GroupCallActivity.this.playingHandAnimation = true;
                AndroidUtilities.shakeView(GroupCallActivity.this.muteLabel[0]);
                try {
                    view.performHapticFeedback(3, 2);
                } catch (Exception unused3) {
                }
                int nextInt = Utilities.random.nextInt(100);
                int i3 = 120;
                if (nextInt >= 32) {
                    i2 = 240;
                    if (nextInt < 64) {
                        i3 = 240;
                        i2 = 120;
                    } else {
                        i3 = 420;
                        if (nextInt >= 97) {
                            i2 = 540;
                            if (nextInt == 98) {
                                i3 = 540;
                                i2 = 420;
                            } else {
                                i3 = 720;
                            }
                        }
                    }
                }
                GroupCallActivity.this.handDrawables.setCustomEndFrame(i3);
                GroupCallActivity.this.handDrawables.setOnFinishCallback(this.finishRunnable, i3 - 1);
                GroupCallActivity.this.muteButton.setAnimation(GroupCallActivity.this.handDrawables);
                GroupCallActivity.this.handDrawables.setCurrentFrame(i2);
                GroupCallActivity.this.muteButton.playAnimation();
                if (GroupCallActivity.this.muteButtonState != 2) {
                    return;
                }
                GroupCallActivity groupCallActivity4 = GroupCallActivity.this;
                long peerId = MessageObject.getPeerId(((TLRPC.TL_groupCallParticipant) groupCallActivity4.call.participants.get(MessageObject.getPeerId(groupCallActivity4.selfPeer))).peer);
                VoIPService.getSharedInstance().editCallMember(DialogObject.isUserDialog(peerId) ? GroupCallActivity.this.accountInstance.getMessagesController().getUser(Long.valueOf(peerId)) : GroupCallActivity.this.accountInstance.getMessagesController().getChat(Long.valueOf(-peerId)), null, null, null, Boolean.TRUE, null);
                groupCallActivity = GroupCallActivity.this;
            }
            groupCallActivity.updateMuteButton(i, true);
        }
    }

    public class AnonymousClass21 extends AudioPlayerAlert.ClippingTextViewSwitcher {
        final Context val$context;

        AnonymousClass21(Context context, Context context2) {
            super(context);
            this.val$context = context2;
        }

        public void lambda$createTextView$0(TextView textView, View view) {
            GroupCallActivity groupCallActivity = GroupCallActivity.this;
            ChatObject.Call call = groupCallActivity.call;
            if (call == null || !call.recording) {
                return;
            }
            groupCallActivity.showRecordHint(textView);
        }

        @Override
        protected TextView createTextView() {
            final TextView textView = new TextView(this.val$context);
            textView.setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
            textView.setTextSize(1, 20.0f);
            textView.setTypeface(AndroidUtilities.bold());
            textView.setGravity(51);
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    GroupCallActivity.AnonymousClass21.this.lambda$createTextView$0(textView, view);
                }
            });
            return textView;
        }
    }

    public class AnonymousClass29 extends GroupCallRenderersContainer {
        ValueAnimator uiVisibilityAnimator;

        AnonymousClass29(Context context, RecyclerView recyclerView, RecyclerView recyclerView2, ArrayList arrayList, ChatObject.Call call, GroupCallActivity groupCallActivity) {
            super(context, recyclerView, recyclerView2, arrayList, call, groupCallActivity);
        }

        public void lambda$onUiVisibilityChanged$0(ValueAnimator valueAnimator) {
            GroupCallActivity.this.progressToHideUi = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            GroupCallActivity.this.renderersContainer.setProgressToHideUi(GroupCallActivity.this.progressToHideUi);
            GroupCallActivity.this.fullscreenUsersListView.invalidate();
            ((BottomSheet) GroupCallActivity.this).containerView.invalidate();
            GroupCallActivity.this.buttonsContainer.invalidate();
        }

        @Override
        protected boolean canHideUI() {
            return super.canHideUI() && GroupCallActivity.this.previewDialog == null;
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (view == GroupCallActivity.this.scrimRenderer) {
                return true;
            }
            return super.drawChild(canvas, view, j);
        }

        @Override
        protected void onBackPressed() {
            GroupCallActivity.this.onBackPressed();
        }

        @Override
        protected void onFullScreenModeChanged(boolean z) {
            GroupCallActivity.this.delayedGroupCallUpdated = z;
            if (GroupCallActivity.isTabletMode) {
                if (z || !GroupCallActivity.this.renderersContainer.inFullscreenMode) {
                    return;
                }
                GroupCallActivity groupCallActivity = GroupCallActivity.this;
                groupCallActivity.tabletGridAdapter.setVisibility(groupCallActivity.tabletVideoGridView, false, true);
                return;
            }
            GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
            if (z) {
                groupCallActivity2.undoView[0].hide(false, 1);
                GroupCallActivity.this.renderersContainer.undoView[0].hide(false, 2);
                if (!GroupCallActivity.this.renderersContainer.inFullscreenMode) {
                    GroupCallActivity.this.listView.setVisibility(0);
                    GroupCallActivity.this.actionBar.setVisibility(0);
                    if (GroupCallActivity.this.watchersView != null) {
                        GroupCallActivity.this.watchersView.setVisibility(0);
                    }
                }
                GroupCallActivity.this.updateState(true, false);
                GroupCallActivity.this.buttonsContainer.requestLayout();
                if (GroupCallActivity.this.fullscreenUsersListView.getVisibility() != 0) {
                    GroupCallActivity.this.fullscreenUsersListView.setVisibility(0);
                    GroupCallActivity groupCallActivity3 = GroupCallActivity.this;
                    groupCallActivity3.fullscreenAdapter.setVisibility(groupCallActivity3.fullscreenUsersListView, true);
                    GroupCallActivity groupCallActivity4 = GroupCallActivity.this;
                    groupCallActivity4.fullscreenAdapter.update(false, groupCallActivity4.fullscreenUsersListView);
                } else {
                    GroupCallActivity groupCallActivity5 = GroupCallActivity.this;
                    groupCallActivity5.fullscreenAdapter.setVisibility(groupCallActivity5.fullscreenUsersListView, true);
                    GroupCallActivity.this.applyCallParticipantUpdates(true);
                }
            } else {
                if (groupCallActivity2.renderersContainer.inFullscreenMode) {
                    GroupCallActivity.this.actionBar.setVisibility(8);
                    GroupCallActivity.this.listView.setVisibility(8);
                    if (GroupCallActivity.this.watchersView != null) {
                        GroupCallActivity.this.watchersView.setVisibility(8);
                    }
                } else {
                    GroupCallActivity.this.fullscreenUsersListView.setVisibility(8);
                    GroupCallActivity groupCallActivity6 = GroupCallActivity.this;
                    groupCallActivity6.fullscreenAdapter.setVisibility(groupCallActivity6.fullscreenUsersListView, false);
                }
                if (GroupCallActivity.this.fullscreenUsersListView.getVisibility() == 0) {
                    for (int i = 0; i < GroupCallActivity.this.fullscreenUsersListView.getChildCount(); i++) {
                        View childAt = GroupCallActivity.this.fullscreenUsersListView.getChildAt(i);
                        childAt.setAlpha(1.0f);
                        childAt.setScaleX(1.0f);
                        childAt.setScaleY(1.0f);
                        childAt.setTranslationX(0.0f);
                        childAt.setTranslationY(0.0f);
                        ((GroupCallFullscreenAdapter.GroupCallUserCell) childAt).setProgressToFullscreen(GroupCallActivity.this.renderersContainer.progressToFullscreenMode);
                    }
                }
            }
            GroupCallActivity.this.buttonsBackgroundGradientView2.setVisibility(z ? 0 : 8);
            if (GroupCallActivity.this.delayedGroupCallUpdated) {
                return;
            }
            GroupCallActivity.this.applyCallParticipantUpdates(true);
        }

        @Override
        public void onUiVisibilityChanged() {
            if (GroupCallActivity.this.renderersContainer == null) {
                return;
            }
            final boolean isUiVisible = GroupCallActivity.this.renderersContainer.isUiVisible();
            ValueAnimator valueAnimator = this.uiVisibilityAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.uiVisibilityAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(GroupCallActivity.this.progressToHideUi, isUiVisible ? 0.0f : 1.0f);
            this.uiVisibilityAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    GroupCallActivity.AnonymousClass29.this.lambda$onUiVisibilityChanged$0(valueAnimator2);
                }
            });
            this.uiVisibilityAnimator.setDuration(350L);
            this.uiVisibilityAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.uiVisibilityAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    AnonymousClass29 anonymousClass29 = AnonymousClass29.this;
                    anonymousClass29.uiVisibilityAnimator = null;
                    GroupCallActivity groupCallActivity = GroupCallActivity.this;
                    groupCallActivity.progressToHideUi = isUiVisible ? 0.0f : 1.0f;
                    groupCallActivity.renderersContainer.setProgressToHideUi(GroupCallActivity.this.progressToHideUi);
                    GroupCallActivity.this.fullscreenUsersListView.invalidate();
                    ((BottomSheet) GroupCallActivity.this).containerView.invalidate();
                    GroupCallActivity.this.buttonsContainer.invalidate();
                }

                @Override
                public void onAnimationStart(Animator animator) {
                    GroupCallActivity.this.invalidateLayoutFullscreen();
                }
            });
            this.uiVisibilityAnimator.start();
        }

        @Override
        protected void update() {
            super.update();
            ((BottomSheet) GroupCallActivity.this).navBarColor = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_actionBarUnscrolled), Theme.getColor(Theme.key_voipgroup_actionBar), Math.max(GroupCallActivity.this.colorProgress, GroupCallActivity.this.renderersContainer == null ? 0.0f : GroupCallActivity.this.renderersContainer.progressToFullscreenMode), 1.0f);
            ((BottomSheet) GroupCallActivity.this).containerView.invalidate();
            GroupCallActivity groupCallActivity = GroupCallActivity.this;
            groupCallActivity.setColorProgress(groupCallActivity.colorProgress);
        }
    }

    public class AnonymousClass7 extends ActionBar.ActionBarMenuOnItemClick {
        final Context val$context;

        public class AnonymousClass1 extends GroupCallRecordAlert {
            AnonymousClass1(Context context, TLRPC.Chat chat, boolean z) {
                super(context, chat, z);
            }

            public static boolean lambda$onStartRecord$0(AlertDialog.Builder builder, TextView textView, int i, KeyEvent keyEvent) {
                AndroidUtilities.hideKeyboard(textView);
                builder.create().getButton(-1).callOnClick();
                return false;
            }

            public void lambda$onStartRecord$1(AlertDialog alertDialog, EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface) {
                GroupCallActivity.this.makeFocusable(null, alertDialog, editTextBoldCursor, true);
            }

            public void lambda$onStartRecord$3(EditTextBoldCursor editTextBoldCursor, int i, DialogInterface dialogInterface, int i2) {
                GroupCallActivity.this.call.toggleRecord(editTextBoldCursor.getText().toString(), i);
                AndroidUtilities.hideKeyboard(editTextBoldCursor);
                GroupCallActivity.this.getUndoView().showWithAction(0L, i == 0 ? 39 : 100, (Runnable) null);
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().playStartRecordSound();
                }
            }

            @Override
            public void onStartRecord(final int i) {
                int i2;
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setDialogButtonColorKey(Theme.key_voipgroup_listeningText);
                GroupCallActivity.this.enterEventSent = false;
                builder.setTitle(LocaleController.getString(R.string.VoipGroupStartRecordingTitle));
                GroupCallActivity groupCallActivity = GroupCallActivity.this;
                if (i == 0) {
                    i2 = groupCallActivity.call.call.rtmp_stream ? R.string.VoipGroupStartRecordingRtmpText : R.string.VoipGroupStartRecordingText;
                } else if (ChatObject.isChannelOrGiga(groupCallActivity.currentChat)) {
                    if (!GroupCallActivity.this.call.call.rtmp_stream) {
                        i2 = R.string.VoipChannelStartRecordingVideoText;
                    }
                    i2 = R.string.VoipGroupStartRecordingRtmpVideoText;
                } else {
                    if (!GroupCallActivity.this.call.call.rtmp_stream) {
                        i2 = R.string.VoipGroupStartRecordingVideoText;
                    }
                    i2 = R.string.VoipGroupStartRecordingRtmpVideoText;
                }
                builder.setMessage(LocaleController.getString(i2));
                builder.setCheckFocusable(false);
                final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(getContext());
                editTextBoldCursor.setBackgroundDrawable(Theme.createEditTextDrawable(getContext(), Theme.getColor(Theme.key_voipgroup_windowBackgroundWhiteInputField), Theme.getColor(Theme.key_voipgroup_windowBackgroundWhiteInputFieldActivated)));
                LinearLayout linearLayout = new LinearLayout(getContext());
                linearLayout.setOrientation(1);
                builder.setView(linearLayout);
                editTextBoldCursor.setTextSize(1, 16.0f);
                int i3 = Theme.key_voipgroup_nameText;
                editTextBoldCursor.setTextColor(Theme.getColor(i3));
                editTextBoldCursor.setMaxLines(1);
                editTextBoldCursor.setLines(1);
                editTextBoldCursor.setInputType(16385);
                editTextBoldCursor.setGravity(51);
                editTextBoldCursor.setSingleLine(true);
                editTextBoldCursor.setHint(LocaleController.getString(R.string.VoipGroupSaveFileHint));
                editTextBoldCursor.setImeOptions(6);
                editTextBoldCursor.setHintTextColor(Theme.getColor(Theme.key_voipgroup_lastSeenText));
                editTextBoldCursor.setCursorColor(Theme.getColor(i3));
                editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
                editTextBoldCursor.setCursorWidth(1.5f);
                editTextBoldCursor.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                linearLayout.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, 36, 51, 24, 0, 24, 12));
                editTextBoldCursor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public final boolean onEditorAction(TextView textView, int i4, KeyEvent keyEvent) {
                        boolean lambda$onStartRecord$0;
                        lambda$onStartRecord$0 = GroupCallActivity.AnonymousClass7.AnonymousClass1.lambda$onStartRecord$0(AlertDialog.Builder.this, textView, i4, keyEvent);
                        return lambda$onStartRecord$0;
                    }
                });
                final AlertDialog create = builder.create();
                create.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_inviteMembersBackground));
                create.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public final void onShow(DialogInterface dialogInterface) {
                        GroupCallActivity.AnonymousClass7.AnonymousClass1.this.lambda$onStartRecord$1(create, editTextBoldCursor, dialogInterface);
                    }
                });
                create.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public final void onDismiss(DialogInterface dialogInterface) {
                        AndroidUtilities.hideKeyboard(EditTextBoldCursor.this);
                    }
                });
                builder.setPositiveButton(LocaleController.getString(R.string.Start), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i4) {
                        GroupCallActivity.AnonymousClass7.AnonymousClass1.this.lambda$onStartRecord$3(editTextBoldCursor, i, dialogInterface, i4);
                    }
                });
                builder.setNegativeButton(LocaleController.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i4) {
                        AndroidUtilities.hideKeyboard(EditTextBoldCursor.this);
                    }
                });
                AlertDialog create2 = builder.create();
                create2.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_dialogBackground));
                create2.show();
                create2.setTextColor(Theme.getColor(i3));
                editTextBoldCursor.requestFocus();
            }
        }

        AnonymousClass7(Context context) {
            this.val$context = context;
        }

        public void lambda$onItemClick$0(TLObject tLObject, TLRPC.TL_error tL_error) {
            if (tLObject instanceof TLRPC.TL_updates) {
                GroupCallActivity.this.accountInstance.getMessagesController().processUpdates((TLRPC.TL_updates) tLObject, false);
            }
        }

        public void lambda$onItemClick$1(DialogInterface dialogInterface, int i) {
            if (GroupCallActivity.this.call.isScheduled()) {
                TLRPC.ChatFull chatFull = GroupCallActivity.this.accountInstance.getMessagesController().getChatFull(GroupCallActivity.this.currentChat.id);
                if (chatFull != null) {
                    chatFull.flags &= -2097153;
                    chatFull.call = null;
                    GroupCallActivity.this.accountInstance.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallUpdated, Long.valueOf(GroupCallActivity.this.currentChat.id), Long.valueOf(GroupCallActivity.this.call.call.id), Boolean.FALSE);
                }
                TLRPC.TL_phone_discardGroupCall tL_phone_discardGroupCall = new TLRPC.TL_phone_discardGroupCall();
                tL_phone_discardGroupCall.call = GroupCallActivity.this.call.getInputGroupCall();
                GroupCallActivity.this.accountInstance.getConnectionsManager().sendRequest(tL_phone_discardGroupCall, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        GroupCallActivity.AnonymousClass7.this.lambda$onItemClick$0(tLObject, tL_error);
                    }
                });
            } else if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().hangUp(1);
            }
            GroupCallActivity.this.dismiss();
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didStartedCall, new Object[0]);
        }

        public static void lambda$onItemClick$10(VoIPService voIPService, ArrayList arrayList, DialogInterface dialogInterface, int i) {
            if (VoIPService.getSharedInstance() == null) {
                return;
            }
            voIPService.setAudioOutput(((Integer) arrayList.get(i)).intValue());
        }

        public void lambda$onItemClick$2(boolean z, DialogInterface dialogInterface, int i) {
            GroupCallActivity.this.call.toggleRecord(null, 0);
            GroupCallActivity.this.getUndoView().showWithAction(0L, z ? 101 : 40, (Runnable) null);
        }

        public static boolean lambda$onItemClick$4(AlertDialog.Builder builder, TextView textView, int i, KeyEvent keyEvent) {
            AndroidUtilities.hideKeyboard(textView);
            builder.create().getButton(-1).callOnClick();
            return false;
        }

        public void lambda$onItemClick$5(EditTextBoldCursor editTextBoldCursor, AlertDialog.Builder builder, DialogInterface dialogInterface, int i) {
            AndroidUtilities.hideKeyboard(editTextBoldCursor);
            GroupCallActivity.this.call.setTitle(editTextBoldCursor.getText().toString());
            builder.getDismissRunnable().run();
        }

        public void lambda$onItemClick$6(AlertDialog alertDialog, EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface) {
            GroupCallActivity.this.makeFocusable(null, alertDialog, editTextBoldCursor, true);
        }

        public static void lambda$onItemClick$8(TLObject tLObject, TLRPC.TL_error tL_error) {
        }

        public void lambda$onItemClick$9(TLRPC.InputPeer inputPeer, boolean z, boolean z2, boolean z3) {
            TLObject chat;
            int i;
            int i2;
            GroupCallActivity groupCallActivity = GroupCallActivity.this;
            if (groupCallActivity.call == null) {
                return;
            }
            boolean z4 = inputPeer instanceof TLRPC.TL_inputPeerUser;
            if (z4) {
                chat = groupCallActivity.accountInstance.getMessagesController().getUser(Long.valueOf(inputPeer.user_id));
            } else {
                chat = groupCallActivity.accountInstance.getMessagesController().getChat(Long.valueOf(inputPeer instanceof TLRPC.TL_inputPeerChat ? inputPeer.chat_id : inputPeer.channel_id));
            }
            TLObject tLObject = chat;
            if (!GroupCallActivity.this.call.isScheduled()) {
                if (VoIPService.getSharedInstance() == null || !z) {
                    return;
                }
                GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
                VoIPService.getSharedInstance().setGroupCallPeer(inputPeer);
                GroupCallActivity.this.userSwitchObject = tLObject;
                return;
            }
            GroupCallActivity.this.getUndoView().showWithAction(0L, 37, tLObject, GroupCallActivity.this.currentChat, (Runnable) null, (Runnable) null);
            if (inputPeer instanceof TLRPC.TL_inputPeerChannel) {
                GroupCallActivity.this.selfPeer = new TLRPC.TL_peerChannel();
                GroupCallActivity.this.selfPeer.channel_id = inputPeer.channel_id;
            } else if (z4) {
                GroupCallActivity.this.selfPeer = new TLRPC.TL_peerUser();
                GroupCallActivity.this.selfPeer.user_id = inputPeer.user_id;
            } else if (inputPeer instanceof TLRPC.TL_inputPeerChat) {
                GroupCallActivity.this.selfPeer = new TLRPC.TL_peerChat();
                GroupCallActivity.this.selfPeer.chat_id = inputPeer.chat_id;
            }
            GroupCallActivity.this.schedulePeer = inputPeer;
            TLRPC.ChatFull chatFull = GroupCallActivity.this.accountInstance.getMessagesController().getChatFull(GroupCallActivity.this.currentChat.id);
            if (chatFull != null) {
                chatFull.groupcall_default_join_as = GroupCallActivity.this.selfPeer;
                if (chatFull instanceof TLRPC.TL_chatFull) {
                    i = chatFull.flags;
                    i2 = 32768;
                } else {
                    i = chatFull.flags;
                    i2 = 67108864;
                }
                chatFull.flags = i | i2;
            }
            TLRPC.TL_phone_saveDefaultGroupCallJoinAs tL_phone_saveDefaultGroupCallJoinAs = new TLRPC.TL_phone_saveDefaultGroupCallJoinAs();
            tL_phone_saveDefaultGroupCallJoinAs.peer = MessagesController.getInputPeer(GroupCallActivity.this.currentChat);
            tL_phone_saveDefaultGroupCallJoinAs.join_as = inputPeer;
            GroupCallActivity.this.accountInstance.getConnectionsManager().sendRequest(tL_phone_saveDefaultGroupCallJoinAs, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    GroupCallActivity.AnonymousClass7.lambda$onItemClick$8(tLObject2, tL_error);
                }
            });
            GroupCallActivity.this.updateItems();
        }

        @Override
        public void onItemClick(int i) {
            final VoIPService sharedInstance;
            int color;
            AlertDialog create;
            int i2;
            int i3;
            GroupCallActivity groupCallActivity;
            if (i == -1) {
                GroupCallActivity.this.onBackPressed();
                return;
            }
            if (i == 1) {
                groupCallActivity = GroupCallActivity.this;
                groupCallActivity.call.call.join_muted = false;
            } else {
                if (i != 2) {
                    if (i == 3) {
                        GroupCallActivity.this.getLink(false);
                        return;
                    }
                    if (i == 4) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(GroupCallActivity.this.getContext());
                        if (ChatObject.isChannelOrGiga(GroupCallActivity.this.currentChat)) {
                            builder.setTitle(LocaleController.getString(R.string.VoipChannelEndAlertTitle));
                            i3 = R.string.VoipChannelEndAlertText;
                        } else {
                            builder.setTitle(LocaleController.getString(R.string.VoipGroupEndAlertTitle));
                            i3 = R.string.VoipGroupEndAlertText;
                        }
                        builder.setMessage(LocaleController.getString(i3));
                        builder.setDialogButtonColorKey(Theme.key_voipgroup_listeningText);
                        builder.setPositiveButton(LocaleController.getString(R.string.VoipGroupEnd), new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(DialogInterface dialogInterface, int i4) {
                                GroupCallActivity.AnonymousClass7.this.lambda$onItemClick$1(dialogInterface, i4);
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                        create = builder.create();
                        create.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_dialogBackground));
                        create.show();
                        TextView textView = (TextView) create.getButton(-1);
                        if (textView != null) {
                            textView.setTextColor(Theme.getColor(Theme.key_voipgroup_leaveCallMenu));
                        }
                        i2 = Theme.key_voipgroup_actionBarItems;
                    } else {
                        if (i == 9) {
                            GroupCallActivity.this.screenShareItem.callOnClick();
                            return;
                        }
                        if (i != 5) {
                            if (i == 7) {
                                GroupCallActivity.this.changingPermissions = true;
                                GroupCallActivity.this.everyoneItem.setVisibility(0);
                                GroupCallActivity.this.adminItem.setVisibility(0);
                                GroupCallActivity.this.accountGap.setVisibility(8);
                                GroupCallActivity.this.inviteItem.setVisibility(8);
                                GroupCallActivity.this.leaveItem.setVisibility(8);
                                GroupCallActivity.this.permissionItem.setVisibility(8);
                                GroupCallActivity.this.editTitleItem.setVisibility(8);
                                GroupCallActivity.this.recordItem.setVisibility(8);
                                GroupCallActivity.this.screenItem.setVisibility(8);
                                GroupCallActivity.this.accountSelectCell.setVisibility(8);
                                GroupCallActivity.this.soundItem.setVisibility(8);
                                GroupCallActivity.this.noiseItem.setVisibility(8);
                                GroupCallActivity.this.otherItem.forceUpdatePopupPosition();
                                return;
                            }
                            if (i == 6) {
                                GroupCallActivity.this.enterEventSent = false;
                                final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(GroupCallActivity.this.getContext());
                                editTextBoldCursor.setBackgroundDrawable(Theme.createEditTextDrawable(GroupCallActivity.this.getContext(), true));
                                final AlertDialog.Builder builder2 = new AlertDialog.Builder(GroupCallActivity.this.getContext());
                                builder2.setDialogButtonColorKey(Theme.key_voipgroup_listeningText);
                                builder2.setTitle(LocaleController.getString(ChatObject.isChannelOrGiga(GroupCallActivity.this.currentChat) ? R.string.VoipChannelTitle : R.string.VoipGroupTitle));
                                builder2.setCheckFocusable(false);
                                builder2.setNegativeButton(LocaleController.getString(R.string.Cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public final void onClick(DialogInterface dialogInterface, int i4) {
                                        AndroidUtilities.hideKeyboard(EditTextBoldCursor.this);
                                    }
                                });
                                LinearLayout linearLayout = new LinearLayout(GroupCallActivity.this.getContext());
                                linearLayout.setOrientation(1);
                                builder2.setView(linearLayout);
                                editTextBoldCursor.setTextSize(1, 16.0f);
                                int i4 = Theme.key_voipgroup_nameText;
                                editTextBoldCursor.setTextColor(Theme.getColor(i4));
                                editTextBoldCursor.setMaxLines(1);
                                editTextBoldCursor.setLines(1);
                                editTextBoldCursor.setInputType(16385);
                                editTextBoldCursor.setGravity(51);
                                editTextBoldCursor.setSingleLine(true);
                                editTextBoldCursor.setImeOptions(6);
                                editTextBoldCursor.setHint(GroupCallActivity.this.currentChat.title);
                                editTextBoldCursor.setHintTextColor(Theme.getColor(Theme.key_voipgroup_lastSeenText));
                                editTextBoldCursor.setCursorColor(Theme.getColor(i4));
                                editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
                                editTextBoldCursor.setCursorWidth(1.5f);
                                editTextBoldCursor.setPadding(0, AndroidUtilities.dp(4.0f), 0, 0);
                                linearLayout.addView(editTextBoldCursor, LayoutHelper.createLinear(-1, 36, 51, 24, 6, 24, 0));
                                editTextBoldCursor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                    @Override
                                    public final boolean onEditorAction(TextView textView2, int i5, KeyEvent keyEvent) {
                                        boolean lambda$onItemClick$4;
                                        lambda$onItemClick$4 = GroupCallActivity.AnonymousClass7.lambda$onItemClick$4(AlertDialog.Builder.this, textView2, i5, keyEvent);
                                        return lambda$onItemClick$4;
                                    }
                                });
                                editTextBoldCursor.addTextChangedListener(new TextWatcher() {
                                    boolean ignoreTextChange;

                                    @Override
                                    public void afterTextChanged(Editable editable) {
                                        if (!this.ignoreTextChange && editable.length() > 40) {
                                            this.ignoreTextChange = true;
                                            editable.delete(40, editable.length());
                                            AndroidUtilities.shakeView(editTextBoldCursor);
                                            try {
                                                editTextBoldCursor.performHapticFeedback(3, 2);
                                            } catch (Exception unused) {
                                            }
                                            this.ignoreTextChange = false;
                                        }
                                    }

                                    @Override
                                    public void beforeTextChanged(CharSequence charSequence, int i5, int i6, int i7) {
                                    }

                                    @Override
                                    public void onTextChanged(CharSequence charSequence, int i5, int i6, int i7) {
                                    }
                                });
                                if (!TextUtils.isEmpty(GroupCallActivity.this.call.call.title)) {
                                    editTextBoldCursor.setText(GroupCallActivity.this.call.call.title);
                                    editTextBoldCursor.setSelection(editTextBoldCursor.length());
                                }
                                builder2.setPositiveButton(LocaleController.getString(R.string.Save), new DialogInterface.OnClickListener() {
                                    @Override
                                    public final void onClick(DialogInterface dialogInterface, int i5) {
                                        GroupCallActivity.AnonymousClass7.this.lambda$onItemClick$5(editTextBoldCursor, builder2, dialogInterface, i5);
                                    }
                                });
                                final AlertDialog create2 = builder2.create();
                                create2.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_inviteMembersBackground));
                                create2.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public final void onShow(DialogInterface dialogInterface) {
                                        GroupCallActivity.AnonymousClass7.this.lambda$onItemClick$6(create2, editTextBoldCursor, dialogInterface);
                                    }
                                });
                                create2.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public final void onDismiss(DialogInterface dialogInterface) {
                                        AndroidUtilities.hideKeyboard(EditTextBoldCursor.this);
                                    }
                                });
                                create2.show();
                                create2.setTextColor(Theme.getColor(i4));
                                editTextBoldCursor.requestFocus();
                                return;
                            }
                            if (i == 8) {
                                Context context = GroupCallActivity.this.getContext();
                                GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
                                JoinCallAlert.open(context, -groupCallActivity2.currentChat.id, groupCallActivity2.accountInstance, null, 2, GroupCallActivity.this.selfPeer, new JoinCallAlert.JoinCallAlertDelegate() {
                                    @Override
                                    public final void didSelectChat(TLRPC.InputPeer inputPeer, boolean z, boolean z2, boolean z3) {
                                        GroupCallActivity.AnonymousClass7.this.lambda$onItemClick$9(inputPeer, z, z2, z3);
                                    }
                                });
                                return;
                            }
                            if (i == 11) {
                                SharedConfig.toggleNoiseSupression();
                                VoIPService sharedInstance2 = VoIPService.getSharedInstance();
                                if (sharedInstance2 == null) {
                                    return;
                                }
                                sharedInstance2.setNoiseSupressionEnabled(SharedConfig.noiseSupression);
                                return;
                            }
                            if (i != 10 || (sharedInstance = VoIPService.getSharedInstance()) == null) {
                                return;
                            }
                            ArrayList arrayList = new ArrayList();
                            ArrayList arrayList2 = new ArrayList();
                            final ArrayList arrayList3 = new ArrayList();
                            arrayList.add(LocaleController.getString(R.string.VoipAudioRoutingSpeaker));
                            arrayList2.add(Integer.valueOf(R.drawable.msg_voice_speaker));
                            arrayList3.add(0);
                            if (sharedInstance.hasEarpiece()) {
                                arrayList.add(LocaleController.getString(sharedInstance.isHeadsetPlugged() ? R.string.VoipAudioRoutingHeadset : R.string.VoipAudioRoutingPhone));
                                arrayList2.add(Integer.valueOf(sharedInstance.isHeadsetPlugged() ? R.drawable.msg_voice_headphones : R.drawable.msg_voice_phone));
                                arrayList3.add(1);
                            }
                            if (sharedInstance.isBluetoothHeadsetConnected()) {
                                String str = sharedInstance.currentBluetoothDeviceName;
                                if (str == null) {
                                    str = LocaleController.getString(R.string.VoipAudioRoutingBluetooth);
                                }
                                arrayList.add(str);
                                arrayList2.add(Integer.valueOf(R.drawable.msg_voice_bluetooth));
                                arrayList3.add(2);
                            }
                            int size = arrayList.size();
                            CharSequence[] charSequenceArr = new CharSequence[size];
                            int[] iArr = new int[size];
                            for (int i5 = 0; i5 < size; i5++) {
                                charSequenceArr[i5] = (CharSequence) arrayList.get(i5);
                                iArr[i5] = ((Integer) arrayList2.get(i5)).intValue();
                            }
                            BottomSheet.Builder items = new BottomSheet.Builder(this.val$context).setTitle(LocaleController.getString(R.string.VoipSelectAudioOutput), true).setItems(charSequenceArr, iArr, new DialogInterface.OnClickListener() {
                                @Override
                                public final void onClick(DialogInterface dialogInterface, int i6) {
                                    GroupCallActivity.AnonymousClass7.lambda$onItemClick$10(VoIPService.this, arrayList3, dialogInterface, i6);
                                }
                            });
                            BottomSheet create3 = items.create();
                            int i6 = Theme.key_voipgroup_listViewBackgroundUnscrolled;
                            create3.setBackgroundColor(Theme.getColor(i6));
                            create3.fixNavigationBar(Theme.getColor(i6));
                            int i7 = sharedInstance.getCurrentAudioRoute() == 1 ? 0 : sharedInstance.getCurrentAudioRoute() == 0 ? 1 : 2;
                            items.show();
                            create3.setTitleColor(Theme.getColor(Theme.key_voipgroup_nameText));
                            for (int i8 = 0; i8 < create3.getItemViews().size(); i8++) {
                                BottomSheet.BottomSheetCell bottomSheetCell = create3.getItemViews().get(i8);
                                if (i8 == i7) {
                                    color = Theme.getColor(Theme.key_voipgroup_listeningText);
                                    bottomSheetCell.isSelected = true;
                                } else {
                                    color = Theme.getColor(Theme.key_voipgroup_nameText);
                                }
                                bottomSheetCell.setTextColor(color);
                                bottomSheetCell.setIconColor(color);
                                bottomSheetCell.setBackground(Theme.createSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_actionBarItems), 12), 2));
                            }
                            return;
                        }
                        GroupCallActivity groupCallActivity3 = GroupCallActivity.this;
                        ChatObject.Call call = groupCallActivity3.call;
                        if (!call.recording) {
                            Context context2 = GroupCallActivity.this.getContext();
                            GroupCallActivity groupCallActivity4 = GroupCallActivity.this;
                            AnonymousClass1 anonymousClass1 = new AnonymousClass1(context2, groupCallActivity4.currentChat, groupCallActivity4.hasVideo);
                            if (GroupCallActivity.this.isRtmpStream()) {
                                anonymousClass1.onStartRecord(2);
                                return;
                            } else {
                                anonymousClass1.show();
                                return;
                            }
                        }
                        final boolean z = call.call.record_video_active;
                        AlertDialog.Builder builder3 = new AlertDialog.Builder(groupCallActivity3.getContext());
                        builder3.setDialogButtonColorKey(Theme.key_voipgroup_listeningText);
                        builder3.setTitle(LocaleController.getString(R.string.VoipGroupStopRecordingTitle));
                        builder3.setMessage(LocaleController.getString(ChatObject.isChannelOrGiga(GroupCallActivity.this.currentChat) ? R.string.VoipChannelStopRecordingText : R.string.VoipGroupStopRecordingText));
                        builder3.setPositiveButton(LocaleController.getString(R.string.Stop), new DialogInterface.OnClickListener() {
                            @Override
                            public final void onClick(DialogInterface dialogInterface, int i9) {
                                GroupCallActivity.AnonymousClass7.this.lambda$onItemClick$2(z, dialogInterface, i9);
                            }
                        });
                        builder3.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                        create = builder3.create();
                        create.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_dialogBackground));
                        create.show();
                        i2 = Theme.key_voipgroup_nameText;
                    }
                    create.setTextColor(Theme.getColor(i2));
                    return;
                }
                groupCallActivity = GroupCallActivity.this;
                groupCallActivity.call.call.join_muted = true;
            }
            groupCallActivity.toggleAdminSpeak();
        }
    }

    public class AvatarUpdaterDelegate implements ImageUpdater.ImageUpdaterDelegate {
        private TLRPC.FileLocation avatar;
        private TLRPC.FileLocation avatarBig;
        private final long peerId;
        private ImageLocation uploadingImageLocation;
        public float uploadingProgress;

        private AvatarUpdaterDelegate(long j) {
            this.peerId = j;
        }

        public void lambda$didUploadPhoto$0(TLRPC.TL_error tL_error, TLObject tLObject, String str) {
            if (this.uploadingImageLocation != null) {
                GroupCallActivity.this.avatarsViewPager.removeUploadingImage(this.uploadingImageLocation);
                this.uploadingImageLocation = null;
            }
            if (tL_error == null) {
                TLRPC.User user = GroupCallActivity.this.accountInstance.getMessagesController().getUser(Long.valueOf(GroupCallActivity.this.accountInstance.getUserConfig().getClientUserId()));
                if (user == null) {
                    user = GroupCallActivity.this.accountInstance.getUserConfig().getCurrentUser();
                    if (user == null) {
                        return;
                    } else {
                        GroupCallActivity.this.accountInstance.getMessagesController().putUser(user, false);
                    }
                } else {
                    GroupCallActivity.this.accountInstance.getUserConfig().setCurrentUser(user);
                }
                TLRPC.TL_photos_photo tL_photos_photo = (TLRPC.TL_photos_photo) tLObject;
                ArrayList<TLRPC.PhotoSize> arrayList = tL_photos_photo.photo.sizes;
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 150);
                TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 800);
                TLRPC.VideoSize videoSize = tL_photos_photo.photo.video_sizes.isEmpty() ? null : tL_photos_photo.photo.video_sizes.get(0);
                TLRPC.TL_userProfilePhoto tL_userProfilePhoto = new TLRPC.TL_userProfilePhoto();
                user.photo = tL_userProfilePhoto;
                tL_userProfilePhoto.photo_id = tL_photos_photo.photo.id;
                if (closestPhotoSizeWithSize != null) {
                    tL_userProfilePhoto.photo_small = closestPhotoSizeWithSize.location;
                }
                if (closestPhotoSizeWithSize2 != null) {
                    tL_userProfilePhoto.photo_big = closestPhotoSizeWithSize2.location;
                }
                if (closestPhotoSizeWithSize != null && this.avatar != null) {
                    FileLoader.getInstance(((BottomSheet) GroupCallActivity.this).currentAccount).getPathToAttach(this.avatar, true).renameTo(FileLoader.getInstance(((BottomSheet) GroupCallActivity.this).currentAccount).getPathToAttach(closestPhotoSizeWithSize, true));
                    ImageLoader.getInstance().replaceImageInCache(this.avatar.volume_id + "_" + this.avatar.local_id + "@50_50", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@50_50", ImageLocation.getForUser(user, 1), false);
                }
                if (closestPhotoSizeWithSize2 != null && this.avatarBig != null) {
                    FileLoader.getInstance(((BottomSheet) GroupCallActivity.this).currentAccount).getPathToAttach(this.avatarBig, true).renameTo(FileLoader.getInstance(((BottomSheet) GroupCallActivity.this).currentAccount).getPathToAttach(closestPhotoSizeWithSize2, true));
                }
                if (videoSize != null && str != null) {
                    new File(str).renameTo(FileLoader.getInstance(((BottomSheet) GroupCallActivity.this).currentAccount).getPathToAttach(videoSize, "mp4", true));
                }
                GroupCallActivity.this.accountInstance.getMessagesController().getDialogPhotos(user.id).reset();
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(user);
                GroupCallActivity.this.accountInstance.getMessagesStorage().putUsersAndChats(arrayList2, null, false, true);
                TLRPC.User user2 = GroupCallActivity.this.accountInstance.getMessagesController().getUser(Long.valueOf(this.peerId));
                ImageLocation forUser = ImageLocation.getForUser(user2, 0);
                ImageLocation forUser2 = ImageLocation.getForUser(user2, 1);
                if (ImageLocation.getForLocal(this.avatarBig) == null) {
                    forUser2 = ImageLocation.getForLocal(this.avatar);
                }
                GroupCallActivity.this.avatarsViewPager.setCreateThumbFromParent(false);
                GroupCallActivity.this.avatarsViewPager.initIfEmpty(null, forUser, forUser2, true);
                this.avatar = null;
                this.avatarBig = null;
                AndroidUtilities.updateVisibleRows(GroupCallActivity.this.listView);
                updateAvatarUploadingProgress(1.0f);
            }
            GroupCallActivity.this.accountInstance.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
            GroupCallActivity.this.accountInstance.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
            GroupCallActivity.this.accountInstance.getUserConfig().saveConfig(true);
        }

        public void lambda$didUploadPhoto$1(final String str, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    GroupCallActivity.AvatarUpdaterDelegate.this.lambda$didUploadPhoto$0(tL_error, tLObject, str);
                }
            });
        }

        public void lambda$didUploadPhoto$2() {
            if (this.uploadingImageLocation != null) {
                GroupCallActivity.this.avatarsViewPager.removeUploadingImage(this.uploadingImageLocation);
                this.uploadingImageLocation = null;
            }
            TLRPC.Chat chat = GroupCallActivity.this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.peerId));
            ImageLocation forChat = ImageLocation.getForChat(chat, 0);
            ImageLocation forChat2 = ImageLocation.getForChat(chat, 1);
            if (ImageLocation.getForLocal(this.avatarBig) == null) {
                forChat2 = ImageLocation.getForLocal(this.avatar);
            }
            GroupCallActivity.this.avatarsViewPager.setCreateThumbFromParent(false);
            GroupCallActivity.this.avatarsViewPager.initIfEmpty(null, forChat, forChat2, true);
            this.avatar = null;
            this.avatarBig = null;
            AndroidUtilities.updateVisibleRows(GroupCallActivity.this.listView);
            updateAvatarUploadingProgress(1.0f);
        }

        public void lambda$didUploadPhoto$3(TLRPC.InputFile inputFile, TLRPC.InputFile inputFile2, TLRPC.VideoSize videoSize, double d, final String str, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2) {
            if (inputFile == null && inputFile2 == null && videoSize == null) {
                this.avatar = photoSize.location;
                TLRPC.FileLocation fileLocation = photoSize2.location;
                this.avatarBig = fileLocation;
                this.uploadingImageLocation = ImageLocation.getForLocal(fileLocation);
                GroupCallActivity.this.avatarsViewPager.addUploadingImage(this.uploadingImageLocation, ImageLocation.getForLocal(this.avatar));
                AndroidUtilities.updateVisibleRows(GroupCallActivity.this.listView);
                return;
            }
            if (this.peerId <= 0) {
                GroupCallActivity.this.accountInstance.getMessagesController().changeChatAvatar(-this.peerId, null, inputFile, inputFile2, videoSize, d, str, photoSize.location, photoSize2.location, new Runnable() {
                    @Override
                    public final void run() {
                        GroupCallActivity.AvatarUpdaterDelegate.this.lambda$didUploadPhoto$2();
                    }
                });
                return;
            }
            TLRPC.TL_photos_uploadProfilePhoto tL_photos_uploadProfilePhoto = new TLRPC.TL_photos_uploadProfilePhoto();
            if (inputFile != null) {
                tL_photos_uploadProfilePhoto.file = inputFile;
                tL_photos_uploadProfilePhoto.flags |= 1;
            }
            if (inputFile2 != null) {
                tL_photos_uploadProfilePhoto.video = inputFile2;
                int i = tL_photos_uploadProfilePhoto.flags;
                tL_photos_uploadProfilePhoto.video_start_ts = d;
                tL_photos_uploadProfilePhoto.flags = i | 6;
            }
            if (videoSize != null) {
                tL_photos_uploadProfilePhoto.video_emoji_markup = videoSize;
                tL_photos_uploadProfilePhoto.flags |= 16;
            }
            GroupCallActivity.this.accountInstance.getConnectionsManager().sendRequest(tL_photos_uploadProfilePhoto, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    GroupCallActivity.AvatarUpdaterDelegate.this.lambda$didUploadPhoto$1(str, tLObject, tL_error);
                }
            });
        }

        @Override
        public boolean canFinishFragment() {
            return ImageUpdater.ImageUpdaterDelegate.CC.$default$canFinishFragment(this);
        }

        @Override
        public void didStartUpload(boolean z) {
        }

        @Override
        public void didUploadFailed() {
            ImageUpdater.ImageUpdaterDelegate.CC.$default$didUploadFailed(this);
        }

        @Override
        public void didUploadPhoto(final TLRPC.InputFile inputFile, final TLRPC.InputFile inputFile2, final double d, final String str, final TLRPC.PhotoSize photoSize, final TLRPC.PhotoSize photoSize2, boolean z, final TLRPC.VideoSize videoSize) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    GroupCallActivity.AvatarUpdaterDelegate.this.lambda$didUploadPhoto$3(inputFile, inputFile2, videoSize, d, str, photoSize2, photoSize);
                }
            });
        }

        @Override
        public String getInitialSearchString() {
            return ImageUpdater.ImageUpdaterDelegate.CC.$default$getInitialSearchString(this);
        }

        @Override
        public void onUploadProgressChanged(float f) {
            GroupCallActivity.this.avatarsViewPager.setUploadProgress(this.uploadingImageLocation, f);
            updateAvatarUploadingProgress(f);
        }

        public void updateAvatarUploadingProgress(float f) {
            this.uploadingProgress = f;
            if (GroupCallActivity.this.listView == null) {
                return;
            }
            for (int i = 0; i < GroupCallActivity.this.listView.getChildCount(); i++) {
                View childAt = GroupCallActivity.this.listView.getChildAt(i);
                if (childAt instanceof GroupCallUserCell) {
                    GroupCallUserCell groupCallUserCell = (GroupCallUserCell) childAt;
                    if (groupCallUserCell.isSelfUser()) {
                        groupCallUserCell.setUploadProgress(f, true);
                    }
                }
            }
        }
    }

    public class GroupCallItemAnimator extends DefaultItemAnimator {
        HashSet addingHolders;
        public float animationProgress;
        public ValueAnimator animator;
        float outMaxBottom;
        float outMinTop;
        HashSet removingHolders;

        private GroupCallItemAnimator() {
            this.addingHolders = new HashSet();
            this.removingHolders = new HashSet();
        }

        public void lambda$runPendingAnimations$0(ValueAnimator valueAnimator) {
            this.animationProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            GroupCallActivity.this.listView.invalidate();
            GroupCallActivity.this.renderersContainer.invalidate();
            ((BottomSheet) GroupCallActivity.this).containerView.invalidate();
            GroupCallActivity.this.updateLayout(true);
        }

        @Override
        public void endAnimations() {
            super.endAnimations();
            this.removingHolders.clear();
            this.addingHolders.clear();
            this.outMinTop = Float.MAX_VALUE;
            GroupCallActivity.this.listView.invalidate();
        }

        @Override
        public void runPendingAnimations() {
            boolean z = !this.mPendingRemovals.isEmpty();
            boolean z2 = !this.mPendingMoves.isEmpty();
            boolean z3 = !this.mPendingAdditions.isEmpty();
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.animator = null;
            }
            if (z || z2 || z3) {
                this.animationProgress = 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.animator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        GroupCallActivity.GroupCallItemAnimator.this.lambda$runPendingAnimations$0(valueAnimator2);
                    }
                });
                this.animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        super.onAnimationEnd(animator);
                        GroupCallItemAnimator groupCallItemAnimator = GroupCallItemAnimator.this;
                        groupCallItemAnimator.animator = null;
                        GroupCallActivity.this.listView.invalidate();
                        GroupCallActivity.this.renderersContainer.invalidate();
                        ((BottomSheet) GroupCallActivity.this).containerView.invalidate();
                        GroupCallActivity.this.updateLayout(true);
                        GroupCallItemAnimator.this.addingHolders.clear();
                        GroupCallItemAnimator.this.removingHolders.clear();
                    }
                });
                this.animator.setDuration(350L);
                this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.animator.start();
                GroupCallActivity.this.listView.invalidate();
                GroupCallActivity.this.renderersContainer.invalidate();
            }
            super.runPendingAnimations();
        }

        public void updateBackgroundBeforeAnimation() {
            if (this.animator != null) {
                return;
            }
            this.addingHolders.clear();
            this.addingHolders.addAll(this.mPendingAdditions);
            this.removingHolders.clear();
            this.removingHolders.addAll(this.mPendingRemovals);
            this.outMaxBottom = 0.0f;
            this.outMinTop = Float.MAX_VALUE;
            if (this.addingHolders.isEmpty() && this.removingHolders.isEmpty()) {
                return;
            }
            int childCount = GroupCallActivity.this.listView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = GroupCallActivity.this.listView.getChildAt(i);
                RecyclerView.ViewHolder findContainingViewHolder = GroupCallActivity.this.listView.findContainingViewHolder(childAt);
                if (findContainingViewHolder != null && findContainingViewHolder.getItemViewType() != 3 && findContainingViewHolder.getItemViewType() != 4 && findContainingViewHolder.getItemViewType() != 5 && !this.addingHolders.contains(findContainingViewHolder)) {
                    this.outMaxBottom = Math.max(this.outMaxBottom, childAt.getY() + childAt.getMeasuredHeight());
                    this.outMinTop = Math.min(this.outMinTop, Math.max(0.0f, childAt.getY()));
                }
            }
            this.animationProgress = 0.0f;
            GroupCallActivity.this.listView.invalidate();
        }
    }

    public class LightningView extends View {
        private int[] currentColors;
        private final Paint paint;
        private int parentBackgroundColor;
        private float shadowOffset;

        public LightningView(Context context) {
            super(context);
            Paint paint = new Paint(1);
            this.paint = paint;
            paint.setStyle(Paint.Style.FILL);
            paint.setAlpha(0);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!GroupCallActivity.this.needTextureLightning || GroupCallActivity.this.renderersContainer.progressToFullscreenMode >= 0.1d) {
                return;
            }
            canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), this.paint);
        }

        public void setNewColors(int[] iArr) {
            int[] iArr2 = this.currentColors;
            boolean z = true;
            boolean z2 = false;
            if (iArr2 == null || iArr[0] != iArr2[0] || iArr[1] != iArr2[1]) {
                if (iArr2 == null) {
                    this.paint.setAlpha(255);
                }
                this.currentColors = iArr;
                if (GroupCallActivity.this.liveLabelBgColorAnimator != null && GroupCallActivity.this.textureLightningViewAnimatingAlpha != 1.0f) {
                    int[] iArr3 = this.currentColors;
                    iArr3[0] = ColorUtils.setAlphaComponent(iArr3[0], (int) (GroupCallActivity.this.textureLightningViewAnimatingAlpha * 255.0f));
                    int[] iArr4 = this.currentColors;
                    iArr4[1] = ColorUtils.setAlphaComponent(iArr4[1], (int) (GroupCallActivity.this.textureLightningViewAnimatingAlpha * 255.0f));
                }
                this.paint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, GroupCallActivity.this.textureLightningView.getMeasuredHeight(), this.currentColors, (float[]) null, Shader.TileMode.CLAMP));
                z2 = true;
            }
            if (this.parentBackgroundColor != GroupCallActivity.this.backgroundColor) {
                this.paint.setShadowLayer(AndroidUtilities.dp(36.0f), 0.0f, this.shadowOffset, GroupCallActivity.this.backgroundColor);
                this.parentBackgroundColor = GroupCallActivity.this.backgroundColor;
            } else {
                z = z2;
            }
            if (z) {
                invalidate();
            }
            GroupCallActivity.this.runUpdateTextureLightningRunnable();
        }

        public void setShadowOffset(int i) {
            float f = i;
            if (this.shadowOffset != f) {
                this.paint.setShadowLayer(AndroidUtilities.dp(36.0f), 0.0f, this.shadowOffset, GroupCallActivity.this.backgroundColor);
                this.shadowOffset = f;
                invalidate();
            }
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private int addMemberRow;
        private boolean hasSelfUser;
        private int invitedEndRow;
        private int invitedStartRow;
        private int lastRow;
        private Context mContext;
        private int rowsCount;
        private int usersEndRow;
        private int usersStartRow;
        private int usersVideoGridEndRow;
        private int usersVideoGridStartRow;
        private int videoGridDividerRow;
        private int videoNotAvailableRow;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public void updateRows() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.ListAdapter.updateRows():void");
        }

        public boolean addSelfToCounter() {
            if (GroupCallActivity.this.isRtmpStream() || this.hasSelfUser || VoIPService.getSharedInstance() == null) {
                return false;
            }
            return !VoIPService.getSharedInstance().isJoined();
        }

        @Override
        public int getItemCount() {
            return this.rowsCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == this.lastRow) {
                return 3;
            }
            if (i == this.addMemberRow) {
                return 0;
            }
            if (i == this.videoGridDividerRow) {
                return 5;
            }
            if (i >= this.usersStartRow && i < this.usersEndRow) {
                return 1;
            }
            if (i < this.usersVideoGridStartRow || i >= this.usersVideoGridEndRow) {
                return i == this.videoNotAvailableRow ? 6 : 2;
            }
            return 4;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return (itemViewType == 3 || itemViewType == 4 || itemViewType == 5 || itemViewType == 6) ? false : true;
        }

        @Override
        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        @Override
        public void notifyItemChanged(int i) {
            updateRows();
            super.notifyItemChanged(i);
        }

        @Override
        public void notifyItemMoved(int i, int i2) {
            updateRows();
            super.notifyItemMoved(i, i2);
        }

        @Override
        public void notifyItemRangeChanged(int i, int i2) {
            updateRows();
            super.notifyItemRangeChanged(i, i2);
        }

        @Override
        public void notifyItemRangeChanged(int i, int i2, Object obj) {
            updateRows();
            super.notifyItemRangeChanged(i, i2, obj);
        }

        @Override
        public void notifyItemRangeInserted(int i, int i2) {
            updateRows();
            super.notifyItemRangeInserted(i, i2);
        }

        @Override
        public void notifyItemRangeRemoved(int i, int i2) {
            updateRows();
            super.notifyItemRangeRemoved(i, i2);
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r17, int r18) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            GroupCallGridCell groupCallGridCell;
            boolean z = false;
            if (i == 0) {
                view = new GroupCallTextCell(this.mContext) {
                    @Override
                    protected void onMeasure(int i2, int i3) {
                        if (AndroidUtilities.isTablet()) {
                            i2 = View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(420.0f), View.MeasureSpec.getSize(i2)), 1073741824);
                        }
                        super.onMeasure(i2, i3);
                    }
                };
            } else if (i == 1) {
                view = new GroupCallUserCell(this.mContext) {
                    @Override
                    protected void onMeasure(int i2, int i3) {
                        if (AndroidUtilities.isTablet()) {
                            i2 = View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(420.0f), View.MeasureSpec.getSize(i2)), 1073741824);
                        }
                        super.onMeasure(i2, i3);
                    }

                    @Override
                    protected void lambda$new$5(GroupCallUserCell groupCallUserCell) {
                        GroupCallActivity.this.showMenuForCell(groupCallUserCell);
                    }
                };
            } else if (i != 2) {
                if (i == 4) {
                    groupCallGridCell = new GroupCallGridCell(this.mContext, z) {
                        @Override
                        protected void onAttachedToWindow() {
                            super.onAttachedToWindow();
                            if (GroupCallActivity.this.listView.getVisibility() == 0 && GroupCallActivity.this.listViewVideoVisibility) {
                                GroupCallActivity.this.attachRenderer(this, true);
                            }
                        }

                        @Override
                        protected void onDetachedFromWindow() {
                            super.onDetachedFromWindow();
                            GroupCallActivity.this.attachRenderer(this, false);
                        }
                    };
                } else if (i == 5) {
                    view = new View(this.mContext) {
                        @Override
                        protected void onMeasure(int i2, int i3) {
                            super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(GroupCallActivity.isLandscapeMode ? 0.0f : 8.0f), 1073741824));
                        }
                    };
                } else if (i != 6) {
                    view = new View(this.mContext);
                } else {
                    TextView textView = new TextView(this.mContext);
                    textView.setTextColor(-8682615);
                    textView.setTextSize(1, 13.0f);
                    textView.setGravity(1);
                    textView.setPadding(0, 0, 0, AndroidUtilities.dp(10.0f));
                    textView.setText(ChatObject.isChannelOrGiga(GroupCallActivity.this.currentChat) ? LocaleController.formatString("VoipChannelVideoNotAvailableAdmin", R.string.VoipChannelVideoNotAvailableAdmin, LocaleController.formatPluralString("Participants", GroupCallActivity.this.accountInstance.getMessagesController().groupCallVideoMaxParticipants, new Object[0])) : LocaleController.formatString("VoipVideoNotAvailableAdmin", R.string.VoipVideoNotAvailableAdmin, LocaleController.formatPluralString("Members", GroupCallActivity.this.accountInstance.getMessagesController().groupCallVideoMaxParticipants, new Object[0])));
                    groupCallGridCell = textView;
                }
                view = groupCallGridCell;
            } else {
                view = new GroupCallInvitedCell(this.mContext) {
                    @Override
                    protected void onMeasure(int i2, int i3) {
                        if (AndroidUtilities.isTablet()) {
                            i2 = View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(420.0f), View.MeasureSpec.getSize(i2)), 1073741824);
                        }
                        super.onMeasure(i2, i3);
                    }
                };
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 1) {
                GroupCallUserCell groupCallUserCell = (GroupCallUserCell) viewHolder.itemView;
                int i = GroupCallActivity.this.actionBar.getTag() != null ? Theme.key_voipgroup_mutedIcon : Theme.key_voipgroup_mutedIconUnscrolled;
                groupCallUserCell.setGrayIconColor(i, Theme.getColor(i));
                groupCallUserCell.setDrawDivider(viewHolder.getAdapterPosition() != getItemCount() - 2);
                return;
            }
            if (itemViewType == 2) {
                GroupCallInvitedCell groupCallInvitedCell = (GroupCallInvitedCell) viewHolder.itemView;
                int i2 = GroupCallActivity.this.actionBar.getTag() != null ? Theme.key_voipgroup_mutedIcon : Theme.key_voipgroup_mutedIconUnscrolled;
                groupCallInvitedCell.setGrayIconColor(i2, Theme.getColor(i2));
                groupCallInvitedCell.setDrawDivider(viewHolder.getAdapterPosition() != getItemCount() - 2);
            }
        }
    }

    public static class RecordCallDrawable extends Drawable {
        private long lastUpdateTime;
        private View parentView;
        private boolean recording;
        private int state;
        private Paint paint = new Paint(1);
        private Paint paint2 = new Paint(1);
        private float alpha = 1.0f;

        public RecordCallDrawable() {
            this.paint.setColor(-1);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth(AndroidUtilities.dp(1.5f));
        }

        @Override
        public void draw(Canvas canvas) {
            float centerX = getBounds().centerX();
            float centerY = getBounds().centerY();
            canvas.drawCircle(centerX, centerY, AndroidUtilities.dp(10.0f), this.paint);
            this.paint2.setColor(this.recording ? -1147527 : -1);
            this.paint2.setAlpha((int) (this.alpha * 255.0f));
            canvas.drawCircle(centerX, centerY, AndroidUtilities.dp(5.0f), this.paint2);
            if (this.recording) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long j = elapsedRealtime - this.lastUpdateTime;
                if (j > 17) {
                    j = 17;
                }
                this.lastUpdateTime = elapsedRealtime;
                int i = this.state;
                if (i == 0) {
                    float f = this.alpha + (((float) j) / 2000.0f);
                    this.alpha = f;
                    if (f >= 1.0f) {
                        this.alpha = 1.0f;
                        this.state = 1;
                    }
                } else if (i == 1) {
                    float f2 = this.alpha - (((float) j) / 2000.0f);
                    this.alpha = f2;
                    if (f2 < 0.5f) {
                        this.alpha = 0.5f;
                        this.state = 0;
                    }
                }
                this.parentView.invalidate();
            }
        }

        @Override
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(24.0f);
        }

        @Override
        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(24.0f);
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void setAlpha(int i) {
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public void setParentView(View view) {
            this.parentView = view;
        }

        public void setRecording(boolean z) {
            this.recording = z;
            this.alpha = 1.0f;
            invalidateSelf();
        }
    }

    public static class SmallRecordCallDrawable extends Drawable {
        private long lastUpdateTime;
        private View parentView;
        private int state;
        private Paint paint2 = new Paint(1);
        private float alpha = 1.0f;

        public SmallRecordCallDrawable(View view) {
            this.parentView = view;
        }

        @Override
        public void draw(Canvas canvas) {
            int dp;
            int centerX = getBounds().centerX();
            int centerY = getBounds().centerY();
            if (this.parentView instanceof SimpleTextView) {
                dp = centerY + AndroidUtilities.dp(1.0f);
                centerX -= AndroidUtilities.dp(3.0f);
            } else {
                dp = centerY + AndroidUtilities.dp(2.0f);
            }
            this.paint2.setColor(-1147527);
            this.paint2.setAlpha((int) (this.alpha * 255.0f));
            canvas.drawCircle(centerX, dp, AndroidUtilities.dp(4.0f), this.paint2);
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long j = elapsedRealtime - this.lastUpdateTime;
            if (j > 17) {
                j = 17;
            }
            this.lastUpdateTime = elapsedRealtime;
            int i = this.state;
            if (i == 0) {
                float f = this.alpha + (((float) j) / 2000.0f);
                this.alpha = f;
                if (f >= 1.0f) {
                    this.alpha = 1.0f;
                    this.state = 1;
                }
            } else if (i == 1) {
                float f2 = this.alpha - (((float) j) / 2000.0f);
                this.alpha = f2;
                if (f2 < 0.5f) {
                    this.alpha = 0.5f;
                    this.state = 0;
                }
            }
            this.parentView.invalidate();
        }

        @Override
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(24.0f);
        }

        @Override
        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(24.0f);
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void setAlpha(int i) {
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    public static class UpdateCallback implements ListUpdateCallback {
        final RecyclerView.Adapter adapter;

        private UpdateCallback(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public void onChanged(int i, int i2, Object obj) {
            this.adapter.notifyItemRangeChanged(i, i2, obj);
        }

        @Override
        public void onInserted(int i, int i2) {
            this.adapter.notifyItemRangeInserted(i, i2);
        }

        @Override
        public void onMoved(int i, int i2) {
            this.adapter.notifyItemMoved(i, i2);
        }

        @Override
        public void onRemoved(int i, int i2) {
            this.adapter.notifyItemRangeRemoved(i, i2);
        }
    }

    public class VolumeSlider extends FrameLayout {
        private boolean captured;
        private float colorChangeProgress;
        private int currentColor;
        private TLRPC.TL_groupCallParticipant currentParticipant;
        private double currentProgress;
        private boolean dragging;
        private RLottieImageView imageView;
        private long lastUpdateTime;
        private int oldColor;
        private Paint paint;
        private Paint paint2;
        private Path path;
        private float[] radii;
        private RectF rect;
        private RLottieDrawable speakerDrawable;
        private float sx;
        private float sy;
        private TextView textView;
        private int thumbX;
        private float[] volumeAlphas;

        public VolumeSlider(Context context, TLRPC.TL_groupCallParticipant tL_groupCallParticipant) {
            super(context);
            this.paint = new Paint(1);
            this.paint2 = new Paint(1);
            this.path = new Path();
            this.radii = new float[8];
            this.rect = new RectF();
            this.volumeAlphas = new float[3];
            setWillNotDraw(false);
            this.currentParticipant = tL_groupCallParticipant;
            this.currentProgress = ChatObject.getParticipantVolume(tL_groupCallParticipant) / 20000.0f;
            this.colorChangeProgress = 1.0f;
            setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
            int i = R.raw.speaker;
            this.speakerDrawable = new RLottieDrawable(i, "" + i, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), true, null);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.setAnimation(this.speakerDrawable);
            this.imageView.setTag(this.currentProgress == 0.0d ? 1 : null);
            addView(this.imageView, LayoutHelper.createFrame(-2, 40.0f, (LocaleController.isRTL ? 5 : 3) | 16, 0.0f, 0.0f, 0.0f, 0.0f));
            this.speakerDrawable.setCustomEndFrame(this.currentProgress == 0.0d ? 17 : 34);
            RLottieDrawable rLottieDrawable = this.speakerDrawable;
            rLottieDrawable.setCurrentFrame(rLottieDrawable.getCustomEndFrame() - 1, false, true);
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(3);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
            this.textView.setTextSize(1, 16.0f);
            double participantVolume = ChatObject.getParticipantVolume(this.currentParticipant);
            Double.isNaN(participantVolume);
            double d = participantVolume / 100.0d;
            this.textView.setText(String.format(Locale.US, "%d%%", Integer.valueOf((int) (d > 0.0d ? Math.max(d, 1.0d) : 0.0d))));
            this.textView.setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(43.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(43.0f) : 0, 0);
            addView(this.textView, LayoutHelper.createFrame(-2, -2, (LocaleController.isRTL ? 5 : 3) | 16));
            this.paint2.setStyle(Paint.Style.STROKE);
            this.paint2.setStrokeWidth(AndroidUtilities.dp(1.5f));
            this.paint2.setStrokeCap(Paint.Cap.ROUND);
            this.paint2.setColor(-1);
            double participantVolume2 = ChatObject.getParticipantVolume(this.currentParticipant);
            Double.isNaN(participantVolume2);
            int i2 = (int) (participantVolume2 / 100.0d);
            int i3 = 0;
            while (true) {
                float[] fArr = this.volumeAlphas;
                if (i3 >= fArr.length) {
                    return;
                }
                if (i2 > (i3 == 0 ? 0 : i3 == 1 ? 50 : 150)) {
                    fArr[i3] = 1.0f;
                } else {
                    fArr[i3] = 0.0f;
                }
                i3++;
            }
        }

        private void onSeekBarDrag(double d, boolean z) {
            if (VoIPService.getSharedInstance() == null) {
                return;
            }
            this.currentProgress = d;
            TLRPC.TL_groupCallParticipant tL_groupCallParticipant = this.currentParticipant;
            tL_groupCallParticipant.volume = (int) (d * 20000.0d);
            tL_groupCallParticipant.volume_by_admin = false;
            tL_groupCallParticipant.flags |= 128;
            double participantVolume = ChatObject.getParticipantVolume(tL_groupCallParticipant);
            Double.isNaN(participantVolume);
            double d2 = participantVolume / 100.0d;
            this.textView.setText(String.format(Locale.US, "%d%%", Integer.valueOf((int) (d2 > 0.0d ? Math.max(d2, 1.0d) : 0.0d))));
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            TLRPC.TL_groupCallParticipant tL_groupCallParticipant2 = this.currentParticipant;
            sharedInstance.setParticipantVolume(tL_groupCallParticipant2, tL_groupCallParticipant2.volume);
            if (z) {
                long peerId = MessageObject.getPeerId(this.currentParticipant.peer);
                TLObject user = peerId > 0 ? GroupCallActivity.this.accountInstance.getMessagesController().getUser(Long.valueOf(peerId)) : GroupCallActivity.this.accountInstance.getMessagesController().getChat(Long.valueOf(-peerId));
                if (this.currentParticipant.volume == 0) {
                    if (GroupCallActivity.this.scrimPopupWindow != null) {
                        GroupCallActivity.this.scrimPopupWindow.dismiss();
                        GroupCallActivity.this.scrimPopupWindow = null;
                    }
                    GroupCallActivity.this.dismissAvatarPreview(true);
                    GroupCallActivity groupCallActivity = GroupCallActivity.this;
                    groupCallActivity.processSelectedOption(this.currentParticipant, peerId, ChatObject.canManageCalls(groupCallActivity.currentChat) ? 0 : 5);
                } else {
                    VoIPService.getSharedInstance().editCallMember(user, null, null, Integer.valueOf(this.currentParticipant.volume), null, null);
                }
            }
            Integer num = this.currentProgress == 0.0d ? 1 : null;
            if ((this.imageView.getTag() != null || num == null) && (this.imageView.getTag() == null || num != null)) {
                return;
            }
            this.speakerDrawable.setCustomEndFrame(this.currentProgress == 0.0d ? 17 : 34);
            this.speakerDrawable.setCurrentFrame(this.currentProgress != 0.0d ? 17 : 0);
            this.speakerDrawable.start();
            this.imageView.setTag(num);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int i;
            float dp;
            int i2;
            int i3 = this.currentColor;
            double d = this.currentProgress;
            this.currentColor = d < 0.25d ? -3385513 : (d <= 0.25d || d >= 0.5d) ? (d < 0.5d || d > 0.75d) ? -11688225 : -11027349 : -3562181;
            float f = 1.0f;
            if (i3 == 0) {
                i = this.currentColor;
                this.colorChangeProgress = 1.0f;
            } else {
                int offsetColor = AndroidUtilities.getOffsetColor(this.oldColor, i3, this.colorChangeProgress, 1.0f);
                if (i3 != this.currentColor) {
                    this.colorChangeProgress = 0.0f;
                    this.oldColor = offsetColor;
                }
                i = offsetColor;
            }
            this.paint.setColor(i);
            long elapsedRealtime = SystemClock.elapsedRealtime();
            long j = elapsedRealtime - this.lastUpdateTime;
            if (j > 17) {
                j = 17;
            }
            this.lastUpdateTime = elapsedRealtime;
            float f2 = this.colorChangeProgress;
            if (f2 < 1.0f) {
                float f3 = f2 + (((float) j) / 200.0f);
                this.colorChangeProgress = f3;
                if (f3 > 1.0f) {
                    this.colorChangeProgress = 1.0f;
                } else {
                    invalidate();
                }
            }
            this.path.reset();
            float[] fArr = this.radii;
            float f4 = 6.0f;
            float dp2 = AndroidUtilities.dp(6.0f);
            fArr[7] = dp2;
            fArr[6] = dp2;
            int i4 = 1;
            fArr[1] = dp2;
            fArr[0] = dp2;
            float max = this.thumbX < AndroidUtilities.dp(12.0f) ? Math.max(0.0f, (this.thumbX - AndroidUtilities.dp(6.0f)) / AndroidUtilities.dp(6.0f)) : 1.0f;
            float[] fArr2 = this.radii;
            float dp3 = AndroidUtilities.dp(6.0f) * max;
            fArr2[5] = dp3;
            fArr2[4] = dp3;
            fArr2[3] = dp3;
            fArr2[2] = dp3;
            this.rect.set(0.0f, 0.0f, this.thumbX, getMeasuredHeight());
            this.path.addRoundRect(this.rect, this.radii, Path.Direction.CW);
            this.path.close();
            canvas.drawPath(this.path, this.paint);
            double participantVolume = ChatObject.getParticipantVolume(this.currentParticipant);
            Double.isNaN(participantVolume);
            int i5 = (int) (participantVolume / 100.0d);
            int left = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2) + AndroidUtilities.dp(5.0f);
            int top = this.imageView.getTop() + (this.imageView.getMeasuredHeight() / 2);
            int i6 = 0;
            while (i6 < this.volumeAlphas.length) {
                if (i6 == 0) {
                    dp = AndroidUtilities.dp(f4);
                    i2 = 0;
                } else if (i6 == i4) {
                    dp = AndroidUtilities.dp(10.0f);
                    i2 = 50;
                } else {
                    dp = AndroidUtilities.dp(14.0f);
                    i2 = 150;
                }
                float dp4 = AndroidUtilities.dp(2.0f);
                float f5 = this.volumeAlphas[i6];
                float f6 = dp4 * (f - f5);
                this.paint2.setAlpha((int) (f5 * 255.0f));
                float f7 = left;
                float f8 = top;
                this.rect.set((f7 - dp) + f6, (f8 - dp) + f6, (f7 + dp) - f6, (f8 + dp) - f6);
                int i7 = i2;
                int i8 = i6;
                canvas.drawArc(this.rect, -50.0f, 100.0f, false, this.paint2);
                if (i5 > i7) {
                    float[] fArr3 = this.volumeAlphas;
                    float f9 = fArr3[i8];
                    if (f9 < 1.0f) {
                        float f10 = f9 + (((float) j) / 180.0f);
                        fArr3[i8] = f10;
                        if (f10 > 1.0f) {
                            fArr3[i8] = 1.0f;
                        }
                        invalidate();
                        i6 = i8 + 1;
                        f = 1.0f;
                        f4 = 6.0f;
                        i4 = 1;
                    } else {
                        i6 = i8 + 1;
                        f = 1.0f;
                        f4 = 6.0f;
                        i4 = 1;
                    }
                } else {
                    float[] fArr4 = this.volumeAlphas;
                    float f11 = fArr4[i8];
                    if (f11 > 0.0f) {
                        float f12 = f11 - (((float) j) / 180.0f);
                        fArr4[i8] = f12;
                        if (f12 < 0.0f) {
                            fArr4[i8] = 0.0f;
                        }
                        invalidate();
                        i6 = i8 + 1;
                        f = 1.0f;
                        f4 = 6.0f;
                        i4 = 1;
                    } else {
                        i6 = i8 + 1;
                        f = 1.0f;
                        f4 = 6.0f;
                        i4 = 1;
                    }
                }
            }
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return onTouch(motionEvent);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
            double size = View.MeasureSpec.getSize(i);
            double d = this.currentProgress;
            Double.isNaN(size);
            this.thumbX = (int) (size * d);
        }

        boolean onTouch(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                this.sx = motionEvent.getX();
                this.sy = motionEvent.getY();
                return true;
            }
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                this.captured = false;
                if (motionEvent.getAction() == 1) {
                    if (Math.abs(motionEvent.getY() - this.sy) < ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                        int x = (int) motionEvent.getX();
                        this.thumbX = x;
                        if (x < 0) {
                            this.thumbX = 0;
                        } else if (x > getMeasuredWidth()) {
                            this.thumbX = getMeasuredWidth();
                        }
                        this.dragging = true;
                    }
                }
                if (this.dragging) {
                    if (motionEvent.getAction() == 1) {
                        double d = this.thumbX;
                        double measuredWidth = getMeasuredWidth();
                        Double.isNaN(d);
                        Double.isNaN(measuredWidth);
                        onSeekBarDrag(d / measuredWidth, true);
                    }
                    this.dragging = false;
                    invalidate();
                    return true;
                }
            } else if (motionEvent.getAction() == 2) {
                if (!this.captured) {
                    ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
                    if (Math.abs(motionEvent.getY() - this.sy) <= viewConfiguration.getScaledTouchSlop() && Math.abs(motionEvent.getX() - this.sx) > viewConfiguration.getScaledTouchSlop()) {
                        this.captured = true;
                        getParent().requestDisallowInterceptTouchEvent(true);
                        if (motionEvent.getY() >= 0.0f && motionEvent.getY() <= getMeasuredHeight()) {
                            int x2 = (int) motionEvent.getX();
                            this.thumbX = x2;
                            if (x2 < 0) {
                                this.thumbX = 0;
                            } else if (x2 > getMeasuredWidth()) {
                                this.thumbX = getMeasuredWidth();
                            }
                            this.dragging = true;
                            invalidate();
                            return true;
                        }
                    }
                } else if (this.dragging) {
                    int x3 = (int) motionEvent.getX();
                    this.thumbX = x3;
                    if (x3 < 0) {
                        this.thumbX = 0;
                    } else if (x3 > getMeasuredWidth()) {
                        this.thumbX = getMeasuredWidth();
                    }
                    double d2 = this.thumbX;
                    double measuredWidth2 = getMeasuredWidth();
                    Double.isNaN(d2);
                    Double.isNaN(measuredWidth2);
                    onSeekBarDrag(d2 / measuredWidth2, false);
                    invalidate();
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return onTouch(motionEvent);
        }
    }

    public class WatchersView extends LinearLayout {
        private float lastWidth;
        private final AnimatedTextView watchersCountTextView;

        public WatchersView(Context context) {
            super(context);
            this.lastWidth = 0.0f;
            setOrientation(1);
            setGravity(17);
            AnimatedTextView animatedTextView = new AnimatedTextView(context, true, false, false);
            this.watchersCountTextView = animatedTextView;
            animatedTextView.setTextColor(-1);
            animatedTextView.setTextSize(AndroidUtilities.dp(46.0f));
            animatedTextView.setTypeface(AndroidUtilities.bold());
            animatedTextView.setGravity(1);
            TextView textView = new TextView(context);
            textView.setTextColor(-1);
            textView.setTextSize(1, 14.0f);
            textView.setTypeface(AndroidUtilities.bold());
            textView.setText(LocaleController.getString(R.string.VoipChannelWatching));
            addView(animatedTextView, LayoutHelper.createLinear(-1, 46));
            addView(textView, LayoutHelper.createLinear(-2, -2));
        }

        void setWatchersCount(int i) {
            String formatNumber = LocaleController.formatNumber(i, ',');
            float measureText = this.watchersCountTextView.getPaint().measureText((CharSequence) formatNumber, 0, formatNumber.length());
            if (this.lastWidth != measureText) {
                this.watchersCountTextView.getPaint().setShader(new LinearGradient(0.0f, 0.0f, measureText, 0.0f, new int[]{GroupCallActivity.this.getThemedColor(Theme.key_premiumGradient1), GroupCallActivity.this.getThemedColor(Theme.key_premiumGradient3)}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
                this.lastWidth = measureText;
            }
            this.watchersCountTextView.setText(formatNumber);
        }
    }

    public static class WeavingState {
        public int currentState;
        private float duration;
        public Shader shader;
        private float startX;
        private float startY;
        private float time;
        private float targetX = -1.0f;
        private float targetY = -1.0f;
        private Matrix matrix = new Matrix();

        public WeavingState(int i) {
            this.currentState = i;
        }

        private void setTarget() {
            float nextInt;
            if (GroupCallActivity.isGradientState(this.currentState)) {
                this.targetX = ((Utilities.random.nextInt(100) * 0.2f) / 100.0f) + 0.85f;
                nextInt = 1.0f;
            } else if (this.currentState == 1) {
                this.targetX = ((Utilities.random.nextInt(100) * 0.3f) / 100.0f) + 0.2f;
                nextInt = ((Utilities.random.nextInt(100) * 0.3f) / 100.0f) + 0.7f;
            } else {
                this.targetX = ((Utilities.random.nextInt(100) / 100.0f) * 0.2f) + 0.8f;
                nextInt = Utilities.random.nextInt(100) / 100.0f;
            }
            this.targetY = nextInt;
        }

        public void update(int i, int i2, int i3, long j, float f) {
            if (this.shader == null) {
                return;
            }
            float f2 = this.duration;
            if (f2 == 0.0f || this.time >= f2) {
                this.duration = Utilities.random.nextInt(200) + 1500;
                this.time = 0.0f;
                if (this.targetX == -1.0f) {
                    setTarget();
                }
                this.startX = this.targetX;
                this.startY = this.targetY;
                setTarget();
            }
            float f3 = (float) j;
            float f4 = this.time + ((BlobDrawable.GRADIENT_SPEED_MIN + 0.5f) * f3) + (f3 * BlobDrawable.GRADIENT_SPEED_MAX * 2.0f * f);
            this.time = f4;
            float f5 = this.duration;
            if (f4 > f5) {
                this.time = f5;
            }
            float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(this.time / f5);
            float f6 = i3;
            float f7 = this.startX;
            float f8 = (i2 + ((f7 + ((this.targetX - f7) * interpolation)) * f6)) - 200.0f;
            float f9 = this.startY;
            float f10 = (i + (f6 * (f9 + ((this.targetY - f9) * interpolation)))) - 200.0f;
            float dp = (AndroidUtilities.dp(122.0f) / 400.0f) * (GroupCallActivity.isGradientState(this.currentState) ? 1.0f : this.currentState == 1 ? 4.0f : 2.5f);
            this.matrix.reset();
            this.matrix.postTranslate(f8, f10);
            this.matrix.postScale(dp, dp, f8 + 200.0f, f10 + 200.0f);
            this.shader.setLocalMatrix(this.matrix);
        }
    }

    private GroupCallActivity(final android.content.Context r40, final org.telegram.messenger.AccountInstance r41, org.telegram.messenger.ChatObject.Call r42, final org.telegram.tgnet.TLRPC.Chat r43, org.telegram.tgnet.TLRPC.InputPeer r44, boolean r45, java.lang.String r46) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.<init>(android.content.Context, org.telegram.messenger.AccountInstance, org.telegram.messenger.ChatObject$Call, org.telegram.tgnet.TLRPC$Chat, org.telegram.tgnet.TLRPC$InputPeer, boolean, java.lang.String):void");
    }

    static float access$11216(GroupCallActivity groupCallActivity, float f) {
        float f2 = groupCallActivity.amplitude + f;
        groupCallActivity.amplitude = f2;
        return f2;
    }

    static float access$13716(GroupCallActivity groupCallActivity, float f) {
        float f2 = groupCallActivity.switchProgress + f;
        groupCallActivity.switchProgress = f2;
        return f2;
    }

    static float access$14316(GroupCallActivity groupCallActivity, float f) {
        float f2 = groupCallActivity.showWavesProgress + f;
        groupCallActivity.showWavesProgress = f2;
        return f2;
    }

    static float access$14324(GroupCallActivity groupCallActivity, float f) {
        float f2 = groupCallActivity.showWavesProgress - f;
        groupCallActivity.showWavesProgress = f2;
        return f2;
    }

    static float access$14416(GroupCallActivity groupCallActivity, float f) {
        float f2 = groupCallActivity.showLightingProgress + f;
        groupCallActivity.showLightingProgress = f2;
        return f2;
    }

    static float access$14424(GroupCallActivity groupCallActivity, float f) {
        float f2 = groupCallActivity.showLightingProgress - f;
        groupCallActivity.showLightingProgress = f2;
        return f2;
    }

    public void applyCallParticipantUpdates(boolean r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.applyCallParticipantUpdates(boolean):void");
    }

    public void attachRenderer(GroupCallGridCell groupCallGridCell, boolean z) {
        if (isDismissed()) {
            return;
        }
        if (z && groupCallGridCell.getRenderer() == null) {
            groupCallGridCell.setRenderer(GroupCallMiniTextureView.getOrCreate(this.attachedRenderers, this.renderersContainer, groupCallGridCell, null, null, groupCallGridCell.getParticipant(), this.call, this));
        } else {
            if (z || groupCallGridCell.getRenderer() == null) {
                return;
            }
            groupCallGridCell.getRenderer().setPrimaryView(null);
            groupCallGridCell.setRenderer(null);
        }
    }

    private void cancelMutePress() {
        if (this.scheduled) {
            this.scheduled = false;
            AndroidUtilities.cancelRunOnUIThread(this.pressRunnable);
        }
        if (this.pressed) {
            this.pressed = false;
            MotionEvent obtain = MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0);
            this.muteButton.onTouchEvent(obtain);
            obtain.recycle();
        }
    }

    public void checkContentOverlayed() {
        boolean z = !this.avatarPriviewTransitionInProgress && this.blurredView.getVisibility() == 0 && this.blurredView.getAlpha() == 1.0f;
        if (this.contentFullyOverlayed != z) {
            this.contentFullyOverlayed = z;
            this.buttonsContainer.invalidate();
            this.containerView.invalidate();
            this.listView.invalidate();
        }
    }

    public void clearScrimView() {
        GroupCallMiniTextureView groupCallMiniTextureView = this.scrimRenderer;
        if (groupCallMiniTextureView != null) {
            groupCallMiniTextureView.textureView.setRoundCorners(AndroidUtilities.dp(8.0f));
            this.scrimRenderer.setShowingAsScrimView(false, false);
            this.scrimRenderer.invalidate();
            this.renderersContainer.invalidate();
        }
        GroupCallUserCell groupCallUserCell = this.scrimView;
        if (groupCallUserCell != null && !this.hasScrimAnchorView && groupCallUserCell.getParent() != null) {
            this.containerView.removeView(this.scrimView);
        }
        GroupCallUserCell groupCallUserCell2 = this.scrimView;
        if (groupCallUserCell2 != null) {
            groupCallUserCell2.setProgressToAvatarPreview(0.0f);
            this.scrimView.setAboutVisible(false);
            this.scrimView.getAvatarImageView().setAlpha(1.0f);
        }
        GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell3 = this.scrimFullscreenView;
        if (groupCallUserCell3 != null) {
            groupCallUserCell3.getAvatarImageView().setAlpha(1.0f);
        }
        this.scrimView = null;
        this.scrimGridView = null;
        this.scrimFullscreenView = null;
        this.scrimRenderer = null;
    }

    public static void create(LaunchActivity launchActivity, AccountInstance accountInstance, TLRPC.Chat chat, TLRPC.InputPeer inputPeer, boolean z, String str) {
        TLRPC.Chat chat2;
        if (groupCallInstance == null) {
            if (inputPeer == null && VoIPService.getSharedInstance() == null) {
                return;
            }
            if (inputPeer != null) {
                groupCallInstance = new GroupCallActivity(launchActivity, accountInstance, accountInstance.getMessagesController().getGroupCall(chat.id, false), chat, inputPeer, z, str);
            } else {
                ChatObject.Call call = VoIPService.getSharedInstance().groupCall;
                if (call == null || (chat2 = accountInstance.getMessagesController().getChat(Long.valueOf(call.chatId))) == null) {
                    return;
                }
                call.addSelfDummyParticipant(true);
                groupCallInstance = new GroupCallActivity(launchActivity, accountInstance, call, chat2, null, z, str);
            }
            groupCallInstance.parentActivity = launchActivity;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    GroupCallActivity.lambda$create$9();
                }
            });
        }
    }

    public void dismissAvatarPreview(boolean z) {
        if (this.avatarPriviewTransitionInProgress || !this.avatarsPreviewShowed) {
            return;
        }
        if (z) {
            this.avatarPriviewTransitionInProgress = true;
            runAvatarPreviewTransition(false, this.scrimView);
            return;
        }
        clearScrimView();
        this.containerView.removeView(this.scrimPopupLayout);
        this.scrimPopupLayout = null;
        this.avatarPreviewContainer.setVisibility(8);
        this.containerView.invalidate();
        this.avatarsPreviewShowed = false;
        this.layoutManager.setCanScrollVertically(true);
        this.listView.invalidate();
        this.blurredView.setVisibility(8);
        if (this.delayedGroupCallUpdated) {
            this.delayedGroupCallUpdated = false;
            applyCallParticipantUpdates(true);
        }
        checkContentOverlayed();
    }

    public void fillColors(int i, int[] iArr) {
        if (isRtmpStream()) {
            int i2 = Theme.key_voipgroup_disabledButton;
            iArr[0] = Theme.getColor(i2);
            iArr[1] = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_rtmpButton), Theme.getColor(Theme.key_voipgroup_soundButtonActiveScrolled), this.colorProgress, 1.0f);
            iArr[2] = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_listViewBackgroundUnscrolled), Theme.getColor(i2), this.colorProgress, 1.0f);
            return;
        }
        if (i == 0) {
            iArr[0] = Theme.getColor(Theme.key_voipgroup_unmuteButton2);
            iArr[1] = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_soundButtonActive), Theme.getColor(Theme.key_voipgroup_soundButtonActiveScrolled), this.colorProgress, 1.0f);
            iArr[2] = Theme.getColor(Theme.key_voipgroup_soundButton);
        } else if (i == 1) {
            iArr[0] = Theme.getColor(Theme.key_voipgroup_muteButton2);
            iArr[1] = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_soundButtonActive2), Theme.getColor(Theme.key_voipgroup_soundButtonActive2Scrolled), this.colorProgress, 1.0f);
            iArr[2] = Theme.getColor(Theme.key_voipgroup_soundButton2);
        } else if (isGradientState(i)) {
            iArr[0] = Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient3);
            iArr[1] = Theme.getColor(Theme.key_voipgroup_mutedByAdminMuteButton);
            iArr[2] = Theme.getColor(Theme.key_voipgroup_mutedByAdminMuteButtonDisabled);
        } else {
            int i3 = Theme.key_voipgroup_disabledButton;
            iArr[0] = Theme.getColor(i3);
            iArr[1] = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_disabledButtonActive), Theme.getColor(Theme.key_voipgroup_disabledButtonActiveScrolled), this.colorProgress, 1.0f);
            iArr[2] = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_listViewBackgroundUnscrolled), Theme.getColor(i3), this.colorProgress, 1.0f);
        }
    }

    public GroupCallGridCell findGroupCallGridCell() {
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            View childAt = this.listView.getChildAt(i);
            if ((childAt instanceof GroupCallGridCell) && this.listView.getChildAdapterPosition(childAt) >= 0) {
                return (GroupCallGridCell) childAt;
            }
        }
        return null;
    }

    public float getColorProgress() {
        return this.colorProgress;
    }

    public void getLink(final boolean z) {
        String str;
        TLRPC.TL_chatInviteExported tL_chatInviteExported;
        TLRPC.Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(this.currentChat.id));
        if (chat == null || ChatObject.isPublic(chat)) {
            if (this.call == null) {
                return;
            }
            final int i = 0;
            while (i < 2) {
                TLRPC.TL_phone_exportGroupCallInvite tL_phone_exportGroupCallInvite = new TLRPC.TL_phone_exportGroupCallInvite();
                tL_phone_exportGroupCallInvite.call = this.call.getInputGroupCall();
                tL_phone_exportGroupCallInvite.can_self_unmute = i == 1;
                this.accountInstance.getConnectionsManager().sendRequest(tL_phone_exportGroupCallInvite, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        GroupCallActivity.this.lambda$getLink$42(i, z, tLObject, tL_error);
                    }
                });
                i++;
            }
            return;
        }
        final TLRPC.ChatFull chatFull = this.accountInstance.getMessagesController().getChatFull(this.currentChat.id);
        String publicUsername = ChatObject.getPublicUsername(this.currentChat);
        if (TextUtils.isEmpty(publicUsername)) {
            str = (chatFull == null || (tL_chatInviteExported = chatFull.exported_invite) == null) ? null : tL_chatInviteExported.link;
        } else {
            str = this.accountInstance.getMessagesController().linkPrefix + "/" + publicUsername;
        }
        if (!TextUtils.isEmpty(str)) {
            openShareAlert(true, null, str, z);
            return;
        }
        TLRPC.TL_messages_exportChatInvite tL_messages_exportChatInvite = new TLRPC.TL_messages_exportChatInvite();
        tL_messages_exportChatInvite.peer = MessagesController.getInputPeer(this.currentChat);
        this.accountInstance.getConnectionsManager().sendRequest(tL_messages_exportChatInvite, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                GroupCallActivity.this.lambda$getLink$40(chatFull, z, tLObject, tL_error);
            }
        });
    }

    private void initCreatedGroupCall() {
        VoIPService sharedInstance;
        if (this.callInitied || (sharedInstance = VoIPService.getSharedInstance()) == null) {
            return;
        }
        this.callInitied = true;
        this.oldParticipants.addAll(this.call.visibleParticipants);
        this.oldVideoParticipants.addAll(this.visibleVideoParticipants);
        this.oldInvited.addAll(this.call.invitedUsers);
        this.currentCallState = sharedInstance.getCallState();
        if (this.call == null) {
            ChatObject.Call call = sharedInstance.groupCall;
            this.call = call;
            this.fullscreenAdapter.setGroupCall(call);
            this.renderersContainer.setGroupCall(this.call);
            this.tabletGridAdapter.setGroupCall(this.call);
        }
        this.actionBar.setTitleRightMargin(AndroidUtilities.dp(48.0f) * 2);
        this.call.saveActiveDates();
        VoIPService.getSharedInstance().registerStateListener(this);
        SimpleTextView simpleTextView = this.scheduleTimeTextView;
        if (simpleTextView == null || simpleTextView.getVisibility() != 0) {
            return;
        }
        this.leaveButton.setData(isRtmpStream() ? R.drawable.msg_voiceclose : R.drawable.calls_decline, -1, Theme.getColor(Theme.key_voipgroup_leaveButton), 0.3f, false, LocaleController.getString(R.string.VoipGroupLeave), false, true);
        updateSpeakerPhoneIcon(true);
        this.leaveItem.setText(LocaleController.getString(ChatObject.isChannelOrGiga(this.currentChat) ? R.string.VoipChannelEndChat : R.string.VoipGroupEndChat));
        this.listView.setVisibility(0);
        this.pipItem.setVisibility(0);
        AnimatorSet animatorSet = new AnimatorSet();
        RecyclerListView recyclerListView = this.listView;
        Property property = View.ALPHA;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(recyclerListView, (Property<RecyclerListView, Float>) property, 0.0f, 1.0f);
        ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.listView, (Property<RecyclerListView, Float>) View.TRANSLATION_Y, AndroidUtilities.dp(200.0f), 0.0f);
        SimpleTextView simpleTextView2 = this.scheduleTimeTextView;
        Property property2 = View.SCALE_X;
        ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(simpleTextView2, (Property<SimpleTextView, Float>) property2, 0.0f);
        SimpleTextView simpleTextView3 = this.scheduleTimeTextView;
        Property property3 = View.SCALE_Y;
        animatorSet.playTogether(ofFloat, ofFloat2, ofFloat3, ObjectAnimator.ofFloat(simpleTextView3, (Property<SimpleTextView, Float>) property3, 0.0f), ObjectAnimator.ofFloat(this.scheduleTimeTextView, (Property<SimpleTextView, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.scheduleStartInTextView, (Property<SimpleTextView, Float>) property2, 0.0f), ObjectAnimator.ofFloat(this.scheduleStartInTextView, (Property<SimpleTextView, Float>) property3, 0.0f), ObjectAnimator.ofFloat(this.scheduleStartInTextView, (Property<SimpleTextView, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.scheduleStartAtTextView, (Property<SimpleTextView, Float>) property2, 0.0f), ObjectAnimator.ofFloat(this.scheduleStartAtTextView, (Property<SimpleTextView, Float>) property3, 0.0f), ObjectAnimator.ofFloat(this.scheduleStartAtTextView, (Property<SimpleTextView, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.pipItem, (Property<ActionBarMenuItem, Float>) property2, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.pipItem, (Property<ActionBarMenuItem, Float>) property3, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.pipItem, (Property<ActionBarMenuItem, Float>) property, 0.0f, 1.0f));
        animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                GroupCallActivity.this.scheduleTimeTextView.setVisibility(4);
                GroupCallActivity.this.scheduleStartAtTextView.setVisibility(4);
                GroupCallActivity.this.scheduleStartInTextView.setVisibility(4);
            }
        });
        animatorSet.setDuration(300L);
        animatorSet.start();
    }

    public void invalidateLayoutFullscreen() {
        int i;
        if (isRtmpStream()) {
            boolean z = (!this.renderersContainer.isUiVisible() && this.renderersContainer.inFullscreenMode && (isLandscapeMode == isRtmpLandscapeMode() || AndroidUtilities.isTablet())) ? false : true;
            Boolean bool = this.wasNotInLayoutFullscreen;
            if (bool == null || z != bool.booleanValue()) {
                int systemUiVisibility = this.containerView.getSystemUiVisibility();
                if (z) {
                    i = systemUiVisibility & (-7);
                    getWindow().clearFlags(1024);
                    setHideSystemVerticalInsets(false);
                } else {
                    setHideSystemVerticalInsets(true);
                    i = systemUiVisibility | 6;
                    getWindow().addFlags(1024);
                }
                this.containerView.setSystemUiVisibility(i);
                this.wasNotInLayoutFullscreen = Boolean.valueOf(z);
            }
        }
    }

    public void inviteUserToCall(final long j, final boolean z) {
        final TLRPC.User user;
        if (this.call == null || (user = this.accountInstance.getMessagesController().getUser(Long.valueOf(j))) == null) {
            return;
        }
        final AlertDialog[] alertDialogArr = {new AlertDialog(getContext(), 3)};
        final TLRPC.TL_phone_inviteToGroupCall tL_phone_inviteToGroupCall = new TLRPC.TL_phone_inviteToGroupCall();
        tL_phone_inviteToGroupCall.call = this.call.getInputGroupCall();
        TLRPC.TL_inputUser tL_inputUser = new TLRPC.TL_inputUser();
        tL_inputUser.user_id = user.id;
        tL_inputUser.access_hash = user.access_hash;
        tL_phone_inviteToGroupCall.users.add(tL_inputUser);
        final int sendRequest = this.accountInstance.getConnectionsManager().sendRequest(tL_phone_inviteToGroupCall, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                GroupCallActivity.this.lambda$inviteUserToCall$47(j, alertDialogArr, user, z, tL_phone_inviteToGroupCall, tLObject, tL_error);
            }
        });
        if (sendRequest != 0) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    GroupCallActivity.this.lambda$inviteUserToCall$49(alertDialogArr, sendRequest);
                }
            }, 500L);
        }
    }

    public static boolean isGradientState(int i) {
        return !(VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().groupCall == null || !VoIPService.getSharedInstance().groupCall.call.rtmp_stream) || i == 2 || i == 4 || i == 5 || i == 6 || i == 7;
    }

    public boolean isStillConnecting() {
        int i = this.currentCallState;
        return i == 1 || i == 2 || i == 6 || i == 5;
    }

    public static void lambda$create$9() {
        GroupCallActivity groupCallActivity = groupCallInstance;
        if (groupCallActivity != null) {
            groupCallActivity.show();
        }
    }

    public void lambda$didReceivedNotification$2() {
        if (isStillConnecting()) {
            updateState(true, false);
        }
    }

    public void lambda$didReceivedNotification$3(DialogInterface dialogInterface) {
        dismiss();
    }

    public void lambda$fullscreenFor$36(ArrayList arrayList) {
        for (int i = 0; i < this.attachedRenderers.size(); i++) {
            if (((GroupCallMiniTextureView) this.attachedRenderers.get(i)).participant != null) {
                arrayList.remove(((GroupCallMiniTextureView) this.attachedRenderers.get(i)).participant);
            }
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            ChatObject.VideoParticipant videoParticipant = (ChatObject.VideoParticipant) arrayList.get(i2);
            if (videoParticipant.participant.self) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().setLocalSink(null, videoParticipant.presentation);
                }
            } else if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().removeRemoteSink(videoParticipant.participant, videoParticipant.presentation);
            }
        }
    }

    public void lambda$getLink$39(TLObject tLObject, TLRPC.ChatFull chatFull, boolean z) {
        if (tLObject instanceof TLRPC.TL_chatInviteExported) {
            TLRPC.TL_chatInviteExported tL_chatInviteExported = (TLRPC.TL_chatInviteExported) tLObject;
            if (chatFull != null) {
                chatFull.exported_invite = tL_chatInviteExported;
            } else {
                openShareAlert(true, null, tL_chatInviteExported.link, z);
            }
        }
    }

    public void lambda$getLink$40(final TLRPC.ChatFull chatFull, final boolean z, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                GroupCallActivity.this.lambda$getLink$39(tLObject, chatFull, z);
            }
        });
    }

    public void lambda$getLink$41(TLObject tLObject, int i, boolean z) {
        if (tLObject instanceof TLRPC.TL_phone_exportedGroupCallInvite) {
            this.invites[i] = ((TLRPC.TL_phone_exportedGroupCallInvite) tLObject).link;
        } else {
            this.invites[i] = "";
        }
        for (int i2 = 0; i2 < 2; i2++) {
            String str = this.invites[i2];
            if (str == null) {
                return;
            }
            if (str.length() == 0) {
                this.invites[i2] = null;
            }
        }
        if (!z && ChatObject.canManageCalls(this.currentChat) && !this.call.call.join_muted) {
            this.invites[0] = null;
        }
        String[] strArr = this.invites;
        if (strArr[0] != null || strArr[1] != null || !ChatObject.isPublic(this.currentChat)) {
            String[] strArr2 = this.invites;
            openShareAlert(false, strArr2[0], strArr2[1], z);
            return;
        }
        openShareAlert(true, null, this.accountInstance.getMessagesController().linkPrefix + "/" + ChatObject.getPublicUsername(this.currentChat), z);
    }

    public void lambda$getLink$42(final int i, final boolean z, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                GroupCallActivity.this.lambda$getLink$41(tLObject, i, z);
            }
        });
    }

    public void lambda$inviteUserToCall$45(long j, AlertDialog[] alertDialogArr, TLRPC.User user) {
        ChatObject.Call call = this.call;
        if (call == null || this.delayedGroupCallUpdated) {
            return;
        }
        call.addInvitedUser(j);
        applyCallParticipantUpdates(true);
        GroupVoipInviteAlert groupVoipInviteAlert = this.groupVoipInviteAlert;
        if (groupVoipInviteAlert != null) {
            groupVoipInviteAlert.dismiss();
        }
        try {
            alertDialogArr[0].dismiss();
        } catch (Throwable unused) {
        }
        alertDialogArr[0] = null;
        getUndoView().showWithAction(0L, 34, user, this.currentChat, (Runnable) null, (Runnable) null);
    }

    public void lambda$inviteUserToCall$46(AlertDialog[] alertDialogArr, boolean z, TLRPC.TL_error tL_error, long j, TLRPC.TL_phone_inviteToGroupCall tL_phone_inviteToGroupCall) {
        try {
            alertDialogArr[0].dismiss();
        } catch (Throwable unused) {
        }
        alertDialogArr[0] = null;
        if (z && "USER_NOT_PARTICIPANT".equals(tL_error.text)) {
            processSelectedOption(null, j, 3);
        } else {
            AlertsCreator.processError(this.currentAccount, tL_error, (BaseFragment) this.parentActivity.getActionBarLayout().getFragmentStack().get(this.parentActivity.getActionBarLayout().getFragmentStack().size() - 1), tL_phone_inviteToGroupCall, new Object[0]);
        }
    }

    public void lambda$inviteUserToCall$47(final long j, final AlertDialog[] alertDialogArr, final TLRPC.User user, final boolean z, final TLRPC.TL_phone_inviteToGroupCall tL_phone_inviteToGroupCall, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    GroupCallActivity.this.lambda$inviteUserToCall$46(alertDialogArr, z, tL_error, j, tL_phone_inviteToGroupCall);
                }
            });
        } else {
            this.accountInstance.getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    GroupCallActivity.this.lambda$inviteUserToCall$45(j, alertDialogArr, user);
                }
            });
        }
    }

    public void lambda$inviteUserToCall$48(int i, DialogInterface dialogInterface) {
        this.accountInstance.getConnectionsManager().cancelRequest(i, true);
    }

    public void lambda$inviteUserToCall$49(AlertDialog[] alertDialogArr, final int i) {
        AlertDialog alertDialog = alertDialogArr[0];
        if (alertDialog == null) {
            return;
        }
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public final void onCancel(DialogInterface dialogInterface) {
                GroupCallActivity.this.lambda$inviteUserToCall$48(i, dialogInterface);
            }
        });
        alertDialogArr[0].show();
    }

    public static void lambda$makeFocusable$7(BottomSheet bottomSheet, final EditTextBoldCursor editTextBoldCursor, boolean z, AlertDialog alertDialog) {
        Runnable runnable;
        if (bottomSheet != null && !bottomSheet.isDismissed()) {
            bottomSheet.setFocusable(true);
            editTextBoldCursor.requestFocus();
            if (!z) {
                return;
            } else {
                runnable = new Runnable() {
                    @Override
                    public final void run() {
                        AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
                    }
                };
            }
        } else {
            if (alertDialog == null || !alertDialog.isShowing()) {
                return;
            }
            alertDialog.setFocusable(true);
            editTextBoldCursor.requestFocus();
            if (!z) {
                return;
            } else {
                runnable = new Runnable() {
                    @Override
                    public final void run() {
                        AndroidUtilities.showKeyboard(EditTextBoldCursor.this);
                    }
                };
            }
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public static void lambda$makeFocusable$8(EditTextBoldCursor editTextBoldCursor) {
        editTextBoldCursor.requestFocus();
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    public static void lambda$new$0() {
        if (VoIPService.getSharedInstance() == null) {
            return;
        }
        VoIPService.getSharedInstance().setMicMute(false, true, false);
    }

    public void lambda$new$1() {
        if (this.call == null || !this.scheduled || VoIPService.getSharedInstance() == null) {
            return;
        }
        try {
            this.muteButton.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        updateMuteButton(1, true);
        AndroidUtilities.runOnUIThread(this.unmuteRunnable, 80L);
        this.scheduled = false;
        this.pressed = true;
    }

    public void lambda$new$10(DialogInterface dialogInterface) {
        BaseFragment baseFragment = (BaseFragment) this.parentActivity.getActionBarLayout().getFragmentStack().get(this.parentActivity.getActionBarLayout().getFragmentStack().size() - 1);
        if (this.anyEnterEventSent && (baseFragment instanceof ChatActivity)) {
            ((ChatActivity) baseFragment).onEditTextDialogClose(true, true);
        }
    }

    public void lambda$new$11(int[] iArr, float[] fArr, boolean[] zArr) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        for (int i = 0; i < iArr.length; i++) {
            TLRPC.TL_groupCallParticipant tL_groupCallParticipant = this.call.participantsBySources.get(iArr[i]);
            if (tL_groupCallParticipant != null) {
                if (this.renderersContainer.inFullscreenMode) {
                    for (int i2 = 0; i2 < this.fullscreenUsersListView.getChildCount(); i2++) {
                        GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = (GroupCallFullscreenAdapter.GroupCallUserCell) this.fullscreenUsersListView.getChildAt(i2);
                        if (MessageObject.getPeerId(groupCallUserCell.getParticipant().peer) == MessageObject.getPeerId(tL_groupCallParticipant.peer)) {
                            groupCallUserCell.setAmplitude(fArr[i] * 15.0f);
                        }
                    }
                } else {
                    int indexOf = (this.delayedGroupCallUpdated ? this.oldParticipants : this.call.visibleParticipants).indexOf(tL_groupCallParticipant);
                    if (indexOf >= 0 && (findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(indexOf + this.listAdapter.usersStartRow)) != null) {
                        View view = findViewHolderForAdapterPosition.itemView;
                        if (view instanceof GroupCallUserCell) {
                            ((GroupCallUserCell) view).setAmplitude(fArr[i] * 15.0f);
                            if (findViewHolderForAdapterPosition.itemView == this.scrimView && !this.contentFullyOverlayed) {
                                this.containerView.invalidate();
                            }
                        }
                    }
                }
                this.renderersContainer.setAmplitude(tL_groupCallParticipant, fArr[i] * 15.0f);
            }
        }
    }

    public void lambda$new$12(DialogInterface dialogInterface) {
        this.groupVoipInviteAlert = null;
    }

    public void lambda$new$13(View view, int i, float f, float f2) {
        if (view instanceof GroupCallGridCell) {
            fullscreenFor(((GroupCallGridCell) view).getParticipant());
            return;
        }
        if (view instanceof GroupCallUserCell) {
            showMenuForCell((GroupCallUserCell) view);
            return;
        }
        if (view instanceof GroupCallInvitedCell) {
            GroupCallInvitedCell groupCallInvitedCell = (GroupCallInvitedCell) view;
            if (groupCallInvitedCell.getUser() == null) {
                return;
            }
            this.parentActivity.switchToAccount(this.currentAccount, true);
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", groupCallInvitedCell.getUser().id);
            if (groupCallInvitedCell.hasAvatarSet()) {
                bundle.putBoolean("expandPhoto", true);
            }
            this.parentActivity.lambda$runLinkRequest$93(new ProfileActivity(bundle));
            dismiss();
            return;
        }
        if (i == this.listAdapter.addMemberRow) {
            if (ChatObject.isChannel(this.currentChat)) {
                TLRPC.Chat chat = this.currentChat;
                if (!chat.megagroup && ChatObject.isPublic(chat)) {
                    getLink(false);
                    return;
                }
            }
            TLRPC.ChatFull chatFull = this.accountInstance.getMessagesController().getChatFull(this.currentChat.id);
            if (chatFull == null) {
                return;
            }
            this.enterEventSent = false;
            Context context = getContext();
            int currentAccount = this.accountInstance.getCurrentAccount();
            TLRPC.Chat chat2 = this.currentChat;
            ChatObject.Call call = this.call;
            GroupVoipInviteAlert groupVoipInviteAlert = new GroupVoipInviteAlert(context, currentAccount, chat2, chatFull, call.participants, call.invitedUsersMap);
            this.groupVoipInviteAlert = groupVoipInviteAlert;
            groupVoipInviteAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    GroupCallActivity.this.lambda$new$12(dialogInterface);
                }
            });
            this.groupVoipInviteAlert.setDelegate(new GroupVoipInviteAlert.GroupVoipInviteAlertDelegate() {
                @Override
                public void copyInviteLink() {
                    GroupCallActivity.this.getLink(true);
                }

                @Override
                public void inviteUser(long j) {
                    GroupCallActivity.this.inviteUserToCall(j, true);
                }

                @Override
                public void needOpenSearch(MotionEvent motionEvent, EditTextBoldCursor editTextBoldCursor) {
                    GroupCallActivity groupCallActivity;
                    GroupVoipInviteAlert groupVoipInviteAlert2;
                    boolean z;
                    if (GroupCallActivity.this.enterEventSent) {
                        return;
                    }
                    if (motionEvent.getX() <= editTextBoldCursor.getLeft() || motionEvent.getX() >= editTextBoldCursor.getRight() || motionEvent.getY() <= editTextBoldCursor.getTop() || motionEvent.getY() >= editTextBoldCursor.getBottom()) {
                        groupCallActivity = GroupCallActivity.this;
                        groupVoipInviteAlert2 = groupCallActivity.groupVoipInviteAlert;
                        z = false;
                    } else {
                        groupCallActivity = GroupCallActivity.this;
                        groupVoipInviteAlert2 = groupCallActivity.groupVoipInviteAlert;
                        z = true;
                    }
                    groupCallActivity.makeFocusable(groupVoipInviteAlert2, null, editTextBoldCursor, z);
                }
            });
            this.groupVoipInviteAlert.show();
        }
    }

    public boolean lambda$new$14(View view, int i) {
        if (isRtmpStream()) {
            return false;
        }
        if (view instanceof GroupCallGridCell) {
            return showMenuForCell(view);
        }
        if (!(view instanceof GroupCallUserCell)) {
            return false;
        }
        updateItems();
        return ((GroupCallUserCell) view).clickMuteButton();
    }

    public void lambda$new$15(View view, int i) {
        GroupCallGridCell groupCallGridCell = (GroupCallGridCell) view;
        if (groupCallGridCell.getParticipant() != null) {
            fullscreenFor(groupCallGridCell.getParticipant());
        }
    }

    public void lambda$new$16(View view) {
        ChatObject.Call call = this.call;
        if (call == null || call.isScheduled() || isRtmpStream()) {
            getLink(false);
        } else {
            if (VoIPService.getSharedInstance() == null) {
                return;
            }
            VoIPService.getSharedInstance().toggleSpeakerphoneOrShowRouteSheet(getContext(), false);
        }
    }

    public void lambda$new$17(View view) {
        RLottieDrawable rLottieDrawable;
        this.renderersContainer.delayHideUi();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            if (sharedInstance.getVideoState(false) == 2) {
                sharedInstance.switchCamera();
                int i = 18;
                if (this.flipIconCurrentEndFrame == 18) {
                    rLottieDrawable = this.flipIcon;
                    i = 39;
                } else {
                    this.flipIcon.setCurrentFrame(0, false);
                    rLottieDrawable = this.flipIcon;
                }
                this.flipIconCurrentEndFrame = i;
                rLottieDrawable.setCustomEndFrame(i);
                this.flipIcon.start();
                for (int i2 = 0; i2 < this.attachedRenderers.size(); i2++) {
                    GroupCallMiniTextureView groupCallMiniTextureView = (GroupCallMiniTextureView) this.attachedRenderers.get(i2);
                    ChatObject.VideoParticipant videoParticipant = groupCallMiniTextureView.participant;
                    if (videoParticipant.participant.self && !videoParticipant.presentation) {
                        groupCallMiniTextureView.startFlipAnimation();
                    }
                }
            }
        }
    }

    public void lambda$new$18(Context context, View view) {
        this.renderersContainer.delayHideUi();
        ChatObject.Call call = this.call;
        if (call == null || call.isScheduled()) {
            dismiss();
        } else {
            updateItems();
            onLeaveClick(context, new Runnable() {
                @Override
                public final void run() {
                    GroupCallActivity.this.dismiss();
                }
            }, false);
        }
    }

    public void lambda$new$19(int i) {
        this.actionBar.getActionBarMenuOnItemClick().onItemClick(i);
    }

    public void lambda$new$20(View view) {
        ActionBarMenuSubItem actionBarMenuSubItem;
        int i;
        String string;
        ChatObject.Call call = this.call;
        if (call == null || this.renderersContainer.inFullscreenMode) {
            return;
        }
        if (call.call.join_muted) {
            ActionBarMenuSubItem actionBarMenuSubItem2 = this.everyoneItem;
            int i2 = Theme.key_voipgroup_actionBarItems;
            actionBarMenuSubItem2.setColors(Theme.getColor(i2), Theme.getColor(i2));
            this.everyoneItem.setChecked(false);
            ActionBarMenuSubItem actionBarMenuSubItem3 = this.adminItem;
            int i3 = Theme.key_voipgroup_checkMenu;
            actionBarMenuSubItem3.setColors(Theme.getColor(i3), Theme.getColor(i3));
            this.adminItem.setChecked(true);
        } else {
            ActionBarMenuSubItem actionBarMenuSubItem4 = this.everyoneItem;
            int i4 = Theme.key_voipgroup_checkMenu;
            actionBarMenuSubItem4.setColors(Theme.getColor(i4), Theme.getColor(i4));
            this.everyoneItem.setChecked(true);
            ActionBarMenuSubItem actionBarMenuSubItem5 = this.adminItem;
            int i5 = Theme.key_voipgroup_actionBarItems;
            actionBarMenuSubItem5.setColors(Theme.getColor(i5), Theme.getColor(i5));
            this.adminItem.setChecked(false);
        }
        this.changingPermissions = false;
        this.otherItem.hideSubItem(1);
        this.otherItem.hideSubItem(2);
        if (VoIPService.getSharedInstance() != null && (VoIPService.getSharedInstance().hasEarpiece() || VoIPService.getSharedInstance().isBluetoothHeadsetConnected())) {
            int currentAudioRoute = VoIPService.getSharedInstance().getCurrentAudioRoute();
            if (currentAudioRoute == 2) {
                this.soundItem.setIcon(R.drawable.msg_voice_bluetooth);
                actionBarMenuSubItem = this.soundItem;
                if (VoIPService.getSharedInstance().currentBluetoothDeviceName != null) {
                    string = VoIPService.getSharedInstance().currentBluetoothDeviceName;
                    actionBarMenuSubItem.setSubtext(string);
                } else {
                    i = R.string.VoipAudioRoutingBluetooth;
                }
            } else if (currentAudioRoute == 0) {
                this.soundItem.setIcon(VoIPService.getSharedInstance().isHeadsetPlugged() ? R.drawable.msg_voice_headphones : R.drawable.msg_voice_phone);
                actionBarMenuSubItem = this.soundItem;
                i = VoIPService.getSharedInstance().isHeadsetPlugged() ? R.string.VoipAudioRoutingHeadset : R.string.VoipAudioRoutingPhone;
            } else if (currentAudioRoute == 1) {
                if (VoipAudioManager.get().isSpeakerphoneOn()) {
                    this.soundItem.setIcon(R.drawable.msg_voice_speaker);
                    actionBarMenuSubItem = this.soundItem;
                    i = R.string.VoipAudioRoutingSpeaker;
                } else {
                    this.soundItem.setIcon(R.drawable.msg_voice_phone);
                    actionBarMenuSubItem = this.soundItem;
                    i = R.string.VoipAudioRoutingPhone;
                }
            }
            string = LocaleController.getString(i);
            actionBarMenuSubItem.setSubtext(string);
        }
        updateItems();
        this.otherItem.toggleSubMenu();
    }

    public void lambda$new$21(View view) {
        if (isRtmpStream()) {
            if (!AndroidUtilities.checkInlinePermissions(this.parentActivity)) {
                AlertsCreator.createDrawOverlayPermissionDialog(this.parentActivity, null).show();
                return;
            } else {
                RTMPStreamPipOverlay.show();
                dismiss();
                return;
            }
        }
        if (!AndroidUtilities.checkInlinePermissions(this.parentActivity)) {
            AlertsCreator.createDrawOverlayGroupCallPermissionDialog(getContext()).show();
        } else {
            GroupCallPip.clearForce();
            dismiss();
        }
    }

    public void lambda$new$22(View view) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null) {
            return;
        }
        if (sharedInstance.getVideoState(true) == 2) {
            sharedInstance.stopScreenCapture();
        } else {
            startScreenCapture();
        }
    }

    public void lambda$new$23(View view) {
        ChatObject.Call call = this.call;
        if (call == null || !call.recording) {
            return;
        }
        showRecordHint(this.actionBar.getTitleTextView());
    }

    public void lambda$new$24(View view, int i) {
        GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = (GroupCallFullscreenAdapter.GroupCallUserCell) view;
        if (groupCallUserCell.getVideoParticipant() == null) {
            fullscreenFor(new ChatObject.VideoParticipant(groupCallUserCell.getParticipant(), false, false));
        } else {
            fullscreenFor(groupCallUserCell.getVideoParticipant());
        }
    }

    public boolean lambda$new$25(View view, int i) {
        if (showMenuForCell(view)) {
            try {
                this.listView.performHapticFeedback(0);
            } catch (Exception unused) {
            }
        }
        return false;
    }

    public void lambda$new$26(ValueAnimator valueAnimator) {
        this.switchToButtonProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateScheduleUI(true);
        this.buttonsContainer.invalidate();
        this.listView.invalidate();
    }

    public void lambda$new$27(TLRPC.Chat chat, TLRPC.InputPeer inputPeer, TLRPC.TL_updateGroupCall tL_updateGroupCall) {
        ChatObject.Call call = new ChatObject.Call();
        this.call = call;
        call.call = new TLRPC.TL_groupCall();
        ChatObject.Call call2 = this.call;
        TLRPC.GroupCall groupCall = call2.call;
        groupCall.participants_count = 0;
        groupCall.version = 1;
        groupCall.can_start_video = true;
        groupCall.can_change_join_muted = true;
        call2.chatId = chat.id;
        groupCall.schedule_date = this.scheduleStartAt;
        groupCall.flags |= 128;
        call2.currentAccount = this.accountInstance;
        call2.setSelfPeer(inputPeer);
        ChatObject.Call call3 = this.call;
        TLRPC.GroupCall groupCall2 = call3.call;
        TLRPC.GroupCall groupCall3 = tL_updateGroupCall.call;
        groupCall2.access_hash = groupCall3.access_hash;
        groupCall2.id = groupCall3.id;
        call3.createNoVideoParticipant();
        this.fullscreenAdapter.setGroupCall(this.call);
        this.renderersContainer.setGroupCall(this.call);
        this.tabletGridAdapter.setGroupCall(this.call);
        MessagesController messagesController = this.accountInstance.getMessagesController();
        ChatObject.Call call4 = this.call;
        messagesController.putGroupCall(call4.chatId, call4);
    }

    public void lambda$new$28(TLRPC.TL_error tL_error) {
        this.accountInstance.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needShowAlert, 6, tL_error.text);
        dismiss();
    }

    public void lambda$new$29(final TLRPC.Chat chat, final TLRPC.InputPeer inputPeer, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tLObject == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    GroupCallActivity.this.lambda$new$28(tL_error);
                }
            });
            return;
        }
        TLRPC.Updates updates = (TLRPC.Updates) tLObject;
        int i = 0;
        while (true) {
            if (i >= updates.updates.size()) {
                break;
            }
            TLRPC.Update update = updates.updates.get(i);
            if (update instanceof TLRPC.TL_updateGroupCall) {
                final TLRPC.TL_updateGroupCall tL_updateGroupCall = (TLRPC.TL_updateGroupCall) update;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        GroupCallActivity.this.lambda$new$27(chat, inputPeer, tL_updateGroupCall);
                    }
                });
                break;
            }
            i++;
        }
        this.accountInstance.getMessagesController().processUpdates(updates, false);
    }

    public void lambda$new$30(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, final TLRPC.Chat chat, AccountInstance accountInstance, final TLRPC.InputPeer inputPeer, View view) {
        AudioPlayerAlert.ClippingTextViewSwitcher clippingTextViewSwitcher;
        int i;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.scheduleAnimator = ofFloat;
        ofFloat.setDuration(600L);
        this.scheduleAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GroupCallActivity.this.lambda$new$26(valueAnimator);
            }
        });
        this.scheduleAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                GroupCallActivity.this.scheduleAnimator = null;
            }
        });
        this.scheduleAnimator.start();
        if (ChatObject.isChannelOrGiga(this.currentChat)) {
            clippingTextViewSwitcher = this.titleTextView;
            i = R.string.VoipChannelVoiceChat;
        } else {
            clippingTextViewSwitcher = this.titleTextView;
            i = R.string.VoipGroupVoiceChat;
        }
        clippingTextViewSwitcher.setText(LocaleController.getString(i), true);
        Calendar calendar = Calendar.getInstance();
        boolean checkScheduleDate = AlertsCreator.checkScheduleDate(null, null, 604800L, 3, numberPicker, numberPicker2, numberPicker3);
        calendar.setTimeInMillis(System.currentTimeMillis() + (numberPicker.getValue() * 86400000));
        calendar.set(11, numberPicker2.getValue());
        calendar.set(12, numberPicker3.getValue());
        if (checkScheduleDate) {
            calendar.set(13, 0);
        }
        this.scheduleStartAt = (int) (calendar.getTimeInMillis() / 1000);
        updateScheduleUI(false);
        TLRPC.TL_phone_createGroupCall tL_phone_createGroupCall = new TLRPC.TL_phone_createGroupCall();
        tL_phone_createGroupCall.peer = MessagesController.getInputPeer(chat);
        tL_phone_createGroupCall.random_id = Utilities.random.nextInt();
        tL_phone_createGroupCall.schedule_date = this.scheduleStartAt;
        tL_phone_createGroupCall.flags |= 2;
        accountInstance.getConnectionsManager().sendRequest(tL_phone_createGroupCall, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                GroupCallActivity.this.lambda$new$29(chat, inputPeer, tLObject, tL_error);
            }
        }, 2);
    }

    public static String lambda$new$31(long j, Calendar calendar, int i, int i2) {
        if (i2 == 0) {
            return LocaleController.getString(R.string.MessageScheduleToday);
        }
        long j2 = j + (i2 * 86400000);
        calendar.setTimeInMillis(j2);
        if (calendar.get(1) != i) {
            return LocaleController.getInstance().getFormatterScheduleYear().format(j2);
        }
        return LocaleController.getInstance().getFormatterWeek().format(j2) + " " + LocaleController.getInstance().getFormatterScheduleDay().format(j2);
    }

    public void lambda$new$32(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i, int i2) {
        try {
            this.container.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        AlertsCreator.checkScheduleDate(this.scheduleButtonTextView, this.scheduleInfoTextView, 604800L, 2, numberPicker, numberPicker2, numberPicker3);
    }

    public static String lambda$new$33(int i) {
        return String.format("%02d", Integer.valueOf(i));
    }

    public static String lambda$new$34(int i) {
        return String.format("%02d", Integer.valueOf(i));
    }

    public void lambda$new$35(Context context, View view) {
        LaunchActivity launchActivity;
        int checkSelfPermission;
        if (Build.VERSION.SDK_INT >= 23 && (launchActivity = this.parentActivity) != null) {
            checkSelfPermission = launchActivity.checkSelfPermission("android.permission.CAMERA");
            if (checkSelfPermission != 0) {
                this.parentActivity.requestPermissions(new String[]{"android.permission.CAMERA"}, 104);
                return;
            }
        }
        if (VoIPService.getSharedInstance() == null) {
            return;
        }
        boolean z = true;
        if (VoIPService.getSharedInstance().getVideoState(false) == 2) {
            VoIPService.getSharedInstance().setVideoState(false, 0);
            updateState(true, false);
            updateSpeakerPhoneIcon(false);
            this.call.sortParticipants();
            applyCallParticipantUpdates(true);
            this.buttonsContainer.requestLayout();
            return;
        }
        this.undoView[0].hide(false, 1);
        if (this.previewDialog == null) {
            VoIPService sharedInstance = VoIPService.getSharedInstance();
            if (sharedInstance != null) {
                sharedInstance.createCaptureDevice(false);
            }
            PrivateVideoPreviewDialog privateVideoPreviewDialog = new PrivateVideoPreviewDialog(context, z, VoIPService.getSharedInstance().getVideoState(true) != 2) {
                @Override
                public void onDismiss(boolean z2, boolean z3) {
                    GroupCallActivity groupCallActivity = GroupCallActivity.this;
                    boolean z4 = groupCallActivity.previewDialog.micEnabled;
                    groupCallActivity.previewDialog = null;
                    VoIPService sharedInstance2 = VoIPService.getSharedInstance();
                    if (!z3) {
                        if (sharedInstance2 != null) {
                            sharedInstance2.setVideoState(false, 0);
                            return;
                        }
                        return;
                    }
                    if (sharedInstance2 != null) {
                        sharedInstance2.setupCaptureDevice(z2, z4);
                    }
                    if (z2 && sharedInstance2 != null) {
                        sharedInstance2.setVideoState(false, 0);
                    }
                    GroupCallActivity.this.updateState(true, false);
                    GroupCallActivity.this.call.sortParticipants();
                    GroupCallActivity.this.applyCallParticipantUpdates(true);
                    GroupCallActivity.this.buttonsContainer.requestLayout();
                }
            };
            this.previewDialog = privateVideoPreviewDialog;
            this.container.addView(privateVideoPreviewDialog);
            if (sharedInstance == null || sharedInstance.isFrontFaceCamera()) {
                return;
            }
            sharedInstance.switchCamera();
        }
    }

    public static void lambda$onLeaveClick$54(CheckBoxCell[] checkBoxCellArr, View view) {
        checkBoxCellArr[((Integer) view.getTag()).intValue()].setChecked(!checkBoxCellArr[r2.intValue()].isChecked(), true);
    }

    public static void lambda$onLeaveClick$55(ChatObject.Call call, CheckBoxCell[] checkBoxCellArr, long j, Runnable runnable, DialogInterface dialogInterface, int i) {
        processOnLeave(call, checkBoxCellArr[0].isChecked(), j, runnable);
    }

    public void lambda$openShareAlert$43(DialogInterface dialogInterface) {
        this.shareAlert = null;
    }

    public void lambda$openShareAlert$44() {
        ShareAlert shareAlert = this.shareAlert;
        if (shareAlert != null) {
            shareAlert.show();
        }
    }

    public void lambda$processSelectedOption$56(TLObject tLObject, DialogInterface dialogInterface, int i) {
        if (tLObject instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) tLObject;
            this.accountInstance.getMessagesController().deleteParticipantFromChat(this.currentChat.id, user);
            getUndoView().showWithAction(0L, 32, user, (Object) null, (Runnable) null, (Runnable) null);
        } else {
            TLRPC.Chat chat = (TLRPC.Chat) tLObject;
            this.accountInstance.getMessagesController().deleteParticipantFromChat(this.currentChat.id, (TLRPC.User) null, chat, false, false);
            getUndoView().showWithAction(0L, 32, chat, (Object) null, (Runnable) null, (Runnable) null);
        }
    }

    public void lambda$processSelectedOption$57(long j) {
        inviteUserToCall(j, false);
    }

    public void lambda$processSelectedOption$58(TLRPC.User user, final long j, DialogInterface dialogInterface, int i) {
        this.accountInstance.getMessagesController().addUserToChat(this.currentChat.id, user, 0, null, (BaseFragment) this.parentActivity.getActionBarLayout().getFragmentStack().get(this.parentActivity.getActionBarLayout().getFragmentStack().size() - 1), new Runnable() {
            @Override
            public final void run() {
                GroupCallActivity.this.lambda$processSelectedOption$57(j);
            }
        });
    }

    public void lambda$processSelectedOption$59() {
        this.accountInstance.getMessagesController().deleteUserPhoto(null);
    }

    public static void lambda$processSelectedOption$60(DialogInterface dialogInterface) {
    }

    public void lambda$runAvatarPreviewTransition$63(float f, float f2, float f3, int i, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.progressToAvatarPreview = floatValue;
        this.renderersContainer.progressToScrimView = floatValue;
        float f4 = (f * (1.0f - floatValue)) + (floatValue * 1.0f);
        this.avatarPreviewContainer.setScaleX(f4);
        this.avatarPreviewContainer.setScaleY(f4);
        this.avatarPreviewContainer.setTranslationX(f2 * (1.0f - this.progressToAvatarPreview));
        this.avatarPreviewContainer.setTranslationY(f3 * (1.0f - this.progressToAvatarPreview));
        if (!this.useBlur) {
            this.scrimPaint.setAlpha((int) (this.progressToAvatarPreview * 100.0f));
        }
        GroupCallMiniTextureView groupCallMiniTextureView = this.scrimRenderer;
        if (groupCallMiniTextureView != null) {
            groupCallMiniTextureView.textureView.setRoundCorners(AndroidUtilities.dp(8.0f) * (1.0f - this.progressToAvatarPreview));
        }
        this.avatarPreviewContainer.invalidate();
        this.containerView.invalidate();
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        int i2 = (int) (i * (1.0f - this.progressToAvatarPreview));
        profileGalleryView.setRoundRadius(i2, i2);
    }

    public void lambda$showMenuForCell$61(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.scrimPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.scrimPopupWindow.dismiss();
        }
    }

    public void lambda$showMenuForCell$62(int i, ArrayList arrayList, TLRPC.TL_groupCallParticipant tL_groupCallParticipant, View view) {
        if (i >= arrayList.size()) {
            return;
        }
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant2 = (TLRPC.TL_groupCallParticipant) this.call.participants.get(MessageObject.getPeerId(tL_groupCallParticipant.peer));
        if (tL_groupCallParticipant2 != null) {
            tL_groupCallParticipant = tL_groupCallParticipant2;
        }
        processSelectedOption(tL_groupCallParticipant, MessageObject.getPeerId(tL_groupCallParticipant.peer), ((Integer) arrayList.get(i)).intValue());
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        } else {
            if (((Integer) arrayList.get(i)).intValue() == 9 || ((Integer) arrayList.get(i)).intValue() == 10 || ((Integer) arrayList.get(i)).intValue() == 11) {
                return;
            }
            dismissAvatarPreview(true);
        }
    }

    public void lambda$toggleAdminSpeak$64(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            this.accountInstance.getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public void lambda$updateItems$4() {
        updateRecordCallText();
        AndroidUtilities.runOnUIThread(this.updateCallRecordRunnable, 1000L);
    }

    public void lambda$updateLiveLabel$50(int i, int i2, boolean z, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.liveLabelPaint.setColor(AndroidUtilities.getOffsetColor(i, i2, floatValue, 1.0f));
        this.liveLabelTextView.invalidate();
        if (!z) {
            floatValue = 1.0f - floatValue;
        }
        this.textureLightningViewAnimatingAlpha = floatValue;
        LightningView lightningView = this.textureLightningView;
        if (lightningView == null || !this.needTextureLightning) {
            return;
        }
        lightningView.invalidate();
    }

    public void lambda$updateMuteButton$51(float f, View view, ValueAnimator valueAnimator) {
        if (!isLandscapeMode && !isRtmpStream()) {
            f = AndroidUtilities.lerp(1.0f, f, this.renderersContainer.progressToFullscreenMode);
        }
        view.setScaleY(f);
    }

    public void lambda$updateMuteButton$52(float f, View view, View view2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (!isLandscapeMode && !isRtmpStream()) {
            f = AndroidUtilities.lerp(1.0f, f, this.renderersContainer.progressToFullscreenMode);
        }
        float f2 = 1.0f - floatValue;
        view.setAlpha(f2);
        float f3 = ((f2 * 0.9f) + 0.1f) * f;
        view.setScaleX(f3);
        view.setScaleY(f3);
        view2.setAlpha(floatValue);
        float f4 = ((floatValue * 0.9f) + 0.1f) * f;
        view2.setScaleX(f4);
        view2.setScaleY(f4);
    }

    public void lambda$updateMuteButton$53(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.muteLabel[0].setAlpha(1.0f - floatValue);
        this.muteLabel[0].setTranslationY(AndroidUtilities.dp(5.0f) * floatValue);
        this.muteLabel[1].setAlpha(floatValue);
        this.muteLabel[1].setTranslationY(AndroidUtilities.dp((floatValue * 5.0f) - 5.0f));
    }

    public void lambda$updateTitle$37(View view) {
        ChatObject.Call call = this.call;
        if (call == null || !call.recording) {
            return;
        }
        showRecordHint(this.actionBar.getTitleTextView());
    }

    public void lambda$updateTitle$38(View view) {
        ChatObject.Call call = this.call;
        if (call == null || !call.recording) {
            return;
        }
        showRecordHint(this.actionBar.getTitleTextView());
    }

    public static void onLeaveClick(Context context, final Runnable runnable, boolean z) {
        int i;
        CheckBoxCell checkBoxCell;
        int i2;
        Window window;
        int i3;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null) {
            return;
        }
        TLRPC.Chat chat = sharedInstance.getChat();
        final ChatObject.Call call = sharedInstance.groupCall;
        final long selfId = sharedInstance.getSelfId();
        if (!ChatObject.canManageCalls(chat)) {
            processOnLeave(call, false, selfId, runnable);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        if (ChatObject.isChannelOrGiga(chat)) {
            builder.setTitle(LocaleController.getString(R.string.VoipChannelLeaveAlertTitle));
            i = R.string.VoipChannelLeaveAlertText;
        } else {
            builder.setTitle(LocaleController.getString(R.string.VoipGroupLeaveAlertTitle));
            i = R.string.VoipGroupLeaveAlertText;
        }
        builder.setMessage(LocaleController.getString(i));
        sharedInstance.getAccount();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        final CheckBoxCell[] checkBoxCellArr = {new CheckBoxCell(context, 1)};
        checkBoxCellArr[0].setBackgroundDrawable(Theme.getSelectorDrawable(false));
        if (z) {
            checkBoxCellArr[0].setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        } else {
            checkBoxCellArr[0].setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
            ((CheckBoxSquare) checkBoxCellArr[0].getCheckBoxView()).setColors(Theme.key_voipgroup_mutedIcon, Theme.key_voipgroup_listeningText, Theme.key_voipgroup_nameText);
        }
        checkBoxCellArr[0].setTag(0);
        if (ChatObject.isChannelOrGiga(chat)) {
            checkBoxCell = checkBoxCellArr[0];
            i2 = R.string.VoipChannelLeaveAlertEndChat;
        } else {
            checkBoxCell = checkBoxCellArr[0];
            i2 = R.string.VoipGroupLeaveAlertEndChat;
        }
        checkBoxCell.setText(LocaleController.getString(i2), "", false, false);
        checkBoxCellArr[0].setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
        linearLayout.addView(checkBoxCellArr[0], LayoutHelper.createLinear(-1, -2));
        checkBoxCellArr[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                GroupCallActivity.lambda$onLeaveClick$54(checkBoxCellArr, view);
            }
        });
        builder.setView(linearLayout);
        builder.setDialogButtonColorKey(Theme.key_voipgroup_listeningText);
        builder.setPositiveButton(LocaleController.getString(R.string.VoipGroupLeave), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i4) {
                GroupCallActivity.lambda$onLeaveClick$55(ChatObject.Call.this, checkBoxCellArr, selfId, runnable, dialogInterface, i4);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        if (z) {
            builder.setDimEnabled(false);
        }
        AlertDialog create = builder.create();
        if (z) {
            if (Build.VERSION.SDK_INT >= 26) {
                window = create.getWindow();
                i3 = 2038;
            } else {
                window = create.getWindow();
                i3 = 2003;
            }
            window.setType(i3);
            create.getWindow().clearFlags(2);
        }
        if (!z) {
            create.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_dialogBackground));
        }
        create.show();
        if (z) {
            return;
        }
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_voipgroup_leaveCallMenu));
        }
        create.setTextColor(Theme.getColor(Theme.key_voipgroup_actionBarItems));
    }

    public void onUserLeaveHint() {
        if (isRtmpStream() && AndroidUtilities.checkInlinePermissions(this.parentActivity) && !RTMPStreamPipOverlay.isVisible()) {
            dismiss();
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    RTMPStreamPipOverlay.show();
                }
            }, 100L);
        }
    }

    private void openShareAlert(boolean r16, java.lang.String r17, java.lang.String r18, boolean r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.openShareAlert(boolean, java.lang.String, java.lang.String, boolean):void");
    }

    private void prepareBlurBitmap() {
        if (this.blurredView == null) {
            return;
        }
        int measuredWidth = (int) ((this.containerView.getMeasuredWidth() - (this.backgroundPaddingLeft * 2)) / 6.0f);
        int measuredHeight = (int) ((this.containerView.getMeasuredHeight() - AndroidUtilities.statusBarHeight) / 6.0f);
        Bitmap createBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        canvas.scale(0.16666667f, 0.16666667f);
        canvas.save();
        canvas.translate(0.0f, -AndroidUtilities.statusBarHeight);
        this.parentActivity.getActionBarLayout().getView().draw(canvas);
        canvas.drawColor(ColorUtils.setAlphaComponent(-16777216, 76));
        canvas.restore();
        canvas.save();
        canvas.translate(this.containerView.getX(), -AndroidUtilities.statusBarHeight);
        this.drawingForBlur = true;
        this.containerView.draw(canvas);
        this.drawingForBlur = false;
        Utilities.stackBlurBitmap(createBitmap, Math.max(7, Math.max(measuredWidth, measuredHeight) / 180));
        this.blurredView.setBackground(new BitmapDrawable(createBitmap));
        this.blurredView.setAlpha(0.0f);
        this.blurredView.setVisibility(0);
    }

    private static void processOnLeave(ChatObject.Call call, boolean z, long j, Runnable runnable) {
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().hangUp(z ? 1 : 0);
        }
        if (call != null) {
            TLRPC.TL_groupCallParticipant tL_groupCallParticipant = (TLRPC.TL_groupCallParticipant) call.participants.get(j);
            if (tL_groupCallParticipant != null) {
                call.participants.delete(j);
                call.sortedParticipants.remove(tL_groupCallParticipant);
                call.visibleParticipants.remove(tL_groupCallParticipant);
                int i = 0;
                while (i < call.visibleVideoParticipants.size()) {
                    if (MessageObject.getPeerId(call.visibleVideoParticipants.get(i).participant.peer) == MessageObject.getPeerId(tL_groupCallParticipant.peer)) {
                        call.visibleVideoParticipants.remove(i);
                        i--;
                    }
                    i++;
                }
                TLRPC.GroupCall groupCall = call.call;
                groupCall.participants_count--;
            }
            for (int i2 = 0; i2 < call.sortedParticipants.size(); i2++) {
                TLRPC.TL_groupCallParticipant tL_groupCallParticipant2 = call.sortedParticipants.get(i2);
                tL_groupCallParticipant2.lastActiveDate = tL_groupCallParticipant2.lastSpeakTime;
            }
        }
        if (runnable != null) {
            runnable.run();
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didStartedCall, new Object[0]);
    }

    public void processSelectedOption(TLRPC.TL_groupCallParticipant tL_groupCallParticipant, final long j, int i) {
        String str;
        String formatString;
        TextView textView;
        Boolean bool;
        Boolean bool2;
        Runnable runnable;
        Boolean bool3;
        Integer num;
        LaunchActivity launchActivity;
        ChatActivity chatActivity;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null) {
            return;
        }
        final TLObject user = j > 0 ? this.accountInstance.getMessagesController().getUser(Long.valueOf(j)) : this.accountInstance.getMessagesController().getChat(Long.valueOf(-j));
        if (i == 0 || i == 2 || i == 3) {
            if (i == 0) {
                if (VoIPService.getSharedInstance() == null) {
                    return;
                }
                VoIPService.getSharedInstance().editCallMember(user, Boolean.TRUE, null, null, null, null);
                getUndoView().showWithAction(0L, 30, user, (Object) null, (Runnable) null, (Runnable) null);
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setDialogButtonColorKey(Theme.key_voipgroup_listeningText);
            TextView textView2 = new TextView(getContext());
            int i2 = Theme.key_voipgroup_actionBarItems;
            textView2.setTextColor(Theme.getColor(i2));
            textView2.setTextSize(1, 16.0f);
            textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
            FrameLayout frameLayout = new FrameLayout(getContext());
            builder.setView(frameLayout);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setTextSize(AndroidUtilities.dp(12.0f));
            BackupImageView backupImageView = new BackupImageView(getContext());
            backupImageView.setRoundRadius(AndroidUtilities.dp(20.0f));
            frameLayout.addView(backupImageView, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 5 : 3) | 48, 22.0f, 5.0f, 22.0f, 0.0f));
            avatarDrawable.setInfo(this.currentAccount, user);
            boolean z = user instanceof TLRPC.User;
            if (z) {
                TLRPC.User user2 = (TLRPC.User) user;
                backupImageView.setForUserOrChat(user2, avatarDrawable);
                str = UserObject.getFirstName(user2);
            } else {
                TLRPC.Chat chat = (TLRPC.Chat) user;
                backupImageView.setForUserOrChat(chat, avatarDrawable);
                str = chat.title;
            }
            TextView textView3 = new TextView(getContext());
            textView3.setTextColor(Theme.getColor(i2));
            textView3.setTextSize(1, 20.0f);
            textView3.setTypeface(AndroidUtilities.bold());
            textView3.setLines(1);
            textView3.setMaxLines(1);
            textView3.setSingleLine(true);
            textView3.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            textView3.setEllipsize(TextUtils.TruncateAt.END);
            if (i == 2) {
                textView3.setText(LocaleController.getString(R.string.VoipGroupRemoveMemberAlertTitle2));
                formatString = ChatObject.isChannelOrGiga(this.currentChat) ? LocaleController.formatString("VoipChannelRemoveMemberAlertText2", R.string.VoipChannelRemoveMemberAlertText2, str, this.currentChat.title) : LocaleController.formatString("VoipGroupRemoveMemberAlertText2", R.string.VoipGroupRemoveMemberAlertText2, str, this.currentChat.title);
            } else {
                textView3.setText(LocaleController.getString(R.string.VoipGroupAddMemberTitle));
                formatString = LocaleController.formatString("VoipGroupAddMemberText", R.string.VoipGroupAddMemberText, str, this.currentChat.title);
            }
            textView2.setText(AndroidUtilities.replaceTags(formatString));
            boolean z2 = LocaleController.isRTL;
            frameLayout.addView(textView3, LayoutHelper.createFrame(-1, -2.0f, (z2 ? 5 : 3) | 48, z2 ? 21 : 76, 11.0f, z2 ? 76 : 21, 0.0f));
            frameLayout.addView(textView2, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 24.0f, 57.0f, 24.0f, 9.0f));
            if (i == 2) {
                builder.setPositiveButton(LocaleController.getString(R.string.VoipGroupUserRemove), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        GroupCallActivity.this.lambda$processSelectedOption$56(user, dialogInterface, i3);
                    }
                });
            } else if (z) {
                final TLRPC.User user3 = (TLRPC.User) user;
                builder.setPositiveButton(LocaleController.getString(R.string.VoipGroupAdd), new DialogInterface.OnClickListener() {
                    @Override
                    public final void onClick(DialogInterface dialogInterface, int i3) {
                        GroupCallActivity.this.lambda$processSelectedOption$58(user3, j, dialogInterface, i3);
                    }
                });
            }
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            AlertDialog create = builder.create();
            create.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_dialogBackground));
            create.show();
            if (i != 2 || (textView = (TextView) create.getButton(-1)) == null) {
                return;
            }
            textView.setTextColor(Theme.getColor(Theme.key_voipgroup_leaveCallMenu));
            return;
        }
        if (i == 6) {
            this.parentActivity.switchToAccount(this.currentAccount, true);
            Bundle bundle = new Bundle();
            if (j > 0) {
                bundle.putLong("user_id", j);
            } else {
                bundle.putLong("chat_id", -j);
            }
            launchActivity = this.parentActivity;
            chatActivity = new ChatActivity(bundle);
        } else {
            if (i != 8) {
                if (i == 7) {
                    sharedInstance.editCallMember(user, Boolean.TRUE, null, null, Boolean.FALSE, null);
                    updateMuteButton(2, true);
                    return;
                }
                if (i == 9) {
                    ImageUpdater imageUpdater = this.currentAvatarUpdater;
                    if (imageUpdater == null || !imageUpdater.isUploadingImage()) {
                        TLRPC.User currentUser = this.accountInstance.getUserConfig().getCurrentUser();
                        ImageUpdater imageUpdater2 = new ImageUpdater(true, 0, true);
                        this.currentAvatarUpdater = imageUpdater2;
                        imageUpdater2.setOpenWithFrontfaceCamera(true);
                        this.currentAvatarUpdater.setForceDarkTheme(true);
                        this.currentAvatarUpdater.setSearchAvailable(true, true);
                        this.currentAvatarUpdater.setShowingFromDialog(true);
                        this.currentAvatarUpdater.parentFragment = this.parentActivity.getActionBarLayout().getLastFragment();
                        ImageUpdater imageUpdater3 = this.currentAvatarUpdater;
                        AvatarUpdaterDelegate avatarUpdaterDelegate = new AvatarUpdaterDelegate(j);
                        this.avatarUpdaterDelegate = avatarUpdaterDelegate;
                        imageUpdater3.setDelegate(avatarUpdaterDelegate);
                        ImageUpdater imageUpdater4 = this.currentAvatarUpdater;
                        TLRPC.UserProfilePhoto userProfilePhoto = currentUser.photo;
                        imageUpdater4.openMenu((userProfilePhoto == null || userProfilePhoto.photo_big == null || (userProfilePhoto instanceof TLRPC.TL_userProfilePhotoEmpty)) ? false : true, new Runnable() {
                            @Override
                            public final void run() {
                                GroupCallActivity.this.lambda$processSelectedOption$59();
                            }
                        }, new DialogInterface.OnDismissListener() {
                            @Override
                            public final void onDismiss(DialogInterface dialogInterface) {
                                GroupCallActivity.lambda$processSelectedOption$60(dialogInterface);
                            }
                        }, 0);
                        return;
                    }
                    return;
                }
                if (i == 10) {
                    AlertsCreator.createChangeBioAlert(tL_groupCallParticipant.about, j, getContext(), this.currentAccount);
                    return;
                }
                if (i == 11) {
                    AlertsCreator.createChangeNameAlert(j, getContext(), this.currentAccount);
                    return;
                }
                if (i == 5) {
                    sharedInstance.editCallMember(user, Boolean.TRUE, null, null, null, null);
                    getUndoView().showWithAction(0L, 35, user);
                    sharedInstance.setParticipantVolume(tL_groupCallParticipant, 0);
                    return;
                }
                if ((tL_groupCallParticipant.flags & 128) == 0 || tL_groupCallParticipant.volume != 0) {
                    bool = Boolean.FALSE;
                    bool2 = null;
                    runnable = null;
                    bool3 = null;
                    num = null;
                } else {
                    tL_groupCallParticipant.volume = 10000;
                    tL_groupCallParticipant.volume_by_admin = false;
                    bool = Boolean.FALSE;
                    num = 10000;
                    bool2 = null;
                    runnable = null;
                    bool3 = null;
                }
                sharedInstance.editCallMember(user, bool, bool3, num, bool2, runnable);
                sharedInstance.setParticipantVolume(tL_groupCallParticipant, ChatObject.getParticipantVolume(tL_groupCallParticipant));
                getUndoView().showWithAction(0L, i == 1 ? 31 : 36, user, (Object) null, (Runnable) null, (Runnable) null);
                return;
            }
            this.parentActivity.switchToAccount(this.currentAccount, true);
            BaseFragment baseFragment = (BaseFragment) this.parentActivity.getActionBarLayout().getFragmentStack().get(this.parentActivity.getActionBarLayout().getFragmentStack().size() - 1);
            if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).getDialogId() == j) {
                dismiss();
                return;
            }
            Bundle bundle2 = new Bundle();
            if (j > 0) {
                bundle2.putLong("user_id", j);
            } else {
                bundle2.putLong("chat_id", -j);
            }
            launchActivity = this.parentActivity;
            chatActivity = new ChatActivity(bundle2);
        }
        launchActivity.lambda$runLinkRequest$93(chatActivity);
        dismiss();
    }

    private void runAvatarPreviewTransition(final boolean r17, org.telegram.ui.Cells.GroupCallUserCell r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.runAvatarPreviewTransition(boolean, org.telegram.ui.Cells.GroupCallUserCell):void");
    }

    public void runUpdateTextureLightningRunnable() {
        AndroidUtilities.cancelRunOnUIThread(this.updateTextureLightningRunnable);
        if (!this.needTextureLightning || this.textureLightningView == null || VoIPService.getSharedInstance() == null || !isRtmpStream() || this.listView == null || !LiteMode.isEnabled(512)) {
            return;
        }
        AndroidUtilities.runOnUIThread(this.updateTextureLightningRunnable, 30L);
    }

    private void setAmplitude(double d) {
        float min = (float) (Math.min(8500.0d, d) / 8500.0d);
        this.animateToAmplitude = min;
        this.animateAmplitudeDiff = (min - this.amplitude) / ((BlobDrawable.AMPLITUDE_SPEED * 500.0f) + 100.0f);
    }

    public void setColorProgress(float f) {
        this.colorProgress = f;
        GroupCallRenderersContainer groupCallRenderersContainer = this.renderersContainer;
        float max = Math.max(f, groupCallRenderersContainer == null ? 0.0f : groupCallRenderersContainer.progressToFullscreenMode);
        int i = Theme.key_voipgroup_actionBarUnscrolled;
        int color = Theme.getColor(i);
        int i2 = Theme.key_voipgroup_actionBar;
        int offsetColor = AndroidUtilities.getOffsetColor(color, Theme.getColor(i2), f, 1.0f);
        this.backgroundColor = offsetColor;
        this.actionBarBackground.setBackgroundColor(offsetColor);
        this.otherItem.redrawPopup(-14472653);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(this.backgroundColor, PorterDuff.Mode.MULTIPLY));
        this.navBarColor = AndroidUtilities.getOffsetColor(Theme.getColor(i), Theme.getColor(i2), max, 1.0f);
        int offsetColor2 = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_listViewBackgroundUnscrolled), Theme.getColor(Theme.key_voipgroup_listViewBackground), f, 1.0f);
        this.listViewBackgroundPaint.setColor(offsetColor2);
        this.listView.setGlowColor(offsetColor2);
        int i3 = this.muteButtonState;
        if (i3 == 3 || isGradientState(i3)) {
            this.muteButton.invalidate();
        }
        View view = this.buttonsBackgroundGradientView;
        if (view != null) {
            int[] iArr = this.gradientColors;
            iArr[0] = this.backgroundColor;
            iArr[1] = 0;
            if (Build.VERSION.SDK_INT > 29) {
                this.buttonsBackgroundGradient.setColors(iArr);
            } else {
                GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, this.gradientColors);
                this.buttonsBackgroundGradient = gradientDrawable;
                view.setBackground(gradientDrawable);
            }
            this.buttonsBackgroundGradientView2.setBackgroundColor(this.gradientColors[0]);
        }
        int offsetColor3 = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_leaveButton), Theme.getColor(Theme.key_voipgroup_leaveButtonScrolled), f, 1.0f);
        this.leaveButton.setBackgroundColor(offsetColor3, offsetColor3);
        int offsetColor4 = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_lastSeenTextUnscrolled), Theme.getColor(Theme.key_voipgroup_lastSeenText), f, 1.0f);
        int offsetColor5 = AndroidUtilities.getOffsetColor(Theme.getColor(Theme.key_voipgroup_mutedIconUnscrolled), Theme.getColor(Theme.key_voipgroup_mutedIcon), f, 1.0f);
        int childCount = this.listView.getChildCount();
        for (int i4 = 0; i4 < childCount; i4++) {
            View childAt = this.listView.getChildAt(i4);
            if (childAt instanceof GroupCallTextCell) {
                ((GroupCallTextCell) childAt).setColors(offsetColor5, offsetColor4);
            } else if (childAt instanceof GroupCallUserCell) {
                ((GroupCallUserCell) childAt).setGrayIconColor(this.actionBar.getTag() != null ? Theme.key_voipgroup_mutedIcon : Theme.key_voipgroup_mutedIconUnscrolled, offsetColor5);
            } else if (childAt instanceof GroupCallInvitedCell) {
                ((GroupCallInvitedCell) childAt).setGrayIconColor(this.actionBar.getTag() != null ? Theme.key_voipgroup_mutedIcon : Theme.key_voipgroup_mutedIconUnscrolled, offsetColor5);
            }
        }
        this.containerView.invalidate();
        this.listView.invalidate();
        this.container.invalidate();
    }

    private void setMicAmplitude(float f) {
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute()) {
            f = 0.0f;
        }
        setAmplitude(4000.0f * f);
        ChatObject.Call call = this.call;
        if (call == null || this.listView == null || (tL_groupCallParticipant = (TLRPC.TL_groupCallParticipant) call.participants.get(MessageObject.getPeerId(this.selfPeer))) == null) {
            return;
        }
        if (this.renderersContainer.inFullscreenMode) {
            for (int i = 0; i < this.fullscreenUsersListView.getChildCount(); i++) {
                GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = (GroupCallFullscreenAdapter.GroupCallUserCell) this.fullscreenUsersListView.getChildAt(i);
                if (MessageObject.getPeerId(groupCallUserCell.getParticipant().peer) == MessageObject.getPeerId(tL_groupCallParticipant.peer)) {
                    groupCallUserCell.setAmplitude(f * 15.0f);
                }
            }
        } else {
            int indexOf = (this.delayedGroupCallUpdated ? this.oldParticipants : this.call.visibleParticipants).indexOf(tL_groupCallParticipant);
            if (indexOf >= 0 && (findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(indexOf + this.listAdapter.usersStartRow)) != null) {
                View view = findViewHolderForAdapterPosition.itemView;
                if (view instanceof GroupCallUserCell) {
                    ((GroupCallUserCell) view).setAmplitude(f * 15.0f);
                    if (findViewHolderForAdapterPosition.itemView == this.scrimView && !this.contentFullyOverlayed) {
                        this.containerView.invalidate();
                    }
                }
            }
        }
        this.renderersContainer.setAmplitude(tL_groupCallParticipant, f * 15.0f);
    }

    private void setScrollOffsetY(float f) {
        int i;
        this.scrollOffsetY = f;
        this.listView.setTopGlowOffset((int) (f - ((FrameLayout.LayoutParams) this.listView.getLayoutParams()).topMargin));
        float dp = f - AndroidUtilities.dp(74.0f);
        if (this.backgroundPaddingTop + dp < ActionBar.getCurrentActionBarHeight() * 2) {
            float min = Math.min(1.0f, (((ActionBar.getCurrentActionBarHeight() * 2) - dp) - this.backgroundPaddingTop) / (((r0 - this.backgroundPaddingTop) - AndroidUtilities.dp(14.0f)) + ActionBar.getCurrentActionBarHeight()));
            i = (int) (AndroidUtilities.dp(AndroidUtilities.isTablet() ? 17.0f : 13.0f) * min);
            LightningView lightningView = this.textureLightningView;
            if (lightningView != null) {
                lightningView.setShadowOffset((int) (AndroidUtilities.dp(8.0f) * min));
            }
            if (Math.abs(Math.min(1.0f, min) - this.colorProgress) > 1.0E-4f) {
                setColorProgress(Math.min(1.0f, min));
            }
            float f2 = 1.0f - ((0.1f * min) * 1.2f);
            this.titleTextView.setScaleX(Math.max(0.9f, f2));
            this.titleTextView.setScaleY(Math.max(0.9f, f2));
            float f3 = 1.0f - (min * 1.2f);
            this.titleTextView.setAlpha(Math.max(0.0f, f3) * (1.0f - this.renderersContainer.progressToFullscreenMode));
            this.liveLabelTextView.setScaleX(Math.max(0.9f, f2));
            this.liveLabelTextView.setScaleY(Math.max(0.9f, f2));
            this.liveLabelTextView.setAlpha(Math.max(0.0f, f3) * (1.0f - this.renderersContainer.progressToFullscreenMode));
        } else {
            this.titleTextView.setScaleX(1.0f);
            this.titleTextView.setScaleY(1.0f);
            this.titleTextView.setAlpha(1.0f - this.renderersContainer.progressToFullscreenMode);
            this.liveLabelTextView.setScaleX(1.0f);
            this.liveLabelTextView.setScaleY(1.0f);
            this.liveLabelTextView.setAlpha(1.0f - this.renderersContainer.progressToFullscreenMode);
            if (this.colorProgress > 1.0E-4f) {
                setColorProgress(0.0f);
            }
            i = 0;
        }
        float f4 = i;
        this.menuItemsContainer.setTranslationY(Math.max(AndroidUtilities.dp(4.0f), (f - AndroidUtilities.dp(53.0f)) - f4));
        this.titleLayout.setTranslationY(Math.max(AndroidUtilities.dp(4.0f), (f - AndroidUtilities.dp(44.0f)) - f4));
        LightningView lightningView2 = this.textureLightningView;
        if (lightningView2 != null) {
            lightningView2.setTranslationY(Math.max(AndroidUtilities.dp(4.0f), f - AndroidUtilities.dp(37.0f)));
        }
        LinearLayout linearLayout = this.scheduleTimerContainer;
        if (linearLayout != null) {
            linearLayout.setTranslationY(Math.max(AndroidUtilities.dp(4.0f), (f - AndroidUtilities.dp(44.0f)) - f4));
        }
        this.containerView.invalidate();
    }

    public boolean showMenuForCell(android.view.View r29) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.showMenuForCell(android.view.View):boolean");
    }

    public void showRecordHint(View view) {
        HintView hintView;
        int i;
        if (this.recordHintView == null) {
            HintView hintView2 = new HintView(getContext(), 8, true);
            this.recordHintView = hintView2;
            hintView2.setAlpha(0.0f);
            this.recordHintView.setVisibility(4);
            this.recordHintView.setShowingDuration(3000L);
            this.containerView.addView(this.recordHintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
            if (ChatObject.isChannelOrGiga(this.currentChat)) {
                hintView = this.recordHintView;
                i = R.string.VoipChannelRecording;
            } else {
                hintView = this.recordHintView;
                i = R.string.VoipGroupRecording;
            }
            hintView.setText(LocaleController.getString(i));
            this.recordHintView.setBackgroundColor(-366530760, -1);
        }
        this.recordHintView.setExtraTranslationY(-AndroidUtilities.statusBarHeight);
        this.recordHintView.showForView(view, true);
    }

    public void showReminderHint() {
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        if (globalMainSettings.getBoolean("reminderhint", false)) {
            return;
        }
        globalMainSettings.edit().putBoolean("reminderhint", true).commit();
        if (this.reminderHintView == null) {
            HintView hintView = new HintView(getContext(), 8);
            this.reminderHintView = hintView;
            hintView.setAlpha(0.0f);
            this.reminderHintView.setVisibility(4);
            this.reminderHintView.setShowingDuration(4000L);
            this.containerView.addView(this.reminderHintView, LayoutHelper.createFrame(-2, -2.0f, 51, 19.0f, 0.0f, 19.0f, 0.0f));
            this.reminderHintView.setText(LocaleController.getString(R.string.VoipChatReminderHint));
            this.reminderHintView.setBackgroundColor(-366530760, -1);
        }
        this.reminderHintView.setExtraTranslationY(-AndroidUtilities.statusBarHeight);
        this.reminderHintView.showForView(this.muteButton, true);
    }

    private void startScreenCapture() {
        Intent createScreenCaptureIntent;
        LaunchActivity launchActivity = this.parentActivity;
        if (launchActivity == null || Build.VERSION.SDK_INT < 21) {
            return;
        }
        MediaProjectionManager m = PrivateVideoPreviewDialog$$ExternalSyntheticApiModelOutline0.m(launchActivity.getSystemService("media_projection"));
        LaunchActivity launchActivity2 = this.parentActivity;
        createScreenCaptureIntent = m.createScreenCaptureIntent();
        launchActivity2.startActivityForResult(createScreenCaptureIntent, 520);
    }

    public void toggleAdminSpeak() {
        TLRPC.TL_phone_toggleGroupCallSettings tL_phone_toggleGroupCallSettings = new TLRPC.TL_phone_toggleGroupCallSettings();
        tL_phone_toggleGroupCallSettings.call = this.call.getInputGroupCall();
        tL_phone_toggleGroupCallSettings.join_muted = this.call.call.join_muted;
        tL_phone_toggleGroupCallSettings.flags |= 1;
        this.accountInstance.getConnectionsManager().sendRequest(tL_phone_toggleGroupCallSettings, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                GroupCallActivity.this.lambda$toggleAdminSpeak$64(tLObject, tL_error);
            }
        });
    }

    public void updateItems() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.updateItems():void");
    }

    public void updateLayout(boolean z) {
        boolean z2 = true;
        int childCount = this.listView.getChildCount();
        float f = 2.1474836E9f;
        for (int i = 0; i < childCount; i++) {
            if (this.listView.getChildAdapterPosition(this.listView.getChildAt(i)) >= 0) {
                f = Math.min(f, r8.getTop());
            }
        }
        if (f < 0.0f || f == 2.1474836E9f) {
            f = childCount != 0 ? 0.0f : this.listView.getPaddingTop();
        }
        final boolean z3 = f <= ((float) (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(14.0f)));
        float currentActionBarHeight = f + ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(14.0f);
        if ((z3 && this.actionBar.getTag() == null) || (!z3 && this.actionBar.getTag() != null)) {
            this.actionBar.setTag(z3 ? 1 : null);
            AnimatorSet animatorSet = this.actionBarAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.actionBarAnimation = null;
            }
            setUseLightStatusBar(this.actionBar.getTag() == null);
            ViewPropertyAnimator duration = this.actionBar.getBackButton().animate().scaleX(z3 ? 1.0f : 0.9f).scaleY(z3 ? 1.0f : 0.9f).translationX(z3 ? 0.0f : -AndroidUtilities.dp(14.0f)).setDuration(300L);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            duration.setInterpolator(cubicBezierInterpolator).start();
            this.actionBar.getTitleTextView().animate().translationY(z3 ? 0.0f : AndroidUtilities.dp(23.0f)).setDuration(300L).setInterpolator(cubicBezierInterpolator).start();
            ObjectAnimator objectAnimator = this.subtitleYAnimator;
            if (objectAnimator != null) {
                objectAnimator.removeAllListeners();
                this.subtitleYAnimator.cancel();
            }
            SimpleTextView subtitleTextView = this.actionBar.getSubtitleTextView();
            Property property = View.TRANSLATION_Y;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(subtitleTextView, (Property<SimpleTextView, Float>) property, this.actionBar.getSubtitleTextView().getTranslationY(), z3 ? 0.0f : AndroidUtilities.dp(20.0f));
            this.subtitleYAnimator = ofFloat;
            ofFloat.setDuration(300L);
            this.subtitleYAnimator.setInterpolator(cubicBezierInterpolator);
            this.subtitleYAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    GroupCallActivity groupCallActivity = GroupCallActivity.this;
                    groupCallActivity.subtitleYAnimator = null;
                    groupCallActivity.actionBar.getSubtitleTextView().setTranslationY(z3 ? 0.0f : AndroidUtilities.dp(20.0f));
                }
            });
            this.subtitleYAnimator.start();
            ObjectAnimator objectAnimator2 = this.additionalSubtitleYAnimator;
            if (objectAnimator2 != null) {
                objectAnimator2.cancel();
            }
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(this.actionBar.getAdditionalSubtitleTextView(), (Property<SimpleTextView, Float>) property, z3 ? 0.0f : AndroidUtilities.dp(20.0f));
            this.additionalSubtitleYAnimator = ofFloat2;
            ofFloat2.setDuration(300L);
            this.additionalSubtitleYAnimator.setInterpolator(cubicBezierInterpolator);
            this.additionalSubtitleYAnimator.start();
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionBarAnimation = animatorSet2;
            animatorSet2.setDuration(140L);
            AnimatorSet animatorSet3 = this.actionBarAnimation;
            ActionBar actionBar = this.actionBar;
            Property property2 = View.ALPHA;
            animatorSet3.playTogether(ObjectAnimator.ofFloat(actionBar, (Property<ActionBar, Float>) property2, z3 ? 1.0f : 0.0f), ObjectAnimator.ofFloat(this.actionBarBackground, (Property<View, Float>) property2, z3 ? 1.0f : 0.0f), ObjectAnimator.ofFloat(this.actionBarShadow, (Property<View, Float>) property2, z3 ? 1.0f : 0.0f));
            this.actionBarAnimation.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    GroupCallActivity.this.actionBarAnimation = null;
                }
            });
            this.actionBarAnimation.start();
            ImageView imageView = this.renderersContainer.pipView;
            if (z3 && !isLandscapeMode) {
                z2 = false;
            }
            imageView.setClickable(z2);
        }
        if (this.scrollOffsetY != currentActionBarHeight) {
            setScrollOffsetY(currentActionBarHeight);
        }
    }

    private void updateLiveLabel(boolean z) {
        if (!isRtmpStream()) {
            this.liveLabelTextView.setVisibility(8);
            return;
        }
        this.liveLabelTextView.setVisibility(0);
        boolean z2 = ((Integer) this.liveLabelTextView.getTag()).intValue() == 3;
        int i = this.currentCallState;
        final boolean z3 = i == 3;
        this.liveLabelTextView.setTag(Integer.valueOf(i));
        if (z2 != z3) {
            ValueAnimator valueAnimator = this.liveLabelBgColorAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (!z) {
                this.liveLabelPaint.setColor(this.currentCallState == 3 ? -1163700 : -12761513);
                this.liveLabelTextView.invalidate();
                return;
            }
            final int color = this.liveLabelPaint.getColor();
            final int i2 = z3 ? -1163700 : -12761513;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.liveLabelBgColorAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    GroupCallActivity.this.lambda$updateLiveLabel$50(color, i2, z3, valueAnimator2);
                }
            });
            this.liveLabelBgColorAnimator.setDuration(300L);
            this.liveLabelBgColorAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.liveLabelBgColorAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    GroupCallActivity.this.liveLabelBgColorAnimator = null;
                    GroupCallActivity.this.liveLabelPaint.setColor(GroupCallActivity.this.currentCallState == 3 ? -1163700 : -12761513);
                    GroupCallActivity.this.liveLabelTextView.invalidate();
                }
            });
            this.liveLabelBgColorAnimator.start();
        }
    }

    public void updateMuteButton(int r20, boolean r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.updateMuteButton(int, boolean):void");
    }

    private void updateMuteButtonState(boolean z) {
        boolean z2;
        WeavingState weavingState;
        Shader radialGradient;
        this.muteButton.invalidate();
        WeavingState[] weavingStateArr = this.states;
        int i = this.muteButtonState;
        if (weavingStateArr[i] == null) {
            weavingStateArr[i] = new WeavingState(i);
            int i2 = this.muteButtonState;
            if (i2 == 3) {
                this.states[i2].shader = null;
            } else {
                if (isGradientState(i2)) {
                    weavingState = this.states[this.muteButtonState];
                    radialGradient = new LinearGradient(0.0f, 400.0f, 400.0f, 0.0f, new int[]{Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient), Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient3), Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient2)}, (float[]) null, Shader.TileMode.CLAMP);
                } else {
                    int i3 = this.muteButtonState;
                    WeavingState[] weavingStateArr2 = this.states;
                    if (i3 == 1) {
                        weavingState = weavingStateArr2[i3];
                        radialGradient = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor(Theme.key_voipgroup_muteButton), Theme.getColor(Theme.key_voipgroup_muteButton3)}, (float[]) null, Shader.TileMode.CLAMP);
                    } else {
                        weavingState = weavingStateArr2[i3];
                        radialGradient = new RadialGradient(200.0f, 200.0f, 200.0f, new int[]{Theme.getColor(Theme.key_voipgroup_unmuteButton2), Theme.getColor(Theme.key_voipgroup_unmuteButton)}, (float[]) null, Shader.TileMode.CLAMP);
                    }
                }
                weavingState.shader = radialGradient;
            }
        }
        WeavingState weavingState2 = this.states[this.muteButtonState];
        WeavingState weavingState3 = this.currentState;
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
        if (!z) {
            WeavingState weavingState4 = this.currentState;
            boolean z3 = false;
            if (weavingState4 != null) {
                int i4 = weavingState4.currentState;
                boolean z4 = i4 == 1 || i4 == 0;
                z2 = i4 != 3;
                z3 = z4;
            } else {
                z2 = false;
            }
            this.showWavesProgress = z3 ? 1.0f : 0.0f;
            this.showLightingProgress = z2 ? 1.0f : 0.0f;
        }
        this.buttonsContainer.invalidate();
    }

    private void updateRecordCallText() {
        if (this.call == null) {
            return;
        }
        int currentTime = this.accountInstance.getConnectionsManager().getCurrentTime();
        ChatObject.Call call = this.call;
        int i = currentTime - call.call.record_start_date;
        if (call.recording) {
            this.recordItem.setSubtext(AndroidUtilities.formatDuration(i, false));
        } else {
            this.recordItem.setSubtext(null);
        }
    }

    private void updateScheduleUI(boolean z) {
        float interpolation;
        float f;
        ActionBarMenuSubItem actionBarMenuSubItem;
        int i;
        LinearLayout linearLayout = this.scheduleTimerContainer;
        if ((linearLayout == null || this.call != null) && this.scheduleAnimator == null) {
            this.scheduleButtonsScale = 1.0f;
            this.switchToButtonInt2 = 1.0f;
            this.switchToButtonProgress = 1.0f;
            if (linearLayout == null) {
                return;
            }
        }
        if (!z) {
            AndroidUtilities.cancelRunOnUIThread(this.updateSchedeulRunnable);
            this.updateSchedeulRunnable.run();
            ChatObject.Call call = this.call;
            if (call == null || call.isScheduled()) {
                this.listView.setVisibility(4);
            } else {
                this.listView.setVisibility(0);
            }
            if (ChatObject.isChannelOrGiga(this.currentChat)) {
                actionBarMenuSubItem = this.leaveItem;
                i = R.string.VoipChannelCancelChat;
            } else {
                actionBarMenuSubItem = this.leaveItem;
                i = R.string.VoipGroupCancelChat;
            }
            actionBarMenuSubItem.setText(LocaleController.getString(i));
        }
        float f2 = this.switchToButtonProgress;
        if (f2 > 0.6f) {
            interpolation = 1.05f - (CubicBezierInterpolator.DEFAULT.getInterpolation((f2 - 0.6f) / 0.4f) * 0.05f);
            this.scheduleButtonsScale = interpolation;
            this.switchToButtonInt2 = 1.0f;
            f = 1.0f;
        } else {
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.DEFAULT;
            this.scheduleButtonsScale = (cubicBezierInterpolator.getInterpolation(f2 / 0.6f) * 0.05f) + 1.0f;
            this.switchToButtonInt2 = cubicBezierInterpolator.getInterpolation(this.switchToButtonProgress / 0.6f);
            interpolation = 1.05f * cubicBezierInterpolator.getInterpolation(this.switchToButtonProgress / 0.6f);
            f = this.switchToButtonProgress / 0.6f;
        }
        float dp = isLandscapeMode ? (AndroidUtilities.dp(52.0f) * interpolation) / (this.muteButton.getMeasuredWidth() - AndroidUtilities.dp(8.0f)) : interpolation;
        float f3 = 1.0f - f;
        this.leaveButton.setAlpha(f);
        VoIPToggleButton voIPToggleButton = this.soundButton;
        voIPToggleButton.setAlpha((voIPToggleButton.isEnabled() ? 1.0f : 0.5f) * f);
        this.muteButton.setAlpha(f);
        this.scheduleTimerContainer.setAlpha(f3);
        this.scheduleStartInTextView.setAlpha(f);
        this.scheduleStartAtTextView.setAlpha(f);
        this.scheduleTimeTextView.setAlpha(f);
        this.muteLabel[0].setAlpha(f);
        this.scheduleTimeTextView.setScaleX(interpolation);
        this.scheduleTimeTextView.setScaleY(interpolation);
        this.leaveButton.setScaleX(interpolation);
        this.leaveButton.setScaleY(interpolation);
        this.soundButton.setScaleX(interpolation);
        this.soundButton.setScaleY(interpolation);
        this.muteButton.setScaleX(dp);
        this.muteButton.setScaleY(dp);
        this.scheduleButtonTextView.setScaleX(f3);
        this.scheduleButtonTextView.setScaleY(f3);
        this.scheduleButtonTextView.setAlpha(f3);
        this.scheduleInfoTextView.setAlpha(f3);
        this.cameraButton.setAlpha(f);
        this.cameraButton.setScaleY(interpolation);
        this.cameraButton.setScaleX(interpolation);
        this.flipButton.setAlpha(f);
        this.flipButton.setScaleY(interpolation);
        this.flipButton.setScaleX(interpolation);
        this.otherItem.setAlpha(f);
        int i2 = f3 != 0.0f ? 0 : 4;
        if (i2 != this.scheduleTimerContainer.getVisibility()) {
            this.scheduleTimerContainer.setVisibility(i2);
            this.scheduleButtonTextView.setVisibility(i2);
        }
    }

    private void updateSpeakerPhoneIcon(boolean z) {
        VoIPToggleButton voIPToggleButton;
        int i;
        int i2;
        String string;
        boolean z2;
        boolean z3;
        int i3;
        int i4;
        float f;
        VoIPToggleButton voIPToggleButton2 = this.soundButton;
        if (voIPToggleButton2 == null || voIPToggleButton2.getVisibility() != 0) {
            return;
        }
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        boolean z4 = false;
        if (sharedInstance == null || isRtmpStream()) {
            this.soundButton.setData(R.drawable.msg_voiceshare, -1, 0, 0.3f, true, LocaleController.getString(R.string.VoipChatShare), false, z);
            this.soundButton.setEnabled(ChatObject.isPublic(this.currentChat) || (ChatObject.hasAdminRights(this.currentChat) && ChatObject.canAddUsers(this.currentChat)), false);
            this.soundButton.setChecked(true, false);
            return;
        }
        this.soundButton.setEnabled(true, z);
        boolean z5 = sharedInstance.isBluetoothOn() || sharedInstance.isBluetoothWillOn();
        if (!z5 && sharedInstance.isSpeakerphoneOn()) {
            z4 = true;
        }
        if (z5) {
            voIPToggleButton = this.soundButton;
            i = R.drawable.calls_bluetooth;
            i2 = R.string.VoipAudioRoutingBluetooth;
        } else {
            if (z4) {
                voIPToggleButton = this.soundButton;
                i = R.drawable.calls_speaker;
                string = LocaleController.getString(R.string.VoipSpeaker);
                z2 = true;
                z3 = false;
                i3 = -1;
                i4 = 0;
                f = 0.3f;
                voIPToggleButton.setData(i, i3, i4, f, z2, string, z3, z);
                this.soundButton.setChecked(z4, z);
            }
            if (sharedInstance.isHeadsetPlugged()) {
                voIPToggleButton = this.soundButton;
                i = R.drawable.calls_headphones;
                i2 = R.string.VoipAudioRoutingHeadset;
            } else {
                voIPToggleButton = this.soundButton;
                i = R.drawable.calls_speaker;
                i2 = R.string.VoipSpeaker;
            }
        }
        string = LocaleController.getString(i2);
        z2 = true;
        z3 = false;
        i3 = -1;
        i4 = 0;
        f = 0.1f;
        voIPToggleButton.setData(i, i3, i4, f, z2, string, z3, z);
        this.soundButton.setChecked(z4, z);
    }

    public void updateState(boolean r36, boolean r37) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.updateState(boolean, boolean):void");
    }

    public void updateSubtitle() {
        boolean z;
        WatchersView watchersView;
        String str;
        TypefaceSpan typefaceSpan;
        if (this.actionBar == null || this.call == null) {
            return;
        }
        SpannableStringBuilder spannableStringBuilder = null;
        int i = 0;
        for (int i2 = 0; i2 < this.call.currentSpeakingPeers.size(); i2++) {
            long keyAt = this.call.currentSpeakingPeers.keyAt(i2);
            TLRPC.TL_groupCallParticipant tL_groupCallParticipant = (TLRPC.TL_groupCallParticipant) this.call.currentSpeakingPeers.get(keyAt);
            if (!tL_groupCallParticipant.self && !this.renderersContainer.isVisible(tL_groupCallParticipant) && this.visiblePeerIds.get(keyAt, 0) != 1) {
                long peerId = MessageObject.getPeerId(tL_groupCallParticipant.peer);
                if (spannableStringBuilder == null) {
                    spannableStringBuilder = new SpannableStringBuilder();
                }
                if (i < 2) {
                    TLRPC.User user = peerId > 0 ? MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(peerId)) : null;
                    TLRPC.Chat chat = peerId <= 0 ? MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(peerId)) : null;
                    if (user != null || chat != null) {
                        if (i != 0) {
                            spannableStringBuilder.append((CharSequence) ", ");
                        }
                        if (user == null) {
                            int i3 = Build.VERSION.SDK_INT;
                            str = chat.title;
                            if (i3 >= 21) {
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
                i++;
                if (i == 2) {
                    break;
                }
            }
        }
        if (i > 0) {
            String pluralString = LocaleController.getPluralString("MembersAreSpeakingToast", i);
            int indexOf = pluralString.indexOf("un1");
            SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(pluralString);
            spannableStringBuilder2.replace(indexOf, indexOf + 3, (CharSequence) spannableStringBuilder);
            this.actionBar.getAdditionalSubtitleTextView().setText(spannableStringBuilder2);
            z = true;
        } else {
            z = false;
        }
        this.actionBar.getSubtitleTextView().setText(LocaleController.formatPluralString(isRtmpStream() ? "ViewersWatching" : "Participants", this.call.call.participants_count + (this.listAdapter.addSelfToCounter() ? 1 : 0), new Object[0]));
        if (isRtmpStream() && (watchersView = this.watchersView) != null) {
            watchersView.setWatchersCount(this.call.call.participants_count);
        }
        if (z != this.drawSpeakingSubtitle) {
            this.drawSpeakingSubtitle = z;
            this.actionBar.invalidate();
            this.actionBar.getSubtitleTextView().setPivotX(0.0f);
            this.actionBar.getSubtitleTextView().setPivotY(this.actionBar.getMeasuredHeight() >> 1);
            this.actionBar.getSubtitleTextView().animate().scaleX(this.drawSpeakingSubtitle ? 0.98f : 1.0f).scaleY(this.drawSpeakingSubtitle ? 0.9f : 1.0f).alpha(this.drawSpeakingSubtitle ? 0.0f : 1.0f).setDuration(150L);
            AndroidUtilities.updateViewVisibilityAnimated(this.actionBar.getAdditionalSubtitleTextView(), this.drawSpeakingSubtitle);
        }
    }

    private void updateTitle(boolean r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.GroupCallActivity.updateTitle(boolean):void");
    }

    private void updateVideoParticipantList() {
        this.visibleVideoParticipants.clear();
        if (!isTabletMode) {
            this.visibleVideoParticipants.addAll(this.call.visibleVideoParticipants);
            return;
        }
        if (this.renderersContainer.inFullscreenMode) {
            this.visibleVideoParticipants.addAll(this.call.visibleVideoParticipants);
            ChatObject.VideoParticipant videoParticipant = this.renderersContainer.fullscreenParticipant;
            if (videoParticipant != null) {
                this.visibleVideoParticipants.remove(videoParticipant);
            }
        }
    }

    @Override
    public boolean canDismissWithSwipe() {
        return false;
    }

    @Override
    protected boolean canDismissWithTouchOutside() {
        return !this.renderersContainer.inFullscreenMode;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant;
        TLRPC.TL_groupCallParticipant tL_groupCallParticipant2;
        int i3;
        String str;
        ChatObject.VideoParticipant videoParticipant;
        TLRPC.Chat chat;
        int i4;
        int i5;
        int i6 = 0;
        if (i == NotificationCenter.groupCallUpdated) {
            Long l = (Long) objArr[1];
            ChatObject.Call call = this.call;
            if (call == null || call.call.id != l.longValue()) {
                return;
            }
            ChatObject.Call call2 = this.call;
            if (!(call2.call instanceof TLRPC.TL_groupCallDiscarded)) {
                if (this.creatingServiceTime == 0 && (((i5 = this.muteButtonState) == 7 || i5 == 5 || i5 == 6) && !call2.isScheduled())) {
                    try {
                        Intent intent = new Intent(this.parentActivity, (Class<?>) VoIPService.class);
                        intent.putExtra("chat_id", this.currentChat.id);
                        intent.putExtra("createGroupCall", false);
                        intent.putExtra("hasFewPeers", this.scheduleHasFewPeers);
                        intent.putExtra("peerChannelId", this.schedulePeer.channel_id);
                        intent.putExtra("peerChatId", this.schedulePeer.chat_id);
                        intent.putExtra("peerUserId", this.schedulePeer.user_id);
                        intent.putExtra("hash", this.scheduledHash);
                        intent.putExtra("peerAccessHash", this.schedulePeer.access_hash);
                        intent.putExtra("is_outgoing", true);
                        intent.putExtra("start_incall_activity", false);
                        intent.putExtra("account", this.accountInstance.getCurrentAccount());
                        intent.putExtra("scheduleDate", this.scheduleStartAt);
                        this.parentActivity.startService(intent);
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                    this.creatingServiceTime = SystemClock.elapsedRealtime();
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            GroupCallActivity.this.lambda$didReceivedNotification$2();
                        }
                    }, 3000L);
                }
                if (!this.callInitied && VoIPService.getSharedInstance() != null) {
                    this.call.addSelfDummyParticipant(false);
                    initCreatedGroupCall();
                    VoIPService.getSharedInstance().playConnectedSound();
                }
                updateItems();
                int childCount = this.listView.getChildCount();
                for (int i7 = 0; i7 < childCount; i7++) {
                    View childAt = this.listView.getChildAt(i7);
                    if (childAt instanceof GroupCallUserCell) {
                        ((GroupCallUserCell) childAt).applyParticipantChanges(true);
                    }
                }
                if (this.scrimView != null) {
                    this.delayedGroupCallUpdated = true;
                } else {
                    applyCallParticipantUpdates(true);
                }
                updateSubtitle();
                boolean booleanValue = ((Boolean) objArr[2]).booleanValue();
                boolean z = this.muteButtonState == 4;
                updateState(true, booleanValue);
                updateTitle(true);
                if (z && ((i4 = this.muteButtonState) == 1 || i4 == 0)) {
                    getUndoView().showWithAction(0L, 38, (Runnable) null);
                    if (VoIPService.getSharedInstance() != null) {
                        VoIPService.getSharedInstance().playAllowTalkSound();
                    }
                }
                if (objArr.length >= 4) {
                    Long l2 = (Long) objArr[3];
                    long longValue = l2.longValue();
                    if (longValue == 0 || isRtmpStream()) {
                        return;
                    }
                    try {
                        ArrayList<TLRPC.Dialog> allDialogs = this.accountInstance.getMessagesController().getAllDialogs();
                        if (allDialogs != null) {
                            Iterator<TLRPC.Dialog> it = allDialogs.iterator();
                            while (true) {
                                if (it.hasNext()) {
                                    if (it.next().id == longValue) {
                                        i6 = 1;
                                        break;
                                    }
                                } else {
                                    break;
                                }
                            }
                        }
                    } catch (Exception unused) {
                    }
                    if (DialogObject.isUserDialog(longValue)) {
                        TLRPC.User user = this.accountInstance.getMessagesController().getUser(l2);
                        if (user == 0) {
                            return;
                        }
                        chat = user;
                        if (this.call.call.participants_count >= 250) {
                            boolean isContact = UserObject.isContact(user);
                            chat = user;
                            if (!isContact) {
                                boolean z2 = user.verified;
                                chat = user;
                                chat = user;
                                if (!z2 && i6 == 0) {
                                    return;
                                }
                            }
                        }
                    } else {
                        TLRPC.Chat chat2 = this.accountInstance.getMessagesController().getChat(Long.valueOf(-longValue));
                        if (chat2 == null) {
                            return;
                        }
                        chat = chat2;
                        if (this.call.call.participants_count >= 250) {
                            boolean isNotInChat = ChatObject.isNotInChat(chat2);
                            chat = chat2;
                            if (isNotInChat) {
                                boolean z3 = chat2.verified;
                                chat = chat2;
                                chat = chat2;
                                if (!z3 && i6 == 0) {
                                    return;
                                }
                            }
                        }
                    }
                    getUndoView().showWithAction(0L, 44, chat, this.currentChat, (Runnable) null, (Runnable) null);
                    return;
                }
                return;
            }
        } else {
            if (i == NotificationCenter.groupCallSpeakingUsersUpdated) {
                GroupCallRenderersContainer groupCallRenderersContainer = this.renderersContainer;
                if (groupCallRenderersContainer.inFullscreenMode && this.call != null) {
                    boolean autoPinEnabled = groupCallRenderersContainer.autoPinEnabled();
                    ChatObject.Call call3 = this.call;
                    if (call3 != null) {
                        GroupCallRenderersContainer groupCallRenderersContainer2 = this.renderersContainer;
                        if (groupCallRenderersContainer2.inFullscreenMode && (videoParticipant = groupCallRenderersContainer2.fullscreenParticipant) != null && call3.participants.get(MessageObject.getPeerId(videoParticipant.participant.peer)) == null) {
                            autoPinEnabled = true;
                        }
                    }
                    if (autoPinEnabled) {
                        ChatObject.VideoParticipant videoParticipant2 = null;
                        for (int i8 = 0; i8 < this.visibleVideoParticipants.size(); i8++) {
                            ChatObject.VideoParticipant videoParticipant3 = (ChatObject.VideoParticipant) this.visibleVideoParticipants.get(i8);
                            if (this.call.currentSpeakingPeers.get(MessageObject.getPeerId(videoParticipant3.participant.peer), null) != null) {
                                TLRPC.TL_groupCallParticipant tL_groupCallParticipant3 = videoParticipant3.participant;
                                if (!tL_groupCallParticipant3.muted_by_you && this.renderersContainer.fullscreenPeerId != MessageObject.getPeerId(tL_groupCallParticipant3.peer)) {
                                    videoParticipant2 = videoParticipant3;
                                }
                            }
                        }
                        if (videoParticipant2 != null) {
                            fullscreenFor(videoParticipant2);
                        }
                    }
                }
                this.renderersContainer.setVisibleParticipant(true);
                updateSubtitle();
                return;
            }
            if (i == NotificationCenter.webRtcMicAmplitudeEvent) {
                setMicAmplitude(((Float) objArr[0]).floatValue());
                return;
            }
            if (i == NotificationCenter.needShowAlert) {
                if (((Integer) objArr[0]).intValue() != 6) {
                    return;
                }
                String str2 = (String) objArr[1];
                try {
                    if ("GROUPCALL_PARTICIPANTS_TOO_MUCH".equals(str2)) {
                        i3 = ChatObject.isChannelOrGiga(this.currentChat) ? R.string.VoipChannelTooMuch : R.string.VoipGroupTooMuch;
                    } else {
                        if (!"ANONYMOUS_CALLS_DISABLED".equals(str2) && !"GROUPCALL_ANONYMOUS_FORBIDDEN".equals(str2)) {
                            str = LocaleController.getString(R.string.ErrorOccurred) + "\n" + str2;
                            AlertDialog.Builder createSimpleAlert = AlertsCreator.createSimpleAlert(getContext(), LocaleController.getString(R.string.VoipGroupVoiceChat), str);
                            createSimpleAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public final void onDismiss(DialogInterface dialogInterface) {
                                    GroupCallActivity.this.lambda$didReceivedNotification$3(dialogInterface);
                                }
                            });
                            createSimpleAlert.show();
                            return;
                        }
                        i3 = ChatObject.isChannelOrGiga(this.currentChat) ? R.string.VoipChannelJoinAnonymousAdmin : R.string.VoipGroupJoinAnonymousAdmin;
                    }
                    createSimpleAlert.show();
                    return;
                } catch (Exception e) {
                    FileLog.e(e);
                    return;
                }
                str = LocaleController.getString(i3);
                AlertDialog.Builder createSimpleAlert2 = AlertsCreator.createSimpleAlert(getContext(), LocaleController.getString(R.string.VoipGroupVoiceChat), str);
                createSimpleAlert2.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public final void onDismiss(DialogInterface dialogInterface) {
                        GroupCallActivity.this.lambda$didReceivedNotification$3(dialogInterface);
                    }
                });
            } else {
                if (i != NotificationCenter.didEndCall) {
                    if (i == NotificationCenter.chatInfoDidLoad) {
                        TLRPC.ChatFull chatFull = (TLRPC.ChatFull) objArr[0];
                        if (chatFull.id == this.currentChat.id) {
                            updateItems();
                            updateState(isShowing(), false);
                        }
                        long peerId = MessageObject.getPeerId(this.selfPeer);
                        ChatObject.Call call4 = this.call;
                        if (call4 == null || chatFull.id != (-peerId) || (tL_groupCallParticipant2 = (TLRPC.TL_groupCallParticipant) call4.participants.get(peerId)) == null) {
                            return;
                        }
                        tL_groupCallParticipant2.about = chatFull.about;
                        applyCallParticipantUpdates(true);
                        AndroidUtilities.updateVisibleRows(this.listView);
                        if (this.currentOptionsLayout != null) {
                            while (i6 < this.currentOptionsLayout.getChildCount()) {
                                View childAt2 = this.currentOptionsLayout.getChildAt(i6);
                                if ((childAt2 instanceof ActionBarMenuSubItem) && childAt2.getTag() != null && ((Integer) childAt2.getTag()).intValue() == 10) {
                                    ((ActionBarMenuSubItem) childAt2).setTextAndIcon(LocaleController.getString(TextUtils.isEmpty(tL_groupCallParticipant2.about) ? R.string.VoipAddDescription : R.string.VoipEditDescription), TextUtils.isEmpty(tL_groupCallParticipant2.about) ? R.drawable.msg_addbio : R.drawable.msg_info);
                                }
                                i6++;
                            }
                            return;
                        }
                        return;
                    }
                    if (i == NotificationCenter.didLoadChatAdmins) {
                        if (((Long) objArr[0]).longValue() == this.currentChat.id) {
                            updateItems();
                            updateState(isShowing(), false);
                            return;
                        }
                        return;
                    }
                    if (i == NotificationCenter.applyGroupCallVisibleParticipants) {
                        int childCount2 = this.listView.getChildCount();
                        long longValue2 = ((Long) objArr[0]).longValue();
                        while (i6 < childCount2) {
                            RecyclerView.ViewHolder findContainingViewHolder = this.listView.findContainingViewHolder(this.listView.getChildAt(i6));
                            if (findContainingViewHolder != null) {
                                View view = findContainingViewHolder.itemView;
                                if (view instanceof GroupCallUserCell) {
                                    GroupCallUserCell groupCallUserCell = (GroupCallUserCell) view;
                                    if (groupCallUserCell.getParticipant() != null) {
                                        groupCallUserCell.getParticipant().lastVisibleDate = longValue2;
                                    }
                                }
                            }
                            i6++;
                        }
                        return;
                    }
                    if (i != NotificationCenter.userInfoDidLoad) {
                        if (i == NotificationCenter.mainUserInfoChanged) {
                            applyCallParticipantUpdates(true);
                        } else {
                            if (i != NotificationCenter.updateInterfaces) {
                                if (i == NotificationCenter.groupCallScreencastStateChanged) {
                                    PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
                                    if (privateVideoPreviewDialog != null) {
                                        privateVideoPreviewDialog.dismiss(true, true);
                                    }
                                    updateItems();
                                    return;
                                }
                                return;
                            }
                            int intValue = ((Integer) objArr[0]).intValue();
                            if ((MessagesController.UPDATE_MASK_CHAT_NAME & intValue) != 0) {
                                applyCallParticipantUpdates(true);
                            }
                            if ((MessagesController.UPDATE_MASK_CHAT_NAME & intValue) == 0 && (intValue & MessagesController.UPDATE_MASK_EMOJI_STATUS) == 0) {
                                return;
                            }
                        }
                        AndroidUtilities.updateVisibleRows(this.listView);
                        return;
                    }
                    Long l3 = (Long) objArr[0];
                    long peerId2 = MessageObject.getPeerId(this.selfPeer);
                    if (this.call == null || peerId2 != l3.longValue() || (tL_groupCallParticipant = (TLRPC.TL_groupCallParticipant) this.call.participants.get(peerId2)) == null) {
                        return;
                    }
                    tL_groupCallParticipant.about = ((TLRPC.UserFull) objArr[1]).about;
                    applyCallParticipantUpdates(true);
                    AndroidUtilities.updateVisibleRows(this.listView);
                    if (this.currentOptionsLayout != null) {
                        while (i6 < this.currentOptionsLayout.getChildCount()) {
                            View childAt3 = this.currentOptionsLayout.getChildAt(i6);
                            if ((childAt3 instanceof ActionBarMenuSubItem) && childAt3.getTag() != null && ((Integer) childAt3.getTag()).intValue() == 10) {
                                ((ActionBarMenuSubItem) childAt3).setTextAndIcon(LocaleController.getString(TextUtils.isEmpty(tL_groupCallParticipant.about) ? R.string.VoipAddBio : R.string.VoipEditBio), TextUtils.isEmpty(tL_groupCallParticipant.about) ? R.drawable.msg_addbio : R.drawable.msg_info);
                            }
                            i6++;
                        }
                        return;
                    }
                    return;
                }
                if (VoIPService.getSharedInstance() != null) {
                    return;
                }
            }
        }
        dismiss();
    }

    @Override
    public void dismiss() {
        this.parentActivity.removeOnUserLeaveHintListener(this.onUserLeaveHintListener);
        this.parentActivity.setRequestedOrientation(-1);
        groupCallUiVisible = false;
        GroupVoipInviteAlert groupVoipInviteAlert = this.groupVoipInviteAlert;
        if (groupVoipInviteAlert != null) {
            groupVoipInviteAlert.dismiss();
        }
        this.delayedGroupCallUpdated = true;
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.needShowAlert);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.groupCallUpdated);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.chatInfoDidLoad);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.didLoadChatAdmins);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.applyGroupCallVisibleParticipants);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.userInfoDidLoad);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.mainUserInfoChanged);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.updateInterfaces);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.groupCallScreencastStateChanged);
        this.accountInstance.getNotificationCenter().removeObserver(this, NotificationCenter.groupCallSpeakingUsersUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.webRtcMicAmplitudeEvent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didEndCall);
        super.dismiss();
    }

    @Override
    public void dismissInternal() {
        if (this.renderersContainer != null) {
            if (this.requestFullscreenListener != null) {
                this.listView.getViewTreeObserver().removeOnPreDrawListener(this.requestFullscreenListener);
                this.requestFullscreenListener = null;
            }
            this.attachedRenderersTmp.clear();
            this.attachedRenderersTmp.addAll(this.attachedRenderers);
            for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
                ((GroupCallMiniTextureView) this.attachedRenderersTmp.get(i)).saveThumb();
                this.renderersContainer.removeView((View) this.attachedRenderersTmp.get(i));
                ((GroupCallMiniTextureView) this.attachedRenderersTmp.get(i)).release();
                ((GroupCallMiniTextureView) this.attachedRenderersTmp.get(i)).forceDetach(true);
            }
            this.attachedRenderers.clear();
            if (this.renderersContainer.getParent() != null) {
                this.attachedRenderers.clear();
                this.containerView.removeView(this.renderersContainer);
            }
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.startAllHeavyOperations, 2048);
        super.dismissInternal();
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().unregisterStateListener(this);
            VoIPService.getSharedInstance().setSinks(null, null);
        }
        if (groupCallInstance == this) {
            groupCallInstance = null;
        }
        groupCallUiVisible = false;
        VoIPService.audioLevelsCallback = null;
        GroupCallPip.updateVisibility(getContext());
        ChatObject.Call call = this.call;
        if (call != null) {
            call.clearVideFramesInfo();
        }
        if (VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().clearRemoteSinks();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int streamMinVolume;
        if (this.parentActivity == null) {
            return super.dispatchKeyEvent(keyEvent);
        }
        if (keyEvent.getAction() == 0 && ((keyEvent.getKeyCode() == 24 || keyEvent.getKeyCode() == 25) && VoIPService.getSharedInstance() != null && Build.VERSION.SDK_INT >= 32)) {
            boolean isSpeakerMuted = WebRtcAudioTrack.isSpeakerMuted();
            AudioManager audioManager = (AudioManager) this.parentActivity.getSystemService("audio");
            boolean z = false;
            streamMinVolume = audioManager.getStreamMinVolume(0);
            if (audioManager.getStreamVolume(0) == streamMinVolume && keyEvent.getKeyCode() == 25) {
                z = true;
            }
            WebRtcAudioTrack.setSpeakerMute(z);
            if (isSpeakerMuted != WebRtcAudioTrack.isSpeakerMuted()) {
                getUndoView().showWithAction(0L, z ? 42 : 43, (Runnable) null);
            }
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    public void enableCamera() {
        this.cameraButton.callOnClick();
    }

    public void fullscreenFor(final ChatObject.VideoParticipant videoParticipant) {
        ViewTreeObserver viewTreeObserver;
        ViewTreeObserver.OnPreDrawListener onPreDrawListener;
        ChatObject.VideoParticipant videoParticipant2;
        if (videoParticipant == null) {
            this.parentActivity.setRequestedOrientation(-1);
        }
        if (VoIPService.getSharedInstance() == null || this.renderersContainer.isAnimating()) {
            return;
        }
        if (!isTabletMode) {
            if (this.requestFullscreenListener != null) {
                this.listView.getViewTreeObserver().removeOnPreDrawListener(this.requestFullscreenListener);
                this.requestFullscreenListener = null;
            }
            if (videoParticipant == null) {
                if (this.listView.getVisibility() != 0) {
                    this.listView.setVisibility(0);
                    applyCallParticipantUpdates(false);
                    this.delayedGroupCallUpdated = true;
                    viewTreeObserver = this.listView.getViewTreeObserver();
                    onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                            GroupCallActivity.this.renderersContainer.requestFullscreen(null);
                            AndroidUtilities.updateVisibleRows(GroupCallActivity.this.fullscreenUsersListView);
                            return false;
                        }
                    };
                } else {
                    viewTreeObserver = this.listView.getViewTreeObserver();
                    onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                            GroupCallActivity.this.renderersContainer.requestFullscreen(null);
                            AndroidUtilities.updateVisibleRows(GroupCallActivity.this.fullscreenUsersListView);
                            return false;
                        }
                    };
                }
                this.requestFullscreenListener = onPreDrawListener;
                viewTreeObserver.addOnPreDrawListener(onPreDrawListener);
                return;
            }
            if (this.fullscreenUsersListView.getVisibility() == 0) {
                this.renderersContainer.requestFullscreen(videoParticipant);
                AndroidUtilities.updateVisibleRows(this.fullscreenUsersListView);
                return;
            }
            this.fullscreenUsersListView.setVisibility(0);
            this.fullscreenAdapter.update(false, this.fullscreenUsersListView);
            this.delayedGroupCallUpdated = true;
            if (!this.renderersContainer.inFullscreenMode) {
                this.fullscreenAdapter.scrollTo(videoParticipant, this.fullscreenUsersListView);
            }
            ViewTreeObserver viewTreeObserver2 = this.listView.getViewTreeObserver();
            ViewTreeObserver.OnPreDrawListener onPreDrawListener2 = new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                    GroupCallActivity groupCallActivity = GroupCallActivity.this;
                    groupCallActivity.requestFullscreenListener = null;
                    groupCallActivity.renderersContainer.requestFullscreen(videoParticipant);
                    AndroidUtilities.updateVisibleRows(GroupCallActivity.this.fullscreenUsersListView);
                    return false;
                }
            };
            this.requestFullscreenListener = onPreDrawListener2;
            viewTreeObserver2.addOnPreDrawListener(onPreDrawListener2);
            return;
        }
        if (this.requestFullscreenListener != null) {
            this.listView.getViewTreeObserver().removeOnPreDrawListener(this.requestFullscreenListener);
            this.requestFullscreenListener = null;
        }
        final ArrayList arrayList = new ArrayList();
        this.attachedRenderersTmp.clear();
        this.attachedRenderersTmp.addAll(this.attachedRenderers);
        int i = 0;
        if (videoParticipant == null) {
            while (i < this.attachedRenderersTmp.size()) {
                final GroupCallMiniTextureView groupCallMiniTextureView = (GroupCallMiniTextureView) this.attachedRenderersTmp.get(i);
                GroupCallGridCell groupCallGridCell = groupCallMiniTextureView.primaryView;
                if (groupCallGridCell != null) {
                    groupCallGridCell.setRenderer(null);
                    GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell = groupCallMiniTextureView.secondaryView;
                    if (groupCallUserCell != null) {
                        groupCallUserCell.setRenderer(null);
                    }
                    GroupCallGridCell groupCallGridCell2 = groupCallMiniTextureView.tabletGridView;
                    if (groupCallGridCell2 != null) {
                        groupCallGridCell2.setRenderer(null);
                    }
                    arrayList.add(groupCallMiniTextureView.participant);
                    groupCallMiniTextureView.forceDetach(false);
                    groupCallMiniTextureView.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (groupCallMiniTextureView.getParent() != null) {
                                ((BottomSheet) GroupCallActivity.this).containerView.removeView(groupCallMiniTextureView);
                            }
                        }
                    });
                }
                i++;
            }
            this.listViewVideoVisibility = false;
            this.tabletGridAdapter.setVisibility(this.tabletVideoGridView, true, true);
        } else {
            while (i < this.attachedRenderersTmp.size()) {
                final GroupCallMiniTextureView groupCallMiniTextureView2 = (GroupCallMiniTextureView) this.attachedRenderersTmp.get(i);
                if (groupCallMiniTextureView2.tabletGridView != null && ((videoParticipant2 = groupCallMiniTextureView2.participant) == null || !videoParticipant2.equals(videoParticipant))) {
                    arrayList.add(groupCallMiniTextureView2.participant);
                    groupCallMiniTextureView2.forceDetach(false);
                    GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell2 = groupCallMiniTextureView2.secondaryView;
                    if (groupCallUserCell2 != null) {
                        groupCallUserCell2.setRenderer(null);
                    }
                    GroupCallGridCell groupCallGridCell3 = groupCallMiniTextureView2.primaryView;
                    if (groupCallGridCell3 != null) {
                        groupCallGridCell3.setRenderer(null);
                    }
                    groupCallMiniTextureView2.animate().alpha(0.0f).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            if (groupCallMiniTextureView2.getParent() != null) {
                                ((BottomSheet) GroupCallActivity.this).containerView.removeView(groupCallMiniTextureView2);
                            }
                        }
                    });
                }
                i++;
            }
            this.listViewVideoVisibility = true;
            this.tabletGridAdapter.setVisibility(this.tabletVideoGridView, false, false);
            if (!arrayList.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        GroupCallActivity.this.lambda$fullscreenFor$36(arrayList);
                    }
                });
            }
        }
        final boolean z = !this.renderersContainer.inFullscreenMode;
        ViewTreeObserver viewTreeObserver3 = this.listView.getViewTreeObserver();
        ViewTreeObserver.OnPreDrawListener onPreDrawListener3 = new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                GroupCallActivity.this.listView.getViewTreeObserver().removeOnPreDrawListener(this);
                GroupCallActivity groupCallActivity = GroupCallActivity.this;
                groupCallActivity.requestFullscreenListener = null;
                groupCallActivity.renderersContainer.requestFullscreen(videoParticipant);
                if (GroupCallActivity.this.delayedGroupCallUpdated) {
                    GroupCallActivity.this.delayedGroupCallUpdated = false;
                    GroupCallActivity.this.applyCallParticipantUpdates(true);
                    if (z && videoParticipant != null) {
                        GroupCallActivity.this.listView.scrollToPosition(0);
                    }
                    GroupCallActivity.this.delayedGroupCallUpdated = true;
                } else {
                    GroupCallActivity.this.applyCallParticipantUpdates(true);
                }
                return false;
            }
        };
        this.requestFullscreenListener = onPreDrawListener3;
        viewTreeObserver3.addOnPreDrawListener(onPreDrawListener3);
    }

    public LinearLayout getMenuItemsContainer() {
        return this.menuItemsContainer;
    }

    public LaunchActivity getParentActivity() {
        return this.parentActivity;
    }

    public View getScrimView() {
        return this.scrimView;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        return new ArrayList();
    }

    public UndoView getUndoView() {
        if (!isTabletMode) {
            GroupCallRenderersContainer groupCallRenderersContainer = this.renderersContainer;
            if (groupCallRenderersContainer.inFullscreenMode) {
                return groupCallRenderersContainer.getUndoView();
            }
        }
        if (this.undoView[0].getVisibility() == 0) {
            UndoView[] undoViewArr = this.undoView;
            UndoView undoView = undoViewArr[0];
            undoViewArr[0] = undoViewArr[1];
            undoViewArr[1] = undoView;
            undoView.hide(true, 2);
            this.containerView.removeView(this.undoView[0]);
            this.containerView.addView(this.undoView[0]);
        }
        return this.undoView[0];
    }

    public void invalidateActionBarAlpha() {
        ActionBar actionBar = this.actionBar;
        actionBar.setAlpha((actionBar.getTag() != null ? 1.0f : 0.0f) * (1.0f - this.renderersContainer.progressToFullscreenMode));
    }

    public void invalidateScrollOffsetY() {
        setScrollOffsetY(this.scrollOffsetY);
    }

    public boolean isRtmpLandscapeMode() {
        if (!isRtmpStream() || this.call.visibleVideoParticipants.isEmpty()) {
            return false;
        }
        return this.call.visibleVideoParticipants.get(0).aspectRatio == 0.0f || this.call.visibleVideoParticipants.get(0).aspectRatio >= 1.0f;
    }

    public boolean isRtmpStream() {
        ChatObject.Call call = this.call;
        return call != null && call.call.rtmp_stream;
    }

    protected void makeFocusable(final BottomSheet bottomSheet, final AlertDialog alertDialog, final EditTextBoldCursor editTextBoldCursor, final boolean z) {
        if (this.enterEventSent) {
            return;
        }
        BaseFragment baseFragment = (BaseFragment) this.parentActivity.getActionBarLayout().getFragmentStack().get(this.parentActivity.getActionBarLayout().getFragmentStack().size() - 1);
        if (baseFragment instanceof ChatActivity) {
            boolean needEnterText = ((ChatActivity) baseFragment).needEnterText();
            this.enterEventSent = true;
            this.anyEnterEventSent = true;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    GroupCallActivity.lambda$makeFocusable$7(BottomSheet.this, editTextBoldCursor, z, alertDialog);
                }
            }, needEnterText ? 200L : 0L);
            return;
        }
        this.enterEventSent = true;
        this.anyEnterEventSent = true;
        if (bottomSheet != null) {
            bottomSheet.setFocusable(true);
        } else if (alertDialog != null) {
            alertDialog.setFocusable(true);
        }
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    GroupCallActivity.lambda$makeFocusable$8(EditTextBoldCursor.this);
                }
            }, 100L);
        }
    }

    @Override
    public void onAudioSettingsChanged() {
        updateSpeakerPhoneIcon(true);
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute()) {
            setMicAmplitude(0.0f);
        }
        if (this.listView.getVisibility() == 0) {
            AndroidUtilities.updateVisibleRows(this.listView);
        }
        if (this.fullscreenUsersListView.getVisibility() == 0) {
            AndroidUtilities.updateVisibleRows(this.fullscreenUsersListView);
        }
        this.attachedRenderersTmp.clear();
        this.attachedRenderersTmp.addAll(this.attachedRenderers);
        for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
            ((GroupCallMiniTextureView) this.attachedRenderersTmp.get(i)).updateAttachState(true);
        }
    }

    @Override
    public void onBackPressed() {
        PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
        if (privateVideoPreviewDialog != null) {
            privateVideoPreviewDialog.dismiss(false, false);
            return;
        }
        if (this.avatarsPreviewShowed) {
            dismissAvatarPreview(true);
        } else if (this.renderersContainer.inFullscreenMode) {
            fullscreenFor(null);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onCameraFirstFrameAvailable() {
        VoIPService.StateListener.CC.$default$onCameraFirstFrameAvailable(this);
    }

    @Override
    public void onCameraSwitch(boolean z) {
        this.attachedRenderersTmp.clear();
        this.attachedRenderersTmp.addAll(this.attachedRenderers);
        for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
            ((GroupCallMiniTextureView) this.attachedRenderersTmp.get(i)).updateAttachState(true);
        }
        PrivateVideoPreviewDialog privateVideoPreviewDialog = this.previewDialog;
        if (privateVideoPreviewDialog != null) {
            privateVideoPreviewDialog.update();
        }
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.parentActivity.addOnUserLeaveHintListener(this.onUserLeaveHintListener);
    }

    @Override
    public boolean onCustomOpenAnimation() {
        groupCallUiVisible = true;
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.groupCallVisibilityChanged, new Object[0]);
        GroupCallPip.updateVisibility(getContext());
        return super.onCustomOpenAnimation();
    }

    @Override
    public void onMediaStateUpdated(int i, int i2) {
        VoIPService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    public void onPause() {
        paused = true;
        this.attachedRenderersTmp.clear();
        this.attachedRenderersTmp.addAll(this.attachedRenderers);
        for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
            ((GroupCallMiniTextureView) this.attachedRenderersTmp.get(i)).updateAttachState(false);
        }
    }

    public void onResume() {
        paused = false;
        this.listAdapter.notifyDataSetChanged();
        if (this.fullscreenUsersListView.getVisibility() == 0) {
            this.fullscreenAdapter.update(false, this.fullscreenUsersListView);
        }
        if (isTabletMode) {
            this.tabletGridAdapter.update(false, this.tabletVideoGridView);
        }
        this.attachedRenderersTmp.clear();
        this.attachedRenderersTmp.addAll(this.attachedRenderers);
        for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
            ((GroupCallMiniTextureView) this.attachedRenderersTmp.get(i)).updateAttachState(true);
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
        this.currentCallState = i;
        updateState(isShowing(), false);
    }

    @Override
    public void onVideoAvailableChange(boolean z) {
        VoIPService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    public void setOldRows(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9) {
        this.oldAddMemberRow = i;
        this.oldUsersStartRow = i2;
        this.oldUsersEndRow = i3;
        this.oldInvitedStartRow = i4;
        this.oldInvitedEndRow = i5;
        this.oldUsersVideoStartRow = i6;
        this.oldUsersVideoEndRow = i7;
        this.oldVideoDividerRow = i8;
        this.oldVideoNotAvailableRow = i9;
    }

    @Override
    public void show() {
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, 2048);
        super.show();
        if (RTMPStreamPipOverlay.isVisible()) {
            RTMPStreamPipOverlay.dismiss();
        }
    }
}
