package org.telegram.ui.Stories.recorder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Shader;
import android.net.Uri;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.zxing.common.detector.MathUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.video.VideoPlayerHolderBase;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.BlurringShader;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Stories.recorder.CollageLayout;
import org.telegram.ui.Stories.recorder.CollageLayoutView2;

public abstract class CollageLayoutView2 extends FrameLayout implements ItemOptions.ScrimView {
    private final AnimatedFloat[] animatedColumns;
    private final AnimatedFloat animatedReordering;
    private final AnimatedFloat animatedRows;
    private boolean attached;
    private final BlurringShader.BlurManager blurManager;
    public CameraView cameraView;
    private Object cameraViewBlurRenderNode;
    private Runnable cancelGestures;
    private final Path clipPath;
    private final FrameLayout containerView;
    private CollageLayout currentLayout;
    public Part currentPart;
    public float dx;
    public float dy;
    private boolean fastSeek;
    private final LinearGradient gradient;
    private final Matrix gradientMatrix;
    private final int gradientWidth;
    private final Paint highlightPaint;
    private final Path highlightPath;
    public boolean isMuted;
    private long lastPausedPosition;
    public float ldx;
    public float ldy;
    private final float[] lefts;
    public Part longPressedPart;
    private boolean needsBlur;
    public Part nextPart;
    public Runnable onLongPressPart;
    private Runnable onResetState;
    public final ArrayList parts;
    private boolean playing;
    public Part pressedPart;
    private boolean preview;
    private long previewStartTime;
    private PreviewView previewView;
    private final float[] radii;
    private final RectF rect;
    public final ArrayList removingParts;
    public boolean reordering;
    public Part reorderingPart;
    public boolean reorderingTouch;
    private final Runnable resetReordering;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean restorePositionOnPlaying;
    private final float[] rights;
    private final Runnable syncRunnable;
    private TimelineView timelineView;
    public float tx;
    public float ty;

    public class Part {
        private ValueAnimator animator;
        private StoryEntry content;
        private boolean current;
        private final AnimatedFloat highlightAnimated;
        public final ImageReceiver imageReceiver;
        private int index;
        public CollageLayout.Part part;
        public TextureView textureView;
        public boolean textureViewReady;
        public VideoPlayerHolderBase videoPlayer;
        private volatile long pendingSeek = -1;
        public boolean hasBounds = false;
        public RectF fromBounds = new RectF();
        public RectF bounds = new RectF();
        public float boundsTransition = 1.0f;

        public class AnonymousClass3 extends VideoPlayerHolderBase {
            AnonymousClass3() {
            }

            public void lambda$onVideoSizeChanged$0(int i, int i2, int i3) {
                StoryEntry storyEntry = Part.this.content;
                if (storyEntry == null) {
                    return;
                }
                if (storyEntry.width == i && storyEntry.height == i2 && storyEntry.orientation == i3) {
                    return;
                }
                storyEntry.width = i;
                storyEntry.height = i2;
                storyEntry.orientation = i3;
                TextureView textureView = Part.this.textureView;
                if (textureView != null) {
                    textureView.requestLayout();
                }
            }

            @Override
            public boolean needRepeat() {
                return !CollageLayoutView2.this.preview;
            }

            @Override
            public void onRenderedFirstFrame() {
                Part part = Part.this;
                part.textureViewReady = true;
                CollageLayoutView2.this.invalidate();
            }

