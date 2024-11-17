package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.ColorSpanUnderline;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;

public class StickerSetNameCell extends FrameLayout {
    private ImageView buttonView;
    private TextView editView;
    private boolean empty;
    private boolean isEmoji;
    public int position;
    private final Theme.ResourcesProvider resourcesProvider;
    private CharSequence stickerSetName;
    private int stickerSetNameSearchIndex;
    private int stickerSetNameSearchLength;
    private TextView textView;
    private CharSequence url;
    private int urlSearchLength;
    private TextView urlTextView;

    public StickerSetNameCell(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
        this(context, z, false, resourcesProvider);
    }

    public StickerSetNameCell(Context context, boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        ViewGroup.LayoutParams createFrame;
        ViewGroup.LayoutParams createFrame2;
        this.resourcesProvider = resourcesProvider;
        this.isEmoji = z;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setGravity(17);
        TextView textView = new TextView(context);
        this.textView = textView;
        int i = Theme.key_chat_emojiPanelStickerSetName;
        textView.setTextColor(getThemedColor(i));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.bold());
        TextView textView2 = this.textView;
        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
        textView2.setEllipsize(truncateAt);
        this.textView.setSingleLine(true);
        if (z) {
            this.textView.setGravity(17);
            this.textView.setTextAlignment(4);
        }
        if (z2) {
            createFrame = LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388659, z ? 5.0f : 15.0f, 5.0f, z ? 15.0f : 25.0f, 0.0f);
        } else {
            createFrame = LayoutHelper.createFrame(-2, -2.0f, 51, z ? 5.0f : 15.0f, 5.0f, z ? 15.0f : 25.0f, 0.0f);
        }
        addView(linearLayout, createFrame);
        linearLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2, 1.0f, 16));
        TextView textView3 = new TextView(context);
        this.editView = textView3;
        textView3.setTextColor(getThemedColor(i));
        this.editView.setTextSize(1, 11.0f);
        this.editView.setTypeface(AndroidUtilities.bold());
        this.editView.setEllipsize(truncateAt);
        this.editView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.33f), 0);
        this.editView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(9.0f), Theme.multAlpha(getThemedColor(i), 0.1f), Theme.multAlpha(getThemedColor(i), 0.24f)));
        this.editView.setGravity(17);
        this.editView.setSingleLine(true);
        ScaleStateListAnimator.apply(this.editView);
        linearLayout.addView(this.editView, LayoutHelper.createLinear(-2, -2, 0.0f, 16, 5, 1, 0, 0));
        this.editView.setVisibility(8);
        TextView textView4 = new TextView(context);
        this.urlTextView = textView4;
        textView4.setTextColor(getThemedColor(i));
        this.urlTextView.setTextSize(1, 12.0f);
        this.urlTextView.setEllipsize(truncateAt);
        this.urlTextView.setSingleLine(true);
        this.urlTextView.setVisibility(4);
        addView(this.urlTextView, z2 ? LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388661, 12.0f, 6.0f, 17.0f, 0.0f) : LayoutHelper.createFrame(-2, -2.0f, 53, 12.0f, 6.0f, 17.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.buttonView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.buttonView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelStickerSetNameIcon), PorterDuff.Mode.MULTIPLY));
        this.buttonView.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector), 3));
        boolean z3 = this.isEmoji;
        if (z2) {
            createFrame2 = LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388661, 0.0f, 0.0f, z3 ? 0.0f : 10.0f, 0.0f);
        } else {
            createFrame2 = LayoutHelper.createFrame(24, 24.0f, 53, 0.0f, 0.0f, z3 ? 0.0f : 10.0f, 0.0f);
        }
        this.buttonView.setTranslationY(AndroidUtilities.dp(4.0f));
        addView(this.buttonView, createFrame2);
    }

    public static void createThemeDescriptions(List list, RecyclerListView recyclerListView, ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate) {
        int i = Theme.key_chat_emojiPanelStickerSetName;
        list.add(new ThemeDescription(recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{StickerSetNameCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        list.add(new ThemeDescription(recyclerListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{StickerSetNameCell.class}, new String[]{"urlTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i));
        list.add(new ThemeDescription(recyclerListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{StickerSetNameCell.class}, new String[]{"buttonView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_emojiPanelStickerSetNameIcon));
        list.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_chat_emojiPanelStickerSetNameHighlight));
        list.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, i));
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    private void updateTextSearchSpan() {
        if (this.stickerSetName == null || this.stickerSetNameSearchLength <= 0) {
            return;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.stickerSetName);
        try {
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(getThemedColor(Theme.key_chat_emojiPanelStickerSetNameHighlight));
            int i = this.stickerSetNameSearchIndex;
            spannableStringBuilder.setSpan(foregroundColorSpan, i, this.stickerSetNameSearchLength + i, 33);
        } catch (Exception unused) {
        }
        TextView textView = this.textView;
        textView.setText(Emoji.replaceEmoji((CharSequence) spannableStringBuilder, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
    }

    private void updateUrlSearchSpan() {
        if (this.url != null) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.url);
            try {
                spannableStringBuilder.setSpan(new ColorSpanUnderline(getThemedColor(Theme.key_chat_emojiPanelStickerSetNameHighlight)), 0, this.urlSearchLength, 33);
                spannableStringBuilder.setSpan(new ColorSpanUnderline(getThemedColor(Theme.key_chat_emojiPanelStickerSetName)), this.urlSearchLength, this.url.length(), 33);
            } catch (Exception unused) {
            }
            this.urlTextView.setText(spannableStringBuilder);
        }
    }

    public TextView getTextView() {
        return this.textView;
    }

    @Override
    public void invalidate() {
        this.textView.invalidate();
        super.invalidate();
    }

    @Override
    protected void measureChildWithMargins(View view, int i, int i2, int i3, int i4) {
        if (view == this.urlTextView) {
            i2 += this.textView.getMeasuredWidth() + AndroidUtilities.dp(16.0f);
        }
        super.measureChildWithMargins(view, i, i2, i3, i4);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(this.empty ? 1 : AndroidUtilities.dp(27.0f), 1073741824));
    }

    public void setEdit(View.OnClickListener onClickListener) {
        this.editView.setVisibility(0);
        this.editView.setText(LocaleController.getString(R.string.EditPack));
        this.editView.setOnClickListener(onClickListener);
    }

    public void setHeaderOnClick(View.OnClickListener onClickListener) {
        this.textView.setOnClickListener(onClickListener);
    }

    public void setOnIconClickListener(View.OnClickListener onClickListener) {
        this.buttonView.setOnClickListener(onClickListener);
    }

    public void setText(CharSequence charSequence, int i) {
        setText(charSequence, i, null, 0, 0);
    }

    public void setText(CharSequence charSequence, int i, int i2, int i3) {
        setText(charSequence, i, null, i2, i3);
    }

    public void setText(CharSequence charSequence, int i, CharSequence charSequence2) {
        setText(charSequence, i, charSequence2, 0, 0);
    }

    public void setText(CharSequence charSequence, int i, CharSequence charSequence2, int i2, int i3) {
        ImageView imageView;
        this.stickerSetName = charSequence;
        this.stickerSetNameSearchIndex = i2;
        this.stickerSetNameSearchLength = i3;
        if (charSequence == null) {
            this.empty = true;
            this.textView.setText("");
            imageView = this.buttonView;
        } else {
            this.empty = false;
            if (i3 != 0) {
                updateTextSearchSpan();
            } else {
                TextView textView = this.textView;
                textView.setText(Emoji.replaceEmoji(charSequence, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(14.0f), false));
            }
            imageView = this.buttonView;
            if (i != 0) {
                imageView.setImageResource(i);
                this.buttonView.setContentDescription(charSequence2);
                this.buttonView.setVisibility(0);
                this.editView.setVisibility(8);
            }
        }
        imageView.setVisibility(4);
        this.editView.setVisibility(8);
    }

    public void setTitleColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setUrl(CharSequence charSequence, int i) {
        this.url = charSequence;
        this.urlSearchLength = i;
        this.urlTextView.setVisibility(charSequence != null ? 0 : 8);
        updateUrlSearchSpan();
    }

    public void updateColors() {
        updateTextSearchSpan();
        updateUrlSearchSpan();
    }
}
