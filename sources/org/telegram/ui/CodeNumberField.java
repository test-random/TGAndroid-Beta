package org.telegram.ui;

import android.animation.ValueAnimator;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.text.Editable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextWatcher;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import androidx.core.content.ContextCompat;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.SimpleFloatPropertyCompat;

public abstract class CodeNumberField extends EditTextBoldCursor {
    float enterAnimation;
    ValueAnimator enterAnimator;
    private float errorProgress;
    private SpringAnimation errorSpringAnimation;
    float exitAnimation;
    ValueAnimator exitAnimator;
    Bitmap exitBitmap;
    Canvas exitCanvas;
    private float focusedProgress;
    private SpringAnimation focusedSpringAnimation;
    boolean pressed;
    boolean replaceAnimation;
    private boolean showSoftInputOnFocusInternal;
    float startX;
    float startY;
    private float successProgress;
    private float successScaleProgress;
    private SpringAnimation successScaleSpringAnimation;
    private SpringAnimation successSpringAnimation;
    private static final FloatPropertyCompat FOCUSED_PROGRESS = new SimpleFloatPropertyCompat("focusedProgress", new SimpleFloatPropertyCompat.Getter() {
        @Override
        public final float get(Object obj) {
            float f;
            f = ((CodeNumberField) obj).focusedProgress;
            return f;
        }
    }, new SimpleFloatPropertyCompat.Setter() {
        @Override
        public final void set(Object obj, float f) {
            CodeNumberField.lambda$static$1((CodeNumberField) obj, f);
        }
    }).setMultiplier(100.0f);
    private static final FloatPropertyCompat ERROR_PROGRESS = new SimpleFloatPropertyCompat("errorProgress", new SimpleFloatPropertyCompat.Getter() {
        @Override
        public final float get(Object obj) {
            float f;
            f = ((CodeNumberField) obj).errorProgress;
            return f;
        }
    }, new SimpleFloatPropertyCompat.Setter() {
        @Override
        public final void set(Object obj, float f) {
            CodeNumberField.lambda$static$3((CodeNumberField) obj, f);
        }
    }).setMultiplier(100.0f);
    private static final FloatPropertyCompat SUCCESS_PROGRESS = new SimpleFloatPropertyCompat("successProgress", new SimpleFloatPropertyCompat.Getter() {
        @Override
        public final float get(Object obj) {
            float f;
            f = ((CodeNumberField) obj).successProgress;
            return f;
        }
    }, new SimpleFloatPropertyCompat.Setter() {
        @Override
        public final void set(Object obj, float f) {
            CodeNumberField.lambda$static$5((CodeNumberField) obj, f);
        }
    }).setMultiplier(100.0f);
    private static final FloatPropertyCompat SUCCESS_SCALE_PROGRESS = new SimpleFloatPropertyCompat("successScaleProgress", new SimpleFloatPropertyCompat.Getter() {
        @Override
        public final float get(Object obj) {
            float f;
            f = ((CodeNumberField) obj).successScaleProgress;
            return f;
        }
    }, new SimpleFloatPropertyCompat.Setter() {
        @Override
        public final void set(Object obj, float f) {
            CodeNumberField.lambda$static$7((CodeNumberField) obj, f);
        }
    }).setMultiplier(100.0f);

