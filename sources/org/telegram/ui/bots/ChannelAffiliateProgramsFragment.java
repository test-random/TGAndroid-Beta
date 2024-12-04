package org.telegram.ui.bots;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ShapeDrawable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_payments;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.GLIcon.GLIconRenderer;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.TableView;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.FilterCreateActivity;
import org.telegram.ui.GradientHeaderActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stars.BotStarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.bots.AffiliateProgramFragment;
import org.telegram.ui.bots.ChannelAffiliateProgramsFragment;

public class ChannelAffiliateProgramsFragment extends GradientHeaderActivity implements NotificationCenter.NotificationCenterDelegate {
    private FrameLayout aboveTitleView;
    private UniversalAdapter adapter;
    public final long dialogId;
    private View emptyLayout;
    private GLIconTextureView iconTextureView;

    public class AnonymousClass4 extends ClickableSpan {
        final BotStarsController.ChannelSuggestedBots.Sort val$sort;
        final BotStarsController.ChannelSuggestedBots val$suggestedBots;

        AnonymousClass4(BotStarsController.ChannelSuggestedBots.Sort sort, BotStarsController.ChannelSuggestedBots channelSuggestedBots) {
            this.val$sort = sort;
            this.val$suggestedBots = channelSuggestedBots;
        }

        public static void lambda$onClick$0(BotStarsController.ChannelSuggestedBots channelSuggestedBots) {
            channelSuggestedBots.setSort(BotStarsController.ChannelSuggestedBots.Sort.BY_DATE);
        }

        public static void lambda$onClick$1(BotStarsController.ChannelSuggestedBots channelSuggestedBots) {
            channelSuggestedBots.setSort(BotStarsController.ChannelSuggestedBots.Sort.BY_REVENUE);
        }

        public static void lambda$onClick$2(BotStarsController.ChannelSuggestedBots channelSuggestedBots) {
            channelSuggestedBots.setSort(BotStarsController.ChannelSuggestedBots.Sort.BY_PROFITABILITY);
        }

        @Override
        public void onClick(View view) {
            ItemOptions makeOptions = ItemOptions.makeOptions(ChannelAffiliateProgramsFragment.this, view);
            boolean z = this.val$sort == BotStarsController.ChannelSuggestedBots.Sort.BY_DATE;
            String string = LocaleController.getString(R.string.ChannelAffiliateProgramProgramsSortDate);
            final BotStarsController.ChannelSuggestedBots channelSuggestedBots = this.val$suggestedBots;
            ItemOptions addChecked = makeOptions.addChecked(z, string, new Runnable() {
                @Override
                public final void run() {
                    ChannelAffiliateProgramsFragment.AnonymousClass4.lambda$onClick$0(BotStarsController.ChannelSuggestedBots.this);
                }
            });
            boolean z2 = this.val$sort == BotStarsController.ChannelSuggestedBots.Sort.BY_REVENUE;
            String string2 = LocaleController.getString(R.string.ChannelAffiliateProgramProgramsSortRevenue);
            final BotStarsController.ChannelSuggestedBots channelSuggestedBots2 = this.val$suggestedBots;
            ItemOptions addChecked2 = addChecked.addChecked(z2, string2, new Runnable() {
                @Override
                public final void run() {
                    ChannelAffiliateProgramsFragment.AnonymousClass4.lambda$onClick$1(BotStarsController.ChannelSuggestedBots.this);
                }
            });
            boolean z3 = this.val$sort == BotStarsController.ChannelSuggestedBots.Sort.BY_PROFITABILITY;
            String string3 = LocaleController.getString(R.string.ChannelAffiliateProgramProgramsSortProfitability);
            final BotStarsController.ChannelSuggestedBots channelSuggestedBots3 = this.val$suggestedBots;
            addChecked2.addChecked(z3, string3, new Runnable() {
                @Override
                public final void run() {
                    ChannelAffiliateProgramsFragment.AnonymousClass4.lambda$onClick$2(BotStarsController.ChannelSuggestedBots.this);
                }
            }).setGravity(5).setDrawScrim(false).setDimAlpha(0).translate(AndroidUtilities.dp(24.0f), -AndroidUtilities.dp(24.0f)).show();
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            textPaint.setUnderlineText(false);
            textPaint.setColor(textPaint.linkColor);
        }
    }

    public static class BotCell extends FrameLayout {
        private final ImageView arrowView;
        private final int currentAccount;
        private final BackupImageView imageView;
        private final View linkBgView;
        private final View linkFg2View;
        private final ImageView linkFgView;
        private final Theme.ResourcesProvider resourcesProvider;
        private final LinearLayout textLayout;
        private final TextView textView;
        private final TextView titleView;

        public static class Factory extends UItem.UItemFactory {
            static {
                UItem.UItemFactory.setup(new Factory());
            }

            public static UItem as(Object obj) {
                return as(obj, true);
            }

            public static UItem as(Object obj, boolean z) {
                UItem ofFactory = UItem.ofFactory(Factory.class);
                ofFactory.object = obj;
                ofFactory.red = z;
                return ofFactory;
            }

            @Override
            public void bindView(View view, UItem uItem, boolean z) {
                Object obj = uItem.object;
                if (obj instanceof TL_payments.connectedBotStarRef) {
                    ((BotCell) view).set((TL_payments.connectedBotStarRef) obj, uItem.red);
                } else if (obj instanceof TL_payments.starRefProgram) {
                    ((BotCell) view).set((TL_payments.starRefProgram) obj, uItem.red);
                }
            }

            @Override
            public BotCell createView(Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
                return new BotCell(context, i, resourcesProvider);
            }
        }

