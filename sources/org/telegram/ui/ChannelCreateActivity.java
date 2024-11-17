package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AdminedChannelCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.RadioButtonCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextBlockCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.ChannelCreateActivity;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkActionView;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.TypefaceSpan;

public class ChannelCreateActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ImageUpdater.ImageUpdaterDelegate {
    private ArrayList adminedChannelCells;
    private TextInfoPrivacyCell adminedInfoCell;
    private LinearLayout adminnedChannelsLayout;
    private TLRPC.FileLocation avatar;
    private AnimatorSet avatarAnimation;
    private TLRPC.FileLocation avatarBig;
    private AvatarDrawable avatarDrawable;
    private RLottieImageView avatarEditor;
    private BackupImageView avatarImage;
    private View avatarOverlay;
    private RadialProgressView avatarProgressView;
    private RLottieDrawable cameraDrawable;
    private boolean canCreatePublic;
    private AlertDialog cancelDialog;
    private long chatId;
    private int checkReqId;
    private Runnable checkRunnable;
    private TextView checkTextView;
    private boolean createAfterUpload;
    private int currentStep;
    private EditTextBoldCursor descriptionTextView;
    private View doneButton;
    private CrossfadeDrawable doneButtonDrawable;
    private ValueAnimator doneButtonDrawableAnimator;
    private boolean donePressed;
    private Integer doneRequestId;
    private EditTextBoldCursor editText;
    private Runnable enableDoneLoading;
    private Boolean forcePublic;
    private HeaderCell headerCell;
    private HeaderCell headerCell2;
    private TextView helpTextView;
    private ImageUpdater imageUpdater;
    private TLRPC.VideoSize inputEmojiMarkup;
    private TLRPC.InputFile inputPhoto;
    private TLRPC.InputFile inputVideo;
    private String inputVideoPath;
    private TLRPC.TL_chatInviteExported invite;
    private boolean isGroup;
    private boolean isPrivate;
    private String lastCheckName;
    private boolean lastNameAvailable;
    private LinearLayout linearLayout;
    private LinearLayout linearLayout2;
    private LinearLayout linkContainer;
    private LoadingCell loadingAdminedCell;
    private boolean loadingAdminedChannels;
    private boolean loadingInvite;
    private EditTextEmoji nameTextView;
    private String nameToSet;
    private Utilities.Callback2 onFinishListener;
    private LinkActionView permanentLinkView;
    private LinearLayout privateContainer;
    private LinearLayout publicContainer;
    private RadioButtonCell radioButtonCell1;
    private RadioButtonCell radioButtonCell2;
    private ShadowSectionCell sectionCell;
    private TextInfoPrivacyCell typeInfoCell;
    private double videoTimestamp;

    public class AnonymousClass1 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass1() {
        }

        public void lambda$onItemClick$0() {
            ChannelCreateActivity.this.updateDoneProgress(false);
            if (ChannelCreateActivity.this.onFinishListener != null) {
                Utilities.Callback2 callback2 = ChannelCreateActivity.this.onFinishListener;
                ChannelCreateActivity channelCreateActivity = ChannelCreateActivity.this;
                callback2.run(channelCreateActivity, Long.valueOf(channelCreateActivity.chatId));
            }
        }

        public void lambda$onItemClick$1() {
            ChannelCreateActivity.this.updateDoneProgress(false);
            if (ChannelCreateActivity.this.onFinishListener != null) {
                Utilities.Callback2 callback2 = ChannelCreateActivity.this.onFinishListener;
                ChannelCreateActivity channelCreateActivity = ChannelCreateActivity.this;
                callback2.run(channelCreateActivity, Long.valueOf(channelCreateActivity.chatId));
            }
        }

