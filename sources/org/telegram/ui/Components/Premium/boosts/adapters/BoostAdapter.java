package org.telegram.ui.Components.Premium.boosts.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.Premium.boosts.BoostRepository;
import org.telegram.ui.Components.Premium.boosts.cells.AddChannelCell;
import org.telegram.ui.Components.Premium.boosts.cells.BoostTypeCell;
import org.telegram.ui.Components.Premium.boosts.cells.BoostTypeSingleCell;
import org.telegram.ui.Components.Premium.boosts.cells.ChatCell;
import org.telegram.ui.Components.Premium.boosts.cells.DateEndCell;
import org.telegram.ui.Components.Premium.boosts.cells.DurationCell;
import org.telegram.ui.Components.Premium.boosts.cells.EnterPrizeCell;
import org.telegram.ui.Components.Premium.boosts.cells.HeaderCell;
import org.telegram.ui.Components.Premium.boosts.cells.ParticipantsTypeCell;
import org.telegram.ui.Components.Premium.boosts.cells.SliderCell;
import org.telegram.ui.Components.Premium.boosts.cells.StarGiveawayOptionCell;
import org.telegram.ui.Components.Premium.boosts.cells.SubtitleWithCounterCell;
import org.telegram.ui.Components.Premium.boosts.cells.SwitcherCell;
import org.telegram.ui.Components.Premium.boosts.cells.TextInfoCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SlideChooseView;
import org.telegram.ui.Stars.StarsIntroActivity;

public class BoostAdapter extends AdapterWithDiffUtils {
    private EnterPrizeCell.AfterTextChangedListener afterTextChangedListener;
    private ChatCell.ChatDeleteListener chatDeleteListener;
    private TLRPC.Chat currentChat;
    private HeaderCell headerCell;
    private RecyclerListView recyclerListView;
    private final Theme.ResourcesProvider resourcesProvider;
    private SlideChooseView.Callback sliderCallback;
    private List items = new ArrayList();
    private HashMap chatsParticipantsCount = new HashMap();

    public static class Item extends AdapterWithDiffUtils.Item {
        public boolean boolValue;
        public TLRPC.Chat chat;
        public float floatValue;
        public int intValue;
        public int intValue2;
        public int intValue3;
        public long longValue;
        public Object object;
        public TLRPC.InputPeer peer;
        public int subType;
        public CharSequence text;
        public Object user;
        public List values;

        private Item(int i, boolean z) {
            super(i, z);
        }

        public static Item asAddChannel() {
            return new Item(8, false);
        }

        public static Item asBoost(int i, int i2, Object obj, int i3) {
            Item item = new Item(2, i3 == i);
            item.subType = i;
            item.intValue = i2;
            item.user = obj;
            return item;
        }

        public static Item asChat(TLRPC.Chat chat, boolean z, int i) {
            Item item = new Item(9, false);
            item.chat = chat;
            item.peer = null;
            item.boolValue = z;
            item.intValue = i;
            return item;
        }

        public static Item asDateEnd(long j) {
            Item item = new Item(10, false);
            item.longValue = j;
            return item;
        }

        public static Item asDivider() {
            return new Item(4, false);
        }

        public static Item asDivider(CharSequence charSequence, boolean z) {
            Item item = new Item(7, false);
            item.text = charSequence;
            item.boolValue = z;
            return item;
        }

        public static Item asDuration(Object obj, int i, int i2, long j, int i3, String str, boolean z) {
            Item item = new Item(12, i == i3);
            item.intValue = i;
            item.intValue2 = i2;
            item.longValue = j;
            item.boolValue = z;
            item.text = str;
            item.object = obj;
            return item;
        }

        public static Item asEnterPrize(int i) {
            Item item = new Item(16, false);
            item.intValue = i;
            return item;
        }

        public static Item asExpandOptions() {
            return new Item(18, false);
        }

        public static Item asHeader(boolean z) {
            Item item = new Item(0, false);
            item.boolValue = z;
            return item;
        }

        public static Item asOption(TL_stars.TL_starsGiveawayOption tL_starsGiveawayOption, int i, long j, boolean z, boolean z2) {
            Item item = new Item(17, z);
            item.intValue = i;
            item.longValue = j;
            item.object = tL_starsGiveawayOption;
            item.boolValue = z2;
            return item;
        }

