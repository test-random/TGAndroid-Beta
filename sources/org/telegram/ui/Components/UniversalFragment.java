package org.telegram.ui.Components;

import android.content.Context;
import android.view.View;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;

public abstract class UniversalFragment extends BaseFragment {
    public UniversalRecyclerView listView;
    private int savedScrollOffset;
    private int savedScrollPosition = -1;

    public void applyScrolledPosition() {
        int i = this.savedScrollPosition;
        if (i >= 0) {
            UniversalRecyclerView universalRecyclerView = this.listView;
            universalRecyclerView.layoutManager.scrollToPositionWithOffset(i, this.savedScrollOffset - universalRecyclerView.getPaddingTop());
        }
    }

    @Override
    public View createView(Context context) {
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(true);
        this.actionBar.setTitle(getTitle());
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i) {
                if (i == -1) {
                    UniversalFragment.this.lambda$onBackPressed$319();
                }
            }
        });
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) {
            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), 1073741824));
            }
        };
        sizeNotifierFrameLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(this, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                UniversalFragment.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() {
            @Override
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                UniversalFragment.this.onClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, new Utilities.Callback5Return() {
            @Override
            public final Object run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                return Boolean.valueOf(UniversalFragment.this.onLongClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue()));
            }
        }) {
            @Override
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                UniversalFragment.this.savedScrollPosition = -1;
            }

            @Override
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
            }
        };
        this.listView = universalRecyclerView;
        sizeNotifierFrameLayout.addView(universalRecyclerView, LayoutHelper.createFrame(-1, -1.0f));
        this.fragmentView = sizeNotifierFrameLayout;
        return sizeNotifierFrameLayout;
    }

    public abstract void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter);

    protected abstract CharSequence getTitle();

    public abstract void onClick(UItem uItem, View view, int i, float f, float f2);

    public abstract boolean onLongClick(UItem uItem, View view, int i, float f, float f2);

    public void saveScrollPosition() {
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView == null || universalRecyclerView.getChildCount() <= 0) {
            return;
        }
        View view = null;
        int i = Integer.MAX_VALUE;
        int i2 = -1;
        for (int i3 = 0; i3 < this.listView.getChildCount(); i3++) {
            UniversalRecyclerView universalRecyclerView2 = this.listView;
            int childAdapterPosition = universalRecyclerView2.getChildAdapterPosition(universalRecyclerView2.getChildAt(i3));
            View childAt = this.listView.getChildAt(i3);
            if (childAdapterPosition != -1 && childAt.getTop() < i) {
                i = childAt.getTop();
                i2 = childAdapterPosition;
                view = childAt;
            }
        }
        if (view != null) {
            this.savedScrollPosition = i2;
            int top = view.getTop();
            this.savedScrollOffset = top;
            if (this.savedScrollPosition == 0 && top > AndroidUtilities.dp(88.0f)) {
                this.savedScrollOffset = AndroidUtilities.dp(88.0f);
            }
            this.listView.layoutManager.scrollToPositionWithOffset(i2, view.getTop() - this.listView.getPaddingTop());
        }
    }
}
