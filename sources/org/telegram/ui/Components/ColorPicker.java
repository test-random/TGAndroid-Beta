package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;

public class ColorPicker extends FrameLayout {
    private ImageView addButton;
    private Drawable circleDrawable;
    private Paint circlePaint;
    private boolean circlePressed;
    private ImageView clearButton;
    private EditTextBoldCursor[] colorEditText;
    private LinearGradient colorGradient;
    private float[] colorHSV;
    private boolean colorPressed;
    private Bitmap colorWheelBitmap;
    private Paint colorWheelPaint;
    private int colorWheelWidth;
    private AnimatorSet colorsAnimator;
    private int colorsCount;
    private int currentResetType;
    private final ColorPickerDelegate delegate;
    private float[] hsvTemp;
    boolean ignoreTextChange;
    private long lastUpdateTime;
    private Paint linePaint;
    private LinearLayout linearLayout;
    private float maxBrightness;
    private int maxColorsCount;
    private float maxHsvBrightness;
    private ActionBarMenuItem menuItem;
    private float minBrightness;
    private float minHsvBrightness;
    private boolean myMessagesColor;
    private float pressedMoveProgress;
    private int prevSelectedColor;
    private RadioButton[] radioButton;
    private FrameLayout radioContainer;
    private TextView resetButton;
    Theme.ResourcesProvider resourcesProvider;
    private int selectedColor;
    private RectF sliderRect;
    private Paint valueSliderPaint;

    public class AnonymousClass1 extends LinearLayout {
        private RectF rect = new RectF();
        private Paint paint = new Paint(1);

