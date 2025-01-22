package org.telegram.ui;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.StickerImageView;

public class SuggestClearDatabaseBottomSheet extends BottomSheet {
    private static SuggestClearDatabaseBottomSheet dialog;
    BaseFragment fragment;

    private SuggestClearDatabaseBottomSheet(final BaseFragment baseFragment) {
        super(baseFragment.getParentActivity(), false);
        this.fragment = baseFragment;
        Activity parentActivity = baseFragment.getParentActivity();
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        StickerImageView stickerImageView = new StickerImageView(parentActivity, this.currentAccount);
        stickerImageView.setStickerNum(7);
        stickerImageView.getImageReceiver().setAutoRepeat(1);
        linearLayout.addView(stickerImageView, LayoutHelper.createLinear(144, 144, 1, 0, 16, 0, 0));
        TextView textView = new TextView(parentActivity);
        textView.setGravity(8388611);
        int i = Theme.key_dialogTextBlack;
        textView.setTextColor(Theme.getColor(i));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setText(LocaleController.getString(R.string.SuggestClearDatabaseTitle));
        linearLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 30.0f, 21.0f, 0.0f));
        TextView textView2 = new TextView(parentActivity);
        textView2.setGravity(8388611);
        textView2.setTextSize(1, 15.0f);
        textView2.setTextColor(Theme.getColor(i));
        textView2.setText(AndroidUtilities.replaceTags(LocaleController.formatString("SuggestClearDatabaseMessage", R.string.SuggestClearDatabaseMessage, AndroidUtilities.formatFileSize(baseFragment.getMessagesStorage().getDatabaseSize()))));
        linearLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 15.0f, 21.0f, 16.0f));
        TextView textView3 = new TextView(parentActivity);
        textView3.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        textView3.setGravity(17);
        textView3.setTextSize(1, 14.0f);
        textView3.setTypeface(AndroidUtilities.bold());
        textView3.setText(LocaleController.getString(R.string.ClearLocalDatabase));
        textView3.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        textView3.setBackgroundDrawable(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.getColor(Theme.key_featuredStickers_addButton), ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_windowBackgroundWhite), 120)));
        linearLayout.addView(textView3, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 15.0f, 16.0f, 16.0f));
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                SuggestClearDatabaseBottomSheet.this.lambda$new$1(baseFragment, view);
            }
        });
        ScrollView scrollView = new ScrollView(parentActivity);
        scrollView.addView(linearLayout);
        setCustomView(scrollView);
    }

    public static void dismissDialog() {
        SuggestClearDatabaseBottomSheet suggestClearDatabaseBottomSheet = dialog;
        if (suggestClearDatabaseBottomSheet != null) {
            suggestClearDatabaseBottomSheet.lambda$new$0();
            dialog = null;
        }
    }

    public void lambda$new$0(BaseFragment baseFragment, AlertDialog alertDialog, int i) {
        if (baseFragment.getParentActivity() == null) {
            return;
        }
        MessagesController.getInstance(this.currentAccount).clearQueryTime();
        baseFragment.getMessagesStorage().clearLocalDatabase();
    }

    public void lambda$new$1(final BaseFragment baseFragment, View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(baseFragment.getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.LocalDatabaseClearTextTitle));
        builder.setMessage(LocaleController.getString(R.string.LocalDatabaseClearText));
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.CacheClear), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                SuggestClearDatabaseBottomSheet.this.lambda$new$0(baseFragment, alertDialog, i);
            }
        });
        AlertDialog create = builder.create();
        baseFragment.showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    public static void show(BaseFragment baseFragment) {
        if (dialog == null) {
            SuggestClearDatabaseBottomSheet suggestClearDatabaseBottomSheet = new SuggestClearDatabaseBottomSheet(baseFragment);
            dialog = suggestClearDatabaseBottomSheet;
            suggestClearDatabaseBottomSheet.show();
        }
    }

    @Override
    public void lambda$new$0() {
        super.lambda$new$0();
        dialog = null;
    }
}
