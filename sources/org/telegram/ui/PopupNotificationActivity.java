package org.telegram.ui;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PlayingGameDrawable;
import org.telegram.ui.Components.PopupAudioView;
import org.telegram.ui.Components.RecordStatusDrawable;
import org.telegram.ui.Components.RoundStatusDrawable;
import org.telegram.ui.Components.SendingFileDrawable;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StatusDrawable;
import org.telegram.ui.Components.TypingDotsDrawable;

public class PopupNotificationActivity extends Activity implements NotificationCenter.NotificationCenterDelegate {
    private ActionBar actionBar;
    private FrameLayout avatarContainer;
    private BackupImageView avatarImageView;
    private ViewGroup centerButtonsView;
    private ViewGroup centerView;
    private ChatActivityEnterView chatActivityEnterView;
    private int classGuid;
    private TextView countText;
    private TLRPC.Chat currentChat;
    private TLRPC.User currentUser;
    private boolean isReply;
    private CharSequence lastPrintString;
    private ViewGroup leftButtonsView;
    private ViewGroup leftView;
    private ViewGroup messageContainer;
    private TextView nameTextView;
    private TextView onlineTextView;
    private RelativeLayout popupContainer;
    private ViewGroup rightButtonsView;
    private ViewGroup rightView;
    private ArrayList textViews = new ArrayList();
    private ArrayList imageViews = new ArrayList();
    private ArrayList audioViews = new ArrayList();
    private VelocityTracker velocityTracker = null;
    private StatusDrawable[] statusDrawables = new StatusDrawable[5];
    private int lastResumedAccount = -1;
    private boolean finished = false;
    private MessageObject currentMessageObject = null;
    private MessageObject[] setMessageObjects = new MessageObject[3];
    private int currentMessageNum = 0;
    private PowerManager.WakeLock wakeLock = null;
    private boolean animationInProgress = false;
    private long animationStartTime = 0;
    private float moveStartX = -1.0f;
    private boolean startedMoving = false;
    private Runnable onAnimationEndRunnable = null;
    private ArrayList popupMessages = new ArrayList();

    private class FrameLayoutTouch extends FrameLayout {
        public FrameLayoutTouch(Context context) {
            super(context);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(motionEvent);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(motionEvent);
        }

        @Override
        public void requestDisallowInterceptTouchEvent(boolean z) {
            ((PopupNotificationActivity) getContext()).onTouchEventMy(null);
            super.requestDisallowInterceptTouchEvent(z);
        }
    }

