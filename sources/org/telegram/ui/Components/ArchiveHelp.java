package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class ArchiveHelp extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private int currentAccount;
    private Runnable linkCallback;
    private LinkSpanDrawable.LinksTextView subtitleTextView;

    public ArchiveHelp(Context context, int i, Theme.ResourcesProvider resourcesProvider, Runnable runnable, final Runnable runnable2) {
        super(context);
        this.currentAccount = i;
        this.linkCallback = runnable;
        ContactsController.getInstance(i).loadGlobalPrivacySetting();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        addView(linearLayout, LayoutHelper.createFrame(-1, -2, 17));
        ImageView imageView = new ImageView(context);
        imageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(80.0f), Theme.getColor(Theme.key_avatar_backgroundSaved, resourcesProvider)));
        imageView.setImageResource(R.drawable.large_archive);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        linearLayout.addView(imageView, LayoutHelper.createLinear(80, 80, 49, 0, runnable2 != null ? 14 : 0, 0, 14));
        TextView textView = new TextView(context);
        textView.setTextSize(1, 20.0f);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        textView.setTypeface(AndroidUtilities.bold());
        textView.setGravity(1);
        textView.setText(LocaleController.getString(R.string.ArchiveHintHeader1));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 1, 32, 0, 32, 9));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
        this.subtitleTextView = linksTextView;
        linksTextView.setTextSize(1, 14.0f);
        this.subtitleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
        this.subtitleTextView.setGravity(1);
        updateText();
        linearLayout.addView(this.subtitleTextView, LayoutHelper.createLinear(-1, -2, 1, 32, 0, 32, 25));
        linearLayout.addView(makeHint(R.drawable.msg_archive_archive, LocaleController.getString("ArchiveHintSection1"), LocaleController.getString("ArchiveHintSection1Info"), resourcesProvider), LayoutHelper.createLinear(-1, -2, 7, 32, 0, 32, 16));
        linearLayout.addView(makeHint(R.drawable.msg_archive_hide, LocaleController.getString("ArchiveHintSection2"), LocaleController.getString("ArchiveHintSection2Info"), resourcesProvider), LayoutHelper.createLinear(-1, -2, 7, 32, 0, 32, 16));
        linearLayout.addView(makeHint(R.drawable.msg_archive_stories, LocaleController.getString("ArchiveHintSection3"), LocaleController.getString("ArchiveHintSection3Info"), resourcesProvider), LayoutHelper.createLinear(-1, -2, 7, 32, 0, 32, 16));
        if (runnable2 != null) {
            ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
            buttonWithCounterView.setText(LocaleController.getString("GotIt"), false);
            buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    runnable2.run();
                }
            });
            linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 14.0f, 18.0f, 14.0f, 0.0f));
        }
    }

    private FrameLayout makeHint(int i, CharSequence charSequence, CharSequence charSequence2, Theme.ResourcesProvider resourcesProvider) {
        FrameLayout frameLayout = new FrameLayout(getContext());
        ImageView imageView = new ImageView(getContext());
        int i2 = Theme.key_dialogTextBlack;
        imageView.setColorFilter(Theme.getColor(i2, resourcesProvider));
        imageView.setImageResource(i);
        frameLayout.addView(imageView, LayoutHelper.createFrame(24, 24.0f, 51, 0.0f, 8.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        TextView textView = new TextView(getContext());
        textView.setTextColor(Theme.getColor(i2, resourcesProvider));
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextSize(0, AndroidUtilities.dp(14.0f));
        textView.setText(charSequence);
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 0.0f, 2.6f, 0.0f, 0.0f));
        TextView textView2 = new TextView(getContext());
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
        textView2.setTextSize(0, AndroidUtilities.dp(14.0f));
        textView2.setText(charSequence2);
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 2.6f, 0.0f, 0.0f));
        frameLayout.addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 55, 41.0f, 0.0f, 0.0f, 0.0f));
        return frameLayout;
    }

    private void updateText() {
        TLRPC.TL_globalPrivacySettings globalPrivacySettings = ContactsController.getInstance(this.currentAccount).getGlobalPrivacySettings();
        String string = LocaleController.getString(globalPrivacySettings != null ? globalPrivacySettings.keep_archived_unmuted : true ? "ArchiveHintSubtitle" : "ArchiveHintSubtitleUnmutedMove");
        int i = Theme.key_chat_messageLinkIn;
        SpannableStringBuilder replaceSingleTag = AndroidUtilities.replaceSingleTag(string, i, 0, this.linkCallback);
        SpannableString spannableString = new SpannableString(">");
        Drawable mutate = getContext().getResources().getDrawable(R.drawable.msg_arrowright).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(mutate);
        coloredImageSpan.setColorKey(i);
        coloredImageSpan.setSize(AndroidUtilities.dp(18.0f));
        coloredImageSpan.setWidth(AndroidUtilities.dp(11.0f));
        coloredImageSpan.setTranslateX(-AndroidUtilities.dp(5.0f));
        spannableString.setSpan(coloredImageSpan, 0, spannableString.length(), 33);
        this.subtitleTextView.setText(AndroidUtilities.replaceCharSequence(">", replaceSingleTag, spannableString));
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.privacyRulesUpdated) {
            updateText();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.privacyRulesUpdated);
        updateText();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.privacyRulesUpdated);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(AndroidUtilities.dp(400.0f), View.MeasureSpec.getSize(i)), 1073741824), i2);
    }
}
