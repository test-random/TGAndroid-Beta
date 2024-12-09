package org.telegram.ui;

import android.app.Activity;
import android.graphics.Point;
import android.view.View;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.Reactions.AnimatedEmojiEffect;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerSetBulletinLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Stories.StoryReactionWidgetView;

public class EmojiAnimationsOverlay implements NotificationCenter.NotificationCenterDelegate {
    private static final HashSet excludeEmojiFromPack;
    private static final HashSet supportedEmoji = new HashSet();
    private boolean attached;
    ChatActivity chatActivity;
    FrameLayout contentLayout;
    int currentAccount;
    long dialogId;
    Runnable hintRunnable;
    String lastTappedEmoji;
    RecyclerListView listView;
    private HashMap preloaded;
    Runnable sentInteractionsRunnable;
    TLRPC.TL_messages_stickerSet set;
    long threadMsgId;
    private final int ANIMATION_JSON_VERSION = 1;
    private final String INTERACTIONS_STICKER_PACK = "EmojiAnimations";
    boolean inited = false;
    HashMap emojiInteractionsStickersMap = new HashMap();
    HashMap lastAnimationIndex = new HashMap();
    Random random = new Random();
    int lastTappedMsgId = -1;
    long lastTappedTime = 0;
    ArrayList timeIntervals = new ArrayList();
    ArrayList animationIndexes = new ArrayList();
    final ArrayList drawingObjects = new ArrayList();

    public static class DrawingObject {
        TLRPC.Document document;
        public long documentId;
        private String fileName;
        public AnimatedEmojiEffect genericEffect;
        ImageReceiver imageReceiver;
        public boolean isMessageEffect;
        boolean isOut;
        public boolean isPremiumSticker;
        public boolean isReaction;
        public float lastH;
        public float lastW;
        public float lastX;
        public float lastY;
        int messageId;
        public float randomOffsetX;
        public float randomOffsetY;
        float removeProgress;
        boolean removing;
        public boolean viewFound;
        boolean wasPlayed;

        DrawingObject() {
            ImageReceiver imageReceiver = new ImageReceiver();
            this.imageReceiver = imageReceiver;
            imageReceiver.setAllowLoadingOnAttachedOnly(true);
            this.imageReceiver.setAllowDrawWhileCacheGenerating(true);
        }

        public float getProgress() {
            ImageLocation mediaLocation = this.imageReceiver.getMediaLocation();
            if (mediaLocation == null) {
                mediaLocation = this.imageReceiver.getImageLocation();
            }
            if (mediaLocation == null) {
                mediaLocation = this.imageReceiver.getThumbLocation();
            }
            if (mediaLocation == null) {
                return -1.0f;
            }
            if (this.fileName == null) {
                TLRPC.Document document = mediaLocation.document;
                this.fileName = document != null ? FileLoader.getAttachFileName(document, "tgs") : FileLoader.getAttachFileName(mediaLocation.location, "tgs");
            }
            if (this.fileName == null) {
                return -1.0f;
            }
            Float fileProgress = ImageLoader.getInstance().getFileProgress(this.fileName);
            if (fileProgress == null) {
                fileProgress = Float.valueOf(1.0f);
            }
            return (fileProgress.floatValue() * 0.55f) + 0.15f + (fileProgress.floatValue() * 0.3f);
        }
    }

    static {
        HashSet hashSet = new HashSet();
        excludeEmojiFromPack = hashSet;
        hashSet.add("0⃣");
        hashSet.add("1⃣");
        hashSet.add("2⃣");
        hashSet.add("3⃣");
        hashSet.add("4⃣");
        hashSet.add("5⃣");
        hashSet.add("6⃣");
        hashSet.add("7⃣");
        hashSet.add("8⃣");
        hashSet.add("9⃣");
    }

    public EmojiAnimationsOverlay(FrameLayout frameLayout, int i) {
        this.contentLayout = frameLayout;
        this.currentAccount = i;
    }

