package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;

public class SponsoredMessageInfoView extends FrameLayout {
    public SponsoredMessageInfoView(final Context context, final Runnable runnable, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        textView.setText(LocaleController.getString(R.string.SponsoredMessageInfo));
        textView.setTypeface(AndroidUtilities.bold());
        int i = Theme.key_windowBackgroundWhiteBlackText;
        textView.setTextColor(Theme.getColor(i, resourcesProvider));
        textView.setTextSize(1, 20.0f);
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        linksTextView.setText(AndroidUtilities.replaceLinks(LocaleController.getString("SponsoredMessageInfo2Description1"), resourcesProvider));
        linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
        linksTextView.setTextColor(Theme.getColor(i, resourcesProvider));
        linksTextView.setTextSize(1, 14.0f);
        linksTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        linksTextView.setOnLinkPressListener(new LinkSpanDrawable.LinksTextView.OnLinkPress() {
            @Override
            public final void run(ClickableSpan clickableSpan) {
                SponsoredMessageInfoView.this.lambda$new$0(runnable, clickableSpan);
            }
        });
        LinkSpanDrawable.LinksTextView linksTextView2 = new LinkSpanDrawable.LinksTextView(context);
        linksTextView2.setText(AndroidUtilities.replaceLinks(LocaleController.getString("SponsoredMessageInfo2Description2"), resourcesProvider));
        linksTextView2.setTextColor(Theme.getColor(i, resourcesProvider));
        linksTextView2.setTextSize(1, 14.0f);
        linksTextView2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        linksTextView2.setOnLinkPressListener(new LinkSpanDrawable.LinksTextView.OnLinkPress() {
            @Override
            public final void run(ClickableSpan clickableSpan) {
                SponsoredMessageInfoView.this.lambda$new$1(runnable, clickableSpan);
            }
        });
        LinkSpanDrawable.LinksTextView linksTextView3 = new LinkSpanDrawable.LinksTextView(context);
        linksTextView3.setText(AndroidUtilities.replaceLinks(LocaleController.getString("SponsoredMessageInfo2Description3"), resourcesProvider));
        linksTextView3.setTextColor(Theme.getColor(i, resourcesProvider));
        linksTextView3.setTextSize(1, 14.0f);
        linksTextView3.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        linksTextView3.setOnLinkPressListener(new LinkSpanDrawable.LinksTextView.OnLinkPress() {
            @Override
            public final void run(ClickableSpan clickableSpan) {
                SponsoredMessageInfoView.this.lambda$new$2(runnable, clickableSpan);
            }
        });
        final Paint paint = new Paint(1);
        paint.setStyle(Paint.Style.STROKE);
        int i2 = Theme.key_featuredStickers_addButton;
        paint.setColor(Theme.getColor(i2, resourcesProvider));
        paint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        TextView textView2 = new TextView(context) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), getMeasuredWidth() - AndroidUtilities.dp(1.0f), getMeasuredHeight() - AndroidUtilities.dp(1.0f));
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), paint);
            }
        };
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
                Browser.openUrl(context, LocaleController.getString(R.string.SponsoredMessageAlertLearnMoreUrl));
            }
        });
        textView2.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        textView2.setText(LocaleController.getString(R.string.SponsoredMessageAlertLearnMoreUrl));
        textView2.setTextColor(Theme.getColor(i2, resourcesProvider));
        textView2.setBackground(Theme.AdaptiveRipple.filledRect(Theme.getColor(Theme.key_dialogBackground, resourcesProvider), 4.0f));
        textView2.setTextSize(1, 14.0f);
        textView2.setGravity(16);
        LinkSpanDrawable.LinksTextView linksTextView4 = new LinkSpanDrawable.LinksTextView(context);
        linksTextView4.setText(AndroidUtilities.replaceLinks(LocaleController.getString("SponsoredMessageInfo2Description4"), resourcesProvider));
        linksTextView4.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        linksTextView4.setTextColor(Theme.getColor(i, resourcesProvider));
        linksTextView4.setTextSize(1, 14.0f);
        textView.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        linearLayout.addView(textView);
        linksTextView.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        linearLayout.addView(linksTextView, LayoutHelper.createLinear(-1, -2, 0, 0, 18, 0, 0));
        linksTextView2.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        linearLayout.addView(linksTextView2, LayoutHelper.createLinear(-1, -2, 0, 0, 24, 0, 0));
        linksTextView3.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        linearLayout.addView(linksTextView3, LayoutHelper.createLinear(-1, -2, 0, 0, 24, 0, 0));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-2, 34, 1, 22, 14, 22, 0));
        linksTextView4.setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), 0);
        linearLayout.addView(linksTextView4, LayoutHelper.createLinear(-1, -2, 0, 0, 14, 0, 0));
        ScrollView scrollView = new ScrollView(getContext());
        scrollView.addView(linearLayout);
        addView(scrollView, LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 12.0f, 0.0f, 22.0f));
    }

    public void lambda$new$0(Runnable runnable, ClickableSpan clickableSpan) {
        if (runnable != null) {
            runnable.run();
        }
        clickableSpan.onClick(this);
    }

    public void lambda$new$1(Runnable runnable, ClickableSpan clickableSpan) {
        if (runnable != null) {
            runnable.run();
        }
        clickableSpan.onClick(this);
    }

    public void lambda$new$2(Runnable runnable, ClickableSpan clickableSpan) {
        if (runnable != null) {
            runnable.run();
        }
        clickableSpan.onClick(this);
    }
}
