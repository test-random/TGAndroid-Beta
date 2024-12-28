package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RLottieDrawable;

public class ShareDialogCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private final AvatarDrawable avatarDrawable;
    private final CheckBox2 checkBox;
    private final int currentAccount;
    private long currentDialog;
    private final int currentType;
    private final BackupImageView imageView;
    private long lastUpdateTime;
    private Drawable lockDrawable;
    private final TextView nameTextView;
    private float onlineProgress;
    private boolean premiumBlocked;
    private final AnimatedFloat premiumBlockedT;
    private PremiumGradient.PremiumGradientTools premiumGradient;
    private RepostStoryDrawable repostStoryDrawable;
    public final Theme.ResourcesProvider resourcesProvider;
    private final SimpleTextView topicTextView;
    private boolean topicWasVisible;
    private TLRPC.User user;

    public static class RepostStoryDrawable extends Drawable {
        int alpha;
        private final Drawable drawable;
        private final LinearGradient gradient;
        private final RLottieDrawable lottieDrawable;
        private final Paint paint;

        public RepostStoryDrawable(Context context, View view, boolean z, Theme.ResourcesProvider resourcesProvider) {
            Paint paint = new Paint(1);
            this.paint = paint;
            this.alpha = 255;
            LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f), new int[]{Theme.getColor(Theme.key_stories_circle1, resourcesProvider), Theme.getColor(Theme.key_stories_circle2, resourcesProvider)}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            this.gradient = linearGradient;
            paint.setShader(linearGradient);
            if (!z) {
                this.lottieDrawable = null;
                Drawable mutate = context.getResources().getDrawable(R.drawable.large_repost_story).mutate();
                this.drawable = mutate;
                mutate.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
                return;
            }
            RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.story_repost, "story_repost", AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f), true, null);
            this.lottieDrawable = rLottieDrawable;
            rLottieDrawable.setMasterParent(view);
            AndroidUtilities.runOnUIThread(new ShareDialogCell$RepostStoryDrawable$$ExternalSyntheticLambda0(rLottieDrawable), 450L);
            this.drawable = null;
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, getBounds().width(), getBounds().height());
            this.paint.setAlpha(this.alpha);
            float min = (Math.min(getBounds().width(), getBounds().height()) / 2.0f) * (this.alpha / 255.0f);
            canvas.drawRoundRect(rectF, min, min, this.paint);
            canvas.restore();
            int dp = AndroidUtilities.dp(this.lottieDrawable != null ? 20.0f : 15.0f);
            Rect rect = AndroidUtilities.rectTmp2;
            rect.set(getBounds().centerX() - dp, getBounds().centerY() - dp, getBounds().centerX() + dp, getBounds().centerY() + dp);
            Drawable drawable = this.lottieDrawable;
            if (drawable == null) {
                drawable = this.drawable;
            }
            if (drawable != null) {
                drawable.setBounds(rect);
                drawable.setAlpha(this.alpha);
                drawable.draw(canvas);
            }
        }

        @Override
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(56.0f);
        }

        @Override
        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(56.0f);
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void setAlpha(int i) {
            this.alpha = i;
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }
    }

    public ShareDialogCell(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        int i2;
        float f;
        this.avatarDrawable = new AvatarDrawable() {
            @Override
            public void invalidateSelf() {
                super.invalidateSelf();
                ShareDialogCell.this.imageView.invalidate();
            }
        };
        this.currentAccount = UserConfig.selectedAccount;
        this.premiumBlockedT = new AnimatedFloat(this, 0L, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.resourcesProvider = resourcesProvider;
        setWillNotDraw(false);
        this.currentType = i;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(28.0f));
        if (i == 2) {
            i2 = 48;
            f = 48.0f;
        } else {
            i2 = 56;
            f = 56.0f;
        }
        addView(backupImageView, LayoutHelper.createFrame(i2, f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context) {
            @Override
            public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
                super.setText(Emoji.replaceEmoji(charSequence, getPaint().getFontMetricsInt(), false), bufferType);
            }
        };
        this.nameTextView = textView;
        NotificationCenter.listenEmojiLoading(textView);
        textView.setTextColor(getThemedColor(this.premiumBlocked ? Theme.key_windowBackgroundWhiteGrayText5 : i == 1 ? Theme.key_voipgroup_nameText : Theme.key_dialogTextBlack));
        textView.setTextSize(1, 12.0f);
        textView.setMaxLines(2);
        textView.setGravity(49);
        textView.setLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        addView(textView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, i == 2 ? 58.0f : 66.0f, 6.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.topicTextView = simpleTextView;
        simpleTextView.setTextColor(getThemedColor(i == 1 ? Theme.key_voipgroup_nameText : Theme.key_dialogTextBlack));
        simpleTextView.setTextSize(12);
        simpleTextView.setMaxLines(2);
        simpleTextView.setGravity(49);
        simpleTextView.setAlignment(Layout.Alignment.ALIGN_CENTER);
        addView(simpleTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, i == 2 ? 58.0f : 66.0f, 6.0f, 0.0f));
        CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider);
        this.checkBox = checkBox2;
        checkBox2.setColor(Theme.key_dialogRoundCheckBox, i == 1 ? Theme.key_voipgroup_inviteMembersBackground : Theme.key_dialogBackground, Theme.key_dialogRoundCheckBoxCheck);
        checkBox2.setDrawUnchecked(false);
        checkBox2.setDrawBackgroundAsArc(4);
        checkBox2.setProgressDelegate(new CheckBoxBase.ProgressDelegate() {
            @Override
            public final void setProgress(float f2) {
                ShareDialogCell.this.lambda$new$0(f2);
            }
        });
        addView(checkBox2, LayoutHelper.createFrame(24, 24.0f, 49, 19.0f, i == 2 ? -40.0f : 42.0f, 0.0f, 0.0f));
        setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f)));
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void lambda$new$0(float f) {
        float progress = 1.0f - (this.checkBox.getProgress() * 0.143f);
        this.imageView.setScaleX(progress);
        this.imageView.setScaleY(progress);
        invalidate();
    }

    public void lambda$setTopic$1(DynamicAnimation dynamicAnimation, float f, float f2) {
        float f3 = f / 1000.0f;
        this.topicTextView.setAlpha(f3);
        float f4 = 1.0f - f3;
        this.nameTextView.setAlpha(f4);
        this.topicTextView.setTranslationX(f4 * (-AndroidUtilities.dp(10.0f)));
        this.nameTextView.setTranslationX(f3 * AndroidUtilities.dp(10.0f));
    }

    public void lambda$setTopic$2(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.topicTextView.setTag(R.id.spring_tag, null);
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.userIsPremiumBlockedUpadted) {
            boolean z = this.premiumBlocked;
            boolean z2 = this.user != null && MessagesController.getInstance(this.currentAccount).isUserPremiumBlocked(this.user.id);
            this.premiumBlocked = z2;
            this.nameTextView.setTextColor(getThemedColor(z2 ? Theme.key_windowBackgroundWhiteGrayText5 : this.currentType == 1 ? Theme.key_voipgroup_nameText : Theme.key_dialogTextBlack));
            if (this.premiumBlocked != z) {
                invalidate();
            }
        }
    }

    @Override
    protected boolean drawChild(android.graphics.Canvas r26, android.view.View r27, long r28) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ShareDialogCell.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
    }

    public long getCurrentDialog() {
        return this.currentDialog;
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public boolean isBlocked() {
        return this.premiumBlocked;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userIsPremiumBlockedUpadted);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userIsPremiumBlockedUpadted);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int left = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2);
        int top = this.imageView.getTop() + (this.imageView.getMeasuredHeight() / 2);
        Theme.checkboxSquare_checkPaint.setColor(getThemedColor(Theme.key_dialogRoundCheckBox));
        Theme.checkboxSquare_checkPaint.setAlpha((int) (this.checkBox.getProgress() * 255.0f));
        int dp = AndroidUtilities.dp(this.currentType == 2 ? 24.0f : 28.0f);
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(left - dp, top - dp, left + dp, top + dp);
        canvas.drawRoundRect(rectF, this.imageView.getRoundRadius()[0], this.imageView.getRoundRadius()[0], Theme.checkboxSquare_checkPaint);
        super.onDraw(canvas);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (this.checkBox.isChecked()) {
            accessibilityNodeInfo.setSelected(true);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.currentType == 2 ? 95.0f : 103.0f), 1073741824));
    }

    public String repostToCustomName() {
        return LocaleController.getString(R.string.FwdMyStory);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
        if (z) {
            return;
        }
        setTopic(null, true);
    }

    public void setDialog(long j, boolean z, CharSequence charSequence) {
        BackupImageView backupImageView;
        int dp;
        TextView textView;
        if (j == Long.MAX_VALUE) {
            this.nameTextView.setText(repostToCustomName());
            if (this.repostStoryDrawable == null) {
                this.repostStoryDrawable = new RepostStoryDrawable(getContext(), this.imageView, true, this.resourcesProvider);
            }
            this.imageView.setImage((ImageLocation) null, (String) null, this.repostStoryDrawable, (Object) null);
        } else {
            if (DialogObject.isUserDialog(j)) {
                this.user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
                boolean isUserPremiumBlocked = MessagesController.getInstance(this.currentAccount).isUserPremiumBlocked(j);
                this.premiumBlocked = isUserPremiumBlocked;
                this.nameTextView.setTextColor(getThemedColor(isUserPremiumBlocked ? Theme.key_windowBackgroundWhiteGrayText5 : this.currentType == 1 ? Theme.key_voipgroup_nameText : Theme.key_dialogTextBlack));
                this.premiumBlockedT.set(this.premiumBlocked, true);
                invalidate();
                this.avatarDrawable.setInfo(this.currentAccount, this.user);
                if (this.currentType != 2 && UserObject.isReplyUser(this.user)) {
                    this.nameTextView.setText(LocaleController.getString(R.string.RepliesTitle));
                    this.avatarDrawable.setAvatarType(12);
                } else if (this.currentType == 2 || !UserObject.isUserSelf(this.user)) {
                    if (charSequence != null) {
                        textView = this.nameTextView;
                    } else {
                        TLRPC.User user = this.user;
                        if (user != null) {
                            textView = this.nameTextView;
                            charSequence = ContactsController.formatName(user.first_name, user.last_name);
                        } else {
                            this.nameTextView.setText("");
                            this.imageView.setForUserOrChat(this.user, this.avatarDrawable);
                            backupImageView = this.imageView;
                        }
                    }
                    textView.setText(charSequence);
                    this.imageView.setForUserOrChat(this.user, this.avatarDrawable);
                    backupImageView = this.imageView;
                } else {
                    this.nameTextView.setText(LocaleController.getString(R.string.SavedMessages));
                    this.avatarDrawable.setAvatarType(1);
                }
                this.imageView.setImage((ImageLocation) null, (String) null, this.avatarDrawable, this.user);
                backupImageView = this.imageView;
            } else {
                this.user = null;
                this.premiumBlocked = false;
                this.premiumBlockedT.set(0.0f, true);
                TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j));
                if (charSequence != null) {
                    this.nameTextView.setText(charSequence);
                } else {
                    TextView textView2 = this.nameTextView;
                    if (chat != null) {
                        textView2.setText(chat.title);
                    } else {
                        textView2.setText("");
                    }
                }
                this.avatarDrawable.setInfo(this.currentAccount, chat);
                this.imageView.setForUserOrChat(chat, this.avatarDrawable);
                backupImageView = this.imageView;
                if (chat != null && chat.forum) {
                    dp = AndroidUtilities.dp(16.0f);
                    backupImageView.setRoundRadius(dp);
                }
            }
            dp = AndroidUtilities.dp(28.0f);
            backupImageView.setRoundRadius(dp);
        }
        this.currentDialog = j;
        this.checkBox.setChecked(z, false);
    }

    public void setTopic(TLRPC.TL_forumTopic tL_forumTopic, boolean z) {
        boolean z2 = this.topicWasVisible;
        boolean z3 = tL_forumTopic != null;
        if (z2 == z3 && z) {
            return;
        }
        SimpleTextView simpleTextView = this.topicTextView;
        int i = R.id.spring_tag;
        SpringAnimation springAnimation = (SpringAnimation) simpleTextView.getTag(i);
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        if (z3) {
            SimpleTextView simpleTextView2 = this.topicTextView;
            simpleTextView2.setText(ForumUtilities.getTopicSpannedName(tL_forumTopic, simpleTextView2.getTextPaint(), false));
            this.topicTextView.requestLayout();
        }
        if (z) {
            SpringAnimation springAnimation2 = (SpringAnimation) ((SpringAnimation) new SpringAnimation(new FloatValueHolder(z3 ? 0.0f : 1000.0f)).setSpring(new SpringForce(z3 ? 1000.0f : 0.0f).setStiffness(1500.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                @Override
                public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                    ShareDialogCell.this.lambda$setTopic$1(dynamicAnimation, f, f2);
                }
            })).addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z4, float f, float f2) {
                    ShareDialogCell.this.lambda$setTopic$2(dynamicAnimation, z4, f, f2);
                }
            });
            this.topicTextView.setTag(i, springAnimation2);
            springAnimation2.start();
        } else {
            SimpleTextView simpleTextView3 = this.topicTextView;
            if (z3) {
                simpleTextView3.setAlpha(1.0f);
                this.nameTextView.setAlpha(0.0f);
                this.topicTextView.setTranslationX(0.0f);
                this.nameTextView.setTranslationX(AndroidUtilities.dp(10.0f));
            } else {
                simpleTextView3.setAlpha(0.0f);
                this.nameTextView.setAlpha(1.0f);
                this.topicTextView.setTranslationX(-AndroidUtilities.dp(10.0f));
                this.nameTextView.setTranslationX(0.0f);
            }
        }
        this.topicWasVisible = z3;
    }
}
