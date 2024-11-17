package org.telegram.ui.Components.Paint.Views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.PaintTypeface;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;

public class PaintTextOptionsView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final List ALIGN_PAIRS;
    private RLottieImageView alignView;
    private View colorClickableView;
    private int currentAlign;
    private Delegate delegate;
    private ChatActivityEnterViewAnimatedIconView emojiButton;
    private String lastTypefaceKey;
    private int outlineType;
    private ImageView outlineView;
    private int plusIcon;
    private ImageView plusView;
    private TypefaceCell typefaceCell;
    private PaintTypefaceListView typefaceListView;
    private int x;

    public static final class AlignFramePair {
        private final int fromAlign;
        private final int fromFrame;
        private final int toAlign;
        private final int toFrame;

        private AlignFramePair(int i, int i2, int i3, int i4) {
            this.fromAlign = i;
            this.toAlign = i2;
            this.fromFrame = i3;
            this.toFrame = i4;
        }
    }

    public interface Delegate {
        void onColorPickerSelected();

        void onNewTextSelected();

        void onTextAlignmentSelected(int i);

        void onTextOutlineSelected(View view);

        void onTypefaceButtonClicked();
    }

    public static final class TypefaceCell extends TextView {
        private Drawable expandDrawable;
        private boolean isCurrent;

        public TypefaceCell(Context context) {
            super(context);
            setTextColor(-1);
            setTextSize(1, 14.0f);
            setCurrent(false);
            setEllipsize(TextUtils.TruncateAt.END);
            setSingleLine();
        }

        public void bind(PaintTypeface paintTypeface) {
            setTypeface(paintTypeface.getTypeface());
            setText(paintTypeface.getName());
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.save();
            canvas.translate(0.0f, AndroidUtilities.dp(-1.0f));
            super.onDraw(canvas);
            canvas.restore();
            if (this.isCurrent) {
                int height = (getHeight() - AndroidUtilities.dp(16.0f)) / 2;
                if (LocaleController.isRTL) {
                    this.expandDrawable.setBounds(AndroidUtilities.dp(7.0f), height, AndroidUtilities.dp(23.0f), AndroidUtilities.dp(16.0f) + height);
                } else {
                    this.expandDrawable.setBounds(getWidth() - AndroidUtilities.dp(23.0f), height, getWidth() - AndroidUtilities.dp(7.0f), AndroidUtilities.dp(16.0f) + height);
                }
                this.expandDrawable.draw(canvas);
            }
        }

        public void setCurrent(boolean z) {
            Drawable rect;
            this.isCurrent = z;
            if (z) {
                setPadding(AndroidUtilities.dp(LocaleController.isRTL ? 27.0f : 12.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(LocaleController.isRTL ? 12.0f : 27.0f), AndroidUtilities.dp(6.0f));
                rect = Theme.AdaptiveRipple.rect(1090519039, AndroidUtilities.dp(32.0f));
            } else {
                setPadding(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(14.0f));
                rect = Theme.AdaptiveRipple.rect(-14145495);
            }
            setBackground(rect);
            if (this.isCurrent && this.expandDrawable == null) {
                Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.photo_expand);
                this.expandDrawable = drawable;
                drawable.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
            }
            invalidate();
        }
    }

    static {
        int i = 40;
        int i2 = 2;
        int i3 = 20;
        int i4 = 0;
        ALIGN_PAIRS = Arrays.asList(new AlignFramePair(0, 1, 20, 0), new AlignFramePair(0, i2, 20, i), new AlignFramePair(1, i4, 0, i3), new AlignFramePair(1, i2, 60, i), new AlignFramePair(2, i4, 40, i3), new AlignFramePair(2, 1, 40, 60));
    }

    public PaintTextOptionsView(Context context) {
        super(context);
        this.currentAlign = 0;
        setWillNotDraw(false);
        View view = new View(context);
        this.colorClickableView = view;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                PaintTextOptionsView.this.lambda$new$0(view2);
            }
        });
        addView(this.colorClickableView, LayoutHelper.createFrame(24, 24.0f, 48, 0.0f, 0.0f, 16.0f, 0.0f));
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.alignView = rLottieImageView;
        rLottieImageView.setAnimation(R.raw.photo_text_allign, 24, 24);
        RLottieDrawable animatedDrawable = this.alignView.getAnimatedDrawable();
        animatedDrawable.setPlayInDirectionOfCustomEndFrame(true);
        animatedDrawable.setCustomEndFrame(20);
        animatedDrawable.setCurrentFrame(20);
        RLottieImageView rLottieImageView2 = this.alignView;
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        rLottieImageView2.setColorFilter(new PorterDuffColorFilter(-1, mode));
        this.alignView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                PaintTextOptionsView.this.lambda$new$1(view2);
            }
        });
        this.alignView.setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
        addView(this.alignView, LayoutHelper.createFrame(28, 28.0f, 16, 0.0f, 0.0f, 16.0f, 0.0f));
        ImageView imageView = new ImageView(context);
        this.outlineView = imageView;
        imageView.setImageResource(R.drawable.msg_text_outlined);
        this.outlineView.setPadding(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f));
        this.outlineView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                PaintTextOptionsView.this.lambda$new$2(view2);
            }
        });
        addView(this.outlineView, LayoutHelper.createFrame(28, 28.0f, 16, 0.0f, 0.0f, 16.0f, 0.0f));
        ImageView imageView2 = new ImageView(context);
        this.plusView = imageView2;
        imageView2.setImageResource(R.drawable.msg_add);
        this.plusView.setColorFilter(new PorterDuffColorFilter(-1, mode));
        this.plusView.setBackground(Theme.createSelectorDrawable(1090519039));
        this.plusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                PaintTextOptionsView.this.lambda$new$3(view2);
            }
        });
        this.plusView.setPadding(AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
        addView(this.plusView, LayoutHelper.createFrame(28, 28.0f, 16, 0.0f, 0.0f, 16.0f, 0.0f));
        TypefaceCell typefaceCell = new TypefaceCell(context);
        this.typefaceCell = typefaceCell;
        typefaceCell.setCurrent(true);
        this.typefaceCell.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                PaintTextOptionsView.this.lambda$new$4(view2);
            }
        });
        addView(this.typefaceCell, LayoutHelper.createLinear(-2, -2, 0.0f, 21));
    }

    public void lambda$new$0(View view) {
        this.delegate.onColorPickerSelected();
    }

    public void lambda$new$1(View view) {
        setAlignment((this.currentAlign + 1) % 3, true);
    }

    public void lambda$new$2(View view) {
        this.delegate.onTextOutlineSelected(view);
    }

    public void lambda$new$3(View view) {
        this.delegate.onNewTextSelected();
    }

    public void lambda$new$4(View view) {
        this.delegate.onTypefaceButtonClicked();
    }

    private void layoutChild(View view) {
        if (view.getVisibility() != 8) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
            int i = this.x + layoutParams.leftMargin;
            this.x = i;
            view.layout(i, (getMeasuredHeight() - layoutParams.height) / 2, this.x + layoutParams.width, (getMeasuredHeight() + layoutParams.height) / 2);
            this.x += layoutParams.width + layoutParams.rightMargin;
        }
    }

    public void animatePlusToIcon(int i) {
        if (i == 0) {
            i = R.drawable.msg_add;
        }
        if (this.plusIcon != i) {
            ImageView imageView = this.plusView;
            this.plusIcon = i;
            AndroidUtilities.updateImageViewImageAnimated(imageView, i);
        }
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        if (i != NotificationCenter.customTypefacesLoaded || (str = this.lastTypefaceKey) == null) {
            return;
        }
        setTypeface(str);
        this.lastTypefaceKey = null;
    }

    public View getColorClickableView() {
        return this.colorClickableView;
    }

    public ChatActivityEnterViewAnimatedIconView getEmojiButton() {
        return this.emojiButton;
    }

    public TypefaceCell getTypefaceCell() {
        return this.typefaceCell;
    }

    public void getTypefaceCellBounds(RectF rectF) {
        rectF.set(this.typefaceCell.getLeft() + AndroidUtilities.dp(8.0f), this.typefaceCell.getTop(), this.typefaceCell.getRight() + AndroidUtilities.dp(8.0f), this.typefaceCell.getBottom());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.customTypefacesLoaded);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.customTypefacesLoaded);
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        this.x = getPaddingLeft();
        layoutChild(this.colorClickableView);
        layoutChild(this.alignView);
        layoutChild(this.outlineView);
        layoutChild(this.plusView);
        this.typefaceCell.layout((getMeasuredWidth() - getPaddingRight()) - this.typefaceCell.getMeasuredWidth(), (getMeasuredHeight() - this.typefaceCell.getMeasuredHeight()) / 2, getMeasuredWidth() - getPaddingRight(), (getMeasuredHeight() + this.typefaceCell.getMeasuredHeight()) / 2);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int paddingLeft = (size - getPaddingLeft()) - getPaddingRight();
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            View childAt = getChildAt(i3);
            TypefaceCell typefaceCell = this.typefaceCell;
            if (childAt == typefaceCell) {
                typefaceCell.measure(View.MeasureSpec.makeMeasureSpec(paddingLeft, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(size2, Integer.MIN_VALUE));
            } else {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                childAt.measure(View.MeasureSpec.makeMeasureSpec(layoutParams.width, 1073741824), View.MeasureSpec.makeMeasureSpec(layoutParams.height, 1073741824));
                paddingLeft -= (childAt.getMeasuredWidth() + layoutParams.leftMargin) + layoutParams.rightMargin;
            }
        }
        setMeasuredDimension(size, size2);
    }

    public void setAlignment(int i) {
        setAlignment(i, false);
    }

    public void setAlignment(int i, boolean z) {
        int i2 = this.currentAlign;
        this.currentAlign = i;
        if (i2 == i) {
            RLottieDrawable animatedDrawable = this.alignView.getAnimatedDrawable();
            List list = ALIGN_PAIRS;
            AlignFramePair alignFramePair = (AlignFramePair) list.get(0);
            Iterator it = list.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                AlignFramePair alignFramePair2 = (AlignFramePair) it.next();
                if (this.currentAlign == alignFramePair2.toAlign) {
                    alignFramePair = alignFramePair2;
                    break;
                }
            }
            animatedDrawable.setCurrentFrame(alignFramePair.toFrame);
            animatedDrawable.setCustomEndFrame(alignFramePair.toFrame);
            if (z) {
                this.delegate.onTextAlignmentSelected(i);
                return;
            }
            return;
        }
        List list2 = ALIGN_PAIRS;
        AlignFramePair alignFramePair3 = (AlignFramePair) list2.get(0);
        Iterator it2 = list2.iterator();
        while (true) {
            if (!it2.hasNext()) {
                break;
            }
            AlignFramePair alignFramePair4 = (AlignFramePair) it2.next();
            if (i2 == alignFramePair4.fromAlign && this.currentAlign == alignFramePair4.toAlign) {
                alignFramePair3 = alignFramePair4;
                break;
            }
        }
        RLottieDrawable animatedDrawable2 = this.alignView.getAnimatedDrawable();
        animatedDrawable2.setCurrentFrame(alignFramePair3.fromFrame);
        animatedDrawable2.setCustomEndFrame(alignFramePair3.toFrame);
        animatedDrawable2.start();
        if (z) {
            this.delegate.onTextAlignmentSelected(i);
        }
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public void setOutlineType(int i) {
        setOutlineType(i, false);
    }

    public void setOutlineType(int i, boolean z) {
        if (this.outlineType == i) {
            return;
        }
        this.outlineType = i;
        int i2 = i != 1 ? i != 2 ? i != 3 ? R.drawable.msg_photo_text_framed : R.drawable.msg_photo_text_regular : R.drawable.msg_photo_text_framed3 : R.drawable.msg_photo_text_framed2;
        if (z) {
            AndroidUtilities.updateImageViewImageAnimated(this.outlineView, i2);
        } else {
            this.outlineView.setImageResource(i2);
        }
    }

    public void setTypeface(String str) {
        this.lastTypefaceKey = str;
        if (this.typefaceCell == null) {
            return;
        }
        for (PaintTypeface paintTypeface : PaintTypeface.get()) {
            if (paintTypeface.getKey().equals(str)) {
                this.typefaceCell.bind(paintTypeface);
                return;
            }
        }
    }

    public void setTypefaceListView(PaintTypefaceListView paintTypefaceListView) {
        this.typefaceListView = paintTypefaceListView;
    }
}
