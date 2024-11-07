package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.text.style.ReplacementSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_chatlists;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.PollEditTextCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FolderBottomSheet;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.QRCodeBottomSheet;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.FilterCreateActivity;
import org.telegram.ui.PeerColorActivity;
import org.telegram.ui.UsersSelectActivity;

public class FilterCreateActivity extends BaseFragment {
    private ListAdapter adapter;
    private CreateLinkCell createLinkCell;
    private boolean creatingNew;
    private boolean doNotCloseWhenSave;
    private ActionBarMenuItem doneItem;
    private boolean excludeExpanded;
    private MessagesController.DialogFilter filter;
    private HeaderCellColorPreview folderTagsHeader;
    private boolean hasUserChanged;
    private boolean includeExpanded;
    private ArrayList invites;
    private ArrayList items;
    private RecyclerListView listView;
    private boolean loadingInvites;
    private boolean nameChangedManually;
    private int nameRow;
    private ArrayList newAlwaysShow;
    private int newFilterColor;
    private int newFilterFlags;
    private String newFilterName;
    private ArrayList newNeverShow;
    private LongSparseIntArray newPinned;
    private ArrayList oldItems;
    private int requestingInvitesReqId;
    private HintView saveHintView;
    float shiftDp;
    private Runnable showBulletinOnResume;
    private boolean showedUpdateBulletin;

    class AnonymousClass1 extends ActionBar.ActionBarMenuOnItemClick {
        AnonymousClass1() {
        }

        @Override
        public void onItemClick(int i) {
            if (i == -1) {
                if (FilterCreateActivity.this.checkDiscard()) {
                    FilterCreateActivity.this.lambda$onBackPressed$321();
                }
            } else if (i == 1) {
                FilterCreateActivity.this.processDone();
            }
        }
    }

    class AnonymousClass2 extends RecyclerListView {
        AnonymousClass2(Context context) {
            super(context);
        }

        @Override
        public Integer getSelectorColor(int i) {
            ItemInner itemInner = (i < 0 || i >= FilterCreateActivity.this.items.size()) ? null : (ItemInner) FilterCreateActivity.this.items.get(i);
            return Integer.valueOf((itemInner == null || !itemInner.isRed) ? getThemedColor(Theme.key_listSelector) : Theme.multAlpha(getThemedColor(Theme.key_text_RedRegular), 0.12f));
        }

        @Override
        public boolean requestFocus(int i, Rect rect) {
            return false;
        }
    }

    public class AnonymousClass3 extends HintView {
        AnonymousClass3(Context context, int i, boolean z) {
            super(context, i, z);
        }

        @Override
        public void setVisibility(int i) {
            super.setVisibility(i);
            if (i != 0) {
                try {
                    ((ViewGroup) getParent()).removeView(this);
                } catch (Exception unused) {
                }
            }
        }
    }

    private static class ButtonCell extends FrameLayout {
        private boolean divider;
        private ImageView imageView;
        private int lastIconResId;
        private TextView textView;
        private Boolean translateText;

