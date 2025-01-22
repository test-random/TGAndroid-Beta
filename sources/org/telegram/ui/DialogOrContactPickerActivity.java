package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Property;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrollSlidingTextTabStrip;
import org.telegram.ui.ContactsActivity;
import org.telegram.ui.DialogsActivity;

public class DialogOrContactPickerActivity extends BaseFragment {
    private static final Interpolator interpolator = new Interpolator() {
        @Override
        public final float getInterpolation(float f) {
            float lambda$static$0;
            lambda$static$0 = DialogOrContactPickerActivity.lambda$static$0(f);
            return lambda$static$0;
        }
    };
    private boolean animatingForward;
    private boolean backAnimation;
    private ContactsActivity contactsActivity;
    private DialogsActivity dialogsActivity;
    private int maximumVelocity;
    private ScrollSlidingTextTabStrip scrollSlidingTextTabStrip;
    private ActionBarMenuItem searchItem;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    private Paint backgroundPaint = new Paint();
    private ViewPage[] viewPages = new ViewPage[2];
    private boolean swipeBackEnabled = true;

    public static class ViewPage extends FrameLayout {
        private ActionBar actionBar;
        private FrameLayout fragmentView;
        private RecyclerListView listView;
        private RecyclerListView listView2;
        private BaseFragment parentFragment;
        private int selectedType;

        public ViewPage(Context context) {
            super(context);
        }
    }

