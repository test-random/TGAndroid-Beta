package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.Property;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import java.util.HashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CloseProgressDrawable2;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;

public class ActionBarMenuItem extends FrameLayout {
    private int additionalXOffset;
    private int additionalYOffset;
    private boolean allowCloseAnimation;
    private boolean animateClear;
    private boolean animationEnabled;
    private ImageView clearButton;
    private AnimatorSet clearButtonAnimator;
    private ArrayList currentSearchFilters;
    private ActionBarMenuItemDelegate delegate;
    private float dimMenu;
    private boolean fixBackground;
    private boolean forceSmoothKeyboard;
    protected RLottieImageView iconView;
    private int iconViewResId;
    private boolean ignoreOnTextChange;
    private boolean isSearchField;
    private boolean layoutInScreen;
    private ArrayList lazyList;
    private HashMap lazyMap;
    protected ActionBarMenuItemSearchListener listener;
    private int[] location;
    private boolean longClickEnabled;
    private boolean measurePopup;
    private final AnimationNotificationsLocker notificationsLocker;
    private View.OnClickListener onClickListener;
    protected boolean overrideMenuClick;
    private ActionBarMenu parentMenu;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private ActionBarPopupWindow popupWindow;
    private boolean processedPopupClick;
    private CloseProgressDrawable2 progressDrawable;
    private Rect rect;
    private final Theme.ResourcesProvider resourcesProvider;
    private View searchAdditionalButton;
    private FrameLayout searchContainer;
    AnimatorSet searchContainerAnimator;
    private EditTextBoldCursor searchField;
    private TextView searchFieldCaption;
    private CharSequence searchFieldHint;
    private CharSequence searchFieldText;
    private LinearLayout searchFilterLayout;
    private ArrayList searchFilterViews;
    public int searchItemPaddingStart;
    public int searchRightMargin;
    private int selectedFilterIndex;
    private View selectedMenuView;
    private Runnable showMenuRunnable;
    private View showSubMenuFrom;
    private boolean showSubmenuByMove;
    private ActionBarSubMenuItemDelegate subMenuDelegate;
    private int subMenuOpenSide;
    protected TextView textView;
    private float transitionOffset;
    private boolean wrapSearchInScrollView;
    private FrameLayout wrappedSearchFrameLayout;
    private int yOffset;

    public interface ActionBarMenuItemDelegate {
        void onItemClick(int i);
    }

    public static class ActionBarMenuItemSearchListener {
        public boolean canClearCaption() {
            return true;
        }

        public boolean canCollapseSearch() {
            return true;
        }

        public boolean canToggleSearch() {
            return true;
        }

        public boolean forceShowClear() {
            return false;
        }

        public Animator getCustomToggleTransition() {
            return null;
        }

        public void onCaptionCleared() {
        }

        public void onLayout(int i, int i2, int i3, int i4) {
        }

        public void onPreToggleSearch() {
        }

        public void onSearchCollapse() {
        }

        public void onSearchExpand() {
        }

        public void onSearchFilterCleared(FiltersView.MediaFilterData mediaFilterData) {
        }

        public void onSearchPressed(EditText editText) {
        }

        public void onTextChanged(EditText editText) {
        }

        public boolean showClearForCaption() {
            return true;
        }
    }

    public interface ActionBarSubMenuItemDelegate {
        void onHideSubMenu();

        void onShowSubMenu();
    }

    public static class Item {
        public boolean dismiss;
        public int icon;
        private Integer iconColor;
        public Drawable iconDrawable;
        public int id;
        public boolean needCheck;
        private View.OnClickListener overrideClickListener;
        public CharSequence text;
        private Integer textColor;
        private View view;
        public View viewToSwipeBack;
        public int viewType;
        private int visibility = 0;
        private int rightIconVisibility = 0;

        private Item(int i) {
            this.viewType = i;
        }

        static Item access$2900() {
            return asColoredGap();
        }

        public android.view.View add(final org.telegram.ui.ActionBar.ActionBarMenuItem r14) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarMenuItem.Item.add(org.telegram.ui.ActionBar.ActionBarMenuItem):android.view.View");
        }

        private static Item asColoredGap() {
            return new Item(1);
        }

        public static Item asSubItem(int i, int i2, Drawable drawable, CharSequence charSequence, boolean z, boolean z2) {
            Item item = new Item(0);
            item.id = i;
            item.icon = i2;
            item.iconDrawable = drawable;
            item.text = charSequence;
            item.dismiss = z;
            item.needCheck = z2;
            return item;
        }

        public static Item asSwipeBackItem(int i, Drawable drawable, String str, View view) {
            Item item = new Item(2);
            item.icon = i;
            item.iconDrawable = drawable;
            item.text = str;
            item.viewToSwipeBack = view;
            return item;
        }

        public void lambda$add$0(ActionBarMenuItem actionBarMenuItem, View view) {
            if (actionBarMenuItem.popupWindow != null && actionBarMenuItem.popupWindow.isShowing() && this.dismiss) {
                if (actionBarMenuItem.processedPopupClick) {
                    return;
                }
                actionBarMenuItem.processedPopupClick = true;
                actionBarMenuItem.popupWindow.dismiss(actionBarMenuItem.allowCloseAnimation);
            }
            if (actionBarMenuItem.parentMenu != null) {
                actionBarMenuItem.parentMenu.onItemClick(((Integer) view.getTag()).intValue());
            } else if (actionBarMenuItem.delegate != null) {
                actionBarMenuItem.delegate.onItemClick(((Integer) view.getTag()).intValue());
            }
        }

        public static void lambda$add$1(ActionBarMenuItem actionBarMenuItem, int i) {
            if (actionBarMenuItem.popupLayout.getSwipeBack() != null) {
                actionBarMenuItem.popupLayout.getSwipeBack().openForeground(i);
            }
        }

        public void openSwipeBack() {
            View view = this.view;
            if (view instanceof ActionBarMenuSubItem) {
                ((ActionBarMenuSubItem) view).openSwipeBack();
            }
        }

        public void setColors(int i, int i2) {
            Integer num = this.textColor;
            if (num == null || this.iconColor == null || num.intValue() != i || this.iconColor.intValue() != i2) {
                this.textColor = Integer.valueOf(i);
                this.iconColor = Integer.valueOf(i2);
                View view = this.view;
                if (view instanceof ActionBarMenuSubItem) {
                    ((ActionBarMenuSubItem) view).setColors(i, i2);
                }
            }
        }

        public void setIcon(int i) {
            if (i != this.icon) {
                this.icon = i;
                View view = this.view;
                if (view instanceof ActionBarMenuSubItem) {
                    ((ActionBarMenuSubItem) view).setIcon(i);
                }
            }
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.overrideClickListener = onClickListener;
            View view = this.view;
            if (view != null) {
                view.setOnClickListener(onClickListener);
            }
        }

        public void setRightIconVisibility(int i) {
            if (this.rightIconVisibility != i) {
                this.rightIconVisibility = i;
                View view = this.view;
                if (view instanceof ActionBarMenuSubItem) {
                    ((ActionBarMenuSubItem) view).getRightIcon().setVisibility(this.rightIconVisibility);
                }
            }
        }

        public void setText(CharSequence charSequence) {
            this.text = charSequence;
            View view = this.view;
            if (view instanceof ActionBarMenuSubItem) {
                ((ActionBarMenuSubItem) view).setText(charSequence);
            }
        }

