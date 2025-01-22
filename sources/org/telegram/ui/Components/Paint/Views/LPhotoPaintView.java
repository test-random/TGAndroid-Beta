package org.telegram.ui.Components.Paint.Views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Looper;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Pair;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.Consumer;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.BubbleActivity;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.IPhotoPaintView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Brush;
import org.telegram.ui.Components.Paint.ColorPickerBottomSheet;
import org.telegram.ui.Components.Paint.PaintTypeface;
import org.telegram.ui.Components.Paint.Painting;
import org.telegram.ui.Components.Paint.PersistColorPalette;
import org.telegram.ui.Components.Paint.PhotoFace;
import org.telegram.ui.Components.Paint.RenderView;
import org.telegram.ui.Components.Paint.Swatch;
import org.telegram.ui.Components.Paint.UndoStore;
import org.telegram.ui.Components.Paint.Views.EntitiesContainerView;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Paint.Views.LPhotoPaintView;
import org.telegram.ui.Components.Paint.Views.PaintTextOptionsView;
import org.telegram.ui.Components.Paint.Views.PaintToolsView;
import org.telegram.ui.Components.Paint.Views.PaintWeightChooserView;
import org.telegram.ui.Components.Point;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.Size;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.telegram.ui.Components.ThanosEffect;
import org.telegram.ui.Components.TrendingStickersLayout;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.Stories.recorder.EmojiBottomSheet;

public abstract class LPhotoPaintView extends SizeNotifierFrameLayoutPhoto implements IPhotoPaintView, PaintToolsView.Delegate, EntityView.EntityViewDelegate, PaintTextOptionsView.Delegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate, NotificationCenter.NotificationCenterDelegate {
    private float baseScale;
    private Bitmap bitmapToEdit;
    public FrameLayout bottomLayout;
    private boolean bottomPanelIgnoreOnce;
    public PaintCancelView cancelButton;
    private TextView cancelTextButton;
    private final Paint clearPaint;
    private Paint colorPickerRainbowPaint;
    private Swatch colorSwatch;
    private Paint colorSwatchOutlinePaint;
    private Paint colorSwatchPaint;
    private PaintColorsListView colorsListView;
    private int currentAccount;
    private MediaController.CropState currentCropState;
    private EntityView currentEntityView;
    private boolean destroyed;
    public PaintDoneView doneButton;
    private TextView doneTextButton;
    private boolean drawShadow;
    private TextView drawTab;
    private boolean editingText;
    private int emojiPadding;
    private EmojiView emojiView;
    public boolean emojiViewVisible;
    public boolean emojiViewWasVisible;
    private int emojiWasPadding;
    public EntitiesContainerView entitiesView;
    private ArrayList faces;
    private Bitmap facesBitmap;
    private boolean fillShapes;
    private boolean ignoreLayout;
    private boolean ignoreToolChangeAnimationOnce;
    private float imageHeight;
    private float imageWidth;
    private boolean inBubbleMode;
    private float inputTransformX;
    private float inputTransformY;
    public boolean isAnimatePopupClosing;
    private boolean isColorListShown;
    private boolean isTypefaceMenuShown;
    private int keyboardHeight;
    private int keyboardHeightLand;
    public boolean keyboardVisible;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private BigInteger lcm;
    private Matrix matrix;
    private float offsetTranslationY;
    private Runnable onDoneButtonClickedListener;
    private Runnable openKeyboardRunnable;
    private int originalBitmapRotation;
    public FrameLayout overlayLayout;
    private PaintToolsView paintToolsView;
    private Size paintingSize;
    private float panTranslationProgress;
    private float panTranslationY;
    private float pany;
    private FrameLayout pipetteContainerLayout;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout;
    private Rect popupRect;
    private ActionBarPopupWindow popupWindow;
    private int[] pos2;
    private float[] position;
    private DispatchQueue queue;
    private View renderInputView;
    private RenderView renderView;
    private Theme.ResourcesProvider resourcesProvider;
    private float scale;
    private int selectedTextType;
    private FrameLayout selectionContainerView;
    private final AnimatedFloat shadowAlpha;
    private final Paint shadowPaint;
    private TextView stickerTab;
    public LinearLayout tabsLayout;
    private int tabsNewSelectedIndex;
    private int tabsSelectedIndex;
    private ValueAnimator tabsSelectionAnimator;
    private float tabsSelectionProgress;
    public PaintTextOptionsView textOptionsView;
    private TextView textTab;
    private ThanosEffect thanosEffect;
    private Paint toolsPaint;
    private SpringAnimation toolsTransformAnimation;
    private float toolsTransformProgress;
    private FrameLayout topLayout;
    private float transformX;
    private float transformY;
    private boolean translateBottomPanelAfterResize;
    private PaintTypefaceListView typefaceListView;
    private Paint typefaceMenuBackgroundPaint;
    private Paint typefaceMenuOutlinePaint;
    private SpringAnimation typefaceMenuTransformAnimation;
    private float typefaceMenuTransformProgress;
    private TextView undoAllButton;
    private ImageView undoButton;
    private UndoStore undoStore;
    private boolean waitingForKeyboardOpen;
    public PaintWeightChooserView weightChooserView;
    private PaintWeightChooserView.ValueOverride weightDefaultValueOverride;
    private LinearLayout zoomOutButton;
    private ImageView zoomOutImage;
    private TextView zoomOutText;
    private boolean zoomOutVisible;

    public class AnonymousClass22 implements EmojiView.EmojiViewDelegate {
        int innerTextChange;

        AnonymousClass22() {
        }

        public void lambda$onClearEmojiRecent$0(AlertDialog alertDialog, int i) {
            LPhotoPaintView.this.emojiView.clearRecentEmoji();
        }

        @Override
        public boolean canSchedule() {
            return EmojiView.EmojiViewDelegate.CC.$default$canSchedule(this);
        }

        @Override
        public long getDialogId() {
            return EmojiView.EmojiViewDelegate.CC.$default$getDialogId(this);
        }

        @Override
        public float getProgressToSearchOpened() {
            return EmojiView.EmojiViewDelegate.CC.$default$getProgressToSearchOpened(this);
        }

        @Override
        public int getThreadId() {
            return EmojiView.EmojiViewDelegate.CC.$default$getThreadId(this);
        }

        @Override
        public void invalidateEnterView() {
            EmojiView.EmojiViewDelegate.CC.$default$invalidateEnterView(this);
        }

        @Override
        public boolean isExpanded() {
            return EmojiView.EmojiViewDelegate.CC.$default$isExpanded(this);
        }

        @Override
        public boolean isInScheduleMode() {
            return EmojiView.EmojiViewDelegate.CC.$default$isInScheduleMode(this);
        }

        @Override
        public boolean isSearchOpened() {
            return EmojiView.EmojiViewDelegate.CC.$default$isSearchOpened(this);
        }

        @Override
        public boolean isUserSelf() {
            return EmojiView.EmojiViewDelegate.CC.$default$isUserSelf(this);
        }

        @Override
        public void onAnimatedEmojiUnlockClick() {
        }

        @Override
        public boolean onBackspace() {
            EditTextOutline editText = ((TextPaintView) LPhotoPaintView.this.currentEntityView).getEditText();
            if (editText == null || editText.length() == 0) {
                return false;
            }
            editText.dispatchKeyEvent(new KeyEvent(0, 67));
            return true;
        }

        @Override
        public void onClearEmojiRecent() {
            AlertDialog.Builder builder = new AlertDialog.Builder(LPhotoPaintView.this.getContext(), LPhotoPaintView.this.resourcesProvider);
            builder.setTitle(LocaleController.getString(R.string.ClearRecentEmojiTitle));
            builder.setMessage(LocaleController.getString(R.string.ClearRecentEmojiText));
            builder.setPositiveButton(LocaleController.getString(R.string.ClearButton), new AlertDialog.OnButtonClickListener() {
                @Override
                public final void onClick(AlertDialog alertDialog, int i) {
                    LPhotoPaintView.AnonymousClass22.this.lambda$onClearEmojiRecent$0(alertDialog, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            builder.show();
        }

        @Override
        public void onCustomEmojiSelected(long j, TLRPC.Document document, String str, boolean z) {
            EditTextOutline editText = ((TextPaintView) LPhotoPaintView.this.currentEntityView).getEditText();
            if (editText == null) {
                return;
            }
            int selectionEnd = editText.getSelectionEnd();
            if (selectionEnd < 0) {
                selectionEnd = 0;
            }
            try {
                try {
                    this.innerTextChange = 2;
                    SpannableString spannableString = new SpannableString(str);
                    spannableString.setSpan(document != null ? new AnimatedEmojiSpan(document, editText.getPaint().getFontMetricsInt()) : new AnimatedEmojiSpan(j, editText.getPaint().getFontMetricsInt()), 0, spannableString.length(), 33);
                    editText.setText(editText.getText().insert(selectionEnd, spannableString));
                    int length = selectionEnd + spannableString.length();
                    editText.setSelection(length, length);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                this.innerTextChange = 0;
            } catch (Throwable th) {
                this.innerTextChange = 0;
                throw th;
            }
        }

        @Override
        public void onEmojiSelected(String str) {
            TextPaintView textPaintView;
            EditTextOutline editText;
            Emoji.EmojiSpan[] emojiSpanArr;
            if ((LPhotoPaintView.this.currentEntityView instanceof TextPaintView) && (editText = (textPaintView = (TextPaintView) LPhotoPaintView.this.currentEntityView).getEditText()) != null) {
                int selectionEnd = editText.getSelectionEnd();
                if (selectionEnd < 0) {
                    selectionEnd = 0;
                }
                try {
                    try {
                        this.innerTextChange = 2;
                        CharSequence replaceEmoji = Emoji.replaceEmoji(str, textPaintView.getFontMetricsInt(), false);
                        if ((replaceEmoji instanceof Spanned) && (emojiSpanArr = (Emoji.EmojiSpan[]) ((Spanned) replaceEmoji).getSpans(0, replaceEmoji.length(), Emoji.EmojiSpan.class)) != null) {
                            for (Emoji.EmojiSpan emojiSpan : emojiSpanArr) {
                                emojiSpan.scale = 0.85f;
                            }
                        }
                        editText.setText(editText.getText().insert(selectionEnd, replaceEmoji));
                        int length = selectionEnd + replaceEmoji.length();
                        editText.setSelection(length, length);
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    this.innerTextChange = 0;
                } catch (Throwable th) {
                    this.innerTextChange = 0;
                    throw th;
                }
            }
        }

        @Override
        public void onEmojiSettingsClick(ArrayList arrayList) {
            EmojiView.EmojiViewDelegate.CC.$default$onEmojiSettingsClick(this, arrayList);
        }

        @Override
        public void lambda$onGifSelected$1(View view, Object obj, String str, Object obj2, boolean z, int i) {
            EmojiView.EmojiViewDelegate.CC.$default$onGifSelected(this, view, obj, str, obj2, z, i);
        }

        @Override
        public void onSearchOpenClose(int i) {
            EmojiView.EmojiViewDelegate.CC.$default$onSearchOpenClose(this, i);
        }

        @Override
        public void onShowStickerSet(TLRPC.StickerSet stickerSet, TLRPC.InputStickerSet inputStickerSet, boolean z) {
            EmojiView.EmojiViewDelegate.CC.$default$onShowStickerSet(this, stickerSet, inputStickerSet, z);
        }

        @Override
        public void onStickerSelected(View view, TLRPC.Document document, String str, Object obj, MessageObject.SendAnimationData sendAnimationData, boolean z, int i) {
            EmojiView.EmojiViewDelegate.CC.$default$onStickerSelected(this, view, document, str, obj, sendAnimationData, z, i);
        }

        @Override
        public void onStickerSetAdd(TLRPC.StickerSetCovered stickerSetCovered) {
            EmojiView.EmojiViewDelegate.CC.$default$onStickerSetAdd(this, stickerSetCovered);
        }

        @Override
        public void onStickerSetRemove(TLRPC.StickerSetCovered stickerSetCovered) {
            EmojiView.EmojiViewDelegate.CC.$default$onStickerSetRemove(this, stickerSetCovered);
        }

        @Override
        public void onStickersGroupClick(long j) {
            EmojiView.EmojiViewDelegate.CC.$default$onStickersGroupClick(this, j);
        }

        @Override
        public void onStickersSettingsClick() {
            EmojiView.EmojiViewDelegate.CC.$default$onStickersSettingsClick(this);
        }

        @Override
        public void onTabOpened(int i) {
            EmojiView.EmojiViewDelegate.CC.$default$onTabOpened(this, i);
        }

        @Override
        public void showTrendingStickersAlert(TrendingStickersLayout trendingStickersLayout) {
            EmojiView.EmojiViewDelegate.CC.$default$showTrendingStickersAlert(this, trendingStickersLayout);
        }
    }

    public class PopupButton extends LinearLayout {
        ImageView checkView;
        ImageView image2View;
        ValueAnimator imageSwitchAnimator;
        boolean imageSwitchFill;
        float imageSwitchT;
        ImageView imageView;
        FrameLayout imagesView;
        public TextView textView;

        public PopupButton(Context context) {
            super(context);
            setOrientation(0);
            setBackground(Theme.getSelectorDrawable(Theme.getColor(Theme.key_listSelector, LPhotoPaintView.this.resourcesProvider), false));
            FrameLayout frameLayout = new FrameLayout(context) {
                Path path = new Path();

                @Override
                protected boolean drawChild(Canvas canvas, View view, long j) {
                    boolean z;
                    PopupButton popupButton = PopupButton.this;
                    if (popupButton.imageSwitchAnimator == null || (!((z = popupButton.imageSwitchFill) && view == popupButton.image2View) && (z || view != popupButton.imageView))) {
                        return super.drawChild(canvas, view, j);
                    }
                    float f = popupButton.imageSwitchT;
                    if (!z) {
                        f = 1.0f - f;
                    }
                    canvas.save();
                    this.path.rewind();
                    this.path.addCircle(getMeasuredWidth() / 2.0f, getMeasuredHeight() / 2.0f, (f * getMeasuredWidth()) / 2.0f, Path.Direction.CW);
                    canvas.clipPath(this.path);
                    boolean drawChild = super.drawChild(canvas, view, j);
                    canvas.restore();
                    return drawChild;
                }
            };
            this.imagesView = frameLayout;
            addView(frameLayout, LayoutHelper.createLinear(-2, -2, 19, 16, 0, 16, 0));
            ImageView imageView = new ImageView(context);
            this.imageView = imageView;
            ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
            imageView.setScaleType(scaleType);
            ImageView imageView2 = this.imageView;
            int i = Theme.key_actionBarDefaultSubmenuItem;
            imageView2.setColorFilter(LPhotoPaintView.this.getThemedColor(i));
            this.imagesView.addView(this.imageView, LayoutHelper.createFrame(-2, -2, 17));
            ImageView imageView3 = new ImageView(context);
            this.image2View = imageView3;
            imageView3.setScaleType(scaleType);
            this.image2View.setColorFilter(LPhotoPaintView.this.getThemedColor(i));
            this.image2View.setVisibility(8);
            this.imagesView.addView(this.image2View, LayoutHelper.createFrame(-2, -2, 17));
            TextView textView = new TextView(context);
            this.textView = textView;
            textView.setTextColor(LPhotoPaintView.this.getThemedColor(i));
            this.textView.setTextSize(1, 16.0f);
            addView(this.textView, LayoutHelper.createLinear(-2, -2, 19, 0, 0, 16, 0));
            ImageView imageView4 = new ImageView(context);
            this.checkView = imageView4;
            imageView4.setImageResource(R.drawable.msg_text_check);
            this.checkView.setScaleType(scaleType);
            this.checkView.setColorFilter(new PorterDuffColorFilter(LPhotoPaintView.this.getThemedColor(Theme.key_radioBackgroundChecked), PorterDuff.Mode.MULTIPLY));
            this.checkView.setVisibility(8);
            addView(this.checkView, LayoutHelper.createLinear(50, -1));
        }

        public void lambda$setIcon$0(boolean z, ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.imageSwitchT = floatValue;
            if (!z) {
                this.imageView.setAlpha(1.0f - floatValue);
            }
            this.imagesView.invalidate();
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return true;
        }

        @Override
        public boolean performClick() {
            if (LPhotoPaintView.this.popupWindow != null && LPhotoPaintView.this.popupWindow.isShowing()) {
                LPhotoPaintView.this.popupWindow.dismiss(true);
            }
            return super.performClick();
        }

        public void setIcon(int i) {
            setIcon(i, true, false);
        }

        public void setIcon(int i, final boolean z, boolean z2) {
            if (!z2) {
                this.imageView.setImageResource(i);
                return;
            }
            ValueAnimator valueAnimator = this.imageSwitchAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.imageSwitchAnimator = null;
                setIcon(i, false, false);
                return;
            }
            this.imageSwitchFill = z;
            this.image2View.setImageResource(i);
            this.image2View.setVisibility(0);
            this.image2View.setAlpha(1.0f);
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.imageSwitchAnimator = ofFloat;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    LPhotoPaintView.PopupButton.this.lambda$setIcon$0(z, valueAnimator2);
                }
            });
            this.imageSwitchAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    super.onAnimationEnd(animator);
                    PopupButton popupButton = PopupButton.this;
                    ImageView imageView = popupButton.imageView;
                    popupButton.imageView = popupButton.image2View;
                    popupButton.image2View = imageView;
                    imageView.bringToFront();
                    PopupButton.this.image2View.setVisibility(8);
                    PopupButton.this.imageSwitchAnimator = null;
                }
            });
            this.imageSwitchAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.imageSwitchAnimator.setDuration(420L);
            this.imageSwitchAnimator.start();
        }

        @Override
        public void setSelected(boolean z) {
            this.checkView.setVisibility(z ? 0 : 8);
        }

