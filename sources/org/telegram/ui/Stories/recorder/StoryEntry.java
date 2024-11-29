package org.telegram.ui.Stories.recorder;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Build;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.TextUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FileRefController;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet;

public class StoryEntry {
    public boolean allowScreenshots;
    public String audioAuthor;
    public long audioDuration;
    public float audioLeft;
    public long audioOffset;
    public String audioPath;
    public String audioTitle;
    public Drawable backgroundDrawable;
    public File backgroundFile;
    public String backgroundWallpaperEmoticon;
    public Bitmap blurredVideoThumb;
    public long botId;
    public CharSequence caption;
    public CollageLayout collage;
    public ArrayList collageContent;
    public Bitmap coverBitmap;
    public boolean coverSet;
    public long draftDate;
    public long draftId;
    public File draftThumbFile;
    public long duration;
    public long editDocumentId;
    public long editExpireDate;
    public long editPhotoId;
    public List editStickers;
    public int editStoryId;
    public long editStoryPeerId;
    public boolean editedCaption;
    public boolean editedMedia;
    public ArrayList editedMediaAreas;
    public boolean editedPrivacy;
    public TLRPC.InputMedia editingBotPreview;
    public TLRPC.Document editingCoverDocument;
    public TLRPC.TL_error error;
    public File file;
    public boolean fileDeletable;
    public File filterFile;
    public MediaController.SavedFilterState filterState;
    private boolean fromCamera;
    public int gradientBottomColor;
    public int gradientTopColor;
    public HDRInfo hdrInfo;
    public int height;
    public int invert;
    public boolean isDraft;
    public boolean isEdit;
    public boolean isEditSaved;
    public boolean isEditingCover;
    public boolean isError;
    public boolean isRepost;
    public boolean isRepostMessage;
    public boolean isVideo;
    public float left;
    public ArrayList mediaEntities;
    public File messageFile;
    public ArrayList messageObjects;
    public File messageVideoMaskFile;
    public boolean muted;
    public int orientation;
    public File paintBlurFile;
    public File paintEntitiesFile;
    public File paintFile;
    public TLRPC.InputPeer peer;
    public StoryPrivacyBottomSheet.StoryPrivacy privacy;
    public String repostCaption;
    public TLRPC.MessageMedia repostMedia;
    public TLRPC.Peer repostPeer;
    public CharSequence repostPeerName;
    public int repostStoryId;
    public File round;
    public long roundDuration;
    public float roundLeft;
    public long roundOffset;
    public String roundThumb;
    public int scheduleDate;
    public ArrayList shareUserIds;
    public boolean silent;
    public List stickers;
    public Bitmap thumbBitmap;
    public String thumbPath;
    public Bitmap thumbPathBitmap;
    public Utilities.Callback updateDocumentRef;
    public File uploadThumbFile;
    public long videoOffset;
    public int width;
    public final int currentAccount = UserConfig.selectedAccount;
    public double fileDuration = -1.0d;
    public float audioRight = 1.0f;
    public float audioVolume = 1.0f;
    public float videoVolume = 1.0f;
    public boolean videoLoop = false;
    public float videoLeft = 0.0f;
    public float videoRight = 1.0f;
    public float right = 1.0f;
    public long cover = -1;
    public int resultWidth = 720;
    public int resultHeight = 1280;
    public final Matrix matrix = new Matrix();
    public float roundRight = 1.0f;
    public float roundVolume = 1.0f;
    public boolean isDark = Theme.isCurrentThemeDark();
    public long backgroundWallpaperPeerId = Long.MIN_VALUE;
    public boolean captionEntitiesAllowed = true;
    public final ArrayList privacyRules = new ArrayList();
    public boolean pinned = true;
    public int period = 86400;
    public String botLang = "";
    public long averageDuration = 5000;
    private int checkStickersReqId = 0;

    public interface DecodeBitmap {
        Bitmap decode(BitmapFactory.Options options);
    }

    public static class HDRInfo {
        public int colorRange;
        public int colorStandard;
        public int colorTransfer;
        public float maxlum;
        public float minlum;

        public int getHDRType() {
            if (this.colorStandard != 6) {
                return 0;
            }
            int i = this.colorTransfer;
            if (i == 7) {
                return 1;
            }
            return i == 6 ? 2 : 0;
        }
    }

