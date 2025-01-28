package org.telegram.messenger;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.util.Base64;
import androidx.collection.LongSparseArray;
import androidx.core.graphics.ColorUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.CodeHighlighting;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.ringtone.RingtoneDataStore;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Business.QuickRepliesController;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.QuoteSpan;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.Reactions.ReactionsUtils;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Components.TranscribeButton;
import org.telegram.ui.Components.URLSpanNoUnderlineBold;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.WebPlayerView;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.StoriesController;

public class MessageObject {
    public static final int ENTITIES_ALL = 0;
    public static final int ENTITIES_ONLY_HASHTAGS = 1;
    private static final int LINES_PER_BLOCK = 10;
    private static final int LINES_PER_BLOCK_WITH_EMOJI = 5;
    public static final int MESSAGE_SEND_STATE_EDITING = 3;
    public static final int MESSAGE_SEND_STATE_SENDING = 1;
    public static final int MESSAGE_SEND_STATE_SEND_ERROR = 2;
    public static final int MESSAGE_SEND_STATE_SENT = 0;
    public static final int POSITION_FLAG_BOTTOM = 8;
    public static final int POSITION_FLAG_LEFT = 1;
    public static final int POSITION_FLAG_RIGHT = 2;
    public static final int POSITION_FLAG_TOP = 4;
    public static final int TYPE_ACTION_PHOTO = 11;
    public static final int TYPE_ACTION_WALLPAPER = 22;
    public static final int TYPE_ANIMATED_STICKER = 15;
    public static final int TYPE_CONTACT = 12;
    public static final int TYPE_DATE = 10;
    public static final int TYPE_EMOJIS = 19;
    public static final int TYPE_EXTENDED_MEDIA_PREVIEW = 20;
    public static final int TYPE_FILE = 9;
    public static final int TYPE_GEO = 4;
    public static final int TYPE_GIF = 8;
    public static final int TYPE_GIFT_PREMIUM = 18;
    public static final int TYPE_GIFT_PREMIUM_CHANNEL = 25;
    public static final int TYPE_GIFT_STARS = 30;
    public static final int TYPE_GIVEAWAY = 26;
    public static final int TYPE_GIVEAWAY_RESULTS = 28;
    public static final int TYPE_JOINED_CHANNEL = 27;
    public static final int TYPE_LOADING = 6;
    public static final int TYPE_MUSIC = 14;
    public static final int TYPE_PAID_MEDIA = 29;
    public static final int TYPE_PHONE_CALL = 16;
    public static final int TYPE_PHOTO = 1;
    public static final int TYPE_POLL = 17;
    public static final int TYPE_ROUND_VIDEO = 5;
    public static final int TYPE_STICKER = 13;
    public static final int TYPE_STORY = 23;
    public static final int TYPE_STORY_MENTION = 24;
    public static final int TYPE_SUGGEST_PHOTO = 21;
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_VIDEO = 3;
    public static final int TYPE_VOICE = 2;
    private static CharSequence channelSpan;
    static final String[] excludeWords = {" vs. ", " vs ", " versus ", " ft. ", " ft ", " featuring ", " feat. ", " feat ", " presents ", " pres. ", " pres ", " and ", " & ", " . "};
    private static CharSequence groupSpan;
    public static Pattern instagramUrlPattern;
    private static Pattern loginCodePattern;
    public static Pattern urlPattern;
    private static CharSequence[] userSpan;
    public static Pattern videoTimeUrlPattern;
    public long actionDeleteGroupEventId;
    public boolean animateComments;
    public int animatedEmojiCount;
    public boolean attachPathExists;
    public double attributeDuration;
    public int audioPlayerDuration;
    public float audioProgress;
    public int audioProgressMs;
    public int audioProgressSec;
    public StringBuilder botButtonsLayout;
    public float bufferedProgress;
    public boolean business;
    public Boolean cachedIsSupergroup;
    public VideoPlayer.VideoUri cachedQuality;
    public Float cachedSavedTimestamp;
    private Integer cachedStartsTimestamp;
    public boolean cancelEditing;
    public CharSequence caption;
    private boolean captionTranslated;
    private boolean channelJoined;
    public boolean channelJoinedExpanded;
    public ArrayList<TLRPC.PollAnswer> checkedVotes;
    public int contentType;
    public int currentAccount;
    public TLRPC.TL_channelAdminLogEvent currentEvent;
    public Drawable customAvatarDrawable;
    public String customName;
    public String customReplyName;
    public String dateKey;
    public int dateKeyInt;
    public boolean deleted;
    public boolean deletedByThanos;
    public boolean drawServiceWithDefaultTypeface;
    public CharSequence editingMessage;
    public ArrayList<TLRPC.MessageEntity> editingMessageEntities;
    public boolean editingMessageSearchWebPage;
    public TLRPC.Document emojiAnimatedSticker;
    public String emojiAnimatedStickerColor;
    public Long emojiAnimatedStickerId;
    private boolean emojiAnimatedStickerLoading;
    public TLRPC.VideoSize emojiMarkup;
    public int emojiOnlyCount;
    public long eventId;
    public HashSet<Integer> expandedQuotes;
    public long extendedMediaLastCheckTime;
    public boolean factCheckExpanded;
    private CharSequence factCheckText;
    public boolean flickerLoading;
    public boolean forceAvatar;
    public boolean forceExpired;
    public boolean forcePlayEffect;
    public float forceSeekTo;
    public boolean forceUpdate;
    private float generatedWithDensity;
    private int generatedWithMinSize;
    public float gifState;
    public boolean hadAnimationNotReadyLoading;
    public boolean hasCode;
    public boolean hasCodeAtBottom;
    public boolean hasCodeAtTop;
    public boolean hasQuote;
    public boolean hasQuoteAtBottom;
    public boolean hasRtl;
    public boolean hasSingleCode;
    public boolean hasSingleQuote;
    private boolean hasUnwrappedEmoji;
    public boolean hasWideCode;
    public boolean hideSendersName;
    public VideoPlayer.VideoUri highestQuality;
    public ArrayList<String> highlightedWords;
    public boolean isDateObject;
    public boolean isDownloadingFile;
    private Boolean isEmbedVideoCached;
    public boolean isMediaSpoilersRevealed;
    public boolean isMediaSpoilersRevealedInSharedMedia;
    public Boolean isOutOwnerCached;
    public boolean isPrimaryGroupMessage;
    public boolean isReactionPush;
    public boolean isRepostPreview;
    public boolean isRepostVideoPreview;
    public boolean isRestrictedMessage;
    private int isRoundVideoCached;
    public boolean isSaved;
    public boolean isSavedFiltered;
    public Boolean isSensitiveCached;
    public boolean isSpoilersRevealed;
    public boolean isStoryMentionPush;
    public boolean isStoryPush;
    public boolean isStoryPushHidden;
    public boolean isStoryReactionPush;
    public boolean isTopicMainMessage;
    public boolean isVideoConversionObject;
    public Object lastGeoWebFileLoaded;
    public Object lastGeoWebFileSet;
    public int lastLineWidth;
    private boolean layoutCreated;
    public CharSequence linkDescription;
    public long loadedFileSize;
    public boolean loadingCancelled;
    public boolean localChannel;
    public boolean localEdit;
    public long localGroupId;
    public String localName;
    public long localSentGroupId;
    public boolean localSupergroup;
    public int localType;
    public String localUserName;
    public boolean mediaExists;
    public ImageLocation mediaSmallThumb;
    public ImageLocation mediaThumb;
    public TLRPC.Message messageOwner;
    public CharSequence messageText;
    public CharSequence messageTextForReply;
    public CharSequence messageTextShort;
    public CharSequence messageTrimmedToHighlight;
    public boolean messageTrimmedToHighlightCut;
    public String monthKey;
    public boolean notime;
    public boolean openedInViewer;
    public int overrideLinkColor;
    public long overrideLinkEmoji;
    public StoriesController.StoriesList parentStoriesList;
    public int parentWidth;
    public SvgHelper.SvgDrawable pathThumb;
    public ArrayList<TLRPC.PhotoSize> photoThumbs;
    public ArrayList<TLRPC.PhotoSize> photoThumbs2;
    public TLObject photoThumbsObject;
    public TLObject photoThumbsObject2;
    public boolean playedGiftAnimation;
    public long pollLastCheckTime;
    public boolean pollVisibleOnScreen;
    public boolean preview;
    public boolean previewForward;
    public String previousAttachPath;
    public TLRPC.MessageMedia previousMedia;
    public String previousMessage;
    public ArrayList<TLRPC.MessageEntity> previousMessageEntities;
    public boolean putInDownloadsStore;
    public TLRPC.Document qualityToSave;
    public String quick_reply_shortcut;
    private byte[] randomWaveform;
    public boolean reactionsChanged;
    public long reactionsLastCheckTime;
    public int realDate;
    public MessageObject replyMessageObject;
    public boolean replyTextEllipsized;
    public boolean replyTextRevealed;
    public TLRPC.TL_forumTopic replyToForumTopic;
    public boolean resendAsIs;
    public boolean revealingMediaSpoilers;
    public boolean scheduled;
    public boolean scheduledSent;
    public int searchType;
    private CharSequence secretOnceSpan;
    private CharSequence secretPlaySpan;
    public SendAnimationData sendAnimationData;
    public TLRPC.Peer sendAsPeer;
    public boolean sendPreview;
    public MediaController.PhotoEntry sendPreviewEntry;
    public boolean settingAvatar;
    public boolean shouldRemoveVideoEditedInfo;
    private boolean spoiledLoginCode;
    public String sponsoredAdditionalInfo;
    public String sponsoredButtonText;
    public boolean sponsoredCanReport;
    public TLRPC.TL_peerColor sponsoredColor;
    public byte[] sponsoredId;
    public String sponsoredInfo;
    public TLRPC.MessageMedia sponsoredMedia;
    public TLRPC.Photo sponsoredPhoto;
    public boolean sponsoredRecommended;
    public String sponsoredTitle;
    public String sponsoredUrl;
    public int stableId;
    public TL_stories.StoryItem storyItem;
    private TLRPC.WebPage storyMentionWebpage;
    public BitmapDrawable strippedThumb;
    public ArrayList<TextLayoutBlock> textLayoutBlocks;
    public int textWidth;
    public float textXOffset;
    public VideoPlayer.VideoUri thumbQuality;
    public Drawable[] topicIconDrawable;
    public int totalAnimatedEmojiCount;
    public boolean translated;
    public int type;
    public StoriesController.UploadingStory uploadingStory;
    public boolean useCustomPhoto;
    public CharSequence vCardData;
    public VideoEditedInfo videoEditedInfo;
    public ArrayList<VideoPlayer.Quality> videoQualities;
    private Boolean videoQualitiesCached;
    public boolean viewsReloaded;
    public int wantedBotKeyboardWidth;
    public boolean wasJustSent;
    public boolean wasUnread;
    public ArrayList<TLRPC.MessageEntity> webPageDescriptionEntities;
    public CharSequence youtubeDescription;

    public static class GroupedMessagePosition {
        public float aspectRatio;
        public boolean edge;
        public int flags;
        public boolean last;
        public float left;
        public int leftSpanOffset;
        public byte maxX;
        public byte maxY;
        public byte minX;
        public byte minY;
        public float ph;
        public int photoHeight;
        public int photoWidth;
        public int pw;
        public float[] siblingHeights;
        public int spanSize;
        public float top;

        public void set(int i, int i2, int i3, int i4, int i5, float f, int i6) {
            this.minX = (byte) i;
            this.maxX = (byte) i2;
            this.minY = (byte) i3;
            this.maxY = (byte) i4;
            this.pw = i5;
            this.spanSize = i5;
            this.ph = f;
            this.flags = (byte) i6;
        }
    }

    public static class GroupedMessages {
        public boolean captionAbove;
        public MessageObject captionMessage;
        public long groupId;
        public boolean hasCaption;
        public boolean hasSibling;
        public boolean isDocuments;
        public boolean reversed;
        public ArrayList<MessageObject> messages = new ArrayList<>();
        public ArrayList<GroupedMessagePosition> posArray = new ArrayList<>();
        public HashMap<MessageObject, GroupedMessagePosition> positions = new HashMap<>();
        public LongSparseArray positionsArray = new LongSparseArray();
        private int maxSizeWidth = 800;
        public final TransitionParams transitionParams = new TransitionParams();

        public static class MessageGroupedLayoutAttempt {
            public float[] heights;
            public int[] lineCounts;

            public MessageGroupedLayoutAttempt(int i, int i2, float f, float f2) {
                this.lineCounts = new int[]{i, i2};
                this.heights = new float[]{f, f2};
            }

            public MessageGroupedLayoutAttempt(int i, int i2, int i3, float f, float f2, float f3) {
                this.lineCounts = new int[]{i, i2, i3};
                this.heights = new float[]{f, f2, f3};
            }

            public MessageGroupedLayoutAttempt(int i, int i2, int i3, int i4, float f, float f2, float f3, float f4) {
                this.lineCounts = new int[]{i, i2, i3, i4};
                this.heights = new float[]{f, f2, f3, f4};
            }
        }

        public static class TransitionParams {
            public boolean backgroundChangeBounds;
            public int bottom;
            public float captionEnterProgress = 1.0f;
            public ChatMessageCell cell;
            public boolean drawBackgroundForDeletedItems;
            public boolean drawCaptionLayout;
            public boolean isNewGroup;
            public int left;
            public float offsetBottom;
            public float offsetLeft;
            public float offsetRight;
            public float offsetTop;
            public boolean pinnedBotton;
            public boolean pinnedTop;
            public int right;
            public int top;

            public void reset() {
                this.captionEnterProgress = 1.0f;
                this.offsetBottom = 0.0f;
                this.offsetTop = 0.0f;
                this.offsetRight = 0.0f;
                this.offsetLeft = 0.0f;
                this.backgroundChangeBounds = false;
            }
        }

        private float multiHeight(float[] fArr, int i, int i2) {
            float f = 0.0f;
            while (i < i2) {
                f += fArr[i];
                i++;
            }
            return this.maxSizeWidth / f;
        }

        public void calculate() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.GroupedMessages.calculate():void");
        }

        public boolean contains(int i) {
            if (this.messages == null) {
                return false;
            }
            for (int i2 = 0; i2 < this.messages.size(); i2++) {
                MessageObject messageObject = this.messages.get(i2);
                if (messageObject != null && messageObject.getId() == i) {
                    return true;
                }
            }
            return false;
        }

        public MessageObject findCaptionMessageObject() {
            if (!this.messages.isEmpty() && this.positions.isEmpty()) {
                calculate();
            }
            MessageObject messageObject = null;
            for (int i = 0; i < this.messages.size(); i++) {
                MessageObject messageObject2 = this.messages.get(i);
                if (!TextUtils.isEmpty(messageObject2.caption)) {
                    if (messageObject != null) {
                        return null;
                    }
                    messageObject = messageObject2;
                }
            }
            return messageObject;
        }

        public MessageObject findMessageWithFlags(int i) {
            if (!this.messages.isEmpty() && this.positions.isEmpty()) {
                calculate();
            }
            for (int i2 = 0; i2 < this.messages.size(); i2++) {
                MessageObject messageObject = this.messages.get(i2);
                GroupedMessagePosition groupedMessagePosition = this.positions.get(messageObject);
                if (groupedMessagePosition != null && (groupedMessagePosition.flags & i) == i) {
                    return messageObject;
                }
            }
            return null;
        }

        public MessageObject findPrimaryMessageObject() {
            return findMessageWithFlags(this.reversed ? 10 : 5);
        }

