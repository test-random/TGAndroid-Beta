package org.telegram.ui.web;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.OKLCH;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LineProgressView;
import org.telegram.ui.GradientClip;
import org.telegram.ui.web.WebActionBar;
import org.telegram.ui.web.WebInstantView;

public abstract class WebActionBar extends FrameLayout {
    private ValueAnimator addressAnimator;
    public int addressBackgroundColor;
    public final Paint addressBackgroundPaint;
    public final FrameLayout addressContainer;
    public final EditTextBoldCursor addressEditText;
    public final Paint addressRoundPaint;
    public int addressTextColor;
    public boolean addressing;
    public float addressingProgress;
    public final ImageView backButton;
    public final BackDrawable backButtonDrawable;
    public final Drawable backButtonSelector;
    private boolean backButtonShown;
    private int backgroundColor;
    public final Paint[] backgroundPaint;
    public final ImageView clearButton;
    public final Drawable clearButtonSelector;
    public final GradientClip clip;
    private ValueAnimator colorAnimator;
    public boolean[] colorSet;
    public boolean drawShadow;
    public final ImageView forwardButton;
    public final ForwardDrawable forwardButtonDrawable;
    public final Drawable forwardButtonSelector;
    private int fromBackgroundColor;
    public boolean hasForward;
    public boolean hasLoaded;
    public int height;
    public int iconColor;
    public boolean isMenuShown;
    public boolean isTonsite;
    public final LinearLayout leftmenu;
    public final LineProgressView lineProgressView;
    public boolean longClicked;
    private Runnable longPressRunnable;
    public int menuBackgroundColor;
    public final ImageView menuButton;
    public final Drawable menuButtonSelector;
    public int menuIconColor;
    private Utilities.Callback menuListener;
    public int menuTextColor;
    private int menuType;
    private boolean occupyStatusBar;
    private long pressTime;
    private float pressX;
    private float pressY;
    public final float[] progress;
    public final Paint[] progressBackgroundPaint;
    public final RectF rect;
    private final Theme.ResourcesProvider resourcesProvider;
    public final LinearLayout rightmenu;
    private int rippleColor;
    public float scale;
    public final Paint scrimPaint;
    private ValueAnimator searchAnimator;
    public final FrameLayout searchContainer;
    public final EditTextBoldCursor searchEditText;
    private int searchEngineIndex;
    public boolean searching;
    public float searchingProgress;
    public final Paint[] shadowPaint;
    public int textColor;
    public final TextPaint titlePaint;
    public float titleProgress;
    public final Title[] titles;
    private Utilities.Callback urlCallback;

    public class ForwardDrawable extends Drawable {
        private AnimatedFloat animatedState;
        private final Paint paint;
        private final Path path = new Path();
        private boolean state;

        public ForwardDrawable() {
            Paint paint = new Paint(1);
            this.paint = paint;
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            this.animatedState = new AnimatedFloat(new Runnable() {
                @Override
                public final void run() {
                    WebActionBar.ForwardDrawable.this.invalidateSelf();
                }
            }, 0L, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
        }

        @Override
        public void draw(Canvas canvas) {
            float f = this.animatedState.set(!this.state);
            float centerX = getBounds().centerX();
            float centerY = getBounds().centerY();
            float width = getBounds().width();
            float f2 = 0.57f * width;
            this.path.rewind();
            float f3 = f2 / 2.0f;
            this.path.moveTo(centerX - AndroidUtilities.lerp(f3, (-f2) / 2.0f, f), centerY);
            float f4 = f3 + centerX;
            this.path.lineTo(f4, centerY);
            float f5 = f4 - (0.27f * width);
            float f6 = (0.54f * width) / 2.0f;
            this.path.moveTo(f5, centerY - f6);
            this.path.lineTo(f4, centerY);
            this.path.lineTo(f5, f6 + centerY);
            canvas.save();
            this.paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            canvas.translate(0.0f, (-width) * 0.1f * f);
            canvas.rotate(f * 90.0f, centerX, centerY);
            canvas.drawPath(this.path, this.paint);
            canvas.restore();
        }

        @Override
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(24.0f);
        }

        @Override
        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(24.0f);
        }

        @Override
        public int getOpacity() {
            return -2;
        }

        @Override
        public void setAlpha(int i) {
        }

        public void setColor(int i) {
            this.paint.setColor(i);
            invalidateSelf();
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public void setState(boolean z) {
            this.state = z;
            invalidateSelf();
        }
    }

    public class Title {
        public final AnimatedFloat animatedDangerous;
        public boolean isDangerous;
        public final AnimatedTextView.AnimatedTextDrawable subtitle;
        public int subtitleColor;
        public final AnimatedTextView.AnimatedTextDrawable title;
        public final Drawable warningDrawable;
        public int warningDrawableColor;

