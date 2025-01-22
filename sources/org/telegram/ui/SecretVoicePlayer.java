package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.AudioVisualizerDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EarListener;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarWaveform;
import org.telegram.ui.Components.ThanosEffect;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.SecretVoicePlayer;
import org.telegram.ui.Stories.recorder.HintView2;

public class SecretVoicePlayer extends Dialog {
    private AudioVisualizerDrawable audioVisualizerDrawable;
    private AlertDialog backDialog;
    private Bitmap blurBitmap;
    private Paint blurBitmapPaint;
    private BitmapShader blurBitmapShader;
    private Matrix blurMatrix;
    private ChatMessageCell cell;
    private Runnable checkTimeRunnable;
    private float clipBottom;
    private float clipTop;
    private Runnable closeAction;
    private TextView closeButton;
    private FrameLayout containerView;
    public final Context context;
    private boolean dismissing;
    private float dtx;
    private float dty;
    private EarListener earListener;
    private boolean hasDestTranslation;
    private boolean hasTranslation;
    private float heightdiff;
    private HintView2 hintView;
    private final Rect insets;
    private boolean isRound;
    private MessageObject messageObject;
    private ChatMessageCell myCell;
    private boolean open;
    private ValueAnimator open2Animator;
    private Runnable openAction;
    private ValueAnimator openAnimator;
    private float openProgress;
    private float openProgress2;
    private VideoPlayer player;
    private float progress;
    private final RectF rect;
    private boolean renderedFirstFrame;
    private Theme.ResourcesProvider resourcesProvider;
    private boolean setCellInvisible;
    private TextureView textureView;
    private ThanosEffect thanosEffect;
    private float tx;
    private float ty;
    private FrameLayout windowView;

    public class AnonymousClass6 implements VideoPlayer.VideoPlayerDelegate {
        AnonymousClass6() {
        }

        public void lambda$onRenderedFirstFrame$0() {
            SecretVoicePlayer.this.renderedFirstFrame = true;
            SecretVoicePlayer.this.myCell.invalidate();
        }

        @Override
        public void onError(VideoPlayer videoPlayer, Exception exc) {
        }

