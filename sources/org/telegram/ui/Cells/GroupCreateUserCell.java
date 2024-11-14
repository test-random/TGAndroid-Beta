package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;

public class GroupCreateUserCell extends FrameLayout {
    private ValueAnimator animator;
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private CheckBox2 checkBox;
    private int checkBoxType;
    private float checkProgress;
    private int currentAccount;
    public boolean currentMiniapps;
    private CharSequence currentName;
    private Object currentObject;
    public boolean currentPremium;
    private CharSequence currentStatus;
    private boolean drawDivider;
    private boolean forceDarkTheme;
    private boolean isChecked;
    private TLRPC.FileLocation lastAvatar;
    private String lastName;
    private int lastStatus;
    private Paint lockBackgroundPaint;
    private Drawable lockDrawable;
    private SimpleTextView nameTextView;
    private int padding;
    private Paint paint;
    private boolean premiumBlocked;
    private Boolean premiumBlockedOverriden;
    private final AnimatedFloat premiumBlockedT;
    private PremiumGradient.PremiumGradientTools premiumGradient;
    Theme.ResourcesProvider resourcesProvider;
    private boolean showPremiumBlocked;
    private boolean showSelfAsSaved;
    private SimpleTextView statusTextView;

    public GroupCreateUserCell(Context context, int i, int i2, boolean z) {
        this(context, i, i2, z, false, null);
    }

