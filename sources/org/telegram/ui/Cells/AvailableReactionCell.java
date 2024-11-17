package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import androidx.core.content.ContextCompat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;

public class AvailableReactionCell extends FrameLayout {
    private boolean canLock;
    private CheckBox2 checkBox;
    private BackupImageView imageView;
    public boolean locked;
    private View overlaySelectorView;
    public TLRPC.TL_availableReaction react;
    private Switch switchView;
    private SimpleTextView textView;

    public AvailableReactionCell(Context context, boolean z, boolean z2) {
        super(context);
        View view;
        float f;
        float f2;
        float f3;
        float f4;
        this.canLock = z2;
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.textView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.textView.setTextSize(16);
        this.textView.setTypeface(AndroidUtilities.bold());
        this.textView.setMaxLines(1);
        this.textView.setMaxLines(1);
        this.textView.setGravity(16 | LayoutHelper.getAbsoluteGravityStart());
        addView(this.textView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388627, 81.0f, 0.0f, 61.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrameRelatively(32.0f, 32.0f, 8388627, 23.0f, 0.0f, 0.0f, 0.0f));
        if (z) {
            CheckBox2 checkBox2 = new CheckBox2(context, 26, null);
            this.checkBox = checkBox2;
            checkBox2.setDrawUnchecked(false);
            this.checkBox.setColor(-1, -1, Theme.key_radioBackgroundChecked);
            this.checkBox.setDrawBackgroundAsArc(-1);
            view = this.checkBox;
            f = 22.0f;
            f2 = 0.0f;
            f3 = 26.0f;
            f4 = 26.0f;
        } else {
            Switch r11 = new Switch(context);
            this.switchView = r11;
            r11.setColors(Theme.key_switchTrack, Theme.key_switchTrackChecked, Theme.key_switchTrackBlueThumb, Theme.key_switchTrackBlueThumbChecked);
            view = this.switchView;
            f = 22.0f;
            f2 = 0.0f;
            f3 = 37.0f;
            f4 = 20.0f;
        }
        addView(view, LayoutHelper.createFrameRelatively(f3, f4, 8388629, 0.0f, 0.0f, f, f2));
        View view2 = new View(context);
        this.overlaySelectorView = view2;
        view2.setBackground(Theme.getSelectorDrawable(false));
        addView(this.overlaySelectorView, LayoutHelper.createFrame(-1, -1.0f));
        setWillNotDraw(false);
    }

    public void bind(TLRPC.TL_availableReaction tL_availableReaction, boolean z, int i) {
        TLRPC.TL_availableReaction tL_availableReaction2;
        boolean z2 = (tL_availableReaction == null || (tL_availableReaction2 = this.react) == null || !tL_availableReaction.reaction.equals(tL_availableReaction2.reaction)) ? false : true;
        this.react = tL_availableReaction;
        this.textView.setText(tL_availableReaction.title);
        this.imageView.setImage(ImageLocation.getForDocument(tL_availableReaction.activate_animation), "30_30_pcache", "tgs", DocumentObject.getSvgThumb(tL_availableReaction.static_icon, Theme.key_windowBackgroundGray, 1.0f), tL_availableReaction);
        boolean z3 = this.canLock && tL_availableReaction.premium && !UserConfig.getInstance(i).isPremium();
        this.locked = z3;
        if (z3) {
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.other_lockedfolders2);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_stickers_menu), PorterDuff.Mode.MULTIPLY));
            this.textView.setRightDrawable(drawable);
        } else {
            this.textView.setRightDrawable((Drawable) null);
        }
        setChecked(z, z2);
    }

    public boolean isChecked() {
        Switch r0 = this.switchView;
        if (r0 != null) {
            return r0.isChecked();
        }
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            return checkBox2.isChecked();
        }
        return false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        float strokeWidth = Theme.dividerPaint.getStrokeWidth();
        int dp = AndroidUtilities.dp(81.0f);
        int i = 0;
        if (LocaleController.isRTL) {
            i = dp;
            dp = 0;
        }
        canvas.drawLine(getPaddingLeft() + dp, getHeight() - strokeWidth, (getWidth() - getPaddingRight()) - i, getHeight() - strokeWidth, Theme.dividerPaint);
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setEnabled(true);
        accessibilityNodeInfo.setClickable(true);
        if (this.switchView != null) {
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setChecked(isChecked());
            accessibilityNodeInfo.setClassName("android.widget.Switch");
        } else if (isChecked()) {
            accessibilityNodeInfo.setSelected(true);
        }
        accessibilityNodeInfo.setContentDescription(this.textView.getText());
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec((int) (AndroidUtilities.dp(58.0f) + Theme.dividerPaint.getStrokeWidth()), 1073741824));
    }

    public void setChecked(boolean z) {
        setChecked(z, false);
    }

    public void setChecked(boolean z, boolean z2) {
        Switch r0 = this.switchView;
        if (r0 != null) {
            r0.setChecked(z, z2);
        }
        CheckBox2 checkBox2 = this.checkBox;
        if (checkBox2 != null) {
            checkBox2.setChecked(z, z2);
        }
    }
}