    public EmojiAnimationsOverlay(ChatActivity chatActivity, FrameLayout frameLayout, RecyclerListView recyclerListView, int i, long j, long j2) {
        this.chatActivity = chatActivity;
        this.contentLayout = frameLayout;
        this.listView = recyclerListView;
        this.currentAccount = i;
        this.dialogId = j;
        this.threadMsgId = j2;
    }

    private void clearSendingInfo() {
        this.lastTappedMsgId = 0;
        this.lastTappedEmoji = null;
        this.lastTappedTime = 0L;
        this.timeIntervals.clear();
        this.animationIndexes.clear();
    }

    public void findViewAndShowAnimation(int i, int i2) {
        ChatMessageCell chatMessageCell;
        ChatActivity chatActivity;
        if (this.attached) {
            int i3 = 0;
            while (true) {
                if (i3 >= this.listView.getChildCount()) {
                    chatMessageCell = null;
                    break;
                }
                View childAt = this.listView.getChildAt(i3);
                if (childAt instanceof ChatMessageCell) {
                    chatMessageCell = (ChatMessageCell) childAt;
                    String stickerEmoji = chatMessageCell.getMessageObject().getStickerEmoji();
                    if (stickerEmoji == null) {
                        stickerEmoji = chatMessageCell.getMessageObject().messageOwner.message;
                    }
                    if (chatMessageCell.getPhotoImage().hasNotThumb() && stickerEmoji != null && chatMessageCell.getMessageObject().getId() == i) {
                        break;
                    }
                }
                i3++;
            }
            if (chatMessageCell == null || (chatActivity = this.chatActivity) == null) {
                return;
            }
            chatActivity.restartSticker(chatMessageCell);
            if (!EmojiData.hasEmojiSupportVibration(chatMessageCell.getMessageObject().getStickerEmoji()) && !chatMessageCell.getMessageObject().isPremiumSticker() && !chatMessageCell.getMessageObject().isAnimatedAnimatedEmoji()) {
                try {
                    chatMessageCell.performHapticFeedback(3);
                } catch (Exception unused) {
                }
            }
            showAnimationForCell(chatMessageCell, i2, false, true);
        }
    }

    public static int getFilterWidth() {
        float min;
        float f;
        if (AndroidUtilities.isTablet()) {
            min = AndroidUtilities.getMinTabletSide();
            f = 0.4f;
        } else {
            Point point = AndroidUtilities.displaySize;
            min = Math.min(point.x, point.y);
            f = 0.5f;
        }
        return (int) ((((int) (min * f)) * 2.0f) / AndroidUtilities.density);
    }

