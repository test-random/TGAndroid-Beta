package org.telegram.ui.ActionBar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import androidx.core.util.Supplier;
import java.util.List;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;

public interface INavigationLayout {

    public abstract class CC {
        public static boolean $default$addFragmentToStack(INavigationLayout iNavigationLayout, BaseFragment baseFragment) {
            return iNavigationLayout.addFragmentToStack(baseFragment, -1);
        }

        public static void $default$animateThemedValues(INavigationLayout iNavigationLayout, Theme.ThemeInfo themeInfo, int i, boolean z, boolean z2) {
            iNavigationLayout.animateThemedValues(new ThemeAnimationSettings(themeInfo, i, z, z2), null);
        }

        public static void $default$animateThemedValues(INavigationLayout iNavigationLayout, Theme.ThemeInfo themeInfo, int i, boolean z, boolean z2, Runnable runnable) {
            iNavigationLayout.animateThemedValues(new ThemeAnimationSettings(themeInfo, i, z, z2), runnable);
        }

        public static void $default$closeLastFragment(INavigationLayout iNavigationLayout) {
            iNavigationLayout.closeLastFragment(true);
        }

        public static void $default$dismissDialogs(INavigationLayout iNavigationLayout) {
            List fragmentStack = iNavigationLayout.getFragmentStack();
            if (fragmentStack.isEmpty()) {
                return;
            }
            ((BaseFragment) fragmentStack.get(fragmentStack.size() - 1)).dismissCurrentDialog();
        }

        public static void $default$drawHeaderShadow(INavigationLayout iNavigationLayout, Canvas canvas, int i) {
            iNavigationLayout.drawHeaderShadow(canvas, 255, i);
        }

        public static BaseFragment $default$getBackgroundFragment(INavigationLayout iNavigationLayout) {
            if (iNavigationLayout.getFragmentStack().size() <= 1) {
                return null;
            }
            return (BaseFragment) iNavigationLayout.getFragmentStack().get(iNavigationLayout.getFragmentStack().size() - 2);
        }

        public static BottomSheet $default$getBottomSheet(INavigationLayout iNavigationLayout) {
            return null;
        }

        public static Activity $default$getParentActivity(INavigationLayout iNavigationLayout) {
            Context context = iNavigationLayout.getView().getContext();
            if (context instanceof Activity) {
                return (Activity) context;
            }
            throw new IllegalArgumentException("NavigationLayout added in non-activity context!");
        }

        public static BaseFragment $default$getSafeLastFragment(INavigationLayout iNavigationLayout) {
            if (iNavigationLayout.getFragmentStack().isEmpty()) {
                return null;
            }
            for (int size = iNavigationLayout.getFragmentStack().size() - 1; size >= 0; size--) {
                BaseFragment baseFragment = (BaseFragment) iNavigationLayout.getFragmentStack().get(size);
                if (baseFragment != null && !baseFragment.isFinishing() && !baseFragment.isRemovingFromStack()) {
                    return baseFragment;
                }
            }
            return null;
        }

        public static ViewGroup $default$getView(INavigationLayout iNavigationLayout) {
            if (iNavigationLayout instanceof ViewGroup) {
                return (ViewGroup) iNavigationLayout;
            }
            throw new IllegalArgumentException("You should override getView() if you're not inheriting from it.");
        }

        public static boolean $default$hasIntegratedBlurInPreview(INavigationLayout iNavigationLayout) {
            return false;
        }

        public static boolean $default$isActionBarInCrossfade(INavigationLayout iNavigationLayout) {
            return false;
        }

        public static boolean $default$presentFragment(INavigationLayout iNavigationLayout, BaseFragment baseFragment) {
            return iNavigationLayout.presentFragment(new NavigationParams(baseFragment));
        }

        public static boolean $default$presentFragment(INavigationLayout iNavigationLayout, BaseFragment baseFragment, boolean z) {
            return iNavigationLayout.presentFragment(new NavigationParams(baseFragment).setRemoveLast(z));
        }