    public static StoryEntry asCollage(CollageLayout collageLayout, ArrayList arrayList) {
        int i;
        int i2;
        StoryEntry storyEntry = new StoryEntry();
        storyEntry.collage = collageLayout;
        storyEntry.collageContent = arrayList;
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            StoryEntry storyEntry2 = (StoryEntry) it.next();
            if (storyEntry2.isVideo) {
                storyEntry.isVideo = true;
                storyEntry2.videoLeft = 0.0f;
                storyEntry2.videoRight = Math.min(1.0f, 59000.0f / ((float) storyEntry2.duration));
            }
        }
        if (storyEntry.isVideo) {
            i = 720;
            storyEntry.width = 720;
            i2 = 1280;
        } else {
            i = 1080;
            storyEntry.width = 1080;
            i2 = 1920;
        }
        storyEntry.height = i2;
        storyEntry.resultWidth = i;
        storyEntry.resultHeight = i2;
        storyEntry.setupMatrix();
        return storyEntry;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int i, int i2) {
        double min = (options.outHeight > i2 || options.outWidth > i) ? Math.min((int) Math.ceil(r0 / i2), (int) Math.ceil(r6 / i)) : 1;
        return Math.max(1, (int) Math.pow(min, Math.floor(Math.log(min) / Math.log(2.0d))));
    }

    public static boolean canRepostMessage(MessageObject messageObject) {
        TLRPC.Message message;
        int i;
        TLRPC.Peer peer;
        if (messageObject != null && !messageObject.isSponsored() && (((message = messageObject.messageOwner) == null || !message.noforwards) && (i = messageObject.type) != 17 && i != 12)) {
            long dialogId = messageObject.getDialogId();
            TLRPC.Chat chat = MessagesController.getInstance(messageObject.currentAccount).getChat(Long.valueOf(-dialogId));
            if (chat != null && chat.noforwards) {
                return false;
            }
            if (dialogId < 0 && ChatObject.isChannelAndNotMegaGroup(chat)) {
                return true;
            }
            TLRPC.MessageFwdHeader messageFwdHeader = messageObject.messageOwner.fwd_from;
            if (messageFwdHeader != null && (peer = messageFwdHeader.from_id) != null && (messageFwdHeader.flags & 4) != 0) {
                long peerDialogId = DialogObject.getPeerDialogId(peer);
                TLRPC.Chat chat2 = MessagesController.getInstance(messageObject.currentAccount).getChat(Long.valueOf(-peerDialogId));
                if (peerDialogId < 0 && ((chat2 == null || !chat2.noforwards) && ChatObject.isChannelAndNotMegaGroup(chat2) && ChatObject.isPublic(chat2))) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void drawBackgroundDrawable(Canvas canvas, Drawable drawable, int i, int i2) {
        if (drawable == null) {
            return;
        }
        Rect rect = new Rect(drawable.getBounds());
        Drawable.Callback callback = drawable.getCallback();
        drawable.setCallback(null);
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            float width = bitmapDrawable.getBitmap().getWidth();
            float height = bitmapDrawable.getBitmap().getHeight();
            float max = Math.max(i / width, i2 / height);
            drawable.setBounds(0, 0, (int) (width * max), (int) (height * max));
        } else {
            drawable.setBounds(0, 0, i, i2);
        }
        drawable.draw(canvas);
        drawable.setBounds(rect);
        drawable.setCallback(callback);
    }

    private String ext(File file) {
        String path;
        int lastIndexOf;
        if (file != null && (lastIndexOf = (path = file.getPath()).lastIndexOf(46)) > 0) {
            return path.substring(lastIndexOf + 1);
        }
        return null;
    }

    public static StoryEntry fromPhotoEntry(MediaController.PhotoEntry photoEntry) {
        int i;
        StoryEntry storyEntry = new StoryEntry();
        storyEntry.file = new File(photoEntry.path);
        storyEntry.orientation = photoEntry.orientation;
        storyEntry.invert = photoEntry.invert;
        storyEntry.isVideo = photoEntry.isVideo;
        storyEntry.thumbPath = photoEntry.thumbPath;
        long j = photoEntry.duration * 1000;
        storyEntry.duration = j;
        storyEntry.left = 0.0f;
        storyEntry.right = Math.min(1.0f, 59500.0f / ((float) j));
        if (storyEntry.isVideo && storyEntry.thumbPath == null) {
            storyEntry.thumbPath = "vthumb://" + photoEntry.imageId;
        }
        storyEntry.gradientTopColor = photoEntry.gradientTopColor;
        storyEntry.gradientBottomColor = photoEntry.gradientBottomColor;
        storyEntry.decodeBounds(storyEntry.file.getAbsolutePath());
        int i2 = photoEntry.width;
        if (i2 > 0 && (i = photoEntry.height) > 0) {
            storyEntry.width = i2;
            storyEntry.height = i;
        }
        storyEntry.setupMatrix();
        return storyEntry;
    }

    public static StoryEntry fromPhotoShoot(File file, int i) {
        StoryEntry storyEntry = new StoryEntry();
        storyEntry.file = file;
        storyEntry.fileDeletable = true;
        storyEntry.orientation = i;
        storyEntry.invert = 0;
        storyEntry.isVideo = false;
        if (file != null) {
            storyEntry.decodeBounds(file.getAbsolutePath());
        }
        storyEntry.setupMatrix();
        return storyEntry;
    }

    public static StoryEntry fromStoryItem(File file, TL_stories.StoryItem storyItem) {
        StoryEntry storyEntry = new StoryEntry();
        storyEntry.isEdit = true;
        storyEntry.editStoryId = storyItem.id;
        storyEntry.file = file;
        int i = 0;
        storyEntry.fileDeletable = false;
        storyEntry.width = 720;
        storyEntry.height = 1280;
        TLRPC.MessageMedia messageMedia = storyItem.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaPhoto) {
            storyEntry.isVideo = false;
            if (file != null) {
                storyEntry.decodeBounds(file.getAbsolutePath());
            }
        } else if (messageMedia instanceof TLRPC.TL_messageMediaDocument) {
            storyEntry.isVideo = true;
            TLRPC.Document document = messageMedia.document;
            if (document != null && document.attributes != null) {
                int i2 = 0;
                while (true) {
                    if (i2 >= storyItem.media.document.attributes.size()) {
                        break;
                    }
                    TLRPC.DocumentAttribute documentAttribute = storyItem.media.document.attributes.get(i2);
                    if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                        storyEntry.width = documentAttribute.w;
                        storyEntry.height = documentAttribute.h;
                        storyEntry.fileDuration = documentAttribute.duration;
                        break;
                    }
                    i2++;
                }
            }
            TLRPC.Document document2 = storyItem.media.document;
            if (document2 != null) {
                String str = storyItem.firstFramePath;
                if (str != null) {
                    storyEntry.thumbPath = str;
                } else if (document2.thumbs != null) {
                    while (true) {
                        if (i >= storyItem.media.document.thumbs.size()) {
                            break;
                        }
                        TLRPC.PhotoSize photoSize = storyItem.media.document.thumbs.get(i);
                        if (!(photoSize instanceof TLRPC.TL_photoStrippedSize)) {
                            File pathToAttach = FileLoader.getInstance(storyEntry.currentAccount).getPathToAttach(photoSize, true);
                            if (pathToAttach != null && pathToAttach.exists()) {
                                storyEntry.thumbPath = pathToAttach.getAbsolutePath();
                                break;
                            }
                            i++;
                        } else {
                            storyEntry.thumbPathBitmap = ImageLoader.getStrippedPhotoBitmap(photoSize.bytes, null);
                            break;
                        }
                    }
                }
            }
        }
        storyEntry.privacyRules.clear();
        storyEntry.privacyRules.addAll(StoryPrivacyBottomSheet.StoryPrivacy.toInput(storyEntry.currentAccount, storyItem.privacy));
        storyEntry.period = storyItem.expire_date - storyItem.date;
        try {
            CharSequence replaceEmoji = Emoji.replaceEmoji(new SpannableString(storyItem.caption), Theme.chat_msgTextPaint.getFontMetricsInt(), true);
            MessageObject.addEntitiesToText(replaceEmoji, storyItem.entities, true, false, true, false);
            storyEntry.caption = MessageObject.replaceAnimatedEmoji(replaceEmoji, storyItem.entities, Theme.chat_msgTextPaint.getFontMetricsInt());
        } catch (Exception unused) {
        }
        storyEntry.setupMatrix();
        storyEntry.checkStickers(storyItem);
        storyEntry.editedMediaAreas = storyItem.media_areas;
        storyEntry.peer = MessagesController.getInstance(storyEntry.currentAccount).getInputPeer(storyItem.dialogId);
        return storyEntry;
    }

    public static StoryEntry fromVideoShoot(File file, String str, long j) {
        StoryEntry storyEntry = new StoryEntry();
        storyEntry.fromCamera = true;
        storyEntry.file = file;
        storyEntry.fileDeletable = true;
        storyEntry.orientation = 0;
        storyEntry.invert = 0;
        storyEntry.isVideo = true;
        storyEntry.duration = j;
        storyEntry.thumbPath = str;
        storyEntry.left = 0.0f;
        storyEntry.right = Math.min(1.0f, 59500.0f / ((float) j));
        return storyEntry;
    }

    public static long getCoverTime(TL_stories.StoryItem storyItem) {
        TLRPC.MessageMedia messageMedia;
        TLRPC.Document document;
        TLRPC.TL_documentAttributeVideo tL_documentAttributeVideo;
        if (storyItem == null || (messageMedia = storyItem.media) == null || (document = messageMedia.document) == null) {
            return 0L;
        }
        int i = 0;
        while (true) {
            if (i >= document.attributes.size()) {
                tL_documentAttributeVideo = null;
                break;
            }
            if (document.attributes.get(i) instanceof TLRPC.TL_documentAttributeVideo) {
                tL_documentAttributeVideo = (TLRPC.TL_documentAttributeVideo) document.attributes.get(i);
                break;
            }
            i++;
        }
        if (tL_documentAttributeVideo == null) {
            return 0L;
        }
        return (long) (tL_documentAttributeVideo.video_start_ts * 1000.0d);
    }

    public static long getRepostDialogId(MessageObject messageObject) {
        Boolean useForwardForRepost = useForwardForRepost(messageObject);
        if (useForwardForRepost == null) {
            return 0L;
        }
        return useForwardForRepost.booleanValue() ? DialogObject.getPeerDialogId(messageObject.messageOwner.fwd_from.from_id) : messageObject.getDialogId();
    }

    public static int getRepostMessageId(MessageObject messageObject) {
        Boolean useForwardForRepost = useForwardForRepost(messageObject);
        if (useForwardForRepost == null) {
            return 0;
        }
        return useForwardForRepost.booleanValue() ? messageObject.messageOwner.fwd_from.channel_post : messageObject.getId();
    }

    public static Bitmap getScaledBitmap(DecodeBitmap decodeBitmap, int i, int i2, boolean z, boolean z2) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        decodeBitmap.decode(options);
        options.inJustDecodeBounds = false;
        options.inScaled = false;
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory());
        int i3 = options.outWidth;
        int i4 = options.outHeight;
        double d = (i3 * i4 * 4) + (i * i2 * 4);
        Double.isNaN(d);
        boolean z3 = d * 1.1d <= ((double) maxMemory);
        if (i3 <= i && i4 <= i2) {
            return decodeBitmap.decode(options);
        }
        if (!z2 || !z3 || SharedConfig.getDevicePerformanceClass() < 1) {
            options.inScaled = true;
            options.inDensity = options.outWidth;
            options.inTargetDensity = i;
            return decodeBitmap.decode(options);
        }
        Bitmap decode = decodeBitmap.decode(options);
        float max = Math.max(i / decode.getWidth(), i2 / decode.getHeight());
        int width = (int) (decode.getWidth() * max);
        int height = (int) (decode.getHeight() * max);
        Bitmap createBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        Matrix matrix = new Matrix();
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        BitmapShader bitmapShader = new BitmapShader(decode, tileMode, tileMode);
        Paint paint = new Paint(3);
        paint.setShader(bitmapShader);
        Utilities.clamp(Math.round(1.0f / max), 8, 0);
        matrix.reset();
        matrix.postScale(max, max);
        bitmapShader.setLocalMatrix(matrix);
        canvas.drawRect(0.0f, 0.0f, width, height, paint);
        return createBitmap;
    }

    public static boolean isAnimated(TLRPC.Document document, String str) {
        if (document != null) {
            if ("video/webm".equals(document.mime_type) || "video/mp4".equals(document.mime_type)) {
                return true;
            }
            if (MessageObject.isAnimatedStickerDocument(document, true) && RLottieDrawable.getFramesCount(str, null) > 1) {
                return true;
            }
        }
        return false;
    }

    public Bitmap lambda$buildBitmap$0(BitmapFactory.Options options) {
        return BitmapFactory.decodeFile(this.backgroundFile.getPath(), options);
    }

    public static Bitmap lambda$buildBitmap$1(File file, BitmapFactory.Options options) {
        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    public static Bitmap lambda$buildBitmap$2(File file, BitmapFactory.Options options) {
        return BitmapFactory.decodeFile(file.getPath(), options);
    }

    public Bitmap lambda$buildBitmap$3(BitmapFactory.Options options) {
        return BitmapFactory.decodeFile(this.paintFile.getPath(), options);
    }

    public Bitmap lambda$buildBitmap$4(BitmapFactory.Options options) {
        return BitmapFactory.decodeFile(this.messageFile.getPath(), options);
    }

    public Bitmap lambda$buildBitmap$5(BitmapFactory.Options options) {
        return BitmapFactory.decodeFile(this.paintEntitiesFile.getPath(), options);
    }

    public void lambda$checkStickers$14(TLObject tLObject) {
        this.checkStickersReqId = 0;
        if (tLObject instanceof TLRPC.Vector) {
            this.editStickers = new ArrayList();
            TLRPC.Vector vector = (TLRPC.Vector) tLObject;
            for (int i = 0; i < vector.objects.size(); i++) {
                TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) vector.objects.get(i);
                TLRPC.Document document = stickerSetCovered.cover;
                if (document == null && !stickerSetCovered.covers.isEmpty()) {
                    document = stickerSetCovered.covers.get(0);
                }
                if (document == null && (stickerSetCovered instanceof TLRPC.TL_stickerSetFullCovered)) {
                    TLRPC.TL_stickerSetFullCovered tL_stickerSetFullCovered = (TLRPC.TL_stickerSetFullCovered) stickerSetCovered;
                    if (!tL_stickerSetFullCovered.documents.isEmpty()) {
                        document = tL_stickerSetFullCovered.documents.get(0);
                    }
                }
                if (document != null) {
                    TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
                    tL_inputDocument.id = document.id;
                    tL_inputDocument.access_hash = document.access_hash;
                    tL_inputDocument.file_reference = document.file_reference;
                    this.editStickers.add(tL_inputDocument);
                }
            }
        }
    }

    public void lambda$checkStickers$15(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StoryEntry.this.lambda$checkStickers$14(tLObject);
            }
        });
    }

    public void lambda$checkStickers$16(TL_stories.StoryItem storyItem, TLRPC.TL_messages_getAttachedStickers tL_messages_getAttachedStickers, RequestDelegate requestDelegate, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null || !FileRefController.isFileRefError(tL_error.text) || storyItem == null) {
            requestDelegate.run(tLObject, tL_error);
        } else {
            FileRefController.getInstance(this.currentAccount).requestReference(storyItem, tL_messages_getAttachedStickers, requestDelegate);
        }
    }

    public void lambda$detectHDR$12(Utilities.Callback callback) {
        callback.run(this.hdrInfo);
    }

    public void lambda$detectHDR$13(final Utilities.Callback callback) {
        Runnable runnable;
        try {
            try {
                HDRInfo hDRInfo = this.hdrInfo;
                if (hDRInfo == null) {
                    hDRInfo = new HDRInfo();
                    this.hdrInfo = hDRInfo;
                    hDRInfo.maxlum = 1000.0f;
                    hDRInfo.minlum = 0.001f;
                }
                MediaExtractor mediaExtractor = new MediaExtractor();
                mediaExtractor.setDataSource(this.file.getAbsolutePath());
                int findTrack = MediaController.findTrack(mediaExtractor, false);
                mediaExtractor.selectTrack(findTrack);
                MediaFormat trackFormat = mediaExtractor.getTrackFormat(findTrack);
                if (trackFormat.containsKey("color-transfer")) {
                    hDRInfo.colorTransfer = trackFormat.getInteger("color-transfer");
                }
                if (trackFormat.containsKey("color-standard")) {
                    hDRInfo.colorStandard = trackFormat.getInteger("color-standard");
                }
                if (trackFormat.containsKey("color-range")) {
                    hDRInfo.colorRange = trackFormat.getInteger("color-range");
                }
                this.hdrInfo = this.hdrInfo;
                runnable = new Runnable() {
                    @Override
                    public final void run() {
                        StoryEntry.this.lambda$detectHDR$12(callback);
                    }
                };
            } catch (Exception e) {
                FileLog.e(e);
                this.hdrInfo = this.hdrInfo;
                runnable = new Runnable() {
                    @Override
                    public final void run() {
                        StoryEntry.this.lambda$detectHDR$12(callback);
                    }
                };
            }
            AndroidUtilities.runOnUIThread(runnable);
        } catch (Throwable th) {
            this.hdrInfo = this.hdrInfo;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StoryEntry.this.lambda$detectHDR$12(callback);
                }
            });
            throw th;
        }
    }

    public static void lambda$getVideoEditedInfo$10(String[] strArr, int[][] iArr, Runnable runnable) {
        for (int i = 0; i < strArr.length; i++) {
            String str = strArr[i];
            if (str != null) {
                AnimatedFileDrawable.getVideoInfo(str, iArr[i]);
            }
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public static void lambda$getVideoEditedInfo$11(String str, int[][] iArr, Runnable runnable) {
        AnimatedFileDrawable.getVideoInfo(str, iArr[0]);
        AndroidUtilities.runOnUIThread(runnable);
    }

    public void lambda$getVideoEditedInfo$9(java.lang.String r20, int[][] r21, org.telegram.messenger.Utilities.Callback r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.recorder.StoryEntry.lambda$getVideoEditedInfo$9(java.lang.String, int[][], org.telegram.messenger.Utilities$Callback):void");
    }

    public void lambda$setupGradient$7(Bitmap bitmap, Runnable runnable, int[] iArr) {
        this.gradientTopColor = iArr[0];
        this.gradientBottomColor = iArr[1];
        bitmap.recycle();
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$setupGradient$8(Runnable runnable, int[] iArr) {
        this.gradientTopColor = iArr[0];
        this.gradientBottomColor = iArr[1];
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$updateFilter$6(Bitmap bitmap, boolean z, Runnable runnable) {
        try {
            bitmap.compress(z ? Bitmap.CompressFormat.WEBP : Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(this.filterFile));
        } catch (Exception e) {
            FileLog.e((Throwable) e, false);
            if (z) {
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, new FileOutputStream(this.filterFile));
                } catch (Exception e2) {
                    FileLog.e((Throwable) e2, false);
                }
            }
        }
        bitmap.recycle();
        AndroidUtilities.runOnUIThread(runnable);
    }

    public static File makeCacheFile(int i, String str) {
        TLRPC.TL_videoSize_layer127 tL_videoSize_layer127;
        TLRPC.TL_fileLocationToBeDeprecated tL_fileLocationToBeDeprecated = new TLRPC.TL_fileLocationToBeDeprecated();
        tL_fileLocationToBeDeprecated.volume_id = -2147483648L;
        tL_fileLocationToBeDeprecated.dc_id = Integer.MIN_VALUE;
        tL_fileLocationToBeDeprecated.local_id = SharedConfig.getLastLocalId();
        tL_fileLocationToBeDeprecated.file_reference = new byte[0];
        if ("mp4".equals(str) || "webm".equals(str)) {
            TLRPC.TL_videoSize_layer127 tL_videoSize_layer1272 = new TLRPC.TL_videoSize_layer127();
            tL_videoSize_layer1272.location = tL_fileLocationToBeDeprecated;
            tL_videoSize_layer127 = tL_videoSize_layer1272;
        } else {
            ?? tL_photoSize_layer127 = new TLRPC.TL_photoSize_layer127();
            tL_photoSize_layer127.location = tL_fileLocationToBeDeprecated;
            tL_videoSize_layer127 = tL_photoSize_layer127;
        }
        return FileLoader.getInstance(i).getPathToAttach(tL_videoSize_layer127, str, true);
    }

    public static File makeCacheFile(int i, boolean z) {
        return makeCacheFile(i, z ? "mp4" : "jpg");
    }

    public static StoryEntry repostMessage(ArrayList arrayList) {
        MessageObject messageObject;
        int i;
        StoryEntry storyEntry = new StoryEntry();
        storyEntry.isRepostMessage = true;
        storyEntry.messageObjects = arrayList;
        storyEntry.resultWidth = 1080;
        storyEntry.resultHeight = 1920;
        storyEntry.backgroundWallpaperPeerId = getRepostDialogId((MessageObject) arrayList.get(0));
        VideoEditedInfo.MediaEntity mediaEntity = new VideoEditedInfo.MediaEntity();
        mediaEntity.type = (byte) 6;
        mediaEntity.x = 0.5f;
        mediaEntity.y = 0.5f;
        ArrayList arrayList2 = new ArrayList();
        storyEntry.mediaEntities = arrayList2;
        arrayList2.add(mediaEntity);
        if (arrayList.size() == 1 && (messageObject = (MessageObject) arrayList.get(0)) != null && ((i = messageObject.type) == 8 || i == 3 || i == 5)) {
            TLRPC.Message message = messageObject.messageOwner;
            if (message != null && message.attachPath != null) {
                storyEntry.file = new File(messageObject.messageOwner.attachPath);
            }
            File file = storyEntry.file;
            if (file == null || !file.exists()) {
                storyEntry.file = FileLoader.getInstance(storyEntry.currentAccount).getPathToMessage(messageObject.messageOwner);
            }
            File file2 = storyEntry.file;
            if (file2 == null || !file2.exists()) {
                storyEntry.file = null;
            } else {
                storyEntry.isVideo = true;
                storyEntry.fileDeletable = false;
                long duration = (long) (messageObject.getDuration() * 1000.0d);
                storyEntry.duration = duration;
                storyEntry.left = 0.0f;
                storyEntry.right = Math.min(1.0f, 59500.0f / ((float) duration));
            }
        }
        return storyEntry;
    }

    public static StoryEntry repostStoryItem(File file, TL_stories.StoryItem storyItem) {
        StoryEntry storyEntry = new StoryEntry();
        storyEntry.isRepost = true;
        storyEntry.repostMedia = storyItem.media;
        storyEntry.repostPeer = MessagesController.getInstance(storyEntry.currentAccount).getPeer(storyItem.dialogId);
        storyEntry.repostStoryId = storyItem.id;
        storyEntry.repostCaption = storyItem.caption;
        storyEntry.file = file;
        storyEntry.fileDeletable = false;
        storyEntry.width = 720;
        storyEntry.height = 1280;
        TLRPC.MessageMedia messageMedia = storyItem.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaPhoto) {
            storyEntry.isVideo = false;
            if (file != null) {
                storyEntry.decodeBounds(file.getAbsolutePath());
            }
        } else if (messageMedia instanceof TLRPC.TL_messageMediaDocument) {
            storyEntry.isVideo = true;
            TLRPC.Document document = messageMedia.document;
            if (document != null && document.attributes != null) {
                int i = 0;
                while (true) {
                    if (i >= storyItem.media.document.attributes.size()) {
                        break;
                    }
                    TLRPC.DocumentAttribute documentAttribute = storyItem.media.document.attributes.get(i);
                    if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                        storyEntry.width = documentAttribute.w;
                        storyEntry.height = documentAttribute.h;
                        storyEntry.fileDuration = documentAttribute.duration;
                        break;
                    }
                    i++;
                }
            }
            TLRPC.Document document2 = storyItem.media.document;
            if (document2 != null) {
                String str = storyItem.firstFramePath;
                if (str != null) {
                    storyEntry.thumbPath = str;
                } else if (document2.thumbs != null) {
                    for (int i2 = 0; i2 < storyItem.media.document.thumbs.size(); i2++) {
                        TLRPC.PhotoSize photoSize = storyItem.media.document.thumbs.get(i2);
                        if (photoSize instanceof TLRPC.TL_photoStrippedSize) {
                            storyEntry.thumbPathBitmap = ImageLoader.getStrippedPhotoBitmap(photoSize.bytes, null);
                        } else {
                            File pathToAttach = FileLoader.getInstance(storyEntry.currentAccount).getPathToAttach(photoSize, true);
                            if (pathToAttach != null && pathToAttach.exists()) {
                                storyEntry.thumbPath = pathToAttach.getAbsolutePath();
                            }
                        }
                    }
                }
            }
        }
        storyEntry.setupMatrix();
        storyEntry.checkStickers(storyItem);
        return storyEntry;
    }

    public static void setupScale(BitmapFactory.Options options, int i, int i2) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() - (runtime.totalMemory() - runtime.freeMemory());
        int i3 = options.outWidth;
        int i4 = options.outHeight;
        if (i3 * i4 * 8 > maxMemory || Math.max(i3, i4) > 4200 || SharedConfig.getDevicePerformanceClass() <= 0) {
            options.inScaled = true;
            options.inDensity = options.outWidth;
            options.inTargetDensity = i;
        }
    }

    public static Boolean useForwardForRepost(MessageObject messageObject) {
        TLRPC.Message message;
        TLRPC.Peer peer;
        if (messageObject == null || (message = messageObject.messageOwner) == null) {
            return null;
        }
        TLRPC.Chat chat = MessagesController.getInstance(messageObject.currentAccount).getChat(Long.valueOf(-DialogObject.getPeerDialogId(message.peer_id)));
        if ((chat == null || !chat.noforwards) && ChatObject.isChannelAndNotMegaGroup(chat)) {
            return Boolean.FALSE;
        }
        TLRPC.MessageFwdHeader messageFwdHeader = messageObject.messageOwner.fwd_from;
        if (messageFwdHeader != null && (peer = messageFwdHeader.from_id) != null && (messageFwdHeader.flags & 4) != 0) {
            long peerDialogId = DialogObject.getPeerDialogId(peer);
            TLRPC.Chat chat2 = MessagesController.getInstance(messageObject.currentAccount).getChat(Long.valueOf(-peerDialogId));
            if (peerDialogId < 0 && ((chat2 == null || !chat2.noforwards) && ChatObject.isChannelAndNotMegaGroup(chat2))) {
                return Boolean.TRUE;
            }
        }
        return null;
    }

    public android.graphics.Bitmap buildBitmap(float r26, android.graphics.Bitmap r27) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.recorder.StoryEntry.buildBitmap(float, android.graphics.Bitmap):android.graphics.Bitmap");
    }

    public void buildPhoto(File file) {
        Bitmap buildBitmap = buildBitmap(1.0f, null);
        Bitmap bitmap = this.thumbBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.thumbBitmap = null;
        }
        this.thumbBitmap = Bitmap.createScaledBitmap(buildBitmap, 40, 22, true);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            buildBitmap.compress(Bitmap.CompressFormat.JPEG, 95, fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
        buildBitmap.recycle();
    }

    public void cancelCheckStickers() {
        if (this.checkStickersReqId != 0) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkStickersReqId, true);
        }
    }

    public void checkStickers(final TL_stories.StoryItem storyItem) {
        if (storyItem == null || storyItem.media == null) {
            return;
        }
        final TLRPC.TL_messages_getAttachedStickers tL_messages_getAttachedStickers = new TLRPC.TL_messages_getAttachedStickers();
        TLRPC.MessageMedia messageMedia = storyItem.media;
        TLRPC.Photo photo = messageMedia.photo;
        if (photo == null) {
            TLRPC.Document document = messageMedia.document;
            if (document == null || !MessageObject.isDocumentHasAttachedStickers(document)) {
                return;
            }
            TLRPC.TL_inputStickeredMediaDocument tL_inputStickeredMediaDocument = new TLRPC.TL_inputStickeredMediaDocument();
            TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
            tL_inputStickeredMediaDocument.id = tL_inputDocument;
            tL_inputDocument.id = document.id;
            tL_inputDocument.access_hash = document.access_hash;
            byte[] bArr = document.file_reference;
            tL_inputDocument.file_reference = bArr;
            if (bArr == null) {
                tL_inputDocument.file_reference = new byte[0];
            }
            tL_messages_getAttachedStickers.media = tL_inputStickeredMediaDocument;
        } else {
            if (!photo.has_stickers) {
                return;
            }
            TLRPC.TL_inputStickeredMediaPhoto tL_inputStickeredMediaPhoto = new TLRPC.TL_inputStickeredMediaPhoto();
            TLRPC.TL_inputPhoto tL_inputPhoto = new TLRPC.TL_inputPhoto();
            tL_inputStickeredMediaPhoto.id = tL_inputPhoto;
            tL_inputPhoto.id = photo.id;
            tL_inputPhoto.access_hash = photo.access_hash;
            byte[] bArr2 = photo.file_reference;
            tL_inputPhoto.file_reference = bArr2;
            if (bArr2 == null) {
                tL_inputPhoto.file_reference = new byte[0];
            }
            tL_messages_getAttachedStickers.media = tL_inputStickeredMediaPhoto;
        }
        final RequestDelegate requestDelegate = new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StoryEntry.this.lambda$checkStickers$15(tLObject, tL_error);
            }
        };
        this.checkStickersReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getAttachedStickers, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StoryEntry.this.lambda$checkStickers$16(storyItem, tL_messages_getAttachedStickers, requestDelegate, tLObject, tL_error);
            }
        });
    }

    public void clearFilter() {
        File file = this.filterFile;
        if (file != null) {
            file.delete();
            this.filterFile = null;
        }
    }

    public void clearPaint() {
        File file = this.paintFile;
        if (file != null) {
            file.delete();
            this.paintFile = null;
        }
        File file2 = this.backgroundFile;
        if (file2 != null) {
            file2.delete();
            this.backgroundFile = null;
        }
        File file3 = this.messageFile;
        if (file3 != null) {
            file3.delete();
            this.messageFile = null;
        }
        File file4 = this.messageVideoMaskFile;
        if (file4 != null) {
            file4.delete();
            this.messageVideoMaskFile = null;
        }
        File file5 = this.paintEntitiesFile;
        if (file5 != null) {
            file5.delete();
            this.paintEntitiesFile = null;
        }
    }

    public StoryEntry copy() {
        StoryEntry storyEntry = new StoryEntry();
        storyEntry.draftId = this.draftId;
        storyEntry.isDraft = this.isDraft;
        storyEntry.draftDate = this.draftDate;
        storyEntry.editStoryPeerId = this.editStoryPeerId;
        storyEntry.editStoryId = this.editStoryId;
        storyEntry.isEdit = this.isEdit;
        storyEntry.isEditSaved = this.isEditSaved;
        storyEntry.fileDuration = this.fileDuration;
        storyEntry.editedMedia = this.editedMedia;
        storyEntry.editedCaption = this.editedCaption;
        storyEntry.editedPrivacy = this.editedPrivacy;
        storyEntry.editedMediaAreas = this.editedMediaAreas;
        storyEntry.isError = this.isError;
        storyEntry.error = this.error;
        storyEntry.audioPath = this.audioPath;
        storyEntry.audioAuthor = this.audioAuthor;
        storyEntry.audioTitle = this.audioTitle;
        storyEntry.audioDuration = this.audioDuration;
        storyEntry.audioOffset = this.audioOffset;
        storyEntry.audioLeft = this.audioLeft;
        storyEntry.audioRight = this.audioRight;
        storyEntry.audioVolume = this.audioVolume;
        storyEntry.editDocumentId = this.editDocumentId;
        storyEntry.editPhotoId = this.editPhotoId;
        storyEntry.editExpireDate = this.editExpireDate;
        storyEntry.isVideo = this.isVideo;
        storyEntry.file = this.file;
        storyEntry.fileDeletable = this.fileDeletable;
        storyEntry.thumbPath = this.thumbPath;
        storyEntry.muted = this.muted;
        storyEntry.left = this.left;
        storyEntry.right = this.right;
        storyEntry.duration = this.duration;
        storyEntry.width = this.width;
        storyEntry.height = this.height;
        storyEntry.resultWidth = this.resultWidth;
        storyEntry.resultHeight = this.resultHeight;
        storyEntry.peer = this.peer;
        storyEntry.invert = this.invert;
        storyEntry.matrix.set(this.matrix);
        storyEntry.gradientTopColor = this.gradientTopColor;
        storyEntry.gradientBottomColor = this.gradientBottomColor;
        storyEntry.caption = this.caption;
        storyEntry.captionEntitiesAllowed = this.captionEntitiesAllowed;
        storyEntry.privacy = this.privacy;
        storyEntry.privacyRules.clear();
        storyEntry.privacyRules.addAll(this.privacyRules);
        storyEntry.pinned = this.pinned;
        storyEntry.allowScreenshots = this.allowScreenshots;
        storyEntry.period = this.period;
        storyEntry.shareUserIds = this.shareUserIds;
        storyEntry.silent = this.silent;
        storyEntry.scheduleDate = this.scheduleDate;
        storyEntry.blurredVideoThumb = this.blurredVideoThumb;
        storyEntry.uploadThumbFile = this.uploadThumbFile;
        storyEntry.draftThumbFile = this.draftThumbFile;
        storyEntry.paintFile = this.paintFile;
        storyEntry.messageFile = this.messageFile;
        storyEntry.backgroundFile = this.backgroundFile;
        storyEntry.paintBlurFile = this.paintBlurFile;
        storyEntry.paintEntitiesFile = this.paintEntitiesFile;
        storyEntry.averageDuration = this.averageDuration;
        storyEntry.mediaEntities = new ArrayList();
        if (this.mediaEntities != null) {
            for (int i = 0; i < this.mediaEntities.size(); i++) {
                storyEntry.mediaEntities.add(((VideoEditedInfo.MediaEntity) this.mediaEntities.get(i)).copy());
            }
        }
        storyEntry.stickers = this.stickers;
        storyEntry.editStickers = this.editStickers;
        storyEntry.filterFile = this.filterFile;
        storyEntry.filterState = this.filterState;
        storyEntry.thumbBitmap = this.thumbBitmap;
        storyEntry.fromCamera = this.fromCamera;
        storyEntry.thumbPathBitmap = this.thumbPathBitmap;
        storyEntry.isRepost = this.isRepost;
        storyEntry.round = this.round;
        storyEntry.roundLeft = this.roundLeft;
        storyEntry.roundRight = this.roundRight;
        storyEntry.roundDuration = this.roundDuration;
        storyEntry.roundThumb = this.roundThumb;
        storyEntry.roundOffset = this.roundOffset;
        storyEntry.roundVolume = this.roundVolume;
        storyEntry.isEditingCover = this.isEditingCover;
        storyEntry.botId = this.botId;
        storyEntry.botLang = this.botLang;
        storyEntry.editingBotPreview = this.editingBotPreview;
        storyEntry.cover = this.cover;
        storyEntry.collageContent = this.collageContent;
        storyEntry.collage = this.collage;
        storyEntry.videoLoop = this.videoLoop;
        storyEntry.videoOffset = this.videoOffset;
        return storyEntry;
    }

    public void decodeBounds(String str) {
        int i;
        if (str != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(str, options);
                this.width = options.outWidth;
                this.height = options.outHeight;
            } catch (Exception unused) {
            }
        }
        if (this.isVideo) {
            return;
        }
        if (((int) Math.max(this.width, (this.height / 16.0f) * 9.0f)) <= 900) {
            this.resultWidth = 720;
            i = 1280;
        } else {
            this.resultWidth = 1080;
            i = 1920;
        }
        this.resultHeight = i;
    }

    public void destroy(boolean z) {
        Bitmap bitmap = this.blurredVideoThumb;
        if (bitmap != null && !bitmap.isRecycled()) {
            this.blurredVideoThumb.recycle();
            this.blurredVideoThumb = null;
        }
        File file = this.uploadThumbFile;
        if (file != null) {
            file.delete();
            this.uploadThumbFile = null;
        }
        if (!z) {
            clearPaint();
            clearFilter();
            File file2 = this.file;
            if (file2 != null) {
                if (this.fileDeletable && (!this.isEdit || this.editedMedia)) {
                    file2.delete();
                }
                this.file = null;
            }
            if (this.thumbPath != null) {
                if (this.fileDeletable) {
                    new File(this.thumbPath).delete();
                }
                this.thumbPath = null;
            }
            ArrayList arrayList = this.mediaEntities;
            if (arrayList != null) {
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    VideoEditedInfo.MediaEntity mediaEntity = (VideoEditedInfo.MediaEntity) it.next();
                    if (mediaEntity.type == 2 && !TextUtils.isEmpty(mediaEntity.segmentedPath)) {
                        try {
                            new File(mediaEntity.segmentedPath).delete();
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                        mediaEntity.segmentedPath = "";
                    }
                }
            }
            File file3 = this.round;
            if (file3 != null && (!this.isEdit || this.editedMedia)) {
                file3.delete();
                this.round = null;
            }
            if (this.roundThumb != null && (!this.isEdit || this.editedMedia)) {
                try {
                    new File(this.roundThumb).delete();
                } catch (Exception unused) {
                }
                this.roundThumb = null;
            }
        }
        Bitmap bitmap2 = this.thumbPathBitmap;
        if (bitmap2 != null) {
            bitmap2.recycle();
            this.thumbPathBitmap = null;
        }
        if (this.collageContent != null) {
            for (int i = 0; i < this.collageContent.size(); i++) {
                ((StoryEntry) this.collageContent.get(i)).destroy(z);
            }
        }
        cancelCheckStickers();
    }

    public void detectHDR(final Utilities.Callback callback) {
        if (callback == null) {
            return;
        }
        HDRInfo hDRInfo = this.hdrInfo;
        if (hDRInfo != null) {
            callback.run(hDRInfo);
            return;
        }
        if (this.isVideo && Build.VERSION.SDK_INT >= 24) {
            Utilities.globalQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    StoryEntry.this.lambda$detectHDR$13(callback);
                }
            });
            return;
        }
        HDRInfo hDRInfo2 = new HDRInfo();
        this.hdrInfo = hDRInfo2;
        callback.run(hDRInfo2);
    }

    public File getOriginalFile() {
        File file = this.filterFile;
        return file != null ? file : this.file;
    }

    public void getVideoEditedInfo(final Utilities.Callback callback) {
        int i;
        if (!wouldBeVideo()) {
            callback.run(null);
            return;
        }
        if (!this.isVideo && ((i = this.resultWidth) > 720 || this.resultHeight > 1280)) {
            float f = 720.0f / i;
            this.matrix.postScale(f, f, 0.0f, 0.0f);
            this.resultWidth = 720;
            this.resultHeight = 1280;
        }
        File file = this.file;
        final String absolutePath = file == null ? null : file.getAbsolutePath();
        final int[][] iArr = (int[][]) Array.newInstance((Class<?>) Integer.TYPE, Math.max(1, isCollage() ? this.collageContent.size() : 0), 11);
        iArr[0] = new int[11];
        final Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                StoryEntry.this.lambda$getVideoEditedInfo$9(absolutePath, iArr, callback);
            }
        };
        if (!isCollage()) {
            if (this.file == null) {
                runnable.run();
                return;
            } else {
                Utilities.globalQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        StoryEntry.lambda$getVideoEditedInfo$11(absolutePath, iArr, runnable);
                    }
                });
                return;
            }
        }
        final String[] strArr = new String[this.collageContent.size()];
        for (int i2 = 0; i2 < this.collageContent.size(); i2++) {
            strArr[i2] = ((StoryEntry) this.collageContent.get(i2)).file == null ? null : ((StoryEntry) this.collageContent.get(i2)).file.getAbsolutePath();
            iArr[i2] = new int[11];
        }
        Utilities.globalQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                StoryEntry.lambda$getVideoEditedInfo$10(strArr, iArr, runnable);
            }
        });
    }

    public boolean hasVideo() {
        if (!isCollage()) {
            return false;
        }
        for (int i = 0; i < this.collageContent.size(); i++) {
            if (((StoryEntry) this.collageContent.get(i)).isVideo) {
                return true;
            }
        }
        return false;
    }

    public boolean isCollage() {
        return (this.collage == null || this.collageContent == null) ? false : true;
    }

    public void setupGradient(final Runnable runnable) {
        final Bitmap bitmap;
        Utilities.Callback callback;
        if (this.isVideo && this.gradientTopColor == 0 && this.gradientBottomColor == 0) {
            if (this.thumbPath != null) {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    if (this.thumbPath.startsWith("vthumb://")) {
                        long parseInt = Integer.parseInt(this.thumbPath.substring(9));
                        options.inJustDecodeBounds = true;
                        MediaStore.Video.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), parseInt, 1, options);
                        options.inSampleSize = calculateInSampleSize(options, 240, 240);
                        options.inJustDecodeBounds = false;
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inDither = true;
                        bitmap = MediaStore.Video.Thumbnails.getThumbnail(ApplicationLoader.applicationContext.getContentResolver(), parseInt, 1, options);
                    } else {
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(this.thumbPath);
                        options.inSampleSize = calculateInSampleSize(options, 240, 240);
                        options.inJustDecodeBounds = false;
                        options.inPreferredConfig = Bitmap.Config.RGB_565;
                        options.inDither = true;
                        bitmap = BitmapFactory.decodeFile(this.thumbPath);
                    }
                } catch (Exception unused) {
                    bitmap = null;
                }
                if (bitmap == null) {
                    return;
                } else {
                    callback = new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            StoryEntry.this.lambda$setupGradient$7(bitmap, runnable, (int[]) obj);
                        }
                    };
                }
            } else {
                bitmap = this.thumbPathBitmap;
                if (bitmap == null) {
                    return;
                } else {
                    callback = new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            StoryEntry.this.lambda$setupGradient$8(runnable, (int[]) obj);
                        }
                    };
                }
            }
            DominantColors.getColors(true, bitmap, true, callback);
        }
    }

    public void setupMatrix() {
        setupMatrix(this.matrix, 0);
    }

    public void setupMatrix(Matrix matrix, int i) {
        matrix.reset();
        int i2 = this.width;
        int i3 = this.height;
        int i4 = this.orientation + i;
        int i5 = this.invert;
        matrix.postScale(i5 == 1 ? -1.0f : 1.0f, i5 == 2 ? -1.0f : 1.0f, i2 / 2.0f, i3 / 2.0f);
        if (i4 != 0) {
            matrix.postTranslate((-i2) / 2.0f, (-i3) / 2.0f);
            matrix.postRotate(i4);
            if (i4 == 90 || i4 == 270) {
                i3 = i2;
                i2 = i3;
            }
            matrix.postTranslate(i2 / 2.0f, i3 / 2.0f);
        }
        float f = i2;
        float f2 = this.resultWidth / f;
        if (this.botId != 0) {
            f2 = Math.min(f2, this.resultHeight / i3);
        } else {
            float f3 = i3;
            if (f3 / f > 1.29f) {
                f2 = Math.max(f2, this.resultHeight / f3);
            }
        }
        matrix.postScale(f2, f2);
        matrix.postTranslate((this.resultWidth - (f * f2)) / 2.0f, (this.resultHeight - (i3 * f2)) / 2.0f);
    }

    public void updateFilter(PhotoFilterView photoFilterView, final Runnable runnable) {
        clearFilter();
        MediaController.SavedFilterState savedFilterState = photoFilterView.getSavedFilterState();
        this.filterState = savedFilterState;
        if (this.isVideo) {
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        if (savedFilterState.isEmpty()) {
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        Bitmap bitmap = photoFilterView.getBitmap();
        if (bitmap == null) {
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        Matrix matrix = new Matrix();
        int i = this.invert;
        final boolean z = true;
        matrix.postScale(i == 1 ? -1.0f : 1.0f, i == 2 ? -1.0f : 1.0f, this.width / 2.0f, this.height / 2.0f);
        matrix.postRotate(-this.orientation);
        final Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        this.matrix.preScale(this.width / createBitmap.getWidth(), this.height / createBitmap.getHeight());
        this.width = createBitmap.getWidth();
        this.height = createBitmap.getHeight();
        bitmap.recycle();
        File file = this.filterFile;
        if (file != null && file.exists()) {
            this.filterFile.delete();
        }
        String ext = ext(this.file);
        if (!"png".equals(ext) && !"webp".equals(ext)) {
            z = false;
        }
        this.filterFile = makeCacheFile(this.currentAccount, z ? "webp" : "jpg");
        if (runnable != null) {
            Utilities.themeQueue.postRunnable(new Runnable() {
                @Override
                public final void run() {
                    StoryEntry.this.lambda$updateFilter$6(createBitmap, z, runnable);
                }
            });
            return;
        }
        try {
            createBitmap.compress(z ? Bitmap.CompressFormat.WEBP : Bitmap.CompressFormat.JPEG, 90, new FileOutputStream(this.filterFile));
        } catch (Exception e) {
            FileLog.e(e);
        }
        createBitmap.recycle();
    }

    public boolean wouldBeVideo() {
        return wouldBeVideo(this.mediaEntities);
    }

    public boolean wouldBeVideo(ArrayList arrayList) {
        ArrayList<VideoEditedInfo.EmojiEntity> arrayList2;
        if (this.isVideo || this.audioPath != null || this.round != null) {
            return true;
        }
        if (arrayList != null && !arrayList.isEmpty()) {
            for (int i = 0; i < arrayList.size(); i++) {
                VideoEditedInfo.MediaEntity mediaEntity = (VideoEditedInfo.MediaEntity) arrayList.get(i);
                byte b = mediaEntity.type;
                if (b == 0) {
                    if (isAnimated(mediaEntity.document, mediaEntity.text)) {
                        return true;
                    }
                } else if (b == 1 && (arrayList2 = mediaEntity.entities) != null && !arrayList2.isEmpty()) {
                    for (int i2 = 0; i2 < mediaEntity.entities.size(); i2++) {
                        VideoEditedInfo.EmojiEntity emojiEntity = mediaEntity.entities.get(i2);
                        if (isAnimated(emojiEntity.document, emojiEntity.documentAbsolutePath)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
