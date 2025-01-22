package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.GroupCreateSectionCell;
import org.telegram.ui.Cells.InviteTextCell;
import org.telegram.ui.Cells.InviteUserCell;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.GroupCreateDividerItemDecoration;
import org.telegram.ui.Components.GroupCreateSpan;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.InviteContactsActivity;

public class InviteContactsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, View.OnClickListener {
    private InviteAdapter adapter;
    private int containerHeight;
    private TextView counterTextView;
    private FrameLayout counterView;
    private GroupCreateSpan currentDeletingSpan;
    private GroupCreateDividerItemDecoration decoration;
    private EditTextBoldCursor editText;
    private StickerEmptyView emptyView;
    private int fieldY;
    private boolean ignoreScrollEvent;
    private TextView infoTextView;
    private RecyclerListView listView;
    private ArrayList phoneBookContacts;
    private ScrollView scrollView;
    private boolean searchWas;
    private boolean searching;
    private SpansContainer spansContainer;
    private TextView textView;
    private HashMap selectedContacts = new HashMap();
    private ArrayList allSpans = new ArrayList();

    public class InviteAdapter extends RecyclerListView.SelectionAdapter {
        private Context context;
        private ArrayList searchResult = new ArrayList();
        private ArrayList searchResultNames = new ArrayList();
        private Timer searchTimer;
        private boolean searching;

        public class AnonymousClass1 extends TimerTask {
            final String val$query;

            AnonymousClass1(String str) {
                this.val$query = str;
            }

            public void lambda$run$0(java.lang.String r17) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.InviteContactsActivity.InviteAdapter.AnonymousClass1.lambda$run$0(java.lang.String):void");
            }

            public void lambda$run$1(final String str) {
                Utilities.searchQueue.postRunnable(new Runnable() {
                    @Override
                    public final void run() {
                        InviteContactsActivity.InviteAdapter.AnonymousClass1.this.lambda$run$0(str);
                    }
                });
            }