            @Override
            protected void onVideoSizeChanged(final int i, final int i2, final int i3, float f) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        CollageLayoutView2.Part.AnonymousClass3.this.lambda$onVideoSizeChanged$0(i, i2, i3);
                    }
                });
            }
        }

        public Part() {
            this.highlightAnimated = new AnimatedFloat(CollageLayoutView2.this, 0L, 1200L, CubicBezierInterpolator.EASE_OUT);
            this.imageReceiver = new ImageReceiver(CollageLayoutView2.this);
        }

        public static long access$802(Part part, long j) {
            part.pendingSeek = j;
            return j;
        }

        public void destroyContent() {
            VideoPlayerHolderBase videoPlayerHolderBase = this.videoPlayer;
            if (videoPlayerHolderBase != null) {
                videoPlayerHolderBase.pause();
                this.videoPlayer.release(null);
                this.videoPlayer = null;
            }
            TextureView textureView = this.textureView;
            if (textureView != null) {
                AndroidUtilities.removeFromParent(textureView);
                this.textureView = null;
            }
            this.textureViewReady = false;
        }

        public boolean hasContent() {
            return this.content != null;
        }

        public void setContent(StoryEntry storyEntry) {
            destroyContent();
            this.content = storyEntry;
            StringBuilder sb = new StringBuilder();
            sb.append((int) Math.ceil(AndroidUtilities.displaySize.x / AndroidUtilities.density));
            sb.append("_");
            sb.append((int) Math.ceil(AndroidUtilities.displaySize.y / AndroidUtilities.density));
            sb.append((storyEntry == null || !storyEntry.isVideo) ? "" : "_g");
            sb.append("_exif");
            String sb2 = sb.toString();
            StoryEntry storyEntry2 = this.content;
            if (storyEntry2 == null) {
                this.imageReceiver.clearImage();
            } else if (storyEntry2.isVideo) {
                Bitmap bitmap = storyEntry2.blurredVideoThumb;
                if (bitmap == null && (bitmap = storyEntry2.thumbBitmap) == null) {
                    String str = storyEntry2.thumbPath;
                    if (str != null) {
                        this.imageReceiver.setImage(str, sb2, null, null, 0L);
                    } else {
                        this.imageReceiver.clearImage();
                    }
                } else {
                    this.imageReceiver.setImageBitmap(bitmap);
                }
                TextureView textureView = new TextureView(CollageLayoutView2.this.getContext());
                this.textureView = textureView;
                CollageLayoutView2.this.addView(textureView);
                AnonymousClass3 anonymousClass3 = new AnonymousClass3();
                this.videoPlayer = anonymousClass3;
                anonymousClass3.allowMultipleInstances(true);
                this.videoPlayer.with(this.textureView);
                this.videoPlayer.preparePlayer(Uri.fromFile(this.content.file), false, 1.0f);
                VideoPlayerHolderBase videoPlayerHolderBase = this.videoPlayer;
                CollageLayoutView2 collageLayoutView2 = CollageLayoutView2.this;
                videoPlayerHolderBase.setVolume((collageLayoutView2.isMuted || this.content.muted || !collageLayoutView2.preview) ? 0.0f : this.content.videoVolume);
                if (!CollageLayoutView2.this.preview || CollageLayoutView2.this.playing) {
                    this.videoPlayer.play();
                } else {
                    this.videoPlayer.pause();
                }
            } else {
                this.imageReceiver.setImage(storyEntry2.file.getAbsolutePath(), sb2, null, null, 0L);
            }
            CollageLayoutView2.this.invalidate();
        }

        public void setCurrent(boolean z) {
            this.current = z;
        }

        public void setPart(CollageLayout.Part part, boolean z) {
            CollageLayout.Part part2 = this.part;
            if (part != null) {
                this.part = part;
            }
            ValueAnimator valueAnimator = this.animator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.animator = null;
            }
            if (z) {
                if (this.hasBounds) {
                    RectF rectF = this.fromBounds;
                    AndroidUtilities.lerp(rectF, this.bounds, this.boundsTransition, rectF);
                } else {
                    CollageLayoutView2.this.layoutOut(this.fromBounds, part);
                }
                if (part == null) {
                    CollageLayoutView2.this.layoutOut(this.bounds, part2);
                } else {
                    CollageLayoutView2.this.layout(this.bounds, part);
                }
                this.boundsTransition = 0.0f;
                ValueAnimator ofFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.animator = ofFloat;
                ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        Part.this.boundsTransition = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                        CollageLayoutView2.this.invalidate();
                    }
                });
                this.animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        Part part3 = Part.this;
                        part3.boundsTransition = 1.0f;
                        if (CollageLayoutView2.this.removingParts.contains(part3)) {
                            Part.this.imageReceiver.onDetachedFromWindow();
                            Part.this.destroyContent();
                            Part part4 = Part.this;
                            CollageLayoutView2.this.removingParts.remove(part4);
                        }
                        CollageLayoutView2.this.invalidate();
                    }
                });
                this.animator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.animator.setDuration(360L);
                this.animator.start();
            } else {
                CollageLayoutView2.this.layout(this.bounds, part);
                this.boundsTransition = 1.0f;
                if (part == null) {
                    this.imageReceiver.onDetachedFromWindow();
                    destroyContent();
                    CollageLayoutView2.this.removingParts.remove(this);
                }
            }
            CollageLayoutView2.this.invalidate();
            this.hasBounds = true;
        }
    }

    public CollageLayoutView2(Context context, BlurringShader.BlurManager blurManager, FrameLayout frameLayout, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentLayout = new CollageLayout(".");
        ArrayList arrayList = new ArrayList();
        this.parts = arrayList;
        this.removingParts = new ArrayList();
        Paint paint = new Paint(1);
        this.highlightPaint = paint;
        this.highlightPath = new Path();
        this.radii = new float[8];
        this.resetReordering = new Runnable() {
            @Override
            public final void run() {
                CollageLayoutView2.this.lambda$new$0();
            }
        };
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.animatedRows = new AnimatedFloat(this, 0L, 320L, cubicBezierInterpolator);
        this.animatedColumns = new AnimatedFloat[]{new AnimatedFloat(this, 0L, 320L, cubicBezierInterpolator), new AnimatedFloat(this, 0L, 320L, cubicBezierInterpolator), new AnimatedFloat(this, 0L, 320L, cubicBezierInterpolator), new AnimatedFloat(this, 0L, 320L, cubicBezierInterpolator), new AnimatedFloat(this, 0L, 320L, cubicBezierInterpolator)};
        this.animatedReordering = new AnimatedFloat(this, 0L, 320L, cubicBezierInterpolator);
        this.lefts = new float[5];
        this.rights = new float[5];
        this.rect = new RectF();
        this.clipPath = new Path();
        this.playing = true;
        this.restorePositionOnPlaying = true;
        this.syncRunnable = new Runnable() {
            @Override
            public final void run() {
                CollageLayoutView2.this.lambda$new$7();
            }
        };
        this.blurManager = blurManager;
        this.containerView = frameLayout;
        this.resourcesProvider = resourcesProvider;
        setBackgroundColor(-14737633);
        Part part = new Part();
        part.setPart((CollageLayout.Part) this.currentLayout.parts.get(0), false);
        part.setCurrent(true);
        if (this.attached) {
            part.imageReceiver.onAttachedToWindow();
        }
        arrayList.add(part);
        this.currentPart = part;
        this.nextPart = null;
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(-1);
        paint.setStrokeWidth(AndroidUtilities.dp(8.0f));
        int dp = AndroidUtilities.dp(300.0f);
        this.gradientWidth = dp;
        LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, dp, 0.0f, new int[]{0, -1, -1, 0}, new float[]{0.0f, 0.2f, 0.8f, 1.0f}, Shader.TileMode.CLAMP);
        this.gradient = linearGradient;
        this.gradientMatrix = new Matrix();
        paint.setShader(linearGradient);
        setWillNotDraw(false);
    }

    private void drawPart(android.graphics.Canvas r10, android.graphics.RectF r11, org.telegram.ui.Stories.recorder.CollageLayoutView2.Part r12) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.recorder.CollageLayoutView2.drawPart(android.graphics.Canvas, android.graphics.RectF, org.telegram.ui.Stories.recorder.CollageLayoutView2$Part):void");
    }

    private void drawView(Canvas canvas, View view, RectF rectF, float f) {
        Bitmap bitmap;
        if (view == null) {
            return;
        }
        float max = Math.max(rectF.width() / view.getWidth(), rectF.height() / view.getHeight());
        canvas.save();
        canvas.translate(rectF.centerX(), rectF.centerY());
        canvas.clipRect((-rectF.width()) / 2.0f, (-rectF.height()) / 2.0f, rectF.width() / 2.0f, rectF.height() / 2.0f);
        canvas.scale(max, max);
        canvas.translate((-view.getWidth()) / 2.0f, (-view.getHeight()) / 2.0f);
        if (AndroidUtilities.makingGlobalBlurBitmap) {
            TextureView textureView = view instanceof TextureView ? (TextureView) view : view instanceof CameraView ? ((CameraView) view).getTextureView() : null;
            if (textureView != null && (bitmap = textureView.getBitmap()) != null) {
                canvas.scale(view.getWidth() / bitmap.getWidth(), view.getHeight() / bitmap.getHeight());
                canvas.drawBitmap(bitmap, 0.0f, 0.0f, (Paint) null);
            }
        } else {
            view.draw(canvas);
        }
        if (f > 0.0f) {
            canvas.drawColor(Theme.multAlpha(-16777216, view.getAlpha() * f));
        }
        canvas.restore();
    }

    public void lambda$new$0() {
        if (this.reordering) {
            this.reordering = false;
            invalidate();
        }
    }

    public void lambda$new$7() {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.recorder.CollageLayoutView2.lambda$new$7():void");
    }

    public void lambda$onLongPress$1(Float f) {
        this.longPressedPart.content.videoVolume = f.floatValue();
        Part part = this.longPressedPart;
        VideoPlayerHolderBase videoPlayerHolderBase = part.videoPlayer;
        if (videoPlayerHolderBase != null) {
            videoPlayerHolderBase.setVolume(part.content.videoVolume);
        }
    }

    public void lambda$onLongPress$2() {
        retake(this.longPressedPart);
    }

    public void lambda$onLongPress$3() {
        delete(this.longPressedPart);
    }

    public static void lambda$onLongPress$4() {
    }

    public void lambda$onLongPress$5() {
        VideoPlayerHolderBase videoPlayerHolderBase;
        Part part = this.longPressedPart;
        if (part == null || (videoPlayerHolderBase = part.videoPlayer) == null) {
            return;
        }
        videoPlayerHolderBase.setVolume(0.0f);
    }

    public void layout(RectF rectF, CollageLayout.Part part) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (measuredWidth <= 0 || measuredHeight <= 0) {
            Point point = AndroidUtilities.displaySize;
            int i = point.x;
            measuredHeight = point.y;
            measuredWidth = i;
        }
        int[] iArr = part.layout.columns;
        int i2 = part.y;
        float f = measuredWidth / iArr[i2];
        float f2 = measuredHeight / r2.h;
        rectF.set(part.x * f, i2 * f2, f * (r8 + 1), f2 * (i2 + 1));
    }

    public void layoutOut(RectF rectF, CollageLayout.Part part) {
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        if (measuredWidth <= 0 || measuredHeight <= 0) {
            Point point = AndroidUtilities.displaySize;
            int i = point.x;
            measuredHeight = point.y;
            measuredWidth = i;
        }
        layout(rectF, part);
        float f = rectF.left;
        boolean z = f <= 0.0f;
        float f2 = rectF.top;
        boolean z2 = f2 <= 0.0f;
        float f3 = measuredWidth;
        boolean z3 = rectF.right >= f3;
        float f4 = measuredHeight;
        boolean z4 = rectF.bottom >= f4;
        if (z && z3 && !z2 && !z4) {
            rectF.offset(0.0f, f4 - f2);
            return;
        }
        if (z2 && z4 && !z && !z3) {
            rectF.offset(0.0f, f3 - f);
            return;
        }
        if (z3 && !z) {
            rectF.offset(rectF.width(), 0.0f);
        }
        if (!z4 || z2) {
            return;
        }
        rectF.offset(0.0f, rectF.height());
    }

    public void onLongPress() {
        VideoPlayerHolderBase videoPlayerHolderBase;
        if (this.reorderingTouch || this.preview) {
            return;
        }
        Part part = this.longPressedPart;
        if (part != null && (videoPlayerHolderBase = part.videoPlayer) != null) {
            videoPlayerHolderBase.setVolume(0.0f);
        }
        Part part2 = this.pressedPart;
        this.longPressedPart = part2;
        if (part2 == null || part2.content == null) {
            return;
        }
        Runnable runnable = this.cancelGestures;
        if (runnable != null) {
            runnable.run();
        }
        Part part3 = this.longPressedPart;
        VideoPlayerHolderBase videoPlayerHolderBase2 = part3.videoPlayer;
        if (videoPlayerHolderBase2 != null) {
            videoPlayerHolderBase2.setVolume(part3.content.videoVolume);
        }
        FrameLayout frameLayout = new FrameLayout(getContext());
        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.menu_lightbulb);
        imageView.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        frameLayout.addView(imageView, LayoutHelper.createFrame(24, 24.0f, 19, 12.0f, 12.0f, 12.0f, 12.0f));
        TextView textView = new TextView(getContext());
        textView.setText(LocaleController.getString(R.string.StoryCollageMenuHint));
        textView.setTextSize(1, 13.0f);
        textView.setTextColor(-1);
        frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 23, 47.0f, 8.0f, 24.0f, 8.0f));
        ItemOptions makeOptions = ItemOptions.makeOptions(this.containerView, this.resourcesProvider, this);
        if (this.longPressedPart.content.isVideo) {
            SliderView onValueChange = new SliderView(getContext(), 0).setMinMax(0.0f, 1.5f).setValue(this.longPressedPart.content.videoVolume).setOnValueChange(new Utilities.Callback() {
                @Override
                public final void run(Object obj) {
                    CollageLayoutView2.this.lambda$onLongPress$1((Float) obj);
                }
            });
            onValueChange.fixWidth = AndroidUtilities.dp(220.0f);
            makeOptions.addView(onValueChange).addSpaceGap();
        }
        makeOptions.setFixedWidth(220).add(R.drawable.menu_camera_retake, LocaleController.getString(R.string.StoreCollageRetake), new Runnable() {
            @Override
            public final void run() {
                CollageLayoutView2.this.lambda$onLongPress$2();
            }
        }).add(R.drawable.msg_delete, (CharSequence) LocaleController.getString(R.string.Delete), true, new Runnable() {
            @Override
            public final void run() {
                CollageLayoutView2.this.lambda$onLongPress$3();
            }
        }).addSpaceGap().addView(frameLayout, LayoutHelper.createLinear(220, -2)).setOnDismiss(new Runnable() {
            @Override
            public final void run() {
                CollageLayoutView2.lambda$onLongPress$4();
            }
        }).setGravity(1).allowCenter(true).setBlur(true).setRoundRadius(AndroidUtilities.dp(12.0f), AndroidUtilities.dp(10.0f)).setOnDismiss(new Runnable() {
            @Override
            public final void run() {
                CollageLayoutView2.this.lambda$onLongPress$5();
            }
        }).show();
        performHapticFeedback(0, 1);
    }

    public boolean cancelTouch() {
        if (this.pressedPart == null) {
            return false;
        }
        this.pressedPart = null;
        this.reorderingTouch = false;
        invalidate();
        Runnable runnable = this.onLongPressPart;
        if (runnable == null) {
            return true;
        }
        AndroidUtilities.cancelRunOnUIThread(runnable);
        this.onLongPressPart = null;
        return true;
    }

    public void clear(boolean z) {
        Iterator it = this.parts.iterator();
        while (it.hasNext()) {
            ((Part) it.next()).setContent(null);
        }
        updatePartsState();
    }

    public void delete(Part part) {
        if (part != null && this.parts.indexOf(part) >= 0) {
            CollageLayout collageLayout = this.currentLayout;
            CollageLayout delete = collageLayout.delete(collageLayout.parts.indexOf(part.part));
            if (delete.parts.size() <= 1) {
                clear(true);
                invalidate();
            }
            setLayout(delete, true);
            this.reordering = true;
            updatePartsState();
            invalidate();
            Runnable runnable = this.onResetState;
            if (runnable != null) {
                runnable.run();
            }
            onLayoutUpdate(delete);
        }
    }

    @Override
    protected void dispatchDraw(android.graphics.Canvas r19) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.recorder.CollageLayoutView2.dispatchDraw(android.graphics.Canvas):void");
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        Runnable runnable;
        if (!hasLayout() || this.preview) {
            return super.dispatchTouchEvent(motionEvent);
        }
        if (motionEvent.getPointerCount() > 1) {
            cancelTouch();
            return false;
        }
        Part partAt = getPartAt(motionEvent.getX(), motionEvent.getY());
        if (motionEvent.getAction() == 0) {
            this.tx = motionEvent.getX();
            this.ty = motionEvent.getY();
            this.reorderingTouch = false;
            this.dx = 0.0f;
            this.ldx = 0.0f;
            this.dy = 0.0f;
            this.ldy = 0.0f;
            this.pressedPart = partAt;
            if (partAt != null) {
                Runnable runnable2 = new Runnable() {
                    @Override
                    public final void run() {
                        CollageLayoutView2.this.onLongPress();
                    }
                };
                this.onLongPressPart = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, ViewConfiguration.getLongPressTimeout());
            }
        } else if (motionEvent.getAction() == 2) {
            if (MathUtils.distance(motionEvent.getX(), motionEvent.getY(), this.tx, this.ty) > AndroidUtilities.touchSlop * 1.2f && (runnable = this.onLongPressPart) != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.onLongPressPart = null;
            }
            if (!this.reorderingTouch && getFilledProgress() >= 1.0f && this.pressedPart != null && partAt != null && MathUtils.distance(motionEvent.getX(), motionEvent.getY(), this.tx, this.ty) > AndroidUtilities.touchSlop * 1.2f) {
                this.reorderingTouch = true;
                this.reorderingPart = this.pressedPart;
                this.dx = 0.0f;
                this.ldx = 0.0f;
                this.dy = 0.0f;
                this.ldy = 0.0f;
                invalidate();
                Runnable runnable3 = this.onLongPressPart;
                if (runnable3 != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable3);
                    this.onLongPressPart = null;
                }
            } else if (this.reorderingTouch && this.reorderingPart != null) {
                int partIndexAt = getPartIndexAt(motionEvent.getX(), motionEvent.getY());
                int indexOf = this.parts.indexOf(this.reorderingPart);
                if (partIndexAt >= 0 && indexOf >= 0 && partIndexAt != indexOf) {
                    swap(indexOf, partIndexAt);
                    float f = this.currentLayout.h;
                    float f2 = this.animatedColumns[this.reorderingPart.part.y].get();
                    this.rect.set((getMeasuredWidth() / f2) * r3.x, (getMeasuredHeight() / f) * r3.y, (getMeasuredWidth() / f2) * (r3.x + 1), (getMeasuredHeight() / f) * (r3.y + 1));
                    this.ldx = this.dx;
                    this.ldy = this.dy;
                    this.tx = this.rect.centerX();
                    this.ty = this.rect.centerY();
                }
                this.dx = motionEvent.getX() - this.tx;
                this.dy = motionEvent.getY() - this.ty;
                invalidate();
            } else if (this.pressedPart != partAt) {
                this.pressedPart = null;
                Runnable runnable4 = this.onLongPressPart;
                if (runnable4 != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable4);
                    this.onLongPressPart = null;
                }
                return true;
            }
        } else if (motionEvent.getAction() == 1) {
            if (this.pressedPart != null) {
                this.pressedPart = null;
                this.reorderingTouch = false;
                invalidate();
                Runnable runnable5 = this.onLongPressPart;
                if (runnable5 != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable5);
                    this.onLongPressPart = null;
                }
                return true;
            }
        } else if (motionEvent.getAction() == 3 && cancelTouch()) {
            return true;
        }
        return this.pressedPart != null || super.dispatchTouchEvent(motionEvent);
    }

    @Override
    protected boolean drawChild(Canvas canvas, View view, long j) {
        if (view == this.cameraView && AndroidUtilities.makingGlobalBlurBitmap) {
            return false;
        }
        return super.drawChild(canvas, view, j);
    }

    @Override
    public void drawScrim(Canvas canvas, float f) {
        Part part = this.longPressedPart;
        if (part != null) {
            CollageLayout.Part part2 = part.part;
            float f2 = part2.layout.h;
            float f3 = this.animatedColumns[part2.y].set(r0.columns[r3]);
            this.rect.set((getMeasuredWidth() / f3) * part2.x, (getMeasuredHeight() / f2) * part2.y, (getMeasuredWidth() / f3) * (part2.x + 1), (getMeasuredHeight() / f2) * (part2.y + 1));
            drawPart(canvas, this.rect, this.longPressedPart);
        }
    }

    public void forceNotRestorePosition() {
    }

    @Override
    public void getBounds(RectF rectF) {
        Part part = this.longPressedPart;
        if (part == null) {
            rectF.set(0.0f, 0.0f, getWidth(), getHeight());
            return;
        }
        CollageLayout.Part part2 = part.part;
        float f = part2.layout.h;
        float f2 = this.animatedColumns[part2.y].set(r1.columns[r4]);
        rectF.set((getMeasuredWidth() / f2) * part2.x, (getMeasuredHeight() / f) * part2.y, (getMeasuredWidth() / f2) * (part2.x + 1), (getMeasuredHeight() / f) * (part2.y + 1));
    }

    public ArrayList<StoryEntry> getContent() {
        ArrayList<StoryEntry> arrayList = new ArrayList<>();
        Iterator it = this.parts.iterator();
        while (it.hasNext()) {
            Part part = (Part) it.next();
            if (part.hasContent()) {
                arrayList.add(part.content);
            }
        }
        return arrayList;
    }

    public Part getCurrent() {
        return this.currentPart;
    }

    public long getDuration() {
        Part mainPart;
        if (!this.preview || (mainPart = getMainPart()) == null || mainPart.content == null) {
            return 1L;
        }
        return Math.max(Math.min(((float) mainPart.content.duration) * (mainPart.content.videoRight - mainPart.content.videoLeft), 59500L), 1L);
    }

    public int getFilledCount() {
        int i = 0;
        for (int i2 = 0; i2 < this.parts.size(); i2++) {
            if (((Part) this.parts.get(i2)).hasContent()) {
                i++;
            }
        }
        return i;
    }

    public float getFilledProgress() {
        return getFilledCount() / getTotalCount();
    }

    public CollageLayout getLayout() {
        return this.currentLayout;
    }

    public Part getMainPart() {
        Part part = null;
        if (!this.preview) {
            return null;
        }
        Iterator it = this.parts.iterator();
        long j = 0;
        while (it.hasNext()) {
            Part part2 = (Part) it.next();
            if (part2.content != null && part2.content.isVideo) {
                long j2 = part2.content.duration;
                VideoPlayerHolderBase videoPlayerHolderBase = part2.videoPlayer;
                if (videoPlayerHolderBase != null && videoPlayerHolderBase.getDuration() > 0) {
                    j2 = part2.videoPlayer.getDuration();
                }
                if (j2 > j) {
                    part = part2;
                    j = j2;
                }
            }
        }
        return part;
    }

    public Part getNext() {
        return this.nextPart;
    }

    public ArrayList<Integer> getOrder() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = 0; i < this.parts.size(); i++) {
            arrayList.add(Integer.valueOf(((Part) this.parts.get(i)).index));
        }
        return arrayList;
    }

    public Part getPartAt(float f, float f2) {
        float f3 = this.animatedRows.get();
        for (int i = 0; i < this.parts.size(); i++) {
            Part part = (Part) this.parts.get(i);
            float f4 = this.animatedColumns[part.part.y].get();
            this.rect.set((getMeasuredWidth() / f4) * r3.x, (getMeasuredHeight() / f3) * r3.y, (getMeasuredWidth() / f4) * (r3.x + 1), (getMeasuredHeight() / f3) * (r3.y + 1));
            if (this.rect.contains(f, f2)) {
                return part;
            }
        }
        return null;
    }

    public int getPartIndexAt(float f, float f2) {
        float f3 = this.animatedRows.get();
        for (int i = 0; i < this.parts.size(); i++) {
            float f4 = this.animatedColumns[((Part) this.parts.get(i)).part.y].get();
            this.rect.set((getMeasuredWidth() / f4) * r2.x, (getMeasuredHeight() / f3) * r2.y, (getMeasuredWidth() / f4) * (r2.x + 1), (getMeasuredHeight() / f3) * (r2.y + 1));
            if (this.rect.contains(f, f2)) {
                return i;
            }
        }
        return -1;
    }

    public long getPosition() {
        if (!this.preview) {
            return 0L;
        }
        if (!this.playing) {
            return this.lastPausedPosition;
        }
        long currentTimeMillis = System.currentTimeMillis();
        long j = currentTimeMillis - this.previewStartTime;
        if (j > getDuration()) {
            this.previewStartTime = currentTimeMillis - (j % getDuration());
        }
        return j;
    }

    public long getPositionWithOffset() {
        if (!this.preview) {
            return 0L;
        }
        getPosition();
        Part mainPart = getMainPart();
        return getPosition() + (mainPart != null ? mainPart.content.videoOffset + (mainPart.content.videoLeft * ((float) mainPart.content.duration)) : 0L);
    }

    public int getTotalCount() {
        return this.parts.size();
    }

    public boolean hasContent() {
        Iterator it = this.parts.iterator();
        while (it.hasNext()) {
            if (((Part) it.next()).hasContent()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasLayout() {
        return this.currentLayout.parts.size() > 1;
    }

    public boolean hasVideo() {
        Iterator it = this.parts.iterator();
        while (it.hasNext()) {
            Part part = (Part) it.next();
            if (part.content != null && part.content.isVideo) {
                return true;
            }
        }
        return false;
    }

    public void highlight(int i) {
        Iterator it = this.parts.iterator();
        while (it.hasNext()) {
            Part part = (Part) it.next();
            if (part.index == i) {
                part.highlightAnimated.set(1.0f, true);
                invalidate();
                return;
            }
        }
    }

    public boolean isPlaying() {
        return this.playing;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        for (int i = 0; i < this.parts.size(); i++) {
            ((Part) this.parts.get(i)).imageReceiver.onAttachedToWindow();
        }
        this.attached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (int i = 0; i < this.parts.size(); i++) {
            ((Part) this.parts.get(i)).imageReceiver.onDetachedFromWindow();
        }
        this.attached = false;
        AndroidUtilities.cancelRunOnUIThread(this.syncRunnable);
    }

    protected abstract void onLayoutUpdate(CollageLayout collageLayout);

    @Override
    protected void onMeasure(int i, int i2) {
        int makeMeasureSpec;
        int makeMeasureSpec2;
        Part part;
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        setMeasuredDimension(size, size2);
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            View childAt = getChildAt(i3);
            if (childAt != this.cameraView) {
                int i4 = 0;
                while (true) {
                    if (i4 >= this.parts.size()) {
                        part = null;
                        break;
                    } else {
                        if (childAt == ((Part) this.parts.get(i4)).textureView) {
                            part = (Part) this.parts.get(i4);
                            break;
                        }
                        i4++;
                    }
                }
                if (part != null && part.content != null && part.content.width > 0 && part.content.height > 0) {
                    int i5 = part.content.width;
                    int i6 = part.content.height;
                    if (part.content.orientation % 90 != 1) {
                        i6 = i5;
                        i5 = i6;
                    }
                    float f = i6;
                    float f2 = i5;
                    float min = Math.min(1.0f, Math.max(f / size, f2 / size2));
                    makeMeasureSpec = View.MeasureSpec.makeMeasureSpec((int) (f * min), 1073741824);
                    makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec((int) (f2 * min), 1073741824);
                    childAt.measure(makeMeasureSpec, makeMeasureSpec2);
                }
            }
            makeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, 1073741824);
            makeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(size2, 1073741824);
            childAt.measure(makeMeasureSpec, makeMeasureSpec2);
        }
    }

    public boolean push(StoryEntry storyEntry) {
        if (storyEntry != null && storyEntry.isVideo) {
            Iterator it = this.parts.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                Part part = (Part) it.next();
                if (part.content != null && part.content.isVideo && part.content.videoVolume > 0.0f) {
                    storyEntry.videoVolume = 0.0f;
                    break;
                }
            }
        }
        Part part2 = this.currentPart;
        if (part2 != null) {
            part2.setContent(storyEntry);
        }
        updatePartsState();
        requestLayout();
        return this.currentPart == null;
    }

    public void retake(Part part) {
        if (part == null) {
            return;
        }
        part.setContent(null);
        updatePartsState();
        invalidate();
        Runnable runnable = this.onResetState;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void seekTo(long j, boolean z) {
        if (this.preview) {
            long clamp = Utilities.clamp(j, getDuration(), 0L);
            if (!this.playing) {
                this.lastPausedPosition = clamp;
            }
            this.previewStartTime = System.currentTimeMillis() - clamp;
            this.fastSeek = z;
            AndroidUtilities.cancelRunOnUIThread(this.syncRunnable);
            this.syncRunnable.run();
        }
    }

    public void set(StoryEntry storyEntry, boolean z) {
        if (storyEntry == null || storyEntry.collageContent == null) {
            clear(true);
            return;
        }
        setLayout(storyEntry.collage, z);
        for (int i = 0; i < this.parts.size(); i++) {
            ((Part) this.parts.get(i)).setContent((StoryEntry) storyEntry.collageContent.get(i));
        }
    }

    public void setCameraNeedsBlur(boolean z) {
        if (this.needsBlur == z) {
            return;
        }
        this.needsBlur = z;
        updateCameraNeedsBlur();
    }

    public void setCameraView(CameraView cameraView) {
        CameraView cameraView2 = this.cameraView;
        if (cameraView2 != cameraView && cameraView2 != null) {
            cameraView2.unlistenDraw(new Runnable() {
                @Override
                public final void run() {
                    CollageLayoutView2.this.invalidate();
                }
            });
            AndroidUtilities.removeFromParent(this.cameraView);
            this.cameraView = null;
            updateCameraNeedsBlur();
        }
        this.cameraView = cameraView;
        if (cameraView != null) {
            addView(cameraView, LayoutHelper.createFrame(-1, -1, 119));
        }
        CameraView cameraView3 = this.cameraView;
        if (cameraView3 != null) {
            cameraView3.unlistenDraw(new Runnable() {
                @Override
                public final void run() {
                    CollageLayoutView2.this.invalidate();
                }
            });
        }
        this.cameraView = cameraView;
        if (cameraView != null) {
            cameraView.listenDraw(new Runnable() {
                @Override
                public final void run() {
                    CollageLayoutView2.this.invalidate();
                }
            });
        }
        updateCameraNeedsBlur();
        invalidate();
    }

    public void setCancelGestures(Runnable runnable) {
        this.cancelGestures = runnable;
    }

    public void setLayout(CollageLayout collageLayout, boolean z) {
        if (collageLayout == null) {
            collageLayout = new CollageLayout(".");
        }
        this.currentLayout = collageLayout;
        AndroidUtilities.cancelRunOnUIThread(this.resetReordering);
        int i = 0;
        while (i < Math.max(collageLayout.parts.size(), this.parts.size())) {
            CollageLayout.Part part = i < collageLayout.parts.size() ? (CollageLayout.Part) collageLayout.parts.get(i) : null;
            Part part2 = i < this.parts.size() ? (Part) this.parts.get(i) : null;
            if (part2 == null && part != null) {
                Part part3 = new Part();
                if (this.attached) {
                    part3.imageReceiver.onAttachedToWindow();
                }
                part3.setPart(part, z);
                this.parts.add(part3);
            } else if (part != null) {
                part2.setPart(part, z);
            } else if (part2 != null) {
                this.removingParts.add(part2);
                this.parts.remove(part2);
                part2.setPart(null, z);
                i--;
            }
            i++;
        }
        updatePartsState();
        invalidate();
        if (z) {
            AndroidUtilities.runOnUIThread(this.resetReordering, 360L);
        }
    }

    public void setMuted(boolean z) {
        if (this.isMuted == z) {
            return;
        }
        this.isMuted = z;
    }

    public void setPlaying(boolean z) {
        boolean z2 = this.restorePositionOnPlaying;
        this.restorePositionOnPlaying = true;
        if (this.playing == z) {
            return;
        }
        this.playing = z;
        if (!z) {
            this.lastPausedPosition = getPosition();
        } else if (z2) {
            seekTo(this.lastPausedPosition, false);
        } else {
            this.fastSeek = false;
        }
        AndroidUtilities.cancelRunOnUIThread(this.syncRunnable);
        this.syncRunnable.run();
    }

    public void setPreview(boolean z) {
        if (this.preview == z) {
            return;
        }
        this.preview = z;
        if (z) {
            BlurringShader.BlurManager blurManager = this.blurManager;
            if (blurManager != null) {
                blurManager.invalidate();
            }
            for (int i = 0; i < this.parts.size(); i++) {
                ((Part) this.parts.get(i)).index = i;
            }
        }
        this.fastSeek = false;
        this.lastPausedPosition = 0L;
        Iterator it = this.parts.iterator();
        while (it.hasNext()) {
            Part part = (Part) it.next();
            VideoPlayerHolderBase videoPlayerHolderBase = part.videoPlayer;
            if (videoPlayerHolderBase != null) {
                videoPlayerHolderBase.setAudioEnabled(z, true);
                if (!z || this.playing) {
                    part.videoPlayer.play();
                } else {
                    part.videoPlayer.pause();
                }
            }
        }
        AndroidUtilities.cancelRunOnUIThread(this.syncRunnable);
        if (z) {
            this.previewStartTime = System.currentTimeMillis();
            AndroidUtilities.runOnUIThread(this.syncRunnable, 1000.0f / AndroidUtilities.screenRefreshRate);
        }
    }

    public void setPreviewView(PreviewView previewView) {
        this.previewView = previewView;
    }

    public void setResetState(Runnable runnable) {
        this.onResetState = runnable;
    }

    public void setTimelineView(TimelineView timelineView) {
        this.timelineView = timelineView;
    }

    public void swap(int i, int i2) {
        Collections.swap(this.parts, i, i2);
        setLayout(this.currentLayout, true);
        this.reordering = true;
        invalidate();
    }

    public void updateCameraNeedsBlur() {
        CameraView cameraView = this.cameraView;
        boolean z = cameraView != null && this.needsBlur;
        if (z == (this.cameraViewBlurRenderNode != null)) {
            return;
        }
        this.cameraViewBlurRenderNode = z ? cameraView.getBlurRenderNode() : null;
    }

    public void updatePartsState() {
        this.currentPart = null;
        this.nextPart = null;
        int i = 0;
        while (true) {
            if (i >= this.parts.size()) {
                break;
            }
            Part part = (Part) this.parts.get(i);
            if (!part.hasContent()) {
                if (this.currentPart != null) {
                    this.nextPart = part;
                    break;
                }
                this.currentPart = part;
            }
            i++;
        }
        for (int i2 = 0; i2 < this.parts.size(); i2++) {
            Part part2 = (Part) this.parts.get(i2);
            part2.setCurrent(part2 == this.currentPart);
        }
    }
}
