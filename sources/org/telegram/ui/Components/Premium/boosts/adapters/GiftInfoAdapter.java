package org.telegram.ui.Components.Premium.boosts.adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Date;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC$TL_error;
import org.telegram.tgnet.TLRPC$TL_payments_checkedGiftCode;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Premium.boosts.BoostDialogs;
import org.telegram.ui.Components.Premium.boosts.BoostRepository;
import org.telegram.ui.Components.Premium.boosts.cells.ActionBtnCell;
import org.telegram.ui.Components.Premium.boosts.cells.HeaderCell;
import org.telegram.ui.Components.Premium.boosts.cells.LinkCell;
import org.telegram.ui.Components.Premium.boosts.cells.TableCell;
import org.telegram.ui.Components.Premium.boosts.cells.TextInfoCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.TopicsFragment;
public abstract class GiftInfoAdapter extends RecyclerListView.SelectionAdapter {
    private BaseFragment baseFragment;
    private TLRPC$TL_payments_checkedGiftCode giftCode;
    private boolean isUnused;
    private final Theme.ResourcesProvider resourcesProvider;
    private String slug;

    protected abstract void afterCodeApplied();

    protected abstract void dismiss();

    @Override
    public int getItemCount() {
        return 5;
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return false;
    }

    public abstract void onObjectClicked(TLObject tLObject);

    public GiftInfoAdapter(Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
    }

    public void init(BaseFragment baseFragment, TLRPC$TL_payments_checkedGiftCode tLRPC$TL_payments_checkedGiftCode, String str) {
        this.isUnused = tLRPC$TL_payments_checkedGiftCode.used_date == 0;
        this.baseFragment = baseFragment;
        this.giftCode = tLRPC$TL_payments_checkedGiftCode;
        this.slug = str;
    }

    private boolean hasSlug() {
        String str = this.slug;
        return (str == null || TextUtils.isEmpty(str)) ? false : true;
    }

