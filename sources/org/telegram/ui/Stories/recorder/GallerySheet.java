package org.telegram.ui.Stories.recorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Stories.DarkThemeResourceProvider;

public class GallerySheet extends BottomSheet {
    private Boolean galleryListViewOpening;
    private ValueAnimator galleryOpenCloseAnimator;
    private SpringAnimation galleryOpenCloseSpringAnimator;
    private final GalleryListView listView;
    private Utilities.Callback onGalleryListener;

    public GallerySheet(Context context, Theme.ResourcesProvider resourcesProvider, float f) {
        super(context, false, resourcesProvider);
        fixNavigationBar(-14737633);
        GalleryListView galleryListView = new GalleryListView(UserConfig.selectedAccount, context, new DarkThemeResourceProvider(), null, true, f) {
            @Override
            public String getTitle() {
                return "Choose cover";
            }
        };
        this.listView = galleryListView;
        galleryListView.allowSearch(false);
        galleryListView.setMultipleOnClick(false);
        galleryListView.setOnBackClickListener(new Runnable() {
            @Override
            public final void run() {
                GallerySheet.this.lambda$new$0();
            }
        });
        galleryListView.setOnSelectListener(new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                GallerySheet.this.lambda$new$1(obj, (Bitmap) obj2);
            }
        });
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context);
        this.containerView = sizeNotifierFrameLayout;
        int i = this.backgroundPaddingLeft;
        sizeNotifierFrameLayout.setPadding(i, 0, i, 0);
        this.containerView.addView(galleryListView);
    }

    private void animate(boolean z, final Runnable runnable) {
        float translationY = this.listView.getTranslationY();
        final float height = z ? 0.0f : (this.containerView.getHeight() - this.listView.top()) + (AndroidUtilities.navigationBarHeight * 2.5f);
        this.galleryListViewOpening = Boolean.valueOf(z);
        if (z) {
            SpringAnimation springAnimation = new SpringAnimation(this.listView, DynamicAnimation.TRANSLATION_Y, height);
            this.galleryOpenCloseSpringAnimator = springAnimation;
            springAnimation.getSpring().setDampingRatio(0.75f);
            this.galleryOpenCloseSpringAnimator.getSpring().setStiffness(350.0f);
            this.galleryOpenCloseSpringAnimator.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                    GallerySheet.this.lambda$animate$3(height, runnable, dynamicAnimation, z2, f, f2);
                }
            });
            this.galleryOpenCloseSpringAnimator.start();
            return;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(translationY, height);
        this.galleryOpenCloseAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                GallerySheet.this.lambda$animate$4(valueAnimator);
            }
        });
        this.galleryOpenCloseAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                GallerySheet.this.galleryOpenCloseAnimator = null;
                GallerySheet.this.galleryListViewOpening = null;
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        });
        this.galleryOpenCloseAnimator.setDuration(450L);
        this.galleryOpenCloseAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.galleryOpenCloseAnimator.start();
    }

    public void lambda$animate$3(float f, Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f2, float f3) {
        if (z) {
            return;
        }
        this.listView.setTranslationY(f);
        this.listView.ignoreScroll = false;
        this.galleryOpenCloseSpringAnimator = null;
        this.galleryListViewOpening = null;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$animate$4(ValueAnimator valueAnimator) {
        this.listView.setTranslationY(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void lambda$dismiss$2() {
        super.lambda$new$0();
    }

    public void lambda$new$1(Object obj, Bitmap bitmap) {
        Utilities.Callback callback;
        if (obj == null || this.galleryListViewOpening != null || !(obj instanceof MediaController.PhotoEntry) || (callback = this.onGalleryListener) == null) {
            return;
        }
        callback.run((MediaController.PhotoEntry) obj);
    }

    @Override
    public void lambda$new$0() {
        animate(false, new Runnable() {
            @Override
            public final void run() {
                GallerySheet.this.lambda$dismiss$2();
            }
        });
        super.lambda$new$0();
    }

    public void setOnGalleryImage(Utilities.Callback callback) {
        this.onGalleryListener = callback;
    }

    @Override
    public void show() {
        super.show();
        animate(true, null);
    }
}
