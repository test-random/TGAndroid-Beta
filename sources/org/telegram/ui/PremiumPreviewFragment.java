package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
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
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Business.AwayMessagesActivity;
import org.telegram.ui.Business.BusinessChatbotController;
import org.telegram.ui.Business.BusinessIntroActivity;
import org.telegram.ui.Business.BusinessLinksActivity;
import org.telegram.ui.Business.BusinessLinksController;
import org.telegram.ui.Business.ChatbotsActivity;
import org.telegram.ui.Business.GreetMessagesActivity;
import org.telegram.ui.Business.OpeningHoursActivity;
import org.telegram.ui.Business.QuickRepliesActivity;
import org.telegram.ui.Business.QuickRepliesController;
import org.telegram.ui.Business.TimezonesController;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FillLastLinearLayoutManager;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MediaActivity;
import org.telegram.ui.Components.Premium.AboutPremiumView;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.Premium.PremiumNotAvailableBottomSheet;
import org.telegram.ui.Components.Premium.PremiumTierCell;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.FilterCreateActivity;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.SelectAnimatedEmojiDialog;

public class PremiumPreviewFragment extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    BackgroundView backgroundView;
    private FrameLayout buttonContainer;
    private View buttonDivider;
    private FrameLayout contentView;
    SubscriptionTier currentSubscriptionTier;
    private int currentYOffset;
    PremiumFeatureCell dummyCell;
    PremiumTierCell dummyTierCell;
    int featuresEndRow;
    int featuresStartRow;
    private int firstViewHeight;
    private boolean forcePremium;
    final Canvas gradientCanvas;
    Paint gradientPaint;
    final Bitmap gradientTextureBitmap;
    PremiumGradient.PremiumGradientTools gradientTools;
    int helpUsRow;
    boolean inc;
    private boolean isDialogVisible;
    boolean isLandscapeMode;
    int lastPaddingRow;
    FillLastLinearLayoutManager layoutManager;
    RecyclerListView listView;
    Matrix matrix;
    int moreFeaturesEndRow;
    int moreFeaturesStartRow;
    int moreHeaderRow;
    ArrayList morePremiumFeatures;
    int paddingRow;
    StarParticlesView particlesView;
    private PremiumButtonView premiumButtonView;
    ArrayList premiumFeatures;
    int privacyRow;
    float progress;
    float progressToFull;
    int rowCount;
    int sectionRow;
    private SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;
    private boolean selectAnnualByDefault;
    int selectedTierIndex;
    FrameLayout settingsView;
    Shader shader;
    Drawable shadowDrawable;
    int showAdsHeaderRow;
    int showAdsInfoRow;
    int showAdsRow;
    private String source;
    private int statusBarHeight;
    int statusRow;
    ArrayList subscriptionTiers;
    PremiumGradient.PremiumGradientTools tiersGradientTools;
    int totalGradientHeight;
    float totalProgress;
    int totalTiersGradientHeight;
    private final int type;
    private boolean whiteBackground;

    public class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        public void lambda$onBindViewHolder$0() {
            PremiumPreviewFragment.this.showDialog(new RevenueSharingAdsInfoBottomSheet(PremiumPreviewFragment.this.getContext(), false, PremiumPreviewFragment.this.getResourceProvider(), null));
        }

        @Override
        public int getItemCount() {
            return PremiumPreviewFragment.this.rowCount;
        }

        @Override
        public int getItemViewType(int i) {
            PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
            if (i == premiumPreviewFragment.paddingRow) {
                return 0;
            }
            if (i >= premiumPreviewFragment.featuresStartRow && i < premiumPreviewFragment.featuresEndRow) {
                return 1;
            }
            if (i >= premiumPreviewFragment.moreFeaturesStartRow && i < premiumPreviewFragment.moreFeaturesEndRow) {
                return 1;
            }
            if (i == premiumPreviewFragment.helpUsRow) {
                return 4;
            }
            if (i == premiumPreviewFragment.sectionRow || i == premiumPreviewFragment.statusRow || i == premiumPreviewFragment.privacyRow || i == premiumPreviewFragment.showAdsInfoRow) {
                return 5;
            }
            if (i == premiumPreviewFragment.lastPaddingRow) {
                return 6;
            }
            if (i == premiumPreviewFragment.moreHeaderRow || i == premiumPreviewFragment.showAdsHeaderRow) {
                return 7;
            }
            return i == premiumPreviewFragment.showAdsRow ? 8 : 0;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return viewHolder.getItemViewType() == 1 || viewHolder.getItemViewType() == 8;
        }

        @Override
        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r18, int r19) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PremiumPreviewFragment.Adapter.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view;
            Context context = viewGroup.getContext();
            switch (i) {
                case 1:
                    view = new PremiumFeatureCell(context) {
                        @Override
                        protected void dispatchDraw(Canvas canvas) {
                            RectF rectF = AndroidUtilities.rectTmp;
                            rectF.set(this.imageView.getLeft(), this.imageView.getTop(), this.imageView.getRight(), this.imageView.getBottom());
                            PremiumPreviewFragment.this.matrix.reset();
                            PremiumPreviewFragment.this.matrix.postScale(1.0f, r1.totalGradientHeight / 100.0f, 0.0f, 0.0f);
                            PremiumPreviewFragment.this.matrix.postTranslate(0.0f, -this.data.yOffset);
                            PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                            premiumPreviewFragment.shader.setLocalMatrix(premiumPreviewFragment.matrix);
                            canvas.drawRoundRect(rectF, AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), PremiumPreviewFragment.this.gradientPaint);
                            super.dispatchDraw(canvas);
                        }
                    };
                    break;
                case 2:
                    int i2 = Theme.key_windowBackgroundGray;
                    view = new ShadowSectionCell(context, 12, Theme.getColor(i2));
                    CombinedDrawable combinedDrawable = new CombinedDrawable(new ColorDrawable(Theme.getColor(i2)), Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.getColor(Theme.key_windowBackgroundGrayShadow)), 0, 0);
                    combinedDrawable.setFullsize(true);
                    view.setBackgroundDrawable(combinedDrawable);
                    break;
                case 3:
                default:
                    view = new View(context) {
                        @Override
                        protected void onMeasure(int i3, int i4) {
                            PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                            if (premiumPreviewFragment.isLandscapeMode) {
                                premiumPreviewFragment.firstViewHeight = (premiumPreviewFragment.statusBarHeight + ((BaseFragment) PremiumPreviewFragment.this).actionBar.getMeasuredHeight()) - AndroidUtilities.dp(16.0f);
                            } else {
                                int dp = AndroidUtilities.dp(80.0f) + PremiumPreviewFragment.this.statusBarHeight;
                                if (PremiumPreviewFragment.this.backgroundView.getMeasuredHeight() + AndroidUtilities.dp(24.0f) > dp) {
                                    dp = PremiumPreviewFragment.this.backgroundView.getMeasuredHeight() + AndroidUtilities.dp(24.0f);
                                }
                                PremiumPreviewFragment.this.firstViewHeight = dp;
                            }
                            super.onMeasure(i3, View.MeasureSpec.makeMeasureSpec(PremiumPreviewFragment.this.firstViewHeight, 1073741824));
                        }
                    };
                    break;
                case 4:
                    view = new AboutPremiumView(context);
                    break;
                case 5:
                    view = new TextInfoPrivacyCell(context);
                    break;
                case 6:
                    view = new View(context);
                    view.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
                    break;
                case 7:
                    view = new HeaderCell(context);
                    break;
                case 8:
                    view = new TextCell(context, 23, false, true, ((BaseFragment) PremiumPreviewFragment.this).resourceProvider);
                    break;
            }
            view.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(view);
        }
    }

    public class BackgroundView extends LinearLayout {
        private final FrameLayout imageFrameLayout;
        private final GLIconTextureView imageView;
        private boolean setTierListViewVisibility;
        private final TextView subtitleView;
        private RecyclerListView tierListView;
        private boolean tierListViewVisible;
        TextView titleView;

        public class AnonymousClass3 extends RecyclerListView.SelectionAdapter {
            final Context val$context;
            final PremiumPreviewFragment val$this$0;

            AnonymousClass3(PremiumPreviewFragment premiumPreviewFragment, Context context) {
                this.val$this$0 = premiumPreviewFragment;
                this.val$context = context;
            }

            public Paint lambda$onCreateViewHolder$0(PremiumTierCell premiumTierCell, Void r9) {
                PremiumPreviewFragment.this.tiersGradientTools.gradientMatrix(0, 0, premiumTierCell.getMeasuredWidth(), PremiumPreviewFragment.this.totalTiersGradientHeight, 0.0f, -premiumTierCell.getTier().yOffset);
                return PremiumPreviewFragment.this.tiersGradientTools.paint;
            }

            @Override
            public int getItemCount() {
                return PremiumPreviewFragment.this.subscriptionTiers.size();
            }

            @Override
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return !((SubscriptionTier) PremiumPreviewFragment.this.subscriptionTiers.get(viewHolder.getAdapterPosition())).subscriptionOption.current;
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                PremiumTierCell premiumTierCell = (PremiumTierCell) viewHolder.itemView;
                premiumTierCell.bind((SubscriptionTier) PremiumPreviewFragment.this.subscriptionTiers.get(i), i != getItemCount() - 1);
                premiumTierCell.setChecked(PremiumPreviewFragment.this.selectedTierIndex == i, false);
            }

            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                final PremiumTierCell premiumTierCell = new PremiumTierCell(this.val$context) {
                    @Override
                    protected void dispatchDraw(Canvas canvas) {
                        if (this.discountView.getVisibility() == 0) {
                            RectF rectF = AndroidUtilities.rectTmp;
                            rectF.set(this.discountView.getLeft(), this.discountView.getTop(), this.discountView.getRight(), this.discountView.getBottom());
                            PremiumPreviewFragment.this.tiersGradientTools.gradientMatrix(0, 0, getMeasuredWidth(), PremiumPreviewFragment.this.totalTiersGradientHeight, 0.0f, -this.tier.yOffset);
                            canvas.drawRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), PremiumPreviewFragment.this.tiersGradientTools.paint);
                        }
                        super.dispatchDraw(canvas);
                    }
                };
                premiumTierCell.setCirclePaintProvider(new GenericProvider() {
                    @Override
                    public final Object provide(Object obj) {
                        Paint lambda$onCreateViewHolder$0;
                        lambda$onCreateViewHolder$0 = PremiumPreviewFragment.BackgroundView.AnonymousClass3.this.lambda$onCreateViewHolder$0(premiumTierCell, (Void) obj);
                        return lambda$onCreateViewHolder$0;
                    }
                });
                return new RecyclerListView.Holder(premiumTierCell);
            }
        }

        public BackgroundView(final Context context) {
            super(context);
            setOrientation(1);
            FrameLayout frameLayout = new FrameLayout(context);
            this.imageFrameLayout = frameLayout;
            int i = PremiumPreviewFragment.this.type == 1 ? 175 : 190;
            addView(frameLayout, LayoutHelper.createLinear(i, i, 1));
            GLIconTextureView gLIconTextureView = new GLIconTextureView(context, PremiumPreviewFragment.this.whiteBackground ? 1 : 0, PremiumPreviewFragment.this.type == 1 ? 1 : 0) {
                @Override
                public void onLongPress() {
                    super.onLongPress();
                    PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                    if (premiumPreviewFragment.settingsView == null && BuildVars.DEBUG_PRIVATE_VERSION) {
                        premiumPreviewFragment.settingsView = new FrameLayout(context);
                        ScrollView scrollView = new ScrollView(context);
                        scrollView.addView(new GLIconSettingsView(context, BackgroundView.this.imageView.mRenderer));
                        PremiumPreviewFragment.this.settingsView.addView(scrollView);
                        PremiumPreviewFragment.this.settingsView.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
                        PremiumPreviewFragment.this.contentView.addView(PremiumPreviewFragment.this.settingsView, LayoutHelper.createFrame(-1, -1, 80));
                        ((ViewGroup.MarginLayoutParams) PremiumPreviewFragment.this.settingsView.getLayoutParams()).topMargin = PremiumPreviewFragment.this.currentYOffset;
                        PremiumPreviewFragment.this.settingsView.setTranslationY(AndroidUtilities.dp(1000.0f));
                        PremiumPreviewFragment.this.settingsView.animate().translationY(1.0f).setDuration(300L);
                    }
                }
            };
            this.imageView = gLIconTextureView;
            frameLayout.addView(gLIconTextureView, LayoutHelper.createFrame(-1, -1.0f));
            frameLayout.setClipChildren(false);
            setClipChildren(false);
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTextSize(1, 22.0f);
            this.titleView.setTypeface(AndroidUtilities.bold());
            this.titleView.setGravity(1);
            addView(this.titleView, LayoutHelper.createLinear(-2, -2, 0.0f, 1, 16, PremiumPreviewFragment.this.type == 1 ? 8 : 20, 16, 0));
            TextView textView2 = new TextView(context);
            this.subtitleView = textView2;
            textView2.setTextSize(1, 14.0f);
            textView2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            textView2.setGravity(1);
            addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 1, 16, 7, 16, 0));
            RecyclerListView recyclerListView = new RecyclerListView(context) {
                Paint paint;
                private Path path;

                {
                    Paint paint = new Paint(1);
                    this.paint = paint;
                    paint.setColor(Theme.getColor(Theme.key_dialogBackground));
                    if (PremiumPreviewFragment.this.whiteBackground) {
                        this.paint.setShadowLayer(AndroidUtilities.dp(2.0f), 0.0f, AndroidUtilities.dp(0.66f), 805306368);
                    }
                    this.path = new Path();
                }

                @Override
                public void dispatchDraw(Canvas canvas) {
                    this.path.rewind();
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(0.0f, 0.0f, getWidth(), getHeight());
                    this.path.addRoundRect(rectF, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), Path.Direction.CW);
                    canvas.drawPath(this.path, this.paint);
                    canvas.save();
                    canvas.clipPath(this.path);
                    super.dispatchDraw(canvas);
                    canvas.restore();
                }

                @Override
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    if (PremiumPreviewFragment.this.progressToFull >= 1.0f) {
                        return false;
                    }
                    return super.dispatchTouchEvent(motionEvent);
                }

                @Override
                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    if (PremiumPreviewFragment.this.progressToFull >= 1.0f) {
                        return false;
                    }
                    return super.onInterceptTouchEvent(motionEvent);
                }

                @Override
                public void onSizeChanged(int i2, int i3, int i4, int i5) {
                    super.onSizeChanged(i2, i3, i4, i5);
                    BackgroundView.this.measureGradient(i2, i3);
                }
            };
            this.tierListView = recyclerListView;
            recyclerListView.setOverScrollMode(2);
            this.tierListView.setLayoutManager(new LinearLayoutManager(context));
            this.tierListView.setAdapter(new AnonymousClass3(PremiumPreviewFragment.this, context));
            this.tierListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                @Override
                public final void onItemClick(View view, int i2) {
                    PremiumPreviewFragment.BackgroundView.this.lambda$new$0(view, i2);
                }
            });
            final Path path = new Path();
            final float[] fArr = new float[8];
            this.tierListView.setSelectorTransformer(new Consumer() {
                @Override
                public final void accept(Object obj) {
                    PremiumPreviewFragment.BackgroundView.this.lambda$new$1(path, fArr, (Canvas) obj);
                }
            });
            setClipChildren(false);
            setClipToPadding(false);
            addView(this.tierListView, LayoutHelper.createLinear(-1, -2, 12.0f, 16.0f, 12.0f, 4.0f));
            updatePremiumTiers();
            updateText();
        }

        public void lambda$new$0(android.view.View r6, int r7) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PremiumPreviewFragment.BackgroundView.lambda$new$0(android.view.View, int):void");
        }

        public void lambda$new$1(Path path, float[] fArr, Canvas canvas) {
            View pressedChildView = this.tierListView.getPressedChildView();
            int adapterPosition = pressedChildView == null ? -1 : this.tierListView.getChildViewHolder(pressedChildView).getAdapterPosition();
            path.rewind();
            Rect selectorRect = this.tierListView.getSelectorRect();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(selectorRect.left, selectorRect.top, selectorRect.right, selectorRect.bottom);
            Arrays.fill(fArr, 0.0f);
            if (adapterPosition == 0) {
                Arrays.fill(fArr, 0, 4, AndroidUtilities.dp(12.0f));
            }
            if (adapterPosition == this.tierListView.getAdapter().getItemCount() - 1) {
                Arrays.fill(fArr, 4, 8, AndroidUtilities.dp(12.0f));
            }
            path.addRoundRect(rectF, fArr, Path.Direction.CW);
            canvas.clipPath(path);
        }

        public void lambda$updateText$2(View view, ValueAnimator valueAnimator, ValueAnimator valueAnimator2) {
            float floatValue = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
            view.setAlpha(floatValue);
            view.setScaleX(floatValue);
            view.setScaleY(floatValue);
            float animatedFraction = valueAnimator.getAnimatedFraction();
            for (int i = 0; i < PremiumPreviewFragment.this.backgroundView.getChildCount(); i++) {
                View childAt = PremiumPreviewFragment.this.backgroundView.getChildAt(i);
                if (childAt != this.tierListView) {
                    childAt.setTranslationY((view.getMeasuredHeight() * animatedFraction) + (childAt == this.imageFrameLayout ? 0.0f - (AndroidUtilities.dp(15.0f) * animatedFraction) : 0.0f + (AndroidUtilities.dp(8.0f) * animatedFraction)));
                }
            }
        }

        public void measureGradient(int i, int i2) {
            int i3 = 0;
            for (int i4 = 0; i4 < PremiumPreviewFragment.this.subscriptionTiers.size(); i4++) {
                PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                premiumPreviewFragment.dummyTierCell.bind((SubscriptionTier) premiumPreviewFragment.subscriptionTiers.get(i4), false);
                PremiumPreviewFragment.this.dummyTierCell.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
                ((SubscriptionTier) PremiumPreviewFragment.this.subscriptionTiers.get(i4)).yOffset = i3;
                i3 += PremiumPreviewFragment.this.dummyTierCell.getMeasuredHeight();
            }
            PremiumPreviewFragment.this.totalTiersGradientHeight = i3;
        }

        public void updatePremiumTiers() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PremiumPreviewFragment.BackgroundView.updatePremiumTiers():void");
        }

        public void updateText() {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PremiumPreviewFragment.BackgroundView.updateText():void");
        }
    }

    public static class PremiumFeatureData {
        public final String description;
        public final int icon;
        public final CharSequence title;
        public final int type;
        public int yOffset;

        public PremiumFeatureData(int i, int i2, CharSequence charSequence, String str) {
            this.type = i;
            this.icon = i2;
            this.title = charSequence;
            this.description = str;
        }
    }

    public static final class SubscriptionTier {
        private int discount;
        private ProductDetails googlePlayProductDetails;
        private ProductDetails.SubscriptionOfferDetails offerDetails;
        private long pricePerMonth;
        private long pricePerYear;
        private long pricePerYearRegular;
        public final TLRPC.TL_premiumSubscriptionOption subscriptionOption;
        public int yOffset;

        public SubscriptionTier(TLRPC.TL_premiumSubscriptionOption tL_premiumSubscriptionOption) {
            this.subscriptionOption = tL_premiumSubscriptionOption;
        }

        private void checkOfferDetails() {
            ProductDetails productDetails = this.googlePlayProductDetails;
            if (productDetails != null && this.offerDetails == null) {
                for (ProductDetails.SubscriptionOfferDetails subscriptionOfferDetails : productDetails.getSubscriptionOfferDetails()) {
                    String billingPeriod = ((ProductDetails.PricingPhase) subscriptionOfferDetails.getPricingPhases().getPricingPhaseList().get(0)).getBillingPeriod();
                    if (getMonths() == 12) {
                        if (billingPeriod.equals("P1Y")) {
                            this.offerDetails = subscriptionOfferDetails;
                            return;
                        }
                    } else if (billingPeriod.equals(String.format(Locale.ROOT, "P%dM", Integer.valueOf(getMonths())))) {
                        this.offerDetails = subscriptionOfferDetails;
                        return;
                    }
                }
            }
        }

        public String getCurrency() {
            if (BuildVars.useInvoiceBilling() || this.subscriptionOption.store_product == null) {
                return this.subscriptionOption.currency;
            }
            if (this.googlePlayProductDetails == null) {
                return "";
            }
            checkOfferDetails();
            ProductDetails.SubscriptionOfferDetails subscriptionOfferDetails = this.offerDetails;
            return subscriptionOfferDetails == null ? "" : ((ProductDetails.PricingPhase) subscriptionOfferDetails.getPricingPhases().getPricingPhaseList().get(0)).getPriceCurrencyCode();
        }

        public int getDiscount() {
            if (this.discount == 0) {
                if (getPricePerMonth() == 0) {
                    return 0;
                }
                if (this.pricePerYearRegular != 0) {
                    double pricePerYear = getPricePerYear();
                    double d = this.pricePerYearRegular;
                    Double.isNaN(pricePerYear);
                    Double.isNaN(d);
                    int i = (int) ((1.0d - (pricePerYear / d)) * 100.0d);
                    this.discount = i;
                    if (i == 0) {
                        this.discount = -1;
                    }
                }
            }
            return this.discount;
        }

        public String getFormattedPrice() {
            return (BuildVars.useInvoiceBilling() || this.subscriptionOption.store_product == null) ? BillingController.getInstance().formatCurrency(getPrice(), getCurrency()) : this.googlePlayProductDetails == null ? "" : BillingController.getInstance().formatCurrency(getPrice(), getCurrency(), 6);
        }

        public String getFormattedPricePerMonth() {
            return (BuildVars.useInvoiceBilling() || this.subscriptionOption.store_product == null) ? BillingController.getInstance().formatCurrency(getPricePerMonth(), getCurrency()) : this.googlePlayProductDetails == null ? "" : BillingController.getInstance().formatCurrency(getPricePerMonth(), getCurrency(), 6);
        }

        public String getFormattedPricePerYear() {
            return (BuildVars.useInvoiceBilling() || this.subscriptionOption.store_product == null) ? BillingController.getInstance().formatCurrency(getPricePerYear(), getCurrency()) : this.googlePlayProductDetails == null ? "" : BillingController.getInstance().formatCurrency(getPricePerYear(), getCurrency(), 6);
        }

        public String getFormattedPricePerYearRegular() {
            return (BuildVars.useInvoiceBilling() || this.subscriptionOption.store_product == null) ? BillingController.getInstance().formatCurrency(this.pricePerYearRegular, getCurrency()) : this.googlePlayProductDetails == null ? "" : BillingController.getInstance().formatCurrency(this.pricePerYearRegular, getCurrency(), 6);
        }

        public ProductDetails getGooglePlayProductDetails() {
            return this.googlePlayProductDetails;
        }

        public int getMonths() {
            return this.subscriptionOption.months;
        }

        public ProductDetails.SubscriptionOfferDetails getOfferDetails() {
            checkOfferDetails();
            return this.offerDetails;
        }

        public long getPrice() {
            if (BuildVars.useInvoiceBilling() || this.subscriptionOption.store_product == null) {
                return this.subscriptionOption.amount;
            }
            if (this.googlePlayProductDetails == null) {
                return 0L;
            }
            checkOfferDetails();
            ProductDetails.SubscriptionOfferDetails subscriptionOfferDetails = this.offerDetails;
            if (subscriptionOfferDetails == null) {
                return 0L;
            }
            return ((ProductDetails.PricingPhase) subscriptionOfferDetails.getPricingPhases().getPricingPhaseList().get(0)).getPriceAmountMicros();
        }

        public long getPricePerMonth() {
            if (this.pricePerMonth == 0) {
                long price = getPrice();
                if (price != 0) {
                    this.pricePerMonth = price / this.subscriptionOption.months;
                }
            }
            return this.pricePerMonth;
        }

        public long getPricePerYear() {
            if (this.pricePerYear == 0) {
                long price = getPrice();
                if (price != 0) {
                    double d = price;
                    double d2 = this.subscriptionOption.months;
                    Double.isNaN(d);
                    Double.isNaN(d2);
                    this.pricePerYear = (long) ((d / d2) * 12.0d);
                }
            }
            return this.pricePerYear;
        }

        public void setGooglePlayProductDetails(ProductDetails productDetails) {
            this.googlePlayProductDetails = productDetails;
        }

        public void setPricePerYearRegular(long j) {
            this.pricePerYearRegular = j;
        }
    }

    public PremiumPreviewFragment(int i, String str) {
        this.premiumFeatures = new ArrayList();
        this.morePremiumFeatures = new ArrayList();
        this.subscriptionTiers = new ArrayList();
        boolean z = false;
        this.selectedTierIndex = 0;
        this.matrix = new Matrix();
        this.gradientPaint = new Paint(1);
        Bitmap createBitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        this.gradientTextureBitmap = createBitmap;
        this.gradientCanvas = new Canvas(createBitmap);
        this.gradientTools = new PremiumGradient.PremiumGradientTools(Theme.key_premiumGradientBackground1, Theme.key_premiumGradientBackground2, Theme.key_premiumGradientBackground3, Theme.key_premiumGradientBackground4);
        PremiumGradient.PremiumGradientTools premiumGradientTools = new PremiumGradient.PremiumGradientTools(Theme.key_premiumGradient1, Theme.key_premiumGradient2, -1, -1);
        this.tiersGradientTools = premiumGradientTools;
        premiumGradientTools.exactly = true;
        premiumGradientTools.x1 = 0.0f;
        premiumGradientTools.y1 = 0.0f;
        premiumGradientTools.x2 = 0.0f;
        premiumGradientTools.y2 = 1.0f;
        premiumGradientTools.cx = 0.0f;
        premiumGradientTools.cy = 0.0f;
        this.type = i;
        if (!Theme.isCurrentThemeDark() && i == 1) {
            z = true;
        }
        this.whiteBackground = z;
        this.source = str;
    }

    public PremiumPreviewFragment(String str) {
        this(0, str);
    }

    public static CharSequence applyNewSpan(String str) {
        return applyNewSpan(str, -1);
    }

    public static CharSequence applyNewSpan(String str, int i) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        spannableStringBuilder.append((CharSequence) "  d");
        FilterCreateActivity.NewSpan newSpan = new FilterCreateActivity.NewSpan(false, i);
        newSpan.setColor(Theme.getColor(Theme.key_premiumGradient1));
        spannableStringBuilder.setSpan(newSpan, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 0);
        return spannableStringBuilder;
    }

    public static void buyPremium(BaseFragment baseFragment) {
        buyPremium(baseFragment, "settings");
    }

    public static void buyPremium(BaseFragment baseFragment, String str) {
        buyPremium(baseFragment, null, str, true);
    }

    public static void buyPremium(BaseFragment baseFragment, SubscriptionTier subscriptionTier, String str) {
        buyPremium(baseFragment, subscriptionTier, str, true);
    }

    public static void buyPremium(BaseFragment baseFragment, SubscriptionTier subscriptionTier, String str, boolean z) {
        buyPremium(baseFragment, subscriptionTier, str, z, null);
    }

    public static void buyPremium(final BaseFragment baseFragment, final SubscriptionTier subscriptionTier, String str, final boolean z, final BillingFlowParams.SubscriptionUpdateParams subscriptionUpdateParams) {
        Intent intent;
        TLRPC.TL_premiumSubscriptionOption tL_premiumSubscriptionOption;
        String str2;
        if (BuildVars.IS_BILLING_UNAVAILABLE) {
            baseFragment.showDialog(new PremiumNotAvailableBottomSheet(baseFragment));
            return;
        }
        if (subscriptionTier == null) {
            TLRPC.TL_help_premiumPromo premiumPromo = baseFragment.getAccountInstance().getMediaDataController().getPremiumPromo();
            if (premiumPromo != null) {
                Iterator<TLRPC.TL_premiumSubscriptionOption> it = premiumPromo.period_options.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    }
                    TLRPC.TL_premiumSubscriptionOption next = it.next();
                    int i = next.months;
                    if (i == 1) {
                        subscriptionTier = new SubscriptionTier(next);
                    } else if (i == 12) {
                        subscriptionTier = new SubscriptionTier(next);
                        break;
                    }
                }
            }
            z = true;
        }
        sentPremiumButtonClick();
        if (BuildVars.useInvoiceBilling()) {
            Activity parentActivity = baseFragment.getParentActivity();
            if (parentActivity instanceof LaunchActivity) {
                LaunchActivity launchActivity = (LaunchActivity) parentActivity;
                if (subscriptionTier != null && (tL_premiumSubscriptionOption = subscriptionTier.subscriptionOption) != null && (str2 = tL_premiumSubscriptionOption.bot_url) != null) {
                    Uri parse = Uri.parse(str2);
                    if (parse.getHost().equals("t.me") && !parse.getPath().startsWith("/$") && !parse.getPath().startsWith("/invoice/")) {
                        launchActivity.setNavigateToPremiumBot(true);
                    }
                    Browser.openUrl(launchActivity, subscriptionTier.subscriptionOption.bot_url);
                    return;
                }
                if (!TextUtils.isEmpty(baseFragment.getMessagesController().premiumBotUsername)) {
                    launchActivity.setNavigateToPremiumBot(true);
                    intent = new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/" + baseFragment.getMessagesController().premiumBotUsername + "?start=" + str));
                } else {
                    if (TextUtils.isEmpty(baseFragment.getMessagesController().premiumInvoiceSlug)) {
                        return;
                    }
                    intent = new Intent("android.intent.action.VIEW", Uri.parse("https://t.me/$" + baseFragment.getMessagesController().premiumInvoiceSlug));
                }
                launchActivity.onNewIntent(intent, null);
                return;
            }
        }
        ProductDetails productDetails = BillingController.PREMIUM_PRODUCT_DETAILS;
        if (productDetails == null || productDetails.getSubscriptionOfferDetails().isEmpty()) {
            return;
        }
        if (subscriptionTier.getGooglePlayProductDetails() == null) {
            subscriptionTier.setGooglePlayProductDetails(BillingController.PREMIUM_PRODUCT_DETAILS);
        }
        if (subscriptionTier.getOfferDetails() == null) {
            return;
        }
        BillingController.getInstance().queryPurchases("subs", new PurchasesResponseListener() {
            @Override
            public final void onQueryPurchasesResponse(BillingResult billingResult, List list) {
                PremiumPreviewFragment.lambda$buyPremium$14(BaseFragment.this, z, subscriptionUpdateParams, subscriptionTier, billingResult, list);
            }
        });
    }

    public void checkButtonDivider() {
        AndroidUtilities.updateViewVisibilityAnimated(this.buttonDivider, this.listView.canScrollVertically(1), 1.0f, true);
    }

    private void closeSetting() {
        this.settingsView.animate().translationY(AndroidUtilities.dp(1000.0f)).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                PremiumPreviewFragment.this.contentView.removeView(PremiumPreviewFragment.this.settingsView);
                PremiumPreviewFragment.this.settingsView = null;
                super.onAnimationEnd(animator);
            }
        });
    }

    public static String featureTypeToServerString(int i) {
        switch (i) {
            case 0:
                return "double_limits";
            case 1:
                return "more_upload";
            case 2:
                return "faster_download";
            case 3:
                return "no_ads";
            case 4:
                return "infinite_reactions";
            case 5:
                return "premium_stickers";
            case 6:
                return "profile_badge";
            case 7:
                return "animated_userpics";
            case 8:
                return "voice_to_text";
            case 9:
                return "advanced_chat_management";
            case 10:
                return "app_icons";
            case 11:
                return "animated_emoji";
            case 12:
                return "emoji_status";
            case 13:
                return "translations";
            case 14:
                return "stories";
            case 15:
                return "stories__stealth_mode";
            case 16:
                return "stories__permanent_views_history";
            case 17:
                return "stories__expiration_durations";
            case 18:
                return "stories__save_stories_to_gallery";
            case 19:
                return "stories__links_and_formatting";
            case 20:
                return "stories__priority_order";
            case 21:
                return "stories__caption";
            case 22:
                return "wallpapers";
            case 23:
                return "peer_colors";
            case 24:
                return "saved_tags";
            case 25:
                return "stories__quality";
            case 26:
                return "last_seen";
            case 27:
                return "message_privacy";
            case 28:
                return "business";
            case 29:
                return "business_location";
            case 30:
                return "business_hours";
            case 31:
                return "quick_replies";
            case 32:
                return "greeting_message";
            case 33:
                return "away_message";
            case 34:
                return "business_bots";
            case 35:
                return "folder_tags";
            case 36:
                return "business_intro";
            case 37:
                return "business_links";
            case 38:
                return "effects";
            default:
                return null;
        }
    }

    public static void fillBusinessFeaturesList(ArrayList arrayList, int i, boolean z) {
        PremiumFeatureData premiumFeatureData;
        final MessagesController messagesController = MessagesController.getInstance(i);
        if (z) {
            arrayList.add(new PremiumFeatureData(12, R.drawable.filled_premium_status2, LocaleController.getString(R.string.PremiumPreviewBusinessEmojiStatus), LocaleController.getString(R.string.PremiumPreviewBusinessEmojiStatusDescription)));
            arrayList.add(new PremiumFeatureData(35, R.drawable.premium_tags, LocaleController.getString(R.string.PremiumPreviewFolderTags), LocaleController.getString(R.string.PremiumPreviewFolderTagsDescription)));
            premiumFeatureData = new PremiumFeatureData(14, R.drawable.filled_premium_camera, LocaleController.getString(R.string.PremiumPreviewBusinessStories), LocaleController.getString(R.string.PremiumPreviewBusinessStoriesDescription));
        } else {
            arrayList.add(new PremiumFeatureData(29, R.drawable.filled_location, LocaleController.getString(R.string.PremiumBusinessLocation), LocaleController.getString(R.string.PremiumBusinessLocationDescription)));
            arrayList.add(new PremiumFeatureData(30, R.drawable.filled_premium_hours, LocaleController.getString(R.string.PremiumBusinessOpeningHours), LocaleController.getString(R.string.PremiumBusinessOpeningHoursDescription)));
            arrayList.add(new PremiumFeatureData(31, R.drawable.filled_open_message, LocaleController.getString(R.string.PremiumBusinessQuickReplies), LocaleController.getString(R.string.PremiumBusinessQuickRepliesDescription)));
            arrayList.add(new PremiumFeatureData(32, R.drawable.premium_status, LocaleController.getString(R.string.PremiumBusinessGreetingMessages), LocaleController.getString(R.string.PremiumBusinessGreetingMessagesDescription)));
            arrayList.add(new PremiumFeatureData(33, R.drawable.filled_premium_away, LocaleController.getString(R.string.PremiumBusinessAwayMessages), LocaleController.getString(R.string.PremiumBusinessAwayMessagesDescription)));
            arrayList.add(new PremiumFeatureData(34, R.drawable.filled_premium_bots, applyNewSpan(LocaleController.getString(R.string.PremiumBusinessChatbots2)), LocaleController.getString(R.string.PremiumBusinessChatbotsDescription)));
            arrayList.add(new PremiumFeatureData(37, R.drawable.filled_premium_chatlink, applyNewSpan(LocaleController.getString(R.string.PremiumBusinessChatLinks)), LocaleController.getString(R.string.PremiumBusinessChatLinksDescription)));
            premiumFeatureData = new PremiumFeatureData(36, R.drawable.filled_premium_intro, applyNewSpan(LocaleController.getString(R.string.PremiumBusinessIntro)), LocaleController.getString(R.string.PremiumBusinessIntroDescription));
        }
        arrayList.add(premiumFeatureData);
        if (messagesController.businessFeaturesTypesToPosition.size() > 0) {
            int i2 = 0;
            while (i2 < arrayList.size()) {
                if (messagesController.businessFeaturesTypesToPosition.get(((PremiumFeatureData) arrayList.get(i2)).type, -1) == -1 && !BuildVars.DEBUG_VERSION) {
                    arrayList.remove(i2);
                    i2--;
                }
                i2++;
            }
        }
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$fillBusinessFeaturesList$6;
                lambda$fillBusinessFeaturesList$6 = PremiumPreviewFragment.lambda$fillBusinessFeaturesList$6(MessagesController.this, (PremiumPreviewFragment.PremiumFeatureData) obj, (PremiumPreviewFragment.PremiumFeatureData) obj2);
                return lambda$fillBusinessFeaturesList$6;
            }
        });
    }

    public static void fillPremiumFeaturesList(ArrayList arrayList, int i, boolean z) {
        final MessagesController messagesController = MessagesController.getInstance(i);
        int i2 = 0;
        arrayList.add(new PremiumFeatureData(0, R.drawable.msg_premium_limits, LocaleController.getString(R.string.PremiumPreviewLimits), LocaleController.formatString(R.string.PremiumPreviewLimitsDescription, Integer.valueOf(messagesController.channelsLimitPremium), Integer.valueOf(messagesController.dialogFiltersLimitPremium), Integer.valueOf(messagesController.dialogFiltersPinnedLimitPremium), Integer.valueOf(messagesController.publicLinksLimitPremium), 4)));
        arrayList.add(new PremiumFeatureData(14, R.drawable.msg_filled_stories, LocaleController.getString(R.string.PremiumPreviewStories), LocaleController.formatString(R.string.PremiumPreviewStoriesDescription, new Object[0])));
        arrayList.add(new PremiumFeatureData(1, R.drawable.msg_premium_uploads, LocaleController.getString(R.string.PremiumPreviewUploads), LocaleController.getString(R.string.PremiumPreviewUploadsDescription)));
        arrayList.add(new PremiumFeatureData(2, R.drawable.msg_premium_speed, LocaleController.getString(R.string.PremiumPreviewDownloadSpeed), LocaleController.getString(R.string.PremiumPreviewDownloadSpeedDescription)));
        arrayList.add(new PremiumFeatureData(8, R.drawable.msg_premium_voice, LocaleController.getString(R.string.PremiumPreviewVoiceToText), LocaleController.getString(R.string.PremiumPreviewVoiceToTextDescription)));
        arrayList.add(new PremiumFeatureData(3, R.drawable.msg_premium_ads, LocaleController.getString(R.string.PremiumPreviewNoAds), LocaleController.getString(R.string.PremiumPreviewNoAdsDescription)));
        arrayList.add(new PremiumFeatureData(4, R.drawable.msg_premium_reactions, LocaleController.getString(R.string.PremiumPreviewReactions2), LocaleController.getString(R.string.PremiumPreviewReactions2Description)));
        arrayList.add(new PremiumFeatureData(5, R.drawable.msg_premium_stickers, LocaleController.getString(R.string.PremiumPreviewStickers), LocaleController.getString(R.string.PremiumPreviewStickersDescription)));
        arrayList.add(new PremiumFeatureData(11, R.drawable.msg_premium_emoji, LocaleController.getString(R.string.PremiumPreviewEmoji), LocaleController.getString(R.string.PremiumPreviewEmojiDescription)));
        arrayList.add(new PremiumFeatureData(9, R.drawable.menu_premium_tools, LocaleController.getString(R.string.PremiumPreviewAdvancedChatManagement), LocaleController.getString(R.string.PremiumPreviewAdvancedChatManagementDescription)));
        arrayList.add(new PremiumFeatureData(6, R.drawable.msg_premium_badge, LocaleController.getString(R.string.PremiumPreviewProfileBadge), LocaleController.getString(R.string.PremiumPreviewProfileBadgeDescription)));
        arrayList.add(new PremiumFeatureData(7, R.drawable.msg_premium_avatar, LocaleController.getString(R.string.PremiumPreviewAnimatedProfiles), LocaleController.getString(R.string.PremiumPreviewAnimatedProfilesDescription)));
        arrayList.add(new PremiumFeatureData(24, R.drawable.premium_tags, LocaleController.getString(R.string.PremiumPreviewTags2), LocaleController.getString(R.string.PremiumPreviewTagsDescription2)));
        arrayList.add(new PremiumFeatureData(10, R.drawable.msg_premium_icons, LocaleController.getString(R.string.PremiumPreviewAppIcon), LocaleController.getString(R.string.PremiumPreviewAppIconDescription)));
        arrayList.add(new PremiumFeatureData(12, R.drawable.premium_status, LocaleController.getString(R.string.PremiumPreviewEmojiStatus), LocaleController.getString(R.string.PremiumPreviewEmojiStatusDescription)));
        arrayList.add(new PremiumFeatureData(13, R.drawable.msg_premium_translate, LocaleController.getString(R.string.PremiumPreviewTranslations), LocaleController.getString(R.string.PremiumPreviewTranslationsDescription)));
        arrayList.add(new PremiumFeatureData(22, R.drawable.premium_wallpaper, LocaleController.getString(R.string.PremiumPreviewWallpaper), LocaleController.getString(R.string.PremiumPreviewWallpaperDescription)));
        arrayList.add(new PremiumFeatureData(23, R.drawable.premium_colors, LocaleController.getString(R.string.PremiumPreviewProfileColor), LocaleController.getString(R.string.PremiumPreviewProfileColorDescription)));
        arrayList.add(new PremiumFeatureData(26, R.drawable.menu_premium_seen, LocaleController.getString(R.string.PremiumPreviewLastSeen), LocaleController.getString(R.string.PremiumPreviewLastSeenDescription)));
        arrayList.add(new PremiumFeatureData(27, R.drawable.menu_premium_privacy, LocaleController.getString(R.string.PremiumPreviewMessagePrivacy), LocaleController.getString(R.string.PremiumPreviewMessagePrivacyDescription)));
        arrayList.add(new PremiumFeatureData(28, R.drawable.filled_premium_business, applyNewSpan(LocaleController.getString(R.string.TelegramBusiness)), LocaleController.getString(R.string.PremiumPreviewBusinessDescription)));
        arrayList.add(new PremiumFeatureData(38, R.drawable.menu_premium_effects, applyNewSpan(LocaleController.getString(R.string.PremiumPreviewEffects)), LocaleController.getString(R.string.PremiumPreviewEffectsDescription)));
        if (messagesController.premiumFeaturesTypesToPosition.size() > 0) {
            while (i2 < arrayList.size()) {
                if (messagesController.premiumFeaturesTypesToPosition.get(((PremiumFeatureData) arrayList.get(i2)).type, -1) == -1 && !BuildVars.DEBUG_PRIVATE_VERSION) {
                    arrayList.remove(i2);
                    i2--;
                }
                i2++;
            }
        }
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$fillPremiumFeaturesList$5;
                lambda$fillPremiumFeaturesList$5 = PremiumPreviewFragment.lambda$fillPremiumFeaturesList$5(MessagesController.this, (PremiumPreviewFragment.PremiumFeatureData) obj, (PremiumPreviewFragment.PremiumFeatureData) obj2);
                return lambda$fillPremiumFeaturesList$5;
            }
        });
    }

    public static String getPremiumButtonText(int i, SubscriptionTier subscriptionTier) {
        String formatCurrency;
        if (BuildVars.IS_BILLING_UNAVAILABLE) {
            return LocaleController.getString(R.string.SubscribeToPremiumNotAvailable);
        }
        int i2 = R.string.SubscribeToPremium;
        if (subscriptionTier != null) {
            if (!BuildVars.useInvoiceBilling() && subscriptionTier.getOfferDetails() == null) {
                return LocaleController.getString(R.string.Loading);
            }
            boolean isPremium = UserConfig.getInstance(i).isPremium();
            boolean z = subscriptionTier.getMonths() == 12;
            String formattedPricePerYear = z ? subscriptionTier.getFormattedPricePerYear() : subscriptionTier.getFormattedPricePerMonth();
            if (isPremium) {
                i2 = z ? R.string.UpgradePremiumPerYear : R.string.UpgradePremiumPerMonth;
            } else if (!z || MessagesController.getInstance(i).showAnnualPerMonth) {
                formattedPricePerYear = subscriptionTier.getFormattedPricePerMonth();
            } else {
                i2 = R.string.SubscribeToPremiumPerYear;
                formattedPricePerYear = subscriptionTier.getFormattedPrice();
            }
            return LocaleController.formatString(i2, formattedPricePerYear);
        }
        String str = null;
        TLRPC.TL_premiumSubscriptionOption tL_premiumSubscriptionOption = null;
        str = null;
        str = null;
        if (!BuildVars.useInvoiceBilling()) {
            ProductDetails productDetails = BillingController.PREMIUM_PRODUCT_DETAILS;
            if (productDetails != null) {
                List subscriptionOfferDetails = productDetails.getSubscriptionOfferDetails();
                if (!subscriptionOfferDetails.isEmpty()) {
                    Iterator it = ((ProductDetails.SubscriptionOfferDetails) subscriptionOfferDetails.get(0)).getPricingPhases().getPricingPhaseList().iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        ProductDetails.PricingPhase pricingPhase = (ProductDetails.PricingPhase) it.next();
                        if (pricingPhase.getBillingPeriod().equals("P1M")) {
                            str = pricingPhase.getFormattedPrice();
                        } else if (pricingPhase.getBillingPeriod().equals("P1Y")) {
                            if (MessagesController.getInstance(i).showAnnualPerMonth) {
                                str = BillingController.getInstance().formatCurrency(pricingPhase.getPriceAmountMicros() / 12, pricingPhase.getPriceCurrencyCode(), 6);
                            } else {
                                i2 = R.string.SubscribeToPremiumPerYear;
                                str = BillingController.getInstance().formatCurrency(pricingPhase.getPriceAmountMicros(), pricingPhase.getPriceCurrencyCode(), 6);
                            }
                        }
                    }
                }
            }
            return str == null ? LocaleController.getString(R.string.Loading) : LocaleController.formatString(i2, str);
        }
        TLRPC.TL_help_premiumPromo premiumPromo = MediaDataController.getInstance(i).getPremiumPromo();
        if (premiumPromo == null) {
            return LocaleController.getString(R.string.SubscribeToPremiumNoPrice);
        }
        Iterator<TLRPC.TL_premiumSubscriptionOption> it2 = premiumPromo.period_options.iterator();
        while (true) {
            if (!it2.hasNext()) {
                break;
            }
            TLRPC.TL_premiumSubscriptionOption next = it2.next();
            int i3 = next.months;
            if (i3 == 12) {
                tL_premiumSubscriptionOption = next;
                break;
            }
            if (tL_premiumSubscriptionOption == null && i3 == 1) {
                tL_premiumSubscriptionOption = next;
            }
        }
        if (tL_premiumSubscriptionOption == null) {
            return LocaleController.getString(R.string.SubscribeToPremiumNoPrice);
        }
        if (tL_premiumSubscriptionOption.months == 12) {
            if (MessagesController.getInstance(i).showAnnualPerMonth) {
                formatCurrency = BillingController.getInstance().formatCurrency(tL_premiumSubscriptionOption.amount / 12, tL_premiumSubscriptionOption.currency);
                return LocaleController.formatString(i2, formatCurrency);
            }
            i2 = R.string.SubscribeToPremiumPerYear;
        }
        formatCurrency = BillingController.getInstance().formatCurrency(tL_premiumSubscriptionOption.amount, tL_premiumSubscriptionOption.currency);
        return LocaleController.formatString(i2, formatCurrency);
    }

    public static void lambda$buyPremium$10(Runnable runnable, BillingResult billingResult) {
        if (billingResult.getResponseCode() == 0) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public static void lambda$buyPremium$11(TLObject tLObject, BaseFragment baseFragment, TLRPC.TL_inputStorePaymentPremiumSubscription tL_inputStorePaymentPremiumSubscription, SubscriptionTier subscriptionTier, BillingFlowParams.SubscriptionUpdateParams subscriptionUpdateParams, TLRPC.TL_error tL_error, TLRPC.TL_payments_canPurchasePremium tL_payments_canPurchasePremium) {
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            BillingController.getInstance().launchBillingFlow(baseFragment.getParentActivity(), baseFragment.getAccountInstance(), tL_inputStorePaymentPremiumSubscription, Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(BillingController.PREMIUM_PRODUCT_DETAILS).setOfferToken(subscriptionTier.getOfferDetails().getOfferToken()).build()), subscriptionUpdateParams, false);
        } else {
            AlertsCreator.processError(baseFragment.getCurrentAccount(), tL_error, baseFragment, tL_payments_canPurchasePremium, new Object[0]);
        }
    }

    public static void lambda$buyPremium$12(final BaseFragment baseFragment, final TLRPC.TL_inputStorePaymentPremiumSubscription tL_inputStorePaymentPremiumSubscription, final SubscriptionTier subscriptionTier, final BillingFlowParams.SubscriptionUpdateParams subscriptionUpdateParams, final TLRPC.TL_payments_canPurchasePremium tL_payments_canPurchasePremium, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PremiumPreviewFragment.lambda$buyPremium$11(TLObject.this, baseFragment, tL_inputStorePaymentPremiumSubscription, subscriptionTier, subscriptionUpdateParams, tL_error, tL_payments_canPurchasePremium);
            }
        });
    }

    public static void lambda$buyPremium$13(BillingResult billingResult, final BaseFragment baseFragment, final boolean z, List list, final BillingFlowParams.SubscriptionUpdateParams subscriptionUpdateParams, final SubscriptionTier subscriptionTier) {
        if (billingResult.getResponseCode() == 0) {
            final Runnable runnable = new Runnable() {
                @Override
                public final void run() {
                    PremiumPreviewFragment.lambda$buyPremium$7(BaseFragment.this, z);
                }
            };
            if (list != null && !list.isEmpty() && !baseFragment.getUserConfig().isPremium()) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    Purchase purchase = (Purchase) it.next();
                    if (purchase.getProducts().contains("telegram_premium")) {
                        final TLRPC.TL_payments_assignPlayMarketTransaction tL_payments_assignPlayMarketTransaction = new TLRPC.TL_payments_assignPlayMarketTransaction();
                        TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
                        tL_payments_assignPlayMarketTransaction.receipt = tL_dataJSON;
                        tL_dataJSON.data = purchase.getOriginalJson();
                        TLRPC.TL_inputStorePaymentPremiumSubscription tL_inputStorePaymentPremiumSubscription = new TLRPC.TL_inputStorePaymentPremiumSubscription();
                        tL_inputStorePaymentPremiumSubscription.restore = true;
                        if (subscriptionUpdateParams != null) {
                            tL_inputStorePaymentPremiumSubscription.upgrade = true;
                        }
                        tL_payments_assignPlayMarketTransaction.purpose = tL_inputStorePaymentPremiumSubscription;
                        baseFragment.getConnectionsManager().sendRequest(tL_payments_assignPlayMarketTransaction, new RequestDelegate() {
                            @Override
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                PremiumPreviewFragment.lambda$buyPremium$9(BaseFragment.this, runnable, tL_payments_assignPlayMarketTransaction, tLObject, tL_error);
                            }
                        }, 66);
                        return;
                    }
                }
            }
            BillingController.getInstance().addResultListener("telegram_premium", new Consumer() {
                @Override
                public final void accept(Object obj) {
                    PremiumPreviewFragment.lambda$buyPremium$10(runnable, (BillingResult) obj);
                }
            });
            final TLRPC.TL_payments_canPurchasePremium tL_payments_canPurchasePremium = new TLRPC.TL_payments_canPurchasePremium();
            final TLRPC.TL_inputStorePaymentPremiumSubscription tL_inputStorePaymentPremiumSubscription2 = new TLRPC.TL_inputStorePaymentPremiumSubscription();
            if (subscriptionUpdateParams != null) {
                tL_inputStorePaymentPremiumSubscription2.upgrade = true;
            }
            tL_payments_canPurchasePremium.purpose = tL_inputStorePaymentPremiumSubscription2;
            baseFragment.getConnectionsManager().sendRequest(tL_payments_canPurchasePremium, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PremiumPreviewFragment.lambda$buyPremium$12(BaseFragment.this, tL_inputStorePaymentPremiumSubscription2, subscriptionTier, subscriptionUpdateParams, tL_payments_canPurchasePremium, tLObject, tL_error);
                }
            });
        }
    }

    public static void lambda$buyPremium$14(final BaseFragment baseFragment, final boolean z, final BillingFlowParams.SubscriptionUpdateParams subscriptionUpdateParams, final SubscriptionTier subscriptionTier, final BillingResult billingResult, final List list) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PremiumPreviewFragment.lambda$buyPremium$13(BillingResult.this, baseFragment, z, list, subscriptionUpdateParams, subscriptionTier);
            }
        });
    }

    public static void lambda$buyPremium$7(BaseFragment baseFragment, boolean z) {
        if (baseFragment instanceof PremiumPreviewFragment) {
            PremiumPreviewFragment premiumPreviewFragment = (PremiumPreviewFragment) baseFragment;
            if (z) {
                premiumPreviewFragment.setForcePremium();
            }
            premiumPreviewFragment.getMediaDataController().loadPremiumPromo(false);
            premiumPreviewFragment.listView.smoothScrollToPosition(0);
        } else {
            PremiumPreviewFragment premiumPreviewFragment2 = new PremiumPreviewFragment(null);
            if (z) {
                premiumPreviewFragment2.setForcePremium();
            }
            baseFragment.presentFragment(premiumPreviewFragment2);
        }
        if (baseFragment.getParentActivity() instanceof LaunchActivity) {
            try {
                baseFragment.getFragmentView().performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            ((LaunchActivity) baseFragment.getParentActivity()).getFireworksOverlay().start();
        }
    }

    public static void lambda$buyPremium$8(BaseFragment baseFragment, TLRPC.TL_error tL_error, TLRPC.TL_payments_assignPlayMarketTransaction tL_payments_assignPlayMarketTransaction) {
        AlertsCreator.processError(baseFragment.getCurrentAccount(), tL_error, baseFragment, tL_payments_assignPlayMarketTransaction, new Object[0]);
    }

    public static void lambda$buyPremium$9(final BaseFragment baseFragment, Runnable runnable, final TLRPC.TL_payments_assignPlayMarketTransaction tL_payments_assignPlayMarketTransaction, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.Updates) {
            baseFragment.getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
        } else if (tL_error == null) {
            return;
        } else {
            runnable = new Runnable() {
                @Override
                public final void run() {
                    PremiumPreviewFragment.lambda$buyPremium$8(BaseFragment.this, tL_error, tL_payments_assignPlayMarketTransaction);
                }
            };
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public void lambda$createView$0(TLRPC.TL_error tL_error, TLObject tLObject) {
        if (tL_error != null) {
            BulletinFactory.showError(tL_error);
        } else {
            if (tLObject instanceof TLRPC.TL_boolTrue) {
                return;
            }
            BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
        }
    }

    public void lambda$createView$1(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                PremiumPreviewFragment.this.lambda$createView$0(tL_error, tLObject);
            }
        });
    }

    public void lambda$createView$2(PremiumFeatureCell premiumFeatureCell, Long l, Integer num) {
        TLRPC.EmojiStatus emojiStatus;
        if (l == null) {
            emojiStatus = new TLRPC.TL_emojiStatusEmpty();
        } else if (num != null) {
            TLRPC.TL_emojiStatusUntil tL_emojiStatusUntil = new TLRPC.TL_emojiStatusUntil();
            tL_emojiStatusUntil.document_id = l.longValue();
            tL_emojiStatusUntil.until = num.intValue();
            emojiStatus = tL_emojiStatusUntil;
        } else {
            TLRPC.TL_emojiStatus tL_emojiStatus = new TLRPC.TL_emojiStatus();
            tL_emojiStatus.document_id = l.longValue();
            emojiStatus = tL_emojiStatus;
        }
        getMessagesController().updateEmojiStatus(emojiStatus);
        premiumFeatureCell.setEmoji(l == null ? 0L : l.longValue(), true);
    }

    public void lambda$createView$3(View view, int i) {
        BaseFragment businessLinksActivity;
        if (i == this.showAdsRow) {
            TLRPC.UserFull userFull = getMessagesController().getUserFull(getUserConfig().getClientUserId());
            if (userFull == null) {
                return;
            }
            TextCell textCell = (TextCell) view;
            textCell.setChecked(!textCell.isChecked());
            userFull.sponsored_enabled = textCell.isChecked();
            TLRPC.TL_account_toggleSponsoredMessages tL_account_toggleSponsoredMessages = new TLRPC.TL_account_toggleSponsoredMessages();
            tL_account_toggleSponsoredMessages.enabled = userFull.sponsored_enabled;
            getConnectionsManager().sendRequest(tL_account_toggleSponsoredMessages, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    PremiumPreviewFragment.this.lambda$createView$1(tLObject, tL_error);
                }
            });
            getMessagesStorage().updateUserInfo(userFull, false);
            return;
        }
        if (view instanceof PremiumFeatureCell) {
            final PremiumFeatureCell premiumFeatureCell = (PremiumFeatureCell) view;
            SubscriptionTier subscriptionTier = null;
            if (this.type != 1 || !getUserConfig().isPremium()) {
                sentShowFeaturePreview(this.currentAccount, premiumFeatureCell.data.type);
                int i2 = this.selectedTierIndex;
                if (i2 >= 0 && i2 < this.subscriptionTiers.size()) {
                    subscriptionTier = (SubscriptionTier) this.subscriptionTiers.get(this.selectedTierIndex);
                }
                showDialog(new PremiumFeatureBottomSheet(this, getContext(), this.currentAccount, this.type == 1, premiumFeatureCell.data.type, false, subscriptionTier));
                return;
            }
            int i3 = premiumFeatureCell.data.type;
            if (i3 == 29) {
                businessLinksActivity = new org.telegram.ui.Business.LocationActivity();
            } else if (i3 == 32) {
                businessLinksActivity = new GreetMessagesActivity();
            } else if (i3 == 33) {
                businessLinksActivity = new AwayMessagesActivity();
            } else if (i3 == 30) {
                businessLinksActivity = new OpeningHoursActivity();
            } else if (i3 == 34) {
                businessLinksActivity = new ChatbotsActivity();
            } else if (i3 == 31) {
                businessLinksActivity = new QuickRepliesActivity();
            } else {
                if (i3 == 14) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("dialog_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
                    bundle.putInt("type", 1);
                    presentFragment(new MediaActivity(bundle, null));
                    return;
                }
                if (i3 == 12) {
                    showSelectStatusDialog(premiumFeatureCell, UserObject.getEmojiStatusDocumentId(getUserConfig().getCurrentUser()), new Utilities.Callback2() {
                        @Override
                        public final void run(Object obj, Object obj2) {
                            PremiumPreviewFragment.this.lambda$createView$2(premiumFeatureCell, (Long) obj, (Integer) obj2);
                        }
                    });
                    return;
                } else if (i3 == 35) {
                    businessLinksActivity = new FiltersSetupActivity().highlightTags();
                } else if (i3 == 36) {
                    businessLinksActivity = new BusinessIntroActivity();
                } else if (i3 != 37) {
                    return;
                } else {
                    businessLinksActivity = new BusinessLinksActivity();
                }
            }
            presentFragment(businessLinksActivity);
        }
    }

    public void lambda$createView$4() {
        getMediaDataController().loadPremiumPromo(false);
    }

    public static int lambda$fillBusinessFeaturesList$6(MessagesController messagesController, PremiumFeatureData premiumFeatureData, PremiumFeatureData premiumFeatureData2) {
        return messagesController.businessFeaturesTypesToPosition.get(premiumFeatureData.type, Integer.MAX_VALUE) - messagesController.businessFeaturesTypesToPosition.get(premiumFeatureData2.type, Integer.MAX_VALUE);
    }

    public static int lambda$fillPremiumFeaturesList$5(MessagesController messagesController, PremiumFeatureData premiumFeatureData, PremiumFeatureData premiumFeatureData2) {
        return messagesController.premiumFeaturesTypesToPosition.get(premiumFeatureData.type, Integer.MAX_VALUE) - messagesController.premiumFeaturesTypesToPosition.get(premiumFeatureData2.type, Integer.MAX_VALUE);
    }

    public static void lambda$sentPremiumButtonClick$19(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$sentPremiumBuyCanceled$20(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$sentShowFeaturePreview$21(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public static void lambda$sentShowScreenStat$18(TLObject tLObject, TLRPC.TL_error tL_error) {
    }

    public void lambda$updateButtonText$15(View view) {
        buyPremium(this);
    }

    public static void lambda$updateButtonText$16(View view) {
    }

    public void lambda$updateButtonText$17(View view) {
        TLRPC.TL_premiumSubscriptionOption tL_premiumSubscriptionOption;
        SubscriptionTier subscriptionTier = (SubscriptionTier) this.subscriptionTiers.get(this.selectedTierIndex);
        SubscriptionTier subscriptionTier2 = this.currentSubscriptionTier;
        buyPremium(this, subscriptionTier, "settings", true, (subscriptionTier2 == null || (tL_premiumSubscriptionOption = subscriptionTier2.subscriptionOption) == null || tL_premiumSubscriptionOption.transaction == null) ? null : BillingFlowParams.SubscriptionUpdateParams.newBuilder().setOldPurchaseToken(BillingController.getInstance().getLastPremiumToken()).setSubscriptionReplacementMode(5).build());
    }

    public void measureGradient(int i, int i2) {
        int i3 = 0;
        for (int i4 = 0; i4 < this.premiumFeatures.size(); i4++) {
            this.dummyCell.setData((PremiumFeatureData) this.premiumFeatures.get(i4), false);
            this.dummyCell.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
            ((PremiumFeatureData) this.premiumFeatures.get(i4)).yOffset = i3;
            i3 += this.dummyCell.getMeasuredHeight();
        }
        for (int i5 = 0; i5 < this.morePremiumFeatures.size(); i5++) {
            this.dummyCell.setData((PremiumFeatureData) this.morePremiumFeatures.get(i5), false);
            this.dummyCell.measure(View.MeasureSpec.makeMeasureSpec(i, 1073741824), View.MeasureSpec.makeMeasureSpec(i2, Integer.MIN_VALUE));
            ((PremiumFeatureData) this.morePremiumFeatures.get(i5)).yOffset = i3;
            i3 += this.dummyCell.getMeasuredHeight();
        }
        this.totalGradientHeight = i3;
    }

    public static void sentPremiumButtonClick() {
        TLRPC.TL_help_saveAppLog tL_help_saveAppLog = new TLRPC.TL_help_saveAppLog();
        TLRPC.TL_inputAppEvent tL_inputAppEvent = new TLRPC.TL_inputAppEvent();
        tL_inputAppEvent.time = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
        tL_inputAppEvent.type = "premium.promo_screen_accept";
        tL_inputAppEvent.data = new TLRPC.TL_jsonNull();
        tL_help_saveAppLog.events.add(tL_inputAppEvent);
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tL_help_saveAppLog, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PremiumPreviewFragment.lambda$sentPremiumButtonClick$19(tLObject, tL_error);
            }
        });
    }

    public static void sentPremiumBuyCanceled() {
        TLRPC.TL_help_saveAppLog tL_help_saveAppLog = new TLRPC.TL_help_saveAppLog();
        TLRPC.TL_inputAppEvent tL_inputAppEvent = new TLRPC.TL_inputAppEvent();
        tL_inputAppEvent.time = ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime();
        tL_inputAppEvent.type = "premium.promo_screen_fail";
        tL_inputAppEvent.data = new TLRPC.TL_jsonNull();
        tL_help_saveAppLog.events.add(tL_inputAppEvent);
        ConnectionsManager.getInstance(UserConfig.selectedAccount).sendRequest(tL_help_saveAppLog, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PremiumPreviewFragment.lambda$sentPremiumBuyCanceled$20(tLObject, tL_error);
            }
        });
    }

    public static void sentShowFeaturePreview(int i, int i2) {
        TLRPC.TL_help_saveAppLog tL_help_saveAppLog = new TLRPC.TL_help_saveAppLog();
        TLRPC.TL_inputAppEvent tL_inputAppEvent = new TLRPC.TL_inputAppEvent();
        tL_inputAppEvent.time = ConnectionsManager.getInstance(i).getCurrentTime();
        tL_inputAppEvent.type = "premium.promo_screen_tap";
        TLRPC.TL_jsonObject tL_jsonObject = new TLRPC.TL_jsonObject();
        tL_inputAppEvent.data = tL_jsonObject;
        TLRPC.TL_jsonObjectValue tL_jsonObjectValue = new TLRPC.TL_jsonObjectValue();
        String featureTypeToServerString = featureTypeToServerString(i2);
        if (featureTypeToServerString != null) {
            TLRPC.TL_jsonString tL_jsonString = new TLRPC.TL_jsonString();
            tL_jsonString.value = featureTypeToServerString;
            tL_jsonObjectValue.value = tL_jsonString;
        } else {
            tL_jsonObjectValue.value = new TLRPC.TL_jsonNull();
        }
        tL_jsonObjectValue.key = "item";
        tL_jsonObject.value.add(tL_jsonObjectValue);
        tL_help_saveAppLog.events.add(tL_inputAppEvent);
        ConnectionsManager.getInstance(i).sendRequest(tL_help_saveAppLog, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PremiumPreviewFragment.lambda$sentShowFeaturePreview$21(tLObject, tL_error);
            }
        });
    }

    public static void sentShowScreenStat(String str) {
        TLRPC.TL_jsonNull tL_jsonNull;
        ConnectionsManager connectionsManager = ConnectionsManager.getInstance(UserConfig.selectedAccount);
        TLRPC.TL_help_saveAppLog tL_help_saveAppLog = new TLRPC.TL_help_saveAppLog();
        TLRPC.TL_inputAppEvent tL_inputAppEvent = new TLRPC.TL_inputAppEvent();
        tL_inputAppEvent.time = connectionsManager.getCurrentTime();
        tL_inputAppEvent.type = "premium.promo_screen_show";
        TLRPC.TL_jsonObject tL_jsonObject = new TLRPC.TL_jsonObject();
        tL_inputAppEvent.data = tL_jsonObject;
        TLRPC.TL_jsonObjectValue tL_jsonObjectValue = new TLRPC.TL_jsonObjectValue();
        if (str != null) {
            TLRPC.TL_jsonString tL_jsonString = new TLRPC.TL_jsonString();
            tL_jsonString.value = str;
            tL_jsonNull = tL_jsonString;
        } else {
            tL_jsonNull = new TLRPC.TL_jsonNull();
        }
        tL_jsonObjectValue.key = "source";
        tL_jsonObjectValue.value = tL_jsonNull;
        tL_jsonObject.value.add(tL_jsonObjectValue);
        tL_help_saveAppLog.events.add(tL_inputAppEvent);
        connectionsManager.sendRequest(tL_help_saveAppLog, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                PremiumPreviewFragment.lambda$sentShowScreenStat$18(tLObject, tL_error);
            }
        });
    }

    public static int serverStringToFeatureType(String str) {
        char c;
        str.hashCode();
        switch (str.hashCode()) {
            case -2145993328:
                if (str.equals("animated_userpics")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -2080028929:
                if (str.equals("infinite_reactions")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case -2022719725:
                if (str.equals("stories__caption")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case -1975141450:
                if (str.equals("away_message")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case -1953490480:
                if (str.equals("business_hours")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case -1952597715:
                if (str.equals("business_intro")) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -1949982086:
                if (str.equals("business_links")) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case -1924078036:
                if (str.equals("stories__quality")) {
                    c = 7;
                    break;
                }
                c = 65535;
                break;
            case -1919626711:
                if (str.equals("stories__save_stories_to_gallery")) {
                    c = '\b';
                    break;
                }
                c = 65535;
                break;
            case -1884266413:
                if (str.equals("stories")) {
                    c = '\t';
                    break;
                }
                c = 65535;
                break;
            case -1833928446:
                if (str.equals("effects")) {
                    c = '\n';
                    break;
                }
                c = 65535;
                break;
            case -1777612460:
                if (str.equals("business_location")) {
                    c = 11;
                    break;
                }
                c = 65535;
                break;
            case -1761847571:
                if (str.equals("peer_colors")) {
                    c = '\f';
                    break;
                }
                c = 65535;
                break;
            case -1755514268:
                if (str.equals("voice_to_text")) {
                    c = '\r';
                    break;
                }
                c = 65535;
                break;
            case -1529105743:
                if (str.equals("wallpapers")) {
                    c = 14;
                    break;
                }
                c = 65535;
                break;
            case -1516580016:
                if (str.equals("message_privacy")) {
                    c = 15;
                    break;
                }
                c = 65535;
                break;
            case -1425144150:
                if (str.equals("animated_emoji")) {
                    c = 16;
                    break;
                }
                c = 65535;
                break;
            case -1225497630:
                if (str.equals("translations")) {
                    c = 17;
                    break;
                }
                c = 65535;
                break;
            case -1146830912:
                if (str.equals("business")) {
                    c = 18;
                    break;
                }
                c = 65535;
                break;
            case -1040323278:
                if (str.equals("no_ads")) {
                    c = 19;
                    break;
                }
                c = 65535;
                break;
            case -1023650261:
                if (str.equals("more_upload")) {
                    c = 20;
                    break;
                }
                c = 65535;
                break;
            case -969043445:
                if (str.equals("emoji_status")) {
                    c = 21;
                    break;
                }
                c = 65535;
                break;
            case -730864243:
                if (str.equals("profile_badge")) {
                    c = 22;
                    break;
                }
                c = 65535;
                break;
            case -448825858:
                if (str.equals("faster_download")) {
                    c = 23;
                    break;
                }
                c = 65535;
                break;
            case -294265343:
                if (str.equals("greeting_message")) {
                    c = 24;
                    break;
                }
                c = 65535;
                break;
            case -165039170:
                if (str.equals("premium_stickers")) {
                    c = 25;
                    break;
                }
                c = 65535;
                break;
            case -96210874:
                if (str.equals("double_limits")) {
                    c = 26;
                    break;
                }
                c = 65535;
                break;
            case 110781770:
                if (str.equals("folder_tags")) {
                    c = 27;
                    break;
                }
                c = 65535;
                break;
            case 234735554:
                if (str.equals("stories__expiration_durations")) {
                    c = 28;
                    break;
                }
                c = 65535;
                break;
            case 388416338:
                if (str.equals("stories__stealth_mode")) {
                    c = 29;
                    break;
                }
                c = 65535;
                break;
            case 480338102:
                if (str.equals("quick_replies")) {
                    c = 30;
                    break;
                }
                c = 65535;
                break;
            case 629542059:
                if (str.equals("business_bots")) {
                    c = 31;
                    break;
                }
                c = 65535;
                break;
            case 705083174:
                if (str.equals("stories__priority_order")) {
                    c = ' ';
                    break;
                }
                c = 65535;
                break;
            case 1080006662:
                if (str.equals("stories__links_and_formatting")) {
                    c = '!';
                    break;
                }
                c = 65535;
                break;
            case 1219849581:
                if (str.equals("advanced_chat_management")) {
                    c = '\"';
                    break;
                }
                c = 65535;
                break;
            case 1438966047:
                if (str.equals("stories__permanent_views_history")) {
                    c = '#';
                    break;
                }
                c = 65535;
                break;
            case 1537309393:
                if (str.equals("saved_tags")) {
                    c = '$';
                    break;
                }
                c = 65535;
                break;
            case 1832801148:
                if (str.equals("app_icons")) {
                    c = '%';
                    break;
                }
                c = 65535;
                break;
            case 2013274756:
                if (str.equals("last_seen")) {
                    c = '&';
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return 7;
            case 1:
                return 4;
            case 2:
                return 21;
            case 3:
                return 33;
            case 4:
                return 30;
            case 5:
                return 36;
            case 6:
                return 37;
            case 7:
                return 25;
            case '\b':
                return 18;
            case '\t':
                return 14;
            case '\n':
                return 38;
            case 11:
                return 29;
            case '\f':
                return 23;
            case '\r':
                return 8;
            case 14:
                return 22;
            case 15:
                return 27;
            case 16:
                return 11;
            case 17:
                return 13;
            case 18:
                return 28;
            case 19:
                return 3;
            case 20:
                return 1;
            case 21:
                return 12;
            case 22:
                return 6;
            case 23:
                return 2;
            case 24:
                return 32;
            case 25:
                return 5;
            case 26:
                return 0;
            case 27:
                return 35;
            case 28:
                return 17;
            case 29:
                return 15;
            case 30:
                return 31;
            case 31:
                return 34;
            case ' ':
                return 20;
            case '!':
                return 19;
            case '\"':
                return 9;
            case '#':
                return 16;
            case '$':
                return 24;
            case '%':
                return 10;
            case '&':
                return 26;
            default:
                return -1;
        }
    }

    public void updateBackgroundImage() {
        BackgroundView backgroundView;
        if (this.contentView.getMeasuredWidth() == 0 || this.contentView.getMeasuredHeight() == 0 || (backgroundView = this.backgroundView) == null || backgroundView.imageView == null) {
            return;
        }
        if (this.whiteBackground) {
            Bitmap createBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
            new Canvas(createBitmap).drawColor(ColorUtils.blendARGB(getThemedColor(Theme.key_premiumGradient2), getThemedColor(Theme.key_dialogBackground), 0.5f));
            this.backgroundView.imageView.setBackgroundBitmap(createBitmap);
        } else {
            this.gradientTools.gradientMatrix(0, 0, this.contentView.getMeasuredWidth(), this.contentView.getMeasuredHeight(), 0.0f, 0.0f);
            this.gradientCanvas.save();
            this.gradientCanvas.scale(100.0f / this.contentView.getMeasuredWidth(), 100.0f / this.contentView.getMeasuredHeight());
            this.gradientCanvas.drawRect(0.0f, 0.0f, this.contentView.getMeasuredWidth(), this.contentView.getMeasuredHeight(), this.gradientTools.paint);
            this.gradientCanvas.restore();
            this.backgroundView.imageView.setBackgroundBitmap(this.gradientTextureBitmap);
        }
    }

    public void updateButtonText(boolean z) {
        if (this.premiumButtonView != null) {
            if (!getUserConfig().isPremium() || this.currentSubscriptionTier == null || ((SubscriptionTier) this.subscriptionTiers.get(this.selectedTierIndex)).getMonths() >= this.currentSubscriptionTier.getMonths()) {
                if (LocaleController.isRTL) {
                    z = false;
                }
                if (BuildVars.IS_BILLING_UNAVAILABLE) {
                    this.premiumButtonView.setButton(getPremiumButtonText(this.currentAccount, (SubscriptionTier) this.subscriptionTiers.get(this.selectedTierIndex)), new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            PremiumPreviewFragment.this.lambda$updateButtonText$15(view);
                        }
                    }, z);
                    return;
                }
                if (!BuildVars.useInvoiceBilling() && (!BillingController.getInstance().isReady() || this.subscriptionTiers.isEmpty() || this.selectedTierIndex >= this.subscriptionTiers.size() || ((SubscriptionTier) this.subscriptionTiers.get(this.selectedTierIndex)).googlePlayProductDetails == null)) {
                    this.premiumButtonView.setButton(LocaleController.getString(R.string.Loading), new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            PremiumPreviewFragment.lambda$updateButtonText$16(view);
                        }
                    }, z);
                    this.premiumButtonView.setFlickerDisabled(true);
                } else {
                    if (this.subscriptionTiers.isEmpty()) {
                        return;
                    }
                    this.premiumButtonView.setButton(getPremiumButtonText(this.currentAccount, (SubscriptionTier) this.subscriptionTiers.get(this.selectedTierIndex)), new View.OnClickListener() {
                        @Override
                        public final void onClick(View view) {
                            PremiumPreviewFragment.this.lambda$updateButtonText$17(view);
                        }
                    }, z);
                    this.premiumButtonView.setFlickerDisabled(false);
                }
            }
        }
    }

    public void updateColors() {
        ActionBar actionBar;
        if (this.backgroundView == null || (actionBar = this.actionBar) == null) {
            return;
        }
        actionBar.setItemsColor(Theme.getColor(this.whiteBackground ? Theme.key_windowBackgroundWhiteBlackText : Theme.key_premiumGradientBackgroundOverlay), false);
        this.actionBar.setItemsColor(Theme.getColor(this.whiteBackground ? Theme.key_windowBackgroundWhiteBlackText : Theme.key_premiumGradientBackgroundOverlay), true);
        ActionBar actionBar2 = this.actionBar;
        int i = Theme.key_premiumGradientBackgroundOverlay;
        actionBar2.setItemsBackgroundColor(ColorUtils.setAlphaComponent(Theme.getColor(i), 60), false);
        this.particlesView.drawable.updateColors();
        BackgroundView backgroundView = this.backgroundView;
        if (backgroundView != null) {
            backgroundView.titleView.setTextColor(Theme.getColor(this.whiteBackground ? Theme.key_windowBackgroundWhiteBlackText : i));
            TextView textView = this.backgroundView.subtitleView;
            if (this.whiteBackground) {
                i = Theme.key_windowBackgroundWhiteBlackText;
            }
            textView.setTextColor(Theme.getColor(i));
            if (this.backgroundView.imageView != null && this.backgroundView.imageView.mRenderer != null) {
                if (this.whiteBackground) {
                    this.backgroundView.imageView.mRenderer.colorKey1 = Theme.key_premiumCoinGradient1;
                    this.backgroundView.imageView.mRenderer.colorKey2 = Theme.key_premiumCoinGradient2;
                }
                this.backgroundView.imageView.mRenderer.updateColors();
            }
        }
        updateBackgroundImage();
    }

    private void updateDialogVisibility(boolean z) {
        if (z != this.isDialogVisible) {
            this.isDialogVisible = z;
            BackgroundView backgroundView = this.backgroundView;
            if (backgroundView != null && backgroundView.imageView != null) {
                this.backgroundView.imageView.setDialogVisible(z);
            }
            this.particlesView.setPaused(z);
            this.contentView.invalidate();
        }
    }

    private void updateRows() {
        SubscriptionTier subscriptionTier;
        this.sectionRow = -1;
        this.privacyRow = -1;
        this.moreHeaderRow = -1;
        this.moreFeaturesStartRow = -1;
        this.moreFeaturesEndRow = -1;
        this.showAdsHeaderRow = -1;
        this.showAdsRow = -1;
        this.showAdsInfoRow = -1;
        boolean z = true;
        this.rowCount = 1;
        this.paddingRow = 0;
        this.featuresStartRow = 1;
        int size = this.premiumFeatures.size() + 1;
        this.rowCount = size;
        this.featuresEndRow = size;
        if (this.type == 1 && getUserConfig().isPremium()) {
            int i = this.rowCount;
            int i2 = i + 1;
            this.sectionRow = i;
            int i3 = i + 2;
            this.rowCount = i3;
            this.moreHeaderRow = i2;
            this.moreFeaturesStartRow = i3;
            int size2 = i3 + this.morePremiumFeatures.size();
            this.rowCount = size2;
            this.moreFeaturesEndRow = size2;
        }
        int i4 = this.rowCount;
        this.statusRow = i4;
        this.rowCount = i4 + 2;
        this.lastPaddingRow = i4 + 1;
        if (this.type == 1 && getUserConfig().isPremium()) {
            int i5 = this.rowCount;
            this.showAdsHeaderRow = i5;
            this.showAdsRow = i5 + 1;
            this.rowCount = i5 + 3;
            this.showAdsInfoRow = i5 + 2;
        }
        FrameLayout frameLayout = this.buttonContainer;
        if (getUserConfig().isPremium() && ((subscriptionTier = this.currentSubscriptionTier) == null || subscriptionTier.getMonths() >= ((SubscriptionTier) this.subscriptionTiers.get(this.selectedTierIndex)).getMonths() || this.forcePremium)) {
            z = false;
        }
        AndroidUtilities.updateViewVisibilityAnimated(frameLayout, z, 1.0f, false);
        int dp = this.buttonContainer.getVisibility() == 0 ? AndroidUtilities.dp(64.0f) : 0;
        this.layoutManager.setAdditionalHeight((this.statusBarHeight + dp) - AndroidUtilities.dp(16.0f));
        this.layoutManager.setMinimumLastViewHeight(dp);
    }

    @Override
    public boolean canBeginSlide() {
        BackgroundView backgroundView = this.backgroundView;
        return backgroundView == null || backgroundView.imageView == null || !this.backgroundView.imageView.touched;
    }

    @Override
    public View createView(Context context) {
        this.hasOwnBackground = true;
        int color = Theme.getColor(Theme.key_premiumGradient4);
        int color2 = Theme.getColor(Theme.key_premiumGradient3);
        int i = Theme.key_premiumGradient2;
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, 100.0f, new int[]{color, color2, Theme.getColor(i), Theme.getColor(Theme.key_premiumGradient1), Theme.getColor(Theme.key_premiumGradient0)}, new float[]{0.0f, 0.32f, 0.5f, 0.7f, 1.0f}, Shader.TileMode.CLAMP);
        this.shader = linearGradient;
        linearGradient.setLocalMatrix(this.matrix);
        this.gradientPaint.setShader(this.shader);
        this.dummyCell = new PremiumFeatureCell(context);
        this.dummyTierCell = new PremiumTierCell(context);
        this.premiumFeatures.clear();
        this.morePremiumFeatures.clear();
        if (this.type == 0) {
            fillPremiumFeaturesList(this.premiumFeatures, this.currentAccount, false);
        } else {
            fillBusinessFeaturesList(this.premiumFeatures, this.currentAccount, false);
            fillBusinessFeaturesList(this.morePremiumFeatures, this.currentAccount, true);
            QuickRepliesController.getInstance(this.currentAccount).load();
            if (getUserConfig().isPremium()) {
                TLRPC.TL_inputStickerSetShortName tL_inputStickerSetShortName = new TLRPC.TL_inputStickerSetShortName();
                tL_inputStickerSetShortName.short_name = "RestrictedEmoji";
                MediaDataController.getInstance(this.currentAccount).getStickerSet(tL_inputStickerSetShortName, false);
                BusinessChatbotController.getInstance(this.currentAccount).load(null);
                if (getMessagesController().suggestedFilters.isEmpty()) {
                    getMessagesController().loadSuggestedFilters();
                }
                BusinessLinksController.getInstance(this.currentAccount).load(false);
            }
        }
        final Rect rect = new Rect();
        Drawable mutate = context.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = mutate;
        int i2 = Theme.key_dialogBackground;
        mutate.setColorFilter(new PorterDuffColorFilter(getThemedColor(i2), PorterDuff.Mode.MULTIPLY));
        this.shadowDrawable.getPadding(rect);
        if (Build.VERSION.SDK_INT >= 21) {
            this.statusBarHeight = AndroidUtilities.isTablet() ? 0 : AndroidUtilities.statusBarHeight;
        }
        FrameLayout frameLayout = new FrameLayout(context) {
            private final Paint backgroundPaint = new Paint(1);
            boolean iconInterceptedTouch;
            int lastSize;
            boolean listInterceptedTouch;

            @Override
            protected void dispatchDraw(Canvas canvas) {
                float measuredWidth;
                float dp;
                Paint paint;
                if (!PremiumPreviewFragment.this.isDialogVisible) {
                    PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                    if (premiumPreviewFragment.inc) {
                        float f = premiumPreviewFragment.progress + 0.016f;
                        premiumPreviewFragment.progress = f;
                        if (f > 3.0f) {
                            premiumPreviewFragment.inc = false;
                        }
                    } else {
                        float f2 = premiumPreviewFragment.progress - 0.016f;
                        premiumPreviewFragment.progress = f2;
                        if (f2 < 1.0f) {
                            premiumPreviewFragment.inc = true;
                        }
                    }
                }
                View findViewByPosition = PremiumPreviewFragment.this.listView.getLayoutManager() != null ? PremiumPreviewFragment.this.listView.getLayoutManager().findViewByPosition(0) : null;
                PremiumPreviewFragment.this.currentYOffset = findViewByPosition != null ? findViewByPosition.getBottom() : 0;
                int bottom = ((BaseFragment) PremiumPreviewFragment.this).actionBar.getBottom() + AndroidUtilities.dp(16.0f);
                PremiumPreviewFragment.this.totalProgress = 1.0f - ((r4.currentYOffset - bottom) / (PremiumPreviewFragment.this.firstViewHeight - bottom));
                PremiumPreviewFragment premiumPreviewFragment2 = PremiumPreviewFragment.this;
                premiumPreviewFragment2.totalProgress = Utilities.clamp(premiumPreviewFragment2.totalProgress, 1.0f, 0.0f);
                int bottom2 = ((BaseFragment) PremiumPreviewFragment.this).actionBar.getBottom() + AndroidUtilities.dp(16.0f);
                if (PremiumPreviewFragment.this.currentYOffset < bottom2) {
                    PremiumPreviewFragment.this.currentYOffset = bottom2;
                }
                PremiumPreviewFragment premiumPreviewFragment3 = PremiumPreviewFragment.this;
                float f3 = premiumPreviewFragment3.progressToFull;
                premiumPreviewFragment3.progressToFull = 0.0f;
                if (premiumPreviewFragment3.currentYOffset < AndroidUtilities.dp(30.0f) + bottom2) {
                    PremiumPreviewFragment.this.progressToFull = ((bottom2 + AndroidUtilities.dp(30.0f)) - PremiumPreviewFragment.this.currentYOffset) / AndroidUtilities.dp(30.0f);
                }
                PremiumPreviewFragment premiumPreviewFragment4 = PremiumPreviewFragment.this;
                if (premiumPreviewFragment4.isLandscapeMode) {
                    premiumPreviewFragment4.progressToFull = 1.0f;
                    premiumPreviewFragment4.totalProgress = 1.0f;
                }
                if (f3 != premiumPreviewFragment4.progressToFull) {
                    premiumPreviewFragment4.listView.invalidate();
                }
                float max = Math.max((((((((BaseFragment) PremiumPreviewFragment.this).actionBar.getMeasuredHeight() - PremiumPreviewFragment.this.statusBarHeight) - PremiumPreviewFragment.this.backgroundView.titleView.getMeasuredHeight()) / 2.0f) + PremiumPreviewFragment.this.statusBarHeight) - PremiumPreviewFragment.this.backgroundView.getTop()) - PremiumPreviewFragment.this.backgroundView.titleView.getTop(), (PremiumPreviewFragment.this.currentYOffset - ((((BaseFragment) PremiumPreviewFragment.this).actionBar.getMeasuredHeight() + PremiumPreviewFragment.this.backgroundView.getMeasuredHeight()) - PremiumPreviewFragment.this.statusBarHeight)) + AndroidUtilities.dp(PremiumPreviewFragment.this.backgroundView.tierListView.getVisibility() == 0 ? 24.0f : 16.0f));
                float dp2 = ((-max) / 4.0f) + AndroidUtilities.dp(16.0f);
                PremiumPreviewFragment.this.backgroundView.setTranslationY(max);
                PremiumPreviewFragment.this.backgroundView.imageView.setTranslationY(dp2 + AndroidUtilities.dp(PremiumPreviewFragment.this.type == 1 ? 9.0f : 16.0f));
                PremiumPreviewFragment premiumPreviewFragment5 = PremiumPreviewFragment.this;
                float f4 = premiumPreviewFragment5.totalProgress;
                float f5 = ((1.0f - f4) * 0.4f) + 0.6f;
                float f6 = 1.0f - (f4 > 0.5f ? (f4 - 0.5f) / 0.5f : 0.0f);
                premiumPreviewFragment5.backgroundView.imageView.setScaleX(f5);
                PremiumPreviewFragment.this.backgroundView.imageView.setScaleY(f5);
                PremiumPreviewFragment.this.backgroundView.imageView.setAlpha(f6);
                PremiumPreviewFragment.this.backgroundView.subtitleView.setAlpha(f6);
                PremiumPreviewFragment.this.backgroundView.tierListView.setAlpha(f6);
                PremiumPreviewFragment premiumPreviewFragment6 = PremiumPreviewFragment.this;
                premiumPreviewFragment6.particlesView.setAlpha(1.0f - premiumPreviewFragment6.totalProgress);
                PremiumPreviewFragment.this.particlesView.setTranslationY(((-(r0.getMeasuredHeight() - PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredWidth())) / 2.0f) + PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageFrameLayout.getY());
                float dp3 = AndroidUtilities.dp(72.0f) - PremiumPreviewFragment.this.backgroundView.titleView.getLeft();
                PremiumPreviewFragment premiumPreviewFragment7 = PremiumPreviewFragment.this;
                float f7 = premiumPreviewFragment7.totalProgress;
                premiumPreviewFragment7.backgroundView.titleView.setTranslationX(dp3 * (1.0f - CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(1.0f - (f7 > 0.3f ? (f7 - 0.3f) / 0.7f : 0.0f))));
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartX = ((PremiumPreviewFragment.this.backgroundView.getX() + PremiumPreviewFragment.this.backgroundView.imageFrameLayout.getX()) + ((getMeasuredWidth() * 0.1f) * PremiumPreviewFragment.this.progress)) / getMeasuredWidth();
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartY = (PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageFrameLayout.getY()) / getMeasuredHeight();
                if (!PremiumPreviewFragment.this.isDialogVisible) {
                    invalidate();
                }
                PremiumPreviewFragment.this.gradientTools.gradientMatrix(0, 0, getMeasuredWidth(), getMeasuredHeight(), (-getMeasuredWidth()) * 0.1f * PremiumPreviewFragment.this.progress, 0.0f);
                if (PremiumPreviewFragment.this.whiteBackground) {
                    this.backgroundPaint.setColor(ColorUtils.blendARGB(PremiumPreviewFragment.this.getThemedColor(Theme.key_windowBackgroundGray), PremiumPreviewFragment.this.getThemedColor(Theme.key_windowBackgroundWhite), PremiumPreviewFragment.this.progressToFull));
                    measuredWidth = getMeasuredWidth();
                    dp = PremiumPreviewFragment.this.currentYOffset + AndroidUtilities.dp(20.0f);
                    paint = this.backgroundPaint;
                } else {
                    measuredWidth = getMeasuredWidth();
                    dp = PremiumPreviewFragment.this.currentYOffset + AndroidUtilities.dp(20.0f);
                    paint = PremiumPreviewFragment.this.gradientTools.paint;
                }
                canvas.drawRect(0.0f, 0.0f, measuredWidth, dp, paint);
                super.dispatchDraw(canvas);
                if (((BaseFragment) PremiumPreviewFragment.this).parentLayout == null || !PremiumPreviewFragment.this.whiteBackground) {
                    return;
                }
                INavigationLayout iNavigationLayout = ((BaseFragment) PremiumPreviewFragment.this).parentLayout;
                PremiumPreviewFragment premiumPreviewFragment8 = PremiumPreviewFragment.this;
                iNavigationLayout.drawHeaderShadow(canvas, (int) (premiumPreviewFragment8.progressToFull * 255.0f), ((BaseFragment) premiumPreviewFragment8).actionBar.getBottom());
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                float x = PremiumPreviewFragment.this.backgroundView.getX() + PremiumPreviewFragment.this.backgroundView.imageFrameLayout.getX();
                float y = PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageFrameLayout.getY();
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(x, y, (PremiumPreviewFragment.this.backgroundView.imageView == null ? 0 : PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredWidth()) + x, (PremiumPreviewFragment.this.backgroundView.imageView == null ? 0 : PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredHeight()) + y);
                if ((rectF.contains(motionEvent.getX(), motionEvent.getY()) || this.iconInterceptedTouch) && !PremiumPreviewFragment.this.listView.scrollingByUser) {
                    motionEvent.offsetLocation(-x, -y);
                    if (motionEvent.getAction() == 0 || motionEvent.getAction() == 2) {
                        this.iconInterceptedTouch = true;
                    } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                        this.iconInterceptedTouch = false;
                    }
                    PremiumPreviewFragment.this.backgroundView.imageView.dispatchTouchEvent(motionEvent);
                    return true;
                }
                float x2 = PremiumPreviewFragment.this.backgroundView.getX() + PremiumPreviewFragment.this.backgroundView.tierListView.getX();
                float y2 = PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.tierListView.getY();
                rectF.set(x2, y2, PremiumPreviewFragment.this.backgroundView.tierListView.getWidth() + x2, PremiumPreviewFragment.this.backgroundView.tierListView.getHeight() + y2);
                if (PremiumPreviewFragment.this.progressToFull < 1.0f && ((rectF.contains(motionEvent.getX(), motionEvent.getY()) || this.listInterceptedTouch) && !PremiumPreviewFragment.this.listView.scrollingByUser)) {
                    motionEvent.offsetLocation(-x2, -y2);
                    if (motionEvent.getAction() == 0) {
                        this.listInterceptedTouch = true;
                    } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                        this.listInterceptedTouch = false;
                    }
                    PremiumPreviewFragment.this.backgroundView.tierListView.dispatchTouchEvent(motionEvent);
                    if (this.listInterceptedTouch) {
                        return true;
                    }
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            @Override
            protected boolean drawChild(Canvas canvas, View view, long j) {
                if (view != PremiumPreviewFragment.this.listView) {
                    return super.drawChild(canvas, view, j);
                }
                canvas.save();
                canvas.clipRect(0, ((BaseFragment) PremiumPreviewFragment.this).actionBar.getBottom(), getMeasuredWidth(), getMeasuredHeight());
                super.drawChild(canvas, view, j);
                canvas.restore();
                return true;
            }

            @Override
            protected void onLayout(boolean z, int i3, int i4, int i5, int i6) {
                super.onLayout(z, i3, i4, i5, i6);
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientScaleX = PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredWidth() / getMeasuredWidth();
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientScaleY = PremiumPreviewFragment.this.backgroundView.imageView.getMeasuredHeight() / getMeasuredHeight();
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartX = (PremiumPreviewFragment.this.backgroundView.getX() + PremiumPreviewFragment.this.backgroundView.imageView.getX()) / getMeasuredWidth();
                PremiumPreviewFragment.this.backgroundView.imageView.mRenderer.gradientStartY = (PremiumPreviewFragment.this.backgroundView.getY() + PremiumPreviewFragment.this.backgroundView.imageView.getY()) / getMeasuredHeight();
            }

            @Override
            protected void onMeasure(int i3, int i4) {
                int i5 = 0;
                if (View.MeasureSpec.getSize(i3) > View.MeasureSpec.getSize(i4)) {
                    PremiumPreviewFragment.this.isLandscapeMode = true;
                } else {
                    PremiumPreviewFragment.this.isLandscapeMode = false;
                }
                if (Build.VERSION.SDK_INT >= 21) {
                    PremiumPreviewFragment.this.statusBarHeight = AndroidUtilities.isTablet() ? 0 : AndroidUtilities.statusBarHeight;
                }
                PremiumPreviewFragment.this.backgroundView.measure(i3, View.MeasureSpec.makeMeasureSpec(0, 0));
                PremiumPreviewFragment.this.particlesView.getLayoutParams().height = PremiumPreviewFragment.this.backgroundView.getMeasuredHeight();
                if (PremiumPreviewFragment.this.buttonContainer != null && PremiumPreviewFragment.this.buttonContainer.getVisibility() != 8) {
                    i5 = AndroidUtilities.dp(68.0f);
                }
                PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                premiumPreviewFragment.layoutManager.setAdditionalHeight((premiumPreviewFragment.statusBarHeight + i5) - AndroidUtilities.dp(16.0f));
                PremiumPreviewFragment.this.layoutManager.setMinimumLastViewHeight(i5);
                super.onMeasure(i3, i4);
                if (this.lastSize != ((getMeasuredHeight() + getMeasuredWidth()) << 16)) {
                    PremiumPreviewFragment.this.updateBackgroundImage();
                }
            }

            @Override
            protected void onSizeChanged(int i3, int i4, int i5, int i6) {
                super.onSizeChanged(i3, i4, i5, i6);
                PremiumPreviewFragment.this.measureGradient(i3, i4);
            }
        };
        this.contentView = frameLayout;
        frameLayout.setFitsSystemWindows(true);
        RecyclerListView recyclerListView = new RecyclerListView(context) {
            @Override
            public void onDraw(Canvas canvas) {
                Drawable drawable = PremiumPreviewFragment.this.shadowDrawable;
                float f = -rect.left;
                float dp = AndroidUtilities.dp(16.0f);
                PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                drawable.setBounds((int) (f - (dp * premiumPreviewFragment.progressToFull)), (premiumPreviewFragment.currentYOffset - rect.top) - AndroidUtilities.dp(16.0f), (int) (getMeasuredWidth() + rect.right + (AndroidUtilities.dp(16.0f) * PremiumPreviewFragment.this.progressToFull)), getMeasuredHeight());
                PremiumPreviewFragment.this.shadowDrawable.draw(canvas);
                super.onDraw(canvas);
            }
        };
        this.listView = recyclerListView;
        FillLastLinearLayoutManager fillLastLinearLayoutManager = new FillLastLinearLayoutManager(context, (AndroidUtilities.dp(68.0f) + this.statusBarHeight) - AndroidUtilities.dp(16.0f), this.listView);
        this.layoutManager = fillLastLinearLayoutManager;
        recyclerListView.setLayoutManager(fillLastLinearLayoutManager);
        this.layoutManager.setFixedLastItemHeight();
        this.listView.setAdapter(new Adapter());
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i3) {
                super.onScrollStateChanged(recyclerView, i3);
                if (i3 == 0) {
                    int bottom = ((BaseFragment) PremiumPreviewFragment.this).actionBar.getBottom() + AndroidUtilities.dp(16.0f);
                    PremiumPreviewFragment premiumPreviewFragment = PremiumPreviewFragment.this;
                    if (premiumPreviewFragment.totalProgress > 0.5f) {
                        premiumPreviewFragment.listView.smoothScrollBy(0, premiumPreviewFragment.currentYOffset - bottom);
                    } else {
                        View findViewByPosition = premiumPreviewFragment.listView.getLayoutManager() != null ? PremiumPreviewFragment.this.listView.getLayoutManager().findViewByPosition(0) : null;
                        if (findViewByPosition != null && findViewByPosition.getTop() < 0) {
                            PremiumPreviewFragment.this.listView.smoothScrollBy(0, findViewByPosition.getTop());
                        }
                    }
                }
                PremiumPreviewFragment.this.checkButtonDivider();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i3, int i4) {
                super.onScrolled(recyclerView, i3, i4);
                PremiumPreviewFragment.this.contentView.invalidate();
                PremiumPreviewFragment.this.checkButtonDivider();
            }
        });
        this.backgroundView = new BackgroundView(context) {
            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return true;
            }
        };
        StarParticlesView starParticlesView = new StarParticlesView(context);
        this.particlesView = starParticlesView;
        starParticlesView.setClipWithGradient();
        if (this.type == 1) {
            if (this.whiteBackground) {
                StarParticlesView.Drawable drawable = this.particlesView.drawable;
                drawable.useGradient = true;
                drawable.useBlur = false;
                drawable.checkBounds = true;
                drawable.isCircle = true;
                drawable.centerOffsetY = AndroidUtilities.dp(-14.0f);
                StarParticlesView.Drawable drawable2 = this.particlesView.drawable;
                drawable2.minLifeTime = 2000L;
                drawable2.randLifeTime = 3000;
                drawable2.size1 = 16;
                drawable2.useRotate = false;
                drawable2.type = 28;
                drawable2.colorKey = i;
            } else {
                StarParticlesView.Drawable drawable3 = this.particlesView.drawable;
                drawable3.isCircle = true;
                drawable3.centerOffsetY = AndroidUtilities.dp(28.0f);
                StarParticlesView.Drawable drawable4 = this.particlesView.drawable;
                drawable4.minLifeTime = 2000L;
                drawable4.randLifeTime = 3000;
                drawable4.size1 = 16;
                drawable4.useRotate = false;
                drawable4.type = 28;
            }
        }
        this.backgroundView.imageView.setStarParticlesView(this.particlesView);
        this.contentView.addView(this.particlesView, LayoutHelper.createFrame(-1, -2.0f));
        this.contentView.addView(this.backgroundView, LayoutHelper.createFrame(-1, -2.0f));
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i3) {
                PremiumPreviewFragment.this.lambda$createView$3(view, i3);
            }
        });
        this.contentView.addView(this.listView);
        this.premiumButtonView = new PremiumButtonView(context, false, getResourceProvider());
        updateButtonText(false);
        this.buttonContainer = new FrameLayout(context);
        View view = new View(context);
        this.buttonDivider = view;
        view.setBackgroundColor(Theme.getColor(Theme.key_divider));
        this.buttonContainer.addView(this.buttonDivider, LayoutHelper.createFrame(-1, 1.0f));
        this.buttonDivider.getLayoutParams().height = 1;
        AndroidUtilities.updateViewVisibilityAnimated(this.buttonDivider, true, 1.0f, false);
        this.buttonContainer.addView(this.premiumButtonView, LayoutHelper.createFrame(-1, 48.0f, 16, 16.0f, 0.0f, 16.0f, 0.0f));
        this.buttonContainer.setBackgroundColor(getThemedColor(i2));
        this.contentView.addView(this.buttonContainer, LayoutHelper.createFrame(-1, 68, 80));
        this.fragmentView = this.contentView;
        this.actionBar.setBackground(null);
        this.actionBar.setCastShadows(false);
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i3) {
                if (i3 == -1) {
                    PremiumPreviewFragment.this.lambda$onBackPressed$319();
                }
            }
        });
        this.actionBar.setForceSkipTouches(true);
        updateColors();
        updateRows();
        this.backgroundView.imageView.startEnterAnimation(-180, 200L);
        if (this.forcePremium) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PremiumPreviewFragment.this.lambda$createView$4();
                }
            }, 400L);
        }
        MediaDataController.getInstance(this.currentAccount).preloadPremiumPreviewStickers();
        sentShowScreenStat(this.source);
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.billingProductDetailsUpdated || i == NotificationCenter.premiumPromoUpdated) {
            updateButtonText(false);
            this.backgroundView.updatePremiumTiers();
        }
        if (i == NotificationCenter.currentUserPremiumStatusChanged || i == NotificationCenter.premiumPromoUpdated) {
            this.backgroundView.updateText();
            this.backgroundView.updatePremiumTiers();
            updateRows();
            this.listView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public ArrayList getThemeDescriptions() {
        return SimpleThemeDescription.createThemeDescriptions(new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                PremiumPreviewFragment.this.updateColors();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        }, Theme.key_premiumGradient1, Theme.key_premiumGradient2, Theme.key_premiumGradient3, Theme.key_premiumGradient4, Theme.key_premiumGradientBackground1, Theme.key_premiumGradientBackground2, Theme.key_premiumGradientBackground3, Theme.key_premiumGradientBackground4, Theme.key_premiumGradientBackgroundOverlay, Theme.key_premiumStarGradient1, Theme.key_premiumStarGradient2, Theme.key_premiumStartSmallStarsColor, Theme.key_premiumStartSmallStarsColor2);
    }

    @Override
    public boolean isLightStatusBar() {
        return this.whiteBackground;
    }

    @Override
    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public boolean onBackPressed() {
        if (this.settingsView == null) {
            return super.onBackPressed();
        }
        closeSetting();
        return false;
    }

    @Override
    public void onDialogDismiss(Dialog dialog) {
        super.onDialogDismiss(dialog);
        updateDialogVisibility(false);
    }

    @Override
    public boolean onFragmentCreate() {
        if (getMessagesController().premiumFeaturesBlocked()) {
            return false;
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        getNotificationCenter().addObserver(this, NotificationCenter.premiumPromoUpdated);
        if (getMediaDataController().getPremiumPromo() != null) {
            Iterator<TLRPC.Document> it = getMediaDataController().getPremiumPromo().videos.iterator();
            while (it.hasNext()) {
                FileLoader.getInstance(this.currentAccount).loadFile(it.next(), getMediaDataController().getPremiumPromo(), 3, 0);
            }
        }
        if (this.type == 1) {
            TimezonesController.getInstance(this.currentAccount).load();
        }
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        getNotificationCenter().removeObserver(this, NotificationCenter.premiumPromoUpdated);
    }

    @Override
    public void onPause() {
        super.onPause();
        BackgroundView backgroundView = this.backgroundView;
        if (backgroundView != null && backgroundView.imageView != null) {
            this.backgroundView.imageView.setDialogVisible(true);
        }
        StarParticlesView starParticlesView = this.particlesView;
        if (starParticlesView != null) {
            starParticlesView.setPaused(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BackgroundView backgroundView = this.backgroundView;
        if (backgroundView != null && backgroundView.imageView != null) {
            this.backgroundView.imageView.setPaused(false);
            this.backgroundView.imageView.setDialogVisible(false);
        }
        this.particlesView.setPaused(false);
    }

    public PremiumPreviewFragment setForcePremium() {
        this.forcePremium = true;
        return this;
    }

    public PremiumPreviewFragment setSelectAnnualByDefault() {
        this.selectAnnualByDefault = true;
        return this;
    }

    @Override
    public Dialog showDialog(Dialog dialog) {
        Dialog showDialog = super.showDialog(dialog);
        updateDialogVisibility(showDialog != null);
        return showDialog;
    }

    public void showSelectStatusDialog(org.telegram.ui.PremiumFeatureCell r24, java.lang.Long r25, final org.telegram.messenger.Utilities.Callback2 r26) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.PremiumPreviewFragment.showSelectStatusDialog(org.telegram.ui.PremiumFeatureCell, java.lang.Long, org.telegram.messenger.Utilities$Callback2):void");
    }
}