        public void setText(CharSequence charSequence) {
            this.textView.setText(charSequence);
        }
    }

    public static class StickerPosition {
        private float angle;
        private Point position;
        private float scale;

        StickerPosition(Point point, float f, float f2) {
            this.position = point;
            this.scale = f;
            this.angle = f2;
        }
    }

    public LPhotoPaintView(final Context context, Activity activity, final int i, Bitmap bitmap, final Bitmap bitmap2, int i2, ArrayList arrayList, MediaController.CropState cropState, final Runnable runnable, final Theme.ResourcesProvider resourcesProvider) {
        super(context, activity, true);
        int i3;
        Emoji.EmojiSpan[] emojiSpanArr;
        PhotoView photoView;
        PhotoView photoView2;
        this.tabsSelectedIndex = 0;
        this.tabsNewSelectedIndex = -1;
        this.weightDefaultValueOverride = new PaintWeightChooserView.ValueOverride() {
            @Override
            public float get() {
                Brush currentBrush = LPhotoPaintView.this.renderView.getCurrentBrush();
                return currentBrush == null ? PersistColorPalette.getInstance(LPhotoPaintView.this.currentAccount).getCurrentWeight() : PersistColorPalette.getInstance(LPhotoPaintView.this.currentAccount).getWeight(String.valueOf(Brush.BRUSHES_LIST.indexOf(currentBrush)), currentBrush.getDefaultWeight());
            }

            @Override
            public void set(float f) {
                PersistColorPalette.getInstance(LPhotoPaintView.this.currentAccount).setWeight(String.valueOf(Brush.BRUSHES_LIST.indexOf(LPhotoPaintView.this.renderView.getCurrentBrush())), f);
                LPhotoPaintView.this.colorSwatch.brushWeight = f;
                LPhotoPaintView lPhotoPaintView = LPhotoPaintView.this;
                lPhotoPaintView.setCurrentSwatch(lPhotoPaintView.colorSwatch, true);
            }
        };
        this.typefaceMenuOutlinePaint = new Paint(1);
        this.typefaceMenuBackgroundPaint = new Paint(1);
        this.colorPickerRainbowPaint = new Paint(1);
        this.colorSwatchPaint = new Paint(1);
        this.colorSwatchOutlinePaint = new Paint(1);
        this.colorSwatch = new Swatch(-1, 1.0f, 0.016773745f);
        this.fillShapes = false;
        this.toolsPaint = new Paint(1);
        this.zoomOutVisible = false;
        this.shadowAlpha = new AnimatedFloat(this, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
        this.shadowPaint = new Paint(1);
        Paint paint = new Paint(1);
        this.clearPaint = paint;
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        this.matrix = new Matrix();
        this.position = new float[2];
        this.pos2 = new int[2];
        this.openKeyboardRunnable = new Runnable() {
            @Override
            public void run() {
                if (LPhotoPaintView.this.currentEntityView instanceof TextPaintView) {
                    EditTextOutline editText = ((TextPaintView) LPhotoPaintView.this.currentEntityView).getEditText();
                    if (LPhotoPaintView.this.destroyed || editText == null || !LPhotoPaintView.this.waitingForKeyboardOpen || LPhotoPaintView.this.keyboardVisible || AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow || !AndroidUtilities.isTablet()) {
                        return;
                    }
                    editText.requestFocus();
                    AndroidUtilities.showKeyboard(editText);
                    AndroidUtilities.cancelRunOnUIThread(LPhotoPaintView.this.openKeyboardRunnable);
                    AndroidUtilities.runOnUIThread(LPhotoPaintView.this.openKeyboardRunnable, 100L);
                }
            }
        };
        setDelegate(this);
        this.currentAccount = i;
        this.resourcesProvider = new Theme.ResourcesProvider() {
            @Override
            public void applyServiceShaderMatrix(int i4, int i5, float f, float f2) {
                Theme.applyServiceShaderMatrix(i4, i5, f, f2);
            }

            @Override
            public ColorFilter getAnimatedEmojiColorFilter() {
                ColorFilter colorFilter;
                colorFilter = Theme.chat_animatedEmojiTextColorFilter;
                return colorFilter;
            }

            @Override
            public final int getColor(int i4) {
                int lambda$new$0;
                lambda$new$0 = LPhotoPaintView.lambda$new$0(Theme.ResourcesProvider.this, i4);
                return lambda$new$0;
            }

            @Override
            public int getColorOrDefault(int i4) {
                int color;
                color = getColor(i4);
                return color;
            }

            @Override
            public int getCurrentColor(int i4) {
                int color;
                color = getColor(i4);
                return color;
            }

            @Override
            public Drawable getDrawable(String str) {
                return Theme.ResourcesProvider.CC.$default$getDrawable(this, str);
            }

            @Override
            public Paint getPaint(String str) {
                Paint themePaint;
                themePaint = Theme.getThemePaint(str);
                return themePaint;
            }

            @Override
            public boolean hasGradientService() {
                return Theme.ResourcesProvider.CC.$default$hasGradientService(this);
            }

            @Override
            public boolean isDark() {
                boolean isCurrentThemeDark;
                isCurrentThemeDark = Theme.isCurrentThemeDark();
                return isCurrentThemeDark;
            }

            @Override
            public void setAnimatedColor(int i4, int i5) {
                Theme.ResourcesProvider.CC.$default$setAnimatedColor(this, i4, i5);
            }
        };
        this.currentCropState = cropState;
        this.inBubbleMode = context instanceof BubbleActivity;
        final PersistColorPalette persistColorPalette = PersistColorPalette.getInstance(i);
        persistColorPalette.resetCurrentColor();
        this.colorSwatch.color = persistColorPalette.getCurrentColor();
        this.colorSwatch.brushWeight = persistColorPalette.getCurrentWeight();
        this.queue = new DispatchQueue("Paint");
        this.bitmapToEdit = bitmap;
        this.facesBitmap = bitmap2;
        this.originalBitmapRotation = i2;
        UndoStore undoStore = new UndoStore();
        this.undoStore = undoStore;
        undoStore.setDelegate(new UndoStore.UndoStoreDelegate() {
            @Override
            public final void historyChanged() {
                LPhotoPaintView.this.lambda$new$1();
            }
        });
        RenderView renderView = new RenderView(context, new Painting(getPaintingSize(), bitmap2, i2, null), this.bitmapToEdit, null, null) {
            @Override
            public void selectBrush(Brush brush) {
                int indexOf = Brush.BRUSHES_LIST.indexOf(brush);
                int i4 = indexOf + 1;
                if (i4 <= 1 || bitmap2 != null) {
                    indexOf = i4;
                }
                LPhotoPaintView.this.paintToolsView.select(indexOf);
                LPhotoPaintView.this.onBrushSelected(brush);
            }
        };
        this.renderView = renderView;
        renderView.setDelegate(new RenderView.RenderViewDelegate() {
            @Override
            public void invalidateInputView() {
                if (LPhotoPaintView.this.renderInputView != null) {
                    LPhotoPaintView.this.renderInputView.invalidate();
                }
            }

            @Override
            public void onBeganDrawing() {
                if (LPhotoPaintView.this.currentEntityView != null) {
                    LPhotoPaintView.this.selectEntity(null);
                }
                LPhotoPaintView.this.weightChooserView.setViewHidden(true);
            }

            @Override
            public void onFinishedDrawing(boolean z) {
                LPhotoPaintView.this.undoStore.getDelegate().historyChanged();
                LPhotoPaintView.this.weightChooserView.setViewHidden(false);
            }

            @Override
            public void onFirstDraw() {
                runnable.run();
            }

            @Override
            public void resetBrush() {
                if (LPhotoPaintView.this.ignoreToolChangeAnimationOnce) {
                    LPhotoPaintView.this.ignoreToolChangeAnimationOnce = false;
                } else {
                    LPhotoPaintView.this.paintToolsView.select(1);
                    LPhotoPaintView.this.onBrushSelected((Brush) Brush.BRUSHES_LIST.get(0));
                }
            }

            @Override
            public boolean shouldDraw() {
                boolean z = LPhotoPaintView.this.currentEntityView == null;
                if (!z) {
                    LPhotoPaintView.this.selectEntity(null);
                }
                return z;
            }
        });
        this.renderView.setUndoStore(this.undoStore);
        this.renderView.setQueue(this.queue);
        this.renderView.setVisibility(4);
        addView(this.renderView, LayoutHelper.createFrame(-1, -1, 51));
        View view = new View(context) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (LPhotoPaintView.this.renderView != null) {
                    LPhotoPaintView.this.renderView.onDrawForInput(canvas);
                }
            }
        };
        this.renderInputView = view;
        view.setVisibility(4);
        addView(this.renderInputView, LayoutHelper.createFrame(-1, -1, 51));
        EntitiesContainerView entitiesContainerView = new EntitiesContainerView(context, new EntitiesContainerView.EntitiesContainerViewDelegate() {
            @Override
            public void onEntityDeselect() {
                LPhotoPaintView.this.selectEntity(null);
            }

            @Override
            public EntityView onSelectedEntityRequest() {
                return LPhotoPaintView.this.currentEntityView;
            }
        }) {
            long lastUpdate;
            Paint linePaint = new Paint();
            float stickyXAlpha;
            float stickyYAlpha;

            {
                setWillNotDraw(false);
                this.linePaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
                this.linePaint.setStyle(Paint.Style.STROKE);
                this.linePaint.setColor(-1);
            }

            @Override
            protected void onDraw(android.graphics.Canvas r14) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Views.LPhotoPaintView.AnonymousClass6.onDraw(android.graphics.Canvas):void");
            }
        };
        this.entitiesView = entitiesContainerView;
        addView(entitiesContainerView);
        if (arrayList != null && !arrayList.isEmpty()) {
            int size = arrayList.size();
            int i4 = 0;
            while (i4 < size) {
                VideoEditedInfo.MediaEntity mediaEntity = (VideoEditedInfo.MediaEntity) arrayList.get(i4);
                byte b = mediaEntity.type;
                if (b == 0) {
                    StickerView createSticker = createSticker(mediaEntity.parentObject, mediaEntity.document, false);
                    if ((mediaEntity.subType & 2) != 0) {
                        createSticker.mirror();
                    }
                    i3 = size;
                    photoView2 = createSticker;
                } else if (b == 1) {
                    TextPaintView createText = createText(false);
                    createText.setType(mediaEntity.subType);
                    createText.setTypeface(mediaEntity.textTypeface);
                    createText.setBaseFontSize(mediaEntity.fontSize);
                    SpannableString spannableString = new SpannableString(mediaEntity.text);
                    Iterator<VideoEditedInfo.EmojiEntity> it = mediaEntity.entities.iterator();
                    while (it.hasNext()) {
                        VideoEditedInfo.EmojiEntity next = it.next();
                        AnimatedEmojiSpan animatedEmojiSpan = new AnimatedEmojiSpan(next.document_id, createText.getFontMetricsInt());
                        int i5 = next.offset;
                        spannableString.setSpan(animatedEmojiSpan, i5, next.length + i5, 33);
                        size = size;
                    }
                    i3 = size;
                    CharSequence replaceEmoji = Emoji.replaceEmoji(spannableString, createText.getFontMetricsInt(), false);
                    if ((replaceEmoji instanceof Spanned) && (emojiSpanArr = (Emoji.EmojiSpan[]) ((Spanned) replaceEmoji).getSpans(0, replaceEmoji.length(), Emoji.EmojiSpan.class)) != null) {
                        for (Emoji.EmojiSpan emojiSpan : emojiSpanArr) {
                            emojiSpan.scale = 0.85f;
                        }
                    }
                    createText.setText(replaceEmoji);
                    setTextAlignment(createText, mediaEntity.textAlign);
                    Swatch swatch = createText.getSwatch();
                    swatch.color = mediaEntity.color;
                    createText.setSwatch(swatch);
                    photoView = createText;
                    photoView.setX((mediaEntity.x * this.paintingSize.width) - ((mediaEntity.viewWidth * (1.0f - mediaEntity.scale)) / 2.0f));
                    photoView.setY((mediaEntity.y * this.paintingSize.height) - ((mediaEntity.viewHeight * (1.0f - mediaEntity.scale)) / 2.0f));
                    photoView.setPosition(new Point(photoView.getX() + (mediaEntity.viewWidth / 2.0f), photoView.getY() + (mediaEntity.viewHeight / 2.0f)));
                    photoView.setScale(mediaEntity.scale);
                    double d = -mediaEntity.rotation;
                    Double.isNaN(d);
                    photoView.setRotation((float) ((d / 3.141592653589793d) * 180.0d));
                    i4++;
                    size = i3;
                } else {
                    i3 = size;
                    if (b == 2) {
                        PhotoView createPhoto = createPhoto(mediaEntity.text, false);
                        createPhoto.preloadSegmented(mediaEntity.segmentedPath);
                        if ((mediaEntity.subType & 2) != 0) {
                            createPhoto.mirror();
                        }
                        photoView2 = createPhoto;
                        if ((mediaEntity.subType & 16) != 0) {
                            createPhoto.toggleSegmented(false);
                            photoView2 = createPhoto;
                        }
                    } else {
                        i4++;
                        size = i3;
                    }
                }
                ViewGroup.LayoutParams layoutParams = photoView2.getLayoutParams();
                layoutParams.width = mediaEntity.viewWidth;
                layoutParams.height = mediaEntity.viewHeight;
                photoView = photoView2;
                photoView.setX((mediaEntity.x * this.paintingSize.width) - ((mediaEntity.viewWidth * (1.0f - mediaEntity.scale)) / 2.0f));
                photoView.setY((mediaEntity.y * this.paintingSize.height) - ((mediaEntity.viewHeight * (1.0f - mediaEntity.scale)) / 2.0f));
                photoView.setPosition(new Point(photoView.getX() + (mediaEntity.viewWidth / 2.0f), photoView.getY() + (mediaEntity.viewHeight / 2.0f)));
                photoView.setScale(mediaEntity.scale);
                double d2 = -mediaEntity.rotation;
                Double.isNaN(d2);
                photoView.setRotation((float) ((d2 / 3.141592653589793d) * 180.0d));
                i4++;
                size = i3;
            }
        }
        this.entitiesView.setVisibility(4);
        FrameLayout frameLayout = new FrameLayout(context) {
            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return false;
            }
        };
        this.selectionContainerView = frameLayout;
        addView(frameLayout);
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.topLayout = frameLayout2;
        frameLayout2.setPadding(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f));
        FrameLayout frameLayout3 = this.topLayout;
        GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TOP_BOTTOM;
        frameLayout3.setBackground(new GradientDrawable(orientation, new int[]{-16777216, 0}));
        addView(this.topLayout, LayoutHelper.createFrame(-1, -2, 48));
        ImageView imageView = new ImageView(context);
        this.undoButton = imageView;
        imageView.setImageResource(R.drawable.photo_undo2);
        this.undoButton.setPadding(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f));
        this.undoButton.setBackground(Theme.createSelectorDrawable(1090519039));
        this.undoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                LPhotoPaintView.this.lambda$new$2(view2);
            }
        });
        this.undoButton.setAlpha(0.6f);
        this.undoButton.setClickable(false);
        this.topLayout.addView(this.undoButton, LayoutHelper.createFrame(32, 32.0f, 51, 12.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.zoomOutButton = linearLayout;
        linearLayout.setOrientation(0);
        this.zoomOutButton.setBackground(Theme.createSelectorDrawable(822083583, 7));
        this.zoomOutButton.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        TextView textView = new TextView(context);
        this.zoomOutText = textView;
        textView.setTextColor(-1);
        this.zoomOutText.setTypeface(AndroidUtilities.bold());
        this.zoomOutText.setTextSize(1, 16.0f);
        this.zoomOutText.setText(LocaleController.getString(R.string.PhotoEditorZoomOut));
        ImageView imageView2 = new ImageView(context);
        this.zoomOutImage = imageView2;
        imageView2.setImageResource(R.drawable.photo_zoomout);
        this.zoomOutButton.addView(this.zoomOutImage, LayoutHelper.createLinear(24, 24, 16, 0, 0, 8, 0));
        this.zoomOutButton.addView(this.zoomOutText, LayoutHelper.createLinear(-2, -2, 16));
        this.zoomOutButton.setAlpha(0.0f);
        this.zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                LPhotoPaintView.lambda$new$3(view2);
            }
        });
        this.topLayout.addView(this.zoomOutButton, LayoutHelper.createFrame(-2, 32, 17));
        TextView textView2 = new TextView(context);
        this.undoAllButton = textView2;
        textView2.setBackground(Theme.createSelectorDrawable(822083583, 7));
        this.undoAllButton.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.undoAllButton.setText(LocaleController.getString(R.string.PhotoEditorClearAll));
        this.undoAllButton.setGravity(16);
        this.undoAllButton.setTextColor(-1);
        this.undoAllButton.setTypeface(AndroidUtilities.bold());
        this.undoAllButton.setTextSize(1, 16.0f);
        this.undoAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                LPhotoPaintView.this.lambda$new$4(view2);
            }
        });
        this.undoAllButton.setAlpha(0.6f);
        this.topLayout.addView(this.undoAllButton, LayoutHelper.createFrame(-2, 32.0f, 5, 0.0f, 0.0f, 4.0f, 0.0f));
        TextView textView3 = new TextView(context);
        this.cancelTextButton = textView3;
        textView3.setBackground(Theme.createSelectorDrawable(822083583, 7));
        this.cancelTextButton.setText(LocaleController.getString(R.string.Clear));
        this.cancelTextButton.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.cancelTextButton.setGravity(16);
        this.cancelTextButton.setTextColor(-1);
        this.cancelTextButton.setTypeface(AndroidUtilities.bold());
        this.cancelTextButton.setTextSize(1, 16.0f);
        this.cancelTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                LPhotoPaintView.this.lambda$new$5(view2);
            }
        });
        this.cancelTextButton.setAlpha(0.0f);
        this.cancelTextButton.setVisibility(8);
        this.topLayout.addView(this.cancelTextButton, LayoutHelper.createFrame(-2, 32.0f, 51, 4.0f, 0.0f, 0.0f, 0.0f));
        TextView textView4 = new TextView(context);
        this.doneTextButton = textView4;
        textView4.setBackground(Theme.createSelectorDrawable(822083583, 7));
        this.doneTextButton.setText(LocaleController.getString(R.string.Done));
        this.doneTextButton.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        this.doneTextButton.setGravity(16);
        this.doneTextButton.setTextColor(-1);
        this.doneTextButton.setTypeface(AndroidUtilities.bold());
        this.doneTextButton.setTextSize(1, 16.0f);
        this.doneTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                LPhotoPaintView.this.lambda$new$6(view2);
            }
        });
        this.doneTextButton.setAlpha(0.0f);
        this.doneTextButton.setVisibility(8);
        this.topLayout.addView(this.doneTextButton, LayoutHelper.createFrame(-2, 32.0f, 5, 0.0f, 0.0f, 4.0f, 0.0f));
        FrameLayout frameLayout4 = new FrameLayout(context) {
            private float lastRainbowX;
            private float lastRainbowY;
            private Path path = new Path();

            {
                setWillNotDraw(false);
                LPhotoPaintView.this.colorPickerRainbowPaint.setStyle(Paint.Style.STROKE);
                LPhotoPaintView.this.colorPickerRainbowPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            }

            private void checkRainbow(float f, float f2) {
                if (f == this.lastRainbowX && f2 == this.lastRainbowY) {
                    return;
                }
                this.lastRainbowX = f;
                this.lastRainbowY = f2;
                LPhotoPaintView.this.colorPickerRainbowPaint.setShader(new SweepGradient(f, f2, new int[]{-1356981, -1146130, -10452764, -16711681, -7352832, -256, -23296, -1356981}, (float[]) null));
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                ViewGroup barView = LPhotoPaintView.this.getBarView();
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(AndroidUtilities.lerp(barView.getLeft(), LPhotoPaintView.this.colorsListView.getLeft(), LPhotoPaintView.this.toolsTransformProgress), AndroidUtilities.lerp(barView.getTop(), LPhotoPaintView.this.colorsListView.getTop(), LPhotoPaintView.this.toolsTransformProgress), AndroidUtilities.lerp(barView.getRight(), LPhotoPaintView.this.colorsListView.getRight(), LPhotoPaintView.this.toolsTransformProgress), AndroidUtilities.lerp(barView.getBottom(), LPhotoPaintView.this.colorsListView.getBottom(), LPhotoPaintView.this.toolsTransformProgress));
                float lerp = AndroidUtilities.lerp(AndroidUtilities.dp(32.0f), AndroidUtilities.dp(24.0f), LPhotoPaintView.this.toolsTransformProgress);
                canvas.drawRoundRect(rectF, lerp, lerp, LPhotoPaintView.this.toolsPaint);
                if (barView.getChildCount() < 1 || LPhotoPaintView.this.toolsTransformProgress == 1.0f) {
                    return;
                }
                canvas.save();
                canvas.translate(barView.getLeft(), barView.getTop());
                View childAt = barView.getChildAt(0);
                if (barView instanceof PaintTextOptionsView) {
                    childAt = ((PaintTextOptionsView) barView).getColorClickableView();
                }
                View view2 = childAt;
                if (view2.getAlpha() != 0.0f) {
                    canvas.scale(view2.getScaleX(), view2.getScaleY(), view2.getPivotX(), view2.getPivotY());
                    LPhotoPaintView.this.colorPickerRainbowPaint.setAlpha((int) ((1.0f - LPhotoPaintView.this.toolsTransformProgress) * view2.getAlpha() * 255.0f));
                    int width = (view2.getWidth() - view2.getPaddingLeft()) - view2.getPaddingRight();
                    int height = (view2.getHeight() - view2.getPaddingTop()) - view2.getPaddingBottom();
                    float x = view2.getX() + view2.getPaddingLeft() + (width / 2.0f);
                    float y = view2.getY() + view2.getPaddingTop() + (height / 2.0f);
                    int i6 = LPhotoPaintView.this.colorSwatch.color;
                    if (LPhotoPaintView.this.tabsNewSelectedIndex != -1) {
                        LPhotoPaintView lPhotoPaintView = LPhotoPaintView.this;
                        ViewGroup viewGroup = (ViewGroup) lPhotoPaintView.getBarView(lPhotoPaintView.tabsNewSelectedIndex);
                        View childAt2 = (viewGroup == null ? barView : viewGroup).getChildAt(0);
                        if (viewGroup instanceof PaintTextOptionsView) {
                            childAt2 = ((PaintTextOptionsView) viewGroup).getColorClickableView();
                        }
                        x = AndroidUtilities.lerp(x, childAt2.getX() + childAt2.getPaddingLeft() + (((childAt2.getWidth() - childAt2.getPaddingLeft()) - childAt2.getPaddingRight()) / 2.0f), LPhotoPaintView.this.tabsSelectionProgress);
                        y = AndroidUtilities.lerp(y, childAt2.getY() + childAt2.getPaddingTop() + (((childAt2.getHeight() - childAt2.getPaddingTop()) - childAt2.getPaddingBottom()) / 2.0f), LPhotoPaintView.this.tabsSelectionProgress);
                    }
                    if (LPhotoPaintView.this.colorsListView != null && LPhotoPaintView.this.colorsListView.getChildCount() > 0) {
                        View childAt3 = LPhotoPaintView.this.colorsListView.getChildAt(0);
                        x = AndroidUtilities.lerp(x, (LPhotoPaintView.this.colorsListView.getX() - barView.getLeft()) + childAt3.getX() + (childAt3.getWidth() / 2.0f), LPhotoPaintView.this.toolsTransformProgress);
                        y = AndroidUtilities.lerp(y, (LPhotoPaintView.this.colorsListView.getY() - barView.getTop()) + childAt3.getY() + (childAt3.getHeight() / 2.0f), LPhotoPaintView.this.toolsTransformProgress);
                        i6 = ColorUtils.blendARGB(LPhotoPaintView.this.colorSwatch.color, persistColorPalette.getColor(0), LPhotoPaintView.this.toolsTransformProgress);
                    }
                    float f = x;
                    checkRainbow(f, y);
                    float min = (Math.min(width, height) / 2.0f) - AndroidUtilities.dp(0.5f);
                    if (LPhotoPaintView.this.colorsListView != null && LPhotoPaintView.this.colorsListView.getChildCount() > 0) {
                        View childAt4 = LPhotoPaintView.this.colorsListView.getChildAt(0);
                        min = AndroidUtilities.lerp(min, (Math.min((childAt4.getWidth() - childAt4.getPaddingLeft()) - childAt4.getPaddingRight(), (childAt4.getHeight() - childAt4.getPaddingTop()) - childAt4.getPaddingBottom()) / 2.0f) - AndroidUtilities.dp(2.0f), LPhotoPaintView.this.toolsTransformProgress);
                    }
                    float f2 = min;
                    rectF.set(f - f2, y - f2, f + f2, y + f2);
                    canvas.drawArc(rectF, 0.0f, 360.0f, false, LPhotoPaintView.this.colorPickerRainbowPaint);
                    LPhotoPaintView.this.colorSwatchPaint.setColor(i6);
                    LPhotoPaintView.this.colorSwatchPaint.setAlpha((int) (LPhotoPaintView.this.colorSwatchPaint.getAlpha() * view2.getAlpha()));
                    LPhotoPaintView.this.colorSwatchOutlinePaint.setColor(i6);
                    LPhotoPaintView.this.colorSwatchOutlinePaint.setAlpha((int) (view2.getAlpha() * 255.0f));
                    float dp = f2 - AndroidUtilities.dp(3.0f);
                    if (LPhotoPaintView.this.colorsListView != null && LPhotoPaintView.this.colorsListView.getSelectedColorIndex() != 0) {
                        dp = AndroidUtilities.lerp(f2 - AndroidUtilities.dp(3.0f), AndroidUtilities.dp(2.0f) + f2, LPhotoPaintView.this.toolsTransformProgress);
                    }
                    PaintColorsListView.drawColorCircle(canvas, f, y, dp, LPhotoPaintView.this.colorSwatchPaint.getColor());
                    if (LPhotoPaintView.this.colorsListView != null && LPhotoPaintView.this.colorsListView.getSelectedColorIndex() == 0) {
                        LPhotoPaintView.this.colorSwatchOutlinePaint.setAlpha((int) (LPhotoPaintView.this.colorSwatchOutlinePaint.getAlpha() * LPhotoPaintView.this.toolsTransformProgress * view2.getAlpha()));
                        canvas.drawCircle(f, y, f2 - ((AndroidUtilities.dp(3.0f) + LPhotoPaintView.this.colorSwatchOutlinePaint.getStrokeWidth()) * (1.0f - LPhotoPaintView.this.toolsTransformProgress)), LPhotoPaintView.this.colorSwatchOutlinePaint);
                    }
                }
                canvas.restore();
            }

            @Override
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                FrameLayout frameLayout5 = LPhotoPaintView.this.overlayLayout;
                if (frameLayout5 != null) {
                    frameLayout5.invalidate();
                }
            }
        };
        this.bottomLayout = frameLayout4;
        frameLayout4.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), 0);
        this.bottomLayout.setBackground(new GradientDrawable(orientation, new int[]{0, Integer.MIN_VALUE}));
        addView(this.bottomLayout, LayoutHelper.createFrame(-1, 104, 80));
        PaintToolsView paintToolsView = new PaintToolsView(context, bitmap2 != null);
        this.paintToolsView = paintToolsView;
        paintToolsView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        this.paintToolsView.setDelegate(this);
        this.paintToolsView.setSelectedIndex(1);
        this.bottomLayout.addView(this.paintToolsView, LayoutHelper.createFrame(-1, 48.0f));
        PaintTextOptionsView paintTextOptionsView = new PaintTextOptionsView(context);
        this.textOptionsView = paintTextOptionsView;
        paintTextOptionsView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
        this.textOptionsView.setVisibility(8);
        this.textOptionsView.setDelegate(this);
        this.textOptionsView.setAlignment(PersistColorPalette.getInstance(i).getCurrentAlignment());
        this.bottomLayout.addView(this.textOptionsView, LayoutHelper.createFrame(-1, 48.0f));
        FrameLayout frameLayout5 = new FrameLayout(context) {
            {
                setWillNotDraw(false);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                LPhotoPaintView.this.typefaceMenuOutlinePaint.setAlpha((int) (LPhotoPaintView.this.textOptionsView.getAlpha() * 102.0f * (1.0f - LPhotoPaintView.this.typefaceMenuTransformProgress)));
                PaintTextOptionsView paintTextOptionsView2 = LPhotoPaintView.this.textOptionsView;
                RectF rectF = AndroidUtilities.rectTmp;
                paintTextOptionsView2.getTypefaceCellBounds(rectF);
                float top = LPhotoPaintView.this.bottomLayout.getTop() + LPhotoPaintView.this.textOptionsView.getTop() + LPhotoPaintView.this.bottomLayout.getTranslationY() + LPhotoPaintView.this.textOptionsView.getTranslationY();
                rectF.set(AndroidUtilities.lerp(rectF.left, LPhotoPaintView.this.typefaceListView.getLeft(), LPhotoPaintView.this.typefaceMenuTransformProgress), AndroidUtilities.lerp(rectF.top + top, LPhotoPaintView.this.typefaceListView.getTop() - LPhotoPaintView.this.typefaceListView.getTranslationY(), LPhotoPaintView.this.typefaceMenuTransformProgress), AndroidUtilities.lerp(rectF.right, LPhotoPaintView.this.typefaceListView.getRight(), LPhotoPaintView.this.typefaceMenuTransformProgress), AndroidUtilities.lerp(top + rectF.bottom, LPhotoPaintView.this.typefaceListView.getBottom() - LPhotoPaintView.this.typefaceListView.getTranslationY(), LPhotoPaintView.this.typefaceMenuTransformProgress));
                float dp = AndroidUtilities.dp(AndroidUtilities.lerp(32, 16, LPhotoPaintView.this.typefaceMenuTransformProgress));
                int alpha = LPhotoPaintView.this.typefaceMenuBackgroundPaint.getAlpha();
                LPhotoPaintView.this.typefaceMenuBackgroundPaint.setAlpha((int) (alpha * LPhotoPaintView.this.typefaceMenuTransformProgress));
                canvas.drawRoundRect(rectF, dp, dp, LPhotoPaintView.this.typefaceMenuBackgroundPaint);
                LPhotoPaintView.this.typefaceMenuBackgroundPaint.setAlpha(alpha);
                canvas.drawRoundRect(rectF, dp, dp, LPhotoPaintView.this.typefaceMenuOutlinePaint);
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() != 0 || !LPhotoPaintView.this.isTypefaceMenuShown) {
                    return super.onTouchEvent(motionEvent);
                }
                LPhotoPaintView.this.showTypefaceMenu(false);
                return true;
            }
        };
        this.overlayLayout = frameLayout5;
        addView(frameLayout5, LayoutHelper.createFrame(-1, -1.0f));
        PaintTypefaceListView paintTypefaceListView = new PaintTypefaceListView(context);
        this.typefaceListView = paintTypefaceListView;
        paintTypefaceListView.setVisibility(8);
        this.typefaceListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
            @Override
            public final void onItemClick(View view2, int i6) {
                LPhotoPaintView.this.lambda$new$7(view2, i6);
            }
        });
        this.textOptionsView.setTypefaceListView(this.typefaceListView);
        this.overlayLayout.addView(this.typefaceListView, LayoutHelper.createFrame(-2, -2.0f, 85, 0.0f, 0.0f, 8.0f, 8.0f));
        Paint paint2 = this.typefaceMenuOutlinePaint;
        Paint.Style style = Paint.Style.STROKE;
        paint2.setStyle(style);
        this.typefaceMenuOutlinePaint.setColor(1728053247);
        this.typefaceMenuOutlinePaint.setStrokeWidth(Math.max(2, AndroidUtilities.dp(1.0f)));
        this.typefaceMenuBackgroundPaint.setColor(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
        PaintColorsListView paintColorsListView = new PaintColorsListView(context) {
            private Path path = new Path();

            @Override
            public void draw(Canvas canvas) {
                ViewGroup barView = LPhotoPaintView.this.getBarView();
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(AndroidUtilities.lerp(barView.getLeft() - getLeft(), 0, LPhotoPaintView.this.toolsTransformProgress), AndroidUtilities.lerp(barView.getTop() - getTop(), 0, LPhotoPaintView.this.toolsTransformProgress), AndroidUtilities.lerp(barView.getRight() - getLeft(), getWidth(), LPhotoPaintView.this.toolsTransformProgress), AndroidUtilities.lerp(barView.getBottom() - getTop(), getHeight(), LPhotoPaintView.this.toolsTransformProgress));
                this.path.rewind();
                this.path.addRoundRect(rectF, AndroidUtilities.dp(32.0f), AndroidUtilities.dp(32.0f), Path.Direction.CW);
                canvas.save();
                canvas.clipPath(this.path);
                super.draw(canvas);
                canvas.restore();
            }
        };
        this.colorsListView = paintColorsListView;
        paintColorsListView.setVisibility(8);
        this.colorsListView.setColorPalette(PersistColorPalette.getInstance(i));
        this.colorsListView.setColorListener(new Consumer() {
            @Override
            public final void accept(Object obj) {
                LPhotoPaintView.this.lambda$new$8((Integer) obj);
            }
        });
        this.bottomLayout.addView(this.colorsListView, LayoutHelper.createFrame(-1, 84.0f, 48, 56.0f, 0.0f, 56.0f, 6.0f));
        setupTabsLayout(context);
        PaintCancelView paintCancelView = new PaintCancelView(context);
        this.cancelButton = paintCancelView;
        paintCancelView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        this.cancelButton.setBackground(Theme.createSelectorDrawable(1090519039));
        this.bottomLayout.addView(this.cancelButton, LayoutHelper.createFrame(32, 32.0f, 83, 12.0f, 0.0f, 0.0f, 4.0f));
        PaintDoneView paintDoneView = new PaintDoneView(context);
        this.doneButton = paintDoneView;
        paintDoneView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
        this.doneButton.setBackground(Theme.createSelectorDrawable(1090519039));
        this.doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                LPhotoPaintView.this.lambda$new$10(context, bitmap2, persistColorPalette, view2);
            }
        });
        this.bottomLayout.addView(this.doneButton, LayoutHelper.createFrame(32, 32.0f, 85, 0.0f, 0.0f, 12.0f, 4.0f));
        PaintWeightChooserView paintWeightChooserView = new PaintWeightChooserView(context);
        this.weightChooserView = paintWeightChooserView;
        paintWeightChooserView.setColorSwatch(this.colorSwatch);
        this.weightChooserView.setRenderView(this.renderView);
        this.weightChooserView.setValueOverride(this.weightDefaultValueOverride);
        this.colorSwatch.brushWeight = this.weightDefaultValueOverride.get();
        this.weightChooserView.setOnUpdate(new Runnable() {
            @Override
            public final void run() {
                LPhotoPaintView.this.lambda$new$11(i);
            }
        });
        addView(this.weightChooserView, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout6 = new FrameLayout(context);
        this.pipetteContainerLayout = frameLayout6;
        addView(frameLayout6, LayoutHelper.createFrame(-1, -1.0f));
        this.colorSwatchOutlinePaint.setStyle(style);
        this.colorSwatchOutlinePaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        setCurrentSwatch(this.colorSwatch, true);
        onBrushSelected((Brush) Brush.BRUSHES_LIST.get(0));
        updateColors();
        if (Build.VERSION.SDK_INT >= 29) {
            int dp = AndroidUtilities.dp(100.0f);
            double d3 = AndroidUtilities.displaySize.y;
            Double.isNaN(d3);
            setSystemGestureExclusionRects(Arrays.asList(new Rect(0, (int) (AndroidUtilities.displaySize.y * 0.35f), dp, (int) (d3 * 0.65d))));
        }
    }

    private Size basePhotoSize(String str) {
        float f;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(str, options);
            f = options.outWidth / options.outHeight;
        } catch (Exception e) {
            FileLog.e(e);
            f = 1.0f;
        }
        if (f > 1.0f) {
            double measuredWidth = this.entitiesView.getMeasuredWidth();
            Double.isNaN(measuredWidth);
            float floor = (float) Math.floor(measuredWidth * 0.5d);
            return new Size(floor, floor / f);
        }
        double measuredHeight = this.entitiesView.getMeasuredHeight();
        Double.isNaN(measuredHeight);
        float floor2 = (float) Math.floor(measuredHeight * 0.5d);
        return new Size(f * floor2, floor2);
    }

    private Size baseStickerSize() {
        double d = getPaintingSize().width;
        Double.isNaN(d);
        float floor = (float) Math.floor(d * 0.5d);
        return new Size(floor, floor);
    }

    private void bottomPanelTranslationY(float f, float f2) {
    }

    private PopupButton buttonForPopup(String str, int i, boolean z, final Runnable runnable) {
        PopupButton popupButton = new PopupButton(getContext());
        popupButton.setIcon(i);
        popupButton.setText(str);
        popupButton.setSelected(z);
        if (runnable != null) {
            popupButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    runnable.run();
                }
            });
        }
        return popupButton;
    }

    private StickerPosition calculateStickerPosition(TLRPC.Document document) {
        TLRPC.TL_maskCoords tL_maskCoords;
        float f;
        ArrayList arrayList;
        int i;
        PhotoFace randomFaceWithVacantAnchor;
        int i2 = 0;
        while (true) {
            if (i2 >= document.attributes.size()) {
                tL_maskCoords = null;
                break;
            }
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i2);
            if (documentAttribute instanceof TLRPC.TL_documentAttributeSticker) {
                tL_maskCoords = documentAttribute.mask_coords;
                break;
            }
            i2++;
        }
        MediaController.CropState cropState = this.currentCropState;
        float f2 = 0.75f;
        if (cropState != null) {
            f = -(cropState.transformRotation + cropState.cropRotate);
            f2 = 0.75f / cropState.cropScale;
        } else {
            f = 0.0f;
        }
        StickerPosition stickerPosition = new StickerPosition(centerPositionForEntity(), f2, f);
        if (tL_maskCoords == null || (arrayList = this.faces) == null || arrayList.size() == 0 || (randomFaceWithVacantAnchor = getRandomFaceWithVacantAnchor((i = tL_maskCoords.n), document.id, tL_maskCoords)) == null) {
            return stickerPosition;
        }
        Point pointForAnchor = randomFaceWithVacantAnchor.getPointForAnchor(i);
        float widthForAnchor = randomFaceWithVacantAnchor.getWidthForAnchor(i);
        float angle = randomFaceWithVacantAnchor.getAngle();
        double d = widthForAnchor / baseStickerSize().width;
        double d2 = tL_maskCoords.zoom;
        Double.isNaN(d);
        double radians = (float) Math.toRadians(angle);
        Double.isNaN(radians);
        double d3 = 1.5707963267948966d - radians;
        double sin = Math.sin(d3);
        double d4 = widthForAnchor;
        Double.isNaN(d4);
        float f3 = (float) (sin * d4 * tL_maskCoords.x);
        double cos = Math.cos(d3);
        Double.isNaN(d4);
        float f4 = (float) (cos * d4 * tL_maskCoords.x);
        Double.isNaN(radians);
        double d5 = radians + 1.5707963267948966d;
        double cos2 = Math.cos(d5);
        Double.isNaN(d4);
        float f5 = (float) (cos2 * d4 * tL_maskCoords.y);
        double sin2 = Math.sin(d5);
        Double.isNaN(d4);
        return new StickerPosition(new Point(pointForAnchor.x + f3 + f5, pointForAnchor.y + f4 + ((float) (sin2 * d4 * tL_maskCoords.y))), (float) (d * d2), angle);
    }

    private Point centerPositionForEntity() {
        Size paintingSize = getPaintingSize();
        float f = paintingSize.width / 2.0f;
        float f2 = paintingSize.height / 2.0f;
        if (this.currentCropState != null) {
            float radians = (float) Math.toRadians(-(r2.transformRotation + r2.cropRotate));
            double d = this.currentCropState.cropPx;
            double d2 = radians;
            double cos = Math.cos(d2);
            Double.isNaN(d);
            double d3 = d * cos;
            double d4 = this.currentCropState.cropPy;
            double sin = Math.sin(d2);
            Double.isNaN(d4);
            float f3 = (float) (d3 - (d4 * sin));
            double d5 = this.currentCropState.cropPx;
            double sin2 = Math.sin(d2);
            Double.isNaN(d5);
            double d6 = d5 * sin2;
            double d7 = this.currentCropState.cropPy;
            double cos2 = Math.cos(d2);
            Double.isNaN(d7);
            f -= f3 * paintingSize.width;
            f2 -= ((float) (d6 + (d7 * cos2))) * paintingSize.height;
        }
        return new Point(f, f2);
    }

    private StickerView createSticker(Object obj, TLRPC.Document document, boolean z) {
        StickerPosition calculateStickerPosition = calculateStickerPosition(document);
        StickerView stickerView = new StickerView(getContext(), calculateStickerPosition.position, calculateStickerPosition.angle, calculateStickerPosition.scale, baseStickerSize(), document, obj) {
            @Override
            protected void didSetAnimatedSticker(RLottieDrawable rLottieDrawable) {
                LPhotoPaintView.this.didSetAnimatedSticker(rLottieDrawable);
            }
        };
        if (MessageObject.isTextColorEmoji(document)) {
            stickerView.centerImage.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
        }
        stickerView.centerImage.setLayerNum(12);
        if (calculateStickerPosition.position.x == this.entitiesView.getMeasuredWidth() / 2.0f) {
            stickerView.setStickyX(2);
        }
        if (calculateStickerPosition.position.y == this.entitiesView.getMeasuredHeight() / 2.0f) {
            stickerView.setStickyY(2);
        }
        stickerView.setDelegate(this);
        this.entitiesView.addView(stickerView);
        if (z) {
            registerRemovalUndo(stickerView);
            selectEntity(stickerView);
        }
        return stickerView;
    }

    private TextPaintView createText(boolean z) {
        onTextAdd();
        Size paintingSize = getPaintingSize();
        Point startPositionRelativeToEntity = startPositionRelativeToEntity(null);
        TextPaintView textPaintView = new TextPaintView(getContext(), startPositionRelativeToEntity, (int) (paintingSize.width / 9.0f), "", this.colorSwatch, this.selectedTextType);
        float f = paintingSize.width / 9.0f;
        textPaintView.setMinMaxFontSize((int) (0.5f * f), (int) (f * 2.0f), new Runnable() {
            @Override
            public final void run() {
                LPhotoPaintView.this.lambda$createText$13();
            }
        });
        if (startPositionRelativeToEntity.x == this.entitiesView.getMeasuredWidth() / 2.0f) {
            textPaintView.setStickyX(2);
        }
        if (startPositionRelativeToEntity.y == this.entitiesView.getMeasuredHeight() / 2.0f) {
            textPaintView.setStickyY(2);
        }
        textPaintView.setDelegate(this);
        textPaintView.setMaxWidth((int) (paintingSize.width - 20.0f));
        textPaintView.setTypeface(PersistColorPalette.getInstance(this.currentAccount).getCurrentTypeface());
        textPaintView.setType(PersistColorPalette.getInstance(this.currentAccount).getCurrentTextType());
        this.entitiesView.addView(textPaintView, LayoutHelper.createFrame(-2, -2.0f));
        MediaController.CropState cropState = this.currentCropState;
        if (cropState != null) {
            textPaintView.scale(1.0f / cropState.cropScale);
            textPaintView.rotate(-(r0.transformRotation + this.currentCropState.cropRotate));
        }
        if (z) {
            registerRemovalUndo(textPaintView);
            textPaintView.beginEditing();
            selectEntity(textPaintView, false);
            textPaintView.getFocusedView().requestFocus();
            AndroidUtilities.showKeyboard(textPaintView.getFocusedView());
            this.editingText = true;
            this.textOptionsView.setAlignment(PersistColorPalette.getInstance(this.currentAccount).getCurrentAlignment(), true);
            this.textOptionsView.setOutlineType(PersistColorPalette.getInstance(this.currentAccount).getCurrentTextType());
        }
        return textPaintView;
    }

    private void detectFaces() {
        this.queue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                LPhotoPaintView.this.lambda$detectFaces$24();
            }
        }, 200L);
    }

    private void duplicateSelectedEntity() {
        EntityView entityView;
        EntityView entityView2 = this.currentEntityView;
        if (entityView2 == null) {
            return;
        }
        Point startPositionRelativeToEntity = startPositionRelativeToEntity(entityView2);
        EntityView entityView3 = this.currentEntityView;
        if (entityView3 instanceof StickerView) {
            StickerView stickerView = new StickerView(getContext(), (StickerView) this.currentEntityView, startPositionRelativeToEntity);
            stickerView.setDelegate(this);
            this.entitiesView.addView(stickerView);
            entityView = stickerView;
        } else if (entityView3 instanceof TextPaintView) {
            TextPaintView textPaintView = new TextPaintView(getContext(), (TextPaintView) this.currentEntityView, startPositionRelativeToEntity);
            textPaintView.setDelegate(this);
            textPaintView.setMaxWidth((int) (getPaintingSize().width - 20.0f));
            this.entitiesView.addView(textPaintView, LayoutHelper.createFrame(-2, -2.0f));
            entityView = textPaintView;
        } else {
            entityView = null;
        }
        registerRemovalUndo(entityView);
        selectEntity(entityView);
    }

    private void editSelectedTextEntity() {
        EntityView entityView = this.currentEntityView;
        if (!(entityView instanceof TextPaintView) || this.editingText) {
            return;
        }
        TextPaintView textPaintView = (TextPaintView) entityView;
        this.editingText = true;
        textPaintView.beginEditing();
        View focusedView = textPaintView.getFocusedView();
        focusedView.requestFocus();
        AndroidUtilities.showKeyboard(focusedView);
    }

    public View getBarView(int i) {
        if (i == 0) {
            return this.paintToolsView;
        }
        if (i == 2) {
            return this.textOptionsView;
        }
        return null;
    }

    public ViewGroup getBarView() {
        return this.tabsSelectedIndex == 2 ? this.textOptionsView : this.paintToolsView;
    }

    private int[] getCenterLocationInWindow(View view) {
        this.position[0] = view.getWidth() / 2.0f;
        this.position[1] = view.getHeight() / 2.0f;
        view.getMatrix().mapPoints(this.position);
        float[] fArr = this.position;
        fArr[0] = fArr[0] + view.getLeft();
        float[] fArr2 = this.position;
        fArr2[1] = fArr2[1] + view.getTop();
        while (true) {
            Object parent = view.getParent();
            if (!(parent instanceof View)) {
                this.pos2[0] = Math.round(this.position[0]);
                this.pos2[1] = Math.round(this.position[1]);
                float f = this.pos2[0];
                android.graphics.Point point = AndroidUtilities.displaySize;
                float f2 = f - (point.x / 2.0f);
                float f3 = r14[1] - (point.y / 2.0f);
                float radians = (float) Math.toRadians(-this.entitiesView.getRotation());
                int[] iArr = this.pos2;
                double d = f2;
                double d2 = radians;
                double cos = Math.cos(d2);
                Double.isNaN(d);
                double d3 = f3;
                double sin = Math.sin(d2);
                Double.isNaN(d3);
                iArr[0] = ((int) ((cos * d) - (sin * d3))) + (AndroidUtilities.displaySize.x / 2);
                int[] iArr2 = this.pos2;
                double sin2 = Math.sin(d2);
                Double.isNaN(d);
                double cos2 = Math.cos(d2);
                Double.isNaN(d3);
                iArr2[1] = ((int) ((d * sin2) + (d3 * cos2))) + (AndroidUtilities.displaySize.y / 2);
                return this.pos2;
            }
            view = (View) parent;
            float[] fArr3 = this.position;
            fArr3[0] = fArr3[0] - view.getScrollX();
            float[] fArr4 = this.position;
            fArr4[1] = fArr4[1] - view.getScrollY();
            view.getMatrix().mapPoints(this.position);
            float[] fArr5 = this.position;
            fArr5[0] = fArr5[0] + view.getLeft();
            float[] fArr6 = this.position;
            fArr6[1] = fArr6[1] + view.getTop();
        }
    }

    private int getFrameRotation() {
        int i = this.originalBitmapRotation;
        if (i == 90) {
            return 1;
        }
        if (i != 180) {
            return i != 270 ? 0 : 3;
        }
        return 2;
    }

    private Size getPaintingSize() {
        Size size = this.paintingSize;
        if (size != null) {
            return size;
        }
        float width = this.bitmapToEdit.getWidth();
        float height = this.bitmapToEdit.getHeight();
        int devicePerformanceClass = SharedConfig.getDevicePerformanceClass();
        int i = devicePerformanceClass != 0 ? devicePerformanceClass != 2 ? 2560 : 3840 : 1280;
        Size size2 = new Size(width, height);
        float f = i;
        size2.width = f;
        float floor = (float) Math.floor((f * height) / width);
        size2.height = floor;
        if (floor > f) {
            size2.height = f;
            size2.width = (float) Math.floor((f * width) / height);
        }
        this.paintingSize = size2;
        return size2;
    }

    private PhotoFace getRandomFaceWithVacantAnchor(int i, long j, TLRPC.TL_maskCoords tL_maskCoords) {
        if (i >= 0 && i <= 3 && !this.faces.isEmpty()) {
            int size = this.faces.size();
            int nextInt = Utilities.random.nextInt(size);
            for (int i2 = size; i2 > 0; i2--) {
                PhotoFace photoFace = (PhotoFace) this.faces.get(nextInt);
                if (!isFaceAnchorOccupied(photoFace, i, j, tL_maskCoords)) {
                    return photoFace;
                }
                nextInt = (nextInt + 1) % size;
            }
        }
        return null;
    }

    public int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    private void hideEmojiPopup(boolean z) {
        if (this.emojiViewVisible) {
            showEmojiPopup(0);
        }
        if (z) {
            EmojiView emojiView = this.emojiView;
            if (emojiView == null || emojiView.getVisibility() != 0 || this.waitingForKeyboardOpen) {
                hideEmojiView();
                return;
            }
            ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, this.emojiView.getMeasuredHeight());
            this.bottomPanelIgnoreOnce = false;
            ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LPhotoPaintView.this.lambda$hideEmojiPopup$47(valueAnimator);
                }
            });
            this.isAnimatePopupClosing = true;
            ofFloat.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    LPhotoPaintView lPhotoPaintView = LPhotoPaintView.this;
                    lPhotoPaintView.isAnimatePopupClosing = false;
                    lPhotoPaintView.emojiView.setTranslationY(0.0f);
                    LPhotoPaintView.this.hideEmojiView();
                }
            });
            ofFloat.setDuration(250L);
            ofFloat.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
            ofFloat.start();
        }
    }

    public void hideEmojiView() {
        EmojiView emojiView;
        if (this.emojiPadding > 0) {
            updateKeyboard();
        }
        if (!this.emojiViewVisible && (emojiView = this.emojiView) != null && emojiView.getVisibility() != 8) {
            this.emojiView.setVisibility(8);
        }
        this.emojiPadding = 0;
    }

    private boolean isFaceAnchorOccupied(PhotoFace photoFace, int i, long j, TLRPC.TL_maskCoords tL_maskCoords) {
        if (photoFace.getPointForAnchor(i) == null) {
            return true;
        }
        float widthForAnchor = photoFace.getWidthForAnchor(0) * 1.1f;
        for (int i2 = 0; i2 < this.entitiesView.getChildCount(); i2++) {
            View childAt = this.entitiesView.getChildAt(i2);
            if (childAt instanceof StickerView) {
                StickerView stickerView = (StickerView) childAt;
                if (stickerView.getAnchor() != i) {
                    continue;
                } else {
                    Point position = stickerView.getPosition();
                    float hypot = (float) Math.hypot(position.x - r14.x, position.y - r14.y);
                    if ((j == stickerView.getSticker().id || this.faces.size() > 1) && hypot < widthForAnchor) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isSidewardOrientation() {
        int i = this.originalBitmapRotation % 360;
        return i == 90 || i == 270;
    }

    public void lambda$appearAnimation$23(View view) {
        if (view instanceof EntityView) {
            EntityView entityView = (EntityView) view;
            entityView.updateSelectionView();
            selectEntity(entityView);
        }
    }

    public void lambda$createText$13() {
        PaintWeightChooserView paintWeightChooserView = this.weightChooserView;
        if (paintWeightChooserView != null) {
            paintWeightChooserView.invalidate();
        }
    }

    public void lambda$detectFaces$24() {
        int i;
        FaceDetector faceDetector = null;
        try {
            try {
                faceDetector = new FaceDetector.Builder(getContext()).setMode(1).setLandmarkType(1).setTrackingEnabled(false).build();
            } catch (Exception e) {
                FileLog.e(e);
                if (0 == 0) {
                    return;
                }
            }
            if (!faceDetector.isOperational()) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.e("face detection is not operational");
                }
                faceDetector.release();
                return;
            }
            try {
                SparseArray detect = faceDetector.detect(new Frame.Builder().setBitmap(this.facesBitmap).setRotation(getFrameRotation()).build());
                ArrayList arrayList = new ArrayList();
                Size paintingSize = getPaintingSize();
                for (i = 0; i < detect.size(); i++) {
                    PhotoFace photoFace = new PhotoFace((Face) detect.get(detect.keyAt(i)), this.facesBitmap, paintingSize, isSidewardOrientation());
                    if (photoFace.isSufficient()) {
                        arrayList.add(photoFace);
                    }
                }
                this.faces = arrayList;
                faceDetector.release();
            } catch (Throwable th) {
                FileLog.e(th);
                faceDetector.release();
            }
        } catch (Throwable th2) {
            if (0 != 0) {
                faceDetector.release();
            }
            throw th2;
        }
    }

    public void lambda$getThanosEffect$48() {
        ThanosEffect thanosEffect = this.thanosEffect;
        if (thanosEffect != null) {
            this.thanosEffect = null;
            removeView(thanosEffect);
        }
    }

    public void lambda$hideEmojiPopup$47(ValueAnimator valueAnimator) {
        this.emojiView.setTranslationY(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public static int lambda$new$0(Theme.ResourcesProvider resourcesProvider, int i) {
        if (i == Theme.key_actionBarDefaultSubmenuBackground) {
            return -14145495;
        }
        if (i == Theme.key_actionBarDefaultSubmenuItem) {
            return -1;
        }
        if (i == Theme.key_dialogBackground) {
            return -14737633;
        }
        if (i == Theme.key_dialogTextBlack) {
            return -592138;
        }
        if (i == Theme.key_dialogTextGray3) {
            return -8553091;
        }
        if (i == Theme.key_chat_emojiPanelBackground) {
            return -16777216;
        }
        if (i == Theme.key_chat_emojiPanelShadowLine) {
            return -1610612736;
        }
        if (i == Theme.key_chat_emojiBottomPanelIcon || i == Theme.key_chat_emojiPanelBackspace || i == Theme.key_chat_emojiPanelIcon) {
            return -9539985;
        }
        if (i == Theme.key_windowBackgroundWhiteBlackText) {
            return -1;
        }
        int i2 = Theme.key_featuredStickers_addedIcon;
        if (i == i2) {
            return -11754001;
        }
        if (i == Theme.key_listSelector) {
            return 536870911;
        }
        if (i == Theme.key_profile_tabSelectedText || i == Theme.key_profile_tabText || i == Theme.key_profile_tabSelectedLine) {
            return -1;
        }
        if (i == Theme.key_profile_tabSelector) {
            return 352321535;
        }
        if (i == Theme.key_chat_emojiSearchIcon || i == i2) {
            return -7895161;
        }
        if (i == Theme.key_chat_emojiSearchBackground) {
            return 780633991;
        }
        if (i == Theme.key_windowBackgroundGray) {
            return -15921907;
        }
        if (i == Theme.key_fastScrollInactive) {
            return -12500671;
        }
        if (i == Theme.key_fastScrollActive) {
            return -13133079;
        }
        if (i == Theme.key_fastScrollText) {
            return -1;
        }
        if (i == Theme.key_windowBackgroundWhite) {
            return -15198183;
        }
        if (i == Theme.key_divider) {
            return -16777216;
        }
        return resourcesProvider != null ? resourcesProvider.getColor(i) : Theme.getColor(i);
    }

    public void lambda$new$1() {
        boolean canUndo = this.undoStore.canUndo();
        this.undoButton.animate().cancel();
        this.undoButton.animate().alpha(canUndo ? 1.0f : 0.6f).translationY(0.0f).setDuration(150L).start();
        this.undoButton.setClickable(canUndo);
        this.undoAllButton.animate().cancel();
        this.undoAllButton.animate().alpha(canUndo ? 1.0f : 0.6f).translationY(0.0f).setDuration(150L).start();
        this.undoAllButton.setClickable(canUndo);
    }

    public void lambda$new$10(Context context, final Bitmap bitmap, final PersistColorPalette persistColorPalette, View view) {
        if (this.isColorListShown) {
            new ColorPickerBottomSheet(context, this.resourcesProvider).setColor(this.colorSwatch.color).setPipetteDelegate(new ColorPickerBottomSheet.PipetteDelegate() {
                private boolean hasPipette;

                @Override
                public ViewGroup getContainerView() {
                    return LPhotoPaintView.this.pipetteContainerLayout;
                }

                @Override
                public View getSnapshotDrawingView() {
                    return LPhotoPaintView.this;
                }

                @Override
                public boolean isPipetteAvailable() {
                    return bitmap != null;
                }

                @Override
                public boolean isPipetteVisible() {
                    return this.hasPipette;
                }

                @Override
                public void onColorSelected(int i) {
                    LPhotoPaintView.this.showColorList(false);
                    persistColorPalette.selectColor(i);
                    persistColorPalette.saveColors();
                    LPhotoPaintView.this.setNewColor(i);
                    LPhotoPaintView.this.colorsListView.setSelectedColorIndex(persistColorPalette.getCurrentColorPosition());
                    LPhotoPaintView.this.colorsListView.getAdapter().notifyDataSetChanged();
                }

                @Override
                public void onDrawImageOverCanvas(Bitmap bitmap2, Canvas canvas) {
                    Matrix matrix = LPhotoPaintView.this.renderView.getMatrix();
                    canvas.save();
                    canvas.translate(LPhotoPaintView.this.renderView.getX(), LPhotoPaintView.this.renderView.getY());
                    canvas.concat(matrix);
                    canvas.scale(LPhotoPaintView.this.renderView.getWidth() / bitmap.getWidth(), LPhotoPaintView.this.renderView.getHeight() / bitmap.getHeight(), 0.0f, 0.0f);
                    canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
                    canvas.restore();
                }

                @Override
                public void onStartColorPipette() {
                    this.hasPipette = true;
                }

                @Override
                public void onStopColorPipette() {
                    this.hasPipette = false;
                }
            }).setColorListener(new Consumer() {
                @Override
                public final void accept(Object obj) {
                    LPhotoPaintView.this.lambda$new$9(persistColorPalette, (Integer) obj);
                }
            }).show();
            return;
        }
        Runnable runnable = this.onDoneButtonClickedListener;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void lambda$new$11(int i) {
        setCurrentSwatch(this.colorSwatch, true);
        PersistColorPalette.getInstance(i).setCurrentWeight(this.colorSwatch.brushWeight);
    }

    public void lambda$new$2(View view) {
        RenderView renderView = this.renderView;
        if (renderView == null || !(renderView.getCurrentBrush() instanceof Brush.Shape)) {
            this.undoStore.undo();
            return;
        }
        this.renderView.clearShape();
        this.paintToolsView.setSelectedIndex(1);
        onBrushSelected((Brush) Brush.BRUSHES_LIST.get(0));
    }

    public static void lambda$new$3(View view) {
        PhotoViewer.getInstance().zoomOut();
    }

    public void lambda$new$4(View view) {
        if (this.undoStore.canUndo()) {
            RenderView renderView = this.renderView;
            if (renderView != null && (renderView.getCurrentBrush() instanceof Brush.Shape)) {
                this.renderView.clearShape();
                this.paintToolsView.setSelectedIndex(1);
                onBrushSelected((Brush) Brush.BRUSHES_LIST.get(0));
            }
            this.renderView.clearAll();
            this.undoStore.reset();
            this.entitiesView.removeAllViews();
        }
    }

    public void lambda$new$5(View view) {
        EntityView entityView = this.currentEntityView;
        if (entityView instanceof TextPaintView) {
            AndroidUtilities.hideKeyboard(((TextPaintView) entityView).getFocusedView());
        }
        if (this.emojiViewVisible) {
            hideEmojiPopup(false);
        }
        lambda$registerRemovalUndo$45(this.currentEntityView);
        selectEntity(null);
    }

    public void lambda$new$6(View view) {
        selectEntity(null);
    }

    public void lambda$new$7(View view, int i) {
        PaintTypeface paintTypeface = (PaintTypeface) PaintTypeface.get().get(i);
        this.textOptionsView.setTypeface(paintTypeface.getKey());
        onTypefaceSelected(paintTypeface);
        showTypefaceMenu(false);
    }

    public void lambda$new$8(Integer num) {
        setNewColor(num.intValue());
        showColorList(false);
    }

    public void lambda$new$9(PersistColorPalette persistColorPalette, Integer num) {
        persistColorPalette.selectColor(num.intValue());
        persistColorPalette.saveColors();
        setNewColor(num.intValue());
        this.colorsListView.setSelectedColorIndex(persistColorPalette.getCurrentColorPosition());
        this.colorsListView.getAdapter().notifyDataSetChanged();
    }

    public void lambda$onAddButtonPressed$33(Brush.Shape shape, int i) {
        if (this.renderView.getCurrentBrush() instanceof Brush.Shape) {
            this.ignoreToolChangeAnimationOnce = true;
        }
        onBrushSelected(shape);
        this.paintToolsView.animatePlusToIcon(i);
    }

    public boolean lambda$onAddButtonPressed$34(View view) {
        if (this.popupLayout != null) {
            PersistColorPalette.getInstance(this.currentAccount).toggleFillShapes();
            boolean fillShapes = PersistColorPalette.getInstance(this.currentAccount).getFillShapes();
            for (int i = 0; i < this.popupLayout.getItemsCount(); i++) {
                View itemAt = this.popupLayout.getItemAt(i);
                if (itemAt instanceof PopupButton) {
                    Brush.Shape shape = (Brush.Shape) Brush.Shape.SHAPES_LIST.get(i);
                    ((PopupButton) itemAt).setIcon(fillShapes ? shape.getFilledIconRes() : shape.getIconRes(), fillShapes, true);
                }
            }
        }
        return true;
    }

    public void lambda$onAddButtonPressed$35() {
        boolean fillShapes = PersistColorPalette.getInstance(this.currentAccount).getFillShapes();
        for (int i = 0; i < Brush.Shape.SHAPES_LIST.size(); i++) {
            final Brush.Shape shape = (Brush.Shape) Brush.Shape.SHAPES_LIST.get(i);
            final int filledIconRes = fillShapes ? shape.getFilledIconRes() : shape.getIconRes();
            PopupButton buttonForPopup = buttonForPopup(shape.getShapeName(), filledIconRes, false, new Runnable() {
                @Override
                public final void run() {
                    LPhotoPaintView.this.lambda$onAddButtonPressed$33(shape, filledIconRes);
                }
            });
            buttonForPopup.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public final boolean onLongClick(View view) {
                    boolean lambda$onAddButtonPressed$34;
                    lambda$onAddButtonPressed$34 = LPhotoPaintView.this.lambda$onAddButtonPressed$34(view);
                    return lambda$onAddButtonPressed$34;
                }
            });
            this.popupLayout.addView((View) buttonForPopup, LayoutHelper.createLinear(-1, 48));
        }
    }

    public static void lambda$onSwitchSegmentedAnimation$50() {
    }

    public void lambda$openStickersView$18() {
        if (this.facesBitmap != null) {
            detectFaces();
        }
    }

    public Boolean lambda$openStickersView$19(Object obj, TLRPC.Document document, Boolean bool) {
        StickerView createSticker = createSticker(obj, document, true);
        if (bool.booleanValue()) {
            createSticker.setScale(1.5f);
        }
        return Boolean.TRUE;
    }

    public Boolean lambda$openStickersView$20(Integer num) {
        if (num.intValue() == 2) {
            showPhotoAlert();
        }
        return Boolean.TRUE;
    }

    public void lambda$openStickersView$21(int i, DialogInterface dialogInterface) {
        onOpenCloseStickersAlert(false);
        switchTab(i);
    }

    public void lambda$setCurrentSwatch$31(Integer num, int i, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.colorSwatch.color = ColorUtils.blendARGB(num.intValue(), i, floatValue);
        FrameLayout frameLayout = this.bottomLayout;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
    }

    public void lambda$setNewColor$12(int i, int i2, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.colorSwatch.color = ColorUtils.blendARGB(i, i2, floatValue);
        this.bottomLayout.invalidate();
    }

    public void lambda$setupTabsLayout$14(View view) {
        if (this.editingText) {
            selectEntity(null);
        } else {
            switchTab(0);
        }
    }

    public void lambda$setupTabsLayout$15(View view) {
        openStickersView();
    }

    public void lambda$setupTabsLayout$16(View view) {
        switchTab(2);
        if (this.currentEntityView instanceof TextPaintView) {
            return;
        }
        createText(true);
    }

    public void lambda$showColorList$29(View view, boolean z, boolean[] zArr, float f, DynamicAnimation dynamicAnimation, float f2, float f3) {
        float f4 = f2 / 1000.0f;
        this.toolsTransformProgress = f4;
        float f5 = ((1.0f - f4) * 0.4f) + 0.6f;
        view.setScaleX(f5);
        view.setScaleY(f5);
        view.setTranslationY((AndroidUtilities.dp(16.0f) * Math.min(this.toolsTransformProgress, 0.25f)) / 0.25f);
        view.setAlpha(1.0f - (Math.min(this.toolsTransformProgress, 0.25f) / 0.25f));
        this.colorsListView.setProgress(this.toolsTransformProgress, z);
        this.doneButton.setProgress(this.toolsTransformProgress);
        this.cancelButton.setProgress(this.toolsTransformProgress);
        this.tabsLayout.setTranslationY(AndroidUtilities.dp(32.0f) * this.toolsTransformProgress);
        if (zArr[0]) {
            float f6 = this.toolsTransformProgress;
            if (!z) {
                f6 = 1.0f - f6;
            }
            this.bottomLayout.setTranslationY(f - ((AndroidUtilities.dp(40.0f) * f6) * (z ? 1 : -1)));
        }
        this.bottomLayout.invalidate();
        if (view == this.textOptionsView) {
            this.overlayLayout.invalidate();
        }
    }

    public void lambda$showColorList$30(boolean z, DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
        if (dynamicAnimation == this.toolsTransformAnimation) {
            this.toolsTransformAnimation = null;
            if (z) {
                return;
            }
            this.colorsListView.setVisibility(8);
            PersistColorPalette.getInstance(this.currentAccount).saveColors();
            this.colorsListView.getAdapter().notifyDataSetChanged();
        }
    }

    public void lambda$showEmojiPopup$46(ValueAnimator valueAnimator) {
        this.emojiView.setTranslationY(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void lambda$showMenuForEntity$36(EntityView entityView, View view) {
        lambda$registerRemovalUndo$45(entityView);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            return;
        }
        this.popupWindow.dismiss(true);
    }

    public void lambda$showMenuForEntity$37(View view) {
        editSelectedTextEntity();
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            return;
        }
        this.popupWindow.dismiss(true);
    }

    public void lambda$showMenuForEntity$38(EntityView entityView, View view) {
        ((StickerView) entityView).mirror(true);
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            return;
        }
        this.popupWindow.dismiss(true);
    }

    public void lambda$showMenuForEntity$39(View view) {
        duplicateSelectedEntity();
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            return;
        }
        this.popupWindow.dismiss(true);
    }

    public void lambda$showMenuForEntity$40(PhotoView photoView, View view) {
        photoView.toggleSegmented(true);
        if (photoView.isSegmented()) {
            onSwitchSegmentedAnimation(photoView);
        }
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow == null || !actionBarPopupWindow.isShowing()) {
            return;
        }
        this.popupWindow.dismiss(true);
    }

    public void lambda$showMenuForEntity$41(final EntityView entityView) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(0);
        TextView textView = new TextView(getContext());
        int i = Theme.key_actionBarDefaultSubmenuItem;
        textView.setTextColor(getThemedColor(i));
        textView.setBackground(Theme.getSelectorDrawable(false));
        textView.setGravity(16);
        textView.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(14.0f), 0);
        textView.setTextSize(1, 14.0f);
        textView.setTag(0);
        textView.setText(LocaleController.getString(R.string.PaintDelete));
        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
        textView.setEllipsize(truncateAt);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                LPhotoPaintView.this.lambda$showMenuForEntity$36(entityView, view);
            }
        });
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, 48));
        if (entityView instanceof TextPaintView) {
            TextView textView2 = new TextView(getContext());
            textView2.setTextColor(getThemedColor(i));
            textView2.setBackground(Theme.getSelectorDrawable(false));
            textView2.setGravity(16);
            textView2.setPadding(AndroidUtilities.dp(16.0f), 0, AndroidUtilities.dp(16.0f), 0);
            textView2.setTextSize(1, 14.0f);
            textView2.setEllipsize(truncateAt);
            textView2.setTag(1);
            textView2.setText(LocaleController.getString(R.string.PaintEdit));
            textView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    LPhotoPaintView.this.lambda$showMenuForEntity$37(view);
                }
            });
            linearLayout.addView(textView2, LayoutHelper.createLinear(-2, 48));
        }
        if (entityView instanceof StickerView) {
            TextView textView3 = new TextView(getContext());
            textView3.setTextColor(getThemedColor(i));
            textView3.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            textView3.setGravity(16);
            textView3.setEllipsize(truncateAt);
            textView3.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(16.0f), 0);
            textView3.setTextSize(1, 14.0f);
            textView3.setTag(2);
            textView3.setText(LocaleController.getString(R.string.Flip));
            textView3.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    LPhotoPaintView.this.lambda$showMenuForEntity$38(entityView, view);
                }
            });
            linearLayout.addView(textView3, LayoutHelper.createLinear(-2, 48));
        }
        boolean z = entityView instanceof PhotoView;
        if (!z) {
            TextView textView4 = new TextView(getContext());
            textView4.setTextColor(getThemedColor(i));
            textView4.setBackgroundDrawable(Theme.getSelectorDrawable(false));
            textView4.setGravity(16);
            textView4.setEllipsize(truncateAt);
            textView4.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(16.0f), 0);
            textView4.setTextSize(1, 14.0f);
            textView4.setTag(2);
            textView4.setText(LocaleController.getString(R.string.PaintDuplicate));
            textView4.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    LPhotoPaintView.this.lambda$showMenuForEntity$39(view);
                }
            });
            linearLayout.addView(textView4, LayoutHelper.createLinear(-2, 48));
        }
        if (z) {
            final PhotoView photoView = (PhotoView) entityView;
            if (photoView.hasSegmentedImage()) {
                TextView textView5 = new TextView(getContext());
                textView5.setTextColor(getThemedColor(i));
                textView5.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                textView5.setGravity(16);
                textView5.setEllipsize(truncateAt);
                textView5.setPadding(AndroidUtilities.dp(14.0f), 0, AndroidUtilities.dp(16.0f), 0);
                textView5.setTextSize(1, 14.0f);
                textView5.setTag(5);
                textView5.setText(LocaleController.getString(photoView.isSegmented() ? R.string.SegmentationUndoCutOut : R.string.SegmentationCutOut));
                textView5.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public final void onClick(View view) {
                        LPhotoPaintView.this.lambda$showMenuForEntity$40(photoView, view);
                    }
                });
                linearLayout.addView(textView5, LayoutHelper.createLinear(-2, 44));
                photoView.highlightSegmented();
            }
        }
        this.popupLayout.addView(linearLayout);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.width = -2;
        layoutParams.height = -2;
        linearLayout.setLayoutParams(layoutParams);
    }

    public boolean lambda$showPopup$42(View view, MotionEvent motionEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (motionEvent.getActionMasked() != 0 || (actionBarPopupWindow = this.popupWindow) == null || !actionBarPopupWindow.isShowing()) {
            return false;
        }
        view.getHitRect(this.popupRect);
        if (this.popupRect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
            return false;
        }
        this.popupWindow.dismiss();
        return false;
    }

    public void lambda$showPopup$43(KeyEvent keyEvent) {
        ActionBarPopupWindow actionBarPopupWindow;
        if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0 && (actionBarPopupWindow = this.popupWindow) != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
        }
    }

    public void lambda$showPopup$44() {
        this.popupLayout.removeInnerViews();
    }

    public void lambda$showTypefaceMenu$27(DynamicAnimation dynamicAnimation, float f, float f2) {
        float f3 = f / 1000.0f;
        this.typefaceMenuTransformProgress = f3;
        this.typefaceListView.setAlpha(f3);
        this.typefaceListView.invalidate();
        this.overlayLayout.invalidate();
        this.textOptionsView.getTypefaceCell().setAlpha(1.0f - this.typefaceMenuTransformProgress);
    }

    public void lambda$showTypefaceMenu$28(boolean z, DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
        if (dynamicAnimation == this.typefaceMenuTransformAnimation) {
            this.typefaceMenuTransformAnimation = null;
            if (!z) {
                this.typefaceListView.setVisibility(8);
            }
            this.typefaceListView.setMaskProvider(null);
        }
    }

    public static void lambda$shutdown$25() {
        Looper myLooper = Looper.myLooper();
        if (myLooper != null) {
            myLooper.quit();
        }
    }

    public void lambda$switchTab$17(View view, View view2, ValueAnimator valueAnimator) {
        this.tabsSelectionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.tabsLayout.invalidate();
        this.bottomLayout.invalidate();
        this.overlayLayout.invalidate();
        int i = 0;
        while (i < this.tabsLayout.getChildCount()) {
            this.tabsLayout.getChildAt(i).setAlpha(((i == this.tabsNewSelectedIndex ? this.tabsSelectionProgress : i == this.tabsSelectedIndex ? 1.0f - this.tabsSelectionProgress : 0.0f) * 0.4f) + 0.6f);
            i++;
        }
        float interpolation = CubicBezierInterpolator.DEFAULT.getInterpolation(this.tabsSelectionProgress);
        if (view == null || view2 == null) {
            return;
        }
        float f = 1.0f - interpolation;
        float f2 = (f * 0.4f) + 0.6f;
        view.setScaleX(f2);
        view.setScaleY(f2);
        view.setTranslationY((AndroidUtilities.dp(16.0f) * Math.min(interpolation, 0.25f)) / 0.25f);
        view.setAlpha(1.0f - (Math.min(interpolation, 0.25f) / 0.25f));
        float f3 = (interpolation * 0.4f) + 0.6f;
        view2.setScaleX(f3);
        view2.setScaleY(f3);
        view2.setTranslationY(((-AndroidUtilities.dp(16.0f)) * Math.min(f, 0.25f)) / 0.25f);
        view2.setAlpha(1.0f - (Math.min(f, 0.25f) / 0.25f));
    }

    private void onWindowSizeChanged() {
        getHeight();
    }

    private void openStickersView() {
        final int i = this.tabsSelectedIndex;
        switchTab(1);
        postDelayed(new Runnable() {
            @Override
            public final void run() {
                LPhotoPaintView.this.lambda$openStickersView$18();
            }
        }, 350L);
        EmojiBottomSheet emojiBottomSheet = new EmojiBottomSheet(getContext(), false, this.resourcesProvider, false) {
            @Override
            public boolean canShowWidget(Integer num) {
                return num.intValue() == 2;
            }
        };
        emojiBottomSheet.whenDocumentSelected(new Utilities.Callback3Return() {
            @Override
            public final Object run(Object obj, Object obj2, Object obj3) {
                Boolean lambda$openStickersView$19;
                lambda$openStickersView$19 = LPhotoPaintView.this.lambda$openStickersView$19(obj, (TLRPC.Document) obj2, (Boolean) obj3);
                return lambda$openStickersView$19;
            }
        });
        emojiBottomSheet.whenWidgetSelected(new Utilities.CallbackReturn() {
            @Override
            public final Object run(Object obj) {
                Boolean lambda$openStickersView$20;
                lambda$openStickersView$20 = LPhotoPaintView.this.lambda$openStickersView$20((Integer) obj);
                return lambda$openStickersView$20;
            }
        });
        emojiBottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                LPhotoPaintView.this.lambda$openStickersView$21(i, dialogInterface);
            }
        });
        emojiBottomSheet.show();
        onOpenCloseStickersAlert(true);
    }

    private void registerRemovalUndo(final EntityView entityView) {
        if (entityView == null) {
            return;
        }
        this.undoStore.registerUndo(entityView.getUUID(), new Runnable() {
            @Override
            public final void run() {
                LPhotoPaintView.this.lambda$registerRemovalUndo$45(entityView);
            }
        });
    }

    public void lambda$registerRemovalUndo$45(EntityView entityView) {
        EntityView entityView2 = this.currentEntityView;
        if (entityView == entityView2 && entityView2 != null) {
            entityView2.deselect();
            this.currentEntityView = null;
            if (entityView instanceof TextPaintView) {
                ValueAnimator valueAnimator = this.tabsSelectionAnimator;
                if (valueAnimator != null && this.tabsNewSelectedIndex != 0) {
                    valueAnimator.cancel();
                }
                switchTab(0);
            }
        }
        this.entitiesView.removeView(entityView);
        if (entityView != null) {
            this.undoStore.unregisterUndo(entityView.getUUID());
        }
        this.weightChooserView.setValueOverride(this.weightDefaultValueOverride);
        this.weightChooserView.setShowPreview(true);
        this.colorSwatch.brushWeight = this.weightDefaultValueOverride.get();
        setCurrentSwatch(this.colorSwatch, true);
    }

    public boolean selectEntity(EntityView entityView) {
        return selectEntity(entityView, true);
    }

    private boolean selectEntity(EntityView entityView, boolean z) {
        boolean z2;
        int i;
        boolean z3 = entityView instanceof TextPaintView;
        int i2 = 2;
        if (z3 && (((i = this.tabsNewSelectedIndex) == -1 && this.tabsSelectedIndex != 2) || (i != -1 && i != 2))) {
            ValueAnimator valueAnimator = this.tabsSelectionAnimator;
            if (valueAnimator != null && i != 2) {
                valueAnimator.cancel();
            }
            if (this.isColorListShown) {
                showColorList(false);
            }
            switchTab(2);
        }
        if (z3 && z) {
            TextPaintView textPaintView = (TextPaintView) entityView;
            int gravity = textPaintView.getEditText().getGravity();
            if (gravity == 17) {
                i2 = 1;
            } else if (gravity != 21) {
                i2 = 0;
            }
            this.textOptionsView.setAlignment(i2);
            this.textOptionsView.setTypeface(textPaintView.getTypeface().getKey());
            this.textOptionsView.setOutlineType(textPaintView.getType(), true);
            this.overlayLayout.invalidate();
        }
        EntityView entityView2 = this.currentEntityView;
        if (entityView2 == null) {
            z2 = false;
        } else {
            if (entityView2 == entityView) {
                if (!this.editingText) {
                    showMenuForEntity(entityView2);
                } else if (entityView2 instanceof TextPaintView) {
                    AndroidUtilities.showKeyboard(((TextPaintView) entityView2).getFocusedView());
                    hideEmojiPopup(false);
                }
                return true;
            }
            entityView2.deselect();
            EntityView entityView3 = this.currentEntityView;
            if (entityView3 instanceof TextPaintView) {
                ((TextPaintView) entityView3).endEditing();
                if (!z3) {
                    this.editingText = false;
                    AndroidUtilities.hideKeyboard(((TextPaintView) this.currentEntityView).getFocusedView());
                    hideEmojiPopup(false);
                }
            }
            z2 = true;
        }
        EntityView entityView4 = this.currentEntityView;
        this.currentEntityView = entityView;
        if ((entityView4 instanceof TextPaintView) && TextUtils.isEmpty(((TextPaintView) entityView4).getText())) {
            lambda$registerRemovalUndo$45(entityView4);
        }
        EntityView entityView5 = this.currentEntityView;
        if (entityView5 == null) {
            ValueAnimator valueAnimator2 = this.tabsSelectionAnimator;
            if (valueAnimator2 != null && this.tabsNewSelectedIndex != 0) {
                valueAnimator2.cancel();
            }
            if (this.isColorListShown) {
                showColorList(false);
            }
            switchTab(0);
            this.weightChooserView.setValueOverride(this.weightDefaultValueOverride);
            this.weightChooserView.setShowPreview(true);
            this.colorSwatch.brushWeight = this.weightDefaultValueOverride.get();
            setCurrentSwatch(this.colorSwatch, true);
            return z2;
        }
        entityView5.select(this.selectionContainerView);
        this.entitiesView.bringChildToFront(this.currentEntityView);
        EntityView entityView6 = this.currentEntityView;
        if (!(entityView6 instanceof TextPaintView)) {
            this.weightChooserView.setValueOverride(this.weightDefaultValueOverride);
            this.weightChooserView.setShowPreview(true);
            this.colorSwatch.brushWeight = this.weightDefaultValueOverride.get();
            setCurrentSwatch(this.colorSwatch, true);
            return true;
        }
        final TextPaintView textPaintView2 = (TextPaintView) entityView6;
        textPaintView2.disableAutoresize(false);
        textPaintView2.getSwatch().brushWeight = this.colorSwatch.brushWeight;
        setCurrentSwatch(textPaintView2.getSwatch(), true);
        final float f = (int) (this.paintingSize.width / 9.0f);
        this.weightChooserView.setValueOverride(new PaintWeightChooserView.ValueOverride() {
            @Override
            public float get() {
                return textPaintView2.getBaseFontSize() / f;
            }

            @Override
            public void set(float f2) {
                textPaintView2.disableAutoresize(true);
                textPaintView2.setBaseFontSize((int) (f * f2));
            }
        });
        this.weightChooserView.setShowPreview(false);
        return true;
    }

    public void setCurrentSwatch(Swatch swatch, boolean z) {
        setCurrentSwatch(swatch, z, null);
    }

    private void setCurrentSwatch(Swatch swatch, boolean z, final Integer num) {
        Swatch swatch2 = this.colorSwatch;
        if (swatch2 != swatch) {
            swatch2.color = swatch.color;
            swatch2.colorLocation = swatch.colorLocation;
            swatch2.brushWeight = swatch.brushWeight;
            PersistColorPalette.getInstance(this.currentAccount).selectColor(swatch.color);
            PersistColorPalette.getInstance(this.currentAccount).setCurrentWeight(swatch.brushWeight);
        }
        this.renderView.setColor(swatch.color);
        this.renderView.setBrushSize(swatch.brushWeight);
        if (z) {
            final int i = this.colorSwatch.color;
            if (num == null || num.intValue() == i) {
                FrameLayout frameLayout = this.bottomLayout;
                if (frameLayout != null) {
                    frameLayout.invalidate();
                }
            } else {
                ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(150L);
                duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        LPhotoPaintView.this.lambda$setCurrentSwatch$31(num, i, valueAnimator);
                    }
                });
                duration.start();
            }
        }
        EntityView entityView = this.currentEntityView;
        if (entityView instanceof TextPaintView) {
            ((TextPaintView) entityView).setSwatch(new Swatch(swatch.color, swatch.colorLocation, swatch.brushWeight));
        }
    }

    public void setNewColor(final int i) {
        Swatch swatch = this.colorSwatch;
        final int i2 = swatch.color;
        swatch.color = i;
        setCurrentSwatch(swatch, true);
        ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(150L);
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                LPhotoPaintView.this.lambda$setNewColor$12(i2, i, valueAnimator);
            }
        });
        duration.start();
    }

    private void setTextAlignment(TextPaintView textPaintView, int i) {
        textPaintView.setAlign(i);
        int i2 = 2;
        textPaintView.getEditText().setGravity(i != 1 ? i != 2 ? 19 : 21 : 17);
        if (i == 1) {
            i2 = 4;
        } else if (i == 2 ? !LocaleController.isRTL : LocaleController.isRTL) {
            i2 = 3;
        }
        textPaintView.getEditText().setTextAlignment(i2);
    }

    private void setTextType(int i) {
        this.selectedTextType = i;
        EntityView entityView = this.currentEntityView;
        if (entityView instanceof TextPaintView) {
            ((TextPaintView) entityView).setType(i);
        }
        PersistColorPalette.getInstance(this.currentAccount).setCurrentTextType(i);
        this.textOptionsView.setOutlineType(i);
    }

    private void setupTabsLayout(Context context) {
        LinearLayout linearLayout = new LinearLayout(context) {
            Paint linePaint;

            {
                Paint paint = new Paint(1);
                this.linePaint = paint;
                paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
                this.linePaint.setStyle(Paint.Style.STROKE);
                this.linePaint.setStrokeCap(Paint.Cap.ROUND);
                setWillNotDraw(false);
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                TextView textView = (TextView) getChildAt(LPhotoPaintView.this.tabsSelectedIndex);
                TextView textView2 = LPhotoPaintView.this.tabsNewSelectedIndex != -1 ? (TextView) getChildAt(LPhotoPaintView.this.tabsNewSelectedIndex) : null;
                this.linePaint.setColor(textView.getCurrentTextColor());
                float y = ((textView.getY() + textView.getHeight()) - textView.getPaddingBottom()) + AndroidUtilities.dp(3.0f);
                Layout layout = textView.getLayout();
                Layout layout2 = textView2 != null ? textView2.getLayout() : null;
                float interpolation = layout2 == null ? 0.0f : CubicBezierInterpolator.DEFAULT.getInterpolation(LPhotoPaintView.this.tabsSelectionProgress);
                float lerp = AndroidUtilities.lerp(textView.getX() + layout.getPrimaryHorizontal(layout.getLineStart(0)), textView2 != null ? textView2.getX() + layout2.getPrimaryHorizontal(layout.getLineStart(0)) : 0.0f, interpolation);
                canvas.drawLine(lerp, y, lerp + AndroidUtilities.lerp(layout.getPrimaryHorizontal(layout.getLineEnd(0)) - layout.getPrimaryHorizontal(layout.getLineStart(0)), layout2 != null ? layout2.getPrimaryHorizontal(layout2.getLineEnd(0)) - layout2.getPrimaryHorizontal(layout2.getLineStart(0)) : 0.0f, interpolation), y, this.linePaint);
            }
        };
        this.tabsLayout = linearLayout;
        linearLayout.setClipToPadding(false);
        this.tabsLayout.setOrientation(0);
        this.bottomLayout.addView(this.tabsLayout, LayoutHelper.createFrame(-1, 40.0f, 80, 52.0f, 0.0f, 52.0f, 0.0f));
        TextView textView = new TextView(context);
        this.drawTab = textView;
        textView.setText(LocaleController.getString(R.string.PhotoEditorDraw).toUpperCase());
        TextView textView2 = this.drawTab;
        int i = Theme.key_listSelector;
        textView2.setBackground(Theme.createSelectorDrawable(getThemedColor(i), 7));
        this.drawTab.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        this.drawTab.setTextColor(-1);
        this.drawTab.setTextSize(1, 14.0f);
        this.drawTab.setGravity(1);
        this.drawTab.setTypeface(AndroidUtilities.bold());
        this.drawTab.setSingleLine();
        this.drawTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                LPhotoPaintView.this.lambda$setupTabsLayout$14(view);
            }
        });
        this.tabsLayout.addView(this.drawTab, LayoutHelper.createLinear(0, -2, 1.0f));
        TextView textView3 = new TextView(context);
        this.stickerTab = textView3;
        textView3.setText(LocaleController.getString(R.string.PhotoEditorSticker).toUpperCase());
        this.stickerTab.setBackground(Theme.createSelectorDrawable(getThemedColor(i), 7));
        this.stickerTab.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        this.stickerTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                LPhotoPaintView.this.lambda$setupTabsLayout$15(view);
            }
        });
        this.stickerTab.setTextColor(-1);
        this.stickerTab.setTextSize(1, 14.0f);
        this.stickerTab.setGravity(1);
        this.stickerTab.setTypeface(AndroidUtilities.bold());
        this.stickerTab.setAlpha(0.6f);
        this.stickerTab.setSingleLine();
        this.tabsLayout.addView(this.stickerTab, LayoutHelper.createLinear(0, -2, 1.0f));
        TextView textView4 = new TextView(context);
        this.textTab = textView4;
        textView4.setText(LocaleController.getString(R.string.PhotoEditorText).toUpperCase());
        this.textTab.setBackground(Theme.createSelectorDrawable(getThemedColor(i), 7));
        this.textTab.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        this.textTab.setTextColor(-1);
        this.textTab.setTextSize(1, 14.0f);
        this.textTab.setGravity(1);
        this.textTab.setTypeface(AndroidUtilities.bold());
        this.textTab.setAlpha(0.6f);
        this.textTab.setSingleLine();
        this.textTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                LPhotoPaintView.this.lambda$setupTabsLayout$16(view);
            }
        });
        this.tabsLayout.addView(this.textTab, LayoutHelper.createLinear(0, -2, 1.0f));
    }

    public void showColorList(final boolean z) {
        if (this.isColorListShown != z) {
            this.isColorListShown = z;
            SpringAnimation springAnimation = this.toolsTransformAnimation;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            SpringAnimation springAnimation2 = new SpringAnimation(new FloatValueHolder(z ? 0.0f : 1000.0f));
            this.toolsTransformAnimation = springAnimation2;
            springAnimation2.setSpring(new SpringForce().setFinalPosition(z ? 1000.0f : 0.0f).setStiffness(1250.0f).setDampingRatio(1.0f));
            final boolean[] zArr = {this.keyboardVisible || this.emojiViewVisible};
            final float translationY = this.bottomLayout.getTranslationY();
            final ViewGroup barView = getBarView();
            this.toolsTransformAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                @Override
                public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                    LPhotoPaintView.this.lambda$showColorList$29(barView, z, zArr, translationY, dynamicAnimation, f, f2);
                }
            });
            this.toolsTransformAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                    LPhotoPaintView.this.lambda$showColorList$30(z, dynamicAnimation, z2, f, f2);
                }
            });
            this.toolsTransformAnimation.start();
            if (z) {
                this.colorsListView.setVisibility(0);
                this.colorsListView.setSelectedColorIndex(PersistColorPalette.getInstance(this.currentAccount).getCurrentColorPosition());
            }
        }
    }

    private void showEmojiPopup(int i) {
        this.bottomPanelIgnoreOnce = false;
        if (i != 1) {
            ChatActivityEnterViewAnimatedIconView emojiButton = this.textOptionsView.getEmojiButton();
            if (emojiButton != null) {
                emojiButton.setState(ChatActivityEnterViewAnimatedIconView.State.SMILE, true);
            }
            EmojiView emojiView = this.emojiView;
            if (emojiView != null) {
                this.emojiViewWasVisible = this.emojiViewVisible;
                this.emojiViewVisible = false;
                if (AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow) {
                    emojiView.setVisibility(8);
                }
            }
            if (i == 0) {
                this.emojiPadding = 0;
            }
            updateKeyboard();
            requestLayout();
            onWindowSizeChanged();
            return;
        }
        EmojiView emojiView2 = this.emojiView;
        boolean z = emojiView2 != null && emojiView2.getVisibility() == 0;
        createEmojiView();
        this.emojiView.setVisibility(0);
        this.emojiViewWasVisible = this.emojiViewVisible;
        this.emojiViewVisible = true;
        EmojiView emojiView3 = this.emojiView;
        if (this.keyboardHeight <= 0) {
            this.keyboardHeight = AndroidUtilities.isTablet() ? AndroidUtilities.dp(150.0f) : MessagesController.getGlobalEmojiSettings().getInt("kbd_height", AndroidUtilities.dp(200.0f));
        }
        if (this.keyboardHeightLand <= 0) {
            this.keyboardHeightLand = AndroidUtilities.isTablet() ? AndroidUtilities.dp(150.0f) : MessagesController.getGlobalEmojiSettings().getInt("kbd_height_land3", AndroidUtilities.dp(200.0f));
        }
        android.graphics.Point point = AndroidUtilities.displaySize;
        int i2 = point.x > point.y ? this.keyboardHeightLand : this.keyboardHeight;
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) emojiView3.getLayoutParams();
        layoutParams.height = i2;
        emojiView3.setLayoutParams(layoutParams);
        if (!AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
            EntityView entityView = this.currentEntityView;
            if (entityView instanceof TextPaintView) {
                AndroidUtilities.hideKeyboard(((TextPaintView) entityView).getEditText());
            }
        }
        this.emojiWasPadding = i2;
        this.emojiPadding = i2;
        requestLayout();
        updateKeyboard();
        ChatActivityEnterViewAnimatedIconView emojiButton2 = this.textOptionsView.getEmojiButton();
        if (emojiButton2 != null) {
            emojiButton2.setState(ChatActivityEnterViewAnimatedIconView.State.KEYBOARD, true);
        }
        onWindowSizeChanged();
        if (z || this.keyboardVisible) {
            return;
        }
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.emojiPadding, 0.0f);
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                LPhotoPaintView.this.lambda$showEmojiPopup$46(valueAnimator);
            }
        });
        ofFloat.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                LPhotoPaintView.this.emojiView.setTranslationY(0.0f);
            }
        });
        ofFloat.start();
    }

    private void showMenuForEntity(final EntityView entityView) {
        int[] centerLocationInWindow = getCenterLocationInWindow(entityView);
        showPopup(new Runnable() {
            @Override
            public final void run() {
                LPhotoPaintView.this.lambda$showMenuForEntity$41(entityView);
            }
        }, this, 51, centerLocationInWindow[0], centerLocationInWindow[1] - AndroidUtilities.dp(32.0f));
    }

    private void showPhotoAlert() {
        final ChatAttachAlert chatAttachAlert = new ChatAttachAlert(getContext(), new ChatActivity(null) {
            @Override
            public TLRPC.User getCurrentUser() {
                return UserConfig.getInstance(this.currentAccount).getCurrentUser();
            }

            @Override
            public long getDialogId() {
                return 0L;
            }

            @Override
            public Activity getParentActivity() {
                return AndroidUtilities.findActivity(LPhotoPaintView.this.getContext());
            }

            @Override
            public Theme.ResourcesProvider getResourceProvider() {
                return LPhotoPaintView.this.resourcesProvider;
            }

            @Override
            public boolean isKeyboardVisible() {
                return false;
            }

            @Override
            public boolean isLightStatusBar() {
                return false;
            }
        }, false, false, false, this.resourcesProvider);
        chatAttachAlert.drawNavigationBar = true;
        chatAttachAlert.setupPhotoPicker(LocaleController.getString(R.string.AddImage));
        chatAttachAlert.setDelegate(new ChatAttachAlert.ChatAttachViewDelegate() {
            long start;

            @Override
            public void didPressedButton(int i, boolean z, boolean z2, int i2, long j, boolean z3, boolean z4) {
                try {
                    HashMap<Object, Object> selectedPhotos = chatAttachAlert.getPhotoLayout().getSelectedPhotos();
                    if (selectedPhotos.isEmpty()) {
                        return;
                    }
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) selectedPhotos.values().iterator().next();
                    String str = photoEntry.imagePath;
                    if (str == null) {
                        str = photoEntry.path;
                    }
                    LPhotoPaintView lPhotoPaintView = LPhotoPaintView.this;
                    lPhotoPaintView.appearAnimation(lPhotoPaintView.createPhoto(str, true));
                    chatAttachAlert.lambda$new$0();
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }

            @Override
            public void didSelectBot(TLRPC.User user) {
                ChatAttachAlert.ChatAttachViewDelegate.CC.$default$didSelectBot(this, user);
            }

            @Override
            public void doOnIdle(Runnable runnable) {
                runnable.run();
            }

            @Override
            public boolean needEnterComment() {
                return ChatAttachAlert.ChatAttachViewDelegate.CC.$default$needEnterComment(this);
            }

            @Override
            public void onCameraOpened() {
                ChatAttachAlert.ChatAttachViewDelegate.CC.$default$onCameraOpened(this);
            }

            @Override
            public void onWallpaperSelected(Object obj) {
                ChatAttachAlert.ChatAttachViewDelegate.CC.$default$onWallpaperSelected(this, obj);
            }

            @Override
            public void openAvatarsSearch() {
                ChatAttachAlert.ChatAttachViewDelegate.CC.$default$openAvatarsSearch(this);
            }

            @Override
            public boolean selectItemOnClicking() {
                this.start = System.currentTimeMillis();
                return true;
            }

            @Override
            public void sendAudio(ArrayList arrayList, CharSequence charSequence, boolean z, int i, long j, boolean z2) {
                ChatAttachAlert.ChatAttachViewDelegate.CC.$default$sendAudio(this, arrayList, charSequence, z, i, j, z2);
            }
        });
        chatAttachAlert.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public final void onDismiss(DialogInterface dialogInterface) {
                MediaController.forceBroadcastNewPhotos = false;
            }
        });
        chatAttachAlert.setMaxSelectedPhotos(1, false);
        chatAttachAlert.init();
        MediaController.forceBroadcastNewPhotos = true;
        chatAttachAlert.getPhotoLayout().loadGalleryPhotos();
        chatAttachAlert.show();
    }

    private void showPopup(Runnable runnable, View view, int i, int i2, int i3) {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.popupWindow.dismiss();
            return;
        }
        if (this.popupLayout == null) {
            this.popupRect = new Rect();
            ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getContext(), this.resourcesProvider);
            this.popupLayout = actionBarPopupWindowLayout;
            actionBarPopupWindowLayout.setAnimationEnabled(true);
            this.popupLayout.setBackgroundColor(-14145495);
            this.popupLayout.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public final boolean onTouch(View view2, MotionEvent motionEvent) {
                    boolean lambda$showPopup$42;
                    lambda$showPopup$42 = LPhotoPaintView.this.lambda$showPopup$42(view2, motionEvent);
                    return lambda$showPopup$42;
                }
            });
            this.popupLayout.setDispatchKeyEventListener(new ActionBarPopupWindow.OnDispatchKeyEventListener() {
                @Override
                public final void onDispatchKeyEvent(KeyEvent keyEvent) {
                    LPhotoPaintView.this.lambda$showPopup$43(keyEvent);
                }
            });
            this.popupLayout.setShownFromBottom(true);
        }
        this.popupLayout.removeInnerViews();
        runnable.run();
        if (this.popupWindow == null) {
            ActionBarPopupWindow actionBarPopupWindow2 = new ActionBarPopupWindow(this.popupLayout, -2, -2);
            this.popupWindow = actionBarPopupWindow2;
            actionBarPopupWindow2.setAnimationEnabled(true);
            this.popupWindow.setAnimationStyle(R.style.PopupAnimation);
            this.popupWindow.setOutsideTouchable(true);
            this.popupWindow.setClippingEnabled(true);
            this.popupWindow.setInputMethodMode(2);
            this.popupWindow.setSoftInputMode(0);
            this.popupWindow.getContentView().setFocusableInTouchMode(true);
            this.popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public final void onDismiss() {
                    LPhotoPaintView.this.lambda$showPopup$44();
                }
            });
        }
        this.popupLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.popupWindow.setFocusable(true);
        if ((i & 48) != 0) {
            i2 -= this.popupLayout.getMeasuredWidth() / 2;
            i3 -= this.popupLayout.getMeasuredHeight();
        }
        this.popupWindow.showAtLocation(view, i, i2, i3);
        ActionBarPopupWindow.startAnimation(this.popupLayout);
    }

    public void showTypefaceMenu(final boolean z) {
        if (this.isTypefaceMenuShown != z) {
            this.isTypefaceMenuShown = z;
            SpringAnimation springAnimation = this.typefaceMenuTransformAnimation;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            SpringAnimation springAnimation2 = new SpringAnimation(new FloatValueHolder(z ? 0.0f : 1000.0f));
            this.typefaceMenuTransformAnimation = springAnimation2;
            springAnimation2.setSpring(new SpringForce().setFinalPosition(z ? 1000.0f : 0.0f).setStiffness(1250.0f).setDampingRatio(1.0f));
            if (z) {
                this.typefaceListView.setAlpha(0.0f);
                this.typefaceListView.setVisibility(0);
            }
            this.typefaceMenuTransformAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                @Override
                public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                    LPhotoPaintView.this.lambda$showTypefaceMenu$27(dynamicAnimation, f, f2);
                }
            });
            this.typefaceMenuTransformAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                    LPhotoPaintView.this.lambda$showTypefaceMenu$28(z, dynamicAnimation, z2, f, f2);
                }
            });
            this.typefaceMenuTransformAnimation.start();
        }
    }

    private Point startPositionRelativeToEntity(EntityView entityView) {
        MediaController.CropState cropState = this.currentCropState;
        float f = cropState != null ? 200.0f / cropState.cropScale : 200.0f;
        if (entityView != null) {
            Point position = entityView.getPosition();
            return new Point(position.x + f, position.y + f);
        }
        float f2 = cropState != null ? 100.0f / cropState.cropScale : 100.0f;
        Point centerPositionForEntity = centerPositionForEntity();
        while (true) {
            boolean z = false;
            for (int i = 0; i < this.entitiesView.getChildCount(); i++) {
                View childAt = this.entitiesView.getChildAt(i);
                if (childAt instanceof EntityView) {
                    Point position2 = ((EntityView) childAt).getPosition();
                    if (((float) Math.sqrt(Math.pow(position2.x - centerPositionForEntity.x, 2.0d) + Math.pow(position2.y - centerPositionForEntity.y, 2.0d))) < f2) {
                        z = true;
                    }
                }
            }
            if (!z) {
                return centerPositionForEntity;
            }
            centerPositionForEntity = new Point(centerPositionForEntity.x + f, centerPositionForEntity.y + f);
        }
    }

    private void switchTab(final int i) {
        if (this.tabsSelectedIndex == i || this.tabsNewSelectedIndex == i) {
            return;
        }
        ValueAnimator valueAnimator = this.tabsSelectionAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        final View barView = getBarView(this.tabsSelectedIndex);
        this.tabsNewSelectedIndex = i;
        final View barView2 = getBarView(i);
        PersistColorPalette.getInstance(this.currentAccount).setInTextMode(i == 2);
        this.colorSwatch.color = PersistColorPalette.getInstance(this.currentAccount).getCurrentColor();
        setCurrentSwatch(this.colorSwatch, true);
        ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(300L);
        this.tabsSelectionAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.tabsSelectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                LPhotoPaintView.this.lambda$switchTab$17(barView, barView2, valueAnimator2);
            }
        });
        this.tabsSelectionAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                LPhotoPaintView lPhotoPaintView = LPhotoPaintView.this;
                lPhotoPaintView.tabsSelectedIndex = lPhotoPaintView.tabsNewSelectedIndex;
                LPhotoPaintView.this.tabsNewSelectedIndex = -1;
                LPhotoPaintView.this.tabsLayout.invalidate();
                View view = barView;
                if (view != null && barView2 != null) {
                    view.setVisibility(8);
                }
                if (animator == LPhotoPaintView.this.tabsSelectionAnimator) {
                    LPhotoPaintView.this.tabsSelectionAnimator = null;
                }
            }

            @Override
            public void onAnimationStart(Animator animator) {
                PaintWeightChooserView paintWeightChooserView;
                float f;
                float f2;
                View view;
                if (barView != null && (view = barView2) != null) {
                    view.setVisibility(0);
                }
                if (i == 2) {
                    paintWeightChooserView = LPhotoPaintView.this.weightChooserView;
                    f = 0.5f;
                    f2 = 2.0f;
                } else {
                    Brush currentBrush = LPhotoPaintView.this.renderView.getCurrentBrush();
                    if ((currentBrush instanceof Brush.Blurer) || (currentBrush instanceof Brush.Eraser)) {
                        paintWeightChooserView = LPhotoPaintView.this.weightChooserView;
                        f = 0.4f;
                        f2 = 1.75f;
                    } else {
                        paintWeightChooserView = LPhotoPaintView.this.weightChooserView;
                        f = 0.05f;
                        f2 = 1.0f;
                    }
                }
                paintWeightChooserView.setMinMax(f, f2);
            }
        });
        this.tabsSelectionAnimator.start();
    }

    public float adjustPanLayoutHelperProgress() {
        return this.panTranslationProgress;
    }

    @Override
    public boolean allowInteraction(EntityView entityView) {
        return !this.editingText;
    }

    public void appearAnimation(final View view) {
        float scaleX = view.getScaleX();
        float scaleY = view.getScaleY();
        view.setScaleX(scaleX * 0.5f);
        view.setScaleY(0.5f * scaleY);
        view.setAlpha(0.0f);
        view.animate().scaleX(scaleX).scaleY(scaleY).alpha(1.0f).setInterpolator(new OvershootInterpolator(3.0f)).setDuration(240L).withEndAction(new Runnable() {
            @Override
            public final void run() {
                LPhotoPaintView.this.lambda$appearAnimation$23(view);
            }
        }).start();
    }

    protected void createEmojiView() {
        EmojiView emojiView = this.emojiView;
        if (emojiView != null && emojiView.currentAccount != UserConfig.selectedAccount) {
            removeView(emojiView);
            this.emojiView = null;
        }
        if (this.emojiView != null) {
            return;
        }
        EmojiView emojiView2 = new EmojiView(null, true, false, false, getContext(), false, null, null, true, this.resourcesProvider, false);
        this.emojiView = emojiView2;
        emojiView2.allowEmojisForNonPremium(true);
        this.emojiView.setVisibility(8);
        if (AndroidUtilities.isTablet()) {
            this.emojiView.setForseMultiwindowLayout(true);
        }
        this.emojiView.setDelegate(new AnonymousClass22());
        addView(this.emojiView);
    }

    public PhotoView createPhoto(String str, boolean z) {
        Size basePhotoSize = basePhotoSize(str);
        Pair<Integer, Integer> imageOrientation = AndroidUtilities.getImageOrientation(str);
        if ((((Integer) imageOrientation.first).intValue() / 90) % 2 == 1) {
            float f = basePhotoSize.width;
            basePhotoSize.width = basePhotoSize.height;
            basePhotoSize.height = f;
        }
        PhotoView photoView = new PhotoView(getContext(), centerPositionForEntity(), 0.0f, 1.0f, basePhotoSize, str, ((Integer) imageOrientation.first).intValue(), ((Integer) imageOrientation.second).intValue());
        photoView.setDelegate(this);
        this.entitiesView.addView(photoView);
        if (z) {
            registerRemovalUndo(photoView);
            selectEntity(photoView);
        }
        return photoView;
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.customTypefacesLoaded || this.entitiesView == null) {
            return;
        }
        for (int i3 = 0; i3 < this.entitiesView.getChildCount(); i3++) {
            View childAt = this.entitiesView.getChildAt(i3);
            if (childAt instanceof TextPaintView) {
                ((TextPaintView) childAt).updateTypeface();
            }
        }
    }

    protected abstract void didSetAnimatedSticker(RLottieDrawable rLottieDrawable);

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        EntitiesContainerView entitiesContainerView;
        FrameLayout frameLayout;
        int i = 0;
        if (view == this.entitiesView) {
            float f = this.shadowAlpha.set(this.drawShadow);
            if (f > 0.0f) {
                this.shadowPaint.setShadowLayer(AndroidUtilities.dp(24.0f * f), 0.0f, 0.0f, Theme.multAlpha(1090519039, f));
                this.shadowPaint.setColor(0);
                canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), 255, 31);
                canvas.translate(this.entitiesView.getX(), this.entitiesView.getY());
                canvas.scale(this.entitiesView.getScaleX(), this.entitiesView.getScaleY(), this.entitiesView.getWidth() / 2.0f, this.entitiesView.getHeight() / 2.0f);
                canvas.drawRect(0.0f, 0.0f, this.entitiesView.getWidth(), this.entitiesView.getHeight(), this.shadowPaint);
                canvas.drawRect(0.0f, 0.0f, this.entitiesView.getWidth(), this.entitiesView.getHeight(), this.clearPaint);
                canvas.restore();
            }
        }
        if ((view == this.renderView || view == this.renderInputView || ((view == (entitiesContainerView = this.entitiesView) && entitiesContainerView.getClipChildren()) || (view == (frameLayout = this.selectionContainerView) && frameLayout.getClipChildren()))) && this.currentCropState != null) {
            canvas.save();
            if (Build.VERSION.SDK_INT >= 21 && !this.inBubbleMode) {
                i = AndroidUtilities.statusBarHeight;
            }
            int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + i;
            int measuredWidth = view.getMeasuredWidth();
            int measuredHeight = view.getMeasuredHeight();
            MediaController.CropState cropState = this.currentCropState;
            int i2 = cropState.transformRotation;
            if (i2 != 90 && i2 != 270) {
                measuredHeight = measuredWidth;
                measuredWidth = measuredHeight;
            }
            float scaleX = measuredHeight * cropState.cropPw * view.getScaleX();
            MediaController.CropState cropState2 = this.currentCropState;
            int i3 = (int) (scaleX / cropState2.cropScale);
            int scaleY = (int) (((measuredWidth * cropState2.cropPh) * view.getScaleY()) / this.currentCropState.cropScale);
            float ceil = ((float) Math.ceil((getMeasuredWidth() - i3) / 2.0f)) + this.transformX;
            float measuredHeight2 = ((((((getMeasuredHeight() - this.emojiPadding) - currentActionBarHeight) - AndroidUtilities.dp(48.0f)) + getAdditionalBottom()) - scaleY) / 2.0f) + AndroidUtilities.dp(8.0f) + i + this.transformY;
            canvas.clipRect(Math.max(0.0f, ceil), Math.max(0.0f, measuredHeight2), Math.min(ceil + i3, getMeasuredWidth()), Math.min(getMeasuredHeight(), measuredHeight2 + scaleY));
            i = 1;
        }
        boolean drawChild = super.drawChild(canvas, view, j);
        if (i != 0) {
            canvas.restore();
        }
        return drawChild;
    }

    public int getAdditionalBottom() {
        return AndroidUtilities.dp(24.0f);
    }

    public int getAdditionalTop() {
        return AndroidUtilities.dp(48.0f);
    }

    public android.graphics.Bitmap getBitmap(java.util.ArrayList r23, android.graphics.Bitmap[] r24) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.Paint.Views.LPhotoPaintView.getBitmap(java.util.ArrayList, android.graphics.Bitmap[]):android.graphics.Bitmap");
    }

    public View getCancelView() {
        return this.cancelButton;
    }

    @Override
    public int[] getCenterLocation(EntityView entityView) {
        return getCenterLocationInWindow(entityView);
    }

    public float getCropRotation() {
        MediaController.CropState cropState = this.currentCropState;
        if (cropState != null) {
            return cropState.cropRotate + cropState.transformRotation;
        }
        return 0.0f;
    }

    public View getDoneView() {
        return this.doneButton;
    }

    public int getEmojiPadding(boolean z) {
        if (this.translateBottomPanelAfterResize && z) {
            bottomPanelTranslationY(0.0f, 1.0f);
        }
        if (this.keyboardVisible && this.translateBottomPanelAfterResize && !z) {
            return 0;
        }
        return this.emojiPadding;
    }

    public long getLcm() {
        return this.lcm.longValue();
    }

    public List<TLRPC.InputDocument> getMasks() {
        AnimatedEmojiSpan[] animatedEmojiSpanArr;
        int childCount = this.entitiesView.getChildCount();
        ArrayList arrayList = null;
        for (int i = 0; i < childCount; i++) {
            View childAt = this.entitiesView.getChildAt(i);
            if (childAt instanceof StickerView) {
                TLRPC.Document sticker = ((StickerView) childAt).getSticker();
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
                tL_inputDocument.id = sticker.id;
                tL_inputDocument.access_hash = sticker.access_hash;
                byte[] bArr = sticker.file_reference;
                tL_inputDocument.file_reference = bArr;
                if (bArr == null) {
                    tL_inputDocument.file_reference = new byte[0];
                }
                arrayList.add(tL_inputDocument);
            } else if (childAt instanceof TextPaintView) {
                CharSequence text = ((TextPaintView) childAt).getText();
                if ((text instanceof Spanned) && (animatedEmojiSpanArr = (AnimatedEmojiSpan[]) ((Spanned) text).getSpans(0, text.length(), AnimatedEmojiSpan.class)) != null) {
                    for (AnimatedEmojiSpan animatedEmojiSpan : animatedEmojiSpanArr) {
                        if (animatedEmojiSpan != null) {
                            TLRPC.Document document = animatedEmojiSpan.document;
                            if (document == null) {
                                document = AnimatedEmojiDrawable.findDocument(this.currentAccount, animatedEmojiSpan.getDocumentId());
                            }
                            if (document != null) {
                                if (arrayList == null) {
                                    arrayList = new ArrayList();
                                }
                                TLRPC.TL_inputDocument tL_inputDocument2 = new TLRPC.TL_inputDocument();
                                tL_inputDocument2.id = document.id;
                                tL_inputDocument2.access_hash = document.access_hash;
                                byte[] bArr2 = document.file_reference;
                                tL_inputDocument2.file_reference = bArr2;
                                if (bArr2 == null) {
                                    tL_inputDocument2.file_reference = new byte[0];
                                }
                                arrayList.add(tL_inputDocument2);
                            }
                        }
                    }
                }
            }
        }
        return arrayList;
    }

    public float getOffsetTranslationY() {
        return this.offsetTranslationY;
    }

    protected int getPKeyboardHeight() {
        return 0;
    }

    public RenderView getRenderView() {
        return this.renderView;
    }

    public float getSelectedEntityBottom() {
        float f;
        float height;
        EntityView entityView = this.currentEntityView;
        if (entityView == null) {
            f = getY();
            height = this.entitiesView.getMeasuredHeight();
        } else {
            int[] iArr = new int[2];
            entityView.getLocationInWindow(iArr);
            f = iArr[1];
            height = this.currentEntityView.getHeight() * this.entitiesView.getScaleY();
        }
        return f + height;
    }

    public ThanosEffect getThanosEffect() {
        if (!ThanosEffect.supports()) {
            return null;
        }
        if (this.thanosEffect == null) {
            ThanosEffect thanosEffect = new ThanosEffect(getContext(), new Runnable() {
                @Override
                public final void run() {
                    LPhotoPaintView.this.lambda$getThanosEffect$48();
                }
            });
            this.thanosEffect = thanosEffect;
            addView(thanosEffect);
        }
        return this.thanosEffect;
    }

    @Override
    public void getTransformedTouch(float f, float f2, float[] fArr) {
        android.graphics.Point point = AndroidUtilities.displaySize;
        float f3 = f2 - (point.y / 2.0f);
        double d = f - (point.x / 2.0f);
        double radians = (float) Math.toRadians(-this.entitiesView.getRotation());
        double cos = Math.cos(radians);
        Double.isNaN(d);
        double d2 = f3;
        double sin = Math.sin(radians);
        Double.isNaN(d2);
        fArr[0] = ((float) ((cos * d) - (sin * d2))) + (AndroidUtilities.displaySize.x / 2.0f);
        double sin2 = Math.sin(radians);
        Double.isNaN(d);
        double cos2 = Math.cos(radians);
        Double.isNaN(d2);
        fArr[1] = ((float) ((d * sin2) + (d2 * cos2))) + (AndroidUtilities.displaySize.y / 2.0f);
    }

    public View getView() {
        return IPhotoPaintView.CC.$default$getView(this);
    }

    public boolean hasChanges() {
        return this.undoStore.canUndo();
    }

    public void init() {
        this.textOptionsView.setTypeface(PersistColorPalette.getInstance(this.currentAccount).getCurrentTypeface());
        this.entitiesView.setVisibility(0);
        this.renderView.setVisibility(0);
        this.renderInputView.setVisibility(0);
    }

    public boolean isCurrentText() {
        return this.currentEntityView instanceof TextPaintView;
    }

    @Override
    public boolean isEntityDeletable() {
        return EntityView.EntityViewDelegate.CC.$default$isEntityDeletable(this);
    }

    public void maybeShowDismissalAlert(PhotoViewer photoViewer, Activity activity, final Runnable runnable) {
        if (this.isColorListShown) {
            showColorList(false);
            return;
        }
        if (this.emojiViewVisible) {
            hideEmojiPopup(true);
            return;
        }
        if (this.editingText && this.currentEntityView != null) {
            selectEntity(null);
            return;
        }
        if (!hasChanges()) {
            runnable.run();
            return;
        }
        if (activity == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, this.resourcesProvider);
        builder.setMessage(LocaleController.getString(R.string.PhotoEditorDiscardAlert));
        builder.setTitle(LocaleController.getString(R.string.DiscardChanges));
        builder.setPositiveButton(LocaleController.getString(R.string.PassportDiscard), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                runnable.run();
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        photoViewer.showAlertDialog(builder);
    }

    @Override
    public void onAddButtonPressed(View view) {
        showPopup(new Runnable() {
            @Override
            public final void run() {
                LPhotoPaintView.this.lambda$onAddButtonPressed$35();
            }
        }, this, 53, 0, getHeight());
    }

    public void onAnimationStateChanged(boolean z) {
        if (this.tabsSelectedIndex == 0) {
            this.weightChooserView.setLayerType(z ? 2 : 0, null);
            this.bottomLayout.setLayerType(z ? 2 : 0, null);
            this.topLayout.setLayerType(z ? 2 : 0, null);
        }
    }

    @Override
    public void onAttachedToWindow() {
        this.destroyed = false;
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.customTypefacesLoaded);
    }

    public boolean onBackPressed() {
        if (this.isColorListShown) {
            showColorList(false);
            return true;
        }
        if (this.emojiViewVisible) {
            hideEmojiPopup(true);
            return true;
        }
        if (!this.editingText) {
            return false;
        }
        selectEntity(null);
        return true;
    }

    @Override
    public void onBrushSelected(Brush brush) {
        PaintWeightChooserView paintWeightChooserView;
        float f;
        float f2;
        if ((brush instanceof Brush.Blurer) || (brush instanceof Brush.Eraser)) {
            paintWeightChooserView = this.weightChooserView;
            f = 0.4f;
            f2 = 1.75f;
        } else {
            paintWeightChooserView = this.weightChooserView;
            f = 0.05f;
            f2 = 1.0f;
        }
        paintWeightChooserView.setMinMax(f, f2);
        this.weightChooserView.setDrawCenter(!(brush instanceof Brush.Shape));
        if (this.renderView.getCurrentBrush() instanceof Brush.Shape) {
            this.ignoreToolChangeAnimationOnce = true;
        }
        this.renderView.setBrush(brush);
        Swatch swatch = this.colorSwatch;
        int i = swatch.color;
        swatch.color = PersistColorPalette.getInstance(this.currentAccount).getCurrentColor();
        this.colorSwatch.brushWeight = this.weightDefaultValueOverride.get();
        setCurrentSwatch(this.colorSwatch, true, Integer.valueOf(i));
        this.renderInputView.invalidate();
    }

    public void onCleanupEntities() {
        this.entitiesView.removeAllViews();
    }

    @Override
    public void onColorPickerSelected() {
        showColorList(true);
    }

    @Override
    public void onDetachedFromWindow() {
        this.destroyed = true;
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.customTypefacesLoaded);
    }

    public void onEmojiButtonClick() {
        boolean z = this.emojiViewVisible;
        if (z && (this.currentEntityView instanceof TextPaintView)) {
            this.bottomPanelIgnoreOnce = true;
        }
        if (z) {
            onEmojiViewCloseByClick();
        }
        showEmojiPopup(1 ^ (this.emojiViewVisible ? 1 : 0));
        if (z) {
            EntityView entityView = this.currentEntityView;
            if (entityView instanceof TextPaintView) {
                AndroidUtilities.showKeyboard(((TextPaintView) entityView).getEditText());
            }
        }
    }

    protected abstract void onEmojiViewCloseByClick();

    @Override
    public void onEntityDragEnd(boolean z) {
        EntityView.EntityViewDelegate.CC.$default$onEntityDragEnd(this, z);
    }

    @Override
    public void onEntityDragMultitouchEnd() {
        EntityView.EntityViewDelegate.CC.$default$onEntityDragMultitouchEnd(this);
    }

    @Override
    public void onEntityDragMultitouchStart() {
        EntityView.EntityViewDelegate.CC.$default$onEntityDragMultitouchStart(this);
    }

    @Override
    public void onEntityDragStart() {
        EntityView.EntityViewDelegate.CC.$default$onEntityDragStart(this);
    }

    @Override
    public void onEntityDragTrash(boolean z) {
        EntityView.EntityViewDelegate.CC.$default$onEntityDragTrash(this, z);
    }

    @Override
    public void onEntityDraggedBottom(boolean z) {
        EntityView.EntityViewDelegate.CC.$default$onEntityDraggedBottom(this, z);
    }

    @Override
    public void onEntityDraggedTop(boolean z) {
        EntityView.EntityViewDelegate.CC.$default$onEntityDraggedTop(this, z);
    }

    @Override
    public void onEntityHandleTouched() {
        EntityView.EntityViewDelegate.CC.$default$onEntityHandleTouched(this);
    }

    @Override
    public boolean onEntityLongClicked(EntityView entityView) {
        showMenuForEntity(entityView);
        return true;
    }

    @Override
    public boolean onEntitySelected(EntityView entityView) {
        return selectEntity(entityView);
    }

    @Override
    public PersistColorPalette onGetPalette() {
        return PersistColorPalette.getInstance(this.currentAccount);
    }

    @Override
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        int i5 = i3 - i;
        int i6 = i4 - i2;
        int i7 = 0;
        if (this.emojiView != null) {
            int measuredHeight = AndroidUtilities.isTablet() ? i6 - this.emojiView.getMeasuredHeight() : (measureKeyboardHeight() + i6) - this.emojiView.getMeasuredHeight();
            EmojiView emojiView = this.emojiView;
            emojiView.layout(0, measuredHeight, emojiView.getMeasuredWidth(), this.emojiView.getMeasuredHeight() + measuredHeight);
        }
        if (Build.VERSION.SDK_INT >= 21 && !this.inBubbleMode) {
            i7 = AndroidUtilities.statusBarHeight;
        }
        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight() + i7;
        int ceil = (int) Math.ceil((i5 - this.renderView.getMeasuredWidth()) / 2.0f);
        int dp = ((((i6 - currentActionBarHeight) - AndroidUtilities.dp(48.0f)) - this.renderView.getMeasuredHeight()) / 2) + AndroidUtilities.dp(8.0f) + i7 + ((getAdditionalTop() - getAdditionalBottom()) / 2);
        RenderView renderView = this.renderView;
        renderView.layout(ceil, dp, renderView.getMeasuredWidth() + ceil, this.renderView.getMeasuredHeight() + dp);
        View view = this.renderInputView;
        view.layout(ceil, dp, view.getMeasuredWidth() + ceil, this.renderInputView.getMeasuredHeight() + dp);
        int measuredWidth = ((this.renderView.getMeasuredWidth() - this.entitiesView.getMeasuredWidth()) / 2) + ceil;
        int measuredHeight2 = ((this.renderView.getMeasuredHeight() - this.entitiesView.getMeasuredHeight()) / 2) + dp;
        EntitiesContainerView entitiesContainerView = this.entitiesView;
        entitiesContainerView.layout(measuredWidth, measuredHeight2, entitiesContainerView.getMeasuredWidth() + measuredWidth, this.entitiesView.getMeasuredHeight() + measuredHeight2);
        ThanosEffect thanosEffect = this.thanosEffect;
        if (thanosEffect != null) {
            thanosEffect.layout(measuredWidth, measuredHeight2, this.entitiesView.getMeasuredWidth() + measuredWidth, this.entitiesView.getMeasuredHeight() + measuredHeight2);
        }
        FrameLayout frameLayout = this.selectionContainerView;
        frameLayout.layout(ceil, dp, frameLayout.getMeasuredWidth() + ceil, this.selectionContainerView.getMeasuredHeight() + dp);
    }

    @Override
    protected void onMeasure(int i, int i2) {
        float currentActionBarHeight;
        float f;
        this.ignoreLayout = true;
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        setMeasuredDimension(size, size2);
        int currentActionBarHeight2 = (((AndroidUtilities.displaySize.y - ActionBar.getCurrentActionBarHeight()) - getAdditionalTop()) - getAdditionalBottom()) - AndroidUtilities.dp(48.0f);
        Bitmap bitmap = this.bitmapToEdit;
        if (bitmap != null) {
            f = bitmap.getWidth();
            currentActionBarHeight = this.bitmapToEdit.getHeight();
        } else {
            currentActionBarHeight = (size2 - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(48.0f);
            f = size;
        }
        float f2 = size;
        float floor = (float) Math.floor((f2 * currentActionBarHeight) / f);
        float f3 = currentActionBarHeight2;
        if (floor > f3) {
            f2 = (float) Math.floor((f * f3) / currentActionBarHeight);
            floor = f3;
        }
        int i3 = (int) f2;
        int i4 = (int) floor;
        this.renderView.measure(View.MeasureSpec.makeMeasureSpec(i3, 1073741824), View.MeasureSpec.makeMeasureSpec(i4, 1073741824));
        this.renderInputView.measure(View.MeasureSpec.makeMeasureSpec(i3, 1073741824), View.MeasureSpec.makeMeasureSpec(i4, 1073741824));
        float f4 = f2 / this.paintingSize.width;
        this.baseScale = f4;
        this.entitiesView.setScaleX(f4);
        this.entitiesView.setScaleY(this.baseScale);
        this.entitiesView.measure(View.MeasureSpec.makeMeasureSpec((int) this.paintingSize.width, 1073741824), View.MeasureSpec.makeMeasureSpec((int) this.paintingSize.height, 1073741824));
        ThanosEffect thanosEffect = this.thanosEffect;
        if (thanosEffect != null) {
            thanosEffect.measure(View.MeasureSpec.makeMeasureSpec((int) this.paintingSize.width, 1073741824), View.MeasureSpec.makeMeasureSpec((int) this.paintingSize.height, 1073741824));
            this.thanosEffect.setScaleX(this.baseScale);
            this.thanosEffect.setScaleY(this.baseScale);
        }
        updateEntitiesSelections();
        this.selectionContainerView.measure(View.MeasureSpec.makeMeasureSpec(i3, 1073741824), View.MeasureSpec.makeMeasureSpec(i4, 1073741824));
        measureChild(this.bottomLayout, i, i2);
        measureChild(this.weightChooserView, i, i2);
        measureChild(this.pipetteContainerLayout, i, i2);
        measureChild(this.overlayLayout, i, View.MeasureSpec.makeMeasureSpec(size2 - Math.max(getPKeyboardHeight(), this.emojiPadding), 1073741824));
        FrameLayout frameLayout = this.topLayout;
        frameLayout.setPadding(frameLayout.getPaddingLeft(), AndroidUtilities.dp(12.0f) + AndroidUtilities.statusBarHeight, this.topLayout.getPaddingRight(), this.topLayout.getPaddingBottom());
        measureChild(this.topLayout, i, i2);
        this.ignoreLayout = false;
        if (!this.waitingForKeyboardOpen && AndroidUtilities.dp(20.0f) >= 0 && !this.emojiViewVisible && !this.isAnimatePopupClosing) {
            this.ignoreLayout = true;
            hideEmojiView();
            this.ignoreLayout = false;
        }
        if (AndroidUtilities.dp(20.0f) < 0) {
            hideEmojiView();
        }
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            measureChild(emojiView, i, i2);
        }
    }

    @Override
    public void onNewTextSelected() {
        if (this.keyboardVisible || this.emojiViewVisible) {
            onEmojiButtonClick();
        } else {
            createText(true);
        }
    }

    protected abstract void onOpenCloseStickersAlert(boolean z);

    @Override
    public void onResume() {
        this.renderView.redraw();
    }

    @Override
    public void onSizeChanged(int i, boolean z) {
        boolean z2;
        SharedPreferences.Editor edit;
        int i2;
        String str;
        if (i > AndroidUtilities.dp(50.0f) && this.keyboardVisible && !AndroidUtilities.isInMultiwindow && !AndroidUtilities.isTablet()) {
            if (z) {
                this.keyboardHeightLand = i;
                edit = MessagesController.getGlobalEmojiSettings().edit();
                i2 = this.keyboardHeightLand;
                str = "kbd_height_land3";
            } else {
                this.keyboardHeight = i;
                edit = MessagesController.getGlobalEmojiSettings().edit();
                i2 = this.keyboardHeight;
                str = "kbd_height";
            }
            edit.putInt(str, i2).commit();
        }
        if (this.emojiViewVisible) {
            int i3 = z ? this.keyboardHeightLand : this.keyboardHeight;
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.emojiView.getLayoutParams();
            int i4 = layoutParams.width;
            int i5 = AndroidUtilities.displaySize.x;
            if (i4 != i5 || layoutParams.height != i3) {
                layoutParams.width = i5;
                layoutParams.height = i3;
                this.emojiView.setLayoutParams(layoutParams);
                int i6 = layoutParams.height;
                this.emojiWasPadding = i6;
                this.emojiPadding = i6;
                requestLayout();
                updateKeyboard();
                onWindowSizeChanged();
            }
        }
        if (this.lastSizeChangeValue1 == i && this.lastSizeChangeValue2 == z) {
            onWindowSizeChanged();
            return;
        }
        this.lastSizeChangeValue1 = i;
        this.lastSizeChangeValue2 = z;
        boolean z3 = this.keyboardVisible;
        EntityView entityView = this.currentEntityView;
        if (entityView instanceof TextPaintView) {
            this.keyboardVisible = ((TextPaintView) entityView).getEditText().isFocused() && i > 0;
        } else {
            this.keyboardVisible = false;
        }
        if (this.keyboardVisible && this.emojiViewVisible) {
            showEmojiPopup(0);
        }
        if (this.emojiPadding != 0 && !(z2 = this.keyboardVisible) && z2 != z3 && !this.emojiViewVisible) {
            this.emojiPadding = 0;
            requestLayout();
            updateKeyboard();
        }
        if (z3 && !this.keyboardVisible && this.emojiPadding > 0 && this.translateBottomPanelAfterResize) {
            this.translateBottomPanelAfterResize = false;
            bottomPanelTranslationY(0.0f, 1.0f);
            this.weightChooserView.stopPanTransition();
        }
        if (this.keyboardVisible && this.waitingForKeyboardOpen) {
            this.waitingForKeyboardOpen = false;
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
        }
        onWindowSizeChanged();
    }

    public void onSwitchSegmentedAnimation(final PhotoView photoView) {
        float f;
        if (photoView == null) {
            return;
        }
        ThanosEffect thanosEffect = getThanosEffect();
        if (thanosEffect == null) {
            photoView.onSwitchSegmentedAnimationStarted(false);
            return;
        }
        Bitmap segmentedOutBitmap = photoView.getSegmentedOutBitmap();
        if (segmentedOutBitmap == null) {
            photoView.onSwitchSegmentedAnimationStarted(false);
            return;
        }
        Matrix matrix = new Matrix();
        float width = photoView.getWidth();
        float height = photoView.getHeight();
        float f2 = 0.0f;
        if (photoView.getRotation() != 0.0f) {
            float width2 = segmentedOutBitmap.getWidth();
            float height2 = segmentedOutBitmap.getHeight();
            float f3 = width2 / 2.0f;
            float f4 = height2 / 2.0f;
            float sqrt = (float) Math.sqrt((f3 * f3) + (f4 * f4));
            float f5 = sqrt * 2.0f;
            int i = (int) f5;
            Bitmap createBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(createBitmap);
            canvas.save();
            canvas.rotate(photoView.getRotation(), sqrt, sqrt);
            canvas.drawBitmap(segmentedOutBitmap, (f5 - width2) / 2.0f, (f5 - height2) / 2.0f, (Paint) null);
            segmentedOutBitmap.recycle();
            float f6 = width / 2.0f;
            float f7 = height / 2.0f;
            float sqrt2 = ((float) Math.sqrt((f6 * f6) + (f7 * f7))) * 2.0f;
            f2 = (-(sqrt2 - width)) / 2.0f;
            float f8 = (-(sqrt2 - height)) / 2.0f;
            height = sqrt2;
            f = f8;
            width = height;
            segmentedOutBitmap = createBitmap;
        } else {
            f = 0.0f;
        }
        matrix.postScale(width, height);
        matrix.postScale(photoView.getScaleX(), photoView.getScaleY(), width / 2.0f, height / 2.0f);
        matrix.postTranslate(photoView.getX() + f2, photoView.getY() + f);
        thanosEffect.animate(matrix, segmentedOutBitmap, new Runnable() {
            @Override
            public final void run() {
                PhotoView.this.onSwitchSegmentedAnimationStarted(true);
            }
        }, new Runnable() {
            @Override
            public final void run() {
                LPhotoPaintView.lambda$onSwitchSegmentedAnimation$50();
            }
        });
    }

    protected abstract void onTextAdd();

    @Override
    public void onTextAlignmentSelected(int i) {
        EntityView entityView = this.currentEntityView;
        if (entityView instanceof TextPaintView) {
            setTextAlignment((TextPaintView) entityView, i);
            PersistColorPalette.getInstance(this.currentAccount).setCurrentAlignment(i);
        }
    }

    @Override
    public void onTextOutlineSelected(View view) {
        setTextType((this.selectedTextType + 1) % 4);
    }

    public boolean onTouch(MotionEvent motionEvent) {
        if (this.currentEntityView != null) {
            selectEntity(null);
        }
        float x = ((motionEvent.getX() - this.renderView.getTranslationX()) - (getMeasuredWidth() / 2.0f)) / this.renderView.getScaleX();
        float y = ((((motionEvent.getY() - this.renderView.getTranslationY()) - (getMeasuredHeight() / 2.0f)) + AndroidUtilities.dp(32.0f)) - ((getAdditionalTop() - getAdditionalBottom()) / 2.0f)) / this.renderView.getScaleY();
        double d = x;
        double radians = (float) Math.toRadians(-this.renderView.getRotation());
        double cos = Math.cos(radians);
        Double.isNaN(d);
        double d2 = y;
        double sin = Math.sin(radians);
        Double.isNaN(d2);
        float measuredWidth = ((float) ((cos * d) - (sin * d2))) + (this.renderView.getMeasuredWidth() / 2.0f);
        double sin2 = Math.sin(radians);
        Double.isNaN(d);
        double cos2 = Math.cos(radians);
        Double.isNaN(d2);
        MotionEvent obtain = MotionEvent.obtain(motionEvent);
        obtain.setLocation(measuredWidth, ((float) ((d * sin2) + (d2 * cos2))) + (this.renderView.getMeasuredHeight() / 2.0f));
        this.renderView.onTouch(obtain);
        obtain.recycle();
        return true;
    }

    @Override
    public void onTypefaceButtonClicked() {
        showTypefaceMenu(true);
    }

    public void onTypefaceSelected(PaintTypeface paintTypeface) {
        PersistColorPalette.getInstance(this.currentAccount).setCurrentTypeface(paintTypeface.getKey());
        EntityView entityView = this.currentEntityView;
        if (entityView instanceof TextPaintView) {
            ((TextPaintView) entityView).setTypeface(paintTypeface);
        }
    }

    @Override
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    public void setDrawShadow(boolean z) {
        this.drawShadow = z;
        invalidate();
        setClipChildren(!z);
    }

    public void setOffsetTranslationX(float f) {
        if (this.tabsSelectedIndex == 0) {
            this.weightChooserView.setTranslationX(f);
        }
    }

    public void setOffsetTranslationY(float f, float f2, int i, boolean z) {
        this.offsetTranslationY = f;
        if (z) {
            setTranslationY(0.0f);
        } else {
            this.topLayout.setTranslationY(-f);
            this.bottomLayout.setTranslationY(f);
        }
    }

    public void setOnDoneButtonClickedListener(Runnable runnable) {
        this.onDoneButtonClickedListener = runnable;
    }

    public void setTransform(float f, float f2, float f3, float f4, float f5) {
        float f6;
        float f7;
        float f8;
        float f9;
        float f10;
        this.scale = f;
        this.imageWidth = f4;
        this.imageHeight = f5;
        this.inputTransformX = f2;
        this.inputTransformY = f3;
        this.transformX = f2;
        float f11 = f3 + this.panTranslationY;
        this.transformY = f11;
        int i = 0;
        while (i < 4) {
            View view = i == 0 ? this.entitiesView : i == 1 ? this.selectionContainerView : i == 2 ? this.renderView : this.renderInputView;
            MediaController.CropState cropState = this.currentCropState;
            if (cropState != null) {
                float f12 = cropState.cropScale * 1.0f;
                int measuredWidth = view.getMeasuredWidth();
                int measuredHeight = view.getMeasuredHeight();
                if (measuredWidth == 0 || measuredHeight == 0) {
                    return;
                }
                int i2 = this.currentCropState.transformRotation;
                if (i2 != 90 && i2 != 270) {
                    measuredHeight = measuredWidth;
                    measuredWidth = measuredHeight;
                }
                float max = Math.max(f4 / ((int) (r12.cropPw * r11)), f5 / ((int) (r12.cropPh * r10)));
                f7 = f12 * max;
                MediaController.CropState cropState2 = this.currentCropState;
                float f13 = cropState2.cropPx * measuredHeight * f * max;
                float f14 = cropState2.cropScale;
                f8 = (f13 * f14) + f2;
                float f15 = (cropState2.cropPy * measuredWidth * f * max * f14) + f11;
                f10 = cropState2.cropRotate + i2;
                f9 = f15;
                f6 = 1.0f;
            } else {
                if (i == 0) {
                    f6 = 1.0f;
                    f7 = this.baseScale * 1.0f;
                } else {
                    f6 = 1.0f;
                    f7 = 1.0f;
                }
                f8 = f2;
                f9 = f11;
                f10 = 0.0f;
            }
            float f16 = f9 + ((-this.emojiPadding) / 2.0f);
            float f17 = f7 * f;
            if (!Float.isNaN(f17)) {
                f6 = f17;
            }
            view.setScaleX(f6);
            view.setScaleY(f6);
            view.setTranslationX(f8);
            view.setTranslationY(f16);
            view.setRotation(f10);
            view.invalidate();
            i++;
        }
        updateEntitiesSelections();
        invalidate();
    }

    public void shutdown() {
        this.renderView.shutdown();
        this.entitiesView.setVisibility(8);
        this.selectionContainerView.setVisibility(8);
        this.queue.postRunnable(new Runnable() {
            @Override
            public final void run() {
                LPhotoPaintView.lambda$shutdown$25();
            }
        });
    }

    public void translateY(float f) {
        if (Math.abs(f - this.pany) > 0.1f) {
            this.pany = f;
            setTransform(this.scale, this.inputTransformX, this.inputTransformY, this.imageWidth, this.imageHeight);
        }
    }

    public void updateColors() {
        this.toolsPaint.setColor(-15132391);
    }

    public void updateEntitiesSelections() {
        for (int i = 0; i < this.entitiesView.getChildCount(); i++) {
            View childAt = this.entitiesView.getChildAt(i);
            if (childAt == this.currentEntityView || ((childAt instanceof EntityView) && ((EntityView) childAt).isSelectedProgress())) {
                ((EntityView) childAt).updateSelectionView();
            }
        }
    }

    protected abstract void updateKeyboard();

    public void updatePlusEmojiKeyboardButton() {
        PaintTextOptionsView paintTextOptionsView = this.textOptionsView;
        if (paintTextOptionsView != null) {
            paintTextOptionsView.animatePlusToIcon(this.keyboardVisible ? R.drawable.input_smile : this.emojiViewVisible ? R.drawable.input_keyboard : R.drawable.msg_add);
        }
        AndroidUtilities.updateViewShow(this.undoAllButton, (this.keyboardVisible || this.emojiViewVisible) ? false : true, false, 1.0f, true, null);
        AndroidUtilities.updateViewShow(this.undoButton, (this.keyboardVisible || this.emojiViewVisible) ? false : true, false, 1.0f, true, null);
        AndroidUtilities.updateViewShow(this.doneTextButton, this.keyboardVisible || this.emojiViewVisible, false, 1.0f, true, null);
        AndroidUtilities.updateViewShow(this.cancelTextButton, this.keyboardVisible || this.emojiViewVisible, false, 1.0f, true, null);
    }

    public void updateZoom(boolean z) {
        boolean z2 = !z;
        if (this.zoomOutVisible != z2) {
            this.zoomOutVisible = z2;
            this.zoomOutButton.animate().cancel();
            this.zoomOutButton.animate().alpha(z ? 0.0f : 1.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(240L).start();
        }
    }
}