        public static boolean $default$presentFragment(INavigationLayout iNavigationLayout, BaseFragment baseFragment, boolean z, boolean z2, boolean z3, boolean z4) {
            return iNavigationLayout.presentFragment(new NavigationParams(baseFragment).setRemoveLast(z).setNoAnimation(z2).setCheckPresentFromDelegate(z3).setPreview(z4));
        }

        public static boolean $default$presentFragment(INavigationLayout iNavigationLayout, BaseFragment baseFragment, boolean z, boolean z2, boolean z3, boolean z4, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
            return iNavigationLayout.presentFragment(new NavigationParams(baseFragment).setRemoveLast(z).setNoAnimation(z2).setCheckPresentFromDelegate(z3).setPreview(z4).setMenuView(actionBarPopupWindowLayout));
        }

        public static boolean $default$presentFragmentAsPreview(INavigationLayout iNavigationLayout, BaseFragment baseFragment) {
            return iNavigationLayout.presentFragment(new NavigationParams(baseFragment).setPreview(true));
        }

        public static boolean $default$presentFragmentAsPreviewWithMenu(INavigationLayout iNavigationLayout, BaseFragment baseFragment, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
            return iNavigationLayout.presentFragment(new NavigationParams(baseFragment).setPreview(true).setMenuView(actionBarPopupWindowLayout));
        }

        public static void $default$rebuildFragments(INavigationLayout iNavigationLayout, int i) {
            if ((i & 2) != 0) {
                iNavigationLayout.showLastFragment();
            } else {
                boolean z = (i & 1) != 0;
                iNavigationLayout.rebuildAllFragmentViews(z, z);
            }
        }

        public static void $default$removeFragmentFromStack(INavigationLayout iNavigationLayout, int i) {
            if (i < 0 || i >= iNavigationLayout.getFragmentStack().size()) {
                return;
            }
            iNavigationLayout.removeFragmentFromStack((BaseFragment) iNavigationLayout.getFragmentStack().get(i));
        }

        public static void $default$removeFragmentFromStack(INavigationLayout iNavigationLayout, BaseFragment baseFragment) {
            iNavigationLayout.removeFragmentFromStack(baseFragment, false);
        }

        public static INavigationLayout newLayout(Context context, boolean z) {
            return new ActionBarLayout(context, z);
        }

        public static INavigationLayout newLayout(Context context, boolean z, Supplier supplier) {
            return new ActionBarLayout(context, z) {
                final Supplier val$supplier;

                AnonymousClass1(Context context2, boolean z2, Supplier supplier2) {
                    super(context2, z2);
                    r3 = supplier2;
                }

                @Override
                public BottomSheet getBottomSheet() {
                    return (BottomSheet) r3.get();
                }
            };
        }
    }

    public class AnonymousClass1 extends ActionBarLayout {
        final Supplier val$supplier;

        AnonymousClass1(Context context2, boolean z2, Supplier supplier2) {
            super(context2, z2);
            r3 = supplier2;
        }

        @Override
        public BottomSheet getBottomSheet() {
            return (BottomSheet) r3.get();
        }
    }

    public enum BackButtonState {
        BACK,
        MENU
    }

    public interface INavigationLayoutDelegate {

        public abstract class CC {
            public static boolean $default$needAddFragmentToStack(INavigationLayoutDelegate iNavigationLayoutDelegate, BaseFragment baseFragment, INavigationLayout iNavigationLayout) {
                return true;
            }

            public static boolean $default$needPresentFragment(INavigationLayoutDelegate iNavigationLayoutDelegate, BaseFragment baseFragment, boolean z, boolean z2, INavigationLayout iNavigationLayout) {
                return true;
            }

            public static void $default$onMeasureOverride(INavigationLayoutDelegate iNavigationLayoutDelegate, int[] iArr) {
            }

            public static boolean $default$onPreIme(INavigationLayoutDelegate iNavigationLayoutDelegate) {
                return false;
            }

            public static void $default$onRebuildAllFragments(INavigationLayoutDelegate iNavigationLayoutDelegate, INavigationLayout iNavigationLayout, boolean z) {
            }

            public static void $default$onThemeProgress(INavigationLayoutDelegate iNavigationLayoutDelegate, float f) {
            }
        }

