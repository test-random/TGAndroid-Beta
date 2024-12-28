package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.StateListAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.LongSparseArray;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatImageHelper$$ExternalSyntheticApiModelOutline0;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.CompoundEmoji;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.EmptyCell;
import org.telegram.ui.Cells.FeaturedStickerSetInfoCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Cells.StickerSetGroupInfoCell;
import org.telegram.ui.Cells.StickerSetNameCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.ListView.RecyclerListViewWithOverlayDraw;
import org.telegram.ui.Components.PagerSlidingTabStrip;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTabStrip;
import org.telegram.ui.Components.StickerCategoriesListView;
import org.telegram.ui.Components.TrendingStickersLayout;
import org.telegram.ui.ContentPreviewViewer;
import org.telegram.ui.SelectAnimatedEmojiDialog;
import org.telegram.ui.StickersActivity;

public class EmojiView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private ArrayList allTabs;
    private boolean allowAnimatedEmoji;
    private boolean allowEmojisForNonPremium;
    private View animateExpandFromButton;
    private int animateExpandFromPosition;
    private long animateExpandStartTime;
    private int animateExpandToPosition;
    private LongSparseArray animatedEmojiDrawables;
    private PorterDuffColorFilter animatedEmojiTextColorFilter;
    private ImageView backspaceButton;
    private AnimatorSet backspaceButtonAnimation;
    private boolean backspaceOnce;
    private boolean backspacePressed;
    private float bottomTabAdditionalTranslation;
    private FrameLayout bottomTabContainer;
    private ValueAnimator bottomTabContainerAnimator;
    private View bottomTabContainerBackground;
    private float bottomTabMainTranslation;
    private FrameLayout bulletinContainer;
    private Runnable checkExpandStickerTabsRunnable;
    private ChooseStickerActionTracker chooseStickerActionTracker;
    private EmojiColorPickerWindow colorPickerView;
    private ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate;
    public int currentAccount;
    private int currentBackgroundType;
    private long currentChatId;
    private int currentPage;
    private ArrayList currentTabs;
    private EmojiViewDelegate delegate;
    private Paint dotPaint;
    private DragListener dragListener;
    private EmojiGridAdapter emojiAdapter;
    boolean emojiBanned;
    public int emojiCacheType;
    private FrameLayout emojiContainer;
    private EmojiGridView emojiGridView;
    private float emojiLastX;
    private float emojiLastY;
    private GridLayoutManager emojiLayoutManager;
    private Drawable emojiLockDrawable;
    private Paint emojiLockPaint;
    private boolean emojiPackAlertOpened;
    EmojiPagesAdapter emojiPagerAdapter;
    private RecyclerAnimationScrollHelper emojiScrollHelper;
    private EmojiSearchAdapter emojiSearchAdapter;
    private SearchField emojiSearchField;
    private int emojiSize;
    private boolean emojiSmoothScrolling;
    private AnimatorSet emojiTabShadowAnimator;
    private EmojiTabsStrip emojiTabs;
    private View emojiTabsShadow;
    private String[] emojiTitles;
    private ImageViewEmoji emojiTouchedView;
    private float emojiTouchedX;
    private float emojiTouchedY;
    private ArrayList emojipacksProcessed;
    private boolean expandStickersByDragg;
    private ArrayList expandedEmojiSets;
    private int favTabNum;
    private ArrayList favouriteStickers;
    private ArrayList featuredEmojiSets;
    private ArrayList featuredStickerSets;
    private boolean firstEmojiAttach;
    private boolean firstGifAttach;
    private boolean firstStickersAttach;
    private boolean firstTabUpdate;
    public boolean fixBottomTabContainerTranslation;
    private ImageView floatingButton;
    private boolean forseMultiwindowLayout;
    private BaseFragment fragment;
    private boolean frozen;
    ArrayList frozenStickerSets;
    private GifAdapter gifAdapter;
    private final Map gifCache;
    private FrameLayout gifContainer;
    private int gifFirstEmojiTabNum;
    private RecyclerListView gifGridView;
    private Drawable[] gifIcons;
    private GifLayoutManager gifLayoutManager;
    private RecyclerListView.OnItemClickListener gifOnItemClickListener;
    private int gifRecentTabNum;
    private GifAdapter gifSearchAdapter;
    private SearchField gifSearchField;
    private GifSearchPreloader gifSearchPreloader;
    private ScrollSlidingTabStrip gifTabs;
    private int gifTrendingTabNum;
    private int groupStickerPackNum;
    private int groupStickerPackPosition;
    private TLRPC.TL_messages_stickerSet groupStickerSet;
    private boolean groupStickersHidden;
    private boolean hasChatStickers;
    private int hasRecentEmoji;
    private Runnable hideStickersBan;
    private boolean ignorePagerScroll;
    private boolean ignoreStickersScroll;
    private TLRPC.ChatFull info;
    public ArrayList installedEmojiSets;
    private LongSparseArray installingStickerSets;
    private boolean isLayout;
    private ArrayList keepFeaturedDuplicate;
    private float lastBottomScrollDy;
    private int lastNotifyWidth;
    private ArrayList lastRecentArray;
    private int lastRecentCount;
    private String[] lastSearchKeyboardLanguage;
    private float lastStickersX;
    private int[] location;
    private TextView mediaBanTooltip;
    private boolean needEmojiSearch;
    private Object outlineProvider;
    private ViewPager pager;
    private boolean premiumBulletin;
    private ArrayList premiumStickers;
    private int premiumTabNum;
    private TLRPC.StickerSetCovered[] primaryInstallingStickerSets;
    private ArrayList recentGifs;
    private ArrayList recentStickers;
    private int recentTabNum;
    android.graphics.Rect rect;
    private LongSparseArray removingStickerSets;
    private final Theme.ResourcesProvider resourcesProvider;
    private AnimatorSet searchAnimation;
    private ImageView searchButton;
    private int searchFieldHeight;
    private Drawable searchIconDotDrawable;
    private Drawable searchIconDrawable;
    private View shadowLine;
    private boolean shouldDrawBackground;
    public boolean shouldLightenBackground;
    private boolean showGifs;
    private AnimatorSet showStickersBanAnimator;
    private boolean showing;
    private long shownBottomTabAfterClick;
    private Drawable[] stickerIcons;
    private ArrayList stickerSets;
    private ImageView stickerSettingsButton;
    boolean stickersBanned;
    private AnimatorSet stickersButtonAnimation;
    private FrameLayout stickersContainer;
    private boolean stickersContainerAttached;
    private StickersGridAdapter stickersGridAdapter;
    private RecyclerListView stickersGridView;
    private GridLayoutManager stickersLayoutManager;
    private RecyclerListView.OnItemClickListener stickersOnItemClickListener;
    private RecyclerAnimationScrollHelper stickersScrollHelper;
    private SearchField stickersSearchField;
    private StickersSearchGridAdapter stickersSearchGridAdapter;
    private ScrollSlidingTabStrip stickersTab;
    private FrameLayout stickersTabContainer;
    private int stickersTabOffset;
    private Drawable[] tabIcons;
    private final int[] tabsMinusDy;
    private ObjectAnimator[] tabsYAnimators;
    private HashMap toInstall;
    private TrendingAdapter trendingAdapter;
    private TrendingAdapter trendingEmojiAdapter;
    private int trendingTabNum;
    private PagerSlidingTabStrip typeTabs;
    private Runnable updateStickersLoadedDelayed;

    public class AnonymousClass1 implements Runnable {
        AnonymousClass1() {
        }

        @Override
        public void run() {
            if (EmojiView.this.stickersTab.isDragging()) {
                return;
            }
            EmojiView.this.expandStickersByDragg = false;
            EmojiView.this.updateStickerTabsPosition();
        }
    }

    public class AnonymousClass10 extends TypedScrollListener {
        AnonymousClass10(int i) {
            super(i);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (i == 0) {
                EmojiView.this.emojiSmoothScrolling = false;
            }
            super.onScrollStateChanged(recyclerView, i);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            EmojiView.this.updateEmojiTabsPosition();
            super.onScrolled(recyclerView, i, i2);
        }
    }

    public class AnonymousClass11 extends EmojiTabsStrip {
        AnonymousClass11(Context context, Theme.ResourcesProvider resourcesProvider, boolean z, boolean z2, boolean z3, int i, Runnable runnable) {
            super(context, resourcesProvider, z, z2, z3, i, runnable);
        }

        @Override
        protected boolean allowEmojisForNonPremium() {
            return EmojiView.this.allowEmojisForNonPremium;
        }

        @Override
        protected boolean doIncludeFeatured() {
            return EmojiView.this.featuredEmojiSets.size() <= 0 || ((TLRPC.StickerSetCovered) EmojiView.this.featuredEmojiSets.get(0)).set == null || MessagesController.getEmojiSettings(EmojiView.this.currentAccount).getLong("emoji_featured_hidden", 0L) == ((TLRPC.StickerSetCovered) EmojiView.this.featuredEmojiSets.get(0)).set.id || !UserConfig.getInstance(UserConfig.selectedAccount).isPremium();
        }

        @Override
        protected ColorFilter getEmojiColorFilter() {
            return EmojiView.this.animatedEmojiTextColorFilter;
        }

        @Override
        protected boolean isInstalled(EmojiPack emojiPack) {
            return emojiPack.installed || EmojiView.this.installedEmojiSets.contains(Long.valueOf(emojiPack.set.id));
        }

        @Override
        protected boolean onTabClick(int r10) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.AnonymousClass11.onTabClick(int):boolean");
        }

        @Override
        public void setTranslationY(float f) {
            if (getTranslationY() != f) {
                super.setTranslationY(f);
                if (EmojiView.this.emojiTabsShadow != null) {
                    EmojiView.this.emojiTabsShadow.setTranslationY(f);
                }
                EmojiView.this.emojiContainer.invalidate();
            }
        }
    }

    public class AnonymousClass12 extends SearchField {
        AnonymousClass12(Context context, int i) {
            super(context, i);
        }

        @Override
        public void setTranslationY(float f) {
            if (f != getTranslationY()) {
                super.setTranslationY(f);
                EmojiView.this.emojiContainer.invalidate();
            }
        }
    }

    public class AnonymousClass13 implements View.OnFocusChangeListener {
        AnonymousClass13() {
        }

        @Override
        public void onFocusChange(View view, boolean z) {
            if (z) {
                EmojiView.this.lastSearchKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(EmojiView.this.lastSearchKeyboardLanguage);
            }
        }
    }

    public class AnonymousClass14 extends FrameLayout {
        AnonymousClass14(Context context) {
            super(context);
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (view != EmojiView.this.gifGridView) {
                return super.drawChild(canvas, view, j);
            }
            canvas.save();
            canvas.clipRect(0.0f, EmojiView.this.gifSearchField.getY() + EmojiView.this.gifSearchField.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
            boolean drawChild = super.drawChild(canvas, view, j);
            canvas.restore();
            return drawChild;
        }
    }

    public class AnonymousClass15 extends RecyclerListView {
        private boolean ignoreLayout;
        private boolean wasMeasured;

        AnonymousClass15(Context context) {
            super(context);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, EmojiView.this.gifGridView, 0, EmojiView.this.contentPreviewViewerDelegate, this.resourcesProvider);
        }

        @Override
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            if (EmojiView.this.firstGifAttach && EmojiView.this.gifAdapter.getItemCount() > 1) {
                this.ignoreLayout = true;
                EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(0, 0);
                EmojiView.this.gifSearchField.setVisibility(0);
                EmojiView.this.gifTabs.onPageScrolled(0, 0);
                EmojiView.this.firstGifAttach = false;
                this.ignoreLayout = false;
            }
            super.onLayout(z, i, i2, i3, i4);
            EmojiView.this.checkGifSearchFieldScroll(true);
        }

        @Override
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            if (this.wasMeasured) {
                return;
            }
            EmojiView.this.gifAdapter.notifyDataSetChanged();
            this.wasMeasured = true;
        }

        @Override
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }
    }

    public class AnonymousClass16 extends RecyclerView.ItemDecoration {
        AnonymousClass16() {
        }

        @Override
        public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
            if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter && childAdapterPosition == EmojiView.this.gifAdapter.trendingSectionItem) {
                rect.set(0, 0, 0, 0);
                return;
            }
            if (childAdapterPosition == 0 && EmojiView.this.gifAdapter.addSearch) {
                rect.set(0, 0, 0, 0);
                return;
            }
            rect.left = 0;
            rect.bottom = 0;
            rect.top = AndroidUtilities.dp(2.0f);
            rect.right = EmojiView.this.gifLayoutManager.isLastInRow(childAdapterPosition - (EmojiView.this.gifAdapter.addSearch ? 1 : 0)) ? 0 : AndroidUtilities.dp(2.0f);
        }
    }

    public class AnonymousClass17 extends SearchField {
        AnonymousClass17(Context context, int i) {
            super(context, i);
        }

        @Override
        public void setTranslationY(float f) {
            if (getTranslationY() != f) {
                super.setTranslationY(f);
                EmojiView.this.gifContainer.invalidate();
            }
        }
    }

    public class AnonymousClass18 extends FrameLayout {
        final boolean val$shouldDrawBackground;

        AnonymousClass18(Context context, boolean z) {
            super(context);
            r3 = z;
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (r3 || !(view == EmojiView.this.stickersGridView || view == EmojiView.this.stickersSearchField)) {
                return super.drawChild(canvas, view, j);
            }
            canvas.save();
            float y = EmojiView.this.stickersTab.getY() + EmojiView.this.stickersTab.getMeasuredHeight() + 1.0f;
            if (view == EmojiView.this.stickersGridView) {
                y = Math.max(y, EmojiView.this.stickersSearchField.getY() + EmojiView.this.stickersSearchField.getMeasuredHeight() + 1.0f);
            }
            canvas.clipRect(0.0f, y, getMeasuredWidth(), getMeasuredHeight());
            boolean drawChild = super.drawChild(canvas, view, j);
            canvas.restore();
            return drawChild;
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            EmojiView.this.stickersContainerAttached = true;
            EmojiView.this.updateStickerTabsPosition();
            if (EmojiView.this.chooseStickerActionTracker != null) {
                EmojiView.this.chooseStickerActionTracker.checkVisibility();
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            EmojiView.this.stickersContainerAttached = false;
            EmojiView.this.updateStickerTabsPosition();
            if (EmojiView.this.chooseStickerActionTracker != null) {
                EmojiView.this.chooseStickerActionTracker.checkVisibility();
            }
        }
    }

    public class AnonymousClass19 extends RecyclerListViewWithOverlayDraw {
        boolean ignoreLayout;

        AnonymousClass19(Context context) {
            super(context);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate, this.resourcesProvider);
        }

        @Override
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            if (EmojiView.this.firstStickersAttach && EmojiView.this.stickersGridAdapter.getItemCount() > 0) {
                this.ignoreLayout = true;
                EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
                EmojiView.this.firstStickersAttach = false;
                this.ignoreLayout = false;
            }
            super.onLayout(z, i, i2, i3, i4);
            EmojiView.this.checkStickersSearchFieldScroll(true);
        }

        @Override
        public void onScrolled(int i, int i2) {
            super.onScrolled(i, i2);
            if (EmojiView.this.stickersTabContainer != null) {
                EmojiView.this.stickersTab.setUnderlineHeight(EmojiView.this.stickersGridView.canScrollVertically(-1) ? AndroidUtilities.getShadowHeight() : 0);
            }
        }

        @Override
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        @Override
        public void setVisibility(int i) {
            super.setVisibility(i);
        }
    }

    public class AnonymousClass2 implements ContentPreviewViewer.ContentPreviewViewerDelegate {
        AnonymousClass2() {
        }

        public void lambda$deleteSticker$1(TLObject tLObject) {
            if (tLObject instanceof TLRPC.TL_messages_stickerSet) {
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) tLObject;
                MediaDataController.getInstance(EmojiView.this.currentAccount).putStickerSet(tL_messages_stickerSet);
                MediaDataController.getInstance(EmojiView.this.currentAccount).replaceStickerSet(tL_messages_stickerSet);
            }
        }

        public void lambda$deleteSticker$2(final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    EmojiView.AnonymousClass2.this.lambda$deleteSticker$1(tLObject);
                }
            });
        }

        public void lambda$setAsEmojiStatus$0(TLRPC.EmojiStatus emojiStatus) {
            MessagesController.getInstance(EmojiView.this.currentAccount).updateEmojiStatus(emojiStatus);
        }

        @Override
        public void addToFavoriteSelected(String str) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$addToFavoriteSelected(this, str);
        }

        @Override
        public boolean can() {
            return (EmojiView.this.fragment == null && EmojiView.this.shouldDrawBackground) ? false : true;
        }

        @Override
        public boolean canDeleteSticker(TLRPC.Document document) {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$canDeleteSticker(this, document);
        }

        @Override
        public boolean canEditSticker() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$canEditSticker(this);
        }

        @Override
        public boolean canSchedule() {
            return EmojiView.this.delegate.canSchedule();
        }

        @Override
        public Boolean canSetAsStatus(TLRPC.Document document) {
            TLRPC.User currentUser;
            if (!UserConfig.getInstance(UserConfig.selectedAccount).isPremium() || (currentUser = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser()) == null) {
                return null;
            }
            Long emojiStatusDocumentId = UserObject.getEmojiStatusDocumentId(currentUser);
            return Boolean.valueOf(document != null && (emojiStatusDocumentId == null || emojiStatusDocumentId.longValue() != document.id));
        }

        @Override
        public void copyEmoji(TLRPC.Document document) {
            SpannableStringBuilder valueOf = SpannableStringBuilder.valueOf(MessageObject.findAnimatedEmojiEmoticon(document));
            valueOf.setSpan(new AnimatedEmojiSpan(document, (Paint.FontMetricsInt) null), 0, valueOf.length(), 33);
            if (AndroidUtilities.addToClipboard(valueOf)) {
                (EmojiView.this.fragment != null ? BulletinFactory.of(EmojiView.this.fragment) : BulletinFactory.of(EmojiView.this.bulletinContainer, EmojiView.this.resourcesProvider)).createCopyBulletin(LocaleController.getString(R.string.EmojiCopied)).show();
            }
        }

        @Override
        public void deleteSticker(TLRPC.Document document) {
            TLRPC.TL_stickers_removeStickerFromSet tL_stickers_removeStickerFromSet = new TLRPC.TL_stickers_removeStickerFromSet();
            tL_stickers_removeStickerFromSet.sticker = MediaDataController.getInputStickerSetItem(document, "").document;
            ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tL_stickers_removeStickerFromSet, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    EmojiView.AnonymousClass2.this.lambda$deleteSticker$2(tLObject, tL_error);
                }
            });
        }

        @Override
        public void editSticker(TLRPC.Document document) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$editSticker(this, document);
        }

        @Override
        public long getDialogId() {
            return EmojiView.this.delegate.getDialogId();
        }

        @Override
        public String getQuery(boolean z) {
            if (z) {
                if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter) {
                    return EmojiView.this.gifSearchAdapter.lastSearchImageString;
                }
                return null;
            }
            if (EmojiView.this.emojiGridView.getAdapter() == EmojiView.this.emojiSearchAdapter) {
                return EmojiView.this.emojiSearchAdapter.lastSearchEmojiString;
            }
            return null;
        }

        @Override
        public void gifAddedOrDeleted() {
            EmojiView.this.updateRecentGifs();
        }

        @Override
        public boolean isInScheduleMode() {
            return EmojiView.this.delegate.isInScheduleMode();
        }

        @Override
        public boolean isPhotoEditor() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$isPhotoEditor(this);
        }

        @Override
        public boolean isReplacedSticker() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$isReplacedSticker(this);
        }

        @Override
        public boolean isSettingIntroSticker() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$isSettingIntroSticker(this);
        }

        @Override
        public boolean isStickerEditor() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$isStickerEditor(this);
        }

        @Override
        public boolean needCopy(TLRPC.Document document) {
            return true;
        }

        @Override
        public boolean needMenu() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needMenu(this);
        }

        @Override
        public boolean needOpen() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needOpen(this);
        }

        @Override
        public boolean needRemove() {
            return ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$needRemove(this);
        }

        @Override
        public boolean needRemoveFromRecent(TLRPC.Document document) {
            if (document != null) {
                if (Emoji.recentEmoji.contains("animated_" + document.id)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean needSend(int i) {
            if (i != 2) {
                return true;
            }
            if ((EmojiView.this.fragment instanceof ChatActivity) && ((ChatActivity) EmojiView.this.fragment).canSendMessage()) {
                if (UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
                    return true;
                }
                if (((ChatActivity) EmojiView.this.fragment).getCurrentUser() != null && UserObject.isUserSelf(((ChatActivity) EmojiView.this.fragment).getCurrentUser())) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public void newStickerPackSelected(CharSequence charSequence, String str, Utilities.Callback callback) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$newStickerPackSelected(this, charSequence, str, callback);
        }

        @Override
        public void openSet(TLRPC.InputStickerSet inputStickerSet, boolean z) {
            if (inputStickerSet == null) {
                return;
            }
            EmojiView.this.delegate.onShowStickerSet(null, inputStickerSet, false);
        }

        @Override
        public void remove(SendMessagesHelper.ImportingSticker importingSticker) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$remove(this, importingSticker);
        }

        @Override
        public void removeFromRecent(TLRPC.Document document) {
            if (document != null) {
                Emoji.removeRecentEmoji("animated_" + document.id);
                if (EmojiView.this.emojiAdapter != null) {
                    EmojiView.this.emojiAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void resetTouch() {
            if (EmojiView.this.emojiGridView != null) {
                EmojiView.this.emojiGridView.clearAllTouches();
            }
        }

        @Override
        public void sendEmoji(TLRPC.Document document) {
            if (EmojiView.this.fragment instanceof ChatActivity) {
                ((ChatActivity) EmojiView.this.fragment).sendAnimatedEmoji(document, true, 0);
            }
        }

        @Override
        public void sendGif(Object obj, Object obj2, boolean z, int i) {
            if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter || EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter) {
                EmojiView.this.delegate.lambda$onGifSelected$1(null, obj, null, obj2, z, i);
            }
        }

        @Override
        public void sendSticker() {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$sendSticker(this);
        }

        @Override
        public void sendSticker(TLRPC.Document document, String str, Object obj, boolean z, int i) {
            EmojiView.this.delegate.onStickerSelected(null, document, str, obj, null, z, i);
        }

        @Override
        public void setAsEmojiStatus(TLRPC.Document document, Integer num) {
            TLRPC.EmojiStatus emojiStatus;
            Bulletin createEmojiBulletin;
            if (document == null) {
                emojiStatus = new TLRPC.TL_emojiStatusEmpty();
            } else if (num != null) {
                TLRPC.TL_emojiStatusUntil tL_emojiStatusUntil = new TLRPC.TL_emojiStatusUntil();
                tL_emojiStatusUntil.document_id = document.id;
                tL_emojiStatusUntil.until = num.intValue();
                emojiStatus = tL_emojiStatusUntil;
            } else {
                TLRPC.TL_emojiStatus tL_emojiStatus = new TLRPC.TL_emojiStatus();
                tL_emojiStatus.document_id = document.id;
                emojiStatus = tL_emojiStatus;
            }
            TLRPC.User currentUser = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
            final TLRPC.EmojiStatus tL_emojiStatusEmpty = currentUser == null ? new TLRPC.TL_emojiStatusEmpty() : currentUser.emoji_status;
            MessagesController.getInstance(EmojiView.this.currentAccount).updateEmojiStatus(emojiStatus);
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    EmojiView.AnonymousClass2.this.lambda$setAsEmojiStatus$0(tL_emojiStatusEmpty);
                }
            };
            if (document == null) {
                Bulletin.SimpleLayout simpleLayout = new Bulletin.SimpleLayout(EmojiView.this.getContext(), EmojiView.this.resourcesProvider);
                simpleLayout.textView.setText(LocaleController.getString(R.string.RemoveStatusInfo));
                simpleLayout.imageView.setImageResource(R.drawable.msg_settings_premium);
                simpleLayout.imageView.setScaleX(0.8f);
                simpleLayout.imageView.setScaleY(0.8f);
                simpleLayout.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_verifiedBackground, EmojiView.this.resourcesProvider), PorterDuff.Mode.MULTIPLY));
                Bulletin.UndoButton undoButton = new Bulletin.UndoButton(EmojiView.this.getContext(), true, EmojiView.this.resourcesProvider);
                undoButton.setUndoAction(runnable);
                simpleLayout.setButton(undoButton);
                createEmojiBulletin = EmojiView.this.fragment != null ? Bulletin.make(EmojiView.this.fragment, simpleLayout, 1500) : Bulletin.make(EmojiView.this.bulletinContainer, simpleLayout, 1500);
            } else {
                createEmojiBulletin = (EmojiView.this.fragment != null ? BulletinFactory.of(EmojiView.this.fragment) : BulletinFactory.of(EmojiView.this.bulletinContainer, EmojiView.this.resourcesProvider)).createEmojiBulletin(document, LocaleController.getString(R.string.SetAsEmojiStatusInfo), LocaleController.getString(R.string.Undo), runnable);
            }
            createEmojiBulletin.show();
        }

        @Override
        public void setIntroSticker(String str) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$setIntroSticker(this, str);
        }

        @Override
        public void stickerSetSelected(TLRPC.StickerSet stickerSet, String str) {
            ContentPreviewViewer.ContentPreviewViewerDelegate.CC.$default$stickerSetSelected(this, stickerSet, str);
        }
    }

    public class AnonymousClass20 extends GridLayoutManager {
        AnonymousClass20(Context context, int i) {
            super(context, i);
        }

        @Override
        public int scrollVerticallyBy(int i, RecyclerView.Recycler recycler, RecyclerView.State state) {
            int scrollVerticallyBy = super.scrollVerticallyBy(i, recycler, state);
            if (scrollVerticallyBy != 0 && EmojiView.this.stickersGridView.getScrollState() == 1) {
                EmojiView.this.expandStickersByDragg = false;
                EmojiView.this.updateStickerTabsPosition();
            }
            if (EmojiView.this.chooseStickerActionTracker == null) {
                EmojiView.this.createStickersChooseActionTracker();
            }
            EmojiView.this.chooseStickerActionTracker.doSomeAction();
            return scrollVerticallyBy;
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
            try {
                LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(recyclerView.getContext(), 2);
                linearSmoothScrollerCustom.setTargetPosition(i);
                startSmoothScroll(linearSmoothScrollerCustom);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public class AnonymousClass21 extends GridLayoutManager.SpanSizeLookup {
        AnonymousClass21() {
        }

        @Override
        public int getSpanSize(int i) {
            if (EmojiView.this.stickersGridView.getAdapter() == EmojiView.this.stickersGridAdapter) {
                if (i != 0 && i != EmojiView.this.stickersGridAdapter.totalItems && (EmojiView.this.stickersGridAdapter.cache.get(i) == null || (EmojiView.this.stickersGridAdapter.cache.get(i) instanceof TLRPC.Document))) {
                    return 1;
                }
            } else if (i != EmojiView.this.stickersSearchGridAdapter.totalItems && (EmojiView.this.stickersSearchGridAdapter.cache.get(i) == null || (EmojiView.this.stickersSearchGridAdapter.cache.get(i) instanceof TLRPC.Document))) {
                return 1;
            }
            return EmojiView.this.stickersGridAdapter.stickersPerRow;
        }
    }

    public class AnonymousClass22 extends SearchField {
        AnonymousClass22(Context context, int i) {
            super(context, i);
        }

        @Override
        public void setTranslationY(float f) {
            if (f != getTranslationY()) {
                super.setTranslationY(f);
                EmojiView.this.stickersContainer.invalidate();
            }
        }
    }

    public class AnonymousClass23 extends DraggableScrollSlidingTabStrip {
        final BaseFragment val$fragment;
        final boolean val$shouldDrawBackground;

        AnonymousClass23(Context context, Theme.ResourcesProvider resourcesProvider, BaseFragment baseFragment, boolean z) {
            super(context, resourcesProvider);
            this.val$fragment = baseFragment;
            this.val$shouldDrawBackground = z;
        }

        public static void lambda$sendReorder$2(TLObject tLObject, TLRPC.TL_error tL_error) {
        }

        public int lambda$stickerSetPositionChanged$0(TLRPC.TL_messages_stickerSet tL_messages_stickerSet, TLRPC.TL_messages_stickerSet tL_messages_stickerSet2) {
            int indexOf = EmojiView.this.stickerSets.indexOf(tL_messages_stickerSet);
            int indexOf2 = EmojiView.this.stickerSets.indexOf(tL_messages_stickerSet2);
            if (indexOf < 0 || indexOf2 < 0) {
                return 0;
            }
            return indexOf - indexOf2;
        }

        public static void lambda$stickerSetPositionChanged$1(BaseFragment baseFragment) {
            baseFragment.presentFragment(new StickersActivity(0, null));
        }

        private void sendReorder() {
            MediaDataController.getInstance(EmojiView.this.currentAccount).calcNewHash(0);
            TLRPC.TL_messages_reorderStickerSets tL_messages_reorderStickerSets = new TLRPC.TL_messages_reorderStickerSets();
            tL_messages_reorderStickerSets.masks = false;
            tL_messages_reorderStickerSets.emojis = false;
            for (?? r3 = EmojiView.this.hasChatStickers; r3 < EmojiView.this.stickerSets.size(); r3++) {
                tL_messages_reorderStickerSets.order.add(Long.valueOf(((TLRPC.TL_messages_stickerSet) EmojiView.this.stickerSets.get(r3)).set.id));
            }
            ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tL_messages_reorderStickerSets, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    EmojiView.AnonymousClass23.lambda$sendReorder$2(tLObject, tL_error);
                }
            });
            NotificationCenter.getInstance(EmojiView.this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.stickersDidLoad, 0, Boolean.TRUE);
        }

        private void swapListElements(List list, int i, int i2) {
            list.add(i2, (TLRPC.TL_messages_stickerSet) list.remove(i));
        }

        @Override
        protected void invalidateOverlays() {
            if (EmojiView.this.stickersTabContainer != null) {
                EmojiView.this.stickersTabContainer.invalidate();
            }
        }

        @Override
        public void setTranslationY(float f) {
            if (getTranslationY() != f) {
                super.setTranslationY(f);
                if (this.val$shouldDrawBackground) {
                    return;
                }
                EmojiView.this.stickersContainer.invalidate();
            }
        }

        @Override
        protected void stickerSetPositionChanged(int i, int i2) {
            Bulletin createSimpleBulletin;
            int i3 = i - EmojiView.this.stickersTabOffset;
            int i4 = i2 - EmojiView.this.stickersTabOffset;
            MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
            swapListElements(EmojiView.this.stickerSets, i3, i4);
            Collections.sort(mediaDataController.getStickerSets(0), new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$stickerSetPositionChanged$0;
                    lambda$stickerSetPositionChanged$0 = EmojiView.AnonymousClass23.this.lambda$stickerSetPositionChanged$0((TLRPC.TL_messages_stickerSet) obj, (TLRPC.TL_messages_stickerSet) obj2);
                    return lambda$stickerSetPositionChanged$0;
                }
            });
            ArrayList arrayList = EmojiView.this.frozenStickerSets;
            if (arrayList != null) {
                arrayList.clear();
                EmojiView emojiView = EmojiView.this;
                emojiView.frozenStickerSets.addAll(emojiView.stickerSets);
            }
            EmojiView.this.reloadStickersAdapter();
            AndroidUtilities.cancelRunOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable);
            AndroidUtilities.runOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable, 1500L);
            sendReorder();
            EmojiView.this.updateStickerTabs(true);
            if (SharedConfig.updateStickersOrderOnSend) {
                SharedConfig.toggleUpdateStickersOrderOnSend();
                BaseFragment baseFragment = this.val$fragment;
                if (baseFragment != null) {
                    BulletinFactory of = BulletinFactory.of(baseFragment);
                    int i5 = R.raw.filter_reorder;
                    String string = LocaleController.getString(R.string.DynamicPackOrderOff);
                    String string2 = LocaleController.getString(R.string.DynamicPackOrderOffInfo);
                    String string3 = LocaleController.getString("Settings");
                    final BaseFragment baseFragment2 = this.val$fragment;
                    createSimpleBulletin = of.createSimpleBulletin(i5, string, string2, string3, new Runnable() {
                        @Override
                        public final void run() {
                            EmojiView.AnonymousClass23.lambda$stickerSetPositionChanged$1(BaseFragment.this);
                        }
                    });
                } else if (EmojiView.this.bulletinContainer == null) {
                    return;
                } else {
                    createSimpleBulletin = BulletinFactory.of(EmojiView.this.bulletinContainer, EmojiView.this.resourcesProvider).createSimpleBulletin(R.raw.filter_reorder, LocaleController.getString(R.string.DynamicPackOrderOff), LocaleController.getString(R.string.DynamicPackOrderOffInfo));
                }
                createSimpleBulletin.show();
            }
        }

        @Override
        protected void updatePosition() {
            EmojiView.this.updateStickerTabsPosition();
            if (EmojiView.this.stickersTabContainer != null) {
                EmojiView.this.stickersTabContainer.invalidate();
            }
            invalidate();
            if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.invalidateEnterView();
            }
        }
    }

    public class AnonymousClass24 extends FrameLayout {
        Paint paint = new Paint();

        AnonymousClass24(Context context) {
            super(context);
            this.paint = new Paint();
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            float dp = AndroidUtilities.dp(50.0f) * EmojiView.this.delegate.getProgressToSearchOpened();
            if (dp > getMeasuredHeight()) {
                return;
            }
            canvas.save();
            if (dp != 0.0f) {
                canvas.clipRect(0.0f, dp, getMeasuredWidth(), getMeasuredHeight());
            }
            this.paint.setColor(EmojiView.this.getThemedColor(Theme.key_chat_emojiPanelBackground));
            canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(36.0f) + EmojiView.this.stickersTab.getExpandedOffset(), this.paint);
            super.dispatchDraw(canvas);
            EmojiView.this.stickersTab.drawOverlays(canvas);
            canvas.restore();
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            EmojiView.this.updateStickerTabsPosition();
        }
    }

    public class AnonymousClass25 extends ViewPager {
        AnonymousClass25(Context context) {
            super(context);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (EmojiView.this.ignorePagerScroll) {
                return false;
            }
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
            }
            try {
                return super.onInterceptTouchEvent(motionEvent);
            } catch (IllegalArgumentException unused) {
                return false;
            }
        }

        @Override
        public void setCurrentItem(int i, boolean z) {
            EmojiView.this.startStopVisibleGifs(i == 1);
            if (i != getCurrentItem()) {
                super.setCurrentItem(i, z);
                return;
            }
            if (i != 0) {
                if (i == 1) {
                    EmojiView.this.gifGridView.smoothScrollToPosition(0);
                    return;
                } else {
                    EmojiView.this.stickersGridView.smoothScrollToPosition(1);
                    return;
                }
            }
            EmojiView.this.tabsMinusDy[1] = 0;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(EmojiView.this.emojiTabs, (Property<EmojiTabsStrip, Float>) ViewGroup.TRANSLATION_Y, 0.0f);
            ofFloat.setDuration(150L);
            ofFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            ofFloat.start();
            EmojiView.this.scrollEmojisToPosition(1, 0);
            if (EmojiView.this.emojiTabs != null) {
                EmojiView.this.emojiTabs.select(0);
            }
        }
    }

    public class AnonymousClass26 extends ImageView {
        AnonymousClass26(Context context) {
            super(context);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                EmojiView.this.backspacePressed = true;
                EmojiView.this.backspaceOnce = false;
                EmojiView.this.postBackspaceRunnable(350);
            } else if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                EmojiView.this.backspacePressed = false;
                if (!EmojiView.this.backspaceOnce && EmojiView.this.delegate != null && EmojiView.this.delegate.onBackspace()) {
                    try {
                        EmojiView.this.backspaceButton.performHapticFeedback(3);
                    } catch (Exception unused) {
                    }
                }
            }
            super.onTouchEvent(motionEvent);
            return true;
        }
    }

    public class AnonymousClass27 implements View.OnClickListener {
        AnonymousClass27() {
        }

        @Override
        public void onClick(View view) {
        }
    }

    public class AnonymousClass28 extends FrameLayout {
        AnonymousClass28(Context context) {
            super(context);
        }

        @Override
        public void setTranslationY(float f) {
            if (getTranslationY() != f) {
                super.setTranslationY(f);
                EmojiView.this.invalidate();
            }
        }
    }

    public class AnonymousClass29 implements View.OnClickListener {
        AnonymousClass29() {
        }

        @Override
        public void onClick(View view) {
            if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onStickersSettingsClick();
            }
        }
    }

    public class AnonymousClass3 extends ChatActivity {
        AnonymousClass3(Bundle bundle) {
            super(bundle);
        }

        public void lambda$onTransitionAnimationEnd$0() {
            if (this.chatActivityEnterView.getEmojiView() != null) {
                this.chatActivityEnterView.getEmojiView().scrollEmojisToAnimated();
            }
        }

        @Override
        public void onTransitionAnimationEnd(boolean z, boolean z2) {
            ChatActivityEnterView chatActivityEnterView;
            super.onTransitionAnimationEnd(z, z2);
            if (!z || (chatActivityEnterView = this.chatActivityEnterView) == null) {
                return;
            }
            chatActivityEnterView.showEmojiView();
            this.chatActivityEnterView.postDelayed(new Runnable() {
                @Override
                public final void run() {
                    EmojiView.AnonymousClass3.this.lambda$onTransitionAnimationEnd$0();
                }
            }, 100L);
        }
    }

    public class AnonymousClass30 implements ViewPager.OnPageChangeListener {
        final boolean val$shouldDrawBackground;

        AnonymousClass30(boolean z) {
            r2 = z;
        }

        @Override
        public void onPageScrollStateChanged(int i) {
        }

        @Override
        public void onPageScrolled(int i, float f, int i2) {
            EmojiView.this.checkGridVisibility(i, f);
            EmojiView emojiView = EmojiView.this;
            emojiView.onPageScrolled(i, (emojiView.getMeasuredWidth() - EmojiView.this.getPaddingLeft()) - EmojiView.this.getPaddingRight(), i2);
            boolean z = true;
            EmojiView.this.showBottomTab(true, true);
            int currentItem = EmojiView.this.pager.getCurrentItem();
            SearchField searchField = currentItem == 0 ? EmojiView.this.emojiSearchField : currentItem == 1 ? EmojiView.this.gifSearchField : EmojiView.this.stickersSearchField;
            String obj = searchField.searchEditText.getText().toString();
            int i3 = 0;
            while (i3 < 3) {
                EmojiView emojiView2 = EmojiView.this;
                SearchField searchField2 = i3 == 0 ? emojiView2.emojiSearchField : i3 == 1 ? emojiView2.gifSearchField : emojiView2.stickersSearchField;
                if (searchField2 != null && searchField2 != searchField && searchField2.searchEditText != null && !searchField2.searchEditText.getText().toString().equals(obj)) {
                    searchField2.searchEditText.setText(obj);
                    searchField2.searchEditText.setSelection(obj.length());
                }
                i3++;
            }
            EmojiView emojiView3 = EmojiView.this;
            if ((i != 0 || f <= 0.0f) && i != 1) {
                z = false;
            }
            emojiView3.startStopVisibleGifs(z);
            EmojiView.this.updateStickerTabsPosition();
        }

        @Override
        public void onPageSelected(int i) {
            SearchField searchField;
            EmojiView.this.saveNewPage();
            boolean z = false;
            EmojiView.this.showBackspaceButton(i == 0, true);
            EmojiView emojiView = EmojiView.this;
            if (i == 2 && r2) {
                z = true;
            }
            emojiView.showStickerSettingsButton(z, true);
            if (EmojiView.this.delegate.isSearchOpened()) {
                if (i == 0) {
                    if (EmojiView.this.emojiSearchField == null) {
                        return;
                    } else {
                        searchField = EmojiView.this.emojiSearchField;
                    }
                } else if (i == 1) {
                    if (EmojiView.this.gifSearchField == null) {
                        return;
                    } else {
                        searchField = EmojiView.this.gifSearchField;
                    }
                } else if (EmojiView.this.stickersSearchField == null) {
                    return;
                } else {
                    searchField = EmojiView.this.stickersSearchField;
                }
                searchField.searchEditText.requestFocus();
            }
        }
    }

    public class AnonymousClass31 implements View.OnClickListener {
        AnonymousClass31() {
        }

        @Override
        public void onClick(View view) {
            int currentItem = EmojiView.this.pager.getCurrentItem();
            SearchField searchField = currentItem == 0 ? EmojiView.this.emojiSearchField : currentItem == 1 ? EmojiView.this.gifSearchField : EmojiView.this.stickersSearchField;
            if (searchField == null) {
                return;
            }
            searchField.searchEditText.requestFocus();
            MotionEvent obtain = MotionEvent.obtain(0L, 0L, 0, 0.0f, 0.0f, 0);
            searchField.searchEditText.onTouchEvent(obtain);
            obtain.recycle();
            MotionEvent obtain2 = MotionEvent.obtain(0L, 0L, 1, 0.0f, 0.0f, 0);
            searchField.searchEditText.onTouchEvent(obtain2);
            obtain2.recycle();
        }
    }

    public class AnonymousClass32 extends ViewOutlineProvider {
        AnonymousClass32() {
        }

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
    }

    public class AnonymousClass33 extends ChooseStickerActionTracker {
        AnonymousClass33(int i, long j, long j2) {
            super(i, j, j2);
        }

        @Override
        public boolean isShown() {
            return EmojiView.this.delegate != null && EmojiView.this.getVisibility() == 0 && EmojiView.this.stickersContainerAttached;
        }
    }

    public class AnonymousClass34 extends EmojiPacksAlert {
        final TLRPC.StickerSet val$set;

        AnonymousClass34(BaseFragment baseFragment, Context context, Theme.ResourcesProvider resourcesProvider, ArrayList arrayList, TLRPC.StickerSet stickerSet) {
            super(baseFragment, context, resourcesProvider, arrayList);
            r6 = stickerSet;
        }

        @Override
        public void dismiss() {
            EmojiView.this.emojiPackAlertOpened = false;
            super.dismiss();
        }

        @Override
        protected void onButtonClicked(boolean z) {
            if (!z) {
                EmojiView.this.installedEmojiSets.remove(Long.valueOf(r6.id));
            } else if (!EmojiView.this.installedEmojiSets.contains(Long.valueOf(r6.id))) {
                EmojiView.this.installedEmojiSets.add(Long.valueOf(r6.id));
            }
            EmojiView.this.updateEmojiHeaders();
        }
    }

    public class AnonymousClass35 extends TrendingStickersLayout.Delegate {
        AnonymousClass35() {
        }

        @Override
        public boolean canSchedule() {
            return EmojiView.this.delegate.canSchedule();
        }

        @Override
        public boolean canSendSticker() {
            return true;
        }

        @Override
        public String[] getLastSearchKeyboardLanguage() {
            return EmojiView.this.lastSearchKeyboardLanguage;
        }

        @Override
        public boolean isInScheduleMode() {
            return EmojiView.this.delegate.isInScheduleMode();
        }

        @Override
        public boolean onListViewInterceptTouchEvent(RecyclerListView recyclerListView, MotionEvent motionEvent) {
            return ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, recyclerListView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate, EmojiView.this.resourcesProvider);
        }

        @Override
        public boolean onListViewTouchEvent(RecyclerListView recyclerListView, RecyclerListView.OnItemClickListener onItemClickListener, MotionEvent motionEvent) {
            return ContentPreviewViewer.getInstance().onTouch(motionEvent, recyclerListView, EmojiView.this.getMeasuredHeight(), onItemClickListener, EmojiView.this.contentPreviewViewerDelegate, EmojiView.this.resourcesProvider);
        }

        @Override
        public void onStickerSelected(TLRPC.Document document, Object obj, boolean z, boolean z2, int i) {
            EmojiView.this.delegate.onStickerSelected(null, document, null, obj, null, z2, i);
        }

        @Override
        public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered, boolean z) {
            EmojiView.this.delegate.onStickerSetAdd(stickerSetCovered);
            if (z) {
                EmojiView.this.updateStickerTabs(true);
            }
        }

        @Override
        public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered) {
            EmojiView.this.delegate.onStickerSetRemove(stickerSetCovered);
        }

        @Override
        public void setLastSearchKeyboardLanguage(String[] strArr) {
            EmojiView.this.lastSearchKeyboardLanguage = strArr;
        }
    }

    public class AnonymousClass36 extends AnimatorListenerAdapter {
        final RecyclerListView val$gridView;

        AnonymousClass36(RecyclerListView recyclerListView) {
            r2 = recyclerListView;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            if (animator.equals(EmojiView.this.searchAnimation)) {
                EmojiView.this.searchAnimation = null;
            }
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (animator.equals(EmojiView.this.searchAnimation)) {
                r2.setTranslationY(0.0f);
                if (r2 == EmojiView.this.stickersGridView) {
                    r2.setPadding(0, 0, 0, 0);
                } else if (r2 == EmojiView.this.emojiGridView) {
                    r2.setPadding(AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(5.0f), 0);
                } else if (r2 == EmojiView.this.gifGridView) {
                    r2.setPadding(0, EmojiView.this.searchFieldHeight, 0, 0);
                }
                EmojiView.this.searchAnimation = null;
            }
        }
    }

    public class AnonymousClass37 extends AnimatorListenerAdapter {
        AnonymousClass37() {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            EmojiView.this.emojiTabShadowAnimator = null;
        }
    }

    public class AnonymousClass38 extends LinearSmoothScrollerCustom {
        AnonymousClass38(Context context, int i) {
            super(context, i);
        }

        @Override
        public void onEnd() {
            EmojiView.this.emojiSmoothScrolling = false;
        }

        @Override
        protected void onStart() {
            EmojiView.this.emojiSmoothScrolling = true;
        }
    }

    public class AnonymousClass39 extends AnimatorListenerAdapter {
        final RecyclerListView val$gridView;
        final GridLayoutManager val$layoutManager;

        AnonymousClass39(GridLayoutManager gridLayoutManager, RecyclerListView recyclerListView) {
            r2 = gridLayoutManager;
            r3 = recyclerListView;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            if (animator.equals(EmojiView.this.searchAnimation)) {
                EmojiView.this.searchAnimation = null;
            }
        }

        @Override
        public void onAnimationEnd(android.animation.Animator r7) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.AnonymousClass39.onAnimationEnd(android.animation.Animator):void");
        }
    }

    public class AnonymousClass4 extends ViewOutlineProvider {
        AnonymousClass4() {
        }

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setRoundRect(view.getPaddingLeft(), view.getPaddingTop(), view.getMeasuredWidth() - view.getPaddingRight(), view.getMeasuredHeight() - view.getPaddingBottom(), AndroidUtilities.dp(6.0f));
        }
    }

    public class AnonymousClass40 extends AnimatorListenerAdapter {
        final boolean val$show;

        AnonymousClass40(boolean z) {
            r2 = z;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (r2) {
                return;
            }
            EmojiView.this.backspaceButton.setVisibility(4);
        }
    }

    public class AnonymousClass41 extends AnimatorListenerAdapter {
        final boolean val$show;

        AnonymousClass41(boolean z) {
            r2 = z;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (r2) {
                return;
            }
            EmojiView.this.stickerSettingsButton.setVisibility(4);
        }
    }

    public class AnonymousClass42 extends AnimatorListenerAdapter {
        AnonymousClass42() {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (EmojiView.this.bottomTabContainerAnimator != animator) {
                return;
            }
            EmojiView emojiView = EmojiView.this;
            emojiView.bottomTabAdditionalTranslation = ((Float) emojiView.bottomTabContainerAnimator.getAnimatedValue()).floatValue();
            EmojiView.this.updateBottomTabContainerPosition();
            EmojiView.this.bottomTabContainerAnimator = null;
        }
    }

    public class AnonymousClass43 extends LinearSmoothScroller {
        final int val$tabsMinusDy;

        AnonymousClass43(Context context, int i) {
            super(context);
            r3 = i;
        }

        @Override
        public int calculateDtToFit(int i, int i2, int i3, int i4, int i5) {
            return super.calculateDtToFit(i, i2, i3, i4, i5) + r3;
        }

        @Override
        public int calculateTimeForDeceleration(int i) {
            return super.calculateTimeForDeceleration(i) * 16;
        }

        @Override
        public int getVerticalSnapPreference() {
            return -1;
        }
    }

    public class AnonymousClass5 extends FrameLayout {
        AnonymousClass5(Context context) {
            super(context);
        }

        @Override
        protected boolean drawChild(Canvas canvas, View view, long j) {
            if (view != EmojiView.this.emojiGridView && view != EmojiView.this.emojiSearchField) {
                return super.drawChild(canvas, view, j);
            }
            canvas.save();
            float y = EmojiView.this.emojiTabs.getY() + EmojiView.this.emojiTabs.getMeasuredHeight() + 1.0f;
            if (view == EmojiView.this.emojiGridView && EmojiView.this.emojiSearchField != null) {
                y = Math.max(y, EmojiView.this.emojiSearchField.getY() + EmojiView.this.emojiSearchField.getMeasuredHeight() + 1.0f);
            }
            canvas.clipRect(0.0f, y, getMeasuredWidth(), getMeasuredHeight());
            boolean drawChild = super.drawChild(canvas, view, j);
            canvas.restore();
            return drawChild;
        }
    }

    public class AnonymousClass6 implements RecyclerListView.OnItemLongClickListener {
        AnonymousClass6() {
        }

        @Override
        public boolean onItemClick(View view, int i) {
            String str;
            int i2;
            if (view instanceof ImageViewEmoji) {
                ImageViewEmoji imageViewEmoji = (ImageViewEmoji) view;
                if (imageViewEmoji.isRecent) {
                    RecyclerView.ViewHolder findContainingViewHolder = EmojiView.this.emojiGridView.findContainingViewHolder(view);
                    if (findContainingViewHolder != null && findContainingViewHolder.getAdapterPosition() <= EmojiView.this.getRecentEmoji().size()) {
                        EmojiView.this.delegate.onClearEmojiRecent();
                    }
                } else {
                    if (imageViewEmoji.getSpan() != null || (str = (String) imageViewEmoji.getTag()) == null) {
                        return false;
                    }
                    String replace = str.replace("🏻", "").replace("🏼", "").replace("🏽", "").replace("🏾", "").replace("🏿", "");
                    String str2 = !imageViewEmoji.isRecent ? Emoji.emojiColor.get(replace) : null;
                    boolean isCompound = CompoundEmoji.isCompound(replace);
                    if (isCompound || EmojiData.emojiColoredMap.contains(replace)) {
                        EmojiView.this.emojiTouchedView = imageViewEmoji;
                        EmojiView emojiView = EmojiView.this;
                        emojiView.emojiTouchedX = emojiView.emojiLastX;
                        EmojiView emojiView2 = EmojiView.this;
                        emojiView2.emojiTouchedY = emojiView2.emojiLastY;
                        if (isCompound) {
                            replace = EmojiView.addColorToCode(replace, str2);
                        } else {
                            EmojiView.this.colorPickerView.setSelection(CompoundEmoji.skinTones.indexOf(str2) + 1);
                        }
                        EmojiView.this.colorPickerView.setEmoji(replace);
                        int popupWidth = EmojiView.this.colorPickerView.getPopupWidth();
                        int popupHeight = EmojiView.this.colorPickerView.getPopupHeight();
                        imageViewEmoji.getLocationOnScreen(EmojiView.this.location);
                        if (EmojiView.this.colorPickerView.isCompound()) {
                            i2 = 0;
                        } else {
                            i2 = (EmojiView.this.emojiSize * EmojiView.this.colorPickerView.getSelection()) + AndroidUtilities.dp((EmojiView.this.colorPickerView.getSelection() * 4) - (AndroidUtilities.isTablet() ? 5 : 1));
                        }
                        if (EmojiView.this.location[0] - i2 < AndroidUtilities.dp(5.0f)) {
                            i2 += (EmojiView.this.location[0] - i2) - AndroidUtilities.dp(5.0f);
                        } else if ((EmojiView.this.location[0] - i2) + popupWidth > AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0f)) {
                            i2 += ((EmojiView.this.location[0] - i2) + popupWidth) - (AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0f));
                        }
                        int i3 = -i2;
                        int top = imageViewEmoji.getTop() < 0 ? imageViewEmoji.getTop() : 0;
                        EmojiView.this.colorPickerView.setupArrow((AndroidUtilities.dp(AndroidUtilities.isTablet() ? 30.0f : 22.0f) - i3) + ((int) AndroidUtilities.dpf2(0.5f)));
                        EmojiView.this.colorPickerView.setFocusable(true);
                        EmojiView.this.colorPickerView.showAsDropDown(view, i3, (((-view.getMeasuredHeight()) - popupHeight) + ((view.getMeasuredHeight() - EmojiView.this.emojiSize) / 2)) - top);
                        EmojiView.this.pager.requestDisallowInterceptTouchEvent(true);
                        EmojiView.this.emojiGridView.hideSelector(true);
                    }
                }
                EmojiView.this.emojiGridView.clearTouchesFor(view);
                return true;
            }
            return false;
        }
    }

    public class AnonymousClass7 extends GridLayoutManager {

        class AnonymousClass1 extends LinearSmoothScrollerCustom {
            AnonymousClass1(Context context, int i) {
                super(context, i);
            }

            @Override
            public void onEnd() {
                EmojiView.this.emojiSmoothScrolling = false;
            }
        }

        AnonymousClass7(Context context, int i) {
            super(context, i);
        }

        @Override
        public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i) {
            try {
                AnonymousClass1 anonymousClass1 = new LinearSmoothScrollerCustom(recyclerView.getContext(), 2) {
                    AnonymousClass1(Context context, int i2) {
                        super(context, i2);
                    }

                    @Override
                    public void onEnd() {
                        EmojiView.this.emojiSmoothScrolling = false;
                    }
                };
                anonymousClass1.setTargetPosition(i);
                startSmoothScroll(anonymousClass1);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public class AnonymousClass8 extends GridLayoutManager.SpanSizeLookup {
        AnonymousClass8() {
        }

        @Override
        public int getSpanGroupIndex(int i, int i2) {
            return super.getSpanGroupIndex(i, i2);
        }

        @Override
        public int getSpanSize(int i) {
            int itemViewType;
            if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter ? !((EmojiView.this.needEmojiSearch && i == 0) || i == EmojiView.this.emojiAdapter.trendingRow || i == EmojiView.this.emojiAdapter.trendingHeaderRow || i == EmojiView.this.emojiAdapter.recentlyUsedHeaderRow || EmojiView.this.emojiAdapter.positionToSection.indexOfKey(i) >= 0 || EmojiView.this.emojiAdapter.positionToUnlock.indexOfKey(i) >= 0) : !((itemViewType = EmojiView.this.emojiSearchAdapter.getItemViewType(i)) == 1 || itemViewType == 3 || itemViewType == 2)) {
                return 1;
            }
            return EmojiView.this.emojiLayoutManager.getSpanCount();
        }
    }

    public class AnonymousClass9 extends RecyclerAnimationScrollHelper.AnimationCallback {
        AnonymousClass9() {
        }

        @Override
        public void ignoreView(View view, boolean z) {
            if (view instanceof ImageViewEmoji) {
                ((ImageViewEmoji) view).ignoring = z;
            }
        }

        @Override
        public void onEndAnimation() {
            EmojiView.this.emojiSmoothScrolling = false;
            EmojiView.this.emojiGridView.updateEmojiDrawables();
        }

        @Override
        public void onPreAnimation() {
            EmojiView.this.emojiGridView.updateEmojiDrawables();
            EmojiView.this.emojiSmoothScrolling = true;
        }
    }

    public static abstract class ChooseStickerActionTracker {
        private final int currentAccount;
        private final long dialogId;
        private final long threadId;
        boolean typingWasSent;
        boolean visible = false;
        long lastActionTime = -1;

        public ChooseStickerActionTracker(int i, long j, long j2) {
            this.currentAccount = i;
            this.dialogId = j;
            this.threadId = j2;
        }

        public void reset() {
            if (this.typingWasSent) {
                MessagesController.getInstance(this.currentAccount).sendTyping(this.dialogId, this.threadId, 2, 0);
            }
            this.lastActionTime = -1L;
        }

        public void checkVisibility() {
            boolean isShown = isShown();
            this.visible = isShown;
            if (isShown) {
                return;
            }
            reset();
        }

        public void doSomeAction() {
            if (this.visible) {
                long j = this.lastActionTime;
                long currentTimeMillis = System.currentTimeMillis();
                if (j == -1) {
                    this.lastActionTime = currentTimeMillis;
                } else if (currentTimeMillis - this.lastActionTime > 2000) {
                    this.typingWasSent = true;
                    this.lastActionTime = System.currentTimeMillis();
                    MessagesController.getInstance(this.currentAccount).sendTyping(this.dialogId, this.threadId, 10, 0);
                }
            }
        }

        public abstract boolean isShown();
    }

    public static class CustomEmoji {
        public long documentId;
        public String emoticon;
        public TLRPC.TL_messages_stickerSet stickerSet;

        public TLRPC.Document getDocument() {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = this.stickerSet;
            if (tL_messages_stickerSet != null && tL_messages_stickerSet.documents != null) {
                for (int i = 0; i < this.stickerSet.documents.size(); i++) {
                    TLRPC.Document document = this.stickerSet.documents.get(i);
                    if (document != null && document.id == this.documentId) {
                        return document;
                    }
                }
            }
            return null;
        }
    }

    public interface DragListener {
        void onDrag(int i);

        void onDragCancel();

        void onDragEnd(float f);

        void onDragStart();
    }

    public class DraggableScrollSlidingTabStrip extends ScrollSlidingTabStrip {
        private float downX;
        private float downY;
        private boolean draggingHorizontally;
        private boolean draggingVertically;
        private boolean first;
        private float lastTranslateX;
        private float lastX;
        private boolean startedScroll;
        private final int touchSlop;
        private VelocityTracker vTracker;

        public DraggableScrollSlidingTabStrip(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            this.first = true;
            this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (isDragging()) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            if (motionEvent.getAction() == 0) {
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                this.downX = motionEvent.getRawX();
                this.downY = motionEvent.getRawY();
            } else if (!this.draggingVertically && !this.draggingHorizontally && EmojiView.this.dragListener != null && Math.abs(motionEvent.getRawY() - this.downY) >= this.touchSlop) {
                this.draggingVertically = true;
                this.downY = motionEvent.getRawY();
                EmojiView.this.dragListener.onDragStart();
                if (this.startedScroll) {
                    EmojiView.this.pager.endFakeDrag();
                    this.startedScroll = false;
                }
                return true;
            }
            return super.onInterceptTouchEvent(motionEvent);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (isDragging()) {
                return super.onTouchEvent(motionEvent);
            }
            if (this.first) {
                this.first = false;
                this.lastX = motionEvent.getX();
            }
            if (motionEvent.getAction() == 0 || motionEvent.getAction() == 2) {
                EmojiView.this.lastStickersX = motionEvent.getRawX();
            }
            if (motionEvent.getAction() == 0) {
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                this.downX = motionEvent.getRawX();
                this.downY = motionEvent.getRawY();
            } else if (!this.draggingVertically && !this.draggingHorizontally && EmojiView.this.dragListener != null) {
                if (Math.abs(motionEvent.getRawX() - this.downX) >= this.touchSlop && canScrollHorizontally((int) (this.downX - motionEvent.getRawX()))) {
                    this.draggingHorizontally = true;
                    AndroidUtilities.cancelRunOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable);
                    EmojiView.this.expandStickersByDragg = true;
                    EmojiView.this.updateStickerTabsPosition();
                } else if (Math.abs(motionEvent.getRawY() - this.downY) >= this.touchSlop) {
                    this.draggingVertically = true;
                    this.downY = motionEvent.getRawY();
                    EmojiView.this.dragListener.onDragStart();
                    if (this.startedScroll) {
                        EmojiView.this.pager.endFakeDrag();
                        this.startedScroll = false;
                    }
                }
            }
            if (EmojiView.this.expandStickersByDragg && (motionEvent.getAction() == 1 || motionEvent.getAction() == 3)) {
                AndroidUtilities.runOnUIThread(EmojiView.this.checkExpandStickerTabsRunnable, 1500L);
            }
            if (this.draggingVertically) {
                if (this.vTracker == null) {
                    this.vTracker = VelocityTracker.obtain();
                }
                this.vTracker.addMovement(motionEvent);
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    this.vTracker.computeCurrentVelocity(1000);
                    float yVelocity = this.vTracker.getYVelocity();
                    this.vTracker.recycle();
                    this.vTracker = null;
                    if (motionEvent.getAction() == 1) {
                        EmojiView.this.dragListener.onDragEnd(yVelocity);
                    } else {
                        EmojiView.this.dragListener.onDragCancel();
                    }
                    this.first = true;
                    this.draggingHorizontally = false;
                    this.draggingVertically = false;
                } else {
                    EmojiView.this.dragListener.onDrag(Math.round(motionEvent.getRawY() - this.downY));
                }
                cancelLongPress();
                return true;
            }
            float translationX = getTranslationX();
            if (getScrollX() == 0 && translationX == 0.0f) {
                if (this.startedScroll || this.lastX - motionEvent.getX() >= 0.0f) {
                    if (this.startedScroll && this.lastX - motionEvent.getX() > 0.0f && EmojiView.this.pager.isFakeDragging()) {
                        EmojiView.this.pager.endFakeDrag();
                        this.startedScroll = false;
                    }
                } else if (EmojiView.this.pager.beginFakeDrag()) {
                    this.startedScroll = true;
                    this.lastTranslateX = getTranslationX();
                }
            }
            if (this.startedScroll) {
                motionEvent.getX();
                try {
                    this.lastTranslateX = translationX;
                } catch (Exception e) {
                    try {
                        EmojiView.this.pager.endFakeDrag();
                    } catch (Exception unused) {
                    }
                    this.startedScroll = false;
                    FileLog.e(e);
                }
            }
            this.lastX = motionEvent.getX();
            if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                this.first = true;
                this.draggingHorizontally = false;
                this.draggingVertically = false;
                if (this.startedScroll) {
                    EmojiView.this.pager.endFakeDrag();
                    this.startedScroll = false;
                }
            }
            return this.startedScroll || super.onTouchEvent(motionEvent);
        }
    }

    public class EmojiGridAdapter extends RecyclerListView.SelectionAdapter {
        private int firstTrendingRow;
        private ArrayList frozenEmojiPacks;
        private int itemCount;
        private ArrayList packStartPosition;
        public int plainEmojisCount;
        private SparseIntArray positionToExpand;
        private SparseIntArray positionToSection;
        private SparseIntArray positionToUnlock;
        private int recentlyUsedHeaderRow;
        private ArrayList rowHashCodes;
        private SparseIntArray sectionToPosition;
        private int trendingHeaderRow;
        private int trendingRow;

        class AnonymousClass1 extends RecyclerView.ItemDecoration {
            AnonymousClass1() {
            }

            @Override
            public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                rect.right = AndroidUtilities.dp(2.0f);
            }
        }

        public class AnonymousClass2 extends DiffUtil.Callback {
            final ArrayList val$prevRowHashCodes;

            AnonymousClass2(ArrayList arrayList) {
                r2 = arrayList;
            }

            @Override
            public boolean areContentsTheSame(int i, int i2) {
                return true;
            }

            @Override
            public boolean areItemsTheSame(int i, int i2) {
                return ((Integer) r2.get(i)).equals(EmojiGridAdapter.this.rowHashCodes.get(i2));
            }

            @Override
            public int getNewListSize() {
                return EmojiGridAdapter.this.rowHashCodes.size();
            }

            @Override
            public int getOldListSize() {
                return r2.size();
            }
        }

        private EmojiGridAdapter() {
            this.trendingHeaderRow = -1;
            this.trendingRow = -1;
            this.firstTrendingRow = -1;
            this.recentlyUsedHeaderRow = -1;
            this.rowHashCodes = new ArrayList();
            this.positionToSection = new SparseIntArray();
            this.sectionToPosition = new SparseIntArray();
            this.positionToUnlock = new SparseIntArray();
            this.positionToExpand = new SparseIntArray();
            this.packStartPosition = new ArrayList();
        }

        EmojiGridAdapter(EmojiView emojiView, AnonymousClass1 anonymousClass1) {
            this();
        }

        public void lambda$expand$2(float f, int i) {
            try {
                LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(EmojiView.this.emojiGridView.getContext(), 0, f);
                linearSmoothScrollerCustom.setTargetPosition(i);
                EmojiView.this.emojiLayoutManager.startSmoothScroll(linearSmoothScrollerCustom);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void lambda$onCreateViewHolder$0(View view) {
            if (EmojiView.this.featuredEmojiSets == null || EmojiView.this.featuredEmojiSets.isEmpty() || ((TLRPC.StickerSetCovered) EmojiView.this.featuredEmojiSets.get(0)).set == null) {
                return;
            }
            MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit().putLong("emoji_featured_hidden", ((TLRPC.StickerSetCovered) EmojiView.this.featuredEmojiSets.get(0)).set.id).commit();
            if (EmojiView.this.emojiAdapter != null) {
                EmojiView.this.emojiAdapter.notifyItemRangeRemoved(1, 3);
            }
            if (EmojiView.this.emojiTabs != null) {
                EmojiView.this.emojiTabs.updateEmojiPacks(EmojiView.this.getEmojipacks());
            }
            updateRows();
        }

        public void lambda$onCreateViewHolder$1(View view, int i) {
            TLRPC.StickerSet stickerSet;
            if (view.getTag() instanceof TLRPC.StickerSetCovered) {
                TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) view.getTag();
                ArrayList arrayList = new ArrayList();
                ArrayList<TLRPC.StickerSetCovered> featuredEmojiSets = MediaDataController.getInstance(EmojiView.this.currentAccount).getFeaturedEmojiSets();
                int i2 = -1;
                for (int i3 = 0; i3 < featuredEmojiSets.size(); i3++) {
                    TLRPC.StickerSetCovered stickerSetCovered2 = featuredEmojiSets.get(i3);
                    if (stickerSetCovered2 != null && stickerSetCovered2.set != null) {
                        TLRPC.TL_inputStickerSetID tL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
                        TLRPC.StickerSet stickerSet2 = stickerSetCovered2.set;
                        tL_inputStickerSetID.id = stickerSet2.id;
                        tL_inputStickerSetID.access_hash = stickerSet2.access_hash;
                        arrayList.add(tL_inputStickerSetID);
                        if (stickerSetCovered != null && (stickerSet = stickerSetCovered.set) != null && stickerSet.id == stickerSetCovered2.set.id) {
                            i2 = i3;
                        }
                    }
                }
                MediaDataController.getInstance(EmojiView.this.currentAccount).markFeaturedStickersAsRead(true, true);
                EmojiPacksAlert emojiPacksAlert = new EmojiPacksAlert(EmojiView.this.fragment, EmojiView.this.getContext(), EmojiView.this.fragment == null ? EmojiView.this.resourcesProvider : EmojiView.this.fragment.getResourceProvider(), arrayList);
                if (i2 >= 0) {
                    emojiPacksAlert.highlight(i2);
                }
                if (EmojiView.this.fragment != null) {
                    EmojiView.this.fragment.showDialog(emojiPacksAlert);
                } else {
                    emojiPacksAlert.show();
                }
            }
        }

        private void removeGroupEmojiPackFromInstalled(TLRPC.StickerSet stickerSet, ArrayList arrayList) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) arrayList.get(i);
                if (tL_messages_stickerSet != null && tL_messages_stickerSet.set.id == stickerSet.id) {
                    arrayList.remove(i);
                    return;
                }
            }
        }

        public void expand(int i, View view) {
            int i2 = this.positionToExpand.get(i);
            if (i2 < 0 || i2 >= EmojiView.this.emojipacksProcessed.size()) {
                return;
            }
            EmojiPack emojiPack = (EmojiPack) EmojiView.this.emojipacksProcessed.get(i2);
            if (emojiPack.expanded) {
                return;
            }
            boolean z = i2 + 1 == EmojiView.this.emojipacksProcessed.size();
            int intValue = ((Integer) this.packStartPosition.get(i2)).intValue();
            EmojiView.this.expandedEmojiSets.add(Long.valueOf(emojiPack.set.id));
            boolean z2 = UserConfig.getInstance(EmojiView.this.currentAccount).isPremium() || EmojiView.this.allowEmojisForNonPremium;
            int spanCount = EmojiView.this.emojiLayoutManager.getSpanCount() * 3;
            int size = ((emojiPack.installed && !emojiPack.featured && (emojiPack.free || z2)) || emojiPack.expanded) ? emojiPack.documents.size() : Math.min(spanCount, emojiPack.documents.size());
            Integer num = null;
            Integer valueOf = emojiPack.documents.size() > spanCount ? Integer.valueOf(intValue + 1 + size) : null;
            emojiPack.expanded = true;
            int size2 = emojiPack.documents.size() - size;
            if (size2 > 0) {
                valueOf = Integer.valueOf(intValue + 1 + size);
                num = Integer.valueOf(size2);
            }
            processEmoji(false);
            updateRows();
            if (valueOf == null || num == null) {
                return;
            }
            EmojiView.this.animateExpandFromButton = view;
            EmojiView.this.animateExpandFromPosition = valueOf.intValue();
            EmojiView.this.animateExpandToPosition = valueOf.intValue() + num.intValue();
            EmojiView.this.animateExpandStartTime = SystemClock.elapsedRealtime();
            notifyItemRangeInserted(valueOf.intValue(), num.intValue());
            notifyItemChanged(valueOf.intValue());
            if (z) {
                final int intValue2 = valueOf.intValue();
                final float f = num.intValue() > spanCount / 2 ? 1.5f : 4.0f;
                EmojiView.this.post(new Runnable() {
                    @Override
                    public final void run() {
                        EmojiView.EmojiGridAdapter.this.lambda$expand$2(f, intValue2);
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return this.itemCount;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == this.trendingRow) {
                return 4;
            }
            if (i == this.trendingHeaderRow || i == this.recentlyUsedHeaderRow) {
                return 1;
            }
            if (this.positionToSection.indexOfKey(i) >= 0) {
                return this.positionToSection.get(i) >= EmojiData.dataColored.length ? 5 : 1;
            }
            if (EmojiView.this.needEmojiSearch && i == 0) {
                return 2;
            }
            if (this.positionToUnlock.indexOfKey(i) >= 0) {
                return 3;
            }
            return this.positionToExpand.indexOfKey(i) >= 0 ? 6 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return itemViewType == 0 || itemViewType == 4 || itemViewType == 3 || itemViewType == 6;
        }

        @Override
        public void notifyDataSetChanged() {
            notifyDataSetChanged(false);
        }

        public void notifyDataSetChanged(boolean z) {
            if (EmojiView.this.frozen) {
                return;
            }
            ArrayList arrayList = new ArrayList(this.rowHashCodes);
            MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
            ArrayList<TLRPC.StickerSetCovered> featuredEmojiSets = mediaDataController.getFeaturedEmojiSets();
            EmojiView.this.featuredEmojiSets.clear();
            int size = featuredEmojiSets.size();
            for (int i = 0; i < size; i++) {
                TLRPC.StickerSetCovered stickerSetCovered = featuredEmojiSets.get(i);
                if (!mediaDataController.isStickerPackInstalled(stickerSetCovered.set.id) || EmojiView.this.installedEmojiSets.contains(Long.valueOf(stickerSetCovered.set.id))) {
                    EmojiView.this.featuredEmojiSets.add(stickerSetCovered);
                }
            }
            processEmoji(z);
            updateRows();
            if (EmojiView.this.trendingEmojiAdapter != null) {
                EmojiView.this.trendingEmojiAdapter.notifyDataSetChanged();
            }
            DiffUtil.calculateDiff(new DiffUtil.Callback() {
                final ArrayList val$prevRowHashCodes;

                AnonymousClass2(ArrayList arrayList2) {
                    r2 = arrayList2;
                }

                @Override
                public boolean areContentsTheSame(int i2, int i22) {
                    return true;
                }

                @Override
                public boolean areItemsTheSame(int i2, int i22) {
                    return ((Integer) r2.get(i2)).equals(EmojiGridAdapter.this.rowHashCodes.get(i22));
                }

                @Override
                public int getNewListSize() {
                    return EmojiGridAdapter.this.rowHashCodes.size();
                }

                @Override
                public int getOldListSize() {
                    return r2.size();
                }
            }, false).dispatchUpdatesTo(this);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            String str;
            String str2;
            Long l;
            TLRPC.Document document;
            String str3;
            TLRPC.Document document2;
            String str4;
            String str5;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            EmojiPack emojiPack = null;
            if (itemViewType != 0) {
                if (itemViewType == 1) {
                    StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                    stickerSetNameCell.position = i;
                    int i2 = this.positionToSection.get(i);
                    if (i == this.trendingHeaderRow) {
                        stickerSetNameCell.setText(LocaleController.getString(R.string.FeaturedEmojiPacks), R.drawable.msg_close, LocaleController.getString(R.string.AccDescrCloseTrendingEmoji));
                        return;
                    }
                    if (i == this.recentlyUsedHeaderRow) {
                        str5 = LocaleController.getString(R.string.RecentlyUsed);
                    } else if (i2 >= EmojiView.this.emojiTitles.length) {
                        try {
                            stickerSetNameCell.setText(((EmojiPack) EmojiView.this.emojipacksProcessed.get(i2 - EmojiView.this.emojiTitles.length)).set.title, 0);
                            return;
                        } catch (Exception unused) {
                            str5 = "";
                        }
                    } else {
                        str5 = EmojiView.this.emojiTitles[i2];
                    }
                    stickerSetNameCell.setText(str5, 0);
                    return;
                }
                if (itemViewType != 5) {
                    if (itemViewType != 6) {
                        return;
                    }
                    EmojiPackExpand emojiPackExpand = (EmojiPackExpand) viewHolder.itemView;
                    int i3 = this.positionToExpand.get(i);
                    int spanCount = EmojiView.this.emojiLayoutManager.getSpanCount() * 3;
                    if (i3 >= 0 && i3 < EmojiView.this.emojipacksProcessed.size()) {
                        emojiPack = (EmojiPack) EmojiView.this.emojipacksProcessed.get(i3);
                    }
                    if (emojiPack != null) {
                        emojiPackExpand.textView.setText("+" + ((emojiPack.documents.size() - spanCount) + 1));
                        return;
                    }
                    return;
                }
                EmojiPackHeader emojiPackHeader = (EmojiPackHeader) viewHolder.itemView;
                int length = this.positionToSection.get(i) - EmojiView.this.emojiTitles.length;
                EmojiPack emojiPack2 = (EmojiPack) EmojiView.this.emojipacksProcessed.get(length);
                int i4 = length - 1;
                EmojiPack emojiPack3 = i4 >= 0 ? (EmojiPack) EmojiView.this.emojipacksProcessed.get(i4) : null;
                if (emojiPack2 == null || !emojiPack2.featured || (emojiPack3 != null && !emojiPack3.free && emojiPack3.installed && !UserConfig.getInstance(EmojiView.this.currentAccount).isPremium())) {
                    z = false;
                }
                if (emojiPack2 != null && emojiPack2.needLoadSet != null) {
                    MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSet(emojiPack2.needLoadSet, false);
                    emojiPack2.needLoadSet = null;
                }
                emojiPackHeader.setStickerSet(emojiPack2, z);
                return;
            }
            ImageViewEmoji imageViewEmoji = (ImageViewEmoji) viewHolder.itemView;
            imageViewEmoji.position = i;
            imageViewEmoji.pack = null;
            if (EmojiView.this.needEmojiSearch) {
                i--;
            }
            if (this.recentlyUsedHeaderRow >= 0) {
                i--;
            }
            if (this.trendingRow >= 0) {
                i -= 2;
            }
            int size = EmojiView.this.getRecentEmoji().size();
            if (i < size) {
                String str6 = EmojiView.this.getRecentEmoji().get(i);
                if (str6 != null && str6.startsWith("animated_")) {
                    try {
                        l = Long.valueOf(Long.parseLong(str6.substring(9)));
                        str = null;
                        str4 = null;
                    } catch (Exception unused2) {
                    }
                    str3 = str4;
                    document2 = null;
                }
                str = str6;
                str4 = str;
                l = null;
                str3 = str4;
                document2 = null;
            } else {
                int i5 = 0;
                while (true) {
                    String[][] strArr = EmojiData.dataColored;
                    if (i5 >= strArr.length) {
                        str = null;
                        break;
                    }
                    String[] strArr2 = strArr[i5];
                    int length2 = strArr2.length + 1;
                    int i6 = (i - size) - 1;
                    if (i6 < 0 || i >= size + length2) {
                        size += length2;
                        i5++;
                    } else {
                        String str7 = strArr2[i6];
                        String str8 = Emoji.emojiColor.get(str7);
                        if (str8 != null) {
                            str = EmojiView.addColorToCode(str7, str8);
                            str2 = str7;
                        } else {
                            str = str7;
                        }
                    }
                }
                str2 = str;
                if (str2 == null) {
                    boolean isPremium = UserConfig.getInstance(EmojiView.this.currentAccount).isPremium();
                    int spanCount2 = EmojiView.this.emojiLayoutManager.getSpanCount() * 3;
                    for (int i7 = 0; i7 < this.packStartPosition.size(); i7++) {
                        EmojiPack emojiPack4 = (EmojiPack) EmojiView.this.emojipacksProcessed.get(i7);
                        int intValue = ((Integer) this.packStartPosition.get(i7)).intValue() + 1;
                        int size2 = ((emojiPack4.installed && !emojiPack4.featured && (emojiPack4.free || isPremium)) || emojiPack4.expanded) ? emojiPack4.documents.size() : Math.min(spanCount2, emojiPack4.documents.size());
                        int i8 = imageViewEmoji.position;
                        if (i8 >= intValue && i8 - intValue < size2) {
                            imageViewEmoji.pack = emojiPack4;
                            TLRPC.Document document3 = (TLRPC.Document) emojiPack4.documents.get(imageViewEmoji.position - intValue);
                            if (document3 == null) {
                                document = document3;
                                l = null;
                            } else {
                                document = document3;
                                l = Long.valueOf(document3.id);
                            }
                            str3 = str2;
                            document2 = document;
                            z = false;
                        }
                    }
                }
                l = null;
                document = null;
                str3 = str2;
                document2 = document;
                z = false;
            }
            if (l != null) {
                imageViewEmoji.setPadding(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f));
            } else {
                imageViewEmoji.setPadding(0, 0, 0, 0);
            }
            if (l != null) {
                imageViewEmoji.setImageDrawable(null, z);
                if (imageViewEmoji.getSpan() == null || imageViewEmoji.getSpan().getDocumentId() != l.longValue()) {
                    if (document2 != null) {
                        imageViewEmoji.setSpan(new AnimatedEmojiSpan(document2, (Paint.FontMetricsInt) null));
                    } else {
                        imageViewEmoji.setSpan(new AnimatedEmojiSpan(l.longValue(), (Paint.FontMetricsInt) null));
                    }
                }
            } else {
                imageViewEmoji.setImageDrawable(Emoji.getEmojiBigDrawable(str), z);
                imageViewEmoji.setSpan(null);
            }
            imageViewEmoji.setTag(str3);
            imageViewEmoji.setContentDescription(str);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View imageViewEmoji;
            StickerSetNameCell stickerSetNameCell;
            if (i != 0) {
                if (i == 1) {
                    StickerSetNameCell stickerSetNameCell2 = new StickerSetNameCell(EmojiView.this.getContext(), true, EmojiView.this.resourcesProvider);
                    stickerSetNameCell2.setOnIconClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            EmojiView.EmojiGridAdapter.this.lambda$onCreateViewHolder$0(view);
                        }
                    });
                    stickerSetNameCell = stickerSetNameCell2;
                } else if (i == 3) {
                    EmojiView emojiView = EmojiView.this;
                    imageViewEmoji = new EmojiPackButton(emojiView.getContext());
                } else if (i == 4) {
                    EmojiView emojiView2 = EmojiView.this;
                    Context context = emojiView2.getContext();
                    EmojiView emojiView3 = EmojiView.this;
                    ?? trendingListView = new TrendingListView(context, emojiView3.trendingEmojiAdapter = new TrendingAdapter(true));
                    trendingListView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(8.0f), 0);
                    trendingListView.setClipToPadding(false);
                    trendingListView.addItemDecoration(new RecyclerView.ItemDecoration() {
                        AnonymousClass1() {
                        }

                        @Override
                        public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                            rect.right = AndroidUtilities.dp(2.0f);
                        }
                    });
                    trendingListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                        @Override
                        public final void onItemClick(View view, int i2) {
                            EmojiView.EmojiGridAdapter.this.lambda$onCreateViewHolder$1(view, i2);
                        }
                    });
                    stickerSetNameCell = trendingListView;
                } else if (i == 5) {
                    EmojiView emojiView4 = EmojiView.this;
                    imageViewEmoji = new EmojiPackHeader(emojiView4.getContext());
                } else if (i != 6) {
                    imageViewEmoji = new View(EmojiView.this.getContext());
                    imageViewEmoji.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                } else {
                    imageViewEmoji = new EmojiPackExpand(EmojiView.this.getContext(), EmojiView.this.resourcesProvider);
                }
                imageViewEmoji = stickerSetNameCell;
            } else {
                imageViewEmoji = new ImageViewEmoji(EmojiView.this.getContext());
            }
            return new RecyclerListView.Holder(imageViewEmoji);
        }

        public void processEmoji(boolean r14) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiGridAdapter.processEmoji(boolean):void");
        }

        public void updateRows() {
            this.positionToSection.clear();
            this.sectionToPosition.clear();
            this.positionToUnlock.clear();
            this.positionToExpand.clear();
            this.packStartPosition.clear();
            this.rowHashCodes.clear();
            this.itemCount = 0;
            boolean z = UserConfig.getInstance(EmojiView.this.currentAccount).isPremium() || EmojiView.this.allowEmojisForNonPremium;
            if (EmojiView.this.needEmojiSearch) {
                this.itemCount++;
                this.rowHashCodes.add(-1);
            }
            if (!z || !EmojiView.this.allowAnimatedEmoji || EmojiView.this.featuredEmojiSets.size() <= 0 || ((TLRPC.StickerSetCovered) EmojiView.this.featuredEmojiSets.get(0)).set == null || MessagesController.getEmojiSettings(EmojiView.this.currentAccount).getLong("emoji_featured_hidden", 0L) == ((TLRPC.StickerSetCovered) EmojiView.this.featuredEmojiSets.get(0)).set.id || !EmojiView.this.needEmojiSearch) {
                this.trendingHeaderRow = -1;
                this.trendingRow = -1;
                this.recentlyUsedHeaderRow = -1;
            } else {
                int i = this.itemCount;
                this.trendingHeaderRow = i;
                this.trendingRow = i + 1;
                this.itemCount = i + 3;
                this.recentlyUsedHeaderRow = i + 2;
                this.rowHashCodes.add(324953);
                this.rowHashCodes.add(123342);
                this.rowHashCodes.add(929132);
            }
            ArrayList<String> recentEmoji = EmojiView.this.getRecentEmoji();
            if (EmojiView.this.emojiTabs != null) {
                EmojiView.this.emojiTabs.showRecent(!recentEmoji.isEmpty());
            }
            this.itemCount += recentEmoji.size();
            for (int i2 = 0; i2 < recentEmoji.size(); i2++) {
                this.rowHashCodes.add(Integer.valueOf(Objects.hash(-43263, recentEmoji.get(i2))));
            }
            int i3 = 0;
            int i4 = 0;
            while (true) {
                String[][] strArr = EmojiData.dataColored;
                if (i3 >= strArr.length) {
                    break;
                }
                this.positionToSection.put(this.itemCount, i4);
                this.sectionToPosition.put(i4, this.itemCount);
                this.itemCount += strArr[i3].length + 1;
                this.rowHashCodes.add(Integer.valueOf(Objects.hash(43245, Integer.valueOf(i3))));
                int i5 = 0;
                while (true) {
                    String[] strArr2 = EmojiData.dataColored[i3];
                    if (i5 < strArr2.length) {
                        this.rowHashCodes.add(Integer.valueOf(strArr2[i5].hashCode()));
                        i5++;
                    }
                }
                i3++;
                i4++;
            }
            int spanCount = EmojiView.this.emojiLayoutManager.getSpanCount() * 3;
            this.plainEmojisCount = this.itemCount;
            this.firstTrendingRow = -1;
            if (EmojiView.this.emojipacksProcessed != null) {
                int i6 = 0;
                while (i6 < EmojiView.this.emojipacksProcessed.size()) {
                    this.positionToSection.put(this.itemCount, i4);
                    this.sectionToPosition.put(i4, this.itemCount);
                    this.packStartPosition.add(Integer.valueOf(this.itemCount));
                    EmojiPack emojiPack = (EmojiPack) EmojiView.this.emojipacksProcessed.get(i6);
                    boolean z2 = emojiPack.featured;
                    if (z2 && this.firstTrendingRow < 0) {
                        this.firstTrendingRow = this.itemCount;
                    }
                    int size = ((emojiPack.installed && !z2 && (emojiPack.free || z)) || emojiPack.expanded) ? emojiPack.documents.size() : Math.min(spanCount, emojiPack.documents.size());
                    int i7 = 1 + size;
                    if (emojiPack.expanded || emojiPack.documents.size() <= spanCount) {
                        size = i7;
                    }
                    ArrayList arrayList = this.rowHashCodes;
                    Integer valueOf = Integer.valueOf(emojiPack.featured ? 56345 : -495231);
                    TLRPC.StickerSet stickerSet = emojiPack.set;
                    arrayList.add(Integer.valueOf(Objects.hash(valueOf, Long.valueOf(stickerSet == null ? i6 : stickerSet.id), Boolean.valueOf(emojiPack.forGroup))));
                    for (int i8 = 1; i8 < size; i8++) {
                        this.rowHashCodes.add(Integer.valueOf(Objects.hash(Integer.valueOf(emojiPack.featured ? 3442 : -9964), Long.valueOf(((TLRPC.Document) emojiPack.documents.get(i8 - 1)).id))));
                    }
                    this.itemCount += size;
                    if (!emojiPack.expanded && emojiPack.documents.size() > spanCount) {
                        this.positionToExpand.put(this.itemCount, i6);
                        this.rowHashCodes.add(Integer.valueOf(Objects.hash(Integer.valueOf(emojiPack.featured ? -65174 : 92242), Long.valueOf(emojiPack.set.id))));
                        this.itemCount++;
                    }
                    i6++;
                    i4++;
                }
            }
        }
    }

    public class EmojiGridSpacing extends RecyclerView.ItemDecoration {
        public EmojiGridSpacing() {
        }

        @Override
        public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            float f;
            if (view instanceof StickerSetNameCell) {
                rect.left = AndroidUtilities.dp(5.0f);
                rect.right = AndroidUtilities.dp(5.0f);
                if (recyclerView.getChildAdapterPosition(view) + 1 <= EmojiView.this.emojiAdapter.plainEmojisCount || UserConfig.getInstance(EmojiView.this.currentAccount).isPremium() || EmojiView.this.allowEmojisForNonPremium) {
                    return;
                } else {
                    f = 10.0f;
                }
            } else if (!(view instanceof RecyclerListView) && !(view instanceof EmojiPackHeader)) {
                if (view instanceof BackupImageView) {
                    rect.bottom = AndroidUtilities.dp(12.0f);
                    return;
                }
                return;
            } else {
                rect.left = -EmojiView.this.emojiGridView.getPaddingLeft();
                rect.right = -EmojiView.this.emojiGridView.getPaddingRight();
                if (!(view instanceof EmojiPackHeader)) {
                    return;
                } else {
                    f = 8.0f;
                }
            }
            rect.top = AndroidUtilities.dp(f);
        }
    }

    public class EmojiGridView extends RecyclerListView {
        private SparseIntArray headerWidthsCache;
        private boolean ignoreLayout;
        private int lastChildCount;
        ArrayList lineDrawables;
        ArrayList lineDrawablesTmp;
        private AnimatedFloat premiumT;
        private SparseArray touches;
        ArrayList unusedArrays;
        ArrayList unusedLineDrawables;
        SparseArray viewsGroupedByLines;

        class DrawingInBackgroundLine extends DrawingInBackgroundThreadDrawable {
            ArrayList imageViewEmojis;
            public int position;
            public int startOffset;
            ArrayList drawInBackgroundViews = new ArrayList();
            private OvershootInterpolator appearScaleInterpolator = new OvershootInterpolator(3.0f);

            DrawingInBackgroundLine() {
            }

            @Override
            public void draw(android.graphics.Canvas r9, long r10, int r12, int r13, float r14) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiGridView.DrawingInBackgroundLine.draw(android.graphics.Canvas, long, int, int, float):void");
            }

            @Override
            public void drawInBackground(Canvas canvas) {
                for (int i = 0; i < this.drawInBackgroundViews.size(); i++) {
                    ImageViewEmoji imageViewEmoji = (ImageViewEmoji) this.drawInBackgroundViews.get(i);
                    AnimatedEmojiDrawable animatedEmojiDrawable = imageViewEmoji.drawable;
                    if (animatedEmojiDrawable != null) {
                        animatedEmojiDrawable.draw(canvas, imageViewEmoji.backgroundThreadDrawHolder[this.threadIndex], false);
                    }
                }
            }

            @Override
            protected void drawInUiThread(android.graphics.Canvas r20, float r21) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiGridView.DrawingInBackgroundLine.drawInUiThread(android.graphics.Canvas, float):void");
            }

            @Override
            public void onFrameReady() {
                super.onFrameReady();
                for (int i = 0; i < this.drawInBackgroundViews.size(); i++) {
                    ImageViewEmoji imageViewEmoji = (ImageViewEmoji) this.drawInBackgroundViews.get(i);
                    if (imageViewEmoji.backgroundThreadDrawHolder != null) {
                        imageViewEmoji.backgroundThreadDrawHolder[this.threadIndex].release();
                    }
                }
                EmojiView.this.emojiGridView.invalidate();
            }

            @Override
            public void prepareDraw(long j) {
                AnimatedEmojiDrawable animatedEmojiDrawable;
                this.drawInBackgroundViews.clear();
                for (int i = 0; i < this.imageViewEmojis.size(); i++) {
                    ImageViewEmoji imageViewEmoji = (ImageViewEmoji) this.imageViewEmojis.get(i);
                    if (imageViewEmoji.getSpan() != null && (animatedEmojiDrawable = (AnimatedEmojiDrawable) EmojiView.this.animatedEmojiDrawables.get(imageViewEmoji.span.getDocumentId())) != null && animatedEmojiDrawable.getImageReceiver() != null) {
                        animatedEmojiDrawable.update(j);
                        ImageReceiver.BackgroundThreadDrawHolder[] backgroundThreadDrawHolderArr = imageViewEmoji.backgroundThreadDrawHolder;
                        int i2 = this.threadIndex;
                        ImageReceiver imageReceiver = animatedEmojiDrawable.getImageReceiver();
                        ImageReceiver.BackgroundThreadDrawHolder[] backgroundThreadDrawHolderArr2 = imageViewEmoji.backgroundThreadDrawHolder;
                        int i3 = this.threadIndex;
                        backgroundThreadDrawHolderArr[i2] = imageReceiver.setDrawInBackgroundThread(backgroundThreadDrawHolderArr2[i3], i3);
                        imageViewEmoji.backgroundThreadDrawHolder[this.threadIndex].time = j;
                        imageViewEmoji.backgroundThreadDrawHolder[this.threadIndex].overrideAlpha = 1.0f;
                        animatedEmojiDrawable.setAlpha(255);
                        int height = (int) (imageViewEmoji.getHeight() * 0.03f);
                        android.graphics.Rect rect = AndroidUtilities.rectTmp2;
                        rect.set((imageViewEmoji.getLeft() + imageViewEmoji.getPaddingLeft()) - this.startOffset, height, (imageViewEmoji.getRight() - imageViewEmoji.getPaddingRight()) - this.startOffset, ((imageViewEmoji.getMeasuredHeight() + height) - imageViewEmoji.getPaddingTop()) - imageViewEmoji.getPaddingBottom());
                        imageViewEmoji.backgroundThreadDrawHolder[this.threadIndex].setBounds(rect);
                        imageViewEmoji.drawable = animatedEmojiDrawable;
                        imageViewEmoji.imageReceiver = animatedEmojiDrawable.getImageReceiver();
                        imageViewEmoji.backgroundThreadDrawHolder[this.threadIndex].colorFilter = animatedEmojiDrawable.canOverrideColor() ? EmojiView.this.animatedEmojiTextColorFilter : null;
                        this.drawInBackgroundViews.add(imageViewEmoji);
                    }
                }
            }
        }

        public class TouchDownInfo {
            long time;
            View view;
            float x;
            float y;

            TouchDownInfo() {
            }
        }

        public EmojiGridView(Context context) {
            super(context);
            this.viewsGroupedByLines = new SparseArray();
            this.lineDrawables = new ArrayList();
            this.lineDrawablesTmp = new ArrayList();
            this.unusedArrays = new ArrayList();
            this.unusedLineDrawables = new ArrayList();
            this.lastChildCount = -1;
            this.headerWidthsCache = new SparseIntArray();
            this.premiumT = new AnimatedFloat(this, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
        }

        private AnimatedEmojiSpan[] getAnimatedEmojiSpans() {
            AnimatedEmojiSpan[] animatedEmojiSpanArr = new AnimatedEmojiSpan[EmojiView.this.emojiGridView.getChildCount()];
            for (int i = 0; i < EmojiView.this.emojiGridView.getChildCount(); i++) {
                View childAt = EmojiView.this.emojiGridView.getChildAt(i);
                if (childAt instanceof ImageViewEmoji) {
                    animatedEmojiSpanArr[i] = ((ImageViewEmoji) childAt).getSpan();
                }
            }
            return animatedEmojiSpanArr;
        }

        public long animateExpandAppearDuration() {
            return Math.max(600L, Math.min(55, EmojiView.this.animateExpandToPosition - EmojiView.this.animateExpandFromPosition) * 40);
        }

        public long animateExpandCrossfadeDuration() {
            return Math.max(400L, Math.min(45, EmojiView.this.animateExpandToPosition - EmojiView.this.animateExpandFromPosition) * 35);
        }

        public long animateExpandDuration() {
            return animateExpandAppearDuration() + animateExpandCrossfadeDuration() + 150;
        }

        public void clearAllTouches() {
            if (this.touches != null) {
                while (this.touches.size() > 0) {
                    TouchDownInfo touchDownInfo = (TouchDownInfo) this.touches.valueAt(0);
                    this.touches.removeAt(0);
                    if (touchDownInfo != null) {
                        View view = touchDownInfo.view;
                        if (view != null && Build.VERSION.SDK_INT >= 21 && AppCompatImageHelper$$ExternalSyntheticApiModelOutline0.m(view.getBackground())) {
                            touchDownInfo.view.getBackground().setState(new int[0]);
                        }
                        View view2 = touchDownInfo.view;
                        if (view2 != null) {
                            view2.setPressed(false);
                        }
                    }
                }
            }
        }

        public void clearTouchesFor(View view) {
            if (this.touches != null) {
                int i = 0;
                while (i < this.touches.size()) {
                    TouchDownInfo touchDownInfo = (TouchDownInfo) this.touches.valueAt(i);
                    if (touchDownInfo.view == view) {
                        this.touches.removeAt(i);
                        i--;
                        View view2 = touchDownInfo.view;
                        if (view2 != null && Build.VERSION.SDK_INT >= 21 && AppCompatImageHelper$$ExternalSyntheticApiModelOutline0.m(view2.getBackground())) {
                            touchDownInfo.view.getBackground().setState(new int[0]);
                        }
                        View view3 = touchDownInfo.view;
                        if (view3 != null) {
                            view3.setPressed(false);
                        }
                    }
                    i++;
                }
            }
        }

        @Override
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            if (this.lastChildCount != getChildCount()) {
                updateEmojiDrawables();
                this.lastChildCount = getChildCount();
            }
            for (int i = 0; i < this.viewsGroupedByLines.size(); i++) {
                ArrayList arrayList = (ArrayList) this.viewsGroupedByLines.valueAt(i);
                arrayList.clear();
                this.unusedArrays.add(arrayList);
            }
            this.viewsGroupedByLines.clear();
            boolean z = EmojiView.this.animateExpandStartTime > 0 && SystemClock.elapsedRealtime() - EmojiView.this.animateExpandStartTime < animateExpandDuration() && EmojiView.this.animateExpandFromButton != null && EmojiView.this.animateExpandFromPosition >= 0;
            if (EmojiView.this.animatedEmojiDrawables != null && EmojiView.this.emojiGridView != null) {
                for (int i2 = 0; i2 < EmojiView.this.emojiGridView.getChildCount(); i2++) {
                    View childAt = EmojiView.this.emojiGridView.getChildAt(i2);
                    if (childAt instanceof ImageViewEmoji) {
                        int top = childAt.getTop() + ((int) childAt.getTranslationY());
                        ArrayList arrayList2 = (ArrayList) this.viewsGroupedByLines.get(top);
                        if (arrayList2 == null) {
                            if (this.unusedArrays.isEmpty()) {
                                arrayList2 = new ArrayList();
                            } else {
                                ArrayList arrayList3 = this.unusedArrays;
                                arrayList2 = (ArrayList) arrayList3.remove(arrayList3.size() - 1);
                            }
                            this.viewsGroupedByLines.put(top, arrayList2);
                        }
                        arrayList2.add((ImageViewEmoji) childAt);
                    }
                    if (z && childAt != null && getChildAdapterPosition(childAt) == EmojiView.this.animateExpandFromPosition - 1) {
                        float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(MathUtils.clamp(((float) (SystemClock.elapsedRealtime() - EmojiView.this.animateExpandStartTime)) / 140.0f, 0.0f, 1.0f));
                        if (interpolation < 1.0f) {
                            float f = 1.0f - interpolation;
                            canvas.saveLayerAlpha(childAt.getLeft(), childAt.getTop(), childAt.getRight(), childAt.getBottom(), (int) (255.0f * f), 31);
                            canvas.translate(childAt.getLeft(), childAt.getTop());
                            float f2 = (f * 0.5f) + 0.5f;
                            canvas.scale(f2, f2, childAt.getWidth() / 2.0f, childAt.getHeight() / 2.0f);
                            EmojiView.this.animateExpandFromButton.draw(canvas);
                            canvas.restore();
                        }
                    }
                }
            }
            this.lineDrawablesTmp.clear();
            this.lineDrawablesTmp.addAll(this.lineDrawables);
            this.lineDrawables.clear();
            long currentTimeMillis = System.currentTimeMillis();
            int i3 = 0;
            while (true) {
                DrawingInBackgroundLine drawingInBackgroundLine = null;
                if (i3 >= this.viewsGroupedByLines.size()) {
                    break;
                }
                ArrayList arrayList4 = (ArrayList) this.viewsGroupedByLines.valueAt(i3);
                ImageViewEmoji imageViewEmoji = (ImageViewEmoji) arrayList4.get(0);
                int i4 = imageViewEmoji.position;
                int i5 = 0;
                while (true) {
                    if (i5 >= this.lineDrawablesTmp.size()) {
                        break;
                    }
                    if (((DrawingInBackgroundLine) this.lineDrawablesTmp.get(i5)).position == i4) {
                        drawingInBackgroundLine = (DrawingInBackgroundLine) this.lineDrawablesTmp.get(i5);
                        this.lineDrawablesTmp.remove(i5);
                        break;
                    }
                    i5++;
                }
                if (drawingInBackgroundLine == null) {
                    if (this.unusedLineDrawables.isEmpty()) {
                        drawingInBackgroundLine = new DrawingInBackgroundLine();
                    } else {
                        ArrayList arrayList5 = this.unusedLineDrawables;
                        drawingInBackgroundLine = (DrawingInBackgroundLine) arrayList5.remove(arrayList5.size() - 1);
                    }
                    drawingInBackgroundLine.position = i4;
                    drawingInBackgroundLine.onAttachToWindow();
                }
                this.lineDrawables.add(drawingInBackgroundLine);
                drawingInBackgroundLine.imageViewEmojis = arrayList4;
                canvas.save();
                canvas.translate(imageViewEmoji.getLeft(), imageViewEmoji.getY() + imageViewEmoji.getPaddingTop());
                drawingInBackgroundLine.startOffset = imageViewEmoji.getLeft();
                int measuredWidth = getMeasuredWidth() - (imageViewEmoji.getLeft() * 2);
                int measuredHeight = imageViewEmoji.getMeasuredHeight() - imageViewEmoji.getPaddingBottom();
                if (measuredWidth > 0 && measuredHeight > 0) {
                    drawingInBackgroundLine.draw(canvas, currentTimeMillis, measuredWidth, measuredHeight, 1.0f);
                }
                canvas.restore();
                i3++;
            }
            for (int i6 = 0; i6 < this.lineDrawablesTmp.size(); i6++) {
                if (this.unusedLineDrawables.size() < 3) {
                    this.unusedLineDrawables.add((DrawingInBackgroundLine) this.lineDrawablesTmp.get(i6));
                    ((DrawingInBackgroundLine) this.lineDrawablesTmp.get(i6)).imageViewEmojis = null;
                    ((DrawingInBackgroundLine) this.lineDrawablesTmp.get(i6)).reset();
                } else {
                    ((DrawingInBackgroundLine) this.lineDrawablesTmp.get(i6)).onDetachFromWindow();
                }
            }
            this.lineDrawablesTmp.clear();
        }

        @Override
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            View view;
            View view2;
            boolean z = motionEvent.getActionMasked() == 5 || motionEvent.getActionMasked() == 0;
            boolean z2 = motionEvent.getActionMasked() == 6 || motionEvent.getActionMasked() == 1;
            boolean z3 = motionEvent.getActionMasked() == 3;
            if (z || z2 || z3) {
                int actionIndex = motionEvent.getActionIndex();
                int pointerId = motionEvent.getPointerId(actionIndex);
                if (this.touches == null) {
                    this.touches = new SparseArray();
                }
                float x = motionEvent.getX(actionIndex);
                float y = motionEvent.getY(actionIndex);
                View findChildViewUnder = findChildViewUnder(x, y);
                if (!z) {
                    TouchDownInfo touchDownInfo = (TouchDownInfo) this.touches.get(pointerId);
                    this.touches.remove(pointerId);
                    if (findChildViewUnder != null && touchDownInfo != null && Math.sqrt(Math.pow(x - touchDownInfo.x, 2.0d) + Math.pow(y - touchDownInfo.y, 2.0d)) < AndroidUtilities.touchSlop * 3.0f && !z3 && (!EmojiView.this.colorPickerView.isShowing() || SystemClock.elapsedRealtime() - touchDownInfo.time < ViewConfiguration.getLongPressTimeout())) {
                        View view3 = touchDownInfo.view;
                        int childAdapterPosition = getChildAdapterPosition(view3);
                        if (view3 instanceof ImageViewEmoji) {
                            EmojiView.this.sendEmoji((ImageViewEmoji) view3, null);
                        } else if (view3 instanceof EmojiPackExpand) {
                            EmojiView.this.emojiAdapter.expand(childAdapterPosition, (EmojiPackExpand) view3);
                        } else if (view3 != null) {
                            view3.callOnClick();
                        }
                        try {
                            performHapticFeedback(3, 1);
                        } catch (Exception unused) {
                        }
                    }
                    if (touchDownInfo != null && (view2 = touchDownInfo.view) != null && Build.VERSION.SDK_INT >= 21 && AppCompatImageHelper$$ExternalSyntheticApiModelOutline0.m(view2.getBackground())) {
                        touchDownInfo.view.getBackground().setState(new int[0]);
                    }
                    if (touchDownInfo != null && (view = touchDownInfo.view) != null) {
                        view.setPressed(false);
                    }
                } else if (findChildViewUnder != null) {
                    TouchDownInfo touchDownInfo2 = new TouchDownInfo();
                    touchDownInfo2.x = x;
                    touchDownInfo2.y = y;
                    touchDownInfo2.time = SystemClock.elapsedRealtime();
                    touchDownInfo2.view = findChildViewUnder;
                    if (Build.VERSION.SDK_INT >= 21 && AppCompatImageHelper$$ExternalSyntheticApiModelOutline0.m(findChildViewUnder.getBackground())) {
                        findChildViewUnder.getBackground().setState(new int[]{16842919, 16842910});
                    }
                    touchDownInfo2.view.setPressed(true);
                    this.touches.put(pointerId, touchDownInfo2);
                    stopScroll();
                }
            }
            return super.dispatchTouchEvent(motionEvent) || (!z3 && this.touches.size() > 0);
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateEmojiDrawables();
        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            AnimatedEmojiSpan.release(this, (LongSparseArray<AnimatedEmojiDrawable>) EmojiView.this.animatedEmojiDrawables);
            for (int i = 0; i < this.lineDrawables.size(); i++) {
                ((DrawingInBackgroundLine) this.lineDrawables.get(i)).onDetachFromWindow();
            }
            for (int i2 = 0; i2 < this.unusedLineDrawables.size(); i2++) {
                ((DrawingInBackgroundLine) this.unusedLineDrawables.get(i2)).onDetachFromWindow();
            }
            this.unusedLineDrawables.addAll(this.lineDrawables);
            this.lineDrawables.clear();
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, this, 0, EmojiView.this.contentPreviewViewerDelegate, this.resourcesProvider);
        }

        @Override
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            if (EmojiView.this.needEmojiSearch && EmojiView.this.firstEmojiAttach) {
                this.ignoreLayout = true;
                EmojiView.this.emojiLayoutManager.scrollToPositionWithOffset(0, 0);
                EmojiView.this.firstEmojiAttach = false;
                this.ignoreLayout = false;
            }
            super.onLayout(z, i, i2, i3, i4);
            EmojiView.this.checkEmojiSearchFieldScroll(true);
            updateEmojiDrawables();
        }

        @Override
        public void onMeasure(int i, int i2) {
            this.ignoreLayout = true;
            int size = View.MeasureSpec.getSize(i);
            int spanCount = EmojiView.this.emojiLayoutManager.getSpanCount();
            EmojiView.this.emojiLayoutManager.setSpanCount(Math.max(1, size / AndroidUtilities.dp(AndroidUtilities.isTablet() ? 60.0f : 45.0f)));
            this.ignoreLayout = false;
            super.onMeasure(i, i2);
            if (spanCount != EmojiView.this.emojiLayoutManager.getSpanCount()) {
                EmojiView.this.emojiAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onScrollStateChanged(int i) {
            super.onScrollStateChanged(i);
            if (i == 0) {
                if (!canScrollVertically(-1) || !canScrollVertically(1)) {
                    EmojiView.this.showBottomTab(true, true);
                }
                if (canScrollVertically(1)) {
                    return;
                }
                EmojiView.this.checkTabsY(1, AndroidUtilities.dp(36.0f));
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (EmojiView.this.emojiTouchedView != null && EmojiView.this.colorPickerView != null) {
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    if (EmojiView.this.colorPickerView != null && EmojiView.this.colorPickerView.isShowing() && !EmojiView.this.colorPickerView.isCompound()) {
                        EmojiView.this.colorPickerView.dismiss();
                        String skinTone = EmojiView.this.colorPickerView.getSkinTone(0);
                        String str = (String) EmojiView.this.emojiTouchedView.getTag();
                        if (EmojiView.this.emojiTouchedView.isRecent) {
                            String replace = str.replace("🏻", "").replace("🏼", "").replace("🏽", "").replace("🏾", "").replace("🏿", "");
                            if (skinTone != null) {
                                EmojiView emojiView = EmojiView.this;
                                emojiView.sendEmoji(emojiView.emojiTouchedView, EmojiView.addColorToCode(replace, skinTone));
                            } else {
                                EmojiView emojiView2 = EmojiView.this;
                                emojiView2.sendEmoji(emojiView2.emojiTouchedView, replace);
                            }
                        } else {
                            if (skinTone != null) {
                                Emoji.emojiColor.put(str, skinTone);
                                str = EmojiView.addColorToCode(str, skinTone);
                            } else {
                                Emoji.emojiColor.remove(str);
                            }
                            EmojiView.this.emojiTouchedView.setImageDrawable(Emoji.getEmojiBigDrawable(str), EmojiView.this.emojiTouchedView.isRecent);
                            EmojiView emojiView3 = EmojiView.this;
                            emojiView3.sendEmoji(emojiView3.emojiTouchedView, null);
                            try {
                                performHapticFeedback(3, 1);
                            } catch (Exception unused) {
                            }
                            Emoji.saveEmojiColors();
                        }
                    }
                    if (EmojiView.this.colorPickerView == null || !EmojiView.this.colorPickerView.isCompound()) {
                        EmojiView.this.emojiTouchedView = null;
                    }
                    EmojiView.this.emojiTouchedX = -10000.0f;
                    EmojiView.this.emojiTouchedY = -10000.0f;
                } else if (motionEvent.getAction() == 2) {
                    if (EmojiView.this.emojiTouchedX != -10000.0f) {
                        if (Math.abs(EmojiView.this.emojiTouchedX - motionEvent.getX()) > AndroidUtilities.getPixelsInCM(0.2f, true) || Math.abs(EmojiView.this.emojiTouchedY - motionEvent.getY()) > AndroidUtilities.getPixelsInCM(0.2f, false)) {
                            EmojiView.this.emojiTouchedX = -10000.0f;
                            EmojiView.this.emojiTouchedY = -10000.0f;
                        }
                    }
                    getLocationOnScreen(EmojiView.this.location);
                    float x = EmojiView.this.location[0] + motionEvent.getX();
                    EmojiView.this.colorPickerView.pickerView.getLocationOnScreen(EmojiView.this.location);
                    EmojiView.this.colorPickerView.onTouchMove((int) (x - (EmojiView.this.location[0] + AndroidUtilities.dp(3.0f))));
                }
                if (EmojiView.this.colorPickerView == null || !EmojiView.this.colorPickerView.isCompound() || EmojiView.this.colorPickerView.isShowing()) {
                    return true;
                }
            }
            EmojiView.this.emojiLastX = motionEvent.getX();
            EmojiView.this.emojiLastY = motionEvent.getY();
            return super.onTouchEvent(motionEvent);
        }

        @Override
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }

        public void updateEmojiDrawables() {
            EmojiView emojiView = EmojiView.this;
            emojiView.animatedEmojiDrawables = AnimatedEmojiSpan.update(emojiView.emojiCacheType, this, getAnimatedEmojiSpans(), (LongSparseArray<AnimatedEmojiDrawable>) EmojiView.this.animatedEmojiDrawables);
        }
    }

    public static class EmojiPack {
        public ArrayList documents = new ArrayList();
        public boolean expanded;
        public boolean featured;
        public boolean forGroup;
        public boolean free;
        public int index;
        public boolean installed;
        public TLRPC.InputStickerSet needLoadSet;
        public int resId;
        public TLRPC.StickerSet set;
    }

    private class EmojiPackButton extends FrameLayout {
        AnimatedTextView addButtonTextView;
        FrameLayout addButtonView;
        PremiumButtonView premiumButtonView;

        public EmojiPackButton(Context context) {
            super(context);
            AnimatedTextView animatedTextView = new AnimatedTextView(getContext());
            this.addButtonTextView = animatedTextView;
            animatedTextView.setAnimationProperties(0.3f, 0L, 250L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.addButtonTextView.setTextSize(AndroidUtilities.dp(14.0f));
            this.addButtonTextView.setTypeface(AndroidUtilities.bold());
            this.addButtonTextView.setTextColor(EmojiView.this.getThemedColor(Theme.key_featuredStickers_buttonText));
            this.addButtonTextView.setGravity(17);
            FrameLayout frameLayout = new FrameLayout(getContext());
            this.addButtonView = frameLayout;
            frameLayout.setBackground(Theme.AdaptiveRipple.filledRect(EmojiView.this.getThemedColor(Theme.key_featuredStickers_addButton), 8.0f));
            this.addButtonView.addView(this.addButtonTextView, LayoutHelper.createFrame(-1, -2, 17));
            addView(this.addButtonView, LayoutHelper.createFrame(-1, -1.0f));
            PremiumButtonView premiumButtonView = new PremiumButtonView(getContext(), false, EmojiView.this.resourcesProvider);
            this.premiumButtonView = premiumButtonView;
            premiumButtonView.setIcon(R.raw.unlock_icon);
            addView(this.premiumButtonView, LayoutHelper.createFrame(-1, -1.0f));
        }

        @Override
        protected void onMeasure(int i, int i2) {
            setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(11.0f));
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f) + getPaddingTop() + getPaddingBottom(), 1073741824));
        }
    }

    public static class EmojiPackExpand extends FrameLayout {
        public TextView textView;

        public EmojiPackExpand(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextSize(1, 13.0f);
            this.textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider));
            this.textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(11.0f), ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_chat_emojiPanelStickerSetName, resourcesProvider), 99)));
            this.textView.setTypeface(AndroidUtilities.bold());
            this.textView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(1.66f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(2.0f));
            addView(this.textView, LayoutHelper.createFrame(-2, -2, 17));
        }
    }

    public class EmojiPackHeader extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
        TextView addButtonView;
        FrameLayout buttonsView;
        private int currentButtonState;
        boolean divider;
        private Paint dividerPaint;
        SimpleTextView headerView;
        RLottieImageView lockView;
        TextView markView;
        private EmojiPack pack;
        PremiumButtonView premiumButtonView;
        TextView removeButtonView;
        private AnimatorSet stateAnimator;
        private TLRPC.InputStickerSet toInstall;
        private TLRPC.InputStickerSet toUninstall;

        public class AnonymousClass1 extends BaseFragment {
            AnonymousClass1() {
            }

            @Override
            public int getCurrentAccount() {
                return EmojiView.this.currentAccount;
            }

            @Override
            public View getFragmentView() {
                return EmojiView.this.bulletinContainer;
            }

            @Override
            public FrameLayout getLayoutContainer() {
                return EmojiView.this.bulletinContainer;
            }

            @Override
            public Theme.ResourcesProvider getResourceProvider() {
                return EmojiView.this.resourcesProvider;
            }
        }

        public class AnonymousClass2 extends AnimatorListenerAdapter {
            final int val$state;

            AnonymousClass2(int i) {
                r2 = i;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                EmojiPackHeader.this.premiumButtonView.setVisibility(r2 == 1 ? 0 : 8);
                EmojiPackHeader.this.addButtonView.setVisibility(r2 == 2 ? 0 : 8);
                EmojiPackHeader.this.removeButtonView.setVisibility(r2 == 3 ? 0 : 8);
            }

            @Override
            public void onAnimationStart(Animator animator) {
                EmojiPackHeader.this.premiumButtonView.setVisibility(0);
                EmojiPackHeader.this.addButtonView.setVisibility(0);
                EmojiPackHeader.this.removeButtonView.setVisibility(0);
            }
        }

        public EmojiPackHeader(Context context) {
            super(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.lockView = rLottieImageView;
            int i = R.raw.unlock_icon;
            rLottieImageView.setAnimation(i, 24, 24);
            RLottieImageView rLottieImageView2 = this.lockView;
            int i2 = Theme.key_chat_emojiPanelStickerSetName;
            rLottieImageView2.setColorFilter(EmojiView.this.getThemedColor(i2));
            addView(this.lockView, LayoutHelper.createFrameRelatively(20.0f, 20.0f, 8388611, 10.0f, 15.0f, 0.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.headerView = simpleTextView;
            simpleTextView.setTextSize(15);
            this.headerView.setTextColor(EmojiView.this.getThemedColor(i2));
            this.headerView.setTypeface(AndroidUtilities.bold());
            this.headerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    EmojiView.EmojiPackHeader.this.lambda$new$0(view);
                }
            });
            TextView textView = new TextView(context);
            this.markView = textView;
            textView.setTextSize(1, 11.0f);
            this.markView.setTextColor(EmojiView.this.getThemedColor(i2));
            this.markView.setTypeface(AndroidUtilities.bold());
            this.markView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(8.0f), Theme.multAlpha(EmojiView.this.getThemedColor(Theme.key_chat_emojiPanelIcon), 0.12f)));
            this.markView.setPadding(AndroidUtilities.dp(6.0f), AndroidUtilities.dp(1.5f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(1.5f));
            this.markView.setText(LocaleController.getString(R.string.GroupEmoji));
            this.headerView.setEllipsizeByGradient(true);
            addView(this.headerView, LayoutHelper.createFrameRelatively(-2.0f, -1.0f, 8388611, 15.0f, 15.0f, 0.0f, 0.0f));
            addView(this.markView, LayoutHelper.createFrameRelatively(-2.0f, -2.0f, 8388611, 15.0f, 10.0f, 0.0f, 0.0f));
            FrameLayout frameLayout = new FrameLayout(context);
            this.buttonsView = frameLayout;
            frameLayout.setPadding(AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f), 0);
            this.buttonsView.setClipToPadding(false);
            this.buttonsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    EmojiView.EmojiPackHeader.this.lambda$new$1(view);
                }
            });
            addView(this.buttonsView, LayoutHelper.createFrameRelatively(-2.0f, -1.0f, 8388725));
            TextView textView2 = new TextView(context);
            this.addButtonView = textView2;
            textView2.setTextSize(1, 14.0f);
            this.addButtonView.setTypeface(AndroidUtilities.bold());
            this.addButtonView.setText(LocaleController.getString(R.string.Add));
            this.addButtonView.setTextColor(EmojiView.this.getThemedColor(Theme.key_featuredStickers_buttonText));
            TextView textView3 = this.addButtonView;
            int i3 = Theme.key_featuredStickers_addButton;
            textView3.setBackground(Theme.AdaptiveRipple.createRect(EmojiView.this.getThemedColor(i3), EmojiView.this.getThemedColor(Theme.key_featuredStickers_addButtonPressed), 16.0f));
            this.addButtonView.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(14.0f), 0);
            this.addButtonView.setGravity(17);
            this.addButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    EmojiView.EmojiPackHeader.this.lambda$new$2(view);
                }
            });
            this.buttonsView.addView(this.addButtonView, LayoutHelper.createFrameRelatively(-2.0f, 26.0f, 8388661));
            TextView textView4 = new TextView(context);
            this.removeButtonView = textView4;
            textView4.setTextSize(1, 14.0f);
            this.removeButtonView.setTypeface(AndroidUtilities.bold());
            this.removeButtonView.setText(LocaleController.getString(R.string.StickersRemove));
            this.removeButtonView.setTextColor(EmojiView.this.getThemedColor(Theme.key_featuredStickers_removeButtonText));
            this.removeButtonView.setBackground(Theme.AdaptiveRipple.createRect(0, EmojiView.this.getThemedColor(i3) & 452984831, 16.0f));
            this.removeButtonView.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
            this.removeButtonView.setGravity(17);
            this.removeButtonView.setTranslationX(AndroidUtilities.dp(4.0f));
            this.removeButtonView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    EmojiView.EmojiPackHeader.this.lambda$new$3(view);
                }
            });
            this.buttonsView.addView(this.removeButtonView, LayoutHelper.createFrameRelatively(-2.0f, 26.0f, 8388661));
            PremiumButtonView premiumButtonView = new PremiumButtonView(context, AndroidUtilities.dp(16.0f), false, EmojiView.this.resourcesProvider);
            this.premiumButtonView = premiumButtonView;
            premiumButtonView.setIcon(i);
            this.premiumButtonView.setButton(LocaleController.getString(R.string.Unlock), new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    EmojiView.EmojiPackHeader.this.lambda$new$4(view);
                }
            });
            try {
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.premiumButtonView.getIconView().getLayoutParams();
                marginLayoutParams.leftMargin = AndroidUtilities.dp(1.0f);
                marginLayoutParams.topMargin = AndroidUtilities.dp(1.0f);
                int dp = AndroidUtilities.dp(20.0f);
                marginLayoutParams.height = dp;
                marginLayoutParams.width = dp;
                ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) this.premiumButtonView.getTextView().getLayoutParams();
                marginLayoutParams2.leftMargin = AndroidUtilities.dp(5.0f);
                marginLayoutParams2.topMargin = AndroidUtilities.dp(-0.5f);
                this.premiumButtonView.getChildAt(0).setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
            } catch (Exception unused) {
            }
            this.buttonsView.addView(this.premiumButtonView, LayoutHelper.createFrameRelatively(-2.0f, 26.0f, 8388661));
            setWillNotDraw(false);
        }

        private BaseFragment getFragment() {
            return EmojiView.this.fragment != null ? EmojiView.this.fragment : new BaseFragment() {
                AnonymousClass1() {
                }

                @Override
                public int getCurrentAccount() {
                    return EmojiView.this.currentAccount;
                }

                @Override
                public View getFragmentView() {
                    return EmojiView.this.bulletinContainer;
                }

                @Override
                public FrameLayout getLayoutContainer() {
                    return EmojiView.this.bulletinContainer;
                }

                @Override
                public Theme.ResourcesProvider getResourceProvider() {
                    return EmojiView.this.resourcesProvider;
                }
            };
        }

        private void install(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            EmojiPacksAlert.installSet(getFragment(), tL_messages_stickerSet, true, null, new Runnable() {
                @Override
                public final void run() {
                    EmojiView.EmojiPackHeader.this.lambda$install$7();
                }
            });
        }

        public void lambda$install$7() {
            this.pack.installed = true;
            updateState(true);
        }

        public void lambda$new$0(View view) {
            TLRPC.StickerSet stickerSet;
            EmojiPack emojiPack = this.pack;
            if (emojiPack == null || (stickerSet = emojiPack.set) == null) {
                return;
            }
            EmojiView.this.openEmojiPackAlert(stickerSet);
        }

        public void lambda$new$1(View view) {
            View view2;
            TextView textView = this.addButtonView;
            if (textView != null && textView.getVisibility() == 0 && this.addButtonView.isEnabled()) {
                view2 = this.addButtonView;
            } else {
                TextView textView2 = this.removeButtonView;
                if (textView2 != null && textView2.getVisibility() == 0 && this.removeButtonView.isEnabled()) {
                    view2 = this.removeButtonView;
                } else {
                    PremiumButtonView premiumButtonView = this.premiumButtonView;
                    if (premiumButtonView == null || premiumButtonView.getVisibility() != 0 || !this.premiumButtonView.isEnabled()) {
                        return;
                    } else {
                        view2 = this.premiumButtonView;
                    }
                }
            }
            view2.performClick();
        }

        public void lambda$new$2(View view) {
            TLRPC.StickerSet stickerSet;
            View view2;
            Integer num;
            int childAdapterPosition;
            int i;
            EmojiPack emojiPack = this.pack;
            if (emojiPack == null || (stickerSet = emojiPack.set) == null) {
                return;
            }
            emojiPack.installed = true;
            if (!EmojiView.this.installedEmojiSets.contains(Long.valueOf(stickerSet.id))) {
                EmojiView.this.installedEmojiSets.add(Long.valueOf(this.pack.set.id));
            }
            updateState(true);
            int i2 = 0;
            while (true) {
                if (i2 >= EmojiView.this.emojiGridView.getChildCount()) {
                    view2 = null;
                    num = null;
                    break;
                } else {
                    if ((EmojiView.this.emojiGridView.getChildAt(i2) instanceof EmojiPackExpand) && (childAdapterPosition = EmojiView.this.emojiGridView.getChildAdapterPosition((view2 = EmojiView.this.emojiGridView.getChildAt(i2)))) >= 0 && (i = EmojiView.this.emojiAdapter.positionToExpand.get(childAdapterPosition)) >= 0 && i < EmojiView.this.emojipacksProcessed.size() && EmojiView.this.emojipacksProcessed.get(i) != null && this.pack != null && ((EmojiPack) EmojiView.this.emojipacksProcessed.get(i)).set.id == this.pack.set.id) {
                        num = Integer.valueOf(childAdapterPosition);
                        break;
                    }
                    i2++;
                }
            }
            if (num != null) {
                EmojiView.this.emojiAdapter.expand(num.intValue(), view2);
            }
            if (this.toInstall != null) {
                return;
            }
            TLRPC.TL_inputStickerSetID tL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
            TLRPC.StickerSet stickerSet2 = this.pack.set;
            tL_inputStickerSetID.id = stickerSet2.id;
            tL_inputStickerSetID.access_hash = stickerSet2.access_hash;
            TLRPC.TL_messages_stickerSet stickerSet3 = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSet(tL_inputStickerSetID, true);
            if (stickerSet3 != null && stickerSet3.set != null) {
                install(stickerSet3);
                return;
            }
            NotificationCenter.getInstance(EmojiView.this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
            MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
            this.toInstall = tL_inputStickerSetID;
            mediaDataController.getStickerSet(tL_inputStickerSetID, false);
        }

        public void lambda$new$3(View view) {
            TLRPC.StickerSet stickerSet;
            EmojiPack emojiPack = this.pack;
            if (emojiPack == null || (stickerSet = emojiPack.set) == null) {
                return;
            }
            emojiPack.installed = false;
            EmojiView.this.installedEmojiSets.remove(Long.valueOf(stickerSet.id));
            updateState(true);
            if (EmojiView.this.emojiTabs != null) {
                EmojiView.this.emojiTabs.updateEmojiPacks(EmojiView.this.getEmojipacks());
            }
            EmojiView.this.updateEmojiTabsPosition();
            if (this.toUninstall != null) {
                return;
            }
            TLRPC.TL_inputStickerSetID tL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
            TLRPC.StickerSet stickerSet2 = this.pack.set;
            tL_inputStickerSetID.id = stickerSet2.id;
            tL_inputStickerSetID.access_hash = stickerSet2.access_hash;
            TLRPC.TL_messages_stickerSet stickerSet3 = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSet(tL_inputStickerSetID, true);
            if (stickerSet3 != null && stickerSet3.set != null) {
                uninstall(stickerSet3);
                return;
            }
            NotificationCenter.getInstance(EmojiView.this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
            MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
            this.toUninstall = tL_inputStickerSetID;
            mediaDataController.getStickerSet(tL_inputStickerSetID, false);
        }

        public void lambda$new$4(View view) {
            EmojiView.this.openPremiumAnimatedEmojiFeature();
        }

        public void lambda$setStickerSet$5(View view) {
            EmojiView.this.openPremiumAnimatedEmojiFeature();
        }

        public void lambda$setStickerSet$6(View view) {
            EmojiView.this.openPremiumAnimatedEmojiFeature();
        }

        public void lambda$uninstall$8(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            this.pack.installed = true;
            if (!EmojiView.this.installedEmojiSets.contains(Long.valueOf(tL_messages_stickerSet.set.id))) {
                EmojiView.this.installedEmojiSets.add(Long.valueOf(tL_messages_stickerSet.set.id));
            }
            updateState(true);
        }

        private void uninstall(final TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            EmojiPacksAlert.uninstallSet(getFragment(), tL_messages_stickerSet, true, new Runnable() {
                @Override
                public final void run() {
                    EmojiView.EmojiPackHeader.this.lambda$uninstall$8(tL_messages_stickerSet);
                }
            }, false);
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            TLRPC.TL_messages_stickerSet stickerSetById;
            TLRPC.TL_messages_stickerSet stickerSetById2;
            if (i == NotificationCenter.groupStickersDidLoad) {
                if (this.toInstall != null && (stickerSetById2 = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSetById(this.toInstall.id)) != null && stickerSetById2.set != null) {
                    install(stickerSetById2);
                    this.toInstall = null;
                }
                if (this.toUninstall == null || (stickerSetById = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSetById(this.toUninstall.id)) == null || stickerSetById.set == null) {
                    return;
                }
                uninstall(stickerSetById);
                this.toUninstall = null;
            }
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            NotificationCenter.getInstance(EmojiView.this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (this.divider) {
                if (this.dividerPaint == null) {
                    Paint paint = new Paint(1);
                    this.dividerPaint = paint;
                    paint.setStrokeWidth(1.0f);
                    this.dividerPaint.setColor(EmojiView.this.getThemedColor(Theme.key_divider));
                }
                canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), 1.0f, this.dividerPaint);
            }
            super.onDraw(canvas);
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            int width = this.buttonsView.getWidth() + AndroidUtilities.dp(11.0f) + (this.markView.getVisibility() == 0 ? this.markView.getMeasuredWidth() : 0);
            this.headerView.setRightPadding(width);
            if (this.markView.getVisibility() == 0) {
                this.markView.setTranslationX(this.headerView.getTextWidth() + AndroidUtilities.dp(4.0f));
                float maxTextWidth = (this.headerView.getMaxTextWidth() - width) + AndroidUtilities.dp(4.0f);
                if (this.markView.getTranslationX() > maxTextWidth) {
                    this.markView.setTranslationX(maxTextWidth);
                }
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            ((ViewGroup.MarginLayoutParams) this.headerView.getLayoutParams()).topMargin = AndroidUtilities.dp(this.currentButtonState == 0 ? 10.0f : 15.0f);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.currentButtonState == 0 ? 32.0f : 42.0f), 1073741824));
        }

        public void setStickerSet(EmojiPack emojiPack, boolean z) {
            PremiumButtonView premiumButtonView;
            String string;
            View.OnClickListener onClickListener;
            if (emojiPack == null) {
                return;
            }
            this.pack = emojiPack;
            this.divider = z;
            this.headerView.setText(emojiPack.set.title);
            this.markView.setVisibility(emojiPack.forGroup ? 0 : 8);
            if (!emojiPack.installed || emojiPack.set.official) {
                premiumButtonView = this.premiumButtonView;
                string = LocaleController.getString(R.string.Unlock);
                onClickListener = new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        EmojiView.EmojiPackHeader.this.lambda$setStickerSet$6(view);
                    }
                };
            } else {
                premiumButtonView = this.premiumButtonView;
                string = LocaleController.getString(R.string.Restore);
                onClickListener = new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        EmojiView.EmojiPackHeader.this.lambda$setStickerSet$5(view);
                    }
                };
            }
            premiumButtonView.setButton(string, onClickListener);
            updateState(false);
        }

        public void updateState(int i, boolean z) {
            int i2;
            float f;
            int i3;
            float f2;
            if ((i == 0) != (this.currentButtonState == 0)) {
                requestLayout();
            }
            this.currentButtonState = i;
            AnimatorSet animatorSet = this.stateAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.stateAnimator = null;
            }
            this.premiumButtonView.setEnabled(i == 1);
            this.addButtonView.setEnabled(i == 2);
            this.removeButtonView.setEnabled(i == 3);
            if (!z) {
                this.lockView.setAlpha(i == 1 ? 1.0f : 0.0f);
                this.lockView.setTranslationX(i == 1 ? 0.0f : -AndroidUtilities.dp(16.0f));
                this.headerView.setTranslationX(i == 1 ? AndroidUtilities.dp(16.0f) : 0.0f);
                this.premiumButtonView.setAlpha(i == 1 ? 1.0f : 0.0f);
                this.premiumButtonView.setScaleX(i == 1 ? 1.0f : 0.6f);
                this.premiumButtonView.setScaleY(i == 1 ? 1.0f : 0.6f);
                this.premiumButtonView.setVisibility(i == 1 ? 0 : 8);
                this.addButtonView.setAlpha(i == 2 ? 1.0f : 0.0f);
                this.addButtonView.setScaleX(i == 2 ? 1.0f : 0.6f);
                this.addButtonView.setScaleY(i == 2 ? 1.0f : 0.6f);
                this.addButtonView.setVisibility(i == 2 ? 0 : 8);
                this.removeButtonView.setAlpha(i == 3 ? 1.0f : 0.0f);
                this.removeButtonView.setScaleX(i == 3 ? 1.0f : 0.6f);
                this.removeButtonView.setScaleY(i == 3 ? 1.0f : 0.6f);
                this.removeButtonView.setVisibility(i == 3 ? 0 : 8);
                return;
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.stateAnimator = animatorSet2;
            RLottieImageView rLottieImageView = this.lockView;
            Property property = FrameLayout.TRANSLATION_X;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(rLottieImageView, (Property<RLottieImageView, Float>) property, i == 1 ? 0.0f : -AndroidUtilities.dp(16.0f));
            RLottieImageView rLottieImageView2 = this.lockView;
            Property property2 = FrameLayout.ALPHA;
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(rLottieImageView2, (Property<RLottieImageView, Float>) property2, i == 1 ? 1.0f : 0.0f);
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(this.headerView, (Property<SimpleTextView, Float>) property, i == 1 ? AndroidUtilities.dp(16.0f) : 0.0f);
            ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(this.premiumButtonView, (Property<PremiumButtonView, Float>) property2, i == 1 ? 1.0f : 0.0f);
            PremiumButtonView premiumButtonView = this.premiumButtonView;
            Property property3 = FrameLayout.SCALE_X;
            ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(premiumButtonView, (Property<PremiumButtonView, Float>) property3, i == 1 ? 1.0f : 0.6f);
            PremiumButtonView premiumButtonView2 = this.premiumButtonView;
            Property property4 = FrameLayout.SCALE_Y;
            ObjectAnimator ofFloat6 = ObjectAnimator.ofFloat(premiumButtonView2, (Property<PremiumButtonView, Float>) property4, i == 1 ? 1.0f : 0.6f);
            ObjectAnimator ofFloat7 = ObjectAnimator.ofFloat(this.addButtonView, (Property<TextView, Float>) property2, i == 2 ? 1.0f : 0.0f);
            ObjectAnimator ofFloat8 = ObjectAnimator.ofFloat(this.addButtonView, (Property<TextView, Float>) property3, i == 2 ? 1.0f : 0.6f);
            TextView textView = this.addButtonView;
            if (i == 2) {
                i2 = 1;
                f = 1.0f;
            } else {
                i2 = 1;
                f = 0.6f;
            }
            float[] fArr = new float[i2];
            fArr[0] = f;
            ObjectAnimator ofFloat9 = ObjectAnimator.ofFloat(textView, (Property<TextView, Float>) property4, fArr);
            TextView textView2 = this.removeButtonView;
            if (i == 3) {
                i3 = 1;
                f2 = 1.0f;
            } else {
                i3 = 1;
                f2 = 0.0f;
            }
            float[] fArr2 = new float[i3];
            fArr2[0] = f2;
            ObjectAnimator ofFloat10 = ObjectAnimator.ofFloat(textView2, (Property<TextView, Float>) property2, fArr2);
            TextView textView3 = this.removeButtonView;
            float[] fArr3 = new float[i3];
            fArr3[0] = i == 3 ? 1.0f : 0.6f;
            ObjectAnimator ofFloat11 = ObjectAnimator.ofFloat(textView3, (Property<TextView, Float>) property3, fArr3);
            TextView textView4 = this.removeButtonView;
            float[] fArr4 = new float[i3];
            fArr4[0] = i == 3 ? 1.0f : 0.6f;
            ObjectAnimator ofFloat12 = ObjectAnimator.ofFloat(textView4, (Property<TextView, Float>) property4, fArr4);
            Animator[] animatorArr = new Animator[12];
            animatorArr[0] = ofFloat;
            animatorArr[i3] = ofFloat2;
            animatorArr[2] = ofFloat3;
            animatorArr[3] = ofFloat4;
            animatorArr[4] = ofFloat5;
            animatorArr[5] = ofFloat6;
            animatorArr[6] = ofFloat7;
            animatorArr[7] = ofFloat8;
            animatorArr[8] = ofFloat9;
            animatorArr[9] = ofFloat10;
            animatorArr[10] = ofFloat11;
            animatorArr[11] = ofFloat12;
            animatorSet2.playTogether(animatorArr);
            this.stateAnimator.addListener(new AnimatorListenerAdapter() {
                final int val$state;

                AnonymousClass2(int i4) {
                    r2 = i4;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    EmojiPackHeader.this.premiumButtonView.setVisibility(r2 == 1 ? 0 : 8);
                    EmojiPackHeader.this.addButtonView.setVisibility(r2 == 2 ? 0 : 8);
                    EmojiPackHeader.this.removeButtonView.setVisibility(r2 == 3 ? 0 : 8);
                }

                @Override
                public void onAnimationStart(Animator animator) {
                    EmojiPackHeader.this.premiumButtonView.setVisibility(0);
                    EmojiPackHeader.this.addButtonView.setVisibility(0);
                    EmojiPackHeader.this.removeButtonView.setVisibility(0);
                }
            });
            this.stateAnimator.setDuration(250L);
            this.stateAnimator.setInterpolator(new OvershootInterpolator(1.02f));
            this.stateAnimator.start();
        }

        public void updateState(boolean z) {
            EmojiPack emojiPack = this.pack;
            if (emojiPack == null) {
                return;
            }
            updateState((this.pack.free || UserConfig.getInstance(EmojiView.this.currentAccount).isPremium() || EmojiView.this.allowEmojisForNonPremium) ? this.pack.featured ? emojiPack.installed || EmojiView.this.installedEmojiSets.contains(Long.valueOf(emojiPack.set.id)) ? 3 : 2 : 0 : 1, z);
        }
    }

    public class EmojiPagesAdapter extends PagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
        private EmojiPagesAdapter() {
        }

        EmojiPagesAdapter(EmojiView emojiView, AnonymousClass1 anonymousClass1) {
            this();
        }

        @Override
        public boolean canScrollToTab(int i) {
            if (i == 1 || i == 2) {
                EmojiView emojiView = EmojiView.this;
                if (emojiView.stickersBanned) {
                    emojiView.showStickerBanHint(true, false, i == 1);
                    return false;
                }
            }
            if (i == 0) {
                EmojiView emojiView2 = EmojiView.this;
                if (emojiView2.emojiBanned) {
                    emojiView2.showStickerBanHint(true, true, false);
                    return false;
                }
            }
            return true;
        }

        @Override
        public void customOnDraw(Canvas canvas, View view, int i) {
        }

        @Override
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        @Override
        public int getCount() {
            return EmojiView.this.currentTabs.size();
        }

        @Override
        public Drawable getPageIconDrawable(int i) {
            return null;
        }

        @Override
        public CharSequence getPageTitle(int i) {
            int i2;
            if (i == 0) {
                i2 = R.string.Emoji;
            } else if (i == 1) {
                i2 = R.string.AccDescrGIFs;
            } else {
                if (i != 2) {
                    return null;
                }
                i2 = R.string.AccDescrStickers;
            }
            return LocaleController.getString(i2);
        }

        @Override
        public int getTabPadding(int i) {
            return AndroidUtilities.dp(i != 0 ? 12.0f : 18.0f);
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int i) {
            View view = ((Tab) EmojiView.this.currentTabs.get(i)).view;
            viewGroup.addView(view);
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }
    }

    public class EmojiSearchAdapter extends RecyclerListView.SelectionAdapter {
        private String lastSearchAlias;
        private String lastSearchEmojiString;
        private final ArrayList packs;
        private final ArrayList result;
        private Runnable searchRunnable;
        private boolean searchWas;

        class AnonymousClass1 extends FrameLayout {
            AnonymousClass1(Context context) {
                super(context);
            }

            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec((((View) EmojiView.this.getParent()) != null ? (int) (r3.getMeasuredHeight() - EmojiView.this.getY()) : AndroidUtilities.dp(120.0f)) - EmojiView.this.searchFieldHeight, 1073741824));
            }
        }

        public class AnonymousClass2 implements View.OnClickListener {

            public class AnonymousClass1 implements View.OnClickListener {
                final BottomSheet.Builder val$builder;
                final boolean[] val$loadingUrl;

                AnonymousClass1(boolean[] zArr, BottomSheet.Builder builder) {
                    this.val$loadingUrl = zArr;
                    this.val$builder = builder;
                }

                public void lambda$onClick$0(AlertDialog[] alertDialogArr, TLObject tLObject, BottomSheet.Builder builder) {
                    try {
                        alertDialogArr[0].dismiss();
                    } catch (Throwable unused) {
                    }
                    alertDialogArr[0] = null;
                    if (tLObject instanceof TLRPC.TL_emojiURL) {
                        Browser.openUrl(EmojiView.this.getContext(), ((TLRPC.TL_emojiURL) tLObject).url);
                        builder.getDismissRunnable().run();
                    }
                }

                public void lambda$onClick$1(final AlertDialog[] alertDialogArr, final BottomSheet.Builder builder, final TLObject tLObject, TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$0(alertDialogArr, tLObject, builder);
                        }
                    });
                }

                public void lambda$onClick$2(int i, DialogInterface dialogInterface) {
                    ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(i, true);
                }

                public void lambda$onClick$3(AlertDialog[] alertDialogArr, final int i) {
                    AlertDialog alertDialog = alertDialogArr[0];
                    if (alertDialog == null) {
                        return;
                    }
                    alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public final void onCancel(DialogInterface dialogInterface) {
                            EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$2(i, dialogInterface);
                        }
                    });
                    alertDialogArr[0].show();
                }

                @Override
                public void onClick(View view) {
                    boolean[] zArr = this.val$loadingUrl;
                    if (zArr[0]) {
                        return;
                    }
                    zArr[0] = true;
                    final AlertDialog[] alertDialogArr = {new AlertDialog(EmojiView.this.getContext(), 3)};
                    TLRPC.TL_messages_getEmojiURL tL_messages_getEmojiURL = new TLRPC.TL_messages_getEmojiURL();
                    tL_messages_getEmojiURL.lang_code = EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiView.this.lastSearchKeyboardLanguage[0];
                    ConnectionsManager connectionsManager = ConnectionsManager.getInstance(EmojiView.this.currentAccount);
                    final BottomSheet.Builder builder = this.val$builder;
                    final int sendRequest = connectionsManager.sendRequest(tL_messages_getEmojiURL, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$1(alertDialogArr, builder, tLObject, tL_error);
                        }
                    });
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public final void run() {
                            EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$3(alertDialogArr, sendRequest);
                        }
                    }, 1000L);
                }
            }

            AnonymousClass2() {
            }

            @Override
            public void onClick(View view) {
                boolean[] zArr = new boolean[1];
                BottomSheet.Builder builder = new BottomSheet.Builder(EmojiView.this.getContext());
                LinearLayout linearLayout = new LinearLayout(EmojiView.this.getContext());
                linearLayout.setOrientation(1);
                linearLayout.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                ImageView imageView = new ImageView(EmojiView.this.getContext());
                imageView.setImageResource(R.drawable.smiles_info);
                linearLayout.addView(imageView, LayoutHelper.createLinear(-2, -2, 49, 0, 15, 0, 0));
                TextView textView = new TextView(EmojiView.this.getContext());
                textView.setText(LocaleController.getString(R.string.EmojiSuggestions));
                textView.setTextSize(1, 15.0f);
                textView.setTextColor(EmojiView.this.getThemedColor(Theme.key_dialogTextBlue2));
                textView.setGravity(LocaleController.isRTL ? 5 : 3);
                textView.setTypeface(AndroidUtilities.bold());
                linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 51, 0, 24, 0, 0));
                TextView textView2 = new TextView(EmojiView.this.getContext());
                textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.EmojiSuggestionsInfo)));
                textView2.setTextSize(1, 15.0f);
                textView2.setTextColor(EmojiView.this.getThemedColor(Theme.key_dialogTextBlack));
                textView2.setGravity(LocaleController.isRTL ? 5 : 3);
                linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 51, 0, 11, 0, 0));
                TextView textView3 = new TextView(EmojiView.this.getContext());
                textView3.setText(LocaleController.formatString("EmojiSuggestionsUrl", R.string.EmojiSuggestionsUrl, EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiView.this.lastSearchKeyboardLanguage));
                textView3.setTextSize(1, 15.0f);
                textView3.setTextColor(EmojiView.this.getThemedColor(Theme.key_dialogTextLink));
                textView3.setGravity(LocaleController.isRTL ? 5 : 3);
                linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 51, 0, 18, 0, 16));
                textView3.setOnClickListener(new AnonymousClass1(zArr, builder));
                builder.setCustomView(linearLayout);
                builder.show();
            }
        }

        private EmojiSearchAdapter() {
            this.result = new ArrayList();
            this.packs = new ArrayList();
        }

        EmojiSearchAdapter(EmojiView emojiView, AnonymousClass1 anonymousClass1) {
            this();
        }

        public void lambda$search$0(String str, ArrayList arrayList, Runnable runnable, ArrayList arrayList2, String str2) {
            if (str.equals(this.lastSearchEmojiString)) {
                this.lastSearchAlias = str2;
                arrayList.addAll(arrayList2);
                runnable.run();
            }
        }

        public void lambda$search$1(final String str, final ArrayList arrayList, final Runnable runnable) {
            MediaDataController.getInstance(EmojiView.this.currentAccount).getEmojiSuggestions(EmojiView.this.lastSearchKeyboardLanguage, this.lastSearchEmojiString, false, new MediaDataController.KeywordResultCallback() {
                @Override
                public final void run(ArrayList arrayList2, String str2) {
                    EmojiView.EmojiSearchAdapter.this.lambda$search$0(str, arrayList, runnable, arrayList2, str2);
                }
            }, null, SharedConfig.suggestAnimatedEmoji || UserConfig.getInstance(EmojiView.this.currentAccount).isPremium(), false, true, 25);
        }

        public void lambda$search$2(String str, ArrayList arrayList, Runnable runnable, ArrayList arrayList2) {
            if (str.equals(this.lastSearchEmojiString)) {
                AnimatedEmojiDrawable.getDocumentFetcher(EmojiView.this.currentAccount).putDocuments(arrayList2);
                Iterator it = arrayList2.iterator();
                while (it.hasNext()) {
                    TLRPC.Document document = (TLRPC.Document) it.next();
                    MediaDataController.KeywordResult keywordResult = new MediaDataController.KeywordResult();
                    keywordResult.emoji = "animated_" + document.id;
                    keywordResult.keyword = null;
                    arrayList.add(keywordResult);
                }
                runnable.run();
            }
        }

        public void lambda$search$3(String[] strArr, final String str, final ArrayList arrayList, final Runnable runnable) {
            if (ConnectionsManager.getInstance(EmojiView.this.currentAccount).getConnectionState() != 3) {
                runnable.run();
            } else {
                MediaDataController.getInstance(EmojiView.this.currentAccount).searchStickers(true, (strArr == null || strArr.length == 0) ? "" : strArr[0], str, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        EmojiView.EmojiSearchAdapter.this.lambda$search$2(str, arrayList, runnable, (ArrayList) obj);
                    }
                });
            }
        }

        public void lambda$search$4(String str, ArrayList arrayList, Runnable runnable) {
            TLRPC.StickerSet stickerSet;
            ArrayList<TLRPC.Document> arrayList2;
            TLRPC.StickerSet stickerSet2;
            ArrayList<TLRPC.Document> arrayList3;
            if (SharedConfig.suggestAnimatedEmoji || UserConfig.getInstance(EmojiView.this.currentAccount).isPremium()) {
                String translitSafe = AndroidUtilities.translitSafe((str + "").toLowerCase());
                ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSets(5);
                HashSet hashSet = new HashSet();
                if (stickerSets != null) {
                    for (int i = 0; i < stickerSets.size(); i++) {
                        TLRPC.TL_messages_stickerSet tL_messages_stickerSet = stickerSets.get(i);
                        if (tL_messages_stickerSet != null && (stickerSet2 = tL_messages_stickerSet.set) != null && stickerSet2.title != null && (arrayList3 = tL_messages_stickerSet.documents) != null && !arrayList3.isEmpty() && !hashSet.contains(Long.valueOf(tL_messages_stickerSet.set.id))) {
                            String translitSafe2 = AndroidUtilities.translitSafe(tL_messages_stickerSet.set.title.toLowerCase());
                            if (!translitSafe2.startsWith(translitSafe)) {
                                if (!translitSafe2.contains(" " + translitSafe)) {
                                }
                            }
                            arrayList.add(new SelectAnimatedEmojiDialog.SetTitleDocument(tL_messages_stickerSet.set.title));
                            arrayList.addAll(tL_messages_stickerSet.documents);
                            hashSet.add(Long.valueOf(tL_messages_stickerSet.set.id));
                        }
                    }
                }
                ArrayList<TLRPC.StickerSetCovered> featuredEmojiSets = MediaDataController.getInstance(EmojiView.this.currentAccount).getFeaturedEmojiSets();
                if (featuredEmojiSets != null) {
                    for (int i2 = 0; i2 < featuredEmojiSets.size(); i2++) {
                        TLRPC.StickerSetCovered stickerSetCovered = featuredEmojiSets.get(i2);
                        if (stickerSetCovered != null && (stickerSet = stickerSetCovered.set) != null && stickerSet.title != null && !hashSet.contains(Long.valueOf(stickerSet.id))) {
                            String translitSafe3 = AndroidUtilities.translitSafe(stickerSetCovered.set.title.toLowerCase());
                            if (!translitSafe3.startsWith(translitSafe)) {
                                if (!translitSafe3.contains(" " + translitSafe)) {
                                }
                            }
                            if (stickerSetCovered instanceof TLRPC.TL_stickerSetFullCovered) {
                                arrayList2 = ((TLRPC.TL_stickerSetFullCovered) stickerSetCovered).documents;
                            } else if (stickerSetCovered instanceof TLRPC.TL_stickerSetNoCovered) {
                                TLRPC.TL_messages_stickerSet stickerSet3 = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSet(MediaDataController.getInputStickerSet(stickerSetCovered.set), Integer.valueOf(stickerSetCovered.set.hash), true);
                                arrayList2 = stickerSet3 != null ? stickerSet3.documents : null;
                            } else {
                                arrayList2 = stickerSetCovered.covers;
                            }
                            if (arrayList2 != null && !arrayList2.isEmpty()) {
                                arrayList.add(new SelectAnimatedEmojiDialog.SetTitleDocument(stickerSetCovered.set.title));
                                arrayList.addAll(arrayList2);
                                hashSet.add(Long.valueOf(stickerSetCovered.set.id));
                            }
                        }
                    }
                }
            }
            runnable.run();
        }

        public void lambda$search$5(String str, ArrayList arrayList, ArrayList arrayList2, Runnable runnable) {
            if (str.equals(this.lastSearchEmojiString)) {
                EmojiView.this.emojiSearchField.showProgress(false);
                this.searchWas = true;
                if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter) {
                    EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiSearchAdapter);
                }
                this.result.clear();
                this.result.addAll(arrayList);
                this.packs.clear();
                this.packs.addAll(arrayList2);
                notifyDataSetChanged();
            }
        }

        public void lambda$search$6(final String str) {
            final String[] currentKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
            if (!Arrays.equals(EmojiView.this.lastSearchKeyboardLanguage, currentKeyboardLanguage)) {
                MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(currentKeyboardLanguage);
            }
            EmojiView.this.lastSearchKeyboardLanguage = currentKeyboardLanguage;
            final ArrayList arrayList = new ArrayList();
            final ArrayList arrayList2 = new ArrayList();
            Utilities.doCallbacks(new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    EmojiView.EmojiSearchAdapter.this.lambda$search$1(str, arrayList, (Runnable) obj);
                }
            }, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    EmojiView.EmojiSearchAdapter.this.lambda$search$3(currentKeyboardLanguage, str, arrayList, (Runnable) obj);
                }
            }, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    EmojiView.EmojiSearchAdapter.this.lambda$search$4(str, arrayList2, (Runnable) obj);
                }
            }, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    EmojiView.EmojiSearchAdapter.this.lambda$search$5(str, arrayList, arrayList2, (Runnable) obj);
                }
            });
        }

        public static void lambda$search$7(LinkedHashSet linkedHashSet, Runnable runnable, TLRPC.TL_emojiList tL_emojiList) {
            if (tL_emojiList != null) {
                linkedHashSet.addAll(tL_emojiList.document_id);
            }
            runnable.run();
        }

        public void lambda$search$8() {
            final LinkedHashSet linkedHashSet = new LinkedHashSet();
            final String str = this.lastSearchEmojiString;
            final Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    EmojiView.EmojiSearchAdapter.this.lambda$search$6(str);
                }
            };
            if (Emoji.fullyConsistsOfEmojis(str)) {
                StickerCategoriesListView.search.fetch(UserConfig.selectedAccount, str, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        EmojiView.EmojiSearchAdapter.lambda$search$7(linkedHashSet, runnable, (TLRPC.TL_emojiList) obj);
                    }
                });
            } else {
                runnable.run();
            }
        }

        @Override
        public int getItemCount() {
            int size;
            if (this.result.isEmpty() && this.packs.isEmpty() && !this.searchWas) {
                size = EmojiView.this.getRecentEmoji().size();
            } else {
                if (this.result.isEmpty() && this.packs.isEmpty()) {
                    return 2;
                }
                size = this.result.size() + this.packs.size();
            }
            return size + 1;
        }

        @Override
        public int getItemViewType(int i) {
            int size;
            if (i == 0) {
                return 1;
            }
            if (i == 1 && this.searchWas && this.result.isEmpty() && this.packs.isEmpty()) {
                return 2;
            }
            return (this.packs.isEmpty() || (size = i - (this.result.size() + 1)) < 0 || size >= this.packs.size() || !(this.packs.get(size) instanceof SelectAnimatedEmojiDialog.SetTitleDocument)) ? 0 : 3;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r10, int r11) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.EmojiSearchAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            ViewGroup.LayoutParams layoutParams;
            View view2;
            if (i != 0) {
                if (i == 1) {
                    View view3 = new View(EmojiView.this.getContext());
                    layoutParams = new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight);
                    view2 = view3;
                } else if (i != 3) {
                    AnonymousClass1 anonymousClass1 = new FrameLayout(EmojiView.this.getContext()) {
                        AnonymousClass1(Context context) {
                            super(context);
                        }

                        @Override
                        protected void onMeasure(int i2, int i22) {
                            super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec((((View) EmojiView.this.getParent()) != null ? (int) (r3.getMeasuredHeight() - EmojiView.this.getY()) : AndroidUtilities.dp(120.0f)) - EmojiView.this.searchFieldHeight, 1073741824));
                        }
                    };
                    TextView textView = new TextView(EmojiView.this.getContext());
                    textView.setText(LocaleController.getString(R.string.NoEmojiFound));
                    textView.setTextSize(1, 16.0f);
                    EmojiView emojiView = EmojiView.this;
                    int i2 = Theme.key_chat_emojiPanelEmptyText;
                    textView.setTextColor(emojiView.getThemedColor(i2));
                    anonymousClass1.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 10.0f, 0.0f, 0.0f));
                    ImageView imageView = new ImageView(EmojiView.this.getContext());
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setImageResource(R.drawable.msg_emoji_question);
                    imageView.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor(i2), PorterDuff.Mode.MULTIPLY));
                    anonymousClass1.addView(imageView, LayoutHelper.createFrame(48, 48, 85));
                    imageView.setOnClickListener(new View.OnClickListener() {

                        public class AnonymousClass1 implements View.OnClickListener {
                            final BottomSheet.Builder val$builder;
                            final boolean[] val$loadingUrl;

                            AnonymousClass1(boolean[] zArr, BottomSheet.Builder builder) {
                                this.val$loadingUrl = zArr;
                                this.val$builder = builder;
                            }

                            public void lambda$onClick$0(AlertDialog[] alertDialogArr, TLObject tLObject, BottomSheet.Builder builder) {
                                try {
                                    alertDialogArr[0].dismiss();
                                } catch (Throwable unused) {
                                }
                                alertDialogArr[0] = null;
                                if (tLObject instanceof TLRPC.TL_emojiURL) {
                                    Browser.openUrl(EmojiView.this.getContext(), ((TLRPC.TL_emojiURL) tLObject).url);
                                    builder.getDismissRunnable().run();
                                }
                            }

                            public void lambda$onClick$1(final AlertDialog[] alertDialogArr, final BottomSheet.Builder builder, final TLObject tLObject, TLRPC.TL_error tL_error) {
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    @Override
                                    public final void run() {
                                        EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$0(alertDialogArr, tLObject, builder);
                                    }
                                });
                            }

                            public void lambda$onClick$2(int i, DialogInterface dialogInterface) {
                                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(i, true);
                            }

                            public void lambda$onClick$3(AlertDialog[] alertDialogArr, final int i) {
                                AlertDialog alertDialog = alertDialogArr[0];
                                if (alertDialog == null) {
                                    return;
                                }
                                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public final void onCancel(DialogInterface dialogInterface) {
                                        EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$2(i, dialogInterface);
                                    }
                                });
                                alertDialogArr[0].show();
                            }

                            @Override
                            public void onClick(View view) {
                                boolean[] zArr = this.val$loadingUrl;
                                if (zArr[0]) {
                                    return;
                                }
                                zArr[0] = true;
                                final AlertDialog[] alertDialogArr = {new AlertDialog(EmojiView.this.getContext(), 3)};
                                TLRPC.TL_messages_getEmojiURL tL_messages_getEmojiURL = new TLRPC.TL_messages_getEmojiURL();
                                tL_messages_getEmojiURL.lang_code = EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiView.this.lastSearchKeyboardLanguage[0];
                                ConnectionsManager connectionsManager = ConnectionsManager.getInstance(EmojiView.this.currentAccount);
                                final BottomSheet.Builder builder = this.val$builder;
                                final int sendRequest = connectionsManager.sendRequest(tL_messages_getEmojiURL, new RequestDelegate() {
                                    @Override
                                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                        EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$1(alertDialogArr, builder, tLObject, tL_error);
                                    }
                                });
                                AndroidUtilities.runOnUIThread(new Runnable() {
                                    @Override
                                    public final void run() {
                                        EmojiView.EmojiSearchAdapter.AnonymousClass2.AnonymousClass1.this.lambda$onClick$3(alertDialogArr, sendRequest);
                                    }
                                }, 1000L);
                            }
                        }

                        AnonymousClass2() {
                        }

                        @Override
                        public void onClick(View view4) {
                            boolean[] zArr = new boolean[1];
                            BottomSheet.Builder builder = new BottomSheet.Builder(EmojiView.this.getContext());
                            LinearLayout linearLayout = new LinearLayout(EmojiView.this.getContext());
                            linearLayout.setOrientation(1);
                            linearLayout.setPadding(AndroidUtilities.dp(21.0f), 0, AndroidUtilities.dp(21.0f), 0);
                            ImageView imageView2 = new ImageView(EmojiView.this.getContext());
                            imageView2.setImageResource(R.drawable.smiles_info);
                            linearLayout.addView(imageView2, LayoutHelper.createLinear(-2, -2, 49, 0, 15, 0, 0));
                            TextView textView2 = new TextView(EmojiView.this.getContext());
                            textView2.setText(LocaleController.getString(R.string.EmojiSuggestions));
                            textView2.setTextSize(1, 15.0f);
                            textView2.setTextColor(EmojiView.this.getThemedColor(Theme.key_dialogTextBlue2));
                            textView2.setGravity(LocaleController.isRTL ? 5 : 3);
                            textView2.setTypeface(AndroidUtilities.bold());
                            linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 51, 0, 24, 0, 0));
                            TextView textView22 = new TextView(EmojiView.this.getContext());
                            textView22.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.EmojiSuggestionsInfo)));
                            textView22.setTextSize(1, 15.0f);
                            textView22.setTextColor(EmojiView.this.getThemedColor(Theme.key_dialogTextBlack));
                            textView22.setGravity(LocaleController.isRTL ? 5 : 3);
                            linearLayout.addView(textView22, LayoutHelper.createLinear(-2, -2, 51, 0, 11, 0, 0));
                            TextView textView3 = new TextView(EmojiView.this.getContext());
                            textView3.setText(LocaleController.formatString("EmojiSuggestionsUrl", R.string.EmojiSuggestionsUrl, EmojiSearchAdapter.this.lastSearchAlias != null ? EmojiSearchAdapter.this.lastSearchAlias : EmojiView.this.lastSearchKeyboardLanguage));
                            textView3.setTextSize(1, 15.0f);
                            textView3.setTextColor(EmojiView.this.getThemedColor(Theme.key_dialogTextLink));
                            textView3.setGravity(LocaleController.isRTL ? 5 : 3);
                            linearLayout.addView(textView3, LayoutHelper.createLinear(-2, -2, 51, 0, 18, 0, 16));
                            textView3.setOnClickListener(new AnonymousClass1(zArr, builder));
                            builder.setCustomView(linearLayout);
                            builder.show();
                        }
                    });
                    layoutParams = new RecyclerView.LayoutParams(-1, -2);
                    view2 = anonymousClass1;
                } else {
                    view = new StickerSetNameCell(EmojiView.this.getContext(), true, EmojiView.this.resourcesProvider);
                }
                view2.setLayoutParams(layoutParams);
                view = view2;
            } else {
                view = new ImageViewEmoji(EmojiView.this.getContext());
            }
            return new RecyclerListView.Holder(view);
        }

        public void search(String str) {
            search(str, true);
        }

        public void search(String str, boolean z) {
            if (TextUtils.isEmpty(str)) {
                this.lastSearchEmojiString = null;
                if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiAdapter) {
                    EmojiView.this.emojiGridView.setAdapter(EmojiView.this.emojiAdapter);
                    this.searchWas = false;
                }
                notifyDataSetChanged();
            } else {
                this.lastSearchEmojiString = str.toLowerCase();
            }
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            if (TextUtils.isEmpty(this.lastSearchEmojiString)) {
                return;
            }
            EmojiView.this.emojiSearchField.showProgress(true);
            Runnable runnable2 = new Runnable() {
                @Override
                public final void run() {
                    EmojiView.EmojiSearchAdapter.this.lambda$search$8();
                }
            };
            this.searchRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, z ? 300L : 0L);
        }
    }

    public interface EmojiViewDelegate {

        public abstract class CC {
            public static boolean $default$canSchedule(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static long $default$getDialogId(EmojiViewDelegate emojiViewDelegate) {
                return 0L;
            }

            public static float $default$getProgressToSearchOpened(EmojiViewDelegate emojiViewDelegate) {
                return 0.0f;
            }

            public static int $default$getThreadId(EmojiViewDelegate emojiViewDelegate) {
                return 0;
            }

            public static void $default$invalidateEnterView(EmojiViewDelegate emojiViewDelegate) {
            }

            public static boolean $default$isExpanded(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static boolean $default$isInScheduleMode(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static boolean $default$isSearchOpened(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static boolean $default$isUserSelf(EmojiViewDelegate emojiViewDelegate) {
                return false;
            }

            public static void $default$onAnimatedEmojiUnlockClick(EmojiViewDelegate emojiViewDelegate) {
            }

            public static void $default$onEmojiSettingsClick(EmojiViewDelegate emojiViewDelegate, ArrayList arrayList) {
            }

            public static void $default$onGifSelected(EmojiViewDelegate emojiViewDelegate, View view, Object obj, String str, Object obj2, boolean z, int i) {
            }

            public static void $default$onSearchOpenClose(EmojiViewDelegate emojiViewDelegate, int i) {
            }

            public static void $default$onShowStickerSet(EmojiViewDelegate emojiViewDelegate, TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet, boolean z) {
            }

            public static void $default$onStickerSelected(EmojiViewDelegate emojiViewDelegate, View view, TLRPC.Document document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i) {
            }

            public static void $default$onStickerSetAdd(EmojiViewDelegate emojiViewDelegate, TLRPC.StickerSetCovered stickerSetCovered) {
            }

            public static void $default$onStickerSetRemove(EmojiViewDelegate emojiViewDelegate, TLRPC.StickerSetCovered stickerSetCovered) {
            }

            public static void $default$onStickersGroupClick(EmojiViewDelegate emojiViewDelegate, long j) {
            }

            public static void $default$onStickersSettingsClick(EmojiViewDelegate emojiViewDelegate) {
            }

            public static void $default$onTabOpened(EmojiViewDelegate emojiViewDelegate, int i) {
            }

            public static void $default$showTrendingStickersAlert(EmojiViewDelegate emojiViewDelegate, TrendingStickersLayout trendingStickersLayout) {
            }
        }

        boolean canSchedule();

        long getDialogId();

        float getProgressToSearchOpened();

        int getThreadId();

        void invalidateEnterView();

        boolean isExpanded();

        boolean isInScheduleMode();

        boolean isSearchOpened();

        boolean isUserSelf();

        void onAnimatedEmojiUnlockClick();

        boolean onBackspace();

        void onClearEmojiRecent();

        void onCustomEmojiSelected(long j, TLRPC.Document document, String str, boolean z);

        void onEmojiSelected(String str);

        void onEmojiSettingsClick(ArrayList arrayList);

        void lambda$onGifSelected$1(View view, Object obj, String str, Object obj2, boolean z, int i);

        void onSearchOpenClose(int i);

        void onShowStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet, boolean z);

        void onStickerSelected(View view, TLRPC.Document document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i);

        void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered);

        void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered);

        void onStickersGroupClick(long j);

        void onStickersSettingsClick();

        void onTabOpened(int i);

        void showTrendingStickersAlert(TrendingStickersLayout trendingStickersLayout);
    }

    public class GifAdapter extends RecyclerListView.SelectionAdapter {
        private boolean addSearch;
        private TLRPC.User bot;
        private final Context context;
        private int firstResultItem;
        private int itemsCount;
        private String lastSearchImageString;
        private boolean lastSearchIsEmoji;
        private final int maxRecentRowsCount;
        private String nextSearchOffset;
        private final GifProgressEmptyView progressEmptyView;
        private int recentItemsCount;
        private int reqId;
        private ArrayList results;
        private HashMap resultsMap;
        private boolean searchEndReached;
        private Runnable searchRunnable;
        private boolean searchingUser;
        private boolean showTrendingWhenSearchEmpty;
        private int trendingSectionItem;
        private final boolean withRecent;

        public GifAdapter(EmojiView emojiView, Context context) {
            this(context, false, 0);
        }

        public GifAdapter(EmojiView emojiView, Context context, boolean z) {
            this(context, z, z ? Integer.MAX_VALUE : 0);
        }

        public GifAdapter(Context context, boolean z, int i) {
            this.results = new ArrayList();
            this.resultsMap = new HashMap();
            this.trendingSectionItem = -1;
            this.firstResultItem = -1;
            this.context = context;
            this.withRecent = z;
            this.maxRecentRowsCount = i;
            this.progressEmptyView = z ? null : new GifProgressEmptyView(context);
        }

        public void lambda$search$2(String str) {
            search(str, "", true);
        }

        public void lambda$search$4(final String str, final String str2, final boolean z, final boolean z2, final boolean z3, final String str3, final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    EmojiView.GifAdapter.this.lambda$search$3(str, str2, z, z2, z3, str3, tLObject);
                }
            });
        }

        public void lambda$searchBotUser$0(TLObject tLObject) {
            TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer) tLObject;
            MessagesController.getInstance(EmojiView.this.currentAccount).putUsers(tL_contacts_resolvedPeer.users, false);
            MessagesController.getInstance(EmojiView.this.currentAccount).putChats(tL_contacts_resolvedPeer.chats, false);
            MessagesStorage.getInstance(EmojiView.this.currentAccount).putUsersAndChats(tL_contacts_resolvedPeer.users, tL_contacts_resolvedPeer.chats, true, true);
            String str = this.lastSearchImageString;
            this.lastSearchImageString = null;
            search(str, "", false);
        }

        public void lambda$searchBotUser$1(final TLObject tLObject, TLRPC.TL_error tL_error) {
            if (tLObject != null) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        EmojiView.GifAdapter.this.lambda$searchBotUser$0(tLObject);
                    }
                });
            }
        }

        public void lambda$search$3(java.lang.String r9, java.lang.String r10, boolean r11, boolean r12, boolean r13, java.lang.String r14, org.telegram.tgnet.TLObject r15) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.GifAdapter.lambda$search$3(java.lang.String, java.lang.String, boolean, boolean, boolean, java.lang.String, org.telegram.tgnet.TLObject):void");
        }

        private void searchBotUser() {
            if (this.searchingUser) {
                return;
            }
            this.searchingUser = true;
            TLRPC.TL_contacts_resolveUsername tL_contacts_resolveUsername = new TLRPC.TL_contacts_resolveUsername();
            tL_contacts_resolveUsername.username = MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot;
            ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tL_contacts_resolveUsername, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    EmojiView.GifAdapter.this.lambda$searchBotUser$1(tLObject, tL_error);
                }
            });
        }

        private void updateItems() {
            int i;
            this.trendingSectionItem = -1;
            this.firstResultItem = -1;
            this.itemsCount = 0;
            if (this.addSearch) {
                this.itemsCount = 1;
            }
            if (this.withRecent) {
                this.itemsCount += this.recentItemsCount;
            }
            if (!this.results.isEmpty()) {
                if (this.withRecent && this.recentItemsCount > 0) {
                    int i2 = this.itemsCount;
                    this.itemsCount = i2 + 1;
                    this.trendingSectionItem = i2;
                }
                int i3 = this.itemsCount;
                this.firstResultItem = i3;
                i = i3 + this.results.size();
            } else if (this.withRecent) {
                return;
            } else {
                i = this.itemsCount + 1;
            }
            this.itemsCount = i;
        }

        private void updateRecentItemsCount() {
            int i;
            if (!this.withRecent || (i = this.maxRecentRowsCount) == 0) {
                return;
            }
            if (i == Integer.MAX_VALUE) {
                this.recentItemsCount = EmojiView.this.recentGifs.size();
                return;
            }
            if (EmojiView.this.gifGridView.getMeasuredWidth() == 0) {
                return;
            }
            int measuredWidth = EmojiView.this.gifGridView.getMeasuredWidth();
            int spanCount = EmojiView.this.gifLayoutManager.getSpanCount();
            int dp = AndroidUtilities.dp(100.0f);
            this.recentItemsCount = 0;
            int size = EmojiView.this.recentGifs.size();
            int i2 = spanCount;
            int i3 = 0;
            int i4 = 0;
            for (int i5 = 0; i5 < size; i5++) {
                Size fixSize = EmojiView.this.gifLayoutManager.fixSize(EmojiView.this.gifLayoutManager.getSizeForItem((TLRPC.Document) EmojiView.this.recentGifs.get(i5)));
                int min = Math.min(spanCount, (int) Math.floor(spanCount * (((fixSize.width / fixSize.height) * dp) / measuredWidth)));
                if (i2 < min) {
                    this.recentItemsCount += i3;
                    i4++;
                    if (i4 == this.maxRecentRowsCount) {
                        break;
                    }
                    i2 = spanCount;
                    i3 = 0;
                }
                i3++;
                i2 -= min;
            }
            if (i4 < this.maxRecentRowsCount) {
                this.recentItemsCount += i3;
            }
        }

        @Override
        public int getItemCount() {
            return this.itemsCount;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == 0 && this.addSearch) {
                return 1;
            }
            boolean z = this.withRecent;
            if (z && i == this.trendingSectionItem) {
                return 2;
            }
            return (z || !this.results.isEmpty()) ? 0 : 3;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 0;
        }

        public void loadTrendingGifs() {
            search("", "", true, true, true);
        }

        @Override
        public void notifyDataSetChanged() {
            updateRecentItemsCount();
            updateItems();
            super.notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() != 0) {
                return;
            }
            ContextLinkCell contextLinkCell = (ContextLinkCell) viewHolder.itemView;
            int i2 = this.firstResultItem;
            if (i2 < 0 || i < i2) {
                contextLinkCell.setGif((TLRPC.Document) EmojiView.this.recentGifs.get(i - (this.addSearch ? 1 : 0)), false);
            } else {
                contextLinkCell.setLink((TLRPC.BotInlineResult) this.results.get(i - i2), this.bot, true, false, false, true);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            ContextLinkCell contextLinkCell;
            RecyclerView.LayoutParams layoutParams;
            ?? r5;
            if (i != 0) {
                if (i == 1) {
                    View view = new View(EmojiView.this.getContext());
                    layoutParams = new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight);
                    r5 = view;
                } else if (i != 2) {
                    GifProgressEmptyView gifProgressEmptyView = this.progressEmptyView;
                    layoutParams = new RecyclerView.LayoutParams(-1, -2);
                    r5 = gifProgressEmptyView;
                } else {
                    StickerSetNameCell stickerSetNameCell = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    stickerSetNameCell.setText(LocaleController.getString(R.string.FeaturedGifs), 0);
                    layoutParams = new RecyclerView.LayoutParams(-1, -2);
                    ((ViewGroup.MarginLayoutParams) layoutParams).topMargin = AndroidUtilities.dp(2.5f);
                    ((ViewGroup.MarginLayoutParams) layoutParams).bottomMargin = AndroidUtilities.dp(5.5f);
                    r5 = stickerSetNameCell;
                }
                r5.setLayoutParams(layoutParams);
                contextLinkCell = r5;
            } else {
                ContextLinkCell contextLinkCell2 = new ContextLinkCell(this.context);
                contextLinkCell2.setIsKeyboard(true);
                contextLinkCell2.setCanPreviewGif(true);
                contextLinkCell = contextLinkCell2;
            }
            return new RecyclerListView.Holder(contextLinkCell);
        }

        public void search(String str) {
            search(str, true);
        }

        protected void search(String str, String str2, boolean z) {
            search(str, str2, z, false, false);
        }

        protected void search(final String str, final String str2, final boolean z, final boolean z2, final boolean z3) {
            int i = this.reqId;
            if (i != 0) {
                if (i >= 0) {
                    ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                }
                this.reqId = 0;
            }
            this.lastSearchImageString = str;
            this.lastSearchIsEmoji = z2;
            GifProgressEmptyView gifProgressEmptyView = this.progressEmptyView;
            if (gifProgressEmptyView != null) {
                gifProgressEmptyView.setLoadingState(z2);
            }
            TLObject userOrChat = MessagesController.getInstance(EmojiView.this.currentAccount).getUserOrChat(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchBot);
            if (!(userOrChat instanceof TLRPC.User)) {
                if (z) {
                    searchBotUser();
                    if (this.withRecent) {
                        return;
                    }
                    EmojiView.this.gifSearchField.showProgress(true);
                    return;
                }
                return;
            }
            if (!this.withRecent && TextUtils.isEmpty(str2)) {
                EmojiView.this.gifSearchField.showProgress(true);
            }
            this.bot = (TLRPC.User) userOrChat;
            final String str3 = "gif_search_" + str + "_" + str2;
            RequestDelegate requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    EmojiView.GifAdapter.this.lambda$search$4(str, str2, z, z2, z3, str3, tLObject, tL_error);
                }
            };
            if (!z3 && !this.withRecent && z2 && TextUtils.isEmpty(str2)) {
                this.results.clear();
                this.resultsMap.clear();
                if (EmojiView.this.gifGridView.getAdapter() != this) {
                    EmojiView.this.gifGridView.setAdapter(this);
                }
                notifyDataSetChanged();
                EmojiView.this.scrollGifsToTop();
            }
            if (z3 && EmojiView.this.gifCache.containsKey(str3)) {
                lambda$search$3(str, str2, z, z2, true, str3, (TLObject) EmojiView.this.gifCache.get(str3));
                return;
            }
            if (EmojiView.this.gifSearchPreloader.isLoading(str3)) {
                return;
            }
            if (z3) {
                this.reqId = -1;
                MessagesStorage.getInstance(EmojiView.this.currentAccount).getBotCache(str3, requestDelegate);
                return;
            }
            TLRPC.TL_messages_getInlineBotResults tL_messages_getInlineBotResults = new TLRPC.TL_messages_getInlineBotResults();
            if (str == null) {
                str = "";
            }
            tL_messages_getInlineBotResults.query = str;
            tL_messages_getInlineBotResults.bot = MessagesController.getInstance(EmojiView.this.currentAccount).getInputUser(this.bot);
            tL_messages_getInlineBotResults.offset = str2;
            tL_messages_getInlineBotResults.peer = new TLRPC.TL_inputPeerEmpty();
            this.reqId = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tL_messages_getInlineBotResults, requestDelegate);
        }

        public void search(final String str, boolean z) {
            if (this.withRecent) {
                return;
            }
            int i = this.reqId;
            if (i != 0) {
                if (i >= 0) {
                    ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                }
                this.reqId = 0;
            }
            this.lastSearchIsEmoji = false;
            GifProgressEmptyView gifProgressEmptyView = this.progressEmptyView;
            if (gifProgressEmptyView != null) {
                gifProgressEmptyView.setLoadingState(false);
            }
            Runnable runnable = this.searchRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            if (!TextUtils.isEmpty(str)) {
                String lowerCase = str.toLowerCase();
                this.lastSearchImageString = lowerCase;
                if (TextUtils.isEmpty(lowerCase)) {
                    return;
                }
                Runnable runnable2 = new Runnable() {
                    @Override
                    public final void run() {
                        EmojiView.GifAdapter.this.lambda$search$2(str);
                    }
                };
                this.searchRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, z ? 300L : 0L);
                return;
            }
            this.lastSearchImageString = null;
            if (this.showTrendingWhenSearchEmpty) {
                loadTrendingGifs();
                return;
            }
            int currentPosition = EmojiView.this.gifTabs.getCurrentPosition();
            if (currentPosition != EmojiView.this.gifRecentTabNum && currentPosition != EmojiView.this.gifTrendingTabNum) {
                searchEmoji(MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchEmojies.get(currentPosition - EmojiView.this.gifFirstEmojiTabNum));
            } else if (EmojiView.this.gifGridView.getAdapter() != EmojiView.this.gifAdapter) {
                EmojiView.this.gifGridView.setAdapter(EmojiView.this.gifAdapter);
            }
        }

        public void searchEmoji(String str) {
            if (this.lastSearchIsEmoji && TextUtils.equals(this.lastSearchImageString, str)) {
                EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(0, 0);
            } else {
                search(str, "", true, true, true);
            }
        }
    }

    public class GifLayoutManager extends ExtendedGridLayoutManager {
        private Size size;

        public class AnonymousClass1 extends GridLayoutManager.SpanSizeLookup {
            final EmojiView val$this$0;

            AnonymousClass1(EmojiView emojiView) {
                r2 = emojiView;
            }

            @Override
            public int getSpanSize(int i) {
                if ((i == 0 && EmojiView.this.gifAdapter.addSearch) || (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter && EmojiView.this.gifSearchAdapter.results.isEmpty())) {
                    return GifLayoutManager.this.getSpanCount();
                }
                GifLayoutManager gifLayoutManager = GifLayoutManager.this;
                return gifLayoutManager.getSpanSizeForItem(i - (EmojiView.this.gifAdapter.addSearch ? 1 : 0));
            }
        }

        public GifLayoutManager(Context context) {
            super(context, 100, true);
            this.size = new Size();
            setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                final EmojiView val$this$0;

                AnonymousClass1(EmojiView emojiView) {
                    r2 = emojiView;
                }

                @Override
                public int getSpanSize(int i) {
                    if ((i == 0 && EmojiView.this.gifAdapter.addSearch) || (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter && EmojiView.this.gifSearchAdapter.results.isEmpty())) {
                        return GifLayoutManager.this.getSpanCount();
                    }
                    GifLayoutManager gifLayoutManager = GifLayoutManager.this;
                    return gifLayoutManager.getSpanSizeForItem(i - (EmojiView.this.gifAdapter.addSearch ? 1 : 0));
                }
            });
        }

        @Override
        public int getFlowItemCount() {
            if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifSearchAdapter && EmojiView.this.gifSearchAdapter.results.isEmpty()) {
                return 0;
            }
            return getItemCount() - 1;
        }

        @Override
        protected org.telegram.ui.Components.Size getSizeForItem(int r4) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.GifLayoutManager.getSizeForItem(int):org.telegram.ui.Components.Size");
        }

        public Size getSizeForItem(TLRPC.Document document) {
            return getSizeForItem(document, document.attributes);
        }

        public Size getSizeForItem(TLRPC.Document document, List list) {
            TLRPC.PhotoSize closestPhotoSizeWithSize;
            int i;
            int i2;
            Size size = this.size;
            size.height = 100.0f;
            size.width = 100.0f;
            if (document != null && (closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90)) != null && (i = closestPhotoSizeWithSize.w) != 0 && (i2 = closestPhotoSizeWithSize.h) != 0) {
                Size size2 = this.size;
                size2.width = i;
                size2.height = i2;
            }
            if (list != null) {
                for (int i3 = 0; i3 < list.size(); i3++) {
                    TLRPC.DocumentAttribute documentAttribute = (TLRPC.DocumentAttribute) list.get(i3);
                    if ((documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) || (documentAttribute instanceof TLRPC.TL_documentAttributeVideo)) {
                        Size size3 = this.size;
                        size3.width = documentAttribute.w;
                        size3.height = documentAttribute.h;
                        break;
                    }
                }
            }
            return this.size;
        }
    }

    public class GifProgressEmptyView extends FrameLayout {
        private final ImageView imageView;
        private boolean loadingState;
        private final RadialProgressView progressView;
        private final TextView textView;

        public GifProgressEmptyView(Context context) {
            super(context);
            ImageView imageView = new ImageView(getContext());
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            imageView.setImageResource(R.drawable.gif_empty);
            int i = Theme.key_chat_emojiPanelEmptyText;
            imageView.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor(i), PorterDuff.Mode.MULTIPLY));
            addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 8.0f, 0.0f, 0.0f));
            TextView textView = new TextView(getContext());
            this.textView = textView;
            textView.setText(LocaleController.getString(R.string.NoGIFsFound));
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(EmojiView.this.getThemedColor(i));
            addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 42.0f, 0.0f, 0.0f));
            RadialProgressView radialProgressView = new RadialProgressView(context, EmojiView.this.resourcesProvider);
            this.progressView = radialProgressView;
            radialProgressView.setVisibility(8);
            radialProgressView.setProgressColor(EmojiView.this.getThemedColor(Theme.key_progressCircle));
            addView(radialProgressView, LayoutHelper.createFrame(-2, -2, 17));
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(!this.loadingState ? (int) ((((r3 - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3) * 1.7f) : EmojiView.this.gifGridView.getMeasuredHeight() - AndroidUtilities.dp(80.0f), 1073741824));
        }

        public void setLoadingState(boolean z) {
            if (this.loadingState != z) {
                this.loadingState = z;
                this.imageView.setVisibility(z ? 8 : 0);
                this.textView.setVisibility(z ? 8 : 0);
                this.progressView.setVisibility(z ? 0 : 8);
            }
        }
    }

    public class GifSearchPreloader {
        private final List loadingKeys;

        private GifSearchPreloader() {
            this.loadingKeys = new ArrayList();
        }

        GifSearchPreloader(EmojiView emojiView, AnonymousClass1 anonymousClass1) {
            this();
        }

        public void lambda$preload$1(final String str, final String str2, final boolean z, final String str3, final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    EmojiView.GifSearchPreloader.this.lambda$preload$0(str, str2, z, str3, tLObject);
                }
            });
        }

        private void preload(final String str, final String str2, final boolean z) {
            final String str3 = "gif_search_" + str + "_" + str2;
            if (z && EmojiView.this.gifCache.containsKey(str3)) {
                return;
            }
            RequestDelegate requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    EmojiView.GifSearchPreloader.this.lambda$preload$1(str, str2, z, str3, tLObject, tL_error);
                }
            };
            if (z) {
                this.loadingKeys.add(str3);
                MessagesStorage.getInstance(EmojiView.this.currentAccount).getBotCache(str3, requestDelegate);
                return;
            }
            MessagesController messagesController = MessagesController.getInstance(EmojiView.this.currentAccount);
            TLObject userOrChat = messagesController.getUserOrChat(messagesController.gifSearchBot);
            if (userOrChat instanceof TLRPC.User) {
                this.loadingKeys.add(str3);
                TLRPC.TL_messages_getInlineBotResults tL_messages_getInlineBotResults = new TLRPC.TL_messages_getInlineBotResults();
                if (str == null) {
                    str = "";
                }
                tL_messages_getInlineBotResults.query = str;
                tL_messages_getInlineBotResults.bot = messagesController.getInputUser((TLRPC.User) userOrChat);
                tL_messages_getInlineBotResults.offset = str2;
                tL_messages_getInlineBotResults.peer = new TLRPC.TL_inputPeerEmpty();
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tL_messages_getInlineBotResults, requestDelegate, 2);
            }
        }

        public void lambda$preload$0(String str, String str2, boolean z, String str3, TLObject tLObject) {
            this.loadingKeys.remove(str3);
            if (EmojiView.this.gifSearchAdapter.lastSearchIsEmoji && EmojiView.this.gifSearchAdapter.lastSearchImageString.equals(str)) {
                EmojiView.this.gifSearchAdapter.lambda$search$3(str, str2, false, true, z, str3, tLObject);
                return;
            }
            if (z && (!(tLObject instanceof TLRPC.messages_BotResults) || ((TLRPC.messages_BotResults) tLObject).results.isEmpty())) {
                preload(str, str2, false);
            } else {
                if (!(tLObject instanceof TLRPC.messages_BotResults) || EmojiView.this.gifCache.containsKey(str3)) {
                    return;
                }
                EmojiView.this.gifCache.put(str3, (TLRPC.messages_BotResults) tLObject);
            }
        }

        public boolean isLoading(String str) {
            return this.loadingKeys.contains(str);
        }

        public void preload(String str) {
            preload(str, "", true);
        }
    }

    public static class ImageViewEmoji extends ImageView {
        ValueAnimator backAnimator;
        private ImageReceiver.BackgroundThreadDrawHolder[] backgroundThreadDrawHolder;
        public AnimatedEmojiDrawable drawable;
        public boolean ignoring;
        public ImageReceiver imageReceiver;
        private boolean isRecent;
        private EmojiPack pack;
        public int position;
        float pressedProgress;
        private AnimatedEmojiSpan span;

        class AnonymousClass1 extends AnimatorListenerAdapter {
            AnonymousClass1() {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                ImageViewEmoji.this.backAnimator = null;
            }
        }

        public ImageViewEmoji(Context context) {
            super(context);
            this.backgroundThreadDrawHolder = new ImageReceiver.BackgroundThreadDrawHolder[2];
            setScaleType(ImageView.ScaleType.CENTER);
            setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f)));
        }

        public void lambda$setPressed$0(ValueAnimator valueAnimator) {
            this.pressedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        public AnimatedEmojiSpan getSpan() {
            return this.span;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (isPressed()) {
                float f = this.pressedProgress;
                if (f != 1.0f) {
                    float min = f + (Math.min(40.0f, 1000.0f / AndroidUtilities.screenRefreshRate) / 100.0f);
                    this.pressedProgress = min;
                    this.pressedProgress = Utilities.clamp(min, 1.0f, 0.0f);
                    invalidate();
                }
            }
            float f2 = ((1.0f - this.pressedProgress) * 0.2f) + 0.8f;
            canvas.save();
            canvas.scale(f2, f2, getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f);
            super.onDraw(canvas);
            canvas.restore();
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName("android.view.View");
        }

        @Override
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i));
        }

        public void setImageDrawable(Drawable drawable, boolean z) {
            super.setImageDrawable(drawable);
            this.isRecent = z;
        }

        @Override
        public void setPressed(boolean z) {
            ValueAnimator valueAnimator;
            if (isPressed() != z) {
                super.setPressed(z);
                invalidate();
                if (z && (valueAnimator = this.backAnimator) != null) {
                    valueAnimator.removeAllListeners();
                    this.backAnimator.cancel();
                }
                if (z) {
                    return;
                }
                float f = this.pressedProgress;
                if (f != 0.0f) {
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(f, 0.0f);
                    this.backAnimator = ofFloat;
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                            EmojiView.ImageViewEmoji.this.lambda$setPressed$0(valueAnimator2);
                        }
                    });
                    this.backAnimator.addListener(new AnimatorListenerAdapter() {
                        AnonymousClass1() {
                        }

                        @Override
                        public void onAnimationEnd(Animator animator) {
                            super.onAnimationEnd(animator);
                            ImageViewEmoji.this.backAnimator = null;
                        }
                    });
                    this.backAnimator.setInterpolator(new OvershootInterpolator(5.0f));
                    this.backAnimator.setDuration(350L);
                    this.backAnimator.start();
                }
            }
        }

        public void setSpan(AnimatedEmojiSpan animatedEmojiSpan) {
            this.span = animatedEmojiSpan;
        }
    }

    public class SearchField extends FrameLayout {
        private View backgroundView;
        private FrameLayout box;
        private StickerCategoriesListView categoriesListView;
        private ImageView clear;
        private Runnable delayedToggle;
        private FrameLayout inputBox;
        private View inputBoxGradient;
        private float inputBoxGradientAlpha;
        ValueAnimator inputBoxGradientAnimator;
        private boolean inputBoxShown;
        private boolean isprogress;
        private StickerCategoriesListView.EmojiCategory recent;
        private EditTextBoldCursor searchEditText;
        private ImageView searchImageView;
        private SearchStateDrawable searchStateDrawable;
        private AnimatorSet shadowAnimator;
        private View shadowView;
        private StickerCategoriesListView.EmojiCategory trending;
        private int type;

        public class AnonymousClass1 extends ViewOutlineProvider {
            final EmojiView val$this$0;

            AnonymousClass1(EmojiView emojiView) {
                r2 = emojiView;
            }

            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), AndroidUtilities.dp(18.0f));
            }
        }

        public class AnonymousClass2 extends FrameLayout {
            Paint fadePaint;
            final EmojiView val$this$0;

            AnonymousClass2(Context context, EmojiView emojiView) {
                super(context);
                r3 = emojiView;
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                if (EmojiView.this.shouldDrawBackground || SearchField.this.inputBoxGradientAlpha <= 0.0f) {
                    super.dispatchDraw(canvas);
                    return;
                }
                if (this.fadePaint == null) {
                    Paint paint = new Paint();
                    this.fadePaint = paint;
                    paint.setShader(new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(18.0f), 0.0f, new int[]{-1, 0}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
                    this.fadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                }
                canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), 255, 31);
                super.dispatchDraw(canvas);
                this.fadePaint.setAlpha((int) (SearchField.this.inputBoxGradientAlpha * 255.0f));
                canvas.drawRect(0.0f, 0.0f, AndroidUtilities.dp(18.0f), getMeasuredHeight(), this.fadePaint);
                canvas.restore();
            }
        }

        public class AnonymousClass3 extends EditTextBoldCursor {
            final EmojiView val$this$0;
            final int val$type;

            AnonymousClass3(Context context, EmojiView emojiView, int i) {
                super(context);
                r3 = emojiView;
                r4 = i;
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (!SearchField.this.searchEditText.isEnabled()) {
                    return super.onTouchEvent(motionEvent);
                }
                if (motionEvent.getAction() == 0) {
                    if (!EmojiView.this.delegate.isSearchOpened()) {
                        SearchField searchField = SearchField.this;
                        EmojiView.this.openSearch(searchField);
                    }
                    EmojiView.this.delegate.onSearchOpenClose(r4 == 1 ? 2 : 1);
                    SearchField.this.searchEditText.requestFocus();
                    AndroidUtilities.showKeyboard(SearchField.this.searchEditText);
                }
                return super.onTouchEvent(motionEvent);
            }
        }

        public class AnonymousClass4 implements TextWatcher {
            final EmojiView val$this$0;

            AnonymousClass4(EmojiView emojiView) {
                r2 = emojiView;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                SearchField.this.updateButton();
                String obj = SearchField.this.searchEditText.getText().toString();
                SearchField.this.search(obj, true);
                if (SearchField.this.categoriesListView != null) {
                    SearchField.this.categoriesListView.selectCategory((StickerCategoriesListView.EmojiCategory) null);
                    SearchField.this.categoriesListView.updateCategoriesShown(TextUtils.isEmpty(obj), true);
                }
                SearchField.this.toggleClear(!TextUtils.isEmpty(obj));
                if (SearchField.this.searchEditText != null) {
                    SearchField.this.searchEditText.clearAnimation();
                    SearchField.this.searchEditText.animate().translationX(0.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
                }
                SearchField.this.showInputBoxGradient(false);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        }

        public class AnonymousClass5 extends CloseProgressDrawable2 {
            final EmojiView val$this$0;

            AnonymousClass5(float f, EmojiView emojiView) {
                super(f);
                r3 = emojiView;
                setSide(AndroidUtilities.dp(7.0f));
            }

            @Override
            protected int getCurrentColor() {
                return Theme.getColor(Theme.key_chat_emojiSearchIcon, EmojiView.this.resourcesProvider);
            }
        }

        public class AnonymousClass6 extends StickerCategoriesListView {
            final EmojiView val$this$0;
            final int val$type;

            AnonymousClass6(Context context, StickerCategoriesListView.EmojiCategory[] emojiCategoryArr, int i, Theme.ResourcesProvider resourcesProvider, EmojiView emojiView, int i2) {
                super(context, emojiCategoryArr, i, resourcesProvider);
                r6 = emojiView;
                r7 = i2;
            }

            @Override
            protected boolean isTabIconsAnimationEnabled(boolean z) {
                return LiteMode.isEnabled(8200);
            }

            @Override
            public void selectCategory(int i) {
                super.selectCategory(i);
                SearchField searchField = SearchField.this;
                EmojiView.this.showBottomTab(searchField.categoriesListView.getSelectedCategory() == null, true);
                if (r7 == 1 && EmojiView.this.emojiTabs != null) {
                    EmojiView.this.emojiTabs.showSelected(SearchField.this.categoriesListView.getSelectedCategory() == null);
                } else if (r7 == 0 && EmojiView.this.stickersTab != null) {
                    EmojiView.this.stickersTab.showSelected(SearchField.this.categoriesListView.getSelectedCategory() == null);
                }
                SearchField.this.updateButton();
            }
        }

        public class AnonymousClass7 implements View.OnTouchListener {
            final EmojiView val$this$0;

            AnonymousClass7(EmojiView emojiView) {
                r2 = emojiView;
            }

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    EmojiView.this.ignorePagerScroll = true;
                } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    EmojiView.this.ignorePagerScroll = false;
                }
                return false;
            }
        }

        public class AnonymousClass8 extends AnimatorListenerAdapter {
            AnonymousClass8() {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                SearchField.this.shadowAnimator = null;
            }
        }

        public SearchField(Context context, int i) {
            super(context);
            FrameLayout frameLayout;
            float f;
            float f2;
            int i2;
            float f3;
            int i3;
            float f4;
            float f5;
            this.inputBoxShown = false;
            this.type = i;
            View view = new View(context);
            this.shadowView = view;
            view.setAlpha(0.0f);
            this.shadowView.setTag(1);
            this.shadowView.setBackgroundColor(EmojiView.this.getThemedColor(Theme.key_chat_emojiPanelShadowLine));
            addView(this.shadowView, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83));
            this.backgroundView = new View(context);
            if (EmojiView.this.shouldDrawBackground) {
                this.backgroundView.setBackgroundColor(EmojiView.this.getThemedColor(Theme.key_chat_emojiPanelBackground));
            }
            addView(this.backgroundView, new FrameLayout.LayoutParams(-1, EmojiView.this.searchFieldHeight));
            FrameLayout frameLayout2 = new FrameLayout(context);
            this.box = frameLayout2;
            int dp = AndroidUtilities.dp(18.0f);
            int i4 = Theme.key_chat_emojiSearchBackground;
            frameLayout2.setBackground(Theme.createRoundRectDrawable(dp, EmojiView.this.getThemedColor(i4)));
            if (Build.VERSION.SDK_INT >= 21) {
                this.box.setClipToOutline(true);
                this.box.setOutlineProvider(new ViewOutlineProvider() {
                    final EmojiView val$this$0;

                    AnonymousClass1(EmojiView emojiView) {
                        r2 = emojiView;
                    }

                    @Override
                    public void getOutline(View view2, Outline outline) {
                        outline.setRoundRect(0, 0, view2.getWidth(), view2.getHeight(), AndroidUtilities.dp(18.0f));
                    }
                });
            }
            if (i == 2) {
                frameLayout = this.box;
                f = 10.0f;
                f2 = 8.0f;
                i2 = -1;
                f3 = 36.0f;
                i3 = 119;
                f4 = 10.0f;
                f5 = 8.0f;
            } else {
                frameLayout = this.box;
                f = 10.0f;
                f2 = 8.0f;
                i2 = -1;
                f3 = 36.0f;
                i3 = 119;
                f4 = 10.0f;
                f5 = 6.0f;
            }
            addView(frameLayout, LayoutHelper.createFrame(i2, f3, i3, f4, f5, f, f2));
            AnonymousClass2 anonymousClass2 = new FrameLayout(context) {
                Paint fadePaint;
                final EmojiView val$this$0;

                AnonymousClass2(Context context2, EmojiView emojiView) {
                    super(context2);
                    r3 = emojiView;
                }

                @Override
                protected void dispatchDraw(Canvas canvas) {
                    if (EmojiView.this.shouldDrawBackground || SearchField.this.inputBoxGradientAlpha <= 0.0f) {
                        super.dispatchDraw(canvas);
                        return;
                    }
                    if (this.fadePaint == null) {
                        Paint paint = new Paint();
                        this.fadePaint = paint;
                        paint.setShader(new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(18.0f), 0.0f, new int[]{-1, 0}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
                        this.fadePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
                    }
                    canvas.saveLayerAlpha(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), 255, 31);
                    super.dispatchDraw(canvas);
                    this.fadePaint.setAlpha((int) (SearchField.this.inputBoxGradientAlpha * 255.0f));
                    canvas.drawRect(0.0f, 0.0f, AndroidUtilities.dp(18.0f), getMeasuredHeight(), this.fadePaint);
                    canvas.restore();
                }
            };
            this.inputBox = anonymousClass2;
            this.box.addView(anonymousClass2, LayoutHelper.createFrame(-1, 40.0f, 51, 38.0f, 0.0f, 0.0f, 0.0f));
            this.searchImageView = new ImageView(context2);
            SearchStateDrawable searchStateDrawable = new SearchStateDrawable();
            this.searchStateDrawable = searchStateDrawable;
            searchStateDrawable.setIconState(0, false);
            SearchStateDrawable searchStateDrawable2 = this.searchStateDrawable;
            int i5 = Theme.key_chat_emojiSearchIcon;
            searchStateDrawable2.setColor(EmojiView.this.getThemedColor(i5));
            ImageView imageView = this.searchImageView;
            ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
            imageView.setScaleType(scaleType);
            this.searchImageView.setImageDrawable(this.searchStateDrawable);
            this.searchImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    EmojiView.SearchField.this.lambda$new$0(view2);
                }
            });
            this.box.addView(this.searchImageView, LayoutHelper.createFrame(36, 36, 51));
            AnonymousClass3 anonymousClass3 = new EditTextBoldCursor(context2) {
                final EmojiView val$this$0;
                final int val$type;

                AnonymousClass3(Context context2, EmojiView emojiView, int i6) {
                    super(context2);
                    r3 = emojiView;
                    r4 = i6;
                }

                @Override
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (!SearchField.this.searchEditText.isEnabled()) {
                        return super.onTouchEvent(motionEvent);
                    }
                    if (motionEvent.getAction() == 0) {
                        if (!EmojiView.this.delegate.isSearchOpened()) {
                            SearchField searchField = SearchField.this;
                            EmojiView.this.openSearch(searchField);
                        }
                        EmojiView.this.delegate.onSearchOpenClose(r4 == 1 ? 2 : 1);
                        SearchField.this.searchEditText.requestFocus();
                        AndroidUtilities.showKeyboard(SearchField.this.searchEditText);
                    }
                    return super.onTouchEvent(motionEvent);
                }
            };
            this.searchEditText = anonymousClass3;
            anonymousClass3.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(EmojiView.this.getThemedColor(i5));
            this.searchEditText.setTextColor(EmojiView.this.getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            this.searchEditText.setBackgroundDrawable(null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(268435459);
            this.searchEditText.setHint(LocaleController.getString(R.string.Search));
            this.searchEditText.setCursorColor(EmojiView.this.getThemedColor(Theme.key_featuredStickers_addedIcon));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            this.searchEditText.setTranslationY(AndroidUtilities.dp(-2.0f));
            this.inputBox.addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 0.0f, 0.0f, 28.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher() {
                final EmojiView val$this$0;

                AnonymousClass4(EmojiView emojiView) {
                    r2 = emojiView;
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    SearchField.this.updateButton();
                    String obj = SearchField.this.searchEditText.getText().toString();
                    SearchField.this.search(obj, true);
                    if (SearchField.this.categoriesListView != null) {
                        SearchField.this.categoriesListView.selectCategory((StickerCategoriesListView.EmojiCategory) null);
                        SearchField.this.categoriesListView.updateCategoriesShown(TextUtils.isEmpty(obj), true);
                    }
                    SearchField.this.toggleClear(!TextUtils.isEmpty(obj));
                    if (SearchField.this.searchEditText != null) {
                        SearchField.this.searchEditText.clearAnimation();
                        SearchField.this.searchEditText.animate().translationX(0.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
                    }
                    SearchField.this.showInputBoxGradient(false);
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i6, int i22, int i32) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i6, int i22, int i32) {
                }
            });
            if (EmojiView.this.shouldDrawBackground) {
                this.inputBoxGradient = new View(context2);
                Drawable mutate = context2.getResources().getDrawable(R.drawable.gradient_right).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(Theme.blendOver(EmojiView.this.getThemedColor(Theme.key_chat_emojiPanelBackground), EmojiView.this.getThemedColor(i4)), PorterDuff.Mode.MULTIPLY));
                this.inputBoxGradient.setBackground(mutate);
                this.inputBoxGradient.setAlpha(0.0f);
                this.inputBox.addView(this.inputBoxGradient, LayoutHelper.createFrame(18, -1, 3));
            }
            ImageView imageView2 = new ImageView(context2);
            this.clear = imageView2;
            imageView2.setScaleType(scaleType);
            this.clear.setImageDrawable(new CloseProgressDrawable2(1.25f) {
                final EmojiView val$this$0;

                AnonymousClass5(float f6, EmojiView emojiView) {
                    super(f6);
                    r3 = emojiView;
                    setSide(AndroidUtilities.dp(7.0f));
                }

                @Override
                protected int getCurrentColor() {
                    return Theme.getColor(Theme.key_chat_emojiSearchIcon, EmojiView.this.resourcesProvider);
                }
            });
            this.clear.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector, EmojiView.this.resourcesProvider), 1, AndroidUtilities.dp(15.0f)));
            this.clear.setAlpha(0.0f);
            this.clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    EmojiView.SearchField.this.lambda$new$1(view2);
                }
            });
            this.box.addView(this.clear, LayoutHelper.createFrame(36, 36, 53));
            if (i6 != 1 || (EmojiView.this.allowAnimatedEmoji && UserConfig.getInstance(UserConfig.selectedAccount).isPremium())) {
                AnonymousClass6 anonymousClass6 = new StickerCategoriesListView(context2, null, i6 == 0 ? 3 : 0, EmojiView.this.resourcesProvider) {
                    final EmojiView val$this$0;
                    final int val$type;

                    AnonymousClass6(Context context2, StickerCategoriesListView.EmojiCategory[] emojiCategoryArr, int i6, Theme.ResourcesProvider resourcesProvider, EmojiView emojiView, int i62) {
                        super(context2, emojiCategoryArr, i6, resourcesProvider);
                        r6 = emojiView;
                        r7 = i62;
                    }

                    @Override
                    protected boolean isTabIconsAnimationEnabled(boolean z) {
                        return LiteMode.isEnabled(8200);
                    }

                    @Override
                    public void selectCategory(int i6) {
                        super.selectCategory(i6);
                        SearchField searchField = SearchField.this;
                        EmojiView.this.showBottomTab(searchField.categoriesListView.getSelectedCategory() == null, true);
                        if (r7 == 1 && EmojiView.this.emojiTabs != null) {
                            EmojiView.this.emojiTabs.showSelected(SearchField.this.categoriesListView.getSelectedCategory() == null);
                        } else if (r7 == 0 && EmojiView.this.stickersTab != null) {
                            EmojiView.this.stickersTab.showSelected(SearchField.this.categoriesListView.getSelectedCategory() == null);
                        }
                        SearchField.this.updateButton();
                    }
                };
                this.categoriesListView = anonymousClass6;
                anonymousClass6.setDontOccupyWidth(((int) this.searchEditText.getPaint().measureText(((Object) this.searchEditText.getHint()) + "")) + AndroidUtilities.dp(16.0f));
                if (EmojiView.this.shouldDrawBackground) {
                    this.categoriesListView.setBackgroundColor(Theme.blendOver(EmojiView.this.getThemedColor(Theme.key_chat_emojiPanelBackground), EmojiView.this.getThemedColor(i4)));
                }
                this.categoriesListView.setOnScrollIntoOccupiedWidth(new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        EmojiView.SearchField.this.lambda$new$2((Integer) obj);
                    }
                });
                this.categoriesListView.setOnTouchListener(new View.OnTouchListener() {
                    final EmojiView val$this$0;

                    AnonymousClass7(EmojiView emojiView) {
                        r2 = emojiView;
                    }

                    @Override
                    public boolean onTouch(View view2, MotionEvent motionEvent) {
                        if (motionEvent.getAction() == 0) {
                            EmojiView.this.ignorePagerScroll = true;
                        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                            EmojiView.this.ignorePagerScroll = false;
                        }
                        return false;
                    }
                });
                this.categoriesListView.setOnCategoryClick(new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        EmojiView.SearchField.this.lambda$new$3((StickerCategoriesListView.EmojiCategory) obj);
                    }
                });
                this.box.addView(this.categoriesListView, LayoutHelper.createFrame(-1, 36.0f, 51, 36.0f, 0.0f, 0.0f, 0.0f));
            }
        }

        public void lambda$new$0(View view) {
            if (this.searchStateDrawable.getIconState() == 1) {
                this.searchEditText.setText("");
                search(null, false);
                StickerCategoriesListView stickerCategoriesListView = this.categoriesListView;
                if (stickerCategoriesListView != null) {
                    stickerCategoriesListView.scrollToStart();
                    this.categoriesListView.selectCategory((StickerCategoriesListView.EmojiCategory) null);
                    this.categoriesListView.updateCategoriesShown(true, true);
                }
                toggleClear(false);
                EditTextBoldCursor editTextBoldCursor = this.searchEditText;
                if (editTextBoldCursor != null) {
                    editTextBoldCursor.clearAnimation();
                    this.searchEditText.animate().translationX(0.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
                }
                showInputBoxGradient(false);
            }
        }

        public void lambda$new$1(View view) {
            this.searchEditText.setText("");
            search(null, false);
            StickerCategoriesListView stickerCategoriesListView = this.categoriesListView;
            if (stickerCategoriesListView != null) {
                stickerCategoriesListView.scrollToStart();
                this.categoriesListView.selectCategory((StickerCategoriesListView.EmojiCategory) null);
                this.categoriesListView.updateCategoriesShown(true, true);
            }
            toggleClear(false);
            EditTextBoldCursor editTextBoldCursor = this.searchEditText;
            if (editTextBoldCursor != null) {
                editTextBoldCursor.clearAnimation();
                this.searchEditText.animate().translationX(0.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
            }
            showInputBoxGradient(false);
        }

        public void lambda$new$2(Integer num) {
            this.searchEditText.setTranslationX(-Math.max(0, num.intValue()));
            showInputBoxGradient(num.intValue() > 0);
            updateButton();
        }

        public void lambda$new$3(StickerCategoriesListView.EmojiCategory emojiCategory) {
            if (emojiCategory == this.recent) {
                showInputBoxGradient(false);
                this.categoriesListView.selectCategory(this.recent);
                EmojiView.this.gifSearchField.searchEditText.setText("");
                EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(0, 0);
                return;
            }
            if (emojiCategory != this.trending) {
                if (this.categoriesListView.getSelectedCategory() == emojiCategory) {
                    emojiCategory = null;
                    search(null, false);
                } else {
                    search(emojiCategory.emojis, false);
                }
                this.categoriesListView.selectCategory(emojiCategory);
                return;
            }
            showInputBoxGradient(false);
            EmojiView.this.gifSearchField.searchEditText.setText("");
            EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(EmojiView.this.gifAdapter.trendingSectionItem, -AndroidUtilities.dp(4.0f));
            this.categoriesListView.selectCategory(this.trending);
            ArrayList<String> arrayList = MessagesController.getInstance(EmojiView.this.currentAccount).gifSearchEmojies;
            if (arrayList.isEmpty()) {
                return;
            }
            EmojiView.this.gifSearchPreloader.preload(arrayList.get(0));
        }

        public void lambda$showInputBoxGradient$4(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.inputBoxGradientAlpha = floatValue;
            View view = this.inputBoxGradient;
            if (view != null) {
                view.setAlpha(floatValue);
                return;
            }
            FrameLayout frameLayout = this.inputBox;
            if (frameLayout != null) {
                frameLayout.invalidate();
            }
        }

        public void lambda$toggleClear$5() {
            AndroidUtilities.updateViewShow(this.clear, true);
        }

        public void showInputBoxGradient(boolean z) {
            if (z == this.inputBoxShown) {
                return;
            }
            this.inputBoxShown = z;
            ValueAnimator valueAnimator = this.inputBoxGradientAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.inputBoxGradientAlpha, z ? 1.0f : 0.0f);
            this.inputBoxGradientAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    EmojiView.SearchField.this.lambda$showInputBoxGradient$4(valueAnimator2);
                }
            });
            this.inputBoxGradientAnimator.setDuration(120L);
            this.inputBoxGradientAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.inputBoxGradientAnimator.start();
        }

        public void showShadow(boolean z, boolean z2) {
            if (z && this.shadowView.getTag() == null) {
                return;
            }
            if (z || this.shadowView.getTag() == null) {
                AnimatorSet animatorSet = this.shadowAnimator;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.shadowAnimator = null;
                }
                this.shadowView.setTag(z ? null : 1);
                if (!z2) {
                    this.shadowView.setAlpha(z ? 1.0f : 0.0f);
                    return;
                }
                AnimatorSet animatorSet2 = new AnimatorSet();
                this.shadowAnimator = animatorSet2;
                animatorSet2.playTogether(ObjectAnimator.ofFloat(this.shadowView, (Property<View, Float>) View.ALPHA, z ? 1.0f : 0.0f));
                this.shadowAnimator.setDuration(200L);
                this.shadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
                this.shadowAnimator.addListener(new AnimatorListenerAdapter() {
                    AnonymousClass8() {
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        SearchField.this.shadowAnimator = null;
                    }
                });
                this.shadowAnimator.start();
            }
        }

        public void toggleClear(boolean z) {
            if (z) {
                if (this.delayedToggle == null) {
                    Runnable runnable = new Runnable() {
                        @Override
                        public final void run() {
                            EmojiView.SearchField.this.lambda$toggleClear$5();
                        }
                    };
                    this.delayedToggle = runnable;
                    AndroidUtilities.runOnUIThread(runnable, 340L);
                    return;
                }
                return;
            }
            Runnable runnable2 = this.delayedToggle;
            if (runnable2 != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable2);
                this.delayedToggle = null;
            }
            AndroidUtilities.updateViewShow(this.clear, false);
        }

        public void updateButton() {
            updateButton(false);
        }

        private void updateButton(boolean z) {
            StickerCategoriesListView stickerCategoriesListView;
            StickerCategoriesListView stickerCategoriesListView2;
            if (!isInProgress() || ((this.searchEditText.length() == 0 && ((stickerCategoriesListView2 = this.categoriesListView) == null || stickerCategoriesListView2.getSelectedCategory() == null)) || z)) {
                this.searchStateDrawable.setIconState((this.searchEditText.length() > 0 || ((stickerCategoriesListView = this.categoriesListView) != null && stickerCategoriesListView.isCategoriesShown() && (this.categoriesListView.isScrolledIntoOccupiedWidth() || this.categoriesListView.getSelectedCategory() != null))) ? 1 : 0);
                this.isprogress = false;
            }
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }

        public boolean isCategorySelected() {
            StickerCategoriesListView stickerCategoriesListView = this.categoriesListView;
            return (stickerCategoriesListView == null || stickerCategoriesListView.getSelectedCategory() == null) ? false : true;
        }

        public boolean isInProgress() {
            return this.isprogress;
        }

        public void search(String str, boolean z) {
            int i = this.type;
            if (i == 0) {
                EmojiView.this.stickersSearchGridAdapter.search(str, z);
            } else if (i == 1) {
                EmojiView.this.emojiSearchAdapter.search(str, z);
            } else if (i == 2) {
                EmojiView.this.gifSearchAdapter.search(str, z);
            }
        }

        public void showProgress(boolean z) {
            this.isprogress = z;
            if (z) {
                this.searchStateDrawable.setIconState(2);
            } else {
                updateButton(true);
            }
        }
    }

    public class StickersGridAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private int stickersPerRow;
        private int totalItems;
        private SparseArray rowStartPack = new SparseArray();
        private HashMap packStartPosition = new HashMap();
        private SparseArray cache = new SparseArray();
        private SparseArray cacheParents = new SparseArray();
        private SparseIntArray positionToRow = new SparseIntArray();

        class AnonymousClass1 extends StickerEmojiCell {
            AnonymousClass1(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
                super(context, z, resourcesProvider);
            }

            @Override
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), 1073741824));
            }
        }

        class AnonymousClass2 extends RecyclerView.ItemDecoration {
            AnonymousClass2() {
            }

            @Override
            public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                rect.right = AndroidUtilities.dp(2.0f);
            }
        }

        public StickersGridAdapter(Context context) {
            this.context = context;
        }

        public void lambda$onBindViewHolder$6(TLRPC.TL_messages_stickerSet tL_messages_stickerSet, View view) {
            EmojiView.this.delegate.onShowStickerSet(tL_messages_stickerSet.set, null, true);
        }

        public void lambda$onBindViewHolder$7(TLRPC.TL_messages_stickerSet tL_messages_stickerSet, View view) {
            EmojiView.this.delegate.onShowStickerSet(tL_messages_stickerSet.set, null, false);
        }

        public void lambda$onCreateViewHolder$0(DialogInterface dialogInterface, int i) {
            MediaDataController.getInstance(EmojiView.this.currentAccount).clearRecentStickers();
        }

        public void lambda$onCreateViewHolder$1(StickerSetNameCell stickerSetNameCell, View view) {
            RecyclerView.ViewHolder childViewHolder;
            if (EmojiView.this.stickersGridView.indexOfChild(stickerSetNameCell) == -1 || (childViewHolder = EmojiView.this.stickersGridView.getChildViewHolder(stickerSetNameCell)) == null) {
                return;
            }
            if (childViewHolder.getAdapterPosition() != EmojiView.this.groupStickerPackPosition) {
                if (this.cache.get(childViewHolder.getAdapterPosition()) == EmojiView.this.recentStickers) {
                    AlertDialog create = new AlertDialog.Builder(this.context).setTitle(LocaleController.getString(R.string.ClearRecentStickersAlertTitle)).setMessage(LocaleController.getString(R.string.ClearRecentStickersAlertMessage)).setPositiveButton(LocaleController.getString(R.string.ClearButton), new DialogInterface.OnClickListener() {
                        @Override
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$0(dialogInterface, i);
                        }
                    }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
                    create.show();
                    TextView textView = (TextView) create.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                        return;
                    }
                    return;
                }
                return;
            }
            if (EmojiView.this.groupStickerSet != null) {
                if (EmojiView.this.delegate != null) {
                    EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
                    return;
                }
                return;
            }
            MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit().putLong("group_hide_stickers_" + EmojiView.this.info.id, EmojiView.this.info.stickerset != null ? EmojiView.this.info.stickerset.id : 0L).apply();
            EmojiView.this.updateStickerTabs(false);
            if (EmojiView.this.stickersGridAdapter != null) {
                EmojiView.this.stickersGridAdapter.notifyDataSetChanged();
            }
        }

        public void lambda$onCreateViewHolder$2(View view) {
            if (EmojiView.this.delegate != null) {
                EmojiView.this.delegate.onStickersGroupClick(EmojiView.this.info.id);
            }
        }

        public void lambda$onCreateViewHolder$3(View view) {
            ArrayList<TLRPC.StickerSetCovered> featuredStickerSets = MediaDataController.getInstance(EmojiView.this.currentAccount).getFeaturedStickerSets();
            if (featuredStickerSets.isEmpty()) {
                return;
            }
            MessagesController.getEmojiSettings(EmojiView.this.currentAccount).edit().putLong("featured_hidden", featuredStickerSets.get(0).set.id).commit();
            if (EmojiView.this.stickersGridAdapter != null) {
                EmojiView.this.stickersGridAdapter.notifyItemRangeRemoved(1, 2);
            }
            EmojiView.this.updateStickerTabs(false);
        }

        public void lambda$onCreateViewHolder$4(View view, int i) {
            EmojiView.this.openTrendingStickers((TLRPC.StickerSetCovered) view.getTag());
        }

        public void lambda$onCreateViewHolder$5(View view) {
            if (EmojiView.this.fragment instanceof ChatActivity) {
                ((ChatActivity) EmojiView.this.fragment).openAttachMenuForCreatingSticker();
            }
        }

        private void updateItems() {
            Object obj;
            ArrayList<TLRPC.Document> arrayList;
            int i;
            if (EmojiView.this.frozen) {
                return;
            }
            int measuredWidth = EmojiView.this.getMeasuredWidth();
            if (measuredWidth == 0) {
                measuredWidth = AndroidUtilities.displaySize.x;
            }
            this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
            EmojiView.this.stickersLayoutManager.setSpanCount(this.stickersPerRow);
            this.rowStartPack.clear();
            this.packStartPosition.clear();
            this.positionToRow.clear();
            this.cache.clear();
            int i2 = 0;
            this.totalItems = 0;
            ArrayList arrayList2 = EmojiView.this.stickerSets;
            int i3 = -5;
            int i4 = -5;
            int i5 = 0;
            boolean z = false;
            while (i4 < arrayList2.size()) {
                if (i4 == i3) {
                    SparseArray sparseArray = this.cache;
                    int i6 = this.totalItems;
                    this.totalItems = i6 + 1;
                    sparseArray.put(i6, "search");
                    i5++;
                } else if (i4 == -4) {
                    MediaDataController mediaDataController = MediaDataController.getInstance(EmojiView.this.currentAccount);
                    SharedPreferences emojiSettings = MessagesController.getEmojiSettings(EmojiView.this.currentAccount);
                    ArrayList<TLRPC.StickerSetCovered> featuredStickerSets = mediaDataController.getFeaturedStickerSets();
                    if (!EmojiView.this.featuredStickerSets.isEmpty() && emojiSettings.getLong("featured_hidden", 0L) != featuredStickerSets.get(i2).set.id) {
                        SparseArray sparseArray2 = this.cache;
                        int i7 = this.totalItems;
                        this.totalItems = i7 + 1;
                        sparseArray2.put(i7, "trend1");
                        SparseArray sparseArray3 = this.cache;
                        int i8 = this.totalItems;
                        this.totalItems = i8 + 1;
                        sparseArray3.put(i8, "trend2");
                        i5 += 2;
                    }
                } else {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet = null;
                    if (i4 == -3) {
                        arrayList = EmojiView.this.favouriteStickers;
                        this.packStartPosition.put("fav", Integer.valueOf(this.totalItems));
                        obj = "fav";
                    } else if (i4 == -2) {
                        arrayList = EmojiView.this.recentStickers;
                        if (!arrayList.isEmpty()) {
                            z = true;
                        }
                        this.packStartPosition.put("recent", Integer.valueOf(this.totalItems));
                        obj = "recent";
                    } else if (i4 != -1) {
                        TLRPC.TL_messages_stickerSet tL_messages_stickerSet2 = (TLRPC.TL_messages_stickerSet) arrayList2.get(i4);
                        ArrayList<TLRPC.Document> arrayList3 = tL_messages_stickerSet2.documents;
                        if (!z) {
                            ArrayList<TLRPC.Document> arrayList4 = new ArrayList<>(arrayList3);
                            arrayList4.add(i2, new TLRPC.TL_documentEmpty());
                            arrayList3 = arrayList4;
                            z = true;
                        }
                        this.packStartPosition.put(tL_messages_stickerSet2, Integer.valueOf(this.totalItems));
                        obj = null;
                        tL_messages_stickerSet = tL_messages_stickerSet2;
                        arrayList = arrayList3;
                    }
                    if (i4 == EmojiView.this.groupStickerPackNum) {
                        EmojiView.this.groupStickerPackPosition = this.totalItems;
                        if (arrayList.isEmpty()) {
                            this.rowStartPack.put(i5, tL_messages_stickerSet);
                            int i9 = i5 + 1;
                            this.positionToRow.put(this.totalItems, i5);
                            this.rowStartPack.put(i9, tL_messages_stickerSet);
                            i5 += 2;
                            this.positionToRow.put(this.totalItems + 1, i9);
                            SparseArray sparseArray4 = this.cache;
                            int i10 = this.totalItems;
                            this.totalItems = i10 + 1;
                            sparseArray4.put(i10, tL_messages_stickerSet);
                            SparseArray sparseArray5 = this.cache;
                            int i11 = this.totalItems;
                            this.totalItems = i11 + 1;
                            sparseArray5.put(i11, "group");
                        }
                    }
                    if (!arrayList.isEmpty()) {
                        int ceil = (int) Math.ceil(arrayList.size() / this.stickersPerRow);
                        if (tL_messages_stickerSet != null) {
                            this.cache.put(this.totalItems, tL_messages_stickerSet);
                        } else {
                            this.cache.put(this.totalItems, arrayList);
                        }
                        this.positionToRow.put(this.totalItems, i5);
                        int i12 = 0;
                        while (i12 < arrayList.size()) {
                            int i13 = i12 + 1;
                            int i14 = this.totalItems + i13;
                            this.cache.put(i14, arrayList.get(i12));
                            SparseArray sparseArray6 = this.cacheParents;
                            if (tL_messages_stickerSet != null) {
                                sparseArray6.put(i14, tL_messages_stickerSet);
                            } else {
                                sparseArray6.put(i14, obj);
                            }
                            this.positionToRow.put(this.totalItems + i13, i5 + 1 + (i12 / this.stickersPerRow));
                            i12 = i13;
                            obj = obj;
                        }
                        int i15 = 0;
                        while (true) {
                            i = ceil + 1;
                            if (i15 >= i) {
                                break;
                            }
                            if (tL_messages_stickerSet != null) {
                                this.rowStartPack.put(i5 + i15, tL_messages_stickerSet);
                            } else if (i4 == -1) {
                                this.rowStartPack.put(i5 + i15, "premium");
                            } else {
                                SparseArray sparseArray7 = this.rowStartPack;
                                int i16 = i5 + i15;
                                if (i4 == -2) {
                                    sparseArray7.put(i16, "recent");
                                } else {
                                    sparseArray7.put(i16, "fav");
                                }
                                i15++;
                            }
                            i15++;
                        }
                        this.totalItems += (ceil * this.stickersPerRow) + 1;
                        i5 += i;
                    }
                }
                i4++;
                i2 = 0;
                i3 = -5;
            }
        }

        @Override
        public int getItemCount() {
            int i = this.totalItems;
            if (i != 0) {
                return i + 1;
            }
            return 0;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == 0) {
                return 4;
            }
            Object obj = this.cache.get(i);
            if (obj == null) {
                return 1;
            }
            if (obj instanceof TLRPC.Document) {
                return obj instanceof TLRPC.TL_documentEmpty ? 7 : 0;
            }
            if (!(obj instanceof String)) {
                return 2;
            }
            if ("trend1".equals(obj)) {
                return 5;
            }
            return "trend2".equals(obj) ? 6 : 3;
        }

        public int getPositionForPack(Object obj) {
            Integer num = (Integer) this.packStartPosition.get(obj);
            if (num == null) {
                return -1;
            }
            return num.intValue();
        }

        public int getTabForPosition(int i) {
            Object obj = this.cache.get(i);
            if ("search".equals(obj) || "trend1".equals(obj) || "trend2".equals(obj)) {
                if (EmojiView.this.favTabNum >= 0) {
                    return EmojiView.this.favTabNum;
                }
                if (EmojiView.this.recentTabNum >= 0) {
                    return EmojiView.this.recentTabNum;
                }
                return 0;
            }
            if (i == 0) {
                i = 1;
            }
            if (this.stickersPerRow == 0) {
                int measuredWidth = EmojiView.this.getMeasuredWidth();
                if (measuredWidth == 0) {
                    measuredWidth = AndroidUtilities.displaySize.x;
                }
                this.stickersPerRow = measuredWidth / AndroidUtilities.dp(72.0f);
            }
            int i2 = this.positionToRow.get(i, Integer.MIN_VALUE);
            if (i2 == Integer.MIN_VALUE) {
                return (EmojiView.this.stickerSets.size() - 1) + EmojiView.this.stickersTabOffset;
            }
            Object obj2 = this.rowStartPack.get(i2);
            if (obj2 instanceof String) {
                return "premium".equals(obj2) ? EmojiView.this.premiumTabNum : "recent".equals(obj2) ? EmojiView.this.recentTabNum : EmojiView.this.favTabNum;
            }
            return EmojiView.this.stickerSets.indexOf((TLRPC.TL_messages_stickerSet) obj2) + EmojiView.this.stickersTabOffset;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.itemView instanceof RecyclerListView;
        }

        @Override
        public void notifyDataSetChanged() {
            updateItems();
            super.notifyDataSetChanged();
        }

        @Override
        public void notifyItemRangeRemoved(int i, int i2) {
            updateItems();
            super.notifyItemRangeRemoved(i, i2);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int dp;
            StickerSetNameCell stickerSetNameCell;
            int i2;
            String string;
            int i3;
            int i4;
            int i5 = 1;
            i5 = 1;
            i5 = 1;
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                TLRPC.Document document = (TLRPC.Document) this.cache.get(i);
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) viewHolder.itemView;
                stickerEmojiCell.setSticker(document, this.cacheParents.get(i), false);
                stickerEmojiCell.setRecent(EmojiView.this.recentStickers.contains(document));
                return;
            }
            ArrayList<TLRPC.Document> arrayList = null;
            if (itemViewType == 1) {
                EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                if (i == this.totalItems) {
                    int i6 = this.positionToRow.get(i - 1, Integer.MIN_VALUE);
                    if (i6 != Integer.MIN_VALUE) {
                        Object obj = this.rowStartPack.get(i6);
                        if (obj instanceof TLRPC.TL_messages_stickerSet) {
                            arrayList = ((TLRPC.TL_messages_stickerSet) obj).documents;
                        } else if (obj instanceof String) {
                            arrayList = "recent".equals(obj) ? EmojiView.this.recentStickers : EmojiView.this.favouriteStickers;
                        }
                        if (arrayList != null) {
                            if (arrayList.isEmpty()) {
                                dp = AndroidUtilities.dp(8.0f);
                            } else {
                                int height = EmojiView.this.pager.getHeight() - (((int) Math.ceil(arrayList.size() / this.stickersPerRow)) * AndroidUtilities.dp(82.0f));
                                if (height > 0) {
                                    i5 = height;
                                }
                            }
                        }
                    }
                    emptyCell.setHeight(i5);
                    return;
                }
                dp = AndroidUtilities.dp(82.0f);
                emptyCell.setHeight(dp);
                return;
            }
            if (itemViewType == 2) {
                stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                stickerSetNameCell.setHeaderOnClick(null);
                if (i == EmojiView.this.groupStickerPackPosition) {
                    int i7 = (EmojiView.this.groupStickersHidden && EmojiView.this.groupStickerSet == null) ? 0 : EmojiView.this.groupStickerSet != null ? R.drawable.msg_mini_customize : R.drawable.msg_close;
                    TLRPC.Chat chat = EmojiView.this.info != null ? MessagesController.getInstance(EmojiView.this.currentAccount).getChat(Long.valueOf(EmojiView.this.info.id)) : null;
                    stickerSetNameCell.setText(LocaleController.formatString("CurrentGroupStickers", R.string.CurrentGroupStickers, chat != null ? chat.title : "Group Stickers"), i7);
                    return;
                }
                Object obj2 = this.cache.get(i);
                if (obj2 instanceof TLRPC.TL_messages_stickerSet) {
                    final TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) obj2;
                    TLRPC.StickerSet stickerSet = tL_messages_stickerSet.set;
                    if (stickerSet != null) {
                        stickerSetNameCell.setText(stickerSet.title, 0);
                        if (tL_messages_stickerSet.set.creator) {
                            stickerSetNameCell.setEdit(new View.OnClickListener() {
                                @Override
                                public final void onClick(View view) {
                                    EmojiView.StickersGridAdapter.this.lambda$onBindViewHolder$6(tL_messages_stickerSet, view);
                                }
                            });
                        }
                        stickerSetNameCell.setHeaderOnClick(new View.OnClickListener() {
                            @Override
                            public final void onClick(View view) {
                                EmojiView.StickersGridAdapter.this.lambda$onBindViewHolder$7(tL_messages_stickerSet, view);
                            }
                        });
                        return;
                    }
                    return;
                }
                if (obj2 != EmojiView.this.recentStickers) {
                    if (obj2 == EmojiView.this.favouriteStickers) {
                        i2 = R.string.FavoriteStickers;
                    } else if (obj2 != EmojiView.this.premiumStickers) {
                        return;
                    } else {
                        i2 = R.string.PremiumStickers;
                    }
                    stickerSetNameCell.setText(LocaleController.getString(i2), 0);
                    return;
                }
                string = LocaleController.getString(R.string.RecentStickers);
                i3 = R.drawable.msg_close;
                i4 = R.string.ClearRecentStickersAlertTitle;
            } else if (itemViewType == 3) {
                ((StickerSetGroupInfoCell) viewHolder.itemView).setIsLast(i == this.totalItems - 1);
                return;
            } else {
                if (itemViewType != 5) {
                    return;
                }
                stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                string = LocaleController.getString(MediaDataController.getInstance(EmojiView.this.currentAccount).loadFeaturedPremium ? R.string.FeaturedStickersPremium : R.string.FeaturedStickers);
                i3 = R.drawable.msg_close;
                i4 = R.string.AccDescrCloseTrendingStickers;
            }
            stickerSetNameCell.setText(string, i3, LocaleController.getString(i4));
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View.OnClickListener onClickListener;
            ViewGroup.LayoutParams layoutParams;
            StickerSetNameCell stickerSetNameCell;
            FrameLayout frameLayout;
            FrameLayout frameLayout2;
            switch (i) {
                case 0:
                    frameLayout2 = new StickerEmojiCell(this.context, true, EmojiView.this.resourcesProvider) {
                        AnonymousClass1(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
                            super(context, z, resourcesProvider);
                        }

                        @Override
                        public void onMeasure(int i2, int i22) {
                            super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), 1073741824));
                        }
                    };
                    break;
                case 1:
                    frameLayout2 = new EmptyCell(this.context);
                    break;
                case 2:
                    final StickerSetNameCell stickerSetNameCell2 = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    onClickListener = new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$1(stickerSetNameCell2, view);
                        }
                    };
                    stickerSetNameCell = stickerSetNameCell2;
                    stickerSetNameCell.setOnIconClickListener(onClickListener);
                    frameLayout2 = stickerSetNameCell;
                    break;
                case 3:
                    ?? stickerSetGroupInfoCell = new StickerSetGroupInfoCell(this.context);
                    stickerSetGroupInfoCell.setAddOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$2(view);
                        }
                    });
                    layoutParams = new RecyclerView.LayoutParams(-1, -2);
                    frameLayout = stickerSetGroupInfoCell;
                    frameLayout.setLayoutParams(layoutParams);
                    frameLayout2 = frameLayout;
                    break;
                case 4:
                    ?? view = new View(this.context);
                    layoutParams = new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight);
                    frameLayout = view;
                    frameLayout.setLayoutParams(layoutParams);
                    frameLayout2 = frameLayout;
                    break;
                case 5:
                    StickerSetNameCell stickerSetNameCell3 = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                    onClickListener = new View.OnClickListener() {
                        @Override
                        public final void onClick(View view2) {
                            EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$3(view2);
                        }
                    };
                    stickerSetNameCell = stickerSetNameCell3;
                    stickerSetNameCell.setOnIconClickListener(onClickListener);
                    frameLayout2 = stickerSetNameCell;
                    break;
                case 6:
                    EmojiView emojiView = EmojiView.this;
                    ?? trendingListView = new TrendingListView(this.context, emojiView.trendingAdapter = new TrendingAdapter(false));
                    trendingListView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(8.0f), 0);
                    trendingListView.setClipToPadding(false);
                    trendingListView.addItemDecoration(new RecyclerView.ItemDecoration() {
                        AnonymousClass2() {
                        }

                        @Override
                        public void getItemOffsets(android.graphics.Rect rect, View view2, RecyclerView recyclerView, RecyclerView.State state) {
                            rect.right = AndroidUtilities.dp(2.0f);
                        }
                    });
                    trendingListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                        @Override
                        public final void onItemClick(View view2, int i2) {
                            EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$4(view2, i2);
                        }
                    });
                    layoutParams = new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(52.0f));
                    frameLayout = trendingListView;
                    frameLayout.setLayoutParams(layoutParams);
                    frameLayout2 = frameLayout;
                    break;
                case 7:
                    FrameLayout frameLayout3 = new FrameLayout(this.context);
                    View view2 = new View(this.context);
                    int dp = AndroidUtilities.dp(28.0f);
                    EmojiView emojiView2 = EmojiView.this;
                    int i2 = Theme.key_chat_emojiPanelIcon;
                    ShapeDrawable createRoundRectDrawable = Theme.createRoundRectDrawable(dp, Theme.multAlpha(emojiView2.getThemedColor(i2), 0.12f));
                    Drawable mutate = EmojiView.this.getResources().getDrawable(R.drawable.filled_add_sticker).mutate();
                    mutate.setColorFilter(new PorterDuffColorFilter(EmojiView.this.getThemedColor(i2), PorterDuff.Mode.MULTIPLY));
                    CombinedDrawable combinedDrawable = new CombinedDrawable(createRoundRectDrawable, mutate);
                    combinedDrawable.setCustomSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                    combinedDrawable.setIconSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
                    view2.setBackground(combinedDrawable);
                    view2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view3) {
                            EmojiView.StickersGridAdapter.this.lambda$onCreateViewHolder$5(view3);
                        }
                    });
                    ScaleStateListAnimator.apply(view2);
                    frameLayout3.addView(view2, LayoutHelper.createFrame(56, 56, 17));
                    frameLayout2 = frameLayout3;
                    break;
                default:
                    frameLayout2 = null;
                    break;
            }
            return new RecyclerListView.Holder(frameLayout2);
        }
    }

    public class StickersSearchGridAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private int emojiSearchId;
        private int reqId;
        private int reqId2;
        private String searchQuery;
        private int totalItems;
        private SparseArray rowStartPack = new SparseArray();
        private SparseArray cache = new SparseArray();
        private SparseArray cacheParent = new SparseArray();
        private SparseIntArray positionToRow = new SparseIntArray();
        private SparseArray positionToEmoji = new SparseArray();
        private ArrayList serverPacks = new ArrayList();
        private ArrayList localPacks = new ArrayList();
        private HashMap localPacksByShortName = new HashMap();
        private HashMap localPacksByName = new HashMap();
        private HashMap emojiStickers = new HashMap();
        private ArrayList emojiArrays = new ArrayList();
        private SparseArray positionsToSets = new SparseArray();
        private Runnable searchRunnable = new AnonymousClass1();

        public class AnonymousClass1 implements Runnable {
            int lastId;
            String query;
            final ArrayList serverPacks = new ArrayList();
            final ArrayList localPacks = new ArrayList();
            final HashMap localPacksByShortName = new HashMap();
            final HashMap localPacksByName = new HashMap();
            final HashMap emojiStickers = new HashMap();
            final ArrayList emojiArrays = new ArrayList();
            final ArrayList emojiStickersArray = new ArrayList(0);
            final LongSparseArray emojiStickersMap = new LongSparseArray(0);

            AnonymousClass1() {
            }

            public void addFromAllStickers(java.lang.Runnable r11) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.StickersSearchGridAdapter.AnonymousClass1.addFromAllStickers(java.lang.Runnable):void");
            }

            public void addFromSuggestions(final Runnable runnable) {
                final HashMap<String, ArrayList<TLRPC.Document>> allStickers = MediaDataController.getInstance(EmojiView.this.currentAccount).getAllStickers();
                if (allStickers == null || allStickers.isEmpty() || this.query.length() <= 1) {
                    runnable.run();
                    return;
                }
                String[] currentKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                if (!Arrays.equals(EmojiView.this.lastSearchKeyboardLanguage, currentKeyboardLanguage)) {
                    MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(currentKeyboardLanguage);
                }
                EmojiView.this.lastSearchKeyboardLanguage = currentKeyboardLanguage;
                MediaDataController.getInstance(EmojiView.this.currentAccount).getEmojiSuggestions(EmojiView.this.lastSearchKeyboardLanguage, StickersSearchGridAdapter.this.searchQuery, false, new MediaDataController.KeywordResultCallback() {
                    @Override
                    public final void run(ArrayList arrayList, String str) {
                        EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.lambda$addFromSuggestions$0(allStickers, runnable, arrayList, str);
                    }
                }, false);
            }

            public void addLocalPacks(Runnable runnable) {
                int indexOfIgnoreCase;
                int indexOfIgnoreCase2;
                HashMap hashMap;
                Object obj;
                ArrayList<TLRPC.TL_messages_stickerSet> stickerSets = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSets(0);
                MessagesController.getInstance(EmojiView.this.currentAccount).filterPremiumStickers(stickerSets);
                int size = stickerSets.size();
                for (int i = 0; i < size; i++) {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet = stickerSets.get(i);
                    int indexOfIgnoreCase3 = AndroidUtilities.indexOfIgnoreCase(tL_messages_stickerSet.set.title, StickersSearchGridAdapter.this.searchQuery);
                    if (indexOfIgnoreCase3 < 0) {
                        String str = tL_messages_stickerSet.set.short_name;
                        if (str != null && (indexOfIgnoreCase2 = AndroidUtilities.indexOfIgnoreCase(str, StickersSearchGridAdapter.this.searchQuery)) >= 0 && (indexOfIgnoreCase2 == 0 || tL_messages_stickerSet.set.short_name.charAt(indexOfIgnoreCase2 - 1) == ' ')) {
                            this.localPacks.add(tL_messages_stickerSet);
                            hashMap = this.localPacksByShortName;
                            obj = Boolean.TRUE;
                            hashMap.put(tL_messages_stickerSet, obj);
                        }
                    } else if (indexOfIgnoreCase3 == 0 || tL_messages_stickerSet.set.title.charAt(indexOfIgnoreCase3 - 1) == ' ') {
                        this.localPacks.add(tL_messages_stickerSet);
                        hashMap = this.localPacksByName;
                        obj = Integer.valueOf(indexOfIgnoreCase3);
                        hashMap.put(tL_messages_stickerSet, obj);
                    }
                }
                ArrayList<TLRPC.TL_messages_stickerSet> stickerSets2 = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSets(3);
                MessagesController.getInstance(EmojiView.this.currentAccount).filterPremiumStickers(stickerSets2);
                int size2 = stickerSets2.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet2 = stickerSets2.get(i2);
                    int indexOfIgnoreCase4 = AndroidUtilities.indexOfIgnoreCase(tL_messages_stickerSet2.set.title, StickersSearchGridAdapter.this.searchQuery);
                    if (indexOfIgnoreCase4 < 0) {
                        String str2 = tL_messages_stickerSet2.set.short_name;
                        if (str2 != null && (indexOfIgnoreCase = AndroidUtilities.indexOfIgnoreCase(str2, StickersSearchGridAdapter.this.searchQuery)) >= 0 && (indexOfIgnoreCase == 0 || tL_messages_stickerSet2.set.short_name.charAt(indexOfIgnoreCase - 1) == ' ')) {
                            this.localPacks.add(tL_messages_stickerSet2);
                            this.localPacksByShortName.put(tL_messages_stickerSet2, Boolean.TRUE);
                        }
                    } else if (indexOfIgnoreCase4 == 0 || tL_messages_stickerSet2.set.title.charAt(indexOfIgnoreCase4 - 1) == ' ') {
                        this.localPacks.add(tL_messages_stickerSet2);
                        this.localPacksByName.put(tL_messages_stickerSet2, Integer.valueOf(indexOfIgnoreCase4));
                    }
                }
                runnable.run();
            }

            public void addPremiumStickers(Runnable runnable) {
                HashMap<String, ArrayList<TLRPC.Document>> allStickers = MediaDataController.getInstance(EmojiView.this.currentAccount).getAllStickers();
                HashSet hashSet = new HashSet();
                ArrayList arrayList = new ArrayList();
                Iterator<ArrayList<TLRPC.Document>> it = allStickers.values().iterator();
                while (it.hasNext()) {
                    Iterator<TLRPC.Document> it2 = it.next().iterator();
                    while (it2.hasNext()) {
                        TLRPC.Document next = it2.next();
                        if (!hashSet.contains(Long.valueOf(next.id)) && MessageObject.isPremiumSticker(next)) {
                            hashSet.add(Long.valueOf(next.id));
                            arrayList.add(next);
                            this.emojiStickersMap.put(next.id, next);
                        }
                    }
                }
                Iterator<TLRPC.StickerSetCovered> it3 = MediaDataController.getInstance(EmojiView.this.currentAccount).getFeaturedStickerSets().iterator();
                while (it3.hasNext()) {
                    TLRPC.StickerSetCovered next2 = it3.next();
                    TLRPC.Document document = next2.cover;
                    if (document != null && !hashSet.contains(Long.valueOf(document.id)) && MessageObject.isPremiumSticker(next2.cover)) {
                        hashSet.add(Long.valueOf(next2.cover.id));
                        arrayList.add(next2.cover);
                        LongSparseArray longSparseArray = this.emojiStickersMap;
                        TLRPC.Document document2 = next2.cover;
                        longSparseArray.put(document2.id, document2);
                    }
                    ArrayList<TLRPC.Document> arrayList2 = next2.covers;
                    if (arrayList2 != null) {
                        Iterator<TLRPC.Document> it4 = arrayList2.iterator();
                        while (it4.hasNext()) {
                            TLRPC.Document next3 = it4.next();
                            if (!hashSet.contains(Long.valueOf(next3.id)) && MessageObject.isPremiumSticker(next3)) {
                                hashSet.add(Long.valueOf(next3.id));
                                arrayList.add(next3);
                                this.emojiStickersMap.put(next3.id, next3);
                            }
                        }
                    }
                }
                if (!arrayList.isEmpty()) {
                    this.emojiStickersArray.addAll(arrayList);
                    this.emojiStickers.put(this.emojiStickersArray, StickersSearchGridAdapter.this.searchQuery);
                    this.emojiArrays.add(this.emojiStickersArray);
                }
                runnable.run();
            }

            public void lambda$addFromSuggestions$0(HashMap hashMap, Runnable runnable, ArrayList arrayList, String str) {
                if (StickersSearchGridAdapter.this.emojiSearchId != this.lastId) {
                    return;
                }
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    String str2 = ((MediaDataController.KeywordResult) arrayList.get(i)).emoji;
                    ArrayList arrayList2 = (ArrayList) hashMap.get(str2);
                    if (arrayList2 != null && !arrayList2.isEmpty() && !this.emojiStickers.containsKey(arrayList2)) {
                        this.emojiStickers.put(arrayList2, str2);
                        this.emojiArrays.add(arrayList2);
                    }
                }
                runnable.run();
            }

            public void lambda$searchStickerSets$1(Runnable runnable, ArrayList arrayList) {
                if (StickersSearchGridAdapter.this.emojiSearchId != this.lastId) {
                    return;
                }
                this.emojiStickersArray.addAll(arrayList);
                Iterator it = arrayList.iterator();
                while (it.hasNext()) {
                    TLRPC.Document document = (TLRPC.Document) it.next();
                    this.emojiStickersMap.put(document.id, document);
                }
                this.emojiStickers.put(this.emojiStickersArray, StickersSearchGridAdapter.this.searchQuery);
                this.emojiArrays.add(this.emojiStickersArray);
                runnable.run();
            }

            public void lambda$searchStickers$2(TLRPC.TL_messages_getStickers tL_messages_getStickers, TLObject tLObject, Runnable runnable) {
                if (StickersSearchGridAdapter.this.emojiSearchId != this.lastId) {
                    return;
                }
                StickersSearchGridAdapter.this.reqId2 = 0;
                if (tL_messages_getStickers.emoticon.equals(this.query)) {
                    if (!(tLObject instanceof TLRPC.TL_messages_stickers)) {
                        runnable.run();
                        return;
                    }
                    TLRPC.TL_messages_stickers tL_messages_stickers = (TLRPC.TL_messages_stickers) tLObject;
                    int size = this.emojiStickersArray.size();
                    int size2 = tL_messages_stickers.stickers.size();
                    for (int i = 0; i < size2; i++) {
                        TLRPC.Document document = tL_messages_stickers.stickers.get(i);
                        if (this.emojiStickersMap.indexOfKey(document.id) < 0) {
                            this.emojiStickersArray.add(document);
                        }
                    }
                    if (size != this.emojiStickersArray.size()) {
                        this.emojiStickers.put(this.emojiStickersArray, StickersSearchGridAdapter.this.searchQuery);
                        if (size == 0) {
                            this.emojiArrays.add(this.emojiStickersArray);
                        }
                    }
                }
                runnable.run();
            }

            public void lambda$searchStickers$3(final TLRPC.TL_messages_getStickers tL_messages_getStickers, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.lambda$searchStickers$2(tL_messages_getStickers, tLObject, runnable);
                    }
                });
            }

            public void searchFinish() {
                if (StickersSearchGridAdapter.this.emojiSearchId != this.lastId) {
                    return;
                }
                StickersSearchGridAdapter.this.localPacks = this.localPacks;
                StickersSearchGridAdapter.this.serverPacks = this.serverPacks;
                StickersSearchGridAdapter.this.localPacksByShortName = this.localPacksByShortName;
                StickersSearchGridAdapter.this.localPacksByName = this.localPacksByName;
                StickersSearchGridAdapter.this.emojiStickers = this.emojiStickers;
                StickersSearchGridAdapter.this.emojiArrays = this.emojiArrays;
                EmojiView.this.stickersSearchField.showProgress(false);
                if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersSearchGridAdapter) {
                    EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersSearchGridAdapter);
                }
                StickersSearchGridAdapter.this.notifyDataSetChanged();
            }

            public void searchStickerSets(final Runnable runnable) {
                String[] currentKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                MediaDataController.getInstance(EmojiView.this.currentAccount).searchStickers(false, (currentKeyboardLanguage == null || currentKeyboardLanguage.length == 0) ? "" : currentKeyboardLanguage[0], this.query, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.lambda$searchStickerSets$1(runnable, (ArrayList) obj);
                    }
                });
            }

            public void searchStickers(final Runnable runnable) {
                if (!Emoji.fullyConsistsOfEmojis(StickersSearchGridAdapter.this.searchQuery)) {
                    runnable.run();
                    return;
                }
                final TLRPC.TL_messages_getStickers tL_messages_getStickers = new TLRPC.TL_messages_getStickers();
                tL_messages_getStickers.emoticon = this.query;
                tL_messages_getStickers.hash = 0L;
                StickersSearchGridAdapter stickersSearchGridAdapter = StickersSearchGridAdapter.this;
                stickersSearchGridAdapter.reqId2 = ConnectionsManager.getInstance(EmojiView.this.currentAccount).sendRequest(tL_messages_getStickers, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.lambda$searchStickers$3(tL_messages_getStickers, runnable, tLObject, tL_error);
                    }
                });
            }

            @Override
            public void run() {
                if (TextUtils.isEmpty(StickersSearchGridAdapter.this.searchQuery)) {
                    if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersGridAdapter) {
                        EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersGridAdapter);
                    }
                    StickersSearchGridAdapter.this.notifyDataSetChanged();
                    return;
                }
                this.lastId = StickersSearchGridAdapter.access$19104(StickersSearchGridAdapter.this);
                this.query = StickersSearchGridAdapter.this.searchQuery;
                this.serverPacks.clear();
                this.localPacks.clear();
                this.localPacksByShortName.clear();
                this.localPacksByName.clear();
                this.emojiStickers.clear();
                this.emojiArrays.clear();
                this.emojiStickersArray.clear();
                this.emojiStickersMap.clear();
                EmojiView.this.stickersSearchField.showProgress(true);
                if ("premium".equalsIgnoreCase(this.query)) {
                    Utilities.raceCallbacks(new Runnable() {
                        @Override
                        public final void run() {
                            EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.searchFinish();
                        }
                    }, new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.addPremiumStickers((Runnable) obj);
                        }
                    });
                } else {
                    Utilities.raceCallbacks(new Runnable() {
                        @Override
                        public final void run() {
                            EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.searchFinish();
                        }
                    }, new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.addFromAllStickers((Runnable) obj);
                        }
                    }, new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.addFromSuggestions((Runnable) obj);
                        }
                    }, new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.addLocalPacks((Runnable) obj);
                        }
                    }, new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.searchStickerSets((Runnable) obj);
                        }
                    }, new Utilities.Callback() {
                        @Override
                        public final void run(Object obj) {
                            EmojiView.StickersSearchGridAdapter.AnonymousClass1.this.searchStickers((Runnable) obj);
                        }
                    });
                }
            }
        }

        class AnonymousClass2 extends StickerEmojiCell {
            AnonymousClass2(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
                super(context, z, resourcesProvider);
            }

            @Override
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), 1073741824));
            }
        }

        class AnonymousClass3 extends FrameLayout {
            AnonymousClass3(Context context) {
                super(context);
            }

            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec((int) ((((EmojiView.this.stickersGridView.getMeasuredHeight() - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3) * 1.7f), 1073741824));
            }
        }

        public StickersSearchGridAdapter(Context context) {
            this.context = context;
        }

        static int access$19104(StickersSearchGridAdapter stickersSearchGridAdapter) {
            int i = stickersSearchGridAdapter.emojiSearchId + 1;
            stickersSearchGridAdapter.emojiSearchId = i;
            return i;
        }

        public void lambda$onCreateViewHolder$0(View view) {
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) view.getParent();
            TLRPC.StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
            if (EmojiView.this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0 || EmojiView.this.removingStickerSets.indexOfKey(stickerSet.set.id) >= 0) {
                return;
            }
            if (featuredStickerSetInfoCell.isInstalled()) {
                EmojiView.this.removingStickerSets.put(stickerSet.set.id, stickerSet);
                EmojiView.this.delegate.onStickerSetRemove(featuredStickerSetInfoCell.getStickerSet());
            } else {
                featuredStickerSetInfoCell.setAddDrawProgress(true, true);
                EmojiView.this.installingStickerSets.put(stickerSet.set.id, stickerSet);
                EmojiView.this.delegate.onStickerSetAdd(featuredStickerSetInfoCell.getStickerSet());
            }
        }

        @Override
        public int getItemCount() {
            int i = this.totalItems;
            if (i != 1) {
                return i + 1;
            }
            return 2;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == 0) {
                return 4;
            }
            if (i == 1 && this.totalItems == 1) {
                return 5;
            }
            Object obj = this.cache.get(i);
            if (obj == null) {
                return 1;
            }
            if (obj instanceof TLRPC.Document) {
                return 0;
            }
            return obj instanceof TLRPC.StickerSetCovered ? 3 : 2;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        @Override
        public void notifyDataSetChanged() {
            int i;
            ArrayList<TLRPC.Document> arrayList;
            TLRPC.StickerSetCovered stickerSetCovered;
            this.rowStartPack.clear();
            this.positionToRow.clear();
            this.cache.clear();
            this.positionsToSets.clear();
            this.positionToEmoji.clear();
            this.totalItems = 0;
            int size = this.serverPacks.size();
            int size2 = this.localPacks.size();
            int i2 = !this.emojiArrays.isEmpty() ? 1 : 0;
            int i3 = -1;
            int i4 = -1;
            int i5 = 0;
            while (i4 < size + size2 + i2) {
                if (i4 == i3) {
                    SparseArray sparseArray = this.cache;
                    int i6 = this.totalItems;
                    this.totalItems = i6 + 1;
                    sparseArray.put(i6, "search");
                    i5++;
                    i = size;
                } else {
                    if (i4 < size2) {
                        ?? r8 = (TLRPC.TL_messages_stickerSet) this.localPacks.get(i4);
                        arrayList = r8.documents;
                        i = size;
                        stickerSetCovered = r8;
                    } else {
                        int i7 = i4 - size2;
                        if (i7 < i2) {
                            int size3 = this.emojiArrays.size();
                            String str = "";
                            int i8 = 0;
                            for (int i9 = 0; i9 < size3; i9++) {
                                ArrayList arrayList2 = (ArrayList) this.emojiArrays.get(i9);
                                String str2 = (String) this.emojiStickers.get(arrayList2);
                                if (str2 != null && !str.equals(str2)) {
                                    this.positionToEmoji.put(this.totalItems + i8, str2);
                                    str = str2;
                                }
                                int size4 = arrayList2.size();
                                int i10 = 0;
                                while (i10 < size4) {
                                    int i11 = this.totalItems + i8;
                                    int i12 = (i8 / EmojiView.this.stickersGridAdapter.stickersPerRow) + i5;
                                    TLRPC.Document document = (TLRPC.Document) arrayList2.get(i10);
                                    int i13 = size;
                                    this.cache.put(i11, document);
                                    int i14 = size3;
                                    String str3 = str;
                                    TLRPC.TL_messages_stickerSet stickerSetById = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSetById(MediaDataController.getStickerSetId(document));
                                    if (stickerSetById != null) {
                                        this.cacheParent.put(i11, stickerSetById);
                                    }
                                    this.positionToRow.put(i11, i12);
                                    i8++;
                                    i10++;
                                    size = i13;
                                    size3 = i14;
                                    str = str3;
                                }
                            }
                            i = size;
                            int ceil = (int) Math.ceil(i8 / EmojiView.this.stickersGridAdapter.stickersPerRow);
                            for (int i15 = 0; i15 < ceil; i15++) {
                                this.rowStartPack.put(i5 + i15, Integer.valueOf(i8));
                            }
                            this.totalItems += EmojiView.this.stickersGridAdapter.stickersPerRow * ceil;
                            i5 += ceil;
                        } else {
                            i = size;
                            TLRPC.StickerSetCovered stickerSetCovered2 = (TLRPC.StickerSetCovered) this.serverPacks.get(i7 - i2);
                            arrayList = stickerSetCovered2.covers;
                            stickerSetCovered = stickerSetCovered2;
                        }
                    }
                    if (!arrayList.isEmpty()) {
                        int ceil2 = (int) Math.ceil(arrayList.size() / EmojiView.this.stickersGridAdapter.stickersPerRow);
                        this.cache.put(this.totalItems, stickerSetCovered);
                        if (i4 >= size2 && (stickerSetCovered instanceof TLRPC.StickerSetCovered)) {
                            this.positionsToSets.put(this.totalItems, stickerSetCovered);
                        }
                        this.positionToRow.put(this.totalItems, i5);
                        int size5 = arrayList.size();
                        int i16 = 0;
                        while (i16 < size5) {
                            int i17 = i16 + 1;
                            int i18 = this.totalItems + i17;
                            int i19 = i5 + 1 + (i16 / EmojiView.this.stickersGridAdapter.stickersPerRow);
                            this.cache.put(i18, arrayList.get(i16));
                            this.cacheParent.put(i18, stickerSetCovered);
                            this.positionToRow.put(i18, i19);
                            if (i4 >= size2 && (stickerSetCovered instanceof TLRPC.StickerSetCovered)) {
                                this.positionsToSets.put(i18, stickerSetCovered);
                            }
                            i16 = i17;
                        }
                        int i20 = ceil2 + 1;
                        for (int i21 = 0; i21 < i20; i21++) {
                            this.rowStartPack.put(i5 + i21, stickerSetCovered);
                        }
                        this.totalItems += (ceil2 * EmojiView.this.stickersGridAdapter.stickersPerRow) + 1;
                        i5 += i20;
                    }
                }
                i4++;
                size = i;
                i3 = -1;
            }
            super.notifyDataSetChanged();
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int dp;
            int itemViewType = viewHolder.getItemViewType();
            boolean z = true;
            r1 = 1;
            r1 = 1;
            int i2 = 1;
            z = true;
            if (itemViewType == 0) {
                TLRPC.Document document = (TLRPC.Document) this.cache.get(i);
                StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) viewHolder.itemView;
                stickerEmojiCell.setSticker(document, null, this.cacheParent.get(i), (String) this.positionToEmoji.get(i), false);
                if (!EmojiView.this.recentStickers.contains(document) && !EmojiView.this.favouriteStickers.contains(document)) {
                    z = false;
                }
                stickerEmojiCell.setRecent(z);
                return;
            }
            Integer num = null;
            if (itemViewType == 1) {
                EmptyCell emptyCell = (EmptyCell) viewHolder.itemView;
                if (i == this.totalItems) {
                    int i3 = this.positionToRow.get(i - 1, Integer.MIN_VALUE);
                    if (i3 != Integer.MIN_VALUE) {
                        Object obj = this.rowStartPack.get(i3);
                        if (obj instanceof TLRPC.TL_messages_stickerSet) {
                            num = Integer.valueOf(((TLRPC.TL_messages_stickerSet) obj).documents.size());
                        } else if (obj instanceof Integer) {
                            num = (Integer) obj;
                        }
                        if (num != null) {
                            if (num.intValue() == 0) {
                                dp = AndroidUtilities.dp(8.0f);
                            } else {
                                int height = EmojiView.this.pager.getHeight() - (((int) Math.ceil(num.intValue() / EmojiView.this.stickersGridAdapter.stickersPerRow)) * AndroidUtilities.dp(82.0f));
                                if (height > 0) {
                                    i2 = height;
                                }
                            }
                        }
                    }
                    emptyCell.setHeight(i2);
                    return;
                }
                dp = AndroidUtilities.dp(82.0f);
                emptyCell.setHeight(dp);
                return;
            }
            if (itemViewType == 2) {
                StickerSetNameCell stickerSetNameCell = (StickerSetNameCell) viewHolder.itemView;
                Object obj2 = this.cache.get(i);
                if (obj2 instanceof TLRPC.TL_messages_stickerSet) {
                    TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) obj2;
                    if (!TextUtils.isEmpty(this.searchQuery) && this.localPacksByShortName.containsKey(tL_messages_stickerSet)) {
                        TLRPC.StickerSet stickerSet = tL_messages_stickerSet.set;
                        if (stickerSet != null) {
                            stickerSetNameCell.setText(stickerSet.title, 0);
                        }
                        stickerSetNameCell.setUrl(tL_messages_stickerSet.set.short_name, this.searchQuery.length());
                        return;
                    }
                    Integer num2 = (Integer) this.localPacksByName.get(tL_messages_stickerSet);
                    TLRPC.StickerSet stickerSet2 = tL_messages_stickerSet.set;
                    if (stickerSet2 != null && num2 != null) {
                        stickerSetNameCell.setText(stickerSet2.title, 0, num2.intValue(), !TextUtils.isEmpty(this.searchQuery) ? this.searchQuery.length() : 0);
                    }
                    stickerSetNameCell.setUrl(null, 0);
                    return;
                }
                return;
            }
            if (itemViewType != 3) {
                return;
            }
            TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) this.cache.get(i);
            FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) viewHolder.itemView;
            boolean z2 = EmojiView.this.installingStickerSets.indexOfKey(stickerSetCovered.set.id) >= 0;
            char c = EmojiView.this.removingStickerSets.indexOfKey(stickerSetCovered.set.id) < 0 ? (char) 0 : (char) 1;
            if (z2 || c != 0) {
                if (z2 && featuredStickerSetInfoCell.isInstalled()) {
                    EmojiView.this.installingStickerSets.remove(stickerSetCovered.set.id);
                    z2 = false;
                } else if (c != 0 && !featuredStickerSetInfoCell.isInstalled()) {
                    EmojiView.this.removingStickerSets.remove(stickerSetCovered.set.id);
                }
            }
            featuredStickerSetInfoCell.setAddDrawProgress(z2, false);
            int indexOfIgnoreCase = TextUtils.isEmpty(this.searchQuery) ? -1 : AndroidUtilities.indexOfIgnoreCase(stickerSetCovered.set.title, this.searchQuery);
            if (indexOfIgnoreCase >= 0) {
                featuredStickerSetInfoCell.setStickerSet(stickerSetCovered, false, false, indexOfIgnoreCase, this.searchQuery.length());
                return;
            }
            featuredStickerSetInfoCell.setStickerSet(stickerSetCovered, false);
            if (TextUtils.isEmpty(this.searchQuery) || AndroidUtilities.indexOfIgnoreCase(stickerSetCovered.set.short_name, this.searchQuery) != 0) {
                return;
            }
            featuredStickerSetInfoCell.setUrl(stickerSetCovered.set.short_name, this.searchQuery.length());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout anonymousClass2;
            View view;
            if (i != 0) {
                if (i == 1) {
                    view = new EmptyCell(this.context);
                } else if (i == 2) {
                    view = new StickerSetNameCell(this.context, false, EmojiView.this.resourcesProvider);
                } else if (i == 3) {
                    FeaturedStickerSetInfoCell featuredStickerSetInfoCell = new FeaturedStickerSetInfoCell(this.context, 17, false, true, EmojiView.this.resourcesProvider);
                    featuredStickerSetInfoCell.setAddOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view2) {
                            EmojiView.StickersSearchGridAdapter.this.lambda$onCreateViewHolder$0(view2);
                        }
                    });
                    view = featuredStickerSetInfoCell;
                } else if (i == 4) {
                    View view2 = new View(this.context);
                    view2.setLayoutParams(new RecyclerView.LayoutParams(-1, EmojiView.this.searchFieldHeight));
                    view = view2;
                } else if (i != 5) {
                    view = null;
                } else {
                    anonymousClass2 = new FrameLayout(this.context) {
                        AnonymousClass3(Context context) {
                            super(context);
                        }

                        @Override
                        protected void onMeasure(int i2, int i22) {
                            super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec((int) ((((EmojiView.this.stickersGridView.getMeasuredHeight() - EmojiView.this.searchFieldHeight) - AndroidUtilities.dp(8.0f)) / 3) * 1.7f), 1073741824));
                        }
                    };
                    ImageView imageView = new ImageView(this.context);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    imageView.setImageResource(R.drawable.stickers_empty);
                    EmojiView emojiView = EmojiView.this;
                    int i2 = Theme.key_chat_emojiPanelEmptyText;
                    imageView.setColorFilter(new PorterDuffColorFilter(emojiView.getThemedColor(i2), PorterDuff.Mode.MULTIPLY));
                    imageView.setTranslationY(-AndroidUtilities.dp(24.0f));
                    anonymousClass2.addView(imageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 42.0f, 0.0f, 28.0f));
                    TextView textView = new TextView(this.context);
                    textView.setText(LocaleController.getString(R.string.NoStickersFound));
                    textView.setTextSize(1, 16.0f);
                    textView.setTextColor(EmojiView.this.getThemedColor(i2));
                    anonymousClass2.addView(textView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 42.0f, 0.0f, 9.0f));
                    anonymousClass2.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
                }
                return new RecyclerListView.Holder(view);
            }
            anonymousClass2 = new StickerEmojiCell(this.context, true, EmojiView.this.resourcesProvider) {
                AnonymousClass2(Context context, boolean z, Theme.ResourcesProvider resourcesProvider) {
                    super(context, z, resourcesProvider);
                }

                @Override
                public void onMeasure(int i3, int i22) {
                    super.onMeasure(i3, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(82.0f), 1073741824));
                }
            };
            view = anonymousClass2;
            return new RecyclerListView.Holder(view);
        }

        public void search(String str, boolean z) {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
            if (this.reqId2 != 0) {
                ConnectionsManager.getInstance(EmojiView.this.currentAccount).cancelRequest(this.reqId2, true);
                this.reqId2 = 0;
            }
            if (TextUtils.isEmpty(str)) {
                this.searchQuery = null;
                this.localPacks.clear();
                this.emojiStickers.clear();
                this.serverPacks.clear();
                if (EmojiView.this.stickersGridView.getAdapter() != EmojiView.this.stickersGridAdapter) {
                    EmojiView.this.stickersGridView.setAdapter(EmojiView.this.stickersGridAdapter);
                }
                notifyDataSetChanged();
                EmojiView.this.stickersSearchField.showProgress(false);
            } else {
                this.searchQuery = str.toLowerCase();
                EmojiView.this.stickersSearchField.showProgress(true);
            }
            AndroidUtilities.cancelRunOnUIThread(this.searchRunnable);
            AndroidUtilities.runOnUIThread(this.searchRunnable, 300L);
        }
    }

    public class Tab {
        int type;
        View view;

        private Tab() {
        }

        Tab(EmojiView emojiView, AnonymousClass1 anonymousClass1) {
            this();
        }
    }

    public class TrendingAdapter extends RecyclerListView.SelectionAdapter {
        private boolean emoji;

        class AnonymousClass1 extends BackupImageView {
            AnonymousClass1(Context context) {
                super(context);
            }

            @Override
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (TrendingAdapter.this.emoji) {
                    return;
                }
                if (!MediaDataController.getInstance(EmojiView.this.currentAccount).isStickerPackUnread(TrendingAdapter.this.emoji, ((TLRPC.StickerSetCovered) getTag()).set.id) || EmojiView.this.dotPaint == null) {
                    return;
                }
                canvas.drawCircle(canvas.getWidth() - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(3.0f), EmojiView.this.dotPaint);
            }
        }

        public TrendingAdapter(boolean z) {
            this.emoji = z;
        }

        @Override
        public int getItemCount() {
            return (this.emoji ? EmojiView.this.featuredEmojiSets : EmojiView.this.featuredStickerSets).size();
        }

        @Override
        public int getItemViewType(int i) {
            return 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ArrayList<TLRPC.Document> arrayList;
            ImageLocation forSticker;
            String str;
            String str2;
            ImageLocation imageLocation;
            BackupImageView backupImageView = (BackupImageView) viewHolder.itemView;
            TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) (this.emoji ? EmojiView.this.featuredEmojiSets : EmojiView.this.featuredStickerSets).get(i);
            backupImageView.setTag(stickerSetCovered);
            if (stickerSetCovered instanceof TLRPC.TL_stickerSetFullCovered) {
                arrayList = ((TLRPC.TL_stickerSetFullCovered) stickerSetCovered).documents;
            } else if (stickerSetCovered instanceof TLRPC.TL_stickerSetNoCovered) {
                TLRPC.TL_messages_stickerSet stickerSet = MediaDataController.getInstance(EmojiView.this.currentAccount).getStickerSet(MediaDataController.getInputStickerSet(stickerSetCovered.set), false);
                arrayList = stickerSet == null ? null : stickerSet.documents;
            } else {
                arrayList = stickerSetCovered.covers;
            }
            TLRPC.Document document = stickerSetCovered.cover;
            if (document == null) {
                if (arrayList == null || arrayList.isEmpty()) {
                    document = null;
                } else {
                    if (stickerSetCovered.set != null) {
                        for (int i2 = 0; i2 < arrayList.size(); i2++) {
                            if (arrayList.get(i2).id == stickerSetCovered.set.thumb_document_id) {
                                document = arrayList.get(i2);
                                break;
                            }
                        }
                    }
                    document = null;
                    if (document == null) {
                        document = arrayList.get(0);
                    }
                }
            }
            if (document == null) {
                return;
            }
            if (this.emoji) {
                backupImageView.setColorFilter(MessageObject.isTextColorEmoji(document) ? Theme.getAnimatedEmojiColorFilter(EmojiView.this.resourcesProvider) : null);
            }
            TLObject closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(stickerSetCovered.set.thumbs, 90);
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(stickerSetCovered.set.thumbs, Theme.key_emptyListPlaceholder, 0.2f);
            if (svgThumb != null) {
                svgThumb.overrideWidthAndHeight(512, 512);
            }
            if (closestPhotoSizeWithSize == null || MessageObject.isVideoSticker(document)) {
                closestPhotoSizeWithSize = document;
            }
            boolean z = closestPhotoSizeWithSize instanceof TLRPC.Document;
            if (z) {
                forSticker = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90), document);
            } else if (!(closestPhotoSizeWithSize instanceof TLRPC.PhotoSize)) {
                return;
            } else {
                forSticker = ImageLocation.getForSticker((TLRPC.PhotoSize) closestPhotoSizeWithSize, document, stickerSetCovered.set.thumb_version);
            }
            if (forSticker == null) {
                return;
            }
            String str3 = !LiteMode.isEnabled(this.emoji ? 16388 : 1) ? "30_30_firstframe" : "30_30";
            if (z && (MessageObject.isAnimatedStickerDocument(document, true) || MessageObject.isVideoSticker(document))) {
                ImageLocation forDocument = ImageLocation.getForDocument(document);
                if (svgThumb != null) {
                    backupImageView.setImage(forDocument, str3, svgThumb, 0, stickerSetCovered);
                    return;
                } else {
                    backupImageView.setImage(forDocument, str3, forSticker, (String) null, 0, stickerSetCovered);
                    return;
                }
            }
            if (forSticker.imageType == 1) {
                str2 = "tgs";
                imageLocation = forSticker;
                str = str3;
            } else {
                str = null;
                str2 = "webp";
                imageLocation = forSticker;
            }
            backupImageView.setImage(imageLocation, str, str2, svgThumb, stickerSetCovered);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            AnonymousClass1 anonymousClass1 = new BackupImageView(EmojiView.this.getContext()) {
                AnonymousClass1(Context context) {
                    super(context);
                }

                @Override
                public void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (TrendingAdapter.this.emoji) {
                        return;
                    }
                    if (!MediaDataController.getInstance(EmojiView.this.currentAccount).isStickerPackUnread(TrendingAdapter.this.emoji, ((TLRPC.StickerSetCovered) getTag()).set.id) || EmojiView.this.dotPaint == null) {
                        return;
                    }
                    canvas.drawCircle(canvas.getWidth() - AndroidUtilities.dp(8.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(3.0f), EmojiView.this.dotPaint);
                }
            };
            anonymousClass1.setSize(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
            anonymousClass1.setLayerNum(1);
            anonymousClass1.setAspectFit(true);
            anonymousClass1.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(34.0f), AndroidUtilities.dp(34.0f)));
            return new RecyclerListView.Holder(anonymousClass1);
        }
    }

    public class TrendingListView extends RecyclerListView {

        public class AnonymousClass1 extends LinearLayoutManager {
            final EmojiView val$this$0;

            AnonymousClass1(Context context, EmojiView emojiView) {
                super(context);
                r3 = emojiView;
            }

            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        }

        public TrendingListView(Context context, RecyclerView.Adapter adapter) {
            super(context);
            setNestedScrollingEnabled(true);
            setSelectorRadius(AndroidUtilities.dp(4.0f));
            setSelectorDrawableColor(getThemedColor(Theme.key_listSelector));
            setTag(9);
            setItemAnimator(null);
            setLayoutAnimation(null);
            AnonymousClass1 anonymousClass1 = new LinearLayoutManager(context) {
                final EmojiView val$this$0;

                AnonymousClass1(Context context2, EmojiView emojiView) {
                    super(context2);
                    r3 = emojiView;
                }

                @Override
                public boolean supportsPredictiveItemAnimations() {
                    return false;
                }
            };
            anonymousClass1.setOrientation(0);
            setLayoutManager(anonymousClass1);
            setAdapter(adapter);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (getParent() != null && getParent().getParent() != null) {
                getParent().getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1) || canScrollHorizontally(1));
                EmojiView.this.pager.requestDisallowInterceptTouchEvent(true);
            }
            return super.onInterceptTouchEvent(motionEvent);
        }
    }

    public class TypedScrollListener extends RecyclerView.OnScrollListener {
        private boolean smoothScrolling;
        private final int type;

        public TypedScrollListener(int i) {
            this.type = i;
        }

        private void checkSearchFieldScroll() {
            int i = this.type;
            if (i == 0) {
                EmojiView.this.checkStickersSearchFieldScroll(false);
            } else if (i == 1) {
                EmojiView.this.checkEmojiSearchFieldScroll(false);
            } else {
                if (i != 2) {
                    return;
                }
                EmojiView.this.checkGifSearchFieldScroll(false);
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int i) {
            if (recyclerView.getLayoutManager().isSmoothScrolling()) {
                this.smoothScrolling = true;
                return;
            }
            if (i == 0) {
                if (!this.smoothScrolling) {
                    EmojiView.this.animateTabsY(this.type);
                }
                if (EmojiView.this.ignoreStickersScroll) {
                    EmojiView.this.ignoreStickersScroll = false;
                }
                this.smoothScrolling = false;
                return;
            }
            if (i == 1) {
                if (EmojiView.this.ignoreStickersScroll) {
                    EmojiView.this.ignoreStickersScroll = false;
                }
                SearchField searchFieldForType = EmojiView.this.getSearchFieldForType(this.type);
                if (searchFieldForType != null) {
                    searchFieldForType.hideKeyboard();
                }
                this.smoothScrolling = false;
            }
            if (!this.smoothScrolling) {
                EmojiView.this.stopAnimatingTabsY(this.type);
            }
            if (this.type == 0) {
                if (EmojiView.this.chooseStickerActionTracker == null) {
                    EmojiView.this.createStickersChooseActionTracker();
                }
                EmojiView.this.chooseStickerActionTracker.doSomeAction();
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            EmojiView.this.checkScroll(this.type);
            EmojiView.this.checkTabsY(this.type, i2);
            checkSearchFieldScroll();
            if (this.smoothScrolling) {
                return;
            }
            EmojiView.this.checkBottomTabScroll(i2);
        }
    }

    public EmojiView(BaseFragment baseFragment, boolean z, boolean z2, boolean z3, Context context, boolean z4, TLRPC.ChatFull chatFull, ViewGroup viewGroup, boolean z5, final Theme.ResourcesProvider resourcesProvider, boolean z6) {
        super(context);
        boolean z7;
        Theme.ResourcesProvider resourcesProvider2;
        float f;
        float f2;
        int i;
        float f3;
        int i2;
        float f4;
        float f5;
        Property property;
        int i3;
        int i4;
        final Theme.ResourcesProvider resourcesProvider3;
        ?? r4;
        AnonymousClass1 anonymousClass1;
        View view;
        FrameLayout.LayoutParams createFrame;
        this.emojiCacheType = 2;
        this.allTabs = new ArrayList();
        this.currentTabs = new ArrayList();
        this.firstEmojiAttach = true;
        this.hasRecentEmoji = -1;
        this.gifSearchPreloader = new GifSearchPreloader();
        this.gifCache = new HashMap();
        this.firstGifAttach = true;
        this.gifRecentTabNum = -2;
        this.gifTrendingTabNum = -2;
        this.gifFirstEmojiTabNum = -2;
        this.shouldDrawBackground = true;
        this.shouldLightenBackground = true;
        this.firstStickersAttach = true;
        this.tabsMinusDy = new int[3];
        this.tabsYAnimators = new ObjectAnimator[3];
        this.currentAccount = UserConfig.selectedAccount;
        this.stickerSets = new ArrayList();
        this.recentGifs = new ArrayList();
        this.recentStickers = new ArrayList();
        this.favouriteStickers = new ArrayList();
        this.premiumStickers = new ArrayList();
        this.featuredStickerSets = new ArrayList();
        this.featuredEmojiSets = new ArrayList();
        this.keepFeaturedDuplicate = new ArrayList();
        this.expandedEmojiSets = new ArrayList();
        this.installedEmojiSets = new ArrayList();
        this.emojipacksProcessed = new ArrayList();
        this.toInstall = new HashMap();
        this.primaryInstallingStickerSets = new TLRPC.StickerSetCovered[10];
        this.installingStickerSets = new LongSparseArray();
        this.removingStickerSets = new LongSparseArray();
        this.location = new int[2];
        this.recentTabNum = -2;
        this.favTabNum = -2;
        this.trendingTabNum = -2;
        this.premiumTabNum = -2;
        this.currentBackgroundType = -1;
        this.checkExpandStickerTabsRunnable = new Runnable() {
            AnonymousClass1() {
            }

            @Override
            public void run() {
                if (EmojiView.this.stickersTab.isDragging()) {
                    return;
                }
                EmojiView.this.expandStickersByDragg = false;
                EmojiView.this.updateStickerTabsPosition();
            }
        };
        this.contentPreviewViewerDelegate = new AnonymousClass2();
        this.premiumBulletin = true;
        this.animateExpandFromPosition = -1;
        this.animateExpandToPosition = -1;
        this.animateExpandStartTime = -1L;
        this.emojiPackAlertOpened = false;
        this.fixBottomTabContainerTranslation = true;
        this.rect = new android.graphics.Rect();
        this.updateStickersLoadedDelayed = new Runnable() {
            @Override
            public final void run() {
                EmojiView.this.lambda$new$17();
            }
        };
        this.shouldDrawBackground = z5;
        this.fragment = baseFragment;
        this.allowAnimatedEmoji = z;
        this.resourcesProvider = resourcesProvider;
        if (z6) {
            freeze(true);
        }
        int themedColor = getThemedColor(Theme.key_chat_emojiBottomPanelIcon);
        int argb = Color.argb(30, Color.red(themedColor), Color.green(themedColor), Color.blue(themedColor));
        this.searchFieldHeight = AndroidUtilities.dp(50.0f);
        this.needEmojiSearch = z4;
        int i5 = R.drawable.smiles_tab_smiles;
        int i6 = Theme.key_chat_emojiPanelBackspace;
        int themedColor2 = getThemedColor(i6);
        int i7 = Theme.key_chat_emojiPanelIconSelected;
        this.tabIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context, i5, themedColor2, getThemedColor(i7)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.smiles_tab_gif, getThemedColor(i6), getThemedColor(i7)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.smiles_tab_stickers, getThemedColor(i6), getThemedColor(i7))};
        int i8 = R.drawable.msg_emoji_recent;
        int i9 = Theme.key_chat_emojiPanelIcon;
        Drawable createEmojiIconSelectorDrawable = Theme.createEmojiIconSelectorDrawable(context, i8, getThemedColor(i9), getThemedColor(i7));
        Drawable createEmojiIconSelectorDrawable2 = Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_faves, getThemedColor(i9), getThemedColor(i7));
        Drawable createEmojiIconSelectorDrawable3 = Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_new3, getThemedColor(i9), getThemedColor(i7));
        Drawable createEmojiIconSelectorDrawable4 = Theme.createEmojiIconSelectorDrawable(context, R.drawable.emoji_tabs_new1, getThemedColor(i9), getThemedColor(i7));
        this.searchIconDrawable = createEmojiIconSelectorDrawable4;
        int i10 = R.drawable.emoji_tabs_new2;
        int i11 = Theme.key_chat_emojiPanelStickerPackSelectorLine;
        Drawable createEmojiIconSelectorDrawable5 = Theme.createEmojiIconSelectorDrawable(context, i10, getThemedColor(i11), getThemedColor(i11));
        this.searchIconDotDrawable = createEmojiIconSelectorDrawable5;
        this.stickerIcons = new Drawable[]{createEmojiIconSelectorDrawable, createEmojiIconSelectorDrawable2, createEmojiIconSelectorDrawable3, new LayerDrawable(new Drawable[]{createEmojiIconSelectorDrawable4, createEmojiIconSelectorDrawable5})};
        this.gifIcons = new Drawable[]{Theme.createEmojiIconSelectorDrawable(context, i8, getThemedColor(i9), getThemedColor(i7)), Theme.createEmojiIconSelectorDrawable(context, R.drawable.stickers_gifs_trending, getThemedColor(i9), getThemedColor(i7))};
        this.emojiTitles = new String[]{LocaleController.getString(R.string.Emoji1), LocaleController.getString(R.string.Emoji2), LocaleController.getString(R.string.Emoji3), LocaleController.getString(R.string.Emoji4), LocaleController.getString(R.string.Emoji5), LocaleController.getString(R.string.Emoji6), LocaleController.getString(R.string.Emoji7), LocaleController.getString(R.string.Emoji8)};
        this.showGifs = z3;
        this.info = chatFull;
        Paint paint = new Paint(1);
        this.dotPaint = paint;
        paint.setColor(getThemedColor(Theme.key_chat_emojiPanelNewTrending));
        int i12 = Build.VERSION.SDK_INT;
        if (i12 >= 21) {
            this.outlineProvider = new ViewOutlineProvider() {
                AnonymousClass4() {
                }

                @Override
                public void getOutline(View view2, Outline outline) {
                    outline.setRoundRect(view2.getPaddingLeft(), view2.getPaddingTop(), view2.getMeasuredWidth() - view2.getPaddingRight(), view2.getMeasuredHeight() - view2.getPaddingBottom(), AndroidUtilities.dp(6.0f));
                }
            };
        }
        this.emojiContainer = new FrameLayout(context) {
            AnonymousClass5(Context context2) {
                super(context2);
            }

            @Override
            protected boolean drawChild(Canvas canvas, View view2, long j) {
                if (view2 != EmojiView.this.emojiGridView && view2 != EmojiView.this.emojiSearchField) {
                    return super.drawChild(canvas, view2, j);
                }
                canvas.save();
                float y = EmojiView.this.emojiTabs.getY() + EmojiView.this.emojiTabs.getMeasuredHeight() + 1.0f;
                if (view2 == EmojiView.this.emojiGridView && EmojiView.this.emojiSearchField != null) {
                    y = Math.max(y, EmojiView.this.emojiSearchField.getY() + EmojiView.this.emojiSearchField.getMeasuredHeight() + 1.0f);
                }
                canvas.clipRect(0.0f, y, getMeasuredWidth(), getMeasuredHeight());
                boolean drawChild = super.drawChild(canvas, view2, j);
                canvas.restore();
                return drawChild;
            }
        };
        Tab tab = new Tab();
        tab.type = 0;
        tab.view = this.emojiContainer;
        this.allTabs.add(tab);
        if (z) {
            MediaDataController.getInstance(this.currentAccount).checkStickers(5);
            MediaDataController.getInstance(this.currentAccount).checkFeaturedEmoji();
            this.animatedEmojiTextColorFilter = new PorterDuffColorFilter(getThemedColor(Theme.key_featuredStickers_addButton), PorterDuff.Mode.SRC_IN);
        }
        this.emojiGridView = new EmojiGridView(context2);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDelay(0L);
        defaultItemAnimator.setAddDuration(220L);
        defaultItemAnimator.setMoveDuration(220L);
        defaultItemAnimator.setChangeDuration(160L);
        defaultItemAnimator.setMoveInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.emojiGridView.setItemAnimator(defaultItemAnimator);
        this.emojiGridView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view2, MotionEvent motionEvent) {
                boolean lambda$new$1;
                lambda$new$1 = EmojiView.this.lambda$new$1(resourcesProvider, view2, motionEvent);
                return lambda$new$1;
            }
        });
        this.emojiGridView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            AnonymousClass6() {
            }

            @Override
            public boolean onItemClick(View view2, int i13) {
                String str;
                int i22;
                if (view2 instanceof ImageViewEmoji) {
                    ImageViewEmoji imageViewEmoji = (ImageViewEmoji) view2;
                    if (imageViewEmoji.isRecent) {
                        RecyclerView.ViewHolder findContainingViewHolder = EmojiView.this.emojiGridView.findContainingViewHolder(view2);
                        if (findContainingViewHolder != null && findContainingViewHolder.getAdapterPosition() <= EmojiView.this.getRecentEmoji().size()) {
                            EmojiView.this.delegate.onClearEmojiRecent();
                        }
                    } else {
                        if (imageViewEmoji.getSpan() != null || (str = (String) imageViewEmoji.getTag()) == null) {
                            return false;
                        }
                        String replace = str.replace("🏻", "").replace("🏼", "").replace("🏽", "").replace("🏾", "").replace("🏿", "");
                        String str2 = !imageViewEmoji.isRecent ? Emoji.emojiColor.get(replace) : null;
                        boolean isCompound = CompoundEmoji.isCompound(replace);
                        if (isCompound || EmojiData.emojiColoredMap.contains(replace)) {
                            EmojiView.this.emojiTouchedView = imageViewEmoji;
                            EmojiView emojiView = EmojiView.this;
                            emojiView.emojiTouchedX = emojiView.emojiLastX;
                            EmojiView emojiView2 = EmojiView.this;
                            emojiView2.emojiTouchedY = emojiView2.emojiLastY;
                            if (isCompound) {
                                replace = EmojiView.addColorToCode(replace, str2);
                            } else {
                                EmojiView.this.colorPickerView.setSelection(CompoundEmoji.skinTones.indexOf(str2) + 1);
                            }
                            EmojiView.this.colorPickerView.setEmoji(replace);
                            int popupWidth = EmojiView.this.colorPickerView.getPopupWidth();
                            int popupHeight = EmojiView.this.colorPickerView.getPopupHeight();
                            imageViewEmoji.getLocationOnScreen(EmojiView.this.location);
                            if (EmojiView.this.colorPickerView.isCompound()) {
                                i22 = 0;
                            } else {
                                i22 = (EmojiView.this.emojiSize * EmojiView.this.colorPickerView.getSelection()) + AndroidUtilities.dp((EmojiView.this.colorPickerView.getSelection() * 4) - (AndroidUtilities.isTablet() ? 5 : 1));
                            }
                            if (EmojiView.this.location[0] - i22 < AndroidUtilities.dp(5.0f)) {
                                i22 += (EmojiView.this.location[0] - i22) - AndroidUtilities.dp(5.0f);
                            } else if ((EmojiView.this.location[0] - i22) + popupWidth > AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0f)) {
                                i22 += ((EmojiView.this.location[0] - i22) + popupWidth) - (AndroidUtilities.displaySize.x - AndroidUtilities.dp(5.0f));
                            }
                            int i32 = -i22;
                            int top = imageViewEmoji.getTop() < 0 ? imageViewEmoji.getTop() : 0;
                            EmojiView.this.colorPickerView.setupArrow((AndroidUtilities.dp(AndroidUtilities.isTablet() ? 30.0f : 22.0f) - i32) + ((int) AndroidUtilities.dpf2(0.5f)));
                            EmojiView.this.colorPickerView.setFocusable(true);
                            EmojiView.this.colorPickerView.showAsDropDown(view2, i32, (((-view2.getMeasuredHeight()) - popupHeight) + ((view2.getMeasuredHeight() - EmojiView.this.emojiSize) / 2)) - top);
                            EmojiView.this.pager.requestDisallowInterceptTouchEvent(true);
                            EmojiView.this.emojiGridView.hideSelector(true);
                        }
                    }
                    EmojiView.this.emojiGridView.clearTouchesFor(view2);
                    return true;
                }
                return false;
            }
        });
        this.emojiGridView.setInstantClick(true);
        EmojiGridView emojiGridView = this.emojiGridView;
        AnonymousClass7 anonymousClass7 = new GridLayoutManager(context2, 8) {

            class AnonymousClass1 extends LinearSmoothScrollerCustom {
                AnonymousClass1(Context context, int i2) {
                    super(context, i2);
                }

                @Override
                public void onEnd() {
                    EmojiView.this.emojiSmoothScrolling = false;
                }
            }

            AnonymousClass7(Context context2, int i13) {
                super(context2, i13);
            }

            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i13) {
                try {
                    AnonymousClass1 anonymousClass12 = new LinearSmoothScrollerCustom(recyclerView.getContext(), 2) {
                        AnonymousClass1(Context context2, int i22) {
                            super(context2, i22);
                        }

                        @Override
                        public void onEnd() {
                            EmojiView.this.emojiSmoothScrolling = false;
                        }
                    };
                    anonymousClass12.setTargetPosition(i13);
                    startSmoothScroll(anonymousClass12);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        };
        this.emojiLayoutManager = anonymousClass7;
        emojiGridView.setLayoutManager(anonymousClass7);
        this.emojiGridView.setTopGlowOffset(AndroidUtilities.dp(38.0f));
        this.emojiGridView.setBottomGlowOffset(AndroidUtilities.dp(36.0f));
        this.emojiGridView.setPadding(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(36.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(44.0f));
        EmojiGridView emojiGridView2 = this.emojiGridView;
        int i13 = Theme.key_chat_emojiPanelBackground;
        emojiGridView2.setGlowColor(getThemedColor(i13));
        this.emojiGridView.setItemSelectorColorProvider(new GenericProvider() {
            @Override
            public final Object provide(Object obj) {
                Integer lambda$new$2;
                lambda$new$2 = EmojiView.lambda$new$2((Integer) obj);
                return lambda$new$2;
            }
        });
        this.emojiGridView.setClipToPadding(false);
        this.emojiLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            AnonymousClass8() {
            }

            @Override
            public int getSpanGroupIndex(int i14, int i22) {
                return super.getSpanGroupIndex(i14, i22);
            }

            @Override
            public int getSpanSize(int i14) {
                int itemViewType;
                if (EmojiView.this.emojiGridView.getAdapter() != EmojiView.this.emojiSearchAdapter ? !((EmojiView.this.needEmojiSearch && i14 == 0) || i14 == EmojiView.this.emojiAdapter.trendingRow || i14 == EmojiView.this.emojiAdapter.trendingHeaderRow || i14 == EmojiView.this.emojiAdapter.recentlyUsedHeaderRow || EmojiView.this.emojiAdapter.positionToSection.indexOfKey(i14) >= 0 || EmojiView.this.emojiAdapter.positionToUnlock.indexOfKey(i14) >= 0) : !((itemViewType = EmojiView.this.emojiSearchAdapter.getItemViewType(i14)) == 1 || itemViewType == 3 || itemViewType == 2)) {
                    return 1;
                }
                return EmojiView.this.emojiLayoutManager.getSpanCount();
            }
        });
        EmojiGridView emojiGridView3 = this.emojiGridView;
        EmojiGridAdapter emojiGridAdapter = new EmojiGridAdapter();
        this.emojiAdapter = emojiGridAdapter;
        emojiGridView3.setAdapter(emojiGridAdapter);
        this.emojiGridView.addItemDecoration(new EmojiGridSpacing());
        this.emojiSearchAdapter = new EmojiSearchAdapter();
        this.emojiContainer.addView(this.emojiGridView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerAnimationScrollHelper recyclerAnimationScrollHelper = new RecyclerAnimationScrollHelper(this.emojiGridView, this.emojiLayoutManager);
        this.emojiScrollHelper = recyclerAnimationScrollHelper;
        recyclerAnimationScrollHelper.setAnimationCallback(new RecyclerAnimationScrollHelper.AnimationCallback() {
            AnonymousClass9() {
            }

            @Override
            public void ignoreView(View view2, boolean z8) {
                if (view2 instanceof ImageViewEmoji) {
                    ((ImageViewEmoji) view2).ignoring = z8;
                }
            }

            @Override
            public void onEndAnimation() {
                EmojiView.this.emojiSmoothScrolling = false;
                EmojiView.this.emojiGridView.updateEmojiDrawables();
            }

            @Override
            public void onPreAnimation() {
                EmojiView.this.emojiGridView.updateEmojiDrawables();
                EmojiView.this.emojiSmoothScrolling = true;
            }
        });
        this.emojiGridView.setOnScrollListener(new TypedScrollListener(1) {
            AnonymousClass10(int i14) {
                super(i14);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i14) {
                if (i14 == 0) {
                    EmojiView.this.emojiSmoothScrolling = false;
                }
                super.onScrollStateChanged(recyclerView, i14);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i14, int i22) {
                EmojiView.this.updateEmojiTabsPosition();
                super.onScrolled(recyclerView, i14, i22);
            }
        });
        this.emojiTabs = new EmojiTabsStrip(context2, resourcesProvider, true, true, z, 0, baseFragment != null ? new Runnable() {
            @Override
            public final void run() {
                EmojiView.this.lambda$new$3();
            }
        } : null) {
            AnonymousClass11(Context context2, final Theme.ResourcesProvider resourcesProvider4, boolean z8, boolean z22, boolean z9, int i14, Runnable runnable) {
                super(context2, resourcesProvider4, z8, z22, z9, i14, runnable);
            }

            @Override
            protected boolean allowEmojisForNonPremium() {
                return EmojiView.this.allowEmojisForNonPremium;
            }

            @Override
            protected boolean doIncludeFeatured() {
                return EmojiView.this.featuredEmojiSets.size() <= 0 || ((TLRPC.StickerSetCovered) EmojiView.this.featuredEmojiSets.get(0)).set == null || MessagesController.getEmojiSettings(EmojiView.this.currentAccount).getLong("emoji_featured_hidden", 0L) == ((TLRPC.StickerSetCovered) EmojiView.this.featuredEmojiSets.get(0)).set.id || !UserConfig.getInstance(UserConfig.selectedAccount).isPremium();
            }

            @Override
            protected ColorFilter getEmojiColorFilter() {
                return EmojiView.this.animatedEmojiTextColorFilter;
            }

            @Override
            protected boolean isInstalled(EmojiPack emojiPack) {
                return emojiPack.installed || EmojiView.this.installedEmojiSets.contains(Long.valueOf(emojiPack.set.id));
            }

            @Override
            protected boolean onTabClick(int r10) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.AnonymousClass11.onTabClick(int):boolean");
            }

            @Override
            public void setTranslationY(float f6) {
                if (getTranslationY() != f6) {
                    super.setTranslationY(f6);
                    if (EmojiView.this.emojiTabsShadow != null) {
                        EmojiView.this.emojiTabsShadow.setTranslationY(f6);
                    }
                    EmojiView.this.emojiContainer.invalidate();
                }
            }
        };
        if (z4) {
            AnonymousClass12 anonymousClass12 = new SearchField(context2, 1) {
                AnonymousClass12(Context context2, int i14) {
                    super(context2, i14);
                }

                @Override
                public void setTranslationY(float f6) {
                    if (f6 != getTranslationY()) {
                        super.setTranslationY(f6);
                        EmojiView.this.emojiContainer.invalidate();
                    }
                }
            };
            this.emojiSearchField = anonymousClass12;
            this.emojiContainer.addView(anonymousClass12, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
            this.emojiSearchField.searchEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                AnonymousClass13() {
                }

                @Override
                public void onFocusChange(View view2, boolean z8) {
                    if (z8) {
                        EmojiView.this.lastSearchKeyboardLanguage = AndroidUtilities.getCurrentKeyboardLanguage();
                        MediaDataController.getInstance(EmojiView.this.currentAccount).fetchNewEmojiKeywords(EmojiView.this.lastSearchKeyboardLanguage);
                    }
                }
            });
        }
        int themedColor3 = getThemedColor(i13);
        if (Color.alpha(themedColor3) >= 255) {
            this.emojiTabs.setBackgroundColor(themedColor3);
        }
        this.emojiAdapter.processEmoji(true);
        this.emojiTabs.updateEmojiPacks(getEmojipacks());
        this.emojiContainer.addView(this.emojiTabs, LayoutHelper.createFrame(-1, 36.0f));
        View view2 = new View(context2);
        this.emojiTabsShadow = view2;
        view2.setAlpha(0.0f);
        this.emojiTabsShadow.setTag(1);
        View view3 = this.emojiTabsShadow;
        int i14 = Theme.key_chat_emojiPanelShadowLine;
        view3.setBackgroundColor(getThemedColor(i14));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp(36.0f);
        this.emojiContainer.addView(this.emojiTabsShadow, layoutParams);
        if (z2) {
            if (z3) {
                this.gifContainer = new FrameLayout(context2) {
                    AnonymousClass14(Context context2) {
                        super(context2);
                    }

                    @Override
                    protected boolean drawChild(Canvas canvas, View view4, long j) {
                        if (view4 != EmojiView.this.gifGridView) {
                            return super.drawChild(canvas, view4, j);
                        }
                        canvas.save();
                        canvas.clipRect(0.0f, EmojiView.this.gifSearchField.getY() + EmojiView.this.gifSearchField.getMeasuredHeight(), getMeasuredWidth(), getMeasuredHeight());
                        boolean drawChild = super.drawChild(canvas, view4, j);
                        canvas.restore();
                        return drawChild;
                    }
                };
                anonymousClass1 = null;
                Tab tab2 = new Tab();
                tab2.type = 1;
                tab2.view = this.gifContainer;
                this.allTabs.add(tab2);
                AnonymousClass15 anonymousClass15 = new RecyclerListView(context2) {
                    private boolean ignoreLayout;
                    private boolean wasMeasured;

                    AnonymousClass15(Context context2) {
                        super(context2);
                    }

                    @Override
                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, EmojiView.this.gifGridView, 0, EmojiView.this.contentPreviewViewerDelegate, this.resourcesProvider);
                    }

                    @Override
                    public void onLayout(boolean z8, int i15, int i22, int i32, int i42) {
                        if (EmojiView.this.firstGifAttach && EmojiView.this.gifAdapter.getItemCount() > 1) {
                            this.ignoreLayout = true;
                            EmojiView.this.gifLayoutManager.scrollToPositionWithOffset(0, 0);
                            EmojiView.this.gifSearchField.setVisibility(0);
                            EmojiView.this.gifTabs.onPageScrolled(0, 0);
                            EmojiView.this.firstGifAttach = false;
                            this.ignoreLayout = false;
                        }
                        super.onLayout(z8, i15, i22, i32, i42);
                        EmojiView.this.checkGifSearchFieldScroll(true);
                    }

                    @Override
                    public void onMeasure(int i15, int i22) {
                        super.onMeasure(i15, i22);
                        if (this.wasMeasured) {
                            return;
                        }
                        EmojiView.this.gifAdapter.notifyDataSetChanged();
                        this.wasMeasured = true;
                    }

                    @Override
                    public void requestLayout() {
                        if (this.ignoreLayout) {
                            return;
                        }
                        super.requestLayout();
                    }
                };
                this.gifGridView = anonymousClass15;
                r4 = 0;
                anonymousClass15.setClipToPadding(false);
                RecyclerListView recyclerListView = this.gifGridView;
                GifLayoutManager gifLayoutManager = new GifLayoutManager(context2);
                this.gifLayoutManager = gifLayoutManager;
                recyclerListView.setLayoutManager(gifLayoutManager);
                this.gifGridView.addItemDecoration(new RecyclerView.ItemDecoration() {
                    AnonymousClass16() {
                    }

                    @Override
                    public void getItemOffsets(android.graphics.Rect rect, View view4, RecyclerView recyclerView, RecyclerView.State state) {
                        int childAdapterPosition = recyclerView.getChildAdapterPosition(view4);
                        if (EmojiView.this.gifGridView.getAdapter() == EmojiView.this.gifAdapter && childAdapterPosition == EmojiView.this.gifAdapter.trendingSectionItem) {
                            rect.set(0, 0, 0, 0);
                            return;
                        }
                        if (childAdapterPosition == 0 && EmojiView.this.gifAdapter.addSearch) {
                            rect.set(0, 0, 0, 0);
                            return;
                        }
                        rect.left = 0;
                        rect.bottom = 0;
                        rect.top = AndroidUtilities.dp(2.0f);
                        rect.right = EmojiView.this.gifLayoutManager.isLastInRow(childAdapterPosition - (EmojiView.this.gifAdapter.addSearch ? 1 : 0)) ? 0 : AndroidUtilities.dp(2.0f);
                    }
                });
                this.gifGridView.setPadding(0, this.searchFieldHeight, 0, AndroidUtilities.dp(44.0f));
                this.gifGridView.setOverScrollMode(2);
                ((SimpleItemAnimator) this.gifGridView.getItemAnimator()).setSupportsChangeAnimations(false);
                RecyclerListView recyclerListView2 = this.gifGridView;
                GifAdapter gifAdapter = new GifAdapter(this, context2, true);
                this.gifAdapter = gifAdapter;
                recyclerListView2.setAdapter(gifAdapter);
                this.gifSearchAdapter = new GifAdapter(this, context2);
                this.gifGridView.setOnScrollListener(new TypedScrollListener(2));
                resourcesProvider3 = resourcesProvider4;
                this.gifGridView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public final boolean onTouch(View view4, MotionEvent motionEvent) {
                        boolean lambda$new$4;
                        lambda$new$4 = EmojiView.this.lambda$new$4(resourcesProvider3, view4, motionEvent);
                        return lambda$new$4;
                    }
                });
                RecyclerListView.OnItemClickListener onItemClickListener = new RecyclerListView.OnItemClickListener() {
                    @Override
                    public final void onItemClick(View view4, int i15) {
                        EmojiView.this.lambda$new$5(view4, i15);
                    }
                };
                this.gifOnItemClickListener = onItemClickListener;
                this.gifGridView.setOnItemClickListener(onItemClickListener);
                this.gifContainer.addView(this.gifGridView, LayoutHelper.createFrame(-1, -1.0f));
                AnonymousClass17 anonymousClass17 = new SearchField(context2, 2) {
                    AnonymousClass17(Context context2, int i15) {
                        super(context2, i15);
                    }

                    @Override
                    public void setTranslationY(float f6) {
                        if (getTranslationY() != f6) {
                            super.setTranslationY(f6);
                            EmojiView.this.gifContainer.invalidate();
                        }
                    }
                };
                this.gifSearchField = anonymousClass17;
                this.gifContainer.addView(anonymousClass17, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
                DraggableScrollSlidingTabStrip draggableScrollSlidingTabStrip = new DraggableScrollSlidingTabStrip(context2, resourcesProvider3);
                this.gifTabs = draggableScrollSlidingTabStrip;
                draggableScrollSlidingTabStrip.setType(ScrollSlidingTabStrip.Type.TAB);
                this.gifTabs.setUnderlineHeight(AndroidUtilities.getShadowHeight());
                this.gifTabs.setIndicatorColor(getThemedColor(i11));
                this.gifTabs.setUnderlineColor(getThemedColor(i14));
                this.gifTabs.setBackgroundColor(getThemedColor(i13));
                updateGifTabs();
                this.gifTabs.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate() {
                    @Override
                    public final void onPageSelected(int i15) {
                        EmojiView.this.lambda$new$6(i15);
                    }
                });
                this.gifAdapter.loadTrendingGifs();
            } else {
                resourcesProvider3 = resourcesProvider4;
                r4 = 0;
                anonymousClass1 = null;
            }
            z7 = z5;
            this.stickersContainer = new FrameLayout(context2) {
                final boolean val$shouldDrawBackground;

                AnonymousClass18(Context context2, boolean z72) {
                    super(context2);
                    r3 = z72;
                }

                @Override
                protected boolean drawChild(Canvas canvas, View view4, long j) {
                    if (r3 || !(view4 == EmojiView.this.stickersGridView || view4 == EmojiView.this.stickersSearchField)) {
                        return super.drawChild(canvas, view4, j);
                    }
                    canvas.save();
                    float y = EmojiView.this.stickersTab.getY() + EmojiView.this.stickersTab.getMeasuredHeight() + 1.0f;
                    if (view4 == EmojiView.this.stickersGridView) {
                        y = Math.max(y, EmojiView.this.stickersSearchField.getY() + EmojiView.this.stickersSearchField.getMeasuredHeight() + 1.0f);
                    }
                    canvas.clipRect(0.0f, y, getMeasuredWidth(), getMeasuredHeight());
                    boolean drawChild = super.drawChild(canvas, view4, j);
                    canvas.restore();
                    return drawChild;
                }

                @Override
                protected void onAttachedToWindow() {
                    super.onAttachedToWindow();
                    EmojiView.this.stickersContainerAttached = true;
                    EmojiView.this.updateStickerTabsPosition();
                    if (EmojiView.this.chooseStickerActionTracker != null) {
                        EmojiView.this.chooseStickerActionTracker.checkVisibility();
                    }
                }

                @Override
                protected void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    EmojiView.this.stickersContainerAttached = false;
                    EmojiView.this.updateStickerTabsPosition();
                    if (EmojiView.this.chooseStickerActionTracker != null) {
                        EmojiView.this.chooseStickerActionTracker.checkVisibility();
                    }
                }
            };
            MediaDataController.getInstance(this.currentAccount).checkStickers(r4);
            MediaDataController.getInstance(this.currentAccount).checkFeaturedStickers();
            AnonymousClass19 anonymousClass19 = new RecyclerListViewWithOverlayDraw(context2) {
                boolean ignoreLayout;

                AnonymousClass19(Context context2) {
                    super(context2);
                }

                @Override
                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    return super.onInterceptTouchEvent(motionEvent) || ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, EmojiView.this.stickersGridView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate, this.resourcesProvider);
                }

                @Override
                public void onLayout(boolean z8, int i15, int i22, int i32, int i42) {
                    if (EmojiView.this.firstStickersAttach && EmojiView.this.stickersGridAdapter.getItemCount() > 0) {
                        this.ignoreLayout = true;
                        EmojiView.this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
                        EmojiView.this.firstStickersAttach = false;
                        this.ignoreLayout = false;
                    }
                    super.onLayout(z8, i15, i22, i32, i42);
                    EmojiView.this.checkStickersSearchFieldScroll(true);
                }

                @Override
                public void onScrolled(int i15, int i22) {
                    super.onScrolled(i15, i22);
                    if (EmojiView.this.stickersTabContainer != null) {
                        EmojiView.this.stickersTab.setUnderlineHeight(EmojiView.this.stickersGridView.canScrollVertically(-1) ? AndroidUtilities.getShadowHeight() : 0);
                    }
                }

                @Override
                public void requestLayout() {
                    if (this.ignoreLayout) {
                        return;
                    }
                    super.requestLayout();
                }

                @Override
                public void setVisibility(int i15) {
                    super.setVisibility(i15);
                }
            };
            this.stickersGridView = anonymousClass19;
            AnonymousClass20 anonymousClass20 = new GridLayoutManager(context2, 5) {
                AnonymousClass20(Context context2, int i15) {
                    super(context2, i15);
                }

                @Override
                public int scrollVerticallyBy(int i15, RecyclerView.Recycler recycler, RecyclerView.State state) {
                    int scrollVerticallyBy = super.scrollVerticallyBy(i15, recycler, state);
                    if (scrollVerticallyBy != 0 && EmojiView.this.stickersGridView.getScrollState() == 1) {
                        EmojiView.this.expandStickersByDragg = false;
                        EmojiView.this.updateStickerTabsPosition();
                    }
                    if (EmojiView.this.chooseStickerActionTracker == null) {
                        EmojiView.this.createStickersChooseActionTracker();
                    }
                    EmojiView.this.chooseStickerActionTracker.doSomeAction();
                    return scrollVerticallyBy;
                }

                @Override
                public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i15) {
                    try {
                        LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(recyclerView.getContext(), 2);
                        linearSmoothScrollerCustom.setTargetPosition(i15);
                        startSmoothScroll(linearSmoothScrollerCustom);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            };
            this.stickersLayoutManager = anonymousClass20;
            anonymousClass19.setLayoutManager(anonymousClass20);
            this.stickersLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                AnonymousClass21() {
                }

                @Override
                public int getSpanSize(int i15) {
                    if (EmojiView.this.stickersGridView.getAdapter() == EmojiView.this.stickersGridAdapter) {
                        if (i15 != 0 && i15 != EmojiView.this.stickersGridAdapter.totalItems && (EmojiView.this.stickersGridAdapter.cache.get(i15) == null || (EmojiView.this.stickersGridAdapter.cache.get(i15) instanceof TLRPC.Document))) {
                            return 1;
                        }
                    } else if (i15 != EmojiView.this.stickersSearchGridAdapter.totalItems && (EmojiView.this.stickersSearchGridAdapter.cache.get(i15) == null || (EmojiView.this.stickersSearchGridAdapter.cache.get(i15) instanceof TLRPC.Document))) {
                        return 1;
                    }
                    return EmojiView.this.stickersGridAdapter.stickersPerRow;
                }
            });
            this.stickersGridView.setPadding(r4, AndroidUtilities.dp(36.0f), r4, AndroidUtilities.dp(44.0f));
            this.stickersGridView.setClipToPadding(r4);
            Tab tab3 = new Tab();
            tab3.type = 2;
            tab3.view = this.stickersContainer;
            this.allTabs.add(tab3);
            this.stickersSearchGridAdapter = new StickersSearchGridAdapter(context2);
            RecyclerListView recyclerListView3 = this.stickersGridView;
            StickersGridAdapter stickersGridAdapter = new StickersGridAdapter(context2);
            this.stickersGridAdapter = stickersGridAdapter;
            recyclerListView3.setAdapter(stickersGridAdapter);
            this.stickersGridView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public final boolean onTouch(View view4, MotionEvent motionEvent) {
                    boolean lambda$new$7;
                    lambda$new$7 = EmojiView.this.lambda$new$7(resourcesProvider3, view4, motionEvent);
                    return lambda$new$7;
                }
            });
            RecyclerListView.OnItemClickListener onItemClickListener2 = new RecyclerListView.OnItemClickListener() {
                @Override
                public final void onItemClick(View view4, int i15) {
                    EmojiView.this.lambda$new$8(view4, i15);
                }
            };
            this.stickersOnItemClickListener = onItemClickListener2;
            this.stickersGridView.setOnItemClickListener(onItemClickListener2);
            this.stickersGridView.setGlowColor(getThemedColor(i13));
            this.stickersContainer.addView(this.stickersGridView);
            this.stickersScrollHelper = new RecyclerAnimationScrollHelper(this.stickersGridView, this.stickersLayoutManager);
            AnonymousClass22 anonymousClass22 = new SearchField(context2, r4) {
                AnonymousClass22(Context context2, int r42) {
                    super(context2, r42);
                }

                @Override
                public void setTranslationY(float f6) {
                    if (f6 != getTranslationY()) {
                        super.setTranslationY(f6);
                        EmojiView.this.stickersContainer.invalidate();
                    }
                }
            };
            this.stickersSearchField = anonymousClass22;
            this.stickersContainer.addView(anonymousClass22, new FrameLayout.LayoutParams(-1, this.searchFieldHeight + AndroidUtilities.getShadowHeight()));
            resourcesProvider2 = resourcesProvider3;
            AnonymousClass23 anonymousClass23 = new AnonymousClass23(context2, resourcesProvider4, baseFragment, z5);
            this.stickersTab = anonymousClass23;
            anonymousClass23.setDragEnabled(true);
            this.stickersTab.setWillNotDraw(false);
            this.stickersTab.setType(ScrollSlidingTabStrip.Type.TAB);
            this.stickersTab.setUnderlineHeight(this.stickersGridView.canScrollVertically(-1) ? AndroidUtilities.getShadowHeight() : 0);
            this.stickersTab.setIndicatorColor(getThemedColor(i11));
            this.stickersTab.setUnderlineColor(getThemedColor(i14));
            ViewGroup viewGroup2 = viewGroup;
            if (viewGroup2 == null || !z72) {
                viewGroup2 = this.stickersContainer;
                view = this.stickersTab;
                createFrame = LayoutHelper.createFrame(-1, 36, 51);
            } else {
                AnonymousClass24 anonymousClass24 = new FrameLayout(context2) {
                    Paint paint = new Paint();

                    AnonymousClass24(Context context2) {
                        super(context2);
                        this.paint = new Paint();
                    }

                    @Override
                    protected void dispatchDraw(Canvas canvas) {
                        float dp = AndroidUtilities.dp(50.0f) * EmojiView.this.delegate.getProgressToSearchOpened();
                        if (dp > getMeasuredHeight()) {
                            return;
                        }
                        canvas.save();
                        if (dp != 0.0f) {
                            canvas.clipRect(0.0f, dp, getMeasuredWidth(), getMeasuredHeight());
                        }
                        this.paint.setColor(EmojiView.this.getThemedColor(Theme.key_chat_emojiPanelBackground));
                        canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(36.0f) + EmojiView.this.stickersTab.getExpandedOffset(), this.paint);
                        super.dispatchDraw(canvas);
                        EmojiView.this.stickersTab.drawOverlays(canvas);
                        canvas.restore();
                    }

                    @Override
                    protected void onLayout(boolean z8, int i15, int i22, int i32, int i42) {
                        super.onLayout(z8, i15, i22, i32, i42);
                        EmojiView.this.updateStickerTabsPosition();
                    }
                };
                this.stickersTabContainer = anonymousClass24;
                anonymousClass24.addView(this.stickersTab, LayoutHelper.createFrame(-1, 36, 51));
                view = this.stickersTabContainer;
                createFrame = LayoutHelper.createFrame(-1, -2.0f);
            }
            viewGroup2.addView(view, createFrame);
            updateStickerTabs(true);
            this.stickersTab.setDelegate(new ScrollSlidingTabStrip.ScrollSlidingTabStripDelegate() {
                @Override
                public final void onPageSelected(int i15) {
                    EmojiView.this.lambda$new$9(i15);
                }
            });
            this.stickersGridView.setOnScrollListener(new TypedScrollListener(0));
        } else {
            z72 = z5;
            resourcesProvider2 = resourcesProvider4;
        }
        this.currentTabs.clear();
        this.currentTabs.addAll(this.allTabs);
        AnonymousClass25 anonymousClass25 = new ViewPager(context2) {
            AnonymousClass25(Context context2) {
                super(context2);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (EmojiView.this.ignorePagerScroll) {
                    return false;
                }
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(canScrollHorizontally(-1));
                }
                try {
                    return super.onInterceptTouchEvent(motionEvent);
                } catch (IllegalArgumentException unused) {
                    return false;
                }
            }

            @Override
            public void setCurrentItem(int i15, boolean z8) {
                EmojiView.this.startStopVisibleGifs(i15 == 1);
                if (i15 != getCurrentItem()) {
                    super.setCurrentItem(i15, z8);
                    return;
                }
                if (i15 != 0) {
                    if (i15 == 1) {
                        EmojiView.this.gifGridView.smoothScrollToPosition(0);
                        return;
                    } else {
                        EmojiView.this.stickersGridView.smoothScrollToPosition(1);
                        return;
                    }
                }
                EmojiView.this.tabsMinusDy[1] = 0;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(EmojiView.this.emojiTabs, (Property<EmojiTabsStrip, Float>) ViewGroup.TRANSLATION_Y, 0.0f);
                ofFloat.setDuration(150L);
                ofFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                ofFloat.start();
                EmojiView.this.scrollEmojisToPosition(1, 0);
                if (EmojiView.this.emojiTabs != null) {
                    EmojiView.this.emojiTabs.select(0);
                }
            }
        };
        this.pager = anonymousClass25;
        EmojiPagesAdapter emojiPagesAdapter = new EmojiPagesAdapter();
        this.emojiPagerAdapter = emojiPagesAdapter;
        anonymousClass25.setAdapter(emojiPagesAdapter);
        AnonymousClass26 anonymousClass26 = new ImageView(context2) {
            AnonymousClass26(Context context2) {
                super(context2);
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0) {
                    EmojiView.this.backspacePressed = true;
                    EmojiView.this.backspaceOnce = false;
                    EmojiView.this.postBackspaceRunnable(350);
                } else if (motionEvent.getAction() == 3 || motionEvent.getAction() == 1) {
                    EmojiView.this.backspacePressed = false;
                    if (!EmojiView.this.backspaceOnce && EmojiView.this.delegate != null && EmojiView.this.delegate.onBackspace()) {
                        try {
                            EmojiView.this.backspaceButton.performHapticFeedback(3);
                        } catch (Exception unused) {
                        }
                    }
                }
                super.onTouchEvent(motionEvent);
                return true;
            }
        };
        this.backspaceButton = anonymousClass26;
        anonymousClass26.setHapticFeedbackEnabled(true);
        this.backspaceButton.setImageResource(R.drawable.smiles_tab_clear);
        ImageView imageView = this.backspaceButton;
        int themedColor4 = getThemedColor(i6);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        imageView.setColorFilter(new PorterDuffColorFilter(themedColor4, mode));
        ImageView imageView2 = this.backspaceButton;
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        imageView2.setScaleType(scaleType);
        ImageView imageView3 = this.backspaceButton;
        int i15 = R.string.AccDescrBackspace;
        imageView3.setContentDescription(LocaleController.getString(i15));
        this.backspaceButton.setFocusable(true);
        this.backspaceButton.setOnClickListener(new View.OnClickListener() {
            AnonymousClass27() {
            }

            @Override
            public void onClick(View view4) {
            }
        });
        FrameLayout frameLayout = new FrameLayout(context2);
        this.bulletinContainer = frameLayout;
        if (z4) {
            f2 = (AndroidUtilities.getShadowHeight() / AndroidUtilities.density) + 40.0f;
            f5 = 0.0f;
            f = 0.0f;
            i = -1;
            f3 = 100.0f;
            i2 = 87;
            f4 = 0.0f;
        } else {
            f = 0.0f;
            f2 = 0.0f;
            i = -1;
            f3 = 100.0f;
            i2 = 87;
            f4 = 0.0f;
            f5 = 0.0f;
        }
        addView(frameLayout, LayoutHelper.createFrame(i, f3, i2, f4, f5, f, f2));
        AnonymousClass28 anonymousClass28 = new FrameLayout(context2) {
            AnonymousClass28(Context context2) {
                super(context2);
            }

            @Override
            public void setTranslationY(float f6) {
                if (getTranslationY() != f6) {
                    super.setTranslationY(f6);
                    EmojiView.this.invalidate();
                }
            }
        };
        this.bottomTabContainer = anonymousClass28;
        anonymousClass28.setClickable(true);
        View view4 = new View(context2);
        this.shadowLine = view4;
        view4.setBackgroundColor(getThemedColor(i14));
        this.bottomTabContainer.addView(this.shadowLine, new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight()));
        View view5 = new View(context2);
        this.bottomTabContainerBackground = view5;
        this.bottomTabContainer.addView(view5, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(40.0f), 83));
        if (z4) {
            addView(this.bottomTabContainer, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(40.0f) + AndroidUtilities.getShadowHeight(), 83));
            this.bottomTabContainer.addView(this.backspaceButton, LayoutHelper.createFrame(47, 40, 85));
            if (i12 >= 21) {
                i4 = argb;
                this.backspaceButton.setBackground(Theme.createSelectorDrawable(i4, 1, AndroidUtilities.dp(18.0f)));
            } else {
                i4 = argb;
            }
            if (z2) {
                ImageView imageView4 = new ImageView(context2);
                this.stickerSettingsButton = imageView4;
                imageView4.setImageResource(R.drawable.smiles_tab_settings);
                this.stickerSettingsButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(i6), mode));
                this.stickerSettingsButton.setScaleType(scaleType);
                this.stickerSettingsButton.setFocusable(true);
                if (i12 >= 21) {
                    this.stickerSettingsButton.setBackground(Theme.createSelectorDrawable(i4, 1, AndroidUtilities.dp(18.0f)));
                }
                this.stickerSettingsButton.setContentDescription(LocaleController.getString(R.string.Settings));
                this.bottomTabContainer.addView(this.stickerSettingsButton, LayoutHelper.createFrame(47, 40, 85));
                this.stickerSettingsButton.setOnClickListener(new View.OnClickListener() {
                    AnonymousClass29() {
                    }

                    @Override
                    public void onClick(View view6) {
                        if (EmojiView.this.delegate != null) {
                            EmojiView.this.delegate.onStickersSettingsClick();
                        }
                    }
                });
            }
            PagerSlidingTabStrip pagerSlidingTabStrip = new PagerSlidingTabStrip(context2, resourcesProvider2);
            this.typeTabs = pagerSlidingTabStrip;
            pagerSlidingTabStrip.setViewPager(this.pager);
            this.typeTabs.setShouldExpand(false);
            this.typeTabs.setIndicatorHeight(AndroidUtilities.dp(3.0f));
            this.typeTabs.setIndicatorColor(ColorUtils.setAlphaComponent(getThemedColor(i7), 20));
            this.typeTabs.setUnderlineHeight(0);
            this.typeTabs.setTabPaddingLeftRight(AndroidUtilities.dp(13.0f));
            this.bottomTabContainer.addView(this.typeTabs, LayoutHelper.createFrame(-2, 40, 81));
            this.typeTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                final boolean val$shouldDrawBackground;

                AnonymousClass30(boolean z72) {
                    r2 = z72;
                }

                @Override
                public void onPageScrollStateChanged(int i16) {
                }

                @Override
                public void onPageScrolled(int i16, float f6, int i22) {
                    EmojiView.this.checkGridVisibility(i16, f6);
                    EmojiView emojiView = EmojiView.this;
                    emojiView.onPageScrolled(i16, (emojiView.getMeasuredWidth() - EmojiView.this.getPaddingLeft()) - EmojiView.this.getPaddingRight(), i22);
                    boolean z8 = true;
                    EmojiView.this.showBottomTab(true, true);
                    int currentItem = EmojiView.this.pager.getCurrentItem();
                    SearchField searchField = currentItem == 0 ? EmojiView.this.emojiSearchField : currentItem == 1 ? EmojiView.this.gifSearchField : EmojiView.this.stickersSearchField;
                    String obj = searchField.searchEditText.getText().toString();
                    int i32 = 0;
                    while (i32 < 3) {
                        EmojiView emojiView2 = EmojiView.this;
                        SearchField searchField2 = i32 == 0 ? emojiView2.emojiSearchField : i32 == 1 ? emojiView2.gifSearchField : emojiView2.stickersSearchField;
                        if (searchField2 != null && searchField2 != searchField && searchField2.searchEditText != null && !searchField2.searchEditText.getText().toString().equals(obj)) {
                            searchField2.searchEditText.setText(obj);
                            searchField2.searchEditText.setSelection(obj.length());
                        }
                        i32++;
                    }
                    EmojiView emojiView3 = EmojiView.this;
                    if ((i16 != 0 || f6 <= 0.0f) && i16 != 1) {
                        z8 = false;
                    }
                    emojiView3.startStopVisibleGifs(z8);
                    EmojiView.this.updateStickerTabsPosition();
                }

                @Override
                public void onPageSelected(int i16) {
                    SearchField searchField;
                    EmojiView.this.saveNewPage();
                    boolean z8 = false;
                    EmojiView.this.showBackspaceButton(i16 == 0, true);
                    EmojiView emojiView = EmojiView.this;
                    if (i16 == 2 && r2) {
                        z8 = true;
                    }
                    emojiView.showStickerSettingsButton(z8, true);
                    if (EmojiView.this.delegate.isSearchOpened()) {
                        if (i16 == 0) {
                            if (EmojiView.this.emojiSearchField == null) {
                                return;
                            } else {
                                searchField = EmojiView.this.emojiSearchField;
                            }
                        } else if (i16 == 1) {
                            if (EmojiView.this.gifSearchField == null) {
                                return;
                            } else {
                                searchField = EmojiView.this.gifSearchField;
                            }
                        } else if (EmojiView.this.stickersSearchField == null) {
                            return;
                        } else {
                            searchField = EmojiView.this.stickersSearchField;
                        }
                        searchField.searchEditText.requestFocus();
                    }
                }
            });
            ImageView imageView5 = new ImageView(context2);
            this.searchButton = imageView5;
            imageView5.setImageResource(R.drawable.smiles_tab_search);
            this.searchButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(i6), mode));
            this.searchButton.setScaleType(scaleType);
            this.searchButton.setContentDescription(LocaleController.getString(R.string.Search));
            this.searchButton.setFocusable(true);
            this.searchButton.setVisibility(8);
            if (i12 >= 21) {
                this.searchButton.setBackground(Theme.createSelectorDrawable(i4, 1, AndroidUtilities.dp(18.0f)));
            }
            this.bottomTabContainer.addView(this.searchButton, LayoutHelper.createFrame(47, 40, 83));
            this.searchButton.setOnClickListener(new View.OnClickListener() {
                AnonymousClass31() {
                }

                @Override
                public void onClick(View view6) {
                    int currentItem = EmojiView.this.pager.getCurrentItem();
                    SearchField searchField = currentItem == 0 ? EmojiView.this.emojiSearchField : currentItem == 1 ? EmojiView.this.gifSearchField : EmojiView.this.stickersSearchField;
                    if (searchField == null) {
                        return;
                    }
                    searchField.searchEditText.requestFocus();
                    MotionEvent obtain = MotionEvent.obtain(0L, 0L, 0, 0.0f, 0.0f, 0);
                    searchField.searchEditText.onTouchEvent(obtain);
                    obtain.recycle();
                    MotionEvent obtain2 = MotionEvent.obtain(0L, 0L, 1, 0.0f, 0.0f, 0);
                    searchField.searchEditText.onTouchEvent(obtain2);
                    obtain2.recycle();
                }
            });
            i3 = 4;
        } else {
            addView(this.bottomTabContainer, LayoutHelper.createFrame((i12 >= 21 ? 40 : 44) + 16, (i12 >= 21 ? 40 : 44) + 8, (LocaleController.isRTL ? 3 : 5) | 80, 0.0f, 0.0f, 2.0f, 0.0f));
            Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), getThemedColor(i13), getThemedColor(i13));
            if (i12 < 21) {
                Drawable mutate = context2.getResources().getDrawable(R.drawable.floating_shadow).mutate();
                mutate.setColorFilter(new PorterDuffColorFilter(-16777216, mode));
                CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
                combinedDrawable.setIconSize(AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f));
                createSimpleSelectorCircleDrawable = combinedDrawable;
            } else {
                StateListAnimator stateListAnimator = new StateListAnimator();
                ImageView imageView6 = this.floatingButton;
                property = View.TRANSLATION_Z;
                stateListAnimator.addState(new int[]{16842919}, ObjectAnimator.ofFloat(imageView6, (Property<ImageView, Float>) property, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f)).setDuration(200L));
                stateListAnimator.addState(new int[0], ObjectAnimator.ofFloat(this.floatingButton, (Property<ImageView, Float>) property, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(2.0f)).setDuration(200L));
                this.backspaceButton.setStateListAnimator(stateListAnimator);
                this.backspaceButton.setOutlineProvider(new ViewOutlineProvider() {
                    AnonymousClass32() {
                    }

                    @Override
                    public void getOutline(View view6, Outline outline) {
                        outline.setOval(0, 0, view6.getMeasuredWidth(), view6.getMeasuredHeight());
                    }
                });
            }
            this.backspaceButton.setPadding(0, 0, AndroidUtilities.dp(2.0f), 0);
            this.backspaceButton.setBackground(createSimpleSelectorCircleDrawable);
            this.backspaceButton.setContentDescription(LocaleController.getString(i15));
            this.backspaceButton.setFocusable(true);
            i3 = 4;
            this.bottomTabContainer.addView(this.backspaceButton, LayoutHelper.createFrame((i12 >= 21 ? 40 : 44) - 4, (i12 >= 21 ? 40 : 44) - 4, 51, 10.0f, 0.0f, 10.0f, 0.0f));
            this.shadowLine.setVisibility(8);
            this.bottomTabContainerBackground.setVisibility(8);
        }
        addView(this.pager, 0, LayoutHelper.createFrame(-1, -1, 51));
        CorrectlyMeasuringTextView correctlyMeasuringTextView = new CorrectlyMeasuringTextView(context2);
        this.mediaBanTooltip = correctlyMeasuringTextView;
        correctlyMeasuringTextView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), getThemedColor(Theme.key_chat_gifSaveHintBackground)));
        this.mediaBanTooltip.setTextColor(getThemedColor(Theme.key_chat_gifSaveHintText));
        this.mediaBanTooltip.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(7.0f));
        this.mediaBanTooltip.setGravity(16);
        this.mediaBanTooltip.setTextSize(1, 14.0f);
        this.mediaBanTooltip.setVisibility(i3);
        addView(this.mediaBanTooltip, LayoutHelper.createFrame(-2, -2.0f, 81, 5.0f, 0.0f, 5.0f, 53.0f));
        this.emojiSize = AndroidUtilities.dp(AndroidUtilities.isTablet() ? 40.0f : 32.0f);
        EmojiColorPickerWindow create = EmojiColorPickerWindow.create(context2, resourcesProvider2);
        this.colorPickerView = create;
        create.setOnSelectionUpdateListener(new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                EmojiView.this.lambda$new$10((Integer) obj, (Integer) obj2);
            }
        });
        this.currentPage = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
        Emoji.loadRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
        setAllow(z2, z3, false);
    }

    public static java.lang.String addColorToCode(java.lang.String r6, java.lang.String r7) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.addColorToCode(java.lang.String, java.lang.String):java.lang.String");
    }

    private void animateSearchField(int i) {
        RecyclerListView listViewForType = getListViewForType(i);
        int dp = AndroidUtilities.dp(i == 1 ? 38.0f : 48.0f);
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = listViewForType.findViewHolderForAdapterPosition(0);
        if (findViewHolderForAdapterPosition != null) {
            int bottom = findViewHolderForAdapterPosition.itemView.getBottom();
            int i2 = this.tabsMinusDy[i];
            float f = (bottom - (dp + i2)) / this.searchFieldHeight;
            if (f > 0.0f || f < 1.0f) {
                animateSearchField(i, f > 0.5f, i2);
            }
        }
    }

    private void animateSearchField(int i, boolean z, int i2) {
        if (i == 2 || getListViewForType(i).findViewHolderForAdapterPosition(0) == null) {
            return;
        }
        AnonymousClass43 anonymousClass43 = new LinearSmoothScroller(getContext()) {
            final int val$tabsMinusDy;

            AnonymousClass43(Context context, int i22) {
                super(context);
                r3 = i22;
            }

            @Override
            public int calculateDtToFit(int i3, int i22, int i32, int i4, int i5) {
                return super.calculateDtToFit(i3, i22, i32, i4, i5) + r3;
            }

            @Override
            public int calculateTimeForDeceleration(int i3) {
                return super.calculateTimeForDeceleration(i3) * 16;
            }

            @Override
            public int getVerticalSnapPreference() {
                return -1;
            }
        };
        anonymousClass43.setTargetPosition(!z ? 1 : 0);
        getLayoutManagerForType(i).startSmoothScroll(anonymousClass43);
    }

    public void animateTabsY(final int i) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && i != 0) {
            float dpf2 = AndroidUtilities.dpf2(i == 1 ? 36.0f : 48.0f);
            float f = this.tabsMinusDy[i] / (-dpf2);
            if (f <= 0.0f || f >= 1.0f) {
                animateSearchField(i);
                return;
            }
            View tabsForType = getTabsForType(i);
            int i2 = f > 0.5f ? (int) (-Math.ceil(dpf2)) : 0;
            if (f > 0.5f) {
                animateSearchField(i, false, i2);
            }
            if (i == 1) {
                checkEmojiShadow(i2);
            }
            ObjectAnimator[] objectAnimatorArr = this.tabsYAnimators;
            ObjectAnimator objectAnimator = objectAnimatorArr[i];
            if (objectAnimator == null) {
                objectAnimatorArr[i] = ObjectAnimator.ofFloat(tabsForType, (Property<View, Float>) View.TRANSLATION_Y, tabsForType.getTranslationY(), i2);
                this.tabsYAnimators[i].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        EmojiView.this.lambda$animateTabsY$12(i, valueAnimator);
                    }
                });
                this.tabsYAnimators[i].setDuration(200L);
            } else {
                objectAnimator.setFloatValues(tabsForType.getTranslationY(), i2);
            }
            this.tabsYAnimators[i].start();
        }
    }

    public void checkBottomTabScroll(float f) {
        if (SystemClock.elapsedRealtime() - this.shownBottomTabAfterClick < ViewConfiguration.getTapTimeout()) {
            return;
        }
        this.lastBottomScrollDy += f;
        int dp = AndroidUtilities.dp(this.pager.getCurrentItem() == 0 ? 38.0f : 48.0f);
        float f2 = this.lastBottomScrollDy;
        if (f2 >= dp) {
            showBottomTab(false, true);
            return;
        }
        if (f2 <= (-dp)) {
            showBottomTab(true, true);
        } else {
            if ((this.bottomTabContainer.getTag() != null || this.lastBottomScrollDy >= 0.0f) && (this.bottomTabContainer.getTag() == null || this.lastBottomScrollDy <= 0.0f)) {
                return;
            }
            this.lastBottomScrollDy = 0.0f;
        }
    }

    private void checkDocuments(boolean z) {
        if (z) {
            updateRecentGifs();
            return;
        }
        int size = this.recentStickers.size();
        int size2 = this.favouriteStickers.size();
        this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0, true);
        this.favouriteStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(2);
        this.premiumStickers = UserConfig.getInstance(this.currentAccount).isPremium() ? MediaDataController.getInstance(this.currentAccount).getRecentStickers(7) : new ArrayList<>();
        for (int i = 0; i < this.favouriteStickers.size(); i++) {
            TLRPC.Document document = (TLRPC.Document) this.favouriteStickers.get(i);
            int i2 = 0;
            while (true) {
                if (i2 < this.recentStickers.size()) {
                    TLRPC.Document document2 = (TLRPC.Document) this.recentStickers.get(i2);
                    if (document2.dc_id == document.dc_id && document2.id == document.id) {
                        this.recentStickers.remove(i2);
                        break;
                    }
                    i2++;
                }
            }
        }
        if (MessagesController.getInstance(this.currentAccount).premiumFeaturesBlocked()) {
            int i3 = 0;
            while (i3 < this.favouriteStickers.size()) {
                if (MessageObject.isPremiumSticker((TLRPC.Document) this.favouriteStickers.get(i3))) {
                    this.favouriteStickers.remove(i3);
                    i3--;
                }
                i3++;
            }
            int i4 = 0;
            while (i4 < this.recentStickers.size()) {
                if (MessageObject.isPremiumSticker((TLRPC.Document) this.recentStickers.get(i4))) {
                    this.recentStickers.remove(i4);
                    i4--;
                }
                i4++;
            }
        }
        if (size != this.recentStickers.size() || size2 != this.favouriteStickers.size()) {
            updateStickerTabs(false);
        }
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        checkPanels();
    }

    public void checkEmojiSearchFieldScroll(boolean z) {
        EmojiGridView emojiGridView;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition == null) {
                this.emojiSearchField.showShadow(true, !z);
            } else {
                this.emojiSearchField.showShadow(findViewHolderForAdapterPosition.itemView.getTop() < this.emojiGridView.getPaddingTop(), !z);
            }
            showEmojiShadow(false, !z);
            return;
        }
        if (this.emojiSearchField == null || (emojiGridView = this.emojiGridView) == null) {
            return;
        }
        if (emojiGridView.findViewHolderForAdapterPosition(0) != null) {
            this.emojiSearchField.setTranslationY(r0.itemView.getTop());
        } else {
            this.emojiSearchField.setTranslationY(-this.searchFieldHeight);
        }
        this.emojiSearchField.showShadow(false, !z);
        checkEmojiShadow(Math.round(this.emojiTabs.getTranslationY()));
    }

    private void checkEmojiShadow(int i) {
        ObjectAnimator objectAnimator = this.tabsYAnimators[1];
        if (objectAnimator == null || !objectAnimator.isRunning()) {
            boolean z = false;
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0);
            int dp = AndroidUtilities.dp(38.0f) + i;
            if (dp > 0 && (findViewHolderForAdapterPosition == null || findViewHolderForAdapterPosition.itemView.getBottom() < dp)) {
                z = true;
            }
            showEmojiShadow(z, !this.isLayout);
        }
    }

    public void checkEmojiTabY(View view, int i) {
        EmojiTabsStrip emojiTabsStrip;
        float max;
        EmojiGridView emojiGridView;
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (view == null) {
            emojiTabsStrip = this.emojiTabs;
            this.tabsMinusDy[1] = 0;
            max = 0;
        } else {
            if (view.getVisibility() != 0 || this.emojiSmoothScrolling) {
                return;
            }
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
                return;
            }
            if (i > 0 && (emojiGridView = this.emojiGridView) != null && emojiGridView.getVisibility() == 0 && (findViewHolderForAdapterPosition = this.emojiGridView.findViewHolderForAdapterPosition(0)) != null) {
                if (findViewHolderForAdapterPosition.itemView.getTop() + (this.needEmojiSearch ? this.searchFieldHeight : 0) >= this.emojiGridView.getPaddingTop()) {
                    return;
                }
            }
            int[] iArr = this.tabsMinusDy;
            int i2 = iArr[1] - i;
            iArr[1] = i2;
            if (i2 > 0) {
                iArr[1] = 0;
            } else if (i2 < (-AndroidUtilities.dp(108.0f))) {
                this.tabsMinusDy[1] = -AndroidUtilities.dp(108.0f);
            }
            emojiTabsStrip = this.emojiTabs;
            max = Math.max(-AndroidUtilities.dp(36.0f), this.tabsMinusDy[1]);
        }
        emojiTabsStrip.setTranslationY(max);
    }

    public void checkGifSearchFieldScroll(boolean z) {
        int findLastVisibleItemPosition;
        RecyclerListView recyclerListView = this.gifGridView;
        if (recyclerListView != null && (recyclerListView.getAdapter() instanceof GifAdapter)) {
            GifAdapter gifAdapter = (GifAdapter) this.gifGridView.getAdapter();
            if (!gifAdapter.searchEndReached && gifAdapter.reqId == 0 && !gifAdapter.results.isEmpty() && (findLastVisibleItemPosition = this.gifLayoutManager.findLastVisibleItemPosition()) != -1 && findLastVisibleItemPosition > this.gifLayoutManager.getItemCount() - 5) {
                gifAdapter.search(gifAdapter.lastSearchImageString, gifAdapter.nextSearchOffset, true, gifAdapter.lastSearchIsEmoji, gifAdapter.lastSearchIsEmoji);
            }
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) {
            SearchField searchField = this.gifSearchField;
            if (searchField == null || this.gifGridView == null) {
                return;
            }
            searchField.showShadow(true, !z);
            return;
        }
        RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.gifGridView.findViewHolderForAdapterPosition(0);
        if (findViewHolderForAdapterPosition == null) {
            this.gifSearchField.showShadow(true, !z);
        } else {
            this.gifSearchField.showShadow(findViewHolderForAdapterPosition.itemView.getTop() < this.gifGridView.getPaddingTop(), !z);
        }
    }

    public void checkGridVisibility(int i, float f) {
        FrameLayout frameLayout;
        if (this.stickersContainer == null || this.gifContainer == null) {
            return;
        }
        if (i == 0) {
            this.emojiGridView.setVisibility(0);
            this.gifGridView.setVisibility(f == 0.0f ? 8 : 0);
            this.gifTabs.setVisibility(f == 0.0f ? 8 : 0);
            this.stickersGridView.setVisibility(8);
            FrameLayout frameLayout2 = this.stickersTabContainer;
            if (frameLayout2 != null) {
                frameLayout2.setVisibility(8);
                return;
            }
            return;
        }
        if (i == 1) {
            this.emojiGridView.setVisibility(8);
            this.gifGridView.setVisibility(0);
            this.gifTabs.setVisibility(0);
            this.stickersGridView.setVisibility(f == 0.0f ? 8 : 0);
            frameLayout = this.stickersTabContainer;
            if (frameLayout == null) {
                return;
            }
            if (f == 0.0f) {
                r1 = 8;
            }
        } else {
            if (i != 2) {
                return;
            }
            this.emojiGridView.setVisibility(8);
            this.gifGridView.setVisibility(8);
            this.gifTabs.setVisibility(8);
            this.stickersGridView.setVisibility(0);
            frameLayout = this.stickersTabContainer;
            if (frameLayout == null) {
                return;
            }
        }
        frameLayout.setVisibility(r1);
    }

    private void checkPanels() {
        int findFirstVisibleItemPosition;
        if (this.stickersTab == null || (findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition()) == -1) {
            return;
        }
        int i = this.favTabNum;
        if (i <= 0 && (i = this.recentTabNum) <= 0) {
            i = this.stickersTabOffset;
        }
        this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition), i);
    }

    public void checkScroll(int i) {
        int findFirstVisibleItemPosition;
        int findFirstVisibleItemPosition2;
        if (i == 0) {
            if (this.ignoreStickersScroll || (findFirstVisibleItemPosition2 = this.stickersLayoutManager.findFirstVisibleItemPosition()) == -1 || this.stickersGridView == null) {
                return;
            }
            int i2 = this.favTabNum;
            if (i2 <= 0 && (i2 = this.recentTabNum) <= 0) {
                i2 = this.stickersTabOffset;
            }
            this.stickersTab.onPageScrolled(this.stickersGridAdapter.getTabForPosition(findFirstVisibleItemPosition2), i2);
            return;
        }
        if (i == 2) {
            RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
            GifAdapter gifAdapter = this.gifAdapter;
            if (adapter != gifAdapter || gifAdapter.trendingSectionItem < 0 || this.gifTrendingTabNum < 0 || this.gifRecentTabNum < 0 || (findFirstVisibleItemPosition = this.gifLayoutManager.findFirstVisibleItemPosition()) == -1) {
                return;
            }
            this.gifTabs.onPageScrolled(findFirstVisibleItemPosition >= this.gifAdapter.trendingSectionItem ? this.gifTrendingTabNum : this.gifRecentTabNum, 0);
        }
    }

    public void checkStickersSearchFieldScroll(boolean z) {
        RecyclerListView recyclerListView;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.stickersGridView.findViewHolderForAdapterPosition(0);
            if (findViewHolderForAdapterPosition == null) {
                this.stickersSearchField.showShadow(true, !z);
                return;
            } else {
                this.stickersSearchField.showShadow(findViewHolderForAdapterPosition.itemView.getTop() < this.stickersGridView.getPaddingTop(), !z);
                return;
            }
        }
        if (this.stickersSearchField == null || (recyclerListView = this.stickersGridView) == null) {
            return;
        }
        if (recyclerListView.findViewHolderForAdapterPosition(0) != null) {
            this.stickersSearchField.setTranslationY(r0.itemView.getTop());
        } else {
            this.stickersSearchField.setTranslationY(-this.searchFieldHeight);
        }
        this.stickersSearchField.showShadow(false, !z);
    }

    public void checkTabsY(int i, int i2) {
        RecyclerView.ViewHolder findViewHolderForAdapterPosition;
        if (i == 1) {
            checkEmojiTabY(this.emojiGridView, i2);
            return;
        }
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && !this.ignoreStickersScroll) {
            RecyclerListView listViewForType = getListViewForType(i);
            if (i2 <= 0 || listViewForType == null || listViewForType.getVisibility() != 0 || (findViewHolderForAdapterPosition = listViewForType.findViewHolderForAdapterPosition(0)) == null || findViewHolderForAdapterPosition.itemView.getTop() + this.searchFieldHeight < listViewForType.getPaddingTop()) {
                int[] iArr = this.tabsMinusDy;
                int i3 = iArr[i] - i2;
                iArr[i] = i3;
                if (i3 > 0) {
                    iArr[i] = 0;
                } else if (i3 < (-AndroidUtilities.dp(288.0f))) {
                    this.tabsMinusDy[i] = -AndroidUtilities.dp(288.0f);
                }
                if (i == 0) {
                    updateStickerTabsPosition();
                } else {
                    getTabsForType(i).setTranslationY(Math.max(-AndroidUtilities.dp(48.0f), this.tabsMinusDy[i]));
                }
            }
        }
    }

    public void createStickersChooseActionTracker() {
        AnonymousClass33 anonymousClass33 = new ChooseStickerActionTracker(this.currentAccount, this.delegate.getDialogId(), this.delegate.getThreadId()) {
            AnonymousClass33(int i, long j, long j2) {
                super(i, j, j2);
            }

            @Override
            public boolean isShown() {
                return EmojiView.this.delegate != null && EmojiView.this.getVisibility() == 0 && EmojiView.this.stickersContainerAttached;
            }
        };
        this.chooseStickerActionTracker = anonymousClass33;
        anonymousClass33.checkVisibility();
    }

    private GridLayoutManager getLayoutManagerForType(int i) {
        if (i == 0) {
            return this.stickersLayoutManager;
        }
        if (i == 1) {
            return this.emojiLayoutManager;
        }
        if (i == 2) {
            return this.gifLayoutManager;
        }
        throw new IllegalArgumentException("Unexpected argument: " + i);
    }

    private RecyclerListView getListViewForType(int i) {
        if (i == 0) {
            return this.stickersGridView;
        }
        if (i == 1) {
            return this.emojiGridView;
        }
        if (i == 2) {
            return this.gifGridView;
        }
        throw new IllegalArgumentException("Unexpected argument: " + i);
    }

    public SearchField getSearchFieldForType(int i) {
        if (i == 0) {
            return this.stickersSearchField;
        }
        if (i == 1) {
            return this.emojiSearchField;
        }
        if (i == 2) {
            return this.gifSearchField;
        }
        throw new IllegalArgumentException("Unexpected argument: " + i);
    }

    private View getTabsForType(int i) {
        if (i == 0) {
            return this.stickersTab;
        }
        if (i == 1) {
            return this.emojiTabs;
        }
        if (i == 2) {
            return this.gifTabs;
        }
        throw new IllegalArgumentException("Unexpected argument: " + i);
    }

    public int getThemedColor(int i) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.getColor(i) : Theme.getColor(i);
    }

    public void lambda$animateTabsY$12(int i, ValueAnimator valueAnimator) {
        this.tabsMinusDy[i] = (int) ((Float) valueAnimator.getAnimatedValue()).floatValue();
    }

    public boolean lambda$new$1(Theme.ResourcesProvider resourcesProvider, View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.emojiGridView, getMeasuredHeight(), null, this.contentPreviewViewerDelegate, resourcesProvider);
    }

    public void lambda$new$10(Integer num, Integer num2) {
        ImageViewEmoji imageViewEmoji = this.emojiTouchedView;
        if (imageViewEmoji == null || !(imageViewEmoji.getDrawable() instanceof CompoundEmoji.CompoundEmojiDrawable)) {
            return;
        }
        ((CompoundEmoji.CompoundEmojiDrawable) this.emojiTouchedView.getDrawable()).update(num.intValue(), num2.intValue());
        String str = (String) this.emojiTouchedView.getTag();
        if (num.intValue() == -1 && num2.intValue() == -1) {
            Emoji.emojiColor.remove(str);
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(num.intValue() >= 0 ? CompoundEmoji.skinTones.get(num.intValue()) : "");
            sb.append("\u200d");
            sb.append(num2.intValue() >= 0 ? CompoundEmoji.skinTones.get(num2.intValue()) : "");
            Emoji.emojiColor.put(str, sb.toString());
        }
        Emoji.saveEmojiColors();
    }

    public void lambda$new$17() {
        EmojiGridAdapter emojiGridAdapter = this.emojiAdapter;
        if (emojiGridAdapter != null) {
            emojiGridAdapter.notifyDataSetChanged(true);
        }
    }

    public static Integer lambda$new$2(Integer num) {
        return 0;
    }

    public void lambda$new$3() {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null) {
            emojiViewDelegate.onEmojiSettingsClick(this.emojiAdapter.frozenEmojiPacks);
        }
    }

    public boolean lambda$new$4(Theme.ResourcesProvider resourcesProvider, View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.gifGridView, 0, this.gifOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider);
    }

    public void lambda$new$5(View view, int i) {
        EmojiViewDelegate emojiViewDelegate;
        Object obj;
        Object obj2;
        boolean z;
        int i2;
        String str;
        if (this.delegate == null) {
            return;
        }
        if (this.gifAdapter.addSearch) {
            i--;
        }
        RecyclerView.Adapter adapter = this.gifGridView.getAdapter();
        GifAdapter gifAdapter = this.gifAdapter;
        if (adapter != gifAdapter) {
            RecyclerView.Adapter adapter2 = this.gifGridView.getAdapter();
            GifAdapter gifAdapter2 = this.gifSearchAdapter;
            if (adapter2 != gifAdapter2 || i < 0 || i >= gifAdapter2.results.size()) {
                return;
            }
            this.delegate.lambda$onGifSelected$1(view, this.gifSearchAdapter.results.get(i), this.gifSearchAdapter.lastSearchImageString, this.gifSearchAdapter.bot, true, 0);
            updateRecentGifs();
            return;
        }
        if (i < 0) {
            return;
        }
        if (i < gifAdapter.recentItemsCount) {
            emojiViewDelegate = this.delegate;
            obj = this.recentGifs.get(i);
            z = true;
            i2 = 0;
            str = null;
            obj2 = "gif";
        } else {
            if (this.gifAdapter.recentItemsCount > 0) {
                i = (i - this.gifAdapter.recentItemsCount) - 1;
            }
            if (i < 0 || i >= this.gifAdapter.results.size()) {
                return;
            }
            emojiViewDelegate = this.delegate;
            obj = this.gifAdapter.results.get(i);
            obj2 = this.gifAdapter.bot;
            z = true;
            i2 = 0;
            str = null;
        }
        emojiViewDelegate.lambda$onGifSelected$1(view, obj, str, obj2, z, i2);
    }

    public void lambda$new$6(int i) {
        if (i == this.gifTrendingTabNum && this.gifAdapter.results.isEmpty()) {
            return;
        }
        this.gifGridView.stopScroll();
        this.gifTabs.onPageScrolled(i, 0);
        int i2 = 1;
        if (i == this.gifRecentTabNum || i == this.gifTrendingTabNum) {
            this.gifSearchField.searchEditText.setText("");
            if (i != this.gifTrendingTabNum || this.gifAdapter.trendingSectionItem < 1) {
                GifLayoutManager gifLayoutManager = this.gifLayoutManager;
                EmojiViewDelegate emojiViewDelegate = this.delegate;
                if (emojiViewDelegate != null && emojiViewDelegate.isExpanded()) {
                    i2 = 0;
                }
                gifLayoutManager.scrollToPositionWithOffset(i2, 0);
            } else {
                this.gifLayoutManager.scrollToPositionWithOffset(this.gifAdapter.trendingSectionItem, -AndroidUtilities.dp(4.0f));
            }
            if (i == this.gifTrendingTabNum) {
                ArrayList<String> arrayList = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
                if (!arrayList.isEmpty()) {
                    this.gifSearchPreloader.preload(arrayList.get(0));
                }
            }
        } else {
            ArrayList<String> arrayList2 = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
            this.gifSearchAdapter.searchEmoji(arrayList2.get(i - this.gifFirstEmojiTabNum));
            int i3 = i - this.gifFirstEmojiTabNum;
            if (i3 > 0) {
                this.gifSearchPreloader.preload(arrayList2.get(i3 - 1));
            }
            if (i - this.gifFirstEmojiTabNum < arrayList2.size() - 1) {
                this.gifSearchPreloader.preload(arrayList2.get((i - this.gifFirstEmojiTabNum) + 1));
            }
        }
        resetTabsY(2);
    }

    public boolean lambda$new$7(Theme.ResourcesProvider resourcesProvider, View view, MotionEvent motionEvent) {
        return ContentPreviewViewer.getInstance().onTouch(motionEvent, this.stickersGridView, getMeasuredHeight(), this.stickersOnItemClickListener, this.contentPreviewViewerDelegate, resourcesProvider);
    }

    public void lambda$new$8(View view, int i) {
        String str;
        RecyclerView.Adapter adapter = this.stickersGridView.getAdapter();
        StickersSearchGridAdapter stickersSearchGridAdapter = this.stickersSearchGridAdapter;
        if (adapter == stickersSearchGridAdapter) {
            String str2 = stickersSearchGridAdapter.searchQuery;
            TLRPC.StickerSetCovered stickerSetCovered = (TLRPC.StickerSetCovered) this.stickersSearchGridAdapter.positionsToSets.get(i);
            if (stickerSetCovered != null) {
                this.delegate.onShowStickerSet(stickerSetCovered.set, null, false);
                return;
            }
            str = str2;
        } else {
            str = null;
        }
        if (view instanceof StickerEmojiCell) {
            StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
            if (stickerEmojiCell.getSticker() != null && MessageObject.isPremiumSticker(stickerEmojiCell.getSticker()) && !AccountInstance.getInstance(this.currentAccount).getUserConfig().isPremium()) {
                ContentPreviewViewer.getInstance().showMenuFor(stickerEmojiCell);
                return;
            }
            ContentPreviewViewer.getInstance().reset();
            if (stickerEmojiCell.isDisabled()) {
                return;
            }
            stickerEmojiCell.disable();
            this.delegate.onStickerSelected(stickerEmojiCell, stickerEmojiCell.getSticker(), str, stickerEmojiCell.getParentObject(), stickerEmojiCell.getSendAnimationData(), true, 0);
        }
    }

    public void lambda$new$9(int i) {
        if (this.firstTabUpdate) {
            return;
        }
        if (i == this.trendingTabNum) {
            openTrendingStickers(null);
            return;
        }
        SearchField searchField = this.stickersSearchField;
        if (searchField != null && searchField.isCategorySelected()) {
            this.stickersSearchField.search(null, false);
            this.stickersSearchField.categoriesListView.selectCategory((StickerCategoriesListView.EmojiCategory) null);
        }
        if (i == this.recentTabNum) {
            this.stickersGridView.stopScroll();
            scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack("recent"), 0);
            resetTabsY(0);
            ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
            int i2 = this.recentTabNum;
            scrollSlidingTabStrip.onPageScrolled(i2, i2 > 0 ? i2 : this.stickersTabOffset);
            return;
        }
        if (i == this.favTabNum) {
            this.stickersGridView.stopScroll();
            scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack("fav"), 0);
            resetTabsY(0);
            ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.stickersTab;
            int i3 = this.favTabNum;
            scrollSlidingTabStrip2.onPageScrolled(i3, i3 > 0 ? i3 : this.stickersTabOffset);
            return;
        }
        if (i == this.premiumTabNum) {
            this.stickersGridView.stopScroll();
            scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack("premium"), 0);
            resetTabsY(0);
            ScrollSlidingTabStrip scrollSlidingTabStrip3 = this.stickersTab;
            int i4 = this.premiumTabNum;
            scrollSlidingTabStrip3.onPageScrolled(i4, i4 > 0 ? i4 : this.stickersTabOffset);
            return;
        }
        int i5 = i - this.stickersTabOffset;
        if (i5 >= this.stickerSets.size()) {
            return;
        }
        if (i5 >= this.stickerSets.size()) {
            i5 = this.stickerSets.size() - 1;
        }
        this.firstStickersAttach = false;
        this.stickersGridView.stopScroll();
        scrollStickersToPosition(this.stickersGridAdapter.getPositionForPack(this.stickerSets.get(i5)), 0);
        resetTabsY(0);
        checkScroll(0);
        int i6 = this.favTabNum;
        if (i6 <= 0 && (i6 = this.recentTabNum) <= 0) {
            i6 = this.stickersTabOffset;
        }
        this.stickersTab.onPageScrolled(i, i6);
        this.expandStickersByDragg = false;
        updateStickerTabsPosition();
    }

    public void lambda$onAttachedToWindow$15() {
        updateStickerTabs(false);
        reloadStickersAdapter();
    }

    public void lambda$onOpen$14() {
        ArrayList<EmojiPack> emojipacks = getEmojipacks();
        for (int i = 0; i < emojipacks.size(); i++) {
            if (emojipacks.get(i).forGroup) {
                int i2 = this.emojiAdapter.sectionToPosition.get(EmojiData.dataColored.length + i);
                this.emojiGridView.stopScroll();
                updateEmojiTabsPosition(i2);
                scrollEmojisToPosition(i2, AndroidUtilities.dp(-9.0f));
                checkEmojiTabY(null, 0);
            }
        }
    }

    public void lambda$postBackspaceRunnable$13(int i) {
        if (this.backspacePressed) {
            EmojiViewDelegate emojiViewDelegate = this.delegate;
            if (emojiViewDelegate != null && emojiViewDelegate.onBackspace()) {
                try {
                    this.backspaceButton.performHapticFeedback(3);
                } catch (Exception unused) {
                }
            }
            this.backspaceOnce = true;
            postBackspaceRunnable(Math.max(50, i - 100));
        }
    }

    public void lambda$sendEmoji$0() {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
        this.fragment.presentFragment(new AnonymousClass3(bundle));
    }

    public void lambda$showBottomTab$11(ValueAnimator valueAnimator) {
        this.bottomTabAdditionalTranslation = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        updateBottomTabContainerPosition();
    }

    public void lambda$showStickerBanHint$16(boolean z, boolean z2) {
        showStickerBanHint(false, z, z2);
    }

    public void onPageScrolled(int i, int i2, int i3) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate == null) {
            return;
        }
        int i4 = 0;
        if (i == 1) {
            if (i3 != 0) {
                i4 = 2;
            }
        } else if (i == 2) {
            emojiViewDelegate.onTabOpened(3);
            return;
        }
        emojiViewDelegate.onTabOpened(i4);
    }

    public void openPremiumAnimatedEmojiFeature() {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null) {
            emojiViewDelegate.onAnimatedEmojiUnlockClick();
        }
    }

    public void openSearch(org.telegram.ui.Components.EmojiView.SearchField r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.openSearch(org.telegram.ui.Components.EmojiView$SearchField):void");
    }

    public void openTrendingStickers(TLRPC.StickerSetCovered stickerSetCovered) {
        this.delegate.showTrendingStickersAlert(new TrendingStickersLayout(getContext(), new TrendingStickersLayout.Delegate() {
            AnonymousClass35() {
            }

            @Override
            public boolean canSchedule() {
                return EmojiView.this.delegate.canSchedule();
            }

            @Override
            public boolean canSendSticker() {
                return true;
            }

            @Override
            public String[] getLastSearchKeyboardLanguage() {
                return EmojiView.this.lastSearchKeyboardLanguage;
            }

            @Override
            public boolean isInScheduleMode() {
                return EmojiView.this.delegate.isInScheduleMode();
            }

            @Override
            public boolean onListViewInterceptTouchEvent(RecyclerListView recyclerListView, MotionEvent motionEvent) {
                return ContentPreviewViewer.getInstance().onInterceptTouchEvent(motionEvent, recyclerListView, EmojiView.this.getMeasuredHeight(), EmojiView.this.contentPreviewViewerDelegate, EmojiView.this.resourcesProvider);
            }

            @Override
            public boolean onListViewTouchEvent(RecyclerListView recyclerListView, RecyclerListView.OnItemClickListener onItemClickListener, MotionEvent motionEvent) {
                return ContentPreviewViewer.getInstance().onTouch(motionEvent, recyclerListView, EmojiView.this.getMeasuredHeight(), onItemClickListener, EmojiView.this.contentPreviewViewerDelegate, EmojiView.this.resourcesProvider);
            }

            @Override
            public void onStickerSelected(TLRPC.Document document, Object obj, boolean z, boolean z2, int i) {
                EmojiView.this.delegate.onStickerSelected(null, document, null, obj, null, z2, i);
            }

            @Override
            public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered2, boolean z) {
                EmojiView.this.delegate.onStickerSetAdd(stickerSetCovered2);
                if (z) {
                    EmojiView.this.updateStickerTabs(true);
                }
            }

            @Override
            public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered2) {
                EmojiView.this.delegate.onStickerSetRemove(stickerSetCovered2);
            }

            @Override
            public void setLastSearchKeyboardLanguage(String[] strArr) {
                EmojiView.this.lastSearchKeyboardLanguage = strArr;
            }
        }, this.primaryInstallingStickerSets, this.installingStickerSets, this.removingStickerSets, stickerSetCovered, this.resourcesProvider));
    }

    public void postBackspaceRunnable(final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                EmojiView.this.lambda$postBackspaceRunnable$13(i);
            }
        }, i);
    }

    public void reloadStickersAdapter() {
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        StickersSearchGridAdapter stickersSearchGridAdapter = this.stickersSearchGridAdapter;
        if (stickersSearchGridAdapter != null) {
            stickersSearchGridAdapter.notifyDataSetChanged();
        }
        if (ContentPreviewViewer.getInstance().isVisible()) {
            ContentPreviewViewer.getInstance().close();
        }
        ContentPreviewViewer.getInstance().reset();
    }

    private void resetTabsY(int i) {
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if ((emojiViewDelegate == null || !emojiViewDelegate.isSearchOpened()) && i != 0) {
            View tabsForType = getTabsForType(i);
            this.tabsMinusDy[i] = 0;
            tabsForType.setTranslationY(0);
        }
    }

    public void saveNewPage() {
        ViewPager viewPager = this.pager;
        if (viewPager == null) {
            return;
        }
        int currentItem = viewPager.getCurrentItem();
        int i = currentItem != 2 ? currentItem == 1 ? 2 : 0 : 1;
        if (this.currentPage != i) {
            this.currentPage = i;
            MessagesController.getGlobalEmojiSettings().edit().putInt("selected_page", i).commit();
        }
    }

    public void scrollEmojisToPosition(int i, int i2) {
        View findViewByPosition = this.emojiLayoutManager.findViewByPosition(i);
        int findFirstVisibleItemPosition = this.emojiLayoutManager.findFirstVisibleItemPosition();
        if ((findViewByPosition == null && Math.abs(i - findFirstVisibleItemPosition) > this.emojiLayoutManager.getSpanCount() * 9.0f) || !SharedConfig.animationsEnabled()) {
            this.emojiScrollHelper.setScrollDirection(this.emojiLayoutManager.findFirstVisibleItemPosition() < i ? 0 : 1);
            this.emojiScrollHelper.scrollToPosition(i, i2, false, true);
            return;
        }
        this.ignoreStickersScroll = true;
        AnonymousClass38 anonymousClass38 = new LinearSmoothScrollerCustom(this.emojiGridView.getContext(), 2) {
            AnonymousClass38(Context context, int i3) {
                super(context, i3);
            }

            @Override
            public void onEnd() {
                EmojiView.this.emojiSmoothScrolling = false;
            }

            @Override
            protected void onStart() {
                EmojiView.this.emojiSmoothScrolling = true;
            }
        };
        anonymousClass38.setTargetPosition(i);
        anonymousClass38.setOffset(i2);
        this.emojiLayoutManager.startSmoothScroll(anonymousClass38);
    }

    public void scrollGifsToTop() {
        this.gifLayoutManager.scrollToPositionWithOffset(0, 0);
        resetTabsY(2);
    }

    private void scrollStickersToPosition(int i, int i2) {
        View findViewByPosition = this.stickersLayoutManager.findViewByPosition(i);
        int findFirstVisibleItemPosition = this.stickersLayoutManager.findFirstVisibleItemPosition();
        if (findViewByPosition != null || Math.abs(i - findFirstVisibleItemPosition) <= 40) {
            this.ignoreStickersScroll = true;
            this.stickersGridView.smoothScrollToPosition(i);
        } else {
            this.stickersScrollHelper.setScrollDirection(this.stickersLayoutManager.findFirstVisibleItemPosition() < i ? 0 : 1);
            this.stickersScrollHelper.scrollToPosition(i, i2, false, true);
        }
    }

    public void sendEmoji(ImageViewEmoji imageViewEmoji, String str) {
        EmojiViewDelegate emojiViewDelegate;
        String fixEmoji;
        String str2;
        EmojiViewDelegate emojiViewDelegate2;
        if (imageViewEmoji == null) {
            return;
        }
        if (imageViewEmoji.getSpan() == null) {
            this.shownBottomTabAfterClick = SystemClock.elapsedRealtime();
            showBottomTab(true, true);
            String str3 = str != null ? str : (String) imageViewEmoji.getTag();
            new SpannableStringBuilder().append((CharSequence) str3);
            if (str == null) {
                if (!imageViewEmoji.isRecent && (str2 = Emoji.emojiColor.get(str3)) != null) {
                    str3 = addColorToCode(str3, str2);
                }
                addEmojiToRecent(str3);
                emojiViewDelegate = this.delegate;
                if (emojiViewDelegate == null) {
                    return;
                } else {
                    fixEmoji = Emoji.fixEmoji(str3);
                }
            } else {
                emojiViewDelegate = this.delegate;
                if (emojiViewDelegate == null) {
                    return;
                } else {
                    fixEmoji = Emoji.fixEmoji(str);
                }
            }
            emojiViewDelegate.onEmojiSelected(fixEmoji);
            return;
        }
        if (this.delegate != null) {
            long j = imageViewEmoji.getSpan().documentId;
            TLRPC.Document document = imageViewEmoji.getSpan().document;
            boolean z = imageViewEmoji.pack != null && imageViewEmoji.pack.forGroup;
            if (document == null) {
                for (int i = 0; i < this.emojipacksProcessed.size(); i++) {
                    EmojiPack emojiPack = (EmojiPack) this.emojipacksProcessed.get(i);
                    int i2 = 0;
                    while (true) {
                        ArrayList arrayList = emojiPack.documents;
                        if (arrayList != null && i2 < arrayList.size()) {
                            if (((TLRPC.Document) emojiPack.documents.get(i2)).id == j) {
                                document = (TLRPC.Document) emojiPack.documents.get(i2);
                                break;
                            }
                            i2++;
                        }
                    }
                }
            }
            if (document == null) {
                document = AnimatedEmojiDrawable.findDocument(this.currentAccount, j);
            }
            TLRPC.Document document2 = document;
            String findAnimatedEmojiEmoticon = document2 != null ? MessageObject.findAnimatedEmojiEmoticon(document2) : null;
            if (!MessageObject.isFreeEmoji(document2) && !UserConfig.getInstance(this.currentAccount).isPremium() && (((emojiViewDelegate2 = this.delegate) == null || !emojiViewDelegate2.isUserSelf()) && !this.allowEmojisForNonPremium && !z)) {
                showBottomTab(false, true);
                BaseFragment baseFragment = this.fragment;
                BulletinFactory of = baseFragment != null ? BulletinFactory.of(baseFragment) : BulletinFactory.of(this.bulletinContainer, this.resourcesProvider);
                ((this.premiumBulletin || this.fragment == null) ? of.createEmojiBulletin(document2, AndroidUtilities.replaceTags(LocaleController.getString(R.string.UnlockPremiumEmojiHint)), LocaleController.getString(R.string.PremiumMore), new Runnable() {
                    @Override
                    public final void run() {
                        EmojiView.this.openPremiumAnimatedEmojiFeature();
                    }
                }) : of.createSimpleBulletin(R.raw.saved_messages, AndroidUtilities.replaceTags(LocaleController.getString(R.string.UnlockPremiumEmojiHint2)), LocaleController.getString(R.string.Open), new Runnable() {
                    @Override
                    public final void run() {
                        EmojiView.this.lambda$sendEmoji$0();
                    }
                })).show();
                this.premiumBulletin = !this.premiumBulletin;
                return;
            }
            this.shownBottomTabAfterClick = SystemClock.elapsedRealtime();
            showBottomTab(true, true);
            addEmojiToRecent("animated_" + j);
            this.delegate.onCustomEmojiSelected(j, document2, findAnimatedEmojiEmoticon, imageViewEmoji.isRecent);
        }
    }

    public void showBackspaceButton(boolean z, boolean z2) {
        if (z && this.backspaceButton.getTag() == null) {
            return;
        }
        if (z || this.backspaceButton.getTag() == null) {
            AnimatorSet animatorSet = this.backspaceButtonAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.backspaceButtonAnimation = null;
            }
            this.backspaceButton.setTag(z ? null : 1);
            if (!z2) {
                this.backspaceButton.setAlpha(z ? 1.0f : 0.0f);
                this.backspaceButton.setScaleX(z ? 1.0f : 0.0f);
                this.backspaceButton.setScaleY(z ? 1.0f : 0.0f);
                this.backspaceButton.setVisibility(z ? 0 : 4);
                return;
            }
            if (z) {
                this.backspaceButton.setVisibility(0);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.backspaceButtonAnimation = animatorSet2;
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.backspaceButton, (Property<ImageView, Float>) View.ALPHA, z ? 1.0f : 0.0f), ObjectAnimator.ofFloat(this.backspaceButton, (Property<ImageView, Float>) View.SCALE_X, z ? 1.0f : 0.0f), ObjectAnimator.ofFloat(this.backspaceButton, (Property<ImageView, Float>) View.SCALE_Y, z ? 1.0f : 0.0f));
            this.backspaceButtonAnimation.setDuration(200L);
            this.backspaceButtonAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.backspaceButtonAnimation.addListener(new AnimatorListenerAdapter() {
                final boolean val$show;

                AnonymousClass40(boolean z3) {
                    r2 = z3;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (r2) {
                        return;
                    }
                    EmojiView.this.backspaceButton.setVisibility(4);
                }
            });
            this.backspaceButtonAnimation.start();
        }
    }

    public void showBottomTab(boolean z, boolean z2) {
        float f = 0.0f;
        this.lastBottomScrollDy = 0.0f;
        EmojiViewDelegate emojiViewDelegate = this.delegate;
        if (emojiViewDelegate != null && emojiViewDelegate.isSearchOpened()) {
            z = false;
        }
        if (z && this.bottomTabContainer.getTag() == null) {
            return;
        }
        if (z || this.bottomTabContainer.getTag() == null) {
            ValueAnimator valueAnimator = this.bottomTabContainerAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.bottomTabContainerAnimator = null;
            }
            this.bottomTabContainer.setTag(z ? null : 1);
            if (!z2) {
                if (!z) {
                    f = AndroidUtilities.dp(this.needEmojiSearch ? 45.0f : 50.0f);
                }
                this.bottomTabAdditionalTranslation = f;
                updateBottomTabContainerPosition();
                return;
            }
            float f2 = this.bottomTabAdditionalTranslation;
            if (!z) {
                f = AndroidUtilities.dp(this.needEmojiSearch ? 45.0f : 50.0f);
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(f2, f);
            this.bottomTabContainerAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    EmojiView.this.lambda$showBottomTab$11(valueAnimator2);
                }
            });
            this.bottomTabContainerAnimator.addListener(new AnimatorListenerAdapter() {
                AnonymousClass42() {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (EmojiView.this.bottomTabContainerAnimator != animator) {
                        return;
                    }
                    EmojiView emojiView = EmojiView.this;
                    emojiView.bottomTabAdditionalTranslation = ((Float) emojiView.bottomTabContainerAnimator.getAnimatedValue()).floatValue();
                    EmojiView.this.updateBottomTabContainerPosition();
                    EmojiView.this.bottomTabContainerAnimator = null;
                }
            });
            this.bottomTabContainerAnimator.setDuration(380L);
            this.bottomTabContainerAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.bottomTabContainerAnimator.start();
        }
    }

    private void showEmojiShadow(boolean z, boolean z2) {
        if (z && this.emojiTabsShadow.getTag() == null) {
            return;
        }
        if (z || this.emojiTabsShadow.getTag() == null) {
            AnimatorSet animatorSet = this.emojiTabShadowAnimator;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.emojiTabShadowAnimator = null;
            }
            this.emojiTabsShadow.setTag(z ? null : 1);
            if (!z2) {
                this.emojiTabsShadow.setAlpha(z ? 1.0f : 0.0f);
                return;
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.emojiTabShadowAnimator = animatorSet2;
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.emojiTabsShadow, (Property<View, Float>) View.ALPHA, z ? 1.0f : 0.0f));
            this.emojiTabShadowAnimator.setDuration(200L);
            this.emojiTabShadowAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.emojiTabShadowAnimator.addListener(new AnimatorListenerAdapter() {
                AnonymousClass37() {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    EmojiView.this.emojiTabShadowAnimator = null;
                }
            });
            this.emojiTabShadowAnimator.start();
        }
    }

    public void showStickerSettingsButton(boolean z, boolean z2) {
        ImageView imageView = this.stickerSettingsButton;
        if (imageView == null) {
            return;
        }
        if (z && imageView.getTag() == null) {
            return;
        }
        if (z || this.stickerSettingsButton.getTag() == null) {
            AnimatorSet animatorSet = this.stickersButtonAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.stickersButtonAnimation = null;
            }
            this.stickerSettingsButton.setTag(z ? null : 1);
            if (!z2) {
                this.stickerSettingsButton.setAlpha(z ? 1.0f : 0.0f);
                this.stickerSettingsButton.setScaleX(z ? 1.0f : 0.0f);
                this.stickerSettingsButton.setScaleY(z ? 1.0f : 0.0f);
                this.stickerSettingsButton.setVisibility(z ? 0 : 4);
                return;
            }
            if (z) {
                this.stickerSettingsButton.setVisibility(0);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.stickersButtonAnimation = animatorSet2;
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.stickerSettingsButton, (Property<ImageView, Float>) View.ALPHA, z ? 1.0f : 0.0f), ObjectAnimator.ofFloat(this.stickerSettingsButton, (Property<ImageView, Float>) View.SCALE_X, z ? 1.0f : 0.0f), ObjectAnimator.ofFloat(this.stickerSettingsButton, (Property<ImageView, Float>) View.SCALE_Y, z ? 1.0f : 0.0f));
            this.stickersButtonAnimation.setDuration(200L);
            this.stickersButtonAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.stickersButtonAnimation.addListener(new AnimatorListenerAdapter() {
                final boolean val$show;

                AnonymousClass41(boolean z3) {
                    r2 = z3;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (r2) {
                        return;
                    }
                    EmojiView.this.stickerSettingsButton.setVisibility(4);
                }
            });
            this.stickersButtonAnimation.start();
        }
    }

    public void startStopVisibleGifs(boolean z) {
        RecyclerListView recyclerListView = this.gifGridView;
        if (recyclerListView == null) {
            return;
        }
        int childCount = recyclerListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.gifGridView.getChildAt(i);
            if (childAt instanceof ContextLinkCell) {
                ImageReceiver photoImage = ((ContextLinkCell) childAt).getPhotoImage();
                if (z) {
                    photoImage.setAllowStartAnimation(true);
                    photoImage.startAnimation();
                } else {
                    photoImage.setAllowStartAnimation(false);
                    photoImage.stopAnimation();
                }
            }
        }
    }

    public void stopAnimatingTabsY(int i) {
        ObjectAnimator objectAnimator = this.tabsYAnimators[i];
        if (objectAnimator == null || !objectAnimator.isRunning()) {
            return;
        }
        this.tabsYAnimators[i].cancel();
    }

    public void updateBottomTabContainerPosition() {
        BaseFragment baseFragment;
        if (((View) getParent()) != null) {
            float y = (getY() + (getLayoutParams().height > 0 ? getLayoutParams().height : getMeasuredHeight())) - ((AndroidUtilities.isInMultiwindow || ((baseFragment = this.fragment) != null && baseFragment.isInBubbleMode())) ? AndroidUtilities.dp(1.0f) : r0.getHeight());
            if (this.bottomTabContainer.getTop() - y < 0.0f || !this.fixBottomTabContainerTranslation) {
                y = 0.0f;
            }
            float f = -y;
            this.bottomTabMainTranslation = f;
            this.bottomTabContainer.setTranslationY(f + this.bottomTabAdditionalTranslation);
            if (this.needEmojiSearch) {
                this.bulletinContainer.setTranslationY(this.bottomTabMainTranslation + this.bottomTabAdditionalTranslation);
            }
        }
    }

    public void updateEmojiHeaders() {
        if (this.emojiGridView == null) {
            return;
        }
        for (int i = 0; i < this.emojiGridView.getChildCount(); i++) {
            View childAt = this.emojiGridView.getChildAt(i);
            if (childAt instanceof EmojiPackHeader) {
                ((EmojiPackHeader) childAt).updateState(true);
            }
        }
    }

    public void updateEmojiTabsPosition() {
        updateEmojiTabsPosition(this.emojiLayoutManager.findFirstCompletelyVisibleItemPosition());
    }

    public void updateEmojiTabsPosition(int i) {
        if (this.emojiSmoothScrolling) {
            return;
        }
        int i2 = -1;
        if (i != -1) {
            int i3 = 0;
            int size = getRecentEmoji().size() + (this.needEmojiSearch ? 1 : 0) + (this.emojiAdapter.trendingHeaderRow >= 0 ? 3 : 0);
            if (i >= size) {
                int i4 = 0;
                while (true) {
                    String[][] strArr = EmojiData.dataColored;
                    if (i4 >= strArr.length) {
                        break;
                    }
                    size += strArr[i4].length + 1;
                    if (i < size) {
                        i2 = i4 + 1;
                        break;
                    }
                    i4++;
                }
                if (i2 < 0) {
                    ArrayList<EmojiPack> emojipacks = getEmojipacks();
                    int size2 = this.emojiAdapter.packStartPosition.size() - 1;
                    while (true) {
                        if (size2 < 0) {
                            break;
                        }
                        if (((Integer) this.emojiAdapter.packStartPosition.get(size2)).intValue() <= i) {
                            EmojiPack emojiPack = (EmojiPack) this.emojipacksProcessed.get(size2);
                            while (i3 < emojipacks.size()) {
                                long j = emojipacks.get(i3).set.id;
                                long j2 = emojiPack.set.id;
                                if (j == j2 && (!emojiPack.featured || (!emojiPack.installed && !this.installedEmojiSets.contains(Long.valueOf(j2))))) {
                                    i3 += EmojiData.dataColored.length + 1;
                                    break;
                                }
                                i3++;
                            }
                        } else {
                            size2--;
                        }
                    }
                }
                i3 = i2;
            }
            if (i3 >= 0) {
                this.emojiTabs.select(i3);
            }
        }
    }

    private void updateGifTabs() {
        int i;
        ScrollSlidingTabStrip scrollSlidingTabStrip;
        int i2;
        int currentPosition = this.gifTabs.getCurrentPosition();
        int i3 = this.gifRecentTabNum;
        boolean z = currentPosition == i3;
        boolean z2 = i3 >= 0;
        boolean z3 = !this.recentGifs.isEmpty();
        this.gifTabs.beginUpdate(false);
        this.gifRecentTabNum = -2;
        this.gifTrendingTabNum = -2;
        this.gifFirstEmojiTabNum = -2;
        if (z3) {
            this.gifRecentTabNum = 0;
            this.gifTabs.addIconTab(0, this.gifIcons[0]).setContentDescription(LocaleController.getString(R.string.RecentStickers));
            i = 1;
        } else {
            i = 0;
        }
        this.gifTrendingTabNum = i;
        this.gifTabs.addIconTab(1, this.gifIcons[1]).setContentDescription(LocaleController.getString(R.string.FeaturedGifs));
        this.gifFirstEmojiTabNum = i + 1;
        AndroidUtilities.dp(13.0f);
        AndroidUtilities.dp(11.0f);
        ArrayList<String> arrayList = MessagesController.getInstance(this.currentAccount).gifSearchEmojies;
        int size = arrayList.size();
        for (int i4 = 0; i4 < size; i4++) {
            String str = arrayList.get(i4);
            Emoji.EmojiDrawable emojiDrawable = Emoji.getEmojiDrawable(str);
            if (emojiDrawable != null) {
                this.gifTabs.addEmojiTab(i4 + 3, emojiDrawable, MediaDataController.getInstance(this.currentAccount).getEmojiAnimatedSticker(str)).setContentDescription(str);
            }
        }
        this.gifTabs.commitUpdate();
        this.gifTabs.updateTabStyles();
        if (z && !z3) {
            this.gifTabs.selectTab(this.gifTrendingTabNum);
            SearchField searchField = this.gifSearchField;
            if (searchField == null || searchField.categoriesListView == null) {
                return;
            }
            this.gifSearchField.categoriesListView.selectCategory(this.gifSearchField.trending);
            return;
        }
        if (ViewCompat.isLaidOut(this.gifTabs)) {
            if (z3 && !z2) {
                scrollSlidingTabStrip = this.gifTabs;
                i2 = currentPosition + 1;
            } else {
                if (z3 || !z2) {
                    return;
                }
                scrollSlidingTabStrip = this.gifTabs;
                i2 = currentPosition - 1;
            }
            scrollSlidingTabStrip.onPageScrolled(i2, 0);
        }
    }

    public void updateRecentGifs() {
        GifAdapter gifAdapter;
        int size = this.recentGifs.size();
        long calcDocumentsHash = MediaDataController.calcDocumentsHash(this.recentGifs, Integer.MAX_VALUE);
        ArrayList<TLRPC.Document> recentGifs = MediaDataController.getInstance(this.currentAccount).getRecentGifs();
        this.recentGifs = recentGifs;
        long calcDocumentsHash2 = MediaDataController.calcDocumentsHash(recentGifs, Integer.MAX_VALUE);
        if ((this.gifTabs != null && size == 0 && !this.recentGifs.isEmpty()) || (size != 0 && this.recentGifs.isEmpty())) {
            updateGifTabs();
        }
        if ((size == this.recentGifs.size() && calcDocumentsHash == calcDocumentsHash2) || (gifAdapter = this.gifAdapter) == null) {
            return;
        }
        gifAdapter.notifyDataSetChanged();
    }

    public void updateStickerTabs(boolean r15) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.updateStickerTabs(boolean):void");
    }

    public void updateStickerTabsPosition() {
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip != null && this.stickersTabContainer == null && this.delegate != null) {
            scrollSlidingTabStrip.setTranslationY((-AndroidUtilities.dp(50.0f)) * this.delegate.getProgressToSearchOpened());
        }
        if (this.stickersTabContainer == null) {
            return;
        }
        boolean z = getVisibility() == 0 && this.stickersContainerAttached && this.delegate.getProgressToSearchOpened() != 1.0f;
        this.stickersTabContainer.setVisibility(z ? 0 : 8);
        if (z) {
            this.rect.setEmpty();
            this.pager.getChildVisibleRect(this.stickersContainer, this.rect, null);
            float dp = AndroidUtilities.dp(50.0f) * this.delegate.getProgressToSearchOpened();
            int i = this.rect.left;
            if (i != 0 || dp != 0.0f) {
                this.expandStickersByDragg = false;
            }
            this.stickersTabContainer.setTranslationX(i);
            float top = (((getTop() + getTranslationY()) - this.stickersTabContainer.getTop()) - this.stickersTab.getExpandedOffset()) - dp;
            if (this.stickersTabContainer.getTranslationY() != top) {
                this.stickersTabContainer.setTranslationY(top);
                this.stickersTabContainer.invalidate();
            }
        }
        if (this.expandStickersByDragg && z && this.showing) {
            this.stickersTab.expandStickers(this.lastStickersX, true);
        } else {
            this.expandStickersByDragg = false;
            this.stickersTab.expandStickers(this.lastStickersX, false);
        }
    }

    private void updateVisibleTrendingSets() {
        boolean z;
        RecyclerListView recyclerListView = this.stickersGridView;
        if (recyclerListView == null) {
            return;
        }
        try {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.stickersGridView.getChildAt(i);
                if ((childAt instanceof FeaturedStickerSetInfoCell) && ((RecyclerListView.Holder) this.stickersGridView.getChildViewHolder(childAt)) != null) {
                    FeaturedStickerSetInfoCell featuredStickerSetInfoCell = (FeaturedStickerSetInfoCell) childAt;
                    ArrayList<Long> unreadStickerSets = MediaDataController.getInstance(this.currentAccount).getUnreadStickerSets();
                    TLRPC.StickerSetCovered stickerSet = featuredStickerSetInfoCell.getStickerSet();
                    boolean z2 = unreadStickerSets != null && unreadStickerSets.contains(Long.valueOf(stickerSet.set.id));
                    int i2 = 0;
                    while (true) {
                        TLRPC.StickerSetCovered[] stickerSetCoveredArr = this.primaryInstallingStickerSets;
                        if (i2 >= stickerSetCoveredArr.length) {
                            z = false;
                            break;
                        }
                        TLRPC.StickerSetCovered stickerSetCovered = stickerSetCoveredArr[i2];
                        if (stickerSetCovered != null && stickerSetCovered.set.id == stickerSet.set.id) {
                            z = true;
                            break;
                        }
                        i2++;
                    }
                    featuredStickerSetInfoCell.setStickerSet(stickerSet, z2, true, 0, 0, z);
                    if (z2) {
                        MediaDataController.getInstance(this.currentAccount).markFeaturedStickersByIdAsRead(false, stickerSet.set.id);
                    }
                    boolean z3 = this.installingStickerSets.indexOfKey(stickerSet.set.id) >= 0;
                    boolean z4 = this.removingStickerSets.indexOfKey(stickerSet.set.id) >= 0;
                    if (z3 || z4) {
                        if (z3 && featuredStickerSetInfoCell.isInstalled()) {
                            this.installingStickerSets.remove(stickerSet.set.id);
                            z3 = false;
                        } else if (z4 && !featuredStickerSetInfoCell.isInstalled()) {
                            this.removingStickerSets.remove(stickerSet.set.id);
                        }
                    }
                    featuredStickerSetInfoCell.setAddDrawProgress(!z && z3, true);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void addEmojiToRecent(String str) {
        if (str == null) {
            return;
        }
        if (!str.startsWith("animated_") && !Emoji.isValidEmoji(str)) {
            return;
        }
        Emoji.addRecentEmoji(str);
        if (getVisibility() != 0 || this.pager.getCurrentItem() != 0) {
            Emoji.sortEmoji();
            this.emojiAdapter.notifyDataSetChanged();
        }
        Emoji.saveRecentEmoji();
        if (this.allowAnimatedEmoji) {
            return;
        }
        ArrayList arrayList = this.lastRecentArray;
        if (arrayList == null) {
            this.lastRecentArray = new ArrayList();
        } else {
            arrayList.clear();
        }
        int i = 0;
        while (true) {
            ArrayList<String> arrayList2 = Emoji.recentEmoji;
            if (i >= arrayList2.size()) {
                this.lastRecentCount = this.lastRecentArray.size();
                return;
            } else {
                if (!arrayList2.get(i).startsWith("animated_")) {
                    this.lastRecentArray.add(arrayList2.get(i));
                }
                i++;
            }
        }
    }

    public void addRecentGif(TLRPC.Document document) {
        if (document == null) {
            return;
        }
        boolean isEmpty = this.recentGifs.isEmpty();
        updateRecentGifs();
        if (isEmpty) {
            updateStickerTabs(false);
        }
    }

    public void addRecentSticker(TLRPC.Document document) {
        if (document == null) {
            return;
        }
        MediaDataController.getInstance(this.currentAccount).addRecentSticker(0, null, document, (int) (System.currentTimeMillis() / 1000), false);
        boolean isEmpty = this.recentStickers.isEmpty();
        this.recentStickers = MediaDataController.getInstance(this.currentAccount).getRecentStickers(0, true);
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        if (stickersGridAdapter != null) {
            stickersGridAdapter.notifyDataSetChanged();
        }
        if (isEmpty) {
            updateStickerTabs(false);
        }
    }

    public void allowEmojisForNonPremium(boolean z) {
        this.allowEmojisForNonPremium = z;
    }

    public boolean areThereAnyStickers() {
        StickersGridAdapter stickersGridAdapter = this.stickersGridAdapter;
        return stickersGridAdapter != null && stickersGridAdapter.getItemCount() > 0;
    }

    public void clearRecentEmoji() {
        Emoji.clearRecentEmoji();
        this.emojiAdapter.notifyDataSetChanged();
    }

    public void closeSearch(boolean z) {
        closeSearch(z, -1L);
    }

    public void closeSearch(boolean z, long j) {
        SearchField searchField;
        RecyclerListView recyclerListView;
        GridLayoutManager gridLayoutManager;
        View view;
        int dp;
        TLRPC.TL_messages_stickerSet stickerSetById;
        int positionForPack;
        AnimatorSet animatorSet = this.searchAnimation;
        StickerCategoriesListView.EmojiCategory emojiCategory = null;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.searchAnimation = null;
        }
        int currentItem = this.pager.getCurrentItem();
        if (currentItem == 2 && j != -1 && (stickerSetById = MediaDataController.getInstance(this.currentAccount).getStickerSetById(j)) != null && (positionForPack = this.stickersGridAdapter.getPositionForPack(stickerSetById)) >= 0 && positionForPack < this.stickersGridAdapter.getItemCount()) {
            scrollStickersToPosition(positionForPack, AndroidUtilities.dp(48.0f));
        }
        GifAdapter gifAdapter = this.gifSearchAdapter;
        if (gifAdapter != null) {
            gifAdapter.showTrendingWhenSearchEmpty = false;
        }
        int i = 0;
        while (i < 3) {
            if (i == 0) {
                searchField = this.emojiSearchField;
                recyclerListView = this.emojiGridView;
                gridLayoutManager = this.emojiLayoutManager;
                view = this.emojiTabs;
            } else if (i == 1) {
                searchField = this.gifSearchField;
                recyclerListView = this.gifGridView;
                gridLayoutManager = this.gifLayoutManager;
                view = this.gifTabs;
            } else {
                searchField = this.stickersSearchField;
                recyclerListView = this.stickersGridView;
                gridLayoutManager = this.stickersLayoutManager;
                view = this.stickersTab;
            }
            if (searchField != null) {
                searchField.searchEditText.setText("");
                if (searchField.categoriesListView != null) {
                    searchField.categoriesListView.selectCategory(emojiCategory);
                    searchField.categoriesListView.scrollToStart();
                }
                if (i == currentItem && z) {
                    AnimatorSet animatorSet2 = new AnimatorSet();
                    this.searchAnimation = animatorSet2;
                    if (view == null || i == 1) {
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(recyclerListView, (Property<RecyclerListView, Float>) View.TRANSLATION_Y, AndroidUtilities.dp(36.0f) - this.searchFieldHeight));
                    } else {
                        Property property = View.TRANSLATION_Y;
                        animatorSet2.playTogether(ObjectAnimator.ofFloat(view, (Property<View, Float>) property, 0.0f), ObjectAnimator.ofFloat(recyclerListView, (Property<RecyclerListView, Float>) property, AndroidUtilities.dp(36.0f)), ObjectAnimator.ofFloat(searchField, (Property<SearchField, Float>) property, AndroidUtilities.dp(36.0f)));
                    }
                    this.searchAnimation.setDuration(200L);
                    this.searchAnimation.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    this.searchAnimation.addListener(new AnimatorListenerAdapter() {
                        final RecyclerListView val$gridView;
                        final GridLayoutManager val$layoutManager;

                        AnonymousClass39(GridLayoutManager gridLayoutManager2, RecyclerListView recyclerListView2) {
                            r2 = gridLayoutManager2;
                            r3 = recyclerListView2;
                        }

                        @Override
                        public void onAnimationCancel(Animator animator) {
                            if (animator.equals(EmojiView.this.searchAnimation)) {
                                EmojiView.this.searchAnimation = null;
                            }
                        }

                        @Override
                        public void onAnimationEnd(android.animation.Animator r7) {
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.EmojiView.AnonymousClass39.onAnimationEnd(android.animation.Animator):void");
                        }
                    });
                    this.searchAnimation.start();
                } else {
                    if (searchField != this.gifSearchField) {
                        searchField.setTranslationY(AndroidUtilities.dp(36.0f) - this.searchFieldHeight);
                    }
                    if (view != null && i != 2) {
                        view.setTranslationY(0.0f);
                    }
                    if (recyclerListView2 == this.stickersGridView) {
                        dp = AndroidUtilities.dp(36.0f);
                    } else if (recyclerListView2 == this.gifGridView) {
                        dp = AndroidUtilities.dp(40.0f);
                    } else {
                        if (recyclerListView2 == this.emojiGridView) {
                            recyclerListView2.setPadding(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(36.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(44.0f));
                        }
                        gridLayoutManager2.scrollToPositionWithOffset(0, 0);
                    }
                    recyclerListView2.setPadding(0, dp, 0, AndroidUtilities.dp(44.0f));
                    gridLayoutManager2.scrollToPositionWithOffset(0, 0);
                }
            }
            i++;
            emojiCategory = null;
        }
        if (z) {
            return;
        }
        this.delegate.onSearchOpenClose(0);
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        Utilities.Callback callback;
        TLRPC.StickerSet stickerSet;
        if (i != NotificationCenter.stickersDidLoad) {
            if (i == NotificationCenter.groupPackUpdated) {
                long longValue = ((Long) objArr[0]).longValue();
                boolean booleanValue = ((Boolean) objArr[1]).booleanValue();
                TLRPC.ChatFull chatFull = this.info;
                if (chatFull != null && chatFull.id == longValue && booleanValue) {
                    this.emojiAdapter.notifyDataSetChanged(true);
                    return;
                }
                return;
            }
            if (i == NotificationCenter.recentDocumentsDidLoad) {
                boolean booleanValue2 = ((Boolean) objArr[0]).booleanValue();
                int intValue = ((Integer) objArr[1]).intValue();
                if (booleanValue2 || intValue == 0 || intValue == 2) {
                    checkDocuments(booleanValue2);
                    return;
                }
                return;
            }
            if (i == NotificationCenter.featuredStickersDidLoad) {
                updateVisibleTrendingSets();
                PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
                if (pagerSlidingTabStrip != null) {
                    int childCount = pagerSlidingTabStrip.getChildCount();
                    for (int i3 = 0; i3 < childCount; i3++) {
                        this.typeTabs.getChildAt(i3).invalidate();
                    }
                }
            } else {
                if (i == NotificationCenter.featuredEmojiDidLoad) {
                    EmojiGridAdapter emojiGridAdapter = this.emojiAdapter;
                    if (emojiGridAdapter != null) {
                        emojiGridAdapter.notifyDataSetChanged();
                        return;
                    }
                    return;
                }
                if (i == NotificationCenter.groupStickersDidLoad) {
                    TLRPC.ChatFull chatFull2 = this.info;
                    if (chatFull2 != null && (stickerSet = chatFull2.stickerset) != null && stickerSet.id == ((Long) objArr[0]).longValue()) {
                        updateStickerTabs(false);
                    }
                    if (this.toInstall.containsKey((Long) objArr[0]) && objArr.length >= 2) {
                        Long l = (Long) objArr[0];
                        l.longValue();
                        TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) objArr[1];
                        if (((Utilities.Callback) this.toInstall.get(l)) != null && tL_messages_stickerSet != null && (callback = (Utilities.Callback) this.toInstall.remove(l)) != null) {
                            callback.run(tL_messages_stickerSet);
                        }
                    }
                } else {
                    if (i == NotificationCenter.emojiLoaded) {
                        RecyclerListView recyclerListView = this.stickersGridView;
                        if (recyclerListView != null) {
                            int childCount2 = recyclerListView.getChildCount();
                            for (int i4 = 0; i4 < childCount2; i4++) {
                                View childAt = this.stickersGridView.getChildAt(i4);
                                if ((childAt instanceof StickerSetNameCell) || (childAt instanceof StickerEmojiCell)) {
                                    childAt.invalidate();
                                }
                            }
                        }
                        EmojiGridView emojiGridView = this.emojiGridView;
                        if (emojiGridView != null) {
                            emojiGridView.invalidate();
                            int childCount3 = this.emojiGridView.getChildCount();
                            for (int i5 = 0; i5 < childCount3; i5++) {
                                View childAt2 = this.emojiGridView.getChildAt(i5);
                                if (childAt2 instanceof ImageViewEmoji) {
                                    childAt2.invalidate();
                                }
                            }
                        }
                        EmojiColorPickerWindow emojiColorPickerWindow = this.colorPickerView;
                        if (emojiColorPickerWindow != null) {
                            emojiColorPickerWindow.pickerView.invalidate();
                        }
                        ScrollSlidingTabStrip scrollSlidingTabStrip = this.gifTabs;
                        if (scrollSlidingTabStrip != null) {
                            scrollSlidingTabStrip.invalidateTabs();
                            return;
                        }
                        return;
                    }
                    if (i == NotificationCenter.newEmojiSuggestionsAvailable) {
                        if (this.emojiGridView == null || !this.needEmojiSearch) {
                            return;
                        }
                        if ((this.emojiSearchField.searchStateDrawable.getIconState() == 2 || this.emojiGridView.getAdapter() == this.emojiSearchAdapter) && !TextUtils.isEmpty(this.emojiSearchAdapter.lastSearchEmojiString)) {
                            EmojiSearchAdapter emojiSearchAdapter = this.emojiSearchAdapter;
                            emojiSearchAdapter.search(emojiSearchAdapter.lastSearchEmojiString);
                            return;
                        }
                        return;
                    }
                    if (i != NotificationCenter.currentUserPremiumStatusChanged) {
                        return;
                    }
                    EmojiGridAdapter emojiGridAdapter2 = this.emojiAdapter;
                    if (emojiGridAdapter2 != null) {
                        emojiGridAdapter2.notifyDataSetChanged();
                    }
                    updateEmojiHeaders();
                }
            }
            updateStickerTabs(false);
            return;
        }
        if (((Integer) objArr[0]).intValue() == 0) {
            if (this.stickersGridAdapter != null) {
                updateStickerTabs(((Boolean) objArr[1]).booleanValue());
                updateVisibleTrendingSets();
                reloadStickersAdapter();
                checkPanels();
                return;
            }
            return;
        }
        if (((Integer) objArr[0]).intValue() != 5) {
            return;
        }
        if (!((Boolean) objArr[1]).booleanValue()) {
            this.emojiAdapter.notifyDataSetChanged(false);
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(this.updateStickersLoadedDelayed);
        AndroidUtilities.runOnUIThread(this.updateStickersLoadedDelayed, 100L);
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        updateBottomTabContainerPosition();
        super.dispatchDraw(canvas);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        if (view != this.pager || this.bottomTabContainer.getVisibility() == 8) {
            return super.drawChild(canvas, view, j);
        }
        canvas.save();
        if (this.needEmojiSearch) {
            canvas.clipRect(0.0f, 0.0f, getMeasuredWidth(), this.bottomTabContainer.getY() - 1.0f);
        }
        if (!this.shouldDrawBackground && this.shouldLightenBackground) {
            canvas.drawColor(ColorUtils.setAlphaComponent(-1, 25));
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        canvas.restore();
        return drawChild;
    }

    public void freeze(boolean z) {
        StickersGridAdapter stickersGridAdapter;
        boolean z2 = this.frozen;
        this.frozen = z;
        if (!z2 || z) {
            return;
        }
        int i = this.currentPage;
        if (i == 0) {
            EmojiGridAdapter emojiGridAdapter = this.emojiAdapter;
            if (emojiGridAdapter != null) {
                emojiGridAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        if (i == 1) {
            GifAdapter gifAdapter = this.gifAdapter;
            if (gifAdapter != null) {
                gifAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        if (i != 2 || (stickersGridAdapter = this.stickersGridAdapter) == null) {
            return;
        }
        stickersGridAdapter.notifyDataSetChanged();
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public ArrayList<EmojiPack> getEmojipacks() {
        ArrayList<EmojiPack> arrayList = new ArrayList<>();
        for (int i = 0; i < this.emojipacksProcessed.size(); i++) {
            EmojiPack emojiPack = (EmojiPack) this.emojipacksProcessed.get(i);
            if ((!emojiPack.featured && (emojiPack.installed || this.installedEmojiSets.contains(Long.valueOf(emojiPack.set.id)))) || (emojiPack.featured && !emojiPack.installed && !this.installedEmojiSets.contains(Long.valueOf(emojiPack.set.id)))) {
                arrayList.add(emojiPack);
            }
        }
        return arrayList;
    }

    public ArrayList<String> getRecentEmoji() {
        if (this.allowAnimatedEmoji) {
            return Emoji.recentEmoji;
        }
        if (this.lastRecentArray == null) {
            this.lastRecentArray = new ArrayList();
        }
        if (Emoji.recentEmoji.size() != this.lastRecentCount) {
            this.lastRecentArray.clear();
            int i = 0;
            while (true) {
                ArrayList<String> arrayList = Emoji.recentEmoji;
                if (i >= arrayList.size()) {
                    break;
                }
                if (!arrayList.get(i).startsWith("animated_")) {
                    this.lastRecentArray.add(arrayList.get(i));
                }
                i++;
            }
            this.lastRecentCount = this.lastRecentArray.size();
        }
        return this.lastRecentArray;
    }

    public float getStickersExpandOffset() {
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip == null) {
            return 0.0f;
        }
        return scrollSlidingTabStrip.getExpandedOffset();
    }

    public void hideSearchKeyboard() {
        SearchField searchField = this.stickersSearchField;
        if (searchField != null) {
            searchField.hideKeyboard();
        }
        SearchField searchField2 = this.gifSearchField;
        if (searchField2 != null) {
            searchField2.hideKeyboard();
        }
        SearchField searchField3 = this.emojiSearchField;
        if (searchField3 != null) {
            searchField3.hideKeyboard();
        }
    }

    public void invalidateViews() {
        this.emojiGridView.invalidateViews();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupPackUpdated);
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.groupStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    EmojiView.this.lambda$onAttachedToWindow$15();
                }
            });
        }
    }

    public void onDestroy() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.stickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupPackUpdated);
        if (this.stickersGridAdapter != null) {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recentDocumentsDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EmojiColorPickerWindow emojiColorPickerWindow = this.colorPickerView;
        if (emojiColorPickerWindow != null && emojiColorPickerWindow.isShowing()) {
            this.colorPickerView.dismiss();
        }
        ContentPreviewViewer.getInstance().clearDelegate(this.contentPreviewViewerDelegate);
    }

    @Override
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int i5 = i3 - i;
        if (this.lastNotifyWidth != i5) {
            this.lastNotifyWidth = i5;
            reloadStickersAdapter();
        }
        super.onLayout(z, i, i2, i3, i4);
        updateBottomTabContainerPosition();
        updateStickerTabsPosition();
    }

    @Override
    public void onMeasure(int i, int i2) {
        this.isLayout = true;
        if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            if (this.currentBackgroundType != 1) {
                if (Build.VERSION.SDK_INT >= 21) {
                    setOutlineProvider(EmojiView$$ExternalSyntheticApiModelOutline0.m(this.outlineProvider));
                    setClipToOutline(true);
                    setElevation(AndroidUtilities.dp(2.0f));
                }
                setBackgroundResource(R.drawable.smiles_popup);
                Drawable background = getBackground();
                int i3 = Theme.key_chat_emojiPanelBackground;
                background.setColorFilter(new PorterDuffColorFilter(getThemedColor(i3), PorterDuff.Mode.MULTIPLY));
                if (this.needEmojiSearch && this.shouldDrawBackground) {
                    this.bottomTabContainerBackground.setBackgroundColor(getThemedColor(i3));
                }
                this.currentBackgroundType = 1;
            }
        } else if (this.currentBackgroundType != 0) {
            if (Build.VERSION.SDK_INT >= 21) {
                setOutlineProvider(null);
                setClipToOutline(false);
                setElevation(0.0f);
            }
            if (this.shouldDrawBackground) {
                int i4 = Theme.key_chat_emojiPanelBackground;
                setBackgroundColor(getThemedColor(i4));
                if (this.needEmojiSearch) {
                    this.bottomTabContainerBackground.setBackgroundColor(getThemedColor(i4));
                }
            }
            this.currentBackgroundType = 0;
        }
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824));
        this.isLayout = false;
        setTranslationY(getTranslationY());
    }

    public void onMessageSend() {
        ChooseStickerActionTracker chooseStickerActionTracker = this.chooseStickerActionTracker;
        if (chooseStickerActionTracker != null) {
            chooseStickerActionTracker.reset();
        }
    }

    public void onOpen(boolean z, boolean z2) {
        if (this.currentPage != 0 && this.stickersBanned) {
            this.currentPage = 0;
        }
        if (this.currentPage == 0 && this.emojiBanned) {
            this.currentPage = 1;
        }
        if (this.currentPage == 0 || z || this.currentTabs.size() == 1) {
            showBackspaceButton(true, false);
            showStickerSettingsButton(false, false);
            if (this.pager.getCurrentItem() != 0) {
                this.pager.setCurrentItem(0, !z);
            }
            if (z2) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        EmojiView.this.lambda$onOpen$14();
                    }
                }, 350L);
            }
        } else {
            int i = this.currentPage;
            if (i == 1) {
                showBackspaceButton(false, false);
                showStickerSettingsButton(this.shouldDrawBackground, false);
                if (this.pager.getCurrentItem() != 2) {
                    this.pager.setCurrentItem(2, false);
                }
                ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
                if (scrollSlidingTabStrip != null) {
                    this.firstTabUpdate = true;
                    int i2 = this.favTabNum;
                    if (i2 < 0 && (i2 = this.recentTabNum) < 0) {
                        i2 = this.stickersTabOffset;
                    }
                    scrollSlidingTabStrip.selectTab(i2);
                    this.firstTabUpdate = false;
                    this.stickersLayoutManager.scrollToPositionWithOffset(0, 0);
                }
            } else if (i == 2) {
                showBackspaceButton(false, false);
                showStickerSettingsButton(false, false);
                if (this.pager.getCurrentItem() != 1) {
                    this.pager.setCurrentItem(1, false);
                }
                ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.gifTabs;
                if (scrollSlidingTabStrip2 != null) {
                    scrollSlidingTabStrip2.selectTab(0);
                }
                SearchField searchField = this.gifSearchField;
                if (searchField != null && searchField.categoriesListView != null) {
                    this.gifSearchField.categoriesListView.selectCategory(this.gifSearchField.recent);
                }
            }
        }
        showBottomTab(true, true);
    }

    public void openEmojiPackAlert(TLRPC.StickerSet stickerSet) {
        if (this.emojiPackAlertOpened) {
            return;
        }
        this.emojiPackAlertOpened = true;
        ArrayList arrayList = new ArrayList(1);
        TLRPC.TL_inputStickerSetID tL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
        tL_inputStickerSetID.id = stickerSet.id;
        tL_inputStickerSetID.access_hash = stickerSet.access_hash;
        arrayList.add(tL_inputStickerSetID);
        new EmojiPacksAlert(this.fragment, getContext(), this.resourcesProvider, arrayList) {
            final TLRPC.StickerSet val$set;

            AnonymousClass34(BaseFragment baseFragment, Context context, Theme.ResourcesProvider resourcesProvider, ArrayList arrayList2, TLRPC.StickerSet stickerSet2) {
                super(baseFragment, context, resourcesProvider, arrayList2);
                r6 = stickerSet2;
            }

            @Override
            public void dismiss() {
                EmojiView.this.emojiPackAlertOpened = false;
                super.dismiss();
            }

            @Override
            protected void onButtonClicked(boolean z) {
                if (!z) {
                    EmojiView.this.installedEmojiSets.remove(Long.valueOf(r6.id));
                } else if (!EmojiView.this.installedEmojiSets.contains(Long.valueOf(r6.id))) {
                    EmojiView.this.installedEmojiSets.add(Long.valueOf(r6.id));
                }
                EmojiView.this.updateEmojiHeaders();
            }
        }.show();
    }

    @Override
    public void requestLayout() {
        if (this.isLayout) {
            return;
        }
        super.requestLayout();
    }

    public void scrollEmojiToTop() {
        this.emojiGridView.stopScroll();
        this.emojiTabs.scrollTo(0, 0);
        resetTabsY(1);
        this.emojiLayoutManager.scrollToPositionWithOffset(0, 0);
    }

    public void scrollEmojisToAnimated() {
        if (this.emojiSmoothScrolling) {
            return;
        }
        try {
            int i = this.emojiAdapter.sectionToPosition.get(EmojiData.dataColored.length);
            if (i > 0) {
                this.emojiGridView.stopScroll();
                updateEmojiTabsPosition(i);
                scrollEmojisToPosition(i, AndroidUtilities.dp(-9.0f));
                checkEmojiTabY(null, 0);
            }
        } catch (Exception unused) {
        }
    }

    public void searchProgressChanged() {
        updateStickerTabsPosition();
    }

    public void setAllow(boolean z, boolean z2, boolean z3) {
        this.currentTabs.clear();
        for (int i = 0; i < this.allTabs.size(); i++) {
            if (((Tab) this.allTabs.get(i)).type == 0) {
                this.currentTabs.add((Tab) this.allTabs.get(i));
            }
            if (((Tab) this.allTabs.get(i)).type == 1 && z2) {
                this.currentTabs.add((Tab) this.allTabs.get(i));
            }
            if (((Tab) this.allTabs.get(i)).type == 2 && z) {
                this.currentTabs.add((Tab) this.allTabs.get(i));
            }
        }
        PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
        if (pagerSlidingTabStrip != null) {
            AndroidUtilities.updateViewVisibilityAnimated(pagerSlidingTabStrip, this.currentTabs.size() > 1, 1.0f, z3);
        }
        ViewPager viewPager = this.pager;
        if (viewPager != null) {
            viewPager.setAdapter(null);
            this.pager.setAdapter(this.emojiPagerAdapter);
            PagerSlidingTabStrip pagerSlidingTabStrip2 = this.typeTabs;
            if (pagerSlidingTabStrip2 != null) {
                pagerSlidingTabStrip2.setViewPager(this.pager);
            }
        }
    }

    public void setChatInfo(TLRPC.ChatFull chatFull) {
        this.info = chatFull;
        updateStickerTabs(false);
    }

    public void setDelegate(EmojiViewDelegate emojiViewDelegate) {
        this.delegate = emojiViewDelegate;
    }

    public void setDragListener(DragListener dragListener) {
        this.dragListener = dragListener;
    }

    @Override
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        SearchField searchField = this.stickersSearchField;
        if (searchField != null) {
            searchField.searchEditText.setEnabled(z);
        }
        SearchField searchField2 = this.gifSearchField;
        if (searchField2 != null) {
            searchField2.searchEditText.setEnabled(z);
        }
        SearchField searchField3 = this.emojiSearchField;
        if (searchField3 != null) {
            searchField3.searchEditText.setEnabled(z);
        }
    }

    public void setForseMultiwindowLayout(boolean z) {
        this.forseMultiwindowLayout = z;
    }

    public void setShouldDrawBackground(boolean z) {
        if (this.shouldDrawBackground != z) {
            this.shouldDrawBackground = z;
            updateColors();
        }
    }

    public void setShowing(boolean z) {
        this.showing = z;
        updateStickerTabsPosition();
    }

    public void setStickersBanned(boolean z, boolean z2, long j) {
        PagerSlidingTabStrip pagerSlidingTabStrip = this.typeTabs;
        if (pagerSlidingTabStrip == null) {
            return;
        }
        this.emojiBanned = z;
        this.stickersBanned = z2;
        if (z2 || z) {
            this.currentChatId = j;
        } else {
            this.currentChatId = 0L;
        }
        View tab = pagerSlidingTabStrip.getTab(z2 ? 2 : 0);
        if (tab != null) {
            tab.setAlpha(this.currentChatId != 0 ? 0.15f : 1.0f);
            long j2 = this.currentChatId;
            if (z2) {
                if (j2 == 0 || this.pager.getCurrentItem() == 0) {
                    return;
                }
                showBackspaceButton(true, true);
                showStickerSettingsButton(false, true);
                this.pager.setCurrentItem(0, false);
                return;
            }
            if (j2 == 0 || this.pager.getCurrentItem() == 1) {
                return;
            }
            showBackspaceButton(false, true);
            showStickerSettingsButton(false, true);
            this.pager.setCurrentItem(1, false);
        }
    }

    @Override
    public void setTranslationY(float f) {
        super.setTranslationY(f);
        updateStickerTabsPosition();
        updateBottomTabContainerPosition();
    }

    @Override
    public void setVisibility(int i) {
        boolean z = getVisibility() != i;
        super.setVisibility(i);
        if (z) {
            if (i != 8) {
                Emoji.sortEmoji();
                this.emojiAdapter.notifyDataSetChanged();
                NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.stickersDidLoad);
                if (this.stickersGridAdapter != null) {
                    NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.recentDocumentsDidLoad);
                    updateStickerTabs(false);
                    reloadStickersAdapter();
                }
                checkDocuments(true);
                checkDocuments(false);
                MediaDataController.getInstance(this.currentAccount).loadRecents(0, true, true, false);
                MediaDataController.getInstance(this.currentAccount).loadRecents(0, false, true, false);
                MediaDataController.getInstance(this.currentAccount).loadRecents(2, false, true, false);
            }
            ChooseStickerActionTracker chooseStickerActionTracker = this.chooseStickerActionTracker;
            if (chooseStickerActionTracker != null) {
                chooseStickerActionTracker.checkVisibility();
            }
        }
    }

    public void showSearchField(boolean z) {
        for (int i = 0; i < 3; i++) {
            GridLayoutManager layoutManagerForType = getLayoutManagerForType(i);
            int findFirstVisibleItemPosition = layoutManagerForType.findFirstVisibleItemPosition();
            if (z) {
                if (findFirstVisibleItemPosition == 1 || findFirstVisibleItemPosition == 2) {
                    layoutManagerForType.scrollToPosition(0);
                    resetTabsY(i);
                }
            } else if (findFirstVisibleItemPosition == 0) {
                layoutManagerForType.scrollToPositionWithOffset(0, 0);
            }
        }
    }

    public void showStickerBanHint(boolean z, final boolean z2, final boolean z3) {
        TextView textView;
        int i;
        TLRPC.TL_chatBannedRights tL_chatBannedRights;
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(this.currentChatId));
        if (chat == null) {
            return;
        }
        if (z) {
            if (ChatObject.hasAdminRights(chat) || (tL_chatBannedRights = chat.default_banned_rights) == null || !(tL_chatBannedRights.send_stickers || (z2 && tL_chatBannedRights.send_plain))) {
                TLRPC.TL_chatBannedRights tL_chatBannedRights2 = chat.banned_rights;
                if (tL_chatBannedRights2 == null) {
                    return;
                }
                if (AndroidUtilities.isBannedForever(tL_chatBannedRights2)) {
                    textView = this.mediaBanTooltip;
                    i = z2 ? R.string.AttachPlainRestrictedForever : z3 ? R.string.AttachGifRestrictedForever : R.string.AttachStickersRestrictedForever;
                } else {
                    if (z2) {
                        this.mediaBanTooltip.setText(LocaleController.formatString("AttachPlainRestricted", R.string.AttachPlainRestricted, LocaleController.formatDateForBan(chat.banned_rights.until_date)));
                    }
                    this.mediaBanTooltip.setText(z3 ? LocaleController.formatString("AttachGifRestricted", R.string.AttachGifRestricted, LocaleController.formatDateForBan(chat.banned_rights.until_date)) : LocaleController.formatString("AttachStickersRestricted", R.string.AttachStickersRestricted, LocaleController.formatDateForBan(chat.banned_rights.until_date)));
                    this.mediaBanTooltip.setVisibility(0);
                }
            } else {
                BaseFragment baseFragment = this.fragment;
                if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).checkCanRemoveRestrictionsByBoosts()) {
                    return;
                }
                textView = this.mediaBanTooltip;
                i = z2 ? R.string.GlobalAttachEmojiRestricted : z3 ? R.string.GlobalAttachGifRestricted : R.string.GlobalAttachStickersRestricted;
            }
            textView.setText(LocaleController.getString(i));
            this.mediaBanTooltip.setVisibility(0);
        }
        AnimatorSet animatorSet = this.showStickersBanAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.showStickersBanAnimator = null;
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.showStickersBanAnimator = animatorSet2;
        TextView textView2 = this.mediaBanTooltip;
        ObjectAnimator ofFloat = ObjectAnimator.ofFloat(textView2, (Property<TextView, Float>) View.ALPHA, z ? textView2.getAlpha() : 1.0f, z ? 1.0f : 0.0f);
        TextView textView3 = this.mediaBanTooltip;
        animatorSet2.playTogether(ofFloat, ObjectAnimator.ofFloat(textView3, (Property<TextView, Float>) View.TRANSLATION_Y, z ? AndroidUtilities.dp(12.0f) : textView3.getTranslationY(), z ? 0.0f : AndroidUtilities.dp(12.0f)));
        Runnable runnable = this.hideStickersBan;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        if (z) {
            Runnable runnable2 = new Runnable() {
                @Override
                public final void run() {
                    EmojiView.this.lambda$showStickerBanHint$16(z2, z3);
                }
            };
            this.hideStickersBan = runnable2;
            AndroidUtilities.runOnUIThread(runnable2, 3500L);
        }
        this.showStickersBanAnimator.setDuration(320L);
        this.showStickersBanAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.showStickersBanAnimator.start();
    }

    public void switchToGifRecent() {
        showBackspaceButton(false, false);
        showStickerSettingsButton(false, false);
        this.pager.setCurrentItem(1, false);
    }

    public void updateColors() {
        if (!this.shouldDrawBackground) {
            setBackground(null);
            this.bottomTabContainerBackground.setBackground(null);
        } else if (AndroidUtilities.isInMultiwindow || this.forseMultiwindowLayout) {
            Drawable background = getBackground();
            if (background != null) {
                background.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelBackground), PorterDuff.Mode.MULTIPLY));
            }
        } else {
            int i = Theme.key_chat_emojiPanelBackground;
            setBackgroundColor(getThemedColor(i));
            if (this.needEmojiSearch) {
                this.bottomTabContainerBackground.setBackgroundColor(getThemedColor(i));
            }
        }
        EmojiTabsStrip emojiTabsStrip = this.emojiTabs;
        if (emojiTabsStrip != null) {
            if (this.shouldDrawBackground) {
                emojiTabsStrip.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
                this.emojiTabsShadow.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelShadowLine));
            } else {
                emojiTabsStrip.setBackground(null);
            }
        }
        EmojiColorPickerWindow emojiColorPickerWindow = this.colorPickerView;
        if (emojiColorPickerWindow != null) {
            emojiColorPickerWindow.updateColors();
        }
        int i2 = 0;
        while (i2 < 3) {
            SearchField searchField = i2 == 0 ? this.stickersSearchField : i2 == 1 ? this.emojiSearchField : this.gifSearchField;
            if (searchField != null) {
                if (this.shouldDrawBackground) {
                    searchField.backgroundView.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
                } else {
                    searchField.backgroundView.setBackground(null);
                }
                searchField.shadowView.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelShadowLine));
                SearchStateDrawable searchStateDrawable = searchField.searchStateDrawable;
                int i3 = Theme.key_chat_emojiSearchIcon;
                searchStateDrawable.setColor(getThemedColor(i3));
                Theme.setDrawableColor(searchField.box.getBackground(), getThemedColor(Theme.key_chat_emojiSearchBackground));
                searchField.box.invalidate();
                searchField.searchEditText.setHintTextColor(getThemedColor(i3));
                searchField.searchEditText.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            }
            i2++;
        }
        Paint paint = this.dotPaint;
        if (paint != null) {
            paint.setColor(getThemedColor(Theme.key_chat_emojiPanelNewTrending));
        }
        EmojiGridView emojiGridView = this.emojiGridView;
        if (emojiGridView != null) {
            emojiGridView.setGlowColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
        }
        RecyclerListView recyclerListView = this.stickersGridView;
        if (recyclerListView != null) {
            recyclerListView.setGlowColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip = this.stickersTab;
        if (scrollSlidingTabStrip != null) {
            scrollSlidingTabStrip.setIndicatorColor(getThemedColor(Theme.key_chat_emojiPanelStickerPackSelectorLine));
            this.stickersTab.setUnderlineColor(getThemedColor(Theme.key_chat_emojiPanelShadowLine));
            if (this.shouldDrawBackground) {
                this.stickersTab.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
            } else {
                this.stickersTab.setBackground(null);
            }
        }
        ScrollSlidingTabStrip scrollSlidingTabStrip2 = this.gifTabs;
        if (scrollSlidingTabStrip2 != null) {
            scrollSlidingTabStrip2.setIndicatorColor(getThemedColor(Theme.key_chat_emojiPanelStickerPackSelectorLine));
            this.gifTabs.setUnderlineColor(getThemedColor(Theme.key_chat_emojiPanelShadowLine));
            if (this.shouldDrawBackground) {
                this.gifTabs.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelBackground));
            } else {
                this.gifTabs.setBackground(null);
            }
        }
        ImageView imageView = this.backspaceButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelBackspace), PorterDuff.Mode.MULTIPLY));
            if (this.emojiSearchField == null) {
                Drawable background2 = this.backspaceButton.getBackground();
                int i4 = Theme.key_chat_emojiPanelBackground;
                Theme.setSelectorDrawableColor(background2, getThemedColor(i4), false);
                Theme.setSelectorDrawableColor(this.backspaceButton.getBackground(), getThemedColor(i4), true);
            }
        }
        ImageView imageView2 = this.stickerSettingsButton;
        if (imageView2 != null) {
            imageView2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelBackspace), PorterDuff.Mode.MULTIPLY));
        }
        ImageView imageView3 = this.searchButton;
        if (imageView3 != null) {
            imageView3.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelBackspace), PorterDuff.Mode.MULTIPLY));
        }
        View view = this.shadowLine;
        if (view != null) {
            view.setBackgroundColor(getThemedColor(Theme.key_chat_emojiPanelShadowLine));
        }
        TextView textView = this.mediaBanTooltip;
        if (textView != null) {
            ((ShapeDrawable) textView.getBackground()).getPaint().setColor(getThemedColor(Theme.key_chat_gifSaveHintBackground));
            this.mediaBanTooltip.setTextColor(getThemedColor(Theme.key_chat_gifSaveHintText));
        }
        GifAdapter gifAdapter = this.gifSearchAdapter;
        if (gifAdapter != null) {
            ImageView imageView4 = gifAdapter.progressEmptyView.imageView;
            int i5 = Theme.key_chat_emojiPanelEmptyText;
            imageView4.setColorFilter(new PorterDuffColorFilter(getThemedColor(i5), PorterDuff.Mode.MULTIPLY));
            this.gifSearchAdapter.progressEmptyView.textView.setTextColor(getThemedColor(i5));
            this.gifSearchAdapter.progressEmptyView.progressView.setProgressColor(getThemedColor(Theme.key_progressCircle));
        }
        this.animatedEmojiTextColorFilter = new PorterDuffColorFilter(getThemedColor(Theme.key_featuredStickers_addButton), PorterDuff.Mode.SRC_IN);
        int i6 = 0;
        while (true) {
            Drawable[] drawableArr = this.tabIcons;
            if (i6 >= drawableArr.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr[i6], getThemedColor(Theme.key_chat_emojiBottomPanelIcon), false);
            Theme.setEmojiDrawableColor(this.tabIcons[i6], getThemedColor(Theme.key_chat_emojiPanelIconSelected), true);
            i6++;
        }
        EmojiTabsStrip emojiTabsStrip2 = this.emojiTabs;
        if (emojiTabsStrip2 != null) {
            emojiTabsStrip2.updateColors();
        }
        int i7 = 0;
        while (true) {
            Drawable[] drawableArr2 = this.stickerIcons;
            if (i7 >= drawableArr2.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr2[i7], getThemedColor(Theme.key_chat_emojiPanelIcon), false);
            Theme.setEmojiDrawableColor(this.stickerIcons[i7], getThemedColor(Theme.key_chat_emojiPanelIconSelected), true);
            i7++;
        }
        int i8 = 0;
        while (true) {
            Drawable[] drawableArr3 = this.gifIcons;
            if (i8 >= drawableArr3.length) {
                break;
            }
            Theme.setEmojiDrawableColor(drawableArr3[i8], getThemedColor(Theme.key_chat_emojiPanelIcon), false);
            Theme.setEmojiDrawableColor(this.gifIcons[i8], getThemedColor(Theme.key_chat_emojiPanelIconSelected), true);
            i8++;
        }
        Drawable drawable = this.searchIconDrawable;
        if (drawable != null) {
            Theme.setEmojiDrawableColor(drawable, getThemedColor(Theme.key_chat_emojiBottomPanelIcon), false);
            Theme.setEmojiDrawableColor(this.searchIconDrawable, getThemedColor(Theme.key_chat_emojiPanelIconSelected), true);
        }
        Drawable drawable2 = this.searchIconDotDrawable;
        if (drawable2 != null) {
            int i9 = Theme.key_chat_emojiPanelStickerPackSelectorLine;
            Theme.setEmojiDrawableColor(drawable2, getThemedColor(i9), false);
            Theme.setEmojiDrawableColor(this.searchIconDotDrawable, getThemedColor(i9), true);
        }
        Paint paint2 = this.emojiLockPaint;
        if (paint2 != null) {
            paint2.setColor(getThemedColor(Theme.key_chat_emojiPanelStickerSetName));
            this.emojiLockPaint.setAlpha((int) (r0.getAlpha() * 0.5f));
        }
        Drawable drawable3 = this.emojiLockDrawable;
        if (drawable3 != null) {
            drawable3.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_emojiPanelStickerSetName), PorterDuff.Mode.MULTIPLY));
        }
    }
}
