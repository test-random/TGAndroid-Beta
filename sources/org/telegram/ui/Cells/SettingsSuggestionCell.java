package org.telegram.ui.Cells;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.URLSpanNoUnderline;

public abstract class SettingsSuggestionCell extends LinearLayout {
    private int currentAccount;
    private int currentType;
    private TextView detailTextView;
    private TextView noButton;
    private Theme.ResourcesProvider resourcesProvider;
    private TextView textView;
    private TextView yesButton;

    public SettingsSuggestionCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        View.OnClickListener onClickListener;
        this.currentAccount = UserConfig.selectedAccount;
        this.resourcesProvider = resourcesProvider;
        setOrientation(1);
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.bold());
        this.textView.setEllipsize(TextUtils.TruncateAt.END);
        this.textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader, resourcesProvider));
        addView(this.textView, LayoutHelper.createLinear(-1, -2, (LocaleController.isRTL ? 5 : 3) | 48, 21, 15, 21, 0));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        this.detailTextView = linksTextView;
        linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
        this.detailTextView.setTextSize(1, 14.0f);
        this.detailTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, resourcesProvider));
        this.detailTextView.setHighlightColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkSelection, resourcesProvider));
        this.detailTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.detailTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        addView(this.detailTextView, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 21, 14, 21, 0));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        addView(linearLayout, LayoutHelper.createLinear(-1, 44, 21.0f, 16.0f, 21.0f, 15.0f));
        int i = 0;
        while (i < 2) {
            TextView textView2 = new TextView(context);
            textView2.setBackground(Theme.AdaptiveRipple.filledRectByKey(Theme.key_featuredStickers_addButton, 8.0f));
            ScaleStateListAnimator.apply(textView2, 0.02f, 1.5f);
            textView2.setLines(1);
            textView2.setSingleLine(true);
            textView2.setGravity(1);
            textView2.setEllipsize(TextUtils.TruncateAt.END);
            textView2.setGravity(17);
            textView2.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText, resourcesProvider));
            textView2.setTextSize(1, 14.0f);
            textView2.setTypeface(AndroidUtilities.bold());
            linearLayout.addView(textView2, LayoutHelper.createLinear(0, 44, 0.5f, i == 0 ? 0 : 4, 0, i == 0 ? 4 : 0, 0));
            if (i == 0) {
                this.yesButton = textView2;
                onClickListener = new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        SettingsSuggestionCell.this.lambda$new$0(view);
                    }
                };
            } else {
                this.noButton = textView2;
                onClickListener = new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        SettingsSuggestionCell.this.lambda$new$1(view);
                    }
                };
            }
            textView2.setOnClickListener(onClickListener);
            i++;
        }
    }

    public void lambda$new$0(View view) {
        onYesClick(this.currentType);
    }

    public void lambda$new$1(View view) {
        onNoClick(this.currentType);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), i2);
    }

    protected abstract void onNoClick(int i);

    protected abstract void onYesClick(int i);

    public void setType(int i) {
        TextView textView;
        int i2;
        this.currentType = i;
        if (i == 0) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).clientUserId));
            this.textView.setText(LocaleController.formatString("CheckPhoneNumber", R.string.CheckPhoneNumber, PhoneFormat.getInstance().format("+" + user.phone)));
            String string = LocaleController.getString(R.string.CheckPhoneNumberInfo);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
            int indexOf = string.indexOf("**");
            int lastIndexOf = string.lastIndexOf("**");
            if (indexOf >= 0 && lastIndexOf >= 0 && indexOf != lastIndexOf) {
                spannableStringBuilder.replace(lastIndexOf, lastIndexOf + 2, (CharSequence) "");
                spannableStringBuilder.replace(indexOf, indexOf + 2, (CharSequence) "");
                try {
                    spannableStringBuilder.setSpan(new URLSpanNoUnderline(LocaleController.getString(R.string.CheckPhoneNumberLearnMoreUrl)), indexOf, lastIndexOf - 2, 33);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            this.detailTextView.setText(spannableStringBuilder);
            this.yesButton.setText(LocaleController.getString(R.string.CheckPhoneNumberYes));
            this.noButton.setVisibility(0);
            textView = this.noButton;
            i2 = R.string.CheckPhoneNumberNo;
        } else {
            if (i != 1) {
                if (i == 2) {
                    this.textView.setText(LocaleController.getString(R.string.GraceSuggestionTitle));
                    this.detailTextView.setText(LocaleController.getString(R.string.GraceSuggestionMessage));
                    this.yesButton.setText(LocaleController.getString(R.string.GraceSuggestionButton));
                    this.noButton.setVisibility(8);
                    return;
                }
                return;
            }
            this.textView.setText(LocaleController.getString(R.string.YourPasswordHeader));
            this.detailTextView.setText(LocaleController.getString(R.string.YourPasswordRemember));
            this.yesButton.setText(LocaleController.getString(R.string.YourPasswordRememberYes));
            this.noButton.setVisibility(0);
            textView = this.noButton;
            i2 = R.string.YourPasswordRememberNo;
        }
        textView.setText(LocaleController.getString(i2));
    }
}
