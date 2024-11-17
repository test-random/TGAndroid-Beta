package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.ColorSpanUnderline;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RecyclerListView;

public class FeaturedStickerSetInfoCell extends FrameLayout {
    private ProgressButton addButton;
    private AnimatorSet animatorSet;
    private boolean canAddRemove;
    private int currentAccount;
    private TextView delButton;
    private boolean hasOnClick;
    private TextView infoTextView;
    private boolean isInstalled;
    private boolean isUnread;
    private TextView nameTextView;
    private boolean needDivider;
    private Paint paint;
    private final Theme.ResourcesProvider resourcesProvider;
    private TLRPC.StickerSetCovered set;
    private int stickerSetNameSearchIndex;
    private int stickerSetNameSearchLength;
    float unreadProgress;
    private CharSequence url;
    private int urlSearchLength;

    public FeaturedStickerSetInfoCell(Context context, int i, boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        this.paint = new Paint(1);
        this.canAddRemove = z2;
        this.resourcesProvider = resourcesProvider;
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(getThemedColor(Theme.key_chat_emojiPanelTrendingTitle));
        this.nameTextView.setTextSize(1, 17.0f);
        this.nameTextView.setTypeface(AndroidUtilities.bold());
        TextView textView2 = this.nameTextView;
        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
        textView2.setEllipsize(truncateAt);
        this.nameTextView.setSingleLine(true);
        float f = i;
        addView(this.nameTextView, z ? LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388659, f, 8.0f, 40.0f, 0.0f) : LayoutHelper.createFrame(-2, -2.0f, 51, f, 8.0f, 40.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.infoTextView = textView3;
        textView3.setTextColor(getThemedColor(Theme.key_chat_emojiPanelTrendingDescription));
        this.infoTextView.setTextSize(1, 13.0f);
        this.infoTextView.setEllipsize(truncateAt);
        this.infoTextView.setSingleLine(true);
        float f2 = i;
        addView(this.infoTextView, z ? LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388659, f2, 30.0f, 100.0f, 0.0f) : LayoutHelper.createFrame(-2, -2.0f, 51, f2, 30.0f, 100.0f, 0.0f));
        if (z2) {
            ProgressButton progressButton = new ProgressButton(context);
            this.addButton = progressButton;
            progressButton.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
            this.addButton.setText(LocaleController.getString(R.string.Add));
            addView(this.addButton, z ? LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 16.0f, 14.0f, 0.0f) : LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 16.0f, 14.0f, 0.0f));
            TextView textView4 = new TextView(context);
            this.delButton = textView4;
            textView4.setGravity(17);
            this.delButton.setTextColor(getThemedColor(Theme.key_featuredStickers_removeButtonText));
            this.delButton.setTextSize(1, 14.0f);
            this.delButton.setTypeface(AndroidUtilities.bold());
            this.delButton.setText(LocaleController.getString(R.string.StickersRemove));
            addView(this.delButton, z ? LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 16.0f, 14.0f, 0.0f) : LayoutHelper.createFrame(-2, 28.0f, 53, 0.0f, 16.0f, 14.0f, 0.0f));
        }
        setWillNotDraw(false);
        updateColors();
    }

    public static void createThemeDescriptions(List list, RecyclerListView recyclerListView, ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate) {
        list.add(new ThemeDescription(recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetInfoCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_emojiPanelTrendingTitle));
        int i = Theme.key_chat_emojiPanelTrendingDescription;
        list.add(new ThemeDescription(recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetInfoCell.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        list.add(new ThemeDescription(recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetInfoCell.class}, new String[]{"addButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_buttonText));
        list.add(new ThemeDescription(recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{FeaturedStickerSetInfoCell.class}, new String[]{"delButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_featuredStickers_removeButtonText));
        list.add(new ThemeDescription(recyclerListView, 0, new Class[]{FeaturedStickerSetInfoCell.class}, null, null, null, Theme.key_featuredStickers_unread));
        list.add(new ThemeDescription(recyclerListView, 0, new Class[]{FeaturedStickerSetInfoCell.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        list.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_featuredStickers_buttonProgress));
        list.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_featuredStickers_addButton));
        list.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_featuredStickers_addButtonPressed));
        list.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlueText4));
        list.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, i));
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    private void updateStickerSetNameSearchSpan() {
        if (this.stickerSetNameSearchLength != 0) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.set.set.title);
            try {
                ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getThemedColor(Theme.key_windowBackgroundWhiteBlueText4));
                int i = this.stickerSetNameSearchIndex;
                spannableStringBuilder.setSpan(foregroundColorSpan, i, this.stickerSetNameSearchLength + i, 33);
            } catch (Exception unused) {
            }
            this.nameTextView.setText(spannableStringBuilder);
        }
    }

    private void updateUrlSearchSpan() {
        if (this.url != null) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.url);
            try {
                spannableStringBuilder.setSpan(new ColorSpanUnderline(getThemedColor(Theme.key_windowBackgroundWhiteBlueText4)), 0, this.urlSearchLength, 33);
                spannableStringBuilder.setSpan(new ColorSpanUnderline(getThemedColor(Theme.key_chat_emojiPanelTrendingDescription)), this.urlSearchLength, this.url.length(), 33);
            } catch (Exception unused) {
            }
            this.infoTextView.setText(spannableStringBuilder);
        }
    }

    public TLRPC.StickerSetCovered getStickerSet() {
        return this.set;
    }

    public boolean isInstalled() {
        return this.isInstalled;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean z = this.isUnread;
        if (z || this.unreadProgress != 0.0f) {
            if (z) {
                float f = this.unreadProgress;
                if (f != 1.0f) {
                    float f2 = f + 0.16f;
                    this.unreadProgress = f2;
                    if (f2 > 1.0f) {
                        this.unreadProgress = 1.0f;
                        this.paint.setColor(getThemedColor(Theme.key_featuredStickers_unread));
                        canvas.drawCircle(this.nameTextView.getRight() + AndroidUtilities.dp(12.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(4.0f) * this.unreadProgress, this.paint);
                    }
                    invalidate();
                    this.paint.setColor(getThemedColor(Theme.key_featuredStickers_unread));
                    canvas.drawCircle(this.nameTextView.getRight() + AndroidUtilities.dp(12.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(4.0f) * this.unreadProgress, this.paint);
                }
            }
            if (!z) {
                float f3 = this.unreadProgress;
                if (f3 != 0.0f) {
                    float f4 = f3 - 0.16f;
                    this.unreadProgress = f4;
                    if (f4 < 0.0f) {
                        this.unreadProgress = 0.0f;
                    }
                    invalidate();
                }
            }
            this.paint.setColor(getThemedColor(Theme.key_featuredStickers_unread));
            canvas.drawCircle(this.nameTextView.getRight() + AndroidUtilities.dp(12.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(4.0f) * this.unreadProgress, this.paint);
        }
        if (this.needDivider) {
            canvas.drawLine(0.0f, 0.0f, getWidth(), 0.0f, Theme.getThemePaint("paintDivider", this.resourcesProvider));
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), 1073741824));
        if (this.canAddRemove) {
            int measuredWidth = this.addButton.getMeasuredWidth();
            int measuredWidth2 = this.delButton.getMeasuredWidth();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.delButton.getLayoutParams();
            if (measuredWidth2 < measuredWidth) {
                layoutParams.rightMargin = AndroidUtilities.dp(14.0f) + ((measuredWidth - measuredWidth2) / 2);
            } else {
                layoutParams.rightMargin = AndroidUtilities.dp(14.0f);
            }
            measureChildWithMargins(this.nameTextView, i, measuredWidth, i2, 0);
        }
    }

    public void setAddDrawProgress(boolean z, boolean z2) {
        if (this.canAddRemove) {
            this.addButton.setDrawProgress(z, z2);
        }
    }

    public void setAddOnClickListener(View.OnClickListener onClickListener) {
        if (this.canAddRemove) {
            this.hasOnClick = true;
            this.addButton.setOnClickListener(onClickListener);
            this.delButton.setOnClickListener(onClickListener);
        }
    }

    public void setNeedDivider(boolean z) {
        this.needDivider = z;
    }

    public void setStickerSet(TLRPC.StickerSetCovered stickerSetCovered, boolean z) {
        setStickerSet(stickerSetCovered, z, false, 0, 0, false);
    }

    public void setStickerSet(TLRPC.StickerSetCovered stickerSetCovered, boolean z, boolean z2, int i, int i2) {
        setStickerSet(stickerSetCovered, z, z2, i, i2, false);
    }

    public void setStickerSet(TLRPC.StickerSetCovered stickerSetCovered, boolean z, boolean z2, int i, int i2, boolean z3) {
        TextView textView;
        String formatPluralString;
        View view;
        AnimatorSet animatorSet = this.animatorSet;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animatorSet = null;
        }
        if (this.set != stickerSetCovered) {
            this.unreadProgress = z ? 1.0f : 0.0f;
            invalidate();
        }
        this.set = stickerSetCovered;
        this.stickerSetNameSearchIndex = i;
        this.stickerSetNameSearchLength = i2;
        if (i2 != 0) {
            updateStickerSetNameSearchSpan();
        } else {
            this.nameTextView.setText(stickerSetCovered.set.title);
        }
        TLRPC.StickerSet stickerSet = stickerSetCovered.set;
        if (stickerSet.emojis) {
            textView = this.infoTextView;
            formatPluralString = LocaleController.formatPluralString("EmojiCount", stickerSet.count, new Object[0]);
        } else {
            textView = this.infoTextView;
            formatPluralString = LocaleController.formatPluralString("Stickers", stickerSet.count, new Object[0]);
        }
        textView.setText(formatPluralString);
        this.isUnread = z;
        if (this.canAddRemove) {
            if (!this.hasOnClick) {
                this.addButton.setVisibility(8);
                return;
            }
            this.addButton.setVisibility(0);
            boolean z4 = z3 || MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(stickerSetCovered.set.id);
            this.isInstalled = z4;
            if (z2) {
                (z4 ? this.delButton : this.addButton).setVisibility(0);
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.animatorSet = animatorSet2;
                animatorSet2.setDuration(250L);
                AnimatorSet animatorSet3 = this.animatorSet;
                TextView textView2 = this.delButton;
                Property property = View.ALPHA;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(textView2, (Property<TextView, Float>) property, this.isInstalled ? 1.0f : 0.0f);
                TextView textView3 = this.delButton;
                Property property2 = View.SCALE_X;
                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(textView3, (Property<TextView, Float>) property2, this.isInstalled ? 1.0f : 0.0f);
                TextView textView4 = this.delButton;
                Property property3 = View.SCALE_Y;
                animatorSet3.playTogether(ofFloat, ofFloat2, ObjectAnimator.ofFloat(textView4, (Property<TextView, Float>) property3, this.isInstalled ? 1.0f : 0.0f), ObjectAnimator.ofFloat(this.addButton, (Property<ProgressButton, Float>) property, this.isInstalled ? 0.0f : 1.0f), ObjectAnimator.ofFloat(this.addButton, (Property<ProgressButton, Float>) property2, this.isInstalled ? 0.0f : 1.0f), ObjectAnimator.ofFloat(this.addButton, (Property<ProgressButton, Float>) property3, this.isInstalled ? 0.0f : 1.0f));
                this.animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        (FeaturedStickerSetInfoCell.this.isInstalled ? FeaturedStickerSetInfoCell.this.addButton : FeaturedStickerSetInfoCell.this.delButton).setVisibility(4);
                    }
                });
                this.animatorSet.setInterpolator(new OvershootInterpolator(1.02f));
                this.animatorSet.start();
                return;
            }
            if (z4) {
                this.delButton.setVisibility(0);
                this.delButton.setAlpha(1.0f);
                this.delButton.setScaleX(1.0f);
                this.delButton.setScaleY(1.0f);
                this.addButton.setVisibility(4);
                this.addButton.setAlpha(0.0f);
                this.addButton.setScaleX(0.0f);
                view = this.addButton;
            } else {
                this.addButton.setVisibility(0);
                this.addButton.setAlpha(1.0f);
                this.addButton.setScaleX(1.0f);
                this.addButton.setScaleY(1.0f);
                this.delButton.setVisibility(4);
                this.delButton.setAlpha(0.0f);
                this.delButton.setScaleX(0.0f);
                view = this.delButton;
            }
            view.setScaleY(0.0f);
        }
    }

    public void setUrl(CharSequence charSequence, int i) {
        this.url = charSequence;
        this.urlSearchLength = i;
        updateUrlSearchSpan();
    }

    public void updateColors() {
        if (this.canAddRemove) {
            this.addButton.setProgressColor(getThemedColor(Theme.key_featuredStickers_buttonProgress));
            this.addButton.setBackgroundRoundRect(getThemedColor(Theme.key_featuredStickers_addButton), getThemedColor(Theme.key_featuredStickers_addButtonPressed));
        }
        updateStickerSetNameSearchSpan();
        updateUrlSearchSpan();
    }
}