    @Override
    public int getItemViewType(int i) {
        if (i != 0) {
            if (i == 1) {
                return !hasSlug() ? 5 : 1;
            }
            int i2 = 2;
            if (i != 2) {
                i2 = 3;
                if (i != 3) {
                    i2 = 4;
                    if (i != 4) {
                        return 5;
                    }
                }
            }
            return i2;
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View linkCell;
        Context context = viewGroup.getContext();
        if (i == 1) {
            linkCell = new LinkCell(context, this.baseFragment, this.resourcesProvider);
        } else if (i == 2) {
            linkCell = new TableCell(context, this.resourcesProvider);
        } else if (i == 3) {
            linkCell = new TextInfoCell(context, this.resourcesProvider);
        } else if (i == 4) {
            linkCell = new ActionBtnCell(context, this.resourcesProvider);
        } else if (i != 5) {
            linkCell = new HeaderCell(context, this.resourcesProvider);
        } else {
            linkCell = new View(context);
        }
        linkCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(linkCell);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int itemViewType = viewHolder.getItemViewType();
        if (itemViewType == 0) {
            HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
            if (this.isUnused) {
                headerCell.setGiftLinkText();
            } else {
                headerCell.setUsedGiftLinkText();
            }
            if (this.giftCode.boost != null) {
                headerCell.setGiftLinkText();
            }
        } else if (itemViewType == 1) {
            ((LinkCell) viewHolder.itemView).setSlug(this.slug);
        } else if (itemViewType == 2) {
            ((TableCell) viewHolder.itemView).setData(this.giftCode, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    GiftInfoAdapter.this.onObjectClicked((TLObject) obj);
                }
            });
        } else if (itemViewType != 3) {
            if (itemViewType != 4) {
                return;
            }
            final ActionBtnCell actionBtnCell = (ActionBtnCell) viewHolder.itemView;
            actionBtnCell.setOkStyle(this.isUnused);
            actionBtnCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    GiftInfoAdapter.this.lambda$onBindViewHolder$4(actionBtnCell, view);
                }
            });
            if (this.giftCode.boost != null) {
                actionBtnCell.setOkStyle(false);
                actionBtnCell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        GiftInfoAdapter.this.lambda$onBindViewHolder$5(view);
                    }
                });
            }
        } else {
            TextInfoCell textInfoCell = (TextInfoCell) viewHolder.itemView;
            textInfoCell.setTextGravity(17);
            textInfoCell.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            textInfoCell.setTopPadding(14);
            textInfoCell.setBottomPadding(15);
            if (!hasSlug()) {
                textInfoCell.setVisibility(4);
            }
            if (this.isUnused) {
                textInfoCell.setText(AndroidUtilities.replaceSingleTag(LocaleController.getString("BoostingSendLinkToFriends", R.string.BoostingSendLinkToFriends), Theme.key_chat_messageLinkIn, 0, new Runnable() {
                    @Override
                    public final void run() {
                        GiftInfoAdapter.this.lambda$onBindViewHolder$1();
                    }
                }, this.resourcesProvider));
                return;
            }
            Date date = new Date(this.giftCode.used_date * 1000);
            textInfoCell.setText(LocaleController.formatString("BoostingUsedLinkDate", R.string.BoostingUsedLinkDate, LocaleController.formatString("formatDateAtTime", R.string.formatDateAtTime, LocaleController.getInstance().formatterYear.format(date), LocaleController.getInstance().formatterDay.format(date))));
        }
    }

    public void lambda$onBindViewHolder$1() {
        final String str = "https://t.me/giftcode/" + this.slug;
        Bundle bundle = new Bundle();
        bundle.putBoolean("onlySelect", true);
        bundle.putInt("dialogsType", 3);
        DialogsActivity dialogsActivity = new DialogsActivity(bundle);
        dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
            @Override
            public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, TopicsFragment topicsFragment) {
                boolean lambda$onBindViewHolder$0;
                lambda$onBindViewHolder$0 = GiftInfoAdapter.this.lambda$onBindViewHolder$0(str, dialogsActivity2, arrayList, charSequence, z, topicsFragment);
                return lambda$onBindViewHolder$0;
            }
        });
        this.baseFragment.presentFragment(dialogsActivity);
    }

    public boolean lambda$onBindViewHolder$0(String str, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, TopicsFragment topicsFragment) {
        long j = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            j = ((MessagesStorage.TopicKey) arrayList.get(i)).dialogId;
            this.baseFragment.getSendMessagesHelper().sendMessage(SendMessagesHelper.SendMessageParams.of(str, j, null, null, null, true, null, null, null, true, 0, null, false));
        }
        dialogsActivity.finishFragment();
        BoostDialogs.showGiftLinkForwardedBulletin(j);
        return true;
    }

    public void lambda$onBindViewHolder$4(final ActionBtnCell actionBtnCell, View view) {
        if (this.isUnused) {
            if (actionBtnCell.isLoading()) {
                return;
            }
            actionBtnCell.updateLoading(true);
            BoostRepository.applyGiftCode(this.slug, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    GiftInfoAdapter.this.lambda$onBindViewHolder$2(actionBtnCell, (Void) obj);
                }
            }, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    GiftInfoAdapter.this.lambda$onBindViewHolder$3(actionBtnCell, (TLRPC$TL_error) obj);
                }
            });
            return;
        }
        dismiss();
    }

    public void lambda$onBindViewHolder$2(ActionBtnCell actionBtnCell, Void r2) {
        actionBtnCell.updateLoading(false);
        afterCodeApplied();
        dismiss();
    }

    public void lambda$onBindViewHolder$3(ActionBtnCell actionBtnCell, TLRPC$TL_error tLRPC$TL_error) {
        actionBtnCell.updateLoading(false);
        BoostDialogs.showToastError(this.baseFragment.getContext(), tLRPC$TL_error);
    }

    public void lambda$onBindViewHolder$5(View view) {
        dismiss();
    }
}