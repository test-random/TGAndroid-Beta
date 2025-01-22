package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.AvatarSpan;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.LoadingDrawable;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.GradientClip;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stars.StarGiftSheet;
import org.telegram.ui.Stars.StarGiftUniqueActionLayout;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.StoriesUtilities;
import org.telegram.ui.Stories.recorder.HintView2;

public class ChatActionCell extends BaseCell implements DownloadController.FileDownloadProgressListener, NotificationCenter.NotificationCenterDelegate {
    private static Map monthsToEmoticon;
    private int TAG;
    private SpannableStringBuilder accessibilityText;
    private int adaptiveEmojiColor;
    private ColorFilter adaptiveEmojiColorFilter;
    private AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiStack;
    private boolean attachedToWindow;
    private AvatarDrawable avatarDrawable;
    StoriesUtilities.AvatarStoryParams avatarStoryParams;
    private int backgroundButtonTop;
    private int backgroundHeight;
    private int backgroundLeft;
    private Path backgroundPath;
    private RectF backgroundRect;
    private int backgroundRectHeight;
    private int backgroundRight;
    private ButtonBounce bounce;
    private boolean buttonClickableAsImage;
    private boolean canDrawInParent;
    private Path clipPath;
    private int currentAccount;
    private MessageObject currentMessageObject;
    private ImageLocation currentVideoLocation;
    private int customDate;
    private CharSequence customText;
    private ChatActionCellDelegate delegate;
    private float dimAmount;
    private final Paint dimPaint;
    private boolean forceWasUnread;
    private boolean giftButtonPressed;
    private RectF giftButtonRect;
    private TLRPC.VideoSize giftEffectAnimation;
    private int giftPremiumAdditionalHeight;
    private StaticLayout giftPremiumButtonLayout;
    private float giftPremiumButtonWidth;
    private StaticLayout giftPremiumSubtitleLayout;
    private TextLayout giftPremiumText;
    private GradientClip giftPremiumTextClip;
    private boolean giftPremiumTextCollapsed;
    private int giftPremiumTextCollapsedHeight;
    private AnimatedFloat giftPremiumTextExpandedAnimated;
    private Text giftPremiumTextMore;
    private int giftPremiumTextMoreH;
    private int giftPremiumTextMoreX;
    private int giftPremiumTextMoreY;
    private boolean giftPremiumTextUncollapsed;
    private StaticLayout giftPremiumTitleLayout;
    private int giftRectSize;
    private CornerPathEffect giftRibbonPaintEffect;
    private ColorMatrixColorFilter giftRibbonPaintFilter;
    private boolean giftRibbonPaintFilterDark;
    private Path giftRibbonPath;
    private Text giftRibbonText;
    private TLRPC.Document giftSticker;
    private ImageReceiver.ImageReceiverDelegate giftStickerDelegate;
    private TextPaint giftSubtitlePaint;
    private TextPaint giftTextPaint;
    private TextPaint giftTitlePaint;
    private boolean hasReplyMessage;
    private boolean imagePressed;
    private ImageReceiver imageReceiver;
    private boolean invalidateColors;
    private Runnable invalidateListener;
    private boolean invalidatePath;
    private View invalidateWithParent;
    private boolean invalidatesParent;
    private boolean isSpoilerRevealing;
    private float lastTouchX;
    private float lastTouchY;
    private ArrayList lineHeights;
    private ArrayList lineWidths;
    private LoadingDrawable loadingDrawable;
    private int overriddenMaxWidth;
    private int overrideBackground;
    private Paint overrideBackgroundPaint;
    private int overrideText;
    private TextPaint overrideTextPaint;
    private URLSpan pressedLink;
    private int previousWidth;
    float progressToProgress;
    RadialProgressView progressView;
    private RadialProgress2 radialProgress;
    public final ReactionsLayoutInBubble reactionsLayoutInBubble;
    private RectF rect;
    private View rippleView;
    private StaticLayout settingWallpaperLayout;
    TextPaint settingWallpaperPaint;
    private float settingWallpaperProgress;
    private StaticLayout settingWallpaperProgressTextLayout;
    private SpoilerEffect spoilerPressed;
    public List spoilers;
    private Stack spoilersPool;
    public final StarGiftUniqueActionLayout starGiftLayout;
    public float starGiftLayoutX;
    public float starGiftLayoutY;
    private StarParticlesView.Drawable starParticlesDrawable;
    private Path starsPath;
    private int starsSize;
    private int stickerSize;
    private int textHeight;
    private StaticLayout textLayout;
    TextPaint textPaint;
    private boolean textPressed;
    private int textWidth;
    private int textX;
    private int textXLeft;
    private int textY;
    private Theme.ResourcesProvider themeDelegate;
    public final TransitionParams transitionParams;
    private float viewTop;
    private float viewTranslationX;
    private boolean visiblePartSet;
    private Drawable wallpaperPreviewDrawable;
    private boolean wasLayout;

    public class AnonymousClass1 extends ClickableSpan {
        final CharacterStyle val$link;

        AnonymousClass1(CharacterStyle characterStyle) {
            r2 = characterStyle;
        }

        @Override
        public void onClick(View view) {
            if (ChatActionCell.this.delegate != null) {
                ChatActionCell.this.openLink(r2);
            }
        }
    }

    public interface ChatActionCellDelegate {

        public abstract class CC {
            public static boolean $default$canDrawOutboundsContent(ChatActionCellDelegate chatActionCellDelegate) {
                return true;
            }

            public static void $default$didClickButton(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell) {
            }

            public static void $default$didClickImage(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell) {
            }

            public static boolean $default$didLongPress(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, float f, float f2) {
                return false;
            }

            public static void $default$didOpenPremiumGift(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, TLRPC.TL_premiumGiftOption tL_premiumGiftOption, String str, boolean z) {
            }

            public static void $default$didOpenPremiumGiftChannel(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, String str, boolean z) {
            }

            public static void $default$didPressReaction(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, TLRPC.ReactionCount reactionCount, boolean z, float f, float f2) {
            }

            public static void $default$didPressReplyMessage(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, int i) {
            }

            public static void $default$forceUpdate(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, boolean z) {
            }

            public static BaseFragment $default$getBaseFragment(ChatActionCellDelegate chatActionCellDelegate) {
                return null;
            }

            public static long $default$getDialogId(ChatActionCellDelegate chatActionCellDelegate) {
                return 0L;
            }

            public static long $default$getTopicId(ChatActionCellDelegate chatActionCellDelegate) {
                return 0L;
            }

            public static void $default$needOpenInviteLink(ChatActionCellDelegate chatActionCellDelegate, TLRPC.TL_chatInviteExported tL_chatInviteExported) {
            }

            public static void $default$needOpenUserProfile(ChatActionCellDelegate chatActionCellDelegate, long j) {
            }

            public static void $default$needShowEffectOverlay(ChatActionCellDelegate chatActionCellDelegate, ChatActionCell chatActionCell, TLRPC.Document document, TLRPC.VideoSize videoSize) {
            }
        }

        boolean canDrawOutboundsContent();

        void didClickButton(ChatActionCell chatActionCell);

        void didClickImage(ChatActionCell chatActionCell);

        boolean didLongPress(ChatActionCell chatActionCell, float f, float f2);

        void didOpenPremiumGift(ChatActionCell chatActionCell, TLRPC.TL_premiumGiftOption tL_premiumGiftOption, String str, boolean z);

        void didOpenPremiumGiftChannel(ChatActionCell chatActionCell, String str, boolean z);

        void didPressReaction(ChatActionCell chatActionCell, TLRPC.ReactionCount reactionCount, boolean z, float f, float f2);

        void didPressReplyMessage(ChatActionCell chatActionCell, int i);

        void forceUpdate(ChatActionCell chatActionCell, boolean z);

        BaseFragment getBaseFragment();

        long getDialogId();

        long getTopicId();

        void needOpenInviteLink(TLRPC.TL_chatInviteExported tL_chatInviteExported);

        void needOpenUserProfile(long j);

        void needShowEffectOverlay(ChatActionCell chatActionCell, TLRPC.Document document, TLRPC.VideoSize videoSize);
    }

    public class TextLayout {
        public AnimatedEmojiSpan.EmojiGroupedSpans emoji;
        public StaticLayout layout;
        public TextPaint paint;
        public int width;
        public float x;
        public float y;
        public List spoilers = new ArrayList();
        public final AtomicReference patchedLayout = new AtomicReference();

        TextLayout() {
        }

        public void attach() {
            this.emoji = AnimatedEmojiSpan.update(0, (View) ChatActionCell.this, false, this.emoji, this.layout);
        }

        public void detach() {
            AnimatedEmojiSpan.release(ChatActionCell.this, this.emoji);
        }

        public void setText(CharSequence charSequence, TextPaint textPaint, int i) {
            this.paint = textPaint;
            this.width = i;
            this.layout = new StaticLayout(charSequence, textPaint, i, Layout.Alignment.ALIGN_CENTER, 1.1f, 0.0f, false);
            if (ChatActionCell.this.currentMessageObject == null || !ChatActionCell.this.currentMessageObject.isSpoilersRevealed) {
                SpoilerEffect.addSpoilers(ChatActionCell.this, this.layout, -1, i, null, this.spoilers);
            } else {
                List list = this.spoilers;
                if (list != null) {
                    list.clear();
                }
            }
            attach();
        }
    }

    public interface ThemeDelegate extends Theme.ResourcesProvider {

        public abstract class CC {
            public static void $default$applyServiceShaderMatrix(ThemeDelegate themeDelegate, int i, int i2, float f, float f2) {
                Theme.applyServiceShaderMatrix(i, i2, f, f2);
            }
        }
    }

