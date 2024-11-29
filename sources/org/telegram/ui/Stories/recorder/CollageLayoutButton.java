package org.telegram.ui.Stories.recorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.GradientClip;
import org.telegram.ui.Stories.recorder.CollageLayout;
import org.telegram.ui.Stories.recorder.CollageLayoutButton;

public class CollageLayoutButton extends ToggleButton2 {

    public static class CollageLayoutDrawable extends Drawable {
        private boolean cross;
        public final Paint crossPaint;
        public final Paint crossXferPaint;
        public final Paint paint;
        public final Path path;
        private final float[] radii;

        public CollageLayoutDrawable(CollageLayout collageLayout) {
            this(collageLayout, false);
        }

        public CollageLayoutDrawable(CollageLayout collageLayout, boolean z) {
            char c;
            float f;
            int i = 1;
            Paint paint = new Paint(1);
            this.paint = paint;
            this.crossXferPaint = new Paint(1);
            this.crossPaint = new Paint(1);
            Path path = new Path();
            this.path = path;
            this.radii = new float[8];
            this.cross = z;
            paint.setColor(-1);
            float dpf2 = AndroidUtilities.dpf2(13.333333f);
            float dpf22 = AndroidUtilities.dpf2(18.666666f);
            float dpf23 = AndroidUtilities.dpf2(3.0f);
            float dpf24 = AndroidUtilities.dpf2(10.0f);
            float dpf25 = AndroidUtilities.dpf2(15.333333f);
            float dpf26 = AndroidUtilities.dpf2(1.0f);
            float dpf27 = AndroidUtilities.dpf2(1.33f);
            path.setFillType(Path.FillType.EVEN_ODD);
            RectF rectF = AndroidUtilities.rectTmp;
            float f2 = 2.0f;
            rectF.set((-dpf2) / 2.0f, (-dpf22) / 2.0f, dpf2 / 2.0f, dpf22 / 2.0f);
            path.addRoundRect(rectF, dpf23, dpf23, Path.Direction.CW);
            Iterator it = collageLayout.parts.iterator();
            while (it.hasNext()) {
                CollageLayout.Part part = (CollageLayout.Part) it.next();
                int i2 = collageLayout.columns[part.y];
                int i3 = i2 - 1;
                float max = (dpf24 - (Math.max(0, i3) * dpf27)) / i2;
                float max2 = (dpf25 - (Math.max(0, collageLayout.h - i) * dpf27)) / collageLayout.h;
                RectF rectF2 = AndroidUtilities.rectTmp;
                float f3 = (-dpf24) / f2;
                float f4 = part.x;
                float f5 = f3 + (max * f4);
                float f6 = f4 * dpf27;
                Iterator it2 = it;
                float f7 = f5 + f6;
                float f8 = dpf24;
                float f9 = (-dpf25) / f2;
                float f10 = dpf25;
                float f11 = part.y;
                float f12 = f9 + (max2 * f11);
                float f13 = f11 * dpf27;
                float f14 = dpf26;
                rectF2.set(f7, f12 + f13, f3 + (max * (r7 + 1)) + f6, f9 + (max2 * (r15 + 1)) + f13);
                float[] fArr = this.radii;
                int i4 = part.x;
                float f15 = 0.0f;
                if (i4 == 0 && part.y == 0) {
                    f = f14;
                    c = 1;
                } else {
                    c = 1;
                    f = 0.0f;
                }
                fArr[c] = f;
                fArr[0] = f;
                float f16 = (i4 == i3 && part.y == 0) ? f14 : 0.0f;
                fArr[3] = f16;
                fArr[2] = f16;
                float f17 = (i4 == i3 && part.y == collageLayout.h - 1) ? f14 : 0.0f;
                fArr[5] = f17;
                fArr[4] = f17;
                if (i4 == 0 && part.y == collageLayout.h - 1) {
                    f15 = f14;
                }
                fArr[7] = f15;
                fArr[6] = f15;
                this.path.addRoundRect(rectF2, fArr, Path.Direction.CW);
                dpf24 = f8;
                it = it2;
                dpf25 = f10;
                dpf26 = f14;
                i = 1;
                f2 = 2.0f;
            }
            Paint paint2 = this.crossXferPaint;
            Paint.Style style = Paint.Style.STROKE;
            paint2.setStyle(style);
            this.crossXferPaint.setStrokeWidth(AndroidUtilities.dp(3.33f));
            this.crossXferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            this.crossPaint.setStyle(style);
            this.crossPaint.setStrokeWidth(AndroidUtilities.dp(1.33f));
            this.crossPaint.setColor(-1);
            this.crossPaint.setStrokeCap(Paint.Cap.ROUND);
            this.crossPaint.setStrokeJoin(Paint.Join.ROUND);
        }