        @Override
        public void onItemClick(int i) {
            if (i == -1) {
                if (ChannelCreateActivity.this.donePressed) {
                    ChannelCreateActivity.this.showDoneCancelDialog();
                    return;
                } else {
                    ChannelCreateActivity.this.lambda$onBackPressed$321();
                    return;
                }
            }
            if (i == 1) {
                if (ChannelCreateActivity.this.currentStep == 0) {
                    if (ChannelCreateActivity.this.getParentActivity() == null) {
                        return;
                    }
                    if (ChannelCreateActivity.this.donePressed) {
                        ChannelCreateActivity.this.showDoneCancelDialog();
                        return;
                    }
                    if (ChannelCreateActivity.this.nameTextView.length() == 0) {
                        Vibrator vibrator = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                        if (vibrator != null) {
                            vibrator.vibrate(200L);
                        }
                        AndroidUtilities.shakeView(ChannelCreateActivity.this.nameTextView);
                        return;
                    }
                    ChannelCreateActivity.this.donePressed = true;
                    AndroidUtilities.runOnUIThread(ChannelCreateActivity.this.enableDoneLoading, 200L);
                    if (ChannelCreateActivity.this.imageUpdater.isUploadingImage()) {
                        ChannelCreateActivity.this.createAfterUpload = true;
                        return;
                    } else {
                        ChannelCreateActivity channelCreateActivity = ChannelCreateActivity.this;
                        channelCreateActivity.doneRequestId = Integer.valueOf(MessagesController.getInstance(((BaseFragment) channelCreateActivity).currentAccount).createChat(ChannelCreateActivity.this.nameTextView.getText().toString(), new ArrayList<>(), ChannelCreateActivity.this.descriptionTextView.getText().toString(), 2, false, null, null, -1, ChannelCreateActivity.this));
                        return;
                    }
                }
                if (ChannelCreateActivity.this.currentStep == 1) {
                    if (ChannelCreateActivity.this.isPrivate) {
                        if (ChannelCreateActivity.this.onFinishListener != null) {
                            Utilities.Callback2 callback2 = ChannelCreateActivity.this.onFinishListener;
                            ChannelCreateActivity channelCreateActivity2 = ChannelCreateActivity.this;
                            callback2.run(channelCreateActivity2, Long.valueOf(channelCreateActivity2.chatId));
                        }
                    } else {
                        if (ChannelCreateActivity.this.descriptionTextView.length() == 0) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(ChannelCreateActivity.this.getParentActivity());
                            builder.setTitle(LocaleController.getString(R.string.ChannelPublicEmptyUsernameTitle));
                            builder.setMessage(LocaleController.getString(R.string.ChannelPublicEmptyUsername));
                            builder.setPositiveButton(LocaleController.getString(R.string.Close), null);
                            ChannelCreateActivity.this.showDialog(builder.create());
                            return;
                        }
                        if (!ChannelCreateActivity.this.lastNameAvailable) {
                            Vibrator vibrator2 = (Vibrator) ChannelCreateActivity.this.getParentActivity().getSystemService("vibrator");
                            if (vibrator2 != null) {
                                vibrator2.vibrate(200L);
                            }
                            AndroidUtilities.shakeView(ChannelCreateActivity.this.checkTextView);
                            return;
                        }
                        AndroidUtilities.runOnUIThread(ChannelCreateActivity.this.enableDoneLoading, 200L);
                        MessagesController messagesController = MessagesController.getInstance(((BaseFragment) ChannelCreateActivity.this).currentAccount);
                        ChannelCreateActivity channelCreateActivity3 = ChannelCreateActivity.this;
                        messagesController.updateChannelUserName(channelCreateActivity3, channelCreateActivity3.chatId, ChannelCreateActivity.this.lastCheckName, new Runnable() {
                            @Override
                            public final void run() {
                                ChannelCreateActivity.AnonymousClass1.this.lambda$onItemClick$0();
                            }
                        }, new Runnable() {
                            @Override
                            public final void run() {
                                ChannelCreateActivity.AnonymousClass1.this.lambda$onItemClick$1();
                            }
                        });
                    }
                    if (ChannelCreateActivity.this.onFinishListener == null) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("step", 2);
                        bundle.putLong("chatId", ChannelCreateActivity.this.chatId);
                        bundle.putInt("chatType", 2);
                        ChannelCreateActivity.this.presentFragment(new GroupCreateActivity(bundle), true);
                    }
                }
            }
        }
    }

    public ChannelCreateActivity(Bundle bundle) {
        super(bundle);
        this.adminedChannelCells = new ArrayList();
        this.canCreatePublic = true;
        this.enableDoneLoading = new Runnable() {
            @Override
            public final void run() {
                ChannelCreateActivity.this.lambda$new$3();
            }
        };
        this.currentStep = bundle.getInt("step", 0);
        if (bundle.containsKey("forcePublic")) {
            this.forcePublic = Boolean.valueOf(bundle.getBoolean("forcePublic", false));
        }
        int i = this.currentStep;
        if (i == 0) {
            this.avatarDrawable = new AvatarDrawable();
            this.imageUpdater = new ImageUpdater(true, 1, true);
            TLRPC.TL_channels_checkUsername tL_channels_checkUsername = new TLRPC.TL_channels_checkUsername();
            tL_channels_checkUsername.username = "1";
            tL_channels_checkUsername.channel = new TLRPC.TL_inputChannelEmpty();
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_checkUsername, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ChannelCreateActivity.this.lambda$new$1(tLObject, tL_error);
                }
            });
            return;
        }
        if (i == 1) {
            boolean z = bundle.getBoolean("canCreatePublic", true);
            this.canCreatePublic = z;
            this.isPrivate = !z;
            if (!z) {
                loadAdminedChannels();
            }
        }
        this.chatId = bundle.getLong("chat_id", 0L);
    }

    public boolean checkUserName(final String str) {
        TextView textView;
        int i;
        if (str == null || str.length() <= 0) {
            this.checkTextView.setVisibility(8);
        } else {
            this.checkTextView.setVisibility(0);
        }
        Runnable runnable = this.checkRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.checkRunnable = null;
            this.lastCheckName = null;
            if (this.checkReqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.checkReqId, true);
            }
        }
        this.lastNameAvailable = false;
        if (str != null) {
            if (!str.startsWith("_") && !str.endsWith("_")) {
                for (int i2 = 0; i2 < str.length(); i2++) {
                    char charAt = str.charAt(i2);
                    if (i2 == 0 && charAt >= '0' && charAt <= '9') {
                        textView = this.checkTextView;
                        i = R.string.LinkInvalidStartNumber;
                        break;
                    }
                    if ((charAt >= '0' && charAt <= '9') || ((charAt >= 'a' && charAt <= 'z') || ((charAt >= 'A' && charAt <= 'Z') || charAt == '_'))) {
                    }
                }
            }
            textView = this.checkTextView;
            i = R.string.LinkInvalid;
            textView.setText(LocaleController.getString(i));
            TextView textView2 = this.checkTextView;
            int i3 = Theme.key_text_RedRegular;
            textView2.setTag(Integer.valueOf(i3));
            this.checkTextView.setTextColor(Theme.getColor(i3));
            return false;
        }
        if (str == null || str.length() < 4) {
            textView = this.checkTextView;
            i = R.string.LinkInvalidShort;
        } else {
            if (str.length() <= 32) {
                this.checkTextView.setText(LocaleController.getString(R.string.LinkChecking));
                TextView textView3 = this.checkTextView;
                int i4 = Theme.key_windowBackgroundWhiteGrayText8;
                textView3.setTag(Integer.valueOf(i4));
                this.checkTextView.setTextColor(Theme.getColor(i4));
                this.lastCheckName = str;
                Runnable runnable2 = new Runnable() {
                    @Override
                    public final void run() {
                        ChannelCreateActivity.this.lambda$checkUserName$24(str);
                    }
                };
                this.checkRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, 300L);
                return true;
            }
            textView = this.checkTextView;
            i = R.string.LinkInvalidLong;
        }
        textView.setText(LocaleController.getString(i));
        TextView textView22 = this.checkTextView;
        int i32 = Theme.key_text_RedRegular;
        textView22.setTag(Integer.valueOf(i32));
        this.checkTextView.setTextColor(Theme.getColor(i32));
        return false;
    }

    private void generateLink() {
        if (this.loadingInvite || this.invite != null) {
            return;
        }
        TLRPC.ChatFull chatFull = getMessagesController().getChatFull(this.chatId);
        if (chatFull != null) {
            this.invite = chatFull.exported_invite;
        }
        if (this.invite != null) {
            return;
        }
        this.loadingInvite = true;
        TLRPC.TL_messages_getExportedChatInvites tL_messages_getExportedChatInvites = new TLRPC.TL_messages_getExportedChatInvites();
        tL_messages_getExportedChatInvites.peer = getMessagesController().getInputPeer(-this.chatId);
        tL_messages_getExportedChatInvites.admin_id = getMessagesController().getInputUser(getUserConfig().getCurrentUser());
        tL_messages_getExportedChatInvites.limit = 1;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getExportedChatInvites, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelCreateActivity.this.lambda$generateLink$14(tLObject, tL_error);
            }
        });
    }

    public void lambda$checkUserName$22(String str, TLRPC.TL_error tL_error, TLObject tLObject, TLRPC.TL_channels_checkUsername tL_channels_checkUsername) {
        TextView textView;
        int i;
        TextView textView2;
        int i2;
        this.checkReqId = 0;
        String str2 = this.lastCheckName;
        if (str2 == null || !str2.equals(str)) {
            return;
        }
        if (tL_error == null && (tLObject instanceof TLRPC.TL_boolTrue)) {
            this.checkTextView.setText(LocaleController.formatString("LinkAvailable", R.string.LinkAvailable, str));
            TextView textView3 = this.checkTextView;
            int i3 = Theme.key_windowBackgroundWhiteGreenText;
            textView3.setTag(Integer.valueOf(i3));
            this.checkTextView.setTextColor(Theme.getColor(i3));
            this.lastNameAvailable = true;
            return;
        }
        if (tL_error != null && "USERNAME_INVALID".equals(tL_error.text) && tL_channels_checkUsername.username.length() == 4) {
            this.checkTextView.setText(LocaleController.getString(R.string.UsernameInvalidShort));
            textView2 = this.checkTextView;
            i2 = Theme.key_text_RedRegular;
        } else {
            if (tL_error == null || !"USERNAME_PURCHASE_AVAILABLE".equals(tL_error.text)) {
                if (tL_error == null || !"CHANNELS_ADMIN_PUBLIC_TOO_MUCH".equals(tL_error.text)) {
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_text_RedRegular));
                    this.checkTextView.setText(LocaleController.getString(R.string.LinkInUse));
                } else {
                    this.checkTextView.setTextColor(Theme.getColor(Theme.key_text_RedRegular));
                    this.canCreatePublic = false;
                    showPremiumIncreaseLimitDialog();
                }
                this.lastNameAvailable = false;
            }
            if (tL_channels_checkUsername.username.length() == 4) {
                textView = this.checkTextView;
                i = R.string.UsernameInvalidShortPurchase;
            } else {
                textView = this.checkTextView;
                i = R.string.UsernameInUsePurchase;
            }
            textView.setText(LocaleController.getString(i));
            textView2 = this.checkTextView;
            i2 = Theme.key_windowBackgroundWhiteGrayText8;
        }
        textView2.setTextColor(Theme.getColor(i2));
        this.lastNameAvailable = false;
    }

    public void lambda$checkUserName$23(final String str, final TLRPC.TL_channels_checkUsername tL_channels_checkUsername, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelCreateActivity.this.lambda$checkUserName$22(str, tL_error, tLObject, tL_channels_checkUsername);
            }
        });
    }

    public void lambda$checkUserName$24(final String str) {
        final TLRPC.TL_channels_checkUsername tL_channels_checkUsername = new TLRPC.TL_channels_checkUsername();
        tL_channels_checkUsername.username = str;
        tL_channels_checkUsername.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(this.chatId);
        this.checkReqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_checkUsername, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelCreateActivity.this.lambda$checkUserName$23(str, tL_channels_checkUsername, tLObject, tL_error);
            }
        }, 2);
    }

    public boolean lambda$createView$10(TextView textView, int i, KeyEvent keyEvent) {
        View view;
        if (i != 6 || (view = this.doneButton) == null) {
            return false;
        }
        view.performClick();
        return true;
    }

    public void lambda$createView$11(View view) {
        if (!this.canCreatePublic) {
            showPremiumIncreaseLimitDialog();
        } else if (this.isPrivate) {
            this.isPrivate = false;
            updatePrivatePublic();
        }
    }

    public void lambda$createView$12(View view) {
        if (this.isPrivate) {
            return;
        }
        this.isPrivate = true;
        updatePrivatePublic();
    }

    public static boolean lambda$createView$5(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$createView$6() {
        this.avatar = null;
        this.avatarBig = null;
        this.inputPhoto = null;
        this.inputVideo = null;
        this.inputVideoPath = null;
        this.inputEmojiMarkup = null;
        this.videoTimestamp = 0.0d;
        showAvatarProgress(false, true);
        this.avatarImage.setImage((ImageLocation) null, (String) null, this.avatarDrawable, (Object) null);
        this.avatarEditor.setAnimation(this.cameraDrawable);
        this.cameraDrawable.setCurrentFrame(0);
    }

    public void lambda$createView$7(DialogInterface dialogInterface) {
        if (this.imageUpdater.isUploadingImage()) {
            this.cameraDrawable.setCurrentFrame(0, false);
        } else {
            this.cameraDrawable.setCustomEndFrame(86);
            this.avatarEditor.playAnimation();
        }
    }

    public void lambda$createView$8(View view) {
        this.imageUpdater.openMenu(this.avatar != null, new Runnable() {
            @Override
            public final void run() {
                ChannelCreateActivity.this.lambda$createView$6();
            }
        }, new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                ChannelCreateActivity.this.lambda$createView$7(dialogInterface);
            }
        }, 0);
        this.cameraDrawable.setCurrentFrame(0);
        this.cameraDrawable.setCustomEndFrame(43);
        this.avatarEditor.playAnimation();
    }

    public boolean lambda$createView$9(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 5 || TextUtils.isEmpty(this.nameTextView.getEditText().getText())) {
            return false;
        }
        this.descriptionTextView.requestFocus();
        return true;
    }

    public void lambda$didUploadPhoto$15(TLRPC.InputFile inputFile, TLRPC.InputFile inputFile2, TLRPC.VideoSize videoSize, String str, double d, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2) {
        if (inputFile == null && inputFile2 == null) {
            TLRPC.FileLocation fileLocation = photoSize.location;
            this.avatar = fileLocation;
            this.avatarBig = photoSize2.location;
            this.avatarImage.setImage(ImageLocation.getForLocal(fileLocation), "50_50", this.avatarDrawable, (Object) null);
            showAvatarProgress(true, false);
            return;
        }
        this.inputPhoto = inputFile;
        this.inputVideo = inputFile2;
        this.inputEmojiMarkup = videoSize;
        this.inputVideoPath = str;
        this.videoTimestamp = d;
        if (this.createAfterUpload) {
            AlertDialog alertDialog = this.cancelDialog;
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                    this.cancelDialog = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            updateDoneProgress(false);
            this.donePressed = false;
            this.doneButton.performClick();
        }
        showAvatarProgress(false, true);
        this.avatarEditor.setImageDrawable(null);
    }

    public void lambda$generateLink$13(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error == null) {
            this.invite = (TLRPC.TL_chatInviteExported) ((TLRPC.TL_messages_exportedChatInvites) tLObject).invites.get(0);
        }
        this.loadingInvite = false;
        LinkActionView linkActionView = this.permanentLinkView;
        TLRPC.TL_chatInviteExported tL_chatInviteExported = this.invite;
        linkActionView.setLink(tL_chatInviteExported != null ? tL_chatInviteExported.link : null);
    }

    public void lambda$generateLink$14(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelCreateActivity.this.lambda$generateLink$13(tL_error, tLObject);
            }
        });
    }

    public void lambda$getThemeDescriptions$26() {
        LinearLayout linearLayout = this.adminnedChannelsLayout;
        if (linearLayout != null) {
            int childCount = linearLayout.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.adminnedChannelsLayout.getChildAt(i);
                if (childAt instanceof AdminedChannelCell) {
                    ((AdminedChannelCell) childAt).update();
                }
            }
        }
    }

    public void lambda$loadAdminedChannels$16() {
        this.canCreatePublic = true;
        if (this.descriptionTextView.length() > 0) {
            checkUserName(this.descriptionTextView.getText().toString());
        }
        updatePrivatePublic();
    }

    public void lambda$loadAdminedChannels$17(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChannelCreateActivity.this.lambda$loadAdminedChannels$16();
                }
            });
        }
    }

    public void lambda$loadAdminedChannels$18(TLRPC.Chat chat, DialogInterface dialogInterface, int i) {
        TLRPC.TL_channels_updateUsername tL_channels_updateUsername = new TLRPC.TL_channels_updateUsername();
        tL_channels_updateUsername.channel = MessagesController.getInputChannel(chat);
        tL_channels_updateUsername.username = "";
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_updateUsername, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelCreateActivity.this.lambda$loadAdminedChannels$17(tLObject, tL_error);
            }
        }, 64);
    }

    public void lambda$loadAdminedChannels$19(View view) {
        String formatString;
        final TLRPC.Chat currentChannel = ((AdminedChannelCell) view.getParent()).getCurrentChannel();
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.AppName));
        if (currentChannel.megagroup) {
            formatString = LocaleController.formatString("RevokeLinkAlert", R.string.RevokeLinkAlert, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + ChatObject.getPublicUsername(currentChannel), currentChannel.title);
        } else {
            formatString = LocaleController.formatString("RevokeLinkAlertChannel", R.string.RevokeLinkAlertChannel, MessagesController.getInstance(this.currentAccount).linkPrefix + "/" + ChatObject.getPublicUsername(currentChannel), currentChannel.title);
        }
        builder.setMessage(AndroidUtilities.replaceTags(formatString));
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.RevokeButton), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChannelCreateActivity.this.lambda$loadAdminedChannels$18(currentChannel, dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    public void lambda$loadAdminedChannels$20(TLObject tLObject) {
        this.loadingAdminedChannels = false;
        if (tLObject == null || getParentActivity() == null) {
            return;
        }
        for (int i = 0; i < this.adminedChannelCells.size(); i++) {
            this.linearLayout.removeView((View) this.adminedChannelCells.get(i));
        }
        this.adminedChannelCells.clear();
        TLRPC.TL_messages_chats tL_messages_chats = (TLRPC.TL_messages_chats) tLObject;
        for (int i2 = 0; i2 < tL_messages_chats.chats.size(); i2++) {
            AdminedChannelCell adminedChannelCell = new AdminedChannelCell(getParentActivity(), new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ChannelCreateActivity.this.lambda$loadAdminedChannels$19(view);
                }
            }, false, 0);
            TLRPC.Chat chat = tL_messages_chats.chats.get(i2);
            boolean z = true;
            if (i2 != tL_messages_chats.chats.size() - 1) {
                z = false;
            }
            adminedChannelCell.setChannel(chat, z);
            this.adminedChannelCells.add(adminedChannelCell);
            this.adminnedChannelsLayout.addView(adminedChannelCell, LayoutHelper.createLinear(-1, 72));
        }
        updatePrivatePublic();
    }

    public void lambda$loadAdminedChannels$21(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelCreateActivity.this.lambda$loadAdminedChannels$20(tLObject);
            }
        });
    }

    public void lambda$new$0(TLRPC.TL_error tL_error) {
        this.canCreatePublic = tL_error == null || !tL_error.text.equals("CHANNELS_ADMIN_PUBLIC_TOO_MUCH");
    }

    public void lambda$new$1(TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelCreateActivity.this.lambda$new$0(tL_error);
            }
        });
    }

    public void lambda$new$3() {
        updateDoneProgress(true);
    }

    public void lambda$showDoneCancelDialog$2(DialogInterface dialogInterface, int i) {
        this.donePressed = false;
        this.createAfterUpload = false;
        if (this.doneRequestId != null) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.doneRequestId.intValue(), true);
            this.doneRequestId = null;
        }
        updateDoneProgress(false);
        dialogInterface.dismiss();
    }

    public void lambda$showPremiumIncreaseLimitDialog$25() {
        this.canCreatePublic = true;
        updatePrivatePublic();
    }

    public void lambda$updateDoneProgress$4(ValueAnimator valueAnimator) {
        this.doneButtonDrawable.setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
        this.doneButtonDrawable.invalidateSelf();
    }

    private void loadAdminedChannels() {
        if (this.loadingAdminedChannels) {
            return;
        }
        this.loadingAdminedChannels = true;
        updatePrivatePublic();
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TLRPC.TL_channels_getAdminedPublicChannels(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelCreateActivity.this.lambda$loadAdminedChannels$21(tLObject, tL_error);
            }
        });
    }

    private void showAvatarProgress(final boolean z, boolean z2) {
        if (this.avatarEditor == null) {
            return;
        }
        AnimatorSet animatorSet = this.avatarAnimation;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.avatarAnimation.cancel();
            this.avatarAnimation = null;
        }
        if (!z2) {
            if (z) {
                this.avatarEditor.setAlpha(1.0f);
                this.avatarEditor.setVisibility(4);
                this.avatarProgressView.setAlpha(1.0f);
                this.avatarProgressView.setVisibility(0);
                return;
            }
            this.avatarEditor.setAlpha(1.0f);
            this.avatarEditor.setVisibility(0);
            this.avatarProgressView.setAlpha(0.0f);
            this.avatarProgressView.setVisibility(4);
            return;
        }
        this.avatarAnimation = new AnimatorSet();
        if (z) {
            this.avatarProgressView.setVisibility(0);
            AnimatorSet animatorSet2 = this.avatarAnimation;
            RLottieImageView rLottieImageView = this.avatarEditor;
            Property property = View.ALPHA;
            animatorSet2.playTogether(ObjectAnimator.ofFloat(rLottieImageView, (Property<RLottieImageView, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.avatarProgressView, (Property<RadialProgressView, Float>) property, 1.0f));
        } else {
            if (this.avatarEditor.getVisibility() != 0) {
                this.avatarEditor.setAlpha(0.0f);
            }
            this.avatarEditor.setVisibility(0);
            AnimatorSet animatorSet3 = this.avatarAnimation;
            RLottieImageView rLottieImageView2 = this.avatarEditor;
            Property property2 = View.ALPHA;
            animatorSet3.playTogether(ObjectAnimator.ofFloat(rLottieImageView2, (Property<RLottieImageView, Float>) property2, 1.0f), ObjectAnimator.ofFloat(this.avatarProgressView, (Property<RadialProgressView, Float>) property2, 0.0f));
        }
        this.avatarAnimation.setDuration(180L);
        this.avatarAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animator) {
                ChannelCreateActivity.this.avatarAnimation = null;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (ChannelCreateActivity.this.avatarAnimation == null || ChannelCreateActivity.this.avatarEditor == null) {
                    return;
                }
                (z ? ChannelCreateActivity.this.avatarEditor : ChannelCreateActivity.this.avatarProgressView).setVisibility(4);
                ChannelCreateActivity.this.avatarAnimation = null;
            }
        });
        this.avatarAnimation.start();
    }

    public void showDoneCancelDialog() {
        if (this.cancelDialog != null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.StopLoadingTitle));
        builder.setMessage(LocaleController.getString(R.string.StopLoading));
        builder.setPositiveButton(LocaleController.getString(R.string.WaitMore), null);
        builder.setNegativeButton(LocaleController.getString(R.string.Stop), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChannelCreateActivity.this.lambda$showDoneCancelDialog$2(dialogInterface, i);
            }
        });
        this.cancelDialog = builder.show();
    }

    private void showPremiumIncreaseLimitDialog() {
        if (getParentActivity() == null) {
            return;
        }
        LimitReachedBottomSheet limitReachedBottomSheet = new LimitReachedBottomSheet(this, getParentActivity(), 2, this.currentAccount, null);
        limitReachedBottomSheet.parentIsChannel = true;
        limitReachedBottomSheet.onSuccessRunnable = new Runnable() {
            @Override
            public final void run() {
                ChannelCreateActivity.this.lambda$showPremiumIncreaseLimitDialog$25();
            }
        };
        showDialog(limitReachedBottomSheet);
    }

    public void updateDoneProgress(boolean z) {
        if (!z) {
            AndroidUtilities.cancelRunOnUIThread(this.enableDoneLoading);
        }
        if (this.doneButtonDrawable != null) {
            ValueAnimator valueAnimator = this.doneButtonDrawableAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.doneButtonDrawable.getProgress(), z ? 1.0f : 0.0f);
            this.doneButtonDrawableAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ChannelCreateActivity.this.lambda$updateDoneProgress$4(valueAnimator2);
                }
            });
            this.doneButtonDrawableAnimator.setDuration(Math.abs(this.doneButtonDrawable.getProgress() - (z ? 1.0f : 0.0f)) * 200.0f);
            this.doneButtonDrawableAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.doneButtonDrawableAnimator.start();
        }
    }

    private void updatePrivatePublic() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelCreateActivity.updatePrivatePublic():void");
    }

    @Override
    public boolean canFinishFragment() {
        return ImageUpdater.ImageUpdaterDelegate.CC.$default$canFinishFragment(this);
    }

    @Override
    public View createView(Context context) {
        RadioButtonCell radioButtonCell;
        String string;
        int i;
        RadioButtonCell radioButtonCell2;
        String string2;
        int i2;
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new AnonymousClass1());
        ActionBarMenu createMenu = this.actionBar.createMenu();
        Drawable mutate = context.getResources().getDrawable(R.drawable.ic_ab_done).mutate();
        int i3 = Theme.key_actionBarDefaultIcon;
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i3), PorterDuff.Mode.MULTIPLY));
        CrossfadeDrawable crossfadeDrawable = new CrossfadeDrawable(mutate, new CircularProgressDrawable(Theme.getColor(i3)));
        this.doneButtonDrawable = crossfadeDrawable;
        this.doneButton = createMenu.addItemWithWidth(1, crossfadeDrawable, AndroidUtilities.dp(56.0f), LocaleController.getString(R.string.Done));
        int i4 = this.currentStep;
        if (i4 == 0) {
            this.actionBar.setTitle(LocaleController.getString(R.string.NewChannel));
            SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) {
                private boolean ignoreLayout;

                @Override
                public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChannelCreateActivity.AnonymousClass2.onLayout(boolean, int, int, int, int):void");
                }

                @Override
                protected void onMeasure(int i5, int i6) {
                    int makeMeasureSpec;
                    int paddingTop;
                    int size = View.MeasureSpec.getSize(i5);
                    int size2 = View.MeasureSpec.getSize(i6);
                    setMeasuredDimension(size, size2);
                    int paddingTop2 = size2 - getPaddingTop();
                    measureChildWithMargins(((BaseFragment) ChannelCreateActivity.this).actionBar, i5, 0, i6, 0);
                    if (measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
                        this.ignoreLayout = true;
                        ChannelCreateActivity.this.nameTextView.hideEmojiView();
                        this.ignoreLayout = false;
                    }
                    int childCount = getChildCount();
                    for (int i7 = 0; i7 < childCount; i7++) {
                        View childAt = getChildAt(i7);
                        if (childAt != null && childAt.getVisibility() != 8 && childAt != ((BaseFragment) ChannelCreateActivity.this).actionBar) {
                            if (ChannelCreateActivity.this.nameTextView == null || !ChannelCreateActivity.this.nameTextView.isPopupView(childAt)) {
                                measureChildWithMargins(childAt, i5, 0, i6, 0);
                            } else {
                                if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
                                    paddingTop = childAt.getLayoutParams().height;
                                } else if (AndroidUtilities.isTablet()) {
                                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
                                    paddingTop = Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (paddingTop2 - AndroidUtilities.statusBarHeight) + getPaddingTop());
                                } else {
                                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
                                    paddingTop = (paddingTop2 - AndroidUtilities.statusBarHeight) + getPaddingTop();
                                }
                                childAt.measure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(paddingTop, 1073741824));
                            }
                        }
                    }
                }

                @Override
                public void requestLayout() {
                    if (this.ignoreLayout) {
                        return;
                    }
                    super.requestLayout();
                }
            };
            sizeNotifierFrameLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    boolean lambda$createView$5;
                    lambda$createView$5 = ChannelCreateActivity.lambda$createView$5(view, motionEvent);
                    return lambda$createView$5;
                }
            });
            this.fragmentView = sizeNotifierFrameLayout;
            int i5 = Theme.key_windowBackgroundWhite;
            sizeNotifierFrameLayout.setTag(Integer.valueOf(i5));
            this.fragmentView.setBackgroundColor(Theme.getColor(i5));
            LinearLayout linearLayout = new LinearLayout(context);
            this.linearLayout = linearLayout;
            linearLayout.setOrientation(1);
            sizeNotifierFrameLayout.addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
            FrameLayout frameLayout = new FrameLayout(context);
            this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            BackupImageView backupImageView = new BackupImageView(context) {
                @Override
                public void invalidate() {
                    if (ChannelCreateActivity.this.avatarOverlay != null) {
                        ChannelCreateActivity.this.avatarOverlay.invalidate();
                    }
                    super.invalidate();
                }

                @Override
                public void invalidate(int i6, int i7, int i8, int i9) {
                    if (ChannelCreateActivity.this.avatarOverlay != null) {
                        ChannelCreateActivity.this.avatarOverlay.invalidate();
                    }
                    super.invalidate(i6, i7, i8, i9);
                }
            };
            this.avatarImage = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(32.0f));
            this.avatarDrawable.setInfo(5L, null, null);
            this.avatarImage.setImageDrawable(this.avatarDrawable);
            BackupImageView backupImageView2 = this.avatarImage;
            boolean z = LocaleController.isRTL;
            frameLayout.addView(backupImageView2, LayoutHelper.createFrame(64, 64.0f, (z ? 5 : 3) | 48, z ? 0.0f : 16.0f, 12.0f, z ? 16.0f : 0.0f, 12.0f));
            final Paint paint = new Paint(1);
            paint.setColor(1426063360);
            View view = new View(context) {
                @Override
                protected void onDraw(Canvas canvas) {
                    if (ChannelCreateActivity.this.avatarImage == null || !ChannelCreateActivity.this.avatarImage.getImageReceiver().hasNotThumb()) {
                        return;
                    }
                    paint.setAlpha((int) (ChannelCreateActivity.this.avatarImage.getImageReceiver().getCurrentAlpha() * 85.0f * ChannelCreateActivity.this.avatarProgressView.getAlpha()));
                    canvas.drawCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, getMeasuredWidth() / 2.0f, paint);
                }
            };
            this.avatarOverlay = view;
            view.setContentDescription(LocaleController.getString(R.string.ChatSetPhotoOrVideo));
            View view2 = this.avatarOverlay;
            boolean z2 = LocaleController.isRTL;
            frameLayout.addView(view2, LayoutHelper.createFrame(64, 64.0f, (z2 ? 5 : 3) | 48, z2 ? 0.0f : 16.0f, 12.0f, z2 ? 16.0f : 0.0f, 12.0f));
            this.avatarOverlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view3) {
                    ChannelCreateActivity.this.lambda$createView$8(view3);
                }
            });
            int i6 = R.raw.camera;
            this.cameraDrawable = new RLottieDrawable(i6, "" + i6, AndroidUtilities.dp(60.0f), AndroidUtilities.dp(60.0f), false, null);
            RLottieImageView rLottieImageView = new RLottieImageView(context) {
                @Override
                public void invalidate() {
                    super.invalidate();
                    ChannelCreateActivity.this.avatarOverlay.invalidate();
                }

                @Override
                public void invalidate(int i7, int i8, int i9, int i10) {
                    super.invalidate(i7, i8, i9, i10);
                    ChannelCreateActivity.this.avatarOverlay.invalidate();
                }
            };
            this.avatarEditor = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.avatarEditor.setAnimation(this.cameraDrawable);
            this.avatarEditor.setEnabled(false);
            this.avatarEditor.setClickable(false);
            this.avatarEditor.setPadding(AndroidUtilities.dp(0.0f), 0, 0, AndroidUtilities.dp(1.0f));
            RLottieImageView rLottieImageView2 = this.avatarEditor;
            boolean z3 = LocaleController.isRTL;
            frameLayout.addView(rLottieImageView2, LayoutHelper.createFrame(64, 64.0f, (z3 ? 5 : 3) | 48, z3 ? 0.0f : 15.0f, 12.0f, z3 ? 15.0f : 0.0f, 12.0f));
            RadialProgressView radialProgressView = new RadialProgressView(context) {
                @Override
                public void setAlpha(float f) {
                    super.setAlpha(f);
                    ChannelCreateActivity.this.avatarOverlay.invalidate();
                }
            };
            this.avatarProgressView = radialProgressView;
            radialProgressView.setSize(AndroidUtilities.dp(30.0f));
            this.avatarProgressView.setProgressColor(-1);
            this.avatarProgressView.setNoProgress(false);
            RadialProgressView radialProgressView2 = this.avatarProgressView;
            boolean z4 = LocaleController.isRTL;
            frameLayout.addView(radialProgressView2, LayoutHelper.createFrame(64, 64.0f, (z4 ? 5 : 3) | 48, z4 ? 0.0f : 16.0f, 12.0f, z4 ? 16.0f : 0.0f, 12.0f));
            showAvatarProgress(false, false);
            EditTextEmoji editTextEmoji2 = new EditTextEmoji(context, sizeNotifierFrameLayout, this, 0, false);
            this.nameTextView = editTextEmoji2;
            editTextEmoji2.setHint(LocaleController.getString(R.string.EnterChannelName));
            String str = this.nameToSet;
            if (str != null) {
                this.nameTextView.setText(str);
                this.nameToSet = null;
            }
            this.nameTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(100)});
            this.nameTextView.getEditText().setSingleLine(true);
            this.nameTextView.getEditText().setImeOptions(5);
            this.nameTextView.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public final boolean onEditorAction(TextView textView, int i7, KeyEvent keyEvent) {
                    boolean lambda$createView$9;
                    lambda$createView$9 = ChannelCreateActivity.this.lambda$createView$9(textView, i7, keyEvent);
                    return lambda$createView$9;
                }
            });
            EditTextEmoji editTextEmoji3 = this.nameTextView;
            boolean z5 = LocaleController.isRTL;
            frameLayout.addView(editTextEmoji3, LayoutHelper.createFrame(-1, -2.0f, 16, z5 ? 5.0f : 96.0f, 0.0f, z5 ? 96.0f : 5.0f, 0.0f));
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
            this.descriptionTextView = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 18.0f);
            this.descriptionTextView.setHintTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteHintText));
            EditTextBoldCursor editTextBoldCursor2 = this.descriptionTextView;
            int i7 = Theme.key_windowBackgroundWhiteBlackText;
            editTextBoldCursor2.setTextColor(Theme.getColor(i7));
            this.descriptionTextView.setBackgroundDrawable(null);
            this.descriptionTextView.setLineColors(getThemedColor(Theme.key_windowBackgroundWhiteInputField), getThemedColor(Theme.key_windowBackgroundWhiteInputFieldActivated), getThemedColor(Theme.key_text_RedRegular));
            this.descriptionTextView.setPadding(0, 0, 0, AndroidUtilities.dp(6.0f));
            this.descriptionTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.descriptionTextView.setInputType(180225);
            this.descriptionTextView.setImeOptions(6);
            this.descriptionTextView.setFilters(new InputFilter[]{new InputFilter.LengthFilter(120)});
            this.descriptionTextView.setHint(LocaleController.getString(R.string.DescriptionPlaceholder));
            this.descriptionTextView.setCursorColor(Theme.getColor(i7));
            this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
            this.descriptionTextView.setCursorWidth(1.5f);
            this.linearLayout.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, -2, 24.0f, 18.0f, 24.0f, 0.0f));
            this.descriptionTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public final boolean onEditorAction(TextView textView, int i8, KeyEvent keyEvent) {
                    boolean lambda$createView$10;
                    lambda$createView$10 = ChannelCreateActivity.this.lambda$createView$10(textView, i8, keyEvent);
                    return lambda$createView$10;
                }
            });
            this.descriptionTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable editable) {
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i8, int i9, int i10) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i8, int i9, int i10) {
                }
            });
            TextView textView = new TextView(context);
            this.helpTextView = textView;
            textView.setTextSize(1, 15.0f);
            this.helpTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText8));
            this.helpTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.helpTextView.setText(LocaleController.getString(R.string.DescriptionInfo));
            this.linearLayout.addView(this.helpTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 24, 10, 24, 20));
        } else if (i4 == 1) {
            ScrollView scrollView = new ScrollView(context);
            this.fragmentView = scrollView;
            scrollView.setFillViewport(true);
            LinearLayout linearLayout2 = new LinearLayout(context);
            this.linearLayout = linearLayout2;
            linearLayout2.setOrientation(1);
            scrollView.addView(this.linearLayout, new FrameLayout.LayoutParams(-1, -2));
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.chatId));
            boolean z6 = chat != null && (!ChatObject.isChannel(chat) || ChatObject.isMegagroup(chat));
            this.isGroup = z6;
            this.actionBar.setTitle(LocaleController.getString(z6 ? R.string.GroupSettingsTitle : R.string.ChannelSettingsTitle));
            View view3 = this.fragmentView;
            int i8 = Theme.key_windowBackgroundGray;
            view3.setTag(Integer.valueOf(i8));
            this.fragmentView.setBackgroundColor(Theme.getColor(i8));
            HeaderCell headerCell = new HeaderCell(context, 23);
            this.headerCell2 = headerCell;
            headerCell.setHeight(46);
            HeaderCell headerCell2 = this.headerCell2;
            int i9 = Theme.key_windowBackgroundWhite;
            headerCell2.setBackgroundColor(Theme.getColor(i9));
            this.headerCell2.setText(LocaleController.getString(this.isGroup ? R.string.GroupTypeHeader : R.string.ChannelTypeHeader));
            this.linearLayout.addView(this.headerCell2);
            LinearLayout linearLayout3 = new LinearLayout(context);
            this.linearLayout2 = linearLayout3;
            linearLayout3.setOrientation(1);
            this.linearLayout2.setBackgroundColor(Theme.getColor(i9));
            this.linearLayout.addView(this.linearLayout2, LayoutHelper.createLinear(-1, -2));
            RadioButtonCell radioButtonCell3 = new RadioButtonCell(context);
            this.radioButtonCell1 = radioButtonCell3;
            radioButtonCell3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            Boolean bool = this.forcePublic;
            if (bool != null && !bool.booleanValue()) {
                this.isPrivate = true;
            }
            if (this.isGroup) {
                radioButtonCell = this.radioButtonCell1;
                string = LocaleController.getString(R.string.MegaPublic);
                i = R.string.MegaPublicInfo;
            } else {
                radioButtonCell = this.radioButtonCell1;
                string = LocaleController.getString(R.string.ChannelPublic);
                i = R.string.ChannelPublicInfo;
            }
            radioButtonCell.setTextAndValue(string, LocaleController.getString(i), false, !this.isPrivate);
            this.radioButtonCell1.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view4) {
                    ChannelCreateActivity.this.lambda$createView$11(view4);
                }
            });
            Boolean bool2 = this.forcePublic;
            if (bool2 == null || bool2.booleanValue()) {
                this.linearLayout2.addView(this.radioButtonCell1, LayoutHelper.createLinear(-1, -2));
            }
            RadioButtonCell radioButtonCell4 = new RadioButtonCell(context);
            this.radioButtonCell2 = radioButtonCell4;
            radioButtonCell4.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            Boolean bool3 = this.forcePublic;
            if (bool3 != null && bool3.booleanValue()) {
                this.isPrivate = false;
            }
            if (this.isGroup) {
                radioButtonCell2 = this.radioButtonCell2;
                string2 = LocaleController.getString(R.string.MegaPrivate);
                i2 = R.string.MegaPrivateInfo;
            } else {
                radioButtonCell2 = this.radioButtonCell2;
                string2 = LocaleController.getString(R.string.ChannelPrivate);
                i2 = R.string.ChannelPrivateInfo;
            }
            radioButtonCell2.setTextAndValue(string2, LocaleController.getString(i2), false, this.isPrivate);
            this.radioButtonCell2.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view4) {
                    ChannelCreateActivity.this.lambda$createView$12(view4);
                }
            });
            Boolean bool4 = this.forcePublic;
            if (bool4 == null || !bool4.booleanValue()) {
                this.linearLayout2.addView(this.radioButtonCell2, LayoutHelper.createLinear(-1, -2));
            }
            ShadowSectionCell shadowSectionCell = new ShadowSectionCell(context);
            this.sectionCell = shadowSectionCell;
            this.linearLayout.addView(shadowSectionCell, LayoutHelper.createLinear(-1, -2));
            LinearLayout linearLayout4 = new LinearLayout(context);
            this.linkContainer = linearLayout4;
            linearLayout4.setOrientation(1);
            this.linkContainer.setBackgroundColor(Theme.getColor(i9));
            this.linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, -2));
            HeaderCell headerCell3 = new HeaderCell(context);
            this.headerCell = headerCell3;
            this.linkContainer.addView(headerCell3);
            LinearLayout linearLayout5 = new LinearLayout(context);
            this.publicContainer = linearLayout5;
            linearLayout5.setOrientation(0);
            this.linkContainer.addView(this.publicContainer, LayoutHelper.createLinear(-1, 36, 21.0f, 7.0f, 21.0f, 0.0f));
            EditTextBoldCursor editTextBoldCursor3 = new EditTextBoldCursor(context);
            this.editText = editTextBoldCursor3;
            editTextBoldCursor3.setText(MessagesController.getInstance(this.currentAccount).linkPrefix + "/");
            this.editText.setTextSize(1, 18.0f);
            EditTextBoldCursor editTextBoldCursor4 = this.editText;
            int i10 = Theme.key_windowBackgroundWhiteHintText;
            editTextBoldCursor4.setHintTextColor(Theme.getColor(i10));
            EditTextBoldCursor editTextBoldCursor5 = this.editText;
            int i11 = Theme.key_windowBackgroundWhiteBlackText;
            editTextBoldCursor5.setTextColor(Theme.getColor(i11));
            this.editText.setMaxLines(1);
            this.editText.setLines(1);
            this.editText.setEnabled(false);
            this.editText.setBackgroundDrawable(null);
            this.editText.setPadding(0, 0, 0, 0);
            this.editText.setSingleLine(true);
            this.editText.setInputType(163840);
            this.editText.setImeOptions(6);
            this.publicContainer.addView(this.editText, LayoutHelper.createLinear(-2, 36));
            EditTextBoldCursor editTextBoldCursor6 = new EditTextBoldCursor(context);
            this.descriptionTextView = editTextBoldCursor6;
            editTextBoldCursor6.setTextSize(1, 18.0f);
            this.descriptionTextView.setHintTextColor(Theme.getColor(i10));
            this.descriptionTextView.setTextColor(Theme.getColor(i11));
            this.descriptionTextView.setMaxLines(1);
            this.descriptionTextView.setLines(1);
            this.descriptionTextView.setBackgroundDrawable(null);
            this.descriptionTextView.setPadding(0, 0, 0, 0);
            this.descriptionTextView.setSingleLine(true);
            this.descriptionTextView.setInputType(163872);
            this.descriptionTextView.setImeOptions(6);
            this.descriptionTextView.setHint(LocaleController.getString(R.string.ChannelUsernamePlaceholder));
            this.descriptionTextView.setCursorColor(Theme.getColor(i11));
            this.descriptionTextView.setCursorSize(AndroidUtilities.dp(20.0f));
            this.descriptionTextView.setCursorWidth(1.5f);
            this.publicContainer.addView(this.descriptionTextView, LayoutHelper.createLinear(-1, 36));
            this.descriptionTextView.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable editable) {
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i12, int i13, int i14) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i12, int i13, int i14) {
                    ChannelCreateActivity channelCreateActivity = ChannelCreateActivity.this;
                    channelCreateActivity.checkUserName(channelCreateActivity.descriptionTextView.getText().toString());
                }
            });
            LinearLayout linearLayout6 = new LinearLayout(context);
            this.privateContainer = linearLayout6;
            linearLayout6.setOrientation(1);
            this.linkContainer.addView(this.privateContainer, LayoutHelper.createLinear(-1, -2));
            LinkActionView linkActionView = new LinkActionView(context, this, null, this.chatId, true, ChatObject.isChannel(getMessagesController().getChat(Long.valueOf(this.chatId))));
            this.permanentLinkView = linkActionView;
            linkActionView.hideRevokeOption(true);
            this.permanentLinkView.setUsers(0, null);
            this.privateContainer.addView(this.permanentLinkView);
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context) {
                @Override
                public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
                    if (charSequence != 0) {
                        charSequence = AndroidUtilities.replaceTags(charSequence.toString());
                        int indexOf = charSequence.toString().indexOf(10);
                        if (indexOf >= 0) {
                            charSequence.replace(indexOf, indexOf + 1, " ");
                            charSequence.setSpan(new ForegroundColorSpan(ChannelCreateActivity.this.getThemedColor(Theme.key_text_RedRegular)), 0, indexOf, 33);
                        }
                        TypefaceSpan[] typefaceSpanArr = (TypefaceSpan[]) charSequence.getSpans(0, charSequence.length(), TypefaceSpan.class);
                        final String obj = (ChannelCreateActivity.this.descriptionTextView == null || ChannelCreateActivity.this.descriptionTextView.getText() == null) ? "" : ChannelCreateActivity.this.descriptionTextView.getText().toString();
                        for (int i12 = 0; i12 < typefaceSpanArr.length; i12++) {
                            charSequence.setSpan(new ClickableSpan() {
                                @Override
                                public void onClick(View view4) {
                                    Browser.openUrl(getContext(), "https://fragment.com/username/" + obj);
                                }

                                @Override
                                public void updateDrawState(TextPaint textPaint) {
                                    super.updateDrawState(textPaint);
                                    textPaint.setUnderlineText(false);
                                }
                            }, charSequence.getSpanStart(typefaceSpanArr[i12]), charSequence.getSpanEnd(typefaceSpanArr[i12]), 33);
                            charSequence.removeSpan(typefaceSpanArr[i12]);
                        }
                    }
                    super.setText(charSequence, bufferType);
                }
            };
            this.checkTextView = linksTextView;
            linksTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
            this.checkTextView.setHighlightColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection));
            this.checkTextView.setTextSize(1, 15.0f);
            this.checkTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.checkTextView.setVisibility(8);
            this.checkTextView.setPadding(AndroidUtilities.dp(3.0f), 0, AndroidUtilities.dp(3.0f), 0);
            this.linkContainer.addView(this.checkTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 18, 3, 18, 7));
            TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
            this.typeInfoCell = textInfoPrivacyCell;
            int i12 = R.drawable.greydivider_bottom;
            int i13 = Theme.key_windowBackgroundGrayShadow;
            textInfoPrivacyCell.setBackgroundDrawable(Theme.getThemedDrawableByKey(context, i12, i13));
            this.linearLayout.addView(this.typeInfoCell, LayoutHelper.createLinear(-1, -2));
            LoadingCell loadingCell = new LoadingCell(context);
            this.loadingAdminedCell = loadingCell;
            this.linearLayout.addView(loadingCell, LayoutHelper.createLinear(-1, -2));
            LinearLayout linearLayout7 = new LinearLayout(context);
            this.adminnedChannelsLayout = linearLayout7;
            linearLayout7.setBackgroundColor(Theme.getColor(i9));
            this.adminnedChannelsLayout.setOrientation(1);
            this.linearLayout.addView(this.adminnedChannelsLayout, LayoutHelper.createLinear(-1, -2));
            TextInfoPrivacyCell textInfoPrivacyCell2 = new TextInfoPrivacyCell(context);
            this.adminedInfoCell = textInfoPrivacyCell2;
            textInfoPrivacyCell2.setBackgroundDrawable(Theme.getThemedDrawableByKey(context, i12, i13));
            this.linearLayout.addView(this.adminedInfoCell, LayoutHelper.createLinear(-1, -2));
            updatePrivatePublic();
        }
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatDidFailCreate) {
            AlertDialog alertDialog = this.cancelDialog;
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                    this.cancelDialog = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            updateDoneProgress(false);
            this.donePressed = false;
            return;
        }
        if (i == NotificationCenter.chatDidCreated) {
            AlertDialog alertDialog2 = this.cancelDialog;
            if (alertDialog2 != null) {
                try {
                    alertDialog2.dismiss();
                    this.cancelDialog = null;
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            long longValue = ((Long) objArr[0]).longValue();
            Bundle bundle = new Bundle();
            bundle.putInt("step", 1);
            bundle.putLong("chat_id", longValue);
            bundle.putBoolean("canCreatePublic", this.canCreatePublic);
            Boolean bool = this.forcePublic;
            if (bool != null) {
                bundle.putBoolean("forcePublic", bool.booleanValue());
            }
            if (this.inputPhoto != null || this.inputVideo != null || this.inputEmojiMarkup != null) {
                MessagesController.getInstance(this.currentAccount).changeChatAvatar(longValue, null, this.inputPhoto, this.inputVideo, this.inputEmojiMarkup, this.videoTimestamp, this.inputVideoPath, this.avatar, this.avatarBig, null);
            }
            ChannelCreateActivity channelCreateActivity = new ChannelCreateActivity(bundle);
            channelCreateActivity.setOnFinishListener(this.onFinishListener);
            presentFragment(channelCreateActivity, true);
        }
    }

    @Override
    public void didStartUpload(boolean z) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        radialProgressView.setProgress(0.0f);
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
                ChannelCreateActivity.this.lambda$didUploadPhoto$15(inputFile, inputFile2, videoSize, str, d, photoSize2, photoSize);
            }
        });
    }

    @Override
    public void dismissCurrentDialog() {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater == null || !imageUpdater.dismissCurrentDialog(this.visibleDialog)) {
            super.dismissCurrentDialog();
        }
    }

    @Override
    public boolean dismissDialogOnPause(Dialog dialog) {
        ImageUpdater imageUpdater = this.imageUpdater;
        return (imageUpdater == null || imageUpdater.dismissDialogOnPause(dialog)) && super.dismissDialogOnPause(dialog);
    }

    @Override
    public String getInitialSearchString() {
        return this.nameTextView.getText().toString();
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                ChannelCreateActivity.this.lambda$getThemeDescriptions$26();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        View view = this.fragmentView;
        int i = ThemeDescription.FLAG_CHECKTAG | ThemeDescription.FLAG_BACKGROUND;
        int i2 = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(view, i, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        EditTextEmoji editTextEmoji = this.nameTextView;
        int i3 = ThemeDescription.FLAG_TEXTCOLOR;
        int i4 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(editTextEmoji, i3, null, null, null, null, i4));
        EditTextEmoji editTextEmoji2 = this.nameTextView;
        int i5 = ThemeDescription.FLAG_HINTTEXTCOLOR;
        int i6 = Theme.key_windowBackgroundWhiteHintText;
        arrayList.add(new ThemeDescription(editTextEmoji2, i5, null, null, null, null, i6));
        EditTextEmoji editTextEmoji3 = this.nameTextView;
        int i7 = ThemeDescription.FLAG_BACKGROUNDFILTER;
        int i8 = Theme.key_windowBackgroundWhiteInputField;
        arrayList.add(new ThemeDescription(editTextEmoji3, i7, null, null, null, null, i8));
        EditTextEmoji editTextEmoji4 = this.nameTextView;
        int i9 = ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE;
        int i10 = Theme.key_windowBackgroundWhiteInputFieldActivated;
        arrayList.add(new ThemeDescription(editTextEmoji4, i9, null, null, null, null, i10));
        arrayList.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, i6));
        arrayList.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, i8));
        arrayList.add(new ThemeDescription(this.descriptionTextView, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, i10));
        TextView textView = this.helpTextView;
        int i11 = ThemeDescription.FLAG_TEXTCOLOR;
        int i12 = Theme.key_windowBackgroundWhiteGrayText8;
        arrayList.add(new ThemeDescription(textView, i11, null, null, null, null, i12));
        arrayList.add(new ThemeDescription(this.linearLayout2, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.linkContainer, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i2));
        ShadowSectionCell shadowSectionCell = this.sectionCell;
        int i13 = ThemeDescription.FLAG_BACKGROUNDFILTER;
        int i14 = Theme.key_windowBackgroundGrayShadow;
        arrayList.add(new ThemeDescription(shadowSectionCell, i13, null, null, null, null, i14));
        int i15 = Theme.key_windowBackgroundWhiteBlueHeader;
        arrayList.add(new ThemeDescription(this.headerCell, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i15));
        arrayList.add(new ThemeDescription(this.headerCell2, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i15));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, i6));
        TextView textView2 = this.checkTextView;
        int i16 = ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG;
        int i17 = Theme.key_text_RedRegular;
        arrayList.add(new ThemeDescription(textView2, i16, null, null, null, null, i17));
        arrayList.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, i12));
        arrayList.add(new ThemeDescription(this.checkTextView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_windowBackgroundWhiteGreenText));
        arrayList.add(new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i14));
        arrayList.add(new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.typeInfoCell, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i17));
        arrayList.add(new ThemeDescription(this.adminedInfoCell, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i14));
        arrayList.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i2));
        LinearLayout linearLayout = this.privateContainer;
        int i18 = ThemeDescription.FLAG_SELECTOR;
        int i19 = Theme.key_listSelector;
        arrayList.add(new ThemeDescription(linearLayout, i18, null, null, null, null, i19));
        arrayList.add(new ThemeDescription(this.privateContainer, 0, new Class[]{TextBlockCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.loadingAdminedCell, 0, new Class[]{LoadingCell.class}, new String[]{"progressBar"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_progressCircle));
        arrayList.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i19));
        int i20 = Theme.key_radioBackground;
        arrayList.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i20));
        int i21 = Theme.key_radioBackgroundChecked;
        arrayList.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i21));
        arrayList.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        int i22 = Theme.key_windowBackgroundWhiteGrayText2;
        arrayList.add(new ThemeDescription(this.radioButtonCell1, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i22));
        arrayList.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_SELECTOR, null, null, null, null, i19));
        arrayList.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOX, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i20));
        arrayList.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_CHECKBOXCHECK, new Class[]{RadioButtonCell.class}, new String[]{"radioButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i21));
        arrayList.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        arrayList.add(new ThemeDescription(this.radioButtonCell2, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{RadioButtonCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i22));
        arrayList.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i4));
        int i23 = Theme.key_windowBackgroundWhiteGrayText;
        arrayList.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i23));
        arrayList.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_LINKCOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteLinkText));
        arrayList.add(new ThemeDescription(this.adminnedChannelsLayout, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{AdminedChannelCell.class}, new String[]{"deleteButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i23));
        arrayList.add(new ThemeDescription(null, 0, null, null, Theme.avatarDrawables, themeDescriptionDelegate, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
        return arrayList;
    }

    @Override
    public void onActivityResultFragment(int i, int i2, Intent intent) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onActivityResult(i, i2, intent);
        }
    }

    @Override
    public boolean onBackPressed() {
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            return true;
        }
        this.nameTextView.hidePopup(true);
        return false;
    }

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.chatDidFailCreate);
        if (this.currentStep == 1) {
            generateLink();
        }
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.parentFragment = this;
            imageUpdater.setDelegate(this);
        }
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.doneRequestId != null) {
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.doneRequestId.intValue(), true);
            this.doneRequestId = null;
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidCreated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.chatDidFailCreate);
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.clear();
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onPause();
        }
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onPause();
        }
    }

    @Override
    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onRequestPermissionsResultFragment(i, strArr, iArr);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        EditTextEmoji editTextEmoji = this.nameTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onResume();
        }
        AndroidUtilities.requestAdjustResize(getParentActivity(), this.classGuid);
        ImageUpdater imageUpdater = this.imageUpdater;
        if (imageUpdater != null) {
            imageUpdater.onResume();
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (!z || this.currentStep == 1) {
            return;
        }
        this.nameTextView.requestFocus();
        this.nameTextView.openKeyboard();
    }

    @Override
    public void onUploadProgressChanged(float f) {
        RadialProgressView radialProgressView = this.avatarProgressView;
        if (radialProgressView == null) {
            return;
        }
        radialProgressView.setProgress(f);
    }

    public void restoreSelfArgs(Bundle bundle) {
        if (this.currentStep == 0) {
            ImageUpdater imageUpdater = this.imageUpdater;
            if (imageUpdater != null) {
                imageUpdater.currentPicturePath = bundle.getString("path");
            }
            String string = bundle.getString("nameTextView");
            if (string != null) {
                EditTextEmoji editTextEmoji = this.nameTextView;
                if (editTextEmoji != null) {
                    editTextEmoji.setText(string);
                } else {
                    this.nameToSet = string;
                }
            }
        }
    }

    @Override
    public void saveSelfArgs(Bundle bundle) {
        String str;
        if (this.currentStep == 0) {
            ImageUpdater imageUpdater = this.imageUpdater;
            if (imageUpdater != null && (str = imageUpdater.currentPicturePath) != null) {
                bundle.putString("path", str);
            }
            EditTextEmoji editTextEmoji = this.nameTextView;
            if (editTextEmoji != null) {
                String obj = editTextEmoji.getText().toString();
                if (obj.length() != 0) {
                    bundle.putString("nameTextView", obj);
                }
            }
        }
    }

    public void setOnFinishListener(Utilities.Callback2 callback2) {
        this.onFinishListener = callback2;
    }
}
