package org.telegram.ui.Components;

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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.NestedScrollingParent;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarConstructorFragment;
import org.telegram.ui.Components.ColorPicker;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.SelectAnimatedEmojiDialog;

public class AvatarConstructorFragment extends BaseFragment {
    public static final int[][] defaultColors = {new int[]{-11302949, -11562789, -10430789, -11480359}, new int[]{-11229725, -12014137, -10234219, -10819908}, new int[]{-12927610, -11158198, -3355566, -5191850}, new int[]{-8164117, -5281560, -2200166, -2525971}, new int[]{-1287263, -1350281, -1337532, -885148}, new int[]{-1419145, -1936819, -742839, -1014448}, new int[]{-1017772, -1212871, -998847, -1003446}};
    CanvasButton avatarClickableArea;
    final ImageUpdater.AvatarFor avatarFor;
    private BackgroundSelectView backgroundSelectView;
    private BottomSheet bottomSheet;
    private FrameLayout button;
    private TextView chooseBackgroundHint;
    private TextView chooseEmojiHint;
    int collapsedHeight;
    BackgroundGradient colorPickerGradient;
    boolean colorPickerInAnimatoin;
    View colorPickerPreviewView;
    Delegate delegate;
    boolean drawForBlur;
    ValueAnimator expandAnimator;
    boolean expandWithKeyboard;
    int expandedHeight;
    boolean forGroup;
    private int gradientBackgroundItemWidth;
    ImageUpdater imageUpdater;
    boolean isLandscapeMode;
    ValueAnimator keyboardVisibilityAnimator;
    boolean keyboardVisible;
    float keyboardVisibleProgress;
    ValueAnimator lightProgressAnimator;
    LinearLayout linearLayout;
    protected ActionBar overlayActionBar;
    PreviewView previewView;
    float progressToExpand;
    private SelectAnimatedEmojiDialog selectAnimatedEmojiDialog;
    private ActionBarMenuItem setPhotoItem;
    boolean wasChanged;
    Paint actionBarPaint = new Paint();
    public boolean finishOnDone = true;
    boolean isLightInternal = false;
    float progressToLightStatusBar = 0.0f;

    public static class BackgroundGradient {
        int color1;
        int color2;
        int color3;
        int color4;
        public int stableId;

        public int colorsCount() {
            if (this.color4 != 0) {
                return 4;
            }
            if (this.color3 != 0) {
                return 3;
            }
            return this.color2 != 0 ? 2 : 1;
        }

