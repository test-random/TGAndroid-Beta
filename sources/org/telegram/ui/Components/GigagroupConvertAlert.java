package org.telegram.ui.Components;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;

public abstract class GigagroupConvertAlert extends BottomSheet {

    public static class BottomSheetCell extends FrameLayout {
        private View background;
        private TextView textView;

        public BottomSheetCell(Context context) {
            super(context);
            View view = new View(context);
            this.background = view;
            view.setBackground(Theme.AdaptiveRipple.filledRectByKey(Theme.key_featuredStickers_addButton, 4.0f));
            addView(this.background, LayoutHelper.createFrame(-1, -1.0f, 0, 16.0f, 16.0f, 16.0f, 16.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setLines(1);
            this.textView.setSingleLine(true);
            this.textView.setGravity(1);
            this.textView.setEllipsize(TextUtils.TruncateAt.END);
            this.textView.setGravity(17);
            this.textView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            this.textView.setTextSize(1, 14.0f);
            this.textView.setTypeface(AndroidUtilities.bold());
            addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(80.0f), 1073741824));
        }

        public void setText(CharSequence charSequence) {
            this.textView.setText(charSequence);
        }
    }

    public GigagroupConvertAlert(final android.content.Context r19, final org.telegram.ui.ActionBar.BaseFragment r20) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GigagroupConvertAlert.<init>(android.content.Context, org.telegram.ui.ActionBar.BaseFragment):void");
    }

    public void lambda$new$0(AlertDialog alertDialog, int i) {
        onCovert();
    }

    public void lambda$new$1(Context context, BaseFragment baseFragment, View view) {
        dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(LocaleController.getString(R.string.GigagroupConvertAlertTitle));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.getString(R.string.GigagroupConvertAlertText)));
        builder.setPositiveButton(LocaleController.getString(R.string.GigagroupConvertAlertConver), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                GigagroupConvertAlert.this.lambda$new$0(alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        baseFragment.showDialog(builder.create());
    }

    public void lambda$new$2(View view) {
        onCancel();
        dismiss();
    }

    protected abstract void onCancel();

    protected abstract void onCovert();
}