        public BotCell(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.currentAccount = i;
            this.resourcesProvider = resourcesProvider;
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            backupImageView.setRoundRadius(AndroidUtilities.dp(46.0f));
            addView(backupImageView, LayoutHelper.createFrame(46, 46.0f, 19, 13.0f, 0.0f, 13.0f, 0.0f));
            View view = new View(context);
            this.linkBgView = view;
            view.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(11.0f), Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider)));
            addView(view, LayoutHelper.createFrame(22, 22.0f, 19, 40.0f, 15.0f, 0.0f, 0.0f));
            View view2 = new View(context);
            this.linkFg2View = view2;
            view2.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(9.665f), Theme.getColor(Theme.key_color_green, resourcesProvider)));
            addView(view2, LayoutHelper.createFrame(19.33f, 19.33f, 19, 41.33f, 15.0f, 0.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.linkFgView = imageView;
            imageView.setScaleX(0.6f);
            imageView.setScaleY(0.6f);
            addView(imageView, LayoutHelper.createFrame(19.33f, 19.33f, 19, 41.33f, 15.0f, 0.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            this.textLayout = linearLayout;
            linearLayout.setOrientation(1);
            addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 55, 66.0f, 8.66f, 10.0f, 0.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
            textView.setEllipsize(truncateAt);
            textView.setTypeface(AndroidUtilities.bold());
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider));
            NotificationCenter.listenEmojiLoading(textView);
            linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 55, 6, 0, 24, 0));
            TextView textView2 = new TextView(context);
            this.textView = textView2;
            textView2.setMaxLines(1);
            textView2.setSingleLine(true);
            textView2.setEllipsize(truncateAt);
            textView2.setTextSize(1, 14.0f);
            int i2 = Theme.key_windowBackgroundWhiteGrayText2;
            textView2.setTextColor(Theme.getColor(i2, resourcesProvider));
            linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 55, 6, 1, 24, 0));
            ImageView imageView2 = new ImageView(context);
            this.arrowView = imageView2;
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2, resourcesProvider), PorterDuff.Mode.SRC_IN));
            imageView2.setImageResource(R.drawable.photos_arrow);
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            addView(imageView2, LayoutHelper.createFrame(24, 24.0f, 21, 0.0f, 0.0f, 10.0f, 0.0f));
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(58.0f), 1073741824));
        }

        public void set(TL_payments.connectedBotStarRef connectedbotstarref, boolean z) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(connectedbotstarref.bot_id));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(user);
            this.imageView.setForUserOrChat(user, avatarDrawable);
            this.titleView.setText(Emoji.replaceEmoji(UserObject.getUserName(user), this.titleView.getPaint().getFontMetricsInt(), false));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (connectedbotstarref.commission_permille > 0) {
                spannableStringBuilder.append((CharSequence) " d");
                FilterCreateActivity.NewSpan newSpan = new FilterCreateActivity.NewSpan(10.0f);
                newSpan.setColor(Theme.getColor(Theme.key_color_green));
                newSpan.setText(AffiliateProgramFragment.percents(connectedbotstarref.commission_permille));
                spannableStringBuilder.setSpan(newSpan, 1, 2, 33);
            }
            int i = connectedbotstarref.duration_months;
            spannableStringBuilder.append((CharSequence) (i == 0 ? LocaleController.getString(R.string.Lifetime) : (i < 12 || i % 12 != 0) ? LocaleController.formatPluralString("Months", i, new Object[0]) : LocaleController.formatPluralString("Years", i / 12, new Object[0])));
            this.textView.setText(spannableStringBuilder);
            this.arrowView.setVisibility(z ? 0 : 8);
            this.linkBgView.setVisibility(0);
            this.linkFgView.setVisibility(0);
            this.linkFg2View.setVisibility(0);
            this.linkFg2View.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(9.665f), Theme.getColor(connectedbotstarref.revoked ? Theme.key_color_red : Theme.key_color_green, this.resourcesProvider)));
            this.linkFgView.setImageResource(connectedbotstarref.revoked ? R.drawable.msg_link_2 : R.drawable.msg_limit_links);
            this.linkFgView.setScaleX(connectedbotstarref.revoked ? 0.8f : 0.6f);
            this.linkFgView.setScaleY(connectedbotstarref.revoked ? 0.8f : 0.6f);
        }

        public void set(TL_payments.starRefProgram starrefprogram, boolean z) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(starrefprogram.bot_id));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(user);
            this.imageView.setForUserOrChat(user, avatarDrawable);
            this.titleView.setText(UserObject.getUserName(user));
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            if (starrefprogram.commission_permille > 0) {
                spannableStringBuilder.append((CharSequence) " d");
                FilterCreateActivity.NewSpan newSpan = new FilterCreateActivity.NewSpan(10.0f);
                newSpan.setColor(Theme.getColor(Theme.key_color_green));
                newSpan.setText(AffiliateProgramFragment.percents(starrefprogram.commission_permille));
                spannableStringBuilder.setSpan(newSpan, 1, 2, 33);
            }
            int i = starrefprogram.duration_months;
            spannableStringBuilder.append((CharSequence) (i == 0 ? LocaleController.getString(R.string.Lifetime) : (i < 12 || i % 12 != 0) ? LocaleController.formatPluralString("Months", i, new Object[0]) : LocaleController.formatPluralString("Years", i / 12, new Object[0])));
            this.textView.setText(spannableStringBuilder);
            this.arrowView.setVisibility(z ? 0 : 8);
            this.linkBgView.setVisibility(8);
            this.linkFgView.setVisibility(8);
            this.linkFg2View.setVisibility(8);
        }
    }

    public static class HeaderSortCell extends HeaderCell {
        private final LinkSpanDrawable.LinksTextView subtextView;

        public static class Factory extends UItem.UItemFactory {
            static {
                UItem.UItemFactory.setup(new Factory());
            }

            public static UItem as(CharSequence charSequence, CharSequence charSequence2) {
                UItem ofFactory = UItem.ofFactory(Factory.class);
                ofFactory.text = charSequence;
                ofFactory.subtext = charSequence2;
                return ofFactory;
            }

            @Override
            public void bindView(View view, UItem uItem, boolean z) {
                ((HeaderSortCell) view).set(uItem.text, uItem.subtext);
            }

            @Override
            public HeaderSortCell createView(Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
                return new HeaderSortCell(context, resourcesProvider);
            }

            @Override
            public boolean isClickable() {
                return false;
            }
        }

        public HeaderSortCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
            this.subtextView = linksTextView;
            linksTextView.setTextSize(1, 14.0f);
            linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider));
            linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
            linksTextView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            addView(linksTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : 5) | 48, 10.0f, 20.0f, 10.0f, 0.0f));
        }

        public void set(CharSequence charSequence, CharSequence charSequence2) {
            setText(charSequence);
            this.subtextView.setText(charSequence2);
        }
    }

    public ChannelAffiliateProgramsFragment(long j) {
        this.dialogId = j;
        setWhiteBackground(true);
        setMinusHeaderHeight(AndroidUtilities.dp(60.0f));
    }

    public boolean isLoadingVisible() {
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            if (this.listView.getChildAt(i) instanceof FlickerLoadingView) {
                return true;
            }
        }
        return false;
    }

    public void lambda$createView$0(Context context, View view, int i) {
        UniversalAdapter universalAdapter = this.adapter;
        if (universalAdapter == null) {
            return;
        }
        Object obj = universalAdapter.getItem(i).object;
        if (obj instanceof TL_payments.starRefProgram) {
            showConnectAffiliateAlert(context, this.currentAccount, (TL_payments.starRefProgram) obj, this.dialogId, this.resourceProvider, false);
        } else if (obj instanceof TL_payments.connectedBotStarRef) {
            showShareAffiliateAlert(context, this.currentAccount, (TL_payments.connectedBotStarRef) obj, this.dialogId, this.resourceProvider);
        }
    }

    public void lambda$createView$1(TLRPC.User user) {
        getMessagesController().openApp(user, getClassGuid());
    }

    public void lambda$createView$2(TL_payments.connectedBotStarRef connectedbotstarref) {
        presentFragment(ChatActivity.of(connectedbotstarref.bot_id));
    }

    public void lambda$createView$3(TL_payments.connectedBotStarRef connectedbotstarref, TLRPC.User user) {
        AndroidUtilities.addToClipboard(connectedbotstarref.url);
        BulletinFactory.of(this).createSimpleBulletin(R.raw.copy, LocaleController.getString(R.string.AffiliateProgramLinkCopiedTitle), AndroidUtilities.replaceTags(LocaleController.formatString(R.string.AffiliateProgramLinkCopiedText, AffiliateProgramFragment.percents(connectedbotstarref.commission_permille), UserObject.getUserName(user)))).show();
    }

    public void lambda$createView$4(TLObject tLObject, AlertDialog alertDialog) {
        if (tLObject instanceof TL_payments.connectedStarRefBots) {
            BotStarsController.getInstance(this.currentAccount).getChannelConnectedBots(this.dialogId).applyEdit((TL_payments.connectedStarRefBots) tLObject);
            BotStarsController.getInstance(this.currentAccount).getChannelSuggestedBots(this.dialogId).reload();
            this.adapter.update(true);
        }
        alertDialog.dismiss();
    }

    public void lambda$createView$5(final AlertDialog alertDialog, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelAffiliateProgramsFragment.this.lambda$createView$4(tLObject, alertDialog);
            }
        });
    }

    public void lambda$createView$6(TL_payments.connectedBotStarRef connectedbotstarref, DialogInterface dialogInterface, int i) {
        final AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3);
        alertDialog.showDelayed(200L);
        TL_payments.editConnectedStarRefBot editconnectedstarrefbot = new TL_payments.editConnectedStarRefBot();
        editconnectedstarrefbot.link = connectedbotstarref.url;
        editconnectedstarrefbot.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
        editconnectedstarrefbot.revoked = true;
        getConnectionsManager().sendRequest(editconnectedstarrefbot, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelAffiliateProgramsFragment.this.lambda$createView$5(alertDialog, tLObject, tL_error);
            }
        });
    }

    public void lambda$createView$7(Context context, TLRPC.User user, final TL_payments.connectedBotStarRef connectedbotstarref) {
        new AlertDialog.Builder(context, this.resourceProvider).setTitle(LocaleController.getString(R.string.LeaveAffiliateLink)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.LeaveAffiliateLinkAlert, UserObject.getUserName(user)))).setPositiveButton(LocaleController.getString(R.string.LeaveAffiliateLinkButton), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChannelAffiliateProgramsFragment.this.lambda$createView$6(connectedbotstarref, dialogInterface, i);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).makeRed(-1).show();
    }

    public boolean lambda$createView$8(final Context context, View view, int i) {
        UniversalAdapter universalAdapter = this.adapter;
        if (universalAdapter == null) {
            return false;
        }
        Object obj = universalAdapter.getItem(i).object;
        if (!(obj instanceof TL_payments.connectedBotStarRef)) {
            return false;
        }
        final TL_payments.connectedBotStarRef connectedbotstarref = (TL_payments.connectedBotStarRef) obj;
        final TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(connectedbotstarref.bot_id));
        ItemOptions makeOptions = ItemOptions.makeOptions(this, view);
        boolean z = user.bot_has_main_app;
        int i2 = R.drawable.msg_bot;
        makeOptions.addIf(z, i2, LocaleController.getString(R.string.ProfileBotOpenApp), new Runnable() {
            @Override
            public final void run() {
                ChannelAffiliateProgramsFragment.this.lambda$createView$1(user);
            }
        }).addIf(!user.bot_has_main_app, i2, LocaleController.getString(R.string.BotWebViewOpenBot), new Runnable() {
            @Override
            public final void run() {
                ChannelAffiliateProgramsFragment.this.lambda$createView$2(connectedbotstarref);
            }
        }).add(R.drawable.msg_link2, LocaleController.getString(R.string.CopyLink), new Runnable() {
            @Override
            public final void run() {
                ChannelAffiliateProgramsFragment.this.lambda$createView$3(connectedbotstarref, user);
            }
        }).addIf(!connectedbotstarref.revoked, R.drawable.msg_leave, (CharSequence) LocaleController.getString(R.string.LeaveAffiliateLinkButton), true, new Runnable() {
            @Override
            public final void run() {
                ChannelAffiliateProgramsFragment.this.lambda$createView$7(context, user, connectedbotstarref);
            }
        }).setGravity(5).show();
        return true;
    }

    public static void lambda$showConnectAffiliateAlert$10(ButtonWithCounterView buttonWithCounterView, TLObject tLObject, int i, long j, BottomSheet bottomSheet, TL_payments.starRefProgram starrefprogram, long j2, boolean z, Context context, Theme.ResourcesProvider resourcesProvider, TLRPC.User user, TLRPC.TL_error tL_error) {
        TL_payments.connectedBotStarRef connectedbotstarref;
        BaseFragment safeLastFragment;
        int i2 = 0;
        buttonWithCounterView.setLoading(false);
        if (!(tLObject instanceof TL_payments.connectedStarRefBots)) {
            if (tL_error != null) {
                BulletinFactory.of(bottomSheet.topBulletinContainer, resourcesProvider).showForError(tL_error);
                return;
            }
            return;
        }
        TL_payments.connectedStarRefBots connectedstarrefbots = (TL_payments.connectedStarRefBots) tLObject;
        BotStarsController.getInstance(i).getChannelConnectedBots(j).apply(connectedstarrefbots);
        bottomSheet.dismiss();
        while (true) {
            if (i2 >= connectedstarrefbots.connected_bots.size()) {
                connectedbotstarref = null;
                break;
            }
            connectedbotstarref = connectedstarrefbots.connected_bots.get(i2);
            if (connectedbotstarref.bot_id == starrefprogram.bot_id) {
                break;
            } else {
                i2++;
            }
        }
        if ((j2 != j || z) && (safeLastFragment = LaunchActivity.getSafeLastFragment()) != null && (!(safeLastFragment instanceof ChannelAffiliateProgramsFragment) || ((ChannelAffiliateProgramsFragment) safeLastFragment).dialogId != j)) {
            safeLastFragment.presentFragment(new ChannelAffiliateProgramsFragment(j));
        }
        if (connectedbotstarref != null) {
            BotStarsController.getInstance(i).getChannelSuggestedBots(j).remove(connectedbotstarref.bot_id);
            BulletinFactory.of(showShareAffiliateAlert(context, i, connectedbotstarref, j, resourcesProvider).topBulletinContainer, resourcesProvider).createUsersBulletin(user, LocaleController.getString(R.string.AffiliateProgramJoinedTitle), LocaleController.getString(R.string.AffiliateProgramJoinedText)).show();
        }
    }

    public static void lambda$showConnectAffiliateAlert$11(final ButtonWithCounterView buttonWithCounterView, final int i, final long j, final BottomSheet bottomSheet, final TL_payments.starRefProgram starrefprogram, final long j2, final boolean z, final Context context, final Theme.ResourcesProvider resourcesProvider, final TLRPC.User user, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelAffiliateProgramsFragment.lambda$showConnectAffiliateAlert$10(ButtonWithCounterView.this, tLObject, i, j, bottomSheet, starrefprogram, j2, z, context, resourcesProvider, user, tL_error);
            }
        });
    }

    public static void lambda$showConnectAffiliateAlert$12(final ButtonWithCounterView buttonWithCounterView, long[] jArr, final int i, final TL_payments.starRefProgram starrefprogram, final BottomSheet bottomSheet, final long j, final boolean z, final Context context, final Theme.ResourcesProvider resourcesProvider, final TLRPC.User user, View view) {
        if (buttonWithCounterView.isLoading()) {
            return;
        }
        buttonWithCounterView.setLoading(true);
        final long j2 = jArr[0];
        TL_payments.connectStarRefBot connectstarrefbot = new TL_payments.connectStarRefBot();
        connectstarrefbot.bot = MessagesController.getInstance(i).getInputUser(starrefprogram.bot_id);
        connectstarrefbot.peer = MessagesController.getInstance(i).getInputPeer(j2);
        ConnectionsManager.getInstance(i).sendRequest(connectstarrefbot, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChannelAffiliateProgramsFragment.lambda$showConnectAffiliateAlert$11(ButtonWithCounterView.this, i, j2, bottomSheet, starrefprogram, j, z, context, resourcesProvider, user, tLObject, tL_error);
            }
        });
    }

    public static void lambda$showConnectAffiliateAlert$13(DialogInterface dialogInterface) {
    }

    public static void lambda$showConnectAffiliateAlert$14(long[] jArr, int i, BackupImageView backupImageView, BackupImageView backupImageView2, TextView textView) {
        AvatarDrawable avatarDrawable;
        TLObject tLObject;
        String str;
        long j = jArr[0];
        MessagesController messagesController = MessagesController.getInstance(i);
        if (j >= 0) {
            TLRPC.User user = messagesController.getUser(Long.valueOf(jArr[0]));
            AvatarDrawable avatarDrawable2 = new AvatarDrawable();
            avatarDrawable2.setInfo(user);
            tLObject = user;
            avatarDrawable = avatarDrawable2;
        } else {
            TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-jArr[0]));
            AvatarDrawable avatarDrawable3 = new AvatarDrawable();
            avatarDrawable3.setInfo(chat);
            tLObject = chat;
            avatarDrawable = avatarDrawable3;
        }
        backupImageView.setForUserOrChat(tLObject, avatarDrawable);
        long j2 = jArr[0];
        MessagesController messagesController2 = MessagesController.getInstance(i);
        if (j2 >= 0) {
            TLRPC.User user2 = messagesController2.getUser(Long.valueOf(jArr[0]));
            if (backupImageView2 != null) {
                AvatarDrawable avatarDrawable4 = new AvatarDrawable();
                avatarDrawable4.setInfo(user2);
                backupImageView2.setForUserOrChat(user2, avatarDrawable4);
            }
            if (textView == null) {
                return;
            } else {
                str = UserObject.getUserName(user2);
            }
        } else {
            TLRPC.Chat chat2 = messagesController2.getChat(Long.valueOf(-jArr[0]));
            if (backupImageView2 != null) {
                AvatarDrawable avatarDrawable5 = new AvatarDrawable();
                avatarDrawable5.setInfo(chat2);
                backupImageView2.setForUserOrChat(chat2, avatarDrawable5);
            }
            if (textView == null) {
                return;
            } else {
                str = chat2 == null ? "" : chat2.title;
            }
        }
        textView.setText(str);
    }

    public static void lambda$showConnectAffiliateAlert$15(long[] jArr, long j, Runnable runnable) {
        jArr[0] = j;
        runnable.run();
    }

    public static void lambda$showConnectAffiliateAlert$16(int i, BottomSheet bottomSheet, Theme.ResourcesProvider resourcesProvider, View view, final long[] jArr, final Runnable runnable, View view2) {
        final long j;
        ArrayList admined = BotStarsController.getInstance(i).getAdmined();
        admined.add(0, UserConfig.getInstance(i).getCurrentUser());
        ItemOptions makeOptions = ItemOptions.makeOptions(bottomSheet.getContainerView(), resourcesProvider, view);
        Iterator it = admined.iterator();
        while (it.hasNext()) {
            TLObject tLObject = (TLObject) it.next();
            if (tLObject instanceof TLRPC.User) {
                j = ((TLRPC.User) tLObject).id;
            } else if (tLObject instanceof TLRPC.Chat) {
                TLRPC.Chat chat = (TLRPC.Chat) tLObject;
                if (ChatObject.isChannelAndNotMegaGroup(chat)) {
                    j = -chat.id;
                }
            }
            makeOptions.addChat(tLObject, j == jArr[0], new Runnable() {
                @Override
                public final void run() {
                    ChannelAffiliateProgramsFragment.lambda$showConnectAffiliateAlert$15(jArr, j, runnable);
                }
            });
        }
        makeOptions.setDrawScrim(false).setDimAlpha(0).setGravity(5).translate(AndroidUtilities.dp(24.0f), 0.0f).show();
    }

    public static void lambda$showConnectAffiliateAlert$9(Context context) {
        Browser.openUrl(context, LocaleController.getString(R.string.ChannelAffiliateProgramJoinButtonInfoLink));
    }

    public static void lambda$showShareAffiliateAlert$17(TL_payments.connectedBotStarRef connectedbotstarref, BottomSheet bottomSheet, Theme.ResourcesProvider resourcesProvider, TLRPC.User user) {
        AndroidUtilities.addToClipboard(connectedbotstarref.url);
        BulletinFactory.of(bottomSheet.topBulletinContainer, resourcesProvider).createSimpleBulletin(R.raw.copy, LocaleController.getString(R.string.AffiliateProgramLinkCopiedTitle), AndroidUtilities.replaceTags(LocaleController.formatString(R.string.AffiliateProgramLinkCopiedText, AffiliateProgramFragment.percents(connectedbotstarref.commission_permille), UserObject.getUserName(user)))).show();
    }

    public static void lambda$showShareAffiliateAlert$19(TLRPC.UserFull userFull, BottomSheet bottomSheet, Context context, int i, long j, Theme.ResourcesProvider resourcesProvider) {
        if (userFull == null || userFull.starref_program == null) {
            return;
        }
        bottomSheet.dismiss();
        showConnectAffiliateAlert(context, i, userFull.starref_program, j, resourcesProvider, true);
    }

    public static void lambda$showShareAffiliateAlert$20(final BottomSheet bottomSheet, final Context context, final int i, final long j, final Theme.ResourcesProvider resourcesProvider, final TLRPC.UserFull userFull) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelAffiliateProgramsFragment.lambda$showShareAffiliateAlert$19(TLRPC.UserFull.this, bottomSheet, context, i, j, resourcesProvider);
            }
        });
    }

    public static void lambda$showShareAffiliateAlert$21(TL_payments.connectedBotStarRef connectedbotstarref, final int i, final BottomSheet bottomSheet, final Context context, final long j, final Theme.ResourcesProvider resourcesProvider, Runnable runnable, View view) {
        if (!connectedbotstarref.revoked) {
            runnable.run();
            return;
        }
        TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(connectedbotstarref.bot_id));
        if (user != null) {
            MessagesController.getInstance(i).loadFullUser(user, 0, true, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    ChannelAffiliateProgramsFragment.lambda$showShareAffiliateAlert$20(BottomSheet.this, context, i, j, resourcesProvider, (TLRPC.UserFull) obj);
                }
            });
        }
    }

    public static void lambda$showShareAffiliateAlert$22(DialogInterface dialogInterface) {
    }

    public static void lambda$showShareAffiliateAlert$23(TLRPC.UserFull userFull, BottomSheet bottomSheet, Context context, int i, long j, Theme.ResourcesProvider resourcesProvider) {
        if (userFull == null || userFull.starref_program == null) {
            return;
        }
        bottomSheet.dismiss();
        showConnectAffiliateAlert(context, i, userFull.starref_program, j, resourcesProvider, true);
    }

    public static void lambda$showShareAffiliateAlert$24(final BottomSheet bottomSheet, final Context context, final int i, final long j, final Theme.ResourcesProvider resourcesProvider, final TLRPC.UserFull userFull) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChannelAffiliateProgramsFragment.lambda$showShareAffiliateAlert$23(TLRPC.UserFull.this, bottomSheet, context, i, j, resourcesProvider);
            }
        });
    }

    public static void lambda$showShareAffiliateAlert$25(final int i, TL_payments.connectedBotStarRef connectedbotstarref, final BottomSheet bottomSheet, final Context context, final long j, final Theme.ResourcesProvider resourcesProvider, TL_payments.connectedBotStarRef connectedbotstarref2) {
        if (connectedbotstarref2 != null) {
            bottomSheet.dismiss();
            showShareAffiliateAlert(context, i, connectedbotstarref2, j, resourcesProvider);
        } else {
            TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(connectedbotstarref.bot_id));
            if (user != null) {
                MessagesController.getInstance(i).loadFullUser(user, 0, true, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        ChannelAffiliateProgramsFragment.lambda$showShareAffiliateAlert$24(BottomSheet.this, context, i, j, resourcesProvider, (TLRPC.UserFull) obj);
                    }
                });
            }
        }
    }

    public static void lambda$showShareAffiliateAlert$26(final int i, final Context context, final long j, final TL_payments.connectedBotStarRef connectedbotstarref, final BottomSheet bottomSheet, final Theme.ResourcesProvider resourcesProvider) {
        BotStarsController.getInstance(i).getConnectedBot(context, j, connectedbotstarref.bot_id, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                ChannelAffiliateProgramsFragment.lambda$showShareAffiliateAlert$25(i, connectedbotstarref, bottomSheet, context, j, resourcesProvider, (TL_payments.connectedBotStarRef) obj);
            }
        });
    }

    public static void lambda$showShareAffiliateAlert$27(final int i, final BottomSheet bottomSheet, final Theme.ResourcesProvider resourcesProvider, View view, long j, final Context context, final TL_payments.connectedBotStarRef connectedbotstarref, View view2) {
        long j2;
        ArrayList admined = BotStarsController.getInstance(i).getAdmined();
        admined.add(0, UserConfig.getInstance(i).getCurrentUser());
        ItemOptions makeOptions = ItemOptions.makeOptions(bottomSheet.getContainerView(), resourcesProvider, view);
        Iterator it = admined.iterator();
        while (it.hasNext()) {
            TLObject tLObject = (TLObject) it.next();
            if (tLObject instanceof TLRPC.User) {
                j2 = ((TLRPC.User) tLObject).id;
            } else if (tLObject instanceof TLRPC.Chat) {
                TLRPC.Chat chat = (TLRPC.Chat) tLObject;
                if (ChatObject.isChannelAndNotMegaGroup(chat)) {
                    j2 = -chat.id;
                }
            }
            final long j3 = j2;
            makeOptions.addChat(tLObject, j3 == j, new Runnable() {
                @Override
                public final void run() {
                    ChannelAffiliateProgramsFragment.lambda$showShareAffiliateAlert$26(i, context, j3, connectedbotstarref, bottomSheet, resourcesProvider);
                }
            });
        }
        makeOptions.setDrawScrim(false).setDimAlpha(0).setGravity(5).translate(AndroidUtilities.dp(24.0f), 0.0f).show();
    }

    public static StarParticlesView makeParticlesView(Context context, int i, int i2) {
        return new StarParticlesView(context) {
            {
                setClipWithGradient();
            }

            @Override
            public void configure() {
                super.configure();
                StarParticlesView.Drawable drawable = this.drawable;
                drawable.useGradient = true;
                drawable.useBlur = false;
                drawable.forceMaxAlpha = true;
                drawable.checkBounds = true;
                drawable.init();
            }

            @Override
            protected int getStarsRectWidth() {
                return getMeasuredWidth();
            }
        };
    }

    public static void showConnectAffiliateAlert(final Context context, final int i, final TL_payments.starRefProgram starrefprogram, final long j, final Theme.ResourcesProvider resourcesProvider, final boolean z) {
        ?? r3;
        String formatPluralString;
        View view;
        final BackupImageView backupImageView;
        final TextView textView;
        if (starrefprogram == null || context == null) {
            return;
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(context, false, resourcesProvider);
        final long[] jArr = {j};
        final TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(starrefprogram.bot_id));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
        linearLayout.setClipChildren(false);
        linearLayout.setClipToPadding(false);
        FrameLayout frameLayout = new FrameLayout(context);
        BackupImageView backupImageView2 = new BackupImageView(context);
        backupImageView2.setRoundRadius(AndroidUtilities.dp(30.0f));
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setInfo(user);
        backupImageView2.setForUserOrChat(user, avatarDrawable);
        frameLayout.addView(backupImageView2, LayoutHelper.createFrame(60, 60.0f, 19, 0.0f, 0.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(R.drawable.msg_arrow_avatar);
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        imageView.setScaleType(scaleType);
        imageView.setTranslationX(-AndroidUtilities.dp(2.0825f));
        int color = Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, resourcesProvider);
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        imageView.setColorFilter(new PorterDuffColorFilter(color, mode));
        frameLayout.addView(imageView, LayoutHelper.createFrame(36, 60.0f, 17, 60.0f, 0.0f, 60.0f, 0.0f));
        final BackupImageView backupImageView3 = new BackupImageView(context);
        backupImageView3.setRoundRadius(AndroidUtilities.dp(30.0f));
        frameLayout.addView(backupImageView3, LayoutHelper.createFrame(60, 60.0f, 21, 0.0f, 0.0f, 5.66f, 0.0f));
        View view2 = new View(context);
        int dp = AndroidUtilities.dp(13.66f);
        int i2 = Theme.key_dialogBackground;
        view2.setBackground(Theme.createCircleDrawable(dp, Theme.getColor(i2, resourcesProvider)));
        frameLayout.addView(view2, LayoutHelper.createFrame(27.33f, 27.33f, 21, 0.0f, 18.0f, 0.0f, 0.0f));
        View view3 = new View(context);
        int i3 = Theme.key_premiumGradient1;
        PremiumGradient.PremiumGradientTools premiumGradientTools = new PremiumGradient.PremiumGradientTools(i3, Theme.key_premiumGradient2, -1, -1, -1, resourcesProvider);
        ShapeDrawable createCircleDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(12.0f), Theme.getColor(i3, resourcesProvider));
        premiumGradientTools.gradientMatrix(0, 0, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), 0.0f, 0.0f);
        createCircleDrawable.getPaint().setShader(premiumGradientTools.paint.getShader());
        view3.setBackground(createCircleDrawable);
        frameLayout.addView(view3, LayoutHelper.createFrame(24, 24.0f, 21, 0.0f, 18.0f, 1.66f, 0.0f));
        ImageView imageView2 = new ImageView(context);
        imageView2.setImageResource(R.drawable.msg_premium_badge);
        imageView2.setScaleX(0.77f);
        imageView2.setScaleY(0.77f);
        frameLayout.addView(imageView2, LayoutHelper.createFrame(24, 24.0f, 21, 0.0f, 18.0f, 1.66f, 0.0f));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 0));
        TextView textView2 = new TextView(context);
        int i4 = Theme.key_windowBackgroundWhiteBlackText;
        textView2.setTextColor(Theme.getColor(i4, resourcesProvider));
        textView2.setTextSize(1, 20.0f);
        textView2.setGravity(17);
        textView2.setText(LocaleController.getString(R.string.ChannelAffiliateProgramJoinTitle));
        textView2.setTypeface(AndroidUtilities.bold());
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 21.0f, 0.0f, 8.33f));
        TextView textView3 = new TextView(context);
        textView3.setTextColor(Theme.getColor(i4, resourcesProvider));
        textView3.setTextSize(1, 14.0f);
        textView3.setGravity(17);
        NotificationCenter.listenEmojiLoading(textView3);
        int i5 = R.string.ChannelAffiliateProgramJoinText;
        String userName = UserObject.getUserName(user);
        CharSequence percents = AffiliateProgramFragment.percents(starrefprogram.commission_permille);
        int i6 = starrefprogram.duration_months;
        if (i6 <= 0) {
            formatPluralString = LocaleController.getString(R.string.ChannelAffiliateProgramJoinText_Lifetime);
            r3 = 0;
        } else if (i6 < 12 || i6 % 12 != 0) {
            r3 = 0;
            formatPluralString = LocaleController.formatPluralString("ChannelAffiliateProgramJoinText_Months", i6, new Object[0]);
        } else {
            r3 = 0;
            formatPluralString = LocaleController.formatPluralString("ChannelAffiliateProgramJoinText_Years", i6 / 12, new Object[0]);
        }
        Object[] objArr = new Object[3];
        objArr[r3] = userName;
        objArr[1] = percents;
        objArr[2] = formatPluralString;
        textView3.setText(Emoji.replaceEmoji(AndroidUtilities.replaceTags(LocaleController.formatString(i5, objArr)), textView3.getPaint().getFontMetricsInt(), r3));
        linearLayout.addView(textView3, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 22.0f));
        if (((user.flags & 4096) != 0 || BuildVars.DEBUG_PRIVATE_VERSION) && (starrefprogram.flags & 4) != 0) {
            TableView tableView = new TableView(context, resourcesProvider);
            tableView.addRow(LocaleController.getString(R.string.ChannelAffiliateProgramJoinMonthlyUsers), LocaleController.formatNumber(user.bot_active_users, ','));
            tableView.addRow(LocaleController.getString(R.string.ChannelAffiliateProgramJoinDailyRevenue), StarsIntroActivity.replaceStarsWithPlain("⭐️ " + ((Object) StarsIntroActivity.formatStarsAmountShort(starrefprogram.daily_revenue_per_user, 0.95f, ',')), 0.75f));
            linearLayout.addView(tableView, LayoutHelper.createLinear(-1, -2, 0.0f, -4.0f, 0.0f, 12.0f));
        }
        if (j >= 0) {
            TextView textView4 = new TextView(context);
            textView4.setTextColor(Theme.getColor(i4, resourcesProvider));
            textView4.setTextSize(1, 14.0f);
            textView4.setGravity(17);
            textView4.setText(LocaleController.getString(R.string.ChannelAffiliateProgramLinkSendTo));
            linearLayout.addView(textView4, LayoutHelper.createLinear(-1, -2, 20.0f, 0.0f, 20.0f, 0.0f));
            LinearLayout linearLayout2 = new LinearLayout(context);
            linearLayout2.setOrientation(r3);
            int dp2 = AndroidUtilities.dp(28.0f);
            int i7 = Theme.key_windowBackgroundGray;
            linearLayout2.setBackground(Theme.createRoundRectDrawable(dp2, Theme.getColor(i7, resourcesProvider)));
            linearLayout2.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(28.0f), Theme.getColor(i7, resourcesProvider), Theme.blendOver(Theme.getColor(i7, resourcesProvider), Theme.getColor(Theme.key_listSelector, resourcesProvider))));
            BackupImageView backupImageView4 = new BackupImageView(context);
            backupImageView4.setRoundRadius(AndroidUtilities.dp(14.0f));
            linearLayout2.addView(backupImageView4, LayoutHelper.createLinear(28, 28));
            TextView textView5 = new TextView(context);
            textView5.setTextSize(1, 13.0f);
            textView5.setTextColor(Theme.getColor(i4, resourcesProvider));
            linearLayout2.addView(textView5, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
            ImageView imageView3 = new ImageView(context);
            imageView3.setScaleType(scaleType);
            imageView3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextGray3, resourcesProvider), mode));
            imageView3.setImageResource(R.drawable.arrows_select);
            linearLayout2.addView(imageView3, LayoutHelper.createLinear(-2, -2, 16, 2, 0, 5, 0));
            linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-2, 28, 1, 0, 11, 0, 20));
            view = linearLayout2;
            backupImageView = backupImageView4;
            textView = textView5;
        } else {
            view = null;
            backupImageView = null;
            textView = null;
        }
        final ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        buttonWithCounterView.setText(LocaleController.getString(R.string.ChannelAffiliateProgramJoinButton), r3);
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        linksTextView.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.ChannelAffiliateProgramJoinButtonInfo), new Runnable() {
            @Override
            public final void run() {
                ChannelAffiliateProgramsFragment.lambda$showConnectAffiliateAlert$9(context);
            }
        }));
        linksTextView.setGravity(17);
        linksTextView.setTextSize(1, 12.0f);
        linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4, resourcesProvider));
        linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
        linearLayout.addView(linksTextView, LayoutHelper.createLinear(-1, -2, 49, 14, 14, 14, 6));
        builder.setCustomView(linearLayout);
        final BottomSheet create = builder.create();
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view4) {
                ChannelAffiliateProgramsFragment.lambda$showConnectAffiliateAlert$12(ButtonWithCounterView.this, jArr, i, starrefprogram, create, j, z, context, resourcesProvider, user, view4);
            }
        });
        create.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                ChannelAffiliateProgramsFragment.lambda$showConnectAffiliateAlert$13(dialogInterface);
            }
        });
        final Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                ChannelAffiliateProgramsFragment.lambda$showConnectAffiliateAlert$14(jArr, i, backupImageView3, backupImageView, textView);
            }
        };
        runnable.run();
        if (view != null) {
            BotStarsController.getInstance(i).loadAdmined();
            final View view4 = view;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view5) {
                    ChannelAffiliateProgramsFragment.lambda$showConnectAffiliateAlert$16(i, create, resourcesProvider, view4, jArr, runnable, view5);
                }
            });
        }
        create.fixNavigationBar(Theme.getColor(i2, resourcesProvider));
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (!AndroidUtilities.isTablet() && safeLastFragment != null && !AndroidUtilities.hasDialogOnTop(safeLastFragment)) {
            create.makeAttached(safeLastFragment);
        }
        create.show();
    }

    public static BottomSheet showShareAffiliateAlert(final Context context, final int i, final TL_payments.connectedBotStarRef connectedbotstarref, final long j, final Theme.ResourcesProvider resourcesProvider) {
        BottomSheet.Builder builder;
        ImageView.ScaleType scaleType;
        ImageView.ScaleType scaleType2;
        char c;
        String formatPluralString;
        int i2;
        String formatString;
        char c2;
        String formatPluralString2;
        View view;
        boolean z;
        String str;
        int i3;
        String formatPluralString3;
        String str2;
        if (connectedbotstarref == null || context == null) {
            return null;
        }
        BottomSheet.Builder builder2 = new BottomSheet.Builder(context, false, resourcesProvider);
        final TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(connectedbotstarref.bot_id));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(20.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(8.0f));
        linearLayout.setClipChildren(false);
        linearLayout.setClipToPadding(false);
        FrameLayout frameLayout = new FrameLayout(context);
        View view2 = new View(context);
        view2.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(40.0f), Theme.getColor(connectedbotstarref.revoked ? Theme.key_color_red : Theme.key_featuredStickers_addButton, resourcesProvider)));
        frameLayout.addView(view2, LayoutHelper.createFrame(80, 80.0f, 49, 0.0f, 0.0f, 0.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        ImageView.ScaleType scaleType3 = ImageView.ScaleType.CENTER;
        imageView.setScaleType(scaleType3);
        imageView.setImageResource(connectedbotstarref.revoked ? R.drawable.msg_link_2 : R.drawable.msg_limit_links);
        imageView.setScaleX(connectedbotstarref.revoked ? 2.0f : 1.8f);
        imageView.setScaleY(connectedbotstarref.revoked ? 2.0f : 1.8f);
        frameLayout.addView(imageView, LayoutHelper.createFrame(80, 80.0f, 49, 0.0f, 0.0f, 0.0f, 0.0f));
        if (connectedbotstarref.participants > 0) {
            FrameLayout frameLayout2 = new FrameLayout(context);
            frameLayout2.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(50.0f), Theme.getColor(Theme.key_dialogBackground, resourcesProvider)));
            frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 66.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            textView.setTypeface(AndroidUtilities.bold());
            textView.setTextSize(1, 12.0f);
            textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(9.5f), Theme.getColor(connectedbotstarref.revoked ? Theme.key_color_red : Theme.key_color_green, resourcesProvider)));
            textView.setTextColor(-1);
            textView.setPadding(AndroidUtilities.dp(6.66f), 0, AndroidUtilities.dp(6.66f), 0);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append((CharSequence) "s ");
            ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.mini_reply_user);
            coloredImageSpan.setScale(0.937f, 0.937f);
            coloredImageSpan.translate(-AndroidUtilities.dp(1.33f), AndroidUtilities.dp(1.0f));
            coloredImageSpan.spaceScaleX = 0.8f;
            builder = builder2;
            spannableStringBuilder.setSpan(coloredImageSpan, 0, 1, 33);
            scaleType = scaleType3;
            spannableStringBuilder.append((CharSequence) String.valueOf(connectedbotstarref.participants));
            textView.setText(spannableStringBuilder);
            textView.setGravity(17);
            frameLayout2.addView(textView, LayoutHelper.createFrame(-1, 19.0f, 119, 1.33f, 1.33f, 1.33f, 1.33f));
        } else {
            builder = builder2;
            scaleType = scaleType3;
        }
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 0));
        TextView textView2 = new TextView(context);
        int i4 = Theme.key_windowBackgroundWhiteBlackText;
        textView2.setTextColor(Theme.getColor(i4, resourcesProvider));
        textView2.setTextSize(1, 20.0f);
        textView2.setGravity(17);
        textView2.setText(LocaleController.getString(R.string.ChannelAffiliateProgramLinkTitle));
        textView2.setTypeface(AndroidUtilities.bold());
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 20.0f, 18.0f, 20.0f, 8.33f));
        TextView textView3 = new TextView(context);
        textView3.setTextColor(Theme.getColor(i4, resourcesProvider));
        textView3.setTextSize(1, 14.0f);
        textView3.setGravity(17);
        if (connectedbotstarref.revoked) {
            textView3.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.ChannelAffiliateProgramLinkTextRevoked)));
            scaleType2 = scaleType;
        } else {
            if (j < 0) {
                int i5 = R.string.ChannelAffiliateProgramLinkTextChannel;
                CharSequence percents = AffiliateProgramFragment.percents(connectedbotstarref.commission_permille);
                String userName = UserObject.getUserName(user);
                int i6 = connectedbotstarref.duration_months;
                if (i6 <= 0) {
                    formatPluralString2 = LocaleController.getString(R.string.ChannelAffiliateProgramJoinText_Lifetime);
                    scaleType2 = scaleType;
                    c2 = 0;
                } else {
                    scaleType2 = scaleType;
                    if (i6 < 12 || i6 % 12 != 0) {
                        c2 = 0;
                        formatPluralString2 = LocaleController.formatPluralString("ChannelAffiliateProgramJoinText_Months", i6, new Object[0]);
                    } else {
                        c2 = 0;
                        formatPluralString2 = LocaleController.formatPluralString("ChannelAffiliateProgramJoinText_Years", i6 / 12, new Object[0]);
                    }
                }
                Object[] objArr = new Object[3];
                objArr[c2] = percents;
                objArr[1] = userName;
                objArr[2] = formatPluralString2;
                formatString = LocaleController.formatString(i5, objArr);
            } else {
                scaleType2 = scaleType;
                int i7 = R.string.ChannelAffiliateProgramLinkTextUser;
                CharSequence percents2 = AffiliateProgramFragment.percents(connectedbotstarref.commission_permille);
                String userName2 = UserObject.getUserName(user);
                int i8 = connectedbotstarref.duration_months;
                if (i8 <= 0) {
                    formatPluralString = LocaleController.getString(R.string.ChannelAffiliateProgramJoinText_Lifetime);
                    i2 = 3;
                    c = 0;
                } else {
                    if (i8 < 12 || i8 % 12 != 0) {
                        c = 0;
                        formatPluralString = LocaleController.formatPluralString("ChannelAffiliateProgramJoinText_Months", i8, new Object[0]);
                    } else {
                        c = 0;
                        formatPluralString = LocaleController.formatPluralString("ChannelAffiliateProgramJoinText_Years", i8 / 12, new Object[0]);
                    }
                    i2 = 3;
                }
                Object[] objArr2 = new Object[i2];
                objArr2[c] = percents2;
                objArr2[1] = userName2;
                objArr2[2] = formatPluralString;
                formatString = LocaleController.formatString(i7, objArr2);
            }
            textView3.setText(AndroidUtilities.replaceTags(formatString));
        }
        linearLayout.addView(textView3, LayoutHelper.createLinear(-1, -2, 20.0f, 0.0f, 20.0f, 18.0f));
        if (connectedbotstarref.revoked) {
            view = null;
        } else {
            TextView textView4 = new TextView(context);
            textView4.setTextColor(Theme.getColor(i4, resourcesProvider));
            textView4.setTextSize(1, 14.0f);
            textView4.setGravity(17);
            textView4.setText(LocaleController.getString(R.string.ChannelAffiliateProgramLinkSendTo));
            linearLayout.addView(textView4, LayoutHelper.createLinear(-1, -2, 20.0f, 0.0f, 20.0f, 0.0f));
            LinearLayout linearLayout2 = new LinearLayout(context);
            linearLayout2.setOrientation(0);
            linearLayout2.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(28.0f), Theme.getColor(Theme.key_windowBackgroundGray, resourcesProvider)));
            BackupImageView backupImageView = new BackupImageView(context);
            backupImageView.setRoundRadius(AndroidUtilities.dp(14.0f));
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            linearLayout2.addView(backupImageView, LayoutHelper.createLinear(28, 28));
            TextView textView5 = new TextView(context);
            textView5.setTextSize(1, 13.0f);
            textView5.setTextColor(Theme.getColor(i4, resourcesProvider));
            if (j >= 0) {
                TLRPC.User user2 = MessagesController.getInstance(i).getUser(Long.valueOf(j));
                avatarDrawable.setInfo(user2);
                backupImageView.setForUserOrChat(user2, avatarDrawable);
                str2 = UserObject.getUserName(user2);
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(-j));
                avatarDrawable.setInfo(chat);
                backupImageView.setForUserOrChat(chat, avatarDrawable);
                str2 = chat == null ? "" : chat.title;
            }
            textView5.setText(str2);
            linearLayout2.addView(textView5, LayoutHelper.createLinear(-2, -2, 16, 6, 0, 0, 0));
            ImageView imageView2 = new ImageView(context);
            imageView2.setScaleType(scaleType2);
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogTextGray3, resourcesProvider), PorterDuff.Mode.SRC_IN));
            imageView2.setImageResource(R.drawable.arrows_select);
            linearLayout2.addView(imageView2, LayoutHelper.createLinear(-2, -2, 16, 2, 0, 5, 0));
            linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-2, 28, 1, 0, 11, 0, 22));
            view = linearLayout2;
        }
        TextView textView6 = new TextView(context);
        textView6.setTextSize(1, 16.0f);
        textView6.setGravity(17);
        textView6.setTextColor(Theme.getColor(i4, resourcesProvider));
        int dp = AndroidUtilities.dp(8.0f);
        int i9 = Theme.key_windowBackgroundGray;
        textView6.setBackground(Theme.createSimpleSelectorRoundRectDrawable(dp, Theme.getColor(i9, resourcesProvider), Theme.blendOver(Theme.getColor(i9, resourcesProvider), Theme.getColor(Theme.key_listSelector, resourcesProvider))));
        textView6.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(14.66f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(14.66f));
        String str3 = connectedbotstarref.url;
        textView6.setText((str3 == null || !str3.startsWith("https://")) ? connectedbotstarref.url : connectedbotstarref.url.substring(8));
        linearLayout.addView(textView6, LayoutHelper.createFrame(-1, -2.0f, 7, 0.0f, 0.0f, 0.0f, 12.0f));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        if (connectedbotstarref.revoked) {
            z = false;
            str = LocaleController.getString(R.string.ChannelAffiliateProgramLinkRejoin);
        } else {
            SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder();
            spannableStringBuilder2.append((CharSequence) "c ");
            z = false;
            spannableStringBuilder2.setSpan(new ColoredImageSpan(R.drawable.msg_copy_filled), 0, 1, 33);
            spannableStringBuilder2.append((CharSequence) LocaleController.getString(R.string.ChannelAffiliateProgramLinkCopy));
            str = spannableStringBuilder2;
        }
        buttonWithCounterView.setText(str, z);
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        long j2 = connectedbotstarref.participants;
        if (j2 <= 0) {
            i3 = 1;
            formatPluralString3 = LocaleController.formatString(R.string.ChannelAffiliateProgramLinkOpenedNone, UserObject.getUserName(user));
        } else {
            i3 = 1;
            formatPluralString3 = LocaleController.formatPluralString("ChannelAffiliateProgramLinkOpened", (int) j2, UserObject.getUserName(user));
        }
        linksTextView.setText(formatPluralString3);
        linksTextView.setGravity(17);
        linksTextView.setTextSize(i3, 12.0f);
        linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4, resourcesProvider));
        linksTextView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
        linearLayout.addView(linksTextView, LayoutHelper.createLinear(-1, -2, 49, 14, 12, 14, 2));
        BottomSheet.Builder builder3 = builder;
        builder3.setCustomView(linearLayout);
        final BottomSheet create = builder3.create();
        final Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                ChannelAffiliateProgramsFragment.lambda$showShareAffiliateAlert$17(TL_payments.connectedBotStarRef.this, create, resourcesProvider, user);
            }
        };
        if (!connectedbotstarref.revoked) {
            textView6.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view3) {
                    runnable.run();
                }
            });
        }
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                ChannelAffiliateProgramsFragment.lambda$showShareAffiliateAlert$21(TL_payments.connectedBotStarRef.this, i, create, context, j, resourcesProvider, runnable, view3);
            }
        });
        create.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                ChannelAffiliateProgramsFragment.lambda$showShareAffiliateAlert$22(dialogInterface);
            }
        });
        if (view != null) {
            BotStarsController.getInstance(i).loadAdmined();
            final View view3 = view;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view4) {
                    ChannelAffiliateProgramsFragment.lambda$showShareAffiliateAlert$27(i, create, resourcesProvider, view3, j, context, connectedbotstarref, view4);
                }
            });
        }
        create.fixNavigationBar(Theme.getColor(Theme.key_dialogBackground, resourcesProvider));
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (!AndroidUtilities.isTablet() && safeLastFragment != null && !AndroidUtilities.hasDialogOnTop(safeLastFragment)) {
            create.makeAttached(safeLastFragment);
        }
        create.show();
        return create;
    }

    private CharSequence sortText(BotStarsController.ChannelSuggestedBots.Sort sort) {
        SpannableString spannableString;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append((CharSequence) LocaleController.getString(R.string.ChannelAffiliateProgramProgramsSort)).append((CharSequence) " ");
        if (sort == BotStarsController.ChannelSuggestedBots.Sort.BY_PROFITABILITY) {
            spannableString = new SpannableString(LocaleController.getString(R.string.ChannelAffiliateProgramProgramsSortProfitability) + "v");
        } else {
            if (sort != BotStarsController.ChannelSuggestedBots.Sort.BY_REVENUE) {
                if (sort == BotStarsController.ChannelSuggestedBots.Sort.BY_DATE) {
                    spannableString = new SpannableString(LocaleController.getString(R.string.ChannelAffiliateProgramProgramsSortDate) + "v");
                }
                return spannableStringBuilder;
            }
            spannableString = new SpannableString(LocaleController.getString(R.string.ChannelAffiliateProgramProgramsSortRevenue) + "v");
        }
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.arrow_more);
        coloredImageSpan.useLinkPaintColor = true;
        coloredImageSpan.setScale(0.6f, 0.6f);
        spannableString.setSpan(coloredImageSpan, spannableString.length() - 1, spannableString.length(), 33);
        spannableString.setSpan(new AnonymousClass4(sort, BotStarsController.getInstance(this.currentAccount).getChannelSuggestedBots(this.dialogId)), 0, spannableString.length(), 33);
        spannableStringBuilder.append((CharSequence) spannableString);
        return spannableStringBuilder;
    }

    @Override
    protected RecyclerView.Adapter createAdapter() {
        UniversalAdapter universalAdapter = new UniversalAdapter(this.listView, getContext(), this.currentAccount, this.classGuid, true, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                ChannelAffiliateProgramsFragment.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, getResourceProvider()) {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                if (i != 42) {
                    return super.onCreateViewHolder(viewGroup, i);
                }
                HeaderCell headerCell = new HeaderCell(ChannelAffiliateProgramsFragment.this.getContext(), Theme.key_windowBackgroundWhiteBlueHeader, 21, 0, false, ((BaseFragment) ChannelAffiliateProgramsFragment.this).resourceProvider);
                headerCell.setHeight(25);
                return new RecyclerListView.Holder(headerCell);
            }
        };
        this.adapter = universalAdapter;
        return universalAdapter;
    }

    @Override
    public StarParticlesView createParticlesView() {
        return makeParticlesView(getContext(), 75, 1);
    }

    @Override
    public View createView(final Context context) {
        this.useFillLastLayoutManager = false;
        this.particlesViewHeight = AndroidUtilities.dp(238.0f);
        View view = new View(context) {
            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), 1073741824));
            }
        };
        this.emptyLayout = view;
        view.setBackgroundColor(Theme.getColor(Theme.key_dialogBackgroundGray));
        super.createView(context);
        FrameLayout frameLayout = new FrameLayout(context);
        this.aboveTitleView = frameLayout;
        frameLayout.setClickable(true);
        GLIconTextureView gLIconTextureView = new GLIconTextureView(context, 1, 3);
        this.iconTextureView = gLIconTextureView;
        GLIconRenderer gLIconRenderer = gLIconTextureView.mRenderer;
        gLIconRenderer.colorKey1 = Theme.key_starsGradient1;
        gLIconRenderer.colorKey2 = Theme.key_starsGradient2;
        gLIconRenderer.updateColors();
        this.iconTextureView.setStarParticlesView(this.particlesView);
        this.aboveTitleView.addView(this.iconTextureView, LayoutHelper.createFrame(190, 190.0f, 17, 0.0f, 32.0f, 0.0f, 24.0f));
        configureHeader(LocaleController.getString(R.string.ChannelAffiliateProgramTitle), AndroidUtilities.replaceTags(LocaleController.getString(R.string.ChannelAffiliateProgramText)), this.aboveTitleView, null);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view2, int i) {
                ChannelAffiliateProgramsFragment.this.lambda$createView$0(context, view2, i);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public final boolean onItemClick(View view2, int i) {
                boolean lambda$createView$8;
                lambda$createView$8 = ChannelAffiliateProgramsFragment.this.lambda$createView$8(context, view2, i);
                return lambda$createView$8;
            }
        });
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setSupportsChangeAnimations(false);
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDurations(350L);
        this.listView.setItemAnimator(defaultItemAnimator);
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                if (ChannelAffiliateProgramsFragment.this.isLoadingVisible() || !recyclerView.canScrollVertically(1)) {
                    BotStarsController.getInstance(((BaseFragment) ChannelAffiliateProgramsFragment.this).currentAccount).getChannelConnectedBots(ChannelAffiliateProgramsFragment.this.dialogId).load();
                    BotStarsController.getInstance(((BaseFragment) ChannelAffiliateProgramsFragment.this).currentAccount).getChannelSuggestedBots(ChannelAffiliateProgramsFragment.this.dialogId).load();
                }
            }
        });
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        UniversalAdapter universalAdapter;
        if (i != NotificationCenter.channelConnectedBotsUpdate) {
            if (i == NotificationCenter.channelSuggestedBotsUpdate && ((Long) objArr[0]).longValue() == this.dialogId && (universalAdapter = this.adapter) != null) {
                universalAdapter.update(true);
                return;
            }
            return;
        }
        if (((Long) objArr[0]).longValue() == this.dialogId) {
            UniversalAdapter universalAdapter2 = this.adapter;
            if (universalAdapter2 != null) {
                universalAdapter2.update(true);
            }
            BotStarsController.getInstance(this.currentAccount).getChannelConnectedBots(this.dialogId).load();
        }
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        if (getContext() == null) {
            return;
        }
        arrayList.add(UItem.asFullyCustom(getHeader(getContext())));
        arrayList.add(AffiliateProgramFragment.FeatureCell.Factory.as(R.drawable.menu_feature_reliable, LocaleController.getString(R.string.ChannelAffiliateProgramFeature1Title), LocaleController.getString(R.string.ChannelAffiliateProgramFeature1)));
        arrayList.add(AffiliateProgramFragment.FeatureCell.Factory.as(R.drawable.menu_feature_transparent, LocaleController.getString(R.string.ChannelAffiliateProgramFeature2Title), LocaleController.getString(R.string.ChannelAffiliateProgramFeature2)));
        arrayList.add(AffiliateProgramFragment.FeatureCell.Factory.as(R.drawable.menu_feature_simple, LocaleController.getString(R.string.ChannelAffiliateProgramFeature3Title), LocaleController.getString(R.string.ChannelAffiliateProgramFeature3)));
        arrayList.add(UItem.asShadow(1, null));
        BotStarsController.ChannelConnectedBots channelConnectedBots = BotStarsController.getInstance(this.currentAccount).getChannelConnectedBots(this.dialogId);
        if (!channelConnectedBots.bots.isEmpty() || channelConnectedBots.count > 0) {
            arrayList.add(UItem.asHeader(LocaleController.getString(R.string.ChannelAffiliateProgramMyPrograms)));
            for (int i = 0; i < channelConnectedBots.bots.size(); i++) {
                arrayList.add(BotCell.Factory.as((TL_payments.connectedBotStarRef) channelConnectedBots.bots.get(i)));
            }
            if (channelConnectedBots.isLoading()) {
                arrayList.add(UItem.asFlicker(29));
                arrayList.add(UItem.asFlicker(29));
                arrayList.add(UItem.asFlicker(29));
            }
            arrayList.add(UItem.asShadow(2, null));
        }
        BotStarsController.ChannelSuggestedBots channelSuggestedBots = BotStarsController.getInstance(this.currentAccount).getChannelSuggestedBots(this.dialogId);
        if (!channelSuggestedBots.bots.isEmpty() || channelSuggestedBots.count > 0) {
            arrayList.add(HeaderSortCell.Factory.as(LocaleController.getString(R.string.ChannelAffiliateProgramPrograms), sortText(channelSuggestedBots.getSort())));
            for (int i2 = 0; i2 < channelSuggestedBots.bots.size(); i2++) {
                arrayList.add(BotCell.Factory.as(channelSuggestedBots.bots.get(i2)));
            }
            if (channelSuggestedBots.isLoading()) {
                arrayList.add(UItem.asFlicker(29));
                arrayList.add(UItem.asFlicker(29));
                arrayList.add(UItem.asFlicker(29));
            }
            arrayList.add(UItem.asShadow(3, null));
        }
        arrayList.add(UItem.asCustom(this.emptyLayout));
    }

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.channelConnectedBotsUpdate);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.channelSuggestedBotsUpdate);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.channelConnectedBotsUpdate);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.channelSuggestedBotsUpdate);
    }

    @Override
    public void onPause() {
        super.onPause();
        GLIconTextureView gLIconTextureView = this.iconTextureView;
        if (gLIconTextureView != null) {
            gLIconTextureView.setPaused(true);
            this.iconTextureView.setDialogVisible(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        GLIconTextureView gLIconTextureView = this.iconTextureView;
        if (gLIconTextureView != null) {
            gLIconTextureView.setPaused(false);
            this.iconTextureView.setDialogVisible(false);
        }
    }
}
