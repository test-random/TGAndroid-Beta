package org.telegram.ui.Gifts;

import android.content.Context;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.RecyclerView;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BirthdayController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.boosts.UserSelectorBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.Stars.StarGiftSheet;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public abstract class ProfileGiftsContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private final ButtonWithCounterView button;
    private final FrameLayout buttonContainer;
    private final int currentAccount;
    private final StarsController.GiftsList list;
    private final UniversalRecyclerView listView;
    private final Theme.ResourcesProvider resourcesProvider;
    private final long userId;
    private int visibleHeight;

    public static class TextFactory extends UItem.UItemFactory {
        static {
            UItem.UItemFactory.setup(new TextFactory());
        }

        public static UItem asText(int i, int i2, float f, CharSequence charSequence, boolean z, int i3) {
            UItem ofFactory = UItem.ofFactory(TextFactory.class);
            ofFactory.text = charSequence;
            ofFactory.intValue = i2;
            ofFactory.longValue = i;
            ofFactory.floatValue = f;
            ofFactory.pad = i3;
            ofFactory.checked = z;
            return ofFactory;
        }

        @Override
        public void bindView(View view, UItem uItem, boolean z) {
            LinkSpanDrawable.LinksTextView linksTextView = (LinkSpanDrawable.LinksTextView) view;
            linksTextView.setGravity(uItem.intValue);
            linksTextView.setTextColor((int) uItem.longValue);
            linksTextView.setTextSize(1, uItem.floatValue);
            linksTextView.setTypeface(uItem.checked ? null : AndroidUtilities.bold());
            int i = uItem.pad;
            linksTextView.setPadding(i, 0, i, 0);
            linksTextView.setText(uItem.text);
        }

        @Override
        public LinkSpanDrawable.LinksTextView createView(Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
            return new LinkSpanDrawable.LinksTextView(context) {
                @Override
                public void onMeasure(int i3, int i4) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i3), 1073741824), i4);
                }
            };
        }
    }

    public ProfileGiftsContainer(Context context, final int i, long j, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.visibleHeight = AndroidUtilities.displaySize.y;
        this.currentAccount = i;
        this.userId = j;
        StarsController.GiftsList profileGiftsList = StarsController.getInstance(i).getProfileGiftsList(j);
        this.list = profileGiftsList;
        profileGiftsList.shown = true;
        profileGiftsList.load();
        this.resourcesProvider = resourcesProvider;
        int i2 = Theme.key_windowBackgroundWhite;
        setBackgroundColor(Theme.blendOver(Theme.getColor(i2, resourcesProvider), Theme.multAlpha(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, resourcesProvider), 0.04f)));
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(context, i, 0, false, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                ProfileGiftsContainer.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() {
            @Override
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                ProfileGiftsContainer.this.onItemClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, new Utilities.Callback5Return() {
            @Override
            public final Object run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                return Boolean.valueOf(ProfileGiftsContainer.this.onItemLongPress((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue()));
            }
        }, resourcesProvider, 3);
        this.listView = universalRecyclerView;
        universalRecyclerView.adapter.setApplyBackground(false);
        universalRecyclerView.setSelectorType(9);
        universalRecyclerView.setSelectorDrawableColor(0);
        universalRecyclerView.setPadding(AndroidUtilities.dp(9.0f), 0, AndroidUtilities.dp(9.0f), 0);
        addView(universalRecyclerView, LayoutHelper.createFrame(-1, -1, 119));
        universalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i3, int i4) {
                if (!ProfileGiftsContainer.this.listView.canScrollVertically(1) || ProfileGiftsContainer.this.isLoadingVisible()) {
                    ProfileGiftsContainer.this.list.load();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.buttonContainer = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(i2, resourcesProvider));
        addView(frameLayout, LayoutHelper.createFrame(-1, -2, 87));
        View view = new View(context);
        view.setBackgroundColor(Theme.getColor(Theme.key_dialogGrayLine, resourcesProvider));
        frameLayout.addView(view, LayoutHelper.createFrame(-1.0f, 1.0f / AndroidUtilities.density, 55));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        this.button = buttonWithCounterView;
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("G  " + LocaleController.getString(R.string.ProfileGiftsSend));
        spannableStringBuilder.setSpan(new ColoredImageSpan(R.drawable.filled_gift_premium), 0, 1, 33);
        buttonWithCounterView.setText(spannableStringBuilder, false);
        frameLayout.addView(buttonWithCounterView, LayoutHelper.createFrame(-1, 48.0f, 119, 10.0f, (1.0f / AndroidUtilities.density) + 10.0f, 10.0f, 10.0f));
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                ProfileGiftsContainer.lambda$new$0(i, view2);
            }
        });
    }

    private long getRandomUserId() {
        ConcurrentHashMap<Long, TLRPC.User> users = MessagesController.getInstance(this.currentAccount).getUsers();
        int size = users.size();
        if (size == 0) {
            return 0L;
        }
        int nextInt = Utilities.fastRandom.nextInt(size);
        int i = 0;
        for (Map.Entry<Long, TLRPC.User> entry : users.entrySet()) {
            if (i == nextInt) {
                return entry.getValue().id;
            }
            i++;
        }
        return 0L;
    }

    public boolean isLoadingVisible() {
        for (int i = 0; i < this.listView.getChildCount(); i++) {
            if (this.listView.getChildAt(i) instanceof FlickerLoadingView) {
                return true;
            }
        }
        return false;
    }

    public static void lambda$new$0(int i, View view) {
        UserSelectorBottomSheet.open(2, 0L, BirthdayController.getInstance(i).getState());
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        UniversalAdapter universalAdapter;
        if (i == NotificationCenter.starUserGiftsLoaded && ((Long) objArr[0]).longValue() == this.userId) {
            UniversalRecyclerView universalRecyclerView = this.listView;
            if (universalRecyclerView != null && (universalAdapter = universalRecyclerView.adapter) != null) {
                universalAdapter.update(true);
            }
            if (!this.listView.canScrollVertically(1) || isLoadingVisible()) {
                this.list.load();
            }
        }
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        int i;
        int i2;
        int i3;
        StarsController.GiftsList giftsList = this.list;
        int max = Math.max(1, (giftsList == null || (i3 = giftsList.totalCount) == 0) ? 3 : Math.min(3, i3));
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.setSpanCount(max);
        }
        arrayList.add(UItem.asSpace(AndroidUtilities.dp(12.0f)));
        StarsController.GiftsList giftsList2 = this.list;
        if (giftsList2 != null) {
            Iterator it = giftsList2.gifts.iterator();
            loop0: while (true) {
                i = 3;
                do {
                    i2 = 0;
                    if (!it.hasNext()) {
                        break loop0;
                    }
                    arrayList.add(GiftSheet.GiftCell.Factory.asStarGift(0, (TL_stars.UserStarGift) it.next(), true));
                    i--;
                } while (i != 0);
            }
            StarsController.GiftsList giftsList3 = this.list;
            if (giftsList3.loading || !giftsList3.endReached) {
                while (true) {
                    if (i2 >= (i <= 0 ? 3 : i)) {
                        break;
                    }
                    arrayList.add(UItem.asFlicker(i2, 34).setSpanCount(1));
                    i2++;
                }
            }
        }
        arrayList.add(UItem.asSpace(AndroidUtilities.dp(20.0f)));
        if (this.userId == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
            arrayList.add(TextFactory.asText(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, this.resourcesProvider), 17, 14.0f, LocaleController.getString(R.string.ProfileGiftsInfo), true, AndroidUtilities.dp(24.0f)));
        }
        arrayList.add(UItem.asSpace(AndroidUtilities.dp(82.0f)));
    }

    public RecyclerListView getCurrentListView() {
        return this.listView;
    }

    public int getGiftsCount() {
        int i;
        StarsController.GiftsList giftsList = this.list;
        if (giftsList != null && (i = giftsList.totalCount) > 0) {
            return i;
        }
        TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(this.userId);
        if (userFull != null) {
            return userFull.stargifts_count;
        }
        return 0;
    }

    public CharSequence getLastEmojis(Paint.FontMetricsInt fontMetricsInt) {
        StarsController.GiftsList giftsList = this.list;
        if (giftsList == null || giftsList.gifts.isEmpty()) {
            return "";
        }
        HashSet hashSet = new HashSet();
        ArrayList arrayList = new ArrayList();
        for (int i = 0; arrayList.size() < 3 && i < this.list.gifts.size(); i++) {
            TLRPC.Document document = ((TL_stars.UserStarGift) this.list.gifts.get(i)).gift.getDocument();
            if (document != null && !hashSet.contains(Long.valueOf(document.id))) {
                hashSet.add(Long.valueOf(document.id));
                arrayList.add(document);
            }
        }
        if (arrayList.isEmpty()) {
            return "";
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(" ");
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder("x");
            spannableStringBuilder2.setSpan(new AnimatedEmojiSpan((TLRPC.Document) arrayList.get(i2), 0.9f, fontMetricsInt), 0, 1, 33);
            spannableStringBuilder.append((CharSequence) spannableStringBuilder2);
        }
        return spannableStringBuilder;
    }

    public long getLastEmojisHash() {
        StarsController.GiftsList giftsList = this.list;
        long j = 0;
        if (giftsList != null && !giftsList.gifts.isEmpty()) {
            HashSet hashSet = new HashSet();
            int i = 0;
            for (int i2 = 0; i < 3 && i2 < this.list.gifts.size(); i2++) {
                TLRPC.Document document = ((TL_stars.UserStarGift) this.list.gifts.get(i2)).gift.getDocument();
                if (document != null) {
                    hashSet.add(Long.valueOf(document.id));
                    j = Objects.hash(Long.valueOf(j), Long.valueOf(document.id));
                    i++;
                }
            }
        }
        return j;
    }

    @Override
    protected void onAttachedToWindow() {
        UniversalAdapter universalAdapter;
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.starUserGiftsLoaded);
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null && (universalAdapter = universalRecyclerView.adapter) != null) {
            universalAdapter.update(false);
        }
        StarsController.GiftsList giftsList = this.list;
        if (giftsList != null) {
            giftsList.shown = true;
            giftsList.load();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starUserGiftsLoaded);
        StarsController.GiftsList giftsList = this.list;
        if (giftsList != null) {
            giftsList.shown = false;
        }
    }

    public void onItemClick(UItem uItem, View view, int i, float f, float f2) {
        Object obj = uItem.object;
        if (obj instanceof TL_stars.UserStarGift) {
            new StarGiftSheet(getContext(), this.currentAccount, this.userId, this.resourcesProvider).set(this.userId == UserConfig.getInstance(this.currentAccount).getClientUserId(), (TL_stars.UserStarGift) obj).show();
        }
    }

    public boolean onItemLongPress(UItem uItem, View view, int i, float f, float f2) {
        return false;
    }

    protected abstract int processColor(int i);

    public void setVisibleHeight(int i) {
        this.visibleHeight = i;
    }

    public void updateColors() {
        ButtonWithCounterView buttonWithCounterView = this.button;
        if (buttonWithCounterView != null) {
            buttonWithCounterView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(8.0f), processColor(Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider))));
        }
    }
}
