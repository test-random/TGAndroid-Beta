package org.telegram.ui.Business;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ClickableAnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;

public class BusinessBotButton extends FrameLayout {
    private final AvatarDrawable avatarDrawable;
    private final BackupImageView avatarView;
    private long botId;
    private final int currentAccount;
    private long dialogId;
    private int flags;
    private String manageUrl;
    private final ImageView menuView;
    private final ClickableAnimatedTextView pauseButton;
    private boolean paused;
    private final AnimatedTextView subtitleView;
    private final AnimatedTextView titleView;

    public BusinessBotButton(Context context, final ChatActivity chatActivity, final Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentAccount = chatActivity.getCurrentAccount();
        this.paused = false;
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarView = backupImageView;
        TLRPC.User user = chatActivity.getMessagesController().getUser(Long.valueOf(this.botId));
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable;
        avatarDrawable.setInfo(user);
        backupImageView.setRoundRadius(AndroidUtilities.dp(16.0f));
        backupImageView.setForUserOrChat(user, avatarDrawable);
        addView(backupImageView, LayoutHelper.createFrame(32, 32.0f, 19, 10.0f, 0.0f, 10.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        AnimatedTextView animatedTextView = new AnimatedTextView(context);
        this.titleView = animatedTextView;
        animatedTextView.adaptWidth = false;
        animatedTextView.getDrawable().setHacks(true, true, false);
        animatedTextView.setTypeface(AndroidUtilities.bold());
        animatedTextView.setTextSize(AndroidUtilities.dp(14.0f));
        animatedTextView.setText(UserObject.getUserName(user));
        animatedTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        animatedTextView.setEllipsizeByGradient(true);
        linearLayout.addView(animatedTextView, LayoutHelper.createLinear(-1, 17, 0.0f, 0.0f, 0.0f, 1.0f));
        AnimatedTextView animatedTextView2 = new AnimatedTextView(context);
        this.subtitleView = animatedTextView2;
        animatedTextView2.adaptWidth = false;
        animatedTextView2.getDrawable().setHacks(true, true, false);
        animatedTextView2.setTextSize(AndroidUtilities.dp(13.0f));
        animatedTextView2.setText(LocaleController.getString(R.string.BizBotStatusManages));
        animatedTextView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
        animatedTextView2.setEllipsizeByGradient(true);
        linearLayout.addView(animatedTextView2, LayoutHelper.createLinear(-1, 17));
        addView(linearLayout, LayoutHelper.createFrame(-2, -2.0f, 16, 52.0f, 0.0f, 49.0f, 0.0f));
        ClickableAnimatedTextView clickableAnimatedTextView = new ClickableAnimatedTextView(context);
        this.pauseButton = clickableAnimatedTextView;
        clickableAnimatedTextView.getDrawable().setHacks(true, true, true);
        clickableAnimatedTextView.setAnimationProperties(0.75f, 0L, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
        clickableAnimatedTextView.setScaleProperty(0.6f);
        clickableAnimatedTextView.setTypeface(AndroidUtilities.bold());
        int dp = AndroidUtilities.dp(14.0f);
        int i = Theme.key_featuredStickers_addButton;
        clickableAnimatedTextView.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(dp, Theme.getColor(i, resourcesProvider), Theme.blendOver(Theme.getColor(i, resourcesProvider), Theme.multAlpha(-1, 0.12f))));
        clickableAnimatedTextView.setTextSize(AndroidUtilities.dp(14.0f));
        clickableAnimatedTextView.setGravity(5);
        clickableAnimatedTextView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText, resourcesProvider));
        clickableAnimatedTextView.setPadding(AndroidUtilities.dp(13.0f), 0, AndroidUtilities.dp(13.0f), 0);
        clickableAnimatedTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                BusinessBotButton.this.lambda$new$0(view);
            }
        });
        clickableAnimatedTextView.setOnWidthUpdatedListener(new Runnable() {
            @Override
            public final void run() {
                BusinessBotButton.this.lambda$new$1();
            }
        });
        clickableAnimatedTextView.setText(LocaleController.getString(this.paused ? R.string.BizBotStart : R.string.BizBotStop));
        addView(clickableAnimatedTextView, LayoutHelper.createFrame(64, 28.0f, 21, 0.0f, 0.0f, 49.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.menuView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setImageResource(R.drawable.msg_mini_customize);
        imageView.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), 7));
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3, resourcesProvider), PorterDuff.Mode.MULTIPLY));
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                BusinessBotButton.this.lambda$new$4(chatActivity, resourcesProvider, view);
            }
        });
        addView(imageView, LayoutHelper.createFrame(32, 32.0f, 21, 8.0f, 0.0f, 9.0f, 0.0f));
    }

    public void lambda$new$0(View view) {
        boolean z = !this.paused;
        this.paused = z;
        this.pauseButton.setText(LocaleController.getString(z ? R.string.BizBotStart : R.string.BizBotStop), true);
        this.subtitleView.cancelAnimation();
        this.subtitleView.setText(LocaleController.getString(this.paused ? R.string.BizBotStatusStopped : R.string.BizBotStatusManages), true);
        this.flags = this.paused ? this.flags | 1 : this.flags & (-2);
        MessagesController.getNotificationsSettings(this.currentAccount).edit().putInt("dialog_botflags" + this.dialogId, this.flags).apply();
        TL_account.toggleConnectedBotPaused toggleconnectedbotpaused = new TL_account.toggleConnectedBotPaused();
        toggleconnectedbotpaused.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
        toggleconnectedbotpaused.paused = this.paused;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(toggleconnectedbotpaused, null);
    }

    public void lambda$new$1() {
        float paddingLeft = this.pauseButton.getPaddingLeft() + this.pauseButton.getDrawable().getCurrentWidth() + this.pauseButton.getPaddingRight() + AndroidUtilities.dp(12.0f);
        this.titleView.setRightPadding(paddingLeft);
        this.subtitleView.setRightPadding(paddingLeft);
    }

    public void lambda$new$2() {
        TL_account.disablePeerConnectedBot disablepeerconnectedbot = new TL_account.disablePeerConnectedBot();
        disablepeerconnectedbot.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(disablepeerconnectedbot, null);
        MessagesController.getNotificationsSettings(this.currentAccount).edit().remove("dialog_botid" + this.dialogId).remove("dialog_boturl" + this.dialogId).remove("dialog_botflags" + this.dialogId).apply();
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.peerSettingsDidLoad, Long.valueOf(this.dialogId));
        BusinessChatbotController.getInstance(this.currentAccount).invalidate(false);
    }

    public void lambda$new$3() {
        Browser.openUrl(getContext(), this.manageUrl);
    }

    public void lambda$new$4(ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider, View view) {
        ItemOptions makeOptions = ItemOptions.makeOptions(chatActivity.getLayoutContainer(), resourcesProvider, this.menuView);
        makeOptions.add(R.drawable.msg_cancel, (CharSequence) LocaleController.getString(R.string.BizBotRemove), true, new Runnable() {
            @Override
            public final void run() {
                BusinessBotButton.this.lambda$new$2();
            }
        }).makeMultiline(false);
        if (this.manageUrl != null) {
            makeOptions.add(R.drawable.msg_settings, LocaleController.getString(R.string.BizBotManage), new Runnable() {
                @Override
                public final void run() {
                    BusinessBotButton.this.lambda$new$3();
                }
            });
        }
        makeOptions.translate(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(7.0f));
        makeOptions.setDimAlpha(0);
        makeOptions.show();
    }

    public void set(long j, long j2, String str, int i) {
        this.dialogId = j;
        this.botId = j2;
        this.manageUrl = str;
        this.flags = i;
        this.paused = (i & 1) != 0;
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j2));
        this.avatarDrawable.setInfo(user);
        this.avatarView.setForUserOrChat(user, this.avatarDrawable);
        this.titleView.setText(UserObject.getUserName(user));
        this.subtitleView.setText(LocaleController.getString(this.paused ? R.string.BizBotStatusStopped : R.string.BizBotStatusManages));
        this.pauseButton.setText(LocaleController.getString(this.paused ? R.string.BizBotStart : R.string.BizBotStop));
    }
}
