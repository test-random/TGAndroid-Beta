package org.telegram.ui.Components.Premium.boosts.cells;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.spoilers.SpoilersTextView;

public class LinkCell extends FrameLayout {
    private ImageView imageView;
    private String link;
    private FrameLayout linkContainer;
    private SpoilersTextView linkView;
    private String slug;

    public LinkCell(Context context, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.linkContainer = new FrameLayout(context);
        SpoilersTextView spoilersTextView = new SpoilersTextView(context);
        this.linkView = spoilersTextView;
        spoilersTextView.setPadding(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(50.0f), AndroidUtilities.dp(13.0f));
        this.linkView.setTextSize(1, 16.0f);
        this.linkView.setEllipsize(TextUtils.TruncateAt.MIDDLE);
        this.linkView.setSingleLine(true);
        this.linkView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        SpoilersTextView spoilersTextView2 = this.linkView;
        spoilersTextView2.allowClickSpoilers = false;
        this.linkContainer.addView(spoilersTextView2, LayoutHelper.createFrame(-2, -2, 17));
        FrameLayout frameLayout = this.linkContainer;
        int dp = AndroidUtilities.dp(8.0f);
        int color = Theme.getColor(Theme.key_graySection, resourcesProvider);
        int i = Theme.key_listSelector;
        frameLayout.setBackground(Theme.createSimpleSelectorRoundRectDrawable(dp, color, ColorUtils.setAlphaComponent(Theme.getColor(i, resourcesProvider), 76)));
        addView(this.linkContainer, LayoutHelper.createFrame(-1, -2.0f, 0, 14.0f, 0.0f, 14.0f, 0.0f));
        this.linkContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                LinkCell.this.lambda$new$0(view);
            }
        });
        ImageView imageView = new ImageView(getContext());
        this.imageView = imageView;
        imageView.setImageResource(R.drawable.menu_copy_s);
        this.imageView.setColorFilter(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        this.imageView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        this.imageView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(20.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor(i, resourcesProvider), 76)));
        addView(this.imageView, LayoutHelper.createFrame(40, 40.0f, 21, 15.0f, 0.0f, 17.0f, 0.0f));
        this.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                LinkCell.this.lambda$new$1(view);
            }
        });
    }

    public void lambda$new$0(View view) {
        AndroidUtilities.addToClipboard(this.link);
    }

    public void lambda$new$1(View view) {
        AndroidUtilities.addToClipboard(this.link);
    }

    public void hideSlug(final Runnable runnable) {
        this.imageView.setVisibility(4);
        this.linkView.setPadding(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(18.0f));
        TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
        textStyleRun.flags |= 256;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("t.me/giftcode/" + this.slug);
        if (this.slug == null) {
            spannableStringBuilder.append((CharSequence) "1234567891011123654897566536223");
        }
        spannableStringBuilder.setSpan(new TextStyleSpan(textStyleRun), 0, spannableStringBuilder.length(), 33);
        this.linkView.setText(spannableStringBuilder);
        this.linkContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                runnable.run();
            }
        });
    }

    public void setSlug(String str) {
        this.slug = str;
        this.link = "https://t.me/giftcode/" + str;
        this.linkView.setText("t.me/giftcode/" + str);
    }
}
