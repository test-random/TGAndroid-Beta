package org.telegram.ui.Components.Premium.boosts;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BirthdayController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
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
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.boosts.adapters.SelectorAdapter;
import org.telegram.ui.Components.Premium.boosts.cells.selector.SelectorBtnCell;
import org.telegram.ui.Components.Premium.boosts.cells.selector.SelectorHeaderCell;
import org.telegram.ui.Components.Premium.boosts.cells.selector.SelectorSearchCell;
import org.telegram.ui.Components.Premium.boosts.cells.selector.SelectorUserCell;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PrivacyControlActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class UserSelectorBottomSheet extends BottomSheetWithRecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    private static UserSelectorBottomSheet instance;
    private final ButtonWithCounterView actionButton;
    private final HashMap allSelectedObjects;
    private BirthdayController.BirthdayState birthdays;
    private final FrameLayout bulletinContainer;
    private final SelectorBtnCell buttonContainer;
    private final List contacts;
    private final List contactsLetters;
    private final Map contactsMap;
    private String customTitle;
    private final List foundedUsers;
    private final SelectorHeaderCell headerView;
    private final List hints;
    private boolean includeTonOption;
    private boolean isHintSearchText;
    private final ArrayList items;
    private int listPaddingTop;
    private final ArrayList oldItems;
    private Utilities.Callback onUserSelectedListener;
    private final List paymentOptions;
    private String query;
    private float recipientsBtnExtraSpace;
    private ReplacementSpan recipientsBtnSpaceSpan;
    private final Runnable remoteSearchRunnable;
    private final SelectorSearchCell searchField;
    private final View sectionCell;
    private final HashSet selectedIds;
    private SelectorAdapter selectorAdapter;
    private int tonDays;
    private Drawable tonIcon;
    public int type;
    private long userId;

    public UserSelectorBottomSheet(BaseFragment baseFragment, long j, BirthdayController.BirthdayState birthdayState, final int i, boolean z) {
        super(baseFragment, z, false, false, baseFragment == null ? null : baseFragment.getResourceProvider());
        Runnable runnable = null;
        this.oldItems = new ArrayList();
        ArrayList arrayList = new ArrayList();
        this.items = arrayList;
        HashSet hashSet = new HashSet();
        this.selectedIds = hashSet;
        this.contacts = new ArrayList();
        this.hints = new ArrayList();
        this.foundedUsers = new ArrayList();
        this.contactsMap = new HashMap();
        this.contactsLetters = new ArrayList();
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        this.allSelectedObjects = linkedHashMap;
        this.listPaddingTop = AndroidUtilities.dp(120.0f);
        this.paymentOptions = new ArrayList();
        this.isHintSearchText = false;
        this.remoteSearchRunnable = new Runnable() {
            @Override
            public void run() {
                String str = UserSelectorBottomSheet.this.query;
                if (str != null) {
                    UserSelectorBottomSheet.this.loadData(str);
                }
            }
        };
        this.type = i;
        this.birthdays = birthdayState;
        this.userId = j;
        if (j != 0 && baseFragment != null && !hashSet.contains(Long.valueOf(j))) {
            TLRPC.User user = baseFragment.getMessagesController().getUser(Long.valueOf(j));
            hashSet.add(Long.valueOf(user.id));
            linkedHashMap.put(Long.valueOf(user.id), user);
        }
        SelectorHeaderCell selectorHeaderCell = new SelectorHeaderCell(getContext(), this.resourcesProvider) {
            @Override
            protected int getHeaderHeight() {
                return AndroidUtilities.dp(getResources().getConfiguration().orientation == 2 ? 48.0f : 54.0f);
            }
        };
        this.headerView = selectorHeaderCell;
        selectorHeaderCell.setOnCloseClickListener(new UserSelectorBottomSheet$$ExternalSyntheticLambda3(this));
        selectorHeaderCell.setText(getTitle());
        selectorHeaderCell.setCloseImageVisible(false);
        selectorHeaderCell.backDrawable.setRotation(0.0f, false);
        createRecipientsBtnSpaceSpan();
        SelectorSearchCell selectorSearchCell = new SelectorSearchCell(getContext(), this.resourcesProvider, runnable) {
            private boolean isKeyboardVisible;

            @Override
            protected void onLayout(boolean z2, int i2, int i3, int i4, int i5) {
                super.onLayout(z2, i2, i3, i4, i5);
                UserSelectorBottomSheet.this.listPaddingTop = getMeasuredHeight() + AndroidUtilities.dp(64.0f);
                UserSelectorBottomSheet.this.selectorAdapter.notifyChangedLast();
                if (this.isKeyboardVisible != UserSelectorBottomSheet.this.isKeyboardVisible()) {
                    boolean isKeyboardVisible = UserSelectorBottomSheet.this.isKeyboardVisible();
                    this.isKeyboardVisible = isKeyboardVisible;
                    if (isKeyboardVisible) {
                        UserSelectorBottomSheet.this.scrollToTop(true);
                    }
                }
            }
        };
        this.searchField = selectorSearchCell;
        int i2 = Theme.key_dialogBackground;
        selectorSearchCell.setBackgroundColor(getThemedColor(i2));
        selectorSearchCell.setOnSearchTextChange(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                UserSelectorBottomSheet.this.onSearch((String) obj);
            }
        });
        selectorSearchCell.setHintText(LocaleController.getString((!hashSet.isEmpty() || i == 1 || i == 2 || i == 3) ? R.string.Search : R.string.GiftPremiumUsersSearchHint), false);
        View view = new View(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                canvas.drawColor(UserSelectorBottomSheet.this.getThemedColor(Theme.key_graySection));
            }
        };
        this.sectionCell = view;
        ViewGroup viewGroup = this.containerView;
        int i3 = this.backgroundPaddingLeft;
        viewGroup.addView(selectorHeaderCell, 0, LayoutHelper.createFrameMarginPx(-1, -2.0f, 55, i3, 0, i3, 0));
        ViewGroup viewGroup2 = this.containerView;
        int i4 = this.backgroundPaddingLeft;
        viewGroup2.addView(selectorSearchCell, LayoutHelper.createFrameMarginPx(-1, -2.0f, 55, i4, 0, i4, 0));
        ViewGroup viewGroup3 = this.containerView;
        int i5 = this.backgroundPaddingLeft;
        viewGroup3.addView(view, LayoutHelper.createFrameMarginPx(-1, 1.0f, 55, i5, 0, i5, 0));
        SelectorBtnCell selectorBtnCell = new SelectorBtnCell(getContext(), this.resourcesProvider, null);
        this.buttonContainer = selectorBtnCell;
        selectorBtnCell.setClickable(true);
        selectorBtnCell.setOrientation(1);
        selectorBtnCell.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        selectorBtnCell.setBackgroundColor(Theme.getColor(i2, this.resourcesProvider));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(getContext(), this.resourcesProvider) {
            @Override
            protected float calculateCounterWidth(float f, float f2) {
                boolean z2 = UserSelectorBottomSheet.this.recipientsBtnExtraSpace == 0.0f;
                UserSelectorBottomSheet.this.recipientsBtnExtraSpace = f;
                if (z2) {
                    UserSelectorBottomSheet.this.createRecipientsBtnSpaceSpan();
                    UserSelectorBottomSheet.this.updateActionButton(false);
                }
                return f;
            }
        };
        this.actionButton = buttonWithCounterView;
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                UserSelectorBottomSheet.this.lambda$new$3(view2);
            }
        });
        selectorBtnCell.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 87));
        FrameLayout frameLayout = new FrameLayout(getContext());
        this.bulletinContainer = frameLayout;
        ViewGroup viewGroup4 = this.containerView;
        int i6 = this.backgroundPaddingLeft;
        viewGroup4.addView(frameLayout, LayoutHelper.createFrameMarginPx(-1, 300.0f, 87, i6, 0, i6, AndroidUtilities.dp(68.0f)));
        this.selectorAdapter.setData(arrayList, this.recyclerListView);
        RecyclerListView recyclerListView = this.recyclerListView;
        int i7 = this.backgroundPaddingLeft;
        recyclerListView.setPadding(i7, 0, i7, AndroidUtilities.dp(i != 1 ? 60.0f : 0.0f));
        this.recyclerListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i8) {
                if (i8 == 1) {
                    AndroidUtilities.hideKeyboard(UserSelectorBottomSheet.this.searchField.getEditText());
                }
            }
        });
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
            @Override
            public boolean hasDoubleTap(View view2, int i8) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view2, i8);
            }

            @Override
            public void onDoubleTap(View view2, int i8, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view2, i8, f, f2);
            }

            @Override
            public final void onItemClick(View view2, int i8, float f, float f2) {
                UserSelectorBottomSheet.this.lambda$new$5(i, view2, i8, f, f2);
            }
        });
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDurations(350L);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.recyclerListView.setItemAnimator(defaultItemAnimator);
        this.recyclerListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect rect, View view2, RecyclerView recyclerView, RecyclerView.State state) {
                super.getItemOffsets(rect, view2, recyclerView, state);
                if (recyclerView.getChildAdapterPosition(view2) == UserSelectorBottomSheet.this.items.size()) {
                    rect.bottom = UserSelectorBottomSheet.this.listPaddingTop;
                }
            }
        });
        selectorSearchCell.setText("");
        selectorSearchCell.spansContainer.removeAllSpans(false);
        selectorSearchCell.updateSpans(false, hashSet, new Runnable() {
            @Override
            public final void run() {
                UserSelectorBottomSheet.this.lambda$new$6();
            }
        }, null);
        selectorHeaderCell.setText(getTitle());
        updateActionButton(false);
        initContacts(false);
        initHints(false);
        updateList(false, true);
        fixNavigationBar();
        if (i == 0 || i == 2) {
            BoostRepository.loadGiftOptions(this.currentAccount, null, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    UserSelectorBottomSheet.this.lambda$new$7((List) obj);
                }
            });
        }
        if (i == 0 || i == 2) {
            StarsController.getInstance(this.currentAccount).loadStarGifts();
        }
    }

    private int addSection(ArrayList arrayList, CharSequence charSequence, final ArrayList arrayList2, boolean z) {
        if (arrayList2.isEmpty()) {
            return 0;
        }
        ArrayList arrayList3 = new ArrayList();
        Iterator it = arrayList2.iterator();
        int i = 0;
        int i2 = 0;
        final boolean z2 = true;
        while (it.hasNext()) {
            TLRPC.User user = (TLRPC.User) it.next();
            if (user != null && !user.bot && !UserObject.isService(user.id)) {
                long j = user.id;
                if (j != this.userId) {
                    if (!this.selectedIds.contains(Long.valueOf(j))) {
                        z2 = false;
                    }
                    i2++;
                    i += AndroidUtilities.dp(56.0f);
                    arrayList3.add(SelectorAdapter.Item.asUser(user, this.selectedIds.contains(Long.valueOf(user.id))).withOptions(openOptions(user)));
                }
            }
        }
        if (arrayList3.isEmpty()) {
            return i;
        }
        int dp = i + AndroidUtilities.dp(32.0f);
        SelectorAdapter.Item asTopSection = SelectorAdapter.Item.asTopSection(charSequence);
        if (z && i2 > 1) {
            asTopSection.withRightText(LocaleController.getString(z2 ? R.string.DeselectAll : R.string.SelectAll), new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    UserSelectorBottomSheet.this.lambda$addSection$9(z2, arrayList2, view);
                }
            });
        }
        arrayList.add(asTopSection);
        arrayList.addAll(arrayList3);
        return dp;
    }

    private void checkEditTextHint() {
        Runnable runnable;
        int i;
        if (!this.selectedIds.isEmpty() || (i = this.type) == 1 || i == 2 || i == 3) {
            if (this.isHintSearchText) {
                return;
            }
            this.isHintSearchText = true;
            runnable = new Runnable() {
                @Override
                public final void run() {
                    UserSelectorBottomSheet.this.lambda$checkEditTextHint$1();
                }
            };
        } else {
            if (!this.isHintSearchText) {
                return;
            }
            this.isHintSearchText = false;
            runnable = new Runnable() {
                @Override
                public final void run() {
                    UserSelectorBottomSheet.this.lambda$checkEditTextHint$2();
                }
            };
        }
        AndroidUtilities.runOnUIThread(runnable, 10L);
    }

    private void clearSearchAfterSelect() {
        if (isSearching()) {
            this.query = null;
            this.searchField.setText("");
            AndroidUtilities.cancelRunOnUIThread(this.remoteSearchRunnable);
            updateItems(true, true);
        }
    }

    public void createRecipientsBtnSpaceSpan() {
        this.recipientsBtnSpaceSpan = new ReplacementSpan() {
            @Override
            public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            }

            @Override
            public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
                return (int) UserSelectorBottomSheet.this.recipientsBtnExtraSpace;
            }
        };
    }

    public static boolean handleIntent(Intent intent, Browser.Progress progress) {
        String scheme;
        String path;
        Uri data = intent.getData();
        if (data == null || (scheme = data.getScheme()) == null) {
            return false;
        }
        if (!scheme.equals("http") && !scheme.equals("https")) {
            if (!scheme.equals("tg")) {
                return false;
            }
            String uri = data.toString();
            if (!uri.startsWith("tg:premium_multigift") && !uri.startsWith("tg://premium_multigift")) {
                return false;
            }
            open();
            return true;
        }
        String lowerCase = data.getHost().toLowerCase();
        if ((!lowerCase.equals("telegram.me") && !lowerCase.equals("t.me") && !lowerCase.equals("telegram.dog")) || (path = data.getPath()) == null || !path.startsWith("/premium_multigift")) {
            return false;
        }
        open();
        return true;
    }

    private void initContacts(boolean z) {
        if (this.contacts.isEmpty()) {
            this.contacts.addAll(ContactsController.getInstance(this.currentAccount).contacts);
            this.contactsMap.putAll(ContactsController.getInstance(this.currentAccount).usersSectionsDict);
            this.contactsLetters.addAll(ContactsController.getInstance(this.currentAccount).sortedUsersSectionsArray);
            if (z) {
                updateItems(true, true);
            }
        }
    }

    private void initHints(boolean z) {
        if (this.hints.isEmpty()) {
            this.hints.addAll(MediaDataController.getInstance(this.currentAccount).hints);
            if (z) {
                updateItems(true, true);
            }
        }
    }

    private boolean isSearching() {
        return !TextUtils.isEmpty(this.query);
    }

    public void lambda$addSection$8() {
        checkEditTextHint();
        updateList(true, false);
    }

    public void lambda$addSection$9(boolean z, ArrayList arrayList, View view) {
        if (z) {
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                TLRPC.User user = (TLRPC.User) it.next();
                this.selectedIds.remove(Long.valueOf(user.id));
                this.allSelectedObjects.remove(Long.valueOf(user.id));
            }
        } else {
            Iterator it2 = arrayList.iterator();
            while (it2.hasNext()) {
                TLRPC.User user2 = (TLRPC.User) it2.next();
                if (!this.selectedIds.contains(Long.valueOf(user2.id))) {
                    this.selectedIds.add(Long.valueOf(user2.id));
                    this.allSelectedObjects.put(Long.valueOf(user2.id), user2);
                }
            }
        }
        checkEditTextHint();
        this.searchField.updateSpans(true, this.selectedIds, new Runnable() {
            @Override
            public final void run() {
                UserSelectorBottomSheet.this.lambda$addSection$8();
            }
        }, null);
        updateList(true, true);
        clearSearchAfterSelect();
    }

    public void lambda$checkEditTextHint$1() {
        this.searchField.setHintText(LocaleController.getString(R.string.Search), true);
    }

    public void lambda$checkEditTextHint$2() {
        this.searchField.setHintText(LocaleController.getString(R.string.GiftPremiumUsersSearchHint), true);
    }

    public void lambda$didReceivedNotification$15() {
        initContacts(true);
    }

    public void lambda$didReceivedNotification$16() {
        initHints(true);
    }

    public void lambda$didReceivedNotification$17() {
        updateItems(true, true);
    }

    public void lambda$loadData$0(List list) {
        this.foundedUsers.clear();
        this.foundedUsers.addAll(list);
        updateList(true, true);
    }

    public void lambda$new$3(View view) {
        next();
    }

    public void lambda$new$4() {
        checkEditTextHint();
        updateList(true, false);
    }

    public void lambda$new$5(int i, View view, int i2, float f, float f2) {
        if (view instanceof TextCell) {
            openBirthdaySetup();
            return;
        }
        if (view instanceof SelectorUserCell) {
            TLRPC.User user = ((SelectorUserCell) view).getUser();
            if (user == null && i == 3) {
                Utilities.Callback callback = this.onUserSelectedListener;
                if (callback != null) {
                    callback.run(-99L);
                    return;
                }
                return;
            }
            long j = user.id;
            if (i == 3) {
                Utilities.Callback callback2 = this.onUserSelectedListener;
                if (callback2 != null) {
                    callback2.run(Long.valueOf(j));
                    return;
                }
                return;
            }
            if (i == 1) {
                SelectorSearchCell selectorSearchCell = this.searchField;
                if (selectorSearchCell != null) {
                    AndroidUtilities.hideKeyboard(selectorSearchCell.getEditText());
                }
                StarsIntroActivity.GiftStarsSheet giftStarsSheet = new StarsIntroActivity.GiftStarsSheet(getContext(), this.resourcesProvider, user, new UserSelectorBottomSheet$$ExternalSyntheticLambda3(this));
                if (!AndroidUtilities.isTablet()) {
                    giftStarsSheet.makeAttached(this.attachedFragment);
                }
                giftStarsSheet.show();
                return;
            }
            if (i == 0 || i == 2) {
                new GiftSheet(getContext(), this.currentAccount, j, BoostRepository.filterGiftOptionsByBilling(BoostRepository.filterGiftOptions(this.paymentOptions, 1)), new UserSelectorBottomSheet$$ExternalSyntheticLambda3(this)).show();
                return;
            }
            if (this.selectedIds.contains(Long.valueOf(j))) {
                this.selectedIds.remove(Long.valueOf(j));
            } else {
                this.selectedIds.add(Long.valueOf(j));
                this.allSelectedObjects.put(Long.valueOf(j), user);
            }
            if (this.selectedIds.size() == 11) {
                this.selectedIds.remove(Long.valueOf(j));
                showMaximumUsersToast();
            } else {
                checkEditTextHint();
                this.searchField.updateSpans(true, this.selectedIds, new Runnable() {
                    @Override
                    public final void run() {
                        UserSelectorBottomSheet.this.lambda$new$4();
                    }
                }, null);
                updateList(true, true);
                clearSearchAfterSelect();
            }
        }
    }

    public void lambda$new$6() {
        checkEditTextHint();
        updateList(true, false);
    }

    public void lambda$new$7(List list) {
        this.paymentOptions.clear();
        this.paymentOptions.addAll(list);
        if (this.actionButton.isLoading()) {
            this.actionButton.setLoading(false);
            if (this.recyclerListView.isAttachedToWindow()) {
                next();
            }
        }
    }

    public void lambda$openBirthdaySetup$18(TLObject tLObject, TLRPC.UserFull userFull, TL_account.TL_birthday tL_birthday, TLRPC.TL_error tL_error) {
        Bulletin createSimpleBulletin;
        String str;
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            createSimpleBulletin = BulletinFactory.of(this.bulletinContainer, this.resourcesProvider).createSimpleBulletin(R.raw.contact_check, LocaleController.getString(R.string.PrivacyBirthdaySetDone)).setDuration(5000);
        } else {
            if (userFull != null) {
                int i = userFull.flags2;
                userFull.flags2 = tL_birthday == null ? i & (-33) : i | 32;
                userFull.birthday = tL_birthday;
                MessagesStorage.getInstance(this.currentAccount).updateUserInfo(userFull, false);
            }
            if (tL_error != null && (str = tL_error.text) != null && str.startsWith("FLOOD_WAIT_")) {
                if (getContext() != null) {
                    new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString(R.string.PrivacyBirthdayTooOftenTitle)).setMessage(LocaleController.getString(R.string.PrivacyBirthdayTooOftenMessage)).setPositiveButton(LocaleController.getString(R.string.OK), null).show();
                    return;
                }
                return;
            }
            createSimpleBulletin = BulletinFactory.of(this.bulletinContainer, this.resourcesProvider).createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.UnknownError));
        }
        createSimpleBulletin.show();
    }

    public void lambda$openBirthdaySetup$19(final TLRPC.UserFull userFull, final TL_account.TL_birthday tL_birthday, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                UserSelectorBottomSheet.this.lambda$openBirthdaySetup$18(tLObject, userFull, tL_birthday, tL_error);
            }
        });
    }

    public void lambda$openBirthdaySetup$20(TL_account.TL_birthday tL_birthday) {
        TL_account.updateBirthday updatebirthday = new TL_account.updateBirthday();
        updatebirthday.flags |= 1;
        updatebirthday.birthday = tL_birthday;
        final TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
        final TL_account.TL_birthday tL_birthday2 = userFull != null ? userFull.birthday : null;
        if (userFull != null) {
            userFull.flags2 |= 32;
            userFull.birthday = tL_birthday;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(updatebirthday, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                UserSelectorBottomSheet.this.lambda$openBirthdaySetup$19(userFull, tL_birthday2, tLObject, tL_error);
            }
        }, 1024);
        MessagesController.getInstance(this.currentAccount).invalidateContentSettings();
        MessagesController.getInstance(this.currentAccount).removeSuggestion(0L, "BIRTHDAY_SETUP");
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.newSuggestionsAvailable, new Object[0]);
        updateItems(true, true);
    }

    public void lambda$openBirthdaySetup$21() {
        if (getBaseFragment() == null) {
            return;
        }
        BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
        bottomSheetParams.transitionFromLeft = true;
        bottomSheetParams.allowNestedScroll = false;
        getBaseFragment().showAsSheet(new PrivacyControlActivity(11), bottomSheetParams);
    }

    public void lambda$openOptions$12(TLRPC.User user) {
        BaseFragment baseFragment = getBaseFragment();
        if (user == null || baseFragment == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", user.id);
        baseFragment.presentFragment(new ChatActivity(bundle));
    }

    public void lambda$openOptions$13(TLRPC.User user) {
        BaseFragment baseFragment = getBaseFragment();
        if (user == null || baseFragment == null) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", user.id);
        baseFragment.presentFragment(new ProfileActivity(bundle));
    }

    public void lambda$openOptions$14(final TLRPC.User user, View view) {
        ItemOptions.makeOptions(this.container, this.resourcesProvider, (View) view.getParent()).add(R.drawable.profile_discuss, LocaleController.getString(R.string.SendMessage), new Runnable() {
            @Override
            public final void run() {
                UserSelectorBottomSheet.this.lambda$openOptions$12(user);
            }
        }).add(R.drawable.msg_openprofile, LocaleController.getString(R.string.OpenProfile), new Runnable() {
            @Override
            public final void run() {
                UserSelectorBottomSheet.this.lambda$openOptions$13(user);
            }
        }).show();
    }

    public void lambda$updateItems$10() {
        checkEditTextHint();
        updateList(true, false);
    }

    public void lambda$updateItems$11(ArrayList arrayList, View view) {
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            Long l = (Long) it.next();
            l.longValue();
            this.selectedIds.remove(l);
            this.allSelectedObjects.remove(l);
        }
        checkEditTextHint();
        this.searchField.updateSpans(true, this.selectedIds, new Runnable() {
            @Override
            public final void run() {
                UserSelectorBottomSheet.this.lambda$updateItems$10();
            }
        }, null);
        updateList(true, true);
        clearSearchAfterSelect();
    }

    public void loadData(String str) {
        BoostRepository.searchContactsLocally(str, false, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                UserSelectorBottomSheet.this.lambda$loadData$0((List) obj);
            }
        });
    }

    private void next() {
        int i;
        if (this.selectedIds.size() != 0) {
            if (!this.paymentOptions.isEmpty() || (i = this.type) == 0 || i == 2) {
                ArrayList arrayList = new ArrayList();
                for (TLRPC.User user : this.allSelectedObjects.values()) {
                    if (this.selectedIds.contains(Long.valueOf(user.id))) {
                        arrayList.add(user);
                    }
                }
                AndroidUtilities.hideKeyboard(this.searchField.getEditText());
                if (this.type == 1) {
                    return;
                }
                List filterGiftOptionsByBilling = BoostRepository.filterGiftOptionsByBilling(BoostRepository.filterGiftOptions(this.paymentOptions, arrayList.size()));
                if (arrayList.size() == 1) {
                    long j = ((TLRPC.User) arrayList.get(0)).id;
                    GiftSheet giftSheet = new GiftSheet(getContext(), this.currentAccount, j, filterGiftOptionsByBilling, new UserSelectorBottomSheet$$ExternalSyntheticLambda3(this));
                    BirthdayController.BirthdayState birthdayState = this.birthdays;
                    giftSheet.setBirthday(birthdayState != null && birthdayState.contains(j)).show();
                }
            }
        }
    }

    public void onSearch(String str) {
        this.query = str;
        AndroidUtilities.cancelRunOnUIThread(this.remoteSearchRunnable);
        AndroidUtilities.runOnUIThread(this.remoteSearchRunnable, 350L);
    }

    public static UserSelectorBottomSheet open() {
        return open(0L, null);
    }

    public static UserSelectorBottomSheet open(final int i, long j, BirthdayController.BirthdayState birthdayState) {
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment == null) {
            return null;
        }
        UserSelectorBottomSheet userSelectorBottomSheet = instance;
        if (userSelectorBottomSheet != null) {
            return userSelectorBottomSheet;
        }
        UserSelectorBottomSheet userSelectorBottomSheet2 = new UserSelectorBottomSheet(lastFragment, j, birthdayState, i, true) {
            @Override
            protected int getType() {
                return i;
            }
        };
        if (!AndroidUtilities.isTablet() && !AndroidUtilities.hasDialogOnTop(lastFragment)) {
            userSelectorBottomSheet2.makeAttached(lastFragment);
        }
        lastFragment.showDialog(userSelectorBottomSheet2);
        instance = userSelectorBottomSheet2;
        return userSelectorBottomSheet2;
    }

    public static UserSelectorBottomSheet open(long j, BirthdayController.BirthdayState birthdayState) {
        return open(0, j, birthdayState);
    }

    private void openBirthdaySetup() {
        AlertsCreator.createBirthdayPickerDialog(getContext(), LocaleController.getString(R.string.EditProfileBirthdayTitle), LocaleController.getString(R.string.EditProfileBirthdayButton), null, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                UserSelectorBottomSheet.this.lambda$openBirthdaySetup$20((TL_account.TL_birthday) obj);
            }
        }, new Runnable() {
            @Override
            public final void run() {
                UserSelectorBottomSheet.this.lambda$openBirthdaySetup$21();
            }
        }, this.resourcesProvider).show();
    }

    private void showMaximumUsersToast() {
        BulletinFactory.of(this.container, this.resourcesProvider).createSimpleBulletin(R.raw.chats_infotip, LocaleController.getString("BoostingSelectUpToWarningUsers", R.string.BoostingSelectUpToWarningUsers)).show(true);
        try {
            this.container.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
    }

    public void updateActionButton(boolean z) {
        String string;
        this.actionButton.setShowZero(false);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (this.selectedIds.size() != 0) {
            string = LocaleController.getString("GiftPremiumProceedBtn", R.string.GiftPremiumProceedBtn);
        } else {
            if (LocaleController.isRTL) {
                spannableStringBuilder.append((CharSequence) LocaleController.getString("GiftPremiumChooseRecipientsBtn", R.string.GiftPremiumChooseRecipientsBtn));
                spannableStringBuilder.append((CharSequence) "d").setSpan(this.recipientsBtnSpaceSpan, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 33);
                this.actionButton.setCount(this.selectedIds.size(), true);
                this.actionButton.setText(spannableStringBuilder, z, false);
                this.actionButton.setEnabled(true);
            }
            spannableStringBuilder.append((CharSequence) "d").setSpan(this.recipientsBtnSpaceSpan, 0, 1, 33);
            string = LocaleController.getString("GiftPremiumChooseRecipientsBtn", R.string.GiftPremiumChooseRecipientsBtn);
        }
        spannableStringBuilder.append((CharSequence) string);
        this.actionButton.setCount(this.selectedIds.size(), true);
        this.actionButton.setText(spannableStringBuilder, z, false);
        this.actionButton.setEnabled(true);
    }

    private void updateCheckboxes(boolean r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.boosts.UserSelectorBottomSheet.updateCheckboxes(boolean):void");
    }

    private void updateList(boolean z, boolean z2) {
        updateItems(z, z2);
        updateCheckboxes(z);
        updateActionButton(z);
    }

    public void addTONOption(int i) {
        this.includeTonOption = true;
        this.tonDays = i;
        updateItems(false, true);
    }

    @Override
    protected RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView) {
        SelectorAdapter selectorAdapter = new SelectorAdapter(getContext(), false, this.resourcesProvider);
        this.selectorAdapter = selectorAdapter;
        selectorAdapter.setGreenSelector(true);
        return this.selectorAdapter;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        Runnable runnable;
        if (i == NotificationCenter.giftsToUserSent) {
            dismiss();
            return;
        }
        if (i == NotificationCenter.contactsDidLoad) {
            runnable = new Runnable() {
                @Override
                public final void run() {
                    UserSelectorBottomSheet.this.lambda$didReceivedNotification$15();
                }
            };
        } else if (i == NotificationCenter.reloadHints) {
            runnable = new Runnable() {
                @Override
                public final void run() {
                    UserSelectorBottomSheet.this.lambda$didReceivedNotification$16();
                }
            };
        } else if (i != NotificationCenter.userInfoDidLoad) {
            return;
        } else {
            runnable = new Runnable() {
                @Override
                public final void run() {
                    UserSelectorBottomSheet.this.lambda$didReceivedNotification$17();
                }
            };
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    @Override
    public void dismiss() {
        AndroidUtilities.hideKeyboard(this.searchField.getEditText());
        super.dismiss();
    }

    @Override
    public void dismissInternal() {
        super.dismissInternal();
        instance = null;
        AndroidUtilities.cancelRunOnUIThread(this.remoteSearchRunnable);
    }

    @Override
    protected CharSequence getTitle() {
        String str = this.customTitle;
        if (str != null) {
            return str;
        }
        return LocaleController.getString(getType() == 1 ? R.string.GiftStarsTitle : ((getType() == 2 || getType() == 0) && !MessagesController.getInstance(this.currentAccount).stargiftsBlocked) ? R.string.GiftTelegramPremiumOrStarsTitle : R.string.GiftTelegramPremiumTitle);
    }

    protected int getType() {
        return 0;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.giftsToUserSent);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.userInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.reloadHints);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateItems(false, true);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.giftsToUserSent);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.reloadHints);
    }

    @Override
    protected void onPreDraw(Canvas canvas, int i, float f) {
        this.headerView.setTranslationY(Math.max(i, AndroidUtilities.statusBarHeight + (((this.headerView.getMeasuredHeight() - AndroidUtilities.statusBarHeight) - AndroidUtilities.dp(40.0f)) / 2.0f)) + AndroidUtilities.dp(8.0f));
        this.searchField.setTranslationY(this.headerView.getTranslationY() + this.headerView.getMeasuredHeight());
        this.sectionCell.setTranslationY(this.searchField.getTranslationY() + this.searchField.getMeasuredHeight());
        this.recyclerListView.setTranslationY(((this.headerView.getMeasuredHeight() + this.searchField.getMeasuredHeight()) + this.sectionCell.getMeasuredHeight()) - AndroidUtilities.dp(8.0f));
    }

    public View.OnClickListener openOptions(final TLRPC.User user) {
        return new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                UserSelectorBottomSheet.this.lambda$openOptions$14(user, view);
            }
        };
    }

    public void scrollToTop(boolean z) {
        if (!z) {
            this.recyclerListView.scrollToPosition(0);
            return;
        }
        LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(getContext(), 2, 0.6f);
        linearSmoothScrollerCustom.setTargetPosition(1);
        linearSmoothScrollerCustom.setOffset(AndroidUtilities.dp(36.0f));
        this.recyclerListView.getLayoutManager().startSmoothScroll(linearSmoothScrollerCustom);
    }

    public void setOnUserSelector(Utilities.Callback callback) {
        this.onUserSelectedListener = callback;
    }

    public void setTitle(String str) {
        this.customTitle = str;
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.setTitle(getTitle());
        }
        SelectorHeaderCell selectorHeaderCell = this.headerView;
        if (selectorHeaderCell != null) {
            selectorHeaderCell.setText(getTitle());
        }
    }

    public void updateItems(boolean z, boolean z2) {
        int i;
        int i2;
        BirthdayController.BirthdayState birthdayState;
        BirthdayController.BirthdayState birthdayState2;
        TLRPC.User currentUser;
        SelectorAdapter selectorAdapter;
        this.oldItems.clear();
        this.oldItems.addAll(this.items);
        this.items.clear();
        if (isSearching()) {
            i2 = 0;
            for (TLRPC.User user : this.foundedUsers) {
                if (user != null && !user.bot && !UserObject.isService(user.id)) {
                    i2 += AndroidUtilities.dp(56.0f);
                    this.items.add(SelectorAdapter.Item.asUser(user, this.selectedIds.contains(Long.valueOf(user.id))).withOptions(openOptions(user)));
                }
            }
        } else {
            if (this.includeTonOption && this.type == 3) {
                if (this.tonIcon == null) {
                    CombinedDrawable combinedDrawable = new CombinedDrawable(Theme.createCircleDrawable(AndroidUtilities.dp(46.0f), Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider)), getContext().getResources().getDrawable(R.drawable.ton).mutate());
                    combinedDrawable.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
                    this.tonIcon = combinedDrawable;
                }
                ArrayList arrayList = this.items;
                Drawable drawable = this.tonIcon;
                String string = LocaleController.getString(R.string.Gift2ExportTONTitle);
                int i3 = this.tonDays;
                arrayList.add(SelectorAdapter.Item.asCustomUser(2, drawable, string, i3 > 0 ? LocaleController.formatPluralString("Gift2ExportTONUnlocksIn", i3, new Object[0]) : ""));
            }
            TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(UserConfig.getInstance(this.currentAccount).getClientUserId());
            if (userFull == null) {
                MessagesController.getInstance(this.currentAccount).loadFullUser(UserConfig.getInstance(this.currentAccount).getCurrentUser(), 0, true);
            }
            if (this.type == 3 || userFull == null || userFull.birthday != null) {
                i = 0;
            } else {
                i = AndroidUtilities.dp(50.0f);
                this.items.add(SelectorAdapter.Item.asButton(1, R.drawable.menu_birthday, LocaleController.getString(R.string.GiftsBirthdaySetup)));
            }
            if (this.type == 0 && BuildVars.DEBUG_PRIVATE_VERSION && (currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser()) != null) {
                this.items.add(SelectorAdapter.Item.asTopSection(LocaleController.getString(R.string.Gift2MyselfSection)));
                SelectorAdapter.Item asUser = SelectorAdapter.Item.asUser(currentUser, this.selectedIds.contains(Long.valueOf(currentUser.id)));
                asUser.subtext = LocaleController.getString(R.string.Gift2Myself);
                this.items.add(asUser);
            }
            if (this.birthdays != null) {
                i = i + addSection(this.items, LocaleController.getString(R.string.BirthdayToday), this.birthdays.today, true) + addSection(this.items, LocaleController.getString(R.string.BirthdayYesterday), this.birthdays.yesterday, true) + addSection(this.items, LocaleController.getString(R.string.BirthdayTomorrow), this.birthdays.tomorrow, true);
            }
            final ArrayList arrayList2 = new ArrayList();
            SelectorAdapter.Item item = null;
            if (!this.hints.isEmpty()) {
                ArrayList arrayList3 = new ArrayList();
                Iterator it = this.hints.iterator();
                while (it.hasNext()) {
                    TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(((TLRPC.TL_topPeer) it.next()).peer.user_id));
                    if (user2 != null) {
                        long j = user2.id;
                        if (j != this.userId && !user2.self && !user2.bot && !UserObject.isService(j) && !UserObject.isDeleted(user2) && ((birthdayState2 = this.birthdays) == null || !birthdayState2.contains(user2.id))) {
                            if (this.selectedIds.contains(Long.valueOf(user2.id))) {
                                arrayList2.add(Long.valueOf(user2.id));
                            }
                            i += AndroidUtilities.dp(56.0f);
                            arrayList3.add(SelectorAdapter.Item.asUser(user2, this.selectedIds.contains(Long.valueOf(user2.id))).withOptions(openOptions(user2)));
                        }
                    }
                }
                if (!arrayList3.isEmpty()) {
                    i += AndroidUtilities.dp(32.0f);
                    item = SelectorAdapter.Item.asTopSection(LocaleController.getString(R.string.GiftPremiumFrequentContacts));
                    this.items.add(item);
                    this.items.addAll(arrayList3);
                }
            }
            for (String str : this.contactsLetters) {
                ArrayList arrayList4 = new ArrayList();
                for (TLRPC.TL_contact tL_contact : (List) this.contactsMap.get(str)) {
                    long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                    long j2 = tL_contact.user_id;
                    if (j2 != clientUserId && j2 != this.userId && ((birthdayState = this.birthdays) == null || !birthdayState.contains(j2))) {
                        TLRPC.User user3 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(tL_contact.user_id));
                        if (user3 != null && !user3.bot && !UserObject.isService(user3.id)) {
                            i += AndroidUtilities.dp(56.0f);
                            if (this.selectedIds.contains(Long.valueOf(user3.id))) {
                                arrayList2.add(Long.valueOf(user3.id));
                            }
                            arrayList4.add(SelectorAdapter.Item.asUser(user3, this.selectedIds.contains(Long.valueOf(user3.id))).withOptions(openOptions(user3)));
                        }
                    }
                }
                if (!arrayList4.isEmpty()) {
                    i += AndroidUtilities.dp(32.0f);
                    this.items.add(SelectorAdapter.Item.asLetter(str.toUpperCase()));
                    this.items.addAll(arrayList4);
                }
            }
            if (item != null && arrayList2.size() > 0 && !this.selectedIds.isEmpty()) {
                item.withRightText(LocaleController.getString(R.string.DeselectAll), new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        UserSelectorBottomSheet.this.lambda$updateItems$11(arrayList2, view);
                    }
                });
            }
            i2 = i;
        }
        if (this.items.isEmpty()) {
            this.items.add(SelectorAdapter.Item.asNoUsers());
            i2 += AndroidUtilities.dp(150.0f);
        }
        this.items.add(SelectorAdapter.Item.asPad(Math.max(0, ((int) (AndroidUtilities.displaySize.y * 0.6f)) - i2)));
        if (!z2 || (selectorAdapter = this.selectorAdapter) == null) {
            return;
        }
        if (z) {
            selectorAdapter.setItems(this.oldItems, this.items);
        } else {
            selectorAdapter.notifyDataSetChanged();
        }
    }
}