        public BackgroundGradient copy() {
            BackgroundGradient backgroundGradient = new BackgroundGradient();
            backgroundGradient.color1 = this.color1;
            backgroundGradient.color2 = this.color2;
            backgroundGradient.color3 = this.color3;
            backgroundGradient.color4 = this.color4;
            return backgroundGradient;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof BackgroundGradient)) {
                return false;
            }
            BackgroundGradient backgroundGradient = (BackgroundGradient) obj;
            return this.color1 == backgroundGradient.color1 && this.color2 == backgroundGradient.color2 && this.color3 == backgroundGradient.color3 && this.color4 == backgroundGradient.color4;
        }

        public int getAverageColor() {
            int i = this.color1;
            int i2 = this.color2;
            if (i2 != 0) {
                i = ColorUtils.blendARGB(i, i2, 0.5f);
            }
            int i3 = this.color3;
            if (i3 != 0) {
                i = ColorUtils.blendARGB(i, i3, 0.5f);
            }
            int i4 = this.color4;
            return i4 != 0 ? ColorUtils.blendARGB(i, i4, 0.5f) : i;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.stableId), Integer.valueOf(this.color1), Integer.valueOf(this.color2), Integer.valueOf(this.color3), Integer.valueOf(this.color4));
        }
    }

    public class BackgroundSelectView extends RecyclerListView {
        RecyclerView.Adapter adapter;
        BackgroundGradient customSelectedGradient;
        ArrayList gradients;
        int selectedItemId;
        int stableIdPointer;

        public BackgroundSelectView(Context context) {
            super(context);
            this.gradients = new ArrayList();
            this.stableIdPointer = 200;
            this.selectedItemId = -1;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(0);
            setLayoutManager(linearLayoutManager);
            int i = 0;
            while (true) {
                int[][] iArr = AvatarConstructorFragment.defaultColors;
                if (i >= iArr.length) {
                    this.useLayoutPositionOnClick = true;
                    setOnItemClickListener(new RecyclerListView.OnItemClickListener() {
                        @Override
                        public final void onItemClick(View view, int i2) {
                            AvatarConstructorFragment.BackgroundSelectView.this.lambda$new$0(view, i2);
                        }
                    });
                    RecyclerView.Adapter adapter = new RecyclerView.Adapter() {
                        @Override
                        public int getItemCount() {
                            return BackgroundSelectView.this.gradients.size() + 1;
                        }

                        @Override
                        public long getItemId(int i2) {
                            if (i2 >= BackgroundSelectView.this.gradients.size()) {
                                return 1L;
                            }
                            return ((BackgroundGradient) BackgroundSelectView.this.gradients.get(i2)).stableId;
                        }

                        @Override
                        public int getItemViewType(int i2) {
                            return i2 >= BackgroundSelectView.this.gradients.size() ? 1 : 0;
                        }

                        @Override
                        public void onBindViewHolder(androidx.recyclerview.widget.RecyclerView.ViewHolder r4, int r5) {
                            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.AvatarConstructorFragment.BackgroundSelectView.AnonymousClass1.onBindViewHolder(androidx.recyclerview.widget.RecyclerView$ViewHolder, int):void");
                        }

                        @Override
                        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i2) {
                            BackgroundSelectView backgroundSelectView = BackgroundSelectView.this;
                            return new RecyclerListView.Holder(new GradientSelectorView(backgroundSelectView.getContext()));
                        }
                    };
                    this.adapter = adapter;
                    setAdapter(adapter);
                    setOverScrollMode(1);
                    return;
                }
                BackgroundGradient backgroundGradient = new BackgroundGradient();
                int i2 = this.stableIdPointer;
                this.stableIdPointer = i2 + 1;
                backgroundGradient.stableId = i2;
                int[] iArr2 = iArr[i];
                backgroundGradient.color1 = iArr2[0];
                backgroundGradient.color2 = iArr2[1];
                backgroundGradient.color3 = iArr2[2];
                backgroundGradient.color4 = iArr2[3];
                this.gradients.add(backgroundGradient);
                i++;
            }
        }

        public void lambda$new$0(View view, int i) {
            BackgroundGradient backgroundGradient;
            RecyclerView.Adapter adapter;
            if (view instanceof GradientSelectorView) {
                GradientSelectorView gradientSelectorView = (GradientSelectorView) view;
                if (!gradientSelectorView.isCustom) {
                    BackgroundGradient backgroundGradient2 = gradientSelectorView.backgroundGradient;
                    this.selectedItemId = backgroundGradient2.stableId;
                    AvatarConstructorFragment.this.previewView.setGradient(backgroundGradient2);
                    adapter = this.adapter;
                    if (adapter == null) {
                        return;
                    }
                    adapter.notifyDataSetChanged();
                }
            }
            if (this.selectedItemId == 1 || (backgroundGradient = this.customSelectedGradient) == null) {
                AvatarConstructorFragment.this.showColorPicker();
                return;
            }
            this.selectedItemId = 1;
            AvatarConstructorFragment.this.previewView.setGradient(backgroundGradient);
            adapter = this.adapter;
            if (adapter == null) {
                return;
            }
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onMeasure(int i, int i2) {
            AvatarConstructorFragment avatarConstructorFragment;
            AvatarConstructorFragment.this.gradientBackgroundItemWidth = View.MeasureSpec.getSize(i) / this.adapter.getItemCount();
            float f = 36.0f;
            if (AvatarConstructorFragment.this.gradientBackgroundItemWidth >= AndroidUtilities.dp(36.0f)) {
                if (AvatarConstructorFragment.this.gradientBackgroundItemWidth > AndroidUtilities.dp(150.0f)) {
                    avatarConstructorFragment = AvatarConstructorFragment.this;
                    f = 48.0f;
                }
                super.onMeasure(i, i2);
            }
            avatarConstructorFragment = AvatarConstructorFragment.this;
            avatarConstructorFragment.gradientBackgroundItemWidth = AndroidUtilities.dp(f);
            super.onMeasure(i, i2);
        }

        public void selectGradient(BackgroundGradient backgroundGradient) {
            int i;
            int i2 = 0;
            while (true) {
                if (i2 >= this.gradients.size()) {
                    this.customSelectedGradient = backgroundGradient;
                    i = 1;
                    break;
                } else {
                    if (((BackgroundGradient) this.gradients.get(i2)).equals(backgroundGradient)) {
                        i = ((BackgroundGradient) this.gradients.get(i2)).stableId;
                        break;
                    }
                    i2++;
                }
            }
            this.selectedItemId = i;
            this.adapter.notifyDataSetChanged();
        }
    }

    private class ContainerLayout extends SizeNotifierFrameLayout implements NestedScrollingParent {
        private NestedScrollingParentHelper nestedScrollingParentHelper;

        public ContainerLayout(Context context) {
            super(context);
            this.nestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        }

        @Override
        public int getNestedScrollAxes() {
            return this.nestedScrollingParentHelper.getNestedScrollAxes();
        }

        @Override
        public boolean onNestedFling(View view, float f, float f2, boolean z) {
            return false;
        }

        @Override
        public boolean onNestedPreFling(View view, float f, float f2) {
            return false;
        }

        @Override
        public void onNestedPreScroll(View view, int i, int i2, int[] iArr) {
            AvatarConstructorFragment avatarConstructorFragment = AvatarConstructorFragment.this;
            if (avatarConstructorFragment.keyboardVisibleProgress > 0.0f || avatarConstructorFragment.isLandscapeMode || i2 <= 0 || avatarConstructorFragment.progressToExpand <= 0.0f) {
                return;
            }
            avatarConstructorFragment.cancelExpandAnimator();
            AvatarConstructorFragment.this.setProgressToExpand(Utilities.clamp(AvatarConstructorFragment.this.progressToExpand - (i2 / r3.expandedHeight), 1.0f, 0.0f), true);
            iArr[1] = i2;
        }

        @Override
        public void onNestedScroll(View view, int i, int i2, int i3, int i4) {
            AvatarConstructorFragment avatarConstructorFragment = AvatarConstructorFragment.this;
            if (avatarConstructorFragment.keyboardVisibleProgress > 0.0f || avatarConstructorFragment.isLandscapeMode || i4 == 0) {
                return;
            }
            avatarConstructorFragment.cancelExpandAnimator();
            AvatarConstructorFragment.this.setProgressToExpand(Utilities.clamp(AvatarConstructorFragment.this.progressToExpand - (i4 / r1.expandedHeight), 1.0f, 0.0f), true);
        }

        @Override
        public void onNestedScrollAccepted(View view, View view2, int i) {
            this.nestedScrollingParentHelper.onNestedScrollAccepted(view, view2, i);
            AvatarConstructorFragment.this.cancelExpandAnimator();
        }

        @Override
        public boolean onStartNestedScroll(View view, View view2, int i) {
            AvatarConstructorFragment avatarConstructorFragment = AvatarConstructorFragment.this;
            return avatarConstructorFragment.keyboardVisibleProgress <= 0.0f && !avatarConstructorFragment.isLandscapeMode;
        }

        @Override
        public void onStopNestedScroll(View view) {
            this.nestedScrollingParentHelper.onStopNestedScroll(view);
            AvatarConstructorFragment avatarConstructorFragment = AvatarConstructorFragment.this;
            avatarConstructorFragment.setExpanded(avatarConstructorFragment.progressToExpand > 0.5f, false, false);
        }
    }

    public interface Delegate {
        void onDone(BackgroundGradient backgroundGradient, long j, TLRPC.Document document, PreviewView previewView);
    }

    public class GradientSelectorView extends View {
        Drawable addIcon;
        BackgroundGradient backgroundGradient;
        Paint defaultPaint;
        GradientTools gradientTools;
        boolean isCustom;
        Paint optionsPaint;
        AnimatedFloat progressToSelect;
        boolean selected;

        public GradientSelectorView(Context context) {
            super(context);
            this.progressToSelect = new AnimatedFloat(400L, AndroidUtilities.overshootInterpolator);
            this.gradientTools = new GradientTools();
            this.progressToSelect.setParent(this);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            Paint paint;
            float dp;
            super.onDraw(canvas);
            this.progressToSelect.set(this.selected ? 1.0f : 0.0f, false);
            float measuredWidth = getMeasuredWidth() / 2.0f;
            float measuredHeight = getMeasuredHeight() / 2.0f;
            BackgroundGradient backgroundGradient = this.backgroundGradient;
            if (backgroundGradient != null) {
                this.gradientTools.setColors(backgroundGradient.color1, backgroundGradient.color2, backgroundGradient.color3, backgroundGradient.color4);
                this.gradientTools.setBounds(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                paint = this.gradientTools.paint;
            } else {
                if (this.defaultPaint == null) {
                    Paint paint2 = new Paint(1);
                    this.defaultPaint = paint2;
                    paint2.setColor(Theme.getColor(Theme.key_chat_emojiPanelBackground));
                }
                paint = this.defaultPaint;
            }
            if (this.progressToSelect.get() == 0.0f) {
                dp = AndroidUtilities.dp(15.0f);
            } else {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
                canvas.drawCircle(measuredWidth, measuredHeight, AndroidUtilities.dpf2(13.5f), paint);
                paint.setStyle(Paint.Style.FILL);
                dp = AndroidUtilities.dp(10.0f) + (AndroidUtilities.dp(5.0f) * (1.0f - this.progressToSelect.get()));
            }
            canvas.drawCircle(measuredWidth, measuredHeight, dp, paint);
            if (this.isCustom) {
                if (this.backgroundGradient == null) {
                    if (this.addIcon == null) {
                        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.msg_filled_plus);
                        this.addIcon = drawable;
                        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chat_emojiSearchIcon), PorterDuff.Mode.MULTIPLY));
                    }
                    this.addIcon.setBounds((int) (measuredWidth - (r2.getIntrinsicWidth() / 2.0f)), (int) (measuredHeight - (this.addIcon.getIntrinsicHeight() / 2.0f)), (int) (measuredWidth + (this.addIcon.getIntrinsicWidth() / 2.0f)), (int) (measuredHeight + (this.addIcon.getIntrinsicHeight() / 2.0f)));
                    this.addIcon.draw(canvas);
                    return;
                }
                if (this.optionsPaint == null) {
                    Paint paint3 = new Paint(1);
                    this.optionsPaint = paint3;
                    paint3.setColor(-1);
                }
                this.optionsPaint.setAlpha(Math.round(Utilities.clamp(this.progressToSelect.get(), 1.0f, 0.0f) * 255.0f));
                canvas.drawCircle(measuredWidth, measuredHeight, AndroidUtilities.dp(1.5f), this.optionsPaint);
                canvas.drawCircle(measuredWidth - (AndroidUtilities.dp(5.0f) * this.progressToSelect.get()), measuredHeight, AndroidUtilities.dp(1.5f), this.optionsPaint);
                canvas.drawCircle(measuredWidth + (AndroidUtilities.dp(5.0f) * this.progressToSelect.get()), measuredHeight, AndroidUtilities.dp(1.5f), this.optionsPaint);
            }
        }

        @Override
        protected void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AvatarConstructorFragment.this.gradientBackgroundItemWidth, 1073741824), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(48.0f), 1073741824));
        }

        public void setCustom(boolean z) {
            this.isCustom = z;
        }

        void setGradient(BackgroundGradient backgroundGradient) {
            this.backgroundGradient = backgroundGradient;
        }

        void setSelectedInternal(boolean z, boolean z2) {
            if (this.selected != z) {
                this.selected = z;
                invalidate();
            }
            if (z2) {
                return;
            }
            this.progressToSelect.set(z ? 1.0f : 0.0f, false);
        }
    }

    public class PreviewView extends FrameLayout {
        BackgroundGradient backgroundGradient;
        BackupImageView backupImageView;
        float changeBackgroundProgress;
        private ColorFilter colorFilter;
        private float cx;
        private float cy;
        public TLRPC.Document document;
        public long documentId;
        AnimatedFloat expandProgress;
        boolean expanded;
        GradientTools gradientTools;
        GradientTools outGradientTools;
        float overrideExpandProgress;
        private float size;

        public PreviewView(Context context) {
            super(context);
            this.gradientTools = new GradientTools();
            this.outGradientTools = new GradientTools();
            this.changeBackgroundProgress = 1.0f;
            this.colorFilter = new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN);
            this.expandProgress = new AnimatedFloat(this, 200L, CubicBezierInterpolator.EASE_OUT);
            this.overrideExpandProgress = -1.0f;
            BackupImageView backupImageView = new BackupImageView(context) {
                @Override
                public void invalidate() {
                    super.invalidate();
                    PreviewView.this.invalidate();
                }

                @Override
                public void invalidate(int i, int i2, int i3, int i4) {
                    super.invalidate(i, i2, i3, i4);
                    PreviewView.this.invalidate();
                }

                @Override
                public void invalidate(android.graphics.Rect rect) {
                    super.invalidate(rect);
                    PreviewView.this.invalidate();
                }
            };
            this.backupImageView = backupImageView;
            backupImageView.getImageReceiver().setAutoRepeatCount(1);
            this.backupImageView.getImageReceiver().setAspectFit(true);
            setClipChildren(false);
            addView(this.backupImageView, LayoutHelper.createFrame(70, 70, 17));
        }

        private void drawBackround(Canvas canvas, float f, float f2, float f3, float f4, Paint paint) {
            float f5 = this.expandProgress.get();
            if (f5 == 0.0f) {
                canvas.drawCircle(f, f2, f4, paint);
                return;
            }
            float lerp = AndroidUtilities.lerp(f3, 0.0f, f5);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(f - f4, f2 - f4, f + f4, f2 + f4);
            canvas.drawRoundRect(rectF, lerp, lerp, paint);
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            this.cx = getMeasuredWidth() / 2.0f;
            this.cy = getMeasuredHeight() / 2.0f;
            float measuredWidth = AvatarConstructorFragment.this.isLandscapeMode ? getMeasuredWidth() * 0.3f : AndroidUtilities.dp(50.0f);
            this.expandProgress.set(this.expanded ? 1.0f : 0.0f);
            float f = this.overrideExpandProgress;
            if (f >= 0.0f) {
                this.expandProgress.set(f, true);
            }
            float lerp = AndroidUtilities.lerp(measuredWidth, getMeasuredWidth() / 2.0f, this.expandProgress.get());
            this.size = lerp;
            this.size = AndroidUtilities.lerp(lerp, AndroidUtilities.dp(21.0f), AvatarConstructorFragment.this.keyboardVisibleProgress);
            this.cx = AndroidUtilities.lerp(this.cx, (getMeasuredWidth() - AndroidUtilities.dp(12.0f)) - AndroidUtilities.dp(21.0f), AvatarConstructorFragment.this.keyboardVisibleProgress);
            canvas.save();
            AvatarConstructorFragment avatarConstructorFragment = AvatarConstructorFragment.this;
            canvas.clipRect(0.0f, (-r3) / 2.0f, getMeasuredWidth(), getMeasuredHeight() + (((avatarConstructorFragment.expandedHeight - avatarConstructorFragment.collapsedHeight) / 2.0f) * AvatarConstructorFragment.this.progressToExpand));
            BackgroundGradient backgroundGradient = this.backgroundGradient;
            if (backgroundGradient != null) {
                this.gradientTools.setColors(backgroundGradient.color1, backgroundGradient.color2, backgroundGradient.color3, backgroundGradient.color4);
                GradientTools gradientTools = this.gradientTools;
                float f2 = this.cx;
                float f3 = this.size;
                float f4 = this.cy;
                gradientTools.setBounds(f2 - f3, f4 - f3, f2 + f3, f4 + f3);
                if (this.changeBackgroundProgress != 1.0f) {
                    GradientTools gradientTools2 = this.outGradientTools;
                    float f5 = this.cx;
                    float f6 = this.size;
                    float f7 = this.cy;
                    gradientTools2.setBounds(f5 - f6, f7 - f6, f5 + f6, f7 + f6);
                    this.outGradientTools.paint.setAlpha(255);
                    float f8 = measuredWidth;
                    drawBackround(canvas, this.cx, this.cy, f8, this.size, this.outGradientTools.paint);
                    this.gradientTools.paint.setAlpha((int) (this.changeBackgroundProgress * 255.0f));
                    drawBackround(canvas, this.cx, this.cy, f8, this.size, this.gradientTools.paint);
                    float f9 = this.changeBackgroundProgress + 0.064f;
                    this.changeBackgroundProgress = f9;
                    if (f9 > 1.0f) {
                        this.changeBackgroundProgress = 1.0f;
                    }
                    invalidate();
                } else {
                    this.gradientTools.paint.setAlpha(255);
                    drawBackround(canvas, this.cx, this.cy, measuredWidth, this.size, this.gradientTools.paint);
                }
            }
            float lerp2 = AndroidUtilities.lerp(AndroidUtilities.lerp(AvatarConstructorFragment.this.isLandscapeMode ? (int) ((measuredWidth * 2.0f) * 0.7f) : AndroidUtilities.dp(70.0f), (int) (getMeasuredWidth() * 0.7f), this.expandProgress.get()), (int) (AndroidUtilities.dp(42.0f) * 0.7f), AvatarConstructorFragment.this.keyboardVisibleProgress) / 2.0f;
            BackupImageView backupImageView = this.backupImageView;
            AnimatedEmojiDrawable animatedEmojiDrawable = backupImageView.animatedEmojiDrawable;
            if (animatedEmojiDrawable == null) {
                ImageReceiver imageReceiver = backupImageView.imageReceiver;
                float f10 = this.cx - lerp2;
                float f11 = this.cy - lerp2;
                float f12 = lerp2 * 2.0f;
                imageReceiver.setImageCoords(f10, f11, f12, f12);
                this.backupImageView.imageReceiver.setRoundRadius((int) (f12 * 0.13f));
                this.backupImageView.imageReceiver.draw(canvas);
                return;
            }
            if (animatedEmojiDrawable.getImageReceiver() != null) {
                this.backupImageView.animatedEmojiDrawable.getImageReceiver().setRoundRadius((int) (2.0f * lerp2 * 0.13f));
            }
            AnimatedEmojiDrawable animatedEmojiDrawable2 = this.backupImageView.animatedEmojiDrawable;
            float f13 = this.cx;
            float f14 = this.cy;
            animatedEmojiDrawable2.setBounds((int) (f13 - lerp2), (int) (f14 - lerp2), (int) (f13 + lerp2), (int) (f14 + lerp2));
            this.backupImageView.animatedEmojiDrawable.setColorFilter(this.colorFilter);
            this.backupImageView.animatedEmojiDrawable.draw(canvas);
        }

        public long getDuration() {
            ImageReceiver imageReceiver = this.backupImageView.getImageReceiver();
            AnimatedEmojiDrawable animatedEmojiDrawable = this.backupImageView.animatedEmojiDrawable;
            if (animatedEmojiDrawable != null) {
                imageReceiver = animatedEmojiDrawable.getImageReceiver();
            }
            if (imageReceiver == null || imageReceiver.getLottieAnimation() == null) {
                return 5000L;
            }
            return imageReceiver.getLottieAnimation().getDuration();
        }

        public ImageReceiver getImageReceiver() {
            ImageReceiver imageReceiver = this.backupImageView.getImageReceiver();
            AnimatedEmojiDrawable animatedEmojiDrawable = this.backupImageView.animatedEmojiDrawable;
            if (animatedEmojiDrawable == null) {
                return imageReceiver;
            }
            ImageReceiver imageReceiver2 = animatedEmojiDrawable.getImageReceiver();
            this.backupImageView.animatedEmojiDrawable.setColorFilter(this.colorFilter);
            return imageReceiver2;
        }

        public boolean hasAnimation() {
            return (getImageReceiver().getAnimation() == null && getImageReceiver().getLottieAnimation() == null) ? false : true;
        }

        @Override
        public void invalidate() {
            super.invalidate();
            AvatarConstructorFragment.this.fragmentView.invalidate();
        }

        @Override
        protected void onMeasure(int i, int i2) {
            if (!AvatarConstructorFragment.this.isLandscapeMode) {
                i2 = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(140.0f), 1073741824);
            }
            super.onMeasure(i, i2);
        }

        public void setExpanded(boolean z) {
            if (this.expanded == z) {
                return;
            }
            this.expanded = z;
            if (z) {
                AnimatedEmojiDrawable animatedEmojiDrawable = this.backupImageView.animatedEmojiDrawable;
                if (animatedEmojiDrawable != null && animatedEmojiDrawable.getImageReceiver() != null) {
                    this.backupImageView.animatedEmojiDrawable.getImageReceiver().startAnimation();
                }
                this.backupImageView.imageReceiver.startAnimation();
            }
            if (Build.VERSION.SDK_INT >= 23) {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, new Object[0]);
            }
            invalidate();
        }

        public void setGradient(BackgroundGradient backgroundGradient) {
            BackgroundGradient backgroundGradient2 = this.backgroundGradient;
            if (backgroundGradient2 != null) {
                this.outGradientTools.setColors(backgroundGradient2.color1, backgroundGradient2.color2, backgroundGradient2.color3, backgroundGradient2.color4);
                this.changeBackgroundProgress = 0.0f;
                AvatarConstructorFragment.this.wasChanged = true;
            }
            this.backgroundGradient = backgroundGradient;
            if (Build.VERSION.SDK_INT >= 23) {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, new Object[0]);
            }
            invalidate();
        }
    }

    public AvatarConstructorFragment(ImageUpdater imageUpdater, ImageUpdater.AvatarFor avatarFor) {
        this.imageUpdater = imageUpdater;
        this.avatarFor = avatarFor;
    }

    public void createKeyboardVisibleAnimator(final boolean z) {
        if (this.isLandscapeMode) {
            return;
        }
        final float f = 0.0f;
        this.keyboardVisibilityAnimator = ValueAnimator.ofFloat(this.keyboardVisibleProgress, z ? 1.0f : 0.0f);
        final float f2 = ((this.expandedHeight - this.collapsedHeight) - AndroidUtilities.statusBarHeight) * this.progressToExpand;
        if (z) {
            this.previewView.setExpanded(false);
            f2 = this.linearLayout.getTranslationY();
        } else {
            f = this.linearLayout.getTranslationY();
        }
        if (!this.expandWithKeyboard || z) {
            this.expandWithKeyboard = false;
        } else {
            this.previewView.setExpanded(true);
        }
        this.keyboardVisibilityAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                AvatarConstructorFragment.this.keyboardVisibleProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                float lerp = AndroidUtilities.lerp(f2, f, AvatarConstructorFragment.this.keyboardVisibleProgress);
                ((BaseFragment) AvatarConstructorFragment.this).actionBar.getTitleTextView().setAlpha(AvatarConstructorFragment.this.keyboardVisibleProgress);
                AvatarConstructorFragment avatarConstructorFragment = AvatarConstructorFragment.this;
                if (avatarConstructorFragment.expandWithKeyboard && !z) {
                    avatarConstructorFragment.setProgressToExpand(1.0f - avatarConstructorFragment.keyboardVisibleProgress, false);
                }
                AvatarConstructorFragment.this.linearLayout.setTranslationY(lerp);
                AvatarConstructorFragment.this.button.setTranslationY(lerp);
                AvatarConstructorFragment.this.fragmentView.invalidate();
                ((BaseFragment) AvatarConstructorFragment.this).actionBar.invalidate();
            }
        });
        this.keyboardVisibilityAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                AvatarConstructorFragment avatarConstructorFragment = AvatarConstructorFragment.this;
                avatarConstructorFragment.setProgressToExpand(avatarConstructorFragment.expandWithKeyboard ? 1.0f : 0.0f, false);
                AvatarConstructorFragment.this.expandWithKeyboard = false;
            }
        });
        this.keyboardVisibilityAnimator.setDuration(250L);
        this.keyboardVisibilityAnimator.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
        this.keyboardVisibilityAnimator.start();
    }

    public void discardEditor() {
        if (getParentActivity() == null) {
            return;
        }
        if (!this.wasChanged) {
            lambda$onBackPressed$323();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.getString(R.string.PhotoEditorDiscardAlert));
        builder.setTitle(LocaleController.getString(R.string.DiscardChanges));
        builder.setPositiveButton(LocaleController.getString(R.string.PassportDiscard), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog, int i) {
                AvatarConstructorFragment.this.lambda$discardEditor$2(alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        AlertDialog create = builder.create();
        showDialog(create);
        create.redPositive();
    }

    public void lambda$createView$0(View view) {
        onDonePressed();
    }

    public void lambda$discardEditor$2(AlertDialog alertDialog, int i) {
        lambda$onBackPressed$323();
    }

    public void lambda$isLightStatusBar$6(ValueAnimator valueAnimator) {
        setProgressToLightStatusBar(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void lambda$setExpanded$3(boolean z, ValueAnimator valueAnimator) {
        float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        setProgressToExpand(floatValue, false);
        if (z) {
            PreviewView previewView = this.previewView;
            previewView.overrideExpandProgress = floatValue;
            previewView.invalidate();
        }
    }

    public void lambda$showColorPicker$4(int i, int i2, boolean z) {
        if (i2 == 0) {
            BackgroundGradient backgroundGradient = this.colorPickerGradient;
            int i3 = backgroundGradient.color1;
            if (i3 != i && (i3 == 0 || i == 0)) {
                BackgroundGradient copy = backgroundGradient.copy();
                this.colorPickerGradient = copy;
                this.previewView.setGradient(copy);
            }
            this.colorPickerGradient.color1 = i;
        } else if (i2 == 1) {
            BackgroundGradient backgroundGradient2 = this.colorPickerGradient;
            int i4 = backgroundGradient2.color2;
            if (i4 != i && (i4 == 0 || i == 0)) {
                BackgroundGradient copy2 = backgroundGradient2.copy();
                this.colorPickerGradient = copy2;
                this.previewView.setGradient(copy2);
            }
            this.colorPickerGradient.color2 = i;
        } else if (i2 == 2) {
            BackgroundGradient backgroundGradient3 = this.colorPickerGradient;
            int i5 = backgroundGradient3.color3;
            if (i5 != i && (i5 == 0 || i == 0)) {
                BackgroundGradient copy3 = backgroundGradient3.copy();
                this.colorPickerGradient = copy3;
                this.previewView.setGradient(copy3);
            }
            this.colorPickerGradient.color3 = i;
        } else if (i2 == 3) {
            BackgroundGradient backgroundGradient4 = this.colorPickerGradient;
            int i6 = backgroundGradient4.color4;
            if (i6 != i && (i6 == 0 || i == 0)) {
                BackgroundGradient copy4 = backgroundGradient4.copy();
                this.colorPickerGradient = copy4;
                this.previewView.setGradient(copy4);
            }
            this.colorPickerGradient.color4 = i;
        }
        if (Build.VERSION.SDK_INT >= 23) {
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, new Object[0]);
        }
        this.previewView.invalidate();
    }

    public void lambda$showColorPicker$5(boolean[] zArr, View view) {
        zArr[0] = true;
        this.backgroundSelectView.selectGradient(this.colorPickerGradient);
        this.bottomSheet.lambda$new$0();
    }

    public void onDonePressed() {
        if (this.previewView.getImageReceiver() == null || !this.previewView.getImageReceiver().hasImageLoaded()) {
            return;
        }
        Delegate delegate = this.delegate;
        if (delegate != null) {
            PreviewView previewView = this.previewView;
            delegate.onDone(previewView.backgroundGradient, previewView.documentId, previewView.document, previewView);
        }
        if (this.finishOnDone) {
            lambda$onBackPressed$323();
        }
    }

    public void setExpanded(final boolean z, final boolean z2, boolean z3) {
        if (this.isLandscapeMode) {
            return;
        }
        cancelExpandAnimator();
        this.expandAnimator = ValueAnimator.ofFloat(this.progressToExpand, z ? 1.0f : 0.0f);
        if (z2) {
            this.previewView.overrideExpandProgress = this.progressToExpand;
            if (Build.VERSION.SDK_INT >= 23) {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, new Object[0]);
            }
        }
        this.expandAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                AvatarConstructorFragment.this.lambda$setExpanded$3(z2, valueAnimator);
            }
        });
        this.expandAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                AvatarConstructorFragment avatarConstructorFragment = AvatarConstructorFragment.this;
                avatarConstructorFragment.expandAnimator = null;
                avatarConstructorFragment.setProgressToExpand(z ? 1.0f : 0.0f, false);
                if (z2) {
                    PreviewView previewView = AvatarConstructorFragment.this.previewView;
                    previewView.overrideExpandProgress = -1.0f;
                    previewView.setExpanded(z);
                }
            }
        });
        ValueAnimator valueAnimator = this.expandAnimator;
        if (z3) {
            valueAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.expandAnimator.setDuration(350L);
            this.expandAnimator.setStartDelay(150L);
        } else {
            valueAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.expandAnimator.setDuration(250L);
        }
        this.expandAnimator.start();
    }

    public void setPreview(long j, TLRPC.Document document) {
        PreviewView previewView = this.previewView;
        previewView.documentId = j;
        previewView.document = document;
        if (j == 0) {
            previewView.backupImageView.setAnimatedEmojiDrawable(null);
            this.previewView.backupImageView.getImageReceiver().setImage(ImageLocation.getForDocument(document), "100_100", null, null, DocumentObject.getSvgThumb(document, Theme.key_windowBackgroundWhiteGrayIcon, 0.2f), 0L, "tgs", document, 0);
        } else {
            previewView.backupImageView.setAnimatedEmojiDrawable(new AnimatedEmojiDrawable(14, this.currentAccount, j));
            this.previewView.backupImageView.getImageReceiver().clearImage();
        }
        if (this.previewView.getImageReceiver() != null && this.previewView.getImageReceiver().getAnimation() != null) {
            this.previewView.getImageReceiver().getAnimation().seekTo(0L, true);
        }
        if (this.previewView.getImageReceiver() != null && this.previewView.getImageReceiver().getLottieAnimation() != null) {
            this.previewView.getImageReceiver().getLottieAnimation().setCurrentFrame(0, false, true);
        }
        this.wasChanged = true;
    }

    public void setProgressToExpand(float f, boolean z) {
        this.progressToExpand = f;
        float f2 = ((this.expandedHeight - this.collapsedHeight) - AndroidUtilities.statusBarHeight) * f;
        if (this.keyboardVisibleProgress == 0.0f) {
            this.linearLayout.setTranslationY(f2);
            this.button.setTranslationY(f2);
        }
        this.previewView.setTranslationY(((-(this.expandedHeight - this.collapsedHeight)) / 2.0f) * f);
        this.fragmentView.invalidate();
        if (z) {
            this.previewView.setExpanded(f > 0.5f);
        }
    }

    private void setProgressToLightStatusBar(float f) {
        if (this.progressToLightStatusBar != f) {
            this.progressToLightStatusBar = f;
            int blendARGB = ColorUtils.blendARGB(-16777216, -1, f);
            int alphaComponent = ColorUtils.setAlphaComponent(blendARGB, 60);
            this.overlayActionBar.setItemsColor(blendARGB, false);
            this.setPhotoItem.setBackground(Theme.createSelectorDrawable(alphaComponent, 3));
        }
    }

    public void showColorPicker() {
        boolean z = false;
        boolean z2 = true;
        if (this.bottomSheet != null) {
            return;
        }
        if (!this.previewView.expanded) {
            setExpanded(true, true, true);
        }
        BackgroundGradient backgroundGradient = this.previewView.backgroundGradient;
        final boolean[] zArr = {false};
        AndroidUtilities.requestAdjustNothing(getParentActivity(), getClassGuid());
        BottomSheet bottomSheet = new BottomSheet(getContext(), z2) {
            @Override
            public void lambda$new$0() {
                super.lambda$new$0();
                AvatarConstructorFragment.this.backgroundSelectView.selectGradient(AvatarConstructorFragment.this.colorPickerGradient);
                AvatarConstructorFragment avatarConstructorFragment = AvatarConstructorFragment.this;
                avatarConstructorFragment.colorPickerInAnimatoin = true;
                avatarConstructorFragment.fragmentView.invalidate();
                AvatarConstructorFragment.this.colorPickerPreviewView.animate().setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        AvatarConstructorFragment avatarConstructorFragment2 = AvatarConstructorFragment.this;
                        avatarConstructorFragment2.colorPickerInAnimatoin = false;
                        avatarConstructorFragment2.colorPickerPreviewView.setVisibility(8);
                    }
                }).alpha(0.0f).setDuration(200L).start();
            }

            @Override
            public void dismissInternal() {
                super.dismissInternal();
                AndroidUtilities.requestAdjustResize(AvatarConstructorFragment.this.getParentActivity(), AvatarConstructorFragment.this.getClassGuid());
                AvatarConstructorFragment.this.bottomSheet = null;
            }
        };
        this.bottomSheet = bottomSheet;
        bottomSheet.fixNavigationBar();
        this.bottomSheet.pauseAllHeavyOperations = false;
        this.drawForBlur = true;
        this.colorPickerPreviewView.setBackground(new BitmapDrawable(getContext().getResources(), AndroidUtilities.makeBlurBitmap(this.fragmentView, 12.0f, 10)));
        this.drawForBlur = false;
        this.colorPickerPreviewView.setVisibility(0);
        this.colorPickerPreviewView.setAlpha(0.0f);
        this.colorPickerInAnimatoin = true;
        this.fragmentView.invalidate();
        this.colorPickerPreviewView.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                AvatarConstructorFragment.this.colorPickerInAnimatoin = false;
            }
        }).alpha(1.0f).setDuration(200L).start();
        this.colorPickerGradient = new BackgroundGradient();
        ColorPicker colorPicker = new ColorPicker(getContext(), z, new ColorPicker.ColorPickerDelegate() {
            @Override
            public void deleteTheme() {
                ColorPicker.ColorPickerDelegate.CC.$default$deleteTheme(this);
            }

            @Override
            public int getDefaultColor(int i) {
                return ColorPicker.ColorPickerDelegate.CC.$default$getDefaultColor(this, i);
            }

            @Override
            public void openThemeCreate(boolean z3) {
                ColorPicker.ColorPickerDelegate.CC.$default$openThemeCreate(this, z3);
            }

            @Override
            public final void setColor(int i, int i2, boolean z3) {
                AvatarConstructorFragment.this.lambda$showColorPicker$4(i, i2, z3);
            }
        }) {
            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(300.0f), 1073741824));
            }
        };
        BackgroundGradient backgroundGradient2 = this.previewView.backgroundGradient;
        if (backgroundGradient2 != null) {
            BackgroundGradient backgroundGradient3 = this.colorPickerGradient;
            int i = backgroundGradient2.color4;
            backgroundGradient3.color4 = i;
            colorPicker.setColor(i, 3);
            BackgroundGradient backgroundGradient4 = this.colorPickerGradient;
            int i2 = this.previewView.backgroundGradient.color3;
            backgroundGradient4.color3 = i2;
            colorPicker.setColor(i2, 2);
            BackgroundGradient backgroundGradient5 = this.colorPickerGradient;
            int i3 = this.previewView.backgroundGradient.color2;
            backgroundGradient5.color2 = i3;
            colorPicker.setColor(i3, 1);
            BackgroundGradient backgroundGradient6 = this.colorPickerGradient;
            int i4 = this.previewView.backgroundGradient.color1;
            backgroundGradient6.color1 = i4;
            colorPicker.setColor(i4, 0);
        }
        colorPicker.setType(-1, true, 4, this.colorPickerGradient.colorsCount(), false, 0, false);
        this.previewView.setGradient(this.colorPickerGradient);
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        linearLayout.setPadding(0, AndroidUtilities.dp(8.0f), 0, 0);
        linearLayout.addView(colorPicker);
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.setBackground(Theme.AdaptiveRipple.filledRectByKey(Theme.key_featuredStickers_addButton, 8.0f));
        TextView textView = new TextView(getContext());
        textView.setTextSize(1, 14.0f);
        textView.setText(LocaleController.getString(R.string.SetColor));
        textView.setGravity(17);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        frameLayout.addView(textView, LayoutHelper.createFrame(-2, -2, 17));
        linearLayout.addView(frameLayout, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, -8.0f, 16.0f, 16.0f));
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                AvatarConstructorFragment.this.lambda$showColorPicker$5(zArr, view);
            }
        });
        this.bottomSheet.setCustomView(linearLayout);
        BottomSheet bottomSheet2 = this.bottomSheet;
        bottomSheet2.smoothKeyboardAnimationEnabled = true;
        bottomSheet2.setDimBehind(false);
        this.bottomSheet.show();
        isLightStatusBar();
    }

    void cancelExpandAnimator() {
        ValueAnimator valueAnimator = this.expandAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.expandAnimator.cancel();
            this.expandAnimator = null;
        }
    }

    @Override
    public View createView(Context context) {
        int i;
        this.hasOwnBackground = true;
        this.actionBar.setBackgroundDrawable(null);
        this.actionBar.setCastShadows(false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setOccupyStatusBar(true);
        ActionBar actionBar = this.actionBar;
        int i2 = Theme.key_windowBackgroundWhiteBlackText;
        actionBar.setTitleColor(Theme.getColor(i2));
        this.actionBar.setItemsColor(Theme.getColor(i2), false);
        this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_listSelector), false);
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setTitle(LocaleController.getString(R.string.PhotoEditor));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i3) {
                if (i3 == -1) {
                    AvatarConstructorFragment.this.discardEditor();
                }
            }
        });
        this.actionBar.getTitleTextView().setAlpha(0.0f);
        ActionBar actionBar2 = new ActionBar(getContext());
        this.overlayActionBar = actionBar2;
        actionBar2.setCastShadows(false);
        this.overlayActionBar.setAddToContainer(false);
        this.overlayActionBar.setOccupyStatusBar(true);
        this.overlayActionBar.setClipChildren(false);
        int alphaComponent = ColorUtils.setAlphaComponent(-1, 60);
        this.overlayActionBar.setItemsColor(-1, false);
        this.overlayActionBar.setBackButtonDrawable(new BackDrawable(false));
        this.overlayActionBar.setAllowOverlayTitle(false);
        this.overlayActionBar.setItemsBackgroundColor(alphaComponent, false);
        ActionBarMenu createMenu = this.overlayActionBar.createMenu();
        createMenu.setClipChildren(false);
        ImageUpdater.AvatarFor avatarFor = this.avatarFor;
        ActionBarMenuItem addItem = createMenu.addItem(1, LocaleController.getString((avatarFor == null || avatarFor.type != 2) ? R.string.SetPhoto : R.string.SuggestPhoto));
        this.setPhotoItem = addItem;
        addItem.setBackground(Theme.createSelectorDrawable(alphaComponent, 3));
        this.overlayActionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i3) {
                if (i3 == -1) {
                    AvatarConstructorFragment.this.discardEditor();
                }
                if (i3 == 1) {
                    AvatarConstructorFragment.this.onDonePressed();
                }
            }
        });
        this.linearLayout = new LinearLayout(getContext()) {
            @Override
            protected boolean drawChild(Canvas canvas, View view, long j) {
                if (view == AvatarConstructorFragment.this.previewView) {
                    return true;
                }
                return super.drawChild(canvas, view, j);
            }
        };
        final ContainerLayout containerLayout = new ContainerLayout(context) {
            boolean isScrolling;
            boolean maybeScroll;
            float scrollFromX;
            float scrollFromY;
            float startFromProgressToExpand;

            @Override
            public void dispatchDraw(Canvas canvas) {
                int save = canvas.save();
                super.dispatchDraw(canvas);
                AvatarConstructorFragment avatarConstructorFragment = AvatarConstructorFragment.this;
                if (!avatarConstructorFragment.isLandscapeMode) {
                    if (!avatarConstructorFragment.drawForBlur) {
                        canvas.save();
                        float x = AvatarConstructorFragment.this.linearLayout.getX() + AvatarConstructorFragment.this.previewView.getX();
                        float y = AvatarConstructorFragment.this.linearLayout.getY() + AvatarConstructorFragment.this.previewView.getY();
                        AvatarConstructorFragment avatarConstructorFragment2 = AvatarConstructorFragment.this;
                        int i3 = avatarConstructorFragment2.expandedHeight - avatarConstructorFragment2.collapsedHeight;
                        int i4 = AndroidUtilities.statusBarHeight;
                        int currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
                        float lerp = AndroidUtilities.lerp(y, i4 + ((currentActionBarHeight - r6.collapsedHeight) >> 1), AvatarConstructorFragment.this.keyboardVisibleProgress);
                        canvas.translate(x, lerp);
                        AvatarConstructorFragment.this.previewView.draw(canvas);
                        float f = i3 / 2.0f;
                        AndroidUtilities.rectTmp.set(x, lerp - (AvatarConstructorFragment.this.progressToExpand * f), r5.previewView.getMeasuredWidth() + x, AvatarConstructorFragment.this.previewView.getMeasuredHeight() + lerp + (f * AvatarConstructorFragment.this.progressToExpand));
                        float f2 = x + AvatarConstructorFragment.this.previewView.cx;
                        float f3 = lerp + AvatarConstructorFragment.this.previewView.cy;
                        AvatarConstructorFragment avatarConstructorFragment3 = AvatarConstructorFragment.this;
                        avatarConstructorFragment3.avatarClickableArea.setRect((int) (f2 - avatarConstructorFragment3.previewView.size), (int) (f3 - AvatarConstructorFragment.this.previewView.size), (int) (f2 + AvatarConstructorFragment.this.previewView.size), (int) (f3 + AvatarConstructorFragment.this.previewView.size));
                        canvas.restore();
                    }
                    canvas.restoreToCount(save);
                    float alpha = AvatarConstructorFragment.this.previewView.expandProgress.get() * (1.0f - (AvatarConstructorFragment.this.colorPickerPreviewView.getVisibility() == 0 ? AvatarConstructorFragment.this.colorPickerPreviewView.getAlpha() : 0.0f));
                    if (alpha != 0.0f) {
                        AvatarConstructorFragment.this.overlayActionBar.setVisibility(0);
                        int save2 = canvas.save();
                        canvas.translate(AvatarConstructorFragment.this.overlayActionBar.getX(), AvatarConstructorFragment.this.overlayActionBar.getY());
                        if (alpha != 1.0f) {
                            canvas.saveLayerAlpha(0.0f, 0.0f, AvatarConstructorFragment.this.overlayActionBar.getMeasuredWidth(), AvatarConstructorFragment.this.overlayActionBar.getMeasuredHeight(), (int) (alpha * 255.0f), 31);
                        }
                        AvatarConstructorFragment.this.overlayActionBar.draw(canvas);
                        canvas.restoreToCount(save2);
                    } else {
                        AvatarConstructorFragment.this.overlayActionBar.setVisibility(8);
                    }
                }
                if (AvatarConstructorFragment.this.colorPickerInAnimatoin) {
                    invalidate();
                }
            }

            @Override
            protected boolean drawChild(Canvas canvas, View view, long j) {
                AvatarConstructorFragment avatarConstructorFragment = AvatarConstructorFragment.this;
                if (view == avatarConstructorFragment.overlayActionBar) {
                    return true;
                }
                if (view == ((BaseFragment) avatarConstructorFragment).actionBar) {
                    AvatarConstructorFragment avatarConstructorFragment2 = AvatarConstructorFragment.this;
                    if (avatarConstructorFragment2.keyboardVisibleProgress > 0.0f) {
                        avatarConstructorFragment2.actionBarPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
                        AvatarConstructorFragment avatarConstructorFragment3 = AvatarConstructorFragment.this;
                        avatarConstructorFragment3.actionBarPaint.setAlpha((int) (avatarConstructorFragment3.keyboardVisibleProgress * 255.0f));
                        canvas.drawRect(0.0f, 0.0f, view.getMeasuredWidth(), view.getMeasuredHeight(), AvatarConstructorFragment.this.actionBarPaint);
                        AvatarConstructorFragment.this.getParentLayout().drawHeaderShadow(canvas, (int) (AvatarConstructorFragment.this.keyboardVisibleProgress * 255.0f), view.getMeasuredHeight());
                    }
                }
                return super.drawChild(canvas, view, j);
            }

            @Override
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (AvatarConstructorFragment.this.keyboardVisibleProgress == 0.0f) {
                    return false;
                }
                return onTouchEvent(motionEvent);
            }

            @Override
            protected void onMeasure(int i3, int i4) {
                ViewGroup.MarginLayoutParams marginLayoutParams;
                int dp;
                measureKeyboardHeight();
                boolean z = View.MeasureSpec.getSize(i3) > View.MeasureSpec.getSize(i4) + this.keyboardHeight;
                AvatarConstructorFragment avatarConstructorFragment = AvatarConstructorFragment.this;
                if (z != avatarConstructorFragment.isLandscapeMode) {
                    avatarConstructorFragment.isLandscapeMode = z;
                    AndroidUtilities.removeFromParent(avatarConstructorFragment.previewView);
                    AndroidUtilities.requestAdjustNothing(AvatarConstructorFragment.this.getParentActivity(), AvatarConstructorFragment.this.getClassGuid());
                    AvatarConstructorFragment avatarConstructorFragment2 = AvatarConstructorFragment.this;
                    if (avatarConstructorFragment2.isLandscapeMode) {
                        avatarConstructorFragment2.setProgressToExpand(0.0f, false);
                        AvatarConstructorFragment.this.previewView.setExpanded(false);
                        addView(AvatarConstructorFragment.this.previewView, 0, LayoutHelper.createFrame(-1, -1.0f));
                    } else {
                        avatarConstructorFragment2.linearLayout.addView(avatarConstructorFragment2.previewView, 0, LayoutHelper.createFrame(-1, -2.0f));
                    }
                    AndroidUtilities.requestAdjustResize(AvatarConstructorFragment.this.getParentActivity(), AvatarConstructorFragment.this.getClassGuid());
                }
                AvatarConstructorFragment avatarConstructorFragment3 = AvatarConstructorFragment.this;
                if (avatarConstructorFragment3.isLandscapeMode) {
                    int size = (int) (View.MeasureSpec.getSize(i3) * 0.55f);
                    ((ViewGroup.MarginLayoutParams) AvatarConstructorFragment.this.linearLayout.getLayoutParams()).bottomMargin = 0;
                    ((ViewGroup.MarginLayoutParams) AvatarConstructorFragment.this.linearLayout.getLayoutParams()).leftMargin = (int) (View.MeasureSpec.getSize(i3) * 0.45f);
                    ((ViewGroup.MarginLayoutParams) AvatarConstructorFragment.this.previewView.getLayoutParams()).rightMargin = size;
                    ((ViewGroup.MarginLayoutParams) AvatarConstructorFragment.this.button.getLayoutParams()).rightMargin = size + AndroidUtilities.dp(16.0f);
                    ((ViewGroup.MarginLayoutParams) AvatarConstructorFragment.this.chooseBackgroundHint.getLayoutParams()).topMargin = 0;
                    marginLayoutParams = (ViewGroup.MarginLayoutParams) AvatarConstructorFragment.this.chooseEmojiHint.getLayoutParams();
                    dp = AndroidUtilities.dp(10.0f);
                } else {
                    ((ViewGroup.MarginLayoutParams) avatarConstructorFragment3.linearLayout.getLayoutParams()).bottomMargin = AndroidUtilities.dp(64.0f);
                    ((ViewGroup.MarginLayoutParams) AvatarConstructorFragment.this.linearLayout.getLayoutParams()).leftMargin = 0;
                    ((ViewGroup.MarginLayoutParams) AvatarConstructorFragment.this.previewView.getLayoutParams()).rightMargin = 0;
                    ((ViewGroup.MarginLayoutParams) AvatarConstructorFragment.this.button.getLayoutParams()).rightMargin = AndroidUtilities.dp(16.0f);
                    ((ViewGroup.MarginLayoutParams) AvatarConstructorFragment.this.chooseBackgroundHint.getLayoutParams()).topMargin = AndroidUtilities.dp(10.0f);
                    marginLayoutParams = (ViewGroup.MarginLayoutParams) AvatarConstructorFragment.this.chooseEmojiHint.getLayoutParams();
                    dp = AndroidUtilities.dp(18.0f);
                }
                marginLayoutParams.topMargin = dp;
                AvatarConstructorFragment avatarConstructorFragment4 = AvatarConstructorFragment.this;
                boolean z2 = avatarConstructorFragment4.keyboardVisible;
                avatarConstructorFragment4.keyboardVisible = this.keyboardHeight >= AndroidUtilities.dp(20.0f);
                if (z2 != AvatarConstructorFragment.this.keyboardVisible) {
                    super.onMeasure(i3, i4);
                    AvatarConstructorFragment avatarConstructorFragment5 = AvatarConstructorFragment.this;
                    int measuredHeight = avatarConstructorFragment5.keyboardVisible ? (-avatarConstructorFragment5.selectAnimatedEmojiDialog.getTop()) + ((BaseFragment) AvatarConstructorFragment.this).actionBar.getMeasuredHeight() + AndroidUtilities.dp(8.0f) : 0;
                    LinearLayout linearLayout = AvatarConstructorFragment.this.linearLayout;
                    linearLayout.setTranslationY((linearLayout.getTranslationY() + ((ViewGroup.MarginLayoutParams) AvatarConstructorFragment.this.linearLayout.getLayoutParams()).topMargin) - measuredHeight);
                    ((ViewGroup.MarginLayoutParams) AvatarConstructorFragment.this.linearLayout.getLayoutParams()).topMargin = measuredHeight;
                    AvatarConstructorFragment avatarConstructorFragment6 = AvatarConstructorFragment.this;
                    avatarConstructorFragment6.createKeyboardVisibleAnimator(avatarConstructorFragment6.keyboardVisible);
                }
                super.onMeasure(i3, i4);
                AvatarConstructorFragment avatarConstructorFragment7 = AvatarConstructorFragment.this;
                avatarConstructorFragment7.collapsedHeight = avatarConstructorFragment7.previewView.getMeasuredHeight();
                AvatarConstructorFragment avatarConstructorFragment8 = AvatarConstructorFragment.this;
                avatarConstructorFragment8.expandedHeight = avatarConstructorFragment8.previewView.getMeasuredWidth();
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                boolean z;
                if (AvatarConstructorFragment.this.avatarClickableArea.checkTouchEvent(motionEvent)) {
                    return true;
                }
                if (!AvatarConstructorFragment.this.isLandscapeMode) {
                    if (motionEvent.getAction() == 0) {
                        SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = AvatarConstructorFragment.this.selectAnimatedEmojiDialog;
                        android.graphics.Rect rect = AndroidUtilities.rectTmp2;
                        selectAnimatedEmojiDialog.getHitRect(rect);
                        rect.offset(0, (int) AvatarConstructorFragment.this.linearLayout.getY());
                        if (AvatarConstructorFragment.this.keyboardVisibleProgress == 0.0f && !rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            this.maybeScroll = true;
                            this.scrollFromX = motionEvent.getX();
                            this.scrollFromY = motionEvent.getY();
                        }
                    } else if (motionEvent.getAction() == 2 && ((z = this.maybeScroll) || this.isScrolling)) {
                        if (!z) {
                            AvatarConstructorFragment.this.setProgressToExpand(Utilities.clamp(this.startFromProgressToExpand + ((-(this.scrollFromY - motionEvent.getY())) / AvatarConstructorFragment.this.expandedHeight), 1.0f, 0.0f), true);
                        } else if (Math.abs(this.scrollFromY - motionEvent.getY()) > AndroidUtilities.touchSlop) {
                            this.maybeScroll = false;
                            this.isScrolling = true;
                            this.startFromProgressToExpand = AvatarConstructorFragment.this.progressToExpand;
                            this.scrollFromX = motionEvent.getX();
                            this.scrollFromY = motionEvent.getY();
                        }
                    } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                        if (this.isScrolling) {
                            AvatarConstructorFragment avatarConstructorFragment = AvatarConstructorFragment.this;
                            avatarConstructorFragment.setExpanded(avatarConstructorFragment.progressToExpand > 0.5f, false, false);
                        }
                        this.maybeScroll = false;
                        this.isScrolling = false;
                    }
                }
                return this.isScrolling || super.onTouchEvent(motionEvent) || this.maybeScroll;
            }
        };
        containerLayout.setFitsSystemWindows(true);
        containerLayout.setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray));
        this.linearLayout.setClipChildren(false);
        this.linearLayout.setClipToPadding(false);
        this.linearLayout.setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
        this.linearLayout.setOrientation(1);
        LinearLayout linearLayout = this.linearLayout;
        PreviewView previewView = new PreviewView(getContext()) {
            @Override
            public void invalidate() {
                super.invalidate();
                containerLayout.invalidate();
            }
        };
        this.previewView = previewView;
        linearLayout.addView(previewView);
        TextView textView = new TextView(getContext());
        this.chooseBackgroundHint = textView;
        textView.setText(LocaleController.getString(R.string.ChooseBackground));
        TextView textView2 = this.chooseBackgroundHint;
        int i3 = Theme.key_windowBackgroundWhiteGrayText;
        textView2.setTextColor(Theme.getColor(i3));
        this.chooseBackgroundHint.setTextSize(1, 14.0f);
        this.chooseBackgroundHint.setGravity(17);
        this.linearLayout.addView(this.chooseBackgroundHint, LayoutHelper.createLinear(-1, -2, 0, 21, 10, 21, 10));
        FrameLayout frameLayout = new FrameLayout(getContext()) {
            private Path path = new Path();
            private Paint paint = new Paint(1);

            @Override
            protected void dispatchDraw(Canvas canvas) {
                Theme.applyDefaultShadow(this.paint);
                this.paint.setColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground, AvatarConstructorFragment.this.getResourceProvider()));
                this.paint.setAlpha((int) (getAlpha() * 255.0f));
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.path.rewind();
                this.path.addRoundRect(rectF, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), Path.Direction.CW);
                canvas.drawPath(this.path, this.paint);
                super.dispatchDraw(canvas);
            }
        };
        BackgroundSelectView backgroundSelectView = new BackgroundSelectView(getContext());
        this.backgroundSelectView = backgroundSelectView;
        frameLayout.addView(backgroundSelectView);
        this.linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, 48, 0, 12, 0, 12, 0));
        TextView textView3 = new TextView(getContext());
        this.chooseEmojiHint = textView3;
        textView3.setText(LocaleController.getString(R.string.ChooseEmojiOrSticker));
        this.chooseEmojiHint.setTextColor(Theme.getColor(i3));
        this.chooseEmojiHint.setTextSize(1, 14.0f);
        this.chooseEmojiHint.setGravity(17);
        this.linearLayout.addView(this.chooseEmojiHint, LayoutHelper.createLinear(-1, -2, 0, 21, 18, 21, 10));
        SelectAnimatedEmojiDialog selectAnimatedEmojiDialog = new SelectAnimatedEmojiDialog(this, getContext(), false, null, 4, true, null, 16, Theme.isCurrentThemeDark() ? -1 : getThemedColor(Theme.key_windowBackgroundWhiteBlueIcon)) {
            private boolean firstLayout = true;

            @Override
            protected void onEmojiSelected(View view, Long l, TLRPC.Document document, TL_stars.TL_starGiftUnique tL_starGiftUnique, Integer num) {
                AvatarConstructorFragment.this.setPreview(l == null ? 0L : l.longValue(), document);
            }

            @Override
            public void onLayout(boolean z, int i4, int i5, int i6, int i7) {
                super.onLayout(z, i4, i5, i6, i7);
                if (this.firstLayout) {
                    this.firstLayout = false;
                    AvatarConstructorFragment.this.selectAnimatedEmojiDialog.onShow(null);
                }
            }
        };
        this.selectAnimatedEmojiDialog = selectAnimatedEmojiDialog;
        selectAnimatedEmojiDialog.forUser = !this.forGroup;
        selectAnimatedEmojiDialog.setAnimationsEnabled(this.fragmentBeginToShow);
        this.selectAnimatedEmojiDialog.setClipChildren(false);
        this.linearLayout.addView(this.selectAnimatedEmojiDialog, LayoutHelper.createLinear(-1, -1, 0, 12, 0, 12, 12));
        this.linearLayout.setClipChildren(false);
        containerLayout.addView(this.linearLayout, LayoutHelper.createFrame(-1, -1.0f, 0, 0.0f, 0.0f, 0.0f, 64.0f));
        View view = new View(getContext());
        this.colorPickerPreviewView = view;
        view.setVisibility(8);
        FrameLayout frameLayout2 = new FrameLayout(getContext());
        this.button = frameLayout2;
        frameLayout2.setBackground(Theme.AdaptiveRipple.filledRectByKey(Theme.key_featuredStickers_addButton, 8.0f));
        TextView textView4 = new TextView(getContext());
        textView4.setTextSize(1, 14.0f);
        int i4 = this.imageUpdater.setForType;
        if (i4 == 1) {
            i = R.string.SetChannelPhoto;
        } else if (i4 == 2) {
            i = R.string.SetGroupPhoto;
        } else {
            ImageUpdater.AvatarFor avatarFor2 = this.avatarFor;
            i = (avatarFor2 == null || avatarFor2.type != 2) ? R.string.SetProfilePhotoAvatarConstructor : R.string.SuggestPhoto;
        }
        textView4.setText(LocaleController.getString(i));
        textView4.setGravity(17);
        textView4.setTypeface(AndroidUtilities.bold());
        textView4.setTextColor(Theme.getColor(Theme.key_featuredStickers_buttonText));
        this.button.addView(textView4, LayoutHelper.createFrame(-2, -2, 17));
        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view2) {
                AvatarConstructorFragment.this.lambda$createView$0(view2);
            }
        });
        containerLayout.addView(this.button, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 16.0f, 16.0f, 16.0f));
        containerLayout.addView(this.actionBar);
        containerLayout.addView(this.overlayActionBar);
        containerLayout.addView(this.colorPickerPreviewView, LayoutHelper.createFrame(-1, -1.0f));
        CanvasButton canvasButton = new CanvasButton(containerLayout);
        this.avatarClickableArea = canvasButton;
        canvasButton.setDelegate(new Runnable() {
            @Override
            public final void run() {
                AvatarConstructorFragment.this.lambda$createView$1();
            }
        });
        this.fragmentView = containerLayout;
        return containerLayout;
    }

    @Override
    public boolean isLightStatusBar() {
        PreviewView previewView = this.previewView;
        boolean z = previewView == null || (!previewView.expanded && (previewView.overrideExpandProgress < 0.0f || previewView.backgroundGradient == null)) ? AndroidUtilities.computePerceivedBrightness(Theme.getColor(Theme.key_windowBackgroundGray)) > 0.721f : AndroidUtilities.computePerceivedBrightness(previewView.backgroundGradient.getAverageColor()) > 0.721f;
        if (this.isLightInternal != z) {
            this.isLightInternal = z;
            if (this.actionBar.getAlpha() == 0.0f) {
                setProgressToLightStatusBar(z ? 0.0f : 1.0f);
            } else {
                ValueAnimator valueAnimator = this.lightProgressAnimator;
                if (valueAnimator != null) {
                    valueAnimator.removeAllListeners();
                    this.lightProgressAnimator.cancel();
                }
                ValueAnimator ofFloat = ValueAnimator.ofFloat(this.progressToLightStatusBar, z ? 0.0f : 1.0f);
                this.lightProgressAnimator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        AvatarConstructorFragment.this.lambda$isLightStatusBar$6(valueAnimator2);
                    }
                });
                this.lightProgressAnimator.setDuration(150L).start();
            }
        }
        BottomSheet bottomSheet = this.bottomSheet;
        if (bottomSheet != null) {
            AndroidUtilities.setLightStatusBar(bottomSheet.getWindow(), z);
        }
        return z;
    }

    @Override
    public boolean isSwipeBackEnabled(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onBackPressed() {
        discardEditor();
        return false;
    }

    public void lambda$createView$1() {
        if (this.isLandscapeMode) {
            return;
        }
        if (this.keyboardVisibleProgress <= 0.0f) {
            setExpanded(!this.previewView.expanded, true, false);
            return;
        }
        if (this.keyboardVisibilityAnimator != null) {
            this.progressToExpand = 1.0f;
            this.expandWithKeyboard = true;
        }
        AndroidUtilities.hideKeyboard(this.fragmentView);
    }

    @Override
    public void onResume() {
        super.onResume();
        AndroidUtilities.requestAdjustResize(getParentActivity(), getClassGuid());
    }

    public void setDelegate(Delegate delegate) {
        this.delegate = delegate;
    }

    public void startFrom(TLRPC.VideoSize videoSize) {
        long j;
        BackgroundGradient backgroundGradient = new BackgroundGradient();
        backgroundGradient.color1 = ColorUtils.setAlphaComponent(videoSize.background_colors.get(0).intValue(), 255);
        backgroundGradient.color2 = videoSize.background_colors.size() > 1 ? ColorUtils.setAlphaComponent(videoSize.background_colors.get(1).intValue(), 255) : 0;
        backgroundGradient.color3 = videoSize.background_colors.size() > 2 ? ColorUtils.setAlphaComponent(videoSize.background_colors.get(2).intValue(), 255) : 0;
        backgroundGradient.color4 = videoSize.background_colors.size() > 3 ? ColorUtils.setAlphaComponent(videoSize.background_colors.get(3).intValue(), 255) : 0;
        this.previewView.setGradient(backgroundGradient);
        TLRPC.Document document = null;
        if (videoSize instanceof TLRPC.TL_videoSizeEmojiMarkup) {
            j = ((TLRPC.TL_videoSizeEmojiMarkup) videoSize).emoji_id;
        } else {
            TLRPC.TL_videoSizeStickerMarkup tL_videoSizeStickerMarkup = new TLRPC.TL_videoSizeStickerMarkup();
            TLRPC.TL_messages_stickerSet stickerSet = MediaDataController.getInstance(this.currentAccount).getStickerSet(tL_videoSizeStickerMarkup.stickerset, false);
            if (stickerSet != null) {
                for (int i = 0; i < stickerSet.documents.size(); i++) {
                    if (stickerSet.documents.get(i).id == tL_videoSizeStickerMarkup.sticker_id) {
                        document = stickerSet.documents.get(i);
                    }
                }
            }
            j = 0;
        }
        setPreview(j, document);
        this.backgroundSelectView.selectGradient(backgroundGradient);
        this.selectAnimatedEmojiDialog.setForUser(true);
    }

    public void startFrom(AvatarConstructorPreviewCell avatarConstructorPreviewCell) {
        BackgroundGradient backgroundGradient = avatarConstructorPreviewCell.getBackgroundGradient();
        PreviewView previewView = this.previewView;
        if (previewView == null) {
            return;
        }
        previewView.setGradient(backgroundGradient);
        if (avatarConstructorPreviewCell.getAnimatedEmoji() != null) {
            long documentId = avatarConstructorPreviewCell.getAnimatedEmoji().getDocumentId();
            PreviewView previewView2 = this.previewView;
            previewView2.documentId = documentId;
            previewView2.backupImageView.setAnimatedEmojiDrawable(new AnimatedEmojiDrawable(14, this.currentAccount, documentId));
        }
        this.backgroundSelectView.selectGradient(backgroundGradient);
        this.selectAnimatedEmojiDialog.setForUser(avatarConstructorPreviewCell.forUser);
    }
}
