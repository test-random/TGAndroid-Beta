package org.telegram.ui.Stars;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Iterator;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ButtonSpan;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CompatDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FireworksOverlay;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.LoadingSpan;
import org.telegram.ui.Components.Premium.boosts.UserSelectorBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.TableView;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.FilterCreateActivity;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stars.StarGiftSheet;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.bots.AffiliateProgramFragment;
import org.telegram.ui.bots.BotWebViewSheet;

public class StarGiftSheet extends BottomSheet {
    private final LinkSpanDrawable.LinksTextView afterTableTextView;
    private final LinkSpanDrawable.LinksTextView beforeTableTextView;
    private final ButtonWithCounterView button;
    private final CheckBox2 checkbox;
    private final LinearLayout checkboxLayout;
    private final View checkboxSeparator;
    private final TextView checkboxTextView;
    private ContainerView container;
    private HintView2 currentHintView;
    private TextView currentHintViewTextView;
    private float currentPage;
    private final long dialogId;
    private final AffiliateProgramFragment.FeatureCell[] featureCells;
    private FireworksOverlay fireworksOverlay;
    private boolean firstSet;
    private final LinearLayout infoLayout;
    private boolean isLearnMore;
    private MessageObject messageObject;
    private boolean messageObjectRepolled;
    private boolean messageObjectRepolling;
    private boolean myProfile;
    private ArrayList sample_attributes;
    private ValueAnimator switchingPagesAnimator;
    private final TableView tableView;
    private final TopView topView;
    private ColoredImageSpan upgradeIconSpan;
    private final LinearLayout upgradeLayout;
    private TLRPC.PaymentForm upgrade_form;
    private TL_stars.UserStarGift userStarGift;

    public class AnonymousClass1 extends AnimatorListenerAdapter {
        final boolean val$toUpgrade;

        AnonymousClass1(boolean z) {
            r2 = z;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            StarGiftSheet.this.onSwitchedPage(r2 ? 1.0f : 0.0f);
            StarGiftSheet.this.infoLayout.setVisibility(r2 ? 8 : 0);
            StarGiftSheet.this.upgradeLayout.setVisibility(r2 ? 0 : 8);
            StarGiftSheet.this.switchingPagesAnimator = null;
        }
    }

    public class AnonymousClass2 extends ClickableSpan {
        final TL_stars.starGiftAttributeOriginalDetails val$details;

        AnonymousClass2(TL_stars.starGiftAttributeOriginalDetails stargiftattributeoriginaldetails) {
            r2 = stargiftattributeoriginaldetails;
        }

        @Override
        public void onClick(View view) {
            StarGiftSheet.this.lambda$set$17(r2.sender_id);
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            textPaint.setColor(textPaint.linkColor);
        }
    }

    public class AnonymousClass3 extends ClickableSpan {
        final TL_stars.starGiftAttributeOriginalDetails val$details;

        AnonymousClass3(TL_stars.starGiftAttributeOriginalDetails stargiftattributeoriginaldetails) {
            r2 = stargiftattributeoriginaldetails;
        }

        @Override
        public void onClick(View view) {
            StarGiftSheet.this.lambda$set$17(r2.recipient_id);
        }

        @Override
        public void updateDrawState(TextPaint textPaint) {
            textPaint.setColor(textPaint.linkColor);
        }
    }

    public class AnonymousClass4 extends BaseFragment {
        final UserSelectorBottomSheet[] val$sheet;

        AnonymousClass4(UserSelectorBottomSheet[] userSelectorBottomSheetArr) {
            r2 = userSelectorBottomSheetArr;
        }

        @Override
        public Context getContext() {
            return StarGiftSheet.this.getContext();
        }

        @Override
        public Activity getParentActivity() {
            LaunchActivity launchActivity = LaunchActivity.instance;
            return launchActivity == null ? AndroidUtilities.findActivity(StarGiftSheet.this.getContext()) : launchActivity;
        }

        @Override
        public Theme.ResourcesProvider getResourceProvider() {
            return ((BottomSheet) StarGiftSheet.this).resourcesProvider;
        }

        @Override
        public boolean presentFragment(BaseFragment baseFragment) {
            r2[0].dismiss();
            StarGiftSheet.this.dismiss();
            BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
            if (safeLastFragment != null) {
                return safeLastFragment.presentFragment(safeLastFragment);
            }
            return false;
        }
    }

    public class ContainerView extends FrameLayout {
        private final Paint backgroundPaint;
        private final Path path;
        private final RectF rect;

        public ContainerView(Context context) {
            super(context);
            this.rect = new RectF();
            this.backgroundPaint = new Paint(1);
            this.path = new Path();
            setWillNotDraw(false);
            setClipChildren(false);
            setClipToPadding(false);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            canvas.save();
            float pVar = top();
            float dp = AndroidUtilities.dp(12.0f);
            this.rect.set(((BottomSheet) StarGiftSheet.this).backgroundPaddingLeft, pVar, getWidth() - ((BottomSheet) StarGiftSheet.this).backgroundPaddingLeft, getHeight() + dp);
            this.backgroundPaint.setColor(StarGiftSheet.this.getThemedColor(Theme.key_dialogBackground));
            this.path.rewind();
            this.path.addRoundRect(this.rect, dp, dp, Path.Direction.CW);
            canvas.drawPath(this.path, this.backgroundPaint);
            canvas.clipPath(this.path);
            super.dispatchDraw(canvas);
            updateTopViewTranslation();
            canvas.restore();
        }

        public float height() {
            return AndroidUtilities.lerp(StarGiftSheet.this.infoLayout.getMeasuredHeight(), StarGiftSheet.this.upgradeLayout.getMeasuredHeight(), StarGiftSheet.this.currentPage) + StarGiftSheet.this.topView.getRealHeight();
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            StarGiftSheet starGiftSheet = StarGiftSheet.this;
            starGiftSheet.onSwitchedPage(starGiftSheet.currentPage);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            for (int i3 = 0; i3 < getChildCount(); i3++) {
                View childAt = getChildAt(i3);
                if (childAt == StarGiftSheet.this.button) {
                    StarGiftSheet.this.button.measure(View.MeasureSpec.makeMeasureSpec((size - (((BottomSheet) StarGiftSheet.this).backgroundPaddingLeft * 2)) - (AndroidUtilities.dp(14.0f) * 2), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
                } else if (childAt instanceof HintView2) {
                    childAt.measure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), 1073741824));
                } else {
                    childAt.measure(i, i2);
                }
            }
            setMeasuredDimension(size, Math.max(StarGiftSheet.this.infoLayout.getMeasuredHeight(), StarGiftSheet.this.upgradeLayout.getMeasuredHeight()) + StarGiftSheet.this.topView.getMeasuredHeight());
        }

        @Override
        public void setTranslationY(float f) {
            super.setTranslationY(f);
            FrameLayout frameLayout = StarGiftSheet.this.topBulletinContainer;
            if (frameLayout != null) {
                frameLayout.setTranslationY((getTranslationY() - height()) - AndroidUtilities.navigationBarHeight);
            }
        }

        public float top() {
            return Math.max(0.0f, getHeight() - height());
        }

