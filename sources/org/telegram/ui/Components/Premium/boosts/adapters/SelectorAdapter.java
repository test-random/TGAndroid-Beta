package org.telegram.ui.Components.Premium.boosts.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.Premium.boosts.BoostRepository;
import org.telegram.ui.Components.Premium.boosts.cells.selector.SelectorCountryCell;
import org.telegram.ui.Components.Premium.boosts.cells.selector.SelectorLetterCell;
import org.telegram.ui.Components.Premium.boosts.cells.selector.SelectorUserCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerEmptyView;

public class SelectorAdapter extends AdapterWithDiffUtils {
    private HashMap chatsParticipantsCount = new HashMap();
    private final Context context;
    private boolean isGreenSelector;
    private List items;
    private RecyclerListView listView;
    public boolean needChecks;
    private final Theme.ResourcesProvider resourcesProvider;
    private GraySectionCell topSectionCell;
    private View.OnClickListener topSectionClickListener;

    public static class Item extends AdapterWithDiffUtils.Item {
        public View.OnClickListener callback;
        public TLRPC.Chat chat;
        public boolean checked;
        public TLRPC.TL_help_country country;
        public int id;
        public View.OnClickListener options;
        public int padHeight;
        public TLRPC.InputPeer peer;
        public int resId;
        public CharSequence subtext;
        public CharSequence text;
        public int type;
        public TLRPC.User user;
        public View view;

        private Item(int i, boolean z) {
            super(i, z);
            this.padHeight = -1;
        }

        public static Item asButton(int i, int i2, String str) {
            Item item = new Item(9, false);
            item.id = i;
            item.resId = i2;
            item.text = str;
            return item;
        }

        public static Item asCountry(TLRPC.TL_help_country tL_help_country, boolean z) {
            Item item = new Item(6, true);
            item.country = tL_help_country;
            item.checked = z;
            return item;
        }

        public static Item asCustom(View view) {
            Item item = new Item(10, false);
            item.view = view;
            return item;
        }

        public static Item asLetter(String str) {
            Item item = new Item(7, false);
            item.text = str;
            return item;
        }

        public static Item asNoUsers() {
            return new Item(5, false);
        }

        public static Item asPad(int i) {
            Item item = new Item(-1, false);
            item.padHeight = i;
            return item;
        }

        public static Item asPeer(TLRPC.InputPeer inputPeer, boolean z) {
            Item item = new Item(3, true);
            item.peer = inputPeer;
            item.user = null;
            item.chat = null;
            item.checked = z;
            return item;
        }

        public static Item asTopSection(CharSequence charSequence) {
            Item item = new Item(8, false);
            item.text = charSequence;
            return item;
        }

        public static Item asUser(TLRPC.User user, boolean z) {
            Item item = new Item(3, true);
            item.user = user;
            item.peer = null;
            item.chat = null;
            item.checked = z;
            return item;
        }

        @Override
        public boolean contentsEquals(AdapterWithDiffUtils.Item item) {
            if (this == item) {
                return true;
            }
            if (item == null || getClass() != item.getClass()) {
                return false;
            }
            Item item2 = (Item) item;
            if (this.checked != item2.checked) {
                return false;
            }
            if (this.viewType != 8) {
                return true;
            }
            if (TextUtils.equals(this.subtext, item2.subtext)) {
                if ((this.callback == null) == (item2.callback == null)) {
                    return true;
                }
            }
            return false;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Item item = (Item) obj;
            int i = this.viewType;
            if (i != item.viewType) {
                return false;
            }
            if (i == -1 && this.padHeight != item.padHeight) {
                return false;
            }
            if (i == 3 && (getDialogId() != item.getDialogId() || this.type != item.type)) {
                return false;
            }
            int i2 = this.viewType;
            if (i2 == 6 && this.country != item.country) {
                return false;
            }
            if (i2 == 7 && !TextUtils.equals(this.text, item.text)) {
                return false;
            }
            if (this.viewType == 8 && !TextUtils.equals(this.text, item.text)) {
                return false;
            }
            if (this.viewType != 9 || (TextUtils.equals(this.text, item.text) && this.id == item.id && this.resId == item.resId)) {
                return this.viewType != 10 || this.view == item.view;
            }
            return false;
        }

