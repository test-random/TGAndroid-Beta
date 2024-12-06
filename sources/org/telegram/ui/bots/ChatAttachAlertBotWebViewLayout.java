package org.telegram.ui.bots;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RenderNode;
import android.os.Build;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.math.MathUtils;
import androidx.core.util.Consumer;
import androidx.core.view.GestureDetectorCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.ChatListItemAnimator;
import java.util.Iterator;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotFullscreenButtons$$ExternalSyntheticApiModelOutline2;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SimpleFloatPropertyCompat;
import org.telegram.ui.bots.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.web.BotWebViewContainer;

public class ChatAttachAlertBotWebViewLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    private ActionBarMenuSubItem addToHomeScreenItem;
    private long botId;
    private int currentAccount;
    private int customActionBarBackground;
    private int customBackground;
    private boolean destroyed;
    private boolean hasCustomActionBarBackground;
    private boolean hasCustomBackground;
    private boolean ignoreLayout;
    private boolean ignoreMeasure;
    private boolean isBotButtonAvailable;
    private long lastSwipeTime;
    private int measureOffsetY;
    private boolean needCloseConfirmation;
    private boolean needReload;
    private ActionBarMenuItem otherItem;
    private long peerId;
    private Runnable pollRunnable;
    private WebProgressView progressView;
    private long queryId;
    private int replyToMsgId;
    public ActionBarMenuSubItem settingsItem;
    private boolean silent;
    private String startCommand;
    private WebViewSwipeContainer swipeContainer;
    private BotWebViewContainer webViewContainer;
    private ValueAnimator webViewScrollAnimator;

    public static class WebProgressView extends View {
        private final SimpleFloatPropertyCompat LOAD_PROGRESS_PROPERTY;
        private Paint bluePaint;
        private float loadProgress;
        private Theme.ResourcesProvider resourcesProvider;
        private SpringAnimation springAnimation;

        public WebProgressView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.LOAD_PROGRESS_PROPERTY = new SimpleFloatPropertyCompat("loadProgress", new SimpleFloatPropertyCompat.Getter() {
                @Override
                public final float get(Object obj) {
                    float f;
                    f = ((ChatAttachAlertBotWebViewLayout.WebProgressView) obj).loadProgress;
                    return f;
                }
            }, new SimpleFloatPropertyCompat.Setter() {
                @Override
                public final void set(Object obj, float f) {
                    ((ChatAttachAlertBotWebViewLayout.WebProgressView) obj).setLoadProgress(f);
                }
            }).setMultiplier(100.0f);
            Paint paint = new Paint(1);
            this.bluePaint = paint;
            this.resourcesProvider = resourcesProvider;
            paint.setColor(getThemedColor(Theme.key_featuredStickers_addButton));
            this.bluePaint.setStyle(Paint.Style.STROKE);
            this.bluePaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            this.bluePaint.setStrokeCap(Paint.Cap.ROUND);
        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            if (this.loadProgress > 0.0f) {
                float height = getHeight() - (this.bluePaint.getStrokeWidth() / 2.0f);
                canvas.drawLine(0.0f, height, getWidth() * this.loadProgress, height, this.bluePaint);
            }
        }

        protected int getThemedColor(int i) {
            return Theme.getColor(i, this.resourcesProvider);
        }

        @Override
        protected void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.springAnimation = new SpringAnimation(this, this.LOAD_PROGRESS_PROPERTY).setSpring(new SpringForce().setStiffness(400.0f).setDampingRatio(1.0f));
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.springAnimation.cancel();
            this.springAnimation = null;
        }

        public void setLoadProgress(float f) {
            this.loadProgress = f;
            invalidate();
        }

        public void setLoadProgressAnimated(float f) {
            SpringAnimation springAnimation = this.springAnimation;
            if (springAnimation == null) {
                setLoadProgress(f);
            } else {
                springAnimation.getSpring().setFinalPosition(f * 100.0f);
                this.springAnimation.start();
            }
        }
    }

    public static class WebViewSwipeContainer extends FrameLayout {
        public static final SimpleFloatPropertyCompat SWIPE_OFFSET_Y = new SimpleFloatPropertyCompat("swipeOffsetY", new SimpleFloatPropertyCompat.Getter() {
            @Override
            public final float get(Object obj) {
                return ((ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer) obj).getSwipeOffsetY();
            }
        }, new SimpleFloatPropertyCompat.Setter() {
            @Override
            public final void set(Object obj, float f) {
                ((ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer) obj).setSwipeOffsetY(f);
            }
        });
        private boolean allowFullSizeSwipe;
        private boolean allowSwipes;
        public boolean allowedScrollX;
        public boolean allowedScrollY;
        private Delegate delegate;
        private float drawnSwipeOffsetY;
        private boolean flingInProgress;
        private boolean fullsize;
        private final GestureDetectorCompat gestureDetector;
        private GenericProvider isKeyboardVisible;
        public boolean isScrolling;
        private boolean isSwipeDisallowed;
        private boolean isSwipeOffsetAnimationDisallowed;
        private final float minscroll;
        public float offsetY;
        private SpringAnimation offsetYAnimator;
        public boolean opened;
        private float pendingOffsetY;
        private float pendingSwipeOffsetY;
        private long pressDownTime;
        private float pressDownX;
        private float pressDownY;
        private Object renderNode;
        private SpringAnimation scrollAnimator;
        private Runnable scrollEndListener;
        private Runnable scrollListener;
        private boolean scrolledOut;
        public boolean shouldWaitWebViewScroll;
        public boolean stickToEdges;
        private float swipeOffsetY;
        private int swipeStickyRange;
        private float sy;
        public float topActionBarOffsetY;
        private BotWebViewContainer.MyWebView webView;

        public interface Delegate {
            void onDismiss(boolean z);
        }

        public WebViewSwipeContainer(Context context) {
            super(context);
            this.topActionBarOffsetY = ActionBar.getCurrentActionBarHeight();
            this.offsetY = 0.0f;
            this.pendingOffsetY = -1.0f;
            this.pendingSwipeOffsetY = -2.1474836E9f;
            this.isKeyboardVisible = new GenericProvider() {
                @Override
                public final Object provide(Object obj) {
                    Boolean lambda$new$0;
                    lambda$new$0 = ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.lambda$new$0((Void) obj);
                    return lambda$new$0;
                }
            };
            this.allowSwipes = true;
            this.sy = 0.0f;
            this.scrolledOut = false;
            this.minscroll = AndroidUtilities.dp(60.0f);
            this.stickToEdges = true;
            final int scaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
            this.gestureDetector = new GestureDetectorCompat(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                    WebViewSwipeContainer webViewSwipeContainer;
                    float f3;
                    if (!WebViewSwipeContainer.this.isSwipeDisallowed && WebViewSwipeContainer.this.allowSwipes && (!WebViewSwipeContainer.this.fullsize || WebViewSwipeContainer.this.allowFullSizeSwipe)) {
                        WebViewSwipeContainer webViewSwipeContainer2 = WebViewSwipeContainer.this;
                        if (!webViewSwipeContainer2.shouldWaitWebViewScroll || webViewSwipeContainer2.allowingScroll(false)) {
                            float distance = AndroidUtilities.distance(motionEvent.getX(), motionEvent.getY(), motionEvent2.getX(), motionEvent2.getY());
                            float eventTime = (float) (motionEvent2.getEventTime() - motionEvent.getEventTime());
                            if (f2 >= AndroidUtilities.dp(650.0f) && ((distance > AndroidUtilities.dp(200.0f) || eventTime > 250.0f) && (WebViewSwipeContainer.this.webView == null || WebViewSwipeContainer.this.webView.getScrollY() == 0))) {
                                WebViewSwipeContainer.this.flingInProgress = true;
                                if (WebViewSwipeContainer.this.swipeOffsetY >= WebViewSwipeContainer.this.swipeStickyRange || WebViewSwipeContainer.this.fullsize) {
                                    if (WebViewSwipeContainer.this.fullsize && WebViewSwipeContainer.this.allowFullSizeSwipe) {
                                        float f4 = WebViewSwipeContainer.this.drawnSwipeOffsetY;
                                        WebViewSwipeContainer webViewSwipeContainer3 = WebViewSwipeContainer.this;
                                        if (f4 == (-webViewSwipeContainer3.offsetY) + webViewSwipeContainer3.topActionBarOffsetY || (webViewSwipeContainer3.swipeOffsetY <= (-WebViewSwipeContainer.this.swipeStickyRange) && f2 < AndroidUtilities.dp(1200.0f))) {
                                            webViewSwipeContainer = WebViewSwipeContainer.this;
                                            f3 = (-webViewSwipeContainer.offsetY) + webViewSwipeContainer.topActionBarOffsetY;
                                        }
                                    }
                                    if (WebViewSwipeContainer.this.delegate != null) {
                                        WebViewSwipeContainer.this.delegate.onDismiss(false);
                                    }
                                    return true;
                                }
                                webViewSwipeContainer = WebViewSwipeContainer.this;
                                f3 = 0.0f;
                                webViewSwipeContainer.stickTo(f3);
                                return true;
                            }
                            if (f2 <= -700.0f) {
                                float f5 = WebViewSwipeContainer.this.swipeOffsetY;
                                WebViewSwipeContainer webViewSwipeContainer4 = WebViewSwipeContainer.this;
                                if (f5 > (-webViewSwipeContainer4.offsetY) + webViewSwipeContainer4.topActionBarOffsetY) {
                                    webViewSwipeContainer4.flingInProgress = true;
                                    WebViewSwipeContainer webViewSwipeContainer5 = WebViewSwipeContainer.this;
                                    webViewSwipeContainer5.stickTo((-webViewSwipeContainer5.offsetY) + webViewSwipeContainer5.topActionBarOffsetY);
                                    return true;
                                }
                            }
                        }
                    }
                    return false;
                }

                @Override
                public boolean onScroll(android.view.MotionEvent r12, android.view.MotionEvent r13, float r14, float r15) {
                    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.AnonymousClass1.onScroll(android.view.MotionEvent, android.view.MotionEvent, float, float):boolean");
                }
            });
            updateStickyRange();
        }

        static float access$1124(WebViewSwipeContainer webViewSwipeContainer, float f) {
            float f2 = webViewSwipeContainer.swipeOffsetY - f;
            webViewSwipeContainer.swipeOffsetY = f2;
            return f2;
        }

        public float cap(float f) {
            if (this.scrolledOut) {
                return f;
            }
            float f2 = this.sy + f;
            this.sy = f2;
            float abs = Math.abs(f2);
            float f3 = this.minscroll;
            if (abs <= f3) {
                return 0.0f;
            }
            this.scrolledOut = true;
            float f4 = this.sy;
            return f4 > 0.0f ? f4 - f3 : f4 + f3;
        }

        public static Boolean lambda$new$0(Void r0) {
            return Boolean.FALSE;
        }

        public void lambda$setOffsetY$1(float f, float f2, boolean z, float f3, DynamicAnimation dynamicAnimation, float f4, float f5) {
            this.offsetY = f4;
            float f6 = f == 0.0f ? 1.0f : (f4 - f2) / f;
            if (z) {
                this.swipeOffsetY = MathUtils.clamp(this.swipeOffsetY - (f6 * Math.max(0.0f, f)), (-this.offsetY) + this.topActionBarOffsetY, (getHeight() - this.offsetY) + this.topActionBarOffsetY);
            }
            SpringAnimation springAnimation = this.scrollAnimator;
            if (springAnimation != null && springAnimation.getSpring().getFinalPosition() == (-f2) + this.topActionBarOffsetY) {
                this.scrollAnimator.getSpring().setFinalPosition((-f3) + this.topActionBarOffsetY);
            }
            invalidateTranslation();
        }

        public void lambda$setOffsetY$2(float f, DynamicAnimation dynamicAnimation, boolean z, float f2, float f3) {
            this.offsetYAnimator = null;
            if (z) {
                this.pendingOffsetY = f;
            } else {
                this.offsetY = f;
                invalidateTranslation();
            }
        }

        public void lambda$stickTo$3(Runnable runnable, DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
            if (dynamicAnimation == this.scrollAnimator) {
                this.scrollAnimator = null;
                if (runnable != null) {
                    runnable.run();
                }
                Runnable runnable2 = this.scrollEndListener;
                if (runnable2 != null) {
                    runnable2.run();
                }
                float f3 = this.pendingOffsetY;
                if (f3 != -1.0f) {
                    boolean z2 = this.isSwipeOffsetAnimationDisallowed;
                    this.isSwipeOffsetAnimationDisallowed = true;
                    setOffsetY(f3);
                    this.pendingOffsetY = -1.0f;
                    this.isSwipeOffsetAnimationDisallowed = z2;
                }
                this.pendingSwipeOffsetY = -2.1474836E9f;
            }
        }

        public void updateDrawn() {
            this.drawnSwipeOffsetY = this.swipeOffsetY;
        }

        private void updateStickyRange() {
            Point point = AndroidUtilities.displaySize;
            this.swipeStickyRange = AndroidUtilities.dp(point.x > point.y ? 8.0f : 64.0f);
        }

        public void allowThisScroll(boolean z, boolean z2) {
            this.allowedScrollX = z;
            this.allowedScrollY = z2;
        }

        public boolean allowingScroll(boolean z) {
            BotWebViewContainer.MyWebView myWebView = this.webView;
            return myWebView == null || !myWebView.injectedJS || (!z ? !this.allowedScrollY : !this.allowedScrollX);
        }

        public void cancelStickTo() {
            SpringAnimation springAnimation = this.offsetYAnimator;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            SpringAnimation springAnimation2 = this.scrollAnimator;
            if (springAnimation2 != null) {
                springAnimation2.cancel();
            }
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            Canvas canvas2;
            if (!canvas.isHardwareAccelerated()) {
                super.dispatchDraw(canvas);
                return;
            }
            Object obj = this.renderNode;
            if (obj != null) {
                RenderNode m = BotFullscreenButtons$$ExternalSyntheticApiModelOutline2.m(obj);
                m.setPosition(0, 0, getWidth(), getHeight());
                canvas2 = m.beginRecording();
            } else {
                canvas2 = canvas;
            }
            super.dispatchDraw(canvas2);
            Object obj2 = this.renderNode;
            if (obj2 != null) {
                RenderNode m2 = BotFullscreenButtons$$ExternalSyntheticApiModelOutline2.m(obj2);
                m2.endRecording();
                canvas.drawRenderNode(m2);
            }
        }

        @Override
        public boolean dispatchTouchEvent(android.view.MotionEvent r11) {
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.dispatchTouchEvent(android.view.MotionEvent):boolean");
        }

        public float getOffsetY() {
            return this.offsetY;
        }

        public Object getRenderNode() {
            if (this.renderNode == null && Build.VERSION.SDK_INT >= 31) {
                this.renderNode = new RenderNode("WebViewSwipeContainer");
            }
            return this.renderNode;
        }

        public float getSwipeOffsetY() {
            return this.swipeOffsetY;
        }

        public float getTopActionBarOffsetY() {
            return this.topActionBarOffsetY;
        }

        public void invalidateTranslation() {
            setTranslationY(Math.max(this.topActionBarOffsetY, this.offsetY + this.swipeOffsetY));
            AndroidUtilities.cancelRunOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.this.updateDrawn();
                }
            });
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.this.updateDrawn();
                }
            });
            Runnable runnable = this.scrollListener;
            if (runnable != null) {
                runnable.run();
            }
            if (Bulletin.getVisibleBulletin() != null) {
                Bulletin.getVisibleBulletin().updatePosition();
            }
        }

        public boolean isAllowedSwipes() {
            return this.allowSwipes;
        }

        public boolean isFullSize() {
            return this.fullsize;
        }

        public boolean isSwipeInProgress() {
            return this.isScrolling;
        }

        @Override
        protected void onConfigurationChanged(Configuration configuration) {
            super.onConfigurationChanged(configuration);
            updateStickyRange();
        }

        @Override
        public void requestDisallowInterceptTouchEvent(boolean z) {
            super.requestDisallowInterceptTouchEvent(z);
            if (z) {
                this.isSwipeDisallowed = true;
                this.isScrolling = false;
            }
        }

        public void setAllowFullSizeSwipe(boolean z) {
            this.allowFullSizeSwipe = z;
        }

        public void setAllowSwipes(boolean z) {
            if (this.allowSwipes != z) {
                this.allowSwipes = z;
            }
        }

        public void setDelegate(Delegate delegate) {
            this.delegate = delegate;
        }

        public void setForceOffsetY(float f) {
            this.offsetY = f;
            invalidateTranslation();
        }

        public void setFullSize(boolean z) {
            float f;
            if (this.fullsize != z) {
                this.fullsize = z;
                if (!z) {
                    f = 0.0f;
                } else if (!this.opened) {
                    return;
                } else {
                    f = (-getOffsetY()) + getTopActionBarOffsetY();
                }
                stickTo(f);
            }
        }

        public void setIsKeyboardVisible(GenericProvider<Void, Boolean> genericProvider) {
            this.isKeyboardVisible = genericProvider;
        }

        public void setOffsetY(final float f) {
            if (this.pendingSwipeOffsetY != -2.1474836E9f) {
                this.pendingOffsetY = f;
                return;
            }
            SpringAnimation springAnimation = this.offsetYAnimator;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            final float f2 = this.offsetY;
            final float f3 = f - f2;
            final boolean z = Math.abs((this.swipeOffsetY + f2) - this.topActionBarOffsetY) <= ((float) AndroidUtilities.dp(1.0f));
            if (this.isSwipeOffsetAnimationDisallowed) {
                this.offsetY = f;
                if (z) {
                    this.swipeOffsetY = MathUtils.clamp(this.swipeOffsetY - Math.max(0.0f, f3), (-this.offsetY) + this.topActionBarOffsetY, (getHeight() - this.offsetY) + this.topActionBarOffsetY);
                }
                invalidateTranslation();
                return;
            }
            SpringAnimation springAnimation2 = this.offsetYAnimator;
            if (springAnimation2 != null) {
                springAnimation2.cancel();
            }
            SpringAnimation springAnimation3 = (SpringAnimation) ((SpringAnimation) new SpringAnimation(new FloatValueHolder(f2)).setSpring(new SpringForce(f).setStiffness(1400.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() {
                @Override
                public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f4, float f5) {
                    ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.this.lambda$setOffsetY$1(f3, f2, z, f, dynamicAnimation, f4, f5);
                }
            })).addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f4, float f5) {
                    ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.this.lambda$setOffsetY$2(f, dynamicAnimation, z2, f4, f5);
                }
            });
            this.offsetYAnimator = springAnimation3;
            springAnimation3.start();
        }

        public void setScrollEndListener(Runnable runnable) {
            this.scrollEndListener = runnable;
        }

        public void setScrollListener(Runnable runnable) {
            this.scrollListener = runnable;
        }

        public void setShouldWaitWebViewScroll(boolean z) {
            this.shouldWaitWebViewScroll = z;
        }

        public void setSwipeOffsetAnimationDisallowed(boolean z) {
            this.isSwipeOffsetAnimationDisallowed = z;
        }

        public void setSwipeOffsetY(float f) {
            this.swipeOffsetY = f;
            invalidateTranslation();
        }

        public void setTopActionBarOffsetY(float f) {
            this.topActionBarOffsetY = f;
            invalidateTranslation();
        }

        @Override
        public void setTranslationY(float f) {
            super.setTranslationY(f);
        }

        public void setWebView(BotWebViewContainer.MyWebView myWebView) {
            this.webView = myWebView;
        }

        public void stickTo(float f) {
            stickTo(f, null);
        }

        public void stickTo(float f, Runnable runnable) {
            stickTo(f, false, runnable);
        }

        public void stickTo(float f, boolean z, final Runnable runnable) {
            SpringAnimation springAnimation;
            if (this.fullsize && !z) {
                f = (-getOffsetY()) + getTopActionBarOffsetY();
            }
            if (this.swipeOffsetY == f || ((springAnimation = this.scrollAnimator) != null && springAnimation.getSpring().getFinalPosition() == f)) {
                if (runnable != null) {
                    runnable.run();
                }
                Runnable runnable2 = this.scrollEndListener;
                if (runnable2 != null) {
                    runnable2.run();
                    return;
                }
                return;
            }
            this.pendingSwipeOffsetY = f;
            SpringAnimation springAnimation2 = this.offsetYAnimator;
            if (springAnimation2 != null) {
                springAnimation2.cancel();
            }
            SpringAnimation springAnimation3 = this.scrollAnimator;
            if (springAnimation3 != null) {
                springAnimation3.cancel();
            }
            SpringAnimation springAnimation4 = (SpringAnimation) new SpringAnimation(this, SWIPE_OFFSET_Y, f).setSpring(new SpringForce(f).setStiffness(1200.0f).setDampingRatio(1.0f)).addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                @Override
                public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f2, float f3) {
                    ChatAttachAlertBotWebViewLayout.WebViewSwipeContainer.this.lambda$stickTo$3(runnable, dynamicAnimation, z2, f2, f3);
                }
            });
            this.scrollAnimator = springAnimation4;
            springAnimation4.start();
        }
    }

    public ChatAttachAlertBotWebViewLayout(ChatAttachAlert chatAttachAlert, Context context, Theme.ResourcesProvider resourcesProvider) {
        super(chatAttachAlert, context, resourcesProvider);
        this.pollRunnable = new Runnable() {
            @Override
            public final void run() {
                ChatAttachAlertBotWebViewLayout.this.lambda$new$2();
            }
        };
        ActionBarMenuItem addItem = this.parentAlert.actionBar.createMenu().addItem(0, R.drawable.ic_ab_other);
        this.otherItem = addItem;
        addItem.addSubItem(R.id.menu_open_bot, R.drawable.msg_bot, LocaleController.getString(R.string.BotWebViewOpenBot));
        ActionBarMenuSubItem addSubItem = this.otherItem.addSubItem(R.id.menu_settings, R.drawable.msg_settings, LocaleController.getString(R.string.BotWebViewSettings));
        this.settingsItem = addSubItem;
        addSubItem.setVisibility(8);
        this.otherItem.addSubItem(R.id.menu_reload_page, R.drawable.msg_retry, LocaleController.getString(R.string.BotWebViewReloadPage));
        ActionBarMenuSubItem addSubItem2 = this.otherItem.addSubItem(R.id.menu_add_to_home_screen_bot, R.drawable.msg_home, LocaleController.getString(R.string.AddShortcut));
        this.addToHomeScreenItem = addSubItem2;
        addSubItem2.setVisibility(8);
        this.otherItem.addSubItem(R.id.menu_tos_bot, R.drawable.menu_intro, LocaleController.getString(R.string.BotWebViewToS));
        this.otherItem.addSubItem(R.id.menu_delete_bot, R.drawable.msg_delete, LocaleController.getString(R.string.BotWebViewDeleteBot));
        this.webViewContainer = new BotWebViewContainer(context, resourcesProvider, getThemedColor(Theme.key_dialogBackground), true) {
            @Override
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && !ChatAttachAlertBotWebViewLayout.this.isBotButtonAvailable) {
                    ChatAttachAlertBotWebViewLayout.this.isBotButtonAvailable = true;
                    ChatAttachAlertBotWebViewLayout.this.webViewContainer.restoreButtonData();
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            @Override
            public void onWebViewCreated(BotWebViewContainer.MyWebView myWebView) {
                super.onWebViewCreated(myWebView);
                ChatAttachAlertBotWebViewLayout.this.swipeContainer.setWebView(myWebView);
            }
        };
        WebViewSwipeContainer webViewSwipeContainer = new WebViewSwipeContainer(context) {
            @Override
            protected void onMeasure(int i, int i2) {
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(((View.MeasureSpec.getSize(i2) - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(84.0f)) + ChatAttachAlertBotWebViewLayout.this.measureOffsetY, 1073741824));
            }
        };
        this.swipeContainer = webViewSwipeContainer;
        webViewSwipeContainer.addView(this.webViewContainer, LayoutHelper.createFrame(-1, -1.0f));
        this.swipeContainer.setScrollListener(new Runnable() {
            @Override
            public final void run() {
                ChatAttachAlertBotWebViewLayout.this.lambda$new$3();
            }
        });
        this.swipeContainer.setScrollEndListener(new Runnable() {
            @Override
            public final void run() {
                ChatAttachAlertBotWebViewLayout.this.lambda$new$4();
            }
        });
        this.swipeContainer.setDelegate(new WebViewSwipeContainer.Delegate() {
            @Override
            public final void onDismiss(boolean z) {
                ChatAttachAlertBotWebViewLayout.this.lambda$new$5(z);
            }
        });
        this.swipeContainer.setIsKeyboardVisible(new GenericProvider() {
            @Override
            public final Object provide(Object obj) {
                Boolean lambda$new$6;
                lambda$new$6 = ChatAttachAlertBotWebViewLayout.this.lambda$new$6((Void) obj);
                return lambda$new$6;
            }
        });
        addView(this.swipeContainer, LayoutHelper.createFrame(-1, -1.0f));
        WebProgressView webProgressView = new WebProgressView(context, resourcesProvider);
        this.progressView = webProgressView;
        addView(webProgressView, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 84.0f));
        this.webViewContainer.setWebViewProgressListener(new Consumer() {
            @Override
            public final void accept(Object obj) {
                ChatAttachAlertBotWebViewLayout.this.lambda$new$8((Float) obj);
            }
        });
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
    }

    public void lambda$new$0(TLRPC.TL_error tL_error) {
        if (this.destroyed) {
            return;
        }
        if (tL_error != null) {
            this.parentAlert.dismiss();
        } else {
            AndroidUtilities.runOnUIThread(this.pollRunnable, 60000L);
        }
    }

    public void lambda$new$1(TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatAttachAlertBotWebViewLayout.this.lambda$new$0(tL_error);
            }
        });
    }

    public void lambda$new$2() {
        TLRPC.ChatFull chatFull;
        TLRPC.Peer peer;
        if (this.destroyed) {
            return;
        }
        TLRPC.TL_messages_prolongWebView tL_messages_prolongWebView = new TLRPC.TL_messages_prolongWebView();
        tL_messages_prolongWebView.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.botId);
        tL_messages_prolongWebView.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.peerId);
        tL_messages_prolongWebView.query_id = this.queryId;
        tL_messages_prolongWebView.silent = this.silent;
        if (this.replyToMsgId != 0) {
            tL_messages_prolongWebView.reply_to = SendMessagesHelper.getInstance(this.currentAccount).createReplyInput(this.replyToMsgId);
            tL_messages_prolongWebView.flags |= 1;
        }
        if (this.peerId < 0 && (chatFull = MessagesController.getInstance(this.currentAccount).getChatFull(-this.peerId)) != null && (peer = chatFull.default_send_as) != null) {
            tL_messages_prolongWebView.send_as = MessagesController.getInstance(this.currentAccount).getInputPeer(peer);
            tL_messages_prolongWebView.flags |= 8192;
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_prolongWebView, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatAttachAlertBotWebViewLayout.this.lambda$new$1(tLObject, tL_error);
            }
        });
    }

    public void lambda$new$3() {
        this.parentAlert.updateLayout(this, true, 0);
        this.webViewContainer.invalidateViewPortHeight();
        this.lastSwipeTime = System.currentTimeMillis();
    }

    public void lambda$new$4() {
        this.webViewContainer.invalidateViewPortHeight(true);
    }

    public void lambda$new$5(boolean z) {
        if (onCheckDismissByUser()) {
            return;
        }
        this.swipeContainer.stickTo(0.0f);
    }

    public Boolean lambda$new$6(Void r2) {
        return Boolean.valueOf(this.parentAlert.sizeNotifierFrameLayout.getKeyboardHeight() >= AndroidUtilities.dp(20.0f));
    }

    public void lambda$new$7(ValueAnimator valueAnimator) {
        this.progressView.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    public void lambda$new$8(Float f) {
        this.progressView.setLoadProgressAnimated(f.floatValue());
        if (f.floatValue() == 1.0f) {
            ValueAnimator duration = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(200L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    ChatAttachAlertBotWebViewLayout.this.lambda$new$7(valueAnimator);
                }
            });
            duration.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animator) {
                    ChatAttachAlertBotWebViewLayout.this.progressView.setVisibility(8);
                }
            });
            duration.start();
            requestEnableKeyboard();
        }
    }

    public void lambda$onCheckDismissByUser$9(DialogInterface dialogInterface, int i) {
        this.parentAlert.dismiss();
    }

    public void lambda$onPanTransitionStart$10(ValueAnimator valueAnimator) {
        int intValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        if (this.webViewContainer.getWebView() != null) {
            this.webViewContainer.getWebView().setScrollY(intValue);
        }
    }

    public void lambda$onShown$11() {
        this.webViewContainer.restoreButtonData();
    }

    public void lambda$requestWebView$12(TLObject tLObject, int i) {
        if (tLObject instanceof TLRPC.TL_webViewResultUrl) {
            TLRPC.TL_webViewResultUrl tL_webViewResultUrl = (TLRPC.TL_webViewResultUrl) tLObject;
            this.queryId = tL_webViewResultUrl.query_id;
            this.webViewContainer.loadUrl(i, tL_webViewResultUrl.url);
            AndroidUtilities.runOnUIThread(this.pollRunnable);
        }
    }

    public void lambda$requestWebView$13(final int i, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatAttachAlertBotWebViewLayout.this.lambda$requestWebView$12(tLObject, i);
            }
        });
    }

    public void lambda$showJustAddedBulletin$14(String str) {
        BulletinFactory.of(this.parentAlert.getContainer(), this.resourcesProvider).createSimpleBulletin(R.raw.contact_check, AndroidUtilities.replaceTags(str)).setDuration(5000).show(true);
    }

    public void requestEnableKeyboard() {
        BaseFragment baseFragment = this.parentAlert.getBaseFragment();
        if ((baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).contentView.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            AndroidUtilities.hideKeyboard(this.parentAlert.baseFragment.getFragmentView());
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    ChatAttachAlertBotWebViewLayout.this.requestEnableKeyboard();
                }
            }, 250L);
        } else {
            this.parentAlert.getWindow().setSoftInputMode(20);
            setFocusable(true);
            this.parentAlert.setFocusable(true);
        }
    }

    public boolean canExpandByRequest() {
        return !this.swipeContainer.isSwipeInProgress();
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i != NotificationCenter.webViewResultSent) {
            if (i == NotificationCenter.didSetNewTheme) {
                this.webViewContainer.updateFlickerBackgroundColor(getThemedColor(Theme.key_dialogBackground));
            }
        } else {
            if (this.queryId == ((Long) objArr[0]).longValue()) {
                this.webViewContainer.destroyWebView();
                this.needReload = true;
                this.parentAlert.dismiss();
            }
        }
    }

    public void disallowSwipeOffsetAnimation() {
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(true);
    }

    @Override
    public int getButtonsHideOffset() {
        return ((int) this.swipeContainer.getTopActionBarOffsetY()) + AndroidUtilities.dp(12.0f);
    }

    @Override
    public int getCurrentItemTop() {
        return (int) (this.swipeContainer.getSwipeOffsetY() + this.swipeContainer.getOffsetY());
    }

    @Override
    public int getCustomActionBarBackground() {
        return this.customActionBarBackground;
    }

    @Override
    public int getCustomBackground() {
        return this.customBackground;
    }

    @Override
    public int getFirstOffset() {
        return getListTopPadding() + AndroidUtilities.dp(56.0f);
    }

    @Override
    public int getListTopPadding() {
        return (int) this.swipeContainer.getOffsetY();
    }

    public String getStartCommand() {
        return this.startCommand;
    }

    public BotWebViewContainer getWebViewContainer() {
        return this.webViewContainer;
    }

    @Override
    public boolean hasCustomActionBarBackground() {
        return this.hasCustomActionBarBackground;
    }

    @Override
    public boolean hasCustomBackground() {
        return this.hasCustomBackground;
    }

    public boolean isBotButtonAvailable() {
        return this.isBotButtonAvailable;
    }

    public boolean needReload() {
        if (!this.needReload) {
            return false;
        }
        this.needReload = false;
        return true;
    }

    @Override
    public int needsActionBar() {
        return 1;
    }

    @Override
    public boolean onBackPressed() {
        if (this.webViewContainer.onBackPressed()) {
            return true;
        }
        onCheckDismissByUser();
        return true;
    }

    public boolean onCheckDismissByUser() {
        if (!this.needCloseConfirmation) {
            this.parentAlert.dismiss();
            return true;
        }
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId));
        AlertDialog create = new AlertDialog.Builder(getContext()).setTitle(user != null ? ContactsController.formatName(user.first_name, user.last_name) : null).setMessage(LocaleController.getString(R.string.BotWebViewChangesMayNotBeSaved)).setPositiveButton(LocaleController.getString(R.string.BotWebViewCloseAnyway), new DialogInterface.OnClickListener() {
            @Override
            public final void onClick(DialogInterface dialogInterface, int i) {
                ChatAttachAlertBotWebViewLayout.this.lambda$onCheckDismissByUser$9(dialogInterface, i);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
        create.show();
        ((TextView) create.getButton(-1)).setTextColor(getThemedColor(Theme.key_text_RedBold));
        return false;
    }

    @Override
    public void onDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.webViewResultSent);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
        ActionBarMenu createMenu = this.parentAlert.actionBar.createMenu();
        this.otherItem.removeAllSubItems();
        createMenu.removeView(this.otherItem);
        this.webViewContainer.destroyWebView();
        this.destroyed = true;
        AndroidUtilities.cancelRunOnUIThread(this.pollRunnable);
    }

    @Override
    public boolean onDismissWithTouchOutside() {
        onCheckDismissByUser();
        return false;
    }

    @Override
    public void onHidden() {
        super.onHidden();
        this.parentAlert.setFocusable(false);
        this.parentAlert.getWindow().setSoftInputMode(48);
    }

    @Override
    public void onHide() {
        super.onHide();
        this.otherItem.setVisibility(8);
        this.isBotButtonAvailable = false;
        if (!this.webViewContainer.isBackButtonVisible()) {
            AndroidUtilities.updateImageViewImageAnimated(this.parentAlert.actionBar.getBackButton(), R.drawable.ic_ab_back);
        }
        this.parentAlert.actionBar.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
        if (this.webViewContainer.hasUserPermissions()) {
            this.webViewContainer.destroyWebView();
            this.needReload = true;
        }
    }

    @Override
    protected void onMeasure(int i, int i2) {
        if (this.ignoreMeasure) {
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
        } else {
            super.onMeasure(i, i2);
        }
    }

    @Override
    public void onMenuItemClick(int i) {
        if (i == -1) {
            if (this.webViewContainer.onBackPressed()) {
                return;
            }
            onCheckDismissByUser();
            return;
        }
        if (i == R.id.menu_open_bot) {
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", this.botId);
            this.parentAlert.baseFragment.presentFragment(new ChatActivity(bundle));
            this.parentAlert.dismiss();
            return;
        }
        if (i == R.id.menu_reload_page) {
            if (this.webViewContainer.getWebView() != null) {
                this.webViewContainer.getWebView().animate().cancel();
                this.webViewContainer.getWebView().animate().alpha(0.0f).start();
            }
            this.progressView.setLoadProgress(0.0f);
            this.progressView.setAlpha(1.0f);
            this.progressView.setVisibility(0);
            this.webViewContainer.setBotUser(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId)));
            this.webViewContainer.loadFlickerAndSettingsItem(this.currentAccount, this.botId, this.settingsItem);
            this.webViewContainer.reload();
            return;
        }
        if (i == R.id.menu_delete_bot) {
            Iterator<TLRPC.TL_attachMenuBot> it = MediaDataController.getInstance(this.currentAccount).getAttachMenuBots().bots.iterator();
            while (it.hasNext()) {
                TLRPC.TL_attachMenuBot next = it.next();
                if (next.bot_id == this.botId) {
                    this.parentAlert.onLongClickBotButton(next, MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId)));
                    return;
                }
            }
            return;
        }
        if (i == R.id.menu_settings) {
            this.webViewContainer.onSettingsButtonPressed();
        } else if (i == R.id.menu_add_to_home_screen_bot) {
            MediaDataController.getInstance(this.currentAccount).installShortcut(this.botId, MediaDataController.SHORTCUT_TYPE_ATTACHED_BOT);
        } else if (i == R.id.menu_tos_bot) {
            Browser.openUrl(getContext(), LocaleController.getString(R.string.BotWebViewToSLink));
        }
    }

    @Override
    public void onPanTransitionEnd() {
        this.ignoreMeasure = false;
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(false);
        this.webViewContainer.setViewPortByMeasureSuppressed(false);
        requestLayout();
    }

    @Override
    public void onPanTransitionStart(boolean z, int i) {
        boolean z2;
        if (z) {
            this.webViewContainer.setViewPortByMeasureSuppressed(true);
            float topActionBarOffsetY = (-this.swipeContainer.getOffsetY()) + this.swipeContainer.getTopActionBarOffsetY();
            if (this.swipeContainer.getSwipeOffsetY() != topActionBarOffsetY) {
                this.swipeContainer.stickTo(topActionBarOffsetY);
                z2 = true;
            } else {
                z2 = false;
            }
            int measureKeyboardHeight = this.parentAlert.sizeNotifierFrameLayout.measureKeyboardHeight() + i;
            setMeasuredDimension(getMeasuredWidth(), i);
            this.ignoreMeasure = true;
            this.swipeContainer.setSwipeOffsetAnimationDisallowed(true);
            if (z2) {
                return;
            }
            ValueAnimator valueAnimator = this.webViewScrollAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.webViewScrollAnimator = null;
            }
            if (this.webViewContainer.getWebView() != null) {
                int scrollY = this.webViewContainer.getWebView().getScrollY();
                final int i2 = (measureKeyboardHeight - i) + scrollY;
                ValueAnimator duration = ValueAnimator.ofInt(scrollY, i2).setDuration(250L);
                this.webViewScrollAnimator = duration;
                duration.setInterpolator(ChatListItemAnimator.DEFAULT_INTERPOLATOR);
                this.webViewScrollAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        ChatAttachAlertBotWebViewLayout.this.lambda$onPanTransitionStart$10(valueAnimator2);
                    }
                });
                this.webViewScrollAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (ChatAttachAlertBotWebViewLayout.this.webViewContainer.getWebView() != null) {
                            ChatAttachAlertBotWebViewLayout.this.webViewContainer.getWebView().setScrollY(i2);
                        }
                        if (animator == ChatAttachAlertBotWebViewLayout.this.webViewScrollAnimator) {
                            ChatAttachAlertBotWebViewLayout.this.webViewScrollAnimator = null;
                        }
                    }
                });
                this.webViewScrollAnimator.start();
            }
        }
    }

    @Override
    public void onPreMeasure(int r3, int r4) {
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.bots.ChatAttachAlertBotWebViewLayout.onPreMeasure(int, int):void");
    }

    @Override
    public void onShow(ChatAttachAlert.AttachAlertLayout attachAlertLayout) {
        CharSequence userName = UserObject.getUserName(MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId)));
        try {
            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(AndroidUtilities.dp(20.0f));
            userName = Emoji.replaceEmoji(userName, textPaint.getFontMetricsInt(), false);
        } catch (Exception unused) {
        }
        this.parentAlert.actionBar.setTitle(userName);
        this.swipeContainer.setSwipeOffsetY(0.0f);
        if (this.webViewContainer.getWebView() != null) {
            this.webViewContainer.getWebView().scrollTo(0, 0);
        }
        if (this.parentAlert.getBaseFragment() != null) {
            this.webViewContainer.setParentActivity(this.parentAlert.getBaseFragment().getParentActivity());
        }
        this.otherItem.setVisibility(0);
        if (this.webViewContainer.isBackButtonVisible()) {
            return;
        }
        AndroidUtilities.updateImageViewImageAnimated(this.parentAlert.actionBar.getBackButton(), R.drawable.ic_close_white);
    }

    @Override
    public void onShown() {
        if (this.webViewContainer.isPageLoaded()) {
            requestEnableKeyboard();
        }
        this.swipeContainer.setSwipeOffsetAnimationDisallowed(false);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatAttachAlertBotWebViewLayout.this.lambda$onShown$11();
            }
        });
    }

    @Override
    public void requestLayout() {
        if (this.ignoreLayout) {
            return;
        }
        super.requestLayout();
    }

    public void requestWebView(final int i, long j, long j2, boolean z, int i2, String str) {
        TLRPC.ChatFull chatFull;
        TLRPC.Peer peer;
        this.currentAccount = i;
        this.peerId = j;
        this.botId = j2;
        this.silent = z;
        this.replyToMsgId = i2;
        this.startCommand = str;
        if (this.addToHomeScreenItem != null) {
            if (MediaDataController.getInstance(i).canCreateAttachedMenuBotShortcut(j2)) {
                this.addToHomeScreenItem.setVisibility(0);
            } else {
                this.addToHomeScreenItem.setVisibility(8);
            }
        }
        this.webViewContainer.setBotUser(MessagesController.getInstance(i).getUser(Long.valueOf(j2)));
        this.webViewContainer.loadFlickerAndSettingsItem(i, j2, this.settingsItem);
        TLRPC.TL_messages_requestWebView tL_messages_requestWebView = new TLRPC.TL_messages_requestWebView();
        tL_messages_requestWebView.peer = MessagesController.getInstance(i).getInputPeer(j);
        tL_messages_requestWebView.bot = MessagesController.getInstance(i).getInputUser(j2);
        tL_messages_requestWebView.silent = z;
        tL_messages_requestWebView.platform = "android";
        if (j < 0 && (chatFull = MessagesController.getInstance(i).getChatFull(-j)) != null && (peer = chatFull.default_send_as) != null) {
            tL_messages_requestWebView.send_as = MessagesController.getInstance(i).getInputPeer(peer);
            tL_messages_requestWebView.flags |= 8192;
        }
        if (str != null) {
            tL_messages_requestWebView.start_param = str;
            tL_messages_requestWebView.flags |= 8;
        }
        if (i2 != 0) {
            tL_messages_requestWebView.reply_to = SendMessagesHelper.getInstance(i).createReplyInput(i2);
            tL_messages_requestWebView.flags |= 1;
        }
        JSONObject makeThemeParams = BotWebViewSheet.makeThemeParams(this.resourcesProvider);
        if (makeThemeParams != null) {
            TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
            tL_messages_requestWebView.theme_params = tL_dataJSON;
            tL_dataJSON.data = makeThemeParams.toString();
            tL_messages_requestWebView.flags |= 4;
        }
        ConnectionsManager.getInstance(i).sendRequest(tL_messages_requestWebView, new RequestDelegate() {
            @Override
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ChatAttachAlertBotWebViewLayout.this.lambda$requestWebView$13(i, tLObject, tL_error);
            }
        });
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.webViewResultSent);
    }

    @Override
    public void scrollToTop() {
        WebViewSwipeContainer webViewSwipeContainer = this.swipeContainer;
        webViewSwipeContainer.stickTo((-webViewSwipeContainer.getOffsetY()) + this.swipeContainer.getTopActionBarOffsetY());
    }

    public void setAllowSwipes(boolean z) {
        this.swipeContainer.setAllowSwipes(z);
    }

    public void setCustomActionBarBackground(int i) {
        this.hasCustomActionBarBackground = true;
        this.customActionBarBackground = i;
    }

    public void setCustomBackground(int i) {
        this.customBackground = i;
        this.hasCustomBackground = true;
    }

    public void setDelegate(BotWebViewContainer.Delegate delegate) {
        this.webViewContainer.setDelegate(delegate);
    }

    public void setMeasureOffsetY(int i) {
        this.measureOffsetY = i;
        this.swipeContainer.requestLayout();
    }

    public void setNeedCloseConfirmation(boolean z) {
        this.needCloseConfirmation = z;
    }

    @Override
    public void setTranslationY(float f) {
        super.setTranslationY(f);
        this.parentAlert.getSheetContainer().invalidate();
    }

    @Override
    public boolean shouldHideBottomButtons() {
        return false;
    }

    public void showJustAddedBulletin() {
        TLRPC.TL_attachMenuBot tL_attachMenuBot;
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.botId));
        Iterator<TLRPC.TL_attachMenuBot> it = MediaDataController.getInstance(this.currentAccount).getAttachMenuBots().bots.iterator();
        while (true) {
            if (!it.hasNext()) {
                tL_attachMenuBot = null;
                break;
            } else {
                tL_attachMenuBot = it.next();
                if (tL_attachMenuBot.bot_id == this.botId) {
                    break;
                }
            }
        }
        if (tL_attachMenuBot == null) {
            return;
        }
        boolean z = tL_attachMenuBot.show_in_side_menu;
        final String formatString = (z && tL_attachMenuBot.show_in_attach_menu) ? LocaleController.formatString("BotAttachMenuShortcatAddedAttachAndSide", R.string.BotAttachMenuShortcatAddedAttachAndSide, user.first_name) : z ? LocaleController.formatString("BotAttachMenuShortcatAddedSide", R.string.BotAttachMenuShortcatAddedSide, user.first_name) : LocaleController.formatString("BotAttachMenuShortcatAddedAttach", R.string.BotAttachMenuShortcatAddedAttach, user.first_name);
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                ChatAttachAlertBotWebViewLayout.this.lambda$showJustAddedBulletin$14(formatString);
            }
        }, 200L);
    }
}