        public Title() {
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(true, true, true);
            this.title = animatedTextDrawable;
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable2 = new AnimatedTextView.AnimatedTextDrawable(true, true, true);
            this.subtitle = animatedTextDrawable2;
            this.animatedDangerous = new AnimatedFloat(WebActionBar.this, 0L, 300L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.isDangerous = false;
            animatedTextDrawable.ignoreRTL = true;
            animatedTextDrawable.setTextSize(AndroidUtilities.dp(18.33f));
            animatedTextDrawable.setScaleProperty(0.6f);
            animatedTextDrawable.setTypeface(AndroidUtilities.bold());
            animatedTextDrawable.setEllipsizeByGradient(false);
            animatedTextDrawable.setCallback(WebActionBar.this);
            animatedTextDrawable.setOverrideFullWidth(9999999);
            animatedTextDrawable2.ignoreRTL = true;
            animatedTextDrawable2.setTextSize(AndroidUtilities.dp(14.0f));
            animatedTextDrawable2.setEllipsizeByGradient(false);
            animatedTextDrawable2.setCallback(WebActionBar.this);
            animatedTextDrawable2.setOverrideFullWidth(9999999);
            this.warningDrawable = WebActionBar.this.getContext().getResources().getDrawable(R.drawable.warning_sign).mutate();
        }

        public void draw(Canvas canvas, float f, float f2, float f3) {
            WebActionBar.this.rect.set(0.0f, 0.0f, f, f2);
            canvas.saveLayerAlpha(WebActionBar.this.rect, (int) (f3 * 255.0f), 31);
            float isNotEmpty = this.title.isNotEmpty() * this.subtitle.isNotEmpty();
            canvas.save();
            float f4 = 0.82f * f2;
            canvas.translate(0.0f, (-AndroidUtilities.dp(1.0f)) + ((1.0f - WebActionBar.this.scale) * f4));
            canvas.translate(0.0f, (-AndroidUtilities.dp(4.0f)) * isNotEmpty);
            float lerp = WebActionBar.this.scale * AndroidUtilities.lerp(1.0f, 0.86f, isNotEmpty);
            canvas.scale(lerp, lerp, 0.0f, 0.0f);
            this.title.setBounds(0.0f, 0.0f, f, f2);
            this.title.draw(canvas);
            canvas.restore();
            float f5 = this.animatedDangerous.set(this.isDangerous);
            canvas.save();
            canvas.translate(0.0f, (((-AndroidUtilities.dp(1.0f)) + ((f4 * (1.0f - WebActionBar.this.scale)) * isNotEmpty)) + (AndroidUtilities.dp(14.0f) * isNotEmpty)) - (AndroidUtilities.dp(4.0f) * (1.0f - isNotEmpty)));
            float lerp2 = WebActionBar.this.scale * AndroidUtilities.lerp(1.15f, 0.9f, isNotEmpty);
            canvas.scale(lerp2, lerp2, 0.0f, 0.0f);
            this.subtitle.setTextColor(ColorUtils.blendARGB(this.subtitleColor, Theme.getColor(Theme.key_text_RedBold), f5));
            if (f5 > 0.0f) {
                if (this.warningDrawableColor != this.subtitle.getTextColor()) {
                    Drawable drawable = this.warningDrawable;
                    int textColor = this.subtitle.getTextColor();
                    this.warningDrawableColor = textColor;
                    drawable.setColorFilter(new PorterDuffColorFilter(textColor, PorterDuff.Mode.SRC_IN));
                }
                this.warningDrawable.setAlpha((int) (255.0f * f5));
                this.warningDrawable.setBounds(0, ((int) (f2 - AndroidUtilities.dp(16.0f))) / 2, AndroidUtilities.dp(16.0f), ((int) (AndroidUtilities.dp(16.0f) + f2)) / 2);
                this.warningDrawable.draw(canvas);
            }
            this.subtitle.setBounds(AndroidUtilities.dp(20.0f) * f5, 0.0f, f, f2);
            this.subtitle.draw(canvas);
            canvas.restore();
            WebActionBar.this.rect.set(f - AndroidUtilities.dp(12.0f), 0.0f, f, f2);
            WebActionBar webActionBar = WebActionBar.this;
            webActionBar.clip.draw(canvas, webActionBar.rect, 2, 1.0f);
            canvas.restore();
        }
    }

