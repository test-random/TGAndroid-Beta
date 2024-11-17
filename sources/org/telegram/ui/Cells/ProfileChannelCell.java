package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.ProfileChannelCell;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.ClickableAnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LoadingDrawable;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesListPlaceProvider;

public abstract class ProfileChannelCell extends FrameLayout {
    public final DialogCell dialogCell;
    private final TextView headerView;
    private boolean loading;
    private AnimatedFloat loadingAlpha;
    private final LoadingDrawable loadingDrawable;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean set;
    private final AnimatedTextView subscribersView;

    public static class ChannelMessageFetcher {
        private ArrayList callbacks = new ArrayList();
        public long channel_id;
        public final int currentAccount;
        public boolean error;
        public boolean loaded;
        public boolean loading;
        public MessageObject messageObject;
        public int message_id;
        private int searchId;

        public ChannelMessageFetcher(int i) {
            this.currentAccount = i;
        }

        private void done(boolean z) {
            this.loading = false;
            this.loaded = true;
            this.error = z;
            Iterator it = this.callbacks.iterator();
            while (it.hasNext()) {
                ((Runnable) it.next()).run();
            }
            this.callbacks.clear();
        }

        public void lambda$fetch$0(TLObject tLObject, MessagesStorage messagesStorage, long j, int i, int i2) {
            TLRPC.Message message;
            if (!(tLObject instanceof TLRPC.messages_Messages)) {
                if (i != this.searchId) {
                    return;
                }
                done(true);
                return;
            }
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(messages_messages.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(messages_messages.chats, false);
            messagesStorage.putUsersAndChats(messages_messages.users, messages_messages.chats, true, true);
            messagesStorage.putMessages(messages_messages, -j, -1, 0, false, 0, 0L);
            if (i != this.searchId) {
                return;
            }
            Iterator<TLRPC.Message> it = messages_messages.messages.iterator();
            while (true) {
                if (!it.hasNext()) {
                    message = null;
                    break;
                } else {
                    message = it.next();
                    if (message.id == i2) {
                        break;
                    }
                }
            }
            if (message != null) {
                if (message instanceof TLRPC.TL_messageEmpty) {
                    this.messageObject = null;
                } else {
                    this.messageObject = new MessageObject(this.currentAccount, message, true, true);
                }
                done(false);
            }
        }

        public void lambda$fetch$1(final MessagesStorage messagesStorage, final long j, final int i, final int i2, final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ProfileChannelCell.ChannelMessageFetcher.this.lambda$fetch$0(tLObject, messagesStorage, j, i, i2);
                }
            });
        }

        public void lambda$fetch$2(final int i, TLRPC.Message message, final long j, final int i2, final MessagesStorage messagesStorage) {
            if (i != this.searchId) {
                return;
            }
            MessageObject messageObject = message != null ? new MessageObject(this.currentAccount, message, true, true) : null;
            if (messageObject != null) {
                this.messageObject = messageObject;
                done(false);
            } else {
                TLRPC.TL_channels_getMessages tL_channels_getMessages = new TLRPC.TL_channels_getMessages();
                tL_channels_getMessages.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(j);
                tL_channels_getMessages.id.add(Integer.valueOf(i2));
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getMessages, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        ProfileChannelCell.ChannelMessageFetcher.this.lambda$fetch$1(messagesStorage, j, i, i2, tLObject, tL_error);
                    }
                });
            }
        }

        public void lambda$fetch$3(final int i, final MessagesStorage messagesStorage, final long j, long j2, final int i2) {
            TLRPC.Message message;
            SQLiteCursor sQLiteCursor;
            NativeByteBuffer byteBufferValue;
            ArrayList<TLRPC.User> arrayList = new ArrayList<>();
            ArrayList<TLRPC.Chat> arrayList2 = new ArrayList<>();
            SQLiteCursor sQLiteCursor2 = null;
            r4 = null;
            r4 = null;
            final TLRPC.Message message2 = null;
            sQLiteCursor2 = null;
            try {
                try {
                    sQLiteCursor = i <= 0 ? messagesStorage.getDatabase().queryFinalized("SELECT data, mid FROM messages_v2 WHERE uid = ? ORDER BY mid DESC LIMIT 1", Long.valueOf(-j)) : messagesStorage.getDatabase().queryFinalized("SELECT data, mid FROM messages_v2 WHERE uid = ? AND mid = ? LIMIT 1", Long.valueOf(-j), Integer.valueOf(i));
                    try {
                        try {
                            ArrayList<Long> arrayList3 = new ArrayList<>();
                            ArrayList arrayList4 = new ArrayList();
                            if (sQLiteCursor.next() && (byteBufferValue = sQLiteCursor.byteBufferValue(0)) != null) {
                                message = TLRPC.Message.TLdeserialize(byteBufferValue, byteBufferValue.readInt32(false), false);
                                try {
                                    message.readAttachPath(byteBufferValue, j2);
                                    byteBufferValue.reuse();
                                    message.id = sQLiteCursor.intValue(1);
                                    message.dialog_id = -j;
                                    MessagesStorage.addUsersAndChatsFromMessage(message, arrayList3, arrayList4, null);
                                    message2 = message;
                                } catch (Exception e) {
                                    e = e;
                                    sQLiteCursor2 = sQLiteCursor;
                                    FileLog.e(e);
                                    if (sQLiteCursor2 == null) {
                                        message2 = message;
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            @Override
                                            public final void run() {
                                                ProfileChannelCell.ChannelMessageFetcher.this.lambda$fetch$2(i2, message2, j, i, messagesStorage);
                                            }
                                        });
                                    } else {
                                        sQLiteCursor = sQLiteCursor2;
                                        message2 = message;
                                        sQLiteCursor.dispose();
                                        AndroidUtilities.runOnUIThread(new Runnable() {
                                            @Override
                                            public final void run() {
                                                ProfileChannelCell.ChannelMessageFetcher.this.lambda$fetch$2(i2, message2, j, i, messagesStorage);
                                            }
                                        });
                                    }
                                }
                            }
                            sQLiteCursor.dispose();
                            if (message2 != null) {
                                if (!arrayList3.isEmpty()) {
                                    messagesStorage.getUsersInternal(arrayList3, arrayList);
                                }
                                if (!arrayList4.isEmpty()) {
                                    messagesStorage.getChatsInternal(TextUtils.join(",", arrayList4), arrayList2);
                                }
                            }
                        } catch (Throwable th) {
                            th = th;
                            sQLiteCursor2 = sQLiteCursor;
                            if (sQLiteCursor2 != null) {
                                sQLiteCursor2.dispose();
                            }
                            throw th;
                        }
                    } catch (Exception e2) {
                        e = e2;
                        message = message2;
                    }
                } catch (Exception e3) {
                    e = e3;
                    message = null;
                }
                sQLiteCursor.dispose();
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        ProfileChannelCell.ChannelMessageFetcher.this.lambda$fetch$2(i2, message2, j, i, messagesStorage);
                    }
                });
            } catch (Throwable th2) {
                th = th2;
            }
        }

        public void fetch(final long j, final int i) {
            if (this.loaded || this.loading) {
                if (this.channel_id == j && this.message_id == i) {
                    return;
                }
                this.loaded = false;
                this.messageObject = null;
            }
            final int i2 = this.searchId + 1;
            this.searchId = i2;
            this.loading = true;
            this.channel_id = j;
            this.message_id = i;
            final long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
            final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
            messagesStorage.getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    ProfileChannelCell.ChannelMessageFetcher.this.lambda$fetch$3(i, messagesStorage, j, clientUserId, i2);
                }
            });
        }

        public void fetch(TLRPC.UserFull userFull) {
            if (userFull != null && (userFull.flags2 & 64) != 0) {
                fetch(userFull.personal_channel_id, userFull.personal_channel_message);
                return;
            }
            this.searchId++;
            this.loaded = true;
            this.messageObject = null;
            done(false);
        }

        public void subscribe(Runnable runnable) {
            if (this.loaded) {
                runnable.run();
            } else {
                this.callbacks.add(runnable);
            }
        }
    }

    public ProfileChannelCell(final BaseFragment baseFragment) {
        super(baseFragment.getContext());
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.loadingAlpha = new AnimatedFloat(320L, cubicBezierInterpolator);
        this.set = false;
        final Context context = baseFragment.getContext();
        Theme.ResourcesProvider resourceProvider = baseFragment.getResourceProvider();
        this.resourcesProvider = resourceProvider;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 55, 22.0f, 16.6f, 22.0f, 0.0f));
        TextView textView = new TextView(context);
        this.headerView = textView;
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextSize(1, 15.0f);
        textView.setText(LocaleController.getString(R.string.ProfileChannel));
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 51));
        ClickableAnimatedTextView clickableAnimatedTextView = new ClickableAnimatedTextView(context);
        this.subscribersView = clickableAnimatedTextView;
        clickableAnimatedTextView.getDrawable().setHacks(true, true, true);
        clickableAnimatedTextView.setAnimationProperties(0.3f, 0L, 165L, cubicBezierInterpolator);
        clickableAnimatedTextView.setTypeface(AndroidUtilities.bold());
        clickableAnimatedTextView.setTextSize(AndroidUtilities.dp(11.0f));
        clickableAnimatedTextView.setPadding(AndroidUtilities.dp(4.33f), 0, AndroidUtilities.dp(4.33f), 0);
        clickableAnimatedTextView.setGravity(3);
        linearLayout.addView(clickableAnimatedTextView, LayoutHelper.createLinear(-1, 17, 51, 4, 2, 4, 0));
        DialogCell dialogCell = new DialogCell(null, context, false, true, UserConfig.selectedAccount, resourceProvider);
        this.dialogCell = dialogCell;
        dialogCell.setBackgroundColor(0);
        dialogCell.setDialogCellDelegate(new DialogCell.DialogCellDelegate() {
            @Override
            public boolean canClickButtonInside() {
                return true;
            }

            @Override
            public void onButtonClicked(DialogCell dialogCell2) {
            }

            @Override
            public void onButtonLongPress(DialogCell dialogCell2) {
            }

            @Override
            public void openHiddenStories() {
                StoriesController storiesController = baseFragment.getMessagesController().getStoriesController();
                if (storiesController.getHiddenList().isEmpty()) {
                    return;
                }
                boolean z = storiesController.getUnreadState(DialogObject.getPeerDialogId(((TL_stories.PeerStories) storiesController.getHiddenList().get(0)).peer)) != 0;
                ArrayList arrayList = new ArrayList();
                for (int i = 0; i < storiesController.getHiddenList().size(); i++) {
                    long peerDialogId = DialogObject.getPeerDialogId(((TL_stories.PeerStories) storiesController.getHiddenList().get(i)).peer);
                    if (!z || storiesController.getUnreadState(peerDialogId) != 0) {
                        arrayList.add(Long.valueOf(peerDialogId));
                    }
                }
                baseFragment.getOrCreateStoryViewer().open(context, null, arrayList, 0, null, null, StoriesListPlaceProvider.of(ProfileChannelCell.this), false);
            }

            @Override
            public void openStory(DialogCell dialogCell2, Runnable runnable) {
                if (baseFragment.getMessagesController().getStoriesController().hasStories(dialogCell2.getDialogId())) {
                    baseFragment.getOrCreateStoryViewer().doOnAnimationReady(runnable);
                    baseFragment.getOrCreateStoryViewer().open(baseFragment.getContext(), dialogCell2.getDialogId(), StoriesListPlaceProvider.of(ProfileChannelCell.this));
                }
            }

            @Override
            public void showChatPreview(DialogCell dialogCell2) {
            }
        });
        dialogCell.avatarStart = 15;
        dialogCell.messagePaddingStart = 83;
        addView(dialogCell, LayoutHelper.createFrame(-1, -2, 87));
        updateColors();
        setWillNotDraw(false);
        LoadingDrawable loadingDrawable = new LoadingDrawable();
        this.loadingDrawable = loadingDrawable;
        int i = Theme.key_listSelector;
        loadingDrawable.setColors(Theme.multAlpha(Theme.getColor(i, resourceProvider), 1.25f), Theme.multAlpha(Theme.getColor(i, resourceProvider), 0.8f));
        loadingDrawable.setRadiiDp(8.0f);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        float f = this.loadingAlpha.set(this.loading);
        if (f > 0.0f) {
            this.loadingDrawable.setAlpha((int) (f * 255.0f));
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(this.dialogCell.getX() + AndroidUtilities.dp(this.dialogCell.messagePaddingStart + 6), this.dialogCell.getY() + AndroidUtilities.dp(38.0f), this.dialogCell.getX() + AndroidUtilities.dp(this.dialogCell.messagePaddingStart + 6) + (getWidth() * 0.5f), this.dialogCell.getY() + AndroidUtilities.dp(46.33f));
            this.loadingDrawable.setBounds(rectF);
            this.loadingDrawable.draw(canvas);
            rectF.set(this.dialogCell.getX() + AndroidUtilities.dp(this.dialogCell.messagePaddingStart + 6), this.dialogCell.getY() + AndroidUtilities.dp(56.0f), this.dialogCell.getX() + AndroidUtilities.dp(this.dialogCell.messagePaddingStart + 6) + (getWidth() * 0.36f), this.dialogCell.getY() + AndroidUtilities.dp(64.33f));
            this.loadingDrawable.setBounds(rectF);
            this.loadingDrawable.draw(canvas);
            rectF.set(((this.dialogCell.getX() + this.dialogCell.getWidth()) - AndroidUtilities.dp(16.0f)) - AndroidUtilities.dp(43.0f), this.dialogCell.getY() + AndroidUtilities.dp(12.0f), (this.dialogCell.getX() + this.dialogCell.getWidth()) - AndroidUtilities.dp(16.0f), this.dialogCell.getY() + AndroidUtilities.dp(20.33f));
            this.loadingDrawable.setBounds(rectF);
            this.loadingDrawable.draw(canvas);
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(115.66f), 1073741824));
    }

    public abstract int processColor(int i);

    public void set(TLRPC.Chat chat, MessageObject messageObject) {
        String formatShortNumber;
        DialogCell dialogCell;
        long j;
        int i;
        boolean z;
        MessageObject messageObject2;
        boolean z2 = this.set;
        boolean z3 = chat == null || chat.participants_count > 0;
        this.subscribersView.cancelAnimation();
        this.subscribersView.setPivotX(0.0f);
        AnimatedTextView animatedTextView = this.subscribersView;
        if (z2) {
            animatedTextView.animate().alpha(z3 ? 1.0f : 0.0f).scaleX(z3 ? 1.0f : 0.8f).scaleY(z3 ? 1.0f : 0.8f).setDuration(420L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
        } else {
            animatedTextView.setAlpha(z3 ? 1.0f : 0.0f);
            this.subscribersView.setScaleX(z3 ? 1.0f : 0.0f);
            this.subscribersView.setScaleY(z3 ? 1.0f : 0.0f);
        }
        if (chat != null) {
            int[] iArr = new int[1];
            if (AndroidUtilities.isAccessibilityScreenReaderEnabled()) {
                int i2 = chat.participants_count;
                iArr[0] = i2;
                formatShortNumber = String.valueOf(i2);
            } else {
                formatShortNumber = LocaleController.formatShortNumber(chat.participants_count, iArr);
            }
            this.subscribersView.setText(LocaleController.formatPluralString("Subscribers", iArr[0], new Object[0]).replace(String.format("%d", Integer.valueOf(iArr[0])), formatShortNumber), true);
            boolean z4 = messageObject == null;
            this.loading = z4;
            if (z4) {
                dialogCell = this.dialogCell;
                j = -chat.id;
                i = 0;
                z = false;
                messageObject2 = null;
            } else {
                dialogCell = this.dialogCell;
                j = -chat.id;
                i = messageObject.messageOwner.date;
                z = false;
                messageObject2 = messageObject;
            }
            dialogCell.setDialog(j, messageObject2, i, z, z2);
        }
        if (!z2) {
            this.loadingAlpha.set(this.loading, true);
        }
        invalidate();
        this.set = true;
    }

    public void updateColors() {
        int processColor = processColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader, this.resourcesProvider));
        this.subscribersView.setTextColor(processColor);
        this.subscribersView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(4.5f), AndroidUtilities.dp(4.5f), Theme.multAlpha(processColor, 0.1f)));
        this.headerView.setTextColor(processColor);
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        return this.loadingDrawable == drawable || super.verifyDrawable(drawable);
    }
}