        public static Item asParticipants(int i, int i2, boolean z, List list) {
            Item item = new Item(11, i2 == i);
            item.subType = i;
            item.boolValue = z;
            item.user = list;
            return item;
        }

        public static Item asPeer(TLRPC.InputPeer inputPeer, boolean z, int i) {
            Item item = new Item(9, false);
            item.peer = inputPeer;
            item.chat = null;
            item.boolValue = z;
            item.intValue = i;
            return item;
        }

        public static Item asSingleBoost(Object obj) {
            Item item = new Item(14, false);
            item.user = obj;
            return item;
        }

        public static Item asSlider(List list, int i) {
            Item item = new Item(5, false);
            item.values = list;
            item.intValue = i;
            return item;
        }

        public static Item asSubTitle(CharSequence charSequence) {
            Item item = new Item(6, false);
            item.text = charSequence;
            return item;
        }

        public static Item asSubTitleWithCounter(CharSequence charSequence, int i) {
            Item item = new Item(13, false);
            item.text = charSequence;
            item.intValue = i;
            return item;
        }

        public static Item asSwitcher(CharSequence charSequence, boolean z, boolean z2, int i) {
            Item item = new Item(15, z);
            item.text = charSequence;
            item.boolValue = z2;
            item.subType = i;
            return item;
        }

        public static boolean eq(List list, List list2) {
            if (list == null && list2 == null) {
                return true;
            }
            if (list == null || list2 == null || list.size() != list2.size()) {
                return false;
            }
            for (int i = 0; i < list.size(); i++) {
                if (((Integer) list.get(i)).intValue() != ((Integer) list2.get(i)).intValue()) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public boolean contentsEquals(AdapterWithDiffUtils.Item item) {
            Item item2;
            int i;
            int i2;
            if (this == item) {
                return true;
            }
            if (item != null && getClass() == item.getClass() && (i = (item2 = (Item) item).viewType) == (i2 = this.viewType)) {
                return i2 == 0 ? this.boolValue == item2.boolValue : i == 17 ? this.intValue == item2.intValue && this.longValue == item2.longValue && this.object == item2.object && this.boolValue == item2.boolValue && this.selectable == item2.selectable : i2 == 5 ? this.intValue == item2.intValue && eq(this.values, item2.values) : i2 == 13 && this.intValue == item2.intValue && TextUtils.equals(this.text, item2.text);
            }
            return false;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && getClass() == obj.getClass()) {
                Item item = (Item) obj;
                int i = this.viewType;
                if (i != item.viewType) {
                    return false;
                }
                if (i == 0) {
                    return true;
                }
                if (i == 17) {
                    return this.intValue == item.intValue && this.object == item.object;
                }
                if (i == 5) {
                    return eq(this.values, item.values);
                }
                if (i == 13) {
                    return TextUtils.equals(this.text, item.text);
                }
                if (this.chat == item.chat && this.user == item.user && this.peer == item.peer && this.object == item.object && this.boolValue == item.boolValue && this.intValue == item.intValue && this.intValue2 == item.intValue2 && this.intValue3 == item.intValue3 && this.longValue == item.longValue && this.subType == item.subType && this.floatValue == item.floatValue && TextUtils.equals(this.text, item.text)) {
                    return true;
                }
            }
            return false;
        }
    }

