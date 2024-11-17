package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public abstract class GroupCallInvitedCell extends FrameLayout {
    private AvatarDrawable avatarDrawable;
    private BackupImageView avatarImageView;
    private TLRPC.User currentUser;
    private Paint dividerPaint;
    private int grayIconColor;
    private ImageView muteButton;
    private SimpleTextView nameTextView;
    private boolean needDivider;
    private SimpleTextView statusTextView;

    public GroupCallInvitedCell(Context context) {
        super(context);
        this.grayIconColor = Theme.key_voipgroup_mutedIcon;
        Paint paint = new Paint();
        this.dividerPaint = paint;
        paint.setColor(Theme.getColor(Theme.key_voipgroup_actionBar));
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        BackupImageView backupImageView2 = this.avatarImageView;
        boolean z = LocaleController.isRTL;
        addView(backupImageView2, LayoutHelper.createFrame(46, 46.0f, (z ? 5 : 3) | 48, z ? 0.0f : 11.0f, 6.0f, z ? 11.0f : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor(Theme.key_voipgroup_nameText));
        this.nameTextView.setTypeface(AndroidUtilities.bold());
        this.nameTextView.setTextSize(16);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        SimpleTextView simpleTextView2 = this.nameTextView;
        boolean z2 = LocaleController.isRTL;
        addView(simpleTextView2, LayoutHelper.createFrame(-1, 20.0f, (z2 ? 5 : 3) | 48, z2 ? 54.0f : 67.0f, 10.0f, z2 ? 67.0f : 54.0f, 0.0f));
        SimpleTextView simpleTextView3 = new SimpleTextView(context);
        this.statusTextView = simpleTextView3;
        simpleTextView3.setTextSize(15);
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        this.statusTextView.setTextColor(Theme.getColor(this.grayIconColor));
        this.statusTextView.setText(LocaleController.getString(R.string.Invited));
        SimpleTextView simpleTextView4 = this.statusTextView;
        boolean z3 = LocaleController.isRTL;
        addView(simpleTextView4, LayoutHelper.createFrame(-1, 20.0f, (z3 ? 5 : 3) | 48, z3 ? 54.0f : 67.0f, 32.0f, z3 ? 67.0f : 54.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.muteButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.muteButton.setImageResource(R.drawable.msg_invited);
        this.muteButton.setImportantForAccessibility(2);
        this.muteButton.setPadding(0, 0, AndroidUtilities.dp(4.0f), 0);
        this.muteButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(this.grayIconColor), PorterDuff.Mode.MULTIPLY));
        addView(this.muteButton, LayoutHelper.createFrame(48, -1.0f, (LocaleController.isRTL ? 3 : 5) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
        setWillNotDraw(false);
        setFocusable(true);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(68.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(68.0f) : 0), getMeasuredHeight() - 1, this.dividerPaint);
        }
        super.dispatchDraw(canvas);
    }

    public CharSequence getName() {
        return this.nameTextView.getText();
    }

    public TLRPC.User getUser() {
        return this.currentUser;
    }

    public boolean hasAvatarSet() {
        return this.avatarImageView.getImageReceiver().hasNotThumb();
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    @Override
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f), 1073741824));
    }

    public void setData(int i, Long l) {
        TLRPC.User user = MessagesController.getInstance(i).getUser(l);
        this.currentUser = user;
        this.avatarDrawable.setInfo(user);
        this.nameTextView.setText(UserObject.getUserName(this.currentUser));
        this.avatarImageView.getImageReceiver().setCurrentAccount(i);
        this.avatarImageView.setForUserOrChat(this.currentUser, this.avatarDrawable);
    }

    public void setDrawDivider(boolean z) {
        this.needDivider = z;
        invalidate();
    }

    public void setGrayIconColor(int i, int i2) {
        this.grayIconColor = i;
        this.muteButton.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
        this.statusTextView.setTextColor(i2);
        Theme.setSelectorDrawableColor(this.muteButton.getDrawable(), i2 & 620756991, true);
    }
}
