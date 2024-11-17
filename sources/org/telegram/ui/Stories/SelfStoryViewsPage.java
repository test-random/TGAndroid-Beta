package org.telegram.ui.Stories;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.exoplayer2.util.Consumer;
import j$.util.Comparator$CC;
import j$.util.function.ToIntFunction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.FixedHeightEmptyCell;
import org.telegram.ui.Cells.ReactedUserHolderView;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.CustomPopupMenu;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.Components.FillLastLinearLayoutManager;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.MessageContainsEmojiButton;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ReplaceableIconDrawable;
import org.telegram.ui.Components.SearchField;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.RecyclerListViewScroller;
import org.telegram.ui.Stories.SelfStoryViewsPage;
import org.telegram.ui.Stories.SelfStoryViewsView;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesListPlaceProvider;
import org.telegram.ui.Stories.recorder.StoryEntry;
import org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet;

public abstract class SelfStoryViewsPage extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private int TOP_PADDING;
    private boolean checkAutoscroll;
    int currentAccount;
    ViewsModel currentModel;
    ViewsModel defaultModel;
    private long dialogId;
    HeaderView headerView;
    private boolean isAttachedToWindow;
    boolean isSearchDebounce;
    public FillLastLinearLayoutManager layoutManager;
    ListAdapter listAdapter;
    private int measuerdHeight;
    Consumer onSharedStateChanged;
    private CustomPopupMenu popupMenu;
    RecyclerItemsEnterAnimator recyclerItemsEnterAnimator;
    RecyclerListView recyclerListView;
    Theme.ResourcesProvider resourcesProvider;
    RecyclerAnimationScrollHelper scrollHelper;
    private final RecyclerListViewScroller scroller;
    SearchField searchField;
    Drawable shadowDrawable;
    private final View shadowView;
    private final View shadowView2;
    final FiltersState sharedFilterState;
    private boolean showContactsFilter;
    private boolean showReactionsSort;
    private boolean showSearch;
    private boolean showServerErrorText;
    final FiltersState state;
    SelfStoryViewsView.StoryItemInternal storyItem;
    StoryViewer storyViewer;
    private final TextView titleView;
    private final FrameLayout topViewsContainer;

    public class AnonymousClass4 implements RecyclerListView.OnItemLongClickListener {
        final StoryViewer val$storyViewer;

        AnonymousClass4(StoryViewer storyViewer) {
            this.val$storyViewer = storyViewer;
        }

        public void lambda$onItemClick$0(MessagesController messagesController, TLRPC.User user, String str, ReactedUserHolderView reactedUserHolderView, TL_stories.StoryView storyView) {
            messagesController.getStoriesController().updateBlockUser(user.id, true);
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            BulletinFactory.of(selfStoryViewsPage, selfStoryViewsPage.resourcesProvider).createSimpleBulletin(R.raw.ic_ban, LocaleController.formatString(R.string.StoryHidFromToast, str)).show();
            reactedUserHolderView.animateAlpha(SelfStoryViewsPage.this.isStoryShownToUser(storyView) ? 1.0f : 0.5f, true);
        }

        public void lambda$onItemClick$1(MessagesController messagesController, TLRPC.User user, String str, ReactedUserHolderView reactedUserHolderView, TL_stories.StoryView storyView) {
            messagesController.getStoriesController().updateBlockUser(user.id, false);
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            BulletinFactory.of(selfStoryViewsPage, selfStoryViewsPage.resourcesProvider).createSimpleBulletin(R.raw.contact_check, LocaleController.formatString(R.string.StoryShownBackToToast, str)).show();
            reactedUserHolderView.animateAlpha(SelfStoryViewsPage.this.isStoryShownToUser(storyView) ? 1.0f : 0.5f, true);
        }

        public void lambda$onItemClick$2(MessagesController messagesController, TLRPC.User user, ReactedUserHolderView reactedUserHolderView, TL_stories.StoryView storyView) {
            messagesController.blockPeer(user.id);
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            BulletinFactory.of(selfStoryViewsPage, selfStoryViewsPage.resourcesProvider).createBanBulletin(true).show();
            reactedUserHolderView.animateAlpha(SelfStoryViewsPage.this.isStoryShownToUser(storyView) ? 1.0f : 0.5f, true);
        }

        public void lambda$onItemClick$3(MessagesController messagesController, TLRPC.User user, ReactedUserHolderView reactedUserHolderView, TL_stories.StoryView storyView) {
            messagesController.getStoriesController().updateBlockUser(user.id, false);
            messagesController.unblockPeer(user.id);
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            BulletinFactory.of(selfStoryViewsPage, selfStoryViewsPage.resourcesProvider).createBanBulletin(false).show();
            reactedUserHolderView.animateAlpha(SelfStoryViewsPage.this.isStoryShownToUser(storyView) ? 1.0f : 0.5f, true);
        }

        public void lambda$onItemClick$4(TLRPC.User user, String str, ReactedUserHolderView reactedUserHolderView, TL_stories.StoryView storyView) {
            ArrayList<TLRPC.User> arrayList = new ArrayList<>();
            arrayList.add(user);
            ContactsController.getInstance(SelfStoryViewsPage.this.currentAccount).deleteContact(arrayList, false);
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            BulletinFactory.of(selfStoryViewsPage, selfStoryViewsPage.resourcesProvider).createSimpleBulletin(R.raw.ic_ban, LocaleController.formatString(R.string.DeletedFromYourContacts, str)).show();
            reactedUserHolderView.animateAlpha(SelfStoryViewsPage.this.isStoryShownToUser(storyView) ? 1.0f : 0.5f, true);
        }

        public void lambda$onItemClick$5(ArrayList arrayList, ItemOptions itemOptions, View view) {
            new EmojiPacksAlert(new BaseFragment() {
                @Override
                public Context getContext() {
                    return SelfStoryViewsPage.this.getContext();
                }

                @Override
                public int getCurrentAccount() {
                    return this.currentAccount;
                }

                @Override
                public Theme.ResourcesProvider getResourceProvider() {
                    return SelfStoryViewsPage.this.resourcesProvider;
                }
            }, SelfStoryViewsPage.this.getContext(), SelfStoryViewsPage.this.resourcesProvider, arrayList).show();
            itemOptions.dismiss();
        }

        @Override
        public boolean onItemClick(View view, int i) {
            final MessagesController messagesController;
            final TLRPC.User user;
            final AnonymousClass4 anonymousClass4;
            boolean z;
            if (!(view instanceof ReactedUserHolderView)) {
                return false;
            }
            final ReactedUserHolderView reactedUserHolderView = (ReactedUserHolderView) view;
            StoryViewer storyViewer = this.val$storyViewer;
            if (storyViewer == null || storyViewer.containerView == null) {
                return false;
            }
            final TL_stories.StoryView storyView = ((Item) SelfStoryViewsPage.this.listAdapter.items.get(i)).view;
            if (storyView == null || (user = (messagesController = MessagesController.getInstance(SelfStoryViewsPage.this.currentAccount)).getUser(Long.valueOf(storyView.user_id))) == null) {
                return false;
            }
            boolean z2 = messagesController.blockePeers.indexOfKey(user.id) >= 0;
            boolean z3 = user.contact || ContactsController.getInstance(SelfStoryViewsPage.this.currentAccount).contactsDict.get(Long.valueOf(user.id)) != null;
            boolean isStoryShownToUser = SelfStoryViewsPage.this.isStoryShownToUser(storyView);
            boolean isBlocked = messagesController.getStoriesController().isBlocked(storyView);
            boolean isUserSelf = UserObject.isUserSelf(user);
            String str = TextUtils.isEmpty(user.first_name) ? TextUtils.isEmpty(user.last_name) ? "" : user.last_name : user.first_name;
            int indexOf = str.indexOf(" ");
            if (indexOf > 2) {
                str = str.substring(0, indexOf);
            }
            final String str2 = str;
            if (isUserSelf) {
                return false;
            }
            boolean z4 = z3;
            ItemOptions cutTextInFancyHalf = ItemOptions.makeOptions(this.val$storyViewer.containerView, SelfStoryViewsPage.this.resourcesProvider, view).setGravity(3).ignoreX().setScrimViewBackground(new ColorDrawable(Theme.getColor(Theme.key_dialogBackground, SelfStoryViewsPage.this.resourcesProvider))).setDimAlpha(133).addIf((!isStoryShownToUser || isBlocked || z2 || isUserSelf) ? false : true, R.drawable.msg_stories_myhide, LocaleController.formatString(R.string.StoryHideFrom, str2), new Runnable() {
                @Override
                public final void run() {
                    SelfStoryViewsPage.AnonymousClass4.this.lambda$onItemClick$0(messagesController, user, str2, reactedUserHolderView, storyView);
                }
            }).makeMultiline(false).cutTextInFancyHalf().addIf((!isBlocked || z2 || isUserSelf) ? false : true, R.drawable.msg_menu_stories, LocaleController.formatString(R.string.StoryShowBackTo, str2), new Runnable() {
                @Override
                public final void run() {
                    SelfStoryViewsPage.AnonymousClass4.this.lambda$onItemClick$1(messagesController, user, str2, reactedUserHolderView, storyView);
                }
            }).makeMultiline(false).cutTextInFancyHalf();
            boolean z5 = (z4 || z2 || isUserSelf) ? false : true;
            int i2 = R.drawable.msg_user_remove;
            final ItemOptions addIf = cutTextInFancyHalf.addIf(z5, i2, (CharSequence) LocaleController.getString(R.string.BlockUser), true, new Runnable() {
                @Override
                public final void run() {
                    SelfStoryViewsPage.AnonymousClass4.this.lambda$onItemClick$2(messagesController, user, reactedUserHolderView, storyView);
                }
            }).addIf((z4 || !z2 || isUserSelf) ? false : true, R.drawable.msg_block, LocaleController.getString(R.string.Unblock), new Runnable() {
                @Override
                public final void run() {
                    SelfStoryViewsPage.AnonymousClass4.this.lambda$onItemClick$3(messagesController, user, reactedUserHolderView, storyView);
                }
            }).addIf(z4 && !isUserSelf, i2, (CharSequence) LocaleController.getString(R.string.StoryDeleteContact), true, new Runnable() {
                @Override
                public final void run() {
                    SelfStoryViewsPage.AnonymousClass4.this.lambda$onItemClick$4(user, str2, reactedUserHolderView, storyView);
                }
            });
            TLRPC.Reaction reaction = storyView.reaction;
            try {
                try {
                    if (reaction instanceof TLRPC.TL_reactionCustomEmoji) {
                        anonymousClass4 = this;
                        TLRPC.InputStickerSet findStickerSet = AnimatedEmojiDrawable.getDocumentFetcher(SelfStoryViewsPage.this.currentAccount).findStickerSet(((TLRPC.TL_reactionCustomEmoji) reaction).document_id);
                        if (findStickerSet != null) {
                            addIf.addGap();
                            final ArrayList arrayList = new ArrayList();
                            arrayList.add(findStickerSet);
                            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
                            MessageContainsEmojiButton messageContainsEmojiButton = new MessageContainsEmojiButton(selfStoryViewsPage.currentAccount, selfStoryViewsPage.getContext(), SelfStoryViewsPage.this.resourcesProvider, arrayList, 3);
                            messageContainsEmojiButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public final void onClick(View view2) {
                                    SelfStoryViewsPage.AnonymousClass4.this.lambda$onItemClick$5(arrayList, addIf, view2);
                                }
                            });
                            addIf.addView(messageContainsEmojiButton);
                            z = true;
                            if (addIf.getItemsCount() > 0 && !z) {
                                return false;
                            }
                            addIf.show();
                            SelfStoryViewsPage.this.performHapticFeedback(0, 1);
                            return true;
                        }
                    } else {
                        anonymousClass4 = this;
                    }
                    SelfStoryViewsPage.this.performHapticFeedback(0, 1);
                    return true;
                } catch (Exception unused) {
                    return true;
                }
            } catch (Exception unused2) {
                return true;
            }
            z = false;
            if (addIf.getItemsCount() > 0) {
            }
            addIf.show();
        }
    }

    public class AnonymousClass5 extends SearchField {
        Runnable runnable;

        AnonymousClass5(Context context, boolean z, float f, Theme.ResourcesProvider resourcesProvider) {
            super(context, z, f, resourcesProvider);
        }

        public void lambda$onTextChange$0(String str) {
            this.runnable = null;
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            selfStoryViewsPage.isSearchDebounce = false;
            selfStoryViewsPage.state.searchQuery = str.toLowerCase();
            SelfStoryViewsPage.this.reload();
        }

        @Override
        public void onTextChange(final String str) {
            Runnable runnable = this.runnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            this.runnable = new Runnable() {
                @Override
                public final void run() {
                    SelfStoryViewsPage.AnonymousClass5.this.lambda$onTextChange$0(str);
                }
            };
            if (TextUtils.isEmpty(str)) {
                this.runnable.run();
            } else {
                AndroidUtilities.runOnUIThread(this.runnable, 300L);
            }
            if (this.runnable != null) {
                SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
                if (selfStoryViewsPage.isSearchDebounce) {
                    return;
                }
                selfStoryViewsPage.isSearchDebounce = true;
                selfStoryViewsPage.listAdapter.updateRows();
                SelfStoryViewsPage selfStoryViewsPage2 = SelfStoryViewsPage.this;
                selfStoryViewsPage2.layoutManager.scrollToPositionWithOffset(0, -selfStoryViewsPage2.recyclerListView.getPaddingTop());
            }
        }
    }

    public static class FiltersState {
        boolean contactsOnly;
        String searchQuery;
        boolean sortByReactions = true;

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            FiltersState filtersState = (FiltersState) obj;
            return this.sortByReactions == filtersState.sortByReactions && this.contactsOnly == filtersState.contactsOnly && ((TextUtils.isEmpty(this.searchQuery) && TextUtils.isEmpty(filtersState.searchQuery)) || Objects.equals(this.searchQuery, filtersState.searchQuery));
        }

        public int hashCode() {
            return Objects.hash(Boolean.valueOf(this.sortByReactions), Boolean.valueOf(this.contactsOnly), this.searchQuery);
        }

        public void set(FiltersState filtersState) {
            this.sortByReactions = filtersState.sortByReactions;
            this.contactsOnly = filtersState.contactsOnly;
            this.searchQuery = filtersState.searchQuery;
        }
    }

    public class HeaderView extends FrameLayout {
        TextView allViewersView;
        float animateFromAlpha1;
        float animateFromAlpha2;
        RectF animateFromRect;
        float animationProgress;
        ValueAnimator animator;
        private final LinearLayout buttonContainer;
        TextView contactsViewersView;
        boolean lastSortType;
        RectF rectF;
        ReplaceableIconDrawable replacableDrawable;
        int selected;
        Paint selectedPaint;

        public class AnonymousClass1 extends CustomPopupMenu {
            AnonymousClass1(Context context, Theme.ResourcesProvider resourcesProvider, boolean z) {
                super(context, resourcesProvider, z);
            }

            public void lambda$onCreate$0(View view) {
                SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
                FiltersState filtersState = selfStoryViewsPage.state;
                if (!filtersState.sortByReactions) {
                    FiltersState filtersState2 = selfStoryViewsPage.sharedFilterState;
                    filtersState.sortByReactions = true;
                    if (filtersState2 != null) {
                        filtersState2.sortByReactions = true;
                    }
                    selfStoryViewsPage.updateViewState(true);
                    SelfStoryViewsPage.this.reload();
                    SelfStoryViewsPage selfStoryViewsPage2 = SelfStoryViewsPage.this;
                    selfStoryViewsPage2.onSharedStateChanged.accept(selfStoryViewsPage2);
                }
                if (SelfStoryViewsPage.this.popupMenu != null) {
                    SelfStoryViewsPage.this.popupMenu.dismiss();
                }
            }

            public void lambda$onCreate$1(View view) {
                SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
                FiltersState filtersState = selfStoryViewsPage.state;
                if (filtersState.sortByReactions) {
                    FiltersState filtersState2 = selfStoryViewsPage.sharedFilterState;
                    filtersState.sortByReactions = false;
                    if (filtersState2 != null) {
                        filtersState2.sortByReactions = false;
                    }
                    selfStoryViewsPage.updateViewState(true);
                    SelfStoryViewsPage.this.reload();
                    SelfStoryViewsPage selfStoryViewsPage2 = SelfStoryViewsPage.this;
                    selfStoryViewsPage2.onSharedStateChanged.accept(selfStoryViewsPage2);
                }
                if (SelfStoryViewsPage.this.popupMenu != null) {
                    SelfStoryViewsPage.this.popupMenu.dismiss();
                }
            }

            @Override
            protected void onCreate(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
                actionBarPopupWindowLayout.setBackgroundColor(ColorUtils.blendARGB(-16777216, -1, 0.18f));
                SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
                ViewsModel viewsModel = selfStoryViewsPage.currentModel;
                boolean z = viewsModel != null && viewsModel.isChannel;
                ActionBarMenuSubItem addItem = ActionBarMenuItem.addItem(actionBarPopupWindowLayout, z ? R.drawable.menu_views_reposts : selfStoryViewsPage.state.sortByReactions ? R.drawable.menu_views_reactions2 : R.drawable.menu_views_reactions, LocaleController.getString(z ? R.string.SortByReposts : R.string.SortByReactions), false, SelfStoryViewsPage.this.resourcesProvider);
                if (!SelfStoryViewsPage.this.state.sortByReactions) {
                    addItem.setAlpha(0.5f);
                }
                addItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        SelfStoryViewsPage.HeaderView.AnonymousClass1.this.lambda$onCreate$0(view);
                    }
                });
                ActionBarMenuSubItem addItem2 = ActionBarMenuItem.addItem(actionBarPopupWindowLayout, !SelfStoryViewsPage.this.state.sortByReactions ? R.drawable.menu_views_recent2 : R.drawable.menu_views_recent, LocaleController.getString(R.string.SortByTime), false, SelfStoryViewsPage.this.resourcesProvider);
                if (SelfStoryViewsPage.this.state.sortByReactions) {
                    addItem2.setAlpha(0.5f);
                }
                addItem2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        SelfStoryViewsPage.HeaderView.AnonymousClass1.this.lambda$onCreate$1(view);
                    }
                });
                ActionBarPopupWindow.GapView gapView = new ActionBarPopupWindow.GapView(HeaderView.this.getContext(), SelfStoryViewsPage.this.resourcesProvider, Theme.key_actionBarDefaultSubmenuSeparator);
                gapView.setTag(R.id.fit_width_tag, 1);
                actionBarPopupWindowLayout.addView((View) gapView, LayoutHelper.createLinear(-1, 8));
                ActionBarMenuItem.addText(actionBarPopupWindowLayout, LocaleController.getString(z ? R.string.StoryReactionsSortDescription : R.string.StoryViewsSortDescription), SelfStoryViewsPage.this.resourcesProvider);
            }

            @Override
            protected void onDismissed() {
            }
        }

        public HeaderView(Context context) {
            super(context);
            this.selectedPaint = new Paint(1);
            this.animateFromRect = new RectF();
            this.rectF = new RectF();
            this.animationProgress = 1.0f;
            Paint paint = this.selectedPaint;
            int i = Theme.key_listSelector;
            paint.setColor(Theme.getColor(i, SelfStoryViewsPage.this.resourcesProvider));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            TextView textView = new TextView(context);
            this.allViewersView = textView;
            textView.setText(LocaleController.getString(R.string.AllViewers));
            TextView textView2 = this.allViewersView;
            int i2 = Theme.key_dialogTextBlack;
            textView2.setTextColor(Theme.getColor(i2, SelfStoryViewsPage.this.resourcesProvider));
            this.allViewersView.setTextSize(1, 14.0f);
            this.allViewersView.setTypeface(AndroidUtilities.bold());
            this.allViewersView.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f));
            TextView textView3 = new TextView(context);
            this.contactsViewersView = textView3;
            textView3.setText(LocaleController.getString(R.string.Contacts));
            this.contactsViewersView.setTextColor(Theme.getColor(i2, SelfStoryViewsPage.this.resourcesProvider));
            this.contactsViewersView.setTextSize(1, 14.0f);
            this.contactsViewersView.setTypeface(AndroidUtilities.bold());
            this.contactsViewersView.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f));
            linearLayout.setPadding(0, AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f));
            linearLayout.addView(this.allViewersView, LayoutHelper.createLinear(-2, -2, 0, 13, 0, 0, 0));
            linearLayout.addView(this.contactsViewersView, LayoutHelper.createLinear(-2, -2, 0, 0, 0, 0, 0));
            LinearLayout linearLayout2 = new LinearLayout(getContext());
            this.buttonContainer = linearLayout2;
            linearLayout2.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
            linearLayout2.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(26.0f), Theme.getColor(i, SelfStoryViewsPage.this.resourcesProvider)));
            linearLayout2.setOrientation(0);
            ReplaceableIconDrawable replaceableIconDrawable = new ReplaceableIconDrawable(getContext());
            this.replacableDrawable = replaceableIconDrawable;
            replaceableIconDrawable.exactlyBounds = true;
            this.lastSortType = true;
            replaceableIconDrawable.setIcon(R.drawable.menu_views_reactions3, false);
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setImageDrawable(this.replacableDrawable);
            imageView.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
            linearLayout2.addView(imageView, LayoutHelper.createLinear(26, 26));
            ImageView imageView2 = new ImageView(getContext());
            imageView2.setImageResource(R.drawable.arrow_more);
            linearLayout2.addView(imageView2, LayoutHelper.createLinear(16, 26));
            addView(linearLayout, LayoutHelper.createFrame(-2, -2.0f));
            addView(linearLayout2, LayoutHelper.createFrame(-2, -2.0f, 5, 13.0f, 6.0f, 13.0f, 6.0f));
            this.allViewersView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SelfStoryViewsPage.HeaderView.this.lambda$new$0(view);
                }
            });
            this.contactsViewersView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SelfStoryViewsPage.HeaderView.this.lambda$new$1(view);
                }
            });
            linearLayout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    SelfStoryViewsPage.HeaderView.this.lambda$new$2(view);
                }
            });
        }

        public void lambda$new$0(View view) {
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            FiltersState filtersState = selfStoryViewsPage.state;
            if (filtersState.contactsOnly) {
                filtersState.contactsOnly = false;
                selfStoryViewsPage.updateViewState(true);
                SelfStoryViewsPage.this.reload();
            }
        }

        public void lambda$new$1(View view) {
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            FiltersState filtersState = selfStoryViewsPage.state;
            if (filtersState.contactsOnly) {
                return;
            }
            filtersState.contactsOnly = true;
            selfStoryViewsPage.updateViewState(true);
            SelfStoryViewsPage.this.reload();
        }

        public void lambda$new$2(View view) {
            SelfStoryViewsPage.this.popupMenu = new AnonymousClass1(getContext(), SelfStoryViewsPage.this.resourcesProvider, false);
            CustomPopupMenu customPopupMenu = SelfStoryViewsPage.this.popupMenu;
            LinearLayout linearLayout = this.buttonContainer;
            customPopupMenu.show(linearLayout, 0, (-linearLayout.getMeasuredHeight()) - AndroidUtilities.dp(8.0f));
        }

        public void lambda$setState$3(ValueAnimator valueAnimator) {
            this.animationProgress = ((Float) this.animator.getAnimatedValue()).floatValue();
            invalidate();
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            float f;
            if (SelfStoryViewsPage.this.showContactsFilter) {
                float f2 = 0.5f;
                if (this.selected == 0) {
                    this.allViewersView.getHitRect(AndroidUtilities.rectTmp2);
                    f = 0.5f;
                    f2 = 1.0f;
                } else {
                    this.contactsViewersView.getHitRect(AndroidUtilities.rectTmp2);
                    f = 1.0f;
                }
                this.rectF.set(AndroidUtilities.rectTmp2);
                float f3 = this.animationProgress;
                if (f3 != 1.0f) {
                    f2 = AndroidUtilities.lerp(this.animateFromAlpha1, f2, f3);
                    f = AndroidUtilities.lerp(this.animateFromAlpha2, f, this.animationProgress);
                    RectF rectF = this.animateFromRect;
                    RectF rectF2 = this.rectF;
                    AndroidUtilities.lerp(rectF, rectF2, this.animationProgress, rectF2);
                }
                this.allViewersView.setAlpha(f2);
                this.contactsViewersView.setAlpha(f);
                float height = this.rectF.height() / 2.0f;
                canvas.drawRoundRect(this.rectF, height, height, this.selectedPaint);
            }
            super.dispatchDraw(canvas);
        }

        public void setState(boolean z, boolean z2) {
            if (z == this.selected && z2) {
                return;
            }
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.animator.cancel();
            }
            this.selected = z ? 1 : 0;
            if (!z2) {
                this.animationProgress = 1.0f;
                invalidate();
                return;
            }
            this.animateFromRect.set(this.rectF);
            this.animateFromAlpha1 = this.allViewersView.getAlpha();
            this.animateFromAlpha2 = this.contactsViewersView.getAlpha();
            this.animationProgress = 0.0f;
            invalidate();
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.animator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    SelfStoryViewsPage.HeaderView.this.lambda$setState$3(valueAnimator2);
                }
            });
            this.animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    HeaderView headerView = HeaderView.this;
                    headerView.animator = null;
                    headerView.animationProgress = 1.0f;
                    headerView.invalidate();
                }
            });
            this.animator.setDuration(250L);
            this.animator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.animator.start();
        }
    }

    public static class Item {
        final TL_stories.StoryReaction reaction;
        final TL_stories.StoryView view;
        final int viewType;

        private Item(int i) {
            this.viewType = i;
            this.view = null;
            this.reaction = null;
        }

        private Item(int i, TL_stories.StoryReaction storyReaction) {
            this.viewType = i;
            this.view = null;
            this.reaction = storyReaction;
        }

        private Item(int i, TL_stories.StoryView storyView) {
            this.viewType = i;
            this.view = storyView;
            this.reaction = null;
        }
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        ArrayList items;

        private ListAdapter() {
            this.items = new ArrayList();
        }

        public void lambda$onCreateViewHolder$0() {
            SelfStoryViewsPage.this.showPremiumAlert();
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
            return viewHolder.getItemViewType() == 1;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r23, int r24) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.SelfStoryViewsPage.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FlickerLoadingView flickerLoadingView;
            int i2;
            View view;
            switch (i) {
                case 0:
                    view = new View(SelfStoryViewsPage.this.getContext()) {
                        @Override
                        protected void onMeasure(int i3, int i4) {
                            super.onMeasure(i3, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(SelfStoryViewsPage.this.TOP_PADDING), 1073741824));
                        }
                    };
                    break;
                case 1:
                    int i3 = ReactedUserHolderView.STYLE_STORY;
                    SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
                    view = new ReactedUserHolderView(i3, selfStoryViewsPage.currentAccount, selfStoryViewsPage.getContext(), SelfStoryViewsPage.this.resourcesProvider, false, true) {
                        @Override
                        public void openStory(long j, Runnable runnable) {
                            BaseFragment lastFragment = LaunchActivity.getLastFragment();
                            if (lastFragment == null) {
                                return;
                            }
                            StoryViewer createOverlayStoryViewer = lastFragment.createOverlayStoryViewer();
                            createOverlayStoryViewer.doOnAnimationReady(runnable);
                            createOverlayStoryViewer.open(getContext(), j, StoriesListPlaceProvider.of(SelfStoryViewsPage.this.recyclerListView));
                        }
                    };
                    break;
                case 2:
                case 9:
                default:
                    view = new View(SelfStoryViewsPage.this.getContext()) {
                        @Override
                        protected void onMeasure(int i4, int i5) {
                            int lastItemHeight = SelfStoryViewsPage.this.layoutManager.getLastItemHeight();
                            if (lastItemHeight >= SelfStoryViewsPage.this.recyclerListView.getPaddingTop() && !SelfStoryViewsPage.this.showSearch) {
                                lastItemHeight = 0;
                            }
                            super.onMeasure(i4, View.MeasureSpec.makeMeasureSpec(lastItemHeight, 1073741824));
                        }
                    };
                    break;
                case 3:
                    view = new FixedHeightEmptyCell(SelfStoryViewsPage.this.getContext(), 70);
                    break;
                case 4:
                    flickerLoadingView = new FlickerLoadingView(SelfStoryViewsPage.this.getContext(), SelfStoryViewsPage.this.resourcesProvider);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setViewType(28);
                    flickerLoadingView.showDate(false);
                    view = flickerLoadingView;
                    break;
                case 5:
                case 7:
                case 8:
                case 10:
                    StickerEmptyView stickerEmptyView = new StickerEmptyView(SelfStoryViewsPage.this.getContext(), null, SelfStoryViewsPage.this.defaultModel.isExpiredViews ? 12 : (i == 10 || i == 7 || i == 8 || i == 5) ? 1 : 0, SelfStoryViewsPage.this.resourcesProvider) {
                        @Override
                        protected void onMeasure(int i4, int i5) {
                            super.onMeasure(i4, View.MeasureSpec.makeMeasureSpec((SelfStoryViewsPage.this.measuerdHeight - SelfStoryViewsPage.this.recyclerListView.getPaddingTop()) - AndroidUtilities.dp(SelfStoryViewsPage.this.TOP_PADDING), 1073741824));
                        }
                    };
                    if (i == 7) {
                        stickerEmptyView.title.setVisibility(8);
                        i2 = R.string.NoResult;
                    } else if (i == 8) {
                        stickerEmptyView.title.setVisibility(8);
                        i2 = R.string.NoContactsViewed;
                    } else if (i == 10) {
                        stickerEmptyView.title.setVisibility(0);
                        stickerEmptyView.title.setText(LocaleController.getString(R.string.ServerErrorViewersTitle));
                        i2 = R.string.ServerErrorViewers;
                    } else if (SelfStoryViewsPage.this.defaultModel.isExpiredViews) {
                        stickerEmptyView.title.setVisibility(8);
                        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                        spannableStringBuilder.append((CharSequence) AndroidUtilities.replaceTags(LocaleController.getString(R.string.ExpiredViewsStub)));
                        if (!MessagesController.getInstance(SelfStoryViewsPage.this.currentAccount).premiumFeaturesBlocked()) {
                            spannableStringBuilder.append((CharSequence) "\n\n");
                            String string = LocaleController.getString(R.string.ExpiredViewsStubPremiumDescription);
                            final SelfStoryViewsPage selfStoryViewsPage2 = SelfStoryViewsPage.this;
                            spannableStringBuilder.append((CharSequence) AndroidUtilities.replaceSingleTag(string, new Runnable() {
                                @Override
                                public final void run() {
                                    SelfStoryViewsPage.access$1300(SelfStoryViewsPage.this);
                                }
                            }));
                            String string2 = LocaleController.getString(R.string.LearnMore);
                            final SelfStoryViewsPage selfStoryViewsPage3 = SelfStoryViewsPage.this;
                            stickerEmptyView.createButtonLayout(string2, new Runnable() {
                                @Override
                                public final void run() {
                                    SelfStoryViewsPage.access$1300(SelfStoryViewsPage.this);
                                }
                            });
                        }
                        stickerEmptyView.subtitle.setText(spannableStringBuilder);
                        stickerEmptyView.showProgress(false, false);
                        view = stickerEmptyView;
                        break;
                    } else {
                        stickerEmptyView.title.setVisibility(0);
                        if (SelfStoryViewsPage.this.defaultModel.isChannel) {
                            stickerEmptyView.title.setText(LocaleController.getString(R.string.NoReactions));
                            i2 = R.string.NoReactionsStub;
                        } else {
                            stickerEmptyView.title.setText(LocaleController.getString(R.string.NoViews));
                            i2 = R.string.NoViewsStub;
                        }
                    }
                    stickerEmptyView.setSubtitle(LocaleController.getString(i2));
                    stickerEmptyView.showProgress(false, false);
                    view = stickerEmptyView;
                case 6:
                    flickerLoadingView = new FlickerLoadingView(SelfStoryViewsPage.this.getContext(), SelfStoryViewsPage.this.resourcesProvider);
                    flickerLoadingView.setIsSingleCell(true);
                    flickerLoadingView.setIgnoreHeightCheck(true);
                    flickerLoadingView.setItemsCount(20);
                    flickerLoadingView.setViewType(28);
                    flickerLoadingView.showDate(false);
                    view = flickerLoadingView;
                    break;
                case 11:
                case 12:
                    LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(SelfStoryViewsPage.this.getContext());
                    linksTextView.setTextSize(1, 13.0f);
                    linksTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText, SelfStoryViewsPage.this.resourcesProvider));
                    linksTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, SelfStoryViewsPage.this.resourcesProvider));
                    int dp = AndroidUtilities.dp(16.0f);
                    int dp2 = AndroidUtilities.dp(21.0f);
                    linksTextView.setPadding(dp2, dp, dp2, dp);
                    linksTextView.setMaxLines(Integer.MAX_VALUE);
                    linksTextView.setGravity(17);
                    linksTextView.setDisablePaddingsOffsetY(true);
                    linksTextView.setText(i == 11 ? AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.StoryViewsPremiumHint), new Runnable() {
                        @Override
                        public final void run() {
                            SelfStoryViewsPage.ListAdapter.this.lambda$onCreateViewHolder$0();
                        }
                    }) : LocaleController.getString(R.string.ServerErrorViewersFull));
                    linksTextView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                    view = linksTextView;
                    break;
            }
            return new RecyclerListView.Holder(view);
        }

        public void updateRows() {
            ArrayList arrayList;
            Item item;
            this.items.clear();
            SelfStoryViewsPage selfStoryViewsPage = SelfStoryViewsPage.this;
            ViewsModel viewsModel = selfStoryViewsPage.currentModel;
            int i = 6;
            int i2 = 0;
            if (selfStoryViewsPage.isSearchDebounce) {
                this.items.add(new Item(i2));
                arrayList = this.items;
                item = new Item(i);
            } else {
                this.items.add(new Item(i2));
                if (viewsModel == null || viewsModel.getCount() > 0 || (!viewsModel.isExpiredViews && (viewsModel.loading || viewsModel.hasNext))) {
                    if (viewsModel != null) {
                        int i3 = 1;
                        if (viewsModel.isChannel) {
                            while (i2 < viewsModel.reactions.size()) {
                                this.items.add(new Item(i3, (TL_stories.StoryReaction) viewsModel.reactions.get(i2)));
                                i2++;
                            }
                        } else {
                            while (i2 < viewsModel.views.size()) {
                                this.items.add(new Item(i3, (TL_stories.StoryView) viewsModel.views.get(i2)));
                                i2++;
                            }
                        }
                    }
                    if (viewsModel == null || !(viewsModel.loading || viewsModel.hasNext)) {
                        if (viewsModel == null || !viewsModel.showReactionOnly) {
                            if (viewsModel != null && viewsModel.getCount() < viewsModel.totalCount && TextUtils.isEmpty(viewsModel.state.searchQuery) && !viewsModel.state.contactsOnly) {
                                arrayList = this.items;
                                item = new Item(12);
                            }
                            this.items.add(new Item(9));
                            notifyDataSetChanged();
                        }
                        arrayList = this.items;
                        item = new Item(11);
                    } else if (viewsModel.getCount() <= 0) {
                        arrayList = this.items;
                        item = new Item(i);
                    } else {
                        arrayList = this.items;
                        item = new Item(4);
                    }
                } else if (TextUtils.isEmpty(viewsModel.state.searchQuery)) {
                    int i4 = 5;
                    if (viewsModel.isExpiredViews) {
                        arrayList = this.items;
                        item = new Item(i4);
                    } else {
                        int i5 = viewsModel.totalCount;
                        if (i5 > 0 && viewsModel.state.contactsOnly) {
                            arrayList = this.items;
                            item = new Item(8);
                        } else if (i5 > 0) {
                            arrayList = this.items;
                            item = new Item(10);
                        } else {
                            arrayList = this.items;
                            item = new Item(i4);
                        }
                    }
                } else {
                    arrayList = this.items;
                    item = new Item(7);
                }
            }
            arrayList.add(item);
            this.items.add(new Item(9));
            notifyDataSetChanged();
        }
    }

    private class RecyclerListViewInner extends RecyclerListView implements StoriesListPlaceProvider.ClippedView {
        public RecyclerListViewInner(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
        }

        @Override
        public void updateClip(int[] iArr) {
            iArr[0] = AndroidUtilities.dp(SelfStoryViewsPage.this.TOP_PADDING);
            iArr[1] = getMeasuredHeight();
        }
    }

    public static class ViewsModel {
        int currentAccount;
        private long dialogId;
        boolean initial;
        public final boolean isChannel;
        boolean isExpiredViews;
        boolean loading;
        String offset;
        boolean showReactionOnly;
        TL_stories.StoryItem storyItem;
        public int totalCount;
        boolean useLocalFilters;
        ArrayList views = new ArrayList();
        ArrayList originalViews = new ArrayList();
        ArrayList reactions = new ArrayList();
        boolean hasNext = true;
        int reqId = -1;
        HashSet animateDateForUsers = new HashSet();
        ArrayList listeners = new ArrayList();
        FiltersState state = new FiltersState();

        public ViewsModel(int i, long j, TL_stories.StoryItem storyItem, boolean z) {
            TL_stories.StoryViews storyViews;
            this.currentAccount = i;
            this.storyItem = storyItem;
            this.isChannel = j < 0;
            this.dialogId = j;
            TL_stories.StoryViews storyViews2 = storyItem.views;
            int i2 = storyViews2 == null ? 0 : storyViews2.views_count;
            this.totalCount = i2;
            if (i2 < 200) {
                this.useLocalFilters = true;
            }
            boolean z2 = StoriesUtilities.hasExpiredViews(storyItem) && !UserConfig.getInstance(i).isPremium();
            this.isExpiredViews = z2;
            if (z2 && (storyViews = storyItem.views) != null && storyViews.reactions_count > 0) {
                this.isExpiredViews = false;
                this.showReactionOnly = true;
            }
            if (this.isExpiredViews) {
                return;
            }
            this.initial = true;
            if (storyItem.views == null || !z) {
                return;
            }
            for (int i3 = 0; i3 < storyItem.views.recent_viewers.size(); i3++) {
                Long l = storyItem.views.recent_viewers.get(i3);
                long longValue = l.longValue();
                if (MessagesController.getInstance(i).getUser(l) != null) {
                    TL_stories.TL_storyView tL_storyView = new TL_stories.TL_storyView();
                    tL_storyView.user_id = longValue;
                    tL_storyView.date = 0;
                    this.views.add(tL_storyView);
                }
            }
        }

        private void applyLocalFilter() {
            String str;
            String str2;
            String str3;
            String str4;
            if (this.isChannel) {
                return;
            }
            this.views.clear();
            FiltersState filtersState = this.state;
            if (filtersState.contactsOnly || !TextUtils.isEmpty(filtersState.searchQuery)) {
                if (TextUtils.isEmpty(this.state.searchQuery)) {
                    str = null;
                    str2 = null;
                    str3 = null;
                    str4 = null;
                } else {
                    str = this.state.searchQuery.trim().toLowerCase();
                    str2 = LocaleController.getInstance().getTranslitString(str);
                    str3 = " " + str;
                    str4 = " " + str2;
                }
                for (int i = 0; i < this.originalViews.size(); i++) {
                    TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(((TL_stories.StoryView) this.originalViews.get(i)).user_id));
                    boolean z = !this.state.contactsOnly || (user != null && user.contact);
                    if (z && str != null) {
                        String lowerCase = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                        String publicUsername = UserObject.getPublicUsername(user);
                        String translitSafe = AndroidUtilities.translitSafe(lowerCase);
                        if ((lowerCase == null || (!lowerCase.startsWith(str) && !lowerCase.contains(str3))) && ((translitSafe == null || (!translitSafe.startsWith(str2) && !translitSafe.contains(str4))) && (publicUsername == null || (!publicUsername.startsWith(str2) && !publicUsername.contains(str4))))) {
                            z = false;
                        }
                    }
                    if (z) {
                        this.views.add((TL_stories.StoryView) this.originalViews.get(i));
                    }
                }
            } else {
                this.views.addAll(this.originalViews);
            }
            if (this.state.sortByReactions) {
                return;
            }
            Collections.sort(this.views, Comparator$CC.comparingInt(new ToIntFunction() {
                @Override
                public final int applyAsInt(Object obj) {
                    int lambda$applyLocalFilter$4;
                    lambda$applyLocalFilter$4 = SelfStoryViewsPage.ViewsModel.lambda$applyLocalFilter$4((TL_stories.StoryView) obj);
                    return lambda$applyLocalFilter$4;
                }
            }));
        }

        public static int lambda$applyLocalFilter$4(TL_stories.StoryView storyView) {
            return -storyView.date;
        }

        public void lambda$loadNext$0(int[] iArr, TLObject tLObject, TLRPC.TL_error tL_error) {
            if (iArr[0] != this.reqId) {
                FileLog.d("SelfStoryViewsPage reactions " + this.storyItem.id + " localId != reqId");
                return;
            }
            this.loading = false;
            this.reqId = -1;
            if (tLObject != null) {
                TL_stories.TL_storyReactionsList tL_storyReactionsList = (TL_stories.TL_storyReactionsList) tLObject;
                MessagesController.getInstance(this.currentAccount).putUsers(tL_storyReactionsList.users, false);
                MessagesController.getInstance(this.currentAccount).putChats(tL_storyReactionsList.chats, false);
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(tL_storyReactionsList.users, tL_storyReactionsList.chats, true, false);
                if (this.initial) {
                    this.initial = false;
                    for (int i = 0; i < this.reactions.size(); i++) {
                        this.animateDateForUsers.add(Long.valueOf(DialogObject.getPeerDialogId(((TL_stories.StoryReaction) this.reactions.get(i)).peer_id)));
                    }
                    this.reactions.clear();
                    this.originalViews.clear();
                }
                this.reactions.addAll(tL_storyReactionsList.reactions);
                if (tL_storyReactionsList.reactions.isEmpty()) {
                    this.hasNext = false;
                } else {
                    this.hasNext = true;
                }
                String str = tL_storyReactionsList.next_offset;
                this.offset = str;
                if (TextUtils.isEmpty(str)) {
                    this.hasNext = false;
                }
                TL_stories.StoryItem storyItem = this.storyItem;
                if (storyItem.views == null) {
                    storyItem.views = new TL_stories.TL_storyViews();
                }
                int i2 = this.totalCount;
                int i3 = tL_storyReactionsList.count;
                boolean z = i2 != i3;
                this.totalCount = i3;
                if (z) {
                    NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesUpdated, new Object[0]);
                }
            } else {
                if (tL_error != null && "MSG_ID_INVALID".equals(tL_error.text)) {
                    this.totalCount = 0;
                }
                this.hasNext = false;
            }
            FileLog.d("SelfStoryViewsPage reactions " + this.storyItem.id + " response  totalItems " + this.reactions.size() + " has next " + this.hasNext);
            for (int i4 = 0; i4 < this.listeners.size(); i4++) {
                ((SelfStoryViewsPage) this.listeners.get(i4)).onDataRecieved(this);
            }
            if (this.reactions.size() >= 20 || !this.hasNext) {
                return;
            }
            loadNext();
        }

        public void lambda$loadNext$1(final int[] iArr, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SelfStoryViewsPage.ViewsModel.this.lambda$loadNext$0(iArr, tLObject, tL_error);
                }
            });
        }

        public void lambda$loadNext$2(int[] iArr, TLObject tLObject, TLRPC.TL_error tL_error) {
            boolean z;
            if (iArr[0] != this.reqId) {
                FileLog.d("SelfStoryViewsPage " + this.storyItem.id + " localId != reqId");
                return;
            }
            this.loading = false;
            this.reqId = -1;
            if (tLObject != null) {
                TL_stories.StoryViewsList storyViewsList = (TL_stories.StoryViewsList) tLObject;
                MessagesController.getInstance(this.currentAccount).getStoriesController().applyStoryViewsBlocked(storyViewsList);
                MessagesController.getInstance(this.currentAccount).putUsers(storyViewsList.users, false);
                MessagesController.getInstance(this.currentAccount).putChats(storyViewsList.chats, false);
                boolean z2 = true;
                MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(storyViewsList.users, storyViewsList.chats, true, false);
                if (this.initial) {
                    this.initial = false;
                    for (int i = 0; i < this.views.size(); i++) {
                        this.animateDateForUsers.add(Long.valueOf(((TL_stories.StoryView) this.views.get(i)).user_id));
                    }
                    this.views.clear();
                    this.originalViews.clear();
                }
                if (this.useLocalFilters) {
                    this.originalViews.addAll(storyViewsList.views);
                    applyLocalFilter();
                } else {
                    this.views.addAll(storyViewsList.views);
                }
                if (storyViewsList.views.isEmpty()) {
                    this.hasNext = false;
                } else {
                    this.hasNext = true;
                }
                String str = storyViewsList.next_offset;
                this.offset = str;
                if (TextUtils.isEmpty(str)) {
                    this.hasNext = false;
                }
                TL_stories.StoryItem storyItem = this.storyItem;
                if (storyItem.views == null) {
                    storyItem.views = new TL_stories.TL_storyViews();
                }
                int i2 = storyViewsList.count;
                TL_stories.StoryViews storyViews = this.storyItem.views;
                if (i2 > storyViews.views_count) {
                    storyViews.recent_viewers.clear();
                    for (int i3 = 0; i3 < Math.min(3, storyViewsList.users.size()); i3++) {
                        this.storyItem.views.recent_viewers.add(Long.valueOf(storyViewsList.users.get(i3).id));
                    }
                    this.storyItem.views.views_count = storyViewsList.count;
                    z = true;
                } else {
                    z = false;
                }
                TL_stories.StoryViews storyViews2 = this.storyItem.views;
                int i4 = storyViews2.reactions_count;
                int i5 = storyViewsList.reactions_count;
                if (i4 != i5) {
                    storyViews2.reactions_count = i5;
                } else {
                    z2 = z;
                }
                if (z2) {
                    NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesUpdated, new Object[0]);
                }
            } else {
                if (tL_error != null && "MSG_ID_INVALID".equals(tL_error.text)) {
                    this.totalCount = 0;
                }
                this.hasNext = false;
            }
            FileLog.d("SelfStoryViewsPage " + this.storyItem.id + " response  totalItems " + this.views.size() + " has next " + this.hasNext);
            for (int i6 = 0; i6 < this.listeners.size(); i6++) {
                ((SelfStoryViewsPage) this.listeners.get(i6)).onDataRecieved(this);
            }
            if (this.views.size() >= 20 || !this.hasNext) {
                return;
            }
            loadNext();
        }

        public void lambda$loadNext$3(final int[] iArr, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SelfStoryViewsPage.ViewsModel.this.lambda$loadNext$2(iArr, tLObject, tL_error);
                }
            });
        }

        public void addListener(SelfStoryViewsPage selfStoryViewsPage) {
            if (this.listeners.contains(selfStoryViewsPage)) {
                return;
            }
            this.listeners.add(selfStoryViewsPage);
        }

        public int getCount() {
            return (this.isChannel ? this.reactions : this.views).size();
        }

        public void loadNext() {
            if (this.loading || !this.hasNext || this.isExpiredViews) {
                return;
            }
            if (this.isChannel) {
                TL_stories.TL_getStoryReactionsList tL_getStoryReactionsList = new TL_stories.TL_getStoryReactionsList();
                tL_getStoryReactionsList.forwards_first = this.state.sortByReactions;
                tL_getStoryReactionsList.id = this.storyItem.id;
                tL_getStoryReactionsList.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
                tL_getStoryReactionsList.limit = (this.initial || this.reactions.size() < 20) ? 20 : 100;
                String str = this.offset;
                tL_getStoryReactionsList.offset = str;
                if (str == null) {
                    tL_getStoryReactionsList.offset = "";
                } else {
                    tL_getStoryReactionsList.flags |= 2;
                }
                this.loading = true;
                FileLog.d("SelfStoryViewsPage reactions load next " + this.storyItem.id + " " + this.initial + " offset=" + tL_getStoryReactionsList.offset);
                int sendRequest = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_getStoryReactionsList, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        SelfStoryViewsPage.ViewsModel.this.lambda$loadNext$1(r2, tLObject, tL_error);
                    }
                });
                this.reqId = sendRequest;
                final int[] iArr = {sendRequest};
                return;
            }
            TL_stories.TL_stories_getStoryViewsList tL_stories_getStoryViewsList = new TL_stories.TL_stories_getStoryViewsList();
            tL_stories_getStoryViewsList.id = this.storyItem.id;
            tL_stories_getStoryViewsList.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            if (this.useLocalFilters) {
                tL_stories_getStoryViewsList.q = "";
                tL_stories_getStoryViewsList.just_contacts = false;
                tL_stories_getStoryViewsList.reactions_first = true;
            } else {
                String str2 = this.state.searchQuery;
                tL_stories_getStoryViewsList.q = str2;
                if (!TextUtils.isEmpty(str2)) {
                    tL_stories_getStoryViewsList.flags |= 2;
                }
                FiltersState filtersState = this.state;
                tL_stories_getStoryViewsList.just_contacts = filtersState.contactsOnly;
                tL_stories_getStoryViewsList.reactions_first = filtersState.sortByReactions;
            }
            tL_stories_getStoryViewsList.limit = (this.initial || this.views.size() < 20) ? 20 : 100;
            String str3 = this.offset;
            tL_stories_getStoryViewsList.offset = str3;
            if (str3 == null) {
                tL_stories_getStoryViewsList.offset = "";
            }
            this.loading = true;
            FileLog.d("SelfStoryViewsPage load next " + this.storyItem.id + " " + this.initial + " offset=" + tL_stories_getStoryViewsList.offset + " q" + tL_stories_getStoryViewsList.q + " " + tL_stories_getStoryViewsList.just_contacts + " " + tL_stories_getStoryViewsList.reactions_first);
            int sendRequest2 = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_stories_getStoryViewsList, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    SelfStoryViewsPage.ViewsModel.this.lambda$loadNext$3(r2, tLObject, tL_error);
                }
            });
            this.reqId = sendRequest2;
            final int[] iArr2 = {sendRequest2};
        }

        public void release() {
            if (this.reqId >= 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, false);
            }
            this.reqId = -1;
        }

        public void reloadIfNeed(FiltersState filtersState, boolean z, boolean z2) {
            FiltersState filtersState2 = new FiltersState();
            filtersState2.set(filtersState);
            if (!z) {
                filtersState2.contactsOnly = false;
            }
            if (!z2) {
                filtersState2.sortByReactions = true;
            }
            if (this.state.equals(filtersState2)) {
                return;
            }
            this.state.set(filtersState2);
            if (!this.isChannel && this.useLocalFilters) {
                applyLocalFilter();
                for (int i = 0; i < this.listeners.size(); i++) {
                    ((SelfStoryViewsPage) this.listeners.get(i)).onDataRecieved(this);
                }
                return;
            }
            release();
            this.views.clear();
            this.reactions.clear();
            this.initial = true;
            this.loading = false;
            this.hasNext = true;
            this.offset = "";
            loadNext();
        }

        public void removeListener(SelfStoryViewsPage selfStoryViewsPage) {
            this.listeners.remove(selfStoryViewsPage);
        }
    }

    public SelfStoryViewsPage(final StoryViewer storyViewer, Context context, FiltersState filtersState, Consumer consumer) {
        super(context);
        this.TOP_PADDING = 96;
        this.state = new FiltersState();
        this.sharedFilterState = filtersState;
        this.onSharedStateChanged = consumer;
        this.resourcesProvider = storyViewer.resourcesProvider;
        this.storyViewer = storyViewer;
        this.currentAccount = storyViewer.currentAccount;
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, this.resourcesProvider));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(8.0f));
        this.headerView = new HeaderView(getContext());
        RecyclerListViewInner recyclerListViewInner = new RecyclerListViewInner(context, this.resourcesProvider) {
            @Override
            public void onMeasure(int i, int i2) {
                SelfStoryViewsPage.this.measuerdHeight = View.MeasureSpec.getSize(i2);
                super.onMeasure(i, i2);
            }
        };
        this.recyclerListView = recyclerListViewInner;
        recyclerListViewInner.setClipToPadding(false);
        this.recyclerItemsEnterAnimator = new RecyclerItemsEnterAnimator(this.recyclerListView, true);
        RecyclerListView recyclerListView = this.recyclerListView;
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(context, 0, recyclerListView);
        this.layoutManager = fillLastLinearLayoutManager;
        recyclerListView.setLayoutManager(fillLastLinearLayoutManager);
        this.recyclerListView.setNestedScrollingEnabled(true);
        RecyclerListView recyclerListView2 = this.recyclerListView;
        ListAdapter listAdapter = new ListAdapter();
        this.listAdapter = listAdapter;
        recyclerListView2.setAdapter(listAdapter);
        RecyclerAnimationScrollHelper recyclerAnimationScrollHelper = new RecyclerAnimationScrollHelper(this.recyclerListView, this.layoutManager);
        this.scrollHelper = recyclerAnimationScrollHelper;
        recyclerAnimationScrollHelper.setScrollListener(new RecyclerAnimationScrollHelper.ScrollListener() {
            @Override
            public void onScroll() {
                SelfStoryViewsPage.this.invalidate();
            }
        });
        addView(this.recyclerListView);
        this.scroller = new RecyclerListViewScroller(this.recyclerListView);
        this.recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                super.onScrollStateChanged(recyclerView, i);
                if (i == 0) {
                    SelfStoryViewsPage.this.checkAutoscroll = true;
                    SelfStoryViewsPage.this.invalidate();
                }
                if (i == 1) {
                    SelfStoryViewsPage.this.checkAutoscroll = false;
                    SelfStoryViewsPage.this.scroller.cancel();
                    AndroidUtilities.hideKeyboard(SelfStoryViewsPage.this);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                SelfStoryViewsPage.this.checkLoadMore();
                SelfStoryViewsPage.this.invalidate();
            }
        });
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                SelfStoryViewsPage.this.lambda$new$0(storyViewer, view, i);
            }
        });
        this.recyclerListView.setOnItemLongClickListener(new AnonymousClass4(storyViewer));
        this.listAdapter.updateRows();
        FrameLayout frameLayout = new FrameLayout(getContext());
        this.topViewsContainer = frameLayout;
        View view = new View(getContext());
        this.shadowView = view;
        GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TOP_BOTTOM;
        int i = Theme.key_dialogBackground;
        view.setBackground(new GradientDrawable(orientation, new int[]{Theme.getColor(i, this.resourcesProvider), 0}));
        frameLayout.addView(view, LayoutHelper.createFrame(-1, 8.0f, 0, 0.0f, this.TOP_PADDING - 8, 0.0f, 0.0f));
        View view2 = new View(getContext());
        this.shadowView2 = view2;
        view2.setBackgroundColor(Theme.getColor(i, this.resourcesProvider));
        frameLayout.addView(view2, LayoutHelper.createFrame(-1, 10.0f, 0, 0.0f, this.TOP_PADDING - 17, 0.0f, 0.0f));
        frameLayout.addView(this.headerView);
        frameLayout.addView(textView);
        AnonymousClass5 anonymousClass5 = new AnonymousClass5(getContext(), true, 13.0f, this.resourcesProvider);
        this.searchField = anonymousClass5;
        anonymousClass5.setHint(LocaleController.getString(R.string.Search));
        frameLayout.addView(this.searchField, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 36.0f, 0.0f, 0.0f));
        addView(frameLayout);
    }

    public static void access$1300(SelfStoryViewsPage selfStoryViewsPage) {
        selfStoryViewsPage.showPremiumAlert();
    }

    public void checkLoadMore() {
        if (this.currentModel == null || this.layoutManager.findLastVisibleItemPosition() <= this.listAdapter.getItemCount() - 10) {
            return;
        }
        this.currentModel.loadNext();
    }

    public boolean isStoryShownToUser(TL_stories.StoryView storyView) {
        StoryEntry storyEntry;
        StoryPrivacyBottomSheet.StoryPrivacy storyPrivacy;
        if (storyView == null) {
            return true;
        }
        if (MessagesController.getInstance(this.currentAccount).getStoriesController().isBlocked(storyView) || MessagesController.getInstance(this.currentAccount).blockePeers.indexOfKey(storyView.user_id) >= 0) {
            return false;
        }
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(storyView.user_id));
        SelfStoryViewsView.StoryItemInternal storyItemInternal = this.storyItem;
        if (storyItemInternal != null) {
            TL_stories.StoryItem storyItem = storyItemInternal.storyItem;
            if (storyItem != null) {
                if (storyItem.parsedPrivacy == null) {
                    storyItem.parsedPrivacy = new StoryPrivacyBottomSheet.StoryPrivacy(this.currentAccount, storyItem.privacy);
                }
                return this.storyItem.storyItem.parsedPrivacy.containsUser(user);
            }
            StoriesController.UploadingStory uploadingStory = storyItemInternal.uploadingStory;
            if (uploadingStory != null && (storyEntry = uploadingStory.entry) != null && (storyPrivacy = storyEntry.privacy) != null) {
                return storyPrivacy.containsUser(user);
            }
        }
        return true;
    }

    public void lambda$new$0(StoryViewer storyViewer, View view, int i) {
        String str;
        BaseFragment chatActivity;
        StoryViewer createOverlayStoryViewer;
        Context context;
        TL_stories.StoryItem storyItem;
        long peerDialogId;
        if (i < 0 || i >= this.listAdapter.items.size()) {
            return;
        }
        Item item = (Item) this.listAdapter.items.get(i);
        TL_stories.StoryView storyView = item.view;
        if (!(storyView instanceof TL_stories.TL_storyView)) {
            if (storyView instanceof TL_stories.TL_storyViewPublicRepost) {
                createOverlayStoryViewer = storyViewer.fragment.createOverlayStoryViewer();
                context = getContext();
                storyItem = ((TL_stories.TL_storyViewPublicRepost) item.view).story;
            } else {
                TL_stories.StoryReaction storyReaction = item.reaction;
                if (storyReaction instanceof TL_stories.TL_storyReaction) {
                    peerDialogId = DialogObject.getPeerDialogId(storyReaction.peer_id);
                } else {
                    if (!(storyReaction instanceof TL_stories.TL_storyReactionPublicRepost)) {
                        boolean z = storyReaction instanceof TL_stories.TL_storyReactionPublicForward;
                        if (z || (storyView instanceof TL_stories.TL_storyViewPublicForward)) {
                            TLRPC.Message message = z ? storyReaction.message : storyView.message;
                            Bundle bundle = new Bundle();
                            long peerDialogId2 = DialogObject.getPeerDialogId(message.peer_id);
                            if (peerDialogId2 >= 0) {
                                str = "user_id";
                            } else {
                                peerDialogId2 = -peerDialogId2;
                                str = "chat_id";
                            }
                            bundle.putLong(str, peerDialogId2);
                            bundle.putInt("message_id", message.id);
                            chatActivity = new ChatActivity(bundle);
                            storyViewer.presentFragment(chatActivity);
                        }
                        return;
                    }
                    createOverlayStoryViewer = storyViewer.fragment.createOverlayStoryViewer();
                    context = getContext();
                    storyItem = ((TL_stories.TL_storyReactionPublicRepost) item.reaction).story;
                }
            }
            createOverlayStoryViewer.open(context, storyItem, StoriesListPlaceProvider.of(this.recyclerListView));
            return;
        }
        peerDialogId = storyView.user_id;
        chatActivity = ProfileActivity.of(peerDialogId);
        storyViewer.presentFragment(chatActivity);
    }

    public static void preload(int i, long j, TL_stories.StoryItem storyItem) {
        if (storyItem == null) {
            return;
        }
        SparseArray sparseArray = (SparseArray) MessagesController.getInstance(i).storiesController.selfViewsModel.get(storyItem.dialogId);
        ViewsModel viewsModel = sparseArray == null ? null : (ViewsModel) sparseArray.get(storyItem.id);
        TL_stories.StoryViews storyViews = storyItem.views;
        int i2 = storyViews == null ? 0 : storyViews.views_count;
        if (viewsModel == null || viewsModel.totalCount != i2) {
            if (viewsModel != null) {
                viewsModel.release();
            }
            ViewsModel viewsModel2 = new ViewsModel(i, j, storyItem, true);
            viewsModel2.loadNext();
            if (sparseArray == null) {
                LongSparseArray longSparseArray = MessagesController.getInstance(i).storiesController.selfViewsModel;
                long j2 = storyItem.dialogId;
                sparseArray = new SparseArray();
                longSparseArray.put(j2, sparseArray);
            }
            sparseArray.put(storyItem.id, viewsModel2);
        }
    }

    public void reload() {
        ViewsModel viewsModel = this.currentModel;
        if (viewsModel != null) {
            viewsModel.removeListener(this);
        }
        ViewsModel viewsModel2 = this.defaultModel;
        this.currentModel = viewsModel2;
        if (viewsModel2 == null) {
            return;
        }
        viewsModel2.addListener(this);
        this.currentModel.reloadIfNeed(this.state, this.showContactsFilter, this.showReactionsSort);
        this.listAdapter.updateRows();
        this.layoutManager.scrollToPositionWithOffset(0, (int) (getTopOffset() - this.recyclerListView.getPaddingTop()));
    }

    public void showPremiumAlert() {
        new PremiumFeatureBottomSheet(this.storyViewer.fragment, 14, false).show();
    }

    public void updateViewState(boolean z) {
        int i;
        this.headerView.setState(this.state.contactsOnly, z);
        HeaderView headerView = this.headerView;
        boolean z2 = this.state.sortByReactions;
        headerView.lastSortType = z2;
        ReplaceableIconDrawable replaceableIconDrawable = headerView.replacableDrawable;
        if (z2) {
            ViewsModel viewsModel = this.currentModel;
            i = (viewsModel == null || !viewsModel.isChannel) ? R.drawable.menu_views_reactions3 : R.drawable.menu_views_reposts3;
        } else {
            i = R.drawable.menu_views_recent3;
        }
        replaceableIconDrawable.setIcon(i, z);
    }

    private void updateViewsVisibility() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.SelfStoryViewsPage.updateViewsVisibility():void");
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int childAdapterPosition;
        TL_stories.PeerStories stories;
        int i3 = 0;
        if (i != NotificationCenter.storiesUpdated) {
            if (i == NotificationCenter.storiesBlocklistUpdate) {
                while (i3 < this.recyclerListView.getChildCount()) {
                    View childAt = this.recyclerListView.getChildAt(i3);
                    if ((childAt instanceof ReactedUserHolderView) && (childAdapterPosition = this.recyclerListView.getChildAdapterPosition(childAt)) >= 0 && childAdapterPosition < this.listAdapter.items.size()) {
                        ((ReactedUserHolderView) childAt).animateAlpha(isStoryShownToUser(((Item) this.listAdapter.items.get(childAdapterPosition)).view) ? 1.0f : 0.5f, true);
                    }
                    i3++;
                }
                return;
            }
            return;
        }
        if (this.storyItem.uploadingStory == null || (stories = MessagesController.getInstance(this.currentAccount).storiesController.getStories(UserConfig.getInstance(this.currentAccount).clientUserId)) == null) {
            return;
        }
        while (i3 < stories.stories.size()) {
            TL_stories.StoryItem storyItem = stories.stories.get(i3);
            String str = storyItem.attachPath;
            if (str != null && str.equals(this.storyItem.uploadingStory.path)) {
                SelfStoryViewsView.StoryItemInternal storyItemInternal = this.storyItem;
                storyItemInternal.uploadingStory = null;
                storyItemInternal.storyItem = storyItem;
                setStoryItem(this.dialogId, storyItemInternal);
                return;
            }
            i3++;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        RecyclerListViewScroller recyclerListViewScroller;
        float translationY;
        View view = null;
        int i = -1;
        for (int i2 = 0; i2 < this.recyclerListView.getChildCount(); i2++) {
            View childAt = this.recyclerListView.getChildAt(i2);
            int childLayoutPosition = this.recyclerListView.getChildLayoutPosition(childAt);
            if (childLayoutPosition < i || i == -1) {
                view = childAt;
                i = childLayoutPosition;
            }
        }
        int max = i == 0 ? (int) Math.max(0.0f, view.getY()) : i > 0 ? 0 : this.recyclerListView.getPaddingTop();
        float f = max;
        if (this.topViewsContainer.getTranslationY() != f) {
            this.topViewsContainer.setTranslationY(f);
            onTopOffsetChanged(max);
        }
        this.shadowDrawable.setBounds(-AndroidUtilities.dp(6.0f), max, getMeasuredWidth() + AndroidUtilities.dp(6.0f), getMeasuredHeight());
        this.shadowDrawable.draw(canvas);
        if (this.checkAutoscroll) {
            this.checkAutoscroll = false;
            if (this.topViewsContainer.getTranslationY() != 0.0f && this.topViewsContainer.getTranslationY() != this.recyclerListView.getPaddingTop()) {
                if (this.topViewsContainer.getTranslationY() > this.recyclerListView.getPaddingTop() / 2.0f) {
                    recyclerListViewScroller = this.scroller;
                    translationY = -(this.recyclerListView.getPaddingTop() - this.topViewsContainer.getTranslationY());
                } else {
                    recyclerListViewScroller = this.scroller;
                    translationY = this.topViewsContainer.getTranslationY();
                }
                recyclerListViewScroller.smoothScrollBy((int) translationY);
            }
        }
        super.dispatchDraw(canvas);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        if (view != this.recyclerListView) {
            return super.drawChild(canvas, view, j);
        }
        canvas.save();
        canvas.clipRect(0, AndroidUtilities.dp(this.TOP_PADDING), getMeasuredWidth(), getMeasuredHeight());
        super.drawChild(canvas, view, j);
        canvas.restore();
        return true;
    }

    public float getTopOffset() {
        return this.topViewsContainer.getTranslationY();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.isAttachedToWindow = true;
        ViewsModel viewsModel = this.currentModel;
        if (viewsModel != null) {
            viewsModel.addListener(this);
            this.currentModel.animateDateForUsers.clear();
        }
        this.listAdapter.updateRows();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.storiesUpdated);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.storiesBlocklistUpdate);
        Bulletin.addDelegate(this, new Bulletin.Delegate() {
            @Override
            public boolean allowLayoutChanges() {
                return Bulletin.Delegate.CC.$default$allowLayoutChanges(this);
            }

            @Override
            public boolean bottomOffsetAnimated() {
                return Bulletin.Delegate.CC.$default$bottomOffsetAnimated(this);
            }

            @Override
            public boolean clipWithGradient(int i) {
                return Bulletin.Delegate.CC.$default$clipWithGradient(this, i);
            }

            @Override
            public int getBottomOffset(int i) {
                return SelfStoryViewsPage.this.recyclerListView.getPaddingBottom();
            }

            @Override
            public int getTopOffset(int i) {
                return Bulletin.Delegate.CC.$default$getTopOffset(this, i);
            }

            @Override
            public void onBottomOffsetChange(float f) {
                Bulletin.Delegate.CC.$default$onBottomOffsetChange(this, f);
            }

            @Override
            public void onHide(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onHide(this, bulletin);
            }

            @Override
            public void onShow(Bulletin bulletin) {
                Bulletin.Delegate.CC.$default$onShow(this, bulletin);
            }
        });
    }

    public boolean onBackPressed() {
        CustomPopupMenu customPopupMenu = this.popupMenu;
        if (customPopupMenu != null && customPopupMenu.isShowing()) {
            this.popupMenu.dismiss();
            return true;
        }
        if (Math.abs(this.topViewsContainer.getTranslationY() - this.recyclerListView.getPaddingTop()) <= AndroidUtilities.dp(2.0f)) {
            return false;
        }
        this.recyclerListView.dispatchTouchEvent(AndroidUtilities.emptyMotionEvent());
        this.recyclerListView.smoothScrollToPosition(0);
        return true;
    }

    public void onDataRecieved(ViewsModel viewsModel) {
        int itemCount = this.listAdapter.getItemCount();
        if (TextUtils.isEmpty(this.state.searchQuery) && !this.state.contactsOnly) {
            updateViewsVisibility();
        }
        this.listAdapter.updateRows();
        this.recyclerItemsEnterAnimator.showItemsAnimated(itemCount - 1);
        checkLoadMore();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isAttachedToWindow = false;
        ViewsModel viewsModel = this.currentModel;
        if (viewsModel != null) {
            viewsModel.removeListener(this);
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.storiesUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.storiesBlocklistUpdate);
        Bulletin.removeDelegate(this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getY() < this.topViewsContainer.getTranslationY()) {
            return false;
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    public void onKeyboardShown() {
        this.recyclerListView.dispatchTouchEvent(AndroidUtilities.emptyMotionEvent());
        if (this.topViewsContainer.getTranslationY() != 0.0f) {
            this.scroller.smoothScrollBy((int) this.topViewsContainer.getTranslationY(), 250L, AdjustPanLayoutHelper.keyboardInterpolator);
        }
    }

    public void onTopOffsetChanged(int i) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getY() < this.topViewsContainer.getTranslationY()) {
            return false;
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setListBottomPadding(float f) {
        if (f != this.recyclerListView.getPaddingBottom()) {
            this.recyclerListView.setPadding(0, (int) f, 0, 0);
            this.recyclerListView.requestLayout();
        }
    }

    public void setShadowDrawable(Drawable drawable) {
        this.shadowDrawable = drawable;
    }

    public void setStoryItem(long j, SelfStoryViewsView.StoryItemInternal storyItemInternal) {
        this.dialogId = j;
        this.storyItem = storyItemInternal;
        updateViewsVisibility();
        updateViewState(false);
        if (storyItemInternal == null || storyItemInternal.storyItem == null) {
            return;
        }
        NotificationsController.getInstance(this.currentAccount).processSeenStoryReactions(j, storyItemInternal.storyItem.id);
    }

    public void updateSharedState() {
    }
}
