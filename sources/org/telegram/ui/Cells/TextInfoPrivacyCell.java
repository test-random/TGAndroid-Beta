package org.telegram.ui.Cells;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.util.Property;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;

public class TextInfoPrivacyCell extends FrameLayout {
    private int bottomPadding;
    private int fixedSize;
    private boolean isRTL;
    private int linkTextColorKey;
    private Integer linkTextRippleColor;
    private LinkSpanDrawable.LinkCollector links;
    private final Theme.ResourcesProvider resourcesProvider;
    private CharSequence text;
    private LinkSpanDrawable.LinksTextView textView;
    private int topPadding;

    public TextInfoPrivacyCell(Context context) {
        this(context, 21, null);
    }

    public TextInfoPrivacyCell(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.linkTextColorKey = Theme.key_windowBackgroundWhiteLinkText;
        this.topPadding = 10;
        this.bottomPadding = 17;
        this.resourcesProvider = resourcesProvider;
        LinkSpanDrawable.LinkCollector linkCollector = new LinkSpanDrawable.LinkCollector(this);
        this.links = linkCollector;
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, linkCollector, resourcesProvider) {
            @Override
            public void onDraw(Canvas canvas) {
                TextInfoPrivacyCell.this.onTextDraw();
                super.onDraw(canvas);
                TextInfoPrivacyCell.this.afterTextDraw();
            }

            @Override
            public int overrideColor() {
                return TextInfoPrivacyCell.this.linkTextRippleColor != null ? TextInfoPrivacyCell.this.linkTextRippleColor.intValue() : super.overrideColor();
            }
        };
        this.textView = linksTextView;
        linksTextView.setTextSize(1, 14.0f);
        this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
        this.textView.setPadding(0, AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(17.0f));
        this.textView.setMovementMethod(LinkMovementMethod.getInstance());
        LinkSpanDrawable.LinksTextView linksTextView2 = this.textView;
        int i2 = Theme.key_windowBackgroundWhiteGrayText4;
        linksTextView2.setTextColor(getThemedColor(i2));
        this.textView.setEmojiColor(getThemedColor(i2));
        this.textView.setLinkTextColor(getThemedColor(this.linkTextColorKey));
        this.textView.setImportantForAccessibility(2);
        float f = i;
        addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, f, 0.0f, f, 0.0f));
        this.isRTL = LocaleController.isRTL;
        setWillNotDraw(false);
    }

    public TextInfoPrivacyCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, 21, resourcesProvider);
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    protected void afterTextDraw() {
    }

    public CharSequence getText() {
        return this.textView.getText();
    }

    public LinkSpanDrawable.LinksTextView getTextView() {
        return this.textView;
    }

    public int length() {
        return this.textView.length();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (this.links != null) {
            canvas.save();
            canvas.translate(this.textView.getLeft(), this.textView.getTop());
            if (this.links.draw(canvas)) {
                invalidate();
            }
            canvas.restore();
        }
        super.onDraw(canvas);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(TextView.class.getName());
        accessibilityNodeInfo.setText(this.text);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int i3 = this.fixedSize;
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), i3 == -1 ? View.MeasureSpec.makeMeasureSpec(0, 1073741824) : i3 != 0 ? View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.fixedSize), 1073741824) : View.MeasureSpec.makeMeasureSpec(0, 0));
    }

    protected void onTextDraw() {
    }

    public void setBottomPadding(int i) {
        this.bottomPadding = i;
    }

    public void setEnabled(boolean z, ArrayList arrayList) {
        if (arrayList != null) {
            arrayList.add(ObjectAnimator.ofFloat(this.textView, (Property<LinkSpanDrawable.LinksTextView, Float>) View.ALPHA, z ? 1.0f : 0.5f));
        } else {
            this.textView.setAlpha(z ? 1.0f : 0.5f);
        }
    }

    public void setFixedSize(int i) {
        this.fixedSize = i;
    }

    public void setLinkTextColorKey(int i) {
        this.linkTextColorKey = i;
    }

    public void setLinkTextRippleColor(Integer num) {
        this.linkTextRippleColor = num;
    }

    public void setText(CharSequence charSequence) {
        if (TextUtils.equals(charSequence, this.text)) {
            return;
        }
        this.text = charSequence;
        LinkSpanDrawable.LinksTextView linksTextView = this.textView;
        if (charSequence == null) {
            linksTextView.setPadding(0, AndroidUtilities.dp(2.0f), 0, 0);
        } else {
            linksTextView.setPadding(0, AndroidUtilities.dp(this.topPadding), 0, AndroidUtilities.dp(this.bottomPadding));
        }
        SpannableString spannableString = null;
        if (charSequence != null) {
            int length = charSequence.length();
            for (int i = 0; i < length - 1; i++) {
                if (charSequence.charAt(i) == '\n') {
                    int i2 = i + 1;
                    if (charSequence.charAt(i2) == '\n') {
                        if (spannableString == null) {
                            spannableString = new SpannableString(charSequence);
                        }
                        spannableString.setSpan(new AbsoluteSizeSpan(10, true), i2, i + 2, 33);
                    }
                }
            }
        }
        LinkSpanDrawable.LinksTextView linksTextView2 = this.textView;
        if (spannableString != null) {
            charSequence = spannableString;
        }
        linksTextView2.setText(charSequence);
    }

    public void setTextColor(int i) {
        this.textView.setTextColor(i);
    }

    public void setTextColorByKey(int i) {
        this.textView.setTextColor(getThemedColor(i));
        this.textView.setTag(Integer.valueOf(i));
    }

    public void setTextGravity(int i) {
        this.textView.setGravity(i);
    }

    public void setTopPadding(int i) {
        this.topPadding = i;
    }

    public void updateRTL() {
        boolean z = this.isRTL;
        boolean z2 = LocaleController.isRTL;
        if (z == z2) {
            return;
        }
        this.isRTL = z2;
        this.textView.setGravity(z2 ? 5 : 3);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textView.getLayoutParams();
        layoutParams.gravity = (LocaleController.isRTL ? 5 : 3) | 48;
        this.textView.setLayoutParams(layoutParams);
    }
}