    public BoostAdapter(Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
        BoostRepository.loadParticipantsCount(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                BoostAdapter.this.lambda$new$0((HashMap) obj);
            }
        });
    }

    private int getParticipantsCount(TLRPC.Chat chat) {
        Integer num;
        int i;
        TLRPC.ChatFull chatFull = MessagesController.getInstance(UserConfig.selectedAccount).getChatFull(chat.id);
        return (chatFull == null || (i = chatFull.participants_count) <= 0) ? (this.chatsParticipantsCount.isEmpty() || (num = (Integer) this.chatsParticipantsCount.get(Long.valueOf(chat.id))) == null) ? chat.participants_count : num.intValue() : i;
    }

    public void lambda$new$0(HashMap hashMap) {
        this.chatsParticipantsCount.clear();
        this.chatsParticipantsCount.putAll(hashMap);
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @Override
    public int getItemViewType(int i) {
        return ((Item) this.items.get(i)).viewType;
    }

    @Override
    public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
        int itemViewType = viewHolder.getItemViewType();
        return itemViewType == 2 || itemViewType == 11 || itemViewType == 8 || itemViewType == 10 || itemViewType == 15 || itemViewType == 12 || itemViewType == 17 || itemViewType == 18;
    }

    public void notifyAdditionalPrizeItem(boolean z) {
        for (int i = 0; i < this.items.size(); i++) {
            Item item = (Item) this.items.get(i);
            if (item.viewType == 15 && item.subType == SwitcherCell.TYPE_ADDITION_PRIZE) {
                int i2 = i + 1;
                if (z) {
                    notifyItemInserted(i2);
                    return;
                } else {
                    notifyItemRemoved(i2);
                    return;
                }
            }
        }
    }

    public void notifyAllVisibleTextDividers() {
        for (int i = 0; i < this.items.size(); i++) {
            if (((Item) this.items.get(i)).viewType == 7) {
                notifyItemChanged(i);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        TLRPC.Chat chat;
        MessagesController messagesController;
        long j;
        int itemViewType = viewHolder.getItemViewType();
        Item item = (Item) this.items.get(i);
        if (itemViewType == 0) {
            HeaderCell headerCell = (HeaderCell) viewHolder.itemView;
            this.headerCell = headerCell;
            headerCell.setBoostViaGifsText(this.currentChat);
            this.headerCell.setStars(item.boolValue);
            return;
        }
        if (itemViewType == 2) {
            ((BoostTypeCell) viewHolder.itemView).setType(item.subType, item.intValue, (TLRPC.User) item.user, item.selectable);
            return;
        }
        if (itemViewType == 5) {
            SliderCell sliderCell = (SliderCell) viewHolder.itemView;
            sliderCell.setValues(item.values, item.intValue);
            sliderCell.setCallBack(this.sliderCallback);
            return;
        }
        if (itemViewType == 6) {
            ((org.telegram.ui.Cells.HeaderCell) viewHolder.itemView).setText(item.text);
            return;
        }
        if (itemViewType == 7) {
            TextInfoCell textInfoCell = (TextInfoCell) viewHolder.itemView;
            textInfoCell.setText(item.text);
            textInfoCell.setBackground(item.boolValue);
            return;
        }
        switch (itemViewType) {
            case 9:
                ChatCell chatCell = (ChatCell) viewHolder.itemView;
                TLRPC.InputPeer inputPeer = item.peer;
                if (inputPeer != null) {
                    if (!(inputPeer instanceof TLRPC.TL_inputPeerChat)) {
                        if (inputPeer instanceof TLRPC.TL_inputPeerChannel) {
                            messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
                            j = inputPeer.channel_id;
                        }
                        chatCell.setChatDeleteListener(this.chatDeleteListener);
                        return;
                    }
                    messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
                    j = inputPeer.chat_id;
                    chat = messagesController.getChat(Long.valueOf(j));
                } else {
                    chat = item.chat;
                }
                chatCell.setChat(chat, item.intValue, item.boolValue, getParticipantsCount(chat));
                chatCell.setChatDeleteListener(this.chatDeleteListener);
                return;
            case 10:
                ((DateEndCell) viewHolder.itemView).setDate(item.longValue);
                return;
            case 11:
                ((ParticipantsTypeCell) viewHolder.itemView).setType(item.subType, item.selectable, item.boolValue, (List) item.user, this.currentChat);
                return;
            case 12:
                ((DurationCell) viewHolder.itemView).setDuration(item.object, item.intValue, item.intValue2, item.longValue, item.text, item.boolValue, item.selectable);
                return;
            case 13:
                SubtitleWithCounterCell subtitleWithCounterCell = (SubtitleWithCounterCell) viewHolder.itemView;
                subtitleWithCounterCell.setText(item.text);
                subtitleWithCounterCell.updateCounter(true, item.intValue);
                return;
            case 14:
                ((BoostTypeSingleCell) viewHolder.itemView).setGiveaway((TL_stories.PrepaidGiveaway) item.user);
                return;
            case 15:
                ((SwitcherCell) viewHolder.itemView).setData(item.text, item.selectable, item.boolValue, item.subType);
                return;
            case 16:
                EnterPrizeCell enterPrizeCell = (EnterPrizeCell) viewHolder.itemView;
                enterPrizeCell.setCount(item.intValue);
                enterPrizeCell.setAfterTextChangedListener(this.afterTextChangedListener);
                return;
            case 17:
                StarGiveawayOptionCell starGiveawayOptionCell = (StarGiveawayOptionCell) viewHolder.itemView;
                Object obj = item.object;
                starGiveawayOptionCell.setOption(obj == null ? null : (TL_stars.TL_starsGiveawayOption) obj, item.intValue, item.longValue, item.selectable, item.boolValue);
                return;
            default:
                return;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view;
        View view2;
        Context context = viewGroup.getContext();
        switch (i) {
            case 2:
                view2 = new BoostTypeCell(context, this.resourcesProvider);
                break;
            case 3:
                view2 = new View(context);
                break;
            case 4:
                view2 = new ShadowSectionCell(context, 12, Theme.getColor(Theme.key_windowBackgroundGray, this.resourcesProvider));
                break;
            case 5:
                view2 = new SliderCell(context, this.resourcesProvider);
                break;
            case 6:
                view = new org.telegram.ui.Cells.HeaderCell(context, Theme.key_windowBackgroundWhiteBlueHeader, 21, 15, 3, false, this.resourcesProvider);
                view.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, this.resourcesProvider));
                view2 = view;
                break;
            case 7:
                view2 = new TextInfoCell(context, this.resourcesProvider);
                break;
            case 8:
                view2 = new AddChannelCell(context, this.resourcesProvider);
                break;
            case 9:
                view2 = new ChatCell(context, this.resourcesProvider);
                break;
            case 10:
                view2 = new DateEndCell(context, this.resourcesProvider);
                break;
            case 11:
                view2 = new ParticipantsTypeCell(context, this.resourcesProvider);
                break;
            case 12:
                view2 = new DurationCell(context, this.resourcesProvider);
                break;
            case 13:
                view = new SubtitleWithCounterCell(context, this.resourcesProvider);
                view.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground, this.resourcesProvider));
                view2 = view;
                break;
            case 14:
                view2 = new BoostTypeSingleCell(context, this.resourcesProvider);
                break;
            case 15:
                SwitcherCell switcherCell = new SwitcherCell(context, this.resourcesProvider);
                switcherCell.setHeight(50);
                view2 = switcherCell;
                break;
            case 16:
                view2 = new EnterPrizeCell(context, this.resourcesProvider);
                break;
            case 17:
                view2 = new StarGiveawayOptionCell(context, this.resourcesProvider);
                break;
            case 18:
                StarsIntroActivity.ExpandView expandView = new StarsIntroActivity.ExpandView(context, this.resourcesProvider);
                expandView.set(LocaleController.getString(R.string.NotifyMoreOptions), true, true, false);
                view2 = expandView;
                break;
            default:
                view2 = new HeaderCell(context, this.resourcesProvider);
                break;
        }
        view2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        return new RecyclerListView.Holder(view2);
    }

    public void setItems(TLRPC.Chat chat, List list, RecyclerListView recyclerListView, SlideChooseView.Callback callback, ChatCell.ChatDeleteListener chatDeleteListener, EnterPrizeCell.AfterTextChangedListener afterTextChangedListener) {
        this.items = list;
        this.currentChat = chat;
        this.recyclerListView = recyclerListView;
        this.sliderCallback = callback;
        this.chatDeleteListener = chatDeleteListener;
        this.afterTextChangedListener = afterTextChangedListener;
    }

    public void setPausedStars(boolean z) {
        HeaderCell headerCell = this.headerCell;
        if (headerCell != null) {
            headerCell.setPaused(z);
        }
    }

    public void updateBoostCounter(int i) {
        for (int i2 = 0; i2 < this.recyclerListView.getChildCount(); i2++) {
            View childAt = this.recyclerListView.getChildAt(i2);
            if (childAt instanceof SubtitleWithCounterCell) {
                ((SubtitleWithCounterCell) childAt).updateCounter(true, i);
            }
            if (childAt instanceof ChatCell) {
                ChatCell chatCell = (ChatCell) childAt;
                chatCell.setCounter(i, getParticipantsCount(chatCell.getChat()));
            }
        }
        notifyItemChanged(8);
        notifyItemRangeChanged(this.items.size() - 12, 12);
    }
}
