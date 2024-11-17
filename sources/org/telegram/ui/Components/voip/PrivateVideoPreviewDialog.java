package org.telegram.ui.Components.voip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Parcelable;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import java.io.File;
import java.io.FileOutputStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.webrtc.RendererCommon;

public abstract class PrivateVideoPreviewDialog extends FrameLayout implements VoIPService.StateListener {
    private boolean cameraReady;
    private int currentPage;
    private int currentTexturePage;
    private boolean isDismissed;
    public boolean micEnabled;
    private RLottieImageView micIconView;
    private boolean needScreencast;
    private float outProgress;
    private float pageOffset;
    private TextView positiveButton;
    private VoIPTextureView textureView;
    private TextView[] titles;
    private LinearLayout titlesLayout;
    private ViewPager viewPager;
    private int visibleCameraPage;

    private class Adapter extends PagerAdapter {
        private Adapter() {
        }

        @Override
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }

        @Override
        public int getCount() {
            return PrivateVideoPreviewDialog.this.titles.length;
        }

        @Override
        public Object instantiateItem(ViewGroup viewGroup, int i) {
            Bitmap bitmap;
            ImageView imageView;
            int i2 = 1;
            if (PrivateVideoPreviewDialog.this.needScreencast && i == 0) {
                ?? frameLayout = new FrameLayout(PrivateVideoPreviewDialog.this.getContext());
                frameLayout.setBackground(new MotionBackgroundDrawable(-14602694, -13935795, -14395293, -14203560, true));
                ImageView imageView2 = new ImageView(PrivateVideoPreviewDialog.this.getContext());
                imageView2.setScaleType(ImageView.ScaleType.CENTER);
                imageView2.setImageResource(R.drawable.screencast_big);
                frameLayout.addView(imageView2, LayoutHelper.createFrame(82, 82.0f, 17, 0.0f, 0.0f, 0.0f, 60.0f));
                TextView textView = new TextView(PrivateVideoPreviewDialog.this.getContext());
                textView.setText(LocaleController.getString(R.string.VoipVideoPrivateScreenSharing));
                textView.setGravity(17);
                textView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
                textView.setTextColor(-1);
                textView.setTextSize(1, 15.0f);
                textView.setTypeface(AndroidUtilities.bold());
                frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 17, 21.0f, 28.0f, 21.0f, 0.0f));
                imageView = frameLayout;
            } else {
                ImageView imageView3 = new ImageView(PrivateVideoPreviewDialog.this.getContext());
                imageView3.setTag(Integer.valueOf(i));
                try {
                    File filesDirFixed = ApplicationLoader.getFilesDirFixed();
                    StringBuilder sb = new StringBuilder();
                    sb.append("cthumb");
                    if (i != 0 && (i != 1 || !PrivateVideoPreviewDialog.this.needScreencast)) {
                        i2 = 2;
                    }
                    sb.append(i2);
                    sb.append(".jpg");
                    bitmap = BitmapFactory.decodeFile(new File(filesDirFixed, sb.toString()).getAbsolutePath());
                } catch (Throwable unused) {
                    bitmap = null;
                }
                if (bitmap != null) {
                    imageView3.setImageBitmap(bitmap);
                } else {
                    imageView3.setImageResource(R.drawable.icplaceholder);
                }
                imageView3.setScaleType(ImageView.ScaleType.FIT_XY);
                imageView = imageView3;
            }
            if (imageView.getParent() != null) {
                ((ViewGroup) imageView.getParent()).removeView(imageView);
            }
            viewGroup.addView(imageView, 0);
            return imageView;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view.equals(obj);
        }

        @Override
        public void restoreState(Parcelable parcelable, ClassLoader classLoader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void setPrimaryItem(ViewGroup viewGroup, int i, Object obj) {
            super.setPrimaryItem(viewGroup, i, obj);
        }
    }

    public PrivateVideoPreviewDialog(Context context, boolean z, boolean z2) {
        super(context);
        TextView textView;
        int i;
        this.currentTexturePage = 1;
        this.visibleCameraPage = 1;
        this.needScreencast = z2;
        this.titles = new TextView[z2 ? 3 : 2];
        ViewPager viewPager = new ViewPager(context);
        this.viewPager = viewPager;
        AndroidUtilities.setViewPagerEdgeEffectColor(viewPager, 2130706432);
        this.viewPager.setAdapter(new Adapter());
        this.viewPager.setPageMargin(0);
        this.viewPager.setOffscreenPageLimit(1);
        addView(this.viewPager, LayoutHelper.createFrame(-1, -1.0f));
        this.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            private int scrollState = 0;
            private int willSetPage;

            @Override
            public void onPageScrollStateChanged(int i2) {
                this.scrollState = i2;
                if (i2 == 0) {
                    PrivateVideoPreviewDialog.this.currentTexturePage = this.willSetPage;
                    PrivateVideoPreviewDialog.this.onFinishMoveCameraPage();
                }
            }

            @Override
            public void onPageScrolled(int i2, float f, int i3) {
                PrivateVideoPreviewDialog.this.currentPage = i2;
                PrivateVideoPreviewDialog.this.pageOffset = f;
                PrivateVideoPreviewDialog.this.updateTitlesLayout();
            }

            @Override
            public void onPageSelected(int i2) {
                if (this.scrollState == 0) {
                    if (i2 <= PrivateVideoPreviewDialog.this.needScreencast) {
                        PrivateVideoPreviewDialog.this.currentTexturePage = 1;
                    } else {
                        PrivateVideoPreviewDialog.this.currentTexturePage = 2;
                    }
                    PrivateVideoPreviewDialog.this.onFinishMoveCameraPage();
                    return;
                }
                if (i2 <= PrivateVideoPreviewDialog.this.needScreencast) {
                    this.willSetPage = 1;
                } else {
                    this.willSetPage = 2;
                }
            }
        });
        VoIPTextureView voIPTextureView = new VoIPTextureView(context, false, false);
        this.textureView = voIPTextureView;
        voIPTextureView.renderer.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL);
        VoIPTextureView voIPTextureView2 = this.textureView;
        voIPTextureView2.scaleType = VoIPTextureView.SCALE_TYPE_FIT;
        voIPTextureView2.clipToTexture = true;
        voIPTextureView2.renderer.setAlpha(0.0f);
        this.textureView.renderer.setRotateTextureWithScreen(true);
        this.textureView.renderer.setUseCameraRotation(true);
        addView(this.textureView, LayoutHelper.createFrame(-1, -1.0f));
        ActionBar actionBar = new ActionBar(context);
        actionBar.setBackButtonDrawable(new BackDrawable(false));
        actionBar.setBackgroundColor(0);
        actionBar.setItemsColor(Theme.getColor(Theme.key_voipgroup_actionBarItems), false);
        actionBar.setOccupyStatusBar(true);
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() {
            @Override
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    PrivateVideoPreviewDialog.this.dismiss(false, false);
                }
            }
        });
        addView(actionBar);
        TextView textView2 = new TextView(getContext()) {
            private Paint[] gradientPaint;

            {
                this.gradientPaint = new Paint[PrivateVideoPreviewDialog.this.titles.length];
                int i2 = 0;
                while (true) {
                    Paint[] paintArr = this.gradientPaint;
                    if (i2 >= paintArr.length) {
                        return;
                    }
                    paintArr[i2] = new Paint(1);
                    i2++;
                }
            }

            @Override
            protected void onDraw(Canvas canvas) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
                this.gradientPaint[PrivateVideoPreviewDialog.this.currentPage].setAlpha(255);
                canvas.drawRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.gradientPaint[PrivateVideoPreviewDialog.this.currentPage]);
                if (PrivateVideoPreviewDialog.this.pageOffset > 0.0f) {
                    int i2 = PrivateVideoPreviewDialog.this.currentPage + 1;
                    Paint[] paintArr = this.gradientPaint;
                    if (i2 < paintArr.length) {
                        paintArr[PrivateVideoPreviewDialog.this.currentPage + 1].setAlpha((int) (PrivateVideoPreviewDialog.this.pageOffset * 255.0f));
                        canvas.drawRoundRect(rectF, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), this.gradientPaint[PrivateVideoPreviewDialog.this.currentPage + 1]);
                    }
                }
                super.onDraw(canvas);
            }

            @Override
            protected void onSizeChanged(int r24, int r25, int r26, int r27) {
                throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Components.voip.PrivateVideoPreviewDialog.AnonymousClass3.onSizeChanged(int, int, int, int):void");
            }
        };
        this.positiveButton = textView2;
        textView2.setMinWidth(AndroidUtilities.dp(64.0f));
        this.positiveButton.setTag(-1);
        this.positiveButton.setTextSize(1, 14.0f);
        TextView textView3 = this.positiveButton;
        int i2 = Theme.key_voipgroup_nameText;
        textView3.setTextColor(Theme.getColor(i2));
        this.positiveButton.setGravity(17);
        this.positiveButton.setTypeface(AndroidUtilities.bold());
        this.positiveButton.setText(LocaleController.getString(R.string.VoipShareVideo));
        if (Build.VERSION.SDK_INT >= 23) {
            this.positiveButton.setForeground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), 0, ColorUtils.setAlphaComponent(Theme.getColor(i2), 76)));
        }
        this.positiveButton.setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
        this.positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view) {
                PrivateVideoPreviewDialog.this.lambda$new$0(view);
            }
        });
        addView(this.positiveButton, LayoutHelper.createFrame(-1, 48.0f, 80, 0.0f, 0.0f, 0.0f, 64.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.titlesLayout = linearLayout;
        addView(linearLayout, LayoutHelper.createFrame(-2, 64, 80));
        final int i3 = 0;
        while (true) {
            TextView[] textViewArr = this.titles;
            if (i3 >= textViewArr.length) {
                break;
            }
            textViewArr[i3] = new TextView(context);
            this.titles[i3].setTextSize(1, 12.0f);
            this.titles[i3].setTextColor(-1);
            this.titles[i3].setTypeface(AndroidUtilities.bold());
            this.titles[i3].setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            this.titles[i3].setGravity(16);
            this.titles[i3].setSingleLine(true);
            this.titlesLayout.addView(this.titles[i3], LayoutHelper.createLinear(-2, -1));
            if (i3 == 0 && this.needScreencast) {
                textView = this.titles[i3];
                i = R.string.VoipPhoneScreen;
            } else if (i3 == 0 || (i3 == 1 && this.needScreencast)) {
                textView = this.titles[i3];
                i = R.string.VoipFrontCamera;
            } else {
                textView = this.titles[i3];
                i = R.string.VoipBackCamera;
            }
            textView.setText(LocaleController.getString(i));
            this.titles[i3].setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    PrivateVideoPreviewDialog.this.lambda$new$1(i3, view);
                }
            });
            i3++;
        }
        setAlpha(0.0f);
        setTranslationX(AndroidUtilities.dp(32.0f));
        animate().alpha(1.0f).translationX(0.0f).setDuration(150L).start();
        setWillNotDraw(false);
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            this.textureView.renderer.setMirror(sharedInstance.isFrontFaceCamera());
            this.textureView.renderer.init(VideoCapturerDevice.getEglBase().getEglBaseContext(), new RendererCommon.RendererEvents() {
                @Override
                public void onFirstFrameRendered() {
                }

                @Override
                public void onFrameResolutionChanged(int i4, int i5, int i6) {
                }
            });
            sharedInstance.setLocalSink(this.textureView.renderer, false);
        }
        this.viewPager.setCurrentItem(this.needScreencast ? 1 : 0);
        if (z) {
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.micIconView = rLottieImageView;
            rLottieImageView.setPadding(AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(9.0f));
            this.micIconView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(48.0f), ColorUtils.setAlphaComponent(-16777216, 76)));
            int i4 = R.raw.voice_mini;
            final RLottieDrawable rLottieDrawable = new RLottieDrawable(i4, "" + i4, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), true, null);
            this.micIconView.setAnimation(rLottieDrawable);
            this.micIconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            this.micEnabled = true;
            rLottieDrawable.setCurrentFrame(69);
            this.micIconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public final void onClick(View view) {
                    PrivateVideoPreviewDialog.this.lambda$new$2(rLottieDrawable, view);
                }
            });
            addView(this.micIconView, LayoutHelper.createFrame(48, 48.0f, 83, 24.0f, 0.0f, 0.0f, 136.0f));
        }
    }

    public void lambda$new$0(View view) {
        Intent createScreenCaptureIntent;
        if (this.isDismissed) {
            return;
        }
        if (this.currentPage != 0 || !this.needScreencast) {
            dismiss(false, true);
            return;
        }
        MediaProjectionManager m = PrivateVideoPreviewDialog$$ExternalSyntheticApiModelOutline0.m(getContext().getSystemService("media_projection"));
        Activity activity = (Activity) getContext();
        createScreenCaptureIntent = m.createScreenCaptureIntent();
        activity.startActivityForResult(createScreenCaptureIntent, 520);
    }

    public void lambda$new$1(int i, View view) {
        this.viewPager.setCurrentItem(i, true);
    }

    public void lambda$new$2(RLottieDrawable rLottieDrawable, View view) {
        boolean z = !this.micEnabled;
        this.micEnabled = z;
        if (z) {
            rLottieDrawable.setCurrentFrame(36);
            rLottieDrawable.setCustomEndFrame(69);
        } else {
            rLottieDrawable.setCurrentFrame(69);
            rLottieDrawable.setCustomEndFrame(99);
        }
        rLottieDrawable.start();
    }

    public void onFinishMoveCameraPage() {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (this.currentTexturePage == this.visibleCameraPage || sharedInstance == null) {
            return;
        }
        boolean isFrontFaceCamera = sharedInstance.isFrontFaceCamera();
        int i = this.currentTexturePage;
        if ((i == 1 && !isFrontFaceCamera) || (i == 2 && isFrontFaceCamera)) {
            saveLastCameraBitmap();
            this.cameraReady = false;
            VoIPService.getSharedInstance().switchCamera();
            this.textureView.setAlpha(0.0f);
        }
        this.visibleCameraPage = this.currentTexturePage;
    }

    private void saveLastCameraBitmap() {
        if (this.cameraReady) {
            try {
                Bitmap bitmap = this.textureView.renderer.getBitmap();
                if (bitmap != null) {
                    Bitmap createBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), this.textureView.renderer.getMatrix(), true);
                    bitmap.recycle();
                    Bitmap createScaledBitmap = Bitmap.createScaledBitmap(createBitmap, 80, (int) (createBitmap.getHeight() / (createBitmap.getWidth() / 80.0f)), true);
                    if (createScaledBitmap != null) {
                        if (createScaledBitmap != createBitmap) {
                            createBitmap.recycle();
                        }
                        Utilities.blurBitmap(createScaledBitmap, 7, 1, createScaledBitmap.getWidth(), createScaledBitmap.getHeight(), createScaledBitmap.getRowBytes());
                        createScaledBitmap.compress(Bitmap.CompressFormat.JPEG, 87, new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), "cthumb" + this.visibleCameraPage + ".jpg")));
                        View findViewWithTag = this.viewPager.findViewWithTag(Integer.valueOf(this.visibleCameraPage - (1 ^ (this.needScreencast ? 1 : 0))));
                        if (findViewWithTag instanceof ImageView) {
                            ((ImageView) findViewWithTag).setImageBitmap(createScaledBitmap);
                        }
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    public void updateTitlesLayout() {
        TextView[] textViewArr = this.titles;
        int i = this.currentPage;
        TextView textView = textViewArr[i];
        TextView textView2 = i < textViewArr.length + (-1) ? textViewArr[i + 1] : null;
        getMeasuredWidth();
        float left = textView.getLeft() + (textView.getMeasuredWidth() / 2);
        float measuredWidth = (getMeasuredWidth() / 2) - left;
        if (textView2 != null) {
            measuredWidth -= ((textView2.getLeft() + (textView2.getMeasuredWidth() / 2)) - left) * this.pageOffset;
        }
        int i2 = 0;
        while (true) {
            TextView[] textViewArr2 = this.titles;
            if (i2 >= textViewArr2.length) {
                break;
            }
            int i3 = this.currentPage;
            float f = 0.9f;
            float f2 = 0.7f;
            if (i2 >= i3 && i2 <= i3 + 1) {
                if (i2 == i3) {
                    float f3 = this.pageOffset;
                    f2 = 1.0f - (0.3f * f3);
                    f = 1.0f - (f3 * 0.1f);
                } else {
                    float f4 = this.pageOffset;
                    f2 = 0.7f + (0.3f * f4);
                    f = 0.9f + (f4 * 0.1f);
                }
            }
            textViewArr2[i2].setAlpha(f2);
            this.titles[i2].setScaleX(f);
            this.titles[i2].setScaleY(f);
            i2++;
        }
        this.titlesLayout.setTranslationX(measuredWidth);
        this.positiveButton.invalidate();
        if (this.needScreencast && this.currentPage == 0 && this.pageOffset <= 0.0f) {
            this.textureView.setVisibility(4);
            return;
        }
        this.textureView.setVisibility(0);
        if (this.currentPage + (!this.needScreencast ? 1 : 0) == this.currentTexturePage) {
            this.textureView.setTranslationX((-this.pageOffset) * getMeasuredWidth());
        } else {
            this.textureView.setTranslationX((1.0f - this.pageOffset) * getMeasuredWidth());
        }
    }

    public void dismiss(boolean z, boolean z2) {
        if (this.isDismissed) {
            return;
        }
        this.isDismissed = true;
        saveLastCameraBitmap();
        onDismiss(z, z2);
        animate().alpha(0.0f).translationX(AndroidUtilities.dp(32.0f)).setDuration(150L).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animator) {
                super.onAnimationEnd(animator);
                if (PrivateVideoPreviewDialog.this.getParent() != null) {
                    ((ViewGroup) PrivateVideoPreviewDialog.this.getParent()).removeView(PrivateVideoPreviewDialog.this);
                }
            }
        });
        invalidate();
    }

    public int getBackgroundColor() {
        return ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_actionBar), (int) (getAlpha() * (1.0f - this.outProgress) * 255.0f));
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (getParent() != null) {
            ((View) getParent()).invalidate();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.registerStateListener(this);
        }
    }

    @Override
    public void onAudioSettingsChanged() {
        VoIPService.StateListener.CC.$default$onAudioSettingsChanged(this);
    }

    @Override
    public void onCameraFirstFrameAvailable() {
        if (this.cameraReady) {
            return;
        }
        this.cameraReady = true;
        this.textureView.animate().alpha(1.0f).setDuration(250L);
    }

    @Override
    public void onCameraSwitch(boolean z) {
        update();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null) {
            sharedInstance.unregisterStateListener(this);
        }
    }

    protected abstract void onDismiss(boolean z, boolean z2);

    @Override
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        updateTitlesLayout();
    }

    @Override
    protected void onMeasure(int i, int i2) {
        boolean z = View.MeasureSpec.getSize(i) > View.MeasureSpec.getSize(i2);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.positiveButton.getLayoutParams();
        int dp = AndroidUtilities.dp(z ? 80.0f : 16.0f);
        marginLayoutParams.leftMargin = dp;
        marginLayoutParams.rightMargin = dp;
        RLottieImageView rLottieImageView = this.micIconView;
        if (rLottieImageView != null) {
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) rLottieImageView.getLayoutParams();
            int dp2 = AndroidUtilities.dp(z ? 88.0f : 24.0f);
            marginLayoutParams2.leftMargin = dp2;
            marginLayoutParams2.rightMargin = dp2;
        }
        super.onMeasure(i, i2);
        measureChildWithMargins(this.titlesLayout, View.MeasureSpec.makeMeasureSpec(0, 0), 0, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(64.0f), 1073741824), 0);
    }

    @Override
    public void onMediaStateUpdated(int i, int i2) {
        VoIPService.StateListener.CC.$default$onMediaStateUpdated(this, i, i2);
    }

    @Override
    public void onScreenOnChange(boolean z) {
        VoIPService.StateListener.CC.$default$onScreenOnChange(this, z);
    }

    @Override
    public void onSignalBarsCountChanged(int i) {
        VoIPService.StateListener.CC.$default$onSignalBarsCountChanged(this, i);
    }

    @Override
    public void onStateChanged(int i) {
        VoIPService.StateListener.CC.$default$onStateChanged(this, i);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return true;
    }

    @Override
    public void onVideoAvailableChange(boolean z) {
        VoIPService.StateListener.CC.$default$onVideoAvailableChange(this, z);
    }

    public void setBottomPadding(int i) {
        ((FrameLayout.LayoutParams) this.positiveButton.getLayoutParams()).bottomMargin = AndroidUtilities.dp(64.0f) + i;
        ((FrameLayout.LayoutParams) this.titlesLayout.getLayoutParams()).bottomMargin = i;
    }

    public void update() {
        if (VoIPService.getSharedInstance() != null) {
            this.textureView.renderer.setMirror(VoIPService.getSharedInstance().isFrontFaceCamera());
        }
    }
}
