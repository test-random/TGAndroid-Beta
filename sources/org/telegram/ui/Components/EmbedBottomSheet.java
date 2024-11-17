package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.arch.core.util.Function;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.WebPlayerView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;

public class EmbedBottomSheet extends BottomSheet {
    private static EmbedBottomSheet instance;
    private boolean animationInProgress;
    private FrameLayout containerLayout;
    private TextView copyTextButton;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private String embedUrl;
    private FrameLayout fullscreenVideoContainer;
    private boolean fullscreenedByButton;
    private boolean hasDescription;
    private int height;
    private LinearLayout imageButtonsContainer;
    private boolean isYouTube;
    private int lastOrientation;
    private DialogInterface.OnShowListener onShowListener;
    private String openUrl;
    private OrientationEventListener orientationEventListener;
    private Activity parentActivity;
    private ImageView pipButton;
    private int[] position;
    private int prevOrientation;
    private RadialProgressView progressBar;
    private View progressBarBlackBackground;
    private int seekTimeOverride;
    private WebPlayerView videoView;
    private int waitingForDraw;
    private boolean wasInLandscape;
    private WebView webView;
    private int width;
    private final String youtubeFrame;

    public class AnonymousClass5 extends WebViewClient {
        AnonymousClass5() {
        }

        public void lambda$onRenderProcessGone$0() {
            Browser.openUrl(EmbedBottomSheet.this.getContext(), "https://play.google.com/store/apps/details?id=com.google.android.webview");
        }

        @Override
        public void onLoadResource(WebView webView, String str) {
            super.onLoadResource(webView, str);
        }

        @Override
        public void onPageFinished(WebView webView, String str) {
            super.onPageFinished(webView, str);
            if (EmbedBottomSheet.this.isYouTube) {
                return;
            }
            EmbedBottomSheet.this.progressBar.setVisibility(4);
            EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
            EmbedBottomSheet.this.pipButton.setEnabled(true);
            EmbedBottomSheet.this.pipButton.setAlpha(1.0f);
        }

