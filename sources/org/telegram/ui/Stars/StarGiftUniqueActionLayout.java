package org.telegram.ui.Stars;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import java.util.ArrayList;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.Stars.StarsReactionsSheet;

public class StarGiftUniqueActionLayout {
    TLRPC.TL_messageActionStarGiftUnique action;
    private boolean attached;
    private TL_stars.starGiftAttributeBackdrop backdrop;
    private final ButtonBounce bounce;
    private final ButtonBounce buttonBounce;
    private float buttonHeight;
    private Text buttonText;
    private float buttonY;
    private final int currentAccount;
    MessageObject currentMessageObject;
    private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable emoji;
    private RadialGradient gradient;
    private int gradientRadius;
    int height;
    private final ImageReceiver imageReceiver;
    private TL_stars.starGiftAttributeModel model;
    private float nameWidth;
    private TL_stars.starGiftAttributePattern pattern;
    private final Theme.ResourcesProvider resourcesProvider;
    private final GiftSheet.RibbonDrawable ribbon;
    private Text subtitle;
    private float subtitleY;
    private Text title;
    private float titleY;
    private float valueWidth;
    private final ChatActionCell view;
    int width;
    private final Paint backgroundPaint = new Paint(1);
    private final Matrix matrix = new Matrix();
    private final RectF backgroundRect = new RectF();
    private final Path backgroundPath = new Path();
    private final ArrayList table = new ArrayList();
    private final RectF buttonRect = new RectF();
    private final Path buttonPath = new Path();
    private final Paint buttonBackgroundPaint = new Paint();
    private final StarsReactionsSheet.Particles buttonParticles = new StarsReactionsSheet.Particles(1, 25);

    public static final class Row {
        public final Text name;
        public final Text value;
        public final float y;

        public Row(float f, CharSequence charSequence, CharSequence charSequence2) {
            this.name = new Text(charSequence, 12.0f);
            this.value = new Text(charSequence2, 12.0f, AndroidUtilities.bold());
            this.y = f + (getHeight() / 2.0f);
        }

        public float getHeight() {
            return Math.max(this.name.getHeight(), this.value.getHeight());
        }
    }

    public StarGiftUniqueActionLayout(int i, ChatActionCell chatActionCell, Theme.ResourcesProvider resourcesProvider) {
        this.currentAccount = i;
        this.view = chatActionCell;
        this.resourcesProvider = resourcesProvider;
        this.ribbon = new GiftSheet.RibbonDrawable(chatActionCell, 1.0f);
        this.buttonBounce = new ButtonBounce(chatActionCell);
        this.bounce = new ButtonBounce(chatActionCell);
        this.imageReceiver = new ImageReceiver(chatActionCell);
        this.emoji = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(chatActionCell, AndroidUtilities.dp(28.0f));
    }

    public void attach() {
        this.attached = true;
        if (this.action != null) {
            this.imageReceiver.onAttachedToWindow();
            this.emoji.attach();
        }
    }

    public void detach() {
        this.attached = false;
        this.imageReceiver.onDetachedFromWindow();
        this.emoji.detach();
    }