    public class TransitionParams {
        public boolean animateChange;
        public float animateChangeProgress = 1.0f;
        public boolean wasDraw;

        public TransitionParams() {
        }

        public boolean animateChange() {
            if (this.wasDraw) {
                return ChatActionCell.this.reactionsLayoutInBubble.animateChange();
            }
            return false;
        }

        public void onDetach() {
            this.wasDraw = false;
        }

        public void recordDrawingState() {
            this.wasDraw = true;
            ChatActionCell.this.reactionsLayoutInBubble.recordDrawingState();
        }

        public void resetAnimation() {
            this.animateChange = false;
            this.animateChangeProgress = 1.0f;
        }

        public boolean supportChangeAnimation() {
            return true;
        }
    }

    static {
        HashMap hashMap = new HashMap();
        monthsToEmoticon = hashMap;
        hashMap.put(1, "1⃣");
        monthsToEmoticon.put(3, "2⃣");
        monthsToEmoticon.put(6, "3⃣");
        monthsToEmoticon.put(12, "4⃣");
        monthsToEmoticon.put(24, "5⃣");
    }

    public ChatActionCell(Context context) {
        this(context, false, null);
    }

    public ChatActionCell(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.bounce = new ButtonBounce(this);
        this.currentAccount = UserConfig.selectedAccount;
        this.avatarStoryParams = new StoriesUtilities.AvatarStoryParams(false);
        this.giftButtonRect = new RectF();
        this.spoilers = new ArrayList();
        this.spoilersPool = new Stack();
        this.reactionsLayoutInBubble = new ReactionsLayoutInBubble(this);
        this.overrideBackground = -1;
        this.overrideText = -1;
        this.lineWidths = new ArrayList();
        this.lineHeights = new ArrayList();
        this.backgroundPath = new Path();
        this.rect = new RectF();
        this.invalidatePath = true;
        this.invalidateColors = false;
        this.giftPremiumTextUncollapsed = false;
        this.giftPremiumTextCollapsed = false;
        this.giftPremiumTextExpandedAnimated = new AnimatedFloat(this, 0L, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.buttonClickableAsImage = true;
        this.giftTitlePaint = new TextPaint(1);
        this.giftTextPaint = new TextPaint(1);
        this.giftSubtitlePaint = new TextPaint(1);
        this.radialProgress = new RadialProgress2(this);
        this.giftStickerDelegate = new ImageReceiver.ImageReceiverDelegate() {
            @Override
            public final void didSetImage(ImageReceiver imageReceiver, boolean z2, boolean z3, boolean z4) {
                ChatActionCell.this.lambda$new$0(imageReceiver, z2, z3, z4);
            }

            @Override
            public void didSetImageBitmap(int i, String str, Drawable drawable) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$didSetImageBitmap(this, i, str, drawable);
            }

            @Override
            public void onAnimationReady(ImageReceiver imageReceiver) {
                ImageReceiver.ImageReceiverDelegate.CC.$default$onAnimationReady(this, imageReceiver);
            }
        };
        this.starsPath = new Path();
        this.dimPaint = new Paint(1);
        this.transitionParams = new TransitionParams();
        this.avatarStoryParams.drawSegments = false;
        this.canDrawInParent = z;
        this.themeDelegate = resourcesProvider;
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.imageReceiver = imageReceiver;
        imageReceiver.setRoundRadius(AndroidUtilities.roundMessageSize / 2);
        this.avatarDrawable = new AvatarDrawable();
        this.TAG = DownloadController.getInstance(this.currentAccount).generateObserverTag();
        this.starGiftLayout = new StarGiftUniqueActionLayout(this.currentAccount, this, resourcesProvider);
        this.giftTitlePaint.setTextSize(TypedValue.applyDimension(1, 16.0f, getResources().getDisplayMetrics()));
        this.giftSubtitlePaint.setTextSize(TypedValue.applyDimension(1, 15.0f, getResources().getDisplayMetrics()));
        this.giftTextPaint.setTextSize(TypedValue.applyDimension(1, 15.0f, getResources().getDisplayMetrics()));
        View view = new View(context);
        this.rippleView = view;
        view.setBackground(Theme.createSelectorDrawable(Theme.multAlpha(-16777216, 0.1f), 7, AndroidUtilities.dp(16.0f)));
        this.rippleView.setVisibility(8);
        addView(this.rippleView);
        StarParticlesView.Drawable drawable = new StarParticlesView.Drawable(10);
        this.starParticlesDrawable = drawable;
        drawable.type = 100;
        drawable.isCircle = false;
        drawable.roundEffect = true;
        drawable.useRotate = false;
        drawable.useBlur = true;
        drawable.checkBounds = true;
        drawable.size1 = 1;
        drawable.k3 = 0.98f;
        drawable.k2 = 0.98f;
        drawable.k1 = 0.98f;
        drawable.paused = false;
        drawable.speedScale = 0.0f;
        drawable.minLifeTime = 750L;
        drawable.randLifeTime = 750;
        drawable.init();
    }

