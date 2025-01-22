package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.arch.core.util.Function;
import androidx.collection.LongSparseArray;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.SearchAdapterHelper;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.Cells.ShareTopicCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.Stories.DarkThemeResourceProvider;

public class ShareAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private AnimatorSet animatorSet;
    private FrameLayout bulletinContainer;
    public FrameLayout bulletinContainer2;
    private float captionEditTextTopOffset;
    private float chatActivityEnterViewAnimateFromTop;
    private EditTextEmoji commentTextView;
    private int containerViewTop;
    private boolean copyLinkOnEnd;
    private float currentPanTranslationY;
    private boolean darkTheme;
    private ShareAlertDelegate delegate;
    private TLRPC.TL_exportedMessageLink exportedMessageLink;
    public boolean forceDarkThemeForHint;
    private FrameLayout frameLayout;
    private BlurredFrameLayout frameLayout2;
    private boolean fullyShown;
    private RecyclerListView gridView;
    private int hasPoll;
    private boolean includeStory;
    public boolean includeStoryFromMessage;
    private boolean isChannel;
    private int keyboardSize2;
    private float keyboardT;
    int lastOffset;
    private GridLayoutManager layoutManager;
    private LinearLayout linkContainer;
    private TextView linkCopyButton;
    private SimpleTextView linkTextView;
    private String[] linkToCopy;
    private ShareDialogsAdapter listAdapter;
    private boolean loadingLink;
    public BlurredFrameLayout navigationBar;
    private Paint paint;
    private boolean panTranslationMoveLayout;
    private Activity parentActivity;
    private ChatActivity parentFragment;
    private FrameLayout pickerBottom;
    private BlurredFrameLayout pickerBottomLayout;
    private int previousScrollOffsetY;
    private ArrayList recentSearchObjects;
    private LongSparseArray recentSearchObjectsById;
    private RectF rect;
    RecyclerItemsEnterAnimator recyclerItemsEnterAnimator;
    private int scrollOffsetY;
    private ShareSearchAdapter searchAdapter;
    private StickerEmptyView searchEmptyView;
    private RecyclerListView searchGridView;
    private boolean searchIsVisible;
    private FillLastGridLayoutManager searchLayoutManager;
    SearchField searchView;
    private boolean searchWasVisibleBeforeTopics;
    private View selectedCountView;
    protected Map selectedDialogTopics;
    protected LongSparseArray selectedDialogs;
    private TLRPC.Dialog selectedTopicDialog;
    private ActionBarPopupWindow sendPopupWindow;
    protected ArrayList sendingMessageObjects;
    private String[] sendingText;
    private View[] shadow;
    private AnimatorSet[] shadowAnimation;
    private Drawable shadowDrawable;
    private ShareTopicsAdapter shareTopicsAdapter;
    private LinearLayout sharesCountLayout;
    private int shiftDp;
    private boolean showSendersName;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    TL_stories.StoryItem storyItem;
    private SwitchView switchView;
    private TextPaint textPaint;
    public int timestamp;
    public CheckBox2 timestampCheckbox;
    public BlurredFrameLayout timestampFrameLayout;
    public LinearLayout timestampLayout;
    public TextView timestampTextView;
    private ValueAnimator topBackgroundAnimator;
    private int topBeforeSwitch;
    private SpringAnimation topicsAnimation;
    ActionBar topicsBackActionBar;
    private RecyclerListView topicsGridView;
    private GridLayoutManager topicsLayoutManager;
    private boolean updateSearchAdapter;
    private FrameLayout writeButtonContainer;

    public class AnonymousClass1 extends DarkThemeResourceProvider {
        AnonymousClass1() {
        }

        @Override
        public void appendColors() {
            SparseIntArray sparseIntArray = this.sparseIntArray;
            int i = Theme.key_windowBackgroundGray;
            int i2 = Theme.key_dialogBackground;
            sparseIntArray.put(i, getColor(i2));
            this.sparseIntArray.put(Theme.key_divider, -15264235);
            this.sparseIntArray.put(Theme.key_chat_messagePanelIcons, Theme.multAlpha(-1, 0.45f));
            this.sparseIntArray.put(Theme.key_dialogBackgroundGray, 352321535);
            this.sparseIntArray.put(Theme.key_chat_emojiPanelBackground, getColor(i2));
        }
    }

    public class AnonymousClass10 extends GridLayoutManager.SpanSizeLookup {
        AnonymousClass10() {
        }

        @Override
        public int getSpanSize(int i) {
            if (i == 0) {
                return ShareAlert.this.layoutManager.getSpanCount();
            }
            return 1;
        }
    }

    public class AnonymousClass11 extends RecyclerView.ItemDecoration {
        AnonymousClass11() {
        }

        @Override
        public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            RecyclerListView.Holder holder = (RecyclerListView.Holder) recyclerView.getChildViewHolder(view);
            if (holder == null) {
                rect.left = AndroidUtilities.dp(4.0f);
                rect.right = AndroidUtilities.dp(4.0f);
            } else {
                int adapterPosition = holder.getAdapterPosition() % 4;
                rect.left = adapterPosition == 0 ? 0 : AndroidUtilities.dp(4.0f);
                rect.right = adapterPosition != 3 ? AndroidUtilities.dp(4.0f) : 0;
            }
        }
    }

    public class AnonymousClass12 extends RecyclerView.OnScrollListener {
        AnonymousClass12() {
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (i2 != 0) {
                ShareAlert.this.updateLayout();
                ShareAlert shareAlert = ShareAlert.this;
                shareAlert.previousScrollOffsetY = shareAlert.scrollOffsetY;
            }
            if (Bulletin.getVisibleBulletin() == null || Bulletin.getVisibleBulletin().getLayout() == null || !(Bulletin.getVisibleBulletin().getLayout().getParent() instanceof View) || ((View) Bulletin.getVisibleBulletin().getLayout().getParent()).getParent() != ShareAlert.this.bulletinContainer2) {
                return;
            }
            Bulletin.hideVisible();
        }
    }

    public class AnonymousClass13 extends RecyclerListView {
        AnonymousClass13(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
        }

        @Override
        protected boolean allowSelectChildAtPosition(float f, float f2) {
            return f2 >= ((float) (AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 58.0f : 111.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
        }

        @Override
        public void draw(Canvas canvas) {
            if (ShareAlert.this.topicsGridView.getVisibility() != 8) {
                canvas.save();
                canvas.clipRect(0, ShareAlert.this.scrollOffsetY + AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 58.0f : 111.0f), getWidth(), getHeight());
            }
            super.draw(canvas);
            if (ShareAlert.this.topicsGridView.getVisibility() != 8) {
                canvas.restore();
            }
        }
    }

    public class AnonymousClass14 extends GridLayoutManager.SpanSizeLookup {
        AnonymousClass14() {
        }

        @Override
        public int getSpanSize(int i) {
            return ShareAlert.this.searchAdapter.getSpanSize(4, i);
        }
    }

    public class AnonymousClass15 extends RecyclerView.OnScrollListener {
        AnonymousClass15() {
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (i2 != 0) {
                ShareAlert.this.updateLayout();
                ShareAlert shareAlert = ShareAlert.this;
                shareAlert.previousScrollOffsetY = shareAlert.scrollOffsetY;
            }
        }
    }

    public class AnonymousClass16 extends RecyclerView.ItemDecoration {
        AnonymousClass16() {
        }

        @Override
        public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            RecyclerListView.Holder holder = (RecyclerListView.Holder) recyclerView.getChildViewHolder(view);
            if (holder == null) {
                rect.left = AndroidUtilities.dp(4.0f);
                rect.right = AndroidUtilities.dp(4.0f);
                return;
            }
            if (holder.getItemViewType() != 5) {
                rect.right = 0;
                rect.left = 0;
            } else {
                int adapterPosition = holder.getAdapterPosition() % 4;
                rect.left = adapterPosition == 0 ? 0 : AndroidUtilities.dp(4.0f);
                rect.right = adapterPosition != 3 ? AndroidUtilities.dp(4.0f) : 0;
            }
        }
    }

    public class AnonymousClass17 extends BlurredFrameLayout {
        private final Paint dividerPaint;
        private final Paint p;

        AnonymousClass17(Context context, SizeNotifierFrameLayout sizeNotifierFrameLayout) {
            super(context, sizeNotifierFrameLayout);
            this.p = new Paint();
            this.dividerPaint = new Paint(1);
        }

        public void lambda$onDraw$0(ValueAnimator valueAnimator) {
            ShareAlert.this.captionEditTextTopOffset = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            ShareAlert.this.frameLayout2.invalidate();
            invalidate();
        }

        @Override
        public void dispatchDraw(Canvas canvas) {
            canvas.save();
            canvas.clipRect(0.0f, ShareAlert.this.captionEditTextTopOffset, getMeasuredWidth(), getMeasuredHeight());
            this.dividerPaint.setColor(ShareAlert.this.getThemedColor(Theme.key_divider));
            canvas.drawRect(0.0f, ShareAlert.this.captionEditTextTopOffset, getMeasuredWidth(), AndroidUtilities.dpf2(0.66f), this.dividerPaint);
            super.dispatchDraw(canvas);
            canvas.restore();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (ShareAlert.this.chatActivityEnterViewAnimateFromTop != 0.0f && ShareAlert.this.chatActivityEnterViewAnimateFromTop != ShareAlert.this.frameLayout2.getTop() + ShareAlert.this.chatActivityEnterViewAnimateFromTop) {
                if (ShareAlert.this.topBackgroundAnimator != null) {
                    ShareAlert.this.topBackgroundAnimator.cancel();
                }
                ShareAlert shareAlert = ShareAlert.this;
                shareAlert.captionEditTextTopOffset = shareAlert.chatActivityEnterViewAnimateFromTop - (ShareAlert.this.frameLayout2.getTop() + ShareAlert.this.captionEditTextTopOffset);
                ShareAlert shareAlert2 = ShareAlert.this;
                shareAlert2.topBackgroundAnimator = ValueAnimator.ofFloat(shareAlert2.captionEditTextTopOffset, 0.0f);
                ShareAlert.this.topBackgroundAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ShareAlert.AnonymousClass17.this.lambda$onDraw$0(valueAnimator);
                    }
                });
                ShareAlert.this.topBackgroundAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
                ShareAlert.this.topBackgroundAnimator.setDuration(200L);
                ShareAlert.this.topBackgroundAnimator.start();
                ShareAlert.this.chatActivityEnterViewAnimateFromTop = 0.0f;
            }
            ShareAlert.this.shadow[1].setTranslationY((-(ShareAlert.this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(48.0f))) + ShareAlert.this.captionEditTextTopOffset + ShareAlert.this.currentPanTranslationY + ((ShareAlert.this.frameLayout2.getMeasuredHeight() - AndroidUtilities.dp(48.0f)) * (1.0f - getAlpha())));
        }

        @Override
        public void setAlpha(float f) {
            super.setAlpha(f);
            invalidate();
        }

        @Override
        public void setVisibility(int i) {
            super.setVisibility(i);
            if (i != 0) {
                ShareAlert.this.shadow[1].setTranslationY(0.0f);
            }
        }
    }

    public class AnonymousClass18 extends EditTextEmoji {
        private ValueAnimator messageEditTextAnimator;
        private int messageEditTextPredrawHeigth;
        private int messageEditTextPredrawScrollY;
        private boolean shouldAnimateEditTextWithBounds;

        AnonymousClass18(Context context, SizeNotifierFrameLayout sizeNotifierFrameLayout, BaseFragment baseFragment, int i, boolean z, Theme.ResourcesProvider resourcesProvider) {
            super(context, sizeNotifierFrameLayout, baseFragment, i, z, resourcesProvider);
        }

        public static void lambda$dispatchDraw$0(EditTextCaption editTextCaption, ValueAnimator valueAnimator) {
            editTextCaption.setOffsetY(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }

        @Override
        public void bottomPanelTranslationY(float f) {
            super.bottomPanelTranslationY(f);
            ShareAlert.this.updateBottomOverlay();
        }

        @Override
        public void createEmojiView() {
            super.createEmojiView();
            BlurredFrameLayout blurredFrameLayout = ShareAlert.this.navigationBar;
            if (blurredFrameLayout != null) {
                blurredFrameLayout.bringToFront();
            }
            BlurredFrameLayout blurredFrameLayout2 = ShareAlert.this.timestampFrameLayout;
            if (blurredFrameLayout2 != null) {
                blurredFrameLayout2.bringToFront();
            }
            if (ShareAlert.this.frameLayout2 != null) {
                ShareAlert.this.frameLayout2.bringToFront();
            }
            if (ShareAlert.this.writeButtonContainer != null) {
                ShareAlert.this.writeButtonContainer.bringToFront();
            }
            if (ShareAlert.this.selectedCountView != null) {
                ShareAlert.this.selectedCountView.bringToFront();
            }
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            if (this.shouldAnimateEditTextWithBounds) {
                final EditTextCaption editText = ShareAlert.this.commentTextView.getEditText();
                editText.setOffsetY(editText.getOffsetY() - ((this.messageEditTextPredrawHeigth - editText.getMeasuredHeight()) + (this.messageEditTextPredrawScrollY - editText.getScrollY())));
                ValueAnimator ofFloat = ValueAnimator.ofFloat(editText.getOffsetY(), 0.0f);
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        ShareAlert.AnonymousClass18.lambda$dispatchDraw$0(EditTextCaption.this, valueAnimator);
                    }
                });
                ValueAnimator valueAnimator = this.messageEditTextAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.messageEditTextAnimator = ofFloat;
                ofFloat.setDuration(200L);
                ofFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
                ofFloat.start();
                this.shouldAnimateEditTextWithBounds = false;
            }
            super.dispatchDraw(canvas);
        }

        @Override
        public void hidePopup(boolean z) {
            super.hidePopup(z);
        }

        @Override
        public void onLineCountChanged(int i, int i2) {
            if (TextUtils.isEmpty(getEditText().getText())) {
                getEditText().animate().cancel();
                getEditText().setOffsetY(0.0f);
                this.shouldAnimateEditTextWithBounds = false;
            } else {
                this.shouldAnimateEditTextWithBounds = true;
                this.messageEditTextPredrawHeigth = getEditText().getMeasuredHeight();
                this.messageEditTextPredrawScrollY = getEditText().getScrollY();
                invalidate();
            }
            ShareAlert.this.chatActivityEnterViewAnimateFromTop = r2.frameLayout2.getTop() + ShareAlert.this.captionEditTextTopOffset;
            ShareAlert.this.frameLayout2.invalidate();
        }

        @Override
        public void showPopup(int i) {
            super.showPopup(i);
        }
    }

    public class AnonymousClass19 extends FrameLayout {
        AnonymousClass19(Context context) {
            super(context);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setText(LocaleController.formatPluralString("AccDescrShareInChats", ShareAlert.this.selectedDialogs.size(), new Object[0]));
            accessibilityNodeInfo.setClassName(Button.class.getName());
            accessibilityNodeInfo.setLongClickable(true);
            accessibilityNodeInfo.setClickable(true);
        }
    }

    public class AnonymousClass2 extends BottomSheet.BottomSheetDelegate {
        AnonymousClass2() {
        }

        @Override
        public void onOpenAnimationEnd() {
            ShareAlert.this.fullyShown = true;
        }
    }

    public class AnonymousClass20 extends ViewOutlineProvider {
        AnonymousClass20() {
        }

        @Override
        public void getOutline(View view, Outline outline) {
            outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        }
    }

    public class AnonymousClass21 extends View {
        AnonymousClass21(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            String format = String.format("%d", Integer.valueOf(Math.max(1, ShareAlert.this.selectedDialogs.size())));
            int max = Math.max(AndroidUtilities.dp(16.0f) + ((int) Math.ceil(ShareAlert.this.textPaint.measureText(format))), AndroidUtilities.dp(24.0f));
            int measuredWidth = getMeasuredWidth() / 2;
            getMeasuredHeight();
            ShareAlert.this.textPaint.setColor(ShareAlert.this.getThemedColor(Theme.key_dialogRoundCheckBoxCheck));
            ShareAlert.this.paint.setColor(ShareAlert.this.getThemedColor(Theme.key_dialogBackground));
            int i = max / 2;
            ShareAlert.this.rect.set(measuredWidth - i, 0.0f, i + measuredWidth, getMeasuredHeight());
            canvas.drawRoundRect(ShareAlert.this.rect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), ShareAlert.this.paint);
            ShareAlert.this.paint.setColor(ShareAlert.this.getThemedColor(Theme.key_dialogFloatingButton));
            ShareAlert.this.rect.set(r5 + AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), r2 - AndroidUtilities.dp(2.0f), getMeasuredHeight() - AndroidUtilities.dp(2.0f));
            canvas.drawRoundRect(ShareAlert.this.rect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), ShareAlert.this.paint);
            canvas.drawText(format, measuredWidth - (r1 / 2), AndroidUtilities.dp(16.2f), ShareAlert.this.textPaint);
        }
    }

    public class AnonymousClass22 implements DialogsSearchAdapter.OnRecentSearchLoaded {
        AnonymousClass22() {
        }

        @Override
        public void setRecentSearch(ArrayList arrayList, LongSparseArray longSparseArray) {
            if (arrayList != null) {
                int i = 0;
                while (i < arrayList.size()) {
                    TLObject tLObject = ((DialogsSearchAdapter.RecentSearchObject) arrayList.get(i)).object;
                    if ((tLObject instanceof TLRPC.Chat) && !ChatObject.canWriteToChat((TLRPC.Chat) tLObject)) {
                        arrayList.remove(i);
                        i--;
                    }
                    i++;
                }
            }
            ShareAlert.this.recentSearchObjects = arrayList;
            ShareAlert.this.recentSearchObjectsById = longSparseArray;
            for (int i2 = 0; i2 < ShareAlert.this.recentSearchObjects.size(); i2++) {
                DialogsSearchAdapter.RecentSearchObject recentSearchObject = (DialogsSearchAdapter.RecentSearchObject) ShareAlert.this.recentSearchObjects.get(i2);
                TLObject tLObject2 = recentSearchObject.object;
                if (tLObject2 instanceof TLRPC.User) {
                    MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).putUser((TLRPC.User) recentSearchObject.object, true);
                } else if (tLObject2 instanceof TLRPC.Chat) {
                    MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).putChat((TLRPC.Chat) recentSearchObject.object, true);
                } else if (tLObject2 instanceof TLRPC.EncryptedChat) {
                    MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).putEncryptedChat((TLRPC.EncryptedChat) recentSearchObject.object, true);
                }
            }
            ShareAlert.this.searchAdapter.notifyDataSetChanged();
        }
    }

    public class AnonymousClass23 implements NotificationCenter.NotificationCenterDelegate {
        final View val$cell;
        final TLRPC.Dialog val$dialog;
        final AtomicReference val$timeoutRef;

        AnonymousClass23(TLRPC.Dialog dialog, AtomicReference atomicReference, View view) {
            this.val$dialog = dialog;
            this.val$timeoutRef = atomicReference;
            this.val$cell = view;
        }

        public void lambda$didReceivedNotification$0(View view, int[] iArr, DynamicAnimation dynamicAnimation, float f, float f2) {
            ShareAlert.this.invalidateTopicsAnimation(view, iArr, f / 1000.0f);
        }

        public void lambda$didReceivedNotification$1(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
            ShareAlert.this.gridView.setVisibility(8);
            ShareAlert.this.searchGridView.setVisibility(8);
            ShareAlert.this.searchView.setVisibility(8);
            ShareAlert.this.topicsAnimation = null;
        }

        @Override
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            if (((Long) objArr[0]).longValue() == (-this.val$dialog.id)) {
                boolean z = (ShareAlert.this.shareTopicsAdapter.topics == null && MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).getTopicsController().getTopics(-this.val$dialog.id) != null) || this.val$timeoutRef.get() == null;
                ShareAlert.this.shareTopicsAdapter.topics = MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).getTopicsController().getTopics(-this.val$dialog.id);
                if (z) {
                    ShareAlert.this.shareTopicsAdapter.notifyDataSetChanged();
                }
                if (ShareAlert.this.shareTopicsAdapter.topics != null) {
                    NotificationCenter.getInstance(((BottomSheet) ShareAlert.this).currentAccount).removeObserver(this, NotificationCenter.topicsDidLoaded);
                }
                if (z) {
                    ShareAlert.this.topicsGridView.setVisibility(0);
                    ShareAlert.this.topicsGridView.setAlpha(0.0f);
                    ShareAlert.this.topicsBackActionBar.setVisibility(0);
                    ShareAlert.this.topicsBackActionBar.setAlpha(0.0f);
                    ShareAlert shareAlert = ShareAlert.this;
                    shareAlert.topicsBackActionBar.setTitle(MessagesController.getInstance(((BottomSheet) shareAlert).currentAccount).getChat(Long.valueOf(-this.val$dialog.id)).title);
                    ShareAlert.this.topicsBackActionBar.setSubtitle(LocaleController.getString(R.string.SelectTopic));
                    ShareAlert shareAlert2 = ShareAlert.this;
                    shareAlert2.searchWasVisibleBeforeTopics = shareAlert2.searchIsVisible;
                    if (ShareAlert.this.topicsAnimation != null) {
                        ShareAlert.this.topicsAnimation.cancel();
                    }
                    final int[] iArr = new int[2];
                    ShareAlert.this.topicsAnimation = new SpringAnimation(new FloatValueHolder(0.0f)).setSpring(new SpringForce(1000.0f).setStiffness((ShareAlert.this.parentFragment == null || !ShareAlert.this.parentFragment.shareAlertDebugTopicsSlowMotion) ? 800.0f : 10.0f).setDampingRatio(1.0f));
                    SpringAnimation springAnimation = ShareAlert.this.topicsAnimation;
                    final View view = this.val$cell;
                    springAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                        @Override
                        public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                            ShareAlert.AnonymousClass23.this.lambda$didReceivedNotification$0(view, iArr, dynamicAnimation, f, f2);
                        }
                    });
                    ShareAlert.this.topicsAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                        @Override
                        public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                            ShareAlert.AnonymousClass23.this.lambda$didReceivedNotification$1(dynamicAnimation, z2, f, f2);
                        }
                    });
                    ShareAlert.this.topicsAnimation.start();
                    if (this.val$timeoutRef.get() != null) {
                        AndroidUtilities.cancelRunOnUIThread((Runnable) this.val$timeoutRef.get());
                        this.val$timeoutRef.set(null);
                    }
                }
            }
        }
    }

    public class AnonymousClass24 implements View.OnTouchListener {
        private android.graphics.Rect popupRect = new android.graphics.Rect();

        AnonymousClass24() {
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getActionMasked() != 0 || ShareAlert.this.sendPopupWindow == null || !ShareAlert.this.sendPopupWindow.isShowing()) {
                return false;
            }
            view.getHitRect(this.popupRect);
            if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return false;
            }
            ShareAlert.this.sendPopupWindow.dismiss();
            return false;
        }
    }

    public class AnonymousClass25 implements View.OnTouchListener {
        private android.graphics.Rect popupRect = new android.graphics.Rect();

        AnonymousClass25() {
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getActionMasked() != 0 || ShareAlert.this.sendPopupWindow == null || !ShareAlert.this.sendPopupWindow.isShowing()) {
                return false;
            }
            view.getHitRect(this.popupRect);
            if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                return false;
            }
            ShareAlert.this.sendPopupWindow.dismiss();
            return false;
        }
    }

    public class AnonymousClass26 extends AnimatorListenerAdapter {
        final int val$num;
        final boolean val$show;

        AnonymousClass26(int i, boolean z) {
            r2 = i;
            r3 = z;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            if (ShareAlert.this.shadowAnimation[r2] == null || !ShareAlert.this.shadowAnimation[r2].equals(animator)) {
                return;
            }
            ShareAlert.this.shadowAnimation[r2] = null;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (ShareAlert.this.shadowAnimation[r2] == null || !ShareAlert.this.shadowAnimation[r2].equals(animator)) {
                return;
            }
            if (!r3) {
                ShareAlert.this.shadow[r2].setVisibility(4);
            }
            ShareAlert.this.shadowAnimation[r2] = null;
        }
    }

    public class AnonymousClass27 extends AnimatorListenerAdapter {
        final boolean val$show;

        AnonymousClass27(boolean z) {
            r2 = z;
        }

        @Override
        public void onAnimationCancel(Animator animator) {
            if (animator.equals(ShareAlert.this.animatorSet)) {
                ShareAlert.this.animatorSet = null;
            }
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            FrameLayout frameLayout;
            if (animator.equals(ShareAlert.this.animatorSet)) {
                if (r2) {
                    if (ShareAlert.this.pickerBottom != null) {
                        frameLayout = ShareAlert.this.pickerBottom;
                    }
                    ShareAlert.this.animatorSet = null;
                } else {
                    ShareAlert.this.frameLayout2.setVisibility(4);
                    ShareAlert shareAlert = ShareAlert.this;
                    if (shareAlert.timestampFrameLayout != null && shareAlert.pickerBottom == null) {
                        ShareAlert.this.timestampFrameLayout.setVisibility(4);
                    }
                    frameLayout = ShareAlert.this.writeButtonContainer;
                }
                frameLayout.setVisibility(4);
                ShareAlert.this.animatorSet = null;
            }
        }
    }

    public class AnonymousClass3 extends SizeNotifierFrameLayout {
        private int fromOffsetTop;
        private int fromScrollY;
        private boolean fullHeight;
        private boolean lightStatusBar;
        private final AnimatedFloat pinnedToTop;
        private int previousTopOffset;
        private int toOffsetTop;
        private int toScrollY;
        private int topOffset;
        private boolean ignoreLayout = false;
        private RectF rect1 = new RectF();

        public class AnonymousClass1 extends AdjustPanLayoutHelper {
            AnonymousClass1(View view) {
                super(view);
            }

            @Override
            protected boolean heightAnimationEnabled() {
                if (ShareAlert.this.isDismissed() || !ShareAlert.this.fullyShown) {
                    return false;
                }
                return !ShareAlert.this.commentTextView.isPopupVisible();
            }

            @Override
            public void onPanTranslationUpdate(float f, float f2, boolean z) {
                ShareAlert.this.keyboardT = f2;
                super.onPanTranslationUpdate(f, f2, z);
                for (int i = 0; i < ((BottomSheet) ShareAlert.this).containerView.getChildCount(); i++) {
                    View childAt = ((BottomSheet) ShareAlert.this).containerView.getChildAt(i);
                    if (childAt != ShareAlert.this.pickerBottom && childAt != ShareAlert.this.bulletinContainer && childAt != ShareAlert.this.shadow[1] && childAt != ShareAlert.this.sharesCountLayout && childAt != ShareAlert.this.frameLayout2) {
                        ShareAlert shareAlert = ShareAlert.this;
                        if (childAt != shareAlert.timestampFrameLayout && childAt != shareAlert.writeButtonContainer && childAt != ShareAlert.this.selectedCountView && childAt != ShareAlert.this.navigationBar) {
                            childAt.setTranslationY(f);
                        }
                    }
                }
                ShareAlert.this.currentPanTranslationY = f;
                if (AnonymousClass3.this.fromScrollY != -1) {
                    if (!z) {
                        f2 = 1.0f - f2;
                    }
                    float f3 = 1.0f - f2;
                    ShareAlert.this.scrollOffsetY = (int) ((r5.fromScrollY * f3) + (AnonymousClass3.this.toScrollY * f2));
                    float f4 = ShareAlert.this.currentPanTranslationY + ((AnonymousClass3.this.fromScrollY - AnonymousClass3.this.toScrollY) * f3);
                    ShareAlert.this.gridView.setTranslationY(f4);
                    RecyclerListView recyclerListView = ShareAlert.this.searchGridView;
                    if (!z) {
                        f4 += ShareAlert.this.gridView.getPaddingTop();
                    }
                    recyclerListView.setTranslationY(f4);
                } else if (AnonymousClass3.this.fromOffsetTop != -1) {
                    float f5 = 1.0f - f2;
                    ShareAlert.this.scrollOffsetY = (int) ((r5.fromOffsetTop * f5) + (AnonymousClass3.this.toOffsetTop * f2));
                    if (!z) {
                        f5 = f2;
                    }
                    RecyclerListView recyclerListView2 = ShareAlert.this.gridView;
                    if (z) {
                        recyclerListView2.setTranslationY(ShareAlert.this.currentPanTranslationY - ((AnonymousClass3.this.fromOffsetTop - AnonymousClass3.this.toOffsetTop) * f2));
                    } else {
                        recyclerListView2.setTranslationY(ShareAlert.this.currentPanTranslationY + ((AnonymousClass3.this.toOffsetTop - AnonymousClass3.this.fromOffsetTop) * f5));
                    }
                }
                ShareAlert.this.gridView.setTopGlowOffset((int) (ShareAlert.this.scrollOffsetY + ShareAlert.this.currentPanTranslationY));
                ShareAlert.this.frameLayout.setTranslationY(ShareAlert.this.scrollOffsetY + ShareAlert.this.currentPanTranslationY);
                ShareAlert.this.searchEmptyView.setTranslationY(ShareAlert.this.scrollOffsetY + ShareAlert.this.currentPanTranslationY);
                ShareAlert.this.frameLayout2.invalidate();
                ShareAlert shareAlert2 = ShareAlert.this;
                shareAlert2.setCurrentPanTranslationY(shareAlert2.currentPanTranslationY);
                ShareAlert.this.updateBottomOverlay();
                AnonymousClass3.this.invalidate();
            }

            @Override
            public void onTransitionEnd() {
                super.onTransitionEnd();
                ShareAlert shareAlert = ShareAlert.this;
                shareAlert.keyboardT = ((shareAlert.commentTextView == null || !ShareAlert.this.commentTextView.isPopupVisible()) && ShareAlert.this.keyboardSize2 <= AndroidUtilities.dp(20.0f)) ? 0.0f : 1.0f;
                ShareAlert.this.panTranslationMoveLayout = false;
                ShareAlert shareAlert2 = ShareAlert.this;
                shareAlert2.previousScrollOffsetY = shareAlert2.scrollOffsetY;
                ShareAlert.this.gridView.setTopGlowOffset(ShareAlert.this.scrollOffsetY);
                ShareAlert.this.frameLayout.setTranslationY(ShareAlert.this.scrollOffsetY);
                ShareAlert.this.searchEmptyView.setTranslationY(ShareAlert.this.scrollOffsetY);
                ShareAlert.this.gridView.setTranslationY(0.0f);
                ShareAlert.this.searchGridView.setTranslationY(0.0f);
                ShareAlert.this.updateBottomOverlay();
            }

            @Override
            public void onTransitionStart(boolean z, int i) {
                super.onTransitionStart(z, i);
                if (ShareAlert.this.previousScrollOffsetY != ShareAlert.this.scrollOffsetY) {
                    AnonymousClass3 anonymousClass3 = AnonymousClass3.this;
                    anonymousClass3.fromScrollY = ShareAlert.this.previousScrollOffsetY;
                    AnonymousClass3 anonymousClass32 = AnonymousClass3.this;
                    anonymousClass32.toScrollY = ShareAlert.this.scrollOffsetY;
                    ShareAlert.this.panTranslationMoveLayout = true;
                    AnonymousClass3 anonymousClass33 = AnonymousClass3.this;
                    ShareAlert.this.scrollOffsetY = anonymousClass33.fromScrollY;
                } else {
                    AnonymousClass3.this.fromScrollY = -1;
                }
                if (AnonymousClass3.this.topOffset != AnonymousClass3.this.previousTopOffset) {
                    AnonymousClass3.this.fromOffsetTop = 0;
                    AnonymousClass3.this.toOffsetTop = 0;
                    ShareAlert.this.panTranslationMoveLayout = true;
                    AnonymousClass3 anonymousClass34 = AnonymousClass3.this;
                    int i2 = anonymousClass34.topOffset - AnonymousClass3.this.previousTopOffset;
                    if (z) {
                        AnonymousClass3.access$3312(anonymousClass34, i2);
                    } else {
                        AnonymousClass3.access$3320(anonymousClass34, i2);
                    }
                    AnonymousClass3 anonymousClass35 = AnonymousClass3.this;
                    ShareAlert.this.scrollOffsetY = z ? anonymousClass35.fromScrollY : anonymousClass35.toScrollY;
                } else {
                    AnonymousClass3.this.fromOffsetTop = -1;
                }
                ShareAlert.this.gridView.setTopGlowOffset((int) (ShareAlert.this.currentPanTranslationY + ShareAlert.this.scrollOffsetY));
                ShareAlert.this.frameLayout.setTranslationY(ShareAlert.this.currentPanTranslationY + ShareAlert.this.scrollOffsetY);
                ShareAlert.this.searchEmptyView.setTranslationY(ShareAlert.this.currentPanTranslationY + ShareAlert.this.scrollOffsetY);
                AnonymousClass3.this.invalidate();
            }
        }

        AnonymousClass3(Context context) {
            super(context);
            this.ignoreLayout = false;
            this.rect1 = new RectF();
            this.adjustPanLayoutHelper = new AdjustPanLayoutHelper(this) {
                AnonymousClass1(View this) {
                    super(this);
                }

                @Override
                protected boolean heightAnimationEnabled() {
                    if (ShareAlert.this.isDismissed() || !ShareAlert.this.fullyShown) {
                        return false;
                    }
                    return !ShareAlert.this.commentTextView.isPopupVisible();
                }

                @Override
                public void onPanTranslationUpdate(float f, float f2, boolean z) {
                    ShareAlert.this.keyboardT = f2;
                    super.onPanTranslationUpdate(f, f2, z);
                    for (int i = 0; i < ((BottomSheet) ShareAlert.this).containerView.getChildCount(); i++) {
                        View childAt = ((BottomSheet) ShareAlert.this).containerView.getChildAt(i);
                        if (childAt != ShareAlert.this.pickerBottom && childAt != ShareAlert.this.bulletinContainer && childAt != ShareAlert.this.shadow[1] && childAt != ShareAlert.this.sharesCountLayout && childAt != ShareAlert.this.frameLayout2) {
                            ShareAlert shareAlert = ShareAlert.this;
                            if (childAt != shareAlert.timestampFrameLayout && childAt != shareAlert.writeButtonContainer && childAt != ShareAlert.this.selectedCountView && childAt != ShareAlert.this.navigationBar) {
                                childAt.setTranslationY(f);
                            }
                        }
                    }
                    ShareAlert.this.currentPanTranslationY = f;
                    if (AnonymousClass3.this.fromScrollY != -1) {
                        if (!z) {
                            f2 = 1.0f - f2;
                        }
                        float f3 = 1.0f - f2;
                        ShareAlert.this.scrollOffsetY = (int) ((r5.fromScrollY * f3) + (AnonymousClass3.this.toScrollY * f2));
                        float f4 = ShareAlert.this.currentPanTranslationY + ((AnonymousClass3.this.fromScrollY - AnonymousClass3.this.toScrollY) * f3);
                        ShareAlert.this.gridView.setTranslationY(f4);
                        RecyclerListView recyclerListView = ShareAlert.this.searchGridView;
                        if (!z) {
                            f4 += ShareAlert.this.gridView.getPaddingTop();
                        }
                        recyclerListView.setTranslationY(f4);
                    } else if (AnonymousClass3.this.fromOffsetTop != -1) {
                        float f5 = 1.0f - f2;
                        ShareAlert.this.scrollOffsetY = (int) ((r5.fromOffsetTop * f5) + (AnonymousClass3.this.toOffsetTop * f2));
                        if (!z) {
                            f5 = f2;
                        }
                        RecyclerListView recyclerListView2 = ShareAlert.this.gridView;
                        if (z) {
                            recyclerListView2.setTranslationY(ShareAlert.this.currentPanTranslationY - ((AnonymousClass3.this.fromOffsetTop - AnonymousClass3.this.toOffsetTop) * f2));
                        } else {
                            recyclerListView2.setTranslationY(ShareAlert.this.currentPanTranslationY + ((AnonymousClass3.this.toOffsetTop - AnonymousClass3.this.fromOffsetTop) * f5));
                        }
                    }
                    ShareAlert.this.gridView.setTopGlowOffset((int) (ShareAlert.this.scrollOffsetY + ShareAlert.this.currentPanTranslationY));
                    ShareAlert.this.frameLayout.setTranslationY(ShareAlert.this.scrollOffsetY + ShareAlert.this.currentPanTranslationY);
                    ShareAlert.this.searchEmptyView.setTranslationY(ShareAlert.this.scrollOffsetY + ShareAlert.this.currentPanTranslationY);
                    ShareAlert.this.frameLayout2.invalidate();
                    ShareAlert shareAlert2 = ShareAlert.this;
                    shareAlert2.setCurrentPanTranslationY(shareAlert2.currentPanTranslationY);
                    ShareAlert.this.updateBottomOverlay();
                    AnonymousClass3.this.invalidate();
                }

                @Override
                public void onTransitionEnd() {
                    super.onTransitionEnd();
                    ShareAlert shareAlert = ShareAlert.this;
                    shareAlert.keyboardT = ((shareAlert.commentTextView == null || !ShareAlert.this.commentTextView.isPopupVisible()) && ShareAlert.this.keyboardSize2 <= AndroidUtilities.dp(20.0f)) ? 0.0f : 1.0f;
                    ShareAlert.this.panTranslationMoveLayout = false;
                    ShareAlert shareAlert2 = ShareAlert.this;
                    shareAlert2.previousScrollOffsetY = shareAlert2.scrollOffsetY;
                    ShareAlert.this.gridView.setTopGlowOffset(ShareAlert.this.scrollOffsetY);
                    ShareAlert.this.frameLayout.setTranslationY(ShareAlert.this.scrollOffsetY);
                    ShareAlert.this.searchEmptyView.setTranslationY(ShareAlert.this.scrollOffsetY);
                    ShareAlert.this.gridView.setTranslationY(0.0f);
                    ShareAlert.this.searchGridView.setTranslationY(0.0f);
                    ShareAlert.this.updateBottomOverlay();
                }

                @Override
                public void onTransitionStart(boolean z, int i) {
                    super.onTransitionStart(z, i);
                    if (ShareAlert.this.previousScrollOffsetY != ShareAlert.this.scrollOffsetY) {
                        AnonymousClass3 anonymousClass3 = AnonymousClass3.this;
                        anonymousClass3.fromScrollY = ShareAlert.this.previousScrollOffsetY;
                        AnonymousClass3 anonymousClass32 = AnonymousClass3.this;
                        anonymousClass32.toScrollY = ShareAlert.this.scrollOffsetY;
                        ShareAlert.this.panTranslationMoveLayout = true;
                        AnonymousClass3 anonymousClass33 = AnonymousClass3.this;
                        ShareAlert.this.scrollOffsetY = anonymousClass33.fromScrollY;
                    } else {
                        AnonymousClass3.this.fromScrollY = -1;
                    }
                    if (AnonymousClass3.this.topOffset != AnonymousClass3.this.previousTopOffset) {
                        AnonymousClass3.this.fromOffsetTop = 0;
                        AnonymousClass3.this.toOffsetTop = 0;
                        ShareAlert.this.panTranslationMoveLayout = true;
                        AnonymousClass3 anonymousClass34 = AnonymousClass3.this;
                        int i2 = anonymousClass34.topOffset - AnonymousClass3.this.previousTopOffset;
                        if (z) {
                            AnonymousClass3.access$3312(anonymousClass34, i2);
                        } else {
                            AnonymousClass3.access$3320(anonymousClass34, i2);
                        }
                        AnonymousClass3 anonymousClass35 = AnonymousClass3.this;
                        ShareAlert.this.scrollOffsetY = z ? anonymousClass35.fromScrollY : anonymousClass35.toScrollY;
                    } else {
                        AnonymousClass3.this.fromOffsetTop = -1;
                    }
                    ShareAlert.this.gridView.setTopGlowOffset((int) (ShareAlert.this.currentPanTranslationY + ShareAlert.this.scrollOffsetY));
                    ShareAlert.this.frameLayout.setTranslationY(ShareAlert.this.currentPanTranslationY + ShareAlert.this.scrollOffsetY);
                    ShareAlert.this.searchEmptyView.setTranslationY(ShareAlert.this.currentPanTranslationY + ShareAlert.this.scrollOffsetY);
                    AnonymousClass3.this.invalidate();
                }
            };
            this.lightStatusBar = AndroidUtilities.computePerceivedBrightness(ShareAlert.this.getThemedColor(Theme.key_dialogBackground)) > 0.721f;
            this.pinnedToTop = new AnimatedFloat(this, 0L, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
        }

        static int access$3312(AnonymousClass3 anonymousClass3, int i) {
            int i2 = anonymousClass3.toOffsetTop + i;
            anonymousClass3.toOffsetTop = i2;
            return i2;
        }

        static int access$3320(AnonymousClass3 anonymousClass3, int i) {
            int i2 = anonymousClass3.toOffsetTop - i;
            anonymousClass3.toOffsetTop = i2;
            return i2;
        }

        private void onMeasureInternal(int i, int i2) {
            int makeMeasureSpec;
            int paddingTop;
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            int i3 = size - (((BottomSheet) ShareAlert.this).backgroundPaddingLeft * 2);
            ShareAlert.this.keyboardSize2 = measureKeyboardHeight();
            if (!ShareAlert.this.commentTextView.isWaitingForKeyboardOpen() && ShareAlert.this.keyboardSize2 <= AndroidUtilities.dp(20.0f) && !ShareAlert.this.commentTextView.isPopupShowing() && !ShareAlert.this.commentTextView.isAnimatePopupClosing()) {
                this.ignoreLayout = true;
                ShareAlert.this.commentTextView.hideEmojiView();
                this.ignoreLayout = false;
            }
            this.ignoreLayout = true;
            if (ShareAlert.this.keyboardSize2 <= AndroidUtilities.dp(20.0f)) {
                if (!AndroidUtilities.isInMultiwindow) {
                    size2 -= ((BottomSheet) ShareAlert.this).keyboardVisible ? 0 : ShareAlert.this.commentTextView.getEmojiPadding();
                    i2 = View.MeasureSpec.makeMeasureSpec(size2, 1073741824);
                }
                int i4 = ShareAlert.this.commentTextView.isPopupShowing() ? 8 : 0;
                if (ShareAlert.this.pickerBottomLayout != null) {
                    ShareAlert.this.pickerBottomLayout.setVisibility(i4);
                }
            } else {
                if (!ShareAlert.this.commentTextView.isPopupVisible()) {
                    ShareAlert.this.commentTextView.hideEmojiView();
                }
                if (ShareAlert.this.pickerBottomLayout != null) {
                    ShareAlert.this.pickerBottomLayout.setVisibility(8);
                }
            }
            this.ignoreLayout = false;
            BlurredFrameLayout blurredFrameLayout = ShareAlert.this.navigationBar;
            if (blurredFrameLayout != null) {
                blurredFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(i3, 1073741824), View.MeasureSpec.makeMeasureSpec(((BottomSheet) ShareAlert.this).navigationBarHeight, 1073741824));
            }
            int childCount = getChildCount();
            for (int i5 = 0; i5 < childCount; i5++) {
                View childAt = getChildAt(i5);
                if (childAt != null && childAt.getVisibility() != 8) {
                    ShareAlert shareAlert = ShareAlert.this;
                    if (childAt != shareAlert.navigationBar) {
                        if (shareAlert.commentTextView == null || !ShareAlert.this.commentTextView.isPopupView(childAt)) {
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                        } else {
                            if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
                                paddingTop = childAt.getLayoutParams().height;
                            } else if (AndroidUtilities.isTablet()) {
                                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
                                paddingTop = Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop());
                            } else {
                                makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i3, 1073741824);
                                paddingTop = (size2 - AndroidUtilities.statusBarHeight) + getPaddingTop();
                            }
                            childAt.measure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(paddingTop, 1073741824));
                        }
                    }
                }
            }
            ShareAlert.this.updateBottomOverlay();
        }

        @Override
        public void dispatchDraw(Canvas canvas) {
            canvas.save();
            canvas.clipRect(0.0f, getPaddingTop() + ShareAlert.this.currentPanTranslationY, getMeasuredWidth(), getMeasuredHeight() + ShareAlert.this.currentPanTranslationY + AndroidUtilities.dp(50.0f));
            super.dispatchDraw(canvas);
            canvas.restore();
        }

        @Override
        public void drawList(Canvas canvas, boolean z, ArrayList arrayList) {
            if (ShareAlert.this.gridView.getVisibility() == 0 && ShareAlert.this.gridView.getAlpha() >= 0.0f) {
                canvas.save();
                canvas.translate(ShareAlert.this.gridView.getX(), ShareAlert.this.gridView.getY());
                ShareAlert.this.gridView.draw(canvas);
                canvas.restore();
            }
            if (ShareAlert.this.topicsGridView.getVisibility() == 0 && ShareAlert.this.topicsGridView.getAlpha() >= 0.0f) {
                canvas.save();
                canvas.translate(ShareAlert.this.topicsGridView.getX(), ShareAlert.this.topicsGridView.getY());
                ShareAlert.this.topicsGridView.draw(canvas);
                canvas.restore();
            }
            if (ShareAlert.this.searchGridView.getVisibility() != 0 || ShareAlert.this.searchGridView.getAlpha() < 0.0f) {
                return;
            }
            canvas.save();
            canvas.translate(ShareAlert.this.searchGridView.getX(), ShareAlert.this.searchGridView.getY());
            ShareAlert.this.searchGridView.draw(canvas);
            canvas.restore();
        }

        @Override
        protected Theme.ResourcesProvider getResourceProvider() {
            return ((BottomSheet) ShareAlert.this).resourcesProvider;
        }

        @Override
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.adjustPanLayoutHelper.setResizableView(this);
            this.adjustPanLayoutHelper.onAttach();
        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.adjustPanLayoutHelper.onDetach();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float f;
            canvas.save();
            canvas.translate(0.0f, ShareAlert.this.currentPanTranslationY);
            int dp = (ShareAlert.this.scrollOffsetY - ((BottomSheet) ShareAlert.this).backgroundPaddingTop) + AndroidUtilities.dp(6.0f) + this.topOffset;
            ShareAlert shareAlert = ShareAlert.this;
            int i = shareAlert.containerViewTop = ((shareAlert.scrollOffsetY - ((BottomSheet) ShareAlert.this).backgroundPaddingTop) - AndroidUtilities.dp(13.0f)) + this.topOffset;
            int measuredHeight = getMeasuredHeight() + AndroidUtilities.dp(60.0f) + ((BottomSheet) ShareAlert.this).backgroundPaddingTop;
            if (((BottomSheet) ShareAlert.this).isFullscreen || Build.VERSION.SDK_INT < 21) {
                f = 0.0f;
            } else {
                dp += AndroidUtilities.statusBarHeight;
                boolean z = this.fullHeight && ((BottomSheet) ShareAlert.this).backgroundPaddingTop + i < AndroidUtilities.statusBarHeight;
                int i2 = i + AndroidUtilities.statusBarHeight;
                int i3 = -((BottomSheet) ShareAlert.this).backgroundPaddingTop;
                f = this.pinnedToTop.set(z);
                i = AndroidUtilities.lerp(i2, i3, f);
            }
            ShareAlert.this.shadowDrawable.setBounds(0, i, getMeasuredWidth(), measuredHeight);
            ShareAlert.this.shadowDrawable.draw(canvas);
            FrameLayout frameLayout = ShareAlert.this.bulletinContainer2;
            if (frameLayout != null) {
                if (i > AndroidUtilities.statusBarHeight || frameLayout.getChildCount() <= 0) {
                    ShareAlert.this.bulletinContainer2.setTranslationY(Math.max(0, ((i + ((BottomSheet) r1).backgroundPaddingTop) - ShareAlert.this.bulletinContainer2.getTop()) - ShareAlert.this.bulletinContainer2.getMeasuredHeight()));
                } else {
                    ShareAlert.this.bulletinContainer2.setTranslationY(0.0f);
                    Bulletin visibleBulletin = Bulletin.getVisibleBulletin();
                    if (visibleBulletin != null) {
                        if (visibleBulletin.getLayout() != null) {
                            visibleBulletin.getLayout().setTop(true);
                        }
                        visibleBulletin.hide();
                    }
                }
            }
            if (f < 1.0f) {
                int dp2 = AndroidUtilities.dp(36.0f);
                this.rect1.set((getMeasuredWidth() - dp2) / 2, dp, (getMeasuredWidth() + dp2) / 2, dp + AndroidUtilities.dp(4.0f));
                Theme.dialogs_onlineCirclePaint.setColor(ShareAlert.this.getThemedColor(Theme.key_sheet_scrollUp));
                Theme.dialogs_onlineCirclePaint.setAlpha((int) (r0.getAlpha() * (1.0f - f)));
                canvas.drawRoundRect(this.rect1, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
            }
            if (Build.VERSION.SDK_INT >= 23) {
                int systemUiVisibility = getSystemUiVisibility();
                boolean z2 = this.lightStatusBar && ((float) 0) > ((float) AndroidUtilities.statusBarHeight) * 0.5f;
                if (z2 != ((systemUiVisibility & 8192) > 0)) {
                    setSystemUiVisibility(z2 ? systemUiVisibility | 8192 : systemUiVisibility & (-8193));
                }
            }
            canvas.restore();
            this.previousTopOffset = this.topOffset;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            if (this.fullHeight ? motionEvent.getAction() != 0 || ShareAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ShareAlert.this.scrollOffsetY - AndroidUtilities.dp(30.0f) : motionEvent.getAction() != 0 || motionEvent.getY() >= this.topOffset - AndroidUtilities.dp(30.0f)) {
                return super.onInterceptTouchEvent(motionEvent);
            }
            ShareAlert.this.lambda$new$0();
            return true;
        }

        @Override
        public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.AnonymousClass3.onLayout(boolean, int, int, int, int):void");
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int dp;
            int size = getLayoutParams().height > 0 ? getLayoutParams().height : View.MeasureSpec.getSize(i2);
            ShareAlert.this.layoutManager.setNeedFixGap(getLayoutParams().height <= 0);
            ShareAlert.this.searchLayoutManager.setNeedFixGap(getLayoutParams().height <= 0);
            if (Build.VERSION.SDK_INT >= 21 && !((BottomSheet) ShareAlert.this).isFullscreen) {
                this.ignoreLayout = true;
                setPadding(((BottomSheet) ShareAlert.this).backgroundPaddingLeft, AndroidUtilities.statusBarHeight, ((BottomSheet) ShareAlert.this).backgroundPaddingLeft, 0);
                this.ignoreLayout = false;
            }
            int paddingTop = size - getPaddingTop();
            int dp2 = AndroidUtilities.dp(103.0f) + AndroidUtilities.dp(48.0f) + (Math.max(2, (int) Math.ceil(Math.max(ShareAlert.this.searchAdapter.getItemCount(), ShareAlert.this.listAdapter.getItemCount() - 1) / 4.0f)) * AndroidUtilities.dp(103.0f)) + ((BottomSheet) ShareAlert.this).backgroundPaddingTop;
            if (ShareAlert.this.topicsGridView.getVisibility() != 8 && (dp = AndroidUtilities.dp(103.0f) + AndroidUtilities.dp(48.0f) + (Math.max(2, (int) Math.ceil((ShareAlert.this.shareTopicsAdapter.getItemCount() - 1) / 4.0f)) * AndroidUtilities.dp(103.0f)) + ((BottomSheet) ShareAlert.this).backgroundPaddingTop) > dp2) {
                dp2 = AndroidUtilities.lerp(dp2, dp, ShareAlert.this.topicsGridView.getAlpha());
            }
            int dp3 = (dp2 < paddingTop ? 0 : paddingTop - ((paddingTop / 5) * 3)) + AndroidUtilities.dp(8.0f);
            if (ShareAlert.this.gridView.getPaddingTop() != dp3) {
                this.ignoreLayout = true;
                ShareAlert.this.gridView.setPadding(0, dp3, 0, AndroidUtilities.dp((ShareAlert.this.timestampFrameLayout != null ? 48 : 0) + 60) + ((BottomSheet) ShareAlert.this).navigationBarHeight);
                ShareAlert.this.topicsGridView.setPadding(0, dp3, 0, AndroidUtilities.dp((ShareAlert.this.timestampFrameLayout != null ? 48 : 0) + 60) + ((BottomSheet) ShareAlert.this).navigationBarHeight);
                this.ignoreLayout = false;
            }
            if (((BottomSheet) ShareAlert.this).keyboardVisible && getLayoutParams().height <= 0 && ShareAlert.this.searchGridView.getPaddingTop() != dp3) {
                this.ignoreLayout = true;
                ShareAlert.this.searchGridView.setPadding(0, 0, 0, AndroidUtilities.dp((ShareAlert.this.timestampFrameLayout == null ? 0 : 48) + 60) + ((BottomSheet) ShareAlert.this).navigationBarHeight);
                this.ignoreLayout = false;
            }
            boolean z = dp2 >= size;
            this.fullHeight = z;
            this.topOffset = z ? 0 : size - dp2;
            this.ignoreLayout = true;
            ShareAlert.this.checkCurrentList(false);
            this.ignoreLayout = false;
            setMeasuredDimension(View.MeasureSpec.getSize(i), size);
            onMeasureInternal(i, View.MeasureSpec.makeMeasureSpec(size, 1073741824));
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return !ShareAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
        }

        @Override
        public void requestLayout() {
            if (this.ignoreLayout) {
                return;
            }
            super.requestLayout();
        }
    }

    public class AnonymousClass4 extends SwitchView {
        AnonymousClass4(Context context) {
            super(context);
        }

        @Override
        protected void onTabSwitch(int i) {
            ShareAlert.this.updateLinkTextView();
        }
    }

    public class AnonymousClass5 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass5() {
        }

        @Override
        public void onItemClick(int i) {
            ShareAlert.this.onBackPressed();
        }
    }

    public class AnonymousClass6 extends GridLayoutManager.SpanSizeLookup {
        AnonymousClass6() {
        }

        @Override
        public int getSpanSize(int i) {
            if (i == 0) {
                return ShareAlert.this.topicsLayoutManager.getSpanCount();
            }
            return 1;
        }
    }

    public class AnonymousClass7 extends RecyclerView.OnScrollListener {
        AnonymousClass7() {
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int i, int i2) {
            if (i2 != 0) {
                ShareAlert.this.updateLayout();
                ShareAlert shareAlert = ShareAlert.this;
                shareAlert.previousScrollOffsetY = shareAlert.scrollOffsetY;
            }
        }
    }

    public class AnonymousClass8 extends RecyclerView.ItemDecoration {
        AnonymousClass8() {
        }

        @Override
        public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
            RecyclerListView.Holder holder = (RecyclerListView.Holder) recyclerView.getChildViewHolder(view);
            if (holder == null) {
                rect.left = AndroidUtilities.dp(4.0f);
                rect.right = AndroidUtilities.dp(4.0f);
            } else {
                int adapterPosition = holder.getAdapterPosition() % 4;
                rect.left = adapterPosition == 0 ? 0 : AndroidUtilities.dp(4.0f);
                rect.right = adapterPosition != 3 ? AndroidUtilities.dp(4.0f) : 0;
            }
        }
    }

    public class AnonymousClass9 extends RecyclerListView {
        AnonymousClass9(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
        }

        @Override
        protected boolean allowSelectChildAtPosition(float f, float f2) {
            return f2 >= ((float) (AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 58.0f : 111.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
        }

        @Override
        public void draw(Canvas canvas) {
            if (ShareAlert.this.topicsGridView.getVisibility() != 8) {
                canvas.save();
                canvas.clipRect(0, ShareAlert.this.scrollOffsetY + AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 58.0f : 111.0f), getWidth(), getHeight());
            }
            super.draw(canvas);
            if (ShareAlert.this.topicsGridView.getVisibility() != 8) {
                canvas.restore();
            }
        }
    }

    public static class DialogSearchResult {
        public int date;
        public TLRPC.Dialog dialog = new TLRPC.TL_dialog();
        public CharSequence name;
        public TLObject object;
    }

    public class SearchField extends FrameLayout {
        private ImageView clearSearchImageView;
        private CloseProgressDrawable2 progressDrawable;
        private View searchBackground;
        private EditTextBoldCursor searchEditText;
        private ImageView searchIconImageView;

        public class AnonymousClass1 extends CloseProgressDrawable2 {
            final ShareAlert val$this$0;

            AnonymousClass1(ShareAlert shareAlert) {
                r2 = shareAlert;
            }

            @Override
            protected int getCurrentColor() {
                return ShareAlert.this.getThemedColor(Theme.key_dialogSearchIcon);
            }
        }

        public class AnonymousClass2 implements TextWatcher {
            final ShareAlert val$this$0;

            AnonymousClass2(ShareAlert shareAlert) {
                r2 = shareAlert;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                boolean z = SearchField.this.searchEditText.length() > 0;
                if (z != (SearchField.this.clearSearchImageView.getAlpha() != 0.0f)) {
                    SearchField.this.clearSearchImageView.animate().alpha(z ? 1.0f : 0.0f).setDuration(150L).scaleX(z ? 1.0f : 0.1f).scaleY(z ? 1.0f : 0.1f).start();
                }
                if (!TextUtils.isEmpty(SearchField.this.searchEditText.getText())) {
                    ShareAlert.this.checkCurrentList(false);
                }
                if (ShareAlert.this.updateSearchAdapter) {
                    String obj = SearchField.this.searchEditText.getText().toString();
                    if (obj.length() != 0) {
                        if (ShareAlert.this.searchEmptyView != null) {
                            ShareAlert.this.searchEmptyView.title.setText(LocaleController.getString(R.string.NoResult));
                        }
                    } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
                        int currentTop = ShareAlert.this.getCurrentTop();
                        ShareAlert.this.searchEmptyView.title.setText(LocaleController.getString(R.string.NoResult));
                        ShareAlert.this.searchEmptyView.showProgress(false, true);
                        ShareAlert.this.checkCurrentList(false);
                        ShareAlert.this.listAdapter.notifyDataSetChanged();
                        if (currentTop > 0) {
                            ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -currentTop);
                        }
                    }
                    if (ShareAlert.this.searchAdapter != null) {
                        ShareAlert.this.searchAdapter.searchDialogs(obj);
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        }

        public SearchField(Context context) {
            super(context);
            View view = new View(context);
            this.searchBackground = view;
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), ShareAlert.this.getThemedColor(Theme.key_dialogSearchBackground)));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 11.0f, 14.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.searchIconImageView = imageView;
            ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
            imageView.setScaleType(scaleType);
            this.searchIconImageView.setImageResource(R.drawable.smiles_inputsearch);
            this.searchIconImageView.setColorFilter(new PorterDuffColorFilter(ShareAlert.this.getThemedColor(Theme.key_dialogSearchIcon), PorterDuff.Mode.MULTIPLY));
            addView(this.searchIconImageView, LayoutHelper.createFrame(36, 36.0f, 51, 16.0f, 11.0f, 0.0f, 0.0f));
            ImageView imageView2 = new ImageView(context);
            this.clearSearchImageView = imageView2;
            imageView2.setScaleType(scaleType);
            ImageView imageView3 = this.clearSearchImageView;
            AnonymousClass1 anonymousClass1 = new CloseProgressDrawable2() {
                final ShareAlert val$this$0;

                AnonymousClass1(ShareAlert shareAlert) {
                    r2 = shareAlert;
                }

                @Override
                protected int getCurrentColor() {
                    return ShareAlert.this.getThemedColor(Theme.key_dialogSearchIcon);
                }
            };
            this.progressDrawable = anonymousClass1;
            imageView3.setImageDrawable(anonymousClass1);
            this.progressDrawable.setSide(AndroidUtilities.dp(7.0f));
            this.clearSearchImageView.setScaleX(0.1f);
            this.clearSearchImageView.setScaleY(0.1f);
            this.clearSearchImageView.setAlpha(0.0f);
            addView(this.clearSearchImageView, LayoutHelper.createFrame(36, 36.0f, 53, 14.0f, 11.0f, 14.0f, 0.0f));
            this.clearSearchImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    ShareAlert.SearchField.this.lambda$new$0(view2);
                }
            });
            EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context);
            this.searchEditText = editTextBoldCursor;
            editTextBoldCursor.setTextSize(1, 16.0f);
            this.searchEditText.setHintTextColor(ShareAlert.this.getThemedColor(Theme.key_dialogSearchHint));
            this.searchEditText.setTextColor(ShareAlert.this.getThemedColor(Theme.key_dialogSearchText));
            this.searchEditText.setBackgroundDrawable(null);
            this.searchEditText.setPadding(0, 0, 0, 0);
            this.searchEditText.setMaxLines(1);
            this.searchEditText.setLines(1);
            this.searchEditText.setSingleLine(true);
            this.searchEditText.setImeOptions(268435459);
            this.searchEditText.setHint(LocaleController.getString(R.string.ShareSendTo));
            this.searchEditText.setCursorColor(ShareAlert.this.getThemedColor(Theme.key_featuredStickers_addedIcon));
            this.searchEditText.setCursorSize(AndroidUtilities.dp(20.0f));
            this.searchEditText.setCursorWidth(1.5f);
            addView(this.searchEditText, LayoutHelper.createFrame(-1, 40.0f, 51, 54.0f, 9.0f, 46.0f, 0.0f));
            this.searchEditText.addTextChangedListener(new TextWatcher() {
                final ShareAlert val$this$0;

                AnonymousClass2(ShareAlert shareAlert) {
                    r2 = shareAlert;
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    boolean z = SearchField.this.searchEditText.length() > 0;
                    if (z != (SearchField.this.clearSearchImageView.getAlpha() != 0.0f)) {
                        SearchField.this.clearSearchImageView.animate().alpha(z ? 1.0f : 0.0f).setDuration(150L).scaleX(z ? 1.0f : 0.1f).scaleY(z ? 1.0f : 0.1f).start();
                    }
                    if (!TextUtils.isEmpty(SearchField.this.searchEditText.getText())) {
                        ShareAlert.this.checkCurrentList(false);
                    }
                    if (ShareAlert.this.updateSearchAdapter) {
                        String obj = SearchField.this.searchEditText.getText().toString();
                        if (obj.length() != 0) {
                            if (ShareAlert.this.searchEmptyView != null) {
                                ShareAlert.this.searchEmptyView.title.setText(LocaleController.getString(R.string.NoResult));
                            }
                        } else if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.listAdapter) {
                            int currentTop = ShareAlert.this.getCurrentTop();
                            ShareAlert.this.searchEmptyView.title.setText(LocaleController.getString(R.string.NoResult));
                            ShareAlert.this.searchEmptyView.showProgress(false, true);
                            ShareAlert.this.checkCurrentList(false);
                            ShareAlert.this.listAdapter.notifyDataSetChanged();
                            if (currentTop > 0) {
                                ShareAlert.this.layoutManager.scrollToPositionWithOffset(0, -currentTop);
                            }
                        }
                        if (ShareAlert.this.searchAdapter != null) {
                            ShareAlert.this.searchAdapter.searchDialogs(obj);
                        }
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                }
            });
            this.searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    boolean lambda$new$1;
                    lambda$new$1 = ShareAlert.SearchField.this.lambda$new$1(textView, i, keyEvent);
                    return lambda$new$1;
                }
            });
        }

        public void lambda$new$0(View view) {
            ShareAlert.this.updateSearchAdapter = true;
            this.searchEditText.setText("");
            AndroidUtilities.showKeyboard(this.searchEditText);
        }

        public boolean lambda$new$1(TextView textView, int i, KeyEvent keyEvent) {
            if (keyEvent == null) {
                return false;
            }
            if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
                return false;
            }
            AndroidUtilities.hideKeyboard(this.searchEditText);
            return false;
        }

        public void hideKeyboard() {
            AndroidUtilities.hideKeyboard(this.searchEditText);
        }
    }

    public interface ShareAlertDelegate {

        public abstract class CC {
            public static void $default$didShare(ShareAlertDelegate shareAlertDelegate) {
            }
        }

        boolean didCopy();

        void didShare();
    }

    public class ShareDialogsAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private ArrayList dialogs = new ArrayList();
        private LongSparseArray dialogsMap = new LongSparseArray();

        class AnonymousClass1 extends ShareDialogCell {
            AnonymousClass1(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
                super(context, i, resourcesProvider);
            }

            @Override
            public String repostToCustomName() {
                return ShareAlert.this.includeStoryFromMessage ? LocaleController.getString(R.string.RepostToStory) : super.repostToCustomName();
            }
        }

        public class MyStoryDialog extends TLRPC.Dialog {
            private MyStoryDialog() {
                this.id = Long.MAX_VALUE;
            }

            MyStoryDialog(ShareDialogsAdapter shareDialogsAdapter, AnonymousClass1 anonymousClass1) {
                this();
            }
        }

        public ShareDialogsAdapter(Context context) {
            this.context = context;
            fetchDialogs();
        }

        public void fetchDialogs() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.ShareDialogsAdapter.fetchDialogs():void");
        }

        public TLRPC.Dialog getItem(int i) {
            int i2 = i - 1;
            if (i2 < 0 || i2 >= this.dialogs.size()) {
                return null;
            }
            return (TLRPC.Dialog) this.dialogs.get(i2);
        }

        @Override
        public int getItemCount() {
            int size = this.dialogs.size();
            return size != 0 ? size + 1 : size;
        }

        @Override
        public int getItemViewType(int i) {
            return i == 0 ? 1 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ShareDialogCell shareDialogCell = (ShareDialogCell) viewHolder.itemView;
                TLRPC.Dialog item = getItem(i);
                if (item == null) {
                    return;
                }
                shareDialogCell.setTopic((TLRPC.TL_forumTopic) ShareAlert.this.selectedDialogTopics.get(item), false);
                long j = item.id;
                shareDialogCell.setDialog(j, ShareAlert.this.selectedDialogs.indexOfKey(j) >= 0, null);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View anonymousClass1;
            RecyclerView.LayoutParams layoutParams;
            if (i != 0) {
                anonymousClass1 = new View(this.context);
                layoutParams = new RecyclerView.LayoutParams(-1, AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 56.0f : 109.0f));
            } else {
                anonymousClass1 = new ShareDialogCell(this.context, 0, ((BottomSheet) ShareAlert.this).resourcesProvider) {
                    AnonymousClass1(Context context, int i2, Theme.ResourcesProvider resourcesProvider) {
                        super(context, i2, resourcesProvider);
                    }

                    @Override
                    public String repostToCustomName() {
                        return ShareAlert.this.includeStoryFromMessage ? LocaleController.getString(R.string.RepostToStory) : super.repostToCustomName();
                    }
                };
                layoutParams = new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0f));
            }
            anonymousClass1.setLayoutParams(layoutParams);
            return new RecyclerListView.Holder(anonymousClass1);
        }
    }

    public class ShareSearchAdapter extends RecyclerListView.SelectionAdapter {
        DialogsSearchAdapter.CategoryAdapterRecycler categoryAdapter;
        RecyclerView categoryListView;
        private Context context;
        int itemsCount;
        private int lastGlobalSearchId;
        int lastItemCont;
        private int lastLocalSearchId;
        private int lastSearchId;
        private String lastSearchText;
        private SearchAdapterHelper searchAdapterHelper;
        private Runnable searchRunnable;
        private Runnable searchRunnable2;
        private ArrayList searchResult = new ArrayList();
        int hintsCell = -1;
        int resentTitleCell = -1;
        int firstEmptyViewCell = -1;
        int recentDialogsStartRow = -1;
        int searchResultsStartRow = -1;
        int lastFilledItem = -1;
        boolean internalDialogsIsSearching = false;

        public class AnonymousClass1 extends SearchAdapterHelper {
            final ShareAlert val$this$0;

            AnonymousClass1(boolean z, ShareAlert shareAlert) {
                super(z);
                r3 = shareAlert;
            }

            @Override
            protected boolean filter(TLObject tLObject) {
                return !(tLObject instanceof TLRPC.Chat) || ChatObject.canWriteToChat((TLRPC.Chat) tLObject);
            }
        }

        public class AnonymousClass2 implements SearchAdapterHelper.SearchAdapterHelperDelegate {
            final ShareAlert val$this$0;

            AnonymousClass2(ShareAlert shareAlert) {
                r2 = shareAlert;
            }

            @Override
            public boolean canApplySearchResults(int i) {
                return i == ShareSearchAdapter.this.lastSearchId;
            }

            @Override
            public LongSparseArray getExcludeCallParticipants() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
            }

            @Override
            public LongSparseArray getExcludeUsers() {
                return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
            }

            @Override
            public void onDataSetChanged(int i) {
                ShareSearchAdapter.this.lastGlobalSearchId = i;
                if (ShareSearchAdapter.this.lastLocalSearchId != i) {
                    ShareSearchAdapter.this.searchResult.clear();
                }
                ShareSearchAdapter shareSearchAdapter = ShareSearchAdapter.this;
                int i2 = shareSearchAdapter.lastItemCont;
                if (shareSearchAdapter.getItemCount() == 0 && !ShareSearchAdapter.this.searchAdapterHelper.isSearchInProgress()) {
                    ShareSearchAdapter shareSearchAdapter2 = ShareSearchAdapter.this;
                    if (!shareSearchAdapter2.internalDialogsIsSearching) {
                        ShareAlert.this.searchEmptyView.showProgress(false, true);
                        ShareSearchAdapter.this.notifyDataSetChanged();
                        ShareAlert.this.checkCurrentList(true);
                    }
                }
                ShareAlert.this.recyclerItemsEnterAnimator.showItemsAnimated(i2);
                ShareSearchAdapter.this.notifyDataSetChanged();
                ShareAlert.this.checkCurrentList(true);
            }

            @Override
            public void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
            }
        }

        class AnonymousClass3 extends RecyclerListView {
            AnonymousClass3(Context context, Theme.ResourcesProvider resourcesProvider) {
                super(context, resourcesProvider);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (getParent() != null && getParent().getParent() != null) {
                    ViewParent parent = getParent().getParent();
                    boolean z = true;
                    if (!canScrollHorizontally(-1) && !canScrollHorizontally(1)) {
                        z = false;
                    }
                    parent.requestDisallowInterceptTouchEvent(z);
                }
                return super.onInterceptTouchEvent(motionEvent);
            }
        }

        class AnonymousClass4 extends LinearLayoutManager {
            AnonymousClass4(Context context) {
                super(context);
            }

            @Override
            public boolean supportsPredictiveItemAnimations() {
                return false;
            }
        }

        class AnonymousClass5 extends DialogsSearchAdapter.CategoryAdapterRecycler {
            AnonymousClass5(Context context, int i, boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider) {
                super(context, i, z, z2, resourcesProvider);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                TLRPC.Chat chat;
                MessagesController messagesController;
                long j;
                HintDialogCell hintDialogCell = (HintDialogCell) viewHolder.itemView;
                if (ShareAlert.this.darkTheme || ShareAlert.this.forceDarkThemeForHint) {
                    hintDialogCell.setColors(Theme.key_voipgroup_nameText, Theme.key_voipgroup_inviteMembersBackground);
                }
                TLRPC.TL_topPeer tL_topPeer = MediaDataController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).hints.get(i);
                TLRPC.Peer peer = tL_topPeer.peer;
                long j2 = peer.user_id;
                TLRPC.User user = null;
                if (j2 != 0) {
                    user = MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).getUser(Long.valueOf(tL_topPeer.peer.user_id));
                    chat = null;
                } else {
                    long j3 = peer.channel_id;
                    if (j3 != 0) {
                        j2 = -j3;
                        messagesController = MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount);
                        j = tL_topPeer.peer.channel_id;
                    } else {
                        long j4 = peer.chat_id;
                        if (j4 != 0) {
                            j2 = -j4;
                            messagesController = MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount);
                            j = tL_topPeer.peer.chat_id;
                        } else {
                            chat = null;
                            j2 = 0;
                        }
                    }
                    chat = messagesController.getChat(Long.valueOf(j));
                }
                boolean z = j2 == hintDialogCell.getDialogId();
                hintDialogCell.setTag(Long.valueOf(j2));
                hintDialogCell.setDialog(j2, true, user != null ? UserObject.getFirstName(user) : chat != null ? chat.title : "");
                hintDialogCell.setChecked(ShareAlert.this.selectedDialogs.indexOfKey(j2) >= 0, z);
            }
        }

        class AnonymousClass6 extends View {
            AnonymousClass6(Context context) {
                super(context);
            }

            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(ShareAlert.this.searchLayoutManager.lastItemHeight, 1073741824));
            }
        }

        public ShareSearchAdapter(Context context) {
            this.context = context;
            AnonymousClass1 anonymousClass1 = new SearchAdapterHelper(false) {
                final ShareAlert val$this$0;

                AnonymousClass1(boolean z, ShareAlert shareAlert) {
                    super(z);
                    r3 = shareAlert;
                }

                @Override
                protected boolean filter(TLObject tLObject) {
                    return !(tLObject instanceof TLRPC.Chat) || ChatObject.canWriteToChat((TLRPC.Chat) tLObject);
                }
            };
            this.searchAdapterHelper = anonymousClass1;
            anonymousClass1.setDelegate(new SearchAdapterHelper.SearchAdapterHelperDelegate() {
                final ShareAlert val$this$0;

                AnonymousClass2(ShareAlert shareAlert) {
                    r2 = shareAlert;
                }

                @Override
                public boolean canApplySearchResults(int i) {
                    return i == ShareSearchAdapter.this.lastSearchId;
                }

                @Override
                public LongSparseArray getExcludeCallParticipants() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeCallParticipants(this);
                }

                @Override
                public LongSparseArray getExcludeUsers() {
                    return SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$getExcludeUsers(this);
                }

                @Override
                public void onDataSetChanged(int i) {
                    ShareSearchAdapter.this.lastGlobalSearchId = i;
                    if (ShareSearchAdapter.this.lastLocalSearchId != i) {
                        ShareSearchAdapter.this.searchResult.clear();
                    }
                    ShareSearchAdapter shareSearchAdapter = ShareSearchAdapter.this;
                    int i2 = shareSearchAdapter.lastItemCont;
                    if (shareSearchAdapter.getItemCount() == 0 && !ShareSearchAdapter.this.searchAdapterHelper.isSearchInProgress()) {
                        ShareSearchAdapter shareSearchAdapter2 = ShareSearchAdapter.this;
                        if (!shareSearchAdapter2.internalDialogsIsSearching) {
                            ShareAlert.this.searchEmptyView.showProgress(false, true);
                            ShareSearchAdapter.this.notifyDataSetChanged();
                            ShareAlert.this.checkCurrentList(true);
                        }
                    }
                    ShareAlert.this.recyclerItemsEnterAnimator.showItemsAnimated(i2);
                    ShareSearchAdapter.this.notifyDataSetChanged();
                    ShareAlert.this.checkCurrentList(true);
                }

                @Override
                public void onSetHashtags(ArrayList arrayList, HashMap hashMap) {
                    SearchAdapterHelper.SearchAdapterHelperDelegate.CC.$default$onSetHashtags(this, arrayList, hashMap);
                }
            });
        }

        public void lambda$onCreateViewHolder$5(View view, int i) {
            HintDialogCell hintDialogCell = (HintDialogCell) view;
            TLRPC.TL_topPeer tL_topPeer = MediaDataController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).hints.get(i);
            TLRPC.TL_dialog tL_dialog = new TLRPC.TL_dialog();
            TLRPC.Peer peer = tL_topPeer.peer;
            long j = peer.user_id;
            if (j == 0) {
                long j2 = peer.channel_id;
                if (j2 == 0) {
                    j2 = peer.chat_id;
                    if (j2 == 0) {
                        j = 0;
                    }
                }
                j = -j2;
            }
            if (hintDialogCell.isBlocked()) {
                ShareAlert.this.showPremiumBlockedToast(hintDialogCell, j);
                return;
            }
            tL_dialog.id = j;
            ShareAlert.this.selectDialog(null, tL_dialog);
            hintDialogCell.setChecked(ShareAlert.this.selectedDialogs.indexOfKey(j) >= 0, true);
        }

        public void lambda$searchDialogs$3(int i, String str) {
            this.searchRunnable2 = null;
            if (i != this.lastSearchId) {
                return;
            }
            this.searchAdapterHelper.queryServerSearch(str, true, true, true, true, false, 0L, false, 0, i);
        }

        public void lambda$searchDialogs$4(final String str, final int i) {
            this.searchRunnable = null;
            searchDialogsInternal(str, i);
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    ShareAlert.ShareSearchAdapter.this.lambda$searchDialogs$3(i, str);
                }
            };
            this.searchRunnable2 = runnable;
            AndroidUtilities.runOnUIThread(runnable);
        }

        public static int lambda$searchDialogsInternal$0(Object obj, Object obj2) {
            int i = ((DialogSearchResult) obj).date;
            int i2 = ((DialogSearchResult) obj2).date;
            if (i < i2) {
                return 1;
            }
            return i > i2 ? -1 : 0;
        }

        public void lambda$searchDialogsInternal$1(java.lang.String r22, int r23) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.ShareSearchAdapter.lambda$searchDialogsInternal$1(java.lang.String, int):void");
        }

        public void lambda$updateSearchResults$2(int i, ArrayList arrayList) {
            if (i != this.lastSearchId) {
                return;
            }
            getItemCount();
            this.internalDialogsIsSearching = false;
            this.lastLocalSearchId = i;
            if (this.lastGlobalSearchId != i) {
                this.searchAdapterHelper.clear();
            }
            if (ShareAlert.this.gridView.getAdapter() != ShareAlert.this.searchAdapter) {
                ShareAlert shareAlert = ShareAlert.this;
                shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                ShareAlert.this.searchAdapter.notifyDataSetChanged();
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                TLObject tLObject = ((DialogSearchResult) arrayList.get(i2)).object;
                if (tLObject instanceof TLRPC.User) {
                    MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).putUser((TLRPC.User) tLObject, true);
                } else if (tLObject instanceof TLRPC.Chat) {
                    MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).putChat((TLRPC.Chat) tLObject, true);
                }
            }
            boolean z = !this.searchResult.isEmpty() && arrayList.isEmpty();
            if (this.searchResult.isEmpty()) {
                arrayList.isEmpty();
            }
            if (z) {
                ShareAlert shareAlert2 = ShareAlert.this;
                shareAlert2.topBeforeSwitch = shareAlert2.getCurrentTop();
            }
            this.searchResult = arrayList;
            this.searchAdapterHelper.mergeResults(arrayList, null);
            int i3 = this.lastItemCont;
            if (getItemCount() != 0 || this.searchAdapterHelper.isSearchInProgress() || this.internalDialogsIsSearching) {
                ShareAlert.this.recyclerItemsEnterAnimator.showItemsAnimated(i3);
            } else {
                ShareAlert.this.searchEmptyView.showProgress(false, true);
            }
            notifyDataSetChanged();
            ShareAlert.this.checkCurrentList(true);
        }

        private void searchDialogsInternal(final String str, final int i) {
            MessagesStorage.getInstance(((BottomSheet) ShareAlert.this).currentAccount).getStorageQueue().postRunnable(new Runnable() {
                @Override
                public final void run() {
                    ShareAlert.ShareSearchAdapter.this.lambda$searchDialogsInternal$1(str, i);
                }
            });
        }

        private void updateSearchResults(final ArrayList arrayList, final int i) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ShareAlert.ShareSearchAdapter.this.lambda$updateSearchResults$2(i, arrayList);
                }
            });
        }

        public TLRPC.Dialog getItem(int i) {
            long j;
            long j2;
            int i2 = this.recentDialogsStartRow;
            if (i >= i2 && i2 >= 0) {
                int i3 = i - i2;
                if (i3 >= 0 && i3 < ShareAlert.this.recentSearchObjects.size()) {
                    TLObject tLObject = ((DialogsSearchAdapter.RecentSearchObject) ShareAlert.this.recentSearchObjects.get(i3)).object;
                    TLRPC.TL_dialog tL_dialog = new TLRPC.TL_dialog();
                    if (tLObject instanceof TLRPC.User) {
                        j2 = ((TLRPC.User) tLObject).id;
                    } else if (tLObject instanceof TLRPC.Chat) {
                        j2 = -((TLRPC.Chat) tLObject).id;
                    }
                    tL_dialog.id = j2;
                    return tL_dialog;
                }
                return null;
            }
            int i4 = i - 1;
            if (i4 < 0) {
                return null;
            }
            if (i4 < this.searchResult.size()) {
                return ((DialogSearchResult) this.searchResult.get(i4)).dialog;
            }
            int size = i4 - this.searchResult.size();
            ArrayList localServerSearch = this.searchAdapterHelper.getLocalServerSearch();
            if (size < localServerSearch.size()) {
                TLObject tLObject2 = (TLObject) localServerSearch.get(size);
                TLRPC.TL_dialog tL_dialog2 = new TLRPC.TL_dialog();
                if (tLObject2 instanceof TLRPC.User) {
                    j = ((TLRPC.User) tLObject2).id;
                } else if (tLObject2 instanceof TLRPC.Chat) {
                    j = -((TLRPC.Chat) tLObject2).id;
                }
                tL_dialog2.id = j;
                return tL_dialog2;
            }
            return null;
        }

        @Override
        public int getItemCount() {
            int i = 0;
            this.itemsCount = 0;
            this.hintsCell = -1;
            this.resentTitleCell = -1;
            this.recentDialogsStartRow = -1;
            this.searchResultsStartRow = -1;
            this.lastFilledItem = -1;
            if (!TextUtils.isEmpty(this.lastSearchText)) {
                int i2 = this.itemsCount;
                int i3 = i2 + 1;
                this.itemsCount = i3;
                this.firstEmptyViewCell = i2;
                this.searchResultsStartRow = i3;
                int size = i3 + this.searchResult.size() + this.searchAdapterHelper.getLocalServerSearch().size();
                this.itemsCount = size;
                if (size == 1) {
                    this.firstEmptyViewCell = -1;
                    this.itemsCount = 0;
                } else {
                    i = size + 1;
                    this.itemsCount = i;
                    this.lastFilledItem = size;
                }
                this.lastItemCont = i;
                return i;
            }
            int i4 = this.itemsCount;
            this.firstEmptyViewCell = i4;
            this.itemsCount = i4 + 2;
            this.hintsCell = i4 + 1;
            if (ShareAlert.this.recentSearchObjects.size() > 0) {
                int i5 = this.itemsCount;
                int i6 = i5 + 1;
                this.itemsCount = i6;
                this.resentTitleCell = i5;
                this.recentDialogsStartRow = i6;
                this.itemsCount = i6 + ShareAlert.this.recentSearchObjects.size();
            }
            int i7 = this.itemsCount;
            int i8 = i7 + 1;
            this.itemsCount = i8;
            this.lastFilledItem = i7;
            this.lastItemCont = i8;
            return i8;
        }

        @Override
        public int getItemViewType(int i) {
            if (i == this.lastFilledItem) {
                return 4;
            }
            if (i == this.firstEmptyViewCell) {
                return 1;
            }
            if (i == this.hintsCell) {
                return 2;
            }
            if (i == this.resentTitleCell) {
                return 3;
            }
            return TextUtils.isEmpty(this.lastSearchText) ? 0 : 5;
        }

        public int getSpanSize(int i, int i2) {
            if (i2 == this.hintsCell || i2 == this.resentTitleCell || i2 == this.firstEmptyViewCell || i2 == this.lastFilledItem || getItemViewType(i2) == 0) {
                return i;
            }
            return 1;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return (viewHolder.getItemViewType() == 1 || viewHolder.getItemViewType() == 4) ? false : true;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            long j;
            String str;
            String str2;
            long j2;
            int indexOfIgnoreCase;
            TLObject tLObject;
            int indexOfIgnoreCase2;
            if (viewHolder.getItemViewType() != 0 && viewHolder.getItemViewType() != 5) {
                if (viewHolder.getItemViewType() == 2) {
                    ((RecyclerListView) viewHolder.itemView).getAdapter().notifyDataSetChanged();
                    return;
                }
                return;
            }
            TLObject tLObject2 = null;
            r6 = null;
            r6 = null;
            String str3 = null;
            TLRPC.TL_encryptedChat tL_encryptedChat = null;
            if (!TextUtils.isEmpty(this.lastSearchText)) {
                int i2 = i - 1;
                if (i2 < this.searchResult.size()) {
                    DialogSearchResult dialogSearchResult = (DialogSearchResult) this.searchResult.get(i2);
                    j2 = dialogSearchResult.dialog.id;
                    str2 = dialogSearchResult.name;
                } else {
                    i2 -= this.searchResult.size();
                    tLObject2 = (TLObject) this.searchAdapterHelper.getLocalServerSearch().get(i2);
                    if (tLObject2 instanceof TLRPC.User) {
                        TLRPC.User user = (TLRPC.User) tLObject2;
                        j = user.id;
                        str = ContactsController.formatName(user.first_name, user.last_name);
                    } else {
                        TLRPC.Chat chat = (TLRPC.Chat) tLObject2;
                        j = -chat.id;
                        str = chat.title;
                    }
                    String lastFoundUsername = this.searchAdapterHelper.getLastFoundUsername();
                    if (TextUtils.isEmpty(lastFoundUsername) || str == null || (indexOfIgnoreCase = AndroidUtilities.indexOfIgnoreCase(str.toString(), lastFoundUsername)) == -1) {
                        str2 = str;
                    } else {
                        ?? spannableStringBuilder = new SpannableStringBuilder(str);
                        spannableStringBuilder.setSpan(new ForegroundColorSpanThemable(Theme.key_windowBackgroundWhiteBlueText4, ((BottomSheet) ShareAlert.this).resourcesProvider), indexOfIgnoreCase, lastFoundUsername.length() + indexOfIgnoreCase, 33);
                        str2 = spannableStringBuilder;
                    }
                    j2 = j;
                }
                TLObject tLObject3 = tLObject2;
                View view = viewHolder.itemView;
                if (view instanceof ProfileSearchCell) {
                    ((ProfileSearchCell) view).setData(tLObject3, null, str2, null, false, false);
                    ((ProfileSearchCell) viewHolder.itemView).useSeparator = i2 < getItemCount() - 2;
                    return;
                } else {
                    if (view instanceof ShareDialogCell) {
                        ((ShareDialogCell) view).setDialog(j2, ShareAlert.this.selectedDialogs.indexOfKey(j2) >= 0, str2);
                        return;
                    }
                    return;
                }
            }
            int i3 = this.recentDialogsStartRow;
            long j3 = 0;
            if (i3 < 0 || i < i3) {
                tLObject = null;
            } else {
                TLObject tLObject4 = ((DialogsSearchAdapter.RecentSearchObject) ShareAlert.this.recentSearchObjects.get(i - i3)).object;
                if (tLObject4 instanceof TLRPC.User) {
                    TLRPC.User user2 = (TLRPC.User) tLObject4;
                    j3 = user2.id;
                    str3 = ContactsController.formatName(user2.first_name, user2.last_name);
                } else if (tLObject4 instanceof TLRPC.Chat) {
                    TLRPC.Chat chat2 = (TLRPC.Chat) tLObject4;
                    j3 = -chat2.id;
                    str3 = chat2.title;
                } else if (tLObject4 instanceof TLRPC.TL_encryptedChat) {
                    tL_encryptedChat = (TLRPC.TL_encryptedChat) tLObject4;
                    TLRPC.User user3 = MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).getUser(Long.valueOf(tL_encryptedChat.user_id));
                    if (user3 != null) {
                        j3 = user3.id;
                        str3 = ContactsController.formatName(user3.first_name, user3.last_name);
                    }
                }
                String lastFoundUsername2 = this.searchAdapterHelper.getLastFoundUsername();
                if (TextUtils.isEmpty(lastFoundUsername2) || str3 == null || (indexOfIgnoreCase2 = AndroidUtilities.indexOfIgnoreCase(str3.toString(), lastFoundUsername2)) == -1) {
                    tLObject = tLObject4;
                } else {
                    ?? spannableStringBuilder2 = new SpannableStringBuilder(str3);
                    spannableStringBuilder2.setSpan(new ForegroundColorSpanThemable(Theme.key_windowBackgroundWhiteBlueText4, ((BottomSheet) ShareAlert.this).resourcesProvider), indexOfIgnoreCase2, lastFoundUsername2.length() + indexOfIgnoreCase2, 33);
                    tLObject = tLObject4;
                    str3 = spannableStringBuilder2;
                }
            }
            TLRPC.TL_encryptedChat tL_encryptedChat2 = tL_encryptedChat;
            View view2 = viewHolder.itemView;
            if (view2 instanceof ProfileSearchCell) {
                ((ProfileSearchCell) view2).setData(tLObject, tL_encryptedChat2, str3, null, false, false);
                ((ProfileSearchCell) viewHolder.itemView).useSeparator = i < getItemCount() - 2;
            } else if (view2 instanceof ShareDialogCell) {
                ((ShareDialogCell) view2).setDialog(j3, ShareAlert.this.selectedDialogs.indexOfKey(j3) >= 0, str3);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            if (i == 0) {
                view = new ProfileSearchCell(this.context, ((BottomSheet) ShareAlert.this).resourcesProvider).useCustomPaints().showPremiumBlock(true);
            } else if (i == 2) {
                AnonymousClass3 anonymousClass3 = new RecyclerListView(this.context, ((BottomSheet) ShareAlert.this).resourcesProvider) {
                    AnonymousClass3(Context context, Theme.ResourcesProvider resourcesProvider) {
                        super(context, resourcesProvider);
                    }

                    @Override
                    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                        if (getParent() != null && getParent().getParent() != null) {
                            ViewParent parent = getParent().getParent();
                            boolean z = true;
                            if (!canScrollHorizontally(-1) && !canScrollHorizontally(1)) {
                                z = false;
                            }
                            parent.requestDisallowInterceptTouchEvent(z);
                        }
                        return super.onInterceptTouchEvent(motionEvent);
                    }
                };
                this.categoryListView = anonymousClass3;
                anonymousClass3.setItemAnimator(null);
                anonymousClass3.setLayoutAnimation(null);
                AnonymousClass4 anonymousClass4 = new LinearLayoutManager(this.context) {
                    AnonymousClass4(Context context) {
                        super(context);
                    }

                    @Override
                    public boolean supportsPredictiveItemAnimations() {
                        return false;
                    }
                };
                anonymousClass4.setOrientation(0);
                anonymousClass3.setLayoutManager(anonymousClass4);
                AnonymousClass5 anonymousClass5 = new DialogsSearchAdapter.CategoryAdapterRecycler(this.context, ((BottomSheet) ShareAlert.this).currentAccount, true, true, ((BottomSheet) ShareAlert.this).resourcesProvider) {
                    AnonymousClass5(Context context, int i2, boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider) {
                        super(context, i2, z, z2, resourcesProvider);
                    }

                    @Override
                    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i2) {
                        TLRPC.Chat chat;
                        MessagesController messagesController;
                        long j;
                        HintDialogCell hintDialogCell = (HintDialogCell) viewHolder.itemView;
                        if (ShareAlert.this.darkTheme || ShareAlert.this.forceDarkThemeForHint) {
                            hintDialogCell.setColors(Theme.key_voipgroup_nameText, Theme.key_voipgroup_inviteMembersBackground);
                        }
                        TLRPC.TL_topPeer tL_topPeer = MediaDataController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).hints.get(i2);
                        TLRPC.Peer peer = tL_topPeer.peer;
                        long j2 = peer.user_id;
                        TLRPC.User user = null;
                        if (j2 != 0) {
                            user = MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).getUser(Long.valueOf(tL_topPeer.peer.user_id));
                            chat = null;
                        } else {
                            long j3 = peer.channel_id;
                            if (j3 != 0) {
                                j2 = -j3;
                                messagesController = MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount);
                                j = tL_topPeer.peer.channel_id;
                            } else {
                                long j4 = peer.chat_id;
                                if (j4 != 0) {
                                    j2 = -j4;
                                    messagesController = MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount);
                                    j = tL_topPeer.peer.chat_id;
                                } else {
                                    chat = null;
                                    j2 = 0;
                                }
                            }
                            chat = messagesController.getChat(Long.valueOf(j));
                        }
                        boolean z = j2 == hintDialogCell.getDialogId();
                        hintDialogCell.setTag(Long.valueOf(j2));
                        hintDialogCell.setDialog(j2, true, user != null ? UserObject.getFirstName(user) : chat != null ? chat.title : "");
                        hintDialogCell.setChecked(ShareAlert.this.selectedDialogs.indexOfKey(j2) >= 0, z);
                    }
                };
                this.categoryAdapter = anonymousClass5;
                anonymousClass3.setAdapter(anonymousClass5);
                anonymousClass3.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                    @Override
                    public final void onItemClick(View view2, int i2) {
                        ShareAlert.ShareSearchAdapter.this.lambda$onCreateViewHolder$5(view2, i2);
                    }
                });
                view = anonymousClass3;
            } else if (i == 3) {
                GraySectionCell graySectionCell = new GraySectionCell(this.context, ((BottomSheet) ShareAlert.this).resourcesProvider);
                graySectionCell.setTextColor(Theme.key_graySectionText);
                graySectionCell.setBackgroundColor(ShareAlert.this.getThemedColor(Theme.key_graySection));
                graySectionCell.setText(LocaleController.getString(R.string.Recent));
                view = graySectionCell;
            } else if (i == 4) {
                view = new View(this.context) {
                    AnonymousClass6(Context context) {
                        super(context);
                    }

                    @Override
                    protected void onMeasure(int i2, int i22) {
                        super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(ShareAlert.this.searchLayoutManager.lastItemHeight, 1073741824));
                    }
                };
            } else if (i != 5) {
                View view2 = new View(this.context);
                view2.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 56.0f : 109.0f)));
                view = view2;
            } else {
                View shareDialogCell = new ShareDialogCell(this.context, 0, ((BottomSheet) ShareAlert.this).resourcesProvider);
                shareDialogCell.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0f)));
                view = shareDialogCell;
            }
            return new RecyclerListView.Holder(view);
        }

        public void searchDialogs(final String str) {
            if (str == null || !str.equals(this.lastSearchText)) {
                this.lastSearchText = str;
                if (this.searchRunnable != null) {
                    Utilities.searchQueue.cancelRunnable(this.searchRunnable);
                    this.searchRunnable = null;
                }
                Runnable runnable = this.searchRunnable2;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                    this.searchRunnable2 = null;
                }
                this.searchResult.clear();
                this.searchAdapterHelper.mergeResults(null);
                this.searchAdapterHelper.queryServerSearch(null, true, true, true, true, false, 0L, false, 0, 0);
                notifyDataSetChanged();
                ShareAlert.this.checkCurrentList(true);
                if (TextUtils.isEmpty(str)) {
                    ShareAlert shareAlert = ShareAlert.this;
                    shareAlert.topBeforeSwitch = shareAlert.getCurrentTop();
                    this.lastSearchId = -1;
                    this.internalDialogsIsSearching = false;
                } else {
                    this.internalDialogsIsSearching = true;
                    final int i = this.lastSearchId + 1;
                    this.lastSearchId = i;
                    ShareAlert.this.searchEmptyView.showProgress(true, true);
                    DispatchQueue dispatchQueue = Utilities.searchQueue;
                    Runnable runnable2 = new Runnable() {
                        @Override
                        public final void run() {
                            ShareAlert.ShareSearchAdapter.this.lambda$searchDialogs$4(str, i);
                        }
                    };
                    this.searchRunnable = runnable2;
                    dispatchQueue.postRunnable(runnable2, 300L);
                }
                ShareAlert.this.checkCurrentList(false);
            }
        }
    }

    public class ShareTopicsAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private List topics;

        public ShareTopicsAdapter(Context context) {
            this.context = context;
        }

        public TLRPC.TL_forumTopic getItem(int i) {
            int i2 = i - 1;
            List list = this.topics;
            if (list == null || i2 < 0 || i2 >= list.size()) {
                return null;
            }
            return (TLRPC.TL_forumTopic) this.topics.get(i2);
        }

        @Override
        public int getItemCount() {
            List list = this.topics;
            if (list == null) {
                return 0;
            }
            return list.size() + 1;
        }

        @Override
        public int getItemViewType(int i) {
            return i == 0 ? 1 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 1;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (viewHolder.getItemViewType() == 0) {
                ShareTopicCell shareTopicCell = (ShareTopicCell) viewHolder.itemView;
                TLRPC.TL_forumTopic item = getItem(i);
                shareTopicCell.setTopic(ShareAlert.this.selectedTopicDialog, item, ShareAlert.this.selectedDialogs.indexOfKey((long) item.id) >= 0, null);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View shareTopicCell;
            RecyclerView.LayoutParams layoutParams;
            if (i != 0) {
                shareTopicCell = new View(this.context);
                layoutParams = new RecyclerView.LayoutParams(-1, ActionBar.getCurrentActionBarHeight());
            } else {
                shareTopicCell = new ShareTopicCell(this.context, ((BottomSheet) ShareAlert.this).resourcesProvider);
                layoutParams = new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(100.0f));
            }
            shareTopicCell.setLayoutParams(layoutParams);
            return new RecyclerListView.Holder(shareTopicCell);
        }
    }

    public class SwitchView extends FrameLayout {
        private AnimatorSet animator;
        private int currentTab;
        private int lastColor;
        private SimpleTextView leftTab;
        private LinearGradient linearGradient;
        private Paint paint;
        private RectF rect;
        private SimpleTextView rightTab;
        private View searchBackground;
        private View slidingView;

        public class AnonymousClass1 extends View {
            final ShareAlert val$this$0;

            AnonymousClass1(Context context, ShareAlert shareAlert) {
                super(context);
                r3 = shareAlert;
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                int offsetColor = AndroidUtilities.getOffsetColor(-9057429, -10513163, getTranslationX() / getMeasuredWidth(), 1.0f);
                int offsetColor2 = AndroidUtilities.getOffsetColor(-11554882, -4629871, getTranslationX() / getMeasuredWidth(), 1.0f);
                if (offsetColor != SwitchView.this.lastColor) {
                    SwitchView.this.linearGradient = new LinearGradient(0.0f, 0.0f, getMeasuredWidth(), 0.0f, new int[]{offsetColor, offsetColor2}, (float[]) null, Shader.TileMode.CLAMP);
                    SwitchView.this.paint.setShader(SwitchView.this.linearGradient);
                }
                SwitchView.this.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                canvas.drawRoundRect(SwitchView.this.rect, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), SwitchView.this.paint);
            }

            @Override
            public void setTranslationX(float f) {
                super.setTranslationX(f);
                invalidate();
            }
        }

        public class AnonymousClass2 extends AnimatorListenerAdapter {
            AnonymousClass2() {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                SwitchView.this.animator = null;
            }
        }

        public SwitchView(Context context) {
            super(context);
            this.paint = new Paint(1);
            this.rect = new RectF();
            View view = new View(context);
            this.searchBackground = view;
            view.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(18.0f), ShareAlert.this.getThemedColor(Theme.key_dialogSearchBackground)));
            addView(this.searchBackground, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 0.0f, 14.0f, 0.0f));
            AnonymousClass1 anonymousClass1 = new View(context) {
                final ShareAlert val$this$0;

                AnonymousClass1(Context context2, ShareAlert shareAlert) {
                    super(context2);
                    r3 = shareAlert;
                }

                @Override
                protected void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    int offsetColor = AndroidUtilities.getOffsetColor(-9057429, -10513163, getTranslationX() / getMeasuredWidth(), 1.0f);
                    int offsetColor2 = AndroidUtilities.getOffsetColor(-11554882, -4629871, getTranslationX() / getMeasuredWidth(), 1.0f);
                    if (offsetColor != SwitchView.this.lastColor) {
                        SwitchView.this.linearGradient = new LinearGradient(0.0f, 0.0f, getMeasuredWidth(), 0.0f, new int[]{offsetColor, offsetColor2}, (float[]) null, Shader.TileMode.CLAMP);
                        SwitchView.this.paint.setShader(SwitchView.this.linearGradient);
                    }
                    SwitchView.this.rect.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                    canvas.drawRoundRect(SwitchView.this.rect, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), SwitchView.this.paint);
                }

                @Override
                public void setTranslationX(float f) {
                    super.setTranslationX(f);
                    invalidate();
                }
            };
            this.slidingView = anonymousClass1;
            addView(anonymousClass1, LayoutHelper.createFrame(-1, 36.0f, 51, 14.0f, 0.0f, 14.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context2);
            this.leftTab = simpleTextView;
            int i = Theme.key_voipgroup_nameText;
            simpleTextView.setTextColor(ShareAlert.this.getThemedColor(i));
            this.leftTab.setTextSize(13);
            this.leftTab.setLeftDrawable(R.drawable.msg_tabs_mic1);
            this.leftTab.setText(LocaleController.getString(R.string.VoipGroupInviteCanSpeak));
            this.leftTab.setGravity(17);
            addView(this.leftTab, LayoutHelper.createFrame(-1, -1.0f, 51, 14.0f, 0.0f, 0.0f, 0.0f));
            this.leftTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    ShareAlert.SwitchView.this.lambda$new$0(view2);
                }
            });
            SimpleTextView simpleTextView2 = new SimpleTextView(context2);
            this.rightTab = simpleTextView2;
            simpleTextView2.setTextColor(ShareAlert.this.getThemedColor(i));
            this.rightTab.setTextSize(13);
            this.rightTab.setLeftDrawable(R.drawable.msg_tabs_mic2);
            this.rightTab.setText(LocaleController.getString(R.string.VoipGroupInviteListenOnly));
            this.rightTab.setGravity(17);
            addView(this.rightTab, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 14.0f, 0.0f));
            this.rightTab.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    ShareAlert.SwitchView.this.lambda$new$1(view2);
                }
            });
        }

        public void lambda$new$0(View view) {
            switchToTab(0);
        }

        public void lambda$new$1(View view) {
            switchToTab(1);
        }

        private void switchToTab(int i) {
            if (this.currentTab == i) {
                return;
            }
            this.currentTab = i;
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.animator = animatorSet2;
            animatorSet2.playTogether(ObjectAnimator.ofFloat(this.slidingView, (Property<View, Float>) View.TRANSLATION_X, this.currentTab == 0 ? 0.0f : r2.getMeasuredWidth()));
            this.animator.setDuration(180L);
            this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.animator.addListener(new AnimatorListenerAdapter() {
                AnonymousClass2() {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    SwitchView.this.animator = null;
                }
            });
            this.animator.start();
            onTabSwitch(this.currentTab);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int size = (View.MeasureSpec.getSize(i) - AndroidUtilities.dp(28.0f)) / 2;
            ((FrameLayout.LayoutParams) this.leftTab.getLayoutParams()).width = size;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.rightTab.getLayoutParams();
            layoutParams.width = size;
            layoutParams.leftMargin = AndroidUtilities.dp(14.0f) + size;
            ((FrameLayout.LayoutParams) this.slidingView.getLayoutParams()).width = size;
            AnimatorSet animatorSet = this.animator;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.slidingView.setTranslationX(this.currentTab == 0 ? 0.0f : r1.width);
            super.onMeasure(i, i2);
        }

        protected abstract void onTabSwitch(int i);
    }

    public ShareAlert(Context context, ArrayList arrayList, String str, boolean z, String str2, boolean z2) {
        this(context, arrayList, str, z, str2, z2, null);
    }

    public ShareAlert(Context context, ArrayList arrayList, String str, boolean z, String str2, boolean z2, Theme.ResourcesProvider resourcesProvider) {
        this(context, null, arrayList, str, null, z, str2, null, z2, false, false, null, resourcesProvider);
    }

    public ShareAlert(Context context, ChatActivity chatActivity, ArrayList arrayList, String str, String str2, boolean z, String str3, String str4, boolean z2, boolean z3) {
        this(context, chatActivity, arrayList, str, str2, z, str3, str4, z2, z3, false, null, null);
    }

    public ShareAlert(final Context context, ChatActivity chatActivity, ArrayList arrayList, String str, String str2, boolean z, String str3, String str4, boolean z2, boolean z3, boolean z4, Integer num, Theme.ResourcesProvider resourcesProvider) {
        super(context, true, resourcesProvider);
        float f;
        float f2;
        int i;
        this.sendingText = new String[2];
        this.shadow = new View[2];
        this.shadowAnimation = new AnimatorSet[2];
        this.selectedDialogs = new LongSparseArray();
        this.selectedDialogTopics = new HashMap();
        this.containerViewTop = -1;
        this.fullyShown = false;
        this.rect = new RectF();
        this.paint = new Paint(1);
        this.textPaint = new TextPaint(1);
        this.linkToCopy = new String[2];
        this.recentSearchObjects = new ArrayList();
        this.recentSearchObjectsById = new LongSparseArray();
        this.shiftDp = 4;
        this.showSendersName = true;
        this.lastOffset = Integer.MAX_VALUE;
        this.resourcesProvider = resourcesProvider;
        this.includeStory = z4;
        this.parentActivity = AndroidUtilities.findActivity(context);
        this.darkTheme = z3;
        if (z3) {
            this.resourcesProvider = new DarkThemeResourceProvider() {
                AnonymousClass1() {
                }

                @Override
                public void appendColors() {
                    SparseIntArray sparseIntArray = this.sparseIntArray;
                    int i2 = Theme.key_windowBackgroundGray;
                    int i22 = Theme.key_dialogBackground;
                    sparseIntArray.put(i2, getColor(i22));
                    this.sparseIntArray.put(Theme.key_divider, -15264235);
                    this.sparseIntArray.put(Theme.key_chat_messagePanelIcons, Theme.multAlpha(-1, 0.45f));
                    this.sparseIntArray.put(Theme.key_dialogBackgroundGray, 352321535);
                    this.sparseIntArray.put(Theme.key_chat_emojiPanelBackground, getColor(i22));
                }
            };
        }
        this.parentFragment = chatActivity;
        this.shadowDrawable = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        int i2 = Theme.key_dialogBackground;
        this.behindKeyboardColorKey = i2;
        int themedColor = getThemedColor(i2);
        this.shadowDrawable.setColorFilter(new PorterDuffColorFilter(themedColor, PorterDuff.Mode.MULTIPLY));
        this.occupyNavigationBarWithoutKeyboard = true;
        this.isFullscreen = z2;
        String[] strArr = this.linkToCopy;
        strArr[0] = str3;
        strArr[1] = str4;
        this.sendingMessageObjects = arrayList;
        this.searchAdapter = new ShareSearchAdapter(context);
        this.isChannel = z;
        String[] strArr2 = this.sendingText;
        strArr2[0] = str;
        strArr2[1] = str2;
        this.useSmoothKeyboard = true;
        super.setDelegate(new BottomSheet.BottomSheetDelegate() {
            AnonymousClass2() {
            }

            @Override
            public void onOpenAnimationEnd() {
                ShareAlert.this.fullyShown = true;
            }
        });
        ArrayList arrayList2 = this.sendingMessageObjects;
        if (arrayList2 != null) {
            int size = arrayList2.size();
            for (int i3 = 0; i3 < size; i3++) {
                MessageObject messageObject = (MessageObject) this.sendingMessageObjects.get(i3);
                if (messageObject.isPoll()) {
                    int i4 = messageObject.isPublicPoll() ? 2 : 1;
                    this.hasPoll = i4;
                    if (i4 == 2) {
                        break;
                    }
                }
            }
        }
        if (z) {
            this.loadingLink = true;
            TLRPC.TL_channels_exportMessageLink tL_channels_exportMessageLink = new TLRPC.TL_channels_exportMessageLink();
            tL_channels_exportMessageLink.id = ((MessageObject) arrayList.get(0)).getId();
            tL_channels_exportMessageLink.channel = MessagesController.getInstance(this.currentAccount).getInputChannel(((MessageObject) arrayList.get(0)).messageOwner.peer_id.channel_id);
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_exportMessageLink, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    ShareAlert.this.lambda$new$1(context, tLObject, tL_error);
                }
            });
        }
        AnonymousClass3 anonymousClass3 = new SizeNotifierFrameLayout(context) {
            private int fromOffsetTop;
            private int fromScrollY;
            private boolean fullHeight;
            private boolean lightStatusBar;
            private final AnimatedFloat pinnedToTop;
            private int previousTopOffset;
            private int toOffsetTop;
            private int toScrollY;
            private int topOffset;
            private boolean ignoreLayout = false;
            private RectF rect1 = new RectF();

            public class AnonymousClass1 extends AdjustPanLayoutHelper {
                AnonymousClass1(View this) {
                    super(this);
                }

                @Override
                protected boolean heightAnimationEnabled() {
                    if (ShareAlert.this.isDismissed() || !ShareAlert.this.fullyShown) {
                        return false;
                    }
                    return !ShareAlert.this.commentTextView.isPopupVisible();
                }

                @Override
                public void onPanTranslationUpdate(float f, float f2, boolean z) {
                    ShareAlert.this.keyboardT = f2;
                    super.onPanTranslationUpdate(f, f2, z);
                    for (int i = 0; i < ((BottomSheet) ShareAlert.this).containerView.getChildCount(); i++) {
                        View childAt = ((BottomSheet) ShareAlert.this).containerView.getChildAt(i);
                        if (childAt != ShareAlert.this.pickerBottom && childAt != ShareAlert.this.bulletinContainer && childAt != ShareAlert.this.shadow[1] && childAt != ShareAlert.this.sharesCountLayout && childAt != ShareAlert.this.frameLayout2) {
                            ShareAlert shareAlert = ShareAlert.this;
                            if (childAt != shareAlert.timestampFrameLayout && childAt != shareAlert.writeButtonContainer && childAt != ShareAlert.this.selectedCountView && childAt != ShareAlert.this.navigationBar) {
                                childAt.setTranslationY(f);
                            }
                        }
                    }
                    ShareAlert.this.currentPanTranslationY = f;
                    if (AnonymousClass3.this.fromScrollY != -1) {
                        if (!z) {
                            f2 = 1.0f - f2;
                        }
                        float f3 = 1.0f - f2;
                        ShareAlert.this.scrollOffsetY = (int) ((r5.fromScrollY * f3) + (AnonymousClass3.this.toScrollY * f2));
                        float f4 = ShareAlert.this.currentPanTranslationY + ((AnonymousClass3.this.fromScrollY - AnonymousClass3.this.toScrollY) * f3);
                        ShareAlert.this.gridView.setTranslationY(f4);
                        RecyclerListView recyclerListView = ShareAlert.this.searchGridView;
                        if (!z) {
                            f4 += ShareAlert.this.gridView.getPaddingTop();
                        }
                        recyclerListView.setTranslationY(f4);
                    } else if (AnonymousClass3.this.fromOffsetTop != -1) {
                        float f5 = 1.0f - f2;
                        ShareAlert.this.scrollOffsetY = (int) ((r5.fromOffsetTop * f5) + (AnonymousClass3.this.toOffsetTop * f2));
                        if (!z) {
                            f5 = f2;
                        }
                        RecyclerListView recyclerListView2 = ShareAlert.this.gridView;
                        if (z) {
                            recyclerListView2.setTranslationY(ShareAlert.this.currentPanTranslationY - ((AnonymousClass3.this.fromOffsetTop - AnonymousClass3.this.toOffsetTop) * f2));
                        } else {
                            recyclerListView2.setTranslationY(ShareAlert.this.currentPanTranslationY + ((AnonymousClass3.this.toOffsetTop - AnonymousClass3.this.fromOffsetTop) * f5));
                        }
                    }
                    ShareAlert.this.gridView.setTopGlowOffset((int) (ShareAlert.this.scrollOffsetY + ShareAlert.this.currentPanTranslationY));
                    ShareAlert.this.frameLayout.setTranslationY(ShareAlert.this.scrollOffsetY + ShareAlert.this.currentPanTranslationY);
                    ShareAlert.this.searchEmptyView.setTranslationY(ShareAlert.this.scrollOffsetY + ShareAlert.this.currentPanTranslationY);
                    ShareAlert.this.frameLayout2.invalidate();
                    ShareAlert shareAlert2 = ShareAlert.this;
                    shareAlert2.setCurrentPanTranslationY(shareAlert2.currentPanTranslationY);
                    ShareAlert.this.updateBottomOverlay();
                    AnonymousClass3.this.invalidate();
                }

                @Override
                public void onTransitionEnd() {
                    super.onTransitionEnd();
                    ShareAlert shareAlert = ShareAlert.this;
                    shareAlert.keyboardT = ((shareAlert.commentTextView == null || !ShareAlert.this.commentTextView.isPopupVisible()) && ShareAlert.this.keyboardSize2 <= AndroidUtilities.dp(20.0f)) ? 0.0f : 1.0f;
                    ShareAlert.this.panTranslationMoveLayout = false;
                    ShareAlert shareAlert2 = ShareAlert.this;
                    shareAlert2.previousScrollOffsetY = shareAlert2.scrollOffsetY;
                    ShareAlert.this.gridView.setTopGlowOffset(ShareAlert.this.scrollOffsetY);
                    ShareAlert.this.frameLayout.setTranslationY(ShareAlert.this.scrollOffsetY);
                    ShareAlert.this.searchEmptyView.setTranslationY(ShareAlert.this.scrollOffsetY);
                    ShareAlert.this.gridView.setTranslationY(0.0f);
                    ShareAlert.this.searchGridView.setTranslationY(0.0f);
                    ShareAlert.this.updateBottomOverlay();
                }

                @Override
                public void onTransitionStart(boolean z, int i) {
                    super.onTransitionStart(z, i);
                    if (ShareAlert.this.previousScrollOffsetY != ShareAlert.this.scrollOffsetY) {
                        AnonymousClass3 anonymousClass3 = AnonymousClass3.this;
                        anonymousClass3.fromScrollY = ShareAlert.this.previousScrollOffsetY;
                        AnonymousClass3 anonymousClass32 = AnonymousClass3.this;
                        anonymousClass32.toScrollY = ShareAlert.this.scrollOffsetY;
                        ShareAlert.this.panTranslationMoveLayout = true;
                        AnonymousClass3 anonymousClass33 = AnonymousClass3.this;
                        ShareAlert.this.scrollOffsetY = anonymousClass33.fromScrollY;
                    } else {
                        AnonymousClass3.this.fromScrollY = -1;
                    }
                    if (AnonymousClass3.this.topOffset != AnonymousClass3.this.previousTopOffset) {
                        AnonymousClass3.this.fromOffsetTop = 0;
                        AnonymousClass3.this.toOffsetTop = 0;
                        ShareAlert.this.panTranslationMoveLayout = true;
                        AnonymousClass3 anonymousClass34 = AnonymousClass3.this;
                        int i2 = anonymousClass34.topOffset - AnonymousClass3.this.previousTopOffset;
                        if (z) {
                            AnonymousClass3.access$3312(anonymousClass34, i2);
                        } else {
                            AnonymousClass3.access$3320(anonymousClass34, i2);
                        }
                        AnonymousClass3 anonymousClass35 = AnonymousClass3.this;
                        ShareAlert.this.scrollOffsetY = z ? anonymousClass35.fromScrollY : anonymousClass35.toScrollY;
                    } else {
                        AnonymousClass3.this.fromOffsetTop = -1;
                    }
                    ShareAlert.this.gridView.setTopGlowOffset((int) (ShareAlert.this.currentPanTranslationY + ShareAlert.this.scrollOffsetY));
                    ShareAlert.this.frameLayout.setTranslationY(ShareAlert.this.currentPanTranslationY + ShareAlert.this.scrollOffsetY);
                    ShareAlert.this.searchEmptyView.setTranslationY(ShareAlert.this.currentPanTranslationY + ShareAlert.this.scrollOffsetY);
                    AnonymousClass3.this.invalidate();
                }
            }

            AnonymousClass3(final Context context2) {
                super(context2);
                this.ignoreLayout = false;
                this.rect1 = new RectF();
                this.adjustPanLayoutHelper = new AdjustPanLayoutHelper(this) {
                    AnonymousClass1(View this) {
                        super(this);
                    }

                    @Override
                    protected boolean heightAnimationEnabled() {
                        if (ShareAlert.this.isDismissed() || !ShareAlert.this.fullyShown) {
                            return false;
                        }
                        return !ShareAlert.this.commentTextView.isPopupVisible();
                    }

                    @Override
                    public void onPanTranslationUpdate(float f3, float f22, boolean z5) {
                        ShareAlert.this.keyboardT = f22;
                        super.onPanTranslationUpdate(f3, f22, z5);
                        for (int i5 = 0; i5 < ((BottomSheet) ShareAlert.this).containerView.getChildCount(); i5++) {
                            View childAt = ((BottomSheet) ShareAlert.this).containerView.getChildAt(i5);
                            if (childAt != ShareAlert.this.pickerBottom && childAt != ShareAlert.this.bulletinContainer && childAt != ShareAlert.this.shadow[1] && childAt != ShareAlert.this.sharesCountLayout && childAt != ShareAlert.this.frameLayout2) {
                                ShareAlert shareAlert = ShareAlert.this;
                                if (childAt != shareAlert.timestampFrameLayout && childAt != shareAlert.writeButtonContainer && childAt != ShareAlert.this.selectedCountView && childAt != ShareAlert.this.navigationBar) {
                                    childAt.setTranslationY(f3);
                                }
                            }
                        }
                        ShareAlert.this.currentPanTranslationY = f3;
                        if (AnonymousClass3.this.fromScrollY != -1) {
                            if (!z5) {
                                f22 = 1.0f - f22;
                            }
                            float f32 = 1.0f - f22;
                            ShareAlert.this.scrollOffsetY = (int) ((r5.fromScrollY * f32) + (AnonymousClass3.this.toScrollY * f22));
                            float f4 = ShareAlert.this.currentPanTranslationY + ((AnonymousClass3.this.fromScrollY - AnonymousClass3.this.toScrollY) * f32);
                            ShareAlert.this.gridView.setTranslationY(f4);
                            RecyclerListView recyclerListView = ShareAlert.this.searchGridView;
                            if (!z5) {
                                f4 += ShareAlert.this.gridView.getPaddingTop();
                            }
                            recyclerListView.setTranslationY(f4);
                        } else if (AnonymousClass3.this.fromOffsetTop != -1) {
                            float f5 = 1.0f - f22;
                            ShareAlert.this.scrollOffsetY = (int) ((r5.fromOffsetTop * f5) + (AnonymousClass3.this.toOffsetTop * f22));
                            if (!z5) {
                                f5 = f22;
                            }
                            RecyclerListView recyclerListView2 = ShareAlert.this.gridView;
                            if (z5) {
                                recyclerListView2.setTranslationY(ShareAlert.this.currentPanTranslationY - ((AnonymousClass3.this.fromOffsetTop - AnonymousClass3.this.toOffsetTop) * f22));
                            } else {
                                recyclerListView2.setTranslationY(ShareAlert.this.currentPanTranslationY + ((AnonymousClass3.this.toOffsetTop - AnonymousClass3.this.fromOffsetTop) * f5));
                            }
                        }
                        ShareAlert.this.gridView.setTopGlowOffset((int) (ShareAlert.this.scrollOffsetY + ShareAlert.this.currentPanTranslationY));
                        ShareAlert.this.frameLayout.setTranslationY(ShareAlert.this.scrollOffsetY + ShareAlert.this.currentPanTranslationY);
                        ShareAlert.this.searchEmptyView.setTranslationY(ShareAlert.this.scrollOffsetY + ShareAlert.this.currentPanTranslationY);
                        ShareAlert.this.frameLayout2.invalidate();
                        ShareAlert shareAlert2 = ShareAlert.this;
                        shareAlert2.setCurrentPanTranslationY(shareAlert2.currentPanTranslationY);
                        ShareAlert.this.updateBottomOverlay();
                        AnonymousClass3.this.invalidate();
                    }

                    @Override
                    public void onTransitionEnd() {
                        super.onTransitionEnd();
                        ShareAlert shareAlert = ShareAlert.this;
                        shareAlert.keyboardT = ((shareAlert.commentTextView == null || !ShareAlert.this.commentTextView.isPopupVisible()) && ShareAlert.this.keyboardSize2 <= AndroidUtilities.dp(20.0f)) ? 0.0f : 1.0f;
                        ShareAlert.this.panTranslationMoveLayout = false;
                        ShareAlert shareAlert2 = ShareAlert.this;
                        shareAlert2.previousScrollOffsetY = shareAlert2.scrollOffsetY;
                        ShareAlert.this.gridView.setTopGlowOffset(ShareAlert.this.scrollOffsetY);
                        ShareAlert.this.frameLayout.setTranslationY(ShareAlert.this.scrollOffsetY);
                        ShareAlert.this.searchEmptyView.setTranslationY(ShareAlert.this.scrollOffsetY);
                        ShareAlert.this.gridView.setTranslationY(0.0f);
                        ShareAlert.this.searchGridView.setTranslationY(0.0f);
                        ShareAlert.this.updateBottomOverlay();
                    }

                    @Override
                    public void onTransitionStart(boolean z5, int i5) {
                        super.onTransitionStart(z5, i5);
                        if (ShareAlert.this.previousScrollOffsetY != ShareAlert.this.scrollOffsetY) {
                            AnonymousClass3 anonymousClass32 = AnonymousClass3.this;
                            anonymousClass32.fromScrollY = ShareAlert.this.previousScrollOffsetY;
                            AnonymousClass3 anonymousClass322 = AnonymousClass3.this;
                            anonymousClass322.toScrollY = ShareAlert.this.scrollOffsetY;
                            ShareAlert.this.panTranslationMoveLayout = true;
                            AnonymousClass3 anonymousClass33 = AnonymousClass3.this;
                            ShareAlert.this.scrollOffsetY = anonymousClass33.fromScrollY;
                        } else {
                            AnonymousClass3.this.fromScrollY = -1;
                        }
                        if (AnonymousClass3.this.topOffset != AnonymousClass3.this.previousTopOffset) {
                            AnonymousClass3.this.fromOffsetTop = 0;
                            AnonymousClass3.this.toOffsetTop = 0;
                            ShareAlert.this.panTranslationMoveLayout = true;
                            AnonymousClass3 anonymousClass34 = AnonymousClass3.this;
                            int i22 = anonymousClass34.topOffset - AnonymousClass3.this.previousTopOffset;
                            if (z5) {
                                AnonymousClass3.access$3312(anonymousClass34, i22);
                            } else {
                                AnonymousClass3.access$3320(anonymousClass34, i22);
                            }
                            AnonymousClass3 anonymousClass35 = AnonymousClass3.this;
                            ShareAlert.this.scrollOffsetY = z5 ? anonymousClass35.fromScrollY : anonymousClass35.toScrollY;
                        } else {
                            AnonymousClass3.this.fromOffsetTop = -1;
                        }
                        ShareAlert.this.gridView.setTopGlowOffset((int) (ShareAlert.this.currentPanTranslationY + ShareAlert.this.scrollOffsetY));
                        ShareAlert.this.frameLayout.setTranslationY(ShareAlert.this.currentPanTranslationY + ShareAlert.this.scrollOffsetY);
                        ShareAlert.this.searchEmptyView.setTranslationY(ShareAlert.this.currentPanTranslationY + ShareAlert.this.scrollOffsetY);
                        AnonymousClass3.this.invalidate();
                    }
                };
                this.lightStatusBar = AndroidUtilities.computePerceivedBrightness(ShareAlert.this.getThemedColor(Theme.key_dialogBackground)) > 0.721f;
                this.pinnedToTop = new AnimatedFloat(this, 0L, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
            }

            static int access$3312(AnonymousClass3 anonymousClass32, int i5) {
                int i22 = anonymousClass32.toOffsetTop + i5;
                anonymousClass32.toOffsetTop = i22;
                return i22;
            }

            static int access$3320(AnonymousClass3 anonymousClass32, int i5) {
                int i22 = anonymousClass32.toOffsetTop - i5;
                anonymousClass32.toOffsetTop = i22;
                return i22;
            }

            private void onMeasureInternal(int i5, int i22) {
                int makeMeasureSpec;
                int paddingTop;
                int size2 = View.MeasureSpec.getSize(i5);
                int size22 = View.MeasureSpec.getSize(i22);
                int i32 = size2 - (((BottomSheet) ShareAlert.this).backgroundPaddingLeft * 2);
                ShareAlert.this.keyboardSize2 = measureKeyboardHeight();
                if (!ShareAlert.this.commentTextView.isWaitingForKeyboardOpen() && ShareAlert.this.keyboardSize2 <= AndroidUtilities.dp(20.0f) && !ShareAlert.this.commentTextView.isPopupShowing() && !ShareAlert.this.commentTextView.isAnimatePopupClosing()) {
                    this.ignoreLayout = true;
                    ShareAlert.this.commentTextView.hideEmojiView();
                    this.ignoreLayout = false;
                }
                this.ignoreLayout = true;
                if (ShareAlert.this.keyboardSize2 <= AndroidUtilities.dp(20.0f)) {
                    if (!AndroidUtilities.isInMultiwindow) {
                        size22 -= ((BottomSheet) ShareAlert.this).keyboardVisible ? 0 : ShareAlert.this.commentTextView.getEmojiPadding();
                        i22 = View.MeasureSpec.makeMeasureSpec(size22, 1073741824);
                    }
                    int i42 = ShareAlert.this.commentTextView.isPopupShowing() ? 8 : 0;
                    if (ShareAlert.this.pickerBottomLayout != null) {
                        ShareAlert.this.pickerBottomLayout.setVisibility(i42);
                    }
                } else {
                    if (!ShareAlert.this.commentTextView.isPopupVisible()) {
                        ShareAlert.this.commentTextView.hideEmojiView();
                    }
                    if (ShareAlert.this.pickerBottomLayout != null) {
                        ShareAlert.this.pickerBottomLayout.setVisibility(8);
                    }
                }
                this.ignoreLayout = false;
                BlurredFrameLayout blurredFrameLayout = ShareAlert.this.navigationBar;
                if (blurredFrameLayout != null) {
                    blurredFrameLayout.measure(View.MeasureSpec.makeMeasureSpec(i32, 1073741824), View.MeasureSpec.makeMeasureSpec(((BottomSheet) ShareAlert.this).navigationBarHeight, 1073741824));
                }
                int childCount = getChildCount();
                for (int i52 = 0; i52 < childCount; i52++) {
                    View childAt = getChildAt(i52);
                    if (childAt != null && childAt.getVisibility() != 8) {
                        ShareAlert shareAlert = ShareAlert.this;
                        if (childAt != shareAlert.navigationBar) {
                            if (shareAlert.commentTextView == null || !ShareAlert.this.commentTextView.isPopupView(childAt)) {
                                measureChildWithMargins(childAt, i5, 0, i22, 0);
                            } else {
                                if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
                                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i32, 1073741824);
                                    paddingTop = childAt.getLayoutParams().height;
                                } else if (AndroidUtilities.isTablet()) {
                                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i32, 1073741824);
                                    paddingTop = Math.min(AndroidUtilities.dp(AndroidUtilities.isTablet() ? 200.0f : 320.0f), (size22 - AndroidUtilities.statusBarHeight) + getPaddingTop());
                                } else {
                                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(i32, 1073741824);
                                    paddingTop = (size22 - AndroidUtilities.statusBarHeight) + getPaddingTop();
                                }
                                childAt.measure(makeMeasureSpec, View.MeasureSpec.makeMeasureSpec(paddingTop, 1073741824));
                            }
                        }
                    }
                }
                ShareAlert.this.updateBottomOverlay();
            }

            @Override
            public void dispatchDraw(Canvas canvas) {
                canvas.save();
                canvas.clipRect(0.0f, getPaddingTop() + ShareAlert.this.currentPanTranslationY, getMeasuredWidth(), getMeasuredHeight() + ShareAlert.this.currentPanTranslationY + AndroidUtilities.dp(50.0f));
                super.dispatchDraw(canvas);
                canvas.restore();
            }

            @Override
            public void drawList(Canvas canvas, boolean z5, ArrayList arrayList3) {
                if (ShareAlert.this.gridView.getVisibility() == 0 && ShareAlert.this.gridView.getAlpha() >= 0.0f) {
                    canvas.save();
                    canvas.translate(ShareAlert.this.gridView.getX(), ShareAlert.this.gridView.getY());
                    ShareAlert.this.gridView.draw(canvas);
                    canvas.restore();
                }
                if (ShareAlert.this.topicsGridView.getVisibility() == 0 && ShareAlert.this.topicsGridView.getAlpha() >= 0.0f) {
                    canvas.save();
                    canvas.translate(ShareAlert.this.topicsGridView.getX(), ShareAlert.this.topicsGridView.getY());
                    ShareAlert.this.topicsGridView.draw(canvas);
                    canvas.restore();
                }
                if (ShareAlert.this.searchGridView.getVisibility() != 0 || ShareAlert.this.searchGridView.getAlpha() < 0.0f) {
                    return;
                }
                canvas.save();
                canvas.translate(ShareAlert.this.searchGridView.getX(), ShareAlert.this.searchGridView.getY());
                ShareAlert.this.searchGridView.draw(canvas);
                canvas.restore();
            }

            @Override
            protected Theme.ResourcesProvider getResourceProvider() {
                return ((BottomSheet) ShareAlert.this).resourcesProvider;
            }

            @Override
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                this.adjustPanLayoutHelper.setResizableView(this);
                this.adjustPanLayoutHelper.onAttach();
            }

            @Override
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                this.adjustPanLayoutHelper.onDetach();
            }

            @Override
            protected void onDraw(Canvas canvas) {
                float f3;
                canvas.save();
                canvas.translate(0.0f, ShareAlert.this.currentPanTranslationY);
                int dp = (ShareAlert.this.scrollOffsetY - ((BottomSheet) ShareAlert.this).backgroundPaddingTop) + AndroidUtilities.dp(6.0f) + this.topOffset;
                ShareAlert shareAlert = ShareAlert.this;
                int i5 = shareAlert.containerViewTop = ((shareAlert.scrollOffsetY - ((BottomSheet) ShareAlert.this).backgroundPaddingTop) - AndroidUtilities.dp(13.0f)) + this.topOffset;
                int measuredHeight = getMeasuredHeight() + AndroidUtilities.dp(60.0f) + ((BottomSheet) ShareAlert.this).backgroundPaddingTop;
                if (((BottomSheet) ShareAlert.this).isFullscreen || Build.VERSION.SDK_INT < 21) {
                    f3 = 0.0f;
                } else {
                    dp += AndroidUtilities.statusBarHeight;
                    boolean z5 = this.fullHeight && ((BottomSheet) ShareAlert.this).backgroundPaddingTop + i5 < AndroidUtilities.statusBarHeight;
                    int i22 = i5 + AndroidUtilities.statusBarHeight;
                    int i32 = -((BottomSheet) ShareAlert.this).backgroundPaddingTop;
                    f3 = this.pinnedToTop.set(z5);
                    i5 = AndroidUtilities.lerp(i22, i32, f3);
                }
                ShareAlert.this.shadowDrawable.setBounds(0, i5, getMeasuredWidth(), measuredHeight);
                ShareAlert.this.shadowDrawable.draw(canvas);
                FrameLayout frameLayout = ShareAlert.this.bulletinContainer2;
                if (frameLayout != null) {
                    if (i5 > AndroidUtilities.statusBarHeight || frameLayout.getChildCount() <= 0) {
                        ShareAlert.this.bulletinContainer2.setTranslationY(Math.max(0, ((i5 + ((BottomSheet) r1).backgroundPaddingTop) - ShareAlert.this.bulletinContainer2.getTop()) - ShareAlert.this.bulletinContainer2.getMeasuredHeight()));
                    } else {
                        ShareAlert.this.bulletinContainer2.setTranslationY(0.0f);
                        Bulletin visibleBulletin = Bulletin.getVisibleBulletin();
                        if (visibleBulletin != null) {
                            if (visibleBulletin.getLayout() != null) {
                                visibleBulletin.getLayout().setTop(true);
                            }
                            visibleBulletin.hide();
                        }
                    }
                }
                if (f3 < 1.0f) {
                    int dp2 = AndroidUtilities.dp(36.0f);
                    this.rect1.set((getMeasuredWidth() - dp2) / 2, dp, (getMeasuredWidth() + dp2) / 2, dp + AndroidUtilities.dp(4.0f));
                    Theme.dialogs_onlineCirclePaint.setColor(ShareAlert.this.getThemedColor(Theme.key_sheet_scrollUp));
                    Theme.dialogs_onlineCirclePaint.setAlpha((int) (r0.getAlpha() * (1.0f - f3)));
                    canvas.drawRoundRect(this.rect1, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.dialogs_onlineCirclePaint);
                }
                if (Build.VERSION.SDK_INT >= 23) {
                    int systemUiVisibility = getSystemUiVisibility();
                    boolean z22 = this.lightStatusBar && ((float) 0) > ((float) AndroidUtilities.statusBarHeight) * 0.5f;
                    if (z22 != ((systemUiVisibility & 8192) > 0)) {
                        setSystemUiVisibility(z22 ? systemUiVisibility | 8192 : systemUiVisibility & (-8193));
                    }
                }
                canvas.restore();
                this.previousTopOffset = this.topOffset;
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (this.fullHeight ? motionEvent.getAction() != 0 || ShareAlert.this.scrollOffsetY == 0 || motionEvent.getY() >= ShareAlert.this.scrollOffsetY - AndroidUtilities.dp(30.0f) : motionEvent.getAction() != 0 || motionEvent.getY() >= this.topOffset - AndroidUtilities.dp(30.0f)) {
                    return super.onInterceptTouchEvent(motionEvent);
                }
                ShareAlert.this.lambda$new$0();
                return true;
            }

            @Override
            public void onLayout(boolean r11, int r12, int r13, int r14, int r15) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.AnonymousClass3.onLayout(boolean, int, int, int, int):void");
            }

            @Override
            protected void onMeasure(int i5, int i22) {
                int dp;
                int size2 = getLayoutParams().height > 0 ? getLayoutParams().height : View.MeasureSpec.getSize(i22);
                ShareAlert.this.layoutManager.setNeedFixGap(getLayoutParams().height <= 0);
                ShareAlert.this.searchLayoutManager.setNeedFixGap(getLayoutParams().height <= 0);
                if (Build.VERSION.SDK_INT >= 21 && !((BottomSheet) ShareAlert.this).isFullscreen) {
                    this.ignoreLayout = true;
                    setPadding(((BottomSheet) ShareAlert.this).backgroundPaddingLeft, AndroidUtilities.statusBarHeight, ((BottomSheet) ShareAlert.this).backgroundPaddingLeft, 0);
                    this.ignoreLayout = false;
                }
                int paddingTop = size2 - getPaddingTop();
                int dp2 = AndroidUtilities.dp(103.0f) + AndroidUtilities.dp(48.0f) + (Math.max(2, (int) Math.ceil(Math.max(ShareAlert.this.searchAdapter.getItemCount(), ShareAlert.this.listAdapter.getItemCount() - 1) / 4.0f)) * AndroidUtilities.dp(103.0f)) + ((BottomSheet) ShareAlert.this).backgroundPaddingTop;
                if (ShareAlert.this.topicsGridView.getVisibility() != 8 && (dp = AndroidUtilities.dp(103.0f) + AndroidUtilities.dp(48.0f) + (Math.max(2, (int) Math.ceil((ShareAlert.this.shareTopicsAdapter.getItemCount() - 1) / 4.0f)) * AndroidUtilities.dp(103.0f)) + ((BottomSheet) ShareAlert.this).backgroundPaddingTop) > dp2) {
                    dp2 = AndroidUtilities.lerp(dp2, dp, ShareAlert.this.topicsGridView.getAlpha());
                }
                int dp3 = (dp2 < paddingTop ? 0 : paddingTop - ((paddingTop / 5) * 3)) + AndroidUtilities.dp(8.0f);
                if (ShareAlert.this.gridView.getPaddingTop() != dp3) {
                    this.ignoreLayout = true;
                    ShareAlert.this.gridView.setPadding(0, dp3, 0, AndroidUtilities.dp((ShareAlert.this.timestampFrameLayout != null ? 48 : 0) + 60) + ((BottomSheet) ShareAlert.this).navigationBarHeight);
                    ShareAlert.this.topicsGridView.setPadding(0, dp3, 0, AndroidUtilities.dp((ShareAlert.this.timestampFrameLayout != null ? 48 : 0) + 60) + ((BottomSheet) ShareAlert.this).navigationBarHeight);
                    this.ignoreLayout = false;
                }
                if (((BottomSheet) ShareAlert.this).keyboardVisible && getLayoutParams().height <= 0 && ShareAlert.this.searchGridView.getPaddingTop() != dp3) {
                    this.ignoreLayout = true;
                    ShareAlert.this.searchGridView.setPadding(0, 0, 0, AndroidUtilities.dp((ShareAlert.this.timestampFrameLayout == null ? 0 : 48) + 60) + ((BottomSheet) ShareAlert.this).navigationBarHeight);
                    this.ignoreLayout = false;
                }
                boolean z5 = dp2 >= size2;
                this.fullHeight = z5;
                this.topOffset = z5 ? 0 : size2 - dp2;
                this.ignoreLayout = true;
                ShareAlert.this.checkCurrentList(false);
                this.ignoreLayout = false;
                setMeasuredDimension(View.MeasureSpec.getSize(i5), size2);
                onMeasureInternal(i5, View.MeasureSpec.makeMeasureSpec(size2, 1073741824));
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !ShareAlert.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            @Override
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.sizeNotifierFrameLayout = anonymousClass3;
        this.containerView = anonymousClass3;
        anonymousClass3.setWillNotDraw(false);
        this.containerView.setClipChildren(false);
        ViewGroup viewGroup = this.containerView;
        int i5 = this.backgroundPaddingLeft;
        viewGroup.setPadding(i5, 0, i5, AndroidUtilities.navigationBarHeight);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.frameLayout = frameLayout;
        int i6 = Theme.key_dialogBackground;
        frameLayout.setBackgroundColor(getThemedColor(i6));
        if (this.darkTheme && this.linkToCopy[1] != null) {
            AnonymousClass4 anonymousClass4 = new SwitchView(context2) {
                AnonymousClass4(final Context context2) {
                    super(context2);
                }

                @Override
                protected void onTabSwitch(int i7) {
                    ShareAlert.this.updateLinkTextView();
                }
            };
            this.switchView = anonymousClass4;
            this.frameLayout.addView(anonymousClass4, LayoutHelper.createFrame(-1, 36.0f, 51, 0.0f, 11.0f, 0.0f, 0.0f));
        }
        SearchField searchField = new SearchField(context2);
        this.searchView = searchField;
        this.frameLayout.addView(searchField, LayoutHelper.createFrame(-1, 58, 83));
        ActionBar actionBar = new ActionBar(context2);
        this.topicsBackActionBar = actionBar;
        actionBar.setOccupyStatusBar(false);
        this.topicsBackActionBar.setBackButtonImage(R.drawable.ic_ab_back);
        ActionBar actionBar2 = this.topicsBackActionBar;
        int i7 = Theme.key_dialogTextBlack;
        actionBar2.setTitleColor(getThemedColor(i7));
        this.topicsBackActionBar.setSubtitleColor(getThemedColor(Theme.key_dialogTextGray2));
        this.topicsBackActionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2), false);
        this.topicsBackActionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_actionBarWhiteSelector), false);
        this.topicsBackActionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            AnonymousClass5() {
            }

            @Override
            public void onItemClick(int i8) {
                ShareAlert.this.onBackPressed();
            }
        });
        this.topicsBackActionBar.setVisibility(8);
        this.frameLayout.addView(this.topicsBackActionBar, LayoutHelper.createFrame(-1, 58, 83));
        RecyclerListView recyclerListView = new RecyclerListView(context2, this.resourcesProvider);
        this.topicsGridView = recyclerListView;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context2, 4);
        this.topicsLayoutManager = gridLayoutManager;
        recyclerListView.setLayoutManager(gridLayoutManager);
        this.topicsLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            AnonymousClass6() {
            }

            @Override
            public int getSpanSize(int i8) {
                if (i8 == 0) {
                    return ShareAlert.this.topicsLayoutManager.getSpanCount();
                }
                return 1;
            }
        });
        this.topicsGridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            AnonymousClass7() {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i8, int i22) {
                if (i22 != 0) {
                    ShareAlert.this.updateLayout();
                    ShareAlert shareAlert = ShareAlert.this;
                    shareAlert.previousScrollOffsetY = shareAlert.scrollOffsetY;
                }
            }
        });
        RecyclerListView recyclerListView2 = this.topicsGridView;
        ShareTopicsAdapter shareTopicsAdapter = new ShareTopicsAdapter(context2);
        this.shareTopicsAdapter = shareTopicsAdapter;
        recyclerListView2.setAdapter(shareTopicsAdapter);
        RecyclerListView recyclerListView3 = this.topicsGridView;
        int i8 = Theme.key_dialogScrollGlow;
        recyclerListView3.setGlowColor(getThemedColor(i8));
        this.topicsGridView.setVerticalScrollBarEnabled(false);
        this.topicsGridView.setHorizontalScrollBarEnabled(false);
        this.topicsGridView.setOverScrollMode(2);
        this.topicsGridView.setSelectorDrawableColor(0);
        this.topicsGridView.setItemSelectorColorProvider(new GenericProvider() {
            @Override
            public final Object provide(Object obj) {
                Integer lambda$new$2;
                lambda$new$2 = ShareAlert.lambda$new$2((Integer) obj);
                return lambda$new$2;
            }
        });
        this.topicsGridView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        this.topicsGridView.setClipToPadding(false);
        this.topicsGridView.addItemDecoration(new RecyclerView.ItemDecoration() {
            AnonymousClass8() {
            }

            @Override
            public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                RecyclerListView.Holder holder = (RecyclerListView.Holder) recyclerView.getChildViewHolder(view);
                if (holder == null) {
                    rect.left = AndroidUtilities.dp(4.0f);
                    rect.right = AndroidUtilities.dp(4.0f);
                } else {
                    int adapterPosition = holder.getAdapterPosition() % 4;
                    rect.left = adapterPosition == 0 ? 0 : AndroidUtilities.dp(4.0f);
                    rect.right = adapterPosition != 3 ? AndroidUtilities.dp(4.0f) : 0;
                }
            }
        });
        this.topicsGridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i9) {
                ShareAlert.this.lambda$new$3(view, i9);
            }
        });
        this.topicsGridView.setVisibility(8);
        this.containerView.addView(this.topicsGridView, LayoutHelper.createFrame(-1, -1, 51));
        AnonymousClass9 anonymousClass9 = new RecyclerListView(context2, this.resourcesProvider) {
            AnonymousClass9(final Context context2, Theme.ResourcesProvider resourcesProvider2) {
                super(context2, resourcesProvider2);
            }

            @Override
            protected boolean allowSelectChildAtPosition(float f3, float f22) {
                return f22 >= ((float) (AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 58.0f : 111.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }

            @Override
            public void draw(Canvas canvas) {
                if (ShareAlert.this.topicsGridView.getVisibility() != 8) {
                    canvas.save();
                    canvas.clipRect(0, ShareAlert.this.scrollOffsetY + AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 58.0f : 111.0f), getWidth(), getHeight());
                }
                super.draw(canvas);
                if (ShareAlert.this.topicsGridView.getVisibility() != 8) {
                    canvas.restore();
                }
            }
        };
        this.gridView = anonymousClass9;
        anonymousClass9.setSelectorDrawableColor(0);
        this.gridView.setItemSelectorColorProvider(new GenericProvider() {
            @Override
            public final Object provide(Object obj) {
                Integer lambda$new$4;
                lambda$new$4 = ShareAlert.lambda$new$4((Integer) obj);
                return lambda$new$4;
            }
        });
        this.gridView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        this.gridView.setClipToPadding(false);
        RecyclerListView recyclerListView4 = this.gridView;
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getContext(), 4);
        this.layoutManager = gridLayoutManager2;
        recyclerListView4.setLayoutManager(gridLayoutManager2);
        this.layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            AnonymousClass10() {
            }

            @Override
            public int getSpanSize(int i9) {
                if (i9 == 0) {
                    return ShareAlert.this.layoutManager.getSpanCount();
                }
                return 1;
            }
        });
        this.gridView.setHorizontalScrollBarEnabled(false);
        this.gridView.setVerticalScrollBarEnabled(false);
        this.gridView.setOverScrollMode(2);
        this.gridView.addItemDecoration(new RecyclerView.ItemDecoration() {
            AnonymousClass11() {
            }

            @Override
            public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                RecyclerListView.Holder holder = (RecyclerListView.Holder) recyclerView.getChildViewHolder(view);
                if (holder == null) {
                    rect.left = AndroidUtilities.dp(4.0f);
                    rect.right = AndroidUtilities.dp(4.0f);
                } else {
                    int adapterPosition = holder.getAdapterPosition() % 4;
                    rect.left = adapterPosition == 0 ? 0 : AndroidUtilities.dp(4.0f);
                    rect.right = adapterPosition != 3 ? AndroidUtilities.dp(4.0f) : 0;
                }
            }
        });
        this.containerView.addView(this.gridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        RecyclerListView recyclerListView5 = this.gridView;
        ShareDialogsAdapter shareDialogsAdapter = new ShareDialogsAdapter(context2);
        this.listAdapter = shareDialogsAdapter;
        recyclerListView5.setAdapter(shareDialogsAdapter);
        this.gridView.setGlowColor(getThemedColor(i8));
        this.gridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i9) {
                ShareAlert.this.lambda$new$5(view, i9);
            }
        });
        this.gridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            AnonymousClass12() {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i9, int i22) {
                if (i22 != 0) {
                    ShareAlert.this.updateLayout();
                    ShareAlert shareAlert = ShareAlert.this;
                    shareAlert.previousScrollOffsetY = shareAlert.scrollOffsetY;
                }
                if (Bulletin.getVisibleBulletin() == null || Bulletin.getVisibleBulletin().getLayout() == null || !(Bulletin.getVisibleBulletin().getLayout().getParent() instanceof View) || ((View) Bulletin.getVisibleBulletin().getLayout().getParent()).getParent() != ShareAlert.this.bulletinContainer2) {
                    return;
                }
                Bulletin.hideVisible();
            }
        });
        AnonymousClass13 anonymousClass13 = new RecyclerListView(context2, this.resourcesProvider) {
            AnonymousClass13(final Context context2, Theme.ResourcesProvider resourcesProvider2) {
                super(context2, resourcesProvider2);
            }

            @Override
            protected boolean allowSelectChildAtPosition(float f3, float f22) {
                return f22 >= ((float) (AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 58.0f : 111.0f) + (Build.VERSION.SDK_INT >= 21 ? AndroidUtilities.statusBarHeight : 0)));
            }

            @Override
            public void draw(Canvas canvas) {
                if (ShareAlert.this.topicsGridView.getVisibility() != 8) {
                    canvas.save();
                    canvas.clipRect(0, ShareAlert.this.scrollOffsetY + AndroidUtilities.dp((!ShareAlert.this.darkTheme || ShareAlert.this.linkToCopy[1] == null) ? 58.0f : 111.0f), getWidth(), getHeight());
                }
                super.draw(canvas);
                if (ShareAlert.this.topicsGridView.getVisibility() != 8) {
                    canvas.restore();
                }
            }
        };
        this.searchGridView = anonymousClass13;
        anonymousClass13.setItemSelectorColorProvider(new GenericProvider() {
            @Override
            public final Object provide(Object obj) {
                Integer lambda$new$6;
                lambda$new$6 = ShareAlert.lambda$new$6((Integer) obj);
                return lambda$new$6;
            }
        });
        this.searchGridView.setSelectorDrawableColor(0);
        this.searchGridView.setPadding(0, 0, 0, AndroidUtilities.dp(48.0f));
        this.searchGridView.setClipToPadding(false);
        RecyclerListView recyclerListView6 = this.searchGridView;
        FillLastGridLayoutManager fillLastGridLayoutManager = new FillLastGridLayoutManager(getContext(), 4, 0, this.searchGridView);
        this.searchLayoutManager = fillLastGridLayoutManager;
        recyclerListView6.setLayoutManager(fillLastGridLayoutManager);
        this.searchLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            AnonymousClass14() {
            }

            @Override
            public int getSpanSize(int i9) {
                return ShareAlert.this.searchAdapter.getSpanSize(4, i9);
            }
        });
        this.searchGridView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i9) {
                ShareAlert.this.lambda$new$7(view, i9);
            }
        });
        this.searchGridView.setHasFixedSize(true);
        this.searchGridView.setItemAnimator(null);
        this.searchGridView.setHorizontalScrollBarEnabled(false);
        this.searchGridView.setVerticalScrollBarEnabled(false);
        this.searchGridView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            AnonymousClass15() {
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i9, int i22) {
                if (i22 != 0) {
                    ShareAlert.this.updateLayout();
                    ShareAlert shareAlert = ShareAlert.this;
                    shareAlert.previousScrollOffsetY = shareAlert.scrollOffsetY;
                }
            }
        });
        this.searchGridView.addItemDecoration(new RecyclerView.ItemDecoration() {
            AnonymousClass16() {
            }

            @Override
            public void getItemOffsets(android.graphics.Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                RecyclerListView.Holder holder = (RecyclerListView.Holder) recyclerView.getChildViewHolder(view);
                if (holder == null) {
                    rect.left = AndroidUtilities.dp(4.0f);
                    rect.right = AndroidUtilities.dp(4.0f);
                    return;
                }
                if (holder.getItemViewType() != 5) {
                    rect.right = 0;
                    rect.left = 0;
                } else {
                    int adapterPosition = holder.getAdapterPosition() % 4;
                    rect.left = adapterPosition == 0 ? 0 : AndroidUtilities.dp(4.0f);
                    rect.right = adapterPosition != 3 ? AndroidUtilities.dp(4.0f) : 0;
                }
            }
        });
        this.searchGridView.setAdapter(this.searchAdapter);
        this.searchGridView.setGlowColor(getThemedColor(i8));
        this.recyclerItemsEnterAnimator = new RecyclerItemsEnterAnimator(this.searchGridView, true);
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context2, this.resourcesProvider);
        flickerLoadingView.setViewType(12);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context2, flickerLoadingView, 1, this.resourcesProvider);
        this.searchEmptyView = stickerEmptyView;
        stickerEmptyView.addView(flickerLoadingView, 0);
        this.searchEmptyView.setAnimateLayoutChange(true);
        this.searchEmptyView.showProgress(false, false);
        if (this.darkTheme) {
            this.searchEmptyView.title.setTextColor(getThemedColor(Theme.key_voipgroup_nameText));
        }
        this.searchEmptyView.title.setText(LocaleController.getString(R.string.NoResult));
        this.searchGridView.setEmptyView(this.searchEmptyView);
        this.searchGridView.setHideIfEmpty(false);
        this.searchGridView.setAnimateEmptyView(true, 0);
        this.containerView.addView(this.searchEmptyView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 52.0f, 0.0f, 0.0f));
        this.containerView.addView(this.searchGridView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 51);
        layoutParams.topMargin = AndroidUtilities.dp((!this.darkTheme || this.linkToCopy[1] == null) ? 58.0f : 111.0f);
        this.shadow[0] = new View(context2);
        View view = this.shadow[0];
        int i9 = Theme.key_dialogShadowLine;
        view.setBackgroundColor(getThemedColor(i9));
        this.shadow[0].setAlpha(0.0f);
        this.shadow[0].setTag(1);
        this.containerView.addView(this.shadow[0], layoutParams);
        this.containerView.addView(this.frameLayout, LayoutHelper.createFrame(-1, (!this.darkTheme || this.linkToCopy[1] == null) ? 58 : 111, 51));
        FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        layoutParams2.bottomMargin = AndroidUtilities.dp(48.0f);
        this.shadow[1] = new View(context2);
        this.shadow[1].setBackgroundColor(getThemedColor(i9));
        this.containerView.addView(this.shadow[1], layoutParams2);
        if (this.isChannel || this.linkToCopy[0] != null) {
            this.pickerBottom = new FrameLayout(context2);
            BlurredFrameLayout blurredFrameLayout = new BlurredFrameLayout(context2, this.sizeNotifierFrameLayout);
            this.pickerBottomLayout = blurredFrameLayout;
            blurredFrameLayout.isTopView = false;
            blurredFrameLayout.setBackgroundColor(themedColor);
            this.pickerBottom.addView(this.pickerBottomLayout);
            LinearLayout linearLayout = new LinearLayout(context2);
            linearLayout.setOrientation(0);
            this.pickerBottomLayout.addView(linearLayout, LayoutHelper.createFrame(-1, -1, 119));
            LinearLayout linearLayout2 = new LinearLayout(context2);
            this.linkContainer = linearLayout2;
            linearLayout2.setOrientation(0);
            LinearLayout linearLayout3 = this.linkContainer;
            int dp = AndroidUtilities.dp(8.0f);
            int i10 = Theme.key_share_linkBackground;
            int themedColor2 = getThemedColor(i10);
            int themedColor3 = getThemedColor(i10);
            int i11 = Theme.key_listSelector;
            linearLayout3.setBackground(Theme.createSimpleSelectorRoundRectDrawable(dp, themedColor2, Theme.blendOver(themedColor3, getThemedColor(i11))));
            ScaleStateListAnimator.apply(this.linkContainer, 0.015f, 1.2f);
            linearLayout.addView(this.linkContainer, LayoutHelper.createLinear(-1, 42, 1.0f, 87, 11, 11, 11, 0));
            this.linkContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    ShareAlert.this.lambda$new$8(view2);
                }
            });
            SimpleTextView simpleTextView = new SimpleTextView(context2);
            this.linkTextView = simpleTextView;
            simpleTextView.setTextSize(15);
            this.linkTextView.setTextColor(getThemedColor(Theme.key_share_linkText));
            this.linkTextView.setEllipsizeByGradient(true);
            updateLinkTextView();
            this.linkContainer.addView(this.linkTextView, LayoutHelper.createLinear(-1, -2, 1.0f, 23, 16, 0, 16, 0));
            TextView textView = new TextView(context2);
            this.linkCopyButton = textView;
            textView.setTextSize(1, 14.0f);
            this.linkCopyButton.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            this.linkCopyButton.setText(LocaleController.getString(R.string.Copy).toUpperCase());
            this.linkCopyButton.setPadding(AndroidUtilities.dp(9.0f), 0, AndroidUtilities.dp(9.0f), 0);
            this.linkCopyButton.setTypeface(AndroidUtilities.bold());
            this.linkCopyButton.setGravity(17);
            this.linkCopyButton.setBackground(Theme.createRadSelectorDrawable(getThemedColor(i11), 4, 4));
            this.linkContainer.addView(this.linkCopyButton, LayoutHelper.createLinear(-2, 28, 0.0f, 21, 0, 0, 7, 0));
            this.linkCopyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    ShareAlert.this.lambda$new$9(view2);
                }
            });
            ScaleStateListAnimator.apply(this.linkCopyButton);
            View view2 = new View(context2);
            view2.setBackgroundColor(getThemedColor(Theme.key_divider));
            this.pickerBottomLayout.addView(view2, LayoutHelper.createFrame(-1.0f, this.darkTheme ? 0.66f : 1.0f / AndroidUtilities.density, 55));
            this.containerView.addView(this.pickerBottom, LayoutHelper.createFrame(-1, 53, 83));
            ArrayList arrayList3 = this.sendingMessageObjects;
            if (arrayList3 != null && arrayList3.size() > 0 && ((MessageObject) this.sendingMessageObjects.get(0)).messageOwner != null && ((MessageObject) this.sendingMessageObjects.get(0)).messageOwner.forwards > 0) {
                MessageObject messageObject2 = (MessageObject) this.sendingMessageObjects.get(0);
                if (ChatObject.hasAdminRights(MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-messageObject2.getDialogId()))) && !messageObject2.isForwarded()) {
                    LinearLayout linearLayout4 = new LinearLayout(context2);
                    linearLayout4.setOrientation(0);
                    ImageView imageView = new ImageView(context2);
                    imageView.setImageResource(R.drawable.mini_stats_shares);
                    imageView.setScaleType(ImageView.ScaleType.CENTER);
                    int i12 = Theme.key_share_icon;
                    imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(i12), PorterDuff.Mode.SRC_IN));
                    linearLayout4.addView(imageView, LayoutHelper.createLinear(-2, -2, 0.0f, 16));
                    TextView textView2 = new TextView(context2);
                    textView2.setTextColor(getThemedColor(i12));
                    textView2.setTextSize(1, 15.0f);
                    textView2.setGravity(17);
                    textView2.setText(LocaleController.formatNumber(messageObject2.messageOwner.forwards, ','));
                    linearLayout4.addView(textView2, LayoutHelper.createLinear(-2, -2, 0.0f, 16, 4, -1, 0, 0));
                    linearLayout.addView(linearLayout4, LayoutHelper.createLinear(-2, -2, 0.0f, 16, 5, 5, 16, 0));
                }
            }
        } else {
            this.shadow[1].setAlpha(0.0f);
        }
        FrameLayout frameLayout2 = new FrameLayout(context2);
        this.bulletinContainer = frameLayout2;
        this.containerView.addView(frameLayout2, LayoutHelper.createFrame(-1, 100.0f, 87, 0.0f, 0.0f, 0.0f, this.pickerBottomLayout != null ? 48.0f : 0.0f));
        FrameLayout frameLayout3 = new FrameLayout(context2);
        this.bulletinContainer2 = frameLayout3;
        this.containerView.addView(frameLayout3, LayoutHelper.createFrame(-1, -2.0f, 55, 0.0f, 0.0f, 0.0f, 0.0f));
        AnonymousClass17 anonymousClass17 = new AnonymousClass17(context2, this.sizeNotifierFrameLayout);
        this.frameLayout2 = anonymousClass17;
        anonymousClass17.setBackgroundColor(themedColor);
        BlurredFrameLayout blurredFrameLayout2 = this.frameLayout2;
        blurredFrameLayout2.isTopView = false;
        blurredFrameLayout2.setWillNotDraw(false);
        this.frameLayout2.setAlpha(0.0f);
        this.frameLayout2.setVisibility(4);
        this.containerView.addView(this.frameLayout2, LayoutHelper.createFrame(-1, -2, 83));
        this.frameLayout2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view3, MotionEvent motionEvent) {
                boolean lambda$new$10;
                lambda$new$10 = ShareAlert.lambda$new$10(view3, motionEvent);
                return lambda$new$10;
            }
        });
        BlurredFrameLayout blurredFrameLayout3 = new BlurredFrameLayout(context2, this.sizeNotifierFrameLayout);
        this.navigationBar = blurredFrameLayout3;
        blurredFrameLayout3.setBackgroundColor(getThemedColor(i6));
        BlurredFrameLayout blurredFrameLayout4 = this.navigationBar;
        blurredFrameLayout4.isTopView = false;
        this.containerView.addView(blurredFrameLayout4, LayoutHelper.createFrame(-1, 0, 87));
        AndroidUtilities.setLightNavigationBar(this.container, AndroidUtilities.computePerceivedBrightness(getThemedColor(i6)) > 0.721f);
        AnonymousClass18 anonymousClass18 = new AnonymousClass18(context2, this.sizeNotifierFrameLayout, null, 1, true, this.resourcesProvider);
        this.commentTextView = anonymousClass18;
        if (this.darkTheme) {
            EditTextCaption editText = anonymousClass18.getEditText();
            int i13 = Theme.key_voipgroup_nameText;
            editText.setTextColor(getThemedColor(i13));
            this.commentTextView.getEditText().setCursorColor(getThemedColor(i13));
        }
        this.commentTextView.setHint(LocaleController.getString(R.string.ShareComment));
        this.commentTextView.onResume();
        this.commentTextView.setPadding(0, 0, AndroidUtilities.dp(84.0f), 0);
        this.frameLayout2.addView(this.commentTextView, LayoutHelper.createFrame(-1, -2, 51));
        this.frameLayout2.setClipChildren(false);
        this.frameLayout2.setClipToPadding(false);
        this.commentTextView.setClipChildren(false);
        AnonymousClass19 anonymousClass19 = new FrameLayout(context2) {
            AnonymousClass19(final Context context2) {
                super(context2);
            }

            @Override
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                accessibilityNodeInfo.setText(LocaleController.formatPluralString("AccDescrShareInChats", ShareAlert.this.selectedDialogs.size(), new Object[0]));
                accessibilityNodeInfo.setClassName(Button.class.getName());
                accessibilityNodeInfo.setLongClickable(true);
                accessibilityNodeInfo.setClickable(true);
            }
        };
        this.writeButtonContainer = anonymousClass19;
        anonymousClass19.setFocusable(true);
        this.writeButtonContainer.setFocusableInTouchMode(true);
        this.writeButtonContainer.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.containerView.addView(this.writeButtonContainer, LayoutHelper.createFrame(60, 60.0f, 85, 0.0f, 0.0f, 6.0f, 10.0f));
        final ImageView imageView2 = new ImageView(context2);
        int dp2 = AndroidUtilities.dp(56.0f);
        int i14 = Theme.key_dialogFloatingButton;
        int themedColor4 = getThemedColor(i14);
        int i15 = Build.VERSION.SDK_INT;
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(dp2, themedColor4, getThemedColor(i15 >= 21 ? Theme.key_dialogFloatingButtonPressed : i14));
        if (i15 < 21) {
            Drawable mutate = context2.getResources().getDrawable(R.drawable.floating_shadow_profile).mutate();
            mutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
            CombinedDrawable combinedDrawable = new CombinedDrawable(mutate, createSimpleSelectorCircleDrawable, 0, 0);
            combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
            createSimpleSelectorCircleDrawable = combinedDrawable;
        }
        imageView2.setBackground(createSimpleSelectorCircleDrawable);
        imageView2.setImageResource(R.drawable.attach_send);
        imageView2.setImportantForAccessibility(2);
        imageView2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_dialogFloatingIcon), PorterDuff.Mode.MULTIPLY));
        imageView2.setScaleType(ImageView.ScaleType.CENTER);
        if (i15 >= 21) {
            imageView2.setOutlineProvider(new ViewOutlineProvider() {
                AnonymousClass20() {
                }

                @Override
                public void getOutline(View view3, Outline outline) {
                    outline.setOval(0, 0, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                }
            });
        }
        this.writeButtonContainer.addView(imageView2, LayoutHelper.createFrame(i15 >= 21 ? 56 : 60, i15 < 21 ? 60.0f : 56.0f, 51, i15 >= 21 ? 2.0f : 0.0f, 0.0f, 0.0f, 0.0f));
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                ShareAlert.this.lambda$new$11(view3);
            }
        });
        imageView2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public final boolean onLongClick(View view3) {
                boolean lambda$new$12;
                lambda$new$12 = ShareAlert.this.lambda$new$12(imageView2, view3);
                return lambda$new$12;
            }
        });
        this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.bold());
        AnonymousClass21 anonymousClass21 = new View(context2) {
            AnonymousClass21(final Context context2) {
                super(context2);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                String format = String.format("%d", Integer.valueOf(Math.max(1, ShareAlert.this.selectedDialogs.size())));
                int max = Math.max(AndroidUtilities.dp(16.0f) + ((int) Math.ceil(ShareAlert.this.textPaint.measureText(format))), AndroidUtilities.dp(24.0f));
                int measuredWidth = getMeasuredWidth() / 2;
                getMeasuredHeight();
                ShareAlert.this.textPaint.setColor(ShareAlert.this.getThemedColor(Theme.key_dialogRoundCheckBoxCheck));
                ShareAlert.this.paint.setColor(ShareAlert.this.getThemedColor(Theme.key_dialogBackground));
                int i16 = max / 2;
                ShareAlert.this.rect.set(measuredWidth - i16, 0.0f, i16 + measuredWidth, getMeasuredHeight());
                canvas.drawRoundRect(ShareAlert.this.rect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), ShareAlert.this.paint);
                ShareAlert.this.paint.setColor(ShareAlert.this.getThemedColor(Theme.key_dialogFloatingButton));
                ShareAlert.this.rect.set(r5 + AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), r2 - AndroidUtilities.dp(2.0f), getMeasuredHeight() - AndroidUtilities.dp(2.0f));
                canvas.drawRoundRect(ShareAlert.this.rect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), ShareAlert.this.paint);
                canvas.drawText(format, measuredWidth - (r1 / 2), AndroidUtilities.dp(16.2f), ShareAlert.this.textPaint);
            }
        };
        this.selectedCountView = anonymousClass21;
        anonymousClass21.setAlpha(0.0f);
        this.selectedCountView.setScaleX(0.2f);
        this.selectedCountView.setScaleY(0.2f);
        this.containerView.addView(this.selectedCountView, LayoutHelper.createFrame(42, 24.0f, 85, 0.0f, 0.0f, -8.0f, 9.0f));
        if (num != null) {
            this.timestamp = num.intValue();
            BlurredFrameLayout blurredFrameLayout5 = new BlurredFrameLayout(context2, this.sizeNotifierFrameLayout);
            this.timestampFrameLayout = blurredFrameLayout5;
            blurredFrameLayout5.setBackgroundColor(themedColor);
            this.timestampFrameLayout.isTopView = false;
            LinearLayout linearLayout5 = new LinearLayout(context2);
            this.timestampLayout = linearLayout5;
            linearLayout5.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(4.0f));
            this.timestampLayout.setClipToPadding(false);
            this.timestampLayout.setOrientation(0);
            this.timestampLayout.setBackground(Theme.createRadSelectorDrawable(getThemedColor(Theme.key_listSelector), 6, 6));
            CheckBox2 checkBox2 = new CheckBox2(context2, 24, this.resourcesProvider);
            this.timestampCheckbox = checkBox2;
            checkBox2.setColor(Theme.key_radioBackgroundChecked, Theme.key_checkboxDisabled, Theme.key_checkboxCheck);
            this.timestampCheckbox.setDrawUnchecked(true);
            this.timestampCheckbox.setChecked(false, false);
            this.timestampCheckbox.setDrawBackgroundAsArc(10);
            this.timestampLayout.addView(this.timestampCheckbox, LayoutHelper.createLinear(26, 26, 16, 0, 0, 0, 0));
            TextView textView3 = new TextView(context2);
            this.timestampTextView = textView3;
            textView3.setTextColor(getThemedColor(i7));
            this.timestampTextView.setTextSize(1, 14.0f);
            this.timestampTextView.setText(LocaleController.formatString(R.string.VideoShareAddTimestamp, AndroidUtilities.formatShortDuration(num.intValue())));
            this.timestampLayout.addView(this.timestampTextView, LayoutHelper.createLinear(-2, -2, 16, 9, 0, 0, 0));
            ScaleStateListAnimator.apply(this.timestampLayout, 0.025f, 1.5f);
            this.timestampLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view3) {
                    ShareAlert.this.lambda$new$13(view3);
                }
            });
            this.timestampFrameLayout.addView(this.timestampLayout, LayoutHelper.createFrame(-2, -2, 17));
            if (this.pickerBottom == null) {
                View view3 = new View(context2);
                view3.setBackgroundColor(getThemedColor(Theme.key_divider));
                BlurredFrameLayout blurredFrameLayout6 = this.timestampFrameLayout;
                if (this.darkTheme) {
                    f2 = -1.0f;
                    i = 55;
                    f = 0.66f;
                } else {
                    f = 1.0f / AndroidUtilities.density;
                    f2 = -1.0f;
                    i = 55;
                }
                blurredFrameLayout6.addView(view3, LayoutHelper.createFrame(f2, f, i));
            }
            this.containerView.addView(this.timestampFrameLayout, LayoutHelper.createFrame(-1, 48, 83));
            if (this.pickerBottom == null) {
                this.timestampFrameLayout.setAlpha(0.0f);
                this.timestampFrameLayout.setVisibility(4);
            }
        }
        updateSelectedCount(0);
        DialogsActivity.loadDialogs(AccountInstance.getInstance(this.currentAccount));
        if (this.listAdapter.dialogs.isEmpty()) {
            NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.dialogsNeedReload);
        }
        DialogsSearchAdapter.loadRecentSearch(this.currentAccount, 0, new DialogsSearchAdapter.OnRecentSearchLoaded() {
            AnonymousClass22() {
            }

            @Override
            public void setRecentSearch(ArrayList arrayList4, LongSparseArray longSparseArray) {
                if (arrayList4 != null) {
                    int i16 = 0;
                    while (i16 < arrayList4.size()) {
                        TLObject tLObject = ((DialogsSearchAdapter.RecentSearchObject) arrayList4.get(i16)).object;
                        if ((tLObject instanceof TLRPC.Chat) && !ChatObject.canWriteToChat((TLRPC.Chat) tLObject)) {
                            arrayList4.remove(i16);
                            i16--;
                        }
                        i16++;
                    }
                }
                ShareAlert.this.recentSearchObjects = arrayList4;
                ShareAlert.this.recentSearchObjectsById = longSparseArray;
                for (int i22 = 0; i22 < ShareAlert.this.recentSearchObjects.size(); i22++) {
                    DialogsSearchAdapter.RecentSearchObject recentSearchObject = (DialogsSearchAdapter.RecentSearchObject) ShareAlert.this.recentSearchObjects.get(i22);
                    TLObject tLObject2 = recentSearchObject.object;
                    if (tLObject2 instanceof TLRPC.User) {
                        MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).putUser((TLRPC.User) recentSearchObject.object, true);
                    } else if (tLObject2 instanceof TLRPC.Chat) {
                        MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).putChat((TLRPC.Chat) recentSearchObject.object, true);
                    } else if (tLObject2 instanceof TLRPC.EncryptedChat) {
                        MessagesController.getInstance(((BottomSheet) ShareAlert.this).currentAccount).putEncryptedChat((TLRPC.EncryptedChat) recentSearchObject.object, true);
                    }
                }
                ShareAlert.this.searchAdapter.notifyDataSetChanged();
            }
        });
        MediaDataController.getInstance(this.currentAccount).loadHints(true);
        AndroidUtilities.updateViewVisibilityAnimated(this.gridView, true, 1.0f, false);
        AndroidUtilities.updateViewVisibilityAnimated(this.searchGridView, false, 1.0f, false);
    }

    public void checkCurrentList(boolean z) {
        LinearLayoutManager linearLayoutManager;
        int paddingTop;
        boolean z2 = true;
        if (!TextUtils.isEmpty(this.searchView.searchEditText.getText()) || ((this.keyboardVisible && this.searchView.searchEditText.hasFocus()) || this.searchWasVisibleBeforeTopics)) {
            this.updateSearchAdapter = true;
            if (this.selectedTopicDialog == null) {
                AndroidUtilities.updateViewVisibilityAnimated(this.gridView, false, 0.98f, true);
                AndroidUtilities.updateViewVisibilityAnimated(this.searchGridView, true);
            }
        } else {
            if (this.selectedTopicDialog == null) {
                AndroidUtilities.updateViewVisibilityAnimated(this.gridView, true, 0.98f, true);
                AndroidUtilities.updateViewVisibilityAnimated(this.searchGridView, false);
            }
            z2 = false;
        }
        if (this.searchIsVisible != z2 || z) {
            this.searchIsVisible = z2;
            this.searchAdapter.notifyDataSetChanged();
            this.listAdapter.notifyDataSetChanged();
            if (!this.searchIsVisible) {
                this.layoutManager.scrollToPositionWithOffset(0, 0);
                return;
            }
            if (this.lastOffset == Integer.MAX_VALUE) {
                linearLayoutManager = (LinearLayoutManager) this.searchGridView.getLayoutManager();
                paddingTop = -this.searchGridView.getPaddingTop();
            } else {
                linearLayoutManager = (LinearLayoutManager) this.searchGridView.getLayoutManager();
                paddingTop = this.lastOffset - this.searchGridView.getPaddingTop();
            }
            linearLayoutManager.scrollToPositionWithOffset(0, paddingTop);
            this.searchAdapter.searchDialogs(this.searchView.searchEditText.getText().toString());
        }
    }

    private void collapseTopics() {
        TLRPC.Dialog dialog = this.selectedTopicDialog;
        if (dialog == null) {
            return;
        }
        final View view = null;
        this.selectedTopicDialog = null;
        for (int i = 0; i < getMainGridView().getChildCount(); i++) {
            View childAt = getMainGridView().getChildAt(i);
            if ((childAt instanceof ShareDialogCell) && ((ShareDialogCell) childAt).getCurrentDialog() == dialog.id) {
                view = childAt;
            }
        }
        if (view == null) {
            return;
        }
        SpringAnimation springAnimation = this.topicsAnimation;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        getMainGridView().setVisibility(0);
        this.searchView.setVisibility(0);
        if (this.searchIsVisible || this.searchWasVisibleBeforeTopics) {
            this.sizeNotifierFrameLayout.adjustPanLayoutHelper.ignoreOnce();
            this.searchView.searchEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.searchView.searchEditText);
        }
        final int[] iArr = new int[2];
        SpringAnimation springAnimation2 = new SpringAnimation(new FloatValueHolder(1000.0f));
        SpringForce springForce = new SpringForce(0.0f);
        ChatActivity chatActivity = this.parentFragment;
        SpringAnimation spring = springAnimation2.setSpring(springForce.setStiffness((chatActivity == null || !chatActivity.shareAlertDebugTopicsSlowMotion) ? 800.0f : 10.0f).setDampingRatio(1.0f));
        this.topicsAnimation = spring;
        spring.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
            @Override
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                ShareAlert.this.lambda$collapseTopics$17(view, iArr, dynamicAnimation, f, f2);
            }
        });
        this.topicsAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
            @Override
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                ShareAlert.this.lambda$collapseTopics$18(dynamicAnimation, z, f, f2);
            }
        });
        this.topicsAnimation.start();
    }

    private void copyLink(Context context) {
        final boolean z = false;
        if (this.exportedMessageLink == null && this.linkToCopy[0] == null) {
            return;
        }
        try {
            String link = getLink();
            ClipboardManager clipboardManager = (ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard");
            if (link == null) {
                link = this.exportedMessageLink.link;
            }
            clipboardManager.setPrimaryClip(ClipData.newPlainText("label", link));
            ShareAlertDelegate shareAlertDelegate = this.delegate;
            if (shareAlertDelegate != null && shareAlertDelegate.didCopy()) {
                return;
            }
            if (this.parentActivity instanceof LaunchActivity) {
                TLRPC.TL_exportedMessageLink tL_exportedMessageLink = this.exportedMessageLink;
                if (tL_exportedMessageLink != null && tL_exportedMessageLink.link.contains("/c/")) {
                    z = true;
                }
                ((LaunchActivity) this.parentActivity).showBulletin(new Function() {
                    @Override
                    public final Object apply(Object obj) {
                        Bulletin lambda$copyLink$25;
                        lambda$copyLink$25 = ShareAlert.lambda$copyLink$25(z, (BulletinFactory) obj);
                        return lambda$copyLink$25;
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static ShareAlert createShareAlert(Context context, MessageObject messageObject, String str, boolean z, String str2, boolean z2) {
        ArrayList arrayList;
        if (messageObject != null) {
            arrayList = new ArrayList();
            arrayList.add(messageObject);
        } else {
            arrayList = null;
        }
        return new ShareAlert(context, null, arrayList, str, null, z, str2, null, z2, false);
    }

    public int getCurrentTop() {
        if (this.gridView.getChildCount() == 0) {
            return -1000;
        }
        int i = 0;
        View childAt = this.gridView.getChildAt(0);
        RecyclerListView.Holder holder = (RecyclerListView.Holder) this.gridView.findContainingViewHolder(childAt);
        if (holder == null) {
            return -1000;
        }
        int paddingTop = this.gridView.getPaddingTop();
        if (holder.getLayoutPosition() == 0 && childAt.getTop() >= 0) {
            i = childAt.getTop();
        }
        return paddingTop - i;
    }

    private String getLink() {
        TLRPC.TL_exportedMessageLink tL_exportedMessageLink;
        SwitchView switchView = this.switchView;
        String str = switchView != null ? this.linkToCopy[switchView.currentTab] : this.linkToCopy[0];
        if (str == null && (tL_exportedMessageLink = this.exportedMessageLink) != null) {
            str = tL_exportedMessageLink.link;
        }
        CheckBox2 checkBox2 = this.timestampCheckbox;
        if (checkBox2 != null && checkBox2.isChecked()) {
            try {
                str = Uri.parse(str).buildUpon().appendQueryParameter("t", AndroidUtilities.formatTimestamp(this.timestamp)).build().toString();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return str == null ? "" : str;
    }

    private RecyclerListView getMainGridView() {
        return (this.searchIsVisible || this.searchWasVisibleBeforeTopics) ? this.searchGridView : this.gridView;
    }

    public void invalidateTopicsAnimation(View view, int[] iArr, float f) {
        this.topicsGridView.setPivotX(view.getX() + (view.getWidth() / 2.0f));
        this.topicsGridView.setPivotY(view.getY() + (view.getHeight() / 2.0f));
        float f2 = 0.25f * f;
        float f3 = 0.75f + f2;
        this.topicsGridView.setScaleX(f3);
        this.topicsGridView.setScaleY(f3);
        this.topicsGridView.setAlpha(f);
        RecyclerListView mainGridView = getMainGridView();
        mainGridView.setPivotX(view.getX() + (view.getWidth() / 2.0f));
        mainGridView.setPivotY(view.getY() + (view.getHeight() / 2.0f));
        float f4 = f2 + 1.0f;
        mainGridView.setScaleX(f4);
        mainGridView.setScaleY(f4);
        float f5 = 1.0f - f;
        mainGridView.setAlpha(f5);
        this.searchView.setPivotX(r4.getWidth() / 2.0f);
        this.searchView.setPivotY(0.0f);
        float f6 = (0.1f * f5) + 0.9f;
        this.searchView.setScaleX(f6);
        this.searchView.setScaleY(f6);
        this.searchView.setAlpha(f5);
        this.topicsBackActionBar.getBackButton().setTranslationX((-AndroidUtilities.dp(16.0f)) * f5);
        this.topicsBackActionBar.getTitleTextView().setTranslationY(AndroidUtilities.dp(16.0f) * f5);
        this.topicsBackActionBar.getSubtitleTextView().setTranslationY(AndroidUtilities.dp(16.0f) * f5);
        this.topicsBackActionBar.setAlpha(f);
        this.topicsGridView.getLocationInWindow(iArr);
        float interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(f);
        for (int i = 0; i < mainGridView.getChildCount(); i++) {
            View childAt = mainGridView.getChildAt(i);
            if (childAt instanceof ShareDialogCell) {
                childAt.setTranslationX((childAt.getX() - view.getX()) * 0.5f * interpolation);
                childAt.setTranslationY((childAt.getY() - view.getY()) * 0.5f * interpolation);
                if (childAt != view) {
                    childAt.setAlpha(1.0f - (Math.min(f, 0.5f) / 0.5f));
                } else {
                    childAt.setAlpha(f5);
                }
            }
        }
        for (int i2 = 0; i2 < this.topicsGridView.getChildCount(); i2++) {
            View childAt2 = this.topicsGridView.getChildAt(i2);
            if (childAt2 instanceof ShareTopicCell) {
                double d = -(childAt2.getX() - view.getX());
                double d2 = 1.0f - interpolation;
                double pow = Math.pow(d2, 2.0d);
                Double.isNaN(d);
                childAt2.setTranslationX((float) (d * pow));
                double d3 = -((childAt2.getY() + this.topicsGridView.getTranslationY()) - view.getY());
                double pow2 = Math.pow(d2, 2.0d);
                Double.isNaN(d3);
                childAt2.setTranslationY((float) (d3 * pow2));
            }
        }
        this.containerView.requestLayout();
        mainGridView.invalidate();
    }

    public void lambda$collapseTopics$17(View view, int[] iArr, DynamicAnimation dynamicAnimation, float f, float f2) {
        invalidateTopicsAnimation(view, iArr, f / 1000.0f);
    }

    public void lambda$collapseTopics$18(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.topicsGridView.setVisibility(8);
        this.topicsBackActionBar.setVisibility(8);
        this.shareTopicsAdapter.topics = null;
        this.shareTopicsAdapter.notifyDataSetChanged();
        this.topicsAnimation = null;
        this.searchWasVisibleBeforeTopics = false;
    }

    public static Bulletin lambda$copyLink$25(boolean z, BulletinFactory bulletinFactory) {
        return bulletinFactory.createCopyLinkBulletin(z);
    }

    public void lambda$new$0(TLObject tLObject, Context context) {
        if (tLObject != null) {
            this.exportedMessageLink = (TLRPC.TL_exportedMessageLink) tLObject;
            updateLinkTextView();
            if (this.copyLinkOnEnd) {
                copyLink(context);
            }
        }
        this.loadingLink = false;
    }

    public void lambda$new$1(final Context context, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ShareAlert.this.lambda$new$0(tLObject, context);
            }
        });
    }

    public static boolean lambda$new$10(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$new$11(View view) {
        sendInternal(true);
    }

    public boolean lambda$new$12(ImageView imageView, View view) {
        return onSendLongClick(imageView);
    }

    public void lambda$new$13(View view) {
        this.timestampCheckbox.setChecked(!r3.isChecked(), true);
        updateLinkTextView();
    }

    public static Integer lambda$new$2(Integer num) {
        return 0;
    }

    public void lambda$new$3(View view, int i) {
        TLRPC.Dialog dialog;
        TLRPC.TL_forumTopic item = this.shareTopicsAdapter.getItem(i);
        if (item == null || (dialog = this.selectedTopicDialog) == null) {
            return;
        }
        this.selectedDialogs.put(dialog.id, dialog);
        this.selectedDialogTopics.put(dialog, item);
        updateSelectedCount(2);
        if (this.searchIsVisible || this.searchWasVisibleBeforeTopics) {
            if (((TLRPC.Dialog) this.listAdapter.dialogsMap.get(dialog.id)) == null) {
                this.listAdapter.dialogsMap.put(dialog.id, dialog);
                this.listAdapter.dialogs.add(!this.listAdapter.dialogs.isEmpty() ? 1 : 0, dialog);
            }
            this.listAdapter.notifyDataSetChanged();
            this.updateSearchAdapter = false;
            this.searchView.searchEditText.setText("");
            checkCurrentList(false);
        }
        for (int i2 = 0; i2 < getMainGridView().getChildCount(); i2++) {
            View childAt = getMainGridView().getChildAt(i2);
            if (childAt instanceof ShareDialogCell) {
                ShareDialogCell shareDialogCell = (ShareDialogCell) childAt;
                if (shareDialogCell.getCurrentDialog() == this.selectedTopicDialog.id) {
                    shareDialogCell.setTopic(item, true);
                    shareDialogCell.setChecked(true, true);
                }
            }
        }
        collapseTopics();
    }

    public static Integer lambda$new$4(Integer num) {
        return 0;
    }

    public void lambda$new$5(View view, int i) {
        TLRPC.Dialog item;
        if (i >= 0 && (item = this.listAdapter.getItem(i)) != null) {
            selectDialog(view, item);
        }
    }

    public static Integer lambda$new$6(Integer num) {
        return 0;
    }

    public void lambda$new$7(View view, int i) {
        TLRPC.Dialog item;
        if (i >= 0 && (item = this.searchAdapter.getItem(i)) != null) {
            selectDialog(view, item);
        }
    }

    public void lambda$new$8(View view) {
        if (this.selectedDialogs.size() == 0) {
            if (this.isChannel || this.linkToCopy[0] != null) {
                lambda$new$0();
                if (this.linkToCopy[0] != null || !this.loadingLink) {
                    copyLink(getContext());
                } else {
                    this.copyLinkOnEnd = true;
                    Toast.makeText(getContext(), LocaleController.getString(R.string.Loading), 0).show();
                }
            }
        }
    }

    public void lambda$new$9(View view) {
        if (this.selectedDialogs.size() == 0) {
            if (this.isChannel || this.linkToCopy[0] != null) {
                lambda$new$0();
                if (this.linkToCopy[0] != null || !this.loadingLink) {
                    copyLink(getContext());
                } else {
                    this.copyLinkOnEnd = true;
                    Toast.makeText(getContext(), LocaleController.getString(R.string.Loading), 0).show();
                }
            }
        }
    }

    public void lambda$onSendLongClick$19(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public void lambda$onSendLongClick$20(ActionBarMenuSubItem actionBarMenuSubItem, ActionBarMenuSubItem actionBarMenuSubItem2, View view) {
        this.showSendersName = true;
        actionBarMenuSubItem.setChecked(true);
        actionBarMenuSubItem2.setChecked(!this.showSendersName);
    }

    public void lambda$onSendLongClick$21(ActionBarMenuSubItem actionBarMenuSubItem, ActionBarMenuSubItem actionBarMenuSubItem2, View view) {
        this.showSendersName = false;
        actionBarMenuSubItem.setChecked(false);
        actionBarMenuSubItem2.setChecked(!this.showSendersName);
    }

    public void lambda$onSendLongClick$22(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.sendPopupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
    }

    public void lambda$onSendLongClick$23(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        sendInternal(false);
    }

    public void lambda$onSendLongClick$24(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        sendInternal(true);
    }

    public void lambda$selectDialog$16(AtomicReference atomicReference, NotificationCenter.NotificationCenterDelegate notificationCenterDelegate, TLRPC.Dialog dialog) {
        atomicReference.set(null);
        notificationCenterDelegate.didReceivedNotification(NotificationCenter.topicsDidLoaded, this.currentAccount, Long.valueOf(-dialog.id));
    }

    public static void lambda$showPremiumBlockedToast$14() {
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment != null) {
            BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
            bottomSheetParams.transitionFromLeft = true;
            bottomSheetParams.allowNestedScroll = false;
            lastFragment.showAsSheet(new PremiumPreviewFragment("noncontacts"), bottomSheetParams);
        }
    }

    public void lambda$showPremiumBlockedToast$15() {
        Runnable runnable = new Runnable() {
            @Override
            public final void run() {
                ShareAlert.lambda$showPremiumBlockedToast$14();
            }
        };
        if (!isKeyboardVisible()) {
            runnable.run();
            return;
        }
        SearchField searchField = this.searchView;
        if (searchField != null) {
            AndroidUtilities.hideKeyboard(searchField.searchEditText);
        }
        AndroidUtilities.runOnUIThread(runnable, 300L);
    }

    private boolean onSendLongClick(View view) {
        ChatActivity chatActivity;
        if (this.parentActivity == null) {
            return false;
        }
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        if (this.sendingMessageObjects != null) {
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity, this.resourcesProvider);
            if (this.darkTheme) {
                actionBarPopupWindowLayout.setBackgroundColor(getThemedColor(Theme.key_voipgroup_inviteMembersBackground));
            }
            actionBarPopupWindowLayout.setAnimationEnabled(false);
            actionBarPopupWindowLayout.setOnTouchListener(new View.OnTouchListener() {
                private android.graphics.Rect popupRect = new android.graphics.Rect();

                AnonymousClass24() {
                }

                @Override
                public boolean onTouch(View view2, MotionEvent motionEvent) {
                    if (motionEvent.getActionMasked() != 0 || ShareAlert.this.sendPopupWindow == null || !ShareAlert.this.sendPopupWindow.isShowing()) {
                        return false;
                    }
                    view2.getHitRect(this.popupRect);
                    if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                        return false;
                    }
                    ShareAlert.this.sendPopupWindow.dismiss();
                    return false;
                }
            });
            actionBarPopupWindowLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                @Override
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    ShareAlert.this.lambda$onSendLongClick$19(keyEvent);
                }
            });
            actionBarPopupWindowLayout.setShownFromBottom(false);
            final ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), true, true, false, this.resourcesProvider);
            if (this.darkTheme) {
                actionBarMenuSubItem.setTextColor(getThemedColor(Theme.key_voipgroup_nameText));
            }
            actionBarPopupWindowLayout.addView((View) actionBarMenuSubItem, LayoutHelper.createLinear(-1, 48));
            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(R.string.ShowSendersName), 0);
            this.showSendersName = true;
            actionBarMenuSubItem.setChecked(true);
            final ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(getContext(), true, false, true, this.resourcesProvider);
            if (this.darkTheme) {
                actionBarMenuSubItem2.setTextColor(getThemedColor(Theme.key_voipgroup_nameText));
            }
            actionBarPopupWindowLayout.addView((View) actionBarMenuSubItem2, LayoutHelper.createLinear(-1, 48));
            actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString(R.string.HideSendersName), 0);
            actionBarMenuSubItem2.setChecked(!this.showSendersName);
            actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    ShareAlert.this.lambda$onSendLongClick$20(actionBarMenuSubItem, actionBarMenuSubItem2, view2);
                }
            });
            actionBarMenuSubItem2.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    ShareAlert.this.lambda$onSendLongClick$21(actionBarMenuSubItem, actionBarMenuSubItem2, view2);
                }
            });
            actionBarPopupWindowLayout.setupRadialSelectors(getThemedColor(Theme.key_dialogButtonSelector));
            linearLayout.addView(actionBarPopupWindowLayout, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, -8.0f));
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout2 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(this.parentActivity, this.resourcesProvider);
        if (this.darkTheme) {
            actionBarPopupWindowLayout2.setBackgroundColor(Theme.getColor(Theme.key_voipgroup_inviteMembersBackground));
        }
        actionBarPopupWindowLayout2.setAnimationEnabled(false);
        actionBarPopupWindowLayout2.setOnTouchListener(new View.OnTouchListener() {
            private android.graphics.Rect popupRect = new android.graphics.Rect();

            AnonymousClass25() {
            }

            @Override
            public boolean onTouch(View view2, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() != 0 || ShareAlert.this.sendPopupWindow == null || !ShareAlert.this.sendPopupWindow.isShowing()) {
                    return false;
                }
                view2.getHitRect(this.popupRect);
                if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                    return false;
                }
                ShareAlert.this.sendPopupWindow.dismiss();
                return false;
            }
        });
        actionBarPopupWindowLayout2.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
            @Override
            public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                ShareAlert.this.lambda$onSendLongClick$22(keyEvent);
            }
        });
        actionBarPopupWindowLayout2.setShownFromBottom(false);
        ActionBarMenuSubItem actionBarMenuSubItem3 = new ActionBarMenuSubItem(getContext(), true, true, this.resourcesProvider);
        if (this.darkTheme) {
            actionBarMenuSubItem3.setTextColor(getThemedColor(Theme.key_voipgroup_nameText));
            actionBarMenuSubItem3.setIconColor(getThemedColor(Theme.key_windowBackgroundWhiteHintText));
        }
        actionBarMenuSubItem3.setTextAndIcon(LocaleController.getString(R.string.SendWithoutSound), R.drawable.input_notify_off);
        actionBarMenuSubItem3.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarPopupWindowLayout2.addView((View) actionBarMenuSubItem3, LayoutHelper.createLinear(-1, 48));
        actionBarMenuSubItem3.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                ShareAlert.this.lambda$onSendLongClick$23(view2);
            }
        });
        ActionBarMenuSubItem actionBarMenuSubItem4 = new ActionBarMenuSubItem(getContext(), true, true, this.resourcesProvider);
        if (this.darkTheme) {
            actionBarMenuSubItem4.setTextColor(getThemedColor(Theme.key_voipgroup_nameText));
            actionBarMenuSubItem4.setIconColor(getThemedColor(Theme.key_windowBackgroundWhiteHintText));
        }
        actionBarMenuSubItem4.setTextAndIcon(LocaleController.getString(R.string.SendMessage), R.drawable.msg_send);
        actionBarMenuSubItem4.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarPopupWindowLayout2.addView((View) actionBarMenuSubItem4, LayoutHelper.createLinear(-1, 48));
        actionBarMenuSubItem4.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                ShareAlert.this.lambda$onSendLongClick$24(view2);
            }
        });
        actionBarPopupWindowLayout2.setupRadialSelectors(getThemedColor(Theme.key_dialogButtonSelector));
        linearLayout.addView(actionBarPopupWindowLayout2, LayoutHelper.createLinear(-1, -2));
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(linearLayout, -2, -2);
        this.sendPopupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setAnimationEnabled(false);
        this.sendPopupWindow.setAnimationStyle(R.style.PopupContextAnimation2);
        this.sendPopupWindow.setOutsideTouchable(true);
        this.sendPopupWindow.setClippingEnabled(true);
        this.sendPopupWindow.setInputMethodMode(2);
        this.sendPopupWindow.setSoftInputMode(0);
        this.sendPopupWindow.getContentView().setFocusableInTouchMode(true);
        SharedConfig.removeScheduledOrNoSoundHint();
        linearLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.sendPopupWindow.setFocusable(true);
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        this.sendPopupWindow.showAtLocation(view, 51, ((iArr[0] + view.getMeasuredWidth()) - linearLayout.getMeasuredWidth()) + AndroidUtilities.dp(8.0f), (!this.keyboardVisible || (chatActivity = this.parentFragment) == null || chatActivity.contentView.getMeasuredHeight() <= AndroidUtilities.dp(58.0f)) ? (iArr[1] - linearLayout.getMeasuredHeight()) - AndroidUtilities.dp(2.0f) : iArr[1] + view.getMeasuredHeight());
        this.sendPopupWindow.dimBehind();
        try {
            view.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        return true;
    }

    private void runShadowAnimation(int i, boolean z) {
        if ((!z || this.shadow[i].getTag() == null) && (z || this.shadow[i].getTag() != null)) {
            return;
        }
        this.shadow[i].setTag(z ? null : 1);
        if (z) {
            this.shadow[i].setVisibility(0);
        }
        AnimatorSet animatorSet = this.shadowAnimation[i];
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.shadowAnimation[i] = new AnimatorSet();
        this.shadowAnimation[i].playTogether(ObjectAnimator.ofFloat(this.shadow[i], (Property<View, Float>) View.ALPHA, z ? 1.0f : 0.0f));
        this.shadowAnimation[i].setDuration(150L);
        this.shadowAnimation[i].addListener(new AnimatorListenerAdapter() {
            final int val$num;
            final boolean val$show;

            AnonymousClass26(int i2, boolean z2) {
                r2 = i2;
                r3 = z2;
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                if (ShareAlert.this.shadowAnimation[r2] == null || !ShareAlert.this.shadowAnimation[r2].equals(animator)) {
                    return;
                }
                ShareAlert.this.shadowAnimation[r2] = null;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (ShareAlert.this.shadowAnimation[r2] == null || !ShareAlert.this.shadowAnimation[r2].equals(animator)) {
                    return;
                }
                if (!r3) {
                    ShareAlert.this.shadow[r2].setVisibility(4);
                }
                ShareAlert.this.shadowAnimation[r2] = null;
            }
        });
        this.shadowAnimation[i2].start();
    }

    public void selectDialog(View view, final TLRPC.Dialog dialog) {
        DialogsSearchAdapter.CategoryAdapterRecycler categoryAdapterRecycler;
        if (dialog instanceof ShareDialogsAdapter.MyStoryDialog) {
            onShareStory(view);
            return;
        }
        if (dialog != null && (((view instanceof ShareDialogCell) && ((ShareDialogCell) view).isBlocked()) || ((view instanceof ProfileSearchCell) && ((ProfileSearchCell) view).isBlocked()))) {
            showPremiumBlockedToast(view, dialog.id);
            return;
        }
        if (this.topicsGridView.getVisibility() != 8 || this.parentActivity == null) {
            return;
        }
        if (DialogObject.isChatDialog(dialog.id)) {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialog.id));
            if (ChatObject.isChannel(chat) && !chat.megagroup && (!ChatObject.isCanWriteToChannel(-dialog.id, this.currentAccount) || this.hasPoll == 2)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this.parentActivity);
                builder.setTitle(LocaleController.getString(R.string.SendMessageTitle));
                builder.setMessage(LocaleController.getString(this.hasPoll == 2 ? this.isChannel ? R.string.PublicPollCantForward : ChatObject.isActionBannedByDefault(chat, 10) ? R.string.ErrorSendRestrictedPollsAll : R.string.ErrorSendRestrictedPolls : R.string.ChannelCantSendMessage));
                builder.setNegativeButton(LocaleController.getString(R.string.OK), null);
                builder.show();
                return;
            }
        } else if (DialogObject.isEncryptedDialog(dialog.id) && this.hasPoll != 0) {
            AlertDialog.Builder builder2 = new AlertDialog.Builder(this.parentActivity);
            builder2.setTitle(LocaleController.getString(R.string.SendMessageTitle));
            builder2.setMessage(LocaleController.getString(this.hasPoll != 0 ? R.string.PollCantForwardSecretChat : R.string.InvoiceCantForwardSecretChat));
            builder2.setNegativeButton(LocaleController.getString(R.string.OK), null);
            builder2.show();
            return;
        }
        if (this.selectedDialogs.indexOfKey(dialog.id) >= 0) {
            this.selectedDialogs.remove(dialog.id);
            this.selectedDialogTopics.remove(dialog);
            if (view instanceof ProfileSearchCell) {
                ((ProfileSearchCell) view).setChecked(false, true);
            } else if (view instanceof ShareDialogCell) {
                ((ShareDialogCell) view).setChecked(false, true);
            }
            updateSelectedCount(1);
        } else {
            if (DialogObject.isChatDialog(dialog.id) && MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialog.id)) != null && MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-dialog.id)).forum) {
                this.selectedTopicDialog = dialog;
                this.topicsLayoutManager.scrollToPositionWithOffset(0, this.scrollOffsetY - this.topicsGridView.getPaddingTop());
                final AtomicReference atomicReference = new AtomicReference();
                final AnonymousClass23 anonymousClass23 = new AnonymousClass23(dialog, atomicReference, view);
                atomicReference.set(new Runnable() {
                    @Override
                    public final void run() {
                        ShareAlert.this.lambda$selectDialog$16(atomicReference, anonymousClass23, dialog);
                    }
                });
                NotificationCenter notificationCenter = NotificationCenter.getInstance(this.currentAccount);
                int i = NotificationCenter.topicsDidLoaded;
                notificationCenter.addObserver(anonymousClass23, i);
                if (MessagesController.getInstance(this.currentAccount).getTopicsController().getTopics(-dialog.id) != null) {
                    anonymousClass23.didReceivedNotification(i, this.currentAccount, Long.valueOf(-dialog.id));
                    return;
                } else {
                    MessagesController.getInstance(this.currentAccount).getTopicsController().loadTopics(-dialog.id);
                    AndroidUtilities.runOnUIThread((Runnable) atomicReference.get(), 300L);
                    return;
                }
            }
            this.selectedDialogs.put(dialog.id, dialog);
            if (view instanceof ProfileSearchCell) {
                ((ProfileSearchCell) view).setChecked(true, true);
            } else if (view instanceof ShareDialogCell) {
                ((ShareDialogCell) view).setChecked(true, true);
            }
            updateSelectedCount(2);
            long j = UserConfig.getInstance(this.currentAccount).clientUserId;
            if (this.searchIsVisible) {
                TLRPC.Dialog dialog2 = (TLRPC.Dialog) this.listAdapter.dialogsMap.get(dialog.id);
                if (dialog2 == null) {
                    this.listAdapter.dialogsMap.put(dialog.id, dialog);
                    this.listAdapter.dialogs.add(1 ^ (this.listAdapter.dialogs.isEmpty() ? 1 : 0), dialog);
                } else if (dialog2.id != j) {
                    this.listAdapter.dialogs.remove(dialog2);
                    this.listAdapter.dialogs.add(1 ^ (this.listAdapter.dialogs.isEmpty() ? 1 : 0), dialog2);
                }
                this.listAdapter.notifyDataSetChanged();
                this.updateSearchAdapter = false;
                this.searchView.searchEditText.setText("");
                checkCurrentList(false);
                this.searchView.hideKeyboard();
            }
        }
        ShareSearchAdapter shareSearchAdapter = this.searchAdapter;
        if (shareSearchAdapter == null || (categoryAdapterRecycler = shareSearchAdapter.categoryAdapter) == null) {
            return;
        }
        categoryAdapterRecycler.notifyItemRangeChanged(0, categoryAdapterRecycler.getItemCount());
    }

    private boolean showCommentTextView(boolean r13) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ShareAlert.showCommentTextView(boolean):boolean");
    }

    public void showPremiumBlockedToast(View view, long j) {
        int i = -this.shiftDp;
        this.shiftDp = i;
        AndroidUtilities.shakeViewSpring(view, i);
        BotWebViewVibrationEffect.APP_ERROR.vibrate();
        String userName = j >= 0 ? UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j))) : "";
        (MessagesController.getInstance(this.currentAccount).premiumFeaturesBlocked() ? BulletinFactory.of(this.bulletinContainer, this.resourcesProvider).createSimpleBulletin(R.raw.star_premium_2, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.UserBlockedNonPremium, userName))) : BulletinFactory.of(this.bulletinContainer, this.resourcesProvider).createSimpleBulletin(R.raw.star_premium_2, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.UserBlockedNonPremium, userName)), LocaleController.getString(R.string.UserBlockedNonPremiumButton), new Runnable() {
            @Override
            public final void run() {
                ShareAlert.this.lambda$showPremiumBlockedToast$15();
            }
        })).show();
    }

    public void updateLayout() {
        if (this.panTranslationMoveLayout) {
            return;
        }
        RecyclerListView recyclerListView = this.searchIsVisible ? this.searchGridView : this.gridView;
        if (recyclerListView.getChildCount() <= 0) {
            return;
        }
        View childAt = recyclerListView.getChildAt(0);
        for (int i = 0; i < recyclerListView.getChildCount(); i++) {
            if (recyclerListView.getChildAt(i).getTop() < childAt.getTop()) {
                childAt = recyclerListView.getChildAt(i);
            }
        }
        RecyclerListView.Holder holder = (RecyclerListView.Holder) recyclerListView.findContainingViewHolder(childAt);
        int top = childAt.getTop() - AndroidUtilities.dp(8.0f);
        int i2 = (top <= 0 || holder == null || holder.getAdapterPosition() != 0) ? 0 : top;
        if (top < 0 || holder == null || holder.getAdapterPosition() != 0) {
            this.lastOffset = Integer.MAX_VALUE;
            runShadowAnimation(0, true);
            top = i2;
        } else {
            this.lastOffset = childAt.getTop();
            runShadowAnimation(0, false);
        }
        if (this.topicsGridView.getVisibility() == 0) {
            RecyclerListView recyclerListView2 = this.topicsGridView;
            if (recyclerListView2.getChildCount() <= 0) {
                return;
            }
            View childAt2 = recyclerListView2.getChildAt(0);
            for (int i3 = 0; i3 < recyclerListView2.getChildCount(); i3++) {
                if (recyclerListView2.getChildAt(i3).getTop() < childAt2.getTop()) {
                    childAt2 = recyclerListView2.getChildAt(i3);
                }
            }
            RecyclerListView.Holder holder2 = (RecyclerListView.Holder) recyclerListView2.findContainingViewHolder(childAt2);
            int top2 = childAt2.getTop() - AndroidUtilities.dp(8.0f);
            int i4 = (top2 <= 0 || holder2 == null || holder2.getAdapterPosition() != 0) ? 0 : top2;
            if (top2 < 0 || holder2 == null || holder2.getAdapterPosition() != 0) {
                this.lastOffset = Integer.MAX_VALUE;
                runShadowAnimation(0, true);
                top2 = i4;
            } else {
                this.lastOffset = childAt2.getTop();
                runShadowAnimation(0, false);
            }
            top = AndroidUtilities.lerp(top, top2, this.topicsGridView.getAlpha());
        }
        int i5 = this.scrollOffsetY;
        if (i5 != top) {
            this.previousScrollOffsetY = i5;
            RecyclerListView recyclerListView3 = this.gridView;
            float f = top;
            int i6 = (int) (this.currentPanTranslationY + f);
            this.scrollOffsetY = i6;
            recyclerListView3.setTopGlowOffset(i6);
            RecyclerListView recyclerListView4 = this.searchGridView;
            int i7 = (int) (this.currentPanTranslationY + f);
            this.scrollOffsetY = i7;
            recyclerListView4.setTopGlowOffset(i7);
            RecyclerListView recyclerListView5 = this.topicsGridView;
            int i8 = (int) (f + this.currentPanTranslationY);
            this.scrollOffsetY = i8;
            recyclerListView5.setTopGlowOffset(i8);
            this.frameLayout.setTranslationY(this.scrollOffsetY + this.currentPanTranslationY);
            this.searchEmptyView.setTranslationY(this.scrollOffsetY + this.currentPanTranslationY);
            this.containerView.invalidate();
        }
    }

    public void updateLinkTextView() {
        int i;
        if (this.linkTextView != null) {
            String link = getLink();
            if (link != null) {
                if (!link.startsWith("https://")) {
                    i = link.startsWith("http://") ? 7 : 8;
                }
                link = link.substring(i);
            }
            this.linkTextView.setText(link);
        }
    }

    @Override
    public boolean canDismissWithSwipe() {
        return false;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int i3 = NotificationCenter.dialogsNeedReload;
        if (i == i3) {
            ShareDialogsAdapter shareDialogsAdapter = this.listAdapter;
            if (shareDialogsAdapter != null) {
                shareDialogsAdapter.fetchDialogs();
            }
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, i3);
        }
    }

    @Override
    public void lambda$new$0() {
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            AndroidUtilities.hideKeyboard(editTextEmoji.getEditText());
        }
        this.fullyShown = false;
        super.lambda$new$0();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.dialogsNeedReload);
    }

    @Override
    public void dismissInternal() {
        super.dismissInternal();
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
    }

    @Override
    public int getContainerViewHeight() {
        return this.containerView.getMeasuredHeight() - this.containerViewTop;
    }

    @Override
    public void onBackPressed() {
        if (this.selectedTopicDialog != null) {
            collapseTopics();
            return;
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji == null || !editTextEmoji.isPopupShowing()) {
            super.onBackPressed();
        } else {
            this.commentTextView.hidePopup(true);
        }
    }

    public void onSend(LongSparseArray longSparseArray, int i, TLRPC.TL_forumTopic tL_forumTopic) {
    }

    protected void onShareStory(View view) {
    }

    protected void sendInternal(boolean z) {
        char c;
        MessageObject messageObject;
        long j;
        int i;
        char c2;
        MessageObject messageObject2;
        int i2;
        TLRPC.TL_forumTopic tL_forumTopic;
        SendMessagesHelper.SendMessageParams of;
        ArrayList arrayList;
        MessageObject messageObject3;
        long j2;
        int i3;
        ArrayList arrayList2;
        int i4 = 1;
        ?? r2 = 0;
        for (int i5 = 0; i5 < this.selectedDialogs.size(); i5++) {
            if (AlertsCreator.checkSlowMode(getContext(), this.currentAccount, this.selectedDialogs.keyAt(i5), this.frameLayout2.getTag() != null && this.commentTextView.length() > 0)) {
                return;
            }
        }
        CharSequence[] charSequenceArr = {this.commentTextView.getText()};
        ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, true);
        CheckBox2 checkBox2 = this.timestampCheckbox;
        int i6 = (checkBox2 == null || !checkBox2.isChecked()) ? -1 : this.timestamp;
        TLRPC.TL_forumTopic tL_forumTopic2 = null;
        if (this.sendingMessageObjects != null) {
            ArrayList arrayList3 = new ArrayList();
            int i7 = 0;
            while (true) {
                if (i7 >= this.selectedDialogs.size()) {
                    arrayList = arrayList3;
                    break;
                }
                long keyAt = this.selectedDialogs.keyAt(i7);
                TLRPC.TL_forumTopic tL_forumTopic3 = (TLRPC.TL_forumTopic) this.selectedDialogTopics.get(this.selectedDialogs.get(keyAt));
                MessageObject messageObject4 = tL_forumTopic3 != null ? new MessageObject(this.currentAccount, tL_forumTopic3.topicStartMessage, r2, r2) : tL_forumTopic2;
                if (messageObject4 != 0) {
                    messageObject4.isTopicMainMessage = true;
                }
                if (this.frameLayout2.getTag() == null || this.commentTextView.length() <= 0) {
                    messageObject3 = messageObject4;
                    j2 = keyAt;
                    i3 = i7;
                    arrayList2 = arrayList3;
                } else {
                    SendMessagesHelper sendMessagesHelper = SendMessagesHelper.getInstance(this.currentAccount);
                    CharSequence charSequence = charSequenceArr[r2];
                    messageObject3 = messageObject4;
                    j2 = keyAt;
                    i3 = i7;
                    arrayList2 = arrayList3;
                    sendMessagesHelper.sendMessage(SendMessagesHelper.SendMessageParams.of(charSequence == null ? tL_forumTopic2 : charSequence.toString(), keyAt, messageObject4, messageObject4, null, true, entities, null, null, z, 0, null, false));
                }
                int sendMessage = SendMessagesHelper.getInstance(this.currentAccount).sendMessage(this.sendingMessageObjects, j2, !this.showSendersName, false, z, 0, messageObject3, i6);
                if (sendMessage != 0) {
                    arrayList = arrayList2;
                    arrayList.add(Long.valueOf(j2));
                } else {
                    arrayList = arrayList2;
                }
                if (this.selectedDialogs.size() == 1) {
                    tL_forumTopic2 = null;
                    AlertsCreator.showSendMediaAlert(sendMessage, this.parentFragment, null);
                    if (sendMessage != 0) {
                        break;
                    }
                } else {
                    tL_forumTopic2 = null;
                }
                i7 = i3 + 1;
                arrayList3 = arrayList;
                r2 = 0;
            }
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                long longValue = ((Long) it.next()).longValue();
                TLRPC.Dialog dialog = (TLRPC.Dialog) this.selectedDialogs.get(longValue);
                this.selectedDialogs.remove(longValue);
                if (dialog != null) {
                    this.selectedDialogTopics.remove(dialog);
                }
            }
            if (!this.selectedDialogs.isEmpty()) {
                LongSparseArray longSparseArray = this.selectedDialogs;
                int size = this.sendingMessageObjects.size();
                if (this.selectedDialogs.size() == 1) {
                    tL_forumTopic2 = (TLRPC.TL_forumTopic) this.selectedDialogTopics.get(this.selectedDialogs.valueAt(0));
                }
                onSend(longSparseArray, size, tL_forumTopic2);
            }
        } else {
            SwitchView switchView = this.switchView;
            int i8 = switchView != null ? switchView.currentTab : 0;
            if (this.storyItem != null) {
                int i9 = 0;
                while (i9 < this.selectedDialogs.size()) {
                    long keyAt2 = this.selectedDialogs.keyAt(i9);
                    TLRPC.TL_forumTopic tL_forumTopic4 = (TLRPC.TL_forumTopic) this.selectedDialogTopics.get(this.selectedDialogs.get(keyAt2));
                    if (tL_forumTopic4 != null) {
                        c2 = 0;
                        messageObject2 = new MessageObject(this.currentAccount, tL_forumTopic4.topicStartMessage, false, false);
                    } else {
                        c2 = 0;
                        messageObject2 = tL_forumTopic2;
                    }
                    if (this.storyItem != null) {
                        i2 = i9;
                        tL_forumTopic = tL_forumTopic2;
                        if (this.frameLayout2.getTag() != null && this.commentTextView.length() > 0 && charSequenceArr[0] != null) {
                            SendMessagesHelper.getInstance(this.currentAccount).sendMessage(SendMessagesHelper.SendMessageParams.of(charSequenceArr[0].toString(), keyAt2, null, messageObject2, null, true, null, null, null, z, 0, null, false));
                        }
                        of = SendMessagesHelper.SendMessageParams.of(null, keyAt2, messageObject2, messageObject2, null, true, null, null, null, z, 0, null, false);
                        of.sendingStory = this.storyItem;
                    } else if (this.frameLayout2.getTag() == null || this.commentTextView.length() <= 0) {
                        i2 = i9;
                        tL_forumTopic = tL_forumTopic2;
                        of = SendMessagesHelper.SendMessageParams.of(this.sendingText[i8], keyAt2, messageObject2, messageObject2, null, true, null, null, null, z, 0, null, false);
                    } else {
                        CharSequence charSequence2 = charSequenceArr[c2];
                        i2 = i9;
                        tL_forumTopic = tL_forumTopic2;
                        of = SendMessagesHelper.SendMessageParams.of(charSequence2 == null ? tL_forumTopic2 : charSequence2.toString(), keyAt2, messageObject2, messageObject2, null, true, entities, null, null, z, 0, null, false);
                    }
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(of);
                    i9 = i2 + 1;
                    tL_forumTopic2 = tL_forumTopic;
                }
            } else if (this.sendingText[i8] != null) {
                int i10 = 0;
                while (i10 < this.selectedDialogs.size()) {
                    long keyAt3 = this.selectedDialogs.keyAt(i10);
                    TLRPC.TL_forumTopic tL_forumTopic5 = (TLRPC.TL_forumTopic) this.selectedDialogTopics.get(this.selectedDialogs.get(keyAt3));
                    if (tL_forumTopic5 != null) {
                        c = 0;
                        messageObject = new MessageObject(this.currentAccount, tL_forumTopic5.topicStartMessage, false, false);
                    } else {
                        c = 0;
                        messageObject = null;
                    }
                    if (this.frameLayout2.getTag() == null || this.commentTextView.length() <= 0) {
                        j = keyAt3;
                        i = i10;
                    } else {
                        SendMessagesHelper sendMessagesHelper2 = SendMessagesHelper.getInstance(this.currentAccount);
                        CharSequence charSequence3 = charSequenceArr[c];
                        j = keyAt3;
                        i = i10;
                        sendMessagesHelper2.sendMessage(SendMessagesHelper.SendMessageParams.of(charSequence3 == null ? null : charSequence3.toString(), keyAt3, messageObject, messageObject, null, true, entities, null, null, z, 0, null, false));
                    }
                    SendMessagesHelper.getInstance(this.currentAccount).sendMessage(SendMessagesHelper.SendMessageParams.of(this.sendingText[i8], j, messageObject, messageObject, null, true, null, null, null, z, 0, null, false));
                    i4 = 1;
                    i10 = i + 1;
                }
            }
            LongSparseArray longSparseArray2 = this.selectedDialogs;
            onSend(longSparseArray2, i4, (TLRPC.TL_forumTopic) this.selectedDialogTopics.get(longSparseArray2.valueAt(0)));
        }
        ShareAlertDelegate shareAlertDelegate = this.delegate;
        if (shareAlertDelegate != null) {
            shareAlertDelegate.didShare();
        }
        lambda$new$0();
    }

    public void setDelegate(ShareAlertDelegate shareAlertDelegate) {
        this.delegate = shareAlertDelegate;
    }

    public void setStoryToShare(TL_stories.StoryItem storyItem) {
        this.storyItem = storyItem;
    }

    public void updateBottomOverlay() {
        BlurredFrameLayout blurredFrameLayout;
        AdjustPanLayoutHelper adjustPanLayoutHelper;
        if (this.frameLayout2 == null) {
            return;
        }
        EditTextEmoji editTextEmoji = this.commentTextView;
        float f = 0.0f;
        float f2 = 1.0f;
        if (editTextEmoji == null || !editTextEmoji.isPopupVisible()) {
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierFrameLayout;
            if (sizeNotifierFrameLayout == null || (adjustPanLayoutHelper = sizeNotifierFrameLayout.adjustPanLayoutHelper) == null || adjustPanLayoutHelper.animationInProgress()) {
                blurredFrameLayout = this.navigationBar;
                f2 = 1.0f - this.keyboardT;
            } else {
                this.keyboardT = this.keyboardSize2 > AndroidUtilities.dp(20.0f) ? 1.0f : 0.0f;
                blurredFrameLayout = this.navigationBar;
            }
            blurredFrameLayout.setAlpha(f2);
        } else {
            float emojiPaddingShown = this.commentTextView.getEmojiPaddingShown();
            this.keyboardT = emojiPaddingShown;
            this.navigationBar.setAlpha(1.0f - emojiPaddingShown);
        }
        this.navigationBar.setTranslationY(this.navigationBarHeight);
        BlurredFrameLayout blurredFrameLayout2 = this.timestampFrameLayout;
        if (blurredFrameLayout2 != null) {
            blurredFrameLayout2.setTranslationY(-0.0f);
            f = 0.0f + AndroidUtilities.dp(48.0f);
        }
        FrameLayout frameLayout = this.pickerBottom;
        if (frameLayout != null) {
            float f3 = -f;
            frameLayout.setTranslationY(f3);
            LinearLayout linearLayout = this.sharesCountLayout;
            if (linearLayout != null) {
                linearLayout.setTranslationY(f3);
            }
        }
        float f4 = -f;
        this.frameLayout2.setTranslationY(f4);
        this.writeButtonContainer.setTranslationY(f4);
        this.selectedCountView.setTranslationY(f4);
    }

    public void updateSelectedCount(int i) {
        if (this.selectedDialogs.size() == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            showCommentTextView(false);
            return;
        }
        this.selectedCountView.invalidate();
        if (showCommentTextView(true) || i == 0) {
            this.selectedCountView.setPivotX(0.0f);
            this.selectedCountView.setPivotY(0.0f);
            return;
        }
        this.selectedCountView.setPivotX(AndroidUtilities.dp(21.0f));
        this.selectedCountView.setPivotY(AndroidUtilities.dp(12.0f));
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(ObjectAnimator.ofFloat(this.selectedCountView, (Property<View, Float>) View.SCALE_X, i == 1 ? 1.1f : 0.9f, 1.0f), ObjectAnimator.ofFloat(this.selectedCountView, (Property<View, Float>) View.SCALE_Y, i == 1 ? 1.1f : 0.9f, 1.0f));
        animatorSet.setInterpolator(new OvershootInterpolator());
        animatorSet.setDuration(180L);
        animatorSet.start();
    }
}
