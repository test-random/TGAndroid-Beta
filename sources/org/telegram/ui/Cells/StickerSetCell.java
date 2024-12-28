package org.telegram.ui.Cells;

import android.animation.AnimatorSet;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.ForegroundColorSpanThemable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.RadialProgressView;

public class StickerSetCell extends FrameLayout {
    private TextView addButtonView;
    private CheckBox2 checkBox;
    private ImageView deleteView;
    private boolean emojis;
    private boolean groupSearch;
    private BackupImageView imageView;
    private boolean needDivider;
    private final int option;
    private ImageView optionsButton;
    private PremiumButtonView premiumButtonView;
    private RadialProgressView progressView;
    private Rect rect;
    private TextView removeButtonView;
    private ImageView reorderButton;
    private FrameLayout sideButtons;
    private AnimatorSet stateAnimator;
    private TLRPC.TL_messages_stickerSet stickersSet;
    private TextView textView;
    private TextView valueTextView;

    public StickerSetCell(Context context, int i) {
        this(context, null, i);
    }

    public StickerSetCell(Context context, Theme.ResourcesProvider resourcesProvider, int i) {
        super(context);
        View view;
        FrameLayout.LayoutParams createFrame;
        this.rect = new Rect();
        this.option = i;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        BackupImageView backupImageView2 = this.imageView;
        boolean z = LocaleController.isRTL;
        addView(backupImageView2, LayoutHelper.createFrame(40, 40.0f, (z ? 5 : 3) | 48, z ? 0.0f : 13.0f, 9.0f, z ? 13.0f : 0.0f, 0.0f));
        if (i != 0) {
            ImageView imageView = new ImageView(context);
            this.optionsButton = imageView;
            imageView.setFocusable(false);
            ImageView imageView2 = this.optionsButton;
            ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
            imageView2.setScaleType(scaleType);
            if (i != 3) {
                this.optionsButton.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_stickers_menuSelector)));
            }
            if (i == 1) {
                ImageView imageView3 = this.optionsButton;
                int i2 = Theme.key_stickers_menu;
                int color = Theme.getColor(i2);
                PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
                imageView3.setColorFilter(new PorterDuffColorFilter(color, mode));
                this.optionsButton.setImageResource(R.drawable.msg_actions);
                this.optionsButton.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
                addView(this.optionsButton, LayoutHelper.createFrame(40, 40, (LocaleController.isRTL ? 3 : 5) | 16));
                ImageView imageView4 = new ImageView(context);
                this.reorderButton = imageView4;
                imageView4.setAlpha(0.0f);
                this.reorderButton.setVisibility(8);
                this.reorderButton.setScaleType(scaleType);
                this.reorderButton.setImageResource(R.drawable.list_reorder);
                this.reorderButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2), mode));
                addView(this.reorderButton, LayoutHelper.createFrameRelatively(58.0f, 58.0f, 8388613));
                CheckBox2 checkBox2 = new CheckBox2(context, 21);
                this.checkBox = checkBox2;
                checkBox2.setColor(-1, Theme.key_windowBackgroundWhite, Theme.key_checkboxCheck);
                this.checkBox.setDrawUnchecked(false);
                this.checkBox.setDrawBackgroundAsArc(3);
                view = this.checkBox;
                createFrame = LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388611, 34.0f, 30.0f, 0.0f, 0.0f);
            } else if (i == 3) {
                this.optionsButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_addedIcon), PorterDuff.Mode.MULTIPLY));
                this.optionsButton.setImageResource(R.drawable.floating_check);
                view = this.optionsButton;
                boolean z2 = LocaleController.isRTL;
                createFrame = LayoutHelper.createFrame(40, 40.0f, (z2 ? 3 : 5) | 48, z2 ? 10 : 0, 9.0f, z2 ? 0 : 10, 0.0f);
            }
            addView(view, createFrame);
        }
        this.sideButtons = new FrameLayout(getContext());
        TextView textView = new TextView(context);
        this.addButtonView = textView;
        textView.setTextSize(1, 14.0f);
        this.addButtonView.setTypeface(AndroidUtilities.bold());
        this.addButtonView.setText(LocaleController.getString(R.string.Add));
        this.addButtonView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText, resourcesProvider));
        TextView textView2 = this.addButtonView;
        int i3 = Theme.key_featuredStickers_addButton;
        textView2.setBackground(Theme.AdaptiveRipple.createRect(Theme.getColor(i3, resourcesProvider), Theme.getColor(Theme.key_featuredStickers_addButtonPressed, resourcesProvider), 4.0f));
        this.addButtonView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
        this.addButtonView.setGravity(17);
        this.addButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                StickerSetCell.this.lambda$new$0(view2);
            }
        });
        this.sideButtons.addView(this.addButtonView, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, (LocaleController.isRTL ? 3 : 5) | 16));
        TextView textView3 = new TextView(context);
        this.removeButtonView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.removeButtonView.setTypeface(AndroidUtilities.bold());
        this.removeButtonView.setText(LocaleController.getString(R.string.StickersRemove));
        this.removeButtonView.setTextColor(Theme.getColor(Theme.key_featuredStickers_removeButtonText, resourcesProvider));
        this.removeButtonView.setBackground(Theme.AdaptiveRipple.createRect(0, Theme.getColor(i3, resourcesProvider) & 452984831, 4.0f));
        this.removeButtonView.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        this.removeButtonView.setGravity(17);
        this.removeButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                StickerSetCell.this.lambda$new$1(view2);
            }
        });
        this.sideButtons.addView(this.removeButtonView, LayoutHelper.createFrameRelatively(-2.0f, 32.0f, (LocaleController.isRTL ? 3 : 5) | 16, 0.0f, -2.0f, 0.0f, 0.0f));
        PremiumButtonView premiumButtonView = new PremiumButtonView(context, AndroidUtilities.dp(4.0f), false, resourcesProvider);
        this.premiumButtonView = premiumButtonView;
        premiumButtonView.setIcon(R.raw.unlock_icon);
        this.premiumButtonView.setButton(LocaleController.getString(R.string.Unlock), new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                StickerSetCell.this.lambda$new$2(view2);
            }
        });
        try {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.premiumButtonView.getIconView().getLayoutParams();
            marginLayoutParams.leftMargin = AndroidUtilities.dp(1.0f);
            marginLayoutParams.topMargin = AndroidUtilities.dp(1.0f);
            int dp = AndroidUtilities.dp(20.0f);
            marginLayoutParams.height = dp;
            marginLayoutParams.width = dp;
            ((ViewGroup.MarginLayoutParams) this.premiumButtonView.getTextView().getLayoutParams()).leftMargin = AndroidUtilities.dp(3.0f);
            this.premiumButtonView.getChildAt(0).setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        } catch (Exception unused) {
        }
        this.sideButtons.addView(this.premiumButtonView, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, (LocaleController.isRTL ? 3 : 5) | 16));
        this.sideButtons.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
        addView(this.sideButtons, LayoutHelper.createFrame(-2, -1.0f, LocaleController.isRTL ? 3 : 5, 0.0f, 0.0f, 0.0f, 0.0f));
        this.sideButtons.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                StickerSetCell.this.lambda$new$3(view2);
            }
        });
        TextView textView4 = new TextView(context) {
            @Override
            public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
                super.setText(Emoji.replaceEmoji(charSequence, getPaint().getFontMetricsInt(), false), bufferType);
            }
        };
        this.textView = textView4;
        NotificationCenter.listenEmojiLoading(textView4);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(1, 16.0f);
        this.textView.setTypeface(AndroidUtilities.bold());
        this.textView.setLines(1);
        this.textView.setMaxLines(1);
        this.textView.setSingleLine(true);
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(this.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388611, 71.0f, 9.0f, 70.0f, 0.0f));
        TextView textView5 = new TextView(context);
        this.valueTextView = textView5;
        textView5.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.valueTextView.setTextSize(1, 13.0f);
        this.valueTextView.setLines(1);
        this.valueTextView.setMaxLines(1);
        this.valueTextView.setSingleLine(true);
        this.valueTextView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        addView(this.valueTextView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388611, 71.0f, 32.0f, 70.0f, 0.0f));
        if (i == 3) {
            ImageView imageView5 = new ImageView(context);
            this.deleteView = imageView5;
            imageView5.setImageResource(R.drawable.msg_close);
            this.deleteView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            this.deleteView.setColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText), PorterDuff.Mode.SRC_IN);
            this.deleteView.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)));
            this.deleteView.setVisibility(8);
            ImageView imageView6 = this.deleteView;
            boolean z3 = LocaleController.isRTL;
            addView(imageView6, LayoutHelper.createFrame(-2, -2.0f, (z3 ? 3 : 5) | 16, z3 ? 4.0f : 0.0f, 0.0f, z3 ? 0.0f : 4.0f, 0.0f));
        }
        updateButtonState(0, false);
    }

    public void lambda$new$0(View view) {
        onAddButtonClick();
    }

    public void lambda$new$1(View view) {
        onRemoveButtonClick();
    }

    public void lambda$new$2(View view) {
        onPremiumButtonClick();
    }

    public void lambda$new$3(View view) {
        View view2;
        if (this.premiumButtonView.getVisibility() == 0 && this.premiumButtonView.isEnabled()) {
            view2 = this.premiumButtonView;
        } else if (this.addButtonView.getVisibility() == 0 && this.addButtonView.isEnabled()) {
            view2 = this.addButtonView;
        } else if (this.removeButtonView.getVisibility() != 0 || !this.removeButtonView.isEnabled()) {
            return;
        } else {
            view2 = this.removeButtonView;
        }
        view2.performClick();
    }

    public void lambda$setReorderable$6(boolean z) {
        if (z) {
            return;
        }
        this.reorderButton.setVisibility(8);
    }

    public void lambda$setReorderable$7(boolean z) {
        if (z) {
            this.sideButtons.setVisibility(8);
        }
    }

    public void lambda$setReorderable$8(boolean z) {
        if (z) {
            this.optionsButton.setVisibility(8);
        }
    }

    public void lambda$setStickersSet$4(TLRPC.Document document) {
        if (this.stickersSet.documents.isEmpty()) {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.stickersSet;
            if (tL_messages_stickerSet.set.thumb_document_id == document.id) {
                tL_messages_stickerSet.documents.add(document);
                setStickersSet(this.stickersSet, this.needDivider, this.groupSearch);
            }
        }
    }

    public void lambda$setStickersSet$5(final TLRPC.Document document) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StickerSetCell.this.lambda$setStickersSet$4(document);
            }
        });
    }

    public void lambda$updateButtonState$10(View view) {
        onPremiumButtonClick();
    }

    public void lambda$updateButtonState$9(View view) {
        onPremiumButtonClick();
    }

    public TLRPC.TL_messages_stickerSet getStickersSet() {
        return this.stickersSet;
    }

    public boolean isChecked() {
        int i = this.option;
        return i == 1 ? this.checkBox.isChecked() : i == 3 ? this.optionsButton.getVisibility() == 0 : this.emojis && this.sideButtons.getVisibility() == 0;
    }

    protected void onAddButtonClick() {
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(71.0f), getHeight() - 1, (getWidth() - getPaddingRight()) - (LocaleController.isRTL ? AndroidUtilities.dp(71.0f) : 0), getHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 == null || !checkBox2.isChecked()) {
            return;
        }
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(true);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f) + (this.needDivider ? 1 : 0), 1073741824));
    }

    protected void onPremiumButtonClick() {
    }

    protected void onRemoveButtonClick() {
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        FrameLayout frameLayout;
        ImageView imageView;
        int i = Build.VERSION.SDK_INT;
        if (i >= 21 && getBackground() != null && (imageView = this.optionsButton) != null) {
            imageView.getHitRect(this.rect);
            if (this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return true;
            }
        }
        if (i >= 21 && getBackground() != null && this.emojis && (frameLayout = this.sideButtons) != null) {
            frameLayout.getHitRect(this.rect);
            if (this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setChecked(boolean z) {
        setChecked(z, true);
    }

    public void setChecked(final boolean r8, boolean r9) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.StickerSetCell.setChecked(boolean, boolean):void");
    }

    public void setDeleteAction(View.OnClickListener onClickListener) {
        ImageView imageView = this.deleteView;
        if (imageView != null) {
            imageView.setVisibility(onClickListener == null ? 8 : 0);
            this.deleteView.setOnClickListener(onClickListener);
        }
    }

    public void setNeedDivider(boolean z) {
        this.needDivider = z;
    }

    public void setOnOptionsClick(View.OnClickListener onClickListener) {
        ImageView imageView = this.optionsButton;
        if (imageView == null) {
            return;
        }
        imageView.setOnClickListener(onClickListener);
    }

    public void setOnReorderButtonTouchListener(View.OnTouchListener onTouchListener) {
        this.reorderButton.setOnTouchListener(onTouchListener);
    }

    public void setReorderable(boolean z) {
        setReorderable(z, true);
    }

    public void setReorderable(final boolean z, boolean z2) {
        View view;
        float f;
        ViewPropertyAnimator interpolator;
        Runnable runnable;
        if (this.option == 1) {
            float[] fArr = {z ? 1.0f : 0.0f, z ? 0.0f : 1.0f};
            float[] fArr2 = {z ? 1.0f : 0.66f, z ? 0.66f : 1.0f};
            if (z2) {
                this.reorderButton.setVisibility(0);
                ViewPropertyAnimator duration = this.reorderButton.animate().alpha(fArr[0]).scaleX(fArr2[0]).scaleY(fArr2[0]).setDuration(200L);
                Interpolator interpolator2 = Easings.easeOutSine;
                duration.setInterpolator(interpolator2).withEndAction(new Runnable() {
                    @Override
                    public final void run() {
                        StickerSetCell.this.lambda$setReorderable$6(z);
                    }
                }).start();
                if (this.emojis) {
                    this.sideButtons.setVisibility(0);
                    interpolator = this.sideButtons.animate().alpha(fArr[1]).scaleX(fArr2[1]).scaleY(fArr2[1]).setDuration(200L).setInterpolator(interpolator2);
                    runnable = new Runnable() {
                        @Override
                        public final void run() {
                            StickerSetCell.this.lambda$setReorderable$7(z);
                        }
                    };
                } else {
                    this.optionsButton.setVisibility(0);
                    interpolator = this.optionsButton.animate().alpha(fArr[1]).scaleX(fArr2[1]).scaleY(fArr2[1]).setDuration(200L).setInterpolator(interpolator2);
                    runnable = new Runnable() {
                        @Override
                        public final void run() {
                            StickerSetCell.this.lambda$setReorderable$8(z);
                        }
                    };
                }
                interpolator.withEndAction(runnable).start();
                return;
            }
            this.reorderButton.setVisibility(z ? 0 : 8);
            this.reorderButton.setAlpha(fArr[0]);
            this.reorderButton.setScaleX(fArr2[0]);
            this.reorderButton.setScaleY(fArr2[0]);
            if (this.emojis) {
                this.sideButtons.setVisibility(z ? 8 : 0);
                this.sideButtons.setAlpha(fArr[1]);
                this.sideButtons.setScaleX(fArr2[1]);
                view = this.sideButtons;
                f = fArr2[1];
            } else {
                this.optionsButton.setVisibility(z ? 8 : 0);
                this.optionsButton.setAlpha(fArr[1]);
                this.optionsButton.setScaleX(fArr2[1]);
                view = this.optionsButton;
                f = fArr2[1];
            }
            view.setScaleY(f);
        }
    }

    public void setSearchQuery(TLRPC.TL_messages_stickerSet tL_messages_stickerSet, String str, Theme.ResourcesProvider resourcesProvider) {
        TLRPC.StickerSet stickerSet = tL_messages_stickerSet.set;
        String str2 = stickerSet.title;
        Locale locale = Locale.ROOT;
        int indexOf = str2.toLowerCase(locale).indexOf(str);
        if (indexOf != -1) {
            SpannableString spannableString = new SpannableString(stickerSet.title);
            spannableString.setSpan(new ForegroundColorSpanThemable(Theme.key_windowBackgroundWhiteBlueText4, resourcesProvider), indexOf, str.length() + indexOf, 0);
            this.textView.setText(spannableString);
        }
        int indexOf2 = stickerSet.short_name.toLowerCase(locale).indexOf(str);
        if (indexOf2 != -1) {
            String str3 = stickerSet.emojis ? "t.me/addemoji/" : "t.me/addstickers/";
            int length = indexOf2 + str3.length();
            SpannableString spannableString2 = new SpannableString(str3 + stickerSet.short_name);
            spannableString2.setSpan(new ForegroundColorSpanThemable(Theme.key_windowBackgroundWhiteBlueText4, resourcesProvider), length, str.length() + length, 0);
            this.valueTextView.setText(spannableString2);
        }
    }

    public void setStickersSet(TLRPC.TL_messages_stickerSet tL_messages_stickerSet, boolean z) {
        setStickersSet(tL_messages_stickerSet, z, false);
    }

    public void setStickersSet(TLRPC.TL_messages_stickerSet tL_messages_stickerSet, boolean z, boolean z2) {
        TLRPC.Document document;
        BackupImageView backupImageView;
        String str;
        this.needDivider = z;
        this.stickersSet = tL_messages_stickerSet;
        this.groupSearch = z2;
        this.imageView.setVisibility(0);
        RadialProgressView radialProgressView = this.progressView;
        if (radialProgressView != null) {
            radialProgressView.setVisibility(4);
        }
        this.textView.setTranslationY(0.0f);
        this.textView.setText(this.stickersSet.set.title);
        if (this.stickersSet.set.archived) {
            this.textView.setAlpha(0.5f);
            this.valueTextView.setAlpha(0.5f);
            this.imageView.setAlpha(0.5f);
        } else {
            this.textView.setAlpha(1.0f);
            this.valueTextView.setAlpha(1.0f);
            this.imageView.setAlpha(1.0f);
        }
        boolean z3 = tL_messages_stickerSet.set.emojis;
        this.emojis = z3;
        this.sideButtons.setVisibility(z3 ? 0 : 8);
        this.optionsButton.setVisibility(this.emojis ? 8 : 0);
        this.imageView.setColorFilter(null);
        ArrayList<TLRPC.Document> arrayList = tL_messages_stickerSet.documents;
        if (arrayList == null || arrayList.isEmpty()) {
            this.valueTextView.setText(LocaleController.formatPluralString(tL_messages_stickerSet.set.emojis ? "EmojiCount" : "Stickers", 0, new Object[0]));
            this.imageView.setImageDrawable(null);
            if (tL_messages_stickerSet.set.thumb_document_id != 0) {
                AnimatedEmojiDrawable.getDocumentFetcher(UserConfig.selectedAccount).fetchDocument(tL_messages_stickerSet.set.thumb_document_id, new AnimatedEmojiDrawable.ReceivedDocument() {
                    @Override
                    public final void run(TLRPC.Document document2) {
                        StickerSetCell.this.lambda$setStickersSet$5(document2);
                    }
                });
            }
        } else {
            this.valueTextView.setText(LocaleController.formatPluralString(this.emojis ? "EmojiCount" : "Stickers", arrayList.size(), new Object[0]));
            int i = 0;
            while (true) {
                if (i >= arrayList.size()) {
                    document = null;
                    break;
                }
                document = arrayList.get(i);
                if (document != null && document.id == tL_messages_stickerSet.set.thumb_document_id) {
                    break;
                } else {
                    i++;
                }
            }
            if (document == null) {
                document = arrayList.get(0);
            }
            TLRPC.Document document2 = document;
            LiteMode.isEnabled(1);
            TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tL_messages_stickerSet.set.thumbs, 90);
            if (closestPhotoSizeWithSize == null) {
                closestPhotoSizeWithSize = document2;
            }
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tL_messages_stickerSet.set.thumbs, Theme.key_windowBackgroundGray, 1.0f);
            boolean z4 = closestPhotoSizeWithSize instanceof TLRPC.Document;
            ImageLocation forDocument = z4 ? ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document2.thumbs, 90), document2) : ImageLocation.getForSticker((TLRPC.PhotoSize) closestPhotoSizeWithSize, document2, tL_messages_stickerSet.set.thumb_version);
            boolean isEnabled = LiteMode.isEnabled(this.emojis ? 16388 : 1);
            StringBuilder sb = new StringBuilder();
            sb.append("50_50");
            sb.append(!isEnabled ? "_firstframe" : "");
            String sb2 = sb.toString();
            if (z4 && (MessageObject.isAnimatedStickerDocument(document2, true) || MessageObject.isVideoSticker(document2))) {
                BackupImageView backupImageView2 = this.imageView;
                ImageLocation forDocument2 = ImageLocation.getForDocument(document2);
                if (svgThumb != null) {
                    backupImageView2.setImage(forDocument2, sb2, svgThumb, 0, tL_messages_stickerSet);
                } else {
                    backupImageView2.setImage(forDocument2, sb2, forDocument, (String) null, 0, tL_messages_stickerSet);
                }
                if (MessageObject.isTextColorEmoji(document2)) {
                    this.imageView.setColorFilter(Theme.getAnimatedEmojiColorFilter(null));
                }
            } else {
                if (forDocument == null || forDocument.imageType != 1) {
                    backupImageView = this.imageView;
                    str = "webp";
                } else {
                    backupImageView = this.imageView;
                    str = "tgs";
                }
                backupImageView.setImage(forDocument, sb2, str, svgThumb, tL_messages_stickerSet);
            }
        }
        if (this.groupSearch) {
            TextView textView = this.valueTextView;
            StringBuilder sb3 = new StringBuilder();
            sb3.append(tL_messages_stickerSet.set.emojis ? "t.me/addemoji/" : "t.me/addstickers/");
            sb3.append(tL_messages_stickerSet.set.short_name);
            textView.setText(sb3.toString());
        }
    }

    public void updateButtonState(final int r20, boolean r21) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.StickerSetCell.updateButtonState(int, boolean):void");
    }

    public void updateRightMargin() {
        this.sideButtons.measure(View.MeasureSpec.makeMeasureSpec(999999, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f), 1073741824));
        int dp = AndroidUtilities.dp(26.0f) + this.sideButtons.getMeasuredWidth();
        if (LocaleController.isRTL) {
            ((ViewGroup.MarginLayoutParams) this.textView.getLayoutParams()).leftMargin = dp;
            ((ViewGroup.MarginLayoutParams) this.valueTextView.getLayoutParams()).leftMargin = dp;
        } else {
            ((ViewGroup.MarginLayoutParams) this.textView.getLayoutParams()).rightMargin = dp;
            ((ViewGroup.MarginLayoutParams) this.valueTextView.getLayoutParams()).rightMargin = dp;
        }
    }
}