        @Override
        public void draw(Canvas canvas) {
            if (this.cross) {
                canvas.saveLayerAlpha(getBounds().left, getBounds().top, getBounds().right, getBounds().bottom, 255, 31);
            } else {
                canvas.save();
            }
            canvas.translate(getBounds().centerX(), getBounds().centerY());
            canvas.drawPath(this.path, this.paint);
            if (this.cross) {
                canvas.drawLine(-AndroidUtilities.dp(8.66f), -AndroidUtilities.dp(8.66f), AndroidUtilities.dp(8.66f), AndroidUtilities.dp(8.66f), this.crossXferPaint);
                canvas.drawLine(-AndroidUtilities.dp(8.66f), -AndroidUtilities.dp(8.66f), AndroidUtilities.dp(8.66f), AndroidUtilities.dp(8.66f), this.crossPaint);
            }
            canvas.restore();
        }

        @Override
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(32.0f);
        }

        @Override
        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(32.0f);
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void setAlpha(int i) {
            this.paint.setAlpha(i);
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            this.paint.setColorFilter(colorFilter);
        }
    }

    public static class CollageLayoutListView extends FrameLayout {
        public final RecyclerListView listView;
        private Utilities.Callback onLayoutClick;
        private CollageLayout selectedLayout;
        private boolean visible;
        private ValueAnimator visibleAnimator;
        private float visibleProgress;

        private static class Button extends ToggleButton2 {
            public int position;

            public Button(Context context) {
                super(context);
            }
        }

        public CollageLayoutListView(final Context context, final FlashViews flashViews) {
            super(context);
            RecyclerListView recyclerListView = new RecyclerListView(context) {
                private final GradientClip clip = new GradientClip();

                @Override
                public void dispatchDraw(Canvas canvas) {
                    canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) (CollageLayoutListView.this.visibleProgress * 255.0f), 31);
                    canvas.save();
                    float paddingLeft = getPaddingLeft();
                    float width = getWidth() - getPaddingRight();
                    canvas.clipRect(paddingLeft, 0.0f, width, getHeight());
                    canvas.translate((1.0f - CollageLayoutListView.this.visibleProgress) * width, 0.0f);
                    super.dispatchDraw(canvas);
                    canvas.restore();
                    canvas.save();
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(paddingLeft, 0.0f, AndroidUtilities.dp(12.0f) + paddingLeft, getHeight());
                    this.clip.draw(canvas, rectF, 0, CollageLayoutListView.this.visibleProgress);
                    rectF.set(width - AndroidUtilities.dp(12.0f), 0.0f, width, getHeight());
                    this.clip.draw(canvas, rectF, 2, CollageLayoutListView.this.visibleProgress);
                    canvas.restore();
                    canvas.restore();
                }

                @Override
                public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getX() <= getPaddingLeft() || motionEvent.getX() >= getWidth() - getPaddingRight()) {
                        return false;
                    }
                    return super.dispatchTouchEvent(motionEvent);
                }

                @Override
                public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                    if (motionEvent.getX() <= getPaddingLeft() || motionEvent.getX() >= getWidth() - getPaddingRight()) {
                        return false;
                    }
                    getParent().requestDisallowInterceptTouchEvent(true);
                    return super.onInterceptTouchEvent(motionEvent);
                }
            };
            this.listView = recyclerListView;
            recyclerListView.setAdapter(new RecyclerView.Adapter() {
                @Override
                public int getItemCount() {
                    return CollageLayout.getLayouts().size();
                }

                @Override
                public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                    Button button = (Button) viewHolder.itemView;
                    CollageLayout collageLayout = (CollageLayout) CollageLayout.getLayouts().get(i);
                    boolean z = i == button.position;
                    button.setDrawable(new CollageLayoutDrawable(collageLayout));
                    button.setSelected(collageLayout.equals(CollageLayoutListView.this.selectedLayout), z);
                    button.position = i;
                }

                @Override
                public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                    Button button = new Button(context);
                    button.setLayoutParams(new RecyclerView.LayoutParams(AndroidUtilities.dp(46.0f), AndroidUtilities.dp(56.0f)));
                    button.setBackground(Theme.createSelectorDrawable(553648127));
                    return new RecyclerListView.Holder(button);
                }

                @Override
                public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
                    Button button = (Button) viewHolder.itemView;
                    flashViews.add(button);
                    int i = button.position;
                    if (i >= 0 && i < CollageLayout.getLayouts().size()) {
                        CollageLayout collageLayout = (CollageLayout) CollageLayout.getLayouts().get(button.position);
                        button.setDrawable(new CollageLayoutDrawable(collageLayout));
                        button.setSelected(collageLayout.equals(CollageLayoutListView.this.selectedLayout), false);
                    }
                    super.onViewAttachedToWindow(viewHolder);
                }

                @Override
                public void onViewDetachedFromWindow(RecyclerView.ViewHolder viewHolder) {
                    flashViews.remove((Button) viewHolder.itemView);
                    super.onViewDetachedFromWindow(viewHolder);
                }
            });
            recyclerListView.setLayoutManager(new LinearLayoutManager(context, 0, false));
            recyclerListView.setClipToPadding(false);
            recyclerListView.setVisibility(8);
            recyclerListView.setWillNotDraw(false);
            recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                @Override
                public final void onItemClick(View view, int i) {
                    CollageLayoutButton.CollageLayoutListView.this.lambda$new$0(view, i);
                }
            });
            addView(recyclerListView, LayoutHelper.createFrame(-1, 56.0f));
        }

        public void lambda$new$0(View view, int i) {
            Utilities.Callback callback = this.onLayoutClick;
            if (callback != null) {
                callback.run((CollageLayout) CollageLayout.getLayouts().get(i));
            }
        }

        public void lambda$setVisible$1(ValueAnimator valueAnimator) {
            this.visibleProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.listView.invalidate();
        }

        public boolean isVisible() {
            return this.visible;
        }

        public void setBounds(float f, float f2) {
            this.listView.setPadding((int) f, 0, (int) f2, 0);
            this.listView.invalidate();
        }

        public void setOnLayoutClick(Utilities.Callback<CollageLayout> callback) {
            this.onLayoutClick = callback;
        }

        public void setSelected(CollageLayout collageLayout) {
            this.selectedLayout = collageLayout;
            AndroidUtilities.updateVisibleRows(this.listView);
        }

        public void setVisible(final boolean z, boolean z2) {
            ValueAnimator valueAnimator = this.visibleAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            if (this.visible == z) {
                return;
            }
            this.visible = z;
            if (!z2) {
                this.visibleProgress = z ? 1.0f : 0.0f;
                this.listView.invalidate();
                this.listView.setVisibility(z ? 0 : 8);
                return;
            }
            this.listView.setVisibility(0);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.visibleProgress, z ? 1.0f : 0.0f);
            this.visibleAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    CollageLayoutButton.CollageLayoutListView.this.lambda$setVisible$1(valueAnimator2);
                }
            });
            this.visibleAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    CollageLayoutListView.this.visibleProgress = z ? 1.0f : 0.0f;
                    CollageLayoutListView.this.listView.invalidate();
                    CollageLayoutListView.this.listView.setVisibility(z ? 0 : 8);
                }
            });
            this.visibleAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.visibleAnimator.setDuration(340L);
            this.visibleAnimator.start();
        }
    }

    public CollageLayoutButton(Context context) {
        super(context);
    }
}