        boolean needAddFragmentToStack(BaseFragment baseFragment, INavigationLayout iNavigationLayout);

        boolean needCloseLastFragment(INavigationLayout iNavigationLayout);

        boolean needPresentFragment(BaseFragment baseFragment, boolean z, boolean z2, INavigationLayout iNavigationLayout);

        boolean needPresentFragment(INavigationLayout iNavigationLayout, NavigationParams navigationParams);

        void onMeasureOverride(int[] iArr);

        boolean onPreIme();

        void onRebuildAllFragments(INavigationLayout iNavigationLayout, boolean z);

        void onThemeProgress(float f);
    }

    public static class NavigationParams {
        public boolean checkPresentFromDelegate = true;
        public BaseFragment fragment;
        public ActionBarPopupWindow.ActionBarPopupWindowLayout menuView;
        public boolean noAnimation;
        public boolean preview;
        public boolean removeLast;

        public NavigationParams(BaseFragment baseFragment) {
            this.fragment = baseFragment;
        }

        public NavigationParams setCheckPresentFromDelegate(boolean z) {
            this.checkPresentFromDelegate = z;
            return this;
        }

        public NavigationParams setMenuView(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
            this.menuView = actionBarPopupWindowLayout;
            return this;
        }

        public NavigationParams setNoAnimation(boolean z) {
            this.noAnimation = z;
            return this;
        }

        public NavigationParams setPreview(boolean z) {
            this.preview = z;
            return this;
        }

        public NavigationParams setRemoveLast(boolean z) {
            this.removeLast = z;
            return this;
        }
    }

    public static class StartColorsProvider implements Theme.ResourcesProvider {
        SparseIntArray colors = new SparseIntArray();
        int[] keysToSave = {Theme.key_chat_outBubble, Theme.key_chat_outBubbleGradient1, Theme.key_chat_outBubbleGradient2, Theme.key_chat_outBubbleGradient3, Theme.key_chat_outBubbleGradientAnimated, Theme.key_chat_outBubbleShadow};

        @Override
        public void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
            Theme.applyServiceShaderMatrix(i, i2, f, f2);
        }

        @Override
        public ColorFilter getAnimatedEmojiColorFilter() {
            ColorFilter colorFilter;
            colorFilter = Theme.chat_animatedEmojiTextColorFilter;
            return colorFilter;
        }

        @Override
        public int getColor(int i) {
            int indexOfKey = this.colors.indexOfKey(i);
            return indexOfKey >= 0 ? this.colors.valueAt(indexOfKey) : Theme.getColor(i);
        }

        @Override
        public int getColorOrDefault(int i) {
            int color;
            color = getColor(i);
            return color;
        }

        @Override
        public int getCurrentColor(int i) {
            return this.colors.get(i);
        }

        @Override
        public Drawable getDrawable(String str) {
            return Theme.ResourcesProvider.CC.$default$getDrawable(this, str);
        }

        @Override
        public Paint getPaint(String str) {
            Paint themePaint;
            themePaint = Theme.getThemePaint(str);
            return themePaint;
        }

        @Override
        public boolean hasGradientService() {
            return Theme.ResourcesProvider.CC.$default$hasGradientService(this);
        }

        @Override
        public boolean isDark() {
            boolean isCurrentThemeDark;
            isCurrentThemeDark = Theme.isCurrentThemeDark();
            return isCurrentThemeDark;
        }

        public void saveColors(Theme.ResourcesProvider resourcesProvider) {
            this.colors.clear();
            for (int i : this.keysToSave) {
                this.colors.put(i, resourcesProvider.getCurrentColor(i));
            }
        }