            @Override
            public void run() {
                try {
                    InviteAdapter.this.searchTimer.cancel();
                    InviteAdapter.this.searchTimer = null;
                } catch (Exception e) {
                    FileLog.e(e);
                }
                final String str = this.val$query;
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        InviteContactsActivity.InviteAdapter.AnonymousClass1.this.lambda$run$1(str);
                    }
                });
            }
        }

        public InviteAdapter(Context context) {
            this.context = context;
        }

        public void lambda$updateSearchResults$0(ArrayList arrayList, ArrayList arrayList2) {
            if (this.searching) {
                this.searchResult = arrayList;
                this.searchResultNames = arrayList2;
                notifyDataSetChanged();
                onSearchFinished();
            }
        }

        public void updateSearchResults(final ArrayList arrayList, final ArrayList arrayList2) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    InviteContactsActivity.InviteAdapter.this.lambda$updateSearchResults$0(arrayList, arrayList2);
                }
            });
        }

        @Override
        public int getItemCount() {
            return this.searching ? this.searchResult.size() : InviteContactsActivity.this.phoneBookContacts.size() + 1;
        }

        @Override
        public int getItemViewType(int i) {
            return (this.searching || i != 0) ? 0 : 1;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            int itemCount = getItemCount();
            if (!this.searching) {
                InviteContactsActivity.this.emptyView.setVisibility(itemCount == 1 ? 0 : 4);
            }
            InviteContactsActivity.this.decoration.setSingle(itemCount == 1);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ContactsController.Contact contact;
            CharSequence charSequence;
            if (viewHolder.getItemViewType() != 0) {
                return;
            }
            InviteUserCell inviteUserCell = (InviteUserCell) viewHolder.itemView;
            if (this.searching) {
                contact = (ContactsController.Contact) this.searchResult.get(i);
                charSequence = (CharSequence) this.searchResultNames.get(i);
            } else {
                contact = (ContactsController.Contact) InviteContactsActivity.this.phoneBookContacts.get(i - 1);
                charSequence = null;
            }
            inviteUserCell.setUser(contact, charSequence);
            inviteUserCell.setChecked(InviteContactsActivity.this.selectedContacts.containsKey(contact.key), false);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            FrameLayout frameLayout;
            if (i != 1) {
                frameLayout = new InviteUserCell(this.context, true);
            } else {
                InviteTextCell inviteTextCell = new InviteTextCell(this.context);
                inviteTextCell.setTextAndIcon(LocaleController.getString(R.string.ShareTelegram), R.drawable.share);
                frameLayout = inviteTextCell;
            }
            return new RecyclerListView.Holder(frameLayout);
        }

        protected abstract void onSearchFinished();

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
            View view = viewHolder.itemView;
            if (view instanceof InviteUserCell) {
                ((InviteUserCell) view).recycle();
            }
        }

        public void searchDialogs(String str) {
            try {
                Timer timer = this.searchTimer;
                if (timer != null) {
                    timer.cancel();
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            if (str == null) {
                this.searchResult.clear();
                this.searchResultNames.clear();
                notifyDataSetChanged();
            } else {
                Timer timer2 = new Timer();
                this.searchTimer = timer2;
                timer2.schedule(new AnonymousClass1(str), 200L, 300L);
            }
        }

        public void setSearching(boolean z) {
            if (this.searching == z) {
                return;
            }
            this.searching = z;
            notifyDataSetChanged();
        }
    }

    public class SpansContainer extends ViewGroup {
        private View addingSpan;
        private boolean animationStarted;
        private ArrayList animators;
        private AnimatorSet currentAnimation;
        private View removingSpan;

        public SpansContainer(Context context) {
            super(context);
            this.animators = new ArrayList();
        }

        public void addSpan(GroupCreateSpan groupCreateSpan) {
            InviteContactsActivity.this.allSpans.add(groupCreateSpan);
            InviteContactsActivity.this.selectedContacts.put(groupCreateSpan.getKey(), groupCreateSpan);
            InviteContactsActivity.this.editText.setHintVisible(false, TextUtils.isEmpty(InviteContactsActivity.this.editText.getText()));
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null) {
                animatorSet.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.currentAnimation = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.addingSpan = null;
                    SpansContainer.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    InviteContactsActivity.this.editText.setAllowDrawCursor(true);
                }
            });
            this.currentAnimation.setDuration(150L);
            this.addingSpan = groupCreateSpan;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleX", 0.01f, 1.0f));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "scaleY", 0.01f, 1.0f));
            this.animators.add(ObjectAnimator.ofFloat(this.addingSpan, "alpha", 0.0f, 1.0f));
            addView(groupCreateSpan);
        }

        @Override
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            int childCount = getChildCount();
            for (int i5 = 0; i5 < childCount; i5++) {
                View childAt = getChildAt(i5);
                childAt.layout(0, 0, childAt.getMeasuredWidth(), childAt.getMeasuredHeight());
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int min;
            boolean z;
            float f;
            float f2;
            int i3;
            char c;
            int childCount = getChildCount();
            int size = View.MeasureSpec.getSize(i);
            float f3 = 32.0f;
            int dp = size - AndroidUtilities.dp(32.0f);
            int dp2 = AndroidUtilities.dp(12.0f);
            int dp3 = AndroidUtilities.dp(12.0f);
            int i4 = 0;
            int i5 = 0;
            int i6 = 0;
            while (i4 < childCount) {
                View childAt = getChildAt(i4);
                if (childAt instanceof GroupCreateSpan) {
                    childAt.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(f3), 1073741824));
                    if (childAt == this.removingSpan || childAt.getMeasuredWidth() + i5 <= dp) {
                        f = 12.0f;
                    } else {
                        f = 12.0f;
                        dp2 += childAt.getMeasuredHeight() + AndroidUtilities.dp(12.0f);
                        i5 = 0;
                    }
                    if (childAt.getMeasuredWidth() + i6 > dp) {
                        dp3 += childAt.getMeasuredHeight() + AndroidUtilities.dp(f);
                        f2 = 16.0f;
                        i6 = 0;
                    } else {
                        f2 = 16.0f;
                    }
                    int dp4 = AndroidUtilities.dp(f2) + i5;
                    if (!this.animationStarted) {
                        View view = this.removingSpan;
                        if (childAt == view) {
                            childAt.setTranslationX(AndroidUtilities.dp(f2) + i6);
                            childAt.setTranslationY(dp3);
                        } else if (view != null) {
                            float f4 = dp4;
                            if (childAt.getTranslationX() != f4) {
                                i3 = 1;
                                c = 0;
                                this.animators.add(ObjectAnimator.ofFloat(childAt, "translationX", f4));
                            } else {
                                i3 = 1;
                                c = 0;
                            }
                            float f5 = dp2;
                            if (childAt.getTranslationY() != f5) {
                                ArrayList arrayList = this.animators;
                                float[] fArr = new float[i3];
                                fArr[c] = f5;
                                arrayList.add(ObjectAnimator.ofFloat(childAt, "translationY", fArr));
                            }
                        } else {
                            childAt.setTranslationX(dp4);
                            childAt.setTranslationY(dp2);
                        }
                    }
                    if (childAt != this.removingSpan) {
                        i5 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                    }
                    i6 += childAt.getMeasuredWidth() + AndroidUtilities.dp(9.0f);
                }
                i4++;
                f3 = 32.0f;
            }
            if (AndroidUtilities.isTablet()) {
                min = AndroidUtilities.dp(366.0f);
            } else {
                Point point = AndroidUtilities.displaySize;
                min = Math.min(point.x, point.y) - AndroidUtilities.dp(164.0f);
            }
            int i7 = min / 3;
            if (dp - i5 < i7) {
                dp2 += AndroidUtilities.dp(44.0f);
                i5 = 0;
            }
            if (dp - i6 < i7) {
                dp3 += AndroidUtilities.dp(44.0f);
            }
            InviteContactsActivity.this.editText.measure(View.MeasureSpec.makeMeasureSpec(dp - i5, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(32.0f), 1073741824));
            if (!this.animationStarted) {
                int dp5 = dp3 + AndroidUtilities.dp(44.0f);
                int dp6 = i5 + AndroidUtilities.dp(16.0f);
                InviteContactsActivity.this.fieldY = dp2;
                if (this.currentAnimation != null) {
                    int dp7 = dp2 + AndroidUtilities.dp(44.0f);
                    if (InviteContactsActivity.this.containerHeight != dp7) {
                        this.animators.add(ObjectAnimator.ofInt(InviteContactsActivity.this, "containerHeight", dp7));
                    }
                    float f6 = dp6;
                    if (InviteContactsActivity.this.editText.getTranslationX() != f6) {
                        this.animators.add(ObjectAnimator.ofFloat(InviteContactsActivity.this.editText, "translationX", f6));
                    }
                    if (InviteContactsActivity.this.editText.getTranslationY() != InviteContactsActivity.this.fieldY) {
                        z = false;
                        this.animators.add(ObjectAnimator.ofFloat(InviteContactsActivity.this.editText, "translationY", InviteContactsActivity.this.fieldY));
                    } else {
                        z = false;
                    }
                    InviteContactsActivity.this.editText.setAllowDrawCursor(z);
                    this.currentAnimation.playTogether(this.animators);
                    this.currentAnimation.start();
                    this.animationStarted = true;
                } else {
                    InviteContactsActivity.this.containerHeight = dp5;
                    InviteContactsActivity.this.editText.setTranslationX(dp6);
                    InviteContactsActivity.this.editText.setTranslationY(InviteContactsActivity.this.fieldY);
                }
            } else if (this.currentAnimation != null && !InviteContactsActivity.this.ignoreScrollEvent && this.removingSpan == null) {
                InviteContactsActivity.this.editText.bringPointIntoView(InviteContactsActivity.this.editText.getSelectionStart());
            }
            setMeasuredDimension(size, InviteContactsActivity.this.containerHeight);
        }

        public void removeSpan(final GroupCreateSpan groupCreateSpan) {
            InviteContactsActivity.this.ignoreScrollEvent = true;
            InviteContactsActivity.this.selectedContacts.remove(groupCreateSpan.getKey());
            InviteContactsActivity.this.allSpans.remove(groupCreateSpan);
            groupCreateSpan.setOnClickListener(null);
            AnimatorSet animatorSet = this.currentAnimation;
            if (animatorSet != null && animatorSet.isRunning()) {
                this.currentAnimation.setupEndValues();
                this.currentAnimation.cancel();
            }
            this.animationStarted = false;
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.currentAnimation = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    SpansContainer.this.removeView(groupCreateSpan);
                    SpansContainer.this.removingSpan = null;
                    SpansContainer.this.currentAnimation = null;
                    SpansContainer.this.animationStarted = false;
                    InviteContactsActivity.this.editText.setAllowDrawCursor(true);
                    if (InviteContactsActivity.this.allSpans.isEmpty()) {
                        InviteContactsActivity.this.editText.setHintVisible(true, true);
                    }
                }
            });
            this.currentAnimation.setDuration(150L);
            this.removingSpan = groupCreateSpan;
            this.animators.clear();
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleX", 1.0f, 0.01f));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "scaleY", 1.0f, 0.01f));
            this.animators.add(ObjectAnimator.ofFloat(this.removingSpan, "alpha", 1.0f, 0.0f));
            requestLayout();
        }
    }

    public void checkVisibleRows() {
        InviteUserCell inviteUserCell;
        ContactsController.Contact contact;
        int childCount = this.listView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.listView.getChildAt(i);
            if ((childAt instanceof InviteUserCell) && (contact = (inviteUserCell = (InviteUserCell) childAt).getContact()) != null) {
                inviteUserCell.setChecked(this.selectedContacts.containsKey(contact.key), true);
            }
        }
    }

    public void closeSearch() {
        this.searching = false;
        this.searchWas = false;
        this.adapter.setSearching(false);
        this.adapter.searchDialogs(null);
        this.listView.setFastScrollVisible(true);
        this.listView.setVerticalScrollBarEnabled(false);
        this.emptyView.showProgress(false);
        this.emptyView.setStickerType(0);
        this.emptyView.title.setText(LocaleController.getString(R.string.NoContacts));
        this.emptyView.subtitle.setText("");
    }

    private void fetchContacts() {
        ArrayList arrayList = new ArrayList(ContactsController.getInstance(this.currentAccount).phoneBookContacts);
        this.phoneBookContacts = arrayList;
        Collections.sort(arrayList, new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$fetchContacts$2;
                lambda$fetchContacts$2 = InviteContactsActivity.lambda$fetchContacts$2((ContactsController.Contact) obj, (ContactsController.Contact) obj2);
                return lambda$fetchContacts$2;
            }
        });
        StickerEmptyView stickerEmptyView = this.emptyView;
        if (stickerEmptyView != null) {
            stickerEmptyView.showProgress(false);
        }
        InviteAdapter inviteAdapter = this.adapter;
        if (inviteAdapter != null) {
            inviteAdapter.notifyDataSetChanged();
        }
    }

    public void lambda$createView$0(View view, int i) {
        InviteUserCell inviteUserCell;
        ContactsController.Contact contact;
        if (i == 0 && !this.searching) {
            try {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType("text/plain");
                String inviteText = ContactsController.getInstance(this.currentAccount).getInviteText(0);
                intent.putExtra("android.intent.extra.TEXT", inviteText);
                getParentActivity().startActivityForResult(Intent.createChooser(intent, inviteText), 500);
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        if ((view instanceof InviteUserCell) && (contact = (inviteUserCell = (InviteUserCell) view).getContact()) != null) {
            boolean containsKey = this.selectedContacts.containsKey(contact.key);
            if (containsKey) {
                this.spansContainer.removeSpan((GroupCreateSpan) this.selectedContacts.get(contact.key));
            } else {
                GroupCreateSpan groupCreateSpan = new GroupCreateSpan(this.editText.getContext(), contact);
                this.spansContainer.addSpan(groupCreateSpan);
                groupCreateSpan.setOnClickListener(this);
            }
            updateHint();
            if (this.searching || this.searchWas) {
                AndroidUtilities.showKeyboard(this.editText);
            } else {
                inviteUserCell.setChecked(!containsKey, true);
            }
            if (this.editText.length() > 0) {
                this.editText.setText((CharSequence) null);
            }
        }
    }

    public void lambda$createView$1(View view) {
        try {
            StringBuilder sb = new StringBuilder();
            int i = 0;
            for (int i2 = 0; i2 < this.allSpans.size(); i2++) {
                ContactsController.Contact contact = ((GroupCreateSpan) this.allSpans.get(i2)).getContact();
                if (sb.length() != 0) {
                    sb.append(';');
                }
                sb.append(contact.phones.get(0));
                if (i2 == 0 && this.allSpans.size() == 1) {
                    i = contact.imported;
                }
            }
            Intent intent = new Intent("android.intent.action.SENDTO", Uri.parse("smsto:" + sb.toString()));
            intent.putExtra("sms_body", ContactsController.getInstance(this.currentAccount).getInviteText(i));
            getParentActivity().startActivityForResult(intent, 500);
        } catch (Exception e) {
            FileLog.e(e);
        }
        lambda$onBackPressed$323();
    }

    public static int lambda$fetchContacts$2(ContactsController.Contact contact, ContactsController.Contact contact2) {
        int i = contact.imported;
        int i2 = contact2.imported;
        if (i > i2) {
            return -1;
        }
        return i < i2 ? 1 : 0;
    }

    public void lambda$getThemeDescriptions$3() {
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            int childCount = recyclerListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.listView.getChildAt(i);
                if (childAt instanceof InviteUserCell) {
                    ((InviteUserCell) childAt).update(0);
                }
            }
        }
    }

    public void updateHint() {
        if (this.selectedContacts.isEmpty()) {
            this.infoTextView.setVisibility(0);
            this.counterView.setVisibility(4);
        } else {
            this.infoTextView.setVisibility(4);
            this.counterView.setVisibility(0);
            this.counterTextView.setText(String.format("%d", Integer.valueOf(this.selectedContacts.size())));
        }
    }

    @Override
    public View createView(Context context) {
        this.searching = false;
        this.searchWas = false;
        this.allSpans.clear();
        this.selectedContacts.clear();
        this.currentDeletingSpan = null;
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(LocaleController.getString(R.string.InviteFriends));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    InviteContactsActivity.this.lambda$onBackPressed$323();
                }
            }
        });
        ViewGroup viewGroup = new ViewGroup(context) {
            @Override
            protected boolean drawChild(Canvas canvas, View view, long j) {
                boolean drawChild = super.drawChild(canvas, view, j);
                if (view == InviteContactsActivity.this.listView || view == InviteContactsActivity.this.emptyView) {
                    ((BaseFragment) InviteContactsActivity.this).parentLayout.drawHeaderShadow(canvas, InviteContactsActivity.this.scrollView.getMeasuredHeight());
                }
                return drawChild;
            }

            @Override
            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                InviteContactsActivity.this.scrollView.layout(0, 0, InviteContactsActivity.this.scrollView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight());
                InviteContactsActivity.this.listView.layout(0, InviteContactsActivity.this.scrollView.getMeasuredHeight(), InviteContactsActivity.this.listView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight() + InviteContactsActivity.this.listView.getMeasuredHeight());
                InviteContactsActivity.this.emptyView.layout(0, InviteContactsActivity.this.scrollView.getMeasuredHeight() + (InviteContactsActivity.this.searching ? 0 : AndroidUtilities.dp(72.0f)), InviteContactsActivity.this.emptyView.getMeasuredWidth(), InviteContactsActivity.this.scrollView.getMeasuredHeight() + InviteContactsActivity.this.emptyView.getMeasuredHeight());
                int i5 = i4 - i2;
                int measuredHeight = i5 - InviteContactsActivity.this.infoTextView.getMeasuredHeight();
                InviteContactsActivity.this.infoTextView.layout(0, measuredHeight, InviteContactsActivity.this.infoTextView.getMeasuredWidth(), InviteContactsActivity.this.infoTextView.getMeasuredHeight() + measuredHeight);
                int measuredHeight2 = i5 - InviteContactsActivity.this.counterView.getMeasuredHeight();
                InviteContactsActivity.this.counterView.layout(0, measuredHeight2, InviteContactsActivity.this.counterView.getMeasuredWidth(), InviteContactsActivity.this.counterView.getMeasuredHeight() + measuredHeight2);
            }

            @Override
            protected void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i);
                int size2 = View.MeasureSpec.getSize(i2);
                setMeasuredDimension(size, size2);
                int dp = AndroidUtilities.dp((AndroidUtilities.isTablet() || size2 > size) ? 144.0f : 56.0f);
                InviteContactsActivity.this.infoTextView.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
                InviteContactsActivity.this.counterView.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
                int measuredHeight = (InviteContactsActivity.this.infoTextView.getVisibility() == 0 ? InviteContactsActivity.this.infoTextView : InviteContactsActivity.this.counterView).getMeasuredHeight();
                InviteContactsActivity.this.scrollView.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec(dp, Integer.MIN_VALUE));
                InviteContactsActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec((size2 - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - measuredHeight, 1073741824));
                InviteContactsActivity.this.emptyView.measure(View.MeasureSpec.makeMeasureSpec(size, 1073741824), View.MeasureSpec.makeMeasureSpec((size2 - InviteContactsActivity.this.scrollView.getMeasuredHeight()) - measuredHeight, 1073741824));
            }
        };
        this.fragmentView = viewGroup;
        ScrollView scrollView = new ScrollView(context) {
            @Override
            public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
                if (InviteContactsActivity.this.ignoreScrollEvent) {
                    InviteContactsActivity.this.ignoreScrollEvent = false;
                    return false;
                }
                rect.offset(view.getLeft() - view.getScrollX(), view.getTop() - view.getScrollY());
                rect.top += InviteContactsActivity.this.fieldY + AndroidUtilities.dp(20.0f);
                rect.bottom += InviteContactsActivity.this.fieldY + AndroidUtilities.dp(50.0f);
                return super.requestChildRectangleOnScreen(view, rect, z);
            }
        };
        this.scrollView = scrollView;
        scrollView.setVerticalScrollBarEnabled(false);
        AndroidUtilities.setScrollViewEdgeEffectColor(this.scrollView, Theme.getColor(Theme.key_windowBackgroundWhite));
        viewGroup.addView(this.scrollView);
        SpansContainer spansContainer = new SpansContainer(context);
        this.spansContainer = spansContainer;
        this.scrollView.addView(spansContainer, LayoutHelper.createFrame(-1, -2.0f));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) {
            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (InviteContactsActivity.this.currentDeletingSpan != null) {
                    InviteContactsActivity.this.currentDeletingSpan.cancelDeleteAnimation();
                    InviteContactsActivity.this.currentDeletingSpan = null;
                }
                if (motionEvent.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.editText = editTextBoldCursor;
        editTextBoldCursor.setTextSize(1, 18.0f);
        this.editText.setHintColor(Theme.getColor(Theme.key_groupcreate_hintText));
        this.editText.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        this.editText.setCursorColor(Theme.getColor(Theme.key_groupcreate_cursor));
        this.editText.setCursorWidth(1.5f);
        this.editText.setInputType(655536);
        this.editText.setSingleLine(true);
        this.editText.setBackgroundDrawable(null);
        this.editText.setVerticalScrollBarEnabled(false);
        this.editText.setHorizontalScrollBarEnabled(false);
        this.editText.setTextIsSelectable(false);
        this.editText.setPadding(0, 0, 0, 0);
        this.editText.setImeOptions(268435462);
        this.editText.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        this.spansContainer.addView(this.editText);
        this.editText.setHintText(LocaleController.getString(R.string.SearchFriends));
        this.editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }
        });
        this.editText.setOnKeyListener(new View.OnKeyListener() {
            private boolean wasEmpty;

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction() == 0) {
                    this.wasEmpty = InviteContactsActivity.this.editText.length() == 0;
                } else if (keyEvent.getAction() == 1 && this.wasEmpty && !InviteContactsActivity.this.allSpans.isEmpty()) {
                    InviteContactsActivity.this.spansContainer.removeSpan((GroupCreateSpan) InviteContactsActivity.this.allSpans.get(InviteContactsActivity.this.allSpans.size() - 1));
                    InviteContactsActivity.this.updateHint();
                    InviteContactsActivity.this.checkVisibleRows();
                    return true;
                }
                return false;
            }
        });
        this.editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                if (InviteContactsActivity.this.editText.length() == 0) {
                    InviteContactsActivity.this.closeSearch();
                    return;
                }
                InviteContactsActivity.this.searching = true;
                InviteContactsActivity.this.searchWas = true;
                InviteContactsActivity.this.adapter.setSearching(true);
                InviteContactsActivity.this.adapter.searchDialogs(InviteContactsActivity.this.editText.getText().toString());
                InviteContactsActivity.this.listView.setFastScrollVisible(false);
                InviteContactsActivity.this.listView.setVerticalScrollBarEnabled(true);
                InviteContactsActivity.this.emptyView.showProgress(true);
                InviteContactsActivity.this.emptyView.setStickerType(1);
                InviteContactsActivity.this.emptyView.title.setText(LocaleController.getString(R.string.NoResult));
                InviteContactsActivity.this.emptyView.subtitle.setText(LocaleController.getString(R.string.SearchEmptyViewFilteredSubtitle2));
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
        FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
        flickerLoadingView.setViewType(6);
        flickerLoadingView.showDate(false);
        StickerEmptyView stickerEmptyView = new StickerEmptyView(context, flickerLoadingView, 0);
        this.emptyView = stickerEmptyView;
        stickerEmptyView.addView(flickerLoadingView, 0);
        this.emptyView.setAnimateLayoutChange(true);
        this.emptyView.title.setText(LocaleController.getString(R.string.NoContacts));
        this.emptyView.subtitle.setText("");
        this.emptyView.showProgress(ContactsController.getInstance(this.currentAccount).isLoadingContacts());
        viewGroup.addView(this.emptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 1, false);
        this.adapter = new InviteAdapter(context) {
            @Override
            protected void onSearchFinished() {
                InviteContactsActivity.this.emptyView.showProgress(false);
            }
        };
        RecyclerListView recyclerListView = new RecyclerListView(context) {
            @Override
            public void setPadding(int i, int i2, int i3, int i4) {
                super.setPadding(i, i2, i3, i4);
                if (InviteContactsActivity.this.emptyView != null) {
                    InviteContactsActivity.this.emptyView.setPadding(i, i2, i3, i4);
                }
            }
        };
        this.listView = recyclerListView;
        recyclerListView.setEmptyView(this.emptyView);
        this.listView.setAdapter(this.adapter);
        this.listView.setLayoutManager(linearLayoutManager);
        this.listView.setVerticalScrollBarEnabled(true);
        this.listView.setVerticalScrollbarPosition(LocaleController.isRTL ? 1 : 2);
        RecyclerListView recyclerListView2 = this.listView;
        GroupCreateDividerItemDecoration groupCreateDividerItemDecoration = new GroupCreateDividerItemDecoration();
        this.decoration = groupCreateDividerItemDecoration;
        recyclerListView2.addItemDecoration(groupCreateDividerItemDecoration);
        viewGroup.addView(this.listView);
        this.listView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i) {
                InviteContactsActivity.this.lambda$createView$0(view, i);
            }
        });
        this.listView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i) {
                if (i == 1) {
                    AndroidUtilities.hideKeyboard(InviteContactsActivity.this.editText);
                }
            }
        });
        TextView textView = new TextView(context);
        this.infoTextView = textView;
        int i = Theme.key_contacts_inviteBackground;
        textView.setBackgroundColor(Theme.getColor(i));
        TextView textView2 = this.infoTextView;
        int i2 = Theme.key_contacts_inviteText;
        textView2.setTextColor(Theme.getColor(i2));
        this.infoTextView.setGravity(17);
        this.infoTextView.setText(LocaleController.getString(R.string.InviteFriendsHelp));
        this.infoTextView.setTextSize(1, 13.0f);
        this.infoTextView.setTypeface(AndroidUtilities.bold());
        this.infoTextView.setPadding(AndroidUtilities.dp(17.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(17.0f), AndroidUtilities.dp(9.0f));
        viewGroup.addView(this.infoTextView, LayoutHelper.createFrame(-1, -2, 83));
        FrameLayout frameLayout = new FrameLayout(context);
        this.counterView = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(i));
        this.counterView.setVisibility(4);
        viewGroup.addView(this.counterView, LayoutHelper.createFrame(-1, 48, 83));
        this.counterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                InviteContactsActivity.this.lambda$createView$1(view);
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        this.counterView.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 17));
        TextView textView3 = new TextView(context);
        this.counterTextView = textView3;
        textView3.setTypeface(AndroidUtilities.bold());
        this.counterTextView.setTextSize(1, 14.0f);
        this.counterTextView.setTextColor(Theme.getColor(i));
        this.counterTextView.setGravity(17);
        this.counterTextView.setBackgroundDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(10.0f), Theme.getColor(i2)));
        this.counterTextView.setMinWidth(AndroidUtilities.dp(20.0f));
        this.counterTextView.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(1.0f));
        linearLayout.addView(this.counterTextView, LayoutHelper.createLinear(-2, 20, 16, 0, 0, 10, 0));
        TextView textView4 = new TextView(context);
        this.textView = textView4;
        textView4.setTextSize(1, 14.0f);
        this.textView.setTextColor(Theme.getColor(i2));
        this.textView.setGravity(17);
        this.textView.setCompoundDrawablePadding(AndroidUtilities.dp(8.0f));
        this.textView.setText(LocaleController.getString(R.string.InviteToTelegram).toUpperCase());
        this.textView.setTypeface(AndroidUtilities.bold());
        linearLayout.addView(this.textView, LayoutHelper.createLinear(-2, -2, 16));
        updateHint();
        this.adapter.notifyDataSetChanged();
        return this.fragmentView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        StickerEmptyView stickerEmptyView;
        if (i == NotificationCenter.contactsImported) {
            fetchContacts();
        } else {
            if (i != NotificationCenter.contactsDidLoad || (stickerEmptyView = this.emptyView) == null) {
                return;
            }
            stickerEmptyView.showProgress(false);
        }
    }

    @Override
    public ArrayList getThemeDescriptions() {
        ArrayList arrayList = new ArrayList();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
            @Override
            public final void didSetColor() {
                InviteContactsActivity.this.lambda$getThemeDescriptions$3();
            }

            @Override
            public void onAnimationProgress(float f) {
                ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
            }
        };
        View view = this.fragmentView;
        int i = ThemeDescription.FLAG_BACKGROUND;
        int i2 = Theme.key_windowBackgroundWhite;
        arrayList.add(new ThemeDescription(view, i, null, null, null, null, i2));
        ActionBar actionBar = this.actionBar;
        int i3 = ThemeDescription.FLAG_BACKGROUND;
        int i4 = Theme.key_actionBarDefault;
        arrayList.add(new ThemeDescription(actionBar, i3, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i4));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.scrollView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, i2));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_SELECTOR, null, null, null, null, Theme.key_listSelector));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollActive));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollInactive));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_FASTSCROLL, null, null, null, null, Theme.key_fastScrollText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{View.class}, Theme.dividerPaint, null, null, Theme.key_divider));
        EditTextBoldCursor editTextBoldCursor = this.editText;
        int i5 = ThemeDescription.FLAG_TEXTCOLOR;
        int i6 = Theme.key_windowBackgroundWhiteBlackText;
        arrayList.add(new ThemeDescription(editTextBoldCursor, i5, null, null, null, null, i6));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_groupcreate_hintText));
        arrayList.add(new ThemeDescription(this.editText, ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, Theme.key_groupcreate_cursor));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_CELLBACKGROUNDCOLOR, new Class[]{GroupCreateSectionCell.class}, null, null, null, Theme.key_graySection));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{GroupCreateSectionCell.class}, new String[]{"drawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_groupcreate_sectionShadow));
        int i7 = Theme.key_groupcreate_sectionText;
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{GroupCreateSectionCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i7));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkbox));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{InviteUserCell.class}, new String[]{"checkBox"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_checkboxCheck));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{InviteUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlueText));
        arrayList.add(new ThemeDescription(this.listView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{InviteUserCell.class}, new String[]{"statusTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{InviteUserCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundCyan));
        int i8 = Theme.key_avatar_backgroundBlue;
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, i8));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_avatar_backgroundPink));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{InviteTextCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i6));
        arrayList.add(new ThemeDescription(this.listView, 0, new Class[]{InviteTextCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayIcon));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanBackground));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanText));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, Theme.key_groupcreate_spanDelete));
        arrayList.add(new ThemeDescription(this.spansContainer, 0, new Class[]{GroupCreateSpan.class}, null, null, null, i8));
        TextView textView = this.infoTextView;
        int i9 = ThemeDescription.FLAG_TEXTCOLOR;
        int i10 = Theme.key_contacts_inviteText;
        arrayList.add(new ThemeDescription(textView, i9, null, null, null, null, i10));
        TextView textView2 = this.infoTextView;
        int i11 = ThemeDescription.FLAG_BACKGROUND;
        int i12 = Theme.key_contacts_inviteBackground;
        arrayList.add(new ThemeDescription(textView2, i11, null, null, null, null, i12));
        arrayList.add(new ThemeDescription(this.counterView, ThemeDescription.FLAG_BACKGROUND, null, null, null, null, i12));
        arrayList.add(new ThemeDescription(this.counterTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i12));
        arrayList.add(new ThemeDescription(this.textView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i10));
        arrayList.add(new ThemeDescription(this.counterTextView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, i10));
        return arrayList;
    }

    @Override
    public void onClick(View view) {
        GroupCreateSpan groupCreateSpan = (GroupCreateSpan) view;
        if (groupCreateSpan.isDeleting()) {
            this.currentDeletingSpan = null;
            this.spansContainer.removeSpan(groupCreateSpan);
            updateHint();
            checkVisibleRows();
            return;
        }
        GroupCreateSpan groupCreateSpan2 = this.currentDeletingSpan;
        if (groupCreateSpan2 != null) {
            groupCreateSpan2.cancelDeleteAnimation();
        }
        this.currentDeletingSpan = groupCreateSpan;
        groupCreateSpan.startDeleteAnimation();
    }

    @Override
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsImported);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.contactsDidLoad);
        fetchContacts();
        if (!UserConfig.getInstance(this.currentAccount).contactsReimported) {
            ContactsController.getInstance(this.currentAccount).forceImportContacts();
            UserConfig.getInstance(this.currentAccount).contactsReimported = true;
            UserConfig.getInstance(this.currentAccount).saveConfig(false);
        }
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsImported);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.contactsDidLoad);
    }

    @Override
    public void onResume() {
        super.onResume();
        EditTextBoldCursor editTextBoldCursor = this.editText;
        if (editTextBoldCursor != null) {
            editTextBoldCursor.requestFocus();
        }
    }
}