        @Override
        public void onRenderedFirstFrame() {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SecretVoicePlayer.AnonymousClass6.this.lambda$onRenderedFirstFrame$0();
                }
            });
        }

        @Override
        public void onRenderedFirstFrame(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onRenderedFirstFrame(this, eventTime);
        }

        @Override
        public void onSeekFinished(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekFinished(this, eventTime);
        }

        @Override
        public void onSeekStarted(AnalyticsListener.EventTime eventTime) {
            VideoPlayer.VideoPlayerDelegate.CC.$default$onSeekStarted(this, eventTime);
        }

        @Override
        public void onStateChanged(boolean z, int i) {
            if (i == 4) {
                SecretVoicePlayer.this.dismiss();
            } else {
                AndroidUtilities.cancelRunOnUIThread(SecretVoicePlayer.this.checkTimeRunnable);
                AndroidUtilities.runOnUIThread(SecretVoicePlayer.this.checkTimeRunnable, 16L);
            }
        }

        @Override
        public boolean onSurfaceDestroyed(SurfaceTexture surfaceTexture) {
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
        }

        @Override
        public void onVideoSizeChanged(int i, int i2, int i3, float f) {
        }
    }

    public SecretVoicePlayer(Context context) {
        super(context, R.style.TransparentDialog);
        this.insets = new Rect();
        this.rect = new RectF();
        this.clipTop = 0.0f;
        this.clipBottom = 0.0f;
        this.checkTimeRunnable = new Runnable() {
            @Override
            public final void run() {
                SecretVoicePlayer.this.checkTime();
            }
        };
        this.progress = 0.0f;
        this.dismissing = false;
        this.context = context;
        FrameLayout frameLayout = new FrameLayout(context) {
            @Override
            protected void dispatchDraw(Canvas canvas) {
                if (SecretVoicePlayer.this.openProgress > 0.0f && SecretVoicePlayer.this.blurBitmapPaint != null) {
                    SecretVoicePlayer.this.blurMatrix.reset();
                    float width = getWidth() / SecretVoicePlayer.this.blurBitmap.getWidth();
                    SecretVoicePlayer.this.blurMatrix.postScale(width, width);
                    SecretVoicePlayer.this.blurBitmapShader.setLocalMatrix(SecretVoicePlayer.this.blurMatrix);
                    SecretVoicePlayer.this.blurBitmapPaint.setAlpha((int) (SecretVoicePlayer.this.openProgress * 255.0f));
                    canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), SecretVoicePlayer.this.blurBitmapPaint);
                }
                if (SecretVoicePlayer.this.setCellInvisible && SecretVoicePlayer.this.cell != null) {
                    SecretVoicePlayer.this.cell.setVisibility(4);
                    SecretVoicePlayer.this.setCellInvisible = false;
                }
                super.dispatchDraw(canvas);
            }

            @Override
            public boolean dispatchKeyEventPreIme(KeyEvent keyEvent) {
                if (keyEvent == null || keyEvent.getKeyCode() != 4 || keyEvent.getAction() != 1) {
                    return super.dispatchKeyEventPreIme(keyEvent);
                }
                SecretVoicePlayer.this.dismiss();
                return true;
            }

            @Override
            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                SecretVoicePlayer.this.setupTranslation();
            }
        };
        this.windowView = frameLayout;
        frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                SecretVoicePlayer.this.lambda$new$0(view);
            }
        });
        FrameLayout frameLayout2 = new FrameLayout(context) {
            private final Path clipPath = new Path();

            @Override
            protected boolean drawChild(Canvas canvas, View view, long j) {
                if (view == SecretVoicePlayer.this.myCell || view == SecretVoicePlayer.this.hintView) {
                    canvas.save();
                    canvas.clipRect(0.0f, AndroidUtilities.lerp(SecretVoicePlayer.this.clipTop, 0.0f, SecretVoicePlayer.this.openProgress), getWidth(), AndroidUtilities.lerp(SecretVoicePlayer.this.clipBottom, getHeight(), SecretVoicePlayer.this.openProgress));
                } else {
                    if (view != SecretVoicePlayer.this.textureView) {
                        return super.drawChild(canvas, view, j);
                    }
                    canvas.save();
                    this.clipPath.rewind();
                    this.clipPath.addCircle(SecretVoicePlayer.this.myCell.getX() + SecretVoicePlayer.this.rect.centerX(), SecretVoicePlayer.this.myCell.getY() + SecretVoicePlayer.this.rect.centerY(), SecretVoicePlayer.this.rect.width() / 2.0f, Path.Direction.CW);
                    canvas.clipPath(this.clipPath);
                    canvas.clipRect(0.0f, AndroidUtilities.lerp(SecretVoicePlayer.this.clipTop, 0.0f, SecretVoicePlayer.this.openProgress), getWidth(), AndroidUtilities.lerp(SecretVoicePlayer.this.clipBottom, getHeight(), SecretVoicePlayer.this.openProgress));
                    canvas.translate(-SecretVoicePlayer.this.textureView.getX(), -SecretVoicePlayer.this.textureView.getY());
                    canvas.translate(SecretVoicePlayer.this.myCell.getX() + SecretVoicePlayer.this.rect.left, SecretVoicePlayer.this.myCell.getY() + SecretVoicePlayer.this.rect.top);
                    canvas.scale(SecretVoicePlayer.this.rect.width() / SecretVoicePlayer.this.textureView.getMeasuredWidth(), SecretVoicePlayer.this.rect.height() / SecretVoicePlayer.this.textureView.getMeasuredHeight(), SecretVoicePlayer.this.textureView.getX(), SecretVoicePlayer.this.textureView.getY());
                }
                boolean drawChild = super.drawChild(canvas, view, j);
                canvas.restore();
                return drawChild;
            }
        };
        this.containerView = frameLayout2;
        frameLayout2.setClipToPadding(false);
        this.windowView.addView(this.containerView, LayoutHelper.createFrame(-1, -1, 119));
        if (Build.VERSION.SDK_INT >= 21) {
            this.windowView.setFitsSystemWindows(true);
            this.windowView.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
                @Override
                public WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                    int systemWindowInsetLeft;
                    int systemWindowInsetTop;
                    int systemWindowInsetRight;
                    int systemWindowInsetBottom;
                    WindowInsets consumeSystemWindowInsets;
                    WindowInsets windowInsets2;
                    Insets insets;
                    int i;
                    int i2;
                    int i3;
                    int i4;
                    int i5 = Build.VERSION.SDK_INT;
                    if (i5 >= 30) {
                        insets = windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout() | WindowInsetsCompat.Type.systemBars());
                        Rect rect = SecretVoicePlayer.this.insets;
                        i = insets.left;
                        i2 = insets.top;
                        i3 = insets.right;
                        i4 = insets.bottom;
                        rect.set(i, i2, i3, i4);
                    } else {
                        Rect rect2 = SecretVoicePlayer.this.insets;
                        systemWindowInsetLeft = windowInsets.getSystemWindowInsetLeft();
                        systemWindowInsetTop = windowInsets.getSystemWindowInsetTop();
                        systemWindowInsetRight = windowInsets.getSystemWindowInsetRight();
                        systemWindowInsetBottom = windowInsets.getSystemWindowInsetBottom();
                        rect2.set(systemWindowInsetLeft, systemWindowInsetTop, systemWindowInsetRight, systemWindowInsetBottom);
                    }
                    SecretVoicePlayer.this.containerView.setPadding(SecretVoicePlayer.this.insets.left, SecretVoicePlayer.this.insets.top, SecretVoicePlayer.this.insets.right, SecretVoicePlayer.this.insets.bottom);
                    SecretVoicePlayer.this.windowView.requestLayout();
                    if (i5 >= 30) {
                        windowInsets2 = WindowInsets.CONSUMED;
                        return windowInsets2;
                    }
                    consumeSystemWindowInsets = windowInsets.consumeSystemWindowInsets();
                    return consumeSystemWindowInsets;
                }
            });
        }
        if (SharedConfig.raiseToListen) {
            this.earListener = new EarListener(context);
        }
    }

    private void animateOpenTo(final boolean z, final Runnable runnable) {
        ValueAnimator valueAnimator = this.openAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator valueAnimator2 = this.open2Animator;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
        }
        setupTranslation();
        ValueAnimator ofFloat = ValueAnimator.ofFloat(this.openProgress, z ? 1.0f : 0.0f);
        this.openAnimator = ofFloat;
        ofFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                SecretVoicePlayer.this.lambda$animateOpenTo$8(z, valueAnimator3);
            }
        });
        this.openAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                SecretVoicePlayer.this.openProgress = z ? 1.0f : 0.0f;
                SecretVoicePlayer.this.windowView.invalidate();
                SecretVoicePlayer.this.containerView.invalidate();
                SecretVoicePlayer.this.updateTranslation();
                if (SecretVoicePlayer.this.closeButton != null) {
                    SecretVoicePlayer.this.closeButton.setAlpha(SecretVoicePlayer.this.openProgress);
                }
                if (SecretVoicePlayer.this.isRound) {
                    SecretVoicePlayer.this.myCell.invalidate();
                }
                if (!SecretVoicePlayer.this.isRound && SecretVoicePlayer.this.myCell != null && SecretVoicePlayer.this.myCell.getSeekBarWaveform() != null) {
                    SecretVoicePlayer.this.myCell.getSeekBarWaveform().setExplosionRate(SecretVoicePlayer.this.openProgress);
                }
                Runnable runnable2 = runnable;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        });
        long j = (z || this.closeAction != null) ? 520L : 330L;
        ValueAnimator valueAnimator3 = this.openAnimator;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        valueAnimator3.setInterpolator(cubicBezierInterpolator);
        this.openAnimator.setDuration(j);
        this.openAnimator.start();
        ValueAnimator ofFloat2 = ValueAnimator.ofFloat(this.openProgress2, z ? 1.0f : 0.0f);
        this.open2Animator = ofFloat2;
        ofFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public final void onAnimationUpdate(ValueAnimator valueAnimator4) {
                SecretVoicePlayer.this.lambda$animateOpenTo$9(valueAnimator4);
            }
        });
        this.open2Animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                SecretVoicePlayer.this.openProgress2 = z ? 1.0f : 0.0f;
                if (SecretVoicePlayer.this.isRound) {
                    SecretVoicePlayer.this.myCell.invalidate();
                }
            }
        });
        this.open2Animator.setDuration(((float) j) * 1.5f);
        this.open2Animator.setInterpolator(cubicBezierInterpolator);
        this.open2Animator.start();
    }

    public void checkTime() {
        VideoPlayer videoPlayer = this.player;
        if (videoPlayer == null) {
            return;
        }
        this.progress = ((float) videoPlayer.getCurrentPosition()) / ((float) this.player.getDuration());
        ChatMessageCell chatMessageCell = this.myCell;
        if (chatMessageCell != null) {
            chatMessageCell.overrideDuration((this.player.getDuration() - this.player.getCurrentPosition()) / 1000);
            this.myCell.updatePlayingMessageProgress();
            SeekBarWaveform seekBarWaveform = this.myCell.getSeekBarWaveform();
            if (seekBarWaveform != null) {
                seekBarWaveform.explodeAt(this.progress);
            }
        }
        if (this.player.isPlaying()) {
            AndroidUtilities.cancelRunOnUIThread(this.checkTimeRunnable);
            AndroidUtilities.runOnUIThread(this.checkTimeRunnable, 16L);
        }
    }

    public void lambda$animateOpenTo$8(boolean z, ValueAnimator valueAnimator) {
        ChatMessageCell chatMessageCell;
        this.openProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.windowView.invalidate();
        this.containerView.invalidate();
        if (this.isRound) {
            this.myCell.invalidate();
        }
        updateTranslation();
        TextView textView = this.closeButton;
        if (textView != null) {
            textView.setAlpha(this.openProgress);
        }
        if (this.isRound || (chatMessageCell = this.myCell) == null || chatMessageCell.getSeekBarWaveform() == null) {
            return;
        }
        this.myCell.getSeekBarWaveform().setExplosionRate((z ? CubicBezierInterpolator.EASE_OUT : CubicBezierInterpolator.EASE_IN).getInterpolation(Utilities.clamp(this.openProgress * 1.25f, 1.0f, 0.0f)));
    }

    public void lambda$animateOpenTo$9(ValueAnimator valueAnimator) {
        this.openProgress2 = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (this.isRound) {
            this.myCell.invalidate();
        }
    }

    public void lambda$dismiss$5() {
        super.dismiss();
    }

    public void lambda$dismiss$6() {
        if (this.thanosEffect == null) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    SecretVoicePlayer.this.lambda$dismiss$5();
                }
            });
            ChatMessageCell chatMessageCell = this.cell;
            if (chatMessageCell != null) {
                chatMessageCell.setVisibility(0);
                this.cell.invalidate();
            }
        }
        MediaController.getInstance().tryResumePausedAudio();
    }

    public void lambda$dismiss$7() {
        super.dismiss();
    }

    public void lambda$new$0(View view) {
        if (this.closeAction == null) {
            dismiss();
        }
    }

    public void lambda$onBackPressed$3(AlertDialog alertDialog, int i) {
        AlertDialog alertDialog2 = this.backDialog;
        if (alertDialog2 != null) {
            alertDialog2.dismiss();
        }
    }

    public void lambda$onBackPressed$4(AlertDialog alertDialog, int i) {
        AlertDialog alertDialog2 = this.backDialog;
        if (alertDialog2 != null) {
            alertDialog2.dismiss();
            this.backDialog = null;
        }
        dismiss();
    }

    public void lambda$prepareBlur$1(View view, Bitmap bitmap) {
        if (view != null) {
            view.setVisibility(0);
        }
        this.blurBitmap = bitmap;
        Paint paint = new Paint(1);
        this.blurBitmapPaint = paint;
        Bitmap bitmap2 = this.blurBitmap;
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        BitmapShader bitmapShader = new BitmapShader(bitmap2, tileMode, tileMode);
        this.blurBitmapShader = bitmapShader;
        paint.setShader(bitmapShader);
        ColorMatrix colorMatrix = new ColorMatrix();
        AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, Theme.isCurrentThemeDark() ? 0.05f : 0.25f);
        AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, Theme.isCurrentThemeDark() ? -0.02f : -0.04f);
        this.blurBitmapPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        this.blurMatrix = new Matrix();
    }

    public void lambda$setCell$2(View view) {
        dismiss();
    }

    private void prepareBlur(final View view) {
        if (view != null) {
            view.setVisibility(4);
        }
        AndroidUtilities.makeGlobalBlurBitmap(new Utilities.Callback() {
            @Override
            public final void run(Object obj) {
                SecretVoicePlayer.this.lambda$prepareBlur$1(view, (Bitmap) obj);
            }
        }, 14.0f);
    }

    public void setupTranslation() {
        if (this.hasTranslation || this.windowView.getWidth() <= 0) {
            return;
        }
        ChatMessageCell chatMessageCell = this.cell;
        if (chatMessageCell != null) {
            int[] iArr = new int[2];
            chatMessageCell.getLocationOnScreen(iArr);
            float f = iArr[0] - this.insets.left;
            int width = this.windowView.getWidth();
            Rect rect = this.insets;
            this.tx = f - ((((width - rect.left) - rect.right) - this.cell.getWidth()) / 2.0f);
            float f2 = iArr[1] - this.insets.top;
            int height = this.windowView.getHeight();
            Rect rect2 = this.insets;
            this.ty = f2 - (((((height - rect2.top) - rect2.bottom) - this.cell.getHeight()) - this.heightdiff) / 2.0f);
            if (!this.hasDestTranslation) {
                this.hasDestTranslation = true;
                this.dtx = 0.0f;
                float clamp = (Utilities.clamp(iArr[1] + (this.cell.getHeight() / 2.0f), this.windowView.getHeight() * 0.7f, this.windowView.getHeight() * 0.3f) - (this.cell.getHeight() / 2.0f)) - ((this.windowView.getHeight() - this.cell.getHeight()) / 2.0f);
                this.dty = clamp;
                if (this.isRound) {
                    this.dty = 0.0f;
                } else {
                    this.dty = AndroidUtilities.lerp(0.0f, clamp, 0.78f);
                }
            }
            updateTranslation();
        } else {
            this.ty = 0.0f;
            this.tx = 0.0f;
        }
        this.hasTranslation = true;
    }

    public void updateTranslation() {
        if (this.thanosEffect != null) {
            return;
        }
        this.myCell.setTranslationX(AndroidUtilities.lerp(this.tx, this.dtx, this.openProgress));
        this.myCell.setTranslationY(AndroidUtilities.lerp(this.ty, this.dty, this.openProgress));
        HintView2 hintView2 = this.hintView;
        if (hintView2 != null) {
            hintView2.setTranslationX(AndroidUtilities.lerp(this.tx, this.dtx, this.openProgress));
            this.hintView.setTranslationY(AndroidUtilities.lerp(this.ty, this.dty, this.openProgress));
        }
    }

    @Override
    public void dismiss() {
        ChatMessageCell chatMessageCell;
        if (this.dismissing) {
            return;
        }
        AlertDialog alertDialog = this.backDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.backDialog = null;
        }
        this.dismissing = true;
        HintView2 hintView2 = this.hintView;
        if (hintView2 != null) {
            hintView2.hide();
        }
        VideoPlayer videoPlayer = this.player;
        if (videoPlayer != null) {
            videoPlayer.pause();
            this.player.releasePlayer(true);
            this.player = null;
        }
        if (!this.isRound && (chatMessageCell = this.myCell) != null && chatMessageCell.getSeekBarWaveform() != null) {
            this.myCell.getSeekBarWaveform().setExplosionRate(this.openProgress);
        }
        this.hasTranslation = false;
        setupTranslation();
        this.open = false;
        animateOpenTo(false, new Runnable() {
            @Override
            public final void run() {
                SecretVoicePlayer.this.lambda$dismiss$6();
            }
        });
        this.windowView.invalidate();
        Runnable runnable = this.closeAction;
        if (runnable != null) {
            ChatMessageCell chatMessageCell2 = this.cell;
            if (chatMessageCell2 != null) {
                chatMessageCell2.makeVisibleAfterChange = true;
            }
            AndroidUtilities.runOnUIThread(runnable);
            this.closeAction = null;
            ThanosEffect thanosEffect = new ThanosEffect(this.context, null);
            this.thanosEffect = thanosEffect;
            this.windowView.addView(thanosEffect, LayoutHelper.createFrame(-1, -1, 119));
            this.thanosEffect.animate(this.myCell, 1.5f, new Runnable() {
                @Override
                public final void run() {
                    SecretVoicePlayer.this.lambda$dismiss$7();
                }
            });
            WindowManager.LayoutParams attributes = getWindow().getAttributes();
            attributes.flags |= 16;
            getWindow().setAttributes(attributes);
        }
        EarListener earListener = this.earListener;
        if (earListener != null) {
            earListener.detach();
        }
    }

    public boolean isShown() {
        return !this.dismissing;
    }

    @Override
    public void onBackPressed() {
        MessageObject messageObject;
        AlertDialog alertDialog = this.backDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.backDialog = null;
            return;
        }
        if (this.dismissing || (messageObject = this.messageObject) == null || messageObject.isOutOwner()) {
            super.onBackPressed();
            return;
        }
        AlertDialog create = new AlertDialog.Builder(getContext(), this.resourcesProvider).setTitle(LocaleController.getString(this.isRound ? R.string.VideoOnceCloseTitle : R.string.VoiceOnceCloseTitle)).setMessage(LocaleController.getString(this.isRound ? R.string.VideoOnceCloseMessage : R.string.VoiceOnceCloseMessage)).setPositiveButton(LocaleController.getString(R.string.Continue), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog2, int i) {
                SecretVoicePlayer.this.lambda$onBackPressed$3(alertDialog2, i);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Delete), new AlertDialog.OnButtonClickListener() {
            @Override
            public final void onClick(AlertDialog alertDialog2, int i) {
                SecretVoicePlayer.this.lambda$onBackPressed$4(alertDialog2, i);
            }
        }).create();
        this.backDialog = create;
        create.show();
        TextView textView = (TextView) this.backDialog.getButton(-2);
        if (textView != null) {
            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
        }
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Window window = getWindow();
        window.setWindowAnimations(R.style.DialogNoAnimation);
        setContentView(this.windowView, new ViewGroup.LayoutParams(-1, -1));
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = -1;
        attributes.height = -1;
        attributes.gravity = 119;
        attributes.dimAmount = 0.0f;
        int i = attributes.flags & (-3);
        attributes.softInputMode = 48;
        attributes.flags = 131072 | i;
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 21) {
            attributes.flags = i | (-2013069056);
        }
        if (!BuildVars.DEBUG_PRIVATE_VERSION) {
            attributes.flags |= 8192;
        }
        attributes.flags |= 1152;
        if (i2 >= 28) {
            attributes.layoutInDisplayCutoutMode = 1;
        }
        window.setAttributes(attributes);
        this.windowView.setSystemUiVisibility(1284);
        AndroidUtilities.setLightNavigationBar(this.windowView, !Theme.isCurrentThemeDark());
    }

    public void setCell(org.telegram.ui.Cells.ChatMessageCell r21, java.lang.Runnable r22, java.lang.Runnable r23) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.SecretVoicePlayer.setCell(org.telegram.ui.Cells.ChatMessageCell, java.lang.Runnable, java.lang.Runnable):void");
    }

    @Override
    public void show() {
        if (AndroidUtilities.isSafeToShow(getContext())) {
            super.show();
            prepareBlur(this.cell);
            this.setCellInvisible = true;
            this.open = true;
            animateOpenTo(true, null);
            Runnable runnable = this.openAction;
            if (runnable != null) {
                AndroidUtilities.runOnUIThread(runnable);
                this.openAction = null;
            }
            EarListener earListener = this.earListener;
            if (earListener != null) {
                earListener.attach();
            }
        }
    }
}
