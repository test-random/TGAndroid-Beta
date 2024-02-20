package org.telegram.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SMSJobController;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_boolFalse;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TL_smsjobs$TL_smsjobs_eligibleToJoin;
import org.telegram.tgnet.TL_smsjobs$TL_smsjobs_join;
import org.telegram.tgnet.TL_smsjobs$TL_smsjobs_status;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.Premium.LimitPreviewView;
import org.telegram.ui.Components.Premium.boosts.GiftInfoBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.SMSStatsActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
public class SMSStatsActivity extends GradientHeaderActivity implements NotificationCenter.NotificationCenterDelegate {
    private View aboveTitleView;
    private LimitPreviewView limitPreviewView;
    private TableView table;
    private ArrayList<Item> oldItems = new ArrayList<>();
    private ArrayList<Item> items = new ArrayList<>();
    private boolean allowInternational = false;
    private boolean askedStatusToLoad = false;
    private final AdapterWithDiffUtils adapter = new AdapterWithDiffUtils() {
        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 3 || itemViewType == 4;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View header;
            if (i == 0) {
                SMSStatsActivity sMSStatsActivity = SMSStatsActivity.this;
                header = sMSStatsActivity.getHeader(sMSStatsActivity.getContext());
            } else if (i == 1) {
                SMSStatsActivity sMSStatsActivity2 = SMSStatsActivity.this;
                SMSStatsActivity sMSStatsActivity3 = SMSStatsActivity.this;
                header = sMSStatsActivity2.table = new TableView(sMSStatsActivity3.getContext(), ((BaseFragment) SMSStatsActivity.this).currentAccount);
            } else if (i == 3) {
                header = new TextCell(SMSStatsActivity.this.getContext());
            } else if (i == 4) {
                header = new TextCell(SMSStatsActivity.this.getContext(), 23, false, true, SMSStatsActivity.this.getResourceProvider());
            } else if (i == 5) {
                header = new HeaderCell(SMSStatsActivity.this.getContext());
            } else if (i == 6) {
                header = new JobEntryCell(SMSStatsActivity.this.getContext());
            } else {
                header = new TextInfoPrivacyCell(SMSStatsActivity.this.getContext());
                CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray)), Theme.getThemedDrawable(SMSStatsActivity.this.getContext(), R.drawable.greydivider, Theme.getColor(Theme.key_windowBackgroundGrayShadow, ((BaseFragment) SMSStatsActivity.this).resourceProvider)), 0, 0);
                combinedDrawable.setFullsize(true);
                header.setBackground(combinedDrawable);
            }
            header.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(header);
        }

        @Override
        public int getItemViewType(int i) {
            if (i < 0 || i >= SMSStatsActivity.this.items.size()) {
                return 2;
            }
            return ((Item) SMSStatsActivity.this.items.get(i)).viewType;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (i < 0 || i >= SMSStatsActivity.this.items.size()) {
                return;
            }
            Item item = (Item) SMSStatsActivity.this.items.get(i);
            int itemViewType = viewHolder.getItemViewType();
            int i2 = i + 1;
            boolean z = i2 < SMSStatsActivity.this.items.size() && ((Item) SMSStatsActivity.this.items.get(i2)).viewType == itemViewType;
            String str = BuildConfig.APP_CENTER_HASH;
            if (itemViewType == 2) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                boolean z2 = i == SMSStatsActivity.this.items.size() - 1;
                if (TextUtils.isEmpty(item.text)) {
                    textInfoPrivacyCell.setFixedSize(z2 ? 350 : 21);
                    textInfoPrivacyCell.setText(BuildConfig.APP_CENTER_HASH);
                    return;
                }
                textInfoPrivacyCell.setFixedSize(0);
                textInfoPrivacyCell.setText(item.text);
            } else if (itemViewType != 3) {
                if (itemViewType == 4) {
                    ((TextCell) viewHolder.itemView).setTextAndCheck(item.text, item.id == 3 ? SMSStatsActivity.this.allowInternational : false, z);
                } else if (itemViewType == 1) {
                    ((TableView) viewHolder.itemView).update(false);
                } else if (itemViewType == 5) {
                    ((HeaderCell) viewHolder.itemView).setText(item.text);
                } else if (itemViewType == 6) {
                    ((JobEntryCell) viewHolder.itemView).set(item.entry);
                }
            } else {
                TextCell textCell = (TextCell) viewHolder.itemView;
                if (item.red) {
                    textCell.setColors(Theme.key_text_RedBold, Theme.key_text_RedRegular);
                } else {
                    textCell.setColors(Theme.key_windowBackgroundWhiteGrayIcon, Theme.key_windowBackgroundWhiteBlackText);
                }
                if (item.id == 4) {
                    SMSJobController.SIM selectedSIM = SMSJobController.getInstance(((BaseFragment) SMSStatsActivity.this).currentAccount).getSelectedSIM();
                    if (selectedSIM != null) {
                        str = selectedSIM.name;
                    }
                    if (item.icon == 0) {
                        textCell.setTextAndValue(item.text.toString(), str, z);
                        return;
                    } else {
                        textCell.setTextAndValueAndIcon(item.text.toString(), str, item.icon, z);
                        return;
                    }
                }
                int i3 = item.icon;
                if (i3 == 0) {
                    textCell.setText(item.text, z);
                } else {
                    textCell.setTextAndIcon(item.text, i3, z);
                }
            }
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            super.onViewAttachedToWindow(viewHolder);
            if (viewHolder.getItemViewType() == 1) {
                ((TableView) viewHolder.itemView).update(false);
            }
        }

        @Override
        public int getItemCount() {
            return SMSStatsActivity.this.items.size();
        }
    };

    public static class Item extends AdapterWithDiffUtils.Item {
        public SMSJobController.JobEntry entry;
        public int icon;
        public int id;
        public boolean red;
        public CharSequence text;

        public Item(int i) {
            super(i, false);
        }

        public static Item asButton(int i, int i2, CharSequence charSequence) {
            Item item = new Item(3);
            item.id = i;
            item.icon = i2;
            item.text = charSequence;
            return item;
        }

        public static Item asSwitch(int i, CharSequence charSequence) {
            Item item = new Item(4);
            item.id = i;
            item.text = charSequence;
            return item;
        }

        public static Item asShadow(CharSequence charSequence) {
            Item item = new Item(2);
            item.text = charSequence;
            return item;
        }

        public Item makeRed() {
            this.red = true;
            return this;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || Item.class != obj.getClass()) {
                return false;
            }
            Item item = (Item) obj;
            return item.id == this.id && item.viewType == this.viewType && item.entry == this.entry && this.icon == item.icon && this.red == item.red && Objects.equals(this.text, item.text);
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.icon), this.text, Boolean.valueOf(this.red));
        }
    }

    public void updateItems() {
        int state = SMSJobController.getInstance(this.currentAccount).getState();
        this.oldItems.clear();
        this.oldItems.addAll(this.items);
        this.items.clear();
        this.items.add(new Item(0));
        this.items.add(new Item(1));
        this.items.add(Item.asShadow(null));
        this.items.add(Item.asButton(1, R.drawable.menu_intro, LocaleController.getString((int) R.string.SmsToS)));
        this.items.add(Item.asButton(2, R.drawable.menu_premium_main, LocaleController.getString((int) R.string.SmsPremiumBenefits)));
        if (state == 3 && !SMSJobController.getInstance(this.currentAccount).journal.isEmpty()) {
            this.items.add(Item.asButton(5, R.drawable.menu_sms_history, LocaleController.getString((int) R.string.SmsHistory)));
        }
        if (state == 3 && SMSJobController.getInstance(this.currentAccount).simsCount() > 1) {
            this.items.add(Item.asButton(4, R.drawable.menu_storage_path, LocaleController.getString((int) R.string.SmsActiveSim)));
        }
        this.items.add(Item.asShadow(null));
        this.items.add(Item.asSwitch(3, LocaleController.getString((int) R.string.SmsAllowInternational)));
        this.items.add(Item.asShadow(LocaleController.getString((int) R.string.SmsCostsInfo)));
        if (state != 0) {
            this.items.add(Item.asButton(6, 0, LocaleController.getString((int) R.string.SmsDeactivate)).makeRed());
        }
        this.items.add(Item.asShadow(null));
        AdapterWithDiffUtils adapterWithDiffUtils = this.adapter;
        if (adapterWithDiffUtils != null) {
            adapterWithDiffUtils.setItems(this.oldItems, this.items);
        }
    }

    public SMSStatsActivity() {
        updateItems();
    }

    @Override
    public View createView(final Context context) {
        View createView = super.createView(context);
        updateHeader();
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDurations(350L);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.listView.setItemAnimator(defaultItemAnimator);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                SMSStatsActivity.this.lambda$createView$6(context, view, i);
            }
        });
        return createView;
    }

    public void lambda$createView$6(Context context, final View view, int i) {
        if (i < 0 || i >= this.items.size()) {
            return;
        }
        Item item = this.items.get(i);
        if (item.viewType == 0) {
            if (SMSJobController.getInstance(this.currentAccount).getState() == 1) {
                SMSSubscribeSheet.requestSMSPermissions(getContext(), new Runnable() {
                    @Override
                    public final void run() {
                        SMSStatsActivity.this.lambda$createView$2();
                    }
                }, false);
                return;
            }
            return;
        }
        int i2 = item.id;
        if (i2 == 1) {
            TL_smsjobs$TL_smsjobs_status tL_smsjobs$TL_smsjobs_status = SMSJobController.getInstance(this.currentAccount).currentStatus;
            TL_smsjobs$TL_smsjobs_eligibleToJoin tL_smsjobs$TL_smsjobs_eligibleToJoin = SMSJobController.getInstance(this.currentAccount).isEligible;
            if (tL_smsjobs$TL_smsjobs_status != null) {
                Browser.openUrl(getContext(), tL_smsjobs$TL_smsjobs_status.terms_url);
            } else if (tL_smsjobs$TL_smsjobs_eligibleToJoin != null) {
                Browser.openUrl(getContext(), tL_smsjobs$TL_smsjobs_eligibleToJoin.terms_of_use);
            }
        } else if (i2 == 3) {
            if (SMSJobController.getInstance(this.currentAccount).currentState != 3) {
                return;
            }
            SMSJobController sMSJobController = SMSJobController.getInstance(this.currentAccount);
            boolean z = !this.allowInternational;
            this.allowInternational = z;
            sMSJobController.toggleAllowInternational(z);
            ((TextCell) view).setChecked(this.allowInternational);
        } else if (i2 == 2) {
            presentFragment(new PremiumPreviewFragment("sms"));
        } else if (i2 == 6) {
            AlertDialog create = new AlertDialog.Builder(getContext(), getResourceProvider()).setTitle(LocaleController.getString((int) R.string.SmsDeactivateTitle)).setMessage(LocaleController.getString((int) R.string.SmsDeactivateMessage)).setPositiveButton(LocaleController.getString((int) R.string.VoipGroupLeave), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i3) {
                    SMSStatsActivity.this.lambda$createView$4(dialogInterface, i3);
                }
            }).setNegativeButton(LocaleController.getString((int) R.string.Back), null).setDimAlpha(0.5f).create();
            showDialog(create);
            ((TextView) create.getButton(-1)).setTextColor(getThemedColor(Theme.key_text_RedBold));
        } else if (i2 != 4) {
            if (i2 == 5) {
                showDialog(new SMSHistorySheet(this));
            }
        } else {
            try {
                ArrayList<SMSJobController.SIM> sIMs = SMSJobController.getInstance(this.currentAccount).getSIMs();
                SMSJobController.SIM selectedSIM = SMSJobController.getInstance(this.currentAccount).getSelectedSIM();
                if (sIMs == null) {
                    return;
                }
                final AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                builder.setTitle(LocaleController.getString((int) R.string.SmsSelectSim));
                LinearLayout linearLayout = new LinearLayout(getParentActivity());
                linearLayout.setOrientation(1);
                builder.setView(linearLayout);
                int size = sIMs.size();
                for (int i3 = 0; i3 < size; i3++) {
                    final SMSJobController.SIM sim = sIMs.get(i3);
                    if (sim != null) {
                        LanguageCell languageCell = new LanguageCell(context);
                        languageCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
                        languageCell.setTag(Integer.valueOf(i3));
                        String str = BuildConfig.APP_CENTER_HASH;
                        if (sim.country != null) {
                            str = BuildConfig.APP_CENTER_HASH + LocationController.countryCodeToEmoji(sim.country);
                        }
                        if (!TextUtils.isEmpty(str)) {
                            str = str + " ";
                        }
                        NotificationCenter.listenEmojiLoading(languageCell.textView2);
                        languageCell.setValue(AndroidUtilities.replaceTags("**SIM" + (sim.slot + 1) + "**"), Emoji.replaceEmoji(str + sim.name, languageCell.textView2.getPaint().getFontMetricsInt(), false));
                        languageCell.setLanguageSelected(selectedSIM != null && selectedSIM.id == sim.id, false);
                        languageCell.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 2));
                        linearLayout.addView(languageCell);
                        languageCell.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public final void onClick(View view2) {
                                SMSStatsActivity.this.lambda$createView$5(sim, view, builder, view2);
                            }
                        });
                    }
                }
                builder.setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), null);
                showDialog(builder.create());
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public void lambda$createView$2() {
        SMSJobController.getInstance(this.currentAccount).checkSelectedSIMCard();
        if (SMSJobController.getInstance(this.currentAccount).getSelectedSIM() == null) {
            SMSJobController.getInstance(this.currentAccount).setState(2);
            new AlertDialog.Builder(getContext(), getResourceProvider()).setTitle(LocaleController.getString((int) R.string.SmsNoSimTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.getString((int) R.string.SmsNoSimMessage))).setPositiveButton(LocaleController.getString((int) R.string.OK), null).show();
            return;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_smsjobs$TL_smsjobs_join(), new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                SMSStatsActivity.this.lambda$createView$1(tLObject, tLRPC$TL_error);
            }
        });
    }

    public void lambda$createView$1(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SMSStatsActivity.this.lambda$createView$0(tLRPC$TL_error, tLObject);
            }
        });
    }

    public void lambda$createView$0(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error != null) {
            BulletinFactory.showError(tLRPC$TL_error);
        } else if (tLObject instanceof TLRPC$TL_boolFalse) {
            BulletinFactory.global().createErrorBulletin(LocaleController.getString((int) R.string.UnknownError)).show();
        } else {
            SMSJobController.getInstance(this.currentAccount).setState(3);
            SMSJobController.getInstance(this.currentAccount).loadStatus(true);
            SMSSubscribeSheet.showSubscribed(getContext(), getResourceProvider());
            update(true);
        }
    }

    public void lambda$createView$4(DialogInterface dialogInterface, int i) {
        finishFragment();
        if (SMSJobController.getInstance(this.currentAccount).getState() == 3) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SMSStatsActivity.this.lambda$createView$3();
                }
            }, 120L);
        } else {
            SMSJobController.getInstance(this.currentAccount).setState(0);
        }
    }

    public void lambda$createView$3() {
        SMSJobController.getInstance(this.currentAccount).leave();
    }

    public void lambda$createView$5(SMSJobController.SIM sim, View view, AlertDialog.Builder builder, View view2) {
        SMSJobController.getInstance(this.currentAccount).setSelectedSIM(sim);
        ((TextCell) view).setValue(sim.name, !LocaleController.isRTL);
        builder.getDismissRunnable().run();
    }

    private void updateHeader() {
        LimitPreviewView limitPreviewView = new LimitPreviewView(getContext(), R.drawable.msg_limit_chats, 0, 0, this.resourceProvider);
        this.limitPreviewView = limitPreviewView;
        limitPreviewView.isStatistic = true;
        limitPreviewView.setDarkGradientProvider(new LimitPreviewView.DarkGradientProvider() {
            @Override
            public final Paint setDarkGradientLocation(float f, float f2) {
                return SMSStatsActivity.this.setDarkGradientLocation(f, f2);
            }
        });
        this.aboveTitleView = new FrameLayout(getContext()) {
            {
                addView(SMSStatsActivity.this.limitPreviewView, LayoutHelper.createFrame(-1, -2.0f, 0, 8.0f, 60.0f, 8.0f, 33.0f));
            }
        };
        update(false);
    }

    private void update(boolean z) {
        int i;
        int state = SMSJobController.getInstance(this.currentAccount).getState();
        TL_smsjobs$TL_smsjobs_status tL_smsjobs$TL_smsjobs_status = SMSJobController.getInstance(this.currentAccount).currentStatus;
        TL_smsjobs$TL_smsjobs_eligibleToJoin tL_smsjobs$TL_smsjobs_eligibleToJoin = SMSJobController.getInstance(this.currentAccount).isEligible;
        if (tL_smsjobs$TL_smsjobs_status == null && !this.askedStatusToLoad) {
            SMSJobController.getInstance(this.currentAccount).loadStatus(true);
            this.askedStatusToLoad = true;
        }
        if (!this.allowInternational) {
            this.allowInternational = tL_smsjobs$TL_smsjobs_status != null && tL_smsjobs$TL_smsjobs_status.allow_international;
        }
        int i2 = tL_smsjobs$TL_smsjobs_status == null ? 0 : tL_smsjobs$TL_smsjobs_status.recent_sent;
        if (tL_smsjobs$TL_smsjobs_status == null) {
            i = tL_smsjobs$TL_smsjobs_eligibleToJoin == null ? 0 : tL_smsjobs$TL_smsjobs_eligibleToJoin.monthly_sent_sms;
        } else {
            i = tL_smsjobs$TL_smsjobs_status.recent_remains;
        }
        LimitPreviewView limitPreviewView = this.limitPreviewView;
        if (limitPreviewView != null) {
            limitPreviewView.setStatus(i2, i2 + i, z);
        }
        TableView tableView = this.table;
        if (tableView != null) {
            tableView.update(z);
        }
        if (state == 2) {
            SMSJobController.getInstance(this.currentAccount).checkSelectedSIMCard();
            configureHeader(LocaleController.getString((int) R.string.SmsStatusNoSim), LocaleController.getString((int) R.string.SmsStatusNoSimSubtitle), this.aboveTitleView, null);
        } else if (state != 1) {
            if (tL_smsjobs$TL_smsjobs_status != null && i2 >= i2 + i) {
                configureHeader(LocaleController.formatString(R.string.SmsStatusDone, Integer.valueOf(i2)), AndroidUtilities.replaceTags(LocaleController.getString((int) R.string.SmsStatusDoneSubtitle)), this.aboveTitleView, null);
            } else if (i2 == 0) {
                configureHeader(LocaleController.getString((int) R.string.SmsStatusFirst), AndroidUtilities.replaceTags(LocaleController.getString((int) R.string.SmsStatusFirstSubtitle)), this.aboveTitleView, null);
            } else {
                configureHeader(LocaleController.formatString(R.string.SmsStatusSending, Integer.valueOf(i2), Integer.valueOf(i2 + i)), AndroidUtilities.replaceTags(LocaleController.formatPluralString("SmsStatusSendingSubtitle", i, new Object[0])), this.aboveTitleView, null);
            }
        } else {
            if (getParentActivity() != null && Build.VERSION.SDK_INT >= 23 && getParentActivity().checkSelfPermission("android.permission.SEND_SMS") == 0 && getParentActivity().checkSelfPermission("android.permission.READ_PHONE_STATE") == 0 && getParentActivity().checkSelfPermission("android.permission.READ_PHONE_NUMBERS") == 0) {
                SMSJobController.getInstance(this.currentAccount).checkSelectedSIMCard();
                if (SMSJobController.getInstance(this.currentAccount).getSelectedSIM() == null) {
                    SMSJobController.getInstance(this.currentAccount).setState(2);
                    update(true);
                    new AlertDialog.Builder(getContext(), getResourceProvider()).setTitle(LocaleController.getString((int) R.string.SmsNoSimTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.getString((int) R.string.SmsNoSimMessage))).setPositiveButton(LocaleController.getString((int) R.string.OK), null).show();
                    return;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_smsjobs$TL_smsjobs_join(), new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC$TL_error tLRPC$TL_error) {
                        SMSStatsActivity.this.lambda$update$8(tLObject, tLRPC$TL_error);
                    }
                });
            }
            configureHeader(LocaleController.getString((int) R.string.SmsStatusNoPermission), LocaleController.getString((int) R.string.SmsStatusNoPermissionSubtitle), this.aboveTitleView, null);
        }
    }

    public void lambda$update$8(final TLObject tLObject, final TLRPC$TL_error tLRPC$TL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                SMSStatsActivity.this.lambda$update$7(tLRPC$TL_error, tLObject);
            }
        });
    }

    public void lambda$update$7(TLRPC$TL_error tLRPC$TL_error, TLObject tLObject) {
        if (tLRPC$TL_error != null) {
            BulletinFactory.showError(tLRPC$TL_error);
        } else if (tLObject instanceof TLRPC$TL_boolFalse) {
            BulletinFactory.global().createErrorBulletin(LocaleController.getString((int) R.string.UnknownError)).show();
        } else {
            SMSJobController.getInstance(this.currentAccount).setState(3);
            SMSJobController.getInstance(this.currentAccount).loadStatus(true);
            SMSSubscribeSheet.showSubscribed(getContext(), getResourceProvider());
            update(true);
        }
    }

    @Override
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.smsJobStatusUpdate);
        SMSJobController.getInstance(this.currentAccount).init();
        SMSJobController.getInstance(this.currentAccount).atStatisticsPage = true;
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        getNotificationCenter().removeObserver(this, NotificationCenter.smsJobStatusUpdate);
        SMSJobController.getInstance(this.currentAccount).atStatisticsPage = false;
        super.onFragmentDestroy();
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.smsJobStatusUpdate) {
            updateItems();
            update(true);
        }
    }

    @Override
    protected RecyclerView.Adapter<?> createAdapter() {
        return this.adapter;
    }

    public class TableView extends LinearLayout {
        public final int currentAccount;
        public final AnimatedTextView giftSinceDateTextView;
        public final LinkSpanDrawable.LinksTextView lastGiftLinkTextView;
        public final AnimatedTextView sentSinceDateTextView;
        public final TextView sentSinceTitleView;
        public final AnimatedTextView smsRemainingTextView;
        public final AnimatedTextView smsSentTextView;

        public TableView(Context context, int i) {
            super(context);
            this.currentAccount = i;
            setOrientation(1);
            setPadding(AndroidUtilities.dp(22.0f), 0, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(20.0f));
            TextView textView = new TextView(context);
            textView.setTextSize(1, 15.0f);
            int i2 = Theme.key_windowBackgroundWhiteBlackText;
            textView.setTextColor(Theme.getColor(i2));
            textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            textView.setText(LocaleController.getString((int) R.string.SmsOverview));
            addView(textView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            addView(linearLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 23.0f, 0.0f, 0.0f));
            LinearLayout linearLayout2 = new LinearLayout(context);
            linearLayout2.setOrientation(1);
            linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, -2, 1.0f, 119));
            LinearLayout linearLayout3 = new LinearLayout(context);
            linearLayout3.setOrientation(1);
            linearLayout.addView(linearLayout3, LayoutHelper.createLinear(-1, -2, 1.0f, 119));
            AnimatedTextView animatedTextView = new AnimatedTextView(context, false, true, true);
            this.smsSentTextView = animatedTextView;
            animatedTextView.setTextColor(Theme.getColor(i2, ((BaseFragment) SMSStatsActivity.this).resourceProvider));
            animatedTextView.setTextSize(AndroidUtilities.dp(17.0f));
            animatedTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            linearLayout2.addView(animatedTextView, LayoutHelper.createLinear(-1, 20, 4.0f, 0.0f, 4.0f, 0.0f));
            TextView textView2 = new TextView(context);
            textView2.setTextSize(1, 13.0f);
            int i3 = Theme.key_windowBackgroundWhiteGrayText4;
            textView2.setTextColor(Theme.getColor(i3));
            textView2.setText(LocaleController.getString((int) R.string.SmsTotalSent));
            linearLayout2.addView(textView2, LayoutHelper.createLinear(-1, -2, 4.0f, 0.0f, 4.0f, 0.0f));
            AnimatedTextView animatedTextView2 = new AnimatedTextView(context, false, true, true);
            this.sentSinceDateTextView = animatedTextView2;
            animatedTextView2.setTextColor(Theme.getColor(i2, ((BaseFragment) SMSStatsActivity.this).resourceProvider));
            animatedTextView2.setTextSize(AndroidUtilities.dp(17.0f));
            animatedTextView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            linearLayout3.addView(animatedTextView2, LayoutHelper.createLinear(-1, 20, 4.0f, 0.0f, 4.0f, 0.0f));
            TextView textView3 = new TextView(context);
            this.sentSinceTitleView = textView3;
            textView3.setTextSize(1, 13.0f);
            textView3.setTextColor(Theme.getColor(i3));
            textView3.setText(LocaleController.getString((int) R.string.SmsSentSince));
            linearLayout3.addView(textView3, LayoutHelper.createLinear(-1, -2, 4.0f, 0.0f, 4.0f, 0.0f));
            LinearLayout linearLayout4 = new LinearLayout(context);
            linearLayout4.setOrientation(0);
            addView(linearLayout4, LayoutHelper.createLinear(-1, -2, 0.0f, 23.0f, 0.0f, 0.0f));
            LinearLayout linearLayout5 = new LinearLayout(context);
            linearLayout5.setOrientation(1);
            linearLayout4.addView(linearLayout5, LayoutHelper.createLinear(-1, -2, 1.0f, 119));
            LinearLayout linearLayout6 = new LinearLayout(context);
            linearLayout6.setOrientation(1);
            linearLayout4.addView(linearLayout6, LayoutHelper.createLinear(-1, -2, 1.0f, 119));
            AnimatedTextView animatedTextView3 = new AnimatedTextView(context, false, true, true);
            this.smsRemainingTextView = animatedTextView3;
            animatedTextView3.setTextColor(Theme.getColor(i2, ((BaseFragment) SMSStatsActivity.this).resourceProvider));
            animatedTextView3.setTextSize(AndroidUtilities.dp(17.0f));
            animatedTextView3.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            animatedTextView3.setText("0");
            linearLayout5.addView(animatedTextView3, LayoutHelper.createLinear(-1, 20, 4.0f, 0.0f, 4.0f, 0.0f));
            TextView textView4 = new TextView(context);
            textView4.setTextSize(1, 13.0f);
            textView4.setTextColor(Theme.getColor(i3));
            textView4.setText(LocaleController.getString((int) R.string.SmsRemaining));
            linearLayout5.addView(textView4, LayoutHelper.createLinear(-1, -2, 4.0f, 0.0f, 4.0f, 0.0f));
            AnimatedTextView animatedTextView4 = new AnimatedTextView(context, false, true, true);
            this.giftSinceDateTextView = animatedTextView4;
            animatedTextView4.setTextColor(Theme.getColor(i2, ((BaseFragment) SMSStatsActivity.this).resourceProvider));
            animatedTextView4.setTextSize(AndroidUtilities.dp(17.0f));
            animatedTextView4.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            linearLayout6.addView(animatedTextView4, LayoutHelper.createLinear(-1, 20, 4.0f, 0.0f, 4.0f, 0.0f));
            LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
            this.lastGiftLinkTextView = linksTextView;
            linksTextView.setPadding(AndroidUtilities.dp(4.0f), 0, 0, 0);
            linksTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, ((BaseFragment) SMSStatsActivity.this).resourceProvider));
            linksTextView.setTextSize(1, 13.0f);
            linksTextView.setTextColor(Theme.getColor(i3));
            linksTextView.setText(LocaleController.getString((int) R.string.SmsLastGiftLink));
            linearLayout6.addView(linksTextView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 0.0f));
            update(false);
        }

        public void update(boolean z) {
            int i;
            final TL_smsjobs$TL_smsjobs_status tL_smsjobs$TL_smsjobs_status = SMSJobController.getInstance(this.currentAccount).currentStatus;
            TL_smsjobs$TL_smsjobs_eligibleToJoin tL_smsjobs$TL_smsjobs_eligibleToJoin = SMSJobController.getInstance(this.currentAccount).isEligible;
            if (tL_smsjobs$TL_smsjobs_status == null && !SMSStatsActivity.this.askedStatusToLoad) {
                SMSJobController.getInstance(this.currentAccount).loadStatus(true);
                SMSStatsActivity.this.askedStatusToLoad = true;
            }
            if (LocaleController.isRTL) {
                z = false;
            }
            if (tL_smsjobs$TL_smsjobs_status == null) {
                i = tL_smsjobs$TL_smsjobs_eligibleToJoin == null ? 0 : tL_smsjobs$TL_smsjobs_eligibleToJoin.monthly_sent_sms;
            } else {
                i = tL_smsjobs$TL_smsjobs_status.recent_remains;
            }
            AnimatedTextView animatedTextView = this.smsSentTextView;
            StringBuilder sb = new StringBuilder();
            sb.append(BuildConfig.APP_CENTER_HASH);
            sb.append(tL_smsjobs$TL_smsjobs_status == null ? 0 : tL_smsjobs$TL_smsjobs_status.total_sent);
            animatedTextView.setText(sb.toString(), z);
            this.smsRemainingTextView.setText(BuildConfig.APP_CENTER_HASH + i, z);
            if (tL_smsjobs$TL_smsjobs_status == null) {
                this.sentSinceDateTextView.setText(LocaleController.getString((int) R.string.None), z);
            } else {
                String formatDateAudio = LocaleController.formatDateAudio(tL_smsjobs$TL_smsjobs_status.total_since, false);
                if (formatDateAudio.length() > 0) {
                    formatDateAudio = formatDateAudio.substring(0, 1).toUpperCase() + formatDateAudio.substring(1);
                }
                this.sentSinceDateTextView.setText(formatDateAudio, z);
            }
            this.sentSinceTitleView.setText(LocaleController.getString((int) R.string.SmsStartDate));
            if (tL_smsjobs$TL_smsjobs_status != null && tL_smsjobs$TL_smsjobs_status.last_gift_slug != null) {
                String formatDateAudio2 = LocaleController.formatDateAudio(tL_smsjobs$TL_smsjobs_status.recent_since, false);
                if (formatDateAudio2.length() > 0) {
                    formatDateAudio2 = formatDateAudio2.substring(0, 1).toUpperCase() + formatDateAudio2.substring(1);
                }
                this.giftSinceDateTextView.setText(formatDateAudio2, z);
            } else {
                this.giftSinceDateTextView.setText(LocaleController.getString((int) R.string.None), z);
            }
            SpannableString spannableString = new SpannableString(LocaleController.getString((int) R.string.SmsLastGiftLink));
            if (tL_smsjobs$TL_smsjobs_status != null && tL_smsjobs$TL_smsjobs_status.last_gift_slug != null) {
                spannableString.setSpan(new URLSpan(BuildConfig.APP_CENTER_HASH) {
                    @Override
                    public void onClick(View view) {
                        GiftInfoBottomSheet.show(SMSStatsActivity.this, tL_smsjobs$TL_smsjobs_status.last_gift_slug);
                    }

                    @Override
                    public void updateDrawState(TextPaint textPaint) {
                        textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, ((BaseFragment) SMSStatsActivity.this).resourceProvider));
                    }
                }, 0, spannableString.length(), 17);
            }
            this.lastGiftLinkTextView.setText(spannableString);
        }
    }

    private static class JobEntryCell extends FrameLayout {
        public TextView dateTextView;
        public TextView statusTextView;

        public JobEntryCell(Context context) {
            super(context);
            TextView textView = new TextView(context);
            this.statusTextView = textView;
            textView.setTextSize(1, 16.0f);
            this.statusTextView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
            this.statusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.statusTextView.setGravity(19);
            addView(this.statusTextView, LayoutHelper.createFrame(-2, -1.0f, 3, 21.0f, 0.0f, 0.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.dateTextView = textView2;
            textView2.setTextSize(1, 15.0f);
            this.dateTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
            this.dateTextView.setGravity(21);
            addView(this.dateTextView, LayoutHelper.createFrame(-2, -1.0f, 5, 0.0f, 0.0f, 21.0f, 0.0f));
        }

        public void set(SMSJobController.JobEntry jobEntry) {
            if (jobEntry.error != null) {
                this.statusTextView.setText("Failed");
                this.statusTextView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
            } else {
                this.statusTextView.setText("Success");
                this.statusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGreenText));
            }
            TextView textView = this.dateTextView;
            LocaleController.getInstance();
            textView.setText(LocaleController.formatDate(jobEntry.date));
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
        }
    }

    public static class SMSHistorySheet extends BottomSheetWithRecyclerListView implements NotificationCenter.NotificationCenterDelegate {
        private final Paint backgroundPaint;
        private final Paint strokePaint;

        public class HeaderCell extends LinearLayout {
            public HeaderCell(SMSHistorySheet sMSHistorySheet, Context context) {
                super(context);
                setOrientation(1);
                ImageView imageView = new ImageView(context);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                imageView.setImageResource(R.drawable.large_sms_code);
                imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
                imageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(80.0f), Theme.getColor(Theme.key_featuredStickers_addButton)));
                addView(imageView, LayoutHelper.createLinear(80, 80, 1, 0, 24, 0, 12));
                TextView textView = new TextView(context);
                textView.setTextSize(1, 20.0f);
                textView.setGravity(17);
                textView.setTextAlignment(4);
                int i = Theme.key_dialogTextBlack;
                textView.setTextColor(Theme.getColor(i));
                textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                textView.setText(LocaleController.getString((int) R.string.SmsHistoryTitle));
                addView(textView, LayoutHelper.createLinear(-1, -2, 1, 50, 0, 50, 6));
                TextView textView2 = new TextView(context);
                textView2.setTextSize(1, 14.0f);
                textView2.setGravity(17);
                textView2.setTextAlignment(4);
                textView2.setTextColor(Theme.getColor(i));
                textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString((int) R.string.SmsHistorySubtitle)));
                textView2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
                addView(textView2, LayoutHelper.createLinear(-1, -2, 1, 50, 0, 50, 20));
            }

            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), i2);
            }
        }

        public class TableHeader extends FrameLayout {
            private final LinearLayout container;

            public TableHeader(Context context) {
                super(context);
                LinearLayout linearLayout = new LinearLayout(this, context, SMSHistorySheet.this) {
                    @Override
                    protected void onMeasure(int i, int i2) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i), AndroidUtilities.dp(350.0f)), 1073741824), i2);
                    }
                };
                this.container = linearLayout;
                addView(linearLayout, LayoutHelper.createFrame(-1, 37.0f, 1, 14.0f, 0.0f, 14.0f, 0.0f));
                TextView textView = new TextView(context);
                textView.setGravity(16);
                textView.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                textView.setText(LocaleController.getString((int) R.string.SmsHistoryDateCountry));
                int i = Theme.key_dialogTextBlack;
                textView.setTextColor(Theme.getColor(i));
                textView.setTextSize(1, 14.0f);
                textView.setPadding(AndroidUtilities.dp(13.0f), 0, AndroidUtilities.dp(13.0f), 0);
                linearLayout.addView(textView, LayoutHelper.createLinear(-1, 37, 30.0f, 119));
                TextView textView2 = new TextView(context);
                textView2.setGravity(16);
                textView2.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                textView2.setText(LocaleController.getString((int) R.string.SmsHistoryStatus));
                textView2.setTextColor(Theme.getColor(i));
                textView2.setTextSize(1, 14.0f);
                textView2.setPadding(AndroidUtilities.dp(13.0f), 0, AndroidUtilities.dp(13.0f), 0);
                linearLayout.addView(textView2, LayoutHelper.createLinear(-1, 37, 70.0f, 119));
            }

            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), AndroidUtilities.dp(37.0f));
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(this.container.getX() - AndroidUtilities.dpf2(0.5f), AndroidUtilities.dpf2(0.5f), this.container.getX() + this.container.getMeasuredWidth() + AndroidUtilities.dpf2(0.5f), this.container.getMeasuredHeight() + AndroidUtilities.dp(5.0f));
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), SMSHistorySheet.this.backgroundPaint);
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), SMSHistorySheet.this.strokePaint);
                canvas.drawLine(this.container.getX() - AndroidUtilities.dpf2(0.5f), this.container.getMeasuredHeight() - AndroidUtilities.dpf2(0.5f), this.container.getX() + this.container.getMeasuredWidth() + AndroidUtilities.dpf2(0.5f), this.container.getMeasuredHeight() - AndroidUtilities.dpf2(0.5f), SMSHistorySheet.this.strokePaint);
                super.dispatchDraw(canvas);
            }
        }

        public class TableCell extends FrameLayout {
            private final LinearLayout container;
            private final TextView countryTextView;
            private final TextView dateTextView;
            private boolean isLast;
            private final TextView statusTextView;

            public TableCell(Context context) {
                super(context);
                LinearLayout linearLayout = new LinearLayout(this, context, SMSHistorySheet.this) {
                    @Override
                    protected void onMeasure(int i, int i2) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i), AndroidUtilities.dp(350.0f)), 1073741824), i2);
                    }
                };
                this.container = linearLayout;
                addView(linearLayout, LayoutHelper.createFrame(-1, 50.0f, 1, 14.0f, 0.0f, 14.0f, 0.0f));
                LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(1);
                linearLayout2.setGravity(16);
                TextView textView = new TextView(context);
                this.dateTextView = textView;
                int i = Theme.key_dialogTextBlack;
                textView.setTextColor(Theme.getColor(i));
                textView.setTextSize(1, 14.0f);
                textView.setPadding(AndroidUtilities.dp(13.0f), 0, AndroidUtilities.dp(13.0f), 0);
                linearLayout2.addView(textView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 2.0f));
                TextView textView2 = new TextView(context);
                this.countryTextView = textView2;
                NotificationCenter.listenEmojiLoading(textView2);
                textView2.setTextColor(Theme.blendOver(Theme.getColor(Theme.key_dialogBackground), Theme.multAlpha(Theme.getColor(i), 0.55f)));
                textView2.setTextSize(1, 13.0f);
                textView2.setPadding(AndroidUtilities.dp(13.0f), 0, AndroidUtilities.dp(13.0f), 0);
                linearLayout2.addView(textView2, LayoutHelper.createLinear(-1, -2));
                linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-1, 50, 30.0f, 119));
                TextView textView3 = new TextView(context);
                this.statusTextView = textView3;
                textView3.setGravity(16);
                textView3.setTypeface(AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_MEDIUM));
                textView3.setTextSize(1, 14.0f);
                textView3.setPadding(AndroidUtilities.dp(13.0f), 0, AndroidUtilities.dp(13.0f), 0);
                linearLayout.addView(textView3, LayoutHelper.createLinear(-1, 50, 70.0f, 119));
            }

            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
            }

            public void setEntry(SMSJobController.JobEntry jobEntry, boolean z) {
                if (jobEntry == null) {
                    return;
                }
                TextView textView = this.dateTextView;
                textView.setText(LocaleController.getInstance().formatterGiveawayCard.format(new Date(jobEntry.date * 1000)) + ", " + LocaleController.getInstance().formatterDay.format(new Date(jobEntry.date * 1000)));
                if (!TextUtils.isEmpty(jobEntry.country)) {
                    TextView textView2 = this.countryTextView;
                    textView2.setText(Emoji.replaceEmoji(LocationController.countryCodeToEmoji(jobEntry.country) + " " + new Locale(BuildConfig.APP_CENTER_HASH, jobEntry.country).getDisplayCountry(), this.countryTextView.getPaint().getFontMetricsInt(), false));
                } else {
                    this.countryTextView.setText(BuildConfig.APP_CENTER_HASH);
                }
                if (TextUtils.isEmpty(jobEntry.error)) {
                    this.statusTextView.setTextColor(Theme.getColor(Theme.key_avatar_nameInMessageGreen));
                    this.statusTextView.setText(LocaleController.getString((int) R.string.SmsHistoryStatusSuccess));
                } else {
                    this.statusTextView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                    this.statusTextView.setText(LocaleController.getString((int) R.string.SmsHistoryStatusFailure));
                }
                this.isLast = z;
                invalidate();
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                if (!this.isLast) {
                    canvas.drawRect(this.container.getX() - AndroidUtilities.dpf2(0.5f), -AndroidUtilities.dp(1.0f), this.container.getX() + this.container.getMeasuredWidth() + AndroidUtilities.dpf2(0.5f), this.container.getMeasuredHeight() - AndroidUtilities.dpf2(0.5f), SMSHistorySheet.this.strokePaint);
                } else {
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(this.container.getX() - AndroidUtilities.dpf2(0.5f), -AndroidUtilities.dp(6.0f), this.container.getX() + this.container.getMeasuredWidth() + AndroidUtilities.dpf2(0.5f), this.container.getMeasuredHeight() - AndroidUtilities.dpf2(0.5f));
                    canvas.drawRoundRect(rectF, AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), SMSHistorySheet.this.strokePaint);
                }
                super.dispatchDraw(canvas);
            }
        }

        public SMSHistorySheet(BaseFragment baseFragment) {
            super(baseFragment, false, false);
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            Paint paint2 = new Paint(1);
            this.strokePaint = paint2;
            paint2.setStrokeWidth(AndroidUtilities.dp(1.0f));
            paint2.setStyle(Paint.Style.STROKE);
            paint2.setColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_divider, this.resourcesProvider), -1, 0.1f));
            paint.setColor(Theme.getColor(Theme.key_graySection, this.resourcesProvider));
            FrameLayout frameLayout = new FrameLayout(getContext());
            frameLayout.setBackgroundColor(getThemedColor(Theme.key_dialogBackground));
            ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(getContext(), this.resourcesProvider);
            buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SMSStatsActivity.SMSHistorySheet.this.lambda$new$0(view);
                }
            });
            buttonWithCounterView.setText(LocaleController.getString((int) R.string.Close), false);
            frameLayout.addView(buttonWithCounterView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
            View view = new View(getContext());
            view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
            frameLayout.addView(view, LayoutHelper.createFrame(-1.0f, 1.5f / AndroidUtilities.density, 55));
            this.containerView.addView(frameLayout, LayoutHelper.createFrame(-1, 68, 80));
            DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
            defaultItemAnimator.setDurations(350L);
            defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            defaultItemAnimator.setDelayAnimations(false);
            defaultItemAnimator.setSupportsChangeAnimations(false);
            this.recyclerListView.setItemAnimator(defaultItemAnimator);
        }

        public void lambda$new$0(View view) {
            dismiss();
        }

        @Override
        protected CharSequence getTitle() {
            return LocaleController.getString((int) R.string.SmsHistoryTitle);
        }

        @Override
        protected RecyclerListView.SelectionAdapter createAdapter() {
            return new RecyclerListView.SelectionAdapter() {
                @Override
                public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                    return false;
                }

                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View tableCell;
                    if (i == 0) {
                        SMSHistorySheet sMSHistorySheet = SMSHistorySheet.this;
                        tableCell = new HeaderCell(sMSHistorySheet, sMSHistorySheet.getContext());
                    } else if (i == 1) {
                        SMSHistorySheet sMSHistorySheet2 = SMSHistorySheet.this;
                        tableCell = new TableHeader(sMSHistorySheet2.getContext());
                    } else if (i == 3) {
                        tableCell = new View(this, SMSHistorySheet.this.getContext()) {
                            @Override
                            protected void onMeasure(int i2, int i3) {
                                super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(88.0f), 1073741824));
                            }
                        };
                    } else {
                        SMSHistorySheet sMSHistorySheet3 = SMSHistorySheet.this;
                        tableCell = new TableCell(sMSHistorySheet3.getContext());
                    }
                    return new RecyclerListView.Holder(tableCell);
                }

                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    int i2 = i - 2;
                    ArrayList<SMSJobController.JobEntry> arrayList = SMSJobController.getInstance(((BottomSheet) SMSHistorySheet.this).currentAccount).journal;
                    if (i2 < 0 || arrayList == null || i2 >= arrayList.size()) {
                        return;
                    }
                    ((TableCell) viewHolder.itemView).setEntry(arrayList.get(i2), i2 + 1 == arrayList.size());
                }

                @Override
                public int getItemViewType(int i) {
                    if (i == 0) {
                        return 0;
                    }
                    if (i == 1) {
                        return 1;
                    }
                    return i == getItemCount() - 1 ? 3 : 2;
                }

                @Override
                public int getItemCount() {
                    return SMSJobController.getInstance(((BottomSheet) SMSHistorySheet.this).currentAccount).journal.size() + 3;
                }
            };
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            RecyclerListView recyclerListView;
            if (i != NotificationCenter.smsJobStatusUpdate || (recyclerListView = this.recyclerListView) == null || recyclerListView.getAdapter() == null) {
                return;
            }
            this.recyclerListView.getAdapter().notifyDataSetChanged();
        }

        @Override
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.smsJobStatusUpdate);
        }

        @Override
        public void dismiss() {
            super.dismiss();
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.smsJobStatusUpdate);
        }
    }
}