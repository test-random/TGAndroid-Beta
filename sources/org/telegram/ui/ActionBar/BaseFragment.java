package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.accessibility.AccessibilityManager;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.Supplier;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.SecretChatHelper;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.EmptyBaseFragment;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stories.StoryViewer;
import org.telegram.ui.bots.BotWebViewAttachedSheet;

public abstract class BaseFragment {
    protected ActionBar actionBar;
    protected Bundle arguments;
    protected boolean finishing;
    protected boolean fragmentBeginToShow;
    public View fragmentView;
    private Runnable fullyVisibleListener;
    protected boolean inBubbleMode;
    protected boolean inMenuMode;
    protected boolean inPreviewMode;
    protected boolean isFinished;
    private boolean isFullyVisible;
    protected Dialog parentDialog;
    protected INavigationLayout parentLayout;
    private PreviewDelegate previewDelegate;
    private boolean removingFromStack;
    protected Theme.ResourcesProvider resourceProvider;
    public ArrayList sheetsStack;
    public Dialog visibleDialog;
    protected int currentAccount = UserConfig.selectedAccount;
    protected boolean hasOwnBackground = false;
    protected boolean isPaused = true;
    protected boolean inTransitionAnimation = false;
    protected int classGuid = ConnectionsManager.generateClassGuid();

    public class AnonymousClass1 extends BottomSheet {
        final INavigationLayout[] val$actionBarLayout;
        final BottomSheet[] val$bottomSheet;
        final BaseFragment val$fragment;
        final BottomSheetParams val$params;