        public ButtonCell(Context context) {
            super(context);
            this.divider = true;
            this.translateText = null;
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 16, 24.0f, 0.0f, 24.0f, 0.0f));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextSize(1, 16.0f);
            this.textView.setLines(1);
            this.textView.setSingleLine();
            TextView textView2 = this.textView;
            boolean z = LocaleController.isRTL;
            textView2.setPadding(z ? 24 : 0, 0, z ? 0 : 24, 0);
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            TextView textView3 = this.textView;
            boolean z2 = LocaleController.isRTL;
            addView(textView3, LayoutHelper.createFrame(-1, -2.0f, 23, z2 ? 0.0f : 72.0f, 0.0f, z2 ? 72.0f : 0.0f, 0.0f));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.divider) {
                canvas.drawRect(this.textView.getLeft(), getMeasuredHeight() - 1, this.textView.getRight(), getMeasuredHeight(), Theme.dividerPaint);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        }

        public void set(int i, CharSequence charSequence, boolean z) {
            int i2 = LocaleController.isRTL ? -1 : 1;
            boolean z2 = false;
            ImageView imageView = this.imageView;
            if (i == 0) {
                imageView.setVisibility(8);
            } else {
                imageView.setVisibility(0);
                this.imageView.setImageResource(i);
            }
            if (LocaleController.isRTL) {
                ((ViewGroup.MarginLayoutParams) this.textView.getLayoutParams()).rightMargin = AndroidUtilities.dp(i == 0 ? 24.0f : 72.0f);
            } else {
                ((ViewGroup.MarginLayoutParams) this.textView.getLayoutParams()).leftMargin = AndroidUtilities.dp(i == 0 ? 24.0f : 72.0f);
            }
            this.textView.setText(charSequence);
            if (!z && i != 0) {
                z2 = true;
            }
            Boolean bool = this.translateText;
            if (bool == null || bool.booleanValue() != z2) {
                this.translateText = Boolean.valueOf(z2);
                if (this.lastIconResId == i) {
                    this.textView.clearAnimation();
                    this.textView.animate().translationX(z2 ? AndroidUtilities.dp(i2 * (-7)) : 0.0f).setDuration(180L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
                } else {
                    this.textView.setTranslationX(z2 ? AndroidUtilities.dp(i2 * (-7)) : 0.0f);
                }
            }
            this.divider = z;
            setWillNotDraw(!z);
            this.lastIconResId = i;
        }

        public void setRed(boolean z) {
            this.imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(z ? Theme.key_text_RedBold : Theme.key_windowBackgroundWhiteBlueText2), PorterDuff.Mode.MULTIPLY));
            this.textView.setTextColor(Theme.getColor(z ? Theme.key_text_RedRegular : Theme.key_windowBackgroundWhiteBlueText4));
        }
    }

    public static class ColorImageSpan extends ImageSpan {
        int lastColor;

        public ColorImageSpan(Drawable drawable, int i) {
            super(drawable, i);
        }

        @Override
        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            if (paint.getColor() != this.lastColor && getDrawable() != null) {
                Drawable drawable = getDrawable();
                int color = paint.getColor();
                this.lastColor = color;
                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            }
            super.draw(canvas, charSequence, i, i2, f, i3, i4, i5, paint);
        }
    }

    private static class CreateLinkCell extends FrameLayout {
        ImageView imageView;
        boolean needDivider;
        TextView textView;

        public CreateLinkCell(Context context) {
            super(context);
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            this.textView.setTextSize(1, 16.0f);
            this.textView.setText(LocaleController.getString(R.string.CreateNewLink));
            this.textView.setGravity(LocaleController.isRTL ? 5 : 3);
            TextView textView2 = this.textView;
            boolean z = LocaleController.isRTL;
            textView2.setPadding(z ? 16 : 0, 0, z ? 0 : 16, 0);
            TextView textView3 = this.textView;
            boolean z2 = LocaleController.isRTL;
            addView(textView3, LayoutHelper.createFrame(-1, -2.0f, 23, z2 ? 0.0f : 64.0f, 0.0f, z2 ? 64.0f : 0.0f, 0.0f));
            this.imageView = new ImageView(context);
            Drawable drawable = context.getResources().getDrawable(R.drawable.poll_add_circle);
            Drawable drawable2 = context.getResources().getDrawable(R.drawable.poll_add_plus);
            int color = Theme.getColor(Theme.key_featuredStickers_addButton);
            PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
            drawable.setColorFilter(new PorterDuffColorFilter(color, mode));
            drawable2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_checkboxCheck), mode));
            this.imageView.setImageDrawable(new CombinedDrawable(drawable, drawable2));
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            ImageView imageView = this.imageView;
            boolean z3 = LocaleController.isRTL;
            addView(imageView, LayoutHelper.createFrame(32, 32.0f, (z3 ? 5 : 3) | 16, z3 ? 0.0f : 16.0f, 0.0f, z3 ? 16.0f : 0.0f, 0.0f));
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (this.needDivider) {
                canvas.drawRect(this.textView.getLeft(), getMeasuredHeight() - 1, this.textView.getRight(), getMeasuredHeight(), Theme.dividerPaint);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(45.0f), 1073741824));
        }

        public void setDivider(boolean z) {
            if (this.needDivider != z) {
                this.needDivider = z;
                setWillNotDraw(!z);
            }
        }

        @Override
        public void setEnabled(boolean z) {
            super.setEnabled(z);
            this.textView.setAlpha(z ? 1.0f : 0.5f);
            this.imageView.setAlpha(z ? 1.0f : 0.5f);
        }

        public void setText(String str) {
            this.textView.setText(str);
        }
    }

    public static class FilterInvitesBottomSheet extends BottomSheetWithRecyclerListView {
        private AdapterWithDiffUtils adapter;
        private FrameLayout bulletinContainer;
        private TextView button;
        private MessagesController.DialogFilter filter;
        private ArrayList invites;
        private ArrayList items;
        private ArrayList oldItems;

        public class AnonymousClass1 extends AdapterWithDiffUtils {

            public class C00421 extends LinkCell {
                C00421(Context context, BaseFragment baseFragment, int i, int i2) {
                    super(context, baseFragment, i, i2);
                }

                public void copy() {
                    String str = this.lastUrl;
                    if (str != null && AndroidUtilities.addToClipboard(str)) {
                        BulletinFactory.of(FilterInvitesBottomSheet.this.bulletinContainer, null).createCopyLinkBulletin().show();
                    }
                }

                @Override
                protected void onDelete(TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite) {
                    FilterInvitesBottomSheet.this.invites.remove(tL_exportedChatlistInvite);
                    FilterInvitesBottomSheet.this.updateCreateInviteButton();
                    FilterInvitesBottomSheet.this.updateRows(true);
                }

                @Override
                public void options() {
                    ItemOptions makeOptions = ItemOptions.makeOptions(FilterInvitesBottomSheet.this.container, this);
                    makeOptions.add(R.drawable.msg_copy, LocaleController.getString(R.string.CopyLink), new Runnable() {
                        @Override
                        public final void run() {
                            FilterCreateActivity.FilterInvitesBottomSheet.AnonymousClass1.C00421.this.copy();
                        }
                    });
                    makeOptions.add(R.drawable.msg_qrcode, LocaleController.getString(R.string.GetQRCode), new Runnable() {
                        @Override
                        public final void run() {
                            FilterCreateActivity.FilterInvitesBottomSheet.AnonymousClass1.C00421.this.qrcode();
                        }
                    });
                    makeOptions.add(R.drawable.msg_delete, (CharSequence) LocaleController.getString(R.string.DeleteLink), true, new Runnable() {
                        @Override
                        public final void run() {
                            FilterCreateActivity.FilterInvitesBottomSheet.AnonymousClass1.C00421.this.deleteLink();
                        }
                    });
                    if (LocaleController.isRTL) {
                        makeOptions.setGravity(3);
                    }
                    makeOptions.show();
                }
            }

            AnonymousClass1() {
            }

            private RecyclerView.Adapter realAdapter() {
                return ((BottomSheetWithRecyclerListView) FilterInvitesBottomSheet.this).recyclerListView.getAdapter();
            }

            @Override
            public int getItemCount() {
                return FilterInvitesBottomSheet.this.items.size();
            }

            @Override
            public int getItemViewType(int i) {
                return ((ItemInner) FilterInvitesBottomSheet.this.items.get(i)).viewType;
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                int itemViewType = viewHolder.getItemViewType();
                return itemViewType == 8 || itemViewType == 7;
            }

            @Override
            public void notifyDataSetChanged() {
                realAdapter().notifyDataSetChanged();
            }

            @Override
            public void notifyItemChanged(int i) {
                realAdapter().notifyItemChanged(i + 1);
            }

            @Override
            public void notifyItemMoved(int i, int i2) {
                realAdapter().notifyItemMoved(i + 1, i2);
            }

            @Override
            public void notifyItemRangeChanged(int i, int i2) {
                realAdapter().notifyItemRangeChanged(i + 1, i2);
            }

            @Override
            public void notifyItemRangeChanged(int i, int i2, Object obj) {
                realAdapter().notifyItemRangeChanged(i + 1, i2, obj);
            }

            @Override
            public void notifyItemRangeInserted(int i, int i2) {
                realAdapter().notifyItemRangeInserted(i + 1, i2);
            }

            @Override
            public void notifyItemRangeRemoved(int i, int i2) {
                realAdapter().notifyItemRangeRemoved(i + 1, i2);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                CharSequence charSequence;
                int itemViewType = viewHolder.getItemViewType();
                ItemInner itemInner = (ItemInner) FilterInvitesBottomSheet.this.items.get(i);
                int i2 = i + 1;
                boolean z = i2 < FilterInvitesBottomSheet.this.items.size() && !((ItemInner) FilterInvitesBottomSheet.this.items.get(i2)).isShadow();
                if (itemViewType == 7) {
                    ((LinkCell) viewHolder.itemView).setInvite(itemInner.link, z);
                    return;
                }
                if (itemViewType != 6 && itemViewType != 3) {
                    if (itemViewType != 0 && itemViewType == 8) {
                        CreateLinkCell createLinkCell = (CreateLinkCell) viewHolder.itemView;
                        createLinkCell.setText(LocaleController.getString(R.string.CreateNewInviteLink));
                        createLinkCell.setDivider(z);
                        return;
                    }
                    return;
                }
                TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                if (itemViewType == 6) {
                    textInfoPrivacyCell.setFixedSize(0);
                    charSequence = itemInner.text;
                } else {
                    textInfoPrivacyCell.setFixedSize(12);
                    charSequence = "";
                }
                textInfoPrivacyCell.setText(charSequence);
                textInfoPrivacyCell.setForeground(Theme.getThemedDrawableByKey(FilterInvitesBottomSheet.this.getContext(), z ? R.drawable.greydivider : R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View textInfoPrivacyCell;
                int i2;
                if (i == 8) {
                    textInfoPrivacyCell = new CreateLinkCell(FilterInvitesBottomSheet.this.getContext());
                } else {
                    if (i != 7) {
                        if (i != 6 && i != 3) {
                            FilterInvitesBottomSheet filterInvitesBottomSheet = FilterInvitesBottomSheet.this;
                            textInfoPrivacyCell = new HeaderView(filterInvitesBottomSheet.getContext());
                            return new RecyclerListView.Holder(textInfoPrivacyCell);
                        }
                        textInfoPrivacyCell = new TextInfoPrivacyCell(FilterInvitesBottomSheet.this.getContext());
                        i2 = Theme.key_windowBackgroundGray;
                        textInfoPrivacyCell.setBackgroundColor(Theme.getColor(i2));
                        return new RecyclerListView.Holder(textInfoPrivacyCell);
                    }
                    textInfoPrivacyCell = new C00421(FilterInvitesBottomSheet.this.getContext(), null, ((BottomSheet) FilterInvitesBottomSheet.this).currentAccount, FilterInvitesBottomSheet.this.filter.id);
                }
                i2 = Theme.key_dialogBackground;
                textInfoPrivacyCell.setBackgroundColor(Theme.getColor(i2));
                return new RecyclerListView.Holder(textInfoPrivacyCell);
            }
        }

        public class HeaderView extends FrameLayout {
            private final ImageView closeImageView;
            private final ImageView imageView;
            private final TextView subtitleView;
            private final TextView titleView;

            public HeaderView(Context context) {
                super(context);
                ImageView imageView = new ImageView(context);
                this.imageView = imageView;
                ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
                imageView.setScaleType(scaleType);
                imageView.setImageResource(R.drawable.msg_limit_links);
                imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
                imageView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(22.0f), Theme.getColor(Theme.key_featuredStickers_addButton)));
                addView(imageView, LayoutHelper.createFrame(54, 44.0f, 49, 0.0f, 22.0f, 0.0f, 0.0f));
                TextView textView = new TextView(context);
                this.titleView = textView;
                textView.setText(FilterInvitesBottomSheet.this.getTitle());
                textView.setTypeface(AndroidUtilities.bold());
                textView.setTextSize(1, 20.0f);
                int i = Theme.key_dialogTextBlack;
                textView.setTextColor(Theme.getColor(i));
                textView.setGravity(1);
                addView(textView, LayoutHelper.createFrame(-2, -2.0f, 49, 20.0f, 84.0f, 20.0f, 0.0f));
                TextView textView2 = new TextView(context);
                this.subtitleView = textView2;
                textView2.setText(LocaleController.getString(FilterInvitesBottomSheet.this.invites.isEmpty() ? R.string.FolderLinkShareSubtitleEmpty : R.string.FolderLinkShareSubtitle));
                textView2.setLines(2);
                textView2.setGravity(1);
                textView2.setTextSize(1, 14.0f);
                textView2.setTextColor(Theme.getColor(i));
                addView(textView2, LayoutHelper.createFrame(-2, -2.0f, 49, 30.0f, 117.0f, 30.0f, 0.0f));
                ImageView imageView2 = new ImageView(context);
                this.closeImageView = imageView2;
                imageView2.setScaleType(scaleType);
                imageView2.setImageResource(R.drawable.msg_close);
                imageView2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText5), PorterDuff.Mode.MULTIPLY));
                imageView2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        FilterCreateActivity.FilterInvitesBottomSheet.HeaderView.this.lambda$new$0(view);
                    }
                });
                addView(imageView2, LayoutHelper.createFrame(48, 48.0f, 53, 0.0f, -4.0f, 2.0f, 0.0f));
            }

            public void lambda$new$0(View view) {
                FilterInvitesBottomSheet.this.dismiss();
            }

            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(171.0f), 1073741824));
            }
        }

        public FilterInvitesBottomSheet(BaseFragment baseFragment, MessagesController.DialogFilter dialogFilter, ArrayList arrayList) {
            super(baseFragment, false, false);
            this.invites = new ArrayList();
            this.oldItems = new ArrayList();
            this.items = new ArrayList();
            this.filter = dialogFilter;
            if (arrayList != null) {
                this.invites.addAll(arrayList);
            }
            updateRows(false);
            this.actionBar.setTitle(getTitle());
            fixNavigationBar(Theme.getColor(Theme.key_dialogBackground));
            TextView textView = new TextView(getContext());
            this.button = textView;
            textView.setTextSize(1, 14.0f);
            this.button.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
            this.button.setTypeface(AndroidUtilities.bold());
            this.button.setBackground(Theme.AdaptiveRipple.filledRectByKey(Theme.key_featuredStickers_addButton, 8.0f));
            this.button.setText(LocaleController.getString(R.string.FolderLinkShareButton));
            this.button.setGravity(17);
            this.button.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    FilterCreateActivity.FilterInvitesBottomSheet.this.lambda$new$2(view);
                }
            });
            this.containerView.addView(this.button, LayoutHelper.createFrame(-1, 48.0f, 87, 16.0f, 10.0f, 16.0f, 10.0f));
            FrameLayout frameLayout = new FrameLayout(getContext());
            this.bulletinContainer = frameLayout;
            this.containerView.addView(frameLayout, LayoutHelper.createFrame(-1, 100.0f, 80, 6.0f, 0.0f, 6.0f, 0.0f));
            updateCreateInviteButton();
        }

        private void createLink() {
            ArrayList<TLRPC.InputPeer> arrayList = new ArrayList<>();
            for (int i = 0; i < this.filter.alwaysShow.size(); i++) {
                long longValue = this.filter.alwaysShow.get(i).longValue();
                if (longValue < 0 && FilterCreateActivity.canAddToFolder(getBaseFragment().getMessagesController().getChat(Long.valueOf(-longValue)))) {
                    arrayList.add(getBaseFragment().getMessagesController().getInputPeer(longValue));
                }
            }
            if (arrayList.isEmpty()) {
                dismiss();
                getBaseFragment().presentFragment(new FilterChatlistActivity(this.filter, null));
                return;
            }
            TL_chatlists.TL_chatlists_exportChatlistInvite tL_chatlists_exportChatlistInvite = new TL_chatlists.TL_chatlists_exportChatlistInvite();
            TL_chatlists.TL_inputChatlistDialogFilter tL_inputChatlistDialogFilter = new TL_chatlists.TL_inputChatlistDialogFilter();
            tL_chatlists_exportChatlistInvite.chatlist = tL_inputChatlistDialogFilter;
            tL_inputChatlistDialogFilter.filter_id = this.filter.id;
            tL_chatlists_exportChatlistInvite.peers = arrayList;
            tL_chatlists_exportChatlistInvite.title = "";
            getBaseFragment().getConnectionsManager().sendRequest(tL_chatlists_exportChatlistInvite, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FilterCreateActivity.FilterInvitesBottomSheet.this.lambda$createLink$4(tLObject, tL_error);
                }
            });
        }

        public void lambda$createLink$3(TLRPC.TL_error tL_error, TLObject tLObject) {
            if (FilterCreateActivity.processErrors(tL_error, getBaseFragment(), BulletinFactory.of(this.bulletinContainer, null)) && (tLObject instanceof TL_chatlists.TL_chatlists_exportedChatlistInvite)) {
                FilterCreateActivity.hideNew(0);
                dismiss();
                getBaseFragment().getMessagesController().loadRemoteFilters(true);
                getBaseFragment().presentFragment(new FilterChatlistActivity(this.filter, ((TL_chatlists.TL_chatlists_exportedChatlistInvite) tLObject).invite));
            }
        }

        public void lambda$createLink$4(final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    FilterCreateActivity.FilterInvitesBottomSheet.this.lambda$createLink$3(tL_error, tLObject);
                }
            });
        }

        public void lambda$new$2(View view) {
            createLink();
        }

        public void lambda$onViewCreated$5(View view, int i) {
            int i2 = i - 1;
            if (i2 < 0 || i2 >= this.items.size()) {
                return;
            }
            ItemInner itemInner = (ItemInner) this.items.get(i2);
            int i3 = itemInner.viewType;
            if (i3 == 7) {
                dismiss();
                getBaseFragment().presentFragment(new FilterChatlistActivity(this.filter, itemInner.link));
            } else if (i3 == 8) {
                createLink();
            }
        }

        public static void lambda$show$0(BaseFragment baseFragment, TLObject tLObject, MessagesController.DialogFilter dialogFilter, TLRPC.TL_error tL_error, Runnable runnable, long j) {
            if (baseFragment == null || baseFragment.getContext() == null) {
                return;
            }
            if (tLObject instanceof TL_chatlists.TL_chatlists_exportedInvites) {
                TL_chatlists.TL_chatlists_exportedInvites tL_chatlists_exportedInvites = (TL_chatlists.TL_chatlists_exportedInvites) tLObject;
                baseFragment.getMessagesController().putChats(tL_chatlists_exportedInvites.chats, false);
                baseFragment.getMessagesController().putUsers(tL_chatlists_exportedInvites.users, false);
                new FilterInvitesBottomSheet(baseFragment, dialogFilter, tL_chatlists_exportedInvites.invites).show();
            } else if (tL_error == null || !"FILTER_ID_INVALID".equals(tL_error.text) || dialogFilter.isDefault()) {
                BulletinFactory.of(baseFragment).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
            } else {
                new FilterInvitesBottomSheet(baseFragment, dialogFilter, null).show();
            }
            if (runnable != null) {
                AndroidUtilities.runOnUIThread(runnable, Math.max(0L, 200 - (System.currentTimeMillis() - j)));
            }
        }

        public static void lambda$show$1(final BaseFragment baseFragment, final MessagesController.DialogFilter dialogFilter, final Runnable runnable, final long j, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    FilterCreateActivity.FilterInvitesBottomSheet.lambda$show$0(BaseFragment.this, tLObject, dialogFilter, tL_error, runnable, j);
                }
            });
        }

        public static void show(final BaseFragment baseFragment, final MessagesController.DialogFilter dialogFilter, final Runnable runnable) {
            final long currentTimeMillis = System.currentTimeMillis();
            TL_chatlists.TL_chatlists_getExportedInvites tL_chatlists_getExportedInvites = new TL_chatlists.TL_chatlists_getExportedInvites();
            TL_chatlists.TL_inputChatlistDialogFilter tL_inputChatlistDialogFilter = new TL_chatlists.TL_inputChatlistDialogFilter();
            tL_chatlists_getExportedInvites.chatlist = tL_inputChatlistDialogFilter;
            tL_inputChatlistDialogFilter.filter_id = dialogFilter.id;
            baseFragment.getConnectionsManager().sendRequest(tL_chatlists_getExportedInvites, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FilterCreateActivity.FilterInvitesBottomSheet.lambda$show$1(BaseFragment.this, dialogFilter, runnable, currentTimeMillis, tLObject, tL_error);
                }
            });
        }

        public void updateCreateInviteButton() {
            this.button.setVisibility(this.invites.isEmpty() ? 0 : 8);
            this.recyclerListView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), this.invites.isEmpty() ? AndroidUtilities.dp(68.0f) : 0);
        }

        public void updateRows(boolean z) {
            this.oldItems.clear();
            this.oldItems.addAll(this.items);
            this.items.clear();
            this.items.add(ItemInner.asHeader(null));
            if (!this.invites.isEmpty()) {
                this.items.add(ItemInner.asShadow(null));
                this.items.add(ItemInner.asCreateLink());
                for (int i = 0; i < this.invites.size(); i++) {
                    this.items.add(ItemInner.asLink((TL_chatlists.TL_exportedChatlistInvite) this.invites.get(i)));
                }
            }
            AdapterWithDiffUtils adapterWithDiffUtils = this.adapter;
            if (adapterWithDiffUtils != null) {
                if (z) {
                    adapterWithDiffUtils.setItems(this.oldItems, this.items);
                } else {
                    notifyDataSetChanged();
                }
            }
        }

        @Override
        protected RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView) {
            AnonymousClass1 anonymousClass1 = new AdapterWithDiffUtils() {

                public class C00421 extends LinkCell {
                    C00421(Context context, BaseFragment baseFragment, int i, int i2) {
                        super(context, baseFragment, i, i2);
                    }

                    public void copy() {
                        String str = this.lastUrl;
                        if (str != null && AndroidUtilities.addToClipboard(str)) {
                            BulletinFactory.of(FilterInvitesBottomSheet.this.bulletinContainer, null).createCopyLinkBulletin().show();
                        }
                    }

                    @Override
                    protected void onDelete(TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite) {
                        FilterInvitesBottomSheet.this.invites.remove(tL_exportedChatlistInvite);
                        FilterInvitesBottomSheet.this.updateCreateInviteButton();
                        FilterInvitesBottomSheet.this.updateRows(true);
                    }

                    @Override
                    public void options() {
                        ItemOptions makeOptions = ItemOptions.makeOptions(FilterInvitesBottomSheet.this.container, this);
                        makeOptions.add(R.drawable.msg_copy, LocaleController.getString(R.string.CopyLink), new Runnable() {
                            @Override
                            public final void run() {
                                FilterCreateActivity.FilterInvitesBottomSheet.AnonymousClass1.C00421.this.copy();
                            }
                        });
                        makeOptions.add(R.drawable.msg_qrcode, LocaleController.getString(R.string.GetQRCode), new Runnable() {
                            @Override
                            public final void run() {
                                FilterCreateActivity.FilterInvitesBottomSheet.AnonymousClass1.C00421.this.qrcode();
                            }
                        });
                        makeOptions.add(R.drawable.msg_delete, (CharSequence) LocaleController.getString(R.string.DeleteLink), true, new Runnable() {
                            @Override
                            public final void run() {
                                FilterCreateActivity.FilterInvitesBottomSheet.AnonymousClass1.C00421.this.deleteLink();
                            }
                        });
                        if (LocaleController.isRTL) {
                            makeOptions.setGravity(3);
                        }
                        makeOptions.show();
                    }
                }

                AnonymousClass1() {
                }

                private RecyclerView.Adapter realAdapter() {
                    return ((BottomSheetWithRecyclerListView) FilterInvitesBottomSheet.this).recyclerListView.getAdapter();
                }

                @Override
                public int getItemCount() {
                    return FilterInvitesBottomSheet.this.items.size();
                }

                @Override
                public int getItemViewType(int i) {
                    return ((ItemInner) FilterInvitesBottomSheet.this.items.get(i)).viewType;
                }

                @Override
                public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                    int itemViewType = viewHolder.getItemViewType();
                    return itemViewType == 8 || itemViewType == 7;
                }

                @Override
                public void notifyDataSetChanged() {
                    realAdapter().notifyDataSetChanged();
                }

                @Override
                public void notifyItemChanged(int i) {
                    realAdapter().notifyItemChanged(i + 1);
                }

                @Override
                public void notifyItemMoved(int i, int i2) {
                    realAdapter().notifyItemMoved(i + 1, i2);
                }

                @Override
                public void notifyItemRangeChanged(int i, int i2) {
                    realAdapter().notifyItemRangeChanged(i + 1, i2);
                }

                @Override
                public void notifyItemRangeChanged(int i, int i2, Object obj) {
                    realAdapter().notifyItemRangeChanged(i + 1, i2, obj);
                }

                @Override
                public void notifyItemRangeInserted(int i, int i2) {
                    realAdapter().notifyItemRangeInserted(i + 1, i2);
                }

                @Override
                public void notifyItemRangeRemoved(int i, int i2) {
                    realAdapter().notifyItemRangeRemoved(i + 1, i2);
                }

                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    CharSequence charSequence;
                    int itemViewType = viewHolder.getItemViewType();
                    ItemInner itemInner = (ItemInner) FilterInvitesBottomSheet.this.items.get(i);
                    int i2 = i + 1;
                    boolean z = i2 < FilterInvitesBottomSheet.this.items.size() && !((ItemInner) FilterInvitesBottomSheet.this.items.get(i2)).isShadow();
                    if (itemViewType == 7) {
                        ((LinkCell) viewHolder.itemView).setInvite(itemInner.link, z);
                        return;
                    }
                    if (itemViewType != 6 && itemViewType != 3) {
                        if (itemViewType != 0 && itemViewType == 8) {
                            CreateLinkCell createLinkCell = (CreateLinkCell) viewHolder.itemView;
                            createLinkCell.setText(LocaleController.getString(R.string.CreateNewInviteLink));
                            createLinkCell.setDivider(z);
                            return;
                        }
                        return;
                    }
                    TextInfoPrivacyCell textInfoPrivacyCell = (TextInfoPrivacyCell) viewHolder.itemView;
                    if (itemViewType == 6) {
                        textInfoPrivacyCell.setFixedSize(0);
                        charSequence = itemInner.text;
                    } else {
                        textInfoPrivacyCell.setFixedSize(12);
                        charSequence = "";
                    }
                    textInfoPrivacyCell.setText(charSequence);
                    textInfoPrivacyCell.setForeground(Theme.getThemedDrawableByKey(FilterInvitesBottomSheet.this.getContext(), z ? R.drawable.greydivider : R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow));
                }

                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    View textInfoPrivacyCell;
                    int i2;
                    if (i == 8) {
                        textInfoPrivacyCell = new CreateLinkCell(FilterInvitesBottomSheet.this.getContext());
                    } else {
                        if (i != 7) {
                            if (i != 6 && i != 3) {
                                FilterInvitesBottomSheet filterInvitesBottomSheet = FilterInvitesBottomSheet.this;
                                textInfoPrivacyCell = new HeaderView(filterInvitesBottomSheet.getContext());
                                return new RecyclerListView.Holder(textInfoPrivacyCell);
                            }
                            textInfoPrivacyCell = new TextInfoPrivacyCell(FilterInvitesBottomSheet.this.getContext());
                            i2 = Theme.key_windowBackgroundGray;
                            textInfoPrivacyCell.setBackgroundColor(Theme.getColor(i2));
                            return new RecyclerListView.Holder(textInfoPrivacyCell);
                        }
                        textInfoPrivacyCell = new C00421(FilterInvitesBottomSheet.this.getContext(), null, ((BottomSheet) FilterInvitesBottomSheet.this).currentAccount, FilterInvitesBottomSheet.this.filter.id);
                    }
                    i2 = Theme.key_dialogBackground;
                    textInfoPrivacyCell.setBackgroundColor(Theme.getColor(i2));
                    return new RecyclerListView.Holder(textInfoPrivacyCell);
                }
            };
            this.adapter = anonymousClass1;
            return anonymousClass1;
        }

        @Override
        protected CharSequence getTitle() {
            int i = R.string.FolderLinkShareTitle;
            MessagesController.DialogFilter dialogFilter = this.filter;
            return LocaleController.formatString("FolderLinkShareTitle", i, dialogFilter == null ? "" : dialogFilter.name);
        }

        @Override
        public void onViewCreated(FrameLayout frameLayout) {
            super.onViewCreated(frameLayout);
            this.recyclerListView.setOverScrollMode(2);
            this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                @Override
                public final void onItemClick(View view, int i) {
                    FilterCreateActivity.FilterInvitesBottomSheet.this.lambda$onViewCreated$5(view, i);
                }
            });
            DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
            defaultItemAnimator.setSupportsChangeAnimations(false);
            defaultItemAnimator.setDelayAnimations(false);
            defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            defaultItemAnimator.setDurations(350L);
            this.recyclerListView.setItemAnimator(defaultItemAnimator);
        }
    }

    public class HeaderCellColorPreview extends HeaderCell {
        private final AnimatedColor animatedColor;
        private int currentColor;
        public final TextView noTag;
        private boolean noTagShown;
        public final AnimatedTextView previewView;

        public class AnonymousClass1 extends AnimatedTextView {
            private final Paint backgroundPaint = new Paint(1);
            final FilterCreateActivity val$this$0;

            AnonymousClass1(Context context, boolean z, boolean z2, boolean z3, FilterCreateActivity filterCreateActivity) {
                super(context, z, z2, z3);
                r6 = filterCreateActivity;
                this.backgroundPaint = new Paint(1);
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                int i = HeaderCellColorPreview.this.animatedColor.set(HeaderCellColorPreview.this.currentColor);
                setTextColor(i);
                this.backgroundPaint.setColor(Theme.multAlpha(i, Theme.isCurrentThemeDark() ? 0.2f : 0.1f));
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set((getWidth() - getDrawable().getCurrentWidth()) - AndroidUtilities.dpf2(9.32f), (getHeight() - AndroidUtilities.dpf2(14.66f)) / 2.0f, getWidth(), (getHeight() + AndroidUtilities.dpf2(14.66f)) / 2.0f);
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.backgroundPaint);
                super.dispatchDraw(canvas);
            }
        }

        public HeaderCellColorPreview(Context context) {
            super(context, Theme.key_windowBackgroundWhiteBlueHeader, 22, 15, false, ((BaseFragment) FilterCreateActivity.this).resourceProvider);
            TextView textView = new TextView(getContext());
            this.noTag = textView;
            textView.setTextSize(1, 14.0f);
            textView.setTextColor(FilterCreateActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText2));
            textView.setText(LocaleController.getString(FilterCreateActivity.this.getUserConfig().isPremium() ? R.string.FolderTagNoColor : R.string.FolderTagNoColorPremium));
            textView.setGravity(5);
            int i = (LocaleController.isRTL ? 3 : 5) | 48;
            float f = this.padding;
            addView(textView, LayoutHelper.createFrame(-1, -1.0f, i, f, 16.66f, f, this.bottomMargin));
            textView.setAlpha(0.0f);
            AnonymousClass1 anonymousClass1 = new AnimatedTextView(getContext(), false, true, true) {
                private final Paint backgroundPaint = new Paint(1);
                final FilterCreateActivity val$this$0;

                AnonymousClass1(Context context2, boolean z, boolean z2, boolean z3, FilterCreateActivity filterCreateActivity) {
                    super(context2, z, z2, z3);
                    r6 = filterCreateActivity;
                    this.backgroundPaint = new Paint(1);
                }

                @Override
                protected void dispatchDraw(Canvas canvas) {
                    int i2 = HeaderCellColorPreview.this.animatedColor.set(HeaderCellColorPreview.this.currentColor);
                    setTextColor(i2);
                    this.backgroundPaint.setColor(Theme.multAlpha(i2, Theme.isCurrentThemeDark() ? 0.2f : 0.1f));
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set((getWidth() - getDrawable().getCurrentWidth()) - AndroidUtilities.dpf2(9.32f), (getHeight() - AndroidUtilities.dpf2(14.66f)) / 2.0f, getWidth(), (getHeight() + AndroidUtilities.dpf2(14.66f)) / 2.0f);
                    canvas.drawRoundRect(rectF, AndroidUtilities.dp(4.0f), AndroidUtilities.dp(4.0f), this.backgroundPaint);
                    super.dispatchDraw(canvas);
                }
            };
            this.previewView = anonymousClass1;
            this.animatedColor = new AnimatedColor(anonymousClass1, 0L, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
            anonymousClass1.setTextSize(AndroidUtilities.dp(10.0f));
            anonymousClass1.setTypeface(AndroidUtilities.bold());
            anonymousClass1.setGravity(5);
            anonymousClass1.setPadding(AndroidUtilities.dp(4.66f), 0, AndroidUtilities.dp(4.66f), 0);
            int i2 = LocaleController.isRTL ? 3 : 5;
            float f2 = this.padding;
            addView(anonymousClass1, LayoutHelper.createFrame(-1, -1.0f, i2 | 48, f2, 16.66f, f2, this.bottomMargin));
        }

        public void setPreviewColor(int i, boolean z) {
            this.noTag.setText(LocaleController.getString(FilterCreateActivity.this.getUserConfig().isPremium() ? R.string.FolderTagNoColor : R.string.FolderTagNoColorPremium));
            int i2 = 0;
            boolean z2 = i < 0;
            if (!z2) {
                FilterCreateActivity filterCreateActivity = FilterCreateActivity.this;
                int[] iArr = Theme.keys_avatar_nameInMessage;
                i2 = filterCreateActivity.getThemedColor(iArr[i % iArr.length]);
            }
            this.currentColor = i2;
            if (!z) {
                this.animatedColor.set(i2, true);
            }
            if (z2 != this.noTagShown) {
                this.noTagShown = z2;
                ViewPropertyAnimator duration = this.noTag.animate().alpha(z2 ? 1.0f : 0.0f).setDuration(320L);
                CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
                duration.setInterpolator(cubicBezierInterpolator).start();
                this.previewView.animate().alpha(z2 ? 0.0f : 1.0f).setDuration(320L).setInterpolator(cubicBezierInterpolator).start();
            }
        }

        public void setPreviewText(String str, boolean z) {
            boolean z2 = false;
            if (str.length() > 12) {
                str = str.substring(0, 12);
            }
            AnimatedTextView animatedTextView = this.previewView;
            CharSequence replaceEmoji = Emoji.replaceEmoji(str, animatedTextView.getPaint().getFontMetricsInt(), false);
            if (z && !LocaleController.isRTL) {
                z2 = true;
            }
            animatedTextView.setText(replaceEmoji, z2);
        }
    }

    public static class HintInnerCell extends FrameLayout {
        private RLottieImageView imageView;

        public HintInnerCell(Context context) {
            super(context);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setAnimation(R.raw.filter_new, 100, 100);
            this.imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.imageView.playAnimation();
            addView(this.imageView, LayoutHelper.createFrame(100, 100.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
            this.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    FilterCreateActivity.HintInnerCell.this.lambda$new$0(view);
                }
            });
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
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(156.0f), 1073741824));
        }
    }

    public static class ItemInner extends AdapterWithDiffUtils.Item {
        private String chatType;
        private long did;
        private int flags;
        private int iconResId;
        private boolean include;
        private boolean isRed;
        private TL_chatlists.TL_exportedChatlistInvite link;
        private boolean newSpan;
        private View.OnClickListener onClickListener;
        private CharSequence text;

        public ItemInner(int i, boolean z) {
            super(i, z);
        }

        public static ItemInner asButton(int i, CharSequence charSequence, boolean z) {
            ItemInner itemInner = new ItemInner(4, false);
            itemInner.iconResId = i;
            itemInner.text = charSequence;
            itemInner.isRed = z;
            return itemInner;
        }

        public static ItemInner asChat(boolean z, long j) {
            ItemInner itemInner = new ItemInner(1, false);
            itemInner.include = z;
            itemInner.did = j;
            return itemInner;
        }

        public static ItemInner asChat(boolean z, CharSequence charSequence, String str, int i) {
            ItemInner itemInner = new ItemInner(1, false);
            itemInner.include = z;
            itemInner.text = charSequence;
            itemInner.chatType = str;
            itemInner.flags = i;
            return itemInner;
        }

        public static ItemInner asCreateLink() {
            return new ItemInner(8, false);
        }

        public static ItemInner asEdit() {
            return new ItemInner(2, false);
        }

        public static ItemInner asHeader(CharSequence charSequence) {
            ItemInner itemInner = new ItemInner(0, false);
            itemInner.text = charSequence;
            return itemInner;
        }

        public static ItemInner asHeader(CharSequence charSequence, boolean z) {
            ItemInner itemInner = new ItemInner(0, false);
            itemInner.text = charSequence;
            itemInner.newSpan = z;
            return itemInner;
        }

        public static ItemInner asLink(TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite) {
            ItemInner itemInner = new ItemInner(7, false);
            itemInner.link = tL_exportedChatlistInvite;
            return itemInner;
        }

        public static ItemInner asShadow(CharSequence charSequence) {
            ItemInner itemInner = new ItemInner(TextUtils.isEmpty(charSequence) ? 3 : 6, false);
            itemInner.text = charSequence;
            return itemInner;
        }

        public boolean equals(Object obj) {
            TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite;
            TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite2;
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            ItemInner itemInner = (ItemInner) obj;
            int i = this.viewType;
            if (i != itemInner.viewType) {
                return false;
            }
            if ((i == 0 || i == 1 || i == 3 || i == 4) && !TextUtils.equals(this.text, itemInner.text)) {
                return false;
            }
            int i2 = this.viewType;
            if (i2 == 0) {
                return this.newSpan == itemInner.newSpan;
            }
            if (i2 == 1) {
                return this.did == itemInner.did && TextUtils.equals(this.chatType, itemInner.chatType) && this.flags == itemInner.flags;
            }
            if (i2 != 7 || (tL_exportedChatlistInvite = this.link) == (tL_exportedChatlistInvite2 = itemInner.link)) {
                return true;
            }
            if (TextUtils.equals(tL_exportedChatlistInvite.url, tL_exportedChatlistInvite2.url)) {
                TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite3 = this.link;
                boolean z = tL_exportedChatlistInvite3.revoked;
                TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite4 = itemInner.link;
                if (z == tL_exportedChatlistInvite4.revoked && TextUtils.equals(tL_exportedChatlistInvite3.title, tL_exportedChatlistInvite4.title) && this.link.peers.size() == itemInner.link.peers.size()) {
                    return true;
                }
            }
            return false;
        }

        public boolean isShadow() {
            int i = this.viewType;
            return i == 3 || i == 6;
        }

        public ItemInner whenClicked(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
            return this;
        }
    }

    public static class LinkCell extends FrameLayout {
        private int currentAccount;
        private int filterId;
        private BaseFragment fragment;
        private TL_chatlists.TL_exportedChatlistInvite lastInvite;
        private boolean lastRevoked;
        protected String lastUrl;
        Drawable linkIcon;
        boolean needDivider;
        ImageView optionsIcon;
        Paint paint;
        float revokeT;
        Drawable revokedLinkIcon;
        Paint revokedPaint;
        AnimatedTextView subtitleTextView;
        AnimatedTextView titleTextView;
        private ValueAnimator valueAnimator;

        public class AnonymousClass1 extends AnimatorListenerAdapter {
            final boolean val$value;

            AnonymousClass1(boolean z) {
                r2 = z;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                LinkCell linkCell = LinkCell.this;
                linkCell.revokeT = r2 ? 1.0f : 0.0f;
                linkCell.invalidate();
            }
        }

        public LinkCell(Context context, BaseFragment baseFragment, int i, int i2) {
            super(context);
            this.fragment = baseFragment;
            this.currentAccount = i;
            this.filterId = i2;
            setImportantForAccessibility(1);
            setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, false);
            this.titleTextView = animatedTextView;
            animatedTextView.setTextSize(AndroidUtilities.dp(15.66f));
            this.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            this.titleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            this.titleTextView.setEllipsizeByGradient(true);
            AnimatedTextView animatedTextView2 = this.titleTextView;
            boolean z = LocaleController.isRTL;
            addView(animatedTextView2, LayoutHelper.createFrame(-1, 20.0f, 55, z ? 56.0f : 64.0f, 10.33f, z ? 64.0f : 56.0f, 0.0f));
            AnimatedTextView animatedTextView3 = new AnimatedTextView(context, false, false, false);
            this.subtitleTextView = animatedTextView3;
            animatedTextView3.setTextSize(AndroidUtilities.dp(13.0f));
            this.subtitleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
            this.subtitleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
            AnimatedTextView animatedTextView4 = this.subtitleTextView;
            boolean z2 = LocaleController.isRTL;
            addView(animatedTextView4, LayoutHelper.createFrame(-1, 16.0f, 55, z2 ? 56.0f : 64.0f, 33.33f, z2 ? 64.0f : 56.0f, 0.0f));
            ImageView imageView = new ImageView(context);
            this.optionsIcon = imageView;
            imageView.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_ab_other));
            this.optionsIcon.setScaleType(ImageView.ScaleType.CENTER);
            this.optionsIcon.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector)));
            ImageView imageView2 = this.optionsIcon;
            int color = Theme.getColor(Theme.key_stickers_menu);
            PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
            imageView2.setColorFilter(new PorterDuffColorFilter(color, mode));
            this.optionsIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    FilterCreateActivity.LinkCell.this.lambda$new$0(view);
                }
            });
            this.optionsIcon.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
            ImageView imageView3 = this.optionsIcon;
            boolean z3 = LocaleController.isRTL;
            addView(imageView3, LayoutHelper.createFrame(40, 40.0f, (z3 ? 3 : 5) | 16, z3 ? 8.0f : 4.0f, 4.0f, z3 ? 4.0f : 8.0f, 4.0f));
            Paint paint = new Paint();
            this.paint = paint;
            paint.setColor(Theme.getColor(Theme.key_featuredStickers_addButton));
            Paint paint2 = new Paint();
            this.revokedPaint = paint2;
            paint2.setColor(Theme.getColor(Theme.key_color_red));
            Drawable mutate = getContext().getResources().getDrawable(R.drawable.msg_link_1).mutate();
            this.linkIcon = mutate;
            mutate.setColorFilter(new PorterDuffColorFilter(-1, mode));
            Drawable mutate2 = getContext().getResources().getDrawable(R.drawable.msg_link_2).mutate();
            this.revokedLinkIcon = mutate2;
            mutate2.setColorFilter(new PorterDuffColorFilter(-1, mode));
            setWillNotDraw(false);
        }

        private String getSlug() {
            String str = this.lastUrl;
            if (str == null) {
                return null;
            }
            return str.substring(str.lastIndexOf(47) + 1);
        }

        public void lambda$deleteLink$4() {
            onDelete(this.lastInvite);
        }

        public void lambda$deleteLink$5(TLRPC.TL_error tL_error, Runnable runnable) {
            if (tL_error != null) {
                BulletinFactory.of(this.fragment).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
        }

        public void lambda$deleteLink$6(final Runnable runnable, TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    FilterCreateActivity.LinkCell.this.lambda$deleteLink$5(tL_error, runnable);
                }
            });
        }

        public void lambda$new$0(View view) {
            options();
        }

        public void lambda$setRevoked$1(ValueAnimator valueAnimator) {
            this.revokeT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }

        public void deleteLink() {
            String slug = getSlug();
            if (slug == null) {
                return;
            }
            TL_chatlists.TL_chatlists_deleteExportedInvite tL_chatlists_deleteExportedInvite = new TL_chatlists.TL_chatlists_deleteExportedInvite();
            TL_chatlists.TL_inputChatlistDialogFilter tL_inputChatlistDialogFilter = new TL_chatlists.TL_inputChatlistDialogFilter();
            tL_chatlists_deleteExportedInvite.chatlist = tL_inputChatlistDialogFilter;
            tL_inputChatlistDialogFilter.filter_id = this.filterId;
            tL_chatlists_deleteExportedInvite.slug = slug;
            final Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    FilterCreateActivity.LinkCell.this.lambda$deleteLink$4();
                }
            };
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_chatlists_deleteExportedInvite, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    FilterCreateActivity.LinkCell.this.lambda$deleteLink$6(runnable, tLObject, tL_error);
                }
            });
            AndroidUtilities.runOnUIThread(runnable, 150L);
        }

        protected abstract void onDelete(TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite);

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int measuredWidth = LocaleController.isRTL ? getMeasuredWidth() - AndroidUtilities.dp(32.0f) : AndroidUtilities.dp(32.0f);
            float f = measuredWidth;
            canvas.drawCircle(f, getMeasuredHeight() / 2.0f, AndroidUtilities.dp(16.0f), this.paint);
            if (this.revokeT > 0.0f) {
                canvas.drawCircle(f, getMeasuredHeight() / 2.0f, AndroidUtilities.dp(16.0f) * this.revokeT, this.revokedPaint);
            }
            float f2 = this.revokeT;
            if (f2 < 1.0f) {
                this.linkIcon.setAlpha((int) ((1.0f - f2) * 255.0f));
                this.linkIcon.setBounds(measuredWidth - AndroidUtilities.dp(14.0f), (getMeasuredHeight() / 2) - AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f) + measuredWidth, (getMeasuredHeight() / 2) + AndroidUtilities.dp(14.0f));
                this.linkIcon.draw(canvas);
            }
            float f3 = this.revokeT;
            if (f3 > 0.0f) {
                this.revokedLinkIcon.setAlpha((int) (f3 * 255.0f));
                this.revokedLinkIcon.setBounds(measuredWidth - AndroidUtilities.dp(14.0f), (getMeasuredHeight() / 2) - AndroidUtilities.dp(14.0f), measuredWidth + AndroidUtilities.dp(14.0f), (getMeasuredHeight() / 2) + AndroidUtilities.dp(14.0f));
                this.revokedLinkIcon.draw(canvas);
            }
            if (this.needDivider) {
                canvas.drawRect(LocaleController.isRTL ? 0.0f : AndroidUtilities.dp(64.0f), getMeasuredHeight() - 1, getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(64.0f) : 0), getMeasuredHeight(), Theme.dividerPaint);
            }
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            String str;
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            StringBuilder sb = new StringBuilder();
            TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite = this.lastInvite;
            String str2 = "";
            if (tL_exportedChatlistInvite == null || TextUtils.isEmpty(tL_exportedChatlistInvite.title)) {
                str = "";
            } else {
                str = this.lastInvite.title + "\n ";
            }
            sb.append(str);
            sb.append(LocaleController.getString(R.string.InviteLink));
            sb.append(", ");
            sb.append((Object) this.subtitleTextView.getText());
            TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite2 = this.lastInvite;
            if (tL_exportedChatlistInvite2 != null && TextUtils.isEmpty(tL_exportedChatlistInvite2.title)) {
                str2 = "\n\n" + this.lastInvite.url;
            }
            sb.append(str2);
            accessibilityNodeInfo.setContentDescription(sb.toString());
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(60.0f), 1073741824));
        }

        public void options() {
            BaseFragment baseFragment = this.fragment;
            if (baseFragment == null) {
                return;
            }
            ItemOptions makeOptions = ItemOptions.makeOptions(baseFragment, this);
            makeOptions.add(R.drawable.msg_qrcode, LocaleController.getString(R.string.GetQRCode), new Runnable() {
                @Override
                public final void run() {
                    FilterCreateActivity.LinkCell.this.qrcode();
                }
            });
            makeOptions.add(R.drawable.msg_delete, (CharSequence) LocaleController.getString(R.string.DeleteLink), true, new Runnable() {
                @Override
                public final void run() {
                    FilterCreateActivity.LinkCell.this.deleteLink();
                }
            });
            if (LocaleController.isRTL) {
                makeOptions.setGravity(3);
            }
            makeOptions.show();
        }

        public void qrcode() {
            if (this.lastUrl == null) {
                return;
            }
            QRCodeBottomSheet qRCodeBottomSheet = new QRCodeBottomSheet(getContext(), LocaleController.getString(R.string.InviteByQRCode), this.lastUrl, LocaleController.getString(R.string.QRCodeLinkHelpFolder), false);
            qRCodeBottomSheet.setCenterAnimation(R.raw.qr_code_logo);
            qRCodeBottomSheet.show();
        }

        public void setInvite(TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite, boolean z) {
            boolean z2 = this.lastInvite == tL_exportedChatlistInvite;
            this.lastInvite = tL_exportedChatlistInvite;
            String str = tL_exportedChatlistInvite.url;
            this.lastUrl = str;
            if (str.startsWith("http://")) {
                str = str.substring(7);
            }
            if (str.startsWith("https://")) {
                str = str.substring(8);
            }
            if (TextUtils.isEmpty(tL_exportedChatlistInvite.title)) {
                this.titleTextView.setText(str, z2);
            } else {
                this.titleTextView.setText(tL_exportedChatlistInvite.title, z2);
            }
            this.subtitleTextView.setText(LocaleController.formatPluralString("FilterInviteChats", tL_exportedChatlistInvite.peers.size(), new Object[0]), z2);
            if (this.needDivider != z) {
                this.needDivider = z;
                invalidate();
            }
            setRevoked(tL_exportedChatlistInvite.revoked, z2);
        }

        public void setRevoked(boolean z, boolean z2) {
            this.lastRevoked = z;
            if ((z ? 1.0f : 0.0f) != this.revokeT) {
                ValueAnimator valueAnimator = this.valueAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.valueAnimator = null;
                }
                if (!z2) {
                    this.revokeT = z ? 1.0f : 0.0f;
                    invalidate();
                    return;
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(this.revokeT, z ? 1.0f : 0.0f);
                this.valueAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        FilterCreateActivity.LinkCell.this.lambda$setRevoked$1(valueAnimator2);
                    }
                });
                this.valueAnimator.addListener(new AnimatorListenerAdapter() {
                    final boolean val$value;

                    AnonymousClass1(boolean z3) {
                        r2 = z3;
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        LinkCell linkCell = LinkCell.this;
                        linkCell.revokeT = r2 ? 1.0f : 0.0f;
                        linkCell.invalidate();
                    }
                });
                this.valueAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.valueAnimator.setDuration(350L);
                this.valueAnimator.start();
            }
        }
    }

    public class ListAdapter extends AdapterWithDiffUtils {
        private Context mContext;

        class AnonymousClass1 implements TextWatcher {
            final PollEditTextCell val$cell;

            AnonymousClass1(PollEditTextCell pollEditTextCell) {
                r2 = pollEditTextCell;
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (r2.getTag() != null) {
                    return;
                }
                String obj = editable.toString();
                if (!TextUtils.equals(obj, FilterCreateActivity.this.newFilterName)) {
                    FilterCreateActivity.this.nameChangedManually = !TextUtils.isEmpty(obj);
                    FilterCreateActivity.this.newFilterName = obj;
                    if (FilterCreateActivity.this.folderTagsHeader != null) {
                        FilterCreateActivity.this.folderTagsHeader.setPreviewText((FilterCreateActivity.this.newFilterName == null ? "" : FilterCreateActivity.this.newFilterName).toUpperCase(), true);
                    }
                }
                RecyclerView.ViewHolder findViewHolderForAdapterPosition = FilterCreateActivity.this.listView.findViewHolderForAdapterPosition(FilterCreateActivity.this.nameRow);
                if (findViewHolderForAdapterPosition != null) {
                    FilterCreateActivity.this.setTextLeft(findViewHolderForAdapterPosition.itemView);
                }
                FilterCreateActivity.this.checkDoneButton(true);
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        }

        class AnonymousClass2 extends LinkCell {
            AnonymousClass2(Context context, BaseFragment baseFragment, int i, int i2) {
                super(context, baseFragment, i, i2);
            }

            @Override
            protected void onDelete(TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite) {
                FilterCreateActivity.this.onDelete(tL_exportedChatlistInvite);
            }
        }

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public void lambda$onBindViewHolder$1(PeerColorActivity.PeerColorGrid peerColorGrid, Integer num) {
            if (!FilterCreateActivity.this.getUserConfig().isPremium()) {
                FilterCreateActivity.this.showDialog(new PremiumFeatureBottomSheet(FilterCreateActivity.this, 35, true));
                return;
            }
            peerColorGrid.setSelected(FilterCreateActivity.this.newFilterColor = num.intValue(), true);
            if (FilterCreateActivity.this.folderTagsHeader != null) {
                FilterCreateActivity.this.folderTagsHeader.setPreviewColor(!FilterCreateActivity.this.getUserConfig().isPremium() ? -1 : FilterCreateActivity.this.newFilterColor, true);
            }
            FilterCreateActivity.this.checkDoneButton(true);
        }

        public void lambda$onCreateViewHolder$0(PollEditTextCell pollEditTextCell, View view, boolean z) {
            pollEditTextCell.getTextView2().setAlpha((z || FilterCreateActivity.this.newFilterName.length() > 12) ? 1.0f : 0.0f);
        }

        @Override
        public int getItemCount() {
            return FilterCreateActivity.this.items.size();
        }

        @Override
        public int getItemViewType(int i) {
            ItemInner itemInner = (ItemInner) FilterCreateActivity.this.items.get(i);
            if (itemInner == null) {
                return 3;
            }
            return itemInner.viewType;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            return (itemViewType == 3 || itemViewType == 0 || itemViewType == 2 || itemViewType == 5 || itemViewType == 9) ? false : true;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r11, int r12) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.FilterCreateActivity.ListAdapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View headerCell;
            UserCell userCell;
            switch (i) {
                case 0:
                    headerCell = new HeaderCell(this.mContext, 22);
                    headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 1:
                    UserCell userCell2 = new UserCell(this.mContext, 6, 0, false);
                    userCell2.setSelfAsSavedMessages(true);
                    userCell2.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    userCell = userCell2;
                    headerCell = userCell;
                    break;
                case 2:
                    final PollEditTextCell pollEditTextCell = new PollEditTextCell(this.mContext, null);
                    pollEditTextCell.createErrorTextView();
                    pollEditTextCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    pollEditTextCell.addTextWatcher(new TextWatcher() {
                        final PollEditTextCell val$cell;

                        AnonymousClass1(final PollEditTextCell pollEditTextCell2) {
                            r2 = pollEditTextCell2;
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (r2.getTag() != null) {
                                return;
                            }
                            String obj = editable.toString();
                            if (!TextUtils.equals(obj, FilterCreateActivity.this.newFilterName)) {
                                FilterCreateActivity.this.nameChangedManually = !TextUtils.isEmpty(obj);
                                FilterCreateActivity.this.newFilterName = obj;
                                if (FilterCreateActivity.this.folderTagsHeader != null) {
                                    FilterCreateActivity.this.folderTagsHeader.setPreviewText((FilterCreateActivity.this.newFilterName == null ? "" : FilterCreateActivity.this.newFilterName).toUpperCase(), true);
                                }
                            }
                            RecyclerView.ViewHolder findViewHolderForAdapterPosition = FilterCreateActivity.this.listView.findViewHolderForAdapterPosition(FilterCreateActivity.this.nameRow);
                            if (findViewHolderForAdapterPosition != null) {
                                FilterCreateActivity.this.setTextLeft(findViewHolderForAdapterPosition.itemView);
                            }
                            FilterCreateActivity.this.checkDoneButton(true);
                        }

                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i2, int i22, int i3) {
                        }
                    });
                    EditTextBoldCursor textView = pollEditTextCell2.getTextView();
                    pollEditTextCell2.setShowNextButton(true);
                    textView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public final void onFocusChange(View view, boolean z) {
                            FilterCreateActivity.ListAdapter.this.lambda$onCreateViewHolder$0(pollEditTextCell2, view, z);
                        }
                    });
                    textView.setImeOptions(268435462);
                    userCell = pollEditTextCell2;
                    headerCell = userCell;
                    break;
                case 3:
                    headerCell = new ShadowSectionCell(this.mContext);
                    break;
                case 4:
                    headerCell = new ButtonCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 5:
                    headerCell = new HintInnerCell(this.mContext);
                    break;
                case 6:
                default:
                    headerCell = new TextInfoPrivacyCell(this.mContext);
                    break;
                case 7:
                    Context context = this.mContext;
                    FilterCreateActivity filterCreateActivity = FilterCreateActivity.this;
                    headerCell = new LinkCell(context, filterCreateActivity, ((BaseFragment) filterCreateActivity).currentAccount, FilterCreateActivity.this.filter.id) {
                        AnonymousClass2(Context context2, BaseFragment filterCreateActivity2, int i2, int i22) {
                            super(context2, filterCreateActivity2, i2, i22);
                        }

                        @Override
                        protected void onDelete(TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite) {
                            FilterCreateActivity.this.onDelete(tL_exportedChatlistInvite);
                        }
                    };
                    break;
                case 8:
                    headerCell = new CreateLinkCell(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 9:
                    headerCell = new HeaderCellColorPreview(this.mContext);
                    headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
                case 10:
                    headerCell = new PeerColorActivity.PeerColorGrid(FilterCreateActivity.this.getContext(), 2, ((BaseFragment) FilterCreateActivity.this).currentAccount, ((BaseFragment) FilterCreateActivity.this).resourceProvider);
                    headerCell.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                    break;
            }
            return new RecyclerListView.Holder(headerCell);
        }

        @Override
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 2) {
                FilterCreateActivity.this.setTextLeft(viewHolder.itemView);
                PollEditTextCell pollEditTextCell = (PollEditTextCell) viewHolder.itemView;
                pollEditTextCell.setTag(1);
                pollEditTextCell.setTextAndHint(FilterCreateActivity.this.newFilterName != null ? FilterCreateActivity.this.newFilterName : "", LocaleController.getString(R.string.FilterNameHint), false);
                pollEditTextCell.setTag(null);
            }
        }

        @Override
        public void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.getItemViewType() == 2) {
                EditTextBoldCursor textView = ((PollEditTextCell) viewHolder.itemView).getTextView();
                if (textView.isFocused()) {
                    textView.clearFocus();
                    AndroidUtilities.hideKeyboard(textView);
                }
            }
        }
    }

    public static class NewSpan extends ReplacementSpan {
        Paint bgPaint;
        private int color;
        private int fontSize;
        float height;
        StaticLayout layout;
        private boolean outline;
        TextPaint textPaint;
        float width;

        public NewSpan(float f) {
            this.textPaint = new TextPaint(1);
            this.bgPaint = new Paint(1);
            this.outline = false;
            this.textPaint.setTypeface(AndroidUtilities.bold());
            this.bgPaint.setStyle(Paint.Style.FILL);
            this.textPaint.setTextSize(AndroidUtilities.dp(f));
        }

        public NewSpan(boolean z, int i) {
            this.textPaint = new TextPaint(1);
            this.bgPaint = new Paint(1);
            this.outline = z;
            this.fontSize = i;
            this.textPaint.setTypeface(AndroidUtilities.bold());
            if (!z) {
                this.bgPaint.setStyle(Paint.Style.FILL);
                this.textPaint.setTextSize(AndroidUtilities.dp(i < 0 ? 12.0f : i));
                return;
            }
            this.bgPaint.setStyle(Paint.Style.STROKE);
            this.bgPaint.setStrokeWidth(AndroidUtilities.dpf2(1.33f));
            this.textPaint.setTextSize(AndroidUtilities.dp(i < 0 ? 10.0f : i));
            this.textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            this.textPaint.setStrokeWidth(AndroidUtilities.dpf2(0.2f));
            if (Build.VERSION.SDK_INT >= 21) {
                this.textPaint.setLetterSpacing(0.03f);
            }
        }

        @Override
        public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
            TextPaint textPaint;
            float dp;
            makeLayout();
            int i6 = this.color;
            if (i6 == 0) {
                i6 = paint.getColor();
            }
            this.bgPaint.setColor(i6);
            if (this.outline) {
                textPaint = this.textPaint;
            } else {
                textPaint = this.textPaint;
                i6 = AndroidUtilities.computePerceivedBrightness(i6) > 0.721f ? -16777216 : -1;
            }
            textPaint.setColor(i6);
            float dp2 = f + AndroidUtilities.dp(2.0f);
            float dp3 = (i4 - this.height) + AndroidUtilities.dp(1.0f);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(dp2, dp3, this.width + dp2, this.height + dp3);
            if (this.outline) {
                dp = AndroidUtilities.dp(3.66f);
                rectF.left -= AndroidUtilities.dp(4.0f);
                rectF.top -= AndroidUtilities.dp(2.33f);
                rectF.right += AndroidUtilities.dp(3.66f);
                rectF.bottom += AndroidUtilities.dp(1.33f);
            } else {
                dp = AndroidUtilities.dp(4.4f);
                rectF.inset(AndroidUtilities.dp(-4.0f), AndroidUtilities.dp(this.fontSize == 8 ? -3.66f : -2.33f));
            }
            canvas.drawRoundRect(rectF, dp, dp, this.bgPaint);
            canvas.save();
            canvas.translate(dp2, dp3);
            this.layout.draw(canvas);
            canvas.restore();
        }

        @Override
        public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
            makeLayout();
            return (int) (AndroidUtilities.dp(10.0f) + this.width);
        }

        public StaticLayout makeLayout() {
            if (this.layout == null) {
                StaticLayout staticLayout = new StaticLayout("NEW", this.textPaint, AndroidUtilities.displaySize.x, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                this.layout = staticLayout;
                this.width = staticLayout.getLineWidth(0);
                this.height = this.layout.getHeight();
            }
            return this.layout;
        }

        public void setColor(int i) {
            this.color = i;
        }
    }

    public FilterCreateActivity() {
        this(null, null);
    }

    public FilterCreateActivity(MessagesController.DialogFilter dialogFilter) {
        this(dialogFilter, null);
    }

    public FilterCreateActivity(MessagesController.DialogFilter dialogFilter, ArrayList arrayList) {
        this.nameRow = -1;
        this.invites = new ArrayList();
        this.oldItems = new ArrayList();
        this.items = new ArrayList();
        this.shiftDp = -5.0f;
        this.filter = dialogFilter;
        if (dialogFilter == null) {
            MessagesController.DialogFilter dialogFilter2 = new MessagesController.DialogFilter();
            this.filter = dialogFilter2;
            dialogFilter2.id = 2;
            while (getMessagesController().dialogFiltersById.get(this.filter.id) != null) {
                this.filter.id++;
            }
            MessagesController.DialogFilter dialogFilter3 = this.filter;
            dialogFilter3.name = "";
            dialogFilter3.color = (int) (Math.random() * 8.0d);
            this.creatingNew = true;
        }
        MessagesController.DialogFilter dialogFilter4 = this.filter;
        this.newFilterName = dialogFilter4.name;
        this.newFilterFlags = dialogFilter4.flags;
        this.newFilterColor = dialogFilter4.color;
        ArrayList arrayList2 = new ArrayList(this.filter.alwaysShow);
        this.newAlwaysShow = arrayList2;
        if (arrayList != null) {
            arrayList2.addAll(arrayList);
        }
        this.newNeverShow = new ArrayList(this.filter.neverShow);
        this.newPinned = this.filter.pinnedDialogs.clone();
    }

    public static boolean canAddToFolder(TLRPC.Chat chat) {
        return ChatObject.canUserDoAdminAction(chat, 3) || (ChatObject.isPublic(chat) && !chat.join_request);
    }

    private boolean canCreateLink() {
        return !(TextUtils.isEmpty(this.newFilterName) && TextUtils.isEmpty(this.filter.name)) && (this.newFilterFlags & ((MessagesController.DIALOG_FILTER_FLAG_CHATLIST | MessagesController.DIALOG_FILTER_FLAG_CHATLIST_ADMIN) ^ (-1))) == 0 && this.newNeverShow.isEmpty() && !this.newAlwaysShow.isEmpty();
    }

    public boolean checkDiscard() {
        String string;
        DialogInterface.OnClickListener onClickListener;
        if (this.doneItem.getAlpha() != 1.0f) {
            return true;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        if (this.creatingNew) {
            builder.setTitle(LocaleController.getString(R.string.FilterDiscardNewTitle));
            builder.setMessage(LocaleController.getString(R.string.FilterDiscardNewAlert));
            string = LocaleController.getString(R.string.FilterDiscardNewSave);
            onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    FilterCreateActivity.this.lambda$checkDiscard$18(dialogInterface, i);
                }
            };
        } else {
            builder.setTitle(LocaleController.getString(R.string.FilterDiscardTitle));
            builder.setMessage(LocaleController.getString(R.string.FilterDiscardAlert));
            string = LocaleController.getString(R.string.ApplyTheme);
            onClickListener = new DialogInterface.OnClickListener() {
                @Override
                public final void onClick(DialogInterface dialogInterface, int i) {
                    FilterCreateActivity.this.lambda$checkDiscard$19(dialogInterface, i);
                }
            };
        }
        builder.setPositiveButton(string, onClickListener);
        builder.setNegativeButton(LocaleController.getString(R.string.PassportDiscard), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                FilterCreateActivity.this.lambda$checkDiscard$20(dialogInterface, i);
            }
        });
        showDialog(builder.create());
        return false;
    }

    public void checkDoneButton(boolean z) {
        boolean z2 = !TextUtils.isEmpty(this.newFilterName) && this.newFilterName.length() <= 12;
        if (z2) {
            boolean z3 = ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == 0 && this.newAlwaysShow.isEmpty()) ? false : true;
            z2 = (!z3 || this.creatingNew) ? z3 : hasChanges();
        }
        if (this.doneItem.isEnabled() == z2) {
            return;
        }
        this.doneItem.setEnabled(z2);
        if (z) {
            this.doneItem.animate().alpha(z2 ? 1.0f : 0.0f).scaleX(z2 ? 1.0f : 0.0f).scaleY(z2 ? 1.0f : 0.0f).setDuration(180L).start();
            return;
        }
        this.doneItem.setAlpha(z2 ? 1.0f : 0.0f);
        this.doneItem.setScaleX(z2 ? 1.0f : 0.0f);
        this.doneItem.setScaleY(z2 ? 1.0f : 0.0f);
    }

    public void deleteFolder(View view) {
        MessagesController.DialogFilter dialogFilter = this.filter;
        if (dialogFilter != null && dialogFilter.isChatlist()) {
            FolderBottomSheet.showForDeletion(this, this.filter.id, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    FilterCreateActivity.this.lambda$deleteFolder$13((Boolean) obj);
                }
            });
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.FilterDelete));
        builder.setMessage(LocaleController.getString(R.string.FilterDeleteAlert));
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.Delete), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                FilterCreateActivity.this.lambda$deleteFolder$16(dialogInterface, i);
            }
        });
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    private void fillFilterName() {
        int i;
        String str;
        if (this.creatingNew) {
            if (TextUtils.isEmpty(this.newFilterName) || !this.nameChangedManually) {
                int i2 = this.newFilterFlags;
                int i3 = MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS;
                int i4 = i2 & i3;
                if ((i4 & i3) == i3) {
                    if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ & i2) != 0) {
                        i = R.string.FilterNameUnread;
                    } else {
                        if ((i2 & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0) {
                            i = R.string.FilterNameNonMuted;
                        }
                        str = "";
                    }
                    str = LocaleController.getString(i);
                } else {
                    int i5 = MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
                    if ((i4 & i5) != 0) {
                        if (((i5 ^ (-1)) & i4) == 0) {
                            i = R.string.FilterContacts;
                            str = LocaleController.getString(i);
                        }
                        str = "";
                    } else {
                        int i6 = MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                        if ((i4 & i6) != 0) {
                            if (((i6 ^ (-1)) & i4) == 0) {
                                i = R.string.FilterNonContacts;
                                str = LocaleController.getString(i);
                            }
                            str = "";
                        } else {
                            int i7 = MessagesController.DIALOG_FILTER_FLAG_GROUPS;
                            if ((i4 & i7) != 0) {
                                if (((i7 ^ (-1)) & i4) == 0) {
                                    i = R.string.FilterGroups;
                                    str = LocaleController.getString(i);
                                }
                                str = "";
                            } else {
                                int i8 = MessagesController.DIALOG_FILTER_FLAG_BOTS;
                                if ((i4 & i8) != 0) {
                                    if (((i8 ^ (-1)) & i4) == 0) {
                                        i = R.string.FilterBots;
                                        str = LocaleController.getString(i);
                                    }
                                    str = "";
                                } else {
                                    int i9 = MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
                                    if ((i4 & i9) != 0 && ((i9 ^ (-1)) & i4) == 0) {
                                        i = R.string.FilterChannels;
                                        str = LocaleController.getString(i);
                                    }
                                    str = "";
                                }
                            }
                        }
                    }
                }
                if (str != null && str.length() > 12) {
                    str = "";
                }
                this.newFilterName = str;
                HeaderCellColorPreview headerCellColorPreview = this.folderTagsHeader;
                if (headerCellColorPreview != null) {
                    headerCellColorPreview.setPreviewText((str != null ? str : "").toUpperCase(), false);
                }
                RecyclerView.ViewHolder findViewHolderForAdapterPosition = this.listView.findViewHolderForAdapterPosition(this.nameRow);
                if (findViewHolderForAdapterPosition != null) {
                    this.adapter.onViewAttachedToWindow(findViewHolderForAdapterPosition);
                }
            }
        }
    }

    private boolean hasChanges() {
        this.hasUserChanged = false;
        if (this.filter.alwaysShow.size() != this.newAlwaysShow.size()) {
            this.hasUserChanged = true;
        }
        if (this.filter.neverShow.size() != this.newNeverShow.size()) {
            this.hasUserChanged = true;
        }
        MessagesController.DialogFilter dialogFilter = this.filter;
        if (dialogFilter.color != this.newFilterColor) {
            this.hasUserChanged = true;
        }
        if (!this.hasUserChanged) {
            Collections.sort(dialogFilter.alwaysShow);
            Collections.sort(this.newAlwaysShow);
            if (!this.filter.alwaysShow.equals(this.newAlwaysShow)) {
                this.hasUserChanged = true;
            }
            Collections.sort(this.filter.neverShow);
            Collections.sort(this.newNeverShow);
            if (!this.filter.neverShow.equals(this.newNeverShow)) {
                this.hasUserChanged = true;
            }
        }
        if (TextUtils.equals(this.filter.name, this.newFilterName) && this.filter.flags == this.newFilterFlags) {
            return this.hasUserChanged;
        }
        return true;
    }

    public static void hideNew(int i) {
        MessagesController.getGlobalMainSettings().edit().putBoolean("n_" + i, true).apply();
    }

    public void lambda$checkDiscard$18(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public void lambda$checkDiscard$19(DialogInterface dialogInterface, int i) {
        processDone();
    }

    public void lambda$checkDiscard$20(DialogInterface dialogInterface, int i) {
        lambda$onBackPressed$321();
    }

    public void lambda$createView$6(ItemInner itemInner) {
        FilterChatlistActivity filterChatlistActivity = new FilterChatlistActivity(this.filter, itemInner.link);
        filterChatlistActivity.setOnEdit(new FilterCreateActivity$$ExternalSyntheticLambda22(this));
        filterChatlistActivity.setOnDelete(new FilterCreateActivity$$ExternalSyntheticLambda23(this));
        presentFragment(filterChatlistActivity);
    }

    public void lambda$createView$7(View view, int i) {
        final ItemInner itemInner;
        if (getParentActivity() == null || (itemInner = (ItemInner) this.items.get(i)) == null) {
            return;
        }
        if (itemInner.onClickListener != null) {
            itemInner.onClickListener.onClick(view);
            return;
        }
        int i2 = itemInner.viewType;
        if (i2 == 1) {
            UserCell userCell = (UserCell) view;
            showRemoveAlert(itemInner, userCell.getName(), userCell.getCurrentObject(), itemInner.include);
            return;
        }
        if (i2 == 7) {
            Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    FilterCreateActivity.this.lambda$createView$6(itemInner);
                }
            };
            if (this.doneItem.isEnabled()) {
                save(false, runnable);
                return;
            } else {
                runnable.run();
                return;
            }
        }
        if (i2 == 8 || (i2 == 4 && itemInner.iconResId == R.drawable.msg2_link2)) {
            onClickCreateLink(view);
        } else if (itemInner.viewType == 2) {
            PollEditTextCell pollEditTextCell = (PollEditTextCell) view;
            pollEditTextCell.getTextView().requestFocus();
            AndroidUtilities.showKeyboard(pollEditTextCell.getTextView());
        }
    }

    public boolean lambda$createView$8(View view, int i) {
        ItemInner itemInner = (ItemInner) this.items.get(i);
        if (itemInner == null || !(view instanceof UserCell)) {
            return false;
        }
        UserCell userCell = (UserCell) view;
        showRemoveAlert(itemInner, userCell.getName(), userCell.getCurrentObject(), itemInner.include);
        return true;
    }

    public void lambda$deleteFolder$13(Boolean bool) {
        lambda$onBackPressed$321();
    }

    public void lambda$deleteFolder$14(AlertDialog alertDialog) {
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        getMessagesController().removeFilter(this.filter);
        getMessagesStorage().deleteDialogFilter(this.filter);
        lambda$onBackPressed$321();
    }

    public void lambda$deleteFolder$15(final AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FilterCreateActivity.this.lambda$deleteFolder$14(alertDialog);
            }
        });
    }

    public void lambda$deleteFolder$16(DialogInterface dialogInterface, int i) {
        final AlertDialog alertDialog;
        if (getParentActivity() != null) {
            alertDialog = new AlertDialog(getParentActivity(), 3);
            alertDialog.setCanCancel(false);
            alertDialog.show();
        } else {
            alertDialog = null;
        }
        TLRPC.TL_messages_updateDialogFilter tL_messages_updateDialogFilter = new TLRPC.TL_messages_updateDialogFilter();
        tL_messages_updateDialogFilter.id = this.filter.id;
        getConnectionsManager().sendRequest(tL_messages_updateDialogFilter, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                FilterCreateActivity.this.lambda$deleteFolder$15(alertDialog, tLObject, tL_error);
            }
        });
    }

    public void lambda$getThemeDescriptions$27() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof UserCell) {
                    ((UserCell) childAt).update(0);
                }
            }
        }
    }

    public void lambda$loadInvites$0(TLObject tLObject) {
        this.loadingInvites = false;
        if (tLObject instanceof TL_chatlists.TL_chatlists_exportedInvites) {
            TL_chatlists.TL_chatlists_exportedInvites tL_chatlists_exportedInvites = (TL_chatlists.TL_chatlists_exportedInvites) tLObject;
            getMessagesController().putChats(tL_chatlists_exportedInvites.chats, false);
            getMessagesController().putUsers(tL_chatlists_exportedInvites.users, false);
            this.invites.clear();
            this.invites.addAll(tL_chatlists_exportedInvites.invites);
            updateRows();
        }
        this.requestingInvitesReqId = 0;
    }

    public void lambda$loadInvites$1(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FilterCreateActivity.this.lambda$loadInvites$0(tLObject);
            }
        });
    }

    public void lambda$onClickCreateLink$10(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (processErrors(tL_error, this, BulletinFactory.of(this)) && (tLObject instanceof TL_chatlists.TL_chatlists_exportedChatlistInvite)) {
            hideNew(0);
            getMessagesController().loadRemoteFilters(true);
            final TL_chatlists.TL_chatlists_exportedChatlistInvite tL_chatlists_exportedChatlistInvite = (TL_chatlists.TL_chatlists_exportedChatlistInvite) tLObject;
            FilterChatlistActivity filterChatlistActivity = new FilterChatlistActivity(this.filter, tL_chatlists_exportedChatlistInvite.invite);
            filterChatlistActivity.setOnEdit(new FilterCreateActivity$$ExternalSyntheticLambda22(this));
            filterChatlistActivity.setOnDelete(new FilterCreateActivity$$ExternalSyntheticLambda23(this));
            presentFragment(filterChatlistActivity);
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    FilterCreateActivity.this.lambda$onClickCreateLink$9(tL_chatlists_exportedChatlistInvite);
                }
            }, 200L);
        }
    }

    public void lambda$onClickCreateLink$11(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FilterCreateActivity.this.lambda$onClickCreateLink$10(tL_error, tLObject);
            }
        });
    }

    public void lambda$onClickCreateLink$12() {
        getMessagesController().updateFilterDialogs(this.filter);
        ArrayList<TLRPC.InputPeer> arrayList = new ArrayList<>();
        for (int i = 0; i < this.filter.alwaysShow.size(); i++) {
            long longValue = this.filter.alwaysShow.get(i).longValue();
            if (longValue < 0 && canAddToFolder(getMessagesController().getChat(Long.valueOf(-longValue)))) {
                arrayList.add(getMessagesController().getInputPeer(longValue));
            }
        }
        if (arrayList.size() > (getUserConfig().isPremium() ? getMessagesController().dialogFiltersChatsLimitPremium : getMessagesController().dialogFiltersChatsLimitDefault)) {
            showDialog(new LimitReachedBottomSheet(this, getContext(), 4, this.currentAccount, null));
            return;
        }
        if (arrayList.isEmpty()) {
            FilterChatlistActivity filterChatlistActivity = new FilterChatlistActivity(this.filter, null);
            filterChatlistActivity.setOnEdit(new FilterCreateActivity$$ExternalSyntheticLambda22(this));
            filterChatlistActivity.setOnDelete(new FilterCreateActivity$$ExternalSyntheticLambda23(this));
            presentFragment(filterChatlistActivity);
            return;
        }
        TL_chatlists.TL_chatlists_exportChatlistInvite tL_chatlists_exportChatlistInvite = new TL_chatlists.TL_chatlists_exportChatlistInvite();
        TL_chatlists.TL_inputChatlistDialogFilter tL_inputChatlistDialogFilter = new TL_chatlists.TL_inputChatlistDialogFilter();
        tL_chatlists_exportChatlistInvite.chatlist = tL_inputChatlistDialogFilter;
        tL_inputChatlistDialogFilter.filter_id = this.filter.id;
        tL_chatlists_exportChatlistInvite.peers = arrayList;
        tL_chatlists_exportChatlistInvite.title = "";
        getConnectionsManager().sendRequest(tL_chatlists_exportChatlistInvite, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                FilterCreateActivity.this.lambda$onClickCreateLink$11(tLObject, tL_error);
            }
        });
    }

    public void lambda$onClickCreateLink$9(TL_chatlists.TL_chatlists_exportedChatlistInvite tL_chatlists_exportedChatlistInvite) {
        onEdit(tL_chatlists_exportedChatlistInvite.invite);
    }

    public void lambda$onUpdate$28(boolean z, int i) {
        BulletinFactory.of(this).createSimpleBulletin(z ? R.raw.folder_in : R.raw.folder_out, z ? LocaleController.formatPluralString("FolderLinkAddedChats", i, new Object[0]) : LocaleController.formatPluralString("FolderLinkRemovedChats", i, new Object[0]), LocaleController.getString(R.string.FolderLinkChatlistUpdate)).setDuration(5000).show();
    }

    public void lambda$processDone$22() {
        if (!this.doNotCloseWhenSave) {
            lambda$onBackPressed$321();
            return;
        }
        this.doNotCloseWhenSave = false;
        TextPaint textPaint = new TextPaint(1);
        textPaint.setTextSize(AndroidUtilities.dp(20.0f));
        this.actionBar.setTitleAnimated(Emoji.replaceEmoji((CharSequence) this.filter.name, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false), true, 220L);
    }

    public void lambda$save$23(Runnable runnable) {
        this.hasUserChanged = false;
        this.creatingNew = false;
        this.filter.flags = this.newFilterFlags;
        checkDoneButton(true);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        if (runnable != null) {
            runnable.run();
        }
    }

    public static int lambda$saveFilterToServer$24(LongSparseIntArray longSparseIntArray, Long l, Long l2) {
        int i = longSparseIntArray.get(l.longValue());
        int i2 = longSparseIntArray.get(l2.longValue());
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public static void lambda$saveFilterToServer$25(boolean z, AlertDialog alertDialog, MessagesController.DialogFilter dialogFilter, int i, String str, int i2, ArrayList arrayList, ArrayList arrayList2, boolean z2, boolean z3, boolean z4, boolean z5, BaseFragment baseFragment, Runnable runnable) {
        if (!z) {
            if (runnable != null) {
                runnable.run();
            }
        } else {
            if (alertDialog != null) {
                try {
                    alertDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            processAddFilter(dialogFilter, i, str, i2, arrayList, arrayList2, z2, z3, z4, z5, baseFragment, runnable);
        }
    }

    public static void lambda$saveFilterToServer$26(final boolean z, final AlertDialog alertDialog, final MessagesController.DialogFilter dialogFilter, final int i, final String str, final int i2, final ArrayList arrayList, final ArrayList arrayList2, final boolean z2, final boolean z3, final boolean z4, final boolean z5, final BaseFragment baseFragment, final Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                FilterCreateActivity.lambda$saveFilterToServer$25(z, alertDialog, dialogFilter, i, str, i2, arrayList, arrayList2, z2, z3, z4, z5, baseFragment, runnable);
            }
        });
    }

    public void lambda$selectChatsFor$17(boolean z, ArrayList arrayList, int i) {
        this.newFilterFlags = i;
        if (z) {
            onUpdate(true, this.newAlwaysShow, arrayList);
            this.newAlwaysShow = arrayList;
            for (int i2 = 0; i2 < this.newAlwaysShow.size(); i2++) {
                this.newNeverShow.remove(this.newAlwaysShow.get(i2));
            }
            ArrayList arrayList2 = new ArrayList();
            int size = this.newPinned.size();
            for (int i3 = 0; i3 < size; i3++) {
                long keyAt = this.newPinned.keyAt(i3);
                Long valueOf = Long.valueOf(keyAt);
                if (!DialogObject.isEncryptedDialog(keyAt) && !this.newAlwaysShow.contains(valueOf)) {
                    arrayList2.add(valueOf);
                }
            }
            int size2 = arrayList2.size();
            for (int i4 = 0; i4 < size2; i4++) {
                this.newPinned.delete(((Long) arrayList2.get(i4)).longValue());
            }
        } else {
            onUpdate(false, this.newNeverShow, arrayList);
            this.newNeverShow = arrayList;
            for (int i5 = 0; i5 < this.newNeverShow.size(); i5++) {
                Long l = (Long) this.newNeverShow.get(i5);
                this.newAlwaysShow.remove(l);
                this.newPinned.delete(l.longValue());
            }
        }
        fillFilterName();
        checkDoneButton(false);
        updateRows();
    }

    public void lambda$showRemoveAlert$21(ItemInner itemInner, boolean z, DialogInterface dialogInterface, int i) {
        if (itemInner.flags > 0) {
            this.newFilterFlags = (itemInner.flags ^ (-1)) & this.newFilterFlags;
        } else {
            (z ? this.newAlwaysShow : this.newNeverShow).remove(Long.valueOf(itemInner.did));
        }
        fillFilterName();
        updateRows();
        checkDoneButton(true);
        if (z) {
            onUpdate(false, 1);
        }
    }

    public void lambda$updateRows$2(View view) {
        selectChatsFor(true);
    }

    public void lambda$updateRows$3(View view) {
        this.includeExpanded = true;
        updateRows();
    }

    public void lambda$updateRows$4(View view) {
        selectChatsFor(false);
    }

    public void lambda$updateRows$5(View view) {
        this.excludeExpanded = true;
        updateRows();
    }

    private void onClickCreateLink(View view) {
        BulletinFactory of;
        int i;
        if (this.creatingNew && this.doneItem.getAlpha() > 0.0f) {
            float f = -this.shiftDp;
            this.shiftDp = f;
            AndroidUtilities.shakeViewSpring(view, f);
            BotWebViewVibrationEffect.APP_ERROR.vibrate();
            this.doNotCloseWhenSave = true;
            showSaveHint();
            return;
        }
        if (canCreateLink()) {
            save(false, new Runnable() {
                @Override
                public final void run() {
                    FilterCreateActivity.this.lambda$onClickCreateLink$12();
                }
            });
            return;
        }
        float f2 = -this.shiftDp;
        this.shiftDp = f2;
        AndroidUtilities.shakeViewSpring(view, f2);
        BotWebViewVibrationEffect.APP_ERROR.vibrate();
        if (TextUtils.isEmpty(this.newFilterName) && TextUtils.isEmpty(this.filter.name)) {
            of = BulletinFactory.of(this);
            i = R.string.FilterInviteErrorEmptyName;
        } else if ((this.newFilterFlags & ((MessagesController.DIALOG_FILTER_FLAG_CHATLIST | MessagesController.DIALOG_FILTER_FLAG_CHATLIST_ADMIN) ^ (-1))) != 0) {
            if (this.newNeverShow.isEmpty()) {
                of = BulletinFactory.of(this);
                i = R.string.FilterInviteErrorTypes;
            } else {
                of = BulletinFactory.of(this);
                i = R.string.FilterInviteErrorTypesExcluded;
            }
        } else if (this.newAlwaysShow.isEmpty()) {
            of = BulletinFactory.of(this);
            i = R.string.FilterInviteErrorEmpty;
        } else {
            of = BulletinFactory.of(this);
            i = R.string.FilterInviteErrorExcluded;
        }
        of.createErrorBulletin(LocaleController.getString(i)).show();
    }

    public void onDelete(TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite) {
        if (tL_exportedChatlistInvite == null) {
            return;
        }
        int i = 0;
        while (true) {
            if (i >= this.invites.size()) {
                i = -1;
                break;
            } else if (TextUtils.equals(((TL_chatlists.TL_exportedChatlistInvite) this.invites.get(i)).url, tL_exportedChatlistInvite.url)) {
                break;
            } else {
                i++;
            }
        }
        if (i >= 0) {
            this.invites.remove(i);
            if (this.invites.isEmpty()) {
                this.filter.flags &= MessagesController.DIALOG_FILTER_FLAG_CHATLIST ^ (-1);
            }
            updateRows();
        }
    }

    public void onEdit(TL_chatlists.TL_exportedChatlistInvite tL_exportedChatlistInvite) {
        if (tL_exportedChatlistInvite == null) {
            return;
        }
        int i = 0;
        while (true) {
            if (i >= this.invites.size()) {
                i = -1;
                break;
            } else if (TextUtils.equals(((TL_chatlists.TL_exportedChatlistInvite) this.invites.get(i)).url, tL_exportedChatlistInvite.url)) {
                break;
            } else {
                i++;
            }
        }
        if (i < 0) {
            this.invites.add(tL_exportedChatlistInvite);
        } else {
            this.invites.set(i, tL_exportedChatlistInvite);
        }
        updateRows();
    }

    private void onUpdate(final boolean z, final int i) {
        MessagesController.DialogFilter dialogFilter;
        if (!this.showedUpdateBulletin && (dialogFilter = this.filter) != null && dialogFilter.isChatlist() && this.filter.isMyChatlist()) {
            this.showedUpdateBulletin = true;
            this.showBulletinOnResume = new Runnable() {
                @Override
                public final void run() {
                    FilterCreateActivity.this.lambda$onUpdate$28(z, i);
                }
            };
            if (getLayoutContainer() != null) {
                this.showBulletinOnResume.run();
                this.showBulletinOnResume = null;
            }
        }
    }

    private void onUpdate(boolean z, ArrayList arrayList, ArrayList arrayList2) {
        int i;
        int size;
        if (arrayList == null || arrayList2 == null) {
            if (arrayList != null) {
                i = arrayList.size();
            } else if (arrayList2 != null) {
                size = arrayList2.size();
                i = 0;
            } else {
                i = 0;
            }
            size = 0;
        } else {
            i = 0;
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                if (!arrayList2.contains(arrayList.get(i2))) {
                    i++;
                }
            }
            size = 0;
            for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                if (!arrayList.contains(arrayList2.get(i3))) {
                    size++;
                }
            }
        }
        if (!z) {
            if (size > 0) {
                onUpdate(false, size);
            }
        } else if (size > 0 && size > i) {
            onUpdate(true, size);
        } else if (i > 0) {
            onUpdate(false, i);
        }
    }

    private static void processAddFilter(MessagesController.DialogFilter dialogFilter, int i, String str, int i2, ArrayList arrayList, ArrayList arrayList2, boolean z, boolean z2, boolean z3, boolean z4, BaseFragment baseFragment, Runnable runnable) {
        if (dialogFilter.flags != i || z3) {
            dialogFilter.pendingUnreadCount = -1;
            if (z4) {
                dialogFilter.unreadCount = -1;
            }
        }
        dialogFilter.flags = i;
        dialogFilter.name = str;
        dialogFilter.color = i2;
        dialogFilter.neverShow = arrayList2;
        dialogFilter.alwaysShow = arrayList;
        MessagesController messagesController = baseFragment.getMessagesController();
        if (z) {
            messagesController.addFilter(dialogFilter, z2);
        } else {
            messagesController.onFilterUpdate(dialogFilter);
        }
        baseFragment.getMessagesStorage().saveDialogFilter(dialogFilter, z2, true);
        if (z2) {
            TLRPC.TL_messages_updateDialogFiltersOrder tL_messages_updateDialogFiltersOrder = new TLRPC.TL_messages_updateDialogFiltersOrder();
            ArrayList<MessagesController.DialogFilter> dialogFilters = baseFragment.getMessagesController().getDialogFilters();
            int size = dialogFilters.size();
            for (int i3 = 0; i3 < size; i3++) {
                tL_messages_updateDialogFiltersOrder.order.add(Integer.valueOf(dialogFilters.get(i3).id));
            }
            baseFragment.getConnectionsManager().sendRequest(tL_messages_updateDialogFiltersOrder, null);
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public void processDone() {
        HintView hintView = this.saveHintView;
        if (hintView != null) {
            hintView.hide(true);
            this.saveHintView = null;
        }
        save(true, new Runnable() {
            @Override
            public final void run() {
                FilterCreateActivity.this.lambda$processDone$22();
            }
        });
    }

    public static boolean processErrors(TLRPC.TL_error tL_error, BaseFragment baseFragment, BulletinFactory bulletinFactory) {
        int i;
        LimitReachedBottomSheet limitReachedBottomSheet;
        if (tL_error != null && !TextUtils.isEmpty(tL_error.text)) {
            if ("INVITE_PEERS_TOO_MUCH".equals(tL_error.text)) {
                limitReachedBottomSheet = new LimitReachedBottomSheet(baseFragment, baseFragment.getContext(), 4, baseFragment.getCurrentAccount(), null);
            } else {
                if ("PEERS_LIST_EMPTY".equals(tL_error.text)) {
                    i = R.string.FolderLinkNoChatsError;
                } else if ("USER_CHANNELS_TOO_MUCH".equals(tL_error.text)) {
                    i = R.string.FolderLinkOtherAdminLimitError;
                } else if ("CHANNELS_TOO_MUCH".equals(tL_error.text)) {
                    limitReachedBottomSheet = new LimitReachedBottomSheet(baseFragment, baseFragment.getContext(), 5, baseFragment.getCurrentAccount(), null);
                } else if ("INVITES_TOO_MUCH".equals(tL_error.text)) {
                    limitReachedBottomSheet = new LimitReachedBottomSheet(baseFragment, baseFragment.getContext(), 12, baseFragment.getCurrentAccount(), null);
                } else if ("CHATLISTS_TOO_MUCH".equals(tL_error.text)) {
                    limitReachedBottomSheet = new LimitReachedBottomSheet(baseFragment, baseFragment.getContext(), 13, baseFragment.getCurrentAccount(), null);
                } else if ("INVITE_SLUG_EXPIRED".equals(tL_error.text)) {
                    i = R.string.NoFolderFound;
                } else if ("FILTER_INCLUDE_TOO_MUCH".equals(tL_error.text)) {
                    limitReachedBottomSheet = new LimitReachedBottomSheet(baseFragment, baseFragment.getContext(), 4, baseFragment.getCurrentAccount(), null);
                } else if ("DIALOG_FILTERS_TOO_MUCH".equals(tL_error.text)) {
                    limitReachedBottomSheet = new LimitReachedBottomSheet(baseFragment, baseFragment.getContext(), 3, baseFragment.getCurrentAccount(), null);
                } else {
                    i = R.string.UnknownError;
                }
                bulletinFactory.createErrorBulletin(LocaleController.getString(i)).show();
            }
            limitReachedBottomSheet.show();
        }
        return true;
    }

    private void save(boolean z, final Runnable runnable) {
        saveFilterToServer(this.filter, this.newFilterFlags, this.newFilterName, this.newFilterColor, this.newAlwaysShow, this.newNeverShow, this.newPinned, this.creatingNew, false, this.hasUserChanged, true, z, this, new Runnable() {
            @Override
            public final void run() {
                FilterCreateActivity.this.lambda$save$23(runnable);
            }
        });
    }

    public static void saveFilterToServer(final MessagesController.DialogFilter dialogFilter, final int i, final String str, final int i2, final ArrayList arrayList, final ArrayList arrayList2, final LongSparseIntArray longSparseIntArray, final boolean z, final boolean z2, final boolean z3, final boolean z4, final boolean z5, final BaseFragment baseFragment, final Runnable runnable) {
        AlertDialog alertDialog;
        ArrayList<TLRPC.InputPeer> arrayList3;
        ArrayList arrayList4;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        int i3 = 3;
        if (z5) {
            alertDialog = new AlertDialog(baseFragment.getParentActivity(), 3);
            alertDialog.setCanCancel(false);
            alertDialog.show();
        } else {
            alertDialog = null;
        }
        TLRPC.TL_messages_updateDialogFilter tL_messages_updateDialogFilter = new TLRPC.TL_messages_updateDialogFilter();
        tL_messages_updateDialogFilter.id = dialogFilter.id;
        int i4 = 1;
        tL_messages_updateDialogFilter.flags |= 1;
        TLRPC.TL_dialogFilter tL_dialogFilter = new TLRPC.TL_dialogFilter();
        tL_messages_updateDialogFilter.filter = tL_dialogFilter;
        tL_dialogFilter.contacts = (i & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0;
        tL_dialogFilter.non_contacts = (i & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0;
        tL_dialogFilter.groups = (i & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0;
        tL_dialogFilter.broadcasts = (i & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0;
        tL_dialogFilter.bots = (i & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0;
        tL_dialogFilter.exclude_muted = (i & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0;
        tL_dialogFilter.exclude_read = (i & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0;
        tL_dialogFilter.exclude_archived = (i & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) != 0;
        tL_dialogFilter.id = dialogFilter.id;
        tL_dialogFilter.title = str;
        int i5 = tL_dialogFilter.flags;
        if (i2 < 0) {
            tL_dialogFilter.flags = i5 & (-134217729);
            tL_dialogFilter.color = 0;
        } else {
            tL_dialogFilter.flags = i5 | 134217728;
            tL_dialogFilter.color = i2;
        }
        MessagesController messagesController = baseFragment.getMessagesController();
        ArrayList arrayList5 = new ArrayList();
        if (longSparseIntArray.size() != 0) {
            int size = longSparseIntArray.size();
            for (int i6 = 0; i6 < size; i6++) {
                long keyAt = longSparseIntArray.keyAt(i6);
                if (!DialogObject.isEncryptedDialog(keyAt)) {
                    arrayList5.add(Long.valueOf(keyAt));
                }
            }
            Collections.sort(arrayList5, new Comparator() {
                @Override
                public final int compare(Object obj, Object obj2) {
                    int lambda$saveFilterToServer$24;
                    lambda$saveFilterToServer$24 = FilterCreateActivity.lambda$saveFilterToServer$24(LongSparseIntArray.this, (Long) obj, (Long) obj2);
                    return lambda$saveFilterToServer$24;
                }
            });
        }
        int i7 = 0;
        while (i7 < i3) {
            TLRPC.TL_dialogFilter tL_dialogFilter2 = tL_messages_updateDialogFilter.filter;
            if (i7 == 0) {
                arrayList3 = tL_dialogFilter2.include_peers;
                arrayList4 = arrayList;
            } else if (i7 == i4) {
                arrayList3 = tL_dialogFilter2.exclude_peers;
                arrayList4 = arrayList2;
            } else {
                arrayList3 = tL_dialogFilter2.pinned_peers;
                arrayList4 = arrayList5;
            }
            int size2 = arrayList4.size();
            int i8 = 0;
            while (i8 < size2) {
                Long l = (Long) arrayList4.get(i8);
                ArrayList arrayList6 = arrayList5;
                long longValue = l.longValue();
                if ((i7 != 0 || longSparseIntArray.indexOfKey(longValue) < 0) && !DialogObject.isEncryptedDialog(longValue)) {
                    if (longValue > 0) {
                        TLRPC.User user = messagesController.getUser(l);
                        if (user != null) {
                            TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
                            tL_inputPeerUser.user_id = longValue;
                            tL_inputPeerUser.access_hash = user.access_hash;
                            arrayList3.add(tL_inputPeerUser);
                        }
                    } else {
                        long j = -longValue;
                        TLRPC.Chat chat = messagesController.getChat(Long.valueOf(j));
                        if (chat != null) {
                            if (ChatObject.isChannel(chat)) {
                                TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
                                tL_inputPeerChannel.channel_id = j;
                                tL_inputPeerChannel.access_hash = chat.access_hash;
                                arrayList3.add(tL_inputPeerChannel);
                            } else {
                                TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
                                tL_inputPeerChat.chat_id = j;
                                arrayList3.add(tL_inputPeerChat);
                            }
                        }
                    }
                }
                i8++;
                arrayList5 = arrayList6;
            }
            i7++;
            i3 = 3;
            i4 = 1;
        }
        final AlertDialog alertDialog2 = alertDialog;
        baseFragment.getConnectionsManager().sendRequest(tL_messages_updateDialogFilter, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                FilterCreateActivity.lambda$saveFilterToServer$26(z5, alertDialog2, dialogFilter, i, str, i2, arrayList, arrayList2, z, z2, z3, z4, baseFragment, runnable, tLObject, tL_error);
            }
        });
        if (z5) {
            return;
        }
        processAddFilter(dialogFilter, i, str, i2, arrayList, arrayList2, z, z2, z3, z4, baseFragment, null);
    }

    private void selectChatsFor(final boolean z) {
        UsersSelectActivity usersSelectActivity = new UsersSelectActivity(z, z ? this.newAlwaysShow : this.newNeverShow, this.newFilterFlags);
        usersSelectActivity.noChatTypes = this.filter.isChatlist();
        usersSelectActivity.setDelegate(new UsersSelectActivity.FilterUsersActivityDelegate() {
            @Override
            public final void didSelectChats(ArrayList arrayList, int i) {
                FilterCreateActivity.this.lambda$selectChatsFor$17(z, arrayList, i);
            }
        });
        presentFragment(usersSelectActivity);
    }

    public void setTextLeft(View view) {
        if (view instanceof PollEditTextCell) {
            PollEditTextCell pollEditTextCell = (PollEditTextCell) view;
            String str = this.newFilterName;
            int length = 12 - (str != null ? str.length() : 0);
            if (length > 3.6000004f) {
                pollEditTextCell.setText2("");
                return;
            }
            pollEditTextCell.setText2(String.format("%d", Integer.valueOf(length)));
            SimpleTextView textView2 = pollEditTextCell.getTextView2();
            int i = length < 0 ? Theme.key_text_RedRegular : Theme.key_windowBackgroundWhiteGrayText3;
            textView2.setTextColor(Theme.getColor(i));
            textView2.setTag(Integer.valueOf(i));
            textView2.setAlpha((pollEditTextCell.getTextView().isFocused() || length < 0) ? 1.0f : 0.0f);
        }
    }

    private void showRemoveAlert(final ItemInner itemInner, CharSequence charSequence, Object obj, final boolean z) {
        String formatString;
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        if (z) {
            builder.setTitle(LocaleController.getString(R.string.FilterRemoveInclusionTitle));
            formatString = obj instanceof String ? LocaleController.formatString("FilterRemoveInclusionText", R.string.FilterRemoveInclusionText, charSequence) : obj instanceof TLRPC.User ? LocaleController.formatString("FilterRemoveInclusionUserText", R.string.FilterRemoveInclusionUserText, charSequence) : LocaleController.formatString("FilterRemoveInclusionChatText", R.string.FilterRemoveInclusionChatText, charSequence);
        } else {
            builder.setTitle(LocaleController.getString(R.string.FilterRemoveExclusionTitle));
            formatString = obj instanceof String ? LocaleController.formatString("FilterRemoveExclusionText", R.string.FilterRemoveExclusionText, charSequence) : obj instanceof TLRPC.User ? LocaleController.formatString("FilterRemoveExclusionUserText", R.string.FilterRemoveExclusionUserText, charSequence) : LocaleController.formatString("FilterRemoveExclusionChatText", R.string.FilterRemoveExclusionChatText, charSequence);
        }
        builder.setMessage(formatString);
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.setPositiveButton(LocaleController.getString(R.string.StickersRemove), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                FilterCreateActivity.this.lambda$showRemoveAlert$21(itemInner, z, dialogInterface, i);
            }
        });
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    private void showSaveHint() {
        HintView hintView = this.saveHintView;
        if (hintView == null || hintView.getVisibility() != 0) {
            AnonymousClass3 anonymousClass3 = new HintView(getContext(), 6, true) {
                AnonymousClass3(Context context, int i, boolean z) {
                    super(context, i, z);
                }

                @Override
                public void setVisibility(int i) {
                    super.setVisibility(i);
                    if (i != 0) {
                        try {
                            ((ViewGroup) getParent()).removeView(this);
                        } catch (Exception unused) {
                        }
                    }
                }
            };
            this.saveHintView = anonymousClass3;
            anonymousClass3.textView.setMaxWidth(AndroidUtilities.displaySize.x);
            this.saveHintView.setExtraTranslationY(AndroidUtilities.dp(-16.0f));
            this.saveHintView.setText(LocaleController.getString(R.string.FilterFinishCreating));
            ViewGroup.MarginLayoutParams marginLayoutParams = new ViewGroup.MarginLayoutParams(-2, -2);
            marginLayoutParams.rightMargin = AndroidUtilities.dp(3.0f);
            getParentLayout().getOverlayContainerView().addView(this.saveHintView, marginLayoutParams);
            this.saveHintView.showForView(this.doneItem, true);
        }
    }

    private void updateRows() {
        updateRows(true);
    }

    private void updateRows(boolean z) {
        ArrayList arrayList;
        int i;
        this.oldItems.clear();
        this.oldItems.addAll(this.items);
        this.items.clear();
        this.items.add(new ItemInner(5, false));
        this.nameRow = this.items.size();
        this.items.add(ItemInner.asEdit());
        this.items.add(ItemInner.asShadow(null));
        this.items.add(ItemInner.asHeader(LocaleController.getString(R.string.FilterInclude)));
        this.items.add(ItemInner.asButton(R.drawable.msg2_chats_add, LocaleController.getString(R.string.FilterAddChats), false).whenClicked(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                FilterCreateActivity.this.lambda$updateRows$2(view);
            }
        }));
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0) {
            this.items.add(ItemInner.asChat(true, LocaleController.getString(R.string.FilterContacts), "contacts", MessagesController.DIALOG_FILTER_FLAG_CONTACTS));
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0) {
            this.items.add(ItemInner.asChat(true, LocaleController.getString(R.string.FilterNonContacts), "non_contacts", MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS));
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0) {
            this.items.add(ItemInner.asChat(true, LocaleController.getString(R.string.FilterGroups), "groups", MessagesController.DIALOG_FILTER_FLAG_GROUPS));
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0) {
            this.items.add(ItemInner.asChat(true, LocaleController.getString(R.string.FilterChannels), "channels", MessagesController.DIALOG_FILTER_FLAG_CHANNELS));
        }
        if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0) {
            this.items.add(ItemInner.asChat(true, LocaleController.getString(R.string.FilterBots), "bots", MessagesController.DIALOG_FILTER_FLAG_BOTS));
        }
        if (!this.newAlwaysShow.isEmpty()) {
            int size = (this.includeExpanded || this.newAlwaysShow.size() < 8) ? this.newAlwaysShow.size() : Math.min(5, this.newAlwaysShow.size());
            for (int i2 = 0; i2 < size; i2++) {
                this.items.add(ItemInner.asChat(true, ((Long) this.newAlwaysShow.get(i2)).longValue()));
            }
            if (size != this.newAlwaysShow.size()) {
                this.items.add(ItemInner.asButton(R.drawable.arrow_more, LocaleController.formatPluralString("FilterShowMoreChats", this.newAlwaysShow.size() - 5, new Object[0]), false).whenClicked(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        FilterCreateActivity.this.lambda$updateRows$3(view);
                    }
                }));
            }
        }
        this.items.add(ItemInner.asShadow(LocaleController.getString(R.string.FilterIncludeInfo)));
        if (!this.filter.isChatlist()) {
            this.items.add(ItemInner.asHeader(LocaleController.getString(R.string.FilterExclude)));
            this.items.add(ItemInner.asButton(R.drawable.msg2_chats_add, LocaleController.getString(R.string.FilterRemoveChats), false).whenClicked(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    FilterCreateActivity.this.lambda$updateRows$4(view);
                }
            }));
            if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0) {
                this.items.add(ItemInner.asChat(false, LocaleController.getString(R.string.FilterMuted), "muted", MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED));
            }
            if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
                this.items.add(ItemInner.asChat(false, LocaleController.getString(R.string.FilterRead), "read", MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ));
            }
            if ((this.newFilterFlags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) != 0) {
                this.items.add(ItemInner.asChat(false, LocaleController.getString(R.string.FilterArchived), "archived", MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED));
            }
            if (!this.newNeverShow.isEmpty()) {
                int size2 = (this.excludeExpanded || this.newNeverShow.size() < 8) ? this.newNeverShow.size() : Math.min(5, this.newNeverShow.size());
                for (int i3 = 0; i3 < size2; i3++) {
                    this.items.add(ItemInner.asChat(false, ((Long) this.newNeverShow.get(i3)).longValue()));
                }
                if (size2 != this.newNeverShow.size()) {
                    this.items.add(ItemInner.asButton(R.drawable.arrow_more, LocaleController.formatPluralString("FilterShowMoreChats", this.newNeverShow.size() - 5, new Object[0]), false).whenClicked(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            FilterCreateActivity.this.lambda$updateRows$5(view);
                        }
                    }));
                }
            }
            this.items.add(ItemInner.asShadow(LocaleController.getString(R.string.FilterExcludeInfo)));
        }
        if (getMessagesController().folderTags || !getUserConfig().isPremium()) {
            this.items.add(new ItemInner(9, false));
            this.items.add(new ItemInner(10, false));
            this.items.add(ItemInner.asShadow(LocaleController.getString(R.string.FolderTagColorInfo)));
        }
        if (this.invites.isEmpty()) {
            this.items.add(ItemInner.asHeader(LocaleController.getString(R.string.FilterShareFolder), true));
            this.items.add(ItemInner.asButton(R.drawable.msg2_link2, LocaleController.getString(R.string.FilterShareFolderButton), false));
            arrayList = this.items;
            i = R.string.FilterInviteLinksHintNew;
        } else {
            this.items.add(ItemInner.asHeader(LocaleController.getString(R.string.FilterInviteLinks), true));
            this.items.add(ItemInner.asCreateLink());
            for (int i4 = 0; i4 < this.invites.size(); i4++) {
                this.items.add(ItemInner.asLink((TL_chatlists.TL_exportedChatlistInvite) this.invites.get(i4)));
            }
            arrayList = this.items;
            MessagesController.DialogFilter dialogFilter = this.filter;
            i = (dialogFilter == null || !dialogFilter.isChatlist()) ? R.string.FilterInviteLinksHint : R.string.FilterInviteLinksHintNew;
        }
        arrayList.add(ItemInner.asShadow(LocaleController.getString(i)));
        if (!this.creatingNew) {
            this.items.add(ItemInner.asButton(0, LocaleController.getString(R.string.FilterDelete), true).whenClicked(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    FilterCreateActivity.this.deleteFolder(view);
                }
            }));
            this.items.add(ItemInner.asShadow(null));
        }
        ListAdapter listAdapter = this.adapter;
        if (listAdapter != null) {
            if (z) {
                listAdapter.setItems(this.oldItems, this.items);
            } else {
                listAdapter.notifyDataSetChanged();
            }
        }
    }

    public static CharSequence withNew(int i, CharSequence charSequence, boolean z) {
        Context context;
        if (i >= 0) {
            if (MessagesController.getGlobalMainSettings().getBoolean("n_" + i, false) || (context = ApplicationLoader.applicationContext) == null) {
                return charSequence;
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
            spannableStringBuilder.append((CharSequence) "  ");
            SpannableString spannableString = new SpannableString("NEW");
            if (z) {
                Drawable mutate = context.getResources().getDrawable(R.drawable.msg_other_new_outline).mutate();
                mutate.setBounds(0, -AndroidUtilities.dp(8.0f), mutate.getIntrinsicWidth(), mutate.getIntrinsicHeight() - AndroidUtilities.dp(8.0f));
                spannableString.setSpan(new ColorImageSpan(mutate, 0), 0, spannableString.length(), 33);
            } else {
                Drawable mutate2 = context.getResources().getDrawable(R.drawable.msg_other_new_filled).mutate();
                Drawable mutate3 = context.getResources().getDrawable(R.drawable.msg_other_new_filled_text).mutate();
                int color = Theme.getColor(Theme.key_featuredStickers_unread);
                PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
                mutate2.setColorFilter(new PorterDuffColorFilter(color, mode));
                mutate3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_featuredStickers_buttonText), mode));
                CombinedDrawable combinedDrawable = new CombinedDrawable(mutate2, mutate3);
                combinedDrawable.setBounds(0, 0, combinedDrawable.getIntrinsicWidth(), combinedDrawable.getIntrinsicHeight());
                spannableString.setSpan(new ImageSpan(combinedDrawable, 0), 0, spannableString.length(), 33);
            }
            spannableStringBuilder.append((CharSequence) spannableString);
            return spannableStringBuilder;
        }
        return charSequence;
    }

    @Override
    public boolean canBeginSlide() {
        return checkDiscard();
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        ActionBarMenu createMenu = this.actionBar.createMenu();
        if (this.creatingNew) {
            this.actionBar.setTitle(LocaleController.getString(R.string.FilterNew));
        } else {
            TextPaint textPaint = new TextPaint(1);
            textPaint.setTextSize(AndroidUtilities.dp(20.0f));
            this.actionBar.setTitle(Emoji.replaceEmoji((CharSequence) this.filter.name, textPaint.getFontMetricsInt(), AndroidUtilities.dp(20.0f), false));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            AnonymousClass1() {
            }

            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    if (FilterCreateActivity.this.checkDiscard()) {
                        FilterCreateActivity.this.lambda$onBackPressed$321();
                    }
                } else if (i == 1) {
                    FilterCreateActivity.this.processDone();
                }
            }
        });
        this.doneItem = createMenu.addItem(1, LocaleController.getString(R.string.Save).toUpperCase());
        FrameLayout frameLayout = new FrameLayout(context);
        this.fragmentView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        AnonymousClass2 anonymousClass2 = new RecyclerListView(context) {
            AnonymousClass2(Context context2) {
                super(context2);
            }

            @Override
            public Integer getSelectorColor(int i) {
                ItemInner itemInner = (i < 0 || i >= FilterCreateActivity.this.items.size()) ? null : (ItemInner) FilterCreateActivity.this.items.get(i);
                return Integer.valueOf((itemInner == null || !itemInner.isRed) ? getThemedColor(Theme.key_listSelector) : Theme.multAlpha(getThemedColor(Theme.key_text_RedRegular), 0.12f));
            }

            @Override
            public boolean requestFocus(int i, Rect rect) {
                return false;
            }
        };
        this.listView = anonymousClass2;
        anonymousClass2.setLayoutManager(new LinearLayoutManager(context2, 1, false));
        this.listView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        RecyclerListView recyclerListView = this.listView;
        ListAdapter listAdapter = new ListAdapter(context2);
        this.adapter = listAdapter;
        recyclerListView.setAdapter(listAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                FilterCreateActivity.this.lambda$createView$7(view, i);
            }
        });
        this.listView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() {
            @Override
            public final boolean onItemClick(View view, int i) {
                boolean lambda$createView$8;
                lambda$createView$8 = FilterCreateActivity.this.lambda$createView$8(view, i);
                return lambda$createView$8;
            }
        });
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setSupportsChangeAnimations(false);
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDurations(350L);
        this.listView.setItemAnimator(defaultItemAnimator);
        checkDoneButton(false);
        loadInvites();
        return this.fragmentView;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                FilterCreateActivity.this.lambda$getThemeDescriptions$27();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{HeaderCell.class, TextCell.class, PollEditTextCell.class, UserCell.class}, null, null, null, Theme.key_windowBackgroundWhite));
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
        int i3 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_text_RedRegular));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CHECKTAG, new Class[]{TextCell.class}, new String[]{"ImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_switchTrackChecked));
        int i4 = Theme.key_windowBackgroundGrayShadow;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ShadowSectionCell.class}, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{TextInfoPrivacyCell.class}, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{TextInfoPrivacyCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{UserCell.class}, new String[]{"adminTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_profile_creatorIcon));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i3));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteGrayText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, new String[]{"statusOnlineColor"}, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_windowBackgroundWhiteBlueText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{UserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
        return arrayList;
    }

    public void loadInvites() {
        MessagesController.DialogFilter dialogFilter;
        if (this.loadingInvites || (dialogFilter = this.filter) == null || !dialogFilter.isChatlist()) {
            return;
        }
        this.loadingInvites = true;
        TL_chatlists.TL_chatlists_getExportedInvites tL_chatlists_getExportedInvites = new TL_chatlists.TL_chatlists_getExportedInvites();
        TL_chatlists.TL_inputChatlistDialogFilter tL_inputChatlistDialogFilter = new TL_chatlists.TL_inputChatlistDialogFilter();
        tL_chatlists_getExportedInvites.chatlist = tL_inputChatlistDialogFilter;
        tL_inputChatlistDialogFilter.filter_id = this.filter.id;
        this.requestingInvitesReqId = getConnectionsManager().sendRequest(tL_chatlists_getExportedInvites, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                FilterCreateActivity.this.lambda$loadInvites$1(tLObject, tL_error);
            }
        });
    }

    @Override
    public boolean onBackPressed() {
        return checkDiscard();
    }

    @Override
    public boolean onFragmentCreate() {
        updateRows();
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        if (this.requestingInvitesReqId != 0) {
            getConnectionsManager().cancelRequest(this.requestingInvitesReqId, true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRows();
        Runnable runnable = this.showBulletinOnResume;
        if (runnable != null) {
            runnable.run();
        }
    }
}