        @Override
        public boolean onRenderProcessGone(WebView webView, RenderProcessGoneDetail renderProcessGoneDetail) {
            if (!AndroidUtilities.isSafeToShow(EmbedBottomSheet.this.getContext())) {
                return true;
            }
            new AlertDialog.Builder(EmbedBottomSheet.this.getContext(), ((BottomSheet) EmbedBottomSheet.this).resourcesProvider).setTitle(LocaleController.getString(R.string.ChromeCrashTitle)).setMessage(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.ChromeCrashMessage), new Runnable() {
                @Override
                public final void run() {
                    EmbedBottomSheet.AnonymousClass5.this.lambda$onRenderProcessGone$0();
                }
            })).setPositiveButton(LocaleController.getString(R.string.OK), null).show();
            return true;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String str) {
            if (!EmbedBottomSheet.this.isYouTube) {
                return super.shouldOverrideUrlLoading(webView, str);
            }
            Browser.openUrl(webView.getContext(), str);
            return true;
        }
    }

    public class YoutubeProxy {
        private YoutubeProxy() {
        }

        public void lambda$postEvent$0() {
            EmbedBottomSheet.this.progressBar.setVisibility(4);
            EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(4);
            EmbedBottomSheet.this.pipButton.setEnabled(true);
            EmbedBottomSheet.this.pipButton.setAlpha(1.0f);
        }

        @JavascriptInterface
        public void postEvent(String str, String str2) {
            if ("loaded".equals(str)) {
                AndroidUtilities.runOnUIThread(new Runnable() {
                    @Override
                    public final void run() {
                        EmbedBottomSheet.YoutubeProxy.this.lambda$postEvent$0();
                    }
                });
            }
        }
    }

    private EmbedBottomSheet(final Context context, String str, String str2, String str3, String str4, int i, int i2, int i3) {
        super(context, false);
        this.position = new int[2];
        this.lastOrientation = -1;
        this.prevOrientation = -2;
        this.youtubeFrame = "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              \"onStateChange\" : \"onStateChange\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 1,                              \"showinfo\" : 0,                              \"modestbranding\" : 0,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onStateChange(event) {       if (event.data == YT.PlayerState.PLAYING && !posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();    }    window.onresize = function() {       player.setSize(window.innerWidth, window.innerHeight);       player.playVideo();    }    </script></body></html>";
        this.onShowListener = new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                if (PipVideoOverlay.isVisible() && EmbedBottomSheet.this.videoView.isInline()) {
                    EmbedBottomSheet.this.videoView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            EmbedBottomSheet.this.videoView.getViewTreeObserver().removeOnPreDrawListener(this);
                            return true;
                        }
                    });
                }
            }
        };
        this.fullWidth = true;
        setApplyTopPadding(false);
        setApplyBottomPadding(false);
        this.seekTimeOverride = i3;
        if (context instanceof Activity) {
            this.parentActivity = (Activity) context;
        }
        this.embedUrl = str4;
        this.hasDescription = str2 != null && str2.length() > 0;
        this.openUrl = str3;
        this.width = i;
        this.height = i2;
        if (i == 0 || i2 == 0) {
            android.graphics.Point point = AndroidUtilities.displaySize;
            this.width = point.x;
            this.height = point.y / 2;
        }
        FrameLayout frameLayout = new FrameLayout(context);
        this.fullscreenVideoContainer = frameLayout;
        frameLayout.setKeepScreenOn(true);
        this.fullscreenVideoContainer.setBackgroundColor(-16777216);
        int i4 = Build.VERSION.SDK_INT;
        if (i4 >= 21) {
            this.fullscreenVideoContainer.setFitsSystemWindows(true);
        }
        this.fullscreenVideoContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$new$0;
                lambda$new$0 = EmbedBottomSheet.lambda$new$0(view, motionEvent);
                return lambda$new$0;
            }
        });
        this.container.addView(this.fullscreenVideoContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.fullscreenVideoContainer.setVisibility(4);
        FrameLayout frameLayout2 = new FrameLayout(context) {
            @Override
            protected void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                try {
                    if (PipVideoOverlay.isVisible()) {
                        if (EmbedBottomSheet.this.webView.getVisibility() != 0) {
                        }
                        if (!EmbedBottomSheet.this.videoView.isInline() || PipVideoOverlay.isVisible()) {
                        }
                        if (EmbedBottomSheet.instance == EmbedBottomSheet.this) {
                            EmbedBottomSheet unused = EmbedBottomSheet.instance = null;
                        }
                        EmbedBottomSheet.this.videoView.destroy();
                        return;
                    }
                    if (EmbedBottomSheet.this.webView.getParent() != null) {
                        removeView(EmbedBottomSheet.this.webView);
                        EmbedBottomSheet.this.webView.stopLoading();
                        EmbedBottomSheet.this.webView.loadUrl("about:blank");
                        EmbedBottomSheet.this.webView.destroy();
                    }
                    if (EmbedBottomSheet.this.videoView.isInline()) {
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            @Override
            protected void onMeasure(int i5, int i6) {
                super.onMeasure(i5, View.MeasureSpec.makeMeasureSpec(((int) Math.min(EmbedBottomSheet.this.height / (EmbedBottomSheet.this.width / View.MeasureSpec.getSize(i5)), AndroidUtilities.displaySize.y / 2)) + AndroidUtilities.dp((EmbedBottomSheet.this.hasDescription ? 22 : 0) + 84) + 1, 1073741824));
            }
        };
        this.containerLayout = frameLayout2;
        frameLayout2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                boolean lambda$new$1;
                lambda$new$1 = EmbedBottomSheet.lambda$new$1(view, motionEvent);
                return lambda$new$1;
            }
        });
        setCustomView(this.containerLayout);
        WebView webView = new WebView(context) {
            @Override
            protected void onAttachedToWindow() {
                AndroidUtilities.checkAndroidTheme(context, true);
                super.onAttachedToWindow();
            }

            @Override
            protected void onDetachedFromWindow() {
                AndroidUtilities.checkAndroidTheme(context, false);
                super.onDetachedFromWindow();
            }

            @Override
            public boolean onTouchEvent(MotionEvent motionEvent) {
                EmbedBottomSheet embedBottomSheet;
                boolean onTouchEvent = super.onTouchEvent(motionEvent);
                if (onTouchEvent) {
                    boolean z = true;
                    if (motionEvent.getAction() == 1) {
                        embedBottomSheet = EmbedBottomSheet.this;
                        z = false;
                    } else {
                        embedBottomSheet = EmbedBottomSheet.this;
                    }
                    embedBottomSheet.setDisableScroll(z);
                }
                return onTouchEvent;
            }
        };
        this.webView = webView;
        webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        this.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        if (i4 >= 21) {
            this.webView.getSettings().setMixedContentMode(0);
            CookieManager.getInstance().setAcceptThirdPartyCookies(this.webView, true);
        }
        this.webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onHideCustomView() {
                super.onHideCustomView();
                if (EmbedBottomSheet.this.customView == null) {
                    return;
                }
                EmbedBottomSheet.this.getSheetContainer().setVisibility(0);
                EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                EmbedBottomSheet.this.fullscreenVideoContainer.removeView(EmbedBottomSheet.this.customView);
                if (EmbedBottomSheet.this.customViewCallback != null && !EmbedBottomSheet.this.customViewCallback.getClass().getName().contains(".chromium.")) {
                    EmbedBottomSheet.this.customViewCallback.onCustomViewHidden();
                }
                EmbedBottomSheet.this.customView = null;
            }

            @Override
            public void onShowCustomView(View view, int i5, WebChromeClient.CustomViewCallback customViewCallback) {
                onShowCustomView(view, customViewCallback);
            }

            @Override
            public void onShowCustomView(View view, WebChromeClient.CustomViewCallback customViewCallback) {
                if (EmbedBottomSheet.this.customView != null || PipVideoOverlay.isVisible()) {
                    customViewCallback.onCustomViewHidden();
                    return;
                }
                EmbedBottomSheet.this.exitFromPip();
                EmbedBottomSheet.this.customView = view;
                EmbedBottomSheet.this.getSheetContainer().setVisibility(4);
                EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
                EmbedBottomSheet.this.fullscreenVideoContainer.addView(view, LayoutHelper.createFrame(-1, -1.0f));
                EmbedBottomSheet.this.customViewCallback = customViewCallback;
            }
        });
        this.webView.setWebViewClient(new AnonymousClass5());
        this.containerLayout.addView(this.webView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (this.hasDescription ? 22 : 0) + 84));
        WebPlayerView webPlayerView = new WebPlayerView(context, true, false, new WebPlayerView.WebPlayerViewDelegate() {
            @Override
            public boolean checkInlinePermissions() {
                return EmbedBottomSheet.this.checkInlinePermissions();
            }

            @Override
            public ViewGroup getTextureViewContainer() {
                return EmbedBottomSheet.this.container;
            }

            @Override
            public void onInitFailed() {
                EmbedBottomSheet.this.webView.setVisibility(0);
                EmbedBottomSheet.this.imageButtonsContainer.setVisibility(0);
                EmbedBottomSheet.this.copyTextButton.setVisibility(4);
                EmbedBottomSheet.this.webView.setKeepScreenOn(true);
                EmbedBottomSheet.this.videoView.setVisibility(4);
                EmbedBottomSheet.this.videoView.getControlsView().setVisibility(4);
                EmbedBottomSheet.this.videoView.getTextureView().setVisibility(4);
                if (EmbedBottomSheet.this.videoView.getTextureImageView() != null) {
                    EmbedBottomSheet.this.videoView.getTextureImageView().setVisibility(4);
                }
                EmbedBottomSheet.this.videoView.loadVideo(null, null, null, null, false);
                HashMap hashMap = new HashMap();
                hashMap.put("Referer", "messenger.telegram.org");
                try {
                    EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, hashMap);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            @Override
            public void onInlineSurfaceTextureReady() {
                if (EmbedBottomSheet.this.videoView.isInline()) {
                    EmbedBottomSheet.this.dismissInternal();
                }
            }

            @Override
            public void onPlayStateChanged(WebPlayerView webPlayerView2, boolean z) {
                try {
                    if (z) {
                        EmbedBottomSheet.this.parentActivity.getWindow().addFlags(128);
                    } else {
                        EmbedBottomSheet.this.parentActivity.getWindow().clearFlags(128);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            @Override
            public void onSharePressed() {
            }

            @Override
            public TextureView onSwitchInlineMode(View view, boolean z, int i5, int i6, int i7, boolean z2) {
                if (z) {
                    view.setTranslationY(0.0f);
                    TextureView textureView = new TextureView(EmbedBottomSheet.this.parentActivity);
                    if (!PipVideoOverlay.show(false, EmbedBottomSheet.this.parentActivity, textureView, i5, i6)) {
                        return null;
                    }
                    PipVideoOverlay.setParentSheet(EmbedBottomSheet.this);
                    return textureView;
                }
                if (!z2) {
                    ((BottomSheet) EmbedBottomSheet.this).containerView.setTranslationY(0.0f);
                    return null;
                }
                EmbedBottomSheet.this.animationInProgress = true;
                EmbedBottomSheet.this.videoView.getAspectRatioView().getLocationInWindow(EmbedBottomSheet.this.position);
                int[] iArr = EmbedBottomSheet.this.position;
                iArr[0] = iArr[0] - EmbedBottomSheet.this.getLeftInset();
                EmbedBottomSheet.this.position[1] = (int) (r5[1] - ((BottomSheet) EmbedBottomSheet.this).containerView.getTranslationY());
                TextureView textureView2 = EmbedBottomSheet.this.videoView.getTextureView();
                ImageView textureImageView = EmbedBottomSheet.this.videoView.getTextureImageView();
                AnimatorSet animatorSet = new AnimatorSet();
                Property property = View.SCALE_X;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(textureImageView, (Property<ImageView, Float>) property, 1.0f);
                Property property2 = View.SCALE_Y;
                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(textureImageView, (Property<ImageView, Float>) property2, 1.0f);
                Property property3 = View.TRANSLATION_X;
                ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(textureImageView, (Property<ImageView, Float>) property3, EmbedBottomSheet.this.position[0]);
                Property property4 = View.TRANSLATION_Y;
                animatorSet.playTogether(ofFloat, ofFloat2, ofFloat3, ObjectAnimator.ofFloat(textureImageView, (Property<ImageView, Float>) property4, EmbedBottomSheet.this.position[1]), ObjectAnimator.ofFloat(textureView2, (Property<TextureView, Float>) property, 1.0f), ObjectAnimator.ofFloat(textureView2, (Property<TextureView, Float>) property2, 1.0f), ObjectAnimator.ofFloat(textureView2, (Property<TextureView, Float>) property3, EmbedBottomSheet.this.position[0]), ObjectAnimator.ofFloat(textureView2, (Property<TextureView, Float>) property4, EmbedBottomSheet.this.position[1]), ObjectAnimator.ofFloat(((BottomSheet) EmbedBottomSheet.this).containerView, (Property<ViewGroup, Float>) property4, 0.0f), ObjectAnimator.ofInt(((BottomSheet) EmbedBottomSheet.this).backDrawable, (Property<ColorDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, 51));
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(250L);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        EmbedBottomSheet.this.animationInProgress = false;
                    }
                });
                animatorSet.start();
                return null;
            }

            @Override
            public TextureView onSwitchToFullscreen(View view, boolean z, float f, int i5, boolean z2) {
                try {
                    if (!z) {
                        EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                        EmbedBottomSheet.this.fullscreenedByButton = false;
                        if (EmbedBottomSheet.this.parentActivity == null) {
                            return null;
                        }
                        ((BottomSheet) EmbedBottomSheet.this).containerView.setSystemUiVisibility(0);
                        EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                        return null;
                    }
                    EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(0);
                    EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0f);
                    EmbedBottomSheet.this.fullscreenVideoContainer.addView(EmbedBottomSheet.this.videoView.getAspectRatioView());
                    EmbedBottomSheet.this.wasInLandscape = false;
                    EmbedBottomSheet.this.fullscreenedByButton = z2;
                    if (EmbedBottomSheet.this.parentActivity == null) {
                        return null;
                    }
                    EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.this;
                    embedBottomSheet.prevOrientation = embedBottomSheet.parentActivity.getRequestedOrientation();
                    if (z2) {
                        if (((WindowManager) EmbedBottomSheet.this.parentActivity.getSystemService("window")).getDefaultDisplay().getRotation() == 3) {
                            EmbedBottomSheet.this.parentActivity.setRequestedOrientation(8);
                        } else {
                            EmbedBottomSheet.this.parentActivity.setRequestedOrientation(0);
                        }
                    }
                    ((BottomSheet) EmbedBottomSheet.this).containerView.setSystemUiVisibility(1028);
                    return null;
                } catch (Exception e) {
                    FileLog.e(e);
                    return null;
                }
            }

            @Override
            public void onVideoSizeChanged(float f, int i5) {
            }

            @Override
            public void prepareToSwitchInlineMode(boolean z, final Runnable runnable, float f, boolean z2) {
                if (!z) {
                    if (ApplicationLoader.mainInterfacePaused) {
                        try {
                            EmbedBottomSheet.this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, (Class<?>) BringAppForegroundService.class));
                        } catch (Throwable th) {
                            FileLog.e(th);
                        }
                    }
                    if (z2) {
                        EmbedBottomSheet embedBottomSheet = EmbedBottomSheet.this;
                        embedBottomSheet.setOnShowListener(embedBottomSheet.onShowListener);
                        Rect pipRect = PipVideoOverlay.getPipRect(false, f);
                        TextureView textureView = EmbedBottomSheet.this.videoView.getTextureView();
                        ImageView textureImageView = EmbedBottomSheet.this.videoView.getTextureImageView();
                        float f2 = pipRect.width / textureView.getLayoutParams().width;
                        textureImageView.setScaleX(f2);
                        textureImageView.setScaleY(f2);
                        textureImageView.setTranslationX(pipRect.x);
                        textureImageView.setTranslationY(pipRect.y);
                        textureView.setScaleX(f2);
                        textureView.setScaleY(f2);
                        textureView.setTranslationX(pipRect.x);
                        textureView.setTranslationY(pipRect.y);
                    } else {
                        PipVideoOverlay.dismiss();
                    }
                    EmbedBottomSheet.this.setShowWithoutAnimation(true);
                    EmbedBottomSheet.this.show();
                    if (z2) {
                        EmbedBottomSheet.this.waitingForDraw = 4;
                        ((BottomSheet) EmbedBottomSheet.this).backDrawable.setAlpha(1);
                        ((BottomSheet) EmbedBottomSheet.this).containerView.setTranslationY(((BottomSheet) EmbedBottomSheet.this).containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f));
                        return;
                    }
                    return;
                }
                if (EmbedBottomSheet.this.parentActivity != null) {
                    try {
                        ((BottomSheet) EmbedBottomSheet.this).containerView.setSystemUiVisibility(0);
                        if (EmbedBottomSheet.this.prevOrientation != -2) {
                            EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0) {
                    ((BottomSheet) EmbedBottomSheet.this).containerView.setTranslationY(((BottomSheet) EmbedBottomSheet.this).containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f));
                    ((BottomSheet) EmbedBottomSheet.this).backDrawable.setAlpha(0);
                }
                EmbedBottomSheet.this.setOnShowListener(null);
                if (!z2) {
                    if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0) {
                        EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0f);
                        EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                    }
                    runnable.run();
                    EmbedBottomSheet.this.dismissInternal();
                    return;
                }
                TextureView textureView2 = EmbedBottomSheet.this.videoView.getTextureView();
                View controlsView = EmbedBottomSheet.this.videoView.getControlsView();
                ImageView textureImageView2 = EmbedBottomSheet.this.videoView.getTextureImageView();
                Rect pipRect2 = PipVideoOverlay.getPipRect(true, f);
                float width = pipRect2.width / textureView2.getWidth();
                AnimatorSet animatorSet = new AnimatorSet();
                Property property = View.SCALE_X;
                ObjectAnimator ofFloat = ObjectAnimator.ofFloat(textureImageView2, (Property<ImageView, Float>) property, width);
                Property property2 = View.SCALE_Y;
                ObjectAnimator ofFloat2 = ObjectAnimator.ofFloat(textureImageView2, (Property<ImageView, Float>) property2, width);
                Property property3 = View.TRANSLATION_X;
                ObjectAnimator ofFloat3 = ObjectAnimator.ofFloat(textureImageView2, (Property<ImageView, Float>) property3, pipRect2.x);
                Property property4 = View.TRANSLATION_Y;
                ObjectAnimator ofFloat4 = ObjectAnimator.ofFloat(textureImageView2, (Property<ImageView, Float>) property4, pipRect2.y);
                ObjectAnimator ofFloat5 = ObjectAnimator.ofFloat(textureView2, (Property<TextureView, Float>) property, width);
                ObjectAnimator ofFloat6 = ObjectAnimator.ofFloat(textureView2, (Property<TextureView, Float>) property2, width);
                ObjectAnimator ofFloat7 = ObjectAnimator.ofFloat(textureView2, (Property<TextureView, Float>) property3, pipRect2.x);
                ObjectAnimator ofFloat8 = ObjectAnimator.ofFloat(textureView2, (Property<TextureView, Float>) property4, pipRect2.y);
                ObjectAnimator ofFloat9 = ObjectAnimator.ofFloat(((BottomSheet) EmbedBottomSheet.this).containerView, (Property<ViewGroup, Float>) property4, ((BottomSheet) EmbedBottomSheet.this).containerView.getMeasuredHeight() + AndroidUtilities.dp(10.0f));
                ObjectAnimator ofInt = ObjectAnimator.ofInt(((BottomSheet) EmbedBottomSheet.this).backDrawable, (Property<ColorDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, 0);
                FrameLayout frameLayout3 = EmbedBottomSheet.this.fullscreenVideoContainer;
                Property property5 = View.ALPHA;
                animatorSet.playTogether(ofFloat, ofFloat2, ofFloat3, ofFloat4, ofFloat5, ofFloat6, ofFloat7, ofFloat8, ofFloat9, ofInt, ObjectAnimator.ofFloat(frameLayout3, (Property<FrameLayout, Float>) property5, 0.0f), ObjectAnimator.ofFloat(controlsView, (Property<View, Float>) property5, 0.0f));
                animatorSet.setInterpolator(new DecelerateInterpolator());
                animatorSet.setDuration(250L);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (EmbedBottomSheet.this.fullscreenVideoContainer.getVisibility() == 0) {
                            EmbedBottomSheet.this.fullscreenVideoContainer.setAlpha(1.0f);
                            EmbedBottomSheet.this.fullscreenVideoContainer.setVisibility(4);
                        }
                        runnable.run();
                    }
                });
                animatorSet.start();
            }
        });
        this.videoView = webPlayerView;
        webPlayerView.setVisibility(4);
        this.containerLayout.addView(this.videoView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (this.hasDescription ? 22 : 0) + 74));
        View view = new View(context);
        this.progressBarBlackBackground = view;
        view.setBackgroundColor(-16777216);
        this.progressBarBlackBackground.setVisibility(4);
        this.containerLayout.addView(this.progressBarBlackBackground, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (this.hasDescription ? 22 : 0) + 84));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressBar = radialProgressView;
        radialProgressView.setVisibility(4);
        this.containerLayout.addView(this.progressBar, LayoutHelper.createFrame(-2, -2.0f, 17, 0.0f, 0.0f, 0.0f, ((this.hasDescription ? 22 : 0) + 84) / 2));
        if (this.hasDescription) {
            TextView textView = new TextView(context);
            textView.setTextSize(1, 16.0f);
            textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
            textView.setText(str2);
            textView.setSingleLine(true);
            textView.setTypeface(AndroidUtilities.bold());
            textView.setEllipsize(TextUtils.TruncateAt.END);
            textView.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
            this.containerLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 77.0f));
        }
        TextView textView2 = new TextView(context);
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(Theme.getColor(Theme.key_dialogTextGray));
        textView2.setText(str);
        textView2.setSingleLine(true);
        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
        textView2.setEllipsize(truncateAt);
        textView2.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.containerLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 0.0f, 57.0f));
        View view2 = new View(context);
        view2.setBackgroundColor(Theme.getColor(Theme.key_dialogGrayLine));
        this.containerLayout.addView(view2, new FrameLayout.LayoutParams(-1, 1, 83));
        ((FrameLayout.LayoutParams) view2.getLayoutParams()).bottomMargin = AndroidUtilities.dp(48.0f);
        FrameLayout frameLayout3 = new FrameLayout(context);
        frameLayout3.setBackgroundColor(Theme.getColor(Theme.key_dialogBackground));
        this.containerLayout.addView(frameLayout3, LayoutHelper.createFrame(-1, 48, 83));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(0);
        linearLayout.setWeightSum(1.0f);
        frameLayout3.addView(linearLayout, LayoutHelper.createFrame(-2, -1, 53));
        TextView textView3 = new TextView(context);
        textView3.setTextSize(1, 14.0f);
        int i5 = Theme.key_dialogTextBlue4;
        textView3.setTextColor(Theme.getColor(i5));
        textView3.setGravity(17);
        textView3.setSingleLine(true);
        textView3.setEllipsize(truncateAt);
        int i6 = Theme.key_dialogButtonSelector;
        textView3.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(i6), 0));
        textView3.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        textView3.setText(LocaleController.getString(R.string.Close).toUpperCase());
        textView3.setTypeface(AndroidUtilities.bold());
        frameLayout3.addView(textView3, LayoutHelper.createLinear(-2, -1, 51));
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                EmbedBottomSheet.this.lambda$new$2(view3);
            }
        });
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.imageButtonsContainer = linearLayout2;
        linearLayout2.setVisibility(4);
        frameLayout3.addView(this.imageButtonsContainer, LayoutHelper.createFrame(-2, -1, 17));
        ImageView imageView = new ImageView(context);
        this.pipButton = imageView;
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        imageView.setScaleType(scaleType);
        this.pipButton.setImageResource(R.drawable.ic_goinline);
        this.pipButton.setContentDescription(LocaleController.getString(R.string.AccDescrPipMode));
        this.pipButton.setEnabled(false);
        this.pipButton.setAlpha(0.5f);
        ImageView imageView2 = this.pipButton;
        int color = Theme.getColor(i5);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        imageView2.setColorFilter(new PorterDuffColorFilter(color, mode));
        this.pipButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(i6), 0));
        this.imageButtonsContainer.addView(this.pipButton, LayoutHelper.createFrame(48, 48.0f, 51, 0.0f, 0.0f, 4.0f, 0.0f));
        this.pipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                EmbedBottomSheet.this.lambda$new$3(view3);
            }
        });
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                EmbedBottomSheet.this.lambda$new$4(view3);
            }
        };
        ImageView imageView3 = new ImageView(context);
        imageView3.setScaleType(scaleType);
        imageView3.setImageResource(R.drawable.msg_copy);
        imageView3.setContentDescription(LocaleController.getString(R.string.CopyLink));
        imageView3.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i5), mode));
        imageView3.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(i6), 0));
        this.imageButtonsContainer.addView(imageView3, LayoutHelper.createFrame(48, 48, 51));
        imageView3.setOnClickListener(onClickListener);
        TextView textView4 = new TextView(context);
        this.copyTextButton = textView4;
        textView4.setTextSize(1, 14.0f);
        this.copyTextButton.setTextColor(Theme.getColor(i5));
        this.copyTextButton.setGravity(17);
        this.copyTextButton.setSingleLine(true);
        this.copyTextButton.setEllipsize(truncateAt);
        this.copyTextButton.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(i6), 0));
        this.copyTextButton.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        this.copyTextButton.setText(LocaleController.getString(R.string.Copy).toUpperCase());
        this.copyTextButton.setTypeface(AndroidUtilities.bold());
        linearLayout.addView(this.copyTextButton, LayoutHelper.createFrame(-2, -1, 51));
        this.copyTextButton.setOnClickListener(onClickListener);
        TextView textView5 = new TextView(context);
        textView5.setTextSize(1, 14.0f);
        textView5.setTextColor(Theme.getColor(i5));
        textView5.setGravity(17);
        textView5.setSingleLine(true);
        textView5.setEllipsize(truncateAt);
        textView5.setBackgroundDrawable(Theme.createSelectorDrawable(Theme.getColor(i6), 0));
        textView5.setPadding(AndroidUtilities.dp(18.0f), 0, AndroidUtilities.dp(18.0f), 0);
        textView5.setText(LocaleController.getString(R.string.OpenInBrowser).toUpperCase());
        textView5.setTypeface(AndroidUtilities.bold());
        linearLayout.addView(textView5, LayoutHelper.createFrame(-2, -1, 51));
        textView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public final void onClick(View view3) {
                EmbedBottomSheet.this.lambda$new$5(view3);
            }
        });
        final boolean z = this.videoView.canHandleUrl(this.embedUrl) || this.videoView.canHandleUrl(str3);
        this.videoView.setVisibility(z ? 0 : 4);
        if (z) {
            this.videoView.willHandle();
        }
        setDelegate(new BottomSheet.BottomSheetDelegate() {
            @Override
            public boolean canDismiss() {
                if (EmbedBottomSheet.this.videoView.isInFullscreen()) {
                    EmbedBottomSheet.this.videoView.exitFullscreen();
                    return false;
                }
                try {
                    EmbedBottomSheet.this.parentActivity.getWindow().clearFlags(128);
                    return true;
                } catch (Exception e) {
                    FileLog.e(e);
                    return true;
                }
            }

            @Override
            public void onOpenAnimationEnd() {
                int intValue;
                if (z && EmbedBottomSheet.this.videoView.loadVideo(EmbedBottomSheet.this.embedUrl, null, null, EmbedBottomSheet.this.openUrl, true)) {
                    EmbedBottomSheet.this.progressBar.setVisibility(4);
                    EmbedBottomSheet.this.webView.setVisibility(4);
                    EmbedBottomSheet.this.videoView.setVisibility(0);
                    return;
                }
                EmbedBottomSheet.this.progressBar.setVisibility(0);
                EmbedBottomSheet.this.webView.setVisibility(0);
                EmbedBottomSheet.this.imageButtonsContainer.setVisibility(0);
                EmbedBottomSheet.this.copyTextButton.setVisibility(4);
                EmbedBottomSheet.this.webView.setKeepScreenOn(true);
                EmbedBottomSheet.this.videoView.setVisibility(4);
                EmbedBottomSheet.this.videoView.getControlsView().setVisibility(4);
                EmbedBottomSheet.this.videoView.getTextureView().setVisibility(4);
                if (EmbedBottomSheet.this.videoView.getTextureImageView() != null) {
                    EmbedBottomSheet.this.videoView.getTextureImageView().setVisibility(4);
                }
                EmbedBottomSheet.this.videoView.loadVideo(null, null, null, null, false);
                HashMap hashMap = new HashMap();
                hashMap.put("Referer", "messenger.telegram.org");
                try {
                    String youtubeId = EmbedBottomSheet.this.videoView.getYoutubeId();
                    if (youtubeId == null) {
                        EmbedBottomSheet.this.webView.loadUrl(EmbedBottomSheet.this.embedUrl, hashMap);
                        return;
                    }
                    EmbedBottomSheet.this.progressBarBlackBackground.setVisibility(0);
                    EmbedBottomSheet.this.isYouTube = true;
                    String str5 = null;
                    EmbedBottomSheet.this.webView.addJavascriptInterface(new YoutubeProxy(), "YoutubeProxy");
                    if (EmbedBottomSheet.this.openUrl != null) {
                        try {
                            Uri parse = Uri.parse(EmbedBottomSheet.this.openUrl);
                            if (EmbedBottomSheet.this.seekTimeOverride > 0) {
                                str5 = "" + EmbedBottomSheet.this.seekTimeOverride;
                            }
                            if (str5 == null && (str5 = parse.getQueryParameter("t")) == null) {
                                str5 = parse.getQueryParameter("time_continue");
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                        if (str5 != null) {
                            if (str5.contains("m")) {
                                String[] split = str5.split("m");
                                intValue = (Utilities.parseInt((CharSequence) split[0]).intValue() * 60) + Utilities.parseInt((CharSequence) split[1]).intValue();
                            } else {
                                intValue = Utilities.parseInt((CharSequence) str5).intValue();
                            }
                            EmbedBottomSheet.this.webView.loadDataWithBaseURL("https://messenger.telegram.org/", String.format(Locale.US, "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              \"onStateChange\" : \"onStateChange\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 1,                              \"showinfo\" : 0,                              \"modestbranding\" : 0,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onStateChange(event) {       if (event.data == YT.PlayerState.PLAYING && !posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();    }    window.onresize = function() {       player.setSize(window.innerWidth, window.innerHeight);       player.playVideo();    }    </script></body></html>", youtubeId, Integer.valueOf(intValue)), "text/html", "UTF-8", "https://youtube.com");
                        }
                    }
                    intValue = 0;
                    EmbedBottomSheet.this.webView.loadDataWithBaseURL("https://messenger.telegram.org/", String.format(Locale.US, "<!DOCTYPE html><html><head><style>body { margin: 0; width:100%%; height:100%%;  background-color:#000; }html { width:100%%; height:100%%; background-color:#000; }.embed-container iframe,.embed-container object,   .embed-container embed {       position: absolute;       top: 0;       left: 0;       width: 100%% !important;       height: 100%% !important;   }   </style></head><body>   <div class=\"embed-container\">       <div id=\"player\"></div>   </div>   <script src=\"https://www.youtube.com/iframe_api\"></script>   <script>   var player;   var observer;   var videoEl;   var playing;   var posted = false;   YT.ready(function() {       player = new YT.Player(\"player\", {                              \"width\" : \"100%%\",                              \"events\" : {                              \"onReady\" : \"onReady\",                              \"onError\" : \"onError\",                              \"onStateChange\" : \"onStateChange\",                              },                              \"videoId\" : \"%1$s\",                              \"height\" : \"100%%\",                              \"playerVars\" : {                              \"start\" : %2$d,                              \"rel\" : 1,                              \"showinfo\" : 0,                              \"modestbranding\" : 0,                              \"iv_load_policy\" : 3,                              \"autohide\" : 1,                              \"autoplay\" : 1,                              \"cc_load_policy\" : 1,                              \"playsinline\" : 1,                              \"controls\" : 1                              }                            });        player.setSize(window.innerWidth, window.innerHeight);    });    function hideControls() {        playing = !videoEl.paused;       videoEl.controls = 0;       observer.observe(videoEl, {attributes: true});    }    function showControls() {        playing = !videoEl.paused;       observer.disconnect();       videoEl.controls = 1;    }    function onError(event) {       if (!posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onStateChange(event) {       if (event.data == YT.PlayerState.PLAYING && !posted) {            if (window.YoutubeProxy !== undefined) {                   YoutubeProxy.postEvent(\"loaded\", null);             }            posted = true;       }    }    function onReady(event) {       player.playVideo();    }    window.onresize = function() {       player.setSize(window.innerWidth, window.innerHeight);       player.playVideo();    }    </script></body></html>", youtubeId, Integer.valueOf(intValue)), "text/html", "UTF-8", "https://youtube.com");
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
        });
        this.orientationEventListener = new OrientationEventListener(ApplicationLoader.applicationContext) {
            @Override
            public void onOrientationChanged(int i7) {
                boolean z2;
                EmbedBottomSheet embedBottomSheet;
                if (EmbedBottomSheet.this.orientationEventListener != null && EmbedBottomSheet.this.videoView.getVisibility() == 0 && EmbedBottomSheet.this.parentActivity != null && EmbedBottomSheet.this.videoView.isInFullscreen() && EmbedBottomSheet.this.fullscreenedByButton) {
                    if (i7 >= 240 && i7 <= 300) {
                        embedBottomSheet = EmbedBottomSheet.this;
                        z2 = true;
                    } else {
                        if (!EmbedBottomSheet.this.wasInLandscape || i7 <= 0) {
                            return;
                        }
                        if (i7 < 330 && i7 > 30) {
                            return;
                        }
                        EmbedBottomSheet.this.parentActivity.setRequestedOrientation(EmbedBottomSheet.this.prevOrientation);
                        z2 = false;
                        EmbedBottomSheet.this.fullscreenedByButton = false;
                        embedBottomSheet = EmbedBottomSheet.this;
                    }
                    embedBottomSheet.wasInLandscape = z2;
                }
            }
        };
        String youTubeVideoId = WebPlayerView.getYouTubeVideoId(this.embedUrl);
        if (youTubeVideoId != null || !z) {
            this.progressBar.setVisibility(0);
            this.webView.setVisibility(0);
            this.imageButtonsContainer.setVisibility(0);
            if (youTubeVideoId != null) {
                this.progressBarBlackBackground.setVisibility(0);
            }
            this.copyTextButton.setVisibility(4);
            this.webView.setKeepScreenOn(true);
            this.videoView.setVisibility(4);
            this.videoView.getControlsView().setVisibility(4);
            this.videoView.getTextureView().setVisibility(4);
            if (this.videoView.getTextureImageView() != null) {
                this.videoView.getTextureImageView().setVisibility(4);
            }
            if (youTubeVideoId != null && "disabled".equals(MessagesController.getInstance(this.currentAccount).youtubePipType)) {
                this.pipButton.setVisibility(8);
            }
        }
        if (this.orientationEventListener.canDetectOrientation()) {
            this.orientationEventListener.enable();
        } else {
            this.orientationEventListener.disable();
            this.orientationEventListener = null;
        }
        instance = this;
    }

    public static EmbedBottomSheet getInstance() {
        return instance;
    }

    public static boolean lambda$new$0(View view, MotionEvent motionEvent) {
        return true;
    }

    public static boolean lambda$new$1(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$new$2(View view) {
        dismiss();
    }

    public void lambda$new$3(View view) {
        if (PipVideoOverlay.isVisible()) {
            PipVideoOverlay.dismiss();
            Objects.requireNonNull(view);
            AndroidUtilities.runOnUIThread(new EmbedBottomSheet$$ExternalSyntheticLambda6(view), 300L);
            return;
        }
        boolean z = this.isYouTube && "inapp".equals(MessagesController.getInstance(this.currentAccount).youtubePipType);
        if ((z || checkInlinePermissions()) && this.progressBar.getVisibility() != 0) {
            if (PipVideoOverlay.show(z, this.parentActivity, this.webView, this.width, this.height)) {
                PipVideoOverlay.setParentSheet(this);
            }
            if (this.isYouTube) {
                runJsCode("hideControls();");
            }
            this.containerView.setTranslationY(0.0f);
            dismissInternal();
        }
    }

    public void lambda$new$4(View view) {
        try {
            ((ClipboardManager) ApplicationLoader.applicationContext.getSystemService("clipboard")).setPrimaryClip(ClipData.newPlainText("label", this.openUrl));
        } catch (Exception e) {
            FileLog.e(e);
        }
        Activity activity = this.parentActivity;
        if (activity instanceof LaunchActivity) {
            ((LaunchActivity) activity).showBulletin(new Function() {
                @Override
                public final Object apply(Object obj) {
                    return ((BulletinFactory) obj).createCopyLinkBulletin();
                }
            });
        }
        dismiss();
    }

    public void lambda$new$5(View view) {
        Browser.openUrl(this.parentActivity, this.openUrl);
        dismiss();
    }

    private void runJsCode(String str) {
        if (Build.VERSION.SDK_INT >= 21) {
            this.webView.evaluateJavascript(str, null);
            return;
        }
        try {
            this.webView.loadUrl("javascript:" + str);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void show(BaseFragment baseFragment, MessageObject messageObject, PhotoViewer.PhotoViewerProvider photoViewerProvider, String str, String str2, String str3, String str4, int i, int i2, int i3, boolean z) {
        TLRPC.MessageMedia messageMedia;
        EmbedBottomSheet embedBottomSheet = instance;
        if (embedBottomSheet != null) {
            embedBottomSheet.destroy();
        }
        if (((messageObject == null || (messageMedia = messageObject.messageOwner.media) == null || messageMedia.webpage == null) ? null : WebPlayerView.getYouTubeVideoId(str4)) != null) {
            PhotoViewer.getInstance().setParentActivity(baseFragment);
            PhotoViewer.getInstance().openPhoto(messageObject, i3, null, 0L, 0L, 0L, photoViewerProvider);
        } else {
            EmbedBottomSheet embedBottomSheet2 = new EmbedBottomSheet(baseFragment.getParentActivity(), str, str2, str3, str4, i, i2, i3);
            embedBottomSheet2.setCalcMandatoryInsets(z);
            embedBottomSheet2.show();
        }
    }

    public static void show(BaseFragment baseFragment, MessageObject messageObject, PhotoViewer.PhotoViewerProvider photoViewerProvider, String str, String str2, String str3, String str4, int i, int i2, boolean z) {
        show(baseFragment, messageObject, photoViewerProvider, str, str2, str3, str4, i, i2, -1, z);
    }

    @Override
    public boolean canDismissWithSwipe() {
        return (this.videoView.getVisibility() == 0 && this.videoView.isInFullscreen()) ? false : true;
    }

    @Override
    protected boolean canDismissWithTouchOutside() {
        return this.fullscreenVideoContainer.getVisibility() != 0;
    }

    public boolean checkInlinePermissions() {
        boolean canDrawOverlays;
        Activity activity = this.parentActivity;
        if (activity == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT < 23) {
            return true;
        }
        canDrawOverlays = Settings.canDrawOverlays(activity);
        if (canDrawOverlays) {
            return true;
        }
        AlertsCreator.createDrawOverlayPermissionDialog(this.parentActivity, null);
        return false;
    }

    public void destroy() {
        WebView webView = this.webView;
        if (webView != null && webView.getVisibility() == 0) {
            this.containerLayout.removeView(this.webView);
            this.webView.stopLoading();
            this.webView.loadUrl("about:blank");
            this.webView.destroy();
        }
        PipVideoOverlay.dismiss();
        WebPlayerView webPlayerView = this.videoView;
        if (webPlayerView != null) {
            webPlayerView.destroy();
        }
        instance = null;
        dismissInternal();
    }

    @Override
    public void dismissInternal() {
        super.dismissInternal();
        OrientationEventListener orientationEventListener = this.orientationEventListener;
        if (orientationEventListener != null) {
            orientationEventListener.disable();
            this.orientationEventListener = null;
        }
    }

    public void exitFromPip() {
        if (this.webView == null || !PipVideoOverlay.isVisible()) {
            return;
        }
        if (ApplicationLoader.mainInterfacePaused) {
            try {
                this.parentActivity.startService(new Intent(ApplicationLoader.applicationContext, (Class<?>) BringAppForegroundService.class));
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        if (this.isYouTube) {
            runJsCode("showControls();");
        }
        ViewGroup viewGroup = (ViewGroup) this.webView.getParent();
        if (viewGroup != null) {
            viewGroup.removeView(this.webView);
        }
        this.containerLayout.addView(this.webView, 0, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, (this.hasDescription ? 22 : 0) + 84));
        setShowWithoutAnimation(true);
        show();
        PipVideoOverlay.dismiss(true);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        if (this.videoView.getVisibility() == 0 && this.videoView.isInitied() && !this.videoView.isInline()) {
            if (configuration.orientation == 2) {
                if (this.videoView.isInFullscreen()) {
                    return;
                }
                this.videoView.enterFullscreen();
            } else if (this.videoView.isInFullscreen()) {
                this.videoView.exitFullscreen();
            }
        }
    }

    @Override
    public void onContainerDraw(Canvas canvas) {
        int i = this.waitingForDraw;
        if (i != 0) {
            int i2 = i - 1;
            this.waitingForDraw = i2;
            if (i2 != 0) {
                this.container.invalidate();
            } else {
                this.videoView.updateTextureImageView();
                PipVideoOverlay.dismiss();
            }
        }
    }

    @Override
    public void onContainerTranslationYChanged(float f) {
        updateTextureViewPosition();
    }

    @Override
    protected boolean onCustomLayout(View view, int i, int i2, int i3, int i4) {
        if (view != this.videoView.getControlsView()) {
            return false;
        }
        updateTextureViewPosition();
        return false;
    }

    @Override
    protected boolean onCustomMeasure(View view, int i, int i2) {
        if (view == this.videoView.getControlsView()) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.width = this.videoView.getMeasuredWidth();
            layoutParams.height = this.videoView.getAspectRatioView().getMeasuredHeight() + (this.videoView.isInFullscreen() ? 0 : AndroidUtilities.dp(10.0f));
        }
        return false;
    }

    public void pause() {
        WebPlayerView webPlayerView = this.videoView;
        if (webPlayerView == null || !webPlayerView.isInitied()) {
            return;
        }
        this.videoView.pause();
    }

    public void updateTextureViewPosition() {
        this.videoView.getAspectRatioView().getLocationInWindow(this.position);
        int[] iArr = this.position;
        iArr[0] = iArr[0] - getLeftInset();
        if (!this.videoView.isInline() && !this.animationInProgress) {
            TextureView textureView = this.videoView.getTextureView();
            textureView.setTranslationX(this.position[0]);
            textureView.setTranslationY(this.position[1]);
            ImageView textureImageView = this.videoView.getTextureImageView();
            if (textureImageView != null) {
                textureImageView.setTranslationX(this.position[0]);
                textureImageView.setTranslationY(this.position[1]);
            }
        }
        View controlsView = this.videoView.getControlsView();
        controlsView.setTranslationY(controlsView.getParent() == this.container ? this.position[1] : 0.0f);
    }
}
