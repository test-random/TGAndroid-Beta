package org.telegram.ui.Components.Premium.boosts.cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import java.util.Date;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;

public class TableCell extends FrameLayout {
    private final TextView dateNameTextView;
    private final TextView dateTextView;
    private FrameLayout fromFrameLayout;
    private final BackupImageView fromImageView;
    private final TextView fromNameTextView;
    private final TextView fromTextView;
    private TLRPC.TL_payments_checkedGiftCode giftCode;
    private final TextView giftNameTextView;
    private final TextView giftTextView;
    private final Paint linePaint;
    private final TextView reasonNameTextView;
    private final TextView reasonTextView;
    private final Theme.ResourcesProvider resourcesProvider;
    private final Path roundPath;
    private final RectF roundRect;
    private TableRow tableRow4;
    private FrameLayout toFrameLayout;
    private final BackupImageView toImageView;
    private final TextView toNameTextView;
    private final TextView toTextView;

    public TableCell(Context context, final Theme.ResourcesProvider resourcesProvider) {
        super(context);
        View view;
        View view2;
        Paint paint = new Paint();
        this.linePaint = paint;
        this.roundPath = new Path();
        this.roundRect = new RectF();
        paint.setStyle(Paint.Style.STROKE);
        this.resourcesProvider = resourcesProvider;
        TextView createTextView = createTextView(LocaleController.getString(R.string.BoostingFrom), false);
        this.fromNameTextView = createTextView;
        TextView createTextView2 = createTextView(LocaleController.getString(R.string.BoostingTo), false);
        this.toNameTextView = createTextView2;
        TextView createTextView3 = createTextView(LocaleController.getString(R.string.BoostingGift), false);
        this.giftNameTextView = createTextView3;
        TextView createTextView4 = createTextView(LocaleController.getString(R.string.BoostingReason), false);
        this.reasonNameTextView = createTextView4;
        TextView createTextView5 = createTextView(LocaleController.getString(R.string.BoostingDate), false);
        this.dateNameTextView = createTextView5;
        TextView createTextView6 = createTextView(true);
        this.fromTextView = createTextView6;
        TextView createTextView7 = createTextView(true);
        this.toTextView = createTextView7;
        TextView createTextView8 = createTextView(false);
        this.giftTextView = createTextView8;
        TextView createTextView9 = createTextView(true);
        this.reasonTextView = createTextView9;
        TextView createTextView10 = createTextView(false);
        this.dateTextView = createTextView10;
        BackupImageView backupImageView = new BackupImageView(context);
        this.fromImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(12.0f));
        BackupImageView backupImageView2 = new BackupImageView(context);
        this.toImageView = backupImageView2;
        backupImageView2.setRoundRadius(AndroidUtilities.dp(12.0f));
        TableRow tableRow = new TableRow(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.fromFrameLayout = frameLayout;
        boolean z = LocaleController.isRTL;
        frameLayout.addView(backupImageView, LayoutHelper.createFrame(24, 24.0f, z ? 5 : 3, z ? 0.0f : 12.0f, 0.0f, z ? 12.0f : 0.0f, 0.0f));
        FrameLayout frameLayout2 = this.fromFrameLayout;
        boolean z2 = LocaleController.isRTL;
        frameLayout2.addView(createTextView6, LayoutHelper.createFrame(-2, -2.0f, (z2 ? 5 : 3) | 16, z2 ? 0.0f : 29.0f, 0.0f, z2 ? 29.0f : 0.0f, 0.0f));
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(-2, -2, LocaleController.isRTL ? 1.0f : 0.0f);
        layoutParams.gravity = 16;
        if (LocaleController.isRTL) {
            tableRow.addView(this.fromFrameLayout, layoutParams);
            layoutParams = new TableRow.LayoutParams(-2, -2);
            view = createTextView;
        } else {
            tableRow.addView(createTextView, new TableRow.LayoutParams(-2, -2));
            view = this.fromFrameLayout;
        }
        tableRow.addView(view, layoutParams);
        this.fromFrameLayout.setPadding(0, AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f));
        TableRow tableRow2 = new TableRow(context);
        FrameLayout frameLayout3 = new FrameLayout(context);
        this.toFrameLayout = frameLayout3;
        boolean z3 = LocaleController.isRTL;
        frameLayout3.addView(backupImageView2, LayoutHelper.createFrame(24, 24.0f, z3 ? 5 : 3, z3 ? 0.0f : 12.0f, 0.0f, z3 ? 12.0f : 0.0f, 0.0f));
        FrameLayout frameLayout4 = this.toFrameLayout;
        boolean z4 = LocaleController.isRTL;
        frameLayout4.addView(createTextView7, LayoutHelper.createFrame(-2, -2.0f, (z4 ? 5 : 3) | 16, z4 ? 0.0f : 29.0f, 0.0f, z4 ? 29.0f : 0.0f, 0.0f));
        TableRow.LayoutParams layoutParams2 = new TableRow.LayoutParams(-2, -2, LocaleController.isRTL ? 1.0f : 0.0f);
        layoutParams2.gravity = 16;
        if (LocaleController.isRTL) {
            tableRow2.addView(this.toFrameLayout, layoutParams2);
            layoutParams2 = new TableRow.LayoutParams(-2, -2);
            view2 = createTextView2;
        } else {
            tableRow2.addView(createTextView2, new TableRow.LayoutParams(-2, -2));
            view2 = this.toFrameLayout;
        }
        tableRow2.addView(view2, layoutParams2);
        this.toFrameLayout.setPadding(0, AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f));
        TableRow tableRow3 = new TableRow(context);
        if (LocaleController.isRTL) {
            tableRow3.addView(createTextView8, new TableRow.LayoutParams(-2, -2, 1.0f));
            tableRow3.addView(createTextView3, new TableRow.LayoutParams(-2, -2));
        } else {
            tableRow3.addView(createTextView3, new TableRow.LayoutParams(-2, -2));
            tableRow3.addView(createTextView8, new TableRow.LayoutParams(-2, -2));
        }
        TableRow tableRow4 = new TableRow(context);
        this.tableRow4 = tableRow4;
        if (LocaleController.isRTL) {
            tableRow4.addView(createTextView9, new TableRow.LayoutParams(-2, -2, 1.0f));
            this.tableRow4.addView(createTextView4, new TableRow.LayoutParams(-2, -2));
        } else {
            tableRow4.addView(createTextView4, new TableRow.LayoutParams(-2, -2));
            this.tableRow4.addView(createTextView9, new TableRow.LayoutParams(-2, -2));
        }
        TableRow tableRow5 = new TableRow(context);
        if (LocaleController.isRTL) {
            tableRow5.addView(createTextView10, new TableRow.LayoutParams(-2, -2, 1.0f));
            tableRow5.addView(createTextView5, new TableRow.LayoutParams(-2, -2));
        } else {
            tableRow5.addView(createTextView5, new TableRow.LayoutParams(-2, -2));
            tableRow5.addView(createTextView10, new TableRow.LayoutParams(-2, -2));
        }
        TableLayout tableLayout = new TableLayout(context) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                TableCell.this.roundRect.set(0.0f, 0.0f, getWidth(), getHeight());
                TableCell.this.roundPath.rewind();
                TableCell.this.roundPath.addRoundRect(TableCell.this.roundRect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Path.Direction.CW);
                if (Build.VERSION.SDK_INT < 21) {
                    canvas.clipPath(TableCell.this.roundPath);
                }
                super.dispatchDraw(canvas);
                TableCell.this.linePaint.setColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_divider, resourcesProvider), -1, 0.1f));
                TableCell.this.linePaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
                float height = getHeight() / (TableCell.this.tableRow4.getVisibility() == 0 ? 5.0f : 4.0f);
                for (int i = 1; i <= 4; i++) {
                    float f = height * i;
                    canvas.drawLine(0.0f, f, getWidth(), f, TableCell.this.linePaint);
                }
                float right = LocaleController.isRTL ? TableCell.this.dateTextView.getRight() : TableCell.this.dateTextView.getLeft();
                canvas.drawLine(right, 0.0f, right, getHeight(), TableCell.this.linePaint);
                TableCell.this.linePaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
                canvas.drawPath(TableCell.this.roundPath, TableCell.this.linePaint);
            }
        };
        tableLayout.addView(tableRow);
        tableLayout.addView(tableRow2);
        tableLayout.addView(tableRow3);
        tableLayout.addView(this.tableRow4);
        tableLayout.addView(tableRow5);
        if (LocaleController.isRTL) {
            tableLayout.setColumnShrinkable(0, true);
        } else {
            tableLayout.setColumnShrinkable(1, true);
        }
        addView(tableLayout, LayoutHelper.createFrame(-1, -2.0f));
        if (Build.VERSION.SDK_INT >= 21) {
            tableLayout.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view3, Outline outline) {
                    outline.setRoundRect(0, 0, view3.getMeasuredWidth(), view3.getMeasuredHeight(), AndroidUtilities.dp(6.0f));
                }
            });
            tableLayout.setClipToOutline(true);
        }
        setPaddingRelative(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(18.0f), AndroidUtilities.dp(14.0f), 0);
    }

    private TextView createTextView(String str, boolean z) {
        TextView textView;
        if (z) {
            textView = new LinkSpanDrawable.LinksTextView(getContext(), this.resourcesProvider);
            textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, this.resourcesProvider));
        } else {
            textView = new TextView(getContext());
        }
        textView.setTextColor(Theme.getColor(z ? Theme.key_dialogTextBlue : Theme.key_dialogTextBlack, this.resourcesProvider));
        textView.setTextSize(1, 14.0f);
        if (!z) {
            textView.setGravity(LocaleController.isRTL ? 5 : 3);
        }
        if (str != null) {
            textView.setTypeface(AndroidUtilities.bold());
            textView.setText(str);
            textView.setBackgroundColor(Theme.getColor(Theme.key_graySection, this.resourcesProvider));
            textView.setPadding(AndroidUtilities.dp(LocaleController.isRTL ? 32.0f : 12.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(LocaleController.isRTL ? 12.0f : 32.0f), AndroidUtilities.dp(11.0f));
        } else {
            textView.setSingleLine(true);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
        }
        return textView;
    }

    private TextView createTextView(boolean z) {
        return createTextView(null, z);
    }

    public void setData(final TLRPC.TL_payments_checkedGiftCode tL_payments_checkedGiftCode, final Utilities.Callback callback) {
        TextView textView;
        View.OnClickListener onClickListener;
        FrameLayout frameLayout;
        View.OnClickListener onClickListener2;
        this.giftCode = tL_payments_checkedGiftCode;
        Date date = new Date(tL_payments_checkedGiftCode.date * 1000);
        this.dateTextView.setText(LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().getFormatterYear().format(date), LocaleController.getInstance().getFormatterDay().format(date)));
        this.reasonTextView.setTextColor(Theme.getColor(tL_payments_checkedGiftCode.via_giveaway ? Theme.key_dialogTextBlue : Theme.key_dialogTextBlack, this.resourcesProvider));
        final TLRPC.Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(-DialogObject.getPeerDialogId(tL_payments_checkedGiftCode.from_id)));
        boolean isChannelAndNotMegaGroup = ChatObject.isChannelAndNotMegaGroup(chat);
        if (tL_payments_checkedGiftCode.via_giveaway) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append((CharSequence) "**");
            spannableStringBuilder.append((CharSequence) LocaleController.getString(R.string.BoostingGiveaway));
            spannableStringBuilder.append((CharSequence) "**");
            this.reasonTextView.setText(AndroidUtilities.replaceSingleTag(spannableStringBuilder.toString(), Theme.key_chat_messageLinkIn, 0, new Runnable() {
                @Override
                public final void run() {
                    Utilities.Callback.this.run(tL_payments_checkedGiftCode);
                }
            }, this.resourcesProvider));
            textView = this.reasonTextView;
            onClickListener = new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    Utilities.Callback.this.run(tL_payments_checkedGiftCode);
                }
            };
        } else {
            this.reasonTextView.setText(LocaleController.getString(isChannelAndNotMegaGroup ? R.string.BoostingYouWereSelected : R.string.BoostingYouWereSelectedGroup));
            textView = this.reasonTextView;
            onClickListener = null;
        }
        textView.setOnClickListener(onClickListener);
        int i = tL_payments_checkedGiftCode.months;
        this.giftTextView.setText(LocaleController.formatString("BoostingTelegramPremiumFor", R.string.BoostingTelegramPremiumFor, i == 12 ? LocaleController.formatPluralString("Years", 1, new Object[0]) : LocaleController.formatPluralString("Months", i, new Object[0])));
        if (chat != null) {
            SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder();
            spannableStringBuilder2.append((CharSequence) "**");
            spannableStringBuilder2.append((CharSequence) chat.title);
            spannableStringBuilder2.append((CharSequence) "**");
            SpannableStringBuilder replaceSingleTag = AndroidUtilities.replaceSingleTag(spannableStringBuilder2.toString(), Theme.key_chat_messageLinkIn, 0, new Runnable() {
                @Override
                public final void run() {
                    Utilities.Callback.this.run(chat);
                }
            }, this.resourcesProvider);
            TextView textView2 = this.fromTextView;
            textView2.setText(Emoji.replaceEmoji((CharSequence) replaceSingleTag, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(12.0f), false));
            this.fromImageView.setForUserOrChat(chat, new AvatarDrawable(chat));
            frameLayout = this.fromFrameLayout;
            onClickListener2 = new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    Utilities.Callback.this.run(chat);
                }
            };
        } else {
            final TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(tL_payments_checkedGiftCode.from_id.user_id));
            this.fromTextView.setText(Emoji.replaceEmoji((CharSequence) UserObject.getFirstName(user), this.fromTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(12.0f), false));
            this.fromImageView.setForUserOrChat(user, new AvatarDrawable(user));
            frameLayout = this.fromFrameLayout;
            onClickListener2 = new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    Utilities.Callback.this.run(user);
                }
            };
        }
        frameLayout.setOnClickListener(onClickListener2);
        if (tL_payments_checkedGiftCode.to_id == -1 && tL_payments_checkedGiftCode.via_giveaway) {
            SpannableStringBuilder spannableStringBuilder3 = new SpannableStringBuilder();
            spannableStringBuilder3.append((CharSequence) "**");
            spannableStringBuilder3.append((CharSequence) LocaleController.getString(R.string.BoostingIncompleteGiveaway));
            spannableStringBuilder3.append((CharSequence) "**");
            this.reasonTextView.setText(AndroidUtilities.replaceSingleTag(spannableStringBuilder3.toString(), Theme.key_chat_messageLinkIn, 0, new Runnable() {
                @Override
                public final void run() {
                    Utilities.Callback.this.run(tL_payments_checkedGiftCode);
                }
            }, this.resourcesProvider));
            this.toTextView.setText(LocaleController.getString(R.string.BoostingNoRecipient));
            this.toTextView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, this.resourcesProvider));
            ((ViewGroup.MarginLayoutParams) this.toTextView.getLayoutParams()).leftMargin = 0;
            ((ViewGroup.MarginLayoutParams) this.toTextView.getLayoutParams()).rightMargin = 0;
            this.toImageView.setVisibility(8);
        } else {
            final TLRPC.User user2 = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(tL_payments_checkedGiftCode.to_id));
            if (user2 != null) {
                SpannableStringBuilder spannableStringBuilder4 = new SpannableStringBuilder();
                spannableStringBuilder4.append((CharSequence) "**");
                spannableStringBuilder4.append((CharSequence) UserObject.getFirstName(user2));
                spannableStringBuilder4.append((CharSequence) "**");
                SpannableStringBuilder replaceSingleTag2 = AndroidUtilities.replaceSingleTag(spannableStringBuilder4.toString(), Theme.key_chat_messageLinkIn, 0, new Runnable() {
                    @Override
                    public final void run() {
                        Utilities.Callback.this.run(user2);
                    }
                }, this.resourcesProvider);
                TextView textView3 = this.toTextView;
                textView3.setText(Emoji.replaceEmoji((CharSequence) replaceSingleTag2, textView3.getPaint().getFontMetricsInt(), AndroidUtilities.dp(12.0f), false));
                this.toImageView.setForUserOrChat(user2, new AvatarDrawable(user2));
                this.toFrameLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        Utilities.Callback.this.run(user2);
                    }
                });
            }
        }
        if (tL_payments_checkedGiftCode.boost != null) {
            this.tableRow4.setVisibility(8);
        }
    }
}
