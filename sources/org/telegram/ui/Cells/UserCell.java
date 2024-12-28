package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stories.StoriesListPlaceProvider;
import org.telegram.ui.Stories.StoriesUtilities;

public class UserCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private TextView addButton;
    private TextView adminTextView;
    protected AvatarDrawable avatarDrawable;
    public BackupImageView avatarImageView;
    private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable botVerification;
    private CheckBox2 checkBox;
    private ImageView checkBox3;
    private CheckBoxSquare checkBoxBig;
    private ImageView closeView;
    private int currentAccount;
    private int currentDrawable;
    private int currentId;
    private CharSequence currentName;
    private Object currentObject;
    private CharSequence currentStatus;
    protected long dialogId;
    private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable emojiStatus;
    private TLRPC.EncryptedChat encryptedChat;
    private ImageView imageView;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    protected SimpleTextView nameTextView;
    public boolean needDivider;
    private Drawable premiumDrawable;
    protected Theme.ResourcesProvider resourcesProvider;
    private boolean selfAsSavedMessages;
    private int statusColor;
    private int statusOnlineColor;
    protected SimpleTextView statusTextView;
    private boolean storiable;
    public StoriesUtilities.AvatarStoryParams storyParams;

    public UserCell(Context context, int i, int i2, boolean z) {
        this(context, i, i2, z, false, null);
    }

    public UserCell(Context context, int i, int i2, boolean z, Theme.ResourcesProvider resourcesProvider) {
        this(context, i, i2, z, false, resourcesProvider);
    }

    public UserCell(Context context, int i, int i2, boolean z, boolean z2) {
        this(context, i, i2, z, z2, null);
    }

    public UserCell(android.content.Context r30, int r31, int r32, boolean r33, boolean r34, org.telegram.ui.ActionBar.Theme.ResourcesProvider r35) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.UserCell.<init>(android.content.Context, int, int, boolean, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.emojiLoaded) {
            this.nameTextView.invalidate();
        }
    }

    public Object getCurrentObject() {
        return this.currentObject;
    }

    public long getDialogId() {
        return this.dialogId;
    }

    public CharSequence getName() {
        return this.nameTextView.getText();
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    public void invalidate() {
        super.invalidate();
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            checkBoxSquare.invalidate();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.emojiStatus.attach();
        this.botVerification.attach();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        this.emojiStatus.detach();
        this.botVerification.detach();
        this.storyParams.onDetachFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(68.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        boolean isChecked;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare == null || checkBoxSquare.getVisibility() != 0) {
            CheckBox2 checkBox2 = this.checkBox;
            if (checkBox2 == null || checkBox2.getVisibility() != 0) {
                return;
            }
            accessibilityNodeInfo.setCheckable(true);
            isChecked = this.checkBox.isChecked();
        } else {
            accessibilityNodeInfo.setCheckable(true);
            isChecked = this.checkBoxBig.isChecked();
        }
        accessibilityNodeInfo.setChecked(isChecked);
        accessibilityNodeInfo.setClassName("android.widget.CheckBox");
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    public void openStory(long j, Runnable runnable) {
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment != null) {
            lastFragment.getOrCreateStoryViewer().doOnAnimationReady(runnable);
            lastFragment.getOrCreateStoryViewer().open(getContext(), j, StoriesListPlaceProvider.of((RecyclerListView) getParent()));
        }
    }

    public void setAddButtonVisible(boolean z) {
        TextView textView = this.addButton;
        if (textView == null) {
            return;
        }
        textView.setVisibility(z ? 0 : 8);
    }

    public void setAdminRole(String str) {
        TextView textView = this.adminTextView;
        if (textView == null) {
            return;
        }
        textView.setVisibility(str != null ? 0 : 8);
        this.adminTextView.setText(str);
        if (str == null) {
            setRightPadding(0, true, false);
        } else {
            CharSequence text = this.adminTextView.getText();
            setRightPadding((int) Math.ceil(this.adminTextView.getPaint().measureText(text, 0, text.length())), true, false);
        }
    }

    public void setAvatarPadding(int i) {
        int i2;
        float f;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.avatarImageView.getLayoutParams();
        layoutParams.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : i + 7);
        layoutParams.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? i + 7 : 0.0f);
        this.avatarImageView.setLayoutParams(layoutParams);
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
        if (LocaleController.isRTL) {
            i2 = (this.checkBoxBig != null ? 18 : 0) + 28;
        } else {
            i2 = i + 64;
        }
        layoutParams2.leftMargin = AndroidUtilities.dp(i2);
        if (LocaleController.isRTL) {
            f = i + 64;
        } else {
            f = (this.checkBoxBig != null ? 18 : 0) + 28;
        }
        layoutParams2.rightMargin = AndroidUtilities.dp(f);
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) this.statusTextView.getLayoutParams();
        layoutParams3.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 28.0f : i + 64);
        layoutParams3.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? i + 64 : 28.0f);
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            FrameLayout.LayoutParams layoutParams4 = (FrameLayout.LayoutParams) checkBox2.getLayoutParams();
            layoutParams4.leftMargin = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : i + 37);
            layoutParams4.rightMargin = AndroidUtilities.dp(LocaleController.isRTL ? i + 37 : 0.0f);
        }
    }

    public void setCheckDisabled(boolean z) {
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            checkBoxSquare.setDisabled(z);
        }
    }

    public void setChecked(boolean z, boolean z2) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            if (checkBox2.getVisibility() != 0) {
                this.checkBox.setVisibility(0);
            }
            this.checkBox.setChecked(z, z2);
            return;
        }
        CheckBoxSquare checkBoxSquare = this.checkBoxBig;
        if (checkBoxSquare != null) {
            if (checkBoxSquare.getVisibility() != 0) {
                this.checkBoxBig.setVisibility(0);
            }
            this.checkBoxBig.setChecked(z, z2);
        } else {
            ImageView imageView = this.checkBox3;
            if (imageView != null) {
                imageView.setVisibility(z ? 0 : 8);
            }
        }
    }

    public void setCloseIcon(View.OnClickListener onClickListener) {
        if (onClickListener == null) {
            ImageView imageView = this.closeView;
            if (imageView != null) {
                removeView(imageView);
                this.closeView = null;
                return;
            }
            return;
        }
        if (this.closeView == null) {
            ImageView imageView2 = new ImageView(getContext());
            this.closeView = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            ScaleStateListAnimator.apply(this.closeView);
            this.closeView.setImageResource(R.drawable.ic_close_white);
            this.closeView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3, this.resourcesProvider), PorterDuff.Mode.SRC_IN));
            this.closeView.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector, this.resourcesProvider), 5));
            ImageView imageView3 = this.closeView;
            boolean z = LocaleController.isRTL;
            addView(imageView3, LayoutHelper.createFrame(30, 30.0f, (z ? 3 : 5) | 16, z ? 14.0f : 0.0f, 0.0f, z ? 0.0f : 14.0f, 0.0f));
        }
        this.closeView.setOnClickListener(onClickListener);
    }

    public void setCurrentId(int i) {
        this.currentId = i;
    }

    public void setData(Object obj, CharSequence charSequence, CharSequence charSequence2, int i) {
        setData(obj, null, charSequence, charSequence2, i, false);
    }

    public void setData(Object obj, CharSequence charSequence, CharSequence charSequence2, int i, boolean z) {
        setData(obj, null, charSequence, charSequence2, i, z);
    }

    public void setData(Object obj, TLRPC.EncryptedChat encryptedChat, CharSequence charSequence, CharSequence charSequence2, int i, boolean z) {
        if (obj == null && charSequence == null && charSequence2 == null) {
            this.currentStatus = null;
            this.currentName = null;
            this.storiable = false;
            this.currentObject = null;
            this.nameTextView.setText("");
            this.statusTextView.setText("");
            this.avatarImageView.setImageDrawable(null);
            return;
        }
        this.encryptedChat = encryptedChat;
        this.currentStatus = charSequence2;
        if (charSequence != null) {
            try {
                SimpleTextView simpleTextView = this.nameTextView;
                if (simpleTextView != null) {
                    charSequence = Emoji.replaceEmoji(charSequence, simpleTextView.getPaint().getFontMetricsInt(), false);
                }
            } catch (Exception unused) {
            }
        }
        this.currentName = charSequence;
        this.storiable = !(obj instanceof String);
        this.currentObject = obj;
        this.currentDrawable = i;
        this.needDivider = z;
        setWillNotDraw(!z);
        update(0);
    }

    public void setException(org.telegram.ui.NotificationsSettingsActivity.NotificationException r9, java.lang.CharSequence r10, boolean r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.UserCell.setException(org.telegram.ui.NotificationsSettingsActivity$NotificationException, java.lang.CharSequence, boolean):void");
    }

    public void setFromUItem(int i, UItem uItem, boolean z) {
        int i2;
        int i3;
        String str;
        String formatPluralStringComma;
        TLRPC.Chat chat;
        TLRPC.Chat chat2;
        String str2 = uItem.chatType;
        if (str2 != null) {
            setData(str2, uItem.text, null, 0, z);
            return;
        }
        long j = uItem.dialogId;
        MessagesController messagesController = MessagesController.getInstance(i);
        if (j > 0) {
            TLRPC.User user = messagesController.getUser(Long.valueOf(j));
            if (user == null) {
                return;
            }
            if (user.bot) {
                i2 = R.string.Bot;
                chat2 = user;
            } else if (user.contact) {
                i2 = R.string.FilterContact;
                chat2 = user;
            } else {
                i2 = R.string.FilterNonContact;
                chat2 = user;
            }
        } else {
            TLRPC.Chat chat3 = messagesController.getChat(Long.valueOf(-j));
            if (chat3 == null) {
                return;
            }
            if (chat3.participants_count != 0) {
                if (ChatObject.isChannelAndNotMegaGroup(chat3)) {
                    i3 = chat3.participants_count;
                    str = "Subscribers";
                } else {
                    i3 = chat3.participants_count;
                    str = "Members";
                }
                formatPluralStringComma = LocaleController.formatPluralStringComma(str, i3);
                chat = chat3;
                setData(chat, null, formatPluralStringComma, 0, z);
            }
            if (ChatObject.isPublic(chat3)) {
                if (!ChatObject.isChannel(chat3) || chat3.megagroup) {
                    i2 = R.string.MegaPublic;
                    chat2 = chat3;
                } else {
                    i2 = R.string.ChannelPublic;
                    chat2 = chat3;
                }
            } else if (!ChatObject.isChannel(chat3) || chat3.megagroup) {
                i2 = R.string.MegaPrivate;
                chat2 = chat3;
            } else {
                i2 = R.string.ChannelPrivate;
                chat2 = chat3;
            }
        }
        formatPluralStringComma = LocaleController.getString(i2);
        chat = chat2;
        setData(chat, null, formatPluralStringComma, 0, z);
    }

    public void setNameTypeface(Typeface typeface) {
        this.nameTextView.setTypeface(typeface);
    }

    public void setRightPadding(int i, boolean z, boolean z2) {
        if (i > 0) {
            i += AndroidUtilities.dp(6.0f);
        }
        if (z) {
            SimpleTextView simpleTextView = this.nameTextView;
            boolean z3 = LocaleController.isRTL;
            simpleTextView.setPadding(z3 ? i : 0, 0, !z3 ? i : 0, 0);
        }
        if (z2) {
            SimpleTextView simpleTextView2 = this.statusTextView;
            boolean z4 = LocaleController.isRTL;
            int i2 = z4 ? i : 0;
            if (z4) {
                i = 0;
            }
            simpleTextView2.setPadding(i2, 0, i, 0);
        }
    }

    public void setSelfAsSavedMessages(boolean z) {
        this.selfAsSavedMessages = z;
    }

    public void update(int r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.UserCell.update(int):void");
    }
}