    public WebActionBar(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.rect = new RectF();
        this.titles = new Title[2];
        this.titleProgress = 0.0f;
        this.progress = new float[2];
        this.colorSet = new boolean[3];
        this.backgroundPaint = new Paint[2];
        this.progressBackgroundPaint = new Paint[2];
        this.shadowPaint = new Paint[2];
        this.scrimPaint = new Paint(1);
        this.addressBackgroundPaint = new Paint(1);
        this.addressRoundPaint = new Paint(1);
        TextPaint textPaint = new TextPaint(1);
        this.titlePaint = textPaint;
        this.isMenuShown = false;
        this.height = AndroidUtilities.dp(56.0f);
        this.scale = 1.0f;
        this.searchingProgress = 0.0f;
        this.addressingProgress = 0.0f;
        this.menuType = -1;
        this.clip = new GradientClip();
        this.longPressRunnable = new Runnable() {
            @Override
            public final void run() {
                WebActionBar.this.lambda$new$11();
            }
        };
        this.longClicked = false;
        this.resourcesProvider = resourcesProvider;
        textPaint.setTypeface(AndroidUtilities.bold());
        textPaint.setTextSize(AndroidUtilities.dp(18.33f));
        for (int i = 0; i < 2; i++) {
            this.backgroundPaint[i] = new Paint(1);
            this.progressBackgroundPaint[i] = new Paint(1);
            this.shadowPaint[i] = new Paint(1);
        }
        FrameLayout frameLayout = new FrameLayout(context);
        this.searchContainer = frameLayout;
        addView(frameLayout, LayoutHelper.createFrame(-1, 56, 87));
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.addressContainer = frameLayout2;
        addView(frameLayout2, LayoutHelper.createFrame(-1, 56, 87));
        LinearLayout linearLayout = new LinearLayout(context) {
            @Override
            protected void onMeasure(int i2, int i3) {
                super.onMeasure(i2, i3);
                setPivotY(0.0f);
                setPivotX(0.0f);
            }
        };
        this.leftmenu = linearLayout;
        linearLayout.setOrientation(0);
        addView(linearLayout, LayoutHelper.createFrame(-2, 56, 83));
        ImageView imageView = new ImageView(context);
        this.backButton = imageView;
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        imageView.setScaleType(scaleType);
        BackDrawable backDrawable = new BackDrawable(false);
        this.backButtonDrawable = backDrawable;
        backDrawable.setAnimationTime(200.0f);
        backDrawable.setRotation(1.0f, false);
        imageView.setImageDrawable(backDrawable);
        Drawable createSelectorDrawable = Theme.createSelectorDrawable(1090519039);
        this.backButtonSelector = createSelectorDrawable;
        imageView.setBackground(createSelectorDrawable);
        linearLayout.addView(imageView, LayoutHelper.createLinear(54, 56));
        LinearLayout linearLayout2 = new LinearLayout(context) {
            @Override
            protected void onMeasure(int i2, int i3) {
                super.onMeasure(i2, i3);
                setPivotY(0.0f);
                setPivotX(getMeasuredWidth());
            }
        };
        this.rightmenu = linearLayout2;
        linearLayout2.setOrientation(0);
        addView(linearLayout2, LayoutHelper.createFrame(-2, 56, 85));
        ImageView imageView2 = new ImageView(context);
        this.forwardButton = imageView2;
        imageView2.setScaleType(scaleType);
        ForwardDrawable forwardDrawable = new ForwardDrawable();
        this.forwardButtonDrawable = forwardDrawable;
        imageView2.setImageDrawable(forwardDrawable);
        forwardDrawable.setState(false);
        Drawable createSelectorDrawable2 = Theme.createSelectorDrawable(1090519039);
        this.forwardButtonSelector = createSelectorDrawable2;
        imageView2.setBackground(createSelectorDrawable2);
        linearLayout2.addView(imageView2, LayoutHelper.createLinear(54, 56));
        ImageView imageView3 = new ImageView(context);
        this.menuButton = imageView3;
        imageView3.setScaleType(scaleType);
        imageView3.setImageResource(R.drawable.ic_ab_other);
        imageView3.setColorFilter(new PorterDuffColorFilter(0, PorterDuff.Mode.SRC_IN));
        imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                WebActionBar.this.lambda$new$4(view);
            }
        });
        Drawable createSelectorDrawable3 = Theme.createSelectorDrawable(1090519039);
        this.menuButtonSelector = createSelectorDrawable3;
        imageView3.setBackground(createSelectorDrawable3);
        imageView3.setContentDescription(LocaleController.getString("AccDescrMoreOptions", R.string.AccDescrMoreOptions));
        linearLayout2.addView(imageView3, LayoutHelper.createLinear(54, 56));
        EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(context) {
            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.searchEditText = editTextBoldCursor;
        editTextBoldCursor.setVisibility(8);
        editTextBoldCursor.setAlpha(0.0f);
        editTextBoldCursor.setTextSize(1, 18.0f);
        editTextBoldCursor.setSingleLine(true);
        editTextBoldCursor.setHint(LocaleController.getString(R.string.Search));
        editTextBoldCursor.setBackgroundResource(0);
        editTextBoldCursor.setCursorWidth(1.5f);
        editTextBoldCursor.setGravity(112);
        editTextBoldCursor.setClipToPadding(true);
        editTextBoldCursor.setPadding(AndroidUtilities.dp(58.0f), 0, AndroidUtilities.dp(112.0f), 0);
        editTextBoldCursor.setTranslationY(-AndroidUtilities.dp(0.66f));
        editTextBoldCursor.setInputType(editTextBoldCursor.getInputType() | 524288);
        editTextBoldCursor.setImeOptions(33554435);
        editTextBoldCursor.setTextIsSelectable(false);
        editTextBoldCursor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                boolean lambda$new$5;
                lambda$new$5 = WebActionBar.this.lambda$new$5(textView, i2, keyEvent);
                return lambda$new$5;
            }
        });
        editTextBoldCursor.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable editable) {
                AndroidUtilities.updateViewShow(WebActionBar.this.clearButton, editable.length() > 0 && WebActionBar.this.searching, true, true);
                WebActionBar.this.onSearchUpdated(editable.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
            }
        });
        frameLayout.addView(editTextBoldCursor, LayoutHelper.createFrame(-1, -1, 119));
        EditTextBoldCursor editTextBoldCursor2 = new EditTextBoldCursor(context) {
            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && !AndroidUtilities.showKeyboard(this)) {
                    clearFocus();
                    requestFocus();
                }
                return super.onTouchEvent(motionEvent);
            }
        };
        this.addressEditText = editTextBoldCursor2;
        editTextBoldCursor2.setVisibility(8);
        editTextBoldCursor2.setAlpha(0.0f);
        editTextBoldCursor2.setTextSize(1, 15.66f);
        editTextBoldCursor2.setSingleLine(true);
        this.searchEngineIndex = SharedConfig.searchEngineType;
        editTextBoldCursor2.setHint(LocaleController.formatString(R.string.AddressPlaceholder, SearchEngine.getCurrent().name));
        editTextBoldCursor2.setBackgroundResource(0);
        editTextBoldCursor2.setCursorWidth(1.5f);
        editTextBoldCursor2.setGravity(112);
        editTextBoldCursor2.setInputType(editTextBoldCursor2.getInputType() | 524288);
        editTextBoldCursor2.setImeOptions(33554434);
        editTextBoldCursor2.setTextIsSelectable(false);
        editTextBoldCursor2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public final boolean onEditorAction(TextView textView, int i2, KeyEvent keyEvent) {
                boolean lambda$new$6;
                lambda$new$6 = WebActionBar.this.lambda$new$6(textView, i2, keyEvent);
                return lambda$new$6;
            }
        });
        frameLayout2.addView(editTextBoldCursor2, LayoutHelper.createFrame(-1, -1.0f, 119, 48.0f, 0.0f, 12.0f, 0.0f));
        ImageView imageView4 = new ImageView(context);
        this.clearButton = imageView4;
        imageView4.setScaleType(scaleType);
        imageView4.setImageResource(R.drawable.ic_close_white);
        Drawable createSelectorDrawable4 = Theme.createSelectorDrawable(1090519039);
        this.clearButtonSelector = createSelectorDrawable4;
        imageView4.setBackground(createSelectorDrawable4);
        imageView4.setVisibility(8);
        imageView4.setAlpha(0.0f);
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                WebActionBar.this.lambda$new$7(view);
            }
        });
        addView(imageView4, LayoutHelper.createFrame(54, 56, 85));
        LineProgressView lineProgressView = new LineProgressView(context);
        this.lineProgressView = lineProgressView;
        lineProgressView.setPivotX(0.0f);
        lineProgressView.setPivotY(AndroidUtilities.dp(2.0f));
        addView(lineProgressView, LayoutHelper.createFrame(-1, 2, 87));
        setWillNotDraw(false);
        this.titles[0] = new Title();
        this.titles[1] = new Title();
        int i2 = Theme.key_iv_background;
        setColors(Theme.getColor(i2, resourcesProvider), false);
        setMenuColors(Theme.getColor(i2, resourcesProvider));
    }

    public void lambda$new$0(Integer num) {
        this.menuListener.run(num);
    }

    public Runnable lambda$new$1(final Integer num) {
        return new Runnable() {
            @Override
            public final void run() {
                WebActionBar.this.lambda$new$0(num);
            }
        };
    }

    public void lambda$new$11() {
        this.longClicked = true;
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        try {
            performHapticFeedback(0, 1);
        } catch (Exception unused) {
        }
    }

    public static void lambda$new$2(ActionBarMenuSubItem actionBarMenuSubItem, WebInstantView.Loader loader) {
        actionBarMenuSubItem.setEnabled(loader.getWebPage() != null);
        actionBarMenuSubItem.animate().alpha(actionBarMenuSubItem.isEnabled() ? 1.0f : 0.5f);
    }

    public void lambda$new$3() {
        this.isMenuShown = false;
    }

    public void lambda$new$4(View view) {
        int i;
        int i2;
        String string;
        if (getParent() instanceof ViewGroup) {
            Utilities.CallbackReturn callbackReturn = new Utilities.CallbackReturn() {
                @Override
                public final Object run(Object obj) {
                    Runnable lambda$new$1;
                    lambda$new$1 = WebActionBar.this.lambda$new$1((Integer) obj);
                    return lambda$new$1;
                }
            };
            ItemOptions makeOptions = ItemOptions.makeOptions((ViewGroup) getParent(), this.menuButton);
            makeOptions.setDimAlpha(0);
            makeOptions.setColors(this.menuTextColor, this.menuIconColor);
            makeOptions.translate(0.0f, -AndroidUtilities.dp(52.0f));
            makeOptions.setMinWidth(200);
            makeOptions.setSelectorColor(Theme.blendOver(this.menuBackgroundColor, Theme.multAlpha(this.menuTextColor, 0.1f)));
            if (AndroidUtilities.computePerceivedBrightness(this.menuBackgroundColor) > 0.721f) {
                makeOptions.setBackgroundColor(-1);
                i = -986896;
            } else {
                makeOptions.setBackgroundColor(-14737633);
                i = -15592942;
            }
            makeOptions.setGapBackgroundColor(i);
            int i3 = this.menuType;
            int i4 = 2;
            if (i3 != 0) {
                if (i3 == 1) {
                    if (!this.isTonsite) {
                        makeOptions.add(R.drawable.msg_openin, LocaleController.getString(R.string.OpenInExternalApp), (Runnable) callbackReturn.run(3));
                        makeOptions.addGap();
                    }
                    if (this.hasForward) {
                        makeOptions.add(R.drawable.msg_arrow_forward, LocaleController.getString(R.string.WebForward), (Runnable) callbackReturn.run(9));
                    }
                    final WebInstantView.Loader instantViewLoader = getInstantViewLoader();
                    if (instantViewLoader != null && (!instantViewLoader.isDone() || instantViewLoader.getWebPage() != null)) {
                        makeOptions.add(R.drawable.menu_instant_view, LocaleController.getString(R.string.OpenLocalInstantView), (Runnable) callbackReturn.run(10));
                        final ActionBarMenuSubItem last = makeOptions.getLast();
                        last.setEnabled(instantViewLoader.getWebPage() != null);
                        last.setAlpha(last.isEnabled() ? 1.0f : 0.5f);
                        makeOptions.setOnDismiss(instantViewLoader.listen(new Runnable() {
                            @Override
                            public final void run() {
                                WebActionBar.lambda$new$2(ActionBarMenuSubItem.this, instantViewLoader);
                            }
                        }));
                    }
                    makeOptions.add(R.drawable.msg_reset, LocaleController.getString(R.string.Refresh), (Runnable) callbackReturn.run(5));
                    makeOptions.add(R.drawable.msg_search, LocaleController.getString(R.string.Search), (Runnable) callbackReturn.run(1));
                    makeOptions.add(R.drawable.msg_saved, LocaleController.getString(R.string.WebBookmark), (Runnable) callbackReturn.run(6));
                    makeOptions.add(R.drawable.msg_share, LocaleController.getString(R.string.ShareFile), (Runnable) callbackReturn.run(2));
                    makeOptions.addGap();
                    if (!BrowserHistory.getHistory().isEmpty()) {
                        makeOptions.add(R.drawable.menu_views_recent, LocaleController.getString(R.string.WebHistory), (Runnable) callbackReturn.run(8));
                    }
                    i2 = R.drawable.menu_browser_bookmarks;
                    string = LocaleController.getString(R.string.WebBookmarks);
                    i4 = 7;
                }
                makeOptions.setOnDismiss(new Runnable() {
                    @Override
                    public final void run() {
                        WebActionBar.this.lambda$new$3();
                    }
                });
                makeOptions.show();
                this.isMenuShown = true;
            }
            makeOptions.add(R.drawable.msg_openin, LocaleController.getString(R.string.OpenInExternalApp), (Runnable) callbackReturn.run(3));
            makeOptions.add(R.drawable.msg_search, LocaleController.getString(R.string.Search), (Runnable) callbackReturn.run(1));
            i2 = R.drawable.msg_share;
            string = LocaleController.getString(R.string.ShareFile);
            makeOptions.add(i2, string, (Runnable) callbackReturn.run(Integer.valueOf(i4)));
            makeOptions.add(R.drawable.msg_settings_old, LocaleController.getString(R.string.Settings), (Runnable) callbackReturn.run(4));
            makeOptions.setOnDismiss(new Runnable() {
                @Override
                public final void run() {
                    WebActionBar.this.lambda$new$3();
                }
            });
            makeOptions.show();
            this.isMenuShown = true;
        }
    }

    public boolean lambda$new$5(TextView textView, int i, KeyEvent keyEvent) {
        if (keyEvent == null) {
            return false;
        }
        if ((keyEvent.getAction() != 1 || keyEvent.getKeyCode() != 84) && (keyEvent.getAction() != 0 || keyEvent.getKeyCode() != 66)) {
            return false;
        }
        AndroidUtilities.hideKeyboard(this.searchEditText);
        return false;
    }

    public boolean lambda$new$6(TextView textView, int i, KeyEvent keyEvent) {
        if (i == 2) {
            Utilities.Callback callback = this.urlCallback;
            if (callback != null) {
                callback.run(this.addressEditText.getText().toString());
            }
            showAddress(false, true);
        }
        return false;
    }

    public void lambda$new$7(View view) {
        this.searchEditText.setText("");
    }

    public void lambda$setColors$8(int i, float f, float f2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        setColors(ColorUtils.blendARGB(this.fromBackgroundColor, i, floatValue), AndroidUtilities.lerp(f, f2, floatValue), false);
    }

    public void lambda$showAddress$10(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.addressingProgress = floatValue;
        onAddressingProgress(floatValue);
        this.addressEditText.setAlpha(this.addressingProgress);
        this.menuButton.setTranslationX(AndroidUtilities.dp(56.0f) * this.addressingProgress);
        this.forwardButton.setTranslationX(AndroidUtilities.dp(112.0f) * this.addressingProgress);
        invalidate();
    }

    public void lambda$showSearch$9(ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.searchingProgress = floatValue;
        this.searchEditText.setAlpha(floatValue);
        invalidate();
    }

    public void showAddressKeyboard() {
        if (this.addressing) {
            this.addressEditText.requestFocus();
            AndroidUtilities.showKeyboard(this.addressEditText);
        } else {
            this.addressEditText.clearFocus();
            AndroidUtilities.hideKeyboard(this.addressEditText);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawBackground(canvas, topPadding() + this.height, 1.0f, 1.0f, this.drawShadow);
        float right = this.leftmenu.getRight();
        float left = this.rightmenu.getLeft();
        float f = topPadding();
        float f2 = topPadding() + this.height;
        if (this.titleProgress < 1.0f) {
            canvas.save();
            float width = (getWidth() * this.titleProgress) - (AndroidUtilities.dp(30.0f) * Utilities.clamp01(this.titleProgress * 2.0f));
            canvas.translate(right + width, f);
            AndroidUtilities.lerp(1.0f, 0.5f, this.titleProgress);
            this.titles[0].draw(canvas, (left - right) - width, f2 - f, (1.0f - this.titleProgress) * (1.0f - this.searchingProgress));
            canvas.restore();
        }
        if (this.titleProgress > 0.0f) {
            float width2 = getWidth() * this.titleProgress;
            canvas.save();
            canvas.clipRect(0.0f, 0.0f, width2, getHeight());
            canvas.translate(right, f);
            canvas.translate(AndroidUtilities.dp(-12.0f) * (1.0f - this.titleProgress), 0.0f);
            float lerp = AndroidUtilities.lerp(1.0f, 0.5f, 1.0f - this.titleProgress);
            float f3 = f2 - f;
            canvas.scale(lerp, lerp, 0.0f, f3 / 2.0f);
            this.titles[1].draw(canvas, left - right, f3, this.titleProgress * (1.0f - this.searchingProgress) * (1.0f - this.addressingProgress));
            canvas.restore();
        }
        if (this.addressingProgress > 0.0f) {
            int alpha = this.addressBackgroundPaint.getAlpha();
            this.addressBackgroundPaint.setAlpha((int) (alpha * this.addressingProgress));
            canvas.drawRect(0.0f, 0.0f, getWidth(), topPadding() + this.height, this.addressBackgroundPaint);
            this.addressBackgroundPaint.setAlpha(alpha);
            float f4 = (f + f2) / 2.0f;
            float dp = AndroidUtilities.dp(42.0f) / 2.0f;
            this.rect.set(AndroidUtilities.dp(6.0f), f4 - dp, AndroidUtilities.lerp(left, getWidth() - AndroidUtilities.dp(6.0f), this.addressingProgress), f4 + dp);
            int alpha2 = this.addressRoundPaint.getAlpha();
            this.addressRoundPaint.setAlpha((int) (alpha2 * this.addressingProgress));
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(50.0f), AndroidUtilities.dp(50.0f), this.addressRoundPaint);
            this.addressRoundPaint.setAlpha(alpha2);
        }
        this.rect.set(0.0f, f, getWidth(), f2);
        canvas.save();
        canvas.clipRect(this.rect);
        super.dispatchDraw(canvas);
        canvas.restore();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.longClicked = false;
            AndroidUtilities.cancelRunOnUIThread(this.longPressRunnable);
            if (motionEvent.getX() > this.leftmenu.getRight() && motionEvent.getX() < this.rightmenu.getLeft() && !isSearching() && !isAddressing()) {
                this.pressX = motionEvent.getX();
                this.pressY = motionEvent.getY();
                this.pressTime = System.currentTimeMillis();
                AndroidUtilities.runOnUIThread(this.longPressRunnable, ViewConfiguration.getLongPressTimeout() * 0.8f);
            }
        } else if (motionEvent.getAction() == 2 && ((float) (System.currentTimeMillis() - this.pressTime)) > ViewConfiguration.getLongPressTimeout() * 0.8f) {
            AndroidUtilities.cancelRunOnUIThread(this.longPressRunnable);
            this.longClicked = true;
            onScrolledProgress((motionEvent.getX() - this.pressX) / (getWidth() * 0.8f));
            getParent().requestDisallowInterceptTouchEvent(true);
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            AndroidUtilities.cancelRunOnUIThread(this.longPressRunnable);
            this.pressTime = 0L;
        }
        this.pressX = motionEvent.getX();
        return super.dispatchTouchEvent(motionEvent);
    }

    public void drawBackground(Canvas canvas, float f, float f2, float f3, boolean z) {
        float max = Math.max(AndroidUtilities.dp(0.66f), 1);
        float f4 = f - max;
        float width = getWidth() * this.titleProgress;
        this.rect.set(0.0f, 0.0f, getWidth(), f);
        int alpha = this.backgroundPaint[1].getAlpha();
        this.backgroundPaint[1].setAlpha((int) (alpha * f2));
        canvas.drawRect(this.rect, this.backgroundPaint[1]);
        this.backgroundPaint[1].setAlpha(alpha);
        if (this.titleProgress > 0.0f) {
            this.rect.set(0.0f, 0.0f, this.progress[1] * getWidth(), f);
            int alpha2 = this.progressBackgroundPaint[1].getAlpha();
            this.progressBackgroundPaint[1].setAlpha((int) (alpha2 * f2 * (1.0f - this.searchingProgress) * (1.0f - this.addressingProgress)));
            canvas.drawRect(this.rect, this.progressBackgroundPaint[1]);
            this.progressBackgroundPaint[1].setAlpha(alpha2);
            if (z) {
                this.rect.set(0.0f, f4, width, f4 + max);
                int alpha3 = this.shadowPaint[1].getAlpha();
                this.shadowPaint[1].setAlpha((int) (alpha3 * f2 * f3 * (1.0f - this.addressingProgress)));
                canvas.drawRect(this.rect, this.shadowPaint[1]);
                this.shadowPaint[1].setAlpha(alpha3);
            }
        }
        float f5 = this.titleProgress;
        if (f5 < 1.0f) {
            this.scrimPaint.setColor(Theme.multAlpha(1610612736, (1.0f - f5) * f2));
            this.rect.set(0.0f, 0.0f, width, f);
            canvas.drawRect(this.rect, this.scrimPaint);
            this.rect.set(width, 0.0f, getWidth(), f);
            int alpha4 = this.backgroundPaint[0].getAlpha();
            this.backgroundPaint[0].setAlpha((int) (alpha4 * f2));
            canvas.drawRect(this.rect, this.backgroundPaint[0]);
            this.backgroundPaint[0].setAlpha(alpha4);
        }
        this.rect.set(width, 0.0f, (this.progress[0] * getWidth()) + width, f);
        int alpha5 = this.progressBackgroundPaint[0].getAlpha();
        this.progressBackgroundPaint[0].setAlpha((int) ((1.0f - Utilities.clamp01(this.titleProgress * 4.0f)) * alpha5 * f2 * (1.0f - this.searchingProgress) * (1.0f - this.addressingProgress)));
        canvas.drawRect(this.rect, this.progressBackgroundPaint[0]);
        this.progressBackgroundPaint[0].setAlpha(alpha5);
        if (z) {
            this.rect.set(width, f4, getWidth() + width, max + f4);
            int alpha6 = this.shadowPaint[0].getAlpha();
            this.shadowPaint[0].setAlpha((int) (alpha6 * f2 * f3 * (1.0f - this.addressingProgress)));
            canvas.drawRect(this.rect, this.shadowPaint[0]);
            this.shadowPaint[0].setAlpha(alpha6);
        }
    }

    public int getBackgroundColor() {
        return this.backgroundColor;
    }

    public int getBackgroundColor(int i) {
        return this.backgroundPaint[i].getColor();
    }

    protected WebInstantView.Loader getInstantViewLoader() {
        return null;
    }

    public int getTextColor() {
        return this.textColor;
    }

    public String getTitle() {
        CharSequence text = this.titles[0].title.getText();
        return text == null ? "" : text.toString();
    }

    public boolean isAddressing() {
        return this.addressing;
    }

    public boolean isSearching() {
        return this.searching;
    }

    public void occupyStatusBar(boolean z) {
        this.occupyStatusBar = z;
    }

    protected abstract void onAddressColorsChanged(int i, int i2);

    public void onAddressingProgress(float f) {
        this.backButtonDrawable.setColor(ColorUtils.blendARGB(this.textColor, this.addressTextColor, this.addressingProgress));
        this.backButtonDrawable.setRotatedColor(ColorUtils.blendARGB(this.textColor, this.addressTextColor, this.addressingProgress));
        this.backButton.invalidate();
    }

    protected abstract void onColorsUpdated();

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
    }

    @Override
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(topPadding() + AndroidUtilities.dp(56.0f), 1073741824));
    }

    protected abstract void onScrolledProgress(float f);

    protected abstract void onSearchUpdated(String str);

    public void setBackButton(boolean z) {
        this.backButtonShown = z;
        if (isSearching() || isAddressing()) {
            return;
        }
        this.backButtonDrawable.setRotation(this.backButtonShown ? 0.0f : 1.0f, true);
    }

    public void setBackButtonCached(boolean z) {
        this.backButtonShown = z;
    }

    public void setBackgroundColor(int i, int i2) {
        if (this.colorSet[i] && this.backgroundPaint[i].getColor() == i2) {
            return;
        }
        this.colorSet[i] = true;
        this.backgroundPaint[i].setColor(i2);
        float f = AndroidUtilities.computePerceivedBrightness(i2) <= 0.721f ? 1.0f : 0.0f;
        int blendARGB = ColorUtils.blendARGB(-16777216, -1, f);
        this.progressBackgroundPaint[i].setColor(Theme.blendOver(i2, Theme.multAlpha(blendARGB, AndroidUtilities.lerp(0.07f, 0.2f, f))));
        this.shadowPaint[i].setColor(Theme.blendOver(i2, Theme.multAlpha(blendARGB, AndroidUtilities.lerp(0.14f, 0.24f, f))));
        this.titles[i].title.setTextColor(blendARGB);
        this.titles[i].subtitleColor = Theme.blendOver(i2, Theme.multAlpha(blendARGB, 0.6f));
        Title title = this.titles[i];
        title.subtitle.setTextColor(ColorUtils.blendARGB(title.subtitleColor, Theme.getColor(Theme.key_text_RedBold), this.titles[i].animatedDangerous.get()));
        invalidate();
    }

    public void setColors(final int i, float f, boolean z) {
        boolean[] zArr = this.colorSet;
        if (zArr[2] && this.backgroundColor == i) {
            return;
        }
        if (z) {
            ValueAnimator valueAnimator = this.colorAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            int i2 = this.backgroundColor;
            this.fromBackgroundColor = i2;
            final float f2 = AndroidUtilities.computePerceivedBrightness(i2) <= 0.721f ? 1.0f : 0.0f;
            final float f3 = AndroidUtilities.computePerceivedBrightness(i) > 0.721f ? 0.0f : 1.0f;
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.colorAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    WebActionBar.this.lambda$setColors$8(i, f2, f3, valueAnimator2);
                }
            });
            this.colorAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    WebActionBar.this.setColors(i, f3, false);
                }
            });
            this.colorAnimator.start();
            return;
        }
        zArr[2] = true;
        if (f < 0.0f) {
            f = AndroidUtilities.computePerceivedBrightness(i) <= 0.721f ? 1.0f : 0.0f;
        }
        int blendARGB = ColorUtils.blendARGB(-16777216, -1, f);
        this.textColor = blendARGB;
        this.iconColor = Theme.multAlpha(blendARGB, 0.55f);
        this.backgroundColor = i;
        this.addressBackgroundColor = ColorUtils.blendARGB(-1, -16777216, f);
        int blendARGB2 = ColorUtils.blendARGB(-1, -16777216, 1.0f - f);
        this.addressTextColor = blendARGB2;
        onAddressColorsChanged(this.addressBackgroundColor, blendARGB2);
        this.addressBackgroundPaint.setColor(this.addressBackgroundColor);
        this.addressRoundPaint.setColor(Theme.blendOver(this.addressBackgroundColor, Theme.multAlpha(this.textColor, AndroidUtilities.lerp(0.07f, 0.2f, f))));
        this.addressEditText.setHintTextColor(Theme.multAlpha(this.addressTextColor, 0.6f));
        this.addressEditText.setTextColor(this.addressTextColor);
        this.addressEditText.setCursorColor(this.addressTextColor);
        this.addressEditText.setHandlesColor(this.addressTextColor);
        this.lineProgressView.setProgressColor(Theme.getColor(Theme.key_iv_ab_progress, this.resourcesProvider));
        this.backButtonDrawable.setColor(ColorUtils.blendARGB(this.textColor, this.addressTextColor, this.addressingProgress));
        this.backButtonDrawable.setRotatedColor(ColorUtils.blendARGB(this.textColor, this.addressTextColor, this.addressingProgress));
        this.forwardButtonDrawable.setColor(this.textColor);
        ImageView imageView = this.menuButton;
        int i3 = this.textColor;
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        imageView.setColorFilter(new PorterDuffColorFilter(i3, mode));
        this.forwardButton.setColorFilter(new PorterDuffColorFilter(this.textColor, mode));
        this.clearButton.setColorFilter(new PorterDuffColorFilter(this.textColor, mode));
        int blendOver = Theme.blendOver(i, Theme.multAlpha(this.textColor, 0.22f));
        this.rippleColor = blendOver;
        Theme.setSelectorDrawableColor(this.backButtonSelector, blendOver, true);
        Theme.setSelectorDrawableColor(this.forwardButtonSelector, this.rippleColor, true);
        Theme.setSelectorDrawableColor(this.menuButtonSelector, this.rippleColor, true);
        Theme.setSelectorDrawableColor(this.clearButtonSelector, this.rippleColor, true);
        this.searchEditText.setHintTextColor(Theme.multAlpha(this.textColor, 0.6f));
        this.searchEditText.setTextColor(this.textColor);
        this.searchEditText.setCursorColor(this.textColor);
        this.searchEditText.setHandlesColor(this.textColor);
        onColorsUpdated();
        invalidate();
    }

    public void setColors(int i, boolean z) {
        setColors(i, -1.0f, z);
    }

    public void setHasForward(boolean z) {
        this.hasForward = z;
    }

    public void setHeight(int i) {
        if (this.height != i) {
            this.height = i;
            float pow = (float) Math.pow(i / AndroidUtilities.dp(56.0f), 0.5d);
            this.scale = pow;
            this.leftmenu.setScaleX(pow);
            this.leftmenu.setScaleY(this.scale);
            this.leftmenu.setTranslationX(AndroidUtilities.dp(42.0f) * (1.0f - this.scale));
            this.leftmenu.setTranslationY(AndroidUtilities.dp(-12.0f) * (1.0f - this.scale));
            this.rightmenu.setScaleX(this.scale);
            this.rightmenu.setScaleY(this.scale);
            this.rightmenu.setTranslationX((-AndroidUtilities.dp(42.0f)) * (1.0f - this.scale));
            this.rightmenu.setTranslationY(AndroidUtilities.dp(-12.0f) * (1.0f - this.scale));
            this.lineProgressView.setTranslationY(this.height - AndroidUtilities.dp(56.0f));
            invalidate();
        }
    }

    public void setIsDangerous(int i, boolean z, boolean z2) {
        Title title = this.titles[i];
        if (title.isDangerous != z) {
            title.isDangerous = z;
            if (!z2) {
                title.animatedDangerous.set(z ? 1.0f : 0.0f, true);
            }
            invalidate();
        }
    }

    public void setIsLoaded(boolean z) {
        this.hasLoaded = z;
    }

    public void setIsTonsite(boolean z) {
        this.isTonsite = z;
    }

    public void setMenuColors(int i) {
        boolean z = OKLCH.rgb2oklch(OKLCH.rgb(i))[0] < 0.5d;
        this.menuBackgroundColor = z ? -16777216 : -1;
        int i2 = z ? -1 : -16777216;
        this.menuTextColor = i2;
        this.menuIconColor = Theme.multAlpha(i2, 0.6f);
    }

    public void setMenuListener(Utilities.Callback<Integer> callback) {
        this.menuListener = callback;
    }

    public void setMenuType(int i) {
        if (this.menuType != i) {
            this.menuType = i;
        }
    }

    public void setProgress(float f) {
        setProgress(0, f);
    }

    public void setProgress(int i, float f) {
        this.progress[i] = f;
        invalidate();
    }

    public void setSubtitle(int i, String str, boolean z) {
        CharSequence text = this.titles[i].subtitle.getText();
        if (text == null || !TextUtils.equals(text.toString(), str)) {
            this.titles[i].subtitle.setText(Emoji.replaceEmoji(str, this.titles[i].subtitle.getPaint().getFontMetricsInt(), false), z);
        }
    }

    public void setTitle(int i, String str, boolean z) {
        CharSequence text = this.titles[i].title.getText();
        if (text == null || !TextUtils.equals(text.toString(), str)) {
            this.titles[i].title.setText(Emoji.replaceEmoji(str, this.titles[i].title.getPaint().getFontMetricsInt(), false), z);
        }
    }

    public void setTransitionProgress(float f) {
        this.titleProgress = f;
        invalidate();
    }

    public void showAddress(String str, Utilities.Callback callback) {
        this.addressEditText.setText(str);
        EditTextBoldCursor editTextBoldCursor = this.addressEditText;
        editTextBoldCursor.setSelection(0, editTextBoldCursor.getText().length());
        this.addressEditText.setScrollX(0);
        this.urlCallback = callback;
        showAddress(true, true);
    }

    public void showAddress(final boolean z, boolean z2) {
        if (this.addressing == z) {
            return;
        }
        ValueAnimator valueAnimator = this.addressAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.addressing = z;
        if (z) {
            int i = this.searchEngineIndex;
            int i2 = SharedConfig.searchEngineType;
            if (i != i2) {
                this.searchEngineIndex = i2;
                this.addressEditText.setHint(LocaleController.formatString(R.string.AddressPlaceholder, SearchEngine.getCurrent().name));
            }
        }
        if (z2) {
            this.addressEditText.setVisibility(0);
            this.backButtonDrawable.setRotation((this.backButtonShown || z) ? 0.0f : 1.0f, true);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.addressingProgress, z ? 1.0f : 0.0f);
            this.addressAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    WebActionBar.this.lambda$showAddress$10(valueAnimator2);
                }
            });
            this.addressAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    WebActionBar webActionBar = WebActionBar.this;
                    if (!webActionBar.addressing) {
                        webActionBar.addressEditText.setVisibility(8);
                    }
                    WebActionBar webActionBar2 = WebActionBar.this;
                    EditTextBoldCursor editTextBoldCursor = webActionBar2.addressEditText;
                    float f = z ? 1.0f : 0.0f;
                    webActionBar2.addressingProgress = f;
                    editTextBoldCursor.setAlpha(f);
                    WebActionBar webActionBar3 = WebActionBar.this;
                    webActionBar3.onAddressingProgress(webActionBar3.addressingProgress);
                    WebActionBar.this.menuButton.setTranslationX(AndroidUtilities.dp(56.0f) * WebActionBar.this.addressingProgress);
                    WebActionBar.this.forwardButton.setTranslationX(AndroidUtilities.dp(112.0f) * WebActionBar.this.addressingProgress);
                    WebActionBar.this.invalidate();
                }
            });
            this.addressAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.addressAnimator.setDuration(360L);
            this.addressAnimator.start();
        } else {
            float f = z ? 1.0f : 0.0f;
            this.addressingProgress = f;
            onAddressingProgress(f);
            invalidate();
            this.addressEditText.setAlpha(z ? 1.0f : 0.0f);
            this.addressEditText.setVisibility(z ? 0 : 8);
            this.menuButton.setTranslationX(AndroidUtilities.dp(56.0f) * this.addressingProgress);
            this.forwardButton.setTranslationX(AndroidUtilities.dp(112.0f) * this.addressingProgress);
            BackDrawable backDrawable = this.backButtonDrawable;
            if (!this.backButtonShown && !z) {
                r2 = 1.0f;
            }
            backDrawable.setRotation(r2, true);
        }
        AndroidUtilities.cancelRunOnUIThread(new Runnable() {
            @Override
            public final void run() {
                WebActionBar.this.showAddressKeyboard();
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                WebActionBar.this.showAddressKeyboard();
            }
        }, this.addressing ? 100L : 0L);
    }

    public void showSearch(final boolean z, boolean z2) {
        boolean z3 = false;
        if (this.searching == z) {
            return;
        }
        ValueAnimator valueAnimator = this.searchAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.searching = z;
        if (z2) {
            this.searchEditText.setVisibility(0);
            this.backButtonDrawable.setRotation((this.backButtonShown || z) ? 0.0f : 1.0f, true);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(this.searchingProgress, z ? 1.0f : 0.0f);
            this.searchAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    WebActionBar.this.lambda$showSearch$9(valueAnimator2);
                }
            });
            this.searchAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    WebActionBar webActionBar = WebActionBar.this;
                    if (!webActionBar.searching) {
                        webActionBar.searchEditText.setVisibility(8);
                        WebActionBar.this.searchEditText.setText("");
                    }
                    WebActionBar webActionBar2 = WebActionBar.this;
                    EditTextBoldCursor editTextBoldCursor = webActionBar2.searchEditText;
                    float f = z ? 1.0f : 0.0f;
                    webActionBar2.searchingProgress = f;
                    editTextBoldCursor.setAlpha(f);
                    WebActionBar.this.invalidate();
                    WebActionBar webActionBar3 = WebActionBar.this;
                    boolean z4 = webActionBar3.searching;
                    EditTextBoldCursor editTextBoldCursor2 = webActionBar3.searchEditText;
                    if (z4) {
                        editTextBoldCursor2.requestFocus();
                        AndroidUtilities.showKeyboard(WebActionBar.this.searchEditText);
                    } else {
                        editTextBoldCursor2.clearFocus();
                        AndroidUtilities.hideKeyboard(WebActionBar.this.searchEditText);
                    }
                }
            });
            this.searchAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.searchAnimator.setDuration(320L);
            this.searchAnimator.start();
        } else {
            this.searchingProgress = z ? 1.0f : 0.0f;
            invalidate();
            this.searchEditText.setAlpha(z ? 1.0f : 0.0f);
            this.searchEditText.setVisibility(z ? 0 : 8);
            BackDrawable backDrawable = this.backButtonDrawable;
            if (!this.backButtonShown && !z) {
                r2 = 1.0f;
            }
            backDrawable.setRotation(r2, true);
            if (this.searching) {
                this.searchEditText.requestFocus();
                AndroidUtilities.showKeyboard(this.searchEditText);
            } else {
                this.searchEditText.clearFocus();
                AndroidUtilities.hideKeyboard(this.searchEditText);
            }
        }
        boolean z4 = !z;
        AndroidUtilities.updateViewShow(this.forwardButton, z4, true, z2);
        AndroidUtilities.updateViewShow(this.menuButton, z4, true, z2);
        ImageView imageView = this.clearButton;
        if (this.searchEditText.length() > 0 && this.searching) {
            z3 = true;
        }
        AndroidUtilities.updateViewShow(imageView, z3, true, z2);
    }

    public void swap() {
        Title[] titleArr = this.titles;
        Title title = titleArr[0];
        titleArr[0] = titleArr[1];
        titleArr[1] = title;
        float[] fArr = this.progress;
        float f = fArr[0];
        fArr[0] = fArr[1];
        fArr[1] = f;
        int backgroundColor = getBackgroundColor(0);
        setBackgroundColor(0, getBackgroundColor(1));
        setBackgroundColor(1, backgroundColor);
        invalidate();
    }

    public int topPadding() {
        if (this.occupyStatusBar) {
            return AndroidUtilities.statusBarHeight;
        }
        return 0;
    }

    @Override
    protected boolean verifyDrawable(Drawable drawable) {
        return true;
    }
}
