package org.telegram.ui.bots;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextSuggestionsFix;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.TopicsFragment;

public abstract class BotVerifySheet {
    public static void lambda$openRemoveVerify$6(boolean[] zArr, TLObject tLObject, Utilities.Callback callback) {
        zArr[0] = false;
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            callback.run(Boolean.TRUE);
        }
    }

    public static void lambda$openRemoveVerify$7(final boolean[] zArr, final Utilities.Callback callback, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotVerifySheet.lambda$openRemoveVerify$6(zArr, tLObject, callback);
            }
        });
    }

    public static void lambda$openRemoveVerify$8(final boolean[] zArr, int i, long j, long j2, final Utilities.Callback callback, DialogInterface dialogInterface, int i2) {
        if (zArr[0]) {
            return;
        }
        zArr[0] = true;
        TL_bots.setCustomVerification setcustomverification = new TL_bots.setCustomVerification();
        setcustomverification.enabled = false;
        setcustomverification.flags |= 1;
        setcustomverification.bot = MessagesController.getInstance(i).getInputUser(j);
        setcustomverification.peer = MessagesController.getInstance(i).getInputPeer(j2);
        ConnectionsManager.getInstance(i).sendRequest(setcustomverification, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BotVerifySheet.lambda$openRemoveVerify$7(zArr, callback, tLObject, tL_error);
            }
        });
    }

    public static void lambda$openSheet$2(OutlineTextContainerView outlineTextContainerView, EditTextBoldCursor editTextBoldCursor, View view, boolean z) {
        outlineTextContainerView.animateSelection(z, !TextUtils.isEmpty(editTextBoldCursor.getText()));
    }

    public static void lambda$openSheet$3(ButtonWithCounterView buttonWithCounterView, TLObject tLObject, BottomSheet bottomSheet, Utilities.Callback callback) {
        buttonWithCounterView.setLoading(false);
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            bottomSheet.dismiss();
            callback.run(Boolean.FALSE);
        }
    }

    public static void lambda$openSheet$4(final ButtonWithCounterView buttonWithCounterView, final BottomSheet bottomSheet, final Utilities.Callback callback, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BotVerifySheet.lambda$openSheet$3(ButtonWithCounterView.this, tLObject, bottomSheet, callback);
            }
        });
    }

    public static void lambda$openSheet$5(final ButtonWithCounterView buttonWithCounterView, TL_bots.botVerifierSettings botverifiersettings, EditTextBoldCursor editTextBoldCursor, int i, OutlineTextContainerView outlineTextContainerView, int i2, long j, long j2, final BottomSheet bottomSheet, final Utilities.Callback callback, View view) {
        if (buttonWithCounterView.isLoading()) {
            return;
        }
        if (botverifiersettings.can_modify_custom_description && editTextBoldCursor.getText().length() > i) {
            outlineTextContainerView.animateError(1.0f);
            AndroidUtilities.shakeViewSpring(outlineTextContainerView, -6.0f);
            return;
        }
        buttonWithCounterView.setLoading(true);
        TL_bots.setCustomVerification setcustomverification = new TL_bots.setCustomVerification();
        setcustomverification.enabled = true;
        setcustomverification.flags = 1 | setcustomverification.flags;
        setcustomverification.bot = MessagesController.getInstance(i2).getInputUser(j);
        setcustomverification.peer = MessagesController.getInstance(i2).getInputPeer(j2);
        setcustomverification.custom_description = botverifiersettings.can_modify_custom_description ? editTextBoldCursor.getText().toString() : botverifiersettings.custom_description;
        if (!TextUtils.isEmpty(setcustomverification.custom_description)) {
            setcustomverification.flags |= 4;
        }
        ConnectionsManager.getInstance(i2).sendRequest(setcustomverification, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                BotVerifySheet.lambda$openSheet$4(ButtonWithCounterView.this, bottomSheet, callback, tLObject, tL_error);
            }
        });
    }

    public static void lambda$openVerify$0(TopicsFragment topicsFragment, DialogsActivity dialogsActivity, long j, int i, Boolean bool) {
        String str;
        TLRPC.Chat chat;
        if (topicsFragment != null) {
            topicsFragment.lambda$onBackPressed$321();
            dialogsActivity.removeSelfFromStack();
        } else {
            dialogsActivity.lambda$onBackPressed$321();
        }
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        if (j >= 0) {
            TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(j));
            str = UserObject.getForcedFirstName(user);
            chat = user;
        } else {
            TLRPC.Chat chat2 = MessagesController.getInstance(i).getChat(Long.valueOf(-j));
            if (chat2 == null) {
                str = "";
                chat = chat2;
            } else {
                str = chat2.title;
                chat = chat2;
            }
        }
        BulletinFactory.of(safeLastFragment).createUsersBulletin(chat, AndroidUtilities.replaceTags(LocaleController.formatString(bool.booleanValue() ? R.string.BotSentRevokeVerifyRequest : R.string.BotSentVerifyRequest, str))).show(false);
    }

    public static boolean lambda$openVerify$1(final DialogsActivity dialogsActivity, final int i, long j, TL_bots.botVerifierSettings botverifiersettings, DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i2, final TopicsFragment topicsFragment) {
        if (arrayList.isEmpty()) {
            return false;
        }
        final long j2 = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
        openSheet(dialogsActivity.getContext(), i, j, j2, botverifiersettings, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                BotVerifySheet.lambda$openVerify$0(TopicsFragment.this, dialogsActivity, j2, i, (Boolean) obj);
            }
        });
        return true;
    }

    public static void openRemoveVerify(Context context, final int i, final long j, final long j2, TL_bots.botVerifierSettings botverifiersettings, final Utilities.Callback callback) {
        String str;
        TLRPC.Chat chat;
        if (context == null) {
            return;
        }
        if (j2 >= 0) {
            TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(j2));
            str = UserObject.getForcedFirstName(user);
            chat = user;
        } else {
            TLRPC.Chat chat2 = MessagesController.getInstance(i).getChat(Long.valueOf(-j2));
            if (chat2 == null) {
                str = "";
                chat = chat2;
            } else {
                str = chat2.title;
                chat = chat2;
            }
        }
        FrameLayout frameLayout = new FrameLayout(context);
        FrameLayout frameLayout2 = new FrameLayout(context);
        frameLayout2.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), Theme.getColor(Theme.key_groupcreate_spanBackground)));
        BackupImageView backupImageView = new BackupImageView(context);
        backupImageView.setRoundRadius(AndroidUtilities.dp(28.0f));
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setInfo((TLObject) chat);
        backupImageView.setForUserOrChat(chat, avatarDrawable);
        frameLayout2.addView(backupImageView, LayoutHelper.createFrame(28, 28, 51));
        BackupImageView backupImageView2 = new BackupImageView(context);
        backupImageView2.setEmojiColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_verifiedBackground), PorterDuff.Mode.SRC_IN));
        backupImageView2.setAnimatedEmojiDrawable(AnimatedEmojiDrawable.make(i, 3, botverifiersettings.icon));
        frameLayout2.addView(backupImageView2, LayoutHelper.createFrame(20, 20.0f, 19, 34.0f, 0.0f, 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        simpleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        simpleTextView.setTextSize(13);
        simpleTextView.setEllipsizeByGradient(true);
        simpleTextView.setText(str);
        simpleTextView.setWidthWrapContent(true);
        frameLayout2.addView(simpleTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 57.0f, 0.0f, 10.0f, 0.0f));
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-2, -2.0f, 17, 16.0f, 0.0f, 16.0f, 0.0f));
        final boolean[] zArr = new boolean[1];
        new AlertDialog.Builder(context).setTitle(LocaleController.getString(R.string.BotRemoveVerificationTitle)).setMessage(LocaleController.getString(j2 >= 0 ? R.string.BotRemoveVerificationText : R.string.BotRemoveVerificationChatText)).setView(frameLayout).setNegativeButton(LocaleController.getString(R.string.Cancel), null).setPositiveButton(LocaleController.getString(R.string.Remove), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i2) {
                BotVerifySheet.lambda$openRemoveVerify$8(zArr, i, j, j2, callback, dialogInterface, i2);
            }
        }).makeRed(-1).show();
    }

    public static void openSheet(Context context, final int i, final long j, final long j2, final TL_bots.botVerifierSettings botverifiersettings, final Utilities.Callback callback) {
        TLRPC.Chat chat;
        String str;
        TLRPC.Chat chat2;
        TLRPC.User user;
        ViewGroup.LayoutParams createFrame;
        View view;
        if (context == null) {
            return;
        }
        MessagesController.getInstance(i).getUser(Long.valueOf(j));
        if (j2 >= 0) {
            TLRPC.User user2 = MessagesController.getInstance(i).getUser(Long.valueOf(j2));
            String forcedFirstName = UserObject.getForcedFirstName(user2);
            if (user2.bot_verification_icon == botverifiersettings.icon) {
                openRemoveVerify(context, i, j, j2, botverifiersettings, callback);
                return;
            }
            str = forcedFirstName;
            chat = null;
            chat2 = user2;
            user = user2;
        } else {
            TLRPC.Chat chat3 = MessagesController.getInstance(i).getChat(Long.valueOf(-j2));
            String str2 = chat3 == null ? "" : chat3.title;
            if (chat3.bot_verification_icon == botverifiersettings.icon) {
                openRemoveVerify(context, i, j, j2, botverifiersettings, callback);
                return;
            }
            chat = chat3;
            str = str2;
            chat2 = chat;
            user = null;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(context, true);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
        linearLayout.setClipChildren(false);
        linearLayout.setClipToPadding(false);
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), Theme.getColor(Theme.key_groupcreate_spanBackground)));
        BackupImageView backupImageView = new BackupImageView(context);
        backupImageView.setRoundRadius(AndroidUtilities.dp(28.0f));
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setInfo((TLObject) chat2);
        backupImageView.setForUserOrChat(chat2, avatarDrawable);
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(28, 28, 51));
        BackupImageView backupImageView2 = new BackupImageView(context);
        backupImageView2.setEmojiColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_verifiedBackground), PorterDuff.Mode.SRC_IN));
        backupImageView2.setAnimatedEmojiDrawable(AnimatedEmojiDrawable.make(i, 3, botverifiersettings.icon));
        frameLayout.addView(backupImageView2, LayoutHelper.createFrame(20, 20.0f, 19, 34.0f, 0.0f, 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        simpleTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        simpleTextView.setTextSize(13);
        simpleTextView.setEllipsizeByGradient(true);
        simpleTextView.setText(str);
        simpleTextView.setWidthWrapContent(true);
        frameLayout.addView(simpleTextView, LayoutHelper.createFrame(-2, -2.0f, 19, 57.0f, 0.0f, 10.0f, 0.0f));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-2, -2, 1, 16, 0, 16, 0));
        TextView textView = new TextView(context);
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        textView.setTextColor(Theme.getColor(i2));
        textView.setTextSize(1, 20.0f);
        textView.setGravity(17);
        textView.setText(LocaleController.getString(UserObject.isBot(user) ? R.string.BotVerifyBotTitle : user != null ? R.string.BotVerifyUserTitle : ChatObject.isChannelAndNotMegaGroup(chat) ? R.string.BotVerifyChannelTitle : R.string.BotVerifyGroupTitle));
        textView.setTypeface(AndroidUtilities.bold());
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 24.0f, 21.0f, 24.0f, 8.33f));
        TextView textView2 = new TextView(context);
        textView2.setTextColor(Theme.getColor(i2));
        textView2.setTextSize(1, 14.0f);
        textView2.setGravity(17);
        NotificationCenter.listenEmojiLoading(textView2);
        textView2.setText(Emoji.replaceEmoji(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.BotVerifyText, str)), textView2.getPaint().getFontMetricsInt(), false));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 24.0f, 0.0f, 24.0f, 22.0f));
        final int i3 = MessagesController.getInstance(i).botVerificationDescriptionLengthLimit;
        final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
        final OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context);
        outlineTextContainerView.setForceForceUseCenter(true);
        outlineTextContainerView.setText(LocaleController.getString(R.string.BotVerifyDescription));
        outlineTextContainerView.setLeftPadding(AndroidUtilities.dp(2.0f));
        editTextBoldCursor.setTextColor(Theme.getColor(i2));
        editTextBoldCursor.setCursorSize(AndroidUtilities.dp(20.0f));
        editTextBoldCursor.setCursorWidth(1.5f);
        editTextBoldCursor.setBackground(null);
        editTextBoldCursor.setTextSize(1, 18.0f);
        editTextBoldCursor.setMaxLines(15);
        editTextBoldCursor.setInputType(180225);
        editTextBoldCursor.setTypeface(Typeface.DEFAULT);
        editTextBoldCursor.setSelectAllOnFocus(true);
        editTextBoldCursor.setHighlightColor(Theme.getColor(Theme.key_chat_inTextSelectionHighlight));
        editTextBoldCursor.setHandlesColor(Theme.getColor(Theme.key_chat_TextSelectionCursor));
        editTextBoldCursor.setGravity(LocaleController.isRTL ? 5 : 3);
        editTextBoldCursor.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public final void onFocusChange(View view2, boolean z) {
                BotVerifySheet.lambda$openSheet$2(OutlineTextContainerView.this, editTextBoldCursor, view2, z);
            }
        });
        outlineTextContainerView.attachEditText(editTextBoldCursor);
        outlineTextContainerView.addView(editTextBoldCursor, LayoutHelper.createFrame(-1, -2.0f, 48, 12.0f, 4.0f, 12.0f, 4.0f));
        int i4 = -1;
        linearLayout.addView(outlineTextContainerView, LayoutHelper.createLinear(-1, -2));
        editTextBoldCursor.addTextChangedListener(new EditTextSuggestionsFix());
        editTextBoldCursor.addTextChangedListener(new TextWatcher() {
            boolean ignoreEditText;

            @Override
            public void afterTextChanged(Editable editable) {
                CharSequence text = EditTextBoldCursor.this.getText();
                if (!this.ignoreEditText) {
                    int length = text.length();
                    int i5 = i3;
                    if (length > i5) {
                        this.ignoreEditText = true;
                        EditTextBoldCursor editTextBoldCursor2 = EditTextBoldCursor.this;
                        text = text.subSequence(0, i5);
                        editTextBoldCursor2.setText(text);
                        EditTextBoldCursor editTextBoldCursor3 = EditTextBoldCursor.this;
                        editTextBoldCursor3.setSelection(editTextBoldCursor3.length());
                        this.ignoreEditText = false;
                    }
                }
                outlineTextContainerView.animateSelection(EditTextBoldCursor.this.isFocused(), !TextUtils.isEmpty(text));
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i5, int i6, int i7) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i5, int i6, int i7) {
            }
        });
        if (!TextUtils.isEmpty(botverifiersettings.custom_description)) {
            editTextBoldCursor.setText(botverifiersettings.custom_description);
            if (!botverifiersettings.can_modify_custom_description) {
                editTextBoldCursor.setEnabled(false);
                editTextBoldCursor.setFocusable(false);
                editTextBoldCursor.setFocusableInTouchMode(false);
            }
        } else if (!botverifiersettings.can_modify_custom_description) {
            outlineTextContainerView.setVisibility(8);
        }
        if (botverifiersettings.can_modify_custom_description) {
            TextView textView3 = new TextView(context);
            textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
            textView3.setTextSize(1, 12.0f);
            textView3.setText(LocaleController.getString(j2 >= 0 ? R.string.BotVerifyDescriptionInfo : R.string.BotVerifyDescriptionInfoChat));
            textView3.setPadding(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(27.0f));
            i4 = -1;
            createFrame = LayoutHelper.createFrame(-1, -2.0f);
            view = textView3;
        } else {
            View view2 = new View(context);
            createFrame = LayoutHelper.createFrame(-1, 12.0f);
            view = view2;
        }
        linearLayout.addView(view, createFrame);
        final ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, null);
        buttonWithCounterView.setText(textView.getText(), false);
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(i4, 48));
        builder.setCustomView(linearLayout);
        final BottomSheet create = builder.create();
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                BotVerifySheet.lambda$openSheet$5(ButtonWithCounterView.this, botverifiersettings, editTextBoldCursor, i3, outlineTextContainerView, i, j, j2, create, callback, view3);
            }
        });
        create.smoothKeyboardAnimationEnabled = true;
        create.smoothKeyboardByBottom = true;
        create.show();
    }

    public static void openVerify(final int i, final long j, final TL_bots.botVerifierSettings botverifiersettings) {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putBoolean("onlySelect", true);
        bundle.putInt("dialogsType", 16);
        bundle.putBoolean("resetDelegate", false);
        final DialogsActivity dialogsActivity = new DialogsActivity(bundle);
        dialogsActivity.setCurrentAccount(i);
        dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
            @Override
            public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i2, TopicsFragment topicsFragment) {
                boolean lambda$openVerify$1;
                lambda$openVerify$1 = BotVerifySheet.lambda$openVerify$1(DialogsActivity.this, i, j, botverifiersettings, dialogsActivity2, arrayList, charSequence, z, z2, i2, topicsFragment);
                return lambda$openVerify$1;
            }
        });
        safeLastFragment.presentFragment(dialogsActivity);
    }
}
