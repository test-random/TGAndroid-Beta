package org.telegram.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AvailableReactionCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.ThemePreviewMessagesCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.SelectAnimatedEmojiDialog;

public class ReactionsDoubleTapManageActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private LinearLayout contentView;
    int infoRow;
    private RecyclerView.Adapter listAdapter;
    private RecyclerListView listView;
    int premiumReactionRow;
    int previewRow;
    int reactionsStartRow = -1;
    int rowCount;
    private SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;

    public class SetDefaultReactionCell extends FrameLayout {
        private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable imageDrawable;
        private TextView textView;

        public SetDefaultReactionCell(Context context) {
            super(context);
            setBackgroundColor(ReactionsDoubleTapManageActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextSize(1, 16.0f);
            this.textView.setTextColor(ReactionsDoubleTapManageActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            this.textView.setText(LocaleController.getString(R.string.DoubleTapSetting));
            addView(this.textView, LayoutHelper.createFrame(-1, -2.0f, 23, 20.0f, 0.0f, 48.0f, 0.0f));
            this.imageDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, AndroidUtilities.dp(24.0f));
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            updateImageBounds();
            this.imageDrawable.draw(canvas);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.imageDrawable.attach();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.imageDrawable.detach();
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(50.0f), 1073741824));
        }

        public void update(boolean z) {
            String doubleTapReaction = MediaDataController.getInstance(((BaseFragment) ReactionsDoubleTapManageActivity.this).currentAccount).getDoubleTapReaction();
            if (doubleTapReaction != null && doubleTapReaction.startsWith("animated_")) {
                try {
                    this.imageDrawable.set(Long.parseLong(doubleTapReaction.substring(9)), z);
                    return;
                } catch (Exception unused) {
                }
            }
            TLRPC.TL_availableReaction tL_availableReaction = MediaDataController.getInstance(((BaseFragment) ReactionsDoubleTapManageActivity.this).currentAccount).getReactionsMap().get(doubleTapReaction);
            if (tL_availableReaction != null) {
                this.imageDrawable.set(tL_availableReaction.static_icon, z);
            }
        }

        public void updateImageBounds() {
            this.imageDrawable.setBounds((getWidth() - this.imageDrawable.getIntrinsicWidth()) - AndroidUtilities.dp(21.0f), (getHeight() - this.imageDrawable.getIntrinsicHeight()) / 2, getWidth() - AndroidUtilities.dp(21.0f), (getHeight() + this.imageDrawable.getIntrinsicHeight()) / 2);
        }
    }

    public List getAvailableReactions() {
        return getMediaDataController().getReactionsList();
    }

    public void lambda$createView$0(View view, int i) {
        if (!(view instanceof AvailableReactionCell)) {
            if (view instanceof SetDefaultReactionCell) {
                showSelectStatusDialog((SetDefaultReactionCell) view);
                return;
            }
            return;
        }
        AvailableReactionCell availableReactionCell = (AvailableReactionCell) view;
        if (availableReactionCell.locked && !getUserConfig().isPremium()) {
            showDialog(new PremiumFeatureBottomSheet(this, 4, true));
        } else {
            MediaDataController.getInstance(this.currentAccount).setDoubleTapReaction(availableReactionCell.react.reaction);
            this.listView.getAdapter().notifyItemRangeChanged(0, this.listView.getAdapter().getItemCount());
        }
    }

    public void updateColors() {
        this.contentView.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.listAdapter.notifyDataSetChanged();
    }

    private void updateRows() {
        this.previewRow = 0;
        this.rowCount = 2;
        this.infoRow = 1;
        if (!UserConfig.getInstance(this.currentAccount).isPremium()) {
            this.premiumReactionRow = -1;
            this.reactionsStartRow = this.rowCount;
        } else {
            this.reactionsStartRow = -1;
            int i = this.rowCount;
            this.rowCount = i + 1;
            this.premiumReactionRow = i;
        }
    }

    @Override
    public View createView(final Context context) {
        this.actionBar.setTitle(LocaleController.getString(R.string.Reactions));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    ReactionsDoubleTapManageActivity.this.lambda$onBackPressed$319();
                }
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        RecyclerListView recyclerListView = new RecyclerListView(context);
        this.listView = recyclerListView;
        ((DefaultItemAnimator) recyclerListView.getItemAnimator()).setSupportsChangeAnimations(false);
        this.listView.setLayoutManager(new LinearLayoutManager(context));
        RecyclerListView recyclerListView2 = this.listView;
        RecyclerListView.SelectionAdapter selectionAdapter = new RecyclerListView.SelectionAdapter() {
            @Override
            public int getItemCount() {
                ReactionsDoubleTapManageActivity reactionsDoubleTapManageActivity = ReactionsDoubleTapManageActivity.this;
                return reactionsDoubleTapManageActivity.rowCount + (reactionsDoubleTapManageActivity.premiumReactionRow < 0 ? reactionsDoubleTapManageActivity.getAvailableReactions().size() : 0) + 1;
            }

            @Override
            public int getItemViewType(int i) {
                ReactionsDoubleTapManageActivity reactionsDoubleTapManageActivity = ReactionsDoubleTapManageActivity.this;
                if (i == reactionsDoubleTapManageActivity.previewRow) {
                    return 0;
                }
                if (i == reactionsDoubleTapManageActivity.infoRow) {
                    return 2;
                }
                if (i == reactionsDoubleTapManageActivity.premiumReactionRow) {
                    return 3;
                }
                return i == getItemCount() - 1 ? 4 : 1;
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return viewHolder.getItemViewType() == 3 || viewHolder.getItemViewType() == 2;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                if (getItemViewType(i) != 1) {
                    return;
                }
                AvailableReactionCell availableReactionCell = (AvailableReactionCell) viewHolder.itemView;
                TLRPC.TL_availableReaction tL_availableReaction = (TLRPC.TL_availableReaction) ReactionsDoubleTapManageActivity.this.getAvailableReactions().get(i - ReactionsDoubleTapManageActivity.this.reactionsStartRow);
                availableReactionCell.bind(tL_availableReaction, tL_availableReaction.reaction.contains(MediaDataController.getInstance(((BaseFragment) ReactionsDoubleTapManageActivity.this).currentAccount).getDoubleTapReaction()), ((BaseFragment) ReactionsDoubleTapManageActivity.this).currentAccount);
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                View view;
                Context context2;
                int i2;
                View view2;
                if (i != 0) {
                    if (i == 2) {
                        TextInfoPrivacyCell textInfoPrivacyCell = new TextInfoPrivacyCell(context);
                        textInfoPrivacyCell.setText(LocaleController.getString(R.string.DoubleTapPreviewRational));
                        context2 = context;
                        i2 = R.drawable.greydivider;
                        view2 = textInfoPrivacyCell;
                    } else if (i == 3) {
                        SetDefaultReactionCell setDefaultReactionCell = new SetDefaultReactionCell(context);
                        setDefaultReactionCell.update(false);
                        view = setDefaultReactionCell;
                    } else if (i != 4) {
                        view = new AvailableReactionCell(context, true, true);
                    } else {
                        View view3 = new View(context) {
                            @Override
                            protected void onMeasure(int i3, int i4) {
                                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i3), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(16.0f), 1073741824));
                            }
                        };
                        context2 = context;
                        i2 = R.drawable.greydivider_bottom;
                        view2 = view3;
                    }
                    view2.setBackground(Theme.getThemedDrawableByKey(context2, i2, Theme.key_windowBackgroundGrayShadow));
                    view = view2;
                } else {
                    ThemePreviewMessagesCell themePreviewMessagesCell = new ThemePreviewMessagesCell(context, ((BaseFragment) ReactionsDoubleTapManageActivity.this).parentLayout, 2);
                    themePreviewMessagesCell.setImportantForAccessibility(4);
                    themePreviewMessagesCell.fragment = ReactionsDoubleTapManageActivity.this;
                    view = themePreviewMessagesCell;
                }
                return new RecyclerListView.Holder(view);
            }
        };
        this.listAdapter = selectionAdapter;
        recyclerListView2.setAdapter(selectionAdapter);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                ReactionsDoubleTapManageActivity.this.lambda$createView$0(view, i);
            }
        });
        linearLayout.addView(this.listView, LayoutHelper.createLinear(-1, -1));
        this.contentView = linearLayout;
        this.fragmentView = linearLayout;
        updateColors();
        updateRows();
        return this.contentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i2 != this.currentAccount) {
            return;
        }
        if (i != NotificationCenter.reactionsDidLoad) {
            if (i != NotificationCenter.currentUserPremiumStatusChanged) {
                return;
            } else {
                updateRows();
            }
        }
        this.listAdapter.notifyDataSetChanged();
    }

    @Override
    public ArrayList getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                ReactionsDoubleTapManageActivity.this.updateColors();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, Theme.key_windowBackgroundWhite, Theme.key_windowBackgroundWhiteBlackText, Theme.key_windowBackgroundWhiteGrayText2, Theme.key_listSelector, Theme.key_windowBackgroundGray, Theme.key_windowBackgroundWhiteGrayText4, Theme.key_text_RedRegular, Theme.key_windowBackgroundChecked, Theme.key_windowBackgroundCheckText, Theme.key_switchTrackBlue, Theme.key_switchTrackBlueChecked, Theme.key_switchTrackBlueThumb, Theme.key_switchTrackBlueThumbChecked);
    }

    @Override
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.reactionsDidLoad);
        getNotificationCenter().addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        getNotificationCenter().removeObserver(this, NotificationCenter.reactionsDidLoad);
        getNotificationCenter().removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
    }

    public void showSelectStatusDialog(final org.telegram.ui.ReactionsDoubleTapManageActivity.SetDefaultReactionCell r18) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ReactionsDoubleTapManageActivity.showSelectStatusDialog(org.telegram.ui.ReactionsDoubleTapManageActivity$SetDefaultReactionCell):void");
    }
}