    private void buildLayout() {
        CharSequence charSequence;
        CharSequence replaceTags;
        int i;
        CharSequence charSequence2;
        boolean z;
        String str;
        CharSequence charSequence3;
        boolean z2;
        int i2;
        String str2;
        String str3;
        ArrayList<TLRPC.VideoSize> arrayList;
        TLRPC.Photo photo;
        ArrayList<TLRPC.VideoSize> arrayList2;
        Spannable spannable;
        CharSequence string;
        String string2;
        int i3;
        String str4;
        boolean z3;
        CharSequence charSequence4;
        boolean z4;
        int i4;
        String str5;
        ChatActionCell chatActionCell;
        String str6;
        String str7;
        String shortName;
        int i5;
        String str8;
        int i6;
        String formatString;
        int i7;
        CharSequence formatPluralStringComma;
        TL_stars.StarGift starGift;
        char c;
        Object valueOf;
        TLRPC.MessageMedia messageMedia;
        int i8;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null) {
            if (messageObject.isExpiredStory()) {
                charSequence = messageObject.messageOwner.media.user_id != UserConfig.getInstance(this.currentAccount).getClientUserId() ? StoriesUtilities.createExpiredStoryString(true, R.string.ExpiredStoryMention, new Object[0]) : StoriesUtilities.createExpiredStoryString(true, R.string.ExpiredStoryMentioned, MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(messageObject.getDialogId())).first_name);
            } else {
                ChatActionCellDelegate chatActionCellDelegate = this.delegate;
                charSequence = (chatActionCellDelegate != null && chatActionCellDelegate.getTopicId() == 0 && MessageObject.isTopicActionMessage(messageObject)) ? ForumUtilities.createActionTextWithTopic(MessagesController.getInstance(this.currentAccount).getTopicsController().findTopic(-messageObject.getDialogId(), MessageObject.getTopicId(this.currentAccount, messageObject.messageOwner, true)), messageObject) : null;
            }
            if (charSequence == null) {
                TLRPC.Message message = messageObject.messageOwner;
                if (message != null && (messageMedia = message.media) != null && messageMedia.ttl_seconds != 0) {
                    if (messageMedia.photo != null) {
                        i8 = R.string.AttachPhotoExpired;
                    } else {
                        TLRPC.Document document = messageMedia.document;
                        if ((document instanceof TLRPC.TL_documentEmpty) || ((messageMedia instanceof TLRPC.TL_messageMediaDocument) && document == null)) {
                            i8 = messageMedia.voice ? R.string.AttachVoiceExpired : messageMedia.round ? R.string.AttachRoundExpired : R.string.AttachVideoExpired;
                        }
                    }
                    charSequence = LocaleController.getString(i8);
                }
                charSequence = AnimatedEmojiSpan.cloneSpans(messageObject.messageText);
            }
        } else {
            charSequence = this.customText;
        }
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 != null && messageObject2.isRepostPreview) {
            charSequence = "";
        }
        createLayout(charSequence, this.previousWidth);
        if (messageObject != null) {
            int i9 = messageObject.type;
            if (i9 == 11) {
                float dp = this.textHeight + AndroidUtilities.dp(19.0f);
                float f = AndroidUtilities.roundMessageSize;
                this.imageReceiver.setImageCoords((this.previousWidth - AndroidUtilities.roundMessageSize) / 2.0f, dp, f, f);
            } else if (i9 == 25) {
                createGiftPremiumChannelLayouts();
            } else {
                if (i9 == 30) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.currentMessageObject.getDialogId()));
                    TLRPC.MessageAction messageAction = messageObject.messageOwner.action;
                    if (messageAction instanceof TLRPC.TL_messageActionGiftStars) {
                        String formatPluralStringComma2 = LocaleController.formatPluralStringComma("ActionGiftStarsTitle", (int) ((TLRPC.TL_messageActionGiftStars) messageAction).stars);
                        string = AndroidUtilities.replaceTags(this.currentMessageObject.isOutOwner() ? LocaleController.formatString(R.string.ActionGiftStarsSubtitle, UserObject.getForcedFirstName(user)) : LocaleController.getString(R.string.ActionGiftStarsSubtitleYou));
                        string2 = LocaleController.getString(R.string.ActionGiftStarsView);
                        i3 = this.giftRectSize;
                        str4 = null;
                        z3 = true;
                        charSequence4 = null;
                        z4 = false;
                        str7 = formatPluralStringComma2;
                        i4 = 11;
                        str6 = str7;
                        chatActionCell = this;
                        str5 = str6;
                    } else if ((messageAction instanceof TLRPC.TL_messageActionStarGiftUnique) && ((TLRPC.TL_messageActionStarGiftUnique) messageAction).refunded) {
                        long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                        if (messageObject.isOutOwner() != (!((TLRPC.TL_messageActionStarGiftUnique) messageObject.messageOwner.action).upgrade)) {
                            clientUserId = messageObject.getDialogId();
                        }
                        TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(clientUserId));
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                        spannableStringBuilder.append((CharSequence) LocaleController.getString(R.string.Gift2ActionTitle)).append((CharSequence) " ");
                        if (user2 != null && user2.photo != null) {
                            spannableStringBuilder.append((CharSequence) "a ");
                            AvatarSpan avatarSpan = new AvatarSpan(this, this.currentAccount, 18.0f);
                            avatarSpan.setUser(user2);
                            spannableStringBuilder.setSpan(avatarSpan, spannableStringBuilder.length() - 2, spannableStringBuilder.length() - 1, 33);
                        }
                        spannableStringBuilder.append((CharSequence) UserObject.getForcedFirstName(user2));
                        string = LocaleController.getString(R.string.Gift2ActionUpgradeRefundedText);
                        string2 = LocaleController.getString(R.string.ActionGiftStarsView);
                        str4 = LocaleController.getString(R.string.Gift2UniqueRibbon);
                        i3 = this.giftRectSize;
                        i4 = 12;
                        z3 = true;
                        charSequence4 = null;
                        z4 = false;
                        str6 = spannableStringBuilder;
                        chatActionCell = this;
                        str5 = str6;
                    } else if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
                        TLRPC.TL_messageActionStarGift tL_messageActionStarGift = (TLRPC.TL_messageActionStarGift) messageAction;
                        long j = tL_messageActionStarGift.convert_stars;
                        long clientUserId2 = UserConfig.getInstance(this.currentAccount).getClientUserId();
                        boolean z5 = tL_messageActionStarGift.peer != null;
                        boolean z6 = messageObject.getDialogId() == clientUserId2 && !z5;
                        long fromChatId = messageObject.getFromChatId();
                        TLRPC.Peer peer = tL_messageActionStarGift.from_id;
                        if (peer != null) {
                            fromChatId = DialogObject.getPeerDialogId(peer);
                        }
                        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder();
                        TLObject userOrChat = MessagesController.getInstance(this.currentAccount).getUserOrChat(fromChatId);
                        boolean z7 = tL_messageActionStarGift.can_upgrade && !tL_messageActionStarGift.converted && tL_messageActionStarGift.upgrade_stars > 0 && !tL_messageActionStarGift.upgraded;
                        if (z6) {
                            shortName = LocaleController.getString(R.string.Gift2ActionSelfTitle);
                        } else {
                            spannableStringBuilder2.append((CharSequence) LocaleController.getString(R.string.Gift2ActionTitle)).append((CharSequence) " ");
                            if (DialogObject.hasPhoto(userOrChat)) {
                                spannableStringBuilder2.append((CharSequence) "a ");
                                AvatarSpan avatarSpan2 = new AvatarSpan(this, this.currentAccount, 18.0f);
                                avatarSpan2.setObject(userOrChat);
                                spannableStringBuilder2.setSpan(avatarSpan2, spannableStringBuilder2.length() - 2, spannableStringBuilder2.length() - 1, 33);
                            }
                            shortName = DialogObject.getShortName(userOrChat);
                        }
                        spannableStringBuilder2.append((CharSequence) shortName);
                        boolean z8 = ((messageObject.isOutOwner() && !z6) || !tL_messageActionStarGift.converted) && tL_messageActionStarGift.convert_stars > 0 && MessagesController.getInstance(this.currentAccount).stargiftsConvertPeriodMax - (ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - messageObject.messageOwner.date) > 0 && !tL_messageActionStarGift.refunded;
                        if (tL_messageActionStarGift.refunded) {
                            i6 = R.string.Gift2ActionConvertRefundedText;
                        } else {
                            TLRPC.TL_textWithEntities tL_textWithEntities = tL_messageActionStarGift.message;
                            if (tL_textWithEntities == null || TextUtils.isEmpty(tL_textWithEntities.text)) {
                                if (z5) {
                                    if (!z8 || j <= 0) {
                                        i7 = R.string.Gift2ActionInfoChannelNoConvert;
                                        formatString = LocaleController.getString(i7);
                                    } else {
                                        i5 = (int) j;
                                        str8 = "Gift2ActionInfoChannel";
                                        formatString = LocaleController.formatPluralStringComma(str8, i5);
                                    }
                                } else if (z6) {
                                    if (!tL_messageActionStarGift.converted || j <= 0) {
                                        i7 = tL_messageActionStarGift.can_upgrade ? R.string.Gift2ActionSelfInfoUpgrade : R.string.Gift2ActionSelfInfoNoConvert;
                                        formatString = LocaleController.getString(i7);
                                    }
                                    formatPluralStringComma = LocaleController.formatPluralStringComma("Gift2ActionConvertedInfo", (int) j);
                                } else if (z7) {
                                    if (messageObject.isOutOwner()) {
                                        formatString = LocaleController.formatString(R.string.Gift2ActionUpgradeOut, UserObject.getForcedFirstName(user));
                                    } else {
                                        i7 = R.string.Gift2ActionUpgrade;
                                        formatString = LocaleController.getString(i7);
                                    }
                                } else if (messageObject.isOutOwner()) {
                                    formatString = (!z8 || j <= 0) ? tL_messageActionStarGift.can_upgrade ? LocaleController.formatString(R.string.Gift2ActionOutInfoUpgrade, UserObject.getForcedFirstName(user)) : LocaleController.formatString(R.string.Gift2ActionOutInfoNoConvert, UserObject.getForcedFirstName(user)) : LocaleController.formatPluralStringComma("Gift2ActionOutInfo", (int) j, UserObject.getForcedFirstName(user));
                                } else {
                                    if (!tL_messageActionStarGift.converted) {
                                        if (tL_messageActionStarGift.saved) {
                                            i6 = !z8 ? R.string.Gift2ActionBotSavedInfo : R.string.Gift2ActionSavedInfo;
                                        } else if (z8) {
                                            i5 = (int) j;
                                            str8 = "Gift2ActionInfo";
                                            formatString = LocaleController.formatPluralStringComma(str8, i5);
                                        } else {
                                            i6 = R.string.Gift2ActionBotInfo;
                                        }
                                    }
                                    formatPluralStringComma = LocaleController.formatPluralStringComma("Gift2ActionConvertedInfo", (int) j);
                                }
                                formatPluralStringComma = AndroidUtilities.replaceTags(formatString);
                            } else {
                                SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder(tL_messageActionStarGift.message.text);
                                this.giftTextPaint.setTextSize(AndroidUtilities.dp(13.0f));
                                MessageObject.addEntitiesToText(spannableStringBuilder3, tL_messageActionStarGift.message.entities, false, false, true, true);
                                formatPluralStringComma = MessageObject.replaceAnimatedEmoji(Emoji.replaceEmoji((CharSequence) spannableStringBuilder3, this.giftTextPaint.getFontMetricsInt(), false, (int[]) null), tL_messageActionStarGift.message.entities, this.giftTextPaint.getFontMetricsInt());
                            }
                            string = formatPluralStringComma;
                            starGift = tL_messageActionStarGift.gift;
                            if (starGift == null && starGift.limited) {
                                int i10 = R.string.Gift2Limited1OfRibbon;
                                int i11 = starGift.availability_total;
                                if (i11 > 1500) {
                                    c = 0;
                                    valueOf = AndroidUtilities.formatWholeNumber(i11, 0);
                                } else {
                                    c = 0;
                                    valueOf = Integer.valueOf(i11);
                                }
                                Object[] objArr = new Object[1];
                                objArr[c] = valueOf;
                                str4 = LocaleController.formatString(i10, objArr);
                            } else {
                                str4 = null;
                            }
                            String string3 = LocaleController.getString(R.string.ActionGiftStarsView);
                            if ((messageObject.isOutOwner() || tL_messageActionStarGift.forceIn || z7) && !messageObject.isOutOwner() && z7) {
                                SpannableStringBuilder spannableStringBuilder4 = new SpannableStringBuilder();
                                spannableStringBuilder4.append((CharSequence) "^  ");
                                ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.gift_unpack);
                                coloredImageSpan.setScale(0.8f, 0.8f);
                                spannableStringBuilder4.setSpan(coloredImageSpan, 0, 1, 33);
                                spannableStringBuilder4.append((CharSequence) LocaleController.getString(R.string.Gift2Unpack));
                                string2 = spannableStringBuilder4;
                            } else {
                                string2 = string3;
                            }
                            i3 = this.giftRectSize;
                            i4 = 11;
                            z3 = true;
                            charSequence4 = null;
                            z4 = false;
                            chatActionCell = this;
                            str5 = spannableStringBuilder2;
                        }
                        formatPluralStringComma = LocaleController.getString(i6);
                        string = formatPluralStringComma;
                        starGift = tL_messageActionStarGift.gift;
                        if (starGift == null) {
                        }
                        str4 = null;
                        String string32 = LocaleController.getString(R.string.ActionGiftStarsView);
                        if (messageObject.isOutOwner()) {
                        }
                        SpannableStringBuilder spannableStringBuilder42 = new SpannableStringBuilder();
                        spannableStringBuilder42.append((CharSequence) "^  ");
                        ColoredImageSpan coloredImageSpan2 = new ColoredImageSpan(R.drawable.gift_unpack);
                        coloredImageSpan2.setScale(0.8f, 0.8f);
                        spannableStringBuilder42.setSpan(coloredImageSpan2, 0, 1, 33);
                        spannableStringBuilder42.append((CharSequence) LocaleController.getString(R.string.Gift2Unpack));
                        string2 = spannableStringBuilder42;
                        i3 = this.giftRectSize;
                        i4 = 11;
                        z3 = true;
                        charSequence4 = null;
                        z4 = false;
                        chatActionCell = this;
                        str5 = spannableStringBuilder2;
                    } else {
                        long j2 = ((TLRPC.TL_messageActionPrizeStars) messageAction).stars;
                        String string4 = LocaleController.getString(R.string.ActionStarGiveawayPrizeTitle);
                        CharSequence charSequence5 = this.currentMessageObject.messageText;
                        str2 = LocaleController.getString(R.string.ActionGiftStarsView);
                        i2 = this.giftRectSize;
                        z2 = true;
                        charSequence3 = charSequence5;
                        str3 = string4;
                        createGiftPremiumLayouts(str3, null, charSequence3, false, str2, 11, null, i2, z2);
                        this.textLayout = null;
                        this.textHeight = 0;
                        this.textY = 0;
                    }
                } else if (i9 == 18) {
                    TLRPC.MessageAction messageAction2 = messageObject.messageOwner.action;
                    TLRPC.TL_textWithEntities tL_textWithEntities2 = messageAction2 instanceof TLRPC.TL_messageActionGiftPremium ? ((TLRPC.TL_messageActionGiftPremium) messageAction2).message : messageAction2 instanceof TLRPC.TL_messageActionGiftCode ? ((TLRPC.TL_messageActionGiftCode) messageAction2).message : null;
                    if (tL_textWithEntities2 == null || TextUtils.isEmpty(tL_textWithEntities2.text)) {
                        spannable = null;
                    } else {
                        SpannableStringBuilder spannableStringBuilder5 = new SpannableStringBuilder(tL_textWithEntities2.text);
                        this.giftTextPaint.setTextSize(AndroidUtilities.dp(13.0f));
                        MessageObject.addEntitiesToText(spannableStringBuilder5, tL_textWithEntities2.entities, false, false, true, true);
                        spannable = MessageObject.replaceAnimatedEmoji(Emoji.replaceEmoji((CharSequence) spannableStringBuilder5, this.giftTextPaint.getFontMetricsInt(), false, (int[]) null), tL_textWithEntities2.entities, this.giftTextPaint.getFontMetricsInt());
                    }
                    string = spannable == null ? LocaleController.getString(R.string.ActionGiftPremiumText) : spannable;
                    string2 = LocaleController.getString((!isGiftCode() || isSelfGiftCode()) ? R.string.ActionGiftPremiumView : R.string.GiftPremiumUseGiftBtn);
                    String formatPluralStringComma3 = LocaleController.formatPluralStringComma("ActionGiftPremiumTitle2", messageObject.messageOwner.action.months);
                    i3 = this.giftRectSize;
                    str4 = null;
                    z3 = false;
                    charSequence4 = null;
                    z4 = true;
                    str7 = formatPluralStringComma3;
                    i4 = 11;
                    str6 = str7;
                    chatActionCell = this;
                    str5 = str6;
                } else {
                    if (i9 == 21) {
                        TLRPC.TL_messageActionSuggestProfilePhoto tL_messageActionSuggestProfilePhoto = (TLRPC.TL_messageActionSuggestProfilePhoto) messageObject.messageOwner.action;
                        TLRPC.User user3 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(messageObject.isOutOwner() ? 0L : messageObject.getDialogId()));
                        boolean z9 = tL_messageActionSuggestProfilePhoto.video || !((photo = tL_messageActionSuggestProfilePhoto.photo) == null || (arrayList2 = photo.video_sizes) == null || arrayList2.isEmpty());
                        if (user3.id == UserConfig.getInstance(this.currentAccount).clientUserId) {
                            TLRPC.User user4 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(messageObject.getDialogId()));
                            replaceTags = z9 ? LocaleController.formatString(R.string.ActionSuggestVideoFromYouDescription, user4.first_name) : LocaleController.formatString(R.string.ActionSuggestPhotoFromYouDescription, user4.first_name);
                        } else {
                            replaceTags = z9 ? LocaleController.formatString(R.string.ActionSuggestVideoToYouDescription, user3.first_name) : LocaleController.formatString(R.string.ActionSuggestPhotoToYouDescription, user3.first_name);
                        }
                        i = (tL_messageActionSuggestProfilePhoto.video || !((arrayList = tL_messageActionSuggestProfilePhoto.photo.video_sizes) == null || arrayList.isEmpty())) ? R.string.ViewVideoAction : R.string.ViewPhotoAction;
                    } else if (i9 == 22) {
                        TLRPC.User user5 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(messageObject.isOutOwner() ? 0L : messageObject.getDialogId()));
                        if (messageObject.getDialogId() >= 0) {
                            if (!messageObject.isOutOwner() && messageObject.isWallpaperForBoth() && messageObject.isCurrentWallpaper()) {
                                charSequence2 = messageObject.messageText;
                                str = LocaleController.getString(R.string.RemoveWallpaperAction);
                                z = false;
                            } else if (user5 == null || user5.id != UserConfig.getInstance(this.currentAccount).clientUserId) {
                                charSequence2 = messageObject.messageText;
                                str = LocaleController.getString(R.string.ViewWallpaperAction);
                                z = true;
                            }
                            charSequence3 = charSequence2;
                            z2 = z;
                            i2 = this.giftRectSize;
                            str2 = str;
                            str3 = null;
                            createGiftPremiumLayouts(str3, null, charSequence3, false, str2, 11, null, i2, z2);
                            this.textLayout = null;
                            this.textHeight = 0;
                            this.textY = 0;
                        }
                        charSequence2 = messageObject.messageText;
                        z = true;
                        str = null;
                        charSequence3 = charSequence2;
                        z2 = z;
                        i2 = this.giftRectSize;
                        str2 = str;
                        str3 = null;
                        createGiftPremiumLayouts(str3, null, charSequence3, false, str2, 11, null, i2, z2);
                        this.textLayout = null;
                        this.textHeight = 0;
                        this.textY = 0;
                    } else if (messageObject.isStoryMention()) {
                        TLRPC.User user6 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(messageObject.messageOwner.media.user_id));
                        replaceTags = AndroidUtilities.replaceTags(user6.self ? LocaleController.formatString("StoryYouMentionedTitle", R.string.StoryYouMentionedTitle, MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(messageObject.getDialogId())).first_name) : LocaleController.formatString("StoryMentionedTitle", R.string.StoryMentionedTitle, user6.first_name));
                        i = R.string.StoryMentionedAction;
                    }
                    charSequence3 = replaceTags;
                    str2 = LocaleController.getString(i);
                    i2 = this.giftRectSize;
                    str3 = null;
                    z2 = true;
                    createGiftPremiumLayouts(str3, null, charSequence3, false, str2, 11, null, i2, z2);
                    this.textLayout = null;
                    this.textHeight = 0;
                    this.textY = 0;
                }
                chatActionCell.createGiftPremiumLayouts(str5, charSequence4, string, z4, string2, i4, str4, i3, z3);
            }
        }
        this.reactionsLayoutInBubble.x = AndroidUtilities.dp(12.0f);
        this.reactionsLayoutInBubble.measure(this.previousWidth - AndroidUtilities.dp(24.0f), 1);
    }

    private void checkLeftRightBounds() {
        this.backgroundLeft = (int) Math.min(this.backgroundLeft, this.rect.left);
        this.backgroundRight = (int) Math.max(this.backgroundRight, this.rect.right);
    }

    private void createGiftPremiumChannelLayouts() {
        int i;
        String str;
        SpannableStringBuilder spannableStringBuilder;
        String formatString;
        int dp = this.giftRectSize - AndroidUtilities.dp(16.0f);
        this.giftTitlePaint.setTextSize(AndroidUtilities.dp(14.0f));
        this.giftTextPaint.setTextSize(AndroidUtilities.dp(13.0f));
        TLRPC.TL_messageActionGiftCode tL_messageActionGiftCode = (TLRPC.TL_messageActionGiftCode) this.currentMessageObject.messageOwner.action;
        int i2 = tL_messageActionGiftCode.months;
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-DialogObject.getPeerDialogId(tL_messageActionGiftCode.boost_peer)));
        String str2 = chat == null ? null : chat.title;
        boolean z = tL_messageActionGiftCode.via_giveaway;
        if (tL_messageActionGiftCode.unclaimed) {
            i = R.string.BoostingUnclaimedPrize;
            str = "BoostingUnclaimedPrize";
        } else {
            i = R.string.BoostingCongratulations;
            str = "BoostingCongratulations";
        }
        String string = LocaleController.getString(str, i);
        String formatPluralString = i2 == 12 ? LocaleController.formatPluralString("BoldYears", 1, new Object[0]) : LocaleController.formatPluralString("BoldMonths", i2, new Object[0]);
        if (!z) {
            spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(str2 == null ? LocaleController.getString("BoostingReceivedGiftNoName", R.string.BoostingReceivedGiftNoName) : LocaleController.formatString("BoostingReceivedGiftFrom", R.string.BoostingReceivedGiftFrom, str2)));
            spannableStringBuilder.append((CharSequence) "\n\n");
            formatString = LocaleController.formatString("BoostingReceivedGiftDuration", R.string.BoostingReceivedGiftDuration, formatPluralString);
        } else if (tL_messageActionGiftCode.unclaimed) {
            spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString("BoostingYouHaveUnclaimedPrize", R.string.BoostingYouHaveUnclaimedPrize, str2)));
            spannableStringBuilder.append((CharSequence) "\n\n");
            formatString = LocaleController.formatString("BoostingUnclaimedPrizeDuration", R.string.BoostingUnclaimedPrizeDuration, formatPluralString);
        } else {
            spannableStringBuilder = new SpannableStringBuilder(AndroidUtilities.replaceTags(LocaleController.formatString("BoostingReceivedPrizeFrom", R.string.BoostingReceivedPrizeFrom, str2)));
            spannableStringBuilder.append((CharSequence) "\n\n");
            formatString = LocaleController.formatString("BoostingReceivedPrizeDuration", R.string.BoostingReceivedPrizeDuration, formatPluralString);
        }
        spannableStringBuilder.append((CharSequence) AndroidUtilities.replaceTags(formatString));
        String string2 = LocaleController.getString("BoostingReceivedGiftOpenBtn", R.string.BoostingReceivedGiftOpenBtn);
        SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(string);
        valueOf.setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, valueOf.length(), 33);
        TextPaint textPaint = this.giftTitlePaint;
        Layout.Alignment alignment = Layout.Alignment.ALIGN_CENTER;
        this.giftPremiumTitleLayout = new StaticLayout(valueOf, textPaint, dp, alignment, 1.1f, 0.0f, false);
        this.giftPremiumSubtitleLayout = null;
        TextLayout textLayout = this.giftPremiumText;
        if (textLayout != null) {
            textLayout.detach();
        }
        TextLayout textLayout2 = new TextLayout();
        this.giftPremiumText = textLayout2;
        textLayout2.setText(spannableStringBuilder, this.giftTextPaint, dp);
        SpannableStringBuilder valueOf2 = SpannableStringBuilder.valueOf(string2);
        valueOf2.setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, valueOf2.length(), 33);
        this.giftPremiumTextCollapsed = false;
        this.giftPremiumTextCollapsedHeight = 0;
        this.giftPremiumTextMore = null;
        StaticLayout staticLayout = new StaticLayout(valueOf2, (TextPaint) getThemedPaint("paintChatActionText"), dp, alignment, 1.0f, 0.0f, false);
        this.giftPremiumButtonLayout = staticLayout;
        this.buttonClickableAsImage = true;
        this.giftPremiumButtonWidth = measureLayoutWidth(staticLayout);
    }

    private void createGiftPremiumLayouts(CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, boolean z, CharSequence charSequence4, int i, CharSequence charSequence5, int i2, boolean z2) {
        int i3;
        float f;
        int cutInFancyHalf;
        int i4;
        CharSequence charSequence6 = charSequence3;
        int dp = i2 - AndroidUtilities.dp(16.0f);
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.type == 30) {
            dp -= AndroidUtilities.dp(16.0f);
        }
        if (charSequence != null) {
            MessageObject messageObject2 = this.currentMessageObject;
            if (messageObject2 == null || messageObject2.type != 30) {
                this.giftTitlePaint.setTextSize(AndroidUtilities.dp(16.0f));
            } else {
                this.giftTitlePaint.setTextSize(AndroidUtilities.dp(14.0f));
            }
            SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(charSequence);
            valueOf.setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, valueOf.length(), 33);
            this.giftPremiumTitleLayout = new StaticLayout(valueOf, this.giftTitlePaint, dp, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        } else {
            this.giftPremiumTitleLayout = null;
        }
        if (charSequence2 != null) {
            this.giftSubtitlePaint.setTextSize(AndroidUtilities.dp(13.0f));
            this.giftPremiumSubtitleLayout = new StaticLayout(charSequence2, this.giftSubtitlePaint, dp, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        } else {
            this.giftPremiumSubtitleLayout = null;
        }
        if (this.currentMessageObject == null || !(isNewStyleButtonLayout() || (i4 = this.currentMessageObject.type) == 30 || i4 == 18)) {
            this.giftTextPaint.setTextSize(AndroidUtilities.dp(15.0f));
        } else {
            this.giftTextPaint.setTextSize(AndroidUtilities.dp(13.0f));
        }
        int dp2 = dp - AndroidUtilities.dp(12.0f);
        MessageObject messageObject3 = this.currentMessageObject;
        if (messageObject3 != null && messageObject3.type == 22 && messageObject3.getDialogId() >= 0 && (cutInFancyHalf = HintView2.cutInFancyHalf(charSequence6, this.giftTextPaint)) < dp2 && cutInFancyHalf > dp2 / 5.0f) {
            dp2 = cutInFancyHalf;
        }
        if (charSequence6 == null) {
            TextLayout textLayout = this.giftPremiumText;
            if (textLayout != null) {
                textLayout.detach();
                this.giftPremiumText = null;
            }
            this.giftPremiumTextCollapsed = false;
            i3 = 0;
        } else {
            if (this.giftPremiumText == null) {
                this.giftPremiumText = new TextLayout();
            }
            try {
                charSequence6 = Emoji.replaceEmoji(charSequence6, this.giftTextPaint.getFontMetricsInt(), false);
            } catch (Exception unused) {
            }
            this.giftPremiumText.setText(charSequence6, this.giftTextPaint, dp2);
            if (!z || this.giftPremiumText.layout.getLineCount() <= 3) {
                i3 = 0;
                this.giftPremiumTextCollapsed = false;
                this.giftPremiumTextExpandedAnimated.set(true, true);
                this.giftPremiumTextCollapsedHeight = 0;
            } else {
                this.giftPremiumTextCollapsed = !this.giftPremiumTextUncollapsed;
                this.giftPremiumTextCollapsedHeight = this.giftPremiumText.layout.getLineBottom(2);
                this.giftPremiumTextMore = new Text(LocaleController.getString(R.string.Gift2CaptionMore), this.giftTextPaint.getTextSize() / AndroidUtilities.density, AndroidUtilities.bold());
                int lineBottom = this.giftPremiumText.layout.getLineBottom(2);
                this.giftPremiumTextMoreY = lineBottom;
                this.giftPremiumTextMoreH = lineBottom - this.giftPremiumText.layout.getLineTop(2);
                this.giftPremiumTextMoreX = (int) this.giftPremiumText.layout.getLineRight(2);
                i3 = 0;
            }
            if (this.giftPremiumTextCollapsed) {
                this.giftPremiumText.setText(charSequence6.subSequence(i3, this.giftPremiumText.layout.getLineEnd(2) - 1), this.giftTextPaint, dp2);
            }
        }
        if (charSequence4 != null) {
            SpannableStringBuilder valueOf2 = SpannableStringBuilder.valueOf(charSequence4);
            valueOf2.setSpan(new TypefaceSpan(AndroidUtilities.bold()), i3, valueOf2.length(), 33);
            StaticLayout staticLayout = new StaticLayout(valueOf2, (TextPaint) getThemedPaint("paintChatActionText"), dp, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            this.giftPremiumButtonLayout = staticLayout;
            this.buttonClickableAsImage = z2 && !this.giftPremiumTextCollapsed;
            f = measureLayoutWidth(staticLayout);
        } else {
            this.giftPremiumButtonLayout = null;
            this.buttonClickableAsImage = false;
            f = 0.0f;
        }
        this.giftPremiumButtonWidth = f;
        if (charSequence5 == null) {
            this.giftRibbonPath = null;
            this.giftRibbonText = null;
            return;
        }
        if (this.giftRibbonPaintEffect == null) {
            this.giftRibbonPaintEffect = new CornerPathEffect(AndroidUtilities.dp(5.0f));
        }
        if (this.giftRibbonPath == null) {
            Path path = new Path();
            this.giftRibbonPath = path;
            GiftSheet.RibbonDrawable.fillRibbonPath(path, 1.35f);
        }
        Text text = new Text(charSequence5, i, AndroidUtilities.bold());
        this.giftRibbonText = text;
        text.ellipsize(AndroidUtilities.dp(62.0f));
    }

    private void createLayout(CharSequence charSequence, int i) {
        ChatActionCellDelegate chatActionCellDelegate;
        int dp = i - AndroidUtilities.dp(30.0f);
        if (dp < 0) {
            return;
        }
        int i2 = this.overriddenMaxWidth;
        if (i2 > 0) {
            dp = Math.min(i2, dp);
        }
        this.invalidatePath = true;
        MessageObject messageObject = this.currentMessageObject;
        TextPaint textPaint = (TextPaint) getThemedPaint((messageObject == null || !messageObject.drawServiceWithDefaultTypeface) ? "paintChatActionText" : "paintChatActionText2");
        textPaint.linkColor = textPaint.getColor();
        this.textLayout = new StaticLayout(charSequence, textPaint, dp, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
        this.animatedEmojiStack = AnimatedEmojiSpan.update(0, this, (!this.canDrawInParent || (chatActionCellDelegate = this.delegate) == null || chatActionCellDelegate.canDrawOutboundsContent()) ? false : true, this.animatedEmojiStack, this.textLayout);
        this.textHeight = 0;
        this.textWidth = 0;
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2 == null || !messageObject2.isRepostPreview) {
            try {
                int lineCount = this.textLayout.getLineCount();
                for (int i3 = 0; i3 < lineCount; i3++) {
                    try {
                        float lineWidth = this.textLayout.getLineWidth(i3);
                        float f = dp;
                        if (lineWidth > f) {
                            lineWidth = f;
                        }
                        this.textHeight = (int) Math.max(this.textHeight, Math.ceil(this.textLayout.getLineBottom(i3)));
                        this.textWidth = (int) Math.max(this.textWidth, Math.ceil(lineWidth));
                    } catch (Exception e) {
                        FileLog.e(e);
                        return;
                    }
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        this.textX = (i - this.textWidth) / 2;
        this.textY = AndroidUtilities.dp(7.0f);
        this.textXLeft = (i - this.textLayout.getWidth()) / 2;
        this.spoilersPool.addAll(this.spoilers);
        this.spoilers.clear();
        if (charSequence instanceof Spannable) {
            StaticLayout staticLayout = this.textLayout;
            int i4 = this.textX;
            SpoilerEffect.addSpoilers(this, staticLayout, i4, i4 + this.textWidth, (Spannable) charSequence, this.spoilersPool, this.spoilers, null);
        }
    }

    private ColorFilter getAdaptiveEmojiColorFilter(int i) {
        if (i != this.adaptiveEmojiColor || this.adaptiveEmojiColorFilter == null) {
            this.adaptiveEmojiColor = i;
            this.adaptiveEmojiColorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN);
        }
        return this.adaptiveEmojiColorFilter;
    }

    private int getImageSize(MessageObject messageObject) {
        return (messageObject.type == 21 || isNewStyleButtonLayout()) ? AndroidUtilities.dp(78.0f) : this.stickerSize;
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.themeDelegate);
    }

    private float getUploadingInfoProgress(MessageObject messageObject) {
        MessagesController messagesController;
        String str;
        if (messageObject == null) {
            return 1.0f;
        }
        try {
            if (messageObject.type == 22 && (str = (messagesController = MessagesController.getInstance(this.currentAccount)).uploadingWallpaper) != null && TextUtils.equals(messageObject.messageOwner.action.wallpaper.uploadingImage, str)) {
                return messagesController.uploadingWallpaperInfo.uploadingProgress;
            }
            return 1.0f;
        } catch (Exception e) {
            FileLog.e(e);
            return 1.0f;
        }
    }

    private boolean isButtonLayout(MessageObject messageObject) {
        int i;
        return messageObject != null && ((i = messageObject.type) == 30 || i == 18 || i == 25 || isNewStyleButtonLayout());
    }

    private boolean isGiftChannel(MessageObject messageObject) {
        return messageObject != null && messageObject.type == 25;
    }

    private boolean isGiftCode() {
        MessageObject messageObject = this.currentMessageObject;
        return messageObject != null && (messageObject.messageOwner.action instanceof TLRPC.TL_messageActionGiftCode);
    }

    private boolean isNewStyleButtonLayout() {
        MessageObject messageObject;
        int i;
        return this.starGiftLayout.has() || (i = (messageObject = this.currentMessageObject).type) == 21 || i == 22 || messageObject.isStoryMention();
    }

    private boolean isSelfGiftCode() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return false;
        }
        TLRPC.Message message = messageObject.messageOwner;
        TLRPC.MessageAction messageAction = message.action;
        if (((messageAction instanceof TLRPC.TL_messageActionGiftCode) || (messageAction instanceof TLRPC.TL_messageActionGiftStars)) && (message.from_id instanceof TLRPC.TL_peerUser)) {
            return UserObject.isUserSelf(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.currentMessageObject.messageOwner.from_id.user_id)));
        }
        return false;
    }

    public void lambda$new$0(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
        RLottieDrawable lottieAnimation;
        ChatActionCellDelegate chatActionCellDelegate;
        if (!z || (lottieAnimation = this.imageReceiver.getLottieAnimation()) == null) {
            return;
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.playedGiftAnimation) {
            if (lottieAnimation.getCurrentFrame() < 1) {
                lottieAnimation.stop();
                lottieAnimation.setCurrentFrame(lottieAnimation.getFramesCount() - 1, false);
                return;
            }
            return;
        }
        messageObject.playedGiftAnimation = true;
        lottieAnimation.setCurrentFrame(0, false);
        AndroidUtilities.runOnUIThread(new ChatActionCell$$ExternalSyntheticLambda7(lottieAnimation));
        if (messageObject.wasUnread || this.forceWasUnread) {
            messageObject.wasUnread = false;
            this.forceWasUnread = false;
            try {
                performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (getContext() instanceof LaunchActivity) {
                ((LaunchActivity) getContext()).getFireworksOverlay().start();
            }
            TLRPC.VideoSize videoSize = this.giftEffectAnimation;
            if (videoSize == null || (chatActionCellDelegate = this.delegate) == null) {
                return;
            }
            chatActionCellDelegate.needShowEffectOverlay(this, this.giftSticker, videoSize);
        }
    }

    public void lambda$onTouchEvent$1() {
        this.isSpoilerRevealing = false;
        getMessageObject().isSpoilersRevealed = true;
        List list = this.giftPremiumText.spoilers;
        if (list != null) {
            list.clear();
        }
        invalidate();
    }

    public void lambda$onTouchEvent$2() {
        post(new Runnable() {
            @Override
            public final void run() {
                ChatActionCell.this.lambda$onTouchEvent$1();
            }
        });
    }

    public void lambda$openPremiumGiftChannel$3(TLRPC.TL_messageActionGiftCode tL_messageActionGiftCode) {
        this.delegate.didOpenPremiumGiftChannel(this, tL_messageActionGiftCode.slug, false);
    }

    public void lambda$openPremiumGiftPreview$4(TLRPC.TL_premiumGiftOption tL_premiumGiftOption, String str) {
        this.delegate.didOpenPremiumGift(this, tL_premiumGiftOption, str, false);
    }

    private float measureLayoutWidth(Layout layout) {
        float f = 0.0f;
        for (int i = 0; i < layout.getLineCount(); i++) {
            float ceil = (int) Math.ceil(layout.getLineWidth(i));
            if (ceil > f) {
                f = ceil;
            }
        }
        return f;
    }

    public void openLink(CharacterStyle characterStyle) {
        if (this.delegate == null || !(characterStyle instanceof URLSpan)) {
            return;
        }
        String url = ((URLSpan) characterStyle).getURL();
        if (url.startsWith("topic")) {
            URLSpan uRLSpan = this.pressedLink;
            if (uRLSpan instanceof URLSpanNoUnderline) {
                TLObject object = ((URLSpanNoUnderline) uRLSpan).getObject();
                if (object instanceof TLRPC.TL_forumTopic) {
                    ForumUtilities.openTopic(this.delegate.getBaseFragment(), -this.delegate.getDialogId(), (TLRPC.TL_forumTopic) object, 0);
                    return;
                }
                return;
            }
        }
        if (url.startsWith("invite")) {
            URLSpan uRLSpan2 = this.pressedLink;
            if (uRLSpan2 instanceof URLSpanNoUnderline) {
                TLObject object2 = ((URLSpanNoUnderline) uRLSpan2).getObject();
                if (object2 instanceof TLRPC.TL_chatInviteExported) {
                    this.delegate.needOpenInviteLink((TLRPC.TL_chatInviteExported) object2);
                    return;
                }
                return;
            }
        }
        if (url.startsWith("game")) {
            this.delegate.didPressReplyMessage(this, this.currentMessageObject.getReplyMsgId());
        } else if (url.startsWith("http")) {
            Browser.openUrl(getContext(), url);
        } else {
            this.delegate.needOpenUserProfile(Long.parseLong(url));
        }
    }

    private void openPremiumGiftChannel() {
        if (this.delegate != null) {
            final TLRPC.TL_messageActionGiftCode tL_messageActionGiftCode = (TLRPC.TL_messageActionGiftCode) this.currentMessageObject.messageOwner.action;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatActionCell.this.lambda$openPremiumGiftChannel$3(tL_messageActionGiftCode);
                }
            });
        }
    }

    private void openPremiumGiftPreview() {
        final TLRPC.TL_premiumGiftOption tL_premiumGiftOption = new TLRPC.TL_premiumGiftOption();
        TLRPC.MessageAction messageAction = this.currentMessageObject.messageOwner.action;
        tL_premiumGiftOption.amount = messageAction.amount;
        tL_premiumGiftOption.months = messageAction.months;
        tL_premiumGiftOption.currency = messageAction.currency;
        final String str = (!isGiftCode() || isSelfGiftCode()) ? null : ((TLRPC.TL_messageActionGiftCode) this.currentMessageObject.messageOwner.action).slug;
        if (this.delegate != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatActionCell.this.lambda$openPremiumGiftPreview$4(tL_premiumGiftOption, str);
                }
            });
        }
    }

    private void openStarsGiftTransaction() {
        TLRPC.Message message;
        StarGiftSheet starGiftSheet;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || (message = messageObject.messageOwner) == null) {
            return;
        }
        TLRPC.MessageAction messageAction = message.action;
        if (messageAction instanceof TLRPC.TL_messageActionGiftStars) {
            Context context = getContext();
            int i = this.currentAccount;
            TLRPC.Message message2 = this.currentMessageObject.messageOwner;
            StarsIntroActivity.showTransactionSheet(context, i, message2.date, message2.from_id, message2.peer_id, (TLRPC.TL_messageActionGiftStars) message2.action, this.avatarStoryParams.resourcesProvider);
            return;
        }
        if (messageAction instanceof TLRPC.TL_messageActionPrizeStars) {
            Context context2 = getContext();
            int i2 = this.currentAccount;
            TLRPC.Message message3 = this.currentMessageObject.messageOwner;
            StarsIntroActivity.showTransactionSheet(context2, i2, message3.date, message3.from_id, message3.peer_id, (TLRPC.TL_messageActionPrizeStars) message3.action, this.avatarStoryParams.resourcesProvider);
            return;
        }
        if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
            if (((TLRPC.TL_messageActionStarGift) messageAction).forceIn) {
                return;
            } else {
                starGiftSheet = new StarGiftSheet(getContext(), this.currentAccount, this.currentMessageObject.getDialogId(), this.themeDelegate);
            }
        } else if (!(messageAction instanceof TLRPC.TL_messageActionStarGiftUnique)) {
            return;
        } else {
            starGiftSheet = new StarGiftSheet(getContext(), this.currentAccount, this.currentMessageObject.getDialogId(), this.themeDelegate);
        }
        starGiftSheet.set(this.currentMessageObject).show();
    }

    private void setStarsPaused(boolean z) {
        StarParticlesView.Drawable drawable = this.starParticlesDrawable;
        if (z == drawable.paused) {
            return;
        }
        drawable.paused = z;
        if (z) {
            drawable.pausedTime = System.currentTimeMillis();
            return;
        }
        for (int i = 0; i < this.starParticlesDrawable.particles.size(); i++) {
            ((StarParticlesView.Drawable.Particle) this.starParticlesDrawable.particles.get(i)).lifeTime += System.currentTimeMillis() - this.starParticlesDrawable.pausedTime;
        }
        invalidate();
    }

    private void updateTextInternal(boolean z) {
        if (getMeasuredWidth() != 0) {
            createLayout(this.customText, getMeasuredWidth());
            invalidate();
        }
        if (this.wasLayout) {
            buildLayout();
        } else if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatActionCell.this.requestLayout();
                }
            });
        } else {
            requestLayout();
        }
    }

    public boolean checkUnreadReactions(float f, int i) {
        if (!this.reactionsLayoutInBubble.hasUnreadReactions) {
            return false;
        }
        float y = getY();
        float f2 = y + r2.y;
        return f2 > f && (f2 + ((float) this.reactionsLayoutInBubble.height)) - ((float) AndroidUtilities.dp(16.0f)) < ((float) i);
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
        if (i == NotificationCenter.didUpdatePremiumGiftStickers || i == NotificationCenter.starGiftsLoaded) {
            messageObject = this.currentMessageObject;
            if (messageObject == null) {
                return;
            }
        } else if (i != NotificationCenter.diceStickersDidLoad || !Objects.equals(objArr[0], UserConfig.getInstance(this.currentAccount).premiumGiftsStickerPack) || (messageObject = this.currentMessageObject) == null) {
            return;
        }
        setMessageObject(messageObject, true);
    }

    public void drawBackground(android.graphics.Canvas r29, boolean r30) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.drawBackground(android.graphics.Canvas, boolean):void");
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        if (view != this.rippleView) {
            return super.drawChild(canvas, view, j);
        }
        float scale = this.bounce.getScale(0.02f);
        canvas.save();
        canvas.scale(scale, scale, view.getX() + (view.getMeasuredWidth() / 2.0f), view.getY() + (view.getMeasuredHeight() / 2.0f));
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restore();
        return drawChild;
    }

    public void drawOutboundsContent(Canvas canvas) {
        canvas.save();
        canvas.translate(this.textXLeft, this.textY);
        StaticLayout staticLayout = this.textLayout;
        AnimatedEmojiSpan.drawAnimatedEmojis(canvas, staticLayout, this.animatedEmojiStack, 0.0f, this.spoilers, 0.0f, 0.0f, 0.0f, 1.0f, staticLayout != null ? getAdaptiveEmojiColorFilter(staticLayout.getPaint().getColor()) : null);
        canvas.restore();
        if (this.starGiftLayout.has()) {
            canvas.save();
            canvas.translate((getWidth() - this.starGiftLayout.getWidth()) / 2.0f, this.starGiftLayout.repost ? AndroidUtilities.dp(4.0f) : this.textY + this.textHeight + AndroidUtilities.dp(16.0f));
            this.starGiftLayout.drawOutbounds(canvas);
            canvas.restore();
        }
    }

    public void drawReactions(Canvas canvas, boolean z, Integer num) {
        if (this.canDrawInParent) {
            if (hasGradientService() && !z) {
                return;
            }
            if (!hasGradientService() && z) {
                return;
            }
        }
        drawReactionsLayout(canvas, z, num);
    }

    public void drawReactionsLayout(Canvas canvas, boolean z, Integer num) {
        float alpha = z ? getAlpha() : 1.0f;
        Theme.ResourcesProvider resourcesProvider = this.themeDelegate;
        if (resourcesProvider != null) {
            resourcesProvider.applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, this.viewTranslationX, this.viewTop + AndroidUtilities.dp(4.0f));
        } else {
            Theme.applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, this.viewTranslationX, this.viewTop + AndroidUtilities.dp(4.0f));
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || !messageObject.shouldDrawReactions()) {
            return;
        }
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        if (!reactionsLayoutInBubble.isSmall || (this.transitionParams.animateChange && reactionsLayoutInBubble.animateHeight)) {
            reactionsLayoutInBubble.drawServiceShaderBackground = 1.0f;
            if (alpha < 1.0f) {
                canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) (255.0f * alpha), 31);
            }
            ReactionsLayoutInBubble reactionsLayoutInBubble2 = this.reactionsLayoutInBubble;
            TransitionParams transitionParams = this.transitionParams;
            reactionsLayoutInBubble2.draw(canvas, transitionParams.animateChange ? transitionParams.animateChangeProgress : 1.0f, num);
            if (alpha < 1.0f) {
                canvas.restore();
            }
        }
    }

    public void drawReactionsLayoutOverlay(Canvas canvas, boolean z) {
        float alpha = z ? getAlpha() : 1.0f;
        Theme.ResourcesProvider resourcesProvider = this.themeDelegate;
        if (resourcesProvider != null) {
            resourcesProvider.applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, this.viewTranslationX, this.viewTop + AndroidUtilities.dp(4.0f));
        } else {
            Theme.applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, this.viewTranslationX, this.viewTop + AndroidUtilities.dp(4.0f));
        }
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || !messageObject.shouldDrawReactions()) {
            return;
        }
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        if (!reactionsLayoutInBubble.isSmall || (this.transitionParams.animateChange && reactionsLayoutInBubble.animateHeight)) {
            reactionsLayoutInBubble.drawServiceShaderBackground = 1.0f;
            if (alpha < 1.0f) {
                canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) (255.0f * alpha), 31);
            }
            ReactionsLayoutInBubble reactionsLayoutInBubble2 = this.reactionsLayoutInBubble;
            TransitionParams transitionParams = this.transitionParams;
            reactionsLayoutInBubble2.drawOverlay(canvas, transitionParams.animateChange ? transitionParams.animateChangeProgress : 1.0f);
            if (alpha < 1.0f) {
                canvas.restore();
            }
        }
    }

    public void drawScrimReaction(Canvas canvas, Integer num, float f, boolean z) {
        if (this.reactionsLayoutInBubble.isSmall) {
            return;
        }
        Theme.ResourcesProvider resourcesProvider = this.themeDelegate;
        if (resourcesProvider != null) {
            resourcesProvider.applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, this.viewTranslationX, this.viewTop + AndroidUtilities.dp(4.0f));
        } else {
            Theme.applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, this.viewTranslationX, this.viewTop + AndroidUtilities.dp(4.0f));
        }
        this.reactionsLayoutInBubble.setScrimProgress(f, z);
        this.reactionsLayoutInBubble.draw(canvas, this.transitionParams.animateChangeProgress, num);
    }

    public void drawScrimReactionPreview(View view, Canvas canvas, int i, Integer num, float f) {
        if (this.reactionsLayoutInBubble.isSmall) {
            return;
        }
        Theme.ResourcesProvider resourcesProvider = this.themeDelegate;
        if (resourcesProvider != null) {
            resourcesProvider.applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, this.viewTranslationX, this.viewTop + AndroidUtilities.dp(4.0f));
        } else {
            Theme.applyServiceShaderMatrix(getMeasuredWidth(), this.backgroundHeight, this.viewTranslationX, this.viewTop + AndroidUtilities.dp(4.0f));
        }
        this.reactionsLayoutInBubble.setScrimProgress(f);
        this.reactionsLayoutInBubble.drawPreview(view, canvas, i, num);
    }

    @Override
    public int getBoundsLeft() {
        if (this.starGiftLayout.has()) {
            int width = ((int) (getWidth() - (this.starGiftLayout.getWidth() + AndroidUtilities.dp(8.0f)))) / 2;
            return this.starGiftLayout.repost ? width : Math.min(this.backgroundLeft, width);
        }
        if (isButtonLayout(this.currentMessageObject)) {
            return (getWidth() - this.giftRectSize) / 2;
        }
        int i = this.backgroundLeft;
        ImageReceiver imageReceiver = this.imageReceiver;
        return (imageReceiver == null || !imageReceiver.getVisible()) ? i : Math.min((int) this.imageReceiver.getImageX(), i);
    }

    @Override
    public int getBoundsRight() {
        if (this.starGiftLayout.has()) {
            int width = ((int) (getWidth() + (this.starGiftLayout.getWidth() + AndroidUtilities.dp(8.0f)))) / 2;
            return this.starGiftLayout.repost ? width : Math.max(this.backgroundRight, width);
        }
        if (isButtonLayout(this.currentMessageObject)) {
            return (getWidth() + this.giftRectSize) / 2;
        }
        int i = this.backgroundRight;
        ImageReceiver imageReceiver = this.imageReceiver;
        return (imageReceiver == null || !imageReceiver.getVisible()) ? i : Math.max((int) this.imageReceiver.getImageX2(), i);
    }

    public int getCustomDate() {
        return this.customDate;
    }

    public ChatActionCellDelegate getDelegate() {
        return this.delegate;
    }

    public MessageObject getMessageObject() {
        return this.currentMessageObject;
    }

    @Override
    public int getObserverTag() {
        return this.TAG;
    }

    public ImageReceiver getPhotoImage() {
        return this.imageReceiver;
    }

    public ReactionsLayoutInBubble.ReactionButton getReactionButton(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        return this.reactionsLayoutInBubble.getReactionButton(visibleReaction);
    }

    public Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider = this.themeDelegate;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    public TransitionParams getTransitionParams() {
        return this.transitionParams;
    }

    public boolean hasButton() {
        MessageObject messageObject = this.currentMessageObject;
        return (messageObject == null || !isButtonLayout(messageObject) || this.giftPremiumButtonLayout == null) ? false : true;
    }

    public boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider;
        return this.overrideBackgroundPaint == null && ((resourcesProvider = this.themeDelegate) == null ? Theme.hasGradientService() : resourcesProvider.hasGradientService());
    }

    @Override
    public void invalidate() {
        super.invalidate();
        View view = this.invalidateWithParent;
        if (view != null) {
            view.invalidate();
        }
        Runnable runnable = this.invalidateListener;
        if (runnable != null) {
            runnable.run();
        }
        if (!this.invalidatesParent || getParent() == null) {
            return;
        }
        View view2 = (View) getParent();
        if (view2.getParent() != null) {
            view2.invalidate();
            ((View) view2.getParent()).invalidate();
        }
    }

    @Override
    public void invalidate(int i, int i2, int i3, int i4) {
        super.invalidate(i, i2, i3, i4);
        View view = this.invalidateWithParent;
        if (view != null) {
            view.invalidate();
        }
        if (!this.invalidatesParent || getParent() == null) {
            return;
        }
        View view2 = (View) getParent();
        if (view2.getParent() != null) {
            view2.invalidate();
            ((View) view2.getParent()).invalidate();
        }
    }

    @Override
    public void invalidate(Rect rect) {
        super.invalidate(rect);
        View view = this.invalidateWithParent;
        if (view != null) {
            view.invalidate();
        }
        if (!this.invalidatesParent || getParent() == null) {
            return;
        }
        View view2 = (View) getParent();
        if (view2.getParent() != null) {
            view2.invalidate();
            ((View) view2.getParent()).invalidate();
        }
    }

    public void invalidateOutbounds() {
        ChatActionCellDelegate chatActionCellDelegate = this.delegate;
        if (chatActionCellDelegate != null && chatActionCellDelegate.canDrawOutboundsContent()) {
            super.invalidate();
        } else if (getParent() instanceof View) {
            ((View) getParent()).invalidate();
        }
    }

    public boolean isCellAttachedToWindow() {
        return this.attachedToWindow;
    }

    public boolean isFloating() {
        return false;
    }

    public void markReactionsAsRead() {
        this.reactionsLayoutInBubble.hasUnreadReactions = false;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.markReactionsAsRead();
    }

    @Override
    protected void onAttachedToWindow() {
        ChatActionCellDelegate chatActionCellDelegate;
        super.onAttachedToWindow();
        this.attachedToWindow = true;
        this.imageReceiver.onAttachedToWindow();
        setStarsPaused(false);
        this.animatedEmojiStack = AnimatedEmojiSpan.update(0, this, (!this.canDrawInParent || (chatActionCellDelegate = this.delegate) == null || chatActionCellDelegate.canDrawOutboundsContent()) ? false : true, this.animatedEmojiStack, this.textLayout);
        TextLayout textLayout = this.giftPremiumText;
        if (textLayout != null) {
            textLayout.attach();
        }
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.didUpdatePremiumGiftStickers);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.starGiftsLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.type == 21) {
            setMessageObject(messageObject, true);
        }
        this.starGiftLayout.attach();
        this.reactionsLayoutInBubble.onAttachToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
        this.imageReceiver.onDetachedFromWindow();
        setStarsPaused(true);
        this.wasLayout = false;
        AnimatedEmojiSpan.release(this, this.animatedEmojiStack);
        TextLayout textLayout = this.giftPremiumText;
        if (textLayout != null) {
            textLayout.detach();
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdatePremiumGiftStickers);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starGiftsLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
        this.avatarStoryParams.onDetachFromWindow();
        this.transitionParams.onDetach();
        this.starGiftLayout.detach();
        this.reactionsLayoutInBubble.onDetachFromWindow();
    }

    @Override
    public void onDraw(android.graphics.Canvas r43) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.onDraw(android.graphics.Canvas):void");
    }

    @Override
    public void onFailedDownload(String str, boolean z) {
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        MessageObject messageObject = this.currentMessageObject;
        if (TextUtils.isEmpty(this.customText) && messageObject == null) {
            return;
        }
        if (this.accessibilityText == null) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(!TextUtils.isEmpty(this.customText) ? this.customText : messageObject.messageText);
            for (CharacterStyle characterStyle : (CharacterStyle[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ClickableSpan.class)) {
                int spanStart = spannableStringBuilder.getSpanStart(characterStyle);
                int spanEnd = spannableStringBuilder.getSpanEnd(characterStyle);
                spannableStringBuilder.removeSpan(characterStyle);
                spannableStringBuilder.setSpan(new ClickableSpan() {
                    final CharacterStyle val$link;

                    AnonymousClass1(CharacterStyle characterStyle2) {
                        r2 = characterStyle2;
                    }

                    @Override
                    public void onClick(View view) {
                        if (ChatActionCell.this.delegate != null) {
                            ChatActionCell.this.openLink(r2);
                        }
                    }
                }, spanStart, spanEnd, 33);
            }
            this.accessibilityText = spannableStringBuilder;
        }
        if (Build.VERSION.SDK_INT < 24) {
            accessibilityNodeInfo.setContentDescription(this.accessibilityText.toString());
        } else {
            accessibilityNodeInfo.setText(this.accessibilityText);
        }
        accessibilityNodeInfo.setEnabled(true);
    }

    @Override
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        View view = this.rippleView;
        RectF rectF = this.giftButtonRect;
        view.layout((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
    }

    @Override
    protected boolean onLongPress() {
        ChatActionCellDelegate chatActionCellDelegate = this.delegate;
        if (chatActionCellDelegate != null) {
            return chatActionCellDelegate.didLongPress(this, this.lastTouchX, this.lastTouchY);
        }
        return false;
    }

    @Override
    protected void onMeasure(int r21, int r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.onMeasure(int, int):void");
    }

    @Override
    public void onProgressDownload(String str, long j, long j2) {
    }

    @Override
    public void onProgressUpload(String str, long j, long j2, boolean z) {
    }

    @Override
    public void onSuccessDownload(String str) {
        TLRPC.PhotoSize photoSize;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.type != 11) {
            return;
        }
        int size = messageObject.photoThumbs.size();
        int i = 0;
        while (true) {
            if (i >= size) {
                photoSize = null;
                break;
            }
            photoSize = messageObject.photoThumbs.get(i);
            if (photoSize instanceof TLRPC.TL_photoStrippedSize) {
                break;
            } else {
                i++;
            }
        }
        this.imageReceiver.setImage(this.currentVideoLocation, "g", ImageLocation.getForObject(photoSize, messageObject.photoThumbsObject), "50_50_b", this.avatarDrawable, 0L, null, messageObject, 1);
        DownloadController.getInstance(this.currentAccount).removeLoadingFileObserver(this);
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void setCustomDate(int i, boolean z, boolean z2) {
        int i2 = this.customDate;
        if (i2 == i || i2 / 3600 == i / 3600) {
            return;
        }
        String string = z ? i == 2147483646 ? LocaleController.getString("MessageScheduledUntilOnline", R.string.MessageScheduledUntilOnline) : LocaleController.formatString("MessageScheduledOn", R.string.MessageScheduledOn, LocaleController.formatDateChat(i)) : LocaleController.formatDateChat(i);
        this.customDate = i;
        CharSequence charSequence = this.customText;
        if (charSequence == null || !TextUtils.equals(string, charSequence)) {
            this.customText = string;
            this.accessibilityText = null;
            updateTextInternal(z2);
        }
    }

    public void setCustomText(CharSequence charSequence) {
        this.customText = charSequence;
        if (charSequence != null) {
            updateTextInternal(false);
        }
    }

    public void setDelegate(ChatActionCellDelegate chatActionCellDelegate) {
        this.delegate = chatActionCellDelegate;
    }

    public void setInvalidateColors(boolean z) {
        if (this.invalidateColors == z) {
            return;
        }
        this.invalidateColors = z;
        invalidate();
    }

    public void setInvalidateListener(Runnable runnable) {
        this.invalidateListener = runnable;
    }

    public void setInvalidateWithParent(View view) {
        this.invalidateWithParent = view;
    }

    public void setInvalidatesParent(boolean z) {
        this.invalidatesParent = z;
    }

    public void setMessageObject(MessageObject messageObject) {
        setMessageObject(messageObject, false);
    }

    public void setMessageObject(org.telegram.messenger.MessageObject r29, boolean r30) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatActionCell.setMessageObject(org.telegram.messenger.MessageObject, boolean):void");
    }

    public void setOverrideColor(int i, int i2) {
        this.overrideBackground = i;
        this.overrideText = i2;
    }

    public void setOverrideTextMaxWidth(int i) {
        this.overriddenMaxWidth = i;
    }

    public void setScrimReaction(Integer num) {
        this.reactionsLayoutInBubble.setScrimReaction(num);
    }

    public void setSpoilersSuppressed(boolean z) {
        Iterator it = this.spoilers.iterator();
        while (it.hasNext()) {
            ((SpoilerEffect) it.next()).setSuppressUpdates(z);
        }
    }

    public void setVisiblePart(float f, float f2, int i, float f3) {
        this.visiblePartSet = true;
        this.backgroundHeight = i;
        this.viewTop = f;
        this.viewTranslationX = f2;
        this.dimAmount = f3;
        this.dimPaint.setColor(ColorUtils.setAlphaComponent(-16777216, (int) (f3 * 255.0f)));
        invalidate();
    }

    public void setVisiblePart(float f, int i) {
        this.visiblePartSet = true;
        this.backgroundHeight = i;
        this.viewTop = f;
        this.viewTranslationX = 0.0f;
    }

    public boolean showingCancelButton() {
        RadialProgress2 radialProgress2 = this.radialProgress;
        return radialProgress2 != null && radialProgress2.getIcon() == 3;
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        return drawable == this.wallpaperPreviewDrawable || super.verifyDrawable(drawable);
    }
}
