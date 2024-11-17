package org.telegram.ui.Components.Premium.boosts.cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.Premium.GLIcon.GLIconRenderer;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Premium.StarParticlesView;

public class HeaderCell extends FrameLayout {
    private ValueAnimator goldenAnimator;
    private final GLIconTextureView iconTextureView;
    private final LinearLayout linearLayout;
    private LinkSpanDrawable.LinkCollector links;
    private final Paint[] paints;
    private final Theme.ResourcesProvider resourcesProvider;
    private final StarParticlesView starParticlesView;
    private final LinkSpanDrawable.LinksTextView subtitleView;
    private final TextView titleView;

    public HeaderCell(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.resourcesProvider = resourcesProvider;
        LinearLayout linearLayout = new LinearLayout(context);
        this.linearLayout = linearLayout;
        linearLayout.setOrientation(1);
        GLIconTextureView gLIconTextureView = new GLIconTextureView(context, 1) {
            @Override
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                setPaused(false);
            }

            @Override
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                setPaused(true);
            }
        };
        this.iconTextureView = gLIconTextureView;
        Bitmap createBitmap = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(createBitmap);
        int i = Theme.key_premiumGradient2;
        canvas.drawColor(ColorUtils.blendARGB(Theme.getColor(i, resourcesProvider), Theme.getColor(Theme.key_dialogBackground, resourcesProvider), 0.5f));
        gLIconTextureView.setBackgroundBitmap(createBitmap);
        GLIconRenderer gLIconRenderer = gLIconTextureView.mRenderer;
        gLIconRenderer.colorKey1 = i;
        gLIconRenderer.colorKey2 = Theme.key_premiumGradient1;
        gLIconRenderer.updateColors();
        linearLayout.addView(gLIconTextureView, LayoutHelper.createLinear(160, 160, 1));
        StarParticlesView starParticlesView = new StarParticlesView(context) {
            @Override
            protected void onAttachedToWindow() {
                super.onAttachedToWindow();
                HeaderCell.this.starParticlesView.setPaused(false);
            }

            @Override
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                HeaderCell.this.starParticlesView.setPaused(true);
            }

            @Override
            public void onMeasure(int i2, int i3) {
                super.onMeasure(i2, i3);
                this.drawable.rect2.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight() - AndroidUtilities.dp(52.0f));
            }
        };
        this.starParticlesView = starParticlesView;
        this.paints = new Paint[20];
        updatePaints(0.0f);
        StarParticlesView.Drawable drawable = starParticlesView.drawable;
        drawable.useGradient = false;
        drawable.useBlur = false;
        drawable.forceMaxAlpha = true;
        drawable.checkBounds = true;
        drawable.getPaint = new Utilities.CallbackReturn() {
            @Override
            public final Object run(Object obj) {
                Paint lambda$new$0;
                lambda$new$0 = HeaderCell.this.lambda$new$0((Integer) obj);
                return lambda$new$0;
            }
        };
        starParticlesView.drawable.init();
        gLIconTextureView.setStarParticlesView(starParticlesView);
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextSize(1, 22.0f);
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        textView.setTextColor(Theme.getColor(i2, resourcesProvider));
        textView.setGravity(1);
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 1, 24, -8, 24, 0));
        LinkSpanDrawable.LinkCollector linkCollector = new LinkSpanDrawable.LinkCollector(this);
        this.links = linkCollector;
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, linkCollector, resourcesProvider);
        this.subtitleView = linksTextView;
        linksTextView.setTextSize(1, 15.0f);
        linksTextView.setGravity(17);
        linksTextView.setTextColor(Theme.getColor(i2, resourcesProvider));
        linksTextView.setMovementMethod(LinkMovementMethod.getInstance());
        linksTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, resourcesProvider));
        linksTextView.setImportantForAccessibility(2);
        linearLayout.addView(linksTextView, LayoutHelper.createFrame(-1, -2.0f, 17, 24.0f, 8.0f, 24.0f, 18.0f));
        setClipChildren(false);
        addView(starParticlesView, LayoutHelper.createFrame(-1, 234, 48));
        addView(linearLayout);
        setWillNotDraw(false);
    }

    public Paint lambda$new$0(Integer num) {
        return this.paints[num.intValue() % this.paints.length];
    }

    public void lambda$setStars$2(float[] fArr, float f, float f2, boolean z, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f3 = floatValue - fArr[0];
        fArr[0] = floatValue;
        this.iconTextureView.mRenderer.golden = AndroidUtilities.lerp(f, f2, floatValue);
        GLIconRenderer gLIconRenderer = this.iconTextureView.mRenderer;
        gLIconRenderer.angleX3 += f3 * 360.0f * (z ? 1 : -1);
        gLIconRenderer.updateColors();
        updatePaints(this.iconTextureView.mRenderer.golden);
    }

    public void updatePaints(float f) {
        int color = Theme.getColor(Theme.key_premiumGradient1, this.resourcesProvider);
        int color2 = Theme.getColor(Theme.key_premiumGradient2, this.resourcesProvider);
        int blendARGB = ColorUtils.blendARGB(color, -371690, f);
        int blendARGB2 = ColorUtils.blendARGB(color2, -14281, f);
        int i = 0;
        while (true) {
            Paint[] paintArr = this.paints;
            if (i >= paintArr.length) {
                return;
            }
            paintArr[i] = new Paint(1);
            this.paints[i].setColorFilter(new PorterDuffColorFilter(ColorUtils.blendARGB(blendARGB, blendARGB2, i / (this.paints.length - 1)), PorterDuff.Mode.SRC_IN));
            i++;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.links != null) {
            canvas.save();
            canvas.translate(this.subtitleView.getLeft(), this.subtitleView.getTop());
            if (this.links.draw(canvas)) {
                invalidate();
            }
            canvas.restore();
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        StarParticlesView starParticlesView = this.starParticlesView;
        starParticlesView.setTranslationY((this.iconTextureView.getTop() + (this.iconTextureView.getMeasuredHeight() / 2.0f)) - (starParticlesView.getMeasuredHeight() / 2.0f));
    }

    public void setBoostViaGifsText(TLRPC.Chat chat) {
        if (Build.VERSION.SDK_INT >= 21) {
            setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    float dp = AndroidUtilities.dp(12.0f);
                    outline.setRoundRect(0, 0, view.getWidth(), (int) (view.getHeight() + dp), dp);
                }
            });
            setClipToOutline(true);
        }
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
        marginLayoutParams.topMargin = -AndroidUtilities.dp(6.0f);
        setLayoutParams(marginLayoutParams);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray, this.resourcesProvider));
        this.titleView.setText(LocaleController.formatString("BoostingBoostsViaGifts", R.string.BoostingBoostsViaGifts, new Object[0]));
        this.subtitleView.setText(LocaleController.formatString(ChatObject.isChannelAndNotMegaGroup(chat) ? R.string.BoostingGetMoreBoost2 : R.string.BoostingGetMoreBoostGroup, new Object[0]));
        this.subtitleView.setTextColor(Theme.getColor(Theme.key_dialogTextGray3, this.resourcesProvider));
    }

    public void setGiftLinkText() {
        this.titleView.setText(LocaleController.formatString("BoostingGiftLink", R.string.BoostingGiftLink, new Object[0]));
        this.subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("BoostingLinkAllows", R.string.BoostingLinkAllows, new Object[0])));
    }

    public void setGiftLinkToUserText(long j, final Utilities.Callback callback) {
        this.titleView.setText(LocaleController.formatString("BoostingGiftLink", R.string.BoostingGiftLink, new Object[0]));
        SpannableStringBuilder replaceTags = AndroidUtilities.replaceTags(LocaleController.getString(R.string.BoostingLinkAllowsToUser));
        final TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(j));
        this.subtitleView.setText(AndroidUtilities.replaceCharSequence("%1$s", replaceTags, AndroidUtilities.replaceSingleTag("**" + UserObject.getUserName(user) + "**", Theme.key_chat_messageLinkIn, 2, new Runnable() {
            @Override
            public final void run() {
                Utilities.Callback.this.run(user);
            }
        }, this.resourcesProvider)));
    }

    public void setPaused(boolean z) {
        this.iconTextureView.setPaused(z);
        this.starParticlesView.setPaused(z);
    }

    public void setStars(final boolean z) {
        ValueAnimator valueAnimator = this.goldenAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        final float f = this.iconTextureView.mRenderer.golden;
        float f2 = z ? 1.0f : 0.0f;
        this.goldenAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
        final float[] fArr = {0.0f};
        this.iconTextureView.cancelIdleAnimation();
        this.iconTextureView.cancelAnimatons();
        this.iconTextureView.startBackAnimation();
        final float f3 = f2;
        this.goldenAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                HeaderCell.this.lambda$setStars$2(fArr, f, f3, z, valueAnimator2);
            }
        });
        this.goldenAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                float[] fArr2 = fArr;
                float f4 = 1.0f - fArr2[0];
                fArr2[0] = 1.0f;
                HeaderCell.this.iconTextureView.mRenderer.golden = AndroidUtilities.lerp(f, f3, 1.0f);
                HeaderCell.this.iconTextureView.mRenderer.angleX3 += f4 * 360.0f * (z ? 1 : -1);
                HeaderCell.this.iconTextureView.mRenderer.updateColors();
                HeaderCell headerCell = HeaderCell.this;
                headerCell.updatePaints(headerCell.iconTextureView.mRenderer.golden);
                HeaderCell.this.iconTextureView.scheduleIdleAnimation(750L);
            }
        });
        this.goldenAnimator.setDuration(680L);
        this.goldenAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.goldenAnimator.start();
    }

    public void setUnclaimedText() {
        this.titleView.setText(LocaleController.formatString("BoostingGiftLink", R.string.BoostingGiftLink, new Object[0]));
        this.subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("BoostingLinkAllowsAnyone", R.string.BoostingLinkAllowsAnyone, new Object[0])));
    }

    public void setUsedGiftLinkText() {
        this.titleView.setText(LocaleController.formatString("BoostingUsedGiftLink", R.string.BoostingUsedGiftLink, new Object[0]));
        this.subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.formatString("BoostingLinkUsed", R.string.BoostingLinkUsed, new Object[0])));
    }
}