    public void applyViewsLayoutParams(int i) {
        int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        ViewGroup viewGroup = this.leftView;
        if (viewGroup != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
            if (layoutParams.width != dp) {
                layoutParams.width = dp;
                this.leftView.setLayoutParams(layoutParams);
            }
            this.leftView.setTranslationX((-dp) + i);
        }
        ViewGroup viewGroup2 = this.leftButtonsView;
        if (viewGroup2 != null) {
            viewGroup2.setTranslationX((-dp) + i);
        }
        ViewGroup viewGroup3 = this.centerView;
        if (viewGroup3 != null) {
            FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) viewGroup3.getLayoutParams();
            if (layoutParams2.width != dp) {
                layoutParams2.width = dp;
                this.centerView.setLayoutParams(layoutParams2);
            }
            this.centerView.setTranslationX(i);
        }
        ViewGroup viewGroup4 = this.centerButtonsView;
        if (viewGroup4 != null) {
            viewGroup4.setTranslationX(i);
        }
        ViewGroup viewGroup5 = this.rightView;
        if (viewGroup5 != null) {
            FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) viewGroup5.getLayoutParams();
            if (layoutParams3.width != dp) {
                layoutParams3.width = dp;
                this.rightView.setLayoutParams(layoutParams3);
            }
            this.rightView.setTranslationX(dp + i);
        }
        ViewGroup viewGroup6 = this.rightButtonsView;
        if (viewGroup6 != null) {
            viewGroup6.setTranslationX(dp + i);
        }
        this.messageContainer.invalidate();
    }

    private void checkAndUpdateAvatar() {
        TLRPC.User user;
        AvatarDrawable avatarDrawable;
        TLRPC.User user2;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        if (this.currentChat != null) {
            TLRPC.Chat chat = MessagesController.getInstance(messageObject.currentAccount).getChat(Long.valueOf(this.currentChat.id));
            if (chat == 0) {
                return;
            }
            this.currentChat = chat;
            if (this.avatarImageView == null) {
                return;
            }
            avatarDrawable = new AvatarDrawable(this.currentChat);
            user2 = chat;
        } else {
            if (this.currentUser == null || (user = MessagesController.getInstance(messageObject.currentAccount).getUser(Long.valueOf(this.currentUser.id))) == null) {
                return;
            }
            this.currentUser = user;
            if (this.avatarImageView == null) {
                return;
            }
            avatarDrawable = new AvatarDrawable(this.currentUser);
            user2 = user;
        }
        this.avatarImageView.setForUserOrChat(user2, avatarDrawable);
    }

    private void fixLayout() {
        FrameLayout frameLayout = this.avatarContainer;
        if (frameLayout != null) {
            frameLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (PopupNotificationActivity.this.avatarContainer != null) {
                        PopupNotificationActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    int currentActionBarHeight = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0f)) / 2;
                    PopupNotificationActivity.this.avatarContainer.setPadding(PopupNotificationActivity.this.avatarContainer.getPaddingLeft(), currentActionBarHeight, PopupNotificationActivity.this.avatarContainer.getPaddingRight(), currentActionBarHeight);
                    return true;
                }
            });
        }
        ViewGroup viewGroup = this.messageContainer;
        if (viewGroup != null) {
            viewGroup.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    PopupNotificationActivity.this.messageContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (PopupNotificationActivity.this.checkTransitionAnimation() || PopupNotificationActivity.this.startedMoving) {
                        return true;
                    }
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) PopupNotificationActivity.this.messageContainer.getLayoutParams();
                    marginLayoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
                    marginLayoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
                    marginLayoutParams.width = -1;
                    marginLayoutParams.height = -1;
                    PopupNotificationActivity.this.messageContainer.setLayoutParams(marginLayoutParams);
                    PopupNotificationActivity.this.applyViewsLayoutParams(0);
                    return true;
                }
            });
        }
    }

    private LinearLayout getButtonsViewForMessage(int i, boolean z) {
        int i2;
        float f;
        int i3 = i;
        LinearLayout linearLayout = null;
        if (this.popupMessages.size() == 1 && (i3 < 0 || i3 >= this.popupMessages.size())) {
            return null;
        }
        int i4 = 0;
        if (i3 == -1) {
            i3 = this.popupMessages.size() - 1;
        } else if (i3 == this.popupMessages.size()) {
            i3 = 0;
        }
        final MessageObject messageObject = (MessageObject) this.popupMessages.get(i3);
        TLRPC.ReplyMarkup replyMarkup = messageObject.messageOwner.reply_markup;
        if (messageObject.getDialogId() != 777000 || replyMarkup == null) {
            i2 = 0;
        } else {
            ArrayList<TLRPC.TL_keyboardButtonRow> arrayList = replyMarkup.rows;
            int size = arrayList.size();
            i2 = 0;
            for (int i5 = 0; i5 < size; i5++) {
                TLRPC.TL_keyboardButtonRow tL_keyboardButtonRow = arrayList.get(i5);
                int size2 = tL_keyboardButtonRow.buttons.size();
                for (int i6 = 0; i6 < size2; i6++) {
                    if (tL_keyboardButtonRow.buttons.get(i6) instanceof TLRPC.TL_keyboardButtonCallback) {
                        i2++;
                    }
                }
            }
        }
        final int i7 = messageObject.currentAccount;
        if (i2 > 0) {
            ArrayList<TLRPC.TL_keyboardButtonRow> arrayList2 = replyMarkup.rows;
            int size3 = arrayList2.size();
            int i8 = 0;
            while (i8 < size3) {
                TLRPC.TL_keyboardButtonRow tL_keyboardButtonRow2 = arrayList2.get(i8);
                int size4 = tL_keyboardButtonRow2.buttons.size();
                int i9 = 0;
                while (i9 < size4) {
                    TLRPC.KeyboardButton keyboardButton = tL_keyboardButtonRow2.buttons.get(i9);
                    if (keyboardButton instanceof TLRPC.TL_keyboardButtonCallback) {
                        if (linearLayout == null) {
                            linearLayout = new LinearLayout(this);
                            linearLayout.setOrientation(i4);
                            linearLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                            linearLayout.setWeightSum(100.0f);
                            linearLayout.setTag("b");
                            linearLayout.setOnTouchListener(new View.OnTouchListener() {
                                @Override
                                public final boolean onTouch(View view, MotionEvent motionEvent) {
                                    boolean lambda$getButtonsViewForMessage$4;
                                    lambda$getButtonsViewForMessage$4 = PopupNotificationActivity.lambda$getButtonsViewForMessage$4(view, motionEvent);
                                    return lambda$getButtonsViewForMessage$4;
                                }
                            });
                        }
                        TextView textView = new TextView(this);
                        textView.setTextSize(1, 16.0f);
                        textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
                        textView.setTypeface(AndroidUtilities.bold());
                        textView.setText(keyboardButton.text.toUpperCase());
                        textView.setTag(keyboardButton);
                        textView.setGravity(17);
                        textView.setBackgroundDrawable(Theme.getSelectorDrawable(true));
                        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -1, 100.0f / i2));
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public final void onClick(View view) {
                                PopupNotificationActivity.lambda$getButtonsViewForMessage$5(i7, messageObject, view);
                            }
                        });
                    }
                    i9++;
                    i4 = 0;
                }
                i8++;
                i4 = 0;
            }
        }
        if (linearLayout != null) {
            int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(-1, -2);
            layoutParams.addRule(12);
            if (z) {
                int i10 = this.currentMessageNum;
                if (i3 == i10) {
                    f = 0.0f;
                } else if (i3 == i10 - 1) {
                    f = -dp;
                } else if (i3 == i10 + 1) {
                    f = dp;
                }
                linearLayout.setTranslationX(f);
            }
            this.popupContainer.addView(linearLayout, layoutParams);
        }
        return linearLayout;
    }

    public void getNewMessage() {
        int i;
        if (this.popupMessages.isEmpty()) {
            onFinish();
            finish();
            return;
        }
        if ((this.currentMessageNum != 0 || this.chatActivityEnterView.hasText() || this.startedMoving) && this.currentMessageObject != null) {
            int size = this.popupMessages.size();
            for (int i2 = 0; i2 < size; i2++) {
                MessageObject messageObject = (MessageObject) this.popupMessages.get(i2);
                if (messageObject.currentAccount == this.currentMessageObject.currentAccount && messageObject.getDialogId() == this.currentMessageObject.getDialogId() && messageObject.getId() == this.currentMessageObject.getId()) {
                    this.currentMessageNum = i2;
                    if (this.startedMoving) {
                        if (i2 != this.popupMessages.size() - 1) {
                            i = this.currentMessageNum == 1 ? 4 : 3;
                        }
                        prepareLayouts(i);
                    }
                    this.countText.setText(String.format("%d/%d", Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())));
                }
            }
        }
        this.currentMessageNum = 0;
        this.currentMessageObject = (MessageObject) this.popupMessages.get(0);
        updateInterfaceForCurrentMessage(0);
        this.countText.setText(String.format("%d/%d", Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())));
    }

    private ViewGroup getViewForMessage(int i, boolean z) {
        ViewGroup frameLayout;
        int i2;
        ViewGroup viewGroup;
        float f;
        ViewGroup viewGroup2;
        PopupAudioView popupAudioView;
        ViewGroup viewGroup3;
        int i3 = i;
        if (this.popupMessages.size() == 1 && (i3 < 0 || i3 >= this.popupMessages.size())) {
            return null;
        }
        if (i3 == -1) {
            i3 = this.popupMessages.size() - 1;
        } else if (i3 == this.popupMessages.size()) {
            i3 = 0;
        }
        MessageObject messageObject = (MessageObject) this.popupMessages.get(i3);
        int i4 = messageObject.type;
        if ((i4 == 1 || i4 == 4) && !messageObject.isSecretMedia()) {
            if (this.imageViews.size() > 0) {
                frameLayout = (ViewGroup) this.imageViews.get(0);
                this.imageViews.remove(0);
            } else {
                frameLayout = new FrameLayout(this);
                FrameLayout frameLayout2 = new FrameLayout(this);
                frameLayout2.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
                frameLayout2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, -1.0f));
                BackupImageView backupImageView = new BackupImageView(this);
                backupImageView.setTag(311);
                frameLayout2.addView(backupImageView, LayoutHelper.createFrame(-1, -1.0f));
                TextView textView = new TextView(this);
                textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                textView.setTextSize(1, 16.0f);
                textView.setGravity(17);
                textView.setTag(312);
                frameLayout2.addView(textView, LayoutHelper.createFrame(-1, -2, 17));
                frameLayout.setTag(2);
                frameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        PopupNotificationActivity.this.lambda$getViewForMessage$6(view);
                    }
                });
            }
            ViewGroup viewGroup4 = frameLayout;
            TextView textView2 = (TextView) viewGroup4.findViewWithTag(312);
            BackupImageView backupImageView2 = (BackupImageView) viewGroup4.findViewWithTag(311);
            backupImageView2.setAspectFit(true);
            int i5 = messageObject.type;
            if (i5 == 1) {
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100);
                if (closestPhotoSizeWithSize != null) {
                    boolean z2 = messageObject.type != 1 || FileLoader.getInstance(UserConfig.selectedAccount).getPathToMessage(messageObject.messageOwner).exists();
                    if (!messageObject.needDrawBluredPreview()) {
                        if (z2 || DownloadController.getInstance(messageObject.currentAccount).canDownloadMedia(messageObject)) {
                            i2 = 8;
                            backupImageView2.setImage(ImageLocation.getForObject(closestPhotoSizeWithSize, messageObject.photoThumbsObject), "100_100", ImageLocation.getForObject(closestPhotoSizeWithSize2, messageObject.photoThumbsObject), "100_100_b", closestPhotoSizeWithSize.size, messageObject);
                        } else if (closestPhotoSizeWithSize2 != null) {
                            backupImageView2.setImage(ImageLocation.getForObject(closestPhotoSizeWithSize2, messageObject.photoThumbsObject), "100_100_b", (String) null, (Drawable) null, messageObject);
                            i2 = 8;
                        }
                        backupImageView2.setVisibility(0);
                        textView2.setVisibility(i2);
                        viewGroup = viewGroup4;
                    }
                }
                backupImageView2.setVisibility(8);
                textView2.setVisibility(0);
                textView2.setTextSize(2, SharedConfig.fontSize);
                textView2.setText(messageObject.messageText);
                viewGroup = viewGroup4;
            } else {
                viewGroup = viewGroup4;
                if (i5 == 4) {
                    textView2.setVisibility(8);
                    textView2.setText(messageObject.messageText);
                    backupImageView2.setVisibility(0);
                    TLRPC.GeoPoint geoPoint = messageObject.messageOwner.media.geo;
                    double d = geoPoint.lat;
                    double d2 = geoPoint._long;
                    if (MessagesController.getInstance(messageObject.currentAccount).mapProvider == 2) {
                        backupImageView2.setImage(ImageLocation.getForWebFile(WebFile.createWithGeoPoint(geoPoint, 100, 100, 15, Math.min(2, (int) Math.ceil(AndroidUtilities.density)))), (String) null, (String) null, (Drawable) null, messageObject);
                        viewGroup = viewGroup4;
                    } else {
                        backupImageView2.setImage(AndroidUtilities.formapMapUrl(messageObject.currentAccount, d, d2, 100, 100, true, 15, -1), null, null);
                        viewGroup = viewGroup4;
                    }
                }
            }
        } else if (messageObject.type == 2) {
            if (this.audioViews.size() > 0) {
                ViewGroup viewGroup5 = (ViewGroup) this.audioViews.get(0);
                this.audioViews.remove(0);
                popupAudioView = (PopupAudioView) viewGroup5.findViewWithTag(300);
                viewGroup3 = viewGroup5;
            } else {
                FrameLayout frameLayout3 = new FrameLayout(this);
                FrameLayout frameLayout4 = new FrameLayout(this);
                frameLayout4.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
                frameLayout4.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                frameLayout3.addView(frameLayout4, LayoutHelper.createFrame(-1, -1.0f));
                FrameLayout frameLayout5 = new FrameLayout(this);
                frameLayout4.addView(frameLayout5, LayoutHelper.createFrame(-1, -2.0f, 17, 20.0f, 0.0f, 20.0f, 0.0f));
                PopupAudioView popupAudioView2 = new PopupAudioView(this);
                popupAudioView2.setTag(300);
                frameLayout5.addView(popupAudioView2);
                frameLayout3.setTag(3);
                frameLayout3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        PopupNotificationActivity.this.lambda$getViewForMessage$7(view);
                    }
                });
                popupAudioView = popupAudioView2;
                viewGroup3 = frameLayout3;
            }
            popupAudioView.setMessageObject(messageObject);
            viewGroup = viewGroup3;
            if (DownloadController.getInstance(messageObject.currentAccount).canDownloadMedia(messageObject)) {
                popupAudioView.downloadAudioIfNeed();
                viewGroup = viewGroup3;
            }
        } else {
            if (this.textViews.size() > 0) {
                ViewGroup viewGroup6 = (ViewGroup) this.textViews.get(0);
                this.textViews.remove(0);
                viewGroup2 = viewGroup6;
            } else {
                FrameLayout frameLayout6 = new FrameLayout(this);
                ScrollView scrollView = new ScrollView(this);
                scrollView.setFillViewport(true);
                frameLayout6.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(0);
                linearLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -2, 1));
                linearLayout.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
                linearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        PopupNotificationActivity.this.lambda$getViewForMessage$8(view);
                    }
                });
                TextView textView3 = new TextView(this);
                textView3.setTextSize(1, 16.0f);
                textView3.setTag(301);
                int i6 = Theme.key_windowBackgroundWhiteBlackText;
                textView3.setTextColor(Theme.getColor(i6));
                textView3.setLinkTextColor(Theme.getColor(i6));
                textView3.setGravity(17);
                linearLayout.addView(textView3, LayoutHelper.createLinear(-1, -2, 17));
                frameLayout6.setTag(1);
                viewGroup2 = frameLayout6;
            }
            TextView textView4 = (TextView) viewGroup2.findViewWithTag(301);
            textView4.setTextSize(2, SharedConfig.fontSize);
            textView4.setText(messageObject.messageText);
            viewGroup = viewGroup2;
        }
        if (viewGroup.getParent() == null) {
            this.messageContainer.addView(viewGroup);
        }
        viewGroup.setVisibility(0);
        if (z) {
            int dp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewGroup.getLayoutParams();
            layoutParams.gravity = 51;
            layoutParams.height = -1;
            layoutParams.width = dp;
            int i7 = this.currentMessageNum;
            if (i3 == i7) {
                f = 0.0f;
            } else if (i3 == i7 - 1) {
                f = -dp;
            } else {
                if (i3 == i7 + 1) {
                    f = dp;
                }
                viewGroup.setLayoutParams(layoutParams);
                viewGroup.invalidate();
            }
            viewGroup.setTranslationX(f);
            viewGroup.setLayoutParams(layoutParams);
            viewGroup.invalidate();
        }
        return viewGroup;
    }

    private void handleIntent(Intent intent) {
        this.isReply = intent != null && intent.getBooleanExtra("force", false);
        this.popupMessages.clear();
        if (this.isReply) {
            int intExtra = intent != null ? intent.getIntExtra("currentAccount", UserConfig.selectedAccount) : UserConfig.selectedAccount;
            if (!UserConfig.isValidAccount(intExtra)) {
                return;
            } else {
                this.popupMessages.addAll(NotificationsController.getInstance(intExtra).popupReplyMessages);
            }
        } else {
            for (int i = 0; i < 4; i++) {
                if (UserConfig.getInstance(i).isClientActivated()) {
                    this.popupMessages.addAll(NotificationsController.getInstance(i).popupMessages);
                }
            }
        }
        if (((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode() || !ApplicationLoader.isScreenOn) {
            getWindow().addFlags(2623490);
        } else {
            getWindow().addFlags(2623488);
            getWindow().clearFlags(2);
        }
        if (this.currentMessageObject == null) {
            this.currentMessageNum = 0;
        }
        getNewMessage();
    }

    public static boolean lambda$getButtonsViewForMessage$4(View view, MotionEvent motionEvent) {
        return true;
    }

    public static void lambda$getButtonsViewForMessage$5(int i, MessageObject messageObject, View view) {
        TLRPC.KeyboardButton keyboardButton = (TLRPC.KeyboardButton) view.getTag();
        if (keyboardButton != null) {
            SendMessagesHelper.getInstance(i).sendNotificationCallback(messageObject.getDialogId(), messageObject.getId(), keyboardButton.data);
        }
    }

    public void lambda$getViewForMessage$6(View view) {
        openCurrentMessage();
    }

    public void lambda$getViewForMessage$7(View view) {
        openCurrentMessage();
    }

    public void lambda$getViewForMessage$8(View view) {
        openCurrentMessage();
    }

    public void lambda$onRequestPermissionsResult$0(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$onTouchEventMy$1() {
        this.animationInProgress = false;
        switchToPreviousMessage();
        AndroidUtilities.unlockOrientation(this);
    }

    public void lambda$onTouchEventMy$2() {
        this.animationInProgress = false;
        switchToNextMessage();
        AndroidUtilities.unlockOrientation(this);
    }

    public void lambda$onTouchEventMy$3() {
        this.animationInProgress = false;
        applyViewsLayoutParams(0);
        AndroidUtilities.unlockOrientation(this);
    }

    public void openCurrentMessage() {
        String str;
        if (this.currentMessageObject == null) {
            return;
        }
        Intent intent = new Intent(ApplicationLoader.applicationContext, (Class<?>) LaunchActivity.class);
        long dialogId = this.currentMessageObject.getDialogId();
        if (DialogObject.isEncryptedDialog(dialogId)) {
            intent.putExtra("encId", DialogObject.getEncryptedChatId(dialogId));
        } else {
            if (DialogObject.isUserDialog(dialogId)) {
                str = "userId";
            } else if (DialogObject.isChatDialog(dialogId)) {
                dialogId = -dialogId;
                str = "chatId";
            }
            intent.putExtra(str, dialogId);
        }
        intent.putExtra("currentAccount", this.currentMessageObject.currentAccount);
        intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
        intent.setFlags(32768);
        startActivity(intent);
        onFinish();
        finish();
    }

    private void prepareLayouts(int r6) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.prepareLayouts(int):void");
    }

    private void reuseButtonsView(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        this.popupContainer.removeView(viewGroup);
    }

    private void reuseView(ViewGroup viewGroup) {
        ArrayList arrayList;
        if (viewGroup == null) {
            return;
        }
        int intValue = ((Integer) viewGroup.getTag()).intValue();
        viewGroup.setVisibility(8);
        if (intValue == 1) {
            arrayList = this.textViews;
        } else if (intValue == 2) {
            arrayList = this.imageViews;
        } else if (intValue != 3) {
            return;
        } else {
            arrayList = this.audioViews;
        }
        arrayList.add(viewGroup);
    }

    private void setTypingAnimation(boolean z) {
        if (this.actionBar == null) {
            return;
        }
        int i = 0;
        if (z) {
            try {
                Integer printingStringType = MessagesController.getInstance(this.currentMessageObject.currentAccount).getPrintingStringType(this.currentMessageObject.getDialogId(), 0L);
                this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.statusDrawables[printingStringType.intValue()], (Drawable) null, (Drawable) null, (Drawable) null);
                this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                while (i < this.statusDrawables.length) {
                    if (i == printingStringType.intValue()) {
                        this.statusDrawables[i].start();
                    } else {
                        this.statusDrawables[i].stop();
                    }
                    i++;
                }
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
        this.onlineTextView.setCompoundDrawablePadding(0);
        while (true) {
            StatusDrawable[] statusDrawableArr = this.statusDrawables;
            if (i >= statusDrawableArr.length) {
                return;
            }
            statusDrawableArr[i].stop();
            i++;
        }
    }

    public void switchToNextMessage() {
        if (this.popupMessages.size() > 1) {
            if (this.currentMessageNum < this.popupMessages.size() - 1) {
                this.currentMessageNum++;
            } else {
                this.currentMessageNum = 0;
            }
            this.currentMessageObject = (MessageObject) this.popupMessages.get(this.currentMessageNum);
            updateInterfaceForCurrentMessage(2);
            this.countText.setText(String.format("%d/%d", Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())));
        }
    }

    private void switchToPreviousMessage() {
        if (this.popupMessages.size() > 1) {
            int i = this.currentMessageNum;
            if (i <= 0) {
                i = this.popupMessages.size();
            }
            this.currentMessageNum = i - 1;
            this.currentMessageObject = (MessageObject) this.popupMessages.get(this.currentMessageNum);
            updateInterfaceForCurrentMessage(1);
            this.countText.setText(String.format("%d/%d", Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())));
        }
    }

    private void updateInterfaceForCurrentMessage(int r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.updateInterfaceForCurrentMessage(int):void");
    }

    private void updateSubtitle() {
        MessageObject messageObject;
        TLRPC.User user;
        TextView textView;
        String userName;
        TextView textView2;
        String formatUserStatus;
        int i;
        String str;
        if (this.actionBar == null || (messageObject = this.currentMessageObject) == null || this.currentChat != null || (user = this.currentUser) == null) {
            return;
        }
        long j = user.id / 1000;
        if (j == 777 || j == 333 || ContactsController.getInstance(messageObject.currentAccount).contactsDict.get(Long.valueOf(this.currentUser.id)) != null || ((ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.size() == 0 && ContactsController.getInstance(this.currentMessageObject.currentAccount).isLoadingContacts()) || (str = this.currentUser.phone) == null || str.length() == 0)) {
            textView = this.nameTextView;
            userName = UserObject.getUserName(this.currentUser);
        } else {
            textView = this.nameTextView;
            userName = PhoneFormat.getInstance().format("+" + this.currentUser.phone);
        }
        textView.setText(userName);
        TLRPC.User user2 = this.currentUser;
        if (user2 != null && user2.id == 489000) {
            textView2 = this.onlineTextView;
            i = R.string.VerifyCodesNotifications;
        } else {
            if (user2 == null || user2.id != 777000) {
                CharSequence printingString = MessagesController.getInstance(this.currentMessageObject.currentAccount).getPrintingString(this.currentMessageObject.getDialogId(), 0L, false);
                if (printingString != null && printingString.length() != 0) {
                    this.lastPrintString = printingString;
                    this.onlineTextView.setText(printingString);
                    setTypingAnimation(true);
                    return;
                }
                this.lastPrintString = null;
                setTypingAnimation(false);
                TLRPC.User user3 = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Long.valueOf(this.currentUser.id));
                if (user3 != null) {
                    this.currentUser = user3;
                }
                textView2 = this.onlineTextView;
                formatUserStatus = LocaleController.formatUserStatus(this.currentMessageObject.currentAccount, this.currentUser);
                textView2.setText(formatUserStatus);
            }
            textView2 = this.onlineTextView;
            i = R.string.ServiceNotifications;
        }
        formatUserStatus = LocaleController.getString(i);
        textView2.setText(formatUserStatus);
    }

    public boolean checkTransitionAnimation() {
        if (this.animationInProgress && this.animationStartTime < System.currentTimeMillis() - 400) {
            this.animationInProgress = false;
            Runnable runnable = this.onAnimationEndRunnable;
            if (runnable != null) {
                runnable.run();
                this.onAnimationEndRunnable = null;
            }
        }
        return this.animationInProgress;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TextView textView;
        PopupAudioView popupAudioView;
        MessageObject messageObject;
        PopupAudioView popupAudioView2;
        MessageObject messageObject2;
        MessageObject messageObject3;
        if (i == NotificationCenter.appDidLogout) {
            if (i2 == this.lastResumedAccount) {
                onFinish();
                finish();
                return;
            }
            return;
        }
        int i3 = 0;
        if (i == NotificationCenter.pushMessagesUpdated) {
            if (this.isReply) {
                return;
            }
            this.popupMessages.clear();
            for (int i4 = 0; i4 < 4; i4++) {
                if (UserConfig.getInstance(i4).isClientActivated()) {
                    this.popupMessages.addAll(NotificationsController.getInstance(i4).popupMessages);
                }
            }
            getNewMessage();
            if (this.popupMessages.isEmpty()) {
                return;
            }
            for (int i5 = 0; i5 < 3; i5++) {
                int i6 = (this.currentMessageNum - 1) + i5;
                if (this.popupMessages.size() != 1 || (i6 >= 0 && i6 < this.popupMessages.size())) {
                    if (i6 == -1) {
                        i6 = this.popupMessages.size() - 1;
                    } else if (i6 == this.popupMessages.size()) {
                        i6 = 0;
                    }
                    messageObject3 = (MessageObject) this.popupMessages.get(i6);
                } else {
                    messageObject3 = null;
                }
                if (this.setMessageObjects[i5] != messageObject3) {
                    updateInterfaceForCurrentMessage(0);
                }
            }
            return;
        }
        if (i == NotificationCenter.updateInterfaces) {
            if (this.currentMessageObject == null || i2 != this.lastResumedAccount) {
                return;
            }
            int intValue = ((Integer) objArr[0]).intValue();
            if ((MessagesController.UPDATE_MASK_NAME & intValue) != 0 || (MessagesController.UPDATE_MASK_STATUS & intValue) != 0 || (MessagesController.UPDATE_MASK_CHAT_NAME & intValue) != 0 || (MessagesController.UPDATE_MASK_CHAT_MEMBERS & intValue) != 0) {
                updateSubtitle();
            }
            if ((MessagesController.UPDATE_MASK_AVATAR & intValue) != 0 || (MessagesController.UPDATE_MASK_CHAT_AVATAR & intValue) != 0) {
                checkAndUpdateAvatar();
            }
            if ((intValue & MessagesController.UPDATE_MASK_USER_PRINT) == 0) {
                return;
            }
            CharSequence printingString = MessagesController.getInstance(this.currentMessageObject.currentAccount).getPrintingString(this.currentMessageObject.getDialogId(), 0L, false);
            CharSequence charSequence = this.lastPrintString;
            if ((charSequence == null || printingString != null) && ((charSequence != null || printingString == null) && (charSequence == null || charSequence.equals(printingString)))) {
                return;
            }
        } else {
            if (i == NotificationCenter.messagePlayingDidReset) {
                Integer num = (Integer) objArr[0];
                ViewGroup viewGroup = this.messageContainer;
                if (viewGroup != null) {
                    int childCount = viewGroup.getChildCount();
                    while (i3 < childCount) {
                        View childAt = this.messageContainer.getChildAt(i3);
                        if (((Integer) childAt.getTag()).intValue() == 3 && (messageObject2 = (popupAudioView2 = (PopupAudioView) childAt.findViewWithTag(300)).getMessageObject()) != null && messageObject2.currentAccount == i2 && messageObject2.getId() == num.intValue()) {
                            popupAudioView2.updateButtonState();
                            return;
                        }
                        i3++;
                    }
                    return;
                }
                return;
            }
            if (i == NotificationCenter.messagePlayingProgressDidChanged) {
                Integer num2 = (Integer) objArr[0];
                ViewGroup viewGroup2 = this.messageContainer;
                if (viewGroup2 != null) {
                    int childCount2 = viewGroup2.getChildCount();
                    while (i3 < childCount2) {
                        View childAt2 = this.messageContainer.getChildAt(i3);
                        if (((Integer) childAt2.getTag()).intValue() == 3 && (messageObject = (popupAudioView = (PopupAudioView) childAt2.findViewWithTag(300)).getMessageObject()) != null && messageObject.currentAccount == i2 && messageObject.getId() == num2.intValue()) {
                            popupAudioView.updateProgress();
                            return;
                        }
                        i3++;
                    }
                    return;
                }
                return;
            }
            if (i == NotificationCenter.emojiLoaded) {
                ViewGroup viewGroup3 = this.messageContainer;
                if (viewGroup3 != null) {
                    int childCount3 = viewGroup3.getChildCount();
                    while (i3 < childCount3) {
                        View childAt3 = this.messageContainer.getChildAt(i3);
                        if (((Integer) childAt3.getTag()).intValue() == 1 && (textView = (TextView) childAt3.findViewWithTag(301)) != null) {
                            textView.invalidate();
                        }
                        i3++;
                    }
                    return;
                }
                return;
            }
            if (i != NotificationCenter.contactsDidLoad || i2 != this.lastResumedAccount) {
                return;
            }
        }
        updateSubtitle();
    }

    @Override
    public void onBackPressed() {
        if (this.chatActivityEnterView.isPopupShowing()) {
            this.chatActivityEnterView.hidePopup(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        AndroidUtilities.checkDisplaySize(this, configuration);
        AndroidUtilities.setPreferredMaxRefreshRate(getWindow());
        fixLayout();
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Theme.createDialogsResources(this);
        Theme.createChatResources(this, false);
        AndroidUtilities.fillStatusBarHeight(this, false);
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.contactsDidLoad);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.classGuid = ConnectionsManager.generateClassGuid();
        this.statusDrawables[0] = new TypingDotsDrawable(false);
        this.statusDrawables[1] = new RecordStatusDrawable(false);
        this.statusDrawables[2] = new SendingFileDrawable(false);
        this.statusDrawables[3] = new PlayingGameDrawable(false, null);
        this.statusDrawables[4] = new RoundStatusDrawable(false);
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(this) {
            @Override
            public void onLayout(boolean r10, int r11, int r12, int r13, int r14) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.AnonymousClass1.onLayout(boolean, int, int, int, int):void");
            }

            @Override
            protected void onMeasure(int i2, int i3) {
                int makeMeasureSpec;
                int max;
                View.MeasureSpec.getMode(i2);
                View.MeasureSpec.getMode(i3);
                int size = View.MeasureSpec.getSize(i2);
                int size2 = View.MeasureSpec.getSize(i3);
                setMeasuredDimension(size, size2);
                if (measureKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                    size2 -= PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();
                }
                int childCount = getChildCount();
                for (int i4 = 0; i4 < childCount; i4++) {
                    View childAt = getChildAt(i4);
                    if (childAt.getVisibility() != 8) {
                        if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(childAt)) {
                            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
                            max = childAt.getLayoutParams().height;
                        } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(childAt)) {
                            measureChildWithMargins(childAt, i2, 0, i3, 0);
                        } else {
                            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
                            max = Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f) + size2);
                        }
                        childAt.measure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(max, 1073741824));
                    }
                }
            }
        };
        setContentView(sizeNotifierFrameLayout);
        sizeNotifierFrameLayout.setBackgroundColor(-1728053248);
        RelativeLayout relativeLayout = new RelativeLayout(this);
        sizeNotifierFrameLayout.addView(relativeLayout, LayoutHelper.createFrame(-1, -1.0f));
        RelativeLayout relativeLayout2 = new RelativeLayout(this) {
            @Override
            protected void onLayout(boolean z, int i2, int i3, int i4, int i5) {
                super.onLayout(z, i2, i3, i4, i5);
                for (int i6 = 0; i6 < getChildCount(); i6++) {
                    View childAt = getChildAt(i6);
                    if (childAt.getTag() instanceof String) {
                        childAt.layout(childAt.getLeft(), PopupNotificationActivity.this.chatActivityEnterView.getTop() + AndroidUtilities.dp(3.0f), childAt.getRight(), PopupNotificationActivity.this.chatActivityEnterView.getBottom());
                    }
                }
            }

            @Override
            protected void onMeasure(int i2, int i3) {
                super.onMeasure(i2, i3);
                int measuredWidth = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredWidth();
                int measuredHeight = PopupNotificationActivity.this.chatActivityEnterView.getMeasuredHeight();
                for (int i4 = 0; i4 < getChildCount(); i4++) {
                    View childAt = getChildAt(i4);
                    if (childAt.getTag() instanceof String) {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(measuredHeight - AndroidUtilities.dp(3.0f), 1073741824));
                    }
                }
            }
        };
        this.popupContainer = relativeLayout2;
        relativeLayout2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        relativeLayout.addView(this.popupContainer, LayoutHelper.createRelative(-1, 240, 12, 0, 12, 0, 13));
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        ChatActivityEnterView chatActivityEnterView2 = new ChatActivityEnterView(this, sizeNotifierFrameLayout, null, false);
        this.chatActivityEnterView = chatActivityEnterView2;
        chatActivityEnterView2.setId(1000);
        this.popupContainer.addView(this.chatActivityEnterView, LayoutHelper.createRelative(-1, -2, 12));
        this.chatActivityEnterView.setDelegate(new ChatActivityEnterView.ChatActivityEnterViewDelegate() {
            @Override
            public void bottomPanelTranslationYChanged(float f) {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$bottomPanelTranslationYChanged(this, f);
            }

            @Override
            public boolean checkCanRemoveRestrictionsByBoosts() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$checkCanRemoveRestrictionsByBoosts(this);
            }

            @Override
            public void didPressAttachButton() {
            }

            @Override
            public int getContentViewHeight() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$getContentViewHeight(this);
            }

            @Override
            public ChatActivity.ReplyQuote getReplyQuote() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$getReplyQuote(this);
            }

            @Override
            public TL_stories.StoryItem getReplyToStory() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$getReplyToStory(this);
            }

            @Override
            public TLRPC.TL_channels_sendAsPeers getSendAsPeers() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$getSendAsPeers(this);
            }

            @Override
            public boolean hasForwardingMessages() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$hasForwardingMessages(this);
            }

            @Override
            public boolean hasScheduledMessages() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$hasScheduledMessages(this);
            }

            @Override
            public int measureKeyboardHeight() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$measureKeyboardHeight(this);
            }

            @Override
            public void needChangeVideoPreviewState(int i2, float f) {
            }

            @Override
            public void needSendTyping() {
                if (PopupNotificationActivity.this.currentMessageObject != null) {
                    MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).sendTyping(PopupNotificationActivity.this.currentMessageObject.getDialogId(), 0L, 0, PopupNotificationActivity.this.classGuid);
                }
            }

            @Override
            public void needShowMediaBanHint() {
            }

            @Override
            public void needStartRecordAudio(int i2) {
            }

            @Override
            public void needStartRecordVideo(int i2, boolean z, int i3, int i4, long j) {
            }

            @Override
            public void onAttachButtonHidden() {
            }

            @Override
            public void onAttachButtonShow() {
            }

            @Override
            public void onAudioVideoInterfaceUpdated() {
            }

            @Override
            public void onContextMenuClose() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$onContextMenuClose(this);
            }

            @Override
            public void onContextMenuOpen() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$onContextMenuOpen(this);
            }

            @Override
            public void onEditTextScroll() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$onEditTextScroll(this);
            }

            @Override
            public void onKeyboardRequested() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$onKeyboardRequested(this);
            }

            @Override
            public void onMessageEditEnd(boolean z) {
            }

            @Override
            public void onMessageSend(CharSequence charSequence, boolean z, int i2) {
                if (PopupNotificationActivity.this.currentMessageObject == null) {
                    return;
                }
                if (PopupNotificationActivity.this.currentMessageNum >= 0 && PopupNotificationActivity.this.currentMessageNum < PopupNotificationActivity.this.popupMessages.size()) {
                    PopupNotificationActivity.this.popupMessages.remove(PopupNotificationActivity.this.currentMessageNum);
                }
                MessagesController.getInstance(PopupNotificationActivity.this.currentMessageObject.currentAccount).markDialogAsRead(PopupNotificationActivity.this.currentMessageObject.getDialogId(), PopupNotificationActivity.this.currentMessageObject.getId(), Math.max(0, PopupNotificationActivity.this.currentMessageObject.getId()), PopupNotificationActivity.this.currentMessageObject.messageOwner.date, true, 0L, 0, true, 0);
                PopupNotificationActivity.this.currentMessageObject = null;
                PopupNotificationActivity.this.getNewMessage();
            }

            @Override
            public void onPreAudioVideoRecord() {
            }

            @Override
            public void onSendLongClick() {
            }

            @Override
            public void onStickersExpandedChange() {
            }

            @Override
            public void onStickersTab(boolean z) {
            }

            @Override
            public void onSwitchRecordMode(boolean z) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, boolean z, boolean z2) {
            }

            @Override
            public void onTextSelectionChanged(int i2, int i3) {
            }

            @Override
            public void onTextSpansChanged(CharSequence charSequence) {
            }

            @Override
            public void onTrendingStickersShowed(boolean z) {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$onTrendingStickersShowed(this, z);
            }

            @Override
            public void onUpdateSlowModeButton(View view, boolean z, CharSequence charSequence) {
            }

            @Override
            public void onWindowSizeChanged(int i2) {
            }

            @Override
            public boolean onceVoiceAvailable() {
                return ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$onceVoiceAvailable(this);
            }

            @Override
            public void openScheduledMessages() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$openScheduledMessages(this);
            }

            @Override
            public void prepareMessageSending() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$prepareMessageSending(this);
            }

            @Override
            public void scrollToSendingMessage() {
                ChatActivityEnterView.ChatActivityEnterViewDelegate.CC.$default$scrollToSendingMessage(this);
            }

            @Override
            public void toggleVideoRecordingPause() {
            }
        });
        FrameLayoutTouch frameLayoutTouch = new FrameLayoutTouch(this);
        this.messageContainer = frameLayoutTouch;
        this.popupContainer.addView(frameLayoutTouch, 0);
        ActionBar actionBar = new ActionBar(this);
        this.actionBar = actionBar;
        actionBar.setOccupyStatusBar(false);
        this.actionBar.setBackButtonImage(R.drawable.ic_close_white);
        this.actionBar.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefault));
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSelector), false);
        this.popupContainer.addView(this.actionBar);
        ViewGroup.LayoutParams layoutParams = this.actionBar.getLayoutParams();
        layoutParams.width = -1;
        this.actionBar.setLayoutParams(layoutParams);
        ActionBarMenuItem addItemWithWidth = this.actionBar.createMenu().addItemWithWidth(2, 0, AndroidUtilities.dp(56.0f));
        TextView textView = new TextView(this);
        this.countText = textView;
        int i2 = Theme.key_actionBarDefaultSubtitle;
        textView.setTextColor(Theme.getColor(i2));
        this.countText.setTextSize(1, 14.0f);
        this.countText.setGravity(17);
        addItemWithWidth.addView(this.countText, LayoutHelper.createFrame(56, -1.0f));
        FrameLayout frameLayout = new FrameLayout(this);
        this.avatarContainer = frameLayout;
        frameLayout.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        this.actionBar.addView(this.avatarContainer);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.avatarContainer.getLayoutParams();
        layoutParams2.height = -1;
        layoutParams2.width = -2;
        layoutParams2.rightMargin = AndroidUtilities.dp(48.0f);
        layoutParams2.leftMargin = AndroidUtilities.dp(60.0f);
        layoutParams2.gravity = 51;
        this.avatarContainer.setLayoutParams(layoutParams2);
        BackupImageView backupImageView = new BackupImageView(this);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(21.0f));
        this.avatarContainer.addView(this.avatarImageView);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.avatarImageView.getLayoutParams();
        layoutParams3.width = AndroidUtilities.dp(42.0f);
        layoutParams3.height = AndroidUtilities.dp(42.0f);
        layoutParams3.topMargin = AndroidUtilities.dp(3.0f);
        this.avatarImageView.setLayoutParams(layoutParams3);
        TextView textView2 = new TextView(this);
        this.nameTextView = textView2;
        textView2.setTextColor(Theme.getColor(Theme.key_actionBarDefaultTitle));
        this.nameTextView.setTextSize(1, 18.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        TextView textView3 = this.nameTextView;
        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
        textView3.setEllipsize(truncateAt);
        this.nameTextView.setGravity(3);
        this.nameTextView.setTypeface(AndroidUtilities.bold());
        this.avatarContainer.addView(this.nameTextView);
        FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
        layoutParams4.width = -2;
        layoutParams4.height = -2;
        layoutParams4.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams4.bottomMargin = AndroidUtilities.dp(22.0f);
        layoutParams4.gravity = 80;
        this.nameTextView.setLayoutParams(layoutParams4);
        TextView textView4 = new TextView(this);
        this.onlineTextView = textView4;
        textView4.setTextColor(Theme.getColor(i2));
        this.onlineTextView.setTextSize(1, 14.0f);
        this.onlineTextView.setLines(1);
        this.onlineTextView.setMaxLines(1);
        this.onlineTextView.setSingleLine(true);
        this.onlineTextView.setEllipsize(truncateAt);
        this.onlineTextView.setGravity(3);
        this.avatarContainer.addView(this.onlineTextView);
        FrameLayout.LayoutParams layoutParams5 = (FrameLayout.LayoutParams) this.onlineTextView.getLayoutParams();
        layoutParams5.width = -2;
        layoutParams5.height = -2;
        layoutParams5.leftMargin = AndroidUtilities.dp(54.0f);
        layoutParams5.bottomMargin = AndroidUtilities.dp(4.0f);
        layoutParams5.gravity = 80;
        this.onlineTextView.setLayoutParams(layoutParams5);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i3) {
                if (i3 == -1) {
                    PopupNotificationActivity.this.onFinish();
                    PopupNotificationActivity.this.finish();
                } else if (i3 == 1) {
                    PopupNotificationActivity.this.openCurrentMessage();
                } else if (i3 == 2) {
                    PopupNotificationActivity.this.switchToNextMessage();
                }
            }
        });
        PowerManager.WakeLock newWakeLock = ((PowerManager) ApplicationLoader.applicationContext.getSystemService("power")).newWakeLock(268435462, "screen");
        this.wakeLock = newWakeLock;
        newWakeLock.setReferenceCounted(false);
        handleIntent(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onFinish();
        MediaController.getInstance().setFeedbackView(this.chatActivityEnterView, false);
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
        BackupImageView backupImageView = this.avatarImageView;
        if (backupImageView != null) {
            backupImageView.setImageDrawable(null);
        }
    }

    protected void onFinish() {
        if (this.finished) {
            return;
        }
        this.finished = true;
        if (this.isReply) {
            this.popupMessages.clear();
        }
        for (int i = 0; i < 4; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.contactsDidLoad);
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.hidePopup(false);
            this.chatActivityEnterView.setFieldFocused(false);
        }
        int i = this.lastResumedAccount;
        if (i >= 0) {
            ConnectionsManager.getInstance(i).setAppPaused(true, false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (i != 3 || iArr[0] == 0) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(LocaleController.getString(R.string.AppName));
        builder.setMessage(LocaleController.getString(R.string.PermissionNoAudioWithHint));
        builder.setNegativeButton(LocaleController.getString(R.string.PermissionOpenSettings), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                PopupNotificationActivity.this.lambda$onRequestPermissionsResult$0(dialogInterface, i2);
            }
        });
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MediaController.getInstance().setFeedbackView(this.chatActivityEnterView, true);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.setFieldFocused(true);
        }
        fixLayout();
        checkAndUpdateAvatar();
        this.wakeLock.acquire(7000L);
    }

    public boolean onTouchEventMy(android.view.MotionEvent r14) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PopupNotificationActivity.onTouchEventMy(android.view.MotionEvent):boolean");
    }
}
