package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.text.TextPaint;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public abstract class ChatListCell extends LinearLayout {
    private ListView[] listView;

    public class ListView extends FrameLayout {
        private RadioButton button;
        private boolean isThreeLines;
        private RectF rect;
        private TextPaint textPaint;

        public ListView(Context context, boolean z) {
            super(context);
            this.rect = new RectF();
            boolean z2 = true;
            this.textPaint = new TextPaint(1);
            setWillNotDraw(false);
            this.isThreeLines = z;
            setContentDescription(LocaleController.getString(z ? R.string.ChatListExpanded : R.string.ChatListDefault));
            this.textPaint.setTextSize(AndroidUtilities.dp(13.0f));
            RadioButton radioButton = new RadioButton(context) {
                @Override
                public void invalidate() {
                    super.invalidate();
                    ListView.this.invalidate();
                }
            };
            this.button = radioButton;
            radioButton.setSize(AndroidUtilities.dp(20.0f));
            addView(this.button, LayoutHelper.createFrame(22, 22.0f, 53, 0.0f, 26.0f, 10.0f, 0.0f));
            RadioButton radioButton2 = this.button;
            boolean z3 = this.isThreeLines;
            if ((!z3 || !SharedConfig.useThreeLinesLayout) && (z3 || SharedConfig.useThreeLinesLayout)) {
                z2 = false;
            }
            radioButton2.setChecked(z2, false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            RectF rectF;
            float dp;
            float dp2;
            int color = Theme.getColor(Theme.key_switchTrack);
            int red = Color.red(color);
            int green = Color.green(color);
            int blue = Color.blue(color);
            this.button.setColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_radioBackgroundChecked));
            this.rect.set(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), getMeasuredWidth() - AndroidUtilities.dp(1.0f), AndroidUtilities.dp(73.0f));
            Theme.chat_instantViewRectPaint.setColor(Color.argb((int) (this.button.getProgress() * 43.0f), red, green, blue));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Theme.chat_instantViewRectPaint);
            this.rect.set(0.0f, 0.0f, getMeasuredWidth(), AndroidUtilities.dp(74.0f));
            Theme.dialogs_onlineCirclePaint.setColor(Color.argb((int) ((1.0f - this.button.getProgress()) * 31.0f), red, green, blue));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Theme.dialogs_onlineCirclePaint);
            String string = LocaleController.getString(this.isThreeLines ? R.string.ChatListExpanded : R.string.ChatListDefault);
            int ceil = (int) Math.ceil(this.textPaint.measureText(string));
            this.textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
            int measuredWidth = getMeasuredWidth() - ceil;
            canvas.drawText(string, measuredWidth / 2, AndroidUtilities.dp(96.0f), this.textPaint);
            int i = 0;
            for (int i2 = 2; i < i2; i2 = 2) {
                int dp3 = AndroidUtilities.dp(i == 0 ? 21.0f : 53.0f);
                Theme.dialogs_onlineCirclePaint.setColor(Color.argb(i == 0 ? 204 : 90, red, green, blue));
                canvas.drawCircle(AndroidUtilities.dp(22.0f), dp3, AndroidUtilities.dp(11.0f), Theme.dialogs_onlineCirclePaint);
                int i3 = 0;
                while (true) {
                    if (i3 < (this.isThreeLines ? 3 : 2)) {
                        Theme.dialogs_onlineCirclePaint.setColor(Color.argb(i3 == 0 ? 204 : 90, red, green, blue));
                        if (this.isThreeLines) {
                            float f = i3 * 7;
                            this.rect.set(AndroidUtilities.dp(41.0f), dp3 - AndroidUtilities.dp(8.3f - f), getMeasuredWidth() - AndroidUtilities.dp(i3 == 0 ? 72.0f : 48.0f), dp3 - AndroidUtilities.dp(5.3f - f));
                            rectF = this.rect;
                            dp = AndroidUtilities.dpf2(1.5f);
                            dp2 = AndroidUtilities.dpf2(1.5f);
                        } else {
                            int i4 = i3 * 10;
                            this.rect.set(AndroidUtilities.dp(41.0f), dp3 - AndroidUtilities.dp(7 - i4), getMeasuredWidth() - AndroidUtilities.dp(i3 == 0 ? 72.0f : 48.0f), dp3 - AndroidUtilities.dp(3 - i4));
                            rectF = this.rect;
                            dp = AndroidUtilities.dp(2.0f);
                            dp2 = AndroidUtilities.dp(2.0f);
                        }
                        canvas.drawRoundRect(rectF, dp, dp2, Theme.dialogs_onlineCirclePaint);
                        i3++;
                    }
                }
                i++;
            }
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setClassName(RadioButton.class.getName());
            accessibilityNodeInfo.setChecked(this.button.isChecked());
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setContentDescription(LocaleController.getString(this.isThreeLines ? R.string.ChatListExpanded : R.string.ChatListDefault));
        }
    }

    public ChatListCell(Context context) {
        super(context);
        this.listView = new ListView[2];
        setOrientation(0);
        setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(21.0f), 0);
        int i = 0;
        while (true) {
            ListView[] listViewArr = this.listView;
            if (i >= listViewArr.length) {
                return;
            }
            final boolean z = i == 1;
            listViewArr[i] = new ListView(context, z);
            addView(this.listView[i], LayoutHelper.createLinear(-1, -1, 0.5f, i == 1 ? 10 : 0, 0, 0, 0));
            this.listView[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ChatListCell.this.lambda$new$0(z, view);
                }
            });
            i++;
        }
    }

    public void lambda$new$0(boolean z, View view) {
        for (int i = 0; i < 2; i++) {
            this.listView[i].button.setChecked(this.listView[i] == view, true);
        }
        didSelectChatType(z);
    }

    protected abstract void didSelectChatType(boolean z);

    @Override
    public void invalidate() {
        super.invalidate();
        int i = 0;
        while (true) {
            ListView[] listViewArr = this.listView;
            if (i >= listViewArr.length) {
                return;
            }
            listViewArr[i].invalidate();
            i++;
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(123.0f), 1073741824));
    }
}
