package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class MemberRequestCell extends FrameLayout {
    private final AvatarDrawable avatarDrawable;
    private final BackupImageView avatarImageView;
    private TLRPC.TL_chatInviteImporter importer;
    private boolean isNeedDivider;
    private final SimpleTextView nameTextView;
    private final SimpleTextView statusTextView;

    public interface OnClickListener {
        void onAddClicked(TLRPC.TL_chatInviteImporter tL_chatInviteImporter);

        void onDismissClicked(TLRPC.TL_chatInviteImporter tL_chatInviteImporter);
    }

    public MemberRequestCell(Context context, final OnClickListener onClickListener, boolean z) {
        super(context);
        this.avatarDrawable = new AvatarDrawable();
        BackupImageView backupImageView = new BackupImageView(getContext());
        this.avatarImageView = backupImageView;
        SimpleTextView simpleTextView = new SimpleTextView(getContext());
        this.nameTextView = simpleTextView;
        SimpleTextView simpleTextView2 = new SimpleTextView(getContext());
        this.statusTextView = simpleTextView2;
        backupImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
        addView(backupImageView, LayoutHelper.createFrame(46, 46.0f, LocaleController.isRTL ? 5 : 3, 12.0f, 8.0f, 12.0f, 0.0f));
        simpleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        simpleTextView.setMaxLines(1);
        simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        simpleTextView.setTextSize(17);
        simpleTextView.setTypeface(AndroidUtilities.bold());
        boolean z2 = LocaleController.isRTL;
        addView(simpleTextView, LayoutHelper.createFrame(-1, -2.0f, 48, z2 ? 12.0f : 74.0f, 12.0f, z2 ? 74.0f : 12.0f, 0.0f));
        simpleTextView2.setGravity(LocaleController.isRTL ? 5 : 3);
        simpleTextView2.setMaxLines(1);
        simpleTextView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        simpleTextView2.setTextSize(14);
        boolean z3 = LocaleController.isRTL;
        addView(simpleTextView2, LayoutHelper.createFrame(-1, -2.0f, 48, z3 ? 12.0f : 74.0f, 36.0f, z3 ? 74.0f : 12.0f, 0.0f));
        int dp = AndroidUtilities.dp(17.0f);
        TextView textView = new TextView(getContext());
        textView.setBackground(Theme.AdaptiveRipple.filledRectByKey(Theme.key_featuredStickers_addButton, 4.0f));
        textView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        textView.setMaxLines(1);
        textView.setPadding(dp, 0, dp, 0);
        textView.setText(LocaleController.getString(z ? R.string.AddToChannel : R.string.AddToGroup));
        textView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        textView.setTextSize(14.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MemberRequestCell.this.lambda$new$0(onClickListener, view);
            }
        });
        boolean z4 = LocaleController.isRTL;
        addView(textView, LayoutHelper.createFrame(-2, 32.0f, z4 ? 5 : 3, z4 ? 0.0f : 73.0f, 62.0f, z4 ? 73.0f : 0.0f, 0.0f));
        float measureText = textView.getPaint().measureText(textView.getText().toString()) + (dp * 2);
        TextView textView2 = new TextView(getContext());
        textView2.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), 0, Theme.getColor(Theme.key_listSelector), -16777216));
        textView2.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        textView2.setMaxLines(1);
        textView2.setPadding(dp, 0, dp, 0);
        textView2.setText(LocaleController.getString(R.string.Dismiss));
        textView2.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText));
        textView2.setTextSize(14.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                MemberRequestCell.this.lambda$new$1(onClickListener, view);
            }
        });
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-2, AndroidUtilities.dp(32.0f), LocaleController.isRTL ? 5 : 3);
        layoutParams.topMargin = AndroidUtilities.dp(62.0f);
        layoutParams.leftMargin = LocaleController.isRTL ? 0 : (int) (AndroidUtilities.dp(79.0f) + measureText);
        layoutParams.rightMargin = LocaleController.isRTL ? (int) (measureText + AndroidUtilities.dp(79.0f)) : 0;
        addView(textView2, layoutParams);
    }

    public void lambda$new$0(OnClickListener onClickListener, View view) {
        TLRPC.TL_chatInviteImporter tL_chatInviteImporter;
        if (onClickListener == null || (tL_chatInviteImporter = this.importer) == null) {
            return;
        }
        onClickListener.onAddClicked(tL_chatInviteImporter);
    }

    public void lambda$new$1(OnClickListener onClickListener, View view) {
        TLRPC.TL_chatInviteImporter tL_chatInviteImporter;
        if (onClickListener == null || (tL_chatInviteImporter = this.importer) == null) {
            return;
        }
        onClickListener.onDismissClicked(tL_chatInviteImporter);
    }

    public BackupImageView getAvatarImageView() {
        return this.avatarImageView;
    }

    public TLRPC.TL_chatInviteImporter getImporter() {
        return this.importer;
    }

    public String getStatus() {
        return this.statusTextView.getText().toString();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.isNeedDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(72.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(72.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(107.0f), 1073741824));
    }

    public void setData(LongSparseArray longSparseArray, TLRPC.TL_chatInviteImporter tL_chatInviteImporter, boolean z) {
        SimpleTextView simpleTextView;
        String str;
        this.importer = tL_chatInviteImporter;
        this.isNeedDivider = z;
        setWillNotDraw(!z);
        TLRPC.User user = (TLRPC.User) longSparseArray.get(tL_chatInviteImporter.user_id);
        this.avatarDrawable.setInfo(user);
        this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
        this.nameTextView.setText(UserObject.getUserName(user));
        String formatDateAudio = LocaleController.formatDateAudio(tL_chatInviteImporter.date, false);
        if (tL_chatInviteImporter.via_chatlist) {
            simpleTextView = this.statusTextView;
            str = LocaleController.getString(R.string.JoinedViaFolder);
        } else {
            long j = tL_chatInviteImporter.approved_by;
            if (j == 0) {
                simpleTextView = this.statusTextView;
                str = LocaleController.formatString("RequestedToJoinAt", R.string.RequestedToJoinAt, formatDateAudio);
            } else {
                TLRPC.User user2 = (TLRPC.User) longSparseArray.get(j);
                if (user2 != null) {
                    this.statusTextView.setText(LocaleController.formatString("AddedBy", R.string.AddedBy, UserObject.getFirstName(user2), formatDateAudio));
                    return;
                } else {
                    simpleTextView = this.statusTextView;
                    str = "";
                }
            }
        }
        simpleTextView.setText(str);
    }
}