    public GroupCreateUserCell(Context context, int i, int i2, boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.premiumBlockedT = new AnimatedFloat(this, 0L, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.resourcesProvider = resourcesProvider;
        this.checkBoxType = i;
        this.forceDarkTheme = z2;
        this.drawDivider = false;
        this.padding = i2;
        this.showSelfAsSaved = z;
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        BackupImageView backupImageView2 = this.avatarImageView;
        boolean z3 = LocaleController.isRTL;
        addView(backupImageView2, LayoutHelper.createFrame(46, 46.0f, (z3 ? 5 : 3) | 48, z3 ? 0.0f : this.padding + 13, 6.0f, z3 ? this.padding + 13 : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context) {
            @Override
            public boolean setText(CharSequence charSequence, boolean z4) {
                return super.setText(Emoji.replaceEmoji(charSequence, getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false), z4);
            }
        };
        this.nameTextView = simpleTextView;
        NotificationCenter.listenEmojiLoading(simpleTextView);
        this.nameTextView.setTextColor(Theme.getColor(this.forceDarkTheme ? Theme.key_voipgroup_nameText : Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        this.nameTextView.setTypeface(AndroidUtilities.bold());
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        boolean z4 = LocaleController.isRTL;
        int i3 = (z4 ? 5 : 3) | 48;
        int i4 = z4 ? 28 : 72;
        int i5 = this.padding;
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, i3, i4 + i5, 10.0f, (z4 ? 72 : 28) + i5, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context);
        this.statusTextView = simpleTextView3;
        simpleTextView3.setTextSize(14);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView4 = this.statusTextView;
        boolean z5 = LocaleController.isRTL;
        int i6 = (z5 ? 5 : 3) | 48;
        int i7 = z5 ? 28 : 72;
        int i8 = this.padding;
        addView(simpleTextView4, LayoutHelper.createFrame(-1, 20.0f, i6, i7 + i8, 32.0f, (z5 ? 72 : 28) + i8, 0.0f));
        if (i == 1) {
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor(-1, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            CheckBox2 checkBox22 = this.checkBox;
            boolean z6 = LocaleController.isRTL;
            addView(checkBox22, LayoutHelper.createFrame(24, 24.0f, (z6 ? 5 : 3) | 48, z6 ? 0.0f : this.padding + 40, 33.0f, z6 ? this.padding + 39 : 0.0f, 0.0f));
        } else if (i == 2) {
            Paint paint = new Paint(1);
            this.paint = paint;
            paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        }
        setWillNotDraw(false);
    }

    public void lambda$setChecked$1(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f = 0.18f * floatValue;
        float f2 = this.isChecked ? 1.0f - f : 0.82f + f;
        this.avatarImageView.setScaleX(f2);
        this.avatarImageView.setScaleY(f2);
        if (!this.isChecked) {
            floatValue = 1.0f - floatValue;
        }
        this.checkProgress = floatValue;
        invalidate();
    }

    public static Drawable makeMiniAppsDrawable(Context context, boolean z) {
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setAvatarType(8);
        avatarDrawable.setScaleSize(z ? 0.8f : 1.1f);
        avatarDrawable.setColor(Theme.getColor(Theme.key_avatar_backgroundBlue), Theme.getColor(Theme.key_avatar_background2Blue));
        return avatarDrawable;
    }

    public static Drawable makePremiumUsersDrawable(Context context, boolean z) {
        final PremiumGradient.PremiumGradientTools premiumGradientTools = new PremiumGradient.PremiumGradientTools(Theme.key_premiumGradient2, Theme.key_premiumGradient1, -1, -1, -1, null);
        CombinedDrawable combinedDrawable = new CombinedDrawable(new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                PremiumGradient.PremiumGradientTools.this.gradientMatrix(getBounds());
                canvas.drawCircle(getBounds().centerX(), getBounds().centerY(), Math.min(getBounds().width(), getBounds().height()) / 2.0f, PremiumGradient.PremiumGradientTools.this.paint);
            }

            @Override
            public int getOpacity() {
                return -2;
            }

            @Override
            public void setAlpha(int i) {
            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {
            }
        }, context.getResources().getDrawable(R.drawable.msg_settings_premium), 0, 0);
        if (z) {
            combinedDrawable.setIconSize(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f));
        }
        return combinedDrawable;
    }

    private void updatePremiumBlocked(boolean z) {
        Boolean bool;
        boolean z2 = this.premiumBlocked;
        boolean z3 = this.showPremiumBlocked && ((bool = this.premiumBlockedOverriden) == null ? (this.currentObject instanceof TLRPC.User) && MessagesController.getInstance(this.currentAccount).isUserPremiumBlocked(((TLRPC.User) this.currentObject).id) : bool.booleanValue());
        this.premiumBlocked = z3;
        if (z2 != z3) {
            if (!z) {
                this.premiumBlockedT.set(z3, true);
            }
            invalidate();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        Paint paint;
        super.dispatchDraw(canvas);
        float f = this.premiumBlockedT.set(this.premiumBlocked);
        if (f > 0.0f) {
            float y = this.avatarImageView.getY() + (this.avatarImageView.getHeight() / 2.0f) + AndroidUtilities.dp(18.0f);
            float x = this.avatarImageView.getX() + (this.avatarImageView.getWidth() / 2.0f) + AndroidUtilities.dp(18.0f);
            canvas.save();
            Theme.dialogs_onlineCirclePaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite, this.resourcesProvider));
            canvas.drawCircle(x, y, AndroidUtilities.dp(11.33f) * f, Theme.dialogs_onlineCirclePaint);
            if (this.premiumBlockedOverriden == null) {
                if (this.premiumGradient == null) {
                    this.premiumGradient = new PremiumGradient.PremiumGradientTools(Theme.key_premiumGradient1, Theme.key_premiumGradient2, -1, -1, -1, this.resourcesProvider);
                }
                this.premiumGradient.gradientMatrix((int) (x - AndroidUtilities.dp(10.0f)), (int) (y - AndroidUtilities.dp(10.0f)), (int) (AndroidUtilities.dp(10.0f) + x), (int) (AndroidUtilities.dp(10.0f) + y), 0.0f, 0.0f);
                paint = this.premiumGradient.paint;
            } else {
                if (this.lockBackgroundPaint == null) {
                    this.lockBackgroundPaint = new Paint();
                }
                this.lockBackgroundPaint.setColor(Theme.getColor(Theme.key_avatar_backgroundGray, this.resourcesProvider));
                paint = this.lockBackgroundPaint;
            }
            canvas.drawCircle(x, y, AndroidUtilities.dp(10.0f) * f, paint);
            if (this.lockDrawable == null) {
                Drawable mutate = getContext().getResources().getDrawable(R.drawable.msg_mini_lock2).mutate();
                this.lockDrawable = mutate;
                mutate.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
            }
            this.lockDrawable.setBounds((int) (x - (((r4.getIntrinsicWidth() / 2.0f) * 0.875f) * f)), (int) (y - (((this.lockDrawable.getIntrinsicHeight() / 2.0f) * 0.875f) * f)), (int) (x + ((this.lockDrawable.getIntrinsicWidth() / 2.0f) * 0.875f * f)), (int) (y + ((this.lockDrawable.getIntrinsicHeight() / 2.0f) * 0.875f * f)));
            this.lockDrawable.setAlpha((int) (f * 255.0f));
            this.lockDrawable.draw(canvas);
            canvas.restore();
        }
    }

    public CheckBox2 getCheckBox() {
        return this.checkBox;
    }

    public Object getObject() {
        return this.currentObject;
    }

    public SimpleTextView getStatusTextView() {
        return this.statusTextView;
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean isBlocked() {
        return this.premiumBlocked;
    }

    public boolean isChecked() {
        CheckBox2 checkBox2 = this.checkBox;
        return checkBox2 != null ? checkBox2.isChecked() : this.isChecked;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float f;
        float measuredHeight;
        float f2;
        float measuredHeight2;
        Paint paint;
        super.onDraw(canvas);
        if (this.premiumBlockedT.set(this.premiumBlocked) <= 0.0f && this.checkBoxType == 2 && (this.isChecked || this.checkProgress > 0.0f)) {
            this.paint.setColor(Theme.getColor(Theme.key_checkboxSquareBackground, this.resourcesProvider));
            canvas.drawCircle(this.avatarImageView.getLeft() + (this.avatarImageView.getMeasuredWidth() / 2), this.avatarImageView.getTop() + (this.avatarImageView.getMeasuredHeight() / 2), AndroidUtilities.dp(18.0f) + (AndroidUtilities.dp(4.0f) * this.checkProgress), this.paint);
        }
        if (this.drawDivider) {
            int dp = AndroidUtilities.dp(LocaleController.isRTL ? 0.0f : this.padding + 72);
            int measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(LocaleController.isRTL ? this.padding + 72 : 0.0f);
            if (this.forceDarkTheme) {
                Theme.dividerExtraPaint.setColor(Theme.getColor(Theme.key_voipgroup_actionBar, this.resourcesProvider));
                f = dp;
                measuredHeight = getMeasuredHeight() - 1;
                f2 = measuredWidth;
                measuredHeight2 = getMeasuredHeight();
                paint = Theme.dividerExtraPaint;
            } else {
                f = dp;
                measuredHeight = getMeasuredHeight() - 1;
                f2 = measuredWidth;
                measuredHeight2 = getMeasuredHeight();
                paint = Theme.dividerPaint;
            }
            canvas.drawRect(f, measuredHeight, f2, measuredHeight2, paint);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (isChecked()) {
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(true);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824);
        Object obj = this.currentObject;
        super.onMeasure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((!(obj instanceof String) || "premium".equalsIgnoreCase((String) obj) || "miniapps".equalsIgnoreCase((String) this.currentObject)) ? 58.0f : 50.0f), 1073741824));
    }

    public void overridePremiumBlocked(boolean z, boolean z2) {
        this.showPremiumBlocked = true;
        this.premiumBlockedOverriden = Boolean.valueOf(z);
        updatePremiumBlocked(z2);
    }

    public void recycle() {
        this.avatarImageView.getImageReceiver().cancelLoadImage();
    }

    public void setCheckBoxEnabled(boolean z) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setEnabled(z);
        }
    }

    public void setChecked(boolean z, boolean z2) {
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setChecked(z, z2);
            return;
        }
        if (this.checkBoxType != 2 || this.isChecked == z) {
            return;
        }
        this.isChecked = z;
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (z2) {
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    GroupCreateUserCell.this.lambda$setChecked$1(valueAnimator2);
                }
            });
            this.animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    GroupCreateUserCell.this.animator = null;
                }
            });
            this.animator.setDuration(180L);
            this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.animator.start();
        } else {
            this.avatarImageView.setScaleX(this.isChecked ? 0.82f : 1.0f);
            this.avatarImageView.setScaleY(this.isChecked ? 0.82f : 1.0f);
            this.checkProgress = this.isChecked ? 1.0f : 0.0f;
        }
        invalidate();
    }

    public void setDrawDivider(boolean z) {
        this.drawDivider = z;
        invalidate();
    }

    public void setForbiddenCheck(boolean z) {
        this.checkBox.setForbidden(z);
    }

    public void setMiniapps() {
        this.currentMiniapps = true;
        this.currentObject = "miniapps";
        this.avatarImageView.setImageDrawable(makeMiniAppsDrawable(getContext(), false));
        this.nameTextView.setText(LocaleController.getString(R.string.PrivacyMiniapps));
        SimpleTextView simpleTextView = this.statusTextView;
        int i = Theme.key_windowBackgroundWhiteGrayText;
        simpleTextView.setTag(Integer.valueOf(i));
        SimpleTextView simpleTextView2 = this.statusTextView;
        if (this.forceDarkTheme) {
            i = Theme.key_voipgroup_lastSeenText;
        }
        simpleTextView2.setTextColor(Theme.getColor(i, this.resourcesProvider));
        this.statusTextView.setText(LocaleController.getString(R.string.PrivacyMiniappsText));
    }

    public void setObject(Object obj, CharSequence charSequence, CharSequence charSequence2) {
        this.currentObject = obj;
        this.currentStatus = charSequence2;
        this.currentName = charSequence;
        this.drawDivider = false;
        this.currentPremium = false;
        this.currentMiniapps = false;
        update(0);
    }

    public void setObject(TLObject tLObject, CharSequence charSequence, CharSequence charSequence2, boolean z) {
        setObject(tLObject, charSequence, charSequence2);
        this.drawDivider = z;
    }

    public void setPremium() {
        this.currentPremium = true;
        this.currentObject = "premium";
        this.avatarImageView.setImageDrawable(makePremiumUsersDrawable(getContext(), false));
        this.nameTextView.setText(LocaleController.getString(R.string.PrivacyPremium));
        SimpleTextView simpleTextView = this.statusTextView;
        int i = Theme.key_windowBackgroundWhiteGrayText;
        simpleTextView.setTag(Integer.valueOf(i));
        SimpleTextView simpleTextView2 = this.statusTextView;
        if (this.forceDarkTheme) {
            i = Theme.key_voipgroup_lastSeenText;
        }
        simpleTextView2.setTextColor(Theme.getColor(i, this.resourcesProvider));
        this.statusTextView.setText(LocaleController.getString(R.string.PrivacyPremiumText));
    }

    public void update(int i) {
        FrameLayout.LayoutParams layoutParams;
        int dp;
        String str;
        SimpleTextView simpleTextView;
        int i2;
        String string;
        String str2;
        TLRPC.FileLocation fileLocation;
        String str3;
        SimpleTextView simpleTextView2;
        int i3;
        TLRPC.UserStatus userStatus;
        String formatUserStatus;
        TLRPC.FileLocation fileLocation2;
        AvatarDrawable avatarDrawable;
        int i4;
        Object obj = this.currentObject;
        if (obj == null || this.currentPremium || this.currentMiniapps) {
            return;
        }
        TLRPC.Chat chat = null;
        if (obj instanceof String) {
            ((FrameLayout.LayoutParams) this.nameTextView.getLayoutParams()).topMargin = AndroidUtilities.dp(15.0f);
            ViewGroup.LayoutParams layoutParams2 = this.avatarImageView.getLayoutParams();
            ViewGroup.LayoutParams layoutParams3 = this.avatarImageView.getLayoutParams();
            int dp2 = AndroidUtilities.dp(38.0f);
            layoutParams3.height = dp2;
            layoutParams2.width = dp2;
            CheckBox2 checkBox2 = this.checkBox;
            if (checkBox2 != null) {
                ((FrameLayout.LayoutParams) checkBox2.getLayoutParams()).topMargin = AndroidUtilities.dp(25.0f);
                if (LocaleController.isRTL) {
                    ((FrameLayout.LayoutParams) this.checkBox.getLayoutParams()).rightMargin = AndroidUtilities.dp(31.0f);
                } else {
                    ((FrameLayout.LayoutParams) this.checkBox.getLayoutParams()).leftMargin = AndroidUtilities.dp(32.0f);
                }
            }
            String str4 = (String) this.currentObject;
            str4.hashCode();
            char c = 65535;
            switch (str4.hashCode()) {
                case -1716307998:
                    if (str4.equals("archived")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1237460524:
                    if (str4.equals("groups")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1197490811:
                    if (str4.equals("non_contacts")) {
                        c = 2;
                        break;
                    }
                    break;
                case -567451565:
                    if (str4.equals("contacts")) {
                        c = 3;
                        break;
                    }
                    break;
                case -268161860:
                    if (str4.equals("new_chats")) {
                        c = 4;
                        break;
                    }
                    break;
                case 3029900:
                    if (str4.equals("bots")) {
                        c = 5;
                        break;
                    }
                    break;
                case 3496342:
                    if (str4.equals("read")) {
                        c = 6;
                        break;
                    }
                    break;
                case 104264043:
                    if (str4.equals("muted")) {
                        c = 7;
                        break;
                    }
                    break;
                case 151051367:
                    if (str4.equals("existing_chats")) {
                        c = '\b';
                        break;
                    }
                    break;
                case 1432626128:
                    if (str4.equals("channels")) {
                        c = '\t';
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    avatarDrawable = this.avatarDrawable;
                    i4 = 11;
                    avatarDrawable.setAvatarType(i4);
                    break;
                case 1:
                    this.avatarDrawable.setAvatarType(6);
                    break;
                case 2:
                    this.avatarDrawable.setAvatarType(5);
                    break;
                case 3:
                    this.avatarDrawable.setAvatarType(4);
                    break;
                case 4:
                    avatarDrawable = this.avatarDrawable;
                    i4 = 24;
                    avatarDrawable.setAvatarType(i4);
                    break;
                case 5:
                    this.avatarDrawable.setAvatarType(8);
                    break;
                case 6:
                    avatarDrawable = this.avatarDrawable;
                    i4 = 10;
                    avatarDrawable.setAvatarType(i4);
                    break;
                case 7:
                    this.avatarDrawable.setAvatarType(9);
                    break;
                case '\b':
                    avatarDrawable = this.avatarDrawable;
                    i4 = 23;
                    avatarDrawable.setAvatarType(i4);
                    break;
                case '\t':
                    this.avatarDrawable.setAvatarType(7);
                    break;
            }
            this.lastName = null;
            this.nameTextView.setText(this.currentName, true);
            this.statusTextView.setText(null);
            this.avatarImageView.setImage(null, "50_50", this.avatarDrawable);
        } else {
            CharSequence charSequence = this.currentStatus;
            if (charSequence == null || !TextUtils.isEmpty(charSequence)) {
                layoutParams = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
                dp = AndroidUtilities.dp(10.0f);
            } else {
                layoutParams = (FrameLayout.LayoutParams) this.nameTextView.getLayoutParams();
                dp = AndroidUtilities.dp(19.0f);
            }
            layoutParams.topMargin = dp;
            ViewGroup.LayoutParams layoutParams4 = this.avatarImageView.getLayoutParams();
            ViewGroup.LayoutParams layoutParams5 = this.avatarImageView.getLayoutParams();
            int dp3 = AndroidUtilities.dp(46.0f);
            layoutParams5.height = dp3;
            layoutParams4.width = dp3;
            CheckBox2 checkBox22 = this.checkBox;
            if (checkBox22 != null) {
                ((FrameLayout.LayoutParams) checkBox22.getLayoutParams()).topMargin = AndroidUtilities.dp(29.0f) + this.padding;
                if (LocaleController.isRTL) {
                    ((FrameLayout.LayoutParams) this.checkBox.getLayoutParams()).rightMargin = AndroidUtilities.dp(40.0f) + this.padding;
                } else {
                    ((FrameLayout.LayoutParams) this.checkBox.getLayoutParams()).leftMargin = AndroidUtilities.dp(40.0f) + this.padding;
                }
            }
            Object obj2 = this.currentObject;
            if (obj2 instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) obj2;
                if (this.showSelfAsSaved && UserObject.isUserSelf(user)) {
                    this.nameTextView.setText(LocaleController.getString(R.string.SavedMessages), true);
                    this.statusTextView.setText(null);
                    this.avatarDrawable.setAvatarType(1);
                    this.avatarImageView.setImage((ImageLocation) null, "50_50", this.avatarDrawable, user);
                    ((FrameLayout.LayoutParams) this.nameTextView.getLayoutParams()).topMargin = AndroidUtilities.dp(19.0f);
                    return;
                }
                TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
                TLRPC.FileLocation fileLocation3 = userProfilePhoto != null ? userProfilePhoto.photo_small : null;
                if (i != 0) {
                    boolean z = (MessagesController.UPDATE_MASK_AVATAR & i) != 0 && (((fileLocation2 = this.lastAvatar) != null && fileLocation3 == null) || ((fileLocation2 == null && fileLocation3 != null) || !(fileLocation2 == null || fileLocation3 == null || (fileLocation2.volume_id == fileLocation3.volume_id && fileLocation2.local_id == fileLocation3.local_id))));
                    if (this.currentStatus == null && !z && (MessagesController.UPDATE_MASK_STATUS & i) != 0) {
                        TLRPC.UserStatus userStatus2 = user.status;
                        if ((userStatus2 != null ? userStatus2.expires : 0) != this.lastStatus) {
                            z = true;
                        }
                    }
                    if (z || this.currentName != null || this.lastName == null || (i & MessagesController.UPDATE_MASK_NAME) == 0) {
                        str3 = null;
                    } else {
                        str3 = UserObject.getUserName(user);
                        if (!str3.equals(this.lastName)) {
                            z = true;
                        }
                    }
                    if (!z) {
                        return;
                    }
                } else {
                    str3 = null;
                }
                this.avatarDrawable.setInfo(this.currentAccount, user);
                TLRPC.UserStatus userStatus3 = user.status;
                this.lastStatus = userStatus3 != null ? userStatus3.expires : 0;
                CharSequence charSequence2 = this.currentName;
                if (charSequence2 != null) {
                    this.lastName = null;
                    this.nameTextView.setText(charSequence2, true);
                } else {
                    if (str3 == null) {
                        str3 = UserObject.getUserName(user);
                    }
                    this.lastName = str3;
                    this.nameTextView.setText(str3);
                }
                if (this.currentStatus == null) {
                    if (user.bot) {
                        SimpleTextView simpleTextView3 = this.statusTextView;
                        int i5 = Theme.key_windowBackgroundWhiteGrayText;
                        simpleTextView3.setTag(Integer.valueOf(i5));
                        SimpleTextView simpleTextView4 = this.statusTextView;
                        if (this.forceDarkTheme) {
                            i5 = Theme.key_voipgroup_lastSeenText;
                        }
                        simpleTextView4.setTextColor(Theme.getColor(i5, this.resourcesProvider));
                        simpleTextView2 = this.statusTextView;
                        i3 = R.string.Bot;
                    } else if (user.id == UserConfig.getInstance(this.currentAccount).getClientUserId() || (((userStatus = user.status) != null && userStatus.expires > ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) || MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Long.valueOf(user.id)))) {
                        SimpleTextView simpleTextView5 = this.statusTextView;
                        int i6 = Theme.key_windowBackgroundWhiteBlueText;
                        simpleTextView5.setTag(Integer.valueOf(i6));
                        SimpleTextView simpleTextView6 = this.statusTextView;
                        if (this.forceDarkTheme) {
                            i6 = Theme.key_voipgroup_listeningText;
                        }
                        simpleTextView6.setTextColor(Theme.getColor(i6, this.resourcesProvider));
                        simpleTextView2 = this.statusTextView;
                        i3 = R.string.Online;
                    } else {
                        SimpleTextView simpleTextView7 = this.statusTextView;
                        int i7 = Theme.key_windowBackgroundWhiteGrayText;
                        simpleTextView7.setTag(Integer.valueOf(i7));
                        SimpleTextView simpleTextView8 = this.statusTextView;
                        if (this.forceDarkTheme) {
                            i7 = Theme.key_voipgroup_lastSeenText;
                        }
                        simpleTextView8.setTextColor(Theme.getColor(i7, this.resourcesProvider));
                        simpleTextView2 = this.statusTextView;
                        formatUserStatus = LocaleController.formatUserStatus(this.currentAccount, user);
                        simpleTextView2.setText(formatUserStatus);
                    }
                    formatUserStatus = LocaleController.getString(i3);
                    simpleTextView2.setText(formatUserStatus);
                }
                this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
            } else {
                TLRPC.Chat chat2 = (TLRPC.Chat) obj2;
                TLRPC.ChatPhoto chatPhoto = chat2.photo;
                TLRPC.FileLocation fileLocation4 = chatPhoto != null ? chatPhoto.photo_small : null;
                if (i != 0) {
                    boolean z2 = (MessagesController.UPDATE_MASK_AVATAR & i) != 0 && (((fileLocation = this.lastAvatar) != null && fileLocation4 == null) || ((fileLocation == null && fileLocation4 != null) || !(fileLocation == null || fileLocation4 == null || (fileLocation.volume_id == fileLocation4.volume_id && fileLocation.local_id == fileLocation4.local_id))));
                    if (z2 || this.currentName != null || (str2 = this.lastName) == null || (i & MessagesController.UPDATE_MASK_NAME) == 0) {
                        str = null;
                    } else {
                        str = chat2.title;
                        if (!str.equals(str2)) {
                            z2 = true;
                        }
                    }
                    if (!z2) {
                        return;
                    }
                } else {
                    str = null;
                }
                this.avatarDrawable.setInfo(this.currentAccount, chat2);
                CharSequence charSequence3 = this.currentName;
                if (charSequence3 != null) {
                    this.lastName = null;
                    this.nameTextView.setText(charSequence3, true);
                } else {
                    if (str == null) {
                        str = chat2.title;
                    }
                    this.lastName = str;
                    this.nameTextView.setText(str);
                }
                if (this.currentStatus == null) {
                    SimpleTextView simpleTextView9 = this.statusTextView;
                    int i8 = Theme.key_windowBackgroundWhiteGrayText;
                    simpleTextView9.setTag(Integer.valueOf(i8));
                    SimpleTextView simpleTextView10 = this.statusTextView;
                    if (this.forceDarkTheme) {
                        i8 = Theme.key_voipgroup_lastSeenText;
                    }
                    simpleTextView10.setTextColor(Theme.getColor(i8));
                    if (chat2.participants_count == 0) {
                        if (chat2.has_geo) {
                            simpleTextView = this.statusTextView;
                            i2 = R.string.MegaLocation;
                        } else if (ChatObject.isPublic(chat2)) {
                            if (!ChatObject.isChannel(chat2) || chat2.megagroup) {
                                simpleTextView = this.statusTextView;
                                i2 = R.string.MegaPublic;
                            } else {
                                simpleTextView = this.statusTextView;
                                i2 = R.string.ChannelPublic;
                            }
                        } else if (!ChatObject.isChannel(chat2) || chat2.megagroup) {
                            simpleTextView = this.statusTextView;
                            i2 = R.string.MegaPrivate;
                        } else {
                            simpleTextView = this.statusTextView;
                            i2 = R.string.ChannelPrivate;
                        }
                        string = LocaleController.getString(i2);
                    } else if (!ChatObject.isChannel(chat2) || chat2.megagroup) {
                        simpleTextView = this.statusTextView;
                        string = LocaleController.formatPluralString("Members", chat2.participants_count, new Object[0]);
                    } else {
                        simpleTextView = this.statusTextView;
                        string = LocaleController.formatPluralString("Subscribers", chat2.participants_count, new Object[0]);
                    }
                    simpleTextView.setText(string);
                }
                this.avatarImageView.setForUserOrChat(chat2, this.avatarDrawable);
                chat = chat2;
            }
        }
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp((chat == null || !chat.forum) ? 24.0f : 14.0f));
        CharSequence charSequence4 = this.currentStatus;
        if (charSequence4 != null) {
            this.statusTextView.setText(charSequence4, true);
            SimpleTextView simpleTextView11 = this.statusTextView;
            int i9 = Theme.key_windowBackgroundWhiteGrayText;
            simpleTextView11.setTag(Integer.valueOf(i9));
            SimpleTextView simpleTextView12 = this.statusTextView;
            if (this.forceDarkTheme) {
                i9 = Theme.key_voipgroup_lastSeenText;
            }
            simpleTextView12.setTextColor(Theme.getColor(i9, this.resourcesProvider));
        }
        updatePremiumBlocked(false);
    }
}
