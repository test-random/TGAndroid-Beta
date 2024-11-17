package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class MentionCell extends LinearLayout {
    private boolean attached;
    private final AvatarDrawable avatarDrawable;
    private Drawable emojiDrawable;
    private final BackupImageView imageView;
    private final TextView nameTextView;
    private boolean needsDivider;
    private final Theme.ResourcesProvider resourcesProvider;
    private final TextView usernameTextView;

    public MentionCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.needsDivider = false;
        this.resourcesProvider = resourcesProvider;
        setOrientation(0);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        this.avatarDrawable = avatarDrawable;
        avatarDrawable.setTextSize(AndroidUtilities.dp(18.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(14.0f));
        addView(backupImageView, LayoutHelper.createLinear(28, 28, 12.0f, 4.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context) {
            @Override
            public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
                super.setText(Emoji.replaceEmoji(charSequence, getPaint().getFontMetricsInt(), false), bufferType);
            }
        };
        this.nameTextView = textView;
        textView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setTextSize(1, 15.0f);
        textView.setSingleLine(true);
        textView.setGravity(3);
        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
        textView.setEllipsize(truncateAt);
        addView(textView, LayoutHelper.createLinear(-2, -2, 16, 12, 0, 0, 0));
        TextView textView2 = new TextView(context);
        this.usernameTextView = textView2;
        textView2.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText3));
        textView2.setTextSize(1, 15.0f);
        textView2.setSingleLine(true);
        textView2.setGravity(3);
        textView2.setEllipsize(truncateAt);
        addView(textView2, LayoutHelper.createLinear(-2, -2, 16, 12, 0, 8, 0));
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        Drawable drawable = this.emojiDrawable;
        if (drawable != null) {
            int dp = AndroidUtilities.dp(drawable instanceof AnimatedEmojiDrawable ? 24.0f : 20.0f);
            int dp2 = AndroidUtilities.dp(this.emojiDrawable instanceof AnimatedEmojiDrawable ? -2.0f : 0.0f);
            this.emojiDrawable.setBounds(this.nameTextView.getLeft() + dp2, ((this.nameTextView.getTop() + this.nameTextView.getBottom()) - dp) / 2, this.nameTextView.getLeft() + dp2 + dp, ((this.nameTextView.getTop() + this.nameTextView.getBottom()) + dp) / 2);
            Drawable drawable2 = this.emojiDrawable;
            if (drawable2 instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawable2).setTime(System.currentTimeMillis());
            }
            this.emojiDrawable.draw(canvas);
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.nameTextView.invalidate();
    }

    public void invalidateEmojis() {
        this.nameTextView.invalidate();
        this.usernameTextView.invalidate();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
        Drawable drawable = this.emojiDrawable;
        if (drawable instanceof AnimatedEmojiDrawable) {
            ((AnimatedEmojiDrawable) drawable).addView(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
        Drawable drawable = this.emojiDrawable;
        if (drawable instanceof AnimatedEmojiDrawable) {
            ((AnimatedEmojiDrawable) drawable).removeView(this);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.needsDivider) {
            canvas.drawLine(AndroidUtilities.dp(52.0f), getHeight() - 1, getWidth() - AndroidUtilities.dp(8.0f), getHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(36.0f), 1073741824));
    }

    public void resetEmojiSuggestion() {
        this.nameTextView.setPadding(0, 0, 0, 0);
        Drawable drawable = this.emojiDrawable;
        if (drawable != null) {
            if (drawable instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawable).removeView(this);
            }
            this.emojiDrawable = null;
            invalidate();
        }
    }

    public void setBotCommand(String str, String str2, TLRPC.User user) {
        resetEmojiSuggestion();
        if (user != null) {
            this.imageView.setVisibility(0);
            this.avatarDrawable.setInfo(user);
            TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
            if (userProfilePhoto == null || userProfilePhoto.photo_small == null) {
                this.imageView.setImageDrawable(this.avatarDrawable);
            } else {
                this.imageView.setForUserOrChat(user, this.avatarDrawable);
            }
        } else {
            this.imageView.setVisibility(4);
        }
        this.usernameTextView.setVisibility(0);
        this.nameTextView.setText(str);
        TextView textView = this.usernameTextView;
        textView.setText(Emoji.replaceEmoji((CharSequence) str2, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
    }

    public void setChat(TLRPC.Chat chat) {
        resetEmojiSuggestion();
        if (chat == null) {
            this.nameTextView.setText("");
            this.usernameTextView.setText("");
            this.imageView.setImageDrawable(null);
            return;
        }
        this.avatarDrawable.setInfo(chat);
        TLRPC.ChatPhoto chatPhoto = chat.photo;
        if (chatPhoto == null || chatPhoto.photo_small == null) {
            this.imageView.setImageDrawable(this.avatarDrawable);
        } else {
            this.imageView.setForUserOrChat(chat, this.avatarDrawable);
        }
        this.nameTextView.setText(chat.title);
        String publicUsername = ChatObject.getPublicUsername(chat);
        if (publicUsername != null) {
            this.usernameTextView.setText("@" + publicUsername);
        } else {
            this.usernameTextView.setText("");
        }
        this.imageView.setVisibility(0);
        this.usernameTextView.setVisibility(0);
    }

    public void setDivider(boolean z) {
        if (z != this.needsDivider) {
            this.needsDivider = z;
            setWillNotDraw(!z);
            invalidate();
        }
    }

    public void setEmojiSuggestion(org.telegram.messenger.MediaDataController.KeywordResult r5) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.MentionCell.setEmojiSuggestion(org.telegram.messenger.MediaDataController$KeywordResult):void");
    }

    public void setIsDarkTheme(boolean z) {
        TextView textView;
        int themedColor;
        if (z) {
            this.nameTextView.setTextColor(-1);
            textView = this.usernameTextView;
            themedColor = -4473925;
        } else {
            this.nameTextView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            textView = this.usernameTextView;
            themedColor = getThemedColor(Theme.key_windowBackgroundWhiteGrayText3);
        }
        textView.setTextColor(themedColor);
    }

    public void setText(String str) {
        resetEmojiSuggestion();
        this.imageView.setVisibility(4);
        this.usernameTextView.setVisibility(4);
        this.nameTextView.setText(str);
    }

    public void setUser(TLRPC.User user) {
        resetEmojiSuggestion();
        if (user == null) {
            this.nameTextView.setText("");
            this.usernameTextView.setText("");
            this.imageView.setImageDrawable(null);
            return;
        }
        this.avatarDrawable.setInfo(user);
        TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
        if (userProfilePhoto == null || userProfilePhoto.photo_small == null) {
            this.imageView.setImageDrawable(this.avatarDrawable);
        } else {
            this.imageView.setForUserOrChat(user, this.avatarDrawable);
        }
        this.nameTextView.setText(UserObject.getUserName(user));
        if (UserObject.getPublicUsername(user) != null) {
            this.usernameTextView.setText("@" + UserObject.getPublicUsername(user));
        } else {
            this.usernameTextView.setText("");
        }
        this.imageView.setVisibility(0);
        this.usernameTextView.setVisibility(0);
    }
}
