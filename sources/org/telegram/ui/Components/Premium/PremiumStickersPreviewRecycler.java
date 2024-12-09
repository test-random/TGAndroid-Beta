package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumStickersPreviewRecycler;
import org.telegram.ui.Components.RecyclerListView;

public abstract class PremiumStickersPreviewRecycler extends RecyclerListView implements NotificationCenter.NotificationCenterDelegate, PagerHeaderView {
    boolean autoPlayEnabled;
    Runnable autoScrollRunnable;
    private boolean checkEffect;
    Comparator comparator;
    private final int currentAccount;
    boolean firstDraw;
    boolean firstMeasure;
    boolean haptic;
    boolean hasSelectedView;
    CubicBezierInterpolator interpolator;
    boolean isVisible;
    LinearLayoutManager layoutManager;
    View oldSelectedView;
    private final ArrayList premiumStickers;
    int selectStickerOnNextLayout;
    private int size;
    ArrayList sortedView;

    private class Adapter extends RecyclerListView.SelectionAdapter {
        private Adapter() {
        }

        @Override
        public int getItemCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return false;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            if (PremiumStickersPreviewRecycler.this.premiumStickers.isEmpty()) {
                return;
            }
            StickerView stickerView = (StickerView) viewHolder.itemView;
            stickerView.setSticker((TLRPC.Document) PremiumStickersPreviewRecycler.this.premiumStickers.get(i % PremiumStickersPreviewRecycler.this.premiumStickers.size()));
            stickerView.setDrawImage(!PremiumStickersPreviewRecycler.this.hasSelectedView, false, false);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            StickerView stickerView = new StickerView(viewGroup.getContext());
            stickerView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(stickerView);
        }
    }

    public class StickerView extends FrameLayout {
        boolean animateImage;
        private float animateImageProgress;
        ImageReceiver centerImage;
        TLRPC.Document document;
        boolean drawEffect;
        ImageReceiver effectImage;
        private float effectProgress;
        public float progress;
        boolean update;
        View view;

        public StickerView(Context context) {
            super(context);
            this.animateImage = true;
            this.view = new View(context) {
                @Override
                public void draw(android.graphics.Canvas r11) {
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Premium.PremiumStickersPreviewRecycler.StickerView.AnonymousClass1.draw(android.graphics.Canvas):void");
                }
            };
            this.centerImage = new ImageReceiver(this.view);
            this.effectImage = new ImageReceiver(this.view);
            this.centerImage.setAllowStartAnimation(false);
            this.effectImage.setAllowStartAnimation(false);
            setClipChildren(false);
            addView(this.view, LayoutHelper.createFrame(-1, -2, 21));
        }

        static float access$416(StickerView stickerView, float f) {
            float f2 = stickerView.effectProgress + f;
            stickerView.effectProgress = f2;
            return f2;
        }

        static float access$424(StickerView stickerView, float f) {
            float f2 = stickerView.effectProgress - f;
            stickerView.effectProgress = f2;
            return f2;
        }

        static float access$516(StickerView stickerView, float f) {
            float f2 = stickerView.animateImageProgress + f;
            stickerView.animateImageProgress = f2;
            return f2;
        }

        static float access$524(StickerView stickerView, float f) {
            float f2 = stickerView.animateImageProgress - f;
            stickerView.animateImageProgress = f2;
            return f2;
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.centerImage.onAttachedToWindow();
            this.effectImage.onAttachedToWindow();
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.centerImage.onDetachedFromWindow();
            this.effectImage.onDetachedFromWindow();
        }

        @Override
        protected void onMeasure(int i, int i2) {
            int i3 = (int) (PremiumStickersPreviewRecycler.this.size * 0.6f);
            ViewGroup.LayoutParams layoutParams = this.view.getLayoutParams();
            ViewGroup.LayoutParams layoutParams2 = this.view.getLayoutParams();
            int dp = i3 - AndroidUtilities.dp(16.0f);
            layoutParams2.height = dp;
            layoutParams.width = dp;
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec((int) (i3 * 0.7f), 1073741824));
        }

        public void setDrawImage(boolean z, boolean z2, boolean z3) {
            if (this.drawEffect != z2) {
                this.drawEffect = z2;
                if (!z3) {
                    this.effectProgress = z2 ? 1.0f : 0.0f;
                }
                this.view.invalidate();
            }
            if (this.animateImage != z) {
                this.animateImage = z;
                if (!z3) {
                    this.animateImageProgress = z ? 1.0f : 0.0f;
                }
                this.view.invalidate();
            }
        }

        public void setSticker(TLRPC.Document document) {
            this.document = document;
            this.update = true;
        }
    }

    public PremiumStickersPreviewRecycler(Context context, int i) {
        super(context);
        this.premiumStickers = new ArrayList();
        this.firstMeasure = true;
        this.firstDraw = true;
        this.autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                PremiumStickersPreviewRecycler premiumStickersPreviewRecycler = PremiumStickersPreviewRecycler.this;
                if (premiumStickersPreviewRecycler.autoPlayEnabled) {
                    if (!premiumStickersPreviewRecycler.sortedView.isEmpty()) {
                        ArrayList arrayList = PremiumStickersPreviewRecycler.this.sortedView;
                        int childAdapterPosition = PremiumStickersPreviewRecycler.this.getChildAdapterPosition((StickerView) arrayList.get(arrayList.size() - 1));
                        if (childAdapterPosition >= 0) {
                            View findViewByPosition = PremiumStickersPreviewRecycler.this.layoutManager.findViewByPosition(childAdapterPosition + 1);
                            if (findViewByPosition != null) {
                                PremiumStickersPreviewRecycler premiumStickersPreviewRecycler2 = PremiumStickersPreviewRecycler.this;
                                premiumStickersPreviewRecycler2.haptic = false;
                                premiumStickersPreviewRecycler2.drawEffectForView(findViewByPosition, true);
                                PremiumStickersPreviewRecycler.this.smoothScrollBy(0, findViewByPosition.getTop() - ((PremiumStickersPreviewRecycler.this.getMeasuredHeight() - findViewByPosition.getMeasuredHeight()) / 2), AndroidUtilities.overshootInterpolator);
                            }
                        }
                    }
                    PremiumStickersPreviewRecycler.this.scheduleAutoScroll();
                }
            }
        };
        this.interpolator = new CubicBezierInterpolator(0.0f, 0.5f, 0.5f, 1.0f);
        this.sortedView = new ArrayList();
        this.comparator = new Comparator() {
            @Override
            public final int compare(Object obj, Object obj2) {
                int lambda$new$0;
                lambda$new$0 = PremiumStickersPreviewRecycler.lambda$new$0((PremiumStickersPreviewRecycler.StickerView) obj, (PremiumStickersPreviewRecycler.StickerView) obj2);
                return lambda$new$0;
            }
        };
        this.selectStickerOnNextLayout = -1;
        this.currentAccount = i;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        this.layoutManager = linearLayoutManager;
        setLayoutManager(linearLayoutManager);
        setAdapter(new Adapter());
        setClipChildren(false);
        setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int i2) {
                super.onScrollStateChanged(recyclerView, i2);
                if (i2 == 1) {
                    PremiumStickersPreviewRecycler.this.haptic = true;
                }
                if (i2 != 0) {
                    AndroidUtilities.cancelRunOnUIThread(PremiumStickersPreviewRecycler.this.autoScrollRunnable);
                    return;
                }
                StickerView stickerView = null;
                for (int i3 = 0; i3 < recyclerView.getChildCount(); i3++) {
                    StickerView stickerView2 = (StickerView) PremiumStickersPreviewRecycler.this.getChildAt(i3);
                    if (stickerView == null || stickerView2.progress > stickerView.progress) {
                        stickerView = stickerView2;
                    }
                }
                if (stickerView != null) {
                    PremiumStickersPreviewRecycler.this.drawEffectForView(stickerView, true);
                    PremiumStickersPreviewRecycler premiumStickersPreviewRecycler = PremiumStickersPreviewRecycler.this;
                    premiumStickersPreviewRecycler.haptic = false;
                    premiumStickersPreviewRecycler.smoothScrollBy(0, stickerView.getTop() - ((PremiumStickersPreviewRecycler.this.getMeasuredHeight() - stickerView.getMeasuredHeight()) / 2), AndroidUtilities.overshootInterpolator);
                }
                PremiumStickersPreviewRecycler.this.scheduleAutoScroll();
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int i2, int i3) {
                super.onScrolled(recyclerView, i2, i3);
                if (recyclerView.getScrollState() == 1) {
                    PremiumStickersPreviewRecycler.this.drawEffectForView(null, true);
                }
                PremiumStickersPreviewRecycler.this.invalidate();
            }
        });
        setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view, int i2) {
                PremiumStickersPreviewRecycler.this.lambda$new$1(view, i2);
            }
        });
        MediaDataController.getInstance(i).preloadPremiumPreviewStickers();
        setStickers();
    }

    public void drawEffectForView(View view, boolean z) {
        this.hasSelectedView = view != null;
        for (int i = 0; i < getChildCount(); i++) {
            StickerView stickerView = (StickerView) getChildAt(i);
            if (stickerView == view) {
                stickerView.setDrawImage(true, true, z);
            } else {
                stickerView.setDrawImage(!this.hasSelectedView, false, z);
            }
        }
    }

    public static int lambda$new$0(StickerView stickerView, StickerView stickerView2) {
        return (int) ((stickerView.progress * 100.0f) - (stickerView2.progress * 100.0f));
    }

    public void lambda$new$1(View view, int i) {
        if (view != null) {
            drawEffectForView(view, true);
            this.haptic = false;
            smoothScrollBy(0, view.getTop() - ((getMeasuredHeight() - view.getMeasuredHeight()) / 2), AndroidUtilities.overshootInterpolator);
        }
    }

    public void lambda$onLayout$2() {
        int size = 1073741823 - (1073741823 % this.premiumStickers.size());
        LinearLayoutManager linearLayoutManager = this.layoutManager;
        this.selectStickerOnNextLayout = size;
        linearLayoutManager.scrollToPositionWithOffset(size, (getMeasuredHeight() - getChildAt(0).getMeasuredHeight()) >> 1);
        drawEffectForView(null, false);
    }

    public void scheduleAutoScroll() {
        if (this.autoPlayEnabled) {
            AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
            AndroidUtilities.runOnUIThread(this.autoScrollRunnable, 2700L);
        }
    }

    private void setStickers() {
        this.premiumStickers.clear();
        this.premiumStickers.addAll(MediaDataController.getInstance(this.currentAccount).premiumPreviewStickers);
        getAdapter().notifyDataSetChanged();
        invalidate();
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.premiumStickersPreviewLoaded) {
            setStickers();
        }
    }

    @Override
    public void dispatchDraw(Canvas canvas) {
        if (this.isVisible) {
            this.sortedView.clear();
            for (int i = 0; i < getChildCount(); i++) {
                StickerView stickerView = (StickerView) getChildAt(i);
                float top = ((stickerView.getTop() + stickerView.getMeasuredHeight()) + (stickerView.getMeasuredHeight() >> 1)) / ((getMeasuredHeight() >> 1) + stickerView.getMeasuredHeight());
                if (top > 1.0f) {
                    top = 2.0f - top;
                }
                float clamp = Utilities.clamp(top, 1.0f, 0.0f);
                stickerView.progress = clamp;
                stickerView.view.setTranslationX((-getMeasuredWidth()) * 2.0f * (1.0f - this.interpolator.getInterpolation(clamp)));
                this.sortedView.add(stickerView);
            }
            Collections.sort(this.sortedView, this.comparator);
            if ((this.firstDraw || this.checkEffect) && this.sortedView.size() > 0 && !this.premiumStickers.isEmpty()) {
                View view = (View) this.sortedView.get(r1.size() - 1);
                this.oldSelectedView = view;
                drawEffectForView(view, !this.firstDraw);
                this.firstDraw = false;
                this.checkEffect = false;
            } else {
                if (this.oldSelectedView != this.sortedView.get(r2.size() - 1)) {
                    this.oldSelectedView = (View) this.sortedView.get(r1.size() - 1);
                    if (this.haptic) {
                        try {
                            performHapticFeedback(3);
                        } catch (Exception unused) {
                        }
                    }
                }
            }
            for (int i2 = 0; i2 < this.sortedView.size(); i2++) {
                canvas.save();
                canvas.translate(((StickerView) this.sortedView.get(i2)).getX(), ((StickerView) this.sortedView.get(i2)).getY());
                ((StickerView) this.sortedView.get(i2)).draw(canvas);
                canvas.restore();
            }
        }
    }

    @Override
    public boolean drawChild(Canvas canvas, View view, long j) {
        return true;
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.premiumStickersPreviewLoaded);
        scheduleAutoScroll();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.premiumStickersPreviewLoaded);
    }

    @Override
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.firstMeasure && !this.premiumStickers.isEmpty() && getChildCount() > 0) {
            this.firstMeasure = false;
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    PremiumStickersPreviewRecycler.this.lambda$onLayout$2();
                }
            });
        }
        int i5 = this.selectStickerOnNextLayout;
        if (i5 > 0) {
            RecyclerView.ViewHolder findViewHolderForAdapterPosition = findViewHolderForAdapterPosition(i5);
            if (findViewHolderForAdapterPosition != null) {
                drawEffectForView(findViewHolderForAdapterPosition.itemView, false);
            }
            this.selectStickerOnNextLayout = -1;
        }
    }

    @Override
    public void onMeasure(int i, int i2) {
        this.size = View.MeasureSpec.getSize(i2) > View.MeasureSpec.getSize(i) ? View.MeasureSpec.getSize(i) : View.MeasureSpec.getSize(i2);
        super.onMeasure(i, i2);
    }

    public void setAutoPlayEnabled(boolean z) {
        if (this.autoPlayEnabled != z) {
            this.autoPlayEnabled = z;
            if (!z) {
                AndroidUtilities.cancelRunOnUIThread(this.autoScrollRunnable);
                drawEffectForView(null, true);
            } else {
                scheduleAutoScroll();
                this.checkEffect = true;
                invalidate();
            }
        }
    }

    public void setOffset(float f) {
        boolean z = Math.abs(f / ((float) getMeasuredWidth())) < 1.0f;
        if (this.isVisible != z) {
            this.isVisible = z;
            invalidate();
        }
    }
}