        public GroupedMessagePosition getPosition(MessageObject messageObject) {
            if (messageObject == null) {
                return null;
            }
            GroupedMessagePosition groupedMessagePosition = this.positions.get(messageObject);
            return groupedMessagePosition == null ? (GroupedMessagePosition) this.positionsArray.get(messageObject.getId()) : groupedMessagePosition;
        }
    }

    public static class SendAnimationData {
        public float currentScale;
        public float currentX;
        public float currentY;
        public ChatMessageCell.TransitionParams fromParams;
        public boolean fromPreview;
        public float height;
        public float progress;
        public float timeAlpha;
        public float width;
        public float x;
        public float y;
    }

    public static class TextLayoutBlock {
        public static final int FLAG_NOT_RTL = 2;
        public static final int FLAG_RTL = 1;
        public int charactersEnd;
        public int charactersOffset;
        public boolean code;
        public ButtonBounce collapsedBounce;
        public int collapsedHeight;
        public Drawable copyIcon;
        public int copyIconColor;
        public Drawable copySelector;
        public int copySelectorColor;
        public Paint copySeparator;
        public Text copyText;
        public byte directionFlags;
        public boolean first;
        public boolean hasCodeCopyButton;
        public int height;
        public int heightByOffset;
        public int index;
        public String language;
        public int languageHeight;
        public Text languageLayout;
        public boolean last;
        public float maxRight;
        public MessageObject messageObject;
        public int originalWidth;
        public int padBottom;
        public int padTop;
        public boolean quote;
        public boolean quoteCollapse;
        public StaticLayout textLayout;
        public AtomicReference<Layout> spoilersPatchedTextLayout = new AtomicReference<>();
        public List<SpoilerEffect> spoilers = new ArrayList();

        private static String capitalizeFirst(String str) {
            return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
        }

        private static String capitalizeLanguage(String str) {
            if (str == null) {
                return null;
            }
            String replaceAll = str.toLowerCase().replaceAll("\\W|lang$", "");
            replaceAll.hashCode();
            char c = 65535;
            switch (replaceAll.hashCode()) {
                case -1886433663:
                    if (replaceAll.equals("actionscript")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1408289185:
                    if (replaceAll.equals("aspnet")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1351281305:
                    if (replaceAll.equals("csharp")) {
                        c = 2;
                        break;
                    }
                    break;
                case -1326485984:
                    if (replaceAll.equals("docker")) {
                        c = 3;
                        break;
                    }
                    break;
                case -1317317732:
                    if (replaceAll.equals("dockerfile")) {
                        c = 4;
                        break;
                    }
                    break;
                case -1125574399:
                    if (replaceAll.equals("kotlin")) {
                        c = 5;
                        break;
                    }
                    break;
                case -995396628:
                    if (replaceAll.equals("pascal")) {
                        c = 6;
                        break;
                    }
                    break;
                case -973197092:
                    if (replaceAll.equals("python")) {
                        c = 7;
                        break;
                    }
                    break;
                case -746790872:
                    if (replaceAll.equals("arduino")) {
                        c = '\b';
                        break;
                    }
                    break;
                case -522285947:
                    if (replaceAll.equals("typescript")) {
                        c = '\t';
                        break;
                    }
                    break;
                case 99:
                    if (replaceAll.equals("c")) {
                        c = '\n';
                        break;
                    }
                    break;
                case 114:
                    if (replaceAll.equals("r")) {
                        c = 11;
                        break;
                    }
                    break;
                case 3184:
                    if (replaceAll.equals("cs")) {
                        c = '\f';
                        break;
                    }
                    break;
                case 3304:
                    if (replaceAll.equals("go")) {
                        c = '\r';
                        break;
                    }
                    break;
                case 3401:
                    if (replaceAll.equals("js")) {
                        c = 14;
                        break;
                    }
                    break;
                case 3479:
                    if (replaceAll.equals("md")) {
                        c = 15;
                        break;
                    }
                    break;
                case 3593:
                    if (replaceAll.equals("py")) {
                        c = 16;
                        break;
                    }
                    break;
                case 3632:
                    if (replaceAll.equals("rb")) {
                        c = 17;
                        break;
                    }
                    break;
                case 3704:
                    if (replaceAll.equals("tl")) {
                        c = 18;
                        break;
                    }
                    break;
                case 3711:
                    if (replaceAll.equals("ts")) {
                        c = 19;
                        break;
                    }
                    break;
                case 96891:
                    if (replaceAll.equals("asm")) {
                        c = 20;
                        break;
                    }
                    break;
                case 98723:
                    if (replaceAll.equals("cpp")) {
                        c = 21;
                        break;
                    }
                    break;
                case 98819:
                    if (replaceAll.equals("css")) {
                        c = 22;
                        break;
                    }
                    break;
                case 98822:
                    if (replaceAll.equals("csv")) {
                        c = 23;
                        break;
                    }
                    break;
                case 104420:
                    if (replaceAll.equals("ini")) {
                        c = 24;
                        break;
                    }
                    break;
                case 105551:
                    if (replaceAll.equals("jsx")) {
                        c = 25;
                        break;
                    }
                    break;
                case 107512:
                    if (replaceAll.equals("lua")) {
                        c = 26;
                        break;
                    }
                    break;
                case 110968:
                    if (replaceAll.equals("php")) {
                        c = 27;
                        break;
                    }
                    break;
                case 114922:
                    if (replaceAll.equals("tlb")) {
                        c = 28;
                        break;
                    }
                    break;
                case 115161:
                    if (replaceAll.equals("tsx")) {
                        c = 29;
                        break;
                    }
                    break;
                case 118807:
                    if (replaceAll.equals("xml")) {
                        c = 30;
                        break;
                    }
                    break;
                case 119768:
                    if (replaceAll.equals("yml")) {
                        c = 31;
                        break;
                    }
                    break;
                case 3075967:
                    if (replaceAll.equals("dart")) {
                        c = ' ';
                        break;
                    }
                    break;
                case 3142865:
                    if (replaceAll.equals("fift")) {
                        c = '!';
                        break;
                    }
                    break;
                case 3154628:
                    if (replaceAll.equals("func")) {
                        c = '\"';
                        break;
                    }
                    break;
                case 3175934:
                    if (replaceAll.equals("glsl")) {
                        c = '#';
                        break;
                    }
                    break;
                case 3205725:
                    if (replaceAll.equals("hlsl")) {
                        c = '$';
                        break;
                    }
                    break;
                case 3213227:
                    if (replaceAll.equals("html")) {
                        c = '%';
                        break;
                    }
                    break;
                case 3213448:
                    if (replaceAll.equals("http")) {
                        c = '&';
                        break;
                    }
                    break;
                case 3254818:
                    if (replaceAll.equals("java")) {
                        c = '\'';
                        break;
                    }
                    break;
                case 3271912:
                    if (replaceAll.equals("json")) {
                        c = '(';
                        break;
                    }
                    break;
                case 3318169:
                    if (replaceAll.equals("less")) {
                        c = ')';
                        break;
                    }
                    break;
                case 3373901:
                    if (replaceAll.equals("nasm")) {
                        c = '*';
                        break;
                    }
                    break;
                case 3404364:
                    if (replaceAll.equals("objc")) {
                        c = '+';
                        break;
                    }
                    break;
                case 3511770:
                    if (replaceAll.equals("ruby")) {
                        c = ',';
                        break;
                    }
                    break;
                case 3512292:
                    if (replaceAll.equals("rust")) {
                        c = '-';
                        break;
                    }
                    break;
                case 3524784:
                    if (replaceAll.equals("scss")) {
                        c = '.';
                        break;
                    }
                    break;
                case 3561037:
                    if (replaceAll.equals("tl-b")) {
                        c = '/';
                        break;
                    }
                    break;
                case 3642020:
                    if (replaceAll.equals("wasm")) {
                        c = '0';
                        break;
                    }
                    break;
                case 3701415:
                    if (replaceAll.equals("yaml")) {
                        c = '1';
                        break;
                    }
                    break;
                case 94833107:
                    if (replaceAll.equals("cobol")) {
                        c = '2';
                        break;
                    }
                    break;
                case 101429325:
                    if (replaceAll.equals("json5")) {
                        c = '3';
                        break;
                    }
                    break;
                case 109854227:
                    if (replaceAll.equals("swift")) {
                        c = '4';
                        break;
                    }
                    break;
                case 188995949:
                    if (replaceAll.equals("javascript")) {
                        c = '5';
                        break;
                    }
                    break;
                case 213985633:
                    if (replaceAll.equals("autohotkey")) {
                        c = '6';
                        break;
                    }
                    break;
                case 246938863:
                    if (replaceAll.equals("markdown")) {
                        c = '7';
                        break;
                    }
                    break;
                case 1067478602:
                    if (replaceAll.equals("objectivec")) {
                        c = '8';
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    return "ActionScript";
                case 1:
                    return "ASP.NET";
                case 2:
                case '\f':
                    return "C#";
                case 3:
                case 4:
                case 5:
                case 6:
                case '\b':
                case '\n':
                case '\r':
                case 26:
                case ' ':
                case '!':
                case '\'':
                case '-':
                case '4':
                    return capitalizeFirst(str);
                case 7:
                case 16:
                    return "Python";
                case '\t':
                case 19:
                    return "TypeScript";
                case 11:
                case 18:
                case 20:
                case 22:
                case 23:
                case 24:
                case 25:
                case 27:
                case 29:
                case 30:
                case 31:
                case '#':
                case '$':
                case '%':
                case '&':
                case '(':
                case ')':
                case '*':
                case '.':
                case '0':
                case '1':
                case '2':
                case '3':
                    return str.toUpperCase();
                case 14:
                case '5':
                    return "JavaScript";
                case 15:
                case '7':
                    return "Markdown";
                case 17:
                case ',':
                    return "Ruby";
                case 21:
                    return "C++";
                case 28:
                case '/':
                    return "TL-B";
                case '\"':
                    return "FunC";
                case '+':
                case '8':
                    return "Objective-C";
                case '6':
                    return "AutoHotKey";
                default:
                    return str;
            }
        }

        public float collapsed(ChatMessageCell.TransitionParams transitionParams) {
            boolean collapsed;
            if (transitionParams.animateExpandedQuotes) {
                HashSet hashSet = transitionParams.animateExpandedQuotesFrom;
                collapsed = hashSet == null || !hashSet.contains(Integer.valueOf(this.index));
            } else {
                collapsed = collapsed();
            }
            return AndroidUtilities.lerp(collapsed ? 1.0f : 0.0f, collapsed() ? 1.0f : 0.0f, transitionParams.animateChangeProgress);
        }

        public boolean collapsed() {
            HashSet<Integer> hashSet;
            MessageObject messageObject = this.messageObject;
            return messageObject == null || (hashSet = messageObject.expandedQuotes) == null || !hashSet.contains(Integer.valueOf(this.index));
        }

        public void drawCopyCodeButton(Canvas canvas, RectF rectF, int i, int i2, float f) {
            if (this.hasCodeCopyButton) {
                int multAlpha = Theme.multAlpha(i, 0.1f);
                if (this.copySelectorColor != multAlpha) {
                    Drawable drawable = this.copySelector;
                    this.copySelectorColor = multAlpha;
                    Theme.setSelectorDrawableColor(drawable, multAlpha, true);
                }
                this.copySelector.setBounds(((int) rectF.left) + AndroidUtilities.dp(3.0f), (int) (rectF.bottom - AndroidUtilities.dp(38.0f)), (int) rectF.right, (int) rectF.bottom);
                int i3 = (int) (255.0f * f);
                this.copySelector.setAlpha(i3);
                if (this.copySelector.getCallback() != null) {
                    this.copySelector.draw(canvas);
                }
                this.copySeparator.setColor(ColorUtils.setAlphaComponent(i2, 38));
                canvas.drawRect(AndroidUtilities.dp(10.0f) + rectF.left, (rectF.bottom - AndroidUtilities.dp(38.0f)) - AndroidUtilities.getShadowHeight(), rectF.right - AndroidUtilities.dp(6.66f), rectF.bottom - AndroidUtilities.dp(38.0f), this.copySeparator);
                float centerX = rectF.centerX() - (Math.min(rectF.width() - AndroidUtilities.dp(12.0f), ((this.copyIcon.getIntrinsicWidth() * 0.8f) + AndroidUtilities.dp(5.0f)) + this.copyText.getCurrentWidth()) / 2.0f);
                float dp = rectF.bottom - (AndroidUtilities.dp(38.0f) / 2.0f);
                if (this.copyIconColor != i) {
                    Drawable drawable2 = this.copyIcon;
                    this.copyIconColor = i;
                    drawable2.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
                }
                this.copyIcon.setAlpha(i3);
                this.copyIcon.setBounds((int) centerX, (int) (dp - ((r10.getIntrinsicHeight() * 0.8f) / 2.0f)), (int) ((this.copyIcon.getIntrinsicWidth() * 0.8f) + centerX), (int) (((this.copyIcon.getIntrinsicHeight() * 0.8f) / 2.0f) + dp));
                this.copyIcon.draw(canvas);
                this.copyText.ellipsize(((int) (r1 - ((this.copyIcon.getIntrinsicWidth() * 0.8f) + AndroidUtilities.dp(5.0f)))) + AndroidUtilities.dp(12.0f)).draw(canvas, centerX + (this.copyIcon.getIntrinsicWidth() * 0.8f) + AndroidUtilities.dp(5.0f), dp, i, f);
            }
        }

        public int height() {
            return (this.quoteCollapse && collapsed()) ? this.collapsedHeight : this.height;
        }

        public int height(ChatMessageCell.TransitionParams transitionParams) {
            return !this.quoteCollapse ? this.height : AndroidUtilities.lerp(this.height, this.collapsedHeight, collapsed(transitionParams));
        }

        public boolean isRtl() {
            byte b = this.directionFlags;
            return (b & 1) != 0 && (b & 2) == 0;
        }

        public void layoutCode(String str, int i, boolean z) {
            boolean z2 = i >= 75 && !z;
            this.hasCodeCopyButton = z2;
            if (z2) {
                this.copyText = new Text(LocaleController.getString(R.string.CopyCode).toUpperCase(), SharedConfig.fontSize - 3, AndroidUtilities.bold());
                Drawable mutate = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.msg_copy).mutate();
                this.copyIcon = mutate;
                mutate.setColorFilter(new PorterDuffColorFilter(this.copyIconColor, PorterDuff.Mode.SRC_IN));
                this.copySelector = Theme.createRadSelectorDrawable(this.copySelectorColor, 0, 0, Math.min(5, SharedConfig.bubbleRadius), 0);
                this.copySeparator = new Paint(1);
            }
            if (TextUtils.isEmpty(str)) {
                this.language = null;
                this.languageLayout = null;
            } else {
                this.language = str;
                Text text = new Text(capitalizeLanguage(str), (SharedConfig.fontSize - 1) - (CodeHighlighting.getTextSizeDecrement(i) / 2), AndroidUtilities.bold());
                this.languageLayout = text;
                this.languageHeight = ((int) (text.getTextSize() * 1.714f)) + AndroidUtilities.dp(4.0f);
            }
        }

        public float textYOffset(ArrayList<TextLayoutBlock> arrayList) {
            TextLayoutBlock textLayoutBlock;
            if (arrayList == null) {
                return 0.0f;
            }
            int i = 0;
            for (int i2 = 0; i2 < arrayList.size() && (textLayoutBlock = arrayList.get(i2)) != this; i2++) {
                i += textLayoutBlock.padTop + textLayoutBlock.height() + textLayoutBlock.padBottom;
            }
            return i;
        }

        public float textYOffset(ArrayList<TextLayoutBlock> arrayList, ChatMessageCell.TransitionParams transitionParams) {
            TextLayoutBlock textLayoutBlock;
            if (arrayList == null) {
                return 0.0f;
            }
            int i = 0;
            for (int i2 = 0; i2 < arrayList.size() && (textLayoutBlock = arrayList.get(i2)) != this; i2++) {
                i += textLayoutBlock.padTop + textLayoutBlock.height(transitionParams) + textLayoutBlock.padBottom;
            }
            return i;
        }
    }

    public static class TextLayoutBlocks {
        public boolean hasCode;
        public boolean hasCodeAtBottom;
        public boolean hasCodeAtTop;
        public boolean hasQuote;
        public boolean hasQuoteAtBottom;
        public boolean hasRtl;
        public boolean hasSingleCode;
        public boolean hasSingleQuote;
        public int lastLineWidth;
        public final CharSequence text;
        public final ArrayList<TextLayoutBlock> textLayoutBlocks = new ArrayList<>();
        public int textWidth;
        public float textXOffset;

        public TextLayoutBlocks(org.telegram.messenger.MessageObject r32, java.lang.CharSequence r33, android.text.TextPaint r34, int r35) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.TextLayoutBlocks.<init>(org.telegram.messenger.MessageObject, java.lang.CharSequence, android.text.TextPaint, int):void");
        }

        public void bounceFrom(TextLayoutBlocks textLayoutBlocks) {
            if (textLayoutBlocks == null) {
                return;
            }
            for (int i = 0; i < Math.min(this.textLayoutBlocks.size(), textLayoutBlocks.textLayoutBlocks.size()); i++) {
                this.textLayoutBlocks.get(i).collapsedBounce = textLayoutBlocks.textLayoutBlocks.get(i).collapsedBounce;
            }
        }

        public int textHeight() {
            int i = 0;
            for (int i2 = 0; i2 < this.textLayoutBlocks.size(); i2++) {
                i += this.textLayoutBlocks.get(i2).padTop + this.textLayoutBlocks.get(i2).height() + this.textLayoutBlocks.get(i2).padBottom;
            }
            return i;
        }

        public int textHeight(ChatMessageCell.TransitionParams transitionParams) {
            int i = 0;
            for (int i2 = 0; i2 < this.textLayoutBlocks.size(); i2++) {
                i += this.textLayoutBlocks.get(i2).padTop + this.textLayoutBlocks.get(i2).height(transitionParams) + this.textLayoutBlocks.get(i2).padBottom;
            }
            return i;
        }
    }

    public static class TextRange {
        public boolean code;
        public boolean collapse;
        public int end;
        public String language;
        public boolean quote;
        public int start;

        public TextRange(int i, int i2) {
            this.start = i;
            this.end = i2;
        }

        public TextRange(int i, int i2, boolean z, boolean z2, boolean z3, String str) {
            this.start = i;
            this.end = i2;
            this.quote = z;
            this.code = z2;
            this.collapse = z && z3;
            this.language = str;
        }
    }

    public static class VCardData {
        private String company;
        private ArrayList<String> emails = new ArrayList<>();
        private ArrayList<String> phones = new ArrayList<>();

        public static CharSequence parse(String str) {
            byte[] decodeQuotedPrintable;
            try {
                BufferedReader bufferedReader = new BufferedReader(new StringReader(str));
                boolean z = false;
                VCardData vCardData = null;
                String str2 = null;
                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        try {
                            break;
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    } else if (!readLine.startsWith("PHOTO")) {
                        if (readLine.indexOf(58) >= 0) {
                            if (readLine.startsWith("BEGIN:VCARD")) {
                                vCardData = new VCardData();
                            } else if (readLine.startsWith("END:VCARD") && vCardData != null) {
                                z = true;
                            }
                        }
                        if (str2 != null) {
                            readLine = str2 + readLine;
                            str2 = null;
                        }
                        if (readLine.contains("=QUOTED-PRINTABLE") && readLine.endsWith("=")) {
                            str2 = readLine.substring(0, readLine.length() - 1);
                        } else {
                            int indexOf = readLine.indexOf(":");
                            String[] strArr = indexOf >= 0 ? new String[]{readLine.substring(0, indexOf), readLine.substring(indexOf + 1).trim()} : new String[]{readLine.trim()};
                            int i = 2;
                            if (strArr.length >= 2 && vCardData != null) {
                                if (strArr[0].startsWith("ORG")) {
                                    String[] split = strArr[0].split(";");
                                    int length = split.length;
                                    int i2 = 0;
                                    String str3 = null;
                                    String str4 = null;
                                    while (i2 < length) {
                                        String[] split2 = split[i2].split("=");
                                        if (split2.length == i) {
                                            if (split2[0].equals("CHARSET")) {
                                                str4 = split2[1];
                                            } else if (split2[0].equals("ENCODING")) {
                                                str3 = split2[1];
                                            }
                                        }
                                        i2++;
                                        i = 2;
                                    }
                                    vCardData.company = strArr[1];
                                    if (str3 != null && str3.equalsIgnoreCase("QUOTED-PRINTABLE") && (decodeQuotedPrintable = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(vCardData.company))) != null && decodeQuotedPrintable.length != 0) {
                                        vCardData.company = new String(decodeQuotedPrintable, str4);
                                    }
                                    vCardData.company = vCardData.company.replace(';', ' ');
                                } else if (strArr[0].startsWith("TEL")) {
                                    if (strArr[1].length() > 0) {
                                        vCardData.phones.add(strArr[1]);
                                    }
                                } else if (strArr[0].startsWith("EMAIL")) {
                                    String str5 = strArr[1];
                                    if (str5.length() > 0) {
                                        vCardData.emails.add(str5);
                                    }
                                }
                            }
                        }
                    }
                }
                bufferedReader.close();
                if (!z) {
                    return null;
                }
                StringBuilder sb = new StringBuilder();
                for (int i3 = 0; i3 < vCardData.phones.size(); i3++) {
                    if (sb.length() > 0) {
                        sb.append('\n');
                    }
                    String str6 = vCardData.phones.get(i3);
                    if (!str6.contains("#") && !str6.contains("*")) {
                        str6 = PhoneFormat.getInstance().format(str6);
                    }
                    sb.append(str6);
                }
                for (int i4 = 0; i4 < vCardData.emails.size(); i4++) {
                    if (sb.length() > 0) {
                        sb.append('\n');
                    }
                    sb.append(PhoneFormat.getInstance().format(vCardData.emails.get(i4)));
                }
                if (!TextUtils.isEmpty(vCardData.company)) {
                    if (sb.length() > 0) {
                        sb.append('\n');
                    }
                    sb.append(vCardData.company);
                }
                return sb;
            } catch (Throwable unused) {
                return null;
            }
        }
    }

    public MessageObject(int i, TLRPC.Message message, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, boolean z, boolean z2) {
        this(i, message, null, null, null, longSparseArray, longSparseArray2, z, z2, 0L, false, false, false);
    }

    public MessageObject(int i, TLRPC.Message message, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, boolean z, boolean z2, boolean z3) {
        this(i, message, null, null, null, longSparseArray, longSparseArray2, z, z2, 0L, false, false, z3);
    }

    public MessageObject(int i, TLRPC.Message message, LongSparseArray longSparseArray, boolean z, boolean z2) {
        this(i, message, longSparseArray, (LongSparseArray) null, z, z2);
    }

    public MessageObject(int i, TLRPC.Message message, String str, String str2, String str3, boolean z, boolean z2, boolean z3, boolean z4) {
        this.type = 1000;
        this.forceSeekTo = -1.0f;
        this.actionDeleteGroupEventId = -1L;
        this.overrideLinkColor = -1;
        this.overrideLinkEmoji = -1L;
        this.messageTrimmedToHighlightCut = true;
        this.topicIconDrawable = new Drawable[1];
        this.spoiledLoginCode = false;
        this.translated = false;
        this.localType = z ? 2 : 1;
        this.currentAccount = i;
        this.localName = str2;
        this.localUserName = str3;
        this.messageText = str;
        this.messageOwner = message;
        this.localChannel = z2;
        this.localSupergroup = z3;
        this.localEdit = z4;
    }

    public MessageObject(int i, TLRPC.Message message, AbstractMap<Long, TLRPC.User> abstractMap, AbstractMap<Long, TLRPC.Chat> abstractMap2, boolean z, boolean z2) {
        this(i, message, abstractMap, abstractMap2, z, z2, 0L);
    }

    public MessageObject(int i, TLRPC.Message message, AbstractMap<Long, TLRPC.User> abstractMap, AbstractMap<Long, TLRPC.Chat> abstractMap2, boolean z, boolean z2, long j) {
        this(i, message, null, abstractMap, abstractMap2, null, null, z, z2, j);
    }

    public MessageObject(int i, TLRPC.Message message, AbstractMap<Long, TLRPC.User> abstractMap, boolean z, boolean z2) {
        this(i, message, abstractMap, (AbstractMap<Long, TLRPC.Chat>) null, z, z2);
    }

    public MessageObject(int i, TLRPC.Message message, MessageObject messageObject, AbstractMap<Long, TLRPC.User> abstractMap, AbstractMap<Long, TLRPC.Chat> abstractMap2, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, boolean z, boolean z2, long j) {
        this(i, message, messageObject, abstractMap, abstractMap2, longSparseArray, longSparseArray2, z, z2, j, false, false, false);
    }

    public MessageObject(int i, TLRPC.Message message, MessageObject messageObject, AbstractMap<Long, TLRPC.User> abstractMap, AbstractMap<Long, TLRPC.Chat> abstractMap2, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, boolean z, boolean z2, long j, boolean z3, boolean z4, boolean z5) {
        this(i, message, messageObject, abstractMap, abstractMap2, longSparseArray, longSparseArray2, z, z2, j, z3, z4, z5, 0);
    }

    public MessageObject(int i, TLRPC.Message message, MessageObject messageObject, AbstractMap<Long, TLRPC.User> abstractMap, AbstractMap<Long, TLRPC.Chat> abstractMap2, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, boolean z, boolean z2, long j, boolean z3, boolean z4, boolean z5, int i2) {
        this.type = 1000;
        this.forceSeekTo = -1.0f;
        this.actionDeleteGroupEventId = -1L;
        this.overrideLinkColor = -1;
        this.overrideLinkEmoji = -1L;
        this.messageTrimmedToHighlightCut = true;
        this.topicIconDrawable = new Drawable[1];
        this.spoiledLoginCode = false;
        this.translated = false;
        Theme.createCommonMessageResources();
        this.isRepostPreview = z3;
        this.isRepostVideoPreview = z4;
        this.isSaved = z5 || getDialogId(message) == UserConfig.getInstance(i).getClientUserId();
        this.searchType = i2;
        this.currentAccount = i;
        this.messageOwner = message;
        this.replyMessageObject = messageObject;
        this.eventId = j;
        this.wasUnread = !message.out && message.unread;
        TLRPC.Message message2 = message.replyMessage;
        if (message2 != null) {
            this.replyMessageObject = new MessageObject(i, message2, null, abstractMap, abstractMap2, longSparseArray, longSparseArray2, false, z2, j);
        }
        TLRPC.Peer peer = message.from_id;
        if (peer instanceof TLRPC.TL_peerUser) {
            getUser(abstractMap, longSparseArray, peer.user_id);
        }
        updateMessageText(abstractMap, abstractMap2, longSparseArray, longSparseArray2);
        setType();
        if (z) {
            updateTranslation(false);
        }
        measureInlineBotButtons();
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(this.messageOwner.date * 1000);
        int i3 = gregorianCalendar.get(6);
        int i4 = gregorianCalendar.get(1);
        int i5 = gregorianCalendar.get(2);
        this.dateKey = String.format("%d_%02d_%02d", Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i3));
        this.dateKeyInt = (i5 * 10000) + i4 + (i3 * 1000000);
        this.monthKey = String.format("%d_%02d", Integer.valueOf(i4), Integer.valueOf(i5));
        createMessageSendInfo();
        generateCaption();
        if (z) {
            TextPaint textPaint = getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaGame ? Theme.chat_msgGameTextPaint : Theme.chat_msgTextPaint;
            int[] iArr = allowsBigEmoji() ? new int[1] : null;
            CharSequence replaceEmoji = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), false, iArr);
            this.messageText = replaceEmoji;
            Spannable replaceAnimatedEmoji = replaceAnimatedEmoji(replaceEmoji, textPaint.getFontMetricsInt());
            this.messageText = replaceAnimatedEmoji;
            if (iArr != null && iArr[0] > 1) {
                replaceEmojiToLottieFrame(replaceAnimatedEmoji, iArr);
            }
            checkEmojiOnly(iArr);
            checkBigAnimatedEmoji();
            setType();
            createPathThumb();
        }
        this.layoutCreated = z;
        generateThumbs(false);
        if (z2) {
            checkMediaExistance();
        }
    }

    public MessageObject(int i, TLRPC.Message message, MessageObject messageObject, boolean z, boolean z2) {
        this(i, message, messageObject, null, null, null, null, z, z2, 0L);
    }

    public MessageObject(int i, TLRPC.Message message, boolean z, boolean z2) {
        this(i, message, null, null, null, null, null, z, z2, 0L);
    }

    public MessageObject(int r37, org.telegram.tgnet.TLRPC.TL_channelAdminLogEvent r38, java.util.ArrayList<org.telegram.messenger.MessageObject> r39, java.util.HashMap<java.lang.String, java.util.ArrayList<org.telegram.messenger.MessageObject>> r40, org.telegram.tgnet.TLRPC.Chat r41, int[] r42, boolean r43) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.<init>(int, org.telegram.tgnet.TLRPC$TL_channelAdminLogEvent, java.util.ArrayList, java.util.HashMap, org.telegram.tgnet.TLRPC$Chat, int[], boolean):void");
    }

    public MessageObject(int i, TL_stories.StoryItem storyItem) {
        this.type = 1000;
        this.forceSeekTo = -1.0f;
        this.actionDeleteGroupEventId = -1L;
        this.overrideLinkColor = -1;
        this.overrideLinkEmoji = -1L;
        this.messageTrimmedToHighlightCut = true;
        this.topicIconDrawable = new Drawable[1];
        this.spoiledLoginCode = false;
        this.translated = false;
        this.currentAccount = i;
        this.storyItem = storyItem;
        if (storyItem != null) {
            TLRPC.TL_message tL_message = new TLRPC.TL_message();
            this.messageOwner = tL_message;
            tL_message.id = storyItem.messageId;
            tL_message.realId = storyItem.id;
            tL_message.date = storyItem.date;
            tL_message.dialog_id = storyItem.dialogId;
            tL_message.message = storyItem.caption;
            tL_message.entities = storyItem.entities;
            tL_message.media = storyItem.media;
            tL_message.attachPath = storyItem.attachPath;
        }
        this.photoThumbs = new ArrayList<>();
        this.photoThumbs2 = new ArrayList<>();
    }

    public static boolean addEntitiesToText(CharSequence charSequence, ArrayList<TLRPC.MessageEntity> arrayList, boolean z, boolean z2, boolean z3, boolean z4) {
        return addEntitiesToText(charSequence, arrayList, z, z2, z3, z4, 0);
    }

    public static boolean addEntitiesToText(java.lang.CharSequence r19, java.util.ArrayList<org.telegram.tgnet.TLRPC.MessageEntity> r20, boolean r21, boolean r22, boolean r23, boolean r24, int r25) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.addEntitiesToText(java.lang.CharSequence, java.util.ArrayList, boolean, boolean, boolean, boolean, int):boolean");
    }

    private boolean addEntitiesToText(CharSequence charSequence, boolean z) {
        return addEntitiesToText(charSequence, false, z);
    }

    public static void addLinks(boolean z, CharSequence charSequence) {
        addLinks(z, charSequence, true, false);
    }

    public static void addLinks(boolean z, CharSequence charSequence, boolean z2, boolean z3) {
        addLinks(z, charSequence, z2, z3, false);
    }

    public static void addLinks(boolean z, CharSequence charSequence, boolean z2, boolean z3, boolean z4) {
        if ((charSequence instanceof Spannable) && containsUrls(charSequence)) {
            try {
                AndroidUtilities.addLinksSafe((Spannable) charSequence, 1, z4, false);
            } catch (Exception e) {
                FileLog.e(e);
            }
            addPhoneLinks(charSequence);
            addUrlsByPattern(z, charSequence, z2, 0, 0, z3);
        }
    }

    public static void addPaidReactions(int i, TLRPC.MessageReactions messageReactions, int i2, boolean z, boolean z2) {
        TLRPC.MessageReactor messageReactor = null;
        TLRPC.ReactionCount reactionCount = null;
        for (int i3 = 0; i3 < messageReactions.results.size(); i3++) {
            if (messageReactions.results.get(i3).reaction instanceof TLRPC.TL_reactionPaid) {
                reactionCount = messageReactions.results.get(i3);
            }
        }
        int i4 = 0;
        while (true) {
            if (i4 >= messageReactions.top_reactors.size()) {
                break;
            }
            if (messageReactions.top_reactors.get(i4).my) {
                messageReactor = messageReactions.top_reactors.get(i4);
                break;
            }
            i4++;
        }
        if (reactionCount == null && i2 > 0) {
            reactionCount = new TLRPC.TL_reactionCount();
            reactionCount.reaction = new TLRPC.TL_reactionPaid();
            messageReactions.results.add(0, reactionCount);
        }
        if (reactionCount != null) {
            reactionCount.chosen = z2;
            int max = Math.max(0, reactionCount.count + i2);
            reactionCount.count = max;
            if (max <= 0) {
                messageReactions.results.remove(reactionCount);
            }
        }
        if (messageReactor == null && i2 > 0) {
            messageReactor = new TLRPC.TL_messageReactor();
            messageReactor.my = true;
            messageReactor.peer_id = MessagesController.getInstance(i).getPeer(UserConfig.getInstance(i).getClientUserId());
            messageReactions.top_reactors.add(messageReactor);
        }
        if (messageReactor != null) {
            int max2 = Math.max(0, messageReactor.count + i2);
            messageReactor.count = max2;
            messageReactor.anonymous = z;
            if (max2 <= 0) {
                messageReactions.top_reactors.remove(messageReactor);
            }
        }
    }

    public static void addPhoneLinks(CharSequence charSequence) {
    }

    public static void addUrlsByPattern(boolean r17, java.lang.CharSequence r18, boolean r19, int r20, int r21, boolean r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.addUrlsByPattern(boolean, java.lang.CharSequence, boolean, int, int, boolean):void");
    }

    private boolean allowsBigEmoji() {
        TLRPC.Peer peer;
        if (!SharedConfig.allowBigEmoji) {
            return false;
        }
        TLRPC.Message message = this.messageOwner;
        if (message == null || (peer = message.peer_id) == null || (peer.channel_id == 0 && peer.chat_id == 0)) {
            return true;
        }
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        TLRPC.Peer peer2 = this.messageOwner.peer_id;
        long j = peer2.channel_id;
        if (j == 0) {
            j = peer2.chat_id;
        }
        TLRPC.Chat chat = messagesController.getChat(Long.valueOf(j));
        return (chat != null && chat.gigagroup) || !ChatObject.isActionBanned(chat, 8) || ChatObject.hasAdminRights(chat);
    }

    private boolean applyEntities() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.applyEntities():boolean");
    }

    public static boolean canAutoplayAnimatedSticker(TLRPC.Document document) {
        return (isAnimatedStickerDocument(document, true) || isVideoStickerDocument(document)) && LiteMode.isEnabled(1);
    }

    public static boolean canCreateStripedThubms() {
        return SharedConfig.getDevicePerformanceClass() == 2;
    }

    public static boolean canDeleteMessage(int i, boolean z, TLRPC.Message message, TLRPC.Chat chat) {
        TLRPC.TL_chatAdminRights tL_chatAdminRights;
        TLRPC.Peer peer;
        if (message == null) {
            return false;
        }
        if (ChatObject.isChannelAndNotMegaGroup(chat) && (message.action instanceof TLRPC.TL_messageActionChatJoinedByRequest)) {
            return false;
        }
        if (message.id < 0) {
            return true;
        }
        if (chat == null && (peer = message.peer_id) != null && peer.channel_id != 0) {
            chat = MessagesController.getInstance(i).getChat(Long.valueOf(message.peer_id.channel_id));
        }
        if (!ChatObject.isChannel(chat)) {
            return z || isOut(message) || !ChatObject.isChannel(chat);
        }
        if (z && !chat.megagroup) {
            if (!chat.creator) {
                TLRPC.TL_chatAdminRights tL_chatAdminRights2 = chat.admin_rights;
                if (tL_chatAdminRights2 == null) {
                    return false;
                }
                if (!tL_chatAdminRights2.delete_messages && !message.out) {
                    return false;
                }
            }
            return true;
        }
        boolean z2 = message.out;
        if (z2 && (message instanceof TLRPC.TL_messageService)) {
            return message.id != 1 && ChatObject.canUserDoAdminAction(chat, 13);
        }
        if (!z) {
            if (message.id == 1) {
                return false;
            }
            if (!chat.creator && (((tL_chatAdminRights = chat.admin_rights) == null || (!tL_chatAdminRights.delete_messages && (!z2 || (!chat.megagroup && !tL_chatAdminRights.post_messages)))) && (!chat.megagroup || !z2))) {
                return false;
            }
        }
        return true;
    }

    public static boolean canEditMessage(int i, TLRPC.Message message, TLRPC.Chat chat, boolean z) {
        TLRPC.MessageAction messageAction;
        TLRPC.TL_chatAdminRights tL_chatAdminRights;
        TLRPC.TL_chatAdminRights tL_chatAdminRights2;
        TLRPC.TL_chatBannedRights tL_chatBannedRights;
        TLRPC.TL_chatAdminRights tL_chatAdminRights3;
        if (z && message.date < ConnectionsManager.getInstance(i).getCurrentTime() - 60) {
            return false;
        }
        if (chat != null && ((chat.left || chat.kicked) && (!chat.megagroup || !chat.has_link))) {
            return false;
        }
        TLRPC.MessageMedia media = getMedia(message);
        if (message != null && message.peer_id != null && ((media == null || (!isRoundVideoDocument(media.document) && !isStickerDocument(media.document) && !isAnimatedStickerDocument(media.document, true) && !isLocationMessage(message))) && (((messageAction = message.action) == null || (messageAction instanceof TLRPC.TL_messageActionEmpty)) && !isForwardedMessage(message) && message.via_bot_id == 0 && message.id >= 0))) {
            TLRPC.Peer peer = message.from_id;
            if (peer instanceof TLRPC.TL_peerUser) {
                long j = peer.user_id;
                if (j == message.peer_id.user_id && j == UserConfig.getInstance(i).getClientUserId() && !isLiveLocationMessage(message) && !(media instanceof TLRPC.TL_messageMediaContact)) {
                    return true;
                }
            }
            if (chat == null && message.peer_id.channel_id != 0 && (chat = MessagesController.getInstance(i).getChat(Long.valueOf(message.peer_id.channel_id))) == null) {
                return false;
            }
            if (media != null && !(media instanceof TLRPC.TL_messageMediaEmpty) && !(media instanceof TLRPC.TL_messageMediaPhoto) && !(media instanceof TLRPC.TL_messageMediaDocument) && !(media instanceof TLRPC.TL_messageMediaWebPage) && !(media instanceof TLRPC.TL_messageMediaPaidMedia)) {
                return false;
            }
            if (ChatObject.isChannel(chat) && !chat.megagroup && (chat.creator || ((tL_chatAdminRights3 = chat.admin_rights) != null && tL_chatAdminRights3.edit_messages))) {
                return true;
            }
            if (message.out && chat != null && chat.megagroup && (chat.creator || (((tL_chatAdminRights2 = chat.admin_rights) != null && tL_chatAdminRights2.pin_messages) || ((tL_chatBannedRights = chat.default_banned_rights) != null && !tL_chatBannedRights.pin_messages)))) {
                return true;
            }
            if (!z && Math.abs(message.date - ConnectionsManager.getInstance(i).getCurrentTime()) > MessagesController.getInstance(i).maxEditTime) {
                return false;
            }
            if (message.peer_id.channel_id == 0) {
                if (!message.out) {
                    TLRPC.Peer peer2 = message.from_id;
                    if (!(peer2 instanceof TLRPC.TL_peerUser) || peer2.user_id != UserConfig.getInstance(i).getClientUserId()) {
                        return false;
                    }
                }
                return (media instanceof TLRPC.TL_messageMediaPhoto) || !(!(media instanceof TLRPC.TL_messageMediaDocument) || isStickerMessage(message) || isAnimatedStickerMessage(message)) || (media instanceof TLRPC.TL_messageMediaEmpty) || (media instanceof TLRPC.TL_messageMediaWebPage) || (media instanceof TLRPC.TL_messageMediaPaidMedia) || media == null;
            }
            if (((chat != null && chat.megagroup && message.out) || (chat != null && !chat.megagroup && ((chat.creator || ((tL_chatAdminRights = chat.admin_rights) != null && (tL_chatAdminRights.edit_messages || (message.out && tL_chatAdminRights.post_messages)))) && message.post))) && ((media instanceof TLRPC.TL_messageMediaPhoto) || (((media instanceof TLRPC.TL_messageMediaDocument) && !isStickerMessage(message) && !isAnimatedStickerMessage(message)) || (media instanceof TLRPC.TL_messageMediaEmpty) || (media instanceof TLRPC.TL_messageMediaWebPage) || (media instanceof TLRPC.TL_messageMediaPaidMedia) || media == null))) {
                return true;
            }
        }
        return false;
    }

    public static boolean canEditMessageAnytime(int i, TLRPC.Message message, TLRPC.Chat chat) {
        TLRPC.MessageAction messageAction;
        TLRPC.TL_chatAdminRights tL_chatAdminRights;
        TLRPC.TL_chatBannedRights tL_chatBannedRights;
        TLRPC.TL_chatAdminRights tL_chatAdminRights2;
        if (message != null && message.peer_id != null && ((getMedia(message) == null || (!isRoundVideoDocument(getMedia(message).document) && !isStickerDocument(getMedia(message).document) && !isAnimatedStickerDocument(getMedia(message).document, true))) && (((messageAction = message.action) == null || (messageAction instanceof TLRPC.TL_messageActionEmpty)) && !isForwardedMessage(message) && message.via_bot_id == 0 && message.id >= 0))) {
            TLRPC.Peer peer = message.from_id;
            if (peer instanceof TLRPC.TL_peerUser) {
                long j = peer.user_id;
                if (j == message.peer_id.user_id && j == UserConfig.getInstance(i).getClientUserId() && !isLiveLocationMessage(message)) {
                    return true;
                }
            }
            if (chat == null && message.peer_id.channel_id != 0 && (chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(message.peer_id.channel_id))) == null) {
                return false;
            }
            if (ChatObject.isChannel(chat) && !chat.megagroup && (chat.creator || ((tL_chatAdminRights2 = chat.admin_rights) != null && tL_chatAdminRights2.edit_messages))) {
                return true;
            }
            if (message.out && chat != null && chat.megagroup && (chat.creator || (((tL_chatAdminRights = chat.admin_rights) != null && tL_chatAdminRights.pin_messages) || ((tL_chatBannedRights = chat.default_banned_rights) != null && !tL_chatBannedRights.pin_messages)))) {
                return true;
            }
        }
        return false;
    }

    public static boolean canEditMessageScheduleTime(int i, TLRPC.Message message, TLRPC.Chat chat) {
        if (message.video_processing_pending) {
            return false;
        }
        if (chat == null && message.peer_id.channel_id != 0 && (chat = MessagesController.getInstance(i).getChat(Long.valueOf(message.peer_id.channel_id))) == null) {
            return false;
        }
        if (!ChatObject.isChannel(chat) || chat.megagroup || chat.creator) {
            return true;
        }
        TLRPC.TL_chatAdminRights tL_chatAdminRights = chat.admin_rights;
        return tL_chatAdminRights != null && (tL_chatAdminRights.edit_messages || message.out);
    }

    public static boolean canPreviewDocument(TLRPC.Document document) {
        String str;
        if (document != null && (str = document.mime_type) != null) {
            if ((isDocumentHasThumb(document) && (str.equalsIgnoreCase("image/png") || str.equalsIgnoreCase("image/jpg") || str.equalsIgnoreCase("image/jpeg"))) || (Build.VERSION.SDK_INT >= 26 && str.equalsIgnoreCase("image/heic"))) {
                for (int i = 0; i < document.attributes.size(); i++) {
                    TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                    if (documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) {
                        TLRPC.TL_documentAttributeImageSize tL_documentAttributeImageSize = (TLRPC.TL_documentAttributeImageSize) documentAttribute;
                        return tL_documentAttributeImageSize.w < 6000 && tL_documentAttributeImageSize.h < 6000;
                    }
                }
            } else if (BuildVars.DEBUG_PRIVATE_VERSION) {
                String documentFileName = FileLoader.getDocumentFileName(document);
                if ((documentFileName.startsWith("tg_secret_sticker") && documentFileName.endsWith("json")) || documentFileName.endsWith(".svg")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static CharSequence channelSpan() {
        if (channelSpan == null) {
            channelSpan = new SpannableStringBuilder("c");
            ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.msg_folders_channels);
            coloredImageSpan.setScale(0.7f, 0.7f);
            ((SpannableStringBuilder) channelSpan).setSpan(coloredImageSpan, 0, 1, 33);
        }
        return channelSpan;
    }

    private void checkEmojiOnly(Integer num) {
        TextPaint textPaint;
        if (num == null || num.intValue() < 1 || this.messageOwner == null || hasNonEmojiEntities()) {
            CharSequence charSequence = this.messageText;
            AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) ((Spannable) charSequence).getSpans(0, charSequence.length(), AnimatedEmojiSpan.class);
            if (animatedEmojiSpanArr == null || animatedEmojiSpanArr.length <= 0) {
                this.totalAnimatedEmojiCount = 0;
                return;
            }
            this.totalAnimatedEmojiCount = animatedEmojiSpanArr.length;
            for (int i = 0; i < animatedEmojiSpanArr.length; i++) {
                animatedEmojiSpanArr[i].replaceFontMetrics(Theme.chat_msgTextPaint.getFontMetricsInt(), (int) (Theme.chat_msgTextPaint.getTextSize() + AndroidUtilities.dp(4.0f)), -1);
                animatedEmojiSpanArr[i].full = false;
            }
            return;
        }
        CharSequence charSequence2 = this.messageText;
        Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) ((Spannable) charSequence2).getSpans(0, charSequence2.length(), Emoji.EmojiSpan.class);
        CharSequence charSequence3 = this.messageText;
        AnimatedEmojiSpan[] animatedEmojiSpanArr2 = (AnimatedEmojiSpan[]) ((Spannable) charSequence3).getSpans(0, charSequence3.length(), AnimatedEmojiSpan.class);
        this.emojiOnlyCount = Math.max(num.intValue(), (emojiSpanArr == null ? 0 : emojiSpanArr.length) + (animatedEmojiSpanArr2 == null ? 0 : animatedEmojiSpanArr2.length));
        this.totalAnimatedEmojiCount = animatedEmojiSpanArr2 == null ? 0 : animatedEmojiSpanArr2.length;
        this.animatedEmojiCount = 0;
        if (animatedEmojiSpanArr2 != null) {
            for (AnimatedEmojiSpan animatedEmojiSpan : animatedEmojiSpanArr2) {
                if (!animatedEmojiSpan.standard) {
                    this.animatedEmojiCount++;
                }
            }
        }
        int i2 = this.emojiOnlyCount;
        boolean z = (i2 - (emojiSpanArr == null ? 0 : emojiSpanArr.length)) - (animatedEmojiSpanArr2 == null ? 0 : animatedEmojiSpanArr2.length) > 0;
        this.hasUnwrappedEmoji = z;
        if (i2 == 0 || z) {
            if (animatedEmojiSpanArr2 == null || animatedEmojiSpanArr2.length <= 0) {
                return;
            }
            for (int i3 = 0; i3 < animatedEmojiSpanArr2.length; i3++) {
                animatedEmojiSpanArr2[i3].replaceFontMetrics(Theme.chat_msgTextPaint.getFontMetricsInt(), (int) (Theme.chat_msgTextPaint.getTextSize() + AndroidUtilities.dp(4.0f)), -1);
                animatedEmojiSpanArr2[i3].full = false;
            }
            return;
        }
        int i4 = this.animatedEmojiCount;
        boolean z2 = i2 == i4;
        int i5 = 2;
        switch (Math.max(i2, i4)) {
            case 0:
            case 1:
            case 2:
                TextPaint[] textPaintArr = Theme.chat_msgTextPaintEmoji;
                textPaint = z2 ? textPaintArr[0] : textPaintArr[2];
                i5 = 1;
                break;
            case 3:
                TextPaint[] textPaintArr2 = Theme.chat_msgTextPaintEmoji;
                textPaint = z2 ? textPaintArr2[1] : textPaintArr2[3];
                i5 = 1;
                break;
            case 4:
                TextPaint[] textPaintArr3 = Theme.chat_msgTextPaintEmoji;
                textPaint = z2 ? textPaintArr3[2] : textPaintArr3[4];
                i5 = 1;
                break;
            case 5:
                TextPaint[] textPaintArr4 = Theme.chat_msgTextPaintEmoji;
                if (z2) {
                    textPaint = textPaintArr4[3];
                    break;
                } else {
                    textPaint = textPaintArr4[5];
                    break;
                }
            case 6:
                TextPaint[] textPaintArr5 = Theme.chat_msgTextPaintEmoji;
                if (z2) {
                    textPaint = textPaintArr5[4];
                    break;
                } else {
                    textPaint = textPaintArr5[5];
                    break;
                }
            default:
                int i6 = this.emojiOnlyCount > 9 ? 0 : -1;
                textPaint = Theme.chat_msgTextPaintEmoji[5];
                i5 = i6;
                break;
        }
        int textSize = (int) (textPaint.getTextSize() + AndroidUtilities.dp(4.0f));
        if (emojiSpanArr != null && emojiSpanArr.length > 0) {
            for (Emoji.EmojiSpan emojiSpan : emojiSpanArr) {
                emojiSpan.replaceFontMetrics(textPaint.getFontMetricsInt(), textSize);
            }
        }
        if (animatedEmojiSpanArr2 == null || animatedEmojiSpanArr2.length <= 0) {
            return;
        }
        for (int i7 = 0; i7 < animatedEmojiSpanArr2.length; i7++) {
            animatedEmojiSpanArr2[i7].replaceFontMetrics(textPaint.getFontMetricsInt(), textSize, i5);
            animatedEmojiSpanArr2[i7].full = true;
        }
    }

    private void checkEmojiOnly(int[] iArr) {
        checkEmojiOnly(iArr == null ? null : Integer.valueOf(iArr[0]));
    }

    public static boolean containsUrls(CharSequence charSequence) {
        if (charSequence != null && charSequence.length() >= 2 && charSequence.length() <= 20480) {
            int length = charSequence.length();
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            int i4 = 0;
            char c = 0;
            while (i < length) {
                char charAt = charSequence.charAt(i);
                if (charAt >= '0' && charAt <= '9') {
                    i2++;
                    if (i2 >= 6) {
                        return true;
                    }
                    i3 = 0;
                    i4 = 0;
                } else if (charAt == ' ' || i2 <= 0) {
                    i2 = 0;
                }
                if ((charAt != '@' && charAt != '#' && charAt != '/' && charAt != '$') || i != 0) {
                    if (i != 0) {
                        int i5 = i - 1;
                        if (charSequence.charAt(i5) != ' ') {
                            if (charSequence.charAt(i5) == '\n') {
                            }
                        }
                    }
                    if (charAt == ':') {
                        if (i3 == 0) {
                            i3 = 1;
                        }
                        i3 = 0;
                    } else if (charAt != '/') {
                        if (charAt == '.') {
                            if (i4 == 0 && c != ' ') {
                                i4++;
                            }
                        } else if (charAt != ' ' && c == '.' && i4 == 1) {
                            return true;
                        }
                        i4 = 0;
                    } else {
                        if (i3 == 2) {
                            return true;
                        }
                        if (i3 == 1) {
                            i3++;
                        }
                        i3 = 0;
                    }
                    i++;
                    c = charAt;
                }
                return true;
            }
        }
        return false;
    }

    private void createDateArray(int i, TLRPC.TL_channelAdminLogEvent tL_channelAdminLogEvent, ArrayList<MessageObject> arrayList, HashMap<String, ArrayList<MessageObject>> hashMap, boolean z) {
        if (hashMap.get(this.dateKey) == null) {
            hashMap.put(this.dateKey, new ArrayList<>());
            TLRPC.TL_message tL_message = new TLRPC.TL_message();
            tL_message.message = LocaleController.formatDateChat(tL_channelAdminLogEvent.date);
            tL_message.id = 0;
            tL_message.date = tL_channelAdminLogEvent.date;
            MessageObject messageObject = new MessageObject(i, tL_message, false, false);
            messageObject.type = 10;
            messageObject.contentType = 1;
            messageObject.isDateObject = true;
            if (z) {
                arrayList.add(0, messageObject);
            } else {
                arrayList.add(messageObject);
            }
        }
    }

    private void createPathThumb() {
        TLRPC.Document document = getDocument();
        if (document == null) {
            return;
        }
        this.pathThumb = DocumentObject.getSvgThumb(document, Theme.key_chat_serviceBackground, 1.0f);
    }

    public static void cutIntoRanges(CharSequence charSequence, ArrayList<TextRange> arrayList) {
        String str;
        int i;
        if (charSequence == null) {
            return;
        }
        if (!(charSequence instanceof Spanned)) {
            arrayList.add(new TextRange(0, charSequence.length()));
            return;
        }
        TreeSet treeSet = new TreeSet();
        HashMap hashMap = new HashMap();
        Spanned spanned = (Spanned) charSequence;
        QuoteSpan.QuoteStyleSpan[] quoteStyleSpanArr = (QuoteSpan.QuoteStyleSpan[]) spanned.getSpans(0, spanned.length(), QuoteSpan.QuoteStyleSpan.class);
        for (int i2 = 0; i2 < quoteStyleSpanArr.length; i2++) {
            QuoteSpan.QuoteStyleSpan quoteStyleSpan = quoteStyleSpanArr[i2];
            quoteStyleSpan.span.adaptLineHeight = false;
            int spanStart = spanned.getSpanStart(quoteStyleSpan);
            int spanEnd = spanned.getSpanEnd(quoteStyleSpanArr[i2]);
            treeSet.add(Integer.valueOf(spanStart));
            hashMap.put(Integer.valueOf(spanStart), Integer.valueOf((hashMap.containsKey(Integer.valueOf(spanStart)) ? ((Integer) hashMap.get(Integer.valueOf(spanStart))).intValue() : 0) | (quoteStyleSpanArr[i2].span.isCollapsing ? 16 : 1)));
            treeSet.add(Integer.valueOf(spanEnd));
            hashMap.put(Integer.valueOf(spanEnd), Integer.valueOf((hashMap.containsKey(Integer.valueOf(spanEnd)) ? ((Integer) hashMap.get(Integer.valueOf(spanEnd))).intValue() : 0) | 2));
        }
        Iterator it = treeSet.iterator();
        while (it.hasNext()) {
            Integer num = (Integer) it.next();
            int intValue = num.intValue();
            if (intValue >= 0 && intValue < spanned.length() && hashMap.containsKey(num)) {
                int intValue2 = ((Integer) hashMap.get(num)).intValue();
                if ((intValue2 & 17) != 0 && (intValue2 & 2) != 0 && spanned.charAt(intValue) != '\n' && (intValue - 1 <= 0 || spanned.charAt(i) != '\n')) {
                    it.remove();
                    hashMap.remove(num);
                }
            }
        }
        CodeHighlighting.Span[] spanArr = (CodeHighlighting.Span[]) spanned.getSpans(0, spanned.length(), CodeHighlighting.Span.class);
        for (int i3 = 0; i3 < spanArr.length; i3++) {
            int spanStart2 = spanned.getSpanStart(spanArr[i3]);
            int spanEnd2 = spanned.getSpanEnd(spanArr[i3]);
            treeSet.add(Integer.valueOf(spanStart2));
            hashMap.put(Integer.valueOf(spanStart2), Integer.valueOf((hashMap.containsKey(Integer.valueOf(spanStart2)) ? ((Integer) hashMap.get(Integer.valueOf(spanStart2))).intValue() : 0) | 4));
            treeSet.add(Integer.valueOf(spanEnd2));
            hashMap.put(Integer.valueOf(spanEnd2), Integer.valueOf((hashMap.containsKey(Integer.valueOf(spanEnd2)) ? ((Integer) hashMap.get(Integer.valueOf(spanEnd2))).intValue() : 0) | 8));
        }
        Iterator it2 = treeSet.iterator();
        boolean z = false;
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        while (it2.hasNext()) {
            Integer num2 = (Integer) it2.next();
            int intValue3 = num2.intValue();
            int intValue4 = ((Integer) hashMap.get(num2)).intValue();
            if (i6 != intValue3) {
                int i8 = intValue3 - 1;
                if (i8 >= 0 && i8 < charSequence.length() && charSequence.charAt(i8) == '\n') {
                    intValue3--;
                }
                int i9 = intValue3;
                if ((intValue4 & 8) == 0 || i5 >= spanArr.length) {
                    str = null;
                } else {
                    str = spanArr[i5].lng;
                    i5++;
                }
                int i10 = i5;
                arrayList.add(new TextRange(i6, i9, i4 > 0, i7 > 0, z, str));
                i6 = i9 + 1;
                if (i6 >= charSequence.length() || charSequence.charAt(i9) != '\n') {
                    i6 = i9;
                }
                i5 = i10;
            }
            if ((intValue4 & 2) != 0) {
                i4--;
            }
            if ((intValue4 & 1) != 0 || (intValue4 & 16) != 0) {
                i4++;
                z = (intValue4 & 16) != 0;
            }
            if ((intValue4 & 8) != 0) {
                i7--;
            }
            if ((intValue4 & 4) != 0) {
                i7++;
            }
        }
        if (i6 < charSequence.length()) {
            arrayList.add(new TextRange(i6, charSequence.length(), i4 > 0, i7 > 0, z, null));
        }
    }

    public static boolean expandedQuotesEquals(HashSet<Integer> hashSet, HashSet<Integer> hashSet2) {
        if (hashSet == null && hashSet2 == null) {
            return true;
        }
        return (hashSet == null ? 0 : hashSet.size()) == (hashSet2 == null ? 0 : hashSet2.size()) && hashSet != null && hashSet.equals(hashSet2);
    }

    public static String findAnimatedEmojiEmoticon(TLRPC.Document document) {
        return findAnimatedEmojiEmoticon(document, "😀");
    }

    public static String findAnimatedEmojiEmoticon(TLRPC.Document document, String str) {
        return findAnimatedEmojiEmoticon(document, str, null);
    }

    public static String findAnimatedEmojiEmoticon(TLRPC.Document document, String str, Integer num) {
        if (document == null) {
            return str;
        }
        int size = document.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if ((documentAttribute instanceof TLRPC.TL_documentAttributeCustomEmoji) || (documentAttribute instanceof TLRPC.TL_documentAttributeSticker)) {
                if (num != null) {
                    TLRPC.TL_messages_stickerSet stickerSet = MediaDataController.getInstance(num.intValue()).getStickerSet(documentAttribute.stickerset, true);
                    StringBuilder sb = new StringBuilder("");
                    if (stickerSet != null && stickerSet.packs != null) {
                        for (int i2 = 0; i2 < stickerSet.packs.size(); i2++) {
                            TLRPC.TL_stickerPack tL_stickerPack = stickerSet.packs.get(i2);
                            if (tL_stickerPack.documents.contains(Long.valueOf(document.id))) {
                                sb.append(tL_stickerPack.emoticon);
                            }
                        }
                    }
                    if (!TextUtils.isEmpty(sb)) {
                        return sb.toString();
                    }
                }
                return documentAttribute.alt;
            }
        }
        return str;
    }

    public static int findQuoteStart(String str, String str2, int i) {
        if (str == null || str2 == null) {
            return -1;
        }
        if (i == -1) {
            return str.indexOf(str2);
        }
        if (str2.length() + i < str.length() && str.startsWith(str2, i)) {
            return i;
        }
        int indexOf = str.indexOf(str2, i);
        int lastIndexOf = str.lastIndexOf(str2, i);
        return indexOf == -1 ? lastIndexOf : (lastIndexOf != -1 && indexOf - i >= i - lastIndexOf) ? lastIndexOf : indexOf;
    }

    public static ArrayList<String> findStickerEmoticons(TLRPC.Document document, Integer num) {
        if (document == null) {
            return null;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        int size = document.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if ((documentAttribute instanceof TLRPC.TL_documentAttributeCustomEmoji) || (documentAttribute instanceof TLRPC.TL_documentAttributeSticker)) {
                if (num != null) {
                    TLRPC.TL_messages_stickerSet stickerSet = MediaDataController.getInstance(num.intValue()).getStickerSet(documentAttribute.stickerset, true);
                    if (stickerSet != null && stickerSet.packs != null) {
                        for (int i2 = 0; i2 < stickerSet.packs.size(); i2++) {
                            TLRPC.TL_stickerPack tL_stickerPack = stickerSet.packs.get(i2);
                            if (tL_stickerPack.documents.contains(Long.valueOf(document.id)) && Emoji.getEmojiDrawable(tL_stickerPack.emoticon) != null) {
                                arrayList.add(tL_stickerPack.emoticon);
                            }
                        }
                    }
                    if (!arrayList.isEmpty()) {
                        return arrayList;
                    }
                }
                if (!TextUtils.isEmpty(documentAttribute.alt) && Emoji.getEmojiDrawable(documentAttribute.alt) != null) {
                    arrayList.add(documentAttribute.alt);
                    return arrayList;
                }
            }
        }
        return null;
    }

    public static void fixMessagePeer(ArrayList<TLRPC.Message> arrayList, long j) {
        if (arrayList == null || arrayList.isEmpty() || j == 0) {
            return;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC.Message message = arrayList.get(i);
            if (message instanceof TLRPC.TL_messageEmpty) {
                TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
                message.peer_id = tL_peerChannel;
                tL_peerChannel.channel_id = j;
            }
        }
    }

    public static long getChannelId(TLRPC.Message message) {
        TLRPC.Peer peer = message.peer_id;
        if (peer != null) {
            return peer.channel_id;
        }
        return 0L;
    }

    private org.telegram.tgnet.TLRPC.Chat getChat(java.util.AbstractMap<java.lang.Long, org.telegram.tgnet.TLRPC.Chat> r1, androidx.collection.LongSparseArray r2, long r3) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.getChat(java.util.AbstractMap, androidx.collection.LongSparseArray, long):org.telegram.tgnet.TLRPC$Chat");
    }

    public static long getChatId(TLRPC.Message message) {
        if (message == null) {
            return 0L;
        }
        TLRPC.Peer peer = message.peer_id;
        if (peer instanceof TLRPC.TL_peerChat) {
            return peer.chat_id;
        }
        if (peer instanceof TLRPC.TL_peerChannel) {
            return peer.channel_id;
        }
        return 0L;
    }

    public static long getDialogId(TLRPC.Message message) {
        TLRPC.Peer peer;
        long j;
        if (message.dialog_id == 0 && (peer = message.peer_id) != null) {
            long j2 = peer.chat_id;
            if (j2 != 0) {
                j = -j2;
            } else {
                long j3 = peer.channel_id;
                if (j3 != 0) {
                    j = -j3;
                } else {
                    j = ((message.from_id == null || isOut(message)) ? message.peer_id : message.from_id).user_id;
                }
            }
            message.dialog_id = j;
        }
        return message.dialog_id;
    }

    public static TLRPC.Document getDocument(TLRPC.Message message) {
        TLRPC.MessageMedia messageMedia;
        TLRPC.Document document;
        if (getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) {
            return getMedia(message).webpage.document;
        }
        if (getMedia(message) instanceof TLRPC.TL_messageMediaGame) {
            return getMedia(message).game.document;
        }
        if (getMedia(message) instanceof TLRPC.TL_messageMediaStory) {
            TL_stories.StoryItem storyItem = ((TLRPC.TL_messageMediaStory) getMedia(message)).storyItem;
            if (storyItem != null && (messageMedia = storyItem.media) != null && (document = messageMedia.document) != null) {
                return document;
            }
        } else if (getMedia(message) instanceof TLRPC.TL_messageMediaPaidMedia) {
            TLRPC.TL_messageMediaPaidMedia tL_messageMediaPaidMedia = (TLRPC.TL_messageMediaPaidMedia) getMedia(message);
            if (tL_messageMediaPaidMedia.extended_media.size() == 1 && (tL_messageMediaPaidMedia.extended_media.get(0) instanceof TLRPC.TL_messageExtendedMedia)) {
                return ((TLRPC.TL_messageExtendedMedia) tL_messageMediaPaidMedia.extended_media.get(0)).media.document;
            }
        }
        if (getMedia(message) != null) {
            return getMedia(message).document;
        }
        return null;
    }

    public static double getDocumentDuration(TLRPC.Document document) {
        int i;
        if (document == null) {
            return 0.0d;
        }
        int size = document.attributes.size();
        while (i < size) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            i = ((documentAttribute instanceof TLRPC.TL_documentAttributeVideo) || (documentAttribute instanceof TLRPC.TL_documentAttributeAudio)) ? 0 : i + 1;
            return documentAttribute.duration;
        }
        return 0.0d;
    }

    public static TLRPC.VideoSize getDocumentVideoThumb(TLRPC.Document document) {
        if (document == null || document.video_thumbs.isEmpty()) {
            return null;
        }
        return document.video_thumbs.get(0);
    }

    private TLRPC.Document getDocumentWithId(TLRPC.WebPage webPage, long j) {
        if (webPage != null && webPage.cached_page != null) {
            TLRPC.Document document = webPage.document;
            if (document != null && document.id == j) {
                return document;
            }
            for (int i = 0; i < webPage.cached_page.documents.size(); i++) {
                TLRPC.Document document2 = webPage.cached_page.documents.get(i);
                if (document2.id == j) {
                    return document2;
                }
            }
        }
        return null;
    }

    public static String getFileName(TLRPC.Document document) {
        return FileLoader.getAttachFileName(document);
    }

    public static String getFileName(TLRPC.Message message) {
        TLRPC.Document document;
        TLRPC.PhotoSize closestPhotoSizeWithSize;
        if (getMedia(message) instanceof TLRPC.TL_messageMediaDocument) {
            document = getDocument(message);
        } else {
            if (getMedia(message) instanceof TLRPC.TL_messageMediaPhoto) {
                ArrayList<TLRPC.PhotoSize> arrayList = getMedia(message).photo.sizes;
                return (arrayList.size() <= 0 || (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize())) == null) ? "" : FileLoader.getAttachFileName(closestPhotoSizeWithSize);
            }
            if (!(getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) || getMedia(message).webpage == null) {
                return "";
            }
            document = getMedia(message).webpage.document;
        }
        return getFileName(document);
    }

    public static String getFileName(TLRPC.MessageMedia messageMedia) {
        TLRPC.WebPage webPage;
        TLRPC.Document document;
        TLRPC.PhotoSize closestPhotoSizeWithSize;
        if (messageMedia instanceof TLRPC.TL_messageMediaDocument) {
            document = messageMedia.document;
        } else {
            if (messageMedia instanceof TLRPC.TL_messageMediaPhoto) {
                ArrayList<TLRPC.PhotoSize> arrayList = messageMedia.photo.sizes;
                return (arrayList.size() <= 0 || (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize())) == null) ? "" : FileLoader.getAttachFileName(closestPhotoSizeWithSize);
            }
            if (!(messageMedia instanceof TLRPC.TL_messageMediaWebPage) || (webPage = messageMedia.webpage) == null) {
                return "";
            }
            document = webPage.document;
        }
        return FileLoader.getAttachFileName(document);
    }

    public static long getFromChatId(TLRPC.Message message) {
        return getPeerId(message.from_id);
    }

    public static int getInlineResultDuration(TLRPC.BotInlineResult botInlineResult) {
        int webDocumentDuration = (int) getWebDocumentDuration(botInlineResult.content);
        return webDocumentDuration == 0 ? (int) getWebDocumentDuration(botInlineResult.thumb) : webDocumentDuration;
    }

    public static int[] getInlineResultWidthAndHeight(TLRPC.BotInlineResult botInlineResult) {
        int[] webDocumentWidthAndHeight = getWebDocumentWidthAndHeight(botInlineResult.content);
        if (webDocumentWidthAndHeight != null) {
            return webDocumentWidthAndHeight;
        }
        int[] webDocumentWidthAndHeight2 = getWebDocumentWidthAndHeight(botInlineResult.thumb);
        return webDocumentWidthAndHeight2 == null ? new int[]{0, 0} : webDocumentWidthAndHeight2;
    }

    public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        int size = document.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if ((documentAttribute instanceof TLRPC.TL_documentAttributeSticker) || (documentAttribute instanceof TLRPC.TL_documentAttributeCustomEmoji)) {
                TLRPC.InputStickerSet inputStickerSet = documentAttribute.stickerset;
                if (inputStickerSet instanceof TLRPC.TL_inputStickerSetEmpty) {
                    return null;
                }
                return inputStickerSet;
            }
        }
        return null;
    }

    public static TLRPC.InputStickerSet getInputStickerSet(TLRPC.Message message) {
        TLRPC.Document document = getDocument(message);
        if (document != null) {
            return getInputStickerSet(document);
        }
        return null;
    }

    public static TLRPC.MessageMedia getMedia(MessageObject messageObject) {
        TLRPC.Message message;
        if (messageObject == null || (message = messageObject.messageOwner) == null) {
            return null;
        }
        TLRPC.MessageMedia messageMedia = messageObject.sponsoredMedia;
        return messageMedia != null ? messageMedia : getMedia(message);
    }

    public static TLRPC.MessageMedia getMedia(TLRPC.Message message) {
        TLRPC.MessageMedia messageMedia = message.media;
        return (messageMedia == null || (messageMedia instanceof TLRPC.TL_messageMediaPaidMedia) || messageMedia.extended_media.isEmpty() || !(message.media.extended_media.get(0) instanceof TLRPC.TL_messageExtendedMedia)) ? message.media : ((TLRPC.TL_messageExtendedMedia) message.media.extended_media.get(0)).media;
    }

    public static long getMediaSize(TLRPC.MessageMedia messageMedia) {
        TLRPC.WebPage webPage;
        TLRPC.Document document = (!(messageMedia instanceof TLRPC.TL_messageMediaWebPage) || (webPage = messageMedia.webpage) == null) ? messageMedia instanceof TLRPC.TL_messageMediaGame ? messageMedia.game.document : messageMedia != null ? messageMedia.document : null : webPage.document;
        if (document != null) {
            return document.size;
        }
        return 0L;
    }

    private MessageObject getMessageObjectForBlock(TLRPC.WebPage webPage, TLRPC.PageBlock pageBlock) {
        TLRPC.TL_message tL_message;
        if (pageBlock instanceof TLRPC.TL_pageBlockPhoto) {
            TLRPC.Photo photoWithId = getPhotoWithId(webPage, ((TLRPC.TL_pageBlockPhoto) pageBlock).photo_id);
            if (photoWithId == webPage.photo) {
                return this;
            }
            tL_message = new TLRPC.TL_message();
            TLRPC.TL_messageMediaPhoto tL_messageMediaPhoto = new TLRPC.TL_messageMediaPhoto();
            tL_message.media = tL_messageMediaPhoto;
            tL_messageMediaPhoto.photo = photoWithId;
        } else if (pageBlock instanceof TLRPC.TL_pageBlockVideo) {
            TLRPC.TL_pageBlockVideo tL_pageBlockVideo = (TLRPC.TL_pageBlockVideo) pageBlock;
            if (getDocumentWithId(webPage, tL_pageBlockVideo.video_id) == webPage.document) {
                return this;
            }
            TLRPC.TL_message tL_message2 = new TLRPC.TL_message();
            TLRPC.TL_messageMediaDocument tL_messageMediaDocument = new TLRPC.TL_messageMediaDocument();
            tL_message2.media = tL_messageMediaDocument;
            tL_messageMediaDocument.document = getDocumentWithId(webPage, tL_pageBlockVideo.video_id);
            tL_message = tL_message2;
        } else {
            tL_message = null;
        }
        tL_message.message = "";
        tL_message.realId = getId();
        tL_message.id = Utilities.random.nextInt();
        TLRPC.Message message = this.messageOwner;
        tL_message.date = message.date;
        tL_message.peer_id = message.peer_id;
        tL_message.out = message.out;
        tL_message.from_id = message.from_id;
        return new MessageObject(this.currentAccount, tL_message, false, true);
    }

    public static long getMessageSize(TLRPC.Message message) {
        return getMediaSize(getMedia(message));
    }

    public static long getObjectPeerId(TLObject tLObject) {
        if (tLObject == null) {
            return 0L;
        }
        if (tLObject instanceof TLRPC.Chat) {
            return -((TLRPC.Chat) tLObject).id;
        }
        if (tLObject instanceof TLRPC.User) {
            return ((TLRPC.User) tLObject).id;
        }
        return 0L;
    }

    private int getParentWidth() {
        int i;
        return (!this.preview || (i = this.parentWidth) <= 0) ? AndroidUtilities.displaySize.x : i;
    }

    public static long getPeerId(TLRPC.Peer peer) {
        long j;
        if (peer == null) {
            return 0L;
        }
        if (peer instanceof TLRPC.TL_peerChat) {
            j = peer.chat_id;
        } else {
            if (!(peer instanceof TLRPC.TL_peerChannel)) {
                return peer.user_id;
            }
            j = peer.channel_id;
        }
        return -j;
    }

    public static String getPeerObjectName(TLObject tLObject) {
        return tLObject instanceof TLRPC.User ? UserObject.getUserName((TLRPC.User) tLObject) : tLObject instanceof TLRPC.Chat ? ((TLRPC.Chat) tLObject).title : "DELETED";
    }

    public static TLRPC.Photo getPhoto(TLRPC.Message message) {
        if (getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) {
            return getMedia(message).webpage.photo;
        }
        if (getMedia(message) != null) {
            return getMedia(message).photo;
        }
        return null;
    }

    private TLRPC.Photo getPhotoWithId(TLRPC.WebPage webPage, long j) {
        if (webPage != null && webPage.cached_page != null) {
            TLRPC.Photo photo = webPage.photo;
            if (photo != null && photo.id == j) {
                return photo;
            }
            for (int i = 0; i < webPage.cached_page.photos.size(); i++) {
                TLRPC.Photo photo2 = webPage.cached_page.photos.get(i);
                if (photo2.id == j) {
                    return photo2;
                }
            }
        }
        return null;
    }

    public static TLRPC.VideoSize getPremiumStickerAnimation(TLRPC.Document document) {
        if (document != null && document.thumbs != null) {
            for (int i = 0; i < document.video_thumbs.size(); i++) {
                if ("f".equals(document.video_thumbs.get(i).type)) {
                    return document.video_thumbs.get(i);
                }
            }
        }
        return null;
    }

    public static int getQuickReplyId(int i, TLRPC.Message message) {
        QuickRepliesController.QuickReply findReply;
        if (message == null) {
            return 0;
        }
        if ((message.flags & 1073741824) != 0) {
            return message.quick_reply_shortcut_id;
        }
        TLRPC.InputQuickReplyShortcut inputQuickReplyShortcut = message.quick_reply_shortcut;
        if (inputQuickReplyShortcut instanceof TLRPC.TL_inputQuickReplyShortcutId) {
            return ((TLRPC.TL_inputQuickReplyShortcutId) inputQuickReplyShortcut).shortcut_id;
        }
        String quickReplyName = getQuickReplyName(message);
        if (quickReplyName == null || (findReply = QuickRepliesController.getInstance(i).findReply(quickReplyName)) == null) {
            return 0;
        }
        return findReply.id;
    }

    public static int getQuickReplyId(TLRPC.Message message) {
        if (message == null) {
            return 0;
        }
        if ((message.flags & 1073741824) != 0) {
            return message.quick_reply_shortcut_id;
        }
        TLRPC.InputQuickReplyShortcut inputQuickReplyShortcut = message.quick_reply_shortcut;
        if (inputQuickReplyShortcut instanceof TLRPC.TL_inputQuickReplyShortcutId) {
            return ((TLRPC.TL_inputQuickReplyShortcutId) inputQuickReplyShortcut).shortcut_id;
        }
        return 0;
    }

    public static String getQuickReplyName(TLRPC.Message message) {
        if (message == null) {
            return null;
        }
        TLRPC.InputQuickReplyShortcut inputQuickReplyShortcut = message.quick_reply_shortcut;
        if (inputQuickReplyShortcut instanceof TLRPC.TL_inputQuickReplyShortcut) {
            return ((TLRPC.TL_inputQuickReplyShortcut) inputQuickReplyShortcut).shortcut;
        }
        return null;
    }

    public static long getReplyToDialogId(TLRPC.Message message) {
        TLRPC.MessageReplyHeader messageReplyHeader = message.reply_to;
        if (messageReplyHeader == null) {
            return 0L;
        }
        TLRPC.Peer peer = messageReplyHeader.reply_to_peer_id;
        return peer != null ? getPeerId(peer) : getDialogId(message);
    }

    public static long getSavedDialogId(long j, TLRPC.Message message) {
        TLRPC.Peer peer;
        TLRPC.Peer peer2 = message.saved_peer_id;
        if (peer2 != null) {
            long j2 = peer2.chat_id;
            if (j2 != 0) {
                return -j2;
            }
            long j3 = peer2.channel_id;
            return j3 != 0 ? -j3 : peer2.user_id;
        }
        if (message.from_id.user_id != j) {
            return 0L;
        }
        TLRPC.MessageFwdHeader messageFwdHeader = message.fwd_from;
        if (messageFwdHeader != null && (peer = messageFwdHeader.saved_from_peer) != null) {
            return DialogObject.getPeerDialogId(peer);
        }
        if ((messageFwdHeader == null || messageFwdHeader.from_id == null) && messageFwdHeader != null) {
            return 2666000L;
        }
        return j;
    }

    public static TLRPC.Peer getSavedDialogPeer(long j, TLRPC.Message message) {
        TLRPC.Peer peer;
        TLRPC.TL_peerUser tL_peerUser;
        TLRPC.Peer peer2;
        TLRPC.Peer peer3 = message.saved_peer_id;
        if (peer3 != null) {
            return peer3;
        }
        TLRPC.Peer peer4 = message.peer_id;
        if (peer4 == null || peer4.user_id != j || (peer = message.from_id) == null || peer.user_id != j) {
            return null;
        }
        TLRPC.MessageFwdHeader messageFwdHeader = message.fwd_from;
        if (messageFwdHeader != null && (peer2 = messageFwdHeader.saved_from_peer) != null) {
            return peer2;
        }
        if (messageFwdHeader != null && messageFwdHeader.from_id != null) {
            tL_peerUser = new TLRPC.TL_peerUser();
        } else {
            if (messageFwdHeader != null) {
                TLRPC.TL_peerUser tL_peerUser2 = new TLRPC.TL_peerUser();
                tL_peerUser2.user_id = 2666000L;
                return tL_peerUser2;
            }
            tL_peerUser = new TLRPC.TL_peerUser();
        }
        tL_peerUser.user_id = j;
        return tL_peerUser;
    }

    public static long getStickerSetId(TLRPC.Document document) {
        if (document == null) {
            return -1L;
        }
        for (int i = 0; i < document.attributes.size(); i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeSticker) {
                TLRPC.InputStickerSet inputStickerSet = documentAttribute.stickerset;
                if (inputStickerSet instanceof TLRPC.TL_inputStickerSetEmpty) {
                    return -1L;
                }
                return inputStickerSet.id;
            }
        }
        return -1L;
    }

    public static String getStickerSetName(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        for (int i = 0; i < document.attributes.size(); i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeSticker) {
                TLRPC.InputStickerSet inputStickerSet = documentAttribute.stickerset;
                if (inputStickerSet instanceof TLRPC.TL_inputStickerSetEmpty) {
                    return null;
                }
                return inputStickerSet.short_name;
            }
        }
        return null;
    }

    private CharSequence getStringFrom(TLRPC.ChatReactions chatReactions) {
        int i;
        if (chatReactions instanceof TLRPC.TL_chatReactionsAll) {
            i = R.string.AllReactions;
        } else {
            if (chatReactions instanceof TLRPC.TL_chatReactionsSome) {
                TLRPC.TL_chatReactionsSome tL_chatReactionsSome = (TLRPC.TL_chatReactionsSome) chatReactions;
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                for (int i2 = 0; i2 < tL_chatReactionsSome.reactions.size(); i2++) {
                    if (i2 != 0) {
                        spannableStringBuilder.append((CharSequence) " ");
                    }
                    spannableStringBuilder.append(Emoji.replaceEmoji(ReactionsUtils.reactionToCharSequence(tL_chatReactionsSome.reactions.get(i2)), null, false));
                }
                return spannableStringBuilder;
            }
            i = R.string.NoReactions;
        }
        return LocaleController.getString(i);
    }

    private static long getTopicId(int i, TLRPC.Message message) {
        return getTopicId(i, message, false);
    }

    public static long getTopicId(int i, TLRPC.Message message, boolean z) {
        int i2;
        long clientUserId = UserConfig.getInstance(i).getClientUserId();
        if ((message.flags & 1073741824) != 0 && DialogObject.getPeerDialogId(message.peer_id) == clientUserId) {
            i2 = message.quick_reply_shortcut_id;
        } else {
            if (!z && i >= 0 && DialogObject.getPeerDialogId(message.peer_id) == clientUserId) {
                return getSavedDialogId(clientUserId, message);
            }
            TLRPC.MessageAction messageAction = message.action;
            if (!(messageAction instanceof TLRPC.TL_messageActionTopicCreate)) {
                TLRPC.MessageReplyHeader messageReplyHeader = message.reply_to;
                if (messageReplyHeader == null || !messageReplyHeader.forum_topic) {
                    return z ? 1L : 0L;
                }
                if (!(message instanceof TLRPC.TL_messageService) || (messageAction instanceof TLRPC.TL_messageActionPinMessage)) {
                    int i3 = messageReplyHeader.reply_to_top_id;
                    if (i3 == 0) {
                        i3 = messageReplyHeader.reply_to_msg_id;
                    }
                    return i3;
                }
                int i4 = messageReplyHeader.reply_to_msg_id;
                if (i4 == 0) {
                    i4 = messageReplyHeader.reply_to_top_id;
                }
                return i4;
            }
            i2 = message.id;
        }
        return i2;
    }

    private static long getTopicId(MessageObject messageObject) {
        if (messageObject == null) {
            return 0L;
        }
        return getTopicId(messageObject.currentAccount, messageObject.messageOwner, false);
    }

    public static int getUnreadFlags(TLRPC.Message message) {
        int i = !message.unread ? 1 : 0;
        return !message.media_unread ? i | 2 : i;
    }

    private org.telegram.tgnet.TLRPC.User getUser(java.util.AbstractMap<java.lang.Long, org.telegram.tgnet.TLRPC.User> r1, androidx.collection.LongSparseArray r2, long r3) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.getUser(java.util.AbstractMap, androidx.collection.LongSparseArray, long):org.telegram.tgnet.TLRPC$User");
    }

    private String getUserName(TLObject tLObject, ArrayList<TLRPC.MessageEntity> arrayList, int i) {
        String str;
        String publicUsername;
        long j;
        String str2;
        String str3;
        long j2;
        if (tLObject == null) {
            str2 = "";
            str3 = null;
            j2 = 0;
        } else {
            if (tLObject instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) tLObject;
                str = user.deleted ? LocaleController.getString(R.string.HiddenName) : ContactsController.formatName(user.first_name, user.last_name);
                publicUsername = UserObject.getPublicUsername(user);
                j = user.id;
            } else {
                TLRPC.Chat chat = (TLRPC.Chat) tLObject;
                str = chat.title;
                publicUsername = ChatObject.getPublicUsername(chat);
                j = -chat.id;
            }
            str2 = str;
            str3 = publicUsername;
            j2 = j;
        }
        if (i >= 0) {
            TLRPC.TL_messageEntityMentionName tL_messageEntityMentionName = new TLRPC.TL_messageEntityMentionName();
            tL_messageEntityMentionName.user_id = j2;
            tL_messageEntityMentionName.offset = i;
            tL_messageEntityMentionName.length = str2.length();
            arrayList.add(tL_messageEntityMentionName);
        }
        if (TextUtils.isEmpty(str3)) {
            return str2;
        }
        if (i >= 0) {
            TLRPC.TL_messageEntityMentionName tL_messageEntityMentionName2 = new TLRPC.TL_messageEntityMentionName();
            tL_messageEntityMentionName2.user_id = j2;
            tL_messageEntityMentionName2.offset = i + str2.length() + 2;
            tL_messageEntityMentionName2.length = str3.length() + 1;
            arrayList.add(tL_messageEntityMentionName2);
        }
        return String.format("%1$s (@%2$s)", str2, str3);
    }

    private String getUsernamesString(ArrayList<String> arrayList) {
        if (arrayList == null || arrayList.size() == 0) {
            return LocaleController.getString(R.string.UsernameEmpty).toLowerCase();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arrayList.size(); i++) {
            sb.append("@");
            sb.append(arrayList.get(i));
            if (i < arrayList.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public static String getVideoCodec(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        int size = document.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                return ((TLRPC.TL_documentAttributeVideo) documentAttribute).video_codec;
            }
        }
        return null;
    }

    public static int getVideoHeight(TLRPC.Document document) {
        if (document == null) {
            return 0;
        }
        int size = document.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                return documentAttribute.h;
            }
        }
        return 0;
    }

    public static int getVideoWidth(TLRPC.Document document) {
        if (document == null) {
            return 0;
        }
        int size = document.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                return documentAttribute.w;
            }
        }
        return 0;
    }

    public static double getWebDocumentDuration(TLRPC.WebDocument webDocument) {
        int i;
        if (webDocument == null) {
            return 0.0d;
        }
        int size = webDocument.attributes.size();
        while (i < size) {
            TLRPC.DocumentAttribute documentAttribute = webDocument.attributes.get(i);
            i = ((documentAttribute instanceof TLRPC.TL_documentAttributeVideo) || (documentAttribute instanceof TLRPC.TL_documentAttributeAudio)) ? 0 : i + 1;
            return documentAttribute.duration;
        }
        return 0.0d;
    }

    public static int[] getWebDocumentWidthAndHeight(TLRPC.WebDocument webDocument) {
        if (webDocument == null) {
            return null;
        }
        int size = webDocument.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC.DocumentAttribute documentAttribute = webDocument.attributes.get(i);
            if ((documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) || (documentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
                return new int[]{documentAttribute.w, documentAttribute.h};
            }
        }
        return null;
    }

    public static CharSequence groupSpan() {
        if (groupSpan == null) {
            groupSpan = new SpannableStringBuilder("g");
            ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.msg_folders_groups);
            coloredImageSpan.setScale(0.7f, 0.7f);
            ((SpannableStringBuilder) groupSpan).setSpan(coloredImageSpan, 0, 1, 33);
        }
        return groupSpan;
    }

    private void handleFoundWords(ArrayList<String> arrayList, String[] strArr, boolean z) {
        handleFoundWords(arrayList, strArr, z, true);
    }

    private void handleFoundWords(ArrayList<String> arrayList, String[] strArr, boolean z, boolean z2) {
        TLRPC.Message message;
        TLRPC.MessageReplyHeader messageReplyHeader;
        boolean z3;
        if (arrayList.isEmpty()) {
            return;
        }
        boolean z4 = false;
        for (int i = 0; i < arrayList.size(); i++) {
            int i2 = 0;
            while (true) {
                if (i2 >= strArr.length) {
                    break;
                }
                if (arrayList.get(i).contains(strArr[i2])) {
                    z4 = true;
                    break;
                }
                i2++;
            }
            if (z4) {
                break;
            }
        }
        if (z4) {
            int i3 = 0;
            while (i3 < arrayList.size()) {
                int i4 = 0;
                while (true) {
                    if (i4 >= strArr.length) {
                        z3 = false;
                        break;
                    } else {
                        if (arrayList.get(i3).contains(strArr[i4])) {
                            z3 = true;
                            break;
                        }
                        i4++;
                    }
                }
                if (!z3) {
                    arrayList.remove(i3);
                    i3--;
                }
                i3++;
            }
            if (arrayList.size() > 0) {
                Collections.sort(arrayList, new Comparator() {
                    @Override
                    public final int compare(Object obj, Object obj2) {
                        int lambda$handleFoundWords$3;
                        lambda$handleFoundWords$3 = MessageObject.lambda$handleFoundWords$3((String) obj, (String) obj2);
                        return lambda$handleFoundWords$3;
                    }
                });
                String str = arrayList.get(0);
                arrayList.clear();
                arrayList.add(str);
            }
        }
        this.highlightedWords = arrayList;
        if (this.messageOwner.message != null) {
            applyEntities();
            CharSequence replaceMultipleCharSequence = AndroidUtilities.replaceMultipleCharSequence("\n", !TextUtils.isEmpty(this.caption) ? this.caption : this.messageText, " ");
            if (z && (message = this.messageOwner) != null && (messageReplyHeader = message.reply_to) != null && messageReplyHeader.quote_text != null) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.messageOwner.reply_to.quote_text);
                addEntitiesToText(spannableStringBuilder, this.messageOwner.reply_to.quote_entities, isOutOwner(), false, false, false);
                SpannableString spannableString = new SpannableString("q ");
                ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.mini_quote);
                coloredImageSpan.setOverrideColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
                spannableString.setSpan(coloredImageSpan, 0, 1, 33);
                replaceMultipleCharSequence = new SpannableStringBuilder(spannableString).append((CharSequence) spannableStringBuilder).append('\n').append(replaceMultipleCharSequence);
            }
            String charSequence = replaceMultipleCharSequence.toString();
            int length = charSequence.length();
            int indexOf = charSequence.toLowerCase().indexOf(arrayList.get(0));
            if (indexOf < 0) {
                indexOf = 0;
            }
            if (length > 120 && z2) {
                float f = 120;
                int max = Math.max(0, indexOf - ((int) (0.1f * f)));
                replaceMultipleCharSequence = replaceMultipleCharSequence.subSequence(max, Math.min(length, (indexOf - max) + indexOf + ((int) (f * 0.9f))));
            }
            this.messageTrimmedToHighlight = replaceMultipleCharSequence;
            this.messageTrimmedToHighlightCut = z2;
        }
    }

    private boolean hasNonEmojiEntities() {
        TLRPC.Message message = this.messageOwner;
        if (message != null && message.entities != null) {
            for (int i = 0; i < this.messageOwner.entities.size(); i++) {
                if (!(this.messageOwner.entities.get(i) instanceof TLRPC.TL_messageEntityCustomEmoji)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean hasUnreadReactions(TLRPC.Message message) {
        if (message == null) {
            return false;
        }
        return hasUnreadReactions(message.reactions);
    }

    public static boolean hasUnreadReactions(TLRPC.TL_messageReactions tL_messageReactions) {
        if (tL_messageReactions == null) {
            return false;
        }
        for (int i = 0; i < tL_messageReactions.recent_reactions.size(); i++) {
            if (tL_messageReactions.recent_reactions.get(i).unread) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAnimatedEmoji(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        int size = document.attributes.size();
        for (int i = 0; i < size; i++) {
            if (document.attributes.get(i) instanceof TLRPC.TL_documentAttributeCustomEmoji) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAnimatedStickerDocument(TLRPC.Document document) {
        return document != null && document.mime_type.equals("video/webm");
    }

    public static boolean isAnimatedStickerDocument(TLRPC.Document document, boolean z) {
        if (document != null && (("application/x-tgsticker".equals(document.mime_type) && !document.thumbs.isEmpty()) || "application/x-tgsdice".equals(document.mime_type))) {
            if (z) {
                return true;
            }
            int size = document.attributes.size();
            for (int i = 0; i < size; i++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeSticker) {
                    return documentAttribute.stickerset instanceof TLRPC.TL_inputStickerSetShortName;
                }
                if (documentAttribute instanceof TLRPC.TL_documentAttributeCustomEmoji) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isAnimatedStickerMessage(TLRPC.Message message) {
        boolean isEncryptedDialog = DialogObject.isEncryptedDialog(message.dialog_id);
        if ((!isEncryptedDialog || message.stickerVerified == 1) && getMedia(message) != null) {
            return isAnimatedStickerDocument(getMedia(message).document, !isEncryptedDialog || message.out);
        }
        return false;
    }

    public static boolean isContentUnread(TLRPC.Message message) {
        return message.media_unread;
    }

    public static boolean isDocumentHasAttachedStickers(TLRPC.Document document) {
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                if (document.attributes.get(i) instanceof TLRPC.TL_documentAttributeHasStickers) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isDocumentHasThumb(TLRPC.Document document) {
        if (document != null && !document.thumbs.isEmpty()) {
            int size = document.thumbs.size();
            for (int i = 0; i < size; i++) {
                TLRPC.PhotoSize photoSize = document.thumbs.get(i);
                if (photoSize != null && !(photoSize instanceof TLRPC.TL_photoSizeEmpty) && (!(photoSize.location instanceof TLRPC.TL_fileLocationUnavailable) || photoSize.bytes != null)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isExtendedVideo(TLRPC.MessageExtendedMedia messageExtendedMedia) {
        if (!(messageExtendedMedia instanceof TLRPC.TL_messageExtendedMedia)) {
            return (messageExtendedMedia instanceof TLRPC.TL_messageExtendedMediaPreview) && (((TLRPC.TL_messageExtendedMediaPreview) messageExtendedMedia).flags & 4) != 0;
        }
        TLRPC.MessageMedia messageMedia = ((TLRPC.TL_messageExtendedMedia) messageExtendedMedia).media;
        return (messageMedia instanceof TLRPC.TL_messageMediaDocument) && isVideoDocument(messageMedia.document);
    }

    public static boolean isForwardedMessage(TLRPC.Message message) {
        return ((message.flags & 4) == 0 || message.fwd_from == null) ? false : true;
    }

    public static boolean isFreeEmoji(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        int size = document.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeCustomEmoji) {
                return ((TLRPC.TL_documentAttributeCustomEmoji) documentAttribute).free;
            }
        }
        return false;
    }

    public static boolean isGameMessage(TLRPC.Message message) {
        return getMedia(message) instanceof TLRPC.TL_messageMediaGame;
    }

    public static boolean isGifDocument(WebFile webFile) {
        return webFile != null && (webFile.mime_type.equals("image/gif") || isNewGifDocument(webFile));
    }

    public static boolean isGifDocument(TLRPC.Document document) {
        return isGifDocument(document, false);
    }

    public static boolean isGifDocument(TLRPC.Document document, boolean z) {
        String str;
        return (document == null || (str = document.mime_type) == null || ((!str.equals("image/gif") || z) && !isNewGifDocument(document))) ? false : true;
    }

    public static boolean isGifMessage(TLRPC.Message message) {
        if (getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) {
            return isGifDocument(getMedia(message).webpage.document);
        }
        if (getMedia(message) != null) {
            return isGifDocument(getMedia(message).document, (message.grouped_id > 0L ? 1 : (message.grouped_id == 0L ? 0 : -1)) != 0);
        }
        return false;
    }

    public static boolean isImageWebDocument(WebFile webFile) {
        return (webFile == null || isGifDocument(webFile) || !webFile.mime_type.startsWith("image/")) ? false : true;
    }

    public static boolean isInvoiceMessage(TLRPC.Message message) {
        return getMedia(message) instanceof TLRPC.TL_messageMediaInvoice;
    }

    public static boolean isLiveLocationMessage(TLRPC.Message message) {
        return getMedia(message) instanceof TLRPC.TL_messageMediaGeoLive;
    }

    public static boolean isLocationMessage(TLRPC.Message message) {
        return (getMedia(message) instanceof TLRPC.TL_messageMediaGeo) || (getMedia(message) instanceof TLRPC.TL_messageMediaGeoLive) || (getMedia(message) instanceof TLRPC.TL_messageMediaVenue);
    }

    public static boolean isMaskDocument(TLRPC.Document document) {
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if ((documentAttribute instanceof TLRPC.TL_documentAttributeSticker) && documentAttribute.mask) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isMaskMessage(TLRPC.Message message) {
        return getMedia(message) != null && isMaskDocument(getMedia(message).document);
    }

    public static boolean isMediaEmpty(TLRPC.Message message) {
        return isMediaEmpty(message, true);
    }

    public static boolean isMediaEmpty(TLRPC.Message message, boolean z) {
        return message == null || getMedia(message) == null || (getMedia(message) instanceof TLRPC.TL_messageMediaEmpty) || (z && (getMedia(message) instanceof TLRPC.TL_messageMediaWebPage));
    }

    public static boolean isMediaEmptyWebpage(TLRPC.Message message) {
        return message == null || getMedia(message) == null || (getMedia(message) instanceof TLRPC.TL_messageMediaEmpty);
    }

    public static boolean isMusicDocument(TLRPC.Document document) {
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                if (document.attributes.get(i) instanceof TLRPC.TL_documentAttributeAudio) {
                    return !r2.voice;
                }
            }
            if (!TextUtils.isEmpty(document.mime_type)) {
                String lowerCase = document.mime_type.toLowerCase();
                if (lowerCase.equals("audio/flac") || lowerCase.equals("audio/ogg") || lowerCase.equals("audio/opus") || lowerCase.equals("audio/x-opus+ogg") || (lowerCase.equals("application/octet-stream") && FileLoader.getDocumentFileName(document).endsWith(".opus"))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isMusicMessage(TLRPC.Message message) {
        return getMedia(message) instanceof TLRPC.TL_messageMediaWebPage ? isMusicDocument(getMedia(message).webpage.document) : getMedia(message) != null && isMusicDocument(getMedia(message).document);
    }

    public static Boolean isMyPaidReactionAnonymous(TLRPC.MessageReactions messageReactions) {
        ArrayList<TLRPC.MessageReactor> arrayList;
        if (messageReactions == null || (arrayList = messageReactions.top_reactors) == null) {
            return null;
        }
        Iterator<TLRPC.MessageReactor> it = arrayList.iterator();
        while (it.hasNext()) {
            TLRPC.MessageReactor next = it.next();
            if (next != null && next.my) {
                return Boolean.valueOf(next.anonymous);
            }
        }
        return null;
    }

    public static boolean isNewGifDocument(WebFile webFile) {
        if (webFile != null && "video/mp4".equals(webFile.mime_type)) {
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < webFile.attributes.size(); i3++) {
                TLRPC.DocumentAttribute documentAttribute = webFile.attributes.get(i3);
                if (!(documentAttribute instanceof TLRPC.TL_documentAttributeAnimated) && (documentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
                    i = documentAttribute.w;
                    i2 = documentAttribute.h;
                }
            }
            if (i <= 1280 && i2 <= 1280) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNewGifDocument(TLRPC.Document document) {
        if (document != null && "video/mp4".equals(document.mime_type)) {
            boolean z = false;
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < document.attributes.size(); i3++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i3);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeAnimated) {
                    z = true;
                } else if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                    i = documentAttribute.w;
                    i2 = documentAttribute.h;
                }
            }
            if (z && i <= 1280 && i2 <= 1280) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNewGifMessage(TLRPC.Message message) {
        return getMedia(message) instanceof TLRPC.TL_messageMediaWebPage ? isNewGifDocument(getMedia(message).webpage.document) : getMedia(message) != null && isNewGifDocument(getMedia(message).document);
    }

    public static boolean isOut(TLRPC.Message message) {
        return message.out;
    }

    public static boolean isPaidVideo(TLRPC.MessageMedia messageMedia) {
        return (messageMedia instanceof TLRPC.TL_messageMediaPaidMedia) && messageMedia.extended_media.size() == 1 && isExtendedVideo(messageMedia.extended_media.get(0));
    }

    public static boolean isPhoto(TLRPC.Message message) {
        TLRPC.MessageAction messageAction;
        TLRPC.Photo photo;
        return getMedia(message) instanceof TLRPC.TL_messageMediaWebPage ? (getMedia(message).webpage.photo instanceof TLRPC.TL_photo) && !(getMedia(message).webpage.document instanceof TLRPC.TL_document) : (message == null || (messageAction = message.action) == null || (photo = messageAction.photo) == null) ? getMedia(message) instanceof TLRPC.TL_messageMediaPhoto : photo instanceof TLRPC.TL_photo;
    }

    public static boolean isPremiumEmojiPack(TLRPC.StickerSetCovered stickerSetCovered) {
        TLRPC.StickerSet stickerSet;
        if (stickerSetCovered != null && (stickerSet = stickerSetCovered.set) != null && !stickerSet.emojis) {
            return false;
        }
        ArrayList<TLRPC.Document> arrayList = stickerSetCovered instanceof TLRPC.TL_stickerSetFullCovered ? ((TLRPC.TL_stickerSetFullCovered) stickerSetCovered).documents : stickerSetCovered.covers;
        if (stickerSetCovered != null && arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (!isFreeEmoji(arrayList.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPremiumEmojiPack(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        TLRPC.StickerSet stickerSet;
        if ((tL_messages_stickerSet == null || (stickerSet = tL_messages_stickerSet.set) == null || stickerSet.emojis) && tL_messages_stickerSet != null && tL_messages_stickerSet.documents != null) {
            for (int i = 0; i < tL_messages_stickerSet.documents.size(); i++) {
                if (!isFreeEmoji(tL_messages_stickerSet.documents.get(i))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPremiumSticker(TLRPC.Document document) {
        if (document != null && document.thumbs != null) {
            for (int i = 0; i < document.video_thumbs.size(); i++) {
                if ("f".equals(document.video_thumbs.get(i).type)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isQuickReply(TLRPC.Message message) {
        return (message == null || ((message.flags & 1073741824) == 0 && message.quick_reply_shortcut == null)) ? false : true;
    }

    public static boolean isRoundVideoDocument(TLRPC.Document document) {
        if (document != null && "video/mp4".equals(document.mime_type)) {
            boolean z = false;
            int i = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < document.attributes.size(); i3++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i3);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                    i = documentAttribute.w;
                    i2 = documentAttribute.h;
                    z = documentAttribute.round_message;
                }
            }
            if (z && i <= 1280 && i2 <= 1280) {
                return true;
            }
        }
        return false;
    }

    public static boolean isRoundVideoMessage(TLRPC.Message message) {
        return (!(getMedia(message) instanceof TLRPC.TL_messageMediaWebPage) || getMedia(message).webpage == null) ? getMedia(message) != null && isRoundVideoDocument(getMedia(message).document) : isRoundVideoDocument(getMedia(message).webpage.document);
    }

    public static boolean isSecretMedia(TLRPC.Message message) {
        if (message instanceof TLRPC.TL_message_secret) {
            return ((getMedia(message) instanceof TLRPC.TL_messageMediaPhoto) || isRoundVideoMessage(message) || isVideoMessage(message)) && getMedia(message).ttl_seconds != 0;
        }
        if (message instanceof TLRPC.TL_message) {
            return ((getMedia(message) instanceof TLRPC.TL_messageMediaPhoto) || (getMedia(message) instanceof TLRPC.TL_messageMediaDocument)) && getMedia(message).ttl_seconds != 0;
        }
        return false;
    }

    public static boolean isSecretPhotoOrVideo(TLRPC.Message message) {
        int i;
        if (message instanceof TLRPC.TL_message_secret) {
            return ((getMedia(message) instanceof TLRPC.TL_messageMediaPhoto) || isRoundVideoMessage(message) || isVideoMessage(message)) && (i = message.ttl) > 0 && i <= 60;
        }
        if (message instanceof TLRPC.TL_message) {
            return ((getMedia(message) instanceof TLRPC.TL_messageMediaPhoto) || (getMedia(message) instanceof TLRPC.TL_messageMediaDocument)) && getMedia(message).ttl_seconds != 0;
        }
        return false;
    }

    public static boolean isStaticStickerDocument(TLRPC.Document document) {
        return document != null && document.mime_type.equals("image/webp");
    }

    public static boolean isStickerDocument(TLRPC.Document document) {
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                if (document.attributes.get(i) instanceof TLRPC.TL_documentAttributeSticker) {
                    return "image/webp".equals(document.mime_type) || "video/webm".equals(document.mime_type);
                }
            }
        }
        return false;
    }

    public static boolean isStickerHasSet(TLRPC.Document document) {
        TLRPC.InputStickerSet inputStickerSet;
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if ((documentAttribute instanceof TLRPC.TL_documentAttributeSticker) && (inputStickerSet = documentAttribute.stickerset) != null && !(inputStickerSet instanceof TLRPC.TL_inputStickerSetEmpty)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isStickerMessage(TLRPC.Message message) {
        return getMedia(message) != null && isStickerDocument(getMedia(message).document);
    }

    public static boolean isSystemSignUp(MessageObject messageObject) {
        if (messageObject != null) {
            TLRPC.Message message = messageObject.messageOwner;
            if ((message instanceof TLRPC.TL_messageService) && (((TLRPC.TL_messageService) message).action instanceof TLRPC.TL_messageActionContactSignUp)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isTextColorEmoji(TLRPC.Document document) {
        if (document == null) {
            return false;
        }
        getInputStickerSet(document);
        int size = document.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeCustomEmoji) {
                TLRPC.InputStickerSet inputStickerSet = documentAttribute.stickerset;
                if ((inputStickerSet instanceof TLRPC.TL_inputStickerSetID) && inputStickerSet.id == 1269403972611866647L) {
                    return true;
                }
                return ((TLRPC.TL_documentAttributeCustomEmoji) documentAttribute).text_color;
            }
        }
        return false;
    }

    public static boolean isTextColorSet(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        TLRPC.StickerSet stickerSet;
        if (tL_messages_stickerSet != null && (stickerSet = tL_messages_stickerSet.set) != null) {
            if (stickerSet.text_color) {
                return true;
            }
            ArrayList<TLRPC.Document> arrayList = tL_messages_stickerSet.documents;
            if (arrayList != null && !arrayList.isEmpty()) {
                return isTextColorEmoji(tL_messages_stickerSet.documents.get(0));
            }
        }
        return false;
    }

    public static boolean isTopicActionMessage(MessageObject messageObject) {
        TLRPC.Message message;
        if (messageObject == null || (message = messageObject.messageOwner) == null) {
            return false;
        }
        TLRPC.MessageAction messageAction = message.action;
        return (messageAction instanceof TLRPC.TL_messageActionTopicCreate) || (messageAction instanceof TLRPC.TL_messageActionTopicEdit);
    }

    public static boolean isUnread(TLRPC.Message message) {
        return message.unread;
    }

    public static boolean isVideoDocument(TLRPC.Document document) {
        int lastIndexOf;
        if (document == null) {
            return false;
        }
        String str = null;
        boolean z = false;
        int i = 0;
        int i2 = 0;
        boolean z2 = false;
        for (int i3 = 0; i3 < document.attributes.size(); i3++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i3);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                if (documentAttribute.round_message) {
                    return false;
                }
                i = documentAttribute.w;
                i2 = documentAttribute.h;
                z2 = true;
            } else if (documentAttribute instanceof TLRPC.TL_documentAttributeAnimated) {
                z = true;
            } else if (documentAttribute instanceof TLRPC.TL_documentAttributeFilename) {
                str = documentAttribute.file_name;
            }
        }
        if (str != null && (lastIndexOf = str.lastIndexOf(".")) >= 0) {
            switch (str.substring(lastIndexOf + 1).toLowerCase().hashCode()) {
                case 3669:
                case 96796:
                case 98689:
                case 99351:
                case 99582:
                case 104987:
                case 3213227:
                    return false;
            }
        }
        if (z && (i > 1280 || i2 > 1280)) {
            z = false;
        }
        if (SharedConfig.streamMkv && !z2 && "video/x-matroska".equals(document.mime_type)) {
            z2 = true;
        }
        return z2 && !z;
    }

    public static boolean isVideoMessage(TLRPC.Message message) {
        if (getMedia(message) == null || !isVideoSticker(getMedia(message).document)) {
            return getMedia(message) instanceof TLRPC.TL_messageMediaWebPage ? isVideoDocument(getMedia(message).webpage.document) : getMedia(message) != null && isVideoDocument(getMedia(message).document);
        }
        return false;
    }

    public static boolean isVideoSticker(TLRPC.Document document) {
        return document != null && isVideoStickerDocument(document);
    }

    public static boolean isVideoStickerDocument(TLRPC.Document document) {
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if ((documentAttribute instanceof TLRPC.TL_documentAttributeSticker) || (documentAttribute instanceof TLRPC.TL_documentAttributeCustomEmoji)) {
                    return "video/webm".equals(document.mime_type);
                }
            }
        }
        return false;
    }

    public static boolean isVideoWebDocument(WebFile webFile) {
        return webFile != null && webFile.mime_type.startsWith("video/");
    }

    public static boolean isVoiceDocument(TLRPC.Document document) {
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                    return documentAttribute.voice;
                }
            }
        }
        return false;
    }

    public static boolean isVoiceMessage(TLRPC.Message message) {
        return getMedia(message) instanceof TLRPC.TL_messageMediaWebPage ? isVoiceDocument(getMedia(message).webpage.document) : getMedia(message) != null && isVoiceDocument(getMedia(message).document);
    }

    public static boolean isVoiceWebDocument(WebFile webFile) {
        return webFile != null && webFile.mime_type.equals("audio/ogg");
    }

    public static boolean isWebM(TLRPC.Document document) {
        return document != null && "video/webm".equals(document.mime_type);
    }

    public static int lambda$addEntitiesToText$2(TLRPC.MessageEntity messageEntity, TLRPC.MessageEntity messageEntity2) {
        int i = messageEntity.offset;
        int i2 = messageEntity2.offset;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public static int lambda$handleFoundWords$3(String str, String str2) {
        return str2.length() - str.length();
    }

    public void lambda$loadAnimatedEmojiDocument$0(TLRPC.Document document) {
        this.emojiAnimatedSticker = document;
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.animatedEmojiDocumentLoaded, this);
    }

    public void lambda$loadAnimatedEmojiDocument$1(final TLRPC.Document document) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                MessageObject.this.lambda$loadAnimatedEmojiDocument$0(document);
            }
        });
    }

    public static StaticLayout makeStaticLayout(CharSequence charSequence, TextPaint textPaint, int i, float f, float f2, boolean z) {
        StaticLayout.Builder obtain;
        StaticLayout.Builder lineSpacing;
        StaticLayout.Builder breakStrategy;
        StaticLayout.Builder hyphenationFrequency;
        StaticLayout.Builder alignment;
        StaticLayout build;
        StaticLayout.Builder obtain2;
        StaticLayout.Builder lineSpacing2;
        StaticLayout.Builder breakStrategy2;
        StaticLayout.Builder hyphenationFrequency2;
        StaticLayout.Builder alignment2;
        StaticLayout build2;
        int i2 = Build.VERSION.SDK_INT;
        if (i2 < 24) {
            return new StaticLayout(charSequence, textPaint, i, Layout.Alignment.ALIGN_NORMAL, f, f2, false);
        }
        obtain = StaticLayout.Builder.obtain(charSequence, 0, charSequence.length(), textPaint, i);
        lineSpacing = obtain.setLineSpacing(f2, f);
        breakStrategy = lineSpacing.setBreakStrategy(1);
        hyphenationFrequency = breakStrategy.setHyphenationFrequency(0);
        alignment = hyphenationFrequency.setAlignment(Layout.Alignment.ALIGN_NORMAL);
        if (z) {
            alignment.setIncludePad(false);
            if (i2 >= 28) {
                alignment.setUseLineSpacingFromFallbacks(false);
            }
        }
        build = alignment.build();
        for (int i3 = 0; i3 < build.getLineCount(); i3++) {
            if (build.getLineRight(i3) > i) {
                obtain2 = StaticLayout.Builder.obtain(charSequence, 0, charSequence.length(), textPaint, i);
                lineSpacing2 = obtain2.setLineSpacing(f2, f);
                breakStrategy2 = lineSpacing2.setBreakStrategy(0);
                hyphenationFrequency2 = breakStrategy2.setHyphenationFrequency(0);
                alignment2 = hyphenationFrequency2.setAlignment(Layout.Alignment.ALIGN_NORMAL);
                if (z) {
                    alignment2.setIncludePad(false);
                    if (Build.VERSION.SDK_INT >= 28) {
                        alignment2.setUseLineSpacingFromFallbacks(false);
                    }
                }
                build2 = alignment2.build();
                return build2;
            }
        }
        return build;
    }

    public boolean needDrawAvatarInternal() {
        TLRPC.Chat chat;
        if (this.isRepostPreview || this.isSaved || this.forceAvatar || this.customAvatarDrawable != null || this.searchType != 0) {
            return true;
        }
        boolean z = getDialogId() >= 0 ? getDialogId() == 489000 : !((chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-getDialogId()))) == null || !chat.signature_profiles);
        if (!isSponsored()) {
            if ((isFromChat() && isFromUser()) || isFromGroup() || z || this.eventId != 0) {
                return true;
            }
            TLRPC.MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
            if (messageFwdHeader != null && messageFwdHeader.saved_from_peer != null) {
                return true;
            }
        }
        return false;
    }

    public static void normalizeFlags(TLRPC.Message message) {
        TLRPC.Peer peer = message.from_id;
        if (peer == null) {
            message.flags &= -257;
        }
        if (peer == null) {
            message.flags &= -5;
        }
        if (message.reply_to == null) {
            message.flags &= -9;
        }
        if (message.media == null) {
            message.flags &= -513;
        }
        if (message.reply_markup == null) {
            message.flags &= -65;
        }
        if (message.replies == null) {
            message.flags &= -8388609;
        }
        if (message.reactions == null) {
            message.flags &= -1048577;
        }
    }

    public static CharSequence peerNameWithIcon(int i, long j) {
        return peerNameWithIcon(i, j, false);
    }

    public static CharSequence peerNameWithIcon(int i, long j, boolean z) {
        MessagesController messagesController = MessagesController.getInstance(i);
        if (j >= 0) {
            TLRPC.User user = messagesController.getUser(Long.valueOf(j));
            return user != null ? AndroidUtilities.removeDiacritics(UserObject.getUserName(user)) : "";
        }
        TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-j));
        if (chat != null) {
            return new SpannableStringBuilder(ChatObject.isChannelAndNotMegaGroup(chat) ? channelSpan() : groupSpan()).append((CharSequence) " ").append((CharSequence) AndroidUtilities.removeDiacritics(chat.title));
        }
        return "";
    }

    public static CharSequence peerNameWithIcon(int i, TLRPC.Peer peer) {
        return peerNameWithIcon(i, peer, !(peer instanceof TLRPC.TL_peerUser));
    }

    public static CharSequence peerNameWithIcon(int i, TLRPC.Peer peer, boolean z) {
        TLRPC.Chat chat;
        SpannableStringBuilder spannableStringBuilder;
        SpannableStringBuilder append;
        String str;
        if (peer instanceof TLRPC.TL_peerUser) {
            TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(peer.user_id));
            if (user == null) {
                return "";
            }
            if (!z) {
                return UserObject.getUserName(user);
            }
            append = new SpannableStringBuilder(userSpan()).append((CharSequence) " ");
            str = UserObject.getUserName(user);
        } else {
            if (peer instanceof TLRPC.TL_peerChat) {
                chat = MessagesController.getInstance(i).getChat(Long.valueOf(peer.chat_id));
                if (chat == null) {
                    return "";
                }
                if (!z) {
                    return chat.title;
                }
                spannableStringBuilder = new SpannableStringBuilder(ChatObject.isChannelAndNotMegaGroup(chat) ? channelSpan() : groupSpan());
            } else {
                if (!(peer instanceof TLRPC.TL_peerChannel) || (chat = MessagesController.getInstance(i).getChat(Long.valueOf(peer.channel_id))) == null) {
                    return "";
                }
                if (!z) {
                    return chat.title;
                }
                spannableStringBuilder = new SpannableStringBuilder(ChatObject.isChannelAndNotMegaGroup(chat) ? channelSpan() : groupSpan());
            }
            append = spannableStringBuilder.append((CharSequence) " ");
            str = chat.title;
        }
        return append.append((CharSequence) str);
    }

    public static boolean peersEqual(TLRPC.Chat chat, TLRPC.Peer peer) {
        if (chat == null && peer == null) {
            return true;
        }
        if (chat != null && peer != null) {
            return (ChatObject.isChannel(chat) && (peer instanceof TLRPC.TL_peerChannel)) ? chat.id == peer.channel_id : !ChatObject.isChannel(chat) && (peer instanceof TLRPC.TL_peerChat) && chat.id == peer.chat_id;
        }
        return false;
    }

    public static boolean peersEqual(TLRPC.InputPeer inputPeer, TLRPC.InputPeer inputPeer2) {
        if (inputPeer == null && inputPeer2 == null) {
            return true;
        }
        if (inputPeer != null && inputPeer2 != null) {
            if ((inputPeer instanceof TLRPC.TL_inputPeerChat) && (inputPeer2 instanceof TLRPC.TL_inputPeerChat)) {
                return inputPeer.chat_id == inputPeer2.chat_id;
            }
            if ((inputPeer instanceof TLRPC.TL_inputPeerChannel) && (inputPeer2 instanceof TLRPC.TL_inputPeerChannel)) {
                return inputPeer.channel_id == inputPeer2.channel_id;
            }
            if ((inputPeer instanceof TLRPC.TL_inputPeerUser) && (inputPeer2 instanceof TLRPC.TL_inputPeerUser)) {
                return inputPeer.user_id == inputPeer2.user_id;
            }
            if ((inputPeer instanceof TLRPC.TL_inputPeerSelf) && (inputPeer2 instanceof TLRPC.TL_inputPeerSelf)) {
                return true;
            }
        }
        return false;
    }

    public static boolean peersEqual(TLRPC.InputPeer inputPeer, TLRPC.Peer peer) {
        if (inputPeer == null && peer == null) {
            return true;
        }
        if (inputPeer != null && peer != null) {
            return ((inputPeer instanceof TLRPC.TL_inputPeerChat) && (peer instanceof TLRPC.TL_peerChat)) ? inputPeer.chat_id == peer.chat_id : ((inputPeer instanceof TLRPC.TL_inputPeerChannel) && (peer instanceof TLRPC.TL_peerChannel)) ? inputPeer.channel_id == peer.channel_id : (inputPeer instanceof TLRPC.TL_inputPeerUser) && (peer instanceof TLRPC.TL_peerUser) && inputPeer.user_id == peer.user_id;
        }
        return false;
    }

    public static boolean peersEqual(TLRPC.Peer peer, TLRPC.Peer peer2) {
        if (peer == null && peer2 == null) {
            return true;
        }
        if (peer != null && peer2 != null) {
            return ((peer instanceof TLRPC.TL_peerChat) && (peer2 instanceof TLRPC.TL_peerChat)) ? peer.chat_id == peer2.chat_id : ((peer instanceof TLRPC.TL_peerChannel) && (peer2 instanceof TLRPC.TL_peerChannel)) ? peer.channel_id == peer2.channel_id : (peer instanceof TLRPC.TL_peerUser) && (peer2 instanceof TLRPC.TL_peerUser) && peer.user_id == peer2.user_id;
        }
        return false;
    }

    public static Spannable replaceAnimatedEmoji(CharSequence charSequence, ArrayList<TLRPC.MessageEntity> arrayList, Paint.FontMetricsInt fontMetricsInt) {
        return replaceAnimatedEmoji(charSequence, arrayList, fontMetricsInt, false);
    }

    public static Spannable replaceAnimatedEmoji(CharSequence charSequence, ArrayList<TLRPC.MessageEntity> arrayList, Paint.FontMetricsInt fontMetricsInt, boolean z) {
        return replaceAnimatedEmoji(charSequence, arrayList, fontMetricsInt, z, 1.2f);
    }

    public static Spannable replaceAnimatedEmoji(CharSequence charSequence, ArrayList<TLRPC.MessageEntity> arrayList, Paint.FontMetricsInt fontMetricsInt, boolean z, float f) {
        if (charSequence == null) {
            return null;
        }
        Spannable spannableString = charSequence instanceof Spannable ? (Spannable) charSequence : new SpannableString(charSequence);
        if (arrayList == null) {
            return spannableString;
        }
        Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) spannableString.getSpans(0, spannableString.length(), Emoji.EmojiSpan.class);
        for (int i = 0; i < arrayList.size(); i++) {
            TLRPC.MessageEntity messageEntity = arrayList.get(i);
            if (messageEntity instanceof TLRPC.TL_messageEntityCustomEmoji) {
                TLRPC.TL_messageEntityCustomEmoji tL_messageEntityCustomEmoji = (TLRPC.TL_messageEntityCustomEmoji) messageEntity;
                for (int i2 = 0; i2 < emojiSpanArr.length; i2++) {
                    Emoji.EmojiSpan emojiSpan = emojiSpanArr[i2];
                    if (emojiSpan != null) {
                        int spanStart = spannableString.getSpanStart(emojiSpan);
                        int spanEnd = spannableString.getSpanEnd(emojiSpan);
                        int i3 = tL_messageEntityCustomEmoji.offset;
                        if (AndroidUtilities.intersect1d(i3, tL_messageEntityCustomEmoji.length + i3, spanStart, spanEnd)) {
                            spannableString.removeSpan(emojiSpan);
                            emojiSpanArr[i2] = null;
                        }
                    }
                }
                if (messageEntity.offset + messageEntity.length <= spannableString.length()) {
                    int i4 = messageEntity.offset;
                    AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spannableString.getSpans(i4, messageEntity.length + i4, AnimatedEmojiSpan.class);
                    if (animatedEmojiSpanArr != null && animatedEmojiSpanArr.length > 0) {
                        for (AnimatedEmojiSpan animatedEmojiSpan : animatedEmojiSpanArr) {
                            spannableString.removeSpan(animatedEmojiSpan);
                        }
                    }
                    AnimatedEmojiSpan animatedEmojiSpan2 = tL_messageEntityCustomEmoji.document != null ? new AnimatedEmojiSpan(tL_messageEntityCustomEmoji.document, f, fontMetricsInt) : new AnimatedEmojiSpan(tL_messageEntityCustomEmoji.document_id, f, fontMetricsInt);
                    animatedEmojiSpan2.top = z;
                    int i5 = messageEntity.offset;
                    spannableString.setSpan(animatedEmojiSpan2, i5, messageEntity.length + i5, 33);
                }
            }
        }
        return spannableString;
    }

    public static CharSequence replaceWithLink(CharSequence charSequence, String str, TLObject tLObject) {
        String str2;
        CharSequence charSequence2;
        String str3;
        StringBuilder sb;
        long j;
        TLObject tLObject2;
        int indexOf = TextUtils.indexOf(charSequence, str);
        if (indexOf < 0) {
            return charSequence;
        }
        if (tLObject instanceof TLRPC.User) {
            TLRPC.User user = (TLRPC.User) tLObject;
            charSequence2 = UserObject.getUserName(user).replace('\n', ' ');
            sb = new StringBuilder();
            sb.append("");
            j = user.id;
        } else {
            if (!(tLObject instanceof TLRPC.Chat)) {
                if (!(tLObject instanceof TLRPC.TL_game)) {
                    if (tLObject instanceof TLRPC.TL_chatInviteExported) {
                        TLRPC.TL_chatInviteExported tL_chatInviteExported = (TLRPC.TL_chatInviteExported) tLObject;
                        charSequence2 = tL_chatInviteExported.link.replace('\n', ' ');
                        str3 = "invite";
                        tLObject2 = tL_chatInviteExported;
                    } else if (tLObject instanceof TLRPC.ForumTopic) {
                        charSequence2 = ForumUtilities.getTopicSpannedName((TLRPC.ForumTopic) tLObject, null, false);
                        str3 = "topic";
                        tLObject2 = tLObject;
                    } else {
                        str2 = "0";
                        charSequence2 = "";
                    }
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(TextUtils.replace(charSequence, new String[]{str}, new CharSequence[]{charSequence2}));
                    URLSpanNoUnderlineBold uRLSpanNoUnderlineBold = new URLSpanNoUnderlineBold("" + str3);
                    uRLSpanNoUnderlineBold.setObject(tLObject2);
                    spannableStringBuilder.setSpan(uRLSpanNoUnderlineBold, indexOf, charSequence2.length() + indexOf, 33);
                    return spannableStringBuilder;
                }
                charSequence2 = ((TLRPC.TL_game) tLObject).title.replace('\n', ' ');
                str2 = "game";
                str3 = str2;
                tLObject2 = null;
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(TextUtils.replace(charSequence, new String[]{str}, new CharSequence[]{charSequence2}));
                URLSpanNoUnderlineBold uRLSpanNoUnderlineBold2 = new URLSpanNoUnderlineBold("" + str3);
                uRLSpanNoUnderlineBold2.setObject(tLObject2);
                spannableStringBuilder2.setSpan(uRLSpanNoUnderlineBold2, indexOf, charSequence2.length() + indexOf, 33);
                return spannableStringBuilder2;
            }
            TLRPC.Chat chat = (TLRPC.Chat) tLObject;
            charSequence2 = chat.title.replace('\n', ' ');
            sb = new StringBuilder();
            sb.append("");
            j = -chat.id;
        }
        sb.append(j);
        str2 = sb.toString();
        str3 = str2;
        tLObject2 = null;
        SpannableStringBuilder spannableStringBuilder22 = new SpannableStringBuilder(TextUtils.replace(charSequence, new String[]{str}, new CharSequence[]{charSequence2}));
        URLSpanNoUnderlineBold uRLSpanNoUnderlineBold22 = new URLSpanNoUnderlineBold("" + str3);
        uRLSpanNoUnderlineBold22.setObject(tLObject2);
        spannableStringBuilder22.setSpan(uRLSpanNoUnderlineBold22, indexOf, charSequence2.length() + indexOf, 33);
        return spannableStringBuilder22;
    }

    public static void setUnreadFlags(TLRPC.Message message, int i) {
        message.unread = (i & 1) == 0;
        message.media_unread = (i & 2) == 0;
    }

    public static boolean shouldEncryptPhotoOrVideo(int i, TLRPC.Message message) {
        int i2;
        if ((message == null || message.media == null || !((isVoiceDocument(getDocument(message)) || isRoundVideoMessage(message)) && message.media.ttl_seconds == Integer.MAX_VALUE)) && !(getMedia(message) instanceof TLRPC.TL_messageMediaPaidMedia)) {
            return message instanceof TLRPC.TL_message_secret ? ((getMedia(message) instanceof TLRPC.TL_messageMediaPhoto) || isVideoMessage(message)) && (i2 = message.ttl) > 0 && i2 <= 60 : ((getMedia(message) instanceof TLRPC.TL_messageMediaPhoto) || (getMedia(message) instanceof TLRPC.TL_messageMediaDocument)) && getMedia(message).ttl_seconds != 0;
        }
        return true;
    }

    private void updateMessageText(java.util.AbstractMap<java.lang.Long, org.telegram.tgnet.TLRPC.User> r31, java.util.AbstractMap<java.lang.Long, org.telegram.tgnet.TLRPC.Chat> r32, androidx.collection.LongSparseArray r33, androidx.collection.LongSparseArray r34) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.updateMessageText(java.util.AbstractMap, java.util.AbstractMap, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray):void");
    }

    private static void updatePhotoSizeLocations(ArrayList<TLRPC.PhotoSize> arrayList, List<TLRPC.PhotoSize> list) {
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            TLRPC.PhotoSize photoSize = arrayList.get(i);
            if (photoSize != null) {
                int size2 = list.size();
                int i2 = 0;
                while (true) {
                    if (i2 < size2) {
                        TLRPC.PhotoSize photoSize2 = list.get(i2);
                        if (!(photoSize2 instanceof TLRPC.TL_photoSizeEmpty) && !(photoSize2 instanceof TLRPC.TL_photoCachedSize) && photoSize2 != null && photoSize2.type.equals(photoSize.type)) {
                            photoSize.location = photoSize2.location;
                            break;
                        }
                        i2++;
                    }
                }
            }
        }
    }

    public static void updatePollResults(TLRPC.TL_messageMediaPoll tL_messageMediaPoll, TLRPC.PollResults pollResults) {
        ArrayList arrayList;
        byte[] bArr;
        ArrayList<TLRPC.TL_pollAnswerVoters> arrayList2;
        if (tL_messageMediaPoll == null || pollResults == null) {
            return;
        }
        if ((pollResults.flags & 2) != 0) {
            if (!pollResults.min || (arrayList2 = tL_messageMediaPoll.results.results) == null) {
                arrayList = null;
                bArr = null;
            } else {
                int size = arrayList2.size();
                arrayList = null;
                bArr = null;
                for (int i = 0; i < size; i++) {
                    TLRPC.TL_pollAnswerVoters tL_pollAnswerVoters = tL_messageMediaPoll.results.results.get(i);
                    if (tL_pollAnswerVoters.chosen) {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        arrayList.add(tL_pollAnswerVoters.option);
                    }
                    if (tL_pollAnswerVoters.correct) {
                        bArr = tL_pollAnswerVoters.option;
                    }
                }
            }
            TLRPC.PollResults pollResults2 = tL_messageMediaPoll.results;
            ArrayList<TLRPC.TL_pollAnswerVoters> arrayList3 = pollResults.results;
            pollResults2.results = arrayList3;
            if (arrayList != null || bArr != null) {
                int size2 = arrayList3.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    TLRPC.TL_pollAnswerVoters tL_pollAnswerVoters2 = tL_messageMediaPoll.results.results.get(i2);
                    if (arrayList != null) {
                        int size3 = arrayList.size();
                        int i3 = 0;
                        while (true) {
                            if (i3 >= size3) {
                                break;
                            }
                            if (Arrays.equals(tL_pollAnswerVoters2.option, (byte[]) arrayList.get(i3))) {
                                tL_pollAnswerVoters2.chosen = true;
                                arrayList.remove(i3);
                                break;
                            }
                            i3++;
                        }
                        if (arrayList.isEmpty()) {
                            arrayList = null;
                        }
                    }
                    if (bArr != null && Arrays.equals(tL_pollAnswerVoters2.option, bArr)) {
                        tL_pollAnswerVoters2.correct = true;
                        bArr = null;
                    }
                    if (arrayList == null && bArr == null) {
                        break;
                    }
                }
            }
            tL_messageMediaPoll.results.flags |= 2;
        }
        if ((pollResults.flags & 4) != 0) {
            TLRPC.PollResults pollResults3 = tL_messageMediaPoll.results;
            pollResults3.total_voters = pollResults.total_voters;
            pollResults3.flags |= 4;
        }
        if ((pollResults.flags & 8) != 0) {
            TLRPC.PollResults pollResults4 = tL_messageMediaPoll.results;
            pollResults4.recent_voters = pollResults.recent_voters;
            pollResults4.flags |= 8;
        }
        if ((pollResults.flags & 16) != 0) {
            TLRPC.PollResults pollResults5 = tL_messageMediaPoll.results;
            pollResults5.solution = pollResults.solution;
            pollResults5.solution_entities = pollResults.solution_entities;
            pollResults5.flags |= 16;
        }
    }

    public static void updateReactions(TLRPC.Message message, TLRPC.TL_messageReactions tL_messageReactions) {
        if (message == null || tL_messageReactions == null) {
            return;
        }
        TLRPC.TL_messageReactions tL_messageReactions2 = message.reactions;
        if (tL_messageReactions2 != null) {
            int size = tL_messageReactions2.results.size();
            boolean z = false;
            for (int i = 0; i < size; i++) {
                TLRPC.ReactionCount reactionCount = message.reactions.results.get(i);
                int size2 = tL_messageReactions.results.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    TLRPC.ReactionCount reactionCount2 = tL_messageReactions.results.get(i2);
                    if (ReactionsLayoutInBubble.equalsTLReaction(reactionCount.reaction, reactionCount2.reaction)) {
                        if (!z && tL_messageReactions.min && reactionCount.chosen) {
                            reactionCount2.chosen = true;
                            z = true;
                        }
                        reactionCount2.lastDrawnPosition = reactionCount.lastDrawnPosition;
                    }
                }
                if (reactionCount.chosen) {
                    z = true;
                }
            }
        }
        message.reactions = tL_messageReactions;
        message.flags |= 1048576;
    }

    public static CharSequence userSpan() {
        return userSpan(0);
    }

    public static CharSequence userSpan(int i) {
        if (userSpan == null) {
            userSpan = new CharSequence[2];
        }
        CharSequence[] charSequenceArr = userSpan;
        if (charSequenceArr[i] == null) {
            charSequenceArr[i] = new SpannableStringBuilder("u");
            ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.mini_reply_user);
            coloredImageSpan.spaceScaleX = 0.9f;
            if (i == 0) {
                coloredImageSpan.translate(0.0f, AndroidUtilities.dp(1.0f));
            }
            ((SpannableStringBuilder) userSpan[i]).setSpan(coloredImageSpan, 0, 1, 33);
        }
        return userSpan[i];
    }

    public boolean addEntitiesToText(CharSequence charSequence, boolean z, boolean z2) {
        ArrayList<TLRPC.MessageEntity> arrayList;
        if (charSequence == null) {
            return false;
        }
        if (!this.isRestrictedMessage && !(getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaUnsupported)) {
            if (this.translated) {
                TLRPC.TL_textWithEntities tL_textWithEntities = this.messageOwner.translatedText;
                arrayList = tL_textWithEntities == null ? null : tL_textWithEntities.entities;
            } else {
                arrayList = this.messageOwner.entities;
            }
            return addEntitiesToText(charSequence, arrayList, isOutOwner(), true, z, z2);
        }
        ArrayList arrayList2 = new ArrayList();
        TLRPC.TL_messageEntityItalic tL_messageEntityItalic = new TLRPC.TL_messageEntityItalic();
        tL_messageEntityItalic.offset = 0;
        tL_messageEntityItalic.length = charSequence.length();
        arrayList2.add(tL_messageEntityItalic);
        return addEntitiesToText(charSequence, arrayList2, isOutOwner(), true, z, z2);
    }

    public void addPaidReactions(int i, boolean z, boolean z2) {
        TLRPC.Message message = this.messageOwner;
        if (message.reactions == null) {
            message.reactions = new TLRPC.TL_messageReactions();
            TLRPC.Message message2 = this.messageOwner;
            message2.reactions.reactions_as_tags = getDialogId(message2) == UserConfig.getInstance(this.currentAccount).getClientUserId();
            this.messageOwner.reactions.can_see_list = isFromGroup() || isFromUser();
        }
        addPaidReactions(this.currentAccount, this.messageOwner.reactions, i, z2, z);
    }

    public void applyMediaExistanceFlags(int i) {
        if (i == -1) {
            checkMediaExistance();
        } else {
            this.attachPathExists = (i & 1) != 0;
            this.mediaExists = (i & 2) != 0;
        }
    }

    public void applyNewText() {
        this.translated = false;
        applyNewText(this.messageOwner.message);
    }

    public void applyNewText(CharSequence charSequence) {
        TLRPC.TL_textWithEntities tL_textWithEntities;
        if (TextUtils.isEmpty(charSequence)) {
            return;
        }
        TLRPC.User user = isFromUser() ? MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id)) : null;
        this.messageText = charSequence;
        ArrayList<TLRPC.MessageEntity> arrayList = (!this.translated || (tL_textWithEntities = this.messageOwner.translatedText) == null) ? this.messageOwner.entities : tL_textWithEntities.entities;
        TextPaint textPaint = getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaGame ? Theme.chat_msgGameTextPaint : Theme.chat_msgTextPaint;
        int[] iArr = allowsBigEmoji() ? new int[1] : null;
        CharSequence replaceEmoji = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), false, iArr);
        this.messageText = replaceEmoji;
        Spannable replaceAnimatedEmoji = replaceAnimatedEmoji(replaceEmoji, arrayList, textPaint.getFontMetricsInt());
        this.messageText = replaceAnimatedEmoji;
        if (iArr != null && iArr[0] > 1) {
            replaceEmojiToLottieFrame(replaceAnimatedEmoji, iArr);
        }
        checkEmojiOnly(iArr);
        generateLayout(user);
        setType();
    }

    public void applyQuickReply(String str, int i) {
        TLRPC.Message message = this.messageOwner;
        if (message == null) {
            return;
        }
        if (i != 0) {
            message.flags |= 1073741824;
            message.quick_reply_shortcut_id = i;
            TLRPC.TL_inputQuickReplyShortcutId tL_inputQuickReplyShortcutId = new TLRPC.TL_inputQuickReplyShortcutId();
            tL_inputQuickReplyShortcutId.shortcut_id = i;
            this.messageOwner.quick_reply_shortcut = tL_inputQuickReplyShortcutId;
            return;
        }
        if (str != null) {
            TLRPC.TL_inputQuickReplyShortcut tL_inputQuickReplyShortcut = new TLRPC.TL_inputQuickReplyShortcut();
            tL_inputQuickReplyShortcut.shortcut = str;
            this.messageOwner.quick_reply_shortcut = tL_inputQuickReplyShortcut;
        } else {
            message.flags &= -1073741825;
            message.quick_reply_shortcut_id = 0;
            message.quick_reply_shortcut = null;
        }
    }

    public void applyTimestampsHighlightForReplyMsg() {
        boolean isOutOwner;
        CharSequence charSequence;
        int duration;
        int i;
        boolean z;
        boolean z2;
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject == null) {
            return;
        }
        if (messageObject.isYouTubeVideo()) {
            isOutOwner = isOutOwner();
            charSequence = this.messageText;
            duration = Integer.MAX_VALUE;
            z = false;
            z2 = false;
            i = 3;
        } else {
            if (!messageObject.isVideo()) {
                if (messageObject.isMusic() || messageObject.isVoice()) {
                    addUrlsByPattern(isOutOwner(), this.messageText, false, 4, (int) messageObject.getDuration(), false);
                    return;
                }
                return;
            }
            isOutOwner = isOutOwner();
            charSequence = this.messageText;
            duration = (int) messageObject.getDuration();
            i = 3;
            z = false;
            z2 = false;
        }
        addUrlsByPattern(isOutOwner, charSequence, z2, i, duration, z);
    }

    public boolean areTags() {
        TLRPC.TL_messageReactions tL_messageReactions;
        TLRPC.Message message = this.messageOwner;
        if (message == null || (tL_messageReactions = message.reactions) == null) {
            return false;
        }
        return tL_messageReactions.reactions_as_tags;
    }

    public boolean canBeSensitive() {
        int i;
        return (this.messageOwner == null || ((i = this.type) != 1 && i != 3 && i != 9 && i != 8 && i != 5) || this.sendPreview || this.isRepostPreview || isOutOwner() || this.messageOwner.send_state != 0) ? false : true;
    }

    public boolean canDeleteMessage(boolean z, TLRPC.Chat chat) {
        TLRPC.Message message;
        return (isStory() && (message = this.messageOwner) != null && message.dialog_id == UserConfig.getInstance(this.currentAccount).getClientUserId()) || (this.eventId == 0 && this.sponsoredId == null && canDeleteMessage(this.currentAccount, z, this.messageOwner, chat));
    }

    public boolean canEditMedia() {
        if (isSecretMedia()) {
            return false;
        }
        if (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaPhoto) {
            return true;
        }
        return getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaDocument ? (isVoice() || isSticker() || isAnimatedSticker() || isRoundVideo()) ? false : true : isMediaEmpty();
    }

    public boolean canEditMessage(TLRPC.Chat chat) {
        return canEditMessage(this.currentAccount, this.messageOwner, chat, this.scheduled);
    }

    public boolean canEditMessageAnytime(TLRPC.Chat chat) {
        return canEditMessageAnytime(this.currentAccount, this.messageOwner, chat);
    }

    public boolean canEditMessageScheduleTime(TLRPC.Chat chat) {
        return canEditMessageScheduleTime(this.currentAccount, this.messageOwner, chat);
    }

    public boolean canForwardMessage() {
        return (isQuickReply() || this.type == 30 || (this.messageOwner instanceof TLRPC.TL_message_secret) || needDrawBluredPreview() || isLiveLocation() || this.type == 16 || isSponsored() || this.messageOwner.noforwards) ? false : true;
    }

    public boolean canPreviewDocument() {
        return canPreviewDocument(getDocument());
    }

    public boolean canSetReaction() {
        TLRPC.Message message = this.messageOwner;
        if (message instanceof TLRPC.TL_messageService) {
            return message.reactions_are_possible;
        }
        return true;
    }

    public boolean canStreamVideo() {
        if (hasVideoQualities()) {
            return true;
        }
        TLRPC.Document document = getDocument();
        if (document != null && !(document instanceof TLRPC.TL_documentEncrypted)) {
            if (SharedConfig.streamAllVideo) {
                return true;
            }
            for (int i = 0; i < document.attributes.size(); i++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                    return documentAttribute.supports_streaming;
                }
            }
            if (SharedConfig.streamMkv && "video/x-matroska".equals(document.mime_type)) {
                return true;
            }
        }
        return false;
    }

    public boolean canUnvote() {
        TLRPC.TL_messageMediaPoll tL_messageMediaPoll;
        TLRPC.PollResults pollResults;
        if (this.type == 17 && (pollResults = (tL_messageMediaPoll = (TLRPC.TL_messageMediaPoll) getMedia(this.messageOwner)).results) != null && !pollResults.results.isEmpty() && !tL_messageMediaPoll.poll.quiz) {
            int size = tL_messageMediaPoll.results.results.size();
            for (int i = 0; i < size; i++) {
                if (tL_messageMediaPoll.results.results.get(i).chosen) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canViewThread() {
        MessageObject messageObject;
        if (this.messageOwner.action != null) {
            return false;
        }
        return hasReplies() || !(((messageObject = this.replyMessageObject) == null || messageObject.messageOwner.replies == null) && getReplyTopMsgId() == 0);
    }

    protected void checkBigAnimatedEmoji() {
        AnimatedEmojiSpan[] animatedEmojiSpanArr;
        String str;
        int i;
        this.emojiAnimatedSticker = null;
        this.emojiAnimatedStickerId = null;
        if (this.emojiOnlyCount == 1 && !(getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaWebPage) && !(getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaInvoice) && ((getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaEmpty) || getMedia(this.messageOwner) == null)) {
            TLRPC.Message message = this.messageOwner;
            if (message.grouped_id == 0) {
                if (message.entities.isEmpty()) {
                    CharSequence charSequence = this.messageText;
                    int indexOf = TextUtils.indexOf(charSequence, "🏻");
                    if (indexOf >= 0) {
                        str = "_c1";
                    } else {
                        indexOf = TextUtils.indexOf(charSequence, "🏼");
                        if (indexOf >= 0) {
                            str = "_c2";
                        } else {
                            indexOf = TextUtils.indexOf(charSequence, "🏽");
                            if (indexOf >= 0) {
                                str = "_c3";
                            } else {
                                indexOf = TextUtils.indexOf(charSequence, "🏾");
                                if (indexOf >= 0) {
                                    str = "_c4";
                                } else {
                                    indexOf = TextUtils.indexOf(charSequence, "🏿");
                                    if (indexOf >= 0) {
                                        str = "_c5";
                                    } else {
                                        this.emojiAnimatedStickerColor = "";
                                        if (!TextUtils.isEmpty(this.emojiAnimatedStickerColor) && (i = indexOf + 2) < this.messageText.length()) {
                                            StringBuilder sb = new StringBuilder();
                                            sb.append(charSequence.toString());
                                            CharSequence charSequence2 = this.messageText;
                                            sb.append(charSequence2.subSequence(i, charSequence2.length()).toString());
                                            charSequence = sb.toString();
                                        }
                                        if (!TextUtils.isEmpty(this.emojiAnimatedStickerColor) || EmojiData.emojiColoredMap.contains(charSequence.toString())) {
                                            this.emojiAnimatedSticker = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(charSequence);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    this.emojiAnimatedStickerColor = str;
                    charSequence = charSequence.subSequence(0, indexOf);
                    if (!TextUtils.isEmpty(this.emojiAnimatedStickerColor)) {
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(charSequence.toString());
                        CharSequence charSequence22 = this.messageText;
                        sb2.append(charSequence22.subSequence(i, charSequence22.length()).toString());
                        charSequence = sb2.toString();
                    }
                    if (!TextUtils.isEmpty(this.emojiAnimatedStickerColor)) {
                    }
                    this.emojiAnimatedSticker = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(charSequence);
                } else if (this.messageOwner.entities.size() == 1 && (this.messageOwner.entities.get(0) instanceof TLRPC.TL_messageEntityCustomEmoji)) {
                    try {
                        long j = ((TLRPC.TL_messageEntityCustomEmoji) this.messageOwner.entities.get(0)).document_id;
                        this.emojiAnimatedStickerId = Long.valueOf(j);
                        TLRPC.Document findDocument = AnimatedEmojiDrawable.findDocument(this.currentAccount, j);
                        this.emojiAnimatedSticker = findDocument;
                        if (findDocument == null) {
                            CharSequence charSequence3 = this.messageText;
                            if ((charSequence3 instanceof Spanned) && (animatedEmojiSpanArr = (AnimatedEmojiSpan[]) ((Spanned) charSequence3).getSpans(0, charSequence3.length(), AnimatedEmojiSpan.class)) != null && animatedEmojiSpanArr.length == 1) {
                                this.emojiAnimatedSticker = animatedEmojiSpanArr[0].document;
                            }
                        }
                    } catch (Exception unused) {
                    }
                }
            }
        }
        if (this.emojiAnimatedSticker == null && this.emojiAnimatedStickerId == null) {
            generateLayout(null);
        } else {
            this.type = isSticker() ? 13 : isAnimatedSticker() ? 15 : 1000;
        }
    }

    public void checkForScam() {
    }

    public boolean checkLayout() {
        CharSequence charSequence;
        int i = this.type;
        if ((i == 0 || i == 19) && this.messageOwner.peer_id != null && (charSequence = this.messageText) != null && charSequence.length() != 0) {
            if (this.layoutCreated) {
                if (Math.abs(this.generatedWithMinSize - (AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x)) > AndroidUtilities.dp(52.0f) || this.generatedWithDensity != AndroidUtilities.density) {
                    this.layoutCreated = false;
                }
            }
            if (!this.layoutCreated) {
                this.layoutCreated = true;
                if (isFromUser()) {
                    MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id));
                }
                TextPaint textPaint = getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaGame ? Theme.chat_msgGameTextPaint : Theme.chat_msgTextPaint;
                int[] iArr = allowsBigEmoji() ? new int[1] : null;
                CharSequence replaceEmoji = Emoji.replaceEmoji(this.messageText, textPaint.getFontMetricsInt(), false, iArr);
                this.messageText = replaceEmoji;
                Spannable replaceAnimatedEmoji = replaceAnimatedEmoji(replaceEmoji, textPaint.getFontMetricsInt());
                this.messageText = replaceAnimatedEmoji;
                if (iArr != null && iArr[0] > 1) {
                    replaceEmojiToLottieFrame(replaceAnimatedEmoji, iArr);
                }
                checkEmojiOnly(iArr);
                checkBigAnimatedEmoji();
                setType();
                return true;
            }
        }
        return false;
    }

    public void checkMediaExistance() {
        checkMediaExistance(true);
    }

    public void checkMediaExistance(boolean z) {
        boolean exists;
        int i;
        File pathToAttach;
        this.attachPathExists = false;
        this.mediaExists = false;
        int i2 = this.type;
        if (i2 == 20) {
            TLRPC.TL_messageExtendedMediaPreview tL_messageExtendedMediaPreview = (TLRPC.TL_messageExtendedMediaPreview) this.messageOwner.media.extended_media.get(0);
            if (tL_messageExtendedMediaPreview.thumb != null) {
                File pathToAttach2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(tL_messageExtendedMediaPreview.thumb, z);
                if (!this.mediaExists) {
                    exists = pathToAttach2.exists() || (tL_messageExtendedMediaPreview.thumb instanceof TLRPC.TL_photoStrippedSize);
                    this.mediaExists = exists;
                }
            }
        } else if (i2 == 1 && FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize()) != null) {
            File pathToMessage = FileLoader.getInstance(this.currentAccount).getPathToMessage(this.messageOwner, z);
            if (needDrawBluredPreview()) {
                this.mediaExists = new File(pathToMessage.getAbsolutePath() + ".enc").exists();
            }
            if (!this.mediaExists) {
                exists = pathToMessage.exists();
                this.mediaExists = exists;
            }
        }
        if ((!this.mediaExists && this.type == 8) || (i = this.type) == 3 || i == 9 || i == 2 || i == 14 || i == 5) {
            String str = this.messageOwner.attachPath;
            if (str != null && str.length() > 0) {
                this.attachPathExists = new File(this.messageOwner.attachPath).exists();
            }
            if (!this.attachPathExists) {
                File pathToMessage2 = FileLoader.getInstance(this.currentAccount).getPathToMessage(this.messageOwner, z);
                if ((this.type == 3 && needDrawBluredPreview()) || isVoiceOnce() || isRoundOnce()) {
                    this.mediaExists = new File(pathToMessage2.getAbsolutePath() + ".enc").exists();
                }
                if (!this.mediaExists) {
                    this.mediaExists = pathToMessage2.exists();
                }
            }
        }
        if (!this.mediaExists) {
            TLObject document = getDocument();
            if (document != null) {
                if (!isWallpaper()) {
                    pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(document, null, false, z);
                    this.mediaExists = pathToAttach.exists();
                }
                pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(document, null, true, z);
                this.mediaExists = pathToAttach.exists();
            } else {
                int i3 = this.type;
                if (i3 == 0) {
                    document = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
                    if (document == null) {
                        return;
                    }
                    pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(document, null, true, z);
                    this.mediaExists = pathToAttach.exists();
                } else if (i3 == 11) {
                    TLRPC.Photo photo = this.messageOwner.action.photo;
                    if (photo == null || photo.video_sizes.isEmpty()) {
                        return;
                    }
                    pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(photo.video_sizes.get(0), null, true, z);
                    this.mediaExists = pathToAttach.exists();
                }
            }
        }
        updateQualitiesCached(z);
    }

    public void copyStableParams(MessageObject messageObject) {
        ArrayList<TextLayoutBlock> arrayList;
        TLRPC.MessageMedia messageMedia;
        ArrayList<TLRPC.ReactionCount> arrayList2;
        TLRPC.TL_messageReactions tL_messageReactions;
        this.stableId = messageObject.stableId;
        TLRPC.Message message = this.messageOwner;
        message.premiumEffectWasPlayed = messageObject.messageOwner.premiumEffectWasPlayed;
        this.forcePlayEffect = messageObject.forcePlayEffect;
        this.wasJustSent = messageObject.wasJustSent;
        TLRPC.TL_messageReactions tL_messageReactions2 = message.reactions;
        if (tL_messageReactions2 != null && (arrayList2 = tL_messageReactions2.results) != null && !arrayList2.isEmpty() && (tL_messageReactions = messageObject.messageOwner.reactions) != null && tL_messageReactions.results != null) {
            for (int i = 0; i < this.messageOwner.reactions.results.size(); i++) {
                TLRPC.ReactionCount reactionCount = this.messageOwner.reactions.results.get(i);
                for (int i2 = 0; i2 < messageObject.messageOwner.reactions.results.size(); i2++) {
                    TLRPC.ReactionCount reactionCount2 = messageObject.messageOwner.reactions.results.get(i2);
                    if (ReactionsLayoutInBubble.equalsTLReaction(reactionCount.reaction, reactionCount2.reaction)) {
                        reactionCount.lastDrawnPosition = reactionCount2.lastDrawnPosition;
                    }
                }
            }
        }
        boolean z = messageObject.isSpoilersRevealed;
        this.isSpoilersRevealed = z;
        TLRPC.Message message2 = this.messageOwner;
        TLRPC.Message message3 = messageObject.messageOwner;
        message2.replyStory = message3.replyStory;
        TLRPC.MessageMedia messageMedia2 = message2.media;
        if (messageMedia2 != null && (messageMedia = message3.media) != null) {
            messageMedia2.storyItem = messageMedia.storyItem;
        }
        if (!z || (arrayList = this.textLayoutBlocks) == null) {
            return;
        }
        Iterator<TextLayoutBlock> it = arrayList.iterator();
        while (it.hasNext()) {
            it.next().spoilers.clear();
        }
    }

    public void createMediaThumbs() {
        TLRPC.PhotoSize closestPhotoSizeWithSize;
        TLRPC.PhotoSize closestPhotoSizeWithSize2;
        ImageLocation forDocument;
        TLRPC.MessageMedia messageMedia;
        if (isStoryMedia()) {
            TL_stories.StoryItem storyItem = getMedia(this.messageOwner).storyItem;
            if (storyItem == null || (messageMedia = storyItem.media) == null) {
                return;
            }
            TLRPC.Document document = messageMedia.document;
            if (document != null) {
                TLRPC.PhotoSize closestPhotoSizeWithSize3 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 50);
                this.mediaThumb = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320, false, null, true), document);
                forDocument = ImageLocation.getForDocument(closestPhotoSizeWithSize3, document);
            } else {
                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, 50);
                closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, 320, false, closestPhotoSizeWithSize, true);
                this.mediaThumb = ImageLocation.getForObject(closestPhotoSizeWithSize2, this.photoThumbsObject);
                forDocument = ImageLocation.getForObject(closestPhotoSizeWithSize, this.photoThumbsObject);
            }
        } else if (isVideo()) {
            TLRPC.Document document2 = getDocument();
            TLRPC.PhotoSize closestPhotoSizeWithSize4 = FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 50);
            this.mediaThumb = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 320), document2);
            forDocument = ImageLocation.getForDocument(closestPhotoSizeWithSize4, document2);
        } else {
            if (!(getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaPhoto) || getMedia(this.messageOwner).photo == null || this.photoThumbs.isEmpty()) {
                return;
            }
            closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, 50);
            closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, 320, false, closestPhotoSizeWithSize, false);
            this.mediaThumb = ImageLocation.getForObject(closestPhotoSizeWithSize2, this.photoThumbsObject);
            forDocument = ImageLocation.getForObject(closestPhotoSizeWithSize, this.photoThumbsObject);
        }
        this.mediaSmallThumb = forDocument;
    }

    public void createMessageSendInfo() {
        HashMap<String, String> hashMap;
        String str;
        VideoEditedInfo videoEditedInfo = this.videoEditedInfo;
        boolean z = videoEditedInfo != null && videoEditedInfo.notReadyYet;
        TLRPC.Message message = this.messageOwner;
        if (message.message != null) {
            if ((message.id < 0 || isEditing()) && (hashMap = this.messageOwner.params) != null) {
                String str2 = hashMap.get("ve");
                if (str2 != null && (isVideo() || isNewGif() || isRoundVideo() || isVideoSticker() || isPaidVideo(getMedia(this)))) {
                    VideoEditedInfo videoEditedInfo2 = new VideoEditedInfo();
                    this.videoEditedInfo = videoEditedInfo2;
                    if (videoEditedInfo2.parseString(str2)) {
                        this.videoEditedInfo.roundVideo = isRoundVideo();
                        this.videoEditedInfo.notReadyYet = z;
                    } else {
                        this.videoEditedInfo = null;
                    }
                }
                TLRPC.Message message2 = this.messageOwner;
                if (message2.send_state != 3 || (str = message2.params.get("prevMedia")) == null) {
                    return;
                }
                SerializedData serializedData = new SerializedData(Base64.decode(str, 0));
                this.previousMedia = TLRPC.MessageMedia.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                this.previousMessage = serializedData.readString(false);
                this.previousAttachPath = serializedData.readString(false);
                int readInt32 = serializedData.readInt32(false);
                this.previousMessageEntities = new ArrayList<>(readInt32);
                for (int i = 0; i < readInt32; i++) {
                    this.previousMessageEntities.add(TLRPC.MessageEntity.TLdeserialize(serializedData, serializedData.readInt32(false), false));
                }
                serializedData.cleanup();
            }
        }
    }

    public void createStrippedThumb() {
        if (this.photoThumbs != null) {
            if ((canCreateStripedThubms() || hasExtendedMediaPreview()) && this.strippedThumb == null) {
                try {
                    String str = isRoundVideo() ? "br" : "b";
                    int size = this.photoThumbs.size();
                    for (int i = 0; i < size; i++) {
                        TLRPC.PhotoSize photoSize = this.photoThumbs.get(i);
                        if (photoSize instanceof TLRPC.TL_photoStrippedSize) {
                            this.strippedThumb = new BitmapDrawable(ApplicationLoader.applicationContext.getResources(), ImageLoader.getStrippedPhotoBitmap(photoSize.bytes, str));
                            return;
                        }
                    }
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
        }
    }

    public boolean didSpoilLoginCode() {
        return this.spoiledLoginCode;
    }

    public boolean doesPaidReactionExist() {
        TLRPC.Message message = this.messageOwner;
        if (message.reactions == null) {
            message.reactions = new TLRPC.TL_messageReactions();
            TLRPC.Message message2 = this.messageOwner;
            message2.reactions.reactions_as_tags = getDialogId(message2) == UserConfig.getInstance(this.currentAccount).getClientUserId();
            this.messageOwner.reactions.can_see_list = isFromGroup() || isFromUser();
        }
        for (int i = 0; i < this.messageOwner.reactions.results.size(); i++) {
            if (this.messageOwner.reactions.results.get(i).reaction instanceof TLRPC.TL_reactionPaid) {
                return true;
            }
        }
        return false;
    }

    public boolean ensurePaidReactionsExist(boolean z) {
        TLRPC.Message message = this.messageOwner;
        if (message.reactions == null) {
            message.reactions = new TLRPC.TL_messageReactions();
            TLRPC.Message message2 = this.messageOwner;
            message2.reactions.reactions_as_tags = getDialogId(message2) == UserConfig.getInstance(this.currentAccount).getClientUserId();
            this.messageOwner.reactions.can_see_list = isFromGroup() || isFromUser();
        }
        TLRPC.ReactionCount reactionCount = null;
        for (int i = 0; i < this.messageOwner.reactions.results.size(); i++) {
            if (this.messageOwner.reactions.results.get(i).reaction instanceof TLRPC.TL_reactionPaid) {
                reactionCount = this.messageOwner.reactions.results.get(i);
            }
        }
        if (reactionCount != null) {
            return false;
        }
        TLRPC.TL_reactionCount tL_reactionCount = new TLRPC.TL_reactionCount();
        tL_reactionCount.reaction = new TLRPC.TL_reactionPaid();
        tL_reactionCount.count = 1;
        tL_reactionCount.chosen = z;
        this.messageOwner.reactions.results.add(0, tL_reactionCount);
        return true;
    }

    public boolean equals(MessageObject messageObject) {
        return messageObject != null && getId() == messageObject.getId() && getDialogId() == messageObject.getDialogId();
    }

    public void expandChannelRecommendations(boolean z) {
        SharedPreferences.Editor edit = MessagesController.getInstance(this.currentAccount).getMainSettings().edit();
        String str = "c" + getDialogId() + "_rec";
        this.channelJoinedExpanded = z;
        edit.putBoolean(str, z).apply();
    }

    public void generateCaption() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateCaption():void");
    }

    public void generateGameMessageText(TLRPC.User user) {
        CharSequence replaceWithLink;
        if (user == null && isFromUser()) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id));
        }
        MessageObject messageObject = this.replyMessageObject;
        TLRPC.TL_game tL_game = (messageObject == null || getMedia(messageObject) == null || getMedia(this.replyMessageObject).game == null) ? null : getMedia(this.replyMessageObject).game;
        if (tL_game == null) {
            replaceWithLink = (user == null || user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) ? replaceWithLink(LocaleController.formatString("ActionUserScored", R.string.ActionUserScored, LocaleController.formatPluralString("Points", this.messageOwner.action.score, new Object[0])), "un1", user) : LocaleController.formatString("ActionYouScored", R.string.ActionYouScored, LocaleController.formatPluralString("Points", this.messageOwner.action.score, new Object[0]));
        } else {
            this.messageText = (user == null || user.id != UserConfig.getInstance(this.currentAccount).getClientUserId()) ? replaceWithLink(LocaleController.formatString("ActionUserScoredInGame", R.string.ActionUserScoredInGame, LocaleController.formatPluralString("Points", this.messageOwner.action.score, new Object[0])), "un1", user) : LocaleController.formatString("ActionYouScoredInGame", R.string.ActionYouScoredInGame, LocaleController.formatPluralString("Points", this.messageOwner.action.score, new Object[0]));
            replaceWithLink = replaceWithLink(this.messageText, "un2", tL_game);
        }
        this.messageText = replaceWithLink;
    }

    public void generateLayout(org.telegram.tgnet.TLRPC.User r36) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateLayout(org.telegram.tgnet.TLRPC$User):void");
    }

    public void generateLinkDescription() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateLinkDescription():void");
    }

    public void generatePaymentSentMessageText(TLRPC.User user, boolean z) {
        String str;
        String formatString;
        if (user == null) {
            user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(getDialogId()));
        }
        String firstName = user != null ? UserObject.getFirstName(user) : "";
        try {
            if ("XTR".equals(this.messageOwner.action.currency)) {
                str = "XTR " + this.messageOwner.action.total_amount;
            } else {
                LocaleController localeController = LocaleController.getInstance();
                TLRPC.MessageAction messageAction = this.messageOwner.action;
                str = localeController.formatCurrencyString(messageAction.total_amount, messageAction.currency);
            }
        } catch (Exception e) {
            FileLog.e(e);
            str = "<error>";
        }
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject == null || !(getMedia(messageObject) instanceof TLRPC.TL_messageMediaInvoice)) {
            TLRPC.MessageAction messageAction2 = this.messageOwner.action;
            int i = messageAction2.subscription_until_date;
            formatString = i != 0 ? z ? LocaleController.formatString(R.string.PaymentSuccessfullyPaidMeNoItemSubscription, firstName, str, LocaleController.formatDateTime(i, false)) : LocaleController.formatString(R.string.PaymentSuccessfullyPaidSubscriptionNoItem, str, firstName, LocaleController.formatDateTime(i, false)) : (!messageAction2.recurring_init || z) ? LocaleController.formatString(R.string.PaymentSuccessfullyPaidNoItem, str, firstName) : LocaleController.formatString(R.string.PaymentSuccessfullyPaidNoItemRecurrent, str, firstName);
        } else {
            TLRPC.MessageAction messageAction3 = this.messageOwner.action;
            formatString = messageAction3.subscription_until_date != 0 ? z ? LocaleController.formatString(R.string.PaymentSuccessfullyPaidMeSubscription, firstName, str, getMedia(this.replyMessageObject).title, LocaleController.formatDateTime(this.messageOwner.action.subscription_until_date, false)) : LocaleController.formatString(R.string.PaymentSuccessfullyPaidSubscription, str, firstName, getMedia(this.replyMessageObject).title, LocaleController.formatDateTime(this.messageOwner.action.subscription_until_date, false)) : (!messageAction3.recurring_init || z) ? LocaleController.formatString(R.string.PaymentSuccessfullyPaid, str, firstName, getMedia(this.replyMessageObject).title) : LocaleController.formatString(R.string.PaymentSuccessfullyPaidRecurrent, str, firstName, getMedia(this.replyMessageObject).title);
        }
        this.messageText = formatString;
        this.messageText = StarsIntroActivity.replaceStars(this.messageText);
    }

    public void generatePinMessageText(org.telegram.tgnet.TLRPC.User r8, org.telegram.tgnet.TLRPC.Chat r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generatePinMessageText(org.telegram.tgnet.TLRPC$User, org.telegram.tgnet.TLRPC$Chat):void");
    }

    public void generateThumbs(boolean r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.generateThumbs(boolean):void");
    }

    public int getApproximateHeight() {
        return getApproximateHeight(false);
    }

    public int getApproximateHeight(boolean z) {
        int i;
        int min;
        TLRPC.PhotoSize closestPhotoSizeWithSize;
        int min2;
        int i2 = this.type;
        int i3 = 0;
        if (i2 == 0) {
            int textHeight = textHeight();
            if ((getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaWebPage) && (getMedia(this.messageOwner).webpage instanceof TLRPC.TL_webPage)) {
                i3 = AndroidUtilities.dp(100.0f);
            }
            int i4 = textHeight + i3;
            return isReply() ? i4 + AndroidUtilities.dp(42.0f) : i4;
        }
        if (i2 == 20) {
            return AndroidUtilities.getPhotoSize();
        }
        if (i2 == 2) {
            return AndroidUtilities.dp(72.0f);
        }
        if (i2 == 12) {
            return AndroidUtilities.dp(71.0f);
        }
        if (i2 == 9) {
            return AndroidUtilities.dp(100.0f);
        }
        if (i2 == 4) {
            return AndroidUtilities.dp(114.0f);
        }
        if (i2 == 14) {
            return AndroidUtilities.dp(82.0f);
        }
        if (i2 == 10) {
            return AndroidUtilities.dp(30.0f);
        }
        if (i2 == 11 || i2 == 18 || i2 == 30 || i2 == 25 || i2 == 21) {
            return AndroidUtilities.dp(50.0f);
        }
        if (i2 == 5) {
            return AndroidUtilities.roundMessageSize;
        }
        if (i2 == 19) {
            return textHeight() + AndroidUtilities.dp(30.0f);
        }
        if (i2 == 13 || i2 == 15) {
            float f = AndroidUtilities.displaySize.y * 0.4f;
            float minTabletSide = (AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : AndroidUtilities.displaySize.x) * 0.5f;
            TLRPC.Document document = getDocument();
            if (document != null) {
                int size = document.attributes.size();
                for (int i5 = 0; i5 < size; i5++) {
                    TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i5);
                    if (documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) {
                        i3 = documentAttribute.w;
                        i = documentAttribute.h;
                        break;
                    }
                }
            }
            i = 0;
            if (i3 == 0) {
                i = (int) f;
                i3 = AndroidUtilities.dp(100.0f) + i;
            }
            float f2 = i;
            if (f2 > f) {
                i3 = (int) (i3 * (f / f2));
                i = (int) f;
            }
            float f3 = i3;
            if (f3 > minTabletSide) {
                i = (int) (i * (minTabletSide / f3));
            }
        } else {
            if (AndroidUtilities.isTablet()) {
                min = AndroidUtilities.getMinTabletSide();
            } else {
                Point point = AndroidUtilities.displaySize;
                min = Math.min(point.x, point.y);
            }
            int i6 = (int) (min * 0.7f);
            i = AndroidUtilities.dp(100.0f) + i6;
            if (i6 > AndroidUtilities.getPhotoSize()) {
                i6 = AndroidUtilities.getPhotoSize();
            }
            if (i > AndroidUtilities.getPhotoSize()) {
                i = AndroidUtilities.getPhotoSize();
            }
            if (z) {
                ArrayList<TLRPC.PhotoSize> arrayList = this.photoThumbs;
                closestPhotoSizeWithSize = (arrayList == null || arrayList.isEmpty()) ? null : this.photoThumbs.get(0);
            } else {
                closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(this.photoThumbs, AndroidUtilities.getPhotoSize());
            }
            if (closestPhotoSizeWithSize != null) {
                int i7 = (int) (closestPhotoSizeWithSize.h / (closestPhotoSizeWithSize.w / i6));
                if (i7 == 0) {
                    i7 = AndroidUtilities.dp(100.0f);
                }
                if (i7 <= i) {
                    i = i7 < AndroidUtilities.dp(120.0f) ? AndroidUtilities.dp(120.0f) : i7;
                }
                if (!z && needDrawBluredPreview()) {
                    if (AndroidUtilities.isTablet()) {
                        min2 = AndroidUtilities.getMinTabletSide();
                    } else {
                        Point point2 = AndroidUtilities.displaySize;
                        min2 = Math.min(point2.x, point2.y);
                    }
                    i = (int) (min2 * 0.5f);
                }
            }
        }
        return i + AndroidUtilities.dp(14.0f);
    }

    public String getArtworkUrl(boolean z) {
        TLRPC.Document document = getDocument();
        if (document == null || "audio/ogg".equals(document.mime_type)) {
            return null;
        }
        int size = document.attributes.size();
        for (int i = 0; i < size; i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                if (documentAttribute.voice) {
                    return null;
                }
                String str = documentAttribute.performer;
                String str2 = documentAttribute.title;
                if (!TextUtils.isEmpty(str)) {
                    int i2 = 0;
                    while (true) {
                        String[] strArr = excludeWords;
                        if (i2 >= strArr.length) {
                            break;
                        }
                        str = str.replace(strArr[i2], " ");
                        i2++;
                    }
                }
                if (TextUtils.isEmpty(str) && TextUtils.isEmpty(str2)) {
                    return null;
                }
                try {
                    StringBuilder sb = new StringBuilder();
                    sb.append("athumb://itunes.apple.com/search?term=");
                    sb.append(URLEncoder.encode(str + " - " + str2, "UTF-8"));
                    sb.append("&entity=song&limit=4");
                    sb.append(z ? "&s=1" : "");
                    return sb.toString();
                } catch (Exception unused) {
                    continue;
                }
            }
        }
        return null;
    }

    public long getChannelId() {
        return getChannelId(this.messageOwner);
    }

    public long getChatId() {
        TLRPC.Peer peer = this.messageOwner.peer_id;
        if (peer instanceof TLRPC.TL_peerChat) {
            return peer.chat_id;
        }
        if (peer instanceof TLRPC.TL_peerChannel) {
            return peer.channel_id;
        }
        return 0L;
    }

    public int getChatMode() {
        if (this.scheduled) {
            return 1;
        }
        return isQuickReply() ? 5 : 0;
    }

    public ArrayList<ReactionsLayoutInBubble.VisibleReaction> getChoosenReactions() {
        ArrayList<ReactionsLayoutInBubble.VisibleReaction> arrayList = new ArrayList<>();
        if (this.messageOwner.reactions == null) {
            return arrayList;
        }
        for (int i = 0; i < this.messageOwner.reactions.results.size(); i++) {
            if (this.messageOwner.reactions.results.get(i).chosen) {
                arrayList.add(ReactionsLayoutInBubble.VisibleReaction.fromTL(this.messageOwner.reactions.results.get(i).reaction));
            }
        }
        return arrayList;
    }

    public long getDialogId() {
        return getDialogId(this.messageOwner);
    }

    public String getDiceEmoji() {
        if (!isDice()) {
            return null;
        }
        TLRPC.TL_messageMediaDice tL_messageMediaDice = (TLRPC.TL_messageMediaDice) getMedia(this.messageOwner);
        return TextUtils.isEmpty(tL_messageMediaDice.emoticon) ? "🎲" : tL_messageMediaDice.emoticon.replace("️", "");
    }

    public int getDiceValue() {
        if (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaDice) {
            return ((TLRPC.TL_messageMediaDice) getMedia(this.messageOwner)).value;
        }
        return -1;
    }

    public TLRPC.Document getDocument() {
        VideoPlayer.VideoUri videoUri;
        TLRPC.Document document = this.emojiAnimatedSticker;
        return document != null ? document : (!hasVideoQualities() || (videoUri = this.highestQuality) == null) ? getDocument(this.messageOwner) : videoUri.document;
    }

    public TLRPC.Document getDocumentFast() {
        TLRPC.Document document = this.emojiAnimatedSticker;
        return document != null ? document : getDocument(this.messageOwner);
    }

    public String getDocumentName() {
        return FileLoader.getDocumentFileName(getDocument());
    }

    public double getDuration() {
        TL_stories.StoryItem storyItem;
        TLRPC.MessageMedia messageMedia;
        double d = this.attributeDuration;
        if (d > 0.0d) {
            return d;
        }
        TLRPC.Document document = getDocument();
        if (document == null && this.type == 23 && (storyItem = getMedia(this.messageOwner).storyItem) != null && (messageMedia = storyItem.media) != null) {
            document = messageMedia.document;
        }
        if (document == null) {
            return 0.0d;
        }
        int i = this.audioPlayerDuration;
        if (i > 0) {
            return i;
        }
        for (int i2 = 0; i2 < document.attributes.size(); i2++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i2);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                double d2 = documentAttribute.duration;
                this.attributeDuration = d2;
                return d2;
            }
            if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                double d3 = documentAttribute.duration;
                this.attributeDuration = d3;
                return d3;
            }
        }
        return this.audioPlayerDuration;
    }

    public TLRPC.TL_availableEffect getEffect() {
        TLRPC.Message message = this.messageOwner;
        if (message == null || (message.flags2 & 4) == 0) {
            return null;
        }
        return MessagesController.getInstance(this.currentAccount).getEffect(this.messageOwner.effect);
    }

    public long getEffectId() {
        TLRPC.Message message = this.messageOwner;
        if (message == null || (message.flags2 & 4) == 0) {
            return 0L;
        }
        return message.effect;
    }

    public int getEmojiOnlyCount() {
        return this.emojiOnlyCount;
    }

    public String getExtension() {
        String fileName = getFileName();
        int lastIndexOf = fileName.lastIndexOf(46);
        String substring = lastIndexOf != -1 ? fileName.substring(lastIndexOf + 1) : null;
        if (substring == null || substring.length() == 0) {
            substring = getDocument().mime_type;
        }
        if (substring == null) {
            substring = "";
        }
        return substring.toUpperCase();
    }

    public TLRPC.TL_factCheck getFactCheck() {
        return FactCheckController.getInstance(this.currentAccount).getFactCheck(this);
    }

    public CharSequence getFactCheckText() {
        if (!isFactCheckable()) {
            return null;
        }
        TLRPC.TL_factCheck factCheck = getFactCheck();
        if (factCheck == null || factCheck.text == null) {
            this.factCheckText = null;
            return null;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(factCheck.text.text);
        addEntitiesToText(spannableStringBuilder, factCheck.text.entities, isOutOwner(), false, false, false);
        this.factCheckText = spannableStringBuilder;
        return spannableStringBuilder;
    }

    public String getFileName() {
        return getDocument() != null ? getFileName(getDocument()) : getFileName(this.messageOwner);
    }

    public String getFileNameFast() {
        return getDocumentFast() != null ? getFileName(getDocumentFast()) : getFileName(this.messageOwner);
    }

    public Long getForwardedFromId() {
        TLRPC.MessageFwdHeader messageFwdHeader;
        TLRPC.Peer peer;
        TLRPC.Message message = this.messageOwner;
        if (message == null || (messageFwdHeader = message.fwd_from) == null || (peer = messageFwdHeader.from_id) == null) {
            return null;
        }
        return Long.valueOf(DialogObject.getPeerDialogId(peer));
    }

    public String getForwardedName() {
        TLRPC.MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
        if (messageFwdHeader == null) {
            return null;
        }
        TLRPC.Peer peer = messageFwdHeader.from_id;
        if (peer instanceof TLRPC.TL_peerChannel) {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.fwd_from.from_id.channel_id));
            if (chat != null) {
                return chat.title;
            }
            return null;
        }
        if (peer instanceof TLRPC.TL_peerChat) {
            TLRPC.Chat chat2 = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.fwd_from.from_id.chat_id));
            if (chat2 != null) {
                return chat2.title;
            }
            return null;
        }
        if (peer instanceof TLRPC.TL_peerUser) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.fwd_from.from_id.user_id));
            if (user != null) {
                return UserObject.getUserName(user);
            }
            return null;
        }
        String str = messageFwdHeader.from_name;
        if (str != null) {
            return str;
        }
        return null;
    }

    public long getFromChatId() {
        return getFromChatId(this.messageOwner);
    }

    public TLObject getFromPeerObject() {
        TLRPC.Message message = this.messageOwner;
        if (message == null) {
            return null;
        }
        TLRPC.Peer peer = message.from_id;
        if ((peer instanceof TLRPC.TL_peerChannel_layer131) || (peer instanceof TLRPC.TL_peerChannel)) {
            return MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.from_id.channel_id));
        }
        if ((peer instanceof TLRPC.TL_peerUser_layer131) || (peer instanceof TLRPC.TL_peerUser)) {
            return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id));
        }
        if ((peer instanceof TLRPC.TL_peerChat_layer131) || (peer instanceof TLRPC.TL_peerChat)) {
            return MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.from_id.chat_id));
        }
        return null;
    }

    public long getGroupId() {
        long j = this.localGroupId;
        return j != 0 ? j : getGroupIdForUse();
    }

    public long getGroupIdForUse() {
        long j = this.localSentGroupId;
        return j != 0 ? j : this.messageOwner.grouped_id;
    }

    public int getId() {
        return this.messageOwner.id;
    }

    public TLRPC.InputStickerSet getInputStickerSet() {
        return getInputStickerSet(this.messageOwner);
    }

    public int getMaxMessageTextWidth() {
        int dp;
        Uri parse;
        String lastPathSegment;
        this.generatedWithMinSize = (!AndroidUtilities.isTablet() || this.eventId == 0) ? AndroidUtilities.isTablet() ? AndroidUtilities.getMinTabletSide() : getParentWidth() : AndroidUtilities.dp(530.0f);
        this.generatedWithDensity = AndroidUtilities.density;
        if (this.hasCode && !this.isSaved) {
            dp = this.generatedWithMinSize - AndroidUtilities.dp(60.0f);
            if (needDrawAvatarInternal() && !isOutOwner() && !this.messageOwner.isThreadMessage) {
                dp -= AndroidUtilities.dp(52.0f);
            }
        } else if ((getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaWebPage) && getMedia(this.messageOwner).webpage != null && "telegram_background".equals(getMedia(this.messageOwner).webpage.type)) {
            try {
                parse = Uri.parse(getMedia(this.messageOwner).webpage.url);
                lastPathSegment = parse.getLastPathSegment();
            } catch (Exception unused) {
            }
            if (parse.getQueryParameter("bg_color") != null) {
                dp = AndroidUtilities.dp(220.0f);
            } else {
                if (lastPathSegment.length() == 6 || (lastPathSegment.length() == 13 && lastPathSegment.charAt(6) == '-')) {
                    dp = AndroidUtilities.dp(200.0f);
                }
                dp = 0;
            }
        } else {
            if (isAndroidTheme()) {
                dp = AndroidUtilities.dp(200.0f);
            }
            dp = 0;
        }
        if (dp == 0) {
            dp = this.generatedWithMinSize - AndroidUtilities.dp(80.0f);
            if (needDrawAvatarInternal() && !isOutOwner() && !this.messageOwner.isThreadMessage) {
                dp -= AndroidUtilities.dp(52.0f);
            }
            if (needDrawShareButton() && (this.isSaved || !isOutOwner())) {
                dp -= AndroidUtilities.dp((this.isSaved && isOutOwner()) ? 40.0f : 14.0f);
            }
            if (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaGame) {
                dp -= AndroidUtilities.dp(10.0f);
            }
        }
        int i = this.emojiOnlyCount;
        if (i < 1) {
            return dp;
        }
        int i2 = this.totalAnimatedEmojiCount;
        if (i2 <= 100) {
            return i - i2 < (SharedConfig.getDevicePerformanceClass() < 2 ? 50 : 100) ? (hasValidReplyMessageObject() || isForwarded()) ? Math.min(dp, (int) (this.generatedWithMinSize * 0.65f)) : dp : dp;
        }
        return dp;
    }

    public int getMediaExistanceFlags() {
        boolean z = this.attachPathExists;
        return this.mediaExists ? (z ? 1 : 0) | 2 : z ? 1 : 0;
    }

    public java.lang.CharSequence getMediaTitle(org.telegram.tgnet.TLRPC.MessageMedia r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.getMediaTitle(org.telegram.tgnet.TLRPC$MessageMedia):java.lang.CharSequence");
    }

    public int getMediaType() {
        if (isVideo()) {
            return 2;
        }
        if (isVoice()) {
            return 1;
        }
        if (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaDocument) {
            return 3;
        }
        return getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaPhoto ? 0 : 4;
    }

    public String getMimeType() {
        TLRPC.Document document = getDocument();
        if (document != null) {
            return document.mime_type;
        }
        if (!(getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaInvoice)) {
            return getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaPhoto ? "image/jpeg" : (!(getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaWebPage) || getMedia(this.messageOwner).webpage.photo == null) ? "" : "image/jpeg";
        }
        TLRPC.WebDocument webDocument = ((TLRPC.TL_messageMediaInvoice) getMedia(this.messageOwner)).webPhoto;
        return webDocument != null ? webDocument.mime_type : "";
    }

    public String getMusicAuthor() {
        return getMusicAuthor(true);
    }

    public java.lang.String getMusicAuthor(boolean r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.getMusicAuthor(boolean):java.lang.String");
    }

    public String getMusicTitle() {
        return getMusicTitle(true);
    }

    public String getMusicTitle(boolean z) {
        TLRPC.Document document = getDocument();
        if (document != null) {
            for (int i = 0; i < document.attributes.size(); i++) {
                TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
                if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                    if (documentAttribute.voice) {
                        if (z) {
                            return LocaleController.formatDateAudio(this.messageOwner.date, true);
                        }
                        return null;
                    }
                    String str = documentAttribute.title;
                    if (str != null && str.length() != 0) {
                        return str;
                    }
                    String documentFileName = FileLoader.getDocumentFileName(document);
                    return (TextUtils.isEmpty(documentFileName) && z) ? LocaleController.getString(R.string.AudioUnknownTitle) : documentFileName;
                }
                if ((documentAttribute instanceof TLRPC.TL_documentAttributeVideo) && documentAttribute.round_message) {
                    if (!isQuickReply()) {
                        return LocaleController.formatDateAudio(this.messageOwner.date, true);
                    }
                    return LocaleController.formatString(R.string.BusinessInReplies, "/" + getQuickReplyDisplayName());
                }
            }
            String documentFileName2 = FileLoader.getDocumentFileName(document);
            if (!TextUtils.isEmpty(documentFileName2)) {
                return documentFileName2;
            }
        }
        return LocaleController.getString(R.string.AudioUnknownTitle);
    }

    public TLObject getPeerObject() {
        TLRPC.Message message = this.messageOwner;
        if (message == null) {
            return null;
        }
        TLRPC.Peer peer = message.peer_id;
        if ((peer instanceof TLRPC.TL_peerChannel_layer131) || (peer instanceof TLRPC.TL_peerChannel)) {
            return MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.peer_id.channel_id));
        }
        if ((peer instanceof TLRPC.TL_peerUser_layer131) || (peer instanceof TLRPC.TL_peerUser)) {
            return MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.peer_id.user_id));
        }
        if ((peer instanceof TLRPC.TL_peerChat_layer131) || (peer instanceof TLRPC.TL_peerChat)) {
            return MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.peer_id.chat_id));
        }
        return null;
    }

    public long getPollId() {
        if (this.type != 17) {
            return 0L;
        }
        return ((TLRPC.TL_messageMediaPoll) getMedia(this.messageOwner)).poll.id;
    }

    public TLRPC.VideoSize getPremiumStickerAnimation() {
        return getPremiumStickerAnimation(getDocument());
    }

    public float getProgress() {
        return 0.0f;
    }

    public String getQuickReplyDisplayName() {
        String quickReplyName = getQuickReplyName();
        if (quickReplyName != null) {
            return quickReplyName;
        }
        QuickRepliesController.QuickReply findReply = QuickRepliesController.getInstance(this.currentAccount).findReply(getQuickReplyId());
        return findReply != null ? findReply.name : "";
    }

    public int getQuickReplyId() {
        return getQuickReplyId(this.messageOwner);
    }

    public String getQuickReplyName() {
        return getQuickReplyName(this.messageOwner);
    }

    public TLRPC.MessagePeerReaction getRandomUnreadReaction() {
        ArrayList<TLRPC.MessagePeerReaction> arrayList;
        TLRPC.TL_messageReactions tL_messageReactions = this.messageOwner.reactions;
        if (tL_messageReactions == null || (arrayList = tL_messageReactions.recent_reactions) == null || arrayList.isEmpty()) {
            return null;
        }
        return this.messageOwner.reactions.recent_reactions.get(0);
    }

    public int getRealId() {
        TLRPC.Message message = this.messageOwner;
        int i = message.realId;
        return i != 0 ? i : message.id;
    }

    public int getRepliesCount() {
        TLRPC.MessageReplies messageReplies = this.messageOwner.replies;
        if (messageReplies != null) {
            return messageReplies.replies;
        }
        return 0;
    }

    public int getReplyAnyMsgId() {
        TLRPC.MessageReplyHeader messageReplyHeader = this.messageOwner.reply_to;
        if (messageReplyHeader == null) {
            return 0;
        }
        int i = messageReplyHeader.reply_to_top_id;
        return i != 0 ? i : messageReplyHeader.reply_to_msg_id;
    }

    public int getReplyMsgId() {
        TLRPC.MessageReplyHeader messageReplyHeader = this.messageOwner.reply_to;
        if (messageReplyHeader != null) {
            return messageReplyHeader.reply_to_msg_id;
        }
        return 0;
    }

    public java.lang.CharSequence getReplyQuoteNameWithIcon() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.getReplyQuoteNameWithIcon():java.lang.CharSequence");
    }

    public int getReplyTopMsgId() {
        TLRPC.MessageReplyHeader messageReplyHeader = this.messageOwner.reply_to;
        if (messageReplyHeader != null) {
            return messageReplyHeader.reply_to_top_id;
        }
        return 0;
    }

    public int getReplyTopMsgId(boolean z) {
        TLRPC.MessageReplyHeader messageReplyHeader = this.messageOwner.reply_to;
        if (messageReplyHeader == null) {
            return 0;
        }
        if (z && (messageReplyHeader.flags & 2) > 0 && messageReplyHeader.reply_to_top_id == 0) {
            return 1;
        }
        return messageReplyHeader.reply_to_top_id;
    }

    public long getSavedDialogId() {
        return getSavedDialogId(UserConfig.getInstance(this.currentAccount).getClientUserId(), this.messageOwner);
    }

    public int getSecretTimeLeft() {
        TLRPC.Message message = this.messageOwner;
        int i = message.ttl;
        int i2 = message.destroyTime;
        return i2 != 0 ? Math.max(0, i2 - ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) : i;
    }

    public CharSequence getSecretTimeString() {
        String str;
        if (!isSecretMedia()) {
            return null;
        }
        if (this.messageOwner.ttl == Integer.MAX_VALUE) {
            if (this.secretOnceSpan == null) {
                this.secretOnceSpan = new SpannableString("v");
                ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.mini_viewonce);
                coloredImageSpan.setTranslateX(-AndroidUtilities.dp(3.0f));
                coloredImageSpan.setWidth(AndroidUtilities.dp(13.0f));
                CharSequence charSequence = this.secretOnceSpan;
                ((Spannable) charSequence).setSpan(coloredImageSpan, 0, charSequence.length(), 33);
            }
            return TextUtils.concat(this.secretOnceSpan, "1");
        }
        int secretTimeLeft = getSecretTimeLeft();
        if (secretTimeLeft < 60) {
            str = secretTimeLeft + "s";
        } else {
            str = (secretTimeLeft / 60) + "m";
        }
        if (this.secretPlaySpan == null) {
            this.secretPlaySpan = new SpannableString("p");
            ColoredImageSpan coloredImageSpan2 = new ColoredImageSpan(R.drawable.play_mini_video);
            coloredImageSpan2.setTranslateX(AndroidUtilities.dp(1.0f));
            coloredImageSpan2.setWidth(AndroidUtilities.dp(13.0f));
            CharSequence charSequence2 = this.secretPlaySpan;
            ((Spannable) charSequence2).setSpan(coloredImageSpan2, 0, charSequence2.length(), 33);
        }
        return TextUtils.concat(this.secretPlaySpan, str);
    }

    public long getSenderId() {
        TLRPC.Peer peer;
        TLRPC.Message message = this.messageOwner;
        TLRPC.MessageFwdHeader messageFwdHeader = message.fwd_from;
        if (messageFwdHeader == null || (peer = messageFwdHeader.saved_from_peer) == null) {
            TLRPC.Peer peer2 = message.from_id;
            if (peer2 instanceof TLRPC.TL_peerUser) {
                return peer2.user_id;
            }
            if (peer2 instanceof TLRPC.TL_peerChannel) {
                return -peer2.channel_id;
            }
            if (peer2 instanceof TLRPC.TL_peerChat) {
                return -peer2.chat_id;
            }
            if (message.post) {
                return message.peer_id.channel_id;
            }
        } else {
            long j = peer.user_id;
            if (j != 0) {
                TLRPC.Peer peer3 = messageFwdHeader.from_id;
                return peer3 instanceof TLRPC.TL_peerUser ? peer3.user_id : j;
            }
            if (peer.channel_id != 0) {
                if (isSavedFromMegagroup()) {
                    TLRPC.Peer peer4 = this.messageOwner.fwd_from.from_id;
                    if (peer4 instanceof TLRPC.TL_peerUser) {
                        return peer4.user_id;
                    }
                }
                TLRPC.MessageFwdHeader messageFwdHeader2 = this.messageOwner.fwd_from;
                TLRPC.Peer peer5 = messageFwdHeader2.from_id;
                return peer5 instanceof TLRPC.TL_peerChannel ? -peer5.channel_id : peer5 instanceof TLRPC.TL_peerChat ? -peer5.chat_id : -messageFwdHeader2.saved_from_peer.channel_id;
            }
            long j2 = peer.chat_id;
            if (j2 != 0) {
                TLRPC.Peer peer6 = messageFwdHeader.from_id;
                return peer6 instanceof TLRPC.TL_peerUser ? peer6.user_id : peer6 instanceof TLRPC.TL_peerChannel ? -peer6.channel_id : peer6 instanceof TLRPC.TL_peerChat ? -peer6.chat_id : -j2;
            }
        }
        return 0L;
    }

    public long getSize() {
        VideoPlayer.VideoUri videoUri = this.highestQuality;
        if (videoUri != null) {
            return videoUri.document.size;
        }
        VideoPlayer.VideoUri videoUri2 = this.thumbQuality;
        if (videoUri2 != null) {
            return videoUri2.document.size;
        }
        VideoPlayer.VideoUri videoUri3 = this.cachedQuality;
        return videoUri3 != null ? videoUri3.document.size : getMessageSize(this.messageOwner);
    }

    public String getStickerChar() {
        TLRPC.Document document = getDocument();
        if (document == null) {
            return null;
        }
        Iterator<TLRPC.DocumentAttribute> it = document.attributes.iterator();
        while (it.hasNext()) {
            TLRPC.DocumentAttribute next = it.next();
            if (next instanceof TLRPC.TL_documentAttributeSticker) {
                return next.alt;
            }
        }
        return null;
    }

    public String getStickerEmoji() {
        TLRPC.Document document = getDocument();
        if (document == null) {
            return null;
        }
        for (int i = 0; i < document.attributes.size(); i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if ((documentAttribute instanceof TLRPC.TL_documentAttributeSticker) || (documentAttribute instanceof TLRPC.TL_documentAttributeCustomEmoji)) {
                String str = documentAttribute.alt;
                if (str == null || str.length() <= 0) {
                    return null;
                }
                return documentAttribute.alt;
            }
        }
        return null;
    }

    public TLRPC.WebPage getStoryMentionWebpage() {
        if (!isStoryMention()) {
            return null;
        }
        TLRPC.WebPage webPage = this.storyMentionWebpage;
        if (webPage != null) {
            return webPage;
        }
        TLRPC.TL_webPage tL_webPage = new TLRPC.TL_webPage();
        tL_webPage.type = "telegram_story";
        TLRPC.TL_webPageAttributeStory tL_webPageAttributeStory = new TLRPC.TL_webPageAttributeStory();
        tL_webPageAttributeStory.id = this.messageOwner.media.id;
        tL_webPageAttributeStory.peer = MessagesController.getInstance(this.currentAccount).getPeer(this.messageOwner.media.user_id);
        TL_stories.StoryItem storyItem = this.messageOwner.media.storyItem;
        if (storyItem != null) {
            tL_webPageAttributeStory.flags |= 1;
            tL_webPageAttributeStory.storyItem = storyItem;
        }
        tL_webPage.attributes.add(tL_webPageAttributeStory);
        this.storyMentionWebpage = tL_webPage;
        return tL_webPage;
    }

    public TextPaint getTextPaint() {
        if (this.emojiOnlyCount < 1 || this.messageOwner == null || hasNonEmojiEntities()) {
            return Theme.chat_msgTextPaint;
        }
        int i = this.emojiOnlyCount;
        int i2 = this.animatedEmojiCount;
        boolean z = i == i2;
        switch (Math.max(i, i2)) {
            case 0:
            case 1:
            case 2:
                TextPaint[] textPaintArr = Theme.chat_msgTextPaintEmoji;
                return z ? textPaintArr[0] : textPaintArr[2];
            case 3:
                TextPaint[] textPaintArr2 = Theme.chat_msgTextPaintEmoji;
                return z ? textPaintArr2[1] : textPaintArr2[3];
            case 4:
                TextPaint[] textPaintArr3 = Theme.chat_msgTextPaintEmoji;
                return z ? textPaintArr3[2] : textPaintArr3[4];
            case 5:
                TextPaint[] textPaintArr4 = Theme.chat_msgTextPaintEmoji;
                return z ? textPaintArr4[3] : textPaintArr4[5];
            case 6:
                TextPaint[] textPaintArr5 = Theme.chat_msgTextPaintEmoji;
                return z ? textPaintArr5[4] : textPaintArr5[5];
            default:
                return Theme.chat_msgTextPaintEmoji[5];
        }
    }

    public TLRPC.Photo getVideoCover() {
        TLRPC.WebPage webPage;
        TLRPC.MessageMedia media = getMedia(this);
        if (media instanceof TLRPC.TL_messageMediaDocument) {
            return ((TLRPC.TL_messageMediaDocument) media).video_cover;
        }
        if (media == null || (webPage = media.webpage) == null || !webPage.video_cover_photo) {
            return null;
        }
        return webPage.photo;
    }

    public float getVideoSavedProgress() {
        if (this.cachedSavedTimestamp != null) {
            return PhotoViewer.getSavedProgressFast(this);
        }
        float savedProgress = PhotoViewer.getSavedProgress(this);
        this.cachedSavedTimestamp = Float.valueOf(savedProgress);
        return savedProgress;
    }

    public int getVideoStartsTimestamp() {
        String str;
        Integer num = this.cachedStartsTimestamp;
        if (num != null) {
            return num.intValue();
        }
        if (isVideo()) {
            TLRPC.MessageMedia media = getMedia(this);
            int i = media.video_timestamp;
            if (i != 0) {
                return i;
            }
            TLRPC.WebPage webPage = media.webpage;
            if (webPage != null && (str = webPage.url) != null) {
                try {
                    int timestampFromLink = LaunchActivity.getTimestampFromLink(Uri.parse(str));
                    this.cachedStartsTimestamp = Integer.valueOf(timestampFromLink);
                    return timestampFromLink;
                } catch (Exception unused) {
                }
            }
        }
        this.cachedStartsTimestamp = -1;
        return -1;
    }

    public CharSequence getVoiceTranscription() {
        String str;
        TLRPC.Message message = this.messageOwner;
        if (message == null || (str = message.voiceTranscription) == null) {
            return null;
        }
        if (!TextUtils.isEmpty(str)) {
            String str2 = this.messageOwner.voiceTranscription;
            return !TextUtils.isEmpty(str2) ? Emoji.replaceEmoji(str2, Theme.chat_msgTextPaint.getFontMetricsInt(), false) : str2;
        }
        SpannableString spannableString = new SpannableString(LocaleController.getString(R.string.NoWordsRecognized));
        spannableString.setSpan(new CharacterStyle() {
            @Override
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setTextSize(textPaint.getTextSize() * 0.8f);
                textPaint.setColor(Theme.chat_timePaint.getColor());
            }
        }, 0, spannableString.length(), 33);
        return spannableString;
    }

    public byte[] getWaveform() {
        if (getDocument() == null) {
            return null;
        }
        int i = 0;
        for (int i2 = 0; i2 < getDocument().attributes.size(); i2++) {
            TLRPC.DocumentAttribute documentAttribute = getDocument().attributes.get(i2);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                byte[] bArr = documentAttribute.waveform;
                if (bArr == null || bArr.length == 0) {
                    MediaController.getInstance().generateWaveform(this);
                }
                return documentAttribute.waveform;
            }
        }
        if (!isRoundVideo()) {
            return null;
        }
        if (this.randomWaveform == null) {
            this.randomWaveform = new byte[120];
            while (true) {
                byte[] bArr2 = this.randomWaveform;
                if (i >= bArr2.length) {
                    break;
                }
                bArr2[i] = (byte) (Math.random() * 255.0d);
                i++;
            }
        }
        return this.randomWaveform;
    }

    public ArrayList<MessageObject> getWebPagePhotos(ArrayList<MessageObject> arrayList, ArrayList<TLRPC.PageBlock> arrayList2) {
        TLRPC.WebPage webPage;
        TLRPC.Page page;
        if (arrayList == null) {
            arrayList = new ArrayList<>();
        }
        if (getMedia(this.messageOwner) == null || getMedia(this.messageOwner).webpage == null || (page = (webPage = getMedia(this.messageOwner).webpage).cached_page) == null) {
            return arrayList;
        }
        if (arrayList2 == null) {
            arrayList2 = page.blocks;
        }
        for (int i = 0; i < arrayList2.size(); i++) {
            TLRPC.PageBlock pageBlock = arrayList2.get(i);
            if (pageBlock instanceof TLRPC.TL_pageBlockSlideshow) {
                TLRPC.TL_pageBlockSlideshow tL_pageBlockSlideshow = (TLRPC.TL_pageBlockSlideshow) pageBlock;
                for (int i2 = 0; i2 < tL_pageBlockSlideshow.items.size(); i2++) {
                    arrayList.add(getMessageObjectForBlock(webPage, tL_pageBlockSlideshow.items.get(i2)));
                }
            } else if (pageBlock instanceof TLRPC.TL_pageBlockCollage) {
                TLRPC.TL_pageBlockCollage tL_pageBlockCollage = (TLRPC.TL_pageBlockCollage) pageBlock;
                for (int i3 = 0; i3 < tL_pageBlockCollage.items.size(); i3++) {
                    arrayList.add(getMessageObjectForBlock(webPage, tL_pageBlockCollage.items.get(i3)));
                }
            }
        }
        return arrayList;
    }

    public boolean hasAttachedStickers() {
        if (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaPhoto) {
            return getMedia(this.messageOwner).photo != null && getMedia(this.messageOwner).photo.has_stickers;
        }
        if (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaDocument) {
            return isDocumentHasAttachedStickers(getMedia(this.messageOwner).document);
        }
        return false;
    }

    public boolean hasEntitiesFromServer() {
        TLRPC.Message message = this.messageOwner;
        if (message == null || message.entities == null) {
            return false;
        }
        for (int i = 0; i < this.messageOwner.entities.size(); i++) {
            TLRPC.MessageEntity messageEntity = this.messageOwner.entities.get(i);
            if ((messageEntity instanceof TLRPC.TL_messageEntityPhone) || (messageEntity instanceof TLRPC.TL_messageEntityBankCard)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasExtendedMedia() {
        TLRPC.MessageMedia messageMedia = this.messageOwner.media;
        return (messageMedia == null || (messageMedia instanceof TLRPC.TL_messageMediaPaidMedia) || messageMedia.extended_media.isEmpty()) ? false : true;
    }

    public boolean hasExtendedMediaPreview() {
        TLRPC.MessageMedia messageMedia = this.messageOwner.media;
        return (messageMedia == null || (messageMedia instanceof TLRPC.TL_messageMediaPaidMedia) || messageMedia.extended_media.isEmpty() || !(this.messageOwner.media.extended_media.get(0) instanceof TLRPC.TL_messageExtendedMediaPreview)) ? false : true;
    }

    public boolean hasHighlightedWords() {
        ArrayList<String> arrayList = this.highlightedWords;
        return (arrayList == null || arrayList.isEmpty()) ? false : true;
    }

    public boolean hasInlineBotButtons() {
        TLRPC.Message message;
        if (!this.isRestrictedMessage && !this.isRepostPreview && (message = this.messageOwner) != null) {
            TLRPC.ReplyMarkup replyMarkup = message.reply_markup;
            if ((replyMarkup instanceof TLRPC.TL_replyInlineMarkup) && !replyMarkup.rows.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasLinkMediaToMakeSmall() {
        boolean z = !this.isRestrictedMessage && (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaWebPage) && (getMedia(this.messageOwner).webpage instanceof TLRPC.TL_webPage);
        TLRPC.WebPage webPage = z ? getMedia(this.messageOwner).webpage : null;
        String str = webPage != null ? webPage.type : null;
        if (!z || isGiveawayOrGiveawayResults() || webPage == null) {
            return false;
        }
        if (webPage.photo != null || isVideoDocument(webPage.document)) {
            return ((TextUtils.isEmpty(webPage.description) && TextUtils.isEmpty(webPage.title)) || isSponsored() || "telegram_megagroup".equals(str) || "telegram_background".equals(str) || "telegram_voicechat".equals(str) || "telegram_videochat".equals(str) || "telegram_livestream".equals(str) || "telegram_user".equals(str) || "telegram_story".equals(str) || "telegram_channel_boost".equals(str) || "telegram_group_boost".equals(str) || "telegram_chat".equals(str)) ? false : true;
        }
        return false;
    }

    public boolean hasMediaSpoilers() {
        TLRPC.MessageMedia messageMedia;
        return (!this.isRepostPreview && (((messageMedia = this.messageOwner.media) != null && messageMedia.spoiler) || needDrawBluredPreview())) || isHiddenSensitive();
    }

    public boolean hasPaidMediaPreview() {
        TLRPC.MessageMedia messageMedia = this.messageOwner.media;
        return messageMedia != null && (messageMedia instanceof TLRPC.TL_messageMediaPaidMedia) && !messageMedia.extended_media.isEmpty() && (this.messageOwner.media.extended_media.get(0) instanceof TLRPC.TL_messageExtendedMediaPreview);
    }

    public boolean hasReaction(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        if (hasReactions() && visibleReaction != null) {
            for (int i = 0; i < this.messageOwner.reactions.results.size(); i++) {
                if (visibleReaction.isSame(this.messageOwner.reactions.results.get(i).reaction)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasReactions() {
        TLRPC.TL_messageReactions tL_messageReactions = this.messageOwner.reactions;
        return (tL_messageReactions == null || tL_messageReactions.results.isEmpty()) ? false : true;
    }

    public boolean hasReplies() {
        TLRPC.MessageReplies messageReplies = this.messageOwner.replies;
        return messageReplies != null && messageReplies.replies > 0;
    }

    public boolean hasRevealedExtendedMedia() {
        TLRPC.MessageMedia messageMedia = this.messageOwner.media;
        return (messageMedia == null || (messageMedia instanceof TLRPC.TL_messageMediaPaidMedia) || messageMedia.extended_media.isEmpty() || !(this.messageOwner.media.extended_media.get(0) instanceof TLRPC.TL_messageExtendedMedia)) ? false : true;
    }

    public boolean hasValidGroupId() {
        ArrayList<TLRPC.PhotoSize> arrayList;
        int i;
        return getGroupId() != 0 && (!((arrayList = this.photoThumbs) == null || arrayList.isEmpty()) || (i = this.type) == 3 || i == 1 || isMusic() || isDocument());
    }

    public boolean hasValidGroupIdFast() {
        ArrayList<TLRPC.PhotoSize> arrayList;
        int i;
        return getGroupId() != 0 && (!((arrayList = this.photoThumbs) == null || arrayList.isEmpty()) || (i = this.type) == 3 || i == 1 || i == 14 || i == 9);
    }

    public boolean hasValidReplyMessageObject() {
        MessageObject messageObject = this.replyMessageObject;
        if (messageObject != null) {
            TLRPC.Message message = messageObject.messageOwner;
            if (!(message instanceof TLRPC.TL_messageEmpty)) {
                TLRPC.MessageAction messageAction = message.action;
                if (!(messageAction instanceof TLRPC.TL_messageActionHistoryClear) && !(messageAction instanceof TLRPC.TL_messageActionTopicCreate)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasVideoCover() {
        return getVideoCover() != null;
    }

    public boolean hasVideoQualities() {
        TLRPC.MessageMedia messageMedia;
        if (this.videoQualitiesCached == null) {
            try {
                TLRPC.Message message = this.messageOwner;
                boolean z = false;
                if (message != null && (messageMedia = message.media) != null && messageMedia.document != null && !messageMedia.alt_documents.isEmpty()) {
                    int i = this.currentAccount;
                    TLRPC.Message message2 = this.messageOwner;
                    ArrayList<VideoPlayer.Quality> qualities = VideoPlayer.getQualities(i, message2 != null ? message2.media : null);
                    this.videoQualities = qualities;
                    if (qualities != null && qualities.size() > 1) {
                        z = true;
                    }
                    this.videoQualitiesCached = Boolean.valueOf(z);
                    this.highestQuality = VideoPlayer.getQualityForPlayer(this.videoQualities);
                    this.thumbQuality = VideoPlayer.getQualityForThumb(this.videoQualities);
                    this.cachedQuality = VideoPlayer.getCachedQuality(this.videoQualities);
                }
                this.videoQualitiesCached = Boolean.FALSE;
                return false;
            } catch (Exception e) {
                FileLog.e(e);
                this.videoQualitiesCached = Boolean.FALSE;
            }
        }
        return this.videoQualitiesCached.booleanValue();
    }

    public boolean isAlbumSingle() {
        return getMedia(this) instanceof TLRPC.TL_messageMediaPaidMedia;
    }

    public boolean isAndroidTheme() {
        if (getMedia(this.messageOwner) != null && getMedia(this.messageOwner).webpage != null && !getMedia(this.messageOwner).webpage.attributes.isEmpty()) {
            int size = getMedia(this.messageOwner).webpage.attributes.size();
            for (int i = 0; i < size; i++) {
                TLRPC.WebPageAttribute webPageAttribute = getMedia(this.messageOwner).webpage.attributes.get(i);
                if (webPageAttribute instanceof TLRPC.TL_webPageAttributeTheme) {
                    TLRPC.TL_webPageAttributeTheme tL_webPageAttributeTheme = (TLRPC.TL_webPageAttributeTheme) webPageAttribute;
                    ArrayList<TLRPC.Document> arrayList = tL_webPageAttributeTheme.documents;
                    int size2 = arrayList.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        if ("application/x-tgtheme-android".equals(arrayList.get(i2).mime_type)) {
                            return true;
                        }
                    }
                    if (tL_webPageAttributeTheme.settings != null) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isAnimatedAnimatedEmoji() {
        return isAnimatedEmoji() && isAnimatedEmoji(getDocument());
    }

    public boolean isAnimatedEmoji() {
        return (this.emojiAnimatedSticker == null && this.emojiAnimatedStickerId == null) ? false : true;
    }

    public boolean isAnimatedEmojiStickerSingle() {
        return this.emojiAnimatedStickerId != null;
    }

    public boolean isAnimatedEmojiStickers() {
        return this.type == 19;
    }

    public boolean isAnimatedSticker() {
        int i = this.type;
        if (i != 1000) {
            return i == 15;
        }
        boolean isEncryptedDialog = DialogObject.isEncryptedDialog(getDialogId());
        if (isEncryptedDialog && this.messageOwner.stickerVerified != 1) {
            return false;
        }
        if (this.emojiAnimatedStickerId == null || this.emojiAnimatedSticker != null) {
            return isAnimatedStickerDocument(getDocument(), (this.emojiAnimatedSticker == null && isEncryptedDialog && !isOut()) ? false : true);
        }
        return true;
    }

    public boolean isAnyGift() {
        int i = this.type;
        return i == 30 || i == 18 || i == 25;
    }

    public boolean isAnyKindOfSticker() {
        int i = this.type;
        return i == 13 || i == 15 || i == 19;
    }

    public boolean isBotPreview() {
        return this.storyItem instanceof StoriesController.BotPreview;
    }

    public boolean isComments() {
        TLRPC.MessageReplies messageReplies = this.messageOwner.replies;
        return messageReplies != null && messageReplies.comments;
    }

    public boolean isContentUnread() {
        return this.messageOwner.media_unread;
    }

    public boolean isCurrentWallpaper() {
        TLRPC.Message message;
        TLRPC.MessageAction messageAction;
        TLRPC.UserFull userFull;
        TLRPC.WallPaper wallPaper;
        return (!isWallpaperAction() || (message = this.messageOwner) == null || (messageAction = message.action) == null || messageAction.wallpaper == null || (userFull = MessagesController.getInstance(this.currentAccount).getUserFull(getDialogId())) == null || (wallPaper = userFull.wallpaper) == null || !userFull.wallpaper_overridden || this.messageOwner.action.wallpaper.id != wallPaper.id) ? false : true;
    }

    public boolean isDice() {
        return getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaDice;
    }

    public boolean isDocument() {
        return (getDocument() == null || isVideo() || isMusic() || isVoice() || isAnyKindOfSticker()) ? false : true;
    }

    public boolean isEdited() {
        TLRPC.Message message = this.messageOwner;
        return (message == null || (message.flags & 32768) == 0 || message.edit_date == 0 || message.edit_hide) ? false : true;
    }

    public boolean isEditing() {
        TLRPC.Message message = this.messageOwner;
        return message.send_state == 3 && message.id > 0;
    }

    public boolean isEditingMedia() {
        return getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaPhoto ? getMedia(this.messageOwner).photo.id == 0 : (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaDocument) && getMedia(this.messageOwner).document.dc_id == 0;
    }

    public boolean isEmbedVideo() {
        TLRPC.MessageMedia messageMedia;
        TLRPC.WebPage webPage;
        TLRPC.MessageMedia messageMedia2;
        Boolean bool = this.isEmbedVideoCached;
        boolean z = false;
        if (bool != null) {
            TLRPC.Message message = this.messageOwner;
            return (message == null || (messageMedia2 = message.media) == null || messageMedia2.webpage == null || !bool.booleanValue()) ? false : true;
        }
        TLRPC.Message message2 = this.messageOwner;
        if (message2 != null && (messageMedia = message2.media) != null && (webPage = messageMedia.webpage) != null && !TextUtils.isEmpty(WebPlayerView.getYouTubeVideoId(webPage.url))) {
            z = true;
        }
        this.isEmbedVideoCached = Boolean.valueOf(z);
        return z;
    }

    public boolean isExpiredLiveLocation(int i) {
        TLRPC.Message message = this.messageOwner;
        return message.date + getMedia(message).period <= i;
    }

    public boolean isExpiredStory() {
        int i = this.type;
        return (i == 23 || i == 24) && (this.messageOwner.media.storyItem instanceof TL_stories.TL_storyItemDeleted);
    }

    public boolean isFactCheckable() {
        int i;
        return getId() >= 0 && !isSponsored() && ((i = this.type) == 0 || i == 2 || i == 1 || i == 3 || i == 8 || i == 9);
    }

    public boolean isFcmMessage() {
        return this.localType != 0;
    }

    public boolean isForwarded() {
        return isForwardedMessage(this.messageOwner);
    }

    public boolean isForwardedChannelPost() {
        TLRPC.MessageFwdHeader messageFwdHeader;
        TLRPC.Message message = this.messageOwner;
        TLRPC.Peer peer = message.from_id;
        if ((peer instanceof TLRPC.TL_peerChannel) && (messageFwdHeader = message.fwd_from) != null && messageFwdHeader.channel_post != 0) {
            TLRPC.Peer peer2 = messageFwdHeader.saved_from_peer;
            if ((peer2 instanceof TLRPC.TL_peerChannel) && peer.channel_id == peer2.channel_id) {
                return true;
            }
        }
        return false;
    }

    public boolean isFromChannel() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isFromChannel():boolean");
    }

    public boolean isFromChat() {
        TLRPC.Peer peer;
        if (getDialogId() == UserConfig.getInstance(this.currentAccount).clientUserId) {
            return true;
        }
        TLRPC.Peer peer2 = this.messageOwner.peer_id;
        TLRPC.Chat chat = null;
        if (peer2 != null) {
            long j = peer2.channel_id;
            if (j != 0) {
                chat = getChat(null, null, j);
            }
        }
        if (!(ChatObject.isChannel(chat) && chat.megagroup) && ((peer = this.messageOwner.peer_id) == null || peer.chat_id == 0)) {
            return (peer == null || peer.channel_id == 0 || chat == null || !chat.megagroup) ? false : true;
        }
        return true;
    }

    public boolean isFromGroup() {
        TLRPC.Message message = this.messageOwner;
        if (message == null) {
            return false;
        }
        TLRPC.Peer peer = message.peer_id;
        TLRPC.Chat chat = null;
        if (peer != null) {
            long j = peer.channel_id;
            if (j != 0) {
                chat = getChat(null, null, j);
            }
        }
        return (this.messageOwner.from_id instanceof TLRPC.TL_peerChannel) && ChatObject.isChannel(chat) && chat.megagroup;
    }

    public boolean isFromUser() {
        TLRPC.Message message = this.messageOwner;
        return (message.from_id instanceof TLRPC.TL_peerUser) && !message.post;
    }

    public boolean isGame() {
        return isGameMessage(this.messageOwner);
    }

    public boolean isGif() {
        return isGifMessage(this.messageOwner);
    }

    public boolean isGiveaway() {
        return this.type == 26;
    }

    public boolean isGiveawayOrGiveawayResults() {
        return isGiveaway() || isGiveawayResults();
    }

    public boolean isGiveawayResults() {
        return this.type == 28;
    }

    public boolean isHiddenSensitive() {
        return isSensitive() && !MessagesController.getInstance(this.currentAccount).showSensitiveContent();
    }

    public boolean isImportedForward() {
        TLRPC.MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
        return messageFwdHeader != null && messageFwdHeader.imported;
    }

    public boolean isInvoice() {
        return isInvoiceMessage(this.messageOwner);
    }

    public boolean isLinkMediaSmall() {
        TLRPC.WebPage webPage = (!this.isRestrictedMessage && (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaWebPage) && (getMedia(this.messageOwner).webpage instanceof TLRPC.TL_webPage)) ? getMedia(this.messageOwner).webpage : null;
        String str = webPage != null ? webPage.type : null;
        return !(webPage != null && TextUtils.isEmpty(webPage.description) && TextUtils.isEmpty(webPage.title)) && ("app".equals(str) || "profile".equals(str) || "article".equals(str) || "telegram_bot".equals(str) || "telegram_user".equals(str) || "telegram_channel".equals(str) || "telegram_megagroup".equals(str) || "telegram_voicechat".equals(str) || "telegram_videochat".equals(str) || "telegram_livestream".equals(str) || "telegram_channel_boost".equals(str) || "telegram_group_boost".equals(str) || "telegram_chat".equals(str));
    }

    public boolean isLinkedToChat(long j) {
        TLRPC.MessageReplies messageReplies = this.messageOwner.replies;
        return messageReplies != null && (j == 0 || messageReplies.channel_id == j);
    }

    public boolean isLiveLocation() {
        return isLiveLocationMessage(this.messageOwner);
    }

    public boolean isLocation() {
        return isLocationMessage(this.messageOwner);
    }

    public boolean isMask() {
        return isMaskMessage(this.messageOwner);
    }

    public boolean isMediaEmpty() {
        return isMediaEmpty(this.messageOwner);
    }

    public boolean isMediaEmpty(boolean z) {
        return isMediaEmpty(this.messageOwner, z);
    }

    public boolean isMediaEmptyWebpage() {
        return isMediaEmptyWebpage(this.messageOwner);
    }

    public boolean isMusic() {
        return (!isMusicMessage(this.messageOwner) || isVideo() || isRoundVideo()) ? false : true;
    }

    public Boolean isMyPaidReactionAnonymous() {
        TLRPC.TL_messageReactions tL_messageReactions;
        ArrayList<TLRPC.MessageReactor> arrayList;
        TLRPC.Message message = this.messageOwner;
        if (message == null || (tL_messageReactions = message.reactions) == null || (arrayList = tL_messageReactions.top_reactors) == null) {
            return null;
        }
        Iterator<TLRPC.MessageReactor> it = arrayList.iterator();
        while (it.hasNext()) {
            TLRPC.MessageReactor next = it.next();
            if (next != null && next.my) {
                return Boolean.valueOf(next.anonymous);
            }
        }
        return null;
    }

    public boolean isNewGif() {
        return getMedia(this.messageOwner) != null && isNewGifDocument(getDocument());
    }

    public boolean isOut() {
        return this.messageOwner.out;
    }

    public boolean isOutOwner() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.isOutOwner():boolean");
    }

    public boolean isPaidReactionChosen() {
        if (this.messageOwner.reactions == null) {
            return false;
        }
        for (int i = 0; i < this.messageOwner.reactions.results.size(); i++) {
            if (this.messageOwner.reactions.results.get(i).reaction instanceof TLRPC.TL_reactionPaid) {
                return this.messageOwner.reactions.results.get(i).chosen;
            }
        }
        return false;
    }

    public boolean isPhoto() {
        return isPhoto(this.messageOwner);
    }

    public boolean isPoll() {
        return this.type == 17;
    }

    public boolean isPollClosed() {
        if (this.type != 17) {
            return false;
        }
        return ((TLRPC.TL_messageMediaPoll) getMedia(this.messageOwner)).poll.closed;
    }

    public boolean isPremiumSticker() {
        if (getMedia(this.messageOwner) == null || !getMedia(this.messageOwner).nopremium) {
            return isPremiumSticker(getDocument());
        }
        return false;
    }

    public boolean isPrivateForward() {
        TLRPC.MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
        return (messageFwdHeader == null || TextUtils.isEmpty(messageFwdHeader.from_name)) ? false : true;
    }

    public boolean isPublicPoll() {
        if (this.type != 17) {
            return false;
        }
        return ((TLRPC.TL_messageMediaPoll) getMedia(this.messageOwner)).poll.public_voters;
    }

    public boolean isQuickReply() {
        return isQuickReply(this.messageOwner);
    }

    public boolean isQuiz() {
        if (this.type != 17) {
            return false;
        }
        return ((TLRPC.TL_messageMediaPoll) getMedia(this.messageOwner)).poll.quiz;
    }

    public boolean isReactionsAvailable() {
        return (isEditing() || isSponsored() || !isSent() || isExpiredStory() || !canSetReaction()) ? false : true;
    }

    public boolean isReply() {
        TLRPC.Message message;
        TLRPC.MessageReplyHeader messageReplyHeader;
        MessageObject messageObject = this.replyMessageObject;
        return ((messageObject != null && (messageObject.messageOwner instanceof TLRPC.TL_messageEmpty)) || (messageReplyHeader = (message = this.messageOwner).reply_to) == null || (messageReplyHeader.reply_to_msg_id == 0 && messageReplyHeader.reply_to_random_id == 0) || (message.flags & 8) == 0) ? false : true;
    }

    public boolean isReplyToStory() {
        TLRPC.Message message;
        TLRPC.MessageReplyHeader messageReplyHeader;
        MessageObject messageObject = this.replyMessageObject;
        return ((messageObject != null && (messageObject.messageOwner instanceof TLRPC.TL_messageEmpty)) || (messageReplyHeader = (message = this.messageOwner).reply_to) == null || messageReplyHeader.story_id == 0 || (message.flags & 8) == 0) ? false : true;
    }

    public boolean isRoundOnce() {
        TLRPC.Message message;
        TLRPC.MessageMedia messageMedia;
        return isRoundVideo() && (message = this.messageOwner) != null && (messageMedia = message.media) != null && messageMedia.ttl_seconds == Integer.MAX_VALUE;
    }

    public boolean isRoundVideo() {
        if (this.isRoundVideoCached == 0) {
            this.isRoundVideoCached = (this.type == 5 || isRoundVideoMessage(this.messageOwner)) ? 1 : 2;
        }
        return this.isRoundVideoCached == 1;
    }

    public boolean isSavedFromMegagroup() {
        TLRPC.Peer peer;
        TLRPC.MessageFwdHeader messageFwdHeader = this.messageOwner.fwd_from;
        if (messageFwdHeader == null || (peer = messageFwdHeader.saved_from_peer) == null || peer.channel_id == 0) {
            return false;
        }
        return ChatObject.isMegagroup(MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.messageOwner.fwd_from.saved_from_peer.channel_id)));
    }

    public boolean isSecretMedia() {
        int i;
        TLRPC.Message message = this.messageOwner;
        return message instanceof TLRPC.TL_message_secret ? (((getMedia(message) instanceof TLRPC.TL_messageMediaPhoto) || isGif()) && (i = this.messageOwner.ttl) > 0 && i <= 60) || isVoice() || isRoundVideo() || isVideo() : (message instanceof TLRPC.TL_message) && getMedia(message) != null && getMedia(this.messageOwner).ttl_seconds != 0 && ((getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaPhoto) || (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaDocument));
    }

    public boolean isSendError() {
        TLRPC.Message message = this.messageOwner;
        if (message.send_state != 2 || message.id >= 0) {
            if (this.scheduled && message.id > 0) {
                if (message.date < ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - (this.messageOwner.video_processing_pending ? 300 : 60)) {
                }
            }
            return false;
        }
        return true;
    }

    public boolean isSending() {
        TLRPC.Message message = this.messageOwner;
        return message.send_state == 1 && message.id < 0;
    }

    public boolean isSensitive() {
        TLRPC.Chat chat;
        Boolean bool = this.isSensitiveCached;
        if (bool != null) {
            return bool.booleanValue();
        }
        if (this.messageOwner == null || !canBeSensitive()) {
            return false;
        }
        if (!this.messageOwner.restriction_reason.isEmpty()) {
            for (int i = 0; i < this.messageOwner.restriction_reason.size(); i++) {
                TLRPC.RestrictionReason restrictionReason = this.messageOwner.restriction_reason.get(i);
                if ("sensitive".equals(restrictionReason.reason) && ("all".equals(restrictionReason.platform) || (((!ApplicationLoader.isStandaloneBuild() && !BuildVars.isBetaApp()) || BuildVars.DEBUG_PRIVATE_VERSION) && "android".equals(restrictionReason.platform)))) {
                    break;
                }
            }
        }
        if (getDialogId() < 0 && (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-getDialogId()))) != null && chat.restriction_reason != null) {
            for (int i2 = 0; i2 < chat.restriction_reason.size(); i2++) {
                TLRPC.RestrictionReason restrictionReason2 = chat.restriction_reason.get(i2);
                if (!"sensitive".equals(restrictionReason2.reason) || (!"all".equals(restrictionReason2.platform) && (((ApplicationLoader.isStandaloneBuild() || BuildVars.isBetaApp()) && !BuildVars.DEBUG_PRIVATE_VERSION) || !"android".equals(restrictionReason2.platform)))) {
                }
                this.isSensitiveCached = Boolean.TRUE;
                return true;
            }
        }
        this.isSensitiveCached = Boolean.FALSE;
        return false;
    }

    public boolean isSent() {
        TLRPC.Message message = this.messageOwner;
        return message.send_state == 0 || message.id > 0;
    }

    public boolean isSponsored() {
        return this.sponsoredId != null;
    }

    public boolean isStarGiftAction() {
        TLRPC.Message message = this.messageOwner;
        if (message != null) {
            TLRPC.MessageAction messageAction = message.action;
            if ((messageAction instanceof TLRPC.TL_messageActionStarGift) || (messageAction instanceof TLRPC.TL_messageActionStarGiftUnique)) {
                return true;
            }
        }
        return false;
    }

    public boolean isSticker() {
        int i = this.type;
        return i != 1000 ? i == 13 : isStickerDocument(getDocument()) || isVideoSticker(getDocument());
    }

    public boolean isStory() {
        return this.storyItem != null;
    }

    public boolean isStoryMedia() {
        TLRPC.Message message = this.messageOwner;
        return message != null && (message.media instanceof TLRPC.TL_messageMediaStory);
    }

    public boolean isStoryMention() {
        return this.type == 24 && !isExpiredStory();
    }

    public boolean isSupergroup() {
        if (this.localSupergroup) {
            return true;
        }
        Boolean bool = this.cachedIsSupergroup;
        if (bool != null) {
            return bool.booleanValue();
        }
        TLRPC.Peer peer = this.messageOwner.peer_id;
        if (peer != null) {
            long j = peer.channel_id;
            if (j != 0) {
                TLRPC.Chat chat = getChat(null, null, j);
                if (chat == null) {
                    return false;
                }
                boolean z = chat.megagroup;
                this.cachedIsSupergroup = Boolean.valueOf(z);
                return z;
            }
        }
        this.cachedIsSupergroup = Boolean.FALSE;
        return false;
    }

    public boolean isTheme() {
        return (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaWebPage) && getMedia(this.messageOwner).webpage != null && "telegram_theme".equals(getMedia(this.messageOwner).webpage.type);
    }

    public boolean isUnread() {
        TLRPC.Message message = this.messageOwner;
        return message != null && message.unread;
    }

    public boolean isUnsupported() {
        return getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaUnsupported;
    }

    public boolean isVideo() {
        return isVideoMessage(this.messageOwner);
    }

    public boolean isVideoAvatar() {
        TLRPC.Photo photo;
        TLRPC.MessageAction messageAction = this.messageOwner.action;
        return (messageAction == null || (photo = messageAction.photo) == null || photo.video_sizes.isEmpty()) ? false : true;
    }

    public boolean isVideoCall() {
        TLRPC.MessageAction messageAction = this.messageOwner.action;
        return (messageAction instanceof TLRPC.TL_messageActionPhoneCall) && messageAction.video;
    }

    public boolean isVideoSticker() {
        return getDocument() != null && isVideoStickerDocument(getDocument());
    }

    public boolean isVideoStory() {
        TL_stories.StoryItem storyItem;
        TLRPC.MessageMedia messageMedia;
        TLRPC.MessageMedia media = getMedia(this.messageOwner);
        if (media == null || (storyItem = media.storyItem) == null || (messageMedia = storyItem.media) == null) {
            return false;
        }
        return isVideoDocument(messageMedia.document);
    }

    public boolean isVoice() {
        return isVoiceMessage(this.messageOwner);
    }

    public boolean isVoiceOnce() {
        TLRPC.Message message;
        TLRPC.MessageMedia messageMedia;
        return isVoice() && (message = this.messageOwner) != null && (messageMedia = message.media) != null && messageMedia.ttl_seconds == Integer.MAX_VALUE;
    }

    public boolean isVoiceTranscriptionOpen() {
        if (this.messageOwner != null && (isVoice() || (isRoundVideo() && TranscribeButton.isVideoTranscriptionOpen(this)))) {
            TLRPC.Message message = this.messageOwner;
            if (message.voiceTranscriptionOpen && message.voiceTranscription != null && (message.voiceTranscriptionFinal || TranscribeButton.isTranscribing(this))) {
                return true;
            }
        }
        return false;
    }

    public boolean isVoted() {
        TLRPC.TL_messageMediaPoll tL_messageMediaPoll;
        TLRPC.PollResults pollResults;
        if (this.type == 17 && (pollResults = (tL_messageMediaPoll = (TLRPC.TL_messageMediaPoll) getMedia(this.messageOwner)).results) != null && !pollResults.results.isEmpty()) {
            int size = tL_messageMediaPoll.results.results.size();
            for (int i = 0; i < size; i++) {
                if (tL_messageMediaPoll.results.results.get(i).chosen) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isWallpaper() {
        return (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaWebPage) && getMedia(this.messageOwner).webpage != null && "telegram_background".equals(getMedia(this.messageOwner).webpage.type);
    }

    public boolean isWallpaperAction() {
        TLRPC.Message message;
        return this.type == 22 || ((message = this.messageOwner) != null && (message.action instanceof TLRPC.TL_messageActionSetSameChatWallPaper));
    }

    public boolean isWallpaperForBoth() {
        TLRPC.Message message;
        if (isWallpaperAction() && (message = this.messageOwner) != null) {
            TLRPC.MessageAction messageAction = message.action;
            if ((messageAction instanceof TLRPC.TL_messageActionSetChatWallPaper) && ((TLRPC.TL_messageActionSetChatWallPaper) messageAction).for_both) {
                return true;
            }
        }
        return false;
    }

    public boolean isWebpage() {
        return getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaWebPage;
    }

    public boolean isWebpageDocument() {
        return (!(getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaWebPage) || getMedia(this.messageOwner).webpage.document == null || isGifDocument(getMedia(this.messageOwner).webpage.document)) ? false : true;
    }

    public boolean isYouTubeVideo() {
        return (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaWebPage) && getMedia(this.messageOwner).webpage != null && !TextUtils.isEmpty(getMedia(this.messageOwner).webpage.embed_url) && "YouTube".equals(getMedia(this.messageOwner).webpage.site_name);
    }

    public void loadAnimatedEmojiDocument() {
        if (this.emojiAnimatedSticker != null || this.emojiAnimatedStickerId == null || this.emojiAnimatedStickerLoading) {
            return;
        }
        this.emojiAnimatedStickerLoading = true;
        AnimatedEmojiDrawable.getDocumentFetcher(this.currentAccount).fetchDocument(this.emojiAnimatedStickerId.longValue(), new AnimatedEmojiDrawable.ReceivedDocument() {
            @Override
            public final void run(TLRPC.Document document) {
                MessageObject.this.lambda$loadAnimatedEmojiDocument$1(document);
            }
        });
    }

    public void markReactionsAsRead() {
        TLRPC.TL_messageReactions tL_messageReactions = this.messageOwner.reactions;
        if (tL_messageReactions == null || tL_messageReactions.recent_reactions == null) {
            return;
        }
        boolean z = false;
        for (int i = 0; i < this.messageOwner.reactions.recent_reactions.size(); i++) {
            if (this.messageOwner.reactions.recent_reactions.get(i).unread) {
                this.messageOwner.reactions.recent_reactions.get(i).unread = false;
                z = true;
            }
        }
        if (z) {
            MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
            TLRPC.Message message = this.messageOwner;
            messagesStorage.markMessageReactionsAsRead(message.dialog_id, getTopicId(this.currentAccount, message), this.messageOwner.id, true);
        }
    }

    public void measureInlineBotButtons() {
        TLRPC.TL_messageReactions tL_messageReactions;
        CharSequence replaceEmoji;
        if (this.isRestrictedMessage) {
            return;
        }
        this.wantedBotKeyboardWidth = 0;
        if (((this.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) && !hasExtendedMedia()) || ((tL_messageReactions = this.messageOwner.reactions) != null && !tL_messageReactions.results.isEmpty())) {
            Theme.createCommonMessageResources();
            StringBuilder sb = this.botButtonsLayout;
            if (sb == null) {
                this.botButtonsLayout = new StringBuilder();
            } else {
                sb.setLength(0);
            }
        }
        if (!(this.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) || hasExtendedMedia() || this.messageOwner.reply_markup.rows == null) {
            return;
        }
        for (int i = 0; i < this.messageOwner.reply_markup.rows.size(); i++) {
            TLRPC.TL_keyboardButtonRow tL_keyboardButtonRow = this.messageOwner.reply_markup.rows.get(i);
            int size = tL_keyboardButtonRow.buttons.size();
            int i2 = 0;
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC.KeyboardButton keyboardButton = tL_keyboardButtonRow.buttons.get(i3);
                StringBuilder sb2 = this.botButtonsLayout;
                sb2.append(i);
                sb2.append(i3);
                if (!(keyboardButton instanceof TLRPC.TL_keyboardButtonBuy) || (getMedia(this.messageOwner).flags & 4) == 0) {
                    String str = keyboardButton.text;
                    if (str == null) {
                        str = "";
                    }
                    replaceEmoji = Emoji.replaceEmoji(str, Theme.chat_msgBotButtonPaint.getFontMetricsInt(), false);
                } else {
                    replaceEmoji = LocaleController.getString(R.string.PaymentReceipt);
                }
                StaticLayout staticLayout = new StaticLayout(replaceEmoji, Theme.chat_msgBotButtonPaint, AndroidUtilities.dp(2000.0f), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                if (staticLayout.getLineCount() > 0) {
                    float lineWidth = staticLayout.getLineWidth(0);
                    float lineLeft = staticLayout.getLineLeft(0);
                    if (lineLeft < lineWidth) {
                        lineWidth -= lineLeft;
                    }
                    i2 = Math.max(i2, ((int) Math.ceil(lineWidth)) + AndroidUtilities.dp(4.0f));
                }
            }
            this.wantedBotKeyboardWidth = Math.max(this.wantedBotKeyboardWidth, ((i2 + AndroidUtilities.dp(12.0f)) * size) + (AndroidUtilities.dp(5.0f) * (size - 1)));
        }
    }

    public float measureVoiceTranscriptionHeight() {
        StaticLayout staticLayout;
        StaticLayout.Builder obtain;
        StaticLayout.Builder breakStrategy;
        StaticLayout.Builder hyphenationFrequency;
        StaticLayout.Builder alignment;
        CharSequence voiceTranscription = getVoiceTranscription();
        if (voiceTranscription == null) {
            return 0.0f;
        }
        int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(needDrawAvatar() ? 147.0f : 95.0f);
        if (Build.VERSION.SDK_INT >= 24) {
            obtain = StaticLayout.Builder.obtain(voiceTranscription, 0, voiceTranscription.length(), Theme.chat_msgTextPaint, dp);
            breakStrategy = obtain.setBreakStrategy(1);
            hyphenationFrequency = breakStrategy.setHyphenationFrequency(0);
            alignment = hyphenationFrequency.setAlignment(Layout.Alignment.ALIGN_NORMAL);
            staticLayout = alignment.build();
        } else {
            staticLayout = new StaticLayout(voiceTranscription, Theme.chat_msgTextPaint, dp, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }
        return staticLayout.getHeight();
    }

    public boolean mediaExists() {
        VideoPlayer.VideoUri videoUri;
        return (!hasVideoQualities() || (videoUri = this.highestQuality) == null) ? this.mediaExists : videoUri.isCached();
    }

    public boolean needDrawAvatar() {
        TLRPC.MessageFwdHeader messageFwdHeader;
        TLRPC.Chat chat;
        if (this.type == 27) {
            return false;
        }
        if (this.isRepostPreview || this.isSaved || this.forceAvatar || this.customAvatarDrawable != null || this.searchType != 0) {
            return true;
        }
        boolean z = getDialogId() >= 0 ? getDialogId() == 489000 : !((chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-getDialogId()))) == null || !chat.signature_profiles);
        if (isSponsored()) {
            return false;
        }
        return isFromUser() || isFromGroup() || z || this.eventId != 0 || !((messageFwdHeader = this.messageOwner.fwd_from) == null || messageFwdHeader.saved_from_peer == null);
    }

    public boolean needDrawBluredPreview() {
        if (this.isRepostPreview) {
            return false;
        }
        if (hasExtendedMediaPreview()) {
            return true;
        }
        TLRPC.Message message = this.messageOwner;
        if (message instanceof TLRPC.TL_message_secret) {
            int max = Math.max(message.ttl, getMedia(message).ttl_seconds);
            if (max > 0) {
                return (((getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaPhoto) || isVideo() || isGif()) && max <= 60) || isRoundVideo();
            }
            return false;
        }
        if (!(message instanceof TLRPC.TL_message) || getMedia(message) == null || getMedia(this.messageOwner).ttl_seconds == 0) {
            return false;
        }
        return (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaPhoto) || (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaDocument);
    }

    public boolean needDrawForwarded() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.needDrawForwarded():boolean");
    }

    public boolean needDrawShareButton() {
        int i;
        TLRPC.Message message;
        TLRPC.MessageFwdHeader messageFwdHeader;
        if (this.isRepostPreview || getDialogId() == 489000) {
            return false;
        }
        if (this.isSaved) {
            long j = UserConfig.getInstance(this.currentAccount).clientUserId;
            long savedDialogId = getSavedDialogId(j, this.messageOwner);
            if (savedDialogId == j || savedDialogId == 2666000 || (message = this.messageOwner) == null || (messageFwdHeader = message.fwd_from) == null) {
                return false;
            }
            return (messageFwdHeader.from_id == null && messageFwdHeader.saved_from_id == null) ? false : true;
        }
        if (this.type == 27 || isSponsored() || this.hasCode || this.preview || this.scheduled || this.eventId != 0) {
            return false;
        }
        if (this.searchType == 2) {
            return true;
        }
        TLRPC.Message message2 = this.messageOwner;
        if (message2.noforwards) {
            return false;
        }
        if (message2.fwd_from != null && !isOutOwner() && this.messageOwner.fwd_from.saved_from_peer != null && getDialogId() == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            return true;
        }
        int i2 = this.type;
        if (i2 != 13 && i2 != 15 && i2 != 19) {
            TLRPC.MessageFwdHeader messageFwdHeader2 = this.messageOwner.fwd_from;
            if (messageFwdHeader2 != null && (messageFwdHeader2.from_id instanceof TLRPC.TL_peerChannel) && !isOutOwner()) {
                return true;
            }
            if (isFromUser()) {
                TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.messageOwner.from_id.user_id));
                if (user != null && user.bot && ("reviews_bot".equals(UserObject.getPublicUsername(user)) || "ReviewInsightsBot".equals(UserObject.getPublicUsername(user)))) {
                    return true;
                }
                if ((getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaEmpty) || getMedia(this.messageOwner) == null || ((getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaWebPage) && !(getMedia(this.messageOwner).webpage instanceof TLRPC.TL_webPage))) {
                    return false;
                }
                if (user != null && user.bot && !hasExtendedMedia()) {
                    return true;
                }
                if (!isOut()) {
                    if ((getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaGame) || (((getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaInvoice) && !hasExtendedMedia()) || (getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaWebPage))) {
                        return true;
                    }
                    TLRPC.Peer peer = this.messageOwner.peer_id;
                    TLRPC.Chat chat = null;
                    if (peer != null) {
                        long j2 = peer.channel_id;
                        if (j2 != 0) {
                            chat = getChat(null, null, j2);
                        }
                    }
                    return ChatObject.isChannel(chat) && chat.megagroup && ChatObject.isPublic(chat) && !(getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaContact) && !(getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaGeo);
                }
            } else {
                TLRPC.Message message3 = this.messageOwner;
                if ((message3.from_id instanceof TLRPC.TL_peerChannel) || message3.post) {
                    if ((getMedia(message3) instanceof TLRPC.TL_messageMediaWebPage) && !isOutOwner()) {
                        return true;
                    }
                    if (isSupergroup()) {
                        return false;
                    }
                    TLRPC.Message message4 = this.messageOwner;
                    if (message4.peer_id.channel_id != 0 && ((message4.via_bot_id == 0 && message4.reply_to == null) || ((i = this.type) != 13 && i != 15))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean probablyRingtone() {
        if (!isVoiceOnce() && getDocument() != null && RingtoneDataStore.ringtoneSupportedMimeType.contains(getDocument().mime_type) && getDocument().size < MessagesController.getInstance(this.currentAccount).ringtoneSizeMax * 2) {
            for (int i = 0; i < getDocument().attributes.size(); i++) {
                TLRPC.DocumentAttribute documentAttribute = getDocument().attributes.get(i);
                if ((documentAttribute instanceof TLRPC.TL_documentAttributeAudio) && documentAttribute.duration < 5.0d) {
                    return true;
                }
            }
        }
        return false;
    }

    public Spannable replaceAnimatedEmoji(CharSequence charSequence, Paint.FontMetricsInt fontMetricsInt) {
        TLRPC.TL_textWithEntities tL_textWithEntities;
        return replaceAnimatedEmoji(charSequence, (!this.translated || (tL_textWithEntities = this.messageOwner.translatedText) == null) ? this.messageOwner.entities : tL_textWithEntities.entities, fontMetricsInt, false);
    }

    public void replaceEmojiToLottieFrame(CharSequence charSequence, int[] iArr) {
        boolean z;
        if (charSequence instanceof Spannable) {
            Spannable spannable = (Spannable) charSequence;
            Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) spannable.getSpans(0, spannable.length(), Emoji.EmojiSpan.class);
            AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spannable.getSpans(0, spannable.length(), AnimatedEmojiSpan.class);
            if (emojiSpanArr != null) {
                if (((iArr == null ? 0 : iArr[0]) - emojiSpanArr.length) - (animatedEmojiSpanArr == null ? 0 : animatedEmojiSpanArr.length) > 0) {
                    return;
                }
                for (int i = 0; i < emojiSpanArr.length; i++) {
                    CharSequence charSequence2 = emojiSpanArr[i].emoji;
                    if (Emoji.endsWithRightArrow(charSequence2)) {
                        charSequence2 = charSequence2.subSequence(0, charSequence2.length() - 2);
                        z = true;
                    } else {
                        z = false;
                    }
                    TLRPC.Document emojiAnimatedSticker = MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(charSequence2);
                    if (emojiAnimatedSticker != null) {
                        int spanStart = spannable.getSpanStart(emojiSpanArr[i]);
                        int spanEnd = spannable.getSpanEnd(emojiSpanArr[i]);
                        spannable.removeSpan(emojiSpanArr[i]);
                        AnimatedEmojiSpan animatedEmojiSpan = new AnimatedEmojiSpan(emojiAnimatedSticker, emojiSpanArr[i].fontMetrics);
                        animatedEmojiSpan.standard = true;
                        animatedEmojiSpan.invert = z;
                        spannable.setSpan(animatedEmojiSpan, spanStart, spanEnd, 33);
                    }
                }
            }
        }
    }

    public java.lang.CharSequence replaceWithLink(java.lang.CharSequence r16, java.lang.String r17, java.util.ArrayList<java.lang.Long> r18, java.util.AbstractMap<java.lang.Long, org.telegram.tgnet.TLRPC.User> r19, androidx.collection.LongSparseArray r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.replaceWithLink(java.lang.CharSequence, java.lang.String, java.util.ArrayList, java.util.AbstractMap, androidx.collection.LongSparseArray):java.lang.CharSequence");
    }

    public void resetLayout() {
        this.layoutCreated = false;
    }

    public void resetPlayingProgress() {
        this.audioProgress = 0.0f;
        this.audioProgressSec = 0;
        this.bufferedProgress = 0.0f;
    }

    public boolean selectReaction(ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean z, boolean z2) {
        MessagesController messagesController;
        long dialogId;
        int i;
        TLRPC.TL_messageReactions tL_messageReactions;
        TLRPC.Message message = this.messageOwner;
        if (message.reactions == null) {
            message.reactions = new TLRPC.TL_messageReactions();
            TLRPC.Message message2 = this.messageOwner;
            message2.reactions.reactions_as_tags = getDialogId(message2) == UserConfig.getInstance(this.currentAccount).getClientUserId();
            this.messageOwner.reactions.can_see_list = isFromGroup() || isFromUser();
        }
        ArrayList arrayList = new ArrayList();
        TLRPC.ReactionCount reactionCount = null;
        int i2 = 0;
        for (int i3 = 0; i3 < this.messageOwner.reactions.results.size(); i3++) {
            TLRPC.ReactionCount reactionCount2 = this.messageOwner.reactions.results.get(i3);
            if (reactionCount2.chosen && !(reactionCount2.reaction instanceof TLRPC.TL_reactionPaid)) {
                arrayList.add(reactionCount2);
                int i4 = reactionCount2.chosen_order;
                if (i4 > i2) {
                    i2 = i4;
                }
            }
            TLRPC.Reaction reaction = this.messageOwner.reactions.results.get(i3).reaction;
            if (reaction instanceof TLRPC.TL_reactionEmoji) {
                String str = visibleReaction.emojicon;
                if (str != null) {
                    if (((TLRPC.TL_reactionEmoji) reaction).emoticon.equals(str)) {
                        reactionCount = this.messageOwner.reactions.results.get(i3);
                    }
                }
            }
            if (reaction instanceof TLRPC.TL_reactionCustomEmoji) {
                long j = visibleReaction.documentId;
                if (j != 0 && ((TLRPC.TL_reactionCustomEmoji) reaction).document_id == j) {
                    reactionCount = this.messageOwner.reactions.results.get(i3);
                }
            }
        }
        if (!arrayList.isEmpty() && arrayList.contains(reactionCount) && z) {
            return true;
        }
        int maxUserReactionsCount = MessagesController.getInstance(this.currentAccount).getMaxUserReactionsCount();
        if (!arrayList.isEmpty() && arrayList.contains(reactionCount)) {
            if (reactionCount != null) {
                reactionCount.chosen = false;
                int i5 = reactionCount.count - 1;
                reactionCount.count = i5;
                if (i5 <= 0) {
                    this.messageOwner.reactions.results.remove(reactionCount);
                }
            }
            if (this.messageOwner.reactions.can_see_list) {
                int i6 = 0;
                while (i6 < this.messageOwner.reactions.recent_reactions.size()) {
                    if (getPeerId(this.messageOwner.reactions.recent_reactions.get(i6).peer_id) == UserConfig.getInstance(this.currentAccount).getClientUserId() && ReactionsUtils.compare(this.messageOwner.reactions.recent_reactions.get(i6).reaction, visibleReaction)) {
                        this.messageOwner.reactions.recent_reactions.remove(i6);
                        i6--;
                    }
                    i6++;
                }
            }
            this.reactionsChanged = true;
            return false;
        }
        while (!arrayList.isEmpty() && arrayList.size() >= maxUserReactionsCount) {
            int i7 = 0;
            for (int i8 = 1; i8 < arrayList.size(); i8++) {
                if (!(((TLRPC.ReactionCount) arrayList.get(i8)).reaction instanceof TLRPC.TL_reactionPaid) && ((TLRPC.ReactionCount) arrayList.get(i8)).chosen_order < ((TLRPC.ReactionCount) arrayList.get(i7)).chosen_order) {
                    i7 = i8;
                }
            }
            TLRPC.ReactionCount reactionCount3 = (TLRPC.ReactionCount) arrayList.get(i7);
            reactionCount3.chosen = false;
            int i9 = reactionCount3.count - 1;
            reactionCount3.count = i9;
            if (i9 <= 0) {
                this.messageOwner.reactions.results.remove(reactionCount3);
            }
            arrayList.remove(reactionCount3);
            if (this.messageOwner.reactions.can_see_list) {
                int i10 = 0;
                while (i10 < this.messageOwner.reactions.recent_reactions.size()) {
                    if (getPeerId(this.messageOwner.reactions.recent_reactions.get(i10).peer_id) == UserConfig.getInstance(this.currentAccount).getClientUserId() && ReactionsUtils.compare(this.messageOwner.reactions.recent_reactions.get(i10).reaction, visibleReaction)) {
                        this.messageOwner.reactions.recent_reactions.remove(i10);
                        i10--;
                    }
                    i10++;
                }
            }
        }
        if (reactionCount == null) {
            int chatMaxUniqReactions = MessagesController.getInstance(this.currentAccount).getChatMaxUniqReactions(getDialogId());
            TLRPC.Message message3 = this.messageOwner;
            if (message3 == null || (tL_messageReactions = message3.reactions) == null) {
                i = 0;
            } else {
                Iterator<TLRPC.ReactionCount> it = tL_messageReactions.results.iterator();
                i = 0;
                while (it.hasNext()) {
                    if (!(it.next().reaction instanceof TLRPC.TL_reactionPaid)) {
                        i++;
                    }
                }
            }
            if (i + 1 > chatMaxUniqReactions) {
                return false;
            }
            reactionCount = new TLRPC.TL_reactionCount();
            reactionCount.reaction = visibleReaction.toTLReaction();
            this.messageOwner.reactions.results.add(reactionCount);
        }
        reactionCount.chosen = true;
        reactionCount.count++;
        reactionCount.chosen_order = i2 + 1;
        TLRPC.Message message4 = this.messageOwner;
        if (message4.reactions.can_see_list || (message4.dialog_id > 0 && maxUserReactionsCount > 1)) {
            TLRPC.TL_messagePeerReaction tL_messagePeerReaction = new TLRPC.TL_messagePeerReaction();
            TLRPC.Message message5 = this.messageOwner;
            if (!message5.isThreadMessage || message5.fwd_from == null) {
                messagesController = MessagesController.getInstance(this.currentAccount);
                dialogId = getDialogId();
            } else {
                messagesController = MessagesController.getInstance(this.currentAccount);
                dialogId = getFromChatId();
            }
            tL_messagePeerReaction.peer_id = messagesController.getSendAsSelectedPeer(dialogId);
            this.messageOwner.reactions.recent_reactions.add(0, tL_messagePeerReaction);
            if (visibleReaction.emojicon != null) {
                TLRPC.TL_reactionEmoji tL_reactionEmoji = new TLRPC.TL_reactionEmoji();
                tL_messagePeerReaction.reaction = tL_reactionEmoji;
                tL_reactionEmoji.emoticon = visibleReaction.emojicon;
            } else {
                TLRPC.TL_reactionCustomEmoji tL_reactionCustomEmoji = new TLRPC.TL_reactionCustomEmoji();
                tL_messagePeerReaction.reaction = tL_reactionCustomEmoji;
                tL_reactionCustomEmoji.document_id = visibleReaction.documentId;
            }
        }
        this.reactionsChanged = true;
        return true;
    }

    public void setContentIsRead() {
        this.messageOwner.media_unread = false;
    }

    public void setIsRead() {
        this.messageOwner.unread = false;
    }

    public void setMyPaidReactionAnonymous(boolean z) {
        TLRPC.TL_messageReactions tL_messageReactions;
        ArrayList<TLRPC.MessageReactor> arrayList;
        TLRPC.Message message = this.messageOwner;
        if (message == null || (tL_messageReactions = message.reactions) == null || (arrayList = tL_messageReactions.top_reactors) == null) {
            return;
        }
        Iterator<TLRPC.MessageReactor> it = arrayList.iterator();
        while (it.hasNext()) {
            TLRPC.MessageReactor next = it.next();
            if (next != null && next.my) {
                next.anonymous = z;
            }
        }
    }

    public void setQuery(String str) {
        setQuery(str, true);
    }

    public void setQuery(String str, boolean z) {
        String str2;
        int indexOf;
        if (TextUtils.isEmpty(str)) {
            this.highlightedWords = null;
            this.messageTrimmedToHighlight = null;
            this.messageTrimmedToHighlightCut = true;
            return;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        String lowerCase = str.trim().toLowerCase();
        String[] split = lowerCase.split("[^\\p{L}#$]+");
        ArrayList arrayList2 = new ArrayList();
        TLRPC.MessageReplyHeader messageReplyHeader = this.messageOwner.reply_to;
        if (messageReplyHeader != null && !TextUtils.isEmpty(messageReplyHeader.quote_text)) {
            String lowerCase2 = this.messageOwner.reply_to.quote_text.trim().toLowerCase();
            if (lowerCase2.contains(lowerCase) && !arrayList.contains(lowerCase)) {
                arrayList.add(lowerCase);
                handleFoundWords(arrayList, split, true, z);
                return;
            }
            arrayList2.addAll(Arrays.asList(lowerCase2.split("[^\\p{L}#$]+")));
        }
        if (!TextUtils.isEmpty(this.messageOwner.message)) {
            String lowerCase3 = this.messageOwner.message.trim().toLowerCase();
            if (lowerCase3.contains(lowerCase) && !arrayList.contains(lowerCase)) {
                arrayList.add(lowerCase);
                handleFoundWords(arrayList, split, false, z);
                return;
            }
            arrayList2.addAll(Arrays.asList(lowerCase3.split("[^\\p{L}#$]+")));
        }
        if (getDocument() != null) {
            String lowerCase4 = FileLoader.getDocumentFileName(getDocument()).toLowerCase();
            if (lowerCase4.contains(lowerCase) && !arrayList.contains(lowerCase)) {
                arrayList.add(lowerCase);
            }
            arrayList2.addAll(Arrays.asList(lowerCase4.split("[^\\p{L}#$]+")));
        }
        if ((getMedia(this.messageOwner) instanceof TLRPC.TL_messageMediaWebPage) && (getMedia(this.messageOwner).webpage instanceof TLRPC.TL_webPage)) {
            TLRPC.WebPage webPage = getMedia(this.messageOwner).webpage;
            String str3 = webPage.title;
            if (str3 == null) {
                str3 = webPage.site_name;
            }
            if (str3 != null) {
                String lowerCase5 = str3.toLowerCase();
                if (lowerCase5.contains(lowerCase) && !arrayList.contains(lowerCase)) {
                    arrayList.add(lowerCase);
                }
                arrayList2.addAll(Arrays.asList(lowerCase5.split("[^\\p{L}#$]+")));
            }
        }
        String musicAuthor = getMusicAuthor();
        if (musicAuthor != null) {
            String lowerCase6 = musicAuthor.toLowerCase();
            if (lowerCase6.contains(lowerCase) && !arrayList.contains(lowerCase)) {
                arrayList.add(lowerCase);
            }
            arrayList2.addAll(Arrays.asList(lowerCase6.split("[^\\p{L}#$]+")));
        }
        for (String str4 : split) {
            if (str4.length() >= 2) {
                for (int i = 0; i < arrayList2.size(); i++) {
                    if (!arrayList.contains(arrayList2.get(i)) && (indexOf = (str2 = (String) arrayList2.get(i)).indexOf(str4.charAt(0))) >= 0) {
                        int max = Math.max(str4.length(), str2.length());
                        if (indexOf != 0) {
                            str2 = str2.substring(indexOf);
                        }
                        int min = Math.min(str4.length(), str2.length());
                        int i2 = 0;
                        for (int i3 = 0; i3 < min && str2.charAt(i3) == str4.charAt(i3); i3++) {
                            i2++;
                        }
                        if (i2 / max >= 0.5d) {
                            arrayList.add((String) arrayList2.get(i));
                        }
                    }
                }
            }
        }
        handleFoundWords(arrayList, split, false, z);
    }

    public void setType() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessageObject.setType():void");
    }

    public boolean shouldAnimateSending() {
        return this.wasJustSent && (this.type == 5 || isVoice() || ((isAnyKindOfSticker() && this.sendAnimationData != null) || !(this.messageText == null || this.sendAnimationData == null)));
    }

    public boolean shouldDrawReactions() {
        return !this.isRepostPreview;
    }

    public boolean shouldDrawReactionsInLayout() {
        return true;
    }

    public boolean shouldDrawWithoutBackground() {
        int i;
        return !isSponsored() && ((i = this.type) == 13 || i == 15 || i == 5 || i == 19 || isExpiredStory());
    }

    public boolean shouldEncryptPhotoOrVideo() {
        return shouldEncryptPhotoOrVideo(this.currentAccount, this.messageOwner);
    }

    public void spoilLoginCode() {
        TLRPC.Message message;
        if (this.spoiledLoginCode || this.messageText == null || (message = this.messageOwner) == null || message.entities == null) {
            return;
        }
        TLRPC.Peer peer = message.from_id;
        if (peer instanceof TLRPC.TL_peerUser) {
            long j = peer.user_id;
            if (j == 777000 || j == 489000) {
                if (loginCodePattern == null) {
                    loginCodePattern = Pattern.compile("[\\d\\-]{5,8}");
                }
                try {
                    Matcher matcher = loginCodePattern.matcher(this.messageText);
                    if (matcher.find()) {
                        TLRPC.TL_messageEntitySpoiler tL_messageEntitySpoiler = new TLRPC.TL_messageEntitySpoiler();
                        tL_messageEntitySpoiler.offset = matcher.start();
                        tL_messageEntitySpoiler.length = matcher.end() - tL_messageEntitySpoiler.offset;
                        this.messageOwner.entities.add(tL_messageEntitySpoiler);
                    }
                } catch (Exception e) {
                    FileLog.e((Throwable) e, false);
                }
                this.spoiledLoginCode = true;
            }
        }
    }

    public int textHeight() {
        if (this.textLayoutBlocks == null) {
            return 0;
        }
        int i = 0;
        for (int i2 = 0; i2 < this.textLayoutBlocks.size(); i2++) {
            i += this.textLayoutBlocks.get(i2).padTop + this.textLayoutBlocks.get(i2).height() + this.textLayoutBlocks.get(i2).padBottom;
        }
        return i;
    }

    public int textHeight(ChatMessageCell.TransitionParams transitionParams) {
        if (this.textLayoutBlocks == null) {
            return 0;
        }
        int i = 0;
        for (int i2 = 0; i2 < this.textLayoutBlocks.size(); i2++) {
            i += this.textLayoutBlocks.get(i2).padTop + this.textLayoutBlocks.get(i2).height(transitionParams) + this.textLayoutBlocks.get(i2).padBottom;
        }
        return i;
    }

    public void toggleChannelRecommendations() {
        expandChannelRecommendations(!this.channelJoinedExpanded);
    }

    public void updateMessageText() {
        updateMessageText(MessagesController.getInstance(this.currentAccount).getUsers(), MessagesController.getInstance(this.currentAccount).getChats(), null, null);
    }

    public void updateQualitiesCached(boolean z) {
        VideoPlayer.VideoUri cachedQuality;
        ArrayList<VideoPlayer.Quality> arrayList = this.videoQualities;
        if (arrayList == null) {
            cachedQuality = null;
        } else {
            Iterator<VideoPlayer.Quality> it = arrayList.iterator();
            while (it.hasNext()) {
                Iterator it2 = it.next().uris.iterator();
                while (it2.hasNext()) {
                    ((VideoPlayer.VideoUri) it2.next()).updateCached(z);
                }
            }
            this.highestQuality = VideoPlayer.getQualityForPlayer(this.videoQualities);
            this.thumbQuality = VideoPlayer.getQualityForThumb(this.videoQualities);
            cachedQuality = VideoPlayer.getCachedQuality(this.videoQualities);
        }
        this.cachedQuality = cachedQuality;
    }

    public boolean updateTranslation() {
        return updateTranslation(false);
    }

    public boolean updateTranslation(boolean z) {
        String str;
        TLRPC.Message message;
        MessageObject messageObject = this.replyMessageObject;
        boolean z2 = (messageObject == null || messageObject == this || !messageObject.updateTranslation(z)) ? false : true;
        TranslateController translateController = MessagesController.getInstance(this.currentAccount).getTranslateController();
        if (!TranslateController.isTranslatable(this) || !translateController.isTranslatingDialog(getDialogId()) || translateController.isTranslateDialogHidden(getDialogId()) || (message = this.messageOwner) == null || message.translatedText == null || !TextUtils.equals(translateController.getDialogTranslateTo(getDialogId()), this.messageOwner.translatedToLanguage)) {
            TLRPC.Message message2 = this.messageOwner;
            if (message2 == null || !(z || this.translated)) {
                return z2;
            }
            this.translated = false;
            str = message2.message;
        } else {
            if (this.translated) {
                return z2;
            }
            this.translated = true;
            str = this.messageOwner.translatedText.text;
        }
        applyNewText(str);
        generateCaption();
        return true;
    }
}
