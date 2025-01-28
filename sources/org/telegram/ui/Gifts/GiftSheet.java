package org.telegram.ui.Gifts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BirthdayController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BottomSheetWithRecyclerListView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CompatDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.ExtendedGridLayoutManager;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$GiftTier;
import org.telegram.ui.Components.Premium.PremiumLockIconView;
import org.telegram.ui.Components.Premium.boosts.BoostRepository;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stars.ExplainStarsSheet;
import org.telegram.ui.Stars.StarGiftPatterns;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stars.StarsReactionsSheet;
import org.telegram.ui.Stories.recorder.HintView2;

public class GiftSheet extends BottomSheetWithRecyclerListView implements NotificationCenter.NotificationCenterDelegate {
    private final int TAB_ALL;
    private final int TAB_IN_STOCK;
    private final int TAB_LIMITED;
    private UniversalAdapter adapter;
    private boolean birthday;
    private final Runnable closeParentSheet;
    private final int currentAccount;
    private final long dialogId;
    private final DefaultItemAnimator itemAnimator;
    private final ExtendedGridLayoutManager layoutManager;
    private final String name;
    private List options;
    private final FrameLayout premiumHeaderView;
    private final ArrayList premiumTiers;
    private int selectedTab;
    private final boolean self;
    private final LinearLayout starsHeaderView;
    private final ArrayList tabs;

    public static class CardBackground extends Drawable {
        private AnimatedFloat animatedSelected;
        private TL_stars.starGiftAttributeBackdrop backdrop;
        private final Path clipPath;
        private RadialGradient gradient;
        private final Matrix gradientMatrix;
        private int gradientRadius;
        public final Paint paint;
        private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable pattern;
        private final RectF rect;
        private final Theme.ResourcesProvider resourcesProvider;
        private boolean selected;
        private final Paint selectedPaint;
        private final View view;

