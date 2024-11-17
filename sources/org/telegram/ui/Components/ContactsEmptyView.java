package org.telegram.ui.Components;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LinkSpanDrawable;

public class ContactsEmptyView extends LinearLayout {
    private LinkSpanDrawable.LinksTextView buttonTextView;
    private int currentAccount;
    private LoadingStickerDrawable drawable;
    private ArrayList imageViews;
    private BackupImageView stickerView;
    private TextView subtitleTextView;
    private ArrayList textViews;
    private TextView titleTextView;

    public ContactsEmptyView(Context context) {
        super(context);
        this.textViews = new ArrayList();
        this.imageViews = new ArrayList();
        this.currentAccount = UserConfig.selectedAccount;
        setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
        setOrientation(1);
        this.stickerView = new BackupImageView(context);
        LoadingStickerDrawable loadingStickerDrawable = new LoadingStickerDrawable(this.stickerView, "m418 282.6c13.4-21.1 20.2-44.9 20.2-70.8 0-88.3-79.8-175.3-178.9-175.3-100.1 0-178.9 88-178.9 175.3 0 46.6 16.9 73.1 29.1 86.1-19.3 23.4-30.9 52.3-34.6 86.1-2.5 22.7 3.2 41.4 17.4 57.3 14.3 16 51.7 35 148.1 35 41.2 0 119.9-5.3 156.7-18.3 49.5-17.4 59.2-41.1 59.2-76.2 0-41.5-12.9-74.8-38.3-99.2z", AndroidUtilities.dp(130.0f), AndroidUtilities.dp(130.0f));
        this.drawable = loadingStickerDrawable;
        this.stickerView.setImageDrawable(loadingStickerDrawable);
        if (!AndroidUtilities.isTablet()) {
            addView(this.stickerView, LayoutHelper.createLinear(130, 130, 49, 0, 2, 0, 0));
        }
        TextView textView = new TextView(context);
        this.titleTextView = textView;
        textView.setTextSize(1, 18.0f);
        TextView textView2 = this.titleTextView;
        int i = Theme.key_windowBackgroundWhiteBlackText;
        textView2.setTextColor(Theme.getColor(i));
        this.titleTextView.setGravity(1);
        this.titleTextView.setText(LocaleController.getString(R.string.NoContactsYet2));
        this.titleTextView.setTypeface(AndroidUtilities.bold());
        addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 18, 0, 9));
        TextView textView3 = new TextView(context);
        this.subtitleTextView = textView3;
        textView3.setTextSize(1, 14.0f);
        this.subtitleTextView.setTextColor(Theme.getColor(i));
        this.subtitleTextView.setGravity(1);
        this.subtitleTextView.setText(LocaleController.getString(R.string.NoContactsYet2Sub));
        this.subtitleTextView.setMaxWidth(AndroidUtilities.dp(260.0f));
        addView(this.subtitleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 0, 0, 14));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
        this.buttonTextView = linksTextView;
        linksTextView.setTextSize(1, 15.0f);
        this.buttonTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn));
        this.buttonTextView.setTextColor(Theme.getColor(i));
        this.buttonTextView.setGravity(1);
        this.buttonTextView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(8.0f));
        this.buttonTextView.setDisablePaddingsOffsetY(true);
        this.buttonTextView.setText(AndroidUtilities.replaceArrows(AndroidUtilities.makeClickable(LocaleController.getString(R.string.NoContactsYet2Invite), new Runnable() {
            @Override
            public final void run() {
                ContactsEmptyView.this.lambda$new$0();
            }
        }), true, AndroidUtilities.dp(2.6666667f), AndroidUtilities.dp(1.0f)));
        this.buttonTextView.setMaxWidth(AndroidUtilities.dp(260.0f));
        addView(this.buttonTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 0, 0, 14));
    }

    private void setSticker() {
        this.stickerView.setImageDrawable(new RLottieDrawable(R.raw.utyan_empty, "utyan_empty", AndroidUtilities.dp(130.0f), AndroidUtilities.dp(130.0f)));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setSticker();
    }

    public void lambda$new$0() {
        Activity findActivity = AndroidUtilities.findActivity(getContext());
        if (findActivity == null || findActivity.isFinishing()) {
            return;
        }
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("text/plain");
        String inviteText = ContactsController.getInstance(this.currentAccount).getInviteText(0);
        intent.putExtra("android.intent.extra.TEXT", inviteText);
        findActivity.startActivityForResult(Intent.createChooser(intent, inviteText), 500);
    }
}