    public void draw(Canvas canvas) {
        float width = getWidth() / 2.0f;
        this.backgroundRect.set(0.0f, 0.0f, getWidth(), getHeight());
        int width2 = ((int) (this.backgroundRect.width() + this.backgroundRect.height())) / 2;
        if (this.backdrop != null && (this.gradient == null || this.gradientRadius != width2)) {
            this.gradientRadius = width2;
            float f = width2;
            TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop = this.backdrop;
            this.gradient = new RadialGradient(0.0f, 0.0f, f, new int[]{stargiftattributebackdrop.center_color | (-16777216), stargiftattributebackdrop.edge_color | (-16777216)}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
        }
        if (this.gradient != null) {
            this.matrix.reset();
            this.matrix.postTranslate(width, width);
            this.gradient.setLocalMatrix(this.matrix);
            this.backgroundPaint.setShader(this.gradient);
        }
        this.backgroundPath.rewind();
        this.backgroundPath.addRoundRect(this.backgroundRect, AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f), Path.Direction.CW);
        canvas.save();
        float scale = this.bounce.getScale(0.0125f);
        canvas.scale(scale, scale, this.backgroundRect.centerX(), this.backgroundRect.centerY());
        canvas.save();
        canvas.clipPath(this.backgroundPath);
        canvas.drawPaint(this.backgroundPaint);
        canvas.save();
        canvas.translate(width, AndroidUtilities.dp(65.0f));
        TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop2 = this.backdrop;
        if (stargiftattributebackdrop2 != null) {
            this.emoji.setColor(Integer.valueOf(stargiftattributebackdrop2.pattern_color | (-16777216)));
        }
        StarGiftPatterns.drawPatterns(canvas, 1, this.emoji, this.backgroundRect.width(), this.backgroundRect.height(), 1.0f, 1.1f);
        canvas.restore();
        this.imageReceiver.setImageCoords(width - (AndroidUtilities.dp(110.0f) / 2.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(110.0f), AndroidUtilities.dp(110.0f));
        this.imageReceiver.draw(canvas);
        int multAlpha = Theme.multAlpha(-1, 0.6f);
        TL_stars.starGiftAttributeBackdrop stargiftattributebackdrop3 = this.backdrop;
        if (stargiftattributebackdrop3 != null) {
            multAlpha = stargiftattributebackdrop3.text_color | (-16777216);
        }
        int i = multAlpha;
        this.title.ellipsize(getWidth() - AndroidUtilities.dp(12.0f));
        Text text = this.title;
        text.draw(canvas, width - (text.getCurrentWidth() / 2.0f), this.titleY, -1, 1.0f);
        this.subtitle.ellipsize(getWidth() - AndroidUtilities.dp(12.0f));
        Text text2 = this.subtitle;
        text2.draw(canvas, width - (text2.getCurrentWidth() / 2.0f), this.subtitleY, i, 1.0f);
        float dp = this.nameWidth + AndroidUtilities.dp(9.0f) + this.valueWidth;
        Iterator it = this.table.iterator();
        while (it.hasNext()) {
            Row row = (Row) it.next();
            Text text3 = row.name;
            float f2 = width - (dp / 2.0f);
            text3.draw(canvas, (f2 + this.nameWidth) - text3.getCurrentWidth(), row.y, i, 1.0f);
            row.value.draw(canvas, f2 + this.nameWidth + AndroidUtilities.dp(9.0f), row.y, -1, 1.0f);
        }
        this.buttonRect.set(width - ((this.buttonText.getCurrentWidth() + AndroidUtilities.dp(30.0f)) / 2.0f), this.buttonY, width + ((this.buttonText.getCurrentWidth() + AndroidUtilities.dp(30.0f)) / 2.0f), this.buttonY + this.buttonHeight);
        this.buttonPath.rewind();
        Path path = this.buttonPath;
        RectF rectF = this.buttonRect;
        float f3 = this.buttonHeight / 2.0f;
        path.addRoundRect(rectF, f3, f3, Path.Direction.CW);
        this.buttonBackgroundPaint.setColor(Theme.multAlpha(-16777216, 0.13f));
        float scale2 = this.buttonBounce.getScale(0.075f);
        canvas.scale(scale2, scale2, this.buttonRect.centerX(), this.buttonRect.centerY());
        canvas.drawPath(this.buttonPath, this.buttonBackgroundPaint);
        canvas.restore();
        this.ribbon.setBounds(((int) this.backgroundRect.right) - AndroidUtilities.dp(46.67f), ((int) this.backgroundRect.top) - AndroidUtilities.dp(1.33f), ((int) this.backgroundRect.right) + AndroidUtilities.dp(1.33f), ((int) this.backgroundRect.top) + AndroidUtilities.dp(46.67f));
        this.ribbon.setTextColor(i);
        this.ribbon.draw(canvas);
        canvas.restore();
    }

    public void drawOutbounds(Canvas canvas) {
        canvas.save();
        float scale = this.bounce.getScale(0.0125f);
        canvas.scale(scale, scale, this.backgroundRect.centerX(), this.backgroundRect.centerY());
        float scale2 = this.buttonBounce.getScale(0.075f);
        canvas.scale(scale2, scale2, this.buttonRect.centerX(), this.buttonRect.centerY());
        canvas.clipPath(this.buttonPath);
        this.buttonParticles.setBounds(this.buttonRect);
        this.buttonParticles.process();
        this.buttonParticles.draw(canvas, Theme.multAlpha(-1, 0.7f));
        this.buttonText.draw(canvas, this.buttonRect.left + AndroidUtilities.dp(15.0f), this.buttonRect.centerY(), -1, 1.0f);
        canvas.restore();
        this.view.invalidateOutbounds();
    }

    public float getHeight() {
        return this.height;
    }

    public float getWidth() {
        return this.width;
    }

    public boolean has() {
        return this.action != null;
    }

    public boolean onTouchEvent(float r10, float r11, android.view.MotionEvent r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarGiftUniqueActionLayout.onTouchEvent(float, float, android.view.MotionEvent):boolean");
    }

    public void set(org.telegram.messenger.MessageObject r10, boolean r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarGiftUniqueActionLayout.set(org.telegram.messenger.MessageObject, boolean):void");
    }
}