        public CardBackground(View view, Theme.ResourcesProvider resourcesProvider, boolean z) {
            Paint paint = new Paint(1);
            this.paint = paint;
            this.rect = new RectF();
            this.clipPath = new Path();
            this.gradientMatrix = new Matrix();
            Paint paint2 = new Paint(1);
            this.selectedPaint = paint2;
            this.animatedSelected = new AnimatedFloat(new Runnable() {
                @Override
                public final void run() {
                    GiftSheet.CardBackground.this.invalidate();
                }
            }, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.view = view;
            this.resourcesProvider = resourcesProvider;
            this.pattern = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(view, AndroidUtilities.dp(28.0f)) {
                @Override
                public void invalidate() {
                    super.invalidate();
                    if (CardBackground.this.getCallback() != null) {
                        CardBackground.this.getCallback().invalidateDrawable(CardBackground.this);
                    }
                }
            };
            view.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                @Override
                public void onViewAttachedToWindow(View view2) {
                    CardBackground.this.pattern.attach();
                }

                @Override
                public void onViewDetachedFromWindow(View view2) {
                    CardBackground.this.pattern.detach();
                }
            });
            if (view.isAttachedToWindow()) {
                this.pattern.attach();
            }
            paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider));
            if (z) {
                paint.setShadowLayer(AndroidUtilities.dp(1.66f), 0.0f, AndroidUtilities.dp(0.33f), Theme.getColor(Theme.key_dialogCardShadow, resourcesProvider));
            }
            paint2.setStyle(Paint.Style.STROKE);
        }

        @Override
        public void draw(Canvas canvas) {
            Paint paint;
            RadialGradient radialGradient;
            Rect bounds = getBounds();
            float f = this.animatedSelected.set(this.selected);
            this.rect.set(bounds);
            this.rect.inset(AndroidUtilities.dp(3.33f), AndroidUtilities.dp(4.0f));
            if (this.backdrop != null) {
                int lerp = AndroidUtilities.lerp(Math.min(bounds.width(), bounds.height()), Math.max(bounds.width(), bounds.height()), 0.35f);
                if (this.gradient == null || this.gradientRadius != lerp) {
                    this.gradientRadius = lerp;
                    float f2 = lerp;
                    TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop = this.backdrop;
                    this.gradient = new RadialGradient(0.0f, 0.0f, f2, new int[]{stargiftattributebackdrop.center_color | (-16777216), stargiftattributebackdrop.edge_color | (-16777216)}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                }
                this.gradientMatrix.reset();
                this.gradientMatrix.postTranslate(bounds.centerX(), Math.min(AndroidUtilities.dp(50.0f), bounds.centerY()));
                this.gradient.setLocalMatrix(this.gradientMatrix);
                paint = this.paint;
                radialGradient = this.gradient;
            } else {
                paint = this.paint;
                radialGradient = null;
            }
            paint.setShader(radialGradient);
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f), this.paint);
            if (this.backdrop != null && !this.pattern.isEmpty()) {
                this.pattern.setColor(Integer.valueOf((-16777216) | this.backdrop.pattern_color));
                canvas.save();
                this.clipPath.rewind();
                this.clipPath.addRoundRect(this.rect, AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f), Path.Direction.CW);
                canvas.clipPath(this.clipPath);
                canvas.translate(bounds.centerX(), bounds.centerY());
                float lerp2 = AndroidUtilities.lerp(1.0f, 0.925f, f);
                canvas.scale(lerp2, lerp2);
                StarGiftPatterns.drawPattern(canvas, 2, this.pattern, bounds.width(), bounds.height(), 1.0f, 1.0f);
                canvas.restore();
            }
            if (f > 0.0f) {
                this.selectedPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite, this.resourcesProvider));
                this.selectedPaint.setStrokeWidth(AndroidUtilities.dpf2(2.33f));
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(this.rect);
                float lerp3 = AndroidUtilities.lerp(-AndroidUtilities.dpf2(2.33f), AndroidUtilities.dp(5.166f), f);
                rectF.inset(lerp3, lerp3);
                float lerp4 = AndroidUtilities.lerp(AndroidUtilities.dpf2(11.0f), AndroidUtilities.dpf2(6.66f), f);
                canvas.drawRoundRect(rectF, lerp4, lerp4, this.selectedPaint);
            }
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public boolean getPadding(Rect rect) {
            rect.set(AndroidUtilities.dp(3.33f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(3.33f), AndroidUtilities.dp(4.0f));
            return true;
        }

        public void invalidate() {
            this.view.invalidate();
            if (getCallback() != null) {
                getCallback().invalidateDrawable(this);
            }
        }

        @Override
        public void setAlpha(int i) {
        }

        public void setBackdrop(TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop) {
            if (this.backdrop != stargiftattributebackdrop) {
                this.gradient = null;
            }
            this.backdrop = stargiftattributebackdrop;
            invalidate();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public void setPattern(TL_stars.starGiftAttributePattern stargiftattributepattern) {
            if (stargiftattributepattern == null) {
                this.pattern.set((Drawable) null, false);
            } else {
                this.pattern.set(stargiftattributepattern.document, false);
            }
        }

        public void setSelected(boolean z, boolean z2) {
            if (this.selected == z) {
                return;
            }
            this.selected = z;
            if (!z2) {
                this.animatedSelected.force(z);
            }
            invalidate();
        }
    }

    public static class GiftCell extends FrameLayout implements ItemOptions.ScrimView {
        private final AvatarDrawable avatarDrawable;
        private final BackupImageView avatarView;
        private Runnable cancel;
        private final FrameLayout card;
        private final CardBackground cardBackground;
        private final Rect cardBackgroundPadding;
        private final int currentAccount;
        private final BackupImageView imageView;
        private final FrameLayout.LayoutParams imageViewLayoutParams;
        private TLRPC.Document lastDocument;
        private long lastDocumentId;
        private GiftPremiumBottomSheet$GiftTier lastTier;
        private TL_stars.SavedStarGift lastUserGift;
        private final PremiumLockIconView lockView;
        private final TextView priceView;
        private final Theme.ResourcesProvider resourcesProvider;
        private final Ribbon ribbon;
        private final TextView subtitleView;
        private final TextView titleView;

        public static class Factory extends UItem.UItemFactory {
            static {
                UItem.UItemFactory.setup(new Factory());
            }

            public static UItem asPremiumGift(GiftPremiumBottomSheet$GiftTier giftPremiumBottomSheet$GiftTier) {
                UItem spanCount = UItem.ofFactory(Factory.class).setSpanCount(1);
                spanCount.object = giftPremiumBottomSheet$GiftTier;
                return spanCount;
            }

            public static UItem asStarGift(int i, TL_stars.SavedStarGift savedStarGift, boolean z) {
                UItem spanCount = UItem.ofFactory(Factory.class).setSpanCount(1);
                spanCount.intValue = i;
                spanCount.object = savedStarGift;
                spanCount.accent = z;
                return spanCount;
            }

            public static UItem asStarGift(int i, TL_stars.StarGift starGift) {
                UItem spanCount = UItem.ofFactory(Factory.class).setSpanCount(1);
                spanCount.intValue = i;
                spanCount.object = starGift;
                return spanCount;
            }

            @Override
            public void bindView(View view, UItem uItem, boolean z) {
                Object obj = uItem.object;
                if (obj instanceof GiftPremiumBottomSheet$GiftTier) {
                    ((GiftCell) view).setPremiumGift((GiftPremiumBottomSheet$GiftTier) obj);
                    return;
                }
                if (obj instanceof TL_stars.StarGift) {
                    ((GiftCell) view).setStarsGift((TL_stars.StarGift) obj);
                } else if (obj instanceof TL_stars.SavedStarGift) {
                    ((GiftCell) view).setStarsGift((TL_stars.SavedStarGift) obj, uItem.accent);
                }
            }

            @Override
            public GiftCell createView(Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
                return new GiftCell(context, i, resourcesProvider);
            }

            @Override
            public boolean equals(UItem uItem, UItem uItem2) {
                if (uItem.accent != uItem2.accent) {
                    return false;
                }
                Object obj = uItem.object;
                if (obj != null || uItem2.object != null) {
                    if (obj instanceof GiftPremiumBottomSheet$GiftTier) {
                        return obj == uItem2.object;
                    }
                    if (obj instanceof TL_stars.StarGift) {
                        Object obj2 = uItem2.object;
                        if (obj2 instanceof TL_stars.StarGift) {
                            return ((TL_stars.StarGift) obj).id == ((TL_stars.StarGift) obj2).id;
                        }
                    }
                    if (obj instanceof TL_stars.SavedStarGift) {
                        Object obj3 = uItem2.object;
                        if (obj3 instanceof TL_stars.SavedStarGift) {
                            return ((TL_stars.SavedStarGift) obj).gift.id == ((TL_stars.SavedStarGift) obj3).gift.id;
                        }
                    }
                }
                return uItem.intValue == uItem2.intValue && uItem.checked == uItem2.checked && uItem.longValue == uItem2.longValue && TextUtils.equals(uItem.text, uItem2.text);
            }
        }

        public GiftCell(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.cardBackgroundPadding = new Rect();
            this.currentAccount = i;
            this.resourcesProvider = resourcesProvider;
            ScaleStateListAnimator.apply(this, 0.04f, 1.5f);
            FrameLayout frameLayout = new FrameLayout(context);
            this.card = frameLayout;
            CardBackground cardBackground = new CardBackground(frameLayout, resourcesProvider, true);
            this.cardBackground = cardBackground;
            frameLayout.setBackground(cardBackground);
            addView(frameLayout, LayoutHelper.createFrame(-1, -1, 119));
            Ribbon ribbon = new Ribbon(context);
            this.ribbon = ribbon;
            addView(ribbon, LayoutHelper.createFrame(-2, -2.0f, 53, 0.0f, 2.0f, 1.0f, 0.0f));
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            backupImageView.getImageReceiver().setAutoRepeat(0);
            FrameLayout.LayoutParams createFrame = LayoutHelper.createFrame(96, 96.0f, 49, 0.0f, 2.0f, 0.0f, 2.0f);
            this.imageViewLayoutParams = createFrame;
            frameLayout.addView(backupImageView, createFrame);
            PremiumLockIconView premiumLockIconView = new PremiumLockIconView(context, PremiumLockIconView.TYPE_GIFT_LOCK, resourcesProvider);
            this.lockView = premiumLockIconView;
            premiumLockIconView.setImageReceiver(backupImageView.getImageReceiver());
            frameLayout.addView(premiumLockIconView, LayoutHelper.createFrame(30, 30.0f, 49, 0.0f, 38.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            int i2 = Theme.key_windowBackgroundWhiteBlackText;
            textView.setTextColor(Theme.getColor(i2, resourcesProvider));
            textView.setGravity(17);
            textView.setTextSize(1, 14.0f);
            textView.setTypeface(AndroidUtilities.bold());
            frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 48, 0.0f, 93.0f, 0.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.subtitleView = textView2;
            textView2.setTextColor(Theme.getColor(i2, resourcesProvider));
            textView2.setGravity(17);
            textView2.setTextSize(1, 12.0f);
            frameLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 48, 0.0f, 111.0f, 0.0f, 0.0f));
            TextView textView3 = new TextView(context);
            this.priceView = textView3;
            textView3.setTextSize(1, 12.0f);
            textView3.setTypeface(AndroidUtilities.bold());
            textView3.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            textView3.setGravity(17);
            textView3.setBackground(new StarsBackground(false));
            textView3.setTextColor(-13397548);
            frameLayout.addView(textView3, LayoutHelper.createFrame(-2, 26.0f, 49, 0.0f, 133.0f, 0.0f, 11.0f));
            this.avatarDrawable = new AvatarDrawable();
            BackupImageView backupImageView2 = new BackupImageView(context);
            this.avatarView = backupImageView2;
            backupImageView2.setRoundRadius(AndroidUtilities.dp(20.0f));
            backupImageView2.setVisibility(8);
            frameLayout.addView(backupImageView2, LayoutHelper.createFrame(20, 20.0f, 51, 2.0f, 2.0f, 2.0f, 2.0f));
        }

        private void setSticker(TLRPC.Document document, Object obj) {
            if (document == null) {
                this.imageView.clearImage();
                this.lastDocument = null;
                this.lastDocumentId = 0L;
            } else {
                if (this.lastDocument == document) {
                    return;
                }
                this.lastDocument = document;
                this.lastDocumentId = document.id;
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, AndroidUtilities.dp(100.0f));
                this.imageView.setImage(ImageLocation.getForDocument(document), "100_100", ImageLocation.getForDocument(closestPhotoSizeWithSize, document), "100_100", DocumentObject.getSvgThumb(document, Theme.key_windowBackgroundGray, 0.3f), obj);
            }
        }

        @Override
        public void drawScrim(Canvas canvas, float f) {
            ItemOptions.ScrimView.CC.$default$drawScrim(this, canvas, f);
        }

        @Override
        public void getBounds(RectF rectF) {
            this.cardBackground.getPadding(this.cardBackgroundPadding);
            Rect rect = this.cardBackgroundPadding;
            rectF.set(rect.left, rect.top, getWidth() - this.cardBackgroundPadding.right, getHeight() - this.cardBackgroundPadding.bottom);
        }

        public void setPremiumGift(GiftPremiumBottomSheet$GiftTier giftPremiumBottomSheet$GiftTier) {
            int months = giftPremiumBottomSheet$GiftTier.getMonths();
            int i = 3;
            if (months <= 3) {
                i = 2;
            } else if (months > 6) {
                i = 4;
            }
            if (this.lastTier != giftPremiumBottomSheet$GiftTier) {
                BackupImageView backupImageView = this.imageView;
                Runnable giftImage = StarsIntroActivity.setGiftImage((View) backupImageView, backupImageView.getImageReceiver(), i);
                this.cancel = giftImage;
                if (giftImage != null) {
                    giftImage.run();
                    this.cancel = null;
                }
            }
            this.cardBackground.setBackdrop(null);
            this.cardBackground.setPattern(null);
            this.titleView.setText(LocaleController.formatPluralString("Gift2Months", months, new Object[0]));
            this.subtitleView.setText(LocaleController.getString(R.string.TelegramPremiumShort));
            this.titleView.setVisibility(0);
            this.subtitleView.setVisibility(0);
            this.imageView.setTranslationY(-AndroidUtilities.dp(8.0f));
            this.avatarView.setVisibility(8);
            this.lockView.setVisibility(8);
            if (giftPremiumBottomSheet$GiftTier.getDiscount() > 0) {
                this.ribbon.setVisibility(0);
                this.ribbon.setBackdrop(null);
                this.ribbon.setColors(-2535425, -8229377);
                this.ribbon.setText(12, LocaleController.formatString(R.string.GiftPremiumOptionDiscount, Integer.valueOf(giftPremiumBottomSheet$GiftTier.getDiscount())), true);
            } else {
                this.ribbon.setVisibility(8);
                this.ribbon.setBackdrop(null);
            }
            this.priceView.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            this.priceView.setTextSize(1, 12.0f);
            this.priceView.setText(giftPremiumBottomSheet$GiftTier.getFormattedPrice());
            this.priceView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(13.0f), 422810068));
            this.priceView.setTextColor(-13397548);
            ((ViewGroup.MarginLayoutParams) this.priceView.getLayoutParams()).topMargin = AndroidUtilities.dp(133.0f);
            this.lastTier = giftPremiumBottomSheet$GiftTier;
            this.lastDocument = null;
        }

        public void setStarsGift(org.telegram.tgnet.tl.TL_stars.SavedStarGift r12, boolean r13) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Gifts.GiftSheet.GiftCell.setStarsGift(org.telegram.tgnet.tl.TL_stars$SavedStarGift, boolean):void");
        }

        public void setStarsGift(TL_stars.StarGift starGift) {
            Runnable runnable = this.cancel;
            if (runnable != null) {
                runnable.run();
                this.cancel = null;
            }
            setSticker(starGift.getDocument(), starGift);
            this.cardBackground.setBackdrop((TL_stars.starGiftAttributeBackdrop) StarsController.findAttribute(starGift.attributes, TL_stars.starGiftAttributeBackdrop.class));
            this.cardBackground.setPattern((TL_stars.starGiftAttributePattern) StarsController.findAttribute(starGift.attributes, TL_stars.starGiftAttributePattern.class));
            this.titleView.setVisibility(8);
            this.subtitleView.setVisibility(8);
            this.imageView.setTranslationY(0.0f);
            this.lockView.setVisibility(8);
            boolean z = starGift.limited;
            if (z && starGift.availability_remains <= 0) {
                this.ribbon.setVisibility(0);
                this.ribbon.setColor(Theme.getColor(Theme.key_gift_ribbon_soldout, this.resourcesProvider));
                this.ribbon.setBackdrop(null);
                this.ribbon.setText(LocaleController.getString(R.string.Gift2SoldOut), true);
            } else if (z) {
                this.ribbon.setVisibility(0);
                this.ribbon.setColor(Theme.getColor(Theme.key_gift_ribbon, this.resourcesProvider));
                this.ribbon.setBackdrop(null);
                this.ribbon.setText(LocaleController.getString(R.string.Gift2LimitedRibbon), false);
            } else {
                this.ribbon.setBackdrop(null);
                this.ribbon.setVisibility(8);
            }
            this.avatarView.setVisibility(8);
            this.priceView.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(10.0f), 0);
            this.priceView.setTextSize(1, 12.0f);
            this.priceView.setText(StarsIntroActivity.replaceStarsWithPlain("XTR " + LocaleController.formatNumber(starGift.stars, ','), 0.71f));
            this.priceView.setBackground(new StarsBackground(starGift instanceof TL_stars.TL_starGiftUnique));
            this.priceView.setTextColor(-4229632);
            ((ViewGroup.MarginLayoutParams) this.priceView.getLayoutParams()).topMargin = AndroidUtilities.dp(103.0f);
            this.lastTier = null;
        }
    }

    public static class Ribbon extends View {
        private RibbonDrawable drawable;

        public Ribbon(Context context) {
            super(context);
            this.drawable = new RibbonDrawable(this, 1.0f);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            this.drawable.setBounds(0, 0, getWidth(), getHeight());
            this.drawable.draw(canvas);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            setMeasuredDimension(AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f));
        }

        public void setBackdrop(TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop) {
            this.drawable.setBackdrop(stargiftattributebackdrop, false);
            invalidate();
        }

        public void setColor(int i) {
            this.drawable.setColor(i);
        }

        public void setColors(int i, int i2) {
            this.drawable.setColors(i, i2);
        }

        public void setText(int i, CharSequence charSequence, boolean z) {
            this.drawable.setText(i, charSequence, z);
        }

        public void setText(CharSequence charSequence, boolean z) {
            this.drawable.setText(z ? 10 : 11, charSequence, z);
        }
    }

    public static class RibbonDrawable extends CompatDrawable {
        private Path path;
        private Text text;
        private int textColor;

        public RibbonDrawable(View view, float f) {
            super(view);
            Path path = new Path();
            this.path = path;
            this.textColor = -1;
            fillRibbonPath(path, f);
            this.paint.setColor(-698031);
            this.paint.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(2.33f)));
        }

        public static void fillRibbonPath(Path path, float f) {
            path.rewind();
            float f2 = 24.5f * f;
            path.moveTo(AndroidUtilities.dp(46.83f * f), AndroidUtilities.dp(f2));
            path.lineTo(AndroidUtilities.dp(23.5f * f), AndroidUtilities.dp(1.17f * f));
            path.cubicTo(AndroidUtilities.dp(22.75f * f), AndroidUtilities.dp(0.42f * f), AndroidUtilities.dp(21.73f * f), 0.0f, AndroidUtilities.dp(20.68f * f), 0.0f);
            float f3 = 0.05f * f;
            path.cubicTo(AndroidUtilities.dp(19.62f * f), 0.0f, AndroidUtilities.dp(2.73f * f), AndroidUtilities.dp(f3), AndroidUtilities.dp(1.55f * f), AndroidUtilities.dp(f3));
            path.cubicTo(AndroidUtilities.dp(0.36f * f), AndroidUtilities.dp(f3), AndroidUtilities.dp((-0.23f) * f), AndroidUtilities.dp(1.4885f * f), AndroidUtilities.dp(0.6f * f), AndroidUtilities.dp(2.32f * f));
            path.lineTo(AndroidUtilities.dp(45.72f * f), AndroidUtilities.dp(47.44f * f));
            float f4 = 48.0f * f;
            path.cubicTo(AndroidUtilities.dp(46.56f * f), AndroidUtilities.dp(48.28f * f), AndroidUtilities.dp(f4), AndroidUtilities.dp(47.68f * f), AndroidUtilities.dp(f4), AndroidUtilities.dp(46.5f * f));
            path.cubicTo(AndroidUtilities.dp(f4), AndroidUtilities.dp(45.31f * f), AndroidUtilities.dp(f4), AndroidUtilities.dp(28.38f * f), AndroidUtilities.dp(f4), AndroidUtilities.dp(27.32f * f));
            path.cubicTo(AndroidUtilities.dp(f4), AndroidUtilities.dp(26.26f * f), AndroidUtilities.dp(47.5f * f), AndroidUtilities.dp(25.24f * f), AndroidUtilities.dp(f * 46.82f), AndroidUtilities.dp(f2));
            path.close();
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            canvas.drawPath(this.path, this.paint);
            if (this.text != null) {
                canvas.save();
                canvas.rotate(45.0f, (getBounds().width() / 2.0f) + AndroidUtilities.dp(6.0f), (getBounds().height() / 2.0f) - AndroidUtilities.dp(6.0f));
                float min = Math.min(1.0f, AndroidUtilities.dp(40.0f) / this.text.getCurrentWidth());
                canvas.scale(min, min, (getBounds().width() / 2.0f) + AndroidUtilities.dp(6.0f), (getBounds().height() / 2.0f) - AndroidUtilities.dp(6.0f));
                this.text.draw(canvas, ((getBounds().width() / 2.0f) + AndroidUtilities.dp(6.0f)) - (this.text.getWidth() / 2.0f), (getBounds().height() / 2.0f) - AndroidUtilities.dp(5.0f), this.textColor, 1.0f);
                canvas.restore();
            }
            canvas.restore();
        }

        public void setBackdrop(TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop, boolean z) {
            if (stargiftattributebackdrop == null) {
                this.paint.setShader(null);
            } else {
                this.paint.setShader(new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f), new int[]{Theme.adaptHSV(stargiftattributebackdrop.center_color | (-16777216), z ? 0.07f : 0.05f, z ? -0.15f : -0.1f), Theme.adaptHSV(stargiftattributebackdrop.edge_color | (-16777216), z ? 0.07f : 0.05f, z ? -0.15f : -0.1f)}, new float[]{z ? 1.0f : 0.0f, z ? 0.0f : 1.0f}, Shader.TileMode.CLAMP));
            }
        }

        public void setColor(int i) {
            this.paint.setShader(null);
            this.paint.setColor(i);
        }

        public void setColors(int i, int i2) {
            this.paint.setShader(new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f), new int[]{i, i2}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
        }

        public void setText(int i, CharSequence charSequence, boolean z) {
            this.text = new Text(charSequence, i, z ? AndroidUtilities.bold() : null);
        }

        public void setTextColor(int i) {
            this.textColor = i;
        }
    }

    public static class StarsBackground extends Drawable {
        public final Paint backgroundPaint;
        public final StarsReactionsSheet.Particles particles;
        private final boolean white;
        public final RectF rectF = new RectF();
        public final Path path = new Path();

        public StarsBackground(boolean z) {
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            this.particles = new StarsReactionsSheet.Particles(1, 25);
            this.white = z;
            paint.setColor(z ? 1090519039 : 1088989954);
        }

        @Override
        public void draw(Canvas canvas) {
            float min = Math.min(getBounds().width(), getBounds().height()) / 2.0f;
            this.rectF.set(getBounds());
            this.path.rewind();
            this.path.addRoundRect(this.rectF, min, min, Path.Direction.CW);
            canvas.drawPath(this.path, this.backgroundPaint);
            canvas.save();
            canvas.clipPath(this.path);
            this.particles.setBounds(this.rectF);
            this.particles.process();
            this.particles.draw(canvas, this.white ? -1 : -1009635);
            canvas.restore();
            invalidateSelf();
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void setAlpha(int i) {
            this.backgroundPaint.setAlpha(i);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            this.backgroundPaint.setColorFilter(colorFilter);
        }
    }

    public static class Tabs extends HorizontalScrollView {
        private AnimatedFloat animatedSelected;
        private final RectF ceiledRect;
        private final RectF flooredRect;
        private int lastId;
        private final LinearLayout layout;
        private final Theme.ResourcesProvider resourcesProvider;
        private int selected;
        private final Paint selectedPaint;
        private final RectF selectedRect;
        private final ArrayList tabs;

        public static class Factory extends UItem.UItemFactory {
            static {
                UItem.UItemFactory.setup(new Factory());
            }

            public static UItem asTabs(int i, ArrayList arrayList, int i2, Utilities.Callback callback) {
                UItem ofFactory = UItem.ofFactory(Factory.class);
                ofFactory.id = i;
                ofFactory.object = arrayList;
                ofFactory.intValue = i2;
                ofFactory.object2 = callback;
                return ofFactory;
            }

            private static boolean eq(ArrayList arrayList, ArrayList arrayList2) {
                if (arrayList == arrayList2) {
                    return true;
                }
                if (arrayList == null && arrayList2 == null) {
                    return true;
                }
                if (arrayList == null || arrayList2 == null || arrayList.size() != arrayList2.size()) {
                    return false;
                }
                for (int i = 0; i < arrayList.size(); i++) {
                    if (!TextUtils.equals((CharSequence) arrayList.get(i), (CharSequence) arrayList2.get(i))) {
                        return false;
                    }
                }
                return true;
            }

            @Override
            public void bindView(View view, UItem uItem, boolean z) {
                ((Tabs) view).set(uItem.id, (ArrayList) uItem.object, uItem.intValue, (Utilities.Callback) uItem.object2);
            }

            @Override
            public boolean contentsEquals(UItem uItem, UItem uItem2) {
                return uItem.intValue == uItem2.intValue && uItem.object2 == uItem2.object2 && equals(uItem, uItem2);
            }

            @Override
            public Tabs createView(Context context, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
                return new Tabs(context, resourcesProvider);
            }

            @Override
            public boolean equals(UItem uItem, UItem uItem2) {
                return uItem.id == uItem2.id && eq((ArrayList) uItem.object, (ArrayList) uItem2.object);
            }
        }

        public Tabs(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.tabs = new ArrayList();
            this.flooredRect = new RectF();
            this.ceiledRect = new RectF();
            this.selectedRect = new RectF();
            this.selectedPaint = new Paint(1);
            this.lastId = Integer.MIN_VALUE;
            this.resourcesProvider = resourcesProvider;
            LinearLayout linearLayout = new LinearLayout(context) {
                private final void setBounds(RectF rectF, View view) {
                    rectF.set(view.getLeft() + AndroidUtilities.dp(5.0f), view.getTop(), view.getRight() - AndroidUtilities.dp(5.0f), view.getBottom());
                }

                @Override
                protected void dispatchDraw(android.graphics.Canvas r7) {
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Gifts.GiftSheet.Tabs.AnonymousClass1.dispatchDraw(android.graphics.Canvas):void");
                }
            };
            this.layout = linearLayout;
            linearLayout.setOrientation(0);
            linearLayout.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(12.0f));
            addView(linearLayout);
            setHorizontalScrollBarEnabled(false);
            this.animatedSelected = new AnimatedFloat(linearLayout, 0L, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
        }

        public void lambda$set$0(int i, Utilities.Callback callback, View view) {
            TextView textView = (TextView) this.tabs.get(i);
            smoothScrollTo(textView.getLeft() - (textView.getWidth() / 2), 0);
            if (callback != null) {
                callback.run(Integer.valueOf(i));
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), i2);
        }

        public void set(int i, ArrayList arrayList, int i2, final Utilities.Callback callback) {
            boolean z = this.lastId == i;
            this.lastId = i;
            if (this.tabs.size() != arrayList.size()) {
                int i3 = 0;
                int i4 = 0;
                while (i3 < this.tabs.size()) {
                    CharSequence charSequence = i4 < arrayList.size() ? (CharSequence) arrayList.get(i4) : null;
                    if (charSequence == null) {
                        this.layout.removeView((View) this.tabs.remove(i3));
                        i3--;
                    } else {
                        ((TextView) this.tabs.get(i3)).setText(charSequence);
                    }
                    i4++;
                    i3++;
                }
                while (i4 < arrayList.size()) {
                    TextView textView = new TextView(getContext());
                    textView.setGravity(17);
                    textView.setText((CharSequence) arrayList.get(i4));
                    textView.setTypeface(AndroidUtilities.bold());
                    textView.setTextColor(Theme.blendOver(Theme.getColor(Theme.key_dialogGiftsBackground), Theme.getColor(Theme.key_dialogGiftsTabText)));
                    textView.setTextSize(1, 14.0f);
                    textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
                    ScaleStateListAnimator.apply(textView, 0.075f, 1.4f);
                    this.layout.addView(textView, LayoutHelper.createLinear(-2, 26));
                    this.tabs.add(textView);
                    i4++;
                }
            }
            this.selected = i2;
            if (!z) {
                this.animatedSelected.set(i2, true);
            }
            this.layout.invalidate();
            for (final int i5 = 0; i5 < this.tabs.size(); i5++) {
                ((TextView) this.tabs.get(i5)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        GiftSheet.Tabs.this.lambda$set$0(i5, callback, view);
                    }
                });
            }
        }
    }

    public GiftSheet(Context context, int i, long j, Runnable runnable) {
        this(context, i, j, null, runnable);
    }

    public GiftSheet(final Context context, final int i, final long j, List list, final Runnable runnable) {
        super(context, null, false, false, false, null);
        TLObject tLObject;
        this.premiumTiers = new ArrayList();
        this.TAB_ALL = 0;
        this.TAB_LIMITED = 1;
        this.TAB_IN_STOCK = 2;
        this.tabs = new ArrayList();
        this.currentAccount = i;
        this.dialogId = j;
        boolean z = UserConfig.getInstance(i).getClientUserId() == j;
        this.self = z;
        this.options = list;
        this.closeParentSheet = runnable;
        int i2 = Theme.key_dialogGiftsBackground;
        setBackgroundColor(Theme.getColor(i2));
        fixNavigationBar(Theme.getColor(i2));
        StarsController.getInstance(i).loadStarGifts();
        BackupImageView backupImageView = new BackupImageView(context);
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        if (j > 0) {
            TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(j));
            this.name = UserObject.getForcedFirstName(user);
            avatarDrawable.setInfo(user);
            tLObject = user;
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(i).getChat(Long.valueOf(-j));
            this.name = chat == null ? "" : chat.title;
            avatarDrawable.setInfo(chat);
            tLObject = chat;
        }
        backupImageView.setForUserOrChat(tLObject, avatarDrawable);
        this.topPadding = 0.15f;
        FrameLayout frameLayout = new FrameLayout(context);
        this.premiumHeaderView = frameLayout;
        FrameLayout frameLayout2 = new FrameLayout(context);
        frameLayout2.setClipChildren(false);
        frameLayout2.setClipToPadding(false);
        frameLayout2.addView(StarsIntroActivity.makeParticlesView(context, 70, 0), LayoutHelper.createFrame(-1, -1.0f));
        backupImageView.setRoundRadius(AndroidUtilities.dp(50.0f));
        frameLayout2.addView(backupImageView, LayoutHelper.createFrame(100, 100.0f, 17, 0.0f, 32.0f, 0.0f, 24.0f));
        ScaleStateListAnimator.apply(backupImageView);
        backupImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                GiftSheet.this.lambda$new$0(j, view);
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        if (!z && j >= 0) {
            frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, 150.0f));
        }
        frameLayout.addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 55, 0.0f, 145.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        int i3 = Theme.key_dialogTextBlack;
        textView.setTextColor(Theme.getColor(i3, this.resourcesProvider));
        textView.setGravity(17);
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 1, 4, 0, 4, 0));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, this.resourcesProvider);
        int i4 = Theme.key_chat_messageLinkIn;
        linksTextView.setLinkTextColor(Theme.getColor(i4, this.resourcesProvider));
        linksTextView.setTextSize(1, 14.0f);
        linksTextView.setTextColor(Theme.getColor(i3, this.resourcesProvider));
        linksTextView.setGravity(17);
        linearLayout.addView(linksTextView, LayoutHelper.createLinear(-1, -2, 1, 4, 9, 4, 10));
        textView.setText(LocaleController.getString(R.string.Gift2Premium));
        linksTextView.setText(TextUtils.concat(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2PremiumInfo, this.name)), " ", AndroidUtilities.replaceArrows(AndroidUtilities.makeClickable(LocaleController.getString(R.string.Gift2PremiumInfoLink), new Runnable() {
            @Override
            public final void run() {
                GiftSheet.lambda$new$1();
            }
        }), true)));
        linksTextView.setMaxWidth(HintView2.cutInFancyHalf(linksTextView.getText(), linksTextView.getPaint()));
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.starsHeaderView = linearLayout2;
        linearLayout2.setOrientation(1);
        if (z || j < 0) {
            linearLayout2.addView(frameLayout2, LayoutHelper.createFrame(-1, 150.0f));
        }
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 20.0f);
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setTextColor(Theme.getColor(i3, this.resourcesProvider));
        textView2.setGravity(17);
        linearLayout2.addView(textView2, LayoutHelper.createLinear(-1, -2, 1, 4, 16, 4, 0));
        LinkSpanDrawable.LinksTextView linksTextView2 = new LinkSpanDrawable.LinksTextView(context, this.resourcesProvider);
        linksTextView2.setLinkTextColor(Theme.getColor(i4, this.resourcesProvider));
        linksTextView2.setTextSize(1, 14.0f);
        linksTextView2.setTextColor(Theme.getColor(i3, this.resourcesProvider));
        linksTextView2.setGravity(17);
        textView2.setText(LocaleController.getString(j < 0 ? R.string.Gift2StarsChannel : z ? R.string.Gift2StarsSelf : R.string.Gift2Stars));
        if (z) {
            linearLayout2.addView(linksTextView2, LayoutHelper.createLinear(-2, -2, 1, 26, 9, 26, 4));
            LinkSpanDrawable.LinksTextView linksTextView3 = new LinkSpanDrawable.LinksTextView(context, this.resourcesProvider);
            linksTextView3.setLinkTextColor(Theme.getColor(i4, this.resourcesProvider));
            linksTextView3.setTextSize(1, 14.0f);
            linksTextView3.setTextColor(Theme.getColor(i3, this.resourcesProvider));
            linksTextView3.setGravity(17);
            linearLayout2.addView(linksTextView3, LayoutHelper.createLinear(-2, -2, 1, 26, 4, 26, 10));
            linksTextView2.setText(LocaleController.getString(R.string.Gift2StarsSelfInfo1));
            linksTextView3.setText(LocaleController.getString(R.string.Gift2StarsSelfInfo2));
        } else if (j < 0) {
            linearLayout2.addView(linksTextView2, LayoutHelper.createLinear(-2, -2, 1, 26, 9, 26, 4));
            NotificationCenter.listenEmojiLoading(linksTextView2);
            linksTextView2.setText(Emoji.replaceEmoji(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2StarsChannelInfo, this.name)), linksTextView2.getPaint().getFontMetricsInt(), false));
        } else {
            linearLayout2.addView(linksTextView2, LayoutHelper.createLinear(-1, -2, 1, 4, 9, 4, 10));
            linksTextView2.setText(TextUtils.concat(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2StarsInfo, this.name)), " ", AndroidUtilities.replaceArrows(AndroidUtilities.makeClickable(LocaleController.getString(R.string.Gift2StarsInfoLink), new Runnable() {
                @Override
                public final void run() {
                    GiftSheet.lambda$new$2(context);
                }
            }), true)));
            linksTextView2.setMaxWidth(HintView2.cutInFancyHalf(linksTextView2.getText(), linksTextView2.getPaint()));
        }
        ExtendedGridLayoutManager extendedGridLayoutManager = new ExtendedGridLayoutManager(context, 3);
        this.layoutManager = extendedGridLayoutManager;
        extendedGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i5) {
                UItem item;
                int i6;
                return (GiftSheet.this.adapter == null || i5 == 0 || (item = GiftSheet.this.adapter.getItem(i5 + (-1))) == null || (i6 = item.spanCount) == -1) ? GiftSheet.this.layoutManager.getSpanCount() : i6;
            }
        });
        this.recyclerListView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        this.recyclerListView.setClipToPadding(false);
        this.recyclerListView.setLayoutManager(extendedGridLayoutManager);
        this.recyclerListView.setSelectorType(9);
        this.recyclerListView.setSelectorDrawableColor(0);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator() {
            @Override
            protected float animateByScale(View view) {
                return 0.3f;
            }
        };
        this.itemAnimator = defaultItemAnimator;
        defaultItemAnimator.setDelayAnimations(false);
        defaultItemAnimator.setSupportsChangeAnimations(false);
        defaultItemAnimator.setDurations(350L);
        defaultItemAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        defaultItemAnimator.setDelayIncrement(40L);
        this.recyclerListView.setItemAnimator(defaultItemAnimator);
        this.recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i5) {
                GiftSheet.this.lambda$new$5(context, i, runnable, view, i5);
            }
        });
        updatePremiumTiers();
        this.adapter.update(false);
        updateTitle();
        if (BirthdayController.getInstance(i).isToday(j)) {
            setBirthday();
        }
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.starGiftsLoaded);
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.userInfoDidLoad);
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.starGiftSoldOut);
        this.actionBar.setTitle(getTitle());
        NotificationCenter.listenEmojiLoading(this.actionBar.getTitleTextView());
    }

    public void lambda$fillItems$10(UniversalAdapter universalAdapter, Integer num) {
        if (this.selectedTab == num.intValue()) {
            return;
        }
        this.selectedTab = num.intValue();
        this.itemAnimator.endAnimations();
        universalAdapter.update(true);
    }

    public void lambda$new$0(long j, View view) {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        lambda$new$0();
        safeLastFragment.presentFragment(ProfileActivity.of(j));
    }

    public static void lambda$new$1() {
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment == null) {
            return;
        }
        BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
        bottomSheetParams.transitionFromLeft = true;
        bottomSheetParams.allowNestedScroll = false;
        lastFragment.showAsSheet(new PremiumPreviewFragment("gifts"), bottomSheetParams);
    }

    public static void lambda$new$2(Context context) {
        new ExplainStarsSheet(context).show();
    }

    public void lambda$new$3(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
        lambda$new$0();
    }

    public void lambda$new$4(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
        lambda$new$0();
    }

    public void lambda$new$5(Context context, int i, final Runnable runnable, View view, int i2) {
        UItem item = this.adapter.getItem(i2 - 1);
        if (item != null && item.instanceOf(GiftCell.Factory.class)) {
            Object obj = item.object;
            if (obj instanceof GiftPremiumBottomSheet$GiftTier) {
                new SendGiftSheet(context, i, (GiftPremiumBottomSheet$GiftTier) obj, this.dialogId, new Runnable() {
                    @Override
                    public final void run() {
                        GiftSheet.this.lambda$new$3(runnable);
                    }
                }).show();
                return;
            }
            if (obj instanceof TL_stars.StarGift) {
                TL_stars.StarGift starGift = (TL_stars.StarGift) obj;
                if (starGift.sold_out) {
                    StarsIntroActivity.showSoldOutGiftSheet(context, i, starGift, this.resourcesProvider);
                } else {
                    new SendGiftSheet(context, i, starGift, this.dialogId, new Runnable() {
                        @Override
                        public final void run() {
                            GiftSheet.this.lambda$new$4(runnable);
                        }
                    }).show();
                }
            }
        }
    }

    public void lambda$updatePremiumTiers$7() {
        UniversalAdapter universalAdapter = this.adapter;
        if (universalAdapter != null) {
            universalAdapter.update(false);
        }
    }

    public void lambda$updatePremiumTiers$8(BillingResult billingResult, List list) {
        Iterator it = list.iterator();
        long j = 0;
        while (it.hasNext()) {
            ProductDetails productDetails = (ProductDetails) it.next();
            Iterator it2 = this.premiumTiers.iterator();
            while (true) {
                if (it2.hasNext()) {
                    GiftPremiumBottomSheet$GiftTier giftPremiumBottomSheet$GiftTier = (GiftPremiumBottomSheet$GiftTier) it2.next();
                    if (giftPremiumBottomSheet$GiftTier.getStoreProduct() != null && giftPremiumBottomSheet$GiftTier.getStoreProduct().equals(productDetails.getProductId())) {
                        giftPremiumBottomSheet$GiftTier.setGooglePlayProductDetails(productDetails);
                        if (giftPremiumBottomSheet$GiftTier.getPricePerMonth() > j) {
                            j = giftPremiumBottomSheet$GiftTier.getPricePerMonth();
                        }
                    }
                }
            }
        }
        Iterator it3 = this.premiumTiers.iterator();
        while (it3.hasNext()) {
            ((GiftPremiumBottomSheet$GiftTier) it3.next()).setPricePerMonthRegular(j);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                GiftSheet.this.lambda$updatePremiumTiers$7();
            }
        });
    }

    public void lambda$updatePremiumTiers$9(List list) {
        if (getContext() == null || !isShown()) {
            return;
        }
        List filterGiftOptions = BoostRepository.filterGiftOptions(list, 1);
        this.options = filterGiftOptions;
        List filterGiftOptionsByBilling = BoostRepository.filterGiftOptionsByBilling(filterGiftOptions);
        this.options = filterGiftOptionsByBilling;
        if (filterGiftOptionsByBilling.isEmpty()) {
            return;
        }
        updatePremiumTiers();
        UniversalAdapter universalAdapter = this.adapter;
        if (universalAdapter != null) {
            universalAdapter.update(true);
        }
    }

    private void updatePremiumTiers() {
        List list;
        this.premiumTiers.clear();
        if (this.premiumTiers.isEmpty() && (list = this.options) != null && !list.isEmpty()) {
            ArrayList arrayList = new ArrayList();
            long j = 0;
            for (int size = this.options.size() - 1; size >= 0; size--) {
                GiftPremiumBottomSheet$GiftTier giftPremiumBottomSheet$GiftTier = new GiftPremiumBottomSheet$GiftTier((TLRPC.TL_premiumGiftCodeOption) this.options.get(size));
                this.premiumTiers.add(giftPremiumBottomSheet$GiftTier);
                if (BuildVars.useInvoiceBilling()) {
                    if (giftPremiumBottomSheet$GiftTier.getPricePerMonth() > j) {
                        j = giftPremiumBottomSheet$GiftTier.getPricePerMonth();
                    }
                } else if (giftPremiumBottomSheet$GiftTier.getStoreProduct() != null && BillingController.getInstance().isReady()) {
                    arrayList.add(QueryProductDetailsParams.Product.newBuilder().setProductType("inapp").setProductId(giftPremiumBottomSheet$GiftTier.getStoreProduct()).build());
                }
            }
            if (BuildVars.useInvoiceBilling()) {
                Iterator it = this.premiumTiers.iterator();
                while (it.hasNext()) {
                    ((GiftPremiumBottomSheet$GiftTier) it.next()).setPricePerMonthRegular(j);
                }
            } else if (!arrayList.isEmpty()) {
                System.currentTimeMillis();
                BillingController.getInstance().queryProductDetails(arrayList, new ProductDetailsResponseListener() {
                    @Override
                    public final void onProductDetailsResponse(BillingResult billingResult, List list2) {
                        GiftSheet.this.lambda$updatePremiumTiers$8(billingResult, list2);
                    }
                });
            }
        }
        if (this.premiumTiers.isEmpty()) {
            BoostRepository.loadGiftOptions(this.currentAccount, null, new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    GiftSheet.this.lambda$updatePremiumTiers$9((List) obj);
                }
            });
        }
    }

    @Override
    protected RecyclerListView.SelectionAdapter createAdapter(RecyclerListView recyclerListView) {
        UniversalAdapter universalAdapter = new UniversalAdapter(this.recyclerListView, getContext(), this.currentAccount, 0, true, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                GiftSheet.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, this.resourcesProvider);
        this.adapter = universalAdapter;
        universalAdapter.setApplyBackground(false);
        return this.adapter;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        UniversalAdapter universalAdapter;
        if (i == NotificationCenter.billingProductDetailsUpdated) {
            updatePremiumTiers();
            return;
        }
        if (i == NotificationCenter.starGiftsLoaded) {
            universalAdapter = this.adapter;
            if (universalAdapter == null) {
                return;
            }
        } else if (i == NotificationCenter.userInfoDidLoad) {
            if (!isShown()) {
                return;
            }
            ArrayList arrayList = this.premiumTiers;
            if (arrayList != null && !arrayList.isEmpty()) {
                return;
            }
            updatePremiumTiers();
            universalAdapter = this.adapter;
            if (universalAdapter == null) {
                return;
            }
        } else {
            if (i != NotificationCenter.starGiftSoldOut || !isShown()) {
                return;
            }
            TL_stars.StarGift starGift = (TL_stars.StarGift) objArr[0];
            BulletinFactory.of(this.container, this.resourcesProvider).createEmojiBulletin(starGift.sticker, LocaleController.getString(R.string.Gift2SoldOutTitle), AndroidUtilities.replaceTags(LocaleController.formatPluralStringComma("Gift2SoldOutCount", starGift.availability_total))).show();
            universalAdapter = this.adapter;
            if (universalAdapter == null) {
                return;
            }
        }
        universalAdapter.update(true);
    }

    @Override
    public void lambda$new$0() {
        super.lambda$new$0();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.billingProductDetailsUpdated);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starGiftsLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.userInfoDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.starGiftSoldOut);
    }

    public void fillItems(ArrayList arrayList, final UniversalAdapter universalAdapter) {
        if (!this.self && this.dialogId >= 0) {
            arrayList.add(UItem.asCustom(this.premiumHeaderView));
            ArrayList arrayList2 = this.premiumTiers;
            if (arrayList2 == null || arrayList2.isEmpty()) {
                arrayList.add(UItem.asFlicker(1, 34).setSpanCount(1));
                arrayList.add(UItem.asFlicker(2, 34).setSpanCount(1));
                arrayList.add(UItem.asFlicker(3, 34).setSpanCount(1));
            } else {
                Iterator it = this.premiumTiers.iterator();
                while (it.hasNext()) {
                    arrayList.add(GiftCell.Factory.asPremiumGift((GiftPremiumBottomSheet$GiftTier) it.next()));
                }
            }
        }
        StarsController starsController = StarsController.getInstance(this.currentAccount);
        ArrayList arrayList3 = this.birthday ? starsController.birthdaySortedGifts : starsController.sortedGifts;
        if (MessagesController.getInstance(this.currentAccount).stargiftsBlocked || arrayList3.isEmpty()) {
            return;
        }
        arrayList.add(UItem.asCustom(this.starsHeaderView));
        TreeSet treeSet = new TreeSet();
        for (int i = 0; i < arrayList3.size(); i++) {
            treeSet.add(Long.valueOf(((TL_stars.StarGift) arrayList3.get(i)).stars));
        }
        ArrayList arrayList4 = new ArrayList();
        arrayList4.add(LocaleController.getString(R.string.Gift2TabAll));
        arrayList4.add(LocaleController.getString(R.string.Gift2TabLimited));
        arrayList4.add(LocaleController.getString(R.string.Gift2TabInStock));
        Iterator it2 = treeSet.iterator();
        ArrayList arrayList5 = new ArrayList();
        while (it2.hasNext()) {
            Long l = (Long) it2.next();
            arrayList4.add(StarsIntroActivity.replaceStarsWithPlain("⭐️ " + LocaleController.formatNumber(l.longValue(), ','), 0.8f));
            arrayList5.add(l);
        }
        arrayList.add(Tabs.Factory.asTabs(1, arrayList4, this.selectedTab, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                GiftSheet.this.lambda$fillItems$10(universalAdapter, (Integer) obj);
            }
        }));
        int i2 = this.selectedTab - 3;
        long longValue = (i2 < 0 || i2 >= arrayList5.size()) ? 0L : ((Long) arrayList5.get(this.selectedTab - 3)).longValue();
        for (int i3 = 0; i3 < arrayList3.size(); i3++) {
            TL_stars.StarGift starGift = (TL_stars.StarGift) arrayList3.get(i3);
            int i4 = this.selectedTab;
            if (i4 == 0 || ((i4 == 1 && starGift.limited) || ((i4 == 2 && !starGift.sold_out) || (i4 >= 3 && starGift.stars == longValue)))) {
                arrayList.add(GiftCell.Factory.asStarGift(i4, starGift));
            }
        }
        if (starsController.giftsLoading) {
            arrayList.add(UItem.asFlicker(4, 34).setSpanCount(1));
            arrayList.add(UItem.asFlicker(5, 34).setSpanCount(1));
            arrayList.add(UItem.asFlicker(6, 34).setSpanCount(1));
        }
        arrayList.add(UItem.asSpace(AndroidUtilities.dp(40.0f)));
    }

    @Override
    protected CharSequence getTitle() {
        return this.self ? LocaleController.getString(R.string.Gift2TitleSelf1) : Emoji.replaceEmoji(LocaleController.formatString(R.string.Gift2User, this.name), null, false);
    }

    public GiftSheet setBirthday() {
        return setBirthday(true);
    }

    public GiftSheet setBirthday(boolean z) {
        this.birthday = z;
        this.adapter.update(false);
        return this;
    }
}