        AnonymousClass1(Context context, boolean z, Theme.ResourcesProvider resourcesProvider, final BottomSheetParams bottomSheetParams, INavigationLayout[] iNavigationLayoutArr, final BaseFragment baseFragment, BottomSheet[] bottomSheetArr) {
            super(context, z, resourcesProvider);
            this.val$params = bottomSheetParams;
            this.val$actionBarLayout = iNavigationLayoutArr;
            this.val$fragment = baseFragment;
            this.val$bottomSheet = bottomSheetArr;
            boolean z2 = bottomSheetParams != null && bottomSheetParams.occupyNavigationBar;
            this.occupyNavigationBar = z2;
            this.drawNavigationBar = true ^ z2;
            iNavigationLayoutArr[0].setFragmentStack(new ArrayList());
            iNavigationLayoutArr[0].addFragmentToStack(baseFragment);
            iNavigationLayoutArr[0].showLastFragment();
            ViewGroup view = iNavigationLayoutArr[0].getView();
            int i = this.backgroundPaddingLeft;
            view.setPadding(i, 0, i, 0);
            this.containerView = iNavigationLayoutArr[0].getView();
            setApplyBottomPadding(false);
            setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public final void onDismiss(DialogInterface dialogInterface) {
                    BaseFragment.AnonymousClass1.lambda$new$0(BaseFragment.this, bottomSheetParams, dialogInterface);
                }
            });
        }

        public static void lambda$new$0(BaseFragment baseFragment, BottomSheetParams bottomSheetParams, DialogInterface dialogInterface) {
            Runnable runnable;
            baseFragment.onPause();
            baseFragment.onFragmentDestroy();
            if (bottomSheetParams == null || (runnable = bottomSheetParams.onDismiss) == null) {
                return;
            }
            runnable.run();
        }

        @Override
        protected boolean canDismissWithSwipe() {
            return false;
        }

        @Override
        protected boolean canSwipeToBack(MotionEvent motionEvent) {
            INavigationLayout iNavigationLayout;
            BottomSheetParams bottomSheetParams = this.val$params;
            if (bottomSheetParams == null || !bottomSheetParams.transitionFromLeft || (iNavigationLayout = this.val$actionBarLayout[0]) == null || iNavigationLayout.getFragmentStack().size() > 1) {
                return false;
            }
            return this.val$actionBarLayout[0].getFragmentStack().size() != 1 || ((BaseFragment) this.val$actionBarLayout[0].getFragmentStack().get(0)).isSwipeBackEnabled(motionEvent);
        }

        @Override
        public void dismiss() {
            BottomSheetParams bottomSheetParams;
            Runnable runnable;
            if (!isDismissed() && (bottomSheetParams = this.val$params) != null && (runnable = bottomSheetParams.onPreFinished) != null) {
                runnable.run();
            }
            super.dismiss();
            LaunchActivity.instance.sheetFragmentsStack.remove(this.val$actionBarLayout[0]);
            this.val$actionBarLayout[0] = null;
        }

        @Override
        public void onBackPressed() {
            INavigationLayout iNavigationLayout = this.val$actionBarLayout[0];
            if (iNavigationLayout == null || iNavigationLayout.getFragmentStack().size() <= 1) {
                super.onBackPressed();
            } else {
                this.val$actionBarLayout[0].onBackPressed();
            }
        }

        @Override
        protected void onCreate(Bundle bundle) {
            super.onCreate(bundle);
            this.val$actionBarLayout[0].setWindow(this.val$bottomSheet[0].getWindow());
            BottomSheetParams bottomSheetParams = this.val$params;
            if (bottomSheetParams == null || !bottomSheetParams.occupyNavigationBar) {
                fixNavigationBar(Theme.getColor(Theme.key_dialogBackgroundGray, this.val$fragment.getResourceProvider()));
            } else {
                AndroidUtilities.setLightNavigationBar(this.val$bottomSheet[0].getWindow(), true);
            }
            AndroidUtilities.setLightStatusBar(getWindow(), this.val$fragment.isLightStatusBar());
            this.val$fragment.onBottomSheetCreated();
        }

        @Override
        protected void onInsetsChanged() {
            INavigationLayout iNavigationLayout = this.val$actionBarLayout[0];
            if (iNavigationLayout != null) {
                for (BaseFragment baseFragment : iNavigationLayout.getFragmentStack()) {
                    if (baseFragment.getFragmentView() != null) {
                        baseFragment.getFragmentView().requestLayout();
                    }
                }
            }
        }

        @Override
        public void onOpenAnimationEnd() {
            Runnable runnable;
            this.val$fragment.onTransitionAnimationEnd(true, false);
            BottomSheetParams bottomSheetParams = this.val$params;
            if (bottomSheetParams == null || (runnable = bottomSheetParams.onOpenAnimationFinished) == null) {
                return;
            }
            runnable.run();
        }
    }

    public interface AttachedSheet {

        public abstract class CC {
            public static void $default$setLastVisible(AttachedSheet attachedSheet, boolean z) {
            }
        }

        boolean attachedToParent();

        void dismiss();

        void dismiss(boolean z);

        int getNavigationBarColor(int i);

        View mo998getWindowView();

        boolean isAttachedLightStatusBar();

        boolean isFullyVisible();

        boolean isShown();

        boolean onAttachedBackPressed();

        void setKeyboardHeightFromParent(int i);

        void setLastVisible(boolean z);

        void setOnDismissListener(Runnable runnable);

        boolean showDialog(Dialog dialog);
    }

    public interface AttachedSheetWindow {
    }

    public static class BottomSheetParams {
        public boolean allowNestedScroll;
        public boolean occupyNavigationBar;
        public Runnable onDismiss;
        public Runnable onOpenAnimationFinished;
        public Runnable onPreFinished;
        public boolean transitionFromLeft;
    }

    public interface PreviewDelegate {
        void finishFragment();
    }

    public BaseFragment() {
    }

    public BaseFragment(Bundle bundle) {
        this.arguments = bundle;
    }

    public static boolean hasSheets(BaseFragment baseFragment) {
        EmptyBaseFragment sheetFragment;
        if (baseFragment == null) {
            return false;
        }
        if (baseFragment.hasShownSheet()) {
            return true;
        }
        return (baseFragment.getParentLayout() instanceof ActionBarLayout) && (sheetFragment = ((ActionBarLayout) baseFragment.getParentLayout()).getSheetFragment(false)) != null && sheetFragment.hasShownSheet();
    }

    public static BottomSheet lambda$showAsSheet$1(BottomSheet[] bottomSheetArr) {
        return bottomSheetArr[0];
    }

    public void lambda$showDialog$0(DialogInterface.OnDismissListener onDismissListener, DialogInterface dialogInterface) {
        if (onDismissListener != null) {
            onDismissListener.onDismiss(dialogInterface);
        }
        onDialogDismiss((Dialog) dialogInterface);
        if (dialogInterface == this.visibleDialog) {
            this.visibleDialog = null;
        }
    }

    private void setParentDialog(Dialog dialog) {
        this.parentDialog = dialog;
    }

    private void updateSheetsVisibility() {
        if (this.sheetsStack == null) {
            return;
        }
        for (int i = 0; i < this.sheetsStack.size(); i++) {
            AttachedSheet attachedSheet = (AttachedSheet) this.sheetsStack.get(i);
            boolean z = true;
            if (i != this.sheetsStack.size() - 1 || !this.isFullyVisible) {
                z = false;
            }
            attachedSheet.setLastVisible(z);
        }
    }

    public void addSheet(AttachedSheet attachedSheet) {
        if (this.sheetsStack == null) {
            this.sheetsStack = new ArrayList();
        }
        StoryViewer lastStoryViewer = getLastStoryViewer();
        if (lastStoryViewer != null) {
            lastStoryViewer.listenToAttachedSheet(attachedSheet);
        }
        this.sheetsStack.add(attachedSheet);
        updateSheetsVisibility();
    }

    public boolean allowFinishFragmentInsteadOfRemoveFromStack() {
        return true;
    }

    protected boolean allowPresentFragment() {
        return true;
    }

    public void attachSheets(ActionBarLayout.LayoutContainer layoutContainer) {
        if (this.sheetsStack != null) {
            for (int i = 0; i < this.sheetsStack.size(); i++) {
                AttachedSheet attachedSheet = (AttachedSheet) this.sheetsStack.get(i);
                if (attachedSheet != null && attachedSheet.attachedToParent()) {
                    AndroidUtilities.removeFromParent(attachedSheet.mo998getWindowView());
                    layoutContainer.addView(attachedSheet.mo998getWindowView());
                }
            }
        }
    }

    public boolean canBeginSlide() {
        return true;
    }

    public void clearSheets() {
        ArrayList arrayList = this.sheetsStack;
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        for (int size = this.sheetsStack.size() - 1; size >= 0; size--) {
            ((AttachedSheet) this.sheetsStack.get(size)).dismiss(true);
        }
        this.sheetsStack.clear();
    }

    public void clearViews() {
        View view = this.fragmentView;
        if (view != null) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            if (viewGroup != null) {
                try {
                    onRemoveFromParent();
                    viewGroup.removeViewInLayout(this.fragmentView);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            this.fragmentView = null;
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            ViewGroup viewGroup2 = (ViewGroup) actionBar.getParent();
            if (viewGroup2 != null) {
                try {
                    viewGroup2.removeViewInLayout(this.actionBar);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            this.actionBar = null;
        }
        clearSheets();
        this.parentLayout = null;
    }

    public boolean closeLastFragment() {
        return false;
    }

    public boolean closeSheet() {
        ArrayList arrayList = this.sheetsStack;
        if (arrayList == null) {
            return false;
        }
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            if (((AttachedSheet) this.sheetsStack.get(size)).isShown()) {
                return ((AttachedSheet) this.sheetsStack.get(size)).onAttachedBackPressed();
            }
        }
        return false;
    }

    public ActionBar createActionBar(Context context) {
        ActionBar actionBar = new ActionBar(context, getResourceProvider());
        actionBar.setBackgroundColor(getThemedColor(Theme.key_actionBarDefault));
        actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_actionBarDefaultSelector), false);
        actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_actionBarActionModeDefaultSelector), true);
        actionBar.setItemsColor(getThemedColor(Theme.key_actionBarDefaultIcon), false);
        actionBar.setItemsColor(getThemedColor(Theme.key_actionBarActionModeDefaultIcon), true);
        if (this.inPreviewMode || this.inBubbleMode) {
            actionBar.setOccupyStatusBar(false);
        }
        return actionBar;
    }

    public ArticleViewer createArticleViewer(boolean z) {
        if (this.sheetsStack == null) {
            this.sheetsStack = new ArrayList();
        }
        if (!z) {
            if ((getLastSheet() instanceof ArticleViewer.Sheet) && getLastSheet().isShown()) {
                return ((ArticleViewer.Sheet) getLastSheet()).getArticleViewer();
            }
            INavigationLayout iNavigationLayout = this.parentLayout;
            if ((iNavigationLayout instanceof ActionBarLayout) && ((ActionBarLayout) iNavigationLayout).getSheetFragment(false) != null && (((ActionBarLayout) this.parentLayout).getSheetFragment(false).getLastSheet() instanceof ArticleViewer.Sheet)) {
                ArticleViewer.Sheet sheet = (ArticleViewer.Sheet) ((ActionBarLayout) this.parentLayout).getSheetFragment(false).getLastSheet();
                if (sheet.isShown()) {
                    return sheet.getArticleViewer();
                }
            }
        }
        ArticleViewer makeSheet = ArticleViewer.makeSheet(this);
        addSheet(makeSheet.sheet);
        BottomSheetTabDialog.checkSheet(makeSheet.sheet);
        return makeSheet;
    }

    public BotWebViewAttachedSheet createBotViewer() {
        BotWebViewAttachedSheet botWebViewAttachedSheet = new BotWebViewAttachedSheet(this);
        addSheet(botWebViewAttachedSheet);
        BottomSheetTabDialog.checkSheet(botWebViewAttachedSheet);
        return botWebViewAttachedSheet;
    }

    public StoryViewer createOverlayStoryViewer() {
        if (this.sheetsStack == null) {
            this.sheetsStack = new ArrayList();
        }
        StoryViewer storyViewer = new StoryViewer(this);
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout != null && iNavigationLayout.isSheet()) {
            storyViewer.fromBottomSheet = true;
        }
        this.sheetsStack.add(storyViewer);
        updateSheetsVisibility();
        return storyViewer;
    }

    public View createView(Context context) {
        return null;
    }

    public void detachSheets() {
        if (this.sheetsStack != null) {
            for (int i = 0; i < this.sheetsStack.size(); i++) {
                AttachedSheet attachedSheet = (AttachedSheet) this.sheetsStack.get(i);
                if (attachedSheet != null && attachedSheet.attachedToParent()) {
                    AndroidUtilities.removeFromParent(attachedSheet.mo998getWindowView());
                }
            }
        }
    }

    public void dismissCurrentDialog() {
        Dialog dialog = this.visibleDialog;
        if (dialog == null) {
            return;
        }
        try {
            dialog.dismiss();
            this.visibleDialog = null;
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean dismissDialogOnPause(Dialog dialog) {
        return true;
    }

    public boolean extendActionMode(Menu menu) {
        return false;
    }

    public void lambda$onBackPressed$319() {
        PreviewDelegate previewDelegate;
        Dialog dialog = this.parentDialog;
        if (dialog != null) {
            dialog.dismiss();
        } else if (!this.inPreviewMode || (previewDelegate = this.previewDelegate) == null) {
            finishFragment(true);
        } else {
            previewDelegate.finishFragment();
        }
    }

    public boolean finishFragment(boolean z) {
        INavigationLayout iNavigationLayout;
        if (this.isFinished || (iNavigationLayout = this.parentLayout) == null) {
            return false;
        }
        this.finishing = true;
        iNavigationLayout.closeLastFragment(z);
        return true;
    }

    public void finishPreviewFragment() {
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout != null) {
            iNavigationLayout.finishPreviewFragment();
        }
    }

    public AccountInstance getAccountInstance() {
        return AccountInstance.getInstance(this.currentAccount);
    }

    public ActionBar getActionBar() {
        return this.actionBar;
    }

    public Bundle getArguments() {
        return this.arguments;
    }

    public int getClassGuid() {
        return this.classGuid;
    }

    public ConnectionsManager getConnectionsManager() {
        return getAccountInstance().getConnectionsManager();
    }

    public ContactsController getContactsController() {
        return getAccountInstance().getContactsController();
    }

    public Context getContext() {
        return getParentActivity();
    }

    public int getCurrentAccount() {
        return this.currentAccount;
    }

    public Animator getCustomSlideTransition(boolean z, boolean z2, float f) {
        return null;
    }

    public DownloadController getDownloadController() {
        return getAccountInstance().getDownloadController();
    }

    public FileLoader getFileLoader() {
        return getAccountInstance().getFileLoader();
    }

    public boolean getFragmentBeginToShow() {
        return this.fragmentBeginToShow;
    }

    public BaseFragment getFragmentForAlert(int i) {
        INavigationLayout iNavigationLayout = this.parentLayout;
        return (iNavigationLayout == null || iNavigationLayout.getFragmentStack().size() <= i + 1) ? this : (BaseFragment) this.parentLayout.getFragmentStack().get((this.parentLayout.getFragmentStack().size() - 2) - i);
    }

    public View getFragmentView() {
        return this.fragmentView;
    }

    public AttachedSheet getLastSheet() {
        ArrayList arrayList = this.sheetsStack;
        if (arrayList != null && !arrayList.isEmpty()) {
            for (int size = this.sheetsStack.size() - 1; size >= 0; size--) {
                if (((AttachedSheet) this.sheetsStack.get(size)).isShown()) {
                    return (AttachedSheet) this.sheetsStack.get(size);
                }
            }
        }
        return null;
    }

    public StoryViewer getLastStoryViewer() {
        ArrayList arrayList = this.sheetsStack;
        if (arrayList != null && !arrayList.isEmpty()) {
            for (int size = this.sheetsStack.size() - 1; size >= 0; size--) {
                if ((this.sheetsStack.get(size) instanceof StoryViewer) && ((AttachedSheet) this.sheetsStack.get(size)).isShown()) {
                    return (StoryViewer) this.sheetsStack.get(size);
                }
            }
        }
        return null;
    }

    public FrameLayout getLayoutContainer() {
        View view = this.fragmentView;
        if (view == null) {
            return null;
        }
        ViewParent parent = view.getParent();
        if (parent instanceof FrameLayout) {
            return (FrameLayout) parent;
        }
        return null;
    }

    public LocationController getLocationController() {
        return getAccountInstance().getLocationController();
    }

    public MediaController getMediaController() {
        return MediaController.getInstance();
    }

    public MediaDataController getMediaDataController() {
        return getAccountInstance().getMediaDataController();
    }

    public MessagesController getMessagesController() {
        return getAccountInstance().getMessagesController();
    }

    public MessagesStorage getMessagesStorage() {
        return getAccountInstance().getMessagesStorage();
    }

    public int getNavigationBarColor() {
        int color = Theme.getColor(Theme.key_windowBackgroundGray, getResourceProvider());
        if (this.sheetsStack != null) {
            for (int i = 0; i < this.sheetsStack.size(); i++) {
                AttachedSheet attachedSheet = (AttachedSheet) this.sheetsStack.get(i);
                if (attachedSheet.attachedToParent()) {
                    color = attachedSheet.getNavigationBarColor(color);
                }
            }
        }
        return color;
    }

    public NotificationCenter getNotificationCenter() {
        return getAccountInstance().getNotificationCenter();
    }

    public NotificationsController getNotificationsController() {
        return getAccountInstance().getNotificationsController();
    }

    public SharedPreferences getNotificationsSettings() {
        return getAccountInstance().getNotificationsSettings();
    }

    public org.telegram.ui.Stories.StoryViewer getOrCreateStoryViewer() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.BaseFragment.getOrCreateStoryViewer():org.telegram.ui.Stories.StoryViewer");
    }

    public Activity getParentActivity() {
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout != null) {
            return iNavigationLayout.getParentActivity();
        }
        return null;
    }

    public INavigationLayout getParentLayout() {
        return this.parentLayout;
    }

    public int getPreviewHeight() {
        return -1;
    }

    public Theme.ResourcesProvider getResourceProvider() {
        return this.resourceProvider;
    }

    public SecretChatHelper getSecretChatHelper() {
        return getAccountInstance().getSecretChatHelper();
    }

    public SendMessagesHelper getSendMessagesHelper() {
        return getAccountInstance().getSendMessagesHelper();
    }

    public ArrayList getThemeDescriptions() {
        return new ArrayList();
    }

    public int getThemedColor(int i) {
        return Theme.getColor(i, getResourceProvider());
    }

    public Drawable getThemedDrawable(String str) {
        return Theme.getThemeDrawable(str);
    }

    public Paint getThemedPaint(String str) {
        Paint paint = getResourceProvider() != null ? getResourceProvider().getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    public UserConfig getUserConfig() {
        return getAccountInstance().getUserConfig();
    }

    public Dialog getVisibleDialog() {
        return this.visibleDialog;
    }

    public boolean hasForceLightStatusBar() {
        return false;
    }

    public boolean hasSheet() {
        ArrayList arrayList = this.sheetsStack;
        return (arrayList == null || arrayList.isEmpty()) ? false : true;
    }

    public boolean hasShownSheet() {
        if (!hasSheet()) {
            return false;
        }
        for (int size = this.sheetsStack.size() - 1; size >= 0; size--) {
            if (((AttachedSheet) this.sheetsStack.get(size)).isShown()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasStoryViewer() {
        return getLastStoryViewer() != null;
    }

    public boolean hideKeyboardOnShow() {
        return true;
    }

    public boolean isBeginToShow() {
        return this.fragmentBeginToShow;
    }

    public boolean isFinishing() {
        return this.finishing;
    }

    public boolean isInBubbleMode() {
        return this.inBubbleMode;
    }

    public boolean isInPreviewMode() {
        return this.inPreviewMode;
    }

    public boolean isLastFragment() {
        INavigationLayout iNavigationLayout = this.parentLayout;
        return iNavigationLayout != null && iNavigationLayout.getLastFragment() == this;
    }

    public boolean isLightStatusBar() {
        if (getLastStoryViewer() != null && getLastStoryViewer().isShown()) {
            return false;
        }
        if (hasForceLightStatusBar() && !Theme.getCurrentTheme().isDark()) {
            return true;
        }
        Theme.ResourcesProvider resourceProvider = getResourceProvider();
        int i = Theme.key_actionBarDefault;
        ActionBar actionBar = this.actionBar;
        if (actionBar != null && actionBar.isActionModeShowed()) {
            i = Theme.key_actionBarActionModeDefault;
        }
        return ColorUtils.calculateLuminance(resourceProvider != null ? resourceProvider.getColorOrDefault(i) : Theme.getColor(i, null, true)) > 0.699999988079071d;
    }

    public boolean isPaused() {
        return this.isPaused;
    }

    public boolean isRemovingFromStack() {
        return this.removingFromStack;
    }

    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return true;
    }

    public void movePreviewFragment(float f) {
        this.parentLayout.movePreviewFragment(f);
    }

    public boolean needDelayOpenAnimation() {
        return false;
    }

    public void onActivityResultFragment(int i, int i2, Intent intent) {
    }

    public boolean onBackPressed() {
        return !closeSheet();
    }

    public void onBecomeFullyHidden() {
        this.isFullyVisible = false;
        updateSheetsVisibility();
    }

    public void onBecomeFullyVisible() {
        ActionBar actionBar;
        this.isFullyVisible = true;
        if (((AccessibilityManager) ApplicationLoader.applicationContext.getSystemService("accessibility")).isEnabled() && (actionBar = getActionBar()) != null) {
            String title = actionBar.getTitle();
            if (!TextUtils.isEmpty(title)) {
                setParentActivityTitle(title);
            }
        }
        Runnable runnable = this.fullyVisibleListener;
        if (runnable != null) {
            this.fullyVisibleListener = null;
            runnable.run();
        }
        updateSheetsVisibility();
    }

    public void onBeginSlide() {
        try {
            Dialog dialog = this.visibleDialog;
            if (dialog != null && dialog.isShowing()) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.onPause();
        }
    }

    public void onBottomSheetCreated() {
    }

    public void onConfigurationChanged(Configuration configuration) {
    }

    public AnimatorSet onCustomTransitionAnimation(boolean z, Runnable runnable) {
        return null;
    }

    public void onDialogDismiss(Dialog dialog) {
    }

    public void onFragmentClosed() {
    }

    public boolean onFragmentCreate() {
        return true;
    }

    public void onFragmentDestroy() {
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
        getMessagesStorage().cancelTasksForGuid(this.classGuid);
        this.isFinished = true;
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.setEnabled(false);
        }
        if (hasForceLightStatusBar() && !AndroidUtilities.isTablet() && getParentLayout().getLastFragment() == this && getParentActivity() != null && !this.finishing) {
            AndroidUtilities.setLightStatusBar(getParentActivity().getWindow(), Theme.getColor(Theme.key_actionBarDefault) == -1);
        }
        ArrayList arrayList = this.sheetsStack;
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                AttachedSheet attachedSheet = (AttachedSheet) this.sheetsStack.get(size);
                attachedSheet.setLastVisible(false);
                attachedSheet.dismiss(true);
                this.sheetsStack.remove(size);
            }
        }
    }

    public void onLowMemory() {
    }

    public void onPause() {
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.onPause();
        }
        this.isPaused = true;
        try {
            Dialog dialog = this.visibleDialog;
            if (dialog != null && dialog.isShowing() && dismissDialogOnPause(this.visibleDialog)) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (getLastStoryViewer() != null) {
            getLastStoryViewer().onPause();
            getLastStoryViewer().updatePlayingMode();
        }
    }

    public void onPreviewOpenAnimationEnd() {
    }

    public void onRemoveFromParent() {
        ArrayList arrayList = this.sheetsStack;
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        updateSheetsVisibility();
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
    }

    public void onResume() {
        this.isPaused = false;
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.onResume();
        }
        if (getLastStoryViewer() != null) {
            getLastStoryViewer().onResume();
            getLastStoryViewer().updatePlayingMode();
        }
    }

    public void onSlideProgress(boolean z, float f) {
    }

    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        this.inTransitionAnimation = false;
    }

    public void onTransitionAnimationProgress(boolean z, float f) {
    }

    public void onTransitionAnimationStart(boolean z, boolean z2) {
        this.inTransitionAnimation = true;
        if (z) {
            this.fragmentBeginToShow = true;
        }
    }

    public void onUserLeaveHint() {
    }

    public void prepareFragmentToSlide(boolean z, boolean z2) {
    }

    public boolean presentFragment(BaseFragment baseFragment) {
        INavigationLayout iNavigationLayout;
        return allowPresentFragment() && (iNavigationLayout = this.parentLayout) != null && iNavigationLayout.presentFragment(baseFragment);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z) {
        INavigationLayout iNavigationLayout;
        return allowPresentFragment() && (iNavigationLayout = this.parentLayout) != null && iNavigationLayout.presentFragment(baseFragment, z);
    }

    public boolean presentFragment(BaseFragment baseFragment, boolean z, boolean z2) {
        INavigationLayout iNavigationLayout;
        return allowPresentFragment() && (iNavigationLayout = this.parentLayout) != null && iNavigationLayout.presentFragment(baseFragment, z, z2, true, false, null);
    }

    public boolean presentFragment(INavigationLayout.NavigationParams navigationParams) {
        INavigationLayout iNavigationLayout;
        return allowPresentFragment() && (iNavigationLayout = this.parentLayout) != null && iNavigationLayout.presentFragment(navigationParams);
    }

    public boolean presentFragmentAsPreview(BaseFragment baseFragment) {
        INavigationLayout iNavigationLayout;
        return allowPresentFragment() && (iNavigationLayout = this.parentLayout) != null && iNavigationLayout.presentFragmentAsPreview(baseFragment);
    }

    public boolean presentFragmentAsPreviewWithMenu(BaseFragment baseFragment, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
        INavigationLayout iNavigationLayout;
        return allowPresentFragment() && (iNavigationLayout = this.parentLayout) != null && iNavigationLayout.presentFragmentAsPreviewWithMenu(baseFragment, actionBarPopupWindowLayout);
    }

    public void removeSelfFromStack() {
        removeSelfFromStack(false);
    }

    public void removeSelfFromStack(boolean z) {
        INavigationLayout iNavigationLayout;
        if (this.isFinished || (iNavigationLayout = this.parentLayout) == null) {
            return;
        }
        Dialog dialog = this.parentDialog;
        if (dialog != null) {
            dialog.dismiss();
        } else {
            iNavigationLayout.removeFragmentFromStack(this, z);
        }
    }

    public void removeSheet(AttachedSheet attachedSheet) {
        ArrayList arrayList = this.sheetsStack;
        if (arrayList == null) {
            return;
        }
        arrayList.remove(attachedSheet);
        updateSheetsVisibility();
    }

    public void resetFragment() {
        if (this.isFinished) {
            clearViews();
            this.isFinished = false;
            this.finishing = false;
        }
    }

    public void resumeDelayedFragmentAnimation() {
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout != null) {
            iNavigationLayout.resumeDelayedFragmentAnimation();
        }
    }

    public void saveKeyboardPositionBeforeTransition() {
    }

    public void saveSelfArgs(Bundle bundle) {
    }

    public void setCurrentAccount(int i) {
        if (this.fragmentView != null) {
            throw new IllegalStateException("trying to set current account when fragment UI already created");
        }
        this.currentAccount = i;
    }

    public void setFinishing(boolean z) {
        this.finishing = z;
    }

    public void setFragmentPanTranslationOffset(int i) {
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout != null) {
            iNavigationLayout.setFragmentPanTranslationOffset(i);
        }
    }

    public void setInBubbleMode(boolean z) {
        this.inBubbleMode = z;
    }

    public void setInMenuMode(boolean z) {
        this.inMenuMode = z;
    }

    public void setInPreviewMode(boolean z) {
        this.inPreviewMode = z;
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            boolean z2 = false;
            if (!z && Build.VERSION.SDK_INT >= 21) {
                z2 = true;
            }
            actionBar.setOccupyStatusBar(z2);
        }
    }

    public void setKeyboardHeightFromParent(int i) {
        if (this.sheetsStack != null) {
            for (int i2 = 0; i2 < this.sheetsStack.size(); i2++) {
                AttachedSheet attachedSheet = (AttachedSheet) this.sheetsStack.get(i2);
                if (attachedSheet != null) {
                    attachedSheet.setKeyboardHeightFromParent(i);
                }
            }
        }
    }

    public void setNavigationBarColor(int i) {
        int navigationBarColor;
        Activity parentActivity = getParentActivity();
        if (parentActivity instanceof LaunchActivity) {
            ((LaunchActivity) parentActivity).setNavigationBarColor(i, true);
        } else if (parentActivity != null) {
            Window window = parentActivity.getWindow();
            if (Build.VERSION.SDK_INT >= 26 && window != null) {
                navigationBarColor = window.getNavigationBarColor();
                if (navigationBarColor != i) {
                    window.setNavigationBarColor(i);
                    AndroidUtilities.setLightNavigationBar(window, AndroidUtilities.computePerceivedBrightness(i) >= 0.721f);
                }
            }
        }
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout != null) {
            iNavigationLayout.setNavigationBarColor(i);
        }
    }

    public void setParentActivityTitle(CharSequence charSequence) {
        Activity parentActivity = getParentActivity();
        if (parentActivity != null) {
            parentActivity.setTitle(charSequence);
        }
    }

    public void setParentFragment(BaseFragment baseFragment) {
        setParentLayout(baseFragment.parentLayout);
        this.fragmentView = createView(this.parentLayout.getView().getContext());
    }

    public void setParentLayout(INavigationLayout iNavigationLayout) {
        ViewGroup viewGroup;
        if (this.parentLayout != iNavigationLayout) {
            this.parentLayout = iNavigationLayout;
            boolean z = false;
            this.inBubbleMode = iNavigationLayout != null && iNavigationLayout.isInBubbleMode();
            View view = this.fragmentView;
            if (view != null) {
                ViewGroup viewGroup2 = (ViewGroup) view.getParent();
                if (viewGroup2 != null) {
                    try {
                        onRemoveFromParent();
                        viewGroup2.removeViewInLayout(this.fragmentView);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                INavigationLayout iNavigationLayout2 = this.parentLayout;
                if (iNavigationLayout2 != null && iNavigationLayout2.getView().getContext() != this.fragmentView.getContext()) {
                    this.fragmentView = null;
                    clearSheets();
                }
            }
            if (this.actionBar != null) {
                INavigationLayout iNavigationLayout3 = this.parentLayout;
                if (iNavigationLayout3 != null && iNavigationLayout3.getView().getContext() != this.actionBar.getContext()) {
                    z = true;
                }
                if ((this.actionBar.shouldAddToContainer() || z) && (viewGroup = (ViewGroup) this.actionBar.getParent()) != null) {
                    try {
                        viewGroup.removeViewInLayout(this.actionBar);
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                }
                if (z) {
                    this.actionBar = null;
                }
            }
            INavigationLayout iNavigationLayout4 = this.parentLayout;
            if (iNavigationLayout4 == null || this.actionBar != null) {
                return;
            }
            ActionBar createActionBar = createActionBar(iNavigationLayout4.getView().getContext());
            this.actionBar = createActionBar;
            if (createActionBar != null) {
                createActionBar.parentFragment = this;
            }
        }
    }

    public void setPreviewDelegate(PreviewDelegate previewDelegate) {
        this.previewDelegate = previewDelegate;
    }

    public void setPreviewOpenedProgress(float f) {
    }

    public void setPreviewReplaceProgress(float f) {
    }

    public void setProgressToDrawerOpened(float f) {
    }

    public void setRemovingFromStack(boolean z) {
        this.removingFromStack = z;
    }

    public void setResourceProvider(Theme.ResourcesProvider resourcesProvider) {
        this.resourceProvider = resourcesProvider;
    }

    public void setVisibleDialog(Dialog dialog) {
        this.visibleDialog = dialog;
    }

    public boolean shouldOverrideSlideTransition(boolean z, boolean z2) {
        return false;
    }

    public INavigationLayout[] showAsSheet(BaseFragment baseFragment) {
        return showAsSheet(baseFragment, null);
    }

    public INavigationLayout[] showAsSheet(BaseFragment baseFragment, BottomSheetParams bottomSheetParams) {
        if (getParentActivity() == null) {
            return null;
        }
        INavigationLayout[] iNavigationLayoutArr = {INavigationLayout.CC.newLayout(getParentActivity(), false, new Supplier() {
            @Override
            public final Object get() {
                BottomSheet lambda$showAsSheet$1;
                lambda$showAsSheet$1 = BaseFragment.lambda$showAsSheet$1(r1);
                return lambda$showAsSheet$1;
            }
        })};
        iNavigationLayoutArr[0].setIsSheet(true);
        LaunchActivity.instance.sheetFragmentsStack.add(iNavigationLayoutArr[0]);
        baseFragment.onTransitionAnimationStart(true, false);
        AnonymousClass1 anonymousClass1 = new AnonymousClass1(getParentActivity(), true, baseFragment.getResourceProvider(), bottomSheetParams, iNavigationLayoutArr, baseFragment, r13);
        final BottomSheet[] bottomSheetArr = {anonymousClass1};
        if (bottomSheetParams != null) {
            anonymousClass1.setAllowNestedScroll(bottomSheetParams.allowNestedScroll);
            bottomSheetArr[0].transitionFromRight(bottomSheetParams.transitionFromLeft);
        }
        baseFragment.setParentDialog(bottomSheetArr[0]);
        bottomSheetArr[0].setOpenNoDelay(true);
        bottomSheetArr[0].show();
        return iNavigationLayoutArr;
    }

    public Dialog showDialog(Dialog dialog) {
        return showDialog(dialog, false, null);
    }

    public Dialog showDialog(Dialog dialog, DialogInterface.OnDismissListener onDismissListener) {
        return showDialog(dialog, false, onDismissListener);
    }

    public Dialog showDialog(Dialog dialog, boolean z, final DialogInterface.OnDismissListener onDismissListener) {
        INavigationLayout iNavigationLayout;
        if (dialog != null && (iNavigationLayout = this.parentLayout) != null && !iNavigationLayout.isTransitionAnimationInProgress() && !this.parentLayout.isSwipeInProgress() && (z || !this.parentLayout.checkTransitionAnimation())) {
            ArrayList arrayList = this.sheetsStack;
            if (arrayList != null) {
                for (int size = arrayList.size() - 1; size >= 0; size--) {
                    if (((AttachedSheet) this.sheetsStack.get(size)).isShown() && ((AttachedSheet) this.sheetsStack.get(size)).showDialog(dialog)) {
                        return dialog;
                    }
                }
            }
            try {
                Dialog dialog2 = this.visibleDialog;
                if (dialog2 != null) {
                    dialog2.dismiss();
                    this.visibleDialog = null;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            try {
                this.visibleDialog = dialog;
                dialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public final void onDismiss(DialogInterface dialogInterface) {
                        BaseFragment.this.lambda$showDialog$0(onDismissListener, dialogInterface);
                    }
                });
                this.visibleDialog.show();
                return this.visibleDialog;
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        return null;
    }

    public void startActivityForResult(Intent intent, int i) {
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout != null) {
            iNavigationLayout.startActivityForResult(intent, i);
        }
    }

    public void whenFullyVisible(Runnable runnable) {
        this.fullyVisibleListener = runnable;
    }
}