        public long getDialogId() {
            TLRPC.User user = this.user;
            if (user != null) {
                return user.id;
            }
            TLRPC.Chat chat = this.chat;
            if (chat != null) {
                return -chat.id;
            }
            TLRPC.InputPeer inputPeer = this.peer;
            if (inputPeer != null) {
                return DialogObject.getPeerDialogId(inputPeer);
            }
            return 0L;
        }

        public Item withOptions(View.OnClickListener onClickListener) {
            this.options = onClickListener;
            return this;
        }

        public Item withRightText(String str, View.OnClickListener onClickListener) {
            this.subtext = str;
            this.callback = onClickListener;
            return this;
        }
    }

    public SelectorAdapter(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
        this.context = context;
        this.needChecks = z;
        this.resourcesProvider = resourcesProvider;
        BoostRepository.loadParticipantsCount(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                SelectorAdapter.this.lambda$new$0((HashMap) obj);
            }
        });
    }

    public void lambda$new$0(HashMap hashMap) {
        this.chatsParticipantsCount.clear();
        this.chatsParticipantsCount.putAll(hashMap);
    }

    @Override
    public int getItemCount() {
        List list = this.items;
        if (list == null) {
            return 0;
        }
        return list.size();
    }

    @Override
    public int getItemViewType(int i) {
        List list = this.items;
        if (list == null || i < 0) {
            return -1;
        }
        return ((Item) list.get(i)).viewType;
    }

    public int getParticipantsCount(TLRPC.Chat chat) {
        Integer num;
        int i;
        TLRPC.ChatFull chatFull = MessagesController.getInstance(UserConfig.selectedAccount).getChatFull(chat.id);
        return (chatFull == null || (i = chatFull.participants_count) <= 0) ? (this.chatsParticipantsCount.isEmpty() || (num = (Integer) this.chatsParticipantsCount.get(Long.valueOf(chat.id))) == null) ? chat.participants_count : num.intValue() : i;
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        return viewHolder.getItemViewType() == 3 || viewHolder.getItemViewType() == 6 || viewHolder.getItemViewType() == 9;
    }

    public void notifyChangedLast() {
        List list = this.items;
        if (list == null || list.isEmpty()) {
            return;
        }
        notifyItemChanged(this.items.size() - 1);
    }

    @Override
    public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r10, int r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.boosts.adapters.SelectorAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        if (i == -1) {
            view = new View(this.context);
        } else if (i == 3) {
            view = new SelectorUserCell(this.context, this.needChecks, this.resourcesProvider, this.isGreenSelector);
        } else if (i == 5) {
            StickerEmptyView stickerEmptyView = new StickerEmptyView(this.context, null, 1, this.resourcesProvider);
            stickerEmptyView.title.setText(LocaleController.getString(R.string.NoResult));
            stickerEmptyView.subtitle.setText(LocaleController.getString(R.string.SearchEmptyViewFilteredSubtitle2));
            stickerEmptyView.linearLayout.setTranslationY(AndroidUtilities.dp(24.0f));
            view = stickerEmptyView;
        } else if (i == 7) {
            view = new SelectorLetterCell(this.context, this.resourcesProvider);
        } else if (i == 6) {
            view = new SelectorCountryCell(this.context, this.resourcesProvider);
        } else if (i == 8) {
            view = new GraySectionCell(this.context, this.resourcesProvider);
        } else if (i == 9) {
            TextCell textCell = new TextCell(this.context, this.resourcesProvider);
            textCell.leftPadding = 16;
            textCell.imageLeft = 19;
            view = textCell;
        } else {
            view = i == 10 ? new FrameLayout(this.context) : new View(this.context);
        }
        return new RecyclerListView.Holder(view);
    }

    public void setData(List list, RecyclerListView recyclerListView) {
        this.items = list;
        this.listView = recyclerListView;
    }

    public void setGreenSelector(boolean z) {
        this.isGreenSelector = z;
    }

    public void setTopSectionClickListener(View.OnClickListener onClickListener) {
        this.topSectionClickListener = onClickListener;
        GraySectionCell graySectionCell = this.topSectionCell;
        if (graySectionCell != null) {
            if (onClickListener == null) {
                graySectionCell.setRightText(null);
            } else {
                graySectionCell.setRightText(LocaleController.getString(R.string.UsersDeselectAll), true, onClickListener);
            }
        }
    }
}