    public CodeNumberField(Context context) {
        super(context);
        this.successScaleProgress = 1.0f;
        this.focusedSpringAnimation = new SpringAnimation(this, FOCUSED_PROGRESS);
        this.errorSpringAnimation = new SpringAnimation(this, ERROR_PROGRESS);
        this.successSpringAnimation = new SpringAnimation(this, SUCCESS_PROGRESS);
        this.successScaleSpringAnimation = new SpringAnimation(this, SUCCESS_SCALE_PROGRESS);
        this.showSoftInputOnFocusInternal = true;
        this.enterAnimation = 1.0f;
        this.exitAnimation = 1.0f;
        this.pressed = false;
        this.startX = 0.0f;
        this.startY = 0.0f;
        setBackground(null);
        setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        setMovementMethod(null);
        addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                CodeNumberField.this.startEnterAnimation(charSequence.length() != 0);
                CodeNumberField.this.hideActionMode();
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }
        });
    }

    private void animateSpring(SpringAnimation springAnimation, float f) {
        if (springAnimation.getSpring() == null || f != springAnimation.getSpring().getFinalPosition()) {
            springAnimation.cancel();
            springAnimation.setSpring(new SpringForce(f).setStiffness(400.0f).setDampingRatio(1.0f).setFinalPosition(f)).start();
        }
    }

    public void lambda$startEnterAnimation$9(ValueAnimator valueAnimator) {
        this.enterAnimation = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (getParent() != null) {
            ((ViewGroup) getParent()).invalidate();
        }
    }

    public void lambda$startExitAnimation$8(ValueAnimator valueAnimator) {
        this.exitAnimation = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (getParent() != null) {
            ((ViewGroup) getParent()).invalidate();
        }
    }

    public static void lambda$static$1(CodeNumberField codeNumberField, float f) {
        codeNumberField.focusedProgress = f;
        if (codeNumberField.getParent() != null) {
            ((View) codeNumberField.getParent()).invalidate();
        }
    }

    public static void lambda$static$3(CodeNumberField codeNumberField, float f) {
        codeNumberField.errorProgress = f;
        if (codeNumberField.getParent() != null) {
            ((View) codeNumberField.getParent()).invalidate();
        }
    }

    public static void lambda$static$5(CodeNumberField codeNumberField, float f) {
        codeNumberField.successProgress = f;
        if (codeNumberField.getParent() != null) {
            ((View) codeNumberField.getParent()).invalidate();
        }
    }

    public static void lambda$static$7(CodeNumberField codeNumberField, float f) {
        codeNumberField.successScaleProgress = f;
        if (codeNumberField.getParent() != null) {
            ((View) codeNumberField.getParent()).invalidate();
        }
    }

    public void pasteFromClipboard() {
        ClipboardManager clipboardManager;
        ClipData primaryClip;
        int i;
        CodeFieldContainer codeFieldContainer = getParent() instanceof CodeFieldContainer ? (CodeFieldContainer) getParent() : null;
        if (codeFieldContainer == null || (clipboardManager = (ClipboardManager) ContextCompat.getSystemService(getContext(), ClipboardManager.class)) == null || (primaryClip = clipboardManager.getPrimaryClip()) == null) {
            return;
        }
        String charSequence = primaryClip.getItemAt(0).getText().toString();
        try {
            i = Integer.parseInt(charSequence);
        } catch (Exception unused) {
            i = -1;
        }
        if (i > 0) {
            codeFieldContainer.setText(charSequence, true);
        }
    }

    public void animateErrorProgress(float f) {
        animateSpring(this.errorSpringAnimation, f * 100.0f);
    }

    public void animateFocusedProgress(float f) {
        animateSpring(this.focusedSpringAnimation, f * 100.0f);
    }

    public void animateSuccessProgress(float f) {
        animateSpring(this.successSpringAnimation, f * 100.0f);
        this.successScaleSpringAnimation.cancel();
        if (f != 0.0f) {
            ((SpringAnimation) ((SpringAnimation) this.successScaleSpringAnimation.setSpring(new SpringForce(1.0f).setStiffness(500.0f).setDampingRatio(0.75f).setFinalPosition(100.0f)).setStartValue(100.0f)).setStartVelocity(4000.0f)).start();
        } else {
            this.successScaleProgress = 1.0f;
        }
    }

    public float getErrorProgress() {
        return this.errorProgress;
    }

    public float getFocusedProgress() {
        return this.focusedProgress;
    }

    public float getSuccessProgress() {
        return this.successProgress;
    }

    public float getSuccessScaleProgress() {
        return this.successScaleProgress;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.focusedSpringAnimation.cancel();
        this.errorSpringAnimation.cancel();
    }

    @Override
    public void onFocusChanged(boolean z, int i, Rect rect) {
        super.onFocusChanged(z, i, rect);
        if (isFocused()) {
            return;
        }
        hideActionMode();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        ClipDescription primaryClipDescription;
        int i;
        if (motionEvent.getAction() == 0) {
            this.pressed = true;
            this.startX = motionEvent.getX();
            this.startY = motionEvent.getY();
        }
        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            CodeFieldContainer codeFieldContainer = getParent() instanceof CodeFieldContainer ? (CodeFieldContainer) getParent() : null;
            if (motionEvent.getAction() == 1 && this.pressed) {
                if (!isFocused() || codeFieldContainer == null) {
                    requestFocus();
                } else {
                    ClipboardManager clipboardManager = (ClipboardManager) ContextCompat.getSystemService(getContext(), ClipboardManager.class);
                    if (clipboardManager == null || clipboardManager.getPrimaryClipDescription() == null || (primaryClipDescription = clipboardManager.getPrimaryClipDescription()) == null) {
                        return false;
                    }
                    primaryClipDescription.hasMimeType("text/plain");
                    ClipData.Item itemAt = clipboardManager.getPrimaryClip().getItemAt(0);
                    try {
                        i = Integer.parseInt((itemAt == null || itemAt.getText() == null) ? "" : itemAt.getText().toString());
                    } catch (Exception unused) {
                        i = -1;
                    }
                    if (i > 0) {
                        startActionMode(new ActionMode.Callback() {
                            @Override
                            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                                if (menuItem.getItemId() != 16908322) {
                                    return true;
                                }
                                CodeNumberField.this.pasteFromClipboard();
                                CodeNumberField.this.hideActionMode();
                                return true;
                            }

                            @Override
                            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                                menu.add(0, 16908322, 0, 17039371);
                                return true;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode actionMode) {
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                                return true;
                            }
                        });
                    }
                }
                setSelection(0);
                if (this.showSoftInputOnFocusInternal) {
                    AndroidUtilities.showKeyboard(this);
                }
            }
            this.pressed = false;
        }
        return this.pressed;
    }

    @Override
    public boolean requestFocus(int i, Rect rect) {
        ((ViewGroup) getParent()).invalidate();
        return super.requestFocus(i, rect);
    }

    public void setShowSoftInputOnFocusCompat(boolean z) {
        this.showSoftInputOnFocusInternal = z;
        if (Build.VERSION.SDK_INT >= 21) {
            setShowSoftInputOnFocus(z);
        }
    }

    public void startEnterAnimation(boolean z) {
        ValueAnimator valueAnimator;
        long j;
        this.replaceAnimation = z;
        this.enterAnimation = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.enterAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                CodeNumberField.this.lambda$startEnterAnimation$9(valueAnimator2);
            }
        });
        if (this.replaceAnimation) {
            valueAnimator = this.enterAnimator;
            j = 220;
        } else {
            this.enterAnimator.setInterpolator(new OvershootInterpolator(1.5f));
            valueAnimator = this.enterAnimator;
            j = 350;
        }
        valueAnimator.setDuration(j);
        this.enterAnimator.start();
    }

    public void startExitAnimation() {
        if (getMeasuredHeight() == 0 || getMeasuredWidth() == 0 || getLayout() == null) {
            return;
        }
        Bitmap bitmap = this.exitBitmap;
        if (bitmap == null || bitmap.getHeight() != getMeasuredHeight() || this.exitBitmap.getWidth() != getMeasuredWidth()) {
            Bitmap bitmap2 = this.exitBitmap;
            if (bitmap2 != null) {
                bitmap2.recycle();
            }
            this.exitBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            this.exitCanvas = new Canvas(this.exitBitmap);
        }
        this.exitBitmap.eraseColor(0);
        StaticLayout staticLayout = new StaticLayout(getTransformationMethod().getTransformation(getText(), this), getLayout().getPaint(), (int) Math.ceil(getLayout().getPaint().measureText(r4, 0, r4.length())), Layout.Alignment.ALIGN_NORMAL, getLineSpacingMultiplier(), getLineSpacingExtra(), getIncludeFontPadding());
        this.exitCanvas.save();
        this.exitCanvas.translate((getMeasuredWidth() - staticLayout.getWidth()) / 2.0f, (getMeasuredHeight() - staticLayout.getHeight()) / 2.0f);
        staticLayout.draw(this.exitCanvas);
        this.exitCanvas.restore();
        this.exitAnimation = 0.0f;
        ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.exitAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CodeNumberField.this.lambda$startExitAnimation$8(valueAnimator);
            }
        });
        this.exitAnimator.setDuration(220L);
        this.exitAnimator.start();
    }
}