        public void updateTopViewTranslation() {
            StarGiftSheet.this.topView.setTranslationY((StarGiftSheet.this.topView.getMeasuredHeight() - StarGiftSheet.this.topView.getRealHeight()) - AndroidUtilities.lerp(StarGiftSheet.this.infoLayout.getMeasuredHeight(), StarGiftSheet.this.upgradeLayout.getMeasuredHeight(), StarGiftSheet.this.currentPage));
            FrameLayout frameLayout = StarGiftSheet.this.topBulletinContainer;
            if (frameLayout != null) {
                frameLayout.setTranslationY((getTranslationY() - height()) - AndroidUtilities.navigationBarHeight);
            }
        }
    }

    public static class GiftTransferTopView extends View {
        private final Paint arrowPaint;
        private final Path arrowPath;
        private final AvatarDrawable avatarDrawable;
        private final StarGiftDrawableIcon giftDrawable;
        private final ImageReceiver userImageReceiver;

        public GiftTransferTopView(Context context, TL_stars.StarGift starGift, TLRPC.User user) {
            super(context);
            Path path = new Path();
            this.arrowPath = path;
            Paint paint = new Paint(1);
            this.arrowPaint = paint;
            this.giftDrawable = new StarGiftDrawableIcon(this, starGift, 60, 0.3f);
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            this.avatarDrawable = avatarDrawable;
            avatarDrawable.setInfo(user);
            ImageReceiver imageReceiver = new ImageReceiver(this);
            this.userImageReceiver = imageReceiver;
            imageReceiver.setRoundRadius(AndroidUtilities.dp(30.0f));
            imageReceiver.setForUserOrChat(user, avatarDrawable);
            paint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText7));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            path.rewind();
            path.moveTo(0.0f, -AndroidUtilities.dp(8.0f));
            path.lineTo(AndroidUtilities.dp(6.166f), 0.0f);
            path.lineTo(0.0f, AndroidUtilities.dp(8.0f));
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.userImageReceiver.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.userImageReceiver.onDetachedFromWindow();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int width = (getWidth() / 2) - (AndroidUtilities.dp(156.0f) / 2);
            int height = (getHeight() / 2) - AndroidUtilities.dp(30.0f);
            this.giftDrawable.setBounds(width, height, AndroidUtilities.dp(60.0f) + width, AndroidUtilities.dp(60.0f) + height);
            this.giftDrawable.draw(canvas);
            canvas.save();
            canvas.translate((getWidth() / 2.0f) - (AndroidUtilities.dp(6.166f) / 2.0f), getHeight() / 2.0f);
            canvas.drawPath(this.arrowPath, this.arrowPaint);
            canvas.restore();
            this.userImageReceiver.setImageCoords(width + AndroidUtilities.dp(96.0f), height, AndroidUtilities.dp(60.0f), AndroidUtilities.dp(60.0f));
            this.userImageReceiver.draw(canvas);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(100.0f), 1073741824));
        }
    }

    public static class StarGiftDrawableIcon extends CompatDrawable {
        private RadialGradient gradient;
        private final ImageReceiver imageReceiver;
        private final Matrix matrix;
        private final Path path;
        private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable pattern;
        private float patternsScale;
        private final RectF rect;
        private final int sizeDp;

        public StarGiftDrawableIcon(View view, TL_stars.StarGift starGift, int i, float f) {
            super(view);
            this.path = new Path();
            this.rect = new RectF();
            this.matrix = new Matrix();
            this.patternsScale = f;
            ImageReceiver imageReceiver = new ImageReceiver(view);
            this.imageReceiver = imageReceiver;
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(view, AndroidUtilities.dp(18.0f));
            this.pattern = swapAnimatedEmojiDrawable;
            this.sizeDp = i;
            if (starGift != null) {
                TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop = (TL_stars.starGiftAttributeBackdrop) StarsController.findAttribute(starGift.attributes, TL_stars.starGiftAttributeBackdrop.class);
                TL_stars.starGiftAttributePattern stargiftattributepattern = (TL_stars.starGiftAttributePattern) StarsController.findAttribute(starGift.attributes, TL_stars.starGiftAttributePattern.class);
                TL_stars.starGiftAttributeModel stargiftattributemodel = (TL_stars.starGiftAttributeModel) StarsController.findAttribute(starGift.attributes, TL_stars.starGiftAttributeModel.class);
                if (stargiftattributebackdrop != null) {
                    this.gradient = new RadialGradient(0.0f, 0.0f, AndroidUtilities.dpf2(i) / 2.0f, new int[]{stargiftattributebackdrop.center_color | (-16777216), stargiftattributebackdrop.edge_color | (-16777216)}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                }
                if (stargiftattributepattern != null) {
                    swapAnimatedEmojiDrawable.set(stargiftattributepattern.document, false);
                }
                if (stargiftattributemodel != null) {
                    StarsIntroActivity.setGiftImage(imageReceiver, stargiftattributemodel.document, 45);
                }
            }
            this.paint.setShader(this.gradient);
            if (view.isAttachedToWindow()) {
                onAttachedToWindow();
            }
        }

        @Override
        public void draw(Canvas canvas) {
            this.rect.set(getBounds());
            canvas.save();
            this.path.rewind();
            this.path.addRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Path.Direction.CW);
            canvas.clipPath(this.path);
            if (this.gradient != null) {
                this.matrix.reset();
                this.matrix.postTranslate(this.rect.centerX(), this.rect.centerY());
                this.gradient.setLocalMatrix(this.matrix);
                this.paint.setShader(this.gradient);
            }
            canvas.drawPaint(this.paint);
            canvas.save();
            canvas.translate(this.rect.centerX(), this.rect.centerY());
            StarGiftPatterns.drawPatterns(canvas, this.pattern, this.rect.width(), this.rect.height(), 1.0f, this.patternsScale);
            canvas.restore();
            float min = Math.min(this.rect.width(), this.rect.height()) * 0.75f;
            float f = min / 2.0f;
            this.imageReceiver.setImageCoords(this.rect.centerX() - f, this.rect.centerY() - f, min, min);
            this.imageReceiver.draw(canvas);
            canvas.restore();
        }

        @Override
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(this.sizeDp);
        }

        @Override
        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(this.sizeDp);
        }

        @Override
        public void onAttachedToWindow() {
            this.pattern.attach();
            this.imageReceiver.onAttachedToWindow();
        }

        @Override
        public void onDetachedToWindow() {
            this.pattern.detach();
            this.imageReceiver.onDetachedFromWindow();
        }
    }

    public static class TopView extends FrameLayout {
        private boolean attached;
        private final TL_stars.starGiftAttributeBackdrop[] backdrop;
        private BagRandomizer backdrops;
        private final RadialGradient[] backgroundGradient;
        private final Matrix[] backgroundMatrix;
        private final Paint[] backgroundPaint;
        private final Runnable checkToRotateRunnable;
        private final ImageView closeView;
        private int currentImageIndex;
        private float currentPage;
        private final FrameLayout imageLayout;
        private final BackupImageView[] imageView;
        private final LinearLayout[] layout;
        private final FrameLayout.LayoutParams[] layoutLayoutParams;
        private BagRandomizer models;
        private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] pattern;
        private BagRandomizer patterns;
        private LinkSpanDrawable.LinksTextView priceView;
        private final Theme.ResourcesProvider resourcesProvider;
        private ValueAnimator rotationAnimator;
        private ArrayList sampleAttributes;
        private final LinkSpanDrawable.LinksTextView[] subtitleView;
        private final LinearLayout.LayoutParams[] subtitleViewLayoutParams;
        private ValueAnimator switchAnimator;
        private float switchScale;
        private final LinkSpanDrawable.LinksTextView[] titleView;
        private float toggleBackdrop;
        private int toggled;

        public class AnonymousClass1 extends AnimatorListenerAdapter {
            AnonymousClass1() {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                TopView.this.toggleBackdrop = r3.toggled;
                TopView topView = TopView.this;
                topView.onSwitchPage(topView.currentPage);
                StarsIntroActivity.setGiftImage(TopView.this.imageView[2 - TopView.this.toggled].getImageReceiver(), ((TL_stars.starGiftAttributeModel) TopView.this.models.getNext()).document, 160);
                AndroidUtilities.cancelRunOnUIThread(TopView.this.checkToRotateRunnable);
                AndroidUtilities.runOnUIThread(TopView.this.checkToRotateRunnable, 2500L);
            }
        }

        public class AnonymousClass2 extends AnimatorListenerAdapter {
            AnonymousClass2() {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                TopView.this.switchScale = 1.0f;
                TopView.this.imageLayout.setScaleX(TopView.this.switchScale);
                TopView.this.imageLayout.setScaleY(TopView.this.switchScale);
                TopView.this.invalidate();
            }
        }

        public TopView(Context context, Theme.ResourcesProvider resourcesProvider, final Runnable runnable) {
            super(context);
            this.imageView = new BackupImageView[3];
            this.currentImageIndex = 0;
            this.layout = new LinearLayout[2];
            this.layoutLayoutParams = new FrameLayout.LayoutParams[2];
            this.titleView = new LinkSpanDrawable.LinksTextView[2];
            this.subtitleView = new LinkSpanDrawable.LinksTextView[2];
            this.subtitleViewLayoutParams = new LinearLayout.LayoutParams[2];
            this.backdrop = new TL_stars.starGiftAttributeBackdrop[3];
            this.checkToRotateRunnable = new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.TopView.this.lambda$new$1();
                }
            };
            this.backgroundPaint = new Paint[3];
            this.backgroundGradient = new RadialGradient[3];
            this.backgroundMatrix = new Matrix[3];
            this.pattern = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[2];
            int i = 0;
            while (true) {
                Paint[] paintArr = this.backgroundPaint;
                if (i >= paintArr.length) {
                    break;
                }
                paintArr[i] = new Paint(1);
                i++;
            }
            int i2 = 0;
            while (true) {
                AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] swapAnimatedEmojiDrawableArr = this.pattern;
                if (i2 >= swapAnimatedEmojiDrawableArr.length) {
                    break;
                }
                swapAnimatedEmojiDrawableArr[i2] = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(this, AndroidUtilities.dp(28.0f));
                i2++;
            }
            this.switchScale = 1.0f;
            this.resourcesProvider = resourcesProvider;
            setWillNotDraw(false);
            FrameLayout frameLayout = new FrameLayout(context);
            this.imageLayout = frameLayout;
            addView(frameLayout, LayoutHelper.createFrame(160, 160.0f, 49, 0.0f, 8.0f, 0.0f, 0.0f));
            int i3 = 0;
            while (i3 < 3) {
                this.imageView[i3] = new BackupImageView(context);
                this.imageView[i3].setLayerNum(6660);
                if (i3 > 0) {
                    this.imageView[i3].getImageReceiver().setCrossfadeDuration(1);
                }
                this.imageLayout.addView(this.imageView[i3], LayoutHelper.createFrame(-1, -1, 119));
                this.imageView[i3].setAlpha(i3 == this.currentImageIndex ? 1.0f : 0.0f);
                i3++;
            }
            int i4 = 0;
            while (i4 < 2) {
                this.layout[i4] = new LinearLayout(context);
                this.layout[i4].setOrientation(1);
                View view = this.layout[i4];
                FrameLayout.LayoutParams[] layoutParamsArr = this.layoutLayoutParams;
                ViewGroup.LayoutParams createFrame = LayoutHelper.createFrame(-1, -2.0f, 119, 20.0f, 170.0f, 20.0f, 0.0f);
                layoutParamsArr[i4] = createFrame;
                addView(view, createFrame);
                this.titleView[i4] = new LinkSpanDrawable.LinksTextView(context);
                LinkSpanDrawable.LinksTextView linksTextView = this.titleView[i4];
                int i5 = Theme.key_dialogTextBlack;
                linksTextView.setTextColor(Theme.getColor(i5, resourcesProvider));
                this.titleView[i4].setTextSize(1, 20.0f);
                this.titleView[i4].setTypeface(AndroidUtilities.bold());
                this.titleView[i4].setGravity(17);
                this.layout[i4].addView(this.titleView[i4], LayoutHelper.createLinear(-1, -2, 17, 20, 0, 20, 0));
                if (i4 == 0) {
                    LinkSpanDrawable.LinksTextView linksTextView2 = new LinkSpanDrawable.LinksTextView(context);
                    this.priceView = linksTextView2;
                    linksTextView2.setTextSize(1, 18.0f);
                    this.priceView.setTypeface(AndroidUtilities.bold());
                    this.priceView.setGravity(17);
                    this.priceView.setTextColor(Theme.getColor(Theme.key_color_green, resourcesProvider));
                    this.layout[i4].addView(this.priceView, LayoutHelper.createLinear(-1, -2, 17, 0, 0, 0, 4));
                }
                this.subtitleView[i4] = new LinkSpanDrawable.LinksTextView(context);
                this.subtitleView[i4].setTextColor(Theme.getColor(i5, resourcesProvider));
                this.subtitleView[i4].setTextSize(1, 14.0f);
                this.subtitleView[i4].setGravity(17);
                this.subtitleView[i4].setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn, resourcesProvider));
                this.subtitleView[i4].setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
                this.subtitleView[i4].setDisablePaddingsOffsetY(true);
                LinearLayout linearLayout = this.layout[i4];
                LinkSpanDrawable.LinksTextView linksTextView3 = this.subtitleView[i4];
                LinearLayout.LayoutParams[] layoutParamsArr2 = this.subtitleViewLayoutParams;
                LinearLayout.LayoutParams createLinear = LayoutHelper.createLinear(-1, -2, 17, 20, 0, 20, 0);
                layoutParamsArr2[i4] = createLinear;
                linearLayout.addView(linksTextView3, createLinear);
                this.subtitleViewLayoutParams[i4].topMargin = AndroidUtilities.dp(i4 == 1 ? 7.33f : this.backdrop[0] == null ? 9.0f : 5.66f);
                i4++;
            }
            ImageView imageView = new ImageView(context);
            this.closeView = imageView;
            imageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(28.0f), 620756991));
            imageView.setImageResource(R.drawable.msg_close);
            ScaleStateListAnimator.apply(imageView);
            addView(imageView, LayoutHelper.createFrame(28, 28.0f, 53, 0.0f, 12.0f, 12.0f, 0.0f));
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view2) {
                    runnable.run();
                }
            });
        }

        private void animateSwitch() {
            ValueAnimator valueAnimator = this.switchAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.switchAnimator = null;
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.switchAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    StarGiftSheet.TopView.this.lambda$animateSwitch$3(valueAnimator2);
                }
            });
            this.switchAnimator.addListener(new AnimatorListenerAdapter() {
                AnonymousClass2() {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    TopView.this.switchScale = 1.0f;
                    TopView.this.imageLayout.setScaleX(TopView.this.switchScale);
                    TopView.this.imageLayout.setScaleY(TopView.this.switchScale);
                    TopView.this.invalidate();
                }
            });
            this.switchAnimator.setDuration(320L);
            this.switchAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
            this.switchAnimator.start();
        }

        public void lambda$animateSwitch$3(ValueAnimator valueAnimator) {
            float pow = (((float) Math.pow((r5 * 2.0f) - 2.0f, 2.0d)) * 0.075f * ((Float) valueAnimator.getAnimatedValue()).floatValue()) + 1.0f;
            this.switchScale = pow;
            this.imageLayout.setScaleX(pow);
            this.imageLayout.setScaleY(this.switchScale);
            invalidate();
        }

        public void lambda$new$1() {
            if (this.imageView[2 - this.toggled].getImageReceiver().hasImageLoaded()) {
                rotateAttributes();
            } else {
                AndroidUtilities.cancelRunOnUIThread(this.checkToRotateRunnable);
                AndroidUtilities.runOnUIThread(this.checkToRotateRunnable, 150L);
            }
        }

        public void lambda$rotateAttributes$2(ValueAnimator valueAnimator) {
            this.toggleBackdrop = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            onSwitchPage(this.currentPage);
        }

        private void rotateAttributes() {
            if (this.currentPage < 0.5f || !isAttachedToWindow()) {
                return;
            }
            AndroidUtilities.cancelRunOnUIThread(this.checkToRotateRunnable);
            ValueAnimator valueAnimator = this.rotationAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.rotationAnimator = null;
            }
            int i = 1 - this.toggled;
            this.toggled = i;
            RLottieDrawable lottieAnimation = this.imageView[2 - i].getImageReceiver().getLottieAnimation();
            RLottieDrawable lottieAnimation2 = this.imageView[this.toggled + 1].getImageReceiver().getLottieAnimation();
            if (lottieAnimation2 != null && lottieAnimation != null) {
                lottieAnimation2.setProgress(lottieAnimation.getProgress(), false);
            }
            this.models.next();
            int i2 = this.toggled + 1;
            TL_stars.starGiftAttributeBackdrop[] stargiftattributebackdropArr = this.backdrop;
            TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop = (TL_stars.starGiftAttributeBackdrop) this.backdrops.next();
            stargiftattributebackdropArr[i2] = stargiftattributebackdrop;
            setBackdropPaint(i2, stargiftattributebackdrop);
            setPattern(1, (TL_stars.starGiftAttributePattern) this.patterns.next());
            animateSwitch();
            float f = this.toggled;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(1.0f - f, f);
            this.rotationAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    StarGiftSheet.TopView.this.lambda$rotateAttributes$2(valueAnimator2);
                }
            });
            this.rotationAnimator.addListener(new AnimatorListenerAdapter() {
                AnonymousClass1() {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    TopView.this.toggleBackdrop = r3.toggled;
                    TopView topView = TopView.this;
                    topView.onSwitchPage(topView.currentPage);
                    StarsIntroActivity.setGiftImage(TopView.this.imageView[2 - TopView.this.toggled].getImageReceiver(), ((TL_stars.starGiftAttributeModel) TopView.this.models.getNext()).document, 160);
                    AndroidUtilities.cancelRunOnUIThread(TopView.this.checkToRotateRunnable);
                    AndroidUtilities.runOnUIThread(TopView.this.checkToRotateRunnable, 2500L);
                }
            });
            this.rotationAnimator.setDuration(320L);
            this.rotationAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.rotationAnimator.start();
        }

        private void setBackdropPaint(int i, TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop) {
            if (stargiftattributebackdrop == null) {
                return;
            }
            this.backgroundGradient[i] = new RadialGradient(0.0f, 0.0f, AndroidUtilities.dp(200.0f), new int[]{stargiftattributebackdrop.center_color | (-16777216), stargiftattributebackdrop.edge_color | (-16777216)}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            this.backgroundMatrix[i] = new Matrix();
            this.backgroundPaint[i].setShader(this.backgroundGradient[i]);
        }

        private void setPattern(int i, TL_stars.starGiftAttributePattern stargiftattributepattern) {
            if (stargiftattributepattern == null) {
                return;
            }
            this.pattern[i].set(stargiftattributepattern.document, true);
        }

        @Override
        protected void dispatchDraw(android.graphics.Canvas r19) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarGiftSheet.TopView.dispatchDraw(android.graphics.Canvas):void");
        }

        public float getRealHeight() {
            return AndroidUtilities.lerp(AndroidUtilities.dp(this.backdrop[0] != null ? 24.0f : 10.0f), AndroidUtilities.dp(this.backdrop[1] != null ? 24.0f : 10.0f), this.currentPage) + AndroidUtilities.dp(160.0f) + AndroidUtilities.lerp(this.layout[0].getMeasuredHeight(), this.layout[1].getMeasuredHeight(), this.currentPage);
        }

        @Override
        protected void onAttachedToWindow() {
            this.attached = true;
            super.onAttachedToWindow();
            this.pattern[0].attach();
            this.pattern[1].attach();
        }

        @Override
        protected void onDetachedFromWindow() {
            this.attached = false;
            super.onDetachedFromWindow();
            this.pattern[0].detach();
            this.pattern[1].detach();
            AndroidUtilities.cancelRunOnUIThread(this.checkToRotateRunnable);
        }

        public void onSwitchPage(float r12) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarGiftSheet.TopView.onSwitchPage(float):void");
        }

        public void prepareSwitchPage(int i, int i2) {
            if (i != i2) {
                RLottieDrawable lottieAnimation = this.imageView[i].getImageReceiver().getLottieAnimation();
                RLottieDrawable lottieAnimation2 = this.imageView[i2].getImageReceiver().getLottieAnimation();
                if (lottieAnimation2 == null || lottieAnimation == null) {
                    return;
                }
                lottieAnimation2.setProgress(lottieAnimation.getProgress(), false);
            }
        }

        public void setGift(TL_stars.StarGift starGift) {
            LinkSpanDrawable.LinksTextView linksTextView;
            float f;
            if (starGift instanceof TL_stars.TL_starGiftUnique) {
                this.backdrop[0] = (TL_stars.starGiftAttributeBackdrop) StarsController.findAttribute(starGift.attributes, TL_stars.starGiftAttributeBackdrop.class);
                setPattern(0, (TL_stars.starGiftAttributePattern) StarsController.findAttribute(starGift.attributes, TL_stars.starGiftAttributePattern.class));
                linksTextView = this.subtitleView[0];
                f = 13.0f;
            } else {
                this.backdrop[0] = null;
                setPattern(0, null);
                linksTextView = this.subtitleView[0];
                f = 14.0f;
            }
            linksTextView.setTextSize(1, f);
            setBackdropPaint(0, this.backdrop[0]);
            StarsIntroActivity.setGiftImage(this.imageView[0].getImageReceiver(), starGift, 160);
        }

        public void setPreviewingAttributes(ArrayList<TL_stars.StarGiftAttribute> arrayList) {
            this.sampleAttributes = arrayList;
            this.models = new BagRandomizer(StarsController.findAttributes(arrayList, TL_stars.starGiftAttributeModel.class));
            this.patterns = new BagRandomizer(StarsController.findAttributes(arrayList, TL_stars.starGiftAttributePattern.class));
            this.backdrops = new BagRandomizer(StarsController.findAttributes(arrayList, TL_stars.starGiftAttributeBackdrop.class));
            this.subtitleView[1].setTextSize(1, 14.0f);
            this.toggleBackdrop = 0.0f;
            this.toggled = 0;
            setPattern(1, (TL_stars.starGiftAttributePattern) this.patterns.next());
            StarsIntroActivity.setGiftImage(this.imageView[1].getImageReceiver(), ((TL_stars.starGiftAttributeModel) this.models.next()).document, 160);
            TL_stars.starGiftAttributeBackdrop[] stargiftattributebackdropArr = this.backdrop;
            TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop = (TL_stars.starGiftAttributeBackdrop) this.backdrops.next();
            stargiftattributebackdropArr[1] = stargiftattributebackdrop;
            setBackdropPaint(1, stargiftattributebackdrop);
            StarsIntroActivity.setGiftImage(this.imageView[2].getImageReceiver(), ((TL_stars.starGiftAttributeModel) this.models.getNext()).document, 160);
            AndroidUtilities.cancelRunOnUIThread(this.checkToRotateRunnable);
            AndroidUtilities.runOnUIThread(this.checkToRotateRunnable, 2500L);
            invalidate();
        }

        public void setText(int i, CharSequence charSequence, long j, CharSequence charSequence2) {
            this.titleView[i].setText(charSequence);
            if (i == 0) {
                this.priceView.setTextColor(Theme.getColor(Theme.key_color_green, this.resourcesProvider));
                this.priceView.setText(StarsIntroActivity.replaceStarsWithPlain(LocaleController.formatNumber((int) j, ' ') + " ⭐️", 0.8f));
                this.priceView.setVisibility(j != 0 ? 0 : 8);
            }
            this.subtitleView[i].setText(charSequence2);
            this.subtitleView[i].setVisibility(TextUtils.isEmpty(charSequence2) ? 8 : 0);
        }
    }

    public static class UpgradeIcon extends CompatDrawable {
        private final Path arrow;
        private final long start;
        private final Paint strokePaint;
        private final View view;

        public UpgradeIcon(View view, int i) {
            super(view);
            Paint paint = new Paint(1);
            this.strokePaint = paint;
            Path path = new Path();
            this.arrow = path;
            this.start = System.currentTimeMillis();
            this.view = view;
            if (view.isAttachedToWindow()) {
                onAttachedToWindow();
            }
            this.paint.setColor(-1);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(i);
            path.rewind();
            path.moveTo(-AndroidUtilities.dpf2(2.91f), AndroidUtilities.dpf2(1.08f));
            path.lineTo(0.0f, -AndroidUtilities.dpf2(1.08f));
            path.lineTo(AndroidUtilities.dpf2(2.91f), AndroidUtilities.dpf2(1.08f));
        }

        @Override
        public void draw(Canvas canvas) {
            canvas.drawCircle(getBounds().centerX(), getBounds().centerY(), getBounds().width() / 2.0f, this.paint);
            float currentTimeMillis = ((float) ((System.currentTimeMillis() - this.start) % 400)) / 400.0f;
            this.strokePaint.setStrokeWidth(AndroidUtilities.dpf2(1.33f));
            canvas.save();
            canvas.translate(getBounds().centerX(), getBounds().centerY() - (((AndroidUtilities.dpf2(2.16f) * 3.0f) + (AndroidUtilities.dpf2(1.166f) * 2.0f)) / 2.0f));
            int i = 0;
            while (i < 4) {
                float f = i == 0 ? 1.0f - currentTimeMillis : i == 3 ? currentTimeMillis : 1.0f;
                this.strokePaint.setAlpha((int) (255.0f * f));
                canvas.save();
                float lerp = AndroidUtilities.lerp(0.5f, 1.0f, f);
                canvas.scale(lerp, lerp);
                canvas.drawPath(this.arrow, this.strokePaint);
                canvas.restore();
                canvas.translate(0.0f, AndroidUtilities.dpf2(3.3260002f) * f);
                i++;
            }
            canvas.restore();
            View view = this.view;
            if (view != null) {
                view.invalidate();
            }
        }

        @Override
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(18.0f);
        }

        @Override
        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(18.0f);
        }
    }

    public StarGiftSheet(Context context, int i, long j, Theme.ResourcesProvider resourcesProvider) {
        super(context, false, resourcesProvider);
        this.firstSet = true;
        this.currentAccount = i;
        this.dialogId = j;
        ContainerView containerView = new ContainerView(context);
        this.container = containerView;
        this.containerView = containerView;
        fixNavigationBar(getThemedColor(Theme.key_dialogBackground));
        LinearLayout linearLayout = new LinearLayout(context);
        this.infoLayout = linearLayout;
        linearLayout.setOrientation(1);
        linearLayout.setPadding(this.backgroundPaddingLeft + AndroidUtilities.dp(14.0f), AndroidUtilities.dp(16.0f), this.backgroundPaddingLeft + AndroidUtilities.dp(14.0f), AndroidUtilities.dp(56.0f));
        this.containerView.addView(linearLayout, LayoutHelper.createFrame(-1, -1, 87));
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        this.beforeTableTextView = linksTextView;
        int i2 = Theme.key_dialogTextGray2;
        linksTextView.setTextColor(Theme.getColor(i2, resourcesProvider));
        linksTextView.setTextSize(1, 14.0f);
        linksTextView.setGravity(17);
        linksTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        int i3 = Theme.key_chat_messageLinkIn;
        linksTextView.setLinkTextColor(Theme.getColor(i3, resourcesProvider));
        linksTextView.setDisablePaddingsOffsetY(true);
        linearLayout.addView(linksTextView, LayoutHelper.createLinear(-2, -2, 1, 5, -4, 5, 10));
        linksTextView.setVisibility(8);
        TableView tableView = new TableView(context, resourcesProvider);
        this.tableView = tableView;
        linearLayout.addView(tableView, LayoutHelper.createLinear(-1, -2, 0.0f, 0.0f, 0.0f, 12.0f));
        LinkSpanDrawable.LinksTextView linksTextView2 = new LinkSpanDrawable.LinksTextView(context, resourcesProvider);
        this.afterTableTextView = linksTextView2;
        linksTextView2.setTextColor(Theme.getColor(i2, resourcesProvider));
        linksTextView2.setTextSize(1, 14.0f);
        linksTextView2.setGravity(17);
        linksTextView2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        linksTextView2.setLinkTextColor(Theme.getColor(i3, resourcesProvider));
        linksTextView2.setDisablePaddingsOffsetY(true);
        linearLayout.addView(linksTextView2, LayoutHelper.createLinear(-2, -2, 1, 5, 6, 5, 16));
        linksTextView2.setVisibility(8);
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, resourcesProvider);
        this.button = buttonWithCounterView;
        buttonWithCounterView.setText(LocaleController.getString(R.string.OK), false);
        FrameLayout.LayoutParams createFrame = LayoutHelper.createFrame(-1, 48.0f, 87, 0.0f, 0.0f, 0.0f, 4.0f);
        createFrame.leftMargin = this.backgroundPaddingLeft + AndroidUtilities.dp(14.0f);
        createFrame.rightMargin = this.backgroundPaddingLeft + AndroidUtilities.dp(14.0f);
        this.containerView.addView(buttonWithCounterView, createFrame);
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.upgradeLayout = linearLayout2;
        linearLayout2.setOrientation(1);
        linearLayout2.setPadding(AndroidUtilities.dp(4.0f) + this.backgroundPaddingLeft, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(4.0f) + this.backgroundPaddingLeft, AndroidUtilities.dp(56.0f));
        this.containerView.addView(linearLayout2, LayoutHelper.createFrame(-1, -1, 87));
        this.featureCells = r8;
        AffiliateProgramFragment.FeatureCell featureCell = new AffiliateProgramFragment.FeatureCell(context, resourcesProvider);
        featureCell.set(R.drawable.menu_feature_unique, LocaleController.getString(R.string.Gift2UpgradeFeature1Title), LocaleController.getString(R.string.Gift2UpgradeFeature1Text));
        linearLayout2.addView(r8[0], LayoutHelper.createLinear(-1, -2));
        AffiliateProgramFragment.FeatureCell featureCell2 = new AffiliateProgramFragment.FeatureCell(context, resourcesProvider);
        featureCell2.set(R.drawable.menu_feature_transfer, LocaleController.getString(R.string.Gift2UpgradeFeature2Title), LocaleController.getString(R.string.Gift2UpgradeFeature2Text));
        linearLayout2.addView(r8[1], LayoutHelper.createLinear(-1, -2));
        AffiliateProgramFragment.FeatureCell[] featureCellArr = {featureCell, featureCell2, new AffiliateProgramFragment.FeatureCell(context, resourcesProvider)};
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString(R.string.Gift2UpgradeFeature3Title));
        spannableStringBuilder.append((CharSequence) "  d");
        FilterCreateActivity.NewSpan newSpan = new FilterCreateActivity.NewSpan(10.0f);
        newSpan.setColor(getThemedColor(Theme.key_featuredStickers_addButton));
        newSpan.setText(LocaleController.getString(R.string.Gift2UpgradeFeatureSoon));
        spannableStringBuilder.setSpan(newSpan, spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 33);
        featureCellArr[2].set(R.drawable.menu_feature_tradable, spannableStringBuilder, LocaleController.getString(R.string.Gift2UpgradeFeature3Text));
        linearLayout2.addView(featureCellArr[2], LayoutHelper.createLinear(-1, -2));
        View view = new View(context);
        this.checkboxSeparator = view;
        view.setBackgroundColor(Theme.getColor(Theme.key_divider, resourcesProvider));
        linearLayout2.addView(view, LayoutHelper.createLinear(-2, 1.0f / AndroidUtilities.density, 7, 17, -4, 17, 6));
        LinearLayout linearLayout3 = new LinearLayout(context);
        this.checkboxLayout = linearLayout3;
        linearLayout3.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(8.0f));
        linearLayout3.setOrientation(0);
        linearLayout3.setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), 6, 6));
        CheckBox2 checkBox2 = new CheckBox2(context, 24, resourcesProvider);
        this.checkbox = checkBox2;
        checkBox2.setColor(Theme.key_radioBackgroundChecked, Theme.key_checkboxDisabled, Theme.key_checkboxCheck);
        checkBox2.setDrawUnchecked(true);
        checkBox2.setChecked(false, false);
        checkBox2.setDrawBackgroundAsArc(10);
        linearLayout3.addView(checkBox2, LayoutHelper.createLinear(24, 24, 16, 0, 0, 0, 0));
        TextView textView = new TextView(context);
        this.checkboxTextView = textView;
        textView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        textView.setTextSize(1, 14.0f);
        textView.setText(LocaleController.getString(R.string.Gift2AddSenderName));
        linearLayout3.addView(textView, LayoutHelper.createLinear(-2, -2, 16, 9, 0, 0, 0));
        linearLayout2.addView(linearLayout3, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 4));
        ScaleStateListAnimator.apply(linearLayout3, 0.025f, 1.5f);
        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                StarGiftSheet.this.lambda$new$0(view2);
            }
        });
        linearLayout.setAlpha(1.0f);
        linearLayout2.setAlpha(0.0f);
        TopView topView = new TopView(context, resourcesProvider, new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.onBackPressed();
            }
        });
        this.topView = topView;
        int i4 = this.backgroundPaddingLeft;
        topView.setPadding(i4, 0, i4, 0);
        this.containerView.addView(topView, LayoutHelper.createFrame(-1, -2, 87));
        FireworksOverlay fireworksOverlay = new FireworksOverlay(context);
        this.fireworksOverlay = fireworksOverlay;
        this.container.addView(fireworksOverlay, LayoutHelper.createFrame(-1, -1.0f));
    }

    private boolean applyNewGiftFromUpdates(TLRPC.Updates updates) {
        TLRPC.TL_updateNewMessage tL_updateNewMessage;
        if (updates == null) {
            return false;
        }
        TLRPC.Update update = updates.update;
        if (update instanceof TLRPC.TL_updateNewMessage) {
            tL_updateNewMessage = (TLRPC.TL_updateNewMessage) update;
        } else {
            if (updates.updates != null) {
                for (int i = 0; i < updates.updates.size(); i++) {
                    TLRPC.Update update2 = updates.updates.get(i);
                    if (update2 instanceof TLRPC.TL_updateNewMessage) {
                        tL_updateNewMessage = (TLRPC.TL_updateNewMessage) update2;
                        break;
                    }
                }
            }
            tL_updateNewMessage = null;
        }
        if (tL_updateNewMessage == null) {
            return false;
        }
        this.userStarGift = null;
        this.myProfile = false;
        set(new MessageObject(this.currentAccount, tL_updateNewMessage.message, false, false));
        return true;
    }

    private boolean canConvert() {
        MessageObject messageObject = this.messageObject;
        if (messageObject != null) {
            TLRPC.MessageAction messageAction = messageObject.messageOwner.action;
            if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
                TLRPC.TL_messageActionStarGift tL_messageActionStarGift = (TLRPC.TL_messageActionStarGift) messageAction;
                return (!messageObject.isOutOwner() || ((this.messageObject.getDialogId() > UserConfig.getInstance(this.currentAccount).getClientUserId() ? 1 : (this.messageObject.getDialogId() == UserConfig.getInstance(this.currentAccount).getClientUserId() ? 0 : -1)) == 0)) && !tL_messageActionStarGift.converted && tL_messageActionStarGift.convert_stars > 0 && MessagesController.getInstance(this.currentAccount).stargiftsConvertPeriodMax - (ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - this.messageObject.messageOwner.date) > 0;
            }
        } else {
            TL_stars.UserStarGift userStarGift = this.userStarGift;
            if (userStarGift != null) {
                int currentTime = MessagesController.getInstance(this.currentAccount).stargiftsConvertPeriodMax - (ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - userStarGift.date);
                if (this.myProfile) {
                    int i = this.userStarGift.flags;
                    if ((i & 8) != 0 && (i & 16) != 0 && (i & 2) != 0 && currentTime > 0) {
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    public void convert() {
        long j;
        final int i;
        int i2;
        final long j2;
        long j3;
        final long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        MessageObject messageObject = this.messageObject;
        if (messageObject != null) {
            i = messageObject.getId();
            MessageObject messageObject2 = this.messageObject;
            int i3 = messageObject2.messageOwner.date;
            boolean isOutOwner = messageObject2.isOutOwner();
            long j4 = this.dialogId;
            j = isOutOwner ? clientUserId : j4;
            j3 = ((TLRPC.TL_messageActionStarGift) this.messageObject.messageOwner.action).convert_stars;
            i2 = i3;
            j2 = j4;
        } else {
            TL_stars.UserStarGift userStarGift = this.userStarGift;
            if (userStarGift == null) {
                return;
            }
            int i4 = userStarGift.msg_id;
            int i5 = userStarGift.date;
            j = ((userStarGift.flags & 2) == 0 || userStarGift.name_hidden) ? 2666000L : userStarGift.from_id;
            long j5 = userStarGift.convert_stars;
            long j6 = userStarGift.from_id;
            i = i4;
            i2 = i5;
            j2 = j6;
            j3 = j5;
        }
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j2));
        int max = Math.max(1, (MessagesController.getInstance(this.currentAccount).stargiftsConvertPeriodMax - (ConnectionsManager.getInstance(this.currentAccount).getCurrentTime() - i2)) / 86400);
        final long j7 = j3;
        new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString(R.string.Gift2ConvertTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatPluralString("Gift2ConvertText2", max, (user == null || UserObject.isService(j)) ? LocaleController.getString(R.string.StarsTransactionHidden) : UserObject.getForcedFirstName(user), LocaleController.formatPluralStringComma("Gift2ConvertStars", (int) j3)))).setPositiveButton(LocaleController.getString(R.string.Gift2ConvertButton), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i6) {
                StarGiftSheet.this.lambda$convert$29(i, j2, clientUserId, j7, dialogInterface, i6);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).show();
    }

    private void doTransfer(final long j, final Runnable runnable) {
        TLRPC.Message message;
        long j2;
        int i;
        final String str;
        RequestDelegate requestDelegate;
        ConnectionsManager connectionsManager;
        TLRPC.TL_payments_getPaymentForm tL_payments_getPaymentForm;
        TL_stars.UserStarGift userStarGift = this.userStarGift;
        if (userStarGift == null || !(userStarGift.gift instanceof TL_stars.TL_starGiftUnique)) {
            MessageObject messageObject = this.messageObject;
            if (messageObject == null || (message = messageObject.messageOwner) == null) {
                return;
            }
            TLRPC.MessageAction messageAction = message.action;
            if (!(messageAction instanceof TLRPC.TL_messageActionStarGiftUnique)) {
                return;
            }
            TLRPC.TL_messageActionStarGiftUnique tL_messageActionStarGiftUnique = (TLRPC.TL_messageActionStarGiftUnique) messageAction;
            int id = messageObject.getId();
            j2 = tL_messageActionStarGiftUnique.transfer_stars;
            i = id;
            str = tL_messageActionStarGiftUnique.gift.title + " #" + LocaleController.formatNumber(tL_messageActionStarGiftUnique.gift.num, ',');
        } else {
            i = userStarGift.msg_id;
            j2 = userStarGift.transfer_stars;
            str = this.userStarGift.gift.title + " #" + LocaleController.formatNumber(this.userStarGift.gift.num, ',');
        }
        if (j2 <= 0) {
            TL_stars.transferStarGift transferstargift = new TL_stars.transferStarGift();
            transferstargift.msg_id = i;
            transferstargift.to_id = MessagesController.getInstance(this.currentAccount).getInputUser(j);
            ConnectionsManager connectionsManager2 = ConnectionsManager.getInstance(this.currentAccount);
            final String str2 = str;
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    StarGiftSheet.this.lambda$doTransfer$53(runnable, j, str2, tLObject, tL_error);
                }
            };
            tL_payments_getPaymentForm = transferstargift;
            connectionsManager = connectionsManager2;
        } else {
            final TLRPC.TL_inputInvoiceStarGiftTransfer tL_inputInvoiceStarGiftTransfer = new TLRPC.TL_inputInvoiceStarGiftTransfer();
            tL_inputInvoiceStarGiftTransfer.msg_id = i;
            tL_inputInvoiceStarGiftTransfer.to_id = MessagesController.getInstance(this.currentAccount).getInputUser(j);
            TLRPC.TL_payments_getPaymentForm tL_payments_getPaymentForm2 = new TLRPC.TL_payments_getPaymentForm();
            tL_payments_getPaymentForm2.invoice = tL_inputInvoiceStarGiftTransfer;
            JSONObject makeThemeParams = BotWebViewSheet.makeThemeParams(this.resourcesProvider);
            if (makeThemeParams != null) {
                TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
                tL_payments_getPaymentForm2.theme_params = tL_dataJSON;
                tL_dataJSON.data = makeThemeParams.toString();
                tL_payments_getPaymentForm2.flags |= 1;
            }
            ConnectionsManager connectionsManager3 = ConnectionsManager.getInstance(this.currentAccount);
            requestDelegate = new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    StarGiftSheet.this.lambda$doTransfer$61(tL_inputInvoiceStarGiftTransfer, runnable, j, str, tLObject, tL_error);
                }
            };
            tL_payments_getPaymentForm = tL_payments_getPaymentForm2;
            connectionsManager = connectionsManager3;
        }
        connectionsManager.sendRequest(tL_payments_getPaymentForm, requestDelegate);
    }

    private void doUpgrade() {
        int i;
        final long j;
        if (this.button.isLoading()) {
            return;
        }
        MessageObject messageObject = this.messageObject;
        if (messageObject != null) {
            i = messageObject.getId();
            TLRPC.MessageAction messageAction = this.messageObject.messageOwner.action;
            if (!(messageAction instanceof TLRPC.TL_messageActionStarGift)) {
                return;
            } else {
                j = ((TLRPC.TL_messageActionStarGift) messageAction).upgrade_stars;
            }
        } else {
            TL_stars.UserStarGift userStarGift = this.userStarGift;
            if (userStarGift == null) {
                return;
            }
            int i2 = userStarGift.msg_id;
            long j2 = userStarGift.upgrade_stars;
            i = i2;
            j = j2;
        }
        if (j > 0 || this.upgrade_form != null) {
            this.button.setLoading(true);
            if (j > 0) {
                TL_stars.upgradeStarGift upgradestargift = new TL_stars.upgradeStarGift();
                upgradestargift.keep_original_details = this.checkbox.isChecked();
                upgradestargift.msg_id = i;
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(upgradestargift, new RequestDelegate() {
                    @Override
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        StarGiftSheet.this.lambda$doUpgrade$42(tLObject, tL_error);
                    }
                });
                return;
            }
            TLRPC.TL_inputInvoiceStarGiftUpgrade tL_inputInvoiceStarGiftUpgrade = new TLRPC.TL_inputInvoiceStarGiftUpgrade();
            tL_inputInvoiceStarGiftUpgrade.keep_original_details = this.checkbox.isChecked();
            tL_inputInvoiceStarGiftUpgrade.msg_id = i;
            TL_stars.TL_payments_sendStarsForm tL_payments_sendStarsForm = new TL_stars.TL_payments_sendStarsForm();
            tL_payments_sendStarsForm.form_id = this.upgrade_form.form_id;
            tL_payments_sendStarsForm.invoice = tL_inputInvoiceStarGiftUpgrade;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_sendStarsForm, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    StarGiftSheet.this.lambda$doUpgrade$47(j, tLObject, tL_error);
                }
            });
        }
    }

    public static void lambda$convert$26(StarsIntroActivity starsIntroActivity, long j) {
        BulletinFactory.of(starsIntroActivity).createSimpleBulletin(R.raw.stars_topup, LocaleController.getString(R.string.Gift2ConvertedTitle), LocaleController.formatPluralStringComma("Gift2Converted", (int) j)).show(true);
    }

    public void lambda$convert$27(AlertDialog alertDialog, TLObject tLObject, long j, long j2, final long j3, TLRPC.TL_error tL_error) {
        alertDialog.dismissUnless(400L);
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        if (!(tLObject instanceof TLRPC.TL_boolTrue)) {
            BulletinFactory.of(this.topBulletinContainer, this.resourcesProvider).createErrorBulletin(tL_error != null ? LocaleController.formatString(R.string.UnknownErrorCode, tL_error.text) : LocaleController.getString(R.string.UnknownError)).show(false);
            return;
        }
        dismiss();
        StarsController.getInstance(this.currentAccount).invalidateProfileGifts(j);
        TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(j2);
        if (userFull != null) {
            int max = Math.max(0, userFull.stargifts_count - 1);
            userFull.stargifts_count = max;
            if (max <= 0) {
                userFull.flags2 &= -257;
            }
        }
        StarsController.getInstance(this.currentAccount).invalidateBalance();
        StarsController.getInstance(this.currentAccount).invalidateTransactions(true);
        if (safeLastFragment instanceof StarsIntroActivity) {
            BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.stars_topup, LocaleController.getString(R.string.Gift2ConvertedTitle), LocaleController.formatPluralStringComma("Gift2Converted", (int) j3)).show(true);
            return;
        }
        final StarsIntroActivity starsIntroActivity = new StarsIntroActivity();
        starsIntroActivity.whenFullyVisible(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.lambda$convert$26(StarsIntroActivity.this, j3);
            }
        });
        safeLastFragment.presentFragment(starsIntroActivity);
    }

    public void lambda$convert$28(final AlertDialog alertDialog, final long j, final long j2, final long j3, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$convert$27(alertDialog, tLObject, j, j2, j3, tL_error);
            }
        });
    }

    public void lambda$convert$29(int i, final long j, final long j2, final long j3, DialogInterface dialogInterface, int i2) {
        final AlertDialog alertDialog = new AlertDialog(ApplicationLoader.applicationContext, 3);
        alertDialog.showDelayed(500L);
        TL_stars.convertStarGift convertstargift = new TL_stars.convertStarGift();
        convertstargift.msg_id = i;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(convertstargift, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarGiftSheet.this.lambda$convert$28(alertDialog, j, j2, j3, tLObject, tL_error);
            }
        });
    }

    public static void lambda$doTransfer$51(ChatActivity chatActivity, String str, TLRPC.User user) {
        BulletinFactory.of(chatActivity).createSimpleBulletin(R.raw.forward, LocaleController.getString(R.string.Gift2TransferredTitle), AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2TransferredText, str, UserObject.getForcedFirstName(user)))).show(true);
    }

    public void lambda$doTransfer$52(Runnable runnable, TLObject tLObject, long j, final String str, TLRPC.TL_error tL_error) {
        if (runnable != null) {
            runnable.run();
        }
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            if (tLObject instanceof TLRPC.Updates) {
                final TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
                final ChatActivity of = ChatActivity.of(j);
                of.whenFullyVisible(new Runnable() {
                    @Override
                    public final void run() {
                        StarGiftSheet.lambda$doTransfer$51(ChatActivity.this, str, user);
                    }
                });
                safeLastFragment.presentFragment(of);
            } else {
                BulletinFactory.of(safeLastFragment).showForError(tL_error);
            }
        }
        StarsController.getInstance(this.currentAccount).invalidateProfileGifts(UserConfig.getInstance(this.currentAccount).getClientUserId());
    }

    public void lambda$doTransfer$53(final Runnable runnable, final long j, final String str, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.Updates) {
            MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.Updates) tLObject, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doTransfer$52(runnable, tLObject, j, str, tL_error);
            }
        });
    }

    public static void lambda$doTransfer$54(ChatActivity chatActivity, String str, TLRPC.User user) {
        BulletinFactory.of(chatActivity).createSimpleBulletin(R.raw.forward, LocaleController.getString(R.string.Gift2TransferredTitle), AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2TransferredText, str, UserObject.getForcedFirstName(user)))).show(true);
    }

    public void lambda$doTransfer$55(TLRPC.TL_payments_paymentResult tL_payments_paymentResult) {
        MessagesController.getInstance(this.currentAccount).processUpdates(tL_payments_paymentResult.updates, false);
    }

    public void lambda$doTransfer$56(boolean[] zArr, long j, Runnable runnable) {
        zArr[0] = true;
        this.button.setLoading(false);
        doTransfer(j, runnable);
    }

    public void lambda$doTransfer$57(DialogInterface dialogInterface) {
        this.button.setLoading(false);
    }

    public void lambda$doTransfer$58(TLObject tLObject, final Runnable runnable, final long j, final String str, TLRPC.TL_error tL_error, long j2) {
        if (!(tLObject instanceof TLRPC.TL_payments_paymentResult)) {
            if (tL_error == null || !"BALANCE_TOO_LOW".equals(tL_error.text)) {
                BulletinFactory.of(this.topBulletinContainer, this.resourcesProvider).showForError(tL_error);
                return;
            }
            if (!MessagesController.getInstance(this.currentAccount).starsPurchaseAvailable()) {
                this.button.setLoading(false);
                StarsController.showNoSupportDialog(getContext(), this.resourcesProvider);
                return;
            } else {
                final boolean[] zArr = {false};
                StarsIntroActivity.StarsNeededSheet starsNeededSheet = new StarsIntroActivity.StarsNeededSheet(getContext(), this.resourcesProvider, j2, 11, null, new Runnable() {
                    @Override
                    public final void run() {
                        StarGiftSheet.this.lambda$doTransfer$56(zArr, j, runnable);
                    }
                });
                starsNeededSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public final void onDismiss(DialogInterface dialogInterface) {
                        StarGiftSheet.this.lambda$doTransfer$57(dialogInterface);
                    }
                });
                starsNeededSheet.show();
                return;
            }
        }
        final TLRPC.TL_payments_paymentResult tL_payments_paymentResult = (TLRPC.TL_payments_paymentResult) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(tL_payments_paymentResult.updates.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(tL_payments_paymentResult.updates.chats, false);
        StarsController.getInstance(this.currentAccount).invalidateTransactions(false);
        StarsController.getInstance(this.currentAccount).invalidateProfileGifts(UserConfig.getInstance(this.currentAccount).getClientUserId());
        StarsController.getInstance(this.currentAccount).invalidateBalance();
        if (runnable != null) {
            runnable.run();
        }
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            final TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
            final ChatActivity of = ChatActivity.of(j);
            of.whenFullyVisible(new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.lambda$doTransfer$54(ChatActivity.this, str, user);
                }
            });
            safeLastFragment.presentFragment(of);
        }
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doTransfer$55(tL_payments_paymentResult);
            }
        });
    }

    public void lambda$doTransfer$59(final Runnable runnable, final long j, final String str, final long j2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doTransfer$58(tLObject, runnable, j, str, tL_error, j2);
            }
        });
    }

    public void lambda$doTransfer$60(TLObject tLObject, TLRPC.TL_inputInvoiceStarGiftTransfer tL_inputInvoiceStarGiftTransfer, final Runnable runnable, final long j, final String str, TLRPC.TL_error tL_error) {
        if (!(tLObject instanceof TLRPC.PaymentForm)) {
            BulletinFactory.of(this.topBulletinContainer, this.resourcesProvider).makeForError(tL_error).show();
            return;
        }
        TLRPC.PaymentForm paymentForm = (TLRPC.PaymentForm) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(paymentForm.users, false);
        TL_stars.TL_payments_sendStarsForm tL_payments_sendStarsForm = new TL_stars.TL_payments_sendStarsForm();
        tL_payments_sendStarsForm.form_id = paymentForm.form_id;
        tL_payments_sendStarsForm.invoice = tL_inputInvoiceStarGiftTransfer;
        Iterator<TLRPC.TL_labeledPrice> it = paymentForm.invoice.prices.iterator();
        final long j2 = 0;
        while (it.hasNext()) {
            j2 += it.next().amount;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_payments_sendStarsForm, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                StarGiftSheet.this.lambda$doTransfer$59(runnable, j, str, j2, tLObject2, tL_error2);
            }
        });
    }

    public void lambda$doTransfer$61(final TLRPC.TL_inputInvoiceStarGiftTransfer tL_inputInvoiceStarGiftTransfer, final Runnable runnable, final long j, final String str, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doTransfer$60(tLObject, tL_inputInvoiceStarGiftTransfer, runnable, j, str, tL_error);
            }
        });
    }

    public void lambda$doUpgrade$40(TLObject tLObject) {
        MessagesController.getInstance(this.currentAccount).processUpdates((TLRPC.Updates) tLObject, false);
    }

    public void lambda$doUpgrade$41(TLRPC.TL_error tL_error, final TLObject tLObject) {
        String str;
        if (tL_error != null || !(tLObject instanceof TLRPC.Updates)) {
            BulletinFactory.of(this.topBulletinContainer, this.resourcesProvider).showForError(tL_error);
            return;
        }
        StarsController.getInstance(this.currentAccount).invalidateProfileGifts(UserConfig.getInstance(this.currentAccount).getClientUserId());
        if (!applyNewGiftFromUpdates((TLRPC.Updates) tLObject)) {
            dismiss();
            return;
        }
        this.button.setLoading(false);
        this.fireworksOverlay.start(true);
        switchPage(false, true);
        if (getGift() != null) {
            str = getGift().title + " #" + LocaleController.formatNumber(getGift().num, ',');
        } else {
            str = "";
        }
        BulletinFactory.of(this.topBulletinContainer, this.resourcesProvider).createSimpleBulletin(R.raw.gift_upgrade, LocaleController.getString(R.string.Gift2UpgradedTitle), AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2UpgradedText, str))).setDuration(5000).show();
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doUpgrade$40(tLObject);
            }
        });
    }

    public void lambda$doUpgrade$42(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.Updates) {
            TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(updates.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(updates.chats, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doUpgrade$41(tL_error, tLObject);
            }
        });
    }

    public void lambda$doUpgrade$43(TLRPC.TL_payments_paymentResult tL_payments_paymentResult) {
        MessagesController.getInstance(this.currentAccount).processUpdates(tL_payments_paymentResult.updates, false);
    }

    public void lambda$doUpgrade$44(boolean[] zArr) {
        zArr[0] = true;
        this.button.setLoading(false);
        doUpgrade();
    }

    public void lambda$doUpgrade$45(DialogInterface dialogInterface) {
        this.button.setLoading(false);
    }

    public void lambda$doUpgrade$46(TLObject tLObject, TLRPC.TL_error tL_error, long j) {
        String str;
        if (!(tLObject instanceof TLRPC.TL_payments_paymentResult)) {
            if (tL_error == null || !"BALANCE_TOO_LOW".equals(tL_error.text)) {
                BulletinFactory.of(this.topBulletinContainer, this.resourcesProvider).showForError(tL_error);
                return;
            }
            if (!MessagesController.getInstance(this.currentAccount).starsPurchaseAvailable()) {
                this.button.setLoading(false);
                StarsController.showNoSupportDialog(getContext(), this.resourcesProvider);
                return;
            } else {
                final boolean[] zArr = {false};
                StarsIntroActivity.StarsNeededSheet starsNeededSheet = new StarsIntroActivity.StarsNeededSheet(getContext(), this.resourcesProvider, j, 10, null, new Runnable() {
                    @Override
                    public final void run() {
                        StarGiftSheet.this.lambda$doUpgrade$44(zArr);
                    }
                });
                starsNeededSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public final void onDismiss(DialogInterface dialogInterface) {
                        StarGiftSheet.this.lambda$doUpgrade$45(dialogInterface);
                    }
                });
                starsNeededSheet.show();
                return;
            }
        }
        final TLRPC.TL_payments_paymentResult tL_payments_paymentResult = (TLRPC.TL_payments_paymentResult) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(tL_payments_paymentResult.updates.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(tL_payments_paymentResult.updates.chats, false);
        StarsController.getInstance(this.currentAccount).invalidateTransactions(false);
        StarsController.getInstance(this.currentAccount).invalidateProfileGifts(UserConfig.getInstance(this.currentAccount).getClientUserId());
        StarsController.getInstance(this.currentAccount).invalidateBalance();
        if (!applyNewGiftFromUpdates(tL_payments_paymentResult.updates)) {
            dismiss();
            return;
        }
        this.button.setLoading(false);
        this.fireworksOverlay.start(true);
        switchPage(false, true);
        if (getGift() != null) {
            str = getGift().title + " #" + LocaleController.formatNumber(getGift().num, ',');
        } else {
            str = "";
        }
        BulletinFactory.of(this.topBulletinContainer, this.resourcesProvider).createSimpleBulletin(R.raw.gift_upgrade, LocaleController.getString(R.string.Gift2UpgradedTitle), AndroidUtilities.replaceTags(LocaleController.formatString(R.string.Gift2UpgradedText, str))).setDuration(5000).show();
        Utilities.stageQueue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doUpgrade$43(tL_payments_paymentResult);
            }
        });
    }

    public void lambda$doUpgrade$47(final long j, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$doUpgrade$46(tLObject, tL_error, j);
            }
        });
    }

    public void lambda$new$0(View view) {
        if (this.button.isLoading()) {
            return;
        }
        this.checkbox.setChecked(!r3.isChecked(), true);
    }

    public void lambda$openAsLearnMore$24(View view) {
        dismiss();
    }

    public void lambda$openAsLearnMore$25(String str, TL_stars.starGiftUpgradePreview stargiftupgradepreview) {
        if (stargiftupgradepreview == null) {
            return;
        }
        this.topView.setPreviewingAttributes(stargiftupgradepreview.sample_attributes);
        switchPage(true, false);
        this.topView.setText(1, LocaleController.getString(R.string.Gift2LearnMoreTitle), 0L, LocaleController.formatString(R.string.Gift2LearnMoreText, str));
        this.featureCells[0].setText(LocaleController.getString(R.string.Gift2UpgradeFeature1TextLearn));
        this.featureCells[1].setText(LocaleController.getString(R.string.Gift2UpgradeFeature2TextLearn));
        this.featureCells[2].setText(LocaleController.getString(R.string.Gift2UpgradeFeature3TextLearn));
        this.checkboxLayout.setVisibility(8);
        this.checkboxSeparator.setVisibility(8);
        this.button.setText(LocaleController.getString(R.string.OK), false);
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                StarGiftSheet.this.lambda$openAsLearnMore$24(view);
            }
        });
        show();
    }

    public void lambda$openTransfer$48(UserSelectorBottomSheet[] userSelectorBottomSheetArr) {
        userSelectorBottomSheetArr[0].dismiss();
        dismiss();
    }

    public void lambda$openTransfer$49(Long l, final UserSelectorBottomSheet[] userSelectorBottomSheetArr, DialogInterface dialogInterface, int i) {
        doTransfer(l.longValue(), new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$openTransfer$48(userSelectorBottomSheetArr);
            }
        });
    }

    public void lambda$openTransfer$50(int i, int i2, int i3, TL_stars.TL_starGiftUnique tL_starGiftUnique, long j, String str, final UserSelectorBottomSheet[] userSelectorBottomSheetArr, final Long l) {
        AlertDialog.Builder title;
        String string;
        if (l.longValue() == -99) {
            Context context = getContext();
            Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
            if (i < i2) {
                title = new AlertDialog.Builder(context, resourcesProvider).setTitle(LocaleController.getString(R.string.Gift2ExportTONUnlocksAlertTitle));
                string = LocaleController.formatPluralString("Gift2ExportTONUnlocksAlertText", Math.max(1, i3), new Object[0]);
            } else {
                title = new AlertDialog.Builder(context, resourcesProvider).setTitle(LocaleController.getString(R.string.Gift2ExportTONUpdateRequiredTitle));
                string = LocaleController.getString(R.string.Gift2ExportTONUpdateRequiredText);
            }
            title.setMessage(string).setPositiveButton(LocaleController.getString(R.string.OK), null).show();
            return;
        }
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(l);
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        linearLayout.addView(new GiftTransferTopView(getContext(), tL_starGiftUnique, user), LayoutHelper.createLinear(-1, -2, 48, 0, -4, 0, 0));
        TextView textView = new TextView(getContext());
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, this.resourcesProvider));
        textView.setTextSize(1, 16.0f);
        textView.setText(AndroidUtilities.replaceTags(j > 0 ? LocaleController.formatPluralString("Gift2TransferPriceText", (int) j, str, UserObject.getForcedFirstName(MessagesController.getInstance(this.currentAccount).getUser(l))) : LocaleController.formatString(R.string.Gift2TransferText, str, UserObject.getForcedFirstName(user))));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 48, 24, 4, 24, 0));
        new AlertDialog.Builder(getContext(), this.resourcesProvider).setView(linearLayout).setPositiveButton(j > 0 ? StarsIntroActivity.replaceStars(LocaleController.formatString(R.string.Gift2TransferDoPrice, Integer.valueOf((int) j))) : LocaleController.getString(R.string.Gift2TransferDo), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i4) {
                StarGiftSheet.this.lambda$openTransfer$49(l, userSelectorBottomSheetArr, dialogInterface, i4);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).show();
    }

    public void lambda$openUpgrade$36(TL_stars.starGiftUpgradePreview stargiftupgradepreview) {
        if (stargiftupgradepreview == null) {
            return;
        }
        this.sample_attributes = stargiftupgradepreview.sample_attributes;
        openUpgradeAfter();
    }

    public void lambda$openUpgrade$37(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (!(tLObject instanceof TLRPC.PaymentForm)) {
            BulletinFactory.of(this.topBulletinContainer, this.resourcesProvider).makeForError(tL_error).show();
            return;
        }
        TLRPC.PaymentForm paymentForm = (TLRPC.PaymentForm) tLObject;
        MessagesController.getInstance(this.currentAccount).putUsers(paymentForm.users, false);
        this.upgrade_form = paymentForm;
        openUpgradeAfter();
    }

    public void lambda$openUpgrade$38(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$openUpgrade$37(tLObject, tL_error);
            }
        });
    }

    public void lambda$openUpgradeAfter$39(View view) {
        doUpgrade();
    }

    public void lambda$set$11(long j) {
        new GiftSheet(getContext(), this.currentAccount, j, new StarGiftSheet$$ExternalSyntheticLambda30(this)).show();
    }

    public void lambda$set$12(View view) {
        openUpgrade();
    }

    public void lambda$set$13(View view) {
        onBackPressed();
    }

    public void lambda$set$14() {
        new ExplainStarsSheet(getContext()).show();
    }

    public void lambda$set$16(long j) {
        new GiftSheet(getContext(), this.currentAccount, j, new StarGiftSheet$$ExternalSyntheticLambda30(this)).show();
    }

    public void lambda$set$18(long j) {
        new GiftSheet(getContext(), this.currentAccount, j, new StarGiftSheet$$ExternalSyntheticLambda30(this)).show();
    }

    public void lambda$set$19(MessageObject messageObject) {
        this.messageObjectRepolled = true;
        set(messageObject);
    }

    public void lambda$set$2(TL_stars.TL_starGiftUnique tL_starGiftUnique) {
        lambda$set$17(tL_starGiftUnique.owner_id);
    }

    public void lambda$set$20(TLObject tLObject, TLRPC.TL_error tL_error) {
        final MessageObject messageObject;
        if (tLObject instanceof TLRPC.messages_Messages) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            for (int i = 0; i < messages_messages.messages.size(); i++) {
                TLRPC.Message message = messages_messages.messages.get(i);
                if (message != null && (message.action instanceof TLRPC.TL_messageActionStarGift)) {
                    messageObject = new MessageObject(this.currentAccount, message, false, false);
                    break;
                }
            }
        }
        messageObject = null;
        if (messageObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.this.lambda$set$19(messageObject);
                }
            });
        }
    }

    public void lambda$set$21(View view) {
        openUpgrade();
    }

    public void lambda$set$22(View view) {
        onBackPressed();
    }

    public void lambda$set$23(long j) {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        dismiss();
        if ((safeLastFragment instanceof ProfileActivity) && ((ProfileActivity) safeLastFragment).myProfile) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", j);
        bundle.putBoolean("my_profile", true);
        bundle.putBoolean("open_gifts", true);
        safeLastFragment.presentFragment(new ProfileActivity(bundle));
    }

    public void lambda$set$3(TL_stars.TL_starGiftUnique tL_starGiftUnique) {
        lambda$set$17(tL_starGiftUnique.owner_id);
    }

    public void lambda$set$4(TL_stars.StarGiftAttribute starGiftAttribute, ButtonSpan.TextViewButtons[] textViewButtonsArr) {
        showHint(starGiftAttribute.rarity_permille, textViewButtonsArr[0]);
    }

    public void lambda$set$5(MessageObject messageObject) {
        this.messageObjectRepolled = true;
        set(messageObject);
    }

    public void lambda$set$6(int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        final MessageObject messageObject;
        if (tLObject instanceof TLRPC.messages_Messages) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(messages_messages.users, false);
            for (int i2 = 0; i2 < messages_messages.messages.size(); i2++) {
                TLRPC.Message message = messages_messages.messages.get(i2);
                if (message != null && message.id == i && (message.action instanceof TLRPC.TL_messageActionStarGiftUnique)) {
                    messageObject = new MessageObject(this.currentAccount, message, false, false);
                    break;
                }
            }
        }
        messageObject = null;
        if (messageObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.this.lambda$set$5(messageObject);
                }
            });
        }
    }

    public void lambda$set$7(View view) {
        toggleShow();
    }

    public void lambda$set$8(View view) {
        onBackPressed();
    }

    public void lambda$set$9() {
        new ExplainStarsSheet(getContext()).show();
    }

    public void lambda$show$33(AlertDialog alertDialog, MessageObject messageObject) {
        alertDialog.dismiss();
        this.messageObjectRepolled = true;
        set(messageObject);
        super.show();
    }

    public static void lambda$show$34(AlertDialog alertDialog) {
        alertDialog.dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.MessageNotFound)).show();
        }
    }

    public void lambda$show$35(TLRPC.TL_messageActionStarGift tL_messageActionStarGift, final AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_error tL_error) {
        final MessageObject messageObject;
        if (tLObject instanceof TLRPC.messages_Messages) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(messages_messages.users, false);
            for (int i = 0; i < messages_messages.messages.size(); i++) {
                TLRPC.Message message = messages_messages.messages.get(i);
                if (message != null && !(message instanceof TLRPC.TL_messageEmpty) && message.id == tL_messageActionStarGift.upgrade_msg_id) {
                    messageObject = new MessageObject(this.currentAccount, message, false, false);
                    break;
                }
            }
        }
        messageObject = null;
        if (messageObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.this.lambda$show$33(alertDialog, messageObject);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.lambda$show$34(AlertDialog.this);
                }
            });
        }
    }

    public void lambda$switchPage$1(ValueAnimator valueAnimator) {
        onSwitchedPage(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void lambda$toggleShow$30(BaseFragment baseFragment) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
        bundle.putBoolean("my_profile", true);
        bundle.putBoolean("open_gifts", true);
        baseFragment.presentFragment(new ProfileActivity(bundle));
    }

    public void lambda$toggleShow$31(TLObject tLObject, long j, TLRPC.Document document, boolean z, TLRPC.TL_error tL_error) {
        final BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            dismiss();
            StarsController.getInstance(this.currentAccount).invalidateProfileGifts(j);
            BulletinFactory.of(safeLastFragment).createEmojiBulletin(document, LocaleController.getString(z ? R.string.Gift2MadePrivateTitle : R.string.Gift2MadePublicTitle), AndroidUtilities.replaceSingleTag(LocaleController.getString(z ? R.string.Gift2MadePrivate : R.string.Gift2MadePublic), safeLastFragment instanceof ProfileActivity ? null : new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.this.lambda$toggleShow$30(safeLastFragment);
                }
            })).show(true);
        } else if (tL_error != null) {
            BulletinFactory.of(this.topBulletinContainer, this.resourcesProvider).createErrorBulletin(LocaleController.formatString(R.string.UnknownErrorCode, tL_error.text)).show(false);
        }
    }

    public void lambda$toggleShow$32(final long j, final TLRPC.Document document, final boolean z, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                StarGiftSheet.this.lambda$toggleShow$31(tLObject, j, document, z, tL_error);
            }
        });
    }

    public void onSwitchedPage(float f) {
        this.currentPage = f;
        this.infoLayout.setAlpha(1.0f - f);
        this.upgradeLayout.setAlpha(f);
        this.topView.onSwitchPage(f);
        this.container.updateTopViewTranslation();
        this.container.invalidate();
    }

    public void lambda$set$17(long j) {
        HintView2 hintView2 = this.currentHintView;
        if (hintView2 != null) {
            hintView2.hide();
            this.currentHintView = null;
        }
        dismiss();
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null || UserObject.isService(j)) {
            return;
        }
        Bundle bundle = new Bundle();
        if (j > 0) {
            bundle.putLong("user_id", j);
            if (j == UserConfig.getInstance(this.currentAccount).getClientUserId()) {
                bundle.putBoolean("my_profile", true);
                bundle.putBoolean("open_gifts", true);
            }
        } else {
            bundle.putLong("chat_id", -j);
        }
        safeLastFragment.presentFragment(new ProfileActivity(bundle));
    }

    public void openUpgrade() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarGiftSheet.openUpgrade():void");
    }

    private void openUpgradeAfter() {
        long j;
        MessageObject messageObject = this.messageObject;
        if (messageObject != null) {
            TLRPC.MessageAction messageAction = messageObject.messageOwner.action;
            if (!(messageAction instanceof TLRPC.TL_messageActionStarGift)) {
                return;
            } else {
                j = ((TLRPC.TL_messageActionStarGift) messageAction).upgrade_stars;
            }
        } else {
            TL_stars.UserStarGift userStarGift = this.userStarGift;
            if (userStarGift == null) {
                return;
            } else {
                j = userStarGift.upgrade_stars;
            }
        }
        if (this.sample_attributes != null) {
            if (j > 0 || this.upgrade_form != null) {
                long j2 = 0;
                if (this.upgrade_form != null) {
                    for (int i = 0; i < this.upgrade_form.invoice.prices.size(); i++) {
                        j2 += this.upgrade_form.invoice.prices.get(i).amount;
                    }
                }
                this.topView.setPreviewingAttributes(this.sample_attributes);
                this.topView.setText(1, LocaleController.getString(R.string.Gift2UpgradeTitle), 0L, LocaleController.getString(R.string.Gift2UpgradeText));
                if (j2 > 0) {
                    this.button.setText(StarsIntroActivity.replaceStars(LocaleController.formatString(R.string.Gift2UpgradeButton, Long.valueOf(j2))), true);
                } else {
                    this.button.setText(LocaleController.getString(R.string.Confirm), true);
                }
                this.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        StarGiftSheet.this.lambda$openUpgradeAfter$39(view);
                    }
                });
                switchPage(true, true);
            }
        }
    }

    public void toggleShow() {
        final boolean z;
        final TLRPC.Document document;
        int i;
        boolean z2;
        TL_stars.StarGift starGift;
        if (this.button.isLoading()) {
            return;
        }
        final long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        MessageObject messageObject = this.messageObject;
        if (messageObject == null || messageObject.messageOwner == null) {
            TL_stars.UserStarGift userStarGift = this.userStarGift;
            if (userStarGift == null) {
                return;
            }
            int i2 = userStarGift.msg_id;
            z = !userStarGift.unsaved;
            document = userStarGift.gift.getDocument();
            i = i2;
        } else {
            i = messageObject.getId();
            TLRPC.MessageAction messageAction = this.messageObject.messageOwner.action;
            if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
                TLRPC.TL_messageActionStarGift tL_messageActionStarGift = (TLRPC.TL_messageActionStarGift) messageAction;
                z2 = tL_messageActionStarGift.saved;
                starGift = tL_messageActionStarGift.gift;
            } else {
                if (!(messageAction instanceof TLRPC.TL_messageActionStarGiftUnique)) {
                    return;
                }
                TLRPC.TL_messageActionStarGiftUnique tL_messageActionStarGiftUnique = (TLRPC.TL_messageActionStarGiftUnique) messageAction;
                z2 = tL_messageActionStarGiftUnique.saved;
                starGift = tL_messageActionStarGiftUnique.gift;
            }
            z = z2;
            document = starGift.getDocument();
        }
        this.button.setLoading(true);
        TL_stars.saveStarGift savestargift = new TL_stars.saveStarGift();
        savestargift.unsave = z;
        savestargift.msg_id = i;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(savestargift, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                StarGiftSheet.this.lambda$toggleShow$32(clientUserId, document, z, tLObject, tL_error);
            }
        });
    }

    public TL_stars.StarGift getGift() {
        MessageObject messageObject = this.messageObject;
        if (messageObject != null) {
            TLRPC.Message message = messageObject.messageOwner;
            if (message == null) {
                return null;
            }
            TLRPC.MessageAction messageAction = message.action;
            if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
                return ((TLRPC.TL_messageActionStarGift) messageAction).gift;
            }
            if (messageAction instanceof TLRPC.TL_messageActionStarGiftUnique) {
                return ((TLRPC.TL_messageActionStarGiftUnique) messageAction).gift;
            }
        } else {
            TL_stars.UserStarGift userStarGift = this.userStarGift;
            if (userStarGift != null) {
                return userStarGift.gift;
            }
        }
        return null;
    }

    public boolean isSaved() {
        TLRPC.Message message;
        MessageObject messageObject = this.messageObject;
        if (messageObject == null || (message = messageObject.messageOwner) == null) {
            if (this.userStarGift != null) {
                return !r0.unsaved;
            }
            return false;
        }
        TLRPC.MessageAction messageAction = message.action;
        if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
            return ((TLRPC.TL_messageActionStarGift) messageAction).saved;
        }
        if (messageAction instanceof TLRPC.TL_messageActionStarGiftUnique) {
            return ((TLRPC.TL_messageActionStarGiftUnique) messageAction).saved;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (this.currentPage <= 0.0f || this.button.isLoading() || this.isLearnMore) {
            super.onBackPressed();
            return;
        }
        MessageObject messageObject = this.messageObject;
        if (messageObject != null) {
            set(messageObject);
        } else {
            TL_stars.UserStarGift userStarGift = this.userStarGift;
            if (userStarGift != null) {
                set(this.myProfile, userStarGift);
            }
        }
        switchPage(false, true);
    }

    @Override
    protected void onSwipeStarts() {
        HintView2 hintView2 = this.currentHintView;
        if (hintView2 != null) {
            hintView2.hide();
            this.currentHintView = null;
        }
    }

    public void openAsLearnMore(long j, final String str) {
        this.isLearnMore = true;
        StarsController.getInstance(this.currentAccount).getStarGiftPreview(j, new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                StarGiftSheet.this.lambda$openAsLearnMore$25(str, (TL_stars.starGiftUpgradePreview) obj);
            }
        });
    }

    public void openTransfer() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarGiftSheet.openTransfer():void");
    }

    public org.telegram.ui.Stars.StarGiftSheet set(org.telegram.messenger.MessageObject r45) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarGiftSheet.set(org.telegram.messenger.MessageObject):org.telegram.ui.Stars.StarGiftSheet");
    }

    public org.telegram.ui.Stars.StarGiftSheet set(boolean r39, org.telegram.tgnet.tl.TL_stars.UserStarGift r40) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarGiftSheet.set(boolean, org.telegram.tgnet.tl.TL_stars$UserStarGift):org.telegram.ui.Stars.StarGiftSheet");
    }

    public void set(final TL_stars.TL_starGiftUnique tL_starGiftUnique, boolean z, boolean z2) {
        ButtonWithCounterView buttonWithCounterView;
        View.OnClickListener onClickListener;
        SpannableString spannableString;
        int i;
        boolean z3;
        TableView tableView;
        CharSequence formatSpannable;
        TableView tableView2;
        CharSequence formatSpannable2;
        TableView.TableRowFullContent addFullRow;
        TableView tableView3;
        String string;
        StringBuilder sb;
        int i2;
        this.topView.setGift(tL_starGiftUnique);
        this.topView.setText(0, tL_starGiftUnique.title, 0L, LocaleController.formatPluralStringComma("Gift2CollectionNumber", tL_starGiftUnique.num));
        this.tableView.clear();
        long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (!z2) {
            if (tL_starGiftUnique.owner_id == clientUserId && z) {
                this.tableView.addRowUser(LocaleController.getString(R.string.Gift2Owner), this.currentAccount, tL_starGiftUnique.owner_id, new Runnable() {
                    @Override
                    public final void run() {
                        StarGiftSheet.this.lambda$set$2(tL_starGiftUnique);
                    }
                }, LocaleController.getString(R.string.Gift2OwnerChange), new Runnable() {
                    @Override
                    public final void run() {
                        StarGiftSheet.this.openTransfer();
                    }
                });
            } else {
                this.tableView.addRowUser(LocaleController.getString(R.string.Gift2Owner), this.currentAccount, tL_starGiftUnique.owner_id, new Runnable() {
                    @Override
                    public final void run() {
                        StarGiftSheet.this.lambda$set$3(tL_starGiftUnique);
                    }
                });
            }
        }
        Iterator<TL_stars.StarGiftAttribute> it = tL_starGiftUnique.attributes.iterator();
        while (it.hasNext()) {
            final TL_stars.StarGiftAttribute next = it.next();
            if (next instanceof TL_stars.starGiftAttributeModel) {
                i2 = R.string.Gift2AttributeModel;
            } else if (next instanceof TL_stars.starGiftAttributeBackdrop) {
                i2 = R.string.Gift2AttributeBackdrop;
            } else if (next instanceof TL_stars.starGiftAttributePattern) {
                i2 = R.string.Gift2AttributeSymbol;
            }
            final ButtonSpan.TextViewButtons[] textViewButtonsArr = {(ButtonSpan.TextViewButtons) ((TableView.TableRowContent) this.tableView.addRow(LocaleController.getString(i2), next.name, AffiliateProgramFragment.percents(next.rarity_permille), new Runnable() {
                @Override
                public final void run() {
                    StarGiftSheet.this.lambda$set$4(next, textViewButtonsArr);
                }
            }).getChildAt(1)).getChildAt(0)};
        }
        if (!z2) {
            if (this.messageObject == null) {
                tableView3 = this.tableView;
                string = LocaleController.getString(R.string.Gift2Quantity);
                sb = new StringBuilder();
            } else if (this.messageObjectRepolled) {
                tableView3 = this.tableView;
                string = LocaleController.getString(R.string.Gift2Quantity);
                sb = new StringBuilder();
            } else {
                TextView textView = (TextView) ((TableView.TableRowContent) this.tableView.addRow(LocaleController.getString(R.string.Gift2Quantity), "").getChildAt(1)).getChildAt(0);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("x ");
                LoadingSpan loadingSpan = new LoadingSpan(textView, AndroidUtilities.dp(90.0f), 0, this.resourcesProvider);
                int i3 = Theme.key_windowBackgroundWhiteBlackText;
                loadingSpan.setColors(Theme.multAlpha(Theme.getColor(i3, this.resourcesProvider), 0.21f), Theme.multAlpha(Theme.getColor(i3, this.resourcesProvider), 0.08f));
                spannableStringBuilder.setSpan(loadingSpan, 0, 1, 33);
                textView.setText(spannableStringBuilder, TextView.BufferType.SPANNABLE);
                if (!this.messageObjectRepolling) {
                    final int id = this.messageObject.getId();
                    this.messageObjectRepolling = true;
                    TLRPC.TL_messages_getMessages tL_messages_getMessages = new TLRPC.TL_messages_getMessages();
                    tL_messages_getMessages.id.add(Integer.valueOf(id));
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getMessages, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            StarGiftSheet.this.lambda$set$6(id, tLObject, tL_error);
                        }
                    });
                }
            }
            sb.append(LocaleController.formatPluralStringComma("Gift2QuantityIssued1", tL_starGiftUnique.availability_issued));
            sb.append(LocaleController.formatPluralStringComma("Gift2QuantityIssued2", tL_starGiftUnique.availability_total));
            tableView3.addRow(string, sb.toString());
        }
        TL_stars.starGiftAttributeOriginalDetails stargiftattributeoriginaldetails = (TL_stars.starGiftAttributeOriginalDetails) StarsController.findAttribute(tL_starGiftUnique.attributes, TL_stars.starGiftAttributeOriginalDetails.class);
        if (stargiftattributeoriginaldetails != null) {
            Spannable spannable = null;
            if ((stargiftattributeoriginaldetails.flags & 1) != 0) {
                spannableString = new SpannableString(UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(stargiftattributeoriginaldetails.sender_id))));
                spannableString.setSpan(new ClickableSpan() {
                    final TL_stars.starGiftAttributeOriginalDetails val$details;

                    AnonymousClass2(TL_stars.starGiftAttributeOriginalDetails stargiftattributeoriginaldetails2) {
                        r2 = stargiftattributeoriginaldetails2;
                    }

                    @Override
                    public void onClick(View view) {
                        StarGiftSheet.this.lambda$set$17(r2.sender_id);
                    }

                    @Override
                    public void updateDrawState(TextPaint textPaint) {
                        textPaint.setColor(textPaint.linkColor);
                    }
                }, 0, spannableString.length(), 33);
            } else {
                spannableString = null;
            }
            SpannableString spannableString2 = new SpannableString(UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(stargiftattributeoriginaldetails2.recipient_id))));
            spannableString2.setSpan(new ClickableSpan() {
                final TL_stars.starGiftAttributeOriginalDetails val$details;

                AnonymousClass3(TL_stars.starGiftAttributeOriginalDetails stargiftattributeoriginaldetails2) {
                    r2 = stargiftattributeoriginaldetails2;
                }

                @Override
                public void onClick(View view) {
                    StarGiftSheet.this.lambda$set$17(r2.recipient_id);
                }

                @Override
                public void updateDrawState(TextPaint textPaint) {
                    textPaint.setColor(textPaint.linkColor);
                }
            }, 0, spannableString2.length(), 33);
            if (stargiftattributeoriginaldetails2.message != null) {
                TextPaint textPaint = new TextPaint(1);
                textPaint.setTextSize(AndroidUtilities.dp(14.0f));
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(stargiftattributeoriginaldetails2.message.text);
                MessageObject.addEntitiesToText(spannableStringBuilder2, stargiftattributeoriginaldetails2.message.entities, false, false, false, false);
                spannable = MessageObject.replaceAnimatedEmoji(Emoji.replaceEmoji(spannableStringBuilder2, textPaint.getFontMetricsInt(), false), stargiftattributeoriginaldetails2.message.entities, textPaint.getFontMetricsInt());
            }
            String replaceAll = LocaleController.getInstance().getFormatterYear().format(stargiftattributeoriginaldetails2.date * 1000).replaceAll("\\.", "/");
            if (stargiftattributeoriginaldetails2.sender_id != stargiftattributeoriginaldetails2.recipient_id) {
                i = 0;
                z3 = true;
                if (spannableString != null) {
                    tableView = this.tableView;
                    formatSpannable = spannable == null ? LocaleController.formatSpannable(R.string.Gift2AttributeOriginalDetails, spannableString, spannableString2, replaceAll) : LocaleController.formatSpannable(R.string.Gift2AttributeOriginalDetailsComment, spannableString, spannableString2, replaceAll, spannable);
                } else if (spannable == null) {
                    tableView2 = this.tableView;
                    formatSpannable2 = LocaleController.formatSpannable(R.string.Gift2AttributeOriginalDetailsNoSender, spannableString2, replaceAll);
                    addFullRow = tableView2.addFullRow(formatSpannable2);
                } else {
                    tableView = this.tableView;
                    formatSpannable = LocaleController.formatSpannable(R.string.Gift2AttributeOriginalDetailsNoSenderComment, spannableString2, replaceAll, spannable);
                }
                addFullRow = tableView.addFullRow(formatSpannable);
            } else if (spannable == null) {
                tableView2 = this.tableView;
                i = 0;
                z3 = true;
                formatSpannable2 = LocaleController.formatSpannable(R.string.Gift2AttributeOriginalDetailsSelf, spannableString, replaceAll);
                addFullRow = tableView2.addFullRow(formatSpannable2);
            } else {
                i = 0;
                z3 = true;
                tableView = this.tableView;
                formatSpannable = LocaleController.formatSpannable(R.string.Gift2AttributeOriginalDetailsSelfComment, spannableString, replaceAll, spannable);
                addFullRow = tableView.addFullRow(formatSpannable);
            }
            addFullRow.setFilled(z3);
            ((SpoilersTextView) addFullRow.getChildAt(i)).setGravity(17);
        }
        if (z2 || tL_starGiftUnique.owner_id != clientUserId) {
            this.button.setText(LocaleController.getString(R.string.OK), true);
            buttonWithCounterView = this.button;
            onClickListener = new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    StarGiftSheet.this.lambda$set$8(view);
                }
            };
        } else {
            this.button.setText(LocaleController.getString(isSaved() ? R.string.Gift2ProfileMakeInvisible : R.string.Gift2ProfileMakeVisible), true);
            buttonWithCounterView = this.button;
            onClickListener = new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    StarGiftSheet.this.lambda$set$7(view);
                }
            };
        }
        buttonWithCounterView.setOnClickListener(onClickListener);
    }

    @Override
    public void show() {
        MessageObject messageObject;
        TLRPC.Message message;
        if (this.userStarGift == null && (messageObject = this.messageObject) != null && (message = messageObject.messageOwner) != null) {
            TLRPC.MessageAction messageAction = message.action;
            if (messageAction instanceof TLRPC.TL_messageActionStarGift) {
                final TLRPC.TL_messageActionStarGift tL_messageActionStarGift = (TLRPC.TL_messageActionStarGift) messageAction;
                if (tL_messageActionStarGift.upgraded) {
                    final AlertDialog alertDialog = new AlertDialog(getContext(), 3);
                    alertDialog.showDelayed(500L);
                    TLRPC.TL_messages_getMessages tL_messages_getMessages = new TLRPC.TL_messages_getMessages();
                    tL_messages_getMessages.id.add(Integer.valueOf(tL_messageActionStarGift.upgrade_msg_id));
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getMessages, new RequestDelegate() {
                        @Override
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            StarGiftSheet.this.lambda$show$35(tL_messageActionStarGift, alertDialog, tLObject, tL_error);
                        }
                    });
                    return;
                }
            }
        }
        super.show();
    }

    public void showHint(int i, TextView textView) {
        Layout layout;
        HintView2 hintView2 = this.currentHintView;
        if ((hintView2 != null && hintView2.shown() && this.currentHintViewTextView == textView) || textView == null || (layout = textView.getLayout()) == null) {
            return;
        }
        CharSequence text = layout.getText();
        if (text instanceof Spanned) {
            Spanned spanned = (Spanned) text;
            ButtonSpan[] buttonSpanArr = (ButtonSpan[]) spanned.getSpans(0, spanned.length(), ButtonSpan.class);
            if (buttonSpanArr == null || buttonSpanArr.length <= 0) {
                return;
            }
            float paddingLeft = textView.getPaddingLeft() + layout.getPrimaryHorizontal(spanned.getSpanStart(buttonSpanArr[buttonSpanArr.length - 1])) + (r4.getSize() / 2.0f);
            int[] iArr = new int[2];
            textView.getLocationOnScreen(r4);
            this.container.getLocationOnScreen(iArr);
            int[] iArr2 = {iArr2[0] - iArr[0], iArr2[1] - iArr[1]};
            HintView2 hintView22 = this.currentHintView;
            if (hintView22 != null) {
                hintView22.hide();
                this.currentHintView = null;
            }
            final HintView2 hintView23 = new HintView2(getContext(), 3);
            hintView23.setText(LocaleController.formatString(R.string.Gift2RarityHint, AffiliateProgramFragment.percents(i)));
            hintView23.setJointPx(0.0f, (iArr2[0] + paddingLeft) - (AndroidUtilities.dp(16.0f) + this.backgroundPaddingLeft));
            hintView23.setTranslationY(((iArr2[1] - AndroidUtilities.dp(100.0f)) - (textView.getHeight() / 2.0f)) + AndroidUtilities.dp(4.33f));
            hintView23.setDuration(3000L);
            hintView23.setPadding(AndroidUtilities.dp(16.0f) + this.backgroundPaddingLeft, 0, AndroidUtilities.dp(16.0f) + this.backgroundPaddingLeft, 0);
            hintView23.setOnHiddenListener(new Runnable() {
                @Override
                public final void run() {
                    AndroidUtilities.removeFromParent(HintView2.this);
                }
            });
            hintView23.show();
            this.container.addView(hintView23, LayoutHelper.createFrame(-1, 100.0f));
            this.currentHintView = hintView23;
            this.currentHintViewTextView = textView;
        }
    }

    public void switchPage(boolean z, boolean z2) {
        ValueAnimator valueAnimator = this.switchingPagesAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.switchingPagesAnimator = null;
        }
        if (!z) {
            AndroidUtilities.cancelRunOnUIThread(this.topView.checkToRotateRunnable);
        }
        if (z2) {
            this.infoLayout.setVisibility(0);
            this.upgradeLayout.setVisibility(0);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.currentPage, z ? 1.0f : 0.0f);
            this.switchingPagesAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    StarGiftSheet.this.lambda$switchPage$1(valueAnimator2);
                }
            });
            this.switchingPagesAnimator.addListener(new AnimatorListenerAdapter() {
                final boolean val$toUpgrade;

                AnonymousClass1(boolean z3) {
                    r2 = z3;
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    StarGiftSheet.this.onSwitchedPage(r2 ? 1.0f : 0.0f);
                    StarGiftSheet.this.infoLayout.setVisibility(r2 ? 8 : 0);
                    StarGiftSheet.this.upgradeLayout.setVisibility(r2 ? 0 : 8);
                    StarGiftSheet.this.switchingPagesAnimator = null;
                }
            });
            this.switchingPagesAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.switchingPagesAnimator.setDuration(320L);
            this.switchingPagesAnimator.start();
            this.topView.prepareSwitchPage(Math.round(this.currentPage), z3 ? 1 : 0);
        } else {
            onSwitchedPage(z3 ? 1.0f : 0.0f);
            this.infoLayout.setVisibility(z3 ? 8 : 0);
            this.upgradeLayout.setVisibility(z3 ? 0 : 8);
        }
        HintView2 hintView2 = this.currentHintView;
        if (hintView2 != null) {
            hintView2.hide();
            this.currentHintView = null;
        }
    }
}
