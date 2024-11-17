package org.telegram.ui.Cells;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.AppIconsSelectorCell;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.LauncherIconController;

public class AppIconsSelectorCell extends RecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    private List availableIcons;
    private int currentAccount;
    private LinearLayoutManager linearLayoutManager;

    public static class AdaptiveIconImageView extends ImageView {
        private int backgroundOuterPadding;
        private Drawable foreground;
        private int outerPadding;
        private Path path;

        public AdaptiveIconImageView(Context context) {
            super(context);
            this.path = new Path();
            this.outerPadding = AndroidUtilities.dp(5.0f);
            this.backgroundOuterPadding = AndroidUtilities.dp(42.0f);
        }

        private void updatePath() {
            this.path.rewind();
            this.path.addCircle(getWidth() / 2.0f, getHeight() / 2.0f, Math.min((getWidth() - getPaddingLeft()) - getPaddingRight(), (getHeight() - getPaddingTop()) - getPaddingBottom()) / 2.0f, Path.Direction.CW);
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.clipPath(this.path);
            canvas.scale((this.backgroundOuterPadding / getWidth()) + 1.0f, (this.backgroundOuterPadding / getHeight()) + 1.0f, getWidth() / 2.0f, getHeight() / 2.0f);
            super.draw(canvas);
            canvas.restore();
            Drawable drawable = this.foreground;
            if (drawable != null) {
                int i = -this.outerPadding;
                drawable.setBounds(i, i, getWidth() + this.outerPadding, getHeight() + this.outerPadding);
                this.foreground.draw(canvas);
            }
        }

        @Override
        protected void onSizeChanged(int i, int i2, int i3, int i4) {
            super.onSizeChanged(i, i2, i3, i4);
            updatePath();
        }

        public void setBackgroundOuterPadding(int i) {
            this.backgroundOuterPadding = i;
        }

        public void setForeground(int i) {
            this.foreground = ContextCompat.getDrawable(getContext(), i);
            invalidate();
        }

        public void setOuterPadding(int i) {
            this.outerPadding = i;
        }

        public void setPadding(int i) {
            setPadding(i, i, i, i);
        }
    }

    public static final class IconHolderView extends LinearLayout {
        private Paint fillPaint;
        private AdaptiveIconImageView iconView;
        private Paint outlinePaint;
        private float progress;
        private TextView titleView;

        private IconHolderView(Context context) {
            super(context);
            this.outlinePaint = new Paint(1);
            this.fillPaint = new Paint(1);
            setOrientation(1);
            setWillNotDraw(false);
            AdaptiveIconImageView adaptiveIconImageView = new AdaptiveIconImageView(context);
            this.iconView = adaptiveIconImageView;
            adaptiveIconImageView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            addView(this.iconView, LayoutHelper.createLinear(58, 58, 1));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setSingleLine();
            this.titleView.setTextSize(1, 13.0f);
            this.titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            addView(this.titleView, LayoutHelper.createLinear(-2, -2, 1, 0, 4, 0, 0));
            this.outlinePaint.setStyle(Paint.Style.STROKE);
            this.outlinePaint.setStrokeWidth(Math.max(2, AndroidUtilities.dp(0.5f)));
            this.fillPaint.setColor(-1);
        }

        public void bind(LauncherIconController.LauncherIcon launcherIcon) {
            String str;
            TextView textView;
            this.iconView.setImageResource(launcherIcon.background);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.titleView.getLayoutParams();
            if (!launcherIcon.premium || UserConfig.hasPremiumOnAccounts()) {
                marginLayoutParams.rightMargin = 0;
                textView = this.titleView;
                str = LocaleController.getString(launcherIcon.title);
            } else {
                SpannableString spannableString = new SpannableString("d " + LocaleController.getString(launcherIcon.title));
                ColoredImageSpan coloredImageSpan = new ColoredImageSpan(R.drawable.msg_mini_premiumlock);
                coloredImageSpan.setTopOffset(1);
                coloredImageSpan.setSize(AndroidUtilities.dp(13.0f));
                spannableString.setSpan(coloredImageSpan, 0, 1, 33);
                marginLayoutParams.rightMargin = AndroidUtilities.dp(4.0f);
                textView = this.titleView;
                str = spannableString;
            }
            textView.setText(str);
            setSelected(LauncherIconController.isEnabled(launcherIcon), false);
        }

        public void lambda$setSelected$0(ValueAnimator valueAnimator) {
            setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }

        private void setProgress(float f) {
            this.progress = f;
            TextView textView = this.titleView;
            int color = Theme.getColor(Theme.key_windowBackgroundWhiteBlackText);
            int i = Theme.key_windowBackgroundWhiteValueText;
            textView.setTextColor(ColorUtils.blendARGB(color, Theme.getColor(i), f));
            this.outlinePaint.setColor(ColorUtils.blendARGB(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_switchTrack), 63), Theme.getColor(i), f));
            this.outlinePaint.setStrokeWidth(Math.max(2, AndroidUtilities.dp(AndroidUtilities.lerp(0.5f, 2.0f, f))));
            invalidate();
        }

        public void setSelected(boolean z, boolean z2) {
            float f = z ? 1.0f : 0.0f;
            float f2 = this.progress;
            if (f == f2 && z2) {
                return;
            }
            if (!z2) {
                setProgress(f);
                return;
            }
            ValueAnimator duration = ValueAnimator.ofFloat(f2, f).setDuration(250L);
            duration.setInterpolator(Easings.easeInOutQuad);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    AppIconsSelectorCell.IconHolderView.this.lambda$setSelected$0(valueAnimator);
                }
            });
            duration.start();
        }

        @Override
        public void draw(Canvas canvas) {
            float strokeWidth = this.outlinePaint.getStrokeWidth();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(this.iconView.getLeft() + strokeWidth, this.iconView.getTop() + strokeWidth, this.iconView.getRight() - strokeWidth, this.iconView.getBottom() - strokeWidth);
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), this.fillPaint);
            super.draw(canvas);
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f), this.outlinePaint);
        }
    }

    public AppIconsSelectorCell(final Context context, final BaseFragment baseFragment, int i) {
        super(context);
        this.availableIcons = new ArrayList();
        this.currentAccount = i;
        setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
        setFocusable(false);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        setItemAnimator(null);
        setLayoutAnimation(null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 0, false);
        this.linearLayoutManager = linearLayoutManager;
        setLayoutManager(linearLayoutManager);
        setAdapter(new RecyclerView.Adapter() {
            @Override
            public int getItemCount() {
                return AppIconsSelectorCell.this.availableIcons.size();
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i2) {
                IconHolderView iconHolderView = (IconHolderView) viewHolder.itemView;
                LauncherIconController.LauncherIcon launcherIcon = (LauncherIconController.LauncherIcon) AppIconsSelectorCell.this.availableIcons.get(i2);
                iconHolderView.bind(launcherIcon);
                iconHolderView.iconView.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(18.0f), 0, Theme.getColor(Theme.key_listSelector), -16777216));
                iconHolderView.iconView.setForeground(launcherIcon.foreground);
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i2) {
                return new RecyclerListView.Holder(new IconHolderView(viewGroup.getContext()));
            }
        });
        addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                int dp;
                int adapterPosition = recyclerView.getChildViewHolder(view).getAdapterPosition();
                if (adapterPosition == 0) {
                    rect.left = AndroidUtilities.dp(18.0f);
                }
                if (adapterPosition == AppIconsSelectorCell.this.getAdapter().getItemCount() - 1) {
                    dp = AndroidUtilities.dp(18.0f);
                } else {
                    int itemCount = AppIconsSelectorCell.this.getAdapter().getItemCount();
                    if (itemCount == 4) {
                        rect.right = ((AppIconsSelectorCell.this.getWidth() - AndroidUtilities.dp(36.0f)) - (AndroidUtilities.dp(58.0f) * itemCount)) / (itemCount - 1);
                        return;
                    }
                    dp = AndroidUtilities.dp(24.0f);
                }
                rect.right = dp;
            }
        });
        setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i2) {
                AppIconsSelectorCell.this.lambda$new$0(baseFragment, context, view, i2);
            }
        });
        updateIconsVisibility();
    }

    public void lambda$new$0(BaseFragment baseFragment, Context context, View view, int i) {
        IconHolderView iconHolderView = (IconHolderView) view;
        LauncherIconController.LauncherIcon launcherIcon = (LauncherIconController.LauncherIcon) this.availableIcons.get(i);
        if (launcherIcon.premium && !UserConfig.hasPremiumOnAccounts()) {
            baseFragment.showDialog(new PremiumFeatureBottomSheet(baseFragment, 10, true));
            return;
        }
        if (LauncherIconController.isEnabled(launcherIcon)) {
            return;
        }
        LinearSmoothScroller linearSmoothScroller = new LinearSmoothScroller(context) {
            @Override
            public int calculateDtToFit(int i2, int i3, int i4, int i5, int i6) {
                return (i4 - i2) + AndroidUtilities.dp(16.0f);
            }

            @Override
            public float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return super.calculateSpeedPerPixel(displayMetrics) * 3.0f;
            }
        };
        linearSmoothScroller.setTargetPosition(i);
        this.linearLayoutManager.startSmoothScroll(linearSmoothScroller);
        LauncherIconController.setIcon(launcherIcon);
        iconHolderView.setSelected(true, true);
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            IconHolderView iconHolderView2 = (IconHolderView) getChildAt(i2);
            if (iconHolderView2 != iconHolderView) {
                iconHolderView2.setSelected(false, true);
            }
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 5, launcherIcon);
    }

    private void updateIconsVisibility() {
        this.availableIcons.clear();
        this.availableIcons.addAll(Arrays.asList(LauncherIconController.LauncherIcon.values()));
        if (MessagesController.getInstance(this.currentAccount).premiumFeaturesBlocked()) {
            int i = 0;
            while (i < this.availableIcons.size()) {
                if (((LauncherIconController.LauncherIcon) this.availableIcons.get(i)).premium) {
                    this.availableIcons.remove(i);
                    i--;
                }
                i++;
            }
        }
        getAdapter().notifyDataSetChanged();
        invalidateItemDecorations();
        for (int i2 = 0; i2 < this.availableIcons.size(); i2++) {
            if (LauncherIconController.isEnabled((LauncherIconController.LauncherIcon) this.availableIcons.get(i2))) {
                this.linearLayoutManager.scrollToPositionWithOffset(i2, AndroidUtilities.dp(16.0f));
                return;
            }
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.premiumStatusChangedGlobal) {
            updateIconsVisibility();
        }
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.premiumStatusChangedGlobal);
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.premiumStatusChangedGlobal);
    }

    @Override
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), i2);
    }

    @Override
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        invalidateItemDecorations();
    }
}
