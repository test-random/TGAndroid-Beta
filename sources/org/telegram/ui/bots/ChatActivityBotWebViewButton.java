package org.telegram.ui.bots;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.SimpleFloatPropertyCompat;

public class ChatActivityBotWebViewButton extends FrameLayout {
    public static final SimpleFloatPropertyCompat PROGRESS_PROPERTY = new SimpleFloatPropertyCompat("progress", new SimpleFloatPropertyCompat.Getter() {
        @Override
        public final float get(Object obj) {
            float f;
            f = ((ChatActivityBotWebViewButton) obj).progress;
            return f;
        }
    }, new SimpleFloatPropertyCompat.Setter() {
        @Override
        public final void set(Object obj, float f) {
            ((ChatActivityBotWebViewButton) obj).setProgress(f);
        }
    }).setMultiplier(100.0f);
    private int backgroundColor;
    private int buttonColor;
    private BotCommandsMenuView menuButton;
    private int menuButtonWidth;
    private Path path;
    private float progress;
    private RadialProgressView progressView;
    private View rippleView;
    private TextView textView;

    public ChatActivityBotWebViewButton(Context context) {
        super(context);
        this.path = new Path();
        this.buttonColor = Theme.getColor(Theme.key_featuredStickers_addButton);
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextSize(1, 14.0f);
        this.textView.setSingleLine();
        this.textView.setAlpha(0.0f);
        this.textView.setGravity(17);
        this.textView.setTypeface(AndroidUtilities.bold());
        addView(this.textView, LayoutHelper.createFrame(-1, -1.0f, 3, 0.0f, 0.0f, 0.0f, 0.0f));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(18.0f));
        this.progressView.setAlpha(0.0f);
        this.progressView.setScaleX(0.0f);
        this.progressView.setScaleY(0.0f);
        addView(this.progressView, LayoutHelper.createFrame(28, 28.0f, 21, 0.0f, 0.0f, 12.0f, 0.0f));
        View view = new View(context);
        this.rippleView = view;
        view.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_featuredStickers_addButtonPressed), 2));
        addView(this.rippleView, LayoutHelper.createFrame(-1, -1.0f, 3, 0.0f, 0.0f, 0.0f, 0.0f));
        setWillNotDraw(false);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.save();
        float height = (getHeight() - AndroidUtilities.dp(32.0f)) / 2.0f;
        float max = Math.max((getWidth() - this.menuButtonWidth) - AndroidUtilities.dp(4.0f), getHeight()) * this.progress;
        float dp = AndroidUtilities.dp(16.0f) + max;
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(AndroidUtilities.dp(14.0f) - max, (AndroidUtilities.dp(4.0f) + height) - max, AndroidUtilities.dp(6.0f) + this.menuButtonWidth + max, (getHeight() - AndroidUtilities.dp(12.0f)) + max);
        this.path.rewind();
        this.path.addRoundRect(rectF, dp, dp, Path.Direction.CW);
        canvas.clipPath(this.path);
        canvas.drawColor(this.backgroundColor);
        canvas.saveLayerAlpha(rectF, (int) ((1.0f - (Math.min(0.5f, this.progress) / 0.5f)) * 255.0f), 31);
        canvas.translate(AndroidUtilities.dp(10.0f), height);
        BotCommandsMenuView botCommandsMenuView = this.menuButton;
        if (botCommandsMenuView != null) {
            botCommandsMenuView.setDrawBackgroundDrawable(false);
            this.menuButton.draw(canvas);
            this.menuButton.setDrawBackgroundDrawable(true);
        }
        canvas.restore();
        canvas.translate((-AndroidUtilities.dp(8.0f)) * (1.0f - this.progress), 0.0f);
        super.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        int size = View.MeasureSpec.getSize(i2);
        int height = getParent() instanceof View ? ((View) getParent()).getHeight() : 0;
        if (height > 0) {
            size = Math.min(size, height);
        }
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(size, View.MeasureSpec.getMode(i2)));
    }

    public void setBotMenuButton(BotCommandsMenuView botCommandsMenuView) {
        this.menuButton = botCommandsMenuView;
        invalidate();
    }

    public void setMeasuredButtonWidth(int i) {
        this.menuButtonWidth = i;
        invalidate();
    }

    public void setProgress(float f) {
        this.progress = f;
        this.backgroundColor = ColorUtils.blendARGB(Theme.getColor(Theme.key_chat_messagePanelVoiceBackground), this.buttonColor, f);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setAlpha(f);
        }
        invalidate();
    }
}