        @Override
        public void setAnimatedColor(int i, int i2) {
            Theme.ResourcesProvider.CC.$default$setAnimatedColor(this, i, i2);
        }
    }

    public static class ThemeAnimationSettings {
        public final int accentId;
        public Runnable afterAnimationRunnable;
        public Runnable afterStartDescriptionsAddedRunnable;
        public onAnimationProgress animationProgress;
        public Runnable beforeAnimationRunnable;
        public final boolean instant;
        public final boolean nightTheme;
        public boolean onlyTopFragment;
        public Theme.ResourcesProvider resourcesProvider;
        public final Theme.ThemeInfo theme;
        public boolean applyTheme = true;
        public boolean applyTrulyTheme = true;
        public long duration = 200;

        public interface onAnimationProgress {
            void setProgress(float f);
        }

        public ThemeAnimationSettings(Theme.ThemeInfo themeInfo, int i, boolean z, boolean z2) {
            this.theme = themeInfo;
            this.accentId = i;
            this.nightTheme = z;
            this.instant = z2;
        }
    }

    boolean addFragmentToStack(BaseFragment baseFragment);

    boolean addFragmentToStack(BaseFragment baseFragment, int i);

    boolean allowSwipe();

    void animateThemedValues(ThemeAnimationSettings themeAnimationSettings, Runnable runnable);

    void animateThemedValues(Theme.ThemeInfo themeInfo, int i, boolean z, boolean z2);

    void animateThemedValues(Theme.ThemeInfo themeInfo, int i, boolean z, boolean z2, Runnable runnable);

    boolean checkTransitionAnimation();

    void closeLastFragment();

    void closeLastFragment(boolean z);

    void dismissDialogs();

    void drawCurrentPreviewFragment(Canvas canvas, Drawable drawable);

    void drawHeaderShadow(Canvas canvas, int i);

    void drawHeaderShadow(Canvas canvas, int i, int i2);

    void expandPreviewFragment();

    void finishPreviewFragment();

    BaseFragment getBackgroundFragment();

    BottomSheet getBottomSheet();

    int getBottomTabsHeight(boolean z);

    float getCurrentPreviewFragmentAlpha();

    DrawerLayoutContainer getDrawerLayoutContainer();

    List getFragmentStack();

    BaseFragment getLastFragment();

    Theme.MessageDrawable getMessageDrawableOutMediaStart();

    Theme.MessageDrawable getMessageDrawableOutStart();

    FrameLayout getOverlayContainerView();

    Activity getParentActivity();

    List getPulledDialogs();

    BaseFragment getSafeLastFragment();

    float getThemeAnimationValue();

    ViewGroup getView();

    Window getWindow();

    boolean hasIntegratedBlurInPreview();

    boolean isActionBarInCrossfade();

    boolean isInBubbleMode();

    boolean isInPreviewMode();

    boolean isPreviewOpenAnimationInProgress();

    boolean isSheet();

    boolean isSwipeInProgress();

    boolean isTransitionAnimationInProgress();

    void movePreviewFragment(float f);

    void onBackPressed();

    void onLowMemory();

    void onPause();

    void onResume();

    boolean presentFragment(BaseFragment baseFragment);

    boolean presentFragment(BaseFragment baseFragment, boolean z);

    boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2, boolean z3, boolean z4);

    boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2, boolean z3, boolean z4, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout);

    boolean presentFragment(NavigationParams navigationParams);

    boolean presentFragmentAsPreview(BaseFragment baseFragment);

    boolean presentFragmentAsPreviewWithMenu(BaseFragment baseFragment, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout);

    void rebuildAllFragmentViews(boolean z, boolean z2);

    void rebuildFragments(int i);

    void removeAllFragments();

    void removeFragmentFromStack(int i);

    void removeFragmentFromStack(BaseFragment baseFragment);

    void removeFragmentFromStack(BaseFragment baseFragment, boolean z);

    void resumeDelayedFragmentAnimation();

    void setBackgroundView(View view);

    void setDelegate(INavigationLayoutDelegate iNavigationLayoutDelegate);

    void setDrawerLayoutContainer(DrawerLayoutContainer drawerLayoutContainer);

    void setFragmentPanTranslationOffset(int i);

    void setFragmentStack(List list);

    void setHighlightActionButtons(boolean z);

    void setInBubbleMode(boolean z);

    void setIsSheet(boolean z);

    void setNavigationBarColor(int i);

    void setPulledDialogs(List list);

    void setRemoveActionBarExtraHeight(boolean z);

    void setUseAlphaAnimations(boolean z);

    void setWindow(Window window);

    void showLastFragment();

    void startActivityForResult(Intent intent, int i);
}