        public void setVisibility(int i) {
            this.visibility = i;
            View view = this.view;
            if (view != null) {
                view.setVisibility(i);
            }
        }
    }

    public static class ReactionFilterView extends SearchFilterView {
        private boolean attached;
        private ReactionsLayoutInBubble.ReactionButton reactionButton;

        public ReactionFilterView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            removeAllViews();
            setBackground(null);
            setWillNotDraw(false);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (this.attached) {
                return;
            }
            ReactionsLayoutInBubble.ReactionButton reactionButton = this.reactionButton;
            if (reactionButton != null) {
                reactionButton.attach();
            }
            this.attached = true;
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (this.attached) {
                ReactionsLayoutInBubble.ReactionButton reactionButton = this.reactionButton;
                if (reactionButton != null) {
                    reactionButton.detach();
                }
                this.attached = false;
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            ReactionsLayoutInBubble.ReactionButton reactionButton = this.reactionButton;
            if (reactionButton != null) {
                reactionButton.draw(canvas, ((getWidth() - AndroidUtilities.dp(4.0f)) - this.reactionButton.width) / 2.0f, (getHeight() - this.reactionButton.height) / 2.0f, 1.0f, 1.0f, false, false, 0.0f);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            setMeasuredDimension(AndroidUtilities.dp(49.0f), AndroidUtilities.dp(32.0f));
        }

        @Override
        public void setData(FiltersView.MediaFilterData mediaFilterData) {
            TLRPC.TL_reactionCount tL_reactionCount = new TLRPC.TL_reactionCount();
            tL_reactionCount.count = 1;
            tL_reactionCount.reaction = mediaFilterData.reaction.toTLReaction();
            ReactionsLayoutInBubble.ReactionButton reactionButton = null;
            ReactionsLayoutInBubble.ReactionButton reactionButton2 = new ReactionsLayoutInBubble.ReactionButton(reactionButton, UserConfig.selectedAccount, this, tL_reactionCount, false, true, this.resourcesProvider) {
                @Override
                protected int getCacheType() {
                    return 9;
                }

                @Override
                protected void updateColors(float f) {
                    this.lastDrawnBackgroundColor = ColorUtils.blendARGB(this.fromBackgroundColor, Theme.getColor(Theme.key_chat_inReactionButtonBackground, ReactionFilterView.this.resourcesProvider), f);
                    this.lastDrawnTagDotColor = ColorUtils.blendARGB(this.fromTagDotColor, 1526726655, f);
                }
            };
            this.reactionButton = reactionButton2;
            reactionButton2.isTag = true;
            reactionButton2.width = AndroidUtilities.dp(44.33f);
            this.reactionButton.height = AndroidUtilities.dp(28.0f);
            ReactionsLayoutInBubble.ReactionButton reactionButton3 = this.reactionButton;
            reactionButton3.choosen = true;
            if (this.attached) {
                reactionButton3.attach();
            }
        }
    }

    public static class SearchFilterView extends FrameLayout {
        BackupImageView avatarImageView;
        ImageView closeIconView;
        FiltersView.MediaFilterData data;
        Runnable removeSelectionRunnable;
        protected final Theme.ResourcesProvider resourcesProvider;
        ValueAnimator selectAnimator;
        private boolean selectedForDelete;
        private float selectedProgress;
        ShapeDrawable shapeDrawable;
        Drawable thumbDrawable;
        TextView titleView;

        public SearchFilterView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.removeSelectionRunnable = new Runnable() {
                @Override
                public void run() {
                    if (SearchFilterView.this.selectedForDelete) {
                        SearchFilterView.this.setSelectedForDelete(false);
                    }
                }
            };
            this.resourcesProvider = resourcesProvider;
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarImageView = backupImageView;
            addView(backupImageView, LayoutHelper.createFrame(32, 32.0f));
            ImageView imageView = new ImageView(context);
            this.closeIconView = imageView;
            imageView.setImageResource(R.drawable.ic_close_white);
            addView(this.closeIconView, LayoutHelper.createFrame(24, 24.0f, 16, 8.0f, 0.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 14.0f);
            addView(this.titleView, LayoutHelper.createFrame(-2, -2.0f, 16, 38.0f, 0.0f, 16.0f, 0.0f));
            ShapeDrawable createRoundRectDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(28.0f), -12292204);
            this.shapeDrawable = createRoundRectDrawable;
            setBackground(createRoundRectDrawable);
            updateColors();
        }

        public void lambda$setSelectedForDelete$0(ValueAnimator valueAnimator) {
            this.selectedProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            updateColors();
        }

        public void updateColors() {
            int themedColor = getThemedColor(Theme.key_groupcreate_spanBackground);
            int i = Theme.key_avatar_backgroundBlue;
            int themedColor2 = getThemedColor(i);
            int themedColor3 = getThemedColor(Theme.key_windowBackgroundWhiteBlackText);
            int i2 = Theme.key_avatar_actionBarIconBlue;
            int themedColor4 = getThemedColor(i2);
            this.shapeDrawable.getPaint().setColor(ColorUtils.blendARGB(themedColor, themedColor2, this.selectedProgress));
            this.titleView.setTextColor(ColorUtils.blendARGB(themedColor3, themedColor4, this.selectedProgress));
            this.closeIconView.setColorFilter(themedColor4);
            this.closeIconView.setAlpha(this.selectedProgress);
            this.closeIconView.setScaleX(this.selectedProgress * 0.82f);
            this.closeIconView.setScaleY(this.selectedProgress * 0.82f);
            Drawable drawable = this.thumbDrawable;
            if (drawable != null) {
                Theme.setCombinedDrawableColor(drawable, getThemedColor(i), false);
                Theme.setCombinedDrawableColor(this.thumbDrawable, getThemedColor(i2), true);
            }
            this.avatarImageView.setAlpha(1.0f - this.selectedProgress);
            FiltersView.MediaFilterData mediaFilterData = this.data;
            if (mediaFilterData != null && mediaFilterData.filterType == 7) {
                setData(mediaFilterData);
            }
            invalidate();
        }

        public FiltersView.MediaFilterData getFilter() {
            return this.data;
        }

        protected int getThemedColor(int i) {
            return Theme.getColor(i, this.resourcesProvider);
        }

        public void setData(FiltersView.MediaFilterData mediaFilterData) {
            CombinedDrawable createCircleDrawableWithIcon;
            int i;
            TLRPC.Chat chat;
            this.data = mediaFilterData;
            this.titleView.setText(mediaFilterData.getTitle());
            CombinedDrawable createCircleDrawableWithIcon2 = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), mediaFilterData.iconResFilled);
            this.thumbDrawable = createCircleDrawableWithIcon2;
            Theme.setCombinedDrawableColor(createCircleDrawableWithIcon2, getThemedColor(Theme.key_avatar_backgroundBlue), false);
            Drawable drawable = this.thumbDrawable;
            int i2 = Theme.key_avatar_actionBarIconBlue;
            Theme.setCombinedDrawableColor(drawable, getThemedColor(i2), true);
            int i3 = mediaFilterData.filterType;
            if (i3 == 4) {
                TLObject tLObject = mediaFilterData.chat;
                if (tLObject instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) tLObject;
                    long j = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser().id;
                    long j2 = user.id;
                    chat = user;
                    if (j == j2) {
                        createCircleDrawableWithIcon = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), R.drawable.chats_saved);
                        createCircleDrawableWithIcon.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                        i = Theme.key_avatar_backgroundSaved;
                    }
                } else if (!(tLObject instanceof TLRPC.Chat)) {
                    return;
                } else {
                    chat = (TLRPC.Chat) tLObject;
                }
                this.avatarImageView.getImageReceiver().setRoundRadius(AndroidUtilities.dp(16.0f));
                this.avatarImageView.getImageReceiver().setForUserOrChat(chat, this.thumbDrawable);
                return;
            }
            if (i3 != 7) {
                this.avatarImageView.setImageDrawable(this.thumbDrawable);
                return;
            } else {
                createCircleDrawableWithIcon = Theme.createCircleDrawableWithIcon(AndroidUtilities.dp(32.0f), R.drawable.chats_archive);
                createCircleDrawableWithIcon.setIconSize(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
                i = Theme.key_avatar_backgroundArchived;
            }
            Theme.setCombinedDrawableColor(createCircleDrawableWithIcon, getThemedColor(i), false);
            Theme.setCombinedDrawableColor(createCircleDrawableWithIcon, getThemedColor(i2), true);
            this.avatarImageView.setImageDrawable(createCircleDrawableWithIcon);
        }

        public void setExpanded(boolean z) {
            if (z) {
                this.titleView.setVisibility(0);
            } else {
                this.titleView.setVisibility(8);
                setSelectedForDelete(false);
            }
        }

        public void setSelectedForDelete(final boolean z) {
            if (this.selectedForDelete == z) {
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(this.removeSelectionRunnable);
            this.selectedForDelete = z;
            ValueAnimator valueAnimator = this.selectAnimator;
            if (valueAnimator != null) {
                valueAnimator.removeAllListeners();
                this.selectAnimator.cancel();
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.selectedProgress, z ? 1.0f : 0.0f);
            this.selectAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    ActionBarMenuItem.SearchFilterView.this.lambda$setSelectedForDelete$0(valueAnimator2);
                }
            });
            this.selectAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    SearchFilterView.this.selectedProgress = z ? 1.0f : 0.0f;
                    SearchFilterView.this.updateColors();
                }
            });
            this.selectAnimator.setDuration(150L).start();
            if (this.selectedForDelete) {
                AndroidUtilities.runOnUIThread(this.removeSelectionRunnable, 2000L);
            }
        }
    }

    public ActionBarMenuItem(Context context, ActionBarMenu actionBarMenu, int i, int i2) {
        this(context, actionBarMenu, i, i2, false);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu actionBarMenu, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
        this(context, actionBarMenu, i, i2, false, resourcesProvider);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu actionBarMenu, int i, int i2, boolean z) {
        this(context, actionBarMenu, i, i2, z, null);
    }

    public ActionBarMenuItem(Context context, ActionBarMenu actionBarMenu, int i, int i2, boolean z, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.searchFilterViews = new ArrayList();
        this.allowCloseAnimation = true;
        this.animationEnabled = true;
        this.animateClear = true;
        this.measurePopup = true;
        this.showSubmenuByMove = true;
        this.currentSearchFilters = new ArrayList();
        this.selectedFilterIndex = -1;
        this.notificationsLocker = new AnimationNotificationsLocker();
        this.resourcesProvider = resourcesProvider;
        if (i != 0) {
            setBackgroundDrawable(Theme.createSelectorDrawable(i, z ? 5 : 1));
        }
        this.parentMenu = actionBarMenu;
        if (!z) {
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.iconView = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            this.iconView.setImportantForAccessibility(2);
            addView(this.iconView, LayoutHelper.createFrame(-1, -1.0f));
            if (i2 != 0) {
                this.iconView.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
                return;
            }
            return;
        }
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextSize(1, 15.0f);
        this.textView.setTypeface(AndroidUtilities.bold());
        this.textView.setGravity(17);
        this.textView.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        this.textView.setImportantForAccessibility(2);
        if (i2 != 0) {
            this.textView.setTextColor(i2);
        }
        addView(this.textView, LayoutHelper.createFrame(-2, -1.0f));
    }

    public static ActionBarMenuSubItem addItem(ViewGroup viewGroup, int i, CharSequence charSequence, boolean z, Theme.ResourcesProvider resourcesProvider) {
        return addItem(false, false, viewGroup, i, charSequence, z, resourcesProvider);
    }

    public static ActionBarMenuSubItem addItem(boolean z, boolean z2, ViewGroup viewGroup, int i, CharSequence charSequence, boolean z3, Theme.ResourcesProvider resourcesProvider) {
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(viewGroup.getContext(), z3, z, z2, resourcesProvider);
        actionBarMenuSubItem.setTextAndIcon(charSequence, i);
        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
        viewGroup.addView(actionBarMenuSubItem);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) actionBarMenuSubItem.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        actionBarMenuSubItem.setLayoutParams(layoutParams);
        return actionBarMenuSubItem;
    }

    public static void addText(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, String str, Theme.ResourcesProvider resourcesProvider) {
        TextView textView = new TextView(actionBarPopupWindowLayout.getContext());
        textView.setTextSize(1, 13.0f);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, resourcesProvider));
        textView.setPadding(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f));
        textView.setText(str);
        textView.setTag(R.id.fit_width_tag, 1);
        textView.setMaxWidth(AndroidUtilities.dp(200.0f));
        actionBarPopupWindowLayout.addView((View) textView, LayoutHelper.createLinear(-1, -2));
    }

    public void checkClearButton() {
        AnimatorSet duration;
        AnimatorListenerAdapter animatorListenerAdapter;
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener;
        TextView textView;
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener2;
        if (this.clearButton != null) {
            if (!hasRemovableFilters() && TextUtils.isEmpty(this.searchField.getText()) && (((actionBarMenuItemSearchListener = this.listener) == null || !actionBarMenuItemSearchListener.forceShowClear()) && ((textView = this.searchFieldCaption) == null || textView.getVisibility() != 0 || ((actionBarMenuItemSearchListener2 = this.listener) != null && !actionBarMenuItemSearchListener2.showClearForCaption())))) {
                if (this.clearButton.getTag() != null) {
                    this.clearButton.setTag(null);
                    AnimatorSet animatorSet = this.clearButtonAnimator;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                    }
                    if (!this.animateClear) {
                        this.clearButton.setAlpha(0.0f);
                        this.clearButton.setRotation(45.0f);
                        this.clearButton.setScaleX(0.0f);
                        this.clearButton.setScaleY(0.0f);
                        this.clearButton.setVisibility(4);
                        this.animateClear = true;
                        return;
                    }
                    duration = new AnimatorSet().setDuration(180L);
                    duration.setInterpolator(new DecelerateInterpolator());
                    ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                    ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            ActionBarMenuItem.this.lambda$checkClearButton$15(valueAnimator);
                        }
                    });
                    duration.playTogether(ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) View.ALPHA, 0.0f), ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) View.SCALE_X, 0.0f), ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) View.SCALE_Y, 0.0f), ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) View.ROTATION, 45.0f), ofFloat);
                    animatorListenerAdapter = new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animator) {
                            ActionBarMenuItem.this.clearButton.setVisibility(4);
                            ActionBarMenuItem.this.clearButtonAnimator = null;
                        }
                    };
                    duration.addListener(animatorListenerAdapter);
                    duration.start();
                    this.clearButtonAnimator = duration;
                }
                return;
            }
            if (this.clearButton.getTag() == null) {
                this.clearButton.setTag(1);
                AnimatorSet animatorSet2 = this.clearButtonAnimator;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                }
                this.clearButton.setVisibility(0);
                if (!this.animateClear) {
                    this.clearButton.setAlpha(1.0f);
                    this.clearButton.setRotation(0.0f);
                    this.clearButton.setScaleX(1.0f);
                    this.clearButton.setScaleY(1.0f);
                    View view = this.searchAdditionalButton;
                    if (view != null) {
                        view.setTranslationX(0.0f);
                    }
                    this.animateClear = true;
                    return;
                }
                duration = new AnimatorSet().setDuration(180L);
                duration.setInterpolator(new DecelerateInterpolator());
                ValueAnimator ofFloat2 = ValueAnimator.ofFloat(1.0f, 0.0f);
                ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ActionBarMenuItem.this.lambda$checkClearButton$16(valueAnimator);
                    }
                });
                duration.playTogether(ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) View.ALPHA, 1.0f), ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) View.SCALE_X, 1.0f), ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) View.SCALE_Y, 1.0f), ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) View.ROTATION, 0.0f), ofFloat2);
                animatorListenerAdapter = new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        ActionBarMenuItem.this.clearButtonAnimator = null;
                    }
                };
                duration.addListener(animatorListenerAdapter);
                duration.start();
                this.clearButtonAnimator = duration;
            }
        }
    }

    private void checkCreateSearchField() {
        ActionBarMenu actionBarMenu;
        FrameLayout frameLayout;
        LinearLayout.LayoutParams createLinear;
        float f;
        FrameLayout frameLayout2;
        View view;
        float f2;
        int i;
        float f3;
        int i2;
        float f4;
        float f5;
        if (this.searchContainer == null && this.isSearchField) {
            FrameLayout frameLayout3 = new FrameLayout(getContext()) {
                private boolean ignoreRequestLayout;

                @Override
                protected void onLayout(boolean z, int i3, int i4, int i5, int i6) {
                    super.onLayout(z, i3, i4, i5, i6);
                    int measuredWidth = (!LocaleController.isRTL && ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) ? ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f) : 0;
                    if (ActionBarMenuItem.this.searchFilterLayout.getVisibility() == 0) {
                        measuredWidth += ActionBarMenuItem.this.searchFilterLayout.getMeasuredWidth();
                    }
                    ActionBarMenuItem.this.searchField.layout(measuredWidth, ActionBarMenuItem.this.searchField.getTop(), ActionBarMenuItem.this.searchField.getMeasuredWidth() + measuredWidth, ActionBarMenuItem.this.searchField.getBottom());
                }

                @Override
                protected void onMeasure(int i3, int i4) {
                    int i5;
                    int max;
                    int i6;
                    if (!ActionBarMenuItem.this.wrapSearchInScrollView) {
                        measureChildWithMargins(ActionBarMenuItem.this.clearButton, i3, 0, i4, 0);
                        if (ActionBarMenuItem.this.searchAdditionalButton != null) {
                            measureChildWithMargins(ActionBarMenuItem.this.searchAdditionalButton, i3, 0, i4, 0);
                        }
                    }
                    if (LocaleController.isRTL) {
                        if (ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                            measureChildWithMargins(ActionBarMenuItem.this.searchFieldCaption, i3, View.MeasureSpec.getSize(i3) / 2, i4, 0);
                            i5 = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                        } else {
                            i5 = 0;
                        }
                        int size = View.MeasureSpec.getSize(i3);
                        this.ignoreRequestLayout = true;
                        measureChildWithMargins(ActionBarMenuItem.this.searchFilterLayout, i3, i5, i4, 0);
                        int measuredWidth = ActionBarMenuItem.this.searchFilterLayout.getVisibility() == 0 ? ActionBarMenuItem.this.searchFilterLayout.getMeasuredWidth() : 0;
                        measureChildWithMargins(ActionBarMenuItem.this.searchField, View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(12.0f), 0), i5 + measuredWidth, i4, 0);
                        this.ignoreRequestLayout = false;
                        max = Math.max(measuredWidth + ActionBarMenuItem.this.searchField.getMeasuredWidth(), size);
                    } else {
                        if (ActionBarMenuItem.this.searchFieldCaption.getVisibility() == 0) {
                            measureChildWithMargins(ActionBarMenuItem.this.searchFieldCaption, i3, View.MeasureSpec.getSize(i3) / 2, i4, 0);
                            i6 = ActionBarMenuItem.this.searchFieldCaption.getMeasuredWidth() + AndroidUtilities.dp(4.0f);
                        } else {
                            i6 = 0;
                        }
                        int size2 = View.MeasureSpec.getSize(i3);
                        this.ignoreRequestLayout = true;
                        measureChildWithMargins(ActionBarMenuItem.this.searchFilterLayout, i3, i6, i4, 0);
                        int measuredWidth2 = ActionBarMenuItem.this.searchFilterLayout.getVisibility() == 0 ? ActionBarMenuItem.this.searchFilterLayout.getMeasuredWidth() : 0;
                        measureChildWithMargins(ActionBarMenuItem.this.searchField, i3, i6 + measuredWidth2 + (ActionBarMenuItem.this.searchAdditionalButton != null ? ActionBarMenuItem.this.searchAdditionalButton.getMeasuredWidth() : 0), i4, 0);
                        this.ignoreRequestLayout = false;
                        max = Math.max(measuredWidth2 + ActionBarMenuItem.this.searchField.getMeasuredWidth(), size2);
                    }
                    setMeasuredDimension(max, View.MeasureSpec.getSize(i4));
                }

                @Override
                public void requestLayout() {
                    if (this.ignoreRequestLayout) {
                        return;
                    }
                    super.requestLayout();
                }

                @Override
                public void setAlpha(float f6) {
                    super.setAlpha(f6);
                    if (ActionBarMenuItem.this.clearButton == null || ActionBarMenuItem.this.clearButton.getTag() == null) {
                        return;
                    }
                    ActionBarMenuItem.this.clearButton.setAlpha(f6);
                    ActionBarMenuItem.this.clearButton.setScaleX(f6);
                    ActionBarMenuItem.this.clearButton.setScaleY(f6);
                }

                @Override
                public void setVisibility(int i3) {
                    super.setVisibility(i3);
                    if (ActionBarMenuItem.this.clearButton != null) {
                        ActionBarMenuItem.this.clearButton.setVisibility(i3);
                    }
                    if (ActionBarMenuItem.this.searchAdditionalButton != null) {
                        ActionBarMenuItem.this.searchAdditionalButton.setVisibility(i3);
                    }
                    if (ActionBarMenuItem.this.wrappedSearchFrameLayout != null) {
                        ActionBarMenuItem.this.wrappedSearchFrameLayout.setVisibility(i3);
                    }
                }
            };
            this.searchContainer = frameLayout3;
            frameLayout3.setClipChildren(this.searchItemPaddingStart != 0);
            this.wrappedSearchFrameLayout = null;
            if (this.wrapSearchInScrollView) {
                this.wrappedSearchFrameLayout = new FrameLayout(getContext());
                HorizontalScrollView horizontalScrollView = new HorizontalScrollView(getContext()) {
                    boolean isDragging;

                    private void checkDragg(MotionEvent motionEvent) {
                        if (motionEvent.getAction() == 0) {
                            this.isDragging = true;
                        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                            this.isDragging = false;
                        }
                    }

                    @Override
                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        checkDragg(motionEvent);
                        return super.onInterceptTouchEvent(motionEvent);
                    }

                    @Override
                    protected void onOverScrolled(int i3, int i4, boolean z, boolean z2) {
                        if (this.isDragging) {
                            super.onOverScrolled(i3, i4, z, z2);
                        }
                    }

                    @Override
                    public boolean onTouchEvent(MotionEvent motionEvent) {
                        checkDragg(motionEvent);
                        return super.onTouchEvent(motionEvent);
                    }
                };
                horizontalScrollView.addView(this.searchContainer, LayoutHelper.createScroll(-2, -1, 0));
                horizontalScrollView.setHorizontalScrollBarEnabled(false);
                horizontalScrollView.setClipChildren(this.searchItemPaddingStart != 0);
                this.wrappedSearchFrameLayout.addView(horizontalScrollView, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 48.0f, 0.0f));
                actionBarMenu = this.parentMenu;
                frameLayout = this.wrappedSearchFrameLayout;
                createLinear = LayoutHelper.createLinear(0, -1, 1.0f, this.searchItemPaddingStart, 0, 0, 0);
            } else {
                actionBarMenu = this.parentMenu;
                frameLayout = this.searchContainer;
                createLinear = LayoutHelper.createLinear(0, -1, 1.0f, this.searchItemPaddingStart + 6, 0, this.searchRightMargin, 0);
            }
            actionBarMenu.addView(frameLayout, 0, createLinear);
            this.searchContainer.setVisibility(8);
            TextView textView = new TextView(getContext());
            this.searchFieldCaption = textView;
            textView.setTextSize(1, 18.0f);
            TextView textView2 = this.searchFieldCaption;
            int i3 = Theme.key_actionBarDefaultSearch;
            textView2.setTextColor(getThemedColor(i3));
            this.searchFieldCaption.setSingleLine(true);
            this.searchFieldCaption.setEllipsize(TextUtils.TruncateAt.END);
            this.searchFieldCaption.setVisibility(8);
            this.searchFieldCaption.setGravity(LocaleController.isRTL ? 5 : 3);
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(getContext()) {
                @Override
                public boolean onKeyDown(int i4, KeyEvent keyEvent) {
                    if (i4 != 67 || ActionBarMenuItem.this.searchField.length() != 0 || ((ActionBarMenuItem.this.searchFieldCaption.getVisibility() != 0 || ActionBarMenuItem.this.searchFieldCaption.length() <= 0) && !ActionBarMenuItem.this.hasRemovableFilters())) {
                        return super.onKeyDown(i4, keyEvent);
                    }
                    if (ActionBarMenuItem.this.hasRemovableFilters()) {
                        FiltersView.MediaFilterData mediaFilterData = (FiltersView.MediaFilterData) ActionBarMenuItem.this.currentSearchFilters.get(ActionBarMenuItem.this.currentSearchFilters.size() - 1);
                        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = ActionBarMenuItem.this.listener;
                        if (actionBarMenuItemSearchListener != null) {
                            actionBarMenuItemSearchListener.onSearchFilterCleared(mediaFilterData);
                        }
                        ActionBarMenuItem.this.removeSearchFilter(mediaFilterData);
                    } else {
                        ActionBarMenuItem.this.clearButton.callOnClick();
                    }
                    return true;
                }

                @Override
                public void onMeasure(int i4, int i5) {
                    super.onMeasure(i4, i5);
                    setMeasuredDimension(Math.max(View.MeasureSpec.getSize(i4), getMeasuredWidth()) + AndroidUtilities.dp(3.0f), getMeasuredHeight());
                }

                @Override
                public void onSelectionChanged(int i4, int i5) {
                    super.onSelectionChanged(i4, i5);
                }

                @Override
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    boolean onTouchEvent = super.onTouchEvent(motionEvent);
                    if (motionEvent.getAction() == 1 && !AndroidUtilities.showKeyboard(this)) {
                        clearFocus();
                        requestFocus();
                    }
                    return onTouchEvent;
                }
            };
            this.searchField = editTextBoldCursor;
            editTextBoldCursor.setScrollContainer(false);
            this.searchField.setCursorWidth(1.5f);
            this.searchField.setCursorColor(getThemedColor(i3));
            this.searchField.setTextSize(1, 18.0f);
            this.searchField.setHintTextColor(getThemedColor(Theme.key_actionBarDefaultSearchPlaceholder));
            this.searchField.setTextColor(getThemedColor(i3));
            this.searchField.setSingleLine(true);
            this.searchField.setBackgroundResource(0);
            this.searchField.setPadding(0, 0, 0, 0);
            this.searchField.setInputType(this.searchField.getInputType() | 524288);
            if (Build.VERSION.SDK_INT < 23) {
                this.searchField.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    @Override
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        return false;
                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }
                });
            }
            this.searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public final boolean onEditorAction(TextView textView3, int i4, KeyEvent keyEvent) {
                    boolean lambda$checkCreateSearchField$13;
                    lambda$checkCreateSearchField$13 = ActionBarMenuItem.this.lambda$checkCreateSearchField$13(textView3, i4, keyEvent);
                    return lambda$checkCreateSearchField$13;
                }
            });
            this.searchField.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable editable) {
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i4, int i5, int i6) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i4, int i5, int i6) {
                    if (ActionBarMenuItem.this.ignoreOnTextChange) {
                        ActionBarMenuItem.this.ignoreOnTextChange = false;
                        return;
                    }
                    ActionBarMenuItem actionBarMenuItem = ActionBarMenuItem.this;
                    ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = actionBarMenuItem.listener;
                    if (actionBarMenuItemSearchListener != null) {
                        actionBarMenuItemSearchListener.onTextChanged(actionBarMenuItem.searchField);
                    }
                    ActionBarMenuItem.this.checkClearButton();
                    if (ActionBarMenuItem.this.currentSearchFilters.isEmpty() || TextUtils.isEmpty(ActionBarMenuItem.this.searchField.getText()) || ActionBarMenuItem.this.selectedFilterIndex < 0) {
                        return;
                    }
                    ActionBarMenuItem.this.selectedFilterIndex = -1;
                    ActionBarMenuItem.this.onFiltersChanged();
                }
            });
            this.searchField.setImeOptions(234881027);
            this.searchField.setTextIsSelectable(false);
            this.searchField.setHighlightColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
            this.searchField.setHandlesColor(getThemedColor(Theme.key_chat_TextSelectionCursor));
            CharSequence charSequence = this.searchFieldHint;
            if (charSequence != null) {
                this.searchField.setHint(charSequence);
                setContentDescription(this.searchFieldHint);
            }
            CharSequence charSequence2 = this.searchFieldText;
            if (charSequence2 != null) {
                this.searchField.setText(charSequence2);
            }
            LinearLayout linearLayout = new LinearLayout(getContext());
            this.searchFilterLayout = linearLayout;
            linearLayout.setOrientation(0);
            this.searchFilterLayout.setVisibility(0);
            if (LocaleController.isRTL) {
                this.searchContainer.addView(this.searchFilterLayout, LayoutHelper.createFrame(-2, 32.0f, 16, 0.0f, 0.0f, 48.0f, 0.0f));
                f = 0.0f;
                this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-1, 36.0f, 16, 0.0f, 0.0f, this.wrapSearchInScrollView ? 0.0f : 48.0f, 0.0f));
                frameLayout2 = this.searchContainer;
                view = this.searchFieldCaption;
                f2 = 48.0f;
                i = -2;
                f3 = 36.0f;
                i2 = 21;
                f4 = 0.0f;
                f5 = 5.5f;
            } else {
                this.searchContainer.addView(this.searchFieldCaption, LayoutHelper.createFrame(-2, 36.0f, 19, 0.0f, 5.5f, 0.0f, 0.0f));
                f = 0.0f;
                this.searchContainer.addView(this.searchField, LayoutHelper.createFrame(-1, 36.0f, 16, 6.0f, 0.0f, this.wrapSearchInScrollView ? 0.0f : 48.0f, 0.0f));
                frameLayout2 = this.searchContainer;
                view = this.searchFilterLayout;
                f2 = 48.0f;
                i = -2;
                f3 = 32.0f;
                i2 = 16;
                f4 = 0.0f;
                f5 = 0.0f;
            }
            frameLayout2.addView(view, LayoutHelper.createFrame(i, f3, i2, f4, f5, f2, f));
            this.searchFilterLayout.setClipChildren(false);
            ImageView imageView = new ImageView(getContext()) {
                @Override
                public void draw(Canvas canvas) {
                    getBackground().draw(canvas);
                    super.draw(canvas);
                }

                @Override
                protected void onDetachedFromWindow() {
                    super.onDetachedFromWindow();
                    clearAnimation();
                    if (getTag() != null) {
                        ActionBarMenuItem.this.clearButton.setAlpha(1.0f);
                        ActionBarMenuItem.this.clearButton.setRotation(0.0f);
                        ActionBarMenuItem.this.clearButton.setScaleX(1.0f);
                        ActionBarMenuItem.this.clearButton.setScaleY(1.0f);
                        return;
                    }
                    ActionBarMenuItem.this.clearButton.setVisibility(4);
                    ActionBarMenuItem.this.clearButton.setAlpha(0.0f);
                    ActionBarMenuItem.this.clearButton.setRotation(45.0f);
                    ActionBarMenuItem.this.clearButton.setScaleX(0.0f);
                    ActionBarMenuItem.this.clearButton.setScaleY(0.0f);
                }
            };
            this.clearButton = imageView;
            CloseProgressDrawable2 closeProgressDrawable2 = new CloseProgressDrawable2() {
                @Override
                public int getCurrentColor() {
                    return ActionBarMenuItem.this.parentMenu.parentActionBar.itemsColor;
                }
            };
            this.progressDrawable = closeProgressDrawable2;
            imageView.setImageDrawable(closeProgressDrawable2);
            this.clearButton.setBackground(Theme.createSelectorDrawable(this.parentMenu.parentActionBar.itemsActionModeBackgroundColor, 1));
            this.clearButton.setScaleType(ImageView.ScaleType.CENTER);
            this.clearButton.setAlpha(0.0f);
            this.clearButton.setRotation(45.0f);
            this.clearButton.setScaleX(0.0f);
            this.clearButton.setScaleY(0.0f);
            this.clearButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    ActionBarMenuItem.this.lambda$checkCreateSearchField$14(view2);
                }
            });
            this.clearButton.setContentDescription(LocaleController.getString(R.string.ClearButton));
            (this.wrapSearchInScrollView ? this.wrappedSearchFrameLayout : this.searchContainer).addView(this.clearButton, LayoutHelper.createFrame(48, -1, 21));
        }
    }

    public void createPopupLayout() {
        if (this.popupLayout != null) {
            return;
        }
        this.rect = new Rect();
        this.location = new int[2];
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext(), R.drawable.popup_fixed_alert2, this.resourcesProvider, 1);
        this.popupLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$createPopupLayout$1;
                lambda$createPopupLayout$1 = ActionBarMenuItem.this.lambda$createPopupLayout$1(view, motionEvent);
                return lambda$createPopupLayout$1;
            }
        });
        this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
            @Override
            public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                ActionBarMenuItem.this.lambda$createPopupLayout$2(keyEvent);
            }
        });
    }

    private Item findLazyItem(int i) {
        HashMap hashMap = this.lazyMap;
        if (hashMap == null) {
            return null;
        }
        return (Item) hashMap.get(Integer.valueOf(i));
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public boolean hasRemovableFilters() {
        if (this.currentSearchFilters.isEmpty()) {
            return false;
        }
        for (int i = 0; i < this.currentSearchFilters.size(); i++) {
            if (((FiltersView.MediaFilterData) this.currentSearchFilters.get(i)).removable) {
                return true;
            }
        }
        return false;
    }

    public void lambda$addSubItem$3(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            if (this.processedPopupClick) {
                return;
            }
            this.processedPopupClick = true;
            this.popupWindow.dismiss(this.allowCloseAnimation);
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public void lambda$addSubItem$4(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            if (this.processedPopupClick) {
                return;
            }
            this.processedPopupClick = true;
            if (!this.allowCloseAnimation) {
                this.popupWindow.setAnimationStyle(R.style.PopupAnimation);
            }
            this.popupWindow.dismiss(this.allowCloseAnimation);
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public void lambda$addSubItem$5(boolean z, View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing() && z) {
            if (this.processedPopupClick) {
                return;
            }
            this.processedPopupClick = true;
            this.popupWindow.dismiss(this.allowCloseAnimation);
        }
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public void lambda$addSubItem$6(View view) {
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            actionBarMenu.onItemClick(((Integer) view.getTag()).intValue());
            return;
        }
        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
        if (actionBarMenuItemDelegate != null) {
            actionBarMenuItemDelegate.onItemClick(((Integer) view.getTag()).intValue());
        }
    }

    public void lambda$addSwipeBackItem$7(int i) {
        if (this.popupLayout.getSwipeBack() != null) {
            this.popupLayout.getSwipeBack().openForeground(i);
        }
    }

    public void lambda$checkClearButton$15(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.searchAdditionalButton;
        if (view != null) {
            view.setTranslationX(AndroidUtilities.dp(32.0f) * floatValue);
        }
    }

    public void lambda$checkClearButton$16(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.searchAdditionalButton;
        if (view != null) {
            view.setTranslationX(AndroidUtilities.dp(32.0f) * floatValue);
        }
    }

    public boolean lambda$checkCreateSearchField$13(TextView textView, int i, KeyEvent keyEvent) {
        if (keyEvent == null) {
            return false;
        }
        if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.searchField);
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener == null) {
            return false;
        }
        actionBarMenuItemSearchListener.onSearchPressed(this.searchField);
        return false;
    }

    public void lambda$checkCreateSearchField$14(View view) {
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener;
        if (this.searchField.length() != 0) {
            this.searchField.setText("");
        } else if (hasRemovableFilters()) {
            this.searchField.hideActionMode();
            for (int i = 0; i < this.currentSearchFilters.size(); i++) {
                if (this.listener != null && ((FiltersView.MediaFilterData) this.currentSearchFilters.get(i)).removable) {
                    this.listener.onSearchFilterCleared((FiltersView.MediaFilterData) this.currentSearchFilters.get(i));
                }
            }
            clearSearchFilters();
        } else {
            TextView textView = this.searchFieldCaption;
            if (textView != null && textView.getVisibility() == 0 && ((actionBarMenuItemSearchListener = this.listener) == null || actionBarMenuItemSearchListener.canClearCaption())) {
                this.searchFieldCaption.setVisibility(8);
                ActionBarMenuItemSearchListener actionBarMenuItemSearchListener2 = this.listener;
                if (actionBarMenuItemSearchListener2 != null) {
                    actionBarMenuItemSearchListener2.onCaptionCleared();
                }
            }
        }
        this.searchField.requestFocus();
        AndroidUtilities.showKeyboard(this.searchField);
    }

    public boolean lambda$createPopupLayout$1(View view, MotionEvent motionEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (motionEvent.getActionMasked() != 0 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        view.getHitRect(this.rect);
        if (this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
            return false;
        }
        this.popupWindow.dismiss();
        return false;
    }

    public void lambda$createPopupLayout$2(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    public void lambda$onFiltersChanged$12(SearchFilterView searchFilterView, View view) {
        int indexOf = this.currentSearchFilters.indexOf(searchFilterView.getFilter());
        if (this.selectedFilterIndex != indexOf) {
            this.selectedFilterIndex = indexOf;
            onFiltersChanged();
            return;
        }
        if (searchFilterView.getFilter().removable) {
            if (!searchFilterView.selectedForDelete) {
                searchFilterView.setSelectedForDelete(true);
                return;
            }
            FiltersView.MediaFilterData filter = searchFilterView.getFilter();
            removeSearchFilter(filter);
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
            if (actionBarMenuItemSearchListener != null) {
                actionBarMenuItemSearchListener.onSearchFilterCleared(filter);
                this.listener.onTextChanged(this.searchField);
            }
        }
    }

    public void lambda$onTouchEvent$0() {
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        toggleSubMenu();
    }

    public void lambda$setOnMenuDismiss$11(Utilities.Callback callback) {
        if (callback != null) {
            callback.run(Boolean.valueOf(this.processedPopupClick));
        }
    }

    public void lambda$toggleSubMenu$10() {
        onDismiss();
        ActionBarSubMenuItemDelegate actionBarSubMenuItemDelegate = this.subMenuDelegate;
        if (actionBarSubMenuItemDelegate != null) {
            actionBarSubMenuItemDelegate.onHideSubMenu();
        }
    }

    public boolean lambda$toggleSubMenu$9(View view, int i, KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (i != 82 || keyEvent.getRepeatCount() != 0 || keyEvent.getAction() != 1 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        this.popupWindow.dismiss();
        return true;
    }

    private void layoutLazyItems() {
        if (this.lazyList == null) {
            return;
        }
        for (int i = 0; i < this.lazyList.size(); i++) {
            ((Item) this.lazyList.get(i)).add(this);
        }
        this.lazyList.clear();
    }

    public void onFiltersChanged() {
        boolean z = !this.currentSearchFilters.isEmpty();
        ArrayList arrayList = new ArrayList(this.currentSearchFilters);
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null && frameLayout.getTag() != null) {
            TransitionSet transitionSet = new TransitionSet();
            ChangeBounds changeBounds = new ChangeBounds();
            changeBounds.setDuration(150L);
            transitionSet.addTransition(new Visibility() {
                @Override
                public Animator onAppear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
                    if (!(view instanceof SearchFilterView)) {
                        return ObjectAnimator.ofFloat(view, (Property<View, Float>) View.ALPHA, 0.0f, 1.0f);
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.ALPHA, 0.0f, 1.0f), ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_X, 0.5f, 1.0f), ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_Y, 0.5f, 1.0f));
                    animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    return animatorSet;
                }

                @Override
                public Animator onDisappear(ViewGroup viewGroup, View view, TransitionValues transitionValues, TransitionValues transitionValues2) {
                    if (!(view instanceof SearchFilterView)) {
                        return ObjectAnimator.ofFloat(view, (Property<View, Float>) View.ALPHA, 1.0f, 0.0f);
                    }
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.ALPHA, view.getAlpha(), 0.0f), ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_X, view.getScaleX(), 0.5f), ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_Y, view.getScaleX(), 0.5f));
                    animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                    return animatorSet;
                }
            }.setDuration(150L)).addTransition(changeBounds);
            transitionSet.setOrdering(0);
            transitionSet.setInterpolator((TimeInterpolator) CubicBezierInterpolator.EASE_OUT);
            transitionSet.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionCancel(Transition transition) {
                    ActionBarMenuItem.this.notificationsLocker.unlock();
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    ActionBarMenuItem.this.notificationsLocker.unlock();
                }

                @Override
                public void onTransitionPause(Transition transition) {
                }

                @Override
                public void onTransitionResume(Transition transition) {
                }

                @Override
                public void onTransitionStart(Transition transition) {
                    ActionBarMenuItem.this.notificationsLocker.lock();
                }
            });
            TransitionManager.beginDelayedTransition(this.searchFilterLayout, transitionSet);
        }
        if (this.searchFilterLayout != null) {
            int i = 0;
            while (i < this.searchFilterLayout.getChildCount()) {
                if (!arrayList.remove(((SearchFilterView) this.searchFilterLayout.getChildAt(i)).getFilter())) {
                    this.searchFilterLayout.removeViewAt(i);
                    i--;
                }
                i++;
            }
        }
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            FiltersView.MediaFilterData mediaFilterData = (FiltersView.MediaFilterData) arrayList.get(i2);
            final SearchFilterView reactionFilterView = mediaFilterData.reaction != null ? new ReactionFilterView(getContext(), this.resourcesProvider) : new SearchFilterView(getContext(), this.resourcesProvider);
            reactionFilterView.setData(mediaFilterData);
            reactionFilterView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ActionBarMenuItem.this.lambda$onFiltersChanged$12(reactionFilterView, view);
                }
            });
            this.searchFilterLayout.addView(reactionFilterView, LayoutHelper.createLinear(-2, -1, 0, 0, 0, 6, 0));
        }
        int i3 = 0;
        while (i3 < this.searchFilterLayout.getChildCount()) {
            ((SearchFilterView) this.searchFilterLayout.getChildAt(i3)).setExpanded(i3 == this.selectedFilterIndex);
            i3++;
        }
        this.searchFilterLayout.setTag(z ? 1 : null);
        final float x = this.searchField.getX();
        if (this.searchContainer.getTag() != null) {
            this.searchField.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    ActionBarMenuItem.this.searchField.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (ActionBarMenuItem.this.searchField.getX() != x) {
                        ActionBarMenuItem.this.searchField.setTranslationX(x - ActionBarMenuItem.this.searchField.getX());
                    }
                    ActionBarMenuItem.this.searchField.animate().translationX(0.0f).setDuration(250L).setStartDelay(0L).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
                    return true;
                }
            });
        }
        checkClearButton();
    }

    private Item putLazyItem(Item item) {
        if (item == null) {
            return item;
        }
        if (this.lazyList == null) {
            this.lazyList = new ArrayList();
        }
        this.lazyList.add(item);
        if (this.lazyMap == null) {
            this.lazyMap = new HashMap();
        }
        this.lazyMap.put(Integer.valueOf(item.id), item);
        return item;
    }

    private void updateOrShowPopup(boolean z, boolean z2) {
        int i;
        int i2;
        ActionBarPopupWindow actionBarPopupWindow;
        int measuredWidth;
        int i3;
        int i4;
        View view;
        int left;
        int i5;
        ActionBarMenu actionBarMenu = this.parentMenu;
        if (actionBarMenu != null) {
            i = (-actionBarMenu.parentActionBar.getMeasuredHeight()) + this.parentMenu.getTop();
            i2 = this.parentMenu.getPaddingTop();
        } else {
            float scaleY = getScaleY();
            i = -((int) ((getMeasuredHeight() * scaleY) - ((this.subMenuOpenSide != 2 ? getTranslationY() : 0.0f) / scaleY)));
            i2 = this.additionalYOffset;
        }
        int i6 = i + i2 + this.yOffset;
        if (z) {
            this.popupLayout.scrollToTop();
        }
        View view2 = this.showSubMenuFrom;
        if (view2 == null) {
            view2 = this;
        }
        ActionBarMenu actionBarMenu2 = this.parentMenu;
        if (actionBarMenu2 != null) {
            view = actionBarMenu2.parentActionBar;
            if (this.subMenuOpenSide == 0) {
                if (z) {
                    this.popupWindow.showAsDropDown(view, (((view2.getLeft() + this.parentMenu.getLeft()) + view2.getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()) + ((int) getTranslationX()), i6);
                }
                if (z2) {
                    actionBarPopupWindow = this.popupWindow;
                    left = ((view2.getLeft() + this.parentMenu.getLeft()) + view2.getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth();
                    i5 = (int) getTranslationX();
                    measuredWidth = left + i5;
                    i3 = -1;
                    i4 = -1;
                    actionBarPopupWindow.update(view, measuredWidth, i6, i3, i4);
                }
                return;
            }
            if (z) {
                if (this.forceSmoothKeyboard) {
                    this.popupWindow.showAtLocation(view, 51, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), i6);
                } else {
                    this.popupWindow.showAsDropDown(view, (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX()), i6);
                }
            }
            if (z2) {
                ActionBarPopupWindow actionBarPopupWindow2 = this.popupWindow;
                measuredWidth = (getLeft() - AndroidUtilities.dp(8.0f)) + ((int) getTranslationX());
                i3 = -1;
                i4 = -1;
                actionBarPopupWindow = actionBarPopupWindow2;
                actionBarPopupWindow.update(view, measuredWidth, i6, i3, i4);
            }
            return;
        }
        int i7 = this.subMenuOpenSide;
        if (i7 == 0) {
            if (getParent() != null) {
                view = (View) getParent();
                if (z) {
                    this.popupWindow.showAsDropDown(view, ((getLeft() + getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth()) + this.additionalXOffset, i6);
                }
                if (z2) {
                    actionBarPopupWindow = this.popupWindow;
                    left = (getLeft() + getMeasuredWidth()) - this.popupWindow.getContentView().getMeasuredWidth();
                    i5 = this.additionalXOffset;
                    measuredWidth = left + i5;
                    i3 = -1;
                    i4 = -1;
                    actionBarPopupWindow.update(view, measuredWidth, i6, i3, i4);
                }
                return;
            }
            return;
        }
        if (i7 == 1) {
            if (z) {
                this.popupWindow.showAsDropDown(this, (-AndroidUtilities.dp(8.0f)) + this.additionalXOffset, i6);
            }
            if (!z2) {
                return;
            }
            ActionBarPopupWindow actionBarPopupWindow3 = this.popupWindow;
            measuredWidth = (-AndroidUtilities.dp(8.0f)) + this.additionalXOffset;
            i3 = -1;
            i4 = -1;
            actionBarPopupWindow = actionBarPopupWindow3;
        } else {
            if (z) {
                this.popupWindow.showAsDropDown(this, (getMeasuredWidth() - this.popupWindow.getContentView().getMeasuredWidth()) + this.additionalXOffset, i6);
            }
            if (!z2) {
                return;
            }
            actionBarPopupWindow = this.popupWindow;
            measuredWidth = (getMeasuredWidth() - this.popupWindow.getContentView().getMeasuredWidth()) + this.additionalXOffset;
            i3 = -1;
            i4 = -1;
        }
        view = this;
        actionBarPopupWindow.update(view, measuredWidth, i6, i3, i4);
    }

    public ActionBarPopupWindow.GapView addColoredGap() {
        return addColoredGap(-1);
    }

    public ActionBarPopupWindow.GapView addColoredGap(int i) {
        createPopupLayout();
        ActionBarPopupWindow.GapView gapView = new ActionBarPopupWindow.GapView(getContext(), this.resourcesProvider, Theme.key_actionBarDefaultSubmenuSeparator);
        if (i != -1) {
            gapView.setTag(Integer.valueOf(i));
        }
        gapView.setTag(R.id.fit_width_tag, 1);
        this.popupLayout.addView((View) gapView, LayoutHelper.createLinear(-1, 8));
        return gapView;
    }

    public View addDivider(int i) {
        createPopupLayout();
        TextView textView = new TextView(getContext());
        textView.setBackgroundColor(i);
        textView.setMinimumWidth(AndroidUtilities.dp(196.0f));
        this.popupLayout.addView(textView);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
        layoutParams.width = -1;
        layoutParams.height = 1;
        int dp = AndroidUtilities.dp(3.0f);
        layoutParams.bottomMargin = dp;
        layoutParams.topMargin = dp;
        textView.setLayoutParams(layoutParams);
        return textView;
    }

    public View addGap(int i) {
        createPopupLayout();
        View view = new View(getContext());
        view.setMinimumWidth(AndroidUtilities.dp(196.0f));
        view.setTag(Integer.valueOf(i));
        view.setTag(R.id.object_tag, 1);
        this.popupLayout.addView(view);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(6.0f);
        view.setLayoutParams(layoutParams);
        return view;
    }

    public void addSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
        this.currentSearchFilters.add(mediaFilterData);
        if (this.searchContainer.getTag() != null) {
            this.selectedFilterIndex = this.currentSearchFilters.size() - 1;
        }
        onFiltersChanged();
    }

    public View addSubItem(int i, View view) {
        createPopupLayout();
        view.setMinimumWidth(AndroidUtilities.dp(196.0f));
        view.setTag(Integer.valueOf(i));
        this.popupLayout.addView(view);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        view.setLayoutParams(layoutParams);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                ActionBarMenuItem.this.lambda$addSubItem$6(view2);
            }
        });
        return view;
    }

    public TextView addSubItem(int i, CharSequence charSequence) {
        createPopupLayout();
        TextView textView = new TextView(getContext());
        textView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
        textView.setBackgroundDrawable(Theme.getSelectorDrawable(false));
        textView.setGravity(!LocaleController.isRTL ? 16 : 21);
        textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        textView.setTextSize(1, 16.0f);
        textView.setMinWidth(AndroidUtilities.dp(196.0f));
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTag(Integer.valueOf(i));
        textView.setText(charSequence);
        this.popupLayout.addView(textView);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        textView.setLayoutParams(layoutParams);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ActionBarMenuItem.this.lambda$addSubItem$4(view);
            }
        });
        return textView;
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, Drawable drawable, CharSequence charSequence, boolean z, boolean z2) {
        return addSubItem(i, i2, drawable, charSequence, z, z2, this.resourcesProvider);
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, Drawable drawable, CharSequence charSequence, final boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider) {
        createPopupLayout();
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), z2, false, false, resourcesProvider);
        actionBarMenuSubItem.setTextAndIcon(charSequence, i2, drawable);
        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarMenuSubItem.setTag(Integer.valueOf(i));
        this.popupLayout.addView(actionBarMenuSubItem);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) actionBarMenuSubItem.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        actionBarMenuSubItem.setLayoutParams(layoutParams);
        actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ActionBarMenuItem.this.lambda$addSubItem$5(z, view);
            }
        });
        return actionBarMenuSubItem;
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, CharSequence charSequence) {
        return addSubItem(i, i2, null, charSequence, true, false);
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, CharSequence charSequence, Theme.ResourcesProvider resourcesProvider) {
        return addSubItem(i, i2, null, charSequence, true, false, resourcesProvider);
    }

    public ActionBarMenuSubItem addSubItem(int i, int i2, CharSequence charSequence, boolean z) {
        return addSubItem(i, i2, null, charSequence, true, z);
    }

    public void addSubItem(int i, View view, int i2, int i3) {
        createPopupLayout();
        view.setLayoutParams(new LinearLayout.LayoutParams(i2, i3));
        this.popupLayout.addView(view);
        view.setTag(Integer.valueOf(i));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                ActionBarMenuItem.this.lambda$addSubItem$3(view2);
            }
        });
        view.setBackgroundDrawable(Theme.getSelectorDrawable(false));
    }

    public void addSubItem(View view, int i, int i2) {
        createPopupLayout();
        this.popupLayout.addView(view, new LinearLayout.LayoutParams(i, i2));
    }

    public ActionBarMenuSubItem addSwipeBackItem(int i, Drawable drawable, String str, View view) {
        createPopupLayout();
        final ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), false, false, false, this.resourcesProvider);
        actionBarMenuSubItem.setTextAndIcon(str, i, drawable);
        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarMenuSubItem.setRightIcon(R.drawable.msg_arrowright);
        this.popupLayout.addView(actionBarMenuSubItem);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) actionBarMenuSubItem.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(48.0f);
        actionBarMenuSubItem.setLayoutParams(layoutParams);
        final int addViewToSwipeBack = this.popupLayout.addViewToSwipeBack(view);
        actionBarMenuSubItem.openSwipeBackLayout = new Runnable() {
            @Override
            public final void run() {
                ActionBarMenuItem.this.lambda$addSwipeBackItem$7(addViewToSwipeBack);
            }
        };
        actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                ActionBarMenuSubItem.this.openSwipeBack();
            }
        });
        this.popupLayout.swipeBackGravityRight = true;
        return actionBarMenuSubItem;
    }

    public void checkHideMenuItem() {
        int i = 0;
        int i2 = 0;
        while (true) {
            if (i2 >= this.popupLayout.getItemsCount()) {
                i = 8;
                break;
            } else if (this.popupLayout.getItemAt(i2).getVisibility() == 0) {
                break;
            } else {
                i2++;
            }
        }
        if (i != getVisibility()) {
            setVisibility(i);
        }
    }

    public void clearFocusOnSearchView() {
        this.searchField.clearFocus();
        AndroidUtilities.hideKeyboard(this.searchField);
    }

    public void clearSearchFilters() {
        int i = 0;
        while (i < this.currentSearchFilters.size()) {
            if (((FiltersView.MediaFilterData) this.currentSearchFilters.get(i)).removable) {
                this.currentSearchFilters.remove(i);
                i--;
            }
            i++;
        }
        onFiltersChanged();
    }

    public void clearSearchText() {
        this.searchFieldText = null;
        EditTextBoldCursor editTextBoldCursor = this.searchField;
        if (editTextBoldCursor == null) {
            return;
        }
        editTextBoldCursor.setText("");
    }

    public void closeSubMenu() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            return;
        }
        this.popupWindow.dismiss();
    }

    public void collapseSearchFilters() {
        this.selectedFilterIndex = -1;
        onFiltersChanged();
    }

    @Override
    public void draw(Canvas canvas) {
        if (this.fixBackground) {
            getBackground().draw(canvas);
        }
        super.draw(canvas);
    }

    public void forceUpdatePopupPosition() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            return;
        }
        this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x - AndroidUtilities.dp(40.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
        updateOrShowPopup(true, true);
    }

    public View getContentView() {
        RLottieImageView rLottieImageView = this.iconView;
        return rLottieImageView != null ? rLottieImageView : this.textView;
    }

    public RLottieImageView getIconView() {
        return this.iconView;
    }

    public View.OnClickListener getOnClickListener() {
        return this.onClickListener;
    }

    public ActionBarPopupWindow.ActionBarPopupWindowLayout getPopupLayout() {
        if (this.popupLayout == null) {
            createPopupLayout();
        }
        return this.popupLayout;
    }

    public ImageView getSearchClearButton() {
        return this.clearButton;
    }

    public FrameLayout getSearchContainer() {
        return this.searchContainer;
    }

    public EditTextBoldCursor getSearchField() {
        checkCreateSearchField();
        return this.searchField;
    }

    public TextView getTextView() {
        return this.textView;
    }

    public int getVisibleSubItemsCount() {
        int i = 0;
        for (int i2 = 0; i2 < this.popupLayout.getItemsCount(); i2++) {
            View itemAt = this.popupLayout.getItemAt(i2);
            if (itemAt != null && itemAt.getVisibility() == 0) {
                i++;
            }
        }
        return i;
    }

    public boolean hasSubItem(int i) {
        if (findLazyItem(i) != null) {
            return true;
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        return (actionBarPopupWindowLayout == null || actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(i)) == null) ? false : true;
    }

    public boolean hasSubMenu() {
        ArrayList arrayList;
        return (this.popupLayout == null && ((arrayList = this.lazyList) == null || arrayList.isEmpty())) ? false : true;
    }

    public void hideSubItem(int i) {
        View findViewWithTag;
        Item findLazyItem = findLazyItem(i);
        if (findLazyItem != null) {
            findLazyItem.setVisibility(8);
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null || (findViewWithTag = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(i))) == null || findViewWithTag.getVisibility() == 8) {
            return;
        }
        findViewWithTag.setVisibility(8);
        this.measurePopup = true;
    }

    public boolean isSearchField() {
        return this.isSearchField;
    }

    public boolean isSearchFieldVisible() {
        FrameLayout frameLayout = this.searchContainer;
        return frameLayout != null && frameLayout.getVisibility() == 0;
    }

    public boolean isSubItemVisible(int i) {
        View findViewWithTag;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        return (actionBarPopupWindowLayout == null || (findViewWithTag = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(i))) == null || findViewWithTag.getVisibility() != 0) ? false : true;
    }

    public boolean isSubMenuShowing() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        return actionBarPopupWindow != null && actionBarPopupWindow.isShowing();
    }

    public Item lazilyAddColoredGap() {
        return putLazyItem(Item.access$2900());
    }

    public Item lazilyAddSubItem(int i, int i2, Drawable drawable, CharSequence charSequence, boolean z, boolean z2) {
        return putLazyItem(Item.asSubItem(i, i2, drawable, charSequence, z, z2));
    }

    public Item lazilyAddSubItem(int i, int i2, CharSequence charSequence) {
        return lazilyAddSubItem(i, i2, null, charSequence, true, false);
    }

    public Item lazilyAddSubItem(int i, Drawable drawable, CharSequence charSequence) {
        return lazilyAddSubItem(i, 0, drawable, charSequence, true, false);
    }

    public Item lazilyAddSwipeBackItem(int i, Drawable drawable, String str, View view) {
        return putLazyItem(Item.asSwipeBackItem(i, drawable, str, view));
    }

    protected void onDismiss() {
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (this.iconView != null) {
            accessibilityNodeInfo.setClassName("android.widget.ImageButton");
        } else if (this.textView != null) {
            accessibilityNodeInfo.setClassName("android.widget.Button");
            if (TextUtils.isEmpty(accessibilityNodeInfo.getText())) {
                accessibilityNodeInfo.setText(this.textView.getText());
            }
        }
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            updateOrShowPopup(false, true);
        }
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener != null) {
            actionBarMenuItemSearchListener.onLayout(i, i2, i3, i4);
        }
    }

    public void onSearchPressed() {
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener = this.listener;
        if (actionBarMenuItemSearchListener != null) {
            actionBarMenuItemSearchListener.onSearchPressed(this.searchField);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        ActionBarPopupWindow actionBarPopupWindow2;
        ActionBarPopupWindow actionBarPopupWindow3;
        if (motionEvent.getActionMasked() == 0) {
            if (this.longClickEnabled && hasSubMenu() && ((actionBarPopupWindow3 = this.popupWindow) == null || !actionBarPopupWindow3.isShowing())) {
                Runnable runnable = new Runnable() {
                    @Override
                    public final void run() {
                        ActionBarMenuItem.this.lambda$onTouchEvent$0();
                    }
                };
                this.showMenuRunnable = runnable;
                AndroidUtilities.runOnUIThread(runnable, 200L);
            }
        } else if (motionEvent.getActionMasked() != 2) {
            ActionBarPopupWindow actionBarPopupWindow4 = this.popupWindow;
            if (actionBarPopupWindow4 != null && actionBarPopupWindow4.isShowing() && motionEvent.getActionMasked() == 1) {
                View view = this.selectedMenuView;
                if (view != null) {
                    view.setSelected(false);
                    ActionBarMenu actionBarMenu = this.parentMenu;
                    if (actionBarMenu != null) {
                        actionBarMenu.onItemClick(((Integer) this.selectedMenuView.getTag()).intValue());
                    } else {
                        ActionBarMenuItemDelegate actionBarMenuItemDelegate = this.delegate;
                        if (actionBarMenuItemDelegate != null) {
                            actionBarMenuItemDelegate.onItemClick(((Integer) this.selectedMenuView.getTag()).intValue());
                        }
                    }
                    this.popupWindow.dismiss(this.allowCloseAnimation);
                } else if (this.showSubmenuByMove) {
                    this.popupWindow.dismiss();
                }
            } else {
                View view2 = this.selectedMenuView;
                if (view2 != null) {
                    view2.setSelected(false);
                    this.selectedMenuView = null;
                }
            }
        } else if (this.showSubmenuByMove && hasSubMenu() && ((actionBarPopupWindow2 = this.popupWindow) == null || !actionBarPopupWindow2.isShowing())) {
            if (motionEvent.getY() > getHeight()) {
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                toggleSubMenu();
                return true;
            }
        } else if (this.showSubmenuByMove && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            getLocationOnScreen(this.location);
            float x = motionEvent.getX() + this.location[0];
            float y = motionEvent.getY();
            float f = y + r5[1];
            this.popupLayout.getLocationOnScreen(this.location);
            int[] iArr = this.location;
            float f2 = x - iArr[0];
            float f3 = f - iArr[1];
            this.selectedMenuView = null;
            for (int i = 0; i < this.popupLayout.getItemsCount(); i++) {
                View itemAt = this.popupLayout.getItemAt(i);
                itemAt.getHitRect(this.rect);
                Object tag = itemAt.getTag();
                if ((tag instanceof Integer) && ((Integer) tag).intValue() < 100) {
                    if (this.rect.contains((int) f2, (int) f3)) {
                        itemAt.setPressed(true);
                        itemAt.setSelected(true);
                        int i2 = Build.VERSION.SDK_INT;
                        if (i2 >= 21) {
                            if (i2 == 21 && itemAt.getBackground() != null) {
                                itemAt.getBackground().setVisible(true, false);
                            }
                            itemAt.drawableHotspotChanged(f2, f3 - itemAt.getTop());
                        }
                        this.selectedMenuView = itemAt;
                    } else {
                        itemAt.setPressed(false);
                        itemAt.setSelected(false);
                        if (Build.VERSION.SDK_INT == 21 && itemAt.getBackground() != null) {
                            itemAt.getBackground().setVisible(false, false);
                        }
                    }
                }
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    public void openSearch(boolean z) {
        ActionBarMenu actionBarMenu;
        checkCreateSearchField();
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout == null || frameLayout.getVisibility() == 0 || (actionBarMenu = this.parentMenu) == null) {
            return;
        }
        actionBarMenu.parentActionBar.onSearchFieldVisibilityChanged(toggleSearch(z));
    }

    public void redrawPopup(int i) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null || actionBarPopupWindowLayout.getBackgroundColor() == i) {
            return;
        }
        this.popupLayout.setBackgroundColor(i);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            return;
        }
        this.popupLayout.invalidate();
    }

    public void removeAllSubItems() {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null) {
            return;
        }
        actionBarPopupWindowLayout.removeInnerViews();
    }

    public void removeSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
        if (mediaFilterData.removable) {
            this.currentSearchFilters.remove(mediaFilterData);
            int i = this.selectedFilterIndex;
            if (i < 0 || i > this.currentSearchFilters.size() - 1) {
                this.selectedFilterIndex = this.currentSearchFilters.size() - 1;
            }
            onFiltersChanged();
            this.searchField.hideActionMode();
        }
    }

    public void requestFocusOnSearchView() {
        if (this.searchContainer.getWidth() == 0 || this.searchField.isFocused()) {
            return;
        }
        this.searchField.requestFocus();
        AndroidUtilities.showKeyboard(this.searchField);
    }

    public ActionBarMenuItem setActionBarMenuItemSearchListener(ActionBarMenuItemSearchListener actionBarMenuItemSearchListener) {
        this.listener = actionBarMenuItemSearchListener;
        return this;
    }

    public void setAdditionalXOffset(int i) {
        this.additionalXOffset = i;
    }

    public void setAdditionalYOffset(int i) {
        this.additionalYOffset = i;
    }

    public ActionBarMenuItem setAllowCloseAnimation(boolean z) {
        this.allowCloseAnimation = z;
        return this;
    }

    public void setDelegate(ActionBarMenuItemDelegate actionBarMenuItemDelegate) {
        this.delegate = actionBarMenuItemDelegate;
    }

    public void setDimMenu(float f) {
        this.dimMenu = f;
    }

    public void setFitSubItems(boolean z) {
        this.popupLayout.setFitItems(z);
    }

    public void setFixBackground(boolean z) {
        this.fixBackground = z;
        invalidate();
    }

    public void setForceSmoothKeyboard(boolean z) {
        this.forceSmoothKeyboard = z;
    }

    public void setIcon(int i) {
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView == null) {
            return;
        }
        this.iconViewResId = i;
        rLottieImageView.setImageResource(i);
    }

    public void setIcon(int i, boolean z) {
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView == null || this.iconViewResId == i) {
            return;
        }
        this.iconViewResId = i;
        if (z) {
            AndroidUtilities.updateImageViewImageAnimated(rLottieImageView, i);
        } else {
            rLottieImageView.setImageResource(i);
        }
    }

    public void setIcon(Drawable drawable) {
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView == null) {
            return;
        }
        if (drawable instanceof RLottieDrawable) {
            rLottieImageView.setAnimation((RLottieDrawable) drawable);
        } else {
            rLottieImageView.setImageDrawable(drawable);
        }
        this.iconViewResId = 0;
    }

    public void setIconColor(int i) {
        RLottieImageView rLottieImageView = this.iconView;
        if (rLottieImageView != null) {
            rLottieImageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        }
        TextView textView = this.textView;
        if (textView != null) {
            textView.setTextColor(i);
        }
        ImageView imageView = this.clearButton;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        }
    }

    public ActionBarMenuItem setIsSearchField(boolean z) {
        return setIsSearchField(z, false);
    }

    public ActionBarMenuItem setIsSearchField(boolean z, boolean z2) {
        if (this.parentMenu == null) {
            return this;
        }
        this.isSearchField = z;
        this.wrapSearchInScrollView = z2;
        return this;
    }

    public void setLayoutInScreen(boolean z) {
        this.layoutInScreen = z;
    }

    public void setLongClickEnabled(boolean z) {
        this.longClickEnabled = z;
    }

    public void setMenuYOffset(int i) {
        this.yOffset = i;
    }

    @Override
    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
        super.setOnClickListener(onClickListener);
    }

    public void setOnMenuDismiss(final Utilities.Callback<Boolean> callback) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public final void onDismiss() {
                    ActionBarMenuItem.this.lambda$setOnMenuDismiss$11(callback);
                }
            });
        }
    }

    public ActionBarMenuItem setOverrideMenuClick(boolean z) {
        this.overrideMenuClick = z;
        return this;
    }

    public void setPopupAnimationEnabled(boolean z) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.setAnimationEnabled(z);
        }
        this.animationEnabled = z;
    }

    public void setPopupItemsColor(int i, boolean z) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null) {
            return;
        }
        LinearLayout linearLayout = actionBarPopupWindowLayout.linearLayout;
        int childCount = linearLayout.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = linearLayout.getChildAt(i2);
            if (childAt instanceof TextView) {
                ((TextView) childAt).setTextColor(i);
            } else if (childAt instanceof ActionBarMenuSubItem) {
                ActionBarMenuSubItem actionBarMenuSubItem = (ActionBarMenuSubItem) childAt;
                if (z) {
                    actionBarMenuSubItem.setIconColor(i);
                } else {
                    actionBarMenuSubItem.setTextColor(i);
                }
            }
        }
    }

    public void setPopupItemsSelectorColor(int i) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null) {
            return;
        }
        LinearLayout linearLayout = actionBarPopupWindowLayout.linearLayout;
        int childCount = linearLayout.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = linearLayout.getChildAt(i2);
            if (childAt instanceof ActionBarMenuSubItem) {
                ((ActionBarMenuSubItem) childAt).setSelectorColor(i);
            }
        }
    }

    public void setSearchAdditionalButton(View view) {
        this.searchAdditionalButton = view;
    }

    public void setSearchFieldCaption(CharSequence charSequence) {
        if (this.searchFieldCaption == null) {
            return;
        }
        if (TextUtils.isEmpty(charSequence)) {
            this.searchFieldCaption.setVisibility(8);
        } else {
            this.searchFieldCaption.setVisibility(0);
            this.searchFieldCaption.setText(charSequence);
        }
    }

    public void setSearchFieldHint(CharSequence charSequence) {
        this.searchFieldHint = charSequence;
        if (this.searchFieldCaption == null) {
            return;
        }
        this.searchField.setHint(charSequence);
        setContentDescription(charSequence);
    }

    public void setSearchFieldText(CharSequence charSequence, boolean z) {
        this.searchFieldText = charSequence;
        if (this.searchFieldCaption == null) {
            return;
        }
        this.animateClear = z;
        this.searchField.setText(charSequence);
        if (TextUtils.isEmpty(charSequence)) {
            return;
        }
        this.searchField.setSelection(charSequence.length());
    }

    public void setSearchPaddingStart(int i) {
        this.searchItemPaddingStart = i;
        FrameLayout frameLayout = this.searchContainer;
        if (frameLayout != null) {
            ((ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams()).leftMargin = AndroidUtilities.dp(i);
            this.searchContainer.setClipChildren(this.searchItemPaddingStart != 0);
            FrameLayout frameLayout2 = this.searchContainer;
            frameLayout2.setLayoutParams(frameLayout2.getLayoutParams());
        }
    }

    public void setShowSearchProgress(boolean z) {
        CloseProgressDrawable2 closeProgressDrawable2 = this.progressDrawable;
        if (closeProgressDrawable2 == null) {
            return;
        }
        if (z) {
            closeProgressDrawable2.startAnimation();
        } else {
            closeProgressDrawable2.stopAnimation();
        }
    }

    public void setShowSubmenuByMove(boolean z) {
        this.showSubmenuByMove = z;
    }

    public void setShowedFromBottom(boolean z) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null) {
            return;
        }
        actionBarPopupWindowLayout.setShownFromBottom(z);
    }

    public void setSubItemShown(int i, boolean z) {
        if (z) {
            showSubItem(i);
        } else {
            hideSubItem(i);
        }
    }

    public void setSubMenuDelegate(ActionBarSubMenuItemDelegate actionBarSubMenuItemDelegate) {
        this.subMenuDelegate = actionBarSubMenuItemDelegate;
    }

    public void setSubMenuOpenSide(int i) {
        this.subMenuOpenSide = i;
    }

    public void setText(CharSequence charSequence) {
        TextView textView = this.textView;
        if (textView == null) {
            return;
        }
        textView.setText(charSequence);
    }

    public void setTransitionOffset(float f) {
        this.transitionOffset = f;
        setTranslationX(0.0f);
    }

    @Override
    public void setTranslationX(float f) {
        super.setTranslationX(f + this.transitionOffset);
    }

    public void setupPopupRadialSelectors(int i) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout != null) {
            actionBarPopupWindowLayout.setupRadialSelectors(i);
        }
    }

    public void showSubItem(int i) {
        showSubItem(i, false);
    }

    public void showSubItem(int i, boolean z) {
        View findViewWithTag;
        Item findLazyItem = findLazyItem(i);
        if (findLazyItem != null) {
            findLazyItem.setVisibility(0);
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = this.popupLayout;
        if (actionBarPopupWindowLayout == null || (findViewWithTag = actionBarPopupWindowLayout.findViewWithTag(Integer.valueOf(i))) == null || findViewWithTag.getVisibility() == 0) {
            return;
        }
        findViewWithTag.setAlpha(0.0f);
        findViewWithTag.animate().alpha(1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).setDuration(150L).start();
        findViewWithTag.setVisibility(0);
        this.measurePopup = true;
    }

    public boolean toggleSearch(boolean z) {
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener;
        RLottieImageView iconView;
        Animator customToggleTransition;
        checkCreateSearchField();
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener2 = this.listener;
        if (actionBarMenuItemSearchListener2 != null) {
            actionBarMenuItemSearchListener2.onPreToggleSearch();
        }
        if (this.searchContainer == null || !((actionBarMenuItemSearchListener = this.listener) == null || actionBarMenuItemSearchListener.canToggleSearch())) {
            return false;
        }
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener3 = this.listener;
        if (actionBarMenuItemSearchListener3 != null && (customToggleTransition = actionBarMenuItemSearchListener3.getCustomToggleTransition()) != null) {
            customToggleTransition.start();
            return true;
        }
        final ArrayList arrayList = new ArrayList();
        for (int i = 0; i < this.parentMenu.getChildCount(); i++) {
            View childAt = this.parentMenu.getChildAt(i);
            if ((childAt instanceof ActionBarMenuItem) && (iconView = ((ActionBarMenuItem) childAt).getIconView()) != null) {
                arrayList.add(iconView);
            }
        }
        if (this.searchContainer.getTag() == null) {
            this.searchContainer.setVisibility(0);
            this.searchContainer.setAlpha(0.0f);
            AnimatorSet animatorSet = this.searchContainerAnimator;
            if (animatorSet != null) {
                animatorSet.removeAllListeners();
                this.searchContainerAnimator.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.searchContainerAnimator = animatorSet2;
            FrameLayout frameLayout = this.searchContainer;
            animatorSet2.playTogether(ObjectAnimator.ofFloat(frameLayout, (Property<FrameLayout, Float>) View.ALPHA, frameLayout.getAlpha(), 1.0f));
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                this.searchContainerAnimator.playTogether(ObjectAnimator.ofFloat((View) arrayList.get(i2), (Property<View, Float>) View.ALPHA, ((View) arrayList.get(i2)).getAlpha(), 0.0f));
            }
            this.searchContainerAnimator.setDuration(150L);
            this.searchContainerAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ActionBarMenuItem.this.searchContainer.setAlpha(1.0f);
                    for (int i3 = 0; i3 < arrayList.size(); i3++) {
                        ((View) arrayList.get(i3)).setAlpha(0.0f);
                    }
                }
            });
            this.searchContainerAnimator.start();
            setVisibility(8);
            clearSearchFilters();
            this.searchField.setText("");
            this.searchField.requestFocus();
            if (z) {
                AndroidUtilities.showKeyboard(this.searchField);
            }
            ActionBarMenuItemSearchListener actionBarMenuItemSearchListener4 = this.listener;
            if (actionBarMenuItemSearchListener4 != null) {
                actionBarMenuItemSearchListener4.onSearchExpand();
            }
            this.searchContainer.setTag(1);
            return true;
        }
        this.searchContainer.setTag(null);
        AnimatorSet animatorSet3 = this.searchContainerAnimator;
        if (animatorSet3 != null) {
            animatorSet3.removeAllListeners();
            this.searchContainerAnimator.cancel();
        }
        AnimatorSet animatorSet4 = new AnimatorSet();
        this.searchContainerAnimator = animatorSet4;
        FrameLayout frameLayout2 = this.searchContainer;
        animatorSet4.playTogether(ObjectAnimator.ofFloat(frameLayout2, (Property<FrameLayout, Float>) View.ALPHA, frameLayout2.getAlpha(), 0.0f));
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            ((View) arrayList.get(i3)).setAlpha(0.0f);
            this.searchContainerAnimator.playTogether(ObjectAnimator.ofFloat((View) arrayList.get(i3), (Property<View, Float>) View.ALPHA, ((View) arrayList.get(i3)).getAlpha(), 1.0f));
        }
        this.searchContainerAnimator.setDuration(150L);
        this.searchContainerAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                ActionBarMenuItem.this.searchContainer.setAlpha(0.0f);
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    ((View) arrayList.get(i4)).setAlpha(1.0f);
                }
                ActionBarMenuItem.this.searchContainer.setVisibility(8);
            }
        });
        this.searchContainerAnimator.start();
        this.searchField.clearFocus();
        setVisibility(0);
        if (!this.currentSearchFilters.isEmpty() && this.listener != null) {
            for (int i4 = 0; i4 < this.currentSearchFilters.size(); i4++) {
                if (((FiltersView.MediaFilterData) this.currentSearchFilters.get(i4)).removable) {
                    this.listener.onSearchFilterCleared((FiltersView.MediaFilterData) this.currentSearchFilters.get(i4));
                }
            }
        }
        ActionBarMenuItemSearchListener actionBarMenuItemSearchListener5 = this.listener;
        if (actionBarMenuItemSearchListener5 != null) {
            actionBarMenuItemSearchListener5.onSearchCollapse();
        }
        if (z) {
            AndroidUtilities.hideKeyboard(this.searchField);
        }
        this.parentMenu.requestLayout();
        requestLayout();
        return false;
    }

    public void toggleSubMenu() {
        toggleSubMenu(null, null);
    }

    public void toggleSubMenu(final View view, View view2) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout;
        View childAt;
        ActionBar actionBar;
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            layoutLazyItems();
        }
        if (this.popupLayout != null) {
            ActionBarMenu actionBarMenu = this.parentMenu;
            if (actionBarMenu == null || !actionBarMenu.isActionMode || (actionBar = actionBarMenu.parentActionBar) == null || actionBar.isActionModeShowed()) {
                Runnable runnable = this.showMenuRunnable;
                FrameLayout frameLayout = null;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.showMenuRunnable = null;
                }
                ActionBarPopupWindow actionBarPopupWindow2 = this.popupWindow;
                if (actionBarPopupWindow2 != null && actionBarPopupWindow2.isShowing()) {
                    this.popupWindow.dismiss();
                    return;
                }
                this.showSubMenuFrom = view2;
                ActionBarSubMenuItemDelegate actionBarSubMenuItemDelegate = this.subMenuDelegate;
                if (actionBarSubMenuItemDelegate != null) {
                    actionBarSubMenuItemDelegate.onShowSubMenu();
                }
                if (this.popupLayout.getParent() != null) {
                    ((ViewGroup) this.popupLayout.getParent()).removeView(this.popupLayout);
                }
                ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout2 = this.popupLayout;
                if (view != null) {
                    LinearLayout linearLayout = new LinearLayout(getContext()) {
                        @Override
                        protected void onMeasure(int i, int i2) {
                            ViewGroup.LayoutParams layoutParams;
                            int measuredWidth;
                            ActionBarMenuItem.this.popupLayout.measure(i, i2);
                            if (ActionBarMenuItem.this.popupLayout.getSwipeBack() != null) {
                                layoutParams = view.getLayoutParams();
                                measuredWidth = ActionBarMenuItem.this.popupLayout.getSwipeBack().getChildAt(0).getMeasuredWidth();
                            } else {
                                layoutParams = view.getLayoutParams();
                                measuredWidth = ActionBarMenuItem.this.popupLayout.getMeasuredWidth() - AndroidUtilities.dp(16.0f);
                            }
                            layoutParams.width = measuredWidth;
                            super.onMeasure(i, i2);
                        }
                    };
                    linearLayout.setOrientation(1);
                    frameLayout = new FrameLayout(getContext());
                    frameLayout.setAlpha(0.0f);
                    frameLayout.animate().alpha(1.0f).setDuration(100L).setStartDelay(this.popupLayout.shownFromBottom ? 165L : 0L).start();
                    if (view.getParent() instanceof ViewGroup) {
                        ((ViewGroup) view.getParent()).removeView(view);
                    }
                    if ((view instanceof ActionBarMenuSubItem) || (view instanceof LinearLayout)) {
                        Drawable mutate = ContextCompat.getDrawable(getContext(), R.drawable.popup_fixed_alert2).mutate();
                        mutate.setColorFilter(new PorterDuffColorFilter(this.popupLayout.getBackgroundColor(), PorterDuff.Mode.MULTIPLY));
                        frameLayout.setBackground(mutate);
                    }
                    frameLayout.addView(view, LayoutHelper.createFrame(-1, -2.0f));
                    linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
                    linearLayout.addView(this.popupLayout, LayoutHelper.createLinear(-2, -2, 0, 0, -10, 0, 0));
                    this.popupLayout.setTopView(frameLayout);
                    actionBarPopupWindowLayout = linearLayout;
                } else {
                    actionBarPopupWindowLayout2.setTopView(null);
                    actionBarPopupWindowLayout = actionBarPopupWindowLayout2;
                }
                ActionBarPopupWindow actionBarPopupWindow3 = new ActionBarPopupWindow(actionBarPopupWindowLayout, -2, -2);
                this.popupWindow = actionBarPopupWindow3;
                if (this.animationEnabled) {
                    actionBarPopupWindow3.setAnimationStyle(0);
                } else {
                    actionBarPopupWindow3.setAnimationStyle(R.style.PopupAnimation);
                }
                boolean z = this.animationEnabled;
                if (!z) {
                    this.popupWindow.setAnimationEnabled(z);
                }
                this.popupWindow.setOutsideTouchable(true);
                this.popupWindow.setClippingEnabled(true);
                if (this.layoutInScreen) {
                    this.popupWindow.setLayoutInScreen(true);
                }
                this.popupWindow.setInputMethodMode(2);
                this.popupWindow.setSoftInputMode(0);
                actionBarPopupWindowLayout.setFocusableInTouchMode(true);
                actionBarPopupWindowLayout.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public final boolean onKey(View view3, int i, KeyEvent keyEvent) {
                        boolean lambda$toggleSubMenu$9;
                        lambda$toggleSubMenu$9 = ActionBarMenuItem.this.lambda$toggleSubMenu$9(view3, i, keyEvent);
                        return lambda$toggleSubMenu$9;
                    }
                });
                this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public final void onDismiss() {
                        ActionBarMenuItem.this.lambda$toggleSubMenu$10();
                    }
                });
                actionBarPopupWindowLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.x - AndroidUtilities.dp(40.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.displaySize.y, Integer.MIN_VALUE));
                if (frameLayout != null && frameLayout.getLayoutParams() != null && this.popupLayout.getSwipeBack() != null && (childAt = this.popupLayout.getSwipeBack().getChildAt(0)) != null && childAt.getMeasuredWidth() > 0) {
                    frameLayout.getLayoutParams().width = childAt.getMeasuredWidth() + AndroidUtilities.dp(16.0f);
                }
                this.measurePopup = false;
                this.processedPopupClick = false;
                this.popupWindow.setFocusable(true);
                updateOrShowPopup(true, actionBarPopupWindowLayout.getMeasuredWidth() == 0);
                this.popupLayout.updateRadialSelectors();
                if (this.popupLayout.getSwipeBack() != null) {
                    this.popupLayout.getSwipeBack().closeForeground(false);
                }
                this.popupWindow.startAnimation();
                float f = this.dimMenu;
                if (f > 0.0f) {
                    this.popupWindow.dimBehind(f);
                }
            }
        }
    }

    public void updateColor() {
        if (this.searchFilterLayout != null) {
            for (int i = 0; i < this.searchFilterLayout.getChildCount(); i++) {
                if (this.searchFilterLayout.getChildAt(i) instanceof SearchFilterView) {
                    ((SearchFilterView) this.searchFilterLayout.getChildAt(i)).updateColors();
                }
            }
        }
        if (this.popupLayout != null) {
            for (int i2 = 0; i2 < this.popupLayout.getItemsCount(); i2++) {
                if (this.popupLayout.getItemAt(i2) instanceof ActionBarMenuSubItem) {
                    ((ActionBarMenuSubItem) this.popupLayout.getItemAt(i2)).setSelectorColor(getThemedColor(Theme.key_dialogButtonSelector));
                }
            }
        }
        EditTextBoldCursor editTextBoldCursor = this.searchField;
        if (editTextBoldCursor != null) {
            int i3 = Theme.key_actionBarDefaultSearch;
            editTextBoldCursor.setCursorColor(getThemedColor(i3));
            this.searchField.setHintTextColor(getThemedColor(Theme.key_actionBarDefaultSearchPlaceholder));
            this.searchField.setTextColor(getThemedColor(i3));
            this.searchField.setHighlightColor(getThemedColor(Theme.key_chat_inTextSelectionHighlight));
            this.searchField.setHandlesColor(getThemedColor(Theme.key_chat_TextSelectionCursor));
        }
    }
}