    public void lambda$createDrawingObject$1(final MessageObject messageObject, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                EmojiAnimationsOverlay.this.lambda$createDrawingObject$0(tLObject, messageObject);
            }
        });
    }

    public void lambda$createDrawingObject$2() {
        sendCurrentTaps();
        this.sentInteractionsRunnable = null;
    }

    public void lambda$showStickerSetBulletin$3(MessageObject messageObject) {
        Activity parentActivity = this.chatActivity.getParentActivity();
        ChatActivity chatActivity = this.chatActivity;
        TLRPC.InputStickerSet inputStickerSet = messageObject.getInputStickerSet();
        ChatActivity chatActivity2 = this.chatActivity;
        StickersAlert stickersAlert = new StickersAlert(parentActivity, chatActivity, inputStickerSet, null, chatActivity2.chatActivityEnterView, chatActivity2.getResourceProvider(), false);
        stickersAlert.setCalcMandatoryInsets(this.chatActivity.isKeyboardVisible());
        this.chatActivity.showDialog(stickersAlert);
    }

    private void preloadAnimation(TLRPC.Document document) {
        if (document == null) {
            return;
        }
        HashMap hashMap = this.preloaded;
        if (hashMap == null || !hashMap.containsKey(Long.valueOf(document.id))) {
            if (this.preloaded == null) {
                this.preloaded = new HashMap();
            }
            this.preloaded.put(Long.valueOf(document.id), Boolean.TRUE);
            MediaDataController.getInstance(this.currentAccount).preloadImage(ImageLocation.getForDocument(document), 2);
        }
    }

    private void sendCurrentTaps() {
        if (this.lastTappedMsgId == 0) {
            return;
        }
        TLRPC.TL_sendMessageEmojiInteraction tL_sendMessageEmojiInteraction = new TLRPC.TL_sendMessageEmojiInteraction();
        tL_sendMessageEmojiInteraction.msg_id = this.lastTappedMsgId;
        tL_sendMessageEmojiInteraction.emoticon = this.lastTappedEmoji;
        tL_sendMessageEmojiInteraction.interaction = new TLRPC.TL_dataJSON();
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("v", 1);
            JSONArray jSONArray = new JSONArray();
            for (int i = 0; i < this.timeIntervals.size(); i++) {
                JSONObject jSONObject2 = new JSONObject();
                jSONObject2.put("i", ((Integer) this.animationIndexes.get(i)).intValue() + 1);
                jSONObject2.put("t", ((float) ((Long) this.timeIntervals.get(i)).longValue()) / 1000.0f);
                jSONArray.put(i, jSONObject2);
            }
            jSONObject.put("a", jSONArray);
            tL_sendMessageEmojiInteraction.interaction.data = jSONObject.toString();
            TLRPC.TL_messages_setTyping tL_messages_setTyping = new TLRPC.TL_messages_setTyping();
            long j = this.threadMsgId;
            if (j != 0) {
                tL_messages_setTyping.top_msg_id = (int) j;
                tL_messages_setTyping.flags |= 1;
            }
            tL_messages_setTyping.action = tL_sendMessageEmojiInteraction;
            tL_messages_setTyping.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_setTyping, null);
            clearSendingInfo();
        } catch (JSONException e) {
            clearSendingInfo();
            FileLog.e(e);
        }
    }

    public void lambda$createDrawingObject$0(TLRPC.TL_messages_stickerSet tL_messages_stickerSet, final MessageObject messageObject) {
        if (this.chatActivity == null || MessagesController.getInstance(this.currentAccount).premiumFeaturesBlocked() || this.chatActivity.getParentActivity() == null) {
            return;
        }
        StickerSetBulletinLayout stickerSetBulletinLayout = new StickerSetBulletinLayout(this.contentLayout.getContext(), null, -1, messageObject.getDocument(), this.chatActivity.getResourceProvider());
        stickerSetBulletinLayout.titleTextView.setText(tL_messages_stickerSet.set.title);
        stickerSetBulletinLayout.subtitleTextView.setText(LocaleController.getString(R.string.PremiumStickerTooltip));
        Bulletin.UndoButton undoButton = new Bulletin.UndoButton(this.chatActivity.getParentActivity(), true, this.chatActivity.getResourceProvider());
        stickerSetBulletinLayout.setButton(undoButton);
        undoButton.setUndoAction(new Runnable() {
            @Override
            public final void run() {
                EmojiAnimationsOverlay.this.lambda$showStickerSetBulletin$3(messageObject);
            }
        });
        undoButton.setText(LocaleController.getString(R.string.ViewAction));
        Bulletin make = Bulletin.make(this.chatActivity, stickerSetBulletinLayout, 2750);
        make.hash = messageObject.getId();
        make.show();
    }

    public static java.lang.String unwrapEmoji(java.lang.String r8) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EmojiAnimationsOverlay.unwrapEmoji(java.lang.String):java.lang.String");
    }

    public void cancelAllAnimations() {
        for (int i = 0; i < this.drawingObjects.size(); i++) {
            ((DrawingObject) this.drawingObjects.get(i)).removing = true;
        }
    }

    public void cancelHintRunnable() {
        Runnable runnable = this.hintRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        this.hintRunnable = null;
    }

    public boolean checkPosition(ChatMessageCell chatMessageCell, float f, int i) {
        float y = chatMessageCell.getY() + chatMessageCell.getPhotoImage().getCenterY();
        return y > f && y < ((float) i);
    }

    public void checkStickerPack() {
        if (this.inited) {
            return;
        }
        TLRPC.TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("EmojiAnimations");
        this.set = stickerSetByName;
        if (stickerSetByName == null) {
            this.set = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("EmojiAnimations");
        }
        if (this.set == null) {
            MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("EmojiAnimations", false, true);
        }
        if (this.set != null) {
            HashMap hashMap = new HashMap();
            for (int i = 0; i < this.set.documents.size(); i++) {
                hashMap.put(Long.valueOf(this.set.documents.get(i).id), this.set.documents.get(i));
            }
            for (int i2 = 0; i2 < this.set.packs.size(); i2++) {
                TLRPC.TL_stickerPack tL_stickerPack = this.set.packs.get(i2);
                if (!excludeEmojiFromPack.contains(tL_stickerPack.emoticon) && tL_stickerPack.documents.size() > 0) {
                    supportedEmoji.add(tL_stickerPack.emoticon);
                    ArrayList arrayList = new ArrayList();
                    this.emojiInteractionsStickersMap.put(tL_stickerPack.emoticon, arrayList);
                    for (int i3 = 0; i3 < tL_stickerPack.documents.size(); i3++) {
                        arrayList.add((TLRPC.Document) hashMap.get(tL_stickerPack.documents.get(i3)));
                    }
                    if (tL_stickerPack.emoticon.equals("❤")) {
                        String[] strArr = {"🧡", "💛", "💚", "💙", "💜", "🖤", "🤍", "🤎"};
                        for (int i4 = 0; i4 < 8; i4++) {
                            String str = strArr[i4];
                            supportedEmoji.add(str);
                            this.emojiInteractionsStickersMap.put(str, arrayList);
                        }
                    }
                }
            }
            this.inited = true;
        }
    }

    public void clear() {
        for (int i = 0; i < this.drawingObjects.size(); i++) {
            ((DrawingObject) this.drawingObjects.get(i)).imageReceiver.onDetachedFromWindow();
            if (((DrawingObject) this.drawingObjects.get(i)).genericEffect != null) {
                ((DrawingObject) this.drawingObjects.get(i)).genericEffect.removeView(this.contentLayout);
            }
        }
        this.drawingObjects.clear();
    }

    public boolean createDrawingObject(java.lang.String r24, int r25, org.telegram.tgnet.TLRPC.Document r26, final org.telegram.messenger.MessageObject r27, int r28, final boolean r29, boolean r30, float r31, float r32, boolean r33) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EmojiAnimationsOverlay.createDrawingObject(java.lang.String, int, org.telegram.tgnet.TLRPC$Document, org.telegram.messenger.MessageObject, int, boolean, boolean, float, float, boolean):boolean");
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        Integer printingStringType;
        if (i == NotificationCenter.diceStickersDidLoad) {
            if ("EmojiAnimations".equals((String) objArr[0])) {
                checkStickerPack();
                return;
            }
            return;
        }
        if (i != NotificationCenter.onEmojiInteractionsReceived) {
            if (i == NotificationCenter.updateInterfaces && (printingStringType = MessagesController.getInstance(this.currentAccount).getPrintingStringType(this.dialogId, this.threadMsgId)) != null && printingStringType.intValue() == 5) {
                cancelHintRunnable();
                return;
            }
            return;
        }
        if (this.chatActivity == null) {
            return;
        }
        long longValue = ((Long) objArr[0]).longValue();
        TLRPC.TL_sendMessageEmojiInteraction tL_sendMessageEmojiInteraction = (TLRPC.TL_sendMessageEmojiInteraction) objArr[1];
        if (longValue == this.dialogId && supportedEmoji.contains(tL_sendMessageEmojiInteraction.emoticon)) {
            final int i3 = tL_sendMessageEmojiInteraction.msg_id;
            if (tL_sendMessageEmojiInteraction.interaction.data != null) {
                try {
                    JSONArray jSONArray = new JSONObject(tL_sendMessageEmojiInteraction.interaction.data).getJSONArray("a");
                    for (int i4 = 0; i4 < jSONArray.length(); i4++) {
                        JSONObject jSONObject = jSONArray.getJSONObject(i4);
                        final int optInt = jSONObject.optInt("i", 1) - 1;
                        AndroidUtilities.runOnUIThread(new Runnable() {
                            @Override
                            public void run() {
                                EmojiAnimationsOverlay.this.findViewAndShowAnimation(i3, optInt);
                            }
                        }, (long) (jSONObject.optDouble("t", 0.0d) * 1000.0d));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void draw(android.graphics.Canvas r17) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.EmojiAnimationsOverlay.draw(android.graphics.Canvas):void");
    }

    public float getProgress() {
        if (this.drawingObjects.isEmpty()) {
            return -1.0f;
        }
        return ((DrawingObject) this.drawingObjects.get(r0.size() - 1)).getProgress();
    }

    public boolean isIdle() {
        return this.drawingObjects.isEmpty();
    }

    protected void layoutObject(DrawingObject drawingObject) {
    }

    public void onAllEffectsEnd() {
    }

    public void onAttachedToWindow() {
        this.attached = true;
        checkStickerPack();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.onEmojiInteractionsReceived);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.updateInterfaces);
        for (int i = 0; i < this.drawingObjects.size(); i++) {
            ((DrawingObject) this.drawingObjects.get(i)).imageReceiver.onAttachedToWindow();
            if (((DrawingObject) this.drawingObjects.get(i)).genericEffect != null) {
                ((DrawingObject) this.drawingObjects.get(i)).genericEffect.setView(this.contentLayout);
            }
        }
    }

    public void onDetachedFromWindow() {
        this.attached = false;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.onEmojiInteractionsReceived);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateInterfaces);
        for (int i = 0; i < this.drawingObjects.size(); i++) {
            ((DrawingObject) this.drawingObjects.get(i)).imageReceiver.onDetachedFromWindow();
            if (((DrawingObject) this.drawingObjects.get(i)).genericEffect != null) {
                ((DrawingObject) this.drawingObjects.get(i)).genericEffect.removeView(this.contentLayout);
            }
        }
        this.drawingObjects.clear();
    }

    public void onScrolled(int i) {
        for (int i2 = 0; i2 < this.drawingObjects.size(); i2++) {
            if (!((DrawingObject) this.drawingObjects.get(i2)).viewFound) {
                ((DrawingObject) this.drawingObjects.get(i2)).lastY -= i;
            }
        }
    }

    public boolean onTapItem(ChatMessageCell chatMessageCell, ChatActivity chatActivity, boolean z) {
        if (chatActivity.isSecretChat() || chatMessageCell.getMessageObject() == null || chatMessageCell.getMessageObject().getId() < 0) {
            return false;
        }
        if (!chatMessageCell.getMessageObject().isPremiumSticker() && chatActivity.currentUser == null) {
            return false;
        }
        boolean showAnimationForCell = showAnimationForCell(chatMessageCell, -1, z, false);
        if (z && showAnimationForCell && !EmojiData.hasEmojiSupportVibration(chatMessageCell.getMessageObject().getStickerEmoji()) && !chatMessageCell.getMessageObject().isPremiumSticker() && !chatMessageCell.getMessageObject().isAnimatedAnimatedEmoji()) {
            try {
                chatMessageCell.performHapticFeedback(3);
            } catch (Exception unused) {
            }
        }
        if (chatMessageCell.getMessageObject().isPremiumSticker() || chatMessageCell.getEffect() != null || (!z && chatMessageCell.getMessageObject().isAnimatedEmojiStickerSingle())) {
            chatMessageCell.getMessageObject().forcePlayEffect = false;
            chatMessageCell.getMessageObject().messageOwner.premiumEffectWasPlayed = true;
            chatActivity.getMessagesStorage().updateMessageCustomParams(this.dialogId, chatMessageCell.getMessageObject().messageOwner);
            return showAnimationForCell;
        }
        Integer printingStringType = MessagesController.getInstance(this.currentAccount).getPrintingStringType(this.dialogId, this.threadMsgId);
        if ((printingStringType == null || printingStringType.intValue() != 5) && this.hintRunnable == null && showAnimationForCell && ((Bulletin.getVisibleBulletin() == null || !Bulletin.getVisibleBulletin().isShowing()) && SharedConfig.emojiInteractionsHintCount > 0 && UserConfig.getInstance(this.currentAccount).getClientUserId() != chatActivity.currentUser.id)) {
            SharedConfig.updateEmojiInteractionsHintCount(SharedConfig.emojiInteractionsHintCount - 1);
            StickerSetBulletinLayout stickerSetBulletinLayout = new StickerSetBulletinLayout(chatActivity.getParentActivity(), null, -1, chatMessageCell.getMessageObject().isAnimatedAnimatedEmoji() ? chatMessageCell.getMessageObject().getDocument() : MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(chatMessageCell.getMessageObject().getStickerEmoji()), chatActivity.getResourceProvider());
            stickerSetBulletinLayout.subtitleTextView.setVisibility(8);
            stickerSetBulletinLayout.titleTextView.setText(Emoji.replaceEmoji(AndroidUtilities.replaceTags(LocaleController.formatString("EmojiInteractionTapHint", R.string.EmojiInteractionTapHint, chatActivity.currentUser.first_name)), stickerSetBulletinLayout.titleTextView.getPaint().getFontMetricsInt(), false));
            stickerSetBulletinLayout.titleTextView.setTypeface(null);
            stickerSetBulletinLayout.titleTextView.setMaxLines(3);
            stickerSetBulletinLayout.titleTextView.setSingleLine(false);
            final Bulletin make = Bulletin.make(chatActivity, stickerSetBulletinLayout, 2750);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    make.show();
                    EmojiAnimationsOverlay.this.hintRunnable = null;
                }
            };
            this.hintRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, 1500L);
        }
        return showAnimationForCell;
    }

    public void preload(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        ArrayList arrayList;
        String str = visibleReaction.emojicon;
        if (str == null) {
            str = MessageObject.findAnimatedEmojiEmoticon(AnimatedEmojiDrawable.findDocument(this.currentAccount, visibleReaction.documentId));
        }
        if (str == null || (arrayList = (ArrayList) this.emojiInteractionsStickersMap.get(str)) == null || arrayList.isEmpty()) {
            return;
        }
        int min = Math.min(1, arrayList.size());
        for (int i = 0; i < min; i++) {
            preloadAnimation((TLRPC.Document) arrayList.get(i));
        }
    }

    public void preloadAnimation(ChatMessageCell chatMessageCell) {
        ArrayList arrayList;
        MessageObject messageObject = chatMessageCell.getMessageObject();
        if (messageObject.isPremiumSticker()) {
            return;
        }
        String stickerEmoji = messageObject.getStickerEmoji();
        if (stickerEmoji == null) {
            stickerEmoji = messageObject.messageOwner.message;
        }
        String unwrapEmoji = unwrapEmoji(stickerEmoji);
        if (!supportedEmoji.contains(unwrapEmoji) || (arrayList = (ArrayList) this.emojiInteractionsStickersMap.get(unwrapEmoji)) == null || arrayList.isEmpty()) {
            return;
        }
        int min = Math.min(1, arrayList.size());
        for (int i = 0; i < min; i++) {
            preloadAnimation((TLRPC.Document) arrayList.get(i));
        }
    }

    public void setAccount(int i) {
        this.currentAccount = i;
    }

    public boolean showAnimationForActionCell(ChatActionCell chatActionCell, TLRPC.Document document, TLRPC.VideoSize videoSize) {
        if (this.drawingObjects.size() > 12 || !chatActionCell.getPhotoImage().hasNotThumb()) {
            return false;
        }
        float imageHeight = chatActionCell.getPhotoImage().getImageHeight();
        float imageWidth = chatActionCell.getPhotoImage().getImageWidth();
        if (imageHeight <= 0.0f || imageWidth <= 0.0f) {
            return false;
        }
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < this.drawingObjects.size(); i3++) {
            if (((DrawingObject) this.drawingObjects.get(i3)).messageId == chatActionCell.getMessageObject().getId()) {
                i++;
                if (((DrawingObject) this.drawingObjects.get(i3)).imageReceiver.getLottieAnimation() == null || ((DrawingObject) this.drawingObjects.get(i3)).imageReceiver.getLottieAnimation().isGeneratingCache()) {
                    return false;
                }
            }
            if (((DrawingObject) this.drawingObjects.get(i3)).document != null && document != null && ((DrawingObject) this.drawingObjects.get(i3)).document.id == document.id) {
                i2++;
            }
        }
        if (i >= 4) {
            return false;
        }
        DrawingObject drawingObject = new DrawingObject();
        drawingObject.isPremiumSticker = true;
        if (!drawingObject.isMessageEffect) {
            drawingObject.randomOffsetX = (imageWidth / 4.0f) * ((this.random.nextInt() % 101) / 100.0f);
            drawingObject.randomOffsetY = (imageHeight / 4.0f) * ((this.random.nextInt() % 101) / 100.0f);
        }
        drawingObject.messageId = chatActionCell.getMessageObject().getId();
        drawingObject.isOut = true;
        drawingObject.imageReceiver.setAllowStartAnimation(true);
        int filterWidth = getFilterWidth();
        if (i2 > 0) {
            Integer num = (Integer) this.lastAnimationIndex.get(Long.valueOf(document.id));
            int intValue = num == null ? 0 : num.intValue();
            this.lastAnimationIndex.put(Long.valueOf(document.id), Integer.valueOf((intValue + 1) % 4));
            drawingObject.imageReceiver.setUniqKeyPrefix(intValue + "_" + drawingObject.messageId + "_");
        }
        drawingObject.document = document;
        drawingObject.imageReceiver.setImage(ImageLocation.getForDocument(videoSize, document), filterWidth + "_" + filterWidth, null, "tgs", this.set, 1);
        drawingObject.imageReceiver.setLayerNum(Integer.MAX_VALUE);
        drawingObject.imageReceiver.setAutoRepeat(0);
        if (drawingObject.imageReceiver.getLottieAnimation() != null) {
            if (drawingObject.isPremiumSticker) {
                drawingObject.imageReceiver.getLottieAnimation().setCurrentFrame(0, false, true);
            }
            drawingObject.imageReceiver.getLottieAnimation().start();
        }
        this.drawingObjects.add(drawingObject);
        if (this.attached) {
            drawingObject.imageReceiver.onAttachedToWindow();
            drawingObject.imageReceiver.setParentView(this.contentLayout);
        }
        this.contentLayout.invalidate();
        return true;
    }

    public boolean showAnimationForCell(ChatMessageCell chatMessageCell, int i, boolean z, boolean z2) {
        if (chatMessageCell == null || this.drawingObjects.size() > 12) {
            return false;
        }
        MessageObject messageObject = chatMessageCell.getMessageObject();
        if (chatMessageCell.getEffect() == null && !chatMessageCell.getPhotoImage().hasNotThumb()) {
            return false;
        }
        String stickerEmoji = messageObject.getStickerEmoji();
        if (stickerEmoji == null) {
            stickerEmoji = messageObject.messageOwner.message;
        }
        if (chatMessageCell.getEffect() == null && stickerEmoji == null) {
            return false;
        }
        float imageHeight = chatMessageCell.getPhotoImage().getImageHeight();
        float imageWidth = chatMessageCell.getPhotoImage().getImageWidth();
        if (chatMessageCell.getEffect() != null || (imageHeight > 0.0f && imageWidth > 0.0f)) {
            return createDrawingObject(unwrapEmoji(stickerEmoji), chatMessageCell.getMessageObject().getId(), chatMessageCell.getMessageObject().getDocument(), messageObject, i, z, z2, imageWidth, imageHeight, chatMessageCell.getMessageObject().isOutOwner());
        }
        return false;
    }

    public boolean showAnimationForWidget(StoryReactionWidgetView storyReactionWidgetView) {
        float f;
        float f2;
        boolean z;
        int i;
        float f3;
        boolean z2;
        float f4;
        float f5;
        if (this.drawingObjects.size() > 12) {
            return false;
        }
        ReactionsLayoutInBubble.VisibleReaction fromTL = ReactionsLayoutInBubble.VisibleReaction.fromTL(storyReactionWidgetView.mediaArea.reaction);
        String str = fromTL.emojicon;
        if (str == null) {
            str = MessageObject.findAnimatedEmojiEmoticon(AnimatedEmojiDrawable.findDocument(this.currentAccount, fromTL.documentId));
        }
        float measuredHeight = storyReactionWidgetView.getMeasuredHeight();
        float measuredWidth = storyReactionWidgetView.getMeasuredWidth();
        View view = (View) storyReactionWidgetView.getParent();
        if (measuredWidth > view.getWidth() * 0.5f) {
            f2 = view.getWidth() * 0.4f;
            f = f2;
        } else {
            f = measuredHeight;
            f2 = measuredWidth;
        }
        String unwrapEmoji = unwrapEmoji(str);
        int hashCode = storyReactionWidgetView.hashCode();
        boolean z3 = storyReactionWidgetView.getTranslationX() > ((float) this.contentLayout.getMeasuredWidth()) / 2.0f;
        if (fromTL.emojicon != null) {
            z = z3;
            z2 = true;
            i = hashCode;
            float f6 = f2;
            if (createDrawingObject(unwrapEmoji, hashCode, null, null, -1, false, false, f2, f, z)) {
                if (!this.drawingObjects.isEmpty()) {
                    ArrayList arrayList = this.drawingObjects;
                    DrawingObject drawingObject = (DrawingObject) arrayList.get(arrayList.size() - 1);
                    drawingObject.isReaction = true;
                    drawingObject.lastH = f;
                    drawingObject.lastW = f6;
                    drawingObject.lastX = storyReactionWidgetView.getTranslationX() - (drawingObject.lastW / 2.0f);
                    float translationY = storyReactionWidgetView.getTranslationY();
                    float f7 = drawingObject.lastW;
                    drawingObject.lastY = translationY - (1.5f * f7);
                    if (drawingObject.isOut) {
                        f4 = drawingObject.lastX;
                        f5 = (-f7) * 1.8f;
                    } else {
                        f4 = drawingObject.lastX;
                        f5 = (-f7) * 0.2f;
                    }
                    drawingObject.lastX = f4 + f5;
                }
                return true;
            }
            f3 = f6;
        } else {
            z = z3;
            i = hashCode;
            f3 = f2;
            z2 = true;
        }
        if (fromTL.documentId == 0 || storyReactionWidgetView.getAnimatedEmojiDrawable() == null) {
            return false;
        }
        int i2 = 0;
        for (int i3 = 0; i3 < this.drawingObjects.size(); i3++) {
            if (((DrawingObject) this.drawingObjects.get(i3)).documentId == fromTL.documentId) {
                i2++;
            }
        }
        if (i2 >= 4) {
            return false;
        }
        DrawingObject drawingObject2 = new DrawingObject();
        drawingObject2.genericEffect = AnimatedEmojiEffect.createFrom(storyReactionWidgetView.getAnimatedEmojiDrawable(), z2, z2);
        if (!drawingObject2.isMessageEffect) {
            drawingObject2.randomOffsetX = (f3 / 4.0f) * ((this.random.nextInt() % 101) / 100.0f);
            drawingObject2.randomOffsetY = (f / 4.0f) * ((this.random.nextInt() % 101) / 100.0f);
        }
        drawingObject2.messageId = i;
        drawingObject2.document = null;
        drawingObject2.documentId = fromTL.documentId;
        drawingObject2.isOut = z;
        drawingObject2.isReaction = z2;
        drawingObject2.lastH = f;
        drawingObject2.lastW = f3;
        drawingObject2.lastX = storyReactionWidgetView.getTranslationX() - (drawingObject2.lastW / 2.0f);
        float translationY2 = storyReactionWidgetView.getTranslationY();
        float f8 = drawingObject2.lastW;
        drawingObject2.lastY = translationY2 - (1.5f * f8);
        drawingObject2.lastX += (-f8) * 1.8f;
        if (this.attached) {
            drawingObject2.genericEffect.setView(this.contentLayout);
        }
        this.drawingObjects.add(drawingObject2);
        return z2;
    }

    public boolean supports(String str) {
        return this.emojiInteractionsStickersMap.containsKey(unwrapEmoji(str));
    }
}
