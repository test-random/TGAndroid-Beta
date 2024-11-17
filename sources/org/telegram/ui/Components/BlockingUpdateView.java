package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.voip.CellFlickerDrawable;

public abstract class BlockingUpdateView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private FrameLayout acceptButton;
    private TextView acceptTextView;
    private int accountNum;
    private TLRPC.TL_help_appUpdate appUpdate;
    private String fileName;
    Drawable gradientDrawableBottom;
    Drawable gradientDrawableTop;
    private int pressCount;
    private AnimatorSet progressAnimation;
    private RadialProgress radialProgress;
    private FrameLayout radialProgressView;
    private ScrollView scrollView;
    private TextView textView;

    public BlockingUpdateView(final Context context) {
        super(context);
        GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TOP_BOTTOM;
        int i = Theme.key_windowBackgroundWhite;
        this.gradientDrawableTop = new GradientDrawable(orientation, new int[]{Theme.getColor(i), 0});
        this.gradientDrawableBottom = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, new int[]{Theme.getColor(i), 0});
        setBackgroundColor(Theme.getColor(i));
        int i2 = Build.VERSION.SDK_INT;
        int i3 = i2 >= 21 ? (int) (AndroidUtilities.statusBarHeight / AndroidUtilities.density) : 0;
        FrameLayout frameLayout = new FrameLayout(context);
        addView(frameLayout, new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(176.0f) + (i2 >= 21 ? AndroidUtilities.statusBarHeight : 0)));
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        rLottieImageView.setAnimation(R.raw.qr_code_logo, 108, 108);
        rLottieImageView.playAnimation();
        rLottieImageView.getAnimatedDrawable().setAutoRepeat(1);
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        rLottieImageView.setPadding(0, 0, 0, AndroidUtilities.dp(14.0f));
        frameLayout.addView(rLottieImageView, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, i3, 0.0f, 0.0f));
        rLottieImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                BlockingUpdateView.this.lambda$new$0(view);
            }
        });
        FrameLayout frameLayout2 = new FrameLayout(context);
        ScrollView scrollView = new ScrollView(context);
        this.scrollView = scrollView;
        AndroidUtilities.setScrollViewEdgeEffectColor(scrollView, Theme.getColor(Theme.key_actionBarDefault));
        this.scrollView.setPadding(0, AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f));
        this.scrollView.setClipToPadding(false);
        addView(this.scrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 27.0f, i3 + 178, 27.0f, 130.0f));
        this.scrollView.addView(frameLayout2);
        TextView textView = new TextView(context);
        int i4 = Theme.key_windowBackgroundWhiteBlackText;
        textView.setTextColor(Theme.getColor(i4));
        textView.setTextSize(1, 20.0f);
        textView.setGravity(49);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setText(LocaleController.getString(R.string.UpdateTelegram));
        frameLayout2.addView(textView, LayoutHelper.createFrame(-2, -2, 49));
        TextView textView2 = new TextView(context);
        this.textView = textView2;
        textView2.setTextColor(Theme.getColor(i4));
        this.textView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        this.textView.setTextSize(1, 15.0f);
        this.textView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        this.textView.setGravity(49);
        this.textView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        frameLayout2.addView(this.textView, LayoutHelper.createFrame(-2, -2.0f, 51, 0.0f, 44.0f, 0.0f, 0.0f));
        FrameLayout frameLayout3 = new FrameLayout(context) {
            CellFlickerDrawable cellFlickerDrawable;

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (this.cellFlickerDrawable == null) {
                    CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
                    this.cellFlickerDrawable = cellFlickerDrawable;
                    cellFlickerDrawable.drawFrame = false;
                    cellFlickerDrawable.repeatProgress = 2.0f;
                }
                this.cellFlickerDrawable.setParentWidth(getMeasuredWidth());
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.cellFlickerDrawable.draw(canvas, rectF, AndroidUtilities.dp(4.0f), null);
                invalidate();
            }

            @Override
            protected void onMeasure(int i5, int i6) {
                if (View.MeasureSpec.getSize(i5) > AndroidUtilities.dp(260.0f)) {
                    i5 = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(320.0f), 1073741824);
                }
                super.onMeasure(i5, i6);
            }
        };
        this.acceptButton = frameLayout3;
        frameLayout3.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        this.acceptButton.setBackgroundDrawable(Theme.AdaptiveRipple.filledRectByKey(Theme.key_featuredStickers_addButton, 4.0f));
        this.acceptButton.setPadding(AndroidUtilities.dp(34.0f), 0, AndroidUtilities.dp(34.0f), 0);
        addView(this.acceptButton, LayoutHelper.createFrame(-2, 46.0f, 81, 0.0f, 0.0f, 0.0f, 45.0f));
        this.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                BlockingUpdateView.this.lambda$new$1(context, view);
            }
        });
        TextView textView3 = new TextView(context);
        this.acceptTextView = textView3;
        textView3.setGravity(17);
        this.acceptTextView.setTypeface(AndroidUtilities.bold());
        this.acceptTextView.setTextColor(-1);
        this.acceptTextView.setTextSize(1, 14.0f);
        this.acceptButton.addView(this.acceptTextView, LayoutHelper.createFrame(-2, -2, 17));
        FrameLayout frameLayout4 = new FrameLayout(context) {
            @Override
            protected void onDraw(Canvas canvas) {
                BlockingUpdateView.this.radialProgress.draw(canvas);
            }

            @Override
            protected void onLayout(boolean z, int i5, int i6, int i7, int i8) {
                super.onLayout(z, i5, i6, i7, i8);
                int dp = AndroidUtilities.dp(36.0f);
                int i9 = ((i7 - i5) - dp) / 2;
                int i10 = ((i8 - i6) - dp) / 2;
                BlockingUpdateView.this.radialProgress.setProgressRect(i9, i10, i9 + dp, dp + i10);
            }
        };
        this.radialProgressView = frameLayout4;
        frameLayout4.setWillNotDraw(false);
        this.radialProgressView.setAlpha(0.0f);
        this.radialProgressView.setScaleX(0.1f);
        this.radialProgressView.setScaleY(0.1f);
        this.radialProgressView.setVisibility(4);
        RadialProgress radialProgress = new RadialProgress(this.radialProgressView);
        this.radialProgress = radialProgress;
        radialProgress.setBackground(null, true, false);
        this.radialProgress.setProgressColor(-1);
        this.acceptButton.addView(this.radialProgressView, LayoutHelper.createFrame(36, 36, 17));
    }

    public void lambda$new$0(View view) {
        int i = this.pressCount + 1;
        this.pressCount = i;
        if (i >= 10) {
            setVisibility(8);
            SharedConfig.pendingAppUpdate = null;
            SharedConfig.saveConfig();
        }
    }

    public void lambda$new$1(Context context, View view) {
        String str;
        if (!ApplicationLoader.isStandaloneBuild() && !BuildVars.DEBUG_VERSION) {
            str = BuildVars.isHuaweiStoreApp() ? BuildVars.HUAWEI_STORE_URL : BuildVars.PLAYSTORE_APP_URL;
        } else {
            if (!ApplicationLoader.applicationLoaderInstance.checkApkInstallPermissions(getContext())) {
                return;
            }
            TLRPC.TL_help_appUpdate tL_help_appUpdate = this.appUpdate;
            if (tL_help_appUpdate.document instanceof TLRPC.TL_document) {
                if (ApplicationLoader.applicationLoaderInstance.openApkInstall((Activity) getContext(), this.appUpdate.document)) {
                    return;
                }
                FileLoader.getInstance(this.accountNum).loadFile(this.appUpdate.document, "update", 3, 1);
                showProgress(true);
                return;
            }
            if (tL_help_appUpdate.url == null) {
                return;
            }
            context = getContext();
            str = this.appUpdate.url;
        }
        Browser.openUrl(context, str);
    }

    public void lambda$show$2(TLObject tLObject) {
        if (!(tLObject instanceof TLRPC.TL_help_appUpdate) || ((TLRPC.TL_help_appUpdate) tLObject).can_not_skip) {
            return;
        }
        setVisibility(8);
        SharedConfig.pendingAppUpdate = null;
        SharedConfig.saveConfig();
    }

    public void lambda$show$3(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                BlockingUpdateView.this.lambda$show$2(tLObject);
            }
        });
    }

    private void showProgress(final boolean z) {
        AnimatorSet animatorSet = this.progressAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.progressAnimation = new AnimatorSet();
        if (z) {
            this.radialProgressView.setVisibility(0);
            this.acceptButton.setEnabled(false);
            AnimatorSet animatorSet2 = this.progressAnimation;
            TextView textView = this.acceptTextView;
            Property property = View.SCALE_X;
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(textView, (Property<TextView, Float>) property, 0.1f);
            TextView textView2 = this.acceptTextView;
            Property property2 = View.SCALE_Y;
            ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(textView2, (Property<TextView, Float>) property2, 0.1f);
            TextView textView3 = this.acceptTextView;
            Property property3 = View.ALPHA;
            animatorSet2.playTogether(ofFloat, ofFloat2, ObjectAnimator.ofFloat(textView3, (Property<TextView, Float>) property3, 0.0f), ObjectAnimator.ofFloat(this.radialProgressView, (Property<FrameLayout, Float>) property, 1.0f), ObjectAnimator.ofFloat(this.radialProgressView, (Property<FrameLayout, Float>) property2, 1.0f), ObjectAnimator.ofFloat(this.radialProgressView, (Property<FrameLayout, Float>) property3, 1.0f));
        } else {
            this.acceptTextView.setVisibility(0);
            this.acceptButton.setEnabled(true);
            AnimatorSet animatorSet3 = this.progressAnimation;
            FrameLayout frameLayout = this.radialProgressView;
            Property property4 = View.SCALE_X;
            ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(frameLayout, (Property<FrameLayout, Float>) property4, 0.1f);
            FrameLayout frameLayout2 = this.radialProgressView;
            Property property5 = View.SCALE_Y;
            ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(frameLayout2, (Property<FrameLayout, Float>) property5, 0.1f);
            FrameLayout frameLayout3 = this.radialProgressView;
            Property property6 = View.ALPHA;
            animatorSet3.playTogether(ofFloat3, ofFloat4, ObjectAnimator.ofFloat(frameLayout3, (Property<FrameLayout, Float>) property6, 0.0f), ObjectAnimator.ofFloat(this.acceptTextView, (Property<TextView, Float>) property4, 1.0f), ObjectAnimator.ofFloat(this.acceptTextView, (Property<TextView, Float>) property5, 1.0f), ObjectAnimator.ofFloat(this.acceptTextView, (Property<TextView, Float>) property6, 1.0f));
        }
        this.progressAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animator) {
                if (BlockingUpdateView.this.progressAnimation == null || !BlockingUpdateView.this.progressAnimation.equals(animator)) {
                    return;
                }
                BlockingUpdateView.this.progressAnimation = null;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (BlockingUpdateView.this.progressAnimation == null || !BlockingUpdateView.this.progressAnimation.equals(animator)) {
                    return;
                }
                (!z ? BlockingUpdateView.this.radialProgressView : BlockingUpdateView.this.acceptTextView).setVisibility(4);
            }
        });
        this.progressAnimation.setDuration(150L);
        this.progressAnimation.start();
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.fileLoaded) {
            String str = (String) objArr[0];
            String str2 = this.fileName;
            if (str2 == null || !str2.equals(str)) {
                return;
            }
            showProgress(false);
            ApplicationLoader.applicationLoaderInstance.openApkInstall((Activity) getContext(), this.appUpdate.document);
            return;
        }
        if (i == NotificationCenter.fileLoadFailed) {
            String str3 = (String) objArr[0];
            String str4 = this.fileName;
            if (str4 == null || !str4.equals(str3)) {
                return;
            }
            showProgress(false);
            return;
        }
        if (i == NotificationCenter.fileLoadProgressChanged) {
            String str5 = (String) objArr[0];
            String str6 = this.fileName;
            if (str6 == null || !str6.equals(str5)) {
                return;
            }
            this.radialProgress.setProgress(Math.min(1.0f, ((float) ((Long) objArr[1]).longValue()) / ((float) ((Long) objArr[2]).longValue())), true);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        this.gradientDrawableTop.setBounds(this.scrollView.getLeft(), this.scrollView.getTop(), this.scrollView.getRight(), this.scrollView.getTop() + AndroidUtilities.dp(16.0f));
        this.gradientDrawableTop.draw(canvas);
        this.gradientDrawableBottom.setBounds(this.scrollView.getLeft(), this.scrollView.getBottom() - AndroidUtilities.dp(18.0f), this.scrollView.getRight(), this.scrollView.getBottom());
        this.gradientDrawableBottom.draw(canvas);
    }

    @Override
    public void setVisibility(int i) {
        super.setVisibility(i);
        if (i == 8) {
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileLoaded);
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileLoadFailed);
            NotificationCenter.getInstance(this.accountNum).removeObserver(this, NotificationCenter.fileLoadProgressChanged);
        }
    }

    public void show(int i, TLRPC.TL_help_appUpdate tL_help_appUpdate, boolean z) {
        TextView textView;
        String string;
        this.pressCount = 0;
        this.appUpdate = tL_help_appUpdate;
        this.accountNum = i;
        TLRPC.Document document = tL_help_appUpdate.document;
        if (document instanceof TLRPC.TL_document) {
            this.fileName = FileLoader.getAttachFileName(document);
        }
        if (getVisibility() != 0) {
            setVisibility(0);
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(tL_help_appUpdate.text);
        MessageObject.addEntitiesToText(spannableStringBuilder, tL_help_appUpdate.entities, false, false, false, false);
        this.textView.setText(spannableStringBuilder);
        if (tL_help_appUpdate.document instanceof TLRPC.TL_document) {
            textView = this.acceptTextView;
            string = LocaleController.getString(R.string.Update) + String.format(Locale.US, " (%1$s)", AndroidUtilities.formatFileSize(tL_help_appUpdate.document.size));
        } else {
            textView = this.acceptTextView;
            string = LocaleController.getString(R.string.Update);
        }
        textView.setText(string);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileLoaded);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileLoadFailed);
        NotificationCenter.getInstance(this.accountNum).addObserver(this, NotificationCenter.fileLoadProgressChanged);
        if (z && ApplicationLoader.isStandaloneBuild()) {
            TLRPC.TL_help_getAppUpdate tL_help_getAppUpdate = new TLRPC.TL_help_getAppUpdate();
            try {
                tL_help_getAppUpdate.source = ApplicationLoader.applicationContext.getPackageManager().getInstallerPackageName(ApplicationLoader.applicationContext.getPackageName());
            } catch (Exception unused) {
            }
            if (tL_help_getAppUpdate.source == null) {
                tL_help_getAppUpdate.source = "";
            }
            ConnectionsManager.getInstance(this.accountNum).sendRequest(tL_help_getAppUpdate, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    BlockingUpdateView.this.lambda$show$3(tLObject, tL_error);
                }
            });
        }
    }
}
