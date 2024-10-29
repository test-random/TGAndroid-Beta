package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Property;
import android.util.SparseArray;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewStructure;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.animation.OvershootInterpolator;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatMessageSharedResources;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FlagSecureReason;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.video.OldVideoPlayerRewinder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.AvatarSpan;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedNumberLayout;
import org.telegram.ui.Components.AudioVisualizerDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.Forum.MessageTopicButton;
import org.telegram.ui.Components.ForwardBackground;
import org.telegram.ui.Components.InfiniteProgress;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.LoadingDrawable;
import org.telegram.ui.Components.MessageBackgroundDrawable;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.Premium.boosts.BoostCounterSpan;
import org.telegram.ui.Components.Premium.boosts.cells.msg.GiveawayMessageCell;
import org.telegram.ui.Components.Premium.boosts.cells.msg.GiveawayResultsMessageCell;
import org.telegram.ui.Components.QuoteHighlight;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ReplyMessageLine;
import org.telegram.ui.Components.RoundVideoPlayingDrawable;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBarAccessibilityDelegate;
import org.telegram.ui.Components.SeekBarWaveform;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.SlotsDrawable;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.StickerSetLinkIcon;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.TimerParticles;
import org.telegram.ui.Components.TranscribeButton;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanBrowser;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.VideoForwardDrawable;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.Components.spoilers.SpoilerEffect2;
import org.telegram.ui.GradientClip;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PinchToZoomHelper;
import org.telegram.ui.Stories.recorder.CaptionContainerView;

public class ChatMessageCell extends BaseCell implements SeekBar.SeekBarDelegate, ImageReceiver.ImageReceiverDelegate, DownloadController.FileDownloadProgressListener, TextSelectionHelper.SelectableView, NotificationCenter.NotificationCenterDelegate {
    private static float[] radii = new float[8];
    private final boolean ALPHA_PROPERTY_WORKAROUND;
    public Property ANIMATION_OFFSET_X;
    private int TAG;
    CharSequence accessibilityText;
    private boolean accessibilityTextContentUnread;
    private long accessibilityTextFileSize;
    private boolean accessibilityTextUnread;
    private SparseArray accessibilityVirtualViewBounds;
    private int[] adaptiveEmojiColor;
    private ColorFilter[] adaptiveEmojiColorFilter;
    private int addedCaptionHeight;
    private boolean addedForTest;
    private int additionalTimeOffsetY;
    private StaticLayout adminLayout;
    private boolean allowAssistant;
    private float alphaInternal;
    private int animateFromStatusDrawableParams;
    private boolean animatePollAnswer;
    private boolean animatePollAnswerAlpha;
    private boolean animatePollAvatars;
    private int animateToStatusDrawableParams;
    public AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiDescriptionStack;
    public AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiPollQuestion;
    public AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiReplyStack;
    public AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiStack;
    private int animatingDrawVideoImageButton;
    private float animatingDrawVideoImageButtonProgress;
    private float animatingLoadingProgressProgress;
    private int animatingNoSound;
    private boolean animatingNoSoundPlaying;
    private float animatingNoSoundProgress;
    private float animationOffsetX;
    private boolean animationRunning;
    private boolean attachedToWindow;
    private StaticLayout authorLayout;
    private int authorLayoutLeft;
    private int authorLayoutWidth;
    private boolean autoPlayingMedia;
    private int availableTimeWidth;
    protected AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private boolean avatarPressed;
    private Theme.MessageDrawable.PathDrawParams backgroundCacheParams;
    private MessageBackgroundDrawable backgroundDrawable;
    private int backgroundDrawableBottom;
    private int backgroundDrawableLeft;
    private int backgroundDrawableRight;
    private int backgroundDrawableTop;
    private int backgroundHeight;
    private int backgroundWidth;
    private ImageReceiver blurredPhotoImage;
    public int blurredViewBottomOffset;
    public int blurredViewTopOffset;
    private RectF boostCounterBounds;
    private Drawable boostCounterLayoutSelector;
    private boolean boostCounterPressed;
    private int boostCounterSelectorColor;
    private BoostCounterSpan boostCounterSpan;
    private Path botButtonPath;
    private float[] botButtonRadii;
    private ArrayList botButtons;
    private HashMap botButtonsByData;
    private HashMap botButtonsByPosition;
    private String botButtonsLayout;
    private boolean bottomNearToSet;
    private int buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private final boolean canDrawBackgroundInParent;
    private boolean canStreamVideo;
    public boolean captionAbove;
    private int captionFullWidth;
    private int captionHeight;
    public MessageObject.TextLayoutBlocks captionLayout;
    private int captionOffsetX;
    private int captionWidth;
    private float captionX;
    public float captionY;
    public ChannelRecommendationsCell channelRecommendationsCell;
    private CheckBoxBase checkBox;
    private boolean checkBoxAnimationInProgress;
    private float checkBoxAnimationProgress;
    public int checkBoxTranslation;
    private boolean checkBoxVisible;
    private boolean checkOnlyButtonPressed;
    public int childPosition;
    private GradientClip clip;
    private Paint clipPaint;
    public boolean clipToGroupBounds;
    private ButtonBounce closeSponsoredBounce;
    private RectF closeSponsoredBounds;
    private String closeTimeText;
    private int closeTimeWidth;
    private int commentArrowX;
    private AvatarDrawable[] commentAvatarDrawables;
    private ImageReceiver[] commentAvatarImages;
    private boolean[] commentAvatarImagesVisible;
    private boolean commentButtonPressed;
    private Rect commentButtonRect;
    private boolean commentDrawUnread;
    private StaticLayout commentLayout;
    private LoadingDrawable commentLoading;
    private AnimatedNumberLayout commentNumberLayout;
    private int commentNumberWidth;
    private InfiniteProgress commentProgress;
    private float commentProgressAlpha;
    private long commentProgressLastUpadteTime;
    private int commentUnreadX;
    private int commentWidth;
    private int commentX;
    public MessageObject.TextLayoutBlocks computedCaptionLayout;
    public int computedGroupCaptionY;
    private AvatarDrawable contactAvatarDrawable;
    public ButtonBounce contactBounce;
    private ArrayList contactButtons;
    public ReplyMessageLine contactLine;
    private boolean contactPressed;
    private RectF contactRect;
    private float controlsAlpha;
    public final int currentAccount;
    public Theme.MessageDrawable currentBackgroundDrawable;
    private Theme.MessageDrawable currentBackgroundSelectedDrawable;
    private CharSequence currentCaption;
    private TLRPC.Chat currentChat;
    private int currentFocusedVirtualView;
    private TLRPC.Chat currentForwardChannel;
    private String currentForwardName;
    private String currentForwardNameString;
    private TLRPC.User currentForwardUser;
    private int currentMapProvider;
    private MessageObject currentMessageObject;
    private MessageObject.GroupedMessages currentMessagesGroup;
    private Object currentNameStatus;
    public AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable currentNameStatusDrawable;
    private String currentNameString;
    private TLRPC.FileLocation currentPhoto;
    private String currentPhotoFilter;
    private String currentPhotoFilterThumb;
    private ImageLocation currentPhotoLocation;
    private TLRPC.PhotoSize currentPhotoObject;
    private TLRPC.PhotoSize currentPhotoObjectThumb;
    private BitmapDrawable currentPhotoObjectThumbStripped;
    private ImageLocation currentPhotoThumbLocation;
    private MessageObject.GroupedMessagePosition currentPosition;
    private String currentRepliesString;
    private TLRPC.PhotoSize currentReplyPhoto;
    private float currentSelectedBackgroundAlpha;
    private CharSequence currentTimeString;
    private String currentUnlockString;
    private String currentUrl;
    private TLRPC.User currentUser;
    private TLRPC.User currentViaBotUser;
    private String currentViewsString;
    private WebFile currentWebFile;
    private ChatMessageCellDelegate delegate;
    private RectF deleteProgressRect;
    private StaticLayout descriptionLayout;
    private int descriptionLayoutLeft;
    private int descriptionLayoutWidth;
    private int descriptionX;
    private int descriptionY;
    private Runnable diceFinishCallback;
    private boolean disallowLongPress;
    public boolean doNotDraw;
    private StaticLayout docTitleLayout;
    private int docTitleOffsetX;
    private int docTitleWidth;
    private TLRPC.Document documentAttach;
    private int documentAttachType;
    private boolean drawBackground;
    private boolean drawCommentButton;
    private boolean drawCommentNumber;
    private boolean drawContact;
    private boolean drawContactAdd;
    private boolean drawContactSendMessage;
    private boolean drawContactView;
    public boolean drawForBlur;
    private boolean drawForwardedName;
    public boolean drawFromPinchToZoom;
    private boolean drawImageButton;
    private boolean drawInstantView;
    public int drawInstantViewType;
    private boolean drawMediaCheckBox;
    private boolean drawName;
    private boolean drawNameLayout;
    public boolean drawPhotoImage;
    public boolean drawPinnedBottom;
    public boolean drawPinnedTop;
    private boolean drawRadialCheckBackground;
    private boolean drawSelectionBackground;
    private int drawSideButton;
    private int drawSideButton2;
    private boolean drawTime;
    private float drawTimeX;
    private float drawTimeY;
    private boolean drawTopic;
    private boolean drawVideoImageButton;
    private boolean drawVideoSize;
    public boolean drawingToBitmap;
    private int drawnContactButtonsFlag;
    private Paint drillHolePaint;
    private Path drillHolePath;
    private StaticLayout durationLayout;
    private int durationWidth;
    private boolean edited;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable effectDrawable;
    private ButtonBounce effectDrawableBounce;
    private long effectId;
    private int effectMessageId;
    boolean enterTransitionInProgress;
    public ExpiredStoryView expiredStoryView;
    private Drawable factCheckArrow;
    private int factCheckArrowColor;
    private ButtonBounce factCheckBounce;
    private int factCheckHeight;
    private boolean factCheckLarge;
    public ReplyMessageLine factCheckLine;
    private LinkSpanDrawable.LinkCollector factCheckLinks;
    private boolean factCheckPressed;
    private StaticLayout factCheckText2Layout;
    private int factCheckText2LayoutLeft;
    private StaticLayout factCheckTextLayout;
    private int factCheckTextLayoutHeight;
    private boolean factCheckTextLayoutLastLineEnd;
    private int factCheckTextLayoutLeft;
    private Text factCheckTitle;
    private Text factCheckWhat;
    private ButtonBounce factCheckWhatBounce;
    private boolean factCheckWhatPressed;
    private int factCheckWidth;
    private int factCheckY;
    private ColorMatrixColorFilter fancyBlurFilter;
    private boolean firstCircleLength;
    private int firstVisibleBlockNum;
    private boolean fitPhotoImage;
    private FlagSecureReason flagSecure;
    private boolean flipImage;
    private boolean forceNotDrawTime;
    private Drawable foreverDrawable;
    private int foreverDrawableColor;
    private AvatarSpan forwardAvatar;
    private ForwardBackground forwardBg;
    private boolean forwardBotPressed;
    private int forwardHeight;
    private int forwardNameCenterX;
    private final float[] forwardNameOffsetX;
    private boolean forwardNamePressed;
    private float forwardNameX;
    private int forwardNameY;
    private final StaticLayout[] forwardedNameLayout;
    private int forwardedNameWidth;
    private boolean frozen;
    private boolean fullyDraw;
    private boolean gamePreviewPressed;
    public final GiveawayMessageCell giveawayMessageCell;
    public final GiveawayResultsMessageCell giveawayResultsMessageCell;
    private Drawable gradientDrawable;
    private LinearGradient gradientShader;
    public GroupMedia groupMedia;
    private boolean groupPhotoInvisible;
    private MessageObject.GroupedMessages groupedMessagesToSet;
    private boolean hadLongPress;
    public boolean hasDiscussion;
    private boolean hasEmbed;
    private boolean hasFactCheck;
    private boolean hasGamePreview;
    private boolean hasInvoicePreview;
    private boolean hasInvoicePrice;
    private boolean hasLinkPreview;
    private int hasMiniProgress;
    private boolean hasNewLineForTime;
    private boolean hasOldCaptionPreview;
    private boolean hasPsaHint;
    public boolean hasReplyQuote;
    private int highlightCaptionToSetEnd;
    private int highlightCaptionToSetStart;
    private LinkPath highlightPath;
    private long highlightPathStart;
    private int highlightProgress;
    private boolean highlightedQuote;
    private float hintButtonProgress;
    private boolean hintButtonVisible;
    private int imageBackgroundColor;
    private int imageBackgroundGradientColor1;
    private int imageBackgroundGradientColor2;
    private int imageBackgroundGradientColor3;
    private int imageBackgroundGradientRotation;
    private float imageBackgroundIntensity;
    private int imageBackgroundSideColor;
    private int imageBackgroundSideWidth;
    private boolean imageDrawn;
    private boolean imagePressed;
    boolean imageReceiversAttachState;
    boolean imageReceiversVisibleState;
    private boolean inLayout;
    private StaticLayout infoLayout;
    private int infoWidth;
    private int infoX;
    private ButtonBounce instantButtonBounce;
    private LoadingDrawable instantButtonLoading;
    private boolean instantButtonPressed;
    private RectF instantButtonRect;
    public Drawable instantDrawable;
    public int instantDrawableColor;
    private Paint instantLinkArrowPaint;
    private Path instantLinkArrowPath;
    private boolean instantPressed;
    private int instantTextLeftX;
    private boolean instantTextNewLine;
    private int instantTextX;
    public String instantViewButtonText;
    private StaticLayout instantViewLayout;
    private float instantViewLayoutLeft;
    private float instantViewLayoutWidth;
    private int instantWidth;
    private Runnable invalidateListener;
    private Runnable invalidateRunnable;
    private boolean invalidateSpoilersParent;
    private boolean invalidatesParent;
    public boolean isAvatarVisible;
    public boolean isBlurred;
    public boolean isBot;
    public boolean isChat;
    private boolean isCheckPressed;
    public boolean isForum;
    public boolean isForumGeneral;
    private boolean isHighlighted;
    private boolean isHighlightedAnimated;
    private boolean isMedia;
    public boolean isMegagroup;
    public boolean isPinned;
    public boolean isPinnedChat;
    private boolean isPlayingRound;
    private boolean isPressed;
    public boolean isRepliesChat;
    public boolean isReplyQuote;
    private boolean isRoundVideo;
    public boolean isSavedChat;
    public boolean isSavedPreviewChat;
    private boolean isSmallImage;
    private boolean isSpoilerRevealing;
    public boolean isThreadChat;
    private boolean isThreadPost;
    private boolean isTitleLabelPressed;
    private boolean isUpdating;
    private int keyboardHeight;
    private long lastAnimationTime;
    private long lastCheckBoxAnimationTime;
    private long lastControlsAlphaChangeTime;
    private int lastDeleteDate;
    private float lastDrawingAudioProgress;
    private int lastHeight;
    private long lastHighlightProgressTime;
    private long lastLoadingSizeTotal;
    private long lastNamesAnimationTime;
    private TLRPC.Poll lastPoll;
    private long lastPollCloseTime;
    private ArrayList lastPollResults;
    private int lastPollResultsVoters;
    private String lastPostAuthor;
    private TLRPC.TL_messageReactions lastReactions;
    private int lastRepliesCount;
    private TLRPC.Message lastReplyMessage;
    private long lastSeekUpdateTime;
    private int lastSendState;
    int lastSize;
    private double lastTime;
    private float lastTouchX;
    private float lastTouchY;
    private boolean lastTranslated;
    private int lastViewsCount;
    private int lastVisibleBlockNum;
    private WebFile lastWebFile;
    private int lastWidth;
    private int layoutHeight;
    private int layoutWidth;
    public int linkBlockNum;
    public ReplyMessageLine linkLine;
    public boolean linkPreviewAbove;
    private ButtonBounce linkPreviewBounce;
    public int linkPreviewHeight;
    private boolean linkPreviewPressed;
    private Drawable linkPreviewSelector;
    public int linkPreviewSelectorColor;
    private int linkPreviewY;
    private int linkSelectionBlockNum;
    public long linkedChatId;
    public LinkSpanDrawable.LinkCollector links;
    private StaticLayout loadingProgressLayout;
    private long loadingProgressLayoutHash;
    private boolean locationExpired;
    private ImageReceiver locationImageReceiver;
    private Drawable locationLoadingThumb;
    public boolean makeVisibleAfterChange;
    private boolean mediaBackground;
    private CheckBoxBase mediaCheckBox;
    private int mediaOffsetY;
    private SpoilerEffect mediaSpoilerEffect;
    private SpoilerEffect2 mediaSpoilerEffect2;
    private Integer mediaSpoilerEffect2Index;
    private Path mediaSpoilerPath;
    private float[] mediaSpoilerRadii;
    private float mediaSpoilerRevealMaxRadius;
    private float mediaSpoilerRevealProgress;
    private float mediaSpoilerRevealX;
    private float mediaSpoilerRevealY;
    private boolean mediaWasInvisible;
    private MessageObject messageObjectToSet;
    private int miniButtonPressed;
    private int miniButtonState;
    private MotionBackgroundDrawable motionBackgroundDrawable;
    private StaticLayout nameLayout;
    private boolean nameLayoutPressed;
    private Drawable nameLayoutSelector;
    private int nameLayoutSelectorColor;
    private int nameLayoutWidth;
    private float nameOffsetX;
    private boolean nameStatusPressed;
    private Drawable nameStatusSelector;
    private int nameStatusSelectorColor;
    private int nameWidth;
    private float nameX;
    private float nameY;
    public int namesOffset;
    private boolean needNewVisiblePart;
    public boolean needReplyImage;
    private int noSoundCenterX;
    private Paint onceClearPaint;
    private RLottieDrawable onceFire;
    private CaptionContainerView.PeriodDrawable oncePeriod;
    private Paint onceRadialCutPaint;
    private Paint onceRadialPaint;
    private Paint onceRadialStrokePaint;
    private boolean otherPressed;
    private int otherX;
    private int otherY;
    private int overideShouldDrawTimeOnMedia;
    private Runnable overrideInvalidate;
    private AudioVisualizerDrawable overridenAudioVisualizer;
    private long overridenDuration;
    public int parentBoundsBottom;
    public float parentBoundsTop;
    public int parentHeight;
    public float parentViewTopOffset;
    public int parentWidth;
    private StaticLayout performerLayout;
    private int performerX;
    private ImageReceiver photoImage;
    private boolean photoImageOutOfBounds;
    private boolean photoNotSet;
    private TLObject photoParentObject;
    private StaticLayout photosCountLayout;
    private int photosCountWidth;
    public boolean pinnedBottom;
    public boolean pinnedTop;
    private float pollAnimationProgress;
    private float pollAnimationProgressTime;
    private AvatarDrawable[] pollAvatarDrawables;
    private ImageReceiver[] pollAvatarImages;
    private boolean[] pollAvatarImagesVisible;
    private ArrayList pollButtons;
    private CheckBoxBase[] pollCheckBox;
    private boolean pollClosed;
    private boolean pollHintPressed;
    private int pollHintX;
    private int pollHintY;
    private boolean pollInstantViewTouchesBottom;
    private boolean pollUnvoteInProgress;
    private boolean pollVoteInProgress;
    private int pollVoteInProgressNum;
    private boolean pollVoted;
    private int pressedBlock;
    private int pressedBotButton;
    private MessageObject.TextLayoutBlock pressedCopyCode;
    private boolean pressedEffect;
    private AnimatedEmojiSpan pressedEmoji;
    private LinkSpanDrawable pressedFactCheckLink;
    private LinkSpanDrawable pressedLink;
    private int pressedLinkType;
    private int pressedSideButton;
    private int[] pressedState;
    private int pressedVoteButton;
    public MessageObject.TextLayoutBlocks prevCaptionLayout;
    private CharacterStyle progressLoadingLink;
    private LoadingDrawable progressLoadingLinkCurrentDrawable;
    private ArrayList progressLoadingLinkDrawables;
    private float psaButtonProgress;
    private boolean psaButtonVisible;
    private int psaHelpX;
    private int psaHelpY;
    private boolean psaHintPressed;
    public Drawable quoteArrow;
    public int quoteArrowColor;
    public Drawable[] quoteDrawable;
    public int[] quoteDrawableColor;
    public QuoteHighlight quoteHighlight;
    public ReplyMessageLine quoteLine;
    private RadialProgress2 radialProgress;
    protected float radialProgressAlpha;
    public final ReactionsLayoutInBubble reactionsLayoutInBubble;
    private RectF rect;
    private Path rectPath;
    private StaticLayout repliesLayout;
    private int repliesTextWidth;
    public ButtonBounce replyBounce;
    public float replyBounceX;
    public float replyBounceY;
    public float replyHeight;
    public ImageReceiver replyImageReceiver;
    public ReplyMessageLine replyLine;
    public StaticLayout replyNameLayout;
    private int replyNameOffset;
    private int replyNameWidth;
    private boolean replyPanelIsForward;
    private boolean replyPressed;
    private AnimatedFloat replyPressedFloat;
    public Drawable replyQuoteDrawable;
    public int replyQuoteDrawableColor;
    private Path replyRoundRectPath;
    public Drawable replySelector;
    private boolean replySelectorCanBePressed;
    public int replySelectorColor;
    private boolean replySelectorPressed;
    public float replySelectorRadLeft;
    public float replySelectorRadRight;
    public RectF replySelectorRect;
    public List replySpoilers;
    private final Stack replySpoilersPool;
    public int replyStartX;
    public int replyStartY;
    private int replyTextHeight;
    public StaticLayout replyTextLayout;
    public int replyTextOffset;
    public boolean replyTextRTL;
    private int replyTextWidth;
    private float replyTouchX;
    private float replyTouchY;
    private Theme.ResourcesProvider resourcesProvider;
    private float roundPlayingDrawableProgress;
    private float roundProgressAlpha;
    float roundSeekbarOutAlpha;
    float roundSeekbarOutProgress;
    int roundSeekbarTouched;
    private float roundToPauseProgress;
    private float roundToPauseProgress2;
    private AnimatedFloat roundVideoPlayPipFloat;
    private RoundVideoPlayingDrawable roundVideoPlayingDrawable;
    private final Path sPath;
    private boolean scheduledInvalidate;
    private Rect scrollRect;
    private SeekBar seekBar;
    private SeekBarAccessibilityDelegate seekBarAccessibilityDelegate;
    private int seekBarTranslateX;
    private SeekBarWaveform seekBarWaveform;
    private int seekBarWaveformTranslateX;
    private int seekBarX;
    private int seekBarY;
    float seekbarRoundX;
    float seekbarRoundY;
    private float selectedBackgroundProgress;
    private Paint selectionOverlayPaint;
    private final Drawable[] selectorDrawable;
    private int selectorDrawableColor;
    private int[] selectorDrawableMaskType;
    private final MaskDrawable[] selectorMaskDrawable;
    private Text sensitiveText;
    private Text sensitiveTextShort;
    private AnimatorSet shakeAnimation;
    private ChatMessageSharedResources sharedResources;
    public boolean shouldCheckVisibleOnScreen;
    private Path sideButtonPath1;
    private Path sideButtonPath2;
    private float[] sideButtonPathCorners1;
    private float[] sideButtonPathCorners2;
    private boolean sideButtonPressed;
    private boolean sideButtonVisible;
    private float sideStartX;
    private float sideStartY;
    public int signWidth;
    private StaticLayout siteNameLayout;
    private float siteNameLayoutWidth;
    private float siteNameLeft;
    private int siteNameWidth;
    private boolean skipFrameUpdate;
    private float slidingOffsetX;
    private StaticLayout songLayout;
    private int songX;
    private SpoilerEffect spoilerPressed;
    private AtomicReference spoilersPatchedReplyTextLayout;
    private boolean statusDrawableAnimationInProgress;
    private ValueAnimator statusDrawableAnimator;
    private float statusDrawableProgress;
    private StickerSetLinkIcon stickerSetIcons;
    private int substractBackgroundHeight;
    public int textX;
    public int textY;
    private float timeAlpha;
    private int timeAudioX;
    public StaticLayout timeLayout;
    private boolean timePressed;
    private int timeTextWidth;
    private boolean timeWasInvisible;
    public int timeWidth;
    private int timeWidthAudio;
    public int timeX;
    private TimerParticles timerParticles;
    private AnimatedFloat timerParticlesAlpha;
    private float timerTransitionProgress;
    private ButtonBounce titleLabelBounce;
    private StaticLayout titleLabelLayout;
    private float titleLabelLayoutHeight;
    private float titleLabelLayoutWidth;
    private float titleLabelX;
    private float titleLabelY;
    private StaticLayout titleLayout;
    private int titleLayoutLeft;
    private int titleLayoutWidth;
    private int titleX;
    private float toSeekBarProgress;
    private boolean topNearToSet;
    private MessageTopicButton topicButton;
    private long totalChangeTime;
    private int totalCommentWidth;
    public int totalHeight;
    private int totalVisibleBlocksCount;
    public TranscribeButton transcribeButton;
    private float transcribeX;
    private float transcribeY;
    public final TransitionParams transitionParams;
    public float transitionYOffsetForDrawables;
    private LoadingDrawable translationLoadingDrawable;
    private ArrayList translationLoadingDrawableText;
    private AnimatedFloat translationLoadingFloat;
    private LinkPath translationLoadingPath;
    private float unlockAlpha;
    private StaticLayout unlockLayout;
    private SpoilerEffect unlockSpoilerEffect;
    private Path unlockSpoilerPath;
    private float[] unlockSpoilerRadii;
    private int unlockTextWidth;
    private float unlockX;
    private float unlockY;
    private int unmovedTextX;
    private ArrayList urlPathCache;
    private ArrayList urlPathSelection;
    private boolean useSeekBarWaveform;
    private boolean useTranscribeButton;
    private int viaNameWidth;
    private boolean viaOnly;
    private TypefaceSpan viaSpan1;
    private TypefaceSpan viaSpan2;
    private int viaWidth;
    private boolean vibrateOnPollVote;
    private int videoButtonPressed;
    private int videoButtonX;
    private int videoButtonY;
    VideoForwardDrawable videoForwardDrawable;
    private StaticLayout videoInfoLayout;
    OldVideoPlayerRewinder videoPlayerRewinder;
    private RadialProgress2 videoRadialProgress;
    private float viewTop;
    private StaticLayout viewsLayout;
    private int viewsTextWidth;
    public int visibleHeight;
    private boolean visibleOnScreen;
    public int visibleParent;
    public float visibleParentOffset;
    public float visibleTop;
    private float voteCurrentCircleLength;
    private float voteCurrentProgressTime;
    private long voteLastUpdateTime;
    private float voteRadOffset;
    private boolean voteRisingCircleLength;
    private boolean wasLayout;
    private boolean wasPinned;
    private boolean wasSending;
    private boolean wasTranscriptionOpen;
    private int widthBeforeNewTimeLine;
    private int widthForButtons;
    private boolean willRemoved;
    private boolean wouldBeInPip;

    public class BotButton {
        private int angle;
        private TLRPC.KeyboardButton button;
        private int height;
        private boolean isInviteButton;
        private long lastUpdateTime;
        private LoadingDrawable loadingDrawable;
        private int positionFlags;
        private ValueAnimator pressAnimator;
        private float pressT;
        private boolean pressed;
        private float progressAlpha;
        private Drawable selectorDrawable;
        private Text title;
        private float width;
        private float x;
        private int y;

        private BotButton() {
        }

        static int access$3776(BotButton botButton, int i) {
            int i2 = i | botButton.positionFlags;
            botButton.positionFlags = i2;
            return i2;
        }

        public float getPressScale() {
            if (this.pressed) {
                float f = this.pressT;
                if (f != 1.0f) {
                    float min = f + (Math.min(40.0f, 1000.0f / AndroidUtilities.screenRefreshRate) / 100.0f);
                    this.pressT = min;
                    this.pressT = Utilities.clamp(min, 1.0f, 0.0f);
                    ChatMessageCell.this.invalidateOutbounds();
                }
            }
            return ((1.0f - this.pressT) * 0.04f) + 0.96f;
        }

        public void lambda$setPressed$0(ValueAnimator valueAnimator) {
            this.pressT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            ChatMessageCell.this.invalidateOutbounds();
        }

        public void setPressed(boolean z) {
            ValueAnimator valueAnimator;
            if (this.pressed != z) {
                this.pressed = z;
                ChatMessageCell.this.invalidateOutbounds();
                if (z && (valueAnimator = this.pressAnimator) != null) {
                    valueAnimator.removeAllListeners();
                    this.pressAnimator.cancel();
                }
                if (z) {
                    return;
                }
                float f = this.pressT;
                if (f != 0.0f) {
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(f, 0.0f);
                    this.pressAnimator = ofFloat;
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            ChatMessageCell.BotButton.this.lambda$setPressed$0(valueAnimator2);
                        }
                    });
                    this.pressAnimator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            super.onAnimationEnd(animator);
                            BotButton.this.pressAnimator = null;
                        }
                    });
                    this.pressAnimator.setInterpolator(new OvershootInterpolator(2.0f));
                    this.pressAnimator.setDuration(350L);
                    this.pressAnimator.start();
                }
            }
        }

        public boolean hasPositionFlag(int i) {
            return (this.positionFlags & i) == i;
        }
    }

    public interface ChatMessageCellDelegate {

        public abstract class CC {
            public static boolean $default$canDrawOutboundsContent(ChatMessageCellDelegate chatMessageCellDelegate) {
                return true;
            }

            public static boolean $default$canPerformActions(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static void $default$didLongPress(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didLongPressBotButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
            }

            public static boolean $default$didLongPressChannelAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2) {
                return false;
            }

            public static boolean $default$didLongPressUserAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2) {
                return false;
            }

            public static void $default$didPressAboutRevenueSharingAds(ChatMessageCellDelegate chatMessageCellDelegate) {
            }

            public static boolean $default$didPressAnimatedEmoji(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, AnimatedEmojiSpan animatedEmojiSpan) {
                return false;
            }

            public static void $default$didPressBoostCounter(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressBotButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
            }

            public static void $default$didPressCancelSendButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressChannelAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2, boolean z) {
            }

            public static void $default$didPressChannelRecommendation(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.Chat chat, boolean z) {
            }

            public static void $default$didPressChannelRecommendationsClose(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressCodeCopy(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, MessageObject.TextLayoutBlock textLayoutBlock) {
            }

            public static void $default$didPressCommentButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressDialogButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressEffect(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressExtendedMediaPreview(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
            }

            public static void $default$didPressFactCheck(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressFactCheckWhat(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i, int i2) {
            }

            public static void $default$didPressGiveawayChatButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i) {
            }

            public static void $default$didPressGroupImage(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, ImageReceiver imageReceiver, TLRPC.MessageExtendedMedia messageExtendedMedia, float f, float f2) {
            }

            public static void $default$didPressHiddenForward(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressHint(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i) {
            }

            public static void $default$didPressImage(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didPressInstantButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i) {
            }

            public static void $default$didPressMoreChannelRecommendations(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressOther(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didPressReaction(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.ReactionCount reactionCount, boolean z, float f, float f2) {
            }

            public static void $default$didPressReplyMessage(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i, float f, float f2, boolean z) {
            }

            public static void $default$didPressRevealSensitiveContent(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressSideButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressSponsoredClose(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressSponsoredInfo(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, float f, float f2) {
            }

            public static void $default$didPressTime(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressTopicButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
            }

            public static void $default$didPressUrl(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
            }

            public static void $default$didPressUserAvatar(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2, boolean z) {
            }

            public static void $default$didPressUserStatus(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.User user, TLRPC.Document document) {
            }

            public static void $default$didPressViaBot(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, String str) {
            }

            public static void $default$didPressViaBotNotInline(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, long j) {
            }

            public static void $default$didPressVoteButtons(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, ArrayList arrayList, int i, int i2, int i3) {
            }

            public static void $default$didPressWebPage(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, TLRPC.WebPage webPage, String str, boolean z) {
                Browser.openUrl(chatMessageCell.getContext(), str);
            }

            public static void $default$didStartVideoStream(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
            }

            public static boolean $default$doNotShowLoadingReply(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
                return messageObject != null && messageObject.getDialogId() == 1271266957;
            }

            public static void $default$forceUpdate(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, boolean z) {
            }

            public static String $default$getAdminRank(ChatMessageCellDelegate chatMessageCellDelegate, long j) {
                return null;
            }

            public static PinchToZoomHelper $default$getPinchToZoomHelper(ChatMessageCellDelegate chatMessageCellDelegate) {
                return null;
            }

            public static String $default$getProgressLoadingBotButtonUrl(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
                return null;
            }

            public static CharacterStyle $default$getProgressLoadingLink(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
                return null;
            }

            public static TextSelectionHelper.ChatListTextSelectionHelper $default$getTextSelectionHelper(ChatMessageCellDelegate chatMessageCellDelegate) {
                return null;
            }

            public static boolean $default$hasSelectedMessages(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static void $default$invalidateBlur(ChatMessageCellDelegate chatMessageCellDelegate) {
            }

            public static boolean $default$isLandscape(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static boolean $default$isProgressLoading(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, int i) {
                return false;
            }

            public static boolean $default$isReplyOrSelf(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static boolean $default$keyboardIsOpened(ChatMessageCellDelegate chatMessageCellDelegate) {
                return false;
            }

            public static void $default$needOpenWebView(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject, String str, String str2, String str3, String str4, int i, int i2) {
            }

            public static boolean $default$needPlayMessage(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, MessageObject messageObject, boolean z) {
                return false;
            }

            public static void $default$needReloadPolls(ChatMessageCellDelegate chatMessageCellDelegate) {
            }

            public static void $default$needShowPremiumBulletin(ChatMessageCellDelegate chatMessageCellDelegate, int i) {
            }

            public static boolean $default$onAccessibilityAction(ChatMessageCellDelegate chatMessageCellDelegate, int i, Bundle bundle) {
                return false;
            }

            public static void $default$onDiceFinished(ChatMessageCellDelegate chatMessageCellDelegate) {
            }

            public static void $default$setShouldNotRepeatSticker(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
            }

            public static boolean $default$shouldDrawThreadProgress(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell, boolean z) {
                return false;
            }

            public static boolean $default$shouldRepeatSticker(ChatMessageCellDelegate chatMessageCellDelegate, MessageObject messageObject) {
                return true;
            }

            public static boolean $default$shouldShowDialogButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
                return false;
            }

            public static boolean $default$shouldShowTopicButton(ChatMessageCellDelegate chatMessageCellDelegate, ChatMessageCell chatMessageCell) {
                return false;
            }

            public static void $default$videoTimerReached(ChatMessageCellDelegate chatMessageCellDelegate) {
            }
        }

        boolean canDrawOutboundsContent();

        boolean canPerformActions();

        boolean canPerformReply();

        void didLongPress(ChatMessageCell chatMessageCell, float f, float f2);

        void didLongPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton);

        boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2);

        boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2);

        void didPressAboutRevenueSharingAds();

        boolean didPressAnimatedEmoji(ChatMessageCell chatMessageCell, AnimatedEmojiSpan animatedEmojiSpan);

        void didPressBoostCounter(ChatMessageCell chatMessageCell);

        void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton);

        void didPressCancelSendButton(ChatMessageCell chatMessageCell);

        void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2, boolean z);

        void didPressChannelRecommendation(ChatMessageCell chatMessageCell, TLRPC.Chat chat, boolean z);

        void didPressChannelRecommendationsClose(ChatMessageCell chatMessageCell);

        void didPressCodeCopy(ChatMessageCell chatMessageCell, MessageObject.TextLayoutBlock textLayoutBlock);

        void didPressCommentButton(ChatMessageCell chatMessageCell);

        void didPressDialogButton(ChatMessageCell chatMessageCell);

        void didPressEffect(ChatMessageCell chatMessageCell);

        void didPressExtendedMediaPreview(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton);

        void didPressFactCheck(ChatMessageCell chatMessageCell);

        void didPressFactCheckWhat(ChatMessageCell chatMessageCell, int i, int i2);

        void didPressGiveawayChatButton(ChatMessageCell chatMessageCell, int i);

        void didPressGroupImage(ChatMessageCell chatMessageCell, ImageReceiver imageReceiver, TLRPC.MessageExtendedMedia messageExtendedMedia, float f, float f2);

        void didPressHiddenForward(ChatMessageCell chatMessageCell);

        void didPressHint(ChatMessageCell chatMessageCell, int i);

        void didPressImage(ChatMessageCell chatMessageCell, float f, float f2);

        void didPressInstantButton(ChatMessageCell chatMessageCell, int i);

        void didPressMoreChannelRecommendations(ChatMessageCell chatMessageCell);

        void didPressOther(ChatMessageCell chatMessageCell, float f, float f2);

        void didPressReaction(ChatMessageCell chatMessageCell, TLRPC.ReactionCount reactionCount, boolean z, float f, float f2);

        void didPressReplyMessage(ChatMessageCell chatMessageCell, int i, float f, float f2, boolean z);

        void didPressRevealSensitiveContent(ChatMessageCell chatMessageCell);

        void didPressSideButton(ChatMessageCell chatMessageCell);

        void didPressSponsoredClose(ChatMessageCell chatMessageCell);

        void didPressSponsoredInfo(ChatMessageCell chatMessageCell, float f, float f2);

        void didPressTime(ChatMessageCell chatMessageCell);

        void didPressTopicButton(ChatMessageCell chatMessageCell);

        void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z);

        void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2, boolean z);

        void didPressUserStatus(ChatMessageCell chatMessageCell, TLRPC.User user, TLRPC.Document document);

        void didPressViaBot(ChatMessageCell chatMessageCell, String str);

        void didPressViaBotNotInline(ChatMessageCell chatMessageCell, long j);

        void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList arrayList, int i, int i2, int i3);

        void didPressWebPage(ChatMessageCell chatMessageCell, TLRPC.WebPage webPage, String str, boolean z);

        void didStartVideoStream(MessageObject messageObject);

        boolean doNotShowLoadingReply(MessageObject messageObject);

        void forceUpdate(ChatMessageCell chatMessageCell, boolean z);

        String getAdminRank(long j);

        PinchToZoomHelper getPinchToZoomHelper();

        String getProgressLoadingBotButtonUrl(ChatMessageCell chatMessageCell);

        CharacterStyle getProgressLoadingLink(ChatMessageCell chatMessageCell);

        TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper();

        boolean hasSelectedMessages();

        void invalidateBlur();

        boolean isLandscape();

        boolean isProgressLoading(ChatMessageCell chatMessageCell, int i);

        boolean isReplyOrSelf();

        boolean keyboardIsOpened();

        void needOpenWebView(MessageObject messageObject, String str, String str2, String str3, String str4, int i, int i2);

        boolean needPlayMessage(ChatMessageCell chatMessageCell, MessageObject messageObject, boolean z);

        void needReloadPolls();

        void needShowPremiumBulletin(int i);

        boolean onAccessibilityAction(int i, Bundle bundle);

        void onDiceFinished();

        void setShouldNotRepeatSticker(MessageObject messageObject);

        boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell, boolean z);

        boolean shouldRepeatSticker(MessageObject messageObject);

        boolean shouldShowDialogButton(ChatMessageCell chatMessageCell);

        boolean shouldShowTopicButton(ChatMessageCell chatMessageCell);

        void videoTimerReached();
    }

    public static class InstantViewButton {
        private ButtonBounce buttonBounce;
        private float buttonWidth;
        private StaticLayout layout;
        private final RectF rect;
        private Drawable selectorDrawable;
        private float textX;
        private int type;

        private InstantViewButton() {
            this.rect = new RectF();
        }

        static float access$5024(InstantViewButton instantViewButton, float f) {
            float f2 = instantViewButton.textX - f;
            instantViewButton.textX = f2;
            return f2;
        }
    }

    public class LoadingDrawableLocation {
        int blockNum;
        LoadingDrawable drawable;

        LoadingDrawableLocation() {
        }
    }

    public static class MaskDrawable extends Drawable {
        protected final Paint maskPaint;
        public final Path path;
        public int pathX;
        public int pathY;
        public final RectF rect;

        private MaskDrawable() {
            Paint paint = new Paint(1);
            this.maskPaint = paint;
            paint.setColor(-1);
            this.rect = new RectF();
            this.path = new Path();
        }

        @Override
        public void draw(Canvas canvas) {
            updatePath();
            canvas.drawPath(this.path, this.maskPaint);
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

        public abstract void updatePath();
    }

    public class MessageAccessibilityNodeProvider extends AccessibilityNodeProvider {
        private Path linkPath;
        private Rect rect;
        private RectF rectF;

        private class ProfileSpan extends ClickableSpan {
            private TLRPC.User user;

            public ProfileSpan(TLRPC.User user) {
                this.user = user;
            }

            @Override
            public void onClick(View view) {
                if (ChatMessageCell.this.delegate != null) {
                    ChatMessageCell.this.delegate.didPressUserAvatar(ChatMessageCell.this, this.user, 0.0f, 0.0f, false);
                }
            }
        }

        private MessageAccessibilityNodeProvider() {
            this.linkPath = new Path();
            this.rectF = new RectF();
            this.rect = new Rect();
        }

        private ClickableSpan getLinkById(int i, boolean z) {
            if (i == 5000) {
                return null;
            }
            if (z) {
                int i2 = i - 3000;
                if (!(ChatMessageCell.this.currentMessageObject.caption instanceof Spannable) || i2 < 0) {
                    return null;
                }
                Spannable spannable = (Spannable) ChatMessageCell.this.currentMessageObject.caption;
                ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(0, spannable.length(), ClickableSpan.class);
                if (clickableSpanArr.length <= i2) {
                    return null;
                }
                return clickableSpanArr[i2];
            }
            int i3 = i - 2000;
            if (!(ChatMessageCell.this.currentMessageObject.messageText instanceof Spannable) || i3 < 0) {
                return null;
            }
            Spannable spannable2 = (Spannable) ChatMessageCell.this.currentMessageObject.messageText;
            ClickableSpan[] clickableSpanArr2 = (ClickableSpan[]) spannable2.getSpans(0, spannable2.length(), ClickableSpan.class);
            if (clickableSpanArr2.length <= i3) {
                return null;
            }
            return clickableSpanArr2[i3];
        }

        @Override
        public android.view.accessibility.AccessibilityNodeInfo createAccessibilityNodeInfo(int r23) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.MessageAccessibilityNodeProvider.createAccessibilityNodeInfo(int):android.view.accessibility.AccessibilityNodeInfo");
        }

        @Override
        public boolean performAction(int r10, int r11, android.os.Bundle r12) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.MessageAccessibilityNodeProvider.performAction(int, int, android.os.Bundle):boolean");
        }
    }

    public static class PollButton {
        public AnimatedEmojiSpan.EmojiGroupedSpans animatedEmoji;
        private TLRPC.PollAnswer answer;
        private boolean chosen;
        private boolean correct;
        private int count;
        private float decimal;
        public int height;
        private int percent;
        private float percentProgress;
        private boolean prevChosen;
        private int prevPercent;
        private float prevPercentProgress;
        private StaticLayout title;
        public int x;
        public int y;

        static int access$2412(PollButton pollButton, int i) {
            int i2 = pollButton.percent + i;
            pollButton.percent = i2;
            return i2;
        }

        static float access$3124(PollButton pollButton, float f) {
            float f2 = pollButton.decimal - f;
            pollButton.decimal = f2;
            return f2;
        }
    }

    public class TransitionParams {
        public boolean animateBackgroundBoundsInner;
        public boolean animateBackgroundWidth;
        boolean animateBotButtonsChanged;
        private boolean animateButton;
        public boolean animateChange;
        private int animateCommentArrowX;
        private boolean animateCommentDrawUnread;
        private int animateCommentUnreadX;
        private float animateCommentX;
        private boolean animateComments;
        private StaticLayout animateCommentsLayout;
        public boolean animateDrawBackground;
        private boolean animateDrawCommentNumber;
        public boolean animateDrawNameLayout;
        public boolean animateDrawingTimeAlpha;
        private boolean animateEditedEnter;
        private StaticLayout animateEditedLayout;
        private int animateEditedWidthDiff;
        public boolean animateExpandedQuotes;
        public HashSet animateExpandedQuotesFrom;
        public boolean animateFactCheck;
        public boolean animateFactCheckExpanded;
        public boolean animateFactCheckHeight;
        public int animateFactCheckHeightFrom;
        int animateForwardNameWidth;
        float animateForwardNameX;
        public boolean animateForwardedLayout;
        public int animateForwardedNamesOffset;
        private float animateFromButtonX;
        private float animateFromButtonY;
        public int animateFromLinkPreviewHeight;
        public int animateFromLinkPreviewY;
        private int animateFromMediaOffsetY;
        public boolean animateFromRecommendationsExpanded;
        private float animateFromReplyTextHeight;
        public float animateFromReplyY;
        public float animateFromRoundVideoDotY;
        public float animateFromTextY;
        public int animateFromTimeX;
        public float animateFromTimeXPinned;
        private float animateFromTimeXReplies;
        private float animateFromTimeXViews;
        public int animateFromWidthForButton;
        public boolean animateLinkAbove;
        public boolean animateLinkPreviewHeight;
        public boolean animateLinkPreviewY;
        public boolean animateLocationIsExpired;
        public boolean animateMediaAbove;
        private boolean animateMediaOffsetY;
        public boolean animateMessageText;
        private float animateNameX;
        private AnimatedEmojiSpan.EmojiGroupedSpans animateOutAnimateEmoji;
        private AnimatedEmojiSpan.EmojiGroupedSpans animateOutAnimateEmojiReply;
        private MessageObject.TextLayoutBlocks animateOutCaptionLayout;
        private ArrayList animateOutTextBlocks;
        private float animateOutTextXOffset;
        private boolean animatePinned;
        public boolean animatePlayingRound;
        public boolean animateRadius;
        public boolean animateRecommendationsExpanded;
        boolean animateReplaceCaptionLayout;
        private boolean animateReplies;
        private StaticLayout animateRepliesLayout;
        private StaticLayout animateReplyTextLayout;
        public float animateReplyTextOffset;
        public boolean animateReplyY;
        public boolean animateRoundVideoDotY;
        private boolean animateShouldDrawMenuDrawable;
        private boolean animateShouldDrawTimeOnMedia;
        private boolean animateSign;
        public boolean animateSmallImage;
        public boolean animateText;
        private StaticLayout animateTimeLayout;
        private int animateTimeWidth;
        public float animateToImageH;
        public float animateToImageW;
        public float animateToImageX;
        public float animateToImageY;
        public int[] animateToRadius;
        private int animateTotalCommentWidth;
        public boolean animateUseTranscribeButton;
        private StaticLayout animateViewsLayout;
        public boolean animateWidthForButton;
        public float captionFromX;
        public float captionFromY;
        public float deltaBottom;
        public float deltaLeft;
        public float deltaRight;
        public float deltaTop;
        public boolean drawPinnedBottomBackground;
        public boolean ignoreAlpha;
        public boolean imageChangeBoundsTransition;
        public int lastBackgroundLeft;
        public int lastBackgroundRight;
        public float lastButtonX;
        public float lastButtonY;
        public int lastCommentArrowX;
        public boolean lastCommentDrawUnread;
        public StaticLayout lastCommentLayout;
        public int lastCommentUnreadX;
        public float lastCommentX;
        public int lastCommentsCount;
        public boolean lastDrawBackground;
        public boolean lastDrawCommentNumber;
        public StaticLayout lastDrawDocTitleLayout;
        public StaticLayout lastDrawInfoLayout;
        public float lastDrawLocationExpireProgress;
        public String lastDrawLocationExpireText;
        public boolean lastDrawNameLayout;
        public float lastDrawReplyY;
        public float lastDrawRoundVideoDotY;
        public boolean lastDrawTime;
        public MessageObject.TextLayoutBlocks lastDrawingCaptionLayout;
        public float lastDrawingCaptionX;
        public float lastDrawingCaptionY;
        public boolean lastDrawingEdited;
        public HashSet lastDrawingExpandedQuotes;
        public boolean lastDrawingFactCheck;
        public boolean lastDrawingFactCheckExpanded;
        public int lastDrawingFactCheckHeight;
        public float lastDrawingImageH;
        public float lastDrawingImageW;
        public float lastDrawingImageX;
        public float lastDrawingImageY;
        public boolean lastDrawingLinkAbove;
        public int lastDrawingLinkPreviewHeight;
        public int lastDrawingLinkPreviewY;
        public boolean lastDrawingMediaAbove;
        public boolean lastDrawingRecommendationsExpanded;
        public float lastDrawingReplyTextHeight;
        public boolean lastDrawingSmallImage;
        public ArrayList lastDrawingTextBlocks;
        public float lastDrawingTextX;
        public float lastDrawingTextY;
        public boolean lastDrawnForwardedName;
        public StaticLayout lastDrawnReplyTextLayout;
        public int lastDrawnWidthForButtons;
        public int lastForwardNameWidth;
        public float lastForwardNameX;
        public int lastForwardedNamesOffset;
        public boolean lastIsPinned;
        public boolean lastIsPlayingRound;
        public boolean lastLocatinIsExpired;
        public int lastMediaOffsetY;
        public int lastRepliesCount;
        public StaticLayout lastRepliesLayout;
        public int lastReplyTextXOffset;
        public boolean lastShouldDrawMenuDrawable;
        public boolean lastShouldDrawTimeOnMedia;
        public String lastSignMessage;
        public float lastTextXOffset;
        public StaticLayout lastTimeLayout;
        public int lastTimeWidth;
        public int lastTimeX;
        public float lastTimeXPinned;
        private float lastTimeXReplies;
        private float lastTimeXViews;
        public int lastTopOffset;
        public int lastTotalCommentWidth;
        public boolean lastUseTranscribeButton;
        public int lastViewsCount;
        public StaticLayout lastViewsLayout;
        public boolean messageEntering;
        private boolean moveCaption;
        public float photoImageFromCenterX;
        public float photoImageFromCenterY;
        public float photoImageFromHeight;
        public float photoImageFromWidth;
        public boolean shouldAnimateTimeX;
        public float toDeltaLeft;
        public float toDeltaRight;
        public boolean transformGroupToSingleMessage;
        public boolean updatePhotoImageX;
        public boolean wasDraw;
        public int[] imageRoundRadius = new int[4];
        public float captionEnterProgress = 1.0f;
        public float changePinnedBottomProgress = 1.0f;
        public Rect lastDrawingBackgroundRect = new Rect();
        public float animateChangeProgress = 1.0f;
        private ArrayList lastDrawBotButtons = new ArrayList();
        private ArrayList transitionBotButtons = new ArrayList();
        public int lastStatusDrawableParams = -1;
        public StaticLayout[] lastDrawnForwardedNameLayout = new StaticLayout[2];
        public StaticLayout[] animatingForwardedNameLayout = new StaticLayout[2];

        public TransitionParams() {
        }

        public boolean animateChange() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.TransitionParams.animateChange():boolean");
        }

        public int createStatusDrawableParams() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.TransitionParams.createStatusDrawableParams():int");
        }

        public void onDetach() {
            this.wasDraw = false;
        }

        public void recordDrawingState() {
            ChannelRecommendationsCell channelRecommendationsCell;
            this.wasDraw = true;
            this.lastDrawingImageX = ChatMessageCell.this.photoImage.getImageX();
            this.lastDrawingImageY = ChatMessageCell.this.photoImage.getImageY();
            this.lastDrawingImageW = ChatMessageCell.this.photoImage.getImageWidth();
            this.lastDrawingImageH = ChatMessageCell.this.photoImage.getImageHeight();
            System.arraycopy(ChatMessageCell.this.photoImage.getRoundRadius(), 0, this.imageRoundRadius, 0, 4);
            Theme.MessageDrawable messageDrawable = ChatMessageCell.this.currentBackgroundDrawable;
            if (messageDrawable != null) {
                this.lastDrawingBackgroundRect.set(messageDrawable.getBounds());
            }
            this.lastDrawingTextBlocks = ChatMessageCell.this.currentMessageObject != null ? ChatMessageCell.this.currentMessageObject.textLayoutBlocks : null;
            this.lastDrawingEdited = ChatMessageCell.this.edited;
            this.lastDrawingCaptionX = ChatMessageCell.this.captionX;
            ChatMessageCell chatMessageCell = ChatMessageCell.this;
            this.lastDrawingCaptionY = chatMessageCell.captionY;
            this.lastDrawingCaptionLayout = chatMessageCell.captionLayout;
            this.lastDrawBotButtons.clear();
            if (!ChatMessageCell.this.botButtons.isEmpty()) {
                this.lastDrawBotButtons.addAll(ChatMessageCell.this.botButtons);
            }
            this.lastDrawingSmallImage = ChatMessageCell.this.isSmallImage;
            ChatMessageCell chatMessageCell2 = ChatMessageCell.this;
            this.lastDrawingLinkPreviewHeight = chatMessageCell2.linkPreviewHeight;
            this.lastDrawingLinkAbove = chatMessageCell2.linkPreviewAbove;
            this.lastDrawingMediaAbove = chatMessageCell2.captionAbove;
            this.lastDrawingRecommendationsExpanded = chatMessageCell2.currentMessageObject != null && ChatMessageCell.this.currentMessageObject.type == 27 && (channelRecommendationsCell = ChatMessageCell.this.channelRecommendationsCell) != null && channelRecommendationsCell.isExpanded();
            if (ChatMessageCell.this.commentLayout != null) {
                this.lastCommentsCount = ChatMessageCell.this.getRepliesCount();
                this.lastTotalCommentWidth = ChatMessageCell.this.totalCommentWidth;
                this.lastCommentLayout = ChatMessageCell.this.commentLayout;
                this.lastCommentArrowX = ChatMessageCell.this.commentArrowX;
                this.lastCommentUnreadX = ChatMessageCell.this.commentUnreadX;
                this.lastCommentDrawUnread = ChatMessageCell.this.commentDrawUnread;
                this.lastCommentX = ChatMessageCell.this.commentX;
                this.lastDrawCommentNumber = ChatMessageCell.this.drawCommentNumber;
            }
            this.lastRepliesCount = ChatMessageCell.this.getRepliesCount();
            this.lastViewsCount = ChatMessageCell.this.getMessageObject().messageOwner.views;
            this.lastRepliesLayout = ChatMessageCell.this.repliesLayout;
            this.lastViewsLayout = ChatMessageCell.this.viewsLayout;
            ChatMessageCell chatMessageCell3 = ChatMessageCell.this;
            this.lastIsPinned = chatMessageCell3.isPinned;
            this.lastSignMessage = chatMessageCell3.lastPostAuthor;
            this.lastDrawBackground = ChatMessageCell.this.drawBackground;
            this.lastUseTranscribeButton = ChatMessageCell.this.useTranscribeButton;
            this.lastButtonX = ChatMessageCell.this.buttonX;
            this.lastButtonY = ChatMessageCell.this.buttonY;
            this.lastMediaOffsetY = ChatMessageCell.this.mediaOffsetY;
            this.lastDrawTime = !ChatMessageCell.this.forceNotDrawTime;
            ChatMessageCell chatMessageCell4 = ChatMessageCell.this;
            this.lastTimeX = chatMessageCell4.timeX;
            this.lastTimeLayout = chatMessageCell4.timeLayout;
            this.lastTimeWidth = chatMessageCell4.timeWidth;
            this.lastShouldDrawTimeOnMedia = chatMessageCell4.shouldDrawTimeOnMedia();
            this.lastTopOffset = ChatMessageCell.this.getTopMediaOffset();
            this.lastShouldDrawMenuDrawable = ChatMessageCell.this.shouldDrawMenuDrawable();
            this.lastLocatinIsExpired = ChatMessageCell.this.locationExpired;
            this.lastIsPlayingRound = ChatMessageCell.this.isPlayingRound;
            ChatMessageCell chatMessageCell5 = ChatMessageCell.this;
            this.lastDrawingTextY = chatMessageCell5.textY;
            this.lastDrawingTextX = chatMessageCell5.textX;
            this.lastDrawingLinkPreviewY = chatMessageCell5.linkPreviewY;
            this.lastDrawnWidthForButtons = ChatMessageCell.this.widthForButtons;
            this.lastDrawnForwardedNameLayout[0] = ChatMessageCell.this.forwardedNameLayout[0];
            this.lastDrawnForwardedNameLayout[1] = ChatMessageCell.this.forwardedNameLayout[1];
            this.lastDrawnForwardedName = ChatMessageCell.this.currentMessageObject != null && ChatMessageCell.this.currentMessageObject.needDrawForwarded();
            this.lastForwardNameX = ChatMessageCell.this.forwardNameX;
            ChatMessageCell chatMessageCell6 = ChatMessageCell.this;
            this.lastForwardedNamesOffset = chatMessageCell6.namesOffset;
            this.lastForwardNameWidth = chatMessageCell6.forwardedNameWidth;
            this.lastBackgroundLeft = ChatMessageCell.this.getCurrentBackgroundLeft();
            Theme.MessageDrawable messageDrawable2 = ChatMessageCell.this.currentBackgroundDrawable;
            if (messageDrawable2 != null) {
                this.lastBackgroundRight = messageDrawable2.getBounds().right;
            }
            this.lastTextXOffset = ChatMessageCell.this.currentMessageObject != null ? ChatMessageCell.this.currentMessageObject.textXOffset : 0.0f;
            this.lastDrawingReplyTextHeight = ChatMessageCell.this.replyTextHeight;
            ChatMessageCell chatMessageCell7 = ChatMessageCell.this;
            this.lastDrawnReplyTextLayout = chatMessageCell7.replyTextLayout;
            this.lastReplyTextXOffset = chatMessageCell7.replyTextOffset;
            chatMessageCell7.reactionsLayoutInBubble.recordDrawingState();
            ChatMessageCell chatMessageCell8 = ChatMessageCell.this;
            this.lastDrawReplyY = chatMessageCell8.replyNameLayout != null ? chatMessageCell8.replyStartY : 0.0f;
            this.lastDrawNameLayout = chatMessageCell8.drawNameLayout;
            this.lastDrawingFactCheckHeight = ChatMessageCell.this.factCheckHeight;
            this.lastDrawingFactCheckExpanded = ChatMessageCell.this.getPrimaryMessageObject() != null && ChatMessageCell.this.getPrimaryMessageObject().factCheckExpanded;
            this.lastDrawingFactCheck = ChatMessageCell.this.hasFactCheck;
            this.lastDrawingExpandedQuotes = ChatMessageCell.this.getPrimaryMessageObject() != null ? ChatMessageCell.this.getPrimaryMessageObject().expandedQuotes : null;
        }

        public void recordDrawingStatePreview() {
            this.lastDrawnForwardedNameLayout[0] = ChatMessageCell.this.forwardedNameLayout[0];
            this.lastDrawnForwardedNameLayout[1] = ChatMessageCell.this.forwardedNameLayout[1];
            this.lastDrawnForwardedName = ChatMessageCell.this.currentMessageObject.needDrawForwarded();
            this.lastForwardNameX = ChatMessageCell.this.forwardNameX;
            ChatMessageCell chatMessageCell = ChatMessageCell.this;
            this.lastForwardedNamesOffset = chatMessageCell.namesOffset;
            this.lastForwardNameWidth = chatMessageCell.forwardedNameWidth;
        }

        public void resetAnimation() {
            this.animateChange = false;
            this.animatePinned = false;
            this.animateBackgroundBoundsInner = false;
            this.animateBackgroundWidth = false;
            this.deltaLeft = 0.0f;
            this.deltaRight = 0.0f;
            this.deltaBottom = 0.0f;
            this.deltaTop = 0.0f;
            this.toDeltaLeft = 0.0f;
            this.toDeltaRight = 0.0f;
            if (this.imageChangeBoundsTransition && this.animateToImageW != 0.0f && this.animateToImageH != 0.0f) {
                ChatMessageCell.this.photoImage.setImageCoords(this.animateToImageX, this.animateToImageY, this.animateToImageW, this.animateToImageH);
            }
            if (this.animateRadius) {
                ChatMessageCell.this.photoImage.setRoundRadius(this.animateToRadius);
            }
            this.animateToImageX = 0.0f;
            this.animateToImageY = 0.0f;
            this.animateToImageW = 0.0f;
            this.animateToImageH = 0.0f;
            this.imageChangeBoundsTransition = false;
            this.changePinnedBottomProgress = 1.0f;
            this.captionEnterProgress = 1.0f;
            this.animateRadius = false;
            this.animateChangeProgress = 1.0f;
            this.animateMessageText = false;
            this.animateDrawNameLayout = false;
            this.animateOutTextBlocks = null;
            this.animateEditedLayout = null;
            this.animateTimeLayout = null;
            this.animateEditedEnter = false;
            this.animateReplaceCaptionLayout = false;
            this.transformGroupToSingleMessage = false;
            this.animateOutCaptionLayout = null;
            AnimatedEmojiSpan.release(ChatMessageCell.this, this.animateOutAnimateEmoji);
            this.animateOutAnimateEmoji = null;
            this.moveCaption = false;
            this.animateDrawingTimeAlpha = false;
            this.transitionBotButtons.clear();
            this.animateButton = false;
            this.animateBotButtonsChanged = false;
            this.animateWidthForButton = false;
            this.animateMediaOffsetY = false;
            this.animateReplyTextLayout = null;
            this.animateReplies = false;
            this.animateRepliesLayout = null;
            this.animateComments = false;
            this.animateCommentsLayout = null;
            this.animateViewsLayout = null;
            this.animateShouldDrawTimeOnMedia = false;
            this.animateShouldDrawMenuDrawable = false;
            this.shouldAnimateTimeX = false;
            this.animateDrawBackground = false;
            this.animateSign = false;
            this.animateSmallImage = false;
            this.animateLinkAbove = false;
            this.animateMediaAbove = false;
            this.animateRecommendationsExpanded = false;
            this.animateDrawingTimeAlpha = false;
            this.animateLocationIsExpired = false;
            this.animatePlayingRound = false;
            this.animateText = false;
            this.animateLinkPreviewY = false;
            this.animateFactCheckHeight = false;
            this.animateFactCheckExpanded = false;
            this.animateExpandedQuotes = false;
            this.animateFactCheck = false;
            this.animateForwardedLayout = false;
            StaticLayout[] staticLayoutArr = this.animatingForwardedNameLayout;
            staticLayoutArr[0] = null;
            staticLayoutArr[1] = null;
            this.animateRoundVideoDotY = false;
            this.animateReplyY = false;
            ChatMessageCell.this.reactionsLayoutInBubble.resetAnimation();
        }

        public boolean supportChangeAnimation() {
            return true;
        }
    }

    public ChatMessageCell(Context context, int i) {
        this(context, i, false, null, null);
    }

    public ChatMessageCell(Context context, int i, boolean z, ChatMessageSharedResources chatMessageSharedResources, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.visibleOnScreen = true;
        this.reactionsLayoutInBubble = new ReactionsLayoutInBubble(this);
        this.giveawayMessageCell = new GiveawayMessageCell(this);
        this.giveawayResultsMessageCell = new GiveawayResultsMessageCell(this);
        this.scrollRect = new Rect();
        this.drawnContactButtonsFlag = 0;
        this.imageBackgroundGradientRotation = 45;
        this.selectorDrawable = new Drawable[2];
        this.selectorMaskDrawable = new MaskDrawable[2];
        this.selectorDrawableMaskType = new int[2];
        this.instantButtonRect = new RectF();
        this.pressedState = new int[]{16842910, 16842919};
        this.highlightCaptionToSetStart = -1;
        this.highlightCaptionToSetEnd = -1;
        this.deleteProgressRect = new RectF();
        this.rect = new RectF();
        this.foreverDrawableColor = -1;
        this.timeAlpha = 1.0f;
        this.controlsAlpha = 1.0f;
        this.pressedBlock = -1;
        this.links = new LinkSpanDrawable.LinkCollector(this);
        this.urlPathCache = new ArrayList();
        this.urlPathSelection = new ArrayList();
        this.rectPath = new Path();
        this.pollButtons = new ArrayList();
        this.botButtons = new ArrayList();
        this.botButtonPath = new Path();
        this.botButtonRadii = new float[8];
        this.botButtonsByData = new HashMap();
        this.botButtonsByPosition = new HashMap();
        this.isCheckPressed = true;
        this.drawBackground = true;
        this.backgroundWidth = 100;
        this.commentButtonRect = new Rect();
        this.spoilersPatchedReplyTextLayout = new AtomicReference();
        this.forwardedNameLayout = new StaticLayout[2];
        this.forwardNameOffsetX = new float[2];
        this.drawTime = true;
        this.mediaSpoilerPath = new Path();
        this.mediaSpoilerRadii = new float[8];
        this.mediaSpoilerEffect = new SpoilerEffect();
        this.unlockAlpha = 1.0f;
        this.unlockSpoilerEffect = new SpoilerEffect();
        this.unlockSpoilerPath = new Path();
        this.unlockSpoilerRadii = new float[8];
        this.replySelectorRect = new RectF();
        this.ALPHA_PROPERTY_WORKAROUND = Build.VERSION.SDK_INT == 28;
        this.alphaInternal = 1.0f;
        this.transitionParams = new TransitionParams();
        this.roundVideoPlayPipFloat = new AnimatedFloat(this, 200L, CubicBezierInterpolator.EASE_OUT);
        this.diceFinishCallback = new Runnable() {
            @Override
            public void run() {
                if (ChatMessageCell.this.delegate != null) {
                    ChatMessageCell.this.delegate.onDiceFinished();
                }
            }
        };
        this.invalidateRunnable = new Runnable() {
            @Override
            public void run() {
                ChatMessageCell.this.checkLocationExpired();
                if (ChatMessageCell.this.locationExpired) {
                    ChatMessageCell.this.invalidate();
                    ChatMessageCell.this.scheduledInvalidate = false;
                    return;
                }
                ChatMessageCell.this.invalidate(((int) r0.rect.left) - 5, ((int) ChatMessageCell.this.rect.top) - 5, ((int) ChatMessageCell.this.rect.right) + 5, ((int) ChatMessageCell.this.rect.bottom) + 5);
                if (ChatMessageCell.this.scheduledInvalidate) {
                    AndroidUtilities.runOnUIThread(ChatMessageCell.this.invalidateRunnable, 1000L);
                }
            }
        };
        this.accessibilityVirtualViewBounds = new SparseArray();
        this.currentFocusedVirtualView = -1;
        this.backgroundCacheParams = new Theme.MessageDrawable.PathDrawParams();
        this.replySpoilers = new ArrayList();
        this.replySpoilersPool = new Stack();
        this.sPath = new Path();
        this.pressedEffect = false;
        this.overridenDuration = -1L;
        this.hadLongPress = false;
        this.radialProgressAlpha = 1.0f;
        this.ANIMATION_OFFSET_X = new Property(Float.class, "animationOffsetX") {
            @Override
            public Float get(ChatMessageCell chatMessageCell) {
                return Float.valueOf(chatMessageCell.animationOffsetX);
            }

            @Override
            public void set(ChatMessageCell chatMessageCell, Float f) {
                chatMessageCell.setAnimationOffsetX(f.floatValue());
            }
        };
        this.currentAccount = i;
        this.resourcesProvider = resourcesProvider;
        this.canDrawBackgroundInParent = z;
        this.sharedResources = chatMessageSharedResources;
        if (chatMessageSharedResources == null) {
            this.sharedResources = new ChatMessageSharedResources(context);
        }
        this.backgroundDrawable = new MessageBackgroundDrawable(this);
        ImageReceiver imageReceiver = new ImageReceiver();
        this.avatarImage = imageReceiver;
        imageReceiver.setAllowLoadingOnAttachedOnly(true);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarDrawable = new AvatarDrawable();
        ImageReceiver imageReceiver2 = new ImageReceiver(this);
        this.replyImageReceiver = imageReceiver2;
        imageReceiver2.setAllowLoadingOnAttachedOnly(true);
        this.replyImageReceiver.setRoundRadius(AndroidUtilities.dp(4.0f));
        ImageReceiver imageReceiver3 = new ImageReceiver(this);
        this.locationImageReceiver = imageReceiver3;
        imageReceiver3.setAllowLoadingOnAttachedOnly(true);
        this.locationImageReceiver.setRoundRadius(AndroidUtilities.dp(26.1f));
        this.TAG = DownloadController.getInstance(i).generateObserverTag();
        this.contactAvatarDrawable = new AvatarDrawable();
        ImageReceiver imageReceiver4 = new ImageReceiver(this);
        this.photoImage = imageReceiver4;
        imageReceiver4.setAllowLoadingOnAttachedOnly(true);
        this.photoImage.setUseRoundForThumbDrawable(true);
        this.photoImage.setDelegate(this);
        ImageReceiver imageReceiver5 = new ImageReceiver(this);
        this.blurredPhotoImage = imageReceiver5;
        imageReceiver5.setAllowLoadingOnAttachedOnly(true);
        this.blurredPhotoImage.setUseRoundForThumbDrawable(true);
        this.radialProgress = new RadialProgress2(this, resourcesProvider);
        RadialProgress2 radialProgress2 = new RadialProgress2(this, resourcesProvider);
        this.videoRadialProgress = radialProgress2;
        radialProgress2.setDrawBackground(false);
        this.videoRadialProgress.setCircleRadius(AndroidUtilities.dp(15.0f));
        SeekBar seekBar = new SeekBar(this) {
            @Override
            protected void onTimestampUpdate(URLSpanNoUnderline uRLSpanNoUnderline) {
                ChatMessageCell.this.setHighlightedSpan(uRLSpanNoUnderline);
            }
        };
        this.seekBar = seekBar;
        seekBar.setDelegate(this);
        SeekBarWaveform seekBarWaveform = new SeekBarWaveform(context);
        this.seekBarWaveform = seekBarWaveform;
        seekBarWaveform.setDelegate(this);
        this.seekBarWaveform.setParentView(this);
        this.seekBarAccessibilityDelegate = new FloatSeekBarAccessibilityDelegate() {
            @Override
            public float getProgress() {
                if (ChatMessageCell.this.currentMessageObject.isMusic()) {
                    return ChatMessageCell.this.seekBar.getProgress();
                }
                if (ChatMessageCell.this.currentMessageObject.isVoice()) {
                    return ChatMessageCell.this.useSeekBarWaveform ? ChatMessageCell.this.seekBarWaveform.getProgress() : ChatMessageCell.this.seekBar.getProgress();
                }
                if (ChatMessageCell.this.currentMessageObject.isRoundVideo()) {
                    return ChatMessageCell.this.currentMessageObject.audioProgress;
                }
                return 0.0f;
            }

            @Override
            public void setProgress(float f) {
                if (!ChatMessageCell.this.currentMessageObject.isMusic()) {
                    if (ChatMessageCell.this.currentMessageObject.isVoice()) {
                        if (ChatMessageCell.this.useSeekBarWaveform) {
                            ChatMessageCell.this.seekBarWaveform.setProgress(f);
                        }
                    } else {
                        if (!ChatMessageCell.this.currentMessageObject.isRoundVideo()) {
                            return;
                        }
                        if (ChatMessageCell.this.useSeekBarWaveform) {
                            if (ChatMessageCell.this.seekBarWaveform != null) {
                                ChatMessageCell.this.seekBarWaveform.setProgress(f);
                            }
                        } else if (ChatMessageCell.this.seekBar != null) {
                            ChatMessageCell.this.seekBar.setProgress(f);
                        }
                        ChatMessageCell.this.currentMessageObject.audioProgress = f;
                    }
                    ChatMessageCell.this.onSeekBarDrag(f);
                    ChatMessageCell.this.invalidate();
                }
                ChatMessageCell.this.seekBar.setProgress(f);
                ChatMessageCell.this.onSeekBarDrag(f);
                ChatMessageCell.this.invalidate();
            }
        };
        this.roundVideoPlayingDrawable = new RoundVideoPlayingDrawable(this, resourcesProvider);
        setImportantForAccessibility(1);
    }

    private boolean allowDrawPhotoImage() {
        return !this.currentMessageObject.hasMediaSpoilers() || this.currentMessageObject.isMediaSpoilersRevealed || this.mediaSpoilerRevealProgress != 0.0f || this.blurredPhotoImage.getBitmap() == null;
    }

    private void animateCheckboxTranslation() {
        boolean z = this.checkBoxVisible;
        if (z || this.checkBoxAnimationInProgress) {
            if ((z && this.checkBoxAnimationProgress == 1.0f) || (!z && this.checkBoxAnimationProgress == 0.0f)) {
                this.checkBoxAnimationInProgress = false;
            }
            this.checkBoxTranslation = (int) Math.ceil((z ? CubicBezierInterpolator.EASE_OUT : CubicBezierInterpolator.EASE_IN).getInterpolation(this.checkBoxAnimationProgress) * AndroidUtilities.dp(35.0f));
            if (!this.currentMessageObject.isOutOwner() || this.currentMessageObject.hasWideCode) {
                updateTranslation();
            }
            if (this.checkBoxAnimationInProgress) {
                long elapsedRealtime = SystemClock.elapsedRealtime();
                long j = elapsedRealtime - this.lastCheckBoxAnimationTime;
                this.lastCheckBoxAnimationTime = elapsedRealtime;
                if (this.checkBoxVisible) {
                    float f = this.checkBoxAnimationProgress + (((float) j) / 200.0f);
                    this.checkBoxAnimationProgress = f;
                    if (f > 1.0f) {
                        this.checkBoxAnimationProgress = 1.0f;
                    }
                } else {
                    float f2 = this.checkBoxAnimationProgress - (((float) j) / 200.0f);
                    this.checkBoxAnimationProgress = f2;
                    if (f2 <= 0.0f) {
                        this.checkBoxAnimationProgress = 0.0f;
                    }
                }
                invalidate();
                ((View) getParent()).invalidate();
            }
        }
    }

    private void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider != null) {
            resourcesProvider.applyServiceShaderMatrix(i, i2, f, f2);
        } else {
            Theme.applyServiceShaderMatrix(i, i2, f, f2);
        }
    }

    private void calcBackgroundWidth(int r7, int r8, int r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.calcBackgroundWidth(int, int, int):void");
    }

    private void calculateUnlockXY() {
        if (this.currentMessageObject.type != 20 || this.unlockLayout == null) {
            return;
        }
        this.unlockX = this.backgroundDrawableLeft + ((this.photoImage.getImageWidth() - this.unlockLayout.getWidth()) / 2.0f);
        this.unlockY = this.backgroundDrawableTop + this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - this.unlockLayout.getHeight()) / 2.0f);
    }

    private void cancelLoading(MessageObject messageObject) {
        if (messageObject == null || messageObject.mediaExists || messageObject.putInDownloadsStore || DownloadController.getInstance(this.currentAccount).isDownloading(messageObject.messageOwner.id) || PhotoViewer.getInstance().isVisible()) {
            return;
        }
        TLRPC.Document document = messageObject.getDocument();
        if (MessageObject.isStickerDocument(document) || MessageObject.isAnimatedStickerDocument(document, true) || MessageObject.isGifDocument(document) || MessageObject.isRoundVideoDocument(document)) {
            return;
        }
        if (document != null) {
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(document);
            return;
        }
        TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
        if (closestPhotoSizeWithSize != null) {
            FileLoader.getInstance(this.currentAccount).cancelLoadFile(closestPhotoSizeWithSize);
        }
    }

    private void cancelShakeAnimation() {
        AnimatorSet animatorSet = this.shakeAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.shakeAnimation = null;
            setScaleX(1.0f);
            setScaleY(1.0f);
            setRotation(0.0f);
        }
    }

    private boolean checkAdminMotionEvent(MotionEvent motionEvent) {
        RectF rectF;
        ChatMessageCellDelegate chatMessageCellDelegate;
        Drawable drawable;
        boolean z = false;
        if (this.adminLayout == null || (rectF = this.boostCounterBounds) == null || (this.currentUser == null && this.currentChat == null)) {
            this.boostCounterPressed = false;
            return false;
        }
        boolean contains = rectF.contains((int) motionEvent.getX(), (int) motionEvent.getY());
        if (motionEvent.getAction() == 0) {
            SpannableString spannableString = new SpannableString(this.adminLayout.getText());
            BoostCounterSpan[] boostCounterSpanArr = (BoostCounterSpan[]) spannableString.getSpans(0, spannableString.length(), BoostCounterSpan.class);
            if (contains && boostCounterSpanArr != null && boostCounterSpanArr.length > 0) {
                z = true;
            }
            this.boostCounterPressed = z;
            if (z && (drawable = this.boostCounterLayoutSelector) != null) {
                if (Build.VERSION.SDK_INT >= 21) {
                    drawable.setHotspot((int) motionEvent.getX(), (int) motionEvent.getY());
                }
                this.boostCounterLayoutSelector.setState(this.pressedState);
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (motionEvent.getAction() == 1 && this.boostCounterPressed && (chatMessageCellDelegate = this.delegate) != null) {
                chatMessageCellDelegate.didPressBoostCounter(this);
            }
            Drawable drawable2 = this.boostCounterLayoutSelector;
            if (drawable2 != null) {
                drawable2.setState(StateSet.NOTHING);
            }
            this.boostCounterPressed = false;
        }
        return this.boostCounterPressed;
    }

    private boolean checkAudioMotionEvent(android.view.MotionEvent r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkAudioMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkBotButtonMotionEvent(MotionEvent motionEvent) {
        int i;
        if (this.botButtons.isEmpty()) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() != 0) {
            if (motionEvent.getAction() == 1) {
                if (this.pressedBotButton == -1) {
                    return false;
                }
                playSoundEffect(0);
                BotButton botButton = (BotButton) this.botButtons.get(this.pressedBotButton);
                if (botButton.selectorDrawable != null) {
                    botButton.selectorDrawable.setState(StateSet.NOTHING);
                }
                botButton.setPressed(false);
                if (this.currentMessageObject.scheduled) {
                    Toast.makeText(getContext(), LocaleController.getString(R.string.MessageScheduledBotAction), 1).show();
                } else if (botButton.button != null) {
                    this.delegate.didPressBotButton(this, botButton.button);
                }
            } else {
                if (motionEvent.getAction() != 3 || (i = this.pressedBotButton) == -1) {
                    return false;
                }
                BotButton botButton2 = (BotButton) this.botButtons.get(i);
                if (botButton2.selectorDrawable != null) {
                    botButton2.selectorDrawable.setState(StateSet.NOTHING);
                }
                botButton2.setPressed(false);
            }
            this.pressedBotButton = -1;
            invalidateOutbounds();
            return false;
        }
        int widthForButtons = getWidthForButtons();
        int measuredWidth = this.currentMessageObject.isOutOwner() ? (getMeasuredWidth() - widthForButtons) - AndroidUtilities.dp(10.0f) : this.backgroundDrawableLeft + AndroidUtilities.dp(this.mediaBackground ? 1.0f : 7.0f);
        for (int i2 = 0; i2 < this.botButtons.size(); i2++) {
            BotButton botButton3 = (BotButton) this.botButtons.get(i2);
            int dp = (botButton3.y + this.layoutHeight) - AndroidUtilities.dp(2.0f);
            float f = x;
            float f2 = widthForButtons;
            float f3 = measuredWidth;
            if (f >= (botButton3.x * f2) + f3 && f <= (botButton3.x * f2) + f3 + (botButton3.width * f2) && y >= dp && y <= botButton3.height + dp) {
                this.pressedBotButton = i2;
                invalidateOutbounds();
                if (botButton3.selectorDrawable == null) {
                    botButton3.selectorDrawable = Theme.createRadSelectorDrawable(getThemedColor(Theme.key_chat_serviceBackgroundSelector), 6, 6);
                    botButton3.selectorDrawable.setCallback(this);
                    botButton3.selectorDrawable.setBounds(((int) (botButton3.x * f2)) + measuredWidth, dp, ((int) (botButton3.x * f2)) + measuredWidth + ((int) (botButton3.width * f2)), botButton3.height + dp);
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    botButton3.selectorDrawable.setHotspot(f, y);
                }
                botButton3.selectorDrawable.setState(this.pressedState);
                botButton3.setPressed(true);
                final int i3 = this.pressedBotButton;
                postDelayed(new Runnable() {
                    @Override
                    public final void run() {
                        ChatMessageCell.this.lambda$checkBotButtonMotionEvent$4(i3);
                    }
                }, ViewConfiguration.getLongPressTimeout() - 1);
                return true;
            }
        }
        return false;
    }

    private boolean checkCaptionMotionEvent(android.view.MotionEvent r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkCaptionMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkCommentButtonMotionEvent(MotionEvent motionEvent) {
        Drawable drawable;
        Drawable drawable2;
        Drawable drawable3;
        if (!this.drawCommentButton) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null) {
            int i = groupedMessagePosition.flags;
            if ((i & 1) == 0 || (i & 8) == 0) {
                ViewGroup viewGroup = (ViewGroup) getParent();
                int childCount = viewGroup.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = viewGroup.getChildAt(i2);
                    if (childAt != this && (childAt instanceof ChatMessageCell)) {
                        ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                        if (chatMessageCell.drawCommentButton && chatMessageCell.currentMessagesGroup == this.currentMessagesGroup) {
                            int i3 = chatMessageCell.currentPosition.flags;
                            if ((i3 & 1) != 0 && (i3 & 8) != 0) {
                                MotionEvent obtain = MotionEvent.obtain(0L, 0L, motionEvent.getActionMasked(), (motionEvent.getX() + getLeft()) - chatMessageCell.getLeft(), (motionEvent.getY() + getTop()) - chatMessageCell.getTop(), 0);
                                boolean checkCommentButtonMotionEvent = chatMessageCell.checkCommentButtonMotionEvent(obtain);
                                obtain.recycle();
                                return checkCommentButtonMotionEvent;
                            }
                        }
                    }
                }
                return false;
            }
        }
        if (motionEvent.getAction() == 0) {
            if (this.commentButtonRect.contains(x, y)) {
                if (this.currentMessageObject.isSent()) {
                    this.selectorDrawableMaskType[1] = 2;
                    this.commentButtonPressed = true;
                    if (Build.VERSION.SDK_INT >= 21 && (drawable3 = this.selectorDrawable[1]) != null) {
                        drawable3.setHotspot(x, y);
                        this.selectorDrawable[1].setState(this.pressedState);
                    }
                    invalidateWithParent();
                }
                return true;
            }
        } else if (motionEvent.getAction() == 1) {
            if (this.commentButtonPressed) {
                ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                if (chatMessageCellDelegate != null) {
                    if (this.isRepliesChat) {
                        chatMessageCellDelegate.didPressSideButton(this);
                    } else {
                        chatMessageCellDelegate.didPressCommentButton(this);
                    }
                }
                playSoundEffect(0);
                if (Build.VERSION.SDK_INT >= 21 && (drawable2 = this.selectorDrawable[1]) != null) {
                    drawable2.setState(StateSet.NOTHING);
                }
                this.commentButtonPressed = false;
                invalidateWithParent();
            }
        } else if (motionEvent.getAction() == 2 && this.commentButtonPressed && Build.VERSION.SDK_INT >= 21 && (drawable = this.selectorDrawable[1]) != null) {
            drawable.setHotspot(x, y);
        }
        return false;
    }

    private boolean checkContactMotionEvent(MotionEvent motionEvent) {
        Drawable drawable;
        Drawable drawable2;
        ChatMessageCellDelegate chatMessageCellDelegate;
        int i;
        Drawable drawable3;
        if (this.currentMessageObject.type != 12) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            ArrayList arrayList = this.contactButtons;
            if (arrayList != null && arrayList.size() > 1) {
                for (int i2 = 0; i2 < this.contactButtons.size(); i2++) {
                    InstantViewButton instantViewButton = (InstantViewButton) this.contactButtons.get(i2);
                    float f = x;
                    float f2 = y;
                    if (instantViewButton.rect.contains(f, f2)) {
                        if (instantViewButton.buttonBounce == null) {
                            instantViewButton.buttonBounce = new ButtonBounce(this);
                        }
                        instantViewButton.buttonBounce.setPressed(true);
                        if (Build.VERSION.SDK_INT >= 21 && instantViewButton.selectorDrawable != null) {
                            instantViewButton.selectorDrawable.setHotspot(f, f2);
                            instantViewButton.selectorDrawable.setState(this.pressedState);
                        }
                        invalidate();
                        return true;
                    }
                }
            }
            float f3 = x;
            float f4 = y;
            if (this.contactRect.contains(f3, f4)) {
                this.contactPressed = true;
                this.contactBounce.setPressed(true);
                if (Build.VERSION.SDK_INT >= 21 && (drawable3 = this.selectorDrawable[0]) != null) {
                    drawable3.setHotspot(f3, f4);
                    this.selectorDrawable[0].setState(this.pressedState);
                }
                invalidate();
                return true;
            }
        } else if (motionEvent.getAction() == 1) {
            if (this.contactPressed) {
                if (this.delegate != null) {
                    ArrayList arrayList2 = this.contactButtons;
                    if (arrayList2 == null || arrayList2.size() != 1) {
                        chatMessageCellDelegate = this.delegate;
                        i = 5;
                    } else {
                        chatMessageCellDelegate = this.delegate;
                        i = ((InstantViewButton) this.contactButtons.get(0)).type;
                    }
                    chatMessageCellDelegate.didPressInstantButton(this, i);
                }
                playSoundEffect(0);
                if (Build.VERSION.SDK_INT >= 21 && (drawable2 = this.selectorDrawable[0]) != null) {
                    drawable2.setState(StateSet.NOTHING);
                }
                this.contactPressed = false;
                this.contactBounce.setPressed(false);
                invalidate();
            } else {
                ArrayList arrayList3 = this.contactButtons;
                if (arrayList3 != null && arrayList3.size() > 1) {
                    for (int i3 = 0; i3 < this.contactButtons.size(); i3++) {
                        InstantViewButton instantViewButton2 = (InstantViewButton) this.contactButtons.get(i3);
                        if (instantViewButton2.buttonBounce != null && instantViewButton2.buttonBounce.isPressed()) {
                            ChatMessageCellDelegate chatMessageCellDelegate2 = this.delegate;
                            if (chatMessageCellDelegate2 != null) {
                                chatMessageCellDelegate2.didPressInstantButton(this, instantViewButton2.type);
                            }
                            if (Build.VERSION.SDK_INT >= 21 && instantViewButton2.selectorDrawable != null) {
                                instantViewButton2.selectorDrawable.setState(StateSet.NOTHING);
                            }
                            instantViewButton2.buttonBounce.setPressed(false);
                            playSoundEffect(0);
                            invalidate();
                        }
                    }
                }
            }
        } else if (motionEvent.getAction() == 2) {
            if (!this.contactPressed || Build.VERSION.SDK_INT < 21 || (drawable = this.selectorDrawable[0]) == null) {
                ArrayList arrayList4 = this.contactButtons;
                if (arrayList4 != null && arrayList4.size() > 1) {
                    int i4 = 0;
                    while (true) {
                        if (i4 >= this.contactButtons.size()) {
                            break;
                        }
                        InstantViewButton instantViewButton3 = (InstantViewButton) this.contactButtons.get(i4);
                        if (instantViewButton3.buttonBounce == null || !instantViewButton3.buttonBounce.isPressed()) {
                            i4++;
                        } else if (Build.VERSION.SDK_INT >= 21 && instantViewButton3.selectorDrawable != null) {
                            drawable = instantViewButton3.selectorDrawable;
                        }
                    }
                }
            }
            drawable.setHotspot(x, y);
        }
        return false;
    }

    private boolean checkDateMotionEvent(MotionEvent motionEvent) {
        if (!this.currentMessageObject.isImportedForward()) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            float f = x;
            float f2 = this.drawTimeX;
            if (f < f2 || f > f2 + this.timeWidth) {
                return false;
            }
            float f3 = y;
            float f4 = this.drawTimeY;
            if (f3 < f4 || f3 > f4 + AndroidUtilities.dp(20.0f)) {
                return false;
            }
            this.timePressed = true;
        } else {
            if (motionEvent.getAction() != 1 || !this.timePressed) {
                return false;
            }
            this.timePressed = false;
            playSoundEffect(0);
            this.delegate.didPressTime(this);
        }
        invalidate();
        return true;
    }

    private boolean checkEffectMotionEvent(android.view.MotionEvent r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkEffectMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkFactCheckMotionEvent(android.view.MotionEvent r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkFactCheckMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkGameMotionEvent(MotionEvent motionEvent) {
        int i;
        int i2;
        if (!this.hasGamePreview) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            if (this.drawPhotoImage && this.drawImageButton && this.buttonState != -1 && x >= (i = this.buttonX) && x <= i + AndroidUtilities.dp(48.0f) && y >= (i2 = this.buttonY) && y <= i2 + AndroidUtilities.dp(48.0f) && this.radialProgress.getIcon() != 4) {
                this.buttonPressed = 1;
                invalidate();
                return true;
            }
            if (this.drawPhotoImage && this.photoImage.isInsideImage(x, y)) {
                this.gamePreviewPressed = true;
                return true;
            }
            if (this.descriptionLayout != null && y >= this.descriptionY) {
                try {
                    int dp = x - ((this.unmovedTextX + AndroidUtilities.dp(10.0f)) + this.descriptionX);
                    int i3 = y - this.descriptionY;
                    int lineForVertical = this.descriptionLayout.getLineForVertical(i3);
                    float f = dp;
                    int offsetForHorizontal = this.descriptionLayout.getOffsetForHorizontal(lineForVertical, f);
                    float lineLeft = this.descriptionLayout.getLineLeft(lineForVertical);
                    if (lineLeft <= f && lineLeft + this.descriptionLayout.getLineWidth(lineForVertical) >= f) {
                        Spannable spannable = (Spannable) this.currentMessageObject.linkDescription;
                        ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class);
                        if (clickableSpanArr.length != 0 && ((!(clickableSpanArr[0] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled) && !AndroidUtilities.isAccessibilityScreenReaderEnabled())) {
                            LinkSpanDrawable linkSpanDrawable = this.pressedLink;
                            if (linkSpanDrawable == null || linkSpanDrawable.getSpan() != clickableSpanArr[0]) {
                                this.links.removeLink(this.pressedLink);
                                ClickableSpan clickableSpan = clickableSpanArr[0];
                                LinkSpanDrawable linkSpanDrawable2 = new LinkSpanDrawable(clickableSpan, this.resourcesProvider, f, i3, spanSupportsLongPress(clickableSpan));
                                this.pressedLink = linkSpanDrawable2;
                                linkSpanDrawable2.setColor(getThemedColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outLinkSelectBackground : Theme.key_chat_linkSelectBackground));
                                this.linkBlockNum = -10;
                                this.pressedLinkType = 2;
                                try {
                                    LinkPath obtainNewPath = this.pressedLink.obtainNewPath();
                                    int[] realSpanStartAndEnd = getRealSpanStartAndEnd(spannable, this.pressedLink.getSpan());
                                    obtainNewPath.setCurrentLayout(this.descriptionLayout, realSpanStartAndEnd[0], 0.0f);
                                    this.descriptionLayout.getSelectionPath(realSpanStartAndEnd[0], realSpanStartAndEnd[1], obtainNewPath);
                                } catch (Exception e) {
                                    FileLog.e(e);
                                }
                                this.links.addLink(this.pressedLink, 2);
                            }
                            invalidate();
                            return true;
                        }
                    }
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
        } else if (motionEvent.getAction() == 1) {
            if (this.pressedLinkType == 2 || this.gamePreviewPressed || this.buttonPressed != 0) {
                if (this.buttonPressed != 0) {
                    this.buttonPressed = 0;
                    playSoundEffect(0);
                    didPressButton(true, false);
                    invalidate();
                } else {
                    LinkSpanDrawable linkSpanDrawable3 = this.pressedLink;
                    if (linkSpanDrawable3 == null) {
                        this.gamePreviewPressed = false;
                        int i4 = 0;
                        while (true) {
                            if (i4 >= this.botButtons.size()) {
                                break;
                            }
                            BotButton botButton = (BotButton) this.botButtons.get(i4);
                            if (botButton.button instanceof TLRPC.TL_keyboardButtonGame) {
                                playSoundEffect(0);
                                this.delegate.didPressBotButton(this, botButton.button);
                                invalidate();
                                break;
                            }
                            i4++;
                        }
                        resetPressedLink(2);
                        return true;
                    }
                    if (linkSpanDrawable3.getSpan() instanceof URLSpan) {
                        Browser.openUrl(getContext(), ((URLSpan) this.pressedLink.getSpan()).getURL());
                    } else if (this.pressedLink.getSpan() instanceof ClickableSpan) {
                        ((ClickableSpan) this.pressedLink.getSpan()).onClick(this);
                    }
                }
            }
            resetPressedLink(2);
        }
        return false;
    }

    private void checkImageReceiversAttachState() {
        boolean z = this.attachedToWindow;
        boolean z2 = false;
        if (z != this.imageReceiversAttachState) {
            this.imageReceiversAttachState = z;
            if (z) {
                this.radialProgress.onAttachedToWindow();
                this.videoRadialProgress.onAttachedToWindow();
                if (this.pollAvatarImages != null) {
                    int i = 0;
                    while (true) {
                        ImageReceiver[] imageReceiverArr = this.pollAvatarImages;
                        if (i >= imageReceiverArr.length) {
                            break;
                        }
                        imageReceiverArr[i].onAttachedToWindow();
                        i++;
                    }
                }
                if (this.commentAvatarImages != null) {
                    int i2 = 0;
                    while (true) {
                        ImageReceiver[] imageReceiverArr2 = this.commentAvatarImages;
                        if (i2 >= imageReceiverArr2.length) {
                            break;
                        }
                        imageReceiverArr2[i2].onAttachedToWindow();
                        i2++;
                    }
                }
                this.giveawayMessageCell.onAttachedToWindow();
                this.giveawayResultsMessageCell.onAttachedToWindow();
                this.replyImageReceiver.onAttachedToWindow();
                this.locationImageReceiver.onAttachedToWindow();
                this.blurredPhotoImage.onAttachedToWindow();
                if (!this.photoImage.onAttachedToWindow() || this.drawPhotoImage) {
                    updateButtonState(false, false, false);
                }
                this.animatedEmojiReplyStack = AnimatedEmojiSpan.update(0, (View) this, false, this.animatedEmojiReplyStack, this.replyTextLayout);
                this.animatedEmojiDescriptionStack = AnimatedEmojiSpan.update(0, (View) this, false, this.animatedEmojiDescriptionStack, this.descriptionLayout);
                updateAnimatedEmojis();
            } else {
                this.radialProgress.onDetachedFromWindow();
                this.videoRadialProgress.onDetachedFromWindow();
                if (this.pollAvatarImages != null) {
                    int i3 = 0;
                    while (true) {
                        ImageReceiver[] imageReceiverArr3 = this.pollAvatarImages;
                        if (i3 >= imageReceiverArr3.length) {
                            break;
                        }
                        imageReceiverArr3[i3].onDetachedFromWindow();
                        i3++;
                    }
                }
                if (this.commentAvatarImages != null) {
                    int i4 = 0;
                    while (true) {
                        ImageReceiver[] imageReceiverArr4 = this.commentAvatarImages;
                        if (i4 >= imageReceiverArr4.length) {
                            break;
                        }
                        imageReceiverArr4[i4].onDetachedFromWindow();
                        i4++;
                    }
                }
                this.replyImageReceiver.onDetachedFromWindow();
                this.locationImageReceiver.onDetachedFromWindow();
                this.photoImage.onDetachedFromWindow();
                this.blurredPhotoImage.onDetachedFromWindow();
                this.giveawayMessageCell.onDetachedFromWindow();
                this.giveawayResultsMessageCell.onDetachedFromWindow();
                AnimatedEmojiSpan.release(this, this.animatedEmojiDescriptionStack);
                AnimatedEmojiSpan.release(this, this.animatedEmojiReplyStack);
                AnimatedEmojiSpan.release(this, this.animatedEmojiStack);
            }
        }
        if (this.attachedToWindow && (this.visibleOnScreen || !this.shouldCheckVisibleOnScreen)) {
            z2 = true;
        }
        if (z2 != this.imageReceiversVisibleState) {
            this.imageReceiversVisibleState = z2;
            if (z2) {
                fileAttach(true, this.currentMessageObject);
            } else {
                fileDetach(this.currentMessageObject);
            }
        }
    }

    private boolean checkInstantButtonMotionEvent(MotionEvent motionEvent) {
        Drawable drawable;
        Drawable drawable2;
        if (!this.currentMessageObject.isSponsored() && !this.currentMessageObject.isUnsupported() && (!this.drawInstantView || this.currentMessageObject.type == 0)) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            if (this.drawInstantView) {
                float f = x;
                float f2 = y;
                if (this.instantButtonRect.contains(f, f2)) {
                    this.selectorDrawableMaskType[0] = this.lastPoll == null ? 0 : 2;
                    this.instantPressed = true;
                    if (Build.VERSION.SDK_INT >= 21 && this.selectorDrawable[0] != null && this.instantButtonRect.contains(f, f2)) {
                        this.selectorDrawable[0].setHotspot(f, f2);
                        this.selectorDrawable[0].setState(this.pressedState);
                        setInstantButtonPressed(true);
                    }
                    invalidate();
                    return true;
                }
            }
        } else if (motionEvent.getAction() == 1) {
            if (this.instantPressed) {
                ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                if (chatMessageCellDelegate != null) {
                    if (this.lastPoll != null) {
                        MessageObject messageObject = this.currentMessageObject;
                        if (messageObject.scheduled) {
                            Toast.makeText(getContext(), LocaleController.getString(R.string.MessageScheduledVoteResults), 1).show();
                        } else if (!this.pollVoted && !this.pollClosed) {
                            if (!messageObject.checkedVotes.isEmpty()) {
                                this.pollVoteInProgressNum = -1;
                                this.pollVoteInProgress = true;
                                this.vibrateOnPollVote = true;
                                this.voteCurrentProgressTime = 0.0f;
                                this.firstCircleLength = true;
                                this.voteCurrentCircleLength = 360.0f;
                                this.voteRisingCircleLength = false;
                            }
                            this.delegate.didPressVoteButtons(this, this.currentMessageObject.checkedVotes, -1, 0, this.namesOffset);
                        }
                    }
                    chatMessageCellDelegate.didPressInstantButton(this, this.drawInstantViewType);
                }
                playSoundEffect(0);
                if (Build.VERSION.SDK_INT >= 21 && (drawable2 = this.selectorDrawable[0]) != null) {
                    drawable2.setState(StateSet.NOTHING);
                }
                this.instantPressed = false;
                setInstantButtonPressed(false);
                invalidate();
            }
        } else if (motionEvent.getAction() == 2 && this.instantButtonPressed && Build.VERSION.SDK_INT >= 21 && (drawable = this.selectorDrawable[0]) != null) {
            drawable.setHotspot(x, y);
        }
        return false;
    }

    private boolean checkLinkPreviewMotionEvent(MotionEvent motionEvent) {
        int i;
        MessageObject messageObject;
        TLRPC.TL_channelAdminLogEvent tL_channelAdminLogEvent;
        int i2;
        int i3;
        int i4;
        int i5;
        int dp;
        int dp2;
        int i6;
        int themedColor;
        MessageObject messageObject2;
        int i7 = this.currentMessageObject.type;
        if ((i7 != 0 && i7 != 24) || !this.hasLinkPreview) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        int i8 = this.unmovedTextX;
        if (x >= i8 && x <= (i8 + this.backgroundWidth) - AndroidUtilities.dp(14.0f) && y >= (i = this.linkPreviewY)) {
            if (y <= i + this.linkPreviewHeight + AndroidUtilities.dp((this.drawInstantView ? 46 : 0) + 8)) {
                if (motionEvent.getAction() == 0) {
                    if (this.descriptionLayout != null && y >= this.descriptionY && !this.currentMessageObject.preview) {
                        try {
                            int dp3 = x - ((this.unmovedTextX + AndroidUtilities.dp(10.0f)) + this.descriptionX);
                            int i9 = y - this.descriptionY;
                            if (i9 <= this.descriptionLayout.getHeight()) {
                                int lineForVertical = this.descriptionLayout.getLineForVertical(i9);
                                float f = dp3;
                                int offsetForHorizontal = this.descriptionLayout.getOffsetForHorizontal(lineForVertical, f);
                                float lineLeft = this.descriptionLayout.getLineLeft(lineForVertical);
                                if (lineLeft <= f && lineLeft + this.descriptionLayout.getLineWidth(lineForVertical) >= f) {
                                    Spannable spannable = (Spannable) (this.currentMessageObject.isSponsored() ? this.currentMessageObject.messageText : this.currentMessageObject.linkDescription);
                                    ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class);
                                    if (clickableSpanArr.length != 0 && ((!(clickableSpanArr[0] instanceof URLSpanBotCommand) || URLSpanBotCommand.enabled) && !AndroidUtilities.isAccessibilityScreenReaderEnabled())) {
                                        LinkSpanDrawable linkSpanDrawable = this.pressedLink;
                                        if (linkSpanDrawable == null || linkSpanDrawable.getSpan() != clickableSpanArr[0]) {
                                            this.links.removeLink(this.pressedLink);
                                            ClickableSpan clickableSpan = clickableSpanArr[0];
                                            LinkSpanDrawable linkSpanDrawable2 = new LinkSpanDrawable(clickableSpan, this.resourcesProvider, x, y, spanSupportsLongPress(clickableSpan));
                                            this.pressedLink = linkSpanDrawable2;
                                            if (!this.hasLinkPreview || this.linkLine == null || (messageObject2 = this.currentMessageObject) == null || messageObject2.isOutOwner()) {
                                                themedColor = getThemedColor(this.currentMessageObject.isOutOwner() ? Theme.key_chat_outLinkSelectBackground : Theme.key_chat_linkSelectBackground);
                                            } else {
                                                themedColor = Theme.multAlpha(this.linkLine.getColor(), 0.1f);
                                            }
                                            linkSpanDrawable2.setColor(themedColor);
                                            this.linkBlockNum = -10;
                                            this.pressedLinkType = 2;
                                            startCheckLongPress();
                                            try {
                                                LinkPath obtainNewPath = this.pressedLink.obtainNewPath();
                                                int[] realSpanStartAndEnd = getRealSpanStartAndEnd(spannable, this.pressedLink.getSpan());
                                                obtainNewPath.setCurrentLayout(this.descriptionLayout, realSpanStartAndEnd[0], 0.0f);
                                                this.descriptionLayout.getSelectionPath(realSpanStartAndEnd[0], realSpanStartAndEnd[1], obtainNewPath);
                                            } catch (Exception e) {
                                                FileLog.e(e);
                                            }
                                            this.links.addLink(this.pressedLink, 2);
                                        }
                                        invalidate();
                                        return true;
                                    }
                                }
                            }
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                    }
                    if (this.pressedLink == null) {
                        int dp4 = AndroidUtilities.dp(48.0f);
                        if (this.miniButtonState >= 0 && x >= (dp2 = this.buttonX + (dp = AndroidUtilities.dp(27.0f))) && x <= dp2 + dp4 && y >= (i6 = this.buttonY + dp) && y <= i6 + dp4) {
                            this.miniButtonPressed = 1;
                        } else {
                            if (!this.drawVideoImageButton || this.buttonState == -1 || x < (i4 = this.videoButtonX) || x > i4 + AndroidUtilities.dp(34.0f) + Math.max(this.infoWidth, this.docTitleWidth) || y < (i5 = this.videoButtonY) || y > i5 + AndroidUtilities.dp(30.0f)) {
                                if (!(this.drawPhotoImage && this.currentMessageObject.sponsoredMedia != null && this.photoImage.isInsideImage(x, y)) && (!this.drawPhotoImage || !this.drawImageButton || this.buttonState == -1 || ((this.checkOnlyButtonPressed || !this.photoImage.isInsideImage(x, y)) && (x < (i2 = this.buttonX) || x > i2 + AndroidUtilities.dp(48.0f) || y < (i3 = this.buttonY) || y > i3 + AndroidUtilities.dp(48.0f) || this.radialProgress.getIcon() == 4)))) {
                                    this.instantPressed = true;
                                    this.selectorDrawableMaskType[0] = 0;
                                    if (Build.VERSION.SDK_INT >= 21) {
                                        Drawable drawable = this.selectorDrawable[0];
                                        if (drawable != null && drawable.getBounds().contains(x, y)) {
                                            this.selectorDrawable[0].setHotspot(x, y);
                                            this.selectorDrawable[0].setState(this.pressedState);
                                        }
                                        Drawable drawable2 = this.linkPreviewSelector;
                                        if (drawable2 != null && drawable2.getBounds().contains(x, y)) {
                                            this.linkPreviewSelector.setHotspot(x, y);
                                            this.linkPreviewSelector.setState(this.pressedState);
                                        }
                                        setInstantButtonPressed(true);
                                    }
                                    ButtonBounce buttonBounce = this.linkPreviewBounce;
                                    if (buttonBounce != null) {
                                        buttonBounce.setPressed(true);
                                    }
                                    invalidate();
                                    return true;
                                }
                                this.buttonPressed = 1;
                                invalidate();
                                TLRPC.MessageMedia messageMedia = this.currentMessageObject.sponsoredMedia;
                                if (messageMedia != null && (MessageObject.isGifDocument(messageMedia.document) || this.currentMessageObject.sponsoredMedia.photo != null)) {
                                    if (Build.VERSION.SDK_INT >= 21) {
                                        Drawable drawable3 = this.selectorDrawable[0];
                                        if (drawable3 != null && drawable3.getBounds().contains(x, y)) {
                                            this.selectorDrawable[0].setHotspot(x, y);
                                            this.selectorDrawable[0].setState(this.pressedState);
                                        }
                                        Drawable drawable4 = this.linkPreviewSelector;
                                        if (drawable4 != null && drawable4.getBounds().contains(x, y)) {
                                            this.linkPreviewSelector.setHotspot(x, y);
                                            this.linkPreviewSelector.setState(this.pressedState);
                                        }
                                        setInstantButtonPressed(true);
                                    }
                                    ButtonBounce buttonBounce2 = this.linkPreviewBounce;
                                    if (buttonBounce2 != null) {
                                        buttonBounce2.setPressed(true);
                                    }
                                }
                                return true;
                            }
                            this.videoButtonPressed = 1;
                        }
                        invalidate();
                        return true;
                    }
                } else if (motionEvent.getAction() == 1) {
                    if (this.instantPressed) {
                        int i10 = this.documentAttachType;
                        if (i10 == 7) {
                            if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || MediaController.getInstance().isMessagePaused()) {
                                this.delegate.needPlayMessage(this, this.currentMessageObject, false);
                            } else {
                                MediaController.getInstance().lambda$startAudioAgain$7(this.currentMessageObject);
                            }
                        } else if (this.drawInstantView || !((messageObject = this.currentMessageObject) == null || (tL_channelAdminLogEvent = messageObject.currentEvent) == null || !(tL_channelAdminLogEvent.action instanceof TLRPC.TL_channelAdminLogEventActionEditMessage))) {
                            ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                            if (chatMessageCellDelegate != null) {
                                chatMessageCellDelegate.didPressInstantButton(this, this.drawInstantViewType);
                            }
                        } else if (this.drawPhotoImage && (i10 == 2 || i10 == 4 || (this.authorLayout == null && this.titleLayout == null && this.descriptionLayout == null && this.siteNameLayout == null))) {
                            ChatMessageCellDelegate chatMessageCellDelegate2 = this.delegate;
                            if (chatMessageCellDelegate2 != null) {
                                chatMessageCellDelegate2.didPressImage(this, this.lastTouchX, this.lastTouchY);
                            }
                        } else if (!messageObject.preview) {
                            TLRPC.WebPage webPage = MessageObject.getMedia(messageObject.messageOwner).webpage;
                            if (webPage != null && !TextUtils.isEmpty(webPage.embed_url)) {
                                ChatMessageCellDelegate chatMessageCellDelegate3 = this.delegate;
                                if (chatMessageCellDelegate3 != null) {
                                    chatMessageCellDelegate3.needOpenWebView(this.currentMessageObject, webPage.embed_url, webPage.site_name, webPage.title, webPage.url, webPage.embed_width, webPage.embed_height);
                                }
                            } else if (webPage != null) {
                                ChatMessageCellDelegate chatMessageCellDelegate4 = this.delegate;
                                if (chatMessageCellDelegate4 != null) {
                                    chatMessageCellDelegate4.didPressWebPage(this, webPage, webPage.url, MessageObject.getMedia(this.currentMessageObject.messageOwner).safe);
                                } else {
                                    Browser.openUrl(getContext(), webPage.url);
                                }
                            }
                        }
                        playSoundEffect(0);
                        if (Build.VERSION.SDK_INT >= 21) {
                            Drawable drawable5 = this.selectorDrawable[0];
                            if (drawable5 != null) {
                                drawable5.setState(StateSet.NOTHING);
                            }
                            Drawable drawable6 = this.linkPreviewSelector;
                            if (drawable6 != null) {
                                drawable6.setState(StateSet.NOTHING);
                            }
                        }
                        ButtonBounce buttonBounce3 = this.linkPreviewBounce;
                        if (buttonBounce3 != null) {
                            buttonBounce3.setPressed(false);
                        }
                        this.instantPressed = false;
                        setInstantButtonPressed(false);
                    } else {
                        if (this.pressedLinkType != 2 && this.buttonPressed == 0 && this.miniButtonPressed == 0 && this.videoButtonPressed == 0 && !this.linkPreviewPressed) {
                            if (!this.hadLongPress) {
                                this.hadLongPress = false;
                            }
                        } else if (this.videoButtonPressed == 1) {
                            this.videoButtonPressed = 0;
                            playSoundEffect(0);
                            didPressButton(true, true);
                        } else if (this.buttonPressed != 0) {
                            this.buttonPressed = 0;
                            playSoundEffect(0);
                            if (this.drawVideoImageButton || this.currentMessageObject.sponsoredMedia != null) {
                                didClickedImage();
                            } else {
                                didPressButton(true, false);
                            }
                        } else if (this.miniButtonPressed != 0) {
                            this.miniButtonPressed = 0;
                            playSoundEffect(0);
                            didPressMiniButton(true);
                        } else {
                            LinkSpanDrawable linkSpanDrawable3 = this.pressedLink;
                            if (linkSpanDrawable3 == null) {
                                AnimatedEmojiSpan animatedEmojiSpan = this.pressedEmoji;
                                if (animatedEmojiSpan == null || !this.delegate.didPressAnimatedEmoji(this, animatedEmojiSpan)) {
                                    if (this.documentAttachType == 2 && this.drawImageButton) {
                                        int i11 = this.buttonState;
                                        if (i11 == -1) {
                                            if (!SharedConfig.isAutoplayGifs() || this.currentMessageObject.isRepostPreview) {
                                                this.buttonState = 2;
                                                this.currentMessageObject.gifState = 1.0f;
                                                this.photoImage.setAllowStartAnimation(false);
                                                this.photoImage.stopAnimation();
                                                this.radialProgress.setIcon(getIconForCurrentState(), false, true);
                                                invalidate();
                                                playSoundEffect(0);
                                            } else {
                                                this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                                            }
                                        } else if (i11 == 2 || i11 == 0) {
                                            didPressButton(true, false);
                                            playSoundEffect(0);
                                        }
                                    } else {
                                        MessageObject messageObject3 = this.currentMessageObject;
                                        if (!messageObject3.preview) {
                                            TLRPC.WebPage webPage2 = MessageObject.getMedia(messageObject3.messageOwner).webpage;
                                            if (webPage2 == null || TextUtils.isEmpty(webPage2.embed_url)) {
                                                int i12 = this.buttonState;
                                                if (i12 == -1 || i12 == 3) {
                                                    this.delegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
                                                    playSoundEffect(0);
                                                } else if (webPage2 != null) {
                                                    ChatMessageCellDelegate chatMessageCellDelegate5 = this.delegate;
                                                    if (chatMessageCellDelegate5 != null) {
                                                        chatMessageCellDelegate5.didPressWebPage(this, webPage2, webPage2.url, MessageObject.getMedia(this.currentMessageObject.messageOwner).safe);
                                                    } else {
                                                        Browser.openUrl(getContext(), webPage2.url);
                                                    }
                                                }
                                            } else {
                                                this.delegate.needOpenWebView(this.currentMessageObject, webPage2.embed_url, webPage2.site_name, webPage2.title, webPage2.url, webPage2.embed_width, webPage2.embed_height);
                                            }
                                        }
                                    }
                                    resetPressedLink(2);
                                    return true;
                                }
                                this.pressedEmoji = null;
                            } else if (linkSpanDrawable3.getSpan() instanceof URLSpan) {
                                this.delegate.didPressUrl(this, this.pressedLink.getSpan(), false);
                            } else if (this.pressedLink.getSpan() instanceof ClickableSpan) {
                                ((ClickableSpan) this.pressedLink.getSpan()).onClick(this);
                            }
                        }
                        resetPressedLink(2);
                    }
                    invalidate();
                } else if (motionEvent.getAction() == 2 && this.instantButtonPressed && Build.VERSION.SDK_INT >= 21) {
                    Drawable drawable7 = this.selectorDrawable[0];
                    if (drawable7 != null) {
                        drawable7.setHotspot(x, y);
                    }
                    Drawable drawable8 = this.linkPreviewSelector;
                    if (drawable8 != null) {
                        drawable8.setHotspot(x, y);
                    }
                }
            }
        }
        return false;
    }

    public void checkLocationExpired() {
        boolean isCurrentLocationTimeExpired;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || (isCurrentLocationTimeExpired = isCurrentLocationTimeExpired(messageObject)) == this.locationExpired) {
            return;
        }
        this.locationExpired = isCurrentLocationTimeExpired;
        if (isCurrentLocationTimeExpired) {
            MessageObject messageObject2 = this.currentMessageObject;
            this.currentMessageObject = null;
            setMessageObject(messageObject2, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
        } else {
            AndroidUtilities.runOnUIThread(this.invalidateRunnable, 1000L);
            this.scheduledInvalidate = true;
            int dp = this.backgroundWidth - AndroidUtilities.dp(91.0f);
            this.docTitleLayout = new StaticLayout(TextUtils.ellipsize(LocaleController.getString(R.string.AttachLiveLocation), Theme.chat_locationTitlePaint, dp, TextUtils.TruncateAt.END), Theme.chat_locationTitlePaint, dp, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }
    }

    private boolean checkNameMotionEvent(MotionEvent motionEvent) {
        Drawable drawable;
        ChatMessageCellDelegate chatMessageCellDelegate;
        TLRPC.Chat chat;
        int i;
        if (!this.drawNameLayout || this.nameLayout == null || (drawable = this.nameLayoutSelector) == null || (this.currentUser == null && this.currentChat == null)) {
            this.nameLayoutPressed = false;
            return false;
        }
        boolean contains = drawable.getBounds().contains((int) motionEvent.getX(), (int) motionEvent.getY());
        if (motionEvent.getAction() == 0) {
            this.nameLayoutPressed = contains;
            if (contains) {
                if (Build.VERSION.SDK_INT >= 21) {
                    this.nameLayoutSelector.setHotspot((int) motionEvent.getX(), (int) motionEvent.getY());
                }
                this.nameLayoutSelector.setState(this.pressedState);
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (motionEvent.getAction() == 1 && this.nameLayoutPressed && (chatMessageCellDelegate = this.delegate) != null) {
                if (this.viaOnly) {
                    TLRPC.User user = this.currentViaBotUser;
                    if (user == null || user.bot_inline_placeholder != null) {
                        chatMessageCellDelegate.didPressViaBot(this, user != null ? user.username : this.currentMessageObject.messageOwner.via_bot_name);
                    } else {
                        chatMessageCellDelegate.didPressViaBotNotInline(this, user.id);
                    }
                } else {
                    TLRPC.User user2 = this.currentUser;
                    if (user2 != null) {
                        chatMessageCellDelegate.didPressUserAvatar(this, user2, motionEvent.getX(), motionEvent.getY(), false);
                    } else {
                        TLRPC.Chat chat2 = this.currentChat;
                        if (chat2 != null) {
                            TLRPC.MessageFwdHeader messageFwdHeader = this.currentMessageObject.messageOwner.fwd_from;
                            if (messageFwdHeader != null) {
                                int i2 = messageFwdHeader.channel_post;
                                chat = this.currentForwardChannel;
                                i = i2;
                            } else {
                                chat = chat2;
                                i = 0;
                            }
                            chatMessageCellDelegate.didPressChannelAvatar(this, chat != null ? chat : chat2, i, this.lastTouchX, this.lastTouchY, false);
                        }
                    }
                }
            }
            this.nameLayoutSelector.setState(StateSet.NOTHING);
            this.nameLayoutPressed = false;
        }
        return this.nameLayoutPressed;
    }

    private boolean checkNameStatusMotionEvent(MotionEvent motionEvent) {
        if (!this.drawNameLayout || this.nameLayout == null || this.nameLayoutSelector == null || ((this.currentUser == null && this.currentChat == null) || this.currentNameStatus == null || this.currentNameStatusDrawable == null)) {
            this.nameStatusPressed = false;
            return false;
        }
        boolean contains = this.nameStatusSelector.getBounds().contains((int) motionEvent.getX(), (int) motionEvent.getY());
        if (motionEvent.getAction() == 0) {
            this.nameStatusPressed = contains;
            if (contains) {
                if (Build.VERSION.SDK_INT >= 21) {
                    this.nameStatusSelector.setHotspot((int) motionEvent.getX(), (int) motionEvent.getY());
                }
                this.nameStatusSelector.setState(this.pressedState);
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (motionEvent.getAction() == 1 && this.nameStatusPressed && this.delegate != null && this.currentUser != null) {
                this.delegate.didPressUserStatus(this, this.currentUser, this.currentNameStatusDrawable.getDrawable() instanceof AnimatedEmojiDrawable ? ((AnimatedEmojiDrawable) this.currentNameStatusDrawable.getDrawable()).getDocument() : null);
                invalidateOutbounds();
            }
            this.nameStatusSelector.setState(StateSet.NOTHING);
            this.nameStatusPressed = false;
        }
        return this.nameStatusPressed;
    }

    private boolean checkOtherButtonMotionEvent(MotionEvent motionEvent) {
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        Drawable drawable;
        Drawable drawable2;
        int i = this.documentAttachType;
        if ((i == 5 || i == 1) && (groupedMessagePosition = this.currentPosition) != null && (groupedMessagePosition.flags & 4) == 0) {
            return false;
        }
        int i2 = this.currentMessageObject.type;
        boolean z = i2 == 16;
        if (!z) {
            z = ((i != 1 && i2 != 12 && i != 5 && i != 4 && i != 2 && i2 != 8) || this.hasGamePreview || this.hasInvoicePreview) ? false : true;
        }
        if (!z) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() != 0) {
            if (motionEvent.getAction() == 1) {
                if (this.otherPressed) {
                    if (this.currentMessageObject.type == 16 && Build.VERSION.SDK_INT >= 21 && (drawable2 = this.selectorDrawable[0]) != null) {
                        drawable2.setState(StateSet.NOTHING);
                    }
                    this.otherPressed = false;
                    playSoundEffect(0);
                    this.delegate.didPressOther(this, this.otherX, this.otherY);
                    invalidate();
                    return true;
                }
            } else if (motionEvent.getAction() == 2 && this.currentMessageObject.type == 16 && this.otherPressed && Build.VERSION.SDK_INT >= 21 && (drawable = this.selectorDrawable[0]) != null) {
                drawable.setHotspot(x, y);
            }
            return false;
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject.type != 16) {
            if (x >= this.otherX - AndroidUtilities.dp(20.0f) && x <= this.otherX + AndroidUtilities.dp(20.0f) && y >= this.otherY - AndroidUtilities.dp(4.0f) && y <= this.otherY + AndroidUtilities.dp(30.0f)) {
                this.otherPressed = true;
                invalidate();
                return true;
            }
            return false;
        }
        boolean isVideoCall = messageObject.isVideoCall();
        int i3 = this.otherX;
        if (x >= i3) {
            if (x <= i3 + AndroidUtilities.dp((LocaleController.isRTL ? 0 : 200) + 30 + (!isVideoCall ? 2 : 0)) && y >= this.otherY - AndroidUtilities.dp(14.0f) && y <= this.otherY + AndroidUtilities.dp(50.0f)) {
                this.otherPressed = true;
                this.selectorDrawableMaskType[0] = 4;
                if (Build.VERSION.SDK_INT >= 21 && this.selectorDrawable[0] != null) {
                    int dp = this.otherX + AndroidUtilities.dp((LocaleController.isRTL ? 0 : 200) + (!isVideoCall ? 2 : 0)) + (Theme.chat_msgInCallDrawable[isVideoCall ? 1 : 0].getIntrinsicWidth() / 2);
                    int intrinsicHeight = this.otherY + (Theme.chat_msgInCallDrawable[isVideoCall ? 1 : 0].getIntrinsicHeight() / 2);
                    this.selectorDrawable[0].setBounds(dp - AndroidUtilities.dp(20.0f), intrinsicHeight - AndroidUtilities.dp(20.0f), dp + AndroidUtilities.dp(20.0f), intrinsicHeight + AndroidUtilities.dp(20.0f));
                    this.selectorDrawable[0].setHotspot(x, y);
                    this.selectorDrawable[0].setState(this.pressedState);
                }
                invalidate();
                return true;
            }
        }
        return false;
    }

    private boolean checkPhotoImageMotionEvent(android.view.MotionEvent r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkPhotoImageMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkPinchToZoom(MotionEvent motionEvent) {
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        PinchToZoomHelper pinchToZoomHelper = chatMessageCellDelegate == null ? null : chatMessageCellDelegate.getPinchToZoomHelper();
        if (this.currentMessageObject == null || !this.photoImage.hasNotThumb() || pinchToZoomHelper == null || this.currentMessageObject.isSticker() || this.currentMessageObject.isAnimatedEmoji() || ((this.currentMessageObject.isVideo() && !this.autoPlayingMedia) || this.isRoundVideo || this.currentMessageObject.isAnimatedSticker() || ((this.currentMessageObject.isDocument() && !this.currentMessageObject.isGif()) || this.currentMessageObject.needDrawBluredPreview()))) {
            return false;
        }
        ImageReceiver imageReceiver = this.photoImage;
        MessageObject messageObject = this.currentMessageObject;
        SpoilerEffect2 spoilerEffect2 = this.mediaSpoilerEffect2;
        return pinchToZoomHelper.checkPinchToZoom(motionEvent, this, imageReceiver, null, null, messageObject, spoilerEffect2 == null ? 0 : spoilerEffect2.getAttachIndex(this));
    }

    private boolean checkPollButtonMotionEvent(MotionEvent motionEvent) {
        Drawable drawable;
        ArrayList arrayList;
        int i;
        int dp;
        int i2;
        ChatMessageCellDelegate chatMessageCellDelegate;
        Drawable drawable2;
        Drawable drawable3;
        Drawable drawable4;
        int i3;
        int i4;
        Drawable drawable5;
        if (this.currentMessageObject.eventId != 0 || this.pollVoteInProgress || this.pollUnvoteInProgress || this.pollButtons.isEmpty()) {
            return false;
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject.type != 17 || !messageObject.isSent()) {
            return false;
        }
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 0) {
            this.pressedVoteButton = -1;
            this.pollHintPressed = false;
            if (!this.hintButtonVisible || (i3 = this.pollHintX) == -1 || x < i3 || x > i3 + AndroidUtilities.dp(40.0f) || y < (i4 = this.pollHintY) || y > i4 + AndroidUtilities.dp(40.0f)) {
                for (int i5 = 0; i5 < this.pollButtons.size(); i5++) {
                    PollButton pollButton = (PollButton) this.pollButtons.get(i5);
                    int dp2 = (pollButton.y + this.namesOffset) - AndroidUtilities.dp(13.0f);
                    int i6 = pollButton.x;
                    if (x >= i6 && x <= (i6 + this.backgroundWidth) - AndroidUtilities.dp(31.0f) && y >= dp2 && y <= pollButton.height + dp2 + AndroidUtilities.dp(26.0f)) {
                        this.pressedVoteButton = i5;
                        if (!this.pollVoted && !this.pollClosed) {
                            this.selectorDrawableMaskType[0] = 1;
                            if (Build.VERSION.SDK_INT >= 21 && (drawable4 = this.selectorDrawable[0]) != null) {
                                drawable4.setBounds(pollButton.x - AndroidUtilities.dp(9.0f), dp2, (pollButton.x + this.backgroundWidth) - AndroidUtilities.dp(22.0f), pollButton.height + dp2 + AndroidUtilities.dp(26.0f));
                                this.selectorDrawable[0].setHotspot(x, y);
                                this.selectorDrawable[0].setState(this.pressedState);
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
            this.pollHintPressed = true;
            this.selectorDrawableMaskType[0] = 3;
            if (Build.VERSION.SDK_INT >= 21 && (drawable5 = this.selectorDrawable[0]) != null) {
                drawable5.setBounds(this.pollHintX - AndroidUtilities.dp(8.0f), this.pollHintY - AndroidUtilities.dp(8.0f), this.pollHintX + AndroidUtilities.dp(32.0f), this.pollHintY + AndroidUtilities.dp(32.0f));
                this.selectorDrawable[0].setHotspot(x, y);
                this.selectorDrawable[0].setState(this.pressedState);
            }
            invalidate();
            return true;
        }
        if (motionEvent.getAction() != 1) {
            if (motionEvent.getAction() != 2) {
                return false;
            }
            if ((this.pressedVoteButton == -1 && !this.pollHintPressed) || Build.VERSION.SDK_INT < 21 || (drawable = this.selectorDrawable[0]) == null) {
                return false;
            }
            drawable.setHotspot(x, y);
            return false;
        }
        if (this.pollHintPressed) {
            playSoundEffect(0);
            this.delegate.didPressHint(this, 0);
            this.pollHintPressed = false;
            if (Build.VERSION.SDK_INT < 21 || (drawable3 = this.selectorDrawable[0]) == null) {
                return false;
            }
            drawable3.setState(StateSet.NOTHING);
            return false;
        }
        if (this.pressedVoteButton == -1) {
            return false;
        }
        playSoundEffect(0);
        if (Build.VERSION.SDK_INT >= 21 && (drawable2 = this.selectorDrawable[0]) != null) {
            drawable2.setState(StateSet.NOTHING);
        }
        if (this.currentMessageObject.scheduled) {
            Toast.makeText(getContext(), LocaleController.getString(R.string.MessageScheduledVote), 1).show();
        } else {
            PollButton pollButton2 = (PollButton) this.pollButtons.get(this.pressedVoteButton);
            TLRPC.PollAnswer pollAnswer = pollButton2.answer;
            if (this.pollVoted || this.pollClosed) {
                arrayList = new ArrayList();
                arrayList.add(pollAnswer);
                ChatMessageCellDelegate chatMessageCellDelegate2 = this.delegate;
                i = pollButton2.count;
                dp = pollButton2.x + AndroidUtilities.dp(50.0f);
                i2 = this.namesOffset + pollButton2.y;
                chatMessageCellDelegate = chatMessageCellDelegate2;
            } else if (!this.lastPoll.multiple_choice) {
                this.pollVoteInProgressNum = this.pressedVoteButton;
                this.pollVoteInProgress = true;
                this.vibrateOnPollVote = true;
                this.voteCurrentProgressTime = 0.0f;
                this.firstCircleLength = true;
                this.voteCurrentCircleLength = 360.0f;
                this.voteRisingCircleLength = false;
                arrayList = new ArrayList();
                arrayList.add(pollAnswer);
                chatMessageCellDelegate = this.delegate;
                dp = 0;
                i2 = 0;
                i = -1;
            } else if (this.currentMessageObject.checkedVotes.contains(pollAnswer)) {
                this.currentMessageObject.checkedVotes.remove(pollAnswer);
                this.pollCheckBox[this.pressedVoteButton].setChecked(false, true);
            } else {
                this.currentMessageObject.checkedVotes.add(pollAnswer);
                this.pollCheckBox[this.pressedVoteButton].setChecked(true, true);
            }
            chatMessageCellDelegate.didPressVoteButtons(this, arrayList, i, dp, i2);
        }
        this.pressedVoteButton = -1;
        invalidate();
        return false;
    }

    private boolean checkReactionsTouchEvent(MotionEvent motionEvent) {
        MessageObject.GroupedMessages groupedMessages;
        if (this.currentMessageObject.hasValidGroupId() && (groupedMessages = this.currentMessagesGroup) != null && !groupedMessages.isDocuments) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            if (viewGroup == null) {
                return false;
            }
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    MessageObject.GroupedMessages currentMessagesGroup = chatMessageCell.getCurrentMessagesGroup();
                    MessageObject.GroupedMessagePosition currentPosition = chatMessageCell.getCurrentPosition();
                    if (currentMessagesGroup != null && currentMessagesGroup.groupId == this.currentMessagesGroup.groupId) {
                        int i2 = currentPosition.flags;
                        if ((i2 & 8) != 0 && (i2 & 1) != 0) {
                            if (chatMessageCell != this) {
                                motionEvent.offsetLocation(getLeft() - chatMessageCell.getLeft(), getTop() - chatMessageCell.getTop());
                                boolean checkTouchEvent = chatMessageCell.reactionsLayoutInBubble.checkTouchEvent(motionEvent);
                                motionEvent.offsetLocation(-(getLeft() - chatMessageCell.getLeft()), -(getTop() - chatMessageCell.getTop()));
                                return checkTouchEvent;
                            }
                        }
                    }
                }
            }
            return false;
        }
        return this.reactionsLayoutInBubble.checkTouchEvent(motionEvent);
    }

    private boolean checkReplyTouchEvent(android.view.MotionEvent r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkReplyTouchEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkRoundSeekbar(android.view.MotionEvent r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkRoundSeekbar(android.view.MotionEvent):boolean");
    }

    private boolean checkSponsoredCloseMotionEvent(android.view.MotionEvent r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkSponsoredCloseMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkTextBlockMotionEvent(android.view.MotionEvent r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkTextBlockMotionEvent(android.view.MotionEvent):boolean");
    }

    private boolean checkTextSelection(MotionEvent motionEvent) {
        TextSelectionHelper.ChatListTextSelectionHelper textSelectionHelper;
        int i;
        int i2;
        int i3;
        int dp;
        MessageObject.GroupedMessages groupedMessages;
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        if (chatMessageCellDelegate == null || (textSelectionHelper = chatMessageCellDelegate.getTextSelectionHelper()) == null || textSelectionHelper.isMenuEmpty()) {
            return false;
        }
        ArrayList<MessageObject.TextLayoutBlock> arrayList = this.currentMessageObject.textLayoutBlocks;
        if ((arrayList == null || arrayList.isEmpty()) && !hasCaptionLayout()) {
            return false;
        }
        if ((!this.drawSelectionBackground && this.currentMessagesGroup == null) || (this.currentMessagesGroup != null && !this.delegate.hasSelectedMessages())) {
            return false;
        }
        if (this.currentMessageObject.hasValidGroupId() && (groupedMessages = this.currentMessagesGroup) != null && !groupedMessages.isDocuments) {
            ViewGroup viewGroup = (ViewGroup) getParent();
            if (viewGroup == null) {
                return false;
            }
            for (int i4 = 0; i4 < viewGroup.getChildCount(); i4++) {
                View childAt = viewGroup.getChildAt(i4);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    MessageObject.GroupedMessages currentMessagesGroup = chatMessageCell.getCurrentMessagesGroup();
                    MessageObject.GroupedMessagePosition currentPosition = chatMessageCell.getCurrentPosition();
                    if (currentMessagesGroup != null && currentMessagesGroup.groupId == this.currentMessagesGroup.groupId && (currentPosition.flags & captionFlag()) != 0 && (currentPosition.flags & 1) != 0) {
                        textSelectionHelper.setMaybeTextCord((int) chatMessageCell.captionX, (int) chatMessageCell.captionY);
                        textSelectionHelper.setMessageObject(chatMessageCell);
                        if (chatMessageCell == this) {
                            return textSelectionHelper.onTouchEvent(motionEvent);
                        }
                        motionEvent.offsetLocation(getLeft() - chatMessageCell.getLeft(), getTop() - chatMessageCell.getTop());
                        boolean onTouchEvent = textSelectionHelper.onTouchEvent(motionEvent);
                        motionEvent.offsetLocation(-(getLeft() - chatMessageCell.getLeft()), -(getTop() - chatMessageCell.getTop()));
                        return onTouchEvent;
                    }
                }
            }
            return false;
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && !messageObject.preview && this.factCheckTextLayout != null && motionEvent.getY() >= this.factCheckY) {
            textSelectionHelper.setIsDescription(false);
            textSelectionHelper.setIsFactCheck(true);
            MessageObject.GroupedMessages groupedMessages2 = this.currentMessagesGroup;
            if (groupedMessages2 == null || groupedMessages2.isDocuments) {
                float f = 12.0f;
                if (this.currentMessageObject.isOutOwner()) {
                    int dp2 = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f) + getExtraTextX();
                    dp = this.currentMessageObject.type == 19 ? dp2 - Math.max(0, ((Math.max(this.replyNameWidth, this.replyTextWidth) + dp2) + AndroidUtilities.dp(14.0f)) - AndroidUtilities.displaySize.x) : dp2;
                } else {
                    if (this.mediaBackground) {
                        i3 = this.backgroundDrawableLeft;
                    } else {
                        i3 = this.backgroundDrawableLeft;
                        if (!this.drawPinnedBottom) {
                            f = 18.0f;
                        }
                    }
                    dp = i3 + AndroidUtilities.dp(f) + getExtraTextX();
                }
            } else {
                dp = (int) this.captionX;
            }
            int dp3 = ((int) (dp + this.transitionParams.deltaLeft)) - AndroidUtilities.dp(1.33f);
            int i5 = this.factCheckY;
            i = (dp3 + AndroidUtilities.dp(10.0f)) - this.factCheckTextLayoutLeft;
            i2 = i5 + AndroidUtilities.dp(22.0f);
        } else if (hasCaptionLayout()) {
            textSelectionHelper.setIsDescription(false);
            textSelectionHelper.setIsFactCheck(false);
            i = (int) this.captionX;
            i2 = (int) this.captionY;
        } else {
            MessageObject messageObject2 = this.currentMessageObject;
            if (messageObject2 == null || messageObject2.preview || this.descriptionLayout == null || (!this.linkPreviewAbove ? motionEvent.getY() <= this.descriptionY : motionEvent.getY() >= this.textY)) {
                textSelectionHelper.setIsDescription(false);
                textSelectionHelper.setIsFactCheck(false);
                i = this.textX;
                i2 = this.textY;
            } else {
                textSelectionHelper.setIsDescription(true);
                textSelectionHelper.setIsFactCheck(false);
                i = (this.hasGamePreview ? this.unmovedTextX - AndroidUtilities.dp(10.0f) : this.unmovedTextX + AndroidUtilities.dp(1.0f)) + AndroidUtilities.dp(10.0f) + this.descriptionX;
                i2 = this.descriptionY;
            }
        }
        textSelectionHelper.setMaybeTextCord(i, i2);
        textSelectionHelper.setMessageObject(this);
        return textSelectionHelper.onTouchEvent(motionEvent);
    }

    private boolean checkTitleLabelMotion(MotionEvent motionEvent) {
        if (this.currentMessageObject.isSponsored() && this.currentMessageObject.sponsoredCanReport) {
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            if (motionEvent.getAction() == 0) {
                float f = x;
                if (f >= this.titleLabelX - AndroidUtilities.dp(6.0f) && f <= this.titleLabelX + this.titleLabelLayoutWidth + AndroidUtilities.dp(6.0f)) {
                    float f2 = y;
                    if (f2 >= this.titleLabelY - AndroidUtilities.dp(2.0f) && f2 <= this.titleLabelY + this.titleLabelLayoutHeight + AndroidUtilities.dp(2.0f)) {
                        ButtonBounce buttonBounce = this.titleLabelBounce;
                        if (buttonBounce != null) {
                            buttonBounce.setPressed(true);
                        }
                        this.isTitleLabelPressed = true;
                        return true;
                    }
                }
            } else if (motionEvent.getAction() == 1 && this.isTitleLabelPressed) {
                ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                if (chatMessageCellDelegate != null) {
                    chatMessageCellDelegate.didPressAboutRevenueSharingAds();
                }
                ButtonBounce buttonBounce2 = this.titleLabelBounce;
                if (buttonBounce2 != null) {
                    buttonBounce2.setPressed(false);
                }
                playSoundEffect(0);
                this.isTitleLabelPressed = false;
            }
        }
        return false;
    }

    private boolean checkTranscribeButtonMotionEvent(MotionEvent motionEvent) {
        TranscribeButton transcribeButton;
        return this.useTranscribeButton && (!this.isPlayingRound || getVideoTranscriptionProgress() > 0.0f || this.wasTranscriptionOpen) && (transcribeButton = this.transcribeButton) != null && transcribeButton.onTouch(motionEvent.getAction(), motionEvent.getX(), motionEvent.getY());
    }

    private void createCommentUI() {
        if (this.commentAvatarImages != null) {
            return;
        }
        this.commentAvatarImages = new ImageReceiver[3];
        this.commentAvatarDrawables = new AvatarDrawable[3];
        this.commentAvatarImagesVisible = new boolean[3];
        int i = 0;
        while (true) {
            ImageReceiver[] imageReceiverArr = this.commentAvatarImages;
            if (i >= imageReceiverArr.length) {
                return;
            }
            imageReceiverArr[i] = new ImageReceiver(this);
            this.commentAvatarImages[i].setRoundRadius(AndroidUtilities.dp(12.0f));
            this.commentAvatarDrawables[i] = new AvatarDrawable();
            this.commentAvatarDrawables[i].setTextSize(AndroidUtilities.dp(18.0f));
            i++;
        }
    }

    private void createContactButtons() {
        int i;
        boolean z;
        if (Build.VERSION.SDK_INT >= 21) {
            createSelectorDrawable(0);
        }
        if (this.drawContact) {
            boolean z2 = this.drawContactView;
            if (this.drawContactSendMessage) {
                boolean z3 = (z2 ? 1 : 0) | 2;
                i = (z2 ? 1 : 0) + 1;
                z = z3;
            } else {
                i = z2 ? 1 : 0;
                z = z2;
            }
            boolean z4 = z;
            if (this.drawContactAdd) {
                i++;
                z4 = (z ? 1 : 0) | 4;
            }
            if (i == 0) {
                this.contactButtons = null;
                this.drawnContactButtonsFlag = 0;
                return;
            }
            this.totalHeight += AndroidUtilities.dp(60.0f);
            if (z4 != this.drawnContactButtonsFlag) {
                this.drawnContactButtonsFlag = 0;
                int dp = (this.backgroundWidth - AndroidUtilities.dp(75.0f)) / i;
                float dpf2 = (this.backgroundWidth - AndroidUtilities.dpf2(37.0f)) / i;
                ArrayList arrayList = this.contactButtons;
                if (arrayList == null) {
                    this.contactButtons = new ArrayList(i);
                } else {
                    arrayList.clear();
                }
                if (this.drawContactView) {
                    this.drawnContactButtonsFlag |= 1;
                    this.contactButtons.add(createInstantViewButton(5, LocaleController.getString("ViewContact", R.string.ViewContact), dp, dpf2));
                }
                if (this.drawContactSendMessage) {
                    this.drawnContactButtonsFlag |= 2;
                    this.contactButtons.add(createInstantViewButton(30, LocaleController.getString("SharedContactMessage", R.string.SharedContactMessage), dp, dpf2));
                }
                if (this.drawContactAdd) {
                    this.drawnContactButtonsFlag |= 4;
                    this.contactButtons.add(createInstantViewButton(31, LocaleController.getString("SharedContactAdd", R.string.SharedContactAdd), dp, dpf2));
                }
            }
        }
    }

    private int createDocumentLayout(int i, MessageObject messageObject) {
        TLRPC.Document document;
        int i2;
        int i3 = i;
        int i4 = 0;
        TLRPC.MessageMedia messageMedia = messageObject.sponsoredMedia;
        if (messageMedia != null) {
            document = messageMedia.document;
        } else if (messageObject.type == 0) {
            TLRPC.MessageMedia media = MessageObject.getMedia(messageObject.messageOwner);
            TLRPC.WebPage webPage = media == null ? null : media.webpage;
            document = webPage == null ? null : webPage.document;
        } else {
            document = messageObject.getDocument();
        }
        this.documentAttach = document;
        TLRPC.Document document2 = this.documentAttach;
        if (document2 == null) {
            return 0;
        }
        double d = 0.0d;
        if (MessageObject.isVoiceDocument(document2)) {
            this.documentAttachType = 3;
            int i5 = 0;
            while (true) {
                if (i5 >= this.documentAttach.attributes.size()) {
                    break;
                }
                TLRPC.DocumentAttribute documentAttribute = this.documentAttach.attributes.get(i5);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                    d = documentAttribute.duration;
                    break;
                }
                i5++;
            }
            this.widthBeforeNewTimeLine = (i3 - AndroidUtilities.dp(94.0f)) - ((int) Math.ceil(Theme.chat_audioTimePaint.measureText("00:00")));
            this.availableTimeWidth = i3 - AndroidUtilities.dp(18.0f);
            measureTime(messageObject);
            int dp = AndroidUtilities.dp(174.0f) + this.timeWidth;
            if (!this.hasLinkPreview) {
                this.backgroundWidth = Math.min(i3, dp + ((int) Math.ceil(Theme.chat_audioTimePaint.measureText(AndroidUtilities.formatLongDuration((int) d)))));
            }
            this.seekBarWaveform.setMessageObject(messageObject);
            return 0;
        }
        if (MessageObject.isVideoDocument(this.documentAttach)) {
            this.documentAttachType = 4;
            if (!messageObject.needDrawBluredPreview()) {
                updatePlayingMessageProgress();
                String format = String.format("%s", AndroidUtilities.formatFileSize(this.documentAttach.size));
                this.docTitleWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(format));
                this.docTitleLayout = new StaticLayout(format, Theme.chat_infoPaint, this.docTitleWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            return 0;
        }
        if (MessageObject.isMusicDocument(this.documentAttach)) {
            this.documentAttachType = 5;
            int dp2 = i3 - AndroidUtilities.dp(92.0f);
            if (dp2 < 0) {
                dp2 = AndroidUtilities.dp(100.0f);
            }
            String replace = messageObject.getMusicTitle().replace('\n', ' ');
            TextPaint textPaint = Theme.chat_audioTitlePaint;
            float dp3 = dp2 - AndroidUtilities.dp(12.0f);
            TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
            CharSequence ellipsize = TextUtils.ellipsize(replace, textPaint, dp3, truncateAt);
            TextPaint textPaint2 = Theme.chat_audioTitlePaint;
            Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
            StaticLayout staticLayout = new StaticLayout(ellipsize, textPaint2, dp2, alignment, 1.0f, 0.0f, false);
            this.songLayout = staticLayout;
            if (staticLayout.getLineCount() > 0) {
                this.songX = -((int) Math.ceil(this.songLayout.getLineLeft(0)));
            }
            StaticLayout staticLayout2 = new StaticLayout(TextUtils.ellipsize(messageObject.getMusicAuthor().replace('\n', ' '), Theme.chat_audioPerformerPaint, dp2, truncateAt), Theme.chat_audioPerformerPaint, dp2, alignment, 1.0f, 0.0f, false);
            this.performerLayout = staticLayout2;
            if (staticLayout2.getLineCount() > 0) {
                this.performerX = -((int) Math.ceil(this.performerLayout.getLineLeft(0)));
            }
            while (true) {
                if (i4 >= this.documentAttach.attributes.size()) {
                    break;
                }
                TLRPC.DocumentAttribute documentAttribute2 = this.documentAttach.attributes.get(i4);
                if (documentAttribute2 instanceof TLRPC.TL_documentAttributeAudio) {
                    d = documentAttribute2.duration;
                    break;
                }
                i4++;
            }
            int i6 = (int) d;
            int ceil = (int) Math.ceil(Theme.chat_audioTimePaint.measureText(AndroidUtilities.formatShortDuration(i6, i6)));
            this.widthBeforeNewTimeLine = (this.backgroundWidth - AndroidUtilities.dp(86.0f)) - ceil;
            this.availableTimeWidth = this.backgroundWidth - AndroidUtilities.dp(28.0f);
            return ceil;
        }
        if (MessageObject.isGifDocument(this.documentAttach, messageObject.hasValidGroupId())) {
            this.documentAttachType = 2;
            if (!messageObject.needDrawBluredPreview()) {
                String string = LocaleController.getString("AttachGif", R.string.AttachGif);
                this.infoWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(string));
                TextPaint textPaint3 = Theme.chat_infoPaint;
                int i7 = this.infoWidth;
                Layout.Alignment alignment2 = Layout.Alignment.ALIGN_NORMAL;
                this.infoLayout = new StaticLayout(string, textPaint3, i7, alignment2, 1.0f, 0.0f, false);
                String format2 = String.format("%s", AndroidUtilities.formatFileSize(this.documentAttach.size));
                this.docTitleWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(format2));
                this.docTitleLayout = new StaticLayout(format2, Theme.chat_infoPaint, this.docTitleWidth, alignment2, 1.0f, 0.0f, false);
            }
            return 0;
        }
        String str = this.documentAttach.mime_type;
        boolean z = (str != null && (str.toLowerCase().startsWith("image/") || this.documentAttach.mime_type.toLowerCase().startsWith("video/mp4"))) || MessageObject.isDocumentHasThumb(this.documentAttach);
        this.drawPhotoImage = z;
        if (!z) {
            i3 += AndroidUtilities.dp(30.0f);
        }
        this.documentAttachType = 1;
        String documentFileName = FileLoader.getDocumentFileName(this.documentAttach);
        if (documentFileName.length() == 0) {
            documentFileName = LocaleController.getString("AttachDocument", R.string.AttachDocument);
        }
        StaticLayout createStaticLayout = StaticLayoutEx.createStaticLayout(documentFileName, Theme.chat_docNamePaint, i3, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.MIDDLE, i3, 2, false);
        this.docTitleLayout = createStaticLayout;
        this.docTitleOffsetX = Integer.MIN_VALUE;
        if (createStaticLayout == null || createStaticLayout.getLineCount() <= 0) {
            this.docTitleOffsetX = 0;
            i2 = i3;
        } else {
            int i8 = 0;
            while (i4 < this.docTitleLayout.getLineCount()) {
                i8 = Math.max(i8, (int) Math.ceil(this.docTitleLayout.getLineWidth(i4)));
                this.docTitleOffsetX = Math.max(this.docTitleOffsetX, (int) Math.ceil(-this.docTitleLayout.getLineLeft(i4)));
                i4++;
            }
            i2 = Math.min(i3, i8);
        }
        String str2 = AndroidUtilities.formatFileSize(this.documentAttach.size) + " " + FileLoader.getDocumentExtension(this.documentAttach);
        int dp4 = i3 - AndroidUtilities.dp(30.0f);
        TextPaint textPaint4 = Theme.chat_infoPaint;
        int min = Math.min(dp4, (int) Math.ceil(textPaint4.measureText("000.0 mm / " + AndroidUtilities.formatFileSize(this.documentAttach.size))));
        this.infoWidth = min;
        CharSequence ellipsize2 = TextUtils.ellipsize(str2, Theme.chat_infoPaint, (float) min, TextUtils.TruncateAt.END);
        try {
            if (this.infoWidth < 0) {
                this.infoWidth = AndroidUtilities.dp(10.0f);
            }
            this.infoLayout = new StaticLayout(ellipsize2, Theme.chat_infoPaint, this.infoWidth + AndroidUtilities.dp(6.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (this.drawPhotoImage) {
            this.currentPhotoObject = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 320);
            this.currentPhotoObjectThumb = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 40);
            if (this.currentMessageObject.isHiddenSensitive() || (DownloadController.getInstance(this.currentAccount).getAutodownloadMask() & 1) == 0) {
                this.currentPhotoObject = null;
            }
            TLRPC.PhotoSize photoSize = this.currentPhotoObject;
            if (photoSize == null || photoSize == this.currentPhotoObjectThumb) {
                this.currentPhotoObject = null;
                this.photoImage.setNeedsQualityThumb(true);
                this.photoImage.setShouldGenerateQualityThumb(true);
            } else {
                BitmapDrawable bitmapDrawable = this.currentMessageObject.strippedThumb;
                if (bitmapDrawable != null) {
                    this.currentPhotoObjectThumb = null;
                    this.currentPhotoObjectThumbStripped = bitmapDrawable;
                }
            }
            this.currentPhotoFilter = "86_86_b";
            this.photoImage.setImage(ImageLocation.getForObject(this.currentPhotoObject, messageObject.photoThumbsObject), "86_86", ImageLocation.getForObject(this.currentPhotoObjectThumb, messageObject.photoThumbsObject), this.currentPhotoFilter, this.currentPhotoObjectThumbStripped, 0L, null, messageObject, 1);
        }
        return i2;
    }

    private InstantViewButton createInstantViewButton(int i, String str, int i2, float f) {
        InstantViewButton instantViewButton = new InstantViewButton();
        instantViewButton.type = i;
        instantViewButton.layout = new StaticLayout(TextUtils.ellipsize(str, Theme.chat_instantViewPaint, i2, TextUtils.TruncateAt.END), Theme.chat_instantViewPaint, i2 + AndroidUtilities.dp(2.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        instantViewButton.buttonWidth = f;
        if (instantViewButton.layout.getLineCount() > 0) {
            double d = instantViewButton.buttonWidth;
            double ceil = Math.ceil(instantViewButton.layout.getLineWidth(0));
            Double.isNaN(d);
            instantViewButton.textX = ((float) (d - ceil)) / 2.0f;
            InstantViewButton.access$5024(instantViewButton, (int) instantViewButton.layout.getLineLeft(0));
        }
        return instantViewButton;
    }

    private void createInstantViewButton() {
        int measureText;
        int i;
        int i2;
        if (Build.VERSION.SDK_INT >= 21 && this.drawInstantView) {
            createSelectorDrawable(0);
        }
        if (this.drawInstantView && this.instantViewLayout == null) {
            this.instantWidth = AndroidUtilities.dp(33.0f);
            String str = this.instantViewButtonText;
            if (str == null) {
                int i3 = this.drawInstantViewType;
                if (i3 == 12) {
                    i = R.string.OpenChannelPost;
                } else if (i3 == 1) {
                    i = R.string.OpenChannel;
                } else {
                    if (i3 == 13) {
                        i2 = R.string.SendMessage;
                    } else if (i3 == 32) {
                        i2 = R.string.OpenProfile;
                    } else if (i3 == 10) {
                        i = R.string.OpenBot;
                    } else if (i3 == 2) {
                        i = R.string.OpenGroup;
                    } else if (i3 == 3) {
                        i = R.string.OpenMessage;
                    } else if (i3 == 5) {
                        i = R.string.ViewContact;
                    } else if (i3 == 6) {
                        i = R.string.OpenBackground;
                    } else if (i3 == 7) {
                        i = R.string.OpenTheme;
                    } else if (i3 == 8) {
                        i = (this.pollVoted || this.pollClosed) ? R.string.PollViewResults : R.string.PollSubmitVotes;
                    } else if (i3 == 9 || i3 == 11) {
                        TLRPC.TL_webPage tL_webPage = (TLRPC.TL_webPage) MessageObject.getMedia(this.currentMessageObject.messageOwner).webpage;
                        if (tL_webPage != null && tL_webPage.url.contains("voicechat=")) {
                            i = R.string.VoipGroupJoinAsSpeaker;
                        }
                        i = R.string.VoipGroupJoinAsLinstener;
                    } else {
                        if (i3 != 25) {
                            if (i3 == 14) {
                                i2 = R.string.ViewChatList;
                            } else if (i3 == 15) {
                                i2 = R.string.BotWebAppInstantViewOpen;
                            } else if (i3 == 16) {
                                i2 = R.string.OpenLink;
                            } else if (i3 == 17) {
                                i2 = R.string.ViewStory;
                            } else {
                                i = (i3 == 18 || i3 == 22) ? R.string.BoostLinkButton : i3 == 19 ? R.string.BoostingHowItWork : i3 == 20 ? R.string.OpenGift : i3 == 21 ? R.string.AppUpdate : i3 == 23 ? R.string.OpenStickerSet : i3 == 24 ? R.string.OpenEmojiSet : R.string.InstantView;
                            }
                        }
                        i = R.string.VoipGroupJoinAsLinstener;
                    }
                    str = LocaleController.getString(i2).toUpperCase();
                }
                str = LocaleController.getString(i);
            }
            if (this.currentMessageObject.isSponsored() && this.backgroundWidth < (measureText = (int) (Theme.chat_instantViewPaint.measureText(str) + AndroidUtilities.dp(75.0f)))) {
                this.backgroundWidth = measureText;
            }
            int dp = this.backgroundWidth - AndroidUtilities.dp(75.0f);
            StaticLayout staticLayout = new StaticLayout(TextUtils.ellipsize(str, Theme.chat_instantViewPaint, dp, TextUtils.TruncateAt.END), Theme.chat_instantViewPaint, dp + AndroidUtilities.dp(2.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.instantViewLayout = staticLayout;
            this.instantViewLayoutWidth = staticLayout.getLineCount() > 0 ? this.instantViewLayout.getLineWidth(0) : 0.0f;
            this.instantViewLayoutLeft = this.instantViewLayout.getLineCount() > 0 ? this.instantViewLayout.getLineLeft(0) : 0.0f;
            this.instantWidth = this.backgroundWidth - AndroidUtilities.dp(this.drawInstantViewType == 8 ? 13.0f : 34.0f);
            int dp2 = this.totalHeight + AndroidUtilities.dp(46.0f);
            this.totalHeight = dp2;
            if (this.currentMessageObject.type == 12) {
                this.totalHeight = dp2 + AndroidUtilities.dp(14.0f);
            }
            if (this.currentMessageObject.isSponsored()) {
                this.totalHeight += AndroidUtilities.dp(2.0f);
            }
            StaticLayout staticLayout2 = this.instantViewLayout;
            if (staticLayout2 == null || staticLayout2.getLineCount() <= 0) {
                return;
            }
            double d = this.instantWidth;
            double ceil = Math.ceil(this.instantViewLayout.getLineWidth(0));
            Double.isNaN(d);
            this.instantTextX = (((int) (d - ceil)) / 2) + (this.drawInstantViewType == 0 ? AndroidUtilities.dp(8.0f) : 0);
            int lineLeft = (int) this.instantViewLayout.getLineLeft(0);
            this.instantTextLeftX = lineLeft;
            this.instantTextX += -lineLeft;
        }
    }

    private void createLoadingProgressLayout(long r22, long r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.createLoadingProgressLayout(long, long):void");
    }

    private void createLoadingProgressLayout(TLRPC.Document document) {
        if (document == null) {
            return;
        }
        long[] fileProgressSizes = ImageLoader.getInstance().getFileProgressSizes(FileLoader.getDocumentFileName(document));
        if (fileProgressSizes != null) {
            createLoadingProgressLayout(fileProgressSizes[0], fileProgressSizes[1]);
        } else {
            createLoadingProgressLayout(this.currentMessageObject.loadedFileSize, document.size);
        }
    }

    private void createPollUI() {
        if (this.pollAvatarImages != null) {
            return;
        }
        this.pollAvatarImages = new ImageReceiver[3];
        this.pollAvatarDrawables = new AvatarDrawable[3];
        this.pollAvatarImagesVisible = new boolean[3];
        int i = 0;
        while (true) {
            ImageReceiver[] imageReceiverArr = this.pollAvatarImages;
            if (i >= imageReceiverArr.length) {
                break;
            }
            imageReceiverArr[i] = new ImageReceiver(this);
            this.pollAvatarImages[i].setRoundRadius(AndroidUtilities.dp(8.0f));
            this.pollAvatarDrawables[i] = new AvatarDrawable();
            this.pollAvatarDrawables[i].setTextSize(AndroidUtilities.dp(22.0f));
            i++;
        }
        this.pollCheckBox = new CheckBoxBase[10];
        int i2 = 0;
        while (true) {
            CheckBoxBase[] checkBoxBaseArr = this.pollCheckBox;
            if (i2 >= checkBoxBaseArr.length) {
                return;
            }
            checkBoxBaseArr[i2] = new CheckBoxBase(this, 20, this.resourcesProvider);
            this.pollCheckBox[i2].setDrawUnchecked(false);
            this.pollCheckBox[i2].setBackgroundType(9);
            i2++;
        }
    }

    public void createStatusDrawableAnimator(int i, int i2, final boolean z) {
        boolean z2 = false;
        boolean z3 = (i2 & 1) != 0;
        boolean z4 = (i2 & 2) != 0;
        boolean z5 = (i & 1) != 0;
        boolean z6 = (i & 2) != 0;
        if ((i & 4) == 0 && z6 && z4 && !z5 && z3) {
            z2 = true;
        }
        if (!this.transitionParams.messageEntering || z2) {
            this.statusDrawableProgress = 0.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.statusDrawableAnimator = ofFloat;
            ofFloat.setDuration(z2 ? 220L : 150L);
            this.statusDrawableAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animateFromStatusDrawableParams = i;
            this.animateToStatusDrawableParams = i2;
            this.statusDrawableAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatMessageCell.this.lambda$createStatusDrawableAnimator$13(z, valueAnimator);
                }
            });
            this.statusDrawableAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    int createStatusDrawableParams = ChatMessageCell.this.transitionParams.createStatusDrawableParams();
                    if (ChatMessageCell.this.animateToStatusDrawableParams != createStatusDrawableParams) {
                        ChatMessageCell chatMessageCell = ChatMessageCell.this;
                        chatMessageCell.createStatusDrawableAnimator(chatMessageCell.animateToStatusDrawableParams, createStatusDrawableParams, z);
                    } else {
                        ChatMessageCell.this.statusDrawableAnimationInProgress = false;
                        ChatMessageCell chatMessageCell2 = ChatMessageCell.this;
                        chatMessageCell2.transitionParams.lastStatusDrawableParams = chatMessageCell2.animateToStatusDrawableParams;
                    }
                }
            });
            this.statusDrawableAnimationInProgress = true;
            this.statusDrawableAnimator.start();
        }
    }

    private void didClickedImage() {
        ChatMessageCellDelegate chatMessageCellDelegate;
        ChatMessageCellDelegate chatMessageCellDelegate2;
        TLRPC.WebPage webPage;
        boolean z;
        TLRPC.MessageMedia messageMedia;
        TLRPC.ReplyMarkup replyMarkup;
        if (this.currentMessageObject.hasMediaSpoilers() && !this.currentMessageObject.needDrawBluredPreview()) {
            MessageObject messageObject = this.currentMessageObject;
            if (!messageObject.isMediaSpoilersRevealed) {
                if (this.delegate == null || !messageObject.isSensitive()) {
                    startRevealMedia(this.lastTouchX, this.lastTouchY);
                    return;
                } else {
                    this.delegate.didPressRevealSensitiveContent(this);
                    return;
                }
            }
        }
        MessageObject messageObject2 = this.currentMessageObject;
        int i = messageObject2.type;
        if (i == 20) {
            TLRPC.Message message = messageObject2.messageOwner;
            if (message == null || (messageMedia = message.media) == null || messageMedia.extended_media.isEmpty() || (replyMarkup = this.currentMessageObject.messageOwner.reply_markup) == null) {
                return;
            }
            Iterator<TLRPC.TL_keyboardButtonRow> it = replyMarkup.rows.iterator();
            while (it.hasNext()) {
                Iterator<TLRPC.KeyboardButton> it2 = it.next().buttons.iterator();
                if (it2.hasNext()) {
                    this.delegate.didPressExtendedMediaPreview(this, it2.next());
                    return;
                }
            }
            return;
        }
        if (i != 1 && !messageObject2.isAnyKindOfSticker()) {
            MessageObject messageObject3 = this.currentMessageObject;
            int i2 = messageObject3.type;
            if (i2 == 12) {
                long j = MessageObject.getMedia(messageObject3.messageOwner).user_id;
                this.delegate.didPressUserAvatar(this, j != 0 ? MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j)) : null, this.lastTouchX, this.lastTouchY, false);
                return;
            }
            if (i2 == 5) {
                if (this.buttonState == -1) {
                    if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || MediaController.getInstance().isMessagePaused()) {
                        this.delegate.needPlayMessage(this, this.currentMessageObject, false);
                        return;
                    } else {
                        MediaController.getInstance().lambda$startAudioAgain$7(this.currentMessageObject);
                        return;
                    }
                }
            } else if (i2 == 8) {
                int i3 = this.buttonState;
                if (i3 != -1 && (i3 != 1 || !this.canStreamVideo || !this.autoPlayingMedia)) {
                    if (i3 != 2 && i3 != 0) {
                        return;
                    }
                }
                chatMessageCellDelegate = this.delegate;
            } else {
                int i4 = this.documentAttachType;
                if (i4 == 4) {
                    int i5 = this.buttonState;
                    if (i5 != -1 && (!(z = this.drawVideoImageButton) || (!this.autoPlayingMedia && (!SharedConfig.streamMedia || !this.canStreamVideo)))) {
                        if (z) {
                            didPressButton(true, true);
                            return;
                        } else if (i5 != 0 && i5 != 3) {
                            return;
                        }
                    }
                } else if (i2 != 4 && i2 != 23 && i2 != 24) {
                    if (i4 == 1) {
                        if (this.buttonState != -1) {
                            return;
                        }
                    } else if (messageObject3.sponsoredMedia != null) {
                        chatMessageCellDelegate = this.delegate;
                        if (chatMessageCellDelegate == null) {
                            return;
                        }
                    } else {
                        if (i4 == 2) {
                            if (this.buttonState != -1 || (webPage = MessageObject.getMedia(messageObject3.messageOwner).webpage) == null) {
                                return;
                            }
                            String str = webPage.embed_url;
                            if (str == null || str.length() == 0) {
                                Browser.openUrl(getContext(), webPage.url);
                                return;
                            } else {
                                this.delegate.needOpenWebView(this.currentMessageObject, webPage.embed_url, webPage.site_name, webPage.description, webPage.url, webPage.embed_width, webPage.embed_height);
                                return;
                            }
                        }
                        if (!this.hasInvoicePreview) {
                            if (Build.VERSION.SDK_INT < 26 || (chatMessageCellDelegate2 = this.delegate) == null) {
                                return;
                            }
                            if (i2 == 16) {
                                chatMessageCellDelegate2.didLongPress(this, 0.0f, 0.0f);
                                return;
                            } else {
                                chatMessageCellDelegate2.didPressOther(this, this.otherX, this.otherY);
                                return;
                            }
                        }
                        if (this.buttonState != -1) {
                            return;
                        }
                    }
                }
                chatMessageCellDelegate = this.delegate;
            }
            didPressButton(true, false);
            return;
        }
        int i6 = this.buttonState;
        if (i6 != -1) {
            if (i6 != 0) {
                return;
            }
            didPressButton(true, false);
            return;
        }
        chatMessageCellDelegate = this.delegate;
        chatMessageCellDelegate.didPressImage(this, this.lastTouchX, this.lastTouchY);
    }

    private void didPressButton(boolean r31, boolean r32) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.didPressButton(boolean, boolean):void");
    }

    private void didPressMiniButton(boolean r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.didPressMiniButton(boolean):void");
    }

    private void drawAnimatedEmojiMessageText(float r26, float r27, android.graphics.Canvas r28, java.util.ArrayList r29, org.telegram.ui.Components.AnimatedEmojiSpan.EmojiGroupedSpans r30, boolean r31, float r32, float r33, boolean r34) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawAnimatedEmojiMessageText(float, float, android.graphics.Canvas, java.util.ArrayList, org.telegram.ui.Components.AnimatedEmojiSpan$EmojiGroupedSpans, boolean, float, float, boolean):void");
    }

    private void drawAnimatedEmojiMessageText(Canvas canvas, float f) {
        float f2;
        int dp;
        int dp2;
        int i;
        int dp3;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.isSponsored()) {
            return;
        }
        float f3 = this.textY;
        TransitionParams transitionParams = this.transitionParams;
        if (transitionParams.animateText) {
            float f4 = transitionParams.animateFromTextY;
            float f5 = transitionParams.animateChangeProgress;
            f2 = (f4 * (1.0f - f5)) + (f3 * f5);
        } else {
            f2 = f3;
        }
        if (transitionParams.animateChangeProgress == 1.0f || !transitionParams.animateMessageText) {
            float f6 = this.textX;
            MessageObject messageObject2 = this.currentMessageObject;
            drawAnimatedEmojiMessageText(f6, f2, canvas, messageObject2.textLayoutBlocks, this.animatedEmojiStack, true, f, messageObject2.textXOffset, false);
            return;
        }
        canvas.save();
        Theme.MessageDrawable messageDrawable = this.currentBackgroundDrawable;
        if (messageDrawable != null) {
            Rect bounds = messageDrawable.getBounds();
            if (!this.currentMessageObject.isOutOwner() || this.mediaBackground || this.pinnedBottom) {
                dp = bounds.left + AndroidUtilities.dp(4.0f);
                dp2 = bounds.top + AndroidUtilities.dp(4.0f);
                i = bounds.right;
                dp3 = AndroidUtilities.dp(4.0f);
            } else {
                dp = bounds.left + AndroidUtilities.dp(4.0f);
                dp2 = bounds.top + AndroidUtilities.dp(4.0f);
                i = bounds.right;
                dp3 = AndroidUtilities.dp(10.0f);
            }
            canvas.clipRect(dp, dp2, i - dp3, bounds.bottom - AndroidUtilities.dp(4.0f));
        }
        drawAnimatedEmojiMessageText(this.textX, f2, canvas, this.transitionParams.animateOutTextBlocks, this.transitionParams.animateOutAnimateEmoji, false, f * (1.0f - this.transitionParams.animateChangeProgress), this.currentMessageObject.textXOffset, false);
        float f7 = this.textX;
        MessageObject messageObject3 = this.currentMessageObject;
        drawAnimatedEmojiMessageText(f7, f2, canvas, messageObject3.textLayoutBlocks, this.animatedEmojiStack, true, f * this.transitionParams.animateChangeProgress, messageObject3.textXOffset, false);
        canvas.restore();
    }

    private void drawBotButtons(Canvas canvas, ArrayList arrayList, int i) {
        Drawable drawable;
        String str;
        ChatMessageCellDelegate chatMessageCellDelegate;
        if (SizeNotifierFrameLayout.drawingBlur) {
            return;
        }
        int widthForButtons = getWidthForButtons();
        MessageObject messageObject = this.currentMessageObject;
        float f = 1.0f;
        int dp = (messageObject == null || !messageObject.isOutOwner()) ? this.backgroundDrawableLeft + AndroidUtilities.dp((this.mediaBackground || this.drawPinnedBottom) ? 1.0f : 7.0f) : (getMeasuredWidth() - widthForButtons) - AndroidUtilities.dp(10.0f);
        float f2 = 2.0f;
        float dp2 = (this.layoutHeight - AndroidUtilities.dp(2.0f)) + this.transitionParams.deltaBottom;
        float f3 = 0.0f;
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            BotButton botButton = (BotButton) arrayList.get(i2);
            float f4 = botButton.y + botButton.height;
            if (f4 > f3) {
                f3 = f4;
            }
        }
        this.rect.set(0.0f, dp2, getMeasuredWidth(), f3 + dp2);
        if (i != 255) {
            canvas.saveLayerAlpha(this.rect, i, 31);
        } else {
            canvas.save();
        }
        int i3 = 0;
        while (i3 < arrayList.size()) {
            BotButton botButton2 = (BotButton) arrayList.get(i3);
            float dp3 = ((botButton2.y + this.layoutHeight) - AndroidUtilities.dp(f2)) + this.transitionParams.deltaBottom;
            float pressScale = botButton2.getPressScale();
            float f5 = widthForButtons;
            float f6 = dp;
            this.rect.set((botButton2.x * f5) + f6, dp3, (botButton2.x * f5) + f6 + (botButton2.width * f5), botButton2.height + dp3);
            canvas.save();
            if (pressScale != f) {
                canvas.scale(pressScale, pressScale, this.rect.centerX(), this.rect.centerY());
            }
            applyServiceShaderMatrix();
            Arrays.fill(this.botButtonRadii, AndroidUtilities.dp(Math.min(6.75f, SharedConfig.bubbleRadius)));
            if (botButton2.hasPositionFlag(9)) {
                float[] fArr = this.botButtonRadii;
                float dp4 = AndroidUtilities.dp(SharedConfig.bubbleRadius);
                fArr[7] = dp4;
                fArr[6] = dp4;
            }
            if (botButton2.hasPositionFlag(10)) {
                float[] fArr2 = this.botButtonRadii;
                float dp5 = AndroidUtilities.dp(SharedConfig.bubbleRadius);
                fArr2[5] = dp5;
                fArr2[4] = dp5;
            }
            this.botButtonPath.rewind();
            this.botButtonPath.addRoundRect(this.rect, this.botButtonRadii, Path.Direction.CW);
            canvas.drawPath(this.botButtonPath, getThemedPaint("paintChatActionBackground"));
            if (hasGradientService()) {
                canvas.drawPath(this.botButtonPath, Theme.chat_actionBackgroundGradientDarkenPaint);
            }
            boolean z = (((botButton2.button instanceof TLRPC.TL_keyboardButtonCallback) || (botButton2.button instanceof TLRPC.TL_keyboardButtonGame) || (botButton2.button instanceof TLRPC.TL_keyboardButtonBuy) || (botButton2.button instanceof TLRPC.TL_keyboardButtonUrlAuth)) && SendMessagesHelper.getInstance(this.currentAccount).isSendingCallback(this.currentMessageObject, botButton2.button)) || ((botButton2.button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation) && SendMessagesHelper.getInstance(this.currentAccount).isSendingCurrentLocation(this.currentMessageObject, botButton2.button)) || ((botButton2.button instanceof TLRPC.TL_keyboardButtonUrl) && (chatMessageCellDelegate = this.delegate) != null && chatMessageCellDelegate.isProgressLoading(this, 3) && this.delegate.getProgressLoadingBotButtonUrl(this) == botButton2.button.url);
            canvas.save();
            canvas.clipPath(this.botButtonPath);
            LoadingDrawable loadingDrawable = botButton2.loadingDrawable;
            if (z) {
                if (loadingDrawable == null) {
                    botButton2.loadingDrawable = new LoadingDrawable();
                    botButton2.loadingDrawable.setRadiiDp(5.5f);
                    botButton2.loadingDrawable.setAppearByGradient(true);
                    botButton2.loadingDrawable.strokePaint.setStrokeWidth(AndroidUtilities.dpf2(1.25f));
                } else if (botButton2.loadingDrawable.isDisappeared() || botButton2.loadingDrawable.isDisappearing()) {
                    botButton2.loadingDrawable.reset();
                    botButton2.loadingDrawable.resetDisappear();
                }
            } else if (loadingDrawable != null && !botButton2.loadingDrawable.isDisappearing() && !botButton2.loadingDrawable.isDisappeared()) {
                botButton2.loadingDrawable.disappear();
            }
            if (botButton2.loadingDrawable != null && (z || botButton2.loadingDrawable.isDisappearing())) {
                this.rect.inset(AndroidUtilities.dpf2(0.625f), AndroidUtilities.dpf2(0.625f));
                botButton2.loadingDrawable.setRadii(this.botButtonRadii);
                botButton2.loadingDrawable.setBounds(this.rect);
                LoadingDrawable loadingDrawable2 = botButton2.loadingDrawable;
                int i4 = Theme.key_chat_serviceBackgroundSelector;
                loadingDrawable2.setColors(Theme.multAlpha(Theme.getColor(i4, this.resourcesProvider), f), Theme.multAlpha(Theme.getColor(i4, this.resourcesProvider), 2.5f), Theme.multAlpha(Theme.getColor(i4, this.resourcesProvider), 3.0f), Theme.multAlpha(Theme.getColor(i4, this.resourcesProvider), 10.0f));
                botButton2.loadingDrawable.setAlpha(255);
                botButton2.loadingDrawable.draw(canvas);
                invalidateOutbounds();
            }
            if (botButton2.selectorDrawable != null) {
                int i5 = (int) dp3;
                botButton2.selectorDrawable.setBounds(((int) (botButton2.x * f5)) + dp, i5, ((int) (botButton2.x * f5)) + dp + ((int) (botButton2.width * f5)), botButton2.height + i5);
                botButton2.selectorDrawable.setAlpha(255);
                botButton2.selectorDrawable.draw(canvas);
            }
            canvas.restore();
            canvas.save();
            botButton2.title.ellipsize(Math.max(1, ((int) (botButton2.width * f5)) - AndroidUtilities.dp(10.0f))).draw(canvas, (botButton2.x * f5) + f6 + (((botButton2.width * f5) - botButton2.title.getWidth()) / 2.0f), (AndroidUtilities.dp(44.0f) / 2.0f) + dp3);
            canvas.restore();
            if (!(botButton2.button instanceof TLRPC.TL_keyboardButtonWebView)) {
                if (botButton2.button instanceof TLRPC.TL_keyboardButtonUrl) {
                    str = AndroidUtilities.isWebAppLink(botButton2.button.url) ? "drawableBotInline" : botButton2.isInviteButton ? "drawable_botInvite" : "drawableBotLink";
                } else if (!(botButton2.button instanceof TLRPC.TL_keyboardButtonSwitchInline) && !(botButton2.button instanceof TLRPC.TL_keyboardButtonRequestPeer)) {
                    if ((botButton2.button instanceof TLRPC.TL_keyboardButtonBuy) && this.hasInvoicePreview && this.hasInvoicePrice) {
                        BaseCell.setDrawableBounds(Theme.chat_botCardDrawable, (((((int) (botButton2.x * f5)) + ((int) (botButton2.width * f5))) - AndroidUtilities.dp(5.0f)) - Theme.chat_botCardDrawable.getIntrinsicWidth()) + dp, dp3 + AndroidUtilities.dp(4.0f));
                        drawable = Theme.chat_botCardDrawable;
                        drawable.draw(canvas);
                    }
                    canvas.restore();
                    i3++;
                    f = 1.0f;
                    f2 = 2.0f;
                }
                drawable = getThemedDrawable(str);
                BaseCell.setDrawableBounds(drawable, (((((int) (botButton2.x * f5)) + ((int) (botButton2.width * f5))) - AndroidUtilities.dp(3.0f)) - drawable.getIntrinsicWidth()) + dp, dp3 + AndroidUtilities.dp(3.0f));
                drawable.draw(canvas);
                canvas.restore();
                i3++;
                f = 1.0f;
                f2 = 2.0f;
            }
            drawable = getThemedDrawable("drawableBotWebView");
            BaseCell.setDrawableBounds(drawable, (((((int) (botButton2.x * f5)) + ((int) (botButton2.width * f5))) - AndroidUtilities.dp(3.0f)) - drawable.getIntrinsicWidth()) + dp, dp3 + AndroidUtilities.dp(3.0f));
            drawable.draw(canvas);
            canvas.restore();
            i3++;
            f = 1.0f;
            f2 = 2.0f;
        }
        canvas.restore();
    }

    private void drawCaptionLayout(android.graphics.Canvas r21, org.telegram.messenger.MessageObject.TextLayoutBlocks r22, boolean r23, boolean r24, float r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawCaptionLayout(android.graphics.Canvas, org.telegram.messenger.MessageObject$TextLayoutBlocks, boolean, boolean, float):void");
    }

    private void drawClockOrErrorLayout(android.graphics.Canvas r7, boolean r8, boolean r9, float r10, float r11, float r12, float r13, float r14, boolean r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawClockOrErrorLayout(android.graphics.Canvas, boolean, boolean, float, float, float, float, float, boolean):void");
    }

    private void drawContact(Canvas canvas) {
        TextPaint textPaint;
        int i;
        boolean z;
        float f;
        Drawable drawable;
        if (this.contactLine == null) {
            this.contactLine = new ReplyMessageLine(this);
        }
        int check = this.contactLine.check(this.currentMessageObject, this.currentUser, this.currentChat, this.resourcesProvider, 4);
        if (this.contactBounce == null) {
            this.contactBounce = new ButtonBounce(this, 2.0f, 2.0f);
        }
        int imageX = (int) (this.photoImage.getImageX() - AndroidUtilities.dp(13.0f));
        int dp = this.layoutHeight - AndroidUtilities.dp(62.0f);
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        if (!reactionsLayoutInBubble.isEmpty && !reactionsLayoutInBubble.isSmall) {
            dp -= reactionsLayoutInBubble.totalHeight;
        }
        if (this.drawCommentButton) {
            dp -= AndroidUtilities.dp(shouldDrawTimeOnMedia() ? 39.3f : 41.0f);
        }
        int i2 = dp;
        if (this.contactRect == null) {
            this.contactRect = new RectF();
        }
        int backgroundDrawableRight = (getBackgroundDrawableRight() - (AndroidUtilities.dp(10.0f) + ((!this.currentMessageObject.isOutOwner() || this.mediaBackground || this.drawPinnedBottom) ? 0 : AndroidUtilities.dp(6.0f)))) - getExtraTextX();
        this.contactRect.set(imageX, this.photoImage.getImageY() - AndroidUtilities.dp(9.0f), backgroundDrawableRight, AndroidUtilities.dp(38.0f) + i2);
        float scale = this.contactBounce.getScale(0.0125f);
        boolean z2 = scale != 1.0f;
        if (z2) {
            canvas.save();
            canvas.scale(scale, scale, this.contactRect.centerX(), this.contactRect.centerY());
        }
        if (Build.VERSION.SDK_INT >= 21 && (drawable = this.selectorDrawable[0]) != null) {
            this.selectorDrawableMaskType[0] = 0;
            drawable.setBounds(imageX, (int) (this.photoImage.getImageY() - AndroidUtilities.dp(9.0f)), backgroundDrawableRight, AndroidUtilities.dp(38.0f) + i2);
            if (this.selectorDrawableColor != Theme.multAlpha(this.contactLine.getColor(), 0.1f)) {
                Drawable drawable2 = this.selectorDrawable[0];
                int multAlpha = Theme.multAlpha(this.contactLine.getColor(), 0.1f);
                this.selectorDrawableColor = multAlpha;
                Theme.setSelectorDrawableColor(drawable2, multAlpha, true);
            }
            this.selectorDrawable[0].draw(canvas);
        }
        float floor = (float) Math.floor(SharedConfig.bubbleRadius / 3.0f);
        int i3 = (int) floor;
        this.contactLine.drawBackground(canvas, this.contactRect, floor, floor, floor, 1.0f);
        this.contactLine.drawLine(canvas, this.contactRect, 1.0f);
        Theme.chat_contactNamePaint.setColor(check);
        Theme.chat_contactPhonePaint.setColor(getThemedColor(Theme.key_chat_inContactPhoneSelectedText));
        if (this.currentMessageObject.isOutOwner()) {
            textPaint = Theme.chat_contactPhonePaint;
            i = Theme.key_chat_messageTextOut;
        } else {
            textPaint = Theme.chat_contactPhonePaint;
            i = Theme.key_chat_messageTextIn;
        }
        textPaint.setColor(getThemedColor(i));
        if (this.titleLayout != null) {
            canvas.save();
            canvas.translate(this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(9.0f), AndroidUtilities.dp(27.0f) + this.namesOffset);
            SpoilerEffect.layoutDrawMaybe(this.titleLayout, canvas);
            canvas.restore();
        }
        if (this.docTitleLayout != null) {
            canvas.save();
            canvas.translate(this.photoImage.getImageX() + this.photoImage.getImageWidth() + AndroidUtilities.dp(9.0f), AndroidUtilities.dp(50.0f) + this.namesOffset);
            SpoilerEffect.layoutDrawMaybe(this.docTitleLayout, canvas);
            canvas.restore();
        }
        ArrayList arrayList = this.contactButtons;
        if (arrayList != null && arrayList.size() > 0) {
            Theme.chat_instantViewPaint.setColor(check);
            Theme.chat_instantViewButtonPaint.setColor(Theme.multAlpha(check, 0.1f));
            int alpha = Theme.chat_instantViewPaint.getAlpha();
            Theme.chat_instantViewPaint.setAlpha((int) (alpha * 0.18f));
            canvas.drawRect(AndroidUtilities.dp(10.0f) + this.contactRect.left, AndroidUtilities.dp(2.0f) + i2, this.contactRect.right - AndroidUtilities.dp(7.0f), AndroidUtilities.dp(2.0f) + i2 + Math.max(1, AndroidUtilities.dp(0.66f)), Theme.chat_instantViewPaint);
            Theme.chat_instantViewPaint.setAlpha(alpha);
            int dp2 = i2 + AndroidUtilities.dp(2.0f);
            float dp3 = imageX + AndroidUtilities.dp(3.0f);
            ArrayList arrayList2 = this.contactButtons;
            boolean z3 = arrayList2 != null && arrayList2.size() > 1;
            int backgroundColor = this.contactLine.getBackgroundColor();
            int i4 = 0;
            while (i4 < this.contactButtons.size()) {
                InstantViewButton instantViewButton = (InstantViewButton) this.contactButtons.get(i4);
                float min = Math.min(instantViewButton.buttonWidth + dp3, this.contactRect.right);
                instantViewButton.rect.set(dp3, dp2, min, AndroidUtilities.dp(36.0f) + dp2);
                if (z3 && instantViewButton.selectorDrawable == null) {
                    this.linkPreviewSelectorColor = backgroundColor;
                    instantViewButton.selectorDrawable = Theme.createRadSelectorDrawable(backgroundColor, 0, 0, i4 == this.contactButtons.size() - 1 ? i3 : 0, 0);
                    instantViewButton.selectorDrawable.setCallback(this);
                }
                if (instantViewButton.selectorDrawable != null) {
                    instantViewButton.selectorDrawable.setBounds((int) dp3, dp2, (int) min, AndroidUtilities.dp(36.0f) + dp2);
                    instantViewButton.selectorDrawable.draw(canvas);
                }
                if (z2 || instantViewButton.buttonBounce == null) {
                    z = false;
                    f = 1.0f;
                } else {
                    f = instantViewButton.buttonBounce.getScale(0.02f);
                    z = f != 1.0f;
                }
                if (z) {
                    canvas.save();
                    canvas.scale(f, f, instantViewButton.rect.centerX(), instantViewButton.rect.centerY());
                }
                if (instantViewButton.layout != null) {
                    canvas.save();
                    canvas.translate(instantViewButton.textX + dp3, AndroidUtilities.dp(10.5f) + dp2);
                    instantViewButton.layout.draw(canvas);
                    canvas.restore();
                }
                if (z) {
                    canvas.restore();
                }
                dp3 += instantViewButton.buttonWidth;
                i4++;
            }
        }
        if (z2) {
            canvas.restore();
        }
    }

    private void drawSideButton(android.graphics.Canvas r17) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawSideButton(android.graphics.Canvas):void");
    }

    private void drawStatusDrawable(android.graphics.Canvas r20, boolean r21, boolean r22, boolean r23, boolean r24, float r25, boolean r26, float r27, float r28, float r29, boolean r30, boolean r31) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawStatusDrawable(android.graphics.Canvas, boolean, boolean, boolean, boolean, float, boolean, float, float, float, boolean, boolean):void");
    }

    private void drawTimeInternal(android.graphics.Canvas r43, float r44, boolean r45, float r46, android.text.StaticLayout r47, float r48, boolean r49) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawTimeInternal(android.graphics.Canvas, float, boolean, float, android.text.StaticLayout, float, boolean):void");
    }

    private void drawViewsAndRepliesLayout(android.graphics.Canvas r25, float r26, float r27, float r28, float r29, float r30, boolean r31) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawViewsAndRepliesLayout(android.graphics.Canvas, float, float, float, float, float, boolean):void");
    }

    private void fileAttach(boolean z, MessageObject messageObject) {
        ImageReceiver imageReceiver = this.photoImage;
        if (imageReceiver != null) {
            imageReceiver.setFileLoadingPriority(1);
        }
        if (z && messageObject != null && (this.isRoundVideo || messageObject.isVideo())) {
            checkVideoPlayback(true, null);
        }
        if (messageObject == null || messageObject.mediaExists) {
            return;
        }
        int canDownloadMediaType = DownloadController.getInstance(this.currentAccount).canDownloadMediaType(messageObject);
        TLRPC.Document document = messageObject.getDocument();
        if (!MessageObject.isStickerDocument(document) && !MessageObject.isAnimatedStickerDocument(document, true) && !MessageObject.isGifDocument(document) && !MessageObject.isRoundVideoDocument(document) && !messageObject.hasVideoQualities() && !this.isSmallImage) {
            TLRPC.PhotoSize closestPhotoSizeWithSize = document == null ? FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize()) : null;
            if (canDownloadMediaType == 2 || (canDownloadMediaType == 1 && messageObject.isVideo())) {
                if (canDownloadMediaType != 2 && document != null && !messageObject.shouldEncryptPhotoOrVideo() && messageObject.canStreamVideo()) {
                    FileLoader.getInstance(this.currentAccount).loadFile(document, messageObject, 1, 0);
                }
            } else if (canDownloadMediaType != 0) {
                if (document != null) {
                    FileLoader.getInstance(this.currentAccount).loadFile(document, messageObject, 1, ((MessageObject.isVideoDocument(document) || messageObject.isVoiceOnce() || messageObject.isRoundOnce()) && messageObject.shouldEncryptPhotoOrVideo()) ? 2 : 0);
                } else if (closestPhotoSizeWithSize != null) {
                    FileLoader.getInstance(this.currentAccount).loadFile(ImageLocation.getForObject(closestPhotoSizeWithSize, messageObject.photoThumbsObject), messageObject, null, 1, messageObject.shouldEncryptPhotoOrVideo() ? 2 : 0);
                }
            }
            if (z) {
                updateButtonState(false, false, false);
            }
        }
        if (messageObject.hasVideoQualities()) {
            VideoPlayer.VideoUri videoUri = messageObject.highestQuality;
            if (videoUri != null && !videoUri.isManifestCached()) {
                FileLoader.getInstance(this.currentAccount).loadFile(messageObject.highestQuality.manifestDocument, messageObject, 1, 0);
            }
            VideoPlayer.VideoUri videoUri2 = messageObject.thumbQuality;
            if (videoUri2 == null || videoUri2.isManifestCached()) {
                return;
            }
            FileLoader.getInstance(this.currentAccount).loadFile(messageObject.thumbQuality.manifestDocument, messageObject, 1, 0);
        }
    }

    private void fileDetach(MessageObject messageObject) {
        ImageReceiver imageReceiver = this.photoImage;
        if (imageReceiver != null) {
            imageReceiver.setFileLoadingPriority(0);
        }
        cancelLoading(messageObject);
    }

    private boolean findProgressLoadingLink(LoadingDrawableLocation loadingDrawableLocation, LinkPath linkPath, Layout layout, float f, int i) {
        if (layout == null || !(layout.getText() instanceof Spanned)) {
            return false;
        }
        Spanned spanned = (Spanned) layout.getText();
        CharacterStyle[] characterStyleArr = (CharacterStyle[]) spanned.getSpans(0, spanned.length(), CharacterStyle.class);
        if (characterStyleArr != null) {
            int i2 = 0;
            while (true) {
                if (i2 >= characterStyleArr.length) {
                    break;
                }
                if (characterStyleArr[i2] == this.progressLoadingLink) {
                    loadingDrawableLocation.blockNum = i;
                    break;
                }
                i2++;
            }
        }
        if (loadingDrawableLocation.blockNum != i) {
            return false;
        }
        linkPath.rewind();
        int spanStart = spanned.getSpanStart(this.progressLoadingLink);
        int spanEnd = spanned.getSpanEnd(this.progressLoadingLink);
        linkPath.setUseCornerPathImplementation(true);
        linkPath.setCurrentLayout(layout, spanStart, f);
        layout.getSelectionPath(spanStart, spanEnd, linkPath);
        linkPath.closeRects();
        this.progressLoadingLinkCurrentDrawable.updateBounds();
        return true;
    }

    private boolean findProgressLoadingLink(LoadingDrawableLocation loadingDrawableLocation, LinkPath linkPath, ArrayList arrayList) {
        if (arrayList == null) {
            return false;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            if (findProgressLoadingLink(loadingDrawableLocation, linkPath, ((MessageObject.TextLayoutBlock) arrayList.get(i)).textLayout, 0.0f, i)) {
                return true;
            }
        }
        return false;
    }

    public static StaticLayout generateStaticLayout(CharSequence charSequence, TextPaint textPaint, int i, int i2, int i3, int i4) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        StaticLayout staticLayout = new StaticLayout(charSequence, textPaint, i2, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        int i5 = i;
        int i6 = 0;
        for (int i7 = 0; i7 < i3; i7++) {
            staticLayout.getLineDirections(i7);
            if (staticLayout.getLineLeft(i7) != 0.0f || staticLayout.isRtlCharAt(staticLayout.getLineStart(i7)) || staticLayout.isRtlCharAt(staticLayout.getLineEnd(i7))) {
                i5 = i2;
            }
            int lineEnd = staticLayout.getLineEnd(i7);
            if (lineEnd == charSequence.length()) {
                break;
            }
            int i8 = (lineEnd - 1) + i6;
            if (spannableStringBuilder.charAt(i8) == ' ') {
                spannableStringBuilder.replace(i8, i8 + 1, (CharSequence) "\n");
            } else if (spannableStringBuilder.charAt(i8) != '\n') {
                spannableStringBuilder.insert(i8, (CharSequence) "\n");
                i6++;
            }
            if (i7 == staticLayout.getLineCount() - 1 || i7 == i4 - 1) {
                break;
            }
        }
        int i9 = i5;
        return StaticLayoutEx.createStaticLayout(spannableStringBuilder, textPaint, i9, Layout.Alignment.ALIGN_NORMAL, 1.0f, AndroidUtilities.dp(1.0f), false, TextUtils.TruncateAt.END, i9, i4, true);
    }

    private ColorFilter getAdaptiveEmojiColorFilter(int i, int i2) {
        if (this.adaptiveEmojiColorFilter == null) {
            this.adaptiveEmojiColor = new int[3];
            this.adaptiveEmojiColorFilter = new ColorFilter[3];
        }
        if (i2 != this.adaptiveEmojiColor[i] || this.adaptiveEmojiColorFilter[i] == null) {
            ColorFilter[] colorFilterArr = this.adaptiveEmojiColorFilter;
            this.adaptiveEmojiColor[i] = i2;
            colorFilterArr[i] = new PorterDuffColorFilter(i2, PorterDuff.Mode.SRC_IN);
        }
        return this.adaptiveEmojiColorFilter[i];
    }

    private int getAdditionalWidthForPosition(MessageObject.GroupedMessagePosition groupedMessagePosition) {
        if (groupedMessagePosition == null) {
            return 0;
        }
        int dp = (groupedMessagePosition.flags & 2) == 0 ? AndroidUtilities.dp(4.0f) : 0;
        return (groupedMessagePosition.flags & 1) == 0 ? dp + AndroidUtilities.dp(4.0f) : dp;
    }

    private String getAuthorName() {
        TLRPC.User user = this.currentUser;
        if (user != null) {
            return UserObject.getUserName(user);
        }
        if (this.currentChat == null) {
            MessageObject messageObject = this.currentMessageObject;
            return (messageObject == null || !messageObject.isSponsored()) ? "DELETED" : this.currentMessageObject.sponsoredTitle;
        }
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 != null && messageObject2.getDialogId() != 1271266957 && this.currentChat.signature_profiles) {
            long peerDialogId = DialogObject.getPeerDialogId(this.currentMessageObject.messageOwner.from_id);
            MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            if (peerDialogId >= 0) {
                TLRPC.User user2 = messagesController.getUser(Long.valueOf(peerDialogId));
                if (user2 != null) {
                    return UserObject.getUserName(user2);
                }
            } else {
                TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-peerDialogId));
                if (chat != null) {
                    return chat.title;
                }
            }
        }
        return this.currentChat.title;
    }

    private Object getAuthorStatus() {
        TLRPC.User user = this.currentUser;
        if (user == null) {
            return null;
        }
        Long emojiStatusDocumentId = UserObject.getEmojiStatusDocumentId(user);
        if (emojiStatusDocumentId != null) {
            return emojiStatusDocumentId;
        }
        if (this.currentUser.premium) {
            return ContextCompat.getDrawable(ApplicationLoader.applicationContext, R.drawable.msg_premium_liststar).mutate();
        }
        return null;
    }

    private int getExtraTimeX() {
        int i;
        if (!this.currentMessageObject.isOutOwner() && ((!this.mediaBackground || this.captionLayout != null) && (i = SharedConfig.bubbleRadius) > 11)) {
            return AndroidUtilities.dp((i - 11) / 1.5f);
        }
        if (!this.currentMessageObject.isOutOwner() && this.isPlayingRound && this.isAvatarVisible && this.currentMessageObject.type == 5) {
            return (int) ((AndroidUtilities.roundPlayingMessageSize - AndroidUtilities.roundMessageSize) * 0.7f);
        }
        return 0;
    }

    private ColorMatrixColorFilter getFancyBlurFilter() {
        if (this.fancyBlurFilter == null) {
            ColorMatrix colorMatrix = new ColorMatrix();
            AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, 0.9f);
            AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, 0.6f);
            this.fancyBlurFilter = new ColorMatrixColorFilter(colorMatrix);
        }
        return this.fancyBlurFilter;
    }

    private String getForwardedMessageText(MessageObject messageObject) {
        if (!this.hasPsaHint) {
            return LocaleController.getString(R.string.ForwardedFrom);
        }
        String string = LocaleController.getString("PsaMessage_" + messageObject.messageOwner.fwd_from.psa_type);
        return string == null ? LocaleController.getString("PsaMessageDefault", R.string.PsaMessageDefault) : string;
    }

    private int getGroupPhotosWidth() {
        int parentWidth = getParentWidth();
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.preview) {
            parentWidth = this.parentWidth;
        }
        if (AndroidUtilities.isInMultiwindow || !AndroidUtilities.isTablet()) {
            return parentWidth;
        }
        if (AndroidUtilities.isSmallTablet() && getResources().getConfiguration().orientation != 2) {
            return parentWidth;
        }
        int i = (parentWidth / 100) * 35;
        if (i < AndroidUtilities.dp(320.0f)) {
            i = AndroidUtilities.dp(320.0f);
        }
        return parentWidth - i;
    }

    public int getIconForCurrentState() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.getIconForCurrentState():int");
    }

    public static Point getMessageSize(int i, int i2) {
        return getMessageSize(i, i2, 0, 0);
    }

    private static org.telegram.ui.Components.Point getMessageSize(int r3, int r4, int r5, int r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.getMessageSize(int, int, int, int):org.telegram.ui.Components.Point");
    }

    public int getMiniIconForCurrentState() {
        int i = this.miniButtonState;
        if (i < 0) {
            return 4;
        }
        return i == 0 ? 2 : 3;
    }

    private String getNameFromDialogId(long j) {
        TLRPC.Chat chat;
        if (j > 0) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
            if (user != null) {
                return UserObject.getUserName(user);
            }
        } else if (j < 0 && (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j))) != null) {
            return chat.title;
        }
        return null;
    }

    public int[] getRealSpanStartAndEnd(Spannable spannable, CharacterStyle characterStyle) {
        int i;
        int i2;
        boolean z;
        TextStyleSpan.TextStyleRun style;
        TLRPC.MessageEntity messageEntity;
        if (!(characterStyle instanceof URLSpanBrowser) || (style = ((URLSpanBrowser) characterStyle).getStyle()) == null || (messageEntity = style.urlEntity) == null) {
            i = 0;
            i2 = 0;
            z = false;
        } else {
            i = messageEntity.offset;
            i2 = messageEntity.length + i;
            z = true;
        }
        if (!z) {
            i = spannable.getSpanStart(characterStyle);
            i2 = spannable.getSpanEnd(characterStyle);
        }
        return new int[]{i, i2};
    }

    private ArrayList<TLRPC.Peer> getRecentRepliers() {
        TLRPC.MessageReplies messageReplies;
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.messages.isEmpty() && (messageReplies = this.currentMessagesGroup.messages.get(0).messageOwner.replies) != null) {
            return messageReplies.recent_repliers;
        }
        TLRPC.MessageReplies messageReplies2 = this.currentMessageObject.messageOwner.replies;
        if (messageReplies2 != null) {
            return messageReplies2.recent_repliers;
        }
        return null;
    }

    public int getRepliesCount() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        return ((groupedMessages == null || groupedMessages.messages.isEmpty()) ? this.currentMessageObject : this.currentMessagesGroup.messages.get(0)).getRepliesCount();
    }

    private int getSelectionOverlayColor() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider == null) {
            return 0;
        }
        MessageObject messageObject = this.currentMessageObject;
        return resourcesProvider.getColor((messageObject == null || !messageObject.isOut()) ? Theme.key_chat_inBubbleSelectedOverlay : Theme.key_chat_outBubbleSelectedOverlay);
    }

    private Drawable getThemedDrawable(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Drawable drawable = resourcesProvider != null ? resourcesProvider.getDrawable(str) : null;
        return drawable != null ? drawable : Theme.getThemeDrawable(str);
    }

    private float getUseTranscribeButtonProgress() {
        TransitionParams transitionParams = this.transitionParams;
        if (!transitionParams.animateUseTranscribeButton) {
            return this.useTranscribeButton ? 1.0f : 0.0f;
        }
        boolean z = this.useTranscribeButton;
        float f = transitionParams.animateChangeProgress;
        return z ? f : 1.0f - f;
    }

    private boolean hasSelectionOverlay() {
        int selectionOverlayColor = getSelectionOverlayColor();
        return (selectionOverlayColor == 0 || selectionOverlayColor == -65536) ? false : true;
    }

    private void highlight(int i, int i2, ArrayList arrayList) {
        if (arrayList == null) {
            return;
        }
        int i3 = i2 - i;
        int i4 = 0;
        while (true) {
            if (i4 >= arrayList.size()) {
                break;
            }
            MessageObject.TextLayoutBlock textLayoutBlock = (MessageObject.TextLayoutBlock) arrayList.get(i4);
            if (i < textLayoutBlock.charactersOffset || i >= textLayoutBlock.charactersEnd) {
                i4++;
            } else {
                this.linkSelectionBlockNum = i4;
                resetUrlPaths();
                try {
                    LinkPath obtainNewUrlPath = obtainNewUrlPath();
                    obtainNewUrlPath.setUseCornerPathImplementation(true);
                    obtainNewUrlPath.setCurrentLayout(textLayoutBlock.textLayout, i, 0.0f);
                    textLayoutBlock.textLayout.getSelectionPath(i, i2, obtainNewUrlPath);
                    obtainNewUrlPath.closeRects();
                    float f = textLayoutBlock.height + textLayoutBlock.padBottom;
                    if (i2 >= textLayoutBlock.charactersOffset + i3) {
                        for (int i5 = i4 + 1; i5 < arrayList.size(); i5++) {
                            MessageObject.TextLayoutBlock textLayoutBlock2 = (MessageObject.TextLayoutBlock) arrayList.get(i5);
                            int i6 = textLayoutBlock2.charactersEnd - textLayoutBlock2.charactersOffset;
                            LinkPath obtainNewUrlPath2 = obtainNewUrlPath();
                            obtainNewUrlPath2.setUseCornerPathImplementation(true);
                            float f2 = f + textLayoutBlock2.padTop;
                            obtainNewUrlPath2.setCurrentLayout(textLayoutBlock2.textLayout, 0, f2);
                            f = f2 + textLayoutBlock2.height + textLayoutBlock2.padBottom;
                            textLayoutBlock2.textLayout.getSelectionPath(0, i2 - textLayoutBlock2.charactersOffset, obtainNewUrlPath2);
                            obtainNewUrlPath2.closeRects();
                            if (i2 < (textLayoutBlock.charactersOffset + i6) - 1) {
                                break;
                            }
                        }
                    }
                    if (this.highlightedQuote) {
                        this.isHighlightedAnimated = true;
                        this.highlightProgress = 2500;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
                invalidate();
            }
        }
        this.highlightCaptionToSetStart = -1;
        this.highlightCaptionToSetEnd = -1;
    }

    private boolean intersect(float f, float f2, float f3, float f4) {
        return f <= f3 ? f2 >= f3 : f <= f4;
    }

    private boolean invalidateParentForce() {
        MessageObject messageObject;
        return (this.links.isEmpty() && this.reactionsLayoutInBubble.isEmpty && ((messageObject = this.currentMessageObject) == null || !messageObject.preview)) ? false : true;
    }

    private void invalidateWithParent() {
        if (this.currentMessagesGroup != null && getParent() != null) {
            ((ViewGroup) getParent()).invalidate();
        }
        invalidate();
    }

    private boolean isCurrentLocationTimeExpired(MessageObject messageObject) {
        int i = MessageObject.getMedia(this.currentMessageObject.messageOwner).period;
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        if (i == Integer.MAX_VALUE) {
            return false;
        }
        int i2 = i % 60;
        int abs = Math.abs(currentTime - messageObject.messageOwner.date);
        return i2 == 0 ? abs > i : abs > i + (-5);
    }

    private boolean isDrawSelectionBackground() {
        MessageObject messageObject;
        return ((isPressed() && this.isCheckPressed) || ((!this.isCheckPressed && this.isPressed) || this.isHighlighted)) && !textIsSelectionMode() && !hasSelectionOverlay() && ((messageObject = this.currentMessageObject) == null || !messageObject.preview);
    }

    public boolean isOpenChatByShare(MessageObject messageObject) {
        ChatMessageCellDelegate chatMessageCellDelegate;
        TLRPC.MessageFwdHeader messageFwdHeader = messageObject.messageOwner.fwd_from;
        return (messageFwdHeader == null || messageFwdHeader.saved_from_peer == null || ((chatMessageCellDelegate = this.delegate) != null && !chatMessageCellDelegate.isReplyOrSelf())) ? false : true;
    }

    private boolean isPhotoDataChanged(org.telegram.messenger.MessageObject r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.isPhotoDataChanged(org.telegram.messenger.MessageObject):boolean");
    }

    private float isSmallImage() {
        TransitionParams transitionParams = this.transitionParams;
        if (!transitionParams.animateSmallImage) {
            return this.isSmallImage ? 1.0f : 0.0f;
        }
        boolean z = this.isSmallImage;
        float f = transitionParams.animateChangeProgress;
        return z ? f : 1.0f - f;
    }

    private boolean isUserDataChanged() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.isUserDataChanged():boolean");
    }

    public void lambda$checkBotButtonMotionEvent$4(int i) {
        int i2 = this.pressedBotButton;
        if (i == i2) {
            BotButton botButton = (BotButton) this.botButtons.get(i2);
            if (botButton != null) {
                if (botButton.selectorDrawable != null) {
                    botButton.selectorDrawable.setState(StateSet.NOTHING);
                }
                botButton.setPressed(false);
                if (!this.currentMessageObject.scheduled && botButton.button != null) {
                    cancelCheckLongPress();
                    this.delegate.didLongPressBotButton(this, botButton.button);
                }
            }
            this.pressedBotButton = -1;
            invalidateOutbounds();
        }
    }

    public void lambda$checkReplyTouchEvent$5() {
        if (this.replyPressed && !this.replySelectorPressed && this.replySelectorCanBePressed) {
            this.replySelectorPressed = true;
            this.replySelector.setState(new int[]{16842919, 16842910});
        }
    }

    public void lambda$checkReplyTouchEvent$6() {
        this.replySelector.setState(new int[0]);
        invalidate();
    }

    public void lambda$checkSpoilersMotionEvent$0() {
        this.isSpoilerRevealing = false;
        getMessageObject().isSpoilersRevealed = true;
        MessageObject.TextLayoutBlocks textLayoutBlocks = this.captionLayout;
        if (textLayoutBlocks != null) {
            Iterator<MessageObject.TextLayoutBlock> it = textLayoutBlocks.textLayoutBlocks.iterator();
            while (it.hasNext()) {
                it.next().spoilers.clear();
            }
        } else {
            ArrayList<MessageObject.TextLayoutBlock> arrayList = this.currentMessageObject.textLayoutBlocks;
            if (arrayList != null) {
                Iterator<MessageObject.TextLayoutBlock> it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    it2.next().spoilers.clear();
                }
            }
        }
        invalidate();
    }

    public void lambda$checkSpoilersMotionEvent$1() {
        post(new Runnable() {
            @Override
            public final void run() {
                ChatMessageCell.this.lambda$checkSpoilersMotionEvent$0();
            }
        });
    }

    public static void lambda$checkSpoilersMotionEvent$2(ChatMessageCell chatMessageCell) {
        chatMessageCell.getMessageObject().replyMessageObject.isSpoilersRevealed = true;
        chatMessageCell.replySpoilers.clear();
        chatMessageCell.invalidate();
    }

    public void lambda$checkSpoilersMotionEvent$3(final ChatMessageCell chatMessageCell) {
        post(new Runnable() {
            @Override
            public final void run() {
                ChatMessageCell.lambda$checkSpoilersMotionEvent$2(ChatMessageCell.this);
            }
        });
    }

    public void lambda$createStatusDrawableAnimator$13(boolean z, ValueAnimator valueAnimator) {
        this.statusDrawableProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (!z || getParent() == null) {
            return;
        }
        ((View) getParent()).invalidate();
    }

    public void lambda$drawContent$11(Canvas canvas) {
        this.radialProgress.draw(canvas);
    }

    public void lambda$onLongPress$10() {
        this.replySelector.setState(new int[0]);
        invalidate();
    }

    public void lambda$setMessageContent$7(TLRPC.User user, int i, TLRPC.Chat chat, long j) {
        if (user != null) {
            this.commentAvatarDrawables[i].setInfo(this.currentAccount, user);
            this.commentAvatarImages[i].setForUserOrChat(user, this.commentAvatarDrawables[i]);
            return;
        }
        AvatarDrawable[] avatarDrawableArr = this.commentAvatarDrawables;
        if (chat == null) {
            avatarDrawableArr[i].setInfo(j, "", "");
        } else {
            avatarDrawableArr[i].setInfo(this.currentAccount, chat);
            this.commentAvatarImages[i].setForUserOrChat(chat, this.commentAvatarDrawables[i]);
        }
    }

    public static int lambda$setMessageContent$8(PollButton pollButton, PollButton pollButton2) {
        if (pollButton.decimal > pollButton2.decimal) {
            return -1;
        }
        if (pollButton.decimal < pollButton2.decimal) {
            return 1;
        }
        if (pollButton.decimal != pollButton2.decimal) {
            return 0;
        }
        if (pollButton.percent > pollButton2.percent) {
            return 1;
        }
        return pollButton.percent < pollButton2.percent ? -1 : 0;
    }

    public void lambda$startRevealMedia$12(ValueAnimator valueAnimator) {
        this.mediaSpoilerRevealProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public boolean lambda$updateFlagSecure$9() {
        TLRPC.Message message;
        GroupMedia groupMedia;
        MessageObject messageObject = this.currentMessageObject;
        return (messageObject == null || (message = messageObject.messageOwner) == null || ((messageObject.type != 29 || ((groupMedia = this.groupMedia) != null && !groupMedia.hidden)) && !message.noforwards && !messageObject.isVoiceOnce() && !this.currentMessageObject.hasRevealedExtendedMedia())) ? false : true;
    }

    private int layoutFactCheck(int r28) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.layoutFactCheck(int):int");
    }

    private boolean loopStickers() {
        return LiteMode.isEnabled(2);
    }

    private void measureTime(org.telegram.messenger.MessageObject r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.measureTime(org.telegram.messenger.MessageObject):void");
    }

    private float mediaAbove() {
        TransitionParams transitionParams = this.transitionParams;
        if (!transitionParams.animateMediaAbove) {
            return this.captionAbove ? 1.0f : 0.0f;
        }
        boolean z = this.captionAbove;
        return AndroidUtilities.lerp(z ? 0.0f : 1.0f, z ? 1.0f : 0.0f, transitionParams.animateChangeProgress);
    }

    private LinkPath obtainNewUrlPath() {
        LinkPath linkPath;
        if (this.urlPathCache.isEmpty()) {
            linkPath = new LinkPath(true);
        } else {
            linkPath = (LinkPath) this.urlPathCache.get(0);
            this.urlPathCache.remove(0);
        }
        linkPath.reset();
        this.urlPathSelection.add(linkPath);
        return linkPath;
    }

    private void resetCodeSelectors() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.textLayoutBlocks != null) {
            for (int i = 0; i < this.currentMessageObject.textLayoutBlocks.size(); i++) {
                MessageObject.TextLayoutBlock textLayoutBlock = this.currentMessageObject.textLayoutBlocks.get(i);
                Drawable drawable = textLayoutBlock.copySelector;
                if (drawable != null) {
                    drawable.setCallback(this);
                    textLayoutBlock.copySelector.setState(StateSet.NOTHING);
                }
            }
        }
        if (this.captionLayout != null) {
            for (int i2 = 0; i2 < this.captionLayout.textLayoutBlocks.size(); i2++) {
                MessageObject.TextLayoutBlock textLayoutBlock2 = this.captionLayout.textLayoutBlocks.get(i2);
                Drawable drawable2 = textLayoutBlock2.copySelector;
                if (drawable2 != null) {
                    drawable2.setCallback(this);
                    textLayoutBlock2.copySelector.setState(StateSet.NOTHING);
                }
            }
        }
    }

    private void resetContactButtonsPressedState() {
        Drawable drawable;
        this.contactPressed = false;
        ButtonBounce buttonBounce = this.contactBounce;
        if (buttonBounce != null) {
            buttonBounce.setPressed(false);
        }
        if (Build.VERSION.SDK_INT >= 21 && (drawable = this.selectorDrawable[0]) != null) {
            drawable.setState(StateSet.NOTHING);
        }
        if (this.contactButtons != null) {
            for (int i = 0; i < this.contactButtons.size(); i++) {
                InstantViewButton instantViewButton = (InstantViewButton) this.contactButtons.get(i);
                if (instantViewButton.buttonBounce != null) {
                    instantViewButton.buttonBounce.setPressed(false);
                }
                if (instantViewButton.selectorDrawable != null) {
                    instantViewButton.selectorDrawable.setState(StateSet.NOTHING);
                }
            }
        }
    }

    private void resetUrlPaths() {
        if (this.quoteHighlight != null) {
            this.quoteHighlight = null;
        }
        if (this.urlPathSelection.isEmpty()) {
            return;
        }
        this.urlPathCache.addAll(this.urlPathSelection);
        this.urlPathSelection.clear();
    }

    public void sendAccessibilityEventForVirtualView(int i, int i2) {
        sendAccessibilityEventForVirtualView(i, i2, null);
    }

    private void sendAccessibilityEventForVirtualView(int i, int i2, String str) {
        if (((AccessibilityManager) getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
            AccessibilityEvent obtain = AccessibilityEvent.obtain(i2);
            obtain.setPackageName(getContext().getPackageName());
            obtain.setSource(this, i);
            if (str != null) {
                obtain.getText().add(str);
            }
            if (getParent() != null) {
                getParent().requestSendAccessibilityEvent(this, obtain);
            }
        }
    }

    private void setInstantButtonPressed(boolean z) {
        if (this.instantButtonBounce == null) {
            this.instantButtonBounce = new ButtonBounce(this);
        }
        ButtonBounce buttonBounce = this.instantButtonBounce;
        this.instantButtonPressed = z;
        buttonBounce.setPressed(z);
    }

    private void setMessageContent(org.telegram.messenger.MessageObject r88, org.telegram.messenger.MessageObject.GroupedMessages r89, boolean r90, boolean r91) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageContent(org.telegram.messenger.MessageObject, org.telegram.messenger.MessageObject$GroupedMessages, boolean, boolean):void");
    }

    private void setMessageObjectInternal(org.telegram.messenger.MessageObject r59) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setMessageObjectInternal(org.telegram.messenger.MessageObject):void");
    }

    private boolean shouldDrawCaptionLayout() {
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        MessageObject.GroupedMessages groupedMessages;
        MessageObject messageObject = this.currentMessageObject;
        return (messageObject == null || messageObject.preview || messageObject.isSponsored() || ((groupedMessagePosition = this.currentPosition) != null && ((groupedMessages = this.currentMessagesGroup) == null || !groupedMessages.isDocuments || (groupedMessagePosition.flags & 8) != 0)) || this.transitionParams.animateBackgroundBoundsInner || (this.enterTransitionInProgress && this.currentMessageObject.isVoice())) ? false : true;
    }

    public boolean shouldDrawMenuDrawable() {
        MessageObject messageObject;
        return (this.currentMessagesGroup == null || (this.currentPosition.flags & 4) != 0) && !this.hasLinkPreview && ((messageObject = this.currentMessageObject) == null || !(messageObject.isRepostPreview || messageObject.isSponsored()));
    }

    private boolean shouldDrawSelectionOverlay() {
        return hasSelectionOverlay() && ((isPressed() && this.isCheckPressed) || ((!this.isCheckPressed && this.isPressed) || this.isHighlighted || this.isHighlightedAnimated)) && !textIsSelectionMode() && ((this.currentMessagesGroup == null || this.drawSelectionBackground) && this.currentBackgroundDrawable != null);
    }

    private static boolean spanSupportsLongPress(CharacterStyle characterStyle) {
        return (characterStyle instanceof URLSpanMono) || (characterStyle instanceof URLSpan);
    }

    private void startRevealMedia(float f, float f2, float f3) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject.isMediaSpoilersRevealed || this.mediaSpoilerRevealProgress != 0.0f) {
            return;
        }
        if (messageObject.type == 3) {
            messageObject.forceUpdate = true;
            messageObject.revealingMediaSpoilers = true;
            setMessageContent(messageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
            MessageObject messageObject2 = this.currentMessageObject;
            messageObject2.revealingMediaSpoilers = false;
            messageObject2.forceUpdate = false;
            if (this.currentMessagesGroup != null) {
                this.radialProgress.setProgress(0.0f, false);
            }
        }
        this.mediaSpoilerRevealX = f;
        this.mediaSpoilerRevealY = f2;
        ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(MathUtils.clamp(this.mediaSpoilerRevealMaxRadius * 0.3f, 250.0f, 550.0f));
        duration.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ChatMessageCell.this.lambda$startRevealMedia$12(valueAnimator);
            }
        });
        duration.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ChatMessageCell.this.currentMessageObject.isMediaSpoilersRevealed = true;
                ChatMessageCell.this.invalidate();
            }
        });
        duration.start();
    }

    private boolean textIsSelectionMode() {
        ChatMessageCellDelegate chatMessageCellDelegate;
        return getCurrentMessagesGroup() == null && (chatMessageCellDelegate = this.delegate) != null && chatMessageCellDelegate.getTextSelectionHelper() != null && this.delegate.getTextSelectionHelper().isSelected(this.currentMessageObject);
    }

    private void updateCurrentUserAndChat() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updateCurrentUserAndChat():void");
    }

    private void updateFlagSecure() {
        if (this.flagSecure == null) {
            Activity findActivity = AndroidUtilities.findActivity(getContext());
            Window window = findActivity == null ? null : findActivity.getWindow();
            if (window != null) {
                FlagSecureReason flagSecureReason = new FlagSecureReason(window, new FlagSecureReason.FlagSecureCondition() {
                    @Override
                    public final boolean run() {
                        boolean lambda$updateFlagSecure$9;
                        lambda$updateFlagSecure$9 = ChatMessageCell.this.lambda$updateFlagSecure$9();
                        return lambda$updateFlagSecure$9;
                    }
                });
                this.flagSecure = flagSecureReason;
                if (this.attachedToWindow) {
                    flagSecureReason.attach();
                }
            }
        }
        FlagSecureReason flagSecureReason2 = this.flagSecure;
        if (flagSecureReason2 != null) {
            flagSecureReason2.invalidate();
        }
    }

    private void updatePollAnimations(long r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updatePollAnimations(long):void");
    }

    private void updateRadialProgressBackground() {
        if (this.drawRadialCheckBackground) {
            return;
        }
        boolean z = true;
        boolean z2 = (this.isHighlighted || this.isPressed || isPressed()) && !(this.drawPhotoImage && this.photoImage.hasBitmapImage());
        this.radialProgress.setPressed(z2 || this.buttonPressed != 0, false);
        if (this.hasMiniProgress != 0) {
            this.radialProgress.setPressed(z2 || this.miniButtonPressed != 0, true);
        }
        RadialProgress2 radialProgress2 = this.videoRadialProgress;
        if (!z2 && this.videoButtonPressed == 0) {
            z = false;
        }
        radialProgress2.setPressed(z, false);
    }

    public void updateReactionLayoutPosition() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updateReactionLayoutPosition():void");
    }

    private void updateSecretTimeText(MessageObject messageObject) {
        CharSequence secretTimeString;
        if (messageObject == null || !messageObject.needDrawBluredPreview() || (secretTimeString = messageObject.getSecretTimeString()) == null) {
            return;
        }
        if (secretTimeString instanceof String) {
            int ceil = (int) Math.ceil(Theme.chat_infoPaint.measureText((String) secretTimeString));
            this.infoWidth = ceil;
            this.infoLayout = new StaticLayout(TextUtils.ellipsize(secretTimeString, Theme.chat_infoPaint, ceil, TextUtils.TruncateAt.END), Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } else {
            StaticLayout staticLayout = new StaticLayout(secretTimeString, Theme.chat_infoBoldPaint, getMeasuredWidth() > 0 ? getMeasuredWidth() : 9999, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.infoLayout = staticLayout;
            this.infoWidth = staticLayout.getLineCount() > 0 ? (int) this.infoLayout.getLineWidth(0) : 0;
        }
        invalidate();
    }

    private void updateSeekBarWaveformWidth(android.graphics.Canvas r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updateSeekBarWaveformWidth(android.graphics.Canvas):void");
    }

    private void updateSelectionTextPosition() {
        TextSelectionHelper.ChatListTextSelectionHelper textSelectionHelper;
        int i;
        int i2;
        int i3;
        int dp;
        if (getDelegate() == null || getDelegate().getTextSelectionHelper() == null || !getDelegate().getTextSelectionHelper().isSelected(this.currentMessageObject)) {
            return;
        }
        int textSelectionType = getDelegate().getTextSelectionHelper().getTextSelectionType(this);
        if (textSelectionType == TextSelectionHelper.ChatListTextSelectionHelper.TYPE_DESCRIPTION) {
            getDelegate().getTextSelectionHelper().updateTextPosition((this.hasGamePreview ? this.unmovedTextX - AndroidUtilities.dp(10.0f) : this.unmovedTextX + AndroidUtilities.dp(1.0f)) + AndroidUtilities.dp(10.0f) + this.descriptionX, this.descriptionY);
            return;
        }
        if (textSelectionType == TextSelectionHelper.ChatListTextSelectionHelper.TYPE_CAPTION) {
            updateCaptionLayout();
            textSelectionHelper = getDelegate().getTextSelectionHelper();
            i = (int) this.captionX;
            i2 = (int) this.captionY;
        } else {
            if (textSelectionType == TextSelectionHelper.ChatListTextSelectionHelper.TYPE_FACTCHECK) {
                MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
                if (groupedMessages == null || groupedMessages.isDocuments) {
                    float f = 12.0f;
                    if (this.currentMessageObject.isOutOwner()) {
                        dp = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f) + getExtraTextX();
                        if (this.currentMessageObject.type == 19) {
                            dp -= Math.max(0, ((Math.max(this.replyNameWidth, this.replyTextWidth) + dp) + AndroidUtilities.dp(14.0f)) - AndroidUtilities.displaySize.x);
                        }
                    } else {
                        if (this.mediaBackground) {
                            i3 = this.backgroundDrawableLeft;
                        } else {
                            i3 = this.backgroundDrawableLeft;
                            if (!this.drawPinnedBottom) {
                                f = 18.0f;
                            }
                        }
                        dp = i3 + AndroidUtilities.dp(f) + getExtraTextX();
                    }
                } else {
                    dp = (int) this.captionX;
                }
                int dp2 = ((int) (dp + this.transitionParams.deltaLeft)) - AndroidUtilities.dp(1.33f);
                int i4 = this.factCheckY;
                updateCaptionLayout();
                getDelegate().getTextSelectionHelper().updateTextPosition((dp2 + AndroidUtilities.dp(10.0f)) - this.factCheckTextLayoutLeft, i4 + AndroidUtilities.dp(22.0f));
                return;
            }
            textSelectionHelper = getDelegate().getTextSelectionHelper();
            i = this.textX;
            i2 = this.textY;
        }
        textSelectionHelper.updateTextPosition(i, i2);
    }

    private void updateSpoilersVisiblePart(int i, int i2) {
        ArrayList<MessageObject.TextLayoutBlock> arrayList;
        MessageObject.TextLayoutBlocks textLayoutBlocks = this.captionLayout;
        if (textLayoutBlocks != null && (arrayList = textLayoutBlocks.textLayoutBlocks) != null) {
            Iterator<MessageObject.TextLayoutBlock> it = arrayList.iterator();
            while (it.hasNext()) {
                MessageObject.TextLayoutBlock next = it.next();
                Iterator<SpoilerEffect> it2 = next.spoilers.iterator();
                while (it2.hasNext()) {
                    it2.next().setVisibleBounds(0.0f, (i - next.textYOffset(this.captionLayout.textLayoutBlocks, this.transitionParams)) - this.captionX, getWidth(), (i2 - next.textYOffset(this.captionLayout.textLayoutBlocks, this.transitionParams)) - this.captionY);
                }
            }
        }
        StaticLayout staticLayout = this.replyTextLayout;
        if (staticLayout != null) {
            float height = (-this.replyStartY) - staticLayout.getHeight();
            Iterator it3 = this.replySpoilers.iterator();
            while (it3.hasNext()) {
                ((SpoilerEffect) it3.next()).setVisibleBounds(0.0f, i + height, getWidth(), i2 + height);
            }
        }
        if (getMessageObject() == null || getMessageObject().textLayoutBlocks == null) {
            return;
        }
        Iterator<MessageObject.TextLayoutBlock> it4 = getMessageObject().textLayoutBlocks.iterator();
        while (it4.hasNext()) {
            MessageObject.TextLayoutBlock next2 = it4.next();
            Iterator<SpoilerEffect> it5 = next2.spoilers.iterator();
            while (it5.hasNext()) {
                it5.next().setVisibleBounds(0.0f, (i - next2.textYOffset(getMessageObject().textLayoutBlocks, this.transitionParams)) - this.textY, getWidth(), (i2 - next2.textYOffset(getMessageObject().textLayoutBlocks, this.transitionParams)) - this.textY);
            }
        }
    }

    private void updateTranslation() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        setTranslationX(this.slidingOffsetX + this.animationOffsetX + ((!messageObject.isOutOwner() || this.currentMessageObject.hasWideCode) ? this.checkBoxTranslation : 0));
    }

    private void updateWaveform() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updateWaveform():void");
    }

    public void applyServiceShaderMatrix() {
        applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, getX(), this.viewTop);
    }

    public boolean areTags() {
        TLRPC.Message message;
        TLRPC.TL_messageReactions tL_messageReactions;
        MessageObject primaryMessageObject = getPrimaryMessageObject();
        if (primaryMessageObject == null || (message = primaryMessageObject.messageOwner) == null || (tL_messageReactions = message.reactions) == null) {
            return false;
        }
        return tL_messageReactions.reactions_as_tags;
    }

    public int captionFlag() {
        return this.captionAbove ? 4 : 8;
    }

    public boolean checkLoadCaughtPremiumFloodWait() {
        return FileLoader.getInstance(this.currentAccount).checkLoadCaughtPremiumFloodWait(getFilename());
    }

    public boolean checkNeedDrawShareButton(org.telegram.messenger.MessageObject r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkNeedDrawShareButton(org.telegram.messenger.MessageObject):boolean");
    }

    public boolean checkSpoilersMotionEvent(MotionEvent motionEvent, int i) {
        int i2;
        MessageObject.GroupedMessages groupedMessages;
        if (i <= 15 && getParent() != null) {
            if (this.currentMessageObject.hasValidGroupId() && (groupedMessages = this.currentMessagesGroup) != null && !groupedMessages.isDocuments) {
                ViewGroup viewGroup = (ViewGroup) getParent();
                for (int i3 = 0; i3 < viewGroup.getChildCount(); i3++) {
                    View childAt = viewGroup.getChildAt(i3);
                    if (childAt instanceof ChatMessageCell) {
                        ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                        MessageObject.GroupedMessages currentMessagesGroup = chatMessageCell.getCurrentMessagesGroup();
                        MessageObject.GroupedMessagePosition currentPosition = chatMessageCell.getCurrentPosition();
                        if (currentMessagesGroup != null && currentMessagesGroup.groupId == this.currentMessagesGroup.groupId) {
                            int i4 = currentPosition.flags;
                            if ((i4 & 8) != 0 && (i4 & 1) != 0 && chatMessageCell != this) {
                                motionEvent.offsetLocation(getLeft() - chatMessageCell.getLeft(), getTop() - chatMessageCell.getTop());
                                boolean checkSpoilersMotionEvent = chatMessageCell.checkSpoilersMotionEvent(motionEvent, i + 1);
                                motionEvent.offsetLocation(-(getLeft() - chatMessageCell.getLeft()), -(getTop() - chatMessageCell.getTop()));
                                return checkSpoilersMotionEvent;
                            }
                        }
                    }
                }
            }
            if (this.isSpoilerRevealing) {
                return false;
            }
            int x = (int) motionEvent.getX();
            int y = (int) motionEvent.getY();
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                int i5 = this.textX;
                if (x >= i5 && y >= (i2 = this.textY)) {
                    MessageObject messageObject = this.currentMessageObject;
                    if (x <= i5 + messageObject.textWidth && y <= i2 + messageObject.textHeight(this.transitionParams)) {
                        ArrayList<MessageObject.TextLayoutBlock> arrayList = this.currentMessageObject.textLayoutBlocks;
                        for (int i6 = 0; i6 < arrayList.size() && arrayList.get(i6).textYOffset(this.currentMessageObject.textLayoutBlocks, this.transitionParams) <= y; i6++) {
                            MessageObject.TextLayoutBlock textLayoutBlock = arrayList.get(i6);
                            int i7 = textLayoutBlock.isRtl() ? (int) this.currentMessageObject.textXOffset : 0;
                            for (SpoilerEffect spoilerEffect : textLayoutBlock.spoilers) {
                                if (spoilerEffect.getBounds().contains((x - this.textX) + i7, (int) ((y - this.textY) - textLayoutBlock.textYOffset(this.currentMessageObject.textLayoutBlocks, this.transitionParams)))) {
                                    this.spoilerPressed = spoilerEffect;
                                    return true;
                                }
                            }
                        }
                    }
                }
                if (this.captionLayout != null) {
                    float f = x;
                    float f2 = this.captionX;
                    if (f >= f2) {
                        float f3 = y;
                        float f4 = this.captionY;
                        if (f3 >= f4 && f <= f2 + r14.textWidth && f3 <= f4 + r14.textHeight(this.transitionParams)) {
                            ArrayList<MessageObject.TextLayoutBlock> arrayList2 = this.captionLayout.textLayoutBlocks;
                            for (int i8 = 0; i8 < arrayList2.size() && arrayList2.get(i8).textYOffset(this.captionLayout.textLayoutBlocks, this.transitionParams) <= f3; i8++) {
                                MessageObject.TextLayoutBlock textLayoutBlock2 = arrayList2.get(i8);
                                int i9 = textLayoutBlock2.isRtl() ? (int) this.captionLayout.textXOffset : 0;
                                for (SpoilerEffect spoilerEffect2 : textLayoutBlock2.spoilers) {
                                    if (spoilerEffect2.getBounds().contains((int) ((f - this.captionX) + i9), (int) ((f3 - this.captionY) - textLayoutBlock2.textYOffset(this.captionLayout.textLayoutBlocks, this.transitionParams)))) {
                                        this.spoilerPressed = spoilerEffect2;
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (actionMasked == 1 && this.spoilerPressed != null) {
                playSoundEffect(0);
                this.sPath.rewind();
                MessageObject.TextLayoutBlocks textLayoutBlocks = this.captionLayout;
                if (textLayoutBlocks != null) {
                    Iterator<MessageObject.TextLayoutBlock> it = textLayoutBlocks.textLayoutBlocks.iterator();
                    while (it.hasNext()) {
                        MessageObject.TextLayoutBlock next = it.next();
                        Iterator<SpoilerEffect> it2 = next.spoilers.iterator();
                        while (it2.hasNext()) {
                            Rect bounds = it2.next().getBounds();
                            this.sPath.addRect(bounds.left, bounds.top + next.textYOffset(this.captionLayout.textLayoutBlocks, this.transitionParams), bounds.right, next.textYOffset(this.captionLayout.textLayoutBlocks, this.transitionParams) + bounds.bottom, Path.Direction.CW);
                        }
                    }
                } else {
                    Iterator<MessageObject.TextLayoutBlock> it3 = this.currentMessageObject.textLayoutBlocks.iterator();
                    while (it3.hasNext()) {
                        MessageObject.TextLayoutBlock next2 = it3.next();
                        Iterator<SpoilerEffect> it4 = next2.spoilers.iterator();
                        while (it4.hasNext()) {
                            Rect bounds2 = it4.next().getBounds();
                            this.sPath.addRect(bounds2.left, bounds2.top + next2.textYOffset(this.currentMessageObject.textLayoutBlocks, this.transitionParams), bounds2.right, next2.textYOffset(this.currentMessageObject.textLayoutBlocks, this.transitionParams) + bounds2.bottom, Path.Direction.CW);
                        }
                    }
                }
                this.sPath.computeBounds(this.rect, false);
                float sqrt = (float) Math.sqrt(Math.pow(this.rect.width(), 2.0d) + Math.pow(this.rect.height(), 2.0d));
                this.isSpoilerRevealing = true;
                this.spoilerPressed.setOnRippleEndCallback(new Runnable() {
                    @Override
                    public final void run() {
                        ChatMessageCell.this.lambda$checkSpoilersMotionEvent$1();
                    }
                });
                MessageObject.TextLayoutBlocks textLayoutBlocks2 = this.captionLayout;
                if (textLayoutBlocks2 != null) {
                    Iterator<MessageObject.TextLayoutBlock> it5 = textLayoutBlocks2.textLayoutBlocks.iterator();
                    while (it5.hasNext()) {
                        MessageObject.TextLayoutBlock next3 = it5.next();
                        int i10 = next3.isRtl() ? (int) this.captionLayout.textXOffset : 0;
                        Iterator<SpoilerEffect> it6 = next3.spoilers.iterator();
                        while (it6.hasNext()) {
                            it6.next().startRipple((x - this.captionX) + i10, (y - next3.textYOffset(this.captionLayout.textLayoutBlocks, this.transitionParams)) - this.captionY, sqrt);
                        }
                    }
                } else {
                    ArrayList<MessageObject.TextLayoutBlock> arrayList3 = this.currentMessageObject.textLayoutBlocks;
                    if (arrayList3 != null) {
                        Iterator<MessageObject.TextLayoutBlock> it7 = arrayList3.iterator();
                        while (it7.hasNext()) {
                            MessageObject.TextLayoutBlock next4 = it7.next();
                            int i11 = next4.isRtl() ? (int) this.currentMessageObject.textXOffset : 0;
                            Iterator<SpoilerEffect> it8 = next4.spoilers.iterator();
                            while (it8.hasNext()) {
                                it8.next().startRipple((x - this.textX) + i11, (y - next4.textYOffset(this.currentMessageObject.textLayoutBlocks, this.transitionParams)) - this.textY, sqrt);
                            }
                        }
                    }
                }
                if (getParent() instanceof RecyclerListView) {
                    ViewGroup viewGroup2 = (ViewGroup) getParent();
                    for (int i12 = 0; i12 < viewGroup2.getChildCount(); i12++) {
                        View childAt2 = viewGroup2.getChildAt(i12);
                        if (childAt2 instanceof ChatMessageCell) {
                            final ChatMessageCell chatMessageCell2 = (ChatMessageCell) childAt2;
                            if (chatMessageCell2.getMessageObject() != null && chatMessageCell2.getMessageObject().getReplyMsgId() == getMessageObject().getId() && !chatMessageCell2.replySpoilers.isEmpty()) {
                                ((SpoilerEffect) chatMessageCell2.replySpoilers.get(0)).setOnRippleEndCallback(new Runnable() {
                                    @Override
                                    public final void run() {
                                        ChatMessageCell.this.lambda$checkSpoilersMotionEvent$3(chatMessageCell2);
                                    }
                                });
                                Iterator it9 = chatMessageCell2.replySpoilers.iterator();
                                while (it9.hasNext()) {
                                    ((SpoilerEffect) it9.next()).startRipple(r4.getBounds().centerX(), r4.getBounds().centerY(), sqrt);
                                }
                            }
                        }
                    }
                }
                this.spoilerPressed = null;
                return true;
            }
        }
        return false;
    }

    public boolean checkUnreadReactions(float f, int i) {
        if (!this.reactionsLayoutInBubble.hasUnreadReactions) {
            return false;
        }
        float y = getY();
        float f2 = y + r2.y;
        return f2 > f && (f2 + ((float) this.reactionsLayoutInBubble.height)) - ((float) AndroidUtilities.dp(16.0f)) < ((float) i);
    }

    public boolean checkUploadCaughtPremiumFloodWait() {
        return FileLoader.getInstance(this.currentAccount).checkUploadCaughtPremiumFloodWait(getFilename());
    }

    public void checkVideoPlayback(boolean r4, android.graphics.Bitmap r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.checkVideoPlayback(boolean, android.graphics.Bitmap):void");
    }

    public int computeHeight(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages, boolean z) {
        this.photoImage.setIgnoreImageSet(true);
        this.avatarImage.setIgnoreImageSet(true);
        this.replyImageReceiver.setIgnoreImageSet(true);
        this.locationImageReceiver.setIgnoreImageSet(true);
        if (groupedMessages == null || groupedMessages.messages.size() == 1) {
            setMessageContent(messageObject, groupedMessages, false, false);
            this.photoImage.setIgnoreImageSet(false);
            this.avatarImage.setIgnoreImageSet(false);
            this.replyImageReceiver.setIgnoreImageSet(false);
            this.locationImageReceiver.setIgnoreImageSet(false);
            updateCaptionLayout();
            return this.totalHeight + this.keyboardHeight;
        }
        if (groupedMessages.messages.size() != groupedMessages.positions.size()) {
            groupedMessages.calculate();
        }
        this.computedGroupCaptionY = 0;
        int i = 0;
        for (int i2 = 0; i2 < groupedMessages.messages.size(); i2++) {
            MessageObject messageObject2 = groupedMessages.messages.get(i2);
            MessageObject.GroupedMessagePosition position = groupedMessages.getPosition(messageObject2);
            if (position != null && (position.flags & 1) != 0) {
                setMessageContent(messageObject2, groupedMessages, false, false);
                if (z && !TextUtils.isEmpty(this.currentCaption)) {
                    updateCaptionLayout();
                    this.computedGroupCaptionY = (int) (i + this.captionY);
                    this.computedCaptionLayout = this.captionLayout;
                }
                i += this.totalHeight + this.keyboardHeight;
            }
        }
        return i;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        ChannelRecommendationsCell channelRecommendationsCell = this.channelRecommendationsCell;
        if (channelRecommendationsCell != null) {
            channelRecommendationsCell.computeScroll();
        }
    }

    public int computeWidth(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages) {
        this.photoImage.setIgnoreImageSet(true);
        this.avatarImage.setIgnoreImageSet(true);
        this.replyImageReceiver.setIgnoreImageSet(true);
        this.locationImageReceiver.setIgnoreImageSet(true);
        if (groupedMessages == null || groupedMessages.messages.size() == 1) {
            setMessageContent(messageObject, groupedMessages, false, false);
            this.photoImage.setIgnoreImageSet(false);
            this.avatarImage.setIgnoreImageSet(false);
            this.replyImageReceiver.setIgnoreImageSet(false);
            this.locationImageReceiver.setIgnoreImageSet(false);
            updateCaptionLayout();
            return this.backgroundWidth;
        }
        if (groupedMessages.messages.size() != groupedMessages.positions.size()) {
            groupedMessages.calculate();
        }
        this.computedGroupCaptionY = 0;
        int i = 0;
        for (int i2 = 0; i2 < groupedMessages.messages.size(); i2++) {
            MessageObject messageObject2 = groupedMessages.messages.get(i2);
            MessageObject.GroupedMessagePosition position = groupedMessages.getPosition(messageObject2);
            if (position != null && (position.flags & 4) != 0) {
                setMessageContent(messageObject2, groupedMessages, false, false);
                i += this.backgroundWidth;
            }
        }
        return i;
    }

    public void copySpoilerEffect2AttachIndexFrom(ChatMessageCell chatMessageCell) {
        SpoilerEffect2 spoilerEffect2;
        if (chatMessageCell == null || (spoilerEffect2 = chatMessageCell.mediaSpoilerEffect2) == null) {
            return;
        }
        int attachIndex = spoilerEffect2.getAttachIndex(chatMessageCell);
        this.mediaSpoilerEffect2Index = Integer.valueOf(attachIndex);
        SpoilerEffect2 spoilerEffect22 = this.mediaSpoilerEffect2;
        if (spoilerEffect22 != null) {
            spoilerEffect22.reassignAttach(this, attachIndex);
        }
    }

    public void copyVisiblePartTo(ChatMessageCell chatMessageCell) {
        if (chatMessageCell == null) {
            return;
        }
        chatMessageCell.setVisiblePart(this.childPosition, this.visibleHeight, this.visibleParent, this.visibleParentOffset, this.visibleTop, this.parentWidth, this.parentHeight, this.blurredViewTopOffset, this.blurredViewBottomOffset);
    }

    public void createSelectorDrawable(final int r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.createSelectorDrawable(int):void");
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        MessageObject messageObject;
        if (i == NotificationCenter.startSpoilers) {
            setSpoilersSuppressed(false);
            return;
        }
        if (i == NotificationCenter.stopSpoilers) {
            setSpoilersSuppressed(true);
            return;
        }
        if (i == NotificationCenter.userInfoDidLoad) {
            TLRPC.User user = this.currentUser;
            if (user != null) {
                if (user.id == ((Long) objArr[0]).longValue()) {
                    setAvatar(this.currentMessageObject);
                    return;
                }
                return;
            }
            return;
        }
        if (i == NotificationCenter.emojiLoaded) {
            invalidate();
            return;
        }
        if (i != NotificationCenter.didUpdatePremiumGiftStickers || (messageObject = this.currentMessageObject) == null) {
            return;
        }
        TLRPC.MessageMedia messageMedia = messageObject.messageOwner.media;
        if ((messageMedia instanceof TLRPC.TL_messageMediaGiveaway) || (messageMedia instanceof TLRPC.TL_messageMediaGiveawayResults)) {
            setMessageObject(messageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
        }
    }

    @Override
    public void didSetImage(org.telegram.messenger.ImageReceiver r5, boolean r6, boolean r7, boolean r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.didSetImage(org.telegram.messenger.ImageReceiver, boolean, boolean, boolean):void");
    }

    @Override
    public void didSetImageBitmap(int i, String str, Drawable drawable) {
        ImageReceiver.ImageReceiverDelegate.CC.$default$didSetImageBitmap(this, i, str, drawable);
    }

    public void drawAnimatedEmojiCaption(android.graphics.Canvas r17, float r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawAnimatedEmojiCaption(android.graphics.Canvas, float):void");
    }

    public void drawAnimatedEmojis(Canvas canvas, float f) {
        drawAnimatedEmojiMessageText(canvas, f);
        if (shouldDrawCaptionLayout()) {
            drawAnimatedEmojiCaption(canvas, f);
        }
    }

    public void drawBackground(android.graphics.Canvas r16, int r17, int r18, int r19, int r20, boolean r21, boolean r22, boolean r23, int r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawBackground(android.graphics.Canvas, int, int, int, int, boolean, boolean, boolean, int):void");
    }

    public boolean drawBackgroundInParent() {
        MessageObject messageObject;
        return this.canDrawBackgroundInParent && (messageObject = this.currentMessageObject) != null && messageObject.isOutOwner() && getThemedColor(Theme.key_chat_outBubbleGradient1) != 0;
    }

    public void drawBackgroundInternal(android.graphics.Canvas r27, boolean r28) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawBackgroundInternal(android.graphics.Canvas, boolean):void");
    }

    public void drawBlurredPhoto(Canvas canvas) {
        ImageReceiver imageReceiver;
        if (this.currentMessageObject.isMediaSpoilersRevealed || this.mediaSpoilerRevealProgress == 1.0f) {
            return;
        }
        int[] roundRadius = this.photoImage.getRoundRadius();
        float[] fArr = this.mediaSpoilerRadii;
        float f = roundRadius[0];
        fArr[1] = f;
        fArr[0] = f;
        float f2 = roundRadius[1];
        fArr[3] = f2;
        fArr[2] = f2;
        float f3 = roundRadius[2];
        fArr[5] = f3;
        fArr[4] = f3;
        float f4 = roundRadius[3];
        fArr[7] = f4;
        fArr[6] = f4;
        this.mediaSpoilerPath.rewind();
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(this.photoImage.getImageX(), this.photoImage.getImageY(), this.photoImage.getImageX2(), this.photoImage.getImageY2());
        Path path = this.mediaSpoilerPath;
        float[] fArr2 = this.mediaSpoilerRadii;
        Path.Direction direction = Path.Direction.CW;
        path.addRoundRect(rectF, fArr2, direction);
        canvas.save();
        canvas.clipPath(this.mediaSpoilerPath);
        if (this.mediaSpoilerRevealProgress != 0.0f) {
            this.mediaSpoilerPath.rewind();
            this.mediaSpoilerPath.addCircle(this.mediaSpoilerRevealX, this.mediaSpoilerRevealY, this.mediaSpoilerRevealMaxRadius * this.mediaSpoilerRevealProgress, direction);
            canvas.clipPath(this.mediaSpoilerPath, Region.Op.DIFFERENCE);
        }
        if (this.currentMessageObject.needDrawBluredPreview()) {
            imageReceiver = this.photoImage;
        } else {
            this.blurredPhotoImage.setImageCoords(this.photoImage.getImageX(), this.photoImage.getImageY(), this.photoImage.getImageWidth(), this.photoImage.getImageHeight());
            this.blurredPhotoImage.setRoundRadius(this.photoImage.getRoundRadius());
            imageReceiver = this.blurredPhotoImage;
        }
        imageReceiver.draw(canvas);
        drawBlurredPhotoParticles(canvas);
        canvas.restore();
    }

    public void drawBlurredPhotoParticles(Canvas canvas) {
        if (this.mediaSpoilerEffect2 != null) {
            canvas.translate(this.photoImage.getImageX(), this.photoImage.getImageY());
            this.mediaSpoilerEffect2.draw(canvas, this, (int) this.photoImage.getImageWidth(), (int) this.photoImage.getImageHeight(), this.photoImage.getAlpha(), this.drawingToBitmap);
        } else {
            this.mediaSpoilerEffect.setColor(ColorUtils.setAlphaComponent(-1, (int) (Color.alpha(-1) * 0.325f * this.photoImage.getAlpha())));
            this.mediaSpoilerEffect.setBounds((int) this.photoImage.getImageX(), (int) this.photoImage.getImageY(), (int) this.photoImage.getImageX2(), (int) this.photoImage.getImageY2());
            this.mediaSpoilerEffect.draw(canvas);
        }
        invalidate();
    }

    public void drawCaptionLayout(Canvas canvas, boolean z, float f) {
        MessageObject.TextLayoutBlocks textLayoutBlocks;
        boolean z2;
        ChatMessageCell chatMessageCell;
        Canvas canvas2;
        boolean z3;
        float f2;
        if (this.animatedEmojiStack != null && !(canvas instanceof SizeNotifierFrameLayout.SimplerCanvas) && (this.captionLayout != null || this.transitionParams.animateOutCaptionLayout != null)) {
            this.animatedEmojiStack.clearPositions();
        }
        TransitionParams transitionParams = this.transitionParams;
        if (!transitionParams.animateReplaceCaptionLayout || transitionParams.animateChangeProgress == 1.0f) {
            textLayoutBlocks = this.captionLayout;
            z2 = true;
            chatMessageCell = this;
            canvas2 = canvas;
            z3 = z;
            f2 = f;
        } else {
            canvas2 = canvas;
            z3 = z;
            drawCaptionLayout(canvas2, transitionParams.animateOutCaptionLayout, false, z3, f * (1.0f - this.transitionParams.animateChangeProgress));
            textLayoutBlocks = this.captionLayout;
            f2 = f * this.transitionParams.animateChangeProgress;
            z2 = true;
            chatMessageCell = this;
        }
        chatMessageCell.drawCaptionLayout(canvas2, textLayoutBlocks, z2, z3, f2);
        if (!z) {
            drawAnimatedEmojiCaption(canvas, f);
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.messageOwner == null || !messageObject.isVoiceTranscriptionOpen()) {
            return;
        }
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2.messageOwner.voiceTranscriptionFinal || !TranscribeButton.isTranscribing(messageObject2)) {
            return;
        }
        invalidate();
    }

    public void drawCheckBox(Canvas canvas) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.isSending()) {
            return;
        }
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2.type == 27 || messageObject2.isSendError() || this.checkBox == null) {
            return;
        }
        if (this.checkBoxVisible || this.checkBoxAnimationInProgress) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            if (groupedMessagePosition != null) {
                int i = groupedMessagePosition.flags;
                if ((i & 8) == 0 || (i & 1) == 0) {
                    return;
                }
            }
            canvas.save();
            float y = getY();
            MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
            canvas.translate(0.0f, ((groupedMessages == null || groupedMessages.messages.size() <= 1) ? y + this.transitionParams.deltaTop : (getTop() + this.currentMessagesGroup.transitionParams.offsetTop) - getTranslationY()) + this.transitionYOffsetForDrawables);
            this.checkBox.draw(canvas);
            canvas.restore();
        }
    }

    public void drawCommentButton(Canvas canvas, float f) {
        if (this.drawSideButton != 3) {
            return;
        }
        int dp = AndroidUtilities.dp(32.0f);
        if (this.commentLayout != null) {
            this.sideStartY -= AndroidUtilities.dp(18.0f);
            dp += AndroidUtilities.dp(18.0f);
        }
        RectF rectF = this.rect;
        float f2 = this.sideStartX;
        rectF.set(f2, this.sideStartY, AndroidUtilities.dp(32.0f) + f2, this.sideStartY + dp);
        applyServiceShaderMatrix();
        if (f != 1.0f) {
            int alpha = getThemedPaint("paintChatActionBackground").getAlpha();
            getThemedPaint("paintChatActionBackground").setAlpha((int) (alpha * f));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), getThemedPaint("paintChatActionBackground"));
            getThemedPaint("paintChatActionBackground").setAlpha(alpha);
        } else {
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), getThemedPaint(this.sideButtonPressed ? "paintChatActionBackgroundSelected" : "paintChatActionBackground"));
        }
        if (hasGradientService()) {
            if (f != 1.0f) {
                int alpha2 = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
                Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (alpha2 * f));
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
                Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(alpha2);
            } else {
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
            }
        }
        Drawable themeDrawable = Theme.getThemeDrawable("drawableCommentSticker");
        BaseCell.setDrawableBounds(themeDrawable, this.sideStartX + AndroidUtilities.dp(4.0f), this.sideStartY + AndroidUtilities.dp(4.0f));
        if (f != 1.0f) {
            themeDrawable.setAlpha((int) (f * 255.0f));
            themeDrawable.draw(canvas);
            themeDrawable.setAlpha(255);
        } else {
            themeDrawable.draw(canvas);
        }
        if (this.commentLayout != null) {
            Theme.chat_stickerCommentCountPaint.setColor(getThemedColor(Theme.key_chat_stickerReplyNameText));
            Theme.chat_stickerCommentCountPaint.setAlpha((int) (f * 255.0f));
            if (this.transitionParams.animateComments) {
                if (this.transitionParams.animateCommentsLayout != null) {
                    canvas.save();
                    TextPaint textPaint = Theme.chat_stickerCommentCountPaint;
                    double d = this.transitionParams.animateChangeProgress;
                    Double.isNaN(d);
                    double d2 = (1.0d - d) * 255.0d;
                    double d3 = f;
                    Double.isNaN(d3);
                    textPaint.setAlpha((int) (d2 * d3));
                    canvas.translate(this.sideStartX + ((AndroidUtilities.dp(32.0f) - this.transitionParams.animateTotalCommentWidth) / 2), this.sideStartY + AndroidUtilities.dp(30.0f));
                    this.transitionParams.animateCommentsLayout.draw(canvas);
                    canvas.restore();
                }
                Theme.chat_stickerCommentCountPaint.setAlpha((int) (this.transitionParams.animateChangeProgress * 255.0f));
            }
            canvas.save();
            canvas.translate(this.sideStartX + ((AndroidUtilities.dp(32.0f) - this.totalCommentWidth) / 2), this.sideStartY + AndroidUtilities.dp(30.0f));
            this.commentLayout.draw(canvas);
            canvas.restore();
        }
    }

    public void drawCommentLayout(android.graphics.Canvas r25, float r26) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawCommentLayout(android.graphics.Canvas, float):void");
    }

    public void drawContent(final android.graphics.Canvas r40, boolean r41) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawContent(android.graphics.Canvas, boolean):void");
    }

    public void drawFactCheck(Canvas canvas, float f) {
        int i;
        int dp;
        int extraTextX;
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition == null || groupedMessagePosition.last) {
            float f2 = this.hasFactCheck ? 1.0f : 0.0f;
            TransitionParams transitionParams = this.transitionParams;
            if (transitionParams.animateFactCheck) {
                f2 = AndroidUtilities.lerp(1.0f - f2, f2, transitionParams.animateChangeProgress);
            }
            float f3 = f * f2;
            if (f3 <= 0.0f) {
                return;
            }
            float backgroundDrawableRight = getBackgroundDrawableRight();
            TransitionParams transitionParams2 = this.transitionParams;
            float dp2 = ((backgroundDrawableRight + (transitionParams2 != null ? transitionParams2.deltaRight : 0.0f)) - AndroidUtilities.dp(10 + ((!this.currentMessageObject.isOutOwner() || this.mediaBackground || this.drawPinnedBottom) ? 0 : 6))) - getExtraTextX();
            MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
            if (groupedMessages != null && !groupedMessages.isDocuments) {
                extraTextX = (int) this.captionX;
            } else if (this.currentMessageObject.isOutOwner()) {
                extraTextX = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f) + getExtraTextX();
                if (this.currentMessageObject.type == 19) {
                    extraTextX -= Math.max(0, ((Math.max(this.replyNameWidth, this.replyTextWidth) + extraTextX) + AndroidUtilities.dp(14.0f)) - AndroidUtilities.displaySize.x);
                }
            } else {
                if (this.mediaBackground) {
                    i = this.backgroundDrawableLeft;
                    dp = AndroidUtilities.dp(12.0f);
                } else {
                    i = this.backgroundDrawableLeft;
                    dp = AndroidUtilities.dp(this.drawPinnedBottom ? 12.0f : 18.0f);
                }
                extraTextX = i + dp + getExtraTextX();
            }
            int dp3 = ((int) (extraTextX + this.transitionParams.deltaLeft)) - AndroidUtilities.dp(1.33f);
            int i2 = this.factCheckY;
            float f4 = dp3;
            int i3 = (int) (dp2 - f4);
            int i4 = this.factCheckHeight;
            TransitionParams transitionParams3 = this.transitionParams;
            if (transitionParams3.animateFactCheckHeight) {
                i4 = AndroidUtilities.lerp(transitionParams3.animateFactCheckHeightFrom, i4, transitionParams3.animateChangeProgress);
            }
            float f5 = (!this.factCheckLarge || (getPrimaryMessageObject() != null && getPrimaryMessageObject().factCheckExpanded)) ? 1.0f : 0.0f;
            TransitionParams transitionParams4 = this.transitionParams;
            if (transitionParams4.animateFactCheckExpanded) {
                AndroidUtilities.lerp(1.0f - f5, f5, transitionParams4.animateChangeProgress);
            }
            if (this.factCheckLine == null) {
                this.factCheckLine = new ReplyMessageLine(this);
            }
            int factCheck = this.factCheckLine.setFactCheck(this.resourcesProvider);
            canvas.save();
            canvas.translate(f4, i2);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, i3, i4);
            ButtonBounce buttonBounce = this.factCheckBounce;
            float scale = buttonBounce != null ? buttonBounce.getScale(0.01f) : 1.0f;
            canvas.scale(scale, scale, rectF.centerX(), rectF.centerY());
            this.factCheckLine.drawBackground(canvas, rectF, 5.0f, 5.0f, 5.0f, f3, false, false);
            this.factCheckLine.drawLine(canvas, rectF, f3);
            Text text = this.factCheckTitle;
            if (text != null) {
                text.draw(canvas, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(12.0f), factCheck, f3);
                if (this.factCheckWhat != null) {
                    rectF.set((int) (AndroidUtilities.dp(10.0f) + this.factCheckTitle.getCurrentWidth() + AndroidUtilities.dp(4.0f)), AndroidUtilities.dp(4.33f), AndroidUtilities.dp(10.0f) + r1 + this.factCheckWhat.getCurrentWidth(), AndroidUtilities.dp(21.66f));
                    ButtonBounce buttonBounce2 = this.factCheckWhatBounce;
                    float scale2 = buttonBounce2 != null ? buttonBounce2.getScale(0.1f) : 1.0f;
                    canvas.save();
                    canvas.scale(scale2, scale2, rectF.centerX(), rectF.centerY());
                    canvas.drawRoundRect(rectF, AndroidUtilities.dp(17.0f), AndroidUtilities.dp(17.0f), this.factCheckLine.backgroundPaint);
                    this.factCheckWhat.draw(canvas, r1 + AndroidUtilities.dp(5.0f), AndroidUtilities.dp(12.0f), factCheck, f3);
                    canvas.restore();
                }
            }
            canvas.restore();
        }
    }

    public void drawFactCheckText(Canvas canvas, float f) {
        int i;
        int dp;
        float f2;
        int i2;
        float f3;
        float f4;
        RectF rectF;
        TextPaint textPaint;
        int i3;
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition == null || groupedMessagePosition.last) {
            float f5 = this.hasFactCheck ? 1.0f : 0.0f;
            TransitionParams transitionParams = this.transitionParams;
            if (transitionParams.animateFactCheck) {
                f5 = AndroidUtilities.lerp(1.0f - f5, f5, transitionParams.animateChangeProgress);
            }
            float f6 = f * f5;
            if (f6 <= 0.0f) {
                return;
            }
            float backgroundDrawableRight = getBackgroundDrawableRight();
            TransitionParams transitionParams2 = this.transitionParams;
            float dp2 = ((backgroundDrawableRight + (transitionParams2 != null ? transitionParams2.deltaRight : 0.0f)) - AndroidUtilities.dp(10 + ((!this.currentMessageObject.isOutOwner() || this.mediaBackground || this.drawPinnedBottom) ? 0 : 6))) - getExtraTextX();
            MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
            if (groupedMessages == null || groupedMessages.isDocuments) {
                float f7 = 12.0f;
                if (this.currentMessageObject.isOutOwner()) {
                    dp = this.backgroundDrawableLeft + AndroidUtilities.dp(12.0f) + getExtraTextX();
                    if (this.currentMessageObject.type == 19) {
                        dp -= Math.max(0, ((Math.max(this.replyNameWidth, this.replyTextWidth) + dp) + AndroidUtilities.dp(14.0f)) - AndroidUtilities.displaySize.x);
                    }
                } else {
                    if (this.mediaBackground) {
                        i = this.backgroundDrawableLeft;
                    } else {
                        i = this.backgroundDrawableLeft;
                        if (!this.drawPinnedBottom) {
                            f7 = 18.0f;
                        }
                    }
                    dp = i + AndroidUtilities.dp(f7) + getExtraTextX();
                }
            } else {
                dp = (int) this.captionX;
            }
            int dp3 = ((int) (dp + this.transitionParams.deltaLeft)) - AndroidUtilities.dp(1.33f);
            int i4 = this.factCheckY;
            float f8 = dp3;
            int i5 = (int) (dp2 - f8);
            int i6 = this.factCheckHeight;
            TransitionParams transitionParams3 = this.transitionParams;
            if (transitionParams3.animateFactCheckHeight) {
                i6 = AndroidUtilities.lerp(transitionParams3.animateFactCheckHeightFrom, i6, transitionParams3.animateChangeProgress);
            }
            int i7 = i6;
            float f9 = (!this.factCheckLarge || (getPrimaryMessageObject() != null && getPrimaryMessageObject().factCheckExpanded)) ? 1.0f : 0.0f;
            TransitionParams transitionParams4 = this.transitionParams;
            if (transitionParams4.animateFactCheckExpanded) {
                f9 = AndroidUtilities.lerp(1.0f - f9, f9, transitionParams4.animateChangeProgress);
            }
            float f10 = f9;
            if (this.factCheckLine == null) {
                this.factCheckLine = new ReplyMessageLine(this);
            }
            int factCheck = this.factCheckLine.setFactCheck(this.resourcesProvider);
            canvas.save();
            canvas.translate(f8, i4);
            RectF rectF2 = AndroidUtilities.rectTmp;
            float f11 = i5;
            float f12 = i7;
            rectF2.set(0.0f, 0.0f, f11, f12);
            ButtonBounce buttonBounce = this.factCheckBounce;
            float scale = buttonBounce != null ? buttonBounce.getScale(0.01f) : 1.0f;
            canvas.scale(scale, scale, rectF2.centerX(), rectF2.centerY());
            if (this.factCheckTextLayout != null) {
                if (this.factCheckLarge) {
                    f3 = f12;
                    f4 = f11;
                    rectF = rectF2;
                    f2 = f10;
                    i2 = factCheck;
                    canvas.saveLayerAlpha(0.0f, 0.0f, f11, i7 - 1, 255, 31);
                } else {
                    f3 = f12;
                    f4 = f11;
                    rectF = rectF2;
                    f2 = f10;
                    i2 = factCheck;
                }
                canvas.save();
                canvas.translate(AndroidUtilities.dp(10.0f) - this.factCheckTextLayoutLeft, AndroidUtilities.dp(22.0f));
                Theme.chat_replyTextPaint.linkColor = i2;
                LinkSpanDrawable.LinkCollector linkCollector = this.factCheckLinks;
                if (linkCollector != null && linkCollector.draw(canvas)) {
                    invalidateOutbounds();
                }
                if (this.currentMessageObject.isOutOwner()) {
                    textPaint = Theme.chat_replyTextPaint;
                    i3 = Theme.key_chat_messageTextOut;
                } else {
                    textPaint = Theme.chat_replyTextPaint;
                    i3 = Theme.key_chat_messageTextIn;
                }
                textPaint.setColor(getThemedColor(i3));
                int alpha = Theme.chat_replyTextPaint.getAlpha();
                Theme.chat_replyTextPaint.setAlpha((int) (alpha * f6));
                ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                if (chatMessageCellDelegate != null && chatMessageCellDelegate.getTextSelectionHelper() != null && getDelegate().getTextSelectionHelper().isSelected(this.currentMessageObject)) {
                    this.delegate.getTextSelectionHelper().drawFactCheck(this.currentMessageObject.isOutOwner(), this.factCheckTextLayout, canvas);
                }
                this.factCheckTextLayout.draw(canvas);
                canvas.restore();
                Theme.chat_replyTextPaint.setAlpha(alpha);
                rectF.set(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(22.0f) + this.factCheckTextLayoutHeight + AndroidUtilities.dp(6.33f), i5 - AndroidUtilities.dp(10.0f), AndroidUtilities.dp(22.0f) + this.factCheckTextLayoutHeight + AndroidUtilities.dp(6.33f) + 1);
                Theme.chat_titleLabelTextPaint.setColor(i2);
                Theme.chat_titleLabelTextPaint.setAlpha((int) (r2.getAlpha() * 0.5f * f6));
                canvas.drawRect(rectF, Theme.chat_titleLabelTextPaint);
                canvas.save();
                canvas.translate(AndroidUtilities.dp(10.0f) - this.factCheckText2LayoutLeft, AndroidUtilities.dp(22.0f) + this.factCheckTextLayoutHeight + AndroidUtilities.dp(12.66f));
                Theme.chat_titleLabelTextPaint.setColor(i2);
                Theme.chat_titleLabelTextPaint.setAlpha((int) (r1.getAlpha() * f6));
                this.factCheckText2Layout.draw(canvas);
                canvas.restore();
                if (this.clip == null) {
                    this.clip = new GradientClip();
                }
                if (this.factCheckLarge) {
                    canvas.save();
                    int dp4 = AndroidUtilities.dp((AndroidUtilities.dp(22.0f) + this.factCheckTextLayoutHeight) + AndroidUtilities.dp(12.66f) < i7 ? 20.0f : 24.0f);
                    rectF.set(0.0f, (i7 - AndroidUtilities.dp(6.66f)) - dp4, f4, f3);
                    float f13 = (1.0f - f2) * f6;
                    this.clip.draw(canvas, rectF, 3, f13);
                    float f14 = i7 - dp4;
                    rectF.set(i5 - AndroidUtilities.dp(60.0f), f14, i5 - AndroidUtilities.dp(32.0f), f3);
                    this.clip.draw(canvas, rectF, 2, f13);
                    rectF.set(i5 - AndroidUtilities.dp(32.0f), f14, f4, f3);
                    canvas.drawRect(rectF, this.clip.getPaint(2, f13));
                    canvas.restore();
                    canvas.restore();
                }
            } else {
                f2 = f10;
                i2 = factCheck;
            }
            if (this.factCheckLarge) {
                if (this.factCheckArrow == null) {
                    Drawable mutate = getContext().getResources().getDrawable(R.drawable.arrow_more).mutate();
                    this.factCheckArrow = mutate;
                    this.factCheckArrowColor = i2;
                    mutate.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.SRC_IN));
                }
                if (i2 != this.factCheckArrowColor) {
                    Drawable drawable = this.factCheckArrow;
                    this.factCheckArrowColor = i2;
                    drawable.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.SRC_IN));
                }
                canvas.save();
                int dp5 = AndroidUtilities.dp(16.0f);
                this.factCheckArrow.setBounds((i5 - dp5) - AndroidUtilities.dp(7.0f), (i7 - dp5) - AndroidUtilities.dp(5.0f), i5 - AndroidUtilities.dp(7.0f), i7 - AndroidUtilities.dp(5.0f));
                canvas.rotate(AndroidUtilities.lerp(0, 180, f2), this.factCheckArrow.getBounds().centerX(), this.factCheckArrow.getBounds().centerY());
                this.factCheckArrow.setAlpha((int) (f6 * 255.0f));
                this.factCheckArrow.draw(canvas);
                canvas.restore();
            }
            canvas.restore();
        }
    }

    public void drawLinkPreview(android.graphics.Canvas r48, float r49) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawLinkPreview(android.graphics.Canvas, float):void");
    }

    public void drawMessageText(float r46, float r47, android.graphics.Canvas r48, java.util.ArrayList r49, float r50, boolean r51, float r52, boolean r53, boolean r54, boolean r55) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawMessageText(float, float, android.graphics.Canvas, java.util.ArrayList, float, boolean, float, boolean, boolean, boolean):void");
    }

    public void drawMessageText(Canvas canvas) {
        float f;
        float f2;
        float f3;
        int dp;
        float f4;
        int dp2;
        float f5;
        ArrayList<MessageObject.TextLayoutBlock> arrayList;
        float f6;
        boolean z;
        boolean z2;
        boolean z3;
        float f7;
        boolean z4;
        ChatMessageCell chatMessageCell;
        float f8;
        Canvas canvas2;
        int dp3;
        int dp4;
        int i;
        int dp5;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.isSponsored()) {
            return;
        }
        float f9 = this.textY;
        TransitionParams transitionParams = this.transitionParams;
        if (transitionParams.animateText) {
            float f10 = transitionParams.animateFromTextY;
            float f11 = transitionParams.animateChangeProgress;
            f = (f10 * (1.0f - f11)) + (f11 * f9);
        } else {
            f = f9;
        }
        if (transitionParams.animateChangeProgress == 1.0f || !transitionParams.animateMessageText) {
            boolean z5 = transitionParams.animateLinkAbove;
            if (!z5 || this.currentBackgroundDrawable == null) {
                float f12 = this.textX;
                MessageObject messageObject2 = this.currentMessageObject;
                drawMessageText(f12, f, canvas, messageObject2.textLayoutBlocks, messageObject2.textXOffset, true, 1.0f, true, false, false);
                return;
            }
            if (z5) {
                float textHeight = (this.linkPreviewAbove ? 1 : -1) * this.currentMessageObject.textHeight(transitionParams);
                TransitionParams transitionParams2 = this.transitionParams;
                f = f9 + (textHeight * (1.0f - transitionParams2.animateChangeProgress));
                f2 = transitionParams2.animateFromTextY - (((this.linkPreviewAbove ? 1 : -1) * this.currentMessageObject.textHeight(transitionParams2)) * this.transitionParams.animateChangeProgress);
            } else {
                f2 = f;
            }
            canvas.save();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(this.currentBackgroundDrawable.getBounds());
            if (!this.currentMessageObject.isOutOwner() || this.mediaBackground || this.pinnedBottom) {
                rectF.left += AndroidUtilities.dp(4.0f);
                f3 = rectF.right;
                dp = AndroidUtilities.dp(4.0f);
            } else {
                rectF.left += AndroidUtilities.dp(4.0f);
                f3 = rectF.right;
                dp = AndroidUtilities.dp(10.0f);
            }
            rectF.right = f3 - dp;
            float f13 = rectF.left;
            float f14 = this.transitionParams.animateFromTextY;
            canvas.clipRect(f13, f14, rectF.right, this.currentMessageObject.textHeight(r1) + f14 + AndroidUtilities.dp(4.0f));
            float f15 = this.textX;
            MessageObject messageObject3 = this.currentMessageObject;
            drawMessageText(f15, f2, canvas, messageObject3.textLayoutBlocks, messageObject3.textXOffset, false, 1.0f - this.transitionParams.animateChangeProgress, true, false, false);
            canvas.restore();
            canvas.save();
            rectF.set(this.currentBackgroundDrawable.getBounds());
            if (!this.currentMessageObject.isOutOwner() || this.mediaBackground || this.pinnedBottom) {
                rectF.left += AndroidUtilities.dp(4.0f);
                f4 = rectF.right;
                dp2 = AndroidUtilities.dp(4.0f);
            } else {
                rectF.left += AndroidUtilities.dp(4.0f);
                f4 = rectF.right;
                dp2 = AndroidUtilities.dp(10.0f);
            }
            rectF.right = f4 - dp2;
            canvas.clipRect(rectF.left, this.textY, rectF.right, r1 + this.currentMessageObject.textHeight(this.transitionParams) + AndroidUtilities.dp(4.0f));
            f5 = this.textX;
            MessageObject messageObject4 = this.currentMessageObject;
            arrayList = messageObject4.textLayoutBlocks;
            f6 = messageObject4.textXOffset;
            z = false;
            z2 = false;
            z3 = true;
            f7 = 1.0f;
            z4 = true;
            chatMessageCell = this;
            f8 = f;
            canvas2 = canvas;
        } else {
            canvas.save();
            Theme.MessageDrawable messageDrawable = this.currentBackgroundDrawable;
            if (messageDrawable != null) {
                Rect bounds = messageDrawable.getBounds();
                if (!this.currentMessageObject.isOutOwner() || this.mediaBackground || this.pinnedBottom) {
                    dp3 = bounds.left + AndroidUtilities.dp(4.0f);
                    dp4 = bounds.top + AndroidUtilities.dp(4.0f);
                    i = bounds.right;
                    dp5 = AndroidUtilities.dp(4.0f);
                } else {
                    dp3 = bounds.left + AndroidUtilities.dp(4.0f);
                    dp4 = bounds.top + AndroidUtilities.dp(4.0f);
                    i = bounds.right;
                    dp5 = AndroidUtilities.dp(10.0f);
                }
                canvas.clipRect(dp3, dp4, i - dp5, bounds.bottom - AndroidUtilities.dp(4.0f));
            }
            z = false;
            z2 = false;
            z4 = true;
            f8 = f;
            canvas2 = canvas;
            drawMessageText(this.textX, f8, canvas2, this.transitionParams.animateOutTextBlocks, this.transitionParams.animateOutTextXOffset, false, 1.0f - this.transitionParams.animateChangeProgress, true, false, false);
            f5 = this.textX;
            MessageObject messageObject5 = this.currentMessageObject;
            arrayList = messageObject5.textLayoutBlocks;
            f6 = messageObject5.textXOffset;
            f7 = this.transitionParams.animateChangeProgress;
            z3 = true;
            chatMessageCell = this;
        }
        chatMessageCell.drawMessageText(f5, f8, canvas2, arrayList, f6, z3, f7, z4, z, z2);
        canvas.restore();
    }

    public void drawMessageText(Canvas canvas, ArrayList arrayList, boolean z, float f, boolean z2) {
        float f2;
        float f3 = this.textY;
        TransitionParams transitionParams = this.transitionParams;
        if (transitionParams.animateText) {
            float f4 = transitionParams.animateFromTextY;
            float f5 = transitionParams.animateChangeProgress;
            f2 = (f4 * (1.0f - f5)) + (f3 * f5);
        } else {
            f2 = f3;
        }
        float f6 = this.textX;
        MessageObject messageObject = this.currentMessageObject;
        drawMessageText(f6, f2, canvas, arrayList, messageObject == null ? 0.0f : messageObject.textXOffset, z, f, false, z2, false);
    }

    public void drawNamesLayout(android.graphics.Canvas r42, float r43) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawNamesLayout(android.graphics.Canvas, float):void");
    }

    public void drawOutboundsContent(android.graphics.Canvas r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawOutboundsContent(android.graphics.Canvas):void");
    }

    public void drawOverlays(android.graphics.Canvas r55) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawOverlays(android.graphics.Canvas):void");
    }

    protected void drawPhotoBlurRect(Canvas canvas, RectF rectF) {
        this.rectPath.rewind();
        this.rectPath.addRoundRect(rectF, rectF.width() / 2.0f, rectF.height() / 2.0f, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(this.rectPath);
        float alpha = this.photoImage.getAlpha();
        this.photoImage.setAlpha((this.currentMessageObject.isRoundOnce() ? 1.0f : 0.5f) * alpha);
        this.photoImage.draw(canvas);
        this.photoImage.setAlpha(alpha);
        canvas.restore();
        Paint themedPaint = getThemedPaint("paintChatTimeBackground");
        int alpha2 = themedPaint.getAlpha();
        themedPaint.setAlpha((int) (alpha2 * this.controlsAlpha * 0.4f));
        canvas.drawRoundRect(rectF, rectF.width() / 2.0f, rectF.height() / 2.0f, themedPaint);
        themedPaint.setAlpha(alpha2);
    }

    public boolean drawPhotoImage(Canvas canvas) {
        return this.photoImage.draw(canvas);
    }

    public boolean drawPinnedBottom() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages == null || !groupedMessages.isDocuments) {
            return this.pinnedBottom;
        }
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition == null || (groupedMessagePosition.flags & 8) == 0) {
            return true;
        }
        return this.pinnedBottom;
    }

    public boolean drawPinnedTop() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages == null || !groupedMessages.isDocuments) {
            return this.pinnedTop;
        }
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition == null || (groupedMessagePosition.flags & 4) == 0) {
            return true;
        }
        return this.pinnedTop;
    }

    public void drawProgressLoadingLink(Canvas canvas, int i) {
        updateProgressLoadingLink();
        ArrayList arrayList = this.progressLoadingLinkDrawables;
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        MessageObject messageObject = this.currentMessageObject;
        int themedColor = getThemedColor((messageObject == null || !messageObject.isOutOwner()) ? Theme.key_chat_linkSelectBackground : Theme.key_chat_outLinkSelectBackground);
        int i2 = 0;
        while (i2 < this.progressLoadingLinkDrawables.size()) {
            LoadingDrawableLocation loadingDrawableLocation = (LoadingDrawableLocation) this.progressLoadingLinkDrawables.get(i2);
            if (loadingDrawableLocation.blockNum == i) {
                LoadingDrawable loadingDrawable = loadingDrawableLocation.drawable;
                loadingDrawable.setColors(Theme.multAlpha(themedColor, 0.85f), Theme.multAlpha(themedColor, 2.0f), Theme.multAlpha(themedColor, 3.5f), Theme.multAlpha(themedColor, 6.0f));
                loadingDrawable.draw(canvas);
                invalidate();
                if (loadingDrawable.isDisappeared()) {
                    this.progressLoadingLinkDrawables.remove(i2);
                    i2--;
                }
            }
            i2++;
        }
    }

    public void drawRadialProgress(android.graphics.Canvas r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawRadialProgress(android.graphics.Canvas):void");
    }

    public void drawReactionsLayout(Canvas canvas, float f, Integer num) {
        if (this.isRoundVideo) {
            this.reactionsLayoutInBubble.drawServiceShaderBackground = 1.0f - getVideoTranscriptionProgress();
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || !messageObject.shouldDrawReactions()) {
            return;
        }
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        boolean z = true;
        if (groupedMessagePosition != null) {
            int i = groupedMessagePosition.flags;
            if ((i & 8) == 0 || (i & 1) == 0) {
                return;
            }
        }
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        if (reactionsLayoutInBubble.isSmall) {
            return;
        }
        if (reactionsLayoutInBubble.drawServiceShaderBackground > 0.0f) {
            applyServiceShaderMatrix();
        }
        if (getAlpha() != 1.0f) {
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, getWidth(), getHeight());
            canvas.saveLayerAlpha(rectF, (int) (getAlpha() * 255.0f), 31);
        } else {
            z = false;
        }
        ReactionsLayoutInBubble reactionsLayoutInBubble2 = this.reactionsLayoutInBubble;
        if (reactionsLayoutInBubble2.drawServiceShaderBackground > 0.0f || !this.transitionParams.animateBackgroundBoundsInner || this.currentPosition != null || this.isRoundVideo) {
            reactionsLayoutInBubble2.setScrimProgress(0.0f, false);
            ReactionsLayoutInBubble reactionsLayoutInBubble3 = this.reactionsLayoutInBubble;
            TransitionParams transitionParams = this.transitionParams;
            reactionsLayoutInBubble3.draw(canvas, transitionParams.animateChange ? transitionParams.animateChangeProgress : 1.0f, num);
        } else {
            canvas.save();
            canvas.clipRect(0.0f, 0.0f, getMeasuredWidth(), getBackgroundDrawableBottom() + this.transitionParams.deltaBottom);
            this.reactionsLayoutInBubble.setScrimProgress(0.0f, false);
            ReactionsLayoutInBubble reactionsLayoutInBubble4 = this.reactionsLayoutInBubble;
            TransitionParams transitionParams2 = this.transitionParams;
            reactionsLayoutInBubble4.draw(canvas, transitionParams2.animateChange ? transitionParams2.animateChangeProgress : 1.0f, num);
            canvas.restore();
        }
        if (z) {
            canvas.restore();
        }
    }

    public void drawReactionsLayoutOverlay(Canvas canvas, float f) {
        if (this.isRoundVideo) {
            this.reactionsLayoutInBubble.drawServiceShaderBackground = 1.0f - getVideoTranscriptionProgress();
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || !messageObject.shouldDrawReactions()) {
            return;
        }
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        boolean z = true;
        if (groupedMessagePosition != null) {
            int i = groupedMessagePosition.flags;
            if ((i & 8) == 0 || (i & 1) == 0) {
                return;
            }
        }
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        if (reactionsLayoutInBubble.isSmall) {
            return;
        }
        if (reactionsLayoutInBubble.drawServiceShaderBackground > 0.0f) {
            applyServiceShaderMatrix();
        }
        if (getAlpha() != 1.0f) {
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, getWidth(), getHeight());
            canvas.saveLayerAlpha(rectF, (int) (getAlpha() * 255.0f), 31);
        } else {
            z = false;
        }
        ReactionsLayoutInBubble reactionsLayoutInBubble2 = this.reactionsLayoutInBubble;
        if (reactionsLayoutInBubble2.drawServiceShaderBackground > 0.0f || !this.transitionParams.animateBackgroundBoundsInner || this.currentPosition != null || this.isRoundVideo) {
            TransitionParams transitionParams = this.transitionParams;
            reactionsLayoutInBubble2.drawOverlay(canvas, transitionParams.animateChange ? transitionParams.animateChangeProgress : 1.0f);
        } else {
            canvas.save();
            canvas.clipRect(0.0f, 0.0f, getMeasuredWidth(), getBackgroundDrawableBottom() + this.transitionParams.deltaBottom);
            ReactionsLayoutInBubble reactionsLayoutInBubble3 = this.reactionsLayoutInBubble;
            TransitionParams transitionParams2 = this.transitionParams;
            reactionsLayoutInBubble3.drawOverlay(canvas, transitionParams2.animateChange ? transitionParams2.animateChangeProgress : 1.0f);
            canvas.restore();
        }
        if (z) {
            canvas.restore();
        }
    }

    public void drawRoundProgress(android.graphics.Canvas r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawRoundProgress(android.graphics.Canvas):void");
    }

    public void drawScrimReaction(Canvas canvas, Integer num, float f, boolean z) {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null) {
            int i = groupedMessagePosition.flags;
            if ((i & 8) == 0 || (i & 1) == 0) {
                return;
            }
        }
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        if (reactionsLayoutInBubble.isSmall) {
            return;
        }
        reactionsLayoutInBubble.setScrimProgress(f, z);
        this.reactionsLayoutInBubble.draw(canvas, this.transitionParams.animateChangeProgress, num);
    }

    public void drawScrimReactionPreview(View view, Canvas canvas, int i, Integer num, float f) {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null) {
            int i2 = groupedMessagePosition.flags;
            if ((i2 & 8) == 0 || (i2 & 1) == 0) {
                return;
            }
        }
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        if (reactionsLayoutInBubble.isSmall) {
            return;
        }
        reactionsLayoutInBubble.setScrimProgress(f);
        this.reactionsLayoutInBubble.drawPreview(view, canvas, i, num);
    }

    public void drawServiceBackground(Canvas canvas, RectF rectF, float f, float f2) {
        applyServiceShaderMatrix();
        if (f2 != 1.0f) {
            int alpha = getThemedPaint("paintChatActionBackground").getAlpha();
            getThemedPaint("paintChatActionBackground").setAlpha((int) (alpha * f2));
            canvas.drawRoundRect(rectF, f, f, getThemedPaint("paintChatActionBackground"));
            getThemedPaint("paintChatActionBackground").setAlpha(alpha);
        } else {
            canvas.drawRoundRect(rectF, f, f, getThemedPaint(this.sideButtonPressed ? "paintChatActionBackgroundSelected" : "paintChatActionBackground"));
        }
        if (hasGradientService()) {
            if (f2 == 1.0f) {
                canvas.drawRoundRect(rectF, f, f, Theme.chat_actionBackgroundGradientDarkenPaint);
                return;
            }
            int alpha2 = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
            Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (f2 * alpha2));
            canvas.drawRoundRect(rectF, f, f, Theme.chat_actionBackgroundGradientDarkenPaint);
            Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(alpha2);
        }
    }

    public void drawTime(android.graphics.Canvas r17, float r18, boolean r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawTime(android.graphics.Canvas, float, boolean):void");
    }

    public void drawVoiceOnce(Canvas canvas, float f, Runnable runnable) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || !messageObject.isVoiceOnce()) {
            runnable.run();
            return;
        }
        float centerX = this.radialProgress.progressRect.centerX();
        double lerp = AndroidUtilities.lerp(190, 45, f) / 180.0f;
        Double.isNaN(lerp);
        float cos = centerX + (((float) Math.cos(lerp * 3.141592653589793d)) * AndroidUtilities.dp(22.6274f));
        float centerY = this.radialProgress.progressRect.centerY();
        double lerp2 = AndroidUtilities.lerp(190, 45, f) / 180.0f;
        Double.isNaN(lerp2);
        float sin = centerY + (((float) Math.sin(lerp2 * 3.141592653589793d)) * AndroidUtilities.dp(22.6274f));
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(this.radialProgress.progressRect);
        rectF.inset(-AndroidUtilities.dp(1.0f), -AndroidUtilities.dp(1.0f));
        canvas.saveLayerAlpha(rectF, 255, 31);
        this.radialProgress.setBackgroundDrawable(isDrawSelectionBackground() ? this.currentBackgroundSelectedDrawable : this.currentBackgroundDrawable);
        this.radialProgress.iconScale = f;
        runnable.run();
        if (this.onceClearPaint == null) {
            Paint paint = new Paint(1);
            this.onceClearPaint = paint;
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        }
        if (f < 1.0f) {
            canvas.save();
            float f2 = 1.0f - f;
            float f3 = 0.7f * f2;
            float centerX2 = this.radialProgress.progressRect.centerX();
            RectF rectF2 = this.radialProgress.progressRect;
            canvas.scale(f3, f3, centerX2, AndroidUtilities.lerp(rectF2.top, rectF2.bottom, 0.5f));
            if (this.onceFire == null) {
                RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.fire_once, "fire_once", AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f), true, null);
                this.onceFire = rLottieDrawable;
                rLottieDrawable.setMasterParent(this);
                this.onceFire.setAllowDecodeSingleFrame(true);
                this.onceFire.setAutoRepeat(1);
                this.onceFire.start();
                this.onceFire.scaleByCanvas = true;
            }
            RLottieDrawable rLottieDrawable2 = this.onceFire;
            RectF rectF3 = this.radialProgress.progressRect;
            rLottieDrawable2.setBounds((int) rectF3.left, (int) rectF3.top, (int) rectF3.right, (int) rectF3.bottom);
            if (this.onceRadialPaint == null) {
                this.onceRadialPaint = new Paint(1);
            }
            if (this.onceRadialCutPaint == null) {
                Paint paint2 = new Paint(1);
                this.onceRadialCutPaint = paint2;
                paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            }
            if (this.onceRadialStrokePaint == null) {
                Paint paint3 = new Paint(1);
                this.onceRadialStrokePaint = paint3;
                paint3.setStyle(Paint.Style.STROKE);
            }
            RadialProgress2 radialProgress2 = this.radialProgress;
            int i = radialProgress2.iconColorKey;
            int themedColor = i >= 0 ? getThemedColor(i) : radialProgress2.iconColor;
            this.onceRadialPaint.setColor(themedColor);
            this.onceRadialStrokePaint.setColor(themedColor);
            this.radialProgress.mediaActionDrawable.applyShaderMatrix(false);
            this.onceRadialPaint.setShader(this.radialProgress.mediaActionDrawable.paint2.getShader());
            this.onceRadialStrokePaint.setShader(this.radialProgress.mediaActionDrawable.paint2.getShader());
            rectF.set(this.onceFire.getBounds());
            canvas.saveLayerAlpha(rectF, 255, 31);
            rectF.inset(1.0f, 1.0f);
            canvas.drawRect(rectF, this.onceRadialPaint);
            this.onceFire.draw(canvas, this.onceRadialCutPaint);
            canvas.restore();
            canvas.restore();
            this.onceRadialStrokePaint.setAlpha((int) (255.0f * f2));
            this.onceRadialStrokePaint.setStrokeWidth(AndroidUtilities.dp(1.66f));
            this.rect.set(this.radialProgress.progressRect);
            this.rect.inset(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f));
            canvas.drawArc(this.rect, -90.0f, (1.0f - this.seekBarWaveform.explodeProgress) * (-360.0f), false, this.onceRadialStrokePaint);
            if (this.timerParticles == null) {
                this.timerParticles = new TimerParticles();
            }
            this.timerParticles.draw(canvas, this.onceRadialStrokePaint, this.rect, (1.0f - this.seekBarWaveform.explodeProgress) * (-360.0f), f2);
        } else {
            RLottieDrawable rLottieDrawable3 = this.onceFire;
            if (rLottieDrawable3 != null) {
                rLottieDrawable3.recycle(true);
                this.onceFire = null;
                if (this.timerParticles != null) {
                    this.timerParticles = null;
                }
            }
        }
        canvas.drawCircle(cos, sin, AndroidUtilities.dp((f * 1.5f) + 10.0f) * f, this.onceClearPaint);
        canvas.restore();
        if (this.oncePeriod == null) {
            CaptionContainerView.PeriodDrawable periodDrawable = new CaptionContainerView.PeriodDrawable(3);
            this.oncePeriod = periodDrawable;
            periodDrawable.updateColors(-1, 0, 0);
            CaptionContainerView.PeriodDrawable periodDrawable2 = this.oncePeriod;
            periodDrawable2.diameterDp = 14.0f;
            periodDrawable2.setTextSize(10.0f);
            this.oncePeriod.strokePaint.setStrokeWidth(AndroidUtilities.dpf2(1.5f));
            this.oncePeriod.setValue(1, false, false);
            this.oncePeriod.textOffsetX = -AndroidUtilities.dpf2(0.33f);
            this.oncePeriod.textOffsetY = AndroidUtilities.dpf2(0.33f);
        }
        CaptionContainerView.PeriodDrawable periodDrawable3 = this.oncePeriod;
        periodDrawable3.diameterDp = f * 14.0f;
        periodDrawable3.setTextSize(f * 10.0f);
        canvas.saveLayerAlpha(cos - AndroidUtilities.dp(10.0f), sin - AndroidUtilities.dp(10.0f), cos + AndroidUtilities.dp(10.0f), sin + AndroidUtilities.dp(10.0f), 255, 31);
        canvas.drawCircle(cos, sin, AndroidUtilities.dp(10.0f) * f, this.radialProgress.circlePaint);
        this.oncePeriod.setClear(AndroidUtilities.computePerceivedBrightness(this.radialProgress.circlePaint.getColor()) > 0.8f);
        this.oncePeriod.setCenterXY(cos, sin);
        this.oncePeriod.draw(canvas, f);
        canvas.restore();
    }

    public void forceResetMessageObject() {
        MessageObject messageObject = this.messageObjectToSet;
        if (messageObject == null) {
            messageObject = this.currentMessageObject;
        }
        this.currentMessageObject = null;
        setMessageObject(messageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
    }

    @Override
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        return new MessageAccessibilityNodeProvider();
    }

    @Override
    public float getAlpha() {
        return this.ALPHA_PROPERTY_WORKAROUND ? this.alphaInternal : super.getAlpha();
    }

    public org.telegram.ui.Components.AnimatedEmojiSpan[] getAnimatedEmojiSpans() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.getAnimatedEmojiSpans():org.telegram.ui.Components.AnimatedEmojiSpan[]");
    }

    public float getAnimationOffsetX() {
        return this.animationOffsetX;
    }

    public ImageReceiver getAvatarImage() {
        if (this.isAvatarVisible) {
            return this.avatarImage;
        }
        return null;
    }

    public MessageBackgroundDrawable getBackgroundDrawable() {
        return this.backgroundDrawable;
    }

    public int getBackgroundDrawableBottom() {
        int i;
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null) {
            int i2 = 4;
            i = (groupedMessagePosition.flags & 4) == 0 ? AndroidUtilities.dp(3.0f) : 0;
            if ((this.currentPosition.flags & 8) == 0) {
                MessageObject messageObject = this.currentMessageObject;
                if (messageObject != null && messageObject.isOutOwner()) {
                    i2 = 3;
                }
                i += AndroidUtilities.dp(i2);
            }
        } else {
            i = 0;
        }
        boolean z = this.drawPinnedBottom;
        int backgroundDrawableTop = ((getBackgroundDrawableTop() + this.layoutHeight) - ((z && this.drawPinnedTop) ? 0 : z ? AndroidUtilities.dp(1.0f) : AndroidUtilities.dp(2.0f))) + i;
        if (this.mediaBackground) {
            return backgroundDrawableTop;
        }
        if (this.drawPinnedTop) {
            backgroundDrawableTop += AndroidUtilities.dp(1.0f);
        }
        return this.drawPinnedBottom ? backgroundDrawableTop + AndroidUtilities.dp(1.0f) : backgroundDrawableTop;
    }

    public int getBackgroundDrawableLeft() {
        int dp;
        int dp2;
        int i;
        MessageObject messageObject = getMessageObject();
        if (messageObject != null && messageObject.isOutOwner()) {
            if (this.isRoundVideo) {
                return (this.layoutWidth - this.backgroundWidth) - ((int) ((1.0f - getVideoTranscriptionProgress()) * AndroidUtilities.dp(9.0f)));
            }
            return (this.layoutWidth - this.backgroundWidth) - (this.mediaBackground ? AndroidUtilities.dp(9.0f) : 0);
        }
        if (this.isRoundVideo) {
            if ((this.isChat || ((messageObject != null && (messageObject.isRepostPreview || messageObject.forceAvatar)) || messageObject.getDialogId() == 489000)) && this.isAvatarVisible) {
                r1 = 48;
            }
            dp = AndroidUtilities.dp(r1 + 3) + ((int) (AndroidUtilities.dp(6.0f) * (1.0f - getVideoTranscriptionProgress())));
        } else {
            if ((this.isChat || ((messageObject != null && (messageObject.isRepostPreview || messageObject.forceAvatar)) || messageObject.getDialogId() == 489000)) && this.isAvatarVisible) {
                r1 = 48;
            }
            dp = AndroidUtilities.dp(r1 + (this.mediaBackground ? 9 : 3));
        }
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.isDocuments && (i = this.currentPosition.leftSpanOffset) != 0) {
            dp += (int) Math.ceil((i / 1000.0f) * getGroupPhotosWidth());
        }
        if (this.isRoundVideo) {
            if (!this.drawPinnedBottom) {
                return dp;
            }
            dp2 = (int) (AndroidUtilities.dp(6.0f) * (1.0f - getVideoTranscriptionProgress()));
        } else {
            if (this.mediaBackground || !this.drawPinnedBottom) {
                return dp;
            }
            dp2 = AndroidUtilities.dp(6.0f);
        }
        return dp + dp2;
    }

    public int getBackgroundDrawableRight() {
        int dp;
        MessageObject messageObject;
        MessageObject messageObject2;
        int i = this.backgroundWidth;
        if (this.isRoundVideo) {
            dp = i - ((int) (getVideoTranscriptionProgress() * AndroidUtilities.dp(3.0f)));
            if (this.drawPinnedBottom && (messageObject2 = this.currentMessageObject) != null && messageObject2.isOutOwner()) {
                dp = (int) (dp - (AndroidUtilities.dp(6.0f) * (1.0f - getVideoTranscriptionProgress())));
            }
            if (this.drawPinnedBottom && ((messageObject = this.currentMessageObject) == null || !messageObject.isOutOwner())) {
                dp = (int) (dp - (AndroidUtilities.dp(6.0f) * (1.0f - getVideoTranscriptionProgress())));
            }
        } else {
            dp = i - (this.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
            if (!this.mediaBackground && this.drawPinnedBottom) {
                dp -= AndroidUtilities.dp(6.0f);
            }
        }
        return getBackgroundDrawableLeft() + dp;
    }

    public int getBackgroundDrawableTop() {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        int dp = ((groupedMessagePosition == null || (groupedMessagePosition.flags & 4) != 0) ? 0 : 0 - AndroidUtilities.dp(3.0f)) + (this.drawPinnedTop ? 0 : AndroidUtilities.dp(1.0f));
        return (this.mediaBackground || !this.drawPinnedTop) ? dp : dp - AndroidUtilities.dp(1.0f);
    }

    public int getBackgroundHeight() {
        return this.backgroundHeight;
    }

    public ImageReceiver getBlurredPhotoImage() {
        return this.blurredPhotoImage;
    }

    @Override
    public int getBoundsLeft() {
        int i;
        float f;
        MessageObject messageObject = this.currentMessageObject;
        boolean z = messageObject != null && messageObject.isOutOwner();
        if (needDrawAvatar()) {
            if (this.currentPosition != null) {
                f = 73.0f;
            } else {
                MessageObject messageObject2 = this.currentMessageObject;
                f = (messageObject2 == null || !messageObject2.isRepostPreview) ? 63 : 42;
            }
            i = AndroidUtilities.dp(f);
        } else {
            i = 0;
        }
        int dp = (z && (checkNeedDrawShareButton(this.currentMessageObject) || this.useTranscribeButton)) ? AndroidUtilities.dp(48.0f) : 0;
        int i2 = Integer.MAX_VALUE;
        if (this.botButtons != null) {
            int widthForButtons = getWidthForButtons();
            MessageObject messageObject3 = this.currentMessageObject;
            int dp2 = (messageObject3 == null || !messageObject3.isOutOwner()) ? this.backgroundDrawableLeft + AndroidUtilities.dp((this.mediaBackground || this.drawPinnedBottom) ? 1.0f : 7.0f) : (getMeasuredWidth() - widthForButtons) - AndroidUtilities.dp(10.0f);
            for (int i3 = 0; i3 < this.botButtons.size(); i3++) {
                i2 = Math.max(i2, ((int) (((BotButton) this.botButtons.get(i3)).x * widthForButtons)) + dp2);
            }
        }
        return Math.max(0, Math.min(i2, (getBackgroundDrawableLeft() - i) - dp));
    }

    @Override
    public int getBoundsRight() {
        int dp;
        MessageObject messageObject = this.currentMessageObject;
        int i = 0;
        int dp2 = (messageObject == null || messageObject.isOutOwner() || !(checkNeedDrawShareButton(this.currentMessageObject) || this.useTranscribeButton)) ? 0 : AndroidUtilities.dp(48.0f);
        if (this.botButtons != null) {
            int widthForButtons = getWidthForButtons();
            MessageObject messageObject2 = this.currentMessageObject;
            if (messageObject2 == null || !messageObject2.isOutOwner()) {
                dp = this.backgroundDrawableLeft + AndroidUtilities.dp((this.mediaBackground || this.drawPinnedBottom) ? 1.0f : 7.0f);
            } else {
                dp = (getMeasuredWidth() - getWidthForButtons()) - AndroidUtilities.dp(10.0f);
            }
            int i2 = 0;
            while (i < this.botButtons.size()) {
                BotButton botButton = (BotButton) this.botButtons.get(i);
                float f = widthForButtons;
                i2 = Math.max(i2, ((int) (botButton.x * f)) + dp + ((int) (botButton.width * f)));
                i++;
            }
            i = i2;
        }
        return Math.max(getBackgroundDrawableRight() + dp2, i);
    }

    public MessageObject.TextLayoutBlocks getCaptionLayout() {
        return this.captionLayout;
    }

    public float getCaptionX() {
        float f;
        float f2;
        TransitionParams transitionParams = this.transitionParams;
        if (transitionParams.animateBackgroundBoundsInner) {
            if (transitionParams.transformGroupToSingleMessage) {
                this.captionX += transitionParams.deltaLeft;
            } else {
                if (transitionParams.moveCaption) {
                    float f3 = this.captionX;
                    TransitionParams transitionParams2 = this.transitionParams;
                    float f4 = transitionParams2.animateChangeProgress;
                    f = f3 * f4;
                    f2 = transitionParams2.captionFromX * (1.0f - f4);
                } else if (!this.currentMessageObject.isVoice() || !TextUtils.isEmpty(this.currentMessageObject.caption)) {
                    f = this.captionX;
                    f2 = this.transitionParams.deltaLeft;
                }
                this.captionX = f + f2;
            }
        }
        return this.captionX;
    }

    public float getCaptionY() {
        float f = this.captionY;
        TransitionParams transitionParams = this.transitionParams;
        if (!transitionParams.animateBackgroundBoundsInner) {
            return f;
        }
        if (transitionParams.transformGroupToSingleMessage) {
            return f - getTranslationY();
        }
        if (!transitionParams.moveCaption) {
            return f;
        }
        float f2 = this.captionY;
        TransitionParams transitionParams2 = this.transitionParams;
        float f3 = transitionParams2.animateChangeProgress;
        return (f2 * f3) + (transitionParams2.captionFromY * (1.0f - f3));
    }

    public float getCheckBoxTranslation() {
        return this.checkBoxTranslation;
    }

    public int getChecksX() {
        return this.layoutWidth - AndroidUtilities.dp(SharedConfig.bubbleRadius >= 10 ? 27.3f : 25.3f);
    }

    public int getChecksY() {
        float f;
        Drawable drawable;
        if (this.currentMessageObject.shouldDrawWithoutBackground()) {
            f = this.drawTimeY;
            drawable = getThemedDrawable("drawableMsgStickerCheck");
        } else {
            f = this.drawTimeY;
            drawable = Theme.chat_msgMediaCheckDrawable;
        }
        return (int) (f - drawable.getIntrinsicHeight());
    }

    public Theme.MessageDrawable getCurrentBackgroundDrawable(boolean z) {
        if (z) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            boolean z2 = groupedMessagePosition != null && (groupedMessagePosition.flags & 8) == 0 && this.currentMessagesGroup.isDocuments && !this.drawPinnedBottom;
            this.currentBackgroundDrawable = (Theme.MessageDrawable) getThemedDrawable(this.currentMessageObject.isOutOwner() ? (this.mediaBackground || this.drawPinnedBottom || z2) ? "drawableMsgOutMedia" : "drawableMsgOut" : (this.mediaBackground || this.drawPinnedBottom || z2) ? "drawableMsgInMedia" : "drawableMsgIn");
        }
        this.currentBackgroundDrawable.getBackgroundDrawable();
        return this.currentBackgroundDrawable;
    }

    public int getCurrentBackgroundLeft() {
        Theme.MessageDrawable messageDrawable = this.currentBackgroundDrawable;
        if (messageDrawable == null) {
            return 0;
        }
        int i = messageDrawable.getBounds().left;
        if (this.currentMessageObject.isOutOwner() || this.transitionParams.changePinnedBottomProgress == 1.0f) {
            return i;
        }
        boolean z = this.isRoundVideo;
        return ((z || !this.mediaBackground) && !this.drawPinnedBottom) ? z ? (int) (i - (AndroidUtilities.dp(6.0f) * getVideoTranscriptionProgress())) : i - AndroidUtilities.dp(6.0f) : i;
    }

    public int getCurrentBackgroundRight() {
        Theme.MessageDrawable messageDrawable = this.currentBackgroundDrawable;
        if (messageDrawable == null) {
            return getWidth();
        }
        int i = messageDrawable.getBounds().right;
        if (!this.currentMessageObject.isOutOwner() || this.transitionParams.changePinnedBottomProgress == 1.0f) {
            return i;
        }
        boolean z = this.isRoundVideo;
        return ((z || !this.mediaBackground) && !this.drawPinnedBottom) ? z ? (int) (i + (AndroidUtilities.dp(6.0f) * getVideoTranscriptionProgress())) : i + AndroidUtilities.dp(6.0f) : i;
    }

    public TLRPC.Chat getCurrentChat() {
        return this.currentChat;
    }

    public MessageObject.GroupedMessages getCurrentMessagesGroup() {
        return this.currentMessagesGroup;
    }

    public MessageObject.GroupedMessagePosition getCurrentPosition() {
        return this.currentPosition;
    }

    public TLRPC.User getCurrentUser() {
        return this.currentUser;
    }

    public ChatMessageCellDelegate getDelegate() {
        return this.delegate;
    }

    public float getDescriptionLayoutX() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.getDescriptionLayoutX():float");
    }

    public float getDescriptionLayoutY() {
        float f = this.descriptionY;
        TransitionParams transitionParams = this.transitionParams;
        return f + (!transitionParams.animateLinkPreviewY ? (-transitionParams.deltaTop) + transitionParams.deltaBottom : 0.0f);
    }

    public StaticLayout getDescriptionlayout() {
        return this.descriptionLayout;
    }

    public float getDrawTopicHeight() {
        if (this.topicButton != null) {
            return r0.height();
        }
        return 0.0f;
    }

    public TLRPC.TL_availableEffect getEffect() {
        MessageObject messageObject;
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if ((groupedMessagePosition == null || groupedMessagePosition.last) && (messageObject = this.currentMessageObject) != null) {
            return messageObject.getEffect();
        }
        return null;
    }

    public int getExtraInsetHeight() {
        int i = this.addedCaptionHeight;
        if (this.hasFactCheck) {
            i += AndroidUtilities.dp((this.reactionsLayoutInBubble.isEmpty ? 18 : 0) + 2) + this.factCheckHeight;
        }
        if (this.drawCommentButton) {
            i += AndroidUtilities.dp(shouldDrawTimeOnMedia() ? 41.3f : 43.0f);
        }
        return (this.reactionsLayoutInBubble.isEmpty || !this.currentMessageObject.shouldDrawReactionsInLayout()) ? i : i + this.reactionsLayoutInBubble.totalHeight;
    }

    public int getExtraTextX() {
        float f;
        int i = SharedConfig.bubbleRadius;
        if (i >= 15) {
            f = 2.0f;
        } else {
            if (i < 11) {
                return 0;
            }
            f = 1.0f;
        }
        return AndroidUtilities.dp(f);
    }

    public StaticLayout getFactCheckLayout() {
        return this.factCheckTextLayout;
    }

    public String getFilename() {
        int i;
        int i2;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return null;
        }
        int i3 = messageObject.type;
        if (i3 == 1) {
            TLRPC.PhotoSize photoSize = this.currentPhotoObject;
            if (photoSize == null) {
                return null;
            }
            return FileLoader.getAttachFileName(photoSize);
        }
        if (i3 == 8 || (i2 = this.documentAttachType) == 7 || i2 == 4 || i2 == 8 || i3 == 9 || i2 == 3 || i2 == 5) {
            if (messageObject.useCustomPhoto) {
                return null;
            }
            if (messageObject.attachPathExists && !TextUtils.isEmpty(messageObject.messageOwner.attachPath)) {
                return this.currentMessageObject.messageOwner.attachPath;
            }
            if (!this.currentMessageObject.isSendError() || (i = this.documentAttachType) == 3 || i == 5) {
                return this.currentMessageObject.getFileName();
            }
        } else {
            if (i2 != 0) {
                return FileLoader.getAttachFileName(this.documentAttach);
            }
            TLRPC.PhotoSize photoSize2 = this.currentPhotoObject;
            if (photoSize2 != null) {
                return FileLoader.getAttachFileName(photoSize2);
            }
        }
        return null;
    }

    public int getForwardNameCenterX() {
        TLRPC.User user = this.currentUser;
        return (int) ((user == null || user.id != 0) ? this.forwardNameX + this.forwardNameCenterX : this.avatarImage.getCenterX());
    }

    public float getHighlightAlpha() {
        return getHighlightAlpha(false);
    }

    public float getHighlightAlpha(boolean z) {
        float f;
        QuoteHighlight quoteHighlight;
        if (this.drawSelectionBackground || !this.isHighlightedAnimated) {
            f = 1.0f;
        } else {
            int i = this.highlightProgress;
            f = (i >= 300 ? 1.0f : i / 300.0f) * 1.0f;
        }
        return (z || (quoteHighlight = this.quoteHighlight) == null) ? f : f * (1.0f - quoteHighlight.getT());
    }

    public int getLayoutHeight() {
        return this.layoutHeight;
    }

    public int getMaxNameWidth() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.getMaxNameWidth():int");
    }

    public int getMediaOffsetY() {
        return this.transitionParams.animateMediaOffsetY ? AndroidUtilities.lerp(this.transitionParams.animateFromMediaOffsetY, this.mediaOffsetY, this.transitionParams.animateChangeProgress) : this.mediaOffsetY;
    }

    public MessageObject getMessageObject() {
        MessageObject messageObject = this.messageObjectToSet;
        return messageObject != null ? messageObject : this.currentMessageObject;
    }

    public int getNameStatusX() {
        return (int) (this.nameX + this.nameOffsetX + (this.viaNameWidth > 0 ? r1 - AndroidUtilities.dp(32.0f) : this.nameLayoutWidth) + AndroidUtilities.dp(2.0f) + (AndroidUtilities.dp(20.0f) / 2));
    }

    public int getNameStatusY() {
        return (int) (this.nameY + ((this.nameLayout == null ? 0 : r1.getHeight()) / 2));
    }

    public int getNoSoundIconCenterX() {
        return this.noSoundCenterX;
    }

    public float getNonAnimationTranslationX(boolean z) {
        boolean z2;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.isOutOwner()) {
            return this.slidingOffsetX;
        }
        if (z && ((z2 = this.checkBoxVisible) || this.checkBoxAnimationInProgress)) {
            this.checkBoxTranslation = (int) Math.ceil((z2 ? CubicBezierInterpolator.EASE_OUT : CubicBezierInterpolator.EASE_IN).getInterpolation(this.checkBoxAnimationProgress) * AndroidUtilities.dp(35.0f));
        }
        return this.slidingOffsetX + this.checkBoxTranslation;
    }

    @Override
    public int getObserverTag() {
        return this.TAG;
    }

    public int getParentWidth() {
        int i;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            messageObject = this.messageObjectToSet;
        }
        return (messageObject == null || !messageObject.preview || (i = this.parentWidth) <= 0) ? AndroidUtilities.displaySize.x : i;
    }

    public float getPhotoBottom() {
        return this.groupMedia != null ? r0.y + r0.height : this.photoImage.getImageY2();
    }

    public ImageReceiver getPhotoImage() {
        return this.photoImage;
    }

    public ImageReceiver getPhotoImage(int i) {
        GroupMedia groupMedia = this.groupMedia;
        return groupMedia != null ? groupMedia.getPhotoImage(i) : this.photoImage;
    }

    public ArrayList<PollButton> getPollButtons() {
        return this.pollButtons;
    }

    public MessageObject getPrimaryMessageObject() {
        MessageObject messageObject = this.currentMessageObject;
        MessageObject findPrimaryMessageObject = (messageObject == null || this.currentMessagesGroup == null || !messageObject.hasValidGroupId()) ? null : this.currentMessagesGroup.findPrimaryMessageObject();
        return findPrimaryMessageObject != null ? findPrimaryMessageObject : this.currentMessageObject;
    }

    public RadialProgress2 getRadialProgress() {
        return this.radialProgress;
    }

    public ReactionsLayoutInBubble.ReactionButton getReactionButton(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        return this.reactionsLayoutInBubble.getReactionButton(visibleReaction);
    }

    public Theme.ResourcesProvider getResourcesProvider() {
        return this.resourcesProvider;
    }

    public SeekBar getSeekBar() {
        return this.seekBar;
    }

    public SeekBarWaveform getSeekBarWaveform() {
        return this.seekBarWaveform;
    }

    public float getSlidingOffsetX() {
        return this.slidingOffsetX;
    }

    public TLRPC.Document getStreamingMedia() {
        int i = this.documentAttachType;
        if (i == 4 || i == 7 || i == 2) {
            return this.documentAttach;
        }
        return null;
    }

    public int getTextX() {
        return this.textX;
    }

    public int getTextY() {
        return this.textY;
    }

    public int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    public float getTimeAlpha() {
        return this.timeAlpha;
    }

    public float getTimeX() {
        return this.transitionParams.shouldAnimateTimeX ? AndroidUtilities.lerp(r0.animateFromTimeX, this.timeX, r0.animateChangeProgress) : this.timeX;
    }

    public float getTimeY() {
        float f;
        int i;
        int i2 = 0;
        if (shouldDrawTimeOnMedia()) {
            if (this.drawCommentButton) {
                f = 41.3f;
                i2 = AndroidUtilities.dp(f);
            }
            i = -i2;
        } else if (this.currentMessageObject.isSponsored()) {
            i = -AndroidUtilities.dp(48.0f);
            if (this.hasNewLineForTime) {
                i -= AndroidUtilities.dp(4.0f);
            }
        } else {
            if (this.drawCommentButton) {
                f = 43.0f;
                i2 = AndroidUtilities.dp(f);
            }
            i = -i2;
        }
        return getTimeY(i);
    }

    public float getTimeY(float f) {
        if (shouldDrawTimeOnMedia() && this.documentAttachType != 7) {
            return ((getPhotoBottom() + this.additionalTimeOffsetY) - AndroidUtilities.dp(7.3f)) - this.timeLayout.getHeight();
        }
        float dp = ((this.layoutHeight - AndroidUtilities.dp((this.pinnedBottom || this.pinnedTop) ? 7.5f : 6.5f)) - this.timeLayout.getHeight()) + f;
        if (this.isRoundVideo) {
            dp -= (AndroidUtilities.dp(this.drawPinnedBottom ? 4.0f : 5.0f) + this.reactionsLayoutInBubble.getCurrentTotalHeight(this.transitionParams.animateChangeProgress)) * (1.0f - getVideoTranscriptionProgress());
        }
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages == null) {
            TransitionParams transitionParams = this.transitionParams;
            return dp + (transitionParams.deltaBottom - transitionParams.deltaTop);
        }
        MessageObject.GroupedMessages.TransitionParams transitionParams2 = groupedMessages.transitionParams;
        float f2 = dp + transitionParams2.offsetBottom;
        return transitionParams2.backgroundChangeBounds ? f2 - getTranslationY() : f2;
    }

    public int getTopMediaOffset() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.type != 14) {
            return 0;
        }
        return this.mediaOffsetY + this.namesOffset;
    }

    public TransitionParams getTransitionParams() {
        return this.transitionParams;
    }

    public float getVideoTranscriptionProgress() {
        MessageObject messageObject;
        if (this.transitionParams == null || (messageObject = this.currentMessageObject) == null || !messageObject.isRoundVideo()) {
            return 1.0f;
        }
        TransitionParams transitionParams = this.transitionParams;
        if (!transitionParams.animateDrawBackground) {
            return this.drawBackground ? 1.0f : 0.0f;
        }
        boolean z = this.drawBackground;
        float f = transitionParams.animateChangeProgress;
        return z ? f : 1.0f - f;
    }

    public float getViewTop() {
        return this.viewTop;
    }

    public int getWidthForButtons() {
        TransitionParams transitionParams = this.transitionParams;
        return transitionParams.animateWidthForButton ? AndroidUtilities.lerp(transitionParams.animateFromWidthForButton, this.widthForButtons, transitionParams.animateChangeProgress) : this.widthForButtons;
    }

    public boolean hasCaptionLayout() {
        return this.captionLayout != null;
    }

    public boolean hasCommentLayout() {
        return this.drawCommentButton;
    }

    public boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.hasGradientService() : Theme.hasGradientService();
    }

    public boolean hasNameLayout() {
        if (this.drawNameLayout && this.nameLayout != null) {
            return true;
        }
        if (this.drawForwardedName) {
            StaticLayout[] staticLayoutArr = this.forwardedNameLayout;
            if (staticLayoutArr[0] != null && staticLayoutArr[1] != null) {
                MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
                if (groupedMessagePosition == null) {
                    return true;
                }
                if (groupedMessagePosition.minY == 0 && groupedMessagePosition.minX == 0) {
                    return true;
                }
            }
        }
        return this.replyNameLayout != null || this.drawTopic;
    }

    public boolean hasOutboundsContent() {
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable;
        MessageObject messageObject;
        MessageObject messageObject2;
        int i;
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable2 = this.effectDrawable;
        if ((swapAnimatedEmojiDrawable2 != null && swapAnimatedEmojiDrawable2.isNotEmpty() > 0.0f) || this.hasFactCheck) {
            return true;
        }
        if (this.transitionParams.animateExpandedQuotes && ((i = (messageObject2 = this.currentMessageObject).type) == 0 || i == 24 || i == 19 || messageObject2.isGiveawayOrGiveawayResults())) {
            return true;
        }
        if (this.channelRecommendationsCell != null && (messageObject = this.currentMessageObject) != null && messageObject.type == 27) {
            return true;
        }
        if (getAlpha() != 1.0f) {
            return false;
        }
        if (this.reactionsLayoutInBubble.hasOverlay()) {
            return true;
        }
        if ((!this.transitionParams.transitionBotButtons.isEmpty() && this.transitionParams.animateBotButtonsChanged) || !this.botButtons.isEmpty() || this.drawSideButton != 0) {
            return true;
        }
        if (this.drawNameLayout && this.nameLayout != null && (swapAnimatedEmojiDrawable = this.currentNameStatusDrawable) != null && swapAnimatedEmojiDrawable.getDrawable() != null) {
            return true;
        }
        AnimatedEmojiSpan.EmojiGroupedSpans emojiGroupedSpans = this.animatedEmojiStack;
        if (emojiGroupedSpans != null && !emojiGroupedSpans.holders.isEmpty()) {
            return true;
        }
        if (this.drawTopic && this.topicButton != null) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            if (groupedMessagePosition == null) {
                return true;
            }
            if (groupedMessagePosition.minY == 0 && groupedMessagePosition.minX == 0) {
                return true;
            }
        }
        if (this.currentMessagesGroup == null) {
            TransitionParams transitionParams = this.transitionParams;
            if (((transitionParams.animateReplaceCaptionLayout && transitionParams.animateChangeProgress != 1.0f) || (transitionParams.animateChangeProgress != 1.0f && transitionParams.animateMessageText)) && transitionParams.animateOutAnimateEmoji != null && !this.transitionParams.animateOutAnimateEmoji.holders.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSpoilers() {
        ArrayList<MessageObject.TextLayoutBlock> arrayList;
        MessageObject.TextLayoutBlocks textLayoutBlocks = this.captionLayout;
        if (textLayoutBlocks != null && (arrayList = textLayoutBlocks.textLayoutBlocks) != null) {
            Iterator<MessageObject.TextLayoutBlock> it = arrayList.iterator();
            while (it.hasNext()) {
                if (!it.next().spoilers.isEmpty()) {
                    return true;
                }
            }
        }
        if (getMessageObject() == null || getMessageObject().textLayoutBlocks == null) {
            return false;
        }
        Iterator<MessageObject.TextLayoutBlock> it2 = getMessageObject().textLayoutBlocks.iterator();
        while (it2.hasNext()) {
            if (!it2.next().spoilers.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void invalidate() {
        ChatMessageCellDelegate chatMessageCellDelegate;
        if (this.currentMessageObject == null) {
            return;
        }
        Runnable runnable = this.invalidateCallback;
        if (runnable != null) {
            runnable.run();
        }
        Runnable runnable2 = this.overrideInvalidate;
        if (runnable2 != null) {
            runnable2.run();
            return;
        }
        Runnable runnable3 = this.invalidateListener;
        if (runnable3 != null) {
            runnable3.run();
        }
        super.invalidate();
        if ((this.invalidatesParent || (this.currentMessagesGroup != null && invalidateParentForce())) && getParent() != null) {
            View view = (View) getParent();
            if (view.getParent() != null) {
                view.invalidate();
                ((View) view.getParent()).invalidate();
            }
        }
        if (!this.isBlurred || (chatMessageCellDelegate = this.delegate) == null) {
            return;
        }
        chatMessageCellDelegate.invalidateBlur();
    }

    @Override
    public void invalidate(int i, int i2, int i3, int i4) {
        ChatMessageCellDelegate chatMessageCellDelegate;
        if (this.currentMessageObject == null) {
            return;
        }
        Runnable runnable = this.overrideInvalidate;
        if (runnable != null) {
            runnable.run();
            return;
        }
        Runnable runnable2 = this.invalidateListener;
        if (runnable2 != null) {
            runnable2.run();
        }
        super.invalidate(i, i2, i3, i4);
        if (this.invalidatesParent && getParent() != null) {
            ((View) getParent()).invalidate(((int) getX()) + i, ((int) getY()) + i2, ((int) getX()) + i3, ((int) getY()) + i4);
        }
        if (!this.isBlurred || (chatMessageCellDelegate = this.delegate) == null) {
            return;
        }
        chatMessageCellDelegate.invalidateBlur();
    }

    @Override
    public void invalidateDrawable(Drawable drawable) {
        super.invalidateDrawable(drawable);
        if (this.currentMessagesGroup != null) {
            invalidateWithParent();
        }
    }

    @Override
    public void invalidateLite() {
        ChatMessageCellDelegate chatMessageCellDelegate;
        if (this.currentMessageObject == null) {
            return;
        }
        Runnable runnable = this.overrideInvalidate;
        if (runnable != null) {
            runnable.run();
            return;
        }
        Runnable runnable2 = this.invalidateListener;
        if (runnable2 != null) {
            runnable2.run();
        }
        super.invalidate();
        if ((this.invalidatesParent || (this.currentMessagesGroup != null && invalidateParentForce())) && getParent() != null) {
            View view = (View) getParent();
            if (view.getParent() != null) {
                view.invalidate();
                ((View) view.getParent()).invalidate();
            }
        }
        if (!this.isBlurred || (chatMessageCellDelegate = this.delegate) == null) {
            return;
        }
        chatMessageCellDelegate.invalidateBlur();
    }

    public void invalidateOutbounds() {
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        if (chatMessageCellDelegate != null && chatMessageCellDelegate.canDrawOutboundsContent()) {
            super.invalidate();
        } else if (getParent() instanceof View) {
            ((View) getParent()).invalidate();
        }
    }

    public boolean isAdminLayoutChanged() {
        return !TextUtils.equals(this.lastPostAuthor, this.currentMessageObject.messageOwner.post_author);
    }

    public boolean isAnimatingPollAnswer() {
        return this.animatePollAnswerAlpha;
    }

    public boolean isCellAttachedToWindow() {
        return this.attachedToWindow;
    }

    public boolean isCheckBoxVisible() {
        return this.checkBoxVisible || this.checkBoxAnimationInProgress;
    }

    public boolean isDrawNameLayout() {
        return this.drawNameLayout && this.nameLayout != null;
    }

    public boolean isDrawPinnedBottom() {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        return this.mediaBackground || this.drawPinnedBottom || (groupedMessagePosition != null && (groupedMessagePosition.flags & 8) == 0 && this.currentMessagesGroup.isDocuments);
    }

    public boolean isDrawTopic() {
        return this.drawTopic;
    }

    public boolean isDrawingSelectionBackground() {
        return this.drawSelectionBackground || this.isHighlightedAnimated || this.isHighlighted;
    }

    public boolean isHighlighted() {
        return this.isHighlighted;
    }

    public boolean isHighlightedAnimated() {
        return this.isHighlightedAnimated;
    }

    public boolean isInsideBackground(float f, float f2) {
        if (this.currentBackgroundDrawable != null) {
            if (f >= this.backgroundDrawableLeft && f <= r3 + this.backgroundDrawableRight) {
                return true;
            }
        }
        return false;
    }

    protected boolean isNeedAuthorName() {
        TLRPC.Message message;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject.forceAvatar) {
            return true;
        }
        if (messageObject.getDialogId() == 489000 && (message = this.currentMessageObject.messageOwner) != null && message.fwd_from != null) {
            return true;
        }
        if (this.currentMessageObject.isSponsored() || this.currentMessageObject.isGiveawayOrGiveawayResults()) {
            return false;
        }
        if (this.isPinnedChat && this.currentMessageObject.type == 0) {
            return true;
        }
        if (!this.pinnedTop && this.drawName && this.isChat) {
            if (!this.currentMessageObject.isOutOwner()) {
                return true;
            }
            if ((this.currentMessageObject.isSupergroup() && this.currentMessageObject.isFromGroup()) || this.currentMessageObject.isRepostPreview) {
                return true;
            }
        }
        return this.currentMessageObject.isImportedForward() && this.currentMessageObject.messageOwner.fwd_from.from_id == null;
    }

    public boolean isPinnedBottom() {
        return this.messageObjectToSet != null ? this.bottomNearToSet : this.pinnedBottom;
    }

    public boolean isPinnedTop() {
        return this.messageObjectToSet != null ? this.topNearToSet : this.pinnedTop;
    }

    public boolean isPlayingRound() {
        return this.isRoundVideo && this.isPlayingRound;
    }

    @Override
    public boolean isSeekBarDragAllowed() {
        MessageObject messageObject = this.currentMessageObject;
        return messageObject == null || !messageObject.isVoiceOnce();
    }

    protected boolean isWidthAdaptive() {
        return false;
    }

    public void layoutTextXY(boolean r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.layoutTextXY(boolean):void");
    }

    protected SpoilerEffect2 makeSpoilerEffect() {
        return SpoilerEffect2.getInstance(this);
    }

    public void markReactionsAsRead() {
        this.reactionsLayoutInBubble.hasUnreadReactions = false;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.markReactionsAsRead();
    }

    public boolean needDelayRoundProgressDraw() {
        MessageObject messageObject;
        int i = this.documentAttachType;
        return (i == 7 || i == 4) && (messageObject = this.currentMessageObject) != null && messageObject.type != 5 && MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
    }

    public boolean needDrawAvatar() {
        MessageObject messageObject;
        MessageObject messageObject2;
        MessageObject messageObject3;
        return (this.isChat && !this.isSavedPreviewChat && ((!this.isThreadPost || this.isForum) && (messageObject3 = this.currentMessageObject) != null && !messageObject3.isOutOwner() && this.currentMessageObject.needDrawAvatar())) || ((messageObject = this.currentMessageObject) != null && messageObject.getDialogId() == 489000) || ((messageObject2 = this.currentMessageObject) != null && messageObject2.forceAvatar);
    }

    public boolean needDrawTime() {
        MessageObject messageObject;
        return !this.forceNotDrawTime && ((messageObject = this.currentMessageObject) == null || messageObject.type != 27);
    }

    @Override
    public void onAnimationReady(ImageReceiver imageReceiver) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && imageReceiver == this.photoImage && messageObject.isAnimatedSticker()) {
            this.delegate.setShouldNotRepeatSticker(this.currentMessageObject);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.startSpoilers);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.stopSpoilers);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didUpdatePremiumGiftStickers);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoad);
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            messageObject.animateComments = false;
        }
        MessageObject messageObject2 = this.messageObjectToSet;
        if (messageObject2 != null) {
            messageObject2.animateComments = false;
            setMessageContent(messageObject2, this.groupedMessagesToSet, this.bottomNearToSet, this.topNearToSet);
            this.messageObjectToSet = null;
            this.groupedMessagesToSet = null;
        }
        CheckBoxBase checkBoxBase = this.checkBox;
        if (checkBoxBase != null) {
            checkBoxBase.onAttachedToWindow();
        }
        CheckBoxBase checkBoxBase2 = this.mediaCheckBox;
        if (checkBoxBase2 != null) {
            checkBoxBase2.onAttachedToWindow();
        }
        if (this.pollCheckBox != null) {
            int i = 0;
            while (true) {
                CheckBoxBase[] checkBoxBaseArr = this.pollCheckBox;
                if (i >= checkBoxBaseArr.length) {
                    break;
                }
                checkBoxBaseArr[i].onAttachedToWindow();
                i++;
            }
        }
        this.attachedToWindow = true;
        this.animationOffsetX = 0.0f;
        this.slidingOffsetX = 0.0f;
        this.checkBoxTranslation = 0;
        updateTranslation();
        this.avatarImage.setParentView((View) getParent());
        this.avatarImage.onAttachedToWindow();
        checkImageReceiversAttachState();
        MessageObject messageObject3 = this.currentMessageObject;
        if (messageObject3 != null) {
            setAvatar(messageObject3);
        }
        int i2 = this.documentAttachType;
        if (i2 == 4 && this.autoPlayingMedia) {
            boolean isPlayingMessage = MediaController.getInstance().isPlayingMessage(this.currentMessageObject);
            this.animatingNoSoundPlaying = isPlayingMessage;
            this.animatingNoSoundProgress = isPlayingMessage ? 0.0f : 1.0f;
            this.animatingNoSound = 0;
        } else {
            this.animatingNoSoundPlaying = false;
            this.animatingNoSoundProgress = 0.0f;
            this.animatingDrawVideoImageButtonProgress = ((i2 == 4 || i2 == 2) && this.drawVideoSize) ? 1.0f : 0.0f;
        }
        if (getDelegate() != null && getDelegate().getTextSelectionHelper() != null) {
            getDelegate().getTextSelectionHelper().onChatMessageCellAttached(this);
        }
        if (this.documentAttachType == 5) {
            this.toSeekBarProgress = MediaController.getInstance().isPlayingMessage(this.currentMessageObject) ? 1.0f : 0.0f;
        }
        this.reactionsLayoutInBubble.onAttachToWindow();
        FlagSecureReason flagSecureReason = this.flagSecure;
        if (flagSecureReason != null) {
            flagSecureReason.attach();
        }
        GroupMedia groupMedia = this.groupMedia;
        if (groupMedia != null) {
            groupMedia.onAttachedToWindow();
        }
        updateFlagSecure();
        MessageObject messageObject4 = this.currentMessageObject;
        if (messageObject4 != null && messageObject4.type == 20 && this.unlockLayout != null) {
            invalidate();
        }
        MessageTopicButton messageTopicButton = this.topicButton;
        if (messageTopicButton != null) {
            messageTopicButton.onAttached(this);
        }
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.currentNameStatusDrawable;
        if (swapAnimatedEmojiDrawable != null) {
            swapAnimatedEmojiDrawable.attach();
        }
        SpoilerEffect2 spoilerEffect2 = this.mediaSpoilerEffect2;
        if (spoilerEffect2 != null) {
            if (spoilerEffect2.destroyed) {
                SpoilerEffect2 makeSpoilerEffect = makeSpoilerEffect();
                this.mediaSpoilerEffect2 = makeSpoilerEffect;
                Integer num = this.mediaSpoilerEffect2Index;
                if (num != null) {
                    makeSpoilerEffect.reassignAttach(this, num.intValue());
                }
            } else {
                spoilerEffect2.attach(this);
            }
        }
        ChannelRecommendationsCell channelRecommendationsCell = this.channelRecommendationsCell;
        if (channelRecommendationsCell != null) {
            channelRecommendationsCell.onAttachedToWindow();
        }
        StickerSetLinkIcon stickerSetLinkIcon = this.stickerSetIcons;
        if (stickerSetLinkIcon != null) {
            stickerSetLinkIcon.attach(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.startSpoilers);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.stopSpoilers);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didUpdatePremiumGiftStickers);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoad);
        cancelShakeAnimation();
        CheckBoxBase checkBoxBase = this.checkBox;
        if (checkBoxBase != null) {
            checkBoxBase.onDetachedFromWindow();
        }
        CheckBoxBase checkBoxBase2 = this.mediaCheckBox;
        if (checkBoxBase2 != null) {
            checkBoxBase2.onDetachedFromWindow();
        }
        if (this.pollCheckBox != null) {
            int i = 0;
            while (true) {
                CheckBoxBase[] checkBoxBaseArr = this.pollCheckBox;
                if (i >= checkBoxBaseArr.length) {
                    break;
                }
                checkBoxBaseArr[i].onDetachedFromWindow();
                i++;
            }
        }
        this.attachedToWindow = false;
        GroupMedia groupMedia = this.groupMedia;
        if (groupMedia != null) {
            groupMedia.onDetachedFromWindow();
        }
        this.avatarImage.onDetachedFromWindow();
        checkImageReceiversAttachState();
        if (this.addedForTest && this.currentUrl != null && this.currentWebFile != null) {
            ImageLoader.getInstance().removeTestWebFile(this.currentUrl);
            this.addedForTest = false;
        }
        StickerSetLinkIcon stickerSetLinkIcon = this.stickerSetIcons;
        if (stickerSetLinkIcon != null) {
            stickerSetLinkIcon.detach(this);
        }
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        if (getDelegate() != null && getDelegate().getTextSelectionHelper() != null) {
            getDelegate().getTextSelectionHelper().onChatMessageCellDetached(this);
        }
        this.transitionParams.onDetach();
        if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
            Theme.getCurrentAudiVisualizerDrawable().setParentView(null);
        }
        ValueAnimator valueAnimator = this.statusDrawableAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.statusDrawableAnimator.cancel();
        }
        this.reactionsLayoutInBubble.onDetachFromWindow();
        this.statusDrawableAnimationInProgress = false;
        FlagSecureReason flagSecureReason = this.flagSecure;
        if (flagSecureReason != null) {
            flagSecureReason.detach();
        }
        MessageTopicButton messageTopicButton = this.topicButton;
        if (messageTopicButton != null) {
            messageTopicButton.onDetached(this);
        }
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.currentNameStatusDrawable;
        if (swapAnimatedEmojiDrawable != null) {
            swapAnimatedEmojiDrawable.detach();
        }
        SpoilerEffect2 spoilerEffect2 = this.mediaSpoilerEffect2;
        if (spoilerEffect2 != null) {
            spoilerEffect2.detach(this);
        }
        ChannelRecommendationsCell channelRecommendationsCell = this.channelRecommendationsCell;
        if (channelRecommendationsCell != null) {
            channelRecommendationsCell.onDetachedFromWindow();
        }
    }

    @Override
    public void onDraw(android.graphics.Canvas r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onDraw(android.graphics.Canvas):void");
    }

    @Override
    public void onFailedDownload(String str, boolean z) {
        int i = this.documentAttachType;
        updateButtonState(true, i == 3 || i == 5, false);
    }

    @Override
    public boolean onHoverEvent(MotionEvent motionEvent) {
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (motionEvent.getAction() == 9 || motionEvent.getAction() == 7) {
            for (int i = 0; i < this.accessibilityVirtualViewBounds.size(); i++) {
                if (((Rect) this.accessibilityVirtualViewBounds.valueAt(i)).contains(x, y)) {
                    int keyAt = this.accessibilityVirtualViewBounds.keyAt(i);
                    if (keyAt == this.currentFocusedVirtualView) {
                        return true;
                    }
                    this.currentFocusedVirtualView = keyAt;
                    sendAccessibilityEventForVirtualView(keyAt, 32768);
                    return true;
                }
            }
        } else if (motionEvent.getAction() == 10) {
            this.currentFocusedVirtualView = 0;
        }
        return super.onHoverEvent(motionEvent);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
    }

    @Override
    public void onLayout(boolean r24, int r25, int r26, int r27, int r28) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onLayout(boolean, int, int, int, int):void");
    }

    @Override
    protected boolean onLongPress() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onLongPress():boolean");
    }

    @Override
    protected void onMeasure(int i, int i2) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && (messageObject.checkLayout() || this.lastHeight != AndroidUtilities.displaySize.y)) {
            this.inLayout = true;
            MessageObject messageObject2 = this.currentMessageObject;
            this.currentMessageObject = null;
            setMessageObject(messageObject2, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop);
            this.inLayout = false;
        }
        updateSelectionTextPosition();
        setMeasuredDimension(isWidthAdaptive() ? getBoundsRight() - getBoundsLeft() : View.MeasureSpec.getSize(i), this.totalHeight + this.keyboardHeight);
    }

    @Override
    public void onProgressDownload(String str, long j, long j2) {
        float min = j2 == 0 ? 0.0f : Math.min(1.0f, ((float) j) / ((float) j2));
        this.currentMessageObject.loadedFileSize = j;
        createLoadingProgressLayout(j, j2);
        (this.drawVideoImageButton ? this.videoRadialProgress : this.radialProgress).setProgress(min, true);
        int i = this.documentAttachType;
        if (i == 3 || i == 5) {
            if (this.hasMiniProgress != 0) {
                if (this.miniButtonState == 1) {
                    return;
                }
            } else if (this.buttonState == 4) {
                return;
            }
        } else if (this.hasMiniProgress != 0) {
            if (this.miniButtonState == 1) {
                return;
            }
        } else if (this.buttonState == 1) {
            return;
        }
        updateButtonState(false, false, false);
    }

    @Override
    public void onProgressUpload(String str, long j, long j2, boolean z) {
        int i;
        float min = j2 == 0 ? 0.0f : Math.min(1.0f, ((float) j) / ((float) j2));
        this.currentMessageObject.loadedFileSize = j;
        this.radialProgress.setProgress(min, true);
        if (j == j2 && this.currentPosition != null && SendMessagesHelper.getInstance(this.currentAccount).isSendingMessage(this.currentMessageObject.getId()) && ((i = this.buttonState) == 1 || (i == 4 && this.documentAttachType == 5))) {
            this.drawRadialCheckBackground = true;
            getIconForCurrentState();
            this.radialProgress.setIcon(6, false, true);
        }
        long j3 = this.lastLoadingSizeTotal;
        if (j3 > 0 && Math.abs(j3 - j2) > 1048576) {
            this.lastLoadingSizeTotal = j2;
        }
        createLoadingProgressLayout(j, j2);
    }

    @Override
    public void onProvideStructure(ViewStructure viewStructure) {
        CharSequence charSequence;
        CharSequence charSequence2;
        CharSequence charSequence3;
        super.onProvideStructure(viewStructure);
        if (!this.allowAssistant || Build.VERSION.SDK_INT < 23) {
            return;
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || (charSequence3 = messageObject.messageText) == null || charSequence3.length() <= 0) {
            MessageObject messageObject2 = this.currentMessageObject;
            if (messageObject2 == null || (charSequence = messageObject2.caption) == null || charSequence.length() <= 0) {
                return;
            } else {
                charSequence2 = this.currentMessageObject.caption;
            }
        } else {
            charSequence2 = this.currentMessageObject.messageText;
        }
        viewStructure.setText(charSequence2);
    }

    @Override
    public void onSeekBarContinuousDrag(float f) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.audioProgress = f;
        double duration = messageObject.getDuration();
        double d = f;
        Double.isNaN(d);
        messageObject.audioProgressSec = (int) (duration * d);
        updatePlayingMessageProgress();
    }

    @Override
    public void onSeekBarDrag(float f) {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.audioProgress = f;
        MediaController.getInstance().seekToProgress(this.currentMessageObject, f);
        updatePlayingMessageProgress();
    }

    @Override
    public void onSeekBarPressed() {
        requestDisallowInterceptTouchEvent(true);
    }

    @Override
    public void onSeekBarReleased() {
        requestDisallowInterceptTouchEvent(false);
    }

    @Override
    public void onSuccessDownload(java.lang.String r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onSuccessDownload(java.lang.String):void");
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void overrideAudioVisualizer(AudioVisualizerDrawable audioVisualizerDrawable) {
        this.overridenAudioVisualizer = audioVisualizerDrawable;
    }

    public void overrideDuration(long j) {
        this.overridenDuration = j;
    }

    @Override
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        ChatMessageCellDelegate chatMessageCellDelegate;
        ChatMessageCellDelegate chatMessageCellDelegate2 = this.delegate;
        if (chatMessageCellDelegate2 != null && chatMessageCellDelegate2.onAccessibilityAction(i, bundle)) {
            return false;
        }
        if (i == 16) {
            int iconForCurrentState = getIconForCurrentState();
            if (iconForCurrentState != 4 && iconForCurrentState != 5) {
                didPressButton(true, false);
            } else if (this.currentMessageObject.type == 16) {
                this.delegate.didPressOther(this, this.otherX, this.otherY);
            } else {
                didClickedImage();
            }
            return true;
        }
        if (i == R.id.acc_action_small_button) {
            didPressMiniButton(true);
        } else if (i == R.id.acc_action_msg_options) {
            ChatMessageCellDelegate chatMessageCellDelegate3 = this.delegate;
            if (chatMessageCellDelegate3 != null) {
                if (this.currentMessageObject.type == 16) {
                    chatMessageCellDelegate3.didLongPress(this, 0.0f, 0.0f);
                } else {
                    chatMessageCellDelegate3.didPressOther(this, this.otherX, this.otherY);
                }
            }
        } else if (i == R.id.acc_action_open_forwarded_origin && (chatMessageCellDelegate = this.delegate) != null) {
            TLRPC.Chat chat = this.currentForwardChannel;
            if (chat != null) {
                chatMessageCellDelegate.didPressChannelAvatar(this, chat, this.currentMessageObject.messageOwner.fwd_from.channel_post, this.lastTouchX, this.lastTouchY, false);
            } else {
                TLRPC.User user = this.currentForwardUser;
                if (user != null) {
                    chatMessageCellDelegate.didPressUserAvatar(this, user, this.lastTouchX, this.lastTouchY, false);
                } else if (this.currentForwardName != null) {
                    chatMessageCellDelegate.didPressHiddenForward(this);
                }
            }
        }
        if ((this.currentMessageObject.isVoice() || this.currentMessageObject.isRoundVideo() || (this.currentMessageObject.isMusic() && MediaController.getInstance().isPlayingMessage(this.currentMessageObject))) && this.seekBarAccessibilityDelegate.performAccessibilityActionInternal(i, bundle)) {
            return true;
        }
        return super.performAccessibilityAction(i, bundle);
    }

    @Override
    public void requestLayout() {
        if (this.inLayout) {
            return;
        }
        super.requestLayout();
    }

    public void resetPressedLink(int i) {
        LinkSpanDrawable.LinkCollector linkCollector = this.links;
        if (i != -1) {
            linkCollector.removeLinks(Integer.valueOf(i));
        } else {
            linkCollector.clear();
        }
        LinkSpanDrawable.LinkCollector linkCollector2 = this.factCheckLinks;
        if (linkCollector2 != null) {
            linkCollector2.clear();
        }
        this.pressedEmoji = null;
        this.pressedFactCheckLink = null;
        if (this.pressedLink != null) {
            if (this.pressedLinkType == i || i == -1) {
                this.pressedLink = null;
                this.pressedLinkType = -1;
                invalidate();
            }
        }
    }

    @Override
    public boolean reverseWaveform() {
        MessageObject messageObject = this.currentMessageObject;
        return messageObject != null && messageObject.isVoiceOnce();
    }

    public void setAllowAssistant(boolean z) {
        this.allowAssistant = z;
    }

    @Override
    public void setAlpha(float f) {
        if ((f == 1.0f) != (getAlpha() == 1.0f)) {
            invalidate();
        }
        if (this.ALPHA_PROPERTY_WORKAROUND) {
            this.alphaInternal = f;
            invalidate();
        } else {
            super.setAlpha(f);
        }
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if ((groupedMessagePosition != null && (groupedMessagePosition.minY != 0 || groupedMessagePosition.minX != 0)) || ((this.enterTransitionInProgress && !this.currentMessageObject.isVoice()) || this.replyNameLayout == null || this.replyTextLayout == null)) {
            MessageObject.GroupedMessagePosition groupedMessagePosition2 = this.currentPosition;
            if (groupedMessagePosition2 != null) {
                int i = groupedMessagePosition2.flags;
                if ((i & 8) == 0 || (i & 1) == 0) {
                    return;
                }
            }
            if (this.reactionsLayoutInBubble.isSmall) {
                return;
            }
        }
        invalidate();
    }

    public void setAnimationOffsetX(float f) {
        if (this.animationOffsetX != f) {
            this.animationOffsetX = f;
            updateTranslation();
        }
    }

    public void setAnimationRunning(boolean z, boolean z2) {
        this.animationRunning = z;
        if (z) {
            this.willRemoved = z2;
        } else {
            this.willRemoved = false;
        }
    }

    public void setAvatar(MessageObject messageObject) {
        TLRPC.Chat chat;
        if (messageObject == null) {
            return;
        }
        if (!this.isAvatarVisible) {
            this.currentPhoto = null;
            return;
        }
        Drawable drawable = messageObject.customAvatarDrawable;
        if (drawable != null) {
            this.avatarImage.setImageBitmap(drawable);
            return;
        }
        TLRPC.User user = this.currentUser;
        if (user != null) {
            TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
            if (userProfilePhoto != null) {
                this.currentPhoto = userProfilePhoto.photo_small;
            } else {
                this.currentPhoto = null;
            }
            this.avatarDrawable.setInfo(this.currentAccount, user);
            this.avatarImage.setForUserOrChat(this.currentUser, this.avatarDrawable, null, LiteMode.isEnabled(98784), 1, false);
            return;
        }
        TLRPC.Chat chat2 = this.currentChat;
        if (chat2 == null) {
            if (!messageObject.isSponsored()) {
                this.currentPhoto = null;
                this.avatarDrawable.setInfo(messageObject.getFromChatId(), null, null);
                this.avatarImage.setImage(null, null, this.avatarDrawable, null, null, 0);
                return;
            } else {
                TLRPC.Photo photo = messageObject.sponsoredPhoto;
                if (photo != null) {
                    this.avatarImage.setImage(ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.dp(50.0f), false, null, true), messageObject.sponsoredPhoto), "50_50", this.avatarDrawable, null, null, 0);
                    return;
                }
                return;
            }
        }
        TLRPC.ChatPhoto chatPhoto = chat2.photo;
        this.currentPhoto = chatPhoto != null ? chatPhoto.photo_small : null;
        if (!chat2.signature_profiles || messageObject.getDialogId() == 1271266957) {
            this.avatarDrawable.setInfo(this.currentAccount, this.currentChat);
            this.avatarImage.setForUserOrChat(this.currentChat, this.avatarDrawable);
            return;
        }
        long peerDialogId = DialogObject.getPeerDialogId(messageObject.messageOwner.from_id);
        MessagesController messagesController = MessagesController.getInstance(messageObject.currentAccount);
        if (peerDialogId >= 0) {
            TLRPC.User user2 = messagesController.getUser(Long.valueOf(peerDialogId));
            this.avatarDrawable.setInfo(this.currentAccount, user2);
            chat = user2;
        } else {
            TLRPC.Chat chat3 = messagesController.getChat(Long.valueOf(-peerDialogId));
            this.avatarDrawable.setInfo(this.currentAccount, chat3);
            chat = chat3;
        }
        this.avatarImage.setForUserOrChat(chat, this.avatarDrawable);
    }

    public void setBackgroundTopY(int i) {
        int i2;
        int i3;
        Theme.MessageDrawable messageDrawable = this.currentBackgroundDrawable;
        int i4 = this.parentWidth;
        int i5 = this.parentHeight;
        if (i5 == 0) {
            i4 = getParentWidth();
            i5 = AndroidUtilities.displaySize.y;
            if (getParent() instanceof View) {
                View view = (View) getParent();
                int measuredWidth = view.getMeasuredWidth();
                i3 = view.getMeasuredHeight();
                i2 = measuredWidth;
                float f = this.parentViewTopOffset;
                messageDrawable.setTop((int) (i + f), i2, i3, (int) f, this.blurredViewTopOffset, this.blurredViewBottomOffset, this.pinnedTop, (this.pinnedBottom && this.transitionParams.changePinnedBottomProgress == 1.0f) ? false : true);
                MessageObject messageObject = this.currentMessageObject;
                messageDrawable.setBotButtonsBottom(messageObject == null && messageObject.hasInlineBotButtons());
            }
        }
        i2 = i4;
        i3 = i5;
        float f2 = this.parentViewTopOffset;
        messageDrawable.setTop((int) (i + f2), i2, i3, (int) f2, this.blurredViewTopOffset, this.blurredViewBottomOffset, this.pinnedTop, (this.pinnedBottom && this.transitionParams.changePinnedBottomProgress == 1.0f) ? false : true);
        MessageObject messageObject2 = this.currentMessageObject;
        messageDrawable.setBotButtonsBottom(messageObject2 == null && messageObject2.hasInlineBotButtons());
    }

    public void setBackgroundTopY(boolean r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setBackgroundTopY(boolean):void");
    }

    public void setCheckBoxVisible(boolean z, boolean z2) {
        MessageObject.GroupedMessages groupedMessages;
        MessageObject.GroupedMessages groupedMessages2;
        MessageObject messageObject;
        if (z2 && (messageObject = this.currentMessageObject) != null && messageObject.deletedByThanos) {
            return;
        }
        if (z) {
            this.quoteHighlight = null;
            CheckBoxBase checkBoxBase = this.checkBox;
            if (checkBoxBase == null) {
                CheckBoxBase checkBoxBase2 = new CheckBoxBase(this, 21, this.resourcesProvider);
                this.checkBox = checkBoxBase2;
                if (this.attachedToWindow) {
                    checkBoxBase2.onAttachedToWindow();
                }
            } else {
                checkBoxBase.setResourcesProvider(this.resourcesProvider);
            }
        }
        if (z && (((groupedMessages = this.currentMessagesGroup) != null && groupedMessages.messages.size() > 1) || ((groupedMessages2 = this.groupedMessagesToSet) != null && groupedMessages2.messages.size() > 1))) {
            CheckBoxBase checkBoxBase3 = this.mediaCheckBox;
            if (checkBoxBase3 == null) {
                CheckBoxBase checkBoxBase4 = new CheckBoxBase(this, 21, this.resourcesProvider);
                this.mediaCheckBox = checkBoxBase4;
                checkBoxBase4.setUseDefaultCheck(true);
                if (this.attachedToWindow) {
                    this.mediaCheckBox.onAttachedToWindow();
                }
            } else {
                checkBoxBase3.setResourcesProvider(this.resourcesProvider);
            }
        }
        if (this.checkBoxVisible == z) {
            if (z2 == this.checkBoxAnimationInProgress || z2) {
                return;
            }
            this.checkBoxAnimationProgress = z ? 1.0f : 0.0f;
            invalidate();
            return;
        }
        this.checkBoxAnimationInProgress = z2;
        this.checkBoxVisible = z;
        if (z2) {
            this.lastCheckBoxAnimationTime = SystemClock.elapsedRealtime();
        } else {
            this.checkBoxAnimationProgress = z ? 1.0f : 0.0f;
        }
        invalidate();
    }

    public void setCheckPressed(boolean z, boolean z2) {
        this.isCheckPressed = z;
        this.isPressed = z2;
        updateRadialProgressBackground();
        if (this.useSeekBarWaveform) {
            this.seekBarWaveform.setSelected(isDrawSelectionBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectionBackground());
        }
        invalidate();
    }

    public void setChecked(boolean z, boolean z2, boolean z3) {
        MessageObject messageObject;
        if (z || !z3 || (messageObject = this.currentMessageObject) == null || !messageObject.deletedByThanos) {
            CheckBoxBase checkBoxBase = this.checkBox;
            if (checkBoxBase != null) {
                checkBoxBase.setChecked(z2, z3);
            }
            CheckBoxBase checkBoxBase2 = this.mediaCheckBox;
            if (checkBoxBase2 != null) {
                checkBoxBase2.setChecked(z, z3);
            }
            this.backgroundDrawable.setSelected(z2, z3);
        }
    }

    public boolean setCurrentDiceValue(boolean z) {
        MessagesController.DiceFrameSuccess diceFrameSuccess;
        if (!this.currentMessageObject.isDice()) {
            return false;
        }
        Drawable drawable = this.photoImage.getDrawable();
        if (drawable instanceof RLottieDrawable) {
            RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
            String diceEmoji = this.currentMessageObject.getDiceEmoji();
            TLRPC.TL_messages_stickerSet stickerSetByEmojiOrName = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName(diceEmoji);
            if (stickerSetByEmojiOrName != null) {
                int diceValue = this.currentMessageObject.getDiceValue();
                if ("🎰".equals(this.currentMessageObject.getDiceEmoji())) {
                    if (diceValue >= 0 && diceValue <= 64) {
                        ((SlotsDrawable) rLottieDrawable).setDiceNumber(this, diceValue, stickerSetByEmojiOrName, z);
                        if (this.currentMessageObject.isOut()) {
                            rLottieDrawable.setOnFinishCallback(this.diceFinishCallback, Integer.MAX_VALUE);
                        }
                        this.currentMessageObject.wasUnread = false;
                    }
                    if (!rLottieDrawable.hasBaseDice() && stickerSetByEmojiOrName.documents.size() > 0) {
                        ((SlotsDrawable) rLottieDrawable).setBaseDice(this, stickerSetByEmojiOrName);
                    }
                } else {
                    if (!rLottieDrawable.hasBaseDice() && stickerSetByEmojiOrName.documents.size() > 0) {
                        TLRPC.Document document = stickerSetByEmojiOrName.documents.get(0);
                        if (rLottieDrawable.setBaseDice(FileLoader.getInstance(this.currentAccount).getPathToAttach(document, true))) {
                            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                        } else {
                            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(FileLoader.getAttachFileName(document), this.currentMessageObject, this);
                            FileLoader.getInstance(this.currentAccount).loadFile(document, stickerSetByEmojiOrName, 1, 1);
                        }
                    }
                    if (diceValue >= 0 && diceValue < stickerSetByEmojiOrName.documents.size()) {
                        if (!z && this.currentMessageObject.isOut() && (diceFrameSuccess = MessagesController.getInstance(this.currentAccount).diceSuccess.get(diceEmoji)) != null && diceFrameSuccess.num == diceValue) {
                            rLottieDrawable.setOnFinishCallback(this.diceFinishCallback, diceFrameSuccess.frame);
                        }
                        TLRPC.Document document2 = stickerSetByEmojiOrName.documents.get(Math.max(diceValue, 0));
                        if (rLottieDrawable.setDiceNumber(FileLoader.getInstance(this.currentAccount).getPathToAttach(document2, true), z)) {
                            DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
                        } else {
                            DownloadController.getInstance(this.currentAccount).addLoadingFileObserver(FileLoader.getAttachFileName(document2), this.currentMessageObject, this);
                            FileLoader.getInstance(this.currentAccount).loadFile(document2, stickerSetByEmojiOrName, 1, 1);
                        }
                        this.currentMessageObject.wasUnread = false;
                    }
                }
            } else {
                MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName(diceEmoji, true, true);
            }
        }
        return true;
    }

    public void setDelegate(ChatMessageCellDelegate chatMessageCellDelegate) {
        this.delegate = chatMessageCellDelegate;
    }

    public void setDrawSelectionBackground(boolean z) {
        if (this.drawSelectionBackground != z) {
            this.drawSelectionBackground = z;
            invalidate();
        }
    }

    public void setDrawableBoundsInner(Drawable drawable, int i, int i2, int i3, int i4) {
        if (drawable != null) {
            TransitionParams transitionParams = this.transitionParams;
            float f = i4 + i2 + transitionParams.deltaBottom;
            int i5 = (int) f;
            this.transitionYOffsetForDrawables = f - i5;
            drawable.setBounds((int) (i + transitionParams.deltaLeft), (int) (i2 + transitionParams.deltaTop), (int) (i + i3 + transitionParams.deltaRight), i5);
        }
    }

    public void setEnterTransitionInProgress(boolean z) {
        this.enterTransitionInProgress = z;
        invalidate();
    }

    public void setFullyDraw(boolean z) {
        this.fullyDraw = z;
    }

    public void setHighlighted(boolean z) {
        int i;
        if (this.isHighlighted == z) {
            return;
        }
        this.isHighlighted = z;
        if (z) {
            this.quoteHighlight = null;
        }
        if (z) {
            i = 0;
            this.isHighlightedAnimated = false;
        } else {
            this.lastHighlightProgressTime = System.currentTimeMillis();
            this.isHighlightedAnimated = true;
            i = 300;
        }
        this.highlightProgress = i;
        updateRadialProgressBackground();
        if (this.useSeekBarWaveform) {
            this.seekBarWaveform.setSelected(isDrawSelectionBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectionBackground());
        }
        invalidate();
        if (getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    public void setHighlightedAnimated() {
        this.isHighlightedAnimated = true;
        this.highlightProgress = 1000;
        this.lastHighlightProgressTime = System.currentTimeMillis();
        invalidate();
        if (getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    public boolean setHighlightedSpan(CharacterStyle characterStyle) {
        int i;
        int i2;
        int i3;
        int i4;
        MessageObject messageObject = this.messageObjectToSet;
        if (messageObject == null) {
            messageObject = this.currentMessageObject;
        }
        this.highlightedQuote = false;
        if (messageObject == null || messageObject.messageOwner.message == null || characterStyle == null) {
            if (!this.urlPathSelection.isEmpty()) {
                this.linkSelectionBlockNum = -1;
                this.highlightedQuote = false;
                resetUrlPaths();
                invalidate();
            }
            return false;
        }
        if (this.captionLayout != null && !TextUtils.isEmpty(messageObject.caption)) {
            CharSequence charSequence = messageObject.caption;
            if (charSequence instanceof Spannable) {
                Spanned spanned = (Spanned) charSequence;
                CharacterStyle[] characterStyleArr = (CharacterStyle[]) spanned.getSpans(0, spanned.length(), CharacterStyle.class);
                if (characterStyleArr != null && characterStyleArr.length > 0) {
                    for (CharacterStyle characterStyle2 : characterStyleArr) {
                        if (characterStyle2 == characterStyle) {
                            i4 = spanned.getSpanStart(characterStyle);
                            i3 = spanned.getSpanEnd(characterStyle);
                            break;
                        }
                    }
                }
                i3 = -1;
                i4 = -1;
                if (i4 >= 0 && i3 >= 0) {
                    int i5 = i3 - i4;
                    for (int i6 = 0; i6 < this.captionLayout.textLayoutBlocks.size(); i6++) {
                        MessageObject.TextLayoutBlock textLayoutBlock = this.captionLayout.textLayoutBlocks.get(i6);
                        if (i4 >= textLayoutBlock.charactersOffset && i4 < textLayoutBlock.charactersEnd) {
                            this.linkSelectionBlockNum = i6;
                            this.highlightedQuote = false;
                            resetUrlPaths();
                            try {
                                LinkPath obtainNewUrlPath = obtainNewUrlPath();
                                obtainNewUrlPath.setCurrentLayout(textLayoutBlock.textLayout, i4, 0.0f);
                                textLayoutBlock.textLayout.getSelectionPath(i4, i3, obtainNewUrlPath);
                                if (i3 >= textLayoutBlock.charactersOffset + i5) {
                                    for (int i7 = i6 + 1; i7 < this.captionLayout.textLayoutBlocks.size(); i7++) {
                                        MessageObject.TextLayoutBlock textLayoutBlock2 = this.captionLayout.textLayoutBlocks.get(i7);
                                        int i8 = textLayoutBlock2.charactersEnd - textLayoutBlock2.charactersOffset;
                                        LinkPath obtainNewUrlPath2 = obtainNewUrlPath();
                                        obtainNewUrlPath2.setCurrentLayout(textLayoutBlock2.textLayout, 0, textLayoutBlock2.height);
                                        textLayoutBlock2.textLayout.getSelectionPath(0, i3 - textLayoutBlock2.charactersOffset, obtainNewUrlPath2);
                                        if (i3 < (textLayoutBlock.charactersOffset + i8) - 1) {
                                            break;
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                            invalidate();
                            return true;
                        }
                    }
                }
            }
        }
        CharSequence charSequence2 = messageObject.messageText;
        if ((charSequence2 instanceof Spanned) && messageObject.textLayoutBlocks != null) {
            Spanned spanned2 = (Spanned) charSequence2;
            CharacterStyle[] characterStyleArr2 = (CharacterStyle[]) spanned2.getSpans(0, spanned2.length(), CharacterStyle.class);
            if (characterStyleArr2 != null && characterStyleArr2.length > 0) {
                for (CharacterStyle characterStyle3 : characterStyleArr2) {
                    if (characterStyle3 == characterStyle) {
                        i2 = spanned2.getSpanStart(characterStyle);
                        i = spanned2.getSpanEnd(characterStyle);
                        break;
                    }
                }
            }
            i = -1;
            i2 = -1;
            if (i2 >= 0 && i >= 0) {
                int i9 = i - i2;
                for (int i10 = 0; i10 < messageObject.textLayoutBlocks.size(); i10++) {
                    MessageObject.TextLayoutBlock textLayoutBlock3 = messageObject.textLayoutBlocks.get(i10);
                    if (i2 >= textLayoutBlock3.charactersOffset && i2 < textLayoutBlock3.charactersEnd) {
                        this.linkSelectionBlockNum = i10;
                        this.highlightedQuote = false;
                        resetUrlPaths();
                        try {
                            LinkPath obtainNewUrlPath3 = obtainNewUrlPath();
                            obtainNewUrlPath3.setCurrentLayout(textLayoutBlock3.textLayout, i2, 0.0f);
                            textLayoutBlock3.textLayout.getSelectionPath(i2, i, obtainNewUrlPath3);
                            if (i >= textLayoutBlock3.charactersOffset + i9) {
                                for (int i11 = i10 + 1; i11 < messageObject.textLayoutBlocks.size(); i11++) {
                                    MessageObject.TextLayoutBlock textLayoutBlock4 = messageObject.textLayoutBlocks.get(i11);
                                    int i12 = textLayoutBlock4.charactersEnd - textLayoutBlock4.charactersOffset;
                                    LinkPath obtainNewUrlPath4 = obtainNewUrlPath();
                                    obtainNewUrlPath4.setCurrentLayout(textLayoutBlock4.textLayout, 0, textLayoutBlock4.height);
                                    textLayoutBlock4.textLayout.getSelectionPath(0, i - textLayoutBlock4.charactersOffset, obtainNewUrlPath4);
                                    if (i < (textLayoutBlock3.charactersOffset + i12) - 1) {
                                        break;
                                    }
                                }
                            }
                        } catch (Exception e2) {
                            FileLog.e(e2);
                        }
                        invalidate();
                        return true;
                    }
                }
            }
        }
        if (!this.urlPathSelection.isEmpty()) {
            this.linkSelectionBlockNum = -1;
            this.highlightedQuote = false;
            resetUrlPaths();
            invalidate();
        }
        return false;
    }

    public boolean setHighlightedText(String str) {
        return setHighlightedText(str, false, -1, false);
    }

    public boolean setHighlightedText(java.lang.String r18, boolean r19, int r20, boolean r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.setHighlightedText(java.lang.String, boolean, int, boolean):boolean");
    }

    public void setImageCoords(float f, float f2, float f3, float f4) {
        this.photoImage.setImageCoords(f, f2, f3, f4);
        int i = this.documentAttachType;
        if (i == 4 || i == 2) {
            this.videoButtonX = (int) (this.photoImage.getImageX() + AndroidUtilities.dp(8.0f));
            int imageY = (int) (this.photoImage.getImageY() + AndroidUtilities.dp(8.0f));
            this.videoButtonY = imageY;
            RadialProgress2 radialProgress2 = this.videoRadialProgress;
            int i2 = this.videoButtonX;
            radialProgress2.setProgressRect(i2, imageY, AndroidUtilities.dp(24.0f) + i2, this.videoButtonY + AndroidUtilities.dp(24.0f));
            this.buttonX = (int) (f + ((this.photoImage.getImageWidth() - AndroidUtilities.dp(48.0f)) / 2.0f));
            int imageY2 = (int) (this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - AndroidUtilities.dp(48.0f)) / 2.0f));
            this.buttonY = imageY2;
            RadialProgress2 radialProgress22 = this.radialProgress;
            int i3 = this.buttonX;
            radialProgress22.setProgressRect(i3, imageY2, AndroidUtilities.dp(48.0f) + i3, this.buttonY + AndroidUtilities.dp(48.0f));
        }
    }

    public void setImageCoords(RectF rectF) {
        setImageCoords(rectF.left, rectF.top, rectF.width(), rectF.height());
    }

    public void setInvalidateListener(Runnable runnable) {
        this.invalidateListener = runnable;
    }

    public void setInvalidateSpoilersParent(boolean z) {
        this.invalidateSpoilersParent = z;
    }

    public void setInvalidatesParent(boolean z) {
        this.invalidatesParent = z;
    }

    public void setIsUpdating(boolean z) {
        this.isUpdating = true;
    }

    public void setLastTouchCoords(float f, float f2) {
        this.lastTouchX = f;
        this.lastTouchY = f2;
        this.backgroundDrawable.setTouchCoords(f + getTranslationX(), this.lastTouchY);
    }

    public void setMessageObject(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages, boolean z, boolean z2) {
        if (this.attachedToWindow && !this.frozen) {
            setMessageContent(messageObject, groupedMessages, z, z2);
            return;
        }
        this.messageObjectToSet = messageObject;
        this.groupedMessagesToSet = groupedMessages;
        this.bottomNearToSet = z;
        this.topNearToSet = z2;
    }

    public void setOverrideInvalidate(Runnable runnable) {
        this.overrideInvalidate = runnable;
    }

    public void setParentBounds(float f, int i) {
        this.parentBoundsTop = f;
        this.parentBoundsBottom = i;
        if (this.photoImageOutOfBounds) {
            float y = getY() + this.photoImage.getImageY();
            if (this.photoImage.getImageHeight() + y < this.parentBoundsTop || y > this.parentBoundsBottom) {
                return;
            }
            invalidate();
        }
    }

    public void setParentViewSize(int i, int i2) {
        Theme.MessageDrawable messageDrawable;
        this.parentWidth = i;
        this.parentHeight = i2;
        this.backgroundHeight = i2;
        if (!(this.currentMessageObject != null && hasGradientService() && this.currentMessageObject.shouldDrawWithoutBackground()) && ((messageDrawable = this.currentBackgroundDrawable) == null || messageDrawable.getGradientShader() == null)) {
            return;
        }
        invalidate();
    }

    @Override
    public void setPressed(boolean z) {
        super.setPressed(z);
        updateRadialProgressBackground();
        if (this.useSeekBarWaveform) {
            this.seekBarWaveform.setSelected(isDrawSelectionBackground());
        } else {
            this.seekBar.setSelected(isDrawSelectionBackground());
        }
        invalidate();
    }

    public void setResourcesProvider(Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setResourcesProvider(resourcesProvider);
        }
        RadialProgress2 radialProgress22 = this.videoRadialProgress;
        if (radialProgress22 != null) {
            radialProgress22.setResourcesProvider(resourcesProvider);
        }
        RoundVideoPlayingDrawable roundVideoPlayingDrawable = this.roundVideoPlayingDrawable;
        if (roundVideoPlayingDrawable != null) {
            roundVideoPlayingDrawable.setResourcesProvider(resourcesProvider);
        }
    }

    public void setScrimReaction(Integer num) {
        this.reactionsLayoutInBubble.setScrimReaction(num);
    }

    public void setSelectedBackgroundProgress(float f) {
        this.selectedBackgroundProgress = f;
        invalidate();
    }

    public void setSlidingOffset(float f) {
        if (this.slidingOffsetX != f) {
            this.slidingOffsetX = f;
            updateTranslation();
        }
    }

    public void setSpoilersSuppressed(boolean z) {
        for (int i = 0; i < this.replySpoilers.size(); i++) {
            ((SpoilerEffect) this.replySpoilers.get(i)).setSuppressUpdates(z);
        }
        MessageObject.TextLayoutBlocks textLayoutBlocks = this.captionLayout;
        if (textLayoutBlocks != null && textLayoutBlocks.textLayoutBlocks != null) {
            for (int i2 = 0; i2 < this.captionLayout.textLayoutBlocks.size(); i2++) {
                MessageObject.TextLayoutBlock textLayoutBlock = this.captionLayout.textLayoutBlocks.get(i2);
                for (int i3 = 0; i3 < textLayoutBlock.spoilers.size(); i3++) {
                    textLayoutBlock.spoilers.get(i3).setSuppressUpdates(z);
                }
            }
        }
        if (getMessageObject() == null || getMessageObject().textLayoutBlocks == null) {
            return;
        }
        for (int i4 = 0; i4 < getMessageObject().textLayoutBlocks.size(); i4++) {
            MessageObject.TextLayoutBlock textLayoutBlock2 = getMessageObject().textLayoutBlocks.get(i4);
            for (int i5 = 0; i5 < textLayoutBlock2.spoilers.size(); i5++) {
                textLayoutBlock2.spoilers.get(i5).setSuppressUpdates(z);
            }
        }
    }

    public void setTimeAlpha(float f) {
        this.timeAlpha = f;
    }

    @Override
    public void setTranslationX(float f) {
        super.setTranslationX(f);
    }

    public void setVisibleOnScreen(boolean z, float f, float f2) {
        if (this.visibleOnScreen != z) {
            this.visibleOnScreen = z;
            checkImageReceiversAttachState();
            if (z) {
                invalidate();
            }
        }
        float imageY = f - this.photoImage.getImageY();
        float measuredHeight = f2 - (getMeasuredHeight() - this.photoImage.getImageY2());
        float imageHeight = this.photoImage.getImageHeight();
        if (imageY > 0.0f) {
            imageHeight -= imageY;
        }
        if (measuredHeight > 0.0f) {
            imageHeight -= measuredHeight;
        }
        ImageReceiver imageReceiver = this.photoImage;
        boolean z2 = imageHeight / imageReceiver.getImageHeight() < 0.25f;
        this.skipFrameUpdate = z2;
        imageReceiver.setSkipUpdateFrame(z2);
    }

    public void setVisiblePart(int i, int i2, int i3, float f, float f2, int i4, int i5, int i6, int i7) {
        MessageObject messageObject;
        this.childPosition = i;
        this.visibleHeight = i2;
        this.visibleParent = i3;
        this.parentWidth = i4;
        this.parentHeight = i5;
        this.visibleTop = f2;
        this.visibleParentOffset = f;
        this.backgroundHeight = i5;
        this.blurredViewTopOffset = i6;
        this.blurredViewBottomOffset = i7;
        if ((!this.botButtons.isEmpty() || (this.channelRecommendationsCell != null && (messageObject = this.currentMessageObject) != null && messageObject.type == 27)) && this.viewTop != f2) {
            invalidate();
        }
        this.viewTop = f2;
        if (i3 != this.parentHeight || f != this.parentViewTopOffset) {
            this.parentViewTopOffset = f;
            this.parentHeight = i3;
        }
        if (this.currentMessageObject != null && hasGradientService() && this.currentMessageObject.shouldDrawWithoutBackground()) {
            invalidate();
        }
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 == null || messageObject2.textLayoutBlocks == null) {
            return;
        }
        int i8 = i - this.textY;
        int i9 = 0;
        for (int i10 = 0; i10 < this.currentMessageObject.textLayoutBlocks.size() && this.currentMessageObject.textLayoutBlocks.get(i10).textYOffset(this.currentMessageObject.textLayoutBlocks, this.transitionParams) <= i8; i10++) {
            i9 = i10;
        }
        int i11 = -1;
        int i12 = -1;
        int i13 = 0;
        while (i9 < this.currentMessageObject.textLayoutBlocks.size()) {
            float textYOffset = this.currentMessageObject.textLayoutBlocks.get(i9).textYOffset(this.currentMessageObject.textLayoutBlocks, this.transitionParams);
            float height = r13.padTop + textYOffset + r13.height(this.transitionParams) + r13.padBottom;
            float f3 = i8;
            if (!intersect(textYOffset, height, f3, i8 + i2)) {
                if (textYOffset > f3) {
                    break;
                }
            } else {
                if (i11 == -1) {
                    i11 = i9;
                }
                i13++;
                i12 = i9;
            }
            i9++;
        }
        if (this.lastVisibleBlockNum == i12 && this.firstVisibleBlockNum == i11 && this.totalVisibleBlocksCount == i13) {
            if (this.animatedEmojiStack != null) {
                for (int i14 = 0; i14 < this.animatedEmojiStack.holders.size(); i14++) {
                    AnimatedEmojiSpan.AnimatedEmojiHolder animatedEmojiHolder = (AnimatedEmojiSpan.AnimatedEmojiHolder) this.animatedEmojiStack.holders.get(i14);
                    if (animatedEmojiHolder == null || !animatedEmojiHolder.skipDraw || animatedEmojiHolder.outOfBounds((this.parentBoundsTop - getY()) - animatedEmojiHolder.drawingYOffset, (this.parentBoundsBottom - getY()) - animatedEmojiHolder.drawingYOffset)) {
                    }
                }
                return;
            }
            return;
        }
        this.lastVisibleBlockNum = i12;
        this.firstVisibleBlockNum = i11;
        this.totalVisibleBlocksCount = i13;
        invalidate();
    }

    public void setupTextColors() {
        TextPaint textPaint;
        TextPaint textPaint2;
        TextPaint textPaint3;
        TextPaint textPaint4;
        TextPaint textPaint5;
        TextPaint textPaint6;
        TextPaint textPaint7;
        int i;
        if (this.currentMessageObject.isOutOwner()) {
            TextPaint textPaint8 = Theme.chat_msgTextPaint;
            int i2 = Theme.key_chat_messageTextOut;
            textPaint8.setColor(getThemedColor(i2));
            Theme.chat_msgGameTextPaint.setColor(getThemedColor(i2));
            Theme.chat_msgTextCodePaint.setColor(getThemedColor(i2));
            Theme.chat_msgTextCode2Paint.setColor(getThemedColor(i2));
            Theme.chat_msgTextCode3Paint.setColor(getThemedColor(i2));
            textPaint = Theme.chat_msgGameTextPaint;
            textPaint2 = Theme.chat_replyTextPaint;
            textPaint3 = Theme.chat_quoteTextPaint;
            textPaint4 = Theme.chat_msgTextPaint;
            textPaint5 = Theme.chat_msgTextCodePaint;
            textPaint6 = Theme.chat_msgTextCode2Paint;
            textPaint7 = Theme.chat_msgTextCode3Paint;
            i = Theme.key_chat_messageLinkOut;
        } else {
            TextPaint textPaint9 = Theme.chat_msgTextPaint;
            int i3 = Theme.key_chat_messageTextIn;
            textPaint9.setColor(getThemedColor(i3));
            Theme.chat_msgGameTextPaint.setColor(getThemedColor(i3));
            Theme.chat_msgTextCodePaint.setColor(getThemedColor(i3));
            Theme.chat_msgTextCode2Paint.setColor(getThemedColor(i3));
            Theme.chat_msgTextCode3Paint.setColor(getThemedColor(i3));
            textPaint = Theme.chat_msgGameTextPaint;
            textPaint2 = Theme.chat_replyTextPaint;
            textPaint3 = Theme.chat_quoteTextPaint;
            textPaint4 = Theme.chat_msgTextPaint;
            textPaint5 = Theme.chat_msgTextCodePaint;
            textPaint6 = Theme.chat_msgTextCode2Paint;
            textPaint7 = Theme.chat_msgTextCode3Paint;
            i = Theme.key_chat_messageLinkIn;
        }
        int themedColor = getThemedColor(i);
        textPaint7.linkColor = themedColor;
        textPaint6.linkColor = themedColor;
        textPaint5.linkColor = themedColor;
        textPaint4.linkColor = themedColor;
        textPaint3.linkColor = themedColor;
        textPaint2.linkColor = themedColor;
        textPaint.linkColor = themedColor;
    }

    public void shakeView() {
        PropertyValuesHolder ofKeyframe = PropertyValuesHolder.ofKeyframe(View.ROTATION, Keyframe.ofFloat(0.0f, 0.0f), Keyframe.ofFloat(0.2f, 3.0f), Keyframe.ofFloat(0.4f, -3.0f), Keyframe.ofFloat(0.6f, 3.0f), Keyframe.ofFloat(0.8f, -3.0f), Keyframe.ofFloat(1.0f, 0.0f));
        Keyframe ofFloat = Keyframe.ofFloat(0.0f, 1.0f);
        Keyframe ofFloat2 = Keyframe.ofFloat(0.5f, 0.97f);
        Keyframe ofFloat3 = Keyframe.ofFloat(1.0f, 1.0f);
        PropertyValuesHolder ofKeyframe2 = PropertyValuesHolder.ofKeyframe(View.SCALE_X, ofFloat, ofFloat2, ofFloat3);
        PropertyValuesHolder ofKeyframe3 = PropertyValuesHolder.ofKeyframe(View.SCALE_Y, ofFloat, ofFloat2, ofFloat3);
        AnimatorSet animatorSet = new AnimatorSet();
        this.shakeAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofPropertyValuesHolder(this, ofKeyframe), ObjectAnimator.ofPropertyValuesHolder(this, ofKeyframe2), ObjectAnimator.ofPropertyValuesHolder(this, ofKeyframe3));
        this.shakeAnimation.setDuration(500L);
        this.shakeAnimation.start();
    }

    public boolean shouldDrawAlphaLayer() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        return (groupedMessages == null || !groupedMessages.transitionParams.backgroundChangeBounds) && getAlpha() != 1.0f;
    }

    public boolean shouldDrawTimeOnMedia() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.shouldDrawTimeOnMedia():boolean");
    }

    public void showHintButton(boolean z, boolean z2, int i) {
        if (i == -1 || i == 0) {
            if (this.hintButtonVisible == z) {
                return;
            }
            this.hintButtonVisible = z;
            if (z2) {
                invalidate();
            } else {
                this.hintButtonProgress = z ? 1.0f : 0.0f;
            }
        }
        if ((i == -1 || i == 1) && this.psaButtonVisible != z) {
            this.psaButtonVisible = z;
            if (!z2) {
                this.psaButtonProgress = z ? 1.0f : 0.0f;
            } else {
                setInvalidatesParent(true);
                invalidate();
            }
        }
    }

    public void startRevealMedia() {
        startRevealMedia(this.photoImage.getImageX() + (this.photoImage.getImageWidth() / 2.0f), this.photoImage.getImageY() + (this.photoImage.getImageHeight() / 2.0f));
    }

    public void startRevealMedia(float f, float f2) {
        float sqrt = (float) Math.sqrt(Math.pow(this.photoImage.getImageWidth(), 2.0d) + Math.pow(this.photoImage.getImageHeight(), 2.0d));
        this.mediaSpoilerRevealMaxRadius = sqrt;
        startRevealMedia(f, f2, sqrt);
    }

    public void updateAnimatedEmojis() {
        MessageObject messageObject;
        AnimatedEmojiSpan.EmojiGroupedSpans update;
        ArrayList<MessageObject.TextLayoutBlock> arrayList;
        if (!this.imageReceiversAttachState || (messageObject = this.currentMessageObject) == null) {
            return;
        }
        int cacheTypeForEnterView = messageObject.wasJustSent ? AnimatedEmojiDrawable.getCacheTypeForEnterView() : 0;
        MessageObject.TextLayoutBlocks textLayoutBlocks = this.captionLayout;
        if (textLayoutBlocks == null || (arrayList = textLayoutBlocks.textLayoutBlocks) == null) {
            ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
            update = AnimatedEmojiSpan.update(cacheTypeForEnterView, this, chatMessageCellDelegate == null || !chatMessageCellDelegate.canDrawOutboundsContent(), this.animatedEmojiStack, this.currentMessageObject.textLayoutBlocks);
        } else {
            update = AnimatedEmojiSpan.update(cacheTypeForEnterView, (View) this, false, this.animatedEmojiStack, arrayList);
        }
        this.animatedEmojiStack = update;
    }

    public void updateButtonState(boolean r18, boolean r19, boolean r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.updateButtonState(boolean, boolean, boolean):void");
    }

    public void updateCaptionLayout() {
        float imageX;
        float imageY;
        float imageHeight;
        float f;
        int i;
        float dp;
        int dp2;
        int i2;
        MessageObject messageObject = this.currentMessageObject;
        int i3 = messageObject.type;
        boolean z = true;
        if (i3 == 1 || i3 == 20 || this.documentAttachType == 4 || i3 == 8 || i3 == 23) {
            TransitionParams transitionParams = this.transitionParams;
            if (transitionParams.imageChangeBoundsTransition) {
                imageX = transitionParams.animateToImageX;
                imageY = transitionParams.animateToImageY;
                imageHeight = transitionParams.animateToImageH;
            } else {
                imageX = this.photoImage.getImageX();
                imageY = this.photoImage.getImageY();
                imageHeight = this.photoImage.getImageHeight();
            }
            this.captionX = imageX + AndroidUtilities.dp(5.0f) + this.captionOffsetX;
            float dp3 = imageY + imageHeight + AndroidUtilities.dp(6.0f);
            this.captionY = imageY + AndroidUtilities.lerp(imageHeight + AndroidUtilities.dp(6.0f), (this.captionLayout == null ? 0 : -r6.textHeight()) - AndroidUtilities.dp(4.0f), mediaAbove());
            f = dp3;
            z = false;
        } else {
            float f2 = 43.0f;
            float f3 = 0.0f;
            if (this.hasOldCaptionPreview) {
                this.captionX = this.backgroundDrawableLeft + AndroidUtilities.dp(messageObject.isOutOwner() ? 11.0f : 17.0f) + this.captionOffsetX;
                dp2 = (((this.totalHeight - this.captionHeight) - AndroidUtilities.dp(this.drawPinnedTop ? 9.0f : 10.0f)) - this.linkPreviewHeight) - AndroidUtilities.dp(17.0f);
                if (!this.drawCommentButton || this.drawSideButton == 3) {
                    f2 = 0.0f;
                } else if (shouldDrawTimeOnMedia()) {
                    f2 = 41.3f;
                }
                i2 = AndroidUtilities.dp(f2);
            } else {
                if (this.isRoundVideo) {
                    dp = getBackgroundDrawableLeft() + AndroidUtilities.dp((this.currentMessageObject.isOutOwner() ? 0 : 6) + 11);
                } else {
                    int i4 = this.backgroundDrawableLeft;
                    if (!messageObject.isOutOwner() && !this.mediaBackground && !this.drawPinnedBottom) {
                        r9 = 17.0f;
                    }
                    dp = i4 + AndroidUtilities.dp(r9) + this.captionOffsetX;
                }
                this.captionX = dp;
                int dp4 = (this.totalHeight - this.captionHeight) - AndroidUtilities.dp(this.drawPinnedTop ? 9.0f : 10.0f);
                if (!this.drawCommentButton || this.drawSideButton == 3) {
                    f2 = 0.0f;
                } else if (shouldDrawTimeOnMedia()) {
                    f2 = 41.3f;
                }
                dp2 = dp4 - AndroidUtilities.dp(f2);
                ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
                if (reactionsLayoutInBubble.isEmpty || reactionsLayoutInBubble.isSmall) {
                    i2 = 0;
                } else {
                    if (this.currentMessageObject.type == 9 && this.currentPosition == null) {
                        f3 = 10.0f;
                    }
                    i2 = AndroidUtilities.dp(f3) + this.reactionsLayoutInBubble.totalHeight;
                }
            }
            f = dp2 - i2;
            this.captionY = AndroidUtilities.lerp(f, AndroidUtilities.dp(9.0f) + this.namesOffset, mediaAbove());
        }
        this.captionX += getExtraTextX();
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 == null || (i = messageObject2.type) == 0 || i == 24 || messageObject2.isGiveawayOrGiveawayResults() || this.currentMessageObject.isSponsored()) {
            return;
        }
        if (z && this.hasFactCheck) {
            this.captionY -= (this.factCheckHeight + AndroidUtilities.dp(16.0f)) * (1.0f - mediaAbove());
        }
        this.factCheckY = (int) (((f - (z ? this.factCheckHeight + AndroidUtilities.dp(14.0f) : 0)) + AndroidUtilities.lerp(this.captionLayout != null ? r1.textHeight(this.transitionParams) + AndroidUtilities.dp(4.0f) : 0, AndroidUtilities.dp(6.0f), mediaAbove())) - this.transitionParams.deltaTop);
    }

    public void updatePlayingMessageProgress() {
        StaticLayout staticLayout;
        double d;
        double d2;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        OldVideoPlayerRewinder oldVideoPlayerRewinder = this.videoPlayerRewinder;
        if (oldVideoPlayerRewinder != null && oldVideoPlayerRewinder.rewindCount != 0 && oldVideoPlayerRewinder.rewindByBackSeek) {
            messageObject.audioProgress = oldVideoPlayerRewinder.getVideoProgress();
        }
        double d3 = 0.0d;
        if (this.documentAttachType == 4) {
            SeekBar seekBar = this.seekBar;
            if (seekBar != null) {
                seekBar.clearTimestamps();
            }
            if (this.infoLayout == null || !(PhotoViewer.isPlayingMessage(this.currentMessageObject) || MediaController.getInstance().isGoingToShowMessageObject(this.currentMessageObject))) {
                AnimatedFileDrawable animation = this.photoImage.getAnimation();
                if (animation != null) {
                    MessageObject messageObject2 = this.currentMessageObject;
                    int durationMs = animation.getDurationMs() / 1000;
                    messageObject2.audioPlayerDuration = durationMs;
                    d2 = durationMs;
                    MessageObject messageObject3 = this.currentMessageObject;
                    TLRPC.Message message = messageObject3.messageOwner;
                    if (message.ttl > 0 && message.destroyTime == 0 && !messageObject3.needDrawBluredPreview() && this.currentMessageObject.isVideo() && animation.hasBitmap()) {
                        this.delegate.didStartVideoStream(this.currentMessageObject);
                    }
                } else {
                    d2 = 0.0d;
                }
                if (d2 == 0.0d) {
                    d2 = this.currentMessageObject.getDuration();
                }
                if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    double d4 = this.currentMessageObject.audioProgress;
                    Double.isNaN(d4);
                    d2 -= d4 * d2;
                } else if (animation != null) {
                    if (d2 != 0.0d) {
                        double currentProgressMs = animation.getCurrentProgressMs() / 1000;
                        Double.isNaN(currentProgressMs);
                        d2 -= currentProgressMs;
                    }
                    if (this.delegate != null && animation.getCurrentProgressMs() >= 3000) {
                        this.delegate.videoTimerReached();
                    }
                }
                if (this.lastTime != d2) {
                    String formatShortDuration = AndroidUtilities.formatShortDuration((int) d2);
                    this.infoWidth = (int) Math.ceil(Theme.chat_infoPaint.measureText(formatShortDuration));
                    this.infoLayout = new StaticLayout(formatShortDuration, Theme.chat_infoPaint, this.infoWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.lastTime = d2;
                    return;
                }
                return;
            }
            return;
        }
        if (this.isRoundVideo) {
            if (!this.useSeekBarWaveform) {
                if (!this.seekBar.isDragging()) {
                    this.seekBar.setProgress(this.currentMessageObject.audioProgress);
                    this.seekBar.setBufferedProgress(this.currentMessageObject.bufferedProgress);
                }
                this.seekBar.clearTimestamps();
            } else if (!this.seekBarWaveform.isDragging()) {
                this.seekBarWaveform.setProgress(this.currentMessageObject.audioProgress, true);
            }
            TLRPC.Document document = this.currentMessageObject.getDocument();
            if (document != null) {
                while (r1 < document.attributes.size()) {
                    TLRPC.DocumentAttribute documentAttribute = document.attributes.get(r1);
                    if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                        d = documentAttribute.duration;
                        break;
                    }
                    r1++;
                }
            }
            d = 0.0d;
            long j = this.overridenDuration;
            if (j >= 0) {
                d = j;
            } else if (MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                double d5 = this.currentMessageObject.audioProgressSec;
                Double.isNaN(d5);
                d = Math.max(0.0d, d - d5);
            }
            if (this.lastTime != d) {
                this.lastTime = d;
                String formatLongDuration = AndroidUtilities.formatLongDuration((int) d);
                this.timeWidthAudio = (int) Math.ceil(Theme.chat_timePaint.measureText(formatLongDuration));
                this.durationLayout = new StaticLayout(formatLongDuration, Theme.chat_timePaint, this.timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            }
            float f = this.currentMessageObject.audioProgress;
            if (f != 0.0f) {
                this.lastDrawingAudioProgress = f;
                if (f > 0.9f) {
                    this.lastDrawingAudioProgress = 1.0f;
                }
            }
        } else {
            if (this.documentAttach == null) {
                return;
            }
            if (!this.useSeekBarWaveform) {
                if (!this.seekBar.isDragging()) {
                    this.seekBar.setProgress(this.currentMessageObject.audioProgress);
                    this.seekBar.setBufferedProgress(this.currentMessageObject.bufferedProgress);
                }
                this.seekBar.updateTimestamps(this.currentMessageObject, null);
            } else if (!this.seekBarWaveform.isDragging()) {
                this.seekBarWaveform.setProgress(this.currentMessageObject.audioProgress, true);
            }
            if (this.documentAttachType == 3) {
                long j2 = this.overridenDuration;
                if (j2 >= 0) {
                    d3 = j2;
                } else if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject)) {
                    while (true) {
                        if (r1 >= this.documentAttach.attributes.size()) {
                            break;
                        }
                        TLRPC.DocumentAttribute documentAttribute2 = this.documentAttach.attributes.get(r1);
                        if (documentAttribute2 instanceof TLRPC.TL_documentAttributeAudio) {
                            d3 = documentAttribute2.duration;
                            break;
                        }
                        r1++;
                    }
                } else {
                    d3 = this.currentMessageObject.audioProgressSec;
                }
                if (this.lastTime != d3) {
                    this.lastTime = d3;
                    String formatLongDuration2 = AndroidUtilities.formatLongDuration((int) d3);
                    this.timeWidthAudio = (int) Math.ceil(Theme.chat_audioTimePaint.measureText(formatLongDuration2));
                    staticLayout = new StaticLayout(formatLongDuration2, Theme.chat_audioTimePaint, this.timeWidthAudio, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.durationLayout = staticLayout;
                }
            } else {
                double duration = this.currentMessageObject.getDuration();
                r1 = MediaController.getInstance().isPlayingMessage(this.currentMessageObject) ? this.currentMessageObject.audioProgressSec : 0;
                double d6 = r1;
                if (this.lastTime != d6) {
                    this.lastTime = d6;
                    staticLayout = new StaticLayout(AndroidUtilities.formatShortDuration(r1, (int) duration), Theme.chat_audioTimePaint, (int) Math.ceil(Theme.chat_audioTimePaint.measureText(r3)), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                    this.durationLayout = staticLayout;
                }
            }
        }
        invalidate();
    }

    public void updateProgressLoadingLink() {
        MessageObject messageObject;
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        if (chatMessageCellDelegate == null) {
            return;
        }
        if (!chatMessageCellDelegate.isProgressLoading(this, 1)) {
            this.progressLoadingLink = null;
            ArrayList arrayList = this.progressLoadingLinkDrawables;
            if (arrayList == null || arrayList.isEmpty()) {
                return;
            }
            for (int i = 0; i < this.progressLoadingLinkDrawables.size(); i++) {
                LoadingDrawableLocation loadingDrawableLocation = (LoadingDrawableLocation) this.progressLoadingLinkDrawables.get(i);
                if (!loadingDrawableLocation.drawable.isDisappearing()) {
                    loadingDrawableLocation.drawable.disappear();
                }
            }
            return;
        }
        CharacterStyle progressLoadingLink = this.delegate.getProgressLoadingLink(this);
        if (progressLoadingLink == this.progressLoadingLink) {
            return;
        }
        this.progressLoadingLink = progressLoadingLink;
        LoadingDrawable loadingDrawable = this.progressLoadingLinkCurrentDrawable;
        if (loadingDrawable != null) {
            loadingDrawable.disappear();
            this.progressLoadingLinkCurrentDrawable = null;
        }
        LoadingDrawable loadingDrawable2 = new LoadingDrawable();
        this.progressLoadingLinkCurrentDrawable = loadingDrawable2;
        loadingDrawable2.setAppearByGradient(true);
        LinkPath linkPath = new LinkPath(true);
        this.progressLoadingLinkCurrentDrawable.usePath(linkPath);
        this.progressLoadingLinkCurrentDrawable.setRadiiDp(5.0f);
        LoadingDrawableLocation loadingDrawableLocation2 = new LoadingDrawableLocation();
        loadingDrawableLocation2.drawable = this.progressLoadingLinkCurrentDrawable;
        loadingDrawableLocation2.blockNum = -3;
        if (this.progressLoadingLinkDrawables == null) {
            this.progressLoadingLinkDrawables = new ArrayList();
        }
        this.progressLoadingLinkDrawables.add(loadingDrawableLocation2);
        if (this.progressLoadingLink == null || findProgressLoadingLink(loadingDrawableLocation2, linkPath, this.descriptionLayout, 0.0f, -2)) {
            return;
        }
        MessageObject.TextLayoutBlocks textLayoutBlocks = this.captionLayout;
        if ((textLayoutBlocks == null || !findProgressLoadingLink(loadingDrawableLocation2, linkPath, textLayoutBlocks.textLayoutBlocks)) && (messageObject = this.currentMessageObject) != null) {
            findProgressLoadingLink(loadingDrawableLocation2, linkPath, messageObject.textLayoutBlocks);
        }
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        if (super.verifyDrawable(drawable)) {
            return true;
        }
        Drawable[] drawableArr = this.selectorDrawable;
        if (drawable == drawableArr[0] || drawable == drawableArr[1] || drawable == this.linkPreviewSelector || drawable == this.nameLayoutSelector || drawable == this.replySelector) {
            return true;
        }
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        return (reactionsLayoutInBubble != null && reactionsLayoutInBubble.verifyDrawable(drawable)) || (drawable instanceof LoadingDrawable);
    }

    public boolean willRemovedAfterAnimation() {
        return this.willRemoved;
    }
}
