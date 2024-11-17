package org.telegram.ui.Stories.recorder;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;

public class AlbumButton extends View {
    private StaticLayout countLayout;
    private float countLayoutLeft;
    private float countLayoutWidth;
    private final TextPaint countPaintLayout;
    private final ImageReceiver imageReceiver;
    final float imageSize;
    private StaticLayout nameLayout;
    private float nameLayoutLeft;
    private float nameLayoutWidth;
    private final TextPaint namePaintLayout;
    private final CharSequence subtitle;
    private final CharSequence title;

    public AlbumButton(Context context, MediaController.PhotoEntry photoEntry, CharSequence charSequence, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        StringBuilder sb;
        String str;
        String sb2;
        ImageReceiver imageReceiver = new ImageReceiver(this);
        this.imageReceiver = imageReceiver;
        TextPaint textPaint = new TextPaint(1);
        this.namePaintLayout = textPaint;
        TextPaint textPaint2 = new TextPaint(1);
        this.countPaintLayout = textPaint2;
        this.imageSize = 30.0f;
        setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        setBackground(Theme.getSelectorDrawable(false));
        setMinimumWidth(AndroidUtilities.dp(196.0f));
        setLayoutParams(new LinearLayout.LayoutParams(-1, 48));
        int i2 = Theme.key_actionBarDefaultSubmenuItem;
        textPaint.setColor(Theme.getColor(i2, resourcesProvider));
        textPaint.setTextSize(AndroidUtilities.dp(16.0f));
        textPaint2.setColor(Theme.getColor(i2, resourcesProvider));
        textPaint2.setAlpha(102);
        textPaint2.setTextSize(AndroidUtilities.dp(13.0f));
        this.title = "" + ((Object) charSequence);
        this.subtitle = "" + i;
        imageReceiver.setRoundRadius(AndroidUtilities.dp(4.0f));
        Drawable mutate = context.getResources().getDrawable(R.drawable.msg_media_gallery).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(1308622847, PorterDuff.Mode.MULTIPLY));
        CombinedDrawable combinedDrawable = new CombinedDrawable(Theme.createRoundRectDrawable(AndroidUtilities.dp(6.0f), -13750737), mutate);
        combinedDrawable.setFullsize(false);
        combinedDrawable.setIconSize(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f));
        if (photoEntry == null || (sb2 = photoEntry.thumbPath) == null) {
            if (photoEntry == null || photoEntry.path == null) {
                imageReceiver.setImageBitmap(combinedDrawable);
                return;
            }
            if (photoEntry.isVideo) {
                sb = new StringBuilder();
                str = "vthumb://";
            } else {
                sb = new StringBuilder();
                str = "thumb://";
            }
            sb.append(str);
            sb.append(photoEntry.imageId);
            sb.append(":");
            sb.append(photoEntry.path);
            sb2 = sb.toString();
        }
        imageReceiver.setImage(ImageLocation.getForPath(sb2), "30.0_30.0", (ImageLocation) null, (String) null, combinedDrawable, (Object) null, 0);
    }

    private void updateLayouts(int i) {
        StaticLayout staticLayout = this.nameLayout;
        if (staticLayout == null || staticLayout.getWidth() != i) {
            TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
            CharSequence ellipsize = TextUtils.ellipsize(this.title, this.namePaintLayout, i, truncateAt);
            TextPaint textPaint = this.namePaintLayout;
            int max = Math.max(0, i);
            Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
            StaticLayout staticLayout2 = new StaticLayout(ellipsize, textPaint, max, alignment, 1.0f, 0.0f, false);
            this.nameLayout = staticLayout2;
            this.nameLayoutLeft = staticLayout2.getLineCount() > 0 ? this.nameLayout.getLineLeft(0) : 0.0f;
            float lineWidth = this.nameLayout.getLineCount() > 0 ? this.nameLayout.getLineWidth(0) : 0.0f;
            this.nameLayoutWidth = lineWidth;
            int dp = i - ((int) (lineWidth + AndroidUtilities.dp(8.0f)));
            StaticLayout staticLayout3 = new StaticLayout(TextUtils.ellipsize(this.subtitle, this.countPaintLayout, dp, truncateAt), this.countPaintLayout, Math.max(0, dp), alignment, 1.0f, 0.0f, false);
            this.countLayout = staticLayout3;
            this.countLayoutLeft = staticLayout3.getLineCount() > 0 ? this.countLayout.getLineLeft(0) : 0.0f;
            this.countLayoutWidth = this.countLayout.getLineCount() > 0 ? this.countLayout.getLineWidth(0) : 0.0f;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        float paddingLeft = getPaddingLeft();
        this.imageReceiver.setImageCoords(paddingLeft, (getMeasuredHeight() - AndroidUtilities.dp(30.0f)) / 2.0f, AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f));
        this.imageReceiver.draw(canvas);
        float dp = paddingLeft + AndroidUtilities.dp(30.0f) + AndroidUtilities.dp(12.0f);
        if (this.nameLayout != null) {
            canvas.save();
            canvas.translate(dp - this.nameLayoutLeft, (getMeasuredHeight() - this.nameLayout.getHeight()) / 2.0f);
            this.nameLayout.draw(canvas);
            dp = dp + this.nameLayoutWidth + AndroidUtilities.dp(6.0f);
            canvas.restore();
        }
        if (this.countLayout != null) {
            canvas.save();
            canvas.translate(dp - this.countLayoutLeft, ((getMeasuredHeight() - this.countLayout.getHeight()) / 2.0f) + AndroidUtilities.dpf2(1.6f));
            this.countLayout.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.imageReceiver.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.imageReceiver.onDetachedFromWindow();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size;
        updateLayouts((((View.MeasureSpec.getSize(i) - AndroidUtilities.dp(30.0f)) - AndroidUtilities.dp(12.0f)) - getPaddingLeft()) - getPaddingRight());
        if (View.MeasureSpec.getMode(i) == Integer.MIN_VALUE) {
            size = (int) Math.min(getPaddingLeft() + AndroidUtilities.dp(30.0f) + AndroidUtilities.dp(12.0f) + this.nameLayoutWidth + AndroidUtilities.dp(8.0f) + this.countLayoutWidth + getPaddingRight(), View.MeasureSpec.getSize(i));
        } else if (View.MeasureSpec.getMode(i) != 1073741824) {
            return;
        } else {
            size = View.MeasureSpec.getSize(i);
        }
        setMeasuredDimension(size, AndroidUtilities.dp(48.0f));
    }
}
