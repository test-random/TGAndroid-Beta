package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class PaymentInfoCell extends FrameLayout {
    private TextView detailExTextView;
    private TextView detailTextView;
    private BackupImageView imageView;
    private TextView nameTextView;

    public PaymentInfoCell(Context context) {
        super(context);
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(8.0f));
        addView(this.imageView, LayoutHelper.createFrame(100, 100.0f, LocaleController.isRTL ? 5 : 3, 10.0f, 10.0f, 10.0f, 0.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        int i = Theme.key_windowBackgroundWhiteBlackText;
        textView.setTextColor(Theme.getColor(i));
        this.nameTextView.setTextSize(1, 16.0f);
        this.nameTextView.setLines(1);
        this.nameTextView.setTypeface(AndroidUtilities.bold());
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        TextView textView2 = this.nameTextView;
        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
        textView2.setEllipsize(truncateAt);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        TextView textView3 = this.nameTextView;
        boolean z = LocaleController.isRTL;
        addView(textView3, LayoutHelper.createFrame(-1, -2.0f, (z ? 5 : 3) | 48, z ? 10.0f : 123.0f, 9.0f, z ? 123.0f : 10.0f, 0.0f));
        TextView textView4 = new TextView(context);
        this.detailTextView = textView4;
        textView4.setTextColor(Theme.getColor(i));
        this.detailTextView.setTextSize(1, 14.0f);
        this.detailTextView.setMaxLines(3);
        this.detailTextView.setEllipsize(truncateAt);
        this.detailTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        TextView textView5 = this.detailTextView;
        boolean z2 = LocaleController.isRTL;
        addView(textView5, LayoutHelper.createFrame(-1, -2.0f, (z2 ? 5 : 3) | 48, z2 ? 10.0f : 123.0f, 33.0f, z2 ? 123.0f : 10.0f, 0.0f));
        TextView textView6 = new TextView(context);
        this.detailExTextView = textView6;
        textView6.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        this.detailExTextView.setTextSize(1, 14.0f);
        this.detailExTextView.setLines(1);
        this.detailExTextView.setMaxLines(1);
        this.detailExTextView.setSingleLine(true);
        this.detailExTextView.setEllipsize(truncateAt);
        this.detailExTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        TextView textView7 = this.detailExTextView;
        boolean z3 = LocaleController.isRTL;
        addView(textView7, LayoutHelper.createFrame(-1, -2.0f, (z3 ? 5 : 3) | 48, z3 ? 10.0f : 123.0f, 90.0f, z3 ? 123.0f : 10.0f, 9.0f));
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int i3;
        if (this.imageView.getVisibility() != 8) {
            i3 = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(120.0f), 1073741824);
        } else {
            int makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, 0);
            measureChildWithMargins(this.detailTextView, i, 0, i2, 0);
            ((FrameLayout.LayoutParams) this.detailExTextView.getLayoutParams()).topMargin = AndroidUtilities.dp(33.0f) + this.detailTextView.getMeasuredHeight() + AndroidUtilities.dp(3.0f);
            i3 = makeMeasureSpec;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), i3);
    }

    public void setInfo(String str, String str2, TLRPC.WebDocument webDocument, String str3, Object obj) {
        int min;
        this.nameTextView.setText(str);
        this.detailTextView.setText(str2);
        this.detailExTextView.setText(str3);
        if (AndroidUtilities.isTablet()) {
            min = AndroidUtilities.getMinTabletSide();
        } else {
            Point point = AndroidUtilities.displaySize;
            min = Math.min(point.x, point.y);
        }
        float f = 640;
        float dp = f / (((int) (min * 0.7f)) - AndroidUtilities.dp(2.0f));
        int i = (int) (f / dp);
        int i2 = (int) (360 / dp);
        if (webDocument == null || !webDocument.mime_type.startsWith("image/")) {
            this.nameTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 9.0f, 17.0f, 0.0f));
            this.detailTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 33.0f, 17.0f, 0.0f));
            this.detailExTextView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, 17.0f, 90.0f, 17.0f, 9.0f));
            this.imageView.setVisibility(8);
            return;
        }
        TextView textView = this.nameTextView;
        boolean z = LocaleController.isRTL;
        textView.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (z ? 5 : 3) | 48, z ? 10.0f : 123.0f, 9.0f, z ? 123.0f : 10.0f, 0.0f));
        TextView textView2 = this.detailTextView;
        boolean z2 = LocaleController.isRTL;
        textView2.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (z2 ? 5 : 3) | 48, z2 ? 10.0f : 123.0f, 33.0f, z2 ? 123.0f : 10.0f, 0.0f));
        TextView textView3 = this.detailExTextView;
        boolean z3 = LocaleController.isRTL;
        textView3.setLayoutParams(LayoutHelper.createFrame(-1, -2.0f, (z3 ? 5 : 3) | 48, z3 ? 10.0f : 123.0f, 90.0f, z3 ? 123.0f : 10.0f, 0.0f));
        this.imageView.setVisibility(0);
        this.imageView.getImageReceiver().setImage(ImageLocation.getForWebFile(WebFile.createWithWebDocument(webDocument)), String.format(Locale.US, "%d_%d", Integer.valueOf(i), Integer.valueOf(i2)), null, null, -1L, null, obj, 1);
    }

    public void setInvoice(TLRPC.TL_messageMediaInvoice tL_messageMediaInvoice, String str) {
        setInfo(tL_messageMediaInvoice.title, tL_messageMediaInvoice.description, tL_messageMediaInvoice.webPhoto, str, tL_messageMediaInvoice);
    }

    public void setReceipt(TLRPC.PaymentReceipt paymentReceipt, String str) {
        setInfo(paymentReceipt.title, paymentReceipt.description, paymentReceipt.photo, str, paymentReceipt);
    }
}
