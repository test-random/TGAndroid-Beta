package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.util.Property;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import org.telegram.messenger.HashtagSearchController;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

public abstract class HashtagHistoryView extends FrameLayout {
    private UniversalAdapter adapter;
    private AnimatorSet animation;
    private int currentAccount;
    private ImageView emptyImage;
    private TextView emptyText;
    public FrameLayout emptyView;
    private ArrayList history;
    private UniversalRecyclerView recyclerView;
    private Theme.ResourcesProvider resourcesProvider;

    public HashtagHistoryView(Context context, Theme.ResourcesProvider resourcesProvider, int i) {
        super(context);
        this.currentAccount = i;
        this.resourcesProvider = resourcesProvider;
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider));
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(context, i, 0, new Utilities.Callback2() {
            @Override
            public final void run(Object obj, Object obj2) {
                HashtagHistoryView.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() {
            @Override
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                HashtagHistoryView.this.onClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, new Utilities.Callback5Return() {
            @Override
            public final Object run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                boolean onLongClick;
                onLongClick = HashtagHistoryView.this.onLongClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
                return Boolean.valueOf(onLongClick);
            }
        }, resourcesProvider);
        this.recyclerView = universalRecyclerView;
        universalRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int i2, int i3) {
                super.onScrolled(recyclerView, i2, i3);
                HashtagHistoryView.this.onScrolled(recyclerView, i2, i3);
            }
        });
        this.adapter = (UniversalAdapter) this.recyclerView.getAdapter();
        addView(this.recyclerView, -1, -1);
        this.emptyView = new FrameLayout(context);
        ImageView imageView = new ImageView(context);
        this.emptyImage = imageView;
        int i2 = Theme.key_windowBackgroundWhiteGrayIcon;
        imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2, resourcesProvider), PorterDuff.Mode.MULTIPLY));
        this.emptyImage.setScaleType(ImageView.ScaleType.CENTER);
        this.emptyImage.setImageResource(R.drawable.large_hashtags);
        this.emptyView.addView(this.emptyImage, LayoutHelper.createFrame(56, 56, 49));
        TextView textView = new TextView(context);
        this.emptyText = textView;
        textView.setTextColor(Theme.getColor(i2, resourcesProvider));
        this.emptyText.setText(LocaleController.getString(R.string.HashtagSearchPlaceholder));
        this.emptyText.setGravity(17);
        this.emptyView.addView(this.emptyText, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 56.0f, 0.0f, 0.0f));
        addView(this.emptyView, LayoutHelper.createFrame(210, -2, 17));
        this.recyclerView.setEmptyView(this.emptyView);
    }

    public void fillItems(ArrayList arrayList, UniversalAdapter universalAdapter) {
        ArrayList arrayList2 = new ArrayList(0);
        this.history = arrayList2;
        arrayList2.addAll(HashtagSearchController.getInstance(this.currentAccount).history);
        if (this.history.isEmpty()) {
            return;
        }
        for (int i = 0; i < this.history.size(); i++) {
            String str = (String) this.history.get(i);
            if (str.startsWith("#") || str.startsWith("$")) {
                arrayList.add(UItem.asButton(i + 1, str.startsWith("$") ? R.drawable.menu_cashtag : R.drawable.menu_hashtag, str.substring(1)));
            }
        }
        arrayList.add(UItem.asButton(0, R.drawable.msg_clear_recent, LocaleController.getString(R.string.ClearHistory)));
    }

    public void lambda$onLongClick$0(String str, AlertDialog alertDialog, int i) {
        HashtagSearchController.getInstance(this.currentAccount).removeHashtagFromHistory(str);
        update();
    }

    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        int i2 = uItem.id;
        if (i2 != 0) {
            onClick((String) this.history.get(i2 - 1));
        } else {
            HashtagSearchController.getInstance(this.currentAccount).clearHistory();
            update();
        }
    }

    public boolean onLongClick(UItem uItem, View view, int i, float f, float f2) {
        int i2 = uItem.id;
        if (i2 == 0) {
            return false;
        }
        final String str = (String) this.history.get(i2 - 1);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), this.resourcesProvider);
        builder.setTitle(LocaleController.getString(R.string.ClearSearchSingleAlertTitle));
        builder.setMessage(LocaleController.formatString(R.string.ClearSearchSingleHashtagAlertText, str));
        builder.setPositiveButton(LocaleController.getString(R.string.ClearSearchRemove), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i3) {
                HashtagHistoryView.this.lambda$onLongClick$0(str, alertDialog, i3);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        builder.create().show();
        return true;
    }

    public boolean isShowing() {
        return getTag() != null;
    }

    protected abstract void onClick(String str);

    public void onScrolled(RecyclerView recyclerView, int i, int i2) {
    }

    public void show(final boolean z) {
        if (z == isShowing()) {
            return;
        }
        AnimatorSet animatorSet = this.animation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.animation = null;
        }
        if (z) {
            setVisibility(0);
        }
        setTag(z ? 1 : null);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.animation = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this, (Property<HashtagHistoryView, Float>) View.ALPHA, z ? 1.0f : 0.0f));
        this.animation.setInterpolator(CubicBezierInterpolator.EASE_IN);
        this.animation.setDuration(180L);
        this.animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animator) {
                if (animator.equals(HashtagHistoryView.this.animation)) {
                    HashtagHistoryView.this.animation = null;
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (animator.equals(HashtagHistoryView.this.animation)) {
                    HashtagHistoryView.this.animation = null;
                    if (z) {
                        return;
                    }
                    HashtagHistoryView.this.setVisibility(8);
                }
            }
        });
        this.animation.start();
    }

    public void update() {
        this.adapter.update(true);
    }
}
