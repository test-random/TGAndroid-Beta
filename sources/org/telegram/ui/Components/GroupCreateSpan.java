package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;

public class GroupCreateSpan extends View {
    private AvatarDrawable avatarDrawable;
    private int[] colors;
    private ContactsController.Contact currentContact;
    private Drawable deleteDrawable;
    private boolean deleting;
    private boolean drawAvatarBackground;
    private ImageReceiver imageReceiver;
    public boolean isFlag;
    private String key;
    private long lastUpdateTime;
    private StaticLayout nameLayout;
    private float progress;
    private RectF rect;
    private Theme.ResourcesProvider resourcesProvider;
    private boolean small;
    private int textWidth;
    private float textX;
    private long uid;
    private static TextPaint textPaint = new TextPaint(1);
    private static Paint backPaint = new Paint(1);

    public GroupCreateSpan(Context context, Object obj) {
        this(context, obj, null);
    }

    public GroupCreateSpan(Context context, Object obj, ContactsController.Contact contact) {
        this(context, obj, contact, null);
    }

    public GroupCreateSpan(Context context, Object obj, ContactsController.Contact contact, Theme.ResourcesProvider resourcesProvider) {
        this(context, obj, contact, false, resourcesProvider);
    }

    public GroupCreateSpan(android.content.Context r33, java.lang.Object r34, org.telegram.messenger.ContactsController.Contact r35, boolean r36, org.telegram.ui.ActionBar.Theme.ResourcesProvider r37) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.GroupCreateSpan.<init>(android.content.Context, java.lang.Object, org.telegram.messenger.ContactsController$Contact, boolean, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public GroupCreateSpan(Context context, ContactsController.Contact contact) {
        this(context, null, contact);
    }

    public void cancelDeleteAnimation() {
        if (this.deleting) {
            this.deleting = false;
            this.lastUpdateTime = System.currentTimeMillis();
            invalidate();
        }
    }

    public ContactsController.Contact getContact() {
        return this.currentContact;
    }

    public String getKey() {
        return this.key;
    }

    public long getUid() {
        return this.uid;
    }

    public boolean isDeleting() {
        return this.deleting;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        boolean z = this.deleting;
        if ((z && this.progress != 1.0f) || (!z && this.progress != 0.0f)) {
            long currentTimeMillis = System.currentTimeMillis() - this.lastUpdateTime;
            if (currentTimeMillis < 0 || currentTimeMillis > 17) {
                currentTimeMillis = 17;
            }
            if (this.deleting) {
                float f = this.progress + (((float) currentTimeMillis) / 120.0f);
                this.progress = f;
                if (f >= 1.0f) {
                    this.progress = 1.0f;
                }
            } else {
                float f2 = this.progress - (((float) currentTimeMillis) / 120.0f);
                this.progress = f2;
                if (f2 < 0.0f) {
                    this.progress = 0.0f;
                }
            }
            invalidate();
        }
        canvas.save();
        this.rect.set(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(this.small ? 28.0f : 32.0f));
        Paint paint = backPaint;
        int[] iArr = this.colors;
        int i = iArr[6];
        float f3 = iArr[7] - i;
        float f4 = this.progress;
        paint.setColor(Color.argb(i + ((int) (f3 * f4)), iArr[0] + ((int) ((iArr[1] - r5) * f4)), iArr[2] + ((int) ((iArr[3] - r7) * f4)), iArr[4] + ((int) ((iArr[5] - r8) * f4))));
        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(this.small ? 14.0f : 16.0f), AndroidUtilities.dp(this.small ? 14.0f : 16.0f), backPaint);
        if (this.progress != 1.0f) {
            this.imageReceiver.draw(canvas);
        }
        if (this.progress != 0.0f) {
            backPaint.setColor(this.avatarDrawable.getColor());
            backPaint.setAlpha((int) (this.progress * 255.0f * (Color.alpha(r0) / 255.0f)));
            canvas.drawCircle(AndroidUtilities.dp(this.small ? 14.0f : 16.0f), AndroidUtilities.dp(this.small ? 14.0f : 16.0f), AndroidUtilities.dp(this.small ? 14.0f : 16.0f), backPaint);
            canvas.save();
            canvas.rotate((1.0f - this.progress) * 45.0f, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
            this.deleteDrawable.setBounds(AndroidUtilities.dp(this.small ? 9.0f : 11.0f), AndroidUtilities.dp(this.small ? 9.0f : 11.0f), AndroidUtilities.dp(this.small ? 19.0f : 21.0f), AndroidUtilities.dp(this.small ? 19.0f : 21.0f));
            this.deleteDrawable.setAlpha((int) (this.progress * 255.0f));
            this.deleteDrawable.draw(canvas);
            canvas.restore();
        }
        canvas.translate(this.textX + AndroidUtilities.dp((this.small ? 26 : 32) + 9), AndroidUtilities.dp(this.small ? 6.0f : 8.0f));
        textPaint.setColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_groupcreate_spanText, this.resourcesProvider), Theme.getColor(Theme.key_avatar_text, this.resourcesProvider), this.progress));
        this.nameLayout.draw(canvas);
        canvas.restore();
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        AccessibilityNodeInfo.AccessibilityAction accessibilityAction;
        int id;
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setText(this.nameLayout.getText());
        if (!isDeleting() || Build.VERSION.SDK_INT < 21) {
            return;
        }
        accessibilityAction = AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK;
        id = accessibilityAction.getId();
        accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(id, LocaleController.getString(R.string.Delete)));
    }

    @Override
    protected void onMeasure(int i, int i2) {
        setMeasuredDimension(AndroidUtilities.dp((this.small ? 20 : 32) + 25) + this.textWidth, AndroidUtilities.dp(this.small ? 28.0f : 32.0f));
    }

    public void startDeleteAnimation() {
        if (this.deleting) {
            return;
        }
        this.deleting = true;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void updateColors() {
        int color = this.avatarDrawable.getColor();
        int color2 = Theme.getColor(Theme.key_groupcreate_spanBackground, this.resourcesProvider);
        int color3 = Theme.getColor(Theme.key_groupcreate_spanDelete, this.resourcesProvider);
        this.colors[0] = Color.red(color2);
        this.colors[1] = Color.red(color);
        this.colors[2] = Color.green(color2);
        this.colors[3] = Color.green(color);
        this.colors[4] = Color.blue(color2);
        this.colors[5] = Color.blue(color);
        this.colors[6] = Color.alpha(color2);
        this.colors[7] = Color.alpha(color);
        this.deleteDrawable.setColorFilter(new PorterDuffColorFilter(color3, PorterDuff.Mode.MULTIPLY));
        backPaint.setColor(color2);
    }
}