    public DialogOrContactPickerActivity() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("onlySelect", true);
        bundle.putBoolean("checkCanWrite", false);
        bundle.putBoolean("resetDelegate", false);
        bundle.putInt("dialogsType", 9);
        DialogsActivity dialogsActivity = new DialogsActivity(bundle);
        this.dialogsActivity = dialogsActivity;
        dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() {
            @Override
            public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
                boolean lambda$new$1;
                lambda$new$1 = DialogOrContactPickerActivity.this.lambda$new$1(dialogsActivity2, arrayList, charSequence, z, z2, i, topicsFragment);
                return lambda$new$1;
            }
        });
        this.dialogsActivity.onFragmentCreate();
        Bundle bundle2 = new Bundle();
        bundle2.putBoolean("onlyUsers", true);
        bundle2.putBoolean("destroyAfterSelect", true);
        bundle2.putBoolean("returnAsResult", true);
        bundle2.putBoolean("disableSections", true);
        bundle2.putBoolean("needFinishFragment", false);
        bundle2.putBoolean("resetDelegate", false);
        bundle2.putBoolean("allowSelf", false);
        ContactsActivity contactsActivity = new ContactsActivity(bundle2);
        this.contactsActivity = contactsActivity;
        contactsActivity.setDelegate(new ContactsActivity.ContactsActivityDelegate() {
            @Override
            public final void didSelectContact(TLRPC.User user, String str, ContactsActivity contactsActivity2) {
                DialogOrContactPickerActivity.this.lambda$new$2(user, str, contactsActivity2);
            }
        });
        this.contactsActivity.onFragmentCreate();
    }

    public boolean lambda$new$1(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, TopicsFragment topicsFragment) {
        if (arrayList.isEmpty()) {
            return true;
        }
        long j = ((MessagesStorage.TopicKey) arrayList.get(0)).dialogId;
        if (!DialogObject.isUserDialog(j)) {
            return true;
        }
        showBlockAlert(getMessagesController().getUser(Long.valueOf(j)));
        return true;
    }

    public void lambda$new$2(TLRPC.User user, String str, ContactsActivity contactsActivity) {
        showBlockAlert(user);
    }

    public void lambda$showBlockAlert$3(TLRPC.User user, AlertDialog alertDialog, int i) {
        int i2;
        if (MessagesController.isSupportUser(user)) {
            i2 = R.string.ErrorOccurred;
        } else {
            MessagesController.getInstance(this.currentAccount).blockPeer(user.id);
            i2 = R.string.UserBlocked;
        }
        AlertsCreator.showSimpleToast(this, LocaleController.getString(i2));
        lambda$onBackPressed$323();
    }

    public static float lambda$static$0(float f) {
        float f2 = f - 1.0f;
        return (f2 * f2 * f2 * f2 * f2) + 1.0f;
    }

    public void setScrollY(float f) {
        this.actionBar.setTranslationY(f);
        int i = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                this.fragmentView.invalidate();
                return;
            }
            int i2 = (int) f;
            viewPageArr[i].listView.setPinnedSectionOffsetY(i2);
            if (this.viewPages[i].listView2 != null) {
                this.viewPages[i].listView2.setPinnedSectionOffsetY(i2);
            }
            i++;
        }
    }

    private void showBlockAlert(final TLRPC.User user) {
        if (user == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.BlockUser));
        builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("AreYouSureBlockContact2", R.string.AreYouSureBlockContact2, ContactsController.formatName(user.first_name, user.last_name))));
        builder.setPositiveButton(LocaleController.getString(R.string.BlockContact), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                DialogOrContactPickerActivity.this.lambda$showBlockAlert$3(user, alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        AlertDialog create = builder.create();
        showDialog(create);
        TextView textView = (TextView) create.getButton(-1);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    public void switchToCurrentSelectedMode(boolean z) {
        int i = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                break;
            }
            viewPageArr[i].listView.stopScroll();
            if (this.viewPages[i].listView2 != null) {
                this.viewPages[i].listView2.stopScroll();
            }
            i++;
        }
        int i2 = 0;
        while (i2 < 2) {
            ViewPage[] viewPageArr2 = this.viewPages;
            RecyclerListView recyclerListView = i2 == 0 ? viewPageArr2[z ? 1 : 0].listView : viewPageArr2[z ? 1 : 0].listView2;
            if (recyclerListView != null) {
                recyclerListView.getAdapter();
                recyclerListView.setPinnedHeaderShadowDrawable(null);
                if (this.actionBar.getTranslationY() != 0.0f) {
                    ((LinearLayoutManager) recyclerListView.getLayoutManager()).scrollToPositionWithOffset(0, (int) this.actionBar.getTranslationY());
                }
            }
            i2++;
        }
    }

    private void updateTabs() {
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = this.scrollSlidingTextTabStrip;
        if (scrollSlidingTextTabStrip == null) {
            return;
        }
        scrollSlidingTextTabStrip.addTextTab(0, LocaleController.getString(R.string.BlockUserChatsTitle));
        this.scrollSlidingTextTabStrip.addTextTab(1, LocaleController.getString(R.string.BlockUserContactsTitle));
        this.scrollSlidingTextTabStrip.setVisibility(0);
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        int currentTabId = this.scrollSlidingTextTabStrip.getCurrentTabId();
        if (currentTabId >= 0) {
            this.viewPages[0].selectedType = currentTabId;
        }
        this.scrollSlidingTextTabStrip.finishAddingTabs();
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setTitle(LocaleController.getString(R.string.BlockUserMultiTitle));
        if (AndroidUtilities.isTablet()) {
            this.actionBar.setOccupyStatusBar(false);
        }
        this.actionBar.setExtraHeight(AndroidUtilities.dp(44.0f));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setClipContent(true);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    DialogOrContactPickerActivity.this.lambda$onBackPressed$323();
                }
            }
        });
        this.hasOwnBackground = true;
        ActionBarMenuItem actionBarMenuItemSearchListener = this.actionBar.createMenu().addItem(0, R.drawable.ic_ab_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() {
            @Override
            public void onSearchCollapse() {
                DialogOrContactPickerActivity.this.dialogsActivity.getActionBar().closeSearchField(false);
                DialogOrContactPickerActivity.this.contactsActivity.getActionBar().closeSearchField(false);
            }

            @Override
            public void onSearchExpand() {
                DialogOrContactPickerActivity.this.dialogsActivity.getActionBar().openSearchField("", false);
                DialogOrContactPickerActivity.this.contactsActivity.getActionBar().openSearchField("", false);
                DialogOrContactPickerActivity.this.searchItem.getSearchField().requestFocus();
            }

            @Override
            public void onTextChanged(EditText editText) {
                DialogOrContactPickerActivity.this.dialogsActivity.getActionBar().setSearchFieldText(editText.getText().toString());
                DialogOrContactPickerActivity.this.contactsActivity.getActionBar().setSearchFieldText(editText.getText().toString());
            }
        });
        this.searchItem = actionBarMenuItemSearchListener;
        actionBarMenuItemSearchListener.setSearchFieldHint(LocaleController.getString(R.string.Search));
        ScrollSlidingTextTabStrip scrollSlidingTextTabStrip = new ScrollSlidingTextTabStrip(context);
        this.scrollSlidingTextTabStrip = scrollSlidingTextTabStrip;
        scrollSlidingTextTabStrip.setUseSameWidth(true);
        this.actionBar.addView(this.scrollSlidingTextTabStrip, LayoutHelper.createFrame(-1, 44, 83));
        this.scrollSlidingTextTabStrip.setDelegate(new ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate() {
            @Override
            public void onPageScrolled(float f) {
                ViewPage viewPage;
                float measuredWidth;
                float measuredWidth2;
                if (f != 1.0f || DialogOrContactPickerActivity.this.viewPages[1].getVisibility() == 0) {
                    if (DialogOrContactPickerActivity.this.animatingForward) {
                        DialogOrContactPickerActivity.this.viewPages[0].setTranslationX((-f) * DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth());
                        viewPage = DialogOrContactPickerActivity.this.viewPages[1];
                        measuredWidth = DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth();
                        measuredWidth2 = DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() * f;
                    } else {
                        DialogOrContactPickerActivity.this.viewPages[0].setTranslationX(DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() * f);
                        viewPage = DialogOrContactPickerActivity.this.viewPages[1];
                        measuredWidth = DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() * f;
                        measuredWidth2 = DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth();
                    }
                    viewPage.setTranslationX(measuredWidth - measuredWidth2);
                    if (f == 1.0f) {
                        ViewPage viewPage2 = DialogOrContactPickerActivity.this.viewPages[0];
                        DialogOrContactPickerActivity.this.viewPages[0] = DialogOrContactPickerActivity.this.viewPages[1];
                        DialogOrContactPickerActivity.this.viewPages[1] = viewPage2;
                        DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                    }
                }
            }

            @Override
            public void onPageSelected(int i, boolean z) {
                if (DialogOrContactPickerActivity.this.viewPages[0].selectedType == i) {
                    return;
                }
                DialogOrContactPickerActivity dialogOrContactPickerActivity = DialogOrContactPickerActivity.this;
                dialogOrContactPickerActivity.swipeBackEnabled = i == dialogOrContactPickerActivity.scrollSlidingTextTabStrip.getFirstTabId();
                DialogOrContactPickerActivity.this.viewPages[1].selectedType = i;
                DialogOrContactPickerActivity.this.viewPages[1].setVisibility(0);
                DialogOrContactPickerActivity.this.switchToCurrentSelectedMode(true);
                DialogOrContactPickerActivity.this.animatingForward = z;
            }

            @Override
            public void onSamePageSelected() {
                ScrollSlidingTextTabStrip.ScrollSlidingTabStripDelegate.CC.$default$onSamePageSelected(this);
            }
        });
        this.maximumVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        FrameLayout frameLayout = new FrameLayout(context) {
            private boolean globalIgnoreLayout;
            private boolean maybeStartTracking;
            private boolean startedTracking;
            private int startedTrackingPointerId;
            private int startedTrackingX;
            private int startedTrackingY;
            private VelocityTracker velocityTracker;

            private boolean prepareForMoving(MotionEvent motionEvent, boolean z) {
                ViewPage viewPage;
                int i;
                int nextPageId = DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.getNextPageId(z);
                if (nextPageId < 0) {
                    return false;
                }
                getParent().requestDisallowInterceptTouchEvent(true);
                this.maybeStartTracking = false;
                this.startedTracking = true;
                this.startedTrackingX = (int) motionEvent.getX();
                ((BaseFragment) DialogOrContactPickerActivity.this).actionBar.setEnabled(false);
                DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(false);
                DialogOrContactPickerActivity.this.viewPages[1].selectedType = nextPageId;
                DialogOrContactPickerActivity.this.viewPages[1].setVisibility(0);
                DialogOrContactPickerActivity.this.animatingForward = z;
                DialogOrContactPickerActivity.this.switchToCurrentSelectedMode(true);
                ViewPage[] viewPageArr = DialogOrContactPickerActivity.this.viewPages;
                if (z) {
                    viewPage = viewPageArr[1];
                    i = DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth();
                } else {
                    viewPage = viewPageArr[1];
                    i = -DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth();
                }
                viewPage.setTranslationX(i);
                return true;
            }

            public boolean checkTabsAnimationInProgress() {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.DialogOrContactPickerActivity.AnonymousClass4.checkTabsAnimationInProgress():boolean");
            }

            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (((BaseFragment) DialogOrContactPickerActivity.this).parentLayout != null) {
                    ((BaseFragment) DialogOrContactPickerActivity.this).parentLayout.drawHeaderShadow(canvas, ((BaseFragment) DialogOrContactPickerActivity.this).actionBar.getMeasuredHeight() + ((int) ((BaseFragment) DialogOrContactPickerActivity.this).actionBar.getTranslationY()));
                }
            }

            @Override
            public void forceHasOverlappingRendering(boolean z) {
                super.forceHasOverlappingRendering(z);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                DialogOrContactPickerActivity.this.backgroundPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                canvas.drawRect(0.0f, ((BaseFragment) DialogOrContactPickerActivity.this).actionBar.getMeasuredHeight() + ((BaseFragment) DialogOrContactPickerActivity.this).actionBar.getTranslationY(), getMeasuredWidth(), getMeasuredHeight(), DialogOrContactPickerActivity.this.backgroundPaint);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                return checkTabsAnimationInProgress() || DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.isAnimatingIndicator() || onTouchEvent(motionEvent);
            }

            @Override
            protected void onMeasure(int i, int i2) {
                setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
                measureChildWithMargins(((BaseFragment) DialogOrContactPickerActivity.this).actionBar, i, 0, i2, 0);
                int measuredHeight = ((BaseFragment) DialogOrContactPickerActivity.this).actionBar.getMeasuredHeight();
                this.globalIgnoreLayout = true;
                for (int i3 = 0; i3 < DialogOrContactPickerActivity.this.viewPages.length; i3++) {
                    if (DialogOrContactPickerActivity.this.viewPages[i3] != null) {
                        if (DialogOrContactPickerActivity.this.viewPages[i3].listView != null) {
                            DialogOrContactPickerActivity.this.viewPages[i3].listView.setPadding(0, measuredHeight, 0, 0);
                        }
                        if (DialogOrContactPickerActivity.this.viewPages[i3].listView2 != null) {
                            DialogOrContactPickerActivity.this.viewPages[i3].listView2.setPadding(0, measuredHeight, 0, 0);
                        }
                    }
                }
                this.globalIgnoreLayout = false;
                int childCount = getChildCount();
                for (int i4 = 0; i4 < childCount; i4++) {
                    View childAt = getChildAt(i4);
                    if (childAt != null && childAt.getVisibility() != 8 && childAt != ((BaseFragment) DialogOrContactPickerActivity.this).actionBar) {
                        measureChildWithMargins(childAt, i, 0, i2, 0);
                    }
                }
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                float f;
                float f2;
                float measuredWidth;
                ViewPage viewPage;
                int measuredWidth2;
                if (((BaseFragment) DialogOrContactPickerActivity.this).parentLayout.checkTransitionAnimation() || checkTabsAnimationInProgress()) {
                    return false;
                }
                if (motionEvent != null) {
                    if (this.velocityTracker == null) {
                        this.velocityTracker = VelocityTracker.obtain();
                    }
                    this.velocityTracker.addMovement(motionEvent);
                }
                if (motionEvent != null && motionEvent.getAction() == 0 && !this.startedTracking && !this.maybeStartTracking) {
                    this.startedTrackingPointerId = motionEvent.getPointerId(0);
                    this.maybeStartTracking = true;
                    this.startedTrackingX = (int) motionEvent.getX();
                    this.startedTrackingY = (int) motionEvent.getY();
                    this.velocityTracker.clear();
                } else if (motionEvent != null && motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.startedTrackingPointerId) {
                    int x = (int) (motionEvent.getX() - this.startedTrackingX);
                    int abs = Math.abs(((int) motionEvent.getY()) - this.startedTrackingY);
                    if (this.startedTracking && ((DialogOrContactPickerActivity.this.animatingForward && x > 0) || (!DialogOrContactPickerActivity.this.animatingForward && x < 0))) {
                        if (!prepareForMoving(motionEvent, x < 0)) {
                            this.maybeStartTracking = true;
                            this.startedTracking = false;
                            DialogOrContactPickerActivity.this.viewPages[0].setTranslationX(0.0f);
                            DialogOrContactPickerActivity.this.viewPages[1].setTranslationX(DialogOrContactPickerActivity.this.animatingForward ? DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() : -DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth());
                            DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[1].selectedType, 0.0f);
                        }
                    }
                    if (!this.maybeStartTracking || this.startedTracking) {
                        if (this.startedTracking) {
                            DialogOrContactPickerActivity.this.viewPages[0].setTranslationX(x);
                            if (DialogOrContactPickerActivity.this.animatingForward) {
                                viewPage = DialogOrContactPickerActivity.this.viewPages[1];
                                measuredWidth2 = DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() + x;
                            } else {
                                viewPage = DialogOrContactPickerActivity.this.viewPages[1];
                                measuredWidth2 = x - DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth();
                            }
                            viewPage.setTranslationX(measuredWidth2);
                            DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[1].selectedType, Math.abs(x) / DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth());
                        }
                    } else if (Math.abs(x) >= AndroidUtilities.getPixelsInCM(0.3f, true) && Math.abs(x) > abs) {
                        prepareForMoving(motionEvent, x < 0);
                    }
                } else if (motionEvent == null || (motionEvent.getPointerId(0) == this.startedTrackingPointerId && (motionEvent.getAction() == 3 || motionEvent.getAction() == 1 || motionEvent.getAction() == 6))) {
                    this.velocityTracker.computeCurrentVelocity(1000, DialogOrContactPickerActivity.this.maximumVelocity);
                    if (motionEvent == null || motionEvent.getAction() == 3) {
                        f = 0.0f;
                        f2 = 0.0f;
                    } else {
                        f = this.velocityTracker.getXVelocity();
                        f2 = this.velocityTracker.getYVelocity();
                        if (!this.startedTracking && Math.abs(f) >= 3000.0f && Math.abs(f) > Math.abs(f2)) {
                            prepareForMoving(motionEvent, f < 0.0f);
                        }
                    }
                    if (this.startedTracking) {
                        float x2 = DialogOrContactPickerActivity.this.viewPages[0].getX();
                        DialogOrContactPickerActivity.this.tabsAnimation = new AnimatorSet();
                        DialogOrContactPickerActivity.this.backAnimation = Math.abs(x2) < ((float) DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()) / 3.0f && (Math.abs(f) < 3500.0f || Math.abs(f) < Math.abs(f2));
                        if (DialogOrContactPickerActivity.this.backAnimation) {
                            measuredWidth = Math.abs(x2);
                            if (DialogOrContactPickerActivity.this.animatingForward) {
                                AnimatorSet animatorSet = DialogOrContactPickerActivity.this.tabsAnimation;
                                ViewPage viewPage2 = DialogOrContactPickerActivity.this.viewPages[0];
                                Property property = View.TRANSLATION_X;
                                animatorSet.playTogether(ObjectAnimator.ofFloat(viewPage2, (Property<ViewPage, Float>) property, 0.0f), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], (Property<ViewPage, Float>) property, DialogOrContactPickerActivity.this.viewPages[1].getMeasuredWidth()));
                            } else {
                                AnimatorSet animatorSet2 = DialogOrContactPickerActivity.this.tabsAnimation;
                                ViewPage viewPage3 = DialogOrContactPickerActivity.this.viewPages[0];
                                Property property2 = View.TRANSLATION_X;
                                animatorSet2.playTogether(ObjectAnimator.ofFloat(viewPage3, (Property<ViewPage, Float>) property2, 0.0f), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], (Property<ViewPage, Float>) property2, -DialogOrContactPickerActivity.this.viewPages[1].getMeasuredWidth()));
                            }
                        } else {
                            measuredWidth = DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth() - Math.abs(x2);
                            if (DialogOrContactPickerActivity.this.animatingForward) {
                                AnimatorSet animatorSet3 = DialogOrContactPickerActivity.this.tabsAnimation;
                                ViewPage viewPage4 = DialogOrContactPickerActivity.this.viewPages[0];
                                Property property3 = View.TRANSLATION_X;
                                animatorSet3.playTogether(ObjectAnimator.ofFloat(viewPage4, (Property<ViewPage, Float>) property3, -DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], (Property<ViewPage, Float>) property3, 0.0f));
                            } else {
                                AnimatorSet animatorSet4 = DialogOrContactPickerActivity.this.tabsAnimation;
                                ViewPage viewPage5 = DialogOrContactPickerActivity.this.viewPages[0];
                                Property property4 = View.TRANSLATION_X;
                                animatorSet4.playTogether(ObjectAnimator.ofFloat(viewPage5, (Property<ViewPage, Float>) property4, DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth()), ObjectAnimator.ofFloat(DialogOrContactPickerActivity.this.viewPages[1], (Property<ViewPage, Float>) property4, 0.0f));
                            }
                        }
                        DialogOrContactPickerActivity.this.tabsAnimation.setInterpolator(DialogOrContactPickerActivity.interpolator);
                        int measuredWidth3 = getMeasuredWidth();
                        float f3 = measuredWidth3 / 2;
                        float distanceInfluenceForSnapDuration = f3 + (AndroidUtilities.distanceInfluenceForSnapDuration(Math.min(1.0f, (measuredWidth * 1.0f) / measuredWidth3)) * f3);
                        DialogOrContactPickerActivity.this.tabsAnimation.setDuration(Math.max(150, Math.min(Math.abs(f) > 0.0f ? Math.round(Math.abs(distanceInfluenceForSnapDuration / r4) * 1000.0f) * 4 : (int) (((measuredWidth / getMeasuredWidth()) + 1.0f) * 100.0f), 600)));
                        DialogOrContactPickerActivity.this.tabsAnimation.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animator) {
                                DialogOrContactPickerActivity.this.tabsAnimation = null;
                                if (DialogOrContactPickerActivity.this.backAnimation) {
                                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                                } else {
                                    ViewPage viewPage6 = DialogOrContactPickerActivity.this.viewPages[0];
                                    DialogOrContactPickerActivity.this.viewPages[0] = DialogOrContactPickerActivity.this.viewPages[1];
                                    DialogOrContactPickerActivity.this.viewPages[1] = viewPage6;
                                    DialogOrContactPickerActivity.this.viewPages[1].setVisibility(8);
                                    DialogOrContactPickerActivity dialogOrContactPickerActivity = DialogOrContactPickerActivity.this;
                                    dialogOrContactPickerActivity.swipeBackEnabled = dialogOrContactPickerActivity.viewPages[0].selectedType == DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.getFirstTabId();
                                    DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[0].selectedType, 1.0f);
                                }
                                DialogOrContactPickerActivity.this.tabsAnimationInProgress = false;
                                AnonymousClass4.this.maybeStartTracking = false;
                                AnonymousClass4.this.startedTracking = false;
                                ((BaseFragment) DialogOrContactPickerActivity.this).actionBar.setEnabled(true);
                                DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                            }
                        });
                        DialogOrContactPickerActivity.this.tabsAnimation.start();
                        DialogOrContactPickerActivity.this.tabsAnimationInProgress = true;
                        this.startedTracking = false;
                    } else {
                        this.maybeStartTracking = false;
                        ((BaseFragment) DialogOrContactPickerActivity.this).actionBar.setEnabled(true);
                        DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.setEnabled(true);
                    }
                    VelocityTracker velocityTracker = this.velocityTracker;
                    if (velocityTracker != null) {
                        velocityTracker.recycle();
                        this.velocityTracker = null;
                    }
                }
                return this.startedTracking;
            }

            @Override
            public void requestLayout() {
                if (this.globalIgnoreLayout) {
                    return;
                }
                super.requestLayout();
            }
        };
        this.fragmentView = frameLayout;
        frameLayout.setWillNotDraw(false);
        this.dialogsActivity.setParentFragment(this);
        this.contactsActivity.setParentFragment(this);
        int i = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                break;
            }
            viewPageArr[i] = new ViewPage(context) {
                @Override
                public void setTranslationX(float f) {
                    super.setTranslationX(f);
                    if (DialogOrContactPickerActivity.this.tabsAnimationInProgress && DialogOrContactPickerActivity.this.viewPages[0] == this) {
                        DialogOrContactPickerActivity.this.scrollSlidingTextTabStrip.selectTabWithId(DialogOrContactPickerActivity.this.viewPages[1].selectedType, Math.abs(DialogOrContactPickerActivity.this.viewPages[0].getTranslationX()) / DialogOrContactPickerActivity.this.viewPages[0].getMeasuredWidth());
                    }
                }
            };
            frameLayout.addView(this.viewPages[i], LayoutHelper.createFrame(-1, -1.0f));
            if (i == 0) {
                this.viewPages[i].parentFragment = this.dialogsActivity;
                this.viewPages[i].listView = this.dialogsActivity.getListView();
                this.viewPages[i].listView2 = this.dialogsActivity.getSearchListView();
            } else if (i == 1) {
                this.viewPages[i].parentFragment = this.contactsActivity;
                this.viewPages[i].listView = this.contactsActivity.getListView();
                this.viewPages[i].setVisibility(8);
            }
            this.viewPages[i].listView.setScrollingTouchSlop(1);
            ViewPage viewPage = this.viewPages[i];
            viewPage.fragmentView = (FrameLayout) viewPage.parentFragment.getFragmentView();
            ViewPage viewPage2 = this.viewPages[i];
            viewPage2.actionBar = viewPage2.parentFragment.getActionBar();
            ViewPage viewPage3 = this.viewPages[i];
            viewPage3.addView(viewPage3.fragmentView, LayoutHelper.createFrame(-1, -1.0f));
            AndroidUtilities.removeFromParent(this.viewPages[i].actionBar);
            ViewPage viewPage4 = this.viewPages[i];
            viewPage4.addView(viewPage4.actionBar, LayoutHelper.createFrame(-1, -2.0f));
            this.viewPages[i].actionBar.setVisibility(8);
            int i2 = 0;
            while (i2 < 2) {
                ViewPage[] viewPageArr2 = this.viewPages;
                RecyclerListView recyclerListView = i2 == 0 ? viewPageArr2[i].listView : viewPageArr2[i].listView2;
                if (recyclerListView != null) {
                    recyclerListView.setClipToPadding(false);
                    final RecyclerView.OnScrollListener onScrollListener = recyclerListView.getOnScrollListener();
                    recyclerListView.setOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(RecyclerView recyclerView, int i3) {
                            onScrollListener.onScrollStateChanged(recyclerView, i3);
                            if (i3 != 1) {
                                int i4 = (int) (-((BaseFragment) DialogOrContactPickerActivity.this).actionBar.getTranslationY());
                                int currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
                                if (i4 == 0 || i4 == currentActionBarHeight) {
                                    return;
                                }
                                if (i4 < currentActionBarHeight / 2) {
                                    int i5 = -i4;
                                    DialogOrContactPickerActivity.this.viewPages[0].listView.smoothScrollBy(0, i5);
                                    if (DialogOrContactPickerActivity.this.viewPages[0].listView2 != null) {
                                        DialogOrContactPickerActivity.this.viewPages[0].listView2.smoothScrollBy(0, i5);
                                        return;
                                    }
                                    return;
                                }
                                int i6 = currentActionBarHeight - i4;
                                DialogOrContactPickerActivity.this.viewPages[0].listView.smoothScrollBy(0, i6);
                                if (DialogOrContactPickerActivity.this.viewPages[0].listView2 != null) {
                                    DialogOrContactPickerActivity.this.viewPages[0].listView2.smoothScrollBy(0, i6);
                                }
                            }
                        }

                        @Override
                        public void onScrolled(RecyclerView recyclerView, int i3, int i4) {
                            onScrollListener.onScrolled(recyclerView, i3, i4);
                            if (recyclerView == DialogOrContactPickerActivity.this.viewPages[0].listView || recyclerView == DialogOrContactPickerActivity.this.viewPages[0].listView2) {
                                float translationY = ((BaseFragment) DialogOrContactPickerActivity.this).actionBar.getTranslationY();
                                float f = translationY - i4;
                                if (f < (-ActionBar.getCurrentActionBarHeight())) {
                                    f = -ActionBar.getCurrentActionBarHeight();
                                } else if (f > 0.0f) {
                                    f = 0.0f;
                                }
                                if (f != translationY) {
                                    DialogOrContactPickerActivity.this.setScrollY(f);
                                }
                            }
                        }
                    });
                }
                i2++;
            }
            i++;
        }
        frameLayout.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        updateTabs();
        switchToCurrentSelectedMode(false);
        this.swipeBackEnabled = this.scrollSlidingTextTabStrip.getCurrentTabId() == this.scrollSlidingTextTabStrip.getFirstTabId();
        return this.fragmentView;
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, Theme.key_actionBarDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_actionBarTabActiveText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{TextView.class}, null, null, null, Theme.key_actionBarTabUnactiveText));
        arrayList.add(new ThemeDescription(this.scrollSlidingTextTabStrip.getTabsContainer(), ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{TextView.class}, null, null, null, Theme.key_actionBarTabLine));
        arrayList.add(new ThemeDescription(null, 0, null, null, new Drawable[]{this.scrollSlidingTextTabStrip.getSelectorDrawable()}, null, Theme.key_actionBarTabSelector));
        arrayList.addAll(this.dialogsActivity.getThemeDescriptions());
        arrayList.addAll(this.contactsActivity.getThemeDescriptions());
        return arrayList;
    }

    @Override
    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return this.swipeBackEnabled;
    }

    @Override
    public void onFragmentDestroy() {
        DialogsActivity dialogsActivity = this.dialogsActivity;
        if (dialogsActivity != null) {
            dialogsActivity.onFragmentDestroy();
        }
        ContactsActivity contactsActivity = this.contactsActivity;
        if (contactsActivity != null) {
            contactsActivity.onFragmentDestroy();
        }
        super.onFragmentDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
        DialogsActivity dialogsActivity = this.dialogsActivity;
        if (dialogsActivity != null) {
            dialogsActivity.onPause();
        }
        ContactsActivity contactsActivity = this.contactsActivity;
        if (contactsActivity != null) {
            contactsActivity.onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        DialogsActivity dialogsActivity = this.dialogsActivity;
        if (dialogsActivity != null) {
            dialogsActivity.onResume();
        }
        ContactsActivity contactsActivity = this.contactsActivity;
        if (contactsActivity != null) {
            contactsActivity.onResume();
        }
    }
}
