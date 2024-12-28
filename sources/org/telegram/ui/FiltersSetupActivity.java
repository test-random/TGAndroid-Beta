package org.telegram.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FolderBottomSheet;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.LoadingDrawable;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.ProgressButton;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.FilterCreateActivity;
import org.telegram.ui.FiltersSetupActivity;

public class FiltersSetupActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ListAdapter adapter;
    private int filtersStartPosition;
    private int folderTagsPosition;
    private boolean highlightTags;
    private boolean ignoreUpdates;
    private ItemTouchHelper itemTouchHelper;
    private RecyclerListView listView;
    private boolean loadedColors;
    private boolean loadingFiltersForColors;
    private boolean orderChanged;
    private UndoView undoView;
    private ArrayList oldItems = new ArrayList();
    private ArrayList items = new ArrayList();
    private int filtersSectionStart = -1;
    private int filtersSectionEnd = -1;
    private int shiftDp = -4;

    public class AnonymousClass2 extends RecyclerListView {
        AnonymousClass2(Context context) {
            super(context);
        }

        public void lambda$onTouchEvent$0() {
            FiltersSetupActivity.this.getMessagesController().lockFiltersInternal();
        }

        @Override
        public void dispatchDraw(Canvas canvas) {
            drawSectionBackground(canvas, FiltersSetupActivity.this.filtersSectionStart, FiltersSetupActivity.this.filtersSectionEnd, Theme.getColor(Theme.key_windowBackgroundWhite));
            super.dispatchDraw(canvas);
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        FiltersSetupActivity.AnonymousClass2.this.lambda$onTouchEvent$0();
                    }
                }, 250L);
            }
            return super.onTouchEvent(motionEvent);
        }
    }

    public class FilterCell extends FrameLayout {
        private final View colorImageView;
        private MessagesController.DialogFilter currentFilter;
        private int lastAppliedColor;
        private int lastColor;
        private final ImageView moveImageView;
        private ValueAnimator moveImageViewAnimator;
        private boolean needDivider;
        private final ImageView optionsImageView;
        float progressToLock;
        private final ImageView shareImageView;
        private boolean shareLoading;
        private final LoadingDrawable shareLoadingDrawable;
        private final SimpleTextView textView;
        private final TextView valueTextView;

        public FilterCell(Context context) {
            super(context);
            this.lastColor = -2;
            this.lastAppliedColor = -1;
            this.shareLoading = false;
            setWillNotDraw(false);
            ImageView imageView = new ImageView(context);
            this.moveImageView = imageView;
            imageView.setFocusable(false);
            ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
            imageView.setScaleType(scaleType);
            imageView.setImageResource(R.drawable.list_reorder);
            int i = Theme.key_stickers_menu;
            int color = Theme.getColor(i);
            PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
            imageView.setColorFilter(new PorterDuffColorFilter(color, mode));
            imageView.setContentDescription(LocaleController.getString(R.string.FilterReorder));
            imageView.setClickable(true);
            addView(imageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 16, 7.0f, 0.0f, 6.0f, 0.0f));
            View view = new View(context);
            this.colorImageView = view;
            addView(view, LayoutHelper.createFrame(20, 20.0f, (LocaleController.isRTL ? 5 : 3) | 16, 22.0f, 0.0f, 22.0f, 0.0f));
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.textView = simpleTextView;
            simpleTextView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f));
            simpleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            simpleTextView.setTextSize(16);
            simpleTextView.setMaxLines(1);
            simpleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
            Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.other_lockedfolders2);
            drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), mode));
            simpleTextView.setRightDrawable(drawable);
            simpleTextView.setEmojiColor(Theme.getColor(Theme.key_featuredStickers_addButton, ((BaseFragment) FiltersSetupActivity.this).resourceProvider));
            boolean z = LocaleController.isRTL;
            addView(simpleTextView, LayoutHelper.createFrame(-1, -2.0f, (z ? 5 : 3) | 48, z ? 80.0f : 64.0f, 10.0f, z ? 64.0f : 80.0f, 0.0f));
            TextView textView = new TextView(context);
            this.valueTextView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            textView.setTextSize(1, 13.0f);
            textView.setGravity(LocaleController.isRTL ? 5 : 3);
            textView.setLines(1);
            textView.setMaxLines(1);
            textView.setSingleLine(true);
            textView.setPadding(0, 0, 0, 0);
            textView.setEllipsize(TextUtils.TruncateAt.END);
            boolean z2 = LocaleController.isRTL;
            addView(textView, LayoutHelper.createFrame(-2, -2.0f, (z2 ? 5 : 3) | 48, z2 ? 80.0f : 64.0f, 35.0f, z2 ? 64.0f : 80.0f, 0.0f));
            textView.setVisibility(8);
            LoadingDrawable loadingDrawable = new LoadingDrawable();
            this.shareLoadingDrawable = loadingDrawable;
            loadingDrawable.setAppearByGradient(true);
            loadingDrawable.setGradientScale(2.0f);
            int i2 = Theme.key_listSelector;
            int color2 = Theme.getColor(i2);
            loadingDrawable.setColors(Theme.multAlpha(color2, 0.4f), Theme.multAlpha(color2, 1.0f), Theme.multAlpha(color2, 0.9f), Theme.multAlpha(color2, 1.7f));
            final int dp = AndroidUtilities.dp(1.0f);
            loadingDrawable.strokePaint.setStrokeWidth(dp);
            loadingDrawable.setRadiiDp(40.0f);
            ImageView imageView2 = new ImageView(context) {
                @Override
                protected void onDraw(Canvas canvas) {
                    super.onDraw(canvas);
                    if (FilterCell.this.shareLoading) {
                        LoadingDrawable loadingDrawable2 = FilterCell.this.shareLoadingDrawable;
                        int i3 = dp / 2;
                        loadingDrawable2.setBounds(i3, i3, getWidth() - (dp / 2), getHeight() - (dp / 2));
                        FilterCell.this.shareLoadingDrawable.draw(canvas);
                    }
                }

                @Override
                protected boolean verifyDrawable(Drawable drawable2) {
                    return drawable2 == FilterCell.this.shareLoadingDrawable || super.verifyDrawable(drawable2);
                }
            };
            this.shareImageView = imageView2;
            loadingDrawable.setCallback(imageView2);
            imageView2.setFocusable(false);
            imageView2.setScaleType(scaleType);
            imageView2.setBackground(Theme.createSelectorDrawable(color2));
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), mode));
            imageView2.setContentDescription(LocaleController.getString(R.string.FilterShare));
            imageView2.setVisibility(8);
            imageView2.setImageResource(R.drawable.msg_link_folder);
            imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), mode));
            boolean z3 = LocaleController.isRTL;
            addView(imageView2, LayoutHelper.createFrame(40, 40.0f, (z3 ? 3 : 5) | 16, z3 ? 52.0f : 6.0f, 0.0f, z3 ? 6.0f : 52.0f, 0.0f));
            imageView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    FiltersSetupActivity.FilterCell.this.lambda$new$1(view2);
                }
            });
            ImageView imageView3 = new ImageView(context);
            this.optionsImageView = imageView3;
            imageView3.setFocusable(false);
            imageView3.setScaleType(scaleType);
            imageView3.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(i2)));
            imageView3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i), mode));
            imageView3.setImageResource(R.drawable.msg_actions);
            imageView3.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
            addView(imageView3, LayoutHelper.createFrame(40, 40.0f, (LocaleController.isRTL ? 3 : 5) | 16, 6.0f, 0.0f, 6.0f, 0.0f));
        }

        public void lambda$new$0() {
            this.shareLoadingDrawable.disappear();
            this.shareImageView.invalidate();
            FiltersSetupActivity.this.updateRows(true);
        }

        public void lambda$new$1(View view) {
            if ((!this.shareLoading || this.shareLoadingDrawable.isDisappeared()) && this.currentFilter != null) {
                this.shareLoading = true;
                this.shareLoadingDrawable.reset();
                this.shareLoadingDrawable.resetDisappear();
                this.shareImageView.invalidate();
                FilterCreateActivity.FilterInvitesBottomSheet.show(FiltersSetupActivity.this, this.currentFilter, new Runnable() {
                    @Override
                    public final void run() {
                        FiltersSetupActivity.FilterCell.this.lambda$new$0();
                    }
                });
            }
        }

        public void lambda$setFilter$2(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.moveImageView.setAlpha(floatValue);
            float f = (floatValue * 0.5f) + 0.5f;
            this.moveImageView.setScaleX(f);
            this.moveImageView.setScaleY(f);
            float f2 = 1.0f - floatValue;
            this.colorImageView.setAlpha(f2);
            float f3 = (f2 * 0.5f) + 0.5f;
            this.colorImageView.setScaleX(f3);
            this.colorImageView.setScaleY(f3);
        }

        public MessagesController.DialogFilter getCurrentFilter() {
            return this.currentFilter;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(62.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(62.0f) : 0), getMeasuredHeight() - 1, Theme.dividerPaint);
            }
            MessagesController.DialogFilter dialogFilter = this.currentFilter;
            if (dialogFilter != null) {
                boolean z = dialogFilter.locked;
                if (z) {
                    float f = this.progressToLock;
                    if (f != 1.0f) {
                        this.progressToLock = f + 0.10666667f;
                        invalidate();
                    }
                }
                if (!z) {
                    float f2 = this.progressToLock;
                    if (f2 != 0.0f) {
                        this.progressToLock = f2 - 0.10666667f;
                        invalidate();
                    }
                }
            }
            float clamp = Utilities.clamp(this.progressToLock, 1.0f, 0.0f);
            this.progressToLock = clamp;
            this.textView.setRightDrawableScale(clamp);
            this.textView.invalidate();
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
        }

        public void setFilter(org.telegram.messenger.MessagesController.DialogFilter r16, boolean r17, int r18) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.FiltersSetupActivity.FilterCell.setFilter(org.telegram.messenger.MessagesController$DialogFilter, boolean, int):void");
        }

        public void setOnOptionsClick(View.OnClickListener onClickListener) {
            this.optionsImageView.setOnClickListener(onClickListener);
        }

        public void setOnReorderButtonTouchListener(View.OnTouchListener onTouchListener) {
            this.moveImageView.setOnTouchListener(onTouchListener);
        }
    }

    public static class HintInnerCell extends FrameLayout {
        private RLottieImageView imageView;
        private TextView messageTextView;

        public HintInnerCell(Context context, int i, CharSequence charSequence) {
            super(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setAnimation(i, 90, 90);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.playAnimation();
            this.imageView.setImportantForAccessibility(2);
            addView(this.imageView, LayoutHelper.createFrame(90, 90.0f, 49, 0.0f, 14.0f, 0.0f, 0.0f));
            this.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    FiltersSetupActivity.HintInnerCell.this.lambda$new$0(view);
                }
            });
            TextView textView = new TextView(context);
            this.messageTextView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText4));
            this.messageTextView.setTextSize(1, 14.0f);
            this.messageTextView.setGravity(17);
            this.messageTextView.setText(charSequence);
            addView(this.messageTextView, LayoutHelper.createFrame(-1, -2.0f, 49, 40.0f, 121.0f, 40.0f, 24.0f));
        }

        public void lambda$new$0(View view) {
            if (this.imageView.isPlaying()) {
                return;
            }
            this.imageView.setProgress(0.0f);
            this.imageView.playAnimation();
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), i2);
        }
    }

    public static class ItemInner extends AdapterWithDiffUtils.Item {
        MessagesController.DialogFilter filter;
        TLRPC.TL_dialogFilterSuggested suggested;
        CharSequence text;

        public ItemInner(int i) {
            super(i, false);
        }

        public static ItemInner asButton(CharSequence charSequence) {
            ItemInner itemInner = new ItemInner(4);
            itemInner.text = charSequence;
            return itemInner;
        }

        public static ItemInner asCheck(CharSequence charSequence) {
            ItemInner itemInner = new ItemInner(6);
            itemInner.text = charSequence;
            return itemInner;
        }

        public static ItemInner asFilter(MessagesController.DialogFilter dialogFilter) {
            ItemInner itemInner = new ItemInner(2);
            itemInner.filter = dialogFilter;
            return itemInner;
        }

        public static ItemInner asHeader(CharSequence charSequence) {
            ItemInner itemInner = new ItemInner(0);
            itemInner.text = charSequence;
            return itemInner;
        }

        public static ItemInner asHint() {
            return new ItemInner(1);
        }

        public static ItemInner asShadow(CharSequence charSequence) {
            ItemInner itemInner = new ItemInner(3);
            itemInner.text = charSequence;
            return itemInner;
        }

        public static ItemInner asSuggested(TLRPC.TL_dialogFilterSuggested tL_dialogFilterSuggested) {
            ItemInner itemInner = new ItemInner(5);
            itemInner.suggested = tL_dialogFilterSuggested;
            return itemInner;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof ItemInner)) {
                return false;
            }
            ItemInner itemInner = (ItemInner) obj;
            int i = itemInner.viewType;
            int i2 = this.viewType;
            if (i != i2) {
                return false;
            }
            if ((i2 == 0 || i2 == 4 || i2 == 3 || i2 == 6) && !TextUtils.equals(this.text, itemInner.text)) {
                return false;
            }
            int i3 = this.viewType;
            if (i3 == 2) {
                MessagesController.DialogFilter dialogFilter = this.filter;
                boolean z = dialogFilter == null;
                MessagesController.DialogFilter dialogFilter2 = itemInner.filter;
                if (z != (dialogFilter2 == null)) {
                    return false;
                }
                if (dialogFilter != null && dialogFilter.id != dialogFilter2.id) {
                    return false;
                }
            }
            if (i3 == 5) {
                TLRPC.TL_dialogFilterSuggested tL_dialogFilterSuggested = this.suggested;
                boolean z2 = tL_dialogFilterSuggested == null;
                TLRPC.TL_dialogFilterSuggested tL_dialogFilterSuggested2 = itemInner.suggested;
                if (z2 != (tL_dialogFilterSuggested2 == null)) {
                    return false;
                }
                if (tL_dialogFilterSuggested != null && tL_dialogFilterSuggested.filter.id != tL_dialogFilterSuggested2.filter.id) {
                    return false;
                }
            }
            return true;
        }
    }

    public class ListAdapter extends AdapterWithDiffUtils {
        private Context mContext;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean lambda$onCreateViewHolder$0(FilterCell filterCell, View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() != 0) {
                return false;
            }
            FiltersSetupActivity.this.itemTouchHelper.startDrag(FiltersSetupActivity.this.listView.getChildViewHolder(filterCell));
            return false;
        }

        public void lambda$onCreateViewHolder$1(MessagesController.DialogFilter dialogFilter) {
            if (!dialogFilter.locked) {
                FiltersSetupActivity.this.presentFragment(new FilterCreateActivity(dialogFilter));
                return;
            }
            FiltersSetupActivity filtersSetupActivity = FiltersSetupActivity.this;
            FiltersSetupActivity filtersSetupActivity2 = FiltersSetupActivity.this;
            filtersSetupActivity.showDialog(new LimitReachedBottomSheet(filtersSetupActivity2, this.mContext, 3, ((BaseFragment) filtersSetupActivity2).currentAccount, null));
        }

        public void lambda$onCreateViewHolder$2(Boolean bool) {
            FiltersSetupActivity.this.updateRows(true);
        }

        public void lambda$onCreateViewHolder$3(AlertDialog alertDialog, MessagesController.DialogFilter dialogFilter) {
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            FiltersSetupActivity.this.getMessagesController().removeFilter(dialogFilter);
            FiltersSetupActivity.this.getMessagesStorage().deleteDialogFilter(dialogFilter);
        }

        public void lambda$onCreateViewHolder$4(final AlertDialog alertDialog, final MessagesController.DialogFilter dialogFilter, TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    FiltersSetupActivity.ListAdapter.this.lambda$onCreateViewHolder$3(alertDialog, dialogFilter);
                }
            });
        }

        public void lambda$onCreateViewHolder$5(final MessagesController.DialogFilter dialogFilter, DialogInterface dialogInterface, int i) {
            final AlertDialog alertDialog;
            if (FiltersSetupActivity.this.getParentActivity() != null) {
                alertDialog = new AlertDialog(FiltersSetupActivity.this.getParentActivity(), 3);
                alertDialog.setCanCancel(false);
                alertDialog.show();
            } else {
                alertDialog = null;
            }
            TLRPC.TL_messages_updateDialogFilter tL_messages_updateDialogFilter = new TLRPC.TL_messages_updateDialogFilter();
            tL_messages_updateDialogFilter.id = dialogFilter.id;
            FiltersSetupActivity.this.getConnectionsManager().sendRequest(tL_messages_updateDialogFilter, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FiltersSetupActivity.ListAdapter.this.lambda$onCreateViewHolder$4(alertDialog, dialogFilter, tLObject, tL_error);
                }
            });
        }

        public void lambda$onCreateViewHolder$6(final MessagesController.DialogFilter dialogFilter) {
            if (dialogFilter.isChatlist()) {
                FolderBottomSheet.showForDeletion(FiltersSetupActivity.this, dialogFilter.id, new Utilities.Callback() {
                    @Override
                    public final void run(Object obj) {
                        FiltersSetupActivity.ListAdapter.this.lambda$onCreateViewHolder$2((Boolean) obj);
                    }
                });
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(FiltersSetupActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.FilterDelete));
            builder.setMessage(LocaleController.getString(R.string.FilterDeleteAlert));
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            builder.setPositiveButton(LocaleController.getString(R.string.Delete), new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    FiltersSetupActivity.ListAdapter.this.lambda$onCreateViewHolder$5(dialogFilter, dialogInterface, i);
                }
            });
            AlertDialog create = builder.create();
            FiltersSetupActivity.this.showDialog(create);
            TextView textView = (TextView) create.getButton(-1);
            if (textView != null) {
                textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
            }
        }

        public void lambda$onCreateViewHolder$7(View view) {
            FilterCell filterCell = (FilterCell) view.getParent();
            final MessagesController.DialogFilter currentFilter = filterCell.getCurrentFilter();
            ItemOptions makeOptions = ItemOptions.makeOptions(FiltersSetupActivity.this, filterCell);
            makeOptions.add(R.drawable.msg_edit, LocaleController.getString(R.string.FilterEditItem), new Runnable() {
                @Override
                public final void run() {
                    FiltersSetupActivity.ListAdapter.this.lambda$onCreateViewHolder$1(currentFilter);
                }
            });
            makeOptions.add(R.drawable.msg_delete, (CharSequence) LocaleController.getString(R.string.FilterDeleteItem), true, new Runnable() {
                @Override
                public final void run() {
                    FiltersSetupActivity.ListAdapter.this.lambda$onCreateViewHolder$6(currentFilter);
                }
            });
            if (LocaleController.isRTL) {
                makeOptions.setGravity(3);
            }
            makeOptions.show();
        }

        public void lambda$onCreateViewHolder$8(TLRPC.TL_dialogFilterSuggested tL_dialogFilterSuggested) {
            FiltersSetupActivity.this.getMessagesController().suggestedFilters.remove(tL_dialogFilterSuggested);
            FiltersSetupActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        }

        public void lambda$onCreateViewHolder$9(SuggestedFilterCell suggestedFilterCell, View view) {
            final TLRPC.TL_dialogFilterSuggested suggestedFilter = suggestedFilterCell.getSuggestedFilter();
            MessagesController.DialogFilter dialogFilter = new MessagesController.DialogFilter();
            TLRPC.TL_textWithEntities tL_textWithEntities = suggestedFilter.filter.title;
            dialogFilter.name = tL_textWithEntities.text;
            dialogFilter.entities = tL_textWithEntities.entities;
            dialogFilter.id = 2;
            while (FiltersSetupActivity.this.getMessagesController().dialogFiltersById.get(dialogFilter.id) != null) {
                dialogFilter.id++;
            }
            dialogFilter.order = FiltersSetupActivity.this.getMessagesController().getDialogFilters().size();
            dialogFilter.unreadCount = -1;
            dialogFilter.pendingUnreadCount = -1;
            int i = 0;
            while (i < 2) {
                TLRPC.DialogFilter dialogFilter2 = suggestedFilter.filter;
                ArrayList<TLRPC.InputPeer> arrayList = i == 0 ? dialogFilter2.include_peers : dialogFilter2.exclude_peers;
                ArrayList<Long> arrayList2 = i == 0 ? dialogFilter.alwaysShow : dialogFilter.neverShow;
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    TLRPC.InputPeer inputPeer = arrayList.get(i2);
                    long j = inputPeer.user_id;
                    if (j == 0) {
                        long j2 = inputPeer.chat_id;
                        j = j2 != 0 ? -j2 : -inputPeer.channel_id;
                    }
                    arrayList2.add(Long.valueOf(j));
                }
                i++;
            }
            TLRPC.DialogFilter dialogFilter3 = suggestedFilter.filter;
            if (dialogFilter3.groups) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_GROUPS;
            }
            if (dialogFilter3.bots) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_BOTS;
            }
            if (dialogFilter3.contacts) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
            }
            if (dialogFilter3.non_contacts) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
            }
            if (dialogFilter3.broadcasts) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
            }
            if (dialogFilter3.exclude_archived) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED;
            }
            if (dialogFilter3.exclude_read) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ;
            }
            if (dialogFilter3.exclude_muted) {
                dialogFilter.flags |= MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED;
            }
            FilterCreateActivity.saveFilterToServer(dialogFilter, dialogFilter.flags, dialogFilter.name, dialogFilter.entities, dialogFilter.title_noanimate, dialogFilter.color, dialogFilter.alwaysShow, dialogFilter.neverShow, dialogFilter.pinnedDialogs, true, true, true, true, true, FiltersSetupActivity.this, new Runnable() {
                @Override
                public final void run() {
                    FiltersSetupActivity.ListAdapter.this.lambda$onCreateViewHolder$8(suggestedFilter);
                }
            });
        }

        @Override
        public int getItemCount() {
            return FiltersSetupActivity.this.items.size();
        }

        @Override
        public int getItemViewType(int i) {
            ItemInner itemInner;
            if (i < 0 || i >= FiltersSetupActivity.this.items.size() || (itemInner = (ItemInner) FiltersSetupActivity.this.items.get(i)) == null) {
                return 3;
            }
            return itemInner.viewType;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return (itemViewType == 3 || itemViewType == 0 || itemViewType == 5 || itemViewType == 1) ? false : true;
        }

        public void moveElementToStart(int i) {
            ArrayList<MessagesController.DialogFilter> arrayList = FiltersSetupActivity.this.getMessagesController().dialogFilters;
            if (i < 0 || i >= arrayList.size()) {
                return;
            }
            arrayList.add(0, arrayList.remove(i));
            for (int i2 = 0; i2 <= i; i2++) {
                arrayList.get(i2).order = i2;
            }
            FiltersSetupActivity.this.orderChanged = true;
            FiltersSetupActivity.this.updateRows(true);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ItemInner itemInner = (ItemInner) FiltersSetupActivity.this.items.get(i);
            if (itemInner == null) {
                return;
            }
            int i2 = i + 1;
            boolean z = i2 < FiltersSetupActivity.this.items.size() && ((ItemInner) FiltersSetupActivity.this.items.get(i2)).viewType != 3;
            boolean z2 = i2 >= FiltersSetupActivity.this.items.size();
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 0) {
                ((HeaderCell) viewHolder.itemView).setText(itemInner.text);
                return;
            }
            if (itemViewType == 2) {
                ((FilterCell) viewHolder.itemView).setFilter(itemInner.filter, z, i);
                return;
            }
            if (itemViewType == 3) {
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (TextUtils.isEmpty(itemInner.text)) {
                    textInfoPrivacyCell.setText(null);
                    textInfoPrivacyCell.setFixedSize(12);
                } else {
                    textInfoPrivacyCell.setFixedSize(0);
                    textInfoPrivacyCell.setText(itemInner.text);
                }
                textInfoPrivacyCell.setBottomPadding(z2 ? 32 : 17);
                textInfoPrivacyCell.setBackground(Theme.getThemedDrawableByKey(this.mContext, z ? R.drawable.greydivider : R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                return;
            }
            if (itemViewType != 4) {
                if (itemViewType == 5) {
                    ((SuggestedFilterCell) viewHolder.itemView).setFilter(itemInner.suggested, z);
                    return;
                } else {
                    if (itemViewType != 6) {
                        return;
                    }
                    TextCheckCell textCheckCell = (TextCheckCell) viewHolder.itemView;
                    textCheckCell.setTextAndCheck(itemInner.text, FiltersSetupActivity.this.getMessagesController().folderTags, z);
                    textCheckCell.setCheckBoxIcon(FiltersSetupActivity.this.getUserConfig().isPremium() ? 0 : R.drawable.permission_locked);
                    return;
                }
            }
            TextCell textCell = (TextCell) viewHolder.itemView;
            Drawable drawable = this.mContext.getResources().getDrawable(R.drawable.poll_add_circle);
            Drawable drawable2 = this.mContext.getResources().getDrawable(R.drawable.poll_add_plus);
            int color = Theme.getColor(Theme.key_switchTrackChecked);
            PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
            drawable.setColorFilter(new PorterDuffColorFilter(color, mode));
            drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_checkboxCheck), mode));
            textCell.setTextAndIcon(((Object) itemInner.text) + "", new CombinedDrawable(drawable, drawable2), false);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            FrameLayout frameLayout2;
            if (i != 0) {
                if (i == 1) {
                    FrameLayout hintInnerCell = new HintInnerCell(this.mContext, R.raw.filters, AndroidUtilities.replaceTags(LocaleController.formatString("CreateNewFilterInfo", R.string.CreateNewFilterInfo, new Object[0])));
                    hintInnerCell.setBackgroundDrawable(Theme.getThemedDrawableByKey(this.mContext, R.drawable.greydivider_top, Theme.key_windowBackgroundGrayShadow));
                    frameLayout2 = hintInnerCell;
                } else if (i == 2) {
                    final FilterCell filterCell = new FilterCell(this.mContext);
                    filterCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    filterCell.setOnReorderButtonTouchListener(new View.OnTouchListener() {
                        @Override
                        public final boolean onTouch(View view, MotionEvent motionEvent) {
                            boolean lambda$onCreateViewHolder$0;
                            lambda$onCreateViewHolder$0 = FiltersSetupActivity.ListAdapter.this.lambda$onCreateViewHolder$0(filterCell, view, motionEvent);
                            return lambda$onCreateViewHolder$0;
                        }
                    });
                    filterCell.setOnOptionsClick(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            FiltersSetupActivity.ListAdapter.this.lambda$onCreateViewHolder$7(view);
                        }
                    });
                    frameLayout2 = filterCell;
                } else if (i == 3) {
                    frameLayout2 = new TextInfoPrivacyCell(this.mContext);
                } else if (i == 4) {
                    frameLayout = new TextCell(this.mContext);
                } else if (i != 6) {
                    final SuggestedFilterCell suggestedFilterCell = new SuggestedFilterCell(this.mContext);
                    suggestedFilterCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    suggestedFilterCell.setAddOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            FiltersSetupActivity.ListAdapter.this.lambda$onCreateViewHolder$9(suggestedFilterCell, view);
                        }
                    });
                    frameLayout2 = suggestedFilterCell;
                } else {
                    frameLayout = new TextCheckCell(this.mContext);
                }
                return new RecyclerListView.Holder(frameLayout2);
            }
            frameLayout = new HeaderCell(this.mContext);
            frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            frameLayout2 = frameLayout;
            return new RecyclerListView.Holder(frameLayout2);
        }

        public void swapElements(int i, int i2) {
            MessagesController.DialogFilter dialogFilter;
            MessagesController.DialogFilter dialogFilter2;
            if (i < FiltersSetupActivity.this.filtersStartPosition || i2 < FiltersSetupActivity.this.filtersStartPosition) {
                return;
            }
            ItemInner itemInner = (ItemInner) FiltersSetupActivity.this.items.get(i);
            ItemInner itemInner2 = (ItemInner) FiltersSetupActivity.this.items.get(i2);
            if (itemInner == null || itemInner2 == null || (dialogFilter = itemInner.filter) == null || (dialogFilter2 = itemInner2.filter) == null) {
                return;
            }
            int i3 = dialogFilter.order;
            dialogFilter.order = dialogFilter2.order;
            dialogFilter2.order = i3;
            ArrayList<MessagesController.DialogFilter> arrayList = FiltersSetupActivity.this.getMessagesController().dialogFilters;
            try {
                arrayList.set(i - FiltersSetupActivity.this.filtersStartPosition, itemInner2.filter);
                arrayList.set(i2 - FiltersSetupActivity.this.filtersStartPosition, itemInner.filter);
            } catch (Exception unused) {
            }
            FiltersSetupActivity.this.orderChanged = true;
            FiltersSetupActivity.this.updateRows(true);
        }
    }

    public static class SuggestedFilterCell extends FrameLayout {
        private ProgressButton addButton;
        private boolean needDivider;
        private TLRPC.TL_dialogFilterSuggested suggestedFilter;
        private TextView textView;
        private TextView valueTextView;

        public SuggestedFilterCell(Context context) {
            super(context);
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setMaxLines(1);
            this.textView.setSingleLine(true);
            TextView textView2 = this.textView;
            TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
            textView2.setEllipsize(truncateAt);
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, 22.0f, 10.0f, 22.0f, 0.0f));
            TextView textView3 = new TextView(context);
            this.valueTextView = textView3;
            textView3.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.valueTextView.setTextSize(1, 13.0f);
            this.valueTextView.setLines(1);
            this.valueTextView.setMaxLines(1);
            this.valueTextView.setSingleLine(true);
            this.valueTextView.setEllipsize(truncateAt);
            this.valueTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            addView(this.valueTextView, LayoutHelper.createFrame(-2, -2.0f, LocaleController.isRTL ? 5 : 3, 22.0f, 35.0f, 22.0f, 0.0f));
            ProgressButton progressButton = new ProgressButton(context);
            this.addButton = progressButton;
            progressButton.setText(LocaleController.getString(R.string.Add));
            this.addButton.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            this.addButton.setProgressColor(Theme.getColor(Theme.key_featuredStickers_buttonProgress));
            this.addButton.setBackgroundRoundRect(Theme.getColor(Theme.key_featuredStickers_addButton), Theme.getColor(Theme.key_featuredStickers_addButtonPressed));
            addView(this.addButton, LayoutHelper.createFrameRelatively(-2.0f, 28.0f, 8388661, 0.0f, 18.0f, 14.0f, 0.0f));
        }

        public TLRPC.TL_dialogFilterSuggested getSuggestedFilter() {
            return this.suggestedFilter;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (this.needDivider) {
                canvas.drawLine(0.0f, getHeight() - 1, getWidth() - getPaddingRight(), getHeight() - 1, Theme.dividerPaint);
            }
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setEnabled(true);
            accessibilityNodeInfo.setText(this.addButton.getText());
            accessibilityNodeInfo.setClassName("android.widget.Button");
        }

        @Override
        protected void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(64.0f));
            measureChildWithMargins(this.addButton, i, 0, i2, 0);
            measureChildWithMargins(this.textView, i, this.addButton.getMeasuredWidth(), i2, 0);
            measureChildWithMargins(this.valueTextView, i, this.addButton.getMeasuredWidth(), i2, 0);
        }

        public void setAddOnClickListener(View.OnClickListener onClickListener) {
            this.addButton.setOnClickListener(onClickListener);
        }

        public void setFilter(TLRPC.TL_dialogFilterSuggested tL_dialogFilterSuggested, boolean z) {
            this.needDivider = z;
            this.suggestedFilter = tL_dialogFilterSuggested;
            setWillNotDraw(!z);
            this.textView.setText(tL_dialogFilterSuggested.filter.title.text);
            this.valueTextView.setText(tL_dialogFilterSuggested.description);
        }
    }

    public static class TextCell extends FrameLayout {
        private ImageView imageView;
        private SimpleTextView textView;

        public TextCell(Context context) {
            super(context);
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.textView = simpleTextView;
            simpleTextView.setTextSize(16);
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            SimpleTextView simpleTextView2 = this.textView;
            int i = Theme.key_windowBackgroundWhiteBlueText2;
            simpleTextView2.setTextColor(Theme.getColor(i));
            this.textView.setTag(Integer.valueOf(i));
            addView(this.textView);
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView);
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int dp;
            int i5 = i3 - i;
            int textHeight = ((i4 - i2) - this.textView.getTextHeight()) / 2;
            if (LocaleController.isRTL) {
                dp = (getMeasuredWidth() - this.textView.getMeasuredWidth()) - AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? 64.0f : 23.0f);
            } else {
                dp = AndroidUtilities.dp(this.imageView.getVisibility() == 0 ? 64.0f : 23.0f);
            }
            SimpleTextView simpleTextView = this.textView;
            simpleTextView.layout(dp, textHeight, simpleTextView.getMeasuredWidth() + dp, this.textView.getMeasuredHeight() + textHeight);
            int dp2 = !LocaleController.isRTL ? AndroidUtilities.dp(20.0f) : (i5 - this.imageView.getMeasuredWidth()) - AndroidUtilities.dp(20.0f);
            ImageView imageView = this.imageView;
            imageView.layout(dp2, 0, imageView.getMeasuredWidth() + dp2, this.imageView.getMeasuredHeight());
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            AndroidUtilities.dp(48.0f);
            this.textView.measure(View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(94.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), 1073741824));
            this.imageView.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
            setMeasuredDimension(size, AndroidUtilities.dp(50.0f));
        }

        public void setTextAndIcon(String str, Drawable drawable, boolean z) {
            this.textView.setText(str);
            this.imageView.setImageDrawable(drawable);
        }
    }

    public class TouchHelperCallback extends ItemTouchHelper.Callback {
        public TouchHelperCallback() {
        }

        public void resetDefaultPosition() {
            if (UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
                return;
            }
            ArrayList<MessagesController.DialogFilter> dialogFilters = FiltersSetupActivity.this.getMessagesController().getDialogFilters();
            for (int i = 0; i < dialogFilters.size(); i++) {
                if (dialogFilters.get(i).isDefault() && i != 0) {
                    FiltersSetupActivity.this.adapter.moveElementToStart(i);
                    FiltersSetupActivity.this.listView.scrollToPosition(0);
                    FiltersSetupActivity.this.onDefaultTabMoved();
                    return;
                }
            }
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            viewHolder.itemView.setPressed(false);
        }

        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() != 2 ? ItemTouchHelper.Callback.makeMovementFlags(0, 0) : ItemTouchHelper.Callback.makeMovementFlags(3, 0);
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float f, float f2, int i, boolean z) {
            super.onChildDraw(canvas, recyclerView, viewHolder, f, f2, i, z);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder2) {
            if (viewHolder.getItemViewType() != viewHolder2.getItemViewType()) {
                return false;
            }
            FiltersSetupActivity.this.adapter.swapElements(viewHolder.getAdapterPosition(), viewHolder2.getAdapterPosition());
            return true;
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int i) {
            if (i != 0) {
                FiltersSetupActivity.this.listView.cancelClickRunnables(false);
                viewHolder.itemView.setPressed(true);
            } else {
                AndroidUtilities.cancelRunOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        FiltersSetupActivity.TouchHelperCallback.this.resetDefaultPosition();
                    }
                });
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        FiltersSetupActivity.TouchHelperCallback.this.resetDefaultPosition();
                    }
                }, 320L);
            }
            super.onSelectedChanged(viewHolder, i);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int i) {
        }
    }

    public void lambda$createView$2(TLRPC.TL_messages_toggleDialogFilterTags tL_messages_toggleDialogFilterTags) {
        if (!tL_messages_toggleDialogFilterTags.enabled || this.loadedColors) {
            return;
        }
        this.loadingFiltersForColors = true;
        getMessagesController().loadRemoteFilters(true);
        this.loadedColors = true;
    }

    public void lambda$createView$3(final TLRPC.TL_messages_toggleDialogFilterTags tL_messages_toggleDialogFilterTags, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FiltersSetupActivity.this.lambda$createView$2(tL_messages_toggleDialogFilterTags);
            }
        });
    }

    public void lambda$createView$4(Context context, View view, int i, float f, float f2) {
        ItemInner itemInner;
        FilterCreateActivity filterCreateActivity;
        LimitReachedBottomSheet limitReachedBottomSheet;
        if (i < 0 || i >= this.items.size() || (itemInner = (ItemInner) this.items.get(i)) == null) {
            return;
        }
        int i2 = itemInner.viewType;
        if (i2 == 6) {
            if (!getUserConfig().isPremium()) {
                showDialog(new PremiumFeatureBottomSheet(this, 35, true));
                return;
            }
            final TLRPC.TL_messages_toggleDialogFilterTags tL_messages_toggleDialogFilterTags = new TLRPC.TL_messages_toggleDialogFilterTags();
            tL_messages_toggleDialogFilterTags.enabled = true ^ getMessagesController().folderTags;
            getMessagesController().setFolderTags(tL_messages_toggleDialogFilterTags.enabled);
            getConnectionsManager().sendRequest(tL_messages_toggleDialogFilterTags, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FiltersSetupActivity.this.lambda$createView$3(tL_messages_toggleDialogFilterTags, tLObject, tL_error);
                }
            });
            ((TextCheckCell) view).setChecked(getMessagesController().folderTags);
            ListAdapter listAdapter = this.adapter;
            int i3 = this.filtersSectionStart;
            listAdapter.notifyItemRangeChanged(i3, this.filtersSectionEnd - i3);
            return;
        }
        if (i2 == 2) {
            MessagesController.DialogFilter dialogFilter = itemInner.filter;
            if (dialogFilter == null || dialogFilter.isDefault()) {
                return;
            }
            if (dialogFilter.locked) {
                limitReachedBottomSheet = new LimitReachedBottomSheet(this, context, 3, this.currentAccount, null);
                showDialog(limitReachedBottomSheet);
            } else {
                filterCreateActivity = new FilterCreateActivity(dialogFilter);
                presentFragment(filterCreateActivity);
            }
        }
        if (i2 == 4) {
            int size = getMessagesController().getDialogFilters().size();
            if ((size - 1 < getMessagesController().dialogFiltersLimitDefault || getUserConfig().isPremium()) && size < getMessagesController().dialogFiltersLimitPremium) {
                filterCreateActivity = new FilterCreateActivity();
                presentFragment(filterCreateActivity);
            } else {
                limitReachedBottomSheet = new LimitReachedBottomSheet(this, context, 3, this.currentAccount, null);
                showDialog(limitReachedBottomSheet);
            }
        }
    }

    public int lambda$createView$5() {
        return this.folderTagsPosition;
    }

    public void lambda$createView$6() {
        this.listView.highlightRow(new RecyclerListView.IntReturnCallback() {
            @Override
            public final int run() {
                int lambda$createView$5;
                lambda$createView$5 = FiltersSetupActivity.this.lambda$createView$5();
                return lambda$createView$5;
            }
        });
    }

    public void lambda$onDefaultTabMoved$7() {
        showDialog(new PremiumFeatureBottomSheet(this, 9, true));
    }

    public static void lambda$onFragmentDestroy$1(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$updateRows$0() {
        presentFragment(new PremiumPreviewFragment("settings"));
    }

    public void updateRows(boolean z) {
        this.oldItems.clear();
        this.oldItems.addAll(this.items);
        this.items.clear();
        ArrayList<TLRPC.TL_dialogFilterSuggested> arrayList = getMessagesController().suggestedFilters;
        ArrayList<MessagesController.DialogFilter> dialogFilters = getMessagesController().getDialogFilters();
        this.items.add(ItemInner.asHint());
        if (!arrayList.isEmpty() && dialogFilters.size() < 10) {
            this.items.add(ItemInner.asHeader(LocaleController.getString(R.string.FilterRecommended)));
            for (int i = 0; i < arrayList.size(); i++) {
                this.items.add(ItemInner.asSuggested(arrayList.get(i)));
            }
            this.items.add(ItemInner.asShadow(null));
        }
        if (dialogFilters.isEmpty()) {
            this.filtersSectionEnd = -1;
            this.filtersSectionStart = -1;
        } else {
            this.filtersSectionStart = this.items.size();
            this.items.add(ItemInner.asHeader(LocaleController.getString(R.string.Filters)));
            this.filtersStartPosition = this.items.size();
            for (int i2 = 0; i2 < dialogFilters.size(); i2++) {
                this.items.add(ItemInner.asFilter(dialogFilters.get(i2)));
                if (MessagesController.getInstance(this.currentAccount).folderTags && dialogFilters.get(i2).color >= 0) {
                    this.loadedColors = true;
                }
            }
            this.filtersSectionEnd = this.items.size();
        }
        if (dialogFilters.size() < getMessagesController().dialogFiltersLimitPremium) {
            this.items.add(ItemInner.asButton(LocaleController.getString(R.string.CreateNewFilter)));
        }
        this.items.add(ItemInner.asShadow(null));
        this.folderTagsPosition = this.items.size();
        this.items.add(ItemInner.asCheck(LocaleController.getString(R.string.FolderShowTags)));
        this.items.add(ItemInner.asShadow(!getUserConfig().isPremium() ? AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.FolderShowTagsInfoPremium), Theme.key_windowBackgroundWhiteBlueHeader, 2, new Runnable() {
            @Override
            public final void run() {
                FiltersSetupActivity.this.lambda$updateRows$0();
            }
        }) : LocaleController.getString(R.string.FolderShowTagsInfo)));
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            if (z) {
                listAdapter.setItems(this.oldItems, this.items);
            } else {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public View createView(final Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.Filters));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    FiltersSetupActivity.this.lambda$onBackPressed$321();
                }
            }
        });
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listView = new AnonymousClass2(context);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setDurations(350L);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        this.listView.setItemAnimator(defaultItemAnimator);
        ((DefaultItemAnimator) this.listView.getItemAnimator()).setDelayAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelperCallback());
        this.itemTouchHelper = itemTouchHelper;
        itemTouchHelper.attachToRecyclerView(this.listView);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() {
            @Override
            public boolean hasDoubleTap(View view, int i) {
                return RecyclerListView.OnItemClickListenerExtended.CC.$default$hasDoubleTap(this, view, i);
            }

            @Override
            public void onDoubleTap(View view, int i, float f, float f2) {
                RecyclerListView.OnItemClickListenerExtended.CC.$default$onDoubleTap(this, view, i, f, f2);
            }

            @Override
            public final void onItemClick(View view, int i, float f, float f2) {
                FiltersSetupActivity.this.lambda$createView$4(context, view, i, f, f2);
            }
        });
        if (this.highlightTags) {
            updateRows(false);
            this.highlightTags = false;
            this.listView.scrollToPosition(this.adapter.getItemCount() - 1);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    FiltersSetupActivity.this.lambda$createView$6();
                }
            }, 200L);
        }
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.dialogFiltersUpdated) {
            if (this.ignoreUpdates) {
                return;
            }
        } else if (i != NotificationCenter.suggestedFiltersLoaded) {
            return;
        }
        updateRows(true);
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, FilterCell.class, SuggestedFilterCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_windowBackgroundGray));
        ActionBar actionBar = this.actionBar;
        int i = ThemeDescription.FLAG_BACKGROUND;
        int i2 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{HeaderCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueHeader));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{FilterCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{FilterCell.class}, new String[]{"valueTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText2));
        int i3 = Theme.key_stickers_menu;
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{FilterCell.class}, new String[]{"moveImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{FilterCell.class}, new String[]{"optionsImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{FilterCell.class}, new String[]{"optionsImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_stickers_menuSelector));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, Theme.key_windowBackgroundGrayShadow));
        return arrayList;
    }

    public UndoView getUndoView() {
        if (getContext() == null) {
            return null;
        }
        if (this.undoView == null) {
            FrameLayout frameLayout = (FrameLayout) this.fragmentView;
            UndoView undoView = new UndoView(getContext());
            this.undoView = undoView;
            frameLayout.addView(undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        }
        return this.undoView;
    }

    public FiltersSetupActivity highlightTags() {
        this.highlightTags = true;
        return this;
    }

    protected void onDefaultTabMoved() {
        try {
            this.fragmentView.performHapticFeedback(3, 1);
        } catch (Exception unused) {
        }
        BulletinFactory.of(this).createSimpleBulletin(R.raw.filter_reorder, AndroidUtilities.replaceTags(LocaleController.formatString("LimitReachedReorderFolder", R.string.LimitReachedReorderFolder, LocaleController.getString(R.string.FilterAllChats))), LocaleController.getString(R.string.PremiumMore), 5000, new Runnable() {
            @Override
            public final void run() {
                FiltersSetupActivity.this.lambda$onDefaultTabMoved$7();
            }
        }).show();
    }

    @Override
    public boolean onFragmentCreate() {
        updateRows(false);
        getMessagesController().loadRemoteFilters(true);
        getNotificationCenter().addObserver(this, NotificationCenter.dialogFiltersUpdated);
        getNotificationCenter().addObserver(this, NotificationCenter.suggestedFiltersLoaded);
        if (getMessagesController().suggestedFilters.isEmpty()) {
            getMessagesController().loadSuggestedFilters();
        }
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.dialogFiltersUpdated;
        notificationCenter.removeObserver(this, i);
        getNotificationCenter().removeObserver(this, NotificationCenter.suggestedFiltersLoaded);
        if (this.orderChanged) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i, new Object[0]);
            getMessagesStorage().saveDialogFiltersOrder();
            TLRPC.TL_messages_updateDialogFiltersOrder tL_messages_updateDialogFiltersOrder = new TLRPC.TL_messages_updateDialogFiltersOrder();
            ArrayList<MessagesController.DialogFilter> dialogFilters = getMessagesController().getDialogFilters();
            int size = dialogFilters.size();
            for (int i2 = 0; i2 < size; i2++) {
                tL_messages_updateDialogFiltersOrder.order.add(Integer.valueOf(dialogFilters.get(i2).id));
            }
            getConnectionsManager().sendRequest(tL_messages_updateDialogFiltersOrder, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FiltersSetupActivity.lambda$onFragmentDestroy$1(tLObject, tL_error);
                }
            });
        }
        super.onFragmentDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }
}