        AnonymousClass1(Context context) {
            super(context);
            this.rect = new RectF();
            this.paint = new Paint(1);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            this.paint.setColor(ColorPicker.this.getThemedColor(Theme.key_dialogBackgroundGray));
            this.rect.set(ColorPicker.this.colorEditText[0].getLeft() - AndroidUtilities.dp(13.0f), AndroidUtilities.dp(5.0f), r0 + ((int) (AndroidUtilities.dp(91.0f) + (ColorPicker.this.clearButton.getVisibility() == 0 ? AndroidUtilities.dp(25.0f) * ColorPicker.this.clearButton.getAlpha() : 0.0f))), AndroidUtilities.dp(37.0f));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), this.paint);
        }
    }

    public class AnonymousClass2 extends EditTextBoldCursor {
        final int val$num;

        AnonymousClass2(Context context, int i) {
            super(context);
            r3 = i;
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (getAlpha() == 1.0f && motionEvent.getAction() == 0) {
                if (ColorPicker.this.colorEditText[r3 + 1].isFocused()) {
                    AndroidUtilities.showKeyboard(ColorPicker.this.colorEditText[r3 + 1]);
                } else {
                    ColorPicker.this.colorEditText[r3 + 1].requestFocus();
                }
            }
            return false;
        }
    }

    public class AnonymousClass3 extends EditTextBoldCursor {
        final int val$num;

        AnonymousClass3(Context context, int i) {
            super(context);
            r3 = i;
        }

        @Override
        public boolean getGlobalVisibleRect(android.graphics.Rect rect, android.graphics.Point point) {
            boolean globalVisibleRect = super.getGlobalVisibleRect(rect, point);
            rect.bottom += AndroidUtilities.dp(40.0f);
            return globalVisibleRect;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            ColorPicker.this.colorEditText[r3 - 1].invalidate();
        }

        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (getAlpha() != 1.0f) {
                return false;
            }
            if (isFocused()) {
                AndroidUtilities.showKeyboard(this);
                return super.onTouchEvent(motionEvent);
            }
            requestFocus();
            return false;
        }
    }

    public class AnonymousClass4 implements TextWatcher {
        final int val$num;

        AnonymousClass4(int i) {
            r2 = i;
        }

        @Override
        public void afterTextChanged(Editable editable) {
            ColorPicker colorPicker = ColorPicker.this;
            if (colorPicker.ignoreTextChange) {
                return;
            }
            colorPicker.ignoreTextChange = true;
            int i = 0;
            while (i < editable.length()) {
                char charAt = editable.charAt(i);
                if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'f') && (charAt < 'A' || charAt > 'F'))) {
                    editable.replace(i, i + 1, "");
                    i--;
                }
                i++;
            }
            if (editable.length() != 0) {
                ColorPicker colorPicker2 = ColorPicker.this;
                colorPicker2.setColorInner(colorPicker2.getFieldColor(r2, -1));
                int color = ColorPicker.this.getColor();
                if (editable.length() == 6) {
                    editable.replace(0, editable.length(), String.format("%02x%02x%02x", Byte.valueOf((byte) Color.red(color)), Byte.valueOf((byte) Color.green(color)), Byte.valueOf((byte) Color.blue(color))).toUpperCase());
                    ColorPicker.this.colorEditText[r2].setSelection(editable.length());
                }
                ColorPicker.this.radioButton[ColorPicker.this.selectedColor].setColor(color);
                ColorPicker.this.delegate.setColor(color, ColorPicker.this.selectedColor, true);
            }
            ColorPicker.this.ignoreTextChange = false;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }
    }

    public class AnonymousClass5 extends AnimatorListenerAdapter {
        AnonymousClass5() {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (ColorPicker.this.colorsCount == ColorPicker.this.maxColorsCount) {
                ColorPicker.this.addButton.setVisibility(4);
            }
            ColorPicker.this.colorsAnimator = null;
        }
    }

    public class AnonymousClass6 extends ImageView {
        AnonymousClass6(Context context) {
            super(context);
        }

        @Override
        public void setAlpha(float f) {
            super.setAlpha(f);
            ColorPicker.this.linearLayout.invalidate();
        }
    }

    public class AnonymousClass7 extends AnimatorListenerAdapter {
        AnonymousClass7() {
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (ColorPicker.this.colorsCount == 1) {
                ColorPicker.this.clearButton.setVisibility(4);
            }
            for (int i = 0; i < ColorPicker.this.radioButton.length; i++) {
                if (ColorPicker.this.radioButton[i].getTag(R.id.index_tag) == null) {
                    ColorPicker.this.radioButton[i].setVisibility(4);
                }
            }
            ColorPicker.this.colorsAnimator = null;
        }
    }

    public class AnonymousClass8 extends AnimatorListenerAdapter {
        final boolean val$value;

        AnonymousClass8(boolean z) {
            r2 = z;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (r2) {
                return;
            }
            ColorPicker.this.resetButton.setVisibility(8);
        }
    }

    public class AnonymousClass9 extends AnimatorListenerAdapter {
        final int val$maxColorsCount;

        AnonymousClass9(int i) {
            r2 = i;
        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (r2 <= 1) {
                ColorPicker.this.clearButton.setVisibility(8);
            }
        }
    }

    public interface ColorPickerDelegate {

        public abstract class CC {
            public static void $default$deleteTheme(ColorPickerDelegate colorPickerDelegate) {
            }

            public static int $default$getDefaultColor(ColorPickerDelegate colorPickerDelegate, int i) {
                return 0;
            }

            public static void $default$openThemeCreate(ColorPickerDelegate colorPickerDelegate, boolean z) {
            }
        }

        void deleteTheme();

        int getDefaultColor(int i);

        void openThemeCreate(boolean z);

        void setColor(int i, int i2, boolean z);
    }

    public static class RadioButton extends View {
        private ObjectAnimator checkAnimator;
        private boolean checked;
        private float checkedState;
        private int currentColor;
        private final Paint paint;

        public RadioButton(Context context) {
            super(context);
            this.paint = new Paint(1);
        }

        public int getColor() {
            return this.currentColor;
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            float dp = AndroidUtilities.dp(15.0f);
            float measuredWidth = getMeasuredWidth() * 0.5f;
            float measuredHeight = getMeasuredHeight() * 0.5f;
            this.paint.setColor(this.currentColor);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeWidth(AndroidUtilities.dp(3.0f));
            this.paint.setAlpha(Math.round(this.checkedState * 255.0f));
            canvas.drawCircle(measuredWidth, measuredHeight, dp - (this.paint.getStrokeWidth() * 0.5f), this.paint);
            this.paint.setAlpha(255);
            this.paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(measuredWidth, measuredHeight, dp - (AndroidUtilities.dp(5.0f) * this.checkedState), this.paint);
        }

        @Override
        public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
            super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            accessibilityNodeInfo.setText(LocaleController.getString(R.string.ColorPickerMainColor));
            accessibilityNodeInfo.setClassName(Button.class.getName());
            accessibilityNodeInfo.setChecked(this.checked);
            accessibilityNodeInfo.setCheckable(true);
            accessibilityNodeInfo.setEnabled(true);
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(30.0f), 1073741824));
        }

        public void setChecked(boolean z, boolean z2) {
            this.checked = z;
            updateCheckedState(z2);
        }

        public void setCheckedState(float f) {
            this.checkedState = f;
            invalidate();
        }

        public void setColor(int i) {
            this.currentColor = i;
            invalidate();
        }

        void updateCheckedState(boolean z) {
            ObjectAnimator objectAnimator = this.checkAnimator;
            if (objectAnimator != null) {
                objectAnimator.cancel();
            }
            if (!z) {
                setCheckedState(this.checked ? 1.0f : 0.0f);
                return;
            }
            ObjectAnimator ofFloat = ObjectAnimator.ofFloat(this, "checkedState", this.checked ? 1.0f : 0.0f);
            this.checkAnimator = ofFloat;
            ofFloat.setDuration(200L);
            this.checkAnimator.start();
        }
    }

    public ColorPicker(Context context, boolean z, ColorPickerDelegate colorPickerDelegate) {
        super(context);
        this.sliderRect = new RectF();
        this.radioButton = new RadioButton[4];
        this.colorsCount = 1;
        this.maxColorsCount = 1;
        this.colorHSV = new float[]{0.0f, 0.0f, 1.0f};
        this.hsvTemp = new float[3];
        this.pressedMoveProgress = 1.0f;
        this.minBrightness = 0.0f;
        this.maxBrightness = 1.0f;
        this.minHsvBrightness = 0.0f;
        this.maxHsvBrightness = 1.0f;
        this.delegate = colorPickerDelegate;
        this.colorEditText = new EditTextBoldCursor[2];
        setWillNotDraw(false);
        this.circleDrawable = context.getResources().getDrawable(R.drawable.knob_shadow).mutate();
        this.circlePaint = new Paint(1);
        this.colorWheelPaint = new Paint(5);
        this.valueSliderPaint = new Paint(5);
        Paint paint = new Paint();
        this.linePaint = paint;
        paint.setColor(301989888);
        setClipChildren(false);
        AnonymousClass1 anonymousClass1 = new LinearLayout(context) {
            private RectF rect = new RectF();
            private Paint paint = new Paint(1);

            AnonymousClass1(Context context2) {
                super(context2);
                this.rect = new RectF();
                this.paint = new Paint(1);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                this.paint.setColor(ColorPicker.this.getThemedColor(Theme.key_dialogBackgroundGray));
                this.rect.set(ColorPicker.this.colorEditText[0].getLeft() - AndroidUtilities.dp(13.0f), AndroidUtilities.dp(5.0f), r0 + ((int) (AndroidUtilities.dp(91.0f) + (ColorPicker.this.clearButton.getVisibility() == 0 ? AndroidUtilities.dp(25.0f) * ColorPicker.this.clearButton.getAlpha() : 0.0f))), AndroidUtilities.dp(37.0f));
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), this.paint);
            }
        };
        this.linearLayout = anonymousClass1;
        anonymousClass1.setOrientation(0);
        addView(this.linearLayout, LayoutHelper.createFrame(-1, 54.0f, 51, 27.0f, -6.0f, 17.0f, 0.0f));
        this.linearLayout.setWillNotDraw(false);
        FrameLayout frameLayout = new FrameLayout(context2);
        this.radioContainer = frameLayout;
        frameLayout.setClipChildren(false);
        addView(this.radioContainer, LayoutHelper.createFrame(174, 30.0f, 49, 72.0f, 1.0f, 0.0f, 0.0f));
        int i = 0;
        while (i < 4) {
            this.radioButton[i] = new RadioButton(context2);
            this.radioButton[i].setChecked(this.selectedColor == i, false);
            this.radioContainer.addView(this.radioButton[i], LayoutHelper.createFrame(30, 30.0f, 48, 0.0f, 0.0f, 0.0f, 0.0f));
            this.radioButton[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ColorPicker.this.lambda$new$0(view);
                }
            });
            i++;
        }
        int i2 = 0;
        while (true) {
            EditTextBoldCursor[] editTextBoldCursorArr = this.colorEditText;
            if (i2 >= editTextBoldCursorArr.length) {
                break;
            }
            if (i2 % 2 == 0) {
                editTextBoldCursorArr[i2] = new EditTextBoldCursor(context2) {
                    final int val$num;

                    AnonymousClass2(Context context2, int i22) {
                        super(context2);
                        r3 = i22;
                    }

                    @Override
                    public boolean onTouchEvent(MotionEvent motionEvent) {
                        if (getAlpha() == 1.0f && motionEvent.getAction() == 0) {
                            if (ColorPicker.this.colorEditText[r3 + 1].isFocused()) {
                                AndroidUtilities.showKeyboard(ColorPicker.this.colorEditText[r3 + 1]);
                            } else {
                                ColorPicker.this.colorEditText[r3 + 1].requestFocus();
                            }
                        }
                        return false;
                    }
                };
                this.colorEditText[i22].setBackgroundDrawable(null);
                this.colorEditText[i22].setText("#");
                this.colorEditText[i22].setEnabled(false);
                this.colorEditText[i22].setFocusable(false);
                this.colorEditText[i22].setPadding(0, AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(16.0f));
                this.linearLayout.addView(this.colorEditText[i22], LayoutHelper.createLinear(-2, -1, 0.0f, 0.0f, 0.0f, 0.0f));
            } else {
                editTextBoldCursorArr[i22] = new EditTextBoldCursor(context2) {
                    final int val$num;

                    AnonymousClass3(Context context2, int i22) {
                        super(context2);
                        r3 = i22;
                    }

                    @Override
                    public boolean getGlobalVisibleRect(android.graphics.Rect rect, android.graphics.Point point) {
                        boolean globalVisibleRect = super.getGlobalVisibleRect(rect, point);
                        rect.bottom += AndroidUtilities.dp(40.0f);
                        return globalVisibleRect;
                    }

                    @Override
                    public void invalidate() {
                        super.invalidate();
                        ColorPicker.this.colorEditText[r3 - 1].invalidate();
                    }

                    @Override
                    public boolean onTouchEvent(MotionEvent motionEvent) {
                        if (getAlpha() != 1.0f) {
                            return false;
                        }
                        if (isFocused()) {
                            AndroidUtilities.showKeyboard(this);
                            return super.onTouchEvent(motionEvent);
                        }
                        requestFocus();
                        return false;
                    }
                };
                this.colorEditText[i22].setBackgroundDrawable(null);
                this.colorEditText[i22].setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                this.colorEditText[i22].setHint("8BC6ED");
                this.colorEditText[i22].setPadding(0, AndroidUtilities.dp(5.0f), 0, AndroidUtilities.dp(16.0f));
                this.linearLayout.addView(this.colorEditText[i22], LayoutHelper.createLinear(71, -1, 0.0f, 0.0f, 0.0f, 0.0f));
                this.colorEditText[i22].addTextChangedListener(new TextWatcher() {
                    final int val$num;

                    AnonymousClass4(int i22) {
                        r2 = i22;
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        ColorPicker colorPicker = ColorPicker.this;
                        if (colorPicker.ignoreTextChange) {
                            return;
                        }
                        colorPicker.ignoreTextChange = true;
                        int i3 = 0;
                        while (i3 < editable.length()) {
                            char charAt = editable.charAt(i3);
                            if ((charAt < '0' || charAt > '9') && ((charAt < 'a' || charAt > 'f') && (charAt < 'A' || charAt > 'F'))) {
                                editable.replace(i3, i3 + 1, "");
                                i3--;
                            }
                            i3++;
                        }
                        if (editable.length() != 0) {
                            ColorPicker colorPicker2 = ColorPicker.this;
                            colorPicker2.setColorInner(colorPicker2.getFieldColor(r2, -1));
                            int color = ColorPicker.this.getColor();
                            if (editable.length() == 6) {
                                editable.replace(0, editable.length(), String.format("%02x%02x%02x", Byte.valueOf((byte) Color.red(color)), Byte.valueOf((byte) Color.green(color)), Byte.valueOf((byte) Color.blue(color))).toUpperCase());
                                ColorPicker.this.colorEditText[r2].setSelection(editable.length());
                            }
                            ColorPicker.this.radioButton[ColorPicker.this.selectedColor].setColor(color);
                            ColorPicker.this.delegate.setColor(color, ColorPicker.this.selectedColor, true);
                        }
                        ColorPicker.this.ignoreTextChange = false;
                    }

                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i3, int i22, int i32) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i3, int i22, int i32) {
                    }
                });
                this.colorEditText[i22].setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public final boolean onEditorAction(TextView textView, int i3, KeyEvent keyEvent) {
                        boolean lambda$new$1;
                        lambda$new$1 = ColorPicker.lambda$new$1(textView, i3, keyEvent);
                        return lambda$new$1;
                    }
                });
            }
            this.colorEditText[i22].setTextSize(1, 16.0f);
            this.colorEditText[i22].setHintTextColor(getThemedColor(Theme.key_windowBackgroundWhiteHintText));
            EditTextBoldCursor editTextBoldCursor = this.colorEditText[i22];
            int i3 = Theme.key_windowBackgroundWhiteBlackText;
            editTextBoldCursor.setTextColor(getThemedColor(i3));
            this.colorEditText[i22].setCursorColor(getThemedColor(i3));
            this.colorEditText[i22].setCursorSize(AndroidUtilities.dp(18.0f));
            this.colorEditText[i22].setCursorWidth(1.5f);
            this.colorEditText[i22].setSingleLine(true);
            this.colorEditText[i22].setGravity(19);
            this.colorEditText[i22].setHeaderHintColor(getThemedColor(Theme.key_windowBackgroundWhiteBlueHeader));
            this.colorEditText[i22].setTransformHintToHeader(true);
            this.colorEditText[i22].setInputType(524416);
            this.colorEditText[i22].setImeOptions(268435462);
            if (i22 == 1) {
                this.colorEditText[i22].requestFocus();
            } else if (i22 == 2 || i22 == 3) {
                this.colorEditText[i22].setVisibility(8);
            }
            i22++;
        }
        ImageView imageView = new ImageView(getContext());
        this.addButton = imageView;
        int i4 = Theme.key_dialogButtonSelector;
        imageView.setBackground(Theme.createSelectorDrawable(getThemedColor(i4), 1));
        this.addButton.setImageResource(R.drawable.msg_add);
        ImageView imageView2 = this.addButton;
        int i5 = Theme.key_windowBackgroundWhiteBlackText;
        int themedColor = getThemedColor(i5);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        imageView2.setColorFilter(new PorterDuffColorFilter(themedColor, mode));
        ImageView imageView3 = this.addButton;
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        imageView3.setScaleType(scaleType);
        this.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ColorPicker.this.lambda$new$2(view);
            }
        });
        this.addButton.setContentDescription(LocaleController.getString(R.string.Add));
        addView(this.addButton, LayoutHelper.createFrame(30, 30.0f, 49, 36.0f, 1.0f, 0.0f, 0.0f));
        AnonymousClass6 anonymousClass6 = new ImageView(getContext()) {
            AnonymousClass6(Context context2) {
                super(context2);
            }

            @Override
            public void setAlpha(float f) {
                super.setAlpha(f);
                ColorPicker.this.linearLayout.invalidate();
            }
        };
        this.clearButton = anonymousClass6;
        anonymousClass6.setBackground(Theme.createSelectorDrawable(getThemedColor(i4), 1));
        this.clearButton.setImageResource(R.drawable.msg_close);
        this.clearButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(i5), mode));
        this.clearButton.setAlpha(0.0f);
        this.clearButton.setScaleX(0.0f);
        this.clearButton.setScaleY(0.0f);
        this.clearButton.setScaleType(scaleType);
        this.clearButton.setVisibility(4);
        this.clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ColorPicker.this.lambda$new$3(view);
            }
        });
        this.clearButton.setContentDescription(LocaleController.getString(R.string.ClearButton));
        addView(this.clearButton, LayoutHelper.createFrame(30, 30.0f, 51, 97.0f, 1.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context2);
        this.resetButton = textView;
        textView.setTextSize(1, 15.0f);
        this.resetButton.setTypeface(AndroidUtilities.bold());
        this.resetButton.setGravity(17);
        this.resetButton.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
        this.resetButton.setTextColor(getThemedColor(i5));
        addView(this.resetButton, LayoutHelper.createFrame(-2, 36.0f, 53, 0.0f, 3.0f, 14.0f, 0.0f));
        this.resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                ColorPicker.lambda$new$4(view);
            }
        });
        if (z) {
            ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context2, null, 0, getThemedColor(i5));
            this.menuItem = actionBarMenuItem;
            actionBarMenuItem.setLongClickEnabled(false);
            this.menuItem.setIcon(R.drawable.ic_ab_other);
            this.menuItem.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
            this.menuItem.addSubItem(1, R.drawable.msg_edit, LocaleController.getString(R.string.OpenInEditor));
            this.menuItem.addSubItem(2, R.drawable.msg_share, LocaleController.getString(R.string.ShareTheme));
            this.menuItem.addSubItem(3, R.drawable.msg_delete, LocaleController.getString(R.string.DeleteTheme));
            this.menuItem.setMenuYOffset(-AndroidUtilities.dp(80.0f));
            this.menuItem.setSubMenuOpenSide(2);
            this.menuItem.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() {
                @Override
                public final void onItemClick(int i6) {
                    ColorPicker.this.lambda$new$5(i6);
                }
            });
            this.menuItem.setAdditionalYOffset(AndroidUtilities.dp(72.0f));
            this.menuItem.setTranslationX(AndroidUtilities.dp(6.0f));
            this.menuItem.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(i4), 1));
            addView(this.menuItem, LayoutHelper.createFrame(30, 30.0f, 53, 0.0f, 2.0f, 10.0f, 0.0f));
            this.menuItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    ColorPicker.this.lambda$new$6(view);
                }
            });
        }
        updateColorsPosition(null, 0, false, getMeasuredWidth());
    }

    private Bitmap createColorWheelBitmap(int i, int i2) {
        Bitmap createBitmap = Bitmap.createBitmap(i, i2, Bitmap.Config.ARGB_8888);
        float f = i;
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        float f2 = i2;
        this.colorWheelPaint.setShader(new ComposeShader(new LinearGradient(0.0f, i2 / 3, 0.0f, f2, new int[]{-1, 0}, (float[]) null, tileMode), new LinearGradient(0.0f, 0.0f, f, 0.0f, new int[]{-65536, -256, -16711936, -16711681, -16776961, -65281, -65536}, (float[]) null, tileMode), PorterDuff.Mode.MULTIPLY));
        new Canvas(createBitmap).drawRect(0.0f, 0.0f, f, f2, this.colorWheelPaint);
        return createBitmap;
    }

    private void drawPointerArrow(Canvas canvas, int i, int i2, int i3, boolean z) {
        int dp = AndroidUtilities.dp(z ? 12.0f : 16.0f);
        this.circleDrawable.setBounds(i - dp, i2 - dp, i + dp, dp + i2);
        this.circleDrawable.draw(canvas);
        this.circlePaint.setColor(-1);
        float f = i;
        float f2 = i2;
        canvas.drawCircle(f, f2, AndroidUtilities.dp(z ? 11.0f : 15.0f), this.circlePaint);
        this.circlePaint.setColor(i3);
        canvas.drawCircle(f, f2, AndroidUtilities.dp(z ? 9.0f : 13.0f), this.circlePaint);
    }

    public static int generateGradientColors(int i) {
        float[] fArr = new float[3];
        Color.colorToHSV(i, fArr);
        float f = fArr[1];
        if (f > 0.5f) {
            fArr[1] = f - 0.15f;
        } else {
            fArr[1] = f + 0.15f;
        }
        float f2 = fArr[0];
        if (f2 > 180.0f) {
            fArr[0] = f2 - 20.0f;
        } else {
            fArr[0] = f2 + 20.0f;
        }
        return Color.HSVToColor(255, fArr);
    }

    private float getBrightness() {
        return Math.max(this.minHsvBrightness, Math.min(this.colorHSV[2], this.maxHsvBrightness));
    }

    public int getFieldColor(int i, int i2) {
        try {
            return Integer.parseInt(this.colorEditText[i].getText().toString(), 16) | (-16777216);
        } catch (Exception unused) {
            return i2;
        }
    }

    public int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void lambda$new$0(View view) {
        RadioButton radioButton = (RadioButton) view;
        int i = 0;
        while (true) {
            RadioButton[] radioButtonArr = this.radioButton;
            if (i >= radioButtonArr.length) {
                int color = radioButton.getColor();
                setColorInner(color);
                this.colorEditText[1].setText(String.format("%02x%02x%02x", Byte.valueOf((byte) Color.red(color)), Byte.valueOf((byte) Color.green(color)), Byte.valueOf((byte) Color.blue(color))).toUpperCase());
                return;
            } else {
                RadioButton radioButton2 = radioButtonArr[i];
                boolean z = radioButton2 == radioButton;
                radioButton2.setChecked(z, true);
                if (z) {
                    this.prevSelectedColor = this.selectedColor;
                    this.selectedColor = i;
                }
                i++;
            }
        }
    }

    public static boolean lambda$new$1(TextView textView, int i, KeyEvent keyEvent) {
        if (i != 6) {
            return false;
        }
        AndroidUtilities.hideKeyboard(textView);
        return true;
    }

    public void lambda$new$2(View view) {
        ObjectAnimator ofFloat;
        if (this.colorsAnimator != null) {
            return;
        }
        int i = this.colorsCount;
        if (i == 1) {
            if (this.radioButton[1].getColor() == 0) {
                RadioButton[] radioButtonArr = this.radioButton;
                radioButtonArr[1].setColor(generateGradientColors(radioButtonArr[0].getColor()));
            }
            if (this.myMessagesColor) {
                this.delegate.setColor(this.radioButton[0].getColor(), 0, true);
            }
            this.delegate.setColor(this.radioButton[1].getColor(), 1, true);
            this.colorsCount = 2;
        } else if (i == 2) {
            this.colorsCount = 3;
            if (this.radioButton[2].getColor() == 0) {
                float[] fArr = new float[3];
                Color.colorToHSV(this.radioButton[0].getColor(), fArr);
                float f = fArr[0];
                if (f > 180.0f) {
                    fArr[0] = f - 60.0f;
                } else {
                    fArr[0] = f + 60.0f;
                }
                this.radioButton[2].setColor(Color.HSVToColor(255, fArr));
            }
            this.delegate.setColor(this.radioButton[2].getColor(), 2, true);
        } else {
            if (i != 3) {
                return;
            }
            this.colorsCount = 4;
            if (this.radioButton[3].getColor() == 0) {
                RadioButton[] radioButtonArr2 = this.radioButton;
                radioButtonArr2[3].setColor(generateGradientColors(radioButtonArr2[2].getColor()));
            }
            this.delegate.setColor(this.radioButton[3].getColor(), 3, true);
        }
        ArrayList arrayList = new ArrayList();
        if (this.colorsCount < this.maxColorsCount) {
            arrayList.add(ObjectAnimator.ofFloat(this.addButton, (Property<ImageView, Float>) View.ALPHA, 1.0f));
            arrayList.add(ObjectAnimator.ofFloat(this.addButton, (Property<ImageView, Float>) View.SCALE_X, 1.0f));
            arrayList.add(ObjectAnimator.ofFloat(this.addButton, (Property<ImageView, Float>) View.SCALE_Y, 1.0f));
            ofFloat = ObjectAnimator.ofFloat(this.addButton, (Property<ImageView, Float>) View.TRANSLATION_X, (AndroidUtilities.dp(30.0f) * (this.colorsCount - 1)) + (AndroidUtilities.dp(13.0f) * (this.colorsCount - 1)));
        } else {
            arrayList.add(ObjectAnimator.ofFloat(this.addButton, (Property<ImageView, Float>) View.TRANSLATION_X, (AndroidUtilities.dp(30.0f) * (this.colorsCount - 1)) + (AndroidUtilities.dp(13.0f) * (this.colorsCount - 1))));
            arrayList.add(ObjectAnimator.ofFloat(this.addButton, (Property<ImageView, Float>) View.ALPHA, 0.0f));
            arrayList.add(ObjectAnimator.ofFloat(this.addButton, (Property<ImageView, Float>) View.SCALE_X, 0.0f));
            ofFloat = ObjectAnimator.ofFloat(this.addButton, (Property<ImageView, Float>) View.SCALE_Y, 0.0f);
        }
        arrayList.add(ofFloat);
        if (this.colorsCount > 1) {
            if (this.clearButton.getVisibility() != 0) {
                this.clearButton.setScaleX(0.0f);
                this.clearButton.setScaleY(0.0f);
            }
            this.clearButton.setVisibility(0);
            arrayList.add(ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) View.ALPHA, 1.0f));
            arrayList.add(ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) View.SCALE_X, 1.0f));
            arrayList.add(ObjectAnimator.ofFloat(this.clearButton, (Property<ImageView, Float>) View.SCALE_Y, 1.0f));
        }
        this.radioButton[this.colorsCount - 1].callOnClick();
        this.colorsAnimator = new AnimatorSet();
        updateColorsPosition(arrayList, 0, false, getMeasuredWidth());
        this.colorsAnimator.playTogether(arrayList);
        this.colorsAnimator.setDuration(180L);
        this.colorsAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.colorsAnimator.addListener(new AnimatorListenerAdapter() {
            AnonymousClass5() {
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                if (ColorPicker.this.colorsCount == ColorPicker.this.maxColorsCount) {
                    ColorPicker.this.addButton.setVisibility(4);
                }
                ColorPicker.this.colorsAnimator = null;
            }
        });
        this.colorsAnimator.start();
    }

    public void lambda$new$3(android.view.View r10) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ColorPicker.lambda$new$3(android.view.View):void");
    }

    public static void lambda$new$4(View view) {
    }

    public void lambda$new$5(int i) {
        if (i == 1 || i == 2) {
            this.delegate.openThemeCreate(i == 2);
        } else if (i == 3) {
            this.delegate.deleteTheme();
        }
    }

    public void lambda$new$6(View view) {
        this.menuItem.toggleSubMenu();
    }

    public void lambda$provideThemeDescriptions$7() {
        this.menuItem.setIconColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        Theme.setDrawableColor(this.menuItem.getBackground(), getThemedColor(Theme.key_dialogButtonSelector));
        this.menuItem.setPopupItemsColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem), false);
        this.menuItem.setPopupItemsColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon), true);
        this.menuItem.redrawPopup(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
    }

    public void setColorInner(int i) {
        Color.colorToHSV(i, this.colorHSV);
        int defaultColor = this.delegate.getDefaultColor(this.selectedColor);
        if (defaultColor == 0 || defaultColor != i) {
            updateHsvMinMaxBrightness();
        }
        this.colorGradient = null;
        invalidate();
    }

    private void updateColorsPosition(ArrayList arrayList, int i, boolean z, int i2) {
        RadioButton radioButton;
        Integer num;
        RadioButton radioButton2;
        int i3 = this.colorsCount;
        float f = this.radioContainer.getLeft() + ((AndroidUtilities.dp(30.0f) * i3) + ((i3 - 1) * AndroidUtilities.dp(13.0f))) > i2 - AndroidUtilities.dp(this.currentResetType == 1 ? 50.0f : 0.0f) ? r0 - r14 : 0.0f;
        FrameLayout frameLayout = this.radioContainer;
        if (arrayList != null) {
            arrayList.add(ObjectAnimator.ofFloat(frameLayout, (Property<FrameLayout, Float>) View.TRANSLATION_X, -f));
        } else {
            frameLayout.setTranslationX(-f);
        }
        int i4 = 0;
        int i5 = 0;
        while (true) {
            RadioButton[] radioButtonArr = this.radioButton;
            if (i4 >= radioButtonArr.length) {
                return;
            }
            RadioButton radioButton3 = radioButtonArr[i4];
            int i6 = R.id.index_tag;
            boolean z2 = radioButton3.getTag(i6) != null;
            if (i4 < this.colorsCount) {
                this.radioButton[i4].setVisibility(0);
                if (arrayList != null) {
                    if (!z2) {
                        arrayList.add(ObjectAnimator.ofFloat(this.radioButton[i4], (Property<RadioButton, Float>) View.ALPHA, 1.0f));
                        arrayList.add(ObjectAnimator.ofFloat(this.radioButton[i4], (Property<RadioButton, Float>) View.SCALE_X, 1.0f));
                        arrayList.add(ObjectAnimator.ofFloat(this.radioButton[i4], (Property<RadioButton, Float>) View.SCALE_Y, 1.0f));
                    }
                    if (z || !(z || i4 == this.colorsCount - 1)) {
                        arrayList.add(ObjectAnimator.ofFloat(this.radioButton[i4], (Property<RadioButton, Float>) View.TRANSLATION_X, i5));
                        radioButton = this.radioButton[i4];
                        num = 1;
                    } else {
                        radioButton2 = this.radioButton[i4];
                    }
                } else {
                    this.radioButton[i4].setVisibility(0);
                    if (this.colorsAnimator == null) {
                        this.radioButton[i4].setAlpha(1.0f);
                        this.radioButton[i4].setScaleX(1.0f);
                        this.radioButton[i4].setScaleY(1.0f);
                    }
                    radioButton2 = this.radioButton[i4];
                }
                radioButton2.setTranslationX(i5);
                radioButton = this.radioButton[i4];
                num = 1;
            } else {
                if (arrayList == null) {
                    this.radioButton[i4].setVisibility(4);
                    if (this.colorsAnimator == null) {
                        this.radioButton[i4].setAlpha(0.0f);
                        this.radioButton[i4].setScaleX(0.0f);
                        this.radioButton[i4].setScaleY(0.0f);
                    }
                } else if (z2) {
                    arrayList.add(ObjectAnimator.ofFloat(this.radioButton[i4], (Property<RadioButton, Float>) View.ALPHA, 0.0f));
                    arrayList.add(ObjectAnimator.ofFloat(this.radioButton[i4], (Property<RadioButton, Float>) View.SCALE_X, 0.0f));
                    arrayList.add(ObjectAnimator.ofFloat(this.radioButton[i4], (Property<RadioButton, Float>) View.SCALE_Y, 0.0f));
                }
                if (!z) {
                    this.radioButton[i4].setTranslationX(i5);
                }
                radioButton = this.radioButton[i4];
                num = null;
            }
            radioButton.setTag(i6, num);
            i5 += AndroidUtilities.dp(30.0f) + AndroidUtilities.dp(13.0f);
            i4++;
        }
    }

    private void updateHsvMinMaxBrightness() {
        ImageView imageView = this.clearButton;
        if (imageView == null) {
            return;
        }
        float f = imageView.getTag() != null ? 0.0f : this.minBrightness;
        float f2 = this.clearButton.getTag() != null ? 1.0f : this.maxBrightness;
        float[] fArr = this.colorHSV;
        float f3 = fArr[2];
        if (f == 0.0f && f2 == 1.0f) {
            this.minHsvBrightness = 0.0f;
            this.maxHsvBrightness = 1.0f;
            return;
        }
        fArr[2] = 1.0f;
        int HSVToColor = Color.HSVToColor(fArr);
        this.colorHSV[2] = f3;
        float computePerceivedBrightness = AndroidUtilities.computePerceivedBrightness(HSVToColor);
        float max = Math.max(0.0f, Math.min(f / computePerceivedBrightness, 1.0f));
        this.minHsvBrightness = max;
        this.maxHsvBrightness = Math.max(max, Math.min(f2 / computePerceivedBrightness, 1.0f));
    }

    public int getColor() {
        float[] fArr = this.hsvTemp;
        float[] fArr2 = this.colorHSV;
        fArr[0] = fArr2[0];
        fArr[1] = fArr2[1];
        fArr[2] = getBrightness();
        return (Color.HSVToColor(this.hsvTemp) & 16777215) | (-16777216);
    }

    public void hideKeyboard() {
        AndroidUtilities.hideKeyboard(this.colorEditText[1]);
    }

    @Override
    public void invalidate() {
        super.invalidate();
        this.linearLayout.invalidate();
    }

    @Override
    protected void onDraw(android.graphics.Canvas r22) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ColorPicker.onDraw(android.graphics.Canvas):void");
    }

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateColorsPosition(null, 0, false, getMeasuredWidth());
    }

    @Override
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        if (this.colorWheelWidth != i) {
            this.colorWheelWidth = i;
            this.colorWheelBitmap = createColorWheelBitmap(i, AndroidUtilities.dp(180.0f));
            this.colorGradient = null;
        }
    }

    @Override
    public boolean onTouchEvent(android.view.MotionEvent r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ColorPicker.onTouchEvent(android.view.MotionEvent):boolean");
    }

    public void provideThemeDescriptions(List list) {
        for (int i = 0; i < this.colorEditText.length; i++) {
            EditTextBoldCursor editTextBoldCursor = this.colorEditText[i];
            int i2 = ThemeDescription.FLAG_TEXTCOLOR;
            int i3 = Theme.key_windowBackgroundWhiteBlackText;
            list.add(new ThemeDescription(editTextBoldCursor, i2, null, null, null, null, i3));
            list.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_CURSORCOLOR, null, null, null, null, i3));
            list.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteHintText));
            list.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_PROGRESSBAR | ThemeDescription.FLAG_HINTTEXTCOLOR, null, null, null, null, Theme.key_windowBackgroundWhiteBlueHeader));
            list.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
            list.add(new ThemeDescription(this.colorEditText[i], ThemeDescription.FLAG_DRAWABLESELECTEDSTATE | ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_windowBackgroundWhiteInputFieldActivated));
        }
        ImageView imageView = this.clearButton;
        int i4 = ThemeDescription.FLAG_IMAGECOLOR;
        int i5 = Theme.key_windowBackgroundWhiteBlackText;
        list.add(new ThemeDescription(imageView, i4, null, null, null, null, i5));
        ImageView imageView2 = this.clearButton;
        int i6 = ThemeDescription.FLAG_BACKGROUNDFILTER;
        int i7 = Theme.key_dialogButtonSelector;
        list.add(new ThemeDescription(imageView2, i6, null, null, null, null, i7));
        if (this.menuItem != null) {
            ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() {
                @Override
                public final void didSetColor() {
                    ColorPicker.this.lambda$provideThemeDescriptions$7();
                }

                @Override
                public void onAnimationProgress(float f) {
                    ThemeDescription.ThemeDescriptionDelegate.CC.$default$onAnimationProgress(this, f);
                }
            };
            list.add(new ThemeDescription(this.menuItem, 0, null, null, null, themeDescriptionDelegate, i5));
            list.add(new ThemeDescription(this.menuItem, 0, null, null, null, themeDescriptionDelegate, i7));
            list.add(new ThemeDescription(this.menuItem, 0, null, null, null, themeDescriptionDelegate, Theme.key_actionBarDefaultSubmenuItem));
            list.add(new ThemeDescription(this.menuItem, 0, null, null, null, themeDescriptionDelegate, Theme.key_actionBarDefaultSubmenuItemIcon));
            list.add(new ThemeDescription(this.menuItem, 0, null, null, null, themeDescriptionDelegate, Theme.key_actionBarDefaultSubmenuBackground));
        }
    }

    public void setColor(int i, int i2) {
        if (!this.ignoreTextChange) {
            this.ignoreTextChange = true;
            if (this.selectedColor == i2) {
                String upperCase = String.format("%02x%02x%02x", Byte.valueOf((byte) Color.red(i)), Byte.valueOf((byte) Color.green(i)), Byte.valueOf((byte) Color.blue(i))).toUpperCase();
                this.colorEditText[1].setText(upperCase);
                this.colorEditText[1].setSelection(upperCase.length());
            }
            this.radioButton[i2].setColor(i);
            this.ignoreTextChange = false;
        }
        setColorInner(i);
    }

    public void setHasChanges(boolean z) {
        if (!z || this.resetButton.getTag() == null) {
            if ((z || this.resetButton.getTag() != null) && this.clearButton.getTag() == null) {
                this.resetButton.setTag(z ? 1 : null);
                AnimatorSet animatorSet = new AnimatorSet();
                ArrayList arrayList = new ArrayList();
                if (z) {
                    this.resetButton.setVisibility(0);
                }
                arrayList.add(ObjectAnimator.ofFloat(this.resetButton, (Property<TextView, Float>) View.ALPHA, z ? 1.0f : 0.0f));
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    final boolean val$value;

                    AnonymousClass8(boolean z2) {
                        r2 = z2;
                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (r2) {
                            return;
                        }
                        ColorPicker.this.resetButton.setVisibility(8);
                    }
                });
                animatorSet.playTogether(arrayList);
                animatorSet.setDuration(180L);
                animatorSet.start();
            }
        }
    }

    public void setMaxBrightness(float f) {
        this.maxBrightness = f;
        updateHsvMinMaxBrightness();
    }

    public void setMinBrightness(float f) {
        this.minBrightness = f;
        updateHsvMinMaxBrightness();
    }

    public void setResourcesProvider(Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
    }

    public void setType(int r5, boolean r6, int r7, int r8, boolean r9, int r10, boolean r11) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.ColorPicker.setType(int, boolean, int, int, boolean, int, boolean):void");
    }
}
